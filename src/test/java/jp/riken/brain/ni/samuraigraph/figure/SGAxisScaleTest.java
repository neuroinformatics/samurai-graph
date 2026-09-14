package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGAxisScale}. */
class SGAxisScaleTest {

  private SGAxisScale scale;

  private SGAxisScale createScale() {
    this.scale = new SGAxisScale(new SGFigureElementAxis());
    return this.scale;
  }

  @Test
  void scaleCanBeConstructed() {
    assertNotNull(this.createScale());
    assertNotNull(this.scale.getXAxis());
    assertNotNull(this.scale.getYAxis());
  }

  @Test
  void mementoRoundTripPreservesProperties() {
    SGAxisScale scale = this.createScale();
    assertTrue(scale.setLineWidth(1.0f, "pt"));
    assertTrue(scale.setSpace(0.5f, "cm"));

    SGProperties p = scale.getProperties();
    assertNotNull(p);

    SGAxisScale restored = new SGAxisScale(new SGFigureElementAxis());
    assertTrue(restored.setProperties(p));
    assertEquals(scale.getLineWidth(), restored.getLineWidth(), 1.0e-6f);
    assertEquals(scale.getFontSize(), restored.getFontSize(), 1.0e-6f);
  }

  @Test
  void setPropertiesRejectsForeignProperties() {
    SGAxisScale scale = this.createScale();
    assertFalse(scale.setProperties(new SGProperties() {}));
  }
}
