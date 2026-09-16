package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory.SDArray.D1;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory.SDArray.MD1;
import org.junit.jupiter.api.Test;

/** Unit tests of the single-dimensional data value history entry. */
class SGDataValueHistoryTest {

  @Test
  void d1ConstructorSetsTheFields() {
    final D1 value = new D1(1.5, "x", 3);
    assertEquals(1.5, value.getValue());
    assertEquals("x", value.getColumnType());
    assertEquals(0, value.getColumnIndex());
    assertEquals(3, value.getRowIndex());
    assertEquals(3, value.getIndex());
    assertNull(value.getPreviousValue());
  }

  @Test
  void d1ConstructorSetsThePreviousValue() {
    final D1 value = new D1(2.0, "y", 1, 9.0);
    assertEquals(2.0, value.getValue());
    final SGDataValueHistory previous = value.getPreviousValue();
    assertNotNull(previous);
    assertEquals(9.0, previous.getValue());
  }

  @Test
  void md1ConstructorUsesTheChildIndexAsTheColumnIndex() {
    final MD1 value = new MD1(3.0, "y", 1, 2);
    assertEquals(1, value.getColumnIndex());
    assertEquals(2, value.getRowIndex());
    assertEquals(1, value.getChildIndex());
    assertEquals(2, value.getIndex());
    assertNull(value.getPreviousValue());
  }

  @Test
  void equalsMatchesTheSameFields() {
    assertEquals(new D1(1.0, "x", 2), new D1(1.0, "x", 2));
    assertEquals(new D1(1.0, null, 2), new D1(1.0, null, 2));
  }

  @Test
  void equalsDistinguishesTheFields() {
    assertNotEquals(new D1(1.0, "x", 2), new D1(2.0, "x", 2));
    assertNotEquals(new D1(1.0, "x", 2), new D1(1.0, "y", 2));
    assertNotEquals(new D1(1.0, "x", 2), new D1(1.0, "x", 3));
    assertNotEquals(new D1(1.0, "x", 2), new D1(1.0, "x", 0, 0)); // different row
    assertFalse(new D1(1.0, "x", 2).equals(new Object()));
  }

  @Test
  void equalsIgnoresThePreviousValue() {
    assertEquals(new D1(1.0, "x", 2, 9.0), new D1(1.0, "x", 2));
  }

  @Test
  void equalEntriesHashEqually() {
    final D1 first = new D1(1.0, "x", 2);
    final D1 second = new D1(1.0, "x", 2);
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void netCDFEntriesWithTheSameNameHashEquallyIgnoreTheBaseFields() {
    final SGDataValueHistory.NetCDF.D1 first =
        new SGDataValueHistory.NetCDF.D1(1.0, "x", 0, 2, "var");
    final SGDataValueHistory.NetCDF.D1 second =
        new SGDataValueHistory.NetCDF.D1(9.0, "y", 3, 4, "var");
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void mdArrayEntriesWithTheSameNameHashEquallyIgnoreTheBaseFields() {
    final SGDataValueHistory.MDArray.D1 first =
        new SGDataValueHistory.MDArray.D1(1.0, "x", 0, 2, "var");
    final SGDataValueHistory.MDArray.D1 second =
        new SGDataValueHistory.MDArray.D1(9.0, "y", 3, 4, "var");
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void toStringContainsTheFields() {
    final String str = new D1(1.25, "x", 2).toString();
    assertNotNull(str);
    assertTrue(str.startsWith("["));
    assertTrue(str.endsWith("]"));
    assertTrue(str.contains("value=1.25"));
    assertTrue(str.contains("columnType=x"));
  }
}
