package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Headful construction tests for the mdarray setup panels. */
class SGMDArrayPanelConstructionTest {

  private void runOnEdt(final Runnable runnable) {
    try {
      javax.swing.SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void dimensionPanelCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGMDArrayDimensionPanel panel = new SGMDArrayDimensionPanel();
            assertNotNull(panel);
          }
        });
  }

  @Test
  void dataSetupPanelCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
            assertNotNull(panel);
          }
        });
  }
}
