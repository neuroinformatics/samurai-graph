package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;


/**
 * An abstract class of the data buffer for two-dimensional data.
 *
 */
public abstract class SGTwoDimensionalDataBuffer extends SGDataBuffer {
	
	// The array of x values.
	protected double[] mXValues = null;
	
	// The array of y values.
	protected double[] mYValues = null;

	/**
	 * The default constructor.
	 */
	public SGTwoDimensionalDataBuffer(double[] xValues, double[] yValues) {
		super();
		if (xValues == null || yValues == null) {
			throw new IllegalArgumentException("xValues == null || yValues == null");
		}
		this.mXValues = SGUtility.copyDoubleArray(xValues);
		this.mYValues = SGUtility.copyDoubleArray(yValues);
	}

	/**
	 * Returns the length of array of x direction.
	 * 
	 * @return the length of array of x direction
	 */
	public int getLengthX() {
		return this.mXValues.length;
	}
	
	/**
	 * Returns the length of array of y direction.
	 * 
	 * @return the length of array of y direction
	 */
	public int getLengthY() {
		return this.mYValues.length;
	}
	
	protected void checkTwoDimensionalArray(double[] xValues, double[] yValues, 
			double[][] twoDimValues, String name) {
		for (int ii = 0; ii < twoDimValues.length; ii++) {
			if (twoDimValues[ii] == null) {
				throw new IllegalArgumentException(name + "[" + ii + "] == null");
			}
		}
		if (twoDimValues.length != yValues.length) {
			throw new IllegalArgumentException(name + ".length != yValues.length");
		}
		for (int ii = 0; ii < twoDimValues.length; ii++) {
			if (twoDimValues[ii].length != xValues.length) {
				throw new IllegalArgumentException(name + "[" + ii + "].length != xValues.length");
			}
		}
	}

	protected void checkBlocks(double[] xValues, double[] yValues, 
			List<SGXYSimpleDoubleValueIndexBlock> blocks, String name) {
		final int xLen = xValues.length;
		final int yLen = yValues.length;
		for (int ii = 0; ii < blocks.size(); ii++) {
			SGXYSimpleDoubleValueIndexBlock block = blocks.get(ii);
			if (block == null) {
				throw new IllegalArgumentException(name + "[" + ii + "] == null");
			}
			SGIntegerSeries xSeries = block.getXSeries();
			int[] xIndices = xSeries.getNumbers();
			for (int jj = 0; jj < xIndices.length; jj++) {
				final int xIndex = xIndices[jj];
				if (xIndex < 0 || xIndex >= xLen) {
					throw new IllegalArgumentException("Index out of bounds: " + xIndex);
				}
			}
			SGIntegerSeries ySeries = block.getYSeries();
			int[] yIndices = ySeries.getNumbers();
			for (int jj = 0; jj < yIndices.length; jj++) {
				final int yIndex = yIndices[jj];
				if (yIndex < 0 || yIndex >= yLen) {
					throw new IllegalArgumentException("Index out of bounds: " + yIndex);
				}
			}
		}
	}

	/**
	 * Returns true if this data buffer is of grid type.
	 * 
	 * @return true if this data buffer is of grid type
	 */
	@Override
	public boolean isGridType() {
		return true;
	}

	protected List<SGXYSimpleDoubleValueIndexBlock> copyBlocks(List<SGXYSimpleDoubleValueIndexBlock> list) {
		List<SGXYSimpleDoubleValueIndexBlock> ret = new ArrayList<SGXYSimpleDoubleValueIndexBlock>();
		for (SGXYSimpleDoubleValueIndexBlock block : list) {
			ret.add((SGXYSimpleDoubleValueIndexBlock) block.clone());
		}
		return ret;
	}

	protected SGXYSimpleDoubleValueIndexBlock createBlock(final double[] xValues, 
			final double[] yValues, final double[][] twoDimValues) {
		final int num = xValues.length * yValues.length;
		double[] flatArray = new double[num];
		int cnt = 0;
		for (int yy = 0; yy < yValues.length; yy++) {
			for (int xx = 0; xx < xValues.length; xx++) {
				flatArray[cnt] = twoDimValues[yy][xx];
				cnt++;
			}
		}
		SGIntegerSeries xSeries = SGIntegerSeries.createInstance(xValues.length);
		SGIntegerSeries ySeries = SGIntegerSeries.createInstance(yValues.length);
		SGXYSimpleDoubleValueIndexBlock block = new SGXYSimpleDoubleValueIndexBlock(
				flatArray, xSeries, ySeries);
		return block;
	}
	
	protected double[][] getToDimensionalValues(List<SGXYSimpleDoubleValueIndexBlock> blocks) {
		final int xLen = this.getLengthX();
		final int yLen = this.getLengthY();
		List<Integer> xIndexList = new ArrayList<Integer>();
		for (int ii = 0; ii < xLen; ii++) {
			xIndexList.add(ii);
		}
		List<Integer> yIndexList = new ArrayList<Integer>();
		for (int ii = 0; ii < yLen; ii++) {
			yIndexList.add(ii);
		}
		double[][] ret = SGDataUtility.getTwoDimensionalValues(blocks, 
				xIndexList, yIndexList);
		return ret;
	}
}
