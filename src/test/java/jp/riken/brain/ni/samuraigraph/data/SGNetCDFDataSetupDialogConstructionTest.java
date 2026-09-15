package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Headful construction tests for the NetCDF data setup dialog. */
class SGNetCDFDataSetupDialogConstructionTest {

  private void runOnEdt(final Runnable runnable) {
    try {
      javax.swing.SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void netCDFDataSetupDialogCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGNetCDFDataSetupDialog dialog =
                new SGNetCDFDataSetupDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }
}
