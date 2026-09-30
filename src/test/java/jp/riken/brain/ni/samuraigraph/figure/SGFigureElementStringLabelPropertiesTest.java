package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.figure.SGFigureElementString.LabelProperties;
import org.junit.jupiter.api.Test;

/** Unit tests of the figure-element string label properties. */
class SGFigureElementStringLabelPropertiesTest {

  private static LabelProperties create(
      final float x, final float y, final String text, final SGAxis xAxis, final SGAxis yAxis) {
    final LabelProperties p = new LabelProperties();
    p.setX(x);
    p.setY(y);
    p.setText(text);
    p.setXAxis(xAxis);
    p.setYAxis(yAxis);
    p.setFontName("Dialog");
    p.setFontSize(12.0f);
    p.setFontStyle(0);
    p.setAngle(0.0f);
    p.setColor(Color.BLACK);
    return p;
  }

  @Test
  void gettersReturnTheSetFields() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final LabelProperties p = create(5.0f, 6.0f, "label", xAxis, yAxis);
    assertEquals(5.0f, p.getX());
    assertEquals(6.0f, p.getY());
    assertEquals("label", p.getText());
    assertEquals(xAxis, p.getXAxis());
    assertEquals(yAxis, p.getYAxis());
    assertEquals("Dialog", p.getFontName());
  }

  @Test
  void defaultsAreZeroAndNull() {
    final LabelProperties p = new LabelProperties();
    assertEquals(0.0f, p.getX());
    assertEquals(0.0f, p.getY());
    assertNull(p.getText());
    assertNull(p.getXAxis());
    assertNull(p.getYAxis());
  }

  @Test
  void equalsMatchesTheSameFields() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final LabelProperties first = create(5.0f, 6.0f, "label", xAxis, yAxis);
    final LabelProperties second = create(5.0f, 6.0f, "label", xAxis, yAxis);
    assertEquals(first, second);
    assertFalse(first.equals(new Object()));
  }

  @Test
  void equalsMatchesNullAxesAndText() {
    final LabelProperties first = create(1.0f, 2.0f, null, null, null);
    final LabelProperties second = create(1.0f, 2.0f, null, null, null);
    assertEquals(first, second);
  }

  @Test
  void equalsDistinguishesTheFields() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final LabelProperties base = create(5.0f, 6.0f, "label", xAxis, yAxis);
    final LabelProperties diffX = create(7.0f, 6.0f, "label", xAxis, yAxis);
    final LabelProperties diffY = create(5.0f, 8.0f, "label", xAxis, yAxis);
    final LabelProperties diffText = create(5.0f, 6.0f, "other", xAxis, yAxis);
    // SGAxis has identity equality, so a different instance is not equal
    final LabelProperties diffXAxis = create(5.0f, 6.0f, "label", new SGAxis(0.0, 1.0), yAxis);
    final LabelProperties diffYAxis = create(5.0f, 6.0f, "label", xAxis, new SGAxis(0.0, 2.0));
    assertNotEquals(base, diffX);
    assertNotEquals(base, diffY);
    assertNotEquals(base, diffText);
    assertNotEquals(base, diffXAxis);
    assertNotEquals(base, diffYAxis);
  }

  @Test
  void equalsDistinguishesTheInheritedFontFields() {
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final LabelProperties base = create(5.0f, 6.0f, "label", xAxis, yAxis);
    final LabelProperties diffSize = create(5.0f, 6.0f, "label", xAxis, yAxis);
    diffSize.setFontSize(13.0f);
    final LabelProperties diffColor = create(5.0f, 6.0f, "label", xAxis, yAxis);
    diffColor.setColor(Color.BLUE);
    assertNotEquals(base, diffSize);
    assertNotEquals(base, diffColor);
  }

  @Test
  void hashCodeIsNotOverriddenSoEqualPropertiesDoNotHashEqually() {
    // the class does not override hashCode, so two equal instances keep
    // their identity-based hash codes
    final SGAxis xAxis = new SGAxis(0.0, 1.0);
    final SGAxis yAxis = new SGAxis(0.0, 2.0);
    final LabelProperties first = create(5.0f, 6.0f, "label", xAxis, yAxis);
    final LabelProperties second = create(5.0f, 6.0f, "label", xAxis, yAxis);
    assertEquals(first, second);
    assertNotEquals(first.hashCode(), second.hashCode());
  }
}
