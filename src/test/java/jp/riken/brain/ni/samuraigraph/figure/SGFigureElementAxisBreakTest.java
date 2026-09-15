package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Headful integration tests for the axis break element family. */
class SGFigureElementAxisBreakTest {

  private SGFigureElementAxisBreak axisBreak;

  @AfterEach
  void disposeElement() {
    if (this.axisBreak != null) {
      this.axisBreak.dispose();
      this.axisBreak = null;
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
  void axisBreakElementCanBeCreatedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGFigureElementAxisBreak element = new SGFigureElementAxisBreak();
            assertNotNull(element);
            element.dispose();
          }
        });
  }

  @Test
  void symbolOutsideTheGraphRectIsRejected() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            axisBreak = new SGFigureElementAxisBreak();
            axisBreak.setAxisElement(new SGFigureElementAxis());
            assertTrue(axisBreak.setGraphRect(10.0f, 10.0f, 100.0f, 100.0f));
            assertFalse(axisBreak.addAxisBreakSymbol(10_000.0f, 10_000.0f));
          }
        });
  }
}
