package jp.riken.brain.ni.samuraigraph.base;

import java.util.List;


/**
 * An interface for all data classes.
 * 
 */
public interface SGIData extends SGIConstants, SGIDisposable {

    /**
     * Returns the data type.
     * 
     * @return the data type
     */
    public String getDataType();

    /**
     * Returns the data source.
     * 
     * @return the data source
     */
    public SGIDataSource getDataSource();

    /**
     * Sets the data source.
     * 
     * @param src
     *           a data source
     */
    public void setDataSource(SGIDataSource src);

    /**
     * Returns the number of data points taking into account the stride.
     * 
     * @return the number of data points taking into account the stride
     */
    public int getPointsNumber();

    /**
     * Returns the number of data points without taking into account the stride.
     * 
     * @return the number of data points without taking into account the stride
     */
    public int getAllPointsNumber();

    /**
     * Creates and returns a data buffer.
     * 
     * @param policy
     *           the policy to get data buffer
     * @return the data buffer
     */
    public SGDataBuffer getDataBuffer(SGDataBufferPolicy policy);

    /**
     * Returns whether the stride of data arrays is available.
     * 
     * @return true if the stride of data arrays is available
     */
    public boolean isStrideAvailable();

	/**
	 * Returns true if this data has at lease one "effective" stride that has the string representation 
	 * different from "0:end".
	 * 
	 * @return true this data has an effective stride
	 */
	public boolean hasEffectiveStride();

    /**
     * Returns whether the animation is supported in this data.
     * 
     * @return true if the animation is supported in this data
     */
    public boolean isAnimationSupported();

	/**
	 * Returns true if this data is available for the animation.
	 * 
	 * @return true if the animation is available
	 */
	public boolean isAnimationAvailable();

    /**
     * Returns the stride of time.
     *
     * @return the stride of time
     */
    public SGIntegerSeriesSet getTimeStride();

    /**
     * Returns the current index of time value.
     *
     * @return the current index of time value
     */
    public int getCurrentTimeValueIndex();
    
    /**
     * Sets the current index of time value.
     *
     * @param index
     *           a value to set to the index of time values
     */
    public void setCurrentTimeValueIndex(final int index);

    /**
     * Returns whether the index is available.
     *
     * @return true if the index is available
     */
	public boolean isIndexAvailable();
	
	public SGIntegerSeriesSet getIndexStride();

    /**
     * Returns the array of column types.
     * 
     * @return the array of column types
     */
    public String[] getDataViewerColumnTypes();
    
    /**
     * Returns preferred column type for data viewer.
     * 
     * @return preferred column type for data viewer
     */
    public String getPreferredDataViewColumnType();
    
    public Double getDataViewerValue(final String columnType, final int row, final int col);

    public void setDataViewerValue(final String columnType, final int row, final int col,
    		final Object value);

    public int getDataViewerColumnNumber(final String columnType, final boolean all);
    
    public int getDataViewerRowNumber(final String columnType, final boolean all);
    
    public SGIntegerSeriesSet getDataViewerColStride(String columnType);

    public SGIntegerSeriesSet getDataViewerRowStride(String columnType);
    
    public void setDataValue(final SGDataValueHistory value);

    public void restoreCache();

    public SGTwoDimensionalArrayIndex getDataViewerCell(SGTwoDimensionalArrayIndex cell,
    		String columnType, final boolean bStride);
    
    public List<SGDataValueHistory> getEditedValueList();
}
