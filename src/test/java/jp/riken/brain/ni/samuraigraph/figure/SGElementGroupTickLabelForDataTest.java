package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import org.junit.jupiter.api.Test;

/** Unit tests for the tick label group for data with a stub subclass. */
class SGElementGroupTickLabelForDataTest {

  private static class TestTickLabelGroup extends SGElementGroupTickLabelForData {

    TestTickLabelGroup() {
      super(null);
    }

    @Override
    public boolean updateLocation() {
      return true;
    }

    @Override
    public boolean setFontSize(float size, String unit) {
      return this.setFontSize(size);
    }

    @Override
    public SGDrawingElement createDrawingElementInstance(int index) {
      return null;
    }

    @Override
    public boolean paintElement(Graphics2D g2d, Rectangle2D clipRect) {
      return true;
    }

    @Override
    public String getTagName() {
      return "TickLabel";
    }
  }

  @Test
  void defaultsAreAppliedAtConstruction() {
    TestTickLabelGroup group = new TestTickLabelGroup();
    assertTrue(group.isVisible());
    assertEquals(SGSXYDataConstants.DEFAULT_TICK_LABEL_FONT_NAME, group.getFontName());
    assertEquals(Font.PLAIN, group.getFontStyle());
    assertEquals(SGSXYDataConstants.DEFAULT_TICK_LABEL_FONT_SIZE, group.getFontSize(), 1.0e-6f);
    assertEquals(SGSXYDataConstants.DEFAULT_TICK_LABEL_FONT_COLOR, group.getColor());
    assertEquals(SGSXYDataConstants.DEFAULT_TICK_LABEL_ANGLE, group.getAngle(), 1.0e-6f);
    assertTrue(group.hasHorizontalAlignment());
  }

  @Test
  void fontSizeInUnitIsApplied() {
    TestTickLabelGroup group = new TestTickLabelGroup();
    assertTrue(group.setFontSize(14.0f, "pt"));
    assertEquals(14.0f, group.getFontSize(), 1.0e-6f);
  }

  @Test
  void fontAttributesAreUpdated() {
    TestTickLabelGroup group = new TestTickLabelGroup();
    assertTrue(group.setFontName("SansSerif"));
    assertEquals("SansSerif", group.getFontName());
    assertTrue(group.setFontStyle(Font.BOLD));
    assertEquals(Font.BOLD, group.getFontStyle());
    assertTrue(group.setHorizontalAlignment(false));
    assertFalse(group.hasHorizontalAlignment());
  }

  @Test
  void angleColorAndDecimalsRoundTrip() {
    TestTickLabelGroup group = new TestTickLabelGroup();
    assertTrue(group.setAngle(45.0f));
    assertEquals(45.0f, group.getAngle(), 1.0e-6f);
    assertTrue(group.setColor(Color.RED));
    assertEquals(Color.RED, group.getColor());
    assertTrue(group.setDecimalPlaces(3));
    assertEquals(3, group.getDecimalPlaces());
    assertTrue(group.setExponent(-5));
    assertEquals(-5, group.getExponent());
    assertTrue(group.updateLocation());
  }
}
