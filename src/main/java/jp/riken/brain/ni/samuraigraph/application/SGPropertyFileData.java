package jp.riken.brain.ni.samuraigraph.application;

import java.util.HashMap;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;

/**
 * A class that represents a single data in a property file.
 *
 */
public class SGPropertyFileData {

    /**
     * The ID of figure this data belongs.
     */
    private int figureId = -1;
    
    /**
     * The name of data.
     */
    private String dataName = null;
    
    /**
     * The type of data.
     */
    private String dataType = null;

    /**
     * The file name.
     */
    private String fileName = null;

    /**
     * A map of information.
     */
    private Map<String, Object> infoMap = null;
    
    /**
     * The size of figure.
     */
    private SGTuple2f figureSize = null;
    
    private SGDataColumnInfoSet mColInfoSet = null;
    
    /**
     * Build a property file data object with given values.
     * @param figureId
     *                 a value set to the figure ID
     * @param dataType
     *                 a value set to the data type
     * @param dataNam
     *                 a value set to the name
     * @param infoMap
     *                 a map of information of this data
     */
    public SGPropertyFileData(final int figureId,
    		final String dataType, String dataName, Map<String, Object> infoMap) {
		super();
		this.figureId = figureId;
		this.dataName = dataName;
		this.dataType = dataType;
		if (infoMap == null) {
		    this.infoMap = new HashMap<String, Object>();
		} else {
		    this.infoMap = new HashMap<String, Object>(infoMap);
		}
    }

    /**
     * Returns the name of data.
     * @return
     *         the name of data
     */
    public String getDataName() {
        return dataName;
    }

    /**
     * Returns the type of data.
     * @return
     *         the type of data
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Returns the figure ID.
     * @return
     *         the figure ID
     */
    public int getFigureId() {
        return figureId;
    }

    /**
     * Returns a map of data information.
     * @return
     *         a map of data information
     */
    public Map<String, Object> getInfoMap() {
        return new HashMap<String, Object>(infoMap);
    }

    /**
     * Returns the file name.
     * @return
     *         the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name.
     * @param fileName
     *          a string to set to the file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public SGDataColumnInfoSet getColumnInfoSet() {
    	if (this.mColInfoSet != null) {
    		return (SGDataColumnInfoSet) this.mColInfoSet.clone();
    	} else {
    		return null;
    	}
    }
    
    public void setColumnInfoSet(SGDataColumnInfoSet colInfoSet) {
    	if (colInfoSet != null) {
    		this.mColInfoSet = (SGDataColumnInfoSet) colInfoSet.clone();
    	} else {
    		this.mColInfoSet = null;
    	}
    }

    /**
     * Returns the size of figure.
     * 
     * @return the size of figure
     */
    public SGTuple2f getFigureSize() {
    	return new SGTuple2f(this.figureSize);
    }

    /**
     * Sets the figure size.
     * 
     * @param figureSize
     *           the figure size
     */
    public void setFigureSize(SGTuple2f figureSize) {
		this.figureSize = new SGTuple2f(figureSize);
    }
}
