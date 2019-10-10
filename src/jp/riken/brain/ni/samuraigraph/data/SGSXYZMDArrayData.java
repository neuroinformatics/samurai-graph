package jp.riken.brain.ni.samuraigraph.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable.MDArrayDataType;

import org.w3c.dom.Element;

import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ch.systemsx.cisd.base.mdarray.MDDoubleArray;
import ch.systemsx.cisd.hdf5.IHDF5Writer;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

/**
 *
 * The class of scalar XYZ type data for multidimensional data file.
 *
 */
public class SGSXYZMDArrayData extends SGTwoDimensionalMDArrayData implements SGISXYZTypeData, SGIMDArrayConstants,
		SGIDataPropertyKeyConstants {

    /**
     * The variable for z-values.
     */
    protected SGMDArrayVariable mZVariable = null;

    /**
     * The default constructor.
     */
	public SGSXYZMDArrayData() {
		super();
	}

	/**
     * Builds a data object.
	 *
     * @param mdFile
     *           multidimensional data file
     * @param obs
     *           data observer
     * @param xInfo
     *           information of x values
     * @param yInfo
     *           information of y values
     * @param zInfo
     *           information of z values
     * @param xStride
     *           stride for x values
     * @param yStride
     *           stride for y values
     * @param scatterStride
     *           stride for scatter plot
     * @param strideAvailable
     *           flag whether to set available the stride
	 */
	public SGSXYZMDArrayData(final SGMDArrayFile mdFile, final SGDataSourceObserver obs,
			final SGMDArrayDataColumnInfo xInfo, final SGMDArrayDataColumnInfo yInfo,
			final SGMDArrayDataColumnInfo zInfo, final SGIntegerSeriesSet xStride,
			final SGIntegerSeriesSet yStride, final SGIntegerSeriesSet scatterStride,
			final boolean strideAvailable) {

		super(mdFile, obs, scatterStride, strideAvailable);
		
        if (zInfo == null) {
            throw new IllegalArgumentException("zInfo == null");
        }

        final int[] zDims = zInfo.getDimensions();
        
		if (scatterStride != null) {
			// scatter plot
	        if (xInfo == null) {
	            throw new IllegalArgumentException("xInfo == null");
	        }
	        if (yInfo == null) {
	            throw new IllegalArgumentException("yInfo == null");
	        }
	        
            final int xLen = xInfo.getGenericDimensionLength();
            if (xLen == -1) {
            	throw new IllegalArgumentException("Invalid length: " + xLen);
            }
            final int yLen = yInfo.getGenericDimensionLength();
            if (yLen == -1) {
            	throw new IllegalArgumentException("Invalid length: " + yLen);
            }
            final int zLen = zInfo.getGenericDimensionLength();
            if (zLen == -1) {
            	throw new IllegalArgumentException("Invalid length: " + zLen);
            }
            if (zLen != xLen) {
            	throw new IllegalArgumentException("Length of dimension for x values is invalid: "
            			+ zLen + " != " + xLen);
            }
            if (zLen != yLen) {
            	throw new IllegalArgumentException("Length of dimension for y values is invalid: "
            			+ zLen + " != " + yLen);
            }

	        // finds variables
	        SGMDArrayVariable xVar = this.initVariable(mdFile, xInfo);
	        SGMDArrayVariable yVar = this.initVariable(mdFile, yInfo);
	        SGMDArrayVariable zVar = this.initVariable(mdFile, zInfo);

	        // sets to the attributes
	        this.mXVariable = xVar;
	        this.mYVariable = yVar;
	        this.mZVariable = zVar;
	        this.initTimeStride();

		} else {
			// grid plot
	        final Integer xDim = zInfo.getDimensionIndex(KEY_SXYZ_X_DIMENSION);
	        if (xDim == null) {
	        	throw new IllegalArgumentException("Failed to get the dimension for x values.");
	        }
	        final Integer yDim = zInfo.getDimensionIndex(KEY_SXYZ_Y_DIMENSION);
	        if (yDim == null) {
	        	throw new IllegalArgumentException("Failed to get the dimension for y values.");
	        }
	        if (xInfo != null) {
	            final int xLen = xInfo.getGenericDimensionLength();
	            if (zDims[xDim] != xLen) {
	            	throw new IllegalArgumentException("Length of dimension for x values is invalid: "
	            			+ zDims[xDim] + " != " + xLen);
	            }
	        }
	        if (yInfo != null) {
	            final int yLen = yInfo.getGenericDimensionLength();
	            if (zDims[yDim] != yLen) {
	            	throw new IllegalArgumentException("Length of dimension for y values is invalid: "
	            			+ zDims[yDim] + " != " + yLen);
	            }
	        }
	        final int xLength = zDims[xDim];
	        final int yLength = zDims[yDim];

	        // finds variables
	        SGMDArrayVariable xVar = this.initVariable(mdFile, xInfo);
	        SGMDArrayVariable yVar = this.initVariable(mdFile, yInfo);
	        SGMDArrayVariable zVar = this.initVariable(mdFile, zInfo);

	        // sets to the attributes
	        this.mXVariable = xVar;
	        this.mYVariable = yVar;
	        this.mZVariable = zVar;
	        this.mXStride = this.createStride(xStride, xLength);
	        this.mYStride = this.createStride(yStride, yLength);
	        this.initTimeStride();
		}
	}

    protected SGMDArrayVariable initVariable(SGMDArrayFile file, SGMDArrayDataColumnInfo info) {
    	SGMDArrayVariable var = super.initVariable(file, info);
    	if (info != null) {
        	Integer xDim = info.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION);
        	Integer xValue = null;
        	if (xDim != null) {
        		xValue = xDim;
        	} else {
        		xValue = 0;
        	}
        	var.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, xValue);

        	Integer yDim = info.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION);
        	Integer yValue = null;
        	if (yDim != null) {
        		yValue = yDim;
        	} else {
        		yValue = 0;
        	}
        	var.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, yValue);
        	
        	Integer zDim = info.getGenericDimensionIndex();
        	Integer zValue = null;
        	if (zDim != null) {
        		zValue = zDim;
        	} else {
        		zValue = 0;
        	}
        	var.setGenericDimensionIndex(zValue);
    	}
    	return var;
    }

    protected SGMDArrayVariable initVariable(SGMDArrayVariable var) {
    	super.initVariable(var);
    	var.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, -1);
    	var.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, -1);
    	return var;
    }

    /**
     * Disposes of this data object.
     *
     */
	@Override
	public void dispose() {
		super.dispose();
		this.mZVariable = null;
	}

    /**
     * Returns a text string of data type.
     *
     * @return a text string of data type
     */
	@Override
	public String getDataType() {
		SGIDataSource src = this.getDataSource();
		if (src instanceof SGHDF5File) {
	        return SGDataTypeConstants.SXYZ_HDF5_DATA;
		} else if (src instanceof SGMATLABFile) {
	        return SGDataTypeConstants.SXYZ_MATLAB_DATA;
		} else if (src instanceof SGVirtualMDArrayFile) {
	        return SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA;
		} else {
			throw new Error("This cannot happen.");
		}
	}

    /**
     * Returns the number of data points taking into account the stride.
     *
     * @return the number of data points taking into account the stride
     */
	@Override
	public int getPointsNumber() {
		if (this.isStrideAvailable()) {
			if (this.isIndexAvailable()) {
				return this.mIndexStride.getLength();
			} else {
				List<SGXYSimpleDoubleValueIndexBlock> blocks = this.getZValueBlockList();
				int num = 0;
				for (SGXYSimpleDoubleValueIndexBlock block : blocks) {
					num += block.getLength();
				}
				return num;
			}
		} else {
			return this.getAllPointsNumber();
		}
	}

    /**
     * Returns the bounds of x-values.
     *
     * @return the bounds of x-values
     */
	@Override
	public SGValueRange getBoundsX() {
		return SGDataUtility.getBoundsX(this);
	}

    /**
     * Returns the bounds of y-values.
     *
     * @return the bounds of y-values
     */
	@Override
	public SGValueRange getBoundsY() {
		return SGDataUtility.getBoundsY(this);
	}

    /**
     * Returns the bounds of z-values.
     *
     * @return the bounds of z-values
     */
	@Override
	public SGValueRange getBoundsZ() {
		return SGDataUtility.getBoundsZ(this);
	}

    /**
     * Returns the title for the Z-axis.
     *
     * @return the title for the Z-axis
     */
	@Override
	public String getTitleZ() {
		return this.mZVariable.getSimpleName();
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
	public double[] getZValueArray(final boolean all, final boolean useCache) {
		double[] ret = this.getValueArray(this.mZVariable, all, 
				KEY_SXYZ_X_DIMENSION, KEY_SXYZ_Y_DIMENSION);
    	if (useCache) {
            SGSXYZDataCache.setZValues(this, ret);
    	}
    	return ret;
	}

    /**
     * Returns a grid type array of Z-values.
     *
     * @param type
     *           the type of export
     * @param all
     *            true to get all values
     * @return a grid type array of Z-values
     */
    public double[][] getGridZValueArray(EXPORT_TYPE  type, final boolean all) {
		double[] values = this.getZValueArray(all);
    	return this.getTwoDimensionalValueArray(values, this.mZVariable, type, all);
    }

    /**
     * Returns a scatter type array of Z-values.
     *
     * @param all
     *            true to get all values
     * @return a scatter array of Z-values
     */
    public double[] getScatterZValueArray(EXPORT_TYPE  type, final boolean all) {
		return this.getZValueArray(all);
    }

    /**
     * Returns an array of current column types.
     *
     * @return
     *        an array of current column types
     */
	@Override
	public String[] getCurrentColumnType() {
        SGMDArrayVariable[] vars = this.getVariables();
        String[] array = new String[vars.length];
        for (int ii = 0; ii < array.length; ii++) {
        	SGMDArrayVariable var = vars[ii];
            if (var.equals(this.mXVariable)) {
                array[ii] = X_VALUE;
            } else if (var.equals(this.mYVariable)) {
                array[ii] = Y_VALUE;
            } else if (var.equals(this.mZVariable)) {
                array[ii] = Z_VALUE;
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
	@Override
	public boolean setColumnType(String[] columns) {
    	SGMDArrayVariable xVar = null;
    	SGMDArrayVariable yVar = null;
    	SGMDArrayVariable zVar = null;

        // set variables
        SGMDArrayVariable[] vars = this.getVariables();
        for (int ii = 0; ii < columns.length; ii++) {
        	SGMDArrayVariable var = vars[ii];
        	String valueType = var.getValueType();
            if (SGDataUtility.isEqualColumnType(X_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                xVar = var;
            } else if (SGDataUtility.isEqualColumnType(Y_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                yVar = var;
            } else if (SGDataUtility.isEqualColumnType(Z_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                zVar = var;
            } else if ("".equals(columns[ii])) {
            	continue;
            } else {
            	return false;
            }
        }
        if (zVar == null) {
        	return false;
        }

        // set variables
        this.mXVariable = xVar;
        this.mYVariable = yVar;
        this.mZVariable = zVar;

        return true;
	}

    /**
     * A class of scalar XYZ multidimensional data properties.
     */
    public static class SXYZMDDataProperties extends TwoDimensionalMDArrayDataProperties {

        protected String zName = null;

        /**
         * The default constructor.
         */
        public SXYZMDDataProperties() {
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
            if ((obj instanceof SXYZMDDataProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
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
            if ((dp instanceof SXYZMDDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualColumnTypes(dp)) {
            	return false;
            }
            SXYZMDDataProperties p = (SXYZMDDataProperties) dp;
            if (SGUtility.equals(this.zName, p.zName) == false) {
            	return false;
            }
        	return true;
        }
    }

    /**
     * Returns the properties of this data.
     *
     * @return the properties of this data
     */
	@Override
	public SGProperties getProperties() {
        SGProperties p = new SXYZMDDataProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
	}

    /**
     * Returns the properties of this data.
     *
     * @param p
     *      the properties of this data
     * @return true if succeeded
     */
	@Override
	public boolean getProperties(final SGProperties p) {
        if (!(p instanceof SXYZMDDataProperties)) {
            return false;
        }
        if (!super.getProperties(p)) {
        	return false;
        }
        SXYZMDDataProperties sp = (SXYZMDDataProperties) p;
        sp.zName = this.getName(this.mZVariable);
		return true;
	}

    /**
     * Sets the properties to this data.
     *
     * @param p
     *          properties to set
     * @return true if succeeded
     */
	@Override
	public boolean setProperties(final SGProperties p) {
        if (!(p instanceof SXYZMDDataProperties)) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        SXYZMDDataProperties sp = (SXYZMDDataProperties) p;
        this.mZVariable = this.findVariable(sp.zName);
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
    public boolean writeProperty(Element el, final SGExportParameter params) {
        if (super.writeProperty(el, params) == false) {
            return false;
        }

        OPERATION type = params.getType();
    	if (OPERATION.SAVE_TO_PROPERTY_FILE.equals(type)
    			|| SGDataUtility.isArchiveDataSetOperation(type)) {

            StringBuffer sb = new StringBuffer();
        	sb.append(this.mZVariable.getName());
        	sb.append(':');
        	if (this.isIndexAvailable()) {
        		// scatter plot
        		Integer index = this.mZVariable.getGenericDimensionIndex();
            	sb.append(index);
        	} else {
        		// grid plot
        		Integer xIndex = this.mZVariable.getDimensionIndex(KEY_SXYZ_X_DIMENSION);
        		Integer yIndex = this.mZVariable.getDimensionIndex(KEY_SXYZ_Y_DIMENSION);
            	sb.append(xIndex);
            	sb.append(':');
            	sb.append(yIndex);
        	}
            el.setAttribute(KEY_Z_VALUE_NAME, sb.toString());

    	} else if (OPERATION.SAVE_TO_DATA_SET_NETCDF.equals(type)) {
            el.setAttribute(KEY_Z_VALUE_NAME, this.mZVariable.getName());
    	}

        return true;
    }

    /**
     * Sets the data.
     *
     * @param data
     *             data set to this object
     * @return true if succeeded
     */
    public boolean setData(SGData data) {
        if (!(data instanceof SGSXYZMDArrayData)) {
            throw new IllegalArgumentException("Given data is not the instance of SGSXYZMDData class.");
        }
        if (super.setData(data) == false) {
            return false;
        }
        SGSXYZMDArrayData nData = (SGSXYZMDArrayData) data;
        this.mZVariable = copyVariable(nData.mZVariable);
        return true;
    }

    /**
     * Returns the copy of this data object.
     *
     * @return a copy of this data object
     */
    public Object clone() {
    	SGSXYZMDArrayData data = (SGSXYZMDArrayData) super.clone();
        data.mZVariable = copyVariable(this.mZVariable);
        return data;
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
		Integer xIndex = this.mZVariable.getDimensionIndex(KEY_SXYZ_X_DIMENSION);
		Integer yIndex = this.mZVariable.getDimensionIndex(KEY_SXYZ_Y_DIMENSION);
		List<SGXYSimpleDoubleValueIndexBlock> ret = this.get2DValueBlockList(
				this.mZVariable, xIndex, yIndex, this.mXStride, this.mYStride,
				all);
        SGSXYZDataCache.setZValueBlockList(this, ret);
        return ret;
	}

	protected double[] getValueArraySub(final boolean all, final SGMDArrayVariable var, final SGIntegerSeriesSet stride,
			String name) {
		return this.getValueArraySub(all, var, stride, name, this.mZVariable);
	}

	protected int getAllPointsNumberSub(final SGMDArrayVariable var, String name) {
		return this.getAllPointsNumberSub(var, name, this.mZVariable);
	}

    /**
     * Sets the information of data columns.
     *
     * @param cols
     *            an array of column information
     * @return true if succeeded
     */
	public boolean setColumnType(SGDataColumnInfo[] cols) {

		// set dimensions
		if (this.setDimensionMap(cols) == false) {
			return false;
		}

		// set column types
		String[] columns = new String[cols.length];
		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii] = cols[ii].getColumnType();
		}
		if (this.setColumnType(columns) == false) {
			return false;
		}

		return true;
	}

	public SGMDArrayVariable getZVariable() {
		return this.mZVariable;
	}

    /**
     * Returns the map of dimension index that are used.
     *
     * @return the map of dimension index
     */
    public Map<String, Map<String, Integer>> getUsedDimensionIndexMap() {
    	Map<String, Map<String, Integer>> map = this.getDimensionIndexMap();
    	Map<String, Integer> dimMap = map.get(this.mZVariable.getName());
    	dimMap.remove(SGIMDArrayConstants.KEY_GENERIC_DIMENSION);
    	return map;
    }

    /**
     * Returns an array of variables that are assigned the column type.
     *
     * @return an array of variables
     */
	@Override
	public SGMDArrayVariable[] getAssignedVariables() {
		List<SGMDArrayVariable> varList = new ArrayList<SGMDArrayVariable>();
		if (this.mXVariable != null) {
			varList.add(this.mXVariable);
		}
		if (this.mYVariable != null) {
			varList.add(this.mYVariable);
		}
		if (this.mZVariable != null) {
			varList.add(this.mZVariable);
		}
		SGMDArrayVariable[] vars = new SGMDArrayVariable[varList.size()];
		return (SGMDArrayVariable[]) varList.toArray(vars);
	}

    /**
     * Adds variables to a netCDF file.
     *
     * @param ncWrite
     *           a netCDF file
     * @return true if succeeded
     */
	@Override
    protected boolean addVariables(NetcdfFileWriteable ncWrite) {

		// add time dimensions
		Dimension timeDim = this.addTimeVariable(ncWrite);

		if (this.isIndexAvailable()) {
			// scatter plot
			
			// index variable
			Dimension indexDim = this.addIndexCoordinateVariable(ncWrite);
			if (indexDim == null) {
				return false;
			}

			List<Dimension> dimList = new ArrayList<Dimension>();
			dimList.add(indexDim);

			// add x-variable
			if (!this.addVariable(ncWrite, this.mXVariable, dimList, timeDim)) {
				return false;
			}
			
			// add y-variable
			if (!this.addVariable(ncWrite, this.mYVariable, dimList, timeDim)) {
				return false;
			}

			// add z-variable
			if (!this.addVariable(ncWrite, this.mZVariable, dimList, timeDim)) {
				return false;
			}

		} else {
			// grid plot
			
			// add x-variable
			Dimension xDim = this.addGridXCoordinateVariable(ncWrite, timeDim);
			if (xDim == null) {
				return false;
			}

			// add y-variable
			Dimension yDim = this.addGridYCoordinateVariable(ncWrite, timeDim);
			if (yDim == null) {
				return false;
			}

			// add z-variable
			List<Dimension> dimList = new ArrayList<Dimension>();
			dimList.add(xDim);
			dimList.add(yDim);
			if (!this.addVariable(ncWrite, this.mZVariable, dimList, timeDim)) {
				return false;
			}
		}

		// add time variable
		if (timeDim != null) {
			if (!this.addSequentialIntegerNumberVariable(ncWrite, timeDim, TIME_DIM_NAME)) {
				return false;
			}
		}

    	return true;
    }
	
    /**
     * Writes data to a netCDF file.
     *
     * @param ncWrite
     *           a netCDF file
     * @return true if succeeded
     */
    protected boolean writeData(NetcdfFileWriteable ncWrite) {

		// time variable
    	if (!this.writeTimeData(ncWrite)) {
    		return false;
    	}

		if (this.isIndexAvailable()) {
			// scatter plot
			
			// index variable
			if (!this.writeIndexData(ncWrite)) {
				return false;
			}

			// x-values
			if (!this.writeDoubleData(ncWrite, this.mXVariable.getName(), false)) {
				return false;
			}

			// y-values
			if (!this.writeDoubleData(ncWrite, this.mYVariable.getName(), false)) {
				return false;
			}

			// z-values
			if (!this.writeDoubleData(ncWrite, this.mZVariable.getName(), false)) {
				return false;
			}

		} else {
			// grid plot
			
			// x-values
			if (!this.writeGridXValues(ncWrite)) {
				return false;
			}

			// y-values
			if (!this.writeGridYValues(ncWrite)) {
				return false;
			}

			// z-values
			if (!this.writeDoubleData(ncWrite, this.mZVariable.getName(), false)) {
				return false;
			}
		}

    	return true;
    }

    /**
     * Returns a text string of the data type to save into a NetCDF data set file.
     *
     * @return a text string of the data type to save into a NetCDF data set file
     */
    @Override
    public String getNetCDFDataSetDataType() {
    	return SGDataTypeConstants.SXYZ_NETCDF_DATA;
    }

	@Override
	protected String getXValueKey() {
		return KEY_X_VALUE_NAME;
	}

	@Override
	protected String getYValueKey() {
		return KEY_Y_VALUE_NAME;
	}

	@Override
	protected String getXDimensionKey() {
		return SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION;
	}

	@Override
	protected String getYDimensionKey() {
		return SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION;
	}

	@Override
	protected String getXStrideKey() {
		return SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X;
	}

	@Override
	protected String getYStrideKey() {
		return SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y;
	}

	@Override
	protected String getScatterStrideKey() {
		return SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE;
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

		double[] xValues = this.getXValueArray(all, false);
		double[] yValues = this.getYValueArray(all, false);
		
		if (this.isIndexAvailable()) {
			double[] zValues = this.getZValueArray(all, false);
			
			// set edited values
			if (edit) {
				for (SGDataValueHistory dataValue : this.mEditedDataValueList) {
					SGDataUtility.setEditedValue(this, xValues, yValues, zValues, 
							all, dataValue);
				}
			}
			
			return new SGSXYZDataBuffer(xValues, yValues, zValues);
			
		} else {
			double[][] zGridValues = this.getGridZValueArray(EXPORT_TYPE.PLUGIN, all);
			
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

    /**
     * Returns a map of information.
     *
     * @return a map of information
     */
    public Map<String, Object> getInfoMap() {
        Map<String, Object> map = super.getInfoMap();
        map.put(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, !this.isIndexAvailable());
        return map;
    }
    
	protected static class SXYZExportInfo {
		String xName;
		String yName;
		String zName;
		double[] xValues;
		double[] yValues;
		double[] oneDimZValueArray;
		double[][] twoDimZValueArray;
		MDArrayDataType xDataType;
		MDArrayDataType yDataType;
		MDArrayDataType zDataType;
		boolean xValid;
		boolean yValid;
	}

	private static final String DEFAULT_VAR_NAME_BASE_X = "X";

	private static final String DEFAULT_VAR_NAME_BASE_Y = "Y";

	private SXYZExportInfo exportCommon(SGExportParameter mode, SGDataBufferPolicy policy) {
		final boolean all = policy.isAllValuesGotten();
		final boolean archiveFlag = SGDataUtility.isArchiveDataSetOperation(mode.getType());
		final boolean exportXFlag = (this.hasEffectiveXStride() && !all) && !archiveFlag;
		final boolean exportYFlag = (this.hasEffectiveYStride() && !all) && !archiveFlag;
		boolean xValid = true;
		boolean yValid = true;

		SGMDArrayVariable[] vars = this.getVariables();
		String xName = this.getTitleX();
		if (xName == null) {
			if (exportXFlag) {
				xName = this.getUniqueVarName(DEFAULT_VAR_NAME_BASE_X, vars);
			} else {
				xValid = false;
			}
		}
		String yName = this.getTitleY();
		if (yName == null) {
			if (exportYFlag) {
				yName = this.getUniqueVarName(DEFAULT_VAR_NAME_BASE_Y, vars);
			} else {
				yValid = false;
			}
		}
		String zName = this.getTitleZ();

		double[] xValues = null;
		double[] yValues = null;
		double[] oneDimZValues = null;
		double[][] twoDimZValues = null;
		SGIDataSource src = this.getDataSource();
		if ((src instanceof SGVirtualMDArrayFile && archiveFlag) || !archiveFlag) {
			// export data to a file or save virtual MDArray data to an archive data set file
			if (this.isIndexAvailable()) {
				SGSXYZDataBuffer buffer = (SGSXYZDataBuffer) this.getDataBuffer(policy);
				xValues = buffer.getXValues();
				yValues = buffer.getYValues();
				oneDimZValues = buffer.getZValues();
			} else {
				SGSXYZGridDataBuffer buffer = (SGSXYZGridDataBuffer) this.getDataBuffer(policy);
				xValues = buffer.getXValues();
				yValues = buffer.getYValues();
				List<SGXYSimpleDoubleValueIndexBlock> blocks = buffer.getZValueBlocks();
				List<Integer> xIndexList = new ArrayList<Integer>();
				List<Integer> yIndexList = new ArrayList<Integer>();
				this.getIndexList(blocks, xIndexList, yIndexList);
				double[][] values = SGDataUtility.getTwoDimensionalValues(blocks, xIndexList, yIndexList);
				twoDimZValues = SGUtility.transpose(values);
			}
		}

		SXYZExportInfo info = new SXYZExportInfo();
		info.xName = xName;
		info.yName = yName;
		info.zName = zName;
		info.xValues = xValues;
		info.yValues = yValues;
		info.oneDimZValueArray = oneDimZValues;
		info.twoDimZValueArray = twoDimZValues;
		info.xDataType = (this.mXVariable != null) ? this.mXVariable.getDataType() : null;
		info.yDataType = (this.mYVariable != null) ? this.mYVariable.getDataType() : null;
		info.zDataType = (this.mZVariable != null) ? this.mZVariable.getDataType() : null;
		info.xValid = xValid;
		info.yValid = yValid;
		
		return info;
	}
	
    /**
     * Exports to a HDF5 file.
     * 
     * @param writer
     *           HDF5-file writer
     * @return true if succeeded
     */
	@Override
    protected boolean exportToHDF5(IHDF5Writer writer, final SGExportParameter mode, SGDataBufferPolicy policy) {
		SXYZExportInfo info = this.exportCommon(mode, policy);
		if (info.xValid) {
			this.write(writer, info.xName, info.xDataType, info.xValues);
		}
		if (info.yValid) {
			this.write(writer, info.yName, info.yDataType, info.yValues);
		}
		if (this.isIndexAvailable()) {
			this.write(writer, info.zName, info.zDataType, info.oneDimZValueArray);
		} else {
			this.write(writer, info.zName, info.zDataType, info.twoDimZValueArray);
		}
		return true;
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
	@Override
    protected boolean exportToMATLAB(File file, MatFileWriter writer, final SGExportParameter mode, 
    		SGDataBufferPolicy policy) {
		SXYZExportInfo info = this.exportCommon(mode, policy);
		List<MLArray> mlList = new ArrayList<MLArray>();
		if (info.xValid) {
			MLDouble xArray = new MLDouble(info.xName, info.xValues, 1);
			mlList.add(xArray);
		}
		if (info.yValid) {
			MLDouble yArray = new MLDouble(info.yName, info.yValues, 1);
			mlList.add(yArray);
		}
		MLDouble zArray;
		if (this.isIndexAvailable()) {
			zArray = new MLDouble(info.zName, info.oneDimZValueArray, 1);
		} else {
			zArray = new MLDouble(info.zName, info.twoDimZValueArray);
		}
		mlList.add(zArray);
		try {
			new MatFileWriter(file.getPath(), mlList);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
    protected void getVarNameColumnTypeList(List<String> varList, 
	    		List<String> columnTypeList) {
		
    	super.getVarNameColumnTypeList(varList, columnTypeList);

		if (this.mXVariable != null) {
			String strX = this.getOneDimensionalVarCommandString(this.mXVariable);
			varList.add(strX);
			columnTypeList.add(X_VALUE);
		}
		
		if (this.mYVariable != null) {
			String strY = this.getOneDimensionalVarCommandString(this.mYVariable);
			varList.add(strY);
			columnTypeList.add(Y_VALUE);
		}
		final String strZ;
		if (this.isIndexAvailable()) {
			strZ = this.getOneDimensionalVarCommandString(this.mZVariable);
		} else {
			strZ = this.getTwoDimensionalVarCommandString(this.mZVariable,
					SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, 
					SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION);
		}
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
    	SGMDArrayFile mdfile = (SGMDArrayFile) src;
    	this.mXVariable = this.findVariable(mdfile, this.mXVariable);
    	this.mYVariable = this.findVariable(mdfile, this.mYVariable);
    	this.mZVariable = this.findVariable(mdfile, this.mZVariable);
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
    	if (this.mXVariable != null) {
    		list.add(SGIDataColumnTypeConstants.X_VALUE);
    	}
    	if (this.mYVariable != null) {
    		list.add(SGIDataColumnTypeConstants.Y_VALUE);
    	}
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
	public Double getDataViewerValue(String columnType, int row, int col) {
		return SGDataUtility.getDataViewerValue(this, columnType, row, col);
	}

	@Override
	public int getDataViewerColumnNumber(final String columnType, final boolean all) {
		int ret = 0;
		if (SGIDataColumnTypeConstants.X_VALUE.equals(columnType)
				|| SGIDataColumnTypeConstants.Y_VALUE.equals(columnType)) {
			ret = 1;
		} else if (SGIDataColumnTypeConstants.Z_VALUE.equals(columnType)) {
			if (this.isIndexAvailable()) {
				ret = 1;
			} else {
				if (all) {
					ret = this.mZVariable.getDimensionLength(KEY_SXYZ_X_DIMENSION);
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
		if (SGIDataColumnTypeConstants.X_VALUE.equals(columnType)) {
			if (this.mXVariable != null) {
				if (all) {
					ret = this.mXVariable.getGenericDimensionLength();
				} else {
					if (this.isIndexAvailable()) {
						ret = this.mIndexStride.getLength();
					} else {
						ret = this.mXStride.getLength();
					}
				}
			}
		} else if (SGIDataColumnTypeConstants.Y_VALUE.equals(columnType)) {
			if (this.mYVariable != null) {
				if (all) {
					ret = this.mYVariable.getGenericDimensionLength();
				} else {
					if (this.isIndexAvailable()) {
						ret = this.mIndexStride.getLength();
					} else {
						ret = this.mYStride.getLength();
					}
				}
			}
		} else if (SGIDataColumnTypeConstants.Z_VALUE.equals(columnType)) {
			if (this.isIndexAvailable()) {
				if (all) {
					ret = this.mZVariable.getGenericDimensionLength();
				} else {
					ret = this.mIndexStride.getLength();
				}
			} else {
				if (all) {
					ret = this.mZVariable.getDimensionLength(KEY_SXYZ_Y_DIMENSION);
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
		return values[index];
	}
    
	@Override
    public Double getYValueAt(final int index) {
		double[] values = this.getYValueArray(false);
		return values[index];
	}

	@Override
    public Double getZValueAt(final int index) {
		if (!this.isIndexAvailable()) {
			throw new Error("Unsupported.");
		}
		double[] values = this.getZValueArray(false);
		return values[index];
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
				len = this.getZVariable().getGenericDimensionLength();
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
	public double[] getXValueArray(final boolean all, final boolean useCache) {
		double[] ret = super.getXValueArray(all);
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
	public double[] getYValueArray(final boolean all, final boolean useCache) {
		double[] ret = super.getYValueArray(all);
    	if (useCache) {
            SGSXYZDataCache.setYValues(this, ret);
    	}
    	return ret;
	}

	@Override
	public String getToolTipSpatiallyVaried(final int index) {
    	return this.getToolTipSpatiallyVaried(index, this.mIndexStride);
	}

	@Override
	public String getToolTipSpatiallyVaried(final int index0, final int index1) {
		StringBuffer sb = new StringBuffer();
		String xyStr = this.getXYToolTipSpatiallyVaried(index0, index1);
		if (!"".equals(xyStr)) {
			sb.append(xyStr);
			sb.append(",<br>");
		}
		String zStr = this.getComponentToolTipSpatiallyVaried(this.mZVariable, 
				SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, 
				SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, index0, index1);
		sb.append(zStr);
		return sb.toString();
	}

    /**
     * Returns a text string of the unit for X values.
     * 
     * @return a text string of the unit for X values
     */
	@Override
    public String getUnitsStringX() {
		// always returns null
		return null;
	}

    /**
     * Returns a text string of the unit for Y values.
     * 
     * @return a text string of the unit for Y values
     */
	@Override
    public String getUnitsStringY() {
		// always returns null
		return null;
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
	public double[] getXValueArray(boolean all, boolean useCache,
			boolean removeInvalidValues) {
		return this.getXValueArray(all, useCache);
	}

	@Override
	public double[] getYValueArray(boolean all, boolean useCache,
			boolean removeInvalidValues) {
		return this.getYValueArray(all, useCache);
	}

	@Override
	public double[] getZValueArray(boolean all, boolean useCache,
			boolean removeInvalidValues) {
		return this.getZValueArray(all, useCache);
	}

	@Override
	protected boolean matches(final int col, final int row, String columnType,
			SGDataValueHistory value, final double d) {
		return SGDataUtility.matches(col, row, columnType, value, d);
	}

	@Override
    protected MDDoubleArray setEditedValues(IHDF5Writer writer,
    		SGMDArrayVariable var, MDDoubleArray array) {
		return this.setEditedValues(writer, var, array, X_VALUE, Y_VALUE,
				new String[] { Z_VALUE });
	}

	@Override
    protected MLDouble setEditedValues(SGMDArrayVariable var, MLDouble array) {
		return this.setEditedValues(var, array, X_VALUE, Y_VALUE,
				new String[] { Z_VALUE });
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
			final boolean all, boolean useCache, boolean removeInvalidValues) {
		return SGDataUtility.getZValueBlockList(this, all, useCache, 
				removeInvalidValues);
	}

}
