package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Headless round-trip tests for the significant difference drawing element. */
class SGDrawingElementSignificantDifferenceRoundTripTest {

  private static class TestSigDiff extends SGDrawingElementSignificantDifference {
    @Override
    public float getMagnification() {
      return 1.0f;
    }
  }

  @Test
  void elementCanBeCreatedAndDisposed() {
    TestSigDiff el = new TestSigDiff();
    assertNotNull(el);
    assertFalse(el.isDisposed());
    el.dispose();
  }
}
