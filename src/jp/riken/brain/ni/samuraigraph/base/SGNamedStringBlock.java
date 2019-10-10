package jp.riken.brain.ni.samuraigraph.base;

import java.util.HashMap;
import java.util.Map;

/**
 * The class of a block of text strings with names.
 *
 */
public class SGNamedStringBlock extends SGStringBlock {
	/**
	 * The map of index series.
	 */
	private Map<String, SGIntegerSeries> mSeriesMap = null;
	
	/**
	 * Builds an block object.
	 * 
	 * @param seriesMap
	 *           the map of dimension indices
	 */
	public SGNamedStringBlock(final String[] values, Map<String, SGIntegerSeries> seriesMap) {
		super(values);
		if (seriesMap == null) {
			throw new IllegalArgumentException("seriesMap == null");
		}
		if (seriesMap.size() == 0) {
			throw new IllegalArgumentException("seriesMap.size() == 0");
		}
		int len = 1;
		for (SGIntegerSeries s : seriesMap.values()) {
			len *= s.getLength();
		}
		if (values.length != len) {
			throw new IllegalArgumentException("Invalid array length: " + values.length + " must be equal to " + len);
		}
		this.mSeriesMap = new HashMap<String, SGIntegerSeries>(seriesMap);
	}
	
	/**
	 * Returns the series of given dimension name.
	 * 
	 * @param name
	 *           the name of dimension
	 * @return the series of given dimension name
	 */
	public SGIntegerSeries getSeries(final String name) {
		return this.mSeriesMap.get(name);
	}

	/**
	 * Returns a text string for this object.
	 * 
	 * @return a text string for this object
	 */
	public String paramString() {
		StringBuffer sb = new StringBuffer();
		sb.append(", map=");
		sb.append(this.mSeriesMap.toString());
		return sb.toString();
	}

}
