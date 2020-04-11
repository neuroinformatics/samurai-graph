package jp.riken.brain.ni.samuraigraph.figure;

import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;

/**
 * Constants for drawing elements in figure.
 */
public interface SGIFigureDrawingElementConstants extends
        SGIDrawingElementConstants {

    // line
    public static final double LINE_WIDTH_MIN = LINE_WIDTH_MIN_VALUE;

    public static final double LINE_WIDTH_MAX = LINE_WIDTH_MAX_VALUE;

    // symbol
    public static final String SYMBOL_SIZE_UNIT = cm;

    public static final double SYMBOL_SIZE_MIN = 0.05;

    public static final double SYMBOL_SIZE_MAX = 2.0;

    public static final double SYMBOL_LINE_WIDTH_MIN = LINE_WIDTH_MIN_VALUE;

    public static final double SYMBOL_LINE_WIDTH_MAX = LINE_WIDTH_MAX_VALUE;

    public static final double SYMBOL_SIZE_STEP = 0.050;

    public static final int SYMBOL_SIZE_FRAC_DIFIT_MIN = 1;
    
    public static final int SYMBOL_SIZE_FRAC_DIFIT_MAX = 2;
    
    public static final int SYMBOL_SIZE_MINIMAL_ORDER = - SYMBOL_SIZE_FRAC_DIFIT_MAX;

    // rectangle
    public static final int RECT_SIZE_FRAC_DIFIT_MIN = 1;
    
    public static final int RECT_SIZE_FRAC_DIFIT_MAX = 2;
    
    public static final int RECT_SIZE_MINIMAL_ORDER = - RECT_SIZE_FRAC_DIFIT_MAX;

    
    // bar
    // public static final float BAR_WIDTH_MIN = 0.05f/CM_POINT_RATIO;
    // public static final float BAR_WIDTH_MAX = 5.0f/CM_POINT_RATIO;
    public static final double BAR_EDGE_LINE_WIDTH_MIN = LINE_WIDTH_MIN_VALUE;

    public static final double BAR_EDGE_LINE_WIDTH_MAX = LINE_WIDTH_MAX_VALUE;
    
    public static final int BAR_WIDTH_INITIAL_ORDER = -4;

    // error bar
    public static final double ERROR_BAR_LINE_WIDTH_MIN = LINE_WIDTH_MIN_VALUE;

    public static final double ERROR_BAR_LINE_WIDTH_MAX = LINE_WIDTH_MAX_VALUE;

    public static final String ERROR_BAR_HEAD_SIZE_UNIT = SYMBOL_SIZE_UNIT;

    public static final double ERROR_BAR_HEAD_SIZE_MIN = SYMBOL_SIZE_MIN;

    public static final double ERROR_BAR_HEAD_SIZE_MAX = SYMBOL_SIZE_MAX;

    public static final double ERROR_BAR_HEAD_SIZE_STEP = SYMBOL_SIZE_STEP;
    
    public static final int ERROR_BAR_HEAD_SIZE_FRAC_DIFIT_MIN = 1;
    
    public static final int ERROR_BAR_HEAD_SIZE_FRAC_DIFIT_MAX = 2;
    
    public static final int ERROR_BAR_HEAD_SIZE_MINIMAL_ORDER = - ERROR_BAR_HEAD_SIZE_FRAC_DIFIT_MAX;
    
    // string
    public static final int LABEL_ANGLE_FRAC_DIFIT_MIN = 1;
    
    public static final int LABEL_ANGLE_FRAC_DIFIT_MAX = 2;
    
    public static final int LABEL_ANGLE_MINIMAL_ORDER = - LABEL_ANGLE_FRAC_DIFIT_MAX;
    
    
    // tick label
    public static final double TICK_LABEL_FONT_SIZE_MIN = FONT_SIZE_MIN_VALUE;

    public static final double TICK_LABEL_FONT_SIZE_MAX = FONT_SIZE_MAX_VALUE;
    
    public static final float TICK_LABEL_TEXT_ANGLE_MIN = -180.0f;
    
    public static final float TICK_LABEL_TEXT_ANGLE_MAX = 180.0f;
    
    public static final int TICK_LABEL_TEXT_ANGLE_STEP = 1;
    
    public static final int TICK_LABEL_TEXT_ANGLE_FRAC_DIFIT_MIN = 1;
    
    public static final int TICK_LABEL_TEXT_ANGLE_FRAC_DIFIT_MAX = 2;

    public static final int TICK_LABEL_TEXT_ANGLE_MINIMAL_ORDER = - TICK_LABEL_TEXT_ANGLE_FRAC_DIFIT_MAX;

    public static final int TICK_LABEL_DECIMAL_PLACES_MIN = 0;

    public static final int TICK_LABEL_DECIMAL_PLACES_MAX = 10;
    
    public static final int TICK_LABEL_DECIMAL_PLACES_STEP = 1;
    
    public static final int TICK_LABEL_EXPONENT_MIN = -10;

    public static final int TICK_LABEL_EXPONENT_MAX = 10;
    
    public static final int TICK_LABEL_EXPONENT_STEP = 1;
    
}
