package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

/**
 * Headful construction tests for the axis selection panels.
 *
 * <p>These tests need a display because the panels are Swing components.
 */
class SGAxisSelectionPanelTest {

  private void runOnEdt(final Runnable runnable) {
    try {
      SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void singleAxisSelectionPanelCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGSingleAxisSelectionPanel panel = new SGSingleAxisSelectionPanel();
            assertNotNull(panel);
          }
        });
  }

  @Test
  void twoAxesSelectionPanelCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGTwoAxesSelectionPanel panel = new SGTwoAxesSelectionPanel();
            assertNotNull(panel);
          }
        });
  }

  @Test
  void patternPaintDialogCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGPatternPaintDialog dialog =
                new SGPatternPaintDialog((javax.swing.JDialog) null, "Paint", false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }
}
