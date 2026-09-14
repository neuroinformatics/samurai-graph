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
import jp.riken.brain.ni.samuraigraph.data.SGSXYZSDArrayData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for legend group sets for SXYZ (color map) data. */
class ElementGroupSetInLegendSXYZTest {

  private SGFigureElementLegend legend;

  private SGSXYZSDArrayData data;

  @BeforeEach
  void setUp() {
    this.legend = new SGFigureElementLegend();
    assertTrue(this.legend.setAxisElement(new SGFigureElementAxis()));
    assertTrue(this.legend.setGraphElement(new SGFigureElementGraph()));
    this.data = this.createData();
  }

  private SGSXYZSDArrayData createData() {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0}),
      new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0}),
      new SGNumberDataColumn("z", new double[] {0.5, 1.5, 2.5})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    return new SGSXYZSDArrayData(file, new SGDataSourceObserver(), 0, 1, 2, null, true);
  }

  @AfterEach
  void disposeLegend() {
    if (this.legend != null) {
      this.legend.dispose();
      this.legend = null;
    }
  }

  @Test
  void colorMapDataIsAddedAsALegendGroupSet() {
    assertTrue(this.legend.addData(this.data, "Map"));
    List<SGIChildObject> children = this.legend.getVisibleChildList();
    assertEquals(1, children.size());
    ElementGroupSetInLegendSXYZ groupSet = (ElementGroupSetInLegendSXYZ) children.get(0);
    assertEquals("Map", groupSet.getName());
    assertNotNull(groupSet.getColorMap());
    assertNotNull(groupSet.getFigureElement());
  }

  @Test
  void colorMapGroupIsPaintedWhenVisible() {
    assertTrue(this.legend.addData(this.data, "Map"));
    List<SGIChildObject> children = this.legend.getVisibleChildList();
    ElementGroupSetInLegendSXYZ groupSet = (ElementGroupSetInLegendSXYZ) children.get(0);
    SGElementGroupPseudocolorMap colorMap = groupSet.getColorMap();
    assertNotNull(colorMap);
    assertTrue(colorMap.isVisible());
  }

  @Test
  void removalKeepsTheGroupSetEntry() {
    assertTrue(this.legend.addData(this.data, "Map"));
    assertTrue(this.legend.removeData(this.data));
    List<SGIChildObject> children = this.legend.getVisibleChildList();
    assertTrue(children.get(0) instanceof ElementGroupSetInLegendSXYZ);
  }
}
