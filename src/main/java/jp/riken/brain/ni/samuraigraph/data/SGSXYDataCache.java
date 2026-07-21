package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGUtility;

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
