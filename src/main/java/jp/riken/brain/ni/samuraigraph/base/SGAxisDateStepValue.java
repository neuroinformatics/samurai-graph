package jp.riken.brain.ni.samuraigraph.base;

import java.text.ParseException;

public class SGAxisDateStepValue extends SGAxisStepValue {

  private SGPeriod mPeriod;

  public SGAxisDateStepValue(String str) throws ParseException {
    super();
    SGPeriod p = SGUtilityText.getPeriod(str);
    if (p == null) {
      throw new ParseException("Failed to parse a given string: " + str, 0);
    }
    this.mPeriod = p;
  }

  public SGAxisDateStepValue(SGPeriod p) {
    super();
    if (p == null) {
      throw new IllegalArgumentException("p == null");
    }
    this.mPeriod = p;
  }

  public SGPeriod getPeriod() {
    return this.mPeriod;
  }

  @Override
  public String toString() {
    return this.mPeriod.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof SGAxisDateStepValue)) {
      return false;
    }
    SGAxisDateStepValue step = (SGAxisDateStepValue) obj;
    return this.mPeriod.equals(step.mPeriod);
  }

  /**
   * Returns true if this object has zero step value.
   *
   * @return true if this object has zero step value
   */
  @Override
  public boolean isZero() {
    return SGPeriod.ZERO.equals(this.mPeriod);
  }

  /**
   * Returns a negated step value.
   *
   * @return a negated step value
   */
  @Override
  public SGAxisStepValue negated() {
    return new SGAxisDateStepValue(this.mPeriod.negated());
  }
}
