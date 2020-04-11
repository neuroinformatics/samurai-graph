package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

/**
 * Constants for the two-dimensional vector type data.
 */
public interface SGIVXYDataConstants extends SGIArrowConstants {

    // Visible
    public static final boolean DEFAULT_ARROW_VISIBLE = true;

    // Line Width
    public static final float DEFAULT_LINE_WIDTH = 1.5f;

    // Line Type
    public static final int DEFAULT_LINE_TYPE = LINE_TYPE_SOLID;

    // Color
    public static final Color DEFAULT_COLOR = Color.BLACK;

    // Head Size
    public static final float DEFAULT_HEAD_SIZE = 0.4f;

    // Start Head Type
    public static final int DEFAULT_START_HEAD_TYPE = SGIArrowConstants.SYMBOL_TYPE_VOID;

    // End Head Type
    public static final int DEFAULT_END_HEAD_TYPE = SGIArrowConstants.SYMBOL_TYPE_ARROW_HEAD;

    // Open Angle
    public static final float DEFAULT_HEAD_OPEN_ANGLE = 30.0f;

    // Close Angle
    public static final float DEFAULT_HEAD_CLOSE_ANGLE = 60.0f;

}
