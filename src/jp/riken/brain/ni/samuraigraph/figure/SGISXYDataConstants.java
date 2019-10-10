package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import java.awt.Font;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGTransparentPaint;

/**
 * Constants for the two-dimensional scalar type data.
 */
public interface SGISXYDataConstants extends SGIConstants {
	
	enum ELEMENT_TYPE {
		Line,
		Symbol,
		Bar,
		ErrorBar,
		TickLabel,
		Void,
	};

    public static final String KEY_SHIFT_X = "ShiftX";
    
    public static final String KEY_SHIFT_Y = "ShiftY";

    //
    // Line
    //

    // Visible
    public static final boolean DEFAULT_LINE_VISIBLE = true;

    // Width
    public static final float DEFAULT_LINE_WIDTH = 1.0f;

    // Type
    public static final int DEFAULT_LINE_TYPE = SGILineConstants.LINE_TYPE_SOLID;

    // Color
    public static final Color DEFAULT_LINE_COLOR = Color.BLACK;

    // Shift X
    public static final double DEFAULT_LINE_SHIFT_X = 0.0;
    
    // Shift Y
    public static final double DEFAULT_LINE_SHIFT_Y = 0.0;
    
    //
    // Symbol
    //

    // Visible
    public static final boolean DEFAULT_SYMBOL_VISIBLE = false;

    // Type
    public static final int DEFAULT_SYMBOL_TYPE = SGISymbolConstants.SYMBOL_TYPE_CIRCLE;

    // Body Size
    public static final float DEFAULT_SYMBOL_SIZE = 0.3f;

    // Body Color
    public static final Color DEFAULT_SYMBOL_BODY_COLOR = Color.WHITE;
    
    // Body Transparency
    public static final int DEFAULT_SYMBOL_BODY_TRANSPARENCY = SGTransparentPaint.ALL_OPAQUE_VALUE;

    // Line Width
    public static final float DEFAULT_SYMBOL_LINE_WIDTH = 1.0f;

    // Line Color
    public static final Color DEFAULT_SYMBOL_LINE_COLOR = Color.BLACK;
    
    // Line Visible
    public static final boolean DEFAULT_SYMBOL_LINE_VISIBLE = true;

    //
    // Bar
    //

    // Visible
    public static final boolean DEFAULT_BAR_VISIBLE = false;

    // Baseline
    public static final double DEFAULT_BAR_BASELINE_VALUE = 0.0;

    // Body Width
    public static final float DEFAULT_BAR_WIDTH = 0.5f;

    // Body Color
    public static final Color DEFAULT_BAR_COLOR = Color.WHITE;

    // Edge Line Width
    public static final float DEFAULT_BAR_EDGE_LINE_WIDTH = 0.5f;

    // Edge Line Color
    public static final Color DEFAULT_BAR_EDGE_LINE_COLOR = Color.BLACK;
    
    // Edge Line Visible
    public static final boolean DEFAULT_BAR_EDGE_LINE_VISIBLE = true;

    // Shift X
    public static final double DEFAULT_BAR_SHIFT_X = 0.0;
    
    // Shift Y
    public static final double DEFAULT_BAR_SHIFT_Y = 0.0;
    
    // Interval
    public static final double DEFAULT_BAR_INTERVAL = 0.0;
    
    //
    // Error Bar
    //

    // Visible
    public static final boolean DEFAULT_ERROR_BAR_VISIBLE = true;

    // Symbol Type
    public static final int DEFAULT_ERROR_BAR_SYMBOL_TYPE = SGIErrorBarConstants.SYMBOL_TYPE_TRANSVERSELINE;

    // Symbol Color
    public static final Color DEFAULT_ERROR_BAR_COLOR = Color.BLACK;

    // Symbol Size
    public static final float DEFAULT_ERROR_BAR_SYMBOL_SIZE = 0.25f;

    // Line Width
    public static final float DEFAULT_ERROR_BAR_LINE_WIDTH = 1.0f;

    // Style
    public static final int DEFAULT_ERROR_BAR_STYLE = SGIErrorBarConstants.ERROR_BAR_BOTHSIDES;

    // Error Bars Position
    public static final boolean DEFAULT_ERROR_BAR_POSITION_IS_LINE = true;
    
    //
    // Tick Label
    //

    // Visible
    public static final boolean DEFAULT_TICK_LABEL_VISIBLE = true;

    // Font
    public static final String DEFAULT_TICK_LABEL_FONT_NAME = "Serif";

    public static final int DEFAULT_TICK_LABEL_FONT_STYLE = Font.PLAIN;

    // Size
    public static final float DEFAULT_TICK_LABEL_FONT_SIZE = 16.0f;

    // Color
    public static final Color DEFAULT_TICK_LABEL_FONT_COLOR = Color.BLACK;

    // Angle
    public static final float DEFAULT_TICK_LABEL_ANGLE = 0.0f;

    // Tick label Position
    public static final boolean DEFAULT_TICK_LABEL_POSITION_IS_LINE = true;

}
