package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Headful lifecycle tests for the data export wizard dialog.
 *
 * <p>These tests need a display because the dialog is a Swing dialog.
 */
class SGDataExportWizardDialogTest {

  private SGDataExportWizardDialog dialog;

  @AfterEach
  void disposeDialog() {
    if (this.dialog != null) {
      this.dialog.dispose();
      this.dialog = null;
    }
  }

  private void runOnEdt(final Runnable runnable) {
    try {
      SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void dialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGDataExportWizardDialog dialog =
                new SGDataExportWizardDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void optionFlagsRoundTrip() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGDataExportWizardDialog dialog =
                new SGDataExportWizardDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.setStrideSelected(true);
            assertTrue(dialog.isStrideSelected());
            dialog.setStrideSelected(false);
            assertFalse(dialog.isStrideSelected());
            dialog.setEditedDataValueSelected(true);
            assertTrue(dialog.isEditedDataValueSelected());
            dialog.setEditedDataValueComponentsEnabled(true);
            dialog.setEditedDataValueComponentsVisible(true);
            dialog.setShiftSelected(true);
            dialog.dispose();
          }
        });
  }
}
