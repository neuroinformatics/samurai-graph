package jp.riken.brain.ni.samuraigraph.base;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class SGNamedIndexBlock extends SGIndexBlock {

	/**
	 * The map of index series.
	 */
	protected Map<String, SGIntegerSeries> mSeriesMap = null;

	/**
	 * Builds an block object.
	 * 
	 * @param seriesMap
	 *           the map of dimension indices
	 */
	public SGNamedIndexBlock(Map<String, SGIntegerSeries> seriesMap) {
		super();
		if (seriesMap == null) {
			throw new IllegalArgumentException("seriesMap == null");
		}
		if (seriesMap.size() == 0) {
			throw new IllegalArgumentException("seriesMap.size() == 0");
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
     * Clones this object.
     * 
     * @return the copy of this object
     */
	@Override
    public Object clone() {
		SGNamedIndexBlock ret = (SGNamedIndexBlock) super.clone();
    	ret.mSeriesMap = new HashMap<String, SGIntegerSeries>();
    	Iterator<Entry<String, SGIntegerSeries>> itr = this.mSeriesMap.entrySet().iterator();
    	while (itr.hasNext()) {
    		Entry<String, SGIntegerSeries> entry = itr.next();
    		String name = entry.getKey();
    		SGIntegerSeries series = entry.getValue();
    		ret.mSeriesMap.put(name, (SGIntegerSeries) series.clone());
    	}
    	return ret;
    }

}
