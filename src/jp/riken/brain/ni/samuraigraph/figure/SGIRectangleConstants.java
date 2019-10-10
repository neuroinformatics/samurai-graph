package jp.riken.brain.ni.samuraigraph.figure;

import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;

/**
 * Constants for rectangles.
 */
public interface SGIRectangleConstants extends SGIDrawingElementConstants {
    public static final String TAG_NAME_RECTANGLE = "Rectangle";

    public static final String KEY_RECTANGLE_WIDTH = "Width";

    public static final String KEY_RECTANGLE_HEIGHT = "Height";

    public static final String KEY_EDGE_LINE_WIDTH = "EdgeLineWidth";

    public static final String KEY_EDGE_LINE_TYPE = "EdgeLineType";

    public static final String KEY_EDGE_LINE_COLOR = "EdgeLineColor";
    
    public static final String KEY_EDGE_LINE_VISIBLE = "EdgeLineVisible";
    
    // for backward compatibility
    public static final String KEY_INNER_COLOR = "InnerColor";
    
    public static final String KEY_BACKGROUND_TRANSPARENT = "Transparent";
}
