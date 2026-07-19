package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGAxisDoubleStepValue}. */
class SGAxisDoubleStepValueTest {

  @Test
  void getValue() {
    SGAxisDoubleStepValue step = new SGAxisDoubleStepValue(5.5);
    assertEquals(5.5, step.getValue(), 0.0);
  }

  @Test
  void toStringReturnsValueAsString() {
    SGAxisDoubleStepValue step = new SGAxisDoubleStepValue(3.0);
    assertEquals("3.0", step.toString());
  }

  @Test
  void equalsSameValue() {
    SGAxisDoubleStepValue a = new SGAxisDoubleStepValue(2.0);
    SGAxisDoubleStepValue b = new SGAxisDoubleStepValue(2.0);
    assertEquals(b, a);
  }

  @Test
  void equalsDifferentValue() {
    SGAxisDoubleStepValue a = new SGAxisDoubleStepValue(2.0);
    SGAxisDoubleStepValue b = new SGAxisDoubleStepValue(3.0);
    assertFalse(b.equals(a));
  }

  @Test
  void isZeroWithZero() {
    SGAxisDoubleStepValue step = new SGAxisDoubleStepValue(0.0);
    assertTrue(step.isZero());
  }

  @Test
  void isZeroWithNonZero() {
    SGAxisDoubleStepValue step = new SGAxisDoubleStepValue(1.0);
    assertFalse(step.isZero());
  }

  @Test
  void negated() {
    SGAxisDoubleStepValue step = new SGAxisDoubleStepValue(5.0);
    SGAxisStepValue neg = step.negated();

    assertEquals(SGAxisDoubleStepValue.class, neg.getClass());
    assertEquals(-5.0, ((SGAxisDoubleStepValue) neg).getValue(), 0.0);
  }
}
