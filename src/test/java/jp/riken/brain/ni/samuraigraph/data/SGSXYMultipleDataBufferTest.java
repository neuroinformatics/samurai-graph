package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jp.riken.brain.ni.samuraigraph.base.SGDate;
import org.junit.jupiter.api.Test;

class SGSXYMultipleDataBufferTest {

  @Test
  void constructorWithSingleAndMultiple() {
    double[] single = {1.0, 2.0, 3.0};
    double[][] multiple = {{4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
    SGSXYMultipleDataBuffer buf = new SGSXYMultipleDataBuffer(single, multiple, true);
    assertEquals(3, buf.getLength());
    assertEquals(2, buf.getMultiplicity());
  }

  @Test
  void constructorWithSingleAndMultipleNullThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGSXYMultipleDataBuffer(null, new double[][] {{1.0}}, true));
  }

  @Test
  void constructorWithSingleAndMultipleMismatchedLengthThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGSXYMultipleDataBuffer(new double[] {1.0, 2.0}, new double[][] {{3.0}}, true));
  }

  @Test
  void constructorWithNullInnerArrayThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGSXYMultipleDataBuffer(new double[] {1.0, 2.0}, new double[][] {null}, true));
  }

  @Test
  void constructorWithXYMatrices() {
    double[][] x = {{1.0, 2.0}, {3.0, 4.0}};
    double[][] y = {{5.0, 6.0}, {7.0, 8.0}};
    SGSXYMultipleDataBuffer buf = new SGSXYMultipleDataBuffer(x, y);
    assertEquals(2, buf.getLength());
    assertEquals(2, buf.getMultiplicity());
  }

  @Test
  void constructorWithXYNullThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGSXYMultipleDataBuffer(null, new double[][] {{1.0}}));
  }

  @Test
  void constructorWithXYEmptyArrayThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGSXYMultipleDataBuffer(new double[][] {}, new double[][] {{1.0}}));
  }

  @Test
  void constructorWithXYNullInnerArrayThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGSXYMultipleDataBuffer(
                new double[][] {{1.0, 2.0}, null}, new double[][] {{3.0, 4.0}, {5.0, 6.0}}));
  }

  @Test
  void constructorWithXYMismatchedLengthThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGSXYMultipleDataBuffer(new double[][] {{1.0, 2.0}}, new double[][] {{3.0}}));
  }

  @Test
  void constructorWithSingleMultipleAndOptionalParams() {
    double[] single = {1.0, 2.0, 3.0};
    double[][] multiple = {{4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
    double[][] lower = {{0.1, 0.2, 0.3}, {0.4, 0.5, 0.6}};
    double[][] upper = {{0.7, 0.8, 0.9}, {1.0, 1.1, 1.2}};
    Boolean[] sameErrorFlags = {true, false};
    String[][] tickLabels = {{"a", "b", "c"}, {"d", "e", "f"}};
    SGSXYMultipleDataBuffer buf =
        new SGSXYMultipleDataBuffer(
            single,
            multiple,
            true,
            true,
            new SGDate[] {new SGDate(1.0), new SGDate(2.0), new SGDate(3.0)},
            lower,
            upper,
            sameErrorFlags,
            tickLabels);
    assertEquals(3, buf.getLength());
    assertEquals(true, buf.getDateFlag());
    assertNotNull(buf.getDateArray());
    assertArrayEquals(new double[][] {{0.1, 0.2, 0.3}, {0.4, 0.5, 0.6}}, buf.getLowerErrorValues());
    assertArrayEquals(new double[][] {{0.7, 0.8, 0.9}, {1.0, 1.1, 1.2}}, buf.getUpperErrorValues());
    assertArrayEquals(new String[][] {{"a", "b", "c"}, {"d", "e", "f"}}, buf.getTickLabels());
  }

  @Test
  void getXAndYValues() {
    double[][] x = {{1.0, 2.0}, {3.0, 4.0}};
    double[][] y = {{5.0, 6.0}, {7.0, 8.0}};
    SGSXYMultipleDataBuffer buf = new SGSXYMultipleDataBuffer(x, y);
    assertArrayEquals(x, buf.getXValues());
    assertArrayEquals(y, buf.getYValues());
  }

  @Test
  void getSXYDataBufferArray() {
    double[][] x = {{1.0, 2.0}, {3.0, 4.0}};
    double[][] y = {{5.0, 6.0}, {7.0, 8.0}};
    SGSXYMultipleDataBuffer buf = new SGSXYMultipleDataBuffer(x, y);
    SGSXYDataBuffer[] buffers = buf.getSXYDataBufferArray();
    assertEquals(2, buffers.length);
    assertArrayEquals(new double[] {1.0, 2.0}, buffers[0].getXValues(), 0.0);
    assertArrayEquals(new double[] {5.0, 6.0}, buffers[0].getYValues(), 0.0);
    assertArrayEquals(new double[] {3.0, 4.0}, buffers[1].getXValues(), 0.0);
    assertArrayEquals(new double[] {7.0, 8.0}, buffers[1].getYValues(), 0.0);
  }

  @Test
  void pickUpWithValidIndices() {
    double[][] x = {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}};
    double[][] y = {{7.0, 8.0}, {9.0, 10.0}, {11.0, 12.0}};
    SGSXYMultipleDataBuffer buf = new SGSXYMultipleDataBuffer(x, y);
    SGSXYMultipleDataBuffer picked = buf.pickUp(new int[] {0, 2});
    assertEquals(2, picked.getMultiplicity());
    assertArrayEquals(new double[] {1.0, 2.0}, picked.getXValues()[0], 0.0);
    assertArrayEquals(new double[] {5.0, 6.0}, picked.getXValues()[1], 0.0);
  }

  @Test
  void pickUpWithNullThrows() {
    SGSXYMultipleDataBuffer buf =
        new SGSXYMultipleDataBuffer(new double[][] {{1.0}}, new double[][] {{2.0}});
    assertThrows(IllegalArgumentException.class, () -> buf.pickUp(null));
  }

  @Test
  void pickUpWithEmptyIndicesThrows() {
    SGSXYMultipleDataBuffer buf =
        new SGSXYMultipleDataBuffer(new double[][] {{1.0}}, new double[][] {{2.0}});
    assertThrows(IllegalArgumentException.class, () -> buf.pickUp(new int[] {}));
  }

  @Test
  void pickUpWithOutOfBoundsThrows() {
    SGSXYMultipleDataBuffer buf =
        new SGSXYMultipleDataBuffer(new double[][] {{1.0}}, new double[][] {{2.0}});
    assertThrows(IllegalArgumentException.class, () -> buf.pickUp(new int[] {5}));
  }

  @Test
  void pickUpWithOverlappingIndicesThrows() {
    SGSXYMultipleDataBuffer buf =
        new SGSXYMultipleDataBuffer(new double[][] {{1.0}, {2.0}}, new double[][] {{3.0}, {4.0}});
    assertThrows(IllegalArgumentException.class, () -> buf.pickUp(new int[] {0, 0}));
  }

  @Test
  void getDataType() {
    SGSXYMultipleDataBuffer buf =
        new SGSXYMultipleDataBuffer(new double[][] {{1.0}}, new double[][] {{2.0}});
    assertEquals(SGDataTypeConstants.SXY_MULTIPLE_VIRTUAL_MDARRAY_DATA, buf.getDataType());
  }

  @Test
  void cloneProducesIndependentCopy() {
    double[][] x = {{1.0, 2.0}, {3.0, 4.0}};
    double[][] y = {{5.0, 6.0}, {7.0, 8.0}};
    SGSXYMultipleDataBuffer original = new SGSXYMultipleDataBuffer(x, y);
    SGSXYMultipleDataBuffer copy = (SGSXYMultipleDataBuffer) original.clone();
    assertArrayEquals(original.getXValues(), copy.getXValues());
    assertArrayEquals(original.getYValues(), copy.getYValues());
  }
}
