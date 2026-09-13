package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the property round trip of {@link SGFigureElementGraph}. */
class SGFigureElementGraphPropertyIOTest {

  private SGFigureElementGraph graph;

  @AfterEach
  void disposeGraph() {
    if (this.graph != null) {
      this.graph.dispose();
    }
  }

  @Test
  void graphCanBeCreatedAndDisposed() {
    this.graph = new SGFigureElementGraph();
    assertNotNull(this.graph);
  }

  @Test
  void mementoRoundTripPreservesElementState() {
    SGFigureElementGraph graph = this.graph = new SGFigureElementGraph();
    assertTrue(graph.setGraphRect(10.0f, 20.0f, 100.0f, 50.0f));
    SGProperties p = graph.getProperties();
    assertNotNull(p);
    SGFigureElementGraph restored = new SGFigureElementGraph();
    assertTrue(restored.setProperties(p));
    assertNotNull(restored.getProperties());
    restored.dispose();
  }

  @Test
  void setPropertiesRejectsNonGraphProperties() {
    SGFigureElementGraph graph = this.graph = new SGFigureElementGraph();
    assertFalse(graph.setProperties(new SGProperties() {}));
  }
}
