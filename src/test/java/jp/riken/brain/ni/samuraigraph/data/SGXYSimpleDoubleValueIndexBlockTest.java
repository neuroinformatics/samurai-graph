package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import org.junit.jupiter.api.Test;

class SGXYSimpleDoubleValueIndexBlockTest {

  private SGIntegerSeries colSeries = new SGIntegerSeries(0, 1, 1);
  private SGIntegerSeries rowSeries = new SGIntegerSeries(0, 2, 1);

  @Test
  void constructorWithValues() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, colSeries, rowSeries);
    assertArrayEquals(values, block.getValues(), 0.0);
    assertEquals(6, block.getLength());
  }

  @Test
  void getXSeries() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, colSeries, rowSeries);
    assertEquals(colSeries, block.getXSeries());
  }

  @Test
  void getYSeries() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, colSeries, rowSeries);
    assertEquals(rowSeries, block.getYSeries());
  }

  @Test
  void getValueByColumnAndRow() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, colSeries, rowSeries);
    assertEquals(1.0, block.getValue(0, 0), 0.0);
    assertEquals(2.0, block.getValue(1, 0), 0.0);
    assertEquals(5.0, block.getValue(0, 2), 0.0);
  }

  @Test
  void getValueWithUnknownColumnReturnsNull() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, colSeries, rowSeries);
    assertNull(block.getValue(99, 0));
  }

  @Test
  void getValueWithUnknownRowReturnsNull() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, colSeries, rowSeries);
    assertNull(block.getValue(0, 99));
  }

  @Test
  void setValueChangesValue() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, colSeries, rowSeries);
    assertTrue(block.setValue(9.9, 0, 0));
    assertEquals(9.9, block.getValue(0, 0), 0.0);
  }

  @Test
  void setValueToSameValueReturnsFalse() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, colSeries, rowSeries);
    assertFalse(block.setValue(1.0, 0, 0));
  }

  @Test
  void setValueWithUnknownColumnReturnsFalse() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, colSeries, rowSeries);
    assertFalse(block.setValue(9.9, 99, 0));
  }

  @Test
  void setValueWithUnknownRowReturnsFalse() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, colSeries, rowSeries);
    assertFalse(block.setValue(9.9, 0, 99));
  }

  @Test
  void constructorWithMismatchedLengthThrows() {
    double[] values = {1.0, 2.0};
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGXYSimpleDoubleValueIndexBlock(values, colSeries, rowSeries));
  }

  @Test
  void getDataViewerValueReturnsFromFirstBlock() {
    double[] values1 = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block1 =
        new SGXYSimpleDoubleValueIndexBlock(values1, colSeries, rowSeries);
    double[] values2 = {7.0, 8.0, 9.0, 10.0, 11.0, 12.0};
    SGXYSimpleDoubleValueIndexBlock block2 =
        new SGXYSimpleDoubleValueIndexBlock(values2, colSeries, rowSeries);

    List<SGXYSimpleDoubleValueIndexBlock> list = new ArrayList<>();
    list.add(block1);
    list.add(block2);

    assertEquals(1.0, SGXYSimpleDoubleValueIndexBlock.getDataViewerValue(list, 0, 0), 0.0);
  }

  @Test
  void getDataViewerValueReturnsFromSecondBlockWhenFirstReturnsNull() {
    double[] values1 = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGIntegerSeries colSeries2 = new SGIntegerSeries(10, 11, 1);
    SGXYSimpleDoubleValueIndexBlock block1 =
        new SGXYSimpleDoubleValueIndexBlock(values1, colSeries2, rowSeries);
    double[] values2 = {7.0, 8.0, 9.0, 10.0, 11.0, 12.0};
    SGXYSimpleDoubleValueIndexBlock block2 =
        new SGXYSimpleDoubleValueIndexBlock(values2, colSeries, rowSeries);

    List<SGXYSimpleDoubleValueIndexBlock> list = new ArrayList<>();
    list.add(block1);
    list.add(block2);

    assertEquals(7.0, SGXYSimpleDoubleValueIndexBlock.getDataViewerValue(list, 0, 0), 0.0);
  }

  @Test
  void getDataViewerValueReturnsNullWhenAllBlocksReturnNull() {
    SGIntegerSeries colSeries2 = new SGIntegerSeries(10, 11, 1);
    SGIntegerSeries rowSeries2 = new SGIntegerSeries(20, 22, 1);
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, colSeries2, rowSeries2);

    List<SGXYSimpleDoubleValueIndexBlock> list = new ArrayList<>();
    list.add(block);

    assertNull(SGXYSimpleDoubleValueIndexBlock.getDataViewerValue(list, 0, 0));
  }

  @Test
  void getDataViewerValueWithEmptyListReturnsNull() {
    List<SGXYSimpleDoubleValueIndexBlock> list = new ArrayList<>();
    assertNull(SGXYSimpleDoubleValueIndexBlock.getDataViewerValue(list, 0, 0));
  }

  @Test
  void cloneProducesIndependentCopy() {
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock original =
        new SGXYSimpleDoubleValueIndexBlock(values, colSeries, rowSeries);
    SGXYSimpleDoubleValueIndexBlock copy = (SGXYSimpleDoubleValueIndexBlock) original.clone();
    assertNotSame(original, copy);
    assertArrayEquals(original.getValues(), copy.getValues(), 0.0);
    copy.setValue(99.0, 0, 0);
    assertEquals(1.0, original.getValue(0, 0), 0.0);
  }
}
