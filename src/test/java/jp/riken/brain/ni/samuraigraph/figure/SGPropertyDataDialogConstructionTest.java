package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Headful construction tests for the data property dialogs. */
class SGPropertyDataDialogConstructionTest {

  private void runOnEdt(final Runnable runnable) {
    try {
      javax.swing.SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void sxyDataDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGPropertyDialogSXYData dialog =
                new SGPropertyDialogSXYData((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void vxyDataDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGPropertyDialogVXYData dialog =
                new SGPropertyDialogVXYData((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void sxyzDataDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGPropertyDialogSXYZData dialog =
                new SGPropertyDialogSXYZData((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }
}
