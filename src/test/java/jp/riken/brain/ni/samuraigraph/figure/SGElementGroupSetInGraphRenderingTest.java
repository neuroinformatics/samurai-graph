package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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

/** Rendering smoke tests for the in-graph group sets with real single SXY data. */
class SGElementGroupSetInGraphRenderingTest {

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

  @Test
  void groupSetIsPaintedWithoutErrors() {
    assertTrue(this.graph.addData(this.data, "Data"));
    List<SGIChildObject> children = this.graph.getVisibleChildList();
    SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) children.get(0);

    assertTrue(groupSet.updateWithData());
    BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
    try {
      groupSet.paintGraphics2D(g2d);
    } finally {
      g2d.dispose();
    }
  }

  @Test
  void lineAndSymbolGroupsArePainted() {
    assertTrue(this.graph.addData(this.data, "Painted"));
    List<SGIChildObject> children = this.graph.getVisibleChildList();
    SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) children.get(0);
    assertTrue(groupSet.updateWithData());
    BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
    try {
      groupSet.paintGraphics2D(g2d);
      groupSet.getLineGroup().paintElement(g2d, null);
      groupSet.getSymbolGroup().paintElement(g2d, null);
      groupSet.getBarGroup().paintElement(g2d, null);
    } finally {
      g2d.dispose();
    }
  }
}
