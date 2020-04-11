package jp.riken.brain.ni.samuraigraph.data;

import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGNamedIndexBlock;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

/**
 * The class of a block of double values with names.
 *
 */
public class SGNamedDoubleValueIndexBlock extends SGNamedIndexBlock {

	/**
	 * The array of double values.
	 */
	protected double[] mValues = null;

	/**
	 * Builds an block object.
	 * 
	 * @param seriesMap
	 *           the map of dimension indices
	 */
	public SGNamedDoubleValueIndexBlock(final double[] values, Map<String, SGIntegerSeries> seriesMap) {
		super(seriesMap);
		int len = 1;
		for (SGIntegerSeries s : seriesMap.values()) {
			len *= s.getLength();
		}
		if (values.length != len) {
			throw new IllegalArgumentException("Invalid array length: " + values.length + " must be equal to " + len);
		}
		this.mValues = SGUtility.copyDoubleArray(values);
	}
	
    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
	@Override
    public Object clone() {
		SGNamedDoubleValueIndexBlock ret = (SGNamedDoubleValueIndexBlock) super.clone();
    	ret.mValues = SGUtility.copyDoubleArray(this.mValues);
    	return ret;
    }

	/**
	 * Returns the array of double values.
	 * 
	 * @return the array of double values
	 */
	public double[] getValues() {
		return SGUtility.copyDoubleArray(this.mValues);
	}

	/**
	 * Returns the double value at given array index.
	 * 
	 * @param index
	 *          the array index
	 * @return the double value at given array index
	 */
	public double getValue(final int index) {
		return this.mValues[index];
	}
	
	/**
	 * Returns the length of value array.
	 * 
	 * @return the length of value array
	 */
	public int getLength() {
		return this.mValues.length;
	}

}
