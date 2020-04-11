package jp.riken.brain.ni.samuraigraph.data;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable.MDArrayDataType;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable.VALUE_TYPE;

import org.w3c.dom.Element;

import ucar.ma2.Array;
import ucar.ma2.ArrayObject;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;
import ch.systemsx.cisd.base.mdarray.MDDoubleArray;
import ch.systemsx.cisd.hdf5.IHDF5Writer;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

/**
 * The class of multiple scalar XY type data for multidimensional data.
 *
 */
public class SGSXYMDArrayMultipleData extends SGMDArrayData implements
		SGISXYTypeMultipleData, SGIDataColumnTypeConstants, SGIDataPropertyKeyConstants, SGISXYMultipleDimensionData,
		SGIMDArrayConstants {

    /**
     * The variables for x-values.
     */
    protected SGMDArrayVariable[] mXVariables = null;

    /**
     * The variables for y-values.
     */
    protected SGMDArrayVariable[] mYVariables = null;

    /**
     * The variables for lower error values.
     */
    protected SGMDArrayVariable[] mLowerErrorVariables = null;

    /**
     * The variables for upper error values.
     */
    protected SGMDArrayVariable[] mUpperErrorVariables = null;

    /**
     * The variables for values that holds error values.
     */
    protected SGMDArrayVariable[] mErrorBarHolderVariables = null;

    /**
     * The variables for tick labels.
     */
    protected SGMDArrayVariable[] mTickLabelVariables = null;

    /**
     * The variables for values that holds tick labels.
     */
    protected SGMDArrayVariable[] mTickLabelHolderVariables = null;

    /**
     * The information for picked up dimension.
     */
    protected SGMDArrayPickUpDimensionInfo mPickUpDimensionInfo = null;

    /**
     * The decimal places for the tick labels.
     */
    protected int mDecimalPlaces = 0;

    /**
     * The exponent for the tick labels.
     */
    protected int mExponent = 0;

    /**
     * The stride of array.
     */
    protected SGIntegerSeriesSet mStride = null;

	/**
	 * The stride for the tick labels.
	 */
	protected SGIntegerSeriesSet mTickLabelStride = null;

	/**
	 * The shift value.
	 */
	private SGTuple2d mShift = new SGTuple2d();

	/**
	 * The default constructor.
	 */
	public SGSXYMDArrayMultipleData() {
		super();
	}

    protected SGMDArrayVariable getVariable(final SGMDArrayVariable[] vars) {
    	if (vars == null || vars.length == 0) {
    		return null;
    	} else {
        	return vars[0];
    	}
    }

    private SGMDArrayVariable[] getVariables(SGMDArrayVariable[] vars) {
    	if (vars != null) {
        	return vars.clone();
    	} else {
    		return null;
    	}
    }

    public SGMDArrayVariable[] getXVariables() {
    	return this.getVariables(this.mXVariables);
    }

    public SGMDArrayVariable[] getYVariables() {
    	return this.getVariables(this.mYVariables);
    }

    public SGMDArrayVariable[] getLowerErrorVariables() {
    	return this.getVariables(this.mLowerErrorVariables);
    }

    public SGMDArrayVariable[] getUpperErrorVariables() {
    	return this.getVariables(this.mUpperErrorVariables);
    }

    public SGMDArrayVariable[] getErrorHolderVariables() {
    	return this.getVariables(this.mErrorBarHolderVariables);
    }

    public SGMDArrayVariable[] getTickLabelVariables() {
    	return this.getVariables(this.mTickLabelVariables);
    }

    public SGMDArrayVariable[] getTickLabelHolderVariables() {
    	return this.getVariables(this.mTickLabelHolderVariables);
    }

    protected SGMDArrayVariable getXVariable() {
    	return this.getVariable(this.mXVariables);
    }

    protected SGMDArrayVariable getYVariable() {
    	return this.getVariable(this.mYVariables);
    }

    protected SGMDArrayVariable getLowerErrorVariable() {
    	return this.getVariable(this.mLowerErrorVariables);
    }

    protected SGMDArrayVariable getUpperErrorVariable() {
    	return this.getVariable(this.mUpperErrorVariables);
    }

    protected SGMDArrayVariable getErrorBarHolderVariable() {
    	return this.getVariable(this.mErrorBarHolderVariables);
    }

    protected SGMDArrayVariable getTickLabelVariable() {
    	return this.getVariable(this.mTickLabelVariables);
    }

    protected SGMDArrayVariable getTickLabelHolderVariable() {
    	return this.getVariable(this.mTickLabelHolderVariables);
    }

    protected SGMDArrayVariable[] createVariableArray(final SGMDArrayVariable var) {
    	if (var != null) {
    		SGMDArrayVariable[] vars = new SGMDArrayVariable[1];
        	vars[0] = var;
        	return vars;
    	} else {
    		return new SGMDArrayVariable[0];
    	}
    }

    protected boolean isValidVariables(
    		final SGMDArrayFile mdFile,
    		final SGMDArrayDataColumnInfo[] cols) {
    	if (cols == null) {
    		return false;
    	}
    	SGMDArrayVariable[] vars = mdFile.getVariables();
        for (int ii = 0; ii < cols.length; ii++) {
            if (cols[ii] == null) {
            	return false;
            }
            boolean found = false;
            for (SGMDArrayVariable var : vars) {
            	if (var.getName().equals(cols[ii].getName())) {
            		found = true;
            		break;
            	}
            }
            if (!found) {
            	return false;
            }
        }
        return true;
    }

    private boolean contains(final SGMDArrayDataColumnInfo[] infoArray,
    		final SGMDArrayDataColumnInfo info) {
    	for (int ii = 0; ii < infoArray.length; ii++) {
    		if (info.getName().equals(infoArray[ii].getName())) {
    			return true;
    		}
    	}
    	return false;
    }

    protected boolean isValidErrorBarVars(
    		final SGMDArrayFile mdFile,
    		final SGMDArrayDataColumnInfo[] multipleInfo,
    		final SGMDArrayDataColumnInfo[] leInfo,
    		final SGMDArrayDataColumnInfo[] ueInfo,
    		final SGMDArrayDataColumnInfo[] ehInfo) {

        if (leInfo != null && ueInfo != null) {
        	if (ehInfo == null) {
        		return false;
        	}
        	if (!this.isValidVariables(mdFile, leInfo)) {
        		return false;
        	}
        	if (!this.isValidVariables(mdFile, ueInfo)) {
        		return false;
        	}
        	if (leInfo.length != ueInfo.length) {
        		return false;
        	}
        	if (!this.isValidVariables(mdFile, ehInfo)) {
        		return false;
        	}
            if (ehInfo.length != leInfo.length) {
            	return false;
            }
            for (SGMDArrayDataColumnInfo info : ehInfo) {
            	if (!this.contains(multipleInfo, info)) {
            		return false;
            	}
            }
        }
        return true;
    }

    protected boolean isValidTickLabelVars(
    		final SGMDArrayFile mdFile,
    		final SGMDArrayDataColumnInfo[] multipleInfo,
    		final SGMDArrayDataColumnInfo[] tlInfo,
    		final SGMDArrayDataColumnInfo[] thInfo) {
        if (tlInfo != null) {
        	if (thInfo == null) {
        		return false;
        	}
        	if (!this.isValidVariables(mdFile, tlInfo)) {
        		return false;
        	}
        	if (!this.isValidVariables(mdFile, thInfo)) {
        		return false;
        	}
            if (thInfo.length != tlInfo.length) {
            	return false;
            }
            for (SGMDArrayDataColumnInfo info : thInfo) {
            	if (!this.contains(multipleInfo, info)) {
            		return false;
            	}
            }
        }
        return true;
    }

    /**
     * Builds a data object with given data file.
     *
     * @param mdFile
     *            multidimensional data file
     * @param obs
     *            data observer
     * @param xInfo
     *            the x variables
     * @param yInfo
     *            the y variables
     * @param leInfo
     *            the lower error variables
     * @param ueInfo
     *            the upper error variables
     * @param ehInfo
     *            the error bar holder variables
     * @param tlInfo
     *            the tick label variables
     * @param thInfo
     *            the tick label holder variables
     * @param stride
     *            stride of an array
     * @param tickLabelStride
     *            stride for tick label
     * @param strideAvailable
     *            flag whether to set available the stride
     */
    public SGSXYMDArrayMultipleData(final SGMDArrayFile mdFile,
    		final SGDataSourceObserver obs,
            final SGMDArrayDataColumnInfo[] xInfo, final SGMDArrayDataColumnInfo[] yInfo,
            final SGMDArrayDataColumnInfo[] leInfo, final SGMDArrayDataColumnInfo[] ueInfo,
            final SGMDArrayDataColumnInfo[] ehInfo, final SGMDArrayDataColumnInfo[] tlInfo,
            final SGMDArrayDataColumnInfo[] thInfo, final SGIntegerSeriesSet stride,
			final SGIntegerSeriesSet tickLabelStride, final boolean strideAvailable) {

        super(mdFile, obs, strideAvailable);

        if (xInfo == null || yInfo == null) {
            throw new IllegalArgumentException("xInfo == null || yInfo == null");
        }
        if (xInfo.length == 0 && yInfo.length == 0) {
            throw new IllegalArgumentException("The number of variables for x-values and that of y-values are both equal to zero.");
        }
    	if (xInfo.length > 1 && yInfo.length > 1) {
            throw new IllegalArgumentException("Invalid size of x-values and y-values: " + xInfo.length + ", " + yInfo.length);
    	}
		if (!this.isValidVariables(mdFile, xInfo)) {
            throw new IllegalArgumentException("Variables for x values are invalid.");
		}
		if (!this.isValidVariables(mdFile, yInfo)) {
            throw new IllegalArgumentException("Variables for y values are invalid.");
		}

        // get variables
        SGMDArrayVariable[] xVars = new SGMDArrayVariable[xInfo.length];
        for (int ii = 0; ii < xVars.length; ii++) {
        	SGMDArrayVariable var = this.initVariable(mdFile, xInfo[ii]);
        	if (var == null) {
        		throw new IllegalArgumentException("The name for x-values is not found: " + xInfo[ii].getName());
        	}
        	xVars[ii] = var;
        }
        SGMDArrayVariable[] yVars = new SGMDArrayVariable[yInfo.length];
        for (int ii = 0; ii < yVars.length; ii++) {
        	SGMDArrayVariable var = this.initVariable(mdFile, yInfo[ii]);
        	if (var == null) {
        		throw new IllegalArgumentException("The name for y-values is not found: " + yInfo[ii].getName());
        	}
        	yVars[ii] = var;
        }

    	// check the input for error bars
        final boolean multipleY;
        if (xInfo.length < yInfo.length) {
        	multipleY = true;
        } else if (xInfo.length == 1 && yInfo.length == 1) {
        	if ((ehInfo != null && this.contains(ehInfo, yInfo[0]))
        			|| (thInfo != null && this.contains(thInfo, yInfo[0]))) {
        		multipleY = true;
        	} else {
        		multipleY = false;
        	}
    	} else {
    		multipleY = false;
    	}
        SGMDArrayDataColumnInfo[] multipleVars = multipleY ? yInfo : xInfo;
    	if (!this.isValidErrorBarVars(mdFile, multipleVars, leInfo, ueInfo, ehInfo)) {
            throw new IllegalArgumentException("Column indices for the error bars are invalid.");
    	}
    	if (!this.isValidTickLabelVars(mdFile, multipleVars, tlInfo, thInfo)) {
            throw new IllegalArgumentException("Column indices for the tick labels are invalid.");
    	}

    	// find variables
    	SGMDArrayVariable[] leVars = null;
    	SGMDArrayVariable[] ueVars = null;
    	SGMDArrayVariable[] ehVars = null;
    	if (leInfo != null && leInfo.length != 0) {
        	leVars = new SGMDArrayVariable[leInfo.length];
        	for (int ii = 0; ii < leInfo.length; ii++) {
        		SGMDArrayVariable var = this.initVariable(mdFile, leInfo[ii]);
        		if (var == null) {
        			throw new IllegalArgumentException("A variable for lower error values is not found: " + leInfo[ii].getName());
        		}
        		leVars[ii] = var;
        	}
        	ueVars = new SGMDArrayVariable[ueInfo.length];
        	for (int ii = 0; ii < ueInfo.length; ii++) {
        		SGMDArrayVariable var = this.initVariable(mdFile, ueInfo[ii]);
        		if (var == null) {
        			throw new IllegalArgumentException("A variable for upper error values is not found: " + ueInfo[ii].getName());
        		}
        		ueVars[ii] = var;
        	}
    		ehVars = new SGMDArrayVariable[ehInfo.length];
    		for (int ii = 0; ii < ehInfo.length; ii++) {
    			String name = ehInfo[ii].getName();
    			SGMDArrayVariable var = null;
    			SGMDArrayVariable[] vars;
            	if (multipleY) {
            		vars = yVars;
            	} else {
            		vars = xVars;
            	}
    			for (int jj = 0; jj < vars.length; jj++) {
    				if (name.equals(vars[jj].getName())) {
    					var = vars[jj];
    					break;
    				}
    			}
    			if (var == null) {
        			throw new IllegalArgumentException("A variable for error value holder is not found: " + name);
    			}
    			ehVars[ii] = var;
    		}
    	}
    	SGMDArrayVariable[] tlVars = null;
    	SGMDArrayVariable[] thVars = null;
    	if (tlInfo != null && tlInfo.length != 0) {
        	tlVars = new SGMDArrayVariable[tlInfo.length];
        	for (int ii = 0; ii < tlInfo.length; ii++) {
        		SGMDArrayVariable var = this.initVariable(mdFile, tlInfo[ii]);
        		if (var == null) {
        			throw new IllegalArgumentException("A variable for tick label is not found: " + tlInfo[ii].getName());
        		}
        		tlVars[ii] = var;
        	}
    		thVars = new SGMDArrayVariable[thInfo.length];
    		for (int ii = 0; ii < thInfo.length; ii++) {
    			String name = thInfo[ii].getName();
    			SGMDArrayVariable var = null;
    			SGMDArrayVariable[] vars;
            	if (multipleY) {
            		vars = yVars;
            	} else {
            		vars = xVars;
            	}
    			for (int jj = 0; jj < vars.length; jj++) {
    				if (name.equals(vars[jj].getName())) {
    					var = vars[jj];
    					break;
    				}
    			}
    			if (var == null) {
        			throw new IllegalArgumentException("A variable for tick label holder is not found: " + name);
    			}
    			thVars[ii] = var;
    		}
    	}

        // set to the attributes
        this.mXVariables = xVars;
        this.mYVariables = yVars;
    	this.mLowerErrorVariables = leVars;
    	this.mUpperErrorVariables = ueVars;
    	this.mErrorBarHolderVariables = ehVars;
    	this.mTickLabelVariables = tlVars;
    	this.mTickLabelHolderVariables = thVars;
    	final int len = this.getAllPointsNumber();
        this.mStride = this.createStride(stride, len);
        this.mTickLabelStride = this.createStride(tickLabelStride, len);
        this.initTimeStride();
    }

    /**
     * Builds a data object with given multidimensional data file.
     *
     * @param mdFile
     *            multidimensional data file
     * @param obs
     *            netCDF data observer
     * @param xInfo
     *            information of x values
     * @param yInfo
     *            information of y values
     * @param leInfo
     *            information of lower error values
     * @param ueInfo
     *            information of lower upper values
     * @param tlInfo
     *            information of tick labels
     * @param indices
     *            indices for given dimension
     * @param stride
     *            stride of an array
     * @param tickLabelStride
     *            stride for tick label
     * @param strideAvailable
     *            flag whether to set available the stride
     */
	public SGSXYMDArrayMultipleData(final SGMDArrayFile mdFile,
			final SGDataSourceObserver obs,
			final SGMDArrayDataColumnInfo xInfo,
			final SGMDArrayDataColumnInfo yInfo,
			final SGMDArrayDataColumnInfo leInfo,
			final SGMDArrayDataColumnInfo ueInfo,
			final SGMDArrayDataColumnInfo tlInfo,
			final SGIntegerSeriesSet indices, final SGIntegerSeriesSet stride,
			final SGIntegerSeriesSet tickLabelStride, final boolean strideAvailable) {

        super(mdFile, obs, strideAvailable);

        if (xInfo == null && yInfo == null) {
            throw new IllegalArgumentException("xInfo == null && yInfo == null");
        }
        if (!(leInfo == null && ueInfo == null)
        		&& !(leInfo != null && ueInfo != null)) {
            throw new IllegalArgumentException("leInfo = " + leInfo + ", ueInfo = " + ueInfo);
        }
        if (indices == null) {
            throw new IllegalArgumentException("indices == null");
        }

        // get variables
        SGMDArrayVariable xVar = this.initVariable(mdFile, xInfo);
        SGMDArrayVariable yVar = this.initVariable(mdFile, yInfo);
        SGMDArrayVariable leVar = null;
        SGMDArrayVariable ueVar = null;
        SGMDArrayVariable ehVar = null;
        if (leInfo != null && ueInfo != null) {
            leVar = this.initVariable(mdFile, leInfo);
            ueVar = this.initVariable(mdFile, ueInfo);
            String hName = SGDataUtility.getHolderName(leInfo);
            ehVar = this.getHoderVariable(hName, xVar, yVar);
        }
        SGMDArrayVariable tlVar = null;
        SGMDArrayVariable thVar = null;
        if (tlInfo != null) {
            tlVar = this.initVariable(mdFile, tlInfo);
            String hName = SGDataUtility.getHolderName(tlInfo);
            thVar = this.getHoderVariable(hName, xVar, yVar);
        }

        final Integer xPickUpDim = (xInfo != null) ? xInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION) : null;
        final Integer yPickUpDim = (yInfo != null) ? yInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION) : null;
        final boolean xValid = SGDataUtility.isValidPickUpValue(xPickUpDim);
        final boolean yValid = SGDataUtility.isValidPickUpValue(yPickUpDim);
        if (!xValid && !yValid) {
            throw new IllegalArgumentException("Both of x and y indices are invalid.");
        }
        Map<String, Integer> pickUpDimMap = new HashMap<String, Integer>();
        if (xValid) {
        	pickUpDimMap.put(xInfo.getName(), xPickUpDim);
        }
        if (yValid) {
        	pickUpDimMap.put(yInfo.getName(), yPickUpDim);
        }

        // set to attributes
        this.mXVariables = this.createVariableArray(xVar);
        this.mYVariables = this.createVariableArray(yVar);
        this.mLowerErrorVariables = this.createVariableArray(leVar);
        this.mUpperErrorVariables = this.createVariableArray(ueVar);
        this.mErrorBarHolderVariables = this.createVariableArray(ehVar);
        this.mTickLabelVariables = this.createVariableArray(tlVar);
        this.mTickLabelHolderVariables = this.createVariableArray(thVar);
        this.mPickUpDimensionInfo = new SGMDArrayPickUpDimensionInfo(
        		pickUpDimMap, (SGIntegerSeriesSet) indices.clone());
        this.updateDimensionIndices();
    	final int len = this.getAllPointsNumber();
        this.mStride = this.createStride(stride, len);
        this.mTickLabelStride = this.createStride(tickLabelStride, len);
        this.initTimeStride();
    }

	private SGMDArrayVariable getHoderVariable(String hName, SGMDArrayVariable xVar,
			SGMDArrayVariable yVar) {
		SGMDArrayVariable hVar = null;
        if (xVar == null) {
        	hVar = yVar;
        } else {
        	if (yVar == null) {
        		hVar = xVar;
        	} else {
        		hVar = hName.equals(xVar.getName()) ? xVar : yVar;
        	}
        }
        return hVar;
	}

    protected SGMDArrayVariable initVariable(SGMDArrayFile file, SGMDArrayDataColumnInfo info) {
    	SGMDArrayVariable var = super.initVariable(file, info);
    	if (info != null) {
        	Integer pickUpDimension = info.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
        	Integer value = null;
        	if (pickUpDimension != null) {
        		value = pickUpDimension;
        	} else {
        		value = -1;
        	}
        	var.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, value);
    	}
    	return var;
    }

    protected SGMDArrayVariable initVariable(SGMDArrayVariable var) {
    	super.initVariable(var);
    	var.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, -1);
    	return var;
    }

    // Updates dimension indices.
    protected void updateDimensionIndices() {
    	if (this.mPickUpDimensionInfo != null) {
    		Map<String, Integer> dimensionMap = this.mPickUpDimensionInfo.getDimensionMap();
    		Iterator<Entry<String, Integer>> itr = dimensionMap.entrySet().iterator();
    		while (itr.hasNext()) {
    			Entry<String, Integer> entry = itr.next();
    			String name = entry.getKey();
    			Integer dimension = entry.getValue();
    			SGMDArrayVariable var = this.findVariable(name);
				var.setDimensionIndex(KEY_SXY_PICKUP_DIMENSION, dimension);
    		}
    	} else {
    		SGMDArrayVariable[] vars = this.getVariables();
    		for (SGMDArrayVariable var : vars) {
    			var.setDimensionIndex(KEY_SXY_PICKUP_DIMENSION, -1);
    		}
    	}
    }

    /**
     * Returns whether a dimension is picked up.
     *
     * @return true if a dimension is picked up
     */
    public boolean isDimensionPicked() {
    	return (this.mPickUpDimensionInfo != null);
    }

    public List<SGMDArrayVariable> getPickUpMDArrayVariables() {
    	if (this.mPickUpDimensionInfo != null) {
    		List<SGMDArrayVariable> varList = new ArrayList<SGMDArrayVariable>();
    		Map<String, Integer> dimensionMap = this.mPickUpDimensionInfo.getDimensionMap();
    		Iterator<Entry<String, Integer>> itr = dimensionMap.entrySet().iterator();
    		while (itr.hasNext()) {
    			Entry<String, Integer> entry = itr.next();
    			String name = entry.getKey();
    			Integer dim = entry.getValue();
    			if(dim != -1) {
            		SGMDArrayVariable var = this.findVariable(name);
            		varList.add(var);
    			}
    		}
    		return varList;
    	} else {
    		return null;
    	}
    }

    private int[] getPickUpDimensionIndices() {
    	if (this.mPickUpDimensionInfo != null) {
        	return this.mPickUpDimensionInfo.getIndices().getNumbers();
    	} else {
    		return null;
    	}
    }

    /**
     * Disposes of this data object.
     * Do not call any method of this object after called this method.
     */
    public void dispose() {
        super.dispose();
        this.mStride = null;
        this.mTickLabelStride = null;
        this.mShift = null;
    }

    /**
     * Sets the decimal places for the tick labels.
     *
     * @param dp
     *          a value to set to the decimal places
     */
	@Override
	public void setDecimalPlaces(int dp) {
        if (dp < 0) {
            throw new IllegalArgumentException("Decimal places must not be negative: " + dp);
        }
        this.mDecimalPlaces = dp;
	}

    /**
     * Sets the exponent for the tick labels.
     *
     * @param exp
     *           a value to set to the exponent
     */
	@Override
	public void setExponent(int exp) {
        this.mExponent = exp;
	}

    /**
     * Returns the decimal places for the tick labels.
     *
     * @return the decimal places for the tick labels
     */
	@Override
	public int getDecimalPlaces() {
		return this.mDecimalPlaces;
	}

    /**
     * Returns the exponent for tick labels.
     *
     * @return the exponent for tick labels
     */
	@Override
	public int getExponent() {
		return this.mExponent;
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

	public boolean useCache(final boolean all) {
		if (!all) {
			return true;
		}
		if (!this.isStrideAvailable()) {
			return true;
		}
		return this.mStride.isComplete();
	}

    /**
     * Returns a variable for multiple values.
     *
     * @return a variable for multiple values
     */
    public SGMDArrayVariable[] getMultipleVariables() {
    	if (this.isDimensionPicked()) {
    		return null;
    	} else {
            if (this.hasMultipleYValues()) {
                return this.mYVariables;
            } else {
                return this.mXVariables;
            }
    	}
    }

    /**
     * Returns a variable for "not" multiple values.
     *
     * @return a variable for "not" multiple values
     */
    public SGMDArrayVariable getSingleVariable() {
    	if (this.isDimensionPicked()) {
    		if (this.hasMultipleYValues()) {
    			return this.getXVariable();
    		} else {
    			return this.getYVariable();
    		}
    	} else {
            if (this.hasMultipleYValues()) {
            	if (this.mXVariables != null && this.mXVariables.length == 1) {
            		return this.mXVariables[0];
            	} else {
            		return null;
            	}
            } else {
            	if (this.mYVariables != null && this.mYVariables.length == 1) {
                    return this.mYVariables[0];
            	} else {
            		return null;
            	}
            }
    	}
    }

    /**
	 * Returns an array of values of multiple variables.
	 *
	 * @param stride
	 *          the stride to use
	 * @return an array of values of multiple variables
	 */
    protected double[][] getMultipleVariableValueArray(SGIntegerSeriesSet stride) {
		SGMDArrayVariable[] vars = this.getMultipleVariables();
        double[][] array = new double[vars.length][];
        for (int ii = 0; ii < array.length; ii++) {
        	array[ii] = vars[ii].getGenericNumberArray(stride);
        }
        return array;
    }

    /**
	 * Returns an array of values of picked up variables.
	 *
	 * @param stride
	 *          the stride to use
	 * @param pickUpVar
	 *          picked up variable
	 * @return an array of values of multiple variables
	 */
    protected double[][] getPickUpValueArray(SGIntegerSeriesSet stride,
    		SGMDArrayVariable pickUpVar, final boolean pickUpAll) {
    	SGIntegerSeriesSet pickUpStride = pickUpAll ? null : this.mPickUpDimensionInfo.getIndices();
		int[] pickUpIndices = null;
    	if (pickUpStride != null) {
    		pickUpIndices = pickUpStride.getNumbers();
    	} else {
    		final int pickUpLen = pickUpVar.getDimensionLength(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
    		pickUpIndices = new int[pickUpLen];
    		for (int ii = 0; ii < pickUpLen; ii++) {
    			pickUpIndices[ii] = ii;
    		}
    	}
		final int len = (stride != null) ? stride.getLength() : this.getAllPointsNumber();
		int[] indices = null;
		if (stride != null) {
			indices = stride.getNumbers();
		} else {
			indices = new int[len];
			for (int ii = 0; ii < len; ii++) {
				indices[ii] = ii;
			}
		}
		final int dimensionIndex = pickUpVar.getGenericDimensionIndex();
		final int pickUpDimensionIndex = pickUpVar.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
        double[][] array = new double[pickUpIndices.length][];
        for (int ii = 0; ii < array.length; ii++) {
        	int[] origins = pickUpVar.getOrigins();
        	origins[pickUpDimensionIndex] = pickUpIndices[ii];
        	array[ii] = new double[len];
        	for (int jj = 0; jj < len; jj++) {
        		final int index = indices[jj];
        		array[ii][jj] = pickUpVar.getDoubleValue(dimensionIndex, index, origins);
        	}
        }
        return array;
    }

    /**
     * Returns whether this multiple data has multiple arrays for y-values.
     *
     * @return true if this multiple data has multiple arrays for y-values
     */
    public boolean hasMultipleYValues() {
    	if (this.isDimensionPicked()) {
    		SGMDArrayVariable yVar = this.getYVariable();
    		if (yVar != null) {
    			Integer index = yVar.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
    			return (index != null && index != -1);
    		} else {
    			return false;
    		}
    	} else {
        	return (this.mYVariables.length >= this.mXVariables.length);
    	}
    }

    protected boolean hasEqualLowerUpperErrorVariable() {
    	if (!this.isErrorBarAvailable()) {
    		return false;
    	}
    	return this.mLowerErrorVariables[0].equals(this.mUpperErrorVariables[0]);
    }

    protected int find(SGMDArrayVariable var, SGMDArrayVariable[] vars) {
    	if (vars == null) {
    		return -1;
    	}
        for (int ii = 0; ii < vars.length; ii++) {
            if (vars[ii].equals(var)) {
                return ii;
            }
        }
        return -1;
    }

    /**
     * The class of properties for multiple multidimensional data.
     *
     */
    public static class SXYMDArrayMultipleDataProperties extends MDArrayDataProperties {

        protected String[] xNames = null;

        protected String[] yNames = null;

        protected String[] lNames = null;

        protected String[] uNames = null;

        protected String[] ehNames = null;

        protected String[] tNames = null;

        protected String[] thNames = null;

    	protected int mDecimalPlaces = 0;

    	protected int mExponent = 0;

    	protected SGMDArrayPickUpDimensionInfo mPickUpInfo = null;

        SGIntegerSeriesSet mStride = null;

        SGIntegerSeriesSet mTickLabelStride = null;

        /**
         * The default constructor.
         */
    	public SXYMDArrayMultipleDataProperties() {
    		super();
    	}

        /**
         * Dispose this object.
         */
        @Override
        public void dispose() {
            super.dispose();
            this.xNames = null;
            this.yNames = null;
            this.lNames = null;
            this.uNames = null;
            this.ehNames = null;
            this.tNames = null;
            this.thNames = null;
            this.mPickUpInfo = null;
            this.mStride = null;
            this.mTickLabelStride = null;
        }

    	@Override
    	public boolean equals(Object obj) {
            if ((obj instanceof SXYMDArrayMultipleDataProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            SXYMDArrayMultipleDataProperties p = (SXYMDArrayMultipleDataProperties) obj;
            if (this.mDecimalPlaces != p.mDecimalPlaces) {
            	return false;
            }
            if (this.mExponent != p.mExponent) {
            	return false;
            }
            return true;
        }
    	
    	@Override
        public boolean hasEqualSize(DataProperties dp) {
            if ((dp instanceof SXYMDArrayMultipleDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualSize(dp)) {
            	return false;
            }
            SXYMDArrayMultipleDataProperties p = (SXYMDArrayMultipleDataProperties) dp;
            if (!SGUtility.equals(this.mPickUpInfo, p.mPickUpInfo)) {
                return false;
            }
            if (!SGUtility.equals(this.mStride, p.mStride)) {
            	return false;
            }
            if (!SGUtility.equals(this.mTickLabelStride, p.mTickLabelStride)) {
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
            if ((dp instanceof SXYMDArrayMultipleDataProperties) == false) {
                return false;
            }
//            if (super.hasEqualColumnTypes(dp) == false) {
//            	return false;
//            }
            SXYMDArrayMultipleDataProperties p = (SXYMDArrayMultipleDataProperties) dp;
            if (SGUtility.equals(this.xNames, p.xNames) == false) {
                return false;
            }
            if (SGUtility.equals(this.yNames, p.yNames) == false) {
                return false;
            }
            if (SGUtility.equals(this.lNames, p.lNames) == false) {
            	return false;
            }
            if (SGUtility.equals(this.uNames, p.uNames) == false) {
            	return false;
            }
            if (SGUtility.equals(this.ehNames, p.ehNames) == false) {
            	return false;
            }
            if (SGUtility.equals(this.tNames, p.tNames) == false) {
            	return false;
            }
            if (SGUtility.equals(this.thNames, p.thNames) == false) {
            	return false;
            }
        	return true;
        }

        /**
         * Copy this object.
         *
         * @return a copied object
         */
        public Object copy() {
        	SXYMDArrayMultipleDataProperties p = (SXYMDArrayMultipleDataProperties) super.copy();
        	p.mStride = (this.mStride != null) ? (SGIntegerSeriesSet) this.mStride.clone() : null;
        	p.mTickLabelStride = (this.mTickLabelStride != null) ? (SGIntegerSeriesSet) this.mTickLabelStride.clone() : null;
        	return p;
        }
    }

    /**
     * Returns the properties of this data.
     *
     * @return the properties of this data
     */
	@Override
	public SGProperties getProperties() {
		SXYMDArrayMultipleDataProperties p = new SXYMDArrayMultipleDataProperties();
		if (!this.getProperties(p)) {
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
    public boolean getProperties(SGProperties p) {
        if (!(p instanceof SXYMDArrayMultipleDataProperties)) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        SXYMDArrayMultipleDataProperties sp = (SXYMDArrayMultipleDataProperties) p;

        String[] xNames = null;
        if (this.mXVariables != null) {
            xNames = new String[this.mXVariables.length];
            for (int ii = 0; ii < xNames.length; ii++) {
                xNames[ii] = this.mXVariables[ii].getName();
            }
        }
        String[] yNames = null;
        if (this.mYVariables != null) {
            yNames = new String[this.mYVariables.length];
            for (int ii = 0; ii < yNames.length; ii++) {
                yNames[ii] = this.mYVariables[ii].getName();
            }
        }
        String[] lNames = null;
        String[] uNames = null;
        String[] ehNames = null;
        if (this.isErrorBarAvailable()) {
        	lNames = new String[this.mLowerErrorVariables.length];
        	for (int ii = 0; ii < lNames.length; ii++) {
        		lNames[ii] = this.mLowerErrorVariables[ii].getName();
        	}
        	uNames = new String[this.mUpperErrorVariables.length];
        	for (int ii = 0; ii < uNames.length; ii++) {
        		uNames[ii] = this.mUpperErrorVariables[ii].getName();
        	}
        	if (this.isDimensionPicked()) {
                lNames = new String[] { this.getName(this.getLowerErrorVariable()) };
                uNames = new String[] { this.getName(this.getUpperErrorVariable()) };
                ehNames = new String[] { this.getName(this.getErrorBarHolderVariable()) };
        	} else {
            	ehNames = new String[this.mErrorBarHolderVariables.length];
            	for (int ii = 0; ii < ehNames.length; ii++) {
            		ehNames[ii] = this.mErrorBarHolderVariables[ii].getName();
            	}
        	}
        }
        String[] tNames = null;
        String[] thNames = null;
        if (this.isTickLabelAvailable()) {
        	tNames = new String[this.mTickLabelVariables.length];
        	for (int ii = 0; ii < tNames.length; ii++) {
        		tNames[ii] = this.mTickLabelVariables[ii].getName();
        	}
        	if (this.isDimensionPicked()) {
                tNames = new String[] { this.getName(this.getTickLabelVariable()) };
                thNames = new String[] { this.getName(this.getTickLabelHolderVariable()) };
        	} else {
            	thNames = new String[this.mTickLabelHolderVariables.length];
            	for (int ii = 0; ii < thNames.length; ii++) {
            		thNames[ii] = this.mTickLabelHolderVariables[ii].getName();
            	}
        	}
        }
        sp.xNames = xNames;
        sp.yNames = yNames;
        sp.lNames = lNames;
        sp.uNames = uNames;
        sp.ehNames = ehNames;
        sp.tNames = tNames;
        sp.thNames = thNames;
        sp.mDecimalPlaces = this.mDecimalPlaces;
        sp.mExponent = this.mExponent;
		sp.mPickUpInfo = (SGMDArrayPickUpDimensionInfo) this.getPickUpDimensionInfo();
        sp.mStride = this.getStride();
        sp.mTickLabelStride = this.getTickLabelStride();
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
	public boolean setProperties(SGProperties p) {
        if (!(p instanceof SXYMDArrayMultipleDataProperties)) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        SXYMDArrayMultipleDataProperties sp = (SXYMDArrayMultipleDataProperties) p;

        SGMDArrayVariable[] xVars = null;
        if (sp.xNames != null) {
            xVars = new SGMDArrayVariable[sp.xNames.length];
            for (int ii = 0; ii < xVars.length; ii++) {
                xVars[ii] = this.findVariable(sp.xNames[ii]);
            }
        }
        SGMDArrayVariable[] yVars = null;
        if (sp.yNames != null) {
            yVars = new SGMDArrayVariable[sp.yNames.length];
            for (int ii = 0; ii < yVars.length; ii++) {
                yVars[ii] = this.findVariable(sp.yNames[ii]);
            }
        }
        SGMDArrayVariable[] lVars = null;
        if (sp.lNames != null) {
            lVars = new SGMDArrayVariable[sp.lNames.length];
            for (int ii = 0; ii < lVars.length; ii++) {
                lVars[ii] = this.findVariable(sp.lNames[ii]);
            }
        }
        SGMDArrayVariable[] uVars = null;
        if (sp.uNames != null) {
            uVars = new SGMDArrayVariable[sp.uNames.length];
            for (int ii = 0; ii < uVars.length; ii++) {
                uVars[ii] = this.findVariable(sp.uNames[ii]);
            }
        }
        SGMDArrayVariable[] ehVars = null;
        if (sp.ehNames != null) {
            ehVars = new SGMDArrayVariable[sp.ehNames.length];
            for (int ii = 0; ii < ehVars.length; ii++) {
                ehVars[ii] = this.findVariable(sp.ehNames[ii]);
            }
        }
        SGMDArrayVariable[] tVars = null;
        if (sp.tNames != null) {
            tVars = new SGMDArrayVariable[sp.tNames.length];
            for (int ii = 0; ii < tVars.length; ii++) {
                tVars[ii] = this.findVariable(sp.tNames[ii]);
            }
        }
        SGMDArrayVariable[] thVars = null;
        if (sp.thNames != null) {
        	thVars = new SGMDArrayVariable[sp.thNames.length];
            for (int ii = 0; ii < thVars.length; ii++) {
                thVars[ii] = this.findVariable(sp.thNames[ii]);
            }
        }

        // set to the attributes
        this.mXVariables = xVars;
        this.mYVariables = yVars;
        this.mLowerErrorVariables = lVars;
        this.mUpperErrorVariables = uVars;
        this.mErrorBarHolderVariables = ehVars;
        this.mTickLabelVariables = tVars;
        this.mTickLabelHolderVariables = thVars;

        this.mDecimalPlaces = sp.mDecimalPlaces;
        this.mExponent = sp.mExponent;
        this.mPickUpDimensionInfo = (sp.mPickUpInfo != null)
        		? (SGMDArrayPickUpDimensionInfo) sp.mPickUpInfo.clone() : null;
		this.setStride(sp.mStride);
		this.setTickLabelStride(sp.mTickLabelStride);

		this.updateDimensionIndices();

        return true;
	}

    /**
     * Returns the index of picked up dimension.
     *
     * @return the index of picked up dimension
     */
    public Integer getPickUpDimension(SGMDArrayVariable var) {
    	if (this.isDimensionPicked()) {
    		return this.mPickUpDimensionInfo.getDimension(var.getName());
//    		SGMDArrayVariable pickedUpVar = this.getPickUpMDArrayVariable();
//    		return pickedUpVar.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
    	} else {
    		return null;
    	}
    }

	public SGIntegerSeriesSet getIndices() {
    	if (this.isDimensionPicked()) {
			return this.mPickUpDimensionInfo.getIndices();
		} else {
			return null;
		}
	}

	@Override
	public SGPickUpDimensionInfo getPickUpDimensionInfo() {
    	if (this.isDimensionPicked()) {
			return (SGPickUpDimensionInfo) this.mPickUpDimensionInfo.clone();
		} else {
			return null;
		}
	}

	@Override
	public boolean setPickUpDimensionInfo(SGPickUpDimensionInfo info) {
		if (info != null) {
			if (!(info instanceof SGMDArrayPickUpDimensionInfo)) {
				throw new IllegalArgumentException("Invalid input: " + info);
			}
			this.mPickUpDimensionInfo = (SGMDArrayPickUpDimensionInfo) info.clone();
		} else {
			this.mPickUpDimensionInfo = null;
		}
		this.updateDimensionIndices();

		return true;
	}

    /**
     * Returns whether error bars are available.
     *
     * @return true if error bars are available
     */
	@Override
	public boolean isErrorBarAvailable() {
		return (this.getLowerErrorVariable() != null && this.getUpperErrorVariable() != null);
	}

    /**
     * Returns whether tick labels are available.
     *
     * @return true if tick labels are available
     */
	@Override
	public boolean isTickLabelAvailable() {
		return (this.getTickLabelVariable() != null);
	}

    /**
	 * Returns whether the error bars are vertical. If this data does not have
	 * error values, returns null.
	 *
	 * @return true if error bars are vertical, false if they are horizontal and
	 *         null if this data do not have error values
	 */
	@Override
	public Boolean isErrorBarVertical() {
    	if (this.isErrorBarAvailable()) {
            return SGUtility.contains(this.mErrorBarHolderVariables, this.mYVariables);
    	} else {
    		return null;
    	}
	}

    /**
     * Returns whether the tick labels align horizontally. If this data does not have
     * tick labels, returns null.
     *
     * @return true if tick labels align horizontally, false if they do not so and
     *         null if this data do not have tick labels
     */
	@Override
	public Boolean isTickLabelHorizontal() {
        if (this.isTickLabelAvailable()) {
            return SGUtility.contains(this.mTickLabelHolderVariables, this.mYVariables);
        } else {
            return null;
        }
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
	        return SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA;
		} else if (src instanceof SGMATLABFile) {
	        return SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA;
		} else if (src instanceof SGVirtualMDArrayFile) {
	        return SGDataTypeConstants.SXY_MULTIPLE_VIRTUAL_MDARRAY_DATA;
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
			return this.mStride.getLength();
		} else {
			return this.getAllPointsNumber();
		}
	}

    /**
     * Returns the title for the X-axis.
     *
     * @return the title for the X-axis
     */
	@Override
	public String getTitleX() {
		if (this.isDimensionPicked()) {
			SGMDArrayVariable xVar = this.getXVariable();
			if (xVar != null) {
				return xVar.getSimpleName();
			} else {
				return "";
			}
		} else {
			if (this.mXVariables != null && this.mXVariables.length == 1) {
				return this.mXVariables[0].getSimpleName();
			} else {
				return "";
			}
		}
	}

    /**
     * Returns the title for the Y-axis.
     *
     * @return the title for the Y-axis
     */
	@Override
	public String getTitleY() {
		if (this.isDimensionPicked()) {
			SGMDArrayVariable yVar = this.getYVariable();
			if (yVar != null) {
				return yVar.getSimpleName();
			} else {
				return "";
			}
		} else {
			if (this.mYVariables != null && this.mYVariables.length == 1) {
				return this.mYVariables[0].getSimpleName();
			} else {
	            return "";
			}
		}
	}

    /**
     * Returns an array of SXYData objects.
     *
     * @return an array of SXYData objects
     */
	@Override
	public SGISXYTypeSingleData[] getSXYDataArray() {
    	SGSXYMultipleDataCache cache = (SGSXYMultipleDataCache) this.getCache();
    	SGSXYDataCache[] sxyCacheArray = null;
        if (cache != null) {
        	sxyCacheArray = cache.mCacheArray;
        }
		if (this.isDimensionPicked()) {
	        final SGMDArrayVariable xVar = this.getXVariable();
	        final SGMDArrayVariable yVar = this.getYVariable();
	        final SGMDArrayVariable leVar = this.getLowerErrorVariable();
	        final SGMDArrayVariable ueVar = this.getUpperErrorVariable();
	        final SGMDArrayVariable ehVar = this.getErrorBarHolderVariable();
	        final SGMDArrayVariable tlVar = this.getTickLabelVariable();
	        final SGMDArrayVariable thVar = this.getTickLabelHolderVariable();
	        SGMDArrayDataColumnInfo xInfo = SGDataUtility.createDataColumnInfo(xVar, X_VALUE);
	        SGMDArrayDataColumnInfo yInfo = SGDataUtility.createDataColumnInfo(yVar, Y_VALUE);
	        SGMDArrayDataColumnInfo leInfo = null;
	        SGMDArrayDataColumnInfo ueInfo = null;
	        SGMDArrayDataColumnInfo ehInfo = null;
	        if (leVar != null && ueVar != null && ehVar != null) {
	        	leInfo = SGDataUtility.createDataColumnInfo(leVar, LOWER_ERROR_VALUE);
	        	ueInfo = SGDataUtility.createDataColumnInfo(ueVar, UPPER_ERROR_VALUE);
	        	ehInfo = (SGMDArrayDataColumnInfo) (ehVar.equals(xVar) ? xInfo : yInfo).clone();
	        }
	        SGMDArrayDataColumnInfo tlInfo = null;
	        SGMDArrayDataColumnInfo thInfo = null;
	        if (tlVar != null) {
	        	tlInfo = SGDataUtility.createDataColumnInfo(tlVar, TICK_LABEL);
	        	thInfo = (SGMDArrayDataColumnInfo) (thVar.equals(xVar) ? xInfo : yInfo).clone();
	        }
	        SGMDArrayVariable[] vars = this.getVariables();
	        int[] pickUpIndices = this.getPickUpDimensionIndices();
	        List<SGMDArrayVariable> pickUpVars = this.getPickUpMDArrayVariables();
	        final SGISXYTypeSingleData[] dataArray = new SGISXYTypeSingleData[pickUpIndices.length];
	        for (int ii = 0; ii < dataArray.length; ii++) {
				SGSXYMDArrayData data = new SGSXYMDArrayData(this.getMDArrayFile(),
						this.getDataSourceObserver(), xInfo, yInfo, leInfo, ueInfo,
						ehInfo, tlInfo, thInfo, this.mStride, this.mTickLabelStride,
						this.isStrideAvailable());
	            for (SGMDArrayVariable var : vars) {
	                final int[] origins = var.getOrigins();
	                final String varName = var.getName();
	                if (pickUpVars.contains(var)) {
	                	Integer pickUpDim = var.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
	                    origins[pickUpDim] = pickUpIndices[ii];
	                }
	                if (leVar != null && ueVar != null) {
	                	if (varName.equals(leVar.getName())) {
	                		final Integer index = leInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
	                		if (index != null && index != -1) {
		                		origins[index] = pickUpIndices[ii];
	                		}
	                	} else if (varName.equals(ueVar.getName())) {
	                		final Integer index = ueInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
	                		if (index != null && index != -1) {
		                		origins[index] = pickUpIndices[ii];
	                		}
	                	}
	                }
	                if (tlVar != null) {
	                	if (varName.equals(tlVar.getName())) {
	                		final Integer index = tlInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
	                		if (index != null && index != -1) {
		                		origins[index] = pickUpIndices[ii];
	                		}
	                	}
	                }
	                data.setOrigin(varName, origins);
	            }
	            data.setDecimalPlaces(this.mDecimalPlaces);
	            data.setExponent(this.mExponent);
				data.setTimeStride(this.mTimeStride);
	            data.setShift(this.mShift);

	            // sets the cache
	            if (sxyCacheArray != null) {
	            	data.setCache(sxyCacheArray[ii]);
	            }

	            dataArray[ii] = (SGISXYTypeSingleData) data;
	        }
	        
	        SGDataUtility.syncDataValueHistory(this.mEditedDataValueList, dataArray);

	        return dataArray;

		} else {
			SGMDArrayVariable varSingle = this.getSingleVariable();
			SGMDArrayVariable[] varMultiple = this.getMultipleVariables();
	        final int len = varMultiple.length;
	        final boolean by = this.hasMultipleYValues();
	        final boolean be = this.isErrorBarAvailable();
	        final boolean bt = this.isTickLabelAvailable();
	        final SGISXYTypeSingleData[] dataArray = new SGISXYTypeSingleData[len];
	        for (int ii = 0; ii < dataArray.length; ii++) {
	        	SGMDArrayVariable xVar = by ? varSingle : varMultiple[ii];
	        	SGMDArrayVariable yVar = by ? varMultiple[ii] : varSingle;
	        	SGMDArrayVariable leVar = null;
	        	SGMDArrayVariable ueVar = null;
	        	SGMDArrayVariable ehVar = null;
	            if (be) {
	                for (int jj = 0; jj < this.mErrorBarHolderVariables.length; jj++) {
	                	SGMDArrayVariable var = this.mErrorBarHolderVariables[jj];
	                    if (var.equals(xVar) || var.equals(yVar)) {
	                        ehVar = var;
	                        leVar = this.mLowerErrorVariables[jj];
	                        ueVar = this.mUpperErrorVariables[jj];
	                        break;
	                    }
	                }
	            }
	            SGMDArrayVariable tlVar = null;
	            SGMDArrayVariable thVar = null;
	            if (bt) {
	                for (int jj = 0; jj < this.mTickLabelHolderVariables.length; jj++) {
	                	SGMDArrayVariable var = this.mTickLabelHolderVariables[jj];
	                    if (var.equals(xVar) || var.equals(yVar)) {
	                        thVar = var;
	                        tlVar = this.mTickLabelVariables[jj];
	                        break;
	                    }
	                }
	            }
	            SGMDArrayDataColumnInfo xInfo = SGDataUtility.createDataColumnInfo(xVar, X_VALUE);
	            SGMDArrayDataColumnInfo yInfo = SGDataUtility.createDataColumnInfo(yVar, Y_VALUE);
	            SGMDArrayDataColumnInfo leInfo = null;
	            SGMDArrayDataColumnInfo ueInfo = null;
	            SGMDArrayDataColumnInfo ehInfo = null;
	            if (leVar != null && ueVar != null && ehVar != null) {
		        	leInfo = SGDataUtility.createErrorBarInfo(leVar, LOWER_ERROR_VALUE, leVar, ueVar, ehVar);
		        	ueInfo = SGDataUtility.createErrorBarInfo(ueVar, UPPER_ERROR_VALUE, leVar, ueVar, ehVar);
		        	ehInfo = (SGMDArrayDataColumnInfo) (ehVar.equals(xVar) ? xInfo : yInfo).clone();
	            }
	            SGMDArrayDataColumnInfo tlInfo = null;
	            SGMDArrayDataColumnInfo thInfo = null;
	            if (tlVar != null && thVar != null) {
	            	tlInfo = SGDataUtility.createDataColumnInfo(tlVar, TICK_LABEL, thVar.getName());
		        	thInfo = (SGMDArrayDataColumnInfo) (thVar.equals(xVar) ? xInfo : yInfo).clone();
	            }
	            SGSXYMDArrayData data = new SGSXYMDArrayData((SGMDArrayFile) this.getDataSource(),
	            		this.getDataSourceObserver(),
	            		xInfo, yInfo, leInfo, ueInfo, ehInfo, tlInfo, thInfo, this.mStride,
	            		this.mTickLabelStride, this.isStrideAvailable());
	            data.setDecimalPlaces(this.mDecimalPlaces);
	            data.setExponent(this.mExponent);
	            data.setTimeStride(this.mTimeStride);
	            data.setOrigin(this.getOriginMap());
	            data.setShift(this.mShift);

	            // sets the cache
	            if (sxyCacheArray != null) {
	            	data.setCache(sxyCacheArray[ii]);
	            }

	            dataArray[ii] = (SGISXYTypeSingleData) data;
	        }
	        
	        SGDataUtility.syncDataValueHistory(this.mEditedDataValueList, dataArray);

	        return dataArray;
		}
	}

    /**
     * Returns the number of child data.
     *
     * @return the number of child data
     */
	@Override
    public int getChildNumber() {
		if (this.isDimensionPicked()) {
	        int[] pickUpIndices = this.getPickUpDimensionIndices();
	        return pickUpIndices.length;
		} else {
			SGMDArrayVariable[] varMultiple = this.getMultipleVariables();
	        return varMultiple.length;
		}
    }

    /**
     * Returns an array of multiple SXYData objects.
     *
     * @return an array of multiple SXYData objects
     */
	@Override
	public SGISXYTypeMultipleData[] getSXYTypeMultipleDataArray() {
		if (this.isDimensionPicked()) {
	        SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
	        SGISXYTypeMultipleData[] ret = new SGISXYTypeMultipleData[sxyArray.length];
	        for (int ii = 0; ii < sxyArray.length; ii++) {
	            SGSXYMDArrayData data = (SGSXYMDArrayData) sxyArray[ii];
	            List<SGMDArrayVariable> pickUpVars = this.getPickUpMDArrayVariables();
	            SGMDArrayVariable pickUpVar = pickUpVars.get(0);
	            final int len = pickUpVar.getDimensionLength(
	            		SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
	            Integer pickUpDim = pickUpVar.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
	            ret[ii] = data.toMultiple(this.hasMultipleYValues(), pickUpDim, len);
	        }
	    	// disposes of data objects
	        SGDataUtility.disposeSXYDataArray(sxyArray);
	        return ret;
		} else {
	        SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
	        SGISXYTypeMultipleData[] ret = new SGISXYTypeMultipleData[sxyArray.length];
	        for (int ii = 0; ii < sxyArray.length; ii++) {
	        	SGSXYMDArrayData data = (SGSXYMDArrayData) sxyArray[ii];
	            ret[ii] = data.toMultiple();
	        }
	    	// disposes of data objects
	        SGDataUtility.disposeSXYDataArray(sxyArray);
	        return ret;
		}
	}

    /**
     * Returns true if this data enables to be split.
     *
     * @return true if this data enables to be split
     */
	@Override
	public boolean isSplitEnabled() {
		if (this.isDimensionPicked()) {
			int[] indices = this.getPickUpDimensionIndices();
			return (indices.length > 1);
		} else {
			SGMDArrayVariable[] varMultiple = this.getMultipleVariables();
	        final int len = varMultiple.length;
	        if (len > 1) {
	            return true;
	        }
	        return false;
		}
	}

    /**
     * Returns an array of current column types.
     *
     * @return an array of current column types
     */
	@Override
	public String[] getCurrentColumnType() {
        SGMDArrayVariable[] vars = this.getVariables();
        String[] array = new String[vars.length];
        for (int ii = 0; ii < array.length; ii++) {
        	SGMDArrayVariable var = vars[ii];
            final int lIndex = this.find(var, this.mLowerErrorVariables);
            final int uIndex = this.find(var, this.mUpperErrorVariables);
            final int tIndex = this.find(var, this.mTickLabelVariables);
            if (this.find(var, this.mXVariables) != -1) {
                array[ii] = X_VALUE;
            } else if (this.find(var, this.mYVariables) != -1) {
                array[ii] = Y_VALUE;
            } else if ((lIndex != -1) || (uIndex != -1)) {
                String name = null;
                int index = -1;
                if (lIndex == uIndex) {
                    name = LOWER_UPPER_ERROR_VALUE;
                    index = lIndex;
                } else if (lIndex != -1) {
                    name = LOWER_ERROR_VALUE;
                    index = lIndex;
                } else if (uIndex != -1) {
                    name = UPPER_ERROR_VALUE;
                    index = uIndex;
                }
                if (name == null) {
                    return null;
                }
                String varName = this.mErrorBarHolderVariables[index].getName();
                final int hIndex = this.getVariableIndex(varName);
                if (hIndex == -1) {
                    return null;
                }
                array[ii] = SGDataUtility.appendColumnTitle(name, varName);
            } else if (tIndex != -1) {
                String varName = this.mTickLabelHolderVariables[tIndex].getName();
                final int hIndex = this.getVariableIndex(varName);
                if (hIndex == -1) {
                    return null;
                }
                array[ii] = SGDataUtility.appendColumnTitle(TICK_LABEL, varName);
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
     *            an array of column types
     * @return true if succeeded
     */
	@Override
    public boolean setColumnType(String[] columns) {
		if (this.isDimensionPicked()) {
			return this.setColumnTypeDimensionPicked(columns);
		} else {
			return this.setColumnTypeDimensionNotPicked(columns);
		}
    }

    /**
     * Returns a map which has data information.
     *
     * @return a map which has data information
     */
    @Override
    public Map<String, Object> getInfoMap() {
        Map<String, Object> infoMap = super.getInfoMap();
        infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.TRUE);
        if (this.isDimensionPicked()) {
            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES,
            		this.mPickUpDimensionInfo.getIndices());
            Map<String, Integer> dimensionIndexMap = new HashMap<String, Integer>();
            SGMDArrayVariable[] vars = this.getVariables();
            for (int ii = 0; ii < vars.length; ii++) {
            	String name = vars[ii].getName();
            	Integer index = vars[ii].getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
            	if (index != null && index != -1) {
            		dimensionIndexMap.put(name, index);
            	}
            }
            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP,
            		dimensionIndexMap);

        } else {
            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
        }
        return infoMap;
    }

    /**
     * Sets a given data.
     *
     * @param data
     *           a data
     * @return true if succeeded
     */
    @Override
    public boolean setData(SGData data) {
        if (!(data instanceof SGSXYMDArrayMultipleData)) {
            throw new IllegalArgumentException("Given data is not the instance of SGSXYMultipleMDArrayData class.");
        }
        if (super.setData(data) == false) {
            return false;
        }
        SGSXYMDArrayMultipleData mdData = (SGSXYMDArrayMultipleData) data;
        this.mXVariables = mdData.getXVariables();
        this.mYVariables = mdData.getYVariables();
        if (mdData.isErrorBarAvailable()) {
        	this.mLowerErrorVariables = this.createVariableArray(mdData.getLowerErrorVariable());
        	this.mUpperErrorVariables = this.createVariableArray(mdData.getUpperErrorVariable());
        	this.mErrorBarHolderVariables = this.createVariableArray(mdData.getErrorBarHolderVariable());
        }
        if (mdData.isTickLabelAvailable()) {
        	this.mTickLabelVariables = this.createVariableArray(mdData.getTickLabelVariable());
        	this.mTickLabelHolderVariables = this.createVariableArray(mdData.getTickLabelHolderVariable());
        }
        if (mdData.mPickUpDimensionInfo != null) {
            this.mPickUpDimensionInfo = (SGMDArrayPickUpDimensionInfo) mdData.mPickUpDimensionInfo.clone();
        }
        this.mStride = mdData.getStride();
        this.mTickLabelStride = mdData.getTickLabelStride();
        this.updateDimensionIndices();
        this.mShift = mdData.getShift();
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
	public boolean writeProperty(Element el, SGExportParameter params) {
        if (super.writeProperty(el, params) == false) {
            return false;
        }
        OPERATION type = params.getType();
    	if (SGDataUtility.isArchiveDataSetOperation(type)
    			|| OPERATION.SAVE_TO_PROPERTY_FILE.equals(type)) {
    		
            String value = null;
            if (this.isDimensionPicked()) {
            	SGMDArrayVariable xVar = this.getXVariable();
            	SGMDArrayVariable yVar = this.getYVariable();
            	if (xVar != null) {
                    value = this.bindVariableNames(xVar, true);
                    el.setAttribute(KEY_X_VALUE_NAME, value);
            	}
            	if (yVar != null) {
                    value = this.bindVariableNames(yVar, true);
                    el.setAttribute(KEY_Y_VALUE_NAME, value);
            	}
                if (this.isErrorBarAvailable()) {
                	SGMDArrayVariable lVar = this.getLowerErrorVariable();
                    value = this.bindVariableNames(lVar, true);
                    el.setAttribute(KEY_LOWER_ERROR_VALUE_NAME, value);
                	SGMDArrayVariable uVar = this.getUpperErrorVariable();
                    value = this.bindVariableNames(uVar, true);
                    el.setAttribute(KEY_UPPER_ERROR_VALUE_NAME, value);
                    SGMDArrayVariable hVar = this.getErrorBarHolderVariable();
                    value = this.bindVariableNames(hVar, false);
                    el.setAttribute(KEY_ERROR_BAR_HOLDER_NAME, value);
                }
                if (this.isTickLabelAvailable()) {
                	SGMDArrayVariable tVar = this.getTickLabelVariable();
                    value = this.bindVariableNames(tVar, true);
                    el.setAttribute(KEY_TICK_LABEL_NAME, value);
                    SGMDArrayVariable hVar = this.getTickLabelHolderVariable();
                    value = this.bindVariableNames(hVar, false);
                    el.setAttribute(KEY_TICK_LABEL_HOLDER_NAME, value);
                }

                String pickUpIndicesStr = this.mPickUpDimensionInfo.getIndices().toString();
                el.setAttribute(KEY_PICK_UP_DIMENSION_INDICES, pickUpIndicesStr);

                StringBuffer sb = new StringBuffer();
                SGMDArrayVariable[] vars = this.getVariables();
                int cnt = 0;
                for (int ii = 0; ii < vars.length; ii++) {
                	Integer index = vars[ii].getDimensionIndex(
                			SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
                	if (index == null || index == -1) {
                		continue;
                	}
                	if (cnt > 0) {
                		sb.append(',');
                	}
                	sb.append(vars[ii].getName());
                	sb.append(':');
                	sb.append(index);
                	cnt++;
                }
                el.setAttribute(KEY_PICK_UP_DIMENSION, sb.toString());

            } else {
                value = this.bindVariableNamesInBracket(this.mXVariables, true);
                el.setAttribute(KEY_X_VALUE_NAMES, value);
                value = this.bindVariableNamesInBracket(this.mYVariables, true);
                el.setAttribute(KEY_Y_VALUE_NAMES, value);
                if (this.isErrorBarAvailable()) {
                    value = this.bindVariableNamesInBracket(this.mLowerErrorVariables, true);
                    el.setAttribute(KEY_LOWER_ERROR_VALUE_NAMES, value);
                    value = this.bindVariableNamesInBracket(this.mUpperErrorVariables, true);
                    el.setAttribute(KEY_UPPER_ERROR_VALUE_NAMES, value);
                    value = this.bindVariableNamesInBracket(this.mErrorBarHolderVariables, false);
                    el.setAttribute(KEY_ERROR_BAR_HOLDER_NAMES, value);
                }
                if (this.isTickLabelAvailable()) {
                    value = this.bindVariableNamesInBracket(this.mTickLabelVariables, true);
                    el.setAttribute(KEY_TICK_LABEL_NAMES, value);
                    value = this.bindVariableNamesInBracket(this.mTickLabelHolderVariables, false);
                    el.setAttribute(KEY_TICK_LABEL_HOLDER_NAMES, value);
                }
            }

            // stride
            el.setAttribute(KEY_ARRAY_SECTION, this.mStride.toString());
            if (this.isTickLabelAvailable()) {
                el.setAttribute(KEY_TICK_LABEL_ARRAY_SECTION, this.mTickLabelStride.toString());
            }

    	} else if (OPERATION.SAVE_TO_DATA_SET_NETCDF.equals(type)) {

    		// get variable names
    		List<String> xNameList = new ArrayList<String>();
    		List<String> yNameList = new ArrayList<String>();
    		List<String> leNameList = new ArrayList<String>();
    		List<String> ueNameList = new ArrayList<String>();
    		List<String> tlNameList = new ArrayList<String>();
    		this.getVariableNames(xNameList, yNameList, leNameList, ueNameList, tlNameList);

            // index variable
            el.setAttribute(SGIDataPropertyKeyConstants.KEY_INDEX_VARIABLE_NAME, INDEX_DIM_NAME);

            // stride as the index stride
            el.setAttribute(SGIDataPropertyKeyConstants.KEY_INDEX_ARRAY_SECTION, this.mStride.toString());

            if (this.isTickLabelAvailable()) {
                el.setAttribute(SGIDataPropertyKeyConstants.KEY_TICK_LABEL_ARRAY_SECTION, this.mTickLabelStride.toString());
            }

            String value = null;
            if (xNameList.size() == 0) {
            	value = this.bindVariableNameInBracket(X_VALUE_VAR_NAME);
            } else {
                value = SGDataUtility.bindVariableNamesInBracket(xNameList);
            }
            el.setAttribute(KEY_X_VALUE_NAMES, value);

            if (yNameList.size() == 0) {
            	value = this.bindVariableNameInBracket(Y_VALUE_VAR_NAME);
            } else {
                value = SGDataUtility.bindVariableNamesInBracket(yNameList);
            }
            el.setAttribute(KEY_Y_VALUE_NAMES, value);

            if (this.isErrorBarAvailable()) {
                value = SGDataUtility.bindVariableNamesInBracket(leNameList);
                el.setAttribute(KEY_LOWER_ERROR_VALUE_NAMES, value);
                value = SGDataUtility.bindVariableNamesInBracket(ueNameList);
                el.setAttribute(KEY_UPPER_ERROR_VALUE_NAMES, value);
                value = SGDataUtility.bindVariableNamesInBracket(this.mErrorBarHolderVariables);
                el.setAttribute(KEY_ERROR_BAR_HOLDER_NAMES, value);
            }

            if (this.isTickLabelAvailable()) {
                value = SGDataUtility.bindVariableNamesInBracket(tlNameList);
                el.setAttribute(KEY_TICK_LABEL_NAMES, value);
                value = SGDataUtility.bindVariableNamesInBracket(this.mTickLabelHolderVariables);
                el.setAttribute(KEY_TICK_LABEL_HOLDER_NAMES, value);
            }

            if (this.isDimensionPicked()) {
                el.setAttribute(SGIDataPropertyKeyConstants.KEY_PICKUP_DIMENSION_NAME, PICKUP_DIM_NAME);
                el.setAttribute(KEY_PICK_UP_DIMENSION_INDICES, this.mPickUpDimensionInfo.getIndices().toString());
            }
    	}

		return true;
	}

	private String bindVariableNameInBracket(String name) {
		StringBuffer sb = new StringBuffer();
		sb.append('{');
		sb.append(name);
		sb.append('}');
		return sb.toString();
	}


    private String bindVariableNamesInBracket(SGMDArrayVariable[] variables,
    		final boolean withDimensionIndex) {
        StringBuffer sb = new StringBuffer("{");
        for (int ii = 0; ii < variables.length; ii++) {
            if (ii > 0) {
                sb.append(",");
            }
            SGMDArrayVariable var = variables[ii];
            sb.append(var.getName());
            if (withDimensionIndex) {
                sb.append(":");
                sb.append(var.getGenericDimensionIndex());
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private String bindVariableNames(SGMDArrayVariable var,
    		final boolean withDimensionIndex) {
        StringBuffer sb = new StringBuffer();
        sb.append(var.getName());
        if (withDimensionIndex) {
            sb.append(":");
            sb.append(var.getGenericDimensionIndex());
        }
        return sb.toString();
    }

    public static SGISXYTypeMultipleData merge(final List<SGData> dataList) {
        if (dataList.size() == 0) {
            return null;
        }

        // checks whether all data is picked up or not
        Boolean dimensionPicked = null;
        for (SGData data : dataList) {
            if ((data instanceof SGSXYMDArrayMultipleData)==false) {
                return null;
            }
            SGSXYMDArrayMultipleData mdData = (SGSXYMDArrayMultipleData) data;
            final boolean b = mdData.isDimensionPicked();
            if (dimensionPicked == null) {
            	dimensionPicked = b;
            } else {
            	if (!dimensionPicked.equals(b)) {
            		return null;
            	}
            }
        }
        if (dimensionPicked == null) {
        	return null;
        }

		// checks the length
		int dimLen = -1;
		for (SGData data : dataList) {
			SGSXYMDArrayMultipleData dataMult = (SGSXYMDArrayMultipleData) data;
			final int len = dataMult.getAllPointsNumber();
			if (dimLen == -1) {
				dimLen = len;
			} else {
				if (len != dimLen) {
					return null;
				}
			}
		}

		SGSXYMDArrayMultipleData dataLast = (SGSXYMDArrayMultipleData) dataList.get(dataList.size() - 1);
		SGDataSourceObserver obs = dataLast.getDataSourceObserver();

        if (dimensionPicked) {
			SGMDArrayDataColumnInfo xInfo = SGDataUtility.createDataColumnInfo(dataLast
					.getXVariable(), X_VALUE);
			SGMDArrayDataColumnInfo yInfo = SGDataUtility.createDataColumnInfo(dataLast
					.getYVariable(), Y_VALUE);
			SGMDArrayDataColumnInfo leInfo = (null != dataLast
					.getLowerErrorVariable()) ? SGDataUtility.createDataColumnInfo(dataLast
					.getLowerErrorVariable(), LOWER_ERROR_VALUE) : null;
			SGMDArrayDataColumnInfo ueInfo = (null != dataLast
					.getUpperErrorVariable()) ? SGDataUtility.createDataColumnInfo(dataLast
					.getUpperErrorVariable(), UPPER_ERROR_VALUE) : null;
			SGMDArrayDataColumnInfo tlInfo = (null != dataLast
					.getTickLabelVariable()) ? SGDataUtility.createDataColumnInfo(dataLast
					.getTickLabelVariable(), TICK_LABEL) : null;

			// get dimension indices
			int len = -1;
			if (xInfo != null) {
				Integer index = xInfo.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
				if (index != null && index != -1) {
					int[] dims = xInfo.getDimensions();
					len = dims[index];
				}
			}
			if (yInfo != null && len == -1) {
				Integer index = yInfo.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
				if (index != null && index != -1) {
					int[] dims = yInfo.getDimensions();
					len = dims[index];
				}
			}
			if (len == -1) {
				return null;
			}
			SGIntegerSeriesSet indices = SGDataUtility.getDimensionSeries(dataList, len);
			if (indices == null) {
				return null;
			}
			indices.addAlias(len - 1, SGIntegerSeries.ARRAY_INDEX_END);

			SGSXYMDArrayMultipleData data = new SGSXYMDArrayMultipleData(
					dataLast.getMDArrayFile(), obs, xInfo, yInfo, leInfo, ueInfo,
					tlInfo, indices, dataLast.getStride(), dataLast.getTickLabelStride(),
					dataLast.isStrideAvailable());
			data.setDecimalPlaces(dataLast.getDecimalPlaces());
			data.setExponent(dataLast.getExponent());
			data.setTimeStride(dataLast.getTimeStride());
			return data;

        } else {

			List<SGMDArrayVariable> xList = new ArrayList<SGMDArrayVariable>();
			List<SGMDArrayVariable> yList = new ArrayList<SGMDArrayVariable>();
			List<SGMDArrayVariable> leList = new ArrayList<SGMDArrayVariable>();
			List<SGMDArrayVariable> ueList = new ArrayList<SGMDArrayVariable>();
			List<SGMDArrayVariable> ehList = new ArrayList<SGMDArrayVariable>();
			List<SGMDArrayVariable> tlList = new ArrayList<SGMDArrayVariable>();
			List<SGMDArrayVariable> thList = new ArrayList<SGMDArrayVariable>();
			for (SGData data : dataList) {
				SGSXYMDArrayMultipleData dataMult = (SGSXYMDArrayMultipleData) data;

				SGMDArrayVariable[] xVars = dataMult.getXVariables();
				SGMDArrayVariable[] yVars = dataMult.getYVariables();
				SGMDArrayVariable[] leVars = dataMult.getLowerErrorVariables();
				SGMDArrayVariable[] ueVars = dataMult.getUpperErrorVariables();
				SGMDArrayVariable[] ehVars = dataMult.getErrorHolderVariables();
				SGMDArrayVariable[] tlVars = dataMult.getTickLabelVariables();
				SGMDArrayVariable[] thVars = dataMult.getTickLabelHolderVariables();
				for (int ii = 0; ii < xVars.length; ii++) {
					xList.add(xVars[ii]);
				}
				for (int ii = 0; ii < yVars.length; ii++) {
					yList.add(yVars[ii]);
				}
				if (dataMult.isErrorBarAvailable()) {
					for (int ii = 0; ii < leVars.length; ii++) {
						leList.add(leVars[ii]);
					}
					for (int ii = 0; ii < ueVars.length; ii++) {
						ueList.add(ueVars[ii]);
					}
					for (int ii = 0; ii < ehVars.length; ii++) {
						ehList.add(ehVars[ii]);
					}
				}
				if (dataMult.isTickLabelAvailable()) {
					for (int ii = 0; ii < tlVars.length; ii++) {
						tlList.add(tlVars[ii]);
					}
					for (int ii = 0; ii < thVars.length; ii++) {
						thList.add(thVars[ii]);
					}
				}
			}

			List<SGMDArrayVariable> xListNew = new ArrayList<SGMDArrayVariable>();
			for (SGMDArrayVariable var : xList) {
				if (!xListNew.contains(var)) {
					xListNew.add(var);
				}
			}
			List<SGMDArrayVariable> yListNew = new ArrayList<SGMDArrayVariable>();
			for (SGMDArrayVariable var : yList) {
				if (!yListNew.contains(var)) {
					yListNew.add(var);
				}
			}

			SGMDArrayVariable[] x = xListNew.toArray(new SGMDArrayVariable[xListNew.size()]);
			SGMDArrayDataColumnInfo[] xInfo = SGDataUtility.createDataColumnInfoArray(x, X_VALUE);

			SGMDArrayVariable[] y = yListNew.toArray(new SGMDArrayVariable[yListNew.size()]);
			SGMDArrayDataColumnInfo[] yInfo = SGDataUtility.createDataColumnInfoArray(y, Y_VALUE);

			SGMDArrayDataColumnInfo[] leInfo = new SGMDArrayDataColumnInfo[leList.size()];
			SGMDArrayDataColumnInfo[] ueInfo = new SGMDArrayDataColumnInfo[ueList.size()];
			SGMDArrayDataColumnInfo[] ehInfo = new SGMDArrayDataColumnInfo[ehList.size()];
			for (int ii = 0; ii < leInfo.length; ii++) {
				String leColumnType, ueColumnType, ehColumnType;
				SGMDArrayVariable leVar = leList.get(ii);
				SGMDArrayVariable ueVar = ueList.get(ii);
				SGMDArrayVariable ehVar = ehList.get(ii);
				StringBuffer sb = new StringBuffer();
				final boolean common = leVar.equals(ueVar);
				String ehName = ehVar.getName();

				// lower error
				if (common) {
					sb.append(LOWER_UPPER_ERROR_VALUE);
				} else {
					sb.append(LOWER_ERROR_VALUE);
				}
				sb.append(SGDataUtility.MID_COLUMN);
				sb.append(ehName);
				leColumnType = sb.toString();

				// upper error
				if (common) {
					ueColumnType = leColumnType;
				} else {
					sb.setLength(0);
					sb.append(UPPER_ERROR_VALUE);
					sb.append(SGDataUtility.MID_COLUMN);
					sb.append(ehName);
					ueColumnType = sb.toString();
				}

				// error bar holder
				SGDataColumnInfo ehInfoX = SGDataUtility.findColumnWithName(xInfo, ehName);
				ehColumnType = (ehInfoX != null) ? X_VALUE : Y_VALUE;

				leInfo[ii] = new SGMDArrayDataColumnInfo(leVar, null, leVar.getValueType());
				leInfo[ii].setColumnType(leColumnType);
				ueInfo[ii] = new SGMDArrayDataColumnInfo(ueVar, null, ueVar.getValueType());
				ueInfo[ii].setColumnType(ueColumnType);
				ehInfo[ii] = new SGMDArrayDataColumnInfo(ehVar, null, ehVar.getValueType());
				ehInfo[ii].setColumnType(ehColumnType);
			}

			SGMDArrayDataColumnInfo[] tlInfo = new SGMDArrayDataColumnInfo[tlList.size()];
			SGMDArrayDataColumnInfo[] thInfo = new SGMDArrayDataColumnInfo[thList.size()];
			for (int ii = 0; ii < tlInfo.length; ii++) {
				String tlColumnType, thColumnType;
				SGMDArrayVariable tlVar = tlList.get(ii);
				SGMDArrayVariable thVar = thList.get(ii);
				StringBuffer sb = new StringBuffer();
				String thName = thVar.getName();

				// tick label
				sb.append(TICK_LABEL);
				sb.append(SGDataUtility.MID_COLUMN);
				sb.append(thName);
				tlColumnType = sb.toString();

				// error bar holder
				SGDataColumnInfo thInfoX = SGDataUtility.findColumnWithName(xInfo, thName);
				thColumnType = (thInfoX != null) ? X_VALUE : Y_VALUE;

				tlInfo[ii] = new SGMDArrayDataColumnInfo(tlVar, null, tlVar.getValueType());
				tlInfo[ii].setColumnType(tlColumnType);
				thInfo[ii] = new SGMDArrayDataColumnInfo(thVar, null, thVar.getValueType());
				thInfo[ii].setColumnType(thColumnType);
			}

			SGSXYMDArrayMultipleData data = new SGSXYMDArrayMultipleData(
					dataLast.getMDArrayFile(), obs, xInfo, yInfo, leInfo, ueInfo, ehInfo, tlInfo, thInfo,
					dataLast.mStride,
					dataLast.mTickLabelStride, dataLast.isStrideAvailable());
            data.setOrigin(dataLast.getOriginMap());
			data.setDecimalPlaces(dataLast.getDecimalPlaces());
			data.setExponent(dataLast.getExponent());
			data.setTimeStride(dataLast.getTimeStride());

			return data;
        }
    }

    /**
     * Returns the stride.
     *
     * @return the stride
     */
    public SGIntegerSeriesSet getStride() {
    	if (this.mStride != null) {
        	return (SGIntegerSeriesSet) this.mStride.clone();
    	} else {
    		return null;
    	}
    }

    /**
     * Sets the stride.
     *
     * @param stride
     *           the stride
     */
    public void setStride(final SGIntegerSeriesSet stride) {
    	if (!SGUtility.equals(this.mStride, stride)) {
    		this.clearCache();
    	}
    	if (stride != null) {
        	this.mStride = (SGIntegerSeriesSet) stride.clone();
    	} else {
    		this.mStride = null;
    	}
    }

    /**
     * Returns the copy of this data object.
     *
     * @return
     *         a copy of this data object
     */
    public Object clone() {
        SGSXYMDArrayMultipleData data = (SGSXYMDArrayMultipleData) super.clone();
        data.mXVariables = copyVariables(this.mXVariables);
        data.mYVariables = copyVariables(this.mYVariables);
        data.mLowerErrorVariables = copyVariables(this.mLowerErrorVariables);
        data.mUpperErrorVariables = copyVariables(this.mUpperErrorVariables);
        data.mErrorBarHolderVariables = copyVariables(this.mErrorBarHolderVariables);
        data.mTickLabelVariables = copyVariables(this.mTickLabelVariables);
        data.mTickLabelHolderVariables = copyVariables(this.mTickLabelHolderVariables);
        data.mPickUpDimensionInfo = (SGMDArrayPickUpDimensionInfo) this.getPickUpDimensionInfo();
        data.mStride = this.getStride();
        data.mTickLabelStride = this.getTickLabelStride();
        data.mShift = this.getShift();
        return data;
    }

    /**
     * Sets the stride of the tick labels.
     *
     * @param stride
     *           stride of arrays
     */
	@Override
	public void setTickLabelStride(SGIntegerSeriesSet stride) {
		if (stride != null) {
			this.mTickLabelStride = (SGIntegerSeriesSet) stride.clone();
		} else {
			this.mTickLabelStride = null;
		}
	}

    /**
     * Returns the stride of the tick labels.
     *
     * @return the stride of the tick labels
     */
	@Override
    public SGIntegerSeriesSet getTickLabelStride() {
    	SGIntegerSeriesSet ret = null;
    	if (this.mTickLabelStride != null) {
        	ret = (SGIntegerSeriesSet) this.mTickLabelStride.clone();
    	}
    	return ret;
    }

    /**
     * Returns a map of stride for data arrays.
     *
     * @return a map of stride for data arrays
     */
    @Override
    protected Map<String, SGIntegerSeriesSet> getStrideMap() {
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    	map.put(SGIDataInformationKeyConstants.KEY_SXY_STRIDE, this.mStride);
    	map.put(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE, this.mTickLabelStride);
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
    	this.mStride = map.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
    }

    /**
     * Returns the indices of tick labels.
     *
     * @return the indices of tick labels
     */
    @Override
    public int[] getTickLabelValueIndices() {
    	if (!this.isTickLabelAvailable()) {
    		return null;
    	}
    	if (this.isStrideAvailable()) {
    		return this.mTickLabelStride.getNumbers();
    	} else {
    		final int len = this.getPointsNumber();
    		return SGUtilityNumber.toIntArray(len);
    	}
    }

    /**
     * Returns an index array of dimension.
     *
     * @return an index array of dimension
     */
    public int[] getDimensionIndices() {
    	return this.getPickUpDimensionIndices();
    }

    /**
     * Returns the number of data points without taking into account the stride.
     *
     * @return the number of data points without taking into account the stride
     */
    @Override
    public int getAllPointsNumber() {
		SGMDArrayVariable var;
		if (this.isDimensionPicked()) {
			SGMDArrayVariable xVar = this.getXVariable();
			if (xVar != null) {
				var = xVar;
			} else {
				var = this.getYVariable();
			}
		} else {
			SGMDArrayVariable singleVar = this.getSingleVariable();
			if (singleVar != null) {
				var = singleVar;
			} else {
				SGMDArrayVariable[] multipleVars = this.getMultipleVariables();
				var = multipleVars[0];
			}
		}
		return var.getGenericDimensionLength();
    }

    /**
     * Sets the information of data columns.
     *
     * @param cols
     *            an array of column information
     * @param info
     *            information of picked up column
     * @return true if succeeded
     */
	public boolean setColumnType(SGDataColumnInfo[] cols, SGPickUpDimensionInfo info) {

		// set dimensions
		if (this.setDimensionMap(cols) == false) {
			return false;
		}

		// set pick up information
		this.setPickUpDimensionInfo(info);

		// set column types
		String[] columns = new String[cols.length];
		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii] = cols[ii].getColumnType();
		}
		if (info != null) {
			if (!this.setColumnTypeDimensionPicked(columns)) {
				// clears pick up information
				this.setPickUpDimensionInfo(null);
				return false;
			}
			return true;
		} else {
			return this.setColumnTypeDimensionNotPicked(columns);
		}
	}

	private boolean setColumnTypeDimensionPicked(String[] columns) {
        List<SGMDArrayVariable> xVarList = new ArrayList<SGMDArrayVariable>();
        List<SGMDArrayVariable> yVarList = new ArrayList<SGMDArrayVariable>();
        List<SGMDArrayVariable> lVarList = new ArrayList<SGMDArrayVariable>();
        List<SGMDArrayVariable> uVarList = new ArrayList<SGMDArrayVariable>();
        List<SGMDArrayVariable> tVarList = new ArrayList<SGMDArrayVariable>();
    	List<String> lColList = new ArrayList<String>();
    	List<String> uColList = new ArrayList<String>();
    	List<String> tColList = new ArrayList<String>();
        if (!this.getVariables(columns, xVarList, yVarList, lVarList, uVarList, tVarList,
        		lColList, uColList, tColList)) {
        	return false;
        }

        SGMDArrayVariable xVar = null;
        if (xVarList.size() == 1) {
            xVar = xVarList.get(0);
        }
        SGMDArrayVariable yVar = null;
        if (yVarList.size() == 1) {
            yVar = yVarList.get(0);
        }
        if (xVar == null && yVar == null) {
        	return false;
        }
        SGMDArrayVariable lVar = lVarList.size() == 1 ? lVarList.get(0) : null;
        SGMDArrayVariable uVar = uVarList.size() == 1 ? uVarList.get(0) : null;
        SGMDArrayVariable tVar = tVarList.size() == 1 ? tVarList.get(0) : null;
        if (!((lVar != null) && (uVar != null)) && !((lVar == null) && (uVar == null))) {
        	return false;
        }

        List<SGMDArrayVariable> ehVarList = this.findErrorVarHolderVars(xVarList, yVarList,
        		lVarList, uVarList, lColList, uColList);
        SGMDArrayVariable ehVar = null;
        if (ehVarList != null && ehVarList.size() == 1) {
            ehVar = ehVarList.get(0);
        }

        List<SGMDArrayVariable> thVarList = this.findTickLabelHolderVars(xVarList, yVarList,
        		tVarList, tColList);
        SGMDArrayVariable thVar = null;
        if (thVarList != null && thVarList.size() == 1) {
        	thVar = thVarList.get(0);
        }

        // set variables
        this.mXVariables = this.createVariableArray(xVar);
        this.mYVariables = this.createVariableArray(yVar);
        this.mLowerErrorVariables = this.createVariableArray(lVar);
        this.mUpperErrorVariables = this.createVariableArray(uVar);
        this.mErrorBarHolderVariables = this.createVariableArray(ehVar);
        this.mTickLabelVariables = this.createVariableArray(tVar);
        this.mTickLabelHolderVariables = this.createVariableArray(thVar);

        return true;
	}

	private boolean setColumnTypeDimensionNotPicked(String[] columns) {
        List<SGMDArrayVariable> xVarList = new ArrayList<SGMDArrayVariable>();
        List<SGMDArrayVariable> yVarList = new ArrayList<SGMDArrayVariable>();
        List<SGMDArrayVariable> lVarList = new ArrayList<SGMDArrayVariable>();
        List<SGMDArrayVariable> uVarList = new ArrayList<SGMDArrayVariable>();
        List<SGMDArrayVariable> tVarList = new ArrayList<SGMDArrayVariable>();
    	List<String> lColList = new ArrayList<String>();
    	List<String> uColList = new ArrayList<String>();
    	List<String> tColList = new ArrayList<String>();
        if (!this.getVariables(columns, xVarList, yVarList, lVarList, uVarList, tVarList,
        		lColList, uColList, tColList)) {
        	return false;
        }

        List<SGMDArrayVariable> ehVarList = this.findErrorVarHolderVars(xVarList, yVarList,
        		lVarList, uVarList, lColList, uColList);
        if (ehVarList == null) {
        	return false;
        }
        List<SGMDArrayVariable> thVarList = this.findTickLabelHolderVars(xVarList, yVarList,
        		tVarList, tColList);
        if (thVarList == null) {
        	return false;
        }

        SGMDArrayVariable[] xVars = xVarList.toArray(new SGMDArrayVariable[xVarList.size()]);
        SGMDArrayVariable[] yVars = yVarList.toArray(new SGMDArrayVariable[yVarList.size()]);
        SGMDArrayVariable[] lVars = null;
        SGMDArrayVariable[] uVars = null;
        SGMDArrayVariable[] ehVars = null;
        if (lVarList.size() != 0) {
            lVars = lVarList.toArray(new SGMDArrayVariable[lVarList.size()]);
            uVars = uVarList.toArray(new SGMDArrayVariable[uVarList.size()]);
            ehVars = ehVarList.toArray(new SGMDArrayVariable[ehVarList.size()]);
        }
        SGMDArrayVariable[] tVars = null;
        SGMDArrayVariable[] thVars = null;
        if (tVarList.size() != 0) {
            tVars = tVarList.toArray(new SGMDArrayVariable[tVarList.size()]);
            thVars = thVarList.toArray(new SGMDArrayVariable[thVarList.size()]);
        }

        // set to the attributes
        this.mXVariables = xVars;
        this.mYVariables = yVars;
        this.mLowerErrorVariables = lVars;
        this.mUpperErrorVariables = uVars;
        this.mErrorBarHolderVariables = ehVars;
        this.mTickLabelVariables = tVars;
        this.mTickLabelHolderVariables = thVars;

        return true;
	}

	private List<SGMDArrayVariable> findErrorVarHolderVars(
			List<SGMDArrayVariable> xVarList, List<SGMDArrayVariable> yVarList,
			List<SGMDArrayVariable> lVarList, List<SGMDArrayVariable> uVarList,
			List<String> lColList, List<String> uColList) {

		SGMDArrayVariable[] vars = this.getVariables();
        List<SGMDArrayVariable> ehVarList = new ArrayList<SGMDArrayVariable>();
        List<Integer> ehList = new ArrayList<Integer>();
        for (int ii = 0; ii < uVarList.size(); ii++) {
            String col = uColList.get(ii);
            Integer eh = SGDataUtility.getColumnIndexOfAppendedColumnTitle(col, this);
            if (eh == null) {
                return null;
            }
            ehList.add(eh);
        }
        for (int ii = 0; ii < lVarList.size(); ii++) {
            String col = lColList.get(ii);
            Integer eh = SGDataUtility.getColumnIndexOfAppendedColumnTitle(col, this);
            if (eh == null) {
                return null;
            }
            if (ehList.contains(eh) == false) {
                return null;
            }
            SGMDArrayVariable ehVar = vars[eh.intValue()];
            if (!xVarList.contains(ehVar) && !yVarList.contains(ehVar)) {
            	return null;
            }
            ehVarList.add(ehVar);
        }
        return ehVarList;
	}

	private List<SGMDArrayVariable> findTickLabelHolderVars(
			List<SGMDArrayVariable> xVarList, List<SGMDArrayVariable> yVarList,
			List<SGMDArrayVariable> tVarList, List<String> tColList) {

		SGMDArrayVariable[] vars = this.getVariables();
        List<SGMDArrayVariable> thVarList = new ArrayList<SGMDArrayVariable>();
        List<Integer> thList = new ArrayList<Integer>();
        for (int ii = 0; ii < tVarList.size(); ii++) {
            String col = tColList.get(ii);
            Integer th = SGDataUtility.getColumnIndexOfAppendedColumnTitle(col, this);
            if (th == null) {
                return null;
            }
            thList.add(th);
        }
        for (int ii = 0; ii < tVarList.size(); ii++) {
            String col = tColList.get(ii);
            Integer th = SGDataUtility.getColumnIndexOfAppendedColumnTitle(col, this);
            if (th == null) {
                return null;
            }
            if (thList.contains(th) == false) {
                return null;
            }
            SGMDArrayVariable thVar = vars[th.intValue()];
            if (!xVarList.contains(thVar) && !yVarList.contains(thVar)) {
            	return null;
            }
            thVarList.add(thVar);
        }
        return thVarList;
	}

	private boolean getVariables(String[] columns,
			List<SGMDArrayVariable> xVarList, List<SGMDArrayVariable> yVarList,
			List<SGMDArrayVariable> lVarList, List<SGMDArrayVariable> uVarList,
			List<SGMDArrayVariable> tVarList,
			List<String> lColList, List<String> uColList, List<String> tColList) {

        SGMDArrayVariable[] vars = this.getVariables();
        for (int ii = 0; ii < columns.length; ii++) {
        	SGMDArrayVariable var = vars[ii];
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
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], LOWER_ERROR_VALUE)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                lVarList.add(var);
                lColList.add(columns[ii]);
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], UPPER_ERROR_VALUE)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                uVarList.add(var);
                uColList.add(columns[ii]);
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], LOWER_UPPER_ERROR_VALUE)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                lVarList.add(var);
                uVarList.add(var);
                lColList.add(columns[ii]);
                uColList.add(columns[ii]);
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], TICK_LABEL)) {
                tVarList.add(var);
                tColList.add(columns[ii]);
            } else if ("".equals(columns[ii])) {
            	continue;
            } else {
            	return false;
            }
        }

        // checks common conditions
        if (xVarList.size() == 0 && yVarList.size() == 0) {
        	return false;
        }
        if (xVarList.size() > 1 && yVarList.size() > 1) {
        	return false;
        }
        if (lVarList.size() != uVarList.size()) {
            return false;
        }

        return true;
	}

	/**
	 * Clears all information for picked up dimension.
	 *
	 * @return true if succeeded
	 */
	public boolean clearPickUp() {
		if (!this.isDimensionPicked()) {
			return true;
		}

		// get pick up variable
//		SGMDArrayVariable pickUpVar = this.getPickUpMDArrayVariable();
//		if (pickUpVar == null) {
//			return false;
//		}
		SGDataColumnInfo[] cols = this.getColumnInfo();

		// replaces attributes
		this.mErrorBarHolderVariables = new SGMDArrayVariable[1];
		this.mTickLabelHolderVariables = new SGMDArrayVariable[1];
		for (int ii = 0; ii < cols.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
			String columnType = mdCol.getColumnType();
			String uColumnType = columnType.toUpperCase();
			String cType;
			final boolean ebFlag;
			if (uColumnType.startsWith(LOWER_ERROR_VALUE.toUpperCase())) {
				ebFlag = true;
				cType = LOWER_ERROR_VALUE;
			} else if (uColumnType.startsWith(UPPER_ERROR_VALUE.toUpperCase())) {
				ebFlag = true;
				cType = UPPER_ERROR_VALUE;
			} else if (uColumnType.startsWith(LOWER_UPPER_ERROR_VALUE.toUpperCase())) {
				ebFlag = true;
				cType = LOWER_UPPER_ERROR_VALUE;
			} else if (columnType.startsWith(TICK_LABEL)) {
				ebFlag = false;
				cType = TICK_LABEL;
			} else {
				continue;
			}
			StringBuffer sb = new StringBuffer();
			sb.append(cType);
			sb.append(SGDataUtility.MID_COLUMN);
			final int index = sb.toString().length();
			String holderVarName = columnType.substring(index);
			SGMDArrayVariable holderVar = this.findVariable(holderVarName);
			if (ebFlag) {
				this.mErrorBarHolderVariables[0] = holderVar;
			} else {
				this.mTickLabelHolderVariables[0] = holderVar;
			}
		}

		// clears the dimension index
		for (int ii = 0; ii < cols.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
			mdCol.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, -1);
		}

		// clears the attribute
		this.setPickUpDimensionInfo(null);

		return true;
	}

    /**
     * Returns the map of dimension index that are used.
     *
     * @return the map of dimension index
     */
    public Map<String, Map<String, Integer>> getUsedDimensionIndexMap() {
    	return this.getDimensionIndexMap();
    }

    /**
     * Returns an array of variables that are assigned the column type.
     *
     * @return an array of variables
     */
	@Override
	public SGMDArrayVariable[] getAssignedVariables() {
		List<SGMDArrayVariable> varList = new ArrayList<SGMDArrayVariable>();
		for (SGMDArrayVariable var : this.mXVariables) {
			varList.add(var);
		}
		for (SGMDArrayVariable var : this.mYVariables) {
			varList.add(var);
		}
		if (this.isErrorBarAvailable()) {
			for (SGMDArrayVariable var : this.mLowerErrorVariables) {
				varList.add(var);
			}
			for (SGMDArrayVariable var : this.mUpperErrorVariables) {
				varList.add(var);
			}
		}
		if (this.isTickLabelAvailable()) {
			for (SGMDArrayVariable var : this.mTickLabelVariables) {
				varList.add(var);
			}
		}
		SGMDArrayVariable[] vars = new SGMDArrayVariable[varList.size()];
		return (SGMDArrayVariable[]) varList.toArray(vars);
	}

	private void getVariableNames(List<String> xNameList, List<String> yNameList,
			List<String> leNameList, List<String> ueNameList, List<String> tlNameList) {

		if (this.isDimensionPicked()) {
			if (this.hasMultipleYValues()) {
				if (this.mXVariables.length > 0) {
					xNameList.add(this.mXVariables[0].getName());
				}
				yNameList.add(this.mYVariables[0].getName());
			} else {
				if (this.mYVariables.length > 0) {
					yNameList.add(this.mYVariables[0].getName());
				}
				xNameList.add(this.mXVariables[0].getName());
			}
			if (this.isErrorBarAvailable()) {
				leNameList.add(this.mLowerErrorVariables[0].getName());
				ueNameList.add(this.mUpperErrorVariables[0].getName());
			}
			if (this.isTickLabelAvailable()) {
				tlNameList.add(this.mTickLabelVariables[0].getName());
			}

		} else {
			for (int ii = 0; ii < this.mXVariables.length; ii++) {
				xNameList.add(this.mXVariables[ii].getName());
			}
			for (int ii = 0; ii < this.mYVariables.length; ii++) {
				yNameList.add(this.mYVariables[ii].getName());
			}
			if (this.isErrorBarAvailable()) {
				for (int ii = 0; ii < this.mLowerErrorVariables.length; ii++) {
					leNameList.add(this.mLowerErrorVariables[ii].getName());
				}
				for (int ii = 0; ii < this.mUpperErrorVariables.length; ii++) {
					ueNameList.add(this.mUpperErrorVariables[ii].getName());
				}
			}
			if (this.isTickLabelAvailable()) {
				for (int ii = 0; ii < this.mTickLabelVariables.length; ii++) {
					tlNameList.add(this.mTickLabelVariables[ii].getName());
				}
			}
		}
	}

	static final String INDEX_DIM_NAME = "index";

	static final String PICKUP_DIM_NAME = "pickUp";

	static final String X_VALUE_VAR_NAME = "x";

	static final String Y_VALUE_VAR_NAME = "y";

    /**
     * Adds variables to a netCDF file.
     *
     * @param ncWrite
     *           a netCDF file
     * @return true if succeeded
     */
	@Override
    protected boolean addVariables(NetcdfFileWriteable ncWrite) {

		// get the number of points
		final int num = this.getAllPointsNumber();

		// whether equal variables are assigned to lower and upper errors
		final boolean bEqualLowerUpperErrorVariable = this.hasEqualLowerUpperErrorVariable();

		// whether y variable is picked
		List<SGMDArrayVariable> pickUpVarList = this.getPickUpMDArrayVariables();
		boolean yVariablePicked = false;
		if (this.isDimensionPicked()) {
			if (pickUpVarList.contains(this.mYVariables[0])) {
				yVariablePicked = true;
			}
		}

		// get variable names
		List<String> xNameList = new ArrayList<String>();
		List<String> yNameList = new ArrayList<String>();
		List<String> leNameList = new ArrayList<String>();
		List<String> ueNameList = new ArrayList<String>();
		List<String> tlNameList = new ArrayList<String>();
		this.getVariableNames(xNameList, yNameList, leNameList, ueNameList, tlNameList);

		// add index dimension
		Dimension indexDim = ncWrite.addDimension(INDEX_DIM_NAME, num);

		// add time dimensions
		Dimension timeDim = this.addTimeVariable(ncWrite);

		// add pick up dimension
		Dimension pickUpDim = null;
		if (this.isDimensionPicked()) {
			SGMDArrayPickUpDimensionInfo pickUpInfo = (SGMDArrayPickUpDimensionInfo) this.getPickUpDimensionInfo();
			Map<String, Integer> dimensionMap = pickUpInfo.getDimensionMap();
			Iterator<Entry<String, Integer>> itr = dimensionMap.entrySet().iterator();
			while (itr.hasNext()) {
				Entry<String, Integer> entry = itr.next();
				String name = entry.getKey();
				Integer dimension = entry.getValue();
				if (dimension == null || dimension == -1) {
					continue;
				}
				SGMDArrayVariable var = this.findVariable(name);
				int[] dims = var.getDimensions();
				final int len = dims[dimension];
				pickUpDim = ncWrite.addDimension(PICKUP_DIM_NAME, len);
				break;
			}
		}

		// add index variable
		if (!this.addSequentialIntegerNumberVariable(ncWrite, indexDim, INDEX_DIM_NAME)) {
			return false;
		}

		Dimension pDim;

		// add x-variables
		pDim = (this.isDimensionPicked() && !yVariablePicked) ? pickUpDim : null;
		if (xNameList.size() == 0) {
			if (!this.addDoubleVariable(ncWrite, indexDim, null, pDim, X_VALUE_VAR_NAME)) {
				return false;
			}
		} else {
			for (int ii = 0; ii < xNameList.size(); ii++) {
				String name = xNameList.get(ii);
				if (!this.addDoubleVariable(ncWrite, indexDim, timeDim, pDim, name)) {
					return false;
				}
			}
		}

		// add y-variables
		pDim = (this.isDimensionPicked() && yVariablePicked) ? pickUpDim : null;
		if (yNameList.size() == 0) {
			if (!this.addDoubleVariable(ncWrite, indexDim, null, pDim, Y_VALUE_VAR_NAME)) {
				return false;
			}
		} else {
			for (int ii = 0; ii < yNameList.size(); ii++) {
				String name = yNameList.get(ii);
				if (!this.addDoubleVariable(ncWrite, indexDim, timeDim, pDim, name)) {
					return false;
				}
			}
		}

		// add error variables
		if (this.isErrorBarAvailable()) {
			for (int ii = 0; ii < leNameList.size(); ii++) {
				String name = leNameList.get(ii);
				if (!this.addDoubleVariable(ncWrite, indexDim, timeDim, pickUpDim, name)) {
					return false;
				}
			}
			if (!bEqualLowerUpperErrorVariable) {
				for (int ii = 0; ii < ueNameList.size(); ii++) {
					String name = ueNameList.get(ii);
					if (!this.addDoubleVariable(ncWrite, indexDim, timeDim, pickUpDim, name)) {
						return false;
					}
				}
			}
		}

		// add tick label variables
		if (this.isTickLabelAvailable()) {
			for (int ii = 0; ii < tlNameList.size(); ii++) {
				// adds a dimension for text strings
				String name = tlNameList.get(ii);
                SGMDArrayVariable var = this.findVariable(name);
                final int maxLength = this.getMaxLength(var);
				if (!this.addStringVariable(ncWrite, indexDim, timeDim, pickUpDim,
						name, maxLength)) {
					return false;
				}
			}
		}

		// add time variable
		if (timeDim != null) {
			if (!this.addSequentialIntegerNumberVariable(ncWrite, timeDim, TIME_DIM_NAME)) {
				return false;
			}
		}

		// add pick up variable
		if (pickUpDim != null) {
			if (!this.addSequentialIntegerNumberVariable(ncWrite, pickUpDim, PICKUP_DIM_NAME)) {
				return false;
			}
		}

    	return true;
    }

    protected void addPickUpDimension(SGMDArrayVariable var, Dimension pickUpDim, List<Dimension> dimList) {
    	this.addDimension(var, SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, pickUpDim, dimList);
    }

	private boolean addDoubleVariable(NetcdfFileWriteable ncWrite, Dimension indexDim,
			Dimension timeDim, Dimension pickUpDim, String name) {
		SGMDArrayVariable var = this.findVariable(name);
		List<Dimension> dimList = new ArrayList<Dimension>();
		dimList.add(indexDim);
		dimList = this.addTimeDimension(var, timeDim, dimList);
		this.addPickUpDimension(var, pickUpDim, dimList);
		return this.addDoubleVariable(ncWrite, dimList, name);
	}

	private boolean addStringVariable(NetcdfFileWriteable ncWrite, Dimension indexDim,
			Dimension timeDim, Dimension pickUpDim, String name, final int maxStrLen) {
		SGMDArrayVariable var = this.findVariable(name);
		List<Dimension> dimList = new ArrayList<Dimension>();
		dimList.add(indexDim);
		dimList = this.addTimeDimension(var, timeDim, dimList);
		this.addPickUpDimension(var, pickUpDim, dimList);
		return this.addStringVariable(ncWrite, dimList, name, maxStrLen);
	}

    /**
     * Writes data to a netCDF file.
     *
     * @param ncWrite
     *           a netCDF file
     * @return true if succeeded
     */
    protected boolean writeData(NetcdfFileWriteable ncWrite) {

		// get the number of points
		final int num = this.getAllPointsNumber();

		// whether equal variables are assigned to lower and upper errors
		final boolean bEqualLowerUpperErrorVariable = this.hasEqualLowerUpperErrorVariable();

		// get variable names
		List<String> xNameList = new ArrayList<String>();
		List<String> yNameList = new ArrayList<String>();
		List<String> leNameList = new ArrayList<String>();
		List<String> ueNameList = new ArrayList<String>();
		List<String> tlNameList = new ArrayList<String>();
		this.getVariableNames(xNameList, yNameList, leNameList, ueNameList, tlNameList);

		// index
		Dimension indexDim = ncWrite.findDimension(INDEX_DIM_NAME);
		if (!this.writeSequentialIntegerNumbers(ncWrite, INDEX_DIM_NAME, indexDim.getLength())) {
			return false;
		}

		// time
    	if (!this.writeTimeData(ncWrite)) {
    		return false;
    	}

		// pickup
		Dimension pickUpDim = ncWrite.findDimension(PICKUP_DIM_NAME);
		if (pickUpDim != null) {
			if (!this.writeSequentialIntegerNumbers(ncWrite, PICKUP_DIM_NAME, pickUpDim.getLength())) {
				return false;
			}
		}

		// x-values
		if (xNameList.size() == 0) {
			if (!this.writeSequentialDoubleNumbers(ncWrite, X_VALUE_VAR_NAME, num)) {
				return false;
			}
		} else {
			if (!this.writeDoubleData(ncWrite, xNameList)) {
				return false;
			}
		}

		// y-values
		if (yNameList.size() == 0) {
			if (!this.writeSequentialDoubleNumbers(ncWrite, Y_VALUE_VAR_NAME, num)) {
				return false;
			}
		} else {
			if (!this.writeDoubleData(ncWrite, yNameList)) {
				return false;
			}
		}

		// error values
		if (this.isErrorBarAvailable()) {
			if (!this.writeDoubleData(ncWrite, leNameList)) {
				return false;
			}
			if (!bEqualLowerUpperErrorVariable) {
				if (!this.writeDoubleData(ncWrite, ueNameList)) {
					return false;
				}
			}
		}

		// tick label
		if (this.isTickLabelAvailable()) {
			if (!this.writeStringData(ncWrite, tlNameList)) {
				return false;
			}
		}

    	return true;
    }

    private boolean writeDoubleData(NetcdfFileWriteable ncWrite, List<String> nameList) {
		for (int ii = 0; ii < nameList.size(); ii++) {
			String varName = nameList.get(ii);
			SGMDArrayVariable mdVar = this.findVariable(varName);
			Map<String, Integer> mdArrayIndexMap = new HashMap<String, Integer>();
			Variable ncVar = ncWrite.findVariable(varName);
			List<Dimension> ncDimList = ncVar.getDimensions();
			for (Dimension dim : ncDimList) {
				String dimName = dim.getName();
				Integer index = null;
				if (INDEX_DIM_NAME.equals(dimName)) {
					index = mdVar.getDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION);
				} else if (TIME_DIM_NAME.equals(dimName)) {
					index = mdVar.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
				} else if (PICKUP_DIM_NAME.equals(dimName)) {
					index = mdVar.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
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
		}
		return true;
    }

    private boolean writeStringData(NetcdfFileWriteable ncWrite, List<String> nameList) {
		for (int ii = 0; ii < nameList.size(); ii++) {
			String varName = nameList.get(ii);
			SGMDArrayVariable mdVar = this.findVariable(varName);
			Map<String, Integer> mdArrayIndexMap = new HashMap<String, Integer>();
			Variable ncVar = ncWrite.findVariable(varName);
			List<Dimension> ncDimList = ncVar.getDimensions();
			for (Dimension dim : ncDimList) {
				String dimName = dim.getName();
				Integer index = null;
				if (INDEX_DIM_NAME.equals(dimName)) {
					index = mdVar.getDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION);
				} else if (TIME_DIM_NAME.equals(dimName)) {
					index = mdVar.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
				} else if (PICKUP_DIM_NAME.equals(dimName)) {
					index = mdVar.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
				} else {
					continue;
				}
				mdArrayIndexMap.put(dimName, index);
			}
			final int dimSize = mdArrayIndexMap.size();	// a hidden dimension is contained
			if (dimSize == 1) {
				if (!this.write1DStringArray(ncWrite, varName, ncDimList, mdArrayIndexMap, ii)) {
					return false;
				}
			} else if (dimSize == 2) {
				if (!this.write2DStringArray(ncWrite, varName, ncDimList, mdArrayIndexMap, ii)) {
					return false;
				}
			} else if (dimSize == 3) {
				if (!this.write3DStringArray(ncWrite, varName, ncDimList, mdArrayIndexMap, ii)) {
					return false;
				}
			} else {
				throw new Error("Unsupported dimension size: " + dimSize);
			}
		}
		return true;
    }

    private boolean write1DStringArray(NetcdfFileWriteable ncWrite, String varName,
    		List<Dimension> ncDimList, Map<String, Integer> mdArrayIndexMap, final int index) {
    	SGMDArrayVariable mdVar = this.findVariable(varName);
    	Dimension ncDim = ncDimList.get(0);
    	Integer dimension = mdArrayIndexMap.get(ncDim.getName());
    	final int num = ncDim.getLength();
    	String[] textArray = new String[num];
        for (int ii = 0; ii < num; ii++) {
        	String str = mdVar.getString(dimension, ii);
        	textArray[ii] = SGDataUtility.encodeString(str);
        }
		Array array = new ArrayObject.D1(String.class, num);
		Index tempIndex = array.getIndex();
		for (int ii = 0; ii < num; ii++) {
			array.setObject(tempIndex.set(ii), textArray[ii]);
		}
		if (!this.writeStringArray(ncWrite, varName, array)) {
			return false;
		}
		return true;
    }

    private boolean write2DStringArray(NetcdfFileWriteable ncWrite, String varName,
    		List<Dimension> ncDimList, Map<String, Integer> mdArrayIndexMap, final int index) {
    	SGMDArrayVariable mdVar = this.findVariable(varName);
    	Integer timeDimIndex = mdArrayIndexMap.get(TIME_DIM_NAME);
    	String mdSecondDimName = (timeDimIndex != null) ? SGIMDArrayConstants.KEY_TIME_DIMENSION
    			: SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION;
    	String mapSecondDimName = (timeDimIndex != null) ? TIME_DIM_NAME : PICKUP_DIM_NAME;
    	final int firstDimLen = this.getAllPointsNumber();
    	final int secondDimLen = mdVar.getDimensionLength(mdSecondDimName);
    	Integer dimension1 = mdArrayIndexMap.get(INDEX_DIM_NAME);
    	Integer dimension2 = mdArrayIndexMap.get(mapSecondDimName);
    	String[][] textArray = new String[firstDimLen][secondDimLen];
        for (int ii = 0; ii < firstDimLen; ii++) {
        	for (int jj = 0; jj < secondDimLen; jj++) {
        		int[] origins = mdVar.getOrigins();
        		origins[dimension1] = ii;
        		origins[dimension2] = jj;
            	String str = mdVar.getString(origins);
            	textArray[ii][jj] = SGDataUtility.encodeString(str);
        	}
        }
		Array array = new ArrayObject.D2(String.class, firstDimLen, secondDimLen);
		Index tempIndex = array.getIndex();
		for (int ii = 0; ii < firstDimLen; ii++) {
			for (int jj = 0; jj < secondDimLen; jj++) {
				array.setObject(tempIndex.set(ii, jj), textArray[ii][jj]);
			}
		}
		if (!this.writeStringArray(ncWrite, varName, array)) {
			return false;
		}
		return true;
    }

    private boolean write3DStringArray(NetcdfFileWriteable ncWrite, String varName,
    		List<Dimension> ncDimList, Map<String, Integer> mdArrayIndexMap, final int index) {
    	SGMDArrayVariable mdVar = this.findVariable(varName);
    	final int firstDimLen = this.getAllPointsNumber();
    	final int secondDimLen = mdVar.getDimensionLength(SGIMDArrayConstants.KEY_TIME_DIMENSION);
    	final int thirdDimLen = mdVar.getDimensionLength(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
    	Integer dimension1 = mdArrayIndexMap.get(INDEX_DIM_NAME);
    	Integer dimension2 = mdArrayIndexMap.get(TIME_DIM_NAME);
    	Integer dimension3 = mdArrayIndexMap.get(PICKUP_DIM_NAME);
    	String[][][] textArray = new String[firstDimLen][secondDimLen][thirdDimLen];
        for (int ii = 0; ii < firstDimLen; ii++) {
        	for (int jj = 0; jj < secondDimLen; jj++) {
        		for (int kk = 0; kk < thirdDimLen; kk++) {
            		int[] origins = mdVar.getOrigins();
            		origins[dimension1] = ii;
            		origins[dimension2] = jj;
            		origins[dimension3] = kk;
                	String str = mdVar.getString(origins);
                	textArray[ii][jj][kk] = SGDataUtility.encodeString(str);
        		}
        	}
        }
		Array array = new ArrayObject.D3(String.class, firstDimLen, secondDimLen, thirdDimLen);
		Index tempIndex = array.getIndex();
		for (int ii = 0; ii < firstDimLen; ii++) {
			for (int jj = 0; jj < secondDimLen; jj++) {
        		for (int kk = 0; kk < thirdDimLen; kk++) {
    				array.setObject(tempIndex.set(ii, jj, kk), textArray[ii][jj][kk]);
        		}
			}
		}
		if (!this.writeStringArray(ncWrite, varName, array)) {
			return false;
		}
		return true;
    }

    private int getMaxLength(SGMDArrayVariable var) {
        int maxLength = 0;
        Integer genericIndex = var.getDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION);
        Integer pickUpIndex = var.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
        Integer timeIndex = var.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
        int[] dimension = var.getDimensions();
        final int genericNum = dimension[genericIndex];
        final int pickUpNum = (pickUpIndex != null && pickUpIndex != -1) ? dimension[pickUpIndex] : -1;
        final int timeNum = (timeIndex != null && timeIndex != -1) ? dimension[timeIndex] : -1;
        for (int ii = 0; ii < genericNum; ii++) {
        	if (pickUpNum != -1 && timeNum != -1) {
            	for (int jj = 0; jj < pickUpNum; jj++) {
            		for (int kk = 0; kk < timeNum; kk++) {
            	        int[] origins = var.getOrigins();
            	        origins[genericIndex] = ii;
            	        origins[pickUpIndex] = jj;
            	        origins[timeIndex] = kk;
            	        maxLength = this.getMaxLength(var, origins, maxLength);
            		}
            	}
        	} else if (pickUpNum != -1) {
            	for (int jj = 0; jj < pickUpNum; jj++) {
        	        int[] origins = var.getOrigins();
        	        origins[genericIndex] = ii;
        	        origins[pickUpIndex] = jj;
        	        maxLength = this.getMaxLength(var, origins, maxLength);
            	}

        	} else if (timeNum != -1) {
        		for (int kk = 0; kk < timeNum; kk++) {
        	        int[] origins = var.getOrigins();
        	        origins[genericIndex] = ii;
        	        origins[timeIndex] = kk;
        	        maxLength = this.getMaxLength(var, origins, maxLength);
        		}
        	} else {
        		int[] origins = var.getOrigins();
    	        origins[genericIndex] = ii;
    	        maxLength = this.getMaxLength(var, origins, maxLength);
        	}
        }

        return maxLength;
    }

	private int getMaxLength(SGMDArrayVariable var, int[] origins,
			final int curMaxLength) {
    	int maxLength = curMaxLength;
        String textString = var.getString(origins);
    	byte[] byteArray;
		try {
			byteArray = textString.getBytes(SGIConstants.CHAR_SET_NAME_UTF8);
		} catch (UnsupportedEncodingException e) {
			return maxLength;
		}
    	if (maxLength < byteArray.length) {
    		maxLength = byteArray.length;
    	}
    	return maxLength;
    }

    /**
     * Returns a text string of the data type to save into a NetCDF data set file.
     *
     * @return a text string of the data type to save into a NetCDF data set file
     */
    @Override
    public String getNetCDFDataSetDataType() {
    	if (this.isDimensionPicked()) {
        	return SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA;
    	} else {
        	return SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA;
    	}
    }

	/**
	 * Returns the shift.
	 * 
	 * @return the shift
	 */
	public SGTuple2d getShift() {
		return (SGTuple2d) this.mShift.clone();
	}
	
	/**
	 * Sets the shift.
	 * 
	 * @param shift
	 *           the shift to set
	 */
	public void setShift(SGTuple2d shift) {
		if (shift == null) {
			throw new IllegalArgumentException("shift == null");
		}
		this.mShift = (SGTuple2d) shift.clone();
	}

    /**
     * Returns the list of child objects.
     * 
     * @return the list of child objects
     */
	@Override
    public List<String> getChildNameList() {
		List<String> nameList = new ArrayList<String>();
		if (this.isDimensionPicked()) {
			SGIntegerSeriesSet indices = this.mPickUpDimensionInfo.getIndices();
			int[] indexArray = indices.getNumbers();
			for (int ii = 0; ii < indexArray.length; ii++) {
				nameList.add(Integer.toString(indexArray[ii]));
			}
		} else {
			SGMDArrayVariable[] vars = this.hasMultipleYValues() ? this.mYVariables : this.mXVariables;
			for (SGMDArrayVariable var : vars) {
				nameList.add(var.getName());
			}
		}
		return nameList;
	}

	/**
	 * Returns the list of the name of child objects with given pick up information.
	 * 
	 * @param pickUpInfo
	 *           pick up information
	 * @return the list of the name of child objects with given pick up information
	 */
	public static List<String> getChildNameList(SGMDArrayPickUpDimensionInfo pickUpInfo) {
		List<String> nameList = new ArrayList<String>();
    	SGIntegerSeriesSet indices = pickUpInfo.getIndices();
    	int[] indexArray = indices.getNumbers();
    	for (int ii = 0; ii < indexArray.length; ii++) {
    		nameList.add(Integer.toString(indexArray[ii]));
    	}
    	return nameList;
	}

	/**
	 * Returns the list of the name of child objects with given data columns.
	 * 
	 * @param cols
	 *           data columns
	 * @return the list of the name of child objects with given data columns
	 */
	public static List<String> getChildNameList(SGDataColumnInfo[] cols) {
        int xNum = 0;
        int yNum = 0;
        for (int ii = 0; ii < cols.length; ii++) {
        	String columnType = cols[ii].getColumnType();
        	if (X_VALUE.equals(columnType)) {
        		xNum++;
        	} else if (Y_VALUE.equals(columnType)) {
        		yNum++;
        	}
        }
		List<String> nameList = new ArrayList<String>();
        for (int ii = 0; ii < cols.length; ii++) {
        	String name = null;
        	String columnType = cols[ii].getColumnType();
        	String colName = cols[ii].getName();
        	if (xNum > 1) {
            	if (X_VALUE.equals(columnType)) {
            		name = colName;
            	}
        	} else {
            	if (Y_VALUE.equals(columnType)) {
            		name = colName;
            	}
        	}
        	if (name != null) {
            	nameList.add(name);
        	}
        }
        return nameList;
	}

    /**
     * Creates and returns a data buffer with given array of child indices.
     * 
     * @param param
     *           parameters for data buffer
     * @param indices
     *           array of child indices
     * @return the data buffer
     */
	@Override
    public SGDataBuffer getDataBuffer(SGSXYDataBufferPolicy param, int[] indices) {
		return SGDataUtility.getDataBuffer(this, param, indices);
	}

	static class SXYExportInfo {
		String[] xNames;
		double[][] xValues;
		MDArrayDataType[] xDataTypes;
		boolean xValid;
		String[] yNames;
		double[][] yValues;
		MDArrayDataType[] yDataTypes;
		boolean yValid;
		String[] leNames;
		double[][] leValues;
		MDArrayDataType[] leDataTypes;
		String[] ueNames;
		double[][] ueValues;
		MDArrayDataType[] ueDataTypes;
		String[] tlNames;
		String[][] tlValues;
		MDArrayDataType[] tlDataTypes;
	}
	
	private static final String DEFAULT_VAR_NAME_BASE_X = "X";

	private static final String DEFAULT_VAR_NAME_BASE_Y = "Y";

	private SXYExportInfo exportCommon(SGExportParameter mode, SGDataBufferPolicy policy) {
		SGSXYDataBufferPolicy sxyPolicy = (SGSXYDataBufferPolicy) policy;
		final boolean all = sxyPolicy.isAllValuesGotten();
		final boolean shift = sxyPolicy.isShiftValuesContained();
		final boolean archiveFlag = SGDataUtility.isArchiveDataSetOperation(mode.getType());
		final boolean exportFlag = (shift || (this.hasEffectiveStride() && !all)) && !archiveFlag;
		boolean xValid = true;
		boolean yValid = true;

		// gets arrays of variables in the same order of XY variables
		SGMDArrayVariable[] leVars = null;
		SGMDArrayVariable[] ueVars = null;
		if (this.isErrorBarAvailable()) {
			leVars = this.getVariablesInXYOrder(this.mLowerErrorVariables, this.mErrorBarHolderVariables);
			ueVars = this.getVariablesInXYOrder(this.mUpperErrorVariables, this.mErrorBarHolderVariables);
		}
		SGMDArrayVariable[] tlVars = null;
		if (this.isTickLabelAvailable()) {
			tlVars = this.getVariablesInXYOrder(this.mTickLabelVariables, this.mTickLabelHolderVariables);
		}

		SGMDArrayVariable[] vars = this.getVariables();
		String[] xNames = null;
		if (this.mXVariables == null || this.mXVariables.length == 0) {
			if (exportFlag) {
				xNames = new String[] { this.getUniqueVarName(DEFAULT_VAR_NAME_BASE_X, vars) };
			} else {
				xValid = false;
			}
		} else {
			xNames = this.getNames(this.mXVariables);
		}
		String[] yNames = null;
		if (this.mYVariables == null || this.mYVariables.length == 0) {
			if (exportFlag) {
				yNames = new String[] { this.getUniqueVarName(DEFAULT_VAR_NAME_BASE_Y, vars) };
			} else {
				yValid = false;
			}
		} else {
			yNames = this.getNames(this.mYVariables);
		}
		String[] leNames = null;
		String[] ueNames = null;
		if (this.isErrorBarAvailable()) {
			leNames = this.getNames(leVars);
			ueNames = this.getNames(ueVars);
		}
		String[] tlNames = null;
		if (this.isTickLabelAvailable()) {
			tlNames = this.getNames(tlVars);
		}

		double[][] xValues = null;
		double[][] yValues = null;
		double[][] leValues = null;
		double[][] ueValues = null;
		String[][] tickLabels = null;
		SGIDataSource src = this.getDataSource();
		if ((src instanceof SGVirtualMDArrayFile && archiveFlag) || !archiveFlag) {
			// export data to a file or save virtual MDArray data to an archive data set file
			SGSXYMultipleDataBuffer buffer = (SGSXYMultipleDataBuffer) this.getDataBuffer(policy);
			xValues = buffer.getXValues();
			yValues = buffer.getYValues();
			leValues = buffer.getLowerErrorValues();
			ueValues = buffer.getUpperErrorValues();
			tickLabels = buffer.getTickLabels();
		}

		SXYExportInfo info = new SXYExportInfo();
		info.xNames = xNames;
		info.yNames = yNames;
		info.leNames = leNames;
		info.ueNames = ueNames;
		info.tlNames = tlNames;
		info.xValues = xValues;
		info.yValues = yValues;
		info.leValues = leValues;
		info.ueValues = ueValues;
		info.tlValues = tickLabels;
		MDArrayDataType[] xDataTypes = null;
		if (xValid) {
			if (this.mXVariables == null || this.mXVariables.length == 0) {
				xDataTypes = new MDArrayDataType[1];
				final VALUE_TYPE valueType = shift ? VALUE_TYPE.FLOAT : VALUE_TYPE.INTEGER;
				xDataTypes[0] = new MDArrayDataType(valueType);
			} else {
				xDataTypes = new MDArrayDataType[this.mXVariables.length];
				for (int ii = 0; ii < this.mXVariables.length; ii++) {
					xDataTypes[ii] = this.getExportNumberDataType(this.mXVariables[ii], mode, sxyPolicy);
				}
			}
		}
		MDArrayDataType[] yDataTypes = null;
		if (yValid) {
			if (this.mYVariables == null || this.mYVariables.length == 0) {
				yDataTypes = new MDArrayDataType[1];
				final VALUE_TYPE valueType = shift ? VALUE_TYPE.FLOAT : VALUE_TYPE.INTEGER;
				xDataTypes[0] = new MDArrayDataType(valueType);
			} else {
				yDataTypes = new MDArrayDataType[this.mYVariables.length];
				for (int ii = 0; ii < this.mYVariables.length; ii++) {
					yDataTypes[ii] = this.getExportNumberDataType(this.mYVariables[ii], mode, sxyPolicy);
				}
			}
		}
		MDArrayDataType[] leDataTypes = null;
		MDArrayDataType[] ueDataTypes = null;
		if (this.isErrorBarAvailable()) {
			leDataTypes = this.getDataTypes(leVars);
			ueDataTypes = this.getDataTypes(ueVars);
		}
		MDArrayDataType[] tlDataTypes = null;
		if (this.isTickLabelAvailable()) {
			tlDataTypes = this.getDataTypes(tlVars);
		}
		
		info.xDataTypes = xDataTypes;
		info.yDataTypes = yDataTypes;
		info.leDataTypes = leDataTypes;
		info.ueDataTypes = ueDataTypes;
		info.tlDataTypes = tlDataTypes;
		
		info.xValid = xValid;
		info.yValid = yValid;
		
		return info;
	}
	
	private SGMDArrayVariable[] getVariablesInXYOrder(SGMDArrayVariable[] vars, 
			SGMDArrayVariable[] holderVars) {
		SGMDArrayVariable[] xyVars = null;
		if (this.mXVariables != null) {
			if (SGUtility.containsAll(this.mXVariables, holderVars)) {
				xyVars = this.mXVariables;
			}
		}
		if (this.mYVariables != null) {
			if (SGUtility.containsAll(this.mYVariables, holderVars)) {
				xyVars = this.mYVariables;
			}
		}
		if (xyVars == null) {
			throw new Error("This should not happen.");
		}
		SGMDArrayVariable[] ret = new SGMDArrayVariable[xyVars.length];
		for (int ii = 0; ii < xyVars.length; ii++) {
			SGMDArrayVariable xyVar = xyVars[ii];
			int varIndex = -1;
			for (int jj = 0; jj < holderVars.length; jj++) {
				if (holderVars[jj].equals(xyVar)) {
					varIndex = jj;
					break;
				}
			}
			if (varIndex != -1) {
				ret[ii] = vars[varIndex];
			}
		}
		return ret;
	}
	
	private String[] getNames(SGMDArrayVariable[] vars) {
		String[] names = new String[vars.length];
		for (int ii = 0; ii < names.length; ii++) {
			names[ii] = (vars[ii] != null) ? vars[ii].getName() : null;
		}
		return names;
	}

	private MDArrayDataType[] getDataTypes(SGMDArrayVariable[] vars) {
		MDArrayDataType[] dataTypes = new MDArrayDataType[vars.length];
		for (int ii = 0; ii < dataTypes.length; ii++) {
			dataTypes[ii] = (vars[ii] != null) ? vars[ii].getDataType() : null;
		}
		return dataTypes;
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
		SXYExportInfo info = this.exportCommon(mode, policy);
		final double[][] xValues = info.xValues;
		final double[][] yValues = info.yValues;
		if (this.isDimensionPicked()) {
			if (info.xValid) {
				this.write(writer, info.xNames[0], info.xDataTypes[0], xValues);
			}
			if (info.yValid) {
				this.write(writer, info.yNames[0], info.yDataTypes[0], yValues);
			}
			if (this.isErrorBarAvailable()) {
				this.write(writer, info.leNames[0], info.leDataTypes[0], info.leValues);
				if (this.hasSameErrorValues(info, 0)) {
					this.write(writer, info.ueNames[0], info.ueDataTypes[0], info.ueValues);
				}			
			}
			if (this.isTickLabelAvailable()) {
				this.write(writer, info.tlNames[0], info.tlValues);
			}
		} else {
			if (info.xValid) {
				for (int ii = 0; ii < info.xNames.length; ii++) {
					this.write(writer, info.xNames[ii], info.xDataTypes[ii], xValues[ii]);
				}
			}
			if (info.yValid) {
				for (int ii = 0; ii < info.yNames.length; ii++) {
					this.write(writer, info.yNames[ii], info.yDataTypes[ii], yValues[ii]);
				}
			}
			if (this.isErrorBarAvailable()) {
				for (int ii = 0; ii < info.leNames.length; ii++) {
					if (info.leNames[ii] != null && info.leValues[ii] != null) {
						this.write(writer, info.leNames[ii], info.leDataTypes[ii], info.leValues[ii]);
					}
				}
				for (int ii = 0; ii < info.ueNames.length; ii++) {
					if (this.hasSameErrorValues(info, ii)) {
						if (info.ueNames[ii] != null && info.ueValues[ii] != null) {
							this.write(writer, info.ueNames[ii], info.ueDataTypes[ii], info.ueValues[ii]);
						}
					}
				}
			}
			if (this.isTickLabelAvailable()) {
				for (int ii = 0; ii < info.tlNames.length; ii++) {
					if (info.tlNames[ii] != null && info.tlValues[ii] != null) {
						writer.writeStringArray(info.tlNames[ii], info.tlValues[ii]);
					}
				}
			}
		}
		return true;
	}
	
	private boolean hasSameErrorValues(SXYExportInfo info, final int index) {
		return (info.leNames[index] != null) && (info.ueNames[index] != null) 
				&& !info.ueNames[index].equals(info.leNames[index]);
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
		SXYExportInfo info = this.exportCommon(mode, policy);
		List<MLArray> mlList = new ArrayList<MLArray>();
		final double[][] xValues = info.xValues;
		final double[][] yValues = info.yValues;
		if (info.xValid) {
			String[] xNames = info.xNames;
			for (int ii = 0; ii < xNames.length; ii++) {
				MLDouble xArray = new MLDouble(xNames[ii], xValues[ii], 1);
				mlList.add(xArray);
			}
		}
		if (info.yValid) {
			String[] yNames = info.yNames;
			for (int ii = 0; ii < yNames.length; ii++) {
				MLDouble yArray = new MLDouble(yNames[ii], yValues[ii], 1);
				mlList.add(yArray);
			}
		}
		try {
			new MatFileWriter(file.getPath(), mlList);
		} catch (IOException e) {
			return false;
		}
		return true;
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
		return SGDataUtility.getDataBuffer(this, (SGSXYDataBufferPolicy) param);
	}

	@Override
    public Boolean getDateFlag() {
		// always returns null
    	return null;
    }

	@Override
    public boolean hasGenericTickLabels() {
		// always returns true
		return true;
	}

	@Override
    public SGDate[] getDateArray(SGSXYDataBufferPolicy policy) {
    	return SGDataUtility.getDateArray(this, policy);
	}

	@Override
	public Boolean[] hasSameErrorVariable() {
		if (!this.isErrorBarAvailable()) {
			return null;
		}
		SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
		Boolean[] ret = new Boolean[sxyArray.length];
		for (int ii = 0; ii < ret.length; ii++) {
			if (sxyArray[ii].isErrorBarAvailable()) {
				ret[ii] = sxyArray[ii].hasSameErrorVariable();
			}
		}
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);
		return ret;
	}

	/**
	 * Returns true if this data has at lease one "effective" stride that has the string representation 
	 * different from "0:end".
	 * 
	 * @return true this data has an effective stride
	 */
	@Override
	public boolean hasEffectiveStride() {
		return SGDataUtility.hasEffectiveStride(this);
	}

	@Override
    protected MDArrayDataType getExportNumberDataType(SGMDArrayVariable var, SGExportParameter mode,
    		SGDataBufferPolicy policy) {
    	if (SGDataUtility.isArchiveDataSetOperation(mode.getType())) {
    		return var.getDataType();
    	} else {
    		SGSXYDataBufferPolicy sxyPolicy = (SGSXYDataBufferPolicy) policy;
    		if (sxyPolicy.isShiftValuesContained()) {
    			return var.getExportFloatingNumberDataType();
    		} else {
    			return var.getDataType();
    		}
    	}
    }

	@Override
	public double[][] getXValueArray(SGSXYDataBufferPolicy policy) {
		return SGDataUtility.getXValues(this, policy);
	}

	@Override
	public double[][] getYValueArray(SGSXYDataBufferPolicy policy) {
		return SGDataUtility.getYValues(this, policy);
	}
	
    /**
     * Returns unshifted x-value arrays with given policy.
     *
     * @param policy
     *           policy to get values
     * @return arrays of unshifted x-values
     */
	@Override
    public double[][] getUnshiftedXValueArray(SGSXYDataBufferPolicy policy) {
		return SGDataUtility.getUnshiftedXValues(this, policy);
	}

    /**
     * Returns unshifted y-value arrays with given policy.
     *
     * @param policy
     *           policy to get values
     * @return arrays of unshifted y-values
     */
	@Override
    public double[][] getUnshiftedYValueArray(SGSXYDataBufferPolicy policy) {
		return SGDataUtility.getUnshiftedYValues(this, policy);
	}

    /**
     * Returns the main stride.
     * 
     * @return the main stride
     */
	@Override
    public SGIntegerSeriesSet getMainStride() {
		SGIntegerSeriesSet ret = null;
		if (this.isStrideAvailable()) {
			ret = this.mStride;
		}
		return ret;
	}

    /**
     * Returns arrays of lower error values with given policy.
     *
     * @param policy
     *           policy to get values
     * @return arrays of lower error values
     */
	@Override
    public double[][] getLowerErrorValueArray(SGSXYDataBufferPolicy policy) {
		return SGDataUtility.getLowerErrorValueArray(this, policy);
	}

    /**
     * Returns arrays of upper error values with given policy.
     *
     * @param policy
     *           policy to get values
     * @return arrays of upper error values
     */
	@Override
    public double[][] getUpperErrorValueArray(SGSXYDataBufferPolicy policy) {
		return SGDataUtility.getUpperErrorValueArray(this, policy);
    }

    /**
     * Returns arrays of tick labels with given policy.
     *
     * @param policy
     *           policy to get values
     * @return arrays of tick labels
     */
	@Override
    public String[][] getTickLabelArray(SGSXYDataBufferPolicy policy) {
    	return SGDataUtility.getTickLabelArray(this, policy);
    }

	@Override
	public SGDate[] getDateArray(boolean all) {
		// always returns null
		return null;
	}

	@Override
	public Boolean isYValuesHolder() {
		if (this.isErrorBarAvailable()) {
			return SGUtility.contains(this.mYVariables, this.mErrorBarHolderVariables);
		} else if (this.isTickLabelAvailable()) {
			return SGUtility.contains(this.mYVariables, this.mTickLabelHolderVariables);
		} else {
			return null;
		}
	}

	@Override
    public boolean hasOneSidedMultipleValues() {
		if (this.isDimensionPicked()) {
			// returns true if only one variable is picked up
			return (this.mPickUpDimensionInfo.getDimensionMap().size() == 1);
		} else {
			// returns true when dimension is not picked
			return true;
		}
	}

	@Override
    protected void getVarNameColumnTypeList(List<String> varList, 
	    		List<String> columnTypeList) {
		
    	super.getVarNameColumnTypeList(varList, columnTypeList);

		if (this.mXVariables != null) {
			for (int ii = 0; ii < this.mXVariables.length; ii++) {
				String strX = this.getOneDimensionalVarCommandString(this.mXVariables[ii]);
				varList.add(strX);
				columnTypeList.add(X_VALUE);
			}
		}
		if (this.mYVariables != null) {
			for (int ii = 0; ii < this.mYVariables.length; ii++) {
				String strY = this.getOneDimensionalVarCommandString(this.mYVariables[ii]);
				varList.add(strY);
				columnTypeList.add(Y_VALUE);
			}
		}
		if (this.isErrorBarAvailable()) {
			final int num = this.mLowerErrorVariables.length;
			for (int ii = 0; ii < num; ii++) {
				if (this.mLowerErrorVariables[ii] == null) {
					continue;
				}
				SGMDArrayVariable leVar = this.mLowerErrorVariables[ii];
				SGMDArrayVariable ueVar = this.mUpperErrorVariables[ii];
				String leStr = this.getOneDimensionalVarCommandString(leVar);
				String ueStr = this.getOneDimensionalVarCommandString(ueVar);
				String ehName = this.mErrorBarHolderVariables[ii].getName();
				boolean equalFlag = leVar.equals(ueVar);
				if (equalFlag) {
					varList.add(leStr);
					String columnTypeStr = SGDataUtility.appendColumnTitle(LOWER_UPPER_ERROR_VALUE, ehName);
					columnTypeList.add(columnTypeStr);
				} else {
					varList.add(leStr);
					String leColumnTypeStr = SGDataUtility.appendColumnTitle(LOWER_ERROR_VALUE, ehName);
					columnTypeList.add(leColumnTypeStr);
					
					varList.add(ueStr);
					String ueColumnTypeStr = SGDataUtility.appendColumnTitle(UPPER_ERROR_VALUE, ehName);
					columnTypeList.add(ueColumnTypeStr);
				}
			}
		}
		if (this.isTickLabelAvailable()) {
			for (int ii = 0; ii < this.mTickLabelVariables.length; ii++) {
				SGMDArrayVariable tlVar = this.mTickLabelVariables[ii];
				String tlStr = this.getOneDimensionalVarCommandString(tlVar);
				String thName = this.mTickLabelHolderVariables[ii].getName();
				varList.add(tlStr);
				String columnTypeStr = SGDataUtility.appendColumnTitle(TICK_LABEL, thName);
				columnTypeList.add(columnTypeStr);
			}
		}
	}

    /**
     * Returns a text string for the command of pick up dimension.
     * 
     * @return a text string for the command of pick up dimension
     */
    public String getPickUpDimensionCommandString() {
    	if (!this.isDimensionPicked()) {
    		return null;
    	}
    	return this.getDimensionCommandString(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
    }

	@Override
	public boolean setArraySectionPropertySub(SGPropertyMap map) {
        if (this.isStrideAvailable()) {
            SGIntegerSeriesSet arraySection = this.getStride();
            if (!arraySection.isComplete()) {
            	SGPropertyUtility.addQuotedStringProperty(map, COM_DATA_ARRAY_SECTION, 
            			arraySection.toString());
            }
            if (this.isTickLabelAvailable()) {
                SGIntegerSeriesSet tickLabelArraySection = this.getTickLabelStride();
                if (!tickLabelArraySection.isComplete()) {
                	SGPropertyUtility.addQuotedStringProperty(map, COM_DATA_TICK_LABEL_ARRAY_SECTION, 
                			tickLabelArraySection.toString());
                }
            }
        }
		return true;
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
    	SGMDArrayFile mdFile = (SGMDArrayFile) src;
    	this.mXVariables = this.findVariables(mdFile, this.mXVariables);
		this.mYVariables = this.findVariables(mdFile, this.mYVariables);
		if (this.isErrorBarAvailable()) {
			this.mLowerErrorVariables = this.findVariables(
					mdFile, this.mLowerErrorVariables);
			this.mUpperErrorVariables = this.findVariables(
					mdFile, this.mUpperErrorVariables);
			this.mErrorBarHolderVariables = this.findVariables(
					mdFile, this.mErrorBarHolderVariables);
		}
		if (this.isTickLabelAvailable()) {
			this.mTickLabelVariables = this.findVariables(
					mdFile, this.mTickLabelVariables);
			this.mTickLabelHolderVariables = this.findVariables(
					mdFile, this.mTickLabelHolderVariables);
		}
    }

	@Override
	public String getDateFormat() {
		// always returns null
		return null;
	}

	@Override
	public void setDateFormat(String format) {
		// do nothing
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
    	if (this.mXVariables != null && this.mXVariables.length != 0) {
    		list.add(SGIDataColumnTypeConstants.X_VALUE);
    	}
    	if (this.mYVariables != null && this.mYVariables.length != 0) {
    		list.add(SGIDataColumnTypeConstants.Y_VALUE);
    	}
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

    /**
     * Returns whether the index is available.
     *
     * @return true if the index is available
     */
    @Override
	public boolean isIndexAvailable() {
		// always returns true
		return true;
	}

	@Override
	public Double getDataViewerValue(String columnType, int row, int col) {
		return SGDataUtility.getDataViewerValue(this, columnType, row, col);
	}

	@Override
    public double getXValueAt(final int childIndex, final int arrayIndex) {
		SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
		double[] values = sxyArray[childIndex].getXValueArray(false);
        SGDataUtility.disposeSXYDataArray(sxyArray);
        return values[arrayIndex];
	}

	@Override
    public double getYValueAt(final int childIndex, final int arrayIndex) {
		SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
		double[] values = sxyArray[childIndex].getYValueArray(false);
        SGDataUtility.disposeSXYDataArray(sxyArray);
        return values[arrayIndex];
	}

	@Override
	public int getDataViewerColumnNumber(final String columnType, final boolean all) {
		final int num;
		if (Y_VALUE.equals(columnType)) {
			if (this.hasMultipleYValues()) {
				num = this.getChildNumber();
			} else {
				SGMDArrayVariable yVar = this.getYVariable();
				Integer pickUpDimension = yVar.getDimensionIndex(
						SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
				if (pickUpDimension != null && pickUpDimension != -1) {
					num = yVar.getDimensions()[pickUpDimension];
				} else {
					num = 1;
				}
			}
		} else {
			if (!this.hasMultipleYValues()) {
				num = this.getChildNumber();
			} else {
				SGMDArrayVariable xVar = this.getXVariable();
				Integer pickUpDimension = xVar.getDimensionIndex(
						SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
				if (pickUpDimension != null && pickUpDimension != -1) {
					num = xVar.getDimensions()[pickUpDimension];
				} else {
					num = 1;
				}
			}
		}
		return num;
	}

	@Override
	public int getDataViewerRowNumber(final String columnType, final boolean all) {
		if (all) {
			return this.getAllPointsNumber();
		} else {
			return this.getPointsNumber();
		}
	}

	@Override
	public SGIntegerSeriesSet getDataViewerColStride(String columnType) {
		return new SGIntegerSeriesSet(0, this.getChildNumber() - 1, 1);
	}

	@Override
	public SGIntegerSeriesSet getDataViewerRowStride(String columnType) {
		return this.mStride;
	}
	
	@Override
	public void setDataViewerValue(final String columnType, final int row, final int col,
			final Object value) {
		List<SGDataValueHistory> editedDataValueList = SGDataUtility.getEditedDataValueList(
				this, columnType, row, col, value, this.mStride);
		this.mEditedDataValueList.addAll(editedDataValueList);
	}

    @Override
    public SGIntegerSeriesSet getIndexStride() {
    	return this.getStride();
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
				value.getRowIndex(), value.getColumnIndex(), value.getValue(), 
				this.mStride);
	}

	@Override
	public void restoreCache() {
    	SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
    	for (int ii = 0; ii < sxyArray.length; ii++) {
    		sxyArray[ii].restoreCache();
    	}
    	
        // updates the cache
    	SGDataUtility.updateCache(this, sxyArray);
    	
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);
	}

	@Override
    public boolean hasDateTypeXVariable() {
		// always returns false
		return false;
	}
    
	@Override
    public boolean hasDateTypeYVariable() {
		// always returns false
		return false;
	}

	@Override
	protected boolean matches(final int col, final int row, String columnType,
			SGDataValueHistory value, final double d) {
		return SGDataUtility.matches(col, row, columnType, value, d);
	}

	private Map<Integer, Double> getEditedValueMap(SGMDArrayVariable var,
			final boolean dimReversed) {
		
		String varName = var.getName();
		int[] dims = var.getDimensions();

		Map<Integer, Double> valueMap = new HashMap<Integer, Double>();

		for (int ii = 0; ii < this.mEditedDataValueList.size(); ii++) {
			SGDataValueHistory.MDArray.MD1 dataValue = (SGDataValueHistory.MDArray.MD1) this.mEditedDataValueList
					.get(ii);
			if (!varName.equals(dataValue.getVarName())) {
				continue;
			}

			final int dimension = dataValue.getDimension();
			final int dimIndex = dataValue.getIndex();

			final int pickUpDimension = dataValue.getPickUpDimension();
			final int pickUpDimIndex = dataValue.getPickUpDimIndex();

			int animationDimension = dataValue.getAnimationDimension();
			int animationDimIndex = dataValue.getAnimationDimIndex();

			int[] origins = var.getOrigins();
			if (pickUpDimension != -1 && pickUpDimIndex != -1) {
				origins[pickUpDimension] = pickUpDimIndex;
			}
			if (animationDimension != -1 && animationDimIndex != -1) {
				origins[animationDimension] = animationDimIndex;
			}

			final int[] factors = this.getFactors(dims, dimReversed);
			final int factor = factors[dimension];
			int offset = 0;
			for (int jj = 0; jj < dims.length; jj++) {
				if (jj == dimension) {
					continue;
				}
				offset += factors[jj] * origins[jj];
			}
			final int arrayIndex = factor * dimIndex + offset;
			final double value = dataValue.getValue();

			valueMap.put(arrayIndex, value);
		}

		return valueMap;
    }

	@Override
    protected MDDoubleArray setEditedValues(IHDF5Writer writer,
    		SGMDArrayVariable var, MDDoubleArray array) {
		
		// get edited values
		Map<Integer, Double> valueMap = this.getEditedValueMap(var, true);
		
		// set to the array
		Iterator<Entry<Integer, Double>> itr = valueMap.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Integer, Double> entry = itr.next();
			final int arrayIndex = entry.getKey();
			final double value = entry.getValue();
			array.set(value, arrayIndex);
		}
		
    	return array;
    }

	@Override
    protected MLDouble setEditedValues(SGMDArrayVariable var, MLDouble array) {

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
		Map<Integer, Double> valueMap = this.getEditedValueMap(var, false);
		
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

	@Override
    public double[][] getXValueArray(final boolean all) {
		return SGDataUtility.getXValueArray(this, all);
    }

	@Override
	public double[][] getXValueArray(boolean all, boolean useCache,
			boolean removeInvalidValues) {
		return SGDataUtility.getXValueArray(this, all, useCache, removeInvalidValues);
	}

	@Override
	public double[][] getXValueArray(boolean all, boolean useCache) {
		return SGDataUtility.getXValueArray(this, all, useCache);
	}

	@Override
    public double[][] getYValueArray(final boolean all) {
		return SGDataUtility.getYValueArray(this, all);
    }

	@Override
	public double[][] getYValueArray(boolean all, boolean useCache,
			boolean removeInvalidValues) {
		return SGDataUtility.getYValueArray(this, all, useCache, removeInvalidValues);
	}

	@Override
	public double[][] getYValueArray(boolean all, boolean useCache) {
		return SGDataUtility.getYValueArray(this, all, useCache);
	}

	@Override
	public void addSingleDimensionEditedDataValue(SGDataValueHistory dataValue) {
		SGDataValueHistory.MDArray.D1 dValue = (SGDataValueHistory.MDArray.D1) dataValue;
		SGDataValueHistory prev = dValue.getPreviousValue();
		SGDataValueHistory.MDArray.MD1 mdValue;
		if (prev != null) {
			mdValue = new SGDataValueHistory.MDArray.MD1(
					dValue.getValue(), dValue.getColumnType(), 0,
					dValue.getIndex(), dValue.getVarName(), prev.getValue());
		} else {
			mdValue = new SGDataValueHistory.MDArray.MD1(
					dValue.getValue(), dValue.getColumnType(), 0,
					dValue.getIndex(), dValue.getVarName());
		}
		this.mEditedDataValueList.add(mdValue);
	}

	@Override
    public SGTwoDimensionalArrayIndex getDataViewerCell(SGTwoDimensionalArrayIndex cell,
    		String columnType, final boolean bStride) {
		SGTwoDimensionalArrayIndex ret = null;
//		if (this.isIndexAvailable()) {
//			ret = super.getDataViewerCell(cell, columnType, bStride);
//		} else {
//			if (this.hasMultipleYValues()) {
//				if (Y_VALUE.equals(columnType)) {
//					ret = super.getDataViewerCell(cell, columnType, bStride);
//				} else {
//					ret = super.getDataViewerCell(new SGTwoDimensionalArrayIndex(0, cell.getRow()), 
//							columnType, bStride);
//				}
//			} else {
//				if (X_VALUE.equals(columnType)) {
//					ret = super.getDataViewerCell(cell, columnType, bStride);
//				} else {
//					ret = super.getDataViewerCell(new SGTwoDimensionalArrayIndex(0, cell.getRow()), 
//							columnType, bStride);
//				}
//			}
//		}
		if (this.hasMultipleYValues()) {
			if (Y_VALUE.equals(columnType)) {
				ret = super.getDataViewerCell(cell, columnType, bStride);
			} else {
				SGMDArrayVariable xVar = this.getXVariable();
				Integer pickUpDimension = xVar.getDimensionIndex(
						SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
				if (pickUpDimension != null && pickUpDimension != -1) {
					ret = super.getDataViewerCell(cell, columnType, bStride);
				} else {
					ret = super.getDataViewerCell(new SGTwoDimensionalArrayIndex(0, cell.getRow()), 
							columnType, bStride);
				}
			}
		} else {
			if (X_VALUE.equals(columnType)) {
				ret = super.getDataViewerCell(cell, columnType, bStride);
			} else {
				SGMDArrayVariable yVar = this.getYVariable();
				Integer pickUpDimension = yVar.getDimensionIndex(
						SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
				if (pickUpDimension != null && pickUpDimension != -1) {
					ret = super.getDataViewerCell(cell, columnType, bStride);
				} else {
					ret = super.getDataViewerCell(new SGTwoDimensionalArrayIndex(0, cell.getRow()), 
							columnType, bStride);
				}
			}
		}
    	return ret;
    }

}
