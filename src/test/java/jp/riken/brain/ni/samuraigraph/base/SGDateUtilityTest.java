package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDateUtility}. */
class SGDateUtilityTest {

  @Test
  void toDateValueAndToMillisAreInverse() {
    long millis = 1000L * 3600 * 24; // 1 day in milliseconds
    double dateValue = SGDateUtility.toDateValue(millis);
    assertEquals(1.0, dateValue, 0.001);

    long roundTripped = SGDateUtility.toMillis(dateValue);
    assertEquals(millis, roundTripped);
  }

  @Test
  void toDateValueZero() {
    assertEquals(0.0, SGDateUtility.toDateValue(0L), 0.0);
  }

  @Test
  void toMillisZero() {
    assertEquals(0L, SGDateUtility.toMillis(0.0));
  }

  @Test
  void toMillisLargeValue() {
    double dateValue = 10000.0; // 10000 days
    long millis = SGDateUtility.toMillis(dateValue);
    assertEquals(10000L * 1000 * 3600 * 24, millis);
  }

  @Test
  void toStringByMillis() {
    String str = SGDateUtility.toStringByMillis(0L, ZoneId.of("UTC"));
    assertNotNull(str);
    // Epoch time string should contain "1970"
    assertTrue(str.contains("1970"));
  }

  @Test
  void toStringByDateValue() {
    String str = SGDateUtility.toStringByDateValue(0.0, ZoneId.of("UTC"));
    assertNotNull(str);
    assertTrue(str.contains("1970"));
  }

  @Test
  void toDateTime() {
    assertNotNull(SGDateUtility.toDateTime(0L, ZoneId.of("UTC")));
  }

  @Test
  void getUTCZoneId() {
    assertEquals(ZoneId.of("UTC"), SGDateUtility.getUTCZoneId());
  }

  @Test
  void getUTCTimeZoneInstance() {
    TimeZone tz = SGDateUtility.getUTCTimeZoneInstance();
    assertEquals("UTC", tz.getID());
  }

  @Test
  void getUTCCalendarInstance() {
    Calendar cal = SGDateUtility.getUTCCalendarInstance();
    assertEquals(TimeZone.getTimeZone("UTC"), cal.getTimeZone());
  }

  @Test
  void toPeriodOfDays() {
    // 1.5 days = 1 day + 12 hours
    SGPeriod p = SGDateUtility.toPeriodOfDays(1.5);
    assertEquals(1, p.getDays());
    assertEquals(12, p.getHours());
    assertEquals(0, p.getMinutes());
    assertEquals(0, p.getSeconds());
  }

  @Test
  void toPeriodOfDaysZero() {
    SGPeriod p = SGDateUtility.toPeriodOfDays(0.0);
    assertEquals(0, p.getDays());
    assertEquals(0, p.getHours());
  }

  @Test
  void isValidDateFormatValid() {
    assertTrue(SGDateUtility.isValidDateFormat("yyyy-MM-dd"));
    assertTrue(SGDateUtility.isValidDateFormat("MM/dd/yyyy"));
  }

  @Test
  void isValidDateFormatInvalid() {
    assertFalse(SGDateUtility.isValidDateFormat("invalid_pattern"));
  }
}
