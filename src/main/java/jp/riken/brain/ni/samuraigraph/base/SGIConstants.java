package jp.riken.brain.ni.samuraigraph.base;

import java.io.File;

/**
 * The basic constants.
 *
 */
public interface SGIConstants {

    /**
     * The ratio of cm to inch.
     */
    public static final float CM_INCH_RATIO = 2.54f;

    /**
     * The ratio of inch to point.
     */
    public static final float INCH_POINT_RATIO = 72.0f;

    /**
     * The ratio of cm to point.
     */
    public static final float CM_POINT_RATIO = CM_INCH_RATIO / INCH_POINT_RATIO;

    /**
     * The ratio of radian to degree.
     */
    public static final float RADIAN_DEGREE_RATIO = (float) (Math.PI / 180.0);

    /**
     * The golden ratio.
     */
    public static final float GOLDEN_RATIO = (float) ((1.0 + Math.sqrt(5.0)) / 2.0);

    /**
     * The units of length.
     */
    public static final String cm = "cm";

    public static final String mm = "mm";

    public static final String pt = "pt";

    public static final String inch = "inch";

    public static final String LINE_WIDTH_UNIT = pt;

    public static final String FONT_SIZE_UNIT = pt;

    /**
     * The unit of angle.
     */
    public static final char degChar = SGStringPatterns.getChar("degree");

    public static final String degree = Character.toString(degChar);


    /**
     * The unit of percentage.
     */
    public static final String percent = "%";

    /**
     * The algebraic signs.
     */
    public static final char multiplyChar = SGStringPatterns.getChar("times");

    public static final String multiply = Character.toString(multiplyChar);

    /**
     * Two-byte space.
     */
    public static final char twoByteSpaceChar = '\u3000';

    /**
     * OS name.
     */
    public static final String OS_NAME = System.getProperty("os.name");

    /**
     * Java home directory.
     */
    public static final String JAVA_HOME = System.getProperty("java.home");

    /**
     * File separator.
     */
    public static final String FILE_SEPARATOR = File.separator;

    /**
     * Path separator.
     */
    public static final String PATH_SEPARATOR = File.pathSeparator;

    /**
     * Line separator.
     */
    public static final String LINE_SEPARATOR = System
            .getProperty("line.separator");

    /**
     * User name.
     */
    public static final String USER_NAME = System.getProperty("user.name");

    /**
     * User home directory.
     */
    public static final String USER_HOME = System.getProperty("user.home");

    /**
     * Temporary directory.
     */
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    /**
     * Directory name of resources.
     */
    public static final String RESOURCES_DIRNAME = "/resources/";

    /**
     * A constant for the Windows systems.
     */
    public static final String OS_NAME_WINDOWS = "windows";

    /**
     * A constant for the Mac OS X system.
     */
    public static final String OS_NAME_MACOSX = "mac os x";

    /**
     * A constant for "My Documents" directory in the Windows systems.
     */
    public static final String MY_DOCUMENTS = "My Documents";

    /**
     * Scale Type
     */
    public static final String SCALE_TYPE_LINEAR = "Linear";

    public static final String SCALE_TYPE_LOG = "Log";

    /**
     * Return values from class method
     */
    public static final int SUCCESSFUL_COMPLETION = 0;

    public static final int PROPERTY_FILE_INCORRECT = 1;

    public static final int DATA_FILE_INVALID = 2;

    public static final int DATA_NUMBER_SHORTAGE = 3;

    public static final int DATA_NUMBER_EXCESS = 4;

    public static final int FILE_OPEN_FAILURE = 5;

    public static final int FILE_SAVE_FAILURE = -1;

    /**
     * Menu commands
     */
    public static final String MENUCMD_CUT = "Cut";

    public static final String MENUCMD_COPY = "Copy";

    public static final String MENUCMD_PASTE = "Paste";

    public static final String MENUCMD_DELETE = "Delete";

    public static final String MENUCMD_DUPLICATE = "Duplicate";

    public static final String MENUCMD_ANCHORED = "Anchored";

    public static final String MENUCMD_PROPERTY = "Property";

    /**
     * The menu commands to change the layer.
     */
    public static final String MENUCMD_ARRANGE = "Arrange";

    public static final String MENUCMD_BRING_TO_FRONT = "Bring to Front";

    public static final String MENUCMD_BRING_FORWARD = "Bring Forward";

    public static final String MENUCMD_SEND_BACKWARD = "Send Backward";

    public static final String MENUCMD_SEND_TO_BACK = "Send to Back";

    /**
     * The menu command to show netCDF properties.
     */
    public static final String MENUCMD_INSERT_NETCDF_LABEL = "Insert Label of Fixed Values";
    
    public static final String MENUCMD_PLUGIN = "Plug-in";

    /**
     * The menu command to play the animation.
     */
    public static final String MENUCMD_ANIMATION = "Play Animation";

    /**
     * The menu command to save properties into global attributes of NetCDF file.
     */
    public static final String MENUCMD_ADD_PROPERTIES_TO_NETCDF = "Add Properties to NetCDF";

    /**
     * The menu command to save command script into global attributes of NetCDF file.
     */
    public static final String MENUCMD_ADD_COMMANDS_TO_NETCDF = "Add Commands to NetCDF";

    /**
     * The menu commands to fit the axes to the data.
     */
    public static final String MENUCMD_FIT_AXES_TO_DATA = "Fit Axes";

    public static final String MENUCMD_FIT_ALL_AXES_TO_DATA = "All Axes";

    public static final String MENUCMD_FIT_ALL_AXES_TO_DATA_FOR_ALL_ANIMATION_FRAMES = "All Axes (All Animation Frames)";

    public static final String MENUCMD_FIT_HORIZONTAL_AXIS_TO_DATA = "Horizontal Axis";

    public static final String MENUCMD_FIT_VERTICAL_AXIS_TO_DATA = "Vertical Axis";

    public static final String MENUCMD_FIT_COLOR_BAR_TO_DATA = "Color Bar";

    /**
     * The menu command to fit the axes to the data.
     */
    public static final String MENUCMD_AXES_VISIBLE = "Axes Visible";

    public static final String MENUCMD_VISIBLE_BOTTOM_AXIS = "Bottom";

    public static final String MENUCMD_VISIBLE_LEFT_AXIS = "Left";

    public static final String MENUCMD_VISIBLE_TOP_AXIS = "Top";

    public static final String MENUCMD_VISIBLE_RIGHT_AXIS = "Right";

    /**
     * The menu command to transform a data object into other type data.
     */
    public static final String MENUCMD_TRANSFORM_DATA = "Transform";

    /**
     * The menu command to split a data object into multiple data objects.
     */
    public static final String MENUCMD_SPLIT_DATA = "Split";

    /**
     * The menu command to merge multiple data objects which was split
     * into the original data object.
     */
    public static final String MENUCMD_MERGE_DATA = "Merge";

    /**
     * The menu command to assign line colors automatically.
     */
    public static final String MENUCMD_ASSIGN_LINE_COLORS= "Assign Line Colors";

    /**
     * The menu command to align all bars in the figure.
     */
    public static final String MENUCMD_ALIGN_BARS = "Align Bars";

    /**
     * The menu command to export data values to file.
     */
    public static final String MENUCMD_EXPORT_TO_FILE = "Export to File";

    public static final String MENUCMD_EXPORT_TO_TEXT_FILE = "Export to Text File";

    public static final String MENUCMD_EXPORT_TO_NETCDF_FILE = "Export to NetCDF File";

    public static final String MENUCMD_EXPORT_TO_HDF5_FILE = "Export to HDF5 File";

    public static final String MENUCMD_EXPORT_TO_MATLAB_FILE = "Export to MATLAB File";

    /**
     * The menu command to reload the data.
     */
    public static final String MENUCMD_RELOAD = "Reload";

    /**
     * The menu command to show data viewer.
     */
    public static final String MENUCMD_SHOW_DATA_VIEWER = "Show Data Viewer";

    /**
     * Look & Feel
     */
    public static final String LAF_METAL = "Metal";

    public static final String LAF_MOTIF = "Motif";

    public static final String LAF_WINDOWS = "Windows";

    public static final String LAF_WINDOWSCLASSIC = "Windows Classic";

    public static final String LAF_AQUA = "Aqua";

    /**
     * Location on a rectangle.
     */
    public static final int OTHER = 0;

    public static final int NORTH = 1;

    public static final int SOUTH = 2;

    public static final int WEST = 3;

    public static final int EAST = 4;

    public static final int NORTH_WEST = 5;

    public static final int NORTH_EAST = 6;

    public static final int SOUTH_WEST = 7;

    public static final int SOUTH_EAST = 8;

    
    public static final int OK_OPTION = 0;

    public static final int CANCEL_OPTION = 1;
    
    public static final int ERROR_OPTION = -1;

    // 
    public static final int MODE_EXPORT_AS_IMAGE = 0;

    public static final int MODE_DISPLAY = 1;

    // paper size
    public static final String PAPER_SIZE_A3 = "A3";

    public static final String PAPER_SIZE_A4 = "A4";

    public static final String PAPER_SIZE_B4 = "B4";

    public static final String PAPER_SIZE_B5 = "B5";

    public static final String PAPER_SIZE_US_LETTER = "US_Letter";

    public static final String PAPER_SIZE_US_LETTER_SIMPLE = "Letter";

    public static final String PORTRAIT = "Portrait";

    public static final String LANDSCAPE = "Landscape";

    /**
     * Line width.
     */
    public static final int LINE_WIDTH_FRAC_DIGIT_MIN = 1;

    public static final int LINE_WIDTH_FRAC_DIGIT_MAX = 2;

    public static final int LINE_WIDTH_MINIMAL_ORDER = - LINE_WIDTH_FRAC_DIGIT_MAX;


    /**
     * Font size.
     */
    public static final int FONT_SIZE_FRAC_DIGIT_MIN = 1;

    public static final int FONT_SIZE_FRAC_DIGIT_MAX = 2;

    public static final int FONT_SIZE_MINIMAL_ORDER = - FONT_SIZE_FRAC_DIGIT_MAX;

    /**
     * Enumeration of operations.
     */
    enum OPERATION {
    	SAVE_TO_PROPERTY_FILE,
    	SAVE_TO_ARCHIVE_DATA_SET,
    	SAVE_TO_ARCHIVE_DATA_SET_107,
    	SAVE_TO_DATA_SET_NETCDF,
    	SAVE_TO_SCRIPT_FILE,
    	SAVE_INTO_FILE_ATTRIBUTE,
    	DUPLICATE_OBJECT,
    	COPY_OBJECT,
    	CUT_OBJECT,
    	EXPORT_TO_FILE_AS_SAME_FORMAT,
    	EXPORT_TO_TEXT,
    	EXPORT_TO_NETCDF,
    	EXPORT_TO_HDF5,
    	EXPORT_TO_MATLAB,
    }

    /**
     * Constants for the type of loading properties.
     */
    public static final int LOAD_PROPERTIES_FROM_PROPERTY_FILE = 1;

    public static final int LOAD_PROPERTIES_FROM_DATA_SET = 2;

    public static final int LOAD_PROPERTIES_IN_DUPLICATION = 3;

    public static final int LOAD_PROPERTIES_IN_PASTING = 4;

    public static final int LOAD_PROPERTIES_IN_NETCDF_ATTRIBUTE = 5;


    public static final String INVALID_INPUT_VALUE = "Input value is invalid.";

    public static final String ERROR = "Error";

    public static final String WARNING = "Warning";

    /**
     * The name of character set.
     */
    public static final String CHAR_SET_NAME_UTF8 = "UTF-8";

    /**
     * Character of the byte order mark.
     */
    public static final char BOM_CHAR = Character.toChars(65279)[0];

	/**
	 * The unit increment of the scroll bar.
	 */
	public static final int SCROLL_BAR_UNIT_INCREMENT = 20;

	/**
	 * The block increment of the scroll bar.
	 */
	public static final int SCROLL_BAR_BLOCK_INCREMENT = 4 * SCROLL_BAR_UNIT_INCREMENT;

	public static final String TITLE_CONFIRMATION = "Confirmation";

	public static final String TITLE_MESSAGE = "Message";

	public static final String TITLE_ERROR = ERROR;

	public static final String TITLE_WARNING = WARNING;

	/**
	 * The effective digit for the axis range.
	 */
	public static final int AXIS_SCALE_EFFECTIVE_DIGIT = 4;

}
