package jp.riken.brain.ni.samuraigraph.base;


public class SGSimpleIndexBlock extends SGIndexBlock {
	
	protected SGIntegerSeries[] mSeriesArray = null;

	/**
	 * Builds an block object.
	 * 
	 * @param seriesArray
	 *           the array of dimension indices
	 */
	public SGSimpleIndexBlock(SGIntegerSeries[] seriesArray) {
		super();
		if (seriesArray == null) {
			throw new IllegalArgumentException("series == null");
		}
		this.mSeriesArray = new SGIntegerSeries[seriesArray.length];
		for (int ii = 0; ii < seriesArray.length; ii++) {
			this.mSeriesArray[ii] = (SGIntegerSeries) seriesArray[ii].clone();
		}
	}
	
	/**
	 * Returns the series of given dimension name.
	 * 
	 * @param dim
	 *           index of the dimension
	 * @return the series of given dimension name
	 */
	public SGIntegerSeries getSeries(final int dim) {
		return this.mSeriesArray[dim];
	}
	
	/**
	 * Returns the array of series.
	 * 
	 * @return the array of series
	 */
	public SGIntegerSeries[] getSeriesArray() {
		return this.mSeriesArray.clone();
	}

	/**
	 * Returns a text string for this object.
	 * 
	 * @return a text string for this object
	 */
	public String paramString() {
		StringBuffer sb = new StringBuffer();
		sb.append(", seriesArray=[");
		for (int ii = 0; ii < this.mSeriesArray.length; ii++) {
			if (ii > 0) {
				sb.append(", ");
			}
			sb.append(this.mSeriesArray[ii].toString());
		}
		sb.append("]");
		return sb.toString();
	}

    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
	@Override
    public Object clone() {
		SGSimpleIndexBlock ret = (SGSimpleIndexBlock) super.clone();
		ret.mSeriesArray = new SGIntegerSeries[this.mSeriesArray.length];
		for (int ii = 0; ii < this.mSeriesArray.length; ii++) {
			ret.mSeriesArray[ii] = (SGIntegerSeries) this.mSeriesArray[ii].clone();
		}
    	return ret;
    }

}
