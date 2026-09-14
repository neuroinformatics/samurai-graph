package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the symbol group base class with a stub subclass. */
class SGElementGroupSymbolTest {

  private static class TestSymbolGroup extends SGElementGroupSymbol {

    TestSymbolGroup() {
      super();
    }

    @Override
    public boolean setSize(float size, String unit) {
      return this.setSize(size);
    }

    @Override
    public boolean setLineWidth(float lw, String unit) {
      return this.setLineWidth(lw);
    }

    @Override
    public SGTuple2f getLocation(int index) {
      return null;
    }

    @Override
    protected SGDrawingElement createDrawingElementInstance(int index) {
      return null;
    }
  }

  private SGElementGroupSymbol group;

  @BeforeEach
  void createGroup() {
    this.group = new TestSymbolGroup();
  }

  @Test
  void sizeIsValidated() {
    assertTrue(this.group.setSize(0.3f));
    assertEquals(0.3f, this.group.getSize());
    assertThrows(IllegalArgumentException.class, () -> this.group.setSize(-0.1f));
  }

  @Test
  void symbolTypeIsValidated() {
    assertTrue(this.group.setType(SGSymbolConstants.SYMBOL_TYPE_CIRCLE));
    assertEquals(SGSymbolConstants.SYMBOL_TYPE_CIRCLE, this.group.getType());
    assertFalse(this.group.setType(999));
    assertEquals(SGSymbolConstants.SYMBOL_TYPE_CIRCLE, this.group.getType());
  }

  @Test
  void lineAttributesAreValidated() {
    assertThrows(IllegalArgumentException.class, () -> this.group.setLineColor(null));
    assertTrue(this.group.setLineColor(Color.RED));
    assertEquals(Color.RED, this.group.getLineColor());
    assertTrue(this.group.setLineVisible(false));
    assertFalse(this.group.isLineVisible());
    assertThrows(IllegalArgumentException.class, () -> this.group.setLineWidth(-1.0f));
    assertTrue(this.group.setLineWidth(1.5f));
    assertEquals(1.5f, this.group.getLineWidth(), 1.0e-6f);
  }

  @Test
  void angleAndSizeConversionsWork() {
    assertTrue(this.group.setSize(0.3f));
    assertTrue(this.group.setAngle(30.0f));
    assertEquals(0.3f, this.group.getSize(), 1.0e-6f);
    assertTrue(this.group.setSize(0.4f, "cm"));
  }

  @Test
  void innerPaintIsClonedOnAssignment() {
    SGFillPaint paint = new SGFillPaint();
    assertTrue(paint.setColor(Color.RED));
    assertTrue(this.group.setInnerPaint(paint));
    assertEquals(Color.RED, this.group.getInnerColor());
    // mutating the original paint does not affect the assigned clone
    assertTrue(paint.setColor(Color.GREEN));
    assertEquals(Color.RED, this.group.getInnerColor());
    assertTrue(this.group.setInnerColor(Color.BLUE));
    assertEquals(Color.BLUE, this.group.getInnerColor());
    assertTrue(this.group.setInnerTransparency(40));
    assertEquals(40, this.group.getInnerTransparency());
    assertThrows(IllegalArgumentException.class, () -> this.group.setInnerPaint(null));
  }

  @Test
  void propertiesRoundTripPreservesEverything() {
    SGElementGroupSymbol group = this.group;
    group.setSize(0.5f);
    group.setType(SGSymbolConstants.SYMBOL_TYPE_SQUARE);
    group.setLineWidth(1.5f);
    group.setLineColor(Color.RED);
    group.setLineVisible(false);
    SGFillPaint inner = new SGFillPaint();
    inner.setColor(Color.BLUE);
    group.setInnerPaint(inner);

    SGProperties p = group.getProperties();
    assertNotNull(p);

    SGElementGroupSymbol restored = new TestSymbolGroup();
    assertTrue(restored.setProperties(p));
    assertEquals(group.getSize(), restored.getSize(), 1.0e-6f);
    assertEquals(group.getType(), restored.getType());
    assertEquals(group.getLineWidth(), restored.getLineWidth(), 1.0e-6f);
    assertEquals(group.getLineColor(), restored.getLineColor());
    assertFalse(restored.isLineVisible());
    assertEquals(group.getInnerColor(), restored.getInnerColor());
  }

  @Test
  void foreignPropertiesAreRejected() {
    assertFalse(this.group.setProperties(new SGProperties() {}));
    assertFalse(this.group.getProperties(null));
  }

  @Test
  void disposeClearsColors() {
    this.group.setLineColor(Color.RED);
    this.group.dispose();
    assertTrue(this.group.isDisposed());
    assertTrue(this.group.getLineColor() == null);
  }
}
