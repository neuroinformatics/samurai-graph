package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

/**
 * Constants for significant difference symbols.
 */
public interface SGISignificantDifferenceConstants extends SGILineAndStringConstants {

    //
    // Code of locations
    //
    
    public static final int BODY = 0;

    public static final int HORIZONTAL_MIDDLE = 1;

    public static final int LEFT_MIDDLE = 2;

    public static final int RIGHT_MIDDLE = 3;

    public static final int LEFT_TERM = 4;

    public static final int LEFT_JOINT = 5;

    public static final int RIGHT_TERM = 6;

    public static final int RIGHT_JOINT = 7;

    public static final int ON_STRING = 8;

    //
    // Space between the horizontal line and the symbol
    //
    
    public static final String SIGDIFF_SPACE_UNIT = SPACE_UNIT;

    public static final double SIGDIFF_SPACE_MIN = SPACE_MIN;

    public static final double SIGDIFF_SPACE_MAX = SPACE_MAX;
    
    public static final double SIGDIFF_SPACE_STEP = SPACE_STEP;

    public static final int SIGDIFF_SPACE_FRAC_DIFIT_MIN = SPACE_FRAC_DIFIT_MIN;
    
    public static final int SIGDIFF_SPACE_FRAC_DIFIT_MAX = SPACE_FRAC_DIFIT_MAX;
    
    public static final int SIGDIFF_SPACE_MINIMAL_ORDER = SPACE_MINIMAL_ORDER;

    //
    // Keys
    //
    
    public static final String TAG_NAME_SIGDIFF_SYMBOL = "SignificantDifferenceSymbol";

    public static final String KEY_SIGDIFF_LEFT_X_VALUE = "LeftX";

    public static final String KEY_SIGDIFF_LEFT_Y_VALUE = "LeftY";

    public static final String KEY_SIGDIFF_RIGHT_X_VALUE = "RightX";

    public static final String KEY_SIGDIFF_RIGHT_Y_VALUE = "RightY";

    public static final String KEY_SIGDIFF_HORIZONTAL_Y_VALUE = "HorizontalY";

    public static final String KEY_SIGDIFF_LINE_VISIBLE = "LineVisible";
    
    public static final String KEY_SIGDIFF_TEXT = "Text";

    public static final String KEY_SIGDIFF_ANCHORED = "Anchored";

    //
    // Default Values
    //

    // Width
    public static final float DEFAULT_SIGDIFF_SYMBOL_WIDTH = 1.0f;

    // Left Height
    public static final float DEFAULT_SIGDIFF_SYMBOL_LEFT_HEIGHT = 1.0f;

    // Right Height
    public static final float DEFAULT_SIGDIFF_SYMBOL_RIGHT_HEIGHT = 1.0f;

    // Line Width
    public static final float DEFAULT_SIGDIFF_SYMBOL_LINE_WIDTH = DEFAULT_LINE_WIDTH;

    // Space
    public static final float DEFAULT_SIGDIFF_SYMBOL_SPACE = DEFAULT_SPACE;

    // Horizontal Axis
    public static final String DEFAULT_SIGDIFF_HORIZONTAL_AXIS = DEFAULT_HORIZONTAL_AXIS;

    // Vertical Axis
    public static final String DEFAULT_SIGDIFF_VERTICAL_AXIS = DEFAULT_VERTICAL_AXIS;

    // Font
    public static final String DEFAULT_SIGDIFF_SYMBOL_FONT_NAME = DEFAULT_FONT_NAME;

    public static final int DEFAULT_SIGDIFF_SYMBOL_FONT_STYLE = DEFAULT_FONT_STYLE;

    public static final float DEFAULT_SIGDIFF_SYMBOL_FONT_SIZE = 24.0f;

    // Color
    public static final Color DEFAULT_SIGDIFF_SYMBOL_COLOR = DEFAULT_COLOR;
    
    // Anchored
    public static final boolean DEFAULT_SIGDIFF_SYMBOL_ANCHORED = false;

    
	public static final String COM_SIGNIFICANT_DIFFERENCE = "SignificantDifference";
    
    public static final String COM_SIGDIFF_AXIS_X = "AxisX";

    public static final String COM_SIGDIFF_AXIS_Y = "AxisY";

    public static final String COM_SIGDIFF_LOCATION_LEFT_X = "LeftX";

    public static final String COM_SIGDIFF_LOCATION_LEFT_Y = "LeftY";

    public static final String COM_SIGDIFF_LOCATION_RIGHT_X = "RightX";

    public static final String COM_SIGDIFF_LOCATION_RIGHT_Y = "RightY";

    public static final String COM_SIGDIFF_LOCATION_HORIZONTAL_Y = "HorizontalY";

    public static final String COM_SIGDIFF_TEXT = "Text";

    public static final String COM_SIGDIFF_LINE_VISIBLE = "LineVisible";
    
    public static final String COM_SIGDIFF_ANCHORED = "Anchored";

    public static final String COM_SIGDIFF_COLOR = "Color";

}
