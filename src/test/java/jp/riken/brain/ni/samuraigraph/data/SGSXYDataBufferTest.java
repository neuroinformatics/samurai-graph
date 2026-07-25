package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jp.riken.brain.ni.samuraigraph.base.SGDate;
import org.junit.jupiter.api.Test;

class SGSXYDataBufferTest {

  @Test
  void constructorWithXAndY() {
    SGSXYDataBuffer buf = new SGSXYDataBuffer(new double[] {1.0, 2.0}, new double[] {3.0, 4.0});
    assertEquals(2, buf.getLength());
    assertArrayEquals(new double[] {1.0, 2.0}, buf.getXValues(), 0.0);
    assertArrayEquals(new double[] {3.0, 4.0}, buf.getYValues(), 0.0);
    assertNull(buf.getLowerErrorValues());
    assertNull(buf.getUpperErrorValues());
    assertNull(buf.getTickLabels());
  }

  @Test
  void constructorWithNullXThrows() {
    assertThrows(
        IllegalArgumentException.class, () -> new SGSXYDataBuffer(null, new double[] {1.0}));
  }

  @Test
  void constructorWithNullYThrows() {
    assertThrows(
        IllegalArgumentException.class, () -> new SGSXYDataBuffer(new double[] {1.0}, null));
  }

  @Test
  void constructorWithMismatchedLengthThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGSXYDataBuffer(new double[] {1.0, 2.0}, new double[] {3.0}));
  }

  @Test
  void constructorWithFullArguments() {
    SGDate[] dates = {new SGDate(1.0), new SGDate(2.0)};
    SGSXYDataBuffer buf =
        new SGSXYDataBuffer(
            new double[] {1.0, 2.0},
            new double[] {3.0, 4.0},
            true,
            dates,
            false,
            new double[] {0.1, 0.2},
            new double[] {0.3, 0.4},
            true,
            new String[] {"a", "b"});
    assertEquals(2, buf.getLength());
    assertArrayEquals(new double[] {0.1, 0.2}, buf.getLowerErrorValues(), 0.0);
    assertArrayEquals(new double[] {0.3, 0.4}, buf.getUpperErrorValues(), 0.0);
    assertArrayEquals(new String[] {"a", "b"}, buf.getTickLabels());
    assertEquals(true, buf.getXDateFlag());
    assertNotNull(buf.getDateArray());
  }

  @Test
  void getDataType() {
    SGSXYDataBuffer buf = new SGSXYDataBuffer(new double[] {1.0, 2.0}, new double[] {3.0, 4.0});
    assertEquals(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA, buf.getDataType());
  }

  @Test
  void getMultipleDataBuffer() {
    SGSXYDataBuffer buf = new SGSXYDataBuffer(new double[] {1.0, 2.0}, new double[] {3.0, 4.0});
    SGSXYMultipleDataBuffer multiple = buf.getMultipleDataBuffer();
    assertNotNull(multiple);
    assertEquals(1, multiple.getMultiplicity());
  }

  @Test
  void hasSameErrorVariableFlagReturnsNullWhenNoErrors() {
    SGSXYDataBuffer buf = new SGSXYDataBuffer(new double[] {1.0, 2.0}, new double[] {3.0, 4.0});
    assertNull(buf.hasSameErrorVariableFlag());
  }

  @Test
  void constructorCopiesXValues() {
    double[] x = {1.0, 2.0};
    SGSXYDataBuffer buf = new SGSXYDataBuffer(x, new double[] {3.0, 4.0});
    x[0] = 99.0;
    assertArrayEquals(new double[] {1.0, 2.0}, buf.getXValues(), 0.0);
  }
}
