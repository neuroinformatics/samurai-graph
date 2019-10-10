package jp.riken.brain.ni.samuraigraph.figure;

import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;

/**
 * Constants for error bars.
 * 
 */
public interface SGIErrorBarConstants extends SGIDrawingElementConstants,
		SGIArrowConstants {

    /*
     * Constants for the style of the error bars.
     */
    public static final int ERROR_BAR_BOTHSIDES = 1;

    public static final int ERROR_BAR_UPSIDE = 2;

    public static final int ERROR_BAR_DOWNSIDE = 3;

    /*
     * Constants for the name of the style of the error bars.
     */
    public static final String ERROR_BAR_STYLE_BOTHSIDES = "Bothsides";

    public static final String ERROR_BAR_STYLE_UPSIDE = "Upside";

    public static final String ERROR_BAR_STYLE_DOWNSIDE = "Downside";

    /*
     * Constants for the location of the error bars.
     */
    public static final int ERROR_BAR_ON_LINE = 1;
    
    public static final int ERROR_BAR_ON_BAR = 2;
    
    /*
     * Constants for the name of the location of the error bars.
     */
    public static final String ERROR_BAR_POSITION_NAME_ON_LINE = "Line";
    
    public static final String ERROR_BAR_POSITION_NAME_ON_BAR = "Bar";

    /*
     * Constants for the property file.
     */
    public static final String TAG_NAME_ERROR_BAR = "ErrorBar";

    public static final String KEY_ERROR_BAR_STYLE = "Style";

    public static final String KEY_ERROR_BAR_HEAD_TYPE = "HeadType";
    
    public static final String KEY_ERROR_BAR_VERTICAL = "Vertical";
    
    public static final String KEY_ERROR_BAR_POSITION = "Position";

}
