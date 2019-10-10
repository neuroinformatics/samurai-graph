package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;

/**
 * Constants for the netCDF data.
 *
 */
public interface SGINetCDFConstants {

    public static final String ATTR_FILL_VALUE = "_FillValue";
    
    public static final String ATTR_MISSING_VALUE = "missing_value";
    
    public static final String ATTR_VALID_RANGE = "valid_range";

    public static final String ATTR_VALID_MIN = "valid_min";

    public static final String ATTR_VALID_MAX = "valid_max";

	public static final String ATTR_LONG_NAME = "long_name";
	
	public static final String ATTR_STANDARD_NAME = "standard_name";

	public static final String ATTR_UNITS = "units";

    public static final String ATTRIBUTE_VALUE_TYPE = "value_type";

    // for NetCDF Data Set
    public static final String ATTRIBUTE_PROPERTY = "samurai_graph_property";

    public static final int DIMENSION_EFFECTIVE_DIGIT = SGIConstants.AXIS_SCALE_EFFECTIVE_DIGIT;
}
