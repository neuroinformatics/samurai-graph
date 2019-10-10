package jp.riken.brain.ni.samuraigraph.application;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import jp.riken.brain.ni.samuraigraph.base.SGExtensionFileFilter;

/**
 * A wizard dialog to choose a file.
 */
public abstract class SGFileChooserWizardDialog extends SGWizardDialog {

    private static final long serialVersionUID = -7199760553826426756L;
    
    protected String mCurrentFileName = null;
    
    protected String mCurrentDirectory = null;
    
    protected SGExtensionFileFilter mFileFilter = null;
    
    protected File[] mSelectedFiles = null;

    /**
     * Creates new form SGFileChooserWizardDialog
     * 
     */
    public SGFileChooserWizardDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    /**
     * Creates new form SGFileChooserWizardDialog
     * 
     */
    public SGFileChooserWizardDialog(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }

    /**
     * Sets the file path and returns a file.
     * 
     * @param dir
     *           directory name
     * @param name
     *           file name
     * @return a file object
     */
    public void setCurrentFile(String dir, String name) {
        if (name == null) {
            name = "";
        }
        this.mCurrentDirectory = dir;
        this.mCurrentFileName = name;
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

    /**
     * Sets the file name and returns a file.
     * 
     * @param path
     *             file path
     * @return
     *             a file object
     */
    public File setSelectedFile(String path) {
        File f = new File(path);
        return this.setSelectedFile(f);
    }

    /**
     * Sets the file name and returns a file.
     * 
     * @param path
     *             file path
     * @return
     *             a file object
     */
    public File setSelectedFile(File f) {
        if (f.exists()) {
        	this.mSelectedFiles = new File[] { f };
        }
        return f;
    }

    /**
     * Returns the selected file.
     * 
     * @return the selected file
     */
    public File getSelectedFile() {
    	return (this.mSelectedFiles != null) ? this.mSelectedFiles[0] : null;
    }

    /**
     * Sets the current file filter to the attribute.
     * 
     * @param ff
     */
    public void setFileFilter(SGExtensionFileFilter ff) {
    	this.mFileFilter = ff;
    }

    /**
     * 
     */
    long mShowFileChooserTime = 0L;

    long lastUsed() {
        return this.mShowFileChooserTime;
    }

    /**
     * Open the file chooser dialog.
     * 
     * @return selected files
     */
    protected File[] openFileChooser() {
        // show open dialog
        this.mShowFileChooserTime = System.currentTimeMillis();
        
        final String exp;
        final String[] extArray;
        if (this.mFileFilter != null) {
        	exp = this.mFileFilter.getExplanation();
        	List<String> extList = this.mFileFilter.getExtensionList();
        	extArray = new String[extList.size()];
        	extList.toArray(extArray);
        } else {
        	exp = null;
        	extArray = null;
        }
        
        JFileChooser chooser = SGApplicationUtility.createFileChooser(this.mCurrentDirectory, 
        		this.mCurrentFileName, extArray, exp, "");
        
        final int ret = chooser.showOpenDialog(this);

        // get selected files
        File[] files = null;
        switch (ret) {
        // selected
        case JFileChooser.APPROVE_OPTION: {
            if (chooser.isMultiSelectionEnabled()) {
                files = chooser.getSelectedFiles();
            } else {
                files = new File[] { chooser.getSelectedFile() };
            }
            break;
        }

        // canceled
        case JFileChooser.CANCEL_OPTION: {
            break;
        }

        // error
        case JFileChooser.ERROR_OPTION: {
            return null;
        }

        default: {

        }
        }
        
        this.mSelectedFiles = (files != null) ? files.clone() : null;

        return files;
    }
    
    /**
     * Returns whether a given file is acceptable as a property file.
     * @param f
     *         a file
     * @return
     *         true if a given file is acceptable
     */
    protected boolean isAcceptable(File f) {
        // the file exists and is a file
        return (f.exists()) && (f.isFile());
    }

    /**
     * Returns whether a given file is acceptable as a property file.
     * @param fileName
     *         the name of a file
     * @return
     *         true if a given file is acceptable
     */
    protected boolean isAcceptable(String fileName) {
        File f = new File(fileName);
        return this.isAcceptable(f);
    }
}
