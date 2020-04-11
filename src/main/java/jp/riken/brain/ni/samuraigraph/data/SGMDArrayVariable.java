package jp.riken.brain.ni.samuraigraph.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import jp.riken.brain.ni.samuraigraph.base.SGIDisposable;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/**
 * The base class for the multidimensional variable.
 *
 */
public abstract class SGMDArrayVariable extends SGVariable implements Cloneable,
		SGIDisposable, SGIMDArrayConstants {
	
	/**
	 * The data file.
	 */
	protected SGMDArrayFile mFile = null;

	/**
	 * Fully qualified name of this variable.
	 */
	protected String mName = null;
	
    /**
     * The array of origins for each dimension.
     */
    protected int[] mOrigins = null;
    
    /**
     * The map of selected dimension indices in a data set.
     */
    protected Map<String, Integer> mDimensionIndices = new HashMap<String, Integer>();
    
	/**
	 * The default constructor.
	 */
	public SGMDArrayVariable() {
		super();
		
		// initializes the dimension indices
		this.mDimensionIndices.put(KEY_GENERIC_DIMENSION, -1);
		this.mDimensionIndices.put(KEY_TIME_DIMENSION, -1);
	}

	/**
	 * Returns the file.
	 * 
	 * @return the file
	 */
	public SGMDArrayFile getFile() {
		return this.mFile;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.mName;
	}

	/**
	 * Returns the simple name.
	 * 
	 * @return the simple name
	 */
	public String getSimpleName() {
		final String name = SGUtilityText.lastSubstring(this.mName, '/');
		if (name == null) {
			return this.mName;
		} else {
			return name;
		}
	}

	/**
	 * Returns the dimension index.
	 * 
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
		final int len = this.getDimensions().length;
		if (len == 0) {
			return;
		}
		this.mDimensionIndices.put(key, Integer.valueOf(index));
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
     * Sets the dimension index for the time.
     * 
     * @param index
     *           the dimension index
     */
	public void setTimeDimensionIndex(final int index) {
		this.setDimensionIndex(KEY_TIME_DIMENSION, index);
	}

	/**
	 * Sets the given map of dimension indices.
	 * 
	 * @param map
	 *           a map of dimension indices
	 */
	public void setDimensionIndices(final Map<String, Integer> map) {
		if (map == null) {
			throw new IllegalArgumentException("map == null");
		}
		Set<String> keySet = map.keySet();
		if (!keySet.contains(KEY_GENERIC_DIMENSION)) {
			throw new IllegalArgumentException("Generic dimension is not contained.");
		}
		this.mDimensionIndices = new HashMap<String, Integer>(map);
		if (!keySet.contains(KEY_TIME_DIMENSION)) {
			this.mDimensionIndices.put(KEY_TIME_DIMENSION, -1);
		}
	}

    /**
     * Returns the origin array.
     * 
     * @return the origin array
     */
    public int[] getOrigins() {
    	return this.mOrigins.clone();
    }
    
    /**
     * Sets the origins.
     * 
     * @param origins
     *           an array of origins
     */
    public void setOrigins(final int[] origins) {
    	if (origins == null) {
    		throw new IllegalArgumentException("origins == null");
    	}
		int[] dims = this.getDimensions();
		if (origins.length != dims.length) {
			throw new IllegalArgumentException("Length of given origin array is invalid: " + origins.length);
		}
		for (int ii = 0; ii < origins.length; ii++) {
			if (dims[ii] == 0) {
				continue;
			}
			if (origins[ii] < 0 || dims[ii] <= origins[ii]) {
				throw new IllegalArgumentException("Invalid origin: " + origins[ii]);
			}
		}
		this.mOrigins = origins.clone();
    }
    
	/**
	 * Returns the dimensions.
	 * 
	 * @return the dimensions
	 */
	public abstract int[] getDimensions();

	/**
	 * Returns a text string to represent this object.
	 * 
	 * @return a text string to represent this object
	 */
	@Override
	public String toString() {
		return this.getName();
	}

	/**
	 * Returns true if the data file and the variable name is equal.
	 * 
	 * @param obj
	 *          an object to be compared
	 * @return true if the data file and the variable name is equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (!(obj instanceof SGMDArrayVariable)) {
			return false;
		}
		SGMDArrayVariable ds = (SGMDArrayVariable) obj;
		if (!SGUtility.equals(this.mFile, ds.mFile)) {
			return false;
		}
		if (!SGUtility.equals(this.mName, ds.mName)) {
			return false;
		}
		return true;
	}

    /**
     * Returns the list of attributes.
     * 
     * @return the list of attributes
     */
	public abstract List<SGAttribute> getAttributes();

	/**
	 * Disposes of this object.
	 * 
	 */
	@Override
	public void dispose() {
		super.dispose();
		this.mFile = null;
		this.mName = null;
		this.mOrigins = null;
	}	
	
	/**
	 * Returns the number array of the generic dimension.
	 * 
	 * @param stride
	 *           stride of the array
	 * @return the number array of the generic dimension
	 */
	public double[] getGenericNumberArray(final SGIntegerSeriesSet stride) {
		return this.getDoubleArray(this.mDimensionIndices.get(KEY_GENERIC_DIMENSION),
				stride);
	}

	/**
	 * Returns the number array of all values of the generic dimension.
	 * 
	 * @return the number array of all values of the generic dimension
	 */
	public double[] getAllGenericNumberArray() {
		return this.getGenericNumberArray(null);
	}

	/**
	 * Returns the string array of the generic dimension.
	 * 
	 * @return the string array of the generic dimension
	 */
	public String[] getAllGenericStringArray() {
		return this.getStringArray(this.mDimensionIndices.get(KEY_GENERIC_DIMENSION));
	}

	/**
	 * Returns the string array of the generic dimension with given stride.
	 * 
	 * @param stride
	 *          the stride for string array
	 * @return the string array of the generic dimension
	 */
	public String[] getGenericStringArray(SGIntegerSeriesSet stride) {
		return this.getStringArray(this.mDimensionIndices.get(KEY_GENERIC_DIMENSION), stride);
	}

	/**
	 * Returns the length of values of the generic dimension.
	 * 
	 * @return the length of values of the generic dimension
	 */
	public int getGenericDimensionLength() {
		return  this.getDimensionLength(KEY_GENERIC_DIMENSION);
	}

	/**
	 * Returns the length of values of specified dimension.
	 * 
	 * @param name
	 *           the name of dimension
	 * @return the length of values of specified dimension
	 */
	public int getDimensionLength(final String name) {
		Integer index = this.mDimensionIndices.get(name);
		if (index == null) {
			throw new IllegalArgumentException("Invalid dimension name: " + name);
		}
		int[] dims = this.getDimensions();
		return dims[index];
	}

	/**
	 * Returns the origin of dimension of given dimension index.
	 * 
	 * @return the origin of dimension of given dimension index
	 */
	public int getOrigin(final int index) {
		if (index < 0 || index >= this.mOrigins.length) {
			throw new IllegalArgumentException("Index out of bounds: " + index);
		}
		return this.mOrigins[index];
	}
	
	/**
	 * Returns the value type.
	 * 
	 * @return the value type
	 */
	public abstract String getValueType();

	/**
	 * Returns true if this variable has only numeric data.
	 * 
	 * @return true if this variable has only numeric data
	 */
	public abstract boolean isNumberVariable();

    /**
     * Clones this data object.
     * 
     * @return copy of this data object
     */
    public Object clone() {
    	SGMDArrayVariable var = null;
        try {
            var = (SGMDArrayVariable) super.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
        var.mFile = this.mFile;
        var.mOrigins = this.mOrigins.clone();
        var.mDimensionIndices = new HashMap<String, Integer>(this.mDimensionIndices);
        return var;
    }

	/**
	 * Returns a double value with given origins.
	 * 
	 * @param origins
	 *          the origins
	 * @return a double value
	 */
	public double getDoubleValue(final int[] origins) {
		if (origins == null || origins.length == 0) {
			throw new IllegalArgumentException("Invalid input values.");
		}
		return this.getDoubleValue(0, origins[0], origins);
	}

	/**
	 * Returns a double value for given dimension index and array index.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param arrayIndex
	 *           array index
	 * @return a double value
	 */
	public double getDoubleValue(final int dimensionIndex, final int arrayIndex) {
		return this.getDoubleValue(dimensionIndex, arrayIndex, this.mOrigins);
	}

	/**
	 * Returns a double value for given dimension index and array index at given origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param arrayIndex
	 *           array index
	 * @param origins
	 *           the origins
	 * @return a double value
	 */
	public abstract double getDoubleValue(final int dimensionIndex, final int arrayIndex,
			final int[] origins);

	public abstract double getDoubleValue(final int index);

	/**
	 * Returns the array of all double values for given dimension index with the current origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @return the array of double values
	 */
	public double[] getDoubleArray(final int dimensionIndex) {
		return this.getDoubleArray(dimensionIndex, null);
	}

	/**
	 * Returns the array of double values for given dimension index with given stride and the current origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param stride
	 *           stride of the array
	 * @return the array of double values
	 */
	public double[] getDoubleArray(final int dimensionIndex, SGIntegerSeriesSet stride) {
		return this.getDoubleArray(dimensionIndex, stride, this.mOrigins);
	}

	/**
	 * Returns the array of double values for given dimension index with given stride and origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param stride
	 *           stride of the array
	 * @param origins
	 *           the origins
	 * @return the array of double values
	 */
	public abstract double[] getDoubleArray(final int dimensionIndex, final SGIntegerSeriesSet stride,
			final int[] origins);

	/**
	 * Returns the array of double values for given dimension indices of x and y values
	 * with the current origins.
	 * 
	 * @param xIndex
	 *           dimension index for x-values
	 * @param yIndex
	 *           dimension index for y-values
	 * @return the array of double values
	 */
	public double[] getDoubleArray(final int xIndex, final int yIndex) {
		return this.getDoubleArray(xIndex, yIndex, this.mOrigins);
	}

	/**
	 * Returns the array of double values for given dimension indices of x and y values
	 * with given origins.
	 * 
	 * @param xIndex
	 *           dimension index for x-values
	 * @param yIndex
	 *           dimension index for y-values
	 * @param origins
	 *           the origins
	 * @return the array of double values
	 */
	public abstract double[] getDoubleArray(final int xIndex, final int yIndex, 
			final int origins[]);
	
	/**
	 * Returns the array of double values for given dimension indices with given stride
	 * and the current origins.
	 * 
	 * @param xIndex
	 *           dimension index for x-values
	 * @param yIndex
	 *           dimension index for y-values
	 * @param xSeries
	 *           array indices for x-values
	 * @param ySeries
	 *           array indices for y-values
	 * @return the array of double values
	 */
	public double[] getDoubleArray(final int xIndex, final int yIndex,
			final SGIntegerSeries xSeries, SGIntegerSeries ySeries) {
		return this.getDoubleArray(xIndex, yIndex, xSeries, ySeries, this.mOrigins);
	}

	public abstract double[] getDoubleArray(final int xIndex, final int yIndex,
			final SGIntegerSeriesSet xStride, SGIntegerSeriesSet yStride, int[] origins);

	public double[] getDoubleArray(final int xIndex, final int yIndex,
			final SGIntegerSeriesSet xStride, SGIntegerSeriesSet yStride) {
		return this.getDoubleArray(xIndex, yIndex, xStride, yStride, this.mOrigins);
	}

	/**
	 * Returns the array of double values for given dimension indices with given stride
	 * and given origins.
	 * 
	 * @param xIndex
	 *           dimension index for x-values
	 * @param yIndex
	 *           dimension index for y-values
	 * @param xSeries
	 *           array indices for x-values
	 * @param ySeries
	 *           array indices for y-values
	 * @param origins
	 *           the origins
	 * @return the array of double values
	 */
	public abstract double[] getDoubleArray(final int xIndex, final int yIndex,
			final SGIntegerSeries xSeries, SGIntegerSeries ySeries, final int[] origins);

	/**
	 * Returns an array of text strings with the current origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @return an array of text strings
	 */
	public abstract String[] getStringArray(final int dimensionIndex);

	/**
	 * Returns an array of text strings with given stride and the current origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param stride
	 *           stride of the array
	 * @return an array of text strings
	 */
	public abstract String[] getStringArray(final int dimensionIndex, 
			SGIntegerSeriesSet stride);

	/**
	 * Returns an array of text strings with given stride and given origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param stride
	 *           stride of the array
	 * @param origins
	 *           the origins
	 * @return an array of text strings
	 */
	public abstract String[] getStringArray(final int dimensionIndex, 
			SGIntegerSeriesSet stride, int[] origins);

	/**
	 * Returns a text string for given dimension index and array index at given origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param arrayIndex
	 *           array index
	 * @param origins
	 *           the origins
	 * @return a text string
	 */
	public abstract String getString(final int dimensionIndex, final int arrayIndex,
			final int[] origins);

	/**
	 * Returns a text string with given origins.
	 * 
	 * @param origins
	 *          the origins
	 * @return a double value
	 */
	public abstract String getString(final int[] origins);

	/**
	 * Returns a text string for given dimension index and array index.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param arrayIndex
	 *           array index
	 * @return a double value
	 */
	public abstract String getString(final int dimensionIndex, final int arrayIndex);

    enum VALUE_TYPE {
    	INTEGER, FLOAT, STRING,
    }

	protected static class MDArrayDataType {
		protected VALUE_TYPE valueType = null;
		protected MDArrayDataType() {
			super();
		}
		protected MDArrayDataType(final VALUE_TYPE valueType) {
			super();
			this.valueType = valueType;
		}
		public VALUE_TYPE getValueType() {
			return this.valueType;
		}
	}

	protected abstract MDArrayDataType getDataType();
	
	protected abstract MDArrayDataType getExportFloatingNumberDataType();

}
