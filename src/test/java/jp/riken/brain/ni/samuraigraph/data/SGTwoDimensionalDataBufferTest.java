package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import org.junit.jupiter.api.Test;

class SGTwoDimensionalDataBufferTest {

  private static class TestTwoDimBuffer extends SGTwoDimensionalDataBuffer {
    TestTwoDimBuffer(double[] xValues, double[] yValues) {
      super(xValues, yValues);
    }

    @Override
    public String getGridTypeKey() {
      return "test";
    }

    @Override
    public String getDataType() {
      return "test";
    }
  }

  @Test
  void constructorWithValidValues() {
    TestTwoDimBuffer buf =
        new TestTwoDimBuffer(new double[] {0.0, 1.0}, new double[] {0.0, 1.0, 2.0});
    assertEquals(2, buf.getLengthX());
    assertEquals(3, buf.getLengthY());
  }

  @Test
  void constructorWithNullXThrows() {
    assertThrows(
        IllegalArgumentException.class, () -> new TestTwoDimBuffer(null, new double[] {1.0}));
  }

  @Test
  void constructorWithNullYThrows() {
    assertThrows(
        IllegalArgumentException.class, () -> new TestTwoDimBuffer(new double[] {1.0}, null));
  }

  @Test
  void isGridTypeReturnsTrue() {
    TestTwoDimBuffer buf =
        new TestTwoDimBuffer(new double[] {0.0, 1.0}, new double[] {0.0, 1.0, 2.0});
    assertTrue(buf.isGridType());
  }

  @Test
  void checkTwoDimensionalArrayWithValidValues() {
    double[] xValues = {0.0, 1.0};
    double[] yValues = {0.0, 1.0, 2.0};
    double[][] zValues = {{0.0, 1.0}, {2.0, 3.0}, {4.0, 5.0}};
    TestTwoDimBuffer buf = new TestTwoDimBuffer(xValues, yValues);
    buf.checkTwoDimensionalArray(xValues, yValues, zValues, "zValues");
  }

  @Test
  void checkTwoDimensionalArrayWithNullRowThrows() {
    TestTwoDimBuffer buf = new TestTwoDimBuffer(new double[] {0.0, 1.0}, new double[] {0.0, 1.0});
    assertThrows(
        IllegalArgumentException.class,
        () ->
            buf.checkTwoDimensionalArray(
                new double[] {0.0, 1.0},
                new double[] {0.0, 1.0},
                new double[][] {{1.0, 2.0}, null},
                "zValues"));
  }

  @Test
  void checkTwoDimensionalArrayWithMismatchedRowCountThrows() {
    TestTwoDimBuffer buf =
        new TestTwoDimBuffer(new double[] {0.0, 1.0}, new double[] {0.0, 1.0, 2.0});
    assertThrows(
        IllegalArgumentException.class,
        () ->
            buf.checkTwoDimensionalArray(
                new double[] {0.0, 1.0},
                new double[] {0.0, 1.0, 2.0},
                new double[][] {{0.0, 1.0}},
                "zValues"));
  }

  @Test
  void checkTwoDimensionalArrayWithMismatchedColumnCountThrows() {
    TestTwoDimBuffer buf = new TestTwoDimBuffer(new double[] {0.0, 1.0}, new double[] {0.0, 1.0});
    assertThrows(
        IllegalArgumentException.class,
        () ->
            buf.checkTwoDimensionalArray(
                new double[] {0.0, 1.0},
                new double[] {0.0, 1.0},
                new double[][] {{0.0, 1.0, 2.0}, {3.0, 4.0, 5.0}},
                "zValues"));
  }

  @Test
  void checkBlocksWithValidBlocks() {
    SGIntegerSeries xSeries = new SGIntegerSeries(0, 1, 1);
    SGIntegerSeries ySeries = new SGIntegerSeries(0, 2, 1);
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, xSeries, ySeries);
    List<SGXYSimpleDoubleValueIndexBlock> blocks = new ArrayList<>();
    blocks.add(block);
    TestTwoDimBuffer buf =
        new TestTwoDimBuffer(new double[] {0.0, 1.0}, new double[] {0.0, 1.0, 2.0});
    buf.checkBlocks(new double[] {0.0, 1.0}, new double[] {0.0, 1.0, 2.0}, blocks, "blocks");
  }

  @Test
  void checkBlocksWithNullBlockThrows() {
    List<SGXYSimpleDoubleValueIndexBlock> blocks = new ArrayList<>();
    blocks.add(null);
    TestTwoDimBuffer buf = new TestTwoDimBuffer(new double[] {0.0, 1.0}, new double[] {0.0, 1.0});
    assertThrows(
        IllegalArgumentException.class,
        () -> buf.checkBlocks(new double[] {0.0, 1.0}, new double[] {0.0, 1.0}, blocks, "blocks"));
  }

  @Test
  void checkBlocksWithOutOfBoundsIndexThrows() {
    SGIntegerSeries xSeries = new SGIntegerSeries(5, 5, 1);
    SGIntegerSeries ySeries = new SGIntegerSeries(0, 0, 1);
    double[] values = {1.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, xSeries, ySeries);
    List<SGXYSimpleDoubleValueIndexBlock> blocks = new ArrayList<>();
    blocks.add(block);
    TestTwoDimBuffer buf = new TestTwoDimBuffer(new double[] {0.0, 1.0}, new double[] {0.0, 1.0});
    assertThrows(
        IllegalArgumentException.class,
        () -> buf.checkBlocks(new double[] {0.0, 1.0}, new double[] {0.0, 1.0}, blocks, "blocks"));
  }

  @Test
  void createBlockFlattens2DArray() {
    TestTwoDimBuffer buf =
        new TestTwoDimBuffer(new double[] {0.0, 1.0}, new double[] {0.0, 1.0, 2.0});
    double[][] values = {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}};
    SGXYSimpleDoubleValueIndexBlock block =
        buf.createBlock(new double[] {0.0, 1.0}, new double[] {0.0, 1.0, 2.0}, values);
    assertArrayEquals(new double[] {1.0, 2.0, 3.0, 4.0, 5.0, 6.0}, block.getValues(), 0.0);
  }

  @Test
  void copyBlocksReturnsIndependentCopy() {
    SGIntegerSeries xSeries = new SGIntegerSeries(0, 1, 1);
    SGIntegerSeries ySeries = new SGIntegerSeries(0, 2, 1);
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(values, xSeries, ySeries);
    List<SGXYSimpleDoubleValueIndexBlock> original = new ArrayList<>();
    original.add(block);
    TestTwoDimBuffer buf =
        new TestTwoDimBuffer(new double[] {0.0, 1.0}, new double[] {0.0, 1.0, 2.0});
    List<SGXYSimpleDoubleValueIndexBlock> copy = buf.copyBlocks(original);
    assertEquals(original.size(), copy.size());
    assertArrayEquals(original.get(0).getValues(), copy.get(0).getValues(), 0.0);
  }
}
