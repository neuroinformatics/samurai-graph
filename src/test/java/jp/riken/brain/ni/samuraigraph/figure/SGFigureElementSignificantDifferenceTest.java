package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Headful integration tests for the significant difference element. */
class SGFigureElementSignificantDifferenceTest {

  private SGFigureElementSignificantDifference element;

  @AfterEach
  void disposeElement() {
    if (this.element != null) {
      this.element.dispose();
      this.element = null;
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
  void elementCanBeCreatedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGFigureElementSignificantDifferenceTest.this.element =
                new SGFigureElementSignificantDifference();
            assertNotNull(element);
          }
        });
  }

  @Test
  void symbolOutsideTheGraphRectIsRejected() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGFigureElementSignificantDifferenceTest.this.element =
                new SGFigureElementSignificantDifference();
            element.setAxisElement(new SGFigureElementAxis());
            assertTrue(element.setGraphRect(10.0f, 10.0f, 100.0f, 100.0f));
            assertFalse(element.addSignificantDifferenceSymbol(10_000.0f, 10_000.0f));
          }
        });
  }
}
