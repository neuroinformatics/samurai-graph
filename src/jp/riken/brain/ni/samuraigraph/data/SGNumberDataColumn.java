package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/**
 * The class of data column of the number type.
 *
 */
public class SGNumberDataColumn extends SGDataColumn implements SGINumberDataColumn {

    /**
     * An array of double values.
     */
    protected double[] mArray = null;

    /**
     * The default constructor.
     * 
     */
    protected SGNumberDataColumn() {
    	super();
    }
    
    /**
     * Builds this object.
     * 
     * @param title
     *              a text string for the title
     * @param array
     *              an array of double values
     */
    public SGNumberDataColumn(final String title, final double[] array) {
        
        super(title);
        
        // check
        if (array == null) {
            throw new IllegalArgumentException("array == null");
        }
        
        // set to an attribute
        this.mArray = SGUtility.copyDoubleArray(array);
    }

    /**
     * Builds this object.
     * 
     * @param title
     *              a text string for the title
     * @param strArray
     *              an array of text strings of double values
     */
    public SGNumberDataColumn(final String title, final String[] strArray) {
        
        super(title);
        
        // check
        if (strArray == null) {
            throw new IllegalArgumentException("array == null");
        }

        final int len = strArray.length;
        double[] dArray = new double[len];
        for (int ii = 0; ii < len; ii++) {
            Double d = SGUtilityText.getDouble(strArray[ii]);
            if (d == null) {
                throw new IllegalArgumentException("strArray[" + ii + "] is not a number: " + strArray[ii]);
            }
            dArray[ii] = d.doubleValue();
        }
        
        // set to an attribute
        this.mArray = SGUtility.copyDoubleArray(dArray);
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
        return SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER;
    }
    
    /**
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();
        this.mArray = null;
    }
    
    /**
     * Returns an array of numbers.
     * 
     * @return an array of numbers
     */
    @Override
    public double[] getNumberArray() {
        return SGUtility.copyDoubleArray(this.mArray);
    }
    
    double[] getArray() {
    	return this.mArray;
    }

    /**
     * Returns an array of numbers with given stride.
     * 
     * @param stride
     *           the stride of an array
     * @return an array of numbers
     */
    @Override
    public double[] getNumberArray(final SGIntegerSeriesSet stride) {
    	final double[] values = this.mArray;
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
    	final double[] values = this.getArray();
    	double[] array = new double[indices.length];
    	for (int ii = 0; ii < indices.length; ii++) {
    		array[ii] = values[indices[ii]];
    	}
    	return array;
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
        return Double.valueOf(this.mArray[rowIndex]);
    }

    /**
     * Returns an array of text strings.
     * 
     * @return an array of text strings
     */
    public String[] getStringArray() {
        return this.getStringArray(0, 0);
    }

    /**
     * Returns an array of strings if they exist.
     * 
     * @param dp
     *          decimal places
     * @param exp
     *          exponent
     * @return string array
     */
    public String[] getStringArray(final int dp, final int exp) {
        return SGUtilityNumber.getStringArray(this.mArray, dp, exp);
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
     * Returns an array of text strings with given stride.
     * 
     * @param dp
     *          decimal places
     * @param exp
     *          exponent
     * @param stride
     *           the stride of an array
     * @return an array of text strings
     */
    public String[] getStringArray(final int dp, final int exp, 
    		final SGIntegerSeriesSet stride) {
    	final int[] indices = this.calcStrideIndices(stride, this.mArray.length);
        String[] strArray = SGUtilityNumber.getStringArray(this.mArray, dp, exp);
    	String[] ret = new String[indices.length];
    	for (int ii = 0; ii < ret.length; ii++) {
    		ret[ii] = strArray[indices[ii]];
    	}
    	return ret;
    }

}
