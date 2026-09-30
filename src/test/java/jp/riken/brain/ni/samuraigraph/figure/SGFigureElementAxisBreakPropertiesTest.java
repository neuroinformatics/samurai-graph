package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.figure.SGFigureElementAxisBreak.AxisBreakSymbolWithAxesProperties;
import org.junit.jupiter.api.Test;

/** Unit tests of the axis break symbol properties with axes. */
class SGFigureElementAxisBreakPropertiesTest {

  private static void setAxis(
      final AxisBreakSymbolWithAxesProperties p, final String field, final SGAxis axis)
      throws Exception {
    // the axes have no public setter, so the test reaches into the field
    final Field f = AxisBreakSymbolWithAxesProperties.class.getDeclaredField(field);
    f.setAccessible(true);
    f.set(p, axis);
  }

  @Test
  void defaultsAreZeroAndNull() {
    final AxisBreakSymbolWithAxesProperties p = new AxisBreakSymbolWithAxesProperties();
    assertEquals(0.0, p.getXValue());
    assertEquals(0.0, p.getYValue());
    assertNull(p.getXAxis());
    assertNull(p.getYAxis());
    assertFalse(p.getAnchored());
  }

  @Test
  void settersAndGettersRoundTripTheFields() throws Exception {
    final AxisBreakSymbolWithAxesProperties p = new AxisBreakSymbolWithAxesProperties();
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    p.setXValue(1.5);
    p.setYValue(2.5);
    p.setAnchored(true);
    p.setLength(30.0f);
    setAxis(p, "mXAxis", xAxis);
    setAxis(p, "mYAxis", yAxis);
    assertEquals(1.5, p.getXValue());
    assertEquals(2.5, p.getYValue());
    assertTrue(p.getAnchored());
    assertEquals(30.0f, p.getLength());
    assertEquals(xAxis, p.getXAxis());
    assertEquals(yAxis, p.getYAxis());
  }

  @Test
  void equalsMatchesTheSameFields() throws Exception {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final AxisBreakSymbolWithAxesProperties first = new AxisBreakSymbolWithAxesProperties();
    final AxisBreakSymbolWithAxesProperties second = new AxisBreakSymbolWithAxesProperties();
    first.setXValue(1.5);
    second.setXValue(1.5);
    first.setAnchored(true);
    second.setAnchored(true);
    setAxis(first, "mXAxis", xAxis);
    setAxis(second, "mXAxis", xAxis);
    setAxis(first, "mYAxis", yAxis);
    setAxis(second, "mYAxis", yAxis);
    assertEquals(first, second);
    assertFalse(first.equals(new Object()));
  }

  @Test
  void equalsMatchesNullAxesWithoutThrowing() {
    // the axes default to null; equals must not dereference them
    final AxisBreakSymbolWithAxesProperties first = new AxisBreakSymbolWithAxesProperties();
    final AxisBreakSymbolWithAxesProperties second = new AxisBreakSymbolWithAxesProperties();
    assertEquals(first, second);
  }

  @Test
  void equalsDistinguishesTheFields() throws Exception {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final AxisBreakSymbolWithAxesProperties base = new AxisBreakSymbolWithAxesProperties();
    base.setXValue(1.5);
    setAxis(base, "mXAxis", xAxis);
    setAxis(base, "mYAxis", yAxis);

    final AxisBreakSymbolWithAxesProperties diffXValue = new AxisBreakSymbolWithAxesProperties();
    diffXValue.setXValue(2.5);
    final AxisBreakSymbolWithAxesProperties diffXAxis = new AxisBreakSymbolWithAxesProperties();
    diffXAxis.setXValue(1.5);
    // SGAxis has identity equality, so a different instance is not equal
    setAxis(diffXAxis, "mXAxis", new SGAxis(0.0, 1.0));
    setAxis(diffXAxis, "mYAxis", yAxis);
    final AxisBreakSymbolWithAxesProperties diffYValue = new AxisBreakSymbolWithAxesProperties();
    diffYValue.setXValue(1.5);
    diffYValue.setYValue(3.5);
    final AxisBreakSymbolWithAxesProperties diffYAxis = new AxisBreakSymbolWithAxesProperties();
    diffYAxis.setXValue(1.5);
    setAxis(diffYAxis, "mXAxis", xAxis);
    setAxis(diffYAxis, "mYAxis", new SGAxis(0.0, 2.0));
    final AxisBreakSymbolWithAxesProperties diffAnchored = new AxisBreakSymbolWithAxesProperties();
    diffAnchored.setXValue(1.5);
    diffAnchored.setAnchored(true);
    setAxis(diffAnchored, "mXAxis", xAxis);
    setAxis(diffAnchored, "mYAxis", yAxis);
    assertNotEquals(base, diffXValue);
    assertNotEquals(base, diffYValue);
    assertNotEquals(base, diffXAxis);
    assertNotEquals(base, diffYAxis);
    assertNotEquals(base, diffAnchored);
  }
}
