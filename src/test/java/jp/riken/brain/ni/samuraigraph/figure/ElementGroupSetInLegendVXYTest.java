package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.data.SGDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGNumberDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGVXYSDArrayData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for legend group sets for vector (VXY) data. */
class ElementGroupSetInLegendVXYTest {

  private SGFigureElementLegend legend;

  private SGVXYSDArrayData data;

  @BeforeEach
  void setUp() {
    this.legend = new SGFigureElementLegend();
    assertTrue(this.legend.setAxisElement(new SGFigureElementAxis()));
    assertTrue(this.legend.setGraphElement(new SGFigureElementGraph()));
    this.data = this.createData();
  }

  private SGVXYSDArrayData createData() {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
      new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0, 8.0}),
      new SGNumberDataColumn("c1", new double[] {0.1, 0.2, 0.3, 0.4}),
      new SGNumberDataColumn("c2", new double[] {0.4, 0.5, 0.6, 0.7})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    return new SGVXYSDArrayData(file, new SGDataSourceObserver(), 0, 1, 2, 3, false, null, true);
  }

  @AfterEach
  void disposeLegend() {
    if (this.legend != null) {
      this.legend.dispose();
      this.legend = null;
    }
  }

  @Test
  void vectorDataIsAddedAsALegendGroupSet() {
    assertTrue(this.legend.addData(this.data, "Vectors"));
    List<SGIChildObject> children = this.legend.getVisibleChildList();
    assertEquals(1, children.size());
    ElementGroupSetInLegendVXY gs = (ElementGroupSetInLegendVXY) children.get(0);
    assertEquals("Vectors", gs.getName());
    assertTrue(gs.getMagnitudePerCM() > 0.0f);
    assertFalse(gs.isDirectionInvariant());
  }

  @Test
  void directionInvariantCanBeToggled() {
    assertTrue(this.legend.addData(this.data, "Vectors"));
    List<SGIChildObject> children = this.legend.getVisibleChildList();
    ElementGroupSetInLegendVXY groupSet = (ElementGroupSetInLegendVXY) children.get(0);
    assertTrue(groupSet.setDirectionInvariant(true));
    assertTrue(groupSet.isDirectionInvariant());
  }
}
