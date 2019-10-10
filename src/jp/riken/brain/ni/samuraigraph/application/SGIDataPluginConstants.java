package jp.riken.brain.ni.samuraigraph.application;

/**
 * Constants for data plug-in.
 *
 */
public interface SGIDataPluginConstants {

	//
	// Constants for XML file.
	//

	public static final String TAG_NAME_PLUGIN = "plugin";

	public static final String TAG_NAME_NAME = "name";

	public static final String TAG_NAME_VERSION = "version";

	public static final String TAG_NAME_DEVELOPER = "developer";

	public static final String TAG_NAME_PARAMETERS = "parameters";

	public static final String TAG_NAME_PARAMETER = "parameter";
	
	public static final String ATTR_NAME_PARAMETER_INDEX = "index";
	
	public static final String ATTR_NAME_PARAMETER_NAME = "name";

	public static final String ATTR_NAME_PARAMETER_DEFAULT = "default";

	public static final String TAG_NAME_DESC = "description";

	public static final String TAG_NAME_LIB = "lib";

	public static final String ATTR_NAME_LIB_OS = "os";

	public static final String ATTR_NAME_LIB_ARCH = "arch";

	public static final String ATTR_NAME_LIB_HREF = "href";

	//
	// Key constants for the map.
	//
	
	public static final String KEY_PLUGIN_NAME = TAG_NAME_NAME;

	public static final String KEY_PLUGIN_VERSION = TAG_NAME_VERSION;

	public static final String KEY_PLUGIN_DEVELOPER = TAG_NAME_DEVELOPER;
	
	public static final String KEY_PLUGIN_DESC = TAG_NAME_DESC;

	public static final String KEY_PLUGIN_PARAMETER_NAME = ATTR_NAME_PARAMETER_NAME;
	
	public static final String KEY_PLUGIN_PARAMETER_DEFAULT = ATTR_NAME_PARAMETER_DEFAULT;

	public static final String KEY_PLUGIN_LIB_ARCH = ATTR_NAME_LIB_ARCH;

	public static final String KEY_PLUGIN_LIB_HREF = ATTR_NAME_LIB_HREF;
	
	//
	// Constants for data type.
	//
	
	public static final int DATA_TYPE_SXY = 0;

	public static final int DATA_TYPE_SXYZ = 1;

	public static final int DATA_TYPE_SXYZ_GRID = 2;
	
	public static final int DATA_TYPE_VXY = 3;

	public static final int DATA_TYPE_VXY_GRID = 4;

}
