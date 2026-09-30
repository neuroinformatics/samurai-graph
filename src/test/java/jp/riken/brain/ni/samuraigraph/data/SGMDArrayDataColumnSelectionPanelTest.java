package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests of the mdarray data column selection panel. */
class SGMDArrayDataColumnSelectionPanelTest {

  private SGMDArrayDataColumnSelectionPanel panel;

  @BeforeEach
  void createPanelWithData() throws Exception {
    this.panel = new SGMDArrayDataColumnSelectionPanel();
    assertTrue(
        this.panel.setData(
            SGDataTypeConstants.SXY_MATLAB_DATA,
            new SGDataColumnInfoSet(buildColumns()),
            new HashMap<String, Object>(),
            false));
  }

  private static SGMDArrayDataColumnInfo[] buildColumns() throws Exception {
    final Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    final MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    final MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b));
    final MatFileReader reader = new MatFileReader(path.toFile());
    final SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    return SGDataFileUtility.getMDArrayDataColumnInfo(
        matFile, matFile.getVariables(), new HashMap<String, Object>());
  }

  @Test
  void thePanelFillsTheSxyTableFromTheColumns() {
    assertEquals(2, this.panel.getRowCount());
    assertEquals(2, this.panel.getDataColumnNum());
    // the SXY layout has the picked-up dimension column
    assertEquals(7, this.panel.getTable().getColumnCount());
    assertEquals("a", this.panel.getDataColumnInfoArray()[0].getName());
  }

  @Test
  void theTimeDimensionColumnShowsTheUnselectedValue() {
    final int colIndex = this.panel.getColumnIndex("Animation Frame");
    assertEquals(" ", this.panel.getValueAt(0, colIndex));
  }

  @Test
  void theTimeDimensionIndexIsReflectedInTheTable() {
    this.panel.setTimeDimensionIndex(0, 0);
    final int colIndex = this.panel.getColumnIndex("Animation Frame");
    assertEquals("0", this.panel.getValueAt(0, colIndex));
    // an index of -1 is the unselected value
    this.panel.setTimeDimensionIndex(0, -1);
    assertEquals(" ", this.panel.getValueAt(0, colIndex));
  }

  @Test
  void theGenericAndPickUpDimensionIndexesAreReflectedInTheTable() {
    this.panel.setGenericDimensionIndex(0, 0);
    final int dimIndex = this.panel.getColumnIndex("Dimension");
    assertEquals(0, this.panel.getValueAt(0, dimIndex));
    this.panel.setPickUpDimensionIndex(1, 0);
    final int pickUpIndex = this.panel.getColumnIndex("PickUp");
    assertEquals("0", this.panel.getValueAt(1, pickUpIndex));
  }

  @Test
  void checkSelectedItemsAcceptsAValidXAndYAssignment() {
    this.panel.setColumnType(0, SGDataColumnTypeConstants.X_VALUE);
    this.panel.setColumnType(1, SGDataColumnTypeConstants.Y_VALUE);
    assertTrue(this.panel.checkSelectedItems());
  }

  @Test
  void checkSelectedItemsRejectsMissingValueColumns() {
    assertFalse(this.panel.checkSelectedItems());
  }

  @Test
  void checkSelectedItemsRejectsOverlappingDimensionIndexes() {
    this.panel.setColumnType(0, SGDataColumnTypeConstants.X_VALUE);
    this.panel.setColumnType(1, SGDataColumnTypeConstants.Y_VALUE);
    // the same dimension index for the generic and the time dimension
    // of one row is rejected
    this.panel.setGenericDimensionIndex(0, 0);
    this.panel.setTimeDimensionIndex(0, 0);
    assertFalse(this.panel.checkSelectedItems());
  }

  @Test
  void theClearResetsTheColumnTypes() {
    this.panel.setColumnType(0, SGDataColumnTypeConstants.X_VALUE);
    this.panel.clearSelectedColumns();
    assertEquals("", this.panel.getColumnTypes()[0]);
  }

  @Test
  void thePanelRejectsInvalidDataArguments() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            this.panel.setData(
                null,
                new SGDataColumnInfoSet(new SGMDArrayDataColumnInfo[0]),
                new HashMap<String, Object>(),
                false));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            this.panel.setData(
                "unsupported-type",
                new SGDataColumnInfoSet(new SGMDArrayDataColumnInfo[0]),
                new HashMap<String, Object>(),
                false));
  }
}
