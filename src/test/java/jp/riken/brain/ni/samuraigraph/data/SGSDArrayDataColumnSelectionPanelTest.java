package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests of the sdarray data column selection panel. */
class SGSDArrayDataColumnSelectionPanelTest {

  private SGSDArrayDataColumnSelectionPanel panel;

  @BeforeEach
  void createPanelWithData() {
    this.panel = new SGSDArrayDataColumnSelectionPanel();
    final SGSDArrayDataColumnInfo[] columns = {
      new SGSDArrayDataColumnInfo("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER),
      new SGSDArrayDataColumnInfo("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER),
      new SGSDArrayDataColumnInfo("height", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER)
    };
    assertTrue(
        this.panel.setData(
            SGDataTypeConstants.SXY_DATA,
            new SGDataColumnInfoSet(columns),
            new HashMap<String, Object>(),
            false));
  }

  @Test
  void thePanelFillsTheTableFromTheColumns() {
    assertEquals(3, this.panel.getRowCount());
    assertEquals(3, this.panel.getDataColumnNum());
    assertEquals("x", this.panel.getTitles()[0]);
    assertEquals(SGDataTypeConstants.SXY_DATA, this.panel.getDataType());
  }

  @Test
  void theValueArraysReadTheTableColumns() {
    assertEquals(3, this.panel.getTitles().length);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, this.panel.getValueTypes()[0]);
    assertEquals(3, this.panel.getColumnTypes().length);
  }

  @Test
  void theSetColumnTypesSyncTheColumnInfo() {
    this.panel.setColumnType(0, SGDataColumnTypeConstants.X_VALUE);
    this.panel.setColumnType(1, SGDataColumnTypeConstants.Y_VALUE);
    final String[] columnTypes = this.panel.getColumnTypes();
    assertEquals(SGDataColumnTypeConstants.X_VALUE, columnTypes[0]);
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, columnTypes[1]);
    assertEquals(
        SGDataColumnTypeConstants.X_VALUE, this.panel.getDataColumnInfoArray()[0].getColumnType());
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
                new SGDataColumnInfoSet(new SGSDArrayDataColumnInfo[0]),
                new HashMap<String, Object>(),
                false));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            this.panel.setData(
                "unsupported-type",
                new SGDataColumnInfoSet(new SGSDArrayDataColumnInfo[0]),
                new HashMap<String, Object>(),
                false));
  }

  @Test
  void theDataTypeRoundTrips() {
    assertEquals(SGDataTypeConstants.SXY_DATA, this.panel.getDataType());
    this.panel.setDataType(SGDataTypeConstants.VXY_DATA);
    assertEquals(SGDataTypeConstants.VXY_DATA, this.panel.getDataType());
  }
}
