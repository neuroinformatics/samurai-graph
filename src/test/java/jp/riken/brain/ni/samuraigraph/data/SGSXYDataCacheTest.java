package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class SGSXYDataCacheTest {

  @Test
  void defaultConstructorFieldsAreNull() {
    SGSXYDataCache cache = new SGSXYDataCache();
    assertNull(cache.mXValues);
    assertNull(cache.mYValues);
    assertNull(cache.mLowerErrorValues);
    assertNull(cache.mUpperErrorValues);
    assertNull(cache.mTickLabels);
  }

  @Test
  void setAndGetXValues() {
    SGSXYDataCache cache = new SGSXYDataCache();
    double[] values = {1.0, 2.0, 3.0};
    cache.setXValues(values);
    assertArrayEquals(values, cache.getXValues(), 0.0);
  }

  @Test
  void setAndGetYValues() {
    SGSXYDataCache cache = new SGSXYDataCache();
    double[] values = {4.0, 5.0, 6.0};
    cache.setYValues(values);
    assertArrayEquals(values, cache.getYValues(), 0.0);
  }

  @Test
  void setAndGetLowerErrorValues() {
    SGSXYDataCache cache = new SGSXYDataCache();
    double[] values = {0.1, 0.2};
    cache.mLowerErrorValues = values;
    assertArrayEquals(values, cache.mLowerErrorValues, 0.0);
  }

  @Test
  void setAndGetUpperErrorValues() {
    SGSXYDataCache cache = new SGSXYDataCache();
    double[] values = {0.3, 0.4};
    cache.mUpperErrorValues = values;
    assertArrayEquals(values, cache.mUpperErrorValues, 0.0);
  }

  @Test
  void setAndGetTickLabels() {
    SGSXYDataCache cache = new SGSXYDataCache();
    String[] labels = {"a", "b", "c"};
    cache.mTickLabels = labels;
    assertArrayEquals(labels, cache.mTickLabels);
  }

  @Test
  void cloneProducesIndependentCopy() {
    SGSXYDataCache original = new SGSXYDataCache();
    original.mXValues = new double[] {1.0, 2.0};
    original.mYValues = new double[] {3.0, 4.0};
    original.mLowerErrorValues = new double[] {0.1, 0.2};
    original.mUpperErrorValues = new double[] {0.3, 0.4};
    original.mTickLabels = new String[] {"a", "b"};

    SGSXYDataCache copy = (SGSXYDataCache) original.clone();
    assertNotSame(original, copy);
    assertArrayEquals(original.mXValues, copy.mXValues, 0.0);
    assertArrayEquals(original.mYValues, copy.mYValues, 0.0);
    assertArrayEquals(original.mLowerErrorValues, copy.mLowerErrorValues, 0.0);
    assertArrayEquals(original.mUpperErrorValues, copy.mUpperErrorValues, 0.0);
    assertArrayEquals(original.mTickLabels, copy.mTickLabels);

    copy.mXValues[0] = 99.0;
    assertArrayEquals(new double[] {1.0, 2.0}, original.mXValues, 0.0);
  }
}
