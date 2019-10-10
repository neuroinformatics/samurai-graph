package jp.riken.brain.ni.samuraigraph.data;

import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;


/**
 * An interface for multiple Scalar-type XY data.
 *
 */
public interface SGISXYTypeMultipleData extends SGISXYTypeData {

    /**
	 * Returns an array of SXYData objects.
	 *
	 * @return an array of SXYData objects
	 */
    public SGISXYTypeSingleData[] getSXYDataArray();

    /**
	 * Returns an array of multiple SXYData objects.
	 *
	 * @return an array of multiple SXYData objects
	 */
    public SGISXYTypeMultipleData[] getSXYTypeMultipleDataArray();

    /**
     * Returns a copy of x-value arrays.
     *
     * @param all
     *           true to get all values
     * @return arrays of x-values
     */
    public double[][] getXValueArray(final boolean all);

    /**
     * Returns x-value arrays with given policy.
     *
     * @param policy
     *           policy to get values
     * @return arrays of x-values
     */
    public double[][] getXValueArray(SGSXYDataBufferPolicy policy);

    /**
     * Returns unshifted x-value arrays with given policy.
     *
     * @param policy
     *           policy to get values
     * @return arrays of unshifted x-values
     */
    public double[][] getUnshiftedXValueArray(SGSXYDataBufferPolicy policy);

    /**
     * Returns a copy of y-value arrays.
     *
     * @param all
     *           true to get all values
     * @return arrays of y-values
     */
    public double[][] getYValueArray(final boolean all);

    /**
     * Returns y-value arrays with given policy.
     *
     * @param policy
     *           policy to get values
     * @return arrays of y-values
     */
    public double[][] getYValueArray(SGSXYDataBufferPolicy policy);

    /**
     * Returns unshifted y-value arrays with given policy.
     *
     * @param policy
     *           policy to get values
     * @return arrays of unshifted y-values
     */
    public double[][] getUnshiftedYValueArray(SGSXYDataBufferPolicy policy);

    /**
     * Returns arrays of lower error values with given policy.
     *
     * @param policy
     *           policy to get values
     * @return arrays of lower error values
     */
    public double[][] getLowerErrorValueArray(SGSXYDataBufferPolicy policy);

    /**
     * Returns arrays of upper error values with given policy.
     *
     * @param policy
     *           policy to get values
     * @return arrays of upper error values
     */
    public double[][] getUpperErrorValueArray(SGSXYDataBufferPolicy policy);

    /**
     * Returns arrays of tick labels with given policy.
     *
     * @param policy
     *           policy to get values
     * @return arrays of tick labels
     */
    public String[][] getTickLabelArray(SGSXYDataBufferPolicy policy);

    /**
     * Returns true if this data enables to be split.
     *
     * @return true if this data enables to be split
     */
    public boolean isSplitEnabled();

    /**
     * Returns the number of child data.
     *
     * @return the number of child data
     */
    public int getChildNumber();
    
    /**
     * Returns the list of child objects.
     * 
     * @return the list of child objects
     */
    public List<String> getChildNameList();
    
    /**
     * Returns whether this multiple data has multiple arrays for y-values.
     *
     * @return true if this multiple data has multiple arrays for y-values
     */
    public boolean hasMultipleYValues();

    /**
     * Creates and returns a data buffer with given array of child indices.
     * 
     * @param policy
     *           policy to get data buffer
     * @param indices
     *           array of child indices
     * @return the data buffer
     */
    public SGDataBuffer getDataBuffer(SGSXYDataBufferPolicy policy, int[] indices);

    public Boolean[] hasSameErrorVariable();

    public boolean hasOneSidedMultipleValues();

    public double getXValueAt(final int childIndex, final int arrayIndex);

    public double getYValueAt(final int childIndex, final int arrayIndex);

    public double[][] getXValueArray(final boolean all, final boolean useCache,
    		final boolean removeInvalidValues);

    public double[][] getXValueArray(final boolean all, final boolean useCache);

    public double[][] getYValueArray(final boolean all, final boolean useCache,
    		final boolean removeInvalidValues);

    public double[][] getYValueArray(final boolean all, final boolean useCache);

	public void addSingleDimensionEditedDataValue(SGDataValueHistory dataValue);

}
