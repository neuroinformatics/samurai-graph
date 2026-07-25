package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SGSimpleDoubleValueIndexBlockTest {

  private SGIntegerSeries series1 = new SGIntegerSeries(0, 2, 1);
  private SGIntegerSeries series2 = new SGIntegerSeries(0, 1, 1);

  @Test
  void constructorWithValues() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGSimpleDoubleValueIndexBlock block =
        new SGSimpleDoubleValueIndexBlock(values, new SGIntegerSeries[] {series1, series2});
    assertArrayEquals(values, block.getValues(), 0.0);
    assertEquals(6, block.getLength());
  }

  @Test
  void constructorWithSingleSeries() {
    double[] values = {1.0, 2.0, 3.0};
    SGSimpleDoubleValueIndexBlock block =
        new SGSimpleDoubleValueIndexBlock(values, new SGIntegerSeries[] {series1});
    assertArrayEquals(values, block.getValues(), 0.0);
    assertEquals(3, block.getLength());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 5})
  void getValueByIndex(int index) {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGSimpleDoubleValueIndexBlock block =
        new SGSimpleDoubleValueIndexBlock(values, new SGIntegerSeries[] {series1, series2});
    assertEquals(values[index], block.getValue(index), 0.0);
  }

  @Test
  void getLength() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGSimpleDoubleValueIndexBlock block =
        new SGSimpleDoubleValueIndexBlock(values, new SGIntegerSeries[] {series1, series2});
    assertEquals(6, block.getLength());
  }

  @Test
  void getValuesReturnsDefensiveCopy() {
    double[] values = {1.0, 2.0, 3.0};
    SGSimpleDoubleValueIndexBlock block =
        new SGSimpleDoubleValueIndexBlock(values, new SGIntegerSeries[] {series1});
    double[] retrieved = block.getValues();
    retrieved[0] = 99.0;
    assertEquals(1.0, block.getValue(0), 0.0);
  }

  @Test
  void constructorCopiesArray() {
    double[] values = {1.0, 2.0, 3.0};
    SGSimpleDoubleValueIndexBlock block =
        new SGSimpleDoubleValueIndexBlock(values, new SGIntegerSeries[] {series1});
    values[0] = 99.0;
    assertEquals(1.0, block.getValue(0), 0.0);
  }

  @Test
  void constructorWithMismatchedLengthThrows() {
    double[] values = {1.0, 2.0};
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGSimpleDoubleValueIndexBlock(values, new SGIntegerSeries[] {series1, series2}));
  }

  @Test
  void cloneProducesIndependentCopy() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGSimpleDoubleValueIndexBlock original =
        new SGSimpleDoubleValueIndexBlock(values, new SGIntegerSeries[] {series1, series2});
    SGSimpleDoubleValueIndexBlock copy = (SGSimpleDoubleValueIndexBlock) original.clone();
    assertNotSame(original, copy);
    assertArrayEquals(original.getValues(), copy.getValues(), 0.0);
    copy.getValues()[0] = 99.0;
    assertEquals(1.0, original.getValue(0), 0.0);
  }
}
