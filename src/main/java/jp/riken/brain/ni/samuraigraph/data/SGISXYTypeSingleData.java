package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;



/**
 * An interface for single Scalar-type XY data.
 * 
 */
public interface SGISXYTypeSingleData extends SGISXYTypeData {

    /**
     * Returns a copy of x-value array.
     * 
     * @param all
     *           true to get all values
     * @return x-value array
     */
    public double[] getXValueArray(final boolean all);

    /**
     * Returns a copy of y-value array.
     * 
     * @param all
     *           true to get all values
     * @return y-value array
     */
    public double[] getYValueArray(final boolean all);

    /**
     * Returns a copy of lower-error-value array if they exist.
     * 
     * @param all
     *           true to get all values
     * @return lower-error-value array
     */
    public double[] getLowerErrorValueArray(final boolean all);

    /**
     * Returns a copy of upper-error-value array if they exist.
     * 
     * @param all
     *           true to get all values
     * @return upper-error-value array
     */
    public double[] getUpperErrorValueArray(final boolean all);

    /**
     * Returns a copy of string array if they exist.
     * 
     * @param all
     *           true to get all strings
     * @return string array
     */
    public String[] getStringArray(final boolean all);
    
    /**
     * Returns whether a given data object has the same tick label resources.
     * 
     * @param data
     *           a data to compare
     * @return true if a given data object has the same tick label resources
     */
    public boolean hasEqualTickLabelResource(SGISXYTypeSingleData data);

    /**
     * Returns the number of strings.
     * 
     * @return the number of strings
     */
    public int getStringNumber();

    public SGISXYTypeMultipleData toMultiple();
    
    public boolean hasSameErrorVariable();

    public String getToolTipSpatiallyVaried(final int index);

    public String getToolTipSpatiallyVaried(final int index0, final int index1);

	static class DoubleValueSetResult {
		boolean status;
		double prev;
	}
	
	public DoubleValueSetResult setDataViewerDoubleValue(String columnType, final int index, 
			Double value);

    public double[] getXValueArray(final boolean all, final boolean useCache,
    		final boolean removeInvalidValues);

    public double[] getXValueArray(final boolean all, final boolean useCache);

    public double[] getYValueArray(final boolean all, final boolean useCache,
    		final boolean removeInvalidValues);

    public double[] getYValueArray(final boolean all, final boolean useCache);

    public double[] getLowerErrorValueArray(final boolean all, final boolean useCache,
    		final boolean removeInvalidValues);

    public double[] getLowerErrorValueArray(final boolean all, final boolean useCache);

    public double[] getUpperErrorValueArray(final boolean all, final boolean useCache,
    		final boolean removeInvalidValues);

    public double[] getUpperErrorValueArray(final boolean all, final boolean useCache);

    public String[] getStringArray(final boolean all, final boolean useCache);

	public boolean useValueCache(final boolean all);

	public boolean useTickLabelCache(final boolean all);

    public Double getXValueAt(final int index);
    
    public Double getYValueAt(final int index);

	public SGDataValue getDataValue(final int index);

	public SGDataValue getDataValue(final int index0, final int index1);

	public void addMultipleDimensionEditedDataValue(SGDataValueHistory dataValue);
}
