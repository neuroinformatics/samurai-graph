package jp.riken.brain.ni.samuraigraph.figure;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;

/**
 * Constants for the color bar.
 *
 */
public interface SGIColorBarConstants extends SGIConstants {

    /*
     * Constants for property files.
     */
    public static final String TAG_NAME_COLOR_BAR = "ColorBar";

    public static final String KEY_COLOR_BAR_WIDTH = "BarWidth";

    public static final String KEY_COLOR_BAR_LENGTH = "BarLength";

    public static final String KEY_COLOR_BAR_SPACE_LINE_AND_NUMBERS = "SpaceLineAndNumber";

    public static final String KEY_COLOR_BAR_SPACE_TITLE_AND_NUMBERS = "SpaceNumberAndTitle";

    public static final String KEY_COLOR_BAR_DIRECTION = "Direction";

    public static final String KEY_COLOR_BAR_STYLE = "ColorBarStyle";

    // for backward compatibility for version <= 2.0.0
    public static final String KEY_COLOR_BAR_REVERSED_ORDER = "ReversedOrder";

    public static final String KEY_COLOR_BAR_LINE_COLOR = "LineColor";
    

    // bar width
    public static final double COLOR_BAR_WIDTH_MIN = 0.50;

    public static final double COLOR_BAR_WIDTH_MAX = 10.0;

    public static final double COLOR_BAR_WIDTH_STEP = 0.10;

    // bar length
    public static final double COLOR_BAR_LENGTH_MIN = 1.0;

    public static final double COLOR_BAR_LENGTH_MAX = 50.0;

    public static final double COLOR_BAR_LENGTH_STEP = 1.0;

    // common to bar width an bar length
    public static final String COLOR_BAR_SIZE_UNIT = cm;

    public static final int COLOR_BAR_SIZE_FRAC_DIFIT_MIN = 1;

    public static final int COLOR_BAR_SIZE_FRAC_DIFIT_MAX = 2;

    public static final int COLOR_BAR_SIZE_MINIMAL_ORDER = -COLOR_BAR_SIZE_FRAC_DIFIT_MAX;

    
    //
    // Direction
    //
    
    public static final String DIRECTION_HORIZONTAL_LOWER = "Horizontal Lower";
    
    public static final String DIRECTION_HORIZONTAL_UPPER = "Horizontal Upper";
    
    public static final String DIRECTION_VERTICAL_LEFT = "Vertical Left";
    
    public static final String DIRECTION_VERTICAL_RIGHT = "Vertical Right";

    
    //
    // Default values
    //

    // Horizontal Axis
    public static final String DEFAULT_COLOR_BAR_HORIZONTAL_AXIS = SGIFigureElementAxis.AXIS_BOTTOM;

    // Vertical Axis
    public static final String DEFAULT_COLOR_BAR_VERTICAL_AXIS = SGIFigureElementAxis.AXIS_LEFT;

    // bar width
    public static final float DEFAULT_COLOR_BAR_WIDTH = 0.70f;

    // bar length
    public static final float DEFAULT_COLOR_BAR_LENGTH = 8.0f;

    // direction
    public static final String DEFAULT_COLOR_BAR_DIRECTION = DIRECTION_HORIZONTAL_LOWER;
    

    //
    // Command
    //

	public static final String COM_COLOR_BAR = "ColorBar";

    public static final String COM_COLOR_BAR_AXIS_X = "AxisX";
    
    public static final String COM_COLOR_BAR_AXIS_Y = "AxisY";

    // Style
    public static final String COM_COLOR_BAR_STYLE = "Style";


    // Layout
    public static final String COM_COLOR_BAR_LOCATION_X = "X";

    public static final String COM_COLOR_BAR_LOCATION_Y = "Y";

    public static final String COM_COLOR_BAR_WIDTH = "Width";

    public static final String COM_COLOR_BAR_LENGTH = "Length";

    public static final String COM_COLOR_BAR_DIRECTION = "Direction";

    // Frame Line
    public static final String COM_COLOR_BAR_FRAME_LINE_VISIBLE = "FrameLineVisible";
    
    public static final String COM_COLOR_BAR_FRAME_LINE_WIDTH = "FrameLineWidth";
    
    public static final String COM_COLOR_BAR_FRAME_LINE_COLOR = "FrameLineColor";
    
    public static final String COM_COLOR_BAR_SPACE_FRAME_LINE_AND_NUMBER = "FrameLineNumberSpace";

    
    //
    // for backward compatibility (<= 2.0.0)
    //

    public static final String COM_COLOR_MAP_REVERSED_ORDER = "ReversedOrder";
    
    public static final String COM_COLOR_BAR_SPACE_TO_SCALE = "SpaceToScale";

    public static final String COM_COLOR_BAR_SPACE_TO_TITLE = "SpaceToTitle";

    public static final String COM_COLOR_BAR_LINE_COLOR = "LineColor";

}
