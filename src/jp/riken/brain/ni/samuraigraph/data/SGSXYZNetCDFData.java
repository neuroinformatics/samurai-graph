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

public class SGSXYZNetCDFData extends SGTwoDimensionalNetCDFData implements SGISXYZTypeData {

    /**
     * The variable for z-values.
     */
    protected SGNetCDFVariable mZVariable = null;

    /**
     * The default constructor.
     *
     */
    public SGSXYZNetCDFData() {
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
     *            information of x-values
     * @param yInfo
     *            information of y-values
     * @param zInfo
     *            information of z-values
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
	public SGSXYZNetCDFData(final SGNetCDFFile ncfile,
			SGDataSourceObserver obs, final SGNetCDFDataColumnInfo xInfo,
			final SGNetCDFDataColumnInfo yInfo,
			final SGNetCDFDataColumnInfo zInfo,
			final SGNetCDFDataColumnInfo timeInfo,
			final SGNetCDFDataColumnInfo indexInfo,
			final SGNetCDFDataColumnInfo xIndexInfo,
			final SGNetCDFDataColumnInfo yIndexInfo,
			final SGIntegerSeriesSet indexStride,
			final SGIntegerSeriesSet xStride, final SGIntegerSeriesSet yStride,
			final boolean strideAvailable) {

		super(ncfile, obs, xInfo, yInfo, timeInfo, indexInfo, xIndexInfo, yIndexInfo, indexStride,
				xStride, yStride, strideAvailable);

        if (zInfo == null) {
            throw new IllegalArgumentException("zInfo == null");
        }

        // get variables
        SGNetCDFVariable zVar = ncfile.findVariable(zInfo.getName());
        if (zVar == null) {
            throw new IllegalArgumentException("Illegal variable name: " + zInfo.getName());
        }

        // check variables
        this.checkNonCoordinateVariable(zVar, this.getXDimension());
        this.checkNonCoordinateVariable(zVar, this.getYDimension());

        // set to attributes
        this.mZVariable = zVar;
	}

    /**
     * Disposes of this data object.
     * Do not call any method of this object after called this method.
     */
    public void dispose() {
        super.dispose();
        this.mZVariable = null;
    }

    /**
     * Returns an array of current column types.
     *
     * @return an array of current column types
     */
    public String[] getCurrentColumnType() {
        List<SGNetCDFVariable> varList = this.getNetcdfFile().getVariables();
        final int varNum = varList.size();
        String[] array = new String[varNum];
        for (int ii = 0; ii < varNum; ii++) {
        	SGNetCDFVariable var = varList.get(ii);
            if (var.equals(this.mXVariable)) {
                array[ii] = X_VALUE;
            } else if (var.equals(this.mYVariable)) {
                array[ii] = Y_VALUE;
            } else if (var.equals(this.mZVariable)) {
                array[ii] = Z_VALUE;
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
     * Returns an array of variables that are assigned the column type.
     *
     * @return an array of variables
     */
    @Override
    public SGNetCDFVariable[] getAssignedVariables() {
    	List<SGNetCDFVariable> varList = new ArrayList<SGNetCDFVariable>();
    	varList.add(this.mXVariable);
    	varList.add(this.mYVariable);
    	varList.add(this.mZVariable);
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

    public SGNetCDFVariable getZVariable() {
    	return this.mZVariable;
    }

    /**
     * Sets the type of data columns.
     *
     * @param column
     *              an array of column types
     * @return true if succeeded
     */
    public boolean setColumnType(String[] columns) {
        List<SGNetCDFVariable> xVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> yVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> zVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> timeVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> indexVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> xIndexVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> yIndexVarList = new ArrayList<SGNetCDFVariable>();

        List<SGNetCDFVariable> varList = this.getNetcdfFile().getVariables();
        for (int ii = 0; ii < columns.length; ii++) {
        	SGNetCDFVariable var = varList.get(ii);
        	String valueType = var.getValueType();
            if (SGDataUtility.isEqualColumnType(X_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                xVarList.add(var);
            } else if (SGDataUtility.isEqualColumnType(Y_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                yVarList.add(var);
            } else if (SGDataUtility.isEqualColumnType(Z_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                zVarList.add(var);
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
    	if (xVarList.size() != 1 || yVarList.size() != 1 || zVarList.size() != 1) {
    		return false;
    	}
    	if (xIndexVarList.size() > 1 || yIndexVarList.size() > 1) {
    		return false;
    	}

    	SGNetCDFVariable xVar = xVarList.get(0);
    	SGNetCDFVariable yVar = yVarList.get(0);
    	SGNetCDFVariable zVar = zVarList.get(0);

    	// index variable
    	SGNetCDFVariable indexVar = (indexVarList.size() == 1) ? indexVarList.get(0) : null;
    	SGNetCDFVariable xIndexVar = (xIndexVarList.size() == 1) ? xIndexVarList.get(0) : null;
    	SGNetCDFVariable yIndexVar = (yIndexVarList.size() == 1) ? yIndexVarList.get(0) : null;

        // time variable
    	SGNetCDFVariable timeVar = (timeVarList.size() == 1) ? timeVarList.get(0) : null;

        // set variables
        this.mXVariable = xVar;
        this.mYVariable = yVar;
        this.mZVariable = zVar;
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
        if (!(data instanceof SGSXYZNetCDFData)) {
            throw new IllegalArgumentException("Given data is not the instance of SGSXYZNetCDFData class.");
        }
        if (super.setData(data) == false) {
            return false;
        }
        SGSXYZNetCDFData nData = (SGSXYZNetCDFData) data;
        this.mZVariable = nData.mZVariable;
        return true;
    }

    /**
     * Returns the properties of this data.
     *
     * @return the properties of this data
     */
    public SGProperties getProperties() {
        SGProperties p = new SXYZNetCDFDataProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    /**
     * Gets the properties of this data.
     * @param p
     *      the properties of this data
     * @return
     *      true if succeeded
     */
    public boolean getProperties(SGProperties p) {
        if (!(p instanceof SXYZNetCDFDataProperties)) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        SXYZNetCDFDataProperties sp = (SXYZNetCDFDataProperties) p;
        sp.zName = this.mZVariable.getName();
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
        if (!(p instanceof SXYZNetCDFDataProperties)) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        SXYZNetCDFDataProperties sp = (SXYZNetCDFDataProperties) p;
        this.mZVariable = this.getNetcdfFile().findVariable(sp.zName);
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
        el.setAttribute(KEY_X_VALUE_NAME, this.mXVariable.getValidName());
        el.setAttribute(KEY_Y_VALUE_NAME, this.mYVariable.getValidName());
        el.setAttribute(KEY_Z_VALUE_NAME, this.mZVariable.getValidName());
        return true;
    }

    /**
     * Returns a text string of data type.
     *
     * @return a text string of data type
     */
    public String getDataType() {
        return SGDataTypeConstants.SXYZ_NETCDF_DATA;
    }

    /**
     * A class of scalar XYZ NetCDF data properties.
     */
    public static class SXYZNetCDFDataProperties extends TwoDimensionalNetCDFDataProperties {

        protected String zName = null;

        /**
         * The default constructor.
         */
        public SXYZNetCDFDataProperties() {
            super();
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.zName = null;
        }

    	@Override
    	public boolean equals(Object obj) {
            if ((obj instanceof SXYZNetCDFDataProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            return true;
        }

    	@Override
        public boolean hasEqualSize(DataProperties dp) {
            if ((dp instanceof SXYZNetCDFDataProperties) == false) {
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
            if ((dp instanceof SXYZNetCDFDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualColumnTypes(dp)) {
            	return false;
            }
            SXYZNetCDFDataProperties p = (SXYZNetCDFDataProperties) dp;
            if (SGUtility.equals(this.zName, p.zName) == false) {
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
        	SXYZNetCDFDataProperties p = (SXYZNetCDFDataProperties) super.copy();
        	return p;
        }
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
    		List<SGXYSimpleDoubleValueIndexBlock> blocks = this.getZValueBlockList();
    		int num = 0;
    		for (SGXYSimpleDoubleValueIndexBlock block : blocks) {
    			num += block.getLength();
    		}
    		return num;
        }
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
	public boolean useXValueCache(final boolean all) {
		return this.useXYCache(all, this.mXStride);
	}

	@Override
	public double[] getXValueArray(final boolean all, final boolean useCache,
			final boolean removeInvalidValues) {
		double[] ret = this.getXYValueArray(all, this.mXVariable, this.mYVariable, 
				this.getXStrideInstance(), removeInvalidValues);
    	if (useCache) {
            SGSXYZDataCache.setXValues(this, ret);
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
	public boolean useYValueCache(final boolean all) {
		return this.useXYCache(all, this.mYStride);
	}

	@Override
	public double[] getYValueArray(final boolean all, final boolean useCache,
			final boolean removeInvalidValues) {
		double[] ret = this.getXYValueArray(all, this.mYVariable, this.mXVariable, 
				this.getYStrideInstance(), removeInvalidValues);
    	if (useCache) {
            SGSXYZDataCache.setYValues(this, ret);
    	}
		return ret;
	}

    /**
     * Returns an array of Z-values.
     *
     * @return an array of Z-values
     */
	@Override
    public double[] getZValueArray(final boolean all) {
        return SGDataUtility.getZValueArray(this, all);
    }

	@Override
	public boolean useZValueCache(final boolean all) {
		return this.useComponentIndexCache(all);
	}

	@Override
	public double[] getZValueArray(final boolean all, final boolean useCache,
			final boolean removeInvalidValues) {
		double[] ret = null;
        if (this.isIndexAvailable()) {
            ret = this.getValueArray(this.mZVariable, 
            		new SGNetCDFVariable[]{ this.mIndexVariable }, all, removeInvalidValues);
        } else {
        	SGNetCDFVariable[] cVars = this.getCoordinateVariables();
            ret = this.getValueArray(this.mZVariable, cVars, all, removeInvalidValues);
        }
    	if (useCache) {
            SGSXYZDataCache.setZValues(this, ret);
    	}
    	return ret;
	}

    /**
     * Returns the list of blocks of z-values.
     *
     * @return the list of blocks of z-values
     */
	@Override
    public List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockList() {
		return SGDataUtility.getZValueBlockList(this, false, true, true);
	}
	
	@Override
	public List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockListSub(
			final boolean all, final boolean useCache, 
			final boolean removeInvalidValues) {
		List<SGXYSimpleDoubleValueIndexBlock> ret = this.getValueBlockList(
				this.mZVariable, all, removeInvalidValues);
		if (useCache) {
	        SGSXYZDataCache.setZValueBlockList(this, ret);
		}
		return ret;
	}
	
    /**
     * Returns a scatter type array of Z-values.
     *
     * @param all
     *            true to get all values
     * @return a scatter array of Z-values
     */
    protected double[] getScatterZValueArray(final boolean all) {
		return this.getZValueArray(all);
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
     * Returns the bounds of z-values.
     *
     * @return the bounds of z-values
     */
    public SGValueRange getBoundsZ() {
        return SGDataUtility.getBoundsZ(this);
    }

    /**
     * Returns the title for the Y-axis.
     *
     * @return the title for the Y-axis
     */
    public String getTitleZ() {
        return this.getNameWithUnit(this.mZVariable);
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
            map.put(SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X, this.getXStride());
            map.put(SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y, this.getYStride());
    	} else {
        	map.put(SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE,
        			this.getIndexStride());
    	}
    	return map;
    }

    /**
     * Sets the stride of data arrays.
     *
     * @param map
     *           the map of the stride
     */
    public void setStrideMap(Map<String, SGIntegerSeriesSet> map) {
    	this.mIndexStride = map.get(SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE);
    	this.mXStride = map.get(SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X);
    	this.mYStride = map.get(SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y);
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

		if (this.isIndexAvailable()) {
			double[] zValues = this.getZValueArray(all, false, remove);
			
			// set edited values
			if (edit) {
				for (SGDataValueHistory dataValue : this.mEditedDataValueList) {
					SGDataUtility.setEditedValue(this, xValues, yValues, zValues, 
							all, dataValue);
				}
			}
			
			return new SGSXYZDataBuffer(xValues, yValues, zValues);
			
		} else {
			double[] zValues = this.getZValueArray(all, false, remove);
	    	double[][] zGridValues = this.getGridValueArray(zValues, all);
			
			// set edited values
			if (edit) {
				for (SGDataValueHistory dataValue : this.mEditedDataValueList) {
					SGDataUtility.setEditedValue(this, xValues, yValues, zGridValues, 
							all, dataValue);
				}
			}
			
			return new SGSXYZGridDataBuffer(xValues, yValues, zGridValues);
		}
	}

	@Override
	protected boolean exportToFile(NetcdfFileWriteable ncWrite, 
			final SGExportParameter mode, SGDataBufferPolicy policy)
			throws IOException, InvalidRangeException {

		DataType xDataType = this.getExportNumberDataType(this.mXVariable, mode, policy);
		DataType yDataType = this.getExportNumberDataType(this.mYVariable, mode, policy);
		DataType zDataType = this.getExportNumberDataType(this.mZVariable, mode, policy);
		
		if (this.isIndexAvailable()) {
			SGSXYZDataBuffer buffer = (SGSXYZDataBuffer) this.getDataBuffer(policy);

			final boolean all = policy.isAllValuesGotten();
			final int len = all ? this.getIndexDimensionLength() : this.mIndexStride.getLength();
			
			// adds dimensions
			String indexDimName = this.mIndexVariable.getValidName();
			this.addDimension(ncWrite, indexDimName, len);
			
			String xName = this.mXVariable.getValidName();
			String yName = this.mYVariable.getValidName();
			String zName = this.mZVariable.getValidName();

			// adds variables
			Variable indexVar = this.addVariable(ncWrite, mode, indexDimName, DataType.INT, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, indexDimName, policy);
			Variable xVar = this.addVariable(ncWrite, mode, xName, xDataType, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, indexDimName, policy);
			Variable yVar = this.addVariable(ncWrite, mode, yName, yDataType, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, indexDimName, policy);
			Variable zVar = this.addVariable(ncWrite, mode, zName, zDataType, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, indexDimName, policy);
			
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
	
			double[] zValues = buffer.getZValues();
			Array zArray = Array.factory(zDataType, new int[] { len });
			this.setArray(zArray, zValues);
			this.writeValues(ncWrite, zVar, zArray);

		} else {

			SGSXYZGridDataBuffer buffer = (SGSXYZGridDataBuffer) this.getDataBuffer(policy);
			
			final int xLen = buffer.getLengthX();
			final int yLen = buffer.getLengthY();

			String xName = this.mXVariable.getValidName();
			String yName = this.mYVariable.getValidName();
			String zName = this.mZVariable.getValidName();

			// adds dimensions
			this.addDimension(ncWrite, xName, xLen);
			this.addDimension(ncWrite, yName, yLen);

			// adds variables
			Variable xVar = this.addVariable(ncWrite, mode, xName, xDataType, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, xName, policy);
			Variable yVar = this.addVariable(ncWrite, mode, yName, yDataType, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, yName, policy);
			String zDimString = SGDataUtility.getDimensionString(new String[] { xName, yName });
			Variable zVar = this.addVariable(ncWrite, mode, zName, zDataType, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, zDimString, policy);

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

			List<SGXYSimpleDoubleValueIndexBlock> blocks = buffer.getZValueBlocks();
			List<Integer> xIndexList = new ArrayList<Integer>();
			List<Integer> yIndexList = new ArrayList<Integer>();
			this.getIndexList(blocks, xIndexList, yIndexList);
			double[][] values = SGDataUtility.getTwoDimensionalValues(blocks, xIndexList, yIndexList);
			values = SGUtility.transpose(values);
			Array array = Array.factory(zDataType, new int[] { xLen, yLen });
			this.setArray(array, values);
			this.writeValues(ncWrite, zVar, array);
		}
		
		return true;
	}

	@Override
    protected void getVarNameColumnTypeList(List<String> varList, 
    		List<String> columnTypeList) {
    	
    	super.getVarNameColumnTypeList(varList, columnTypeList);
    	
		String strX = this.mXVariable.getName();
		varList.add(strX);
		columnTypeList.add(X_VALUE);
		
		String strY = this.mYVariable.getName();
		varList.add(strY);
		columnTypeList.add(Y_VALUE);
		
		String strZ = this.mZVariable.getName();
		varList.add(strZ);
		columnTypeList.add(Z_VALUE);
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
    	this.mZVariable = this.findVariable(ncfile, this.mZVariable);
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
     * Returns the bounds of z-values for all animation frames.
     * 
     * @return the bounds of z-values
     */
	@Override
    public SGValueRange getAllAnimationFrameBoundsZ() {
		return SGDataUtility.getAllAnimationFrameBoundsZ(this);
	}

    /**
     * Returns the array of column types.
     * 
     * @return the array of column types
     */
	@Override
    public String[] getDataViewerColumnTypes() {
    	List<String> list = new ArrayList<String>();
		list.add(SGIDataColumnTypeConstants.X_VALUE);
		list.add(SGIDataColumnTypeConstants.Y_VALUE);
		list.add(SGIDataColumnTypeConstants.Z_VALUE);
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
	public Double getDataViewerValue(String columnType, final int row, final int col) {
		return SGDataUtility.getDataViewerValue(this, columnType, row, col);
	}

	@Override
	public int getDataViewerColumnNumber(final String columnType, final boolean all) {
		int ret = 0;
		if (this.isIndexAvailable()) {
			ret = 1;
		} else {
			if (SGIDataColumnTypeConstants.X_VALUE.equals(columnType)
					|| SGIDataColumnTypeConstants.Y_VALUE.equals(columnType)) {
				ret = 1;
			} else if (SGIDataColumnTypeConstants.Z_VALUE.equals(columnType)) {
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
			if (SGIDataColumnTypeConstants.X_VALUE.equals(columnType)) {
				if (all) {
					ret = this.mXVariable.getDimension(0).getLength();
				} else {
					ret = this.mXStride.getLength();
				}
			} else if (SGIDataColumnTypeConstants.Y_VALUE.equals(columnType)) {
				if (all) {
					ret = this.mYVariable.getDimension(0).getLength();
				} else {
					ret = this.mYStride.getLength();
				}
			} else if (SGIDataColumnTypeConstants.Z_VALUE.equals(columnType)) {
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
		/*
		if (this.isNaNAssignedInvalidValue(0, index, X_VALUE, d)) {
			SGNetCDFVariable cVar = this.isIndexAvailable() ? this.mIndexVariable : this.mXVariable;
            return this.getValueAt(this.mXVariable, 
            		new SGNetCDFVariable[]{ cVar }, new int[] { index });
		} else {
			return d;
		}
		*/
		return d;
	}
    
	@Override
    public Double getYValueAt(final int index) {
		double[] values = this.getYValueArray(false);
		final double d = values[index];
		/*
		if (this.isNaNAssignedInvalidValue(0, index, Y_VALUE, d)) {
			SGNetCDFVariable cVar = this.isIndexAvailable() ? this.mIndexVariable : this.mYVariable;
            return this.getValueAt(this.mYVariable, 
            		new SGNetCDFVariable[]{ cVar }, new int[] { index });
		} else {
			return d;
		}
		*/
		return d;
	}

	@Override
    public Double getZValueAt(final int index) {
		if (!this.isIndexAvailable()) {
			throw new Error("Unsupported.");
		}
		double[] values = this.getZValueArray(false);
		final double d = values[index];
		return d;
	}

	@Override
    public Double getZValueAt(final int row, final int col) {
		if (this.isIndexAvailable()) {
			throw new Error("Unsupported.");
		}
		List<SGXYSimpleDoubleValueIndexBlock> blockList = this.getZValueBlockList();
		Double d = SGXYSimpleDoubleValueIndexBlock.getDataViewerValue(blockList, row, col);
		return d;
	}

	@Override
    public SGIntegerSeriesSet getDataViewerColStride(String columnType) {
		SGIntegerSeriesSet ret = null;
		if (!this.isIndexAvailable() && Z_VALUE.equals(columnType)) {
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
				if (X_VALUE.equals(columnType)) {
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
				if (X_VALUE.equals(columnType)) {
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
	public void setDataViewerValue(final String columnType, final int row, final int col,
			final Object value) {
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
    	dimSet.addAll(this.mZVariable.getDimensions());
    	if (this.isIndexAvailable()) {
        	dimSet.remove(this.mIndexVariable.getDimension(0));
    	} else {
        	dimSet.remove(this.mXVariable.getDimension(0));
        	dimSet.remove(this.mYVariable.getDimension(0));
    	}
    	return new ArrayList<Dimension>(dimSet);
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
        	this.getZValueArray(all, useCache);
    	} else {
        	this.getZValueBlockListSub(all, useCache, true);
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
	public double[] getZValueArray(boolean all, boolean useCache) {
		return this.getZValueArray(all, useCache, true);
	}

	@Override
	protected boolean matches(final int col, final int row, String columnType,
			SGDataValueHistory value, final double d) {
		return SGDataUtility.matches(col, row, columnType, value, d);
	}

	@Override
    protected Array setEditedValues(NetcdfFileWriteable ncWrite,
    		String varName, Array array, final boolean all) {
		return this.setEditedValues(ncWrite, varName, array, X_VALUE, Y_VALUE,
				new String[] { Z_VALUE }, all);
    }

	@Override
    public SGTwoDimensionalArrayIndex getDataViewerCell(SGTwoDimensionalArrayIndex cell,
    		String columnType, final boolean bStride) {
		SGTwoDimensionalArrayIndex ret = null;
		if (this.isIndexAvailable()) {
			ret = super.getDataViewerCell(cell, columnType, bStride);
		} else {
			if (Z_VALUE.equals(columnType)) {
				ret = super.getDataViewerCell(cell, columnType, bStride);
			} else if (X_VALUE.equals(columnType)) {
				ret = this.getDataViewerCellX(cell, bStride);
			} else if (Y_VALUE.equals(columnType)) {
				ret = this.getDataViewerCellY(cell, bStride);
			}
		}
    	return ret;
    }

	@Override
	public List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockList(
			final boolean all, final boolean useCache, 
			final boolean removeInvalidValues) {
		return SGDataUtility.getZValueBlockList(this, all, useCache, removeInvalidValues);
	}

}
