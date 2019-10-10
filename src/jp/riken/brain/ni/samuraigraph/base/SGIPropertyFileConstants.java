package jp.riken.brain.ni.samuraigraph.base;


/**
 * Constants for the property file.
 */
public interface SGIPropertyFileConstants {

    /**
     * File extension of the property files.
     */
    public static final String PROPERTY_FILE_EXTENSION = "sgp";

    /**
     * Description of the property files.
     */
    public static final String PROPERTY_FILE_DESCRIPTION = "Samurai Graph Property File";

    /**
     * 
     */
    public static final String PROPERTY_DTD_FILE_NAME = "property.dtd";

    /**
     * Public ID of the property files.
     */
    public static final String PROPERTY_FILE_PUBLIC_ID = "-//NILAB//DTD Samurai-Graph Property XML 1.0//EN";

    /**
     * System ID of the property files.
     */
    public static final String PROPERTY_FILE_SYSTEM_ID = "http://samurai-graph.sourceforge.jp/" + PROPERTY_DTD_FILE_NAME;

    /**
     * Default name of the property file.
     */
    public static final String DEFAULT_PROPERTY_FILE_NAME = "property";

    /**
     * Root tag name of the property file.
     */
    public static final String ROOT_TAG_NAME = "Property";

    /**
     * Key for the version number.
     */
    public static final String KEY_VERSION_NUMBER = "Version";
}
