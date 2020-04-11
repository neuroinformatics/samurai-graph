package jp.riken.brain.ni.samuraigraph.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;

import org.w3c.dom.Element;

import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

public class SGVXYNetCDFData extends SGTwoDimensionalNetCDFData implements SGIVXYTypeData {

    /**
     * The variable for the first component
     */
    protected SGNetCDFVariable mFirstComponentVariable = null;

    /**
     * The variable for the second component
     */
    protected SGNetCDFVariable mSecondComponentVariable = null;

    /**
     * A flag for polar mode.
     */
    private boolean mPolarFlag = false;

    /**
     * The default constructor.
     *
     */
    public SGVXYNetCDFData() {
        super();
    }

    /**
     * Builds a data object with given netCDF data.
     *
     * @param ncfile
     *            netCDF data file
     * @param obs
     *            netCDF data observer
     * @param xInfo
     *            information of x coordinate values
     * @param yInfo
     *            information of y coordinate values
     * @param firstInfo
     *            information of the first component values
     * @param secondInfo
     *            information of the second component values
     * @param isPolar
     *            true for polar coordinate
     * @param timeInfo
     *            information of time values
     * @param indexInfo
     *            information of index
     * @param xIndexInfo
     *            information of index for x-values
     * @param yIndexInfo
     *            information of index for y-values
     * @param indexStride
     *            stride for the index
     * @param xStride
     *            stride for the x values
     * @param yStride
     *            stride for the y values
     * @param strideAvailable
     *            flag whether to set available the stride
     */
	public SGVXYNetCDFData(SGNetCDFFile ncfile, SGDataSourceObserver obs,
			final SGNetCDFDataColumnInfo xInfo,
			final SGNetCDFDataColumnInfo yInfo,
			final SGNetCDFDataColumnInfo firstInfo,
			final SGNetCDFDataColumnInfo secondInfo, final boolean isPolar,
			final SGNetCDFDataColumnInfo timeInfo,
			final SGNetCDFDataColumnInfo indexInfo,
			final SGNetCDFDataColumnInfo xIndexInfo,
			final SGNetCDFDataColumnInfo yIndexInfo,
			final SGIntegerSeriesSet indexStride,
			final SGIntegerSeriesSet xStride, final SGIntegerSeriesSet yStride,
			final boolean strideAvailable) {

        super(ncfile, obs, xInfo, yInfo, timeInfo, indexInfo, xIndexInfo, yIndexInfo, indexStride,
        		xStride, yStride, strideAvailable);

        if (firstInfo == null || secondInfo == null) {
            throw new IllegalArgumentException("firstInfo == null || secondInfo == null");
        }

        // get variables
        SGNetCDFVariable firstVar = ncfile.findVariable(firstInfo.getName());
        if (firstVar == null) {
            throw new IllegalArgumentException("Illegal variable name: " + firstInfo.getName());
        }
        SGNetCDFVariable secondVar = ncfile.findVariable(secondInfo.getName());
        if (secondVar == null) {
            throw new IllegalArgumentException("Illegal variable name: " + secondInfo.getName());
        }

        // check variables
        Dimension xDim = this.getXDimension();
        Dimension yDim = this.getYDimension();
        this.checkNonCoordinateVariable(firstVar, xDim);
        this.checkNonCoordinateVariable(firstVar, yDim);
        this.checkNonCoordinateVariable(secondVar, xDim);
        this.checkNonCoordinateVariable(secondVar, yDim);

        // set to attributes
        this.mFirstComponentVariable = firstVar;
        this.mSecondComponentVariable = secondVar;
        this.mPolarFlag = isPolar;
    }

    /**
     * Disposes of this data object.
     * Do not call any method of this object after called this method.
     */
    public void dispose() {
        super.dispose();
        this.mFirstComponentVariable = null;
        this.mSecondComponentVariable = null;
    }

    /**
     * Returns whether the data is given in the polar coordinate.
     *
     * @return true if the data is given in the polar coordinate
     *
     */
    public boolean isPolar() {
        return this.mPolarFlag;
    }

    /**
     * Returns a map of information.
     *
     * @return a map of information
     */
    public Map<String, Object> getInfoMap() {
        Map<String, Object> infoMap = super.getInfoMap();
        infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, this.isPolar());
        return infoMap;
    }

    /**
     * Returns a map of stride for data arrays.
     *
     * @return a map of stride for data arrays
     */
    @Override
    protected Map<String, SGIntegerSeriesSet> getStrideMap() {
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    	if (!this.isIndexAvailable()) {
            map.put(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X, this.getXStride());
            map.put(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y, this.getYStride());
    	} else {
        	map.put(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE,
        			this.getIndexStride());
    	}
    	return map;
    }

    /**
     * Returns a text string of data type.
     *
     * @return a text string of data type
     */
    public String getDataType() {
        return SGDataTypeConstants.VXY_NETCDF_DATA;
    }

    /**
     * Returns the number of data points taking into account the stride.
     *
     * @return the number of data points taking into account the stride
     */
	@Override
    public int getPointsNumber() {
    	if (this.isIndexAvailable()) {
        	return this.getIndexStrideInstance().getLength();
    	} else {
    		List<SGXYSimpleDoubleValueIndexBlock> blocks = this.getFirstComponentValueBlockList();
    		int num = 0;
    		for (SGXYSimpleDoubleValueIndexBlock block : blocks) {
    			num += block.getLength();
    		}
    		return num;
    	}
    }

    /**
     * Returns an array of variables that are assigned the column type.
     *
     * @return an array of variables
     */
	@Override
    public SGNetCDFVariable[] getAssignedVariables() {
    	List<SGNetCDFVariable> varList = new ArrayList<SGNetCDFVariable>();
    	varList.add(this.mXVariable);
    	varList.add(this.mYVariable);
    	varList.add(this.mFirstComponentVariable);
    	varList.add(this.mSecondComponentVariable);
        if (this.mTimeVariable != null) {
        	varList.add(this.mTimeVariable);
        }
    	if (this.mIndexVariable != null) {
    		varList.add(this.mIndexVariable);
    	}
    	if (this.mXIndexVariable != null) {
    		varList.add(this.mXIndexVariable);
    	}
    	if (this.mYIndexVariable != null) {
    		varList.add(this.mYIndexVariable);
    	}
        SGNetCDFVariable[] varArray = new SGNetCDFVariable[varList.size()];
        return (SGNetCDFVariable[]) varList.toArray(varArray);
    }

    /**
     * Returns an array of current column types.
     * @return
     *        an array of current column types
     */
    public String[] getCurrentColumnType() {
		final boolean polar = this.isPolar();
        final String com1 = SGDataUtility.getVXYFirstComponentColumnType(polar);
        final String com2 = SGDataUtility.getVXYSecondComponentColumnType(polar);
        List<SGNetCDFVariable> varList = this.getNetcdfFile().getVariables();
        final int varNum = varList.size();
        String[] array = new String[varNum];
        for (int ii = 0; ii < varNum; ii++) {
        	SGNetCDFVariable var = varList.get(ii);
            if (var.equals(this.mXVariable)) {
                array[ii] = X_COORDINATE;
            } else if (var.equals(this.mYVariable)) {
                array[ii] = Y_COORDINATE;
            } else if (var.equals(this.mFirstComponentVariable)) {
                array[ii] = com1;
            } else if (var.equals(this.mSecondComponentVariable)) {
                array[ii] = com2;
            } else if (var.equals(this.mTimeVariable)) {
                array[ii] = ANIMATION_FRAME;
			} else if (var.equals(this.mIndexVariable)) {
				array[ii] = INDEX;
			} else if (var.equals(this.mXIndexVariable)) {
				array[ii] = X_INDEX;
			} else if (var.equals(this.mYIndexVariable)) {
				array[ii] = Y_INDEX;
            } else {
                array[ii] = "";
            }
        }
        return array;
    }

    /**
     * Sets the type of data columns.
     *
     * @param column
     *              an array of column types
     * @return true if succeeded
     */
    public boolean setColumnType(String[] columns) {
		final boolean polar = this.isPolar();
        final String com1 = SGDataUtility.getVXYFirstComponentColumnType(polar);
        final String com2 = SGDataUtility.getVXYSecondComponentColumnType(polar);

        List<SGNetCDFVariable> xVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> yVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> fVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> sVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> timeVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> indexVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> xIndexVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> yIndexVarList = new ArrayList<SGNetCDFVariable>();

        List<SGNetCDFVariable> varList = this.getNetcdfFile().getVariables();
        for (int ii = 0; ii < columns.length; ii++) {
        	SGNetCDFVariable var = varList.get(ii);
        	String valueType = var.getValueType();
            if (SGDataUtility.isEqualColumnType(X_COORDINATE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                xVarList.add(var);
            } else if (SGDataUtility.isEqualColumnType(Y_COORDINATE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                yVarList.add(var);
            } else if (SGDataUtility.isEqualColumnType(com1, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                fVarList.add(var);
            } else if (SGDataUtility.isEqualColumnType(com2, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                sVarList.add(var);
            } else if (SGDataUtility.isEqualColumnType(ANIMATION_FRAME, columns[ii])
            		|| SGDataUtility.isEqualColumnType(TIME, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                timeVarList.add(var);
            } else if (SGDataUtility.isEqualColumnType(INDEX, columns[ii])
            		|| SGDataUtility.isEqualColumnType(SERIAL_NUMBERS, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                indexVarList.add(var);
//            } else if (SGDataUtility.equals(X_INDEX, columns[ii])) {
//            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
//            		return false;
//            	}
//                xIndexVarList.add(var);
//            } else if (SGDataUtility.equals(Y_INDEX, columns[ii])) {
//            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
//            		return false;
//            	}
//                yIndexVarList.add(var);
            } else if ("".equals(columns[ii])) {
            	continue;
            } else {
            	return false;
            }
        }
    	if (xVarList.size() != 1 || yVarList.size() != 1 || fVarList.size() != 1 || sVarList.size() != 1) {
    		return false;
    	}
    	if (xIndexVarList.size() > 1 || yIndexVarList.size() > 1) {
    		return false;
    	}

    	SGNetCDFVariable xVar = xVarList.get(0);
    	SGNetCDFVariable yVar = yVarList.get(0);
    	SGNetCDFVariable fVar = fVarList.get(0);
    	SGNetCDFVariable sVar = sVarList.get(0);

    	// index variable
    	SGNetCDFVariable indexVar = (indexVarList.size() == 1) ? indexVarList.get(0) : null;
    	SGNetCDFVariable xIndexVar = (xIndexVarList.size() == 1) ? xIndexVarList.get(0) : null;
    	SGNetCDFVariable yIndexVar = (yIndexVarList.size() == 1) ? yIndexVarList.get(0) : null;

        // time variable
    	SGNetCDFVariable timeVar = (timeVarList.size() == 1) ? timeVarList.get(0) : null;

        // set variables
        this.mXVariable = xVar;
        this.mYVariable = yVar;
        this.mFirstComponentVariable = fVar;
        this.mSecondComponentVariable = sVar;
        this.mIndexVariable = indexVar;
        this.mXIndexVariable = xIndexVar;
        this.mYIndexVariable = yIndexVar;
        this.setTimeVariable(timeVar);

        return true;
    }

    /**
     * Sets the data.
     *
     * @param data
     *             data set to this object
     * @return
     *             true if succeeded
     */
    public boolean setData(SGData data) {
        if (!(data instanceof SGVXYNetCDFData)) {
            throw new IllegalArgumentException(
            		"Given data is not the instance of SGVXYNetCDFData class.");
        }
        if (super.setData(data) == false) {
            return false;
        }
        SGVXYNetCDFData nData = (SGVXYNetCDFData) data;
        this.mFirstComponentVariable = nData.mFirstComponentVariable;
        this.mSecondComponentVariable = nData.mSecondComponentVariable;
        this.mPolarFlag = nData.mPolarFlag;
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
        if (!(p instanceof VXYNetCDFDataProperties)) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        VXYNetCDFDataProperties np = (VXYNetCDFDataProperties) p;
        this.mFirstComponentVariable = this.getNetcdfFile().findVariable(np.fName);
        this.mSecondComponentVariable = this.getNetcdfFile().findVariable(np.sName);
        return true;
    }

    /**
     * Returns the properties of this data.
     *
     * @return the properties of this data
     */
    public SGProperties getProperties() {
        SGProperties p = new VXYNetCDFDataProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    /**
     * Gets the properties of this data.
     *
     * @param p
     *      the properties of this data
     * @return
     *      true if succeeded
     */
    public boolean getProperties(SGProperties p) {
        if (!(p instanceof VXYNetCDFDataProperties)) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        VXYNetCDFDataProperties sp = (VXYNetCDFDataProperties) p;
        sp.fName = this.mFirstComponentVariable.getName();
        sp.sName = this.mSecondComponentVariable.getName();
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
    public boolean writeProperty(Element el, final SGExportParameter type) {
        if (super.writeProperty(el, type) == false) {
            return false;
        }
        el.setAttribute(KEY_X_COORDINATE_VARIABLE_NAME, this.mXVariable.getValidName());
        el.setAttribute(KEY_Y_COORDINATE_VARIABLE_NAME, this.mYVariable.getValidName());
        el.setAttribute(KEY_FIRST_COMPONENT_VARIABLE_NAME, this.mFirstComponentVariable.getValidName());
        el.setAttribute(KEY_SECOND_COMPONENT_VARIABLE_NAME, this.mSecondComponentVariable.getValidName());
        return true;
    }

    /**
     * Returns an array of values for the first component.
     *
     * @return an array of values for the first component
     */
	@Override
    public double[] getFirstComponentValueArray(final boolean all) {
		return SGDataUtility.getFirstComponentValueArray(this, all);
	}
	
	@Override
	public boolean useFirstComponentValueCache(boolean all) {
		return this.useComponentIndexCache(all);
	}

	@Override
	public double[] getFirstComponentValueArray(final boolean all, final boolean useCache,
			final boolean removeInvalidValues) {
		double[] ret = null;
        if (this.isIndexAvailable()) {
            ret = this.getValueArray(this.mFirstComponentVariable, 
            		new SGNetCDFVariable[]{ this.mIndexVariable }, all,
            		removeInvalidValues);
        } else {
        	SGNetCDFVariable[] cVars = this.getCoordinateVariables();
            ret = this.getValueArray(this.mFirstComponentVariable, cVars, all,
            		removeInvalidValues);
        }
    	if (useCache) {
            SGVXYDataCache.setFirstComponentValues(this, ret);
    	}
    	return ret;
	}
	
    /**
     * Returns an array of values for the second component.
     *
     * @return an array of values for the second component
     */
	@Override
    public double[] getSecondComponentValueArray(final boolean all) {
		return SGDataUtility.getSecondComponentValueArray(this, all);
    }

	@Override
	public boolean useSecondComponentValueCache(boolean all) {
		return this.useComponentIndexCache(all);
	}

	@Override
    public double[] getSecondComponentValueArray(final boolean all, final boolean useCache,
    		final boolean removeInvalidValues) {
    	double[] ret = null;
        if (this.isIndexAvailable()) {
            ret = this.getValueArray(this.mSecondComponentVariable, 
            		new SGNetCDFVariable[]{ this.mIndexVariable }, all,
            		removeInvalidValues);
        } else {
        	SGNetCDFVariable[] cVars = this.getCoordinateVariables();
            ret = this.getValueArray(this.mSecondComponentVariable, cVars, all,
            		removeInvalidValues);
        }
    	if (useCache) {
            SGVXYDataCache.setSecondComponentValues(this, ret);
    	}
    	return ret;
    }

    /**
     * Returns a scatter type array of the first component.
     *
     * @param all
     *           true to get all values
     * @return a scatter type array of the first component
     */
    protected double[] getScatterFirstComponentValueArray(final boolean all) {
    	return this.getFirstComponentValueArray(all);
    }

    /**
     * Returns a scatter type array of the second component.
     *
     * @param all
     *           true to get all values
     * @return a scatter type array of the second component
     */
    protected double[] getScatterSecondComponentValueArray(final boolean all) {
    	return this.getSecondComponentValueArray(all);
    }

    /**
     * Returns an array of X-values.
     * 
     * @return an array of X-values
     */
	@Override
    public double[] getXValueArray(final boolean all) {
    	double[] values = SGDataUtility.getXValueArray(this, all);
		return SGDataUtility.updateXValueArray(this, all, values);
    }

	@Override
	public boolean useXValueCache(boolean all) {
		return this.useXYCache(all, this.mXStride);
	}

	@Override
	public double[] getXValueArray(final boolean all, final boolean useCache,
			final boolean removeInvalidValues) {
		double[] ret = this.getXYValueArray(all, this.mXVariable, this.mYVariable, 
				this.getXStrideInstance(), removeInvalidValues);
		if (useCache) {
			SGVXYDataCache.setXValues(this, ret);
		}
		return ret;
	}

    /**
     * Returns an array of Y-values.
     * 
     * @return an array of Y-values
     */
	@Override
    public double[] getYValueArray(final boolean all) {
    	double[] values = SGDataUtility.getYValueArray(this, all);
		return SGDataUtility.updateYValueArray(this, all, values);
    }
	
	@Override
	public boolean useYValueCache(boolean all) {
		return this.useXYCache(all, this.mYStride);
	}

	@Override
	public double[] getYValueArray(final boolean all, final boolean useCache,
			final boolean removeInvalidValues) {
		double[] ret = this.getXYValueArray(all, this.mYVariable, this.mXVariable, 
				this.getYStrideInstance(), removeInvalidValues);
		if (useCache) {
			SGVXYDataCache.setYValues(this, ret);
		}
		return ret;
	}

    /**
     * Returns an array of x component of vectors.
     *
     * @return an array of x component of vectors
     */
    public double[] getXComponentArray(final boolean all) {
    	double[] ret = null;
        if (this.isPolar()) {
            final double[] magArray = this.getFirstComponentValueArray(all);
            final double[] angleArray = this.getSecondComponentValueArray(all);
            ret = SGVXYDataUtility.getXComponentArray(magArray, angleArray);
        } else {
            ret = this.getFirstComponentValueArray(all);
        }
        return ret;
    }

    /**
     * Returns an array of y component of vectors.
     *
     * @return an array of y component of vectors
     */
    public double[] getYComponentArray(final boolean all) {
    	double[] ret = null;
        if (this.isPolar()) {
            final double[] magArray = this.getFirstComponentValueArray(all);
            final double[] angleArray = this.getSecondComponentValueArray(all);
            ret = SGVXYDataUtility.getYComponentArray(magArray, angleArray);
        } else {
            ret = this.getSecondComponentValueArray(all);
        }
        return ret;
    }

    /**
     * Returns an array of magnitude of vectors.
     *
     * @return an array of magnitude of vectors
     */
    public double[] getMagnitudeArray(final boolean all) {
    	double[] ret = null;
        if (this.isPolar()) {
            ret = this.getFirstComponentValueArray(all);
        } else {
            final double[] xComArray = this.getFirstComponentValueArray(all);
            final double[] yComArray = this.getSecondComponentValueArray(all);
            ret = SGVXYDataUtility.getMagnitudeArray(xComArray, yComArray);
        }
        return ret;
    }

    /**
     * Returns an array of angle of vectors.
     *
     * @return an array of angle of vectors
     */
    public double[] getAngleArray(final boolean all) {
    	double[] ret = null;
        if (this.isPolar()) {
            ret = this.getSecondComponentValueArray(all);
        } else {
            final double[] xComArray = this.getFirstComponentValueArray(all);
            final double[] yComArray = this.getSecondComponentValueArray(all);
            ret = SGVXYDataUtility.getAngleArray(xComArray, yComArray);
        }
        return ret;
    }

    /**
     * Returns the bounds of x-values.
     *
     * @return the bounds of x-values
     */
    public SGValueRange getBoundsX() {
        return SGDataUtility.getBoundsX(this);
    }

    /**
     * Returns the bounds of y-values.
     *
     * @return the bounds of y-values
     */
    public SGValueRange getBoundsY() {
        return SGDataUtility.getBoundsY(this);
    }

    /**
     * A class of vector XY NetCDF data properties.
     */
    public static class VXYNetCDFDataProperties extends TwoDimensionalNetCDFDataProperties {

        protected String fName = null;

        protected String sName = null;

        /**
         * The default constructor.
         */
        public VXYNetCDFDataProperties() {
            super();
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.fName = null;
            this.sName = null;
        }

    	@Override
    	public boolean equals(Object obj) {
            if ((obj instanceof VXYNetCDFDataProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            return true;
        }

    	@Override
        public boolean hasEqualSize(DataProperties dp) {
            if ((dp instanceof VXYNetCDFDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualSize(dp)) {
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
            if ((dp instanceof VXYNetCDFDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualColumnTypes(dp)) {
            	return false;
            }
            VXYNetCDFDataProperties p = (VXYNetCDFDataProperties) dp;
            if (SGUtility.equals(this.fName, p.fName) == false) {
                return false;
            }
            if (SGUtility.equals(this.sName, p.sName) == false) {
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
        	VXYNetCDFDataProperties p = (VXYNetCDFDataProperties) super.copy();
        	return p;
        }
    }

    /**
     * Sets the stride of data arrays.
     *
     * @param map
     *           the map of the stride
     */
    public void setStrideMap(Map<String, SGIntegerSeriesSet> map) {
    	this.mIndexStride = map.get(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE);
    	this.mXStride = map.get(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X);
    	this.mYStride = map.get(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y);
    }

    /**
     * Returns the list of blocks of values of the first component.
     *
     * @return the list of blocks of values of the first component
     */
	@Override
    public List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlockList() {
		return SGDataUtility.getFirstComponentValueBlockList(this, false, true, true);
    }

	@Override
    public List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlockListSub(
    		final boolean all, final boolean useCache, 
    		final boolean removeInvalidValues) {
		List<SGXYSimpleDoubleValueIndexBlock> ret = this.getValueBlockList(
				this.mFirstComponentVariable, all, removeInvalidValues);
		if (useCache) {
	        SGVXYDataCache.setFirstComponentValueBlockList(this, ret);
		}
    	return ret;
    }

    /**
     * Returns the list of blocks of values of the second component.
     *
     * @return the list of blocks of values of the second component
     */
	@Override
    public List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlockList() {
		return SGDataUtility.getSecondComponentValueBlockList(this, false, true, true);
    }

	@Override
    public List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlockListSub(
    		final boolean all, final boolean useCache, 
    		final boolean removeInvalidValues) {
		List<SGXYSimpleDoubleValueIndexBlock> ret = this.getValueBlockList(
				this.mSecondComponentVariable, all, removeInvalidValues);
		if (useCache) {
	        SGVXYDataCache.setSecondComponentValueBlockList(this, ret);
		}
        return ret;
    }

    /**
     * Creates and returns a data buffer.
     * 
     * @param param
     *           parameters for data buffer
     * @return the data buffer
     */
	@Override
	public SGDataBuffer getDataBuffer(SGDataBufferPolicy param) {
		final boolean all = param.isAllValuesGotten();
		final boolean edit = param.isEditedValuesReflected();
		final boolean remove = param.isInvalidValuesRemoved();
		
		double[] xValues = this.getXValueArray(all, false, remove);
		double[] yValues = this.getYValueArray(all, false, remove);
		
		final boolean polar = this.isPolar();
		if (this.isIndexAvailable()) {
			double[] fValues = this.getFirstComponentValueArray(all, false, remove);
			double[] sValues = this.getSecondComponentValueArray(all, false, remove);
			
			// set edited values
			if (edit) {
				for (SGDataValueHistory dataValue : this.mEditedDataValueList) {
					SGDataUtility.setEditedValue(this, xValues, yValues, fValues, 
							sValues, all, dataValue);
				}
			}
			
			return new SGVXYDataBuffer(xValues, yValues, fValues, sValues, polar);
			
		} else {
			
			double[] fValues = this.getFirstComponentValueArray(all, false, remove);
			double[][] fGridValues = this.getGridValueArray(fValues, all);

			double[] sValues = this.getSecondComponentValueArray(all, false, remove);
			double[][] sGridValues = this.getGridValueArray(sValues, all);
			
			// set edited values
			if (edit) {
				for (SGDataValueHistory dataValue : this.mEditedDataValueList) {
					SGDataUtility.setEditedValue(this, xValues, yValues, fGridValues, 
							sGridValues, all, dataValue);
				}
			}
			
			return new SGVXYGridDataBuffer(xValues, yValues, fGridValues, sGridValues, polar);
		}
	}

	@Override
	protected boolean exportToFile(NetcdfFileWriteable ncWrite, 
			final SGExportParameter mode, SGDataBufferPolicy policy)
			throws IOException, InvalidRangeException {
		
		DataType xDataType = this.getExportNumberDataType(this.mXVariable, mode, policy);
		DataType yDataType = this.getExportNumberDataType(this.mYVariable, mode, policy);
		DataType fDataType = this.getExportNumberDataType(this.mFirstComponentVariable, mode, policy);
		DataType sDataType = this.getExportNumberDataType(this.mSecondComponentVariable, mode, policy);

		if (this.isIndexAvailable()) {
			SGVXYDataBuffer buffer = (SGVXYDataBuffer) this.getDataBuffer(policy);
			
			final boolean all = policy.isAllValuesGotten();
			final int len = all ? this.getIndexDimensionLength() : this.mIndexStride.getLength();
			
			// adds dimensions
			String indexDimName = this.mIndexVariable.getValidName();
			this.addDimension(ncWrite, indexDimName, len);
			
			String xName = this.mXVariable.getValidName();
			String yName = this.mYVariable.getValidName();
			String fName = this.mFirstComponentVariable.getValidName();
			String sName = this.mSecondComponentVariable.getValidName();

			// adds variables
			Variable indexVar = this.addVariable(ncWrite, mode, indexDimName, DataType.INT, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, indexDimName, policy);
			Variable xVar = this.addVariable(ncWrite, mode, xName, xDataType, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, indexDimName, policy);
			Variable yVar = this.addVariable(ncWrite, mode, yName, yDataType, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, indexDimName, policy);
			Variable fVar = this.addVariable(ncWrite, mode, fName, 
					fDataType, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, indexDimName, policy);
			Variable sVar = this.addVariable(ncWrite, mode, sName, 
					sDataType, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, indexDimName, policy);
			
			// creates the file
			ncWrite.create();
			
			// writes data to the file
			int[] indices = this.mIndexStride.getNumbers();
			double[] indexValues = new double[indices.length];
			for (int ii = 0; ii < indexValues.length; ii++) {
				indexValues[ii] = (double) indices[ii];
			}
            Array indexArray = Array.factory(DataType.INT, new int[] { len });
			this.setArray(indexArray, indexValues);
			this.writeValues(ncWrite, indexVar, indexArray);
            
			double[] xValues = buffer.getXValues();
			Array xArray = Array.factory(xDataType, new int[] { len });
			this.setArray(xArray, xValues);
			this.writeValues(ncWrite, xVar, xArray);
			
			double[] yValues = buffer.getYValues();
			Array yArray = Array.factory(yDataType, new int[] { len });
			this.setArray(yArray, yValues);
			this.writeValues(ncWrite, yVar, yArray);
			
			double[] fValues = buffer.getFirstComponentValues();
			Array fArray = Array.factory(fDataType, new int[] { len });
			this.setArray(fArray, fValues);
			this.writeValues(ncWrite, fVar, fArray);

			double[] sValues = buffer.getSecondComponentValues();
			Array sArray = Array.factory(sDataType, new int[] { len });
			this.setArray(sArray, sValues);
			this.writeValues(ncWrite, sVar, sArray);

		} else {
			
			SGVXYGridDataBuffer buffer = (SGVXYGridDataBuffer) this.getDataBuffer(policy);

			final int xLen = buffer.getLengthX();
			final int yLen = buffer.getLengthY();
			
			String xName = this.mXVariable.getValidName();
			String yName = this.mYVariable.getValidName();
			String fName = this.mFirstComponentVariable.getValidName();
			String sName = this.mSecondComponentVariable.getValidName();

			// adds dimensions
			this.addDimension(ncWrite, xName, xLen);
			this.addDimension(ncWrite, yName, yLen);

			// adds variables
			Variable xVar = this.addVariable(ncWrite, mode, xName, xDataType, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, xName, policy);
			Variable yVar = this.addVariable(ncWrite, mode, yName, yDataType, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, yName, policy);
			String dimString = SGDataUtility.getDimensionString(new String[] { xName, yName });
			Variable fVar = this.addVariable(ncWrite, mode, fName, fDataType, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, dimString, policy);
			Variable sVar = this.addVariable(ncWrite, mode, sName, sDataType, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, dimString, policy);

			// creates the file
			ncWrite.create();

			// writes data to the file
			double[] xValues = buffer.getXValues();
			Array xArray = Array.factory(xDataType, new int[] { xLen });
			this.setArray(xArray, xValues);
			this.writeValues(ncWrite, xVar, xArray);
			
			double[] yValues = buffer.getYValues();
			Array yArray = Array.factory(yDataType, new int[] { yLen });
			this.setArray(yArray, yValues);
			this.writeValues(ncWrite, yVar, yArray);
			
			List<SGXYSimpleDoubleValueIndexBlock> fBlocks = buffer.getFirstComponentValueBlocks();
			List<SGXYSimpleDoubleValueIndexBlock> sBlocks = buffer.getSecondComponentValueBlocks();
			List<Integer> xIndexList = new ArrayList<Integer>();
			List<Integer> yIndexList = new ArrayList<Integer>();
			this.getIndexList(fBlocks, xIndexList, yIndexList);
			double[][] fValues = SGDataUtility.getTwoDimensionalValues(fBlocks, xIndexList, yIndexList);
			fValues = SGUtility.transpose(fValues);
			Array fArray = Array.factory(fDataType, new int[] { xLen, yLen });
			this.setArray(fArray, fValues);
			this.writeValues(ncWrite, fVar, fArray);
			double[][] sValues = SGDataUtility.getTwoDimensionalValues(sBlocks, xIndexList, yIndexList);
			sValues = SGUtility.transpose(sValues);
			Array sArray = Array.factory(sDataType, new int[] { xLen, yLen });
			this.setArray(sArray, sValues);
			this.writeValues(ncWrite, sVar, sArray);
		}
		
		return true;
	}

	@Override
    protected void getVarNameColumnTypeList(List<String> varList, 
    		List<String> columnTypeList) {
    	
    	super.getVarNameColumnTypeList(varList, columnTypeList);
    	
    	final boolean polar = this.isPolar();

		String strX = this.mXVariable.getName();
		varList.add(strX);
		columnTypeList.add(X_COORDINATE);
		
		String strY = this.mYVariable.getName();
		varList.add(strY);
		columnTypeList.add(Y_COORDINATE);
		
		String strF = this.mFirstComponentVariable.getName();
		varList.add(strF);
		columnTypeList.add(SGDataUtility.getVXYFirstComponentColumnType(polar));
		
		String strS = this.mSecondComponentVariable.getName();
		varList.add(strS);
		columnTypeList.add(SGDataUtility.getVXYSecondComponentColumnType(polar));
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
    	SGNetCDFFile ncfile = (SGNetCDFFile) src;
    	this.mXVariable = this.findVariable(ncfile, this.mXVariable);
    	this.mYVariable = this.findVariable(ncfile, this.mYVariable);
    	this.mFirstComponentVariable = this.findVariable(
    			ncfile, this.mFirstComponentVariable);
    	this.mSecondComponentVariable = this.findVariable(
    			ncfile, this.mSecondComponentVariable);
    }

    /**
     * Returns the bounds of x-values for all animation frames.
     * 
     * @return the bounds of x-values
     */
	@Override
    public SGValueRange getAllAnimationFrameBoundsX() {
		return SGDataUtility.getAllAnimationFrameBoundsX(this);
	}

    /**
     * Returns the bounds of y-values for all animation frames.
     * 
     * @return the bounds of y-values
     */
	@Override
    public SGValueRange getAllAnimationFrameBoundsY() {
		return SGDataUtility.getAllAnimationFrameBoundsY(this);
	}

    /**
     * Returns the array of column types.
     * 
     * @return the array of column types
     */
	@Override
    public String[] getDataViewerColumnTypes() {
    	List<String> list = new ArrayList<String>();
		list.add(SGIDataColumnTypeConstants.X_COORDINATE);
		list.add(SGIDataColumnTypeConstants.Y_COORDINATE);
		final boolean polar = this.isPolar();
    	final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
    	final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
		list.add(first);
		list.add(second);
    	String[] ret = list.toArray(new String[list.size()]);
		return ret;
    }
	
    /**
     * Returns preferred column type for data viewer.
     * 
     * @return preferred column type for data viewer
     */
	@Override
    public String getPreferredDataViewColumnType() {
    	return SGDataUtility.getPreferredDataViewColumnType(this);
    }

	@Override
	public Double getDataViewerValue(String columnType, int row, int col) {
		return SGDataUtility.getDataViewerValue(this, columnType, row, col);
	}

	@Override
	public int getDataViewerColumnNumber(final String columnType, final boolean all) {
		int ret = 0;
		if (this.isIndexAvailable()) {
			ret = 1;
		} else {
			final boolean polar = this.isPolar();
			final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
			final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
			if (SGIDataColumnTypeConstants.X_COORDINATE.equals(columnType)
					|| SGIDataColumnTypeConstants.Y_COORDINATE.equals(columnType)) {
				ret = 1;
			} else if (first.equals(columnType) || second.equals(columnType)) {
				if (all) {
					ret = this.mXVariable.getDimension(0).getLength();
				} else {
					ret = this.mXStride.getLength();
				}
			}
		}
		return ret;
	}

	@Override
	public int getDataViewerRowNumber(final String columnType, final boolean all) {
		int ret = 0;
		if (this.isIndexAvailable()) {
			if (all) {
				ret = this.mIndexVariable.getDimension(0).getLength();
			} else {
				ret = this.mIndexStride.getLength();
			}
		} else {
			final boolean polar = this.isPolar();
			final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
			final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
			if (SGIDataColumnTypeConstants.X_COORDINATE.equals(columnType)) {
				if (all) {
					ret = this.mXVariable.getDimension(0).getLength();
				} else {
					ret = this.mXStride.getLength();
				}
			} else if (SGIDataColumnTypeConstants.Y_COORDINATE.equals(columnType)) {
				if (all) {
					ret = this.mYVariable.getDimension(0).getLength();
				} else {
					ret = this.mYStride.getLength();
				}
			} else if (first.equals(columnType) || second.equals(columnType)) {
				if (all) {
					ret = this.mYVariable.getDimension(0).getLength();
				} else {
					ret = this.mYStride.getLength();
				}
			}
		}
		return ret;
	}

	@Override
    public Double getXValueAt(final int index) {
		double[] values = this.getXValueArray(false);
		final double d = values[index];
		return d;
	}
    
	@Override
    public Double getYValueAt(final int index) {
		double[] values = this.getYValueArray(false);
		final double d = values[index];
		return d;
	}

	@Override
    public Double getFirstComponentValueAt(final int index) {
		if (!this.isIndexAvailable()) {
			throw new Error("Unsupported.");
		}
		double[] values = this.getFirstComponentValueArray(false);
		final double d = values[index];
		return d;
	}

	@Override
    public Double getFirstComponentValueAt(final int row, final int col) {
		if (this.isIndexAvailable()) {
			throw new Error("Unsupported.");
		}
		List<SGXYSimpleDoubleValueIndexBlock> blockList = this.getFirstComponentValueBlockList();
		Double d = SGXYSimpleDoubleValueIndexBlock.getDataViewerValue(blockList, row, col);
		return d;
	}

	@Override
    public Double getSecondComponentValueAt(final int index) {
		if (!this.isIndexAvailable()) {
			throw new Error("Unsupported.");
		}
		double[] values = this.getSecondComponentValueArray(false);
		final double d = values[index];
		return d;
	}

	@Override
    public Double getSecondComponentValueAt(final int row, final int col) {
		if (this.isIndexAvailable()) {
			throw new Error("Unsupported.");
		}
		List<SGXYSimpleDoubleValueIndexBlock> blockList = this.getSecondComponentValueBlockList();
		Double d = SGXYSimpleDoubleValueIndexBlock.getDataViewerValue(blockList, row, col);
		return d;
	}

	@Override
    public SGIntegerSeriesSet getDataViewerColStride(String columnType) {
		SGIntegerSeriesSet ret = null;
		final boolean polar = this.isPolar();
		final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
		final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
		if (!this.isIndexAvailable() 
				&& (first.equals(columnType) || second.equals(columnType))) {
			if (this.isStrideAvailable()) {
				ret = this.mXStride;
			} else {
				final int len = this.getXDimensionLength();
				ret = SGIntegerSeriesSet.createInstance(len);
			}
		} else {
			ret = new SGIntegerSeriesSet(0);
		}
		return ret;
    }

	@Override
    public SGIntegerSeriesSet getDataViewerRowStride(String columnType) {
		SGIntegerSeriesSet ret = null;
		if (this.isStrideAvailable()) {
			if (this.isIndexAvailable()) {
				ret = this.mIndexStride;
			} else {
				if (X_COORDINATE.equals(columnType)) {
					ret = this.mXStride;
				} else {
					ret = this.mYStride;
				}
			}
		} else {
			final int len;
			if (this.isIndexAvailable()) {
				len = this.getIndexDimensionLength();
			} else {
				if (X_COORDINATE.equals(columnType)) {
					len = this.getXDimensionLength();
				} else {
					len = this.getYDimensionLength();
				}
			}
			ret = SGIntegerSeriesSet.createInstance(len);
		}
		return ret;
	}

	@Override
	public void setDataViewerValue(final String columnType, final int row, 
			final int col, final Object value) {
		SGDataValueHistory editedValue = SGDataUtility.setDataViewerValue(this,
				columnType, row, col, value);
		if (editedValue != null) {
			this.mEditedDataValueList.add(editedValue);
		}
	}

	@Override
    protected List<Dimension> getToolTipNotSpatiallyVariedDimensionList() {
    	Set<Dimension> dimSet = new HashSet<Dimension>();
    	dimSet.addAll(this.mXVariable.getDimensions());
    	dimSet.addAll(this.mYVariable.getDimensions());
    	dimSet.addAll(this.mFirstComponentVariable.getDimensions());
    	dimSet.addAll(this.mSecondComponentVariable.getDimensions());
    	if (this.isIndexAvailable()) {
        	dimSet.remove(this.mIndexVariable.getDimension(0));
    	} else {
        	dimSet.remove(this.mXVariable.getDimension(0));
        	dimSet.remove(this.mYVariable.getDimension(0));
    	}
    	return new ArrayList<Dimension>(dimSet);
    }

    public SGNetCDFVariable getFirstComponentVariable() {
    	return this.mFirstComponentVariable;
    }

    public SGNetCDFVariable getSecondComponentVariable() {
    	return this.mSecondComponentVariable;
    }

	@Override
    public String getToolTipSpatiallyVaried(final int index) {
		return this.getIndexToolTipSpatiallyVaried(index);
	}

	@Override
    public String getToolTipSpatiallyVaried(final int index0, final int index1) {
		return this.getXYToolTipSpatiallyVaried(index0, index1);
    }

    /**
     * Returns a text string of the unit for X values.
     * 
     * @return a text string of the unit for X values
     */
	@Override
    public String getUnitsStringX() {
		return this.mXVariable.getUnitsString();
	}

    /**
     * Returns a text string of the unit for Y values.
     * 
     * @return a text string of the unit for Y values
     */
	@Override
    public String getUnitsStringY() {
		return this.mYVariable.getUnitsString();
	}

	@Override
	public void setDataValue(SGDataValueHistory value) {
		SGDataUtility.setDataViewerValue(this, value.getColumnType(), 
				value.getRowIndex(), value.getColumnIndex(), 
				value.getValue());
	}

	@Override
	public void restoreCache() {
		final boolean all = false;
		final boolean useCache = true;
    	this.getXValueArray(all, useCache);
    	this.getYValueArray(all, useCache);
    	if (this.isIndexAvailable()) {
        	this.getFirstComponentValueArray(all, useCache);
        	this.getSecondComponentValueArray(all, useCache);
    	} else {
        	this.getFirstComponentValueBlockListSub(all, useCache, true);
        	this.getSecondComponentValueBlockListSub(all, useCache, true);
    	}
    }

	@Override
	public SGDataValue getDataValue(final int index) {
		return SGDataUtility.getDataValue(this, index);
	}

	@Override
	public SGDataValue getDataValue(final int xIndex, final int yIndex) {
		return SGDataUtility.getDataValue(this, xIndex, yIndex);
	}

	@Override
	public double[] getXValueArray(boolean all, boolean useCache) {
		return this.getXValueArray(all, useCache, true);
	}

	@Override
	public double[] getYValueArray(boolean all, boolean useCache) {
		return this.getYValueArray(all, useCache, true);
	}

	@Override
	public double[] getFirstComponentValueArray(boolean all, boolean useCache) {
		return this.getFirstComponentValueArray(all, useCache, true);
	}

	@Override
	public double[] getSecondComponentValueArray(boolean all, boolean useCache) {
		return this.getSecondComponentValueArray(all, useCache, true);
	}

	@Override
	protected boolean matches(final int col, final int row, String columnType,
			SGDataValueHistory value, final double d) {
		return SGDataUtility.matches(col, row, columnType,
				value, d);
	}

	@Override
    protected Array setEditedValues(NetcdfFileWriteable ncWrite,
    		String varName, Array array, final boolean all) {
		final boolean polar = this.isPolar();
		String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
		String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
		return this.setEditedValues(ncWrite, varName, array, X_COORDINATE, Y_COORDINATE,
				new String[] { first, second }, all);
    }

	@Override
    public SGTwoDimensionalArrayIndex getDataViewerCell(SGTwoDimensionalArrayIndex cell,
    		String columnType, final boolean bStride) {
		SGTwoDimensionalArrayIndex ret = null;
		if (this.isIndexAvailable()) {
			ret = super.getDataViewerCell(cell, columnType, bStride);
		} else {
			final boolean polar = this.isPolar();
			String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
			String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
			if (first.equals(columnType) || second.equals(columnType)) {
				ret = super.getDataViewerCell(cell, columnType, bStride);
			} else if (X_COORDINATE.equals(columnType)) {
				ret = this.getDataViewerCellX(cell, bStride);
			} else if (Y_COORDINATE.equals(columnType)) {
				ret = this.getDataViewerCellY(cell, bStride);
			}
		}
    	return ret;
    }

	@Override
	public List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlockList(
			final boolean all, final boolean useCache, 
			final boolean removeInvalidValues) {
		return SGDataUtility.getFirstComponentValueBlockList(this, all, useCache, 
				removeInvalidValues);
	}

	@Override
	public List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlockList(
			final boolean all, final boolean useCache, 
			final boolean removeInvalidValues) {
		return SGDataUtility.getSecondComponentValueBlockList(this, all, useCache, 
				removeInvalidValues);
	}

}
