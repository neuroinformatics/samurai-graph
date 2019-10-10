package jp.riken.brain.ni.samuraigraph.application;

/**
 * Constants for the upgrading of the application.
 */
public interface SGIUpgradeConstants {

    /**
     * URL of the XML file for the product information.
     */
    public static final String PRODUCT_XML_FILE_NAME = "http://samurai-graph.sourceforge.jp/product.xml";

    /**
     * URL of the XML file for the product information for developer mode.
     */
    public static final String DEV_PRODUCT_XML_FILE_NAME = "http://samurai-graph.sourceforge.jp/product_debug.xml";

    /**
     * Not upgrading automatically.
     */
    public static final int NO_UPGRADE = 0;

    public static final int UPGRADE_EVERY_TIME = 1;

    public static final int UPGRADE_EVERY_DAY = 2;

    public static final int UPGRADE_EVERY_WEEK = 3;

    public static final int UPGRADE_EVERY_MONTH = 4;

    /**
     * Name of the upgrade helper application.
     */
    public static final String UPGRADE_HELPER_FILE_NAME = "upgrade-helper.jar";

    /**
     * Temporary folder used by helper applications.
     */
    public static final String HELPER_TEMP_DIR_NAME = "SamuraiGraphTemp";

}
