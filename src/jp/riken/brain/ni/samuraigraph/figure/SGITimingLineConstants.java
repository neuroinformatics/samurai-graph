package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

/**
 * Constants for timing lines.
 */
public interface SGITimingLineConstants extends SGILineConstants {

    //
    // Keys
    //
    
    public static final String TAG_NAME_TIMING_LINE = "TimingLine";
    
    public static final String KEY_VALUE = "Value";
    
    public static final String KEY_ANCHORED = "Anchored";

    
    //
    // Default values
    //

    // Type
    public static final int DEFAULT_LINE_TYPE = LINE_TYPE_SOLID;

    // Width
    public static final float DEFAULT_LINE_WIDTH = 1.0f;

    // Color
    public static final Color DEFAULT_LINE_COLOR = Color.BLACK;

    // Anchored
    public static final boolean DEFAULT_LINE_ANCHORED = false;
    
	public static final String COM_TIMING_LINE = "TimingLine";
	
    public static final String COM_TIMING_LINE_AXIS = "Axis";
    
    public static final String COM_TIMING_LINE_VALUE = "Value";

    public static final String COM_TIMING_LINE_WIDTH = "LineWidth";

    public static final String COM_TIMING_LINE_TYPE = "LineType";

    public static final String COM_TIMING_LINE_COLOR = "Color";

    public static final String COM_TIMING_LINE_ANCHORED = "Anchored";

}
