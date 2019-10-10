package jp.riken.brain.ni.samuraigraph.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

/**
 * A class of data column for multidimensional data: HDF5 and MATLAB type.
 *
 */
public class SGMDArrayDataColumnInfo extends SGDataColumnInfo implements SGIMDArrayConstants {

    /**
     * The name of variable.
     */
    private String mVariableName = null;

    /**
     * Array of dimensions.
     */
    private int[] mDimensions = null;
    
    /**
     * The map of dimension indices.
     */
    protected Map<String, Integer> mDimensionIndices = null;
    
    /**
     * The origins of a multidimensional variables.
     */
    private int[] mOrigins = null;
    
    /**
     * Builds a data column object with a given title and the value type.
     * 
     * @param var
     *            a variable
     * @param title
     *            a string to set to the title of this column
     * @param valueType
     *            a value to set to the type of value of this column
     */
    public SGMDArrayDataColumnInfo(final SGMDArrayVariable var, final String title, final String valueType) {
        super(title, valueType);
        this.mVariableName = var.getName();
        this.mDimensions = var.getDimensions().clone();
        this.mOrigins = var.getOrigins().clone();
        this.mDimensionIndices = new HashMap<String, Integer>(var.getDimensionIndices());
        this.mAttributeList.addAll(var.getAttributes());
    }

    /**
     * Builds a data column object with a given title, value type and the origin.
     * 
     * @param var
     *            a variable
     * @param title
     *            a string to set to the title of this column
     * @param valueType
     *            a value to set to the type of value of this column
     * @param origins
     *            the origins
     */
    public SGMDArrayDataColumnInfo(final SGMDArrayVariable var, final String title, final String valueType,
    		final int[] origins) {
        this(var, title, valueType);
        this.mOrigins = origins.clone();
    }

    /**
     * Builds a data column object with given information, title and value type.
     * 
     * @param info
     *           information for a netCDF variable
     * @param title
     *           the title
     * @param valueType
     *           the value type
     */
    public SGMDArrayDataColumnInfo(final SGMDArrayDataColumnInfo info, final String title, final String valueType) {
    	super(title, valueType);
    	this.mVariableName = info.getName();
    	this.mDimensions = info.getDimensions().clone();
    	this.mOrigins = info.getOrigins().clone();
    	this.mDimensionIndices = new HashMap<String, Integer>(info.getDimensionIndices());
    	this.mAttributeList.addAll(info.mAttributeList);
    }

    /**
     * Returns the origins.
     * 
     * @return the origins
     */
    public int[] getOrigins() {
        return this.mOrigins.clone();
    }

    /**
     * Sets the origins.
     * 
     * @param origins
     *           the origins to set
     */
    public void setOrigins(final int[] origins) {
        this.mOrigins = origins.clone();
    }
    
    public void setOrigin(final int dimensionIndex, final int origin) {
    	if (dimensionIndex < 0 || dimensionIndex >= this.mOrigins.length) {
    		throw new IllegalArgumentException("Dimension index is out of bounds: " + dimensionIndex);
    	}
    	final int len = this.mDimensions[dimensionIndex];
    	if (origin < 0 || origin >= len) {
    		throw new IllegalArgumentException("Origin is out of bounds: " + origin);
    	}
    	this.mOrigins[dimensionIndex] = origin;
    }

    /**
     * Returns the name of variable.
     * 
     * @return the name variable
     */
    public String getName() {
    	return this.mVariableName;
    }
    
    /**
     * Returns a text string for this object.
     * 
     * @return a text string for this object
     */
    protected String paramString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.paramString());
        sb.append(", origins=");
        sb.append(Arrays.toString(this.mOrigins));
        sb.append(", dimension=");
        sb.append(this.mDimensionIndices);
        return sb.toString();
    }

    /**
     * Returns the length.
     * 
     * @return the length
     */
    public int[] getDimensions() {
    	return this.mDimensions.clone();
    }

    /**
     * Returns the dimension index of given name.
     * 
     * @param key
     *           the name
     * @return the dimension index
     */
    public Integer getDimensionIndex(final String key) {
    	return this.mDimensionIndices.get(key);
    }
    
    /**
     * Returns the generic dimension index.
     * 
     * @return the generic dimension index
     */
    public Integer getGenericDimensionIndex() {
    	return this.getDimensionIndex(KEY_GENERIC_DIMENSION);
    }

    /**
     * Returns the dimension index of the time.
     * 
     * @return the dimension index of the time
     */
    public Integer getTimeDimensionIndex() {
    	return this.getDimensionIndex(KEY_TIME_DIMENSION);
    }

    /**
     * Returns the map of dimension indices.
     * 
     * @return the map of dimension indices
     */
    public Map<String, Integer> getDimensionIndices() {
    	return new HashMap<String, Integer>(this.mDimensionIndices);
    }

    /**
     * Sets the dimension index of given name.
     * 
     * @param key
     *           the name
     * @param index
     *           the index to set
     */
    public void setDimensionIndex(final String key, final int index) {
    	this.mDimensionIndices.put(key, index);
    }
    
    /**
     * Puts all dimension indices.
     * 
     * @param map
     *          a map of dimension indices
     */
    public void putAllDimensionIndices(Map<String, Integer> map) {
    	this.mDimensionIndices.putAll(map);
    }
    
    /**
     * Clears dimension maps. Sets -1 to values of all entries.
     * 
     */
    public void clearDimensionIndices() {
    	Iterator<Entry<String, Integer>> itr = this.mDimensionIndices.entrySet().iterator();
    	while (itr.hasNext()) {
    		Entry<String, Integer> entry = itr.next();
    		entry.setValue(-1);
    	}
    }

    /**
     * Sets the dimension indices.
     * 
     * @param map
     *          a map of dimension indices
     */
    public void setDimensionIndices(Map<String, Integer> map) {
    	this.mDimensionIndices = new HashMap<String, Integer>(map);
    }

    /**
     * Clears dimension indices of given name.
     * 
     * @param key
     *           the name
     */
    public void clearDimensionIndex(final String key) {
    	this.mDimensionIndices.put(key, -1);
    }

    /**
     * Sets the generic dimension index.
     * 
     * @param index
     *           the dimension index
     */
    public void setGenericDimensionIndex(final int index) {
    	this.setDimensionIndex(KEY_GENERIC_DIMENSION, index);
    }
    
    /**
     * Returns the length of generic dimension.
     * 
     * @return the length of generic dimension
     */
    public int getGenericDimensionLength() {
    	Integer index = this.getGenericDimensionIndex();
    	if (!SGDataUtility.isValidDimensionIndex(index)) {
    		return -1;
    	}
    	return this.mDimensions[index.intValue()];
    }
    
    /**
     * Overrode to copies attributes.
     * 
     * @return a copied object
     */
    @Override
    public Object clone() {
    	SGMDArrayDataColumnInfo info = (SGMDArrayDataColumnInfo) super.clone();
    	info.mDimensionIndices = new HashMap<String, Integer>(this.mDimensionIndices);
    	info.mDimensions = this.mDimensions.clone();
    	info.mOrigins = this.mOrigins.clone();
    	return info;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (!(obj instanceof SGMDArrayDataColumnInfo)) {
    		return false;
    	}
    	if (!super.equals(obj)) {
    		return false;
    	}
    	SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) obj;
    	if (!SGUtility.equals(this.mVariableName, col.mVariableName)) {
    		return false;
    	}
    	if (!SGUtility.equals(this.mDimensions, col.mDimensions)) {
    		return false;
    	}
    	if (!SGUtility.equals(this.mDimensionIndices, col.mDimensionIndices)) {
    		return false;
    	}
    	if (!SGUtility.equals(this.mOrigins, col.mOrigins)) {
    		return false;
    	}
    	return true;
    }

}
