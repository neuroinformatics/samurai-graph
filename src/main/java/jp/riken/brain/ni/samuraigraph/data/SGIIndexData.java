package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;

public interface SGIIndexData {

    /**
     * Returns the stride for index.
     *
     * @return the stride for index
     */
    public SGIntegerSeriesSet getIndexStride();

    /**
     * Sets the stride for index.
     *
     * @param stride
     *           stride of arrays
     */
	public void setIndexStride(SGIntegerSeriesSet stride);

    /**
     * Returns whether the index is available.
     *
     * @return true if the index is available
     */
	public boolean isIndexAvailable();

}
