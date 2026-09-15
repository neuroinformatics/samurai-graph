package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Headful construction tests for the remaining application dialogs. */
class SGApplicationDialogFamilyTest {

  private SGUpgradeDialog upgradeDialog;

  private SGScrollPaneDialog scrollPaneDialog;

  private SGDataReloadResultPanel reloadResultPanel;

  private SGPropertyFileChooserWizardDialog fileChooserDialog;

  private SGPluginInfoPanel pluginInfoPanel;

  @AfterEach
  void disposeDialogs() {
    if (this.upgradeDialog != null) {
      this.upgradeDialog.dispose();
      this.upgradeDialog = null;
    }
    if (this.scrollPaneDialog != null) {
      this.scrollPaneDialog.dispose();
      this.scrollPaneDialog = null;
    }
    if (this.fileChooserDialog != null) {
      this.fileChooserDialog.dispose();
      this.fileChooserDialog = null;
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
  void dialogsWithoutArgumentsCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            // SGDataReloadResultPanel has a no-arg constructor
            SGDataReloadResultPanel reloadPanel = new SGDataReloadResultPanel();
            assertNotNull(reloadPanel);

            // SGPluginInfoPanel has a no-arg constructor
            SGPluginInfoPanel panel = new SGPluginInfoPanel();
            assertNotNull(panel);
          }
        });
  }

  @Test
  void upgradeDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGApplicationDialogFamilyTest.this.upgradeDialog =
                new SGUpgradeDialog((java.awt.Frame) null, false);
            assertNotNull(SGApplicationDialogFamilyTest.this.upgradeDialog);
            SGApplicationDialogFamilyTest.this.upgradeDialog.dispose();
          }
        });
  }

  @Test
  void scrollPaneDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGScrollPaneDialog dialog = new SGScrollPaneDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void propertyFileChooserWizardDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGPropertyFileChooserWizardDialog dialog =
                new SGPropertyFileChooserWizardDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }
}
