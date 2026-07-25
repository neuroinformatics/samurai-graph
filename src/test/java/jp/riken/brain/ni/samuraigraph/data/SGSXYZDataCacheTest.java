package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import org.junit.jupiter.api.Test;

class SGSXYZDataCacheTest {

  @Test
  void defaultConstructorFieldsAreNull() {
    SGSXYZDataCache cache = new SGSXYZDataCache();
    assertNull(cache.mXValues);
    assertNull(cache.mYValues);
    assertNull(cache.mZValues);
    assertNull(cache.mZValueBlockList);
  }

  @Test
  void setAndGetXValues() {
    SGSXYZDataCache cache = new SGSXYZDataCache();
    double[] values = {1.0, 2.0};
    cache.mXValues = values;
    assertArrayEquals(values, cache.mXValues, 0.0);
  }

  @Test
  void setAndGetYValues() {
    SGSXYZDataCache cache = new SGSXYZDataCache();
    double[] values = {3.0, 4.0};
    cache.mYValues = values;
    assertArrayEquals(values, cache.mYValues, 0.0);
  }

  @Test
  void setAndGetZValues() {
    SGSXYZDataCache cache = new SGSXYZDataCache();
    double[] values = {5.0, 6.0};
    cache.mZValues = values;
    assertArrayEquals(values, cache.mZValues, 0.0);
  }

  @Test
  void cloneProducesIndependentCopy() {
    SGSXYZDataCache original = new SGSXYZDataCache();
    original.mXValues = new double[] {1.0, 2.0};
    original.mYValues = new double[] {3.0, 4.0};
    original.mZValues = new double[] {5.0, 6.0};

    SGIntegerSeries col = new SGIntegerSeries(0, 1, 1);
    SGIntegerSeries row = new SGIntegerSeries(0, 1, 1);
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(new double[] {1.0, 2.0, 3.0, 4.0}, col, row);
    List<SGXYSimpleDoubleValueIndexBlock> blockList = new ArrayList<>();
    blockList.add(block);
    original.mZValueBlockList = blockList;

    SGSXYZDataCache copy = (SGSXYZDataCache) original.clone();
    assertNotSame(original, copy);
    assertArrayEquals(original.mXValues, copy.mXValues, 0.0);
    assertArrayEquals(original.mYValues, copy.mYValues, 0.0);
    assertArrayEquals(original.mZValues, copy.mZValues, 0.0);
    assertEquals(original.mZValueBlockList.size(), copy.mZValueBlockList.size());
    assertNotSame(original.mZValueBlockList, copy.mZValueBlockList);

    copy.mXValues[0] = 99.0;
    assertArrayEquals(new double[] {1.0, 2.0}, original.mXValues, 0.0);
  }

  @Test
  void cloneWithNullBlockList() {
    SGSXYZDataCache original = new SGSXYZDataCache();
    original.mXValues = new double[] {1.0};
    SGSXYZDataCache copy = (SGSXYZDataCache) original.clone();
    assertNull(copy.mZValueBlockList);
  }
}
