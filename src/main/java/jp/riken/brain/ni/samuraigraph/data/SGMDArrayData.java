package jp.riken.brain.ni.samuraigraph.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable.MDArrayDataType;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable.VALUE_TYPE;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

import org.w3c.dom.Element;

import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Group;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;
import ch.systemsx.cisd.base.mdarray.MDArray;
import ch.systemsx.cisd.base.mdarray.MDDoubleArray;
import ch.systemsx.cisd.base.mdarray.MDIntArray;
import ch.systemsx.cisd.hdf5.HDF5DataClass;
import ch.systemsx.cisd.hdf5.HDF5FactoryProvider;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import ch.systemsx.cisd.hdf5.IHDF5Writer;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

/**
 * The base class for multidimensional data.
 *
 */
public abstract class SGMDArrayData extends SGArrayData implements SGIDataColumnTypeConstants {

	/**
	 * The list of variables.
	 */
	protected SGMDArrayVariable[] mVariables = null;

    /**
     * The stride for the time.
     */
    protected SGIntegerSeriesSet mTimeStride = null;

    /**
     * The default constructor.
     */
    public SGMDArrayData() {
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
    public SGMDArrayData(final SGIDataSource src, final SGDataSourceObserver obs,
    		final boolean strideAvailable) {
    	super(src, obs, strideAvailable);
    	if (!(src instanceof SGMDArrayFile)) {
    		throw new IllegalArgumentException("Invalid data source: " + src);
    	}
    	SGMDArrayFile mdFile = (SGMDArrayFile) src;
    	SGMDArrayVariable[] vars = mdFile.getVariables();
    	this.mVariables = new SGMDArrayVariable[vars.length];
    	for (int ii = 0; ii < vars.length; ii++) {
    		SGMDArrayVariable var = (SGMDArrayVariable) vars[ii].clone();
    		this.mVariables[ii] = this.initVariable(var);
    	}
    }

	protected void initTimeStride() {
		SGIntegerSeriesSet stride = null;
		if (this.isTimeDimensionAvailable()) {
			final int len = this.getTimeDimensionLength();
			stride = SGIntegerSeriesSet.createInstance(len);
		}
		this.mTimeStride = stride;
	}

    /**
     * Disposes of this data object.
     * Do not call any method of this object after called this method.
     */
    public void dispose() {
        super.dispose();
        this.mTimeStride = null;
        this.mVariables = null;
    }

	/**
	 * Finds and returns a variable of given name.
	 *
	 * @param name
	 *           the name of variable
	 * @return a variable if it is found
	 */
    public SGMDArrayVariable findVariable(String name) {
    	SGMDArrayVariable var = null;
        if (name != null) {
        	for (int ii = 0; ii < this.mVariables.length; ii++) {
        		SGMDArrayVariable v = this.mVariables[ii];
        		if (v.getName().equals(name)) {
        			var = v;
        			break;
        		}
        	}
        }
        return var;
    }

    protected SGMDArrayVariable initVariable(SGMDArrayFile file, SGMDArrayDataColumnInfo info) {
    	SGMDArrayVariable var = null;
    	if (info != null) {
        	var = this.findVariable(info.getName());
        	var.setDimensionIndices(info.getDimensionIndices());
        	var.setGenericDimensionIndex(info.getGenericDimensionIndex());
        	Integer timeDimension = info.getTimeDimensionIndex();
        	Integer value = null;
        	if (timeDimension != null) {
        		value = timeDimension;
        	} else {
        		value = -1;
        	}
        	var.setTimeDimensionIndex(value);
        	var.setOrigins(info.getOrigins());
    	}
    	return var;
    }

    protected SGMDArrayVariable initVariable(SGMDArrayVariable var) {
    	var.setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    	var.setDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION, -1);
    	return var;
    }

    /**
     * Returns the multidimensional data file.
     *
     * @return the multidimensional data file
     */
    public SGMDArrayFile getMDArrayFile() {
    	return (SGMDArrayFile) this.getDataSource();
    }

    /**
     * Sets the dimension index of a variable.
     *
     * @param name
     *           the name of a variable
     * @param index
     *           index to set
     * @return true if succeeded
     */
    public boolean setDimensionIndex(final String name, final int index) {
    	SGMDArrayVariable var = this.findVariable(name);
    	if (var == null) {
    		return false;
    	}
    	var.setGenericDimensionIndex(index);
        return true;
    }

    /**
     * Sets the time dimension index of a variable.
     *
     * @param name
     *           the name of a variable
     * @param index
     *           index to set
     * @return true if succeeded
     */
    public boolean setTimeDimensionIndex(final String name, final int index) {
    	SGMDArrayVariable var = this.findVariable(name);
    	if (var == null) {
    		return false;
    	}
    	var.setTimeDimensionIndex(index);
        return true;
    }

    /**
     * Sets the origins of a variable.
     *
     * @param name
     *           the name of a variable
     * @param origins
     *           origins to set
     * @return true if succeeded
     */
    public boolean setOrigin(final String name, final int[] origins) {
		if (origins == null) {
			throw new IllegalArgumentException("Variable is not found: " + name);
		}
    	SGMDArrayVariable var = this.findVariable(name);
    	if (var == null) {
    		return false;
    	}
    	var.setOrigins(origins.clone());
        return true;
    }

    /**
     * Returns an array of variables.
     *
     * @return an array of variables
     */
    public SGMDArrayVariable[] getVariables() {
    	return this.mVariables.clone();
    }

    /**
     * Returns the map of dimension index.
     *
     * @return the map of dimension index
     */
    public Map<String, Map<String, Integer>> getDimensionIndexMap() {
    	Map<String, Map<String, Integer>> map = new HashMap<String, Map<String, Integer>>();
    	SGMDArrayVariable[] vars = this.getVariables();
    	for (SGMDArrayVariable var : vars) {
    		final String name = var.getName();
    		final Map<String, Integer> dimensionIndices = var.getDimensionIndices();
    		map.put(name, dimensionIndices);
    	}
    	return map;
    }

    /**
     * Sets the dimension index of variables.
     *
     * @param map
     *           the map of dimension index
     */
    public void setDimensionIndex(Map<String, Map<String, Integer>> map) {
    	SGMDArrayVariable[] vars = this.getVariables();
    	for (SGMDArrayVariable var : vars) {
    		final String name = var.getName();
    		Map<String, Integer> dimensionIndices = map.get(name);
    		if (dimensionIndices == null) {
    			throw new IllegalArgumentException("Variable is not found: " + name);
    		}
    		var.setDimensionIndices(dimensionIndices);
    	}
    }

    /**
     * Returns the map of origins.
     *
     * @return the map of origins
     */
    public Map<String, int[]> getOriginMap() {
    	Map<String, int[]> map = new HashMap<String, int[]>();
    	SGMDArrayVariable[] vars = this.getVariables();
    	for (SGMDArrayVariable var : vars) {
    		final String name = var.getName();
    		final int[] origins = var.getOrigins().clone();
    		map.put(name, origins);
    	}
    	return map;
    }

    /**
     * Sets the origin of variables.
     *
     * @param map
     *           the map of origin
     */
    public void setOrigin(Map<String, int[]> map) {
    	SGMDArrayVariable[] vars = this.getVariables();
    	for (SGMDArrayVariable var : vars) {
    		final String name = var.getName();
    		int[] origins = map.get(name);
    		if (origins == null) {
    			throw new IllegalArgumentException("Variable is not found: " + name);
    		}
    		var.setOrigins(origins.clone());
    	}
    }

    /**
     * A class of multidimensional data properties.
     */
    public static abstract class MDArrayDataProperties extends ArrayDataProperties {

        protected Map<String, Map<String, Integer>> dimensionIndexMap = new HashMap<String, Map<String, Integer>>();

        protected Map<String, int[]> originMap = new HashMap<String, int[]>();

        protected SGIntegerSeriesSet timeStride = null;

        /**
         * The default constructor.
         */
        public MDArrayDataProperties() {
            super();
        }

        /**
         * Dispose this object.
         */
    	@Override
        public void dispose() {
        	super.dispose();
            this.dimensionIndexMap.clear();
            this.originMap.clear();
            this.timeStride = null;
        }

        /**
         * Returns whether this object is equal to given object.
         * 
         * @param obj
         *            an object to be compared
         * @return
         *            true if two objects are equal
         */
        @Override
        public boolean equals(final Object obj) {
        	if (!(obj instanceof MDArrayDataProperties)) {
        		return false;
        	}
        	if (super.equals(obj) == false) {
        		return false;
        	}
        	MDArrayDataProperties p = (MDArrayDataProperties) obj;
            if (SGUtility.equals(this.timeStride, p.timeStride) == false) {
            	return false;
            }
            Set<String> keySet = this.originMap.keySet();
            for (String key : keySet) {
            	int[] origins = this.originMap.get(key);
            	int[] pOrigins = p.originMap.get(key);
            	if (!SGUtility.equals(origins, pOrigins)) {
            		return false;
            	}
            }

        	return true;
        }

        @Override
        public boolean hasEqualSize(DataProperties dp) {
        	if (!(dp instanceof MDArrayDataProperties)) {
        		return false;
        	}
        	MDArrayDataProperties p = (MDArrayDataProperties) dp;
            Iterator<Entry<String, Map<String, Integer>>> varItr = this.dimensionIndexMap.entrySet().iterator();
            while (varItr.hasNext()) {
            	Entry<String, Map<String, Integer>> varEntry = varItr.next();
            	String varName = varEntry.getKey();
            	Map<String, Integer> dimMap = varEntry.getValue();
            	Map<String, Integer> pDimMap = p.dimensionIndexMap.get(varName);
            	Iterator<Entry<String, Integer>> dimItr = dimMap.entrySet().iterator();
            	while (dimItr.hasNext()) {
            		Entry<String, Integer> dimEntry = dimItr.next();
            		String dimKey = dimEntry.getKey();
            		Integer dimIndex = dimEntry.getValue();
            		Integer pDimIndex = pDimMap.get(dimKey);
            		if (!SGUtility.equals(dimIndex, pDimIndex)) {
            			return false;
            		}
            	}
            }
        	return true;
        }

        /**
         * Returns a copy of this object.
         *
         * @return a copy of this object
         */
        public Object copy() {
        	MDArrayDataProperties p = (MDArrayDataProperties) super.copy();
        	p.dimensionIndexMap = new HashMap<String, Map<String, Integer>>();
        	Set<Entry<String, Map<String, Integer>>> dEntrySet = this.dimensionIndexMap.entrySet();
        	Iterator<Entry<String, Map<String, Integer>>> dItr = dEntrySet.iterator();
        	while (dItr.hasNext()) {
        		Entry<String, Map<String, Integer>> entry = dItr.next();
        		String key = entry.getKey();
        		Map<String, Integer> value = entry.getValue();
        		p.dimensionIndexMap.put(key, new HashMap<String, Integer>(value));
        	}
        	p.originMap = new HashMap<String, int[]>();
        	Set<Entry<String, int[]>> oEntrySet = this.originMap.entrySet();
        	Iterator<Entry<String, int[]>> oItr = oEntrySet.iterator();
        	while (oItr.hasNext()) {
        		Entry<String, int[]> entry = oItr.next();
        		String key = entry.getKey();
        		int[] value = entry.getValue().clone();
        		p.originMap.put(key, value);
        	}
        	p.timeStride = (this.timeStride != null) ? (SGIntegerSeriesSet) this.timeStride.clone() : null;
        	return p;
        }
        
//        @Override
//        public boolean hasEqualColumnTypes(DataProperties dp) {
//            if ((dp instanceof MDArrayDataProperties) == false) {
//                return false;
//            }
//            MDArrayDataProperties p = (MDArrayDataProperties) dp;
//            if (SGUtility.equals(this.dimensionIndexMap, p.dimensionIndexMap) == false) {
//            	return false;
//            }
//            return true;
//        }
        
    }

    /**
     * Returns the properties of this data.
     *
     * @param p
     *      the properties of this data
     * @return true if succeeded
     */
	@Override
    public boolean getProperties(SGProperties p) {
        if (!(p instanceof MDArrayDataProperties)) {
            return false;
        }
        if (super.getProperties(p) == false) {
        	return false;
        }
        MDArrayDataProperties mp = (MDArrayDataProperties) p;
        mp.dimensionIndexMap = this.getDimensionIndexMap();
        mp.originMap = this.getOriginMap();
        mp.timeStride = this.getTimeStride();
        return true;
    }

    /**
     * Sets the properties to this data.
     *
     * @param p
     *          properties to be set
     * @return true if succeeded
     */
	@Override
    public boolean setProperties(SGProperties p) {
        if (!(p instanceof MDArrayDataProperties)) {
            return false;
        }
        if (super.setProperties(p) == false) {
        	return false;
        }
        MDArrayDataProperties mp = (MDArrayDataProperties) p;
        Map<String, Map<String, Integer>> dimensionIndexMap = mp.dimensionIndexMap;
        Map<String, int[]> originMap = mp.originMap;
        this.setDimensionIndex(dimensionIndexMap);
        this.setOrigin(originMap);
        this.setTimeStride(mp.timeStride);
        return true;
    }

    /**
     * Returns the name of given variable if it is not null.
     *
     * @param var
     *            a variable
     * @return the name of given variable or null
     */
    protected String getName(SGMDArrayVariable var) {
    	return (var != null) ? var.getName() : null;
    }

    /**
     * Returns a map which has data information.
     * Overrode to set the data type and netCDF file.
     *
     * @return a map which has data information
     */
    public Map<String, Object> getInfoMap() {
    	Map<String, Object> map = super.getInfoMap();
        map.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, this.getDataType());
    	map.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, this.getMDArrayFile());

        Map<String, Integer> timeDimensionMap = new HashMap<String, Integer>();
        SGMDArrayVariable[] vars = this.getVariables();
        for (int ii = 0; ii < vars.length; ii++) {
        	String name = vars[ii].getName();
        	Integer index = vars[ii].getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
        	if (index != null && index != -1) {
        		timeDimensionMap.put(name, index);
        	}
        }
        map.put(SGIDataInformationKeyConstants.KEY_TIME_DIMENSION_INDEX_MAP,
        		timeDimensionMap);

    	return map;
    }

    /**
     * Sets the column information.
     *
     * @param colInfo
     *           the column information
     * @return true if succeeded
     */
    public boolean setColumnInfo(SGDataColumnInfo[] colInfo) {
        if (super.setColumnInfo(colInfo) == false) {
        	return false;
        }

        // sets the dimensions and origins
        boolean timeDimChanged = false;
        for (int ii = 0; ii < colInfo.length; ii++) {
            SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfo[ii];
            String name = mdInfo.getName();
            SGMDArrayVariable var = this.findVariable(name);
            if (var == null) {
            	throw new IllegalArgumentException("Variable is not found: " + name);
            }
            int[] origins = mdInfo.getOrigins();
            var.setOrigins(origins);

            // compares time dimension
            final int timeDim = var.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
            final int timeDimNew = mdInfo.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
            if (timeDim != timeDimNew) {
            	timeDimChanged = true;
            }

            Map<String, Integer> indices = mdInfo.getDimensionIndices();
            var.setDimensionIndices(indices);
        }

        // setup the time stride
        SGIntegerSeriesSet stride = null;
        final int curTimeLen = this.getTimeDimensionLength();
    	if (curTimeLen != -1) {
            if (this.mTimeStride == null) {
            	stride = SGIntegerSeriesSet.createInstance(curTimeLen);
            } else {
            	if (timeDimChanged) {
                	stride = SGIntegerSeriesSet.createInstance(curTimeLen);
            	} else {
            		stride = this.mTimeStride;
            	}
            }
    	}
    	this.mTimeStride = stride;

        return true;
    }

	/**
	 * Finds a variable of a given name if it exists and returns its array index.
	 *
	 * @param name
	 *           the name of variable
	 * @return the array index of found variable or -1 if it is not found
	 */
	public int getVariableIndex(final String name) {
		SGMDArrayVariable[] vars = this.getVariables();
		for (int ii = 0; ii < vars.length; ii++) {
			SGMDArrayVariable var = vars[ii];
			if (name.equals(var.getName())) {
				return ii;
			}
		}
		return -1;
	}

	/**
	 * Generates and returns an array of double values.
	 *
	 * @param stride
	 *           stride of an array
	 * @return an array of double values
	 */
	protected double[] getIndexValues(final SGIntegerSeriesSet stride) {
		final int[] indices = stride.getNumbers();
		double[] array = new double[indices.length];
		for (int ii = 0; ii < array.length; ii++) {
			array[ii] = (double) indices[ii];
		}
		return array;
	}

	protected double[] getIndexValues(final int len) {
		return SGUtilityNumber.toDoubleArray(len);
	}

	protected int[] getIndices(final int len) {
		return SGUtilityNumber.toIntArray(len);
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
        if (!(data instanceof SGMDArrayData)) {
            throw new IllegalArgumentException("Given data is not the instance of SGMDData class.");
        }
        SGMDArrayData mdData = (SGMDArrayData) data;
        this.mVariables = new SGMDArrayVariable[mdData.mVariables.length];
        for (int ii = 0; ii < this.mVariables.length; ii++) {
        	this.mVariables[ii] = (SGMDArrayVariable) mdData.mVariables[ii].clone();
        }
        this.mTimeStride = mdData.getTimeStride();
        return true;
    }

    protected static SGMDArrayVariable copyVariable(SGMDArrayVariable var) {
    	if (var != null) {
    		return (SGMDArrayVariable) var.clone();
    	} else {
    		return null;
    	}
    }

    protected static SGMDArrayVariable[] copyVariables(SGMDArrayVariable[] vars) {
    	if (vars != null) {
    		SGMDArrayVariable[] cVars = new SGMDArrayVariable[vars.length];
    		for (int ii = 0; ii < vars.length; ii++) {
    			cVars[ii] = copyVariable(vars[ii]);
    		}
    		return cVars;
    	} else {
    		return null;
    	}
    }

    /**
     * Returns the copy of this data object.
     *
     * @return
     *         a copy of this data object
     */
    public Object clone() {
        SGMDArrayData data = (SGMDArrayData) super.clone();
        data.mVariables = new SGMDArrayVariable[this.mVariables.length];
        for (int ii = 0; ii < this.mVariables.length; ii++) {
        	data.mVariables[ii] = (SGMDArrayVariable) this.mVariables[ii].clone();
        }
        data.mTimeStride = this.getTimeStride();
        return data;
    }

    /**
     * Returns the stride of time.
     *
     * @return the stride of time
     */
    @Override
    public SGIntegerSeriesSet getTimeStride() {
    	if (this.mTimeStride != null) {
    		return (SGIntegerSeriesSet) this.mTimeStride.clone();
    	} else {
    		return null;
    	}
    }

    /**
     * Sets the stride of the time.
     *
     * @param stride
     *           stride of arrays
     */
	public void setTimeStride(SGIntegerSeriesSet stride) {
    	if (stride == null) {
    		this.mTimeStride = null;
    	} else {
    		this.mTimeStride = (SGIntegerSeriesSet) stride.clone();
    	}
	}

    protected List<SGXYSimpleDoubleValueIndexBlock> get2DValueBlockList(
    		SGMDArrayVariable var, final int xIndex, final int yIndex,
    		SGIntegerSeriesSet xStride, SGIntegerSeriesSet yStride,
    		final boolean all) {
    	final List<SGIntegerSeries> xSeriesList, ySeriesList;
    	if (this.isStrideAvailable()) {
        	xSeriesList = xStride.getSeriesList();
        	ySeriesList = yStride.getSeriesList();
    	} else {
    		xSeriesList = new ArrayList<SGIntegerSeries>();
    		ySeriesList = new ArrayList<SGIntegerSeries>();
    		final int[] dims = var.getDimensions();
    		final int xLen = dims[xIndex];
    		final int yLen = dims[yIndex];
    		SGIntegerSeries xSeries = SGIntegerSeries.createInstance(xLen);
    		SGIntegerSeries ySeries = SGIntegerSeries.createInstance(yLen);
    		xSeriesList.add(xSeries);
    		ySeriesList.add(ySeries);
    	}
		List<SGXYSimpleDoubleValueIndexBlock> sBlockList = new ArrayList<SGXYSimpleDoubleValueIndexBlock>();
		for (SGIntegerSeries ySeries : ySeriesList) {
			for (SGIntegerSeries xSeries : xSeriesList) {
				double[] values = var.getDoubleArray(xIndex, yIndex, xSeries, ySeries);
				SGXYSimpleDoubleValueIndexBlock block = new SGXYSimpleDoubleValueIndexBlock(values, xSeries, ySeries);
				sBlockList.add(block);
			}
		}
		return sBlockList;
	}

    /**
     * Returns an array of data column information.
     *
     * @return an array of data column information
     */
    @Override
    public SGDataColumnInfo[] getColumnInfo() {
        SGMDArrayVariable[] vars = this.getVariables();
        SGMDArrayDataColumnInfo[] colArray = new SGMDArrayDataColumnInfo[vars.length];
        String[] colTypes = this.getCurrentColumnType();
        for (int ii = 0; ii < vars.length; ii++) {
        	SGMDArrayVariable var = vars[ii];
        	colArray[ii] = new SGMDArrayDataColumnInfo(var, var.getName(), var.getValueType(),
        			var.getOrigins());
        	colArray[ii].setColumnType(colTypes[ii]);
        }
        return colArray;
    }

    /**
     * Sets the dimensions.
     *
     * @param cols
     *            an array of column information
     * @return true if succeeded
     */
	protected boolean setDimensionMap(SGDataColumnInfo[] cols) {
		// set dimensions
		Map<String, Map<String, Integer>> dimIndexMap = new HashMap<String, Map<String, Integer>>();
		for (int ii = 0; ii < cols.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
			Map<String, Integer> map = mdCol.getDimensionIndices();
			dimIndexMap.put(mdCol.getName(), map);
		}
		this.setDimensionIndex(dimIndexMap);

		return true;
	}

    /**
     * Writes properties of this object to the Element.
     *
     * @param el
     *            the Element object
     * @param type
     *            type of the method to save properties
     * @return true if succeeded
     */
	@Override
    public boolean writeProperty(Element el, final SGExportParameter type) {
    	if (super.writeProperty(el, type) == false) {
    		return false;
    	}

    	if (SGDataUtility.isArchiveDataSetOperation(type.getType())
    			|| OPERATION.SAVE_TO_PROPERTY_FILE.equals(type.getType())) {
            // origins
            StringBuffer sb = new StringBuffer();
            SGMDArrayVariable[] vars = this.getVariables();
            List<SGMDArrayVariable> numVarList = new ArrayList<SGMDArrayVariable>();
            for (int ii = 0; ii < vars.length; ii++) {
            	if (vars[ii].isNumberVariable()) {
            		numVarList.add(vars[ii]);
            	}
            }
            for (int ii = 0; ii < numVarList.size(); ii++) {
            	SGMDArrayVariable var = numVarList.get(ii);
                if (ii > 0) {
                    sb.append(',');
                }
            	String name = var.getName();
                sb.append(name);
                sb.append("=(");
            	int[] origins = var.getOrigins();
            	for (int jj = 0; jj < origins.length; jj++) {
            		if (jj > 0) {
            			sb.append(',');
            		}
            		sb.append(origins[jj]);
            	}
                sb.append(")");
            }
            el.setAttribute(SGIDataPropertyKeyConstants.KEY_ORIGIN_MAP, sb.toString());

            // time dimensions
            sb.setLength(0);
            int cnt = 0;
            for (int ii = 0; ii < vars.length; ii++) {
            	SGMDArrayVariable var = vars[ii];
                Integer timeDimension = var.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
                if (timeDimension == null || timeDimension == -1) {
                	continue;
                }
            	String name = var.getName();
                if (cnt > 0) {
                    sb.append(',');
                }
                sb.append(name);
                sb.append(":");
                sb.append(timeDimension);
                cnt++;
            }
            el.setAttribute(SGIDataPropertyKeyConstants.KEY_ANIMATION_DIMENSION, sb.toString());

    	} else if (OPERATION.SAVE_TO_DATA_SET_NETCDF.equals(type)) {

            // origins
    		SGMDArrayVariable[] vars = this.getAssignedVariables();
            StringBuffer sb = new StringBuffer();
            for (int ii = 0; ii < vars.length; ii++) {
                if (ii > 0) {
                    sb.append(',');
                }
            	SGMDArrayVariable var = vars[ii];
                Integer value = var.getDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION);
                sb.append(var.getName());
                sb.append('=');
                sb.append(value);
            }
            el.setAttribute(SGIDataPropertyKeyConstants.KEY_ORIGIN_MAP, sb.toString());

            // time dimension
            int cnt = 0;
            for (int ii = 0; ii < vars.length; ii++) {
            	SGMDArrayVariable var = vars[ii];
                Integer timeDimension = var.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
                if (timeDimension == null || timeDimension == -1) {
                	continue;
                }
                cnt++;
            }
            if (cnt > 0) {
                el.setAttribute(SGIDataPropertyKeyConstants.KEY_TIME_VARIABLE_NAME, TIME_DIM_NAME);
            }
       	}

        return true;
    }
	
    /**
     * Returns whether time dimensions are selected properly.
     *
     * @return true if time dimensions are selected properly
     */
    public boolean isTimeDimensionAvailable() {
    	return (this.getTimeDimensionLength() != -1);
    }

    /**
     * Returns whether animation is available.
     * 
     * @return true if animation is available
     */
    @Override
    public boolean isAnimationAvailable() {
    	return this.isTimeDimensionAvailable();
    }

    /**
     * Returns the length of animation.
     * 
     * @return the length of animation
     */
    @Override
    public int getAnimationLength() {
    	return this.getTimeDimensionLength();
    }

    /**
     * Returns the length of time dimension.
     * If time dimension is unavailable, returns -1.
     *
     * @return the length of time dimension
     */
    public int getTimeDimensionLength() {
    	int len = -1;
    	SGDataColumnInfo[] cols = this.getColumnInfo();
    	for (int ii = 0; ii < cols.length; ii++) {
    		SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
    		Integer index = this.getTimeDimensionIndex(mdCol);
    		if (index == null) {
    			continue;
    		}
    		final int[] dims = mdCol.getDimensions();
    		if (len == -1) {
    			len = dims[index];
    		} else {
    			if (len != dims[index]) {
    				return -1;
    			}
    		}
    	}
    	return len;
    }

    /**
     * Returns the current time value.
     *
     * @return the current time value
     */
    public Number getCurrentTimeValue() {
    	if (!this.isTimeDimensionAvailable()) {
            return -1;
        }
        final int index = this.getCurrentTimeValueIndex();
        return Integer.valueOf(index);
    }

    /**
     * Returns the current index of time value.
     *
     * @return the current index of time value
     */
    @Override
    public int getCurrentTimeValueIndex() {
    	if (!this.isTimeDimensionAvailable()) {
            return -1;
        }
    	int index = -1;
    	SGMDArrayVariable[] vars = this.getVariables();
    	for (SGMDArrayVariable var : vars) {
    		Integer timeDimension = var.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
    		if (timeDimension == null || timeDimension == -1) {
    			continue;
    		}
    		final int[] origins = var.getOrigins();
    		if (index == -1) {
    			index = origins[timeDimension];
    		} else {
    			if (index != origins[timeDimension]) {
    				return -1;
    			}
    		}
    	}
    	return index;
    }

    public Map<String, Integer> getTimeOriginMap() {
    	Map<String, Integer> map = new HashMap<String, Integer>();
    	SGDataColumnInfo[] cols = this.getColumnInfo();
    	for (int ii = 0; ii < cols.length; ii++) {
    		SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
    		Integer index = this.getTimeDimensionIndex(mdCol);
    		if (index == null) {
    			continue;
    		}
    		final int[] origins = mdCol.getOrigins();
    		map.put(mdCol.getName(), origins[index]);
    	}
    	return map;
    }

    private Integer getTimeDimensionIndex(SGMDArrayDataColumnInfo mdCol) {
		String columnType = mdCol.getColumnType();
		if ("".equals(columnType)) {
			return null;
		}
		Integer index = mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
		if (index == null || index == -1) {
			return null;
		}
    	return index;
    }

    /**
     * Sets the current index of time value.
     *
     * @param index
     *           a value to set to the index of time values
     */
    @Override
    public void setCurrentTimeValueIndex(final int index) {
    	if (!this.isTimeDimensionAvailable()) {
            return;
        }
    	SGDataColumnInfo[] cols = this.getColumnInfo();
    	for (int ii = 0; ii < cols.length; ii++) {
    		SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
    		Integer dimIndex = this.getTimeDimensionIndex(mdCol);
    		if (dimIndex == null) {
    			continue;
    		}
    		String name = mdCol.getName();
    		SGMDArrayVariable var = this.findVariable(name);
    		Integer timeDimension = var.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
    		if (timeDimension != null && timeDimension != -1) {
    			final int[] dims = var.getDimensions();
    			if (index < 0 || dims[timeDimension] <= index) {
    				throw new IllegalArgumentException("Index out of bounds: " + index);
    			}
        		final int[] origins = var.getOrigins();
        		origins[timeDimension] = index;
    			var.setOrigins(origins);
    		}
    	}
    	
        // clear the cache
        this.clearCache();
    }

    /**
     * Returns the map of dimension index that are used.
     *
     * @return the map of dimension index
     */
    public abstract Map<String, Map<String, Integer>> getUsedDimensionIndexMap();

    /**
     * Returns an array of variables that are assigned the column type.
     *
     * @return an array of variables
     */
    public abstract SGMDArrayVariable[] getAssignedVariables();

    /**
     * Exports the data into a NetCDF file.
     * 
     * @param file
     *           the file to save
     * @return true if succeeded
     */
    @Override
    public boolean saveToNetCDFFile(final File file, final SGExportParameter mode,
    		SGDataBufferPolicy policy) {
    	SGMDArrayFile mdFile = this.getMDArrayFile();
        NetcdfFileWriteable ncWrite = null;
        try {
            ncWrite = NetcdfFileWriteable.createNew(file.getAbsolutePath());

            // adds the global attributes
            List<SGAttribute> globalAttrList = mdFile.getAttributes();
            for (SGAttribute gattr : globalAttrList) {
            	String name = gattr.getName();
            	List<Object> values = gattr.getValues();
            	Attribute attr = new Attribute(name, values);
                ncWrite.addGlobalAttribute(gattr.getName(), attr.getValues());
            }

            // adds groups
            MDArrayNode root = new MDArrayNode(null, 0);
            SGMDArrayVariable[] aVars = this.getAssignedVariables();
            for (SGMDArrayVariable var : aVars) {
            	String name = var.getName();
            	final int sIndex = name.lastIndexOf('/');
            	if (sIndex <= 0) {
            		continue;
            	}
            	List<String> groupNameList = new ArrayList<String>();
            	String groupStr = name.substring(0, sIndex);
            	String[] tokens = groupStr.split("/");
            	for (String token : tokens) {
            		groupNameList.add(token);
            	}
            	String[] groupNameArray = new String[groupNameList.size()];
            	groupNameList.toArray(groupNameArray);
            	root.addChild(groupNameArray);
            }
            this.addGroup(ncWrite, null, root);

            // adds variables to the NetCDF file
            if (!this.addVariables(ncWrite)) {
            	return false;
            }

    		// create the file
			ncWrite.create();

			// writes data to the file
			if (!this.writeData(ncWrite)) {
				return false;
			}

        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            if (ncWrite != null) {
                try {
					ncWrite.close();
				} catch (IOException e) {
				}
            }
        }
        return true;
    }

    @Override
    public boolean saveToDataSetNetCDFFile(final File file) {
    	SGMDArrayFile mdFile = this.getMDArrayFile();
        NetcdfFileWriteable ncWrite = null;
        try {
            ncWrite = NetcdfFileWriteable.createNew(file.getAbsolutePath());

            // adds the global attributes
            List<SGAttribute> globalAttrList = mdFile.getAttributes();
            for (SGAttribute gattr : globalAttrList) {
            	String name = gattr.getName();
            	List<Object> values = gattr.getValues();
            	Attribute attr = new Attribute(name, values);
                ncWrite.addGlobalAttribute(gattr.getName(), attr.getValues());
            }

            // adds groups
            MDArrayNode root = new MDArrayNode(null, 0);
            SGMDArrayVariable[] aVars = this.getAssignedVariables();
            for (SGMDArrayVariable var : aVars) {
            	String name = var.getName();
            	final int sIndex = name.lastIndexOf('/');
            	if (sIndex <= 0) {
            		continue;
            	}
            	List<String> groupNameList = new ArrayList<String>();
            	String groupStr = name.substring(0, sIndex);
            	String[] tokens = groupStr.split("/");
            	for (String token : tokens) {
            		groupNameList.add(token);
            	}
            	String[] groupNameArray = new String[groupNameList.size()];
            	groupNameList.toArray(groupNameArray);
            	root.addChild(groupNameArray);
            }
            this.addGroup(ncWrite, null, root);

            // adds variables to the NetCDF file
            if (!this.addVariables(ncWrite)) {
            	return false;
            }

    		// create the file
			ncWrite.create();

			// writes data to the file
			if (!this.writeData(ncWrite)) {
				return false;
			}

        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            if (ncWrite != null) {
                try {
					ncWrite.close();
				} catch (IOException e) {
				}
            }
        }
        return true;
    }

    void addGroup(NetcdfFileWriteable ncWrite, Group parent, MDArrayNode node) {
    	Group g = null;
    	if (node.name != null) {
    		g = new Group(ncWrite, parent, node.name);
    		ncWrite.addGroup(parent, g);
    	}
    	for (MDArrayNode c : node.childList) {
    		this.addGroup(ncWrite, g, c);
    	}
    }

    class MDArrayNode {
    	int depth = 0;
    	String name;
    	List<MDArrayNode> childList = new ArrayList<MDArrayNode>();
    	MDArrayNode(String name, int depth) {
    		super();
    		this.name = name;
    		this.depth = depth;
    	}
    	void addChild(String[] path) {
    		if (this.depth >= path.length) {
    			return;
    		}
    		String p = path[this.depth];
			MDArrayNode matched = null;
			for (MDArrayNode child : childList) {
				if (p.equals(child.name)) {
					matched = child;
					break;
				}
			}
			if (matched != null) {
				matched.addChild(path);
			} else {
				MDArrayNode childNew = new MDArrayNode(p, this.depth + 1);
				this.childList.add(childNew);
				childNew.addChild(path);
			}
    	}
    	@Override
    	public String toString() {
    		StringBuffer sb = new StringBuffer();
    		if (this.name != null) {
    			sb.append(this.name);
    			sb.append(":");
    		}
    		sb.append(this.childList);
    		return sb.toString();
    	}
    }

    /**
     * Adds variables to a netCDF file.
     *
     * @param ncWrite
     *           a netCDF file
     * @return true if succeeded
     */
    protected abstract boolean addVariables(NetcdfFileWriteable ncWrite);

    /**
     * Writes data to a netCDF file.
     *
     * @param ncWrite
     *           a netCDF file
     * @return true if succeeded
     */
    protected abstract boolean writeData(NetcdfFileWriteable ncWrite);


    protected void appendAttribute(NetcdfFileWriteable ncWrite, SGMDArrayVariable var,
    		String ncVarName) {
		List<SGAttribute> attrList = var.getAttributes();
		for (SGAttribute attr : attrList) {
			String name = attr.getName();
			List<Object> values = attr.getValues();
			ncWrite.addVariableAttribute(ncVarName, new Attribute(name, values));
		}
    }

	protected static final String TIME_DIM_NAME = "time";

	// add time dimensions
    protected Dimension addTimeVariable(NetcdfFileWriteable ncWrite) {
		int timeLen = -1;
		SGMDArrayVariable[] vars = this.getAssignedVariables();
		for (int ii = 0; ii < vars.length; ii++) {
			Integer timeDim = vars[ii].getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
			if (timeDim != null && timeDim != -1) {
				int[] dims = vars[ii].getDimensions();
				timeLen = dims[timeDim];
				break;
			}
		}
		Dimension timeDim = null;
		if (timeLen != -1) {
			timeDim = ncWrite.addDimension(TIME_DIM_NAME, timeLen);
		}
		return timeDim;
    }

	protected boolean addSequentialIntegerNumberVariable(NetcdfFileWriteable ncWrite, Dimension dim, String name) {
		return this.addSequentialNumberVariable(ncWrite, dim, name, DataType.INT);
	}

	protected boolean addSequentialDoubleNumberVariable(NetcdfFileWriteable ncWrite, Dimension dim, String name) {
		return this.addSequentialNumberVariable(ncWrite, dim, name, DataType.DOUBLE);
	}

	private boolean addSequentialNumberVariable(NetcdfFileWriteable ncWrite, Dimension dim, String name,
			DataType dataType) {
		List<Dimension> dimList = new ArrayList<Dimension>();
		dimList.add(dim);
		Variable ncVar = ncWrite.addVariable(name, dataType, dimList);
		ncVar.addAttribute(SGDataUtility.getValueTypeAttribute(SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		return true;
	}

    protected boolean writeSequentialIntegerNumbers(NetcdfFileWriteable ncWrite, String varName,
    		final int len) {
    	return this.writeSequentialNumbers(ncWrite, varName, len, DataType.INT);
    }

    protected boolean writeSequentialDoubleNumbers(NetcdfFileWriteable ncWrite, String varName,
    		final int len) {
    	return this.writeSequentialNumbers(ncWrite, varName, len, DataType.DOUBLE);
    }

    private boolean writeSequentialNumbers(NetcdfFileWriteable ncWrite, String varName,
    		final int len, DataType dataType) {
		Array valueArray = Array.factory(dataType, new int[] { len });
		for (int ii = 0; ii < len; ii++) {
			valueArray.setInt(ii, ii);
		}
		if (!this.writeArray(ncWrite, varName, valueArray)) {
			return false;
		}
		return true;
    }

    protected boolean write1DDoubleArray(NetcdfFileWriteable ncWrite, String varName,
    		List<Dimension> ncDimList, Map<String, Integer> mdArrayIndexMap) {
    	SGMDArrayVariable mdVar = this.findVariable(varName);
    	Dimension ncDim = ncDimList.get(0);
    	Integer dimension = mdArrayIndexMap.get(ncDim.getName());
    	final int len = ncDim.getLength();
    	ArrayDouble valueArray = new ArrayDouble.D1(len);
		Index idx = valueArray.getIndex();
		for (int ii = 0; ii < len; ii++) {
			final double value = mdVar.getDoubleValue(dimension, ii);
			valueArray.setDouble(idx.set(ii), value);
		}
		if (!this.writeArray(ncWrite, varName, valueArray)) {
			return false;
		}
		return true;
    }

    protected boolean write2DDoubleArray(NetcdfFileWriteable ncWrite, String varName,
    		List<Dimension> ncDimList, Map<String, Integer> mdArrayIndexMap) {
    	SGMDArrayVariable mdVar = this.findVariable(varName);
    	Dimension ncDim1 = ncDimList.get(0);
    	Dimension ncDim2 = ncDimList.get(1);
    	Integer dimension1 = mdArrayIndexMap.get(ncDim1.getName());
    	Integer dimension2 = mdArrayIndexMap.get(ncDim2.getName());
    	final int len1 = ncDim1.getLength();
    	final int len2 = ncDim2.getLength();
    	ArrayDouble valueArray = new ArrayDouble.D2(len1, len2);
		Index idx = valueArray.getIndex();
		for (int ii = 0; ii < len1; ii++) {
			for (int jj = 0; jj < len2; jj++) {
				int[] origins = mdVar.getOrigins();
				origins[dimension1] = ii;
				origins[dimension2] = jj;
				final double value = mdVar.getDoubleValue(origins);
				valueArray.setDouble(idx.set(ii, jj), value);
			}
		}
		if (!this.writeArray(ncWrite, varName, valueArray)) {
			return false;
		}
		return true;
    }

    protected boolean write3DDoubleArray(NetcdfFileWriteable ncWrite, String varName,
    		List<Dimension> ncDimList, Map<String, Integer> mdArrayIndexMap) {
    	SGMDArrayVariable mdVar = this.findVariable(varName);
    	Dimension ncDim1 = ncDimList.get(0);
    	Dimension ncDim2 = ncDimList.get(1);
    	Dimension ncDim3 = ncDimList.get(2);
    	Integer dimension1 = mdArrayIndexMap.get(ncDim1.getName());
    	Integer dimension2 = mdArrayIndexMap.get(ncDim2.getName());
    	Integer dimension3 = mdArrayIndexMap.get(ncDim3.getName());
    	final int len1 = ncDim1.getLength();
    	final int len2 = ncDim2.getLength();
    	final int len3 = ncDim3.getLength();
    	ArrayDouble valueArray = new ArrayDouble.D3(len1, len2, len3);
		Index idx = valueArray.getIndex();
		for (int ii = 0; ii < len1; ii++) {
			for (int jj = 0; jj < len2; jj++) {
				for (int kk = 0; kk < len3; kk++) {
					int[] origins = mdVar.getOrigins();
					origins[dimension1] = ii;
					origins[dimension2] = jj;
					origins[dimension3] = kk;
					final double value = mdVar.getDoubleValue(origins);
					valueArray.setDouble(idx.set(ii, jj, kk), value);
				}
			}
		}
		if (!this.writeArray(ncWrite, varName, valueArray)) {
			return false;
		}
		return true;
    }

    protected boolean writeArray(NetcdfFileWriteable ncWrite, String varName,
    		Array array) {
		try {
			ncWrite.write(varName, array);
		} catch (IOException e) {
			return false;
		} catch (InvalidRangeException e) {
			return false;
		}
    	return true;
    }

    protected boolean writeStringArray(NetcdfFileWriteable ncWrite, String varName,
    		Array array) {
		try {
			ncWrite.writeStringData(varName, array);
		} catch (IOException e) {
			return false;
		} catch (InvalidRangeException e) {
			e.printStackTrace();
			return false;
		}
    	return true;
    }

    protected List<Dimension> addTimeDimension(SGMDArrayVariable var, Dimension timeDim, List<Dimension> dimList) {
    	return this.addDimension(var, SGIMDArrayConstants.KEY_TIME_DIMENSION, timeDim, dimList);
    }

    protected List<Dimension> addDimension(SGMDArrayVariable var, String key, Dimension dim, List<Dimension> dimList) {
    	List<Dimension> retList = new ArrayList<Dimension>(dimList);
		if (var != null && dim != null) {
			Integer dimension = var.getDimensionIndex(key);
			if (dimension != null && dimension != -1) {
				retList.add(dim);
			}
		}
		return retList;
    }

	protected boolean addDoubleVariable(NetcdfFileWriteable ncWrite, List<Dimension> dimList,
			String name) {
		SGMDArrayVariable var = this.findVariable(name);

		// get the short name and the group name
		StringBuffer sbShortName = new StringBuffer();
		StringBuffer sbGroupName = new StringBuffer();
		this.getNames(name, sbShortName, sbGroupName);

		String dimString = SGDataUtility.getDimensionString(dimList);

		Group group = ncWrite.findGroup(sbGroupName.toString());
		Variable ncVar = new Variable(ncWrite, group, null, sbShortName.toString(),
				DataType.DOUBLE, dimString);
		ncVar.addAttribute(SGDataUtility.getValueTypeAttribute(
				SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		group.addVariable(ncVar);

		// adds attributes
		if (var != null) {
			this.addAttributes(var, ncVar);
		}

		return true;
	}

	protected boolean addStringVariable(NetcdfFileWriteable ncWrite, List<Dimension> indexDimList,
			String name, final int maxStrLen) {
		List<Dimension> dimList = new ArrayList<Dimension>();
		dimList.addAll(indexDimList);

		SGMDArrayVariable var = this.findVariable(name);

		// get the short name and the group name
		StringBuffer sbShortName = new StringBuffer();
		StringBuffer sbGroupName = new StringBuffer();
		this.getNames(name, sbShortName, sbGroupName);

		Group group = ncWrite.findGroup(sbGroupName.toString());
		Variable ncVar = ncWrite.addStringVariable(sbShortName.toString(), dimList, maxStrLen);
		ncVar.addAttribute(SGDataUtility.getValueTypeAttribute(
				SGIDataColumnTypeConstants.VALUE_TYPE_TEXT));
		ncVar.setParentGroup(group);

		// adds attributes
		if (var != null) {
			this.addAttributes(var, ncVar);
		}

		return true;
	}

	private void addAttributes(SGMDArrayVariable mdVar, Variable ncVar) {
        List<SGAttribute> attrList = mdVar.getAttributes();
        for (SGAttribute attr : attrList) {
        	Attribute a = new Attribute(attr.getName(), attr.getValues());
        	ncVar.addAttribute(a);
        }
	}

	private void getNames(String name, StringBuffer sbShortName, StringBuffer sbGroupName) {
		String shortName;
		String groupName;
		final int sIndex = name.lastIndexOf('/');
		if (sIndex != -1 && sIndex != name.length() - 1) {
			shortName = name.substring(sIndex + 1);
			groupName = name.substring(0, sIndex);
		} else {
			shortName = name;
			groupName = "";
		}
		sbShortName.setLength(0);
		sbShortName.append(shortName);
		sbGroupName.setLength(0);
		sbGroupName.append(groupName);
	}

	protected boolean writeTimeData(NetcdfFileWriteable ncWrite) {
		Dimension timeDim = ncWrite.findDimension(TIME_DIM_NAME);
		if (timeDim != null) {
			if (!this.writeSequentialIntegerNumbers(ncWrite, TIME_DIM_NAME, timeDim.getLength())) {
				return false;
			}
		}
		return true;
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
    @Override
    public boolean saveToArchiveDataSetFile(final File file, final SGExportParameter mode) {
    	if (!SGDataUtility.isArchiveDataSetOperation(mode.getType())) {
    		return false;
    	}
    	SGIDataSource dataSrc = this.getDataSource();
    	if (dataSrc instanceof SGHDF5File) {
    		return this.saveToArchiveDataSetFileAsHDF5File(file, mode);
    	} else if (dataSrc instanceof SGMATLABFile) {
    		return this.saveToArchiveDataSetFileAsMATLABFile(file, mode);
    	} else if (dataSrc instanceof SGVirtualMDArrayFile) {
        	return this.saveToSameFormatFile(file, mode, 
        			SGDataUtility.getArchiveDataSetBufferPolicy(this));
    	} else {
    		return false;
    	}
    }
    
    private boolean saveToArchiveDataSetFileAsHDF5File(final File file, final SGExportParameter mode) {
		if (file.exists()) {
			// deletes existing file to avoid updating the file
			file.delete();
		}
    	IHDF5Writer writer = HDF5FactoryProvider.get().open(file);
    	try {
			SGIDataSource src = this.getDataSource();
			SGHDF5File hdf5File = (SGHDF5File) src;
			IHDF5Reader reader = hdf5File.getReader();
        	SGMDArrayVariable[] assignedVars = this.getAssignedVariables();
        	for (SGMDArrayVariable aVar : assignedVars) {
        		SGHDF5Variable hdf5Var = (SGHDF5Variable) aVar;
        		String varName = hdf5Var.getName();
        		
        		// writes data
        		HDF5DataClass dataClass = hdf5Var.getDataClass();
        		if (HDF5DataClass.FLOAT.equals(dataClass)) {
    				MDDoubleArray array = reader.readDoubleMDArray(varName);
    				
    				// set edited values
    				array = this.setEditedValues(writer, hdf5Var, array);

    				writer.writeDoubleMDArray(varName, array);
        		} else if (HDF5DataClass.INTEGER.equals(dataClass)) {
        			MDIntArray array = reader.readIntMDArray(varName);
        			writer.writeIntMDArray(varName, array);
        		} else if (HDF5DataClass.STRING.equals(dataClass)) {
        			MDArray<String> array = reader.readStringMDArray(varName);
        			writer.writeStringMDArray(varName, array);
        		} else {
        			return false;
        		}

        		// writes attributes of variables
    			List<String> attrNameList = reader.getAttributeNames(varName);
    			if (!SGDataUtility.writeHDF5Attribute(writer, reader, varName, attrNameList)) {
    				return false;
    			}
    			
    			// writes global attributes
    			final String rootPath = "/";
    			List<String> gAttrNameList = reader.getAttributeNames(rootPath);
    			if (!SGDataUtility.writeHDF5Attribute(writer, reader, rootPath, gAttrNameList)) {
    				return false;
    			}
        	}
    	} catch (HDF5Exception e) {
    		return false;
    	} finally {
    		// closes the writer
    		writer.close();
    	}
    	return true;
    }

    protected abstract MDDoubleArray setEditedValues(IHDF5Writer writer,
    		SGMDArrayVariable var, MDDoubleArray array);

    private boolean saveToArchiveDataSetFileAsMATLABFile(final File file, 
    		final SGExportParameter mode) {
		if (file.exists()) {
			// deletes existing file to avoid updating the file
			file.delete();
		}
		SGMATLABFile matFile = (SGMATLABFile) this.getDataSource();
		MatFileReader reader = matFile.getReader();
		List<MLArray> mlList = new ArrayList<MLArray>();
    	SGMDArrayVariable[] assignedVars = this.getAssignedVariables();
    	for (SGMDArrayVariable aVar : assignedVars) {
    		String name = aVar.getName();
    		MLArray array = reader.getMLArray(name);
    		
    		if (array instanceof MLDouble) {
    			// set edited values
        		array = this.setEditedValues(aVar, (MLDouble) array);
    		}
    		
			mlList.add(array);
    	}
		try {
			new MatFileWriter(file.getPath(), mlList);
		} catch (IOException e) {
			return false;
		}
		return true;
    }
    
    protected abstract MLDouble setEditedValues(SGMDArrayVariable var, 
    		MLDouble array);
    
    /**
     * Exports the data into a file of the same format.
     * 
     * @param file
     *           the file to save
     * @return true if succeeded
     */
    @Override
    public boolean saveToSameFormatFile(final File file, final SGExportParameter mode,
    		SGDataBufferPolicy policy) {
    	SGIDataSource dataSrc = this.getDataSource();
    	if (dataSrc instanceof SGHDF5File 
    			|| dataSrc instanceof SGVirtualMDArrayFile) {
    		return this.saveToHDF5File(file, mode, policy);
    	} else if (dataSrc instanceof SGMATLABFile) {
    		return this.saveToMATLABFile(file, mode, policy);
    	} else {
    		return false;
    	}
    }

    /**
     * Creates and returns a text string for the unique variable name.
     * 
     * @param def
     *           default value
     * @param vars
     *           an array of existing variables
     * @return a text string for the unique variable name
     */
	protected String getUniqueVarName(String def, SGVariable[] vars) {
		StringBuffer sb = new StringBuffer(def);
		while(true) {
			boolean exists = false;
			for (SGVariable var : vars) {
				String name = var.getName();
				if (sb.toString().equals(name)) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				break;
			}
			sb.insert(0, '_');
		}
		return sb.toString();
	}

    /**
     * Returns a text string of file extension for the file in a data set file.
     * 
     * @return a text string of file extension
     */
    @Override
    public String getDataSetFileExtension() {
    	SGIDataSource dataSrc = this.getDataSource();
    	String ext = null;
    	if (dataSrc instanceof SGHDF5File || dataSrc instanceof SGVirtualMDArrayFile) {
    		ext = SGIDataFileConstants.HDF5_FILE_EXTENSION;
    	} else if (dataSrc instanceof SGMATLABFile) {
    		ext = SGIDataFileConstants.MATLAB_FILE_EXTENSION;
    	}
    	return ext;
    }

    /**
     * Exports the data into a HDF5 file.
     * 
     * @param path
     *           the path of HDF5 file
     * @param mode
     *           the mode to save data
     * @return true if succeeded
     */
    @Override
    public boolean saveToHDF5File(final File file, final SGExportParameter mode,
    		SGDataBufferPolicy policy) {
		if (file.exists()) {
			// deletes existing file to avoid updating the file
			if (!file.delete()) {
				return false;
			}
		}
    	IHDF5Writer writer = HDF5FactoryProvider.get().open(file);
    	try {
    		return this.exportToHDF5(writer, mode, policy);
    	} finally {
    		// closes the writer
    		writer.close();
    	}
    }

    /**
     * Exports to a HDF5 file.
     * 
     * @param writer
     *           HDF5-file writer
     * @return true if succeeded
     */
    protected abstract boolean exportToHDF5(IHDF5Writer writer, final SGExportParameter mode,
    		SGDataBufferPolicy policy);

    /**
     * Exports the data into a MATLAB file.
     * 
     * @param path
     *           the path of MATLAB file
     * @return true if succeeded
     */
    @Override
    public boolean saveToMATLABFile(final File file, final SGExportParameter mode,
    		SGDataBufferPolicy policy) {
		MatFileWriter writer = new MatFileWriter();
		return this.exportToMATLAB(file, writer, mode, policy);
    }

    /**
     * Exports to a MATLAB file.
     * 
     * @param file
     *           the MATLAB file
     * @param writer
     *           MAT-file writer
     * @return true if succeeded
     */
    protected abstract boolean exportToMATLAB(File file, MatFileWriter writer, final SGExportParameter mode,
    		SGDataBufferPolicy policy);

    /**
     * Exports the data into a text file.
     * 
     * @param file
     *           the file to save
     * @param mode
     *           the mode to save data
     * @return true if succeeded
     */
    @Override
    public boolean saveToTextFile(final File file, final SGExportParameter mode,
    		SGDataBufferPolicy policy) {
    	// TODO
    	return false;
    }

    protected boolean write(IHDF5Writer writer, String name, MDArrayDataType dataType, double[] values) {
    	final boolean nFlag;
    	if (dataType != null) {
    		nFlag = VALUE_TYPE.INTEGER.equals(dataType.getValueType());
    	} else {
    		nFlag = true;
    	}
    	if (nFlag) {
    		int[] nValues = new int[values.length];
    		for (int ii = 0; ii < nValues.length; ii++) {
    			nValues[ii] = (int) values[ii];
    		}
    		writer.writeIntArray(name, nValues);
    	} else {
    		writer.writeDoubleArray(name, values);
    	}
    	return true;
    }
    
    protected boolean write(IHDF5Writer writer, String name, MDArrayDataType dataType, double[][] values) {
    	final boolean nFlag;
    	if (dataType != null) {
    		nFlag = VALUE_TYPE.INTEGER.equals(dataType.getValueType());
    	} else {
    		nFlag = true;
    	}
    	if (nFlag) {
    		int[][] nValues = new int[values.length][];
    		for (int ii = 0; ii < nValues.length; ii++) {
    			nValues[ii] = new int[values[ii].length];
    			for (int jj = 0; jj < nValues[ii].length; jj++) {
        			nValues[ii][jj] = (int) values[ii][jj];
    			}
    		}
    		writer.writeIntMatrix(name, nValues);
    	} else {
    		writer.writeDoubleMatrix(name, values);
    	}
    	return true;
    }

    protected boolean write(IHDF5Writer writer, String name, String[][] values) {
    	final int dim0 = values.length;
    	final int dim1 = values[0].length;
    	String[] flattenedArray = new String[dim0 * dim1];
    	int cnt = 0;
    	for (int ii = 0; ii < values.length; ii++) {
    		for (int jj = 0; jj < values[ii].length; jj++) {
    			flattenedArray[cnt] = values[ii][jj];
    			cnt++;
    		}
    	}
    	MDArray<String> array = new MDArray<String>(flattenedArray, new int[] { dim0, dim1 });
    	writer.writeStringMDArray(name, array);
    	return true;
    }

    protected MDArrayDataType getExportNumberDataType(SGMDArrayVariable var, SGExportParameter mode,
    		SGDataBufferPolicy policy) {
    	if (SGDataUtility.isArchiveDataSetOperation(mode.getType())) {
    		return var.getDataType();
    	} else {
    		return var.getExportFloatingNumberDataType();
    	}
    }

    /**
     * Returns a text string for the command of the column types.
     * 
     * @return a text string for the command of the column types
     */
	@Override
    public String getColumnTypeCommandString() {
		List<String> varList = new ArrayList<String>();
		List<String> columnTypeList = new ArrayList<String>();
		this.getVarNameColumnTypeList(varList, columnTypeList);
		return SGDataUtility.getDataColumnTypeCommand(varList, columnTypeList);
	}

    protected void getVarNameColumnTypeList(List<String> varList, 
    		List<String> columnTypeList) {
    	// do nothing by default
    }
    
	protected String getOneDimensionalVarCommandString(SGMDArrayVariable var) {
		StringBuffer sb = new StringBuffer();
		sb.append(var.getName());
		sb.append(':');
		sb.append(var.getGenericDimensionIndex());
		return sb.toString();
	}

	protected String getTwoDimensionalVarCommandString(SGMDArrayVariable var,
			String keyX, String keyY) {
		StringBuffer sb = new StringBuffer();
		sb.append(var.getName());
		sb.append(':');
		sb.append(var.getDimensionIndex(keyX));
		sb.append(':');
		sb.append(var.getDimensionIndex(keyY));
		return sb.toString();
	}

    /**
     * Returns a text string for the command of the origin.
     * 
     * @return a text string for the command of the origin
     */
    @Override
    public String getOriginCommandString() {
    	SGMDArrayVariable[] vars = this.getAssignedVariables();
    	StringBuffer sb = new StringBuffer();
    	sb.append('(');
    	int cnt = 0;
    	for (int ii = 0; ii < vars.length; ii++) {
    		SGMDArrayVariable var = vars[ii];
    		if (cnt > 0) {
    			sb.append(',');
    		}
    		StringBuffer originSb = new StringBuffer();
    		int[] origins = var.getOrigins();
    		for (int jj = 0; jj < origins.length; jj++) {
    			if (jj > 0) {
    				originSb.append(',');
    			}
    			originSb.append(jj);
    			originSb.append(':');
    			originSb.append(origins[jj]);
    		}
    		StringBuffer varSb = new StringBuffer();
    		varSb.append(var.getName());
    		varSb.append('(');
    		varSb.append(originSb.toString());
    		varSb.append(')');
    		sb.append(varSb.toString());
    		cnt++;
    	}
    	sb.append(')');
    	return sb.toString();
    }

    /**
     * Returns a text string for the command of animation frame dimension.
     * 
     * @return a text string for the command of animation frame dimension
     */
    public String getAnimationFrameDimensionCommandString() {
    	if (!this.isTimeDimensionAvailable()) {
    		return null;
    	}
    	return this.getDimensionCommandString(SGIMDArrayConstants.KEY_TIME_DIMENSION);
    }

    protected String getDimensionCommandString(final String key) {
    	SGMDArrayVariable[] vars = this.getAssignedVariables();
    	StringBuffer sb = new StringBuffer();
    	sb.append('(');
    	int cnt = 0;
    	for (int ii = 0; ii < vars.length; ii++) {
    		SGMDArrayVariable var = vars[ii];
    		Integer index = var.getDimensionIndex(key);
    		if (!SGDataUtility.isValidDimensionIndex(index)) {
    			continue;
    		}
    		if (cnt > 0) {
    			sb.append(", ");
    		}
    		StringBuffer varSb = new StringBuffer();
    		varSb.append(var.getName());
    		varSb.append(':');
    		varSb.append(index);
    		sb.append(varSb.toString());
    		cnt++;
    	}
    	sb.append(')');
    	return sb.toString();
    }
    
    /**
     * Sets the data source.
     * 
     * @param src
     *           a data source
     */
    @Override
    public void setDataSource(SGIDataSource src) {
    	if (!(src instanceof SGMDArrayFile)) {
    		throw new IllegalArgumentException("Invalid data source.");
    	}
    	super.setDataSource(src);
    	SGMDArrayFile mdFile = (SGMDArrayFile) src;
    	SGMDArrayVariable[] vars = mdFile.getVariables();
    	for (int ii = 0; ii < vars.length; ii++) {
    		SGMDArrayVariable var = (SGMDArrayVariable) vars[ii].clone();
    		var.setDimensionIndices(this.mVariables[ii].getDimensionIndices());
    		this.mVariables[ii] = var;
    	}
    }

    protected SGMDArrayVariable findVariable(SGMDArrayFile mdfile, 
    		SGMDArrayVariable var) {
    	if (var == null) {
    		return null;
    	}
    	SGMDArrayVariable ret = mdfile.findVariable(var.getName());
		ret.setDimensionIndices(new HashMap<String, Integer>(
				var.getDimensionIndices()));
    	return ret;
    }
    
    protected SGMDArrayVariable[] findVariables(SGMDArrayFile mdfile, 
    		SGMDArrayVariable[] vars) {
    	if (vars == null) {
    		return null;
    	}
    	SGMDArrayVariable[] ret = new SGMDArrayVariable[vars.length];
    	for (int ii = 0; ii < vars.length; ii++) {
    		SGMDArrayVariable var = this.findVariable(mdfile, vars[ii]);
    		ret[ii] = var;
    	}
    	return ret;
    }

    /**
     * Returns whether the animation is supported in this data.
     * 
     * @return true if the animation is supported in this data
     */
    @Override
    public boolean isAnimationSupported() {
    	return true;
    }

    /**
     * Returns a text string for the tooltip.
     * 
     * @return a text string for the tooltip
     */
    @Override
    public String getToolTipTextNotSpatiallyVaried() {
    	// always returns null
    	return null;
    }

	protected String getToolTipSpatiallyVaried(final int index, SGIntegerSeriesSet stride) {
    	StringBuffer sb = new StringBuffer();
		SGMDArrayVariable[] vars = this.getAssignedVariables();
		for (int ii = 0; ii < vars.length; ii++) {
			if (ii > 0) {
				sb.append(",<br>");
			}
			sb.append(this.getToolTipSpatiallyVaried(vars[ii], index));
		}
    	return sb.toString();
    }

	protected String getToolTipSpatiallyVaried(SGMDArrayVariable var, final int arrayIndex) {
		StringBuffer sb = new StringBuffer();
    	if (var != null) {
    		int[] origins = var.getOrigins();
    		final int dim = var.getGenericDimensionIndex();
    		origins[dim] = arrayIndex;
    		String str = this.getToolTip(var, origins);
    		sb.append(str);
    	}
    	return sb.toString();
	}

	protected String getToolTip(SGMDArrayVariable var, final int[] origins) {
		StringBuffer sb = new StringBuffer();
		sb.append(var.getName());
		sb.append("=[");
		for (int ii = 0; ii < origins.length; ii++) {
			if (ii > 0) {
				sb.append(',');
			}
			sb.append(origins[ii]);
		}
		sb.append(']');
    	return sb.toString();
	}

    protected int[] getFactors(int[] dims, final boolean dimReversed) {
		final int[] factors = new int[dims.length];
		int temp = 1;
		if (dimReversed) {
			for (int jj = dims.length - 1; jj >= 0; jj--) {
				factors[jj] = temp;
				temp *= dims[jj];
			}
		} else {
			for (int jj = 0; jj < dims.length; jj++) {
				factors[jj] = temp;
				temp *= dims[jj];
			}
		}
		return factors;
    }

}
