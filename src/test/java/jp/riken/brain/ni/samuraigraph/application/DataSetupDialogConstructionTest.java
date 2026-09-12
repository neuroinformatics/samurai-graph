package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Frame;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

/** Unit tests for constructing the data setup dialog families. */
class DataSetupDialogConstructionTest {

  private void runOnEdt(final Runnable runnable) {
    try {
      SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void propertyFileDialogsCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          public void run() {
            SGPropertyFileMDArrayDataDialog md =
                new SGPropertyFileMDArrayDataDialog((Frame) null, false);
            assertNotNull(md);
            md.dispose();

            SGPropertyFileNetCDFDataDialog nc =
                new SGPropertyFileNetCDFDataDialog((Frame) null, false);
            assertNotNull(nc);
            nc.dispose();

            SGPropertyFileSDArrayDataDialog sd =
                new SGPropertyFileSDArrayDataDialog((Frame) null, false);
            assertNotNull(sd);
            sd.dispose();
          }
        });
  }

  @Test
  void dataSetupWizardDialogsCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          public void run() {
            SGMDArrayDataSetupWizardDialog md =
                new SGMDArrayDataSetupWizardDialog((Frame) null, false);
            assertNotNull(md);
            md.dispose();

            SGNetCDFDataSetupWizardDialog nc =
                new SGNetCDFDataSetupWizardDialog((Frame) null, false);
            assertNotNull(nc);
            nc.dispose();

            SGSDArrayDataSetupWizardDialog sd =
                new SGSDArrayDataSetupWizardDialog((Frame) null, false);
            assertNotNull(sd);
            sd.dispose();
          }
        });
  }
}
