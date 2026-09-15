package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Headful construction tests for the remaining application dialogs. */
class SGApplicationDialogBatch2Test {

  private SGAboutDialog aboutDialog;

  private SGPluginOutputWizardDialog pluginOutputDialog;

  @AfterEach
  void disposeDialogs() {
    if (this.aboutDialog != null) {
      this.aboutDialog.dispose();
      this.aboutDialog = null;
    }
    if (this.pluginOutputDialog != null) {
      this.pluginOutputDialog.dispose();
      this.pluginOutputDialog = null;
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
  void aboutDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGApplicationDialogBatch2Test.this.aboutDialog =
                new SGAboutDialog((java.awt.Frame) null, false, "2.2.0");
            assertNotNull(aboutDialog);
            aboutDialog.dispose();
          }
        });
  }

  @Test
  void pluginOutputWizardDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGApplicationDialogBatch2Test.this.pluginOutputDialog =
                new SGPluginOutputWizardDialog((java.awt.Frame) null, false);
            assertNotNull(pluginOutputDialog);
            pluginOutputDialog.dispose();
          }
        });
  }
}
