package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.figure.SGFigureElementTimingLine.TimingLineProperties;
import org.junit.jupiter.api.Test;

/** Unit tests of the timing line properties. */
class SGFigureElementTimingLinePropertiesTest {

  private static TimingLineProperties filled(final SGAxis axis) {
    final TimingLineProperties p = new TimingLineProperties();
    p.setValue(2.5);
    p.setAxis(axis);
    p.setAnchored(true);
    p.setLineWidth(1.5f);
    p.setLineType(1);
    p.setColor(Color.RED);
    return p;
  }

  @Test
  void defaultsAreZeroAndNull() {
    final TimingLineProperties p = new TimingLineProperties();
    assertEquals(0.0, p.getValue());
    assertNull(p.getAxis());
    assertFalse(p.getAnchored());
  }

  @Test
  void settersAndGettersRoundTripTheFields() {
    final SGAxis axis = new SGAxis(0.0, 1.0);
    final TimingLineProperties p = filled(axis);
    assertEquals(2.5, p.getValue());
    assertEquals(axis, p.getAxis());
    assertTrue(p.getAnchored());
    assertEquals(1.5f, p.getLineWidth());
    assertEquals(1, p.getLineType());
    assertEquals(Color.RED, p.getColor());
  }

  @Test
  void equalsMatchesTheSameFields() {
    final SGAxis axis = new SGAxis(0.0, 1.0);
    assertTrue(filled(axis).equals(filled(axis)));
    assertFalse(filled(axis).equals(new Object()));
  }

  @Test
  void equalsMatchesNullAxis() {
    final TimingLineProperties first = new TimingLineProperties();
    final TimingLineProperties second = new TimingLineProperties();
    assertTrue(first.equals(second));
  }

  @Test
  void equalsDistinguishesTheFields() {
    final SGAxis axis = new SGAxis(0.0, 1.0);

    final TimingLineProperties value = filled(axis);
    value.setValue(3.5);
    assertFalse(filled(axis).equals(value));

    // SGAxis has identity equality, so a different instance is not equal
    final TimingLineProperties otherAxis = filled(new SGAxis(0.0, 1.0));
    assertFalse(filled(axis).equals(otherAxis));

    final TimingLineProperties anchored = filled(axis);
    anchored.setAnchored(false);
    assertFalse(filled(axis).equals(anchored));
  }

  @Test
  void equalsDistinguishesTheInheritedLineFields() {
    final SGAxis axis = new SGAxis(0.0, 1.0);
    final TimingLineProperties width = filled(axis);
    width.setLineWidth(2.0f);
    assertFalse(filled(axis).equals(width));

    final TimingLineProperties color = filled(axis);
    color.setColor(Color.BLUE);
    assertFalse(filled(axis).equals(color));
  }
}
