package jp.riken.brain.ni.samuraigraph.base;

import java.text.ParseException;
import java.time.ZonedDateTime;

public class SGAxisDateValue extends SGAxisValue {

  private final SGDate mDate;

  /**
   * Creates an instance for a given date.
   *
   * @param date a date
   */
  public SGAxisDateValue(SGDate date) {
    super();
    this.mDate = date;
  }

  /** Creates an instance at the epoch time in UTC time zone. */
  public SGAxisDateValue() {
    this(new SGDate());
  }

  public SGAxisDateValue(String str) throws ParseException {
    this(new SGDate(str));
  }

  public SGAxisDateValue(ZonedDateTime dateTime) {
    this(new SGDate(dateTime));
  }

  public SGAxisDateValue(final double dateValue) {
    this(new SGDate(dateValue));
  }

  public SGAxisDateValue(final long millis) {
    this(new SGDate(millis));
  }

  public SGDate getDate() {
    return this.mDate;
  }

  @Override
  public double getValue() {
    return this.mDate.getDateValue();
  }

  @Override
  public SGAxisValue plus(SGAxisStepValue step) {
    if (step instanceof SGAxisDateStepValue) {
      SGAxisDateStepValue p = (SGAxisDateStepValue) step;
      ZonedDateTime dateTime = addPeriod(this.mDate.getUTCDateTime(), p.getPeriod());
      return new SGAxisDateValue(dateTime);
    } else if (step instanceof SGAxisDoubleStepValue) {
      SGAxisDoubleStepValue d = (SGAxisDoubleStepValue) step;
      return new SGAxisDateValue(this.getValue() + d.getValue());
    } else {
      throw new IllegalArgumentException("Invalid parameter: " + step);
    }
  }

  @Override
  public SGAxisValue minus(SGAxisStepValue step) {
    if (step instanceof SGAxisDateStepValue) {
      SGAxisDateStepValue p = (SGAxisDateStepValue) step;
      ZonedDateTime dateTime = subtractPeriod(this.mDate.getUTCDateTime(), p.getPeriod());
      return new SGAxisDateValue(dateTime);
    } else if (step instanceof SGAxisDoubleStepValue) {
      SGAxisDoubleStepValue d = (SGAxisDoubleStepValue) step;
      return new SGAxisDateValue(this.getValue() - d.getValue());
    } else {
      throw new IllegalArgumentException("Invalid parameter: " + step);
    }
  }

  @Override
  public String toString() {
    return this.mDate.toString();
  }

  @Override
  public SGAxisValue adjustValue(SGAxisValue min, SGAxisValue max, final int digit) {
    // just returns this object
    return this;
  }

  private static ZonedDateTime addPeriod(ZonedDateTime dt, SGPeriod p) {
    ZonedDateTime result = dt;
    if (p.getYears() != 0) result = result.plusYears(p.getYears());
    if (p.getMonths() != 0) result = result.plusMonths(p.getMonths());
    if (p.getWeeks() != 0) result = result.plusWeeks(p.getWeeks());
    if (p.getDays() != 0) result = result.plusDays(p.getDays());
    if (p.getHours() != 0) result = result.plusHours(p.getHours());
    if (p.getMinutes() != 0) result = result.plusMinutes(p.getMinutes());
    if (p.getSeconds() != 0) result = result.plusSeconds(p.getSeconds());
    if (p.getMillis() != 0) result = result.plus(java.time.Duration.ofMillis(p.getMillis()));
    return result;
  }

  private static ZonedDateTime subtractPeriod(ZonedDateTime dt, SGPeriod p) {
    ZonedDateTime result = dt;
    if (p.getYears() != 0) result = result.minusYears(p.getYears());
    if (p.getMonths() != 0) result = result.minusMonths(p.getMonths());
    if (p.getWeeks() != 0) result = result.minusWeeks(p.getWeeks());
    if (p.getDays() != 0) result = result.minusDays(p.getDays());
    if (p.getHours() != 0) result = result.minusHours(p.getHours());
    if (p.getMinutes() != 0) result = result.minusMinutes(p.getMinutes());
    if (p.getSeconds() != 0) result = result.minusSeconds(p.getSeconds());
    if (p.getMillis() != 0) result = result.minus(java.time.Duration.ofMillis(p.getMillis()));
    return result;
  }
}
