package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.data.SGPickUpDimensionInfo;

/** An observer of the property dialog for two-dimensional scalar type data. */
public interface SGISXYDataDialogObserver
    extends SGIDataPropertyDialogObserver, SGITwoAxesHolder, SGILineStyleDialogObserver {

  /**
   * Returns the type of clicked objects.
   *
   * @return
   */
  public SGISXYDataConstants.ELEMENT_TYPE getSelectedGroupType();

  // Line
  /**
   * @return
   */
  public boolean isLineVisible();

  /**
   * Returns whether the lines connect all effective points.
   *
   * @return true if connecting all effective points
   */
  public boolean isLineConnectingAll();

  /**
   * @return
   */
  public double getShiftX();

  /**
   * @return
   */
  public double getShiftY();

  /**
   * @param b
   * @return
   */
  public boolean setLineVisible(final boolean b);

  /**
   * Sets whether the lines connect all effective points.
   *
   * @return true if succeeded
   */
  public boolean setLineConnectingAll(final boolean b);

  /**
   * Sets the shift size of line to x direction.
   *
   * @param shift
   * @return true if succeeded
   */
  public boolean setShiftX(final double shift);

  /**
   * Sets the shift size of line to y direction.
   *
   * @param shift
   * @return true if succeeded
   */
  public boolean setShiftY(final double shift);

  // Symbol
  /**
   * @return
   */
  public boolean isSymbolVisible();

  /**
   * @return
   */
  public int getSymbolType();

  public float getSymbolSize(final String unit);

  public float getSymbolLineWidth(final String unit);

  /**
   * @return
   */
  public SGIPaint getSymbolInnerPaint();

  /**
   * @return
   */
  public Color getSymbolLineColor();

  /**
   * @return
   */
  public boolean isSymbolLineVisible();

  /**
   * @param b
   * @return
   */
  public boolean setSymbolVisible(final boolean b);

  /**
   * @param type
   * @return
   */
  public boolean setSymbolType(final int type);

  public boolean setSymbolSize(final float size, final String unit);

  public boolean setSymbolLineWidth(final float width, final String unit);

  /**
   * @param paint
   * @return
   */
  public boolean setSymbolInnerPaint(final SGIPaint paint);

  /**
   * @param cl
   * @return
   */
  public boolean setSymbolLineColor(final Color cl);

  /**
   * @param b
   * @return
   */
  public boolean setSymbolLineVisible(final boolean b);

  // Bar
  /**
   * @return
   */
  public boolean isBarVisible();

  /**
   * @return
   */
  public double getBarBaselineValue();

  /**
   * @return
   */
  public double getBarWidthValue();

  public float getBarEdgeLineWidth(final String unit);

  /**
   * @return
   */
  public SGIPaint getBarInnerPaint();

  /**
   * @return
   */
  public Color getBarEdgeLineColor();

  /**
   * @return
   */
  public boolean isBarEdgeLineVisible();

  /**
   * @return
   */
  public boolean isBarVertical();

  /**
   * @return
   */
  public double getBarOffsetX();

  /**
   * @return
   */
  public double getBarOffsetY();

  /**
   * @return
   */
  public double getBarInterval();

  /**
   * @param b
   * @return
   */
  public boolean setBarVisible(final boolean b);

  /**
   * @param value
   * @return
   */
  public boolean setBarBaselineValue(final double value);

  /**
   * @param width
   * @return
   */
  public boolean setBarWidthValue(final double width);

  public boolean setBarEdgeLineWidth(final float width, final String unit);

  /**
   * @param paint
   * @return
   */
  public boolean setBarInnerPaint(final SGIPaint paint);

  /**
   * @param cl
   * @return
   */
  public boolean setBarEdgeLineColor(final Color cl);

  /**
   * @param b
   * @return
   */
  public boolean setBarEdgeLineVisible(final boolean b);

  public boolean setBarVertical(final boolean b);

  public boolean hasValidBaselineValue(final int config, final Number value);

  /**
   * @return
   */
  public boolean setBarOffsetX(final double shift);

  /**
   * @return
   */
  public boolean setBarOffsetY(final double shift);

  /**
   * @return
   */
  public boolean setBarInterval(final double interval);

  // Error Bar
  public boolean isErrorBarAvailable();

  /**
   * @return
   */
  public boolean isErrorBarVisible();

  /**
   * @return
   */
  public int getErrorBarHeadType();

  public float getErrorBarHeadSize(final String unit);

  /**
   * @return
   */
  public Color getErrorBarColor();

  public float getErrorBarLineWidth(final String unit);

  /**
   * @return
   */
  public int getErrorBarStyle();

  public boolean isErrorBarVertical();

  public boolean isErrorBarOnLinePosition();

  /**
   * @param b
   * @return
   */
  public boolean setErrorBarVisible(final boolean b);

  /**
   * @param type
   * @return
   */
  public boolean setErrorBarHeadType(final int type);

  public boolean setErrorBarHeadSize(final float size, final String unit);

  /**
   * @param cl
   * @return
   */
  public boolean setErrorBarColor(final Color cl);

  public boolean setErrorBarLineWidth(final float width, final String unit);

  /**
   * @param style
   * @return
   */
  public boolean setErrorBarStyle(final int style);

  public boolean setErrorBarVertical(final boolean b);

  public boolean setErrorBarOnLinePosition(final boolean b);

  // Tick Label
  public boolean isTickLabelAvailable();

  /**
   * @return
   */
  public boolean isTickLabelVisible();

  /**
   * @return
   */
  public String getTickLabelFontName();

  /**
   * @return
   */
  public int getTickLabelFontStyle();

  public float getTickLabelFontSize(final String unit);

  /**
   * @return
   */
  public Color getTickLabelColor();

  /**
   * @return true if tick labels align horizontally. Return false if vertically.
   */
  public boolean hasTickLabelHorizontalAlignment();

  /**
   * @return
   */
  public float getTickLabelAngle();

  /**
   * @param b
   * @return
   */
  public boolean setTickLabelVisible(final boolean b);

  /**
   * @param name
   * @return
   */
  public boolean setTickLabelFontName(final String name);

  /**
   * @param style
   * @return
   */
  public boolean setTickLabelFontStyle(final int style);

  public boolean setTickLabelFontSize(final float size, final String unit);

  public boolean setTickLabelDateFormat(final String format);

  /**
   * @param cl
   * @return
   */
  public boolean setTickLabelColor(final Color cl);

  /**
   * @param b
   * @return
   */
  public boolean setTickLabelHorizontalAlignment(final boolean b);

  /**
   * @param angle
   * @return
   */
  public boolean setTickLabelAngle(final float angle);

  /**
   * Returns the decimal places of tick labels.
   *
   * @return the decimal places of tick labels
   */
  public int getTickLabelDecimalPlaces();

  /**
   * Returns the exponent of tick labels.
   *
   * @return the exponent of tick labels
   */
  public int getTickLabelExponent();

  public String getTickLabelDateFormat();

  /**
   * Sets the value of the decimal places of tick labels.
   *
   * @param dp a value to set to the decimal places of tick labels
   * @return true if succeeded
   */
  public boolean setTickLabelDecimalPlaces(final int dp);

  /**
   * Sets the value of the exponent of tick labels.
   *
   * @param exp a value to set to the exponent of tick labels
   * @return true if succeeded
   */
  public boolean setTickLabelExponent(final int exp);

  /**
   * Sets the information of picked up dimension.
   *
   * @param info the information of picked up dimension
   * @return true if succeeded
   */
  public boolean setPickUpDimensionInfo(SGPickUpDimensionInfo info);

  /**
   * Sets the stride.
   *
   * @param stride stride of arrays
   * @return true if succeeded
   */
  public boolean setStride(SGIntegerSeriesSet stride);

  /**
   * Sets the stride of the tick labels.
   *
   * @param stride stride of arrays
   * @return true if succeeded
   */
  public boolean setTickLabelStride(SGIntegerSeriesSet stride);

  public boolean hasDateTickLabels();
}
