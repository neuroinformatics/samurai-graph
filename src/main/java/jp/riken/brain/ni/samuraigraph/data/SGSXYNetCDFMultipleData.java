package jp.riken.brain.ni.samuraigraph.data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIStringModifier;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGNamedStringBlock;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;

import org.w3c.dom.Element;

import ucar.ma2.Array;
import ucar.ma2.ArrayByte;
import ucar.ma2.ArrayChar;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

/**
 * The class of multiple scalar XY type data with netCDF data.
 *
 */
public class SGSXYNetCDFMultipleData extends SGNetCDFData implements
        SGISXYTypeMultipleData, SGIDataColumnTypeConstants, SGIDataPropertyKeyConstants,
        SGISXYMultipleDimensionData, SGINetCDFConstants {

    /**
     * The variables for x-values.
     */
    protected SGNetCDFVariable[] mXVariables = null;

    /**
     * The variables for y-values.
     */
    protected SGNetCDFVariable[] mYVariables = null;

    /**
     * The variables for lower error values.
     */
    protected SGNetCDFVariable[] mLowerErrorVariables = null;

    /**
     * The variables for upper error values.
     */
    protected SGNetCDFVariable[] mUpperErrorVariables = null;

    /**
     * The variables for values that holds error values.
     */
    protected SGNetCDFVariable[] mErrorBarHolderVariables = null;

    /**
     * The variables for tick labels.
     */
    protected SGNetCDFVariable[] mTickLabelVariables = null;

    /**
     * The variables for values that holds tick labels.
     */
    protected SGNetCDFVariable[] mTickLabelHolderVariables = null;

    /**
     * Whether single variable is date.
     */
    protected boolean mIsSingleVariableDateFlag = false;

    /**
     * The decimal places for the tick labels.
     */
    protected int mDecimalPlaces = 0;

    /**
     * The exponent for the tick labels.
     */
    protected int mExponent = 0;

    protected String mDateFormat = "";

    /**
     * The stride of array.
     */
    protected SGIntegerSeriesSet mStride = null;

	/**
	 * The stride for the tick labels.
	 */
	protected SGIntegerSeriesSet mTickLabelStride = null;

    /**
     * Indices of the dimension.
     */
    protected int[] mDimensionIndices = null;

    /**
     * The information for picked up dimension.
     */
    protected SGNetCDFPickUpDimensionInfo mPickUpDimensionInfo = null;

	/**
	 * The shift value.
	 */
	private SGTuple2d mShift = new SGTuple2d();

    /**
     * The default constructor.
     *
     */
    public SGSXYNetCDFMultipleData() {
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
     *            information of x values
     * @param yInfo
     *            information of y values
     * @param timeInfo
     *            information of time values
     * @param indexInfo
     *            information of index
     * @param stride
     *            stride of an array
     * @param tickLabelStride
     *            stride for tick label
     * @param strideAvailable
     *            flag whether to set available the stride
     */
    public SGSXYNetCDFMultipleData(final SGNetCDFFile ncfile,
            final SGDataSourceObserver obs, final SGNetCDFDataColumnInfo[] xInfo,
            final SGNetCDFDataColumnInfo[] yInfo, final SGNetCDFDataColumnInfo timeInfo,
            final SGNetCDFDataColumnInfo indexInfo,
            final SGIntegerSeriesSet stride, final SGIntegerSeriesSet tickLabelStride,
            final boolean strideAvailable) {

        super(ncfile, obs, timeInfo, indexInfo, stride, strideAvailable);

        if (xInfo == null || yInfo == null) {
            throw new IllegalArgumentException("xInfo == null || yInfo == null");
        }
        final int xNum = xInfo.length;
        if (xNum == 0) {
            throw new IllegalArgumentException("The number of variables for x-values is equal to zero.");
        }
        final int yNum = yInfo.length;
        if (yNum == 0) {
            throw new IllegalArgumentException("The number of variables for y-values is equal to zero.");
        }
        if (!this.isValidXYNames(ncfile, xInfo, yInfo)) {
            throw new IllegalArgumentException("Either one of the two arrays must have only one element.");
        }

        // get variables
        SGNetCDFVariable[] xVars = new SGNetCDFVariable[xNum];
        for (int ii = 0; ii < xNum; ii++) {
            xVars[ii] = ncfile.findVariable(xInfo[ii].getName());
            if (xVars[ii] == null) {
                throw new IllegalArgumentException("Illegal variable name: " + xInfo[ii]);
            }
        }
        SGNetCDFVariable[] yVars = new SGNetCDFVariable[yNum];
        for (int ii = 0; ii < yNum; ii++) {
            yVars[ii] = ncfile.findVariable(yInfo[ii].getName());
            if (yVars[ii] == null) {
                throw new IllegalArgumentException("Illegal variable name: " + yInfo[ii]);
            }
        }

        //
        // check variables
        //

    	Dimension cDim = null;
        if (this.mIndexVariable != null) {

        	// index variable
            List<Dimension> cDimList = new ArrayList<Dimension>();
        	cDim = this.mIndexVariable.getDimension(0);
            cDimList.add(cDim);

        	// x and y variables
            for (int ii = 0; ii < xVars.length; ii++) {
            	this.checkNonCoordinateVariable(xVars[ii], cDimList);
            }
            for (int ii = 0; ii < yVars.length; ii++) {
            	this.checkNonCoordinateVariable(yVars[ii], cDimList);
            }

        } else {

        	// x and y variables
        	SGNetCDFVariable varSingle = null;
        	SGNetCDFVariable[] varMultiple = null;
        	if (xVars.length == 1 && yVars.length == 1) {
        		SGNetCDFVariable xVar = xVars[0];
        		SGNetCDFVariable yVar = yVars[0];
        		if (xVar.isCoordinateVariable()) {
        			varSingle = xVar;
        			varMultiple = yVars;
        		} else {
        			varSingle = yVar;
        			varMultiple = xVars;
        		}
        	} else if (xVars.length == 1) {
        		varSingle = xVars[0];
        		varMultiple = yVars;
        	} else if (yVars.length == 1) {
        		varSingle = yVars[0];
        		varMultiple = xVars;
        	} else {
                throw new IllegalArgumentException("Either of x and y variables must be size one array.");
        	}

        	// x and y variables
        	if (!varSingle.isCoordinateVariable()) {
                throw new IllegalArgumentException("The single variable must be a coordinate variable.");
        	}
        	cDim = varSingle.getDimension(0);

            List<Dimension> cDimList = new ArrayList<Dimension>();
            cDimList.add(cDim);

            // multiple variables
            for (int ii = 0; ii < varMultiple.length; ii++) {
            	this.checkNonCoordinateVariable(varMultiple[ii], cDimList);
            }
        }

        // set to attributes
        this.mXVariables = xVars;
        this.mYVariables = yVars;
        if (!this.isIndexAvailable()) {
            this.mStride = this.createStride(stride, cDim);
        }
        this.setupTickLabelStride(tickLabelStride);

        this.updateIsSingleVariableDateFlag();
    }

    private void setupTickLabelStride(SGIntegerSeriesSet tickLabelStride) {
        SGIntegerSeriesSet tlStride = null;
        if (tickLabelStride != null) {
            tlStride = (SGIntegerSeriesSet) tickLabelStride.clone();
        } else {
        	if (!this.isIndexAvailable()) {
        		tlStride = this.getStrideInstance();
        	} else {
        		tlStride = this.getIndexStrideInstance();
        	}
        }
        this.mTickLabelStride = tlStride;
    }

    protected void updateIsSingleVariableDateFlag() {
        if (this.mXVariables.length==1 && this.mXVariables[0] instanceof SGDateVariable) {
            this.mIsSingleVariableDateFlag = true;
        } else if (this.mYVariables.length==1 && this.mYVariables[0] instanceof SGDateVariable) {
            this.mIsSingleVariableDateFlag = true;
        } else {
            this.mIsSingleVariableDateFlag = false;
        }
    }

    /**
     * Builds a data object with given netCDF data.
     *
     * @param ncfile
     *            netCDF data file
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
     * @param ehInfo
     *            information of the error bar holder
     * @param tlInfo
     *            information of tick labels
     * @param thInfo
     *            information of tick label holder
     * @param timeInfo
     *            information of time values
     * @param indexInfo
     *            information of index
     * @param stride
     *            stride of an array
     * @param tickLabelStride
     *            stride for tick label
     * @param strideAvailable
     *            flag whether to set available the stride
     */
    public SGSXYNetCDFMultipleData(SGNetCDFFile ncfile,
    		SGDataSourceObserver obs,
            final SGNetCDFDataColumnInfo[] xInfo, final SGNetCDFDataColumnInfo[] yInfo,
            final SGNetCDFDataColumnInfo[] leInfo, final SGNetCDFDataColumnInfo[] ueInfo,
            final SGNetCDFDataColumnInfo[] ehInfo, final SGNetCDFDataColumnInfo[] tlInfo,
            final SGNetCDFDataColumnInfo[] thInfo, final SGNetCDFDataColumnInfo timeInfo,
            final SGNetCDFDataColumnInfo indexInfo, final SGIntegerSeriesSet stride,
            final SGIntegerSeriesSet tickLabelStride, final boolean strideAvailable) {

        this(ncfile, obs, xInfo, yInfo, timeInfo, indexInfo, stride, tickLabelStride, strideAvailable);

    	// check the input for error bars
        SGNetCDFDataColumnInfo[] multipleNames = (xInfo.length == 1) ? yInfo : xInfo;
    	if (!this.isValidErrorBarNames(ncfile, multipleNames, leInfo, ueInfo, ehInfo)) {
            throw new IllegalArgumentException("Column indices for the error bars are invalid.");
    	}
    	if (!this.isValidTickLabelNames(ncfile, multipleNames, tlInfo, thInfo)) {
            throw new IllegalArgumentException("Column indices for the tick labels are invalid.");
    	}

        //
        // check variables
        //

        SGNetCDFVariable cVar = (this.mXVariables.length == 1) ? this.mXVariables[0] : this.mYVariables[0];
        List<Dimension> cDimList = new ArrayList<Dimension>();
        Dimension cDim = cVar.getDimension(0);
        cDimList.add(cDim);
    	if (this.mIndexVariable != null) {
    		cDimList.add(this.mIndexVariable.getDimension(0));
    	}

        // get variables with the check of consistency
    	final boolean be = (leInfo != null && leInfo.length != 0);
    	final boolean bt = (tlInfo != null && tlInfo.length != 0);
    	SGNetCDFVariable[] lVars = be ? this.getVariables(leInfo, ncfile, cDimList): null;
    	SGNetCDFVariable[] uVars = be ? this.getVariables(ueInfo, ncfile, cDimList): null;
    	SGNetCDFVariable[] ehVars = be ? this.getVariables(ehInfo, ncfile, cDimList): null;
    	SGNetCDFVariable[] tVars = bt ? this.getVariables(tlInfo, ncfile, cDimList): null;
    	SGNetCDFVariable[] thVars = bt ? this.getVariables(thInfo, ncfile, cDimList): null;

        // set to attributes
    	this.mLowerErrorVariables = lVars;
    	this.mUpperErrorVariables = uVars;
    	this.mErrorBarHolderVariables = ehVars;
    	this.mTickLabelVariables = tVars;
    	this.mTickLabelHolderVariables = thVars;
    	
    	// set tick label for date type variables
        if (tVars == null || tVars.length == 0) {
        	this.updateDateTickLabelVariables();
        }

    	if (!this.isIndexAvailable()) {
            this.mStride = this.createStride(stride, cDim);
    	}
        this.setupTickLabelStride(tickLabelStride);
    }

    private void updateDateTickLabelVariables() {
    	SGNetCDFVariable xVar = this.mXVariables[0];
    	SGNetCDFVariable yVar = this.mYVariables[0];
    	if (xVar instanceof SGDateVariable) {
    		this.mTickLabelHolderVariables = this.mYVariables;
    		this.mTickLabelVariables = new SGNetCDFVariable[this.mYVariables.length];
    		for (int ii = 0; ii < this.mTickLabelVariables.length; ii++) {
    			this.mTickLabelVariables[ii] = this.mXVariables[0];
    		}
    	} else if (yVar instanceof SGDateVariable) {
    		this.mTickLabelHolderVariables = this.mXVariables;
    		this.mTickLabelVariables = new SGNetCDFVariable[this.mXVariables.length];
    		for (int ii = 0; ii < this.mTickLabelVariables.length; ii++) {
    			this.mTickLabelVariables[ii] = this.mYVariables[0];
    		}
    	}
    }

    private boolean isValidXYNames(
    		final SGNetCDFFile ncfile,
    		final SGNetCDFDataColumnInfo[] xNames,
    		final SGNetCDFDataColumnInfo[] yNames) {
        if (xNames.length == 0 || yNames.length == 0) {
        	return false;
        }
        if (xNames.length != 1 && yNames.length != 1) {
        	return false;
        }
        if (this.isValidNames(ncfile, xNames) == false) {
        	return false;
        }
        if (this.isValidNames(ncfile, yNames) == false) {
        	return false;
        }
        return true;
    }

    private boolean isValidErrorBarNames(
    		final SGNetCDFFile ncfile,
    		final SGNetCDFDataColumnInfo[] multipleNames,
    		final SGNetCDFDataColumnInfo[] lowerErrorNames,
    		final SGNetCDFDataColumnInfo[] upperErrorNames,
    		final SGNetCDFDataColumnInfo[] errorBarHolderNames) {

        if (lowerErrorNames != null && upperErrorNames != null) {
        	if (!this.isValidNames(ncfile, lowerErrorNames)) {
        		return false;
        	}
        	if (!this.isValidNames(ncfile, upperErrorNames)) {
        		return false;
        	}
        	if (lowerErrorNames.length != upperErrorNames.length) {
        		return false;
        	}
        	if (errorBarHolderNames != null) {
            	if (!this.isValidNames(ncfile, errorBarHolderNames)) {
            		return false;
            	}
                if (errorBarHolderNames.length != lowerErrorNames.length) {
                	return false;
                }
        	}
        }
        return true;
    }

    private boolean isValidTickLabelNames(
    		final SGNetCDFFile ncfile,
    		final SGNetCDFDataColumnInfo[] multipleNames,
    		final SGNetCDFDataColumnInfo[] tickLabelNames,
    		final SGNetCDFDataColumnInfo[] tickLabelHolderNames) {

        if (tickLabelNames != null) {
        	if (!this.isValidNames(ncfile, tickLabelNames)) {
        		return false;
        	}
        	if (tickLabelHolderNames != null) {
            	if (!this.isValidNames(ncfile, tickLabelHolderNames)) {
            		return false;
            	}
                if (tickLabelHolderNames.length != tickLabelNames.length) {
                	return false;
                }
        	}
        }
        return true;
    }

    private boolean isValidNames(
    		final SGNetCDFFile ncfile,
    		final SGNetCDFDataColumnInfo[] names) {
    	if (names == null) {
    		return false;
    	}
        for (int ii = 0; ii < names.length; ii++) {
            if (names[ii] == null) {
            	return false;
            }
            if (ncfile.findVariable(names[ii].getName()) == null) {
            	return false;
            }
        }
        return true;
    }

    /**
     * Builds a data object with given netCDF data.
     *
     * @param ncfile
     *            netCDF data file
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
     * @param ehInfo
     *            information of error bar holder
     * @param tlInfo
     *            information of tick labels
     * @param thInfo
     *            information of tick label holder
     * @param pickUpDimName
     *            the name of picked up dimension
     * @param pickUpIndices
     *            indices for given dimension
     * @param timeInfo
     *            information of time values
     * @param indexInfo
     *            information of index
     * @param stride
     *            stride of an array
     * @param tickLabelStride
     *            stride for tick label
     * @param strideAvailable
     *            flag whether to set available the stride
     */
	public SGSXYNetCDFMultipleData(final SGNetCDFFile ncfile,
			final SGDataSourceObserver obs, final SGNetCDFDataColumnInfo xInfo,
			final SGNetCDFDataColumnInfo yInfo,
			final SGNetCDFDataColumnInfo leInfo,
			final SGNetCDFDataColumnInfo ueInfo,
			final SGNetCDFDataColumnInfo ehInfo,
			final SGNetCDFDataColumnInfo tlInfo,
			final SGNetCDFDataColumnInfo thInfo,
			final String pickUpDimName,
			final SGIntegerSeriesSet pickUpIndices,
			final SGNetCDFDataColumnInfo timeInfo,
			final SGNetCDFDataColumnInfo indexInfo,
			final SGIntegerSeriesSet stride,
			final SGIntegerSeriesSet tickLabelStride,
			final boolean strideAvailable) {

        super(ncfile, obs, timeInfo, indexInfo, stride, strideAvailable);

        if (xInfo == null || yInfo == null || pickUpDimName == null) {
            throw new IllegalArgumentException(
            		"xInfo == null || yInfo == null || dimName == null");
        }
        if (!(leInfo == null && ueInfo == null)
        		&& !(leInfo != null && ueInfo != null)) {
            throw new IllegalArgumentException("leInfo = " + leInfo + ", ueInfo = " + ueInfo);
        }
        if (pickUpIndices == null) {
            throw new IllegalArgumentException("indices == null");
        }

        // check the dimension
        Dimension pickUpDim = ncfile.findDimension(pickUpDimName);
        if (pickUpDim == null) {
            throw new IllegalArgumentException("Picked up dimension is not found: " + pickUpDimName);
        }

        // get variables
        SGNetCDFVariable xVar = this.getVariable(ncfile, xInfo.getName());
        SGNetCDFVariable yVar = this.getVariable(ncfile, yInfo.getName());
        SGNetCDFVariable leVar = null;
        SGNetCDFVariable ueVar = null;
        SGNetCDFVariable ehVar = null;
        if (leInfo != null && ueInfo != null) {
            leVar = this.getVariable(ncfile, leInfo.getName());
            ueVar = this.getVariable(ncfile, ueInfo.getName());
            ehVar = this.getVariable(ncfile, SGDataUtility.getHolderName(leInfo));
        }
        SGNetCDFVariable tlVar = null;
        SGNetCDFVariable thVar = null;
        if (tlInfo != null) {
            tlVar = this.getVariable(ncfile, tlInfo.getName());
            thVar = this.getVariable(ncfile, SGDataUtility.getHolderName(tlInfo));
        }

        //
        // check variables
        //

        List<Dimension> cDimList = new ArrayList<Dimension>();

        // time variable
    	if (this.mTimeVariable != null) {
    		Dimension tDim = this.mTimeVariable.getDimension(0);
            if (tDim.equals(pickUpDim)) {
                throw new IllegalArgumentException(
        			"The dimension of a coordinate variable is equal to the given dimension.");
            }
    	}

    	Dimension cDim = null;
        if (this.mIndexVariable != null) {
        	// index variable
            cDim = this.mIndexVariable.getDimension(0);
            cDimList.add(cDim);
            this.checkNonCoordinateVariable(xVar, cDimList);
            this.checkNonCoordinateVariable(yVar, cDimList);
            if (!xVar.getDimensions().contains(pickUpDim)
            		&& !yVar.getDimensions().contains(pickUpDim)) {
                throw new IllegalArgumentException(
    					"Both of x and y variables do not have picked up dimension.");
            }

        } else {
        	// x and y variables
            if ((xVar.isCoordinateVariable() && yVar.isCoordinateVariable())
            		|| (!xVar.isCoordinateVariable() && !yVar.isCoordinateVariable())) {
                throw new IllegalArgumentException(
                		"Either of x and y variables must be a coordinate variable.");
            }
            SGNetCDFVariable cVar = null;
            SGNetCDFVariable oVar = null;
            if (xVar.isCoordinateVariable()) {
            	cVar = xVar;
            	oVar = yVar;
            } else {
            	cVar = yVar;
            	oVar = xVar;
            }
            cDim = cVar.getDimension(0);
            if (cDim.equals(pickUpDim)) {
                throw new IllegalArgumentException(
        			"The dimension of a coordinate variable is equal to the given dimension.");
            }
            cDimList.add(cDim);

            // time variable
        	if (this.mTimeVariable != null) {
        		Dimension tDim = this.mTimeVariable.getDimension(0);
                if (tDim.equals(cDim)) {
                    throw new IllegalArgumentException(
            			"The dimension of a coordinate variable is equal to the dimension for time.");
                }
        	}

            this.checkNonCoordinateVariable(oVar, cDimList);
        }
    	this.checkNonCoordinateVariable(leVar, cDimList);
    	this.checkNonCoordinateVariable(ueVar, cDimList);
    	this.checkNonCoordinateVariable(tlVar, cDimList);

        // set to attributes
        this.mXVariables = this.createVariableArray(xVar);
        this.mYVariables = this.createVariableArray(yVar);
        this.mLowerErrorVariables = this.createVariableArray(leVar);
        this.mUpperErrorVariables = this.createVariableArray(ueVar);
        this.mErrorBarHolderVariables = this.createVariableArray(ehVar);
        this.mTickLabelVariables = this.createVariableArray(tlVar);
        this.mTickLabelHolderVariables = this.createVariableArray(thVar);
        this.mPickUpDimensionInfo = new SGNetCDFPickUpDimensionInfo(pickUpDim.getName(),
        		(SGIntegerSeriesSet) pickUpIndices.clone());
        if (!this.isIndexAvailable()) {
            this.mStride = this.createStride(stride, cDim);
        }
        this.setupTickLabelStride(tickLabelStride);

        this.updateDimensionIndices();
    }

    protected SGNetCDFVariable getVariable(final SGNetCDFVariable[] vars) {
    	if (vars == null || vars.length == 0) {
    		return null;
    	} else {
        	return vars[0];
    	}
    }

    private SGNetCDFVariable[] getVariables(SGNetCDFVariable[] vars) {
    	if (vars != null) {
        	return vars.clone();
    	} else {
    		return null;
    	}
    }

    public SGNetCDFVariable[] getXVariables() {
    	return this.getVariables(this.mXVariables);
    }

    public SGNetCDFVariable[] getYVariables() {
    	return this.getVariables(this.mYVariables);
    }

    public SGNetCDFVariable[] getLowerErrorVariables() {
    	return this.getVariables(this.mLowerErrorVariables);
    }

    public SGNetCDFVariable[] getUpperErrorVariables() {
    	return this.getVariables(this.mUpperErrorVariables);
    }

    public SGNetCDFVariable[] getErrorHolderVariables() {
    	return this.getVariables(this.mErrorBarHolderVariables);
    }

    public SGNetCDFVariable[] getTickLabelVariables() {
    	return this.getVariables(this.mTickLabelVariables);
    }

    public SGNetCDFVariable[] getTickLabelHolderVariables() {
    	return this.getVariables(this.mTickLabelHolderVariables);
    }

    protected SGNetCDFVariable getXVariable() {
    	return this.getVariable(this.mXVariables);
    }

    protected SGNetCDFVariable getYVariable() {
    	return this.getVariable(this.mYVariables);
    }

    protected SGNetCDFVariable getLowerErrorVariable() {
    	return this.getVariable(this.mLowerErrorVariables);
    }

    protected SGNetCDFVariable getUpperErrorVariable() {
    	return this.getVariable(this.mUpperErrorVariables);
    }

    protected SGNetCDFVariable getErrorBarHolderVariable() {
    	return this.getVariable(this.mErrorBarHolderVariables);
    }

    protected SGNetCDFVariable getTickLabelVariable() {
    	return this.getVariable(this.mTickLabelVariables);
    }

    protected SGNetCDFVariable getTickLabelHolderVariable() {
    	return this.getVariable(this.mTickLabelHolderVariables);
    }

    protected SGNetCDFVariable[] createVariableArray(final SGNetCDFVariable var) {
    	if (var != null) {
    		SGNetCDFVariable[] vars = new SGNetCDFVariable[1];
        	vars[0] = var;
        	return vars;
    	} else {
    		return null;
    	}
    }

    // Updates dimension indices.
    protected void updateDimensionIndices() {
    	if (this.mPickUpDimensionInfo != null) {
    		this.mDimensionIndices = this.mPickUpDimensionInfo.getIndices().getNumbers();
    	} else {
    		this.mDimensionIndices = new int[0];
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
    		return this.getStrideInstance().getLength();
    	}
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

    protected SGNetCDFVariable[] getVariables(final SGNetCDFDataColumnInfo[] names,
    		SGNetCDFFile ncfile, List<Dimension> cDimList) {
        SGNetCDFVariable[] vars = null;
    	if (names != null) {
            vars = new SGNetCDFVariable[names.length];
            for (int ii = 0; ii < vars.length; ii++) {
                SGNetCDFVariable var = ncfile.findVariable(names[ii].getName());
                this.checkNonCoordinateVariable(var, cDimList);
                vars[ii] = var;
            }
    	}
    	return vars;
    }

    /**
     * Returns whether a dimension is picked up.
     *
     * @return true if a dimension is picked up
     */
    public boolean isDimensionPicked() {
    	return (this.mPickUpDimensionInfo != null);
    }

    /**
     * Returns whether this multiple data has multiple arrays for y-values.
     *
     * @return true if this multiple data has multiple arrays for y-values
     */
    public boolean hasMultipleYValues() {
    	if (this.isDimensionPicked()) {
        	// if the x variable is a coordinate variable,
        	// this data has multiple y values
        	return this.getXVariable().isCoordinateVariable();
    	} else {
        	// if the array of x indices has only one elements,
        	// this data has "multiple" y values
        	return (this.mXVariables.length == 1);
    	}
    }

    /**
     * Returns a variable for multiple values.
     *
     * @return a variable for multiple values
     */
    public SGNetCDFVariable[] getMultipleVariables() {
        if (this.hasMultipleYValues()) {
            return this.mYVariables;
        } else {
            return this.mXVariables;
        }
    }

    /**
     * Returns a variable for "not" multiple values.
     *
     * @return a variable for "not" multiple values
     */
    public SGNetCDFVariable getSingleVariable() {
		if (this.hasMultipleYValues()) {
			return this.getXVariable();
		} else {
			return this.getYVariable();
		}
    }

    @Override
    protected double[] arrangeValueArray(Array data, SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, SGIntegerSeries> dimensionStrideMap) {
        return this.arrange1Dim(data);
    }

    @Override
    protected String[] arrangeStringArray(ArrayChar data, SGNetCDFVariable var, SGNetCDFVariable[] cVars,
    		Map<String, SGIntegerSeries> dimensionStrideMap) {
        return this.arrangeString1Dim(data, this.getAllPointsNumber());
    }

    protected int find(SGNetCDFVariable var, SGNetCDFVariable[] vars) {
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
     * Sets the decimal places for the tick labels.
     *
     * @param dp
     *          a value to set to the decimal places
     */
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
	public void setExponent(int exp) {
        this.mExponent = exp;
	}

    /**
     * Returns the decimal places for the tick labels.
     *
     * @return the decimal places for the tick labels
     */
	public int getDecimalPlaces() {
        return this.mDecimalPlaces;
	}

    /**
     * Returns the exponent for tick labels.
     *
     * @return the exponent for tick labels
     */
	public int getExponent() {
        return this.mExponent;
	}

    protected SGNetCDFVariable getCoordinateVariable() {
    	if (this.isIndexAvailable()) {
    		return this.mIndexVariable;
    	} else {
    		if (this.mXVariables[0].isCoordinateVariable()) {
    			return this.mXVariables[0];
    		} else if (this.mYVariables[0].isCoordinateVariable()) {
    			return this.mYVariables[0];
    		} else {
    			return null;
    		}
    	}
    }

    public boolean useCache(final boolean all) {
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
			stride = this.mStride;
		}
		return stride.isComplete();
	}

    /**
     * Return whether the XVariable is a coordinate variable.
     *
     * @return whether the XVariable is a coordinate variable.
     */
    public boolean isXVariableCoordinate() {
    	if (this.isDimensionPicked()) {
            return this.getXVariable().isCoordinateVariable();
    	} else {
            if (this.getXVariables().length == 1) {
                return this.getXVariables()[0].isCoordinateVariable();
            } else {
                return false;
            }
    	}
    }

    /**
     * Disposes of this data object.
     * Do not call any method of this object after called this method.
     */
    public void dispose() {
        super.dispose();

        this.mXVariables = null;
        this.mYVariables = null;
        this.mLowerErrorVariables = null;
        this.mUpperErrorVariables = null;
        this.mTickLabelVariables = null;
        this.mTickLabelHolderVariables = null;
        this.mDimensionIndices = null;
        this.mPickUpDimensionInfo = null;
        this.mStride = null;
        this.mTickLabelStride = null;
        this.mShift = null;
    }

	/**
	 * Returns the picked up dimension.
	 *
	 * @return the picked up dimension
	 */
	public Dimension getDimension() {
		if (this.mPickUpDimensionInfo != null) {
			String name = this.mPickUpDimensionInfo.getDimensionName();
			return this.findDimension(name);
		} else {
			return null;
		}
	}

    /**
     * Returns name of the dimension.
     *
     * @return name of the dimension
     */
    public String getDimensionName() {
    	Dimension dim = this.getDimension();
    	if (dim != null) {
    		return dim.getName();
    	} else {
    		return null;
    	}
    }

    /**
     * Returns an index array of dimension.
     *
     * @return an index array of dimension
     */
    public int[] getDimensionIndices() {
        return this.mDimensionIndices.clone();
    }

    @Override
	public SGIntegerSeriesSet getIndices() {
		if (this.mPickUpDimensionInfo != null) {
			return this.mPickUpDimensionInfo.getIndices();
		} else {
			return null;
		}
	}

    /**
     * The class of properties for multiple netCDF data.
     *
     */
    public static class SXYNetCDFMultipleDataProperties extends NetCDFDataProperties {

        protected String[] xNames = null;

        protected String[] yNames = null;

        protected String[] lNames = null;

        protected String[] uNames = null;

        protected String[] ehNames = null;

        protected String[] tNames = null;

        protected String[] thNames = null;

    	protected int mDecimalPlaces = 0;

    	protected int mExponent = 0;

    	protected SGNetCDFPickUpDimensionInfo mPickUpInfo = null;

        SGIntegerSeriesSet mStride = null;

        SGIntegerSeriesSet mTickLabelStride = null;

        /**
         * The default constructor.
         */
    	public SXYNetCDFMultipleDataProperties() {
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
            if ((obj instanceof SXYNetCDFMultipleDataProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            SXYNetCDFMultipleDataProperties p = (SXYNetCDFMultipleDataProperties) obj;
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
            if ((dp instanceof SXYNetCDFMultipleDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualSize(dp)) {
            	return false;
            }
            SXYNetCDFMultipleDataProperties p = (SXYNetCDFMultipleDataProperties) dp;
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
            if ((dp instanceof SXYNetCDFMultipleDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualColumnTypes(dp)) {
            	return false;
            }
            SXYNetCDFMultipleDataProperties p = (SXYNetCDFMultipleDataProperties) dp;
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

//            // compares pick up dimension
//        	String dimNameThis = null;
//        	String dimName = null;
//        	if (this.mPickUpInfo != null) {
//        		dimNameThis = this.mPickUpInfo.getDimensionName();
//        	}
//        	if (p.mPickUpInfo != null) {
//        		dimName = p.mPickUpInfo.getDimensionName();
//        	}
//        	if (SGUtility.equals(dimNameThis, dimName) == false) {
//        		return false;
//        	}
        	return true;
        }

        /**
         * Returns a copy of this object.
         *
         * @return a copy of this object
         */
        public Object copy() {
        	SXYNetCDFMultipleDataProperties p = (SXYNetCDFMultipleDataProperties) super.copy();
        	if (this.xNames != null) {
        		p.xNames = (String[]) this.xNames;
        	}
        	if (this.yNames != null) {
        		p.yNames = (String[]) this.yNames;
        	}
        	if (this.lNames != null) {
        		p.lNames = (String[]) this.lNames;
        	}
        	if (this.uNames != null) {
        		p.uNames = (String[]) this.uNames;
        	}
        	if (this.ehNames != null) {
        		p.ehNames = (String[]) this.ehNames;
        	}
        	if (this.tNames != null) {
        		p.tNames = (String[]) this.tNames;
        	}
        	if (this.thNames != null) {
        		p.thNames = (String[]) this.thNames;
        	}
        	if (this.mPickUpInfo != null) {
        		p.mPickUpInfo = (SGNetCDFPickUpDimensionInfo) this.mPickUpInfo.clone();
        	}
        	p.mStride = (this.mStride != null) ? (SGIntegerSeriesSet) this.mStride.clone() : null;
        	p.mTickLabelStride = (this.mTickLabelStride != null) ? (SGIntegerSeriesSet) this.mTickLabelStride.clone() : null;
        	return p;
        }
    }

	protected void setPickUpProperties(SXYNetCDFMultipleDataProperties mp) {
		this.mPickUpDimensionInfo = (mp.mPickUpInfo != null) ? (SGNetCDFPickUpDimensionInfo) mp.mPickUpInfo
				.clone() : null;
		this.updateDimensionIndices();
	}

	@Override
	public SGPickUpDimensionInfo getPickUpDimensionInfo() {
		if (this.mPickUpDimensionInfo != null) {
			return (SGPickUpDimensionInfo) this.mPickUpDimensionInfo.clone();
		} else {
			return null;
		}
	}

	@Override
	public boolean setPickUpDimensionInfo(SGPickUpDimensionInfo info) {
		if (info != null) {
			if (!(info instanceof SGNetCDFPickUpDimensionInfo)) {
				throw new IllegalArgumentException("Invalid input: " + info);
			}
			this.mPickUpDimensionInfo = (SGNetCDFPickUpDimensionInfo) info.clone();
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
		if (this.isDimensionPicked()) {
			return (this.getLowerErrorVariable() != null && this.getUpperErrorVariable() != null);
		} else {
			return (this.mLowerErrorVariables != null && this.mUpperErrorVariables != null);
		}
	}

    /**
     * Returns whether tick labels are available.
     *
     * @return true if tick labels are available
     */
	@Override
	public boolean isTickLabelAvailable() {
		if (this.isDimensionPicked()) {
			return (this.getTickLabelVariable() != null);
		} else {
	        if (this.mIsSingleVariableDateFlag) {
	            return true;
	        }
			return (this.mTickLabelVariables != null);
		}
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
	        if (this.mIsSingleVariableDateFlag) {
	            if (this.hasMultipleYValues() && this.mXVariables[0] instanceof SGDateVariable) {
	                return true;
	            } else {
	                return false;
	            }
	        } else {
	            return SGUtility.contains(this.mTickLabelHolderVariables, this.mYVariables);
	        }
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
        return SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA;
	}

    /**
     * Returns the title for the X-axis.
     *
     * @return the title for the X-axis
     */
	@Override
	public String getTitleX() {
		if (this.hasSingleVar(true)) {
            return this.getNameWithUnit(this.getXVariable());
		} else {
			return "";
		}
	}
	
    /**
     * Returns the title for the Y-axis.
     *
     * @return the title for the Y-axis
     */
	@Override
	public String getTitleY() {
		if (this.hasSingleVar(false)) {
            return this.getNameWithUnit(this.getYVariable());
		} else {
			return "";
		}
	}
	
	private boolean hasSingleVar(final boolean bx) {
		boolean singleVar = false;
		if (this.isIndexAvailable()) {
			singleVar = true;
		} else {
			if (this.isDimensionPicked()) {
				singleVar = true;
			} else {
		        if (this.mXVariables.length == 1 && this.mYVariables.length == 1) {
					singleVar = true;
		        } else {
	        		singleVar = (bx == this.hasMultipleYValues());
		        }
			}
		}
		return singleVar;
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
	        final int len = this.mDimensionIndices.length;
	        final List<Dimension> dimList = this.getNetcdfFile().getDimensions();
	        final String dimName = this.getDimensionName();
	        final SGNetCDFVariable xVar = this.getXVariable();
	        final SGNetCDFVariable yVar = this.getYVariable();
	        final SGNetCDFVariable leVar = this.getLowerErrorVariable();
	        final SGNetCDFVariable ueVar = this.getUpperErrorVariable();
	        final SGNetCDFVariable ehVar = this.getErrorBarHolderVariable();
	        final SGNetCDFVariable tlVar = this.getTickLabelVariable();
	        final SGNetCDFVariable thVar = this.getTickLabelHolderVariable();
	        final SGNetCDFVariable timeVar = (this.mTimeVariable != null) ? this.mTimeVariable : null;
	        final SGNetCDFVariable indexVar = (this.mIndexVariable != null) ? this.mIndexVariable : null;

	        SGNetCDFDataColumnInfo xInfo = SGDataUtility.createDataColumnInfo(xVar, X_VALUE);
	        SGNetCDFDataColumnInfo yInfo = SGDataUtility.createDataColumnInfo(yVar, Y_VALUE);
	        SGNetCDFDataColumnInfo leInfo = null;
	        SGNetCDFDataColumnInfo ueInfo = null;
	        SGNetCDFDataColumnInfo ehInfo = null;
	        if (leVar != null && ueVar != null && ehVar != null) {
	        	leInfo = SGDataUtility.createErrorBarInfo(leVar, LOWER_ERROR_VALUE, leVar, ueVar, ehVar);
	        	ueInfo = SGDataUtility.createErrorBarInfo(ueVar, UPPER_ERROR_VALUE, leVar, ueVar, ehVar);
	        	ehInfo = (SGNetCDFDataColumnInfo) (ehVar.equals(xVar) ? xInfo : yInfo).clone();
	        }
	        SGNetCDFDataColumnInfo tlInfo = null;
	        SGNetCDFDataColumnInfo thInfo = null;
	        if (tlVar != null && thVar != null) {
	        	tlInfo = SGDataUtility.createDataColumnInfo(tlVar, TICK_LABEL, thVar.getName());
	        	thInfo = (SGNetCDFDataColumnInfo) (thVar.equals(xVar) ? xInfo : yInfo).clone();
	        }
	        SGNetCDFDataColumnInfo timeInfo = null;
	        if (timeVar != null) {
	        	timeInfo = SGDataUtility.createDataColumnInfo(timeVar, ANIMATION_FRAME);
	        }
	        SGNetCDFDataColumnInfo indexInfo = null;
	        if (indexVar != null) {
	        	indexInfo = SGDataUtility.createDataColumnInfo(indexVar, INDEX);
	        }

	        final SGISXYTypeSingleData[] dataArray = new SGISXYTypeSingleData[len];
	        for (int ii = 0; ii < dataArray.length; ii++) {
	            SGSXYNetCDFData data;
				if (this.getXVariable() instanceof SGDateVariable
						|| this.getYVariable() instanceof SGDateVariable) {
					data = new SGSXYNetCDFDateData(this.getNetcdfFile(), this.getDataSourceObserver(),
							xInfo, yInfo, leInfo, ueInfo, ehInfo, tlInfo,
							thInfo, timeInfo, indexInfo, this.getStride(),
							this.getIndexStride(), this.getTickLabelStride(),
							this.isStrideAvailable());
				} else {
					data = new SGSXYNetCDFData(this.getNetcdfFile(), this.getDataSourceObserver(), xInfo,
							yInfo, leInfo, ueInfo, ehInfo, tlInfo,
							thInfo, timeInfo, indexInfo, this.getStride(),
							this.getIndexStride(), this.getTickLabelStride(),
							this.isStrideAvailable());
				}
	            for (Dimension dim : dimList) {
	                String name = dim.getName();
	                Integer origin;
	                if (name.equals(dimName)) {
	                    origin = this.mDimensionIndices[ii];
	                } else {
	                    origin = this.mOriginMap.get(name);
	                    if (origin == null) {
	                        origin = Integer.valueOf(0);
	                    }
	                }
	                data.setOrigin(name, origin.intValue());
	            }

	            // sets attributes for tick labels
	            data.setDecimalPlaces(this.mDecimalPlaces);
	            data.setExponent(this.mExponent);
	            data.setDateFormat(this.mDateFormat);

	            // sets the stride of time
	            data.setTimeStride(this.mTimeStride);

	            // sets the shift
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
	    	SGNetCDFVariable varSingle = this.getSingleVariable();
	    	SGNetCDFVariable[] varMultiple = this.getMultipleVariables();
	        final int len = varMultiple.length;
			final SGNetCDFVariable timeVar = this.mTimeVariable;
			final SGNetCDFVariable indexVar = this.mIndexVariable;
	        List<SGNetCDFVariable> varList = this.getNetcdfFile().getVariables();
	        final boolean by = this.hasMultipleYValues();
	        final boolean be = this.isErrorBarAvailable();
	        final boolean bt = this.isTickLabelAvailable();
	        final SGISXYTypeSingleData[] dataArray = new SGISXYTypeSingleData[len];
	        for (int ii = 0; ii < dataArray.length; ii++) {
	        	SGNetCDFVariable xVar = by ? varSingle : varMultiple[ii];
	        	SGNetCDFVariable yVar = by ? varMultiple[ii] : varSingle;
	        	SGNetCDFVariable leVar = null;
	        	SGNetCDFVariable ueVar = null;
	        	SGNetCDFVariable ehVar = null;
	            if (be) {
	                for (int jj = 0; jj < this.mErrorBarHolderVariables.length; jj++) {
	                	SGNetCDFVariable var = this.mErrorBarHolderVariables[jj];
	                    if (xVar.equals(var) || yVar.equals(var)) {
	                        ehVar = var;
	                        leVar = this.mLowerErrorVariables[jj];
	                        ueVar = this.mUpperErrorVariables[jj];
	                        break;
	                    }
	                }
	            }
	            SGNetCDFVariable tlVar = null;
	            SGNetCDFVariable thVar = null;
	            if (bt && this.mTickLabelHolderVariables != null) {
	            	// checks whether the holder variable is null because isTickLabelAvailable method
	            	// returns true even when date variable is used and mTickLabelHolderVariables is null
	                for (int jj = 0; jj < this.mTickLabelHolderVariables.length; jj++) {
	                	SGNetCDFVariable var = this.mTickLabelHolderVariables[jj];
	                    if (xVar.equals(var) || yVar.equals(var)) {
	                        thVar = var;
	                        tlVar = this.mTickLabelVariables[jj];
	                        break;
	                    }
	                }
	            }

	            SGNetCDFDataColumnInfo xInfo = SGDataUtility.createDataColumnInfo(xVar, X_VALUE);
	            SGNetCDFDataColumnInfo yInfo = SGDataUtility.createDataColumnInfo(yVar, Y_VALUE);
	            SGNetCDFDataColumnInfo leInfo = null;
	            SGNetCDFDataColumnInfo ueInfo = null;
	            SGNetCDFDataColumnInfo ehInfo = null;
	            if (leVar != null && ueVar != null && ehVar != null) {
		        	leInfo = SGDataUtility.createErrorBarInfo(leVar, LOWER_ERROR_VALUE, leVar, ueVar, ehVar);
		        	ueInfo = SGDataUtility.createErrorBarInfo(ueVar, UPPER_ERROR_VALUE, leVar, ueVar, ehVar);
		        	ehInfo = (SGNetCDFDataColumnInfo) (ehVar.equals(xVar) ? xInfo : yInfo).clone();
	            }
	            SGNetCDFDataColumnInfo tlInfo = null;
	            SGNetCDFDataColumnInfo thInfo = null;
	            if (tlVar != null && thVar != null) {
		        	tlInfo = SGDataUtility.createDataColumnInfo(tlVar, TICK_LABEL, thVar.getName());
		        	thInfo = (SGNetCDFDataColumnInfo) (thVar.equals(xVar) ? xInfo : yInfo).clone();
	            }
	            SGNetCDFDataColumnInfo timeInfo = null;
	            if (timeVar != null) {
	            	timeInfo = SGDataUtility.createDataColumnInfo(timeVar, ANIMATION_FRAME);
	            }
	            SGNetCDFDataColumnInfo indexInfo = null;
	            if (indexVar != null) {
	            	indexInfo = SGDataUtility.createDataColumnInfo(indexVar, INDEX);
	            }

	            SGSXYNetCDFData data;
	            if (this.getNetcdfFile().findVariable(xVar.getName()) instanceof SGDateVariable ||
	                    this.getNetcdfFile().findVariable(yVar.getName()) instanceof SGDateVariable) {
	                data = new SGSXYNetCDFDateData(this.getNetcdfFile(), this.getDataSourceObserver(),
	                        xInfo, yInfo, leInfo, ueInfo, ehInfo, tlInfo, thInfo, timeInfo,
	                        indexInfo, this.getStride(), this.getIndexStride(),
	                        this.getTickLabelStride(), this.isStrideAvailable());
	            } else {
	                data = new SGSXYNetCDFData(this.getNetcdfFile(), this.getDataSourceObserver(),
	                        xInfo, yInfo, leInfo, ueInfo, ehInfo, tlInfo, thInfo, timeInfo,
	                        indexInfo, this.getStride(), this.getIndexStride(),
	                        this.getTickLabelStride(), this.isStrideAvailable());
	            }
	            for (SGNetCDFVariable var : varList) {
	                if (var.isCoordinateVariable()) {
	                    String name = var.getName();
	                    if (this.mOriginMap.containsKey(name)) {
	                        final int origin = this.mOriginMap.get(name);
	                        data.setOrigin(name, origin);
	                    }
	                }
	            }

	            // sets attributes for tick labels
	            data.setDecimalPlaces(this.mDecimalPlaces);
	            data.setExponent(this.mExponent);
	            data.setDateFormat(this.mDateFormat);

	            // sets the stride of time
	            data.setTimeStride(this.mTimeStride);
	            
	            // sets the shift
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
	        return this.mDimensionIndices.length;
		} else {
	    	SGNetCDFVariable[] varMultiple = this.getMultipleVariables();
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
        SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
        SGISXYTypeMultipleData[] ret = new SGISXYTypeMultipleData[sxyArray.length];
        for (int ii = 0; ii < sxyArray.length; ii++) {
            SGSXYNetCDFData data = (SGSXYNetCDFData) sxyArray[ii];
    		if (this.isDimensionPicked()) {
    			SGNetCDFVariable pickUpVar = this.getPickUpNetCDFVariable();
    			final int len = pickUpVar.getDimension(0).getLength();
	            ret[ii] = data.toMultiple(this.getDimension(), this.mDimensionIndices[ii], len);
    		} else {
                ret[ii] = data.toMultiple();
    		}
        }
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);
        return ret;
	}

    public SGNetCDFVariable getPickUpNetCDFVariable() {
    	if (this.isDimensionPicked()) {
    		String name = this.mPickUpDimensionInfo.getDimensionName();
    		return this.findVariable(name);
    	} else {
    		return null;
    	}
    }

    /**
     * Returns true if this data enables to be split.
     *
     * @return true if this data enables to be split
     */
	@Override
	public boolean isSplitEnabled() {
		final int len;
		if (this.isDimensionPicked()) {
	        len = this.mDimensionIndices.length;
		} else {
	        SGNetCDFVariable[] varMultiple = this.getMultipleVariables();
	        len = varMultiple.length;
		}
        return (len > 1);
	}

    /**
     * Returns an array of current column types.
     *
     * @return an array of current column types
     */
    @Override
    public String[] getCurrentColumnType() {
        List<SGNetCDFVariable> varList = this.getNetcdfFile().getVariables();
        final int varNum = varList.size();
        String[] array = new String[varNum];
        SGNetCDFFile ncFile = this.getNetcdfFile();
        for (int ii = 0; ii < varNum; ii++) {
            SGNetCDFVariable var = varList.get(ii);
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
                final int hIndex = ncFile.getVariableIndex(varName);
                if (hIndex == -1) {
                    return null;
                }
                array[ii] = SGDataUtility.appendColumnTitle(name, varName);
            } else if (tIndex != -1) {
                String varName = this.mTickLabelHolderVariables[tIndex].getName();
                final int hIndex = ncFile.getVariableIndex(varName);
                if (hIndex == -1) {
                    return null;
                }
                array[ii] = SGDataUtility.appendColumnTitle(TICK_LABEL, varName);
            } else if (var.equals(this.mTimeVariable)) {
                array[ii] = ANIMATION_FRAME;
            } else if (var.equals(this.mIndexVariable)) {
				array[ii] = INDEX;
			} else {
				String type = "";
				if (this.isDimensionPicked()) {
					if (var.isCoordinateVariable()) {
						Dimension dim = var.getDimension(0);
						if (dim.getName().equals(this.getDimensionName())) {
							type = PICKUP;
						}
					}
				}
				array[ii] = type;
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
		if (this.isDimensionPicked()) {
	        List<SGNetCDFVariable> varList = new ArrayList<SGNetCDFVariable>();
	        varList.add(this.getPickUpNetCDFVariable());
	        varList.add(this.getXVariable());
	        varList.add(this.getYVariable());
	        if (this.isErrorBarAvailable()) {
	        	SGNetCDFVariable lVar = this.getLowerErrorVariable();
	        	varList.add(lVar);
	        	SGNetCDFVariable uVar = this.getUpperErrorVariable();
	        	if (!uVar.equals(lVar)) {
		        	varList.add(uVar);
	        	}
	        }
	        if (this.isTickLabelAvailable()) {
	        	SGNetCDFVariable tlVar = this.getTickLabelVariable();
	        	varList.add(tlVar);
	        }
	        if (this.mTimeVariable != null) {
	        	varList.add(this.mTimeVariable);
	        }
	        if (this.mIndexVariable != null) {
	        	varList.add(this.mIndexVariable);
	        }
	        SGNetCDFVariable[] varArray = new SGNetCDFVariable[varList.size()];
	        return (SGNetCDFVariable[]) varList.toArray(varArray);
		} else {
	        List<SGNetCDFVariable> varList = new ArrayList<SGNetCDFVariable>();
	        for (int ii = 0; ii < this.mXVariables.length; ii++) {
	            varList.add(this.mXVariables[ii]);
	        }
	        for (int ii = 0; ii < this.mYVariables.length; ii++) {
	            varList.add(this.mYVariables[ii]);
	        }
	        if (this.isErrorBarAvailable()) {
	            for (int ii = 0; ii < this.mLowerErrorVariables.length; ii++) {
	            	varList.add(this.mLowerErrorVariables[ii]);
	            }
	            for (int ii = 0; ii < this.mUpperErrorVariables.length; ii++) {
	            	if (!varList.contains(this.mUpperErrorVariables[ii])) {
		            	varList.add(this.mUpperErrorVariables[ii]);
	            	}
	            }
	        }
	        if (this.mTickLabelVariables != null) {
	            for (int ii = 0; ii < this.mTickLabelVariables.length; ii++) {
	            	varList.add(this.mTickLabelVariables[ii]);
	            }
	        }
	        if (this.mTimeVariable != null) {
	        	varList.add(this.mTimeVariable);
	        }
	        if (this.mIndexVariable != null) {
	        	varList.add(this.mIndexVariable);
	        }
	        SGNetCDFVariable[] varArray = new SGNetCDFVariable[varList.size()];
	        return (SGNetCDFVariable[]) varList.toArray(varArray);
		}
	}

    /**
     * Returns a map which has data information.
     *
     * @return a map which has data information
     */
    public Map<String, Object> getInfoMap() {
        Map<String, Object> infoMap = super.getInfoMap();
        infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.TRUE);
        if (this.isDimensionPicked()) {
            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, this.mPickUpDimensionInfo.getIndices());
        } else {
            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
        }
        return infoMap;
    }

    /**
     * Returns the origin of a coordinate variable.
     *
     * @param name
     *           the name of the coordinate variable
     * @return the origin of the coordinate variable
     */
    @Override
    public int getOrigin(final String name) {
    	if (this.isDimensionPicked()) {
            if (SGDataUtility.isSameNetCDFGroup(name, this.getDimensionName())) {
                return super.getOrigin(name);
            } else {
                return 0;
            }
    	} else {
    		return super.getOrigin(name);
    	}
    }

    /**
     * Sets the data.
     *
     * @param data
     *             data set to this object
     * @return
     *             true if succeeded
     */
    @Override
    public boolean setData(SGData data) {
        if (!(data instanceof SGSXYNetCDFMultipleData)) {
            throw new IllegalArgumentException("Given data is not the instance of SGSXYMultipleNetCDFData class.");
        }
        if (super.setData(data) == false) {
            return false;
        }
        SGSXYNetCDFMultipleData nData = (SGSXYNetCDFMultipleData) data;
        this.mXVariables = this.getVariables(nData.mXVariables);
        this.mYVariables = this.getVariables(nData.mYVariables);
        this.mLowerErrorVariables = this.getVariables(nData.mLowerErrorVariables);
        this.mUpperErrorVariables = this.getVariables(nData.mUpperErrorVariables);
        this.mErrorBarHolderVariables = this.getVariables(nData.mErrorBarHolderVariables);
        this.mTickLabelVariables = this.getVariables(nData.mTickLabelVariables);
        this.mTickLabelHolderVariables = this.getVariables(nData.mTickLabelHolderVariables);
    	if (nData.isDimensionPicked()) {
            SGIntegerSeriesSet indices = (SGIntegerSeriesSet) nData.getIndices().clone();
            this.mPickUpDimensionInfo = new SGNetCDFPickUpDimensionInfo(nData.getDimensionName(), indices);
            this.updateDimensionIndices();
    	} else {
            this.updateIsSingleVariableDateFlag();
    	}
    	this.setStride(nData.mStride);
    	this.setTickLabelStride(nData.mTickLabelStride);
        this.mShift = nData.getShift();
        return true;
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
//		if (this.isDimensionPicked()) {
//			return this.setColumnTypeDimensionPicked(columns);
//		} else {
//			return this.setColumnTypeDimensionNotPicked(columns);
//		}
		// tries both cases
		if (this.setColumnTypeDimensionPicked(columns)) {
			return true;
		}
		if (this.setColumnTypeDimensionNotPicked(columns)) {
			return true;
		}
		return false;
	}

    /**
     * Sets the type of data columns with the information of picked up dimension.
     *
     * @param column
     *            an array of column types
     * @param info
     *            the information of picked up dimension
     * @return true if succeeded
     */
	public boolean setColumnType(String[] columns, SGPickUpDimensionInfo info) {
		this.setPickUpDimensionInfo(info);
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
        List<SGNetCDFVariable> xVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> yVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> lVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> uVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> tVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> timeVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> indexVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> pickUpVarList = new ArrayList<SGNetCDFVariable>();
    	List<String> lColList = new ArrayList<String>();
    	List<String> uColList = new ArrayList<String>();
    	List<String> tColList = new ArrayList<String>();
        if (!this.getVariables(columns, xVarList, yVarList, lVarList, uVarList, tVarList,
        		timeVarList, indexVarList, pickUpVarList, lColList, uColList, tColList)) {
        	return false;
        }
        if (xVarList.size() != 1 || yVarList.size() != 1) {
        	return false;
        }
        if (lVarList.size() > 1 || uVarList.size() > 1 || tVarList.size() > 1) {
        	return false;
        }
        if (pickUpVarList.size() != 1) {
        	// only one pick up variable must exist
        	return false;
        }

    	SGNetCDFVariable timeVar = null;
    	if (timeVarList.size() == 1) {
    		timeVar = timeVarList.get(0);
    	}
    	SGNetCDFVariable indexVar = null;
    	if (indexVarList.size() == 1) {
    		indexVar = indexVarList.get(0);
    	}

    	SGNetCDFVariable xVar = xVarList.get(0);
    	SGNetCDFVariable yVar = yVarList.get(0);
    	SGNetCDFVariable pickUpVar = pickUpVarList.get(0);
        if (xVar == null || yVar == null || pickUpVar == null) {
        	return false;
        }
    	SGNetCDFVariable lVar = lVarList.size() == 1 ? lVarList.get(0) : null;
    	SGNetCDFVariable uVar = uVarList.size() == 1 ? uVarList.get(0) : null;
    	SGNetCDFVariable tVar = tVarList.size() == 1 ? tVarList.get(0) : null;
        if (!((lVar != null) && (uVar != null)) && !((lVar == null) && (uVar == null))) {
        	return false;
        }

        List<SGNetCDFVariable> ehVarList = this.findErrorVarHolderVars(xVarList, yVarList,
        		lVarList, uVarList, lColList, uColList);
        SGNetCDFVariable ehVar = null;
        if (ehVarList != null && ehVarList.size() == 1) {
            ehVar = ehVarList.get(0);
        }

        List<SGNetCDFVariable> thVarList = this.findTickLabelHolderVars(xVarList, yVarList,
        		tVarList, tColList);
        SGNetCDFVariable thVar = null;
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
        this.mIndexVariable = indexVar;
        this.setTimeVariable(timeVar);

        if (tVar == null) {
        	this.updateDateTickLabelVariables();
        }

        return true;
	}

	private boolean setColumnTypeDimensionNotPicked(String[] columns) {
        List<SGNetCDFVariable> xVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> yVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> lVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> uVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> tVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> timeVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> indexVarList = new ArrayList<SGNetCDFVariable>();
        List<SGNetCDFVariable> pickUpVarList = new ArrayList<SGNetCDFVariable>();
    	List<String> lColList = new ArrayList<String>();
    	List<String> uColList = new ArrayList<String>();
    	List<String> tColList = new ArrayList<String>();
        if (!this.getVariables(columns, xVarList, yVarList, lVarList, uVarList, tVarList,
        		timeVarList, indexVarList, pickUpVarList, lColList, uColList, tColList)) {
        	return false;
        }
        if (pickUpVarList.size() != 0) {
        	// pick up variable must not exist
        	return false;
        }

    	SGNetCDFVariable timeVar = null;
    	if (timeVarList.size() == 1) {
    		timeVar = timeVarList.get(0);
    	}
    	SGNetCDFVariable indexVar = null;
    	if (indexVarList.size() == 1) {
    		indexVar = indexVarList.get(0);
    	}

        List<SGNetCDFVariable> ehVarList = this.findErrorVarHolderVars(xVarList, yVarList,
        		lVarList, uVarList, lColList, uColList);
        if (ehVarList == null) {
        	return false;
        }
        List<SGNetCDFVariable> thVarList = this.findTickLabelHolderVars(xVarList, yVarList,
        		tVarList, tColList);
        if (thVarList == null) {
        	return false;
        }

        SGNetCDFVariable[] xVars = xVarList.toArray(new SGNetCDFVariable[xVarList.size()]);
        SGNetCDFVariable[] yVars = yVarList.toArray(new SGNetCDFVariable[yVarList.size()]);
        SGNetCDFVariable[] lVars = null;
        SGNetCDFVariable[] uVars = null;
        SGNetCDFVariable[] ehVars = null;
        if (lVarList.size() != 0) {
            lVars = lVarList.toArray(new SGNetCDFVariable[lVarList.size()]);
            uVars = uVarList.toArray(new SGNetCDFVariable[uVarList.size()]);
            ehVars = ehVarList.toArray(new SGNetCDFVariable[ehVarList.size()]);
        }
        SGNetCDFVariable[] tVars = null;
        SGNetCDFVariable[] thVars = null;
        if (tVarList.size() != 0) {
            tVars = tVarList.toArray(new SGNetCDFVariable[tVarList.size()]);
            thVars = thVarList.toArray(new SGNetCDFVariable[thVarList.size()]);
        }

        // set to the attributes
        this.mXVariables = xVars;
        this.mYVariables = yVars;
        this.mLowerErrorVariables = lVars;
        this.mUpperErrorVariables = uVars;
        this.mErrorBarHolderVariables = ehVars;
        this.mTickLabelVariables = tVars;
        this.mTickLabelHolderVariables = thVars;
        this.mIndexVariable = indexVar;
        this.setTimeVariable(timeVar);

        this.updateIsSingleVariableDateFlag();
        
        if (tVars == null) {
        	this.updateDateTickLabelVariables();
        }

        return true;
	}

	private List<SGNetCDFVariable> findErrorVarHolderVars(
			List<SGNetCDFVariable> xVarList, List<SGNetCDFVariable> yVarList,
			List<SGNetCDFVariable> lVarList, List<SGNetCDFVariable> uVarList,
			List<String> lColList, List<String> uColList) {

        List<SGNetCDFVariable> varList = this.getNetcdfFile().getVariables();
        List<SGNetCDFVariable> ehVarList = new ArrayList<SGNetCDFVariable>();
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
            SGNetCDFVariable ehVar = varList.get(eh.intValue());
            if (!xVarList.contains(ehVar) && !yVarList.contains(ehVar)) {
            	return null;
            }
            ehVarList.add(ehVar);
        }
        return ehVarList;
	}

	private List<SGNetCDFVariable> findTickLabelHolderVars(
			List<SGNetCDFVariable> xVarList, List<SGNetCDFVariable> yVarList,
			List<SGNetCDFVariable> tVarList, List<String> tColList) {

        List<SGNetCDFVariable> varList = this.getNetcdfFile().getVariables();
        List<SGNetCDFVariable> thVarList = new ArrayList<SGNetCDFVariable>();
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
            SGNetCDFVariable thVar = varList.get(th.intValue());
            if (!xVarList.contains(thVar) && !yVarList.contains(thVar)) {
            	return null;
            }
            thVarList.add(thVar);
        }
        return thVarList;
	}

	private boolean getVariables(String[] columns,
			List<SGNetCDFVariable> xVarList, List<SGNetCDFVariable> yVarList,
			List<SGNetCDFVariable> lVarList, List<SGNetCDFVariable> uVarList,
			List<SGNetCDFVariable> tVarList,
			List<SGNetCDFVariable> timeVarList,
			List<SGNetCDFVariable> indexVarList,
			List<SGNetCDFVariable> pickUpVarList, List<String> lColList,
			List<String> uColList, List<String> tColList) {

        List<SGNetCDFVariable> varList = this.getNetcdfFile().getVariables();
        for (int ii = 0; ii < columns.length; ii++) {
            SGNetCDFVariable var = varList.get(ii);
            String valueType = var.getValueType();
            if (SGDataUtility.isEqualColumnType(X_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)
            			&& !VALUE_TYPE_DATE.equals(valueType)) {
            		return false;
            	}
                xVarList.add(var);
            } else if (SGDataUtility.isEqualColumnType(Y_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)
            			&& !VALUE_TYPE_DATE.equals(valueType)) {
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
            } else if (SGDataUtility.isEqualColumnType(columns[ii], PICKUP)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
            	pickUpVarList.add(var);
            } else if ("".equals(columns[ii])) {
            	continue;
            } else {
            	return false;
            }
        }

        // checks common conditions
        if (xVarList.size() == 0 || yVarList.size() == 0) {
        	return false;
        }
        if (xVarList.size() != 1 && yVarList.size() != 1) {
        	return false;
        }
        if (lVarList.size() != uVarList.size()) {
            return false;
        }
        if (timeVarList.size() > 1) {
        	return false;
        }
        if (indexVarList.size() > 1) {
        	return false;
        }

        return true;
	}

    /**
     * Returns the properties of this data.
     *
     * @return the properties of this data
     */
	@Override
    public SGProperties getProperties() {
        SGProperties p = new SXYNetCDFMultipleDataProperties();
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
	@Override
    public boolean getProperties(SGProperties p) {
        if (!(p instanceof SXYNetCDFMultipleDataProperties)) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        SXYNetCDFMultipleDataProperties sp = (SXYNetCDFMultipleDataProperties) p;
        if (this.isDimensionPicked()) {
            sp.xNames = new String[] { this.getName(this.getXVariable()) };
            sp.yNames = new String[] { this.getName(this.getYVariable()) };
            if (this.isErrorBarAvailable()) {
                sp.lNames = new String[] { this.getName(this.getLowerErrorVariable()) };
                sp.uNames = new String[] { this.getName(this.getUpperErrorVariable()) };
                sp.ehNames = new String[] { this.getName(this.getErrorBarHolderVariable()) };
            } else {
            	sp.lNames = null;
            	sp.uNames = null;
            	sp.ehNames = null;
            }
            if (this.isTickLabelAvailable()) {
                sp.tNames = new String[] { this.getName(this.getTickLabelVariable()) };
                sp.thNames = new String[] { this.getName(this.getTickLabelHolderVariable()) };
            } else {
            	sp.tNames = null;
            	sp.thNames = null;
            }
        } else {
            String[] xNames = new String[this.mXVariables.length];
            for (int ii = 0; ii < xNames.length; ii++) {
                xNames[ii] = this.mXVariables[ii].getName();
            }
            String[] yNames = new String[this.mYVariables.length];
            for (int ii = 0; ii < yNames.length; ii++) {
                yNames[ii] = this.mYVariables[ii].getName();
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
            	ehNames = new String[this.mErrorBarHolderVariables.length];
            	for (int ii = 0; ii < ehNames.length; ii++) {
            		ehNames[ii] = this.mErrorBarHolderVariables[ii].getName();
            	}
            }
            String[] tNames = null;
            String[] thNames = null;
            if (this.mTickLabelVariables!=null) {
            	tNames = new String[this.mTickLabelVariables.length];
            	for (int ii = 0; ii < tNames.length; ii++) {
            		tNames[ii] = this.mTickLabelVariables[ii].getName();
            	}
            	thNames = new String[this.mTickLabelHolderVariables.length];
            	for (int ii = 0; ii < thNames.length; ii++) {
            		thNames[ii] = this.mTickLabelHolderVariables[ii].getName();
            	}
            }
            sp.xNames = xNames;
            sp.yNames = yNames;
            sp.lNames = lNames;
            sp.uNames = uNames;
            sp.ehNames = ehNames;
            sp.tNames = tNames;
            sp.thNames = thNames;
        }

        sp.mDecimalPlaces = this.mDecimalPlaces;
        sp.mExponent = this.mExponent;
		sp.mPickUpInfo = (SGNetCDFPickUpDimensionInfo) this.getPickUpDimensionInfo();
        sp.mStride = this.getStride();
        sp.mTickLabelStride = this.getTickLabelStride();

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
        if (!(p instanceof SXYNetCDFMultipleDataProperties)) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        SXYNetCDFMultipleDataProperties sp = (SXYNetCDFMultipleDataProperties) p;
        SGNetCDFPickUpDimensionInfo pickUpInfo = sp.mPickUpInfo;
        if (pickUpInfo != null) {
            this.mXVariables = this.findVariables(sp.xNames);
            this.mYVariables = this.findVariables(sp.yNames);
            if (sp.lNames != null && sp.uNames != null) {
                this.mLowerErrorVariables = this.findVariables(sp.lNames);
                this.mUpperErrorVariables = this.findVariables(sp.uNames);
                this.mErrorBarHolderVariables = this.findVariables(sp.ehNames);
            } else {
            	this.mLowerErrorVariables = null;
            	this.mUpperErrorVariables = null;
            	this.mErrorBarHolderVariables = null;
            }

            if (sp.tNames != null) {
                this.mTickLabelVariables = this.findVariables(sp.tNames);
                this.mTickLabelHolderVariables = this.findVariables(sp.thNames);
            } else {
            	this.mTickLabelVariables = null;
            	this.mTickLabelHolderVariables = null;
            }

        } else {

        	SGNetCDFFile ncFile = this.getNetcdfFile();

            SGNetCDFVariable[] xVars = new SGNetCDFVariable[sp.xNames.length];
            for (int ii = 0; ii < xVars.length; ii++) {
                xVars[ii] = ncFile.findVariable(sp.xNames[ii]);
            }
            SGNetCDFVariable[] yVars = new SGNetCDFVariable[sp.yNames.length];
            for (int ii = 0; ii < yVars.length; ii++) {
                yVars[ii] = ncFile.findVariable(sp.yNames[ii]);
            }
            SGNetCDFVariable[] lVars = null;
            if (sp.lNames != null) {
                lVars = new SGNetCDFVariable[sp.lNames.length];
                for (int ii = 0; ii < lVars.length; ii++) {
                    lVars[ii] = ncFile.findVariable(sp.lNames[ii]);
                }
            }
            SGNetCDFVariable[] uVars = null;
            if (sp.uNames != null) {
                uVars = new SGNetCDFVariable[sp.uNames.length];
                for (int ii = 0; ii < uVars.length; ii++) {
                    uVars[ii] = ncFile.findVariable(sp.uNames[ii]);
                }
            }
            SGNetCDFVariable[] ehVars = null;
            if (sp.ehNames != null) {
                ehVars = new SGNetCDFVariable[sp.ehNames.length];
                for (int ii = 0; ii < ehVars.length; ii++) {
                    ehVars[ii] = ncFile.findVariable(sp.ehNames[ii]);
                }
            }
            SGNetCDFVariable[] tVars = null;
            if (sp.tNames != null) {
                tVars = new SGNetCDFVariable[sp.tNames.length];
                for (int ii = 0; ii < tVars.length; ii++) {
                    tVars[ii] = ncFile.findVariable(sp.tNames[ii]);
                }
            }
            SGNetCDFVariable[] thVars = null;
            if (sp.thNames != null) {
            	thVars = new SGNetCDFVariable[sp.thNames.length];
                for (int ii = 0; ii < thVars.length; ii++) {
                    thVars[ii] = ncFile.findVariable(sp.thNames[ii]);
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

            this.updateIsSingleVariableDateFlag();
        }

        this.mDecimalPlaces = sp.mDecimalPlaces;
        this.mExponent = sp.mExponent;
        this.setPickUpProperties(sp);
        this.setStride(sp.mStride);
        this.setTickLabelStride(sp.mTickLabelStride);

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
        if (this.isDimensionPicked()) {
            el.setAttribute(KEY_X_VALUE_NAME, this.getXVariable().getValidName());
            el.setAttribute(KEY_Y_VALUE_NAME, this.getYVariable().getValidName());
            if (this.isErrorBarAvailable()) {
                el.setAttribute(KEY_LOWER_ERROR_VALUE_NAME, this.getLowerErrorVariable().getValidName());
                el.setAttribute(KEY_UPPER_ERROR_VALUE_NAME, this.getUpperErrorVariable().getValidName());
                el.setAttribute(KEY_ERROR_BAR_HOLDER_NAME, this.getErrorBarHolderVariable().getValidName());
            }
            if (this.isTickLabelAvailable()) {
                el.setAttribute(KEY_TICK_LABEL_NAME, this.getTickLabelVariable().getValidName());
                el.setAttribute(KEY_TICK_LABEL_HOLDER_NAME, this.getTickLabelHolderVariable().getValidName());
            }
            String dimName = this.getDimensionName();
            if (dimName != null) {
                el.setAttribute(KEY_PICKUP_DIMENSION_NAME, dimName);
            }
            SGIntegerSeriesSet pickUpIndices = this.mPickUpDimensionInfo.getIndices();
            el.setAttribute(KEY_PICK_UP_DIMENSION_INDICES, pickUpIndices.toString());

        } else {
            String value = null;
            value = SGDataUtility.bindVariableNamesInBracket(this.mXVariables);
            el.setAttribute(KEY_X_VALUE_NAMES, value);
            value = SGDataUtility.bindVariableNamesInBracket(this.mYVariables);
            el.setAttribute(KEY_Y_VALUE_NAMES, value);
            if (this.isErrorBarAvailable()) {
                value = SGDataUtility.bindVariableNamesInBracket(this.mLowerErrorVariables);
                el.setAttribute(KEY_LOWER_ERROR_VALUE_NAMES, value);
                value = SGDataUtility.bindVariableNamesInBracket(this.mUpperErrorVariables);
                el.setAttribute(KEY_UPPER_ERROR_VALUE_NAMES, value);
                value = SGDataUtility.bindVariableNamesInBracket(this.mErrorBarHolderVariables);
                el.setAttribute(KEY_ERROR_BAR_HOLDER_NAMES, value);
            }
            if (this.mTickLabelVariables != null) {
                value = SGDataUtility.bindVariableNamesInBracket(this.mTickLabelVariables);
                el.setAttribute(KEY_TICK_LABEL_NAMES, value);
                value = SGDataUtility.bindVariableNamesInBracket(this.mTickLabelHolderVariables);
                el.setAttribute(KEY_TICK_LABEL_HOLDER_NAMES, value);
            }
        }

        // stride
        if (!this.isIndexAvailable()) {
            el.setAttribute(KEY_ARRAY_SECTION, this.mStride.toString());
        }
        if (this.isTickLabelAvailable()) {
            el.setAttribute(KEY_TICK_LABEL_ARRAY_SECTION, this.mTickLabelStride.toString());
        }

        return true;
    }

    /**
     * Returns the value of coordinate variable at a given index which is specified
     *  as dimension.
     * @param index
     *         the array index
     * @return the value of coordinate variable at a given index.
     * null if file reading failure.
     * @throws IllegalArgumentException
     *  if the dimension variable is not a coordinate variable or index is illegal.
     */
    public Double getDimensionValue(final int index) {
    	return this.getDimensionValue(this.getDimensionName(), index);
    }

    public Double getDimensionValue(final String dimName, final int index) {
        try {
            double value = this.getCoordinateValue(dimName, index);
            return Double.valueOf(value);
        } catch (IOException e) {
            return null;
        }
    }

	public static SGISXYTypeMultipleData merge(final List<SGData> dataList) {
		if (dataList.size() == 0) {
			return null;
		}
		if (dataList.size() == 1) {
			SGData data = (SGData) dataList.get(0);
			if ((data instanceof SGSXYNetCDFMultipleData) == false) {
				return null;
			}
			return (SGISXYTypeMultipleData) data;
		}

		// checks the data source
		SGIDataSource src = null;
		for (SGData data : dataList) {
			SGIDataSource s = data.getDataSource();
			if (src == null) {
				src = s;
			} else {
				if (!src.equals(s)) {
					return null;
				}
			}
		}

		// checks the type
		Boolean dimensionPicked = null;
		for (SGData data : dataList) {
			if ((data instanceof SGSXYNetCDFMultipleData) == false) {
				return null;
			}
			SGSXYNetCDFMultipleData ncData = (SGSXYNetCDFMultipleData) data;
			final boolean b = ncData.isDimensionPicked();
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

		SGSXYNetCDFMultipleData dataLast = (SGSXYNetCDFMultipleData) dataList.get(dataList.size() - 1);
		SGNetCDFFile ncFile = dataLast.getNetcdfFile();
		SGDataSourceObserver obs = dataLast.getDataSourceObserver();

		if (dimensionPicked) {
			SGNetCDFVariable xVar = dataLast.getXVariable();
			SGNetCDFVariable yVar = dataLast.getYVariable();
			SGNetCDFVariable leVar = dataLast.getLowerErrorVariable();
			SGNetCDFVariable ueVar = dataLast.getUpperErrorVariable();
			SGNetCDFVariable ehVar = dataLast.getErrorBarHolderVariable();
			SGNetCDFVariable tlVar = dataLast.getTickLabelVariable();
			SGNetCDFVariable thVar = dataLast.getTickLabelHolderVariable();
			SGNetCDFVariable timeVar = dataLast.getTimeVariable();
			SGNetCDFVariable indexVar = dataLast.getIndexVariable();

			SGNetCDFDataColumnInfo xInfo = SGDataUtility.createDataColumnInfo(xVar, X_VALUE);
			SGNetCDFDataColumnInfo yInfo = SGDataUtility.createDataColumnInfo(yVar, Y_VALUE);
			SGNetCDFDataColumnInfo leInfo = (leVar != null) ? SGDataUtility
					.createErrorBarInfo(leVar, LOWER_ERROR_VALUE, leVar, ueVar, ehVar) : null;
			SGNetCDFDataColumnInfo ueInfo = (ueVar != null) ? SGDataUtility
					.createErrorBarInfo(ueVar, UPPER_ERROR_VALUE, leVar, ueVar, ehVar) : null;
			SGNetCDFDataColumnInfo ehInfo = (ehVar != null) ?
					(SGNetCDFDataColumnInfo) (ehVar.equals(xVar) ? xInfo : yInfo).clone() : null;
			SGNetCDFDataColumnInfo tlInfo = (tlVar != null) ? SGDataUtility.createDataColumnInfo(
					tlVar, TICK_LABEL, tlVar.getName()) : null;
			SGNetCDFDataColumnInfo thInfo = (thVar != null) ?
					(SGNetCDFDataColumnInfo) (thVar.equals(xVar) ? xInfo : yInfo).clone() : null;
			String dimName = dataLast.getDimensionName();
			SGNetCDFDataColumnInfo timeInfo = dataLast
					.isTimeVariableAvailable() ? SGDataUtility.createDataColumnInfo(timeVar, ANIMATION_FRAME) : null;
			SGNetCDFDataColumnInfo indexInfo = dataLast
					.isIndexAvailable() ? SGDataUtility.createDataColumnInfo(indexVar, INDEX) : null;

			// get dimension indices
			Dimension dim = dataLast.getDimension();
			final int dimLen = (null != dim) ? dim.getLength() : 0;
			SGIntegerSeriesSet indices = SGDataUtility.getDimensionSeries(dataList, dimLen);
			if (indices == null) {
				return null;
			}

			SGSXYNetCDFMultipleData data = new SGSXYNetCDFMultipleData(ncFile,
					obs, xInfo, yInfo, leInfo, ueInfo, ehInfo, tlInfo, thInfo,
					dimName, indices, timeInfo, indexInfo, dataLast.mStride,
					dataLast.mTickLabelStride, dataLast.isStrideAvailable());
			data.mOriginMap = new HashMap<String, Integer>(dataLast.mOriginMap);
			data.setDecimalPlaces(dataLast.getDecimalPlaces());
			data.setExponent(dataLast.getExponent());
			data.setTimeStride(dataLast.getTimeStride());
			return data;

		} else {

			List<SGNetCDFVariable> xList = new ArrayList<SGNetCDFVariable>();
			List<SGNetCDFVariable> yList = new ArrayList<SGNetCDFVariable>();
			List<SGNetCDFVariable> leList = new ArrayList<SGNetCDFVariable>();
			List<SGNetCDFVariable> ueList = new ArrayList<SGNetCDFVariable>();
			List<SGNetCDFVariable> ehList = new ArrayList<SGNetCDFVariable>();
			List<SGNetCDFVariable> tlList = new ArrayList<SGNetCDFVariable>();
			List<SGNetCDFVariable> thList = new ArrayList<SGNetCDFVariable>();
			List<SGNetCDFVariable> timeList = new ArrayList<SGNetCDFVariable>();
			List<SGNetCDFVariable> indexList = new ArrayList<SGNetCDFVariable>();
			for (SGData data : dataList) {
				SGSXYNetCDFMultipleData dataMult = (SGSXYNetCDFMultipleData) data;
				SGNetCDFVariable[] xVars = dataMult.getXVariables();
				SGNetCDFVariable[] yVars = dataMult.getYVariables();
				SGNetCDFVariable[] leVars = dataMult.getLowerErrorVariables();
				SGNetCDFVariable[] ueVars = dataMult.getUpperErrorVariables();
				SGNetCDFVariable[] ehVars = dataMult.getErrorHolderVariables();
				SGNetCDFVariable[] tlVars = dataMult.getTickLabelVariables();
				SGNetCDFVariable[] thVars = dataMult.getTickLabelHolderVariables();
				SGNetCDFVariable timeVar = dataMult.getTimeVariable();
				SGNetCDFVariable indexVar = dataMult.getIndexVariable();
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
				if (timeVar != null) {
					timeList.add(timeVar);
				}
				if (indexVar != null) {
					indexList.add(indexVar);
				}
			}

			// if index variables are selected, cannot merge the data
			if (indexList.size() > 0) {
				return null;
			}

			// selects the time variable
			SGNetCDFVariable timeVar = null;
			if (timeList.size() != 0) {
				timeVar = timeList.get(timeList.size() - 1);
			}

			// there must exist only one common coordinate variable
			Set<SGNetCDFVariable> xSet = new HashSet<SGNetCDFVariable>(xList);
			Set<SGNetCDFVariable> ySet = new HashSet<SGNetCDFVariable>(yList);
			Boolean bCVarX = null;
			Iterator<SGNetCDFVariable> xItr = xSet.iterator();
			while (xItr.hasNext()) {
				SGNetCDFVariable xVar = xItr.next();
				if (bCVarX == null) {
					bCVarX = xVar.isCoordinateVariable();
				} else {
					if (!bCVarX.equals(xVar.isCoordinateVariable())) {
						return null;
					}
				}
			}
			Boolean bCVarY = null;
			Iterator<SGNetCDFVariable> yItr = ySet.iterator();
			while (yItr.hasNext()) {
				SGNetCDFVariable yVar = yItr.next();
				if (bCVarY == null) {
					bCVarY = yVar.isCoordinateVariable();
				} else {
					if (!bCVarY.equals(yVar.isCoordinateVariable())) {
						return null;
					}
				}
			}
			if (bCVarX || bCVarY) {

				if (bCVarX && bCVarY) {
					// both of x and y variables must not coordinate variables
					return null;
				}
				if (bCVarX) {
					// only one coordinate variable can exist
					if (xSet.size() != 1) {
						return null;
					}
				}
				if (bCVarY) {
					// only one coordinate variable can exist
					if (ySet.size() != 1) {
						return null;
					}
				}
			}

			List<SGNetCDFVariable> xListNew = new ArrayList<SGNetCDFVariable>();
			for (SGNetCDFVariable var : xList) {
				if (!xListNew.contains(var)) {
					xListNew.add(var);
				}
			}
			List<SGNetCDFVariable> yListNew = new ArrayList<SGNetCDFVariable>();
			for (SGNetCDFVariable var : yList) {
				if (!yListNew.contains(var)) {
					yListNew.add(var);
				}
			}

			SGNetCDFVariable[] x = xListNew.toArray(new SGNetCDFVariable[xListNew.size()]);
			SGNetCDFDataColumnInfo[] xInfo = SGDataUtility.createDataColumnInfoArray(x, X_VALUE);

			SGNetCDFVariable[] y = yListNew.toArray(new SGNetCDFVariable[yListNew.size()]);
			SGNetCDFDataColumnInfo[] yInfo = SGDataUtility.createDataColumnInfoArray(y, Y_VALUE);

			SGNetCDFDataColumnInfo[] leInfo = new SGNetCDFDataColumnInfo[leList.size()];
			SGNetCDFDataColumnInfo[] ueInfo = new SGNetCDFDataColumnInfo[ueList.size()];
			SGNetCDFDataColumnInfo[] ehInfo = new SGNetCDFDataColumnInfo[ehList.size()];
			for (int ii = 0; ii < leInfo.length; ii++) {
				String leColumnType, ueColumnType, ehColumnType;
				SGNetCDFVariable leVar = leList.get(ii);
				SGNetCDFVariable ueVar = ueList.get(ii);
				SGNetCDFVariable ehVar = ehList.get(ii);
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

				leInfo[ii] = new SGNetCDFDataColumnInfo(leVar, null, leVar.getValueType());
				leInfo[ii].setColumnType(leColumnType);
				ueInfo[ii] = new SGNetCDFDataColumnInfo(ueVar, null, ueVar.getValueType());
				ueInfo[ii].setColumnType(ueColumnType);
				ehInfo[ii] = new SGNetCDFDataColumnInfo(ehVar, null, ehVar.getValueType());
				ehInfo[ii].setColumnType(ehColumnType);
			}

			SGNetCDFDataColumnInfo[] tlInfo = new SGNetCDFDataColumnInfo[tlList.size()];
			SGNetCDFDataColumnInfo[] thInfo = new SGNetCDFDataColumnInfo[thList.size()];
			for (int ii = 0; ii < tlInfo.length; ii++) {
				String tlColumnType, thColumnType;
				SGNetCDFVariable tlVar = tlList.get(ii);
				SGNetCDFVariable thVar = thList.get(ii);
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

				tlInfo[ii] = new SGNetCDFDataColumnInfo(tlVar, null, tlVar.getValueType());
				tlInfo[ii].setColumnType(tlColumnType);
				thInfo[ii] = new SGNetCDFDataColumnInfo(thVar, null, thVar.getValueType());
				thInfo[ii].setColumnType(thColumnType);
			}

			SGSXYNetCDFMultipleData data = new SGSXYNetCDFMultipleData(ncFile,
					obs, xInfo, yInfo, leInfo, ueInfo, ehInfo, tlInfo, thInfo,
					SGDataUtility.createDataColumnInfo(timeVar, ANIMATION_FRAME), null,
					dataLast.mStride,
					dataLast.mTickLabelStride, dataLast.isStrideAvailable());
			data.mOriginMap = new HashMap<String, Integer>(dataLast.mOriginMap);
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
    	if (this.isIndexAvailable()) {
    		return null;
    	} else {
    		if (this.mStride != null) {
            	return (SGIntegerSeriesSet) this.mStride.clone();
    		} else {
    			return null;
    		}
    	}
    }

    private SGIntegerSeriesSet getStrideInstance() {
    	return this.getStrideInstance(this.mStride, this.getCoordinateVariable());
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
    	}
    }

    /**
     * Returns the copy of this data object.
     *
     * @return
     *         a copy of this data object
     */
    public Object clone() {
        SGSXYNetCDFMultipleData data = (SGSXYNetCDFMultipleData) super.clone();
        data.mXVariables = copyVariables(this.mXVariables);
        data.mYVariables = copyVariables(this.mYVariables);
        data.mLowerErrorVariables = copyVariables(this.mLowerErrorVariables);
        data.mUpperErrorVariables = copyVariables(this.mUpperErrorVariables);
        data.mErrorBarHolderVariables = copyVariables(this.mErrorBarHolderVariables);
        data.mTickLabelVariables = copyVariables(this.mTickLabelVariables);
        data.mTickLabelHolderVariables = copyVariables(this.mTickLabelHolderVariables);
        data.mPickUpDimensionInfo = (SGNetCDFPickUpDimensionInfo) this.getPickUpDimensionInfo();
        data.mStride = this.getStride();
        data.mTickLabelStride = this.getTickLabelStride();
        data.mShift = this.getShift();
        return data;
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
        	map.put(SGIDataInformationKeyConstants.KEY_SXY_STRIDE, this.getStride());
    	} else {
        	map.put(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE, this.getIndexStride());
    	}
    	map.put(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE, this.mTickLabelStride);
    	return map;
    }

    /**
     * Returns a map of stride of dimensions. The keys are the name of dimensions.
     *
     * @return a map of stride of dimensions
     */
    @Override
    protected Map<String, SGIntegerSeriesSet> getDimensionStrideMap() {
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    	if (!this.isIndexAvailable()) {
        	map.put(this.getCoordinateVariable().getName(), this.getStrideInstance());
    	} else {
        	map.put(this.mIndexVariable.getName(), this.getIndexStrideInstance());
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
    	this.mIndexStride = map.get(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
    	this.mStride = map.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
    }

	@Override
	protected double[] arrangeValueArray(SGNetCDFVariable var, List<SGNamedDoubleValueIndexBlock> blockList) {
		int len = 0;
		for (int ii = 0; ii < blockList.size(); ii++) {
			SGNamedDoubleValueIndexBlock block = blockList.get(ii);
			len += block.getLength();
		}
		double[] ret = new double[len];
		int cnt = 0;
		for (int ii = 0; ii < blockList.size(); ii++) {
			SGNamedDoubleValueIndexBlock block = blockList.get(ii);
			double[] values = block.getValues();
			for (int jj = 0; jj < values.length; jj++) {
				ret[cnt] = values[jj];
				cnt++;
			}
		}
		return ret;
	}

	@Override
	protected String[] arrangeStringArray(List<SGNamedStringBlock> blockList) {
		int len = 0;
		for (int ii = 0; ii < blockList.size(); ii++) {
			SGNamedStringBlock block = blockList.get(ii);
			len += block.getLength();
		}
		String[] ret = new String[len];
		int cnt = 0;
		for (int ii = 0; ii < blockList.size(); ii++) {
			SGNamedStringBlock block = blockList.get(ii);
			String[] values = block.getValues();
			for (int jj = 0; jj < values.length; jj++) {
				ret[cnt] = values[jj];
				cnt++;
			}
		}
		return ret;
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
     * Returns the number of data points without taking into account the stride.
     *
     * @return the number of data points without taking into account the stride
     */
    @Override
    public int getAllPointsNumber() {
    	return this.getCoordinateVariable().getDimension(0).getLength();
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
			nameList = getChildNameList(this, this.mPickUpDimensionInfo);
		} else {
			nameList = getChildNameList(this.getColumnInfo());
		}
		return nameList;
	}
	
	/**
	 * Returns the list of the name of child objects with given pick up information for given data.
	 * 
	 * @param data
	 *           a data
	 * @param pickUpInfo
	 *           pick up information
	 * @return the list of the name of child objects with given pick up information
	 */
	public static List<String> getChildNameList(SGSXYNetCDFMultipleData data,
			SGNetCDFPickUpDimensionInfo pickUpInfo) {
		List<String> nameList = new ArrayList<String>();
    	String dimName = pickUpInfo.getDimensionName();
    	SGIntegerSeriesSet indices = pickUpInfo.getIndices();
    	int[] indexArray = indices.getNumbers();
    	for (int ii = 0; ii < indexArray.length; ii++) {
            Double dimValue = data.getDimensionValue(dimName, indexArray[ii]);
    		StringBuffer sb = new StringBuffer();
    		sb.append(dimName);
    		sb.append('[');
    		sb.append(indexArray[ii]);
    		sb.append("]=");
			sb.append(dimValue);
    		nameList.add(sb.toString());
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
	
	@Override
	protected boolean exportToFile(NetcdfFileWriteable ncWrite, 
			final SGExportParameter mode, SGDataBufferPolicy policy)
			throws IOException, InvalidRangeException {
	
		if (!(policy instanceof SGSXYDataBufferPolicy)) {
			throw new IllegalArgumentException("!(policy instanceof SGSXYDataBufferPolicy)");
		}
		
		// gets data buffer
		SGSXYMultipleDataBuffer buffer = (SGSXYMultipleDataBuffer) this.getDataBuffer(policy);
		final int len = buffer.getLength();
		final int multiplicity = buffer.getMultiplicity();
		final Boolean dateFlag = buffer.getDateFlag();
		SGDate[] dateArray = buffer.getDateArray();
		double[][] xValues = buffer.getXValues();
		double[][] yValues = buffer.getYValues();
		final double[][] lowerErrorValues = buffer.getLowerErrorValues();
		final double[][] upperErrorValues = buffer.getUpperErrorValues();
		final Boolean[] sameErrorVariableFlags = buffer.hasSameErrorVariable();
		final String[][] tickLabels = buffer.getTickLabels();
		
		final boolean yMultiple = this.hasMultipleYValues();
		final boolean idx = this.isIndexAvailable();
		final boolean pickedUp = this.isDimensionPicked();
		final boolean errorBarAvailable = this.isErrorBarAvailable();
		final boolean tickLabelAvailable = this.isTickLabelAvailable();
		
		SGNetCDFVariable[] lowerErrorVars = this.getErrorBarTickLabelVariables(
				this.mLowerErrorVariables, this.mErrorBarHolderVariables, errorBarAvailable);
		SGNetCDFVariable[] upperErrorVars = this.getErrorBarTickLabelVariables(
				this.mUpperErrorVariables, this.mErrorBarHolderVariables, errorBarAvailable);
		SGNetCDFVariable[] tickLabelVars = this.getErrorBarTickLabelVariables(
				this.mTickLabelVariables, this.mTickLabelHolderVariables, tickLabelAvailable);

		//
		// adds dimensions
		//

		// index dimension
		String indexDimName = null;
		String validIndexDimName = null;
		if (idx) {
			indexDimName = this.mIndexVariable.getName();
			validIndexDimName = this.getValidName(indexDimName);
			this.addDimension(ncWrite, validIndexDimName, len);
		}

		// pick up dimension
		String pickUpDimName = null;
		String validPickUpDimName = null;
		if (pickedUp) {
			pickUpDimName = this.mPickUpDimensionInfo.getDimensionName();
			validPickUpDimName = this.getValidName(pickUpDimName);
			this.addDimension(ncWrite, validPickUpDimName, multiplicity);
		}

		// x or y dimension
		String cDimName = null;
		String validCDimName = null;
		if (!idx) {
			SGNetCDFVariable cVar = yMultiple ? this.mXVariables[0] : this.mYVariables[0];
			cDimName = cVar.getName();
			validCDimName = this.getValidName(cDimName);
			this.addDimension(ncWrite, validCDimName, len);
		}
		
		// dimension of string length of tick labels
		String[] lenDimNames = null;
		String[][] tlStrArray = null;
		if (tickLabelAvailable && dateFlag == null) {
			final int tNum;
			if (pickedUp) {
				tNum = 1;
			} else {
				tNum = multiplicity;
			}
			lenDimNames = new String[tNum];
			tlStrArray = new String[multiplicity][];
			this.addTickLabelDimension(ncWrite, tNum, tickLabelVars, tickLabels, lenDimNames, tlStrArray);
		}
		
		// dimension of string length of date
		SGDateVariable dVar = null;
		String dateDimName = null;
		String validDateDimName = null;
		String[] dateStrArray = null;
		String dateLenDimName = null;
		String validDateLenDimName = null;
		if (dateFlag != null) {
			dVar = this.getDateVariable();
			dateDimName = dVar.getName();
			validDateDimName = this.getValidName(dateDimName);
			Dimension strLengthDim = dVar.getLengthDimension();
			dateLenDimName = strLengthDim.getName();
			validDateLenDimName = this.getValidName(dateLenDimName);
			dateStrArray = new String[dateArray.length];
			for (int ii = 0; ii < dateArray.length; ii++) {
				dateStrArray[ii] = dateArray[ii].toString();
			}
			final int maxLength = this.getMaxLength(dateStrArray);
			Dimension dim = new Dimension(validDateLenDimName, maxLength);
			ncWrite.addDimension(null, dim);
		}

		//
		// adds variables
		//
		
		// coordinate variable for index dimension
		DataType idxDataType = null;
		Variable indexVar = null;
		if (idx) {
			SGNetCDFVariable idxVar = this.findVariable(indexDimName);
			idxDataType = this.getExportNumberDataType(idxVar, mode, policy);
			String dimString = SGDataUtility.getDimensionString(new String[] { validIndexDimName });
			indexVar = this.addVariable(ncWrite, mode, indexDimName, validIndexDimName, 
					idxDataType, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, dimString, policy);
		}
		
		// coordinate variable for picked up dimension
		DataType pDataType = null;
		Variable pickUpVar = null;
		if (pickedUp) {
			SGNetCDFVariable pVar = this.findVariable(pickUpDimName);
			pDataType = this.getExportNumberDataType(pVar, mode, policy);
			pickUpVar = this.addVariable(ncWrite, mode, pickUpDimName, validPickUpDimName, pDataType, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, validPickUpDimName, policy);
		}
		
		// variables for x and y values
		String[] xDimNameArray = null;
		String[] yDimNameArray = null;
		if (idx) {
			if (pickedUp) {
				xDimNameArray = new String[] { validPickUpDimName, validIndexDimName };
				yDimNameArray = xDimNameArray.clone();
			} else {
				xDimNameArray = new String[] { validIndexDimName };
				yDimNameArray = xDimNameArray.clone();
			}
		} else {
			if (pickedUp) {
				if (yMultiple) {
					xDimNameArray = new String[] { validCDimName };
					yDimNameArray = new String[] { validPickUpDimName, validCDimName };
				} else {
					xDimNameArray = new String[] { validPickUpDimName, validCDimName };
					yDimNameArray = new String[] { validCDimName };
				}
			} else {
				xDimNameArray = new String[] { validCDimName };
				yDimNameArray = xDimNameArray.clone();
			}
		}
		if (dateFlag != null) {
			if (dateFlag) {
				String[] tempArray = new String[xDimNameArray.length + 1];
				for (int ii = 0; ii < xDimNameArray.length; ii++) {
					tempArray[ii] = xDimNameArray[ii];
				}
				tempArray[tempArray.length - 1] = validDateLenDimName;
				xDimNameArray = tempArray;
			} else {
				String[] tempArray = new String[yDimNameArray.length + 1];
				for (int ii = 0; ii < yDimNameArray.length; ii++) {
					tempArray[ii] = yDimNameArray[ii];
				}
				tempArray[tempArray.length - 1] = validDateLenDimName;
				yDimNameArray = tempArray;
			}
		}
		String xDimString = SGDataUtility.getDimensionString(xDimNameArray);
		String yDimString = SGDataUtility.getDimensionString(yDimNameArray);
		
		Variable[] xVars = null;
		Variable[] yVars = null;
		DataType[] xDataTypes = null;
		DataType[] yDataTypes = null;
		final int xNum;
		final int yNum;
		if (idx) {
			xNum = this.mXVariables.length;
			yNum = this.mYVariables.length;
			xVars = new Variable[xNum];
			xDataTypes = new DataType[xNum];
			String xNumberType = (dateFlag != null && dateFlag) ? SGIDataColumnTypeConstants.VALUE_TYPE_DATE : SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER;
			for (int ii = 0; ii < xNum; ii++) {
				xDataTypes[ii] = this.getExportNumberDataType(this.mXVariables[ii], mode, policy);
				String xName = this.mXVariables[ii].getName();
				xVars[ii] = this.addVariable(ncWrite, mode, xName, this.getValidName(xName),
						xDataTypes[ii], xNumberType, xDimString, policy);
			}
			yVars = new Variable[yNum];
			yDataTypes = new DataType[yNum];
			String yNumberType = (dateFlag != null && !dateFlag) ? SGIDataColumnTypeConstants.VALUE_TYPE_DATE : SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER;
			for (int ii = 0; ii < yNum; ii++) {
				yDataTypes[ii] = this.getExportNumberDataType(this.mYVariables[ii], mode, policy);
				String yName = this.mYVariables[ii].getName();
				yVars[ii] = this.addVariable(ncWrite, mode, yName, this.getValidName(yName),
						yDataTypes[ii], yNumberType, yDimString, policy);
			}
		} else {
			if (dateFlag != null) {
				String dimString = SGDataUtility.getDimensionString(new String[] { validDateDimName, validDateLenDimName });
				this.addVariable(ncWrite, mode, dVar.getName(), validDateDimName, DataType.CHAR, 
						SGIDataColumnTypeConstants.VALUE_TYPE_DATE, dimString, policy);
			}
			if (pickedUp) {
				xNum = 1;
				yNum = 1;
			} else {
				xNum = yMultiple ? 1 : multiplicity;
				yNum = yMultiple ? multiplicity : 1;
			}
			xVars = new Variable[xNum];
			xDataTypes = new DataType[xNum];
			yVars = new Variable[yNum];
			yDataTypes = new DataType[yNum];
			if (dateFlag == null || !dateFlag.booleanValue()) {
				for (int ii = 0; ii < xNum; ii++) {
					xDataTypes[ii] = this.getExportNumberDataType(this.mXVariables[ii], mode, policy);
					String xName = this.mXVariables[ii].getName();
					xVars[ii] = this.addVariable(ncWrite, mode, xName, this.getValidName(xName),
							xDataTypes[ii], SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, xDimString, policy);
				}
			}
			if (dateFlag == null || dateFlag.booleanValue()) {
				for (int ii = 0; ii < yNum; ii++) {
					yDataTypes[ii] = this.getExportNumberDataType(this.mYVariables[ii], mode, policy);
					String yName = this.mYVariables[ii].getName();
					yVars[ii] = this.addVariable(ncWrite, mode, yName, this.getValidName(yName),
							yDataTypes[ii], SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, yDimString, policy);
				}
			}
		}

		// error bars and tick labels
		String[] dimNames;
		if (idx) {
			if (pickedUp) {
				dimNames = new String[] { validPickUpDimName, validIndexDimName };
			} else {
				dimNames = new String[] { validIndexDimName };
			}
		} else {
			if (pickedUp) {
				dimNames = new String[] { validPickUpDimName, validCDimName };
			} else {
				dimNames = new String[] { validCDimName };
			}
		}
		DataType[] leDataTypes = null;
		Variable[] leVars = null;
		DataType[] ueDataTypes = null;
		Variable[] ueVars = null;
		DataType[] tlDataTypes = null;
		Variable[] tlVars = null;
		if (errorBarAvailable) {
			final int eNum;
			if (pickedUp) {
				eNum = 1;
			} else {
				eNum = multiplicity;
			}
			leVars = new Variable[eNum];
			ueVars = new Variable[eNum];
			leDataTypes = new DataType[eNum];
			ueDataTypes = new DataType[eNum];
			this.addErrorVariables(ncWrite, eNum, lowerErrorVars, upperErrorVars, mode, policy, 
					sameErrorVariableFlags, dimNames, leVars, ueVars, leDataTypes, ueDataTypes);
		}
		if (tickLabelAvailable && dateFlag == null) {
			final int tNum;
			if (pickedUp) {
				tNum = 1;
			} else {
				tNum = multiplicity;
			}
			tlVars = new Variable[tNum];
			tlDataTypes = new DataType[tNum];
			this.addTickLabelVariables(ncWrite, tNum, tickLabelVars, mode, policy, dimNames, lenDimNames, 
					tlVars, tlDataTypes);
		}

		// creates the file
		ncWrite.create();
		
		//
		// writes data to the file
		//
		
		// coordinate variable for index dimension
		if (this.isIndexAvailable()) {
			SGNetCDFVariable idxVar = this.findVariable(indexDimName);
        	int[] idxUpIndices = this.mIndexStride.getNumbers();
        	double[] idxValueArrayAll = idxVar.getNumberArray();
        	double[] idxValueArray = new double[idxUpIndices.length];
        	for (int ii = 0; ii < idxValueArray.length; ii++) {
        		idxValueArray[ii] = idxValueArrayAll[idxUpIndices[ii]];
        	}
    		Array idxArray = Array.factory(idxDataType, new int[] { idxUpIndices.length });
    		this.setArray(idxArray, idxValueArray);
    		this.writeValues(ncWrite, indexVar, idxArray);
		}

		// coordinate variable for picked up dimension
		if (pickedUp) {
			SGNetCDFVariable pVar = this.findVariable(pickUpDimName);
        	int[] pickUpIndices = this.mPickUpDimensionInfo.getIndices().getNumbers();
        	double[] pValueArrayAll = pVar.getNumberArray();
        	double[] pValueArray = new double[pickUpIndices.length];
        	for (int ii = 0; ii < pValueArray.length; ii++) {
        		pValueArray[ii] = pValueArrayAll[pickUpIndices[ii]];
        	}
    		Array pickUpArray = Array.factory(pDataType, new int[] { multiplicity });
    		this.setArray(pickUpArray, pValueArray);
    		this.writeValues(ncWrite, pickUpVar, pickUpArray);
		}

		// date variable
		if (dateFlag != null) {
			this.writeCharValue(ncWrite, validDateDimName, validDateLenDimName, dateStrArray);
		}
		
		// variables for x and y values
		if (pickedUp) {
			if (idx) {
				int[] shape = new int[] { multiplicity, len };
				if (dateFlag == null || !dateFlag) {
					this.writeValues(ncWrite, xVars[0], xDataTypes[0], xValues, shape);
				}
				if (dateFlag == null || dateFlag) {
					this.writeValues(ncWrite, yVars[0], yDataTypes[0], yValues, shape);
				}
			} else {
				Variable sVar, mVar;
				DataType sDataType, mDataType;
				double[] sValues;
				double[][] mValues;
				int[] sShape = new int[] { len };
				int[] mShape = new int[] { multiplicity, len };
				if (yMultiple) {
					sVar = xVars[0];
					mVar = yVars[0];
					sDataType = xDataTypes[0];
					mDataType = yDataTypes[0];
					sValues = xValues[0];
					mValues = yValues;
				} else {
					sVar = yVars[0];
					mVar = xVars[0];
					sDataType = yDataTypes[0];
					mDataType = xDataTypes[0];
					sValues = yValues[0];
					mValues = xValues;
				}
				if (dateFlag == null || !dateFlag) {
					this.writeValues(ncWrite, sVar, sDataType, sValues, sShape);
				}
				if (dateFlag == null || dateFlag) {
					this.writeValues(ncWrite, mVar, mDataType, mValues, mShape);
				}
			}
		} else {
			int[] shape = new int[] { len };
			if (dateFlag == null || !dateFlag) {
				for (int ii = 0; ii < xVars.length; ii++) {
					this.writeValues(ncWrite, xVars[ii], xDataTypes[ii], xValues[ii], shape);
				}
			}
			if (dateFlag == null || dateFlag) {
				for (int ii = 0; ii < yVars.length; ii++) {
					this.writeValues(ncWrite, yVars[ii], yDataTypes[ii], yValues[ii], shape);
				}
			}
		}
		
		// error bar variable
        if (errorBarAvailable) {
        	final int[] errorShape;
        	if (pickedUp) {
//        		this.setFillValue(leVars[0], lowerErrorValues);
        		Array lowerErrorArray = Array.factory(leDataTypes[0], new int[] { multiplicity, len });
        		this.setArray(lowerErrorArray, lowerErrorValues);
        		this.writeValues(ncWrite, leVars[0], lowerErrorArray);
        		if (!sameErrorVariableFlags[0]) {
//            		this.setFillValue(ueVars[0], upperErrorValues);
            		Array upperErrorArray = Array.factory(ueDataTypes[0], new int[] { multiplicity, len });
            		this.setArray(upperErrorArray, upperErrorValues);
            		this.writeValues(ncWrite, ueVars[0], upperErrorArray);
        		}
        	} else {
        		errorShape = new int[] { len };
                for (int ii = 0; ii < lowerErrorValues.length; ii++) {
                	if (leVars[ii] != null) {
//                		this.setFillValue(leVars[ii], lowerErrorValues);
                		Array lowerErrorArray = Array.factory(leDataTypes[ii], errorShape);
                		this.setArray(lowerErrorArray, lowerErrorValues[ii]);
                		this.writeValues(ncWrite, leVars[ii], lowerErrorArray);
                	}
                	if (ueVars[ii] != null) {
//                		this.setFillValue(ueVars[ii], upperErrorValues);
                		Array upperErrorArray = Array.factory(ueDataTypes[ii], errorShape);
                		this.setArray(upperErrorArray, upperErrorValues[ii]);
                		this.writeValues(ncWrite, ueVars[ii], upperErrorArray);
                	}
                }
        	}
        }
        
		// tick label variable
        if (tickLabelAvailable && dateFlag == null) {
        	if (pickedUp) {
                this.writeCharValue(ncWrite, tlVars[0].getShortName(), lenDimNames[0], tlStrArray);
        	} else {
                for (int ii = 0; ii < tickLabels.length; ii++) {
                	if (tlVars[ii] != null) {
                		this.writeCharValue(ncWrite, tlVars[ii].getShortName(), lenDimNames[ii], tlStrArray[ii]);
                	}
                }
        	}
        }

        return true;
	}
	
	private void writeValues(NetcdfFileWriteable ncWrite, Variable var, DataType dataType, 
			double[] values, int[] shape) throws IOException, InvalidRangeException {
//		this.setFillValue(var, values);
		Array array = Array.factory(dataType, shape);
		this.setArray(array, values);
		this.writeValues(ncWrite, var, array);
	}

	private void writeValues(NetcdfFileWriteable ncWrite, Variable var, DataType dataType, 
			double[][] values, int[] shape) throws IOException, InvalidRangeException {
//		this.setFillValue(var, values);
		Array array = Array.factory(dataType, shape);
		this.setArray(array, values);
		this.writeValues(ncWrite, var, array);
	}

	private SGNetCDFVariable[] getErrorBarTickLabelVariables(SGNetCDFVariable[] vars,
			SGNetCDFVariable[] holderVars, final boolean available) {
		SGNetCDFVariable[] ret = null;
		if (!available) {
			return null;
		}
		final Boolean yHolder = this.isYValuesHolder();
		if (this.isDimensionPicked()) {
			ret = new SGNetCDFVariable[] { vars[0] };
		} else {
			SGNetCDFVariable[] xyVars = yHolder.booleanValue() ? this.mYVariables : this.mXVariables;
			ret = new SGNetCDFVariable[xyVars.length];
			for (int ii = 0; ii < xyVars.length; ii++) {
				for (int jj = 0; jj < holderVars.length; jj++) {
					if (xyVars[ii].equals(holderVars[jj])) {
						ret[ii] = vars[jj];
						break;
					}
				}
			}
		}
		return ret;
	}
	
	private void addErrorVariables(NetcdfFileWriteable ncWrite, final int num, 
			SGNetCDFVariable[] lowerErrorVars, SGNetCDFVariable[] upperErrorVars, SGExportParameter mode, 
			SGDataBufferPolicy policy, Boolean[] sameErrorVariableFlags, String[] dimNames,
			Variable[] leVars, Variable[] ueVars, DataType[] leDataTypes, DataType[] ueDataTypes) {
		String dimString = SGDataUtility.getDimensionString(dimNames);
		for (int ii = 0; ii < num; ii++) {
			SGNetCDFVariable lVar = lowerErrorVars[ii];
			SGNetCDFVariable uVar = upperErrorVars[ii];
			if (lVar != null && uVar != null) {
				leDataTypes[ii] = this.getExportNumberDataType(lVar, mode, policy);
				String lName = lVar.getName();
				leVars[ii] = this.addVariable(ncWrite, mode, lName, this.getValidName(lName), 
						leDataTypes[ii], SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, dimString, policy);
				if (sameErrorVariableFlags[ii]) {
					ueDataTypes[ii] = leDataTypes[ii];
					ueVars[ii] = leVars[ii];
				} else {
					ueDataTypes[ii] = this.getExportNumberDataType(uVar, mode, policy);
					String uName = uVar.getName();
					ueVars[ii] = this.addVariable(ncWrite, mode, uName, this.getValidName(uName),
							ueDataTypes[ii], SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, dimString, policy);
				}
			} else {
				leDataTypes[ii] = null;
				ueDataTypes[ii] = null;
			}
		}
	}
	
	private void addTickLabelVariables(NetcdfFileWriteable ncWrite, final int num, 
			SGNetCDFVariable[] tickLabelVars, SGExportParameter mode, 
			SGDataBufferPolicy policy, String[] dimNames, String[] lenDimNames, Variable[] tlVars, 
			DataType[] tlDataTypes) {
		for (int ii = 0; ii < num; ii++) {
			if (tickLabelVars[ii] != null) {
				List<String> tlDimNameList = new ArrayList<String>();
				for (String dimName : dimNames) {
					tlDimNameList.add(dimName);
				}
				if (lenDimNames[ii] != null) {
					tlDimNameList.add(lenDimNames[ii]);
				}
				String[] tlDimNames = tlDimNameList.toArray(new String[tlDimNameList.size()]);
				String dimString = SGDataUtility.getDimensionString(tlDimNames);
				tlDataTypes[ii] = DataType.CHAR;
				String tName = tickLabelVars[ii].getName();
				tlVars[ii] = this.addVariable(ncWrite, mode, tName, this.getValidName(tName), 
						tlDataTypes[ii], SGIDataColumnTypeConstants.VALUE_TYPE_TEXT, dimString, policy);
			} else {
				tlDataTypes[ii] = DataType.OPAQUE;
			}
		}
	}
	
	private void addTickLabelDimension(NetcdfFileWriteable ncWrite, final int num, 
			SGNetCDFVariable[] tickLabelVars, String[][] tickLabels,
			String[] lenDimNames, String[][] tlStrArray) throws IOException {
		
		if (this.isDimensionPicked()) {
			SGNetCDFVariable var = tickLabelVars[0];
			if (var != null) {
				String lenDimName = var.getName() + "_strlen";
				int maxLength = 0;
				for (int ii = 0; ii < tickLabels.length; ii++) {
					final int len = this.getMaxLength(tickLabels[ii]);
					if (len > maxLength) {
						maxLength = len;
					}
				}
				Dimension dim = new Dimension(lenDimName, maxLength);
				ncWrite.addDimension(null, dim);
				lenDimNames[0] = lenDimName;
				for (int ii = 0; ii < tlStrArray.length; ii++) {
					tlStrArray[ii] = tickLabels[ii];
				}
			}
		} else {
			for (int ii = 0; ii < num; ii++) {
				if (tickLabelVars[ii] != null) {
					SGNetCDFVariable var = tickLabelVars[ii];
					String[] strArray = null;
					if (var instanceof SGCharVariable) {
						SGCharVariable cVar = (SGCharVariable) var;
						Dimension strLengthDim = cVar.getLengthDimension();
						String oldLenDimName = strLengthDim.getName();
						lenDimNames[ii] = this.getValidName(oldLenDimName);
						SGIStringModifier mod = cVar.getModifier();
						if (tickLabels[ii] != null) {
							if (mod != null) {
								strArray = new String[tickLabels[ii].length];
								for (int jj = 0; jj < strArray.length; jj++) {
									strArray[jj] = mod.modify(tickLabels[ii][jj]);
								}
							} else {
								strArray = SGUtility.copyStringArray(tickLabels[ii]);
							}
						}
					} else if (var instanceof SGVariable) {
						lenDimNames[ii] = this.getValidName(var.getName() + "_strlen");
						strArray = tickLabels[ii];
					} else {
						throw new Error("Unsupported type variable: " + var.getName());
					}
					final int maxLength = this.getMaxLength(strArray);
					tlStrArray[ii] = strArray;
					Dimension dim = new Dimension(lenDimNames[ii], maxLength);
					ncWrite.addDimension(null, dim);
				}
			}
		}
	}
	
	private int getMaxLength(String[] strArray) throws UnsupportedEncodingException {
		int maxLength = 0;
		for (String str : strArray) {
			final int len = str.getBytes(CHAR_SET_NAME_UTF8).length;
			if (len > maxLength) {
				maxLength = len;
			}
		}
		return maxLength;
	}
	
	private void writeCharValue(NetcdfFileWriteable ncWrite, String varName, String lenDimName, 
			String[] strArray) throws IOException, InvalidRangeException {
		Dimension strLengthDim = ncWrite.findDimension(lenDimName);
        ArrayByte array = new ArrayByte(new int[] { strArray.length, strLengthDim.getLength() });
		Index index = array.getIndex();
		int[] shape = index.getShape();
        for (int ii = 0; ii < shape[0]; ii++) {
			String str = strArray[ii];
        	byte[] byteArray = str.getBytes(CHAR_SET_NAME_UTF8);
        	for (int jj = 0; jj < byteArray.length; jj++) {
        		array.setByte(index.set(ii, jj), byteArray[jj]);
        	}
        }
		ncWrite.write(varName, array);
	}

	private void writeCharValue(NetcdfFileWriteable ncWrite, String varName, String lenDimName, 
			String[][] strArray) throws IOException, InvalidRangeException {
		Dimension strLengthDim = ncWrite.findDimension(lenDimName);
        ArrayByte array = new ArrayByte(new int[] { strArray.length, strArray[0].length, 
        		strLengthDim.getLength() });
		Index index = array.getIndex();
		int[] shape = index.getShape();
        for (int ii = 0; ii < shape[0]; ii++) {
        	for (int jj = 0; jj < shape[1]; jj++) {
    			String str = strArray[ii][jj];
            	byte[] byteArray = str.getBytes(CHAR_SET_NAME_UTF8);
            	for (int kk = 0; kk < byteArray.length; kk++) {
            		array.setByte(index.set(ii, jj, kk), byteArray[kk]);
            	}
        	}
        }
		ncWrite.write(varName, array);
	}

	/**
	 * Overrode to add the attributes taking into account the shift values.
	 * 
	 */
	@Override
	protected void addFillValueAttributes(SGNetCDFVariable curVar, Variable var,
			SGDataBufferPolicy policy) {
		SGSXYDataBufferPolicy sxyPolicy = (SGSXYDataBufferPolicy) policy;
		if (sxyPolicy.isShiftValuesContained()) {
			Number fillValue = curVar.getFillValue();
			if (fillValue != null) {
				this.addAttribute(var, ATTR_FILL_VALUE, Double.NaN);
			}
			Number missingValue = curVar.getMissingValue();
			if (missingValue != null) {
				this.addAttribute(var, ATTR_MISSING_VALUE, Double.NaN);
			}
		} else {
			super.addFillValueAttributes(curVar, var, policy);
		}
	}
	
	/**
	 * Overrode to get the data type taking into account the shift values.
	 * 
	 */
	@Override
    protected DataType getExportNumberDataType(SGNetCDFVariable var, SGExportParameter mode,
    		SGDataBufferPolicy policy) {
    	if (SGDataUtility.isArchiveDataSetOperation(mode.getType())) {
    		return var.getDataType();
    	} else {
    		SGSXYDataBufferPolicy sxyPolicy = (SGSXYDataBufferPolicy) policy;
    		if (sxyPolicy.isShiftValuesContained()) {
        		return DataType.DOUBLE;
    		} else {
        		return var.getDataType();
    		}
    	}
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

	@Override
    public Boolean getDateFlag() {
		if (this.mXVariables[0] instanceof SGDateVariable) {
    		return true;
		} else if (this.mYVariables[0] instanceof SGDateVariable) {
			return false;
    	} else {
    		return null;
    	}
    }

	private SGDateVariable getDateVariable() {
		if (this.mXVariables[0] instanceof SGDateVariable) {
			return (SGDateVariable) this.mXVariables[0];
		}
		if (this.mYVariables[0] instanceof SGDateVariable) {
			return (SGDateVariable) this.mYVariables[0];
		}
		return null;
	}

	@Override
    public boolean hasGenericTickLabels() {
		if (this.mTickLabelVariables == null) {
			return false;
		}
		return !SGUtility.contains(this.mTickLabelVariables, this.getDateVariable());
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
			if (this.isIndexAvailable()) {
				ret = this.mIndexStride;
			} else {
				ret = this.mStride;
			}
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
		if (this.getDateFlag() != null) {
			SGDateVariable dateVar = this.getDateVariable();
			return dateVar.getDate();
		} else {
			return null;
		}
	}

	@Override
	public Boolean isYValuesHolder() {
		if (this.isErrorBarAvailable()) {
			return SGUtility.contains(this.mYVariables, this.mErrorBarHolderVariables);
		} else if (this.isTickLabelAvailable()) {
			if (this.hasGenericTickLabels()) {
				return SGUtility.contains(this.mYVariables, this.mTickLabelHolderVariables);
			} else {
				SGDateVariable dateVar = this.getDateVariable();
				return SGUtility.contains(this.mXVariables, dateVar);
			}
		} else {
			return null;
		}
	}

	@Override
    public boolean hasOneSidedMultipleValues() {
		return !this.isIndexAvailable();
	}

	@Override
    protected void getVarNameColumnTypeList(List<String> varList, 
    		List<String> columnTypeList) {
    	
    	super.getVarNameColumnTypeList(varList, columnTypeList);
    	
		if (this.isDimensionPicked()) {
			String str = this.mPickUpDimensionInfo.getDimensionName();
			varList.add(str);
			columnTypeList.add(PICKUP);
		}
		for (int ii = 0; ii < this.mXVariables.length; ii++) {
			String str = this.mXVariables[ii].getName();
			varList.add(str);
			columnTypeList.add(X_VALUE);
		}
		for (int ii = 0; ii < this.mYVariables.length; ii++) {
			String str = this.mYVariables[ii].getName();
			varList.add(str);
			columnTypeList.add(Y_VALUE);
		}
		if (this.isErrorBarAvailable()) {
			final int num = this.mLowerErrorVariables.length;
			for (int ii = 0; ii < num; ii++) {
				if (this.mLowerErrorVariables[ii] == null) {
					continue;
				}
				final String leName = this.mLowerErrorVariables[ii].getName();
				final String ueName = this.mUpperErrorVariables[ii].getName();
				final String ehName = this.mErrorBarHolderVariables[ii].getName();
				final boolean equalFlag = leName.equals(ueName);
				if (equalFlag) {
					varList.add(leName);
					String columnTypeStr = SGDataUtility.appendColumnTitle(LOWER_UPPER_ERROR_VALUE, ehName);
					columnTypeList.add(columnTypeStr);
				} else {
					varList.add(leName);
					String leColumnTypeStr = SGDataUtility.appendColumnTitle(LOWER_ERROR_VALUE, ehName);
					columnTypeList.add(leColumnTypeStr);
					
					varList.add(ueName);
					String ueColumnTypeStr = SGDataUtility.appendColumnTitle(UPPER_ERROR_VALUE, ehName);
					columnTypeList.add(ueColumnTypeStr);
				}
			}
		}
		if (this.isTickLabelAvailable()) {
			for (int ii = 0; ii < this.mTickLabelVariables.length; ii++) {
				String tlName = this.mTickLabelVariables[ii].getName();
				String thName = this.mTickLabelHolderVariables[ii].getName();
				varList.add(tlName);
				String columnTypeStr = SGDataUtility.appendColumnTitle(TICK_LABEL, thName);
				columnTypeList.add(columnTypeStr);
			}
		}
	}

	@Override
	public boolean setArraySectionPropertySub(SGPropertyMap map) {
        if (this.isStrideAvailable()) {
        	final boolean idxAvailable = this.isIndexAvailable();
            SGIntegerSeriesSet arraySection = idxAvailable ? this.getIndexStride() : this.getStride();
            String key = idxAvailable ? COM_DATA_INDEX_ARRAY_SECTION : COM_DATA_ARRAY_SECTION;
            if (!arraySection.isComplete()) {
            	SGPropertyUtility.addQuotedStringProperty(map, key, arraySection.toString());
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
    	SGNetCDFFile ncfile = (SGNetCDFFile) src;
    	this.mXVariables = this.findVariables(ncfile, this.mXVariables);
		this.mYVariables = this.findVariables(ncfile, this.mYVariables);
		if (this.isErrorBarAvailable()) {
			this.mLowerErrorVariables = this.findVariables(
					ncfile, this.mLowerErrorVariables);
			this.mUpperErrorVariables = this.findVariables(
					ncfile, this.mUpperErrorVariables);
			this.mErrorBarHolderVariables = this.findVariables(
					ncfile, this.mErrorBarHolderVariables);
		}
		if (this.isTickLabelAvailable()) {
			this.mTickLabelVariables = this.findVariables(
					ncfile, this.mTickLabelVariables);
			this.mTickLabelHolderVariables = this.findVariables(
					ncfile, this.mTickLabelHolderVariables);
		}
    }

	@Override
	public String getDateFormat() {
		return this.mDateFormat;
	}

	@Override
	public void setDateFormat(String format) {
		this.mDateFormat = format;
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
		list.add(SGIDataColumnTypeConstants.X_VALUE);
		list.add(SGIDataColumnTypeConstants.Y_VALUE);
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
    public double getXValueAt(final int childIndex, final int arrayIndex) {
		SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
		try {
			SGSXYNetCDFData ncData = (SGSXYNetCDFData) sxyArray[childIndex];
			double[] values = ncData.getXValueArray(false);
			final double d = values[arrayIndex];
			/*
			if (this.isNaNAssignedInvalidValue(childIndex, arrayIndex, X_VALUE, d)) {
				return ncData.getXValueAt(arrayIndex);
			} else {
				return d;
			}
			*/
			return d;
		} finally {
	        SGDataUtility.disposeSXYDataArray(sxyArray);
		}
	}

	@Override
    public double getYValueAt(final int childIndex, final int arrayIndex) {
		SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
		try {
			SGSXYNetCDFData ncData = (SGSXYNetCDFData) sxyArray[childIndex];
			double[] values = ncData.getYValueArray(false);
			final double d = values[arrayIndex];
			/*
			if (this.isNaNAssignedInvalidValue(childIndex, arrayIndex, Y_VALUE, d)) {
				return ncData.getYValueAt(arrayIndex);
			} else {
				return d;
			}
			*/
			return d;
		} finally {
	        SGDataUtility.disposeSXYDataArray(sxyArray);
		}
	}

	@Override
	public int getDataViewerColumnNumber(final String columnType, final boolean all) {
		if (this.isIndexAvailable()) {
			return this.getChildNumber();
		} else {
			return SGDataUtility.getDataViewerColumnNumber(this, columnType);
		}
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
		return this.isIndexAvailable() ? this.mIndexStride : this.mStride;
	}

	@Override
	public void setDataViewerValue(final String columnType, final int row, final int col,
			final Object value) {
		if (this.isIndexAvailable()) {
			SGDataValueHistory editedValue = SGDataUtility.setDataViewerValue(
					this, columnType, row, col, value, this.mIndexStride);
			if (editedValue != null) {
				this.mEditedDataValueList.add(editedValue);
			}
		} else {
			List<SGDataValueHistory> editedDataValueList = SGDataUtility.getEditedDataValueList(
					this, columnType, row, col, value, this.mStride);
			this.mEditedDataValueList.addAll(editedDataValueList);
		}
	}

	@Override
    protected List<Dimension> getToolTipNotSpatiallyVariedDimensionList() {
    	Set<Dimension> dimSet = new HashSet<Dimension>();
    	for (SGNetCDFVariable var : this.mXVariables) {
        	dimSet.addAll(var.getDimensions());
    	}
    	for (SGNetCDFVariable var : this.mYVariables) {
        	dimSet.addAll(var.getDimensions());
    	}
    	if (this.isErrorBarAvailable()) {
        	for (SGNetCDFVariable var : this.mLowerErrorVariables) {
        		if (var == null) {
        			continue;
        		}
            	dimSet.addAll(var.getDimensions());
        	}
        	for (SGNetCDFVariable var : this.mUpperErrorVariables) {
        		if (var == null) {
        			continue;
        		}
            	dimSet.addAll(var.getDimensions());
        	}
    	}
    	if (this.isTickLabelAvailable()) {
        	for (SGNetCDFVariable var : this.mTickLabelVariables) {
        		if (var == null) {
        			continue;
        		}
            	dimSet.addAll(var.getDimensions());
        	}
    	}
    	SGNetCDFVariable cVar = this.getCoordinateVariable();
    	dimSet.remove(cVar.getDimension(0));
    	return new ArrayList<Dimension>(dimSet);
    }

    /**
     * Returns a text string of the unit for X values.
     * 
     * @return a text string of the unit for X values
     */
	@Override
    public String getUnitsStringX() {
		if (this.hasSingleVar(true)) {
            return this.getXVariable().getUnitsString();
		} else {
			return "";
		}
	}

    /**
     * Returns a text string of the unit for Y values.
     * 
     * @return a text string of the unit for Y values
     */
	@Override
    public String getUnitsStringY() {
		if (this.hasSingleVar(false)) {
            return this.getYVariable().getUnitsString();
		} else {
			return "";
		}
	}

	@Override
	public void setDataValue(SGDataValueHistory value) {
		SGIntegerSeriesSet stride = this.isIndexAvailable() ? this.mIndexStride : this.mStride;
		SGDataUtility.setDataViewerValue(this, value.getColumnType(), 
				value.getRowIndex(), value.getColumnIndex(), value.getValue(), 
				stride);
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
		if (this.mXVariables.length != 1) {
			return false;
		}
		return (this.mXVariables[0] instanceof SGDateVariable);
	}
    
	@Override
    public boolean hasDateTypeYVariable() {
		if (this.mYVariables.length != 1) {
			return false;
		}
		return (this.mYVariables[0] instanceof SGDateVariable);
	}

	@Override
	protected boolean matches(final int col, final int row, String columnType,
			SGDataValueHistory value, final double d) {
		return SGDataUtility.matches(col, row, columnType, value, d);
	}

	@Override
    protected Array setEditedValues(NetcdfFileWriteable ncWrite,
    		String varName, Array array, final boolean all) {
		
		for (int ii = 0; ii < this.mEditedDataValueList.size(); ii++) {
			SGDataValueHistory.NetCDF.MD1 dataValue 
					= (SGDataValueHistory.NetCDF.MD1) this.mEditedDataValueList.get(ii);
			if (!varName.equals(dataValue.getVarName())) {
				continue;
			}

			Variable var = ncWrite.findVariable(varName);
			List<Dimension> dims = var.getDimensions();
			
			final String dimName = dataValue.getDimName();
			final int dimOrder = this.findDimensionOrder(dims, dimName);
			final int dimIndex = dataValue.getIndex();
			
			final String pickUpDimName = dataValue.getPickUpDimName();
			int pickUpDimOrder = -1;
			int pickUpDimIndex = -1;
			if (pickUpDimName != null) {
				pickUpDimOrder = this.findDimensionOrder(dims, 
						pickUpDimName);
				pickUpDimIndex = dataValue.getPickUpDimIndex();
			}
			
			final String animationDimName = dataValue.getAnimationDimName();
			int animationDimOrder = -1;
			int animationDimIndex = -1;
			if (animationDimName != null) {
				animationDimOrder = this.findDimensionOrder(dims,
						animationDimName);
				animationDimIndex = dataValue.getAnimationDimIndex();
			}
			
			Index idx = array.getIndex();
			if (pickUpDimOrder == -1 && animationDimOrder == -1) {
				idx = idx.set(dimIndex);
			} else if (pickUpDimOrder != -1 && animationDimOrder == -1) {
				if (dimOrder < pickUpDimOrder) {
					idx = idx.set(dimIndex, pickUpDimIndex);
				} else {
					idx = idx.set(pickUpDimIndex, dimIndex);
				}
			} else if (pickUpDimOrder == -1 && animationDimOrder != -1) {
				if (dimOrder < animationDimOrder) {
					idx = idx.set(dimIndex, animationDimIndex);
				} else {
					idx = idx.set(animationDimIndex, dimIndex);
				}
			} else if (pickUpDimOrder != -1 && animationDimOrder != -1) {
				if (dimOrder < pickUpDimOrder) {
					if (pickUpDimOrder < animationDimOrder) {
						idx = idx.set(dimIndex, pickUpDimIndex, animationDimIndex);
					} else {
						if (dimOrder < animationDimOrder) {
							idx = idx.set(dimIndex, animationDimIndex, pickUpDimIndex);
						} else {
							idx = idx.set(animationDimIndex, dimIndex, pickUpDimIndex);
						}
					}
				} else {
					if (pickUpDimOrder < animationDimOrder) {
						if (dimOrder < animationDimOrder) {
							idx = idx.set(pickUpDimIndex, dimIndex, animationDimIndex);
						} else {
							idx = idx.set(pickUpDimIndex, animationDimIndex, dimIndex);
						}
					} else {
						idx = idx.set(animationDimIndex, pickUpDimIndex, dimIndex);
					}
				}
			}
			
			final double value = dataValue.getValue();
			array.setDouble(idx, value);
		}
		
    	return array;
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

//    /**
//     * Returns an array of X-values.
//     *
//     * @return an array of X-values
//     */
//    public double[][] getXValueArray(final boolean all) {
//    	double[][] ret = null;
//    	final boolean useCache = this.useCache(all);
//    	if (useCache) {
//    		ret = SGSXYMultipleDataCache.getXValues(this);
//    	}
//    	if (ret == null) {
//        	SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
//        	ret = new double[sxyArray.length][];
//        	for (int ii = 0; ii < ret.length; ii++) {
//        		ret[ii] = sxyArray[ii].getXValueArray(all);
//        	}
//        	// disposes of data objects
//            SGDataUtility.disposeSXYDataArray(sxyArray);
//    	}
//    	return ret;
//    }
//
//    /**
//     * Returns an array of Y-values.
//     *
//     * @return an array of Y-values
//     */
//    public double[][] getYValueArray(final boolean all) {
//    	double[][] ret = null;
//    	final boolean useCache = this.useCache(all);
//    	if (useCache) {
//    		ret = SGSXYMultipleDataCache.getYValues(this);
//    	}
//    	if (ret == null) {
//        	SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
//        	ret = new double[sxyArray.length][];
//        	for (int ii = 0; ii < ret.length; ii++) {
//        		ret[ii] = sxyArray[ii].getYValueArray(all);
//        	}
//        	// disposes of data objects
//            SGDataUtility.disposeSXYDataArray(sxyArray);
//    	}
//    	return ret;
//    }

	@Override
	public void addSingleDimensionEditedDataValue(SGDataValueHistory dataValue) {
		SGDataValueHistory.NetCDF.D1 dValue = (SGDataValueHistory.NetCDF.D1) dataValue;
		SGDataValueHistory prev = dValue.getPreviousValue();
		SGDataValueHistory.NetCDF.MD1 mdValue;
		if (prev != null) {
			mdValue = new SGDataValueHistory.NetCDF.MD1(
					dValue.getValue(), dValue.getColumnType(), 0,
					dValue.getIndex(), dValue.getVarName(), prev.getValue());
		} else {
			mdValue = new SGDataValueHistory.NetCDF.MD1(
					dValue.getValue(), dValue.getColumnType(), 0,
					dValue.getIndex(), dValue.getVarName());
		}
		this.mEditedDataValueList.add(mdValue);
	}

	@Override
    public SGTwoDimensionalArrayIndex getDataViewerCell(SGTwoDimensionalArrayIndex cell,
    		String columnType, final boolean bStride) {
		SGTwoDimensionalArrayIndex ret = null;
		if (this.isIndexAvailable()) {
			ret = super.getDataViewerCell(cell, columnType, bStride);
		} else {
			if (this.hasMultipleYValues()) {
				if (Y_VALUE.equals(columnType)) {
					ret = super.getDataViewerCell(cell, columnType, bStride);
				} else {
					ret = super.getDataViewerCell(new SGTwoDimensionalArrayIndex(0, cell.getRow()), 
							columnType, bStride);
				}
			} else {
				if (X_VALUE.equals(columnType)) {
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
