package jp.riken.brain.ni.samuraigraph.application;

import jp.riken.brain.ni.samuraigraph.data.SGIDataFileConstants;

/**
 * Constants used in this application.
 * 
 */
public interface SGIApplicationConstants extends SGIDataFileConstants {

    /**
     * Application resource directory
     */
    public static final String APPLICATION_RESOURCE_DIRECTORY = "resources";

    /**
     * Name of the property file.
     */
    public static final String APPLICATION_PROPERTY_FILENAME = "samurai-graph.properties";

    /**
     * A key string for the version in properties.
     */
    public static final String VERSION_PROPERTY_NAME = "samurai-graph.version";

    /**
     * A key string for the upgrading cycle in properties
     */
    public static final String UPGRADE_CYCLE_PROPERTY_NAME = "samurai-graph.upgrade-cycle";

    /**
     * Enumeration of file type.
     *
     */
    enum FILE_TYPE {
    	TXT_DATA, NETCDF_DATA, HDF5_DATA, MATLAB_DATA, VIRTUAL_DATA, POSSIBLY_HDF5_DATA, 
    	PROPERTY, DATASET, SCRIPT, IMAGE,
    };

    /**
     * Application plug-in directory
     */
    public static final String APPLICATION_PLUGIN_DIRECTORY = "plugins";

    /**
     * Constants for the status of reloading data.
     */
    enum RELOAD_DATA_STATUS {
    	SUCCEEDED,
    	LOST,
    	INVALID_DATA,
    	NULL,
    }

    static final String PROGRESS_MESSAGE_READFILE = "Read File";

    static final String PROGRESS_MESSAGE_CREATEDATA = "Create Data";

    /**
     * The name of an attribute for Samurai Graph properties.
     */
    public static final String ATTR_NAME_SAMURAI_GRAPH_PROPERTIES = "samurai_graph_properties";
    
    /**
     * The name of an attribute for Samurai Graph command.
     */
    public static final String ATTR_NAME_SAMURAI_GRAPH_COMMAND = "samurai_graph_command";
}
