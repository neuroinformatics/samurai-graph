package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Round-trip tests for the window attribute reading of {@link SGDrawingWindowPropertyIO}.
 *
 * <p>These tests need a display because {@link SGDrawingWindow} is a {@link java.awt.Frame}.
 */
class SGDrawingWindowPropertyIOTest {

  private SGDrawingWindow wnd;

  private SGDrawingWindow createWindow() {
    this.wnd = new SGDrawingWindow();
    return this.wnd;
  }

  @AfterEach
  void disposeWindow() {
    if (this.wnd != null) {
      this.wnd.dispose();
    }
  }

  private static Element createRootElement() {
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    return doc.getDocumentElement();
  }

  private SGDrawingWindow createReadyWindow() {
    SGDrawingWindow wnd = this.createWindow();
    wnd.init();
    return wnd;
  }

  private static Element createRootElementOld() {
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    return doc.getDocumentElement();
  }

  @Test
  void windowCanBeCreated() {
    SGDrawingWindow wnd = this.createWindow();
    wnd.init();
    assertNotNull(wnd);
    assertNotNull(wnd.getFigurePanel());
  }

  @Test
  void attributesAreReadFromElement() {
    SGDrawingWindow wnd = this.createReadyWindow();
    Element el = createRootElement();
    el.setAttribute(SGIRootObjectConstants.KEY_PAPER_WIDTH, "20cm");
    el.setAttribute(SGIRootObjectConstants.KEY_PAPER_HEIGHT, "10cm");
    el.setAttribute(SGIRootObjectConstants.KEY_GRID_VISIBLE, "true");
    el.setAttribute(SGIRootObjectConstants.KEY_GRID_INTERVAL, "2cm");
    el.setAttribute(SGIRootObjectConstants.KEY_GRID_LINE_WIDTH, "2pt");
    el.setAttribute(SGIRootObjectConstants.KEY_IMAGE_LOCATION_X, "3cm");
    el.setAttribute(SGIRootObjectConstants.KEY_IMAGE_LOCATION_Y, "4cm");
    el.setAttribute(SGIRootObjectConstants.KEY_IMAGE_SCALE, "2.0");
    assertTrue(wnd.setPaperWidth(20.0f, "cm"), "setPaperWidth");
    assertTrue(wnd.setPaperHeight(10.0f, "cm"), "setPaperHeight");
    assertTrue(wnd.setGridLineVisible(true), "setGridLineVisible");
    assertTrue(wnd.setGridLineInterval(2.0f, "cm"), "setGridLineInterval");
    assertTrue(wnd.setGridLineWidth(2.0f, "pt"), "setGridLineWidth");
    assertTrue(wnd.setImageLocationX(3.0f, "cm"), "setImageLocationX");
    assertTrue(wnd.setImageLocationY(4.0f, "cm"), "setImageLocationY");
    assertTrue(wnd.setImageScalingFactor(2.0f), "setImageScalingFactor");
    assertTrue(wnd.readProperty(el, 0.0f, 9.0f), "readProperty");
    assertEquals(20.0f, wnd.getPaperWidth("cm"), 0.001f);
    assertEquals(10.0f, wnd.getPaperHeight("cm"), 0.001f);
    assertTrue(wnd.isGridLineVisible());
    assertEquals(2.0f, wnd.getGridLineInterval("cm"), 0.001f);
    assertEquals(2.0f, wnd.getGridLineWidth("pt"), 0.001f);
    assertEquals(3.0f, wnd.getImageLocationX("cm"), 0.001f);
    assertEquals(4.0f, wnd.getImageLocationY("cm"), 0.001f);
    assertEquals(2.0, wnd.getImageScalingFactor(), 0.001f);
  }

  @Test
  void missingAttributesAreIgnored() {
    SGDrawingWindow wnd = this.createReadyWindow();
    Element el = createRootElement();
    assertTrue(wnd.readProperty(el, 0.0f, 9.0f));
    assertNull(wnd.getImageFilePath());
  }

  @Test
  void invalidNumberRejected() {
    SGDrawingWindow wnd = this.createReadyWindow();
    Element el = createRootElement();
    el.setAttribute(SGIRootObjectConstants.KEY_PAPER_WIDTH, "twentycm");
    assertTrue(wnd.readProperty(el, 0.0f, 9.0f) == false);
  }

  @Test
  void mementoRoundTrip() {
    SGDrawingWindow wnd1 = this.createReadyWindow();
    assertTrue(wnd1.setPaperWidth(20.0f, "cm"));
    assertTrue(wnd1.setPaperHeight(10.0f, "cm"));
    assertTrue(wnd1.setGridLineVisible(true));
    assertTrue(wnd1.setGridLineInterval(2.0f, "cm"));
    assertTrue(wnd1.setGridLineWidth(2.0f, "pt"));
    assertTrue(wnd1.setPaperColor(new java.awt.Color(255, 0, 0)));
    assertTrue(wnd1.setGridLineColor(new java.awt.Color(0, 255, 0)));
    assertTrue(wnd1.setImageLocationX(3.0f, "cm"));
    assertTrue(wnd1.setImageLocationY(4.0f, "cm"));
    assertTrue(wnd1.setImageScalingFactor(2.0f));

    SGProperties p = wnd1.getProperties();
    assertNotNull(p);

    SGDrawingWindow wnd2 = this.createReadyWindow();
    assertTrue(wnd2.setProperties(p));
    assertEquals(20.0f, wnd2.getPaperWidth("cm"), 0.001f);
    assertEquals(10.0f, wnd2.getPaperHeight("cm"), 0.001f);
    assertTrue(wnd2.isGridLineVisible());
    assertEquals(2.0f, wnd2.getGridLineInterval("cm"), 0.001f);
    assertEquals(2.0f, wnd2.getGridLineWidth("pt"), 0.001f);
    assertEquals(new java.awt.Color(255, 0, 0), wnd2.getPaperColor());
    assertEquals(new java.awt.Color(0, 255, 0), wnd2.getGridLineColor());
    assertEquals(3.0f, wnd2.getImageLocationX("cm"), 0.001f);
    assertEquals(4.0f, wnd2.getImageLocationY("cm"), 0.001f);
    assertEquals(2.0, wnd2.getImageScalingFactor(), 0.001f);
  }

  @Test
  void commandMapRoundTrip() {
    SGDrawingWindow wnd = this.createReadyWindow();
    SGPropertyMap map = new SGPropertyMap();
    map.putValue(SGIRootObjectConstants.COM_PAPER_WIDTH, "20cm");
    map.putValue(SGIRootObjectConstants.COM_PAPER_HEIGHT, "10cm");
    map.putValue(SGIRootObjectConstants.COM_WINDOW_GRID_VISIBLE, "true");
    map.putValue(SGIRootObjectConstants.COM_WINDOW_GRID_INTERVAL, "2cm");
    map.putValue(SGIRootObjectConstants.COM_WINDOW_GRID_LINE_WIDTH, "2pt");
    map.putValue(SGIRootObjectConstants.COM_IMAGE_LOCATION_X, "3cm");
    map.putValue(SGIRootObjectConstants.COM_IMAGE_LOCATION_Y, "4cm");
    map.putValue(SGIRootObjectConstants.COM_IMAGE_SCALING_FACTOR, "2.0");

    SGPropertyResults results = wnd.setProperties(map);
    assertNotNull(results);
    assertEquals(
        SGPropertyResults.SUCCEEDED, results.getResult(SGIRootObjectConstants.COM_PAPER_WIDTH));
    assertEquals(
        SGPropertyResults.SUCCEEDED, results.getResult(SGIRootObjectConstants.COM_PAPER_HEIGHT));
    assertEquals(
        SGPropertyResults.SUCCEEDED,
        results.getResult(SGIRootObjectConstants.COM_WINDOW_GRID_VISIBLE));
    assertEquals(
        SGPropertyResults.SUCCEEDED,
        results.getResult(SGIRootObjectConstants.COM_WINDOW_GRID_INTERVAL));
    assertEquals(
        SGPropertyResults.SUCCEEDED,
        results.getResult(SGIRootObjectConstants.COM_WINDOW_GRID_LINE_WIDTH));
    assertEquals(
        SGPropertyResults.SUCCEEDED,
        results.getResult(SGIRootObjectConstants.COM_IMAGE_LOCATION_X));
    assertEquals(
        SGPropertyResults.SUCCEEDED,
        results.getResult(SGIRootObjectConstants.COM_IMAGE_LOCATION_Y));
    assertEquals(
        SGPropertyResults.SUCCEEDED,
        results.getResult(SGIRootObjectConstants.COM_IMAGE_SCALING_FACTOR));
    assertEquals(20.0f, wnd.getPaperWidth("cm"), 0.001f);
    assertEquals(10.0f, wnd.getPaperHeight("cm"), 0.001f);
    assertTrue(wnd.isGridLineVisible());
    assertEquals(2.0f, wnd.getGridLineInterval("cm"), 0.001f);
    assertEquals(2.0f, wnd.getGridLineWidth("pt"), 0.001f);
    assertEquals(3.0f, wnd.getImageLocationX("cm"), 0.001f);
    assertEquals(4.0f, wnd.getImageLocationY("cm"), 0.001f);
    assertEquals(2.0, wnd.getImageScalingFactor(), 0.001f);
  }
}
