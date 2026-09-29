package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import javax.xml.parsers.DocumentBuilderFactory;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Headless unit tests of the abstract rectangle drawing element. */
class SGDrawingElementRectangleTest {

  /** A minimal rectangle stub remembering the plain state. */
  private static class TestRectangle extends SGDrawingElementRectangle {

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

    TestRectangle() {
      super();
    }

    @Override
    protected SGStroke getStroke() {
      return new SGStroke();
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

  private TestRectangle rect;

  @BeforeEach
  void createRectangle() {
    this.rect = new TestRectangle();
  }

  @Test
  void locationIsCopiedOnRead() {
    this.rect.setX(3.0f);
    this.rect.setY(4.0f);
    final SGTuple2f loc = this.rect.getLocation();
    assertEquals(3.0f, loc.x, 1.0e-6f);
    assertEquals(4.0f, loc.y, 1.0e-6f);
    loc.x = 99.0f;
    // mutating the returned tuple does not affect the stored location
    assertEquals(3.0f, this.rect.getX(), 1.0e-6f);
  }

  @Test
  void locationAndBoundsSettersUpdateTheState() {
    assertTrue(this.rect.setLocation(5.0f, 6.0f));
    assertEquals(5.0f, this.rect.getX(), 1.0e-6f);
    assertEquals(6.0f, this.rect.getY(), 1.0e-6f);
    assertTrue(this.rect.setLocation(new SGTuple2f(7.0f, 8.0f)));
    assertEquals(7.0f, this.rect.getX(), 1.0e-6f);
    assertEquals(8.0f, this.rect.getY(), 1.0e-6f);
    assertTrue(this.rect.setSize(10.0f, 20.0f));
    assertEquals(10.0f, this.rect.getWidth(), 1.0e-6f);
    assertEquals(20.0f, this.rect.getHeight(), 1.0e-6f);
    assertTrue(this.rect.setBounds(1.0f, 2.0f, 3.0f, 4.0f));
    assertEquals(1.0f, this.rect.getX(), 1.0e-6f);
    assertEquals(2.0f, this.rect.getY(), 1.0e-6f);
    assertEquals(3.0f, this.rect.getWidth(), 1.0e-6f);
    assertEquals(4.0f, this.rect.getHeight(), 1.0e-6f);
  }

  @Test
  void unitAwareWidthAndHeightConvertToPoints() {
    assertTrue(this.rect.setWidth(10.0f, "cm"));
    assertTrue(this.rect.setHeight(20.0f, "cm"));
    // 1 cm = 72 / 2.54 pt; the plain setter does no rounding
    assertEquals(10.0 * 72.0 / 2.54, this.rect.getWidth(), 1.0e-3f);
    assertEquals(20.0 * 72.0 / 2.54, this.rect.getHeight(), 1.0e-3f);
  }

  @Test
  void unitAwareEdgeLineWidthConvertsAndRoundsToPoints() {
    // 1 mm = (72 / (2.54 * 10)) pt, rounded to two fractional digits
    assertTrue(this.rect.setEdgeLineWidth(1.0f, "mm"));
    assertEquals(2.83f, this.rect.getEdgeLineWidth(), 1.0e-3f);
  }

  @Test
  void unitAwareEdgeLineWidthRejectsOutOfRangeValue() {
    assertFalse(this.rect.setEdgeLineWidth(-1.0f, "mm"));
  }

  @Test
  void edgeLineWidthInPointsIsReadBack() {
    this.rect.setEdgeLineWidth(2.0f);
    // the default export unit for line widths is "pt"
    assertEquals(2.0f, this.rect.getEdgeLineWidth("pt"), 1.0e-6f);
  }

  @Test
  void propertiesRoundTripPreservesTheFullState() {
    this.rect.setVisible(false);
    this.rect.setX(1.0f);
    this.rect.setY(2.0f);
    this.rect.setWidth(10.0f);
    this.rect.setHeight(20.0f);
    this.rect.setEdgeLineWidth(1.5f);
    this.rect.setEdgeLineType(SGLineConstants.LINE_TYPE_DASHED);
    this.rect.setEdgeLineColor(Color.RED);
    this.rect.setEdgeLineVisible(false);
    SGFillPaint inner = new SGFillPaint();
    inner.setColor(Color.BLUE);
    this.rect.setInnerPaint(inner);

    final SGProperties p = this.rect.getProperties();
    assertNotNull(p);

    final TestRectangle restored = new TestRectangle();
    assertTrue(restored.setProperties(p));
    // x/y are not part of the rectangle properties (they are managed
    // by the owning group), so only the size and edge state round trip
    assertEquals(this.rect.getWidth(), restored.getWidth(), 1.0e-6f);
    assertEquals(this.rect.getHeight(), restored.getHeight(), 1.0e-6f);
    assertEquals(this.rect.getEdgeLineWidth(), restored.getEdgeLineWidth(), 1.0e-6f);
    assertEquals(this.rect.getEdgeLineType(), restored.getEdgeLineType());
    assertEquals(this.rect.getEdgeLineColor(), restored.getEdgeLineColor());
    assertEquals(this.rect.isEdgeLineVisible(), restored.isEdgeLineVisible());
    assertEquals(
        ((SGFillPaint) this.rect.getInnerPaint()).getColor(),
        ((SGFillPaint) restored.getInnerPaint()).getColor());
    assertFalse(restored.isVisible());
  }

  @Test
  void foreignPropertiesAreRejectedByBothCalls() {
    assertFalse(this.rect.getProperties(null));
    assertFalse(this.rect.getProperties(new SGProperties() {}));
    assertFalse(this.rect.setProperties(new SGProperties() {}));
  }

  @Test
  void untypedRectanglePropertiesAreRejected() {
    final SGDrawingElementRectangle.RectangleProperties p =
        new SGDrawingElementRectangle.RectangleProperties();
    // the default property object still carries an unset edge line type of -1,
    // which setProperties treats as "not provided"
    assertFalse(this.rect.setProperties(p));
  }

  @Test
  void elementBoundsNormalizesNegativeDimensions() {
    this.rect.setLocation(5.0f, 6.0f);
    this.rect.setSize(-10.0f, -20.0f);
    final Rectangle2D bounds = this.rect.getElementBounds();
    // a negative width/height moves the origin corner back
    assertEquals(-5.0, bounds.getX(), 1.0e-6);
    assertEquals(-14.0, bounds.getY(), 1.0e-6);
    assertEquals(10.0, bounds.getWidth(), 1.0e-6);
    assertEquals(20.0, bounds.getHeight(), 1.0e-6);
  }

  @Test
  void elementBoundsAppliesTheMagnification() {
    this.rect.setLocation(0.0f, 0.0f);
    this.rect.setSize(4.0f, 6.0f);
    this.rect.setMagnification(2.0f);
    final Rectangle2D bounds = this.rect.getElementBounds();
    assertEquals(8.0, bounds.getWidth(), 1.0e-6);
    assertEquals(12.0, bounds.getHeight(), 1.0e-6);
  }

  @Test
  void containsHitTestsTheElementBounds() {
    this.rect.setLocation(5.0f, 6.0f);
    this.rect.setSize(10.0f, 10.0f);
    assertTrue(this.rect.contains(10, 11));
    assertFalse(this.rect.contains(0, 0));
    assertFalse(this.rect.contains(15, 16));
  }

  @Test
  void writePropertyWritesTheEdgeLineAttributes() throws Exception {
    this.rect.setEdgeLineWidth(1.5f);
    this.rect.setEdgeLineType(SGLineConstants.LINE_TYPE_DOTTED);
    this.rect.setEdgeLineColor(Color.RED);
    this.rect.setEdgeLineVisible(false);

    final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    final Element el = doc.createElement("rectangle");
    assertTrue(this.rect.writeProperty(el, null));

    assertEquals("1.5pt", el.getAttribute(SGRectangleConstants.KEY_EDGE_LINE_WIDTH));
    assertEquals("Dotted", el.getAttribute(SGRectangleConstants.KEY_EDGE_LINE_TYPE));
    assertEquals("(255,0,0)", el.getAttribute(SGRectangleConstants.KEY_EDGE_LINE_COLOR));
    assertEquals("false", el.getAttribute(SGRectangleConstants.KEY_EDGE_LINE_VISIBLE));
  }

  @Test
  void readPropertyRestoresTheEdgeLineAttributes() throws Exception {
    final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    final Element el = doc.createElement("rectangle");
    el.setAttribute(SGRectangleConstants.KEY_EDGE_LINE_WIDTH, "2.5pt");
    el.setAttribute(SGRectangleConstants.KEY_EDGE_LINE_TYPE, "Dashed");
    el.setAttribute(SGRectangleConstants.KEY_EDGE_LINE_VISIBLE, "false");
    // the inner paint is read via the generic paint keys
    el.setAttribute("FillColor", "(0,0,255)");

    final TestRectangle restored = new TestRectangle();
    assertTrue(restored.readProperty(el));
    assertEquals(2.5f, restored.getEdgeLineWidth(), 1.0e-6f);
    assertEquals(SGLineConstants.LINE_TYPE_DASHED, restored.getEdgeLineType());
    assertFalse(restored.isEdgeLineVisible());
    assertEquals(Color.BLUE, ((SGFillPaint) restored.getInnerPaint()).getColor());
  }
}
