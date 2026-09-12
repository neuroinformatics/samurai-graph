package jp.riken.brain.ni.samuraigraph.figure.java2d;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Font;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the property round trip of {@link SGFigureElementLegend}. */
class SGFigureElementLegendPropertyIOTest {

  private SGFigureElementLegend legend;

  private SGFigureElementLegend createLegend() {
    this.legend = new SGFigureElementLegend();
    return this.legend;
  }

  @AfterEach
  void disposeLegend() {
    if (this.legend != null) {
      this.legend.dispose();
    }
  }

  @Test
  void legendCanBeCreatedAndDisposed() {
    SGFigureElementLegend legend = this.createLegend();
    assertNotNull(legend);
    assertTrue(legend.isVisible());
    assertEquals("Legend", legend.getClassDescription());
  }

  @Test
  void mementoRoundTripPreservesAppearance() {
    SGFigureElementLegend legend = this.createLegend();
    assertTrue(legend.setFontSize(14.0f, "pt"));
    assertTrue(legend.setFontStyle(Font.BOLD));
    assertTrue(legend.setFontName("Serif"));
    assertTrue(legend.setFrameLineWidth(2.0f, "pt"));
    assertTrue(legend.setFrameLineColor(Color.BLUE));
    assertTrue(legend.setBackgroundColor(Color.RED));
    assertTrue(legend.setBackgroundTransparent(30));
    assertTrue(legend.setSymbolSpan(0.5f, "cm"));
    assertTrue(legend.setFrameVisible(false));
    assertTrue(legend.setLegendLocation(100.0f, 50.0f));

    SGProperties p = legend.getProperties();
    assertNotNull(p);

    SGFigureElementLegend restored = new SGFigureElementLegend();
    assertTrue(restored.setProperties(p));
    assertEquals(14.0f, restored.getFontSize("pt"), 0.001f);
    assertEquals(Font.BOLD, restored.getFontStyle());
    assertEquals("Serif", restored.getFontName());
    assertEquals(2.0f, restored.getFrameLineWidth("pt"), 0.001f);
    assertEquals(Color.BLUE, restored.getFrameColor());
    assertEquals(Color.RED, restored.getBackgroundColor());
    assertEquals(30, restored.getBackgroundTransparency());
    assertEquals(0.5f, restored.getSymbolSpan("cm"), 0.001f);
    assertFalse(restored.isFrameVisible());
    assertEquals(100.0f, restored.getLegendX(), 0.001f);
    assertEquals(50.0f, restored.getLegendY(), 0.001f);
    restored.dispose();
  }

  @Test
  void setPropertiesRejectsNonLegendProperties() {
    SGFigureElementLegend legend = this.createLegend();
    assertFalse(legend.setProperties(new SGProperties() {}));
  }
}
