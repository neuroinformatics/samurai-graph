package jp.riken.brain.ni.samuraigraph.application;

import java.io.File;
import java.io.IOException;

import jp.riken.brain.ni.samuraigraph.base.SGBufferedFileWriter;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;


public class SGCommandScriptCreator extends SGFileHandler 
		implements SGIApplicationCommandConstants {

    /**
     * Default name of the property file with the extension.
     */
	public static final String DEFAULT_SCRIPT_FILE_NAME_WITH_EXTENSION = SGApplicationUtility.appendExtension(
			DEFAULT_SCRIPT_FILE_NAME, SCRIPT_FILE_EXTENSION);

	SGCommandScriptCreator() {
		super();
        this.initFilePath(DEFAULT_SCRIPT_FILE_NAME, SCRIPT_FILE_EXTENSION);
	}

    public int create(final SGDrawingWindow wnd, final String commandString) throws IOException {
        // show a file chooser and get selected file
        File file = this.selectOutputFile(wnd, SCRIPT_FILE_EXTENSION, SCRIPT_FILE_DESCRIPTION, 
        		DEFAULT_SCRIPT_FILE_NAME_WITH_EXTENSION);
        if (file == null) {
            return CANCEL_OPTION;
        }
        
    	SGBufferedFileWriter bw = null;
    	try {
			bw = new SGBufferedFileWriter(file.getPath(), SGIConstants.CHAR_SET_NAME_UTF8);
			bw.getBufferedWriter().write(commandString);
		} catch (IOException e1) {
			return ERROR_OPTION;
		} finally {
			if (bw != null) {
				bw.close();
			}
		}
        return OK_OPTION;
    }

}
