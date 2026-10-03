package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGRadioButton;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import org.junit.jupiter.api.Test;

/**
 * Characterization tests for the MDArray data setup panel.
 *
 * <p>The tests drive {@code setData} with virtual MDArray files and assert the resulting state of
 * the panel copies of the data columns.
 *
 * <p>The panel and its parent register most of their listeners through tasks posted to the event
 * dispatch thread, so the tests alternate between EDT blocks (construction, {@code setData}, and
 * component interaction) and {@code pumpEdt} calls executed from the test thread, which let the
 * posted tasks run.
 */
class SGMDArrayDataSetupPanelCharacterizationTest {

  // Runs a runnable on the event dispatch thread.
  private void runOnEdt(final Runnable runnable) {
    try {
      SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // Pumps the event dispatch thread so that the tasks posted by setData run.
  // Must be called from a non-EDT thread.
  private void pumpEdt() {
    this.runOnEdt(() -> {});
  }

  // Sets the full text of a document, replacing the current contents.
  private static void setDocumentText(final Document doc, final String text) {
    try {
      doc.remove(0, doc.getLength());
      doc.insertString(0, text, null);
    } catch (BadLocationException e) {
      throw new RuntimeException(e);
    }
  }

  private SGVirtualMDArrayVariable.D1 var1(final int len, final String name) {
    return new SGVirtualMDArrayVariable.D1(new double[len], name);
  }

  private SGVirtualMDArrayVariable.D2 var2(final int len0, final int len1, final String name) {
    double[][] values = new double[len0][len1];
    for (int ii = 0; ii < len0; ii++) {
      for (int jj = 0; jj < len1; jj++) {
        values[ii][jj] = ii * len1 + jj;
      }
    }
    return new SGVirtualMDArrayVariable.D2(values, name);
  }

  private SGMDArrayDataColumnInfo col(final SGMDArrayVariable var, final String columnType) {
    return SGDataFileUtility.createDataColumnInfo(var, columnType);
  }

  private SGDataColumnInfoSet set(final SGMDArrayDataColumnInfo... cols) {
    return new SGDataColumnInfoSet(cols);
  }

  private SGVirtualMDArrayFile file(final SGVirtualMDArrayVariable... vars) {
    return new SGVirtualMDArrayFile(vars);
  }

  // Builds a document event for a given document and type.
  private static DocumentEvent documentEvent(
      final Document doc, final DocumentEvent.EventType type) {
    return new DocumentEvent() {
      @Override
      public int getOffset() {
        return 0;
      }

      @Override
      public int getLength() {
        return 0;
      }

      @Override
      public Document getDocument() {
        return doc;
      }

      @Override
      public DocumentEvent.EventType getType() {
        return type;
      }

      @Override
      public DocumentEvent.ElementChange getChange(Element e) {
        return null;
      }
    };
  }

  private Map<String, Object> infoMap() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    return map;
  }

  // SXY fixture: two one-dimensional variables with the generic dimension selected.
  private SGVirtualMDArrayVariable.D1[] sxyVars() {
    SGVirtualMDArrayVariable.D1 x = this.var1(5, "x");
    SGVirtualMDArrayVariable.D1 y = this.var1(5, "y");
    x.setGenericDimensionIndex(0);
    y.setGenericDimensionIndex(0);
    return new SGVirtualMDArrayVariable.D1[] {x, y};
  }

  private SGDataColumnInfoSet sxyCols(final SGVirtualMDArrayVariable.D1[] vars) {
    return this.set(
        this.col(vars[0], SGDataColumnTypeConstants.X_VALUE),
        this.col(vars[1], SGDataColumnTypeConstants.Y_VALUE));
  }

  // SXYZ grid fixture: x (5), y (3), z (5 x 3) with the grid plot flag selected.
  private SGVirtualMDArrayVariable[] sxyzGridVars() {
    SGVirtualMDArrayVariable.D1 x = this.var1(5, "x");
    SGVirtualMDArrayVariable.D1 y = this.var1(3, "y");
    SGVirtualMDArrayVariable.D2 z = this.var2(5, 3, "z");
    x.setGenericDimensionIndex(0);
    y.setGenericDimensionIndex(0);
    return new SGVirtualMDArrayVariable[] {x, y, z};
  }

  private SGDataColumnInfoSet sxyzGridCols(final SGVirtualMDArrayVariable[] vars) {
    return this.set(
        this.col(vars[0], SGDataColumnTypeConstants.X_VALUE),
        this.col(vars[1], SGDataColumnTypeConstants.Y_VALUE),
        this.col(vars[2], SGDataColumnTypeConstants.Z_VALUE));
  }

  private Map<String, Object> sxyzGridInfo() {
    Map<String, Object> info = this.infoMap();
    info.put(SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, Boolean.TRUE);
    return info;
  }

  // VXY grid fixture: x (5), y (3), f and s (5 x 3) in Cartesian coordinates.
  // The f and s dimension indices are intentionally left unset so that the panel
  // derives them from the dimension lengths.
  private SGVirtualMDArrayVariable[] vxyGridVars() {
    SGVirtualMDArrayVariable.D1 x = this.var1(5, "x");
    SGVirtualMDArrayVariable.D1 y = this.var1(3, "y");
    SGVirtualMDArrayVariable.D2 f = this.var2(5, 3, "f");
    SGVirtualMDArrayVariable.D2 s = this.var2(5, 3, "s");
    x.setGenericDimensionIndex(0);
    y.setGenericDimensionIndex(0);
    return new SGVirtualMDArrayVariable[] {x, y, f, s};
  }

  private SGDataColumnInfoSet vxyGridCols(final SGVirtualMDArrayVariable[] vars) {
    return this.set(
        this.col(vars[0], SGDataColumnTypeConstants.X_COORDINATE),
        this.col(vars[1], SGDataColumnTypeConstants.Y_COORDINATE),
        this.col(vars[2], SGDataColumnTypeConstants.X_COMPONENT),
        this.col(vars[3], SGDataColumnTypeConstants.Y_COMPONENT));
  }

  private Map<String, Object> vxyGridInfo() {
    Map<String, Object> info = this.infoMap();
    info.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    info.put(SGDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, Boolean.TRUE);
    return info;
  }

  // Counts the visible MDArray dimension panels of a given container.
  private int countVisibleDimensionPanels(final Container c) {
    int cnt = 0;
    for (java.awt.Component comp : c.getComponents()) {
      if (comp instanceof SGMDArrayDimensionPanel && comp.isVisible()) {
        cnt++;
      }
    }
    return cnt;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> infoMap(final SGMDArrayDataSetupPanel panel) {
    return (Map<String, Object>) field(panel, "mInfoMap");
  }

  private static Object field(final Object target, final String name) {
    Class<?> c = target.getClass();
    while (c != null) {
      try {
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(target);
      } catch (NoSuchFieldException e) {
        c = c.getSuperclass();
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    throw new IllegalArgumentException("field not found: " + name);
  }

  // --- setData ------------------------------------------------------------

  @Test
  void setDataThrowsOnNullArguments() {
    final SGVirtualMDArrayVariable.D1 x = this.var1(5, "x");
    final SGDataColumnInfoSet cols = this.set(this.col(x, SGDataColumnTypeConstants.X_VALUE));
    final Map<String, Object> info = this.infoMap();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          assertThrows(
              IllegalArgumentException.class,
              () ->
                  panel.setData(
                      null, SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA, cols, info, false));
          assertThrows(
              IllegalArgumentException.class,
              () -> panel.setData(this.file(x), null, cols, info, false));
          assertThrows(
              IllegalArgumentException.class,
              () ->
                  panel.setData(
                      this.file(x),
                      SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
                      null,
                      info,
                      false));
          assertThrows(
              IllegalArgumentException.class,
              () ->
                  panel.setData(
                      this.file(x),
                      SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
                      cols,
                      null,
                      false));
          assertThrows(
              IllegalArgumentException.class,
              () -> panel.setData(this.file(x), "UnsupportedType", cols, info, false));
        });
  }

  @Test
  void vxySetDataFailsWithoutPolarSelectedKey() {
    final SGVirtualMDArrayVariable[] vars = this.vxyGridVars();
    final SGDataColumnInfoSet cols = this.vxyGridCols(vars);
    final Map<String, Object> info = this.infoMap();
    info.put(SGDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, Boolean.TRUE);

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars), SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA, cols, info, false);
          assertFalse(ok);
        });
  }

  // --- SXY ----------------------------------------------------------------

  @Test
  void sxySingleColumnsProduceValidPanelState() {
    final SGVirtualMDArrayVariable.D1[] vars = this.sxyVars();
    final SGDataColumnInfoSet cols = this.sxyCols(vars);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars),
                  SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.infoMap(),
                  false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];

          // the row of the first variable is selected
          assertEquals(0, panel.getTable().getSelectedRow());

          // a single x and y pair is not a pickup data set
          assertFalse(panel.isVariableDataType());
          assertNull(panel.getSXYPickUpIndices());
          assertNotNull(panel.getSXYDataPickUpDatasetName());
          assertTrue(panel.getSXYDataPickUpDatasetName().isEmpty());
          Map<String, Integer> dimMap = panel.getSXYDataPickUpDimensionIndexMap();
          assertEquals(2, dimMap.size());
          assertEquals(Integer.valueOf(-1), dimMap.get("x"));
          assertEquals(Integer.valueOf(-1), dimMap.get("y"));
          Map<String, Integer> timeMap = panel.getTimeDimensionIndexMap();
          assertEquals(Integer.valueOf(-1), timeMap.get("x"));
          assertEquals(Integer.valueOf(-1), timeMap.get("y"));

          // the stride panels are initialized with the full ranges
          assertFalse(panel.isStrideAvailable());
          Map<String, SGIntegerSeriesSet> strideMap = panel.getStrideMap();
          assertArrayEquals(
              new int[] {0, 1, 2, 3, 4},
              strideMap.get(SGDataInformationKeyConstants.KEY_SXY_STRIDE).getNumbers());
          assertArrayEquals(
              new int[] {0, 1, 2, 3, 4},
              strideMap.get(SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE).getNumbers());
          Map<String, Integer> fullLengthMap = panel.getFullLengthMap();
          assertEquals(
              Integer.valueOf(5), fullLengthMap.get(SGDataInformationKeyConstants.KEY_SXY_STRIDE));
          assertEquals(
              Integer.valueOf(5),
              fullLengthMap.get(SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE));

          assertTrue(panel.checkSelectedItems());
          assertArrayEquals(new int[] {0}, panel.getOrigins("x"));
        });
  }

  @Test
  void sxyMultipleYColumnsAreVariableDataType() {
    final SGVirtualMDArrayVariable.D1 x = this.var1(5, "x");
    final SGVirtualMDArrayVariable.D1 y1 = this.var1(5, "y1");
    final SGVirtualMDArrayVariable.D1 y2 = this.var1(5, "y2");
    final SGVirtualMDArrayVariable.D1 y3 = this.var1(5, "y3");
    x.setGenericDimensionIndex(0);
    y1.setGenericDimensionIndex(0);
    y2.setGenericDimensionIndex(0);
    y3.setGenericDimensionIndex(0);
    final SGDataColumnInfoSet cols =
        this.set(
            this.col(x, SGDataColumnTypeConstants.X_VALUE),
            this.col(y1, SGDataColumnTypeConstants.Y_VALUE),
            this.col(y2, SGDataColumnTypeConstants.Y_VALUE),
            this.col(y3, SGDataColumnTypeConstants.Y_VALUE));

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(x, y1, y2, y3),
                  SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.infoMap(),
                  false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          assertTrue(panel.isVariableDataType());
          assertNotNull(panel.getStrideMap().get(SGDataInformationKeyConstants.KEY_SXY_STRIDE));
          assertTrue(panel.checkSelectedItems());
        });
  }

  @Test
  void sxyTwoDimensionalPickUpColumnsDerivePickUpIndices() {
    final SGVirtualMDArrayVariable.D2 x = this.var2(5, 3, "x");
    final SGVirtualMDArrayVariable.D2 y = this.var2(5, 3, "y");
    x.setGenericDimensionIndex(0);
    x.setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 1);
    y.setGenericDimensionIndex(0);
    y.setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 1);
    final SGDataColumnInfoSet cols =
        this.set(
            this.col(x, SGDataColumnTypeConstants.X_VALUE),
            this.col(y, SGDataColumnTypeConstants.Y_VALUE));

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(x, y),
                  SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.infoMap(),
                  false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          assertFalse(panel.isVariableDataType());
          assertArrayEquals(new int[] {0, 1, 2}, panel.getSXYPickUpIndices().getNumbers());
          assertEquals(Arrays.asList("x", "y"), panel.getSXYDataPickUpDatasetName());
          Map<String, Integer> dimMap = panel.getSXYDataPickUpDimensionIndexMap();
          assertEquals(Integer.valueOf(1), dimMap.get("x"));
          assertEquals(Integer.valueOf(1), dimMap.get("y"));
          assertTrue(panel.checkSelectedItems());
        });
  }

  // --- SXYZ -----------------------------------------------------------------

  @Test
  void sxyzGridPlotAutoSelectsDistinctZDimensions() {
    final SGVirtualMDArrayVariable[] vars = this.sxyzGridVars();
    final SGDataColumnInfoSet cols = this.sxyzGridCols(vars);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars),
                  SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.sxyzGridInfo(),
                  false);
          assertTrue(ok);
          assertFalse(panel.isSXYZIndexAvailable());
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];

          // the z x and y dimension indices are derived from the dimension
          // lengths, so the state is valid right after the pump
          JComboBox<?> zxCombo =
              (JComboBox<?>) field(panel, "mSXYZDataZVariableDimensionXComboBox");
          JComboBox<?> zyCombo =
              (JComboBox<?>) field(panel, "mSXYZDataZVariableDimensionYComboBox");
          assertEquals("0", zxCombo.getSelectedItem());
          assertEquals("1", zyCombo.getSelectedItem());
          assertTrue(panel.checkSelectedItems());

          // duplicating the z y selection invalidates the state
          zyCombo.setSelectedItem("0");
          assertFalse(panel.checkSelectedItems());

          // restoring the z y selection makes the state valid again
          zyCombo.setSelectedItem("1");
          assertTrue(panel.checkSelectedItems());

          Map<String, Integer> fullLengthMap = panel.getFullLengthMap();
          assertEquals(
              Integer.valueOf(5),
              fullLengthMap.get(SGDataInformationKeyConstants.KEY_SXYZ_STRIDE_X));
          assertEquals(
              Integer.valueOf(3),
              fullLengthMap.get(SGDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y));
          assertEquals(
              Integer.valueOf(-1),
              fullLengthMap.get(SGDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE));
        });
  }

  @Test
  void sxyzScatterPlotDerivesIndexStride() {
    final SGVirtualMDArrayVariable.D1 x = this.var1(4, "x");
    final SGVirtualMDArrayVariable.D1 y = this.var1(4, "y");
    final SGVirtualMDArrayVariable.D1 z = this.var1(4, "z");
    x.setGenericDimensionIndex(0);
    y.setGenericDimensionIndex(0);
    z.setGenericDimensionIndex(0);
    final SGDataColumnInfoSet cols =
        this.set(
            this.col(x, SGDataColumnTypeConstants.X_VALUE),
            this.col(y, SGDataColumnTypeConstants.Y_VALUE),
            this.col(z, SGDataColumnTypeConstants.Z_VALUE));
    final Map<String, Object> info = this.infoMap();
    info.put(SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, Boolean.FALSE);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(x, y, z),
                  SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA,
                  cols,
                  info,
                  false);
          assertTrue(ok);
          assertTrue(panel.isSXYZIndexAvailable());
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          assertTrue(panel.checkSelectedItems());
          Map<String, Integer> fullLengthMap = panel.getFullLengthMap();
          assertEquals(
              Integer.valueOf(-1),
              fullLengthMap.get(SGDataInformationKeyConstants.KEY_SXYZ_STRIDE_X));
          assertEquals(
              Integer.valueOf(-1),
              fullLengthMap.get(SGDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y));
          assertEquals(
              Integer.valueOf(4),
              fullLengthMap.get(SGDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE));
        });
  }

  @Test
  void sxyzRadioButtonsSwitchBetweenGridAndScatter() {
    final SGVirtualMDArrayVariable[] vars = this.sxyzGridVars();
    final SGDataColumnInfoSet cols = this.sxyzGridCols(vars);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars),
                  SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.sxyzGridInfo(),
                  false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          assertFalse(panel.isSXYZIndexAvailable());

          // the radios share a button group, so selecting scatter deselects
          // grid, and the panel re-derives the scatter state
          SGRadioButton scatter = (SGRadioButton) field(panel, "mSXYZScatterPlotRadioButton");
          scatter.doClick();

          assertTrue(panel.isSXYZIndexAvailable());
          // the grid fixture has mismatched x (5) and y (3) lengths for a
          // scatter plot
          assertFalse(panel.checkSelectedItems());
        });
  }

  // --- VXY ------------------------------------------------------------------

  @Test
  void vxyGridPlotDerivesDimensionIndices() {
    final SGVirtualMDArrayVariable[] vars = this.vxyGridVars();
    final SGDataColumnInfoSet cols = this.vxyGridCols(vars);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars),
                  SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.vxyGridInfo(),
                  false);
          assertTrue(ok);
          assertFalse(panel.isVXYIndexAvailable());
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          assertTrue(panel.checkSelectedItems());
          Map<String, Integer> fullLengthMap = panel.getFullLengthMap();
          assertEquals(
              Integer.valueOf(5),
              fullLengthMap.get(SGDataInformationKeyConstants.KEY_VXY_STRIDE_X));
          assertEquals(
              Integer.valueOf(3),
              fullLengthMap.get(SGDataInformationKeyConstants.KEY_VXY_STRIDE_Y));
          assertEquals(
              Integer.valueOf(-1),
              fullLengthMap.get(SGDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE));
        });
  }

  @Test
  void vxyScatterPlotDerivesIndexStride() {
    final SGVirtualMDArrayVariable.D1 x = this.var1(4, "x");
    final SGVirtualMDArrayVariable.D1 y = this.var1(4, "y");
    final SGVirtualMDArrayVariable.D1 f = this.var1(4, "f");
    final SGVirtualMDArrayVariable.D1 s = this.var1(4, "s");
    x.setGenericDimensionIndex(0);
    y.setGenericDimensionIndex(0);
    f.setGenericDimensionIndex(0);
    s.setGenericDimensionIndex(0);
    final SGDataColumnInfoSet cols =
        this.set(
            this.col(x, SGDataColumnTypeConstants.X_COORDINATE),
            this.col(y, SGDataColumnTypeConstants.Y_COORDINATE),
            this.col(f, SGDataColumnTypeConstants.X_COMPONENT),
            this.col(s, SGDataColumnTypeConstants.Y_COMPONENT));
    final Map<String, Object> info = this.infoMap();
    info.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    info.put(SGDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, Boolean.FALSE);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(x, y, f, s),
                  SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  info,
                  false);
          assertTrue(ok);
          assertTrue(panel.isVXYIndexAvailable());
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          assertTrue(panel.checkSelectedItems());
          Map<String, Integer> fullLengthMap = panel.getFullLengthMap();
          assertEquals(
              Integer.valueOf(-1),
              fullLengthMap.get(SGDataInformationKeyConstants.KEY_VXY_STRIDE_X));
          assertEquals(
              Integer.valueOf(-1),
              fullLengthMap.get(SGDataInformationKeyConstants.KEY_VXY_STRIDE_Y));
          assertEquals(
              Integer.valueOf(4),
              fullLengthMap.get(SGDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE));
        });
  }

  // --- event handlers ---------------------------------------------------------

  @Test
  void itemStateChangedIgnoresForeignComboBox() {
    final SGVirtualMDArrayVariable[] vars = this.vxyGridVars();
    final SGDataColumnInfoSet cols = this.vxyGridCols(vars);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars),
                  SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.vxyGridInfo(),
                  false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          assertTrue(panel.checkSelectedItems());

          // a SELECTED event from an unrelated combo box must be a no-op
          JComboBox<Object> foreign = new JComboBox<Object>();
          foreign.addItem("0");
          panel.itemStateChanged(
              new ItemEvent(foreign, ItemEvent.ITEM_STATE_CHANGED, "0", ItemEvent.SELECTED));
          assertTrue(panel.checkSelectedItems());
        });
  }

  @Test
  void addItemListenerRelaysRealComboEvents() {
    final SGVirtualMDArrayVariable[] vars = this.vxyGridVars();
    final SGDataColumnInfoSet cols = this.vxyGridCols(vars);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars),
                  SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.vxyGridInfo(),
                  false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];

          final int[] itemEvents = {0};
          ItemListener itemListener =
              new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                  itemEvents[0]++;
                }
              };
          panel.addItemListener(itemListener);

          // a change on the first component x-dimension combo is relayed to
          // the registered listener
          JComboBox<?> fxCombo =
              (JComboBox<?>) field(panel, "mVXYDataFirstVariableDimensionXComboBox");
          fxCombo.setSelectedItem("1");
          assertTrue(itemEvents[0] > 0);
        });
  }

  @Test
  void tableSelectionTogglesOriginPanelVisibility() {
    final SGVirtualMDArrayVariable.D1[] vars = this.sxyVars();
    final SGDataColumnInfoSet cols = this.sxyCols(vars);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars),
                  SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.infoMap(),
                  false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          javax.swing.JPanel originPanel = (javax.swing.JPanel) field(panel, "mOriginPanel");
          assertEquals(1, this.countVisibleDimensionPanels(originPanel));

          // an adjusting event is ignored
          panel.valueChanged(
              new ListSelectionEvent(panel.getTable().getSelectionModel(), 0, 0, true));
          assertEquals(1, this.countVisibleDimensionPanels(originPanel));

          // clearing the selection hides all dimension panels
          panel.getTable().getSelectionModel().clearSelection();
          assertEquals(0, this.countVisibleDimensionPanels(originPanel));

          // re-selecting the first row shows the dimension panels again
          panel.getTable().setRowSelectionInterval(0, 0);
          assertEquals(1, this.countVisibleDimensionPanels(originPanel));
        });
  }

  @Test
  void sliderChangeUpdatesOriginIndex() {
    final SGVirtualMDArrayVariable.D1[] vars = this.sxyVars();
    final SGDataColumnInfoSet cols = this.sxyCols(vars);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars),
                  SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.infoMap(),
                  false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          javax.swing.JPanel originPanel = (javax.swing.JPanel) field(panel, "mOriginPanel");
          SGDimensionPanel[] dimPanels = panel.getDimensionPanels(originPanel);
          assertEquals(1, dimPanels.length);
          SGSliderPanel slider = dimPanels[0].getSliderPanel();

          // moving the slider updates the origin of the selected row
          slider.setRange(0, 4, 3);
          assertArrayEquals(new int[] {3}, panel.getOrigins("x"));
        });
  }

  // --- document handlers ---------------------------------------------------------

  @Test
  void documentHandlersIgnoreUnrelatedDocuments() {
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          Document doc = new PlainDocument();
          panel.insertUpdate(
              SGMDArrayDataSetupPanelCharacterizationTest.documentEvent(
                  doc, DocumentEvent.EventType.INSERT));
          panel.removeUpdate(
              SGMDArrayDataSetupPanelCharacterizationTest.documentEvent(
                  doc, DocumentEvent.EventType.REMOVE));
          panel.changedUpdate(
              SGMDArrayDataSetupPanelCharacterizationTest.documentEvent(
                  doc, DocumentEvent.EventType.CHANGE));
        });
  }

  @Test
  void lineAndBarStrideDocumentSyncsTickLabelStride() {
    final SGVirtualMDArrayVariable.D1[] vars = this.sxyVars();
    final SGDataColumnInfoSet cols = this.sxyCols(vars);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars),
                  SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.infoMap(),
                  false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];

          // the missing tick label stride key makes the sync checkbox selected
          // after the pump
          SGIndexPanel lineAndBar = (SGIndexPanel) field(panel, "mSXYDataLineAndBarStridePanel");
          Document doc =
              ((javax.swing.JTextField) field(lineAndBar, "mIndexTextField")).getDocument();
          SGMDArrayDataSetupPanelCharacterizationTest.setDocumentText(doc, "0:1");

          Map<String, SGIntegerSeriesSet> strideMap = panel.getStrideMap();
          assertArrayEquals(
              new int[] {0, 1},
              strideMap.get(SGDataInformationKeyConstants.KEY_SXY_STRIDE).getNumbers());
          assertArrayEquals(
              new int[] {0, 1},
              strideMap.get(SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE).getNumbers());
        });
  }

  @Test
  void pickUpStrideDocumentUpdatesInfoMap() {
    final SGVirtualMDArrayVariable.D2 x = this.var2(5, 3, "x");
    final SGVirtualMDArrayVariable.D2 y = this.var2(5, 3, "y");
    x.setGenericDimensionIndex(0);
    x.setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 1);
    y.setGenericDimensionIndex(0);
    y.setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 1);
    final SGDataColumnInfoSet cols =
        this.set(
            this.col(x, SGDataColumnTypeConstants.X_VALUE),
            this.col(y, SGDataColumnTypeConstants.Y_VALUE));

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(x, y),
                  SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.infoMap(),
                  false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];

          // the default indices are stored in the info map after the pump
          final Map<String, Object> infoMap = infoMap(panel);
          SGIntegerSeriesSet defaults =
              (SGIntegerSeriesSet)
                  infoMap.get(SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
          assertNotNull(defaults);
          assertArrayEquals(new int[] {0, 1, 2}, defaults.getNumbers());

          SGIndexPanel pickUpPanel =
              (SGIndexPanel) field(panel, "mSXYDataPickUpDimensionIndexPanel");
          Document doc =
              ((javax.swing.JTextField) field(pickUpPanel, "mIndexTextField")).getDocument();
          SGMDArrayDataSetupPanelCharacterizationTest.setDocumentText(doc, "0:1");

          SGIntegerSeriesSet indices =
              (SGIntegerSeriesSet)
                  infoMap.get(SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
          assertNotNull(indices);
          assertArrayEquals(new int[] {0, 1}, indices.getNumbers());
        });
  }

  // --- action handlers -------------------------------------------------------------

  @Test
  void clearRestoreAndComplementButtonsReinitializePanel() {
    final SGVirtualMDArrayVariable.D1[] vars = this.sxyVars();
    final SGDataColumnInfoSet cols = this.sxyCols(vars);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars),
                  SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.infoMap(),
                  false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          assertTrue(panel.checkSelectedItems());
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];

          // clear resets all column types and the panel re-derives an empty
          // state; the table keeps one row per variable
          JButton clear = panel.getClearButton();
          clear.doClick();
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          assertEquals(2, panel.getDataColumnTypes().length);
          assertEquals("", panel.getDataColumnTypes()[0].getColumnType());
          assertFalse(panel.checkSelectedItems());
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          // restore brings the initial column types back
          panel.getRestoreButton().doClick();
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          assertEquals(
              SGDataColumnTypeConstants.X_VALUE, panel.getDataColumnTypes()[0].getColumnType());
          assertEquals(
              SGDataColumnTypeConstants.Y_VALUE, panel.getDataColumnTypes()[1].getColumnType());
          assertTrue(panel.checkSelectedItems());
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          // the complement button is hidden, so a click is a no-op
          panel.getComplementButton().doClick();
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          assertEquals(
              SGDataColumnTypeConstants.X_VALUE, panel.getDataColumnTypes()[0].getColumnType());
          assertTrue(panel.checkSelectedItems());
        });
  }

  @Test
  void tickLabelSyncCheckboxControlsSyncing() {
    final SGVirtualMDArrayVariable.D1[] vars = this.sxyVars();
    final SGDataColumnInfoSet cols = this.sxyCols(vars);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars),
                  SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.infoMap(),
                  false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];

          SGIndexPanel lineAndBar = (SGIndexPanel) field(panel, "mSXYDataLineAndBarStridePanel");
          javax.swing.JTextField textField =
              (javax.swing.JTextField) field(lineAndBar, "mIndexTextField");

          // deselect the sync checkbox and edit the line-and-bar stride:
          // no sync
          JCheckBox syncBox = (JCheckBox) field(panel, "mSXYDataTickLabelStrideSyncCheckBox");
          syncBox.setSelected(false);
          assertFalse(syncBox.isSelected());
          SGMDArrayDataSetupPanelCharacterizationTest.setDocumentText(
              textField.getDocument(), "0:1");
          Map<String, SGIntegerSeriesSet> strideMap = panel.getStrideMap();
          assertArrayEquals(
              new int[] {0, 1},
              strideMap.get(SGDataInformationKeyConstants.KEY_SXY_STRIDE).getNumbers());
          assertArrayEquals(
              new int[] {0, 1, 2, 3, 4},
              strideMap.get(SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE).getNumbers());

          // reselect it and edit the line-and-bar stride again: the tick
          // label stride is synced from the line-and-bar stride
          syncBox.setSelected(true);
          assertTrue(syncBox.isSelected());
          SGMDArrayDataSetupPanelCharacterizationTest.setDocumentText(
              textField.getDocument(), "0:2");
          strideMap = panel.getStrideMap();
          assertArrayEquals(
              new int[] {0, 1, 2},
              strideMap.get(SGDataInformationKeyConstants.KEY_SXY_STRIDE).getNumbers());
          assertArrayEquals(
              new int[] {0, 1, 2},
              strideMap.get(SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE).getNumbers());
        });
  }

  @Test
  void strideAvailableCheckboxReinitializesStride() {
    final SGVirtualMDArrayVariable.D1[] vars = this.sxyVars();
    final SGDataColumnInfoSet cols = this.sxyCols(vars);
    final Map<String, Object> info = this.infoMap();
    info.put(SGDataInformationKeyConstants.KEY_STRIDE_AVAILABLE, Boolean.TRUE);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars), SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA, cols, info, false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];
          assertTrue(panel.isStrideAvailable());

          JCheckBox checkBox = (JCheckBox) field(panel, "mStrideAvailableCheckBox");
          checkBox.doClick();
          assertFalse(panel.isStrideAvailable());
          assertNotNull(panel.getStrideMap().get(SGDataInformationKeyConstants.KEY_SXY_STRIDE));

          checkBox.doClick();
          assertTrue(panel.isStrideAvailable());
          assertNotNull(panel.getStrideMap().get(SGDataInformationKeyConstants.KEY_SXY_STRIDE));
        });
  }

  // --- getters --------------------------------------------------------------------

  @Test
  void freshPanelGettersThrowForUnsetDataType() {
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          assertEquals(0, panel.getDataColumnTypes().length);
          assertNull(panel.getSXYDataPickUpDatasetName());
          assertNull(panel.getSXYPickUpIndices());
          assertEquals(0, panel.getTimeDimensionIndexMap().size());
          assertTrue(panel.isVariableDataType());
          assertFalse(panel.isStrideAvailable());
          assertTrue(panel.isSXYZIndexAvailable());
          assertTrue(panel.isVXYIndexAvailable());
          assertNotNull(panel.getOriginScrollPane());
          // the stride and full-length maps require a data type
          assertThrows(IllegalArgumentException.class, () -> panel.getStrideMap().size());
          assertThrows(IllegalArgumentException.class, () -> panel.getFullLengthMap().size());
          panel.clear();
        });
  }

  @Test
  void setStrideMapRoundTripsThroughPanels() {
    final SGVirtualMDArrayVariable.D1[] vars = this.sxyVars();
    final SGDataColumnInfoSet cols = this.sxyCols(vars);

    final SGMDArrayDataSetupPanel[] panelRef = new SGMDArrayDataSetupPanel[1];
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          boolean ok =
              panel.setData(
                  this.file(vars),
                  SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
                  cols,
                  this.infoMap(),
                  false);
          assertTrue(ok);
          panelRef[0] = panel;
        });
    this.pumpEdt();

    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = panelRef[0];

          // the full-range stride is the unambiguous value: a partial stride
          // is serialized with the "end" alias and re-parses against the
          // panel's current length
          Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
          map.put(
              SGDataInformationKeyConstants.KEY_SXY_STRIDE, SGIntegerSeriesSet.createInstance(5));
          map.put(
              SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE,
              SGIntegerSeriesSet.createInstance(5));
          panel.setStrideMap(map);

          Map<String, SGIntegerSeriesSet> result = panel.getStrideMap();
          assertArrayEquals(
              new int[] {0, 1, 2, 3, 4},
              result.get(SGDataInformationKeyConstants.KEY_SXY_STRIDE).getNumbers());
          assertArrayEquals(
              new int[] {0, 1, 2, 3, 4},
              result.get(SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE).getNumbers());
        });
  }

  @Test
  void getOriginsThrowsForUnknownName() {
    this.runOnEdt(
        () -> {
          SGMDArrayDataSetupPanel panel = new SGMDArrayDataSetupPanel();
          assertThrows(NullPointerException.class, () -> panel.getOrigins("no-such-variable"));
        });
  }
}
