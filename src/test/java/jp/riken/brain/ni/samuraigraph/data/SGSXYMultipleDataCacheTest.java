package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class SGSXYMultipleDataCacheTest {

  @Test
  void defaultConstructor() {
    SGSXYMultipleDataCache cache = new SGSXYMultipleDataCache();
    assertNull(cache.mCacheArray);
  }

  @Test
  void constructorWithCache() {
    SGSXYDataCache inner = new SGSXYDataCache();
    inner.mXValues = new double[] {1.0, 2.0};
    SGSXYMultipleDataCache cache = new SGSXYMultipleDataCache(inner);
    assertNotNull(cache.mCacheArray);
    assertEquals(1, cache.mCacheArray.length);
    assertArrayEquals(new double[] {1.0, 2.0}, cache.mCacheArray[0].mXValues, 0.0);
  }

  @Test
  void constructorWithNullCache() {
    SGSXYMultipleDataCache cache = new SGSXYMultipleDataCache(null);
    assertNull(cache.mCacheArray);
  }

  @Test
  void cloneProducesIndependentCopy() {
    SGSXYDataCache inner = new SGSXYDataCache();
    inner.mXValues = new double[] {1.0, 2.0};
    inner.mYValues = new double[] {3.0, 4.0};
    SGSXYMultipleDataCache original = new SGSXYMultipleDataCache(inner);

    SGSXYMultipleDataCache copy = (SGSXYMultipleDataCache) original.clone();
    assertNotSame(original, copy);
    assertNotNull(copy.mCacheArray);
    assertEquals(1, copy.mCacheArray.length);
    assertArrayEquals(original.mCacheArray[0].mXValues, copy.mCacheArray[0].mXValues, 0.0);
    assertNotSame(original.mCacheArray, copy.mCacheArray);
    assertNotSame(original.mCacheArray[0], copy.mCacheArray[0]);

    copy.mCacheArray[0].mXValues[0] = 99.0;
    assertArrayEquals(new double[] {1.0, 2.0}, original.mCacheArray[0].mXValues, 0.0);
  }

  @Test
  void cloneWithNullCacheArray() {
    SGSXYMultipleDataCache original = new SGSXYMultipleDataCache();
    SGSXYMultipleDataCache copy = (SGSXYMultipleDataCache) original.clone();
    assertNull(copy.mCacheArray);
  }
}
