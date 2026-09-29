package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Headless unit tests of the abstract bar drawing element. */
class SGDrawingElementBarTest {

  /** A minimal bar stub remembering the plain state. */
  private static class TestBar extends SGDrawingElementBar {

    private float x;

    private float y;

    private float width;

    private float height;

    private float edgeLineWidth;

    private int edgeLineType = SGLineConstants.LINE_TYPE_SOLID;

    private Color edgeLineColor;

    private boolean edgeLineVisible = true;

    private SGIPaint innerPaint = new SGFillPaint();

    private float transparency;

    private float magnification = 1.0f;

    private double baselineValue;

    private double widthValue;

    private boolean vertical = true;

    private double interval;

    TestBar() {
      super();
    }

    @Override
    protected SGStroke getStroke() {
      return new SGStroke();
    }

    @Override
    public double getBaselineValue() {
      return this.baselineValue;
    }

    @Override
    public boolean setBaselineValue(double value) {
      this.baselineValue = value;
      return true;
    }

    @Override
    public double getWidthValue() {
      return this.widthValue;
    }

    @Override
    public boolean setWidthValue(double value) {
      this.widthValue = value;
      return true;
    }

    @Override
    public boolean isVertical() {
      return this.vertical;
    }

    @Override
    public boolean setVertical(boolean b) {
      this.vertical = b;
      return true;
    }

    @Override
    public double getInterval() {
      return this.interval;
    }

    @Override
    public boolean setInterval(double value) {
      this.interval = value;
      return true;
    }

    @Override
    public float getX() {
      return this.x;
    }

    @Override
    public float getY() {
      return this.y;
    }

    @Override
    public float getWidth() {
      return this.width;
    }

    @Override
    public float getHeight() {
      return this.height;
    }

    @Override
    public float getEdgeLineWidth() {
      return this.edgeLineWidth;
    }

    @Override
    public Color getEdgeLineColor() {
      return this.edgeLineColor;
    }

    @Override
    public boolean isEdgeLineVisible() {
      return this.edgeLineVisible;
    }

    @Override
    public SGIPaint getInnerPaint() {
      return this.innerPaint;
    }

    @Override
    public int getEdgeLineType() {
      return this.edgeLineType;
    }

    @Override
    public float getTransparency() {
      return this.transparency;
    }

    @Override
    public boolean setEdgeLineWidth(float width) {
      this.edgeLineWidth = width;
      return true;
    }

    @Override
    public boolean setEdgeLineType(int type) {
      this.edgeLineType = type;
      return true;
    }

    @Override
    public boolean setEdgeLineColor(Color color) {
      if (color == null) {
        return false;
      }
      this.edgeLineColor = color;
      return true;
    }

    @Override
    public boolean setEdgeLineVisible(boolean visible) {
      this.edgeLineVisible = visible;
      return true;
    }

    @Override
    public boolean setInnerPaint(SGIPaint paint) {
      this.innerPaint = paint;
      return true;
    }

    @Override
    public boolean setTransparent(float alpha) {
      this.transparency = alpha;
      return true;
    }

    @Override
    public boolean setX(float x) {
      this.x = x;
      return true;
    }

    @Override
    public boolean setY(float y) {
      this.y = y;
      return true;
    }

    @Override
    public boolean setWidth(float w) {
      this.width = w;
      return true;
    }

    @Override
    public boolean setHeight(float h) {
      this.height = h;
      return true;
    }

    @Override
    public boolean setMagnification(float mag) {
      this.magnification = mag;
      return true;
    }

    @Override
    public float getMagnification() {
      return this.magnification;
    }
  }

  private TestBar bar;

  @BeforeEach
  void createBar() {
    this.bar = new TestBar();
  }

  @Test
  void axisValuesAreStored() {
    assertTrue(this.bar.setBaselineValue(2.5));
    assertEquals(2.5, this.bar.getBaselineValue(), 1.0e-6);
    assertTrue(this.bar.setWidthValue(0.4));
    assertEquals(0.4, this.bar.getWidthValue(), 1.0e-6);
  }

  @Test
  void verticalFlagAndIntervalAreStored() {
    assertTrue(this.bar.isVertical());
    assertTrue(this.bar.setVertical(false));
    assertFalse(this.bar.isVertical());
    assertTrue(this.bar.setVertical(true));
    assertTrue(this.bar.setInterval(0.5));
    assertEquals(0.5, this.bar.getInterval(), 1.0e-6);
  }

  @Test
  void setBoundsSetsTheRectangleState() {
    final TestBar created = new TestBar();
    created.setBounds(1.0f, 2.0f, 3.0f, 4.0f);
    assertEquals(1.0f, created.getX(), 1.0e-6f);
    assertEquals(2.0f, created.getY(), 1.0e-6f);
    assertEquals(3.0f, created.getWidth(), 1.0e-6f);
    assertEquals(4.0f, created.getHeight(), 1.0e-6f);
  }

  @Test
  void elementBoundsNormalizesNegativeDimensions() {
    this.bar.setLocation(5.0f, 6.0f);
    this.bar.setSize(-10.0f, -20.0f);
    final Rectangle2D bounds = this.bar.getElementBounds();
    assertEquals(5.0, bounds.getX(), 1.0e-6);
    assertEquals(6.0, bounds.getY(), 1.0e-6);
    assertEquals(10.0, bounds.getWidth(), 1.0e-6);
    assertEquals(20.0, bounds.getHeight(), 1.0e-6);
  }

  @Test
  void containsHitTestsTheElementBounds() {
    this.bar.setLocation(5.0f, 6.0f);
    this.bar.setSize(10.0f, 10.0f);
    assertTrue(this.bar.contains(10, 11));
    assertFalse(this.bar.contains(0, 0));
    assertFalse(this.bar.contains(15, 16));
  }

  @Test
  void getShapeMatchesTheElementBounds() {
    this.bar.setLocation(1.0f, 2.0f);
    this.bar.setSize(3.0f, 4.0f);
    final Rectangle2D sh = (Rectangle2D) this.bar.getShape();
    assertEquals(1.0, sh.getX(), 1.0e-6);
    assertEquals(2.0, sh.getY(), 1.0e-6);
    assertEquals(3.0, sh.getWidth(), 1.0e-6);
    assertEquals(4.0, sh.getHeight(), 1.0e-6);
  }

  @Test
  void barPropertiesRoundTripPreservesTheFullState() {
    this.bar.setBaselineValue(2.5);
    this.bar.setWidthValue(0.4);
    this.bar.setVertical(false);
    this.bar.setInterval(0.5);
    this.bar.setEdgeLineWidth(1.5f);
    this.bar.setEdgeLineType(SGLineConstants.LINE_TYPE_DASHED);
    this.bar.setEdgeLineColor(Color.RED);
    this.bar.setEdgeLineVisible(false);

    final SGProperties p = this.bar.getProperties();
    assertNotNull(p);

    final TestBar restored = new TestBar();
    assertTrue(restored.setProperties(p));
    assertEquals(this.bar.getBaselineValue(), restored.getBaselineValue(), 1.0e-6);
    assertEquals(this.bar.getWidthValue(), restored.getWidthValue(), 1.0e-6);
    assertEquals(this.bar.isVertical(), restored.isVertical());
    assertEquals(this.bar.getInterval(), restored.getInterval(), 1.0e-6);
    assertEquals(this.bar.getEdgeLineWidth(), restored.getEdgeLineWidth(), 1.0e-6f);
    assertEquals(this.bar.getEdgeLineType(), restored.getEdgeLineType());
    assertEquals(this.bar.getEdgeLineColor(), restored.getEdgeLineColor());
    assertEquals(this.bar.isEdgeLineVisible(), restored.isEdgeLineVisible());
  }

  @Test
  void foreignPropertiesAreRejected() {
    assertFalse(this.bar.getProperties(null));
    assertFalse(this.bar.getProperties(new SGProperties() {}));
    assertFalse(this.bar.setProperties(new SGProperties() {}));
  }
}
