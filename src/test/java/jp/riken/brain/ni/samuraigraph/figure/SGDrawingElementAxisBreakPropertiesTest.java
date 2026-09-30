package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import org.junit.jupiter.api.Test;

/** Unit tests of the axis break symbol properties. */
class SGDrawingElementAxisBreakPropertiesTest {

  private static SGDrawingElementAxisBreak.AxisBreakSymbolProperties create(
      final float length, final Color lineColor, final Color innerColor) {
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties p =
        new SGDrawingElementAxisBreak.AxisBreakSymbolProperties();
    p.setX(10.0f);
    p.setY(20.0f);
    p.setLength(length);
    p.setInterval(4.0f);
    p.setDistortion(0.5f);
    p.setAngle(90.0f);
    p.setLineWidth(1.5f);
    p.setLineColor(lineColor);
    p.setInnerColor(innerColor);
    p.setHorizontal(true);
    return p;
  }

  @Test
  void settersAndGettersRoundTripTheFields() {
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties p =
        new SGDrawingElementAxisBreak.AxisBreakSymbolProperties();
    p.setX(10.0f);
    p.setY(20.0f);
    p.setLength(30.0f);
    p.setInterval(4.0f);
    p.setDistortion(0.5f);
    p.setAngle(90.0f);
    p.setLineWidth(1.5f);
    p.setLineColor(Color.RED);
    p.setInnerColor(Color.BLUE);
    p.setHorizontal(false);
    assertEquals(10.0f, p.getX());
    assertEquals(20.0f, p.getY());
    assertEquals(30.0f, p.getLength());
    assertEquals(4.0f, p.getInterval());
    assertEquals(0.5f, p.getDistortion());
    assertEquals(90.0f, p.getAngle());
    assertEquals(1.5f, p.getLineWidth());
    assertEquals(Color.RED, p.getLineColor());
    assertEquals(Color.BLUE, p.getInnerColor());
    assertFalse(p.isHorizontal());
  }

  @Test
  void defaultsAreZeroAndColorsAreNull() {
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties p =
        new SGDrawingElementAxisBreak.AxisBreakSymbolProperties();
    assertEquals(0.0f, p.getX());
    assertEquals(0.0f, p.getY());
    assertNull(p.getLineColor());
    assertNull(p.getInnerColor());
    assertTrue(p.isHorizontal());
  }

  @Test
  void equalsMatchesTheSameFields() {
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties first =
        create(30.0f, Color.RED, Color.BLUE);
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties second =
        create(30.0f, Color.RED, Color.BLUE);
    assertEquals(first, second);
    assertFalse(first.equals(new Object()));
  }

  @Test
  void equalsMatchesNullColors() {
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties first =
        new SGDrawingElementAxisBreak.AxisBreakSymbolProperties();
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties second =
        new SGDrawingElementAxisBreak.AxisBreakSymbolProperties();
    assertEquals(first, second);
  }

  @Test
  void equalsDistinguishesTheFields() {
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties base =
        create(30.0f, Color.RED, Color.BLUE);
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties diffLength =
        create(31.0f, Color.RED, Color.BLUE);
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties diffLineColor =
        create(30.0f, Color.RED, Color.GREEN);
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties diffInnerColor =
        create(30.0f, Color.RED, Color.BLACK);
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties diffHorizontal =
        create(30.0f, Color.RED, Color.BLUE);
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties diffX =
        create(30.0f, Color.RED, Color.BLUE);
    diffX.setX(11.0f);
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties diffY =
        create(30.0f, Color.RED, Color.BLUE);
    diffY.setY(21.0f);
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties diffInterval =
        create(30.0f, Color.RED, Color.BLUE);
    diffInterval.setInterval(5.0f);
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties diffDistortion =
        create(30.0f, Color.RED, Color.BLUE);
    diffDistortion.setDistortion(0.6f);
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties diffAngle =
        create(30.0f, Color.RED, Color.BLUE);
    diffAngle.setAngle(100.0f);
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties diffLineWidth =
        create(30.0f, Color.RED, Color.BLUE);
    diffLineWidth.setLineWidth(2.0f);
    diffHorizontal.setHorizontal(false);
    assertNotEquals(base, diffLength);
    assertNotEquals(base, diffLineColor);
    assertNotEquals(base, diffInnerColor);
    assertNotEquals(base, diffHorizontal);
    assertNotEquals(base, diffX);
    assertNotEquals(base, diffY);
    assertNotEquals(base, diffInterval);
    assertNotEquals(base, diffDistortion);
    assertNotEquals(base, diffAngle);
    assertNotEquals(base, diffLineWidth);
  }

  @Test
  void equalsDistinguishesTheVisibility() {
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties base =
        create(30.0f, Color.RED, Color.BLUE);
    final SGDrawingElementAxisBreak.AxisBreakSymbolProperties diffVisible =
        create(30.0f, Color.RED, Color.BLUE);
    diffVisible.setVisible(true);
    assertNotEquals(base, diffVisible);
  }
}
