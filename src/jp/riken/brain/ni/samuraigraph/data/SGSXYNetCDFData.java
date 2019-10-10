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
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGNamedStringBlock;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import ucar.ma2.Array;
import ucar.ma2.ArrayChar;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

/**
 * The class of scalar XY type data with netCDF data.
 *
 */
public class SGSXYNetCDFData extends SGNetCDFData implements
    SGISXYTypeSingleData, SGIDataColumnTypeConstants, SGIDataPropertyKeyConstants,
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
     * The variable for lower error values.
     */
    protected SGNetCDFVariable mLowerErrorVariable = null;

    /**
     * The variable for upper error values.
     */
    protected SGNetCDFVariable mUpperErrorVariable = null;

    /**
     * The variable for values that holds error values.
     */
    protected SGNetCDFVariable mErrorBarHolderVariable = null;

    /**
     * The variable for tick labels.
     */
    protected SGNetCDFVariable mTickLabelVariable = null;

    /**
     * The variable for values that holds tick labels.
     */
    protected SGNetCDFVariable mTickLabelHolderVariable = null;

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
	 * The shift value.
	 */
	private SGTuple2d mShift = new SGTuple2d();

    /**
     * The default constructor.
     *
     */
    public SGSXYNetCDFData() {
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
     * @param leInfo
     *            information of lower error values
     * @param ueInfo
     *            information of upper error values
     * @param ehInfo
     *            information of error bar holder
     * @param tlInfo
     *            information of tick label
     * @param thInfo
     *            information of tick label holder
     * @param timeInfo
     *            information of time values
     * @param indexInfo
     *            information of index
     * @param stride
     *            stride of an array
     * @param indexStride
     *            stride for index
     * @param tickLabelStride
     *            stride for tick label
     * @param strideAvailable
     *            flag whether to set available the stride
     */
	public SGSXYNetCDFData(final SGNetCDFFile ncfile, SGDataSourceObserver obs,
			final SGNetCDFDataColumnInfo xInfo,
			final SGNetCDFDataColumnInfo yInfo,
			final SGNetCDFDataColumnInfo leInfo,
			final SGNetCDFDataColumnInfo ueInfo,
			final SGNetCDFDataColumnInfo ehInfo,
			final SGNetCDFDataColumnInfo tlInfo,
			final SGNetCDFDataColumnInfo thInfo,
			final SGNetCDFDataColumnInfo timeInfo,
			final SGNetCDFDataColumnInfo indexInfo,
			final SGIntegerSeriesSet stride,
			final SGIntegerSeriesSet indexStride,
			final SGIntegerSeriesSet tickLabelStride,
			final boolean strideAvailable) {

        super(ncfile, obs, timeInfo, indexInfo, indexStride, strideAvailable);

        if (xInfo == null || yInfo == null) {
            throw new IllegalArgumentException("xInfo == null || yInfo == null");
        }
        if (!(leInfo == null && ueInfo == null)
        		&& !(leInfo != null && ueInfo != null)) {
            throw new IllegalArgumentException("leInfo = " + leInfo + ", ueInfo = " + ueInfo);
        }
        if (leInfo != null && ehInfo == null) {
            throw new IllegalArgumentException("Error bar holder name is null.");
        }
        if (tlInfo != null && thInfo == null) {
            throw new IllegalArgumentException("Tick label holder name is null.");
        }

        // get variables
        SGNetCDFVariable xVar = this.getVariable(ncfile, xInfo.getName());
        SGNetCDFVariable yVar = this.getVariable(ncfile, yInfo.getName());
        SGNetCDFVariable lVar = null;
        SGNetCDFVariable uVar = null;
        SGNetCDFVariable ehVar = null;
        if (leInfo != null) {
            lVar = this.getVariable(ncfile, leInfo.getName());
            uVar = this.getVariable(ncfile, ueInfo.getName());
            ehVar = this.getVariable(ncfile, ehInfo.getName());
        }
        SGNetCDFVariable tVar = null;
        SGNetCDFVariable thVar = null;
        if (tlInfo != null) {
            tVar = this.getVariable(ncfile, tlInfo.getName());
            thVar = this.getVariable(ncfile, thInfo.getName());
        }

        //
        // check variables
        //

        List<Dimension> cDimList = new ArrayList<Dimension>();
    	Dimension cDim = null;
        if (this.isIndexAvailable()) {
        	// x and y variables
            if (xVar.isCoordinateVariable() || yVar.isCoordinateVariable()) {
                throw new IllegalArgumentException("Both of x and y variables must not be coordinate variables.");
            }

        	// index variable
            cDim = this.mIndexVariable.getDimension(0);
            cDimList.add(cDim);

            // x and y variables
            this.checkNonCoordinateVariable(xVar, cDimList);
            this.checkNonCoordinateVariable(yVar, cDimList);

        } else {
        	// x and y variables
            if ((xVar.isCoordinateVariable() && yVar.isCoordinateVariable())
            		|| (!xVar.isCoordinateVariable() && !yVar.isCoordinateVariable())) {
                throw new IllegalArgumentException("Either of x and y variables must be a coordinate variable.");
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
            cDimList.add(cDim);

            // time variable
        	if (this.mTimeVariable != null) {
        		Dimension tDim = this.mTimeVariable.getDimension(0);
                if (cDim.equals(tDim)) {
                    throw new IllegalArgumentException(
            			"The dimension of a coordinate variable is equal to the dimension for time.");
                }
        	}

            // y variables
            this.checkNonCoordinateVariable(oVar, cDimList);
        }

        // other variables
    	this.checkNonCoordinateVariable(lVar, cDimList);
    	this.checkNonCoordinateVariable(uVar, cDimList);
    	this.checkNonCoordinateVariable(ehVar, cDimList);
    	this.checkNonCoordinateVariable(tVar, cDimList);
    	this.checkNonCoordinateVariable(thVar, cDimList);

        // set to attributes
        this.mXVariable = xVar;
        this.mYVariable = yVar;
        this.mLowerErrorVariable = lVar;
        this.mUpperErrorVariable = uVar;
        this.mErrorBarHolderVariable = ehVar;
        this.mTickLabelVariable = tVar;
        this.mTickLabelHolderVariable = thVar;
        if (!this.isIndexAvailable()) {
            this.mStride = this.createStride(stride, cDim);
        }

        if (this.mTickLabelVariable != null) {
        	// TODO set decimal place?
        } else {
        	// if tick labels are not set yet
        	this.setupTickLabel(xVar, yVar);
        }
        
        SGIntegerSeriesSet tlStride = null;
        if (tickLabelStride != null) {
            tlStride = (SGIntegerSeriesSet) tickLabelStride.clone();
        } else {
        	if (!this.isIndexAvailable()) {
        		tlStride = (SGIntegerSeriesSet) this.mStride.clone();
        	} else {
        		tlStride = (SGIntegerSeriesSet) this.mIndexStride.clone();
        	}
        }
        this.mTickLabelStride = tlStride;
    }
	
    private void setupTickLabel(SGNetCDFVariable xVar, SGNetCDFVariable yVar) {
    	if (xVar instanceof SGDateVariable) {
    		this.mTickLabelVariable = xVar;
    		this.mTickLabelHolderVariable = yVar;
    	} else if (yVar instanceof SGDateVariable) {
    		this.mTickLabelVariable = yVar;
    		this.mTickLabelHolderVariable = xVar;
    	}
    }

    /**
     * Disposes of this data object.
     * Do not call any method of this object after called this method.
     */
    public void dispose() {
        super.dispose();

        // Note: Do not clear the caches!

        this.mXVariable = null;
        this.mYVariable = null;
        this.mLowerErrorVariable = null;
        this.mUpperErrorVariable = null;
        this.mErrorBarHolderVariable = null;
        this.mTickLabelVariable = null;
        this.mTickLabelHolderVariable = null;
        this.mStride = null;
        this.mTickLabelStride = null;
		this.mShift = null;
    }

    private SGNetCDFVariable getCoordinateVariable() {
    	if (this.isIndexAvailable()) {
    		return this.mIndexVariable;
    	} else {
            if (this.mXVariable.isCoordinateVariable()) {
                return this.mXVariable;
            } else {
                return this.mYVariable;
            }
    	}
    }

    /**
     * Returns the properties of this data.
     *
     * @return the properties of this data
     */
    public SGProperties getProperties() {
        SGProperties p = new SXYNetCDFDataProperties();
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
        if (!(p instanceof SXYNetCDFDataProperties)) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        SXYNetCDFDataProperties sp = (SXYNetCDFDataProperties) p;
        sp.xName = this.getName(this.mXVariable);
        sp.yName = this.getName(this.mYVariable);
        sp.lName = this.getName(this.mLowerErrorVariable);
        sp.uName = this.getName(this.mUpperErrorVariable);
        sp.ehName = this.getName(this.mErrorBarHolderVariable);
        sp.tName = this.getName(this.mTickLabelVariable);
        sp.thName = this.getName(this.mTickLabelHolderVariable);
        sp.mStride = this.getStride();
        sp.mTickLabelStride = this.getTickLabelStride();
        return true;
    }

    // This method is not already called anywhere.
//    /**
//     * Read properties.
//     *
//     * @param el
//     *           an Element object
//     * @return true if succeeded
//     */
//    public boolean readProperty(Element el) {
//
//        if (super.readProperty(el) == false) {
//            return false;
//        }
//
//        String str = null;
//        SGVariable xVar = null;
//        SGVariable yVar = null;
//        SGVariable lVar = null;
//        SGVariable uVar = null;
//        SGVariable ehVar = null;
//        SGVariable tVar = null;
//        SGVariable thVar = null;
//
//        // x variable
//        str = el.getAttribute(KEY_X_VALUE_NAME);
//        if (str.length() != 0) {
//            xVar = this.getNetcdfFile().findVariable(str);
//            if (xVar == null) {
//                return false;
//            }
//        } else {
//            return false;
//        }
//
//        // y variable
//        str = el.getAttribute(KEY_Y_VALUE_NAME);
//        if (str.length() != 0) {
//            yVar = this.getNetcdfFile().findVariable(str);
//            if (yVar == null) {
//                return false;
//            }
//        } else {
//            return false;
//        }
//
//        // lower error variable
//        str = el.getAttribute(KEY_LOWER_ERROR_VALUE_NAME);
//        if (str.length() != 0) {
//            lVar = this.getNetcdfFile().findVariable(str);
//            if (lVar == null) {
//                return false;
//            }
//        }
//
//        // error bar holder variable
//        str = el.getAttribute(KEY_ERROR_BAR_HOLDER_NAME);
//        if (str.length() != 0) {
//            ehVar = this.getNetcdfFile().findVariable(str);
//            if (ehVar == null) {
//                return false;
//            }
//        }
//
//        if (!(lVar == null && uVar == null && ehVar == null)
//        		&& !(lVar != null && uVar != null && ehVar != null)) {
//            return false;
//        }
//
//        // tick label variable
//        str = el.getAttribute(KEY_TICK_LABEL_NAME);
//        if (str.length() != 0) {
//            tVar = this.getNetcdfFile().findVariable(str);
//            if (tVar == null) {
//                return false;
//            }
//        }
//
//        // tick label holder variable
//        str = el.getAttribute(KEY_TICK_LABEL_HOLDER_NAME);
//        if (str.length() != 0) {
//            thVar = this.getNetcdfFile().findVariable(str);
//            if (thVar == null) {
//                return false;
//            }
//        }
//
//        if (!(tVar == null && thVar == null) && !(tVar != null && thVar != null)) {
//            return false;
//        }
//
//        // set to attribute
//        this.mXVariable = xVar;
//        this.mYVariable = yVar;
//        this.mLowerErrorVariable = lVar;
//        this.mUpperErrorVariable = uVar;
//        this.mErrorBarHolderVariable = ehVar;
//        this.mTickLabelVariable = tVar;
//        this.mTickLabelHolderVariable = thVar;
//
//        return true;
//    }

//    /**
//     * Save values to given file.
//     *
//     * @param data
//     *             a data
//     * @return
//     *             true if succeeded
//     */
//    public boolean saveData(final File file) {
//        boolean status = false;
//        Variable var = this.getVariable();
//        Variable cVar = this.getCoordinateVariable();
//        Variable[] vArray;
//        if (this.mLowerErrorVariable != null && this.mUpperErrorVariable != null) {
//            vArray = new Variable[]{cVar, var, this.mLowerErrorVariable, this.mUpperErrorVariable};
//        } else {
//            vArray = new Variable[]{cVar, var};
//        }
//        try {
//            FileWriter fw = new FileWriter(file);
//            for (int ii = 0; ii < vArray.length; ii++) {
//                String title = this.getNameWithUnit(vArray[ii]);
//                if (title == null) {
//                    title = "";
//                }
//                String str = SGUtilityText.getCSVString(title);
//                if (ii != 0) {
//                    fw.write(",");
//                }
//                fw.write(str);
//            }
//            fw.write("\n");
//            status = this.writeData(fw);
//            fw.close();
//        } catch (IOException ex) {
//            return false;
//        }
//        return status;
//    }

    /**
     * Sets the data.
     *
     * @param data
     *             data set to this object
     * @return
     *             true if succeeded
     */
    public boolean setData(SGData data) {
        if (!(data instanceof SGSXYNetCDFData)) {
            throw new IllegalArgumentException("Given data is not the instance of SGSXYNetCDFData class.");
        }
        if (super.setData(data) == false) {
            return false;
        }
        SGSXYNetCDFData nData = (SGSXYNetCDFData) data;
        this.mXVariable = nData.mXVariable;
        this.mYVariable = nData.mYVariable;
        this.mLowerErrorVariable = nData.mLowerErrorVariable;
        this.mUpperErrorVariable = nData.mUpperErrorVariable;
        this.mErrorBarHolderVariable = nData.mErrorBarHolderVariable;
        this.mTickLabelVariable = nData.mTickLabelVariable;
        this.mTickLabelHolderVariable = nData.mTickLabelHolderVariable;
        this.mDecimalPlaces = nData.mDecimalPlaces;
        this.mExponent = nData.mExponent;
        this.setStride(nData.mStride);
        this.setTickLabelStride(nData.mTickLabelStride);
        this.setShift(nData.mShift);
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
        if (!(p instanceof SXYNetCDFDataProperties)) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        SXYNetCDFDataProperties sp = (SXYNetCDFDataProperties) p;
        this.mXVariable = this.findVariable(sp.xName);
        this.mYVariable = this.findVariable(sp.yName);
        this.mLowerErrorVariable = this.findVariable(sp.lName);
        this.mUpperErrorVariable = this.findVariable(sp.uName);
        this.mErrorBarHolderVariable = this.findVariable(sp.ehName);
        this.mTickLabelVariable = this.findVariable(sp.tName);
        this.mTickLabelHolderVariable = this.findVariable(sp.thName);
        this.setStride(sp.mStride);
        this.setTickLabelStride(sp.mTickLabelStride);
        return true;
    }

    /**
     * Returns a text string of data type.
     *
     * @return a text string of data type
     */
    public String getDataType() {
        return SGDataTypeConstants.SXY_NETCDF_DATA;
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

	private double[] getValueArray(final boolean all, SGNetCDFVariable var,
			final boolean removeInvalidValues) {
		double[] ret = null;
        if (this.isIndexAvailable()) {
            ret = this.getValueArray(var, new SGNetCDFVariable[]{ this.mIndexVariable }, all,
            		removeInvalidValues);
        } else {
            if (var.isCoordinateVariable()) {
                ret = this.getCoordinateValueArray(var, this.getStrideInstance(), all,
                		removeInvalidValues);
            } else {
                ret = this.getValueArray(var, new SGNetCDFVariable[]{ this.getCoordinateVariable() }, all,
                		removeInvalidValues);
            }
        }
        return ret;
	}

	double getValueAt(SGNetCDFVariable var, final int index) {
        if (this.isIndexAvailable()) {
            return this.getValueAt(var, new SGNetCDFVariable[]{ this.mIndexVariable }, new int[] { index });
        } else {
            if (var.isCoordinateVariable()) {
                return this.getCoordinateValueAt(var, this.getStrideInstance(), index);
            } else {
                return this.getValueAt(var, new SGNetCDFVariable[]{ this.getCoordinateVariable() }, 
                		new int[] { index });
            }
        }
	}

	@Override
	public boolean useValueCache(final boolean all) {
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

	@Override
	public boolean useTickLabelCache(final boolean all) {
		if (!all) {
			return true;
		}
		if (!this.isStrideAvailable()) {
			return true;
		}
		return this.mTickLabelStride.isComplete();
	}

    /**
     * Returns an array of X-values.
     *
     * @return an array of X-values
     */
	@Override
    public double[] getXValueArray(final boolean all) {
    	return SGDataUtility.getXValueArray(this, all);
    }
    
	@Override
    public double[] getXValueArray(final boolean all, final boolean useCache,
    		final boolean removeInvalidValues) {
		double[] ret = this.getValueArray(all, this.mXVariable,
				removeInvalidValues);
    	if (useCache) {
            SGSXYDataCache.setXValues(this, ret);
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
    	return SGDataUtility.getYValueArray(this, all);
    }

	@Override
    public double[] getYValueArray(final boolean all, final boolean useCache,
    		final boolean removeInvalidValues) {
    	double[] ret = this.getValueArray(all, this.mYVariable, removeInvalidValues);
    	if (useCache) {
            SGSXYDataCache.setYValues(this, ret);
    	}
    	return ret;
    }

    /**
     * Returns an array of lower error values.
     *
     * @return an array of lower error values
     */
	@Override
    public double[] getLowerErrorValueArray(final boolean all) {
        return SGDataUtility.getLowerErrorValueArray(this, all);
    }
	
	@Override
    public double[] getLowerErrorValueArray(final boolean all, final boolean useCache,
    		final boolean removeInvalidValues) {
    	double[] ret = this.getValueArray(all, this.mLowerErrorVariable, removeInvalidValues);
    	if (useCache) {
            SGSXYDataCache.setLowerErrorValues(this, ret);
    	}
    	return ret;
    }

    /**
     * Returns an array of upper error values.
     *
     * @return an array of upper error values
     */
	@Override
    public double[] getUpperErrorValueArray(final boolean all) {
        return SGDataUtility.getUpperErrorValueArray(this, all);
    }

	@Override
    public double[] getUpperErrorValueArray(final boolean all, final boolean useCache,
    		final boolean removeInvalidValues) {
		double[] ret = this.getValueArray(all, this.mUpperErrorVariable, removeInvalidValues);
    	if (useCache) {
            SGSXYDataCache.setUpperErrorValues(this, ret);
    	}
    	return ret;
    }
    
    /**
     * Returns an array of text strings.
     *
     * @return an array of text strings
     */
    @Override
    public String[] getStringArray(final boolean all) {
    	return SGDataUtility.getStringArray(this, all);
    }
    
	@Override
    public String[] getStringArray(final boolean all, final boolean useCache) {
    	String[] ret = null;
		if (this.mTickLabelVariable instanceof SGCharVariable) {
			ret = ((SGCharVariable) this.mTickLabelVariable)
					.getStringArray();
		} else if (this.mTickLabelVariable.getVariable() != null) {
			Variable var = this.mTickLabelVariable.getVariable();
			DataType dataType = var.getDataType();
			SGNetCDFVariable cVar = this.getCoordinateVariable();
			if (dataType.isString()) {
				ret = this.getStringArray(this.mTickLabelVariable,
						new SGNetCDFVariable[] { cVar }, this.mOriginMap,
						true);
			} else {
				double[] dArray = this.getValueArray(
						this.mTickLabelVariable,
						new SGNetCDFVariable[] { cVar }, this.mOriginMap,
						true, false);
				ret = SGUtilityNumber.getStringArray(dArray,
						this.mDecimalPlaces, this.mExponent);
			}
		}
        if (useCache) {
        	SGSXYDataCache.setTickLabels(this, ret);
        }
        return ret;
    }

    /**
     * Returns whether error bars are available.
     *
     * @return true if error bars are available
     */
    public boolean isErrorBarAvailable() {
        return (this.mLowerErrorVariable != null && this.mUpperErrorVariable != null);
    }

    /**
     * Returns whether tick labels are available.
     *
     * @return true if tick labels are available
     */
    public boolean isTickLabelAvailable() {
        return (this.mTickLabelVariable != null);
    }

    /**
     * Returns whether a given data object has the same tick label resources.
     *
     * @param data
     *           a data to compare
     * @return true if a given data object has the same tick label resources
     */
    public boolean hasEqualTickLabelResource(SGISXYTypeSingleData data) {
    	if (!(data instanceof SGSXYNetCDFData)) {
    		return false;
    	}
    	SGSXYNetCDFData dataSXY = (SGSXYNetCDFData) data;
    	if (this.mTickLabelVariable == null) {
    		return (dataSXY.mTickLabelVariable == null);
    	} else {
        	return this.mTickLabelVariable.equals(dataSXY.mTickLabelVariable);
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

    /**
     * Returns the title for the X-axis.
     *
     * @return the title for the X-axis
     */
    public String getTitleX() {
        return this.getNameWithUnit(this.mXVariable);
    }

    /**
     * Returns the title for the Y-axis.
     *
     * @return the title for the Y-axis
     */
    public String getTitleY() {
        return this.getNameWithUnit(this.mYVariable);
    }

    /**
     * A class of scalar XY netCDF data properties.
     */
    public static class SXYNetCDFDataProperties extends NetCDFDataProperties {

        protected String xName = null;

        protected String yName = null;

        protected String lName = null;

        protected String uName = null;

        protected String ehName = null;

        protected String tName = null;

        protected String thName = null;

        SGIntegerSeriesSet mStride = null;

        SGIntegerSeriesSet mTickLabelStride = null;

        /**
         * The default constructor.
         */
        public SXYNetCDFDataProperties() {
            super();
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.xName = null;
            this.yName = null;
            this.lName = null;
            this.uName = null;
            this.ehName = null;
            this.tName = null;
            this.thName = null;
            this.mStride = null;
            this.mTickLabelStride = null;
        }

    	@Override
    	public boolean equals(Object obj) {
            if ((obj instanceof SXYNetCDFDataProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            return true;
        }

    	@Override
        public boolean hasEqualSize(DataProperties dp) {
            if ((dp instanceof SXYNetCDFDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualSize(dp)) {
            	return false;
            }
            SXYNetCDFDataProperties p = (SXYNetCDFDataProperties) dp;
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
            if ((dp instanceof SXYNetCDFDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualColumnTypes(dp)) {
            	return false;
            }
            SXYNetCDFDataProperties p = (SXYNetCDFDataProperties) dp;
            if (SGUtility.equals(this.xName, p.xName) == false) {
            	return false;
            }
            if (SGUtility.equals(this.yName, p.yName) == false) {
            	return false;
            }
            if (SGUtility.equals(this.lName, p.lName) == false) {
            	return false;
            }
            if (SGUtility.equals(this.uName, p.uName) == false) {
            	return false;
            }
            if (SGUtility.equals(this.ehName, p.ehName) == false) {
            	return false;
            }
            if (SGUtility.equals(this.tName, p.tName) == false) {
            	return false;
            }
            if (SGUtility.equals(this.thName, p.thName) == false) {
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
        	SXYNetCDFDataProperties p = (SXYNetCDFDataProperties) super.copy();
        	p.mStride = (this.mStride != null) ? (SGIntegerSeriesSet) this.mStride.clone() : null;
        	p.mTickLabelStride = (this.mTickLabelStride != null) ? (SGIntegerSeriesSet) this.mTickLabelStride.clone() : null;
        	return p;
        }
    }

    /**
     * Returns an array of current column types.
     *
     * @return an array of current column types
     */
    @Override
    public String[] getCurrentColumnType() {
    	SGNetCDFFile file = this.getNetcdfFile();
        List<SGNetCDFVariable> varList = file.getVariables();
        final int varNum = varList.size();
        String[] array = new String[varNum];
        for (int ii = 0; ii < varNum; ii++) {
            SGNetCDFVariable var = varList.get(ii);
            if (var.equals(this.mXVariable)) {
                array[ii] = X_VALUE;
            } else if (var.equals(this.mYVariable)) {
                array[ii] = Y_VALUE;
            } else if (var.equals(this.mLowerErrorVariable)
                    || var.equals(this.mUpperErrorVariable)) {
                String name = null;
                if (this.mLowerErrorVariable.equals(this.mUpperErrorVariable)) {
                    name = LOWER_UPPER_ERROR_VALUE;
                } else {
                    if (var.equals(this.mLowerErrorVariable)) {
                        name = LOWER_ERROR_VALUE;
                    } else if (var.equals(this.mUpperErrorVariable)) {
                        name = UPPER_ERROR_VALUE;
                    }
                }
                if (name == null) {
                    return null;
                }
                final int index = file.getVariableIndex(this.mErrorBarHolderVariable.getName());
                if (index == -1) {
                    return null;
                }
                array[ii] = SGDataUtility.appendColumnTitle(name, this.mErrorBarHolderVariable.getName());
            } else if (var.equals(this.mTickLabelVariable)) {
                final int index = file.getVariableIndex(this.mTickLabelHolderVariable.getName());
                if (index == -1) {
                    return null;
                }
                array[ii] = SGDataUtility.appendColumnTitle(TICK_LABEL, this.mTickLabelHolderVariable.getName());
            } else if (var.equals(this.mTimeVariable)) {
                array[ii] = ANIMATION_FRAME;
            } else if (var.equals(this.mIndexVariable)) {
				array[ii] = INDEX;
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
    public boolean setColumnType(String[] columns) {

    	SGNetCDFVariable xVar = null;
    	SGNetCDFVariable yVar = null;
    	SGNetCDFVariable lVar = null;
    	SGNetCDFVariable uVar = null;
    	SGNetCDFVariable tVar = null;
    	SGNetCDFVariable timeVar = null;
    	SGNetCDFVariable indexVar = null;
    	String lCol = null;
    	String uCol = null;
    	String tCol = null;

        // set variables
        List<SGNetCDFVariable> varList = this.getNetcdfFile().getVariables();
        for (int ii = 0; ii < columns.length; ii++) {
            SGNetCDFVariable var = varList.get(ii);
            String valueType = var.getValueType();
            if (SGDataUtility.isEqualColumnType(X_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)
            			&& !VALUE_TYPE_DATE.equals(valueType)) {
            		return false;
            	}
                xVar = var;
            } else if (SGDataUtility.isEqualColumnType(Y_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)
            			&& !VALUE_TYPE_DATE.equals(valueType)) {
            		return false;
            	}
                yVar = var;
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], LOWER_ERROR_VALUE)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                lVar = var;
                lCol = columns[ii];
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], UPPER_ERROR_VALUE)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                uVar = var;
                uCol = columns[ii];
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], LOWER_UPPER_ERROR_VALUE)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                lVar = var;
                uVar = var;
                lCol = columns[ii];
                uCol = columns[ii];
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], TICK_LABEL)) {
                tVar = var;
                tCol = columns[ii];
            } else if (SGDataUtility.isEqualColumnType(columns[ii], ANIMATION_FRAME)
            		|| SGDataUtility.isEqualColumnType(TIME, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                timeVar = var;
            } else if (SGDataUtility.isEqualColumnType(columns[ii], INDEX)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                indexVar = var;
            } else if ("".equals(columns[ii])) {
            	continue;
            } else {
            	return false;
            }
        }
        if (xVar == null || yVar == null) {
            return false;
        }
        if (!((lVar != null) && (uVar != null)) && !((lVar == null) && (uVar == null))) {
            return false;
        }

        SGNetCDFVariable ehVar = null;
        if (lCol != null && uCol != null) {
            Integer ehle = SGDataUtility.getColumnIndexOfAppendedColumnTitle(lCol, this);
            Integer ehue = SGDataUtility.getColumnIndexOfAppendedColumnTitle(uCol, this);
            if (ehle == null || ehue == null || ehle.equals(ehue)==false) {
                return false;
            }
            ehVar = varList.get(ehle.intValue());
        }

        SGNetCDFVariable thVar = null;
        if (tCol != null) {
            Integer th = SGDataUtility.getColumnIndexOfAppendedColumnTitle(tCol, this);
            if (th == null) {
                return false;
            }
            thVar = varList.get(th.intValue());
        }

        // set variables
        this.mXVariable = xVar;
        this.mYVariable = yVar;
        this.mLowerErrorVariable = lVar;
        this.mUpperErrorVariable = uVar;
        this.mErrorBarHolderVariable = ehVar;
        this.mTickLabelVariable = tVar;
        this.mTickLabelHolderVariable = thVar;
        this.mIndexVariable = indexVar;
        this.setTimeVariable(timeVar);

        return true;
    }

    /**
     * Returns an array of all variables.
     *
     * @return an array of all variables
     */
    public SGNetCDFVariable[] getAssignedVariables() {
    	List<SGNetCDFVariable> varList = new ArrayList<SGNetCDFVariable>();
    	varList.add(this.mXVariable);
    	varList.add(this.mYVariable);
    	final boolean be = this.isErrorBarAvailable();
    	final boolean bt = this.isTickLabelAvailable();
        if (be && bt) {
        	varList.add(this.mLowerErrorVariable);
        	varList.add(this.mUpperErrorVariable);
        	varList.add(this.mTickLabelVariable);
        } else if (be && !bt) {
        	varList.add(this.mLowerErrorVariable);
        	varList.add(this.mUpperErrorVariable);
        } else if (!be && bt) {
        	varList.add(this.mTickLabelVariable);
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

    public SGNetCDFVariable getXVariable() {
    	return this.mXVariable;
    }

    public SGNetCDFVariable getYVariable() {
    	return this.mYVariable;
    }

    public SGNetCDFVariable getLowerErrorVariable() {
    	return this.mLowerErrorVariable;
    }

    public SGNetCDFVariable getUpperErrorVariable() {
    	return this.mUpperErrorVariable;
    }

    public SGNetCDFVariable getErrorHolderVariable() {
    	return this.mErrorBarHolderVariable;
    }

    public SGNetCDFVariable getTickLabelVariable() {
    	return this.mTickLabelVariable;
    }

    public SGNetCDFVariable getTickLabelHolderVariable() {
    	return this.mTickLabelHolderVariable;
    }

    /**
     * Sets the decimal places for the tick labels.
     *
     * @param dp
     *          a value to set to the decimal places
     */
    @Override
    public void setDecimalPlaces(final int dp) {
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
    public void setExponent(final int exp) {
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

    /**
	 * Returns whether the error bars are vertical. If this data does not have
	 * error values, returns null.
	 *
	 * @return true if error bars are vertical, false if they are horizontal and
	 *         null if this data do not have error values
	 */
    public Boolean isErrorBarVertical() {
    	if (this.isErrorBarAvailable()) {
    		return (this.mErrorBarHolderVariable.equals(this.mYVariable));
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
	public Boolean isTickLabelHorizontal() {
    	if (this.isTickLabelAvailable()) {
    		return (this.mTickLabelHolderVariable.equals(this.mYVariable));
    	} else {
    		return null;
    	}
	}

    /**
     * Return whether the XVariable is a coordinate variable.
     *
     * @return whether the XVariable is a coordinate variable.
     */
    public boolean isXVariableCoordinate() {
        return this.getXVariable().isCoordinateVariable();
    }

    protected SGNetCDFDataColumnInfo createErrorBarInfo(SGNetCDFVariable var, String nameStr) {
		if (this.isErrorBarAvailable()) {
			return SGDataUtility.createErrorBarInfo(var, nameStr, this.mLowerErrorVariable,
					this.mUpperErrorVariable, this.mErrorBarHolderVariable);
		} else {
			return null;
		}
    }

    protected SGNetCDFDataColumnInfo getErrorBarHolderInfo(SGNetCDFDataColumnInfo xInfo,
    		SGNetCDFDataColumnInfo yInfo) {
    	if (this.mErrorBarHolderVariable.equals(this.mXVariable)) {
    		return xInfo;
    	} else {
    		return yInfo;
    	}
    }

    protected SGNetCDFDataColumnInfo[] getErrorBarHolderInfo(SGNetCDFDataColumnInfo[] xInfo,
    		SGNetCDFDataColumnInfo[] yInfo) {
    	if (this.mErrorBarHolderVariable.equals(this.mXVariable)) {
    		return xInfo;
    	} else {
    		return yInfo;
    	}
    }

    protected SGNetCDFDataColumnInfo createTickLabelInfo(SGNetCDFVariable var) {
		if (this.isTickLabelAvailable()) {
			String thName = this.mTickLabelHolderVariable.getName();
			return SGDataUtility.createDataColumnInfo(this.mTickLabelVariable, TICK_LABEL, thName);
		} else {
			return null;
		}
    }

    protected SGNetCDFDataColumnInfo getTickLabelHolderInfo(SGNetCDFDataColumnInfo xInfo,
    		SGNetCDFDataColumnInfo yInfo) {
    	if (this.mTickLabelHolderVariable.equals(this.mXVariable)) {
    		return xInfo;
    	} else {
    		return yInfo;
    	}
    }

    protected SGNetCDFDataColumnInfo[] getTickLabelHolderInfo(SGNetCDFDataColumnInfo[] xInfo,
    		SGNetCDFDataColumnInfo[] yInfo) {
    	if (this.mTickLabelHolderVariable.equals(this.mXVariable)) {
    		return xInfo;
    	} else {
    		return yInfo;
    	}
    }

    /**
     * Transforms this data to a multiple type data.
     *
     * @return a transformed data
     */
    public SGISXYTypeMultipleData toMultiple() {
        // Convert this class (SGSXYNetCDFData) to class (SGSXYMultipleVariableNetCDFData).
        final boolean be = this.isErrorBarAvailable();
        final boolean bt = this.isTickLabelAvailable();
		SGNetCDFDataColumnInfo[] x = SGDataUtility.createDataColumnInfoArray(this.mXVariable, X_VALUE);
		SGNetCDFDataColumnInfo[] y = SGDataUtility.createDataColumnInfoArray(this.mYVariable, Y_VALUE);
		SGNetCDFDataColumnInfo[] le = be ? new SGNetCDFDataColumnInfo[] { this.createErrorBarInfo(
				this.mLowerErrorVariable, LOWER_ERROR_VALUE) } : null;
		SGNetCDFDataColumnInfo[] ue = be ? new SGNetCDFDataColumnInfo[] { this.createErrorBarInfo(
				this.mUpperErrorVariable, UPPER_ERROR_VALUE) } : null;
		SGNetCDFDataColumnInfo[] eh = be ? this.getErrorBarHolderInfo(x, y) : null;
		SGNetCDFDataColumnInfo[] tl = bt ? new SGNetCDFDataColumnInfo[] { SGDataUtility
				.createDataColumnInfo(this.mTickLabelVariable, TICK_LABEL,
						this.mTickLabelHolderVariable.getName()) } : null;
		SGNetCDFDataColumnInfo[] th = bt ? this.getTickLabelHolderInfo(x, y) : null;
		SGNetCDFDataColumnInfo time = (this.mTimeVariable != null) ? SGDataUtility
				.createDataColumnInfo(this.mTimeVariable, ANIMATION_FRAME) : null;
		SGNetCDFDataColumnInfo index = this.isIndexAvailable() ? SGDataUtility
				.createDataColumnInfo(this.mIndexVariable, INDEX) : null;
		SGSXYNetCDFMultipleData data = new SGSXYNetCDFMultipleData(
				this.getNetcdfFile(), this.getDataSourceObserver(), x, y, le, ue, eh, tl, th, time,
				index, this.getIndexStride(), this.getTickLabelStride(), this.isStrideAvailable());
        data.mOriginMap = new HashMap<String, Integer>(this.mOriginMap);
        data.setDecimalPlaces(this.mDecimalPlaces);
        data.setExponent(this.mExponent);
		data.setStrideMap(this.getStrideMap());
		SGDataCache cache = this.getCache();
        if (cache != null) {
    		data.setCache(new SGSXYMultipleDataCache((SGSXYDataCache) cache));
        }
        data.setTimeStride(this.mTimeStride);
        data.setShift(this.mShift);
        for (int ii = 0; ii < this.mEditedDataValueList.size(); ii++) {
        	SGDataValueHistory dataValue = this.mEditedDataValueList.get(ii);
        	data.addSingleDimensionEditedDataValue(dataValue);
        }

        return data;
    }

    /**
     * Transforms this data to a multiple type data.
     *
     * @param dim
     *            the dimension
     * @param pickUpIndex
     *            the index of dimension
     * @param len
     *            the length of picked up dimension
     * @return a transformed data
     */
    public SGISXYTypeMultipleData toMultiple(final Dimension dim, final int pickUpIndex,
    		final int len) {
		SGNetCDFDataColumnInfo x = SGDataUtility.createDataColumnInfo(this.mXVariable, X_VALUE);
		SGNetCDFDataColumnInfo y = SGDataUtility.createDataColumnInfo(this.mYVariable, Y_VALUE);
		SGNetCDFDataColumnInfo le = null;
		SGNetCDFDataColumnInfo ue = null;
		SGNetCDFDataColumnInfo eh = null;
		if (this.isErrorBarAvailable()) {
			le = this.createErrorBarInfo(this.mLowerErrorVariable, LOWER_ERROR_VALUE);
			ue = this.createErrorBarInfo(this.mUpperErrorVariable, UPPER_ERROR_VALUE);
			eh = this.getErrorBarHolderInfo(x, y);
		}
		SGNetCDFDataColumnInfo tl = null;
		SGNetCDFDataColumnInfo th = null;
		if (this.isTickLabelAvailable()) {
			String thName = this.mTickLabelHolderVariable.getName();
			tl = SGDataUtility.createDataColumnInfo(this.mTickLabelVariable, TICK_LABEL, thName);
			th = this.getTickLabelHolderInfo(x, y);
		}
		SGNetCDFDataColumnInfo time = (this.mTimeVariable != null) ? SGDataUtility
				.createDataColumnInfo(this.mTimeVariable, ANIMATION_FRAME) : null;
		SGNetCDFDataColumnInfo index = this.isIndexAvailable() ? SGDataUtility
				.createDataColumnInfo(this.mIndexVariable, INDEX) : null;
		SGIntegerSeriesSet pickUpIndices;
		if (pickUpIndex == len - 1) {
			pickUpIndices = SGIntegerSeriesSet.parse(SGIntegerSeries.ARRAY_INDEX_END, len);
		} else {
			pickUpIndices = new SGIntegerSeriesSet(pickUpIndex);
		}
		SGSXYNetCDFMultipleData data = new SGSXYNetCDFMultipleData(
				this.getNetcdfFile(), this.getDataSourceObserver(),
				x, y, le, ue, eh, tl, th, dim.getName(), pickUpIndices, time, index,
				this.mStride, this.mTickLabelStride, this.isStrideAvailable());
        data.setDecimalPlaces(this.mDecimalPlaces);
        data.setExponent(this.mExponent);
        data.mOriginMap = new HashMap<String, Integer>(this.mOriginMap);
        SGDataCache cache = this.getCache();
        if (cache != null) {
    		data.setCache(new SGSXYMultipleDataCache((SGSXYDataCache) cache));
        }
		data.setTimeStride(this.mTimeStride);
		return data;
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
        SGSXYNetCDFData data = (SGSXYNetCDFData) super.clone();
        data.mStride = this.getStride();
        data.mTickLabelStride = this.getTickLabelStride();
        data.mShift = (SGTuple2d) this.mShift.clone();
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
     * Returns the number of strings.
     *
     * @return the number of strings
     */
    @Override
    public int getStringNumber() {
    	if (this.isStrideAvailable()) {
        	return this.mTickLabelStride.getLength();
    	} else {
    		return this.getPointsNumber();
    	}
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

	@Override
	protected boolean exportToFile(NetcdfFileWriteable ncWrite, 
			final SGExportParameter mode, SGDataBufferPolicy policy)
			throws IOException, InvalidRangeException {
		// do nothing
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
		return this.toMultiple().getDateFlag();
    }

	@Override
    public boolean hasGenericTickLabels() {
		return this.toMultiple().hasGenericTickLabels();
	}

	@Override
    public boolean hasSameErrorVariable() {
		if (!this.isErrorBarAvailable()) {
			return false;
		}
		return this.mLowerErrorVariable.equals(this.mUpperErrorVariable);
	}

	@Override
    public SGDate[] getDateArray(SGSXYDataBufferPolicy policy) {
		// always returns null
    	return null;
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

	@Override
	public SGDate[] getDateArray(boolean all) {
		// always returns null
		return null;
	}

	@Override
	public Boolean isYValuesHolder() {
		return this.toMultiple().isYValuesHolder();
	}

	@Override
	protected boolean setArraySectionPropertySub(SGPropertyMap map) {
		// do nothing
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
    	this.mXVariable = this.findVariable(ncfile, this.mXVariable);
		this.mYVariable = this.findVariable(ncfile, this.mYVariable);
		if (this.isErrorBarAvailable()) {
			this.mLowerErrorVariable = this.findVariable(
					ncfile, this.mLowerErrorVariable);
			this.mUpperErrorVariable = this.findVariable(
					ncfile, this.mUpperErrorVariable);
			this.mErrorBarHolderVariable = this.findVariable(
					ncfile, this.mErrorBarHolderVariable);
		}
		if (this.isTickLabelAvailable()) {
			this.mTickLabelVariable = this.findVariable(
					ncfile, this.mTickLabelVariable);
			this.mTickLabelHolderVariable = this.findVariable(
					ncfile, this.mTickLabelHolderVariable);
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
		// always returns null
		return null;
    }

    /**
     * Returns preferred column type for data viewer.
     * 
     * @return preferred column type for data viewer
     */
	@Override
    public String getPreferredDataViewColumnType() {
    	// always returns null
		return null;
    }
	
	@Override
	public Double getDataViewerValue(String columnType, int row, int col) {
		// always returns null
		return null;
	}

	@Override
	public int getDataViewerColumnNumber(final String columnType, final boolean all) {
		// always returns 0
		return 0;
	}

	@Override
	public int getDataViewerRowNumber(final String columnType, final boolean all) {
		// always returns 0
		return 0;
	}

	@Override
	public SGIntegerSeriesSet getDataViewerColStride(String columnType) {
		// always returns null
		return null;
	}

	@Override
	public SGIntegerSeriesSet getDataViewerRowStride(String columnType) {
		// always returns null
		return null;
	}

	@Override
	public void setDataViewerValue(String columnType, final int row, final int col,
			Object value) {
		// do nothing
	}
	
	@Override
	public DoubleValueSetResult setDataViewerDoubleValue(String columnType, final int index, 
			Double value) {
		DoubleValueSetResult ret = new DoubleValueSetResult();
		SGSXYDataCache cache = SGSXYDataCache.getCache(this);
		if (cache == null) {
			ret.status = false;
			return ret;
		}
		if (X_VALUE.equals(columnType)) {
			if (cache.mXValues.length <= index) {
				ret.status = false;
				return ret;
			}
			if (cache.mXValues[index] != value) {
				ret.prev = cache.mXValues[index];
				cache.mXValues[index] = value;
				ret.status = true;
			}
		} else if (Y_VALUE.equals(columnType)) {
			if (cache.mYValues.length <= index) {
				ret.status = false;
				return ret;
			}
			if (cache.mYValues[index] != value) {
				ret.prev = cache.mYValues[index];
				cache.mYValues[index] = value;
				ret.status = true;
			}
		}
		return ret;
	}

	@Override
    protected List<Dimension> getToolTipNotSpatiallyVariedDimensionList() {
    	Set<Dimension> dimSet = new HashSet<Dimension>();
    	dimSet.addAll(this.mXVariable.getDimensions());
    	dimSet.addAll(this.mYVariable.getDimensions());
    	if (this.isErrorBarAvailable()) {
        	dimSet.addAll(this.mLowerErrorVariable.getDimensions());
        	dimSet.addAll(this.mUpperErrorVariable.getDimensions());
    	}
    	if (this.isTickLabelAvailable()) {
        	dimSet.addAll(this.mTickLabelVariable.getDimensions());
    	}
    	SGNetCDFVariable cVar = this.getCoordinateVariable();
    	dimSet.remove(cVar.getDimension(0));
    	return new ArrayList<Dimension>(dimSet);
    }

	@Override
	public String getToolTipSpatiallyVaried(final int index) {
		SGNetCDFVariable cVar = this.getCoordinateVariable();
		Dimension dim = cVar.getDimension(0);
		String name = dim.getName();
		Double value = null;
		try {
			value = this.getCoordinateValue(name, index);
		} catch (IOException e) {
		}
		
		// append to the string buffer
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append('=');
		sb.append(index);
		if (value != null) {
			sb.append(" (");
			sb.append(value);
			sb.append(")");
		}
    	return sb.toString();
    }
	
	@Override
    public String getToolTipSpatiallyVaried(final int index0, final int index1) {
		SGNetCDFVariable cVar = this.getCoordinateVariable();
		Dimension dim = cVar.getDimension(0);
		String name = dim.getName();
		Double value0 = null;
		try {
			value0 = this.getCoordinateValue(name, index0);
		} catch (IOException e) {
		}
		Double value1 = null;
		try {
			value1 = this.getCoordinateValue(name, index1);
		} catch (IOException e) {
		}
		
		// append to the string buffer
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append('=');
		sb.append(index0);
		sb.append("-");
		sb.append(index1);
		sb.append(" (");
		sb.append(value0);
		sb.append(" - ");
		sb.append(value1);
		sb.append(")");
    	return sb.toString();
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
		// do nothing
	}

	@Override
	public void restoreCache() {
		final boolean all = false;
		final boolean useCache = true;
    	this.getXValueArray(all, useCache);
    	this.getYValueArray(all, useCache);
    	if (this.isErrorBarAvailable()) {
    		this.getLowerErrorValueArray(all, useCache);
    		this.getUpperErrorValueArray(all, useCache);
    	}
    	if (this.isTickLabelAvailable()) {
    		this.getStringArray(all, useCache);
    	}
	}

	@Override
    public Double getXValueAt(final int index) {
		double[] values = this.getXValueArray(false);
		final double d = values[index];
		/*
		if (this.isNaNAssignedInvalidValue(0, index, X_VALUE, d)) {
			return this.getValueAt(this.mXVariable, index);
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
			return this.getValueAt(this.mYVariable, index);
		} else {
			return d;
		}
		*/
		return d;
	}
	
	@Override
	public SGDataValue getDataValue(final int index) {
		return SGDataUtility.getDataValue(this, index);
	}

	@Override
	public SGDataValue getDataValue(final int index0, final int index1) {
		return SGDataUtility.getDataValue(this, index0, index1);
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
	public double[] getLowerErrorValueArray(boolean all, boolean useCache) {
		return this.getLowerErrorValueArray(all, useCache, true);
	}

	@Override
	public double[] getUpperErrorValueArray(boolean all, boolean useCache) {
		return this.getUpperErrorValueArray(all, useCache, true);
	}

	@Override
    public boolean hasDateTypeXVariable() {
		return (this.mXVariable instanceof SGDateVariable);
	}
    
	@Override
    public boolean hasDateTypeYVariable() {
		return (this.mYVariable instanceof SGDateVariable);
	}

	@Override
	protected boolean matches(final int col, final int row, String columnType,
			SGDataValueHistory value, final double d) {
		return SGDataUtility.matches(row, columnType, value, d);
	}

	@Override
	public void addMultipleDimensionEditedDataValue(SGDataValueHistory dataValue) {
		SGDataValueHistory.NetCDF.MD1 mdValue = (SGDataValueHistory.NetCDF.MD1) dataValue;
		SGDataValueHistory prev = mdValue.getPreviousValue();
		SGDataValueHistory.NetCDF.D1 dValue;
		if (prev != null) {
			dValue = new SGDataValueHistory.NetCDF.D1(
					mdValue.getValue(), mdValue.getColumnType(), 
					mdValue.getIndex(), mdValue.getVarName(), prev.getValue());
		} else {
			dValue = new SGDataValueHistory.NetCDF.D1(
					mdValue.getValue(), mdValue.getColumnType(), 
					mdValue.getIndex(), mdValue.getVarName());
		}
		this.mEditedDataValueList.add(dValue);
	}

	@Override
    protected Array setEditedValues(NetcdfFileWriteable ncWrite,
    		String varName, Array array, final boolean all) {
		// do nothing
    	return array;
    }

}
