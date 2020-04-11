package jp.riken.brain.ni.samuraigraph.data;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIStringModifier;
import jp.riken.brain.ni.samuraigraph.base.SGITextDataConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGNamedStringBlock;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.Value;

import org.w3c.dom.Element;

import ucar.ma2.Array;
import ucar.ma2.ArrayByte;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayLong;
import ucar.ma2.ArrayShort;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Group;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

/**
 * The base class for netCDF data.
 *
 */
public abstract class SGNetCDFData extends SGArrayData
		implements SGIIndexData, SGITextDataConstants, SGIDataColumnTypeConstants, SGINetCDFConstants {

    /**
     * The map of origin of coordinate variables.
     * Keys are the name of coordinate variables and values are origin of each variables.
     */
    protected Map<String, Integer> mOriginMap = new HashMap<String, Integer>();

    /**
     * The variable for time values.
     */
    protected SGNetCDFVariable mTimeVariable = null;

    /**
     * The stride for the time.
     */
    protected SGIntegerSeriesSet mTimeStride = null;

    /**
     * The variable of the index.
     */
    protected SGNetCDFVariable mIndexVariable = null;

    /**
     * The stride for the index.
     */
    protected SGIntegerSeriesSet mIndexStride = null;

    /**
     * The default constructor.
     *
     */
    public SGNetCDFData() {
        super();
    }

    /**
     * Builds a data object with given netCDF data.
     *
     * @param ncfile
     *            netCDF data file
     * @param obs
     *            netCDF data observer
     * @param timeInfo
     *            information of time values
     * @param indexInfo
     *            information of index values
     * @param indexStride
     *            stride for the index
     * @param strideAvailable
     *            flag whether to set available the stride
     */
    public SGNetCDFData(SGNetCDFFile ncfile, SGDataSourceObserver obs, SGNetCDFDataColumnInfo timeInfo,
    		SGNetCDFDataColumnInfo indexInfo, final SGIntegerSeriesSet indexStride,
    		final boolean strideAvailable) {

        super(ncfile, obs, strideAvailable);

        // initialize the origin map
        List<Dimension> dimList = ncfile.getDimensions();
        for (Dimension dim : dimList) {
            String dimName = dim.getName();
            this.mOriginMap.put(dimName, 0);
        }

        // setup time variable
        if (timeInfo != null) {
            SGNetCDFVariable tVar = ncfile.findVariable(timeInfo.getName());
            if (tVar == null) {
                throw new IllegalArgumentException("Illegal variable name: " + timeInfo.getName());
            }
            if (!tVar.isCoordinateVariable()) {
                throw new IllegalArgumentException("Time variable must be a coordinate variable.");
            }
            this.setTimeVariable(tVar);
        }

        // setup index variable and the stride
        if (indexInfo != null) {
            this.mIndexVariable = this.findIndexVariable(ncfile, indexInfo);
            Dimension indexDim = this.mIndexVariable.getDimension(0);
            this.mIndexStride = this.createStride(indexStride, indexDim);
        }

        if (this.mTimeVariable != null && this.mIndexVariable != null) {
        	Dimension tDim = this.mTimeVariable.getDimension(0);
        	Dimension iDim = this.mIndexVariable.getDimension(0);
        	if (tDim.equals(iDim)) {
        		throw new IllegalArgumentException("Dimensions of time and index variables must not be the same.");
        	}
        }
    }

    protected void setTimeVariable(SGNetCDFVariable tVar) {
    	if (SGUtility.equals(this.mTimeVariable, tVar)) {
    		return;
    	}
        this.mTimeVariable = tVar;
    	if (tVar != null) {
            Dimension timeDim = tVar.getDimension(0);
            this.mTimeStride = this.createStride(null, timeDim);
    	} else {
    		this.mTimeStride = null;
    	}
    }

    protected SGNetCDFVariable findIndexVariable(SGNetCDFFile ncfile, SGNetCDFDataColumnInfo indexInfo) {
    	String name = indexInfo.getName();
    	SGNetCDFVariable indexVar = ncfile.findVariable(name);
        if (indexVar == null) {
            throw new IllegalArgumentException("Variable of given name is not found: " + name);
        }
        if (!indexVar.isCoordinateVariable()) {
            throw new IllegalArgumentException("Index variable must be a coordinate variable: " + name);
        }
        return indexVar;
    }

    /**
     * Returns the netCDF file.
     *
     * @return the netCDF file
     */
    public SGNetCDFFile getNetcdfFile() {
        return (SGNetCDFFile) this.getDataSource();
    }

    /**
     * Returns the copy of this data object.
     *
     * @return
     *         a copy of this data object
     */
    public Object clone() {
        SGNetCDFData data = (SGNetCDFData) super.clone();
        data.mOriginMap = new HashMap<String, Integer>(this.mOriginMap);
        data.mIndexStride = this.getIndexStride();
        data.mTimeStride = this.getTimeStride();
        return data;
    }

    /**
     * Disposes of this data object.
     * Do not call any method of this object after called this method.
     */
    public void dispose() {
        super.dispose();

        // clear origin map
        this.mOriginMap.clear();
        this.mOriginMap = null;

        // clear variables
        this.mTimeVariable = null;
        this.mIndexVariable = null;

        this.mTimeStride = null;
        this.mIndexStride = null;
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
        if (!(data instanceof SGNetCDFData)) {
            throw new IllegalArgumentException("Given data is not the instance of SGNetCDFData class.");
        }
        SGNetCDFData nData = (SGNetCDFData) data;
        this.mOriginMap = new HashMap<String, Integer>(nData.mOriginMap);
        this.mIndexVariable = nData.mIndexVariable;
    	this.mIndexStride = nData.mIndexStride;
        this.mTimeVariable = nData.mTimeVariable;
    	this.mTimeStride = (nData.mTimeStride != null) ? (SGIntegerSeriesSet) nData.mTimeStride.clone() : null;
        return true;
    }

    /**
     * Gets the properties of this data.
     * @param p
     *      the properties of this data
     * @return
     *      true if succeeded
     */
    public boolean getProperties(SGProperties p) {
        if (!(p instanceof NetCDFDataProperties)) {
            return false;
        }
        if (super.getProperties(p) == false) {
        	return false;
        }
        NetCDFDataProperties np = (NetCDFDataProperties) p;
        np.timeName = (this.mTimeVariable != null) ? this.mTimeVariable.getName() : null;
    	np.indexName = (this.mIndexVariable != null) ? this.mIndexVariable.getName() : null;
        np.originMap = new HashMap<String, Integer>(this.mOriginMap);
        np.indexStride = this.getIndexStrideInstance();
        np.timeStride = this.getTimeStrideInstance();
        return true;
    }

    /**
     * Set properties to this data.
     *
     * @param p
     *          properties to be set
     * @return true if succeeded
     */
    public boolean setProperties(SGProperties p) {
        if (!(p instanceof NetCDFDataProperties)) {
            return false;
        }
        if (super.setProperties(p) == false) {
        	return false;
        }
        NetCDFDataProperties np = (NetCDFDataProperties) p;
        SGNetCDFFile ncfile = this.getNetcdfFile();
        this.mTimeVariable = ncfile.findVariable(np.timeName);
		this.mIndexVariable = ncfile.findVariable(np.indexName);
		this.mOriginMap = new HashMap<String, Integer>(np.originMap);
		this.setIndexStrideSub(np.indexStride);
		this.setTimeStride(np.timeStride);
        return true;
    }

    /**
     * Write properties of this object to the Element.
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

        // write the origins
        StringBuffer sb = new StringBuffer();
        Iterator<String> itr = this.mOriginMap.keySet().iterator();
        if (SGDataUtility.isArchiveDataSetOperation(type.getType())) {
        	// archive
        	List<String> varNameList = new ArrayList<String>();
        	SGNetCDFVariable[] vars = this.getAssignedVariables();
        	for (SGNetCDFVariable var : vars) {
        		varNameList.add(var.getName());
        	}
        	int cnt = 0;
            while (itr.hasNext()) {
                String key = itr.next();
                if (!varNameList.contains(key)) {
                	continue;
                }
                if (cnt > 0) {
                    sb.append(',');
                }
                Integer value = this.mOriginMap.get(key);
                sb.append(key);
                sb.append('=');
                sb.append(value);
                cnt++;
            }
        } else {
        	// export
        	int cnt = 0;
            while (itr.hasNext()) {
                if (cnt > 0) {
                    sb.append(',');
                }
                String key = itr.next();
                Integer value = this.mOriginMap.get(key);
                sb.append(key);
                sb.append('=');
                sb.append(value);
                cnt++;
            }
        }
        el.setAttribute(SGIDataPropertyKeyConstants.KEY_ORIGIN_MAP, sb.toString());

        // time variable
        if (this.isTimeVariableAvailable()) {
            el.setAttribute(SGIDataPropertyKeyConstants.KEY_TIME_VARIABLE_NAME, this.mTimeVariable.getValidName());
        }

        // index variable
        if (this.isIndexAvailable()) {
            el.setAttribute(SGIDataPropertyKeyConstants.KEY_INDEX_VARIABLE_NAME, this.mIndexVariable.getValidName());
        }

        // stride
        if (this.isIndexAvailable()) {
            el.setAttribute(SGIDataPropertyKeyConstants.KEY_INDEX_ARRAY_SECTION, this.mIndexStride.toString());
        }

        return true;
    }
    
    /**
     * Sets the origin of a coordinate variable.
     *
     * @param name
     *           the name of the coordinate variable
     * @param origin
     *           a value to set to the origin of the coordinate variable
     * @return true if succeeded
     */
    public boolean setOrigin(final String name, final int origin) {
        Dimension dim = this.getNetcdfFile().findDimension(name);
        if (dim == null) {
        	return false;
        }
        final int len = dim.getLength();
        if (origin < 0 || origin >= len) {
        	return false;
        }
        this.mOriginMap.put(dim.getName(), origin);
        return true;
    }

    /**
     * Returns the origin of a coordinate variable.
     *
     * @param name
     *           the name of the coordinate variable
     * @return the origin of the coordinate variable
     */
    public int getOrigin(final String name) {
        Dimension dim = this.getNetcdfFile().findDimension(name);
        if (dim == null) {
            throw new IllegalArgumentException("Dimension is not found: " + name);
        }
        Integer num = this.mOriginMap.get(name);
        if (num == null) {
            throw new Error("Origin is not found in the map for the coordinate variable: " + name);
        }
        return num.intValue();
    }

    /**
     * Returns the value of coordinate variable at a given index.
     *
     * @param name
     *           the name of a coordinate variable
     * @param index
     *           the array index
     * @return the value of coordinate variable at a given index
     *
     * @throws IOException
     */
    public double getCoordinateValue(final String name, final int index) throws IOException {
    	SGNetCDFVariable var = this.getNetcdfFile().findVariable(name);
        if (var == null) {
            throw new IllegalArgumentException("Variable is not found: " + name);
        }
        if (var.isCoordinateVariable() == false) {
            throw new IllegalArgumentException("Not a coordinate variable: " + name);
        }
        if (var instanceof SGCoordinateVariable) {
            SGCoordinateVariable cVar = (SGCoordinateVariable) var;
            final int len = cVar.getLength();
            if (index < 0 || index >= len) {
            	throw new IllegalArgumentException("Index out of bounds [0, " + len + " + ]: " + index);
            }
            return index;
        } else {
            Array array = var.getVariable().read();
            final int size = (int) array.getSize();
            if (index < 0 || index >= size) {
                throw new IllegalArgumentException("Index out of bounds [0, " + size + " + ]: " + index);
            }
            double[] doubleArray = new double[size];
            for (int ii = 0; ii < size; ii++) {
            	doubleArray[ii] = array.getDouble(ii);
            }
            final double value = SGDataUtility.getCoordinateVariableValue(
            		doubleArray, index);
            return value;
//          return array.getDouble(index);
        }
    }

    /**
     * Returns a text string of the name with units string.
     *
     * @param var
     *          the variable for an axis
     * @return a text string of the name with units string
     */
    protected String getNameWithUnit(SGNetCDFVariable var) {
        final String name = var.getNameInPriorityOrder();
        final String unit = var.getUnitsString();
        StringBuffer sb = new StringBuffer();
        sb.append(name);
        if (unit != null) {
            sb.append(" [");
            sb.append(unit);
            sb.append(']');
        }
        return sb.toString();
    }

    /**
     * A class of netCDF data properties.
     */
    public static abstract class NetCDFDataProperties extends ArrayDataProperties {

        protected String timeName = null;

        protected String indexName = null;

        protected Map<String, Integer> originMap = new HashMap<String, Integer>();

        protected SGIntegerSeriesSet indexStride = null;

        protected SGIntegerSeriesSet timeStride = null;

        /**
         * The default constructor.
         */
        public NetCDFDataProperties() {
            super();
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
        	super.dispose();
            this.timeName = null;
            this.indexName = null;
            this.originMap.clear();
            this.originMap = null;
            this.indexStride = null;
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
        	if (!(obj instanceof NetCDFDataProperties)) {
        		return false;
        	}
        	if (super.equals(obj) == false) {
        		return false;
        	}
            NetCDFDataProperties p = (NetCDFDataProperties) obj;
            if (this.originMap.equals(p.originMap) == false) {
                return false;
            }
        	return true;
        }

        @Override
        public boolean hasEqualSize(DataProperties dp) {
        	if (!(dp instanceof NetCDFDataProperties)) {
        		return false;
        	}
            NetCDFDataProperties p = (NetCDFDataProperties) dp;
            if (SGUtility.equals(this.indexStride, p.indexStride) == false) {
            	return false;
            }
            if (SGUtility.equals(this.timeStride, p.timeStride) == false) {
            	return false;
            }
        	return true;
        }

        /**
         * Returns whether this data property has the equal column types with given data property.
         *
         * @param dp
         *          a data property
         * @return true if this data property has the equal column types with given data property
         */
        @Override
        public boolean hasEqualColumnTypes(DataProperties dp) {
            if ((dp instanceof NetCDFDataProperties) == false) {
                return false;
            }
            NetCDFDataProperties p = (NetCDFDataProperties) dp;
            if (SGUtility.equals(this.indexName, p.indexName) == false) {
            	return false;
            }
            if (SGUtility.equals(this.timeName, p.timeName) == false) {
            	return false;
            }
        	return true;
        }

        /**
         * Returns a copy of this object.
         *
         * @return a copy of this object
         */
        public Object copy() {
        	NetCDFDataProperties p = (NetCDFDataProperties) super.copy();
        	p.originMap = new HashMap<String, Integer>(this.originMap);
        	p.indexStride = (this.indexStride != null) ? (SGIntegerSeriesSet) this.indexStride.clone() : null;
        	p.timeStride = (this.timeStride != null) ? (SGIntegerSeriesSet) this.timeStride.clone() : null;
        	return p;
        }
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

        // set the origins
        for (int ii = 0; ii < colInfo.length; ii++) {
            SGNetCDFDataColumnInfo nCol = (SGNetCDFDataColumnInfo) colInfo[ii];
            if (nCol.isCoordinateVariable()) {
                String name = nCol.getName();
                this.mOriginMap.put(name, nCol.getOrigin());
            }
        }

        return true;
    }

    /**
     * Returns an array of variables that are assigned the column type.
     *
     * @return an array of variables
     */
    public abstract SGNetCDFVariable[] getAssignedVariables();

    /**
     * Returns whether the coordinate variable is fixed.
     *
     * @param name
     *           the name of a coordinate variable
     * @return true if fixed
     */
    public boolean isFixedCoordinateVariable(String name) {
    	SGNetCDFVariable var = this.getNetcdfFile().findVariable(name);
        if (var == null) {
            throw new IllegalArgumentException("Variable is not found: " + name);
        }
        if (var.isCoordinateVariable() == false) {
            throw new IllegalArgumentException("Not a coordinate variable: " + name);
        }
        SGNetCDFVariable[] vars = this.getAssignedVariables();
        for (int ii = 0; ii < vars.length; ii++) {
            if (name.equals(vars[ii].getName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Read and returns the variable names.
     *
     * @param el
     *            an Element object
     * @param key
     *            the key of name property
     * @return the indices it exists
     */
    protected String[] readNames(Element el, String key) {
        String str = el.getAttribute(key);
//        String[] array = null;
//        if (str.length() != 0) {
//            final int start = str.indexOf('{');
//            if (start == -1) {
//                return null;
//            }
//            final int end = str.lastIndexOf('}');
//            if (end == -1) {
//                return null;
//            }
//            if (start > end) {
//                return null;
//            }
//            String sub = str.substring(start + 1, end);
//            array = sub.split(",");
//            for (int ii = 0; ii < array.length; ii++) {
//                array[ii] = array[ii].trim();
//            }
//        }
//        return array;
        return SGUtilityText.parseStrings(str);
    }

//    /**
//     * Returns an array of values of the coordinate variable.
//     *
//     * @param stride
//     *           the stride of an array
//     * @return an array of values of the coordinate variable
//     */
//    protected double[] getCoordinateValueArray(SGNetCDFVariable var, SGIntegerSeriesSet stride) {
//    	return this.getCoordinateValueArray(var, stride, false);
//    }

    protected double[] getCoordinateValueArray(SGNetCDFVariable var, SGIntegerSeriesSet stride,
    		final boolean all, final boolean removeInvalidValues) {
    	final double[] dArray;
    	if (all) {
    		dArray = this.createCoordinateValueArray(var, removeInvalidValues);
    	} else {
        	dArray = this.createCoordinateValueArray(var, stride, removeInvalidValues);
    	}
        return dArray;
    }

    protected double getCoordinateValueAt(SGNetCDFVariable var, SGIntegerSeriesSet stride,
    		final int index) {
		return this.createCoordinateValueArray(var, true)[index];
    }

    /**
     * Creates and returns an array of values of given coordinate variable.
     *
     * @param var
     *           a coordinate variable
     * @return an array of values of given variable
     */
    private double[] createCoordinateValueArray(SGNetCDFVariable var,
    		final boolean removeInvalidValues) {
    	double[] valueArray = null;
        if (var instanceof SGCoordinateVariable) {
            SGCoordinateVariable cVar = (SGCoordinateVariable) var;
            final int len = cVar.getLength();
            valueArray = new double[len];
            for (int ii = 0; ii < valueArray.length; ii++) {
            	valueArray[ii] = ii;
            }
        } else {
            Array array = null;
            try {
                array = var.getVariable().read();
            } catch (IOException e) {
                return null;
            }
            final int size = (int) array.getSize();
            valueArray = new double[size];
            for (int ii = 0; ii < size; ii++) {
            	valueArray[ii] = array.getDouble(ii);
            }
        }
		final double[] dArray = removeInvalidValues ? this.removeFillValue(var,
				valueArray) : valueArray;
        return dArray;
    }

//    /**
//     * Returns an array of values of given variable.
//     *
//     * @param var
//     *           a variable
//     * @param cVars
//     *           an array of coordinate variables
//     * @return an array of values of given variable
//     */
//    protected double[] getValueArray(SGNetCDFVariable var, SGNetCDFVariable[] cVars,
//    		final boolean removeInvalidValues) {
//    	return this.getValueArray(var, cVars, this.mOriginMap, false, removeInvalidValues);
//    }

	protected double[] getValueArray(SGNetCDFVariable var,
			SGNetCDFVariable[] cVars, final boolean all,
			final boolean removeInvalidValues) {
		return this.getValueArray(var, cVars, this.mOriginMap, all,
				removeInvalidValues);
	}

	protected double getValueAt(SGNetCDFVariable var,
			SGNetCDFVariable[] cVars, final int[] indices) {
		return this.getValueAt(var, cVars, this.mOriginMap, indices);
	}

	protected double[] getValueArray(SGNetCDFVariable var,
			SGNetCDFVariable[] cVars, Map<String, Integer> originMap,
			final boolean all, final boolean removeInvalidValues) {
		return this.getValueArray(var, cVars, originMap, all, var.getName(),
				removeInvalidValues);
	}

    protected double[] getValueArray(SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, Integer> originMap, final boolean all, final String cacheKey,
    		final boolean removeInvalidValues) {

    	// returns date values
    	if (var instanceof SGDateVariable) {
    		SGDateVariable dVar = (SGDateVariable) var;
    		double[] dArray = dVar.getNumberArray();
    		if (all) {
        		return dArray;
    		} else {
                Map<String, SGIntegerSeriesSet> dimensionStrideMap = this.getDimensionStrideMap();
                SGIntegerSeriesSet stride = dimensionStrideMap.get(cVars[0].getName());
                int[] indices = stride.getNumbers();
                double[] ret = new double[indices.length];
                for (int ii = 0; ii < ret.length; ii++) {
                	ret[ii] = dArray[indices[ii]];
                }
                return ret;
    		}
    	}

    	final double[] valueArray;
    	if (all) {
    		// returns an array of all values
            valueArray = this.createAllValueArray(var, cVars, originMap);
    	} else {
        	// creates an array
            Map<String, SGIntegerSeriesSet> dimensionStrideMap = this.getDimensionStrideMap();
            valueArray = this.createValueArray(var, cVars, originMap, dimensionStrideMap);
    	}
		final double[] dArray = removeInvalidValues ? this.removeFillValue(var,
				valueArray) : valueArray;
        return dArray;
    }
    
    protected double getValueAt(SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, Integer> originMap, int[] indices) {

    	// returns date values
    	if (var instanceof SGDateVariable) {
    		SGDateVariable dVar = (SGDateVariable) var;
    		double[] dArray = dVar.getNumberArray();
    		return dArray[indices[0]];
    	}

        // get dimensions
        List<Dimension> dimList = var.getDimensions();
        
        String[] cNames = new String[cVars.length];
        for (int ii = 0; ii < cVars.length; ii++) {
        	cNames[ii] = cVars[ii].getName();
        }

		// creates a text string specifying the section
		StringBuffer sb = new StringBuffer();
		for (int ii = 0; ii < dimList.size(); ii++) {
			if (ii > 0) {
				sb.append(',');
			}
			Dimension dim = dimList.get(ii);
			String dimName = dim.getName();

			int index = -1;
			for (int jj = 0; jj < cVars.length; jj++) {
				if (cNames[jj].equals(dimName)) {
					index = indices[jj];
					break;
				}
			}
			
			final int origin;
			if (index != -1) {
				origin = index;
			} else {
				origin = originMap.get(dimName);
			}
			SGIntegerSeries series = new SGIntegerSeries(origin);
			String str = getNetCDFStride(series);
			sb.append(str);
		}
        Array data = null;
        try {
            data = var.read(sb.toString());
        } catch (InvalidRangeException e) {
            return Double.NaN;
        } catch (IOException e) {
            return Double.NaN;
        }
        Array reducedData = data.reduce();
        return reducedData.getDouble(0);
    }

    protected String[] getStringArray(SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, Integer> originMap, final boolean all) {

    	// returns text array
    	if (var instanceof SGCharVariable) {
    		return ((SGCharVariable) var).getStringArray();
    	}

    	final String[] valueArray;
    	if (all) {
    		// returns an array of all values
            valueArray = this.createStringArray(var, cVars, originMap);
    	} else {
        	// creates an array
            Map<String, SGIntegerSeriesSet> dimensionStrideMap = this.getDimensionStrideMap();
            valueArray = this.createStringArray(var, cVars, originMap, dimensionStrideMap);
    	}
    	return valueArray;
    }

    protected double[] removeFillValue(final SGNetCDFVariable var, final double[] valueArray) {
        double[] validRange = var.getValidRange();
        Number fillValue = var.getFillValue();
        Number missingValue = var.getMissingValue();
    	Number min = var.getValidMin();
		Number max = var.getValidMax();
        double[] dArray = new double[valueArray.length];
        for (int ii = 0; ii < dArray.length; ii++) {
            double d = valueArray[ii];
            boolean valid = true;
            if (validRange != null) {
            	if (d < validRange[0] || d > validRange[1]) {
            		valid = false;
            	}
            } else {
            	if (min != null || max != null) {
                	if (min != null) {
                		if (d < min.doubleValue()) {
                    		valid = false;
                		}
                	}
                	if (valid) {
                    	if (max != null) {
                			if (d > max.doubleValue()) {
                				valid = false;
                			}
                    	}
                	}
            	} else {
                	if (fillValue != null) {
                    	if (d == fillValue.doubleValue()) {
                    		valid = false;
                    	}
                    } else if (missingValue != null) {
                    	if (d == missingValue.doubleValue()) {
                    		valid = false;
                    	}
                    }
            	}
            }
            dArray[ii] = valid ? d : Double.NaN;
        }
        return dArray;
    }

    protected List<Integer> getCoordinateVariableIndices(SGNetCDFVariable var,
    		SGNetCDFVariable[] cVars) {
        List<Integer> cIndices = new ArrayList<Integer>();
        for (int ii = 0; ii < cVars.length; ii++) {
            final int cIndex = var.findDimensionIndex(cVars[ii].getName());
            cIndices.add(cIndex);
        }
        return cIndices;
    }

    protected abstract double[] arrangeValueArray(Array data, SGNetCDFVariable var,
            SGNetCDFVariable[] cVars, Map<String, SGIntegerSeries> dimensionStrideMap);

    protected abstract String[] arrangeStringArray(ArrayChar data, SGNetCDFVariable var,
            SGNetCDFVariable[] cVars, Map<String, SGIntegerSeries> dimensionStrideMap);

    protected double[] arrange1Dim(Array data) {
    	if (data.getRank() == 0) {
			return new double[] { data.getDouble(0) };
    	}
        final int size = (int) data.getSize();
        double[] dArray = new double[size];
        for (int ii = 0; ii < size; ii++) {
            dArray[ii] = data.getDouble(ii);
        }
        return dArray;
    }

    protected String[] arrangeString1Dim(ArrayChar data, final int num) {
    	if (data.getRank() == 0) {
        	String str = this.arrangeString(data.getString(0));
			return new String[] { str };
    	}
        String[] dArray = new String[num];
        for (int ii = 0; ii < num; ii++) {
			dArray[ii] = this.arrangeString(data.getString(ii));
        }
        return dArray;
    }

    private String arrangeString(String str) {
    	String strNew;
        try {
        	strNew = this.transformByteString(str);
		} catch (UnsupportedEncodingException e) {
			strNew = "";
		}
		return strNew;
    }

    protected double[] arrange2Dim(Array data, SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, SGIntegerSeries> dimensionStrideMap) {

    	if (data.getRank() == 0) {
			return new double[] { data.getDouble(0) };
    	}

        // a list of dimensions of coordinate variables
        List<Dimension> cDimList = new ArrayList<Dimension>();
        for (int ii = 0; ii < cVars.length; ii++) {
            cDimList.add(cVars[ii].getDimension(0));
        }

        // get dimensions
        List<Dimension> dimList = var.getDimensions();
        final int dimSize = dimList.size();
        List<Dimension> redDimList = new ArrayList<Dimension>();
        for (int ii = 0; ii < dimSize; ii++) {
            Dimension dim = dimList.get(ii);
            if (cDimList.contains(dim)) {
                redDimList.add(dim);
            }
        }

    	final String firstName = redDimList.get(0).getName();
    	final String secondName = redDimList.get(1).getName();
    	final int firstLen = dimensionStrideMap.get(firstName).getLength();
    	final int secondLen = dimensionStrideMap.get(secondName).getLength();

        Class<?> elType = data.getElementType();
        Object obj = data.copyToNDJavaArray();

        double[] dim1Array = null;
        double[][] dim2Array = null;
        if (elType.equals(double.class)) {
        	if (obj instanceof double[]) {
                dim1Array = (double[]) obj;
        	} else if (obj instanceof double[][]) {
                dim2Array = (double[][]) obj;
        	}
        } else {
            if (elType.equals(float.class)) {
            	if (obj instanceof float[]) {
                    float[] array = (float[]) obj;
            		dim1Array = new double[array.length];
            		for (int ii = 0; ii < array.length; ii++) {
            			dim1Array[ii] = array[ii];
            		}
            	} else if (obj instanceof float[][]) {
                    float[][] array = (float[][]) obj;
                    dim2Array = new double[firstLen][];
                    for (int ii = 0; ii < firstLen; ii++) {
                        dim2Array[ii] = new double[secondLen];
                        for (int jj = 0; jj < secondLen; jj++) {
                            dim2Array[ii][jj] = array[ii][jj];
                        }
                    }
            	}
            } else if (elType.equals(int.class)) {
            	if (obj instanceof int[]) {
                    int[] array = (int[]) obj;
            		dim1Array = new double[array.length];
            		for (int ii = 0; ii < array.length; ii++) {
            			dim1Array[ii] = array[ii];
            		}
            	} else if (obj instanceof int[][]) {
                    int[][] array = (int[][]) obj;
                    dim2Array = new double[firstLen][];
                    for (int ii = 0; ii < firstLen; ii++) {
                        dim2Array[ii] = new double[secondLen];
                        for (int jj = 0; jj < secondLen; jj++) {
                            dim2Array[ii][jj] = array[ii][jj];
                        }
                    }
            	}
            } else if (elType.equals(long.class)) {
            	if (obj instanceof long[]) {
            		long[] array = (long[]) obj;
            		dim1Array = new double[array.length];
            		for (int ii = 0; ii < array.length; ii++) {
            			dim1Array[ii] = array[ii];
            		}

            	} else if (obj instanceof long[][]) {
                    long[][] array = (long[][]) obj;
                    dim2Array = new double[firstLen][];
                    for (int ii = 0; ii < firstLen; ii++) {
                        dim2Array[ii] = new double[secondLen];
                        for (int jj = 0; jj < secondLen; jj++) {
                            dim2Array[ii][jj] = array[ii][jj];
                        }
                    }
            	}
            } else if (elType.equals(short.class)) {
            	if (obj instanceof short[]) {
            		short[] array = (short[]) obj;
            		dim1Array = new double[array.length];
            		for (int ii = 0; ii < array.length; ii++) {
            			dim1Array[ii] = array[ii];
            		}
            	} else if (obj instanceof short[][]) {
                    short[][] array = (short[][]) obj;
                    dim2Array = new double[firstLen][];
                    for (int ii = 0; ii < firstLen; ii++) {
                        dim2Array[ii] = new double[secondLen];
                        for (int jj = 0; jj < secondLen; jj++) {
                            dim2Array[ii][jj] = array[ii][jj];
                        }
                    }
            	}
            } else if (elType.equals(byte.class)) {
            	if (obj instanceof byte[]) {
            		byte[] array = (byte[]) obj;
            		dim1Array = new double[array.length];
            		for (int ii = 0; ii < array.length; ii++) {
            			dim1Array[ii] = array[ii];
            		}
            	} else if (obj instanceof byte[][]) {
                    byte[][] array = (byte[][]) obj;
                    dim2Array = new double[firstLen][];
                    for (int ii = 0; ii < firstLen; ii++) {
                        dim2Array[ii] = new double[secondLen];
                        for (int jj = 0; jj < secondLen; jj++) {
                            dim2Array[ii][jj] = array[ii][jj];
                        }
                    }
            	}
            } else {
                throw new Error("Not supported element type: " + elType);
            }
        }

        double[] ret = null;
        if (dim1Array != null) {
        	ret = dim1Array;
        } else {
            boolean reversed = false;
            if (!cDimList.equals(redDimList)) {
                reversed = true;
            }
        	final int allLen = firstLen * secondLen;
        	ret = new double[allLen];
            final int yLen = dim2Array.length;
            for (int yy = 0; yy < yLen; yy++) {
                final int xLen = dim2Array[yy].length;
                for (int xx = 0; xx < xLen; xx++) {
                    final int index = reversed ? (yy * xLen + xx) : (xx * yLen + yy);
                    ret[index] = dim2Array[yy][xx];
                }
            }
        }

        return ret;
    }

	private void updateVariableNameSet(List<Variable> varList, Dimension dim, Set<String> varNameSet) {
		for (Variable var : varList) {
			String name = var.getShortName();
			if (dim.getName().equals(name)) {
				varNameSet.add(name);
				break;
			}
		}
	}

    /**
     * Returns an array of time values.
     *
     * @return an array of time values
     */
    public double[] getTimeValueArray() {
        if (this.mTimeVariable == null) {
            return null;
        } else {
        	final int len = this.mTimeVariable.getDimension(0).getLength();
        	SGIntegerSeriesSet stride = SGIntegerSeriesSet.createInstance(len);
            return this.getCoordinateValueArray(this.mTimeVariable, stride, false, 
            		false);
        }
    }

    /**
     * Returns whether time variable is available.
     *
     * @return true if available
     */
    public boolean isTimeVariableAvailable() {
        return (this.mTimeVariable != null);
    }

    /**
     * Returns whether animation is available.
     * 
     * @return true if animation is available
     */
    @Override
    public boolean isAnimationAvailable() {
    	return this.isTimeVariableAvailable();
    }

    /**
     * Returns the length of animation.
     * 
     * @return the length of animation
     */
    @Override
    public int getAnimationLength() {
    	int ret = -1;
    	if (this.isTimeVariableAvailable()) {
        	ret = this.mTimeVariable.getDimension(0).getLength();
    	}
    	return ret;
    }

    /**
     * Returns the current index of time value.
     *
     * @return the current index of time value
     */
    @Override
    public int getCurrentTimeValueIndex() {
        if (this.mTimeVariable == null) {
            return -1;
        }
        String key = this.mTimeVariable.getName();
        Integer num = this.mOriginMap.get(key);
        return num;
    }

    /**
     * Returns the time variable.
     *
     * @return the time variable
     */
    public SGNetCDFVariable getTimeVariable() {
        return this.mTimeVariable;
    }

    /**
     * Returns the current time value.
     *
     * @return the current time value
     */
    public Number getCurrentTimeValue() {
        if (this.mTimeVariable == null) {
            return -1;
        }
        final int index = this.getCurrentTimeValueIndex();
        final double[] array = this.getTimeValueArray();
        return SGDataUtility.getCoordinateVariableValue(array, index);
    }

    /**
     * Sets the current index of time value.
     *
     * @param index
     *           a value to set to the index of time values
     */
    @Override
    public void setCurrentTimeValueIndex(final int index) {
        if (this.mTimeVariable == null) {
            return;
        }
        Dimension tDim = this.mTimeVariable.getDimension(0);
        final int len = tDim.getLength();
        if (index < 0 || index >= len) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        String key = this.mTimeVariable.getName();
        this.mOriginMap.put(key, index);

        // clear the cache
        this.clearCache();
    }

    /**
     * Returns the name of given variable if it is not null.
     *
     * @param var
     *            a variable
     * @return the name of given variable or null
     */
    protected String getName(SGNetCDFVariable var) {
    	return (var != null) ? var.getName() : null;
    }

    /**
     * Returns the name of given dimension if it is not null.
     *
     * @param dim
     *            a dimension
     * @return the name of given dimension or null
     */
    protected String getName(Dimension dim) {
    	return (dim != null) ? dim.getName() : null;
    }

    /**
     * Finds and returns a variable of given name.
     *
     * @param name
     *            the name of variable
     * @return a variable of given name
     */
    protected SGNetCDFVariable findVariable(String name) {
    	return (name != null) ? this.getNetcdfFile().findVariable(name) : null;
    }

    protected SGNetCDFVariable[] findVariables(String names[]) {
    	if (names == null || names.length == 0) {
    		return null;
    	} else {
        	SGNetCDFVariable[] vars = new SGNetCDFVariable[names.length];
        	for (int ii = 0; ii < names.length; ii++) {
        		vars[ii] = this.findVariable(names[ii]);
        	}
        	return vars;
    	}
    }

    /**
     * Finds and returns a dimension of given name.
     *
     * @param name
     *            the name of dimension
     * @return a variable of given name
     */
    protected Dimension findDimension(String name) {
    	return (name != null) ? this.getNetcdfFile().findDimension(name) : null;
    }

    // Finds and returns a variable.
    protected SGNetCDFVariable getVariable(SGNetCDFFile ncfile, String name) {
    	SGNetCDFVariable var = null;
        if (name != null) {
        	var = ncfile.findVariable(name);
        	if (var == null) {
                throw new IllegalArgumentException("Invalid variable name: " + name);
        	}
        }
        return var;
    }

    // Checks whether a variable is not a coordinate variable and has given
    // dimensions.
    protected void checkNonCoordinateVariable(SGNetCDFVariable var,
    		List<Dimension> cDimList) {
		if (var != null) {
			if (var.isCoordinateVariable()) {
				throw new IllegalArgumentException("The variable "
						+ var.getName() + " must not be coordinate values.");
			}
			List<Dimension> dims = var.getDimensions();
			for (int ii = 0; ii < cDimList.size(); ii++) {
				Dimension cDim = cDimList.get(ii);
				if (!dims.contains(cDim)) {
					throw new IllegalArgumentException("The variable "
							+ var.getName() + " does not have a dimension "
							+ cDim.getName());
				}
			}
		}
	}

    // Checks whether a variable is not a coordinate variable and has given
    // dimension.
    protected void checkNonCoordinateVariable(SGNetCDFVariable var,
    		Dimension cDim) {
    	List<Dimension> cDimList = new ArrayList<Dimension>();
    	cDimList.add(cDim);
    	this.checkNonCoordinateVariable(var, cDimList);
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
    	map.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, this.getNetcdfFile());
    	return map;
    }

    /**
     * Returns a map of stride of dimensions. The keys are the name of dimensions.
     *
     * @return a map of stride of dimensions
     */
    /**
     * Returns a map of stride of dimensions. The keys are the name of dimensions.
     *
     * @return a map of stride of dimensions
     */
    protected abstract Map<String, SGIntegerSeriesSet> getDimensionStrideMap();

    /**
     * Returns the variable of index.
     *
     * @return the variable of index
     */
    public SGNetCDFVariable getIndexVariable() {
    	return this.mIndexVariable;
    }

    /**
     * Returns whether the index is available.
     *
     * @return true if the index is available
     */
    @Override
    public boolean isIndexAvailable() {
    	return (this.mIndexVariable != null);
    }

    /**
     * Sets the data source.
     *
     * @param src
     *           a data source
     */
	@Override
    public void setDataSource(SGIDataSource src) {
    	if (!(src instanceof SGNetCDFFile)) {
    		throw new IllegalArgumentException("Invalid data source.");
    	}
    	super.setDataSource(src);
    	SGNetCDFFile ncfile = (SGNetCDFFile) src;
    	this.mIndexVariable = this.findVariable(ncfile, this.mIndexVariable);
    	this.mTimeVariable = this.findVariable(ncfile, this.mTimeVariable);
    }

    protected static SGNetCDFVariable[] copyVariables(SGNetCDFVariable[] vars) {
    	if (vars != null) {
    		return (SGNetCDFVariable[]) vars.clone();
    	} else {
    		return null;
    	}
    }

    protected static String getNetCDFStride(SGIntegerSeries series) {
    	int start = series.getStart().getNumber();
    	int end = series.getEnd().getNumber();
    	int step = series.getStep().getNumber();
    	if (end < start) {
    		final int temp = start;
    		start = end;
    		end = temp;
    		step *= -1;
    	}
    	StringBuffer sb = new StringBuffer();
    	sb.append(start);
    	if (start != end) {
			sb.append(':');
			sb.append(end);
			if (step != 1 && step != -1) {
				sb.append(':');
				sb.append(step);
			}
    	}
    	return sb.toString();
    }

    protected double[] createAllValueArray(SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, Integer> originMap) {
		List<SGNamedDoubleValueIndexBlock> blockList = this.createAllValueBlockList(var, cVars, originMap);
		double[] allValueArray = this.arrangeValueArray(var, blockList);
        return allValueArray;
    }

    protected String[] createStringArray(SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, Integer> originMap) {
		List<SGNamedStringBlock> blockList = this.createStringBlockList(var, cVars, originMap);
		if (blockList == null) {
			return null;
		}
		String[] allValueArray = this.arrangeStringArray(blockList);
        return allValueArray;
    }

    protected double[] createValueArray(SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, Integer> originMap, Map<String, SGIntegerSeriesSet> dimensionStrideMap) {
		List<SGNamedDoubleValueIndexBlock> blockList = this.createBlockList(var, cVars, originMap, dimensionStrideMap);
		double[] allValueArray = this.arrangeValueArray(var, blockList);
        return allValueArray;
    }

    protected String[] createStringArray(SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, Integer> originMap, Map<String, SGIntegerSeriesSet> dimensionStrideMap) {
		List<SGNamedStringBlock> blockList = this.createStringBlockList(var, cVars, originMap, dimensionStrideMap);
		String[] allValueArray = this.arrangeStringArray(blockList);
        return allValueArray;
    }

    protected List<SGNamedDoubleValueIndexBlock> createBlockList(SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, Integer> originMap, Map<String, SGIntegerSeriesSet> dimensionStrideMap) {

        // get dimensions
        List<Dimension> dimList = new ArrayList<Dimension>(var.getDimensions());

        // creates the list of series maps
        List<Map<String, SGIntegerSeries>> seriesMapList = this.createSeriesMapList(var, cVars,
        		originMap, dimensionStrideMap);

		// loop for all combinations of series
		List<SGNamedDoubleValueIndexBlock> blockList = new ArrayList<SGNamedDoubleValueIndexBlock>();
		for (Map<String, SGIntegerSeries> map : seriesMapList) {
			// creates a text string specifying the section
			StringBuffer sb = new StringBuffer();
			for (int ii = 0; ii < dimList.size(); ii++) {
				if (ii > 0) {
					sb.append(',');
				}
				Dimension dim = dimList.get(ii);
				String dimName = dim.getName();
				SGIntegerSeries series = map.get(dimName);
				String str = getNetCDFStride(series);
				sb.append(str);
			}
	        Array data = null;
	        try {
	            data = var.read(sb.toString());
	        } catch (InvalidRangeException e) {
	            return null;
	        } catch (IOException e) {
	            return null;
	        }
	        Array reducedData = data.reduce();
	        double[] valueArray = this.arrangeValueArray(reducedData, var, cVars, map);
	        SGNamedDoubleValueIndexBlock block = new SGNamedDoubleValueIndexBlock(valueArray, map);
	        blockList.add(block);
		}

		return blockList;
    }

    protected List<SGNamedStringBlock> createStringBlockList(SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, Integer> originMap, Map<String, SGIntegerSeriesSet> dimensionStrideMap) {

        // get dimensions
        List<Dimension> dimList = new ArrayList<Dimension>(var.getDimensions());
        SGNetCDFTextVariable textVar = (SGNetCDFTextVariable) var;
        Dimension lenDim = textVar.getLengthDimension();
        dimList.remove(lenDim);		// removes length dimension

        // creates the list of series maps
        List<Map<String, SGIntegerSeries>> seriesMapList = this.createSeriesMapList(var, cVars,
        		originMap, dimensionStrideMap);

		// loop for all combinations of series
		List<SGNamedStringBlock> blockList = new ArrayList<SGNamedStringBlock>();
		for (Map<String, SGIntegerSeries> map : seriesMapList) {

			// creates a text string specifying the section
			StringBuffer sb = new StringBuffer();
			for (int ii = 0; ii < dimList.size(); ii++) {
				Dimension dim = dimList.get(ii);
				String dimName = dim.getName();
				SGIntegerSeries series = map.get(dimName);
				String str = getNetCDFStride(series);
				sb.append(str);
				sb.append(',');
			}
			// append for the length dimension
			sb.append("0:");
			sb.append(lenDim.getLength() - 1);
			sb.append(":1");
			String spec = sb.toString();

			Array data = null;
	        try {
	            data = var.read(spec);
	        } catch (InvalidRangeException e) {
	            return null;
	        } catch (IOException e) {
	            return null;
	        }
	        ArrayChar reducedData = (ArrayChar) data.reduce();
	        String[] valueArray = this.arrangeStringArray(reducedData, var, cVars, map);
	        SGNamedStringBlock block = new SGNamedStringBlock(valueArray, map);
	        blockList.add(block);
		}

		return blockList;
    }

    private List<Map<String, SGIntegerSeries>> createSeriesMapList(SGNetCDFVariable var,
    		SGNetCDFVariable[] cVars, Map<String, Integer> originMap,
    		Map<String, SGIntegerSeriesSet> dimensionStrideMap) {

        // get dimensions
        List<Dimension> dimList = var.getDimensions();

    	// creates a list of indices of coordinate variables
        List<Integer> cIndices = this.getCoordinateVariableIndices(var, cVars);

        // creates a list of series lists for each dimension
        List<List<SGIntegerSeries>> seriesListList = new ArrayList<List<SGIntegerSeries>>();
		for (int ii = 0; ii < dimList.size(); ii++) {
			Dimension dim = dimList.get(ii);
			String dimName = dim.getName();
            List<SGIntegerSeries> seriesList = new ArrayList<SGIntegerSeries>();
			if (cIndices.contains(ii)) {
				SGIntegerSeriesSet stride = dimensionStrideMap.get(dimName);
				List<SGIntegerSeries> sList = stride.getSeriesList();
		        if (sList.size() == 1) {
		        	// add a series
		        	SGIntegerSeries series = sList.get(0);
	                seriesList.add(series);
		        } else {
		        	// add all indices as an array of strides
		        	int[] indices = stride.getNumbers();
		        	seriesList.addAll(SGIntegerSeries.createList(indices));
		        }
			} else {
				// add single index
                Integer value = originMap.get(dimName);
                SGIntegerSeries series = new SGIntegerSeries(value);
                seriesList.add(series);
			}
            seriesListList.add(seriesList);
		}

        // creates a list of all combinations of series for dimensions
		List<Map<String, SGIntegerSeries>> seriesMapList = new ArrayList<Map<String, SGIntegerSeries>>();
		for (int ii = 0; ii < dimList.size(); ii++) {
			Dimension dim = dimList.get(ii);
			String dimName = dim.getName();
			List<SGIntegerSeries> seriesList = seriesListList.get(ii);
			if (ii == 0) {
				for (SGIntegerSeries series : seriesList) {
					Map<String, SGIntegerSeries> seriesMap = new HashMap<String, SGIntegerSeries>();
					seriesMap.put(dimName, series);
					seriesMapList.add(seriesMap);
				}
			} else {
				List<Map<String, SGIntegerSeries>> seriesMapListNew = new ArrayList<Map<String, SGIntegerSeries>>();
				for (Map<String, SGIntegerSeries> seriesMap : seriesMapList) {
					for (SGIntegerSeries series : seriesList) {
						Map<String, SGIntegerSeries> seriesMapNew = new HashMap<String, SGIntegerSeries>(seriesMap);
						seriesMapNew.put(dimName, series);
						seriesMapListNew.add(seriesMapNew);
					}
				}
				seriesMapList = seriesMapListNew;
			}
		}

		return seriesMapList;
    }

    protected List<SGNamedDoubleValueIndexBlock> createAllValueBlockList(SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, Integer> originMap) {

        // get dimensions
        List<Dimension> dimList = new ArrayList<Dimension>(var.getDimensions());

    	Map<String, SGIntegerSeriesSet> dimensionStrideMap = new HashMap<String, SGIntegerSeriesSet>();
    	for (Dimension dim : dimList) {
    		String name = dim.getName();
    		final int len = dim.getLength();
    		SGIntegerSeriesSet all = SGIntegerSeriesSet.createInstance(len);
    		dimensionStrideMap.put(name, all);
    	}

		return this.createBlockList(var, cVars, originMap, dimensionStrideMap);
    }

    protected List<SGNamedStringBlock> createStringBlockList(SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, Integer> originMap) {

        // get dimensions
        List<Dimension> dimList = new ArrayList<Dimension>(var.getDimensions());

    	Map<String, SGIntegerSeriesSet> dimensionStrideMap = new HashMap<String, SGIntegerSeriesSet>();
    	for (Dimension dim : dimList) {
    		String name = dim.getName();
    		final int len = dim.getLength();
    		SGIntegerSeriesSet all = SGIntegerSeriesSet.createInstance(len);
    		dimensionStrideMap.put(name, all);
    	}

		return this.createStringBlockList(var, cVars, originMap, dimensionStrideMap);
    }

    protected abstract double[] arrangeValueArray(SGNetCDFVariable var, List<SGNamedDoubleValueIndexBlock> blockList);

    protected abstract String[] arrangeStringArray(List<SGNamedStringBlock> blockList);

    protected double[] createCoordinateValueArray(SGNetCDFVariable var,
    		SGIntegerSeriesSet stride, final boolean removeInvalidValues) {
        List<SGIntegerSeries> seriesList = stride.getSeriesList();
        List<double[]> arrays = new ArrayList<double[]>();
        if (var instanceof SGCoordinateVariable) {
            for (SGIntegerSeries series : seriesList) {
                int[] indexArray = series.getNumbers();
            	double[] dArray = new double[indexArray.length];
                for (int ii = 0; ii < dArray.length; ii++) {
                	dArray[ii] = indexArray[ii];
                }
                arrays.add(dArray);
            }
        } else {
            for (SGIntegerSeries series : seriesList) {
        		StringBuffer sb = new StringBuffer();
				String str = getNetCDFStride(series);
				sb.append(str);
                Array array = null;
                try {
                	array = var.read(sb.toString());
                } catch (InvalidRangeException e) {
                    return null;
                } catch (IOException e) {
                    return null;
                }
                final int size = (int) array.getSize();
            	double[] dArray = new double[size];
                for (int ii = 0; ii < size; ii++) {
                    dArray[ii] = array.getDouble(ii);
                }
                arrays.add(dArray);
            }
        }
        double[] valueArray = this.getArray(arrays);
		final double[] dArray = removeInvalidValues ? this.removeFillValue(var,
				valueArray) : valueArray;
        return dArray;
    }

    private double[] getArray(List<double[]> arrays) {
        if (arrays.size() == 1) {
        	return arrays.get(0);
        } else {
        	List<Double> allValues = new ArrayList<Double>();
        	for (double[] array : arrays) {
        		for (int ii = 0; ii < array.length; ii++) {
        			allValues.add(array[ii]);
        		}
        	}
        	double[] allArray = new double[allValues.size()];
        	for (int ii = 0; ii < allArray.length; ii++) {
        		allArray[ii] = allValues.get(ii);
        	}
        	Arrays.sort(allArray);
        	return allArray;
        }
    }

    protected SGIntegerSeriesSet createStride(SGIntegerSeriesSet stride, Dimension dim) {
        if (stride != null) {
            return (SGIntegerSeriesSet) stride.clone();
        } else {
        	final int len = dim.getLength();
        	return SGIntegerSeriesSet.createInstance(len);
        }
    }

    protected double[] arrangeValueArray1Dim(List<SGNamedDoubleValueIndexBlock> blockList,
    		SGNetCDFVariable indexVar, SGIntegerSeriesSet indexStride) {
		List<Double> valueList = new ArrayList<Double>();
		for (SGNamedDoubleValueIndexBlock block : blockList) {
			double[] values = block.getValues();
			for (int ii = 0; ii < values.length; ii++) {
				valueList.add(values[ii]);
			}
		}
		double[] values = new double[valueList.size()];
		for (int ii = 0; ii < values.length; ii++) {
			values[ii] = valueList.get(ii);
		}
    	return values;
    }

    protected double[] arrangeValueArray2Dim(List<SGNamedDoubleValueIndexBlock> blockList,
    		SGNetCDFVariable xVar, SGNetCDFVariable yVar, SGIntegerSeriesSet xStride,
    		SGIntegerSeriesSet yStride) {
		Dimension xDim = xVar.getDimension(0);
		Dimension yDim = yVar.getDimension(0);
		final String xName = xDim.getName();
		final String yName = yDim.getName();
		List<Double> valueList = new ArrayList<Double>();
		for (SGNamedDoubleValueIndexBlock block : blockList) {
			SGIntegerSeries blockSeriesX = block.getSeries(xName);
			SGIntegerSeries blockSeriesY = block.getSeries(yName);
			int[] blockIndicesX = blockSeriesX.getNumbers();
			int[] blockIndicesY = blockSeriesY.getNumbers();
			final int xLen = blockIndicesX.length;
			final int yLen = blockIndicesY.length;
			for (int by = 0; by < yLen; by++) {
				final int offset = by * xLen;
				for (int bx = 0; bx < xLen; bx++) {
					final int index = offset + bx;
					valueList.add(block.getValue(index));
				}
			}
		}
		double[] values = new double[valueList.size()];
		for (int ii = 0; ii < values.length; ii++) {
			values[ii] = valueList.get(ii);
		}
    	return values;
    }

    protected void setIndexStrideSub(SGIntegerSeriesSet stride) {
    	if (!SGUtility.equals(this.mIndexStride, stride)) {
    		this.clearCache();
    	}
    	if (stride == null) {
    		this.mIndexStride = null;
    	} else {
    		this.mIndexStride = (SGIntegerSeriesSet) stride.clone();
    	}
    }

    /**
     * Returns the stride for index.
     *
     * @return the stride for index
     */
    @Override
    public SGIntegerSeriesSet getIndexStride() {
    	if (this.mIndexStride != null) {
    		return (SGIntegerSeriesSet) this.mIndexStride.clone();
    	} else {
    		return null;
    	}
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

    protected SGIntegerSeriesSet getIndexStrideInstance() {
    	if (this.isIndexAvailable()) {
        	return this.getStrideInstance(this.mIndexStride, this.mIndexVariable);
    	} else {
    		return null;
    	}
    }

    protected SGIntegerSeriesSet getStrideInstance(SGIntegerSeriesSet stride, SGNetCDFVariable var) {
    	if (!this.isStrideAvailable()) {
    		return this.createStrideInstance(var);
    	} else {
    		if (stride != null) {
            	return (SGIntegerSeriesSet) stride.clone();
    		} else {
        		return this.createStrideInstance(var);
    		}
    	}
    }

    protected SGIntegerSeriesSet getTimeStrideInstance() {
    	if (this.isTimeVariableAvailable()) {
    		if (this.mTimeStride != null) {
            	return (SGIntegerSeriesSet) this.mTimeStride.clone();
    		} else {
        		return this.createStrideInstance(this.mTimeVariable);
    		}
    	} else {
    		return null;
    	}
    }

    private SGIntegerSeriesSet createStrideInstance(SGNetCDFVariable var) {
    	final int len = var.getDimension(0).getLength();
		return SGIntegerSeriesSet.createInstance(len);
    }

    /**
     * Sets the stride for index.
     *
     * @param stride
     *           stride of arrays
     */
    @Override
	public void setIndexStride(SGIntegerSeriesSet stride) {
		this.setIndexStrideSub(stride);
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
    		SGNetCDFVariable var, SGNetCDFVariable xVar, SGNetCDFVariable yVar,
    		final boolean all, final boolean removeInvalidValues) {
    	if (this.isIndexAvailable()) {
    		// always returns null
    		return null;
    	} else {
    		SGNetCDFVariable[] cVars = new SGNetCDFVariable[] { xVar, yVar };
    		List<SGNamedDoubleValueIndexBlock> blockList;
			if (all) {
				blockList = this.createAllValueBlockList(var, cVars,
						this.mOriginMap);
			} else {
				blockList = this.createBlockList(var, cVars, this.mOriginMap,
						this.getDimensionStrideMap());
			}
    		List<SGXYSimpleDoubleValueIndexBlock> sBlockList = new ArrayList<SGXYSimpleDoubleValueIndexBlock>();
    		for (SGNamedDoubleValueIndexBlock block : blockList) {
    			SGIntegerSeries xSeries = block.getSeries(xVar.getName());
    			SGIntegerSeries ySeries = block.getSeries(yVar.getName());
    			double[] values = block.getValues();
    			if (removeInvalidValues) {
        			values = this.removeFillValue(var, values);
    			}
    			SGXYSimpleDoubleValueIndexBlock sBlock = new SGXYSimpleDoubleValueIndexBlock(values, xSeries, ySeries);
    			sBlockList.add(sBlock);
    		}
    		return sBlockList;
    	}
	}

    /**
     * Returns an array of data column information.
     *
     * @return an array of data column information
     */
    @Override
    public SGDataColumnInfo[] getColumnInfo() {
        SGNetCDFFile ncFile = this.getNetcdfFile();
        List<SGNetCDFVariable> varList = ncFile.getVariables();
        final int varNum = varList.size();
        SGNetCDFDataColumnInfo[] colArray = new SGNetCDFDataColumnInfo[varNum];
        String[] colTypes = this.getCurrentColumnType();
        for (int ii = 0; ii < varNum; ii++) {
        	SGNetCDFVariable var = varList.get(ii);
            final String name = var.getName();
            final int origin;
            if (var.isCoordinateVariable()) {
                origin = this.getOrigin(name);
            } else {
                origin = 0;
            }
            colArray[ii] = new SGNetCDFDataColumnInfo(var, var.getName(),
            		var.getValueType(), origin);
            colArray[ii].setColumnType(colTypes[ii]);
        }
    	return colArray;
    }

	protected String transformByteString(String str) throws UnsupportedEncodingException {
		char[] cArray = str.toCharArray();
		byte[] bArray = new byte[cArray.length];
		for (int ii = 0; ii < bArray.length; ii++) {
			bArray[ii] = (byte) cArray[ii];
		}
		return SGUtility.createString(bArray);
	}

    /**
     * Returns a text string of the data type to save into a NetCDF data set file.
     *
     * @return a text string of the data type to save into a NetCDF data set file
     */
    @Override
    public String getNetCDFDataSetDataType() {
    	return this.getDataType();
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
    	
    	// gets dimensions
    	SGNetCDFVariable[] curVars = this.getAssignedVariables();
    	Set<Dimension> curDimSet = new HashSet<Dimension>();
    	Map<String, Dimension> curLenDimMap = new HashMap<String, Dimension>(); 
    	for (SGNetCDFVariable ncVar : curVars) {
    		if (ncVar.isCoordinateVariable()) {
    			curDimSet.add(ncVar.getDimension(0));
    		} else {
    			Variable var = ncVar.getVariable();
    			DataType dataType = var.getDataType();
    			List<Dimension> dims = var.getDimensions();
    			if (DataType.CHAR.equals(dataType) && dims.size() == 2) {
    				// text or date variable
    				Dimension lenDim = dims.get(1);
    				curLenDimMap.put(ncVar.getName(), lenDim);
    			}
    		}
    	}
    	List<Dimension> curDimList = new ArrayList<Dimension>(curDimSet);
		List<Dimension> allCurDimList = new ArrayList<Dimension>();
		allCurDimList.addAll(curDimList);
		allCurDimList.addAll(curLenDimMap.values());

		NetcdfFileWriteable ncWrite = null;
		try {
			ncWrite = NetcdfFileWriteable.createNew(file.getAbsolutePath());

			// adds dimensions
			Map<String, Dimension> addedDimMap = new HashMap<String, Dimension>();
			Map<String, String> addedVarDimCurDimNameMap = new HashMap<String, String>();
			for (Dimension curDim : allCurDimList) {
				final int len = curDim.getLength();
				String curDimName = curDim.getName();
				String validDimName = this.getValidName(curDimName);
				Dimension dim = ncWrite.addDimension(validDimName, len, 
						curDim.isShared(), curDim.isUnlimited(), curDim.isVariableLength());
				addedDimMap.put(curDimName, dim);
				addedVarDimCurDimNameMap.put(validDimName, curDimName);
			}
			
			// adds variables other than coordinate variables
			Map<String, Variable> addedVarMap = new HashMap<String, Variable>();
	    	for (SGNetCDFVariable curVar : curVars) {
	    		if (curVar.isCoordinateVariable()) {
	    			continue;
	    		}
	    		String curVarName = curVar.getName();
	    		List<Dimension> curVarDimList = curVar.getDimensions();
	    		List<Dimension> addedDimList = new ArrayList<Dimension>();
	    		for (Dimension curVarDim : curVarDimList) {
	    			if (curDimList.contains(curVarDim)) {
	    				Dimension addedDim = addedDimMap.get(curVarDim.getName());
	    				addedDimList.add(addedDim);
	    			}
	    		}
	    		Dimension curLenDim = curLenDimMap.get(curVarName);
	    		if (curLenDim != null) {
    				Dimension addedDim = addedDimMap.get(curLenDim.getName());
    				addedDimList.add(addedDim);
	    		}
	    		Dimension[] addedDimArray = addedDimList.toArray(new Dimension[addedDimList.size()]);
	    		String validVarName = this.getValidName(curVarName);
	    		Variable var = this.addVariable(ncWrite, validVarName, curVar.getDataType(), 
	    				curVar.getAttributes(), addedDimArray);
	    		addedVarMap.put(curVarName, var);
	    	}
	    	
	    	// adds coordinate variables
	    	for (Dimension curDim : curDimList) {
	    		String curDimName = curDim.getName();
	    		SGNetCDFVariable cVar = this.findVariable(curDimName);
	    		if (cVar == null) {
	    			continue;
	    		}
	    		Dimension addedDim = addedDimMap.get(curDimName);
	    		Variable var = this.addVariable(ncWrite, addedDim.getName(), cVar.getDataType(), 
	    				cVar.getAttributes(), new Dimension[] { addedDim });
	    		addedVarMap.put(curDimName, var);
	    	}
			
			ncWrite.create();
			
			// writes values
			Iterator<Entry<String, Variable>> addedVarItr = addedVarMap.entrySet().iterator();
			while (addedVarItr.hasNext()) {
				Entry<String, Variable> addedVarEntry = addedVarItr.next();
				String curVarName = addedVarEntry.getKey();
				Variable addedVar = addedVarEntry.getValue();
				String addedVarName = addedVar.getShortName();
				List<Dimension> addedVarDimList = addedVar.getDimensions();
				List<String> addedVarDimCurDimNameList = new ArrayList<String>();
				for (Dimension addedVarDim : addedVarDimList) {
					String addedVarDimName = addedVarDim.getName();
					String addedVarDimCurDimName = addedVarDimCurDimNameMap.get(addedVarDimName);
					addedVarDimCurDimNameList.add(addedVarDimCurDimName);
				}
				Variable curVar = this.findVariable(curVarName).getVariable();
				List<Dimension> curVarDimList = curVar.getDimensions();
				List<Integer> reducedDimList = new ArrayList<Integer>();
				StringBuffer sb = new StringBuffer();
				for (int ii = 0; ii < curVarDimList.size(); ii++) {
					if (ii > 0) {
						sb.append(',');
					}
					Dimension curVarDim = curVarDimList.get(ii);
					String curVarDimName = curVarDim.getName();
					if (addedVarDimCurDimNameList.contains(curVarDimName)) {
						SGIntegerSeries series = SGIntegerSeries.createInstance(curVarDim.getLength());
						String str = getNetCDFStride(series);
						sb.append(str);
					} else {
						Integer origin = this.mOriginMap.get(curVarDimName);
						sb.append(origin);
						reducedDimList.add(ii);
					}
				}
				Array array = curVar.read(sb.toString());
				Array reducedArray = array;
				Integer[] sortedReducedDims = reducedDimList.toArray(new Integer[reducedDimList.size()]);
				Arrays.sort(sortedReducedDims, new Comparator<Integer>() {
					// sorts in descending order
					@Override
					public int compare(Integer o1, Integer o2) {
						if (o1 < o2) {
							return 1;
						} else if (o1.equals(o2)) {
							return 0;
						} else {
							return -1;
						}
					}
				});
				for (Integer reducedDim : sortedReducedDims) {
					reducedArray = reducedArray.reduce(reducedDim);
				}
				
				// set edited values
				reducedArray = this.setEditedValues(ncWrite, addedVarName, 
						reducedArray, true);
				
				ncWrite.write(addedVarName, reducedArray);
			}
			
		} catch (IOException e) {
			return false;
		} catch (InvalidRangeException e) {
			return false;
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
    
    protected abstract Array setEditedValues(NetcdfFileWriteable ncWrite,
    		String varName, Array array, final boolean all);
    
    private Variable addVariable(NetcdfFileWriteable ncWrite, String varName, 
    		DataType dataType, List<Attribute> attrList, Dimension[] dims) {
		Variable var = ncWrite.addVariable(varName, dataType, dims);
		for (Attribute attr : attrList) {
			var.addAttribute(new Attribute(attr.getName(), attr.getValues()));
		}
		return var;
    }
    
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
    	return this.saveToNetCDFFile(file, mode, policy);
    }

    protected abstract boolean exportToFile(NetcdfFileWriteable ncWrite, 
    		final SGExportParameter mode, SGDataBufferPolicy policy) 
    		throws IOException, InvalidRangeException;

	protected Dimension addDimension(NetcdfFileWriteable ncWrite, String name,
			final int len) {
		Dimension dim = new Dimension(name, len);
		ncWrite.addDimension(null, dim);
		return dim;
	}
	
	protected Attribute addAttribute(Variable var, String name, String value) {
		Attribute attr = new Attribute(name, value);
		var.addAttribute(attr);
		return attr;
	}

	protected Attribute addAttribute(Variable var, String name, Number value) {
		Attribute attr = new Attribute(name, value);
		var.addAttribute(attr);
		return attr;
	}

	protected Attribute addAttribute(Variable var, String name, Array array) {
		Attribute attr = new Attribute(name, array);
		var.addAttribute(attr);
		return attr;
	}
	
	protected Variable addVariable(NetcdfFileWriteable ncWrite, SGExportParameter mode, String name, 
			DataType dataType, String valueType, String dimString, SGDataBufferPolicy policy) {
		return this.addVariable(ncWrite, mode, name, name, dataType, valueType, dimString, policy);
	}
	
	protected Variable addVariable(NetcdfFileWriteable ncWrite, SGExportParameter mode, String oldName, 
			String name, DataType dataType, String valueType, String dimString, SGDataBufferPolicy policy) {
		
		SGNetCDFVariable curVar = this.getVariable(this.getNetcdfFile(), oldName);
		Variable var = new Variable(ncWrite, null, null, name, dataType, dimString);
		
		// value type
		if (valueType != null) {
			this.addAttribute(var, ATTRIBUTE_VALUE_TYPE, valueType);
		}
		
		// long name and standard name
		String longName = curVar.getLongName();
		if (longName != null && !"".equals(longName)) {
			this.addAttribute(var, ATTR_LONG_NAME, longName);
		}
		String standardName = curVar.getStandardName();
		if (standardName != null && !"".equals(standardName)) {
			this.addAttribute(var, ATTR_STANDARD_NAME, standardName);
		}
		
		// units string
		String unitsString = curVar.getUnitsString();
		if (unitsString != null && !"".equals(unitsString)) {
			this.addAttribute(var, ATTR_UNITS, unitsString);
		}
		
		// fill value and values for valid range
		this.addFillValueAttributes(curVar, var, policy);
		
		ncWrite.addVariable(null, var);
		return var;
	}
	
	protected void addFillValueAttributes(SGNetCDFVariable curVar, Variable var, SGDataBufferPolicy policy) {
		Number fillValue = curVar.getFillValue();
		if (fillValue != null) {
			this.addAttribute(var, ATTR_FILL_VALUE, fillValue);
		}
		Number missingValue = curVar.getMissingValue();
		if (missingValue != null) {
			this.addAttribute(var, ATTR_MISSING_VALUE, missingValue);
		}
		double[] validRange = curVar.getValidRange();
		if (validRange != null) {
			Array range = Array.factory(DataType.DOUBLE, new int[] { 2 });
			range.setDouble(0, validRange[0]);
			range.setDouble(1, validRange[1]);
			this.addAttribute(var, ATTR_VALID_RANGE, range);
		}
		Number validMin = curVar.getValidMin();
		if (validMin != null) {
			this.addAttribute(var, ATTR_VALID_MIN, validMin);
		}
		Number validMax = curVar.getValidMax();
		if (validMax != null) {
			this.addAttribute(var, ATTR_VALID_MAX, validMax);
		}
	}
	
    /**
     * Returns a text string of file extension for the file in a data set file.
     * 
     * @return a text string of file extension
     */
    @Override
    public String getDataSetFileExtension() {
    	return SGIDataFileConstants.NETCDF_FILE_EXTENSION;
    }

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
    	
		NetcdfFileWriteable ncWrite = null;
		try {
			ncWrite = NetcdfFileWriteable.createNew(file.getAbsolutePath());
			if (!this.exportToFile(ncWrite, mode, policy)) {
				return false;
			}
		} catch (IOException e) {
			return false;
		} catch (InvalidRangeException e) {
			return false;
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
    
    public boolean saveToDataSetNetCDFFile(final File file) {
    	
		SGNetCDFVariable[] vArray = this.getAssignedVariables();

		NetcdfFile ncRead = this.getNetcdfFile().getNetcdfFile();
		NetcdfFileWriteable ncWrite = null;
		try {
			ncWrite = NetcdfFileWriteable.createNew(file.getAbsolutePath());

			List<Attribute> globalAttrList = ncRead.getGlobalAttributes();
			for (Attribute gattr : globalAttrList) {
				ncWrite.addGlobalAttribute(gattr);
			}

			// maps for text variable
			Map<Variable, SGIStringModifier> modMap = new HashMap<Variable, SGIStringModifier>();
			Map<Variable, List<String>> strListMap = new HashMap<Variable, List<String>>();
			Map<Variable, Dimension> strLengthDimMap = new HashMap<Variable, Dimension>();

			final List<Variable> varList = ncRead.getVariables();
			Set<Dimension> dimSet = new HashSet<Dimension>();
			Set<String> varNameSet = new HashSet<String>();
			for (SGNetCDFVariable ncVar : vArray) {
				if (ncVar instanceof SGTextVariable) {
					SGTextVariable textVar = (SGTextVariable) ncVar;
					Variable var = null;
					for (Variable v : varList) {
						String varName = v.getShortName();
						if (varName.equals(textVar.getName())) {
							var = v;
							break;
						}
					}
					if (var == null) {
						return false;
					}
					List<Dimension> dims = var.getDimensions();
					Dimension strLengthDim = textVar.getLengthDimension();

					// get modifier
					SGIStringModifier mod = textVar.getModifier();
					if (mod != null) {
						// modifies text strings
						ArrayChar ac = (ArrayChar) var.read();
						Index idx = ac.getIndex();
						int[] shape = idx.getShape();
						List<String> strList = new ArrayList<String>();
						int maxLength = 0;
						for (int ii = 0; ii < shape[0]; ii++) {
							StringBuffer sb = new StringBuffer();
							for (int jj = 0; jj < shape[1]; jj++) {
								final char c = ac.getChar(idx.set(ii, jj));
								sb.append(c);
							}
							String str = mod.modify(sb.toString()).trim();
							strList.add(str);
							final int len = str.getBytes().length;
							if (len > maxLength) {
								maxLength = len;
							}
						}
						Dimension dim = new Dimension(strLengthDim.getName(),
								maxLength);
						dimSet.add(dim);

						modMap.put(var, mod);
						strListMap.put(var, strList);
						strLengthDimMap.put(var, dim);
					} else {
						dimSet.add(strLengthDim);
					}

					varNameSet.add(var.getShortName());
					for (Dimension dim : dims) {
						if (!dim.equals(strLengthDim)) {
							dimSet.add(dim);
						}
						this.updateVariableNameSet(varList, dim, varNameSet);
					}

				} else if (ncVar instanceof SGCoordinateVariable) {
					SGCoordinateVariable coordVar = (SGCoordinateVariable) ncVar;
					Variable var = ncVar.getVariable();
					if (var != null) {
						varNameSet.add(var.getShortName());
						List<Dimension> dims = var.getDimensions();
						for (Dimension dim : dims) {
							dimSet.add(dim);
							this.updateVariableNameSet(varList, dim, varNameSet);
						}
					}
					Dimension dim = coordVar.getDimension();
					if (dim != null) {
						dimSet.add(dim);
						this.updateVariableNameSet(varList, dim, varNameSet);
					}
				} else {
					// SGVariable, SGDateVariable
					Variable var = ncVar.getVariable();
					varNameSet.add(var.getShortName());
					List<Dimension> dims = var.getDimensions();
					for (Dimension dim : dims) {
						dimSet.add(dim);
						this.updateVariableNameSet(varList, dim, varNameSet);
					}
				}
			}

			Set<Variable> modVarSet = modMap.keySet();

			for (Dimension dim : dimSet) {
				Dimension d = new Dimension(dim.getName(), dim.getLength());
				ncWrite.addDimension(null, d);
			}

			for (Variable var : varList) {
				String varName = var.getShortName();
				if (varNameSet.contains(varName)) {
					List<Attribute> attrList = var.getAttributes();
					String[] groupNames = varName.split("/");
					StringBuffer sb = new StringBuffer();
					for (int ii = 0; ii < groupNames.length - 1; ii++) {
						sb.append(groupNames[ii]);
						if (ii != groupNames.length - 2) {
							sb.append("/");
						}
					}
					String groupName = sb.toString();
					if (groupName.length() == 0) {
						Variable v = new Variable(ncWrite, null, null,
								var.getShortName(), var.getDataType(),
								var.getDimensionsString());
						for (Attribute attr : attrList) {
							v.addAttribute(attr);
						}
						ncWrite.addVariable(null, v);
					} else {
						Group group = ncWrite.findGroup(groupName);
						if (group == null) {
							Group g = ncWrite.getRootGroup();
							for (int ii = 0; ii < groupNames.length - 1; ii++) {
								Group g2 = g.findGroup(groupNames[ii]);
								if (g2 == null) {
									g2 = new Group(ncWrite, g, groupNames[ii]);
									g.addGroup(g2);
								}
								g = g2;
							}
						}
						group = ncWrite.findGroup(groupName);
						String dimString = var.getDimensionsString();
						Variable v = new Variable(ncWrite, null, null,
								groupNames[groupNames.length - 1],
								var.getDataType(), dimString);
						for (Attribute attr : attrList) {
							v.addAttribute(attr);
						}
						ncWrite.addVariable(group, v);
					}
				}
			}

			ncWrite.create();

			for (Variable var : varList) {
				Array array = var.read();
				if (modVarSet.contains(var)) {
					// create new array
					List<String> strList = strListMap.get(var);
					Dimension strLengthDim = strLengthDimMap.get(var);
					ArrayChar acNew = new ArrayChar.D2(strList.size(),
							strLengthDim.getLength());
					Index idxNew = acNew.getIndex();
					int[] shape = idxNew.getShape();
					for (int ii = 0; ii < shape[0]; ii++) {
						String str = strList.get(ii);
						char[] cArray = str.toCharArray();
						for (int jj = 0; jj < cArray.length; jj++) {
							acNew.setChar(idxNew.set(ii, jj), cArray[jj]);
						}
					}
					array = acNew;
				}
				String varName = var.getShortName();
				if (varNameSet.contains(varName)) {
					ncWrite.write(varName, array);
				}
			}

		} catch (IOException e) {
			return false;
		} catch (InvalidRangeException e) {
			return false;
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

    /**
     * Exports the data into a HDF5 file.
     * 
     * @param path
     *           the path of HDF5 file
     * @return true if succeeded
     */
    @Override
    public boolean saveToHDF5File(final File file, final SGExportParameter mode,
    		SGDataBufferPolicy policy) {
    	// TODO
    	return false;
    }

    /**
     * Exports the data into a MATLAB file.
     * 
     * @param path
     *           the path of MATLAB file
     * @return true if succeeded
     */
	@Override
	public boolean saveToMATLABFile(File file, final SGExportParameter mode,
			SGDataBufferPolicy policy) {
		// TODO
		return false;
	}

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

    /**
     * Returns the length of index-dimension without taking into account the stride.
     * 
     * @return the length of index-dimension without taking into account the stride
     */
    public int getIndexDimensionLength() {
    	if (this.isIndexAvailable()) {
        	return this.mIndexVariable.getDimension(0).getLength();
    	} else {
    		return -1;
    	}
    }

    protected void writeValues(NetcdfFileWriteable ncWrite, Variable var, 
    		Array values) throws IOException, InvalidRangeException {
    	String varName = var.getShortName();
    	ncWrite.write(varName, values);
    }
    
    /*
    protected void setFillValue(Variable var, double[] values) {
    	Number fillValue = SGNetCDFVariable.getFillValue(var);
    	if (fillValue != null) {
    		final double f = fillValue.doubleValue();
    		for (int ii = 0; ii < values.length; ii++) {
    			if (Double.isNaN(values[ii])) {
    				values[ii] = f;
    			}
    		}
    	}
    }
    
    protected void setFillValue(Variable var, double[][] values) {
    	Number fillValue = SGNetCDFVariable.getFillValue(var);
    	if (fillValue != null) {
    		final double f = fillValue.doubleValue();
    		for (int ii = 0; ii < values.length; ii++) {
    			for (int jj = 0; jj < values[ii].length; jj++) {
        			if (Double.isNaN(values[ii][jj])) {
        				values[ii][jj] = f;
        			}
    			}
    		}
    	}
    }
    */

    protected DataType getExportNumberDataType(SGNetCDFVariable var, SGExportParameter mode,
    		SGDataBufferPolicy policy) {
    	if (SGDataUtility.isArchiveDataSetOperation(mode.getType())) {
    		return var.getDataType();
    	} else {
    		return DataType.DOUBLE;
    	}
    }

    protected boolean setArray(Array array, double[] values) {
    	if (array instanceof ArrayInt.D1) {
    		ArrayInt.D1 a = (ArrayInt.D1) array;
    		int[] valueArray = new int[values.length];
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			valueArray[ii] = (int) values[ii];
    		}
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			a.set(ii, valueArray[ii]);
    		}
    	} else if (array instanceof ArrayShort.D1) {
    		ArrayShort.D1 a = (ArrayShort.D1) array;
    		short[] valueArray = new short[values.length];
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			valueArray[ii] = (short) values[ii];
    		}
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			a.set(ii, valueArray[ii]);
    		}
    	} else if (array instanceof ArrayLong.D1) {
    		ArrayLong.D1 a = (ArrayLong.D1) array;
    		long[] valueArray = new long[values.length];
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			valueArray[ii] = (long) values[ii];
    		}
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			a.set(ii, valueArray[ii]);
    		}
    	} else if (array instanceof ArrayFloat.D1) {
    		ArrayFloat.D1 a = (ArrayFloat.D1) array;
    		float[] valueArray = new float[values.length];
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			valueArray[ii] = (float) values[ii];
    		}
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			a.set(ii, valueArray[ii]);
    		}
    	} else if (array instanceof ArrayDouble.D1) {
    		ArrayDouble.D1 a = (ArrayDouble.D1) array;
    		double[] valueArray = values;
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			a.set(ii, valueArray[ii]);
    		}
    	} else if (array instanceof ArrayChar.D1) {
    		ArrayChar.D1 a = (ArrayChar.D1) array;
    		char[] valueArray = new char[values.length];
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			valueArray[ii] = (char) values[ii];
    		}
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			a.set(ii, valueArray[ii]);
    		}
    	} else if (array instanceof ArrayByte.D1) {
    		ArrayByte.D1 a = (ArrayByte.D1) array;
    		byte[] valueArray = new byte[values.length];
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			valueArray[ii] = (byte) values[ii];
    		}
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			a.set(ii, valueArray[ii]);
    		}
    	} else {
    		return false;
    	}
    	return true;
    }
    
    protected boolean setArray(Array array, double[][] values) {
    	if (array instanceof ArrayInt.D2) {
    		ArrayInt.D2 a = (ArrayInt.D2) array;
    		int[][] valueArray = new int[values.length][];
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			valueArray[ii] = new int[values[ii].length];
    			for (int jj = 0; jj < valueArray[ii].length; jj++) {
    				valueArray[ii][jj] = (int) values[ii][jj];
    			}
    		}
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			for (int jj = 0; jj < valueArray[ii].length; jj++) {
        			a.set(ii, jj, valueArray[ii][jj]);
    			}
    		}
    	} else if (array instanceof ArrayShort.D2) {
    		ArrayShort.D2 a = (ArrayShort.D2) array;
    		short[][] valueArray = new short[values.length][];
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			valueArray[ii] = new short[values[ii].length];
    			for (int jj = 0; jj < valueArray[ii].length; jj++) {
    				valueArray[ii][jj] = (short) values[ii][jj];
    			}
    		}
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			for (int jj = 0; jj < valueArray[ii].length; jj++) {
        			a.set(ii, jj, valueArray[ii][jj]);
    			}
    		}
    	} else if (array instanceof ArrayLong.D2) {
    		ArrayLong.D2 a = (ArrayLong.D2) array;
    		long[][] valueArray = new long[values.length][];
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			valueArray[ii] = new long[values[ii].length];
    			for (int jj = 0; jj < valueArray[ii].length; jj++) {
    				valueArray[ii][jj] = (long) values[ii][jj];
    			}
    		}
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			for (int jj = 0; jj < valueArray[ii].length; jj++) {
        			a.set(ii, jj, valueArray[ii][jj]);
    			}
    		}
    	} else if (array instanceof ArrayFloat.D2) {
    		ArrayFloat.D2 a = (ArrayFloat.D2) array;
    		float[][] valueArray = new float[values.length][];
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			valueArray[ii] = new float[values[ii].length];
    			for (int jj = 0; jj < valueArray[ii].length; jj++) {
    				valueArray[ii][jj] = (float) values[ii][jj];
    			}
    		}
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			for (int jj = 0; jj < valueArray[ii].length; jj++) {
        			a.set(ii, jj, valueArray[ii][jj]);
    			}
    		}
    	} else if (array instanceof ArrayDouble.D2) {
    		ArrayDouble.D2 a = (ArrayDouble.D2) array;
    		double[][] valueArray = values;
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			for (int jj = 0; jj < valueArray[ii].length; jj++) {
        			a.set(ii, jj, valueArray[ii][jj]);
    			}
    		}
    	} else if (array instanceof ArrayChar.D2) {
    		ArrayChar.D2 a = (ArrayChar.D2) array;
    		char[][] valueArray = new char[values.length][];
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			valueArray[ii] = new char[values[ii].length];
    			for (int jj = 0; jj < valueArray[ii].length; jj++) {
    				valueArray[ii][jj] = (char) values[ii][jj];
    			}
    		}
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			for (int jj = 0; jj < valueArray[ii].length; jj++) {
        			a.set(ii, jj, valueArray[ii][jj]);
    			}
    		}
    	} else if (array instanceof ArrayByte.D2) {
    		ArrayByte.D2 a = (ArrayByte.D2) array;
    		byte[][] valueArray = new byte[values.length][];
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			valueArray[ii] = new byte[values[ii].length];
    			for (int jj = 0; jj < valueArray[ii].length; jj++) {
    				valueArray[ii][jj] = (byte) values[ii][jj];
    			}
    		}
    		for (int ii = 0; ii < valueArray.length; ii++) {
    			for (int jj = 0; jj < valueArray[ii].length; jj++) {
        			a.set(ii, jj, valueArray[ii][jj]);
    			}
    		}
    	} else {
    		return false;
    	}
    	return true;
    }
    
    protected int[] createIntArray(final int len) {
    	int[] array = new int[len];
    	for (int ii = 0; ii < len; ii++) {
    		array[ii] = ii;
    	}
    	return array;
    }

    protected String getValidName(String str) {
    	return SGDataUtility.getNetCDFValidName(str);
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
		if (this.isIndexAvailable()) {
			String str = this.mIndexVariable.getName();
			varList.add(str);
			columnTypeList.add(INDEX);
		}
		if (this.isTimeVariableAvailable()) {
			String str = this.mTimeVariable.getName();
			varList.add(str);
			columnTypeList.add(ANIMATION_FRAME);
		}
	}
    
    /**
     * Returns a text string for the command of the origin.
     * 
     * @return a text string for the command of the origin
     */
    @Override
    public String getOriginCommandString() {
    	List<SGNetCDFVariable> varList = this.getNetcdfFile().getVariables();
    	StringBuffer sb = new StringBuffer();
    	sb.append('(');
    	int cnt = 0;
    	for (int ii = 0; ii < varList.size(); ii++) {
    		SGNetCDFVariable var = varList.get(ii);
    		if (!var.isCoordinateVariable()) {
    			continue;
    		}
    		final int origin = this.getOrigin(var.getName());
    		if (cnt > 0) {
    			sb.append(',');
    		}
    		StringBuffer varSb = new StringBuffer();
    		varSb.append(var.getName());
    		varSb.append('(');
    		varSb.append(origin);
    		varSb.append(')');
    		sb.append(varSb.toString());
    		cnt++;
    	}
    	sb.append(')');
    	return sb.toString();
    }

    protected SGNetCDFVariable findVariable(SGNetCDFFile ncfile, 
    		SGNetCDFVariable var) {
    	if (var == null) {
    		return null;
    	}
    	return ncfile.findVariable(var.getName());
    }
    
    protected SGNetCDFVariable[] findVariables(SGNetCDFFile ncfile, 
    		SGNetCDFVariable[] vars) {
    	if (vars == null) {
    		return null;
    	}
    	SGNetCDFVariable[] ret = new SGNetCDFVariable[vars.length];
    	for (int ii = 0; ii < vars.length; ii++) {
    		ret[ii] = this.findVariable(ncfile, vars[ii]);
    	}
    	return ret;
    }
    
    public boolean isRemoteFile() {
    	return this.getNetcdfFile().isRemoteFile();
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
    	StringBuffer sb = new StringBuffer();
    	List<Dimension> dimList = this.getToolTipNotSpatiallyVariedDimensionList();
    	for (int ii = 0; ii < dimList.size(); ii++) {
    		if (ii > 0) {
    			sb.append(",");
            	sb.append("<br>");
    		}
    		Dimension dim = dimList.get(ii);
    		String name = dim.getName();
    		Integer index = this.mOriginMap.get(name);
    		Double value = null;
    		try {
				value = this.getCoordinateValue(name, index);
			} catch (IOException e) {
			}

    		// append to the string buffer
    		sb.append(name);
    		sb.append('=');
    		sb.append(index);
    		if (value != null) {
        		sb.append(" (");
        		sb.append(value);
        		sb.append(")");
    		}
    	}
    	return sb.toString();
    }
    
    protected abstract List<Dimension> getToolTipNotSpatiallyVariedDimensionList();

	protected int findDimensionOrder(List<Dimension> dims, String dimName) {
		int order = -1;
		for (int ii = 0; ii < dims.size(); ii++) {
			Dimension dim = dims.get(ii);
			if (dimName.equals(dim.getName())) {
				order = ii;
				break;
			}
		}
		return order;
	}

	protected Value getIndexValueAt(SGNetCDFVariable var, final int index) {
        final double value = this.getValueAt(var, 
        		new SGNetCDFVariable[]{ this.mIndexVariable }, new int[] { index });
		return new Value(value, true);
	}
}
