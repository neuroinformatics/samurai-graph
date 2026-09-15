package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Headful construction tests for the remaining property dialogs. */
class SGFigureDialogFamilyConstructionTest {

  private void runOnEdt(final Runnable runnable) {
    try {
      javax.swing.SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void dialogsWithFrameOwnerCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGLegendDialog legendDialog = new SGLegendDialog((java.awt.Frame) null, false);
            assertNotNull(legendDialog);
            legendDialog.dispose();

            SGArrowDialog arrowDialog = new SGArrowDialog((java.awt.Frame) null, false);
            assertNotNull(arrowDialog);
            arrowDialog.dispose();

            SGAxisDialog axisDialog = new SGAxisDialog((java.awt.Frame) null, false);
            assertNotNull(axisDialog);
            axisDialog.dispose();
          }
        });
  }

  @Test
  void symbolDialogsCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGSignificantDifferenceDialog dialog =
                new SGSignificantDifferenceDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void timingLineDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGTimingLineDialog dialog = new SGTimingLineDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void shapeDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGRectangularShapeDialog dialog =
                new SGRectangularShapeDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void colorBarDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGColorBarDialog dialog = new SGColorBarDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void axisScaleDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGAxisScaleDialog dialog = new SGAxisScaleDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }

  @Test
  void stringElementDialogCanBeConstructedAndDisposed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGStringElementDialog dialog = new SGStringElementDialog((java.awt.Frame) null, false);
            assertNotNull(dialog);
            dialog.dispose();
          }
        });
  }
}
