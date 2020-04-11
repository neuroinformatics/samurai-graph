package jp.riken.brain.ni.samuraigraph.data;

import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.Value;

/**
 * An interface for the vector type XY data.
 * 
 */
public interface SGIVXYTypeData extends SGIXYData {

	public double[] getXValueArray(final boolean all);

	public double[] getYValueArray(final boolean all);

    /**
	 * Returns an array of xy-values.
	 * 
	 * @return an array of xy-values
	 */
	public SGTuple2d[] getXYValueArray(final boolean all);

    /**
     * Returns an array of values for the first component.
     *
     * @return an array of values for the first component
     */
    public double[] getFirstComponentValueArray(final boolean all);

    /**
     * Returns an array of values for the second component.
     *
     * @return an array of values for the second component
     */
    public double[] getSecondComponentValueArray(final boolean all);
    
    /**
     * Returns a copy of magnitude array.
     * 
     * @return magnitude array
     */
    public double[] getMagnitudeArray(final boolean all);

    /**
     * Returns a copy of angle array.
     * 
     * @return angle array
     */
    public double[] getAngleArray(final boolean all);

    /**
     * 
     * @return
     */
    public double[] getXComponentArray(final boolean all);

    /**
     * 
     * @return
     */
    public double[] getYComponentArray(final boolean all);

    /**
     * 
     * @return
     */
    public boolean isPolar();

    /**
     * Returns the list of blocks of values of the first component.
     * 
     * @return the list of blocks of values of the first component
     */
    public List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlockList();

    public List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlockList(
    		final boolean all, final boolean useCache, final boolean removeInvalidValues);

    /**
     * Returns the list of blocks of values of the second component.
     * 
     * @return the list of blocks of values of the second component
     */
    public List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlockList();

    public List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlockList(
    		final boolean all, final boolean useCache, final boolean removeInvalidValues);

    public Double getXValueAt(final int index);
    
    public Double getYValueAt(final int index);

    public Double getFirstComponentValueAt(final int index);

    public Double getFirstComponentValueAt(final int row, final int col);

    public Double getSecondComponentValueAt(final int index);

    public Double getSecondComponentValueAt(final int row, final int col);

    public SGIntegerSeriesSet getXStride();

    public SGIntegerSeriesSet getYStride();

    public String getToolTipSpatiallyVaried(final int index);

    public String getToolTipSpatiallyVaried(final int index0, final int index1);

	public double[] getXValueArray(final boolean all, final boolean useCache);

	public double[] getXValueArray(final boolean all, final boolean useCache,
			final boolean removeInvalidValues);

	public double[] getYValueArray(final boolean all, final boolean useCache);

	public double[] getYValueArray(final boolean all, final boolean useCache,
			final boolean removeInvalidValues);

	public double[] getFirstComponentValueArray(final boolean all, final boolean useCache);

	public double[] getFirstComponentValueArray(final boolean all, final boolean useCache,
			final boolean removeInvalidValues);

	public double[] getSecondComponentValueArray(final boolean all, final boolean useCache);

	public double[] getSecondComponentValueArray(final boolean all, final boolean useCache,
			final boolean removeInvalidValues);

	public boolean useXValueCache(final boolean all);

	public boolean useYValueCache(final boolean all);

	public boolean useFirstComponentValueCache(final boolean all);

	public boolean useSecondComponentValueCache(final boolean all);

	public List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlockListSub(
			final boolean all, final boolean useCache, final boolean removeInvalidValues);

	public List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlockListSub(
			final boolean all, final boolean useCache, final boolean removeInvalidValues);

	public SGDataValue getDataValue(final int index);

	public SGDataValue getDataValue(final int xIndex, final int yIndex);

}
