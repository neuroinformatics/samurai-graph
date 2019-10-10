package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;

interface SGINumberDataColumn {
	
    /**
     * Returns an array of numbers.
     * 
     * @return an array of numbers
     */
    public double[] getNumberArray();

    /**
     * Returns an array of numbers with given stride.
     * 
     * @param stride
     *           the stride of an array
     * @return an array of numbers
     */
    public double[] getNumberArray(SGIntegerSeriesSet stride);

    /**
     * Returns an array of numbers at given array indices.
     * 
     * @param indices
     *           array indices
     * @return an array of numbers
     */
    public double[] getNumberArray(int[] indices);

}
