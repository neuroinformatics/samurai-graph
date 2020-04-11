package jp.riken.brain.ni.samuraigraph.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;

import org.w3c.dom.Element;

import ucar.nc2.NetcdfFileWriteable;
import ch.systemsx.cisd.base.mdarray.MDDoubleArray;
import ch.systemsx.cisd.hdf5.IHDF5Writer;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;

/**
 * The class of scalar XY type data for multidimensional data file.
 *
 */
public class SGSXYMDArrayData extends SGMDArrayData implements SGISXYTypeSingleData, SGIDataColumnTypeConstants,
		SGIMDArrayConstants {

    /**
     * The variable for x-values.
     */
    protected SGMDArrayVariable mXVariable = null;

    /**
     * The variable for y-values.
     */
    protected SGMDArrayVariable mYVariable = null;

    /**
     * The variable for lower error values.
     */
    protected SGMDArrayVariable mLowerErrorVariable = null;

    /**
     * The variable for upper error values.
     */
    protected SGMDArrayVariable mUpperErrorVariable = null;

    /**
     * The variable for values that holds error values.
     */
    protected SGMDArrayVariable mErrorBarHolderVariable = null;

    /**
     * The variable for tick labels.
     */
    protected SGMDArrayVariable mTickLabelVariable = null;

    /**
     * The variable for values that holds tick labels.
     */
    protected SGMDArrayVariable mTickLabelHolderVariable = null;

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
     *
     */
	public SGSXYMDArrayData() {
		super();
	}

    /**
     * Builds a data object.
     *
     * @param mdFile
     *            multidimensional data file
     * @param obs
     *            data observer
     * @param xInfo
     *            information of x values
     * @param yInfo
     *            information of y values
     * @param leInfo
     *            information of lower error values
     * @param ueInfo
     *            information of upper error values
     * @param ehInfo
     *            information of the error bar holder, which must be a variable for x or y values
     * @param tlInfo
     *            information of the tick label
     * @param thInfo
     *            information of the tick label holder, which must be a variable for x or y values
     * @param stride
     *            stride of an array
     * @param tickLabelStride
     *            stride for tick label
     * @param strideAvailable
     *            flag whether to set available the stride
     */
	public SGSXYMDArrayData(final SGMDArrayFile mdFile, final SGDataSourceObserver obs,
			final SGMDArrayDataColumnInfo xInfo, final SGMDArrayDataColumnInfo yInfo,
            final SGMDArrayDataColumnInfo leInfo, final SGMDArrayDataColumnInfo ueInfo,
            final SGMDArrayDataColumnInfo ehInfo, final SGMDArrayDataColumnInfo tlInfo,
            final SGMDArrayDataColumnInfo thInfo, final SGIntegerSeriesSet stride,
			final SGIntegerSeriesSet tickLabelStride, final boolean strideAvailable) {

		super(mdFile, obs, strideAvailable);

        if (xInfo == null && yInfo == null) {
            throw new IllegalArgumentException("xInfo == null && yInfo == null");
        }
    	final String xName = (xInfo != null) ? xInfo.getName() : null;
    	final String yName = (yInfo != null) ? yInfo.getName() : null;

        if (!(leInfo == null && ueInfo == null)
        		&& !(leInfo != null && ueInfo != null)) {
            throw new IllegalArgumentException("leInfo = " + leInfo + ", ueInfo = " + ueInfo);
        }
        if (leInfo != null && ehInfo == null) {
            throw new IllegalArgumentException("Error bar holder is null.");
        }
        if (ehInfo != null) {
        	if (!ehInfo.getName().equals(xName) && !ehInfo.getName().equals(yName)) {
                throw new IllegalArgumentException("Error bar holder must be the same as the x or y variable.");
        	}
        }
        if (tlInfo != null && thInfo == null) {
            throw new IllegalArgumentException("Tick label holder is null.");
        }
        if (thInfo != null) {
        	if (!thInfo.getName().equals(xName) && !thInfo.getName().equals(yName)) {
                throw new IllegalArgumentException("Tick label holder must be the same as the x or y variable.");
        	}
        }

        //
        // Checks variables.
        //

        final int xLen = (xInfo != null) ? xInfo.getGenericDimensionLength() : yInfo.getGenericDimensionLength();
        if (xLen == -1) {
        	throw new IllegalArgumentException("Invalid length: " + xLen);
        }
        final int yLen = (yInfo != null) ? yInfo.getGenericDimensionLength() : xInfo.getGenericDimensionLength();
        if (yLen == -1) {
        	throw new IllegalArgumentException("Invalid length: " + yLen);
        }
        if (xLen != yLen) {
        	throw new IllegalArgumentException("Length of data is different: " + xLen + ", " + yLen);
        }
        if (leInfo != null) {
            final int lLen = leInfo.getGenericDimensionLength();
            if (lLen != xLen) {
            	throw new IllegalArgumentException("Length of data is different: " + xLen + ", " + lLen);
            }
        }
        if (ueInfo != null) {
            final int uLen = ueInfo.getGenericDimensionLength();
            if (uLen != xLen) {
            	throw new IllegalArgumentException("Length of data is different: " + xLen + ", " + uLen);
            }
        }
        if (tlInfo != null) {
            final int tLen = tlInfo.getGenericDimensionLength();
            if (tLen != xLen) {
            	throw new IllegalArgumentException("Length of data is different: " + xLen + ", " + tLen);
            }
        }

        // find variables
        SGMDArrayVariable xVar = this.initVariable(mdFile, xInfo);
        SGMDArrayVariable yVar = this.initVariable(mdFile, yInfo);
        SGMDArrayVariable leVar = null;
        if (leInfo != null) {
        	leVar = this.initVariable(mdFile, leInfo);
        }
        SGMDArrayVariable ueVar = null;
        if (ueInfo != null) {
        	ueVar = this.initVariable(mdFile, ueInfo);
        }
        SGMDArrayVariable ehVar = null;
        if (ehInfo != null) {
        	final String ehName = ehInfo.getName();
        	if (ehName.equals(xName)) {
        		ehVar = xVar;
        	} else if (ehName.equals(yName)) {
        		ehVar = yVar;
        	} else {
        		throw new IllegalArgumentException("Error bar holder is not equal to x nor y variable.");
        	}
        }
        SGMDArrayVariable tlVar = null;
        if (tlInfo != null) {
        	tlVar = this.initVariable(mdFile, tlInfo);
        }
        SGMDArrayVariable thVar = null;
        if (thInfo != null) {
        	final String thName = thInfo.getName();
        	if (thName.equals(xName)) {
        		thVar = xVar;
        	} else if (thName.equals(yName)) {
        		thVar = yVar;
        	} else {
        		throw new IllegalArgumentException("Tick label holder is not equal to x nor y variable.");
        	}
        }

        // set to the attributes
        this.mXVariable = xVar;
        this.mYVariable = yVar;
    	this.mLowerErrorVariable = leVar;
    	this.mUpperErrorVariable = ueVar;
    	this.mErrorBarHolderVariable = ehVar;
    	this.mTickLabelVariable = tlVar;
    	this.mTickLabelHolderVariable = thVar;
    	final int len = (xVar != null) ? xVar.getGenericDimensionLength() : yVar.getGenericDimensionLength();
        this.mStride = this.createStride(stride, len);
        this.mTickLabelStride = this.createStride(tickLabelStride, len);
        this.initTimeStride();
	}

    // Overrode to set pick up dimension index.
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

    /**
     * Returns a text string of data type.
     *
     * @return a text string of data type
     */
	@Override
	public String getDataType() {
		SGIDataSource src = this.getDataSource();
		if (src instanceof SGHDF5File) {
	        return SGDataTypeConstants.SXY_HDF5_DATA;
		} else if (src instanceof SGMATLABFile) {
	        return SGDataTypeConstants.SXY_MATLAB_DATA;
		} else if (src instanceof SGVirtualMDArrayFile) {
	        return SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA;
		} else {
			throw new Error("This cannot happen.");
		}
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
    	SGMDArrayVariable lVar = null;
    	SGMDArrayVariable uVar = null;
    	SGMDArrayVariable tVar = null;
    	String lCol = null;
    	String uCol = null;
    	String tCol = null;

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
            } else if ("".equals(columns[ii])) {
            	continue;
            } else {
            	return false;
            }
        }
        if (xVar == null && yVar == null) {
            return false;
        }
        if (!((lVar != null) && (uVar != null)) && !((lVar == null) && (uVar == null))) {
            return false;
        }

        SGMDArrayVariable ehVar = null;
        if (lCol != null && uCol != null) {
            Integer ehle = SGDataUtility.getColumnIndexOfAppendedColumnTitle(lCol, this);
            Integer ehue = SGDataUtility.getColumnIndexOfAppendedColumnTitle(uCol, this);
            if (ehle == null || ehue == null || ehle.equals(ehue)==false) {
                return false;
            }
            ehVar = vars[ehle];
        }

        SGMDArrayVariable thVar = null;
        if (tCol != null) {
            Integer th = SGDataUtility.getColumnIndexOfAppendedColumnTitle(tCol, this);
            if (th == null) {
                return false;
            }
            thVar = vars[th];
        }

        // set variables
        this.mXVariable = xVar;
        this.mYVariable = yVar;
        this.mLowerErrorVariable = lVar;
        this.mUpperErrorVariable = uVar;
        this.mErrorBarHolderVariable = ehVar;
        this.mTickLabelVariable = tVar;
        this.mTickLabelHolderVariable = thVar;

        return true;
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
		this.mLowerErrorVariable = null;
		this.mUpperErrorVariable = null;
		this.mErrorBarHolderVariable = null;
		this.mTickLabelVariable = null;
		this.mTickLabelHolderVariable = null;
		this.mStride = null;
		this.mTickLabelStride = null;
		this.mShift = null;
	}

    /**
     * Returns whether error bars are available.
     *
     * @return true if error bars are available
     */
	@Override
	public boolean isErrorBarAvailable() {
        return (this.mLowerErrorVariable != null && this.mUpperErrorVariable != null);
	}

    /**
     * Returns whether tick labels are available.
     *
     * @return true if tick labels are available
     */
	@Override
	public boolean isTickLabelAvailable() {
        return (this.mTickLabelVariable != null);
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
	@Override
	public Boolean isTickLabelHorizontal() {
    	if (this.isTickLabelAvailable()) {
    		return (this.mTickLabelHolderVariable.equals(this.mYVariable));
    	} else {
    		return null;
    	}
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

	@Override
	public boolean useValueCache(final boolean all) {
		if (!all) {
			return true;
		}
		if (!this.isStrideAvailable()) {
			return true;
		}
		return this.mStride.isComplete();
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
	public double[] getXValueArray(final boolean all, final boolean useCache) {
		double[] ret = null;
    	final boolean b = (all || !this.isStrideAvailable());
		if (this.mXVariable != null) {
			if (b) {
				ret = this.mXVariable.getAllGenericNumberArray();
			} else {
				ret = this.mXVariable.getGenericNumberArray(this.mStride);
			}
		} else {
			if (b) {
				final int len = this.mYVariable.getGenericDimensionLength();
				ret = this.getIndexValues(len);
			} else {
				ret = this.getIndexValues(this.mStride);
			}
		}
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
	public double[] getYValueArray(final boolean all, final boolean useCache) {
		double[] ret = null;
    	final boolean b = (all || !this.isStrideAvailable());
		if (this.mYVariable != null) {
			if (b) {
				ret = this.mYVariable.getAllGenericNumberArray();
			} else {
				ret = this.mYVariable.getGenericNumberArray(this.mStride);
			}
		} else {
			if (b) {
				final int len = this.mXVariable.getGenericDimensionLength();
				ret = this.getIndexValues(len);
			} else {
				ret = this.getIndexValues(this.mStride);
			}
		}
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
	public double[] getLowerErrorValueArray(final boolean all, final boolean useCache) {
		double[] ret = null;
    	if (all || !this.isStrideAvailable()) {
        	ret = this.mLowerErrorVariable.getAllGenericNumberArray();
    	} else {
        	ret = this.mLowerErrorVariable.getGenericNumberArray(
        			this.mStride);
    	}
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
	public double[] getUpperErrorValueArray(final boolean all, final boolean useCache) {
		double[] ret = null;
    	if (all || !this.isStrideAvailable()) {
    		ret = this.mUpperErrorVariable.getAllGenericNumberArray();
    	} else {
        	ret = this.mUpperErrorVariable.getGenericNumberArray(
        			this.mStride);
    	}
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
		final boolean b = (all || !this.isStrideAvailable());
		if (this.mTickLabelVariable.isNumberVariable()) {
	        double[] dArray;
	        if (b) {
		        dArray = this.mTickLabelVariable.getAllGenericNumberArray();
	        } else {
		        dArray = this.mTickLabelVariable.getGenericNumberArray(this.mTickLabelStride);
	        }
	        ret = SGUtilityNumber.getStringArray(dArray, this.mDecimalPlaces, this.mExponent);
		} else {
	        if (b) {
	        	ret = this.mTickLabelVariable.getAllGenericStringArray();
	        } else {
	        	ret = this.mTickLabelVariable.getGenericStringArray(this.mTickLabelStride);
	        }
		}
        if (useCache) {
        	SGSXYDataCache.setTickLabels(this, ret);
        }
        return ret;
	}

    /**
     * Returns whether a given data object has the same tick label resources.
     *
     * @param data
     *           a data to compare
     * @return true if a given data object has the same tick label resources
     */
	@Override
	public boolean hasEqualTickLabelResource(SGISXYTypeSingleData data) {
    	if (!(data instanceof SGSXYMDArrayData)) {
    		return false;
    	}
    	SGSXYMDArrayData dataSXY = (SGSXYMDArrayData) data;
    	if (this.mTickLabelVariable == null) {
    		return (dataSXY.mTickLabelVariable == null);
    	} else {
        	return this.mTickLabelVariable.equals(dataSXY.mTickLabelVariable);
    	}
	}

    protected SGMDArrayDataColumnInfo createErrorBarInfo(SGMDArrayVariable var, String nameStr) {
		if (this.isErrorBarAvailable()) {
			return SGDataUtility.createErrorBarInfo(var, nameStr, this.mLowerErrorVariable,
					this.mUpperErrorVariable, this.mErrorBarHolderVariable);
		} else {
			return null;
		}
    }

    protected SGMDArrayDataColumnInfo getErrorBarHolderInfo(SGMDArrayDataColumnInfo xInfo,
    		SGMDArrayDataColumnInfo yInfo) {
    	if (this.mErrorBarHolderVariable.equals(this.mXVariable)) {
    		return xInfo;
    	} else {
    		return yInfo;
    	}
    }

    protected SGMDArrayDataColumnInfo[] getErrorBarHolderInfo(SGMDArrayDataColumnInfo[] xInfo,
    		SGMDArrayDataColumnInfo[] yInfo) {
    	if (this.mErrorBarHolderVariable.equals(this.mXVariable)) {
    		return xInfo;
    	} else {
    		return yInfo;
    	}
    }

    protected SGMDArrayDataColumnInfo createTickLabelInfo(SGMDArrayVariable var) {
		if (this.isTickLabelAvailable()) {
			String thName = this.mTickLabelHolderVariable.getName();
			return SGDataUtility.createDataColumnInfo(this.mTickLabelVariable, TICK_LABEL, thName);
		} else {
			return null;
		}
    }

    protected SGMDArrayDataColumnInfo getTickLabelHolderInfo(SGMDArrayDataColumnInfo xInfo,
    		SGMDArrayDataColumnInfo yInfo) {
    	if (this.mTickLabelHolderVariable.equals(this.mXVariable)) {
    		return xInfo;
    	} else {
    		return yInfo;
    	}
    }

    protected SGMDArrayDataColumnInfo[] getTickLabelHolderInfo(SGMDArrayDataColumnInfo[] xInfo,
    		SGMDArrayDataColumnInfo[] yInfo) {
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
        final boolean be = this.isErrorBarAvailable();
        final boolean bt = this.isTickLabelAvailable();
		SGMDArrayDataColumnInfo[] x = SGDataUtility.createDataColumnInfoArray(this.mXVariable, X_VALUE);
		SGMDArrayDataColumnInfo[] y = SGDataUtility.createDataColumnInfoArray(this.mYVariable, Y_VALUE);
		SGMDArrayDataColumnInfo[] le = be ? new SGMDArrayDataColumnInfo[] { this.createErrorBarInfo(
				this.mLowerErrorVariable, LOWER_ERROR_VALUE) } : null;
		SGMDArrayDataColumnInfo[] ue = be ? new SGMDArrayDataColumnInfo[] { this.createErrorBarInfo(
				this.mUpperErrorVariable, UPPER_ERROR_VALUE) } : null;
		SGMDArrayDataColumnInfo[] eh = be ? this.getErrorBarHolderInfo(x, y) : null;
		SGMDArrayDataColumnInfo[] t = bt ? new SGMDArrayDataColumnInfo[] { SGDataUtility
				.createDataColumnInfo(this.mTickLabelVariable, TICK_LABEL,
						this.mTickLabelHolderVariable.getName()) } : null;
		SGMDArrayDataColumnInfo[] th = bt ? this.getTickLabelHolderInfo(x, y) : null;
        SGSXYMDArrayMultipleData data = new SGSXYMDArrayMultipleData(
        		this.getMDArrayFile(), this.getDataSourceObserver(),
                x, y, le, ue, eh, t, th, this.mStride, this.mTickLabelStride,
                this.isStrideAvailable());
        data.setDecimalPlaces(this.mDecimalPlaces);
        data.setExponent(this.mExponent);
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
     * @param pickupYVariable
     *            true to pick up the y variable, and false to pick up the x variable
     * @param pickUpDimensionIndex
     *            the dimension index to pick up
     * @return a transformed data
     */
    public SGISXYTypeMultipleData toMultiple(final boolean pickupYVariable,
    		final int pickUpDimensionIndex, final int len) {
    		SGMDArrayDataColumnInfo x = SGDataUtility.createDataColumnInfo(this.mXVariable, X_VALUE);
    		SGMDArrayDataColumnInfo y = SGDataUtility.createDataColumnInfo(this.mYVariable, Y_VALUE);
			final boolean be = this.isErrorBarAvailable();
			final boolean bt = this.isTickLabelAvailable();
			SGMDArrayDataColumnInfo le = be ? SGDataUtility.createDataColumnInfo(this.mLowerErrorVariable,
					LOWER_ERROR_VALUE) : null;
			SGMDArrayDataColumnInfo ue = be ? SGDataUtility.createDataColumnInfo(this.mUpperErrorVariable,
					UPPER_ERROR_VALUE) : null;
			SGMDArrayDataColumnInfo t = bt ? SGDataUtility.createDataColumnInfo(this.mTickLabelVariable,
					TICK_LABEL) : null;
			SGMDArrayDataColumnInfo pickUpInfo = pickupYVariable ? y : x;
			pickUpInfo.setDimensionIndex(KEY_SXY_PICKUP_DIMENSION, pickUpDimensionIndex);
			final int origin = pickUpInfo.getOrigins()[pickUpDimensionIndex];
			SGIntegerSeriesSet pickUpIndices;
			if (origin == len - 1) {
				pickUpIndices = SGIntegerSeriesSet.parse(SGIntegerSeries.ARRAY_INDEX_END, len);
			} else {
				pickUpIndices = new SGIntegerSeriesSet(origin);
			}
			SGSXYMDArrayMultipleData data = new SGSXYMDArrayMultipleData(
					this.getMDArrayFile(), this.getDataSourceObserver(), x, y,
					le, ue, t, pickUpIndices, this.mStride, this.mTickLabelStride,
					this.isStrideAvailable());
	        data.setDecimalPlaces(this.mDecimalPlaces);
	        data.setExponent(this.mExponent);
			return data;
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
                final int index = this.getVariableIndex(this.mErrorBarHolderVariable.getName());
                if (index == -1) {
                    return null;
                }
                array[ii] = SGDataUtility.appendColumnTitle(name, this.mErrorBarHolderVariable.getName());
            } else if (var.equals(this.mTickLabelVariable)) {
                final int index = this.getVariableIndex(this.mTickLabelHolderVariable.getName());
                if (index == -1) {
                    return null;
                }
                array[ii] = SGDataUtility.appendColumnTitle(TICK_LABEL, this.mTickLabelHolderVariable.getName());
            } else {
                array[ii] = "";
            }
        }
        return array;
	}

    /**
     * A class of scalar XY multidimensional data properties.
     */
    public static class SXYMDDataProperties extends MDArrayDataProperties {

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
        public SXYMDDataProperties() {
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
            if ((obj instanceof SXYMDDataProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
            	return false;
            }
        	return true;
        }
        
    	@Override
        public boolean hasEqualSize(DataProperties dp) {
            if ((dp instanceof SXYMDDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualSize(dp)) {
            	return false;
            }
            SXYMDDataProperties p = (SXYMDDataProperties) dp;
            if (SGUtility.equals(this.mStride, p.mStride) == false) {
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
            if ((dp instanceof SXYMDDataProperties) == false) {
                return false;
            }
//            if (super.hasEqualColumnTypes(dp) == false) {
//            	return false;
//            }
            SXYMDDataProperties p = (SXYMDDataProperties) dp;
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
         * Copy this object.
         *
         * @return a copied object
         */
        public Object copy() {
        	SXYMDDataProperties p = (SXYMDDataProperties) super.copy();
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
        SGProperties p = new SXYMDDataProperties();
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
    public boolean getProperties(SGProperties p) {
        if (!(p instanceof SXYMDDataProperties)) {
            return false;
        }
        if (!super.getProperties(p)) {
        	return false;
        }
        SXYMDDataProperties sp = (SXYMDDataProperties) p;
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

    /**
     * Sets the properties to this data.
     *
     * @param p
     *          properties to set
     * @return true if succeeded
     */
	@Override
	public boolean setProperties(SGProperties p) {
        if (!(p instanceof SXYMDDataProperties)) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        SXYMDDataProperties sp = (SXYMDDataProperties) p;
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

	@Override
	public boolean writeProperty(Element el, SGExportParameter type) {
		// do nothing
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
        if (!(data instanceof SGSXYMDArrayData)) {
            throw new IllegalArgumentException("Given data is not the instance of SGSXYMDData class.");
        }
        if (super.setData(data) == false) {
            return false;
        }
        SGSXYMDArrayData mdData = (SGSXYMDArrayData) data;
        this.mXVariable = copyVariable(mdData.mXVariable);
        this.mYVariable = copyVariable(mdData.mYVariable);
        this.mLowerErrorVariable = copyVariable(mdData.mLowerErrorVariable);
        this.mUpperErrorVariable = copyVariable(mdData.mUpperErrorVariable);
        this.mErrorBarHolderVariable = copyVariable(mdData.mErrorBarHolderVariable);
        this.mTickLabelVariable = copyVariable(mdData.mTickLabelVariable);
        this.mTickLabelHolderVariable = copyVariable(mdData.mTickLabelHolderVariable);
        this.mDecimalPlaces = mdData.mDecimalPlaces;
        this.mExponent = mdData.mExponent;
        this.setStride(mdData.mStride);
        this.setTickLabelStride(mdData.mTickLabelStride);
        this.setShift(mdData.mShift);
        return true;
    }

    /**
     * Returns the copy of this data object.
     *
     * @return
     *         a copy of this data object
     */
    public Object clone() {
        SGSXYMDArrayData data = (SGSXYMDArrayData) super.clone();
        data.mXVariable = copyVariable(this.mXVariable);
        data.mYVariable = copyVariable(this.mYVariable);
        data.mLowerErrorVariable = copyVariable(this.mLowerErrorVariable);
        data.mUpperErrorVariable = copyVariable(this.mUpperErrorVariable);
        data.mErrorBarHolderVariable = copyVariable(this.mErrorBarHolderVariable);
        data.mTickLabelVariable = copyVariable(this.mTickLabelVariable);
        data.mTickLabelHolderVariable = copyVariable(this.mTickLabelHolderVariable);
        data.mDecimalPlaces = this.mDecimalPlaces;
        data.mExponent = this.mExponent;
        data.mStride = this.getStride();
        data.mTickLabelStride = this.getTickLabelStride();
        data.mShift = (SGTuple2d) this.mShift.clone();
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
    	SGMDArrayVariable var = (this.mXVariable != null) ? this.mXVariable : this.mYVariable;
    	return var.getGenericDimensionLength();
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
		if (this.mXVariable != null) {
			varList.add(this.mXVariable);
		}
		if (this.mYVariable != null) {
			varList.add(this.mYVariable);
		}
		if (this.mLowerErrorVariable != null) {
			varList.add(this.mLowerErrorVariable);
		}
		if (this.mUpperErrorVariable != null) {
			varList.add(this.mUpperErrorVariable);
		}
		if (this.mTickLabelVariable != null) {
			varList.add(this.mTickLabelVariable);
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
    	// do nothing
    	return true;
    }

    /**
     * Writes data to a netCDF file.
     *
     * @param ncWrite
     *           a netCDF file
     * @return true if succeeded
     */
	@Override
    protected boolean writeData(NetcdfFileWriteable ncWrite) {
    	// do nothing
    	return true;
    }

    /**
     * Returns a text string of the data type to save into a NetCDF data set file.
     *
     * @return a text string of the data type to save into a NetCDF data set file
     */
    @Override
    public String getNetCDFDataSetDataType() {
    	return SGDataTypeConstants.SXY_NETCDF_DATA;
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
     * Exports to a HDF5 file.
     * 
     * @param writer
     *           HDF5-file writer
     * @return true if succeeded
     */
	@Override
    protected boolean exportToHDF5(IHDF5Writer writer, final SGExportParameter mode, SGDataBufferPolicy policy) {
		// do nothing
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
			ret = this.mStride;
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
    	SGMDArrayFile mdfile = (SGMDArrayFile) src;
    	this.mXVariable = this.findVariable(mdfile, this.mXVariable);
		this.mYVariable = this.findVariable(mdfile, this.mYVariable);
		if (this.isErrorBarAvailable()) {
			this.mLowerErrorVariable = this.findVariable(
					mdfile, this.mLowerErrorVariable);
			this.mUpperErrorVariable = this.findVariable(
					mdfile, this.mUpperErrorVariable);
			this.mErrorBarHolderVariable = this.findVariable(
					mdfile, this.mErrorBarHolderVariable);
		}
		if (this.isTickLabelAvailable()) {
			this.mTickLabelVariable = this.findVariable(
					mdfile, this.mTickLabelVariable);
			this.mTickLabelHolderVariable = this.findVariable(
					mdfile, this.mTickLabelHolderVariable);
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
	public void setDataViewerValue(String columnType, int row, int col,
			Object value) {
		// do nothing
	}

    @Override
    public SGIntegerSeriesSet getIndexStride() {
    	return this.getStride();
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
	public String getToolTipSpatiallyVaried(final int index) {
    	return this.getToolTipSpatiallyVaried(index, this.mStride);
    }
	
	@Override
    public String getToolTipSpatiallyVaried(final int index0, final int index1) {
    	StringBuffer sb = new StringBuffer();
		SGMDArrayVariable[] vars = this.getAssignedVariables();
		for (int ii = 0; ii < vars.length; ii++) {
			if (ii > 0) {
				sb.append(",<br>");
			}
			sb.append(this.getToolTipSpatiallyVaried(vars[ii], index0, index1));
		}
    	return sb.toString();
    }

	private String getToolTipSpatiallyVaried(SGMDArrayVariable var, 
			final int arrayIndex0, final int arrayIndex1) {
		StringBuffer sb = new StringBuffer();
		int[] origins = var.getOrigins();
		final int dim = var.getGenericDimensionIndex();
		sb.append(var.getName());
		sb.append("=[");
		for (int ii = 0; ii < origins.length; ii++) {
			if (ii > 0) {
				sb.append(',');
			}
			if (ii == dim) {
				sb.append(arrayIndex0);
				sb.append('-');
				sb.append(arrayIndex1);
			} else {
    			sb.append(origins[ii]);
			}
		}
		sb.append(']');
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
		return values[index];
	}
    
	@Override
    public Double getYValueAt(final int index) {
		double[] values = this.getYValueArray(false);
		return values[index];
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
	public double[] getLowerErrorValueArray(boolean all, boolean useCache,
			final boolean removeInvalidValues) {
		return this.getLowerErrorValueArray(all, useCache);
	}

	@Override
	public double[] getUpperErrorValueArray(boolean all, boolean useCache,
			boolean removeInvalidValues) {
		return this.getUpperErrorValueArray(all, useCache);
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
		return SGDataUtility.matches(row, columnType, value, d);
	}

	@Override
	public void addMultipleDimensionEditedDataValue(SGDataValueHistory dataValue) {
		SGDataValueHistory.MDArray.MD1 mdValue = (SGDataValueHistory.MDArray.MD1) dataValue;
		SGDataValueHistory prev = mdValue.getPreviousValue();
		SGDataValueHistory.MDArray.D1 dValue;
		if (prev != null) {
			dValue = new SGDataValueHistory.MDArray.D1(
					mdValue.getValue(), mdValue.getColumnType(), 
					mdValue.getIndex(), mdValue.getVarName(), 
					mdValue.getPreviousValue().getValue());
		} else {
			dValue = new SGDataValueHistory.MDArray.D1(
					mdValue.getValue(), mdValue.getColumnType(), 
					mdValue.getIndex(), mdValue.getVarName());
		}
		this.mEditedDataValueList.add(dValue);
	}

	@Override
    protected MDDoubleArray setEditedValues(IHDF5Writer writer,
    		SGMDArrayVariable var, MDDoubleArray array) {
		return array;
	}

	@Override
    protected MLDouble setEditedValues(SGMDArrayVariable var, MLDouble array) {
    	return array;
    }

}
