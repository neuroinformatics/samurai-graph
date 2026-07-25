package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;

class SGNumberDataColumnTest {

  @Test
  void constructorWithDoubleArray() {
    double[] values = {1.0, 2.0, 3.0};
    SGNumberDataColumn col = new SGNumberDataColumn("x", values);
    assertEquals(3, col.getLength());
    assertArrayEquals(values, col.getNumberArray(), 0.0);
  }

  @Test
  void constructorWithDoubleArrayCopiesInput() {
    double[] values = {1.0, 2.0};
    SGNumberDataColumn col = new SGNumberDataColumn("x", values);
    values[0] = 99.0;
    assertArrayEquals(new double[] {1.0, 2.0}, col.getNumberArray(), 0.0);
  }

  @Test
  void constructorWithDoubleArrayNullThrows() {
    assertThrows(
        IllegalArgumentException.class, () -> new SGNumberDataColumn("x", (double[]) null));
  }

  @Test
  void constructorWithStringArray() {
    String[] strValues = {"1.5", "2.5", "3.5"};
    SGNumberDataColumn col = new SGNumberDataColumn("x", strValues);
    assertArrayEquals(new double[] {1.5, 2.5, 3.5}, col.getNumberArray(), 0.0);
  }

  @Test
  void constructorWithStringArrayNullThrows() {
    assertThrows(
        IllegalArgumentException.class, () -> new SGNumberDataColumn("x", (String[]) null));
  }

  @Test
  void constructorWithStringArrayInvalidFormatThrows() {
    String[] strValues = {"1.0", "not-a-number"};
    assertThrows(IllegalArgumentException.class, () -> new SGNumberDataColumn("x", strValues));
  }

  @Test
  void getLength() {
    SGNumberDataColumn col = new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0});
    assertEquals(3, col.getLength());
  }

  @Test
  void getValueType() {
    SGNumberDataColumn col = new SGNumberDataColumn("x", new double[] {1.0});
    assertEquals(SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, col.getValueType());
  }

  @Test
  void getValue() {
    SGNumberDataColumn col = new SGNumberDataColumn("x", new double[] {1.0, 2.0});
    assertEquals(1.0, (Double) col.getValue(0), 0.0);
    assertEquals(2.0, (Double) col.getValue(1), 0.0);
  }

  @Test
  void getValueWithIndexOutOfBoundsThrows() {
    SGNumberDataColumn col = new SGNumberDataColumn("x", new double[] {1.0});
    assertThrows(IllegalArgumentException.class, () -> col.getValue(2));
  }

  @Test
  void getNumberArrayReturnsDefensiveCopy() {
    SGNumberDataColumn col = new SGNumberDataColumn("x", new double[] {1.0, 2.0});
    double[] retrieved = col.getNumberArray();
    retrieved[0] = 99.0;
    assertArrayEquals(new double[] {1.0, 2.0}, col.getNumberArray(), 0.0);
  }

  @Test
  void getNumberArrayWithStride() {
    SGNumberDataColumn col = new SGNumberDataColumn("x", new double[] {0.0, 1.0, 2.0, 3.0, 4.0});
    SGIntegerSeriesSet stride = new SGIntegerSeriesSet(0, 4, 2);
    assertArrayEquals(new double[] {0.0, 2.0, 4.0}, col.getNumberArray(stride), 0.0);
  }

  @Test
  void getNumberArrayWithIndices() {
    SGNumberDataColumn col = new SGNumberDataColumn("x", new double[] {0.0, 1.0, 2.0, 3.0});
    assertArrayEquals(new double[] {0.0, 2.0, 3.0}, col.getNumberArray(new int[] {0, 2, 3}), 0.0);
  }

  @Test
  void getStringArray() {
    SGNumberDataColumn col = new SGNumberDataColumn("x", new double[] {1.0, 2.0});
    String[] strArray = col.getStringArray();
    assertEquals(2, strArray.length);
  }

  @Test
  void getStringArrayWithStride() {
    SGNumberDataColumn col = new SGNumberDataColumn("x", new double[] {0.0, 1.0, 2.0, 3.0, 4.0});
    SGIntegerSeriesSet stride = new SGIntegerSeriesSet(0, 4, 2);
    String[] strArray = col.getStringArray(stride);
    assertEquals(3, strArray.length);
  }

  @Test
  void dispose() {
    SGNumberDataColumn col = new SGNumberDataColumn("x", new double[] {1.0});
    col.dispose();
    assertTrue(col.isDisposed());
  }

  @Test
  void getValueTypeConstant() {
    SGNumberDataColumn col = new SGNumberDataColumn("x", new double[] {1.0});
    assertEquals(SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, col.getValueType());
  }

  @Test
  void getArrayPackagePrivate() {
    double[] values = {1.0, 2.0};
    SGNumberDataColumn col = new SGNumberDataColumn("x", values);
    assertNotSame(values, col.getArray());
    assertArrayEquals(values, col.getArray(), 0.0);
  }
}
