package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the bar element group with a stub subclass. */
class SGElementGroupBarTest {

  private static class TestBarGroup extends SGElementGroupBar {

    TestBarGroup() {
      super();
    }

    @Override
    public float getX(int index) {
      return 0.0f;
    }

    @Override
    public float getY(int index) {
      return 0.0f;
    }

    @Override
    public boolean setEdgeLineWidth(float lw, String unit) {
      return this.setEdgeLineWidth(lw);
    }
  }

  private SGElementGroupBar group;

  @BeforeEach
  void createGroup() {
    this.group = new TestBarGroup();
  }

  @Test
  void baselineAndWidthValuesAreStored() {
    assertTrue(this.group.setBaselineValue(1.5));
    assertEquals(1.5, this.group.getBaselineValue());
    assertFalse(this.group.setWidthValue(-1.0));
    assertTrue(this.group.setWidthValue(0.4));
    assertEquals(0.4, this.group.getWidthValue(), 1.0e-6);
  }

  @Test
  void offsetsAndIntervalAreStored() {
    assertEquals(0.0, this.group.getOffsetX(), 0.0);
    assertEquals(0.0, this.group.getInterval(), 0.0);
    assertTrue(this.group.setOffsetX(0.2));
    assertTrue(this.group.setOffsetY(0.3));
    assertTrue(this.group.setInterval(0.4));
    assertEquals(0.2, this.group.getOffsetX(), 0.0);
    assertEquals(0.3, this.group.getOffsetY(), 0.0);
    assertEquals(0.4, this.group.getInterval(), 0.0);
  }

  @Test
  void verticalFlagCanBeToggled() {
    assertTrue(this.group.setVertical(true));
    assertTrue(this.group.isVertical());
    assertTrue(this.group.setVertical(false));
    assertFalse(this.group.isVertical());
  }

  @Test
  void edgeLineAttributesAreValidated() {
    assertTrue(this.group.setEdgeLineWidth(1.0f));
    assertEquals(1.0f, this.group.getEdgeLineWidth(), 1.0e-6f);
    assertThrows(IllegalArgumentException.class, () -> this.group.setEdgeLineWidth(-1.0f));
    assertThrows(IllegalArgumentException.class, () -> this.group.setEdgeLineColor((Color) null));
    assertTrue(this.group.setEdgeLineColor(Color.RED));
    assertEquals(Color.RED, this.group.getEdgeLineColor());
    assertTrue(this.group.setEdgeLineVisible(false));
    assertFalse(this.group.isEdgeLineVisible());
  }

  @Test
  void sizeAndInnerPaintAreManaged() {
    assertTrue(this.group.setRectangleWidth(0.4f));
    assertTrue(this.group.setRectangleHeight(0.5f));
    SGFillPaint paint = new SGFillPaint();
    assertTrue(paint.setColor(Color.RED));
    assertTrue(this.group.setInnerPaint(paint));
    assertEquals(1.0f, this.group.getTransparency(), 1.0e-6f);
  }

  @Test
  void propertiesRoundTripPreservesBarAttributes() {
    SGElementGroupBar group = this.group;
    group.setBaselineValue(2.5);
    assertTrue(group.setWidthValue(0.3));
    group.setVertical(false);
    group.setInterval(0.4);
    group.setOffsetX(0.1);
    group.setOffsetY(0.3);
    group.setEdgeLineWidth(1.5f);
    assertTrue(group.setEdgeLineColor(java.awt.Color.RED));
    group.setEdgeLineVisible(true);
    SGFillPaint inner = new SGFillPaint();
    inner.setColor(Color.BLUE);
    group.setInnerPaint(inner);

    SGProperties p = group.getProperties();
    assertNotNull(p);

    SGElementGroupBar restored = new TestBarGroup();
    assertTrue(restored.setProperties(p));
    assertEquals(group.getBaselineValue(), restored.getBaselineValue(), 1.0e-6);
    assertEquals(group.getWidthValue(), restored.getWidthValue(), 1.0e-6);
    assertEquals(group.isVertical(), restored.isVertical());
    assertEquals(group.getInterval(), restored.getInterval(), 1.0e-6);
    assertEquals(group.getOffsetX(), restored.getOffsetX(), 1.0e-6);
    assertEquals(group.getOffsetY(), restored.getOffsetY(), 1.0e-6);
    assertEquals(group.getEdgeLineWidth(), restored.getEdgeLineWidth(), 1.0e-6f);
    assertEquals(group.getEdgeLineColor(), restored.getEdgeLineColor());
    assertEquals(group.isEdgeLineVisible(), restored.isEdgeLineVisible());
    assertEquals(group.getInnerPaint().getPaint(null), restored.getInnerPaint().getPaint(null));
  }

  @Test
  void foreignPropertiesAreRejected() {
    assertFalse(this.group.setProperties(new SGProperties() {}));
    assertFalse(this.group.getProperties(null));
  }

  @Test
  void disposeClearsPaintState() {
    this.group.setEdgeLineColor(Color.RED);
    this.group.dispose();
    assertTrue(this.group.isDisposed());
    assertTrue(this.group.getEdgeLineColor() == null);
  }
}
