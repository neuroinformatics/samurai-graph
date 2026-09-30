package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementSignificantDifference.SigDiffProperties;
import jp.riken.brain.ni.samuraigraph.figure.SGFigureElementSignificantDifference.SigDiffPropertiesWithAxes;
import org.junit.jupiter.api.Test;

/** Unit tests of the significant difference properties. */
class SGFigureElementSignificantDifferencePropertiesTest {

  private static SigDiffProperties filledBase() {
    final SigDiffProperties p = new SigDiffProperties();
    p.mText = "p<0.05";
    p.mLineVisible = true;
    p.mColor = Color.RED;
    p.mFontSize = 12.0f;
    return p;
  }

  private static SigDiffPropertiesWithAxes filled(final SGAxis xAxis, final SGAxis yAxis) {
    final SigDiffPropertiesWithAxes p = new SigDiffPropertiesWithAxes();
    p.setLeftXValue(1.0);
    p.setRightXValue(2.0);
    p.setLeftYValue(3.0);
    p.setRightYValue(4.0);
    p.setHorizontalYValue(5.0);
    p.setXAxis(xAxis);
    p.setYAxis(yAxis);
    p.setAnchored(true);
    return p;
  }

  @Test
  void basePropertiesCompareByValue() {
    assertTrue(filledBase().equals(filledBase()));
    assertFalse(filledBase().equals(new Object()));
  }

  @Test
  void basePropertiesDistinguishTheFields() {
    final SigDiffProperties text = filledBase();
    text.mText = "p<0.01";
    assertFalse(filledBase().equals(text));

    final SigDiffProperties visible = filledBase();
    visible.mLineVisible = false;
    assertFalse(filledBase().equals(visible));

    final SigDiffProperties color = filledBase();
    color.mColor = Color.BLUE;
    assertFalse(filledBase().equals(color));

    final SigDiffProperties fontSize = filledBase();
    fontSize.mFontSize = 13.0f;
    assertFalse(filledBase().equals(fontSize));
  }

  @Test
  void basePropertiesMatchNullText() {
    final SigDiffProperties first = new SigDiffProperties();
    final SigDiffProperties second = new SigDiffProperties();
    assertTrue(first.equals(second));
  }

  @Test
  void withAxesSettersAndGettersRoundTrip() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final SigDiffPropertiesWithAxes p = filled(xAxis, yAxis);
    assertEquals(1.0, p.getLeftXValue());
    assertEquals(2.0, p.getRightXValue());
    assertEquals(3.0, p.getLeftYValue());
    assertEquals(4.0, p.getRightYValue());
    assertEquals(5.0, p.getHorizontalYValue());
    assertEquals(xAxis, p.getXAxis());
    assertEquals(yAxis, p.getYAxis());
    assertTrue(p.getAnchored());
  }

  @Test
  void withAxesAxisSettersRejectNull() {
    final SigDiffPropertiesWithAxes p = new SigDiffPropertiesWithAxes();
    assertThrows(IllegalArgumentException.class, () -> p.setXAxis(null));
    assertThrows(IllegalArgumentException.class, () -> p.setYAxis(null));
  }

  @Test
  void withAxesCompareByValue() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    assertTrue(filled(xAxis, yAxis).equals(filled(xAxis, yAxis)));
    assertFalse(filled(xAxis, yAxis).equals(new Object()));
  }

  @Test
  void withAxesMatchNullAxesWithoutThrowing() {
    // the axes default to null; equals must not dereference them
    final SigDiffPropertiesWithAxes first = new SigDiffPropertiesWithAxes();
    final SigDiffPropertiesWithAxes second = new SigDiffPropertiesWithAxes();
    assertTrue(first.equals(second));
  }

  @Test
  void withAxesDistinguishTheFields() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);

    final SigDiffPropertiesWithAxes leftX = filled(xAxis, yAxis);
    leftX.setLeftXValue(9.0);
    assertFalse(filled(xAxis, yAxis).equals(leftX));

    final SigDiffPropertiesWithAxes rightX = filled(xAxis, yAxis);
    rightX.setRightXValue(9.0);
    assertFalse(filled(xAxis, yAxis).equals(rightX));

    final SigDiffPropertiesWithAxes leftY = filled(xAxis, yAxis);
    leftY.setLeftYValue(9.0);
    assertFalse(filled(xAxis, yAxis).equals(leftY));

    final SigDiffPropertiesWithAxes rightY = filled(xAxis, yAxis);
    rightY.setRightYValue(9.0);
    assertFalse(filled(xAxis, yAxis).equals(rightY));

    final SigDiffPropertiesWithAxes horizontal = filled(xAxis, yAxis);
    horizontal.setHorizontalYValue(9.0);
    assertFalse(filled(xAxis, yAxis).equals(horizontal));

    // SGAxis has identity equality, so a different instance is not equal
    final SigDiffPropertiesWithAxes otherXAxis = filled(new SGAxis(0.0, 1.0), yAxis);
    assertFalse(filled(xAxis, yAxis).equals(otherXAxis));

    final SigDiffPropertiesWithAxes otherYAxis = filled(xAxis, new SGAxis(0.0, 2.0));
    assertFalse(filled(xAxis, yAxis).equals(otherYAxis));

    final SigDiffPropertiesWithAxes anchored = filled(xAxis, yAxis);
    anchored.setAnchored(false);
    assertFalse(filled(xAxis, yAxis).equals(anchored));
  }

  @Test
  void baseFieldsStillBreakWithAxesEquality() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final SigDiffPropertiesWithAxes text = filled(xAxis, yAxis);
    text.mText = "p<0.01";
    assertFalse(filled(xAxis, yAxis).equals(text));
  }
}
