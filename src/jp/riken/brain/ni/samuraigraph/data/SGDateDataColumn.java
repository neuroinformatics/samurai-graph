package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGDateUtility;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;

/**
 * The class of data column of the date type.
 *
 */
public class SGDateDataColumn extends SGDataColumn implements SGINumberDataColumn {
    
    /**
     * An array of text strings.
     */
    protected SGDate[] mArray = null;

    /**
     * An array of text string for date objects.
     */
    private String[] mStringArray = null;

    /**
     * Builds this object.
     * @param title
     *              a text string for the title
     * @param array
     *              an array of text strings
     */
    public SGDateDataColumn(final String title, final SGDate[] array) {
        
        super(title);
        
        // check
        if (array == null) {
            throw new IllegalArgumentException("array == null");
        }
        
        // set to an attribute
        final SGDate[] dest = new SGDate[array.length];
        System.arraycopy(array, 0, dest, 0, array.length);
        this.mArray = dest;
    }
    
    /**
     * Returns the length of data column.
     * @return
     *         the length of data column
     */
    public int getLength() {
        return this.mArray.length;
    }

    /**
     * Returns the value type of this column.
     * @return
     *         the value type of this column
     */
    public String getValueType() {
        return SGIDataColumnTypeConstants.VALUE_TYPE_DATE;
    }

    /**
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();
        this.mArray = null;
        this.mStringArray = null;
    }

    SGDate[] getDateArray() {
        return this.mArray;
    }
    
    /**
     * Returns a value in this data column at given row index.
     * 
     * @param rowIndex
     *            the row index
     * @return a value at given row index
     */
    public Object getValue(final int rowIndex) {
        if (rowIndex > this.mArray.length) {
            throw new IllegalArgumentException("Index out of bounds: "
                    + rowIndex + " > " + this.mArray.length);
        }
        return this.mArray[rowIndex];
    }
    
    /**
     * Returns an array of text strings.
     * 
     * @return an array of text strings
     */
    public String[] getStringArray() {
        if (this.mStringArray == null) {
            String[] array = new String[this.mArray.length];
            for (int ii = 0; ii < array.length; ii++) {
                array[ii] = this.mArray[ii].toString();
            }
            this.mStringArray = array;
        }
        return (String[]) this.mStringArray.clone();
    }

    /**
     * Returns an array of text strings with given stride.
     * 
     * @param stride
     *           the stride of an array
     * @return an array of text strings
     */
    public String[] getStringArray(final SGIntegerSeriesSet stride) {
    	final int[] indices = this.calcStrideIndices(stride, this.mArray.length);
    	String[] strArray = this.getStringArray();
    	String[] ret = new String[indices.length];
    	for (int ii = 0; ii < ret.length; ii++) {
    		ret[ii] = strArray[indices[ii]];
    	}
    	return ret;
    }

    /**
     * Clones this data column.
     * 
     * @return
     *         shallow copy of this data column
     */
    public Object clone() {
        SGDateDataColumn col = (SGDateDataColumn) super.clone();
        col.mStringArray = null;
        return col;
    }

    /**
     * Returns an array of numbers.
     * 
     * @return an array of numbers
     */
    public double[] getNumberArray() {
        final SGDate[] dArray = this.getDateArray();
        return SGDataUtility.getDateValueArray(dArray);
    }

    /**
     * Returns an array of numbers with given stride.
     * 
     * @param stride
     *           the stride of an array
     * @return an array of numbers
     */
    public double[] getNumberArray(final SGIntegerSeriesSet stride) {
    	final double[] values = this.getNumberArray();
    	final int[] indices = this.calcStrideIndices(stride, values.length);
    	double[] array = new double[indices.length];
    	for (int ii = 0; ii < indices.length; ii++) {
    		array[ii] = values[indices[ii]];
    	}
    	return array;
    }

    /**
     * Returns an array of numbers at given array indices.
     * 
     * @param indices
     *           array indices
     * @return an array of numbers
     */
    @Override
    public double[] getNumberArray(int[] indices) {
    	final double[] values = this.getNumberArray();
    	double[] array = new double[indices.length];
    	for (int ii = 0; ii < indices.length; ii++) {
    		array[ii] = values[indices[ii]];
    	}
    	return array;
    }

    public String[] getStringArray(final String format, 
    		final SGIntegerSeriesSet stride) {
    	String[] strArray = this.getStringArray(stride);
    	for (int ii = 0; ii < strArray.length; ii++) {
    		strArray[ii] = SGDateUtility.format(strArray[ii], format);
    	}
    	return strArray;
    }
}
