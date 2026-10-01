package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests of the table models and the cell editors of the mdarray column panel. */
class SGMDArrayDataColumnSelectionPanelEditorTest {

  private SGMDArrayDataColumnSelectionPanel panel;

  @BeforeEach
  void createPanelWithData() throws Exception {
    this.panel = new SGMDArrayDataColumnSelectionPanel();
    final Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    assertTrue(
        this.panel.setData(
            SGDataTypeConstants.SXY_MATLAB_DATA,
            new SGDataColumnInfoSet(buildColumns()),
            infoMap,
            false));
  }

  private static SGMDArrayDataColumnInfo[] buildColumns() throws Exception {
    return buildColumns(2);
  }

  private static SGMDArrayDataColumnInfo[] buildColumns(final int variableNum) throws Exception {
    final Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    final List<MLArray> vars = new ArrayList<MLArray>();
    for (int ii = 0; ii < variableNum; ii++) {
      vars.add(
          new MLDouble(
              String.valueOf((char) ('a' + ii)),
              new double[][] {{1.0 + ii}, {2.0 + ii}, {3.0 + ii}}));
    }
    new MatFileWriter(path.toFile(), vars);
    final MatFileReader reader = new MatFileReader(path.toFile());
    final SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    return SGDataFileUtility.getMDArrayDataColumnInfo(
        matFile, matFile.getVariables(), new HashMap<String, Object>());
  }

  // -- the sxy table model -------------------------------------------------------

  @Test
  void theSxyModelFillsAllSevenCells() {
    final JTable table = this.panel.getTable();
    assertEquals(7, table.getColumnCount());
    assertEquals("1", this.panel.getValueAt(0, this.panel.getColumnIndex("No.")));
    assertEquals("a", this.panel.getValueAt(0, this.panel.getColumnIndex("Name")));
    assertEquals("Number", this.panel.getValueAt(0, this.panel.getColumnIndex("Value Type")));
    // the generic dimension defaults to -1 (unselected) for mdarray columns
    assertEquals(-1, this.panel.getValueAt(0, this.panel.getColumnIndex("Dimension")));
    assertEquals(" ", this.panel.getValueAt(0, this.panel.getColumnIndex("Animation Frame")));
    assertEquals(" ", this.panel.getValueAt(0, this.panel.getColumnIndex("PickUp")));
    assertEquals("", this.panel.getValueAt(0, this.panel.getColumnIndex("Column Type")));
  }

  @Test
  void theSxyModelReflectsTheDimensionIndexesInTheCells() {
    this.panel.setGenericDimensionIndex(0, 1);
    this.panel.setTimeDimensionIndex(0, 1);
    this.panel.setPickUpDimensionIndex(0, 1);
    // the generic dimension is stored as an integer, the others as strings
    assertEquals(1, this.panel.getValueAt(0, this.panel.getColumnIndex("Dimension")));
    assertEquals("1", this.panel.getValueAt(0, this.panel.getColumnIndex("Animation Frame")));
    assertEquals("1", this.panel.getValueAt(0, this.panel.getColumnIndex("PickUp")));
  }

  @Test
  void theSxyModelShowsTheUnselectedStringForAnInvalidPickUpIndex() {
    this.panel.setPickUpDimensionIndex(0, -1);
    assertEquals(" ", this.panel.getValueAt(0, this.panel.getColumnIndex("PickUp")));
  }

  @Test
  void theTimeIndexStringHelperFormatsTheIndex() {
    final SGDataColumnInfo[] cols = this.panel.getDataColumnInfoArray();
    final SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) cols[0];
    assertEquals(" ", SGMDArrayDataColumnSelectionPanel.getTimeIndexString(col));
    col.setDimensionIndex(SGMDArrayConstants.KEY_TIME_DIMENSION, 0);
    assertEquals("0", SGMDArrayDataColumnSelectionPanel.getTimeIndexString(col));
    col.setDimensionIndex(SGMDArrayConstants.KEY_TIME_DIMENSION, -1);
    assertEquals(" ", SGMDArrayDataColumnSelectionPanel.getTimeIndexString(col));
  }

  @Test
  void theSxyColumnCopyKeepsTheNameAndIsIndependentFromTheOriginal() {
    final SGDataColumnInfo[] cols = this.panel.getDataColumnInfoArray();
    final SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) cols[0];
    final SGMDArrayDataColumnInfo copy = new SGMDArrayDataColumnInfo(col, "a", col.getValueType());
    copy.setColumnType(SGDataColumnTypeConstants.X_VALUE);
    copy.setDimensionIndex(SGMDArrayConstants.KEY_TIME_DIMENSION, 0);
    assertEquals("a", copy.getName());
    assertEquals(SGDataColumnTypeConstants.X_VALUE, copy.getColumnType());
    assertEquals(0, copy.getTimeDimensionIndex());
    // modifying the copy does not change the original column
    assertEquals("", col.getColumnType());
    // the time dimension index defaults to -1 for mdarray columns
    assertEquals(-1, col.getTimeDimensionIndex());
  }

  // -- the multi-dimension table model ---------------------------------------------

  private SGMDArrayDataColumnSelectionPanel createSxyzPanel(final Map<String, Object> infoMap)
      throws Exception {
    final SGMDArrayDataColumnSelectionPanel md = new SGMDArrayDataColumnSelectionPanel();
    assertTrue(
        md.setData(
            SGDataTypeConstants.SXYZ_MATLAB_DATA,
            new SGDataColumnInfoSet(buildColumns()),
            infoMap,
            false));
    return md;
  }

  @Test
  void theMultiDimensionModelFillsTheFiveCells() throws Exception {
    final SGMDArrayDataColumnSelectionPanel md = createSxyzPanel(new HashMap<String, Object>());
    final JTable table = md.getTable();
    assertEquals(5, table.getColumnCount());
    assertEquals("1", md.getValueAt(0, md.getColumnIndex("No.")));
    assertEquals("a", md.getValueAt(0, md.getColumnIndex("Name")));
    assertEquals("Number", md.getValueAt(0, md.getColumnIndex("Value Type")));
    assertEquals(" ", md.getValueAt(0, md.getColumnIndex("Animation Frame")));
    assertEquals("", md.getValueAt(0, md.getColumnIndex("Column Type")));
  }

  @Test
  void theMultiDimensionModelReflectsTheTimeIndexAndTheColumnType() throws Exception {
    final SGMDArrayDataColumnSelectionPanel md = createSxyzPanel(new HashMap<String, Object>());
    md.setTimeDimensionIndex(0, 0);
    md.setColumnType(0, SGDataColumnTypeConstants.X_VALUE);
    assertEquals("0", md.getValueAt(0, md.getColumnIndex("Animation Frame")));
    assertEquals(
        SGDataColumnTypeConstants.X_VALUE, md.getValueAt(0, md.getColumnIndex("Column Type")));
  }

  @Test
  void theMultiDimensionModelFillsTheTimeCellWithTheIndexOfTheOriginalColumn() throws Exception {
    // a column that already has a time dimension index is shown with that index
    final SGMDArrayDataColumnInfo[] cols = buildColumns();
    cols[0].setDimensionIndex(SGMDArrayConstants.KEY_TIME_DIMENSION, 1);
    final SGMDArrayDataColumnSelectionPanel md = new SGMDArrayDataColumnSelectionPanel();
    assertTrue(
        md.setData(
            SGDataTypeConstants.SXYZ_MATLAB_DATA,
            new SGDataColumnInfoSet(cols),
            new HashMap<String, Object>(),
            false));
    assertEquals("1", md.getValueAt(0, md.getColumnIndex("Animation Frame")));
    // the column type of the original column is kept
    assertEquals("", md.getValueAt(0, md.getColumnIndex("Column Type")));
  }

  @Test
  void theMultiDimensionTimeEditorListsTheUnselectedValueAndTheDimensionIndexes() throws Exception {
    final SGMDArrayDataColumnSelectionPanel md = createSxyzPanel(new HashMap<String, Object>());
    final int colIndex = md.getColumnIndex("Animation Frame");
    final TableColumn col = columnOf(md, colIndex);
    final JComboBox<?> cb =
        (JComboBox<?>)
            col.getCellEditor().getTableCellEditorComponent(md.getTable(), " ", false, 0, colIndex);
    // the test variable has two dimensions (a 3x1 matrix)
    assertEquals(3, cb.getItemCount());
    assertEquals(" ", cb.getItemAt(0));
    assertEquals("0", cb.getItemAt(1));
    assertEquals("1", cb.getItemAt(2));
    assertEquals(" ", cb.getSelectedItem());
  }

  @Test
  void theMultiDimensionTimeEditorGraysOutTheXyzDimensionsOfTheZColumn() throws Exception {
    final SGMDArrayDataColumnSelectionPanel md = createSxyzPanel(new HashMap<String, Object>());
    md.setColumnType(0, SGDataColumnTypeConstants.Z_VALUE);
    md.setDimensionIndex(0, SGMDArrayConstants.KEY_SXYZ_X_DIMENSION, 1);
    final int colIndex = md.getColumnIndex("Animation Frame");
    final TableColumn col = columnOf(md, colIndex);
    final JComboBox<?> cb =
        (JComboBox<?>)
            col.getCellEditor().getTableCellEditorComponent(md.getTable(), " ", false, 0, colIndex);
    final JList<String> list = newList();
    final SGMDArrayDataColumnSelectionPanel.DimensionListCellRenderer renderer =
        (SGMDArrayDataColumnSelectionPanel.DimensionListCellRenderer) cb.getRenderer();
    assertEquals(Color.LIGHT_GRAY, labelOf(renderer, list, "1").getForeground());
    // the other indexes keep the color of the list
    assertEquals(list.getForeground(), labelOf(renderer, list, "0").getForeground());
  }

  // -- the time cell editor ---------------------------------------------------------

  @Test
  void theTimeCellEditorListsTheUnselectedValueAndTheDimensionIndexes() {
    final int colIndex = this.panel.getColumnIndex("Animation Frame");
    final TableColumn col = columnOf(this.panel, colIndex);
    final JComboBox<?> cb =
        (JComboBox<?>)
            col.getCellEditor()
                .getTableCellEditorComponent(this.panel.getTable(), " ", false, 0, colIndex);
    // the test variable has two dimensions (a 3x1 matrix)
    assertEquals(3, cb.getItemCount());
    assertEquals(" ", cb.getItemAt(0));
    assertEquals("0", cb.getItemAt(1));
    assertEquals("1", cb.getItemAt(2));
    assertEquals(" ", cb.getSelectedItem());
  }

  @Test
  void theTimeCellEditorGraysOutTheIndexesUsedByOtherRoles() {
    final int colIndex = this.panel.getColumnIndex("Animation Frame");
    this.panel.setGenericDimensionIndex(0, 0);
    this.panel.setPickUpDimensionIndex(0, 1);
    final TableColumn col = columnOf(this.panel, colIndex);
    final JComboBox<?> cb =
        (JComboBox<?>)
            col.getCellEditor()
                .getTableCellEditorComponent(this.panel.getTable(), " ", false, 0, colIndex);
    final JList<String> list = newList();
    final SGMDArrayDataColumnSelectionPanel.DimensionListCellRenderer renderer =
        (SGMDArrayDataColumnSelectionPanel.DimensionListCellRenderer) cb.getRenderer();
    assertEquals(Color.LIGHT_GRAY, labelOf(renderer, list, "0").getForeground());
    assertEquals(Color.LIGHT_GRAY, labelOf(renderer, list, "1").getForeground());
    // the unselected entry is never grayed out
    assertNotSame(Color.LIGHT_GRAY, labelOf(renderer, list, " ").getForeground());
  }

  // -- the generic dimension cell editor ---------------------------------------------

  @Test
  void theGenericDimensionCellEditorListsTheDimensionIndexes() {
    final int colIndex = this.panel.getColumnIndex("Dimension");
    final TableColumn col = columnOf(this.panel, colIndex);
    final JComboBox<?> cb =
        (JComboBox<?>)
            col.getCellEditor()
                .getTableCellEditorComponent(this.panel.getTable(), 0, false, 0, colIndex);
    assertEquals(2, cb.getItemCount());
    assertEquals("0", cb.getItemAt(0));
    assertEquals("1", cb.getItemAt(1));
  }

  @Test
  void theGenericDimensionCellEditorGraysOutTheTimeAndPickUpIndexes() {
    final int colIndex = this.panel.getColumnIndex("Dimension");
    this.panel.setTimeDimensionIndex(0, 0);
    this.panel.setPickUpDimensionIndex(0, 1);
    final TableColumn col = columnOf(this.panel, colIndex);
    final JComboBox<?> cb =
        (JComboBox<?>)
            col.getCellEditor()
                .getTableCellEditorComponent(this.panel.getTable(), 0, false, 0, colIndex);
    final JList<String> list = newList();
    final SGMDArrayDataColumnSelectionPanel.DimensionListCellRenderer renderer =
        (SGMDArrayDataColumnSelectionPanel.DimensionListCellRenderer) cb.getRenderer();
    assertEquals(Color.LIGHT_GRAY, labelOf(renderer, list, "0").getForeground());
    assertEquals(Color.LIGHT_GRAY, labelOf(renderer, list, "1").getForeground());
  }

  // -- the pick up cell editor ----------------------------------------------------------

  @Test
  void thePickUpCellEditorListsTheUnselectedValueAndTheDimensionIndexes() {
    final int colIndex = this.panel.getColumnIndex("PickUp");
    final TableColumn col = columnOf(this.panel, colIndex);
    final JComboBox<?> cb =
        (JComboBox<?>)
            col.getCellEditor()
                .getTableCellEditorComponent(this.panel.getTable(), " ", false, 0, colIndex);
    assertEquals(3, cb.getItemCount());
    assertEquals(" ", cb.getItemAt(0));
    assertEquals("0", cb.getItemAt(1));
    assertEquals("1", cb.getItemAt(2));
    assertEquals(" ", cb.getSelectedItem());
  }

  @Test
  void thePickUpCellEditorGraysOutTheTimeAndGenericIndexes() {
    final int colIndex = this.panel.getColumnIndex("PickUp");
    this.panel.setTimeDimensionIndex(0, 0);
    this.panel.setGenericDimensionIndex(0, 1);
    final TableColumn col = columnOf(this.panel, colIndex);
    final JComboBox<?> cb =
        (JComboBox<?>)
            col.getCellEditor()
                .getTableCellEditorComponent(this.panel.getTable(), " ", false, 0, colIndex);
    final JList<String> list = newList();
    final SGMDArrayDataColumnSelectionPanel.DimensionListCellRenderer renderer =
        (SGMDArrayDataColumnSelectionPanel.DimensionListCellRenderer) cb.getRenderer();
    assertEquals(Color.LIGHT_GRAY, labelOf(renderer, list, "0").getForeground());
    assertEquals(Color.LIGHT_GRAY, labelOf(renderer, list, "1").getForeground());
  }

  // -- the dimension list renderer -------------------------------------------------------

  @Test
  void theDimensionListCellRendererGraysOutDeprecatedValues() {
    final int colIndex = this.panel.getColumnIndex("Dimension");
    this.panel.setTimeDimensionIndex(0, 0);
    final TableColumn col = columnOf(this.panel, colIndex);
    final JComboBox<?> cb =
        (JComboBox<?>)
            col.getCellEditor()
                .getTableCellEditorComponent(this.panel.getTable(), 0, false, 0, colIndex);
    final JList<String> list = newList();
    final SGMDArrayDataColumnSelectionPanel.DimensionListCellRenderer renderer =
        (SGMDArrayDataColumnSelectionPanel.DimensionListCellRenderer) cb.getRenderer();
    // a deprecated value is grayed out whether selected or not
    assertEquals(Color.LIGHT_GRAY, labelOf(renderer, list, "0").getForeground());
    assertEquals(Color.LIGHT_GRAY, labelOfSelected(renderer, list, "0").getForeground());
    // a non-deprecated value keeps the colors of the list
    assertEquals(list.getForeground(), labelOf(renderer, list, "1").getForeground());
    assertEquals(list.getBackground(), labelOf(renderer, list, "1").getBackground());
  }

  @Test
  void theDimensionListCellRendererUsesTheSelectionColorsForSelectedValues() {
    final int colIndex = this.panel.getColumnIndex("Dimension");
    final TableColumn col = columnOf(this.panel, colIndex);
    final JComboBox<?> cb =
        (JComboBox<?>)
            col.getCellEditor()
                .getTableCellEditorComponent(this.panel.getTable(), 0, false, 0, colIndex);
    final JList<String> list = newList();
    final SGMDArrayDataColumnSelectionPanel.DimensionListCellRenderer renderer =
        (SGMDArrayDataColumnSelectionPanel.DimensionListCellRenderer) cb.getRenderer();
    assertEquals(
        list.getSelectionForeground(), labelOfSelected(renderer, list, "1").getForeground());
    assertEquals(
        list.getSelectionBackground(), labelOfSelected(renderer, list, "1").getBackground());
  }

  // -- the column type cell editor ---------------------------------------------------------

  @Test
  void theColumnTypeCellEditorListsTheColumnTypes() {
    final int colIndex = this.panel.getColumnIndex("Column Type");
    final TableColumn col = columnOf(this.panel, colIndex);
    final JComboBox<?> cb =
        (JComboBox<?>)
            col.getCellEditor()
                .getTableCellEditorComponent(this.panel.getTable(), "", false, 0, colIndex);
    // no column has a column type yet, so only the base types are offered
    assertEquals(3, cb.getItemCount());
    assertEquals("", cb.getItemAt(0));
    assertEquals(SGDataColumnTypeConstants.X_VALUE, cb.getItemAt(1));
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, cb.getItemAt(2));
  }

  @Test
  void theColumnTypeCellEditorOffersErrorBarOptionsForTheOtherXAndYColumns() throws Exception {
    final SGMDArrayDataColumnSelectionPanel panel = new SGMDArrayDataColumnSelectionPanel();
    final Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    assertTrue(
        panel.setData(
            SGDataTypeConstants.SXY_MATLAB_DATA,
            new SGDataColumnInfoSet(buildColumns(4)),
            infoMap,
            false));
    // the options refer to the other number columns, so one must be assigned first
    panel.setColumnType(0, SGDataColumnTypeConstants.X_VALUE);
    final int colIndex = panel.getColumnIndex("Column Type");
    final TableColumn col = columnOf(panel, colIndex);
    final JComboBox<?> cb =
        (JComboBox<?>)
            col.getCellEditor()
                .getTableCellEditorComponent(panel.getTable(), "", false, 1, colIndex);
    // the options refer to the other column (row 0, named "a")
    assertEquals(SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for a", cb.getItemAt(3));
    assertEquals(SGDataColumnTypeConstants.UPPER_ERROR_VALUE + " for a", cb.getItemAt(4));
    assertEquals(SGDataColumnTypeConstants.LOWER_UPPER_ERROR_VALUE + " for a", cb.getItemAt(5));
    assertEquals(SGDataColumnTypeConstants.TICK_LABEL + " for a", cb.getItemAt(6));
  }

  // -- the data column cell renderer ---------------------------------------------------------

  @Test
  void theDataColumnCellRendererShowsTheCellValueInAComboBox() {
    final int colIndex = this.panel.getColumnIndex("Animation Frame");
    final TableColumn col = columnOf(this.panel, colIndex);
    final TableCellRenderer renderer = col.getCellRenderer();
    final Component comp =
        renderer.getTableCellRendererComponent(
            this.panel.getTable(), "0", false, false, 0, colIndex);
    final JComboBox<?> cb = (JComboBox<?>) comp;
    assertEquals(1, cb.getItemCount());
    assertEquals("0", cb.getItemAt(0));
  }

  @Test
  void theTableTooltipIsSuppressedForUnselectedCells() {
    final JTable table = this.panel.getTable();
    table.setSize(table.getPreferredSize());
    table.doLayout();
    // a cell with a value shows the value as the tooltip
    final MouseEvent normal =
        new MouseEvent(table, MouseEvent.MOUSE_MOVED, 0, 0, 5, 5, 0, false, MouseEvent.BUTTON1);
    assertEquals("1", table.getToolTipText(normal));
    // a cell with the unselected value shows no tooltip
    final int timeColIndex = this.panel.getColumnIndex("Animation Frame");
    assertEquals(" ", table.getValueAt(0, timeColIndex));
    int x = 5;
    for (int ii = 0; ii < timeColIndex; ii++) {
      x += table.getColumnModel().getColumn(ii).getWidth();
    }
    final MouseEvent unselected =
        new MouseEvent(table, MouseEvent.MOUSE_MOVED, 0, 0, x, 5, 0, false, MouseEvent.BUTTON1);
    assertNull(table.getToolTipText(unselected));
  }

  // -- the button column of the base panel ------------------------------------------------------

  @Test
  void theButtonColumnProvidesRenderAndEditorButtons() {
    final SGDataColumnSelectionPanel.ButtonColumn column =
        new SGDataColumnSelectionPanel.ButtonColumn();
    final Component rendered =
        column.getTableCellRendererComponent(this.panel.getTable(), "1", false, false, 0, 0);
    final Component edited =
        column.getTableCellEditorComponent(this.panel.getTable(), "1", false, 0, 0);
    assertTrue(rendered instanceof JButton);
    assertTrue(edited instanceof JButton);
    assertNotSame(rendered, edited);
    assertEquals(" ", column.getCellEditorValue());
  }

  // -- cleanup --------------------------------------------------------------------------

  @Test
  void theSetVisibleFalseClearsTheDimensionComboBoxes() {
    // the editors populate the shared combo boxes
    invokeEditor("Animation Frame", " ");
    invokeEditor("Dimension", 0);
    invokeEditor("PickUp", " ");
    assertEquals(3, this.panel.mTimeDimensionEditorComboBox.getItemCount());
    assertEquals(2, this.panel.mGenericDimensionEditorComboBox.getItemCount());
    assertEquals(3, this.panel.mPickUpDimensionEditorComboBox.getItemCount());
    // hiding the panel clears the dimension and column type combo boxes
    this.panel.setVisible(false);
    assertEquals(0, this.panel.mTimeDimensionEditorComboBox.getItemCount());
    assertEquals(0, this.panel.mGenericDimensionEditorComboBox.getItemCount());
    assertEquals(0, this.panel.mPickUpDimensionEditorComboBox.getItemCount());
    assertEquals(0, this.panel.mColumnTypeEditorComboBox.getItemCount());
    assertEquals(0, this.panel.mColumnTypeRendererComboBox.getItemCount());
  }

  // -- validation -------------------------------------------------------------------------------

  @Test
  void checkSelectedItemsRejectsUnassignedColumns() throws Exception {
    final SGMDArrayDataColumnSelectionPanel md = createSxyzPanel(new HashMap<String, Object>());
    md.setTimeDimensionIndex(0, 0);
    md.setDimensionIndex(0, SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    assertFalse(md.checkSelectedItems());
  }

  @Test
  void checkSelectedItemsRejectsDifferentTimeDimensionLengths() throws Exception {
    // the first column has a shorter dimension 0 than the second one
    final Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    final MLDouble shortVar = new MLDouble("s", new double[][] {{1.0}, {2.0}});
    final MLDouble longVar = new MLDouble("l", new double[][] {{1.0}, {2.0}, {3.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(shortVar, longVar));
    final MatFileReader reader = new MatFileReader(path.toFile());
    final SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    final SGMDArrayDataColumnInfo[] mixed =
        SGDataFileUtility.getMDArrayDataColumnInfo(
            matFile, matFile.getVariables(), new HashMap<String, Object>());
    final SGMDArrayDataColumnSelectionPanel panel = new SGMDArrayDataColumnSelectionPanel();
    assertTrue(
        panel.setData(
            SGDataTypeConstants.SXY_MATLAB_DATA,
            new SGDataColumnInfoSet(mixed),
            new HashMap<String, Object>(),
            false));
    panel.setColumnType(0, SGDataColumnTypeConstants.X_VALUE);
    panel.setColumnType(1, SGDataColumnTypeConstants.Y_VALUE);
    panel.setTimeDimensionIndex(0, 0);
    panel.setTimeDimensionIndex(1, 0);
    assertFalse(panel.checkSelectedItems());
  }

  // -- helpers -----------------------------------------------------------------------------------

  private static TableColumn columnOf(
      final SGMDArrayDataColumnSelectionPanel panel, final int colIndex) {
    return panel.getTable().getColumnModel().getColumn(colIndex);
  }

  private static JList<String> newList() {
    return new JList<String>(
        new String[] {" ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"});
  }

  private static JLabel labelOf(
      final ListCellRenderer<Object> renderer, final JList<String> list, final String value) {
    return (JLabel) renderer.getListCellRendererComponent(list, value, 0, false, false);
  }

  private static JLabel labelOfSelected(
      final ListCellRenderer<Object> renderer, final JList<String> list, final String value) {
    return (JLabel) renderer.getListCellRendererComponent(list, value, 0, true, false);
  }

  private void invokeEditor(final String columnName, final Object value) {
    final int colIndex = this.panel.getColumnIndex(columnName);
    final TableColumn col = columnOf(this.panel, colIndex);
    col.getCellEditor()
        .getTableCellEditorComponent(this.panel.getTable(), value, false, 0, colIndex);
  }
}
