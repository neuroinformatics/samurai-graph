package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import jp.riken.brain.ni.samuraigraph.base.SGConstants;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;
import ucar.nc2.write.NetcdfFormatWriter;

/**
 * Characterization tests for {@link SGSXYSDArrayMultipleData} built from in-memory SDArray files:
 * column type resolution, property export (indices, sequential numbers and names), value tables,
 * NetCDF export, merge and the data viewer cells.
 */
class SGSXYSDArrayMultipleDataCharacterizationTest {

  private static final SGConstants.OPERATION PROPERTY_FILE =
      SGConstants.OPERATION.SAVE_TO_PROPERTY_FILE;

  private static final SGConstants.OPERATION ARCHIVE =
      SGConstants.OPERATION.SAVE_TO_ARCHIVE_DATA_SET;

  private static final SGConstants.OPERATION DATASET_NETCDF =
      SGConstants.OPERATION.SAVE_TO_DATA_SET_NETCDF;

  private static final SGConstants.OPERATION EXPORT_SAME_FORMAT =
      SGConstants.OPERATION.EXPORT_TO_FILE_AS_SAME_FORMAT;

  private static final SGConstants.OPERATION EXPORT_TEXT = SGConstants.OPERATION.EXPORT_TO_TEXT;

  private static final SGSXYDataBufferPolicy POLICY =
      new SGSXYDataBufferPolicy(false, false, false, false, false);

  // -----------------------------------------------------------------
  // Fixtures
  // -----------------------------------------------------------------

  // File A: 5 number columns [x, y1, y2, le, ue], 4 points.
  private SGSDArrayFile fileA() {
    return new SGSDArrayFile(
        "dummy.dat",
        new SGDataColumn[] {
          new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
          new SGNumberDataColumn("y1", new double[] {5.0, 6.0, 7.0, 8.0}),
          new SGNumberDataColumn("y2", new double[] {9.0, 10.0, 11.0, 12.0}),
          new SGNumberDataColumn("le", new double[] {1.0, 1.0, 1.0, 1.0}),
          new SGNumberDataColumn("ue", new double[] {2.0, 2.0, 2.0, 2.0})
        });
  }

  // File B: 3 columns [x, y1, text tick label].
  private SGSDArrayFile fileB() {
    return new SGSDArrayFile(
        "dummy.dat",
        new SGDataColumn[] {
          new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
          new SGNumberDataColumn("y1", new double[] {5.0, 6.0, 7.0, 8.0}),
          new SGTextDataColumn("label", new String[] {"a", "bb", "ccc", "dddd"})
        });
  }

  // File D: 5 columns [x, y, le, ue, text tick label] for the single-child form.
  private SGSDArrayFile fileD() {
    return new SGSDArrayFile(
        "dummy.dat",
        new SGDataColumn[] {
          new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
          new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0, 8.0}),
          new SGNumberDataColumn("le", new double[] {1.0, 1.0, 1.0, 1.0}),
          new SGNumberDataColumn("ue", new double[] {2.0, 2.0, 2.0, 2.0}),
          new SGTextDataColumn("label", new String[] {"a", "bb", "ccc", "dddd"})
        });
  }

  // File E: 6 columns [x, y1, y2, le, ue, text tick label] with equal-length labels.
  private SGSDArrayFile fileE() {
    return new SGSDArrayFile(
        "dummy.dat",
        new SGDataColumn[] {
          new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
          new SGNumberDataColumn("y1", new double[] {5.0, 6.0, 7.0, 8.0}),
          new SGNumberDataColumn("y2", new double[] {9.0, 10.0, 11.0, 12.0}),
          new SGNumberDataColumn("le", new double[] {1.0, 1.0, 1.0, 1.0}),
          new SGNumberDataColumn("ue", new double[] {2.0, 2.0, 2.0, 2.0}),
          new SGTextDataColumn("label", new String[] {"aa", "bb", "cc", "dd"})
        });
  }

  // File F: 3 columns [x, y1, y2] for the multi-X form.
  private SGSDArrayFile fileF() {
    return new SGSDArrayFile(
        "dummy.dat",
        new SGDataColumn[] {
          new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
          new SGNumberDataColumn("y1", new double[] {5.0, 6.0, 7.0, 8.0}),
          new SGNumberDataColumn("y2", new double[] {9.0, 10.0, 11.0, 12.0})
        });
  }

  // File G: 3 number columns [x, y1, y2].
  private SGSDArrayFile fileG() {
    return new SGSDArrayFile(
        "dummy.dat",
        new SGDataColumn[] {
          new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
          new SGNumberDataColumn("y1", new double[] {5.0, 6.0, 7.0, 8.0}),
          new SGNumberDataColumn("y2", new double[] {9.0, 10.0, 11.0, 12.0})
        });
  }

  private SGSXYSDArrayMultipleData data(
      SGSDArrayFile file,
      Integer[] x,
      Integer[] y,
      Integer[] le,
      Integer[] ue,
      Integer[] eh,
      Integer[] tl,
      Integer[] th) {
    return new SGSXYSDArrayMultipleData(
        file, new SGDataSourceObserver(), x, y, le, ue, eh, tl, th, null, null, true, null);
  }

  // multi-Y over y1/y2 of file A.
  private SGSXYSDArrayMultipleData basic() {
    return this.data(
        fileA(), new Integer[] {0}, new Integer[] {1, 2}, null, null, null, null, null);
  }

  // multi-Y with separate lower/upper error bars for y1 only.
  private SGSXYSDArrayMultipleData withBars() {
    return this.data(
        fileA(),
        new Integer[] {0},
        new Integer[] {1, 2},
        new Integer[] {3},
        new Integer[] {4},
        new Integer[] {1},
        null,
        null);
  }

  // multi-Y with a shared lower/upper error bar for y1.
  private SGSXYSDArrayMultipleData withSharedBars() {
    return this.data(
        fileA(),
        new Integer[] {0},
        new Integer[] {1, 2},
        new Integer[] {3},
        new Integer[] {3},
        new Integer[] {1},
        null,
        null);
  }

  // single Y with a text tick label for y1 (generic).
  private SGSXYSDArrayMultipleData withTickLabel() {
    return this.data(
        fileB(),
        new Integer[] {0},
        new Integer[] {1},
        null,
        null,
        null,
        new Integer[] {2},
        new Integer[] {1});
  }

  // single Y with separate bars and a text tick label.
  private SGSXYSDArrayMultipleData singleWithBarsAndTickLabel() {
    return this.data(
        fileD(),
        new Integer[] {0},
        new Integer[] {1},
        new Integer[] {2},
        new Integer[] {3},
        new Integer[] {1},
        new Integer[] {4},
        new Integer[] {1});
  }

  // multi-X form: x and y2 are the X values, y1 is the single Y.
  private SGSXYSDArrayMultipleData multiX() {
    return this.data(
        fileF(), new Integer[] {0, 2}, new Integer[] {1}, null, null, null, null, null);
  }

  private File createTempNetcdfFile() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    return path.toFile();
  }

  private Document newDocument() throws Exception {
    return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
  }

  // -----------------------------------------------------------------
  // getCurrentColumnType
  // -----------------------------------------------------------------

  @Test
  void currentColumnTypeBasicMultipleY() {
    assertArrayEquals(new String[] {"X", "Y", "Y", "", ""}, basic().getCurrentColumnType());
  }

  @Test
  void currentColumnTypeSeparateErrorBars() {
    assertArrayEquals(
        new String[] {"X", "Y", "Y", "Lower Error for y1", "Upper Error for y1"},
        withBars().getCurrentColumnType());
  }

  @Test
  void currentColumnTypeSharedErrorBar() {
    assertArrayEquals(
        new String[] {"X", "Y", "Y", "Lower / Upper Error for y1", ""},
        withSharedBars().getCurrentColumnType());
  }

  @Test
  void currentColumnTypeTickLabelHolder() {
    assertArrayEquals(
        new String[] {"X", "Y", "Tick Label for y1"}, withTickLabel().getCurrentColumnType());
  }

  @Test
  void currentColumnTypeMultiX() {
    assertArrayEquals(new String[] {"X", "Y", "X"}, multiX().getCurrentColumnType());
  }

  // -----------------------------------------------------------------
  // setColumnType
  // -----------------------------------------------------------------

  @Test
  void setColumnTypeResolvesHolderByTitle() {
    SGSXYSDArrayMultipleData data = basic();
    assertTrue(
        data.setColumnType(
            new String[] {"X", "Y", "Y", "Lower Error for y1", "Upper Error for y1"}));
    assertArrayEquals(new Integer[] {3}, data.getLowerErrorIndices());
    assertArrayEquals(new Integer[] {4}, data.getUpperErrorIndices());
    assertArrayEquals(new Integer[] {1}, data.getErrorBarHolderIndices());
    assertArrayEquals(
        new String[] {"X", "Y", "Y", "Lower Error for y1", "Upper Error for y1"},
        data.getCurrentColumnType());
  }

  @Test
  void setColumnTypeResolvesHolderByOneBasedNumber() {
    SGSXYSDArrayMultipleData data = basic();
    assertTrue(
        data.setColumnType(
            new String[] {"X", "Y", "Y", "Lower Error for No.2", "Upper Error for No.2"}));
    assertArrayEquals(new Integer[] {3}, data.getLowerErrorIndices());
    assertArrayEquals(new Integer[] {4}, data.getUpperErrorIndices());
    assertArrayEquals(new Integer[] {1}, data.getErrorBarHolderIndices());
  }

  @Test
  void setColumnTypeSharedErrorForm() {
    SGSXYSDArrayMultipleData data = basic();
    assertTrue(data.setColumnType(new String[] {"X", "Y", "Y", "Lower / Upper Error for y1", ""}));
    assertArrayEquals(new Integer[] {3}, data.getLowerErrorIndices());
    assertArrayEquals(new Integer[] {3}, data.getUpperErrorIndices());
    assertArrayEquals(new Integer[] {1}, data.getErrorBarHolderIndices());
    assertTrue(data.isErrorBarAvailable());
  }

  @Test
  void setColumnTypeTickLabelForm() {
    SGSXYSDArrayMultipleData data = basic();
    assertTrue(data.setColumnType(new String[] {"X", "Y", "Y", "Tick Label for y2", ""}));
    assertArrayEquals(new Integer[] {3}, data.getTickLabelIndices());
    assertArrayEquals(new Integer[] {2}, data.getTickLabelHolderIndices());
    assertTrue(data.isTickLabelAvailable());
  }

  @Test
  void setColumnTypeSwapsToMultiX() {
    SGSXYSDArrayMultipleData data = basic();
    assertTrue(data.setColumnType(new String[] {"X", "Y", "X", "", ""}));
    assertArrayEquals(new Integer[] {0, 2}, data.getXIndices());
    assertArrayEquals(new Integer[] {1}, data.getYIndices());
    assertFalse(data.hasMultipleYValues());
  }

  @Test
  void setColumnTypeRejectsMismatchedErrorHolders() {
    SGSXYSDArrayMultipleData data = basic();
    // the upper error refers to y1 while the lower error refers to y2
    assertFalse(
        data.setColumnType(
            new String[] {"X", "Y", "Y", "Lower Error for y2", "Upper Error for y1"}));
  }

  @Test
  void setColumnTypeRejectsUnknownColumnType() {
    assertFalse(basic().setColumnType(new String[] {"X", "Y", "Y", "Foo", ""}));
  }

  @Test
  void setColumnTypeRejectsUnpairedErrorIndices() {
    // only lower errors, no upper errors
    assertFalse(
        basic()
            .setColumnType(
                new String[] {"X", "Y", "Y", "Lower Error for y1", "Lower Error for y2"}));
  }

  @Test
  void setColumnTypeRejectsMissingX() {
    assertFalse(basic().setColumnType(new String[] {"", "Y", "Y", "", ""}));
  }

  @Test
  void setColumnTypeRejectsBothMultiple() {
    assertFalse(basic().setColumnType(new String[] {"X", "Y", "Y", "", "X"}));
  }

  // -----------------------------------------------------------------
  // getColumnTypeCommandString
  // -----------------------------------------------------------------

  @Test
  void columnTypeCommandStringBasic() {
    assertEquals("(1:X,2:Y,3:Y)", basic().getColumnTypeCommandString());
  }

  @Test
  void columnTypeCommandStringSeparateBars() {
    assertEquals(
        "(1:X,2:Y,3:Y,4:Lower Error for No.2,5:Upper Error for No.2)",
        withBars().getColumnTypeCommandString());
  }

  @Test
  void columnTypeCommandStringSharedBar() {
    assertEquals(
        "(1:X,2:Y,3:Y,4:Lower / Upper Error for No.2)",
        withSharedBars().getColumnTypeCommandString());
  }

  @Test
  void columnTypeCommandStringTickLabel() {
    assertEquals("(1:X,2:Y,3:Tick Label for No.2)", withTickLabel().getColumnTypeCommandString());
  }

  @Test
  void columnTypeCommandStringMultiX() {
    assertEquals("(1:X,3:X,2:Y)", multiX().getColumnTypeCommandString());
  }

  // -----------------------------------------------------------------
  // Value tables
  // -----------------------------------------------------------------

  @Test
  void valueTableExportToTextMultipleY() {
    SGSXYSDArrayMultipleData data =
        this.data(fileG(), new Integer[] {0}, new Integer[] {1, 2}, null, null, null, null, null);
    Object[][] table = data.getValueTable(new SGExportParameter(EXPORT_TEXT), POLICY);
    assertEquals(4, table.length);
    assertEquals(3, table[0].length);
    assertEquals(1.0, table[0][0]);
    assertEquals(5.0, table[0][1]);
    assertEquals(9.0, table[0][2]);
    assertEquals(4.0, table[3][0]);
    assertEquals(8.0, table[3][1]);
    assertEquals(12.0, table[3][2]);
  }

  @Test
  void valueTableArchiveSeparateBars() {
    Object[][] table = withBars().getValueTable(new SGExportParameter(ARCHIVE), POLICY);
    assertEquals(4, table.length);
    assertEquals(5, table[0].length);
    assertEquals(1.0, table[0][0]);
    assertEquals(5.0, table[0][1]);
    assertEquals(9.0, table[0][2]);
    assertEquals(1.0, table[0][3]);
    assertEquals(2.0, table[0][4]);
    assertEquals(8.0, table[3][1]);
    assertEquals(12.0, table[3][2]);
    assertEquals(1.0, table[3][3]);
    assertEquals(2.0, table[3][4]);
  }

  @Test
  void valueTableSharedBarUsesSingleColumn() {
    Object[][] table =
        withSharedBars().getValueTable(new SGExportParameter(EXPORT_SAME_FORMAT), POLICY);
    assertEquals(4, table.length);
    // a shared lower/upper error bar occupies a single column
    assertEquals(4, table[0].length);
    assertEquals(1.0, table[0][0]);
    assertEquals(5.0, table[0][1]);
    assertEquals(9.0, table[0][2]);
    assertEquals(1.0, table[0][3]);
  }

  @Test
  void valueTableTickLabelIsQuoted() {
    Object[][] table = withTickLabel().getValueTable(new SGExportParameter(EXPORT_TEXT), POLICY);
    assertEquals(4, table.length);
    assertEquals(3, table[0].length);
    assertEquals(1.0, table[0][0]);
    assertEquals(5.0, table[0][1]);
    assertEquals("\"a\"", table[0][2]);
    assertEquals(4.0, table[3][0]);
    assertEquals(8.0, table[3][1]);
    assertEquals("\"dddd\"", table[3][2]);
  }

  @Test
  void valueTableSingleChildWithBarsAndTickLabel() {
    Object[][] table =
        singleWithBarsAndTickLabel().getValueTable(new SGExportParameter(ARCHIVE), POLICY);
    assertEquals(4, table.length);
    assertEquals(5, table[0].length);
    assertEquals(1.0, table[0][0]);
    assertEquals(5.0, table[0][1]);
    assertEquals(1.0, table[0][2]);
    assertEquals(2.0, table[0][3]);
    assertEquals("\"a\"", table[0][4]);
  }

  @Test
  void valueTableNotAvailableInPropertyMode() {
    assertNull(basic().getValueTable(new SGExportParameter(PROPERTY_FILE), POLICY));
  }

  // -----------------------------------------------------------------
  // writeProperty
  // -----------------------------------------------------------------

  @Test
  void writePropertyFileModeWritesRawIndices() throws Exception {
    Element el = newDocument().createElement("data");
    SGExportParameter param = new SGExportParameter(PROPERTY_FILE);
    assertTrue(withBars().writeProperty(el, param));
    assertEquals("true", el.getAttribute(SGDataPropertyKeyConstants.KEY_ARRAY_SECTION_AVAILABLE));
    assertEquals("{0}", el.getAttribute(SGDataPropertyKeyConstants.KEY_X_VALUE_COLUMN_INDICES));
    assertEquals("{1,2}", el.getAttribute(SGDataPropertyKeyConstants.KEY_Y_VALUE_COLUMN_INDICES));
    assertEquals(
        "{3}", el.getAttribute(SGDataPropertyKeyConstants.KEY_LOWER_ERROR_BAR_COLUMN_INDICES));
    assertEquals(
        "{4}", el.getAttribute(SGDataPropertyKeyConstants.KEY_UPPER_ERROR_BAR_COLUMN_INDICES));
    assertEquals(
        "{1}", el.getAttribute(SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_COLUMN_INDICES));
    assertEquals("0:end", el.getAttribute(SGDataPropertyKeyConstants.KEY_ARRAY_SECTION));
    assertFalse(el.hasAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_COLUMN_INDICES));
  }

  @Test
  void writePropertyFileModeWritesTickLabelIndices() throws Exception {
    Element el = newDocument().createElement("data");
    SGExportParameter param = new SGExportParameter(PROPERTY_FILE);
    assertTrue(withTickLabel().writeProperty(el, param));
    assertEquals("{0}", el.getAttribute(SGDataPropertyKeyConstants.KEY_X_VALUE_COLUMN_INDICES));
    assertEquals("{1}", el.getAttribute(SGDataPropertyKeyConstants.KEY_Y_VALUE_COLUMN_INDICES));
    assertEquals("{2}", el.getAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_COLUMN_INDICES));
    assertEquals(
        "{1}", el.getAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_HOLDER_COLUMN_INDICES));
    assertEquals("0:end", el.getAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_ARRAY_SECTION));
  }

  @Test
  void writePropertyArchiveModeWritesSequentialIndices() throws Exception {
    Element el = newDocument().createElement("data");
    SGExportParameter param = new SGExportParameter(ARCHIVE);
    assertTrue(withBars().writeProperty(el, param));
    assertEquals("{0}", el.getAttribute(SGDataPropertyKeyConstants.KEY_X_VALUE_COLUMN_INDICES));
    assertEquals("{1,2}", el.getAttribute(SGDataPropertyKeyConstants.KEY_Y_VALUE_COLUMN_INDICES));
    assertEquals(
        "{3}", el.getAttribute(SGDataPropertyKeyConstants.KEY_LOWER_ERROR_BAR_COLUMN_INDICES));
    assertEquals(
        "{4}", el.getAttribute(SGDataPropertyKeyConstants.KEY_UPPER_ERROR_BAR_COLUMN_INDICES));
    // the holder is renumbered to the position of y1 within the exported values
    assertEquals(
        "{1}", el.getAttribute(SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_COLUMN_INDICES));
    assertEquals("0:end", el.getAttribute(SGDataPropertyKeyConstants.KEY_ARRAY_SECTION));
  }

  @Test
  void writePropertyArchiveModeSharesErrorSlot() throws Exception {
    Element el = newDocument().createElement("data");
    SGExportParameter param = new SGExportParameter(ARCHIVE);
    assertTrue(withSharedBars().writeProperty(el, param));
    assertEquals(
        "{3}", el.getAttribute(SGDataPropertyKeyConstants.KEY_LOWER_ERROR_BAR_COLUMN_INDICES));
    assertEquals(
        "{3}", el.getAttribute(SGDataPropertyKeyConstants.KEY_UPPER_ERROR_BAR_COLUMN_INDICES));
    assertEquals(
        "{1}", el.getAttribute(SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_COLUMN_INDICES));
  }

  @Test
  void writePropertyDataSetNetcdfModeWritesColumnNames() throws Exception {
    Element el = newDocument().createElement("data");
    SGExportParameter param = new SGExportParameter(DATASET_NETCDF);
    assertTrue(singleWithBarsAndTickLabel().writeProperty(el, param));
    // x/y/holders/tick labels are sequential column names, each wrapped in braces
    assertEquals(
        "{column0}", el.getAttribute(SGDataPropertyKeyConstants.KEY_X_VALUE_COLUMN_INDICES));
    assertEquals(
        "{column1}", el.getAttribute(SGDataPropertyKeyConstants.KEY_Y_VALUE_COLUMN_INDICES));
    // lower/upper are written as bracketed plain sequential numbers
    assertEquals(
        "{2}", el.getAttribute(SGDataPropertyKeyConstants.KEY_LOWER_ERROR_BAR_COLUMN_INDICES));
    assertEquals(
        "{3}", el.getAttribute(SGDataPropertyKeyConstants.KEY_UPPER_ERROR_BAR_COLUMN_INDICES));
    assertEquals(
        "{column1}",
        el.getAttribute(SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_COLUMN_INDICES));
    assertEquals(
        "{column4}", el.getAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_COLUMN_INDICES));
    assertEquals(
        "{column1}",
        el.getAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_HOLDER_COLUMN_INDICES));
    assertEquals("Index", el.getAttribute(SGDataPropertyKeyConstants.KEY_INDEX_VARIABLE_NAME));
    assertEquals("0:end", el.getAttribute(SGDataPropertyKeyConstants.KEY_INDEX_ARRAY_SECTION));
  }

  @Test
  void writePropertyExportModeIsRejected() throws Exception {
    Element el = newDocument().createElement("data");
    assertFalse(basic().writeProperty(el, new SGExportParameter(EXPORT_TEXT)));
  }

  // -----------------------------------------------------------------
  // NetCDF export
  // -----------------------------------------------------------------

  @Test
  void exportToNetCDFFileRejectsMultiX() throws Exception {
    File file = createTempNetcdfFile();
    NetcdfFormatWriter.Builder builder =
        NetcdfFormatWriter.createNewNetcdf3(file.getAbsolutePath());
    SGExportParameter param = new SGExportParameter(EXPORT_SAME_FORMAT);
    assertFalse(multiX().exportToNetCDFFile(builder, param, POLICY));
  }

  @Test
  void exportToNetCDFFileWritesXAndYVariables() throws Exception {
    File file = createTempNetcdfFile();
    NetcdfFormatWriter.Builder builder =
        NetcdfFormatWriter.createNewNetcdf3(file.getAbsolutePath());
    SGExportParameter param = new SGExportParameter(EXPORT_SAME_FORMAT);
    assertTrue(basic().exportToNetCDFFile(builder, param, POLICY));
    try (NetcdfFile nc = NetcdfFiles.open(file.getAbsolutePath())) {
      assertEquals(3, nc.getVariables().size());
      Variable x = nc.findVariable("X");
      Variable y1 = nc.findVariable("Y1");
      Variable y2 = nc.findVariable("Y2");
      assertEquals(DataType.DOUBLE, x.getDataType());
      assertArrayEquals(new int[] {4}, x.getShape());
      double[] xValues = (double[]) x.read().copyTo1DJavaArray();
      assertEquals(1.0, xValues[0], 0.0);
      assertEquals(4.0, xValues[3], 0.0);
      assertEquals(5.0, ((double[]) y1.read().copyTo1DJavaArray())[0], 0.0);
      assertEquals(12.0, ((double[]) y2.read().copyTo1DJavaArray())[3], 0.0);
    }
  }

  @Test
  void exportToNetCDFFileSingleChildWritesErrorsAndTickLabel() throws Exception {
    File file = createTempNetcdfFile();
    NetcdfFormatWriter.Builder builder =
        NetcdfFormatWriter.createNewNetcdf3(file.getAbsolutePath());
    SGExportParameter param = new SGExportParameter(EXPORT_SAME_FORMAT);
    assertTrue(singleWithBarsAndTickLabel().exportToNetCDFFile(builder, param, POLICY));
    try (NetcdfFile nc = NetcdfFiles.open(file.getAbsolutePath())) {
      assertEquals(5, nc.getVariables().size());
      assertEquals(DataType.DOUBLE, nc.findVariable("Y").getDataType());
      double[] lower = (double[]) nc.findVariable("LowerError").read().copyTo1DJavaArray();
      double[] upper = (double[]) nc.findVariable("UpperError").read().copyTo1DJavaArray();
      assertEquals(1.0, lower[0], 0.0);
      assertEquals(2.0, upper[3], 0.0);
      Variable tickLabel = nc.findVariable("TickLabel");
      assertEquals(DataType.CHAR, tickLabel.getDataType());
      assertArrayEquals(new int[] {4, 4}, tickLabel.getShape());
      char[] chars = (char[]) tickLabel.read().copyTo1DJavaArray();
      StringBuilder first = new StringBuilder();
      for (int ii = 0; ii < chars.length / 4; ii++) {
        if (chars[ii] == 0) {
          break;
        }
        first.append(chars[ii]);
      }
      assertEquals("a", first.toString());
    }
  }

  @Test
  void saveToDataSetNetCDFFileRoundTrip() throws Exception {
    File file = createTempNetcdfFile();
    SGSXYSDArrayMultipleData data =
        this.data(
            fileE(),
            new Integer[] {0},
            new Integer[] {1, 2},
            new Integer[] {3},
            new Integer[] {4},
            new Integer[] {1},
            new Integer[] {5},
            new Integer[] {1});
    assertTrue(data.saveToDataSetNetCDFFile(file));
    try (NetcdfFile nc = NetcdfFiles.open(file.getAbsolutePath())) {
      // Index plus the five numeric columns plus the CHAR label column
      assertEquals(7, nc.getVariables().size());
      int[] indexValues = (int[]) nc.findVariable("Index").read().copyTo1DJavaArray();
      assertArrayEquals(new int[] {0, 1, 2, 3}, indexValues);
      Variable column5 = nc.findVariable("column5");
      assertEquals(DataType.CHAR, column5.getDataType());
      assertArrayEquals(new int[] {4, 2}, column5.getShape());
      Attribute longName = column5.findAttribute("long_name");
      assertNotNull(longName);
      assertEquals("label", longName.getStringValue());
      double[] xValues = (double[]) nc.findVariable("column0").read().copyTo1DJavaArray();
      assertEquals(1.0, xValues[0], 0.0);
      assertEquals(4.0, xValues[3], 0.0);
      double[] y1Values = (double[]) nc.findVariable("column1").read().copyTo1DJavaArray();
      assertEquals(5.0, y1Values[0], 0.0);
      double[] leValues = (double[]) nc.findVariable("column3").read().copyTo1DJavaArray();
      assertEquals(1.0, leValues[0], 0.0);
      // The 2 x 2 CHAR matrix flattens row-major to "aabbccdd".
      char[] chars = (char[]) column5.read().copyTo1DJavaArray();
      assertEquals("aabbccdd", new String(chars));
    }
  }

  // -----------------------------------------------------------------
  // merge
  // -----------------------------------------------------------------

  @Test
  void mergeReturnsNullForEmptyList() {
    assertNull(SGSXYSDArrayMultipleData.merge(new ArrayList<>()));
  }

  @Test
  void mergeReturnsNullForNonInstanceElement() {
    List<SGData> list = new ArrayList<>();
    list.add(
        new SGSXYSDArrayData(
            fileA(),
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
            null));
    assertNull(SGSXYSDArrayMultipleData.merge(list));
  }

  @Test
  void mergeReturnsNullForNullElement() {
    List<SGData> list = new ArrayList<>();
    list.add(null);
    assertNull(SGSXYSDArrayMultipleData.merge(list));
  }

  @Test
  void mergeUnionsTheColumnIndices() {
    SGSDArrayFile file = fileA();
    SGSXYSDArrayMultipleData d1 =
        this.data(
            file,
            new Integer[] {0},
            new Integer[] {1},
            new Integer[] {3},
            new Integer[] {4},
            new Integer[] {1},
            null,
            null);
    SGSXYSDArrayMultipleData d2 =
        this.data(file, new Integer[] {0}, new Integer[] {2}, null, null, null, null, null);
    List<SGData> list = new ArrayList<>();
    list.add(d1);
    list.add(d2);
    SGSXYSDArrayMultipleData merged =
        (SGSXYSDArrayMultipleData) SGSXYSDArrayMultipleData.merge(list);
    assertArrayEquals(new Integer[] {0}, merged.getXIndices());
    assertArrayEquals(new Integer[] {1, 2}, merged.getYIndices());
    assertArrayEquals(new Integer[] {3}, merged.getLowerErrorIndices());
    assertArrayEquals(new Integer[] {4}, merged.getUpperErrorIndices());
    assertArrayEquals(new Integer[] {1}, merged.getErrorBarHolderIndices());
    assertNull(merged.getTickLabelIndices());
    assertTrue(merged.isErrorBarAvailable());
    assertFalse(merged.isTickLabelAvailable());
  }

  // -----------------------------------------------------------------
  // Misc
  // -----------------------------------------------------------------

  @Test
  void infoMapReportsTheMultipleDataType() {
    Map<String, Object> map = basic().getInfoMap();
    assertEquals(Boolean.TRUE, map.get(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE));
    assertEquals(
        SGDataTypeConstants.SXY_MULTIPLE_DATA,
        map.get(SGDataInformationKeyConstants.KEY_DATA_TYPE));
  }

  @Test
  void childNameListMultipleYListsTheYTitles() {
    List<String> names = basic().getChildNameList();
    assertEquals(Arrays.asList("y1", "y2"), names);
  }

  @Test
  void childNameListMultiXListsTheXTitles() {
    List<String> names = multiX().getChildNameList();
    assertEquals(Arrays.asList("x", "y2"), names);
  }

  @Test
  void dataViewerColumnTypesAreXAndY() {
    assertArrayEquals(new String[] {"X", "Y"}, basic().getDataViewerColumnTypes());
  }

  @Test
  void dataViewerCellKeepsMultipleYColumn() {
    // multiple Y: the Y cell keeps its column, the X cell falls back to column 0
    SGTwoDimensionalArrayIndex yCell =
        basic().getDataViewerCell(new SGTwoDimensionalArrayIndex(1, 2), "Y", false);
    assertEquals(1, yCell.getColumn());
    assertEquals(2, yCell.getRow());
    SGTwoDimensionalArrayIndex xCell =
        basic().getDataViewerCell(new SGTwoDimensionalArrayIndex(0, 3), "X", false);
    assertEquals(0, xCell.getColumn());
    assertEquals(3, xCell.getRow());
  }

  @Test
  void dataViewerCellKeepsMultipleXColumn() {
    // multiple X: the X cell keeps its column, the Y cell falls back to column 0
    SGTwoDimensionalArrayIndex xCell =
        multiX().getDataViewerCell(new SGTwoDimensionalArrayIndex(1, 0), "X", false);
    assertEquals(1, xCell.getColumn());
    assertEquals(0, xCell.getRow());
    SGTwoDimensionalArrayIndex yCell =
        multiX().getDataViewerCell(new SGTwoDimensionalArrayIndex(2, 0), "Y", false);
    assertEquals(0, yCell.getColumn());
    assertEquals(0, yCell.getRow());
  }
}
