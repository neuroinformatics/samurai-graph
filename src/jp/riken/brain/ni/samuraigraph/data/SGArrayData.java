package jp.riken.brain.ni.samuraigraph.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

import org.w3c.dom.Element;

/**
 * The base class of array type data.
 *
 */
public abstract class SGArrayData extends SGData implements SGIDataInformationKeyConstants, SGIDataCommandConstants {

    /**
     * The flag whether the stride of data arrays is available.
     */
    private boolean mStrideAvailable = false;

    // Data cache.
    private SGDataCache mCache = null;

    /**
     * The list of edited data values.
     */
    protected List<SGDataValueHistory> mEditedDataValueList = new ArrayList<SGDataValueHistory>();
    
    /**
     * The default constructor.
     */
	public SGArrayData() {
		super();
	}

    /**
     * Builds a data object.
     * 
     * @param src
     *           the data source
     * @param obs
     *           a data observer
     */
	public SGArrayData(SGIDataSource src, SGDataSourceObserver obs) {
		super(src, obs);
	}

    /**
     * Builds a data object.
     * 
     * @param src
     *           the data source
     * @param obs
     *           a data observer
     * @param strideAvailable
     *            flag whether to set available the stride
     */
	public SGArrayData(SGIDataSource src, SGDataSourceObserver obs,
			final boolean strideAvailable) {
		this(src, obs);
		this.mStrideAvailable = strideAvailable;
	}
	
    /**
     * Disposes of this data object.
     * 
     */
	@Override
	public void dispose() {
		super.dispose();
		this.mCache = null;
	}

    /**
     * Sets a given data.
     * 
     * @param data
     *           a data
     * @return true if succeeded
     */
    public boolean setData(final SGData data) {
    	if (!super.setData(data)) {
    		return false;
    	}
        if (!(data instanceof SGArrayData)) {
            throw new IllegalArgumentException("Given data is not the instance of SGArrayData class.");
        }
        SGArrayData aData = (SGArrayData) data;
        this.mStrideAvailable = aData.isStrideAvailable();
        
        // clears cache
        this.clearCache();
        return true;
    }

    /**
     * Returns whether the stride of data arrays is available.
     * 
     * @return true if the stride of data arrays is available
     */
    public boolean isStrideAvailable() {
    	return this.mStrideAvailable;
    }

    /**
     * Sets whether the stride of data arrays is available.
     * 
     * @param b
     *          true to set available
     */
    public void setStrideAvailable(final boolean b) {
    	if (this.isStrideAvailable() != b) {
    		this.clearCache();
    	}
    	this.mStrideAvailable = b;
    }

    /**
     * Returns a map of data information.
     * 
     * @return
     *        a map of data information
     */
    @Override
    public Map<String, Object> getInfoMap() {
        Map<String, Object> map = super.getInfoMap();
        map.put(KEY_STRIDE_AVAILABLE, this.mStrideAvailable);
    	map.putAll(this.getStrideMap());
        return map;
    }

    /**
     * Sets the column information.
     * 
     * @param colInfo
     *           the column information
     * @param true if succeeded
     */
    public boolean setColumnInfo(SGDataColumnInfo[] colInfo) {
        String[] types = new String[colInfo.length];
        for (int ii = 0; ii < types.length; ii++) {
            types[ii] = colInfo[ii].getColumnType();
        }
        if (this.setColumnType(types) == false) {
        	return false;
        }
        
        // clear the cache
        this.clearCache();

        return true;
    }

    /**
     * Sets the type of data columns.
     * 
     * @param column
     *              an array of column types
     * @return true if succeeded
     */
    public abstract boolean setColumnType(String[] columns);

    /**
     * Sets the stride of data arrays.
     * 
     * @param map
     *           the map of the stride
     */
    public abstract void setStrideMap(Map<String, SGIntegerSeriesSet> map);

    /**
     * Returns a map of stride for data arrays.
     * 
     * @return a map of stride for data arrays
     */
    protected abstract Map<String, SGIntegerSeriesSet> getStrideMap();

    /**
     * A class for data properties.
     */
    public static abstract class ArrayDataProperties extends DataProperties {

    	boolean strideAvailable;
    	
        List<SGDataValueHistory> editedDataValueList = new ArrayList<SGDataValueHistory>();

        /**
         * The default constructor.
         */
        public ArrayDataProperties() {
            super();
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj instanceof ArrayDataProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            ArrayDataProperties p = (ArrayDataProperties) obj;
            if (this.strideAvailable != p.strideAvailable) {
            	return false;
            }
            if (!SGUtility.equals(this.editedDataValueList, p.editedDataValueList)) {
            	return false;
            }
            return true;
        }
        
        public List<SGDataValueHistory> getEditedDataValueList() {
        	return new ArrayList<SGDataValueHistory>(this.editedDataValueList);
        }
        
        public boolean addEditedDataValue(SGDataValueHistory dataValue) {
        	return this.editedDataValueList.add(dataValue);
        }
        
        public void clearEditedDataValueList() {
        	this.editedDataValueList.clear();
        }
    }

    /**
     * Get properties of this data.
     * @param p
     *          properties to set values
     * @return
     *          true if succeeded
     */
    public boolean getProperties(final SGProperties p) {
        if ((p instanceof ArrayDataProperties) == false) {
            return false;
        }
        ArrayDataProperties ap = (ArrayDataProperties) p;
        ap.strideAvailable = this.isStrideAvailable();
        ap.editedDataValueList = new ArrayList<SGDataValueHistory>(this.mEditedDataValueList);
        return true;
    }
    
    /**
     * Set properties to this data.
     * @param p
     *          properties that have values to set to this data
     * @return
     *          true if succeeded
     */
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof ArrayDataProperties) == false) {
            return false;
        }
        if (!p.equals(this.getProperties())) {
            // clear the cache
            this.clearCache();
        }
        ArrayDataProperties ap = (ArrayDataProperties) p;
        this.setStrideAvailable(ap.strideAvailable);
        this.mEditedDataValueList = new ArrayList<SGDataValueHistory>(ap.editedDataValueList);
        return true;
    }

    /**
     * Writes properties of this data to a given Element.
     * 
     * @param el
     *          an Element
     * @param type
     *            type of the method to save properties
     * @return
     *          true if succeeded
     */
    public boolean writeProperty(Element el, SGExportParameter type) {
        el.setAttribute(SGIDataPropertyKeyConstants.KEY_ARRAY_SECTION_AVAILABLE, 
        		Boolean.toString(this.mStrideAvailable));
    	return true;
    }
    
    /**
     * Returns an array of current column types.
     * 
     * @return
     *        an array of current column types
     */
    public abstract String[] getCurrentColumnType();

    /**
     * Returns an array of data column information.
     * 
     * @return an array of data column information
     */
    public abstract SGDataColumnInfo[] getColumnInfo();

    protected SGIntegerSeriesSet createStride(SGIntegerSeriesSet stride, final int len) {
        if (stride != null) {
            return (SGIntegerSeriesSet) stride.clone();
        } else {
        	return SGIntegerSeriesSet.createInstance(len);
        }
    }

    /**
     * Saves the data to a file for archive data set file.
     *
     * @param file
     *             the file to save to
     * @param mode
     *             the mode of saving data
     * @return true if succeeded
     */
    public abstract boolean saveToArchiveDataSetFile(final File file, final SGExportParameter mode);

    /**
     * Saves the data into a file of the same format.
     * 
     * @param file
     *           the file to save
     * @param mode
     *           the mode to save data
     * @return true if succeeded
     */
    public abstract boolean saveToSameFormatFile(final File file, final SGExportParameter mode,
    		SGDataBufferPolicy policy);

    /**
     * Saves the data into a text file.
     * 
     * @param file
     *           the file to save
     * @param mode
     *           the mode to save data
     * @return true if succeeded
     */
    public abstract boolean saveToTextFile(final File file, final SGExportParameter mode, 
    		SGDataBufferPolicy policy);

    /**
     * Saves the data into a NetCDF file.
     * 
     * @param file
     *           the file to save
     * @param mode
     *           the mode to save data
     * @return true if succeeded
     */
    public abstract boolean saveToNetCDFFile(final File file, final SGExportParameter mode,
    		SGDataBufferPolicy policy);

    /**
     * Saves the data to a file for NetCDF data set file.
     * 
     * @param file
     *           the file to save
     * @return true if succeeded
     */
    public abstract boolean saveToDataSetNetCDFFile(final File file);

    /**
     * Saves the data into a HDF5 file.
     * 
     * @param path
     *           the path of HDF5 file
     * @param mode
     *           the mode to save data
     * @return true if succeeded
     */
    public abstract boolean saveToHDF5File(final File file, final SGExportParameter mode, 
    		SGDataBufferPolicy policy);

    /**
     * Saves the data into a MATLAB file.
     * 
     * @param path
     *           the path of MATLAB file
     * @param mode
     *           the mode to save data
     * @return true if succeeded
     */
    public abstract boolean saveToMATLABFile(final File file, final SGExportParameter mode,
    		SGDataBufferPolicy policy);

    /**
     * Returns a text string for the command of the column types.
     * 
     * @return a text string for the command of the column types
     */
    public abstract String getColumnTypeCommandString();

    /**
     * Returns a text string for the command of the origin.
     * 
     * @return a text string for the command of the origin
     */
    public abstract String getOriginCommandString();

    /**
     * Sets the properties for the array section to given property map.
     * 
     * @param map
     *           the property map
     * @return true if succeeded
     */
    public boolean setArraySectionProperty(SGPropertyMap map) {
    	SGPropertyUtility.addProperty(map, COM_DATA_ARRAY_SECTION_AVAILABLE, this.isStrideAvailable());
    	this.setArraySectionPropertySub(map);
    	return true;
    }
    
    protected abstract boolean setArraySectionPropertySub(SGPropertyMap map);
    
    /**
     * Returns the length of animation.
     * 
     * @return the length of animation
     */
    public abstract int getAnimationLength();

    void setCache(SGDataCache cache) {
    	this.mCache = cache;
    }

    SGDataCache getCache() {
    	return this.mCache;
    }
    
    // clear the cache
    protected void clearCache() {
    	this.setCache(null);
    	
    	// clear the list of edited values
    	this.mEditedDataValueList.clear();
    }
    
    /**
     * Sets the data source.
     * 
     * @param src
     *           a data source
     */
    @Override
    public void setDataSource(SGIDataSource src) {
    	super.setDataSource(src);
    	
    	// clears cache
    	this.clearCache();
    }

    @Override
    public Object clone() {
        SGArrayData data = (SGArrayData) super.clone();
        data.mCache = (this.mCache != null) ? (SGDataCache) this.mCache.clone() : null;
        return data;
    }

    public List<SGDataValueHistory> getEditedValueList() {
    	return new ArrayList<SGDataValueHistory>(this.mEditedDataValueList);
    }

    /**
     * Returns a text string for the tooltip.
     * 
     * @return a text string for the tooltip
     */
    public abstract String getToolTipTextNotSpatiallyVaried();

    // Returns true if NaN is assigned by fill value and valu range.
	protected boolean isNaNAssignedInvalidValue(final int col, final int row, String columnType,
			final double d) {
		if (Double.isNaN(d)) {
			boolean ret = true;
			for (SGDataValueHistory value : this.mEditedDataValueList) {
				if (this.matches(col, row, columnType, value, d)) {
					ret = false;
					break;
				}
			}
			return ret;
		} else {
			return false;
		}
	}

	protected abstract boolean matches(final int col, final int row, String columnType,
			SGDataValueHistory value, final double d);

	@Override
    public SGTwoDimensionalArrayIndex getDataViewerCell(SGTwoDimensionalArrayIndex cell,
    		String columnType, final boolean bStride) {
		SGTwoDimensionalArrayIndex ret;
    	if (bStride) {
    		SGIntegerSeriesSet colStride = this.getDataViewerColStride(columnType);
    		int[] colIndices = colStride.getNumbers();
    		final int col = Arrays.binarySearch(colIndices, cell.getColumn());
    		
    		SGIntegerSeriesSet rowStride = this.getDataViewerRowStride(columnType);
    		int[] rowIndices = rowStride.getNumbers();
    		final int row = Arrays.binarySearch(rowIndices, cell.getRow());
    		
    		ret = new SGTwoDimensionalArrayIndex(col, row);
    	} else {
    		ret = cell;
    	}
    	return ret;
    }
}
