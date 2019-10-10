package jp.riken.brain.ni.samuraigraph.application;

import jp.riken.brain.ni.samuraigraph.base.SGIFigureConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIRootObjectConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIAxisBreakConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIAxisConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIShapeConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISignificantDifferenceConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIStringConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGITimingLineConstants;

/**
 * The constants for command.
 */
public interface SGIApplicationCommandConstants extends SGIRootObjectConstants,
        SGIFigureConstants, SGIDataCommandConstants, SGIAxisConstants {
    
    /**
     * The status that a given command is failed.
     */
    public static final int STATUS_FAILED = -1;

    /**
     * The status that a given command is succeeded.
     */
    public static final int STATUS_SUCCEEDED = 0;

    /**
     * The status that a given command is not found.
     */
    public static final int STATUS_NOT_FOUND = 1;

    /**
     * The status that a given command is partially failed.
     */
    public static final int STATUS_PARTIALLY_FAILED = 2;

    /**
     * Default name of the script file.
     */
    public static final String DEFAULT_SCRIPT_FILE_NAME = "command";

    /**
     * File extension for the script files.
     */
    public static final String SCRIPT_FILE_EXTENSION = "sgs";
    
    /**
     * Description for the script file
     */
    public static final String SCRIPT_FILE_DESCRIPTION = "Samurai Graph Script File";

    //
    // Menu Bar
    //

    public static final String COM_LOAD_PROPERTY = "loadProperty";

    public static final String COM_SAVE_PROPERTY = "saveProperty";

    public static final String COM_LOAD_DATA_SET = "LoadDataSet";

    public static final String COM_SAVE_DATA_SET = "SaveDataSet";

    public static final String COM_SAVE_FILE_PATH = "FilePath";
    
    public static final String COM_SAVE_TYPE = "Type";

    public static final String COM_RELOAD_DATA = "Reload";
    
    public static final String COM_EXIT = "Exit";

    
    /**
     * A header for the menu command to bring the object to front.
     */
    public static final String COM_HEADER_BRING_TO_FRONT = "BringToFront";
    
    /**
     * A header for the menu command to bring the object forward.
     */
    public static final String COM_HEADER_BRING_FORWARD = "BringForward";

    /**
     * A header for the menu command to send the object backward.
     */
    public static final String COM_HEADER_SEND_BACKWARD = "SendBackward";
    
    /**
     * A header for the menu command to send the object to back.
     */
    public static final String COM_HEADER_SEND_TO_BACK = "SendToBack";
    

    //
    // window
    //
    
    public static final String COM_CLOSE_WINDOW = "CloseWindow";    


    //
    // figure
    //
    
    public static final String COM_BRING_TO_FRONT_FIGURE = COM_HEADER_BRING_TO_FRONT + SGIFigureConstants.COM_FIGURE;

    public static final String COM_BRING_FORWARD_FIGURE = COM_HEADER_BRING_FORWARD + SGIFigureConstants.COM_FIGURE;

    public static final String COM_SEND_BACKWARD_FIGURE = COM_HEADER_SEND_BACKWARD + SGIFigureConstants.COM_FIGURE;

    public static final String COM_SEND_TO_BACK_FIGURE = COM_HEADER_SEND_TO_BACK + SGIFigureConstants.COM_FIGURE;

    
    //
    // data
    //
    
    public static final String COM_BRING_TO_FRONT_DATA = COM_HEADER_BRING_TO_FRONT + SGIDataCommandConstants.COM_DATA;

    public static final String COM_BRING_FORWARD_DATA = COM_HEADER_BRING_FORWARD + SGIDataCommandConstants.COM_DATA;

    public static final String COM_SEND_BACKWARD_DATA = COM_HEADER_SEND_BACKWARD + SGIDataCommandConstants.COM_DATA;

    public static final String COM_SEND_TO_BACK_DATA = COM_HEADER_SEND_TO_BACK + SGIDataCommandConstants.COM_DATA;

    public static final String COM_MOVE_TO_TOP_DATA = "MoveToTopData";

    public static final String COM_MOVE_TO_UPPER_DATA = "MoveToUpperData";

    public static final String COM_MOVE_TO_LOWER_DATA = "MoveToLowerData";

    public static final String COM_MOVE_TO_BOTTOM_DATA = "MoveToBottomData";

    public static final String COM_DELETE_DATA = "DeleteData";

    
    //
    // label
    //
    
    public static final String COM_BRING_TO_FRONT_LABEL = COM_HEADER_BRING_TO_FRONT + SGIStringConstants.COM_LABEL;

    public static final String COM_BRING_FORWARD_LABEL = COM_HEADER_BRING_FORWARD + SGIStringConstants.COM_LABEL;

    public static final String COM_SEND_BACKWARD_LABEL = COM_HEADER_SEND_BACKWARD + SGIStringConstants.COM_LABEL;
    
    public static final String COM_SEND_TO_BACK_LABEL = COM_HEADER_SEND_TO_BACK + SGIStringConstants.COM_LABEL;
    
    
    //
    // significant difference symbols
    //
    
    public static final String COM_BRING_TO_FRONT_SIGDIFF = COM_HEADER_BRING_TO_FRONT + SGISignificantDifferenceConstants.COM_SIGNIFICANT_DIFFERENCE;

    public static final String COM_BRING_FORWARD_SIGDIFF = COM_HEADER_BRING_FORWARD + SGISignificantDifferenceConstants.COM_SIGNIFICANT_DIFFERENCE;

    public static final String COM_SEND_BACKWARD_SIGDIFF = COM_HEADER_SEND_BACKWARD + SGISignificantDifferenceConstants.COM_SIGNIFICANT_DIFFERENCE;
    
    public static final String COM_SEND_TO_BACK_SIGDIFF = COM_HEADER_SEND_TO_BACK + SGISignificantDifferenceConstants.COM_SIGNIFICANT_DIFFERENCE;


    //
    // axis break symbols
    //
    
    public static final String COM_BRING_TO_FRONT_BREAK = COM_HEADER_BRING_TO_FRONT + SGIAxisBreakConstants.COM_AXIS_BREAK;

    public static final String COM_BRING_FORWARD_BREAK = COM_HEADER_BRING_FORWARD + SGIAxisBreakConstants.COM_AXIS_BREAK;
    
    public static final String COM_SEND_BACKWARD_BREAK = COM_HEADER_SEND_BACKWARD + SGIAxisBreakConstants.COM_AXIS_BREAK;
    
    public static final String COM_SEND_TO_BACK_BREAK = COM_HEADER_SEND_TO_BACK + SGIAxisBreakConstants.COM_AXIS_BREAK;


    //
    // timing lines
    //
    
    public static final String COM_BRING_TO_FRONT_TIMING_LINE = COM_HEADER_BRING_TO_FRONT + SGITimingLineConstants.COM_TIMING_LINE;

    public static final String COM_BRING_FORWARD_TIMING_LINE = COM_HEADER_BRING_FORWARD + SGITimingLineConstants.COM_TIMING_LINE;

    public static final String COM_SEND_BACKWARD_TIMING_LINE = COM_HEADER_SEND_BACKWARD + SGITimingLineConstants.COM_TIMING_LINE;
    
    public static final String COM_SEND_TO_BACK_TIMING_LINE = COM_HEADER_SEND_TO_BACK + SGITimingLineConstants.COM_TIMING_LINE;
    
    
    //
    // shape
    //
    
    public static final String COM_BRING_TO_FRONT_SHAPE = COM_HEADER_BRING_TO_FRONT + SGIShapeConstants.COM_SHAPE;

    public static final String COM_BRING_FORWARD_SHAPE = COM_HEADER_BRING_FORWARD + SGIShapeConstants.COM_SHAPE;

    public static final String COM_SEND_BACKWARD_SHAPE = COM_HEADER_SEND_BACKWARD + SGIShapeConstants.COM_SHAPE;
    
    public static final String COM_SEND_TO_BACK_SHAPE = COM_HEADER_SEND_TO_BACK + SGIShapeConstants.COM_SHAPE;
    
    
   
    /**
     * The command to export as an image.
     */
    public static final String COM_EXPORT_IMAGE = "ExportAsImage";

    public static final String COM_EXPORT_IMAGE_FILE_PATH = "FilePath";
    
    public static final String COM_EXPORT_IMAGE_TYPE = "ImageType";

    public static final String COM_PRINT = "Print";

    /**
     * The command to fit axes to data.
     */
    public static final String COM_FIT_AXES = "FitAxes";
    
    public static final String COM_DATA_ID_LIST = "Data";
    
    public static final String COM_AXIS_DIRECTION = "Axes";
    
    /**
     * The command to align bars.
     */
    public static final String COM_ALIGN_BARS = "AlignBars";
    
    /**
     * The command to split data.
     */
    public static final String COM_SPLIT = "Split";
    
    /**
     * The command to merge data.
     */
    public static final String COM_MERGE = "Merge";
    
    /**
     * The command to insert label of netCDF data.
     */
    public static final String COM_INSERT_NETCDF_DATA_LABEL = "InsertNetCDFDataLabel";

//    /**
//     * The command to setup the line color map.
//     */
//    public static final String COM_SETUP_LINE_COLOR_MAP = "SetupLineColorMap";
    
    /**
     * The command to assign line color.
     */
    public static final String COM_ASSIGN_LINE_COLOR = "AssignLineColor";
    
    /**
     * The command to sleep the execution of commands.
     */
    public static final String COM_COMMAND_SLEEP = "Sleep";
    
}
