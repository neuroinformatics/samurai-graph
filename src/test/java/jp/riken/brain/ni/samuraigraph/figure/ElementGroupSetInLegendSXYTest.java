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
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for legend group sets for single SXY data. */
class ElementGroupSetInLegendSXYTest {

  private SGFigureElementLegend legend;

  private SGSXYSDArrayData data;

  @BeforeEach
  void setUp() {
    this.legend = new SGFigureElementLegend();
    assertTrue(this.legend.setAxisElement(new SGFigureElementAxis()));
    assertTrue(this.legend.setGraphElement(new SGFigureElementGraph()));
    this.data = this.createData();
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
  void disposeLegend() {
    if (this.legend != null) {
      this.legend.dispose();
      this.legend = null;
    }
  }

  @Test
  void dataIsAddedAsALegendGroupSet() {
    assertTrue(this.legend.addData(this.data, "Data 1"));
    List<SGIChildObject> children = this.legend.getVisibleChildList();
    assertEquals(1, children.size());
    assertTrue(children.get(0) instanceof ElementGroupSetInLegendSXY);
    ElementGroupSetInLegendSXY groupSet = (ElementGroupSetInLegendSXY) children.get(0);
    assertEquals("Data 1", groupSet.getName());
  }

  @Test
  void legendVisibilityTogglesDoNotAffectDataVisibility() {
    assertTrue(this.legend.addData(this.data, "Data 1"));
    List<SGIChildObject> children = this.legend.getVisibleChildList();
    ElementGroupSetInLegendSXY groupSet = (ElementGroupSetInLegendSXY) children.get(0);
    assertTrue(groupSet.isVisibleInLegend());
    assertTrue(groupSet.setVisibleInLegend(false));
    assertFalse(groupSet.isVisibleInLegend());
    assertTrue(groupSet.setVisibleInLegend(true));
    assertTrue(groupSet.isVisibleInLegend());
    groupSet.setName("Renamed");
    assertTrue(groupSet.getName() != null);
  }

  @Test
  void removalNullsTheGroupSetData() {
    assertTrue(this.legend.addData(this.data, "Data 1"));
    assertTrue(this.legend.removeData(this.data));
    assertTrue(this.legend.getVisibleChildList().get(0) instanceof ElementGroupSetInLegendSXY);
  }
}
