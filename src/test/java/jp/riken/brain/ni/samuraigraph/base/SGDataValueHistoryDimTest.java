package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory.NetCDF.D2;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory.NetCDF.MD1;
import org.junit.jupiter.api.Test;

/** Unit tests of the dimension-carrying data value history entries. */
class SGDataValueHistoryDimTest {

  // --- NetCDF.D2 ---------------------------------------------------------

  @Test
  void netCdfD2ConstructorSetsTheFields() {
    final D2 value = new D2(1.5, "x", 3, 7, "var");
    assertEquals(1.5, value.getValue());
    assertEquals("x", value.getColumnType());
    assertEquals(3, value.getColumnIndex());
    assertEquals(7, value.getRowIndex());
    assertEquals(3, value.getXIndex());
    assertEquals(7, value.getYIndex());
    assertEquals("var", value.getVarName());
    assertNull(value.getXDimName());
    assertNull(value.getYDimName());
    assertNull(value.getPreviousValue());
  }

  @Test
  void netCdfD2ConstructorSetsThePreviousValue() {
    final D2 value = new D2(2.0, "y", 1, 2, "var", 9.0);
    final D2 previous = (D2) value.getPreviousValue();
    assertEquals(9.0, previous.getValue());
  }

  @Test
  void netCdfD2SetXYDimNamePropagatesToThePreviousValue() {
    final D2 value = new D2(2.0, "y", 1, 2, "var", 9.0);
    value.setXYDimName("xDim", "yDim");
    assertEquals("xDim", value.getXDimName());
    assertEquals("yDim", value.getYDimName());
    final D2 previous = (D2) value.getPreviousValue();
    assertEquals("xDim", previous.getXDimName());
    assertEquals("yDim", previous.getYDimName());
  }

  @Test
  void netCdfD2EqualsComparesTheDimNames() {
    final D2 first = new D2(1.0, "x", 2, 3, "var");
    final D2 second = new D2(1.0, "x", 2, 3, "var");
    assertEquals(first, second);
    first.setXYDimName("xDim", "yDim");
    second.setXYDimName("xDim", "yDim");
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    second.setXYDimName("xDim", "other");
    assertNotEquals(first, second);
    assertNotEquals(first, new D2(1.0, "x", 2, 3, "var", 5.0));
    assertFalse(first.equals(new Object()));
  }

  @Test
  void netCdfD2ToStringContainsTheDimNames() {
    final D2 value = new D2(1.25, "x", 2, 3, "var");
    value.setXYDimName("xDim", "yDim");
    final String str = value.toString();
    assertTrue(str.startsWith("["));
    assertTrue(str.endsWith("]"));
    assertTrue(str.contains("var=var"));
    assertTrue(str.contains("dim=(xDim,yDim)"));
  }

  // --- NetCDF.MD1 --------------------------------------------------------

  @Test
  void netCdfMD1ConstructorAndSettersSetTheFields() {
    final MD1 value = new MD1(3.0, "y", 1, 2, "var");
    assertEquals(1, value.getChildIndex());
    assertEquals(2, value.getIndex());
    assertEquals("var", value.getVarName());
    assertNull(value.getDimName());
    assertNull(value.getPickUpDimName());
    assertEquals(-1, value.getPickUpDimIndex());
    value.setDimInfo("time");
    value.setPickUpInfo("xDim", 4);
    assertEquals("time", value.getDimName());
    assertEquals("xDim", value.getPickUpDimName());
    assertEquals(4, value.getPickUpDimIndex());
  }

  @Test
  void netCdfMD1ConstructorSetsThePreviousValue() {
    final MD1 value = new MD1(3.0, "y", 1, 2, "var", 9.0);
    final MD1 previous = (MD1) value.getPreviousValue();
    assertEquals(9.0, previous.getValue());
  }

  @Test
  void netCdfMD1EqualsComparesTheBaseFields() {
    final MD1 first = new MD1(1.0, "x", 2, 3, "var");
    final MD1 second = new MD1(1.0, "x", 2, 3, "var");
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    first.setDimInfo("d1");
    first.setPickUpInfo("p1", 1);
    second.setDimInfo("d1");
    second.setPickUpInfo("p1", 1);
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void netCdfMD1EqualsDistinguishesTheFields() {
    final MD1 value = new MD1(1.0, "x", 2, 3, "var");
    value.setDimInfo("d1");
    value.setPickUpInfo("p1", 1);
    // the base value/columnType/varName fields are compared
    assertNotEquals(value, new MD1(2.0, "x", 2, 3, "var"));
    assertNotEquals(value, new MD1(1.0, "y", 2, 3, "var"));
    assertNotEquals(value, new MD1(1.0, "x", 2, 3, "other"));
    // the dimension info fields are compared
    final MD1 otherDim = new MD1(1.0, "x", 2, 3, "var");
    otherDim.setDimInfo("d2");
    otherDim.setPickUpInfo("p1", 1);
    assertNotEquals(value, otherDim);
    final MD1 otherPickUp = new MD1(1.0, "x", 2, 3, "var");
    otherPickUp.setDimInfo("d1");
    otherPickUp.setPickUpInfo("p1", 2);
    assertNotEquals(value, otherPickUp);
    assertFalse(value.equals(new Object()));
  }

  @Test
  void netCdfMD1ToStringContainsTheDimInfo() {
    final MD1 value = new MD1(1.25, "x", 2, 3, "var");
    value.setDimInfo("d1");
    value.setPickUpInfo("p1", 4);
    final String str = value.toString();
    assertTrue(str.contains("dim=d1"));
    assertTrue(str.contains("pickup=p1(4)"));
  }

  // --- MDArray.D2 --------------------------------------------------------

  @Test
  void mdArrayD2ConstructorSetsTheFields() {
    final SGDataValueHistory.MDArray.D2 value =
        new SGDataValueHistory.MDArray.D2(1.5, "x", 3, 7, "var");
    assertEquals(1.5, value.getValue());
    assertEquals(3, value.getXIndex());
    assertEquals(7, value.getYIndex());
    assertEquals("var", value.getVarName());
    assertEquals(-1, value.getXDimension());
    assertEquals(-1, value.getYDimension());
    assertNull(value.getPreviousValue());
  }

  @Test
  void mdArrayD2SetXYDimensionPropagatesToThePreviousValue() {
    final SGDataValueHistory.MDArray.D2 value =
        new SGDataValueHistory.MDArray.D2(2.0, "y", 1, 2, "var", 9.0);
    value.setXYDimension(1, 2);
    assertEquals(1, value.getXDimension());
    assertEquals(2, value.getYDimension());
    final SGDataValueHistory.MDArray.D2 previous =
        (SGDataValueHistory.MDArray.D2) value.getPreviousValue();
    assertEquals(1, previous.getXDimension());
    assertEquals(2, previous.getYDimension());
  }

  @Test
  void mdArrayD2EqualsComparesTheDimensions() {
    final SGDataValueHistory.MDArray.D2 first =
        new SGDataValueHistory.MDArray.D2(1.0, "x", 2, 3, "var");
    final SGDataValueHistory.MDArray.D2 second =
        new SGDataValueHistory.MDArray.D2(1.0, "x", 2, 3, "var");
    assertEquals(first, second);
    first.setXYDimension(1, 2);
    second.setXYDimension(1, 2);
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    second.setXYDimension(1, 3);
    assertNotEquals(first, second);
    assertNotEquals(first, new SGDataValueHistory.MDArray.D2(1.0, "x", 2, 3, "other"));
    assertFalse(first.equals(new Object()));
  }

  @Test
  void mdArrayD2ToStringContainsTheDimensions() {
    final SGDataValueHistory.MDArray.D2 value =
        new SGDataValueHistory.MDArray.D2(1.25, "x", 2, 3, "var");
    value.setXYDimension(1, 2);
    final String str = value.toString();
    assertTrue(str.contains("var=var"));
    assertTrue(str.contains("dim=(1,2)"));
  }

  // --- MDArray.MD1 -------------------------------------------------------

  @Test
  void mdArrayMD1ConstructorAndSettersSetTheFields() {
    final SGDataValueHistory.MDArray.MD1 value =
        new SGDataValueHistory.MDArray.MD1(3.0, "y", 1, 2, "var");
    assertEquals(1, value.getChildIndex());
    assertEquals(2, value.getIndex());
    assertEquals("var", value.getVarName());
    assertEquals(-1, value.getDimension());
    assertEquals(-1, value.getPickUpDimension());
    assertEquals(-1, value.getPickUpDimIndex());
    value.setDimension(3);
    value.setPickUpInfo(1, 2);
    assertEquals(3, value.getDimension());
    assertEquals(1, value.getPickUpDimension());
    assertEquals(2, value.getPickUpDimIndex());
  }

  @Test
  void mdArrayMD1SettersPropagateToThePreviousValue() {
    final SGDataValueHistory.MDArray.MD1 value =
        new SGDataValueHistory.MDArray.MD1(3.0, "y", 1, 2, "var", 9.0);
    value.setDimension(3);
    value.setPickUpInfo(1, 2);
    final SGDataValueHistory.MDArray.MD1 previous =
        (SGDataValueHistory.MDArray.MD1) value.getPreviousValue();
    assertEquals(3, previous.getDimension());
    assertEquals(1, previous.getPickUpDimension());
    assertEquals(2, previous.getPickUpDimIndex());
  }

  @Test
  void mdArrayMD1EqualsComparesTheDimensions() {
    final SGDataValueHistory.MDArray.MD1 first =
        new SGDataValueHistory.MDArray.MD1(1.0, "x", 2, 3, "var");
    final SGDataValueHistory.MDArray.MD1 second =
        new SGDataValueHistory.MDArray.MD1(1.0, "x", 2, 3, "var");
    assertEquals(first, second);
    first.setDimension(3);
    first.setPickUpInfo(1, 2);
    second.setDimension(3);
    second.setPickUpInfo(1, 2);
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    second.setPickUpInfo(1, 3);
    assertNotEquals(first, second);
    assertNotEquals(first, new SGDataValueHistory.MDArray.MD1(1.0, "x", 2, 3, "other"));
    assertFalse(first.equals(new Object()));
  }

  @Test
  void mdArrayMD1ToStringContainsTheDimInfo() {
    final SGDataValueHistory.MDArray.MD1 value =
        new SGDataValueHistory.MDArray.MD1(1.25, "x", 2, 3, "var");
    value.setDimension(3);
    value.setPickUpInfo(1, 2);
    final String str = value.toString();
    assertTrue(str.contains("var=var"));
    assertTrue(str.contains("dim=3"));
    assertTrue(str.contains("pickup=1(2)"));
  }
}
