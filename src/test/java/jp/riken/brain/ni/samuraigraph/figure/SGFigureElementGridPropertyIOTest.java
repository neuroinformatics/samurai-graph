package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the property round trip of {@link SGFigureElementGrid}. */
class SGFigureElementGridPropertyIOTest {

  private SGFigureElementGrid grid;

  @AfterEach
  void disposeGrid() {
    if (this.grid != null) {
      this.grid.dispose();
    }
  }

  private SGFigureElementGrid createGrid() {
    this.grid = new SGFigureElementGrid();
    return this.grid;
  }

  @Test
  void gridCanBeCreatedAndDisposed() {
    SGFigureElementGrid grid = this.createGrid();
    assertNotNull(grid);
  }

  @Test
  void mementoRoundTripPreservesAppearance() {
    SGFigureElementAxis axisElement = new SGFigureElementAxis();
    SGFigureElementGrid grid = this.createGrid();
    grid.setAxisElement(axisElement);
    assertTrue(grid.setColor(Color.GREEN));
    assertTrue(grid.setLineType(2));
    assertTrue(grid.setLineWidth(3.0f, "pt"));
    assertTrue(grid.setGridVisible(false));
    assertTrue(grid.setAutoRangeFlag(true));
    assertEquals(2, grid.getLineType());

    SGProperties p = grid.getProperties();
    assertNotNull(p);

    SGFigureElementGrid restored = new SGFigureElementGrid();
    SGFigureElementAxis restoredAxis = new SGFigureElementAxis();
    restored.setAxisElement(restoredAxis);
    assertTrue(restored.setProperties(p));
    assertEquals(Color.GREEN, restored.getColor());
    assertEquals(2, restored.getLineType());
    assertEquals(3.0f, restored.getLineWidth("pt"), 0.001f);
    assertFalse(restored.isGridVisible());
    assertTrue(restored.isAutoRange());
    restored.dispose();
    restoredAxis.dispose();
    axisElement.dispose();
  }

  @Test
  void setPropertiesRejectsNonGridProperties() {
    SGFigureElementGrid grid = this.createGrid();
    assertFalse(grid.setProperties(new SGProperties() {}));
  }
}
