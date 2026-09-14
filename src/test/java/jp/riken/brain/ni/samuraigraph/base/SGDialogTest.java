package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Dialog;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDialog} virtual bounds handling. */
class SGDialogTest {

  private SGDialog dialog;

  @AfterEach
  void disposeDialog() {
    if (this.dialog != null) {
      this.dialog.dispose();
    }
  }

  private static class TestDialog extends SGDialog {
    private static final long serialVersionUID = 1L;

    TestDialog() {
      super((Dialog) null, false);
    }

    @Override
    protected void onEscKeyTyped() {}
  }

  private static Rectangle getScreenVirtualBounds() {
    Rectangle bounds = null;
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    for (GraphicsDevice device : env.getScreenDevices()) {
      for (GraphicsConfiguration config : device.getConfigurations()) {
        Rectangle boundsOfConfig = config.getBounds();
        bounds = (bounds == null) ? boundsOfConfig : bounds.union(boundsOfConfig);
      }
    }
    return bounds == null ? new Rectangle() : bounds;
  }

  @Test
  void virtualBoundsSpansScreenDevices() {
    this.dialog = new TestDialog();
    Rectangle expected = getScreenVirtualBounds();
    Rectangle actual = this.dialog.getVirtualBounds();
    assertEquals(expected, actual);
    assertTrue(actual.width > 0);
    assertTrue(actual.height > 0);
  }

  @Test
  void locationIsClampedToVirtualBoundsOnShow() {
    this.dialog = new TestDialog();
    this.dialog.setLocation(-100, -100);
    this.dialog.setVisible(true);
    assertTrue(this.dialog.getX() >= 0);
    assertTrue(this.dialog.getY() >= 0);
    this.dialog.setVisible(false);
  }
}
