package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.data.SGDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGNumberDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Integration tests for adding real single SXY data to the graph element. */
class SGFigureElementGraphSXYIntegrationTest {

  private SGFigureElementGraph graph;

  private SGSXYSDArrayData data;

  @BeforeEach
  void setUp() {
    this.graph = new SGFigureElementGraph();
    assertTrue(this.graph.setAxisElement(new SGFigureElementAxis()));
    this.data = this.createData();
  }

  private SGSXYSDArrayData createSingleSXYData() {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
      new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0, 8.0})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    return new SGSXYSDArrayData(
        file,
        new SGDataSourceObserver(),
        0,
        1,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        true,
        null);
  }

  private SGSXYSDArrayData createData() {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
      new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0, 8.0})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    return new SGSXYSDArrayData(
        file,
        new SGDataSourceObserver(),
        0,
        1,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        true,
        null);
  }

  @AfterEach
  void disposeGraphElement() {
    if (this.graph != null) {
      this.graph.dispose();
      this.graph = null;
    }
  }

  @Test
  void singleSXYDataIsAddedAsAnInGraphGroupSet() {
    assertTrue(this.graph.addData(this.data, "Graph Data 1"));
    List<SGIChildObject> children = this.graph.getVisibleChildList();
    assertEquals(1, children.size());
    SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) children.get(0);
    assertEquals("Graph Data 1", groupSet.getName());
    assertNotNull(groupSet.getFigureElement());
  }

  @Test
  void lineSymbolAndBarGroupsAreCreated() {
    assertTrue(this.graph.addData(this.data, "Graph Data 2"));
    List<SGIChildObject> children = this.graph.getVisibleChildList();
    SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) children.get(0);
    assertNotNull(groupSet.getLineGroup());
    assertNotNull(groupSet.getSymbolGroup());
    assertNotNull(groupSet.getBarGroup());
    // the group states reflect the visibility setup of this graph
    assertTrue(groupSet.isVisible());
  }
}
