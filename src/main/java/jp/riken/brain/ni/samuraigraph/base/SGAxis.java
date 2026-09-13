package jp.riken.brain.ni.samuraigraph.base;

/** The axis with the range and the scale type. */
public class SGAxis implements Cloneable {

  /** A constant for the linear scale. */
  public static final int LINEAR_SCALE = 0;

  /** A constant for the log scale. */
  public static final int LOG_SCALE = 1;

  // The type of scale.
  private int mScaleType = LINEAR_SCALE;

  // The minimum value.
  private SGAxisValue mMinValue;

  // The maximum value.
  private SGAxisValue mMaxValue;

  // The flag for invert coordinates.
  private boolean mInvertCoordinates = false;

  // The flag for date mode.
  private boolean mDateFlag = false;

  /**
   * Creates an axis object with the given minimum and maximum value. The scale is linear type by
   * default. The minValue must be smaller than maxValue.
   *
   * @param minValue the minimum value
   * @param maxValue the maximum value
   */
  public SGAxis(final double minValue, final double maxValue) {
    this(minValue, maxValue, LINEAR_SCALE);
  }

  /**
   * Creates an axis object with the given minimum and maximum value. The scale is linear type by
   * default. The minValue must be smaller than maxValue.
   *
   * @param minValue the minimum value
   * @param maxValue the maximum value
   * @param type scale type
   */
  public SGAxis(final double minValue, final double maxValue, final int type) {
    super();
    this.setScaleImpl(new SGAxisDoubleValue(minValue), new SGAxisDoubleValue(maxValue), type);
  }

  /**
   * Creates an axis object with the given range. The scale is linear type by default.
   *
   * @param range range of the axis
   */
  public SGAxis(final SGTuple2d range) {
    this(range, LINEAR_SCALE);
  }

  /**
   * Creates an axis object with the given range.
   *
   * @param range range of the axis
   * @param type scale type
   */
  public SGAxis(final SGTuple2d range, final int type) {
    super();
    this.setScaleImpl(new SGAxisDoubleValue(range.x), new SGAxisDoubleValue(range.y), type);
  }

  /**
   * Set the invert coordinates flag.
   *
   * @param b The invert coordinates flag.
   */
  public boolean setInvertCoordinates(final boolean b) {
    this.mInvertCoordinates = b;
    return true;
  }

  /**
   * Sets the scale range.
   *
   * @param range the axis value range to set
   */
  public boolean setScale(final SGTuple2d range) {
    if (range == null) {
      throw new IllegalArgumentException("range == null");
    }
    return this.setScale(range.x, range.y, this.getScaleType());
  }

  /**
   * Sets the scale type and the scale range.
   *
   * @param range the axis value range to set
   * @param scaleType the type of scale
   */
  public boolean setScale(final SGTuple2d range, final int scaleType) {
    if (range == null) {
      throw new IllegalArgumentException("range == null");
    }
    return this.setScale(range.x, range.y, scaleType);
  }

  /**
   * Sets the scale type and the scale range.
   *
   * @param minValue the minimum value
   * @param maxValue the maximum value
   * @param scaleType the type of scale
   */
  public boolean setScale(final double minValue, final double maxValue, final int scaleType) {
    return this.setScale(
        new SGAxisDoubleValue(minValue), new SGAxisDoubleValue(maxValue), scaleType);
  }

  public boolean setScale(final SGAxisValue min, final SGAxisValue max) {
    return this.setScale(min, max, this.getScaleType());
  }

  public boolean setScale(final SGAxisValue min, final SGAxisValue max, final int scaleType) {
    return setScaleImpl(min, max, scaleType);
  }

  private boolean setScaleImpl(final SGAxisValue min, final SGAxisValue max, final int scaleType) {
    // checks wrong scale type
    if (isValidScaleType(scaleType) == false) {
      throw new IllegalArgumentException("Invalid scale type: " + scaleType);
    }

    final double minValue = min.getValue();
    final double maxValue = max.getValue();

    // the minimum value is smaller than the maximum value
    if (minValue >= maxValue) {
      throw new IllegalArgumentException("minValue >= maxValue: " + minValue + " >= " + maxValue);
    }

    // non-positive value is not accepted in the log scale
    if (!isValidValue(minValue, scaleType) || !isValidValue(maxValue, scaleType)) {
      throw new IllegalArgumentException("Input values are invalid in a given scale type.");
    }

    // set to the attributes
    this.mScaleType = scaleType;
    this.mMinValue = min;
    this.mMaxValue = max;

    return true;
  }

  /** Returns the minimum value. */
  public double getMinDoubleValue() {
    return this.mMinValue.getValue();
  }

  /** Returns the maximum value. */
  public double getMaxDoubleValue() {
    return this.mMaxValue.getValue();
  }

  public SGAxisValue getMinValue() {
    return this.mMinValue;
  }

  public SGAxisValue getMaxValue() {
    return this.mMaxValue;
  }

  /** Returns the invert coordinate flag. */
  public boolean isInvertCoordinates() {
    return this.mInvertCoordinates;
  }

  /** Returns the range. */
  public SGTuple2d getRange() {
    return new SGTuple2d(this.getMinDoubleValue(), this.getMaxDoubleValue());
  }

  /** Returns the scale type. */
  public int getScaleType() {
    return this.mScaleType;
  }

  /**
   * Returns whether the given value is inside the range.
   *
   * @param value the value to be checked
   */
  public boolean insideRange(final double value) {
    return (this.getMinDoubleValue() <= value && value <= this.getMaxDoubleValue());
  }

  /**
   * Returns whether the given value is "valid" in the present axis.
   *
   * @param value the value to be checked
   */
  public boolean isValidValue(final double value) {
    return isValidValue(value, this.getScaleType());
  }

  /**
   * Returns whether the given value is "valid" in a given scale type.
   *
   * @param value the value to be checked
   * @param scaleType the scale type
   */
  public static boolean isValidValue(final double value, final int scaleType) {
    // negative value is "invalid" in the log scale
    if (scaleType == SGAxis.LOG_SCALE) {
      if (value <= 0.0) {
        return false;
      }
    }
    return true;
  }

  public static boolean isValidValue(final SGAxisValue value, final int scaleType) {
    return isValidValue(value.getValue(), scaleType);
  }

  /** Clones this axis. */
  public final Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException ex) {
      // this shouldn't happen, since we are Cloneable
      throw new InternalError();
    }
  }

  /**
   * Returns whether the given scale type is valid.
   *
   * @param type the scale type to check
   */
  public static boolean isValidScaleType(final int type) {
    return ((LINEAR_SCALE == type) || (LOG_SCALE == type));
  }

  /** Returns the flag for date mode. */
  public boolean getDateMode() {
    return this.mDateFlag;
  }

  /**
   * Sets the flag for date mode.
   *
   * @param b the flag to set
   */
  public void setDateMode(final boolean b) {
    this.mDateFlag = b;
  }
}
