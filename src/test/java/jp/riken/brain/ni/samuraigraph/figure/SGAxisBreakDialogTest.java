package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Headful construction tests for the axis break dialog. */
class SGAxisBreakDialogTest {

  private void runOnEdt(final Runnable runnable) {
    try {
      javax.swing.SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void axisBreakDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGAxisBreakDialog dialog = new SGAxisBreakDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }
}
