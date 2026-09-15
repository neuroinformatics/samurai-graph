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

/** Interaction tests for the in-graph symbol and bar groups. */
class SGElementGroupInGraphInteractionTest {

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

  private SGElementGroupSetInGraphSXY getGroupSet() {
    List<SGIChildObject> children = this.graph.getVisibleChildList();
    return (SGElementGroupSetInGraphSXY) children.get(0);
  }

  @Test
  void barGroupWidthCanBeChangedThroughTheGroupSet() {
    assertTrue(this.graph.addData(this.data, "bar"));
    List<SGIChildObject> children = this.graph.getVisibleChildList();
    SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) children.get(0);
    SGElementGroupBarInGraph barGroup = (SGElementGroupBarInGraph) groupSet.getBarGroup();
    assertNotNull(barGroup);
    assertTrue(groupSet.isBarVisible() == barGroup.isVisible());
  }

  @Test
  void unknownClickLocationIsNotHandled() {
    assertTrue(this.graph.addData(this.data, "outside"));
    List<SGIChildObject> children = this.graph.getVisibleChildList();
    SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) children.get(0);
    assertTrue(groupSet.updateWithData());
    // the layout locations are updated with the data paths
    assertTrue(this.graph.updateDrawingElementsLocation(this.data));
    assertNotNull(groupSet.getLineGroup());
    assertNotNull(groupSet.getSymbolGroup());
  }
}
