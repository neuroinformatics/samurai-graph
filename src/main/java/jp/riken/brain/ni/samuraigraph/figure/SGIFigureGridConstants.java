package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;

/**
 * Constants for grid in a figure.
 * 
 */
public interface SGIFigureGridConstants extends SGILineConstants {

    //
    // Default values
    //

    // Visible
    public static final boolean DEFAULT_GRID_VISIBLE = false;

    // auto calc
    public static final boolean DEFAULT_GRID_AUTO_CALC = true;

    // Line Width
    public static final float DEFAULT_GRID_LINE_WIDTH = 1.0f;

    // Line Type
    public static final int DEFAULT_GRID_LINE_TYPE = SGILineConstants.LINE_TYPE_DOTTED;

    // Line Color
    public static final Color DEFAULT_GRID_COLOR = Color.GRAY;

    // Horizontal Axis
    public static final String DEFAULT_GRID_HORIZONTAL_AXIS = SGIFigureElementAxis.AXIS_BOTTOM;

    // Vertical Axis
    public static final String DEFAULT_GRID_VERTICAL_AXIS = SGIFigureElementAxis.AXIS_LEFT;

    // The stepping value for X-axis.
    public static final double DEFAULT_GRID_STEP_VALUE_X = 1.0;

    // The baseline value for X-axis.
    public static final double DEFAULT_GRID_BASELINE_VALUE_X = 0.0;

    // The stepping value for Y-axis.
    public static final double DEFAULT_GRID_STEP_VALUE_Y = 1.0;

    // The baseline value for Y-axis.
    public static final double DEFAULT_GRID_BASELINE_VALUE_Y = 0.0;
}
