package jp.riken.brain.ni.samuraigraph.data;

/**
 * String constants for the type of data columns.
 *
 */
public interface SGIDataColumnTypeConstants {

    /**
     * A constant for the number type column.
     */
    public static final String VALUE_TYPE_NUMBER = "Number";

    /**
     * A constant for the text type column.
     */
    public static final String VALUE_TYPE_TEXT = "Text";

    /**
     * A constant for the date type column.
     */
    public static final String VALUE_TYPE_DATE = "Date";

    /**
     * A constant for the column with the sampling rate.
     */
    public static final String VALUE_TYPE_SAMPLING_RATE = "Sampling Rate";

    /**
     * A constant for the byte date type column.
     */
    public static final String VALUE_TYPE_BYTE_DATA = "Byte Data";

    /**
     * A constant for x value.
     */
    public static final String X_VALUE = "X";

    /**
     * A constant for y value.
     */
    public static final String Y_VALUE = "Y";

    /**
     * A constant for z value.
     */
    public static final String Z_VALUE = "Z";

    /**
     * A constant for lower error value.
     */
    public static final String LOWER_ERROR_VALUE = "Lower Error";

    /**
     * A constant for upper error value.
     */
    public static final String UPPER_ERROR_VALUE = "Upper Error";

    /**
     * A constant for lower and upper error value.
     */
    public static final String LOWER_UPPER_ERROR_VALUE = "Lower / Upper Error";

    /**
     * A constant for tick label.
     */
    public static final String TICK_LABEL = "Tick Label";

//    /**
//     * A constant for date.
//     */
//    public static final String DATE = "Date";

    /**
     * A constant for the x coordinate.
     */
    public static final String X_COORDINATE = "X-Coordinate";

    /**
     * A constant for the y coordinate.
     */
    public static final String Y_COORDINATE = "Y-Coordinate";

    /**
     * A constant for the x component of a vector.
     */
    public static final String X_COMPONENT = "X-Component";

    /**
     * A constant for the y component of a vector.
     */
    public static final String Y_COMPONENT = "Y-Component";

    /**
     * A constant for the magnitude of a vector.
     */
    public static final String MAGNITUDE = "Magnitude";

    /**
     * A constant for the angle of a vector.
     */
    public static final String ANGLE = "Angle";

    /**
     * A constant for the animation frame.
     */
    public static final String ANIMATION_FRAME = "Animation Frame";

    /**
     * A constant for the time.
     * (Only for backward compatibility <= 2.0.0)
     */
    public static final String TIME = "Time";

    /**
     * A constant for the multiple dimension.
     */
    public static final String PICKUP = "Pickup";

    /**
     * A constant for the index.
     */
    public static final String INDEX = "Index";

    /**
     * A constant for the serial numbers.
     * (Only for backward compatibility <= 2.0.0)
     */
    public static final String SERIAL_NUMBERS = "SerialNumbers";

    /**
     * A constant for the index for x values.
     * Note: Procedure using this constant should be removed.
     */
    public static final String X_INDEX = "X Index";

    /**
     * A constant for the index for y values.
     * Note: Procedure using this constant should be removed.
     */
    public static final String Y_INDEX = "Y Index";

}
