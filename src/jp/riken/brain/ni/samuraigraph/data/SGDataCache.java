package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGUtility;

abstract class SGDataCache implements Cloneable {
	
	SGDataCache() {
		super();
	}

	@Override
    public Object clone() {
        try {
        	return super.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
}

class SGSXYDataCache extends SGDataCache {

	double[] mXValues = null;
	
	double[] mYValues = null;

	double[] mLowerErrorValues = null;

	double[] mUpperErrorValues = null;
	
	String[] mTickLabels = null;

	SGSXYDataCache() {
		super();
	}

	@Override
    public Object clone() {
    	SGSXYDataCache cache = (SGSXYDataCache) super.clone();
    	cache.mXValues = SGUtility.copyDoubleArray(this.mXValues);
    	cache.mYValues = SGUtility.copyDoubleArray(this.mYValues);
    	cache.mLowerErrorValues = SGUtility.copyDoubleArray(this.mLowerErrorValues);
    	cache.mUpperErrorValues = SGUtility.copyDoubleArray(this.mUpperErrorValues);
    	cache.mTickLabels = SGUtility.copyStringArray(this.mTickLabels);
    	return cache;
    }

	static SGSXYDataCache getCache(SGArrayData data) {
		SGSXYDataCache cache = (SGSXYDataCache) data.getCache();
		if (cache == null) {
			cache = new SGSXYDataCache();
			data.setCache(cache);
		}
		return cache;
	}
	
	static double[] getXValues(SGArrayData data) {
		double[] ret = null;
		SGSXYDataCache cache = getCache(data);
		ret = cache.getXValues();
		return ret;
	}
	
	static void setXValues(SGArrayData data, double[] values) {
		SGSXYDataCache cache = getCache(data);
		cache.mXValues = values;
	}

	double[] getXValues() {
		return this.mXValues;
	}
	
	void setXValues(double[] values) {
		this.mXValues = values;
	}

	static double[] getYValues(SGArrayData data) {
		double[] ret = null;
		SGSXYDataCache cache = getCache(data);
		ret = cache.getYValues();
		return ret;
	}

	static void setYValues(SGArrayData data, double[] values) {
		SGSXYDataCache cache = getCache(data);
		cache.mYValues = values;
	}

	double[] getYValues() {
		return this.mYValues;
	}
	
	void setYValues(double[] values) {
		this.mYValues = values;
	}

	static double[] getLowerErrorValues(SGArrayData data) {
		double[] ret = null;
		SGSXYDataCache cache = getCache(data);
		ret = cache.mLowerErrorValues;
		return ret;
	}
	
	static void setLowerErrorValues(SGArrayData data, double[] values) {
		SGSXYDataCache cache = getCache(data);
		cache.mLowerErrorValues = values;
	}

	static double[] getUpperErrorValues(SGArrayData data) {
		double[] ret = null;
		SGSXYDataCache cache = getCache(data);
		ret = cache.mUpperErrorValues;
		return ret;
	}

	static void setUpperErrorValues(SGArrayData data, double[] values) {
		SGSXYDataCache cache = getCache(data);
		cache.mUpperErrorValues = values;
	}

	static String[] getTickLabels(SGArrayData data) {
		String[] ret = null;
		SGSXYDataCache cache = getCache(data);
		ret = cache.mTickLabels;
		return ret;
	}
	
	static void setTickLabels(SGArrayData data, String[] values) {
		SGSXYDataCache cache = getCache(data);
		cache.mTickLabels = values;
	}
}

class SGSXYMultipleDataCache extends SGDataCache {
	
	SGSXYDataCache[] mCacheArray = null;

	SGSXYMultipleDataCache() {
		super();
	}
	
	SGSXYMultipleDataCache(SGSXYDataCache c) {
		this();
		if (c != null) {
			this.mCacheArray = new SGSXYDataCache[] { c };
		}
	}
	
	@Override
    public Object clone() {
		SGSXYMultipleDataCache cache = (SGSXYMultipleDataCache) super.clone();
		if (this.mCacheArray != null) {
			cache.mCacheArray = new SGSXYDataCache[this.mCacheArray.length];
			for (int ii = 0; ii < this.mCacheArray.length; ii++) {
				cache.mCacheArray[ii] = (this.mCacheArray[ii] != null) ? (SGSXYDataCache) this.mCacheArray[ii].clone() : null;
			}
		}
    	return cache;
    }

	static SGSXYMultipleDataCache getCache(SGArrayData data) {
		SGSXYMultipleDataCache cache = (SGSXYMultipleDataCache) data.getCache();
		if (cache == null) {
			cache = new SGSXYMultipleDataCache();
			data.setCache(cache);
		}
		return cache;
	}

	static double[][] getXValues(SGArrayData data) {
		double[][] ret = null;
		SGSXYMultipleDataCache cache = getCache(data);
		if (cache.mCacheArray != null) {
			ret = new double[cache.mCacheArray.length][];
			for (int ii = 0; ii < ret.length; ii++) {
				if (cache.mCacheArray[ii] != null) {
					ret[ii] = cache.mCacheArray[ii].mXValues;
				}
			}
		}
		return ret;
	}

	static double[][] getYValues(SGArrayData data) {
		double[][] ret = null;
		SGSXYMultipleDataCache cache = getCache(data);
		if (cache.mCacheArray != null) {
			ret = new double[cache.mCacheArray.length][];
			for (int ii = 0; ii < ret.length; ii++) {
				if (cache.mCacheArray[ii] != null) {
					ret[ii] = cache.mCacheArray[ii].mYValues;
				}
			}
		}
		return ret;
	}

	static double[][] getLowerErrorValues(SGArrayData data) {
		double[][] ret = null;
		SGSXYMultipleDataCache cache = getCache(data);
		if (cache.mCacheArray != null) {
			ret = new double[cache.mCacheArray.length][];
			for (int ii = 0; ii < ret.length; ii++) {
				ret[ii] = cache.mCacheArray[ii].mLowerErrorValues;
			}
		}
		return ret;
	}

	static double[][] getUpperErrorValues(SGArrayData data) {
		double[][] ret = null;
		SGSXYMultipleDataCache cache = getCache(data);
		if (cache.mCacheArray != null) {
			ret = new double[cache.mCacheArray.length][];
			for (int ii = 0; ii < ret.length; ii++) {
				ret[ii] = cache.mCacheArray[ii].mUpperErrorValues;
			}
		}
		return ret;
	}

	static String[][] getTickLabels(SGArrayData data) {
		String[][] ret = null;
		SGSXYMultipleDataCache cache = getCache(data);
		if (cache.mCacheArray != null) {
			ret = new String[cache.mCacheArray.length][];
			for (int ii = 0; ii < ret.length; ii++) {
				ret[ii] = cache.mCacheArray[ii].mTickLabels;
			}
		}
		return ret;
	}
}

class SGSXYZDataCache extends SGDataCache {

	double[] mXValues = null;
	
	double[] mYValues = null;

	double[] mZValues = null;

	List<SGXYSimpleDoubleValueIndexBlock> mZValueBlockList = null;
	
	SGSXYZDataCache() {
		super();
	}
	
	@Override
    public Object clone() {
		SGSXYZDataCache cache = (SGSXYZDataCache) super.clone();
    	cache.mXValues = SGUtility.copyDoubleArray(this.mXValues);
    	cache.mYValues = SGUtility.copyDoubleArray(this.mYValues);
    	cache.mZValues = SGUtility.copyDoubleArray(this.mZValues);
    	if (this.mZValueBlockList != null) {
    		cache.mZValueBlockList = new ArrayList<SGXYSimpleDoubleValueIndexBlock>();
    		for (SGXYSimpleDoubleValueIndexBlock block : this.mZValueBlockList) {
    			cache.mZValueBlockList.add((SGXYSimpleDoubleValueIndexBlock) block.clone());
    		}
    	}
    	return cache;
    }

	static SGSXYZDataCache getCache(SGArrayData data) {
		SGSXYZDataCache cache = (SGSXYZDataCache) data.getCache();
		if (cache == null) {
			cache = new SGSXYZDataCache();
			data.setCache(cache);
		}
		return cache;
	}
	
	static double[] getXValues(SGArrayData data) {
		double[] ret = null;
		SGSXYZDataCache cache = getCache(data);
		if (cache.mXValues != null) {
			ret = cache.mXValues;
		}
		return ret;
	}

	static void setXValues(SGArrayData data, double[] values) {
		SGSXYZDataCache cache = getCache(data);
		cache.mXValues = values;
	}
	
	static double[] getYValues(SGArrayData data) {
		double[] ret = null;
		SGSXYZDataCache cache = getCache(data);
		if (cache.mYValues != null) {
			ret = cache.mYValues;
		}
		return ret;
	}

	static void setYValues(SGArrayData data, double[] values) {
		SGSXYZDataCache cache = getCache(data);
		cache.mYValues = values;
	}

	static double[] getZValues(SGArrayData data) {
		double[] ret = null;
		SGSXYZDataCache cache = getCache(data);
		if (cache.mZValues != null) {
			ret = cache.mZValues;
		}
		return ret;
	}

	static void setZValues(SGArrayData data, double[] values) {
		SGSXYZDataCache cache = getCache(data);
		cache.mZValues = values;
	}

	static List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockList(
			SGArrayData data) {
		List<SGXYSimpleDoubleValueIndexBlock> ret = null;
		SGSXYZDataCache cache = getCache(data);
		if (cache.mZValueBlockList != null) {
			ret = cache.mZValueBlockList;
		}
		return ret;
	}

	static void setZValueBlockList(SGArrayData data, 
			List<SGXYSimpleDoubleValueIndexBlock> blockList) {
		SGSXYZDataCache cache = getCache(data);
		cache.mZValueBlockList = blockList;
	}
}

class SGVXYDataCache extends SGDataCache {

	double[] mXValues = null;
	
	double[] mYValues = null;
	
	double[] mFirstComponentValues = null;
	
	double[] mSecondComponentValues = null;
	
	List<SGXYSimpleDoubleValueIndexBlock> mFirstComponentValueBlockList = null;

	List<SGXYSimpleDoubleValueIndexBlock> mSecondComponentValueBlockList = null;

	SGVXYDataCache() {
		super();
	}

	@Override
    public Object clone() {
		SGVXYDataCache cache = (SGVXYDataCache) super.clone();
    	cache.mXValues = SGUtility.copyDoubleArray(this.mXValues);
    	cache.mYValues = SGUtility.copyDoubleArray(this.mYValues);
    	cache.mFirstComponentValues = SGUtility.copyDoubleArray(this.mFirstComponentValues);
    	cache.mSecondComponentValues = SGUtility.copyDoubleArray(this.mSecondComponentValues);
    	if (this.mFirstComponentValueBlockList != null) {
    		cache.mFirstComponentValueBlockList = new ArrayList<SGXYSimpleDoubleValueIndexBlock>();
    		for (SGXYSimpleDoubleValueIndexBlock block : this.mFirstComponentValueBlockList) {
    			cache.mFirstComponentValueBlockList.add((SGXYSimpleDoubleValueIndexBlock) block.clone());
    		}
    	}
    	if (this.mSecondComponentValueBlockList != null) {
    		cache.mSecondComponentValueBlockList = new ArrayList<SGXYSimpleDoubleValueIndexBlock>();
    		for (SGXYSimpleDoubleValueIndexBlock block : this.mSecondComponentValueBlockList) {
    			cache.mSecondComponentValueBlockList.add((SGXYSimpleDoubleValueIndexBlock) block.clone());
    		}
    	}
    	return cache;
    }

	static SGVXYDataCache getCache(SGArrayData data) {
		SGVXYDataCache cache = (SGVXYDataCache) data.getCache();
		if (cache == null) {
			cache = new SGVXYDataCache();
			data.setCache(cache);
		}
		return cache;
	}
	
	static double[] getXValues(SGArrayData data) {
		double[] ret = null;
		SGVXYDataCache cache = getCache(data);
		if (cache.mXValues != null) {
			ret = cache.mXValues;
		}
		return ret;
	}

	static void setXValues(SGArrayData data, double[] values) {
		SGVXYDataCache cache = getCache(data);
		cache.mXValues = values;
	}

	static double[] getYValues(SGArrayData data) {
		double[] ret = null;
		SGVXYDataCache cache = getCache(data);
		if (cache.mYValues != null) {
			ret = cache.mYValues;
		}
		return ret;
	}

	static void setYValues(SGArrayData data, double[] values) {
		SGVXYDataCache cache = getCache(data);
		cache.mYValues = values;
	}

	static double[] getFirstComponentValues(SGArrayData data) {
		double[] ret = null;
		SGVXYDataCache cache = getCache(data);
		if (cache.mFirstComponentValues != null) {
			ret = cache.mFirstComponentValues;
		}
		return ret;
	}

	static void setFirstComponentValues(SGArrayData data, double[] values) {
		SGVXYDataCache cache = getCache(data);
		cache.mFirstComponentValues = values;
	}

	static double[] getSecondComponentValues(SGArrayData data) {
		double[] ret = null;
		SGVXYDataCache cache = getCache(data);
		if (cache.mSecondComponentValues != null) {
			ret = cache.mSecondComponentValues;
		}
		return ret;
	}

	static void setSecondComponentValues(SGArrayData data, double[] values) {
		SGVXYDataCache cache = getCache(data);
		cache.mSecondComponentValues = values;
	}

	static List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlockList(
			SGArrayData data) {
		List<SGXYSimpleDoubleValueIndexBlock> ret = null;
		SGVXYDataCache cache = getCache(data);
		if (cache.mFirstComponentValueBlockList != null) {
			ret = cache.mFirstComponentValueBlockList;
		}
		return ret;
	}

	static void setFirstComponentValueBlockList(SGArrayData data, 
			List<SGXYSimpleDoubleValueIndexBlock> blockList) {
		SGVXYDataCache cache = getCache(data);
		cache.mFirstComponentValueBlockList = blockList;;
	}

	static List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlockList(
			SGArrayData data) {
		List<SGXYSimpleDoubleValueIndexBlock> ret = null;
		SGVXYDataCache cache = getCache(data);
		if (cache.mSecondComponentValueBlockList != null) {
			ret = cache.mSecondComponentValueBlockList;
		}
		return ret;
	}

	static void setSecondComponentValueBlockList(SGArrayData data, 
			List<SGXYSimpleDoubleValueIndexBlock> blockList) {
		SGVXYDataCache cache = getCache(data);
		cache.mSecondComponentValueBlockList = blockList;;
	}

}
