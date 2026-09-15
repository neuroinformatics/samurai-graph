package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGRootObjectConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGTextDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;

import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;

/** An interface for Scalar-type XY data. */
public interface SGISXYTypeData extends SGIXYData {

  /** Returns whether error bars are available. */
  public boolean isErrorBarAvailable();

  /** Returns whether tick labels are available. */
  public boolean isTickLabelAvailable();

  /**
   * Returns whether the error bars are vertical. If this data does not have error values, returns
   * null.
   *
   * <p>not have error values
   */
  public Boolean isErrorBarVertical();

  /**
   * Returns whether the tick labels align horizontally. If this data does not have tick labels,
   * returns null.
   *
   * <p>do not have tick labels
   */
  public Boolean isTickLabelHorizontal();

  /** Returns the decimal places for the tick labels. */
  public int getDecimalPlaces();

  /** Returns the exponent for tick labels. */
  public int getExponent();

  public String getDateFormat();

  /**
   * Sets the decimal places for the tick labels.
   *
   * @param dp a value to set to the decimal places
   */
  public void setDecimalPlaces(final int dp);

  /**
   * Sets the exponent for the tick labels.
   *
   * @param exp a value to set to the exponent
   */
  public void setExponent(final int exp);

  public void setDateFormat(final String format);

  /**
   * Sets the stride.
   *
   * @param stride the stride
   */
  public void setStride(final SGIntegerSeriesSet stride);

  /** Returns the stride. */
  public SGIntegerSeriesSet getStride();

  /**
   * Sets the stride of the tick labels.
   *
   * @param stride stride of arrays
   */
  public void setTickLabelStride(SGIntegerSeriesSet stride);

  /** Returns the stride of the tick labels. */
  public SGIntegerSeriesSet getTickLabelStride();

  /** Returns the indices of tick labels. */
  public int[] getTickLabelValueIndices();

  /** Returns the shift. */
  public SGTuple2d getShift();

  /**
   * Sets the shift.
   *
   * @param shift the shift to set
   */
  public void setShift(SGTuple2d shift);

  public Boolean getDateFlag();

  public SGDate[] getDateArray(SGSXYDataBufferPolicy policy);

  public SGDate[] getDateArray(final boolean all);

  public boolean hasGenericTickLabels();

  /**
   * Returns the main stride.
   *
   * @return the main stride
   */
  public SGIntegerSeriesSet getMainStride();

  public Boolean isYValuesHolder();

  public boolean hasDateTypeXVariable();

  public boolean hasDateTypeYVariable();
}
