package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import java.awt.Font;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;

/**
 * Constants for axes.
 */
public interface SGIAxisConstants extends SGIConstants {

    /*
     * Constants for the tick marks.
     */
    public static final String TICK_MARK_LENGTH_UNIT = cm;

    public static final double TICK_MARK_LENGTH_MIN = -5.0;

    public static final double TICK_MARK_LENGTH_MAX = 5.0;
    
    public static final double TICK_MARK_LENGTH_STEP = 0.05;

    public static final int TICK_MARK_LENGTH_FRAC_DIFIT_MIN = 1;
    
    public static final int TICK_MARK_LENGTH_FRAC_DIFIT_MAX = 2;
    
    public static final int TICK_MARK_LENGTH_MINIMAL_ORDER = - TICK_MARK_LENGTH_FRAC_DIFIT_MAX;
    
    public static final int MINOR_TICK_MARK_NUMBER_MIN = 0;

    public static final int MINOR_TICK_MARK_NUMBER_MAX = 5;

    public static final int MINOR_TICK_MARK_NUMBER_STEP = 1;
    
    public static final double MINOR_TICK_MARK_LENGTH_MIN = TICK_MARK_LENGTH_MIN;

    public static final double MINOR_TICK_MARK_LENGTH_MAX = TICK_MARK_LENGTH_MAX;

    public static final double MINOR_TICK_MARK_LENGTH_STEP = TICK_MARK_LENGTH_STEP;

    public static final int MINOR_TICK_MARK_LENGTH_FRAC_DIGIT_MIN = 1;
    
    public static final int MINOR_TICK_MARK_LENGTH_FRAC_DIGIT_MAX = 2;

    public static final int MINOR_TICK_MARK_LENGTH_MINIMAL_ORDER = - MINOR_TICK_MARK_LENGTH_FRAC_DIGIT_MAX;

    
    /*
     * Constants for the exponent.
     */
    public static final int AXIS_EXPONENT_MIN = -10;

    public static final int AXIS_EXPONENT_MAX = 10;
    
    public static final int AXIS_EXPONENT_STEP = 1;

    /*
     * Constants for axis shift.
     */
    public static final String AXIS_SHIFT_UNIT = cm;
    
    public static final double AXIS_SHIFT_MIN = -50.0;

    public static final double AXIS_SHIFT_MAX = 50.0;

    public static final double AXIS_SHIFT_STEP = 0.50;
    
    public static final int AXIS_SHIFT_FRAC_DIGIT_MIN = 1;
    
    public static final int AXIS_SHIFT_FRAC_DIGIT_MAX = 2;

    public static final int AXIS_SHIFT_MINIMAL_ORDER = - AXIS_SHIFT_FRAC_DIGIT_MAX;

    /*
     * Constants for title shift.
     */
    public static final String TITLE_SHIFT_UNIT = cm;
    
    public static final double TITLE_SHIFT_MIN = -50.0;

    public static final double TITLE_SHIFT_MAX = 50.0;

    public static final double TITLE_SHIFT_STEP = 0.10;
    
    public static final int TITLE_SHIFT_FRAC_DIGIT_MIN = 1;
    
    public static final int TITLE_SHIFT_FRAC_DIGIT_MAX = 2;

    public static final int TITLE_SHIFT_MINIMAL_ORDER = - TITLE_SHIFT_FRAC_DIGIT_MAX;
    
    /*
     * Constants for spaces.
     */
    public static final String SPACE_UNIT = cm;

    public static final double SPACE_BETWEEN_LINE_AND_NUMBERS_MIN = -50.0;

    public static final double SPACE_BETWEEN_LINE_AND_NUMBERS_MAX = 50.0;

    public static final double SPACE_BETWEEN_TITLE_AND_NUMBERS_MIN = -50.0;

    public static final double SPACE_BETWEEN_TITLE_AND_NUMBERS_MAX = 50.0;

    public static final int SPACE_FRAC_DIGIT_MIN = 1;
    
    public static final int SPACE_FRAC_DIGIT_MAX = 2;

    public static final double SPACE_STEP = 0.10;

	public static final int SPACE_MINIMAL_ORDER = - SPACE_FRAC_DIGIT_MAX;
	
	/*
	 * Constants for the exponent.
	 */
    public static final String EXPONENT_LOCATION_UNIT = cm;
    
    public static final double EXPONENT_LOCATION_MIN = -50.0;

    public static final double EXPONENT_LOCATION_MAX = 50.0;

    public static final double EXPONENT_LOCATION_STEP = 0.10;
    
    public static final int EXPONENT_LOCATION_FRAC_DIGIT_MIN = 1;
    
    public static final int EXPONENT_LOCATION_FRAC_DIGIT_MAX = 2;

    public static final int EXPONENT_LOCATION_MINIMAL_ORDER = - EXPONENT_LOCATION_FRAC_DIGIT_MAX;
	

    //
    // Default values
    //

	// shift
    public static final float DEFAULT_AXIS_SHIFT = 0.0f;

    // line color
    public static final Color DEFAULT_LINE_COLOR = Color.BLACK;
    
    // font name
    public static final String DEFAULT_FONT_NAME = "Serif";

    // font style
    public static final int DEFAULT_FONT_STYLE = Font.PLAIN;

    // font size
    public static final float DEFAULT_FONT_SIZE = 16.0f;

    // font color
    public static final Color DEFAULT_FONT_COLOR = Color.BLACK;

    
    //
    // Axis Visibility
    //

    public static final boolean DEFAULT_BOTTOM_AXIS_VISIBLE = true;

    public static final boolean DEFAULT_TOP_AXIS_VISIBLE = false;

    public static final boolean DEFAULT_LEFT_AXIS_VISIBLE = true;

    public static final boolean DEFAULT_RIGHT_AXIS_VISIBLE = false;

    
    //
    // Frame
    //

    // Visible
    public static final boolean DEFAULT_FRAME_LINE_VISIBLE = false;

    // Width
    public static final float DEFAULT_FRAME_LINE_WIDTH = 1.5f;

    // Color
    public static final Color DEFAULT_FRAME_LINE_COLOR = DEFAULT_LINE_COLOR;

    
    //
    // Axis Line
    //

    // Axis width
    public static final float DEFAULT_AXIS_LINE_WIDTH = 1.5f;

    // space between axis line and numbers
    public static final float DEFAULT_SPACE_AXIS_LINE_AND_NUMBER = 0.20f;

    
    //
    // Title
    //
    
    // title visible
    public static final boolean DEFAULT_TITLE_VISIBLE = true;
    
    public static final String DEFAULT_HORIZONTAL_AXIS_TITLE = "X Label";

    public static final String DEFAULT_VERTICAL_AXIS_TITLE = "Y Label";

    public static final String DEFAULT_NORMAL_AXIS_TITLE = "Z Label";

    public static final String DEFAULT_BOTTOM_AXIS_TITLE = DEFAULT_HORIZONTAL_AXIS_TITLE;

    public static final String DEFAULT_TOP_AXIS_TITLE = DEFAULT_HORIZONTAL_AXIS_TITLE;

    public static final String DEFAULT_LEFT_AXIS_TITLE = DEFAULT_VERTICAL_AXIS_TITLE;

    public static final String DEFAULT_RIGHT_AXIS_TITLE = DEFAULT_VERTICAL_AXIS_TITLE;

    public static final String DEFAULT_Z_AXIS_TITLE = DEFAULT_NORMAL_AXIS_TITLE;
    
    // space between title and numbers
    public static final float DEFAULT_SPACE_NUMBER_AND_TITLE = 0.20f;
    
    // shift from the center
    public static final float DEFAULT_TITLE_SHIFT_FROM_CENTER = 0.0f;

    
    //
    // Scale
    //
    
    public static final double DEFAULT_MIN_VALUE = 0.0;
    
    public static final double DEFAULT_MAX_VALUE = 1.0;

    public static final double DEFAULT_X_MIN_VALUE = DEFAULT_MIN_VALUE;
    
    public static final double DEFAULT_X_MAX_VALUE = DEFAULT_MAX_VALUE;

    public static final double DEFAULT_Y_MIN_VALUE = DEFAULT_MIN_VALUE;
    
    public static final double DEFAULT_Y_MAX_VALUE = DEFAULT_MAX_VALUE;

    public static final double DEFAULT_Z_MIN_VALUE = DEFAULT_MIN_VALUE;
    
    public static final double DEFAULT_Z_MAX_VALUE = DEFAULT_MAX_VALUE;
    
    public static final boolean DEFAULT_AUTO_CALC = true;
    
    
    //
    // Number
    //
    
    public static final boolean DEFAULT_NUMBER_VISIBLE = true;

    public static final boolean DEFAULT_NUMBER_INTEGER = false;
    
    public static final float DEFAULT_NUMBER_ANGLE = 0.0f;
    
    public static final boolean DEFAULT_EXPONENT_VISIBLE = false;
    
    public static final int DEFAULT_EXPONENT = 0;
    
    
    //
    // Tick Mark
    //
    
    public static final boolean DEFAULT_TICK_MARK_VISIBLE = true;
    
    public static final float DEFAULT_TICK_MARK_WIDTH = 1.5f;

    public static final float DEFAULT_TICK_MARK_LENGTH = 0.25f;

    public static final boolean DEFAULT_TICK_MARK_BOTHSIDES = false;
    
    public static final int DEFAULT_MINOR_TICK_MARK_NUMBER = 1;

    public static final float DEFAULT_MINOR_TICK_MARK_LENGTH = DEFAULT_TICK_MARK_LENGTH;

    
    //
    // Commands
    //
    
	public static final String COM_AXIS = "Axis";

    // for figure axis
    public static final String COM_AXIS_VISIBLE = "Visible";
    
    public static final String COM_AXIS_SHIFT = "Shift";
    
    public static final String COM_AXIS_DATE_MODE = "DateMode";
    
    // Axis Line
    public static final String COM_AXIS_LINE_VISIBLE = "AxisLineVisible";
    
    public static final String COM_AXIS_AXIS_LINE_WIDTH = "AxisLineWidth";
    
    public static final String COM_AXIS_AXIS_LINE_COLOR = "AxisLineColor";

    public static final String COM_AXIS_SPACE_AXIS_LINE_AND_NUMBER = "AxisLineNumberSpace";
    
    // Title
    public static final String COM_AXIS_TITLE_VISIBLE = "TitleVisible";

    public static final String COM_AXIS_TITLE_TEXT = "TitleText";

    public static final String COM_AXIS_SPACE_TITLE_AND_NUMBER = "TitleNumberSpace";
    
    public static final String COM_AXIS_TITLE_CENTER_SHIFT = "TitleCenterShift";
    
    public static final String COM_AXIS_TITLE_FONT_NAME = "TitleFontName";

    public static final String COM_AXIS_TITLE_FONT_SIZE = "TitleFontSize";

    public static final String COM_AXIS_TITLE_FONT_STYLE = "TitleFontStyle";

    public static final String COM_AXIS_TITLE_FONT_COLOR = "TitleFontColor";
    
    // Number
    public static final String COM_AXIS_NUMBER_VISIBLE = "NumberVisible";

    public static final String COM_AXIS_NUMBER_INTEGER = "NumberInteger";
    
    public static final String COM_AXIS_NUMBER_ANGLE = "NumberAngle";

    public static final String COM_AXIS_EXPONENT_VISIBLE = "ExponentVisible";

    public static final String COM_AXIS_EXPONENT_VALUE = "ExponentValue";

    public static final String COM_AXIS_EXPONENT_LOCATION_X = "ExponentLocationX";

    public static final String COM_AXIS_EXPONENT_LOCATION_Y = "ExponentLocationY";

    public static final String COM_AXIS_NUMBER_FONT_NAME = "NumberFontName";

    public static final String COM_AXIS_NUMBER_FONT_SIZE = "NumberFontSize";

    public static final String COM_AXIS_NUMBER_FONT_STYLE = "NumberFontStyle";

    public static final String COM_AXIS_NUMBER_FONT_COLOR = "NumberFontColor";
    
    public static final String COM_AXIS_DATE_FORMAT = "DateFormat";
    
    // Scale
    public static final String COM_AXIS_SCALE_MIN_VALUE = "ScaleMinValue";

    public static final String COM_AXIS_SCALE_MAX_VALUE = "ScaleMaxValue";

    public static final String COM_AXIS_SCALE_RANGE = "ScaleRange";

    public static final String COM_AXIS_SCALE_TYPE = "ScaleType";

    public static final String COM_AXIS_INVERT_COORDINATES = "InvertCoordinates";
    
    public static final String COM_AXIS_SCALE_AUTO = "ScaleAuto";

    public static final String COM_AXIS_SCALE_STEP = "ScaleStep";

    public static final String COM_AXIS_SCALE_BASE = "ScaleBase";

    // Tick Mark
    public static final String COM_AXIS_TICK_MARK_VISIBLE = "TickMarkVisible";

    public static final String COM_AXIS_TICK_MARK_BOTHSIDES = "TickMarkBothsides";

    public static final String COM_AXIS_TICK_MARK_WIDTH = "TickMarkWidth";

    public static final String COM_AXIS_MAJOR_TICK_MARK_LENGTH = "MajorTickMarkLength";

    public static final String COM_AXIS_MINOR_TICK_MARK_NUMBER = "MinorTickMarkNumber";
    
    public static final String COM_AXIS_MINOR_TICK_MARK_LENGTH = "MinorTickMarkLength";
    
    public static final String COM_AXIS_TICK_MARK_COLOR = "TickMarkColor";

    
    //
    // for backward compatibility (<= 2.0.0)
    //
    
    // common to the title and numbers
    public static final String COM_AXIS_FONT_NAME = "FontName";

    public static final String COM_AXIS_FONT_STYLE = "FontStyle";

    public static final String COM_AXIS_FONT_SIZE = "FontSize";

    public static final String COM_AXIS_FONT_COLOR = "FontColor";

    // common to major and minor tick marks
    public static final String COM_AXIS_TICK_MARK_LENGTH = "TickMarkLength";

    // older version of TickMarkDirection
    public static final String COM_AXIS_TICK_MARK_INNER = "TickMarkInner";

    // renamed commands
    public static final String COM_AXIS_SPACE_TO_SCALE = "SpaceToScale";

    public static final String COM_AXIS_SPACE_TO_TITLE = "SpaceToTitle";

    public static final String COM_AXIS_LINE_WIDTH = "LineWidth";

    public static final String COM_AXIS_LINE_COLOR = "LineColor";

    public static final String COM_AXIS_TICK_MARK_AUTO = "TickMarkAuto";

    public static final String COM_AXIS_TICK_MARK_STEP = "TickMarkStep";

    public static final String COM_AXIS_TICK_MARK_BASE = "TickMarkBase";

    public static final String COM_AXIS_NUMBER_FORMAT_EXPONENT_VISIBLE = "NumberFormatExponentVisible";

    public static final String COM_AXIS_NUMBER_FORMAT_EXPONTNE_VALUE = "NumberFormatExponentValue";

    // frame
    public static final String COM_AXIS_FRAME_VISIBLE = "FrameVisible";

    public static final String COM_AXIS_FRAME_LINE_WIDTH = "FrameLineWidth";

    public static final String COM_AXIS_FRAME_COLOR = "FrameColor";

}
