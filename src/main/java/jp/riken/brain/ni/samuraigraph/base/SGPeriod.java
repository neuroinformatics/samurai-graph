package jp.riken.brain.ni.samuraigraph.base;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a time-based or date-based amount of time, such as '2 years, 3 months, 4 days, 5
 * hours, 6 minutes, 7 seconds, 890 milliseconds'. Replacement for joda-time Period.
 */
public final class SGPeriod {

  private static final Pattern ISO_PATTERN =
      Pattern.compile(
          "P(?:(\\d+)Y)?(?:(\\d+)M)?(?:(\\d+)D)?"
              + "(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:([\\d.]+)S)?)?");

  private final int years;
  private final int months;
  private final int weeks;
  private final int days;
  private final int hours;
  private final int minutes;
  private final int seconds;
  private final int millis;

  public SGPeriod(
      int years, int months, int weeks, int days, int hours, int minutes, int seconds, int millis) {
    this.years = years;
    this.months = months;
    this.weeks = weeks;
    this.days = days;
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
    this.millis = millis;
  }

  public SGPeriod(
      int years, int months, int days, int hours, int minutes, int seconds, int millis) {
    this(years, months, 0, days, hours, minutes, seconds, millis);
  }

  public SGPeriod() {
    this(0, 0, 0, 0, 0, 0, 0, 0);
  }

  public int getYears() {
    return years;
  }

  public int getMonths() {
    return months;
  }

  public int getWeeks() {
    return weeks;
  }

  public int getDays() {
    return days;
  }

  public int getHours() {
    return hours;
  }

  public int getMinutes() {
    return minutes;
  }

  public int getSeconds() {
    return seconds;
  }

  public int getMillis() {
    return millis;
  }

  public static SGPeriod parse(String str) {
    if (str == null || str.isEmpty()) {
      return ZERO;
    }
    Matcher m = ISO_PATTERN.matcher(str);
    if (!m.matches()) {
      throw new IllegalArgumentException("Invalid period format: " + str);
    }

    int years = parseIntOrNull(m.group(1), 0);
    int months = parseIntOrNull(m.group(2), 0);
    int days = parseIntOrNull(m.group(3), 0);
    int hours = parseIntOrNull(m.group(4), 0);
    int minutes = parseIntOrNull(m.group(5), 0);
    int seconds = 0;
    int millis = 0;

    String secStr = m.group(6);
    if (secStr != null) {
      int dotIdx = secStr.indexOf('.');
      if (dotIdx >= 0) {
        seconds = parseIntOrNull(secStr.substring(0, dotIdx), 0);
        String msPart = secStr.substring(dotIdx + 1);
        millis = (int) (Double.parseDouble("0." + msPart) * 1000);
      } else {
        seconds = parseIntOrNull(secStr, 0);
      }
    }

    return new SGPeriod(years, months, 0, days, hours, minutes, seconds, millis);
  }

  private static int parseIntOrNull(String s, int def) {
    if (s == null) return def;
    return Integer.parseInt(s);
  }

  public SGPeriod withYears(int years) {
    return new SGPeriod(years, months, weeks, days, hours, minutes, seconds, millis);
  }

  public SGPeriod withMonths(int months) {
    return new SGPeriod(this.years, months, weeks, days, hours, minutes, seconds, millis);
  }

  public SGPeriod withDays(int days) {
    return new SGPeriod(this.years, this.months, weeks, days, hours, minutes, seconds, millis);
  }

  public SGPeriod withHours(int hours) {
    return new SGPeriod(this.years, this.months, weeks, this.days, hours, minutes, seconds, millis);
  }

  public SGPeriod withMinutes(int minutes) {
    return new SGPeriod(
        this.years, this.months, weeks, this.days, this.hours, minutes, seconds, millis);
  }

  public SGPeriod withSeconds(int seconds) {
    return new SGPeriod(
        this.years, this.months, weeks, this.days, this.hours, this.minutes, seconds, millis);
  }

  public SGPeriod withMillis(int millis) {
    return new SGPeriod(
        this.years, this.months, weeks, this.days, this.hours, this.minutes, this.seconds, millis);
  }

  public SGPeriod plus(SGPeriod other) {
    return new SGPeriod(
        this.years + other.years,
        this.months + other.months,
        this.weeks + other.weeks,
        this.days + other.days,
        this.hours + other.hours,
        this.minutes + other.minutes,
        this.seconds + other.seconds,
        this.millis + other.millis);
  }

  public SGPeriod negated() {
    return new SGPeriod(
        -this.years,
        -this.months,
        -this.weeks,
        -this.days,
        -this.hours,
        -this.minutes,
        -this.seconds,
        -this.millis);
  }

  public boolean isZero() {
    return years == 0
        && months == 0
        && weeks == 0
        && days == 0
        && hours == 0
        && minutes == 0
        && seconds == 0
        && millis == 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('P');
    if (years != 0) sb.append(years).append('Y');
    if (months != 0) sb.append(months).append('M');
    if (days != 0) sb.append(days).append('D');
    if (hours != 0 || minutes != 0 || seconds != 0 || millis != 0) {
      sb.append('T');
      if (hours != 0) sb.append(hours).append('H');
      if (minutes != 0) sb.append(minutes).append('M');
      if (seconds != 0 || millis != 0) {
        sb.append(seconds);
        if (millis != 0) sb.append('.').append(String.format("%03d", millis));
        sb.append('S');
      }
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    SGPeriod other = (SGPeriod) obj;
    return years == other.years
        && months == other.months
        && weeks == other.weeks
        && days == other.days
        && hours == other.hours
        && minutes == other.minutes
        && seconds == other.seconds
        && millis == other.millis;
  }

  @Override
  public int hashCode() {
    return Objects.hash(years, months, weeks, days, hours, minutes, seconds, millis);
  }

  public static SGPeriod years(int years) {
    return new SGPeriod(years, 0, 0, 0, 0, 0, 0, 0);
  }

  public static SGPeriod months(int months) {
    return new SGPeriod(0, months, 0, 0, 0, 0, 0, 0);
  }

  public static SGPeriod days(int days) {
    return new SGPeriod(0, 0, 0, days, 0, 0, 0, 0);
  }

  public static SGPeriod hours(int hours) {
    return new SGPeriod(0, 0, 0, 0, hours, 0, 0, 0);
  }

  public static SGPeriod minutes(int minutes) {
    return new SGPeriod(0, 0, 0, 0, 0, minutes, 0, 0);
  }

  public static SGPeriod seconds(int seconds) {
    return new SGPeriod(0, 0, 0, 0, 0, 0, seconds, 0);
  }

  public static SGPeriod millis(int millis) {
    return new SGPeriod(0, 0, 0, 0, 0, 0, 0, millis);
  }

  public static final SGPeriod ZERO = new SGPeriod();
}
