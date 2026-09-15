package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Headful construction tests for the base input dialogs. */
class SGInputHeadfulTest {

  private SGDateInputDialog dateDialog;

  private SGPeriodInputDialog periodDialog;

  @AfterEach
  void disposeDialogs() {
    if (this.dateDialog != null) {
      this.dateDialog.dispose();
      this.dateDialog = null;
    }
    if (this.periodDialog != null) {
      this.periodDialog.dispose();
      this.periodDialog = null;
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
  void dateDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGDateInputDialog dialog = new SGDateInputDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void periodDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGPeriodInputDialog dialog = new SGPeriodInputDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }
}
