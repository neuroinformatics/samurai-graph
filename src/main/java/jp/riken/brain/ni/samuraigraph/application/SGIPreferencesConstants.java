package jp.riken.brain.ni.samuraigraph.application;

/**
 * Constants used in backing store.
 */
public interface SGIPreferencesConstants {
    /**
     * The key for the time Samurai Graph is used.
     */
    public static final String PREF_KEY_DATE = "Date";

    /**
     * The key of upgrade cycle.
     */
    public static final String PREF_KEY_UPGRADE_CYCLE = "Upgrade Cycle";

    /**
     * The key of proxy host name.
     */
    public static final String PREF_KEY_PROXY_HOST_NAME = "Proxy Host Name";

    /**
     * The key of proxy port number.
     */
    public static final String PREF_KEY_PROXY_PORT_NUMBER = "Proxy Port Number";

    /**
     * The key of the flag whether to access directly.
     */
    public static final String PREF_KEY_DIRECT_ACCESS = "Direct Access";

    /**
     * The key of the current directory.
     */
    public static final String PREF_KEY_CURRENT_DIRECTORY = "Current Directory";

    /**
     * The key of the pattern of the tool bar.
     */
    public static final String PREF_KEY_TOOL_BAR_PATTERN = "Tool Bar Pattern";

    // for old version
    // constants to be removed from the backing store
    public static final String PREF_KEY_MAJOR_VERSION_NUMBER = "Major Version";

    public static final String PREF_KEY_MINOR_VERSION_NUMBER = "Minor Version";

    public static final String PREF_KEY_MICRO_VERSION_NUMBER = "Micro Version";
    
    /**
     * The key whether the stride of data arrays is available by default.
     */
    public static final String PREF_KEY_DATA_STRIDE_AVAILABLE = "Data Stride Available";

    /**
     * The key for the width of the viewport.
     */
    public static final String PREF_KEY_VIEWPORT_WIDTH = "Viewport Width";

    /**
     * The key for the height of the viewport.
     */
    public static final String PREF_KEY_VIEWPORT_HEIGHT = "Viewport Height";

}
