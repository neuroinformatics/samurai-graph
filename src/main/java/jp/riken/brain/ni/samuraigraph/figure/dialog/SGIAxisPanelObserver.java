package jp.riken.brain.ni.samuraigraph.figure.dialog;

/** An observer of the property panel for single axis. */
public interface SGIAxisPanelObserver {

  /**
   * @return
   */
  public boolean isAxisVisible();

  /**
   * @return
   */
  public boolean isTitleVisible();

  /**
   * @return
   */
  public String getTitleString();

  /**
   * @return
   */
  public double getMinValue();

  /**
   * @return
   */
  public double getMaxValue();

  /**
   * @return
   */
  public boolean isInvertCoordinates();

  /**
   * @return
   */
  public int getScaleType();

  /**
   * @return
   */
  public boolean isCalculateAutomatically();

  /**
   * @return
   */
  public double getStepValue();

  /**
   * @return
   */
  public double getBaselineValue();

  /**
   * @return
   */
  public boolean isTickMarksVisible();

  /**
   * @return
   */
  public boolean isTickMarksInside();

  /**
   * @return
   */
  public boolean isNumbersVisible();

  /**
   * @return
   */
  public boolean isNumbersInteger();

  /**
   * @return
   */
  public boolean getExponentFlag();

  /**
   * @return
   */
  public int getExponentValue();

  /**
   * @param b
   * @return
   */
  public boolean setAxisVisible(final boolean b);

  /**
   * @param b
   * @return
   */
  public boolean setTitleVisible(final boolean b);

  /**
   * @param str
   * @return
   */
  public boolean setTitle(final String str);

  /**
   * @param b
   * @return
   */
  public boolean setInvertCoordinates(final boolean b);

  /**
   * Sets the range and the scale type.
   *
   * @param minValue the minimum value
   * @param maxValue the maximum value
   * @param scaleType the scale type
   * @return true if succeeded
   */
  public boolean setScale(final Number minValue, final Number maxValue, final Integer scaleType);

  /**
   * @param b
   * @return
   */
  public boolean setCalculateAutomatically(final boolean b);

  /**
   * @param value
   * @return
   */
  public boolean setStepValue(final double value);

  /**
   * @param value
   * @return
   */
  public boolean setBaselineValue(final double value);

  /**
   * @param b
   * @return
   */
  public boolean setTickMarksVisible(final boolean b);

  /**
   * @param b
   * @return
   */
  public boolean setTickMarksInside(final boolean b);

  /**
   * @param b
   * @return
   */
  public boolean setNumbersVisible(final boolean b);

  /**
   * @param b
   * @return
   */
  public boolean setNumbersInteger(final boolean b);

  /**
   * @param b
   * @return
   */
  public boolean setExponentFlag(final boolean b);

  /**
   * @param value
   * @return
   */
  public boolean setExponentValue(final int value);

  /**
   * @param minValue
   * @param maxValue
   * @param scaleType
   * @return
   */
  public boolean hasValidAxisRange(
      final Number minValue, final Number maxValue, final Integer scaleType);

  /**
   * @param baseValue
   * @param stepValue
   * @param scaleType
   * @return
   */
  public boolean hasValidAxisValues(
      final Number baseValue, final Number stepValue, final Integer scaleType);
}
