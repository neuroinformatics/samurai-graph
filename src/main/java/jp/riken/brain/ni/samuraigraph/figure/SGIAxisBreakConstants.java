package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;

/**
 * Constants for axis break symbols.
 */
public interface SGIAxisBreakConstants extends SGIDrawingElementConstants {

    //
    // Axis Break Length
    //
    
    public static final String AXIS_BREAK_LENGTH_UNIT = cm;

    public static final double AXIS_BREAK_LENGTH_MIN = 0.50;

    public static final double AXIS_BREAK_LENGTH_MAX = 2.0;

    public static final double AXIS_BREAK_LENGTH_STEP = 0.10;

    public static final int AXIS_BREAK_LENGTH_FRAC_DIFIT_MIN = 1;
    
    public static final int AXIS_BREAK_LENGTH_FRAC_DIFIT_MAX = 2;
    
    public static final int AXIS_BREAK_LENGTH_MINIMAL_ORDER = - AXIS_BREAK_LENGTH_FRAC_DIFIT_MAX;

    
    //
    // Axis Break Interval
    //
    
    public static final String AXIS_BREAK_INTERVAL_UNIT = cm;

    public static final double AXIS_BREAK_INTERVAL_MIN = 0.10;

    public static final double AXIS_BREAK_INTERVAL_MAX = 2.0;

    public static final double AXIS_BREAK_INTERVAL_STEP = 0.10;
    
    public static final int AXIS_BREAK_INTERVAL_FRAC_DIFIT_MIN = 1;
    
    public static final int AXIS_BREAK_INTERVAL_FRAC_DIFIT_MAX = 2;
    
    public static final int AXIS_BREAK_INTERVAL_MINIMAL_ORDER = - AXIS_BREAK_INTERVAL_FRAC_DIFIT_MAX;

    
    //
    // Axis Break Distortion
    //

    public static final double AXIS_BREAK_DISTORTION_MIN = -1.0;

    public static final double AXIS_BREAK_DISTORTION_MAX = 1.0;

    public static final double AXIS_BREAK_DISTORTION_STEP = 0.10;

    public static final int AXIS_BREAK_DISTORTION_FRAC_DIFIT_MIN = 1;
    
    public static final int AXIS_BREAK_DISTORTION_FRAC_DIFIT_MAX = 2;
    
    public static final int AXIS_BREAK_DISTORTION_MINIMAL_ORDER = - AXIS_BREAK_DISTORTION_FRAC_DIFIT_MAX;

    
    //
    // Axis Break Angle
    //
    
    public static final double ANGLE_ABS_MAX = 60.0;
    
    public static final double AXIS_BREAK_ANGLE_MAX = ANGLE_ABS_MAX;

    public static final double AXIS_BREAK_ANGLE_MIN = -AXIS_BREAK_ANGLE_MAX;
    
    public static final double AXIS_BREAK_ANGLE_STEP = 1.0;
   
    public static final int AXIS_BREAK_ANGLE_FRAC_DIFIT_MIN = 1;
    
    public static final int AXIS_BREAK_ANGLE_FRAC_DIFIT_MAX = 2;
    
    public static final int AXIS_BREAK_ANGLE_MINIMAL_ORDER = - AXIS_BREAK_ANGLE_FRAC_DIFIT_MAX;
    

    //
    // Keys
    //
    
    public static final String TAG_NAME_AXIS_BREAK_SYMBOL = "AxisBreakSymbol";

    public static final String KEY_X = "X";

    public static final String KEY_Y = "Y";

    public static final String KEY_LENGTH = "Length";

    public static final String KEY_INTERVAL = "Interval";

    public static final String KEY_DISTORTION = "Distortion";

    public static final String KEY_ANGLE = "Angle";

    public static final String KEY_HORIZONTAL = "Horizontal";

    public static final String KEY_LINE_WIDTH = "LineWidth";

    public static final String KEY_INNER_COLOR = "InnerColor";

    public static final String KEY_LINE_COLOR = "LineColor";

    public static final String KEY_X_VALUE = "XValue";

    public static final String KEY_Y_VALUE = "YValue";
    
    public static final String KEY_ANCHORED = "Anchored";

    
    //
    // Default Values
    //

    // Length
    public static final float DEFAULT_AXIS_BREAK_LENGTH = 1.0f;

    // Interval
    public static final float DEFAULT_AXIS_BREAK_INTERVAL = 0.50f;

    // Line Width
    public static final float DEFAULT_AXIS_BREAK_LINE_WIDTH = 1.5f;

    // Angle
    public static final float DEFAULT_AXIS_BREAK_ANGLE = 0.0f;

    // Distortion
    public static final float DEFAULT_AXIS_BREAK_DISTORTION = 0.50f;

    // Horizontal
    public static final boolean DEFAULT_AXIS_BREAK_FOR_HORIZONTAL = true;

    // Line Color
    public static final Color DEFAULT_AXIS_BREAK_LINE_COLOR = Color.BLACK;

    // Inner Color
    public static final Color DEFAULT_AXIS_BREAK_INNER_COLOR = Color.WHITE;

    // Horizontal Axis
    public static final String DEFAULT_AXIS_BREAK_HORIZONTAL_AXIS = SGIFigureElementAxis.AXIS_BOTTOM;

    // Vertical Axis
    public static final String DEFAULT_AXIS_BREAK_VERTICAL_AXIS = SGIFigureElementAxis.AXIS_LEFT;

	public static final String COM_AXIS_BREAK = "AxisBreak";
    
    public static final String COM_BREAK_AXIS_X = "AxisX";

    public static final String COM_BREAK_AXIS_Y = "AxisY";

    public static final String COM_BREAK_LOCATION_X = "X";

    public static final String COM_BREAK_LOCATION_Y = "Y";

    public static final String COM_BREAK_LENGTH = "Length";

    public static final String COM_BREAK_INTERVAL = "Interval";

    public static final String COM_BREAK_DISTORTION = "Distortion";

    public static final String COM_BREAK_ANGLE = "Angle";

    public static final String COM_BREAK_LINE_WIDTH = "LineWidth";

    public static final String COM_BREAK_HORIZONTAL = "Horizontal";

    public static final String COM_BREAK_LINE_COLOR = "LineColor";

    public static final String COM_BREAK_INNER_COLOR = "InnerColor";

    public static final String COM_BREAK_ANCHORED = "Anchored";

}
