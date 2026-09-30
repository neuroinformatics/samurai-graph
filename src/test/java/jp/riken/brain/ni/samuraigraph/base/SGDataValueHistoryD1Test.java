package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory.NetCDF.D1;
import org.junit.jupiter.api.Test;

/** Unit tests of the single-dimensional data value history entries. */
class SGDataValueHistoryD1Test {

  // --- NetCDF.D1 -----------------------------------------------------------

  @Test
  void netCdfD1ConstructorSetsTheFields() {
    final D1 value = new D1(1.5, "x", 2, 3, "var");
    assertEquals(1.5, value.getValue());
    assertEquals("x", value.getColumnType());
    assertEquals(2, value.getColumnIndex());
    assertEquals(3, value.getRowIndex());
    assertEquals(3, value.getIndex());
    assertEquals("var", value.getVarName());
    assertNull(value.getIndexDimName());
    assertNull(value.getPreviousValue());
  }

  @Test
  void netCdfD1IndexConstructorPutsTheIndexInTheRow() {
    final D1 value = new D1(1.5, "x", 3, "var");
    assertEquals(0, value.getColumnIndex());
    assertEquals(3, value.getRowIndex());
    assertEquals(3, value.getIndex());
  }

  @Test
  void netCdfD1ConstructorSetsThePreviousValue() {
    final D1 value = new D1(2.0, "y", 1, 2, "var", 9.0);
    final D1 previous = (D1) value.getPreviousValue();
    assertEquals(9.0, previous.getValue());
    assertEquals(1, previous.getColumnIndex());
    assertEquals(2, previous.getRowIndex());
    assertEquals("var", previous.getVarName());
  }

  @Test
  void netCdfD1IndexConstructorSetsThePreviousValue() {
    final D1 value = new D1(2.0, "y", 3, "var", 9.0);
    final D1 previous = (D1) value.getPreviousValue();
    assertEquals(9.0, previous.getValue());
    assertEquals(3, previous.getIndex());
  }

  @Test
  void netCdfD1SetIndexDimNamePropagatesToThePreviousValue() {
    final D1 value = new D1(2.0, "y", 1, 2, "var", 9.0);
    value.setIndexDimName("xDim");
    assertEquals("xDim", value.getIndexDimName());
    final D1 previous = (D1) value.getPreviousValue();
    assertEquals("xDim", previous.getIndexDimName());
  }

  @Test
  void netCdfD1EqualsComparesTheBaseFields() {
    final D1 value = new D1(1.0, "x", 2, 3, "var");
    assertEquals(value, new D1(1.0, "x", 2, 3, "var"));
    assertEquals(value.hashCode(), new D1(1.0, "x", 2, 3, "var").hashCode());
    assertNotEquals(value, new D1(2.0, "x", 2, 3, "var"));
    assertNotEquals(value, new D1(1.0, "y", 2, 3, "var"));
    assertNotEquals(value, new D1(1.0, "x", 0, 3, "var"));
    assertNotEquals(value, new D1(1.0, "x", 2, 4, "var"));
    assertNotEquals(value, new D1(1.0, "x", 2, 3, "other"));
    assertFalse(value.equals(new Object()));
  }

  @Test
  void netCdfD1EqualsComparesTheIndexDimName() {
    final D1 first = new D1(1.0, "x", 2, 3, "var");
    final D1 second = new D1(1.0, "x", 2, 3, "var");
    first.setIndexDimName("d1");
    second.setIndexDimName("d1");
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    second.setIndexDimName("d2");
    assertNotEquals(first, second);
    assertNotEquals(first, new D1(1.0, "x", 2, 3, "var"));
  }

  @Test
  void netCdfD1ToStringContainsTheIndexDimName() {
    final D1 value = new D1(1.25, "x", 2, 3, "var");
    value.setIndexDimName("xDim");
    final String str = value.toString();
    assertTrue(str.startsWith("["));
    assertTrue(str.endsWith("]"));
    assertTrue(str.contains("var=var"));
    assertTrue(str.contains("dim=xDim"));
  }

  // --- MDArray.D1 ----------------------------------------------------------

  @Test
  void mdArrayD1ConstructorSetsTheFields() {
    final SGDataValueHistory.MDArray.D1 value =
        new SGDataValueHistory.MDArray.D1(1.5, "x", 3, 7, "var");
    assertEquals(1.5, value.getValue());
    assertEquals(3, value.getColumnIndex());
    assertEquals(7, value.getRowIndex());
    assertEquals(7, value.getIndex());
    assertEquals("var", value.getVarName());
    assertEquals(-1, value.getDimension());
    assertNull(value.getPreviousValue());
  }

  @Test
  void mdArrayD1IndexConstructorPutsTheIndexInTheRow() {
    final SGDataValueHistory.MDArray.D1 value =
        new SGDataValueHistory.MDArray.D1(1.5, "x", 3, "var");
    assertEquals(0, value.getColumnIndex());
    assertEquals(3, value.getRowIndex());
    assertEquals(3, value.getIndex());
  }

  @Test
  void mdArrayD1ConstructorSetsThePreviousValue() {
    final SGDataValueHistory.MDArray.D1 value =
        new SGDataValueHistory.MDArray.D1(2.0, "y", 1, 2, "var", 9.0);
    final SGDataValueHistory.MDArray.D1 previous =
        (SGDataValueHistory.MDArray.D1) value.getPreviousValue();
    assertEquals(9.0, previous.getValue());
    assertEquals(1, previous.getColumnIndex());
    assertEquals(2, previous.getRowIndex());
    assertEquals("var", previous.getVarName());
  }

  @Test
  void mdArrayD1IndexConstructorSetsThePreviousValue() {
    final SGDataValueHistory.MDArray.D1 value =
        new SGDataValueHistory.MDArray.D1(2.0, "y", 3, "var", 9.0);
    final SGDataValueHistory.MDArray.D1 previous =
        (SGDataValueHistory.MDArray.D1) value.getPreviousValue();
    assertEquals(9.0, previous.getValue());
    assertEquals(3, previous.getIndex());
  }

  @Test
  void mdArrayD1SetDimensionPropagatesToThePreviousValue() {
    final SGDataValueHistory.MDArray.D1 value =
        new SGDataValueHistory.MDArray.D1(2.0, "y", 1, 2, "var", 9.0);
    value.setDimension(3);
    assertEquals(3, value.getDimension());
    final SGDataValueHistory.MDArray.D1 previous =
        (SGDataValueHistory.MDArray.D1) value.getPreviousValue();
    assertEquals(3, previous.getDimension());
  }

  @Test
  void mdArrayD1EqualsComparesTheBaseFieldsAndTheDimension() {
    final SGDataValueHistory.MDArray.D1 value =
        new SGDataValueHistory.MDArray.D1(1.0, "x", 2, 3, "var");
    assertEquals(value, new SGDataValueHistory.MDArray.D1(1.0, "x", 2, 3, "var"));
    assertNotEquals(value, new SGDataValueHistory.MDArray.D1(2.0, "x", 2, 3, "var"));
    assertNotEquals(value, new SGDataValueHistory.MDArray.D1(1.0, "y", 2, 3, "var"));
    assertNotEquals(value, new SGDataValueHistory.MDArray.D1(1.0, "x", 0, 3, "var"));
    assertNotEquals(value, new SGDataValueHistory.MDArray.D1(1.0, "x", 2, 4, "var"));
    assertNotEquals(value, new SGDataValueHistory.MDArray.D1(1.0, "x", 2, 3, "other"));
    value.setDimension(2);
    final SGDataValueHistory.MDArray.D1 sameDim =
        new SGDataValueHistory.MDArray.D1(1.0, "x", 2, 3, "var");
    sameDim.setDimension(2);
    assertEquals(value, sameDim);
    assertEquals(value.hashCode(), sameDim.hashCode());
    sameDim.setDimension(3);
    assertNotEquals(value, sameDim);
    assertFalse(value.equals(new Object()));
  }

  @Test
  void mdArrayD1ToStringContainsTheDimension() {
    final SGDataValueHistory.MDArray.D1 value =
        new SGDataValueHistory.MDArray.D1(1.25, "x", 2, 3, "var");
    value.setDimension(3);
    final String str = value.toString();
    assertTrue(str.contains("var=var"));
    assertTrue(str.contains("dim=3"));
  }
}
