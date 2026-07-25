package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import org.junit.jupiter.api.Test;

class SGVXYGridDataBufferTest {

  @Test
  void constructorWithBlockLists() {
    SGIntegerSeries xSeries = new SGIntegerSeries(0, 1, 1);
    SGIntegerSeries ySeries = new SGIntegerSeries(0, 2, 1);
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, xSeries, ySeries);
    List<SGXYSimpleDoubleValueIndexBlock> fBlocks = new ArrayList<>();
    fBlocks.add(block);
    List<SGXYSimpleDoubleValueIndexBlock> sBlocks = new ArrayList<>();
    sBlocks.add(block);
    SGVXYGridDataBuffer buf =
        new SGVXYGridDataBuffer(
            new double[] {0.0, 1.0}, new double[] {0.0, 1.0, 2.0}, fBlocks, sBlocks, false);
    assertEquals(2, buf.getLengthX());
    assertEquals(3, buf.getLengthY());
    assertFalse(buf.isPolar());
  }

  @Test
  void constructorWithNullBlockListThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGVXYGridDataBuffer(
                new double[] {0.0, 1.0}, new double[] {0.0, 1.0}, null, new ArrayList<>(), false));
  }

  @Test
  void constructorWith2DArrays() {
    SGVXYGridDataBuffer buf =
        new SGVXYGridDataBuffer(
            new double[] {0.0, 1.0},
            new double[] {0.0, 1.0},
            new double[][] {{1.0, 2.0}, {3.0, 4.0}},
            new double[][] {{5.0, 6.0}, {7.0, 8.0}},
            true);
    assertTrue(buf.isPolar());
    assertNotNull(buf.getFirstComponentValueBlocks());
    assertNotNull(buf.getSecondComponentValueBlocks());
  }

  @Test
  void getFirstComponentValueBlocksReturnsDefensiveCopy() {
    SGIntegerSeries xSeries = new SGIntegerSeries(0, 1, 1);
    SGIntegerSeries ySeries = new SGIntegerSeries(0, 1, 1);
    double[] values = {1.0, 2.0, 3.0, 4.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, xSeries, ySeries);
    List<SGXYSimpleDoubleValueIndexBlock> fBlocks = new ArrayList<>();
    fBlocks.add(block);
    SGVXYGridDataBuffer buf =
        new SGVXYGridDataBuffer(
            new double[] {0.0, 1.0}, new double[] {0.0, 1.0}, fBlocks, fBlocks, false);
    List<SGXYSimpleDoubleValueIndexBlock> retrieved = buf.getFirstComponentValueBlocks();
    assertNotNull(retrieved);
  }

  @Test
  void getDataType() {
    SGVXYGridDataBuffer buf =
        new SGVXYGridDataBuffer(
            new double[] {0.0, 1.0},
            new double[] {0.0, 1.0},
            new double[][] {{1.0, 2.0}, {3.0, 4.0}},
            new double[][] {{5.0, 6.0}, {7.0, 8.0}},
            false);
    assertEquals(SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA, buf.getDataType());
  }
}
