package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;

import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.MediaSize;

/**
 * Constants for the root objects.
 *
 */
public interface SGIRootObjectConstants extends SGIConstants {

    //
    // Text for the menu command of the menu bar
    //

    public static final String MENUBAR_FILE = "File";

    public static final String MENUBARCMD_CREATE_NEW_WINDOW = "Create New Window";

    public static final String MENUBARCMD_CLOSE_WINDOW = "Close Window";

    public static final String MENUBARCMD_DRAW_GRAPH = "Draw Graph";

    public static final String MENUBARCMD_RELOAD = "Reload";

    public static final String MENUBARCMD_LOAD_PROPERTY = "Load Property";

    public static final String MENUBARCMD_SAVE_PROPERTY = "Save Property";

    public static final String MENUBARCMD_LOAD_DATASET = "Load Data Set";

    public static final String MENUBARCMD_SAVE_DATASET = "Save Data Set";

    public static final String MENUBARCMD_LOAD_SCRIPT = "Load Script";

    public static final String MENUBARCMD_SAVE_AS_SCRIPT = "Save as Script";

    public static final String MENUBARCMD_LOAD_BACKGROUND_IMAGE = "Load Background Image";

    public static final String MENUBARCMD_EXPORT_AS_IMAGE = "Export as Image";

    public static final String MENUBARCMD_PRINT = "Print";

    public static final String MENUBARCMD_EXIT = "Exit";

    public static final String MENUBAR_EDIT = "Edit";

    public static final String MENUBARCMD_UNDO = "Undo";

    public static final String MENUBARCMD_REDO = "Redo";

    public static final String MENUBARCMD_CLEAR_UNDO_BUFFER = "Clear Undo Buffer";

    public static final String MENUBARCMD_CUT = "Cut";

    public static final String MENUBARCMD_COPY = "Copy";

    public static final String MENUBARCMD_PASTE = "Paste";

    public static final String MENUBARCMD_DELETE = "Delete";

    public static final String MENUBARCMD_SELECT_ALL = "Select All";

    public static final String MENUBARCMD_DUPLICATE = "Duplicate";

    public static final String MENUBARCMD_DELETE_BACKGROUND_IMAGE = "Delete Background Image";

    public static final String MENUBAR_INSERT = "Insert";

    public static final String MENUBARCMD_INSERT_LABEL = "Label";

    public static final String MENUBARCMD_INSERT_SIG_DIFF_SYMBOL = "Significant Difference";

    public static final String MENUBARCMD_INSERT_AXIS_BREAK_SYMBOL = "Axis Break";

    public static final String MENUBARCMD_INSERT_TIMING_LINE = "Timing Line";

    public static final String MENUBARCMD_INSERT_RECTANGLE = "Rectangle";

    public static final String MENUBARCMD_INSERT_ELLIPSE = "Ellipse";

    public static final String MENUBARCMD_INSERT_LINE = "Line";

    public static final String MENUBARCMD_INSERT_ARROW = "Arrow";

    public static final String MENUBAR_LAYOUT = "Layout";

    public static final String MENUBAR_PAPER_SIZE = "Paper Size";

//    public static final String MENUBARCMD_PAPER_A4_SIZE = PAPER_SIZE_A4;
//
//    public static final String MENUBARCMD_PAPER_B5_SIZE = PAPER_SIZE_B5;
//
//    public static final String MENUBARCMD_PAPER_USLETTER_SIZE = PAPER_SIZE_US_LETTER;
//
//    public static final String MENUBARCMD_PAPER_PORTRAIT = "Portrait";
//
//    public static final String MENUBARCMD_PAPER_LANDSCAPE = "Landscape";

    public static final String MENUBARCMD_PAPER_A4_PORTRAIT = "A4 Portrait";

    public static final String MENUBARCMD_PAPER_B5_PORTRAIT = "B5 Portrait";

    public static final String MENUBARCMD_PAPER_USLETTER_PORTRAIT = "US Letter Portrait";

    public static final String MENUBARCMD_PAPER_A4_LANDSCAPE = "A4 Landscape";

    public static final String MENUBARCMD_PAPER_B5_LANDSCAPE = "B5 Landscape";

    public static final String MENUBARCMD_PAPER_USLETTER_LANDSCAPE = "US Letter Landscape";

    public static final String MENUBARCMD_BOUNDING_BOX = "Bounding Box";

    public static final String MENUBARCMD_PAPER_USER_CUSTOMIZE = "User Customize";

    public static final String MENUBAR_TOOL_BAR = "Tool Bar";

    public static final String MENUBARCMD_VISIBLE_FILE = "File";

    public static final String MENUBARCMD_VISIBLE_EDIT = "Edit";

    public static final String MENUBARCMD_VISIBLE_INSERT = "Insert";

    public static final String MENUBARCMD_VISIBLE_LAYOUT = "Layout";

    public static final String MENUBARCMD_VISIBLE_HELP = "Help";

    public static final String MENUBARCMD_VISIBLE_ZOOM = "Zoom";

    public static final String MENUBAR_GRID = "Grid";

    public static final String MENUBARCMD_PLUS_GRID = "Plus";

    public static final String MENUBARCMD_MINUS_GRID = "Minus";

    public static final String MENUBARCMD_GRID_VISIBLE = "Visible";

    public static final String MENUBARCMD_SNAP_TO_GRID = "Snap to Grid";

    public static final String MENUBAR_ZOOM = "Zoom";

    public static final String MENUBARCMD_ZOOM_IN = "Zoom In";

    public static final String MENUBARCMD_ZOOM_OUT = "Zoom Out";

    public static final String MENUBARCMD_DEFAULT_ZOOM = "Default Zoom";

    public static final String MENUBARCMD_ZOOM_WAY_OUT = "Zoom Way Out";

    public static final String MENUBARCMD_AUTO_ZOOM = "Auto Zoom";

    public static final String MENUBARCMD_LOCK = "Lock";

    public static final String MENUBAR_ARRANGE = "Arrange";

    public static final String MENUBARCMD_BRING_TO_FRONT = "Bring to Front";

    public static final String MENUBARCMD_BRING_FORWARD = "Bring Forward";

    public static final String MENUBARCMD_SEND_BACKWARD = "Send Backward";

    public static final String MENUBARCMD_SEND_TO_BACK = "Send to Back";

    public static final String MENUBARCMD_SPLIT = "Split";

    public static final String MENUBARCMD_MERGE = "Merge";

    public static final String MENUBARCMD_ASSIGN_LINE_COLORS = "Assign Line Colors";

    public static final String MENUBARCMD_MODE = "Mode";

    public static final String MENUBARCMD_AUTO_ARRANGEMENT = "Auto Arrangement";

    public static final String MENUBAR_ALIGN_OBJECTS = "Align Objects";

    public static final String MENUBARCMD_ALIGN_LEFT = "Left";

    public static final String MENUBARCMD_ALIGN_CENTER = "Center";

    public static final String MENUBARCMD_ALIGN_RIGHT = "Right";

    public static final String MENUBARCMD_ALIGN_TOP = "Top";

    public static final String MENUBARCMD_ALIGN_MIDDLE = "Middle";

    public static final String MENUBARCMD_ALIGN_BOTTOM = "Bottom";

    public static final String MENUBAR_PROPERTIES = "Properties";

    public static final String MENU_CHILD_OBJECTS = "Child";

    public static final String MENUBAR_PLUGIN = "Plug-in";
    
    public static final String MENUBAR_HELP = "Help";

    public static final String MENUBARCMD_HELP = MENUBAR_HELP;

    public static final String MENUBAR_LOOKANDFEEL = "Look & Feel";

    public static final String MENUBARCMD_LAF_METAL = LAF_METAL;

    public static final String MENUBARCMD_LAF_MOTIF = LAF_MOTIF;

    public static final String MENUBARCMD_LAF_WINDOWS = LAF_WINDOWS;

    public static final String MENUBARCMD_LAF_WINDOWSCLASSIC = LAF_WINDOWSCLASSIC;

    public static final String MENUBARCMD_LAF_AQUA = LAF_AQUA;

    public static final String LAF_CLASS_NAME_BASIC = "javax.swing.plaf.basic.BasicLookAndFeel";

    public static final String LAF_CLASS_NAME_METAL = "javax.swing.plaf.metal.MetalLookAndFeel";

    public static final String LAF_CLASS_NAME_AQUA = "apple.laf.AquaLookAndFeel";

    public static final String LAF_CLASS_NAME_MOTIF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

    public static final String LAF_CLASS_NAME_WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

    public static final String LAF_CLASS_NAME_WINDOWSCLASSIC = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";

    public static final String MENUBARCMD_UPGRADE = "Upgrade";

    public static final String MENUBARCMD_CHANGE_LOG = "Change Log";

    public static final String MENUBARCMD_PROXY = "Proxy";

    public static final String MENUBARCMD_MEMORY = "Memory Info";

    public static final String MENUBARCMD_PLUGIN_DETAIL = "Details of Plug-in";

    public static final String MENUBARCMD_ABOUT = "About";

    public static final String[] INSERT_MENUBARCMD_ARRAY = {
            MENUBARCMD_INSERT_LABEL, MENUBARCMD_INSERT_SIG_DIFF_SYMBOL,
            MENUBARCMD_INSERT_AXIS_BREAK_SYMBOL, MENUBARCMD_INSERT_TIMING_LINE,
            MENUBARCMD_INSERT_RECTANGLE, MENUBARCMD_INSERT_ELLIPSE,
            MENUBARCMD_INSERT_ARROW, MENUBARCMD_INSERT_LINE };

    public static final String[] TOOLBAR_MENUCMD_ARRAY = {
            MENUBARCMD_VISIBLE_FILE, MENUBARCMD_VISIBLE_EDIT,
            MENUBARCMD_VISIBLE_INSERT, MENUBARCMD_VISIBLE_LAYOUT,
            // MENUBARCMD_VISIBLE_HELP,
            MENUBARCMD_VISIBLE_ZOOM };

    public static final String PROPERTY_NAME_TOOL_BAR = "Tool Bar";

    //
    // Text for the tool-tips of the tool bar
    //

    public static final String TIP_CREATE_NEW_WINDOW = MENUBARCMD_CREATE_NEW_WINDOW;

    public static final String TIP_DRAW_GRAPH = MENUBARCMD_DRAW_GRAPH;

    public static final String TIP_LOAD_PROPERTY = MENUBARCMD_LOAD_PROPERTY;

    public static final String TIP_SAVE_PROPERTY = MENUBARCMD_SAVE_PROPERTY;

    public static final String TIP_EXPORT_FORMAT = MENUBARCMD_EXPORT_AS_IMAGE;

    public static final String TIP_BOUNDING_BOX = MENUBARCMD_BOUNDING_BOX;

    public static final String TIP_INSERT_LABEL = "Insert Label";

    public static final String TIP_INSERT_SIG_DIFF_SYMBOL = "Insert Significant Difference Symbol";

    public static final String TIP_INSERT_AXIS_BREAK_SYMBOL = "Insert Axis Break Symbol";

    public static final String TIP_INSERT_TIMING_LINE = "Insert Timing Line";

    public static final String TIP_LOCK = MENUBARCMD_LOCK;

    public static final String TIP_ZOOM = "Zoom";

    public static final String TIP_UNDO = "Undo";

    public static final String TIP_REDO = "Redo";

    public static final String TIP_CUT = "Cut";

    public static final String TIP_COPY = "Copy";

    public static final String TIP_PASTE = "Paste";

    public static final String TIP_PRINT = "Print";

    public static final String TIP_HELP = "Help";

    //
    // Keys for the properties
    //

    public static final String TAG_NAME_WINDOW = "Window";

    public static final String KEY_PAPER_WIDTH = "PaperWidth";

    public static final String KEY_PAPER_HEIGHT = "PaperHeight";

    public static final String KEY_BACKGROUND_COLOR = "BackgroundColor";

    public static final String KEY_GRID_VISIBLE = "GridVisible";

    public static final String KEY_GRID_INTERVAL = "GridInterval";

    public static final String KEY_GRID_LINE_WIDTH = "GridLineWidth";

    public static final String KEY_GRID_COLOR = "GridLineColor";

    public static final String KEY_IMAGE_LOCATION_X = "ImageX";

    public static final String KEY_IMAGE_LOCATION_Y = "ImageY";

    public static final String KEY_IMAGE_SCALE = "ImageScale";


    //
    // Constant Values
    //

    public static final String PAPER_SIZE_UNIT = cm;

    public static final double PAPER_WIDTH_MIN_VALUE = 0.10;

    public static final double PAPER_WIDTH_MAX_VALUE = 99.0;

    public static final double PAPER_HEIGHT_MIN_VALUE = 0.10;

    public static final double PAPER_HEIGHT_MAX_VALUE = 99.0;

    public static final String GRID_INTERVAL_UNIT = cm;

    public static final double GRID_INTERVAL_MIN_VALUE = 0.10;

    public static final double GRID_INTERVAL_MAX_VALUE = 10.0;

    public static final String IMAGE_LOCATION_UNIT = cm;

    public static final double IMAGE_LOCATION_X_MIN_VALUE = -50.0;

    public static final double IMAGE_LOCATION_X_MAX_VALUE = 150.0;

    public static final double IMAGE_LOCATION_Y_MIN_VALUE = -50.0;

    public static final double IMAGE_LOCATION_Y_MAX_VALUE = 150.0;

    public static final String IMAGE_SIZE_UNIT = cm;

//    public static final double IMAGE_WIDTH_MIN_VALUE = 0.0;
//
//    public static final double IMAGE_WIDTH_MAX_VALUE = 99.0;
//
//    public static final double IMAGE_HEIGHT_MIN_VALUE = 0.0;
//
//    public static final double IMAGE_HEIGHT_MAX_VALUE = 99.0;

    public static final double IMAGE_SCALE_MIN_VALUE = 0.01;

    public static final double IMAGE_SCALE_MAX_VALUE = 100.0;

    public static final double PAPER_SIZE_STEP_SIZE = 1.0;

    public static final double GRID_INTERVAL_STEP_SIZE = 0.1;

    public static final double IMAGE_LOCATION_STEP_SIZE = 1.0;

    public static final double IMAGE_SIZE_STEP_SIZE = 1.0;

    /**
     * The order of length on the paper in CM unit.
     */
    public static final int LENGTH_MINIMAL_ORDER = -1;

    /**
     * The order of scaling factor for the background image.
     */
    public static final int IMAGE_SCALING_ORDER = -2;

    /**
     * zoom
     */
    public static final int DEFAULT_ZOOM = 100;

    public static final int MIN_MAGNIFICATION_VALUE = 10;

    public static final int MAX_MAGNIFICATION_VALUE = 400;

    public static final int[] MAGNIFICATION_ARRAY = { 400, 300, 200, 150, 100,
            75, 66, 50, 33, 25 };

    public static final String AUTO_ZOOM_IN_COMBO_BOX = "Auto";

    public static final String[] MAGNIFICATION_STRING_ARRAY = { "400", "300",
            "200", "150", "100", "75", "66", "50", "33", "25" };

    /**
     * icons
     */
    public static final String SAMURAI_IMG_FILENAME = "Samurai.gif";

    public static final String NEW_WINDOW_ICON_FILENAME = "NewGraph.png";

    public static final String DRAW_GRAPH_ICON_FILENAME = "DrawGraph.png";

    public static final String LOAD_PROPERTY_ICON_FILENAME = "LoadProperty.png";

    public static final String SAVE_PROPERTY_ICON_FILENAME = "SaveGraph.png";

    public static final String EXPORT_IMAGE_ICON_FILENAME = "ExportImage.png";

    public static final String PRINT_ICON_FILENAME = "Print.png";

    public static final String UNDO_ICON_FILENAME = "Undo.png";

    public static final String REDO_ICON_FILENAME = "Redo.png";

    public static final String CUT_ICON_FILENAME = "Cut.png";

    public static final String COPY_ICON_FILENAME = "Copy.png";

    public static final String PASTE_ICON_FILENAME = "Paste.png";

    public static final String INSERT_LABEL_ICON_FILENAME = "InsertString.png";

    public static final String INSERT_SIGDIFF_ICON_FILENAME = "InsertSig.png";

    public static final String INSERT_BREAK_ICON_FILENAME = "InsertBreak.png";

    public static final String INSERT_TIMING_ICON_FILENAME = "InsertTiming.png";

    public static final String BOUNDING_BOX_ICON_FILENAME = "BoundingBox.png";

    public static final String LOCK_ICON_FILENAME = "LockFigure.png";

    public static final String UNLOCK_ICON_FILENAME = "UnlockFigure.png";

    public static final String HELP_ICON_FILENAME = "Help.png";

    /**
     *
     */
    public static final int ALL_FIGURES = 0;

    public static final int FOCUSED_FIGURES_FOR_COPY = 1;

    public static final int FOCUSED_FIGURES_IN_BOUNDING_BOX = 2;

    public static final int FOCUSED_FIGURES_FOR_DUPLICATION = 3;

    /**
     *
     */
    public static final int PREVIEW = 0;

    public static final int EXPORT = 1;

    public static final int PRINT = 2;

    //
    // Default values
    //

    // width of viewport
    public static final float DEFAULT_VIEWPORT_WIDTH = 20.0f / SGIConstants.CM_POINT_RATIO;

    // height of viewport
    public static final float DEFAULT_VIEWPORT_HEIGHT = 20.0f / SGIConstants.CM_POINT_RATIO;

    // paper width
    public static final float DEFAULT_PAPER_WIDTH = (MediaSize.ISO.A4
            .getX(Size2DSyntax.MM) / 10.0f)
            / SGIConstants.CM_POINT_RATIO;

    // paper height
    public static final float DEFAULT_PAPER_HEIGHT = (MediaSize.ISO.A4
            .getY(Size2DSyntax.MM) / 10.0f)
            / SGIConstants.CM_POINT_RATIO;

    // paper color
    public static final Color DEFAULT_PAPER_COLOR = new Color(255, 255, 255);

    // grid line width
    public static final float DEFAULT_GRID_LINE_WIDTH = 1.0f;

    // grid interval
    public static final float DEFAULT_GRID_INTERVAL = 1.0f / SGIConstants.CM_POINT_RATIO;

    // grid line color
    public static final Color DEFAULT_GRID_LINE_COLOR = new Color(233, 215, 215);

    // grid visible
    public static final boolean DEFAULT_GRID_VISIBLE = true;

	public static final String COM_WINDOW = "Window";

    public static final String COM_PAPER_WIDTH = "PaperWidth";

    public static final String COM_PAPER_HEIGHT = "PaperHeight";

    public static final String COM_PAPER_SIZE = "PaperSize";

    public static final String COM_WINDOW_GRID_INTERVAL = "GridInterval";

    public static final String COM_WINDOW_GRID_LINE_WIDTH = "GridLineWidth";

    public static final String COM_WINDOW_GRID_VISIBLE = "GridVisible";

    public static final String COM_WINDOW_GRID_COLOR = "GridColor";

    public static final String COM_WINDOW_BACKGROUND_COLOR = "BackgroundColor";

    public static final String COM_IMAGE_FILE_PATH = "ImageFilePath";

    public static final String COM_IMAGE_LOCATION_X = "ImageX";

    public static final String COM_IMAGE_LOCATION_Y = "ImageY";

    public static final String COM_IMAGE_SCALING_FACTOR = "ImageScalingFactor";

}
