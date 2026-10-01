package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.Dialog;
import java.awt.Frame;
import org.junit.jupiter.api.Test;

/**
 * Headful construction tests for the MDArray data setup dialog and its delegation to the embedded
 * setup panel.
 */
class SGMDArrayDataSetupDialogConstructionTest {

  private void runOnEdt(final Runnable runnable) {
    try {
      javax.swing.SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void theDialogCanBeConstructedFromAFrame() {
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupDialog dialog = new SGMDArrayDataSetupDialog((Frame) null, false);
          assertNotNull(dialog);
          // the OK and cancel buttons and the table holder are wired up
          assertNotNull(dialog.getOKButton());
          assertNotNull(dialog.getCancelButton());
          assertSame(dialog.getTableHolder(), dialog.getDataSetupPanel());
          // the public delegation methods reach the embedded panel
          assertNotNull(dialog.getPickUpDimensionIndexMap());
          assertNull(dialog.getPickUpDatasetName());
          assertFalse(dialog.isStrideAvailable());
          dialog.dispose();
        });
  }

  @Test
  void theDialogCanBeConstructedFromADialog() {
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupDialog dialog =
              new SGMDArrayDataSetupDialog(new Dialog((Frame) null, "parent"), false);
          assertNotNull(dialog);
          assertNotNull(dialog.getOKButton());
          assertNotNull(dialog.getCancelButton());
          dialog.dispose();
        });
  }
}
