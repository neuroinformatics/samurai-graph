package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import java.awt.Font;

import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;

public interface SGILineAndStringConstants extends SGIDrawingElementConstants, SGIStringConstants {

    //
    // Space between the horizontal line and the symbol
    //
    
    public static final String SPACE_UNIT = cm;

    public static final double SPACE_MIN = -1.0;

    public static final double SPACE_MAX = 1.0;
    
    public static final double SPACE_STEP = 0.050;

    public static final int SPACE_FRAC_DIFIT_MIN = 1;
    
    public static final int SPACE_FRAC_DIFIT_MAX = 2;
    
    public static final int SPACE_MINIMAL_ORDER = - SPACE_FRAC_DIFIT_MAX;

    //
    // Keys
    //
    
    public static final String KEY_LINE_WIDTH = "LineWidth";

    public static final String KEY_SPACE = "Space";
    
    
    //
    // Default Values
    //

    // Line Width
    public static final float DEFAULT_LINE_WIDTH = 1.0f;

    // Space
    public static final float DEFAULT_SPACE = 0.20f;

    // Horizontal Axis
    public static final String DEFAULT_HORIZONTAL_AXIS = SGIFigureElementAxis.AXIS_BOTTOM;

    // Vertical Axis
    public static final String DEFAULT_VERTICAL_AXIS = SGIFigureElementAxis.AXIS_LEFT;

    // Font
    public static final String DEFAULT_FONT_NAME = "Serif";

    public static final int DEFAULT_FONT_STYLE = Font.PLAIN;

    // Color
    public static final Color DEFAULT_COLOR = Color.BLACK;
    
    
    public static final String COM_SPACE = "Space";

    public static final String COM_LINE_WIDTH = "LineWidth";

    public static final String COM_FONT_NAME = "FontName";

    public static final String COM_FONT_STYLE = "FontStyle";

    public static final String COM_FONT_SIZE = "FontSize";

}
