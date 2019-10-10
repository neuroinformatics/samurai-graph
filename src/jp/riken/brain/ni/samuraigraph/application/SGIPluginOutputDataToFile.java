package jp.riken.brain.ni.samuraigraph.application;

import java.io.File;
import java.io.IOException;

import jp.riken.brain.ni.samuraigraph.base.SGData;

public interface SGIPluginOutputDataToFile {
    
    /**
     * Return a description for file chooser.
     * 
     * @return description for file chooser
     */
    public String getDescription();
    
    /**
     * Return a file extension.
     * 
     * @return file extension
     */
    public String getExtension();
    
    /**
     * Return a filetype name.
     * 
     * @return filetype name
     */
    public String getFileTypeName();
    
    /**
     * Write contents of given data to given file.
     * 
     * @param file
     * @param data
     * @throws IOException
     */
    public void writeData(final File file, final SGData data) throws IOException;

}
