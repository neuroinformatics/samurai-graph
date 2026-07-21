package jp.riken.brain.ni.samuraigraph.data;

class SGSXYMultipleDataCache extends SGDataCache {

  SGSXYDataCache[] mCacheArray = null;

  SGSXYMultipleDataCache() {
    super();
  }

  SGSXYMultipleDataCache(SGSXYDataCache c) {
    this();
    if (c != null) {
      this.mCacheArray = new SGSXYDataCache[] {c};
    }
  }

  @Override
  public Object clone() {
    SGSXYMultipleDataCache cache = (SGSXYMultipleDataCache) super.clone();
    if (this.mCacheArray != null) {
      cache.mCacheArray = new SGSXYDataCache[this.mCacheArray.length];
      for (int ii = 0; ii < this.mCacheArray.length; ii++) {
        cache.mCacheArray[ii] =
            (this.mCacheArray[ii] != null) ? (SGSXYDataCache) this.mCacheArray[ii].clone() : null;
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
