package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.swing.SwingUtilities;
import jp.riken.brain.ni.samuraigraph.data.SGDataViewerDialog;
import org.junit.jupiter.api.Test;

/** Unit tests for constructing the preview and data viewer dialogs. */
class SGDialogConstructionTest {

  private void runOnEdt(final Runnable runnable) {
    try {
      SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void previewDialogCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGPreviewDialog dialog = new SGPreviewDialog((java.awt.Frame) null, "Preview", false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void dataViewerDialogCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          public void run() {
            SGDataViewerDialog dialog = new SGDataViewerDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }
}
