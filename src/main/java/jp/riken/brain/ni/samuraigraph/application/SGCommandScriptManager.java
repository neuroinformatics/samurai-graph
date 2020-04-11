package jp.riken.brain.ni.samuraigraph.application;

import java.io.File;
import java.io.IOException;

import jp.riken.brain.ni.samuraigraph.application.SGIApplicationConstants.FILE_TYPE;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

class SGCommandScriptManager implements SGIApplicationCommandConstants {

    private SGCommandScriptCreator mCommandScriptCreator = null;

    /**
     * The main functions.
     */
    private SGMainFunctions mMain = null;
    
    SGCommandScriptManager(SGMainFunctions main) {
    	super();
    	this.mMain = main;
    	this.mCommandScriptCreator = new SGCommandScriptCreator();
    }

	public int create(SGDrawingWindow wnd) {
		
    	// creates commands
    	String commandString = wnd.getCommandString(
    			new SGExportParameter(OPERATION.SAVE_TO_SCRIPT_FILE));

    	String fileName = SGApplicationUtility.getOutputFileName(wnd);
    	if (fileName == null) {
    		fileName = DEFAULT_SCRIPT_FILE_NAME;;
    	}
    	String name = SGApplicationUtility.appendExtension(
                fileName, SCRIPT_FILE_EXTENSION);
    	String dir = this.mMain.getCurrentFileDirectory();
    	
        // set the selected file name
        this.mCommandScriptCreator.setCurrentFile(dir, name);

        // create a command script file
        int ret;
        try {
			ret = this.mCommandScriptCreator.create(wnd, commandString);
		} catch (IOException e) {
            SGUtility.showErrorMessageDialog(wnd, e.getMessage(), TITLE_ERROR);
            ret = ERROR_OPTION;
		}
        if (ret != OK_OPTION) {
            return ret;
        }
        
        File f = this.mCommandScriptCreator.getCurrentFile();
        if (f != null) {
        	this.mMain.updateCurrentFile(f, FILE_TYPE.SCRIPT);
        }

        return OK_OPTION;
	}

}
