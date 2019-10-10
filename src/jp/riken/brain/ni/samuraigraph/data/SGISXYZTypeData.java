package jp.riken.brain.ni.samuraigraph.data;

import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.Value;

/**
 * An interface for the scalar type XYZ data.
 *
 */
public interface SGISXYZTypeData extends SGIXYZData {

    /**
	 * Returns an array of xy-values.
	 * 
	 * @return an array of xy-values
	 */
	public SGTuple2d[] getXYValueArray(final boolean all);

    /**
     * Returns an array of x-values.
     * 
     * @return an array of x-values
     */
    public double[] getXValueArray(final boolean all);

    /**
     * Returns an array of y-values.
     * 
     * @return an array of y-values
     */
    public double[] getYValueArray(final boolean all);

    /**
     * Returns an array of z-values.
     * 
     * @return z-value array
     */
    public double[] getZValueArray(final boolean all);
    
    /**
     * Returns the list of blocks of z-values.
     * 
     * @return the list of blocks of z-values
     */
    public List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockList();

    public List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockList(
    		final boolean all, final boolean useCache, 
    		final boolean removeInvalidValues);

    public Double getXValueAt(final int index);
    
    public Double getYValueAt(final int index);

    public Double getZValueAt(final int index);

    public Double getZValueAt(final int row, final int col);

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

	public double[] getZValueArray(final boolean all, final boolean useCache);

	public double[] getZValueArray(final boolean all, final boolean useCache,
			final boolean removeInvalidValues);

	public boolean useXValueCache(final boolean all);

	public boolean useYValueCache(final boolean all);

	public boolean useZValueCache(final boolean all);
	
	public List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockListSub(
			final boolean all, final boolean useCache,
			final boolean removeInvalidValues);

	public SGDataValue getDataValue(final int index);

	public SGDataValue getDataValue(final int xIndex, final int yIndex);

}
