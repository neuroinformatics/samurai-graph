package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.swing.SwingUtilities;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Headful lifecycle tests for the animation dialog with a real window.
 *
 * <p>These tests need a display because the dialog is a Swing window.
 */
class SGDataAnimationDialogLifecycleTest {

  private SGDrawingWindow wnd;

  private SGDataAnimationDialog dialog;

  @AfterEach
  void disposeAll() {
    if (this.dialog != null) {
      this.dialog.dispose();
      this.dialog = null;
    }
    if (this.wnd != null) {
      this.wnd.dispose();
      this.wnd = null;
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
  void dialogCanBeConstructedOnAWindowAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGDrawingWindow window = new SGDrawingWindow();
            window.init();
            SGDataAnimationDialogLifecycleTest.this.wnd = window;
            SGDataAnimationDialog dialog = new SGDataAnimationDialog(window, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }
}
