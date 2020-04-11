package jp.riken.brain.ni.samuraigraph.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

/**
 * The file filter class to handle files with given file extensions.
 * 
 */
public class SGExtensionFileFilter extends FileFilter {

    /**
     * The description of the file format.
     */
    private String mExplanation = "";

    /**
     * The list of file extension.
     */
    private List<String> mExtensionsList = new ArrayList<String>();

    /**
     * Add an file extension.
     * 
     * @param extension
     */
    public void addExtension(String extension) {
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }
        this.mExtensionsList.add(extension.toLowerCase());
    }

    /**
     * Sets explanation for the file format.
     * 
     * @param exp
     *           explanation for the file format
     */
    public void setExplanation(String exp) {
        this.mExplanation = exp;
    }

    /**
     * Returns a text string of explanation of this file filter.
     * 
     * @return a text string of explanation of this file filter
     */
    public String getExplanation() {
    	return this.mExplanation;
    }
    
    /**
     * Returns the description of this filter.
     * 
     * @return the description of this filter
     */
    public String getDescription() {
        StringBuffer sb = new StringBuffer();
        if (this.mExtensionsList.size() != 0) {
	        for (int ii = 0; ii < this.mExtensionsList.size(); ii++) {
	            if (ii > 0) {
	                sb.append(", ");
	            }
	            sb.append('*');
	            sb.append(this.mExtensionsList.get(ii));
	        }
        } else {
            sb.append("*.*");
        }
        StringBuffer ret = new StringBuffer();
        ret.append(this.mExplanation);
        ret.append(" (");
        ret.append(sb.toString());
        ret.append(')');
        return ret.toString();
    }

    /**
     * The list of file extensions.
     * 
     * @return
     */
    public List<String> getExtensionList() {
        return new ArrayList<String>(this.mExtensionsList);
    }

    /**
     * Whether the given file is accepted by this filter.
     */
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }

        String name = file.getName().toLowerCase();

        // check if the file name ends with any of the extensions
        for (String ext : this.mExtensionsList) {
            if (name.endsWith(ext)) {
                return true;
            }
        }

        return false;
    }

}
