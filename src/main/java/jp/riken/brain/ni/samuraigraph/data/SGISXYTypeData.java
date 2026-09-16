package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;

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

  /**
   * Returns the number format state shared by the scalar XY data classes.
   *
   * @return the number format state
   */
  public SGXYNumberFormat getNumberFormat();

  /** Returns the decimal places for the tick labels. */
  public default int getDecimalPlaces() {
    return this.getNumberFormat().getDecimalPlaces();
  }

  /** Returns the exponent for tick labels. */
  public default int getExponent() {
    return this.getNumberFormat().getExponent();
  }

  public default String getDateFormat() {
    return this.getNumberFormat().getDateFormat();
  }

  /**
   * Sets the decimal places for the tick labels.
   *
   * @param dp a value to set to the decimal places
   */
  public default void setDecimalPlaces(final int dp) {
    this.getNumberFormat().setDecimalPlaces(dp);
  }

  /**
   * Sets the exponent for the tick labels.
   *
   * @param exp a value to set to the exponent
   */
  public default void setExponent(final int exp) {
    this.getNumberFormat().setExponent(exp);
  }

  public default void setDateFormat(final String format) {
    this.getNumberFormat().setDateFormat(format);
  }

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

  /**
   * Returns the indices of tick labels.
   *
   * <p>If the stride is available, returns the tick label indices in the tick label stride, and
   * otherwise the indices of all points.
   */
  default int[] getTickLabelValueIndices() {
    if (!this.isTickLabelAvailable()) {
      return null;
    }
    if (this instanceof SGArrayData arrayData && arrayData.isStrideAvailable()) {
      final SGIntegerSeriesSet tickLabelStride = this.getTickLabelStride();
      if (tickLabelStride != null) {
        return tickLabelStride.getNumbers();
      }
    }
    final int len = this.getPointsNumber();
    return SGUtilityNumber.toIntArray(len);
  }

  /** Returns whether an effective stride is applied. */
  public default boolean hasEffectiveStride() {
    return SGDataViewerUtility.hasEffectiveStride(this);
  }

  /** Returns the shift. */
  public default SGTuple2d getShift() {
    return this.getNumberFormat().getShift();
  }

  /**
   * Sets the shift.
   *
   * @param shift the shift to set
   */
  public default void setShift(SGTuple2d shift) {
    this.getNumberFormat().setShift(shift);
  }

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
