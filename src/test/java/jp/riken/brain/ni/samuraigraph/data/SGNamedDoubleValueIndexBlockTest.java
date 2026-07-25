package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import org.junit.jupiter.api.Test;

class SGNamedDoubleValueIndexBlockTest {

  @Test
  void constructorWithValues() {
    Map<String, SGIntegerSeries> seriesMap = new HashMap<>();
    seriesMap.put("x", new SGIntegerSeries(0, 2, 1));
    seriesMap.put("y", new SGIntegerSeries(0, 1, 1));
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGNamedDoubleValueIndexBlock block = new SGNamedDoubleValueIndexBlock(values, seriesMap);
    assertArrayEquals(values, block.getValues(), 0.0);
    assertEquals(6, block.getLength());
  }

  @Test
  void constructorWithSingleEntry() {
    Map<String, SGIntegerSeries> seriesMap = new HashMap<>();
    seriesMap.put("t", new SGIntegerSeries(0, 2, 1));
    double[] values = {1.0, 2.0, 3.0};
    SGNamedDoubleValueIndexBlock block = new SGNamedDoubleValueIndexBlock(values, seriesMap);
    assertArrayEquals(values, block.getValues(), 0.0);
    assertEquals(3, block.getLength());
  }

  @Test
  void constructorWithMismatchedLengthThrows() {
    Map<String, SGIntegerSeries> seriesMap = new HashMap<>();
    seriesMap.put("x", new SGIntegerSeries(0, 2, 1));
    seriesMap.put("y", new SGIntegerSeries(0, 1, 1));
    double[] values = {1.0, 2.0};
    assertThrows(
        IllegalArgumentException.class, () -> new SGNamedDoubleValueIndexBlock(values, seriesMap));
  }

  @Test
  void constructorWithEmptyValuesThrows() {
    Map<String, SGIntegerSeries> seriesMap = new HashMap<>();
    seriesMap.put("x", new SGIntegerSeries(0, 2, 1));
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGNamedDoubleValueIndexBlock(new double[] {}, seriesMap));
  }

  @Test
  void getValueByIndex() {
    Map<String, SGIntegerSeries> seriesMap = new HashMap<>();
    seriesMap.put("x", new SGIntegerSeries(0, 2, 1));
    double[] values = {1.0, 2.0, 3.0};
    SGNamedDoubleValueIndexBlock block = new SGNamedDoubleValueIndexBlock(values, seriesMap);
    assertEquals(2.0, block.getValue(1), 0.0);
  }

  @Test
  void getValuesReturnsDefensiveCopy() {
    Map<String, SGIntegerSeries> seriesMap = new HashMap<>();
    seriesMap.put("x", new SGIntegerSeries(0, 2, 1));
    double[] values = {1.0, 2.0, 3.0};
    SGNamedDoubleValueIndexBlock block = new SGNamedDoubleValueIndexBlock(values, seriesMap);
    double[] retrieved = block.getValues();
    retrieved[0] = 99.0;
    assertEquals(1.0, block.getValue(0), 0.0);
  }

  @Test
  void constructorCopiesArray() {
    Map<String, SGIntegerSeries> seriesMap = new HashMap<>();
    seriesMap.put("x", new SGIntegerSeries(0, 2, 1));
    double[] values = {1.0, 2.0, 3.0};
    SGNamedDoubleValueIndexBlock block = new SGNamedDoubleValueIndexBlock(values, seriesMap);
    values[0] = 99.0;
    assertEquals(1.0, block.getValue(0), 0.0);
  }

  @Test
  void cloneProducesIndependentCopy() {
    Map<String, SGIntegerSeries> seriesMap = new HashMap<>();
    seriesMap.put("x", new SGIntegerSeries(0, 2, 1));
    double[] values = {1.0, 2.0, 3.0};
    SGNamedDoubleValueIndexBlock original = new SGNamedDoubleValueIndexBlock(values, seriesMap);
    SGNamedDoubleValueIndexBlock copy = (SGNamedDoubleValueIndexBlock) original.clone();
    assertNotSame(original, copy);
    assertArrayEquals(original.getValues(), copy.getValues(), 0.0);
    copy.getValues()[0] = 99.0;
    assertEquals(1.0, original.getValue(0), 0.0);
  }
}
