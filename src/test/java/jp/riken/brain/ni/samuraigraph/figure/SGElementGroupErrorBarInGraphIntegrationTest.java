package jp.riken.brain.ni.samuraigraph.figure;

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

/** Error bar integration tests of the in-graph SXY group set. */
class SGElementGroupErrorBarInGraphIntegrationTest {

  private SGFigureElementGraph graph;

  private SGSXYSDArrayData data;

  @BeforeEach
  void setUp() {
    this.graph = new SGFigureElementGraph();
    assertTrue(this.graph.setAxisElement(new SGFigureElementAxis()));
    assertTrue(this.graph.setGraphRect(10.0f, 10.0f, 100.0f, 100.0f));
    this.data = this.createData();
  }

  @AfterEach
  void disposeGraphElement() {
    if (this.graph != null) {
      this.graph.dispose();
      this.graph = null;
    }
  }

  private SGSXYSDArrayData createData() {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
      new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0, 8.0}),
      new SGNumberDataColumn("eLow", new double[] {0.1, 0.1, 0.1, 0.1}),
      new SGNumberDataColumn("eHigh", new double[] {0.2, 0.2, 0.2, 0.2})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    return new SGSXYSDArrayData(
        file, new SGDataSourceObserver(), 0, 1, 2, 3, 1, null, null, null, null, true, null);
  }

  @Test
  void errorBarGroupIsCreatedFromErrorColumns() {
    assertTrue(this.graph.addData(this.data, "with error"));
    List<SGIChildObject> children = this.graph.getVisibleChildList();
    SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) children.get(0);
    assertNotNull(groupSet.getErrorBarGroup());
    assertTrue(groupSet.isErrorBarAvailable());
  }

  @Test
  void errorBarGroupKeepsLineWidth() {
    assertTrue(this.graph.addData(this.data, "error bar"));
    List<SGIChildObject> children = this.graph.getVisibleChildList();
    SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) children.get(0);

    assertTrue(groupSet.isErrorBarAvailable());
    assertTrue(groupSet.getErrorBarGroup().isPositionOnLine());
    assertTrue(groupSet.getErrorBarGroup().isVertical());
  }
}
