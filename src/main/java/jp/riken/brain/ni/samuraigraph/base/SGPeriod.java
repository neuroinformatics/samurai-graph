package jp.riken.brain.ni.samuraigraph.base;

import java.time.Duration;
import java.time.Period;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A compatibility class that provides a unified API for ISO 8601 period strings containing both
 * date and time components. java.time.Period only handles date components (years, months, days) and
 * java.time.Duration only handles time components (hours, minutes, seconds, millis). This class
 * parses ISO 8601 period strings like "P1Y2M3DT4H5M6.789S" and provides access to all components.
 */
public class SGPeriod {

  public static final SGPeriod ZERO = new SGPeriod(0, 0, 0, 0, 0, 0, 0);

  private static final Pattern PERIOD_PATTERN =
      Pattern.compile(
          "^P(?:(\\d+)Y)?(?:(\\d+)M)?(?:(\\d+)D)?(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+(?:\\.\\d+)?)S)?)?$");

  private final int years;
  private final int months;
  private final int days;
  private final int hours;
  private final int minutes;
  private final int seconds;
  private final int millis;

  private SGPeriod(
      int years, int months, int days, int hours, int minutes, int seconds, int millis) {
    this.years = years;
    this.months = months;
    this.days = days;
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
    this.millis = millis;
  }

  public static SGPeriod parse(String str) {
    if (str == null || str.isEmpty()) {
      throw new IllegalArgumentException("Period string must not be null or empty");
    }
    Matcher m = PERIOD_PATTERN.matcher(str);
    if (!m.matches()) {
      throw new IllegalArgumentException("Invalid ISO 8601 period string: " + str);
    }
    int y = m.group(1) != null ? Integer.parseInt(m.group(1)) : 0;
    int mo = m.group(2) != null ? Integer.parseInt(m.group(2)) : 0;
    int d = m.group(3) != null ? Integer.parseInt(m.group(3)) : 0;
    int h = m.group(4) != null ? Integer.parseInt(m.group(4)) : 0;
    int mi = m.group(5) != null ? Integer.parseInt(m.group(5)) : 0;
    int s = 0;
    int ml = 0;
    if (m.group(6) != null) {
      String secStr = m.group(6);
      if (secStr.contains(".")) {
        String[] parts = secStr.split("\\.");
        s = Integer.parseInt(parts[0]);
        String millisStr = parts[1];
        if (millisStr.length() > 3) {
          millisStr = millisStr.substring(0, 3);
        }
        ml = Integer.parseInt(millisStr);
        for (int i = millisStr.length(); i < 3; i++) {
          ml *= 10;
        }
      } else {
        s = Integer.parseInt(secStr);
      }
    }
    return new SGPeriod(y, mo, d, h, mi, s, ml);
  }

  public static SGPeriod years(int years) {
    return new SGPeriod(years, 0, 0, 0, 0, 0, 0);
  }

  public static SGPeriod months(int months) {
    return new SGPeriod(0, months, 0, 0, 0, 0, 0);
  }

  public static SGPeriod days(int days) {
    return new SGPeriod(0, 0, days, 0, 0, 0, 0);
  }

  public static SGPeriod hours(int hours) {
    return new SGPeriod(0, 0, 0, hours, 0, 0, 0);
  }

  public static SGPeriod minutes(int minutes) {
    return new SGPeriod(0, 0, 0, 0, minutes, 0, 0);
  }

  public static SGPeriod seconds(int seconds) {
    return new SGPeriod(0, 0, 0, 0, 0, seconds, 0);
  }

  public static SGPeriod millis(int millis) {
    return new SGPeriod(0, 0, 0, 0, 0, 0, millis);
  }

  public int getYears() {
    return years;
  }

  public int getMonths() {
    return months;
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

  public int getWeeks() {
    return 0;
  }

  public SGPeriod withYears(int years) {
    return new SGPeriod(years, months, days, hours, minutes, seconds, millis);
  }

  public SGPeriod withMonths(int months) {
    return new SGPeriod(years, months, days, hours, minutes, seconds, millis);
  }

  public SGPeriod withDays(int days) {
    return new SGPeriod(years, months, days, hours, minutes, seconds, millis);
  }

  public SGPeriod withHours(int hours) {
    return new SGPeriod(years, months, days, hours, minutes, seconds, millis);
  }

  public SGPeriod withMinutes(int minutes) {
    return new SGPeriod(years, months, days, hours, minutes, seconds, millis);
  }

  public SGPeriod withSeconds(int seconds) {
    return new SGPeriod(years, months, days, hours, minutes, seconds, millis);
  }

  public SGPeriod withMillis(int millis) {
    return new SGPeriod(years, months, days, hours, minutes, seconds, millis);
  }

  public SGPeriod plusYears(int years) {
    return new SGPeriod(this.years + years, months, days, hours, minutes, seconds, millis);
  }

  public SGPeriod plusMonths(int months) {
    return new SGPeriod(years, this.months + months, days, hours, minutes, seconds, millis);
  }

  public SGPeriod plusDays(int days) {
    return new SGPeriod(years, months, this.days + days, hours, minutes, seconds, millis);
  }

  public SGPeriod plusHours(int hours) {
    return new SGPeriod(years, months, days, this.hours + hours, minutes, seconds, millis);
  }

  public SGPeriod plusMinutes(int minutes) {
    return new SGPeriod(years, months, days, hours, this.minutes + minutes, seconds, millis);
  }

  public SGPeriod plusSeconds(int seconds) {
    return new SGPeriod(years, months, days, hours, minutes, this.seconds + seconds, millis);
  }

  public SGPeriod plusMillis(int millis) {
    return new SGPeriod(years, months, days, hours, minutes, seconds, this.millis + millis);
  }

  public SGPeriod negated() {
    return new SGPeriod(-years, -months, -days, -hours, -minutes, -seconds, -millis);
  }

  /**
   * Converts this SGPeriod to a java.time.Period for date-only components. Time components (hours,
   * minutes, seconds, millis) are ignored. Use {@link #toJavaTimeDuration()} for time components.
   */
  public Period toJavaTimePeriod() {
    return Period.of(years, months, days);
  }

  /**
   * Converts this SGPeriod to a java.time.Duration for time-only components. Date components
   * (years, months, days) are ignored. Use {@link #toJavaTimePeriod()} for date components.
   */
  public Duration toJavaTimeDuration() {
    return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds).plusMillis(millis);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('P');
    boolean hasDate = years != 0 || months != 0 || days != 0;
    boolean hasTime = hours != 0 || minutes != 0 || seconds != 0 || millis != 0;
    if (years != 0) {
      sb.append(years).append('Y');
    }
    if (months != 0) {
      sb.append(months).append('M');
    }
    if (days != 0) {
      sb.append(days).append('D');
    }
    if (hasTime) {
      sb.append('T');
      if (hours != 0) {
        sb.append(hours).append('H');
      }
      if (minutes != 0) {
        sb.append(minutes).append('M');
      }
      if (seconds != 0 || millis != 0) {
        if (millis != 0) {
          sb.append(String.format("%d.%03d", seconds, millis));
        } else {
          sb.append(seconds);
        }
        sb.append('S');
      }
    }
    if (!hasDate && !hasTime) {
      sb.append("T0S");
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof SGPeriod)) return false;
    SGPeriod other = (SGPeriod) obj;
    return this.years == other.years
        && this.months == other.months
        && this.days == other.days
        && this.hours == other.hours
        && this.minutes == other.minutes
        && this.seconds == other.seconds
        && this.millis == other.millis;
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + years;
    result = 31 * result + months;
    result = 31 * result + days;
    result = 31 * result + hours;
    result = 31 * result + minutes;
    result = 31 * result + seconds;
    result = 31 * result + millis;
    return result;
  }
}
