package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGUtilityNumber}. */
class SGUtilityNumberTest {

  @Test
  void getOrderForPositiveValue() {
    assertEquals(2, SGUtilityNumber.getOrder(100.0));
    assertEquals(1, SGUtilityNumber.getOrder(15.0));
    assertEquals(-2, SGUtilityNumber.getOrder(0.01));
  }

  @Test
  void getOrderForNegativeValueUsesAbsoluteValue() {
    assertEquals(2, SGUtilityNumber.getOrder(-150.0));
  }

  @Test
  void getOrderRejectsZero() {
    assertThrows(IllegalArgumentException.class, () -> SGUtilityNumber.getOrder(0.0));
  }

  @Test
  void getOrderRejectsNonFiniteValues() {
    assertThrows(IllegalArgumentException.class, () -> SGUtilityNumber.getOrder(Double.NaN));
    assertThrows(
        IllegalArgumentException.class, () -> SGUtilityNumber.getOrder(Double.POSITIVE_INFINITY));
  }

  @Test
  void getPowersOfTenReturnsPower() {
    assertEquals(100.0, SGUtilityNumber.getPowersOfTen(2), 0.0);
    assertEquals(0.1, SGUtilityNumber.getPowersOfTen(-1), 1e-12);
  }

  @Test
  void truncateNumberTruncatesAtGivenDigit() {
    assertEquals(8700.0, SGUtilityNumber.truncateNumber(8715.61, 2), 0.0);
    assertEquals(50.0, SGUtilityNumber.truncateNumber("50.5", 1), 0.0);
  }

  @Test
  void roundOffNumberRoundsHalfUpAtGivenDigit() {
    assertEquals(8700.0, SGUtilityNumber.roundOffNumber(8715.61, 1), 0.0);
    assertEquals(8800.0, SGUtilityNumber.roundOffNumber(8765.61, 1), 0.0);
  }

  @Test
  void roundOutNumberRoundsUpAtGivenDigit() {
    assertEquals(8800.0, SGUtilityNumber.roundOutNumber(8715.61, 1), 0.0);
  }

  @Test
  void containsWithinRangeIncludingBounds() {
    assertTrue(SGUtilityNumber.contains(1.0, 5.0, 3.0));
    assertTrue(SGUtilityNumber.contains(1.0, 5.0, 1.0));
    assertTrue(SGUtilityNumber.contains(1.0, 5.0, 5.0));
    assertFalse(SGUtilityNumber.contains(1.0, 5.0, 6.0));
  }

  @Test
  void containsAcceptsReversedRange() {
    assertTrue(SGUtilityNumber.contains(5.0, 1.0, 4.0));
  }

  @Test
  void containsRangeWithinRange() {
    assertTrue(SGUtilityNumber.contains(0.0, 10.0, 2.0, 3.0));
    assertFalse(SGUtilityNumber.contains(0.0, 10.0, 5.0, 20.0));
  }

  @Test
  void getOverlapReturnsOverlappingSize() {
    assertEquals(5.0, SGUtilityNumber.getOverlap(0.0, 10.0, 5.0, 15.0), 0.0);
    assertEquals(0.0, SGUtilityNumber.getOverlap(0.0, 1.0, 2.0, 3.0), 0.0);
  }

  @Test
  void isOverlappingDetectsRangeIntersection() {
    assertTrue(SGUtilityNumber.isOverlapping(0.0, 10.0, 5.0, 15.0));
    assertFalse(SGUtilityNumber.isOverlapping(0.0, 1.0, 2.0, 3.0));
  }

  @Test
  void minIgnoresNonFiniteValues() {
    assertEquals(2.0, SGUtilityNumber.min(new double[] {Double.NaN, 5.0, 2.0}), 0.0);
    assertEquals(
        Double.NaN, SGUtilityNumber.min(new double[] {Double.NaN, Double.POSITIVE_INFINITY}), 0.0);
  }

  @Test
  void minWithIntegerArray() {
    assertEquals(1, SGUtilityNumber.min(new int[] {3, 1, 2}));
  }

  @Test
  void minWithTwoValues() {
    assertEquals(3.0, SGUtilityNumber.min(3.0, 5.0), 0.0);
    assertEquals(4.5, SGUtilityNumber.min(9.5, 4.5), 0.0);
  }

  @Test
  void maxIgnoresNonFiniteValues() {
    assertEquals(5.0, SGUtilityNumber.max(new double[] {5.0, Double.NaN, 2.0}), 0.0);
    assertEquals(Double.NaN, SGUtilityNumber.max(new double[] {}), 0.0);
  }

  @Test
  void absMaxReturnsMaximumAbsoluteValue() {
    assertEquals(7.0, SGUtilityNumber.absMax(new double[] {-7.0, 3.0, 5.0}), 0.0);
  }
}
