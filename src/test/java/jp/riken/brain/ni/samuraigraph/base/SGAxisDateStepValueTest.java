package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGAxisDateStepValue}. */
class SGAxisDateStepValueTest {

  @Test
  void constructorWithPeriod() {
    SGPeriod period = SGPeriod.parse("P1D");
    SGAxisDateStepValue step = new SGAxisDateStepValue(period);
    assertEquals(period, step.getPeriod());
  }

  @Test
  void constructorRejectsNullPeriod() {
    assertThrows(IllegalArgumentException.class, () -> new SGAxisDateStepValue((SGPeriod) null));
  }

  @Test
  void equalsComparesPeriod() {
    SGAxisDateStepValue a = new SGAxisDateStepValue(SGPeriod.parse("P1D"));
    SGAxisDateStepValue b = new SGAxisDateStepValue(SGPeriod.parse("P1D"));
    SGAxisDateStepValue c = new SGAxisDateStepValue(SGPeriod.parse("P2D"));
    assertEquals(a, b);
    assertFalse(a.equals(c));
    assertFalse(a.equals(new Object()));
  }

  @Test
  void isZeroForZeroPeriod() {
    assertTrue(new SGAxisDateStepValue(SGPeriod.ZERO).isZero());
    assertFalse(new SGAxisDateStepValue(SGPeriod.parse("P1D")).isZero());
  }

  @Test
  void negatedReturnsOppositePeriod() {
    SGAxisDateStepValue step = new SGAxisDateStepValue(SGPeriod.parse("P1D"));
    SGAxisDateStepValue negated = (SGAxisDateStepValue) step.negated();
    assertEquals(SGPeriod.parse("P1D").negated(), negated.getPeriod());
  }
}
