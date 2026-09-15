package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Headful construction tests for the archive creator and the remaining wizard dialogs. */
class SGArchiveWizardTest {

  private void runOnEdt(final Runnable runnable) {
    try {
      javax.swing.SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void archiveFileCreatorDefaultFile() {
    SGArchiveFileCreator creator = new SGArchiveFileCreator();
    assertNotNull(creator.getCurrentFile());
    assertEquals("dataset.sga", creator.getCurrentFile().getName());
  }

  @Test
  void pluginOutputWizardDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGPluginOutputWizardDialog dialog =
                new SGPluginOutputWizardDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void propertyDataFileChooserWizardDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGPropertyDataFileChooserWizardDialog dialog =
                new SGPropertyDataFileChooserWizardDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void archiveFileCreatorSetName() {
    SGArchiveFileCreator creator = new SGArchiveFileCreator();
    creator.setCurrentFileName("test.sga");
    assertNotNull(creator.getCurrentFile());
    assertEquals("test.sga", creator.getCurrentFile().getName());
  }
}
