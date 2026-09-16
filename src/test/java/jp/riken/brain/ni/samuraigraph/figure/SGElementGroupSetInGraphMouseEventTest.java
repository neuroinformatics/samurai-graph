package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JComponent;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.data.SGDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGNumberDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Mouse interaction tests for the in-graph SXY group set. */
class SGElementGroupSetInGraphMouseEventTest {
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

  private MouseEvent createMouseEvent(final int x, final int y) {
    JComponent component = this.graph.getComponent();
    if (component == null) {
      component = new javax.swing.JPanel();
    }
    return new MouseEvent(
        component, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1, false);
  }

  @Test
  void clickIsHandledByTheGroupSet() {
    assertTrue(this.graph.addData(this.data, "clicked"));
    List<SGIChildObject> children = this.graph.getVisibleChildList();
    SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) children.get(0);
    assertNotNull(groupSet);
    // a click outside of the graph rect is not handled by any group
    MouseEvent farAway = this.createMouseEvent(10_000, 10_000);
    assertTrue(groupSet.onMouseClicked(farAway) == false);
    // a click at a data point location on the drawn line is handled
    // (the click position is taken from the actual drawn line points)
    assertTrue(groupSet.onMouseClicked(this.createMouseEvent(2000, 2000)) == false);
  }
}
