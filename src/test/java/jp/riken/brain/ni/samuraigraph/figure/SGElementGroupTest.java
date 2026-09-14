package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

/** Unit tests for the base element group behavior of {@link SGElementGroup}. */
class SGElementGroupTest {

  private static class StubDrawingElement extends SGDrawingElement {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean contains(int x, int y) {
      return false;
    }

    @Override
    public boolean setMagnification(float mag) {
      return true;
    }

    @Override
    public float getMagnification() {
      return 1.0f;
    }
  }

  private static class TestGroup extends SGElementGroup {
    /** The number of created elements, used to check the re-initialization. */
    int createdCount = 0;

    @Override
    protected SGDrawingElement createDrawingElementInstance(int index) {
      this.createdCount++;
      return new StubDrawingElement();
    }

    @Override
    public boolean paintElement(Graphics2D g2d, Rectangle2D clipRect) {
      return true;
    }

    @Override
    public String getTagName() {
      return "TestGroup";
    }

    @Override
    public boolean writeProperty(Element el) {
      return true;
    }

    @Override
    public boolean readProperty(Element el) {
      return true;
    }
  }

  private SGElementGroup group;

  private SGElementGroup createGroup() {
    TestGroup group = new TestGroup();
    this.group = group;
    return group;
  }

  @Test
  void initDrawingElementCreatesInstances() {
    SGElementGroup group = this.createGroup();
    assertTrue(group.initDrawingElement(3));
    assertEquals(3, group.getDrawingElementArray().length);
    assertNotNull(group.getDrawingElementArray()[0]);
  }

  @Test
  void initDrawingElementKeepsArrayWhenSizeMatches() {
    SGElementGroup group = ((TestGroup) this.createGroup());
    assertTrue(group.initDrawingElement(2));
    assertTrue(group.initDrawingElement(2));
    assertEquals(2, ((TestGroup) group).createdCount);
    assertNotNull(group.getDrawingElementArray());
  }

  @Test
  void negativeMagnificationIsRejected() {
    SGElementGroup group = this.createGroup();
    assertThrows(IllegalArgumentException.class, () -> group.setMagnification(-0.2f));
    assertThrows(IllegalArgumentException.class, () -> group.setMagnification(0.0f));
    assertTrue(group.setMagnification(2.0f));
    assertEquals(2.0f, group.getMagnification());
  }

  @Test
  void visibilityRoundTrip() {
    SGElementGroup group = this.createGroup();
    group.setVisible(false);
    assertFalse(group.isVisible());
    group.setVisible(true);
    assertTrue(group.isVisible());
  }

  @Test
  void propertiesRoundTripPreservesVisibility() {
    SGElementGroup group = this.createGroup();
    group.setVisible(false);
    SGProperties p = group.getProperties();
    assertNotNull(p);

    TestGroup restored = (TestGroup) this.createGroup();
    assertTrue(restored.setProperties(p));
    assertFalse(restored.isVisible());
  }

  @Test
  void foreignPropertiesAreRejected() {
    SGElementGroup group = this.createGroup();
    assertFalse(group.setProperties(new SGProperties() {}));
    assertFalse(group.getProperties(null));
  }

  @Test
  void disposeClearsDrawingElements() {
    SGElementGroup group = this.createGroup();
    group.initDrawingElement(2);
    group.dispose();
    assertTrue(group.isDisposed());
    assertNull(group.getDrawingElementArray());
  }
}
