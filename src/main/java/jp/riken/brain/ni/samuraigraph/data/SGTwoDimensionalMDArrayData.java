package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

import org.w3c.dom.Element;

import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;
import ch.systemsx.cisd.base.mdarray.MDDoubleArray;
import ch.systemsx.cisd.hdf5.IHDF5Writer;

import com.jmatio.types.MLDouble;

/**
 * The base class of two dimensional MDArray data.
 *
 */
public abstract class SGTwoDimensionalMDArrayData extends SGMDArrayData
		implements SGITwoDimensionalData, SGIIndexData, SGIDataColumnTypeConstants,
		SGIDataPropertyKeyConstants, SGIMDArrayConstants {

    /**
     * The variable for x-values.
     */
    protected SGMDArrayVariable mXVariable = null;
    
    /**
     * The variable for y-values.
     */
    protected SGMDArrayVariable mYVariable = null;

    /**
     * The stride for x-values.
     */
    protected SGIntegerSeriesSet mXStride = null;

    /**
     * The stride for y-values.
     */
    protected SGIntegerSeriesSet mYStride = null;

    /**
     * The stride for scatter plot.
     */
    protected SGIntegerSeriesSet mIndexStride = null;

    /**
     * The default constructor.
     */
	public SGTwoDimensionalMDArrayData() {
		super();
	}

    /**
     * Disposes of this data object.
     * 
     */
	@Override
	public void dispose() {
		super.dispose();
		this.mXVariable = null;
		this.mYVariable = null;
		this.mXStride = null;
		this.mYStride = null;
	}

    /**
     * Returns the copy of this data object.
     * 
     * @return a copy of this data object
     */
    public Object clone() {
    	SGTwoDimensionalMDArrayData data = (SGTwoDimensionalMDArrayData) super.clone();
        data.mXVariable = copyVariable(this.mXVariable);
        data.mYVariable = copyVariable(this.mYVariable);
        data.mXStride = this.getXStride();
        data.mYStride = this.getYStride();
        return data;
    }

    /**
     * Builds a data object.
     * 
     * @param src
     *           the data source
     * @param obs
     *           a data observer
     * @param scatterStride
     *           stride for scatter plot
     */
    public SGTwoDimensionalMDArrayData(final SGIDataSource src, final SGDataSourceObserver obs,
    		final SGIntegerSeriesSet scatterStride, final boolean strideAvailable) {
    	super(src, obs, strideAvailable);
    	this.mIndexStride = (scatterStride != null) ? (SGIntegerSeriesSet) scatterStride.clone() : null;
    }
    
    /**
     * Returns the stride for x-values.
     * 
     * @return the stride
     */
    public SGIntegerSeriesSet getXStride() {
    	if (this.mXStride != null) {
        	return (SGIntegerSeriesSet) this.mXStride.clone();
    	} else {
    		return null;
    	}
    }

    /**
     * Sets the stride for x-values.
     * 
     * @param stride
     *           the stride
     */
    public void setXStride(final SGIntegerSeriesSet stride) {
    	if (!SGUtility.equals(this.mXStride, stride)) {
    		this.clearCache();
    	}
    	if (stride != null) {
        	this.mXStride = (SGIntegerSeriesSet) stride.clone();
    	} else {
    		this.mXStride = null;
    	}
    }

    /**
     * Returns the stride for y-values.
     * 
     * @return the stride
     */
    public SGIntegerSeriesSet getYStride() {
    	if (this.mYStride != null) {
        	return (SGIntegerSeriesSet) this.mYStride.clone();
    	} else {
    		return null;
    	}
    }

    /**
     * Sets the stride for y-values.
     * 
     * @param stride
     *           the stride
     */
    public void setYStride(final SGIntegerSeriesSet stride) {
    	if (!SGUtility.equals(this.mYStride, stride)) {
    		this.clearCache();
    	}
    	if (stride != null) {
        	this.mYStride = (SGIntegerSeriesSet) stride.clone();
    	} else {
    		this.mYStride = null;
    	}
    }

    /**
     * Returns the number of data points without taking into account the stride.
     * 
     * @return the number of data points without taking into account the stride
     */
    @Override
    public int getAllPointsNumber() {
    	if (this.isIndexAvailable()) {
        	return this.getXDimensionLength();
    	} else {
        	final int lenX = this.getXDimensionLength();
        	final int lenY = this.getYDimensionLength();
        	return lenX * lenY;
    	}
    }

    /**
     * Returns the title for the X-axis.
     * 
     * @return the title for the X-axis
     */
	@Override
	public String getTitleX() {
		if (this.mXVariable != null) {
			return this.mXVariable.getSimpleName();
		} else {
			return null;
		}
	}

    /**
     * Returns the title for the Y-axis.
     * 
     * @return the title for the Y-axis
     */
	@Override
	public String getTitleY() {
		if (this.mYVariable != null) {
			return this.mYVariable.getSimpleName();
		} else {
			return null;
		}
	}

    /**
     * Returns an array of X-values.
     * 
     * @return an array of X-values
     */
	public double[] getXValueArray(final boolean all) {
    	SGIntegerSeriesSet stride;
    	String key;
    	if (this.isIndexAvailable()) {
    		stride = this.mIndexStride;
    		key = SGIMDArrayConstants.KEY_GENERIC_DIMENSION;
    	} else {
    		stride = this.mXStride;
    		key = this.getXDimensionKey();
    	}
		return this.getValueArraySub(all, this.mXVariable, stride, key);
    }


    /**
     * Returns an array of Y-values.
     * 
     * @return an array of Y-values
     */
	public double[] getYValueArray(final boolean all) {
    	SGIntegerSeriesSet stride;
    	String key;
    	if (this.isIndexAvailable()) {
    		stride = this.mIndexStride;
    		key = SGIMDArrayConstants.KEY_GENERIC_DIMENSION;
    	} else {
    		stride = this.mYStride;
    		key = this.getYDimensionKey();
    	}
		return this.getValueArraySub(all, this.mYVariable, stride, key);
	}

    /**
	 * Returns an array of xy-values.
	 * 
	 * @return an array of xy-values
	 */
	@Override
	public SGTuple2d[] getXYValueArray(final boolean all) {
        double[] xArray = this.getXValueArray(all);
        double[] yArray = this.getYValueArray(all);
		if (this.isIndexAvailable()) {
	        SGTuple2d[] cArray = new SGTuple2d[xArray.length];
	        for (int ii = 0; ii < xArray.length; ii++) {
	            cArray[ii] = new SGTuple2d(xArray[ii], yArray[ii]);
	        }
	        return cArray;
		} else {
	        SGTuple2d[] cArray = new SGTuple2d[xArray.length * yArray.length];
	        for (int yy = 0; yy < yArray.length; yy++) {
	            for (int xx = 0; xx < xArray.length; xx++) {
	                final int index = yy * xArray.length + xx;
	                cArray[index] = new SGTuple2d(xArray[xx], yArray[yy]);
	            }
	        }
	        return cArray;
		}
	}

    /**
     * Returns the variable for x-values.
     * 
     * @return the variable for x-values
     */
	public SGMDArrayVariable getXVariable() {
		return this.mXVariable;
	}

    /**
     * Returns the variable for y-values.
     * 
     * @return the variable for y-values
     */
	public SGMDArrayVariable getYVariable() {
		return this.mYVariable;
	}
	
    /**
     * Sets the data.
     * 
     * @param data
     *             data set to this object
     * @return true if succeeded            
     */
    public boolean setData(SGData data) {
        if (!(data instanceof SGTwoDimensionalMDArrayData)) {
            throw new IllegalArgumentException("Given data is not the instance of SGTwoDimensionalMDArrayData class.");
        }
        if (super.setData(data) == false) {
            return false;
        }
        SGTwoDimensionalMDArrayData mdData = (SGTwoDimensionalMDArrayData) data;
        this.mXVariable = copyVariable(mdData.mXVariable);
        this.mYVariable = copyVariable(mdData.mYVariable);
        this.setXStride(mdData.mXStride);
        this.setYStride(mdData.mYStride);
        this.setIndexStride(mdData.mIndexStride);
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
    public boolean writeProperty(Element el, final SGExportParameter params) {
        if (super.writeProperty(el, params) == false) {
            return false;
        }
        
        OPERATION type = params.getType();
    	if (OPERATION.SAVE_TO_PROPERTY_FILE.equals(type)
    			|| SGDataUtility.isArchiveDataSetOperation(type)) {
    		
            StringBuffer sb = new StringBuffer();
            if (this.mXVariable != null) {
            	sb.setLength(0);
            	sb.append(this.mXVariable.getName());
            	sb.append(':');
            	Integer index = this.mXVariable.getGenericDimensionIndex();
            	sb.append(index);
                el.setAttribute(this.getXValueKey(), sb.toString());
            }
            if (this.mYVariable != null) {
            	sb.setLength(0);
            	sb.append(this.mYVariable.getName());
            	sb.append(':');
            	Integer index = this.mYVariable.getGenericDimensionIndex();
            	sb.append(index);
                el.setAttribute(this.getYValueKey(), sb.toString());
            }

    	} else if (OPERATION.SAVE_TO_DATA_SET_NETCDF.equals(type)) {

    		if (this.isIndexAvailable()) {
    			// scatter plot
                el.setAttribute(KEY_X_VALUE_NAME, this.mXVariable.getName());
                el.setAttribute(KEY_Y_VALUE_NAME, this.mYVariable.getName());
    		} else {
    			// grid plot
                if (this.mXVariable != null) {
                    el.setAttribute(this.getXValueKey(), this.mXVariable.getName());
                    el.setAttribute(KEY_X_INDEX_VARIABLE_NAME, X_INDEX_DIM_NAME);
                } else {
                    el.setAttribute(this.getXValueKey(), X_VALUE_VAR_NAME);
                }
                if (this.mYVariable != null) {
                    el.setAttribute(this.getYValueKey(), this.mYVariable.getName());
                    el.setAttribute(KEY_Y_INDEX_VARIABLE_NAME, Y_INDEX_DIM_NAME);
                } else {
                    el.setAttribute(this.getYValueKey(), Y_VALUE_VAR_NAME);
                }
    		}
    	}
    	
        // stride
    	if (this.isIndexAvailable()) {
        	el.setAttribute(KEY_INDEX_ARRAY_SECTION, this.mIndexStride.toString());
    	} else {
        	el.setAttribute(KEY_X_ARRAY_SECTION, this.mXStride.toString());
        	el.setAttribute(KEY_Y_ARRAY_SECTION, this.mYStride.toString());
    	}
		
        return true;
    }
    
    protected abstract String getXValueKey();

    protected abstract String getYValueKey();

    /**
     * A class of two-dimensional MDArray data properties.
     */
    public static abstract class TwoDimensionalMDArrayDataProperties extends MDArrayDataProperties {

        protected String xName = null;
        
        protected String yName = null;
        
        protected SGIntegerSeriesSet xStride = null;
        
        protected SGIntegerSeriesSet yStride = null;
        
        protected SGIntegerSeriesSet scatterStride = null;
        
        /**
         * The default constructor.
         */
        public TwoDimensionalMDArrayDataProperties() {
            super();
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.xName = null;
            this.yName = null;
            this.xStride = null;
            this.yStride = null;
            this.scatterStride = null;
        }
        
    	@Override
    	public boolean equals(Object obj) {
            if ((obj instanceof TwoDimensionalMDArrayDataProperties) == false) {
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
            if ((dp instanceof TwoDimensionalMDArrayDataProperties) == false) {
                return false;
            }
//            if (super.hasEqualColumnTypes(dp) == false) {
//            	return false;
//            }
            TwoDimensionalMDArrayDataProperties p = (TwoDimensionalMDArrayDataProperties) dp;
            if (SGUtility.equals(this.xName, p.xName) == false) {
            	return false;
            }
            if (SGUtility.equals(this.yName, p.yName) == false) {
            	return false;
            }
        	return true;
        }

    	@Override
        public boolean hasEqualSize(DataProperties dp) {
            if ((dp instanceof TwoDimensionalMDArrayDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualSize(dp)) {
            	return false;
            }
            TwoDimensionalMDArrayDataProperties p = (TwoDimensionalMDArrayDataProperties) dp;
            if (!SGUtility.equals(this.xStride, p.xStride)) {
                return false;
            }
            if (!SGUtility.equals(this.yStride, p.yStride)) {
                return false;
            }
            if (!SGUtility.equals(this.scatterStride, p.scatterStride)) {
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
        	TwoDimensionalMDArrayDataProperties p = (TwoDimensionalMDArrayDataProperties) super.copy();
        	p.xStride = (this.xStride != null) ? (SGIntegerSeriesSet) this.xStride.clone() : null;
        	p.yStride = (this.yStride != null) ? (SGIntegerSeriesSet) this.yStride.clone() : null;
        	p.scatterStride = (this.scatterStride != null) ? (SGIntegerSeriesSet) this.scatterStride.clone() : null;
        	return p;
        }
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
        if (!(p instanceof TwoDimensionalMDArrayDataProperties)) {
            return false;
        }
        if (!super.getProperties(p)) {
        	return false;
        }
        TwoDimensionalMDArrayDataProperties sp = (TwoDimensionalMDArrayDataProperties) p;
        sp.xName = this.getName(this.mXVariable);
        sp.yName = this.getName(this.mYVariable);
        sp.xStride = this.getXStride();
        sp.yStride = this.getYStride();
        sp.scatterStride = this.getIndexStride();
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
        if (!(p instanceof TwoDimensionalMDArrayDataProperties)) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        TwoDimensionalMDArrayDataProperties sp = (TwoDimensionalMDArrayDataProperties) p;
        this.mXVariable = this.findVariable(sp.xName);
        this.mYVariable = this.findVariable(sp.yName);
        this.setXStride(sp.xStride);
        this.setYStride(sp.yStride);
        this.setIndexStride(sp.scatterStride);
        return true;
	}

	protected static final String INDEX_DIM_NAME = "index";

	protected static final String X_INDEX_DIM_NAME = "x_index";

	protected static final String Y_INDEX_DIM_NAME = "y_index";

	protected static final String X_VALUE_VAR_NAME = "x";

	protected static final String Y_VALUE_VAR_NAME = "y";

	protected boolean addVariable(NetcdfFileWriteable ncWrite, SGMDArrayVariable var,
			List<Dimension> dimList, Dimension timeDim) {
		List<Dimension> list = this.addTimeDimension(var, timeDim, dimList);
		if (!this.addDoubleVariable(ncWrite, list, var.getName())) {
			return false;
		}
		return true;
	}

	protected Dimension addGridXCoordinateVariable(NetcdfFileWriteable ncWrite, Dimension timeDim) {
		if (this.isIndexAvailable()) {
			return null;
		}
		
		// get the number of points
		final int xLen = this.getXDimensionLength();

		Dimension xDim = null;

		// add x-variable
		if (this.mXVariable != null) {
			String xName = this.mXVariable.getName();
			Dimension xIndexDim = ncWrite.addDimension(X_INDEX_DIM_NAME, xLen);
			if (!this.addSequentialIntegerNumberVariable(ncWrite, xIndexDim, X_INDEX_DIM_NAME)) {
				return null;
			}
			List<Dimension> dimList = new ArrayList<Dimension>();
			dimList.add(xIndexDim);
			dimList = this.addTimeDimension(this.mXVariable, timeDim, dimList);
			if (!this.addDoubleVariable(ncWrite, dimList, xName)) {
				return null;
			}
			xDim = xIndexDim;
		} else {
			Dimension xValueDim = ncWrite.addDimension(X_VALUE_VAR_NAME, xLen);
			if (!this.addSequentialDoubleNumberVariable(ncWrite, xValueDim, X_VALUE_VAR_NAME)) {
				return null;
			}
			xDim = xValueDim;
		}

		return xDim;
	}

	protected Dimension addGridYCoordinateVariable(NetcdfFileWriteable ncWrite, Dimension timeDim) {
		if (this.isIndexAvailable()) {
			return null;
		}
		
		// get the number of points
		final int yLen = this.getYDimensionLength();

		Dimension yDim = null;

		if (this.mYVariable != null) {
			String yName = this.mYVariable.getName();
			Dimension yIndexDim = ncWrite.addDimension(Y_INDEX_DIM_NAME, yLen);
			if (!this.addSequentialIntegerNumberVariable(ncWrite, yIndexDim, Y_INDEX_DIM_NAME)) {
				return null;
			}
			List<Dimension> dimList = new ArrayList<Dimension>();
			dimList.add(yIndexDim);
			dimList = this.addTimeDimension(this.mYVariable, timeDim, dimList);
			if (!this.addDoubleVariable(ncWrite, dimList, yName)) {
				return null;
			}
			yDim = yIndexDim;
		} else {
			Dimension yValueDim = ncWrite.addDimension(Y_VALUE_VAR_NAME, yLen);
			if (!this.addSequentialDoubleNumberVariable(ncWrite, yValueDim, Y_VALUE_VAR_NAME)) {
				return null;
			}
			yDim = yValueDim;
		}
		return yDim;
	}
	
	protected Dimension addIndexCoordinateVariable(NetcdfFileWriteable ncWrite) {
		if (!this.isIndexAvailable()) {
			return null;
		}
		// add index variable
		final int fullLength = this.getIndexStride().getEndIndex() + 1;
		Dimension indexDim = ncWrite.addDimension(INDEX_DIM_NAME, fullLength);
		if (!this.addSequentialIntegerNumberVariable(ncWrite, indexDim, INDEX_DIM_NAME)) {
			return null;
		}
		return indexDim;
	}

	protected boolean writeGridXValues(NetcdfFileWriteable ncWrite) {
		if (this.mXVariable != null) {
			Dimension xIndexDim = ncWrite.findDimension(X_INDEX_DIM_NAME);
			if (!this.writeSequentialIntegerNumbers(ncWrite, X_INDEX_DIM_NAME, xIndexDim.getLength())) {
				return false;
			}
			if (!this.writeDoubleData(ncWrite, this.mXVariable.getName(), true)) {
				return false;
			}
		} else {
			Dimension xValueDim = ncWrite.findDimension(X_VALUE_VAR_NAME);
			if (!this.writeSequentialDoubleNumbers(ncWrite, X_VALUE_VAR_NAME, xValueDim.getLength())) {
				return false;
			}
		}
		return true;
	}
	
	protected boolean writeGridYValues(NetcdfFileWriteable ncWrite) {
		if (this.mYVariable != null) {
			Dimension yIndexDim = ncWrite.findDimension(Y_INDEX_DIM_NAME);
			if (!this.writeSequentialIntegerNumbers(ncWrite, Y_INDEX_DIM_NAME, yIndexDim.getLength())) {
				return false;
			}
			if (!this.writeDoubleData(ncWrite, this.mYVariable.getName(), true)) {
				return false;
			}
		} else {
			Dimension yValueDim = ncWrite.findDimension(Y_VALUE_VAR_NAME);
			if (!this.writeSequentialDoubleNumbers(ncWrite, Y_VALUE_VAR_NAME, yValueDim.getLength())) {
				return false;
			}
		}
		return true;
	}

	protected abstract String getXDimensionKey();

	protected abstract String getYDimensionKey();

    protected boolean writeDoubleData(NetcdfFileWriteable ncWrite, String varName,
    		final boolean bGeneric) {
		SGMDArrayVariable mdVar = this.findVariable(varName);
		Map<String, Integer> mdArrayIndexMap = new HashMap<String, Integer>();
		Variable ncVar = ncWrite.findVariable(varName);
		List<Dimension> ncDimList = ncVar.getDimensions();
		
		List<String> xNameList = new ArrayList<String>();
		xNameList.add(X_INDEX_DIM_NAME);
		xNameList.add(X_VALUE_VAR_NAME);
		if (this.mXVariable != null) {
			xNameList.add(this.mXVariable.getName());
		}
		
		List<String> yNameList = new ArrayList<String>();
		yNameList.add(Y_INDEX_DIM_NAME);
		yNameList.add(Y_VALUE_VAR_NAME);
		if (this.mYVariable != null) {
			yNameList.add(this.mYVariable.getName());
		}

		for (Dimension dim : ncDimList) {
			String dimName = dim.getName();
			Integer index = null;
			if (xNameList.contains(dimName)) {
				String key = bGeneric ? SGIMDArrayConstants.KEY_GENERIC_DIMENSION : this.getXDimensionKey();
				index = mdVar.getDimensionIndex(key);
			} else if (yNameList.contains(dimName)) {
				String key = bGeneric ? SGIMDArrayConstants.KEY_GENERIC_DIMENSION : this.getYDimensionKey();
				index = mdVar.getDimensionIndex(key);
			} else if (INDEX_DIM_NAME.equals(dimName)) {
				index = mdVar.getGenericDimensionIndex();
			} else if (TIME_DIM_NAME.equals(dimName)) {
				index = mdVar.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
			}
			if (index != null && index != -1) {
				mdArrayIndexMap.put(dimName, index);
			}
		}
		final int dimSize = mdArrayIndexMap.size();
		if (dimSize == 1) {
			if (!this.write1DDoubleArray(ncWrite, varName, ncDimList, mdArrayIndexMap)) {
				return false;
			}
		} else if (dimSize == 2) {
			if (!this.write2DDoubleArray(ncWrite, varName, ncDimList, mdArrayIndexMap)) {
				return false;
			}
		} else if (dimSize == 3) {
			if (!this.write3DDoubleArray(ncWrite, varName, ncDimList, mdArrayIndexMap)) {
				return false;
			}
		} else {
			throw new Error("Unsupported dimension size: " + dimSize);
		}
		return true;
    }

    /**
     * Returns a map of stride for data arrays.
     * 
     * @return a map of stride for data arrays
     */
	@Override
    protected Map<String, SGIntegerSeriesSet> getStrideMap() {
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    	if (this.isIndexAvailable()) {
            map.put(this.getScatterStrideKey(), this.mIndexStride);
    	} else {
            map.put(this.getXStrideKey(), this.mXStride);
            map.put(this.getYStrideKey(), this.mYStride);
    	}
    	return map;
    }
	
    /**
     * Sets the stride of data arrays.
     * 
     * @param map
     *           the map of the stride
     */
	@Override
    public void setStrideMap(Map<String, SGIntegerSeriesSet> map) {
    	this.mXStride = map.get(this.getXStrideKey());
    	this.mYStride = map.get(this.getYStrideKey());
    	this.mIndexStride = map.get(this.getScatterStrideKey());
    }

	protected abstract String getXStrideKey();

	protected abstract String getYStrideKey();

	protected abstract String getScatterStrideKey();

    /**
     * Returns the length of x-dimension without taking into account the stride.
     * 
     * @return the length of x-dimension without taking into account the stride
     */
    public int getXDimensionLength() {
    	return this.getAllPointsNumberSub(this.mXVariable, this.getXDimensionKey());
    }
    
    /**
     * Returns the length of y-dimension without taking into account the stride.
     * 
     * @return the length of y-dimension without taking into account the stride
     */
    public int getYDimensionLength() {
    	return this.getAllPointsNumberSub(this.mYVariable, this.getYDimensionKey());
    }

	protected abstract int getAllPointsNumberSub(final SGMDArrayVariable var, String name);

	protected int getAllPointsNumberSub(final SGMDArrayVariable var, String name, 
			SGMDArrayVariable componentVar) {
		if (var != null) {
			return var.getGenericDimensionLength();
		} else {
			return componentVar.getDimensionLength(name);
		}
	}

	protected abstract double[] getValueArraySub(final boolean all, final SGMDArrayVariable var, 
			final SGIntegerSeriesSet stride, String name);

	protected double[] getValueArraySub(final boolean all, final SGMDArrayVariable var, 
			final SGIntegerSeriesSet stride, String name, SGMDArrayVariable componentVar) {
		final boolean b = (all || !this.isStrideAvailable());
		if (var != null) {
			if (b) {
				return var.getAllGenericNumberArray();
			} else {
				return var.getGenericNumberArray(stride);
			}
		} else {
			if (b) {
				final int len = componentVar.getDimensionLength(name);
				return this.getIndexValues(len);
			} else {
				return this.getIndexValues(stride);
			}
		}
	}

    /**
     * Returns whether the index is available.
     *
     * @return true if the index is available
     */
    @Override
	public boolean isIndexAvailable() {
		return (this.mIndexStride != null);
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
     * Sets the stride for index.
     *
     * @param stride
     *           stride of arrays
     */
    @Override
	public void setIndexStride(SGIntegerSeriesSet stride) {
    	if (!SGUtility.equals(this.mIndexStride, stride)) {
    		this.clearCache();
    	}
    	if (stride == null) {
    		this.mIndexStride = null;
    	} else {
    		this.mIndexStride = (SGIntegerSeriesSet) stride.clone();
    	}
	}
    
	protected boolean writeIndexData(NetcdfFileWriteable ncWrite) {
		Dimension indexDim = ncWrite.findDimension(INDEX_DIM_NAME);
		if (indexDim != null) {
			if (!this.writeSequentialIntegerNumbers(ncWrite, INDEX_DIM_NAME, indexDim.getLength())) {
				return false;
			}
		}
		return true;
	}
	
	enum EXPORT_TYPE {
		PLUGIN,
		FILE,
	}

    protected double[][] getTwoDimensionalValueArray(final double[] values, 
    		SGMDArrayVariable var, EXPORT_TYPE type, final boolean all) {
    	double[][] twoDimArray = null;
    	if (all) {
    		final int xLen = this.getXDimensionLength();
    		final int yLen = this.getYDimensionLength();
        	if (EXPORT_TYPE.FILE.equals(type)) {
        		twoDimArray = new double[xLen][yLen];
        		for (int xx = 0; xx < xLen; xx++) {
        			for (int yy = 0; yy < yLen; yy++) {
        				final int index = yy * xLen + xx;
        				twoDimArray[xx][yy] = values[index];
        			}
        		}
        	} else if (EXPORT_TYPE.PLUGIN.equals(type)) {
        		twoDimArray = new double[yLen][xLen];
        		for (int yy = 0; yy < yLen; yy++) {
        			for (int xx = 0; xx < xLen; xx++) {
        				final int index = yy * xLen + xx;
        				twoDimArray[yy][xx] = values[index];
        			}
        		}
        	}
    	} else {
        	int[] xIndices = this.mXStride.getNumbers();
        	int[] yIndices = this.mYStride.getNumbers();
        	final int xLen = xIndices.length;
        	final int yLen = yIndices.length;
        	if (EXPORT_TYPE.FILE.equals(type)) {
        		twoDimArray = new double[xLen][yLen];
        		for (int xx = 0; xx < xLen; xx++) {
        			for (int yy = 0; yy < yLen; yy++) {
        				final int index = yy * xLen + xx;
        				twoDimArray[xx][yy] = values[index];
        			}
        		}
        	} else if (EXPORT_TYPE.PLUGIN.equals(type)) {
        		twoDimArray = new double[yLen][xLen];
        		for (int yy = 0; yy < yLen; yy++) {
        			for (int xx = 0; xx < xLen; xx++) {
        				final int index = yy * xLen + xx;
        				twoDimArray[yy][xx] = values[index];
        			}
        		}
        	}
    	}
		return twoDimArray;
    }

    protected double[] getValueArray(SGMDArrayVariable var, final boolean all,
    		String xKey, String yKey) {
		int[] dims = var.getDimensions();
		if (this.isIndexAvailable()) {
			final int index = var.getGenericDimensionIndex();
			SGIntegerSeriesSet stride;
			if (!all && this.isStrideAvailable()) {
				stride = this.mIndexStride;
			} else {
				stride = SGIntegerSeriesSet.createInstance(dims[index]);
			}
			return var.getDoubleArray(index, stride);
		} else {
			final int xIndex = var.getDimensionIndex(xKey);
			final int yIndex = var.getDimensionIndex(yKey);
			SGIntegerSeriesSet xStride;
			SGIntegerSeriesSet yStride;
			if (!all && this.isStrideAvailable()) {
				xStride = this.mXStride;
				yStride = this.mYStride;
			} else {
				xStride = SGIntegerSeriesSet.createInstance(dims[xIndex]);
				yStride = SGIntegerSeriesSet.createInstance(dims[yIndex]);
			}
			final double[] array = var.getDoubleArray(xIndex, yIndex, xStride, yStride);
			return array;
		}
    }

	/**
	 * Returns true if all stride of this data are available and each string representation 
	 * is different from "0:end".
	 * 
	 * @return true if all stride are effective
	 */
	@Override
	public boolean hasEffectiveStride() {
		if (!this.isStrideAvailable()) {
			return false;
		}
		if (this.isIndexAvailable()) {
	    	SGIntegerSeriesSet stride = this.getIndexStride();
	    	return !stride.isComplete();
		} else {
	    	SGIntegerSeriesSet xStride = this.getXStride();
	    	if (!xStride.isComplete()) {
	    		return true;
	    	}
	    	SGIntegerSeriesSet yStride = this.getYStride();
	    	if (!yStride.isComplete()) {
	    		return true;
	    	}
			return false;
		}
	}

	public boolean hasEffectiveIndexStride() {
		if (!this.isStrideAvailable()) {
			return false;
		}
		if (this.isIndexAvailable()) {
	    	SGIntegerSeriesSet stride = this.getIndexStride();
	    	return !stride.isComplete();
		} else {
			return false;
		}
	}

	public boolean hasEffectiveXStride() {
		if (!this.isStrideAvailable()) {
			return false;
		}
		if (this.isIndexAvailable()) {
			return false;
		} else {
	    	SGIntegerSeriesSet xStride = this.getXStride();
	    	return !xStride.isComplete(); 
		}
	}

	public boolean hasEffectiveYStride() {
		if (!this.isStrideAvailable()) {
			return false;
		}
		if (this.isIndexAvailable()) {
			return false;
		} else {
	    	SGIntegerSeriesSet yStride = this.getYStride();
	    	return !yStride.isComplete(); 
		}
	}

	protected void getIndexList(List<SGXYSimpleDoubleValueIndexBlock> blocks,
			List<Integer> xIndexList, List<Integer> yIndexList) {
		final int xAllLen = this.getXDimensionLength();
		final int yAllLen = this.getYDimensionLength();
		SGDataUtility.getIndexList(blocks, xIndexList, yIndexList, xAllLen, yAllLen);
	}

	protected double[] getOneDimensionalValues(final double[] allValues, List<Integer> indexList) {
		final int len = indexList.size();
		double[] values = new double[len];
		for (int ii = 0; ii < len; ii++) {
			values[ii] = allValues[indexList.get(ii)];
		}
		return values;
	}
	
	@Override
	public boolean setArraySectionPropertySub(SGPropertyMap map) {
        if (this.isStrideAvailable()) {
        	if (this.isIndexAvailable()) {
        		SGIntegerSeriesSet idxStride = this.getIndexStride();
        		if (!idxStride.isComplete()) {
                	SGPropertyUtility.addQuotedStringProperty(map, COM_DATA_INDEX_ARRAY_SECTION, 
                			idxStride.toString());
        		}
        	} else {
        		SGIntegerSeriesSet xStride = this.getXStride();
        		if (!xStride.isComplete()) {
                	SGPropertyUtility.addQuotedStringProperty(map, COM_DATA_X_ARRAY_SECTION, 
                			xStride.toString());
        		}
        		SGIntegerSeriesSet yStride = this.getYStride();
        		if (!yStride.isComplete()) {
                	SGPropertyUtility.addQuotedStringProperty(map, COM_DATA_Y_ARRAY_SECTION, 
                			yStride.toString());
        		}
        	}
        }
		return true;
	}
	
	@Override
	public boolean useXYCache(final boolean all, SGIntegerSeriesSet xyStride) {
		if (!all) {
			return true;
		}
		if (!this.isStrideAvailable()) {
			return true;
		}
		final SGIntegerSeriesSet stride;
		if (this.isIndexAvailable()) {
			stride = this.mIndexStride;
		} else {
			stride = xyStride;
		}
		return stride.isComplete();
	}

	@Override
	public boolean useComponentIndexCache(final boolean all) {
		if (!this.isIndexAvailable()) {
			return false;
		}
		if (!all) {
			return true;
		}
		if (!this.isStrideAvailable()) {
			return true;
		}
		return this.mIndexStride.isComplete();
	}

    protected String getXYToolTipSpatiallyVaried(final int xIndex, final int yIndex) {
    	StringBuffer sb = new StringBuffer();
    	if (this.mXVariable != null) {
    		sb.append(this.getToolTipSpatiallyVaried(this.mXVariable, xIndex));
    	}
    	if (this.mYVariable != null) {
        	if (this.mXVariable != null) {
        		sb.append(",<br>");
        	}
    		sb.append(this.getToolTipSpatiallyVaried(this.mYVariable, yIndex));
    	}
    	return sb.toString();
    }

	protected String getComponentToolTipSpatiallyVaried(SGMDArrayVariable var, 
			final String xKey, final String yKey, final int xIndex, final int yIndex) {
		StringBuffer sb = new StringBuffer();
    	if (var != null) {
    		int[] origins = var.getOrigins();
    		final int xDim = var.getDimensionIndex(xKey);
    		final int yDim = var.getDimensionIndex(yKey);
    		origins[xDim] = xIndex;
    		origins[yDim] = yIndex;
    		String str = this.getToolTip(var, origins);
    		sb.append(str);
    	}
    	return sb.toString();
	}

    protected SGTwoDimensionalArrayIndex getDataViewerCellX(SGTwoDimensionalArrayIndex cell,
    		final boolean bStride) {
		SGTwoDimensionalArrayIndex ret;
    	if (bStride) {
    		SGIntegerSeriesSet stride = this.getXStride();
    		int[] indices = stride.getNumbers();
    		final int row = Arrays.binarySearch(indices, cell.getColumn());
    		ret = new SGTwoDimensionalArrayIndex(0, row);
    	} else {
    		ret = new SGTwoDimensionalArrayIndex(0, cell.getColumn());
    	}
    	return ret;
    }

    protected SGTwoDimensionalArrayIndex getDataViewerCellY(SGTwoDimensionalArrayIndex cell,
    		final boolean bStride) {
		SGTwoDimensionalArrayIndex ret;
    	if (bStride) {
    		SGIntegerSeriesSet stride = this.getYStride();
    		int[] indices = stride.getNumbers();
    		final int row = Arrays.binarySearch(indices, cell.getRow());
    		ret = new SGTwoDimensionalArrayIndex(0, row);
    	} else {
    		ret = new SGTwoDimensionalArrayIndex(0, cell.getRow());
    	}
    	return ret;
    }
    
	private Map<Integer, Double> getEditedValueMap(SGMDArrayVariable var,
			String xColumnType, String yColumnType, String[] valueColumnTypes,
			final boolean dimReversed) {
		
		String varName = var.getName();
		int[] dims = var.getDimensions();
		int[] originArray = var.getOrigins();
		
		Map<Integer, Double> valueMap = new HashMap<Integer, Double>();

		for (int ii = 0; ii < this.mEditedDataValueList.size(); ii++) {
			SGDataValueHistory.MDArray dataValue 
					= (SGDataValueHistory.MDArray) this.mEditedDataValueList.get(ii);
			if (!varName.equals(dataValue.getVarName())) {
				continue;
			}
			String columnType = dataValue.getColumnType();
			int animationDimension = dataValue.getAnimationDimension();
			int animationDimIndex = dataValue.getAnimationDimIndex();
			int[] origins = originArray.clone();
			if (animationDimension != -1 && animationDimIndex != -1) {
				origins[animationDimension] = animationDimIndex;
			}

			final int arrayIndex;
			final double value;

			if (this.isIndexAvailable()) {
				SGDataValueHistory.MDArray.D1 dataValueDim1 = (SGDataValueHistory.MDArray.D1) dataValue;
				final int dimension = dataValueDim1.getDimension();
				final int index = dataValueDim1.getIndex();
				
				final int[] factors = this.getFactors(dims, dimReversed);
				final int factor = factors[dimension];
				
				int offset = 0;
				for (int jj = 0; jj < dims.length; jj++) {
					if (jj == dimension) {
						continue;
					}
					offset += factors[jj] * origins[jj];
				}
				arrayIndex = factor * index + offset;
				value = dataValue.getValue();

			} else {
				
				if (SGUtility.contains(valueColumnTypes, columnType)) {
					SGDataValueHistory.MDArray.D2 dataValueDim2 = (SGDataValueHistory.MDArray.D2) dataValue;

					final int xDimension = dataValueDim2.getXDimension();
					final int xDimIndex = dataValueDim2.getXIndex();
					
					final int yDimension = dataValueDim2.getYDimension();
					final int yDimIndex = dataValueDim2.getYIndex();

					final int[] factors = this.getFactors(dims, dimReversed);
					final int xFactor = factors[xDimension];
					final int yFactor = factors[yDimension];
					
					int offset = 0;
					for (int jj = 0; jj < dims.length; jj++) {
						if (jj == xDimension || jj == yDimension) {
							continue;
						}
						offset += factors[jj] * origins[jj];
					}
					final int offsetNew = yFactor * yDimIndex + offset;
					arrayIndex = xFactor * xDimIndex + offsetNew;
					value = dataValue.getValue();
					
				} else if (xColumnType.equals(columnType) || yColumnType.equals(columnType)) {
					
					SGDataValueHistory.MDArray.D1 dataValueDim1 = (SGDataValueHistory.MDArray.D1) dataValue;
					final int dimension = dataValueDim1.getDimension();
					final int index = dataValueDim1.getIndex();
					
					final int[] factors = this.getFactors(dims, dimReversed);
					final int factor = factors[dimension];
					
					int offset = 0;
					for (int jj = 0; jj < dims.length; jj++) {
						if (jj == dimension) {
							continue;
						}
						offset += factors[jj] * origins[jj];
					}
					arrayIndex = factor * index + offset;
					value = dataValue.getValue();
					
				} else {
					throw new Error("Invalid column type: " + columnType);
				}
			}
			
			valueMap.put(arrayIndex, value);
		}

		return valueMap;
    }

    protected MDDoubleArray setEditedValues(IHDF5Writer writer,
    		SGMDArrayVariable var, MDDoubleArray array, String xColumnType,
    		String yColumnType, String[] valueColumnTypes) {

		// get edited values
		Map<Integer, Double> valueMap = this.getEditedValueMap(var, xColumnType, 
				yColumnType, valueColumnTypes, true);
		
		// set to the array
		Iterator<Entry<Integer, Double>> itr = valueMap.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Integer, Double> entry = itr.next();
			final int arrayIndex = entry.getKey();
			final double value = entry.getValue();
			array.setToObject(value, arrayIndex);
		}
		
    	return array;
	}

	protected MLDouble setEditedValues(SGMDArrayVariable var, MLDouble array,
			String xColumnType, String yColumnType, String[] valueColumnTypes) {

		int[] dims = var.getDimensions();
		int size = 1;
		for (int jj = 0; jj < dims.length; jj++) {
			size *= dims[jj];
		}
		
		// copies value
		String varName = var.getName();
		MLDouble ret = new MLDouble(varName, dims);
		for (int jj = 0; jj < size; jj++) {
			final double value = var.getDoubleValue(jj);
			ret.set(value, jj);
		}

		// get edited values
		Map<Integer, Double> valueMap = this.getEditedValueMap(var, xColumnType, 
				yColumnType, valueColumnTypes, false);
		
		// set to the array
		Iterator<Entry<Integer, Double>> itr = valueMap.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Integer, Double> entry = itr.next();
			final int arrayIndex = entry.getKey();
			final double value = entry.getValue();
			ret.set(value, arrayIndex);
		}

    	return ret;
    }

}
