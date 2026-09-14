package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementShape;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the property round trip of {@link SGFigureElementShape}. */
class SGFigureElementShapePropertyIOTest {

  private SGFigureElementShape element;

  @AfterEach
  void disposeElement() {
    if (this.element != null) {
      this.element.dispose();
    }
  }

  private SGFigureElementShape createElement() {
    SGFigureElementShape element = this.element = new SGFigureElementShape();
    element.setAxisElement(new SGFigureElementAxis());
    assertTrue(element.setGraphRect(0.0f, 0.0f, 100.0f, 100.0f));
    return element;
  }

  @Test
  void elementCanBeCreatedAndDisposed() {
    assertNotNull(this.createElement());
  }

  @Test
  void mementoRoundTripPreservesShapeObject() {
    SGFigureElementShape element = this.createElement();
    assertTrue(element.addShape(SGIFigureElementShape.RECTANGLE, 10.0f, 20.0f));
    assertEquals(1, element.getVisibleChildList().size());

    SGProperties p = element.getProperties();
    assertNotNull(p);

    SGFigureElementShape restored = new SGFigureElementShape();
    restored.setAxisElement(new SGFigureElementAxis());
    assertTrue(restored.setGraphRect(0.0f, 0.0f, 100.0f, 100.0f));
    assertTrue(restored.setProperties(p));
    assertEquals(1, restored.getVisibleChildList().size());
    assertNotNull(restored.getVisibleChildList().get(0));
  }

  @Test
  void setPropertiesRejectsNonShapeProperties() {
    SGFigureElementShape element = this.createElement();
    assertFalse(element.setProperties(new SGProperties() {}));
  }

  @Test
  void mementoRoundTripPreservesArrowShape() {
    SGFigureElementShape element = this.createElement();
    assertTrue(element.addShape(SGIFigureElementShape.ARROW, 15.0f, 25.0f));
    assertEquals(1, element.getVisibleChildList().size());

    SGProperties p = element.getProperties();
    assertNotNull(p);

    SGFigureElementShape restored = new SGFigureElementShape();
    restored.setAxisElement(new SGFigureElementAxis());
    assertTrue(restored.setGraphRect(0.0f, 0.0f, 100.0f, 100.0f));
    assertTrue(restored.setProperties(p));
    assertEquals(1, restored.getVisibleChildList().size());
    Object restoredShape = restored.getVisibleChildList().get(0);
    assertNotNull(restoredShape);
    assertEquals(element.getVisibleChildList().get(0).getClass(), restoredShape.getClass());
  }

  @Test
  void mementoRoundTripPreservesLineShape() {
    SGFigureElementShape element = this.createElement();
    assertTrue(element.addShape(SGIFigureElementShape.LINE, 20.0f, 30.0f));
    assertEquals(1, element.getVisibleChildList().size());

    SGProperties p = element.getProperties();
    assertNotNull(p);

    SGFigureElementShape restored = new SGFigureElementShape();
    restored.setAxisElement(new SGFigureElementAxis());
    assertTrue(restored.setGraphRect(0.0f, 0.0f, 100.0f, 100.0f));
    assertTrue(restored.setProperties(p));
    assertEquals(1, restored.getVisibleChildList().size());
    assertNotNull(restored.getVisibleChildList().get(0));
    assertEquals(
        element.getVisibleChildList().get(0).getClass(),
        restored.getVisibleChildList().get(0).getClass());
  }

  @Test
  void mementoRoundTripPreservesEllipseShape() {
    SGFigureElementShape element = this.createElement();
    assertTrue(element.addShape(SGIFigureElementShape.ELLIPSE, 35.0f, 45.0f));
    assertEquals(1, element.getVisibleChildList().size());

    SGProperties p = element.getProperties();
    assertNotNull(p);

    SGFigureElementShape restored = new SGFigureElementShape();
    restored.setAxisElement(new SGFigureElementAxis());
    assertTrue(restored.setGraphRect(0.0f, 0.0f, 100.0f, 100.0f));
    assertTrue(restored.setProperties(p));
    assertEquals(1, restored.getVisibleChildList().size());
    assertNotNull(restored.getVisibleChildList().get(0));
  }
}
