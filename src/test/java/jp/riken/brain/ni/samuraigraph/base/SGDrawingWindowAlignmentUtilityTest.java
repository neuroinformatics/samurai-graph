package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Headful tests of the drawing window alignment utility.
 *
 * <p>These tests need a display because the drawing window is a JFrame.
 */
class SGDrawingWindowAlignmentUtilityTest {

  private SGDrawingWindow wnd;

  @AfterEach
  void disposeWindow() {
    if (this.wnd != null) {
      this.wnd.dispose();
      this.wnd = null;
    }
  }

  @Test
  void orderedFigureArrayForEmptyWindowReturnsEmpty2DArray() {
    SGDrawingWindow window = new SGDrawingWindow();
    window.init();
    assertEquals(0, SGDrawingWindowAlignmentUtility.getOrderedFigureArray(window).length);
  }
}
