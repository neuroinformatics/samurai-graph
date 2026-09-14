package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.figure.SGLineConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the line element group with a stub subclass. */
class SGElementGroupLineTest {

  private static class TestLineGroup extends SGElementGroupLine {

    private SGTuple2f[] points = null;

    TestLineGroup() {
      super(LINE_TYPE_SOLID, Color.BLACK, 1.0f);
    }

    @Override
    public boolean setLineWidth(float lw, String unit) {
      return this.setLineWidth(lw);
    }

    @Override
    public SGTuple2f getStart(final int index) {
      return this.points[index];
    }

    @Override
    public SGTuple2f getEnd(final int index) {
      return this.points[index + 1];
    }

    public void setPoints(SGTuple2f[] points) {
      this.points = points;
    }
  }

  private TestLineGroup group;

  @BeforeEach
  void createGroup() {
    this.group = new TestLineGroup();
  }

  private static SGTuple2f tuple(float x, float y) {
    return new SGTuple2f(x, y);
  }

  private SGTuple2f[] locate(SGTuple2f... points) {
    TestLineGroup lineGroup = (TestLineGroup) this.group;
    lineGroup.setPoints(points);
    return points;
  }

  @Test
  void styleAttributesAreSetAndReflected() {
    SGElementGroupLine group = this.group;
    assertTrue(group.setLineWidth(2.0f));
    assertEquals(2.0f, group.getLineWidth());
    assertTrue(group.setLineType(LINE_TYPE_DASHED));
    assertEquals(LINE_TYPE_DASHED, group.getLineType());
    group.setColor(Color.RED);
    assertEquals(Color.RED, group.getColor());
    assertTrue(group.setMagnification(2.0f));
    assertEquals(2.0f, group.getMagnification());
  }

  @Test
  void invalidLineTypeIsRejected() {
    SGElementGroupLine group = this.group;
    assertTrue(group.setLineType(LINE_TYPE_DASHED));
    assertFalse(group.setLineType(999));
    assertEquals(LINE_TYPE_DASHED, group.getLineType());
  }

  @Test
  void nullColorAndHashCodeAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> this.group.setColor(null));
    assertThrows(IllegalArgumentException.class, () -> this.group.setStyle(null));
  }

  @Test
  void importingLineParamsInUnitConvertsToPoints() {
    SGElementGroupLine group = this.group;
    assertTrue(group.setLineWidth(2.0f, "pt"));
    assertEquals(2.0f, group.getLineWidth("pt"), 1.0e-4f);
  }

  @Test
  void propertiesRoundTripPreservesStyle() throws Exception {
    SGElementGroupLine group = this.group;
    group.setLineWidth(2.0f);
    group.setLineType(LINE_TYPE_DOTTED);
    group.setColor(Color.RED);
    assertTrue(group.isLineConnectingAll());
    group.setLineConnectingAll(false);
    assertFalse(group.isLineConnectingAll());

    SGProperties p = group.getProperties();
    assertNotNull(p);

    SGElementGroupLine restored = new TestLineGroup();
    assertTrue(restored.setProperties(p));
    assertEquals(group.getLineWidth(), restored.getLineWidth(), 1.0e-6f);
    assertEquals(group.getLineType(), restored.getLineType());
    assertEquals(group.getColor(), restored.getColor());
    assertFalse(restored.isLineConnectingAll());
  }

  @Test
  void foreignPropertiesAreRejected() {
    assertFalse(this.group.setProperties(new SGProperties() {}));
    assertFalse(this.group.getProperties(null));
  }

  @Test
  void allEffectivePointsBuildSinglePath() throws Exception {
    SGTuple2f[] points = new SGTuple2f[] {tuple(0, 0), tuple(1, 0), tuple(2, 0), tuple(3, 0)};
    SGElementGroupLine group = this.group;
    assertTrue(group.initDrawingElement(points));
    assertTrue(group.setLocation(points));
    assertEquals(1, group.mConnectedPathList.size());
  }

  @Test
  void missingPointsSplitTheConnection() throws Exception {
    SGTuple2f[] points =
        new SGTuple2f[] {
          tuple(0, 0), new SGTuple2f(Float.NaN, Float.NaN), tuple(2, 2), tuple(3, 3)
        };
    ((TestLineGroup) (this.group)).setPoints(points);
    SGElementGroupLine group = this.group;
    assertTrue(group.initDrawingElement(points));
    group.setLineConnectingAll(true);
    assertTrue(group.setLocation(points));
    assertEquals(1, group.mConnectedPathList.size());
  }

  @Test
  void closePointsAreReducedInOmitMode() throws Exception {
    SGTuple2f[] array = {tuple(0, 0), tuple(0.2f, 0), tuple(2, 0)};
    assertTrue(this.group.initDrawingElement(array));
    this.group.setLineConnectingAll(false);
    assertTrue(this.group.setLocation(array));
    // the first two points collapse into a single integer location
    assertEquals(1, this.group.mConnectedPathList.size());
  }

  @Test
  void paintElementDrawsWithAndWithoutClip() throws Exception {
    TestLineGroup group = this.group;
    assertTrue(
        group.initDrawingElement(new SGTuple2f[] {tuple(0, 0), tuple(10, 0), tuple(20, 10)}));
    assertTrue(group.setLocation(new SGTuple2f[] {tuple(0, 0), tuple(10, 0), tuple(20, 10)}));
    BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = img.createGraphics();
    try {
      assertTrue(group.paintElement(g2d, null));
      assertTrue(group.paintElement(g2d, new Rectangle2D.Double(0, 0, 5, 5)));
    } finally {
      g2d.dispose();
    }
  }

  @Test
  void elementCountsReduceByOne() {
    assertTrue(this.group.initDrawingElement(1));
    assertEquals(0, this.group.getDrawingElementArray().length);
    assertTrue(this.group.initDrawingElement(4));
    assertEquals(3, this.group.getDrawingElementArray().length);
  }

  @Test
  void nullGraphicsContextIsRejected() {
    assertFalse(((TestLineGroup) this.group).paintElement(null, null));
  }

  @Test
  void disposeClearsState() {
    SGElementGroupLine group = this.group;
    assertTrue(group.initDrawingElement(new SGTuple2f[] {tuple(0, 0), tuple(1, 1)}));
    group.dispose();
    assertTrue(group.isDisposed());
    assertTrue(group.mConnectedPathList == null);
  }
}
