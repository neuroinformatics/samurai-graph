package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.HashMap;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleValue;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap.ColorMapProperties;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.Test;

/** Unit tests of the color bar properties. */
class SGColorBarAxisColorBarPropertiesTest {

  private static ColorBarProperties filled(final SGAxis xAxis, final SGAxis yAxis) {
    final ColorBarProperties p = new ColorBarProperties();
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
    p.xAxis = xAxis;
    p.yAxis = yAxis;
    p.x = 10.0f;
    p.y = 20.0f;
    p.barWidth = 3.0f;
    p.barLength = 40.0f;
    p.direction = "vertical";
    p.frameLineWidth = 1.5f;
    p.colorBarStyle = "simple";
    p.colorMapPropertiesMap = new HashMap<String, ColorMapProperties>();
    p.colorMapPropertiesMap.put("map", new ColorMapProperties());
    return p;
  }

  @Test
  void defaultsAreZeroAndNull() {
    final ColorBarProperties p = new ColorBarProperties();
    assertNull(p.xAxis);
    assertNull(p.yAxis);
    assertNull(p.direction);
    assertNull(p.colorBarStyle);
    assertEquals(0.0f, p.x);
    assertEquals(0.0f, p.barWidth);
    assertTrue(p.colorMapPropertiesMap.isEmpty());
  }

  @Test
  void equalPropertiesCompareEqual() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    assertTrue(filled(xAxis, yAxis).equals(filled(xAxis, yAxis)));
    assertFalse(filled(xAxis, yAxis).equals(new SGProperties() {}));
    assertFalse(filled(xAxis, yAxis).equals(new SGAxisElement.AxisProperties()));
  }

  @Test
  void geometryDifferencesBreakEquality() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final ColorBarProperties x = filled(xAxis, yAxis);
    x.x = 11.0f;
    assertFalse(filled(xAxis, yAxis).equals(x));

    final ColorBarProperties y = filled(xAxis, yAxis);
    y.y = 21.0f;
    assertFalse(filled(xAxis, yAxis).equals(y));

    final ColorBarProperties width = filled(xAxis, yAxis);
    width.barWidth = 4.0f;
    assertFalse(filled(xAxis, yAxis).equals(width));

    final ColorBarProperties length = filled(xAxis, yAxis);
    length.barLength = 41.0f;
    assertFalse(filled(xAxis, yAxis).equals(length));

    final ColorBarProperties frame = filled(xAxis, yAxis);
    frame.frameLineWidth = 2.0f;
    assertFalse(filled(xAxis, yAxis).equals(frame));
  }

  @Test
  void stringAndAxisDifferencesBreakEquality() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final ColorBarProperties direction = filled(xAxis, yAxis);
    direction.direction = "horizontal";
    assertFalse(filled(xAxis, yAxis).equals(direction));

    final ColorBarProperties style = filled(xAxis, yAxis);
    style.colorBarStyle = "detailed";
    assertFalse(filled(xAxis, yAxis).equals(style));

    // SGAxis has identity equality, so a different instance is not equal
    final ColorBarProperties otherXAxis = filled(new SGAxis(0.0, 1.0), yAxis);
    assertFalse(filled(xAxis, yAxis).equals(otherXAxis));

    final ColorBarProperties otherYAxis = filled(xAxis, new SGAxis(0.0, 2.0));
    assertFalse(filled(xAxis, yAxis).equals(otherYAxis));
  }

  @Test
  void baseAxisFieldDifferencesBreakEquality() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final ColorBarProperties width = filled(xAxis, yAxis);
    width.axisLineWidth = 9.0f;
    assertFalse(filled(xAxis, yAxis).equals(width));

    final ColorBarProperties title = filled(xAxis, yAxis);
    title.titleText = "other";
    assertFalse(filled(xAxis, yAxis).equals(title));
  }

  @Test
  void colorMapDifferencesBreakEquality() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final ColorBarProperties p = filled(xAxis, yAxis);
    p.colorMapPropertiesMap = new HashMap<String, ColorMapProperties>();
    assertFalse(filled(xAxis, yAxis).equals(p));

    final ColorBarProperties renamed = filled(xAxis, yAxis);
    renamed.colorMapPropertiesMap = new HashMap<String, ColorMapProperties>();
    renamed.colorMapPropertiesMap.put("other", new ColorMapProperties());
    assertFalse(filled(xAxis, yAxis).equals(renamed));
  }

  @Test
  void copyProducesAnEqualDeepCopyOfTheColorMap() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final ColorBarProperties original = filled(xAxis, yAxis);
    final ColorBarProperties copy = (ColorBarProperties) original.copy();
    assertNotSame(copy, original);
    assertEquals(original, copy);
    assertNotSame(copy.colorMapPropertiesMap, original.colorMapPropertiesMap);
    assertNotSame(copy.colorMapPropertiesMap.get("map"), original.colorMapPropertiesMap.get("map"));
  }

  @Test
  void disposeClearsTheAxesAndTheColorMap() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final ColorBarProperties p = filled(xAxis, yAxis);
    p.dispose();
    assertTrue(p.isDisposed());
    assertNull(p.xAxis);
    assertNull(p.yAxis);
    assertNull(p.colorMapPropertiesMap);
  }
}
