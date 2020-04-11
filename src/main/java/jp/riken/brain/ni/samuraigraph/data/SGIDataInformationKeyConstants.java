package jp.riken.brain.ni.samuraigraph.data;

/**
 * String constants for the keys of the information map for data objects.
 *
 */
public interface SGIDataInformationKeyConstants {

	/**
	 * A key string for the data type.
	 */
	public static final String KEY_DATA_TYPE = "DataType";

	/**
	 * A key string for the data name.
	 */
	public static final String KEY_DATA_NAME = "Data Name";
	
	/**
	 * A key string for the data source.
	 */
	public static final String KEY_DATA_SOURCE = "Data Source";

	/**
	 * A key string for the sampling rate.
	 */
	public static final String KEY_SAMPLING_RATE = "Sampling rate";
	
	/**
	 * A key string for the column information.
	 */
	public static final String KEY_COLUMN_INFO = "Column Information";
	
	/**
	 * A key string for the current row index.
	 */
	public static final String KEY_CURRENT_ROW_INDEX = "Current Row Index";
	
	/**
	 * A text string of the key for the size of figure.
	 */
	public static final String KEY_FIGURE_SIZE = "Figure Size";

	/**
	 * A key string whether to draw multiple graphs.
	 */
	public static final String KEY_SXY_MULTIPLE = "Multiple";
	
	/**
	 * A key string whether multiple graphs are for multiple variables.
	 */
	public static final String KEY_SXY_MULTIPLE_VARIABLE = "Graphs for multiple variables";
	
    /**
     * A text string of the key for the indices of picked up data sets.
     */
    public static final String KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP = "Pickup dimension index map";

	/**
	 * A key string for the indices of a picked up dimension for scalar XY type data.
	 */
	public static final String KEY_SXY_PICKUP_INDICES = "Pickup indices";
	
	/**
	 * A key string for the start index of multiple graph of dimension.
	 * (Only for the version older than 2.1.0.)
	 */
	public static final String KEY_SXY_PICKUP_DIMENSION_START = "Multiple dimension start";

	/**
	 * A key string for the last index of multiple graph of dimension.
	 * (Only for the version older than 2.1.0.)
	 */
	public static final String KEY_SXY_PICKUP_DIMENSION_END = "Multiple dimension end";

	/**
	 * A key string for the step of multiple graph of dimension.
	 * (Only for the version older than 2.1.0.)
	 */
	public static final String KEY_SXY_PICKUP_DIMENSION_STEP = "Multiple dimension step";
	
	/**
	 * A key string whether the polar mode is selected for the vector data.
	 */
	public static final String KEY_VXY_POLAR_SELECTED = "Polar selected";

	/**
	 * A key string for the stride of scalar XY type data.
	 */
	public static final String KEY_SXY_STRIDE = "SXY Stride";

	/**
	 * A key string for the stride of the tick labels of scalar XY type data.
	 */
	public static final String KEY_SXY_TICK_LABEL_STRIDE = "SXY Tick Label Stride";

	/**
	 * A key string for the stride of the index of scalar XY type data.
	 */
	public static final String KEY_SXY_INDEX_STRIDE = "SXY Index Stride";

	/**
	 * A key string for the stride for the x direction of scalar XYZ type data.
	 */
	public static final String KEY_SXYZ_STRIDE_X = "SXYZ Stride X";

	/**
	 * A key string for the stride for the y direction of scalar XYZ type data.
	 */
	public static final String KEY_SXYZ_STRIDE_Y = "SXYZ Stride Y";

	/**
	 * A key string for the stride of the index of scalar XYZ type data.
	 */
	public static final String KEY_SXYZ_INDEX_STRIDE = "SXYZ Index Stride";

	/**
	 * A key string for the stride for the x direction of vector XY type data.
	 */
	public static final String KEY_VXY_STRIDE_X = "VXY Stride X";

	/**
	 * A key string for the stride for the y direction of vector XY type data.
	 */
	public static final String KEY_VXY_STRIDE_Y = "VXY Stride Y";

	/**
	 * A key string for the stride of the index of vector XY type data.
	 */
	public static final String KEY_VXY_INDEX_STRIDE = "VXY Index Stride";
	
	/**
	 * A key string whether to set enabled the stride of data arrays.
	 */
	public static final String KEY_STRIDE_AVAILABLE = "Stride Available";

	/**
	 * A key string for all stride.
	 */
	public static final String KEY_ALL_STRIDE = "All Stride";

    /**
     * A text string of the key for the map of time dimension index.
     */
    public static final String KEY_TIME_DIMENSION_INDEX_MAP = "Time dimension index map";
    
    public static final String KEY_SXYZ_GRID_PLOT_FLAG = "SXYZ Grid Plot";
    
    public static final String KEY_VXY_GRID_PLOT_FLAG = "VXY Grid Plot";


}
