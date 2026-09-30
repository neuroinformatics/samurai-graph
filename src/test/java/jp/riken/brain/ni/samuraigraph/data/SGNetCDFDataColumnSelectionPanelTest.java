package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

/** Unit tests of the NetCDF data column selection panel. */
class SGNetCDFDataColumnSelectionPanelTest {

  private SGNetCDFDataColumnSelectionPanel panel;

  @BeforeEach
  void createPanel() throws IOException {
    this.panel = new SGNetCDFDataColumnSelectionPanel();
    final SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    final SGNetCDFDataColumnInfo[] columns =
        SGDataFileUtility.getNetCDFDataColumnInfo(
            file.getVariables(), new HashMap<String, Object>());
    final SGDataColumnInfoSet set = new SGDataColumnInfoSet(columns);
    assertTrue(
        this.panel.setData(
            SGDataTypeConstants.SXY_NETCDF_DATA, set, new HashMap<String, Object>(), false));
  }

  @Test
  void thePanelFillsTheTableFromTheColumns() {
    assertEquals(5, this.panel.getRowCount());
    assertEquals(5, this.panel.getDataColumnNum());
    assertEquals("x", this.panel.getDataColumnInfoArray()[0].getName());
    assertEquals(SGDataTypeConstants.SXY_NETCDF_DATA, this.panel.getDataType());
  }

  @Test
  void theValueArraysReadTheTableColumns() {
    // the NetCDF panel has no Title column, only Value Type and Column Type
    assertEquals(5, this.panel.getValueTypes().length);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, this.panel.getValueTypes()[0]);
    assertNotNull(this.panel.getColumnTypes());
    assertEquals(5, this.panel.getColumnTypes().length);
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
  void theColumnTypeCellEditorAndRendererAreInstalled() {
    final int colIndex =
        this.panel.getColumnIndex(SGDataColumnSelectionPanel.COLUMN_NAME_COLUMN_TYPE);
    assertNotNull(this.panel.getTable().getColumnModel().getColumn(colIndex).getCellRenderer());
    assertNotNull(this.panel.getTable().getColumnModel().getColumn(colIndex).getCellEditor());
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
                new SGDataColumnInfoSet(new SGDataColumnInfo[0]),
                new HashMap<String, Object>(),
                false));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            this.panel.setData(
                "unsupported-type",
                new SGDataColumnInfoSet(new SGDataColumnInfo[0]),
                new HashMap<String, Object>(),
                false));
  }

  @Test
  void theDataTypeRoundTrips() {
    assertEquals(SGDataTypeConstants.SXY_NETCDF_DATA, this.panel.getDataType());
    this.panel.setDataType(SGDataTypeConstants.VXY_NETCDF_DATA);
    assertEquals(SGDataTypeConstants.VXY_NETCDF_DATA, this.panel.getDataType());
  }
}
