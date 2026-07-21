package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

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

  static List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockList(SGArrayData data) {
    List<SGXYSimpleDoubleValueIndexBlock> ret = null;
    SGSXYZDataCache cache = getCache(data);
    if (cache.mZValueBlockList != null) {
      ret = cache.mZValueBlockList;
    }
    return ret;
  }

  static void setZValueBlockList(
      SGArrayData data, List<SGXYSimpleDoubleValueIndexBlock> blockList) {
    SGSXYZDataCache cache = getCache(data);
    cache.mZValueBlockList = blockList;
  }
}
