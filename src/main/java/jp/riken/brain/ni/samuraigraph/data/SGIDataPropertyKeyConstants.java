package jp.riken.brain.ni.samuraigraph.data;

/**
 * String constants for the keys of properties in the property file or data set file.
 *
 */
public interface SGIDataPropertyKeyConstants {

    public static final String KEY_X_VALUE_COLUMN_INDEX = "XValueIndex";
    
    public static final String KEY_Y_VALUE_COLUMN_INDEX = "YValueIndex";
    
    public static final String KEY_Z_VALUE_COLUMN_INDEX = "ZValueIndex";
    
    public static final String KEY_X_VALUE_COLUMN_INDICES = "XValueIndices";
    
    public static final String KEY_Y_VALUE_COLUMN_INDICES = "YValueIndices";

    public static final String KEY_LOWER_ERROR_BAR_COLUMN_INDICES = "LowerErrorBarIndices";
    
    public static final String KEY_UPPER_ERROR_BAR_COLUMN_INDICES = "UpperErrorBarIndices";

    public static final String KEY_ERROR_BAR_HOLDER_COLUMN_INDICES = "ErrorBarHolderIndices";

    public static final String KEY_TICK_LABEL_COLUMN_INDICES = "TickLabelIndices";

    public static final String KEY_TICK_LABEL_HOLDER_COLUMN_INDICES = "TickLabelHolderIndices";

    public static final String KEY_X_COORDINATE_COLUMN_INDEX = "XCoordinateIndex";
    
    public static final String KEY_Y_COORDINATE_COLUMN_INDEX = "YCoordinateIndex";

    public static final String KEY_FIRST_COMPONENT_COLUMN_INDEX = "ComponentIndex1";
    
    public static final String KEY_SECOND_COMPONENT_COLUMN_INDEX = "ComponentIndex2";
	
    /**
     * A text string of the key for the name of x-values.
     */
    public static final String KEY_X_VALUE_NAME = "XValueName";
    
    /**
     * A text string of the key for the name of y-values.
     */
    public static final String KEY_Y_VALUE_NAME = "YValueName";

    /**
     * A text string of the key for the name of z-values.
     */
    public static final String KEY_Z_VALUE_NAME = "ZValueName";
    
    /**
     * A text string of the key for the name of lower error values.
     */
    public static final String KEY_LOWER_ERROR_VALUE_NAME = "LowerErrorValueName";

    /**
     * A text string of the key for the name of upper error values.
     */
    public static final String KEY_UPPER_ERROR_VALUE_NAME = "UpperErrorValueName";

    /**
     * A text string of the key for the name of error bar holder.
     */
    public static final String KEY_ERROR_BAR_HOLDER_NAME = "ErrorBarHolderName";

    /**
     * A text string of the key for the name of tick labels.
     */
    public static final String KEY_TICK_LABEL_NAME = "TickLabelName";

    /**
     * A text string of the key for the name of tick label holder.
     */
    public static final String KEY_TICK_LABEL_HOLDER_NAME = "TickLabelHolderName";

    /**
     * A text string of the key for the names of x-values.
     */
    public static final String KEY_X_VALUE_NAMES = "XValueNames";

    /**
     * A text string of the key for the names of y-values.
     */
    public static final String KEY_Y_VALUE_NAMES = "YValueNames";

    /**
     * A text string of the key for the names of lower error values.
     */
    public static final String KEY_LOWER_ERROR_VALUE_NAMES = "LowerErrorValueNames";

    /**
     * A text string of the key for the names of upper error values.
     */
    public static final String KEY_UPPER_ERROR_VALUE_NAMES = "UpperErrorValueNames";

    /**
     * A text string of the key for the names of error bar holder.
     */
    public static final String KEY_ERROR_BAR_HOLDER_NAMES = "ErrorBarHolderNames";

    /**
     * A text string of the key for the names of tick labels.
     */
    public static final String KEY_TICK_LABEL_NAMES = "TickLabelNames";

    /**
     * A text string of the key for the names of tick label holders.
     */
    public static final String KEY_TICK_LABEL_HOLDER_NAMES = "TickLabelHolderNames";

    /**
     * A text string of the key for the name of a variable for x coordinates.
     */
    public static final String KEY_X_COORDINATE_VARIABLE_NAME = "XCoordinateVariableName";
    
    /**
     * A text string of the key for the name of a variable for y coordinates.
     */
    public static final String KEY_Y_COORDINATE_VARIABLE_NAME = "YCoordinateVariableName";

    /**
     * A text string of the key for the name of a variable for the first component.
     */
    public static final String KEY_FIRST_COMPONENT_VARIABLE_NAME = "FirstComponentVariableName";

    /**
     * A text string of the key for the name of a variable for the second component.
     */
    public static final String KEY_SECOND_COMPONENT_VARIABLE_NAME = "SecondComponentVariableName";
    
    /**
     * A text string of the key for the name of a variable for time.
     */
    public static final String KEY_TIME_VARIABLE_NAME = "TimeVariableName";

    /**
     * A text string of the key for the name of a variable for indices.
     */
    public static final String KEY_INDEX_VARIABLE_NAME = "IndexVariableName";

    /**
     * A text string of the key for the name of a variable for serial numbers.
     * (Only for the backward compatibility for the version <= 2.0.0)
     */
    public static final String KEY_SERIAL_NUMBER_VARIABLE_NAME = "SerialNumberVariableName";
    
    // unused
    public static final String KEY_X_INDEX_VARIABLE_NAME = "XIndexVariableName";

    // unused
    public static final String KEY_Y_INDEX_VARIABLE_NAME = "YIndexVariableName";

    /**
     * A text string of the key for the animation dimension.
     */
    public static final String KEY_ANIMATION_DIMENSION = "AnimationDimension";

    /**
     * A text string of the key for the name of a dimension.
     */
    public static final String KEY_PICKUP_DIMENSION_NAME = "PickupDimensionName";

    /**
     * A text string of the key for picked up dimension.
     */
    public static final String KEY_PICK_UP_DIMENSION = "PickUpDimension";

    /**
     * A text string of the key for the indices of picked up dimension.
     */
    public static final String KEY_PICK_UP_DIMENSION_INDICES = "PickUpIndices";

    /**
     * A text string of the key for the index start of a pickup.
	 * (Only for the version older than 2.1.0.)
     */
    public static final String KEY_PICKUP_START = "PickupStart";

    /**
     * A text string of the key for the index end of a pickup.
	 * (Only for the version older than 2.1.0.)
     */
    public static final String KEY_PICKUP_END = "PickupEnd";

    /**
     * A text string of the key for the index step of a pickup.
	 * (Only for the version older than 2.1.0.)
     */
    public static final String KEY_PICKUP_STEP = "PickupStep";
    
	/**
	 * A key string for availability of the array section.
	 */
	public static final String KEY_ARRAY_SECTION_AVAILABLE = "ArraySectionAvailable";

	/**
	 * A key string for the array section of the serial numbers.
	 * (Only for the version older than 2.1.0.)
	 */
	public static final String KEY_SERIAL_NUMBER_ARRAY_SECTION = "SerialNumberStride";

	/**
	 * A key string for the array section of indices.
	 */
	public static final String KEY_INDEX_ARRAY_SECTION = "IndexArraySection";

	/**
	 * A key string for the array section.
	 */
	public static final String KEY_ARRAY_SECTION = "ArraySection";

	/**
	 * A key string for the array section of the tick labels.
	 */
	public static final String KEY_TICK_LABEL_ARRAY_SECTION = "TickLabelArraySection";

	/**
	 * A key string for the array section for the x direction for two dimensional data.
	 */
	public static final String KEY_X_ARRAY_SECTION = "XArraySection";

	/**
	 * A key string for the array section for the y direction for two dimensional data.
	 */
	public static final String KEY_Y_ARRAY_SECTION = "YArraySection";
	
	/**
	 * A text string of the key for the origin map.
	 */
	public static final String KEY_ORIGIN_MAP = "Origin";

}
