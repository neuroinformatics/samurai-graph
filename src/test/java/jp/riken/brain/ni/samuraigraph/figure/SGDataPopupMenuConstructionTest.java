package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.data.SGDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGNumberDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Headful tests for the data pop-up menus on a real window. */
class SGDataPopupMenuConstructionTest {

  private SGDrawingWindow wnd;

  private SGFigureElementGraph graph;

  @AfterEach
  void disposeAll() {
    if (this.graph != null) {
      this.graph.dispose();
      this.graph = null;
    }
    if (this.wnd != null) {
      this.wnd.dispose();
      this.wnd = null;
    }
  }

  private void runOnEdt(final Runnable runnable) {
    try {
      javax.swing.SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private SGFigureElementGraph createGraphWithData() {
    SGFigureElementGraph graph = new SGFigureElementGraph();
    assertTrue(graph.setAxisElement(new SGFigureElementAxis()));
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
      new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0, 8.0})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    SGSXYSDArrayData data =
        new SGSXYSDArrayData(
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
    assertTrue(graph.addData(data, "popup"));
    return graph;
  }

  @Test
  void sxyDataPopupMenuCanBeConstructed() {
    this.runOnEdt(
        new Runnable() {
          @Override
          public void run() {
            SGDataPopupMenuConstructionTest.this.wnd = new SGDrawingWindow();
            assertTrue(wnd.init());
            SGFigureElementGraph graph = createGraphWithData();
            List<SGIChildObject> children = graph.getVisibleChildList();
            SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) children.get(0);
            SGSXYDataPopupMenu menu = new SGSXYDataPopupMenu(wnd, groupSet, true);
            assertNotNull(menu);
            assertNotNull(graph);
          }
        });
  }
}
