package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGAxisDateValue}. */
class SGAxisDateValueTest {

  @Test
  void constructorWithZonedDateTime() {
    ZonedDateTime dateTime = ZonedDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    SGAxisDateValue val = new SGAxisDateValue(dateTime);
    assertNotNull(val.getDate());
    assertEquals(dateTime, val.getDate().getUTCDateTime());
  }

  @Test
  void defaultConstructorRepresentsEpoch() {
    SGAxisDateValue val = new SGAxisDateValue();
    assertNotNull(val.getDate());
    assertEquals(Instant.EPOCH, val.getDate().getUTCDateTime().toInstant());
  }

  @Test
  void plusWithDateStepValue() {
    ZonedDateTime dateTime = ZonedDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    SGAxisDateValue val = new SGAxisDateValue(dateTime);
    SGAxisDateStepValue step = new SGAxisDateStepValue(SGPeriod.parse("P1D"));
    SGAxisDateValue result = (SGAxisDateValue) val.plus(step);
    assertEquals(
        ZonedDateTime.of(2026, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC),
        result.getDate().getUTCDateTime());
  }

  @Test
  void minusWithDateStepValue() {
    ZonedDateTime dateTime = ZonedDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    SGAxisDateValue val = new SGAxisDateValue(dateTime);
    SGAxisDateStepValue step = new SGAxisDateStepValue(SGPeriod.parse("P1D"));
    SGAxisDateValue result = (SGAxisDateValue) val.minus(step);
    assertEquals(
        ZonedDateTime.of(2025, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC),
        result.getDate().getUTCDateTime());
  }

  @Test
  void plusWithDoubleStepValue() {
    ZonedDateTime dateTime = ZonedDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    SGAxisDateValue val = new SGAxisDateValue(dateTime);
    SGAxisDoubleStepValue step = new SGAxisDoubleStepValue(0.0);
    SGAxisDateValue result = (SGAxisDateValue) val.plus(step);
    assertEquals(dateTime.toInstant(), result.getDate().getUTCDateTime().toInstant());
  }
}
