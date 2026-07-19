package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.Period;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGPeriod}. */
class SGPeriodTest {

  @Test
  void parseFullIso8601Period() {
    SGPeriod p = SGPeriod.parse("P1Y2M3DT4H5M6S");

    assertEquals(1, p.getYears());
    assertEquals(2, p.getMonths());
    assertEquals(3, p.getDays());
    assertEquals(4, p.getHours());
    assertEquals(5, p.getMinutes());
    assertEquals(6, p.getSeconds());
    assertEquals(0, p.getMillis());
  }

  @Test
  void parsePeriodWithMilliseconds() {
    SGPeriod p = SGPeriod.parse("PT1.234S");

    assertEquals(0, p.getYears());
    assertEquals(0, p.getMonths());
    assertEquals(0, p.getDays());
    assertEquals(0, p.getHours());
    assertEquals(0, p.getMinutes());
    assertEquals(1, p.getSeconds());
    assertEquals(234, p.getMillis());
  }

  @Test
  void parsePeriodWithSingleDigitMillis() {
    SGPeriod p = SGPeriod.parse("PT0.1S");
    assertEquals(100, p.getMillis());
  }

  @Test
  void parsePeriodWithTwoDigitMillis() {
    SGPeriod p = SGPeriod.parse("PT0.12S");
    assertEquals(120, p.getMillis());
  }

  @Test
  void parseDateOnlyPeriod() {
    SGPeriod p = SGPeriod.parse("P1Y2M3D");

    assertEquals(1, p.getYears());
    assertEquals(2, p.getMonths());
    assertEquals(3, p.getDays());
    assertEquals(0, p.getHours());
    assertEquals(0, p.getMinutes());
  }

  @Test
  void parseTimeOnlyPeriod() {
    SGPeriod p = SGPeriod.parse("PT4H5M6S");

    assertEquals(0, p.getYears());
    assertEquals(0, p.getMonths());
    assertEquals(0, p.getDays());
    assertEquals(4, p.getHours());
    assertEquals(5, p.getMinutes());
    assertEquals(6, p.getSeconds());
  }

  @Test
  void parseDaysOnly() {
    SGPeriod p = SGPeriod.parse("P7D");
    assertEquals(7, p.getDays());
  }

  @Test
  void parseZeroPeriod() {
    SGPeriod p = SGPeriod.parse("P");
    assertEquals(0, p.getYears());
    assertEquals(0, p.getMonths());
    assertEquals(0, p.getDays());
  }

  @Test
  void parseRejectsNull() {
    assertThrows(IllegalArgumentException.class, () -> SGPeriod.parse((String) null));
  }

  @Test
  void parseRejectsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> SGPeriod.parse(""));
  }

  @Test
  void parseRejectsInvalidFormat() {
    assertThrows(IllegalArgumentException.class, () -> SGPeriod.parse("invalid"));
    assertThrows(IllegalArgumentException.class, () -> SGPeriod.parse("1Y2M"));
  }

  @Test
  void factoryMethodYears() {
    SGPeriod p = SGPeriod.years(5);
    assertEquals(5, p.getYears());
    assertEquals(0, p.getMonths());
  }

  @Test
  void factoryMethodMonths() {
    SGPeriod p = SGPeriod.months(3);
    assertEquals(3, p.getMonths());
  }

  @Test
  void factoryMethodDays() {
    SGPeriod p = SGPeriod.days(10);
    assertEquals(10, p.getDays());
  }

  @Test
  void factoryMethodHours() {
    SGPeriod p = SGPeriod.hours(2);
    assertEquals(2, p.getHours());
  }

  @Test
  void factoryMethodMinutes() {
    SGPeriod p = SGPeriod.minutes(30);
    assertEquals(30, p.getMinutes());
  }

  @Test
  void factoryMethodSeconds() {
    SGPeriod p = SGPeriod.seconds(45);
    assertEquals(45, p.getSeconds());
  }

  @Test
  void factoryMethodMillis() {
    SGPeriod p = SGPeriod.millis(500);
    assertEquals(500, p.getMillis());
  }

  @Test
  void plusAddsValues() {
    SGPeriod p = SGPeriod.days(5).plusHours(3).plusMinutes(10);
    assertEquals(5, p.getDays());
    assertEquals(3, p.getHours());
    assertEquals(10, p.getMinutes());
  }

  @Test
  void withReplacesValue() {
    SGPeriod p = SGPeriod.days(5).withDays(10);
    assertEquals(10, p.getDays());
  }

  @Test
  void negatedNegatesAllComponents() {
    SGPeriod p = SGPeriod.years(1).plusMonths(2).plusDays(3).plusHours(4).negated();
    assertEquals(-1, p.getYears());
    assertEquals(-2, p.getMonths());
    assertEquals(-3, p.getDays());
    assertEquals(-4, p.getHours());
  }

  @Test
  void toJavaTimePeriodReturnsDateComponents() {
    SGPeriod p = SGPeriod.years(2).plusMonths(3).plusDays(4);
    Period jtp = p.toJavaTimePeriod();

    assertEquals(2, jtp.getYears());
    assertEquals(3, jtp.getMonths());
    assertEquals(4, jtp.getDays());
  }

  @Test
  void toJavaTimeDurationReturnsTimeComponents() {
    SGPeriod p = SGPeriod.hours(2).plusMinutes(30).plusSeconds(15).plusMillis(500);
    Duration jtd = p.toJavaTimeDuration();

    assertEquals(2, jtd.toHours());
    // 2h + 30m + 15s + 500ms = 9015500ms
    assertEquals(9015500, jtd.toMillis());
  }

  @Test
  void toStringFullPeriod() {
    SGPeriod p =
        SGPeriod.years(1)
            .plusMonths(2)
            .plusDays(3)
            .plusHours(4)
            .plusMinutes(5)
            .plusSeconds(6)
            .plusMillis(789);
    assertEquals("P1Y2M3DT4H5M6.789S", p.toString());
  }

  @Test
  void toStringZeroPeriod() {
    assertEquals("PT0S", SGPeriod.ZERO.toString());
  }

  @Test
  void toStringOnlyYears() {
    assertEquals("P5Y", SGPeriod.years(5).toString());
  }

  @Test
  void toStringOnlyTime() {
    assertEquals("PT2H30M", SGPeriod.hours(2).plusMinutes(30).toString());
  }

  @Test
  void equalsSamePeriod() {
    SGPeriod p1 = SGPeriod.parse("P1Y2M3DT4H5M6S");
    SGPeriod p2 = SGPeriod.parse("P1Y2M3DT4H5M6S");
    assertEquals(p1, p2);
  }

  @Test
  void equalsDifferentPeriod() {
    SGPeriod p1 = SGPeriod.days(1);
    SGPeriod p2 = SGPeriod.days(2);
    assertFalse(p2.equals(p1));
  }

  @Test
  void equalsAndHashCodeConsistency() {
    SGPeriod p1 = SGPeriod.parse("P1Y2M3DT4H5M6S");
    SGPeriod p2 = SGPeriod.parse("P1Y2M3DT4H5M6S");
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  @Test
  void getWeeksAlwaysReturnsZero() {
    SGPeriod p = SGPeriod.days(14);
    assertEquals(0, p.getWeeks());
  }

  @Test
  void zeroConstant() {
    assertEquals(0, SGPeriod.ZERO.getYears());
    assertEquals(0, SGPeriod.ZERO.getMonths());
    assertEquals(0, SGPeriod.ZERO.getDays());
    assertEquals(0, SGPeriod.ZERO.getHours());
  }
}
