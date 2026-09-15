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
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYSDArrayData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Integration tests for adding vector, map and multiple data to the graph element. */
class SGFigureElementGraphTypeIntegrationTest {

  private SGFigureElementGraph graph;

  @BeforeEach
  void setUp() {
    this.graph = new SGFigureElementGraph();
    assertTrue(this.graph.setAxisElement(new SGFigureElementAxis()));
  }

  @AfterEach
  void disposeGraphElement() {
    if (this.graph != null) {
      this.graph.dispose();
      this.graph = null;
    }
  }

  private SGVXYSDArrayData createVXYData() {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
      new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0, 8.0}),
      new SGNumberDataColumn("c1", new double[] {0.1, 0.2, 0.3, 0.4}),
      new SGNumberDataColumn("c2", new double[] {0.4, 0.5, 0.6, 0.7})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    return new SGVXYSDArrayData(file, new SGDataSourceObserver(), 0, 1, 2, 3, false, null, true);
  }

  private SGSXYZSDArrayData createSXYZData() {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0}),
      new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0}),
      new SGNumberDataColumn("z", new double[] {0.5, 1.5, 2.5})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    return new SGSXYZSDArrayData(file, new SGDataSourceObserver(), 0, 1, 2, null, true);
  }

  private SGSXYSDArrayMultipleData createMultipleSXYData() {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
      new SGNumberDataColumn("y1", new double[] {5.0, 6.0, 7.0, 8.0}),
      new SGNumberDataColumn("y2", new double[] {9.0, 10.0, 11.0, 12.0})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    return new SGSXYSDArrayMultipleData(
        file,
        new SGDataSourceObserver(),
        new Integer[] {0},
        new Integer[] {1, 2},
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

  @Test
  void vectorDataIsAddedAsAnInGraphGroupSet() {
    assertTrue(this.graph.addData(this.createVXYData(), "Vectors"));
    List<SGIChildObject> children = this.graph.getVisibleChildList();
    assertEquals(1, children.size());
    assertTrue(children.get(0) instanceof SGElementGroupSetInGraphVXY);
    SGElementGroupSetInGraphVXY groupSet = (SGElementGroupSetInGraphVXY) children.get(0);
    assertEquals("Vectors", groupSet.getName());
    assertNotNull(groupSet.getArrowGroup());
  }

  @Test
  void colorMapDataIsAddedAsAnInGraphGroupSet() {
    SGSXYZSDArrayData data = this.createSXYZData();
    assertTrue(this.graph.addData(data, "Map"));
    List<SGIChildObject> children = this.graph.getVisibleChildList();
    assertEquals(1, children.size());
    SGElementGroupSetInGraphSXYZ groupSet = (SGElementGroupSetInGraphSXYZ) children.get(0);
    assertNotNull(groupSet.getColorMapGroup());
    assertEquals("Map", groupSet.getName());
  }

  @Test
  void multipleSXYDataIsAddedAsAnInGraphGroupSet() {
    assertTrue(this.graph.addData(this.createMultipleSXYData(), "Multi"));
    List<SGIChildObject> children = this.graph.getVisibleChildList();
    assertEquals(1, children.size());
    SGElementGroupSetInGraphSXYMultiple groupSet =
        (SGElementGroupSetInGraphSXYMultiple) children.get(0);
    assertEquals("Multi", groupSet.getName());
    assertEquals(2, groupSet.getChildNumber());
  }
}
