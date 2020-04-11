package jp.riken.brain.ni.samuraigraph.base;

public interface SGIFigureElementAxisConstants extends SGIFigureElementConstants {

    /**
     * Constants for axis location.
     */
    public static final int AXIS_HORIZONTAL_1 = 0;

    public static final int AXIS_HORIZONTAL_2 = 1;

    public static final int AXIS_VERTICAL_1 = 2;

    public static final int AXIS_VERTICAL_2 = 3;
    
    public static final int AXIS_NORMAL = 4;
    
    public static final int AXIS_NORMAL_HORIZONTAL_LOWER = 10;  // horizontal lower
    
    public static final int AXIS_NORMAL_HORIZONTAL_UPPER = 11;  // horizontal upper
    
    public static final int AXIS_NORMAL_VERTICAL_LEFT = 12;  // vertical left

    public static final int AXIS_NORMAL_VERTICAL_RIGHT = 13;  // vertical right
    
    /**
     * Constants for axis direction.
     */
    public static final int AXIS_DIRECTION_HORIZONTAL = 0;

    public static final int AXIS_DIRECTION_VERTICAL = 1;

    public static final int AXIS_DIRECTION_NORMAL = 2;

    public static final String AXIS_DIRECTION_NAME_HORIZONTAL = "Horizontal";

    public static final String AXIS_DIRECTION_NAME_VERTICAL = "Vertical";

    public static final String AXIS_DIRECTION_NAME_COLOR_BAR = "ColorBar";

    /**
     * Brief description of axis location
     */
    public static final String AXIS_TOP = "Top";

    public static final String AXIS_BOTTOM = "Bottom";

    public static final String AXIS_LEFT = "Left";

    public static final String AXIS_RIGHT = "Right";
    
    public static final String AXIS_COLOR_BAR = "ColorBar";

    /**
     * The table connecting a number code and a brief description of each axis location.
     */
    public static final String[] AXIS_LOCATION_TABLE = {
        AXIS_BOTTOM, AXIS_TOP, AXIS_LEFT, AXIS_RIGHT, AXIS_COLOR_BAR
    };
    
    /**
     * Brief description of axis pairs
     */
    public static final String LEFT_TOP = "Left-Top";

    public static final String LEFT_BOTTOM = "Left-Bottom";

    public static final String RIGHT_TOP = "Right-Top";

    public static final String RIGHT_BOTTOM = "Right-Bottom";

    /**
     * Command to show or hide an axis
     */
    public static final String MENUCMD_SHOW_AXIS = "Show Axis";

    /**
     * Command to draw image later
     */
    public static final String MENUCMD_DRAW_LATER = "Draw Later";

    /**
     * Command to hide the color bar
     */
    public static final String MENUCMD_HIDE_COLOR_BAR = "Hide";

    /**
     * Command to hide the axis scale symbol
     */
    public static final String MENUCMD_HIDE_SCALE = "Hide";

    /**
     * Commands for the axis properties.
     */
    public static final String MENUCMD_SHOW_PROPERTIES_SELECTED_AXES = "Selected Axes";

    public static final String MENUCMD_SHOW_PROPERTIES_ALL_VISIBLE_AXES = "All Visible Axes";

    public static final String MENUCMD_SHOW_PROPERTIES_ALL_AXES = "All Axes";

    public static final String MENUCMD_HIDE_AXES = "Hide Axes";

    //
    // Constants for properties.
    //
    
    public static final String TAG_NAME_AXES = "Axes";
    
    public static final String TAG_NAME_AXIS = "Axis";

    public static final String KEY_POSITION = "Position";

    public static final String KEY_SHIFT = "Shift";
    
    public static final String KEY_DATE_MODE = "DateMode";
    
    // Axis Line
    public static final String KEY_AXIS_LINE_VISIBLE = "AxisLineVisible";
    
    public static final String KEY_AXIS_LINE_WIDTH = "AxisLineWidth";
    
    public static final String KEY_AXIS_LINE_COLOR = "AxisLineColor";

    public static final String KEY_SPACE_AXIS_LINE_AND_NUMBERS = "SpaceAxisLineAndNumber";

    // Title
    public static final String KEY_TITLE_VISIBLE = "TitleVisible";
    
    public static final String KEY_TITLE_TEXT = "Title";

    public static final String KEY_SPACE_TITLE_AND_NUMBERS = "SpaceNumberAndTitle";

    public static final String KEY_TITLE_SHIFT_FROM_CENTER = "TitleShiftFromCenter";

    public static final String KEY_TITLE_FONT_NAME = "TitleFontName";
    
    public static final String KEY_TITLE_FONT_SIZE = "TitleFontSize";
    
    public static final String KEY_TITLE_FONT_STYLE = "TitleFontStyle";
    
    public static final String KEY_TITLE_FONT_COLOR = "TitleFontColor";

    // Number
    public static final String KEY_NUMBER_VISIBLE = "NumberVisible";

    public static final String KEY_NUMBER_INTEGER = "NumberInteger";

    public static final String KEY_NUMBER_ANGLE = "NumberAngle";
    
    public static final String KEY_EXPONENT_VISIBLE = "Exponent";

    public static final String KEY_EXPONENT_VALUE = "ExponentValue";
    
    public static final String KEY_EXPONENT_LOCATION_X = "ExponentLocationX";
    
    public static final String KEY_EXPONENT_LOCATION_Y = "ExponentLocationY";
    
    public static final String KEY_NUMBER_FONT_NAME = "NumberFontName";
    
    public static final String KEY_NUMBER_FONT_SIZE = "NumberFontSize";
    
    public static final String KEY_NUMBER_FONT_STYLE = "NumberFontStyle";
    
    public static final String KEY_NUMBER_FONT_COLOR = "NumberFontColor";

    public static final String KEY_NUMBER_DATE_FORMAT = "DateFormat";

    // Scale
    public static final String KEY_AXIS_MIN_VALUE = "MinValue";

    public static final String KEY_AXIS_MAX_VALUE = "MaxValue";

    public static final String KEY_AXIS_SCALE_TYPE = "ScaleType";

    public static final String KEY_AXIS_INVERT_COORDINATES = "InvertCoordinates";

    public static final String KEY_AUTO_CALC_NUMBER = "AutomaticCalculationOfTick";
    
    public static final String KEY_STEP_VALUE = "StepValue";

    public static final String KEY_BASELINE_VALUE = "BaselineValue";

    // Tick Mark
    public static final String KEY_TICK_MARK_VISIBLE = "TickMarkVisible";

    public static final String KEY_TICK_MARK_BOTHSIDES = "TickMarkBothsides";

    public static final String KEY_TICK_MARK_WIDTH = "TickMarkWidth";

    public static final String KEY_MAJOR_TICK_MARK_LENGTH = "MajorTickMarkLength";

    public static final String KEY_MINOR_TICK_MARK_NUMBER = "MinorTickMarkNumber";

    public static final String KEY_MINOR_TICK_MARK_LENGTH = "MinorTickMarkLength";

    public static final String KEY_TICK_MARK_COLOR = "TickMarkColor";

    // for backward compatibility
    public static final String KEY_TICK_MARK_LENGTH = "TickMarkLength";
    
    public static final String KEY_TICK_MARK_INSIDE = "TickMarkInside";

    public static final String KEY_LINE_COLOR = "LineColor";
    
    public static final String KEY_FRAME_LINE_VISIBLE = "FrameLineVisible";

    public static final String KEY_FRAME_LINE_WIDTH = "FrameLineWidth";

    public static final String KEY_FRAME_LINE_COLOR = "FrameLineColor";
}
