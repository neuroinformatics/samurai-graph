package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGValueRange}. */
class SGValueRangeTest {

  @Test
  void defaultConstructorHasZeroRange() {
    SGValueRange range = new SGValueRange();
    assertEquals(0.0, range.getMinValue(), 0.0);
    assertEquals(0.0, range.getMaxValue(), 0.0);
  }

  @Test
  void constructorWithMinMax() {
    SGValueRange range = new SGValueRange(1.0, 10.0);
    assertEquals(1.0, range.getMinValue(), 0.0);
    assertEquals(10.0, range.getMaxValue(), 0.0);
  }

  @Test
  void copyConstructor() {
    SGValueRange original = new SGValueRange(2.0, 8.0);
    SGValueRange copy = new SGValueRange(original);

    assertEquals(2.0, copy.getMinValue(), 0.0);
    assertEquals(8.0, copy.getMaxValue(), 0.0);
  }

  @Test
  void setRangeWithValues() {
    SGValueRange range = new SGValueRange();
    range.setRange(5.0, 15.0);

    assertEquals(5.0, range.getMinValue(), 0.0);
    assertEquals(15.0, range.getMaxValue(), 0.0);
  }

  @Test
  void setRangeFromRangeObject() {
    SGValueRange src = new SGValueRange(3.0, 7.0);
    SGValueRange dst = new SGValueRange();
    dst.setRange(src);

    assertEquals(3.0, dst.getMinValue(), 0.0);
    assertEquals(7.0, dst.getMaxValue(), 0.0);
  }

  @Test
  void setRangeRejectsMinGreaterThanMax() {
    SGValueRange range = new SGValueRange();
    assertThrows(IllegalArgumentException.class, () -> range.setRange(10.0, 5.0));
  }

  @Test
  void setRangeAllowsEqualMinMax() {
    SGValueRange range = new SGValueRange();
    range.setRange(5.0, 5.0);
    assertEquals(5.0, range.getMinValue(), 0.0);
    assertEquals(5.0, range.getMaxValue(), 0.0);
  }

  @Test
  void isMinValidReturnsTrueForNormalValue() {
    SGValueRange range = new SGValueRange(1.0, 10.0);
    assertTrue(range.isMinValid());
  }

  @Test
  void isMinValidReturnsFalseForNaN() {
    SGValueRange range = new SGValueRange(Double.NaN, 10.0);
    assertFalse(range.isMinValid());
  }

  @Test
  void isMinValidReturnsFalseForInfinity() {
    SGValueRange range = new SGValueRange(Double.POSITIVE_INFINITY, 10.0);
    assertFalse(range.isMinValid());
  }

  @Test
  void isMaxValidReturnsTrueForNormalValue() {
    SGValueRange range = new SGValueRange(1.0, 10.0);
    assertTrue(range.isMaxValid());
  }

  @Test
  void isMaxValidReturnsFalseForNaN() {
    SGValueRange range = new SGValueRange(1.0, Double.NaN);
    assertFalse(range.isMaxValid());
  }

  @Test
  void isMaxValidReturnsFalseForInfinity() {
    SGValueRange range = new SGValueRange(1.0, Double.NEGATIVE_INFINITY);
    assertFalse(range.isMaxValid());
  }

  @Test
  void getRangeReturnsClone() {
    SGValueRange range = new SGValueRange(1.0, 10.0);
    SGTuple2d tuple = range.getRange();
    assertEquals(1.0, tuple.x, 0.0);
    assertEquals(10.0, tuple.y, 0.0);
  }

  @Test
  void toStringContainsValues() {
    SGValueRange range = new SGValueRange(1.0, 10.0);
    String str = range.toString();
    assertTrue(str.contains("1.0"));
    assertTrue(str.contains("10.0"));
  }
}
