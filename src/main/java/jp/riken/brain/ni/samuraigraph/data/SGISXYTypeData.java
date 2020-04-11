package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;

/**
 * An interface for Scalar-type XY data.
 * 
 */
public interface SGISXYTypeData extends SGIXYData {

    /**
     * Returns whether error bars are available.
     * 
     * @return true if error bars are available
     */
    public boolean isErrorBarAvailable();

    /**
     * Returns whether tick labels are available.
     * 
     * @return true if tick labels are available
     */
    public boolean isTickLabelAvailable();

    /**
	 * Returns whether the error bars are vertical. If this data does not have
	 * error values, returns null.
	 * 
	 * @return true if error bars are vertical, false if they are horizontal and
	 *         null if this data do not have error values
	 */
    public Boolean isErrorBarVertical();
    
    /**
	 * Returns whether the tick labels align horizontally. If this data does not have
	 * tick labels, returns null.
	 * 
	 * @return true if tick labels align horizontally, false if they do not so and
	 *         null if this data do not have tick labels
	 */
    public Boolean isTickLabelHorizontal();

    /**
     * Returns the decimal places for the tick labels.
     * 
     * @return the decimal places for the tick labels
     */
    public int getDecimalPlaces();

    /**
     * Returns the exponent for tick labels.
     * 
     * @return the exponent for tick labels
     */
    public int getExponent();
    
    public String getDateFormat();


    /**
     * Sets the decimal places for the tick labels.
     * 
     * @param dp
     *          a value to set to the decimal places
     */
    public void setDecimalPlaces(final int dp);

    /**
     * Sets the exponent for the tick labels.
     * 
     * @param exp
     *           a value to set to the exponent
     */
    public void setExponent(final int exp);
    
    public void setDateFormat(final String format);

    /**
     * Sets the stride.
     * 
     * @param stride
     *           the stride
     */
    public void setStride(final SGIntegerSeriesSet stride);

    /**
     * Returns the stride.
     * 
     * @return the stride
     */
    public SGIntegerSeriesSet getStride();

    /**
     * Sets the stride of the tick labels.
     * 
     * @param stride
     *           stride of arrays
     */
	public void setTickLabelStride(SGIntegerSeriesSet stride);

    /**
     * Returns the stride of the tick labels.
     * 
     * @return the stride
     */
    public SGIntegerSeriesSet getTickLabelStride();

    /**
     * Returns the indices of tick labels.
     * 
     * @return the indices of tick labels
     */
    public int[] getTickLabelValueIndices();
    
	/**
	 * Returns the shift.
	 * 
	 * @return the shift
	 */
	public SGTuple2d getShift();
	
	/**
	 * Sets the shift.
	 * 
	 * @param shift
	 *           the shift to set
	 */
	public void setShift(SGTuple2d shift);

    public Boolean getDateFlag();
    
    public SGDate[] getDateArray(SGSXYDataBufferPolicy policy);

    public SGDate[] getDateArray(final boolean all);

    public boolean hasGenericTickLabels();

    /**
     * Returns the main stride.
     * 
     * @return the main stride
     */
    public SGIntegerSeriesSet getMainStride();
    
    public Boolean isYValuesHolder();

    public boolean hasDateTypeXVariable();
    
    public boolean hasDateTypeYVariable();
    
}
