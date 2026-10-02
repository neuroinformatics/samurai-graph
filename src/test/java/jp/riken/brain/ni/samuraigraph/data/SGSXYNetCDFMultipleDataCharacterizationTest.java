package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import jp.riken.brain.ni.samuraigraph.base.SGConstants;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.write.NetcdfFormatWriter;

/**
 * Characterization tests for the less-exercised paths of {@link SGSXYNetCDFMultipleData}: column
 * type resolution (picked and not picked), picked-dimension info, child name lists, data viewer
 * cells and property export.
 */
class SGSXYNetCDFMultipleDataCharacterizationTest {

  private static final String EXAMPLE_16 = "examples/data/Example16.nc";

  private SGNetCDFFile ncfile;

  @BeforeEach
  void openFile() throws IOException {
    this.ncfile = new SGNetCDFFile(NetcdfFiles.open(EXAMPLE_16));
  }

  // -----------------------------------------------------------------
  // Fixtures
  // -----------------------------------------------------------------

  // Example16.nc: x(8), y(12) are coordinate variables; height, le, ue are (x, y).
  private SGNetCDFDataColumnInfo info(String name, String type) {
    return SGDataFileUtility.createDataColumnInfo(this.ncfile.findVariable(name), type);
  }

  // x/height: the not-picked, multiple-Y form.
  private SGSXYNetCDFMultipleData createBasic() {
    return new SGSXYNetCDFMultipleData(
        this.ncfile,
        new SGDataSourceObserver(),
        new SGNetCDFDataColumnInfo[] {info("x", SGDataColumnTypeConstants.X_VALUE)},
        new SGNetCDFDataColumnInfo[] {info("height", SGDataColumnTypeConstants.Y_VALUE)},
        null,
        null,
        null,
        null,
        true);
  }

  // Picks up the "y" dimension (indices 0..2) of Example16: 3 children over x(8).
  private SGSXYNetCDFMultipleData createPicked() {
    return new SGSXYNetCDFMultipleData(
        this.ncfile,
        new SGDataSourceObserver(),
        info("x", SGDataColumnTypeConstants.X_VALUE),
        info("height", SGDataColumnTypeConstants.Y_VALUE),
        null,
        null,
        null,
        null,
        null,
        "y",
        new SGIntegerSeriesSet(0, 2, 1),
        null,
        null,
        null,
        null,
        true);
  }

  // A 2D file with error bars: x(5), y(12) coordinates, y1(le, ue) of type (x, y).
  private static Path createErrorBarFile() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    NetcdfFileWriter writer =
        NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf3, path.toString());
    Dimension xDim = writer.addDimension("x", 5);
    Dimension yDim = writer.addDimension("y", 12);
    List<Dimension> xyDims = Arrays.asList(xDim, yDim);
    writer.addVariable(null, "x", DataType.DOUBLE, "x");
    writer.addVariable(null, "y", DataType.DOUBLE, "y");
    writer.addVariable(null, "y1", DataType.DOUBLE, xyDims);
    writer.addVariable(null, "le", DataType.DOUBLE, xyDims);
    writer.addVariable(null, "ue", DataType.DOUBLE, xyDims);
    writer.create();
    double[] xs = {1.0, 2.0, 3.0, 4.0, 5.0};
    double[] ys = new double[12];
    for (int i = 0; i < ys.length; i++) {
      ys[i] = i + 1.0;
    }
    writer.write("x", Array.factory(DataType.DOUBLE, new int[] {5}, xs));
    writer.write("y", Array.factory(DataType.DOUBLE, new int[] {12}, ys));
    double[] y1 = new double[5 * 12];
    double[] le = new double[5 * 12];
    double[] ue = new double[5 * 12];
    for (int i = 0; i < 5 * 12; i++) {
      y1[i] = 10.0 + i;
      le[i] = 1.0;
      ue[i] = 2.0;
    }
    writer.write("y1", Array.factory(DataType.DOUBLE, new int[] {5, 12}, y1));
    writer.write("le", Array.factory(DataType.DOUBLE, new int[] {5, 12}, le));
    writer.write("ue", Array.factory(DataType.DOUBLE, new int[] {5, 12}, ue));
    writer.close();
    return path;
  }

  private static SGSXYNetCDFMultipleData createPickedWithErrorBar() throws Exception {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open(createErrorBarFile().toString()));
    return new SGSXYNetCDFMultipleData(
        file,
        new SGDataSourceObserver(),
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE),
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("y1"), SGDataColumnTypeConstants.Y_VALUE),
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("le"),
            SGDataColumnTitleUtility.appendColumnTitle(
                SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "y1")),
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("ue"),
            SGDataColumnTitleUtility.appendColumnTitle(
                SGDataColumnTypeConstants.UPPER_ERROR_VALUE, "y1")),
        null,
        null,
        null,
        "y",
        new SGIntegerSeriesSet(0, 2, 1),
        null,
        null,
        null,
        null,
        true);
  }

  // Builds a column-type array aligned to the file's variable order, assigning a type per
  // variable name and defaulting the rest to the empty string.
  private static String[] columnsFor(SGNetCDFFile file, Map<String, String> byName) {
    List<SGNetCDFVariable> vars = file.getVariables();
    String[] columns = new String[vars.size()];
    for (int ii = 0; ii < vars.size(); ii++) {
      String name = vars.get(ii).getName();
      columns[ii] = byName.getOrDefault(name, "");
    }
    return columns;
  }

  // -----------------------------------------------------------------
  // Non-picked column type resolution
  // -----------------------------------------------------------------

  @Test
  void currentColumnTypeForBasicData() {
    SGSXYNetCDFMultipleData data = createBasic();
    // Example16 variable order: x, y, height, le, ue.
    assertColumns(
        data,
        new String[] {
          SGDataColumnTypeConstants.X_VALUE, "", SGDataColumnTypeConstants.Y_VALUE, "", ""
        });
  }

  // The column type array is aligned to the file's variable order, so the helper maps it back to
  // variable names before asserting.
  private static void assertColumns(SGSXYNetCDFMultipleData data, String[] byFileOrder) {
    String[] types = data.getCurrentColumnType();
    assertNotNull(types);
    assertEquals(byFileOrder.length, types.length);
    for (int ii = 0; ii < byFileOrder.length; ii++) {
      assertEquals(byFileOrder[ii], types[ii]);
    }
  }

  @Test
  void setColumnTypeNotPickedReassignsRoles() {
    SGSXYNetCDFMultipleData data = createBasic();
    // Swap the roles: height becomes the X value and x becomes a Y value.
    Map<String, String> byName =
        Map.of(
            "x", SGDataColumnTypeConstants.Y_VALUE,
            "height", SGDataColumnTypeConstants.X_VALUE);
    assertTrue(data.setColumnType(columnsFor(this.ncfile, byName)));
    assertEquals("height", data.getXVariable().getName());
    assertEquals("x", data.getMultipleVariables()[0].getName());
  }

  @Test
  void setColumnTypeNotPickedAssignsErrorBarHolder() {
    SGSXYNetCDFMultipleData data = createBasic();
    // The error bars reference the y-value holder: height.
    Map<String, String> byName =
        Map.of(
            "x",
            SGDataColumnTypeConstants.X_VALUE,
            "height",
            SGDataColumnTypeConstants.Y_VALUE,
            "le",
            SGDataColumnTitleUtility.appendColumnTitle(
                SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "height"),
            "ue",
            SGDataColumnTitleUtility.appendColumnTitle(
                SGDataColumnTypeConstants.UPPER_ERROR_VALUE, "height"));
    assertTrue(data.setColumnType(columnsFor(this.ncfile, byName)));
    assertTrue(data.isErrorBarAvailable());
    assertEquals("le", data.getLowerErrorVariable().getName());
    assertEquals("ue", data.getUpperErrorVariable().getName());
    assertEquals("height", data.getErrorBarHolderVariable().getName());
  }

  @Test
  void setColumnTypeNotPickedRejectsUnmatchedHolder() {
    SGSXYNetCDFMultipleData data = createBasic();
    // le and ue must reference the same holder.
    Map<String, String> byName =
        Map.of(
            "x",
            SGDataColumnTypeConstants.X_VALUE,
            "height",
            SGDataColumnTypeConstants.Y_VALUE,
            "le",
            SGDataColumnTitleUtility.appendColumnTitle(
                SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "height"),
            "ue",
            SGDataColumnTitleUtility.appendColumnTitle(
                SGDataColumnTypeConstants.UPPER_ERROR_VALUE, "x"));
    assertFalse(data.setColumnType(columnsFor(this.ncfile, byName)));
  }

  @Test
  void setColumnTypeRejectsUnknownString() {
    SGSXYNetCDFMultipleData data = createBasic();
    Map<String, String> byName = Map.of("x", SGDataColumnTypeConstants.X_VALUE, "height", "Bogus");
    assertFalse(data.setColumnType(columnsFor(this.ncfile, byName)));
  }

  // -----------------------------------------------------------------
  // Picked-dimension info and column types
  // -----------------------------------------------------------------

  @Test
  void pickedDataExposesPickUpInfo() {
    SGSXYNetCDFMultipleData data = createPicked();
    assertTrue(data.isDimensionPicked());
    assertEquals("y", data.getDimensionName());
    SGIntegerSeriesSet indices = data.getIndices();
    assertNotNull(indices);
    assertArrayEquals3(indices);
    assertEquals(3, data.getChildNumber());
    assertEquals(8, data.getAllPointsNumber());
    assertTrue(data.hasMultipleYValues());
    assertEquals("x", data.getSingleVariable().getName());
  }

  private static void assertArrayEquals3(SGIntegerSeriesSet set) {
    int[] nums = set.getNumbers();
    assertEquals(3, nums.length);
    assertEquals(0, nums[0]);
    assertEquals(2, nums[2]);
  }

  @Test
  void pickedSetColumnTypeAppliesPickupType() {
    SGSXYNetCDFMultipleData data = createBasic();
    // Assign a pickup: the coordinate variable y holds the PICKUP type.
    Map<String, String> byName =
        Map.of(
            "x", SGDataColumnTypeConstants.X_VALUE,
            "y", SGDataColumnTypeConstants.PICKUP,
            "height", SGDataColumnTypeConstants.Y_VALUE);
    SGNetCDFPickUpDimensionInfo pickUpInfo =
        new SGNetCDFPickUpDimensionInfo("y", new SGIntegerSeriesSet(0, 2, 1));
    assertTrue(data.setColumnType(columnsFor(this.ncfile, byName), pickUpInfo));
    assertTrue(data.isDimensionPicked());
    assertEquals("y", data.getDimensionName());
    assertEquals(3, data.getChildNumber());
    assertEquals(SGDataColumnTypeConstants.PICKUP, columnTypeFor(data, "y"));
  }

  // Finds the column type assigned to the variable with the given name.
  private static String columnTypeFor(SGSXYNetCDFMultipleData data, String name) {
    String[] types = data.getCurrentColumnType();
    List<SGNetCDFVariable> vars = data.getNetcdfFile().getVariables();
    for (int ii = 0; ii < vars.size(); ii++) {
      if (vars.get(ii).getName().equals(name)) {
        return types[ii];
      }
    }
    throw new IllegalStateException("variable not found: " + name);
  }

  @Test
  void pickedSetColumnTypeRejectsMissingPickup() {
    SGSXYNetCDFMultipleData data = createBasic();
    // No variable is typed PICKUP, so the picked assignment must fail and the pick up info is
    // cleared again.
    Map<String, String> byName =
        Map.of(
            "x", SGDataColumnTypeConstants.X_VALUE,
            "height", SGDataColumnTypeConstants.Y_VALUE);
    SGNetCDFPickUpDimensionInfo pickUpInfo =
        new SGNetCDFPickUpDimensionInfo("y", new SGIntegerSeriesSet(0, 2, 1));
    assertFalse(data.setColumnType(columnsFor(this.ncfile, byName), pickUpInfo));
    assertFalse(data.isDimensionPicked());
  }

  @Test
  void setColumnTypeWithNullInfoAppliesNotPickedRules() {
    SGSXYNetCDFMultipleData data = createPicked();
    // A null pick up info clears the picked state and dispatches to the not-picked rules,
    // which succeed because the columns name exactly one X and one Y variable.
    Map<String, String> byName =
        Map.of(
            "x", SGDataColumnTypeConstants.X_VALUE,
            "height", SGDataColumnTypeConstants.Y_VALUE);
    assertTrue(data.setColumnType(columnsFor(this.ncfile, byName), null));
    assertEquals("x", data.getXVariable().getName());
    assertEquals("height", data.getYVariable().getName());
    assertFalse(data.isDimensionPicked());
  }

  @Test
  void pickedSetColumnTypeAssignsErrorBarHolder() throws Exception {
    SGSXYNetCDFMultipleData data = createPickedWithErrorBar();
    assertTrue(data.isErrorBarAvailable());
    assertEquals("le", data.getLowerErrorVariable().getName());
    assertEquals("ue", data.getUpperErrorVariable().getName());
    assertEquals("y1", data.getErrorBarHolderVariable().getName());
    assertColumns(
        data,
        new String[] {
          SGDataColumnTypeConstants.X_VALUE,
          SGDataColumnTypeConstants.PICKUP,
          SGDataColumnTypeConstants.Y_VALUE,
          SGDataColumnTitleUtility.appendColumnTitle(
              SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "y1"),
          SGDataColumnTitleUtility.appendColumnTitle(
              SGDataColumnTypeConstants.UPPER_ERROR_VALUE, "y1")
        });
  }

  @Test
  void setPickUpDimensionInfoRoundTrips() {
    SGSXYNetCDFMultipleData data = createPicked();
    SGPickUpDimensionInfo info = data.getPickUpDimensionInfo();
    assertNotNull(info);
    assertEquals("y", ((SGNetCDFPickUpDimensionInfo) info).getDimensionName());
    // Clearing the info resets the picked state.
    assertTrue(data.setPickUpDimensionInfo(null));
    assertFalse(data.isDimensionPicked());
    // Re-applying the original info restores it.
    assertTrue(data.setPickUpDimensionInfo(info));
    assertTrue(data.isDimensionPicked());
    assertEquals(3, data.getChildNumber());
  }

  @Test
  void infoMapReflectsPickedState() {
    SGSXYNetCDFMultipleData picked = createPicked();
    Map<String, Object> map = picked.getInfoMap();
    assertEquals(Boolean.TRUE, map.get(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE));
    assertEquals(Boolean.FALSE, map.get(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE));
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES));

    Map<String, Object> plain = createBasic().getInfoMap();
    assertEquals(Boolean.TRUE, plain.get(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE));
    assertFalse(plain.containsKey(SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES));
  }

  // -----------------------------------------------------------------
  // Data viewer cells and child names
  // -----------------------------------------------------------------

  @Test
  void dataViewerCellRemapsSharedAxisToFirstChild() {
    SGSXYNetCDFMultipleData data = createBasic();
    // A non-X cell is remapped to child 0 because the X value is shared among the Y children.
    SGTwoDimensionalArrayIndex cell =
        data.getDataViewerCell(
            new SGTwoDimensionalArrayIndex(1, 2), SGDataColumnTypeConstants.X_VALUE, false);
    assertNotNull(cell);
    assertEquals(0, cell.getColumn());
    assertEquals(2, cell.getRow());
    // The Y cell is kept as is.
    SGTwoDimensionalArrayIndex yCell =
        data.getDataViewerCell(
            new SGTwoDimensionalArrayIndex(1, 2), SGDataColumnTypeConstants.Y_VALUE, false);
    assertNotNull(yCell);
    assertEquals(1, yCell.getColumn());
    assertEquals(2, yCell.getRow());
  }

  @Test
  void dataViewerColumnTypesAndRowStride() {
    SGSXYNetCDFMultipleData data = createBasic();
    String[] columnTypes = data.getDataViewerColumnTypes();
    assertEquals(2, columnTypes.length);
    assertEquals(SGDataColumnTypeConstants.X_VALUE, columnTypes[0]);
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, columnTypes[1]);
    assertNotNull(data.getDataViewerRowStride(SGDataColumnTypeConstants.X_VALUE));
    assertTrue(data.getDataViewerColumnNumber(SGDataColumnTypeConstants.X_VALUE, true) > 0);
  }

  @Test
  void valueAccessorsReadExample16() {
    SGSXYNetCDFMultipleData data = createBasic();
    assertEquals(7.0, data.getXValueAt(0, 3), 0.0);
    assertEquals(7.5, data.getYValueAt(0, 3), 0.0);
  }

  @Test
  void childNameListCoversBothModes() {
    List<String> pickedNames = createPicked().getChildNameList();
    assertEquals(3, pickedNames.size());
    // Picked names carry the dimension, the index and the coordinate value: "y[0]=0.5".
    assertEquals("y[0]=0.5", pickedNames.get(0));
    assertEquals("y[2]=2.5", pickedNames.get(2));

    List<String> plainNames = createBasic().getChildNameList();
    assertEquals(1, plainNames.size());
    assertNotNull(plainNames.get(0));
  }

  @Test
  void dimensionValueIsReadFromThePickedCoordinate() {
    SGSXYNetCDFMultipleData data = createPicked();
    assertEquals(Double.valueOf(1.5), data.getDimensionValue("y", 1));
    assertEquals(Double.valueOf(1.5), data.getDimensionValue(1));
  }

  // -----------------------------------------------------------------
  // Property I/O (picked and not picked)
  // -----------------------------------------------------------------

  @Test
  void writePropertyNotPickedWritesBracketNames() throws Exception {
    SGSXYNetCDFMultipleData data = createBasic();
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element el = doc.createElement("data");
    SGExportParameter param = new SGExportParameter(SGConstants.OPERATION.SAVE_TO_PROPERTY_FILE);
    assertTrue(data.writeProperty(el, param));
    assertEquals("{x}", el.getAttribute(SGDataPropertyKeyConstants.KEY_X_VALUE_NAMES));
    assertEquals("{height}", el.getAttribute(SGDataPropertyKeyConstants.KEY_Y_VALUE_NAMES));
    assertFalse(el.hasAttribute(SGDataPropertyKeyConstants.KEY_PICKUP_DIMENSION_NAME));
  }

  @Test
  void writePropertyPickedWritesSingleNamesAndPickUp() throws Exception {
    SGSXYNetCDFMultipleData data = createPicked();
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element el = doc.createElement("data");
    SGExportParameter param = new SGExportParameter(SGConstants.OPERATION.SAVE_TO_PROPERTY_FILE);
    assertTrue(data.writeProperty(el, param));
    assertEquals("x", el.getAttribute(SGDataPropertyKeyConstants.KEY_X_VALUE_NAME));
    assertEquals("height", el.getAttribute(SGDataPropertyKeyConstants.KEY_Y_VALUE_NAME));
    assertEquals("y", el.getAttribute(SGDataPropertyKeyConstants.KEY_PICKUP_DIMENSION_NAME));
    assertEquals(
        "0:end", el.getAttribute(SGDataPropertyKeyConstants.KEY_PICK_UP_DIMENSION_INDICES));
  }

  @Test
  void exportToFileRejectsWrongPolicyType() throws Exception {
    SGSXYNetCDFMultipleData data = createBasic();
    NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3("unused.nc");
    SGExportParameter param =
        new SGExportParameter(SGConstants.OPERATION.EXPORT_TO_FILE_AS_SAME_FORMAT);
    // A generic buffer policy is not accepted by the multiple NetCDF export.
    boolean rejected;
    try {
      rejected = !data.exportToFile(builder, param, new SGDataBufferPolicy(false, false, false));
    } catch (IllegalArgumentException e) {
      rejected = true;
    }
    assertTrue(rejected);
  }
}
