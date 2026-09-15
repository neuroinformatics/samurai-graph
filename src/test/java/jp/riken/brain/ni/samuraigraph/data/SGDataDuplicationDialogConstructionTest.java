package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Headful smoke tests for constructing the data duplication dialogs. */
class SGDataDuplicationDialogConstructionTest {

  private void runOnEdt(final Runnable runnable) {
    try {
      javax.swing.SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void sdArrayDuplicationDialogCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGSDArrayDataDuplicationDialog dialog =
                new SGSDArrayDataDuplicationDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void netCDFDialogCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          public void run() {
            SGNetCDFDataDuplicationDialog dialog =
                new SGNetCDFDataDuplicationDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void mdArrayDialogCanBeConstructed() {
    this.runOnEdt(
        () -> {
          SGMDArrayDataDuplicationDialog dialog =
              new SGMDArrayDataDuplicationDialog((java.awt.Frame) null, false);
          assertNotNull(dialog);
          dialog.dispose();
        });
  }
}
