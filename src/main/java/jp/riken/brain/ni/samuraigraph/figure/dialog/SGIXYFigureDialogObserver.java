package jp.riken.brain.ni.samuraigraph.figure.dialog;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGAxisStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisValue;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/** An observer of the dialog for XY-type figures. */
public interface SGIXYFigureDialogObserver extends SGIFigureDialogObserver, SGITwoAxesHolder {

  /**
   * @param b
   * @return
   */
  public boolean setGridVisible(final boolean b);

  /**
   * @param b
   * @return
   */
  public boolean setAutoCalculateRange(final boolean b);

  /**
   * @param value
   * @return
   */
  public boolean setGridStepValueX(final SGAxisStepValue value);

  /**
   * @param value
   * @return
   */
  public boolean setGridStepValueY(final SGAxisStepValue value);

  /**
   * @param value
   * @return
   */
  public boolean setGridBaselineValueX(final SGAxisValue value);

  /**
   * @param value
   * @return
   */
  public boolean setGridBaselineValueY(final SGAxisValue value);

  public boolean setGridLineWidth(final float width, final String unit);

  /**
   * @param type
   * @return
   */
  public boolean setGridLineType(final int type);

  /**
   * @param cl
   * @return
   */
  public boolean setGridLineColor(final Color cl);

  /**
   * @return
   */
  public boolean isGridVisible();

  /**
   * @return
   */
  public boolean isAutoCalculateRange();

  /**
   * @return
   */
  public SGAxisStepValue getGridStepValueX();

  /**
   * @return
   */
  public SGAxisStepValue getGridStepValueY();

  /**
   * @return
   */
  public SGAxisValue getGridBaselineValueX();

  /**
   * @return
   */
  public SGAxisValue getGridBaselineValueY();

  public float getGridLineWidth(final String unit);

  /**
   * @return
   */
  public int getGridLineType();

  /**
   * @return
   */
  public Color getGridLineColor();

  public boolean hasValidStepXValue(final SGAxisStepValue step);

  public boolean hasValidStepYValue(final SGAxisStepValue step);
}
