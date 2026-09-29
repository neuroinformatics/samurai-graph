package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleValue;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.Test;

/** Unit tests of the axis element properties. */
class SGAxisElementAxisPropertiesTest {

  private static SGAxisElement.AxisProperties filled() {
    final SGAxisElement.AxisProperties p = new SGAxisElement.AxisProperties();
    p.axisVisible = true;
    p.dateMode = false;
    p.axisLineVisible = true;
    p.axisLineWidth = 1.0f;
    p.axisLineColor = Color.BLACK;
    p.spaceLineAndNumbers = 2.0f;
    p.titleVisible = true;
    p.titleText = "title";
    p.spaceTitleAndNumbers = 3.0f;
    p.titleShiftFromCenter = 0.5f;
    p.titleFontName = "SansSerif";
    p.titleFontStyle = 1;
    p.titleFontSize = 12.0f;
    p.titleFontColor = Color.RED;
    p.numberInteger = false;
    p.minValue = new SGAxisDoubleValue(0.0);
    p.maxValue = new SGAxisDoubleValue(10.0);
    p.scaleType = 1;
    p.invertedCoordinates = false;
    p.autoCalc = true;
    p.baselineValue = new SGAxisDoubleValue(5.0);
    p.stepValue = new SGAxisDoubleStepValue(1.0);
    p.exponentVisible = true;
    p.exponent = 2;
    p.numberVisible = true;
    p.numberAngle = 0.0f;
    p.numberFontName = "SansSerif";
    p.numberFontSize = 11.0f;
    p.numberFontStyle = 0;
    p.numberFontColor = Color.BLUE;
    p.exponentLocationX = 1.0f;
    p.exponentLocationY = 2.0f;
    p.numberDateFormat = "yyyy";
    p.tickMarkVisible = true;
    p.tickMarkBothsides = false;
    p.tickMarkWidth = 1.0f;
    p.tickMarkLength = 5.0f;
    p.minorTickMarkNumber = 1;
    p.minorTickMarkLength = 2.0f;
    p.tickMarkColor = Color.GRAY;
    return p;
  }

  @Test
  void equalPropertiesCompareEqual() {
    assertTrue(filled().equals(filled()));
  }

  @Test
  void booleanDifferencesBreakEquality() {
    assertFalse(filled().equals(differentAxisVisible()));
    assertFalse(filled().equals(differentNumberVisible()));
    assertFalse(filled().equals(differentTickMarkVisible()));
  }

  private static SGAxisElement.AxisProperties differentAxisVisible() {
    final SGAxisElement.AxisProperties p = filled();
    p.axisVisible = false;
    return p;
  }

  private static SGAxisElement.AxisProperties differentNumberVisible() {
    final SGAxisElement.AxisProperties p = filled();
    p.numberVisible = false;
    return p;
  }

  private static SGAxisElement.AxisProperties differentTickMarkVisible() {
    final SGAxisElement.AxisProperties p = filled();
    p.tickMarkVisible = false;
    return p;
  }

  @Test
  void floatDifferencesBreakEquality() {
    final SGAxisElement.AxisProperties width = filled();
    width.axisLineWidth = 9.0f;
    assertFalse(filled().equals(width));

    final SGAxisElement.AxisProperties angle = filled();
    angle.numberAngle = 45.0f;
    assertFalse(filled().equals(angle));

    final SGAxisElement.AxisProperties tickLen = filled();
    tickLen.tickMarkLength = 9.0f;
    assertFalse(filled().equals(tickLen));
  }

  @Test
  void colorDifferencesBreakEquality() {
    final SGAxisElement.AxisProperties line = filled();
    line.axisLineColor = Color.MAGENTA;
    assertFalse(filled().equals(line));

    final SGAxisElement.AxisProperties title = filled();
    title.titleFontColor = Color.CYAN;
    assertFalse(filled().equals(title));

    final SGAxisElement.AxisProperties tick = filled();
    tick.tickMarkColor = Color.DARK_GRAY;
    assertFalse(filled().equals(tick));
  }

  @Test
  void stringDifferencesBreakEquality() {
    final SGAxisElement.AxisProperties title = filled();
    title.titleText = "other";
    assertFalse(filled().equals(title));

    final SGAxisElement.AxisProperties date = filled();
    date.numberDateFormat = "yyyy-MM";
    assertFalse(filled().equals(date));
  }

  @Test
  void axisValueAndStepDifferencesBreakEquality() {
    final SGAxisElement.AxisProperties min = filled();
    min.minValue = new SGAxisDoubleValue(-1.0);
    assertFalse(filled().equals(min));

    final SGAxisElement.AxisProperties step = filled();
    step.stepValue = new SGAxisDoubleStepValue(2.0);
    assertFalse(filled().equals(step));
  }

  @Test
  void intAndExponentDifferencesBreakEquality() {
    final SGAxisElement.AxisProperties scale = filled();
    scale.scaleType = 2;
    assertFalse(filled().equals(scale));

    final SGAxisElement.AxisProperties exponent = filled();
    exponent.exponent = 5;
    assertFalse(filled().equals(exponent));
  }

  @Test
  void foreignTypeIsNotEqual() {
    assertFalse(filled().equals(new SGProperties() {}));
    assertFalse(filled().equals(null));
  }

  @Test
  void defaultPropertiesDifferFromFilled() {
    assertFalse(new SGAxisElement.AxisProperties().equals(filled()));
  }
}
