package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDate}. */
class SGDateTest {

  @Test
  void constructorFromEpochMillis() {
    SGDate date = new SGDate(0L);
    assertEquals(0L, date.getTimeInMillis());
  }

  @Test
  void constructorFromDateStringSlash() throws ParseException {
    SGDate date = new SGDate("24/07/20");
    assertNotNull(date);
  }

  @Test
  void constructorFromDateStringDot() throws ParseException {
    SGDate date = new SGDate("2024.07.20");
    assertNotNull(date);
  }

  @Test
  void constructorFromDateStringSpace() throws ParseException {
    SGDate date = new SGDate("24 07 20");
    assertNotNull(date);
  }

  @Test
  void constructorFromISO8601() throws ParseException {
    SGDate date = new SGDate("2024-07-20T10:30:00Z");
    assertNotNull(date);
  }

  @Test
  void constructorFromHyphenFormat() throws ParseException {
    SGDate date = new SGDate("24-07-20");
    assertNotNull(date);
  }

  @Test
  void constructorFromNoSeparatorFormat() throws ParseException {
    SGDate date = new SGDate("20240720");
    assertNotNull(date);
  }

  @Test
  void constructorFromInvalidStringThrows() {
    assertThrows(ParseException.class, () -> new SGDate("not-a-date"));
  }

  @Test
  void constructorFromMillis() {
    SGDate date = new SGDate(1721452800000L); // 2024-07-20 approx
    assertEquals(1721452800000L, date.getTimeInMillis());
  }

  @Test
  void constructorFromDateValue() {
    double dateValue = 1.0; // 1 day after epoch
    SGDate date = new SGDate(dateValue);
    assertNotNull(date);
  }

  @Test
  void constructorFromZonedDateTime() {
    ZonedDateTime zdt = ZonedDateTime.of(2024, 7, 20, 10, 30, 0, 0, ZoneId.of("UTC"));
    SGDate date = new SGDate(zdt);
    assertEquals(zdt, date.getUTCDateTime());
  }

  @Test
  void compareToEarlier() {
    SGDate d1 = new SGDate(0L);
    SGDate d2 = new SGDate(1000L);
    assertTrue(d1.compareTo(d2) < 0);
  }

  @Test
  void compareToLater() {
    SGDate d1 = new SGDate(1000L);
    SGDate d2 = new SGDate(0L);
    assertTrue(d1.compareTo(d2) > 0);
  }

  @Test
  void compareToSame() {
    SGDate d1 = new SGDate(500L);
    SGDate d2 = new SGDate(500L);
    assertEquals(0, d1.compareTo(d2));
  }

  @Test
  void getZoneIdReturnsUTCByDefault() {
    SGDate date = new SGDate(0L);
    assertEquals(ZoneId.of("UTC"), date.getZoneId());
  }

  @Test
  void getDateValue() {
    SGDate date = new SGDate(0L);
    assertEquals(0.0, date.getDateValue(), 0.0);
  }

  @Test
  void toStringNotNull() {
    SGDate date = new SGDate(0L);
    assertNotNull(date.toString());
  }

  @Test
  void toStringByDateValue() {
    SGDate date = new SGDate(0L);
    assertNotNull(date.toStringByDateValue());
  }

  @Test
  void toStringByMillis() {
    SGDate date = new SGDate(0L);
    assertNotNull(date.toStringByMillis());
  }
}
