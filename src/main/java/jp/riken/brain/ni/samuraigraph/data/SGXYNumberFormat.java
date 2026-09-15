package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;

/**
 * The number format state shared by the scalar XY data classes: the decimal places and the exponent
 * for the tick labels and the shift of the coordinate values.
 */
class SGXYNumberFormat {

  /** The decimal places for the tick labels. */
  private int mDecimalPlaces = 0;

  /** The exponent for the tick labels. */
  private int mExponent = 0;

  /** The shift value. */
  private SGTuple2d mShift = new SGTuple2d();

  /** The date format string. */
  private String mDateFormat = "";

  /** The default constructor. */
  SGXYNumberFormat() {
    super();
  }

  /** Disposes of this object. */
  public void dispose() {
    this.mShift = null;
  }

  /** Returns the decimal places for the tick labels. */
  public int getDecimalPlaces() {
    return this.mDecimalPlaces;
  }

  /**
   * Sets the decimal places for the tick labels.
   *
   * @param dp a value to set to the decimal places
   */
  public void setDecimalPlaces(final int dp) {
    if (dp < 0) {
      throw new IllegalArgumentException("Decimal places must not be negative: " + dp);
    }
    this.mDecimalPlaces = dp;
  }

  /** Returns the exponent for the tick labels. */
  public int getExponent() {
    return this.mExponent;
  }

  /**
   * Sets the exponent for the tick labels.
   *
   * @param exp a value to set to the exponent
   */
  public void setExponent(final int exp) {
    this.mExponent = exp;
  }

  /** Returns the shift value. */
  public SGTuple2d getShift() {
    return (SGTuple2d) this.mShift.clone();
  }

  /**
   * Sets the shift value.
   *
   * @param shift the shift
   */
  public void setShift(final SGTuple2d shift) {
    if (shift == null) {
      throw new IllegalArgumentException("shift == null");
    }
    this.mShift = (SGTuple2d) shift.clone();
  }

  /** Returns the date format string. */
  public String getDateFormat() {
    return this.mDateFormat;
  }

  /**
   * Sets the date format string.
   *
   * @param format the date format string
   */
  public void setDateFormat(final String format) {
    this.mDateFormat = format;
  }
}
