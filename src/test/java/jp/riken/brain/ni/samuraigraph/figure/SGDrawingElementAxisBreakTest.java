package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.geom.Rectangle2D;
import org.junit.jupiter.api.Test;

/** Regression tests for shape creation in {@link SGDrawingElementAxisBreak}. */
class SGDrawingElementAxisBreakTest {

  private SGDrawingElementAxisBreak createElement() {
    return new SGDrawingElementAxisBreak(1.0f, "cm", 0.5f, "cm", 0.5f, 30.0f, true) {
      @Override
      public float getMagnification() {
        return 1.0f;
      }
    };
  }

  @Test
  void boundsAreNotEmptyAfterLocationIsSet() {
    SGDrawingElementAxisBreak el = this.createElement();
    Rectangle2D before = el.getElementBounds();
    assertTrue(before.getWidth() == 0.0, "empty before setLocation");
    assertTrue(el.setLocation(100.0f, 100.0f));
    Rectangle2D after = el.getElementBounds();
    assertTrue(after.getWidth() > 0.0, "non-empty after setLocation");
    assertTrue(el.contains((int) after.getCenterX(), (int) after.getCenterY()));
  }

  @Test
  void boundsAreNotEmptyAfterLocationTupleIsSet() {
    SGDrawingElementAxisBreak el = this.createElement();
    assertTrue(el.setLocation(new jp.riken.brain.ni.samuraigraph.base.SGTuple2f(50.0f, 50.0f)));
    Rectangle2D after = el.getElementBounds();
    assertTrue(after.getWidth() > 0.0, "non-empty after tuple setLocation");
  }
}
