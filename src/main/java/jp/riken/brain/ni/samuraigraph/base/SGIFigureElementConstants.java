package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;

public interface SGIFigureElementConstants {

    public static final String KEY_DATA_TYPE = "Type";

    public static final String KEY_DATA_NAME = "Name";

    public static final String KEY_VISIBLE_IN_LEGEND = "VisibleInLegend";

    public static final String KEY_AXIS_POSITION = "AxisPosition";

    public static final String KEY_X_AXIS_POSITION = "XAxisPosition";

    public static final String KEY_Y_AXIS_POSITION = "YAxisPosition";

    public static final String KEY_INDEX_IN_LEGEND = "IndexInLegend";

    public static final String KEY_ANIMATION_ARRAY_SECTION = "AnimationArraySection";

    public static final String KEY_ANIMATION_FRAME_RATE = "FrameRate";
    
    public static final String KEY_ANIMATION_LOOP_PLAYBACK = "LoopPlayback";
    
    public static final String NOTIFY_CHANGE = "Notify the change";

    public static final String NOTIFY_CHANGE_ON_UNDO = "Notify the change on undo";

    public static final String NOTIFY_CHANGE_ON_COMMIT = "Notify the change on commit";

    public static final String NOTIFY_CHANGE_ON_CANCEL = "Notify the change on cancel";

    public static final String SHOW_PROPERTY_DIALOG_FOR_SELECTED_OBJECTS = "Show property dialog for selected objects";

    public static final String SHOW_PROPERTY_DIALOG_FOR_VISIBLE_OBJECTS = "Show property dialog for visible objects";

    public static final String SHOW_PROPERTY_DIALOG_FOR_ALL_OBJECTS = "Show property dialog for all objects";

    public static final String CLEAR_FOCUSED_OBJECTS = "Clear focused objects";

    public static final String NOTIFY_CHANGE_TO_ROOT = "Notify the change to root";

    public static final String NOTIFY_CHANGE_CURSOR = "Notify the change of cursor";

    public static final String NOTIFY_DATA_CLICKED = "Notigy a data object is clicked";
    
    public static final String NOTIFY_DATA_SELECTION = "Notify the selection of data";
    
    public static final String NOTIFY_DATA_HIDDEN = "Data objects are hidden";
    
    public static final String NOTIFY_DATA_WILL_BE_HIDDEN = "Data objects will be hidden";
    
    public static final String NOTIFY_NETCDF_DATALABEL_WILL_BE_HIDDEN = "Data label will be hidden";
    
    public static final String NOTIFY_DATA_STRUCTURE_CHANGE_ON_PREVIEW = "Data structure is changed on preview";

    public static final String NOTIFY_DATA_STRUCTURE_CHANGE_ON_CANCEL = "Data structure is changed on cancel";

    public static final String NOTIFY_DATA_STRUCTURE_CHANGE_ON_COMMIT = "Data structure is changed on commit";

    public static final String NOTIFY_DATA_PROPERTIES_CHANGE_ON_PREVIEW = "Data properties are changed on preview";

    public static final String NOTIFY_DATA_PROPERTIES_CHANGE_ON_CANCEL = "Data properties are changed on cancel";

    public static final String NOTIFY_DATA_PROPERTIES_CHANGE_ON_COMMIT = "Data properties are changed on commit";

    public static final String NOTIFY_UNKNOWN_DATA_ERROR = "Notify unknown data error occurred";


    /**
     * Size of the anchor.
     */
    public static final float ANCHOR_SIZE_FOR_FOCUSED_OBJECTS = 6.0f;

    /**
     * Width of the edge line of the anchor.
     */
    public static final float ANCHOR_EDGE_LINE_WIDTH = 1.0f;

    /**
     * Color of the edge line of the anchor.
     */
    public static final Color ANCHOR_EDGE_LINE_COLOR = Color.BLACK;

    /**
     * Color of inside of the anchor.
     */
    public static final Color ANCHOR_INNER_COLOR = Color.WHITE;

    public static final float ANCHOR_SIZE_FOR_CHILD_OBJECTS = 4.0f;

    public static final Color ANCHOR_INNER_COLOR_FOR_CHILD_OBJECTS = Color.BLACK;

}
