package jp.riken.brain.ni.samuraigraph.application;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;

/**
 * Messages used in application.
 * 
 */
public interface SGIApplicationTextConstants {

    public static final String MSG_SUCCESSFUL_COMPLETION = "Successful completion.";

    public static final String MSG_DATA_FILE_OPEN_FAILURE = "Failed to open the data file.";

    public static final String MSG_INVALID_DATA_FILE = "Invalid data file.";

    public static final String MSG_INVALID_DATA_TYPE = "Invalid data type.";

    public static final String MSG_DATA_NUMBER_SHORTAGE = "Data number shortage.";
    
    public static final String MSG_DATA_NUMBER_EXCESS = "Data number excess.";

    public static final String MSG_FILE_OPEN_FAILURE = "Failed to open selected file.";

    public static final String MSG_PROPERTY_FILE_INVALID = "Invalid property file.";

    public static final String MSG_DATA_SET_FILE_INVALID = "Failed to open selected data set file.";

    public static final String MSG_INVALID_INPUT_VALUE = SGIConstants.INVALID_INPUT_VALUE;

    public static final String MSG_CLOSE_WITHOUT_SAVING = "Close without saving";

    public static final String MSG_DISCARD_WITHOUT_SAVING = "Discard without saving";
    
    public static final String MSG_UNKNOWN_ERROR_OCCURED = "Unknown error occured";

    /**
     * Error message for the values out of range in a HDF5 file.
     */
    public static final String MSG_HDF5_VALUES_OUT_OF_RANGE = "Failed to read the HDF5 file:\nvalues are out of bounds.";

    public static final String TITLE_FILE_OPEN_FAILURE = "File open failure";

    public static final String TAG_NAME_FOCUSED_FIGURES = "FocusedFigures";

}
