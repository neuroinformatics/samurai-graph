package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

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

  static List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlockList(SGArrayData data) {
    List<SGXYSimpleDoubleValueIndexBlock> ret = null;
    SGVXYDataCache cache = getCache(data);
    if (cache.mFirstComponentValueBlockList != null) {
      ret = cache.mFirstComponentValueBlockList;
    }
    return ret;
  }

  static void setFirstComponentValueBlockList(
      SGArrayData data, List<SGXYSimpleDoubleValueIndexBlock> blockList) {
    SGVXYDataCache cache = getCache(data);
    cache.mFirstComponentValueBlockList = blockList;
  }

  static List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlockList(SGArrayData data) {
    List<SGXYSimpleDoubleValueIndexBlock> ret = null;
    SGVXYDataCache cache = getCache(data);
    if (cache.mSecondComponentValueBlockList != null) {
      ret = cache.mSecondComponentValueBlockList;
    }
    return ret;
  }

  static void setSecondComponentValueBlockList(
      SGArrayData data, List<SGXYSimpleDoubleValueIndexBlock> blockList) {
    SGVXYDataCache cache = getCache(data);
    cache.mSecondComponentValueBlockList = blockList;
  }
}
