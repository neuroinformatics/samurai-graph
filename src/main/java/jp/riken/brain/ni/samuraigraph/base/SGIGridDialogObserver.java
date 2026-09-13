package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;

/** An observer of the grid dialog. */
public interface SGIGridDialogObserver extends SGIPropertyDialogObserver, SGITwoAxesHolder {

  /**
   * @param b
   * @return
   */
  public boolean setGridVisible(final boolean b);

  public boolean setAutoRangeFlag(final boolean b);

  /**
   * @param value
   * @return
   */
  public boolean setStepValueX(final SGAxisStepValue value);

  /**
   * @param value
   * @return
   */
  public boolean setStepValueY(final SGAxisStepValue value);

  /**
   * @param value
   * @return
   */
  public boolean setBaselineValueX(final SGAxisValue value);

  /**
   * @param value
   * @return
   */
  public boolean setBaselineValueY(final SGAxisValue value);

  public boolean setLineWidth(final float width, final String unit);

  /**
   * @param type
   * @return
   */
  public boolean setLineType(final int type);

  /**
   * @param cl
   * @return
   */
  public boolean setColor(final Color cl);

  /**
   * @return
   */
  public boolean isGridVisible();

  public boolean isAutoRange();

  /**
   * @return
   */
  public SGAxisStepValue getStepValueX();

  /**
   * @return
   */
  public SGAxisStepValue getStepValueY();

  /**
   * @return
   */
  public SGAxisValue getBaselineValueX();

  /**
   * @return
   */
  public SGAxisValue getBaselineValueY();

  public float getLineWidth(final String unit);

  /**
   * @return
   */
  public int getLineType();

  /**
   * @return
   */
  public Color getColor();

  public boolean hasValidStepXValue(final SGAxisStepValue step);

  public boolean hasValidStepYValue(final SGAxisStepValue step);
}
