package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import org.junit.jupiter.api.Test;

class SGVXYDataCacheTest {

  @Test
  void defaultConstructorFieldsAreNull() {
    SGVXYDataCache cache = new SGVXYDataCache();
    assertNull(cache.mXValues);
    assertNull(cache.mYValues);
    assertNull(cache.mFirstComponentValues);
    assertNull(cache.mSecondComponentValues);
    assertNull(cache.mFirstComponentValueBlockList);
    assertNull(cache.mSecondComponentValueBlockList);
  }

  @Test
  void setAndGetXValues() {
    SGVXYDataCache cache = new SGVXYDataCache();
    double[] values = {1.0, 2.0};
    cache.mXValues = values;
    assertArrayEquals(values, cache.mXValues, 0.0);
  }

  @Test
  void setAndGetYValues() {
    SGVXYDataCache cache = new SGVXYDataCache();
    double[] values = {3.0, 4.0};
    cache.mYValues = values;
    assertArrayEquals(values, cache.mYValues, 0.0);
  }

  @Test
  void cloneProducesIndependentCopy() {
    SGVXYDataCache original = new SGVXYDataCache();
    original.mXValues = new double[] {1.0, 2.0};
    original.mYValues = new double[] {3.0, 4.0};
    original.mFirstComponentValues = new double[] {5.0, 6.0};
    original.mSecondComponentValues = new double[] {7.0, 8.0};

    SGIntegerSeries col = new SGIntegerSeries(0, 1, 1);
    SGIntegerSeries row = new SGIntegerSeries(0, 1, 1);
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(new double[] {1.0, 2.0, 3.0, 4.0}, col, row);
    List<SGXYSimpleDoubleValueIndexBlock> blockList = new ArrayList<>();
    blockList.add(block);
    original.mFirstComponentValueBlockList = blockList;
    original.mSecondComponentValueBlockList = new ArrayList<>(blockList);

    SGVXYDataCache copy = (SGVXYDataCache) original.clone();
    assertNotSame(original, copy);
    assertArrayEquals(original.mXValues, copy.mXValues, 0.0);
    assertArrayEquals(original.mYValues, copy.mYValues, 0.0);
    assertArrayEquals(original.mFirstComponentValues, copy.mFirstComponentValues, 0.0);
    assertArrayEquals(original.mSecondComponentValues, copy.mSecondComponentValues, 0.0);
    assertEquals(
        original.mFirstComponentValueBlockList.size(), copy.mFirstComponentValueBlockList.size());
    assertNotSame(original.mFirstComponentValueBlockList, copy.mFirstComponentValueBlockList);

    copy.mXValues[0] = 99.0;
    assertArrayEquals(new double[] {1.0, 2.0}, original.mXValues, 0.0);
  }

  @Test
  void cloneWithNullBlockLists() {
    SGVXYDataCache original = new SGVXYDataCache();
    original.mXValues = new double[] {1.0};
    SGVXYDataCache copy = (SGVXYDataCache) original.clone();
    assertNull(copy.mFirstComponentValueBlockList);
    assertNull(copy.mSecondComponentValueBlockList);
  }
}
