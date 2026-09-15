package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Headless tests for {@link SGAnimationThread} lifecycle and helpers. */
class SGAnimationThreadTest {

  /** A stub animation with configurable properties. */
  private static class TestAnimation implements SGIAnimation {
    private final int frameCount;
    private double frameRate;
    private final boolean loopAvailable;

    TestAnimation(int frameCount, double frameRate, boolean loopAvailable) {
      this.frameCount = frameCount;
      this.frameRate = frameRate;
      this.loopAvailable = loopAvailable;
    }

    @Override
    public int getFrameNumber() {
      return this.frameCount;
    }

    @Override
    public int getCurrentFrameIndex() {
      return 0;
    }

    @Override
    public void setCurrentFrameIndex(int index) {
      // no-op
    }

    @Override
    public SGIntegerSeriesSet getAnimationArraySection() {
      return null;
    }

    @Override
    public void setFrameIndices(SGIntegerSeriesSet arraySection) {
      // no-op
    }

    @Override
    public boolean isLoopPlaybackAvailable() {
      return this.loopAvailable;
    }

    @Override
    public void setLoopPlaybackAvailable(final boolean b) {
      // no-op
    }

    @Override
    public double getFrameRate() {
      return this.frameRate;
    }

    @Override
    public void setFrameRate(final double rate) {
      this.frameRate = rate;
    }
  }

  @Test
  void threadIsCreatedFromAnimations() {
    SGIAnimation[] animations = {new TestAnimation(5, 10.0, true)};
    SGAnimationThread thread = new SGAnimationThread(animations);
    assertEquals(5, thread.getFrameNumber());
    assertFalse(thread.isPlaying());
    assertFalse(thread.isDisposed());
    assertNotNull(thread.getAnimations());
  }

  @Test
  void constructorRejectsEmpty() {
    assertThrows(
        IllegalArgumentException.class, () -> new SGAnimationThread(new SGIAnimation[] {}));
  }

  @Test
  void constructorRejectsNull() {
    final SGIAnimation[] animations = null;
    assertThrows(
        IllegalArgumentException.class, () -> new SGAnimationThread((SGIAnimation[]) animations));
  }

  @Test
  void constructorRejectsMismatchedFrameNumbers() {
    SGIAnimation[] animations = {
      new TestAnimation(3, 10.0, true), new TestAnimation(5, 10.0, true)
    };
    assertThrows(IllegalArgumentException.class, () -> new SGAnimationThread(animations));
  }

  @Test
  void frameNumberAndRateReflectSingleAnimation() {
    SGIAnimation[] animations = {new TestAnimation(4, 8.0, true)};
    SGAnimationThread thread = new SGAnimationThread(animations);
    assertEquals(4, thread.getFrameNumber());
    assertEquals(8.0, thread.getFrameRate(), 1.0e-6);
    // 8 fps means 125 ms interval
    assertEquals(125L, thread.getTimeInterval());
  }

  @Test
  void loopPlaybackFlagIsFromAnimation() {
    SGIAnimation[] animations = {new TestAnimation(4, 10.0, true)};
    SGAnimationThread thread = new SGAnimationThread(animations);
    assertTrue(thread.isLoopPlaybackEnabled());
    thread.setLoopPlaybackEnabled(false);
    assertFalse(thread.isLoopPlaybackEnabled());
  }

  @Test
  void playingDirectionDefaults() {
    SGIAnimation[] animations = {new TestAnimation(4, 10.0, true)};
    SGAnimationThread thread = new SGAnimationThread(animations);
    assertTrue(thread.isPlayingForward());
    thread.setPlayingForward(false);
    assertFalse(thread.isPlayingForward());
  }

  @Test
  void disposalSetsDisposed() {
    SGIAnimation[] animations = {new TestAnimation(3, 10.0, true)};
    SGAnimationThread thread = new SGAnimationThread(animations);
    thread.dispose();
    assertTrue(thread.isDisposed());
  }

  @Test
  void copyConstructorCarriesFrameNumber() {
    SGIAnimation[] animations = {new TestAnimation(7, 10.0, true)};
    SGAnimationThread original = new SGAnimationThread(animations);
    SGAnimationThread copy = new SGAnimationThread((SGAnimationThread) original);
    assertEquals(original.getFrameNumber(), copy.getFrameNumber());
  }

  @Test
  void staticTimeIntervalComputedFromFrameRate() {
    assertEquals(100L, SGAnimationThread.getTimeInterval(10.0), 0.0);
    assertEquals(200L, SGAnimationThread.getTimeInterval(5.0), 0.0);
    assertEquals(250L, SGAnimationThread.getTimeInterval(4.0), 0.0);
  }

  @Test
  void staticGetValidFrameIndexForwardLoop() {
    int[] fullFull = {0, 1, 2, 3, 4};
    assertEquals(4, SGAnimationThread.getValidFrameIndex(4, 5, fullFull, true, true), 0);
  }

  @Test
  void staticGetValidFrameIndexForwardNoLoop() {
    int[] arr = {0, 1, 2};
    assertEquals(1, SGAnimationThread.getValidFrameIndex(1, 3, arr, true, false), 0.0);
  }

  @Test
  void staticGetValidFrameIndexBackwardLoop() {
    int[] idx = {0, 1, 2};
    // a frame index of 1 is valid; a frame index of 0 is valid;
    // a frame index of -1 wraps to the last index
    assertEquals(1, SGAnimationThread.getValidFrameIndex(1, 3, idx, false, true), 0.0);
    assertEquals(0, SGAnimationThread.getValidFrameIndex(0, 3, idx, false, true), 0.0);
    assertEquals(2, SGAnimationThread.getValidFrameIndex(-1, 3, idx, false, true), 0.0);
  }
}
