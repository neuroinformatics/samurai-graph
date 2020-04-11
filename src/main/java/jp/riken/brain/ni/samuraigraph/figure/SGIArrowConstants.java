package jp.riken.brain.ni.samuraigraph.figure;

/**
 * Constants for arrows.
 * 
 */
public interface SGIArrowConstants extends SGILineConstants,
        SGISymbolConstants, SGIFigureDrawingElementConstants {

    /*
     * Constants for the types of the head.
     */
    public static final int SYMBOL_TYPE_VOID = -1;
    
    public static final int SYMBOL_TYPE_TRANSVERSELINE = 8;

    public static final int SYMBOL_TYPE_ARROW_HEAD = 10;

    public static final int SYMBOL_TYPE_ARROW = 11;

    public static final int[] ARROW_SYMBOL_TYPE_ARRAY = {
    	SYMBOL_TYPE_ARROW_HEAD, SYMBOL_TYPE_ARROW, SYMBOL_TYPE_CIRCLE, 
    	SYMBOL_TYPE_TRIANGLE, SYMBOL_TYPE_INVERTED_TRIANGLE, 
    	SYMBOL_TYPE_SQUARE, SYMBOL_TYPE_DIAMOND, SYMBOL_TYPE_TRANSVERSELINE,
    	SYMBOL_TYPE_CROSS, SYMBOL_TYPE_PLUS
    };

    /*
     * Constants for the names of the head.
     */
    public static final String SYMBOL_NAME_VOID = "No Symbol";

    public static final String SYMBOL_NAME_TRANSVERSE_LINE = "Transverse Line";

    public static final String SYMBOL_NAME_ARROW_HEAD = "Arrow Head";

    public static final String SYMBOL_NAME_ARROW = "Arrow";
    
    final String ARROW_SYMBOL_NAME_ARRAY[] = {
    		SYMBOL_NAME_ARROW_HEAD,
    		SYMBOL_NAME_ARROW,
    		SYMBOL_NAME_CIRCLE,
    		SYMBOL_NAME_TRIANGLE,
    		SYMBOL_NAME_INVERTED_TRIANGLE,
    		SYMBOL_NAME_SQUARE,
    		SYMBOL_NAME_DIAMOND,
    		SYMBOL_NAME_TRANSVERSE_LINE,
    		SYMBOL_NAME_CROSS,
    		SYMBOL_NAME_VOID };

    //
    // Arrow head size
    //
    
    public static final String ARROW_HEAD_SIZE_UNIT = SYMBOL_SIZE_UNIT;

    public static final double ARROW_HEAD_SIZE_MIN = SYMBOL_SIZE_MIN;

    public static final double ARROW_HEAD_SIZE_MAX = SYMBOL_SIZE_MAX;

    public static final double ARROW_HEAD_SIZE_STEP = 0.05;
    
    public static final int ARROW_HEAD_SIZE_FRAC_DIFIT_MIN = 1;
    
    public static final int ARROW_HEAD_SIZE_FRAC_DIFIT_MAX = 2;
    
    public static final int ARROW_HEAD_SIZE_MINIMAL_ORDER = - ARROW_HEAD_SIZE_FRAC_DIFIT_MAX;
    
    
    //
    // Open and Close angle
    //
    
    public static final double ARROW_HEAD_OPEN_ANGLE_MIN = 5.0;

    public static final double ARROW_HEAD_OPEN_ANGLE_MAX = 85.0;

    public static final double ARROW_HEAD_CLOSE_ANGLE_MIN = 5.0;

    public static final double ARROW_HEAD_CLOSE_ANGLE_MAX = 175.0;
    
    public static final double ARROW_HEAD_ANGLE_STEP = 1.0;
    
    public static final int ARROW_HEAD_ANGLE_FRAC_DIFIT_MIN = 1;
    
    public static final int ARROW_HEAD_ANGLE_FRAC_DIFIT_MAX = 2;
    
    public static final int ARROW_HEAD_ANGLE_MINIMAL_ORDER = - ARROW_HEAD_ANGLE_FRAC_DIFIT_MAX;
    
    
    //
    // Keys
    //
    
    public static final String TAG_NAME_ARROW = "Arrow";

    // public static final String KEY_LINE_WIDTH = "LineWidth";
    // public static final String KEY_LINE_TYPE = "LineType";
    public static final String KEY_HEAD_SIZE = "HeadSize";

    public static final String KEY_START_HEAD_TYPE = "StartHeadType";

    public static final String KEY_END_HEAD_TYPE = "EndHeadType";

    // public static final String KEY_HEAD_LINE_WIDTH = "HeadLineWidth";
    public static final String KEY_HEAD_LINE_COLOR = "HeadLineColor";

    public static final String KEY_HEAD_OPEN_ANGLE = "HeadOpenAngle";

    public static final String KEY_HEAD_CLOSE_ANGLE = "HeadCloseAngle";

}
