package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGAxisDoubleValue}. */
class SGAxisDoubleValueTest {

  @Test
  void defaultConstructorHasZeroValue() {
    SGAxisDoubleValue val = new SGAxisDoubleValue();
    assertEquals(0.0, val.getValue(), 0.0);
  }

  @Test
  void constructorWithValue() {
    SGAxisDoubleValue val = new SGAxisDoubleValue(3.14);
    assertEquals(3.14, val.getValue(), 0.0);
  }

  @Test
  void plusWithDoubleStepValue() {
    SGAxisDoubleValue val = new SGAxisDoubleValue(10.0);
    SGAxisDoubleStepValue step = new SGAxisDoubleStepValue(2.5);

    SGAxisValue result = val.plus(step);
    assertEquals(12.5, ((SGAxisDoubleValue) result).getValue(), 0.0);
  }

  @Test
  void minusWithDoubleStepValue() {
    SGAxisDoubleValue val = new SGAxisDoubleValue(10.0);
    SGAxisDoubleStepValue step = new SGAxisDoubleStepValue(2.5);

    SGAxisValue result = val.minus(step);
    assertEquals(7.5, ((SGAxisDoubleValue) result).getValue(), 0.0);
  }

  @Test
  void toStringReturnsValueAsString() {
    SGAxisDoubleValue val = new SGAxisDoubleValue(42.0);
    assertEquals("42.0", val.toString());
  }

  @Test
  void toStringWithNonIntegerValue() {
    SGAxisDoubleValue val = new SGAxisDoubleValue(3.14);
    assertEquals("3.14", val.toString());
  }
}
