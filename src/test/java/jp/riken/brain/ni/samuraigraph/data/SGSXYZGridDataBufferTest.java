package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import org.junit.jupiter.api.Test;

class SGSXYZGridDataBufferTest {

  @Test
  void constructorWithBlockList() {
    SGIntegerSeries xSeries = new SGIntegerSeries(0, 1, 1);
    SGIntegerSeries ySeries = new SGIntegerSeries(0, 2, 1);
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, xSeries, ySeries);
    List<SGXYSimpleDoubleValueIndexBlock> blocks = new ArrayList<>();
    blocks.add(block);
    SGSXYZGridDataBuffer buf =
        new SGSXYZGridDataBuffer(new double[] {0.0, 1.0}, new double[] {0.0, 1.0, 2.0}, blocks);
    assertEquals(2, buf.getLengthX());
    assertEquals(3, buf.getLengthY());
    assertArrayEquals(new double[] {0.0, 1.0}, buf.getXValues(), 0.0);
    assertArrayEquals(new double[] {0.0, 1.0, 2.0}, buf.getYValues(), 0.0);
  }

  @Test
  void constructorWithNullBlocksThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGSXYZGridDataBuffer(
                new double[] {0.0, 1.0},
                new double[] {0.0, 1.0},
                (List<SGXYSimpleDoubleValueIndexBlock>) null));
  }

  @Test
  void constructorWith2DArray() {
    SGSXYZGridDataBuffer buf =
        new SGSXYZGridDataBuffer(
            new double[] {0.0, 1.0},
            new double[] {0.0, 1.0, 2.0},
            new double[][] {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}});
    assertEquals(2, buf.getLengthX());
    assertEquals(3, buf.getLengthY());
    assertNotNull(buf.getZValueBlocks());
    assertEquals(1, buf.getZValueBlocks().size());
  }

  @Test
  void getZValueBlocksReturnsDefensiveCopy() {
    SGIntegerSeries xSeries = new SGIntegerSeries(0, 1, 1);
    SGIntegerSeries ySeries = new SGIntegerSeries(0, 2, 1);
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, xSeries, ySeries);
    List<SGXYSimpleDoubleValueIndexBlock> blocks = new ArrayList<>();
    blocks.add(block);
    SGSXYZGridDataBuffer buf =
        new SGSXYZGridDataBuffer(new double[] {0.0, 1.0}, new double[] {0.0, 1.0, 2.0}, blocks);
    List<SGXYSimpleDoubleValueIndexBlock> retrieved = buf.getZValueBlocks();
    assertNotNull(retrieved);
    assertEquals(1, retrieved.size());
  }

  @Test
  void getDataType() {
    SGSXYZGridDataBuffer buf =
        new SGSXYZGridDataBuffer(
            new double[] {0.0, 1.0},
            new double[] {0.0, 1.0},
            new double[][] {{1.0, 2.0}, {3.0, 4.0}});
    assertEquals(SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA, buf.getDataType());
  }
}
