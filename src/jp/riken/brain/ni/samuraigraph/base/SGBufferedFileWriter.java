package jp.riken.brain.ni.samuraigraph.base;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * The wrapper class of file reader using buffered reader.
 *
 */
public class SGBufferedFileWriter {

    private FileOutputStream fos = null;
    private OutputStreamWriter osw = null;
    private BufferedWriter bw = null;

	/**
     * Builds a file writer for a file of given path.
     * 
	 * @param path
     *           path of the file to write to
	 * @throws IOException
	 */
	public SGBufferedFileWriter(final String path) throws IOException {
		this(path, SGUtilityText.detectCharacterSet(path));
	}

	/**
     * Builds a file writer for a file of given path with given character set.
     * 
	 * @param path
     *           path of the file to write to
     * @param charsetName
     *           the name of character set
	 * @throws IOException
	 */
	public SGBufferedFileWriter(final String path, final String charsetName) throws IOException {
		super();
		String cName = (charsetName == null) ? SGIConstants.CHAR_SET_NAME_UTF8 : charsetName;
		fos = new FileOutputStream(new File(path));
		osw = new OutputStreamWriter(fos, cName);
		bw = new BufferedWriter(osw);
	}

	/**
	 * Returns the buffered writer.
	 * 
	 * @return the buffered writer
	 */
	public BufferedWriter getBufferedWriter() {
		return this.bw;
	}
    
	/**
	 * Closes this writer.
	 * 
	 */
    public void close() {
    	if (bw != null) {
    		try {
				bw.close();
			} catch (IOException e) {
			}
    	}
    	if (osw != null) {
    		try {
				osw.close();
			} catch (IOException e) {
			}
    	}
    	if (fos != null) {
    		try {
				fos.close();
			} catch (IOException e) {
			}
    	}
    }
}
