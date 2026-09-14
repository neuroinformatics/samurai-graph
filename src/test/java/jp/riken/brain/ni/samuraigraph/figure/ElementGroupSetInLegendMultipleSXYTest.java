package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.data.SGDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGNumberDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayMultipleData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for legend group sets for multiple SXY data. */
class ElementGroupSetInLegendMultipleSXYTest {

  private SGFigureElementLegend legend;

  private SGSXYSDArrayMultipleData data;

  @BeforeEach
  void setUp() {
    this.legend = new SGFigureElementLegend();
    assertTrue(this.legend.setAxisElement(new SGFigureElementAxis()));
    assertTrue(this.legend.setGraphElement(new SGFigureElementGraph()));
    this.data = this.createData();
  }

  private SGSXYSDArrayMultipleData createData() {
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

  @AfterEach
  void disposeLegend() {
    if (this.legend != null) {
      this.legend.dispose();
      this.legend = null;
    }
  }

  @Test
  void multipleSXYDataIsAddedAsALegendGroupSet() {
    assertTrue(this.legend.addData(this.data, "Multi"));
    List<SGIChildObject> children = this.legend.getVisibleChildList();
    assertEquals(1, children.size());
    ElementGroupSetInLegendMultipleSXY groupSet =
        (ElementGroupSetInLegendMultipleSXY) children.get(0);
    assertEquals("Multi", groupSet.getName());
    assertEquals(2, groupSet.getChildNumber());
  }

  @Test
  void multiGroupSetHasLineGroups() {
    assertTrue(this.legend.addData(this.data, "Multi"));
    List<SGIChildObject> children = this.legend.getVisibleChildList();
    ElementGroupSetInLegendMultipleSXY groupSet =
        (ElementGroupSetInLegendMultipleSXY) children.get(0);
    // the parent group plus one line group per child group set
    assertEquals(3, SGMultipleSXYUtility.getLineGroups(groupSet).size());
    assertEquals(3, SGMultipleSXYUtility.getSymbolGroups(groupSet).size());
  }
}
