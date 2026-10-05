package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Unit tests for the property-value calculation methods of {@link SGUtility}. */
class SGUtilityPropertyValueTest {

  // -- calcPropertyValueInUnit ------------------------------------------------

  @Test
  void calcPropertyValueInUnitKeepsValueWhenNoUnitsGiven() {
    assertEquals(50.0f, SGUtility.calcPropertyValueInUnit(50.0f, null, null, 0, 100, 1));
    assertEquals(0.5f, SGUtility.calcPropertyValueInUnit(0.5f, null, null, 0.25, 6.0, -2));
  }

  @Test
  void calcPropertyValueInUnitRoundsOffToGivenOrder() {
    // order 1 rounds at the tens place (digit = 0)
    assertEquals(60.0f, SGUtility.calcPropertyValueInUnit(55.0f, null, null, 0, 100, 1));
    // order -1 keeps the ones place (digit = -2)
    assertEquals(12.3f, SGUtility.calcPropertyValueInUnit(12.3f, null, null, 0, 100, -1));
  }

  @Test
  void calcPropertyValueInUnitReturnsNullWhenOutOfRange() {
    // below the minimum
    assertNull(SGUtility.calcPropertyValueInUnit(0.1f, null, null, 0.25, 6.0, -2));
    // above the maximum
    assertNull(SGUtility.calcPropertyValueInUnit(7.0f, null, null, 0.25, 6.0, -2));
    // a value within the range is kept
    assertEquals(0.5f, SGUtility.calcPropertyValueInUnit(0.5f, null, null, 0.25, 6.0, -2));
  }

  @Test
  void calcPropertyValueInUnitConvertsBetweenLengthUnits() {
    // 1 cm -> about 28.3 pt, rounded at the ones place
    assertEquals(28.3f, SGUtility.calcPropertyValueInUnit(1.0f, "cm", "pt", 0, 1000, -1));
    // 2.54 cm is exactly one inch = 72 pt
    assertEquals(72.0f, SGUtility.calcPropertyValueInUnit(2.54f, "cm", "pt", 0, 1000, -2));
  }

  @Test
  void calcPropertyValueInUnitRejectsNonFiniteValues() {
    assertNull(SGUtility.calcPropertyValueInUnit(Float.NaN, null, null, 0, 100, 1));
    assertNull(SGUtility.calcPropertyValueInUnit(Float.POSITIVE_INFINITY, null, null, 0, 100, 1));
  }

  // -- calcPropertyValue ------------------------------------------------------

  @Test
  void calcPropertyValueConvertsResultToOutputUnit() {
    // 1 cm -> about 28.3 pt
    assertEquals(28.3f, SGUtility.calcPropertyValue(1.0f, "cm", "pt", 0, 1000, -1));
    // a point value expressed in points is unchanged
    assertEquals(72.0f, SGUtility.calcPropertyValue(72.0f, "pt", "pt", 0, 1000, -2));
  }

  @Test
  void calcPropertyValueKeepsValueWhenNoUnitsGiven() {
    assertEquals(50.0f, SGUtility.calcPropertyValue(50.0f, null, null, 0, 1000, 1));
  }

  // -- line width and font size helpers ---------------------------------------

  @Test
  void getLineWidthClampsToAllowedRange() {
    assertEquals(1.0f, SGUtility.getLineWidth(1.0f, "pt"));
    assertEquals(3.5f, SGUtility.getLineWidth(3.5f, "pt"));
    // below the minimum line width
    assertNull(SGUtility.getLineWidth(0.1f, "pt"));
    // above the maximum line width
    assertNull(SGUtility.getLineWidth(10.0f, "pt"));
  }

  @Test
  void getFontSizeClampsToAllowedRange() {
    assertEquals(12.0f, SGUtility.getFontSize(12.0f, "pt"));
    // below the minimum font size
    assertNull(SGUtility.getFontSize(3.0f, "pt"));
    // above the maximum font size
    assertNull(SGUtility.getFontSize(200.0f, "pt"));
  }
}
