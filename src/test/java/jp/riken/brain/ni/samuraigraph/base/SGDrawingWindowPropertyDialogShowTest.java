package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JTextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the property dialog show path through the drawing window utility using a lightweight
 * dialog subclass and a stub observer.
 *
 * <p>These tests need a display.
 */
class SGDrawingWindowPropertyDialogShowTest {

  private SGDrawingWindow wnd;

  /** A dialog subclass that does not show a window on its own. */
  private static class TestPropertyDialog extends SGPropertyDialog {
    private static final long serialVersionUID = 1L;

    boolean dialogPropertySet = false;

    TestPropertyDialog() {
      super();
    }

    @Override
    public List<JTextField> getTextFieldComponentsList() {
      return new ArrayList<>();
    }

    @Override
    public List<SGTextField> getAxisNumberTextFieldList() {
      return new ArrayList<>();
    }

    @Override
    public List<SGSpinner> getSpinnerList() {
      return new ArrayList<>();
    }

    @Override
    protected javax.swing.JButton getOKButton() {
      return new javax.swing.JButton();
    }

    @Override
    protected JButton getCancelButton() {
      return new JButton();
    }

    @Override
    protected JButton getPreviewButton() {
      return null;
    }

    @Override
    public boolean setPropertiesToObserver(SGIPropertyDialogObserver obs) {
      return true;
    }

    @Override
    public boolean setDialogProperty() {
      this.dialogPropertySet = true;
      return true;
    }
  }

  /** A stub observer which counts prepare calls. */
  private static class TestObserver implements SGIPropertyDialogObserver {
    int prepareCount = 0;

    @Override
    public boolean commit() {
      return true;
    }

    @Override
    public boolean cancel() {
      return true;
    }

    @Override
    public boolean preview() {
      return true;
    }

    @Override
    public boolean prepare() {
      this.prepareCount++;
      return true;
    }

    @Override
    public SGPropertyDialog getPropertyDialog() {
      return null;
    }
  }

  @AfterEach
  void disposeWindow() {
    if (this.wnd != null) {
      this.wnd.dispose();
      this.wnd = null;
    }
  }

  private void runOnEdt(final Runnable runnable) {
    try {
      javax.swing.SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void showPathAddsTheObserverAndCoversDialogLifecycle() {
    this.wnd = new SGDrawingWindow();
    this.wnd.init();
    final SGDrawingWindow window = this.wnd;
    TestPropertyDialog dialog = new TestPropertyDialog();
    TestObserver observer = new TestObserver();

    ArrayList<SGIPropertyDialogObserver> observers = new ArrayList<>();
    observers.add(observer);

    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGDrawingWindowPropertyDialogUtility.showPropertyDialog(window, dialog, observers);
            dialog.setVisible(false);
            dialog.dispose();
          }
        });

    assertEquals(1, observer.prepareCount);
    assertTrue(dialog.dialogPropertySet);
    // the observers are cleared after the show path
    assertTrue(dialog.removeAllPropertyDialogObserver());
  }
}
