package jp.riken.brain.ni.samuraigraph.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The wrapper class of file writer using buffered writer.
 *
 */
public class SGBufferedFileReader {
	
    private FileInputStream fis = null;
    private InputStreamReader isr = null;
    private BufferedReader br = null;

	/**
     * Builds a file reader for a file of given path.
     * 
	 * @param path
     *           the file path to read
	 * @throws IOException
	 */
	public SGBufferedFileReader(final String path) throws IOException {
		this(path, SGUtilityText.detectCharacterSet(path));
	}

	/**
     * Builds a file reader for a file of given path with given character set.
     * 
	 * @param path
     *           the file path to read
	 * @throws IOException
	 */
	public SGBufferedFileReader(final String path, final String charsetName) throws IOException {
		super();
		String cName = (charsetName == null) ? SGIConstants.CHAR_SET_NAME_UTF8 : charsetName;
		fis = new FileInputStream(new File(path));
		isr = new InputStreamReader(fis, cName);
		br = new BufferedReader(isr);
	}

	/**
	 * Returns the buffered reader.
	 * 
	 * @return the buffered reader
	 */
	public BufferedReader getBufferedReader() {
		return this.br;
	}

	/**
	 * Closes this reader.
	 * 
	 */
    public void close() {
    	if (br != null) {
    		try {
				br.close();
			} catch (IOException e) {
			}
    	}
    	if (isr != null) {
    		try {
				isr.close();
			} catch (IOException e) {
			}
    	}
    	if (fis != null) {
    		try {
				fis.close();
			} catch (IOException e) {
			}
    	}
    }
}
