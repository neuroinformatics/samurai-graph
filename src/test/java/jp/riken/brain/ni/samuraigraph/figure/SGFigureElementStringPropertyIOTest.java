package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the property round trip of {@link SGFigureElementString}. */
class SGFigureElementStringPropertyIOTest {

  private SGFigureElementString element;

  @AfterEach
  void disposeElement() {
    if (this.element != null) {
      this.element.dispose();
    }
  }

  private SGFigureElementString createElement() {
    SGFigureElementString element = this.element = new SGFigureElementString();
    element.setAxisElement(new SGFigureElementAxis());
    return element;
  }

  @Test
  void elementCanBeCreatedAndDisposed() {
    assertNotNull(this.createElement());
  }

  @Test
  void mementoRoundTripPreservesLabelText() {
    SGFigureElementString element = this.createElement();
    assertTrue(element.addString(1, "Hello", 0.5, 0.5));
    assertEquals("Hello", element.getString(1));

    SGProperties p = element.getProperties();
    assertNotNull(p);

    SGFigureElementString restored = new SGFigureElementString();
    restored.setAxisElement(new SGFigureElementAxis());
    assertTrue(restored.setProperties(p));
    assertEquals(1, restored.getVisibleChildList().size());
    assertEquals("Hello", restored.getString(1));
  }

  @Test
  void setPropertiesRejectsNonStringProperties() {
    SGFigureElementString element = this.createElement();
    assertTrue(!element.setProperties(new SGProperties() {}));
  }
}
