package jp.riken.brain.ni.samuraigraph.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SGPeriodTest {

  // -- Constructors --

  @Test
  void constructor_allFields_initializesCorrectly() {
    SGPeriod period = new SGPeriod(2, 3, 1, 4, 5, 6, 7, 890);

    assertThat(period.getYears()).isEqualTo(2);
    assertThat(period.getMonths()).isEqualTo(3);
    assertThat(period.getWeeks()).isEqualTo(1);
    assertThat(period.getDays()).isEqualTo(4);
    assertThat(period.getHours()).isEqualTo(5);
    assertThat(period.getMinutes()).isEqualTo(6);
    assertThat(period.getSeconds()).isEqualTo(7);
    assertThat(period.getMillis()).isEqualTo(890);
  }

  @Test
  void constructor_sevenArgs_setsWeeksToZero() {
    SGPeriod period = new SGPeriod(1, 2, 3, 4, 5, 6, 7);

    assertThat(period.getYears()).isEqualTo(1);
    assertThat(period.getMonths()).isEqualTo(2);
    assertThat(period.getWeeks()).isEqualTo(0);
    assertThat(period.getDays()).isEqualTo(3);
    assertThat(period.getHours()).isEqualTo(4);
    assertThat(period.getMinutes()).isEqualTo(5);
    assertThat(period.getSeconds()).isEqualTo(6);
    assertThat(period.getMillis()).isEqualTo(7);
  }

  @Test
  void constructor_default_allFieldsZero() {
    SGPeriod period = new SGPeriod();

    assertThat(period.getYears()).isZero();
    assertThat(period.getMonths()).isZero();
    assertThat(period.getWeeks()).isZero();
    assertThat(period.getDays()).isZero();
    assertThat(period.getHours()).isZero();
    assertThat(period.getMinutes()).isZero();
    assertThat(period.getSeconds()).isZero();
    assertThat(period.getMillis()).isZero();
  }

  // -- Static factory methods --

  @Test
  void years_onlySetsYears() {
    SGPeriod period = SGPeriod.years(5);

    assertThat(period.getYears()).isEqualTo(5);
    assertThat(period.getMonths()).isZero();
    assertThat(period.getDays()).isZero();
    assertThat(period.getHours()).isZero();
  }

  @Test
  void months_onlySetsMonths() {
    SGPeriod period = SGPeriod.months(3);

    assertThat(period.getMonths()).isEqualTo(3);
    assertThat(period.getYears()).isZero();
    assertThat(period.getDays()).isZero();
  }

  @Test
  void days_onlySetsDays() {
    SGPeriod period = SGPeriod.days(10);

    assertThat(period.getDays()).isEqualTo(10);
    assertThat(period.getYears()).isZero();
    assertThat(period.getHours()).isZero();
  }

  @Test
  void hours_onlySetsHours() {
    SGPeriod period = SGPeriod.hours(2);

    assertThat(period.getHours()).isEqualTo(2);
    assertThat(period.getYears()).isZero();
    assertThat(period.getMinutes()).isZero();
  }

  @Test
  void minutes_onlySetsMinutes() {
    SGPeriod period = SGPeriod.minutes(30);

    assertThat(period.getMinutes()).isEqualTo(30);
    assertThat(period.getYears()).isZero();
    assertThat(period.getSeconds()).isZero();
  }

  @Test
  void seconds_onlySetsSeconds() {
    SGPeriod period = SGPeriod.seconds(45);

    assertThat(period.getSeconds()).isEqualTo(45);
    assertThat(period.getYears()).isZero();
    assertThat(period.getMillis()).isZero();
  }

  @Test
  void millis_onlySetsMillis() {
    SGPeriod period = SGPeriod.millis(500);

    assertThat(period.getMillis()).isEqualTo(500);
    assertThat(period.getYears()).isZero();
    assertThat(period.getSeconds()).isZero();
  }

  // -- ZERO constant --

  @Test
  void zero_isAllFieldsZero() {
    assertThat(SGPeriod.ZERO.getYears()).isZero();
    assertThat(SGPeriod.ZERO.getMonths()).isZero();
    assertThat(SGPeriod.ZERO.getWeeks()).isZero();
    assertThat(SGPeriod.ZERO.getDays()).isZero();
    assertThat(SGPeriod.ZERO.getHours()).isZero();
    assertThat(SGPeriod.ZERO.getMinutes()).isZero();
    assertThat(SGPeriod.ZERO.getSeconds()).isZero();
    assertThat(SGPeriod.ZERO.getMillis()).isZero();
  }

  // -- with* methods --

  @Test
  void withYears_returnsNewPeriodWithUpdatedYears() {
    SGPeriod original = SGPeriod.years(1);
    SGPeriod updated = original.withYears(5);

    assertThat(original.getYears()).isEqualTo(1);
    assertThat(updated.getYears()).isEqualTo(5);
    assertThat(updated.getMonths()).isEqualTo(original.getMonths());
  }

  @Test
  void withMonths_returnsNewPeriodWithUpdatedMonths() {
    SGPeriod original = SGPeriod.months(1);
    SGPeriod updated = original.withMonths(6);

    assertThat(original.getMonths()).isEqualTo(1);
    assertThat(updated.getMonths()).isEqualTo(6);
  }

  @Test
  void withDays_returnsNewPeriodWithUpdatedDays() {
    SGPeriod original = SGPeriod.days(1);
    SGPeriod updated = original.withDays(30);

    assertThat(updated.getDays()).isEqualTo(30);
    assertThat(updated.getYears()).isEqualTo(original.getYears());
  }

  @Test
  void withHours_returnsNewPeriodWithUpdatedHours() {
    SGPeriod original = SGPeriod.hours(1);
    SGPeriod updated = original.withHours(12);

    assertThat(updated.getHours()).isEqualTo(12);
  }

  @Test
  void withMinutes_returnsNewPeriodWithUpdatedMinutes() {
    SGPeriod original = SGPeriod.minutes(1);
    SGPeriod updated = original.withMinutes(45);

    assertThat(updated.getMinutes()).isEqualTo(45);
  }

  @Test
  void withSeconds_returnsNewPeriodWithUpdatedSeconds() {
    SGPeriod original = SGPeriod.seconds(1);
    SGPeriod updated = original.withSeconds(59);

    assertThat(updated.getSeconds()).isEqualTo(59);
  }

  @Test
  void withMillis_returnsNewPeriodWithUpdatedMillis() {
    SGPeriod original = SGPeriod.millis(1);
    SGPeriod updated = original.withMillis(999);

    assertThat(updated.getMillis()).isEqualTo(999);
  }

  // -- plus method --

  @Test
  void plus_addsAllFields() {
    SGPeriod p1 = new SGPeriod(1, 2, 3, 4, 5, 6, 7, 8);
    SGPeriod p2 = new SGPeriod(10, 20, 30, 40, 50, 60, 70, 80);
    SGPeriod result = p1.plus(p2);

    assertThat(result.getYears()).isEqualTo(11);
    assertThat(result.getMonths()).isEqualTo(22);
    assertThat(result.getWeeks()).isEqualTo(33);
    assertThat(result.getDays()).isEqualTo(44);
    assertThat(result.getHours()).isEqualTo(55);
    assertThat(result.getMinutes()).isEqualTo(66);
    assertThat(result.getSeconds()).isEqualTo(77);
    assertThat(result.getMillis()).isEqualTo(88);
  }

  @Test
  void plus_withZero_returnsEquivalentPeriod() {
    SGPeriod p1 = SGPeriod.years(5);
    SGPeriod result = p1.plus(SGPeriod.ZERO);

    assertThat(result).isEqualTo(p1);
  }

  // -- negated method --

  @Test
  void negated_negatesAllFields() {
    SGPeriod p1 = new SGPeriod(1, 2, 3, 4, 5, 6, 7, 8);
    SGPeriod result = p1.negated();

    assertThat(result.getYears()).isEqualTo(-1);
    assertThat(result.getMonths()).isEqualTo(-2);
    assertThat(result.getWeeks()).isEqualTo(-3);
    assertThat(result.getDays()).isEqualTo(-4);
    assertThat(result.getHours()).isEqualTo(-5);
    assertThat(result.getMinutes()).isEqualTo(-6);
    assertThat(result.getSeconds()).isEqualTo(-7);
    assertThat(result.getMillis()).isEqualTo(-8);
  }

  @Test
  void negated_twice_returnsOriginal() {
    SGPeriod p1 = SGPeriod.days(10);
    SGPeriod result = p1.negated().negated();

    assertThat(result).isEqualTo(p1);
  }

  // -- isZero method --

  @Test
  void isZero_returnsTrueForZeroPeriod() {
    assertThat(SGPeriod.ZERO.isZero()).isTrue();
    assertThat(new SGPeriod().isZero()).isTrue();
  }

  @Test
  void isZero_returnsFalseForNonZeroPeriod() {
    assertThat(SGPeriod.years(1).isZero()).isFalse();
    assertThat(SGPeriod.millis(1).isZero()).isFalse();
  }

  // -- toString method --

  @Test
  void toString_zeroReturnsP() {
    assertThat(SGPeriod.ZERO.toString()).isEqualTo("P");
  }

  @Test
  void toString_yearsOnly() {
    assertThat(SGPeriod.years(2).toString()).isEqualTo("P2Y");
  }

  @Test
  void toString_monthsOnly() {
    assertThat(SGPeriod.months(3).toString()).isEqualTo("P3M");
  }

  @Test
  void toString_daysOnly() {
    assertThat(SGPeriod.days(10).toString()).isEqualTo("P10D");
  }

  @Test
  void toString_hoursOnly() {
    assertThat(SGPeriod.hours(5).toString()).isEqualTo("PT5H");
  }

  @Test
  void toString_minutesOnly() {
    assertThat(SGPeriod.minutes(30).toString()).isEqualTo("PT30M");
  }

  @Test
  void toString_secondsOnly() {
    assertThat(SGPeriod.seconds(45).toString()).isEqualTo("PT45S");
  }

  @Test
  void toString_secondsWithMillis() {
    SGPeriod period = new SGPeriod(0, 0, 0, 0, 0, 0, 10, 500);
    assertThat(period.toString()).isEqualTo("PT10.500S");
  }

  @Test
  void toString_complexPeriod() {
    SGPeriod period = new SGPeriod(1, 2, 0, 3, 4, 5, 6, 0);
    assertThat(period.toString()).isEqualTo("P1Y2M3DT4H5M6S");
  }

  // -- equals and hashCode --

  @Test
  void equals_sameInstance_returnsTrue() {
    SGPeriod p = SGPeriod.years(1);
    assertThat(p.equals((Object) p)).isTrue();
  }

  @Test
  void equals_null_returnsFalse() {
    assertThat(SGPeriod.years(1).equals(null)).isFalse();
  }

  @Test
  void equals_differentClass_returnsFalse() {
    assertThat(SGPeriod.years(1).equals("P1Y")).isFalse();
  }

  @Test
  void equals_sameValues_returnsTrue() {
    SGPeriod p1 = new SGPeriod(1, 2, 3, 4, 5, 6, 7, 8);
    SGPeriod p2 = new SGPeriod(1, 2, 3, 4, 5, 6, 7, 8);

    assertThat(p1.equals(p2)).isTrue();
  }

  @Test
  void equals_differentValues_returnsFalse() {
    SGPeriod p1 = SGPeriod.years(1);
    SGPeriod p2 = SGPeriod.years(2);

    assertThat(p1.equals(p2)).isFalse();
  }

  @Test
  void hashCode_sameValues_sameHashCode() {
    SGPeriod p1 = new SGPeriod(1, 2, 3, 4, 5, 6, 7, 8);
    SGPeriod p2 = new SGPeriod(1, 2, 3, 4, 5, 6, 7, 8);

    assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
  }

  // -- parse method --

  @Nested
  class ParseTests {

    @Test
    void parse_null_returnsZero() {
      assertThat(SGPeriod.parse(null)).isEqualTo(SGPeriod.ZERO);
    }

    @Test
    void parse_emptyString_returnsZero() {
      assertThat(SGPeriod.parse("")).isEqualTo(SGPeriod.ZERO);
    }

    @Test
    void parse_yearsOnly() {
      SGPeriod period = SGPeriod.parse("P2Y");

      assertThat(period.getYears()).isEqualTo(2);
      assertThat(period.getMonths()).isZero();
      assertThat(period.getDays()).isZero();
    }

    @Test
    void parse_monthsOnly() {
      SGPeriod period = SGPeriod.parse("P3M");

      assertThat(period.getMonths()).isEqualTo(3);
    }

    @Test
    void parse_daysOnly() {
      SGPeriod period = SGPeriod.parse("P10D");

      assertThat(period.getDays()).isEqualTo(10);
    }

    @Test
    void parse_hoursOnly() {
      SGPeriod period = SGPeriod.parse("PT5H");

      assertThat(period.getHours()).isEqualTo(5);
    }

    @Test
    void parse_minutesOnly() {
      SGPeriod period = SGPeriod.parse("PT30M");

      assertThat(period.getMinutes()).isEqualTo(30);
    }

    @Test
    void parse_secondsOnly() {
      SGPeriod period = SGPeriod.parse("PT45S");

      assertThat(period.getSeconds()).isEqualTo(45);
    }

    @Test
    void parse_secondsWithFractionalMillis() {
      SGPeriod period = SGPeriod.parse("PT10.5S");

      assertThat(period.getSeconds()).isEqualTo(10);
      assertThat(period.getMillis()).isEqualTo(500);
    }

    @Test
    void parse_complexPeriod() {
      SGPeriod period = SGPeriod.parse("P1Y2M3DT4H5M6.789S");

      assertThat(period.getYears()).isEqualTo(1);
      assertThat(period.getMonths()).isEqualTo(2);
      assertThat(period.getDays()).isEqualTo(3);
      assertThat(period.getHours()).isEqualTo(4);
      assertThat(period.getMinutes()).isEqualTo(5);
      assertThat(period.getSeconds()).isEqualTo(6);
      assertThat(period.getMillis()).isEqualTo(789);
    }

    @Test
    void parse_partialComplex() {
      SGPeriod period = SGPeriod.parse("P1Y3DT4H");

      assertThat(period.getYears()).isEqualTo(1);
      assertThat(period.getMonths()).isZero();
      assertThat(period.getDays()).isEqualTo(3);
      assertThat(period.getHours()).isEqualTo(4);
      assertThat(period.getMinutes()).isZero();
      assertThat(period.getSeconds()).isZero();
    }

    @Test
    void parse_invalidFormat_throwsException() {
      assertThatThrownBy(() -> SGPeriod.parse("invalid"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid period format");
    }

    @Test
    void parse_missingPT_forTime_throwsException() {
      assertThatThrownBy(() -> SGPeriod.parse("P5H")).isInstanceOf(IllegalArgumentException.class);
    }
  }
}
