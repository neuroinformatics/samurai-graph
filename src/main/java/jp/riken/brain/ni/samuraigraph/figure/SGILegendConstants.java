package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import java.awt.Font;

import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGTransparentPaint;

/**
 * Constants for the legend.
 */
public interface SGILegendConstants extends SGIConstants {

    /**
     * Constants for the symbol span.
     */
    public static final String SYMBOL_SPAN_UNIT = cm;

    public static final double SYMBOL_SPAN_MIN = 0.50;

    public static final double SYMBOL_SPAN_MAX = 6.0;

    public static final double SYMBOL_SPAN_STEP = 0.10;
    
    public static final int SYMBOL_SPAN_FRAC_DIFIT_MIN = 1;
    
    public static final int SYMBOL_SPAN_FRAC_DIFIT_MAX = 2;
    
    public static final int SYMBOL_SPAN_MINIMAL_ORDER = - SYMBOL_SPAN_FRAC_DIFIT_MAX;

    
    //
    // Default Values
    //

    // Scale Reference
    public static final String DEFAULT_LEGEND_SCALE_REFERENCE = SGIFigureElementAxis.LEFT_BOTTOM;

    // Visible
    public static final boolean DEFAULT_LEGEND_VISIBLE = true;

    // Transparency percentage
    public static final int DEFAULT_LEGEND_BACKGROUND_TRANSPARENCY = SGTransparentPaint.ALL_OPAQUE_VALUE;

    //
    // Font
    //

    public static final String DEFAULT_LEGEND_FONT_NAME = "Serif";

    public static final int DEFAULT_LEGEND_FONT_STYLE = Font.PLAIN;

    public static final float DEFAULT_LEGEND_FONT_SIZE = 16.0f;

    public static final Color DEFAULT_LEGEND_FONT_COLOR = Color.BLACK;

    //
    // Frame
    //

    // Visible
    public static final boolean DEFAULT_LEGEND_FRAME_VISIBLE = true;

    // Width
    public static final float DEFAULT_LEGEND_FRAME_WIDTH = 0.5f;

    // Color
    public static final Color DEFAULT_LEGEND_FRAME_COLOR = Color.BLACK;

    // BG Color
    public static final Color DEFAULT_LEGEND_BACKGROUND_COLOR = Color.WHITE;

    //
    // Axes
    //

    // Horizontal Axis
    public static final String DEFAULT_LEGEND_HORIZONTAL_AXIS = SGIFigureElementAxis.AXIS_BOTTOM;

    // Vertical Axis
    public static final String DEFAULT_LEGEND_VERTICAL_AXIS = SGIFigureElementAxis.AXIS_LEFT;

    //
    // Symbol span
    //

    public static final float DEFAULT_LEGEND_SYMBOL_SPAN = 2.0f;
    
    //
    // Menu Commands
    //
    
    public static final String MENUCMD_MOVE_TO_TOP = "Move to Top";
    
    public static final String MENUCMD_MOVE_TO_UPPER = "Move to Upper";
    
    public static final String MENUCMD_MOVE_TO_LOWER = "Move to Lower";
    
    public static final String MENUCMD_MOVE_TO_BOTTOM = "Move to Bottom";
    
	public static final String COM_LEGEND = "Legend";
    
    public static final String COM_LEGEND_AXIS_X = "AxisX";

    public static final String COM_LEGEND_AXIS_Y = "AxisY";

    public static final String COM_LEGEND_LOCATION_X = "X";

    public static final String COM_LEGEND_LOCATION_Y = "Y";

    public static final String COM_LEGEND_VISIBLE = "Visible";

    public static final String COM_LEGEND_FONT_NAME = "FontName";

    public static final String COM_LEGEND_FONT_STYLE = "FontStyle";

    public static final String COM_LEGEND_FONT_SIZE = "FontSize";

    public static final String COM_LEGEND_FONT_COLOR = "FontColor";

    public static final String COM_LEGEND_FRAME_VISIBLE = "FrameVisible";

    public static final String COM_LEGEND_FRAME_LINE_WIDTH = "FrameLineWidth";

    public static final String COM_LEGEND_FRAME_COLOR = "FrameColor";

    public static final String COM_LEGEND_BACKGROUND_TRANSPARENT = "BackgroundTransparent";
    
    public static final String COM_LEGEND_BACKGROUND_TRANSPARENCY = "BackgroundTransparency";

    public static final String COM_LEGEND_BACKGROUND_COLOR = "BackgroundColor";

    public static final String COM_LEGEND_SYMBOL_SPAN = "SymbolSpan";

}
