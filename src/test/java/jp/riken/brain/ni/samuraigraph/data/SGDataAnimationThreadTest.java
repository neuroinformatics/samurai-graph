package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jp.riken.brain.ni.samuraigraph.base.SGIAnimation;
import org.junit.jupiter.api.Test;

/** Unit tests of the data animation thread. */
class SGDataAnimationThreadTest {

  private static SGIDataAnimation mockAnimation(final int frameNumber) {
    final SGIDataAnimation animation = mock(SGIDataAnimation.class);
    when(animation.getFrameNumber()).thenReturn(frameNumber);
    when(animation.getCurrentFrameIndex()).thenReturn(0);
    when(animation.getFrameRate()).thenReturn(1.0);
    when(animation.isLoopPlaybackAvailable()).thenReturn(false);
    when(animation.getAnimationArraySection()).thenReturn(null);
    return animation;
  }

  @Test
  void theConstructorRejectsNullAnimations() {
    assertThrows(
        IllegalArgumentException.class, () -> new SGDataAnimationThread((SGIAnimation[]) null));
  }

  @Test
  void theConstructorRejectsEmptyAnimations() {
    assertThrows(
        IllegalArgumentException.class, () -> new SGDataAnimationThread(new SGIDataAnimation[0]));
  }

  @Test
  void saveChangesSavesEveryAnimation() {
    final SGIDataAnimation first = mockAnimation(5);
    final SGIDataAnimation second = mockAnimation(5);
    final SGDataAnimationThread thread =
        new SGDataAnimationThread(new SGIDataAnimation[] {first, second});
    thread.saveChanges();
    verify(first).saveChanges();
    verify(second).saveChanges();
  }

  @Test
  void cancelChangesCancelsEveryAnimation() {
    final SGIDataAnimation first = mockAnimation(5);
    final SGIDataAnimation second = mockAnimation(5);
    final SGDataAnimationThread thread =
        new SGDataAnimationThread(new SGIDataAnimation[] {first, second});
    thread.cancelChanges();
    verify(first).cancelChanges();
    verify(second).cancelChanges();
  }

  @Test
  void theCopyConstructorSharesTheAnimations() {
    final SGIDataAnimation animation = mockAnimation(5);
    final SGDataAnimationThread thread =
        new SGDataAnimationThread(new SGIDataAnimation[] {animation});
    final SGDataAnimationThread copy = new SGDataAnimationThread(thread);
    copy.saveChanges();
    verify(animation).saveChanges();
  }
}
