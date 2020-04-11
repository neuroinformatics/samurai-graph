package jp.riken.brain.ni.samuraigraph.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGNamedStringBlock;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.Value;

import org.w3c.dom.Element;

import ucar.ma2.Array;
import ucar.ma2.ArrayChar;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

/**
 * The base class of two dimensional NetCDF data.
 *
 */
public abstract class SGTwoDimensionalNetCDFData extends SGNetCDFData
		implements SGITwoDimensionalData, SGIDataColumnTypeConstants, SGIDataPropertyKeyConstants, 
		SGINetCDFConstants {

    /**
     * The variable for x-values.
     */
    protected SGNetCDFVariable mXVariable = null;
    
    /**
     * The variable for y-values.
     */
    protected SGNetCDFVariable mYVariable = null;

    /**
     * The variable of the index for x-values.
     */
    protected SGNetCDFVariable mXIndexVariable = null;

    /**
     * The variable of the index for y-values.
     */
    protected SGNetCDFVariable mYIndexVariable = null;

    /**
     * The stride for x-values.
     */
    protected SGIntegerSeriesSet mXStride = null;

    /**
     * The stride for y-values.
     */
    protected SGIntegerSeriesSet mYStride = null;

    /**
     * The default constructor.
     */
	public SGTwoDimensionalNetCDFData() {
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
     * @param timeInfo
     *            information of time values
     * @param indexInfo
     *            information of index values
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
	public SGTwoDimensionalNetCDFData(final SGNetCDFFile ncfile, final SGDataSourceObserver obs,
			final SGNetCDFDataColumnInfo xInfo, final SGNetCDFDataColumnInfo yInfo,
			final SGNetCDFDataColumnInfo timeInfo, SGNetCDFDataColumnInfo indexInfo,
			final SGNetCDFDataColumnInfo xIndexInfo,
			final SGNetCDFDataColumnInfo yIndexInfo,
			final SGIntegerSeriesSet indexStride, 
			final SGIntegerSeriesSet xStride, final SGIntegerSeriesSet yStride,
			final boolean strideAvailable) {
		
		super(ncfile, obs, timeInfo, indexInfo, indexStride, strideAvailable);
		
        if (xInfo == null || yInfo== null) {
            throw new IllegalArgumentException("xInfo == null || yInfo == null");
        }
        
        // get variables
        SGNetCDFVariable xVar = ncfile.findVariable(xInfo.getName());
        if (xVar == null) {
            throw new IllegalArgumentException("Illegal variable name: " + xInfo.getName());
        }
        SGNetCDFVariable yVar = ncfile.findVariable(yInfo.getName());
        if (yVar == null) {
            throw new IllegalArgumentException("Illegal variable name: " + yInfo.getName());
        }

   		SGNetCDFVariable xIndexVar = null;
       	if (xIndexInfo != null) {
       		xIndexVar = this.findIndexVariable(ncfile, xIndexInfo);
       	}
   		SGNetCDFVariable yIndexVar = null;
       	if (yIndexInfo != null) {
       		yIndexVar = this.findIndexVariable(ncfile, yIndexInfo);
       	}

        //
        // check variables
        //
        
        Dimension timeDim = null;
        if (this.mTimeVariable != null) {
        	timeDim = this.mTimeVariable.getDimension(0);
        }
        if (this.isIndexAvailable()) {
        	// index variable
            Dimension indexDim = this.mIndexVariable.getDimension(0);
            if (indexDim.equals(timeDim)) {
                throw new IllegalArgumentException(
                	"The index dimension must not be equal to the time dimension.");
            }
            this.checkNonCoordinateVariable(xVar, indexDim);
            this.checkNonCoordinateVariable(yVar, indexDim);
        } else {
        	Dimension xDim, yDim;
        	if (xIndexInfo != null) {
        		xDim = xIndexVar.getDimension(0);
                this.checkNonCoordinateVariable(xVar, xDim);
        	} else {
        		if (!xVar.isCoordinateVariable()) {
                    throw new IllegalArgumentException("X variable must be a coordinate variable: " + xVar.getName());
        		}
                xDim = xVar.getDimension(0);
        	}
        	if (yIndexInfo != null) {
        		yDim = yIndexVar.getDimension(0);
                this.checkNonCoordinateVariable(yVar, yDim);
        	} else {
        		if (!yVar.isCoordinateVariable()) {
                    throw new IllegalArgumentException("X variable must be a coordinate variable: " + xVar.getName());
        		}
                yDim = yVar.getDimension(0);
        	}
            if (xDim.equals(timeDim) || yDim.equals(timeDim)) {
                throw new IllegalArgumentException(
    			    "Dimensions for x or y variables must not be equal to the time dimension.");
            }
        }
        
        // set to attributes
        this.mXVariable = xVar;
        this.mYVariable = yVar;
        this.mXIndexVariable = xIndexVar;
        this.mYIndexVariable = yIndexVar;
        if (!this.isIndexAvailable()) {
            Dimension xDim = xVar.getDimension(0);
            this.mXStride = this.createStride(xStride, xDim);
            Dimension yDim = yVar.getDimension(0);
            this.mYStride = this.createStride(yStride, yDim);
        }
	}
	
	protected Dimension getXDimension() {
    	SGNetCDFVariable var;
        if (this.mXIndexVariable != null) {
        	var = this.mXIndexVariable;
        } else {
        	var = this.mXVariable;
        }
        return var.getDimension(0);
	}

	protected Dimension getYDimension() {
    	SGNetCDFVariable var;
        if (this.mYIndexVariable != null) {
        	var = this.mYIndexVariable;
        } else {
        	var = this.mYVariable;
        }
        return var.getDimension(0);
	}

    /**
     * Disposes of this data object.
     * Do not call any method of this object after called this method.
     */
	@Override
    public void dispose() {
        super.dispose();
        this.mXVariable = null;
        this.mYVariable = null;
        this.mXIndexVariable = null;
        this.mYIndexVariable = null;
        this.mXStride = null;
        this.mYStride = null;
    }

    /**
     * Returns the copy of this data object.
     * 
     * @return
     *         a copy of this data object
     */
	@Override
    public Object clone() {
		SGTwoDimensionalNetCDFData data = (SGTwoDimensionalNetCDFData) super.clone();
        data.mXStride = this.getXStride();
        data.mYStride = this.getYStride();
        return data;
    }

    /**
     * Returns the length of x-dimension without taking into account the stride.
     * 
     * @return the length of x-dimension without taking into account the stride
     */
    public int getXDimensionLength() {
    	return this.mXVariable.getDimension(0).getLength();
    }
    
    /**
     * Returns the length of y-dimension without taking into account the stride.
     * 
     * @return the length of y-dimension without taking into account the stride
     */
    public int getYDimensionLength() {
    	return this.mYVariable.getDimension(0).getLength();
    }
    
    /**
     * Returns the number of data points without taking into account the stride.
     * 
     * @return the number of data points without taking into account the stride
     */
    @Override
    public int getAllPointsNumber() {
    	if (this.isIndexAvailable()) {
    		return this.getIndexDimensionLength();
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
        return this.getNameWithUnit(this.mXVariable);
    }

    /**
     * Returns the title for the Y-axis.
     * 
     * @return the title for the Y-axis
     */
	@Override
    public String getTitleY() {
        return this.getNameWithUnit(this.mYVariable);
    }

    /**
     * Returns the stride for x-values.
     * 
     * @return the stride
     */
    public SGIntegerSeriesSet getXStride() {
    	if (this.isIndexAvailable()) {
    		return null;
    	} else {
    		if (this.mXStride != null) {
            	return (SGIntegerSeriesSet) this.mXStride.clone();
    		} else {
    			return null;
    		}
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
    	if (stride == null) {
    		this.mXStride = null;
    	} else {
        	this.mXStride = (SGIntegerSeriesSet) stride.clone();
    	}
    }

    /**
     * Returns the stride for y-values.
     * 
     * @return the stride
     */
    public SGIntegerSeriesSet getYStride() {
    	if (this.isIndexAvailable()) {
    		return null;
    	} else {
    		if (this.mYStride != null) {
            	return (SGIntegerSeriesSet) this.mYStride.clone();
    		} else {
    			return null;
    		}
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
    	if (stride == null) {
    		this.mYStride = null;
    	} else {
        	this.mYStride = (SGIntegerSeriesSet) stride.clone();
    	}
    }

	@Override
    protected double[] arrangeValueArray(Array data, SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, SGIntegerSeries> dimensionStrideMap) {
    	if (this.isIndexAvailable()) {
    		return this.arrange1Dim(data);
    	} else {
    		if (var.equals(this.mXVariable) || var.equals(this.mYVariable)) {
        		return this.arrange1Dim(data);
    		} else {
                return this.arrange2Dim(data, var, cVars, dimensionStrideMap);
    		}
    	}
    }

	@Override
	protected double[] arrangeValueArray(SGNetCDFVariable var, List<SGNamedDoubleValueIndexBlock> blockList) {
		if (this.isIndexAvailable()) {
    		return this.arrangeValueArray1Dim(blockList, this.mIndexVariable, 
    				this.getIndexStrideInstance());
		} else {
    		if (var.equals(this.mXVariable)) {
        		return this.arrangeValueArray1Dim(blockList, var, 
        				this.getXIndexStrideInstance());
    		} else if (var.equals(this.mYVariable)) {
        		return this.arrangeValueArray1Dim(blockList, var, 
        				this.getYIndexStrideInstance());
    		} else {
    			return this.arrangeValueArray2Dim(blockList,
    					this.mXVariable, this.mYVariable,
    					this.getXStrideInstance(), this.getYStrideInstance());
    		}
		}
	}

    protected SGIntegerSeriesSet getXStrideInstance() {
    	return this.getStrideInstance(this.mXStride, this.mXVariable);
    }

    protected SGIntegerSeriesSet getYStrideInstance() {
    	return this.getStrideInstance(this.mYStride, this.mYVariable);
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
        if (!(data instanceof SGTwoDimensionalNetCDFData)) {
            throw new IllegalArgumentException("Given data is not the instance of SGTwoDimensionalNetCDFData class.");
        }
        if (super.setData(data) == false) {
            return false;
        }
        SGTwoDimensionalNetCDFData nData = (SGTwoDimensionalNetCDFData) data;
        this.mXVariable = nData.mXVariable;
        this.mYVariable = nData.mYVariable;
        this.mXIndexVariable = nData.mXIndexVariable;
        this.mYIndexVariable = nData.mYIndexVariable;
        if (!nData.isIndexAvailable()) {
            this.setXStride(nData.mXStride);
            this.setYStride(nData.mYStride);
        }
        return true;
    }

    /**
     * Returns a map of stride of dimensions. The keys are the name of dimensions.
     * 
     * @return a map of stride of dimensions
     */
	@Override
    protected Map<String, SGIntegerSeriesSet> getDimensionStrideMap() {
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    	if (this.isIndexAvailable()) {
        	map.put(this.mIndexVariable.getName(), this.getIndexStrideInstance());
    	} else {
    		String xName = this.isXIndexAvailable() ? this.mXIndexVariable.getName() : this.mXVariable.getName();
    		String yName = this.isYIndexAvailable() ? this.mYIndexVariable.getName() : this.mYVariable.getName();
        	map.put(xName, this.getXStrideInstance());
        	map.put(yName, this.getYStrideInstance());
    	}
    	return map;
    }

//    /**
//     * Returns an array of X-values.
//     * 
//     * @return an array of X-values
//     */
//    public double[] getXValueArray(final boolean all) {
//        if (this.isIndexAvailable()) {
//            return this.getValueArray(this.mXVariable, new SGNetCDFVariable[]{ this.mIndexVariable }, all);
//        } else {
//            return this.getCoordinateValueArray(this.mXVariable, this.getXStrideInstance(), all);
//        }
//    }
//
//    /**
//     * Returns an array of Y-values.
//     * 
//     * @return an array of Y-values
//     */
//    public double[] getYValueArray(final boolean all) {
//        if (this.isIndexAvailable()) {
//            return this.getValueArray(this.mYVariable, new SGNetCDFVariable[]{ this.mIndexVariable }, all);
//        } else {
//            return this.getCoordinateValueArray(this.mYVariable, this.getYStrideInstance(), all);
//        }
//    }

    /**
	 * Returns an array of xy-values.
	 * 
	 * @return an array of xy-values
	 */
    public SGTuple2d[] getXYValueArray(final boolean all) {
        final double[] xArray = this.getXValueArray(all);
        final double[] yArray = this.getYValueArray(all);
        if (this.isIndexAvailable()) {
        	final int len = xArray.length;
            SGTuple2d[] cArray = new SGTuple2d[len];
            for (int ii = 0; ii < len; ii++) {
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
    public SGNetCDFVariable getXVariable() {
    	return this.mXVariable;
    }

    /**
     * Returns the variable for y-values.
     * 
     * @return the variable for y-values
     */
    public SGNetCDFVariable getYVariable() {
    	return this.mYVariable;
    }

    /**
     * Returns an array of coordinate variables.
     * 
     * @return an array of coordinate variables
     */
    protected SGNetCDFVariable[] getCoordinateVariables() {
    	List<SGNetCDFVariable> varList = new ArrayList<SGNetCDFVariable>();
    	if (this.isIndexAvailable()) {
    		varList.add(this.mIndexVariable);
    	} else {
    		if (this.isXIndexAvailable()) {
    			varList.add(this.mXIndexVariable);
    		} else {
    			varList.add(this.mXVariable);
    		}
    		if (this.isYIndexAvailable()) {
    			varList.add(this.mYIndexVariable);
    		} else {
    			varList.add(this.mYVariable);
    		}
    	}
    	return varList.toArray(new SGNetCDFVariable[varList.size()]);
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
        
        if (this.isXIndexAvailable()) {
            el.setAttribute(SGIDataPropertyKeyConstants.KEY_X_INDEX_VARIABLE_NAME, this.mXIndexVariable.getValidName());
        }
        if (this.isYIndexAvailable()) {
            el.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_INDEX_VARIABLE_NAME, this.mYIndexVariable.getValidName());
        }

        // stride
        if (!this.isIndexAvailable()) {
            el.setAttribute(KEY_X_ARRAY_SECTION, this.mXStride.toString());
            el.setAttribute(KEY_Y_ARRAY_SECTION, this.mYStride.toString());
        }

        return true;
    }

	@Override
	protected String[] arrangeStringArray(ArrayChar data, SGNetCDFVariable var,
			SGNetCDFVariable[] cVars, Map<String, SGIntegerSeries> dimensionStrideMap) {
		// always returns null
		return null;
	}

	@Override
	protected String[] arrangeStringArray(List<SGNamedStringBlock> blockList) {
		// always returns null
		return null;
	}

    protected List<SGXYSimpleDoubleValueIndexBlock> getValueBlockList(
    		SGNetCDFVariable var, final boolean all, 
    		final boolean removeInvalidValues) {
    	// returns cached values if it exists
    	SGNetCDFVariable xVar = this.isXIndexAvailable() ? this.mXIndexVariable : this.mXVariable;
    	SGNetCDFVariable yVar = this.isYIndexAvailable() ? this.mYIndexVariable : this.mYVariable;
    	List<SGXYSimpleDoubleValueIndexBlock> blockList = this.get2DValueBlockList(
    			var, xVar, yVar, all, removeInvalidValues);
        return blockList;
    }

    /**
     * A class of two-dimensional NetCDF data properties.
     */
    public static abstract class TwoDimensionalNetCDFDataProperties extends NetCDFDataProperties {

        protected String xName = null;
        
        protected String yName = null;
        
        protected String xIndexName = null;

        protected String yIndexName = null;

        protected SGIntegerSeriesSet xStride = null;
        
        protected SGIntegerSeriesSet yStride = null;
        
        /**
         * The default constructor.
         */
        public TwoDimensionalNetCDFDataProperties() {
            super();
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.xName = null;
            this.yName = null;
            this.xIndexName = null;
            this.yIndexName = null;
            this.xStride = null;
            this.yStride = null;
        }
        
    	@Override
    	public boolean equals(Object obj) {
            if ((obj instanceof TwoDimensionalNetCDFDataProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            return true;
        }
        
    	@Override
        public boolean hasEqualSize(DataProperties dp) {
            if ((dp instanceof TwoDimensionalNetCDFDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualSize(dp)) {
            	return false;
            }
            TwoDimensionalNetCDFDataProperties p = (TwoDimensionalNetCDFDataProperties) dp;
            if (!SGUtility.equals(this.xStride, p.xStride)) {
                return false;
            }
            if (!SGUtility.equals(this.yStride, p.yStride)) {
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
            if ((dp instanceof TwoDimensionalNetCDFDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualColumnTypes(dp)) {
            	return false;
            }
            TwoDimensionalNetCDFDataProperties p = (TwoDimensionalNetCDFDataProperties) dp;
            if (SGUtility.equals(this.xName, p.xName) == false) {
                return false;
            }
            if (SGUtility.equals(this.yName, p.yName) == false) {
                return false;
            }
            if (SGUtility.equals(this.xIndexName, p.xIndexName) == false) {
                return false;
            }
            if (SGUtility.equals(this.yIndexName, p.yIndexName) == false) {
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
        	TwoDimensionalNetCDFDataProperties p = (TwoDimensionalNetCDFDataProperties) super.copy();
        	p.xStride = (this.xStride != null) ? (SGIntegerSeriesSet) this.xStride.clone() : null;
        	p.yStride = (this.yStride != null) ? (SGIntegerSeriesSet) this.yStride.clone() : null;
        	return p;
        }
    }

    /**
     * Set properties to this data.
     * 
     * @param p
     *          properties to be set
     * @return true if succeeded
     */
    public boolean setProperties(SGProperties p) {
        if (!(p instanceof TwoDimensionalNetCDFDataProperties)) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        TwoDimensionalNetCDFDataProperties np = (TwoDimensionalNetCDFDataProperties) p;
        SGNetCDFFile ncfile = this.getNetcdfFile();
        this.mXVariable = ncfile.findVariable(np.xName);
        this.mYVariable = ncfile.findVariable(np.yName);
        this.mXIndexVariable = ncfile.findVariable(np.xIndexName);
        this.mYIndexVariable = ncfile.findVariable(np.yIndexName);
        this.setXStride(np.xStride);
        this.setYStride(np.yStride);
        return true;
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
        if (!(p instanceof TwoDimensionalNetCDFDataProperties)) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        TwoDimensionalNetCDFDataProperties sp = (TwoDimensionalNetCDFDataProperties) p;
        sp.xName = this.mXVariable.getName();
        sp.yName = this.mYVariable.getName();
        sp.xIndexName = (this.mXIndexVariable != null) ? this.mXIndexVariable.getName() : null;
        sp.yIndexName = (this.mYIndexVariable != null) ? this.mYIndexVariable.getName() : null;
        sp.xStride = this.getXStride();
        sp.yStride = this.getYStride();
        return true;
    }

    /**
     * Returns whether the variable of x-index is available.
     * 
     * @return true if the variable of x-index is available
     */
    public boolean isXIndexAvailable() {
    	return (this.mXIndexVariable != null);
    }

    /**
     * Returns whether the variable of y-index is available.
     * 
     * @return true if the variable of y-index is available
     */
    public boolean isYIndexAvailable() {
    	return (this.mYIndexVariable != null);
    }

    /**
     * Returns the variable of x-index.
     * 
     * @return the variable of x-index
     */
    public SGNetCDFVariable getXIndexVariable() {
    	return this.mXIndexVariable;
    }

    /**
     * Returns the variable of y-index.
     * 
     * @return the variable of y-index
     */
    public SGNetCDFVariable getYIndexVariable() {
    	return this.mYIndexVariable;
    }
    
    protected SGIntegerSeriesSet getXIndexStrideInstance() {
    	if (this.isXIndexAvailable()) {
        	return this.getStrideInstance(this.mXStride, this.mXIndexVariable);
    	} else {
    		return null;
    	}
    }

    protected SGIntegerSeriesSet getYIndexStrideInstance() {
    	if (this.isYIndexAvailable()) {
        	return this.getStrideInstance(this.mYStride, this.mYIndexVariable);
    	} else {
    		return null;
    	}
    }
    
    protected double[][] getGridValueArray(final double[] flatArray, final boolean all) {
		final int xLen, yLen;
    	if (all || !this.isStrideAvailable()) {
        	xLen = this.getXDimensionLength();
        	yLen = this.getYDimensionLength();
    	} else {
        	xLen = this.mXStride.getLength();
        	yLen = this.mYStride.getLength();
    	}
		double[][] twoDimArray = new double[yLen][xLen];
		for (int yy = 0; yy < yLen; yy++) {
			for (int xx = 0; xx < xLen; xx++) {
				final int index = yy * xLen + xx;
				twoDimArray[yy][xx] = flatArray[index];
			}
		}
		return twoDimArray;
    }
    
    protected double[] getFlatValueArray(final double[][] twoDimArray, final boolean all) {
		final int xLen, yLen;
    	if (all || !this.isStrideAvailable()) {
        	xLen = this.getXDimensionLength();
        	yLen = this.getYDimensionLength();
    	} else {
        	xLen = this.mXStride.getLength();
        	yLen = this.mYStride.getLength();
    	}
    	final int num = xLen * yLen;
		double[] flatArray = new double[num];
		for (int yy = 0; yy < yLen; yy++) {
			for (int xx = 0; xx < xLen; xx++) {
				final int index = yy * xLen + xx;
				flatArray[index] = twoDimArray[yy][xx];
			}
		}
		return flatArray;
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
	    	SGIntegerSeriesSet xStride = this.getXStride();;
	    	if (!xStride.isComplete()) {
	    		return true;
	    	}
	    	SGIntegerSeriesSet yStride = this.getYStride();;
	    	if (!yStride.isComplete()) {
	    		return true;
	    	}
			return false;
		}
	}
	
	protected void getIndexList(List<SGXYSimpleDoubleValueIndexBlock> blocks,
			List<Integer> xIndexList, List<Integer> yIndexList) {
		final int xAllLen = this.getXDimensionLength();
		final int yAllLen = this.getYDimensionLength();
		SGDataUtility.getIndexList(blocks, xIndexList, yIndexList, xAllLen, yAllLen);
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

	protected double[] getXYValueArray(final boolean all, SGNetCDFVariable xyVar,
			SGNetCDFVariable xyCounterVar, SGIntegerSeriesSet indices,
			final boolean removeInvalidValues) {
		double[] ret = null;
        if (this.isIndexAvailable()) {
            ret = this.getValueArray(xyVar, new SGNetCDFVariable[]{ this.mIndexVariable }, 
            		all, removeInvalidValues);
        } else {
            if (xyVar.isCoordinateVariable()) {
                ret = this.getCoordinateValueArray(xyVar, indices, all,
                		removeInvalidValues);
            } else {
                ret = this.getValueArray(xyVar, new SGNetCDFVariable[]{ xyCounterVar }, 
                		all, removeInvalidValues);
            }
        }
        return ret;
	}
	
    protected String getIndexToolTipSpatiallyVaried(final int index) {
		if (!this.isIndexAvailable()) {
			throw new Error("Unsupported case.");
		}
		SGNetCDFVariable cVar = this.mIndexVariable;
		Dimension dim = cVar.getDimension(0);
		String name = dim.getName();
		Double value = null;
		try {
			value = this.getCoordinateValue(name, index);
		} catch (IOException e) {
		}
		
		// append to the string buffer
		StringBuffer sb = new StringBuffer();
		sb.append(dim.getName());
		sb.append('=');
		sb.append(index);
		if (value != null) {
			sb.append(" (");
			sb.append(value);
			sb.append(")");
		}
    	return sb.toString();
	}

    protected String getXYToolTipSpatiallyVaried(final int xIndex, final int yIndex) {
		if (this.isIndexAvailable()) {
			throw new Error("Unsupported case.");
		}
		Dimension xDim = this.mXVariable.getDimension(0);
		String xName = xDim.getName();
		Double xValue = null;
		try {
			xValue = this.getCoordinateValue(xName, xIndex);
		} catch (IOException e) {
		}
		Dimension yDim = this.mYVariable.getDimension(0);
		String yName = yDim.getName();
		Double yValue = null;
		try {
			yValue = this.getCoordinateValue(yName, yIndex);
		} catch (IOException e) {
		}
		
		// append to the string buffer
		StringBuffer sb = new StringBuffer();
		sb.append(xName);
		sb.append('=');
		sb.append(xIndex);
		if (xValue != null) {
			sb.append(" (");
			sb.append(xValue);
			sb.append(")");
		}
		sb.append(",<br>");
		sb.append(yName);
		sb.append('=');
		sb.append(yIndex);
		if (yValue != null) {
			sb.append(" (");
			sb.append(yValue);
			sb.append(")");
		}
    	return sb.toString();
    }

    protected Value getValueAt(SGNetCDFVariable var, final int col, final int row) {
    	SGNetCDFVariable[] cVars = this.getCoordinateVariables();
    	int[] indices;
    	if (cVars[0].getName().equals(this.mXVariable.getName())) {
    		indices = new int[] { col, row };
    	} else {
    		indices = new int[] { row, col };
    	}
        final double value = this.getValueAt(var, cVars, indices);
		return new Value(value, true);
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

    protected Array setEditedValues(NetcdfFileWriteable ncWrite,
    		String varName, Array array, String xColumnType, String yColumnType,
    		String[] valueColumnTypes, final boolean all) {
		
		for (int ii = 0; ii < this.mEditedDataValueList.size(); ii++) {
			SGDataValueHistory.NetCDF dataValue 
					= (SGDataValueHistory.NetCDF) this.mEditedDataValueList.get(ii);
			if (!varName.equals(dataValue.getVarName())) {
				continue;
			}

			Variable var = ncWrite.findVariable(varName);
			List<Dimension> dims = var.getDimensions();
			
			String columnType = dataValue.getColumnType();
			
			final String animationDimName = dataValue.getAnimationDimName();
			int animationDimOrder = -1;
			int animationDimIndex = -1;
			if (animationDimName != null) {
				animationDimOrder = this.findDimensionOrder(dims,
						animationDimName);
				animationDimIndex = dataValue.getAnimationDimIndex();
			}

			Index idx = array.getIndex();
			if (this.isIndexAvailable()) {
				SGDataValueHistory.NetCDF.D1 dataValueDim1 = (SGDataValueHistory.NetCDF.D1) dataValue;
				final String indexDimName = dataValueDim1.getIndexDimName();
				final int indexDimOrder = this.findDimensionOrder(dims, indexDimName);
				int indexDimIndex = dataValueDim1.getIndex();
				if (!all && this.isStrideAvailable()) {
					int[] numbers = this.mIndexStride.getNumbers();
					indexDimIndex = Arrays.binarySearch(numbers, indexDimIndex);
				}
				if (animationDimOrder == -1) {
					idx = idx.set(indexDimIndex);
				} else {
					if (indexDimOrder < animationDimOrder) {
						idx = idx.set(indexDimIndex, animationDimIndex);
					} else {
						idx = idx.set(animationDimIndex, indexDimIndex);
					}
				}
				
			} else {
				
				if (SGUtility.contains(valueColumnTypes, columnType)) {
					SGDataValueHistory.NetCDF.D2 dataValueDim2 = (SGDataValueHistory.NetCDF.D2) dataValue;
					final String xDimName = dataValueDim2.getXDimName();
					final int xDimOrder = this.findDimensionOrder(dims, xDimName);
					int xDimIndex = dataValueDim2.getXIndex();
					if (!all && this.isStrideAvailable()) {
						int[] numbers = this.mXStride.getNumbers();
						xDimIndex = Arrays.binarySearch(numbers, xDimIndex);
					}
					final String yDimName = dataValueDim2.getYDimName();
					final int yDimOrder = this.findDimensionOrder(dims, yDimName);
					int yDimIndex = dataValueDim2.getYIndex();
					if (!all && this.isStrideAvailable()) {
						int[] numbers = this.mYStride.getNumbers();
						yDimIndex = Arrays.binarySearch(numbers, yDimIndex);
					}
					if (animationDimOrder == -1) {
						if (xDimOrder < yDimOrder) {
							idx = idx.set(xDimIndex, yDimIndex);
						} else {
							idx = idx.set(yDimIndex, xDimIndex);
						}
					} else {
						if (xDimOrder < yDimOrder) {
							if (animationDimOrder < xDimOrder) {
								idx = idx.set(animationDimIndex, xDimIndex, yDimIndex);
							} else {
								if (animationDimOrder < yDimOrder) {
									idx = idx.set(xDimIndex, animationDimIndex, yDimIndex);
								} else {
									idx = idx.set(xDimIndex, yDimIndex, animationDimIndex);
								}
							}
						} else {
							if (animationDimOrder < yDimOrder) {
								idx = idx.set(animationDimIndex, yDimIndex, xDimIndex);
							} else {
								if (animationDimOrder < xDimOrder) {
									idx = idx.set(yDimIndex, animationDimIndex, xDimIndex);
								} else {
									idx = idx.set(yDimIndex, xDimIndex, animationDimIndex);
								}
							}
						}
					}
					
				} else if (xColumnType.equals(columnType) ) {
					SGDataValueHistory.NetCDF.D1 dataValueDim1 = (SGDataValueHistory.NetCDF.D1) dataValue;
					int dimIndex = dataValueDim1.getIndex();
					if (!all && this.isStrideAvailable()) {
						int[] numbers = this.mXStride.getNumbers();
						dimIndex = Arrays.binarySearch(numbers, dimIndex);
					}
					idx = idx.set(dimIndex);
				} else if (yColumnType.equals(columnType)) {
					SGDataValueHistory.NetCDF.D1 dataValueDim1 = (SGDataValueHistory.NetCDF.D1) dataValue;
					int dimIndex = dataValueDim1.getIndex();
					if (!all && this.isStrideAvailable()) {
						int[] numbers = this.mYStride.getNumbers();
						dimIndex = Arrays.binarySearch(numbers, dimIndex);
					}
					idx = idx.set(dimIndex);
				}
			}
			
			final double value = dataValue.getValue();
			array.setDouble(idx, value);
		}

    	return array;
    }

}
