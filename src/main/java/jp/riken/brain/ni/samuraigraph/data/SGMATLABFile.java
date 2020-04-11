package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.riken.brain.ni.samuraigraph.base.SGAttribute;

import com.jmatio.io.MatFileHeader;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;

/**
 * The wrapper class for a MATLAB file.
 *
 */
public class SGMATLABFile extends SGMDArrayFile {

	/**
	 * The reader for MATLAB files.
	 */
	private MatFileReader mReader = null;
	
    /**
     * The path of data file.
     */
    private String mFilePath = null;
    
    /**
     * Builds a MATLAB file object.
     * 
     * @param filePath
     *           the file path
     * @param reader
     *           the MATLAB file reader
     */
    public SGMATLABFile(String filePath, MatFileReader reader) {
        super();
        if (filePath == null) {
            throw new IllegalArgumentException("filePath == null");
        }
        if (reader == null) {
            throw new IllegalArgumentException("reader == null");
        }
        this.mFilePath = filePath;
        this.mReader = reader;
        
		// create variables
		Map<String, MLArray> content = reader.getContent();
		Set<Entry<String, MLArray>> entrySet = content.entrySet();
		Iterator<Entry<String, MLArray>> itr = entrySet.iterator();
		int cnt = 0;
		List<SGMATLABVariable> dsList = new ArrayList<SGMATLABVariable>();
		while (itr.hasNext()) {
			Entry<String, MLArray> entry = itr.next();
			String key = entry.getKey();
			MLArray mlArray = entry.getValue();
			if (mlArray.isDouble()) {
				dsList.add(new SGMATLABVariable(this, key));
				cnt++;
			}
		}
		this.mVariables = new SGMATLABVariable[cnt];
		this.mVariables = dsList.toArray(this.mVariables);
    }

    /**
     * Returns the reader.
     * 
     * @return the reader
     */
    public MatFileReader getReader() {
    	return this.mReader;
    }

    /**
     * Disposes of this object.
     * 
     */
	@Override
	public void dispose() {
		super.dispose();
		this.mFilePath = null;
		this.mReader = null;
	}

    /**
     * Returns the file path.
     * 
     * @return the file path
     */
	@Override
    public String getPath() {
        return this.mFilePath;
    }

    /**
     * Returns the list of attributes picked up from the header of MATLAB file.
     * 
     * @return the list of attributes picked up from the header of MATLAB file
     */
	@Override
	public List<SGAttribute> getAttributes() {
		MatFileHeader header = this.mReader.getMatFileHeader();
		final String desc = header.getDescription();
		final String version = Integer.toString(header.getVersion());
		final String ei = new String(header.getEndianIndicator());
		List<SGAttribute> aList = new ArrayList<SGAttribute>();
		aList.add(new SGAttribute("descriptive text", desc));
		aList.add(new SGAttribute("version", version));
		aList.add(new SGAttribute("endianIndicator", ei));
		return aList;
	}

	/**
	 * Returns true if the reader object is equal.
	 * 
	 * @param obj
	 *          an object to be compared
	 * @return true if the reader object e is equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof SGMDArrayFile)) {
			return false;
		}
		SGMATLABFile matFile = (SGMATLABFile) obj;
		if (!this.mFilePath.equals(matFile.mFilePath)) {
			return false;
		}
		return true;
	}

}
