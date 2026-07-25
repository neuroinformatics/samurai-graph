package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;

class SGDataColumnTest {

  private static class TestDataColumn extends SGDataColumn {
    TestDataColumn() {
      super();
    }

    TestDataColumn(String title) {
      super(title);
    }

    @Override
    public int getLength() {
      return 0;
    }

    @Override
    public String getValueType() {
      return "test";
    }

    @Override
    public Object getValue(int rowIndex) {
      return null;
    }

    @Override
    public String[] getStringArray() {
      return new String[0];
    }

    @Override
    public String[] getStringArray(SGIntegerSeriesSet stride) {
      return new String[0];
    }
  }

  @Test
  void defaultConstructor() {
    TestDataColumn col = new TestDataColumn();
    assertNull(col.getTitle());
    assertNull(col.getColumnType());
    assertFalse(col.isDisposed());
  }

  @Test
  void constructorWithTitle() {
    TestDataColumn col = new TestDataColumn("x-value");
    assertEquals("x-value", col.getTitle());
  }

  @Test
  void setAndGetTitle() {
    TestDataColumn col = new TestDataColumn();
    col.setTitle("y-value");
    assertEquals("y-value", col.getTitle());
  }

  @Test
  void setTitleNull() {
    TestDataColumn col = new TestDataColumn("x");
    col.setTitle(null);
    assertNull(col.getTitle());
  }

  @Test
  void setAndGetColumnType() {
    TestDataColumn col = new TestDataColumn();
    col.setColumnType("X_VALUE");
    assertEquals("X_VALUE", col.getColumnType());
  }

  @Test
  void dispose() {
    TestDataColumn col = new TestDataColumn("title");
    col.setColumnType("X_VALUE");
    col.dispose();
    assertTrue(col.isDisposed());
    assertNull(col.getTitle());
  }

  @Test
  void calcStrideIndicesWithNullReturnsAllIndices() {
    TestDataColumn col = new TestDataColumn();
    int[] indices = col.calcStrideIndices(null, 5);
    assertArrayEquals(new int[] {0, 1, 2, 3, 4}, indices);
  }

  @Test
  void calcStrideIndicesWithStride() {
    TestDataColumn col = new TestDataColumn();
    SGIntegerSeriesSet stride = new SGIntegerSeriesSet(0, 4, 2);
    int[] indices = col.calcStrideIndices(stride, 5);
    assertArrayEquals(new int[] {0, 2, 4}, indices);
  }

  @Test
  void calcStrideIndicesClampsToLength() {
    TestDataColumn col = new TestDataColumn();
    SGIntegerSeriesSet stride = new SGIntegerSeriesSet(0, 10, 1);
    int[] indices = col.calcStrideIndices(stride, 3);
    assertArrayEquals(new int[] {0, 1, 2}, indices);
  }
}
