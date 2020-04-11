package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;

/**
 * An interface for the data that has two dimensions: X and Y.
 *
 */
public interface SGITwoDimensionalData extends SGIXYData {

    /**
     * Returns an array of xy-values.
     * 
     * @return an array of xy-values
     */
    public SGTuple2d[] getXYValueArray(final boolean all);

    /**
     * Returns the stride for x-values.
     * 
     * @return the stride
     */
    public SGIntegerSeriesSet getXStride();

    /**
     * Returns the stride for y-values.
     * 
     * @return the stride
     */
    public SGIntegerSeriesSet getYStride();

    /**
     * Sets the stride for the x-direction.
     * 
     * @param stride
     *           the stride
     */
    public void setXStride(final SGIntegerSeriesSet stride);

    /**
     * Sets the stride for the y-direction.
     * 
     * @param stride
     *           the stride
     */
    public void setYStride(final SGIntegerSeriesSet stride);

    /**
     * Returns the length of x-dimension without taking into account the stride.
     * 
     * @return the length of x-dimension without taking into account the stride
     */
    public int getXDimensionLength();

    /**
     * Returns the length of y-dimension without taking into account the stride.
     * 
     * @return the length of y-dimension without taking into account the stride
     */
    public int getYDimensionLength();

    public double[] getXValueArray(final boolean all);

    public double[] getYValueArray(final boolean all);
    
	public boolean useXYCache(final boolean all, SGIntegerSeriesSet xyStride);

	public double[] getXValueArray(final boolean all, final boolean useCache);

	public double[] getYValueArray(final boolean all, final boolean useCache);

	public boolean useComponentIndexCache(final boolean all);

}
