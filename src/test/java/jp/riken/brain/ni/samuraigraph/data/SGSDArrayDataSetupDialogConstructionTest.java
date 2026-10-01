package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.Dialog;
import java.awt.Frame;
import org.junit.jupiter.api.Test;

/**
 * Headful construction tests for the SDArray data setup dialog and its delegation to the embedded
 * setup panel.
 */
class SGSDArrayDataSetupDialogConstructionTest {

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
          SGSDArrayDataSetupDialog dialog = new SGSDArrayDataSetupDialog((Frame) null, false);
          assertNotNull(dialog);
          // the OK and cancel buttons and the table holder are wired up
          assertNotNull(dialog.getOKButton());
          assertNotNull(dialog.getCancelButton());
          assertSame(dialog.getTableHolder(), dialog.getDataSetupPanel());
          dialog.dispose();
        });
  }

  @Test
  void theDialogCanBeConstructedFromADialog() {
    this.runOnEdt(
        () -> {
          SGSDArrayDataSetupDialog dialog =
              new SGSDArrayDataSetupDialog(new Dialog((Frame) null, "parent"), false);
          assertNotNull(dialog);
          assertNotNull(dialog.getOKButton());
          assertNotNull(dialog.getCancelButton());
          dialog.dispose();
        });
  }
}
