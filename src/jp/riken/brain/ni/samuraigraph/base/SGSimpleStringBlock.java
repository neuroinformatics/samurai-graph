package jp.riken.brain.ni.samuraigraph.base;

/**
 * The class of a block of text strings with indices.
 *
 */
public class SGSimpleStringBlock extends SGStringBlock {
	/**
	 * The map of index series.
	 */
	private SGIntegerSeries[] mSeriesArray = null;
	
	/**
	 * Builds an block object.
	 * 
	 * @param seriesArray
	 *           the array of dimension indices
	 */
	public SGSimpleStringBlock(final String[] values, SGIntegerSeries[] seriesArray) {
		super(values);
		if (seriesArray == null) {
			throw new IllegalArgumentException("series == null");
		}
		int len = 1;
		for (SGIntegerSeries s : seriesArray) {
			len *= s.getLength();
		}
		if (values.length != len) {
			throw new IllegalArgumentException("Invalid array length: " + values.length + " must be equal to " + len);
		}
		this.mSeriesArray = seriesArray.clone();
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
}
