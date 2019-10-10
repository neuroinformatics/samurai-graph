package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;

public class SGFileHandler implements SGIConstants {

    /**
     * Current directory.
     */
    protected String mCurrentDirectory = null;

    /**
     * Current file name.
     */
    protected String mCurrentFileName = null;

    /**
     * The default constructor.
     */
	public SGFileHandler() {
		super();
	}
	
    // Sets default directory name and file name.
	private String initDirectory() {
        String dir = USER_HOME;
        String md = MY_DOCUMENTS; // for windows
        File home = new File(dir);
        String[] fList = home.list();
        if (fList == null) {
            throw new Error();
        }
        // for windows
        StringBuffer sb = new StringBuffer();
        sb.append(dir);
        for (int ii = 0; ii < fList.length; ii++) {
            if (fList[ii].endsWith(md)) {
                sb.append(FILE_SEPARATOR);
                sb.append(md);
                break;
            }
        }
        dir = sb.toString();
        return dir;
	}

    protected void initFilePath() {
    	this.mCurrentDirectory = this.initDirectory();
    	this.mCurrentFileName = null;
    }
    
    protected void initFilePath(final String defaultName,
    		final String extension) {
        this.mCurrentDirectory = this.initDirectory();
        this.mCurrentFileName = SGApplicationUtility.appendExtension(
        		defaultName, extension);
    }

    /**
     * Returns the current file.
     * 
     * @return the current file
     */
    public File getCurrentFile() {
    	if (this.mCurrentDirectory != null && this.mCurrentFileName != null) {
            return new File(this.mCurrentDirectory, this.mCurrentFileName);
    	} else if (this.mCurrentDirectory != null) {
    		return new File(this.mCurrentDirectory);
    	} else {
    		return null;
    	}
    }

    /**
     * Sets the current file.
     * 
     * @param dir
     *            directory name
     * @param name
     *            file name
     * @return a File object
     */
    public File setCurrentFile(String dir, String name) {
        if (name == null) {
        	name = "";
        }
        this.mCurrentFileName = name;
        this.mCurrentDirectory = dir;

        // create the full path name
        String path = SGApplicationUtility.getPathName(
                this.mCurrentDirectory, this.mCurrentFileName);
        File f = new File(path);

        return f;
    }

    /**
     * Sets the current file name.
     * 
     * @param name
     *           file name
     */
    public void setCurrentFileName(final String name) {
        this.mCurrentFileName = name;
    }

    /**
     * Sets the current directory.
     * 
     * @param dir
     *           directory name
     */
    public void setCurrentDirectory(final String dir) {
        this.mCurrentDirectory = dir;
    }
    
    private JFileChooser createFileChooser(String ext, String desc, String defaultFileName) {
    	JFileChooser chooser = SGApplicationUtility.createFileChooser(
    			this.mCurrentDirectory, this.mCurrentFileName, 
    			ext, desc, defaultFileName);
    	return chooser;
    }

    protected File selectOutputFile(Component parent,
    		String ext, String desc, String defaultFileName) throws IOException {
    	
    	// creates a file chooser
    	JFileChooser chooser = this.createFileChooser(ext, desc, defaultFileName);

        // shows the save dialog
        final int ret = chooser.showSaveDialog(parent);
        
        File file = null;
        if (ret == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            this.setSelectedFile(file);
        }

        return file;
    }
    
    protected void setSelectedFile(File file) throws IOException {
        File parentFile = file.getParentFile();
        if (parentFile != null) {
        	if (!parentFile.exists()) {
            	throw new IOException("Invalid file path: " + file.getPath());
        	}
        	if (!parentFile.canWrite()) {
            	throw new IOException("Invalid file path: " + file.getPath());
        	}
        }
        String name = file.getName();
        name = SGApplicationUtility.removeExtension(name);
        if (!SGApplicationUtility.checkOutputFileName(name)) {
        	throw new IOException("Invalid file name: " + name);
        }
        this.mCurrentDirectory = file.getParent();
        this.mCurrentFileName = file.getName();
    }

}
