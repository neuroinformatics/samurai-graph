package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;

class SGDateDataColumnTest {

  private SGDate[] createDateArray(double... values) {
    SGDate[] dates = new SGDate[values.length];
    for (int i = 0; i < values.length; i++) {
      dates[i] = new SGDate(values[i]);
    }
    return dates;
  }

  @Test
  void constructorWithDateArray() {
    SGDate[] dates = createDateArray(1.0, 2.0, 3.0);
    SGDateDataColumn col = new SGDateDataColumn("time", dates);
    assertEquals(3, col.getLength());
  }

  @Test
  void constructorWithDateArrayNullThrows() {
    assertThrows(
        IllegalArgumentException.class, () -> new SGDateDataColumn("time", (SGDate[]) null));
  }

  @Test
  void constructorCopiesArray() {
    SGDate[] dates = createDateArray(1.0, 2.0);
    SGDateDataColumn col = new SGDateDataColumn("time", dates);
    dates[0] = new SGDate(99.0);
    assertEquals(1.0, col.getDateArray()[0].getDateValue(), 0.0);
  }

  @Test
  void getLength() {
    SGDateDataColumn col = new SGDateDataColumn("time", createDateArray(1.0, 2.0, 3.0));
    assertEquals(3, col.getLength());
  }

  @Test
  void getValueType() {
    SGDateDataColumn col = new SGDateDataColumn("time", createDateArray(1.0));
    assertEquals(SGIDataColumnTypeConstants.VALUE_TYPE_DATE, col.getValueType());
  }

  @Test
  void getValue() {
    SGDate[] dates = createDateArray(1.0, 2.0);
    SGDateDataColumn col = new SGDateDataColumn("time", dates);
    assertEquals(dates[0], col.getValue(0));
    assertEquals(dates[1], col.getValue(1));
  }

  @Test
  void getValueWithIndexOutOfBoundsThrows() {
    SGDateDataColumn col = new SGDateDataColumn("time", createDateArray(1.0));
    assertThrows(IllegalArgumentException.class, () -> col.getValue(2));
  }

  @Test
  void getStringArray() {
    SGDateDataColumn col = new SGDateDataColumn("time", createDateArray(1.0, 2.0));
    String[] strArray = col.getStringArray();
    assertEquals(2, strArray.length);
    assertNotNull(strArray[0]);
  }

  @Test
  void getStringArrayCachesResult() {
    SGDateDataColumn col = new SGDateDataColumn("time", createDateArray(1.0));
    String[] first = col.getStringArray();
    assertNotSame(first, col.getStringArray());
  }

  @Test
  void getStringArrayWithStride() {
    SGDateDataColumn col = new SGDateDataColumn("time", createDateArray(1.0, 2.0, 3.0));
    SGIntegerSeriesSet stride = new SGIntegerSeriesSet(0, 2, 2);
    String[] strArray = col.getStringArray(stride);
    assertEquals(2, strArray.length);
  }

  @Test
  void getNumberArray() {
    SGDateDataColumn col = new SGDateDataColumn("time", createDateArray(1.0, 2.0));
    double[] numbers = col.getNumberArray();
    assertArrayEquals(new double[] {1.0, 2.0}, numbers, 0.0);
  }

  @Test
  void getNumberArrayWithStride() {
    SGDateDataColumn col = new SGDateDataColumn("time", createDateArray(1.0, 2.0, 3.0, 4.0, 5.0));
    SGIntegerSeriesSet stride = new SGIntegerSeriesSet(0, 4, 2);
    assertArrayEquals(new double[] {1.0, 3.0, 5.0}, col.getNumberArray(stride), 0.0);
  }

  @Test
  void getNumberArrayWithIndices() {
    SGDateDataColumn col = new SGDateDataColumn("time", createDateArray(1.0, 2.0, 3.0, 4.0));
    assertArrayEquals(new double[] {1.0, 3.0, 4.0}, col.getNumberArray(new int[] {0, 2, 3}), 0.0);
  }

  @Test
  void cloneClearsStringCache() {
    SGDateDataColumn col = new SGDateDataColumn("time", createDateArray(1.0, 2.0));
    col.getStringArray();
    SGDateDataColumn copy = (SGDateDataColumn) col.clone();
    assertNotNull(copy.getLength());
  }

  @Test
  void dispose() {
    SGDateDataColumn col = new SGDateDataColumn("time", createDateArray(1.0));
    col.dispose();
    assertTrue(col.isDisposed());
  }
}
