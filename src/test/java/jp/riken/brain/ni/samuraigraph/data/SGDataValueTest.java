package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDataValue} inner classes. */
class SGDataValueTest {

  @Test
  void valueConstructorWithNumberOnly() {
    SGDataValue.Value val = new SGDataValue.Value(3.14);
    assertEquals(3.14, val.number, 0.0);
    assertFalse(val.missing);
  }

  @Test
  void valueConstructorWithMissing() {
    SGDataValue.Value val = new SGDataValue.Value(0.0, true);
    assertEquals(0.0, val.number, 0.0);
    assertTrue(val.missing);
  }

  @Test
  void valueToString() {
    SGDataValue.Value val = new SGDataValue.Value(42.0);
    assertEquals("42.0", val.toString());
  }

  @Test
  void sxysingleDataValue() {
    SGDataValue.SXYSingleDataValue val = new SGDataValue.SXYSingleDataValue();
    val.xValue = 1.0;
    val.yValue = 2.0;
    assertEquals(1.0, val.xValue, 0.0);
    assertEquals(2.0, val.yValue, 0.0);
  }

  @Test
  void sxydoubleDataValue() {
    SGDataValue.SXYDoubleDataValue val = new SGDataValue.SXYDoubleDataValue();
    val.xValue0 = 0.0;
    val.yValue0 = 1.0;
    val.xValue1 = 2.0;
    val.yValue1 = 3.0;
    assertEquals(0.0, val.xValue0, 0.0);
    assertEquals(3.0, val.yValue1, 0.0);
  }

  @Test
  void sxyzDataValue() {
    SGDataValue.SXYZDataValue val = new SGDataValue.SXYZDataValue();
    val.xValue = 1.0;
    val.yValue = 2.0;
    val.zValue = new SGDataValue.Value(3.0, false);
    assertEquals(1.0, val.xValue, 0.0);
    assertEquals(2.0, val.yValue, 0.0);
    assertEquals(3.0, val.zValue.number, 0.0);
    assertFalse(val.zValue.missing);
  }

  @Test
  void vxyDataValue() {
    SGDataValue.VXYDataValue val = new SGDataValue.VXYDataValue();
    val.xValue = 1.0;
    val.yValue = 2.0;
    val.fValue = new SGDataValue.Value(3.0);
    val.sValue = new SGDataValue.Value(4.0, true);
    assertEquals(1.0, val.xValue, 0.0);
    assertTrue(val.sValue.missing);
  }
}
