package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;

/**
 * Constants for shape objects.
 * 
 */
public interface SGIShapeConstants {

    public static final String SHAPE_TYPE_RECTANGLE = "Rectangle";

    public static final String SHAPE_TYPE_ELLIPSE = "Ellipse";

    public static final String SHAPE_TYPE_ARROW = "Arrow";

    public static final String SHAPE_TYPE_LINE = "Line";

    
    //
    // Default vaules
    //

    // X Axis
    public static final String DEFAULT_SHAPE_HORIZONTAL_AXIS = SGIFigureElementAxis.AXIS_BOTTOM;

    // Y Axis
    public static final String DEFAULT_SHAPE_VERTICAL_AXIS = SGIFigureElementAxis.AXIS_LEFT;

    //
    // Line
    //

    // Line Width
    public static final float DEFAULT_SHAPE_LINE_WIDTH = 1.5f;

    // Line Type
    public static final int DEFAULT_SHAPE_LINE_TYPE = SGILineConstants.LINE_TYPE_SOLID;

    // Color
    public static final Color DEFAULT_SHAPE_LINE_COLOR = Color.BLACK;

    //
    // Arrow
    //

    // Line Width
    public static final float DEFAULT_SHAPE_ARROW_LINE_WIDTH = DEFAULT_SHAPE_LINE_WIDTH;

    // Line Type
    public static final int DEFAULT_SHAPE_ARROW_LINE_TYPE = DEFAULT_SHAPE_LINE_TYPE;

    // Color
    public static final Color DEFAULT_SHAPE_ARROW_COLOR = DEFAULT_SHAPE_LINE_COLOR;

    // Head Size
    public static final float DEFAULT_SHAPE_ARROW_HEAD_SIZE = 0.4f;

    // Start Head Type
    public static final int DEFAULT_SHAPE_ARROW_START_HEAD_TYPE = SGIArrowConstants.SYMBOL_TYPE_VOID;

    // End Head Type
    public static final int DEFAULT_SHAPE_ARROW_END_HEAD_TYPE = SGIArrowConstants.SYMBOL_TYPE_ARROW_HEAD;

    // Open Angle
    public static final float DEFAULT_SHAPE_ARROW_HEAD_OPEN_ANGLE = 30.0f;

    // Close Angle
    public static final float DEFAULT_SHAPE_ARROW_HEAD_CLOSE_ANGLE = 60.0f;
    
    // Anchored
    public static final boolean DEFAULT_SHAPE_ARROW_ANCHORED = false;

    //
    // Rectangle
    //

    // Width
    public static final float DEFAULT_SHAPE_RETANGLE_WIDTH = 5.0f;

    // Height
    public static final float DEFAULT_SHAPE_RETANGLE_HEIGHT = 4.0f;

    // Color
    public static final Color DEFAULT_SHAPE_RECTANGLE_INNER_COLOR = Color.WHITE;

    // Edge Line Width
    public static final float DEFAULT_SHAPE_RCTANGLE_EDGE_LINE_WIDTH = DEFAULT_SHAPE_LINE_WIDTH;

    // Edge Line Type
    public static final int DEFAULT_SHAPE_RCTANGLE_EDGE_LINE_TYPE = DEFAULT_SHAPE_LINE_TYPE;

    // Edge Line Color
    public static final Color DEFAULT_SHAPE_RECTANGLE_EDGE_LINE_COLOR = DEFAULT_SHAPE_LINE_COLOR;
    
    // Edge Line Visibility
    public static final boolean DEFAULT_SHAPE_RECTANGLE_EDGE_LINE_VISIBLE = true;

    // Transparecy
    public static final boolean DEFAULT_SHAPE_RECTANGLE_TRANSPARENT = false;
    
    // Anchored
    public static final boolean DEFAULT_SHAPE_RECTANGLE_ANCHORED = false;

    
	public static final String COM_SHAPE = "Shape";

    public static final String COM_SHAPE_TYPE = "ShapeType";

    public static final String COM_SHAPE_AXIS_X = "AxisX";

    public static final String COM_SHAPE_AXIS_Y = "AxisY";

    // rectangular shape
    public static final String COM_RECTANGLE_LEFT_X = "LeftX";

    public static final String COM_RECTANGLE_RIGHT_X = "RightX";

    public static final String COM_RECTANGLE_TOP_Y = "TopY";

    public static final String COM_RECTANGLE_BOTTOM_Y = "BottomY";
    
    public static final String COM_RECTANGLE_ANCHORED = "Anchored";

    public static final String COM_RECTANGLE_EDGE_LINE_WIDTH = "EdgeLineWidth";

    public static final String COM_RECTANGLE_EDGE_LINE_TYPE = "EdgeLineType";

    public static final String COM_RECTANGLE_EDGE_LINE_COLOR = "EdgeLineColor";
    
    public static final String COM_RECTANGLE_EDGE_LINE_VISIBLE = "EdgeLineVisible";

    public static final String COM_RECTANGLE_BACKGROUND_PAINT_STYLE = "BackgroundPaintStyle";
    
    public static final String COM_RECTANGLE_BACKGROUND_FILL_COLOR = "BackgroundFillColor";
    
    public static final String COM_RECTANGLE_BACKGROUND_PATTERN_COLOR = "BackgroundPatternColor";
    
    public static final String COM_RECTANGLE_BACKGROUND_PATTERN_TYPE = "BackgroundPatternType";
    
    public static final String COM_RECTANGLE_BACKGROUND_GRADATION_COLOR1 = "BackgroundGradationColor1";
    
    public static final String COM_RECTANGLE_BACKGROUND_GRADATION_COLOR2 = "BackgroundGradationColor2";
    
    public static final String COM_RECTANGLE_BACKGROUND_GRADATION_DIRECTION = "BackgroundGradationDirection";
    
    public static final String COM_RECTANGLE_BACKGROUND_GRADATION_ORDER = "BackgroundGradationOrder";
    
    public static final String COM_RECTANGLE_BACKGROUND_TRANSPARENCY = "BackgroundTransparency";

    // arrow
    public static final String COM_ARROW_START_X = "StartX";

    public static final String COM_ARROW_START_Y = "StartY";

    public static final String COM_ARROW_END_X = "EndX";

    public static final String COM_ARROW_END_Y = "EndY";

    public static final String COM_ARROW_LINE_WIDTH = "LineWidth";

    public static final String COM_ARROW_LINE_TYPE = "LineType";

    public static final String COM_ARROW_HEAD_SIZE = "HeadSize";

    public static final String COM_ARROW_COLOR = "Color";

    public static final String COM_ARROW_START_TYPE = "StartType";

    public static final String COM_ARROW_END_TYPE = "EndType";

    public static final String COM_ARROW_HEAD_OPEN_ANGLE = "HeadOpenAngle";
    
    public static final String COM_ARROW_HEAD_CLOSE_ANGLE = "HeadCloseAngle";

    public static final String COM_ARROW_HEAD_ANGLE = "HeadAngle";
    
    public static final String COM_ARROW_ANCHORED = "Anchored";

}
