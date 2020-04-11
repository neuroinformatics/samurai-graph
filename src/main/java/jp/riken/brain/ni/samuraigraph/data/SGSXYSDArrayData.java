package jp.riken.brain.ni.samuraigraph.data;

import java.io.File;
import java.io.IOException;
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
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;

import org.w3c.dom.Element;

import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFileWriteable;

/**
 * Scalar type XY data. This object has of x and y values. If given, this object
 * can keep error bar values and text strings.
 *
 */
public class SGSXYSDArrayData extends SGSDArrayData implements SGISXYTypeSingleData,
        SGIDataColumnTypeConstants, SGIDataPropertyKeyConstants, SGIConstants {

    /**
     * The column index for x-values.
     */
    protected Integer mXIndex = null;

    /**
     * The column index for y-values.
     */
    protected Integer mYIndex = null;

    /**
     * The column index for lower error values.
     */
    protected Integer mLowerErrorIndex = null;

    /**
     * The column index for upper error values.
     */
    protected Integer mUpperErrorIndex = null;

    /**
     * The column index for values that holds error values.
     */
    protected Integer mErrorBarHolderIndex = null;

    /**
     * The column index for tick labels.
     */
    protected Integer mTickLabelIndex = null;

    /**
     * The column index for values that holds tick labels.
     */
    protected Integer mTickLabelHolderIndex = null;

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
	 * The stride for the tick labels.
	 */
	protected SGIntegerSeriesSet mTickLabelStride = null;

	/**
	 * The shift value.
	 */
	private SGTuple2d mShift = new SGTuple2d();

	/**
	 * Sampling rate.
	 */
	private Double mSamplingRate = null;

    /**
     * Builds a data object.
     *
     */
    public SGSXYSDArrayData() {
        super();
    }

	/**
	 * Builds a data object with given columns and indices.
	 *
	 * @param dataFile
	 *            the text data file
	 * @param obs
	 *            an observer for array data
	 * @param xIndex
	 *            the column index for x-values
	 * @param yIndex
	 *            the column index for y-values
	 * @param lowerErrorIndex
	 *            the column index for lower error values
	 * @param upperErrorIndex
	 *            the column index for upper error values
	 * @param errorBarHolderIndex
	 *            the column index for values that holds error values
	 * @param tickLabelIndex
	 *            the column index for strings
	 * @param tickLabelHolderIndex
	 *            the column index for values that holds tick labels
	 * @param stride
	 *            stride of an array
	 * @param tickLabelStride
	 *            stride for tick label
	 * @param strideAvailable
	 *            flag whether to set available the stride
	 */
    public SGSXYSDArrayData(
            final SGSDArrayFile dataFile,
            final SGDataSourceObserver obs,
            final Integer xIndex, final Integer yIndex,
            final Integer lowerErrorIndex,
            final Integer upperErrorIndex,
            final Integer errorBarHolderIndex,
            final Integer tickLabelIndex,
            final Integer tickLabelHolderIndex, final SGIntegerSeriesSet stride,
			final SGIntegerSeriesSet tickLabelStride, final boolean strideAvailable,
			final Double samplingRate) {

        super(dataFile, obs, strideAvailable);

        // null check
        if (xIndex == null || yIndex == null) {
            throw new IllegalArgumentException("Indices for x and y values must not be null.");
        }
        if (!(lowerErrorIndex == null && upperErrorIndex == null) && !(lowerErrorIndex != null && upperErrorIndex != null)) {
            throw new IllegalArgumentException(
                    "A pair of indices for the lower and upper error bars as (null, null) or (not null, not null) are permitted.");
        }
        if (lowerErrorIndex != null && errorBarHolderIndex == null) {
			throw new IllegalArgumentException("Error bar holder index is null.");
		}
		if (tickLabelIndex != null && tickLabelHolderIndex == null) {
			throw new IllegalArgumentException("Tick label holder index is null.");
		}

        // x values
        if (this.checkColumnIndexRange(xIndex) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + xIndex);
        }

        // y values
        if (this.checkColumnIndexRange(yIndex) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + yIndex);
        }

        // error values
        if (lowerErrorIndex != null) {
            if (this.checkColumnIndexRange(lowerErrorIndex) == false) {
                throw new IllegalArgumentException("Index out of bounds: " + lowerErrorIndex);
            }
        }
        if (upperErrorIndex != null) {
            if (this.checkColumnIndexRange(upperErrorIndex) == false) {
                throw new IllegalArgumentException("Index out of bounds: " + upperErrorIndex);
            }
        }
        if (errorBarHolderIndex != null) {
            if (this.checkColumnIndexRange(errorBarHolderIndex) == false) {
                throw new IllegalArgumentException("Index out of bounds: " + errorBarHolderIndex);
            }
            if (!errorBarHolderIndex.equals(xIndex) && !errorBarHolderIndex.equals(yIndex)) {
                throw new IllegalArgumentException("Error holder value index must be equal to x value index or y value index.");
            }
        }

        // strings
        if (tickLabelIndex != null) {
            if (this.checkColumnIndexRange(tickLabelIndex) == false) {
                throw new IllegalArgumentException("Index out of bounds: " + tickLabelIndex);
            }
        }
        if (tickLabelHolderIndex != null) {
        	if (this.checkColumnIndexRange(tickLabelHolderIndex) == false) {
                throw new IllegalArgumentException("Index out of bounds: " + tickLabelHolderIndex);
        	}
        }

        // set indices
        this.mXIndex = xIndex;
        this.mYIndex = yIndex;
        this.mLowerErrorIndex = lowerErrorIndex;
        this.mUpperErrorIndex = upperErrorIndex;
        this.mErrorBarHolderIndex = errorBarHolderIndex;
        this.mTickLabelIndex = tickLabelIndex;
        this.mTickLabelHolderIndex = tickLabelHolderIndex;

        if (tickLabelIndex != null) {
            // initialize the decimal places and exponent when the column index for tick labels
            // are given and the column is of the number column
        	SGDataColumn[] columns = this.getDataFile().mDataColumns;
            SGDataColumn col = columns[tickLabelIndex.intValue()];
            if (col instanceof SGNumberDataColumn) {
                SGNumberDataColumn nCol = (SGNumberDataColumn) col;

                // set the maximum order to exponent
                final double[] values = nCol.getArray();
                int maxOrder = - Integer.MAX_VALUE;
                for (int ii = 0; ii < values.length; ii++) {
                    if (values[ii] == 0.0) {
                        continue;
                    }
                    if (Double.isNaN(values[ii])) {
                        continue;
                    }
                    final int order = SGUtilityNumber.getOrder(Math.abs(values[ii]));
                    if (order > maxOrder) {
                        maxOrder = order;
                    }
                }
                this.mExponent = maxOrder;

                // set the default value to decimal places
                this.mDecimalPlaces = 2;
            }

        } else {
        	// if tick labels are not set yet
        	this.setupTickLabel(xIndex, yIndex);
        }

        // set up the stride
        final int len = this.getAllPointsNumber();
        this.mStride = this.createStride(stride, len);
        SGIntegerSeriesSet tlStride = null;
        if (tickLabelStride != null) {
            tlStride = (SGIntegerSeriesSet) tickLabelStride.clone();
        } else {
    		tlStride = (SGIntegerSeriesSet) this.mStride.clone();
        }
        this.mTickLabelStride = tlStride;
        
        this.mSamplingRate = samplingRate;
    }
    
    /**
     * Returns the sampling rate.
     * 
     * @return the sampling rate
     */
    public Double getSamplingRate() {
    	return this.mSamplingRate;
    }

    private void setupTickLabel(Integer xIndex, Integer yIndex) {
    	SGDataColumn[] columns = this.getDataFile().mDataColumns;
        SGDataColumn xColumn = columns[xIndex.intValue()];
        SGDataColumn yColumn = columns[yIndex.intValue()];
    	if (xColumn instanceof SGDateDataColumn) {
    		this.mTickLabelIndex = xIndex;
    		this.mTickLabelHolderIndex = yIndex;
    	} else if (yColumn instanceof SGDateDataColumn) {
    		this.mTickLabelIndex = yIndex;
    		this.mTickLabelHolderIndex = xIndex;
    	}
    }

    /**
     * Disposes of this data object.
     * Do not call any method of this object after called this method.
     */
    public void dispose() {
        super.dispose();
        this.mXIndex = null;
        this.mYIndex = null;
        this.mLowerErrorIndex = null;
        this.mUpperErrorIndex = null;
        this.mErrorBarHolderIndex = null;
        this.mTickLabelIndex = null;
        this.mTickLabelHolderIndex = null;
        this.mTickLabelStride = null;
		this.mShift = null;
    }

    /**
     * Returns the copy of this data object.
     *
     * @return
     *         a copy of this data object
     */
    public Object clone() {
        SGSXYSDArrayData data = (SGSXYSDArrayData) super.clone();
        data.mTickLabelStride = this.getTickLabelStride();
        data.mShift = (SGTuple2d) this.mShift.clone();
        return data;
    }

    /**
     * Sets the data.
     *
     * @param data
     *             data set to this object
     * @return
     *             true if succeeded
     */
    public boolean setData(final SGData data) {
        if (!(data instanceof SGSXYSDArrayData)) {
            throw new IllegalArgumentException("!(data instanceof SGSXYData)");
        }
        if (super.setData(data) == false) {
            return false;
        }
        SGSXYSDArrayData dataSXY = (SGSXYSDArrayData) data;
        this.mXIndex = dataSXY.mXIndex;
        this.mYIndex = dataSXY.mYIndex;
        this.mLowerErrorIndex = dataSXY.mLowerErrorIndex;
        this.mUpperErrorIndex = dataSXY.mUpperErrorIndex;
        this.mErrorBarHolderIndex = dataSXY.mErrorBarHolderIndex;
        this.mTickLabelIndex = dataSXY.mTickLabelIndex;
        this.mTickLabelHolderIndex = dataSXY.mTickLabelHolderIndex;
        this.mStride = dataSXY.getTickLabelStride();
        this.setShift(dataSXY.mShift);
        return true;
    }

    /**
     * Returns the type of data.
     *
     * @return
     *         the type of data
     */
    public String getDataType() {
        return SGDataTypeConstants.SXY_DATA;
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
     * Returns a copy of x-value array.
     *
     * @return
     *         x-value array
     */
    @Override
    public double[] getXValueArray(final boolean all) {
    	return SGDataUtility.getXValueArray(this, all);
    }
    
	@Override
    public double[] getXValueArray(final boolean all, final boolean useCache) {
    	double[] ret = null;
    	final boolean b = (all || !this.isStrideAvailable());
    	ret = this.getNumberArray(this.mXIndex, b);
    	if (useCache) {
            SGSXYDataCache.setXValues(this, ret);
    	}
    	return ret;
    }

    /**
     * Returns a copy of y-value array.
     *
     * @return
     *         y-value array
     */
    @Override
    public double[] getYValueArray(final boolean all) {
    	return SGDataUtility.getYValueArray(this, all);
    }
    
	@Override
    public double[] getYValueArray(final boolean all, final boolean useCache) {
    	double[] ret = null;
    	final boolean b = (all || !this.isStrideAvailable());
    	ret = this.getNumberArray(this.mYIndex, b);
    	if (useCache) {
            SGSXYDataCache.setYValues(this, ret);
    	}
    	return ret;
    }

    /**
     * Returns a copy of lower-error-value array if they exist.
     *
     * @return
     *         an array of lower-error-values or null if this data has no error bars
     */
    @Override
    public double[] getLowerErrorValueArray(final boolean all) {
    	return SGDataUtility.getLowerErrorValueArray(this, all);
    }
    
	@Override
    public double[] getLowerErrorValueArray(final boolean all, final boolean useCache) {
    	double[] ret = null;
    	final boolean b = (all || !this.isStrideAvailable());
        ret = SGUtility.copyDoubleArray(this.getLowerErrorArray(b));
    	if (useCache) {
            SGSXYDataCache.setLowerErrorValues(this, ret);
    	}
    	return ret;
    }

    /**
     * Returns a copy of upper-error-value array if they exist.
     *
     * @return
     *         an array of upper-error-values or null if this data has no error bars
     */
    @Override
    public double[] getUpperErrorValueArray(final boolean all) {
    	return SGDataUtility.getUpperErrorValueArray(this, all);
    }
    
	@Override
    public double[] getUpperErrorValueArray(final boolean all, final boolean useCache) {
    	double[] ret = null;
    	final boolean b = (all || !this.isStrideAvailable());
        ret = SGUtility.copyDoubleArray(this.getUpperErrorArray(b));
    	if (useCache) {
            SGSXYDataCache.setUpperErrorValues(this, ret);
    	}
    	return ret;
    }

    /**
     * Returns an array of strings if they exist.
     * Decimal places and exponent are effective when the column for tick labels is of the number column.
     *
     * @param dp
     *          decimal places
     * @param exp
     *          exponent
     * @return string array
     */
	@Override
    public String[] getStringArray(final boolean all) {
    	return SGDataUtility.getStringArray(this, all);
    }
    
	@Override
    public String[] getStringArray(final boolean all, final boolean useCache) {
    	String[] ret = null;
        if (this.mTickLabelIndex != null) {
            SGDataColumn[] columns = this.getDataFile().mDataColumns;
            SGDataColumn col = columns[this.mTickLabelIndex.intValue()];
            SGIntegerSeriesSet stride = (all || !this.isStrideAvailable()) ? null : this.mTickLabelStride;
            if (col instanceof SGNumberDataColumn) {
                SGNumberDataColumn numCol = (SGNumberDataColumn) col;
                return numCol.getStringArray(this.mDecimalPlaces, this.mExponent, stride);
            } else if (col instanceof SGDateDataColumn) {
            	SGDateDataColumn dateCol = (SGDateDataColumn) col;
            	return dateCol.getStringArray(this.mDateFormat, stride);
            } else {
                return col.getStringArray(stride);
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
        return (this.mUpperErrorIndex != null && this.mLowerErrorIndex != null);
    }

    /**
     * Returns whether tick labels are available.
     *
     * @return true if tick labels are available
     */
    public boolean isTickLabelAvailable() {
        return (this.mTickLabelIndex != null);
    }

    /**
     * Returns whether a given data object has the same tick label resources.
     *
     * @param data
     *           a data to compare
     * @return true if a given data object has the same tick label resources
     */
    public boolean hasEqualTickLabelResource(SGISXYTypeSingleData data) {
    	if (!(data instanceof SGSXYSDArrayData)) {
    		return false;
    	}
    	SGSXYSDArrayData dataSXY = (SGSXYSDArrayData) data;
    	if (this.mTickLabelIndex == null) {
    		return (dataSXY.mTickLabelIndex == null);
    	} else {
        	return this.mTickLabelIndex.equals(dataSXY.mTickLabelIndex);
    	}
    }

    /**
     * Returns whether a data type column is used.
     *
     * @return true if a data type column is used
     */
    public boolean isDateColumnUsed() {
    	SGDataColumn[] columns = this.getDataFile().mDataColumns;
		if (columns[this.mXIndex] instanceof SGDateDataColumn) {
			return true;
		}
		if (columns[this.mYIndex] instanceof SGDateDataColumn) {
			return true;
		}
		return false;
    }

    /**
     * Returns an array of lower error values.
     * 
     * @param all
     *           true to get all values
     * @return an array of lower error values
     */
    protected double[] getLowerErrorArray(final boolean all) {
    	if (this.mLowerErrorIndex == null) {
    	    return null;
    	} else {
    		return this.getNumberArray(this.mLowerErrorIndex, all);
    	}
    }

    /**
     * Returns an array of upper error values.
     * 
     * @param all
     *           true to get all values
     * @return an array of upper error values
     */
    protected double[] getUpperErrorArray(final boolean all) {
    	if (this.mUpperErrorIndex == null) {
    	    return null;
    	} else {
    		return this.getNumberArray(this.mUpperErrorIndex, all);
    	}
    }

    /**
     * Writes data column indices in the attributes.
     *
     * @param el
     *            the Element object
     * @return true if succeeded
     */
    protected boolean writeAttributeColumnIndices(Element el) {
    	// do nothing
        return true;
    }

    /**
     * Writes data column indices as sequential numbers.
     *
     * @param el
     *            the Element object
     * @return true if succeeded
     */
    protected boolean writeSequentialColumnIndices(Element el) {
    	// do nothing
        return true;
    }

    @Override
    protected boolean writeSequentialColumnName(Element el) {
    	// do nothing
        return true;
    }

    /**
     * Returns the title of X values.
     * @return
     *         the title of X values
     */
    public String getTitleX() {
        if (this.mXIndex == null) {
            return "";
        } else {
        	SGDataColumn[] columns = this.getDataFile().mDataColumns;
            return columns[this.mXIndex.intValue()].getTitle();
        }
    }

    /**
     * Returns the title of Y values.
     * @return
     *         the title of Y values
     */
    public String getTitleY() {
        if (this.mYIndex == null) {
            return "";
        } else {
        	SGDataColumn[] columns = this.getDataFile().mDataColumns;
            return columns[this.mYIndex.intValue()].getTitle();
        }
    }

    public Integer getXIndex() {
        return this.mXIndex;
    }

    public Integer getYIndex() {
        return this.mYIndex;
    }

    public Integer getLowerErrorIndex() {
        return this.mLowerErrorIndex;
    }

    public Integer getUpperErrorIndex() {
        return this.mUpperErrorIndex;
    }

    public Integer getErrorBarHolderIndex() {
        return this.mErrorBarHolderIndex;
    }

    public Integer getTickLabelIndex() {
        return this.mTickLabelIndex;
    }

    public Integer getTickLabelHolderIndex() {
    	return this.mTickLabelHolderIndex;
    }

    public void setXIndex(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("index == null");
        }
        if (this.checkColumnIndexRange(index) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        this.mXIndex = index;
    }

    public void setYIndex(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("index == null");
        }
        if (this.checkColumnIndexRange(index) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        this.mYIndex = index;
    }

    public void setLowerErrorIndex(Integer index) {
        if (index != null) {
            if (this.checkColumnIndexRange(index) == false) {
                throw new IllegalArgumentException("Index out of bounds: " + index);
            }
        }
        this.mLowerErrorIndex = index;
    }

    public void setUpperErrorIndex(Integer index) {
        if (index != null) {
            if (this.checkColumnIndexRange(index) == false) {
                throw new IllegalArgumentException("Index out of bounds: " + index);
            }
        }
        this.mUpperErrorIndex = index;
    }

    public void setErrorBarHolderIndex(Integer index) {
        if (index != null) {
            if (this.checkColumnIndexRange(index) == false) {
                throw new IllegalArgumentException("Index out of bounds: " + index);
            }
        }
        this.mErrorBarHolderIndex = index;
    }

    public void setTickLabelIndex(Integer index) {
        if (index != null) {
            if (this.checkColumnIndexRange(index) == false) {
                throw new IllegalArgumentException("Index out of bounds: " + index);
            }
        }
        this.mTickLabelIndex = index;
    }

    public void setTickLabelHolderIndex(Integer index) {
        if (index != null) {
            if (this.checkColumnIndexRange(index) == false) {
                throw new IllegalArgumentException("Index out of bounds: " + index);
            }
        }
        this.mTickLabelHolderIndex = index;
    }

    /**
     * Returns properties of this data.
     * @return
     *         properties of this data
     */
    public SGProperties getProperties() {
        SGProperties p = new SXYSDArrayDataProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    /**
     * Get properties of this data.
     * @param p
     *          properties to set values
     * @return
     *          true if succeeded
     */
    public boolean getProperties(final SGProperties p) {
        if ((p instanceof SXYSDArrayDataProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        SXYSDArrayDataProperties ep = (SXYSDArrayDataProperties) p;
        ep.mXIndex = this.getXIndex();
        ep.mYIndex = this.getYIndex();
        ep.mLowerErrorIndex = this.getLowerErrorIndex();
        ep.mUpperErrorIndex = this.getUpperErrorIndex();
        ep.mErrorBarHolderIndex = this.getErrorBarHolderIndex();
        ep.mTickLabelIndex = this.getTickLabelIndex();
        ep.mTickLabelHolderIndex = this.getTickLabelHolderIndex();
        ep.mStride = this.getStride();
        ep.mTickLabelStride = this.getTickLabelStride();
        return true;
    }

    /**
     * Set properties to this data.
     * @param p
     *          properties that have values to set to this data
     * @return
     *          true if succeeded
     */
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof SXYSDArrayDataProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        SXYSDArrayDataProperties ep = (SXYSDArrayDataProperties) p;
        this.mXIndex = ep.mXIndex;
        this.mYIndex = ep.mYIndex;
        this.mLowerErrorIndex = ep.mLowerErrorIndex;
        this.mUpperErrorIndex = ep.mUpperErrorIndex;
        this.mErrorBarHolderIndex = ep.mErrorBarHolderIndex;
        this.mTickLabelIndex = ep.mTickLabelIndex;
        this.mTickLabelHolderIndex = ep.mTickLabelHolderIndex;
        this.setStride(ep.mStride);
        this.setTickLabelStride(ep.mTickLabelStride);
        return true;
    }

    /**
     * Sets the type of data columns.
     *
     * @param column
     *           an array of column types
     * @return true if succeeded
     */
    public boolean setColumnType(String[] columns) {
        if (columns == null) {
            throw new IllegalArgumentException("columns == null");
        }
    	SGDataColumn[] cols = this.getDataFile().getDataColumns();
        Integer x = null;
        Integer y = null;
        Integer le = null;
        Integer ue = null;
        Integer tl = null;
        for (int ii = 0; ii < columns.length; ii++) {
        	String valueType = cols[ii].getValueType();
            if (SGDataUtility.isEqualColumnType(X_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)
            			&& !VALUE_TYPE_DATE.equals(valueType)) {
            		return false;
            	}
                x = Integer.valueOf(ii);
            } else if (SGDataUtility.isEqualColumnType(Y_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)
            			&& !VALUE_TYPE_DATE.equals(valueType)) {
            		return false;
            	}
                y = Integer.valueOf(ii);
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], LOWER_ERROR_VALUE)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                le = Integer.valueOf(ii);
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], UPPER_ERROR_VALUE)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                ue = Integer.valueOf(ii);
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], LOWER_UPPER_ERROR_VALUE)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                le = Integer.valueOf(ii);
                ue = Integer.valueOf(ii);
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], TICK_LABEL)) {
                tl = Integer.valueOf(ii);
            } else if ("".equals(columns[ii])) {
                continue;
            } else {
            	return false;
            }
        }

        // check the necessary columns
        if (x == null || y == null) {
        	return false;
        }

        // check the error bar
        if (!(le == null && ue == null) && !(le != null && ue != null)) {
        	return false;
        }

        String[] columnTitles = this.getDataFile().getTitles();

        Integer eh = null;
        if (le != null) {
            String strle = columns[le.intValue()];
            String strue = columns[ue.intValue()];
            Integer ehle = SGDataUtility.getAppendedColumnIndex(strle, columnTitles);
            Integer ehue = SGDataUtility.getAppendedColumnIndex(strue, columnTitles);
            if (ehle == null || ehue==null || ehle.equals(ehue)==false) {
                return false;
            }
            eh = ehle;
        }

        Integer th = null;
        if (tl != null) {
            String str = columns[tl.intValue()];
            th = SGDataUtility.getAppendedColumnIndex(str, columnTitles);
            if (th == null) {
                return false;
            }
        }

        // set to the attributes
        this.mXIndex = x;
        this.mYIndex = y;
        this.mLowerErrorIndex = le;
        this.mUpperErrorIndex = ue;
        this.mErrorBarHolderIndex = eh;
        this.mTickLabelIndex = tl;
        this.mTickLabelHolderIndex = th;

    	// if tick labels are not set yet
        if (tl == null && th == null) {
        	this.setupTickLabel(x, y);
        }

        return true;
    }


    /**
     * A class for scalar xy-type data properties.
     *
     */
    public static class SXYSDArrayDataProperties extends SDArrayDataProperties {

        // indices of data columns
        Integer mXIndex = null;

        Integer mYIndex = null;

        Integer mLowerErrorIndex = null;

        Integer mUpperErrorIndex = null;

        Integer mErrorBarHolderIndex = null;

        Integer mTickLabelIndex = null;

        Integer mTickLabelHolderIndex = null;

        SGIntegerSeriesSet mTickLabelStride = null;

        /**
         * The default constructor.
         */
        public SXYSDArrayDataProperties() {
            super();
        }

    	@Override
    	public boolean equals(Object obj) {
            if ((obj instanceof SXYSDArrayDataProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
            	return false;
            }
            return true;
        }

    	@Override
        public boolean hasEqualSize(DataProperties dp) {
            if ((dp instanceof SXYSDArrayDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualSize(dp)) {
            	return false;
            }
            SXYSDArrayDataProperties p = (SXYSDArrayDataProperties) dp;
            if (!SGUtility.equals(this.mTickLabelStride, p.mTickLabelStride)) {
            	return false;
            }
        	return true;
        }

        /**
         * Returns whether this data property has the equal column types with given data property.
         *
         * @param p
         *          a data property
         * @return true if this data property has the equal column types with given data property
         */
        @Override
        public boolean hasEqualColumnTypes(DataProperties dp) {
            if ((dp instanceof SXYSDArrayDataProperties) == false) {
                return false;
            }
            SXYSDArrayDataProperties p = (SXYSDArrayDataProperties) dp;
            if (!SGUtility.equals(this.mXIndex, p.mXIndex)) {
            	return false;
            }
            if (!SGUtility.equals(this.mYIndex, p.mYIndex)) {
            	return false;
            }
            if (!SGUtility.equals(this.mLowerErrorIndex, p.mLowerErrorIndex)) {
            	return false;
            }
            if (!SGUtility.equals(this.mUpperErrorIndex, p.mUpperErrorIndex)) {
            	return false;
            }
            if (!SGUtility.equals(this.mErrorBarHolderIndex, p.mErrorBarHolderIndex)) {
            	return false;
            }
            if (!SGUtility.equals(this.mTickLabelIndex, p.mTickLabelIndex)) {
            	return false;
            }
            if (!SGUtility.equals(this.mTickLabelHolderIndex, p.mTickLabelHolderIndex)) {
            	return false;
            }
            return true;
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mXIndex = null;
            this.mYIndex = null;
            this.mLowerErrorIndex = null;
            this.mUpperErrorIndex = null;
            this.mErrorBarHolderIndex = null;
            this.mTickLabelIndex = null;
            this.mTickLabelHolderIndex = null;
            this.mTickLabelStride = null;
        }

        /**
         * Copy this object.
         *
         * @return a copied object
         */
        public Object copy() {
        	SXYSDArrayDataProperties p = (SXYSDArrayDataProperties) super.copy();
        	p.mTickLabelStride = (this.mTickLabelStride != null) ? (SGIntegerSeriesSet) this.mTickLabelStride.clone() : null;
        	return p;
        }
    }


    /**
     * Returns an array of current column types.
     * @return
     *        an array of current column types
     */
    public String[] getCurrentColumnType() {
        final int colNum = this.getColNum();
        String[] array = new String[colNum];
        for (int ii = 0; ii < array.length; ii++) {
        	array[ii] = "";
        }
        if (this.mXIndex != null) {
            array[this.mXIndex.intValue()] = X_VALUE;
        }
        if (this.mYIndex != null) {
            array[this.mYIndex.intValue()] = Y_VALUE;
        }
        if (this.mLowerErrorIndex != null && this.mUpperErrorIndex != null
                && this.mErrorBarHolderIndex != null) {
            int index = this.mErrorBarHolderIndex.intValue();
            if (this.mLowerErrorIndex.equals(this.mUpperErrorIndex)) {
                array[this.mLowerErrorIndex.intValue()] =
                    SGDataUtility.appendColumnNoOrTitle(
                        LOWER_UPPER_ERROR_VALUE, index,
                        this.getDataFile().isEmptyTitle(index) || this.getDataFile().isRepeatedTitle(index),
                        this.getTitle(index));
            } else {
                array[this.mLowerErrorIndex.intValue()] =
                    SGDataUtility.appendColumnNoOrTitle(
                        LOWER_ERROR_VALUE, index,
                        this.getDataFile().isEmptyTitle(index) || this.getDataFile().isRepeatedTitle(index),
                        this.getTitle(index));
                array[this.mUpperErrorIndex.intValue()] =
                    SGDataUtility.appendColumnNoOrTitle(
                        UPPER_ERROR_VALUE, index,
                        this.getDataFile().isEmptyTitle(index) || this.getDataFile().isRepeatedTitle(index),
                        this.getTitle(index));
            }
        }
        if (this.mTickLabelIndex != null && this.mTickLabelHolderIndex != null
                && !this.hasDateTickLabel()) {
            int index = this.mTickLabelHolderIndex.intValue();
            array[this.mTickLabelIndex.intValue()] =
                SGDataUtility.appendColumnNoOrTitle(
                    TICK_LABEL, index,
                    this.getDataFile().isEmptyTitle(index) || this.getDataFile().isRepeatedTitle(index),
                    this.getTitle(index));
        }
        return array;
    }

    private boolean hasDateTickLabel() {
		if (this.isTickLabelAvailable()) {
			if (this.mTickLabelIndex.equals(this.mXIndex)
					|| this.mTickLabelIndex.equals(this.mYIndex)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

    /**
	 * Returns an array of data columns to save to a data set.
	 *
	 * @return an array of data columns to save to a data set
	 */
    public SGDataColumn[] getExportedColumns() {
        SGDataColumn[] colArray;
    	SGDataColumn[] columns = this.getDataFile().mDataColumns;
        if (this.isErrorBarAvailable()) {
            if (this.isTickLabelAvailable()) {
                colArray = new SGDataColumn[7];
                colArray[5] = columns[this.mTickLabelIndex.intValue()];
                colArray[6] = columns[this.mTickLabelHolderIndex.intValue()];
            } else {
                colArray = new SGDataColumn[5];
            }
            colArray[2] = columns[this.mLowerErrorIndex.intValue()];
            colArray[3] = columns[this.mUpperErrorIndex.intValue()];
            colArray[4] = columns[this.mErrorBarHolderIndex.intValue()];
        } else {
            if (this.isTickLabelAvailable()) {
                colArray = new SGDataColumn[4];
                colArray[2] = columns[this.mTickLabelIndex.intValue()];
                colArray[3] = columns[this.mTickLabelHolderIndex.intValue()];
            } else {
                colArray = new SGDataColumn[2];
            }
        }
        colArray[0] = columns[this.mXIndex.intValue()];
        colArray[1] = columns[this.mYIndex.intValue()];
        return colArray;
    }

    @Override
    public SGDataColumn[] getUsedDataColumnsClone() {
        SGDataColumn[] columns = this.getDataFile().mDataColumns;
        String[] columnTypes = this.getCurrentColumnType();
        List<SGDataColumn> collist = new ArrayList<SGDataColumn>();

        int index = this.mXIndex.intValue();
        SGDataColumn col = (SGDataColumn)columns[index].clone();
        col.setColumnType(columnTypes[index]);
        collist.add(col);

        index = this.mYIndex.intValue();
        col = (SGDataColumn)columns[index].clone();
        col.setColumnType(columnTypes[index]);
        collist.add(col);

        if (this.isErrorBarAvailable()) {
            if (this.mLowerErrorIndex.equals(this.mUpperErrorIndex)) {
                index = this.mLowerErrorIndex.intValue();
                col = (SGDataColumn)columns[index].clone();
                col.setColumnType(columnTypes[index]);
                collist.add(col);
            } else {
                index = this.mLowerErrorIndex.intValue();
                col = (SGDataColumn)columns[index].clone();
                col.setColumnType(columnTypes[index]);
                collist.add(col);
                index = this.mUpperErrorIndex.intValue();
                col = (SGDataColumn)columns[index].clone();
                col.setColumnType(columnTypes[index]);
                collist.add(col);

            }
        }
        if (this.isTickLabelAvailable()) {
            index = this.mTickLabelIndex.intValue();
            col = (SGDataColumn)columns[index].clone();
            col.setColumnType(columnTypes[index]);
            collist.add(col);
        }

        return collist.toArray(new SGDataColumn[collist.size()]);
    }

    /**
     * Sets the decimal places for the tick labels.
     *
     * @param dp
     *          a value to set to the decimal places
     */
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
    public void setExponent(final int exp) {
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

    /**
	 * Returns whether the error bars are vertical. If this data does not have
	 * error values, returns null.
	 *
	 * @return true if error bars are vertical, false if they are horizontal and
	 *         null if this data do not have error values
	 */
    public Boolean isErrorBarVertical() {
    	if (this.isErrorBarAvailable()) {
    		return this.mYIndex.equals(this.mErrorBarHolderIndex);
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
    		return this.mYIndex.equals(this.mTickLabelHolderIndex);
    	} else if (this.isDateColumnUsed()) {
    		SGDataColumn[] columns = this.getDataFile().mDataColumns;
    		if (columns[this.mXIndex] instanceof SGDateDataColumn) {
    			return Boolean.TRUE;
    		} else if (columns[this.mYIndex] instanceof SGDateDataColumn) {
    			return Boolean.FALSE;
    		}
    	}
		return null;
    }

    /**
     * Transforms this data to a multiple type data.
     *
     * @return a transformed data
     */
    public SGISXYTypeMultipleData toMultiple() {
        final boolean be = this.isErrorBarAvailable();
        final boolean bt = this.isTickLabelAvailable();
        Integer[] x = new Integer[] { this.mXIndex };
        Integer[] y = new Integer[] { this.mYIndex };
        Integer[] le = be ? new Integer[] { this.mLowerErrorIndex } : null;
        Integer[] ue = be ? new Integer[] { this.mUpperErrorIndex } : null;
        Integer[] eh = be ? new Integer[] { this.mErrorBarHolderIndex } : null;
        Integer[] t = bt ? new Integer[] { this.mTickLabelIndex } : null;
        Integer[] th = bt ? new Integer[] { this.mTickLabelHolderIndex } : null;
        SGSXYSDArrayMultipleData data = new SGSXYSDArrayMultipleData(this.getDataFile(),
                this.getDataSourceObserver(), x, y, le, ue, eh, t, th, this.mStride,
                this.mTickLabelStride, this.isStrideAvailable(), this.mSamplingRate);
        data.setDecimalPlaces(this.mDecimalPlaces);
        data.setExponent(this.mExponent);
        data.setShift(this.mShift);
        for (int ii = 0; ii < this.mEditedDataValueList.size(); ii++) {
        	SGDataValueHistory dataValue = this.mEditedDataValueList.get(ii);
        	data.addSingleDimensionEditedDataValue(dataValue);
        }
        
        return data;
    }

	public SGDataColumn getXColumn() {
		return this.getColumn(this.mXIndex);
	}

	public SGDataColumn getYColumn() {
		return this.getColumn(this.mYIndex);
	}

	public SGDataColumn getLowerErrorColumn() {
		return this.getColumn(this.mLowerErrorIndex);
	}

	public SGDataColumn getUpperErrorColumn() {
		return this.getColumn(this.mUpperErrorIndex);
	}

	public SGDataColumn getErrorBarHolderColumn() {
		return this.getColumn(this.mErrorBarHolderIndex);
	}

	public SGDataColumn getTickLableColumn() {
		return this.getColumn(this.mTickLabelIndex);
	}

	public SGDataColumn getTickLabelHolderColumn() {
		return this.getColumn(this.mTickLabelHolderIndex);
	}

    /**
     * Returns a map which has data information.
     *
     * @return a map which has data information
     */
    public Map<String, Object> getInfoMap() {
    	Map<String, Object> infoMap = super.getInfoMap();
    	infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    	return infoMap;
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
     * Returns a text string of the data type to save into a NetCDF data set file.
     *
     * @return a text string of the data type to save into a NetCDF data set file
     */
    @Override
    public String getNetCDFDataSetDataType() {
    	return SGDataTypeConstants.SXY_NETCDF_DATA;
    }

    /**
     * Sets the stride of data arrays.
     *
     * @param map
     *           the map of the stride
     */
    @Override
    public void setStrideMap(Map<String, SGIntegerSeriesSet> map) {
    	this.mStride = map.get(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
    	this.mTickLabelStride = map.get(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
    }

    /**
     * Returns a map of stride for data arrays.
     *
     * @return a map of stride for data arrays
     */
	@Override
    protected Map<String, SGIntegerSeriesSet> getStrideMap() {
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    	map.put(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE, this.getStride());
    	map.put(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE, this.getTickLabelStride());
    	return map;
    }

    /**
     * Exports the data into a NetCDF file.
     * 
     * @param file
     *           the file to save
     * @return true if succeeded
     */
	@Override
    public boolean exportToNetCDFFile(NetcdfFileWriteable ncWrite, final SGExportParameter mode, 
			SGDataBufferPolicy policy) throws IOException, InvalidRangeException {
		// do nothing
		return true;
	}

	@Override
    public boolean saveToDataSetNetCDFFile(final File file) {
		// do nothing
		return true;
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
	protected Object[][] getDataFileExportValues(SGExportParameter mode, SGDataBufferPolicy policy) {
		// always returns null
		return null;
	}

	@Override
	public Object[][] getValueTable(SGExportParameter mode, SGDataBufferPolicy policy) {
		// always returns null
		return null;
	}

	@Override
	protected Object[][] getArchiveDataSetExportValues(SGExportParameter mode) {
		// always returns null
		return null;
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
		return this.mLowerErrorIndex.equals(this.mUpperErrorIndex);
	}

	@Override
    public SGDate[] getDateArray(SGSXYDataBufferPolicy policy) {
		return this.toMultiple().getDateArray(policy);
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
	public String getColumnTypeCommandString() {
		// always returns null
		return null;
	}

	@Override
	protected boolean setArraySectionPropertySub(SGPropertyMap map) {
		// do nothing
		return true;
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
	public void setDataViewerValue(String columnType, int row, int col,
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
	public String getToolTipSpatiallyVaried(final int index) {
		return this.getIndexToolTipSpatiallyVaried(index);
    }

	@Override
	public String getToolTipSpatiallyVaried(final int index0, final int index1) {
    	return this.getIndexToolTipSpatiallyVaried(index0, index1);
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
	public boolean useValueCache(final boolean all) {
		return this.useCache(all);
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
		return (this.getXColumn() instanceof SGDateDataColumn);
	}
	
	@Override
    public boolean hasDateTypeYVariable() {
		return (this.getYColumn() instanceof SGDateDataColumn);
	}

	@Override
	protected boolean matches(final int col, final int row, String columnType,
			SGDataValueHistory value, final double d) {
		return SGDataUtility.matches(row, columnType, value, d);
	}

	@Override
	public void addMultipleDimensionEditedDataValue(SGDataValueHistory dataValue) {
		SGDataValueHistory.SDArray.MD1 mdValue = (SGDataValueHistory.SDArray.MD1) dataValue;
		SGDataValueHistory prev = mdValue.getPreviousValue();
		SGDataValueHistory.SDArray.D1 dValue;
		if (prev != null) {
			dValue = new SGDataValueHistory.SDArray.D1(
					mdValue.getValue(), mdValue.getColumnType(), 
					mdValue.getIndex(), prev.getValue());
		} else {
			dValue = new SGDataValueHistory.SDArray.D1(
					mdValue.getValue(), mdValue.getColumnType(), 
					mdValue.getIndex());
		}
		this.mEditedDataValueList.add(dValue);
	}

}
