package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.util.List;

/** An object to manage axes. */
public interface SGIFigureElementAxis extends SGIFigureElement {

  /** Returns the list of all axes. */
  public List<SGAxis> getAxisList();

  /** Returns the list of horizontal axes. */
  public List<SGAxis> getHorizontalAxisList();

  /** Returns the list of vertical axes. */
  public List<SGAxis> getVerticalAxisList();

  /** Returns the list of normal axes. */
  public List<SGAxis> getNormalAxisList();

  /**
   * Returns whether a given axis is horizontal.
   *
   * @param axis an axis
   */
  public boolean isHorizontal(final SGAxis axis);

  /**
   * Returns whether a given axis is vertical.
   *
   * @param axis an axis
   */
  public boolean isVertical(final SGAxis axis);

  /**
   * Returns whether a given axis is normal.
   *
   * @param axis an axis
   */
  public boolean isNormal(final SGAxis axis);

  /**
   * Returns string representation of the axis location.
   *
   * @param locationInPlane - One of the following parameters: AXIS_HORIZONTAL_1, AXIS_HORIZONTAL_2,
   *     AXIS_VERTICAL_1 and AXIS_VERTICAL_2
   */
  public String getLocationName(final int locationInPlane);

  /**
   * Returns string representation of the axis location.
   *
   * @param axis an axis
   */
  public String getLocationName(final SGAxis axis);

  /**
   * Returns code of the location in a plane of a given axis.
   *
   * @param axis an axis
   */
  public int getLocationInPlane(final SGAxis axis);

  /**
   * Returns an axis at a given location.
   *
   * @param locationInPlane string representation of the location of an axis in a plane
   */
  public SGAxis getAxisInPlane(final int locationInPlane);

  /**
   * Returns an axis at a given location.
   *
   * @param str the location of an axis in a plane
   */
  public SGAxis getAxis(final String str);

  /** Returns the z-axis. */
  public SGAxis getZAxis();

  public boolean isFrameLineVisible();

  public float getFrameLineWidth();

  public float getFrameLineWidth(String unit);

  public Color getFrameLineColor();

  public boolean setFrameVisible(boolean b);

  public boolean setFrameLineWidth(float lw);

  public boolean setFrameLineWidth(float lw, String unit);

  public boolean setFrameLineColor(Color cl);

  /**
   * Returns the space between axis line and numbers in the default unit at given location.
   *
   * @param location of axis
   */
  public float getSpaceAxisLineAndNumber(final int location);

  /**
   * Returns the space between numbers and title in the default unit at given location.
   *
   * @param location of axis
   */
  public float getSpaceNumberAndTitle(final int location);

  /** Returns whether the color bar is available. */
  public boolean isColorBarAvailable();

  /** Returns the color bar model. */
  public SGColorMap getColorMap();

  /** Returns whether the color bar is visible. */
  public boolean isColorBarVisible();

  /**
   * Sets whether the color bar is visible.
   *
   * @param b to set visible
   */
  public boolean setColorBarVisible(final boolean b);

  /**
   * Fits the range of axes range to the focused data.
   *
   * @param forAnimationFrames the forAnimationFrames parameter
   * @param element a figure element for data
   */
  public boolean fitAxisRangeToFocusedData(
      SGIFigureElementForData element, final boolean forAnimationFrames);

  public boolean fitAxisRangeToFocusedData(
      SGIFigureElementForData element, int axisDirection, final boolean forAnimationFrames);

  /**
   * Fits the range of axes range to the given data.
   *
   * @param forAnimationFrames the forAnimationFrames parameter
   * @param element a figure element for data
   * @param dataList a list of data
   */
  public boolean fitAxisRangeToData(
      SGIFigureElementForData element, List<SGData> dataList, final boolean forAnimationFrames);

  public boolean fitAxisRangeToData(
      SGIFigureElementForData element,
      List<SGData> dataList,
      int axisDirection,
      final boolean forAnimationFrames);

  /**
   * @param config
   * @param x
   * @param y
   */
  public double getValue(final int config, final int x, final int y);

  /**
   * Returns whether the axis at given location is visible.
   *
   * @param location location of an axis
   */
  public boolean isAxisVisible(final int location);

  /**
   * Sets the visibility of the axis at given location is visible.
   *
   * @param location location of an axis
   * @param b true to set visible
   */
  public void setAxisVisible(final int location, final boolean b);

  public void setChanged(final int location, final boolean b);

  public void hideSelectedAxes();

  public boolean getAxisDateMode(final int location);

  public SGPropertyResults setScaleProperties(SGPropertyMap map);

  public boolean isAxisScaleVisible();

  public void setAxisScaleVisible(final boolean b);

  public void setAxisScaleVisibleForCommit(final boolean b);

  public void setColorBarVisibleForCommit(final boolean b);
}
