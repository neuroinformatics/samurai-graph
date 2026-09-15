package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

/** Headful construction tests for the remaining application wizard dialogs. */
class SGApplicationWizardDialogFamilyTest {

  private void runOnEdt(final Runnable runnable) {
    try {
      SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void fileTypeWizardDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGFileTypeSelectionWizardDialog dialog =
                new SGFileTypeSelectionWizardDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void plotTypeWizardDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGPlotTypeSelectionWizardDialog dialog =
                new SGPlotTypeSelectionWizardDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void proxySettingDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGProxySettingDialog dialog = new SGProxySettingDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void figureIDWizardDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGFigureIDSelectionWizardDialog dialog =
                new SGFigureIDSelectionWizardDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void dataTypeWizardDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGDataTypeWizardDialog dialog =
                new SGDataTypeWizardDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void commandDataFileChooserWizardDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGCommandDataFileChooserWizardDialog dialog =
                new SGCommandDataFileChooserWizardDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void singleDataFileChooserWizardDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGSingleDataFileChooserWizardDialog dialog =
                new SGSingleDataFileChooserWizardDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }
}
