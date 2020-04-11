package jp.riken.brain.ni.samuraigraph.base;

/**
 * Constants for drawing elements.
 */
public interface SGIDrawingElementConstants extends SGIConstants {

    // symbol type of graph elements
    public static final int SYMBOL_ELEMENT_TYPE_STRING = 0;

    public static final int SYMBOL_ELEMENT_TYPE_AXISBREAK = 1;

    public static final int SYMBOL_ELEMENT_TYPE_SIGDIFF = 2;

    public static final int SYMBOL_ELEMENT_TYPE_TIMINGLINE = 3;

    public static final int SYMBOL_ELEMENT_TYPE_SHAPE = 4;

    // keys
    public static final String KEY_VISIBLE = "Visible";

    public static final String KEY_COLOR = "Color";

    public static final String KEY_DRAWING_ELEMENT_COLORS = "Color";

    // line width
    public static final double LINE_WIDTH_MIN_VALUE = 0.25;

    public static final double LINE_WIDTH_MAX_VALUE = 6.0;

    public static final double LINE_WIDTH_STEP_SIZE = 0.25;

    // font size
    public static final double FONT_SIZE_MIN_VALUE = 6.0;

    public static final double FONT_SIZE_MAX_VALUE = 96.0;

    public static final double FONT_SIZE_STEP_VALUE = 1.0;

    // font style
    public static final String FONT_PLAIN = "Plain";

    public static final String FONT_ITALIC = "Italic";

    public static final String FONT_BOLD = "Bold";

    public static final String FONT_BOLD_ITALIC = "Bold Italic";
}
