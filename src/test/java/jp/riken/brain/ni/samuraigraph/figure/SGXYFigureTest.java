package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Headful integration tests for the XY figure family. */
class SGXYFigureTest {

  private SGDrawingWindow wnd;

  private SGXYFigure figure;

  @AfterEach
  void disposeAll() {
    if (this.figure != null) {
      this.figure.dispose();
      this.figure = null;
    }
    if (this.wnd != null) {
      this.wnd.dispose();
      this.wnd = null;
    }
  }

  private void runOnEdt(final Runnable runnable) {
    try {
      javax.swing.SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void xyFigureCanBeCreatedWithARealWindow() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGXYFigureTest.this.wnd = new SGDrawingWindow();
            wnd.init();
            SGXYFigureTest.this.figure = new SGXYFigure(wnd);
            assertNotNull(figure);
          }
        });
  }
}
