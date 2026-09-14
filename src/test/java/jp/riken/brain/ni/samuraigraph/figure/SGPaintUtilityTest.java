package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import javax.xml.parsers.DocumentBuilderFactory;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGGradationPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGPatternPaint;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGSelectablePaint;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Unit tests for {@link SGPaintUtility} property reading. */
class SGPaintUtilityTest {

  private static final Color COLOR_1 = new Color(0x00, 0x66, 0xCC);

  private static final Color COLOR_2 = new Color(0xFF, 0x88, 0x00);

  private Element toElement(SGPropertyMap map) throws Exception {
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element el = doc.createElement("paint");
    doc.appendChild(el);
    map.setToElement(el);
    return el;
  }

  @Test
  void emptyElementReturnsNull() throws Exception {
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element el = doc.createElement("paint");
    assertNull(SGPaintUtility.readProperty(el));
  }

  @Test
  void fillPaintRoundTrip() throws Exception {
    SGFillPaint paint = new SGFillPaint();
    paint.setColor(COLOR_1);
    paint.setTransparency(50);

    Element el = this.toElement(paint.getPropertyFileMap());
    SGIPaint read = SGPaintUtility.readProperty(el);
    assertNotNull(read);
    assertTrue(read instanceof SGFillPaint);
    assertEquals(COLOR_1, ((SGFillPaint) read).getColor());
    assertEquals(50, ((SGFillPaint) read).getTransparencyPercent());
  }

  @Test
  void patternPaintRoundTrip() throws Exception {
    SGPatternPaint paint = new SGPatternPaint();
    paint.setColor(COLOR_2);
    paint.setTypeIndex(SGPatternPaint.INDEX_45_DEGREE);
    paint.setTransparency(30);

    Element el = this.toElement(paint.getPropertyFileMap());
    SGIPaint read = SGPaintUtility.readProperty(el);
    assertNotNull(read);
    assertTrue(read instanceof SGPatternPaint);
    assertEquals(COLOR_2, ((SGPatternPaint) read).getColor());
    assertEquals(SGPatternPaint.INDEX_45_DEGREE, ((SGPatternPaint) read).getTypeIndex());
    assertEquals(30, ((SGPatternPaint) read).getTransparencyPercent());
  }

  @Test
  void gradationPaintRoundTrip() throws Exception {
    SGGradationPaint paint = new SGGradationPaint();
    paint.setColors(new Color[] {COLOR_1, COLOR_2});
    paint.setDirection(SGGradationPaint.INDEX_DIRECTION_VERTICAL);
    paint.setOrder(SGGradationPaint.INDEX_ORDER_COLOR_2_1);
    paint.setTransparency(10);

    Element el = this.toElement(paint.getPropertyFileMap());
    SGIPaint read = SGPaintUtility.readProperty(el);
    assertNotNull(read);
    assertTrue(read instanceof SGGradationPaint);
    SGGradationPaint gradationPaint = (SGGradationPaint) read;
    assertEquals(COLOR_1, gradationPaint.getColors()[0]);
    assertEquals(COLOR_2, gradationPaint.getColors()[1]);
    assertEquals(SGGradationPaint.INDEX_DIRECTION_VERTICAL, gradationPaint.getDirectionIndex());
    assertEquals(SGGradationPaint.INDEX_ORDER_COLOR_2_1, gradationPaint.getOrderIndex());
    assertEquals(10, gradationPaint.getTransparencyPercent());
  }

  @Test
  void selectablePaintRoundTrip() throws Exception {
    SGSelectablePaint paint = new SGSelectablePaint();
    paint.setFillColor(COLOR_1);
    paint.setTransparency(50);
    paint.setSelectedPaintStyle(SGSelectablePaint.STYLE_INDEX_FILL);

    Element el = this.toElement(paint.getPropertyFileMap());
    SGIPaint read = SGPaintUtility.readProperty(el);
    assertNotNull(read);
    assertTrue(read instanceof SGSelectablePaint);
    SGSelectablePaint selectablePaint = (SGSelectablePaint) read;
    assertEquals(SGSelectablePaint.STYLE_INDEX_FILL, selectablePaint.getSelectedStyle().intValue());
    // the alpha value reflects the transparency and is not stored in the property file
    assertEquals(COLOR_1.getRGB() & 0xFFFFFF, selectablePaint.getFillColor().getRGB() & 0xFFFFFF);
    assertEquals(50, selectablePaint.getTransparencyPercent());
  }

  @Test
  void invalidPaintStyleIsRejected() throws Exception {
    SGSelectablePaint paint = new SGSelectablePaint();
    paint.setFillColor(COLOR_1);
    paint.setSelectedPaintStyle(SGSelectablePaint.STYLE_INDEX_FILL);

    Element el = this.toElement(paint.getPropertyFileMap());
    el.setAttribute("PaintStyle", "Invalid Style");
    assertNull(SGPaintUtility.readProperty(el));
  }
}
