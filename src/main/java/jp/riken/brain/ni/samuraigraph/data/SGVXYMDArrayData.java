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
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.Value;
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
 * The class of vector XY type data for multidimensional data file.
 *
 */
public class SGVXYMDArrayData extends SGTwoDimensionalMDArrayData implements SGIVXYTypeData, 
		SGIMDArrayConstants, SGIDataPropertyKeyConstants {

    /**
     * The variable for the first component
     */
    protected SGMDArrayVariable mFirstComponentVariable = null;

    /**
     * The variable for the second component
     */
    protected SGMDArrayVariable mSecondComponentVariable = null;

    /**
     * A flag for polar mode.
     */
    private boolean mPolarFlag = false;

	/**
	 * The default constructor.
	 */
	public SGVXYMDArrayData() {
		super();
	}

    /**
     * Builds a data object with given multidimensional data.
     *
     * @param mdFile
     *           multidimensional data file
     * @param obs
     *           data observer
     * @param xInfo
     *           information of x values
     * @param yInfo
     *           information of y values
     * @param firstInfo
     *           information of the first component values
     * @param secondInfo
     *           information of the second component values
     * @param isPolar
     *           true for polar coordinate
     * @param xStride
     *           stride for x values
     * @param yStride
     *           stride for y values
     * @param scatterStride
     *           stride for scatter plot
     * @param strideAvailable
     *           flag whether to set available the stride
     */
	public SGVXYMDArrayData(SGMDArrayFile mdFile, SGDataSourceObserver obs,
			final SGMDArrayDataColumnInfo xInfo, final SGMDArrayDataColumnInfo yInfo,
			final SGMDArrayDataColumnInfo firstInfo,
			final SGMDArrayDataColumnInfo secondInfo, final boolean isPolar,
			final SGIntegerSeriesSet xStride, final SGIntegerSeriesSet yStride,
			final SGIntegerSeriesSet scatterStride, final boolean strideAvailable) {

		super(mdFile, obs, scatterStride, strideAvailable);

        if (firstInfo == null) {
            throw new IllegalArgumentException("firstInfo == null");
        }
        if (secondInfo == null) {
            throw new IllegalArgumentException("secondInfo == null");
        }

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
            final int fLen = firstInfo.getGenericDimensionLength();
            if (fLen == -1) {
            	throw new IllegalArgumentException("Invalid length: " + fLen);
            }
            final int sLen = secondInfo.getGenericDimensionLength();
            if (sLen == -1) {
            	throw new IllegalArgumentException("Invalid length: " + sLen);
            }
            if (yLen != xLen) {
            	throw new IllegalArgumentException("Length of dimension for y values is invalid: "
            			+ yLen + " != " + xLen);
            }
            if (fLen != xLen) {
            	throw new IllegalArgumentException("Length of dimension for the first component is invalid: "
            			+ fLen + " != " + xLen);
            }
            if (sLen != xLen) {
            	throw new IllegalArgumentException("Length of dimension for the second component is invalid: "
            			+ sLen + " != " + xLen);
            }

	        // finds variables
	        SGMDArrayVariable xVar = this.initVariable(mdFile, xInfo);
	        SGMDArrayVariable yVar = this.initVariable(mdFile, yInfo);
	        SGMDArrayVariable fVar = this.initVariable(mdFile, firstInfo);
	        SGMDArrayVariable sVar = this.initVariable(mdFile, secondInfo);

	        // sets to the attributes
	        this.mXVariable = xVar;
	        this.mYVariable = yVar;
	        this.mFirstComponentVariable = fVar;
	        this.mSecondComponentVariable = sVar;
	        
		} else {
			// grid plot
	        final Integer firstXDim = firstInfo.getDimensionIndex(KEY_VXY_X_DIMENSION);
	        if (firstXDim == null) {
	        	throw new IllegalArgumentException("Failed to get the dimension for x values of the first component.");
	        }
	        final Integer firstYDim = firstInfo.getDimensionIndex(KEY_VXY_Y_DIMENSION);
	        if (firstYDim == null) {
	        	throw new IllegalArgumentException("Failed to get the dimension for y values of the second component.");
	        }
	        final Integer secondXDim = secondInfo.getDimensionIndex(KEY_VXY_X_DIMENSION);
	        if (secondXDim == null) {
	        	throw new IllegalArgumentException("Failed to get the dimension for x values of the first component.");
	        }
	        final Integer secondYDim = secondInfo.getDimensionIndex(KEY_VXY_Y_DIMENSION);
	        if (secondYDim == null) {
	        	throw new IllegalArgumentException("Failed to get the dimension for y values of the second component.");
	        }

	        final int[] firstDims = firstInfo.getDimensions();
	        final int[] secondDims = secondInfo.getDimensions();
	        if (xInfo != null) {
	            final int xLen = xInfo.getGenericDimensionLength();
	            if (firstDims[firstXDim] != xLen) {
	            	throw new IllegalArgumentException("Length of dimension for x values of the first component is invalid: "
	            			+ firstDims[firstXDim] + " != " + xLen);
	            }
	            if (secondDims[secondXDim] != xLen) {
	            	throw new IllegalArgumentException("Length of dimension for x values of the second component is invalid: "
	            			+ secondDims[secondXDim] + " != " + xLen);
	            }
	        }
	        if (yInfo != null) {
	            final int yLen = yInfo.getGenericDimensionLength();
	            if (firstDims[firstYDim] != yLen) {
	            	throw new IllegalArgumentException("Length of dimension for y values of the first component is invalid: "
	            			+ firstDims[firstYDim] + " != " + yLen);
	            }
	            if (secondDims[secondYDim] != yLen) {
	            	throw new IllegalArgumentException("Length of dimension for y values of the second component is invalid: "
	            			+ firstDims[secondYDim] + " != " + yLen);
	            }
	        }
	        final int xLength = firstDims[firstXDim];
	        final int yLength = firstDims[firstYDim];

	        // finds variables
	        SGMDArrayVariable xVar = this.initVariable(mdFile, xInfo);
	        SGMDArrayVariable yVar = this.initVariable(mdFile, yInfo);
	        SGMDArrayVariable fVar = this.initVariable(mdFile, firstInfo);
	        SGMDArrayVariable sVar = this.initVariable(mdFile, secondInfo);

	        // sets to the attributes
	        this.mXVariable = xVar;
	        this.mYVariable = yVar;
	        this.mFirstComponentVariable = fVar;
	        this.mSecondComponentVariable = sVar;
	        this.mXStride = this.createStride(xStride, xLength);
	        this.mYStride = this.createStride(yStride, yLength);
		}
		
        this.mPolarFlag = isPolar;
        this.initTimeStride();
	}

    protected SGMDArrayVariable initVariable(SGMDArrayFile file, SGMDArrayDataColumnInfo info) {
    	SGMDArrayVariable var = super.initVariable(file, info);
    	if (info != null) {
        	Integer xDim = info.getDimensionIndex(SGIMDArrayConstants.KEY_VXY_X_DIMENSION);
        	Integer xValue = null;
        	if (xDim != null) {
        		xValue = xDim;
        	} else {
        		xValue = 0;
        	}
        	var.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_X_DIMENSION, xValue);

        	Integer yDim = info.getDimensionIndex(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION);
        	Integer yValue = null;
        	if (yDim != null) {
        		yValue = yDim;
        	} else {
        		yValue = 0;
        	}
        	var.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION, yValue);
    	}
    	return var;
    }

    protected SGMDArrayVariable initVariable(SGMDArrayVariable var) {
    	super.initVariable(var);
    	var.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_X_DIMENSION, -1);
    	var.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION, -1);
    	return var;
    }

    /**
     * Disposes of this data object.
     *
     */
	@Override
	public void dispose() {
		super.dispose();
		this.mFirstComponentVariable = null;
		this.mSecondComponentVariable = null;
	}

    /**
     * Returns a map of information.
     *
     * @return a map of information
     */
    public Map<String, Object> getInfoMap() {
        Map<String, Object> map = super.getInfoMap();
        map.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, this.isPolar());
        map.put(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, !this.isIndexAvailable());
        return map;
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
	        return SGDataTypeConstants.VXY_HDF5_DATA;
		} else if (src instanceof SGMATLABFile) {
	        return SGDataTypeConstants.VXY_MATLAB_DATA;
		} else if (src instanceof SGVirtualMDArrayFile) {
	        return SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA;
		} else {
			throw new Error("This shouldn't happen.");
		}
	}

    /**
     * Returns whether the data is given in the polar coordinate.
     *
     * @return true if the data is given in the polar coordinate
     *
     */
	@Override
    public boolean isPolar() {
        return this.mPolarFlag;
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
				List<SGXYSimpleDoubleValueIndexBlock> blocks = this.getFirstComponentValueBlockList();
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
	public double[] getFirstComponentValueArray(final boolean all, final boolean useCache) {
		double[] ret = this.getValueArray(this.mFirstComponentVariable, all, 
				KEY_VXY_X_DIMENSION, KEY_VXY_Y_DIMENSION);
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
    public double[] getSecondComponentValueArray(final boolean all, final boolean useCache) {
		double[] ret = this.getValueArray(this.mSecondComponentVariable, all, 
				KEY_VXY_X_DIMENSION, KEY_VXY_Y_DIMENSION);
    	if (useCache) {
            SGVXYDataCache.setSecondComponentValues(this, ret);
    	}
    	return ret;
    }

    /**
     * Returns an array of magnitude of vectors.
     *
     * @return an array of magnitude of vectors
     */
	@Override
    public double[] getMagnitudeArray(final boolean all) {
        if (this.isPolar()) {
            return this.getFirstComponentValueArray(all);
        } else {
            final double[] xComArray = this.getFirstComponentValueArray(all);
            final double[] yComArray = this.getSecondComponentValueArray(all);
            return SGVXYDataUtility.getMagnitudeArray(xComArray, yComArray);
        }
    }

    /**
     * Returns an array of angle of vectors.
     *
     * @return an array of angle of vectors
     */
	@Override
	public double[] getAngleArray(final boolean all) {
        if (this.isPolar()) {
            return this.getSecondComponentValueArray(all);
        } else {
            final double[] xComArray = this.getFirstComponentValueArray(all);
            final double[] yComArray = this.getSecondComponentValueArray(all);
            return SGVXYDataUtility.getAngleArray(xComArray, yComArray);
        }
	}

    /**
     * Returns an array of x component of vectors.
     *
     * @return an array of x component of vectors
     */
	@Override
	public double[] getXComponentArray(final boolean all) {
        if (this.isPolar()) {
            final double[] magArray = this.getFirstComponentValueArray(all);
            final double[] angleArray = this.getSecondComponentValueArray(all);
            return SGVXYDataUtility.getXComponentArray(magArray, angleArray);
        } else {
            return this.getFirstComponentValueArray(all);
        }
	}

    /**
     * Returns an array of y component of vectors.
     *
     * @return an array of y component of vectors
     */
	@Override
	public double[] getYComponentArray(final boolean all) {
        if (this.isPolar()) {
            final double[] magArray = this.getFirstComponentValueArray(all);
            final double[] angleArray = this.getSecondComponentValueArray(all);
            return SGVXYDataUtility.getYComponentArray(magArray, angleArray);
        } else {
            return this.getSecondComponentValueArray(all);
        }
	}

    /**
     * Returns an array of current column types.
     *
     * @return
     *        an array of current column types
     */
	@Override
	public String[] getCurrentColumnType() {
		final boolean polar = this.isPolar();
        final String com1 = SGDataUtility.getVXYFirstComponentColumnType(polar);
        final String com2 = SGDataUtility.getVXYSecondComponentColumnType(polar);
        SGMDArrayVariable[] vars = this.getVariables();
        String[] array = new String[vars.length];
        for (int ii = 0; ii < array.length; ii++) {
        	SGMDArrayVariable var = vars[ii];
            if (var.equals(this.mXVariable)) {
                array[ii] = X_COORDINATE;
            } else if (var.equals(this.mYVariable)) {
                array[ii] = Y_COORDINATE;
            } else if (var.equals(this.mFirstComponentVariable)) {
                array[ii] = com1;
            } else if (var.equals(this.mSecondComponentVariable)) {
                array[ii] = com2;
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
		final boolean polar = this.isPolar();
        final String com1 = SGDataUtility.getVXYFirstComponentColumnType(polar);
        final String com2 = SGDataUtility.getVXYSecondComponentColumnType(polar);
    	SGMDArrayVariable xVar = null;
    	SGMDArrayVariable yVar = null;
    	SGMDArrayVariable fVar = null;
    	SGMDArrayVariable sVar = null;

        SGMDArrayVariable[] vars = this.getVariables();
        for (int ii = 0; ii < columns.length; ii++) {
        	SGMDArrayVariable var = vars[ii];
            if (SGDataUtility.isEqualColumnType(X_COORDINATE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(var.getValueType())) {
            		return false;
            	}
                xVar = var;
            } else if (SGDataUtility.isEqualColumnType(Y_COORDINATE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(var.getValueType())) {
            		return false;
            	}
                yVar = var;
            } else if (SGDataUtility.isEqualColumnType(com1, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(var.getValueType())) {
            		return false;
            	}
                fVar = var;
            } else if (SGDataUtility.isEqualColumnType(com2, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(var.getValueType())) {
            		return false;
            	}
                sVar = var;
            } else if ("".equals(columns[ii])) {
            	continue;
            } else {
            	return false;
            }
        }
        if (fVar == null) {
        	return false;
        }
        if (sVar == null) {
        	return false;
        }

        // set variables
        this.mXVariable = xVar;
        this.mYVariable = yVar;
        this.mFirstComponentVariable = fVar;
        this.mSecondComponentVariable = sVar;

        return true;
	}

    /**
     * A class of vector XY multidimensional data properties.
     */
    public static class VXYMDDataProperties extends TwoDimensionalMDArrayDataProperties {

        protected String fName = null;

        protected String sName = null;

        /**
         * The default constructor.
         */
        public VXYMDDataProperties() {
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
            if ((obj instanceof VXYMDDataProperties) == false) {
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
            if ((dp instanceof VXYMDDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualColumnTypes(dp)) {
            	return false;
            }
            VXYMDDataProperties p = (VXYMDDataProperties) dp;
            if (SGUtility.equals(this.fName, p.fName) == false) {
            	return false;
            }
            if (SGUtility.equals(this.sName, p.sName) == false) {
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
        SGProperties p = new VXYMDDataProperties();
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
        if (!(p instanceof VXYMDDataProperties)) {
            return false;
        }
        if (!super.getProperties(p)) {
        	return false;
        }
        VXYMDDataProperties sp = (VXYMDDataProperties) p;
        sp.fName = this.getName(this.mFirstComponentVariable);
        sp.sName = this.getName(this.mSecondComponentVariable);
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
        if (!(p instanceof VXYMDDataProperties)) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        VXYMDDataProperties sp = (VXYMDDataProperties) p;
        this.mFirstComponentVariable = this.findVariable(sp.fName);
        this.mSecondComponentVariable = this.findVariable(sp.sName);
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
        	sb.append(this.mFirstComponentVariable.getName());
        	sb.append(':');
        	if (this.isIndexAvailable()) {
        		// scatter plot
        		Integer index = this.mFirstComponentVariable.getGenericDimensionIndex();
            	sb.append(index);
        	} else {
        		// grid plot
        		Integer xIndex = this.mFirstComponentVariable.getDimensionIndex(KEY_VXY_X_DIMENSION);
        		Integer yIndex = this.mFirstComponentVariable.getDimensionIndex(KEY_VXY_Y_DIMENSION);
            	sb.append(xIndex);
            	sb.append(':');
            	sb.append(yIndex);
        	}
            el.setAttribute(KEY_FIRST_COMPONENT_VARIABLE_NAME, sb.toString());

        	sb.setLength(0);
        	sb.append(this.mSecondComponentVariable.getName());
        	sb.append(':');
        	if (this.isIndexAvailable()) {
        		// scatter plot
        		Integer index = this.mSecondComponentVariable.getGenericDimensionIndex();
            	sb.append(index);
        	} else {
        		// grid plot
        		Integer xIndex = this.mSecondComponentVariable.getDimensionIndex(KEY_VXY_X_DIMENSION);
        		Integer yIndex = this.mSecondComponentVariable.getDimensionIndex(KEY_VXY_Y_DIMENSION);
            	sb.append(xIndex);
            	sb.append(':');
            	sb.append(yIndex);
        	}
            el.setAttribute(KEY_SECOND_COMPONENT_VARIABLE_NAME, sb.toString());

    	} else if (OPERATION.SAVE_TO_DATA_SET_NETCDF.equals(type)) {
            el.setAttribute(KEY_FIRST_COMPONENT_VARIABLE_NAME, this.mFirstComponentVariable.getName());
            el.setAttribute(KEY_SECOND_COMPONENT_VARIABLE_NAME, this.mSecondComponentVariable.getName());
    	}

        return true;
    }

	@Override
	protected String getXValueKey() {
		return KEY_X_COORDINATE_VARIABLE_NAME;
	}

	@Override
	protected String getYValueKey() {
		return KEY_Y_COORDINATE_VARIABLE_NAME;
	}

    /**
     * Sets the data.
     *
     * @param data
     *             data set to this object
     * @return true if succeeded
     */
    public boolean setData(SGData data) {
        if (!(data instanceof SGVXYMDArrayData)) {
            throw new IllegalArgumentException("Given data is not the instance of SGVXYMDData class.");
        }
        if (super.setData(data) == false) {
            return false;
        }
        SGVXYMDArrayData nData = (SGVXYMDArrayData) data;
        this.mPolarFlag = nData.mPolarFlag;
        this.mFirstComponentVariable = copyVariable(nData.mFirstComponentVariable);
        this.mSecondComponentVariable = copyVariable(nData.mSecondComponentVariable);
        return true;
    }

    /**
     * Returns the copy of this data object.
     *
     * @return a copy of this data object
     */
    public Object clone() {
    	SGVXYMDArrayData data = (SGVXYMDArrayData) super.clone();
        data.mFirstComponentVariable = copyVariable(this.mFirstComponentVariable);
        data.mSecondComponentVariable = copyVariable(this.mSecondComponentVariable);
        return data;
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
    		final boolean all, final boolean useCache, final boolean removeInvalidValues) {
		Integer xIndex = this.mFirstComponentVariable.getDimensionIndex(KEY_VXY_X_DIMENSION);
		Integer yIndex = this.mFirstComponentVariable.getDimensionIndex(KEY_VXY_Y_DIMENSION);
		List<SGXYSimpleDoubleValueIndexBlock> ret = this.get2DValueBlockList(
				this.mFirstComponentVariable, xIndex, yIndex, this.mXStride, this.mYStride,
				all);
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
    		final boolean all, final boolean useCache, final boolean removeInvalidValues) {
		Integer xIndex = this.mSecondComponentVariable.getDimensionIndex(KEY_VXY_X_DIMENSION);
		Integer yIndex = this.mSecondComponentVariable.getDimensionIndex(KEY_VXY_Y_DIMENSION);
		List<SGXYSimpleDoubleValueIndexBlock> ret = this.get2DValueBlockList(
				this.mSecondComponentVariable, xIndex, yIndex, this.mXStride, this.mYStride, 
				all);
		if (useCache) {
	        SGVXYDataCache.setSecondComponentValueBlockList(this, ret);
		}
		return ret;
    }

	protected double[] getValueArraySub(final boolean all, final SGMDArrayVariable var, final SGIntegerSeriesSet stride,
			String name) {
		return this.getValueArraySub(all, var, stride, name, this.mFirstComponentVariable);
	}

	protected int getAllPointsNumberSub(final SGMDArrayVariable var, String name) {
		return this.getAllPointsNumberSub(var, name, this.mFirstComponentVariable);
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
		return this.setColumnType(columns);
	}

	public SGMDArrayVariable getFirstComponentVariable() {
		return this.mFirstComponentVariable;
	}

	public SGMDArrayVariable getSecondComponentVariable() {
		return this.mSecondComponentVariable;
	}

    /**
     * Returns the map of dimension index that are used.
     *
     * @return the map of dimension index
     */
    public Map<String, Map<String, Integer>> getUsedDimensionIndexMap() {
    	Map<String, Map<String, Integer>> map = this.getDimensionIndexMap();
    	Map<String, Integer> fDimMap = map.get(this.mFirstComponentVariable.getName());
    	Map<String, Integer> sDimMap = map.get(this.mSecondComponentVariable.getName());
    	fDimMap.remove(SGIMDArrayConstants.KEY_GENERIC_DIMENSION);
    	sDimMap.remove(SGIMDArrayConstants.KEY_GENERIC_DIMENSION);
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
		if (this.mFirstComponentVariable != null) {
			varList.add(this.mFirstComponentVariable);
		}
		if (this.mSecondComponentVariable != null) {
			varList.add(this.mSecondComponentVariable);
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

			// add the first component variable
			if (!this.addVariable(ncWrite, this.mFirstComponentVariable, dimList, timeDim)) {
				return false;
			}

			// add the second component variable
			if (!this.addVariable(ncWrite, this.mSecondComponentVariable, dimList, timeDim)) {
				return false;
			}

		} else {
			// grid plot
			
			// add x-variable
			Dimension xDim = this.addGridXCoordinateVariable(ncWrite, timeDim);

			// add y-variable
			Dimension yDim = this.addGridYCoordinateVariable(ncWrite, timeDim);

			List<Dimension> dimList = new ArrayList<Dimension>();
			dimList.add(xDim);
			dimList.add(yDim);

			// add the component variables
			if (!this.addVariable(ncWrite, this.mFirstComponentVariable, dimList, timeDim)) {
				return false;
			}
			if (!this.addVariable(ncWrite, this.mSecondComponentVariable, dimList, timeDim)) {
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

			// the first component values
			if (!this.writeDoubleData(ncWrite, this.mFirstComponentVariable.getName(), false)) {
				return false;
			}

			// the second component values
			if (!this.writeDoubleData(ncWrite, this.mSecondComponentVariable.getName(), false)) {
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

			// the first component values
			if (!this.writeDoubleData(ncWrite, this.mFirstComponentVariable.getName(), false)) {
				return false;
			}
			
			// the second component values
			if (!this.writeDoubleData(ncWrite, this.mSecondComponentVariable.getName(), false)) {
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
    	return SGDataTypeConstants.VXY_NETCDF_DATA;
    }

	@Override
	protected String getXDimensionKey() {
		return SGIMDArrayConstants.KEY_VXY_X_DIMENSION;
	}

	@Override
	protected String getYDimensionKey() {
		return SGIMDArrayConstants.KEY_VXY_Y_DIMENSION;
	}

	@Override
	protected String getXStrideKey() {
		return SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X;
	}

	@Override
	protected String getYStrideKey() {
		return SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y;
	}
	
	@Override
	protected String getScatterStrideKey() {
		return SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE;
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
		
		final boolean polar = this.isPolar();
		if (this.isIndexAvailable()) {
			double[] fValues = this.getFirstComponentValueArray(all, false);
			double[] sValues = this.getSecondComponentValueArray(all, false);
			
			// set edited values
			if (edit) {
				for (SGDataValueHistory dataValue : this.mEditedDataValueList) {
					SGDataUtility.setEditedValue(this, xValues, yValues, fValues, sValues, 
							all, dataValue);
				}
			}
			
			return new SGVXYDataBuffer(xValues, yValues, fValues, sValues, polar);
			
		} else {
			double[][] fValues = this.getGridFirstComponentValueArray(EXPORT_TYPE.PLUGIN, all);
			double[][] sValues = this.getGridSecondComponentValueArray(EXPORT_TYPE.PLUGIN, all);
			
			// set edited values
			if (edit) {
				for (SGDataValueHistory dataValue : this.mEditedDataValueList) {
					SGDataUtility.setEditedValue(this, xValues, yValues, fValues, sValues, 
							all, dataValue);
				}
			}
			
			return new SGVXYGridDataBuffer(xValues, yValues, fValues, sValues, polar);
		}

	}

    /**
     * Returns a two-dimensional array of the first component values.
     *
     * @param type
     *           the type of export
     * @param all
     *           true to get all values
     * @return a two-dimensional array of the first component values
     */
    public double[][] getTwoDimensionalFirstComponentValueArray(EXPORT_TYPE type,
    		final boolean all) {
		double[] values = this.getFirstComponentValueArray(all);
    	return this.getTwoDimensionalValueArray(values, this.mFirstComponentVariable, type, all);
    }

    /**
     * Returns a two-dimensional array of the first second values.
     *
     * @param type
     *           the type of export
     * @param all
     *           true to get all values
     * @return a two-dimensional array of the second component values
     */
    public double[][] getTwoDimensionalSecondComponentValueArray(EXPORT_TYPE type, 
    		final boolean all) {
		double[] values = this.getSecondComponentValueArray(all);
    	return this.getTwoDimensionalValueArray(values, this.mSecondComponentVariable, type, all);
    }

	static class VXYExportInfo {
		String xName;
		String yName;
		String fName;
		String sName;
		double[] xValueArray;
		double[] yValueArray;
		double[] oneDimFValueArray;
		double[][] twoDimFValueArray;
		double[] oneDimSValueArray;
		double[][] twoDimSValueArray;
		MDArrayDataType xDataType;
		MDArrayDataType yDataType;
		MDArrayDataType fDataType;
		MDArrayDataType sDataType;
		boolean xValid;
		boolean yValid;
	}

	private static final String DEFAULT_VAR_NAME_BASE_X = "X";

	private static final String DEFAULT_VAR_NAME_BASE_Y = "Y";

	private VXYExportInfo exportCommon(SGExportParameter mode, SGDataBufferPolicy policy) {
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
		String fName = this.mFirstComponentVariable.getName();
		String sName = this.mSecondComponentVariable.getName();

		double[] xValues = null;
		double[] yValues = null;
		double[] oneDimFValues = null;
		double[][] twoDimFValues = null;
		double[] oneDimSValues = null;
		double[][] twoDimSValues = null;
		SGIDataSource src = this.getDataSource();
		if ((src instanceof SGVirtualMDArrayFile && archiveFlag) || !archiveFlag) {
			// export data to a file or save virtual MDArray data to an archive data set file
			if (this.isIndexAvailable()) {
				SGVXYDataBuffer buffer = (SGVXYDataBuffer) this.getDataBuffer(policy);
				xValues = buffer.getXValues();
				yValues = buffer.getYValues();
				oneDimFValues = buffer.getFirstComponentValues();
				oneDimSValues = buffer.getSecondComponentValues();
			} else {
				SGVXYGridDataBuffer buffer = (SGVXYGridDataBuffer) this.getDataBuffer(policy);
				xValues = buffer.getXValues();
				yValues = buffer.getYValues();
				List<SGXYSimpleDoubleValueIndexBlock> fBlocks = buffer.getFirstComponentValueBlocks();
				List<SGXYSimpleDoubleValueIndexBlock> sBlocks = buffer.getSecondComponentValueBlocks();
				List<Integer> xIndexList = new ArrayList<Integer>();
				List<Integer> yIndexList = new ArrayList<Integer>();
				this.getIndexList(fBlocks, xIndexList, yIndexList);
				double[][] fValues = SGDataUtility.getTwoDimensionalValues(fBlocks, xIndexList, yIndexList);
				twoDimFValues = SGUtility.transpose(fValues);
				double[][] sValues = SGDataUtility.getTwoDimensionalValues(sBlocks, xIndexList, yIndexList);
				twoDimSValues = SGUtility.transpose(sValues);
			}
		}
		
		VXYExportInfo info = new VXYExportInfo();
		info.xName = xName;
		info.yName = yName;
		info.fName = fName;
		info.sName = sName;
		info.xValueArray = xValues;
		info.yValueArray = yValues;
		info.oneDimFValueArray = oneDimFValues;
		info.oneDimSValueArray = oneDimSValues;
		info.twoDimFValueArray = twoDimFValues;
		info.twoDimSValueArray = twoDimSValues;
		info.xDataType = (this.mXVariable != null) ? this.mXVariable.getDataType() : null;
		info.yDataType = (this.mYVariable != null) ? this.mYVariable.getDataType() : null;
		info.fDataType = (this.mFirstComponentVariable != null) ? this.mFirstComponentVariable.getDataType() : null;
		info.sDataType = (this.mSecondComponentVariable != null) ? this.mSecondComponentVariable.getDataType() : null;
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
		VXYExportInfo info = this.exportCommon(mode, policy);
		if (info.xValid) {
			this.write(writer, info.xName, info.xDataType, info.xValueArray);
		}
		if (info.yValid) {
			this.write(writer, info.yName, info.yDataType, info.yValueArray);
		}
		if (this.isIndexAvailable()) {
			this.write(writer, info.fName, info.fDataType, info.oneDimFValueArray);
			this.write(writer, info.sName, info.sDataType, info.oneDimSValueArray);
		} else {
			this.write(writer, info.fName, info.fDataType, info.twoDimFValueArray);
			this.write(writer, info.sName, info.sDataType, info.twoDimSValueArray);
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
		VXYExportInfo info = this.exportCommon(mode, policy);
		List<MLArray> mlList = new ArrayList<MLArray>();
		if (info.xValid) {
			MLDouble xArray = new MLDouble(info.xName, info.xValueArray, 1);
			mlList.add(xArray);
		}
		if (info.yValid) {
			MLDouble yArray = new MLDouble(info.yName, info.yValueArray, 1);
			mlList.add(yArray);
		}
		MLDouble fArray, sArray;
		if (this.isIndexAvailable()) {
			fArray = new MLDouble(info.fName, info.oneDimFValueArray, 1);
			sArray = new MLDouble(info.sName, info.oneDimSValueArray, 1);
		} else {
			fArray = new MLDouble(info.fName, info.twoDimFValueArray);
			sArray = new MLDouble(info.sName, info.twoDimSValueArray);
		}
		mlList.add(fArray);
		mlList.add(sArray);
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

    	final boolean polar = this.isPolar();

		if (this.mXVariable != null) {
			String strX = this.getOneDimensionalVarCommandString(this.mXVariable);
			varList.add(strX);
			columnTypeList.add(X_COORDINATE);
		}
		
		if (this.mYVariable != null) {
			String strY = this.getOneDimensionalVarCommandString(this.mYVariable);
			varList.add(strY);
			columnTypeList.add(Y_COORDINATE);
		}
		
		final String strF;
		final String strS;
		if (this.isIndexAvailable()) {
			strF = this.getOneDimensionalVarCommandString(this.mFirstComponentVariable);
			strS = this.getOneDimensionalVarCommandString(this.mSecondComponentVariable);
		} else {
			strF = this.getTwoDimensionalVarCommandString(
					this.mFirstComponentVariable,
					SGIMDArrayConstants.KEY_VXY_X_DIMENSION, 
					SGIMDArrayConstants.KEY_VXY_Y_DIMENSION);
			strS = this.getTwoDimensionalVarCommandString(
					this.mSecondComponentVariable,
					SGIMDArrayConstants.KEY_VXY_X_DIMENSION, 
					SGIMDArrayConstants.KEY_VXY_Y_DIMENSION);
		}
		varList.add(strF);
		columnTypeList.add(SGDataUtility.getVXYFirstComponentColumnType(polar));
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
    	SGMDArrayFile mdfile = (SGMDArrayFile) src;
    	this.mXVariable = this.findVariable(mdfile, this.mXVariable);
    	this.mYVariable = this.findVariable(mdfile, this.mYVariable);
    	this.mFirstComponentVariable = this.findVariable(
    			mdfile, this.mFirstComponentVariable);
    	this.mSecondComponentVariable = this.findVariable(
    			mdfile, this.mSecondComponentVariable);
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
    	if (this.mXVariable != null) {
    		list.add(SGIDataColumnTypeConstants.X_COORDINATE);
    	}
    	if (this.mYVariable != null) {
    		list.add(SGIDataColumnTypeConstants.Y_COORDINATE);
    	}
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
		final boolean polar = this.isPolar();
		final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
		final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
		if (SGIDataColumnTypeConstants.X_COORDINATE.equals(columnType)
				|| SGIDataColumnTypeConstants.Y_COORDINATE.equals(columnType)) {
			ret = 1;
		} else if (first.equals(columnType) || second.equals(columnType)) {
			if (this.isIndexAvailable()) {
				ret = 1;
			} else {
				if (all) {
					ret = this.mFirstComponentVariable.getDimensionLength(KEY_VXY_X_DIMENSION);
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
		final boolean polar = this.isPolar();
		final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
		final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
		if (SGIDataColumnTypeConstants.X_COORDINATE.equals(columnType)) {
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
		} else if (SGIDataColumnTypeConstants.Y_COORDINATE.equals(columnType)) {
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
		} else if (first.equals(columnType) || second.equals(columnType)) {
			if (this.isIndexAvailable()) {
				if (all) {
					ret = this.mFirstComponentVariable.getGenericDimensionLength();
				} else {
					ret = this.mIndexStride.getLength();
				}
			} else {
				if (all) {
					ret = this.mFirstComponentVariable.getDimensionLength(KEY_VXY_Y_DIMENSION);
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
    public Double getFirstComponentValueAt(final int index) {
		if (!this.isIndexAvailable()) {
			throw new Error("Unsupported.");
		}
		double[] values = this.getFirstComponentValueArray(false);
		return values[index];
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
		return values[index];
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
				len = this.getFirstComponentVariable().getGenericDimensionLength();
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

    /**
     * Returns a grid type array of the first component values.
     *
     * @param type
     *           the type of export
     * @param all
     *            true to get all values
     * @return a grid type array of the first component values
     */
    public double[][] getGridFirstComponentValueArray(EXPORT_TYPE  type, final boolean all) {
		double[] values = this.getFirstComponentValueArray(all);
    	return this.getTwoDimensionalValueArray(values, this.mFirstComponentVariable, type, all);
    }

    /**
     * Returns a grid type array of the second component values.
     *
     * @param type
     *           the type of export
     * @param all
     *            true to get all values
     * @return a grid type array of the second component values
     */
    public double[][] getGridSecondComponentValueArray(EXPORT_TYPE  type, final boolean all) {
		double[] values = this.getSecondComponentValueArray(all);
    	return this.getTwoDimensionalValueArray(values, this.mSecondComponentVariable, type, all);
    }

    /**
     * Returns a scatter type array the first component values.
     *
     * @param all
     *            true to get all values
     * @return a scatter array of the first component values
     */
    public double[] getScatterFirstComponentValueArray(EXPORT_TYPE  type, final boolean all) {
		return this.getFirstComponentValueArray(all);
    }

    /**
     * Returns a scatter type array the second component values.
     *
     * @param all
     *            true to get all values
     * @return a scatter array of the second component values
     */
    public double[] getScatterSecondComponentValueArray(EXPORT_TYPE  type, final boolean all) {
		return this.getSecondComponentValueArray(all);
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
	public boolean useXValueCache(boolean all) {
		return this.useXYCache(all, this.mXStride);
	}

	@Override
    public double[] getXValueArray(final boolean all, final boolean useCache) {
    	double[] ret = super.getXValueArray(all);
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
    public double[] getYValueArray(final boolean all, final boolean useCache) {
		double[] ret = super.getYValueArray(all);
    	if (useCache) {
    		SGVXYDataCache.setYValues(this, ret);
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
		String fStr = this.getComponentToolTipSpatiallyVaried(
				this.mFirstComponentVariable, 
				SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, 
				SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, index0, index1);
		String sStr = this.getComponentToolTipSpatiallyVaried(
				this.mSecondComponentVariable, 
				SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, 
				SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, index0, index1);
		sb.append(fStr);
		sb.append(",<br>");
		sb.append(sStr);
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
	public double[] getFirstComponentValueArray(boolean all, boolean useCache,
			boolean removeInvalidValues) {
		return this.getFirstComponentValueArray(all, useCache);
	}

	@Override
	public double[] getSecondComponentValueArray(boolean all, boolean useCache,
			boolean removeInvalidValues) {
		return this.getSecondComponentValueArray(all, useCache);
	}

	@Override
	protected boolean matches(final int col, final int row, String columnType,
			SGDataValueHistory value, final double d) {
		return SGDataUtility.matches(col, row, columnType, value, d);
	}

	@Override
    protected MDDoubleArray setEditedValues(IHDF5Writer writer,
    		SGMDArrayVariable var, MDDoubleArray array) {
		final boolean polar = this.isPolar();
		String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
		String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
		return this.setEditedValues(writer, var, array, X_COORDINATE, Y_COORDINATE,
				new String[] { first, second });
	}

	@Override
    protected MLDouble setEditedValues(SGMDArrayVariable var, MLDouble array) {
		final boolean polar = this.isPolar();
		String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
		String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
		return this.setEditedValues(var, array, X_COORDINATE, Y_COORDINATE,
				new String[] { first, second });
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
