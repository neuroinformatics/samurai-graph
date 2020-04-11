package jp.riken.brain.ni.samuraigraph.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
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
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

/**
 * Data with multiple scalar type XY data.
 *
 */
public class SGSXYSDArrayMultipleData extends SGSDArrayData implements
        SGISXYTypeMultipleData, SGIDataColumnTypeConstants, SGIDataPropertyKeyConstants {

    /**
     * An array of column indices for x-values.
     */
    protected Integer[] mXIndices = null;

    /**
     * An array of column indices for y-values.
     */
    protected Integer[] mYIndices = null;

    /**
     * An array of column indices for lower error values.
     */
    protected Integer[] mLowerErrorIndices = null;

    /**
     * An array of column indices for upper error values.
     */
    protected Integer[] mUpperErrorIndices = null;

    /**
     * An array of column indices for error bar holders.
     */
    protected Integer[] mErrorBarHolderIndices = null;

    /**
     * An array of column indices for tick labels.
     */
    protected Integer[] mTickLabelIndices = null;

    /**
     * An array of column indices for tick label holders.
     */
    protected Integer[] mTickLabelHolderIndices = null;

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
     * The default constructor.
     */
    public SGSXYSDArrayMultipleData() {
        super();
    }

	/**
	 * Build a data with given columns and indices.
	 *
	 * @param dataFile
	 *            the text data file
	 * @param obs
	 *            an observer for array data
	 * @param xIndices
	 *            an array of the indices for x-values
	 * @param yIndices
	 *            an array of the indices for y-values
	 * @param lowerErrorIndices
	 *            an array of the indices for lower error values
	 * @param upperErrorIndices
	 *            an array of the indices for upper error values
	 * @param errorBarHolderIndices
	 *            an array of the indices of columns those hold error bars
	 * @param tickLabelIndices
	 *            an array of the indices for tick labels
	 * @param tickLabelHolderIndices
	 *            an array of the indices of columns those hold tick labels
	 */
    public SGSXYSDArrayMultipleData(final SGSDArrayFile dataFile,
    		final SGDataSourceObserver obs,
            final Integer[] xIndices, final Integer[] yIndices,
            final Integer[] lowerErrorIndices, final Integer[] upperErrorIndices,
            final Integer[] errorBarHolderIndices, final Integer[] tickLabelIndices,
            final Integer[] tickLabelHolderIndices, final SGIntegerSeriesSet stride,
			final SGIntegerSeriesSet tickLabelStride, final boolean strideAvailable,
			final Double samplingRate) {

        super(dataFile, obs, strideAvailable);

        // check indices
        if (!this.isValidXYValueIndices(xIndices, yIndices)) {
            throw new IllegalArgumentException("Column indices for x or y values are invalid.");
        }

        // set indices
        this.mXIndices = (Integer[]) xIndices.clone();
        this.mYIndices = (Integer[]) yIndices.clone();

    	// check the input for error bars
    	Integer[] multipleIndices = (xIndices.length == 1) ? yIndices : xIndices;
    	if (!this.isValidErrorBarIndices(multipleIndices, lowerErrorIndices, upperErrorIndices,
    			errorBarHolderIndices)) {
            throw new IllegalArgumentException("Column indices for the error bars are invalid.");
    	}
    	if (!this.isValidTickLabelIndices(multipleIndices, tickLabelIndices,
    			tickLabelHolderIndices)) {
            throw new IllegalArgumentException("Column indices for the tick labels are invalid.");
    	}

        // set indices
    	if (lowerErrorIndices != null) {
    		if (lowerErrorIndices.length != 0) {
                this.mLowerErrorIndices = (Integer[]) lowerErrorIndices.clone();
    		}
    	}
    	if (upperErrorIndices != null) {
    		if (upperErrorIndices.length != 0) {
                this.mUpperErrorIndices = (Integer[]) upperErrorIndices.clone();
    		}
    	}
    	if (errorBarHolderIndices != null) {
    		if (errorBarHolderIndices.length != 0) {
        		this.mErrorBarHolderIndices = (Integer[]) errorBarHolderIndices.clone();
    		}
    	}
    	if (tickLabelIndices != null) {
    		if (tickLabelIndices.length != 0) {
                this.mTickLabelIndices = (Integer[]) tickLabelIndices.clone();
    		}
    	}
    	if (tickLabelHolderIndices != null) {
    		if (tickLabelHolderIndices.length != 0) {
        		this.mTickLabelHolderIndices = (Integer[]) tickLabelHolderIndices.clone();
    		}
    	}

    	// set tick label indices for date type columns
        if (tickLabelIndices == null || tickLabelIndices.length == 0) {
        	this.updateDateTickLabelColumns();
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

    private void updateDateTickLabelColumns() {
    	SGDataColumn[] columns = this.getDataFile().mDataColumns;
    	SGDataColumn xColumn = columns[this.mXIndices[0].intValue()];
    	SGDataColumn yColumn = columns[this.mYIndices[0].intValue()];
    	if (xColumn instanceof SGDateDataColumn) {
    		this.mTickLabelHolderIndices = this.mYIndices;
    		this.mTickLabelIndices = new Integer[this.mYIndices.length];
    		for (int ii = 0; ii < this.mTickLabelIndices.length; ii++) {
    			this.mTickLabelIndices[ii] = this.mXIndices[0];
    		}
    	} else if (yColumn instanceof SGDateDataColumn) {
    		this.mTickLabelHolderIndices = this.mXIndices;
    		this.mTickLabelIndices = new Integer[this.mXIndices.length];
    		for (int ii = 0; ii < this.mTickLabelIndices.length; ii++) {
    			this.mTickLabelIndices[ii] = this.mYIndices[0];
    		}
    	}
    }

    protected boolean isValidIndices(final Integer[] indices) {
    	if (indices == null) {
    		return false;
    	}
        for (int ii = 0; ii < indices.length; ii++) {
            if (indices[ii] == null) {
            	return false;
            }
            if (this.checkColumnIndexRange(indices[ii]) == false) {
            	return false;
            }
        }
        return true;
    }

    protected boolean isValidXYValueIndices(final Integer[] xIndices,
    		final Integer[] yIndices) {
        if (xIndices == null || yIndices == null) {
        	return false;
        }
        if (xIndices.length == 0 || yIndices.length == 0) {
        	return false;
        }
        if (xIndices.length != 1 && yIndices.length != 1) {
        	return false;
        }
        if (!this.isValidIndices(xIndices)) {
        	return false;
        }
        if (!this.isValidIndices(yIndices)) {
        	return false;
        }
        return true;
    }

    protected boolean isValidErrorBarIndices(
    		final Integer[] multipleIndices,
    		final Integer[] lowerErrorIndices,
    		final Integer[] upperErrorIndices,
    		final Integer[] errorBarHolderIndices) {

        if (lowerErrorIndices != null && upperErrorIndices != null) {
        	if (!this.isValidIndices(lowerErrorIndices)) {
        		return false;
        	}
        	if (!this.isValidIndices(upperErrorIndices)) {
        		return false;
        	}
        	if (lowerErrorIndices.length != upperErrorIndices.length) {
        		return false;
        	}
        	if (errorBarHolderIndices != null) {
            	if (!this.isValidIndices(errorBarHolderIndices)) {
            		return false;
            	}
                if (errorBarHolderIndices.length != lowerErrorIndices.length) {
                	return false;
                }
        	}
        }
        return true;
    }

    protected boolean isValidTickLabelIndices(
    		final Integer[] multipleIndices,
    		final Integer[] tickLabelIndices,
    		final Integer[] tickLabelHolderIndices) {

        if (tickLabelIndices != null) {
        	if (!this.isValidIndices(tickLabelIndices)) {
        		return false;
        	}
        	if (tickLabelHolderIndices != null) {
            	if (!this.isValidIndices(tickLabelHolderIndices)) {
            		return false;
            	}
                if (tickLabelHolderIndices.length != tickLabelIndices.length) {
                	return false;
                }
        	}
        }
        return true;
    }

    public boolean hasMultipleYValues() {
    	if (this.hasPairOfXYValues()) {
    		if (this.isErrorBarAvailable()) {
    			return SGUtility.contains(this.mErrorBarHolderIndices, this.mYIndices);
    		} else if (this.isTickLabelAvailable()) {
    			return SGUtility.contains(this.mTickLabelHolderIndices, this.mYIndices);
    		} else {
    			return true;
    		}
    	} else {
        	return (this.mXIndices.length == 1);
    	}
    }

    protected boolean hasPairOfXYValues() {
    	return (this.mXIndices.length == 1 && this.mYIndices.length == 1);
    }

    /**
     * Returns the type of data.
     *
     * @return
     *         the type of data
     */
    public String getDataType() {
        return SGDataTypeConstants.SXY_MULTIPLE_DATA;
    }

    /**
     * Returns an array of SXYData objects.
     *
     * @return an array of SXYData objects
     */
    public SGISXYTypeSingleData[] getSXYDataArray() {
    	
    	SGSXYMultipleDataCache cache = (SGSXYMultipleDataCache) this.getCache();
    	SGSXYDataCache[] sxyCacheArray = null;
        if (cache != null) {
        	sxyCacheArray = cache.mCacheArray;
        }

        final boolean by = this.hasMultipleYValues();
        final boolean be = this.isErrorBarAvailable();
        final boolean bt = this.isTickLabelAvailable();
        final int dataNum = this.getChildNumber();
        final SGISXYTypeSingleData[] dataArray = new SGISXYTypeSingleData[dataNum];
        Integer xIndex = null;
        Integer yIndex = null;
        Integer[] lArray = null;
        Integer[] uArray = null;
        Integer[] ehArray = null;
        Integer[] tArray = null;
        Integer[] thArray = null;
        if (by) {
            xIndex = this.mXIndices[0];
        } else {
            yIndex = this.mYIndices[0];
        }
        if (be) {
            lArray = new Integer[dataNum];
            uArray = new Integer[dataNum];
            ehArray = new Integer[dataNum];
            for (int ii = 0; ii < dataArray.length; ii++) {
                lArray[ii] = null;
                uArray[ii] = null;
                ehArray[ii] = null;
                for (int jj = 0; jj < this.mErrorBarHolderIndices.length; jj++) {
                    final boolean b = by ? this.mErrorBarHolderIndices[jj].equals(this.mYIndices[ii])
                            : this.mErrorBarHolderIndices[jj].equals(this.mXIndices[ii]);
                    if (b) {
                        lArray[ii] = this.mLowerErrorIndices[jj];
                        uArray[ii] = this.mUpperErrorIndices[jj];
                        ehArray[ii] = this.mErrorBarHolderIndices[jj];
                        break;
                    }
                }
            }
        }
        if (bt) {
            tArray = new Integer[dataNum];
            thArray = new Integer[dataNum];
            for (int ii = 0; ii < dataArray.length; ii++) {
                tArray[ii] = null;
                thArray[ii] = null;
                for (int jj = 0; jj < this.mTickLabelHolderIndices.length; jj++) {
                    final boolean b = by ? this.mTickLabelHolderIndices[jj].equals(this.mYIndices[ii])
                            : this.mTickLabelHolderIndices[jj].equals(this.mXIndices[ii]);
                    if (b) {
                        tArray[ii] = this.mTickLabelIndices[jj];
                        thArray[ii] = this.mTickLabelHolderIndices[jj];
                        break;
                    }
                }
            }
        }
        for (int ii = 0; ii < dataArray.length; ii++) {
            Integer x = by ? xIndex : this.mXIndices[ii];
            Integer y = by ? this.mYIndices[ii] : yIndex;
            Integer le = be ? lArray[ii] : null;
            Integer ue = be ? uArray[ii] : null;
            Integer eh = be ? ehArray[ii] : null;
            Integer t = bt ? tArray[ii] : null;
            Integer th = bt ? thArray[ii] : null;
            SGSXYSDArrayData data = new SGSXYSDArrayData(
                    this.getDataFile(), this.getDataSourceObserver(), x, y,
                    le, ue, eh, t, th, this.mStride, this.mTickLabelStride,
                    this.isStrideAvailable(), this.mSamplingRate);
            data.setDecimalPlaces(this.mDecimalPlaces);
            data.setExponent(this.mExponent);
            data.setDateFormat(this.mDateFormat);
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

    /**
     * Returns the number of child data.
     *
     * @return the number of child data
     */
	@Override
    public int getChildNumber() {
        final boolean by = this.hasMultipleYValues();
        final int dataNum = by ? this.getYNumber() : this.getXNumber();
        return dataNum;
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
            SGSXYSDArrayData data = (SGSXYSDArrayData) sxyArray[ii];
            ret[ii] = data.toMultiple();
        }
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);
        return ret;
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
     * Set values with a given data.
     *
     * @param data
     *             data to be copied
     * @return true if succeeded
     */
    public boolean setData(final SGData data) {
        if (!(data instanceof SGSXYSDArrayMultipleData)) {
            return false;
        }
        if (super.setData(data) == false) {
            return false;
        }
        SGSXYSDArrayMultipleData dataMult = (SGSXYSDArrayMultipleData) data;
        if (dataMult.mXIndices != null) {
            this.mXIndices = SGUtility.copyIntegerArray(dataMult.mXIndices);
        }
        if (dataMult.mYIndices != null) {
            this.mYIndices = SGUtility.copyIntegerArray(dataMult.mYIndices);
        }
        if (dataMult.mLowerErrorIndices != null) {
            this.mLowerErrorIndices = SGUtility.copyIntegerArray(dataMult.mLowerErrorIndices);
        }
        if (dataMult.mUpperErrorIndices != null) {
            this.mUpperErrorIndices = SGUtility.copyIntegerArray(dataMult.mUpperErrorIndices);
        }
        if (dataMult.mErrorBarHolderIndices != null) {
            this.mErrorBarHolderIndices = SGUtility.copyIntegerArray(dataMult.mErrorBarHolderIndices);
        }
        if (dataMult.mTickLabelIndices != null) {
            this.mTickLabelIndices = SGUtility.copyIntegerArray(dataMult.mTickLabelIndices);
        }
        if (dataMult.mTickLabelHolderIndices != null) {
            this.mTickLabelHolderIndices = SGUtility.copyIntegerArray(dataMult.mTickLabelHolderIndices);
        }
        this.mTickLabelStride = dataMult.getTickLabelStride();
        this.mShift = dataMult.getShift();
        return true;
    }

    /**
     * Returns the copy of this data object.
     *
     * @return copy of this data object
     */
    public Object clone() {
        SGSXYSDArrayMultipleData data = (SGSXYSDArrayMultipleData) super.clone();
        if (this.mXIndices != null) {
            data.mXIndices = SGUtility.copyIntegerArray(this.mXIndices);
        }
        if (this.mYIndices != null) {
            data.mYIndices = SGUtility.copyIntegerArray(this.mYIndices);
        }
        if (this.mLowerErrorIndices != null) {
            data.mLowerErrorIndices = SGUtility.copyIntegerArray(this.mLowerErrorIndices);
        }
        if (this.mUpperErrorIndices != null) {
            data.mUpperErrorIndices = SGUtility.copyIntegerArray(this.mUpperErrorIndices);
        }
        if (this.mErrorBarHolderIndices != null) {
            data.mErrorBarHolderIndices = SGUtility.copyIntegerArray(this.mErrorBarHolderIndices);
        }
        if (this.mTickLabelIndices != null) {
            data.mTickLabelIndices = SGUtility.copyIntegerArray(this.mTickLabelIndices);
        }
        if (this.mTickLabelHolderIndices != null) {
            data.mTickLabelHolderIndices = SGUtility.copyIntegerArray(this.mTickLabelHolderIndices);
        }
        data.mTickLabelStride = this.getTickLabelStride();
        data.mShift = this.getShift();
        return data;
    }

    /**
     * Disposes of this data object.
     * Do not call any method of this object after called this method.
     */
    public void dispose() {
        super.dispose();
        this.mXIndices = null;
        this.mYIndices = null;
        this.mLowerErrorIndices = null;
        this.mUpperErrorIndices = null;
        this.mErrorBarHolderIndices = null;
        this.mTickLabelIndices = null;
        this.mTickLabelHolderIndices = null;
        this.mTickLabelStride = null;
        this.mShift = null;
    }

    protected double[][] getValueArray(Integer[] indices, final boolean all) {
    	double[][] array = new double[indices.length][];
    	for (int ii = 0; ii < array.length; ii++) {
    		array[ii] = this.getNumberArray(indices[ii], all);
    	}
    	return array;
    }

//    protected double[] getNumberArray(final int index) {
//    	SGDataColumn[] columns = this.getDataFile().getDataColumns();
//		int[] indices = SGDataUtility.getIndices(this);
//    	SGINumberDataColumn col = (SGINumberDataColumn) columns[index];
//        return col.getNumberArray(indices);
//    }

    /**
     * Returns the number of arrays of x-values.
     *
     * @return
     *         the number of arrays of x-values
     */
    public int getXNumber() {
        return this.mXIndices.length;
    }

    /**
     * Returns the number of arrays of y-values.
     *
     * @return
     *         the number of arrays of y-values
     */
    public int getYNumber() {
        return this.mYIndices.length;
    }

    /**
     * Writes data column indices in the attributes.
     *
     * @param el
     *            the Element object
     * @return true if succeeded
     */
    protected boolean writeAttributeColumnIndices(Element el) {
//        if (this.hasMultipleYValues()) {
////            el.setAttribute(KEY_X_VALUE_COLUMN_INDICES, this.mXIndices[0].toString());
//            if (this.writeAttributeColumnIndicesSub(el, KEY_Y_VALUE_COLUMN_INDICES,
//                    this.mYIndices) == false) {
//                return false;
//            }
//        } else {
//            el.setAttribute(KEY_Y_VALUE_COLUMN_INDICES, this.mYIndices[0].toString());
//            if (this.writeAttributeColumnIndicesSub(el, KEY_X_VALUE_COLUMN_INDICES,
//                    this.mXIndices) == false) {
//                return false;
//            }
//        }
        if (this.writeAttributeColumnIndicesSub(el, KEY_X_VALUE_COLUMN_INDICES,
                this.mXIndices) == false) {
            return false;
        }
        if (this.writeAttributeColumnIndicesSub(el, KEY_Y_VALUE_COLUMN_INDICES,
                this.mYIndices) == false) {
            return false;
        }
		if (this.mLowerErrorIndices != null && this.mUpperErrorIndices != null
				&& this.mErrorBarHolderIndices != null) {
			if (this.writeAttributeColumnIndicesSub(el,
					KEY_LOWER_ERROR_BAR_COLUMN_INDICES, this.mLowerErrorIndices) == false) {
				return false;
			}
			if (this.writeAttributeColumnIndicesSub(el,
					KEY_UPPER_ERROR_BAR_COLUMN_INDICES, this.mUpperErrorIndices) == false) {
				return false;
			}
			if (this.writeAttributeColumnIndicesSub(el,
					KEY_ERROR_BAR_HOLDER_COLUMN_INDICES,
					this.mErrorBarHolderIndices) == false) {
				return false;
			}
		}
		if (this.mTickLabelIndices != null
				&& this.mTickLabelHolderIndices != null) {
			SGDataColumn[] columns = this.getDataFile().getDataColumns();
			List<Integer> tlIndicesList = new ArrayList<Integer>();
			List<Integer> thIndicesList = new ArrayList<Integer>();
			for (int ii = 0; ii < this.mTickLabelIndices.length; ii++) {
				if (!(columns[this.mTickLabelIndices[ii].intValue()] instanceof SGDateDataColumn)) {
					tlIndicesList.add(this.mTickLabelIndices[ii]);
					thIndicesList.add(this.mTickLabelHolderIndices[ii]);
				}
			}
			if (tlIndicesList.size() != 0) {
				Integer[] tlIndices = new Integer[tlIndicesList.size()];
				tlIndicesList.toArray(tlIndices);
				Integer[] thIndices = new Integer[thIndicesList.size()];
				thIndicesList.toArray(thIndices);
				if (this.writeAttributeColumnIndicesSub(el,
						KEY_TICK_LABEL_COLUMN_INDICES, tlIndices) == false) {
					return false;
				}
				if (this.writeAttributeColumnIndicesSub(el,
						KEY_TICK_LABEL_HOLDER_COLUMN_INDICES, thIndices) == false) {
					return false;
				}
			}
		}
        return true;
    }

    private boolean writeAttributeColumnIndicesSub(Element el, String key,
			Integer[] indices) {
		int[] array = new int[indices.length];
		for (int ii = 0; ii < array.length; ii++) {
			array[ii] = indices[ii].intValue();
		}
		return this.writeAttributeColumnIndicesSub(el, key, array);
	}

	private boolean writeAttributeColumnIndicesSub(Element el, String key,
			int[] indices) {
		StringBuffer sb = new StringBuffer();
		sb.append('{');
		for (int ii = 0; ii < indices.length; ii++) {
			sb.append(indices[ii]);
			if (ii != indices.length - 1) {
				sb.append(',');
			}
		}
		sb.append('}');
		el.setAttribute(key, sb.toString());
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
    	int[] xIndices, yIndices;
    	if (this.hasMultipleYValues()) {
    		xIndices = new int[] { 0 };
    		yIndices = new int[this.mYIndices.length];
    		for (int ii = 0; ii < yIndices.length; ii++) {
    			yIndices[ii] = ii + 1;
    		}
    	} else {
    		xIndices = new int[this.mXIndices.length];
    		for (int ii = 0; ii < xIndices.length; ii++) {
    			xIndices[ii] = ii;
    		}
    		yIndices = new int[] { xIndices.length };
    	}

    	// write indices
        if (!this.writeAttributeColumnIndicesSub(el, KEY_X_VALUE_COLUMN_INDICES, xIndices)) {
        	return false;
        }
        if (!this.writeAttributeColumnIndicesSub(el, KEY_Y_VALUE_COLUMN_INDICES, yIndices)) {
        	return false;
        }

		final Integer[] holders = this.hasMultipleYValues() ? this.mYIndices : this.mXIndices;
		final int holderOffset = this.hasMultipleYValues() ? 1 : 0;

        int offset = xIndices.length + yIndices.length;
        if (this.isErrorBarAvailable()) {
        	// error bars
        	final int errLen = this.mLowerErrorIndices.length;
        	int[] lIndices = new int[errLen];
    		int[] uIndices = new int[errLen];
    		boolean[] sameErrorVariableFlags = this.getSameErrorVariableFlags();
    		int tmp = offset;
    		for (int ii = 0; ii < errLen; ii++) {
    			lIndices[ii] = tmp;
    			tmp++;
    			if (!sameErrorVariableFlags[ii]) {
        			uIndices[ii] = tmp;
        			tmp++;
    			} else {
    				uIndices[ii] = lIndices[ii];
    			}
    		}
    		offset = tmp;
    		
            if (!this.writeAttributeColumnIndicesSub(el, KEY_LOWER_ERROR_BAR_COLUMN_INDICES,
            		lIndices)) {
            	return false;
            }
            if (!this.writeAttributeColumnIndicesSub(el, KEY_UPPER_ERROR_BAR_COLUMN_INDICES,
            		uIndices)) {
            	return false;
            }

        	int[] ehIndices = new int[this.mErrorBarHolderIndices.length];
    		for (int ii = 0; ii < ehIndices.length; ii++) {
    			int index = -1;
    			for (int jj = 0; jj < holders.length; jj++) {
    				if (holders[jj].equals(this.mErrorBarHolderIndices[ii])) {
    					index = jj;
    					break;
    				}
    			}
    			if (index == -1) {
    				return false;
    			}
    			ehIndices[ii] = index + holderOffset;
    		}
            if (!this.writeAttributeColumnIndicesSub(el, KEY_ERROR_BAR_HOLDER_COLUMN_INDICES,
            		ehIndices)) {
            	return false;
            }
        }

        if (this.isTickLabelAvailable() && this.hasGenericTickLabels()) {
        	// tick labels

        	int[] tIndices = new int[this.mTickLabelIndices.length];
    		for (int ii = 0; ii < tIndices.length; ii++) {
    			tIndices[ii] = ii + offset;
    		}
            if (!this.writeAttributeColumnIndicesSub(el, KEY_TICK_LABEL_COLUMN_INDICES,
            		tIndices)) {
            	return false;
            }

        	int[] thIndices = new int[this.mTickLabelHolderIndices.length];
    		for (int ii = 0; ii < thIndices.length; ii++) {
    			int index = -1;
    			for (int jj = 0; jj < holders.length; jj++) {
    				if (holders[jj].equals(this.mTickLabelHolderIndices[ii])) {
    					index = jj;
    					break;
    				}
    			}
    			if (index == -1) {
    				return false;
    			}
    			thIndices[ii] = index + holderOffset;
    		}
            if (!this.writeAttributeColumnIndicesSub(el, KEY_TICK_LABEL_HOLDER_COLUMN_INDICES,
            		thIndices)) {
            	return false;
            }
        }

        return true;
    }

    private boolean writeAttributeColumnNameSub(Element el, String key, int[] indices) {
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        for (int ii = 0; ii < indices.length; ii++) {
            sb.append(this.getSequentialColumnName(indices[ii]));
            if (ii != indices.length - 1) {
                sb.append(',');
            }
        }
        sb.append('}');
        el.setAttribute(key, sb.toString());
        return true;
    }

    @Override
    protected boolean writeSequentialColumnName(Element el) {
        int[] xIndices, yIndices;
        if (this.hasMultipleYValues()) {
            xIndices = new int[] { 0 };
            yIndices = new int[this.mYIndices.length];
            for (int ii = 0; ii < yIndices.length; ii++) {
                yIndices[ii] = ii + 1;
            }
        } else {
            xIndices = new int[this.mXIndices.length];
            for (int ii = 0; ii < xIndices.length; ii++) {
                xIndices[ii] = ii;
            }
            yIndices = new int[] { xIndices.length };
        }

        // write indices
        if (!this.writeAttributeColumnNameSub(el, KEY_X_VALUE_COLUMN_INDICES, xIndices)) {
            return false;
        }
        if (!this.writeAttributeColumnNameSub(el, KEY_Y_VALUE_COLUMN_INDICES, yIndices)) {
            return false;
        }

		final Integer[] holders = this.hasMultipleYValues() ? this.mYIndices : this.mXIndices;
		final int holderOffset = this.hasMultipleYValues() ? 1 : 0;

        int offset = xIndices.length + yIndices.length;
        if (this.isErrorBarAvailable()) {
        	// error bars
        	final int errLen = this.mLowerErrorIndices.length;
        	int[] lIndices = new int[errLen];
    		int[] uIndices = new int[errLen];
    		boolean[] sameErrorVariableFlags = this.getSameErrorVariableFlags();
    		int errCnt = 0;
    		for (int ii = 0; ii < errLen; ii++) {
    			lIndices[ii] = offset + errCnt;
    			if (!sameErrorVariableFlags[ii]) {
    				uIndices[ii] = lIndices[ii];
    			} else {
        			uIndices[ii] = lIndices[ii] + 1;
        			errCnt++;
    			}
    			errCnt++;
    		}
    		offset += errCnt;
    		
            if (!this.writeAttributeColumnIndicesSub(el, KEY_LOWER_ERROR_BAR_COLUMN_INDICES,
            		lIndices)) {
            	return false;
            }
            if (!this.writeAttributeColumnIndicesSub(el, KEY_UPPER_ERROR_BAR_COLUMN_INDICES,
            		uIndices)) {
            	return false;
            }

        	int[] ehIndices = new int[this.mErrorBarHolderIndices.length];
    		for (int ii = 0; ii < ehIndices.length; ii++) {
    			int index = -1;
    			for (int jj = 0; jj < holders.length; jj++) {
    				if (holders[jj].equals(this.mErrorBarHolderIndices[ii])) {
    					index = jj;
    					break;
    				}
    			}
    			if (index == -1) {
    				return false;
    			}
    			ehIndices[ii] = index + holderOffset;
    		}
            if (!this.writeAttributeColumnNameSub(el, KEY_ERROR_BAR_HOLDER_COLUMN_INDICES, ehIndices)) {
                return false;
            }
        }

        if (this.isTickLabelAvailable() && this.hasGenericTickLabels()) {
            // tick labels

            int[] tIndices = new int[this.mTickLabelIndices.length];
            for (int ii = 0; ii < tIndices.length; ii++) {
                tIndices[ii] = ii + offset;
            }
            if (!this.writeAttributeColumnNameSub(el, KEY_TICK_LABEL_COLUMN_INDICES, tIndices)) {
                return false;
            }

        	int[] thIndices = new int[this.mTickLabelHolderIndices.length];
    		for (int ii = 0; ii < thIndices.length; ii++) {
    			int index = -1;
    			for (int jj = 0; jj < holders.length; jj++) {
    				if (holders[jj].equals(this.mTickLabelHolderIndices[ii])) {
    					index = jj;
    					break;
    				}
    			}
    			if (index == -1) {
    				return false;
    			}
    			thIndices[ii] = index + holderOffset;
    		}
            if (!this.writeAttributeColumnNameSub(el, KEY_TICK_LABEL_HOLDER_COLUMN_INDICES, thIndices)) {
                return false;
            }
        }

        // serial numbers
        el.setAttribute(KEY_INDEX_VARIABLE_NAME, INDEX);

        return true;
    }

    /**
     * Write sampling rate to the Element.
     *
     * @param el the Element object
     * @return true
     */
    public boolean writeSamplingRateToProperty(Element el) {
        SGDataColumn[] columns = this.getExportedColumnsClone();
        for (int i = 0; i < columns.length; i++) {
            if (SGIDataColumnTypeConstants.VALUE_TYPE_SAMPLING_RATE.equals(columns[i].getValueType())) {
                SGSamplingDataColumn scol = (SGSamplingDataColumn)columns[i];
                el.setAttribute(SGIFigureElementGraph.KEY_SAMPLING_RATE, Double.toString(scol.getSamplingRate()));
                break;
            }
        }
        return true;
    }

    /**
     * Returns the title of X values.
     *
     * @return
     *         the title of X values
     */
    public String getTitleX() {
    	if (this.hasMultipleYValues() || this.hasPairOfXYValues()) {
            SGDataColumn[] columns = this.getDataFile().mDataColumns;
            return columns[this.mXIndices[0].intValue()].getTitle();
    	} else {
    		return "";
    	}
    }

    /**
     * Returns the title of Y values.
     *
     * @return
     *         the title of Y values
     */
    public String getTitleY() {
    	if (!this.hasMultipleYValues() || this.hasPairOfXYValues()) {
            SGDataColumn[] columns = this.getDataFile().mDataColumns;
            return columns[this.mYIndices[0].intValue()].getTitle();
    	} else {
    		return "";
    	}
    }

    public Integer[] getXIndices() {
        if (this.mXIndices == null) {
            return null;
        } else {
            return (Integer[]) mXIndices.clone();
        }
    }

    public void setXIndices(Integer[] indices) {
        if (indices == null) {
            this.mXIndices = null;
        } else {
        	if (!this.isValidIndices(indices)) {
                throw new IllegalArgumentException("Column indices for x values are invalid.");
        	}
            this.mXIndices = (Integer[]) indices.clone();
        }
    }

    public Integer[] getYIndices() {
        if (this.mYIndices == null) {
            return null;
        } else {
            return (Integer[]) mYIndices.clone();
        }
    }

    public void setYIndices(Integer[] indices) {
        if (indices == null) {
            this.mYIndices = null;
        } else {
        	if (!this.isValidIndices(indices)) {
                throw new IllegalArgumentException("Column indices for y values are invalid.");
        	}
            this.mYIndices = (Integer[]) indices.clone();
        }
    }

    public Integer[] getLowerErrorIndices() {
        if (this.mLowerErrorIndices == null) {
            return null;
        } else {
            return (Integer[]) this.mLowerErrorIndices.clone();
        }
    }

    public void setLowerErrorIndices(Integer[] indices) {
        if (indices == null) {
        	this.mLowerErrorIndices = null;
        } else {
            for (int ii = 0; ii < indices.length; ii++) {
                final Integer index = indices[ii];
                if (this.checkColumnIndexRange(index) == false) {
                    throw new IllegalArgumentException("Index out of bounds: " + index);
                }
            }
            this.mLowerErrorIndices = (Integer[]) indices.clone();
        }
    }

    public Integer[] getUpperErrorIndices() {
        if (this.mUpperErrorIndices == null) {
            return null;
        } else {
            return (Integer[]) this.mUpperErrorIndices.clone();
        }
    }

    public void setUpperErrorIndices(Integer[] indices) {
        if (indices == null) {
        	this.mUpperErrorIndices = null;
        } else {
            for (int ii = 0; ii < indices.length; ii++) {
                final Integer index = indices[ii];
                if (this.checkColumnIndexRange(index) == false) {
                    throw new IllegalArgumentException("Index out of bounds: " + index);
                }
            }
            this.mUpperErrorIndices = (Integer[]) indices.clone();
        }
    }

    public Integer[] getErrorBarHolderIndices() {
        if (this.mErrorBarHolderIndices == null) {
            return null;
        } else {
            return (Integer[]) this.mErrorBarHolderIndices.clone();
        }
    }

    public void setErrorBarHolderIndices(Integer[] indices) {
        if (indices == null) {
        	this.mErrorBarHolderIndices = null;
        } else {
            for (int ii = 0; ii < indices.length; ii++) {
                final Integer index = indices[ii];
                if (this.checkColumnIndexRange(index) == false) {
                    throw new IllegalArgumentException("Index out of bounds: " + index);
                }
            }
            this.mErrorBarHolderIndices = (Integer[]) indices.clone();
        }
    }

    public Integer[] getTickLabelIndices() {
        if (this.mTickLabelIndices == null) {
            return null;
        } else {
            return (Integer[]) this.mTickLabelIndices.clone();
        }
    }

    public void setTickLabelIndices(Integer[] indices) {
        if (indices == null) {
        	this.mTickLabelIndices = null;
        } else {
            for (int ii = 0; ii < indices.length; ii++) {
                final Integer index = indices[ii];
                if (this.checkColumnIndexRange(index) == false) {
                    throw new IllegalArgumentException("Index out of bounds: " + index);
                }
            }
            this.mTickLabelIndices = (Integer[]) indices.clone();
        }
    }

    public Integer[] getTickLabelHolderIndices() {
        if (this.mTickLabelHolderIndices == null) {
            return null;
        } else {
            return (Integer[]) this.mTickLabelHolderIndices.clone();
        }
    }

    public void setTickLabelHolderIndices(Integer[] indices) {
        if (indices == null) {
        	this.mTickLabelHolderIndices = null;
        } else {
            for (int ii = 0; ii < indices.length; ii++) {
                final Integer index = indices[ii];
                if (this.checkColumnIndexRange(index) == false) {
                    throw new IllegalArgumentException("Index out of bounds: " + index);
                }
            }
            this.mTickLabelHolderIndices = (Integer[]) indices.clone();
        }
    }

    /**
     * Returns properties of this data.
     * @return
     *         properties of this data
     */
    public SGProperties getProperties() {
        SGProperties p = new SXYMultipleDataProperties();
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
        if ((p instanceof SXYMultipleDataProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        SXYMultipleDataProperties ep = (SXYMultipleDataProperties) p;
        ep.mXIndices = this.getXIndices();
        ep.mYIndices = this.getYIndices();
        ep.mLowerErrorIndices = this.getLowerErrorIndices();
        ep.mUpperErrorIndices = this.getUpperErrorIndices();
        ep.mErrorBarHolderIndices = this.getErrorBarHolderIndices();
        ep.mTickLabelIndices = this.getTickLabelIndices();
        ep.mTickLabelHolderIndices = this.getTickLabelHolderIndices();
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
        if ((p instanceof SXYMultipleDataProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        SXYMultipleDataProperties ep = (SXYMultipleDataProperties) p;
        this.setXIndices(ep.mXIndices);
        this.setYIndices(ep.mYIndices);
        this.setLowerErrorIndices(ep.mLowerErrorIndices);
        this.setUpperErrorIndices(ep.mUpperErrorIndices);
        this.setErrorBarHolderIndices(ep.mErrorBarHolderIndices);
        this.setTickLabelIndices(ep.mTickLabelIndices);
        this.setTickLabelHolderIndices(ep.mTickLabelHolderIndices);
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
        List<Integer> xList = new ArrayList<Integer>();
        List<Integer> yList = new ArrayList<Integer>();
        List<Integer> lList = new ArrayList<Integer>();
        List<Integer> uList = new ArrayList<Integer>();
        List<Integer> tList = new ArrayList<Integer>();
        for (int ii = 0; ii < columns.length; ii++) {
        	String valueType = cols[ii].getValueType();
            if (SGDataUtility.isEqualColumnType(X_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)
            			&& !VALUE_TYPE_DATE.equals(valueType)
            			&& !VALUE_TYPE_SAMPLING_RATE.equals(valueType)) {
            		return false;
            	}
                xList.add(Integer.valueOf(ii));
            } else if (SGDataUtility.isEqualColumnType(Y_VALUE, columns[ii])) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)
            			&& !VALUE_TYPE_DATE.equals(valueType)
            			&& !VALUE_TYPE_SAMPLING_RATE.equals(valueType)) {
            		return false;
            	}
                yList.add(Integer.valueOf(ii));
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], LOWER_ERROR_VALUE)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                lList.add(Integer.valueOf(ii));
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], UPPER_ERROR_VALUE)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                uList.add(Integer.valueOf(ii));
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], LOWER_UPPER_ERROR_VALUE)) {
            	if (!VALUE_TYPE_NUMBER.equals(valueType)) {
            		return false;
            	}
                lList.add(Integer.valueOf(ii));
                uList.add(Integer.valueOf(ii));
            } else if (SGDataUtility.columnTypeStartsWith(columns[ii], TICK_LABEL)) {
                tList.add(Integer.valueOf(ii));
            } else if ("".equals(columns[ii])) {
                continue;
            } else {
            	return false;
            }
        }

        // check values
        if (xList.size() == 0 || yList.size() == 0) {
        	return false;
        }
        if (xList.size() != 1 && yList.size() != 1) {
        	return false;
        }
        if (lList.size() != uList.size()) {
        	return false;
        }

        String[] columnTitles = this.getDataFile().getTitles();

        Integer[] xIndices = xList.toArray(new Integer[xList.size()]);
        Integer[] yIndices = yList.toArray(new Integer[yList.size()]);
        Integer[] lIndices = null;
        if (lList.size() != 0) {
            lIndices = lList.toArray(new Integer[lList.size()]);
        }
        Integer[] uIndices = null;
        if (uList.size() != 0) {
            uIndices = uList.toArray(new Integer[uList.size()]);
        }
        Integer[] ehIndices = null;
        if (lList.size() != 0) {
            ehIndices = new Integer[lList.size()];
            List<Integer> ehueList = new ArrayList<Integer>();
            for (int ii = 0; ii < ehIndices.length; ii++) {
                String str = columns[uIndices[ii].intValue()];
                Integer eh = SGDataUtility.getAppendedColumnIndex(str, columnTitles);
                if (eh == null) {
                    return false;
                }
                ehueList.add(eh);
            }
            for (int ii = 0; ii < ehIndices.length; ii++) {
                String str = columns[lIndices[ii].intValue()];
                Integer eh = SGDataUtility.getAppendedColumnIndex(str, columnTitles);
                if (eh == null) {
                    return false;
                }
                if (ehueList.contains(eh) == false) {
                    return false;
                }
                ehIndices[ii] = eh;
            }
        }
        
        Integer[] tIndices = null;
        if (tList.size() != 0) {
            tIndices = tList.toArray(new Integer[tList.size()]);
        }
        Integer[] thIndices = null;
        if (tList.size() != 0) {
            thIndices = new Integer[tList.size()];
            for (int ii = 0; ii < thIndices.length; ii++) {
                String str = columns[tIndices[ii].intValue()];
                Integer th = SGDataUtility.getAppendedColumnIndex(str, columnTitles);
                if (th == null) {
                    return false;
                }
                thIndices[ii] = th;
            }
        }

        // set to the attributes
        this.mXIndices = xIndices;
        this.mYIndices = yIndices;
        this.mLowerErrorIndices = lIndices;
        this.mUpperErrorIndices = uIndices;
        this.mErrorBarHolderIndices = ehIndices;
        this.mTickLabelIndices = tIndices;
        this.mTickLabelHolderIndices = thIndices;

        if (tIndices == null) {
        	this.updateDateTickLabelColumns();
        }

        return true;
    }

    /**
     * A class for multiple scalar xy-type data properties.
     *
     */
    public static class SXYMultipleDataProperties extends SDArrayDataProperties {

        // indices of data columns
        Integer[] mXIndices = null;

        Integer[] mYIndices = null;

        Integer[] mLowerErrorIndices = null;

        Integer[] mUpperErrorIndices = null;

        Integer[] mErrorBarHolderIndices = null;

        Integer[] mTickLabelIndices = null;

        Integer[] mTickLabelHolderIndices = null;

        SGIntegerSeriesSet mTickLabelStride = null;

        /**
         * The default constructor.
         */
        public SXYMultipleDataProperties() {
            super();
        }

    	@Override
    	public boolean equals(Object obj) {
            if ((obj instanceof SXYMultipleDataProperties) == false) {
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
         * @param p
         *          a data property
         * @return true if this data property has the equal column types with given data property
         */
        @Override
        public boolean hasEqualColumnTypes(DataProperties dp) {
            if ((dp instanceof SXYMultipleDataProperties) == false) {
                return false;
            }
            SXYMultipleDataProperties p = (SXYMultipleDataProperties) dp;
            if (!SGUtility.equals(this.mXIndices, p.mXIndices)) {
            	return false;
            }
            if (!SGUtility.equals(this.mYIndices, p.mYIndices)) {
            	return false;
            }
            if (!SGUtility.equals(this.mLowerErrorIndices, p.mLowerErrorIndices)) {
            	return false;
            }
            if (!SGUtility.equals(this.mUpperErrorIndices, p.mUpperErrorIndices)) {
            	return false;
            }
            if (!SGUtility.equals(this.mErrorBarHolderIndices, p.mErrorBarHolderIndices)) {
            	return false;
            }
            if (!SGUtility.equals(this.mTickLabelIndices, p.mTickLabelIndices)) {
            	return false;
            }
            if (!SGUtility.equals(this.mTickLabelHolderIndices, p.mTickLabelHolderIndices)) {
            	return false;
            }
            return true;
        }

    	@Override
        public boolean hasEqualSize(DataProperties dp) {
            if ((dp instanceof SXYMultipleDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualSize(dp)) {
            	return false;
            }
            SXYMultipleDataProperties p = (SXYMultipleDataProperties) dp;
            if (!SGUtility.equals(this.mTickLabelStride, p.mTickLabelStride)) {
            	return false;
            }
        	return true;
        }

        /**
         * Copy this object.
         * @return a copied object
         */
        public Object copy() {
            Object obj = super.copy();
            SXYMultipleDataProperties p = (SXYMultipleDataProperties) obj;
            if (this.mXIndices != null) {
                p.mXIndices = (Integer[]) this.mXIndices.clone();
            }
            if (this.mYIndices != null) {
                p.mYIndices = (Integer[]) this.mYIndices.clone();
            }
            if (this.mLowerErrorIndices != null) {
                p.mLowerErrorIndices = (Integer[]) this.mLowerErrorIndices.clone();
            }
            if (this.mUpperErrorIndices != null) {
                p.mUpperErrorIndices = (Integer[]) this.mUpperErrorIndices.clone();
            }
            if (this.mErrorBarHolderIndices != null) {
                p.mErrorBarHolderIndices = (Integer[]) this.mErrorBarHolderIndices.clone();
            }
            if (this.mTickLabelIndices != null) {
            	p.mTickLabelIndices = (Integer[]) this.mTickLabelIndices.clone();
            }
            if (this.mTickLabelHolderIndices != null) {
            	p.mTickLabelHolderIndices = (Integer[]) this.mTickLabelHolderIndices.clone();
            }
        	p.mTickLabelStride = (this.mTickLabelStride != null) ? (SGIntegerSeriesSet) this.mTickLabelStride.clone() : null;
            return p;
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mXIndices = null;
            this.mYIndices = null;
            this.mLowerErrorIndices = null;
            this.mUpperErrorIndices = null;
            this.mErrorBarHolderIndices = null;
            this.mTickLabelIndices = null;
            this.mTickLabelHolderIndices = null;
            this.mTickLabelStride = null;
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
        for (int ii = 0; ii < this.mXIndices.length; ii++) {
            array[this.mXIndices[ii].intValue()] = X_VALUE;
        }
        for (int ii = 0; ii < this.mYIndices.length; ii++) {
            array[this.mYIndices[ii].intValue()] = Y_VALUE;
        }
        if (this.isErrorBarAvailable()) {
            for (int ii = 0; ii < this.mLowerErrorIndices.length; ii++) {
                int index = this.mErrorBarHolderIndices[ii].intValue();
                if (this.mLowerErrorIndices[ii].equals(this.mUpperErrorIndices[ii])) {
                    array[this.mLowerErrorIndices[ii].intValue()] =
                        SGDataUtility.appendColumnNoOrTitle(
                                LOWER_UPPER_ERROR_VALUE, index,
                                this.getDataFile().isEmptyTitle(index) || this.getDataFile().isRepeatedTitle(index),
                                this.getTitle(index));
                } else {
                    array[this.mLowerErrorIndices[ii].intValue()] =
                        SGDataUtility.appendColumnNoOrTitle(
                                LOWER_ERROR_VALUE, index,
                                this.getDataFile().isEmptyTitle(index) || this.getDataFile().isRepeatedTitle(index),
                                this.getTitle(index));
                    array[this.mUpperErrorIndices[ii].intValue()] =
                        SGDataUtility.appendColumnNoOrTitle(
                                UPPER_ERROR_VALUE, index,
                                this.getDataFile().isEmptyTitle(index) || this.getDataFile().isRepeatedTitle(index),
                                this.getTitle(index));
                }
            }
        }
        if (this.isTickLabelAvailable() && this.hasGenericTickLabels()) {
            for (int ii = 0; ii < this.mTickLabelIndices.length; ii++) {
                int index = this.mTickLabelHolderIndices[ii].intValue();
                array[this.mTickLabelIndices[ii].intValue()] =
                    SGDataUtility.appendColumnNoOrTitle(
                            TICK_LABEL, index,
                            this.getDataFile().isEmptyTitle(index) || this.getDataFile().isRepeatedTitle(index),
                            this.getTitle(index));
            }
        }
        return array;
    }

    /**
	 * Returns the maximum number of SXY data objects that can be obtained from
	 * this data object.
	 *
	 * @return the maximum number of SXY data objects that can be obtained from
	 *         this data object
	 */
    public int getSXYDataNumMax() {
//        return this.getColNum(SGNumberDataColumn.class) - 1;
        int num = this.getColNum(SGNumberDataColumn.class) - 1;
        final int sNum = this.getColNum(SGSamplingDataColumn.class);
        if (sNum != 0) {
        	num++;
        }
        return num;
    }

    /**
     * Returns whether a data type column is used.
     *
     * @return true if a data type column is used
     */
    public boolean isDateColumnUsed() {
    	if (this.getDateColumnIndex(this.mXIndices) != -1) {
    		return true;
    	}
    	if (this.getDateColumnIndex(this.mYIndices) != -1) {
    		return true;
    	}
    	return false;
    }

    private int getDateColumnIndex(Integer[] indices) {
    	SGDataColumn[] columns = this.getDataFile().mDataColumns;
    	for (int ii = 0; ii < indices.length; ii++) {
    		SGDataColumn col = columns[indices[ii]];
    		if (col instanceof SGDateDataColumn) {
    			return ii;
    		}
    	}
    	return -1;
    }

    /**
     * Returns an array of data columns.
     * 
     * @return an array of data columns
     */
    public SGDataColumn[] getExportedColumns() {
    	final boolean be = this.isErrorBarAvailable();
    	final boolean bt = this.isTickLabelAvailable() && this.hasGenericTickLabels();
    	SGDataColumn[] columns = this.getDataFile().mDataColumns;
    	boolean[] sameErrorVariableFlags = be ? this.getSameErrorVariableFlags() : null;
    	int len = this.mXIndices.length + this.mYIndices.length;
    	if (be) {
    		for (int ii = 0; ii < sameErrorVariableFlags.length; ii++) {
				final int num = sameErrorVariableFlags[ii] ? 1 : 2;
				len += num;
    		}
    	}
    	if (bt) {
    		len += this.mTickLabelIndices.length;
    	}
    	int offset = 0;
        SGDataColumn[] colArray = new SGDataColumn[len];
		for (int ii = 0; ii < this.mXIndices.length; ii++) {
			colArray[ii] = columns[this.mXIndices[ii].intValue()];
		}
		offset += this.mXIndices.length;
		for (int ii = 0; ii < this.mYIndices.length; ii++) {
			colArray[ii + offset] = columns[this.mYIndices[ii].intValue()];
		}
		offset += this.mYIndices.length;
		if (be) {
			int tmp = offset;
			int cnt = 0;
    		for (int ii = 0; ii < sameErrorVariableFlags.length; ii++) {
    			SGDataColumn leCol = columns[this.mLowerErrorIndices[cnt].intValue()];
    			colArray[tmp] = leCol;
				tmp++;
    			if (!sameErrorVariableFlags[ii]) {
    				colArray[tmp] = columns[this.mUpperErrorIndices[cnt].intValue()];
    				tmp++;
    			}
    			cnt++;
    		}
    		offset = tmp;
		}
		if (bt) {
			for (int ii = 0; ii < this.mTickLabelIndices.length; ii++) {
				colArray[ii + offset] = columns[this.mTickLabelIndices[ii].intValue()];
			}
		}
		return colArray;
    }

    @Override
    public SGDataColumn[] getUsedDataColumnsClone() {
        SGDataColumn[] columns = this.getDataFile().mDataColumns;
        String[] columnTypes = this.getCurrentColumnType();
        List<SGDataColumn> collist = new ArrayList<SGDataColumn>();

        for (int i = 0; i < this.mXIndices.length; i++) {
            int index = this.mXIndices[i].intValue();
            SGDataColumn col = (SGDataColumn)columns[index].clone();
            col.setColumnType(columnTypes[index]);
            collist.add(col);
        }

        for (int i = 0; i < this.mYIndices.length; i++) {
            int index = this.mYIndices[i].intValue();
            SGDataColumn col = (SGDataColumn)columns[index].clone();
            col.setColumnType(columnTypes[index]);
            collist.add(col);
        }

        if (this.isErrorBarAvailable()) {
            for (int i = 0; i < this.mLowerErrorIndices.length; i++) {
                int index1 = this.mLowerErrorIndices[i].intValue();
                int index2 = this.mUpperErrorIndices[i].intValue();
                if (index1==index2) {
                    SGDataColumn col = (SGDataColumn)columns[index1].clone();
                    col.setColumnType(columnTypes[index1]);
                    collist.add(col);
                } else {
                    SGDataColumn col = (SGDataColumn)columns[index1].clone();
                    col.setColumnType(columnTypes[index1]);
                    collist.add(col);
                    col = (SGDataColumn)columns[index2].clone();
                    col.setColumnType(columnTypes[index2]);
                    collist.add(col);
                }
            }
        }
        if (this.isTickLabelAvailable()) {
            for (int i = 0; i < this.mTickLabelIndices.length; i++) {
                int index = this.mTickLabelIndices[i].intValue();
                SGDataColumn col = (SGDataColumn)columns[index].clone();
                col.setColumnType(columnTypes[index]);
                collist.add(col);
            }
        }

        return collist.toArray(new SGDataColumn[collist.size()]);
    }

    /**
     * Returns whether error bars are available.
     *
     * @return true if error bars are available
     */
    public boolean isErrorBarAvailable() {
        return (this.mLowerErrorIndices != null && this.mUpperErrorIndices != null);
    }

    /**
     * Returns whether tick labels are available.
     *
     * @return true if tick labels are available
     */
    public boolean isTickLabelAvailable() {
		return (this.mTickLabelIndices != null);
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
	 * Returns whether the error bars are vertical. If this data does not have
	 * error values, returns null.
	 *
	 * @return true if error bars are vertical, false if they are horizontal and
	 *         null if this data do not have error values
	 */
	public Boolean isErrorBarVertical() {
    	if (this.isErrorBarAvailable()) {
    		if (SGUtility.contains(this.mYIndices, this.mErrorBarHolderIndices[0])) {
    			return Boolean.TRUE;
    		}
    		if (SGUtility.contains(this.mXIndices, this.mErrorBarHolderIndices[0])) {
    			return Boolean.FALSE;
    		}
    	}
		return null;
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
    		if (SGUtility.contains(this.mYIndices, this.mTickLabelHolderIndices[0])) {
    			return Boolean.TRUE;
    		}
    		if (SGUtility.contains(this.mXIndices, this.mTickLabelHolderIndices[0])) {
    			return Boolean.FALSE;
    		}
    	} else {
    		if (this.getDateColumnIndex(this.mXIndices) != -1) {
        		return Boolean.TRUE;
    		}
    		if (this.getDateColumnIndex(this.mYIndices) != -1) {
        		return Boolean.FALSE;
    		}
    	}
		return null;
	}

	public SGDataColumn[] getXColumns() {
		return this.getColumns(this.mXIndices);
	}

	public SGDataColumn[] getYColumns() {
		return this.getColumns(this.mYIndices);
	}

	public SGDataColumn[] getLowerErrorColumns() {
		return this.getColumns(this.mLowerErrorIndices);
	}

	public SGDataColumn[] getUpperErrorColumns() {
		return this.getColumns(this.mUpperErrorIndices);
	}

	public SGDataColumn[] getErrorBarHolderColumns() {
		return this.getColumns(this.mErrorBarHolderIndices);
	}

	public SGDataColumn[] getTickLableColumns() {
		return this.getColumns(this.mTickLabelIndices);
	}

	public SGDataColumn[] getTickLabelHolderColumns() {
		return this.getColumns(this.mTickLabelHolderIndices);
	}

	public SGDataColumn[] getMultipleColumns() {
        if (this.hasMultipleYValues()) {
        	return this.getYColumns();
        } else {
        	return this.getXColumns();
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

    	// add the sampling rate if it exists
    	SGDataColumn[] dataColumns = this.getTextDataFile().getDataColumns();
    	for (int ii = 0; ii < dataColumns.length; ii++) {
    		if (dataColumns[ii] instanceof SGSamplingDataColumn) {
    			SGSamplingDataColumn sampleColumn = (SGSamplingDataColumn) dataColumns[ii];
    			final double samplingRate = sampleColumn.getSamplingRate();
    			infoMap.put(SGIDataInformationKeyConstants.KEY_SAMPLING_RATE, samplingRate);
    			break;
    		}
    	}

    	return infoMap;
    }

    @Override
    public boolean isSplitEnabled() {
        final boolean by = this.hasMultipleYValues();
        final int dataNum = by ? this.getYNumber() : this.getXNumber();

        if (dataNum>1) {
            return true;
        }
        return false;
    }

    public static SGISXYTypeMultipleData merge(final List<SGData> dataList) {
        if (dataList.size()==0) {
            return null;
        }
        for (SGData data : dataList) {
            if ((data instanceof SGSXYSDArrayMultipleData)==false) {
                return null;
            }
        }

        SGSXYSDArrayMultipleData data0 = (SGSXYSDArrayMultipleData)dataList.get(0);
        SGSDArrayFile dataFile = data0.getDataFile();
		SGSXYSDArrayMultipleData dataLast = (SGSXYSDArrayMultipleData) dataList.get(dataList.size() - 1);
        SGDataSourceObserver obs = data0.getDataSourceObserver();

        Set<Integer> xset = new TreeSet<Integer>();
        Set<Integer> yset = new TreeSet<Integer>();
        Set<Integer> leset = new TreeSet<Integer>();
        Set<Integer> ueset = new TreeSet<Integer>();
        Set<Integer> ehset = new TreeSet<Integer>();
        Set<Integer> tset = new TreeSet<Integer>();
        Set<Integer> thset = new TreeSet<Integer>();
        for (SGData data : dataList) {
            SGSXYSDArrayMultipleData dataMult = (SGSXYSDArrayMultipleData)data;
            Integer[] x = dataMult.getXIndices();
            Integer[] y = dataMult.getYIndices();
            Integer[] le = dataMult.getLowerErrorIndices();
            Integer[] ue = dataMult.getUpperErrorIndices();
            Integer[] eh = dataMult.getErrorBarHolderIndices();
            Integer[] t = dataMult.getTickLabelIndices();
            Integer[] th = dataMult.getTickLabelHolderIndices();
            if (null!=x) {
                for (int i = 0; i < x.length; i++) {
                    xset.add(x[i]);
                }
            }
            if (null!=y) {
                for (int i = 0; i < y.length; i++) {
                    yset.add(y[i]);
                }
            }
            if (null!=le) {
                for (int i = 0; i < le.length; i++) {
                    leset.add(le[i]);
                }
            }
            if (null!=ue) {
                for (int i = 0; i < ue.length; i++) {
                    ueset.add(ue[i]);
                }
            }
            if (null!=eh) {
                for (int i = 0; i < eh.length; i++) {
                    ehset.add(eh[i]);
                }
            }
            if (null!=t) {
                for (int i = 0; i < t.length; i++) {
                    tset.add(t[i]);
                }
            }
            if (null!=th) {
                for (int i = 0; i < th.length; i++) {
                    thset.add(th[i]);
                }
            }
        }

        Integer[] x = new Integer[xset.size()];
        Integer[] y = new Integer[yset.size()];
        Integer[] le = new Integer[leset.size()];
        Integer[] ue = new Integer[ueset.size()];
        Integer[] eh = new Integer[ehset.size()];
        Integer[] t = new Integer[tset.size()];
        Integer[] th = new Integer[thset.size()];
        xset.toArray(x);
        yset.toArray(y);
        leset.toArray(le);
        ueset.toArray(ue);
        ehset.toArray(eh);
        tset.toArray(t);
        thset.toArray(th);
        
        // a measure for data objects with date type column
        if (t.length != th.length && t.length == 1) {
        	SGDataColumn[] cols = dataFile.getDataColumns();
        	Integer tIndex = t[0];
        	if (cols[tIndex] instanceof SGDateDataColumn) {
        		Integer[] tNew = new Integer[th.length];
        		Arrays.fill(tNew, tIndex);
        		t = tNew;
        	} else {
        		return null;
        	}
        }

        SGSXYSDArrayMultipleData data = new SGSXYSDArrayMultipleData(
                dataFile, obs, x, y, le, ue, eh, t, th, dataLast.getStride(),
                dataLast.getTickLabelStride(), dataLast.isStrideAvailable(),
                dataLast.getSamplingRate());
        data.setDecimalPlaces(data0.getDecimalPlaces());
        data.setExponent(data0.getExponent());
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
    	return SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA;
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
     * Writes properties of this data object to the Element.
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

        // tick label stride
    	if (this.isTickLabelAvailable()) {
            el.setAttribute(SGIDataPropertyKeyConstants.KEY_TICK_LABEL_ARRAY_SECTION, this.mTickLabelStride.toString());
    	}

        return true;
    }

    /**
     * Exports the data into a NetCDF file.
     * 
     * @param ncWrite
     *           the file to save
     * @param mode
     *           the mode to save
     * @param policy
     *           the policy for exporting data
     * @return true if succeeded
     */
	@Override
    public boolean exportToNetCDFFile(NetcdfFileWriteable ncWrite, final SGExportParameter mode, 
			SGDataBufferPolicy policy) throws IOException, InvalidRangeException {

		//
		// function creating NetCDF data file
		//

		if (!this.hasMultipleYValues()) {
			return false;
		}
		
		final int childNum = this.getChildNumber();
        final int dataNum = this.getAllPointsNumber();
        
        SGSXYDataBufferPolicy sxyPolicy = (SGSXYDataBufferPolicy) policy;
		double[][] xValues = this.getXValueArray(sxyPolicy);
		double[][] yValues = this.getYValueArray(sxyPolicy);
		double[][] leValues = this.getLowerErrorValueArray(sxyPolicy);
		double[][] ueValues = this.getUpperErrorValueArray(sxyPolicy);
		String[][] tickLabels = this.getTickLabelArray(sxyPolicy);
        
        String xName = "X";
        String yName = "Y";
        String[] yNames = new String[childNum];
        if (childNum == 1) {
        	yNames[0] = yName;
        } else {
    		for (int ii = 0; ii < childNum; ii++) {
    			yNames[ii] = yName + (ii + 1);
    		}
        }
		String leName = "LowerError";
		String ueName = "UpperError";
		final boolean eFlag = this.isErrorBarAvailable() && (childNum == 1);
		String tlName = "TickLabel";
		final boolean tlFlag = this.isTickLabelAvailable() && (childNum == 1);
		String lenName = "clen";
		
		Dimension xDim = new Dimension(xName, dataNum);
		ncWrite.addDimension(null, xDim);
		
		Variable xVar = new Variable(ncWrite, null, null, xName, DataType.DOUBLE, xName);
		xVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
				SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		ncWrite.addVariable(null, xVar);

		for (int ii = 0; ii < childNum; ii++) {
			Variable yVar = new Variable(ncWrite, null, null, yNames[ii], DataType.DOUBLE, xName);
			yVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
			ncWrite.addVariable(null, yVar);
		}
		
		if (eFlag) {
			Variable leVar = new Variable(ncWrite, null, null, leName, DataType.DOUBLE, xName);
			leVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
			ncWrite.addVariable(null, leVar);
			Variable ueVar = new Variable(ncWrite, null, null, ueName, DataType.DOUBLE, xName);
			ueVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
			ncWrite.addVariable(null, ueVar);
		}
		
		int maxLen = 0;
		if (tlFlag) {
			String[] tlArray = tickLabels[0];
			for (int ii = 0; ii < tlArray.length; ii++) {
				String tl = tlArray[ii];
				final int len = tl.getBytes().length;
				if (len > maxLen) {
					maxLen = len;
				}
			}
			
			Dimension lenDim = new Dimension(lenName, maxLen);
			ncWrite.addDimension(null, lenDim);
			
			String dimString = xName + " " + lenName;
			Variable tlVar = new Variable(ncWrite, null, null, tlName, DataType.CHAR, dimString);
			tlVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
					SGIDataColumnTypeConstants.VALUE_TYPE_TEXT));
			ncWrite.addVariable(null, tlVar);
		}

		ncWrite.create();
		
		Array xArray = Array.factory(DataType.DOUBLE, new int[] { dataNum });
		for (int ii = 0; ii < dataNum; ii++) {
			xArray.setDouble(ii, xValues[0][ii]);
		}
		ncWrite.write(xName, xArray);

		for (int ii = 0; ii < childNum; ii++) {
			Array yArray = Array.factory(DataType.DOUBLE, new int[] { dataNum });
			for (int jj = 0; jj < dataNum; jj++) {
				yArray.setDouble(jj, yValues[ii][jj]);
			}
			ncWrite.write(yNames[ii], yArray);
		}

		if (eFlag) {
			Array leArray = Array.factory(DataType.DOUBLE, new int[] { dataNum });
			for (int ii = 0; ii < dataNum; ii++) {
				leArray.setDouble(ii, leValues[0][ii]);
			}
			ncWrite.write(leName, leArray);
			Array ueArray = Array.factory(DataType.DOUBLE, new int[] { dataNum });
			for (int ii = 0; ii < dataNum; ii++) {
				ueArray.setDouble(ii, ueValues[0][ii]);
			}
			ncWrite.write(ueName, ueArray);
		}

		if (tlFlag) {
			Array tlArray = Array.factory(DataType.CHAR, new int[] { dataNum, maxLen });
			for (int ii = 0; ii < dataNum; ii++) {
				String str = tickLabels[0][ii];
				Index idx = tlArray.getIndex();
				for (int jj = 0; jj < str.length(); jj++) {
					idx.set(ii, jj);
					final char c = str.charAt(jj);
					tlArray.setChar(idx, c);
				}
			}
			ncWrite.write(tlName, tlArray);
		}
		
		return true;
	}
		
    @Override
    public boolean saveToDataSetNetCDFFile(final File file) {
		
        final int dataNum = this.getAllPointsNumber();
        
        NetcdfFileWriteable ncfile = null;
		try {
            ncfile = NetcdfFileWriteable.createNew(file.getAbsolutePath(), true);
            
            // Creates the dimension and variable of indices
            Dimension indexDim = this.addIndexDimension(ncfile, dataNum);
            String indexDimName = indexDim.getName();
            Variable indexVar = this.addIndexVarialbe(ncfile, indexDim);
            indexVar.addAttribute(SGDataUtility.getValueTypeAttribute(SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));

            // Add data columns as variables.
            SGDataColumn[] colArray = this.getExportedColumnsClone();
            
            // Add dimensions for text string.
            String[] textDimensionName = new String[colArray.length];
            int[] maxTextLength = new int[colArray.length];
            Arrays.fill(maxTextLength, 0);
            for (int i = 0; i < colArray.length; i++) {
                SGDataColumn col = colArray[i];
                if (SGIDataColumnTypeConstants.VALUE_TYPE_TEXT.equals(col.getValueType())
                		|| SGIDataColumnTypeConstants.VALUE_TYPE_DATE.equals(col.getValueType())) {
                    String[] textString = ((SGDataColumn)col).getStringArray();
                    int maxLength = 0;
                    for (int j = 0; j < textString.length; j++) {
                    	byte[] byteArray = textString[j].getBytes(SGIConstants.CHAR_SET_NAME_UTF8);
                    	if (maxLength < byteArray.length) {
                    		maxLength = byteArray.length;
                    	}
                    }
                    
                    String dimName = "clen" + i;
                    Dimension dimc = new Dimension(dimName, maxLength);
                    ncfile.addDimension(null, dimc);
                    
                    textDimensionName[i] = dimName;
                    maxTextLength[i] = maxLength;
                }
            }

            String[] varNames = new String[colArray.length];
            for (int ii = 0; ii < colArray.length; ii++) {
                SGDataColumn col = colArray[ii];
                String colValueType = col.getValueType();
                String varName = "column" + ii;
                Variable var = null;
                if (SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER.equals(colValueType) ||
                        SGIDataColumnTypeConstants.VALUE_TYPE_SAMPLING_RATE.equals(colValueType)) {
                    var = new Variable(ncfile, null, null, varName, DataType.DOUBLE, indexDimName);
                    var.addAttribute(SGDataUtility.getValueTypeAttribute(col.getValueType()));
                } else if (SGIDataColumnTypeConstants.VALUE_TYPE_TEXT.equals(colValueType)
                		|| SGIDataColumnTypeConstants.VALUE_TYPE_DATE.equals(colValueType)) {
                	String[] dimNames = { indexDimName, textDimensionName[ii] };
                	String dims = SGDataUtility.getDimensionString(dimNames);
                    var = new Variable(ncfile, null, null, varName, DataType.CHAR, dims);
                    String attrValueType;
                    if (SGIDataColumnTypeConstants.VALUE_TYPE_TEXT.equals(colValueType)) {
                    	attrValueType = SGIDataColumnTypeConstants.VALUE_TYPE_TEXT;
                    } else {
                    	attrValueType = SGIDataColumnTypeConstants.VALUE_TYPE_DATE;
                    }
                    var.addAttribute(SGDataUtility.getValueTypeAttribute(attrValueType));
                } else {
					throw new Error("Illegal value type=" + col.getValueType()
							+ ", (" + col.getTitle() + ", "
							+ col.getColumnType() + ")");
                }

                String title = col.getTitle();
                if (null != title && !"".equals(title.trim())) {
                    Attribute attr = new Attribute(ATTRIBUTE_KEY_LONG_NAME, title);
                    var.addAttribute(attr);
                }

    			varNames[ii] = var.getShortName();
                ncfile.addVariable(null, var);
            }
            
            ncfile.create();

            for (int i = 0; i < colArray.length; i++) {
                SGDataColumn col = colArray[i];
                String colValueType = col.getValueType();
                if (SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER.equals(colValueType) ||
                        SGIDataColumnTypeConstants.VALUE_TYPE_SAMPLING_RATE.equals(colValueType)) {
                    Array array = Array.factory(DataType.DOUBLE, new int[] { dataNum });
                    for (int j = 0; j < dataNum; j++) {
                        Object obj = col.getValue(j);
                        if (obj instanceof Double) {
                            array.setDouble(j, ((Double)obj).doubleValue());
                        } else {
                            array.setDouble(j, Double.NaN);
                        }
                    }
                    ncfile.write(varNames[i], array);
                    
                } else if (SGIDataColumnTypeConstants.VALUE_TYPE_TEXT.equals(colValueType)
                		|| SGIDataColumnTypeConstants.VALUE_TYPE_DATE.equals(colValueType)) {
                	int maxLength = 0;
                	byte[][] byteArrays = new byte[dataNum][];
                    for (int j = 0; j < dataNum; j++) {
                        Object obj = col.getValue(j);
                        String str;
                        if (obj instanceof String) {
                        	str = (String) obj;
                        } else if (obj instanceof SGDate) {
                           	str = ((SGDate) obj).toString();
                        } else {
                        	str = "";
                        }
                    	byteArrays[j] = str.getBytes(SGIConstants.CHAR_SET_NAME_UTF8);
                    	int len = byteArrays[j].length;
                    	if (len > maxLength) {
                    		maxLength = len;
                    	}
                    }
                	
                    ArrayByte array = new ArrayByte(new int[] { dataNum, maxLength });
            		Index index = array.getIndex();
                    for (int j = 0; j < dataNum; j++) {
                    	for (int k = 0; k < byteArrays[j].length; k++) {
                    		array.setByte(index.set(j, k), byteArrays[j][k]);
                    	}
                    }
                    ncfile.write(varNames[i], array);
                }
            }

            // write serial numbers
            this.writeIndexVarialbe(ncfile, indexVar);

	    } catch (IOException e) {
	        return false;
	    } catch (InvalidRangeException e) {
	    	return false;
	    } finally {
	        if (ncfile != null) {
	            try {
					ncfile.close();
				} catch (IOException e) {
				}
	        }
	    }
	    
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

	@Override
    protected Object[][] getDataFileExportValues(final SGExportParameter mode,
    		SGDataBufferPolicy policy) {
		return this.getValueTable(mode, policy);
	}
	
	@Override
    protected Object[][] getArchiveDataSetExportValues(final SGExportParameter mode) {
		return this.getValueTable(mode, SGDataUtility.getArchiveDataSetBufferPolicy(this));
    }

	@Override
    public Object[][] getValueTable(final SGExportParameter params, SGDataBufferPolicy policy) {
		
		// get values from data
		OPERATION mode = params.getType();
		final boolean archiveFlag = SGDataUtility.isArchiveDataSetOperation(mode);
		final boolean exportFlag = (OPERATION.EXPORT_TO_FILE_AS_SAME_FORMAT.equals(mode)
				|| OPERATION.EXPORT_TO_TEXT.equals(mode));
		SGSXYMultipleDataBuffer buffer = (SGSXYMultipleDataBuffer) this.getDataBuffer(policy);
		final int len = buffer.getLength();
		final double[][] xValuesOri = buffer.getXValues();
		final double[][] yValuesOri = buffer.getYValues();
		
		double[][] xValues = null;
		double[][] yValues = null;
		if (this.hasMultipleYValues()) {
			xValues = new double[][] { xValuesOri[0] };
			yValues = yValuesOri;
		} else {
			xValues = xValuesOri;
			yValues = new double[][] { yValuesOri[0] };
		}
		final double[][] lowerErrorValues = buffer.getLowerErrorValues();
		final double[][] upperErrorValues = buffer.getUpperErrorValues();
		final Boolean[] sameErrorVariableFlags = buffer.hasSameErrorVariable();
		final String[][] tickLabels = buffer.getTickLabels();
		int colNum = xValues.length + yValues.length;
		final Boolean xDateFlag = buffer.getDateFlag();
		SGDate[] dateArray = buffer.getDateArray();
		if (lowerErrorValues != null && upperErrorValues != null) {
			for (int ii = 0; ii < lowerErrorValues.length; ii++) {
				if (lowerErrorValues[ii] != null) {
					colNum++;
				}
			}
			for (int ii = 0; ii < upperErrorValues.length; ii++) {
				if (upperErrorValues[ii] != null && !sameErrorVariableFlags[ii]) {
					colNum++;
				}
			}
		}
		if (tickLabels != null) {
			for (int ii = 0; ii < tickLabels.length; ii++) {
				if (tickLabels[ii] != null) {
					colNum++;
				}
			}
		} else {
			if (exportFlag) {
				if (xDateFlag != null) {
					colNum++;
				}
			}
		}
		Object[][] array = new Object[len][colNum];
		for (int ii = 0; ii < len; ii++) {
			Object[] row = array[ii];
			int cnt = 0;
			if (archiveFlag) {
				// save to an archive file
				if (xDateFlag != null && xDateFlag.booleanValue()) {
					for (int jj = 0; jj < xValues.length; jj++) {
						array[ii][cnt] = SGDataUtility.getTextValue(dateArray[ii].toString());
						cnt++;
					}
				} else {
					for (int jj = 0; jj < xValues.length; jj++) {
						array[ii][cnt] = xValues[jj][ii];
						cnt++;
					}
				}
				if (xDateFlag != null && !xDateFlag.booleanValue()) {
					for (int jj = 0; jj < yValues.length; jj++) {
						array[ii][cnt] = SGDataUtility.getTextValue(dateArray[ii].toString());
						cnt++;
					}
				} else {
					for (int jj = 0; jj < yValues.length; jj++) {
						array[ii][cnt] = yValues[jj][ii];
						cnt++;
					}
				}
			} else if (exportFlag) {
				// export to a text file
				for (int jj = 0; jj < xValues.length; jj++) {
					row[cnt] = xValues[jj][ii];
					cnt++;
				}
				for (int jj = 0; jj < yValues.length; jj++) {
					row[cnt] = yValues[jj][ii];
					cnt++;
				}
			} else {
				return null;
			}
			if (lowerErrorValues != null && upperErrorValues != null) {
				for (int jj = 0; jj < lowerErrorValues.length; jj++) {
					if (lowerErrorValues[jj] != null) {
						row[cnt] = lowerErrorValues[jj][ii];
						cnt++;
					}
				}
				for (int jj = 0; jj < upperErrorValues.length; jj++) {
					if (upperErrorValues[jj] != null && !sameErrorVariableFlags[jj]) {
						row[cnt] = upperErrorValues[jj][ii];
						cnt++;
					}
				}
			}
			if (tickLabels != null) {
				for (int jj = 0; jj < tickLabels.length; jj++) {
					if (tickLabels[jj] != null) {
						row[cnt] = SGDataUtility.getTextValue(tickLabels[jj][ii]);
						cnt++;
					}
				}
			} else {
				if (exportFlag) {
					if (xDateFlag != null) {
						SGDate date = dateArray[ii];
						row[cnt] = SGDataUtility.getTextValue(date.toString());
						cnt++;
					}
				}
			}
		}
        return array;
    }
	
    /**
     * Returns the list of the name of child objects.
     * 
     * @return the list of the name of child objects
     */
	@Override
    public List<String> getChildNameList() {
		return getChildNameList(this.getColumnInfo());
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
        	if (xNum > 1) {
            	if (!X_VALUE.equals(columnType)) {
            		continue;
            	}
        	} else {
            	if (!Y_VALUE.equals(columnType)) {
            		continue;
            	}
        	}
        	String title = cols[ii].getTitle();
			if (title != null && !"".equals(title)) {
				name = title;
			} else {
				name = Integer.toString(ii + 1);
			}
			if (name != null) {
            	nameList.add(name);
        	}
        }
        return nameList;
	}

    /**
     * Creates and returns a data buffer.
     * 
     * @param policy
     *           the policy to get the data buffer
     * @return the data buffer
     */
	@Override
	public SGDataBuffer getDataBuffer(SGDataBufferPolicy policy) {
		return SGDataUtility.getDataBuffer(this, (SGSXYDataBufferPolicy) policy);
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
	
	private SGDateDataColumn getDateDataColumn() {
		SGDateDataColumn col;
		col = this.getDateDataColumn(this.mXIndices[0]);
		if (col != null) {
			return col;
		}
		col = this.getDateDataColumn(this.mYIndices[0]);
		if (col != null) {
			return col;
		}
		return null;
	}

	private SGDateDataColumn getDateDataColumn(final Integer singleIndex) {
		SGDataColumn singleColumn = this.getDataFile().mDataColumns[singleIndex];
    	if (singleColumn instanceof SGDateDataColumn) {
    		return (SGDateDataColumn) singleColumn;
    	} else {
    		return null;
    	}
	}

	@Override
    public Boolean getDateFlag() {
		if (this.getDateDataColumn(this.mXIndices[0]) != null) {
			return true;
		} else if (this.getDateDataColumn(this.mYIndices[0]) != null) {
			return false;
		} else {
			return null;
		}
    }

	@Override
    public boolean hasGenericTickLabels() {
    	if (!this.isTickLabelAvailable()) {
    		return false;
    	}
    	for (int ii = 0; ii < this.mTickLabelIndices.length; ii++) {
    		Integer tlIndex = this.mTickLabelIndices[ii];
    		if (SGUtility.contains(this.mXIndices, tlIndex)
    				|| SGUtility.contains(this.mYIndices, tlIndex)) {
    			return false;
    		}
    	}
    	return true;
	}
	
	@Override
	public SGDate[] getDateArray(boolean all) {
		if (this.getDateFlag() != null) {
			SGDateDataColumn dateColumn = this.getDateDataColumn();
			SGDate[] dateArray = dateColumn.getDateArray();
			return dateArray;
		} else {
			return null;
		}
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
	
	private boolean[] getSameErrorVariableFlags() {
		boolean[] ret = new boolean[this.mLowerErrorIndices.length];
		for (int ii = 0; ii < ret.length; ii++) {
			ret[ii] = this.mLowerErrorIndices[ii].equals(this.mUpperErrorIndices[ii]);
		}
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
	public Boolean isYValuesHolder() {
		if (this.isErrorBarAvailable()) {
			return SGUtility.contains(this.mYIndices, this.mErrorBarHolderIndices);
		} else if (this.isTickLabelAvailable()) {
			return SGUtility.contains(this.mYIndices, this.mTickLabelHolderIndices);
		} else {
			return null;
		}
	}

	@Override
    public boolean hasOneSidedMultipleValues() {
		// always returns true
		return true;
	}

    /**
     * Returns a text string for the command of the column types.
     * 
     * @return a text string for the command of the column types
     */
	@Override
    public String getColumnTypeCommandString() {
		List<String> varList = new ArrayList<String>();
		List<String> columnTypeList = new ArrayList<String>();
		for (int ii = 0; ii < this.mXIndices.length; ii++) {
			String str = Integer.toString(this.mXIndices[ii] + 1);
			varList.add(str);
			columnTypeList.add(X_VALUE);
		}
		for (int ii = 0; ii < this.mYIndices.length; ii++) {
			String str = Integer.toString(this.mYIndices[ii] + 1);
			varList.add(str);
			columnTypeList.add(Y_VALUE);
		}
		if (this.isErrorBarAvailable()) {
			final int num = this.mLowerErrorIndices.length;
			for (int ii = 0; ii < num; ii++) {
				if (this.mLowerErrorIndices[ii] == null) {
					continue;
				}
				final int leNo = this.mLowerErrorIndices[ii] + 1;
				final int ueNo = this.mUpperErrorIndices[ii] + 1;
				final int ehIndex = this.mErrorBarHolderIndices[ii];
				final boolean equalFlag = (leNo == ueNo);
				if (equalFlag) {
					String str = Integer.toString(leNo);
					varList.add(str);
					String columnTypeStr = SGDataUtility.appendColumnNo(LOWER_UPPER_ERROR_VALUE, ehIndex);
					columnTypeList.add(columnTypeStr);
				} else {
					String leStr = Integer.toString(leNo);
					varList.add(leStr);
					String leColumnTypeStr = SGDataUtility.appendColumnNo(LOWER_ERROR_VALUE, ehIndex);
					columnTypeList.add(leColumnTypeStr);
					
					String ueStr = Integer.toString(ueNo);
					varList.add(ueStr);
					String ueColumnTypeStr = SGDataUtility.appendColumnNo(UPPER_ERROR_VALUE, ehIndex);
					columnTypeList.add(ueColumnTypeStr);
				}
			}
		}
		if (this.isTickLabelAvailable() && this.hasGenericTickLabels()) {
			for (int ii = 0; ii < this.mTickLabelIndices.length; ii++) {
				final int tlNo = this.mTickLabelIndices[ii] + 1;
				final int thIndex = this.mTickLabelHolderIndices[ii];
				String str = Integer.toString(tlNo);
				varList.add(str);
				String columnTypeStr = SGDataUtility.appendColumnNo(TICK_LABEL, thIndex);
				columnTypeList.add(columnTypeStr);
			}
		}
		return SGDataUtility.getDataColumnTypeCommand(varList, columnTypeList);
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
		return SGDataUtility.getDataViewerColumnNumber(this, columnType);
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
		if (this.mXIndices.length != 1) {
			return false;
		}
		return (this.getXColumns()[0] instanceof SGDateDataColumn);
	}
	
	@Override
    public boolean hasDateTypeYVariable() {
		if (this.mYIndices.length != 1) {
			return false;
		}
		return (this.getYColumns()[0] instanceof SGDateDataColumn);
	}

	@Override
	protected boolean matches(final int col, final int row, String columnType,
			SGDataValueHistory value, final double d) {
		return SGDataUtility.matches(col, row, columnType, value, d);
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
//	 * Returns a copy of x-value array.
//	 *
//	 * @return x-value array
//	 */
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
//	 * Returns a copy of y-value array.
//	 *
//	 * @return y-value array
//	 */
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
		SGDataValueHistory.SDArray.D1 dValue = (SGDataValueHistory.SDArray.D1) dataValue;
		SGDataValueHistory prev = dValue.getPreviousValue();
		SGDataValueHistory.SDArray.MD1 mdValue;
		if (prev != null) {
			mdValue = new SGDataValueHistory.SDArray.MD1(
					dValue.getValue(), dValue.getColumnType(), 0,
					dValue.getIndex(), prev.getValue());
		} else {
			mdValue = new SGDataValueHistory.SDArray.MD1(
					dValue.getValue(), dValue.getColumnType(), 0,
					dValue.getIndex());
		}
		this.mEditedDataValueList.add(mdValue);
	}

	@Override
    public SGTwoDimensionalArrayIndex getDataViewerCell(SGTwoDimensionalArrayIndex cell,
    		String columnType, final boolean bStride) {
		SGTwoDimensionalArrayIndex ret = null;
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
    	return ret;
    }

}
