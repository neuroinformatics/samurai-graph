package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;

/**
 * Constants for the figures.
 * 
 */
public interface SGIFigureConstants extends SGIConstants {

    // figure location
    public static final String FIGURE_LOCATION_UNIT = cm;

    public static final double FIGURE_X_MIN = -50.0;

    public static final double FIGURE_X_MAX = 150.0;

    public static final double FIGURE_Y_MIN = -50.0;

    public static final double FIGURE_Y_MAX = 150.0;

    public static final double FIGURE_LOCATION_STEP = 1.0;
    
    // figure size
    public static final String FIGURE_SIZE_UNIT = cm;

    public static final double FIGURE_WIDTH_MIN = 0.10;

    public static final double FIGURE_WIDTH_MAX = 50.0;

    public static final double FIGURE_HEIGHT_MIN = 0.10;

    public static final double FIGURE_HEIGHT_MAX = 50.0;

    public static final double FIGURE_SIZE_STEP = 1.0;
    
    //
    // Default values
    //

    // x-coordinate
    public static final float DEFAULT_FIGURE_X = 2.0f;

    // y-coordinate
    public static final float DEFAULT_FIGURE_Y = 1.0f;

    // background color to white
    public static final Color DEFAULT_FIGURE_BACKGROUND_COLOR = Color.WHITE;

    // bounding box visible
    public static final boolean DEFAULT_FIGURE_BOUNDING_BOX_VISIBLE = false;

    // rubber banding enabled
    public static final boolean DEFAULT_FIGURE_RUBBER_BANDING_ENABLED = true;

    // snap to grid
    public static final boolean DEFAULT_FIGURE_SNAP_TO_GRID = true;


    /**
     * Constants for the property file.
     */
    
    public static final String TAG_NAME_FIGURE = "Figure";

    public static final String KEY_FIGURE_TYPE = "Type";

    public static final String KEY_FIGURE_X_IN_CLIENT = "X";

    public static final String KEY_FIGURE_Y_IN_CLIENT = "Y";

    public static final String KEY_FIGURE_WIDTH = "Width";

    public static final String KEY_FIGURE_HEIGHT = "Height";

    public static final String KEY_FIGURE_BACKGROUND_COLOR = "BackgroundColor";

    public static final String KEY_FIGURE_BACKGROUND_TRANSPARENT = "BackgroundTransparent";
    
    public static final String KEY_FIGURE_DATA_ANCHOR = "DataAnchor";

	public static final String COM_FIGURE = "Figure";

    public static final String COM_FIGURE_LOCATION_X = "X";

    public static final String COM_FIGURE_LOCATION_Y = "Y";

    public static final String COM_FIGURE_WIDTH = "Width";

    public static final String COM_FIGURE_HEIGHT = "Height";

    public static final String COM_FIGURE_SPACE_TO_SCALE = "SpaceToScale";

    public static final String COM_FIGURE_SPACE_TO_TITLE = "SpaceToTitle";

    public static final String COM_FIGURE_BACKGROUND_COLOR = "BackgroundColor";

    public static final String COM_FIGURE_TRANSPARENT = "BackgroundTransparent";

    public static final String COM_FIGURE_FRAME_VISIBLE = "FrameVisible";

    public static final String COM_FIGURE_FRAME_LINE_WIDTH = "FrameLineWidth";

    public static final String COM_FIGURE_FRAME_COLOR = "FrameColor";
    
    public static final String COM_FIGURE_DATA_ANCHOR = "DataAnchor";

    public static final String COM_FIGURE_GRID_AXIS_X = "GridAxisX";

    public static final String COM_FIGURE_GRID_AXIS_Y = "GridAxisY";

    public static final String COM_FIGURE_GRID_VISIBLE = "GridVisible";

    public static final String COM_FIGURE_GRID_AUTO = "GridAuto";

    public static final String COM_FIGURE_GRID_STEP_X = "GridStepX";

    public static final String COM_FIGURE_GRID_STEP_Y = "GridStepY";

    public static final String COM_FIGURE_GRID_BASE_X = "GridBaseX";

    public static final String COM_FIGURE_GRID_BASE_Y = "GridBaseY";

    public static final String COM_FIGURE_GRID_LINE_WIDTH = "GridLineWidth";

    public static final String COM_FIGURE_GRID_LINE_TYPE = "GridLineType";

    public static final String COM_FIGURE_GRID_LINE_COLOR = "GridColor";

}
