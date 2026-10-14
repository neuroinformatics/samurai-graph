package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGConstants;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGNamedStringBlock;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFiles;

/**
 * Characterization tests for the less-exercised paths of {@link SGSXYNetCDFMultipleData}: the
 * export and archive-save factories, assigned-variable and column-type lists, data copying,
 * cloning, tool-tip dimensions, array-section properties, data-viewer edits and the protected block
 * arrangement helpers.
 */
class SGSXYNetCDFMultipleDataCoverageTest {

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

  // x/height: the not-picked form with a single Y variable.
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

  // x/height with lower and upper error bars bound to the height variable.
  private SGSXYNetCDFMultipleData createWithErrorBar() {
    return new SGSXYNetCDFMultipleData(
        this.ncfile,
        new SGDataSourceObserver(),
        new SGNetCDFDataColumnInfo[] {info("x", SGDataColumnTypeConstants.X_VALUE)},
        new SGNetCDFDataColumnInfo[] {info("height", SGDataColumnTypeConstants.Y_VALUE)},
        new SGNetCDFDataColumnInfo[] {
          info(
              "le",
              SGDataColumnTitleUtility.appendColumnTitle(
                  SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "height"))
        },
        new SGNetCDFDataColumnInfo[] {
          info(
              "ue",
              SGDataColumnTitleUtility.appendColumnTitle(
                  SGDataColumnTypeConstants.UPPER_ERROR_VALUE, "height"))
        },
        new SGNetCDFDataColumnInfo[] {info("height", SGDataColumnTypeConstants.Y_VALUE)},
        null,
        null,
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

  // height/le with the coordinate "x" as the index variable: index-available multiple data.
  private SGSXYNetCDFMultipleData createIndexAvailable() {
    return new SGSXYNetCDFMultipleData(
        this.ncfile,
        new SGDataSourceObserver(),
        new SGNetCDFDataColumnInfo[] {info("height", SGDataColumnTypeConstants.X_VALUE)},
        new SGNetCDFDataColumnInfo[] {info("le", SGDataColumnTypeConstants.Y_VALUE)},
        null,
        info("x", SGDataColumnTypeConstants.INDEX),
        new SGIntegerSeriesSet(0, 7, 2),
        null,
        true);
  }

  private static SGSXYDataBufferPolicy policy() {
    return new SGSXYDataBufferPolicy(true, false, false, false, true);
  }

  private static SGExportParameter exportMode() {
    return new SGExportParameter(SGConstants.OPERATION.EXPORT_TO_NETCDF);
  }

  private static SGExportParameter archiveMode() {
    return new SGExportParameter(SGConstants.OPERATION.SAVE_TO_ARCHIVE_DATA_SET);
  }

  private static File newTempFile(String suffix) throws IOException {
    Path path = Files.createTempFile("samurai-graph-test", suffix);
    Files.delete(path);
    path.toFile().deleteOnExit();
    return path.toFile();
  }

  // -----------------------------------------------------------------
  // Export
  // -----------------------------------------------------------------

  @Test
  void saveToNetCDFFileWritesBasicData() throws Exception {
    SGSXYNetCDFMultipleData data = createBasic();
    assertTrue(data.saveToNetCDFFile(newTempFile(".nc"), exportMode(), policy()));
  }

  @Test
  void saveToNetCDFFileWritesErrorBarData() throws Exception {
    SGSXYNetCDFMultipleData data = createWithErrorBar();
    assertTrue(data.saveToNetCDFFile(newTempFile(".nc"), exportMode(), policy()));
  }

  @Test
  void saveToNetCDFFileWritesPickedData() throws Exception {
    SGSXYNetCDFMultipleData data = createPicked();
    assertTrue(data.saveToNetCDFFile(newTempFile(".nc"), exportMode(), policy()));
  }

  @Test
  void exportRejectsNonSXYPolicy() throws Exception {
    SGSXYNetCDFMultipleData data = createBasic();
    jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy other =
        new jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy(true, false, false);
    assertThrows(
        IllegalArgumentException.class,
        () -> data.saveToNetCDFFile(newTempFile(".nc"), exportMode(), other));
  }

  @Test
  void saveToArchiveDataSetFileWritesBasicData() throws Exception {
    SGSXYNetCDFMultipleData data = createBasic();
    assertTrue(data.saveToArchiveDataSetFile(newTempFile(".nc"), archiveMode()));
  }

  @Test
  void saveToArchiveDataSetFileRejectsNonArchiveOperation() throws Exception {
    SGSXYNetCDFMultipleData data = createBasic();
    assertFalse(data.saveToArchiveDataSetFile(newTempFile(".nc"), exportMode()));
  }

  // -----------------------------------------------------------------
  // Assigned variables and column-type lists
  // -----------------------------------------------------------------

  @Test
  void getAssignedVariablesForBasicData() {
    SGSXYNetCDFMultipleData data = createBasic();
    SGNetCDFVariable[] vars = data.getAssignedVariables();
    assertEquals(2, vars.length);
    assertEquals("x", vars[0].getName());
    assertEquals("height", vars[1].getName());
  }

  @Test
  void getAssignedVariablesForErrorBarData() {
    SGSXYNetCDFMultipleData data = createWithErrorBar();
    SGNetCDFVariable[] vars = data.getAssignedVariables();
    List<String> names = new ArrayList<String>();
    for (SGNetCDFVariable var : vars) {
      names.add(var.getName());
    }
    assertTrue(names.contains("x"));
    assertTrue(names.contains("height"));
    assertTrue(names.contains("le"));
    assertTrue(names.contains("ue"));
  }

  @Test
  void getAssignedVariablesForPickedData() {
    SGSXYNetCDFMultipleData data = createPicked();
    SGNetCDFVariable[] vars = data.getAssignedVariables();
    assertEquals(3, vars.length);
    // Pick-up variable comes first, then x and y.
    assertEquals("y", vars[0].getName());
    assertEquals("x", vars[1].getName());
    assertEquals("height", vars[2].getName());
  }

  @Test
  void getVarNameColumnTypeListForBasicData() {
    SGSXYNetCDFMultipleData data = createBasic();
    List<String> varList = new ArrayList<String>();
    List<String> columnTypeList = new ArrayList<String>();
    // Note: this data is not picked, so the base list (x coordinate) is followed by x/y.
    data.getVarNameColumnTypeList(varList, columnTypeList);
    assertTrue(varList.contains("x"));
    assertTrue(varList.contains("height"));
    int xIndex = varList.indexOf("x");
    int yIndex = varList.indexOf("height");
    assertEquals(SGDataColumnTypeConstants.X_VALUE, columnTypeList.get(xIndex));
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, columnTypeList.get(yIndex));
  }

  @Test
  void getVarNameColumnTypeListForErrorBarData() {
    SGSXYNetCDFMultipleData data = createWithErrorBar();
    List<String> varList = new ArrayList<String>();
    List<String> columnTypeList = new ArrayList<String>();
    data.getVarNameColumnTypeList(varList, columnTypeList);
    assertTrue(varList.contains("le"));
    assertTrue(varList.contains("ue"));
    int leIndex = varList.indexOf("le");
    assertEquals(
        SGDataColumnTitleUtility.appendColumnTitle(
            SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "height"),
        columnTypeList.get(leIndex));
  }

  @Test
  void getVarNameColumnTypeListForPickedData() {
    SGSXYNetCDFMultipleData data = createPicked();
    List<String> varList = new ArrayList<String>();
    List<String> columnTypeList = new ArrayList<String>();
    data.getVarNameColumnTypeList(varList, columnTypeList);
    int yIndex = varList.indexOf("y");
    assertEquals(SGDataColumnTypeConstants.PICKUP, columnTypeList.get(yIndex));
  }

  // -----------------------------------------------------------------
  // Data copying and cloning
  // -----------------------------------------------------------------

  @Test
  void setDataCopiesStateFromAnotherInstance() {
    SGSXYNetCDFMultipleData source = createBasic();
    SGSXYNetCDFMultipleData target = createWithErrorBar();
    assertTrue(target.setData(source));
    assertEquals(1, target.getXVariables().length);
    assertEquals("x", target.getXVariables()[0].getName());
    assertEquals("height", target.getYVariables()[0].getName());
    assertFalse(target.isErrorBarAvailable());
  }

  @Test
  void setDataCopiesPickedState() {
    SGSXYNetCDFMultipleData source = createPicked();
    SGSXYNetCDFMultipleData target = createBasic();
    assertTrue(target.setData(source));
    assertTrue(target.isDimensionPicked());
    assertEquals("y", target.getDimensionName());
    assertEquals(3, target.getChildNumber());
  }

  @Test
  void setDataRejectsWrongDataType() {
    SGSXYNetCDFMultipleData target = createBasic();
    SGData wrong = new SGSXYMDArrayMultipleData();
    assertThrows(IllegalArgumentException.class, () -> target.setData(wrong));
  }

  @Test
  void cloneCopiesState() {
    SGSXYNetCDFMultipleData original = createWithErrorBar();
    SGSXYNetCDFMultipleData copy = (SGSXYNetCDFMultipleData) original.clone();
    assertEquals(original.getXVariables().length, copy.getXVariables().length);
    assertEquals(original.getYVariables().length, copy.getYVariables().length);
    assertTrue(copy.isErrorBarAvailable());
    assertEquals("le", copy.getLowerErrorVariable().getName());
    assertEquals("ue", copy.getUpperErrorVariable().getName());
  }

  // -----------------------------------------------------------------
  // Tool-tip dimensions, array-section properties
  // -----------------------------------------------------------------

  @Test
  void toolTipNotSpatiallyVariedDimensionListExcludesCoordinateDimension() {
    SGSXYNetCDFMultipleData data = createBasic();
    List<Dimension> dims = data.getToolTipNotSpatiallyVariedDimensionList();
    // height is (x, y): the y dimension is left after removing the x coordinate dimension.
    assertNotNull(dims);
  }

  @Test
  void setArraySectionPropertySubAddsProperties() {
    SGSXYNetCDFMultipleData data =
        new SGSXYNetCDFMultipleData(
            this.ncfile,
            new SGDataSourceObserver(),
            new SGNetCDFDataColumnInfo[] {info("x", SGDataColumnTypeConstants.X_VALUE)},
            new SGNetCDFDataColumnInfo[] {info("height", SGDataColumnTypeConstants.Y_VALUE)},
            null,
            null,
            new SGIntegerSeriesSet(1, 3, 1),
            null,
            true);
    SGPropertyMap map = new SGPropertyMap();
    assertTrue(data.setArraySectionPropertySub(map));
    assertNotNull(map);
  }

  // -----------------------------------------------------------------
  // Data-viewer edits
  // -----------------------------------------------------------------

  @Test
  void setDataViewerValueRecordsEditedValue() throws Exception {
    SGSXYNetCDFMultipleData data = createIndexAvailable();
    // Editing a value on an index-available data records an edited value into the list.
    data.setDataViewerValue(SGDataColumnTypeConstants.Y_VALUE, 0, 0, "100.0");
    // The archive save reflects the recorded edit through setEditedValues.
    assertTrue(data.saveToArchiveDataSetFile(newTempFile(".nc"), archiveMode()));
  }

  @Test
  void archiveSaveReflectsEditedValue() throws Exception {
    SGSXYNetCDFMultipleData data = createIndexAvailable();
    data.setDataViewerValue(SGDataColumnTypeConstants.Y_VALUE, 0, 0, "0.0");
    File out = newTempFile(".nc");
    assertTrue(data.saveToArchiveDataSetFile(out, archiveMode()));
  }

  @Test
  void addSingleDimensionEditedDataValueAcceptsD1() {
    SGSXYNetCDFMultipleData data = createIndexAvailable();
    SGDataValueHistory.NetCDF.D1 d1 =
        new SGDataValueHistory.NetCDF.D1(1.5, SGDataColumnTypeConstants.Y_VALUE, 0, 0, "le");
    d1.setIndexDimName("x");
    // The method converts the 1-D edited value to the multiple form.
    data.addSingleDimensionEditedDataValue(d1);
  }

  // -----------------------------------------------------------------
  // Protected block arrangement helpers
  // -----------------------------------------------------------------

  @Test
  void arrangeValueArrayConcatenatesBlocks() throws Exception {
    SGSXYNetCDFMultipleData data = createBasic();
    Map<String, SGIntegerSeries> seriesMap = new HashMap<String, SGIntegerSeries>();
    seriesMap.put("x", new SGIntegerSeries(0, 0, 1));
    List<SGNamedDoubleValueIndexBlock> blockList = new ArrayList<SGNamedDoubleValueIndexBlock>();
    blockList.add(new SGNamedDoubleValueIndexBlock(new double[] {1.0}, seriesMap));
    blockList.add(new SGNamedDoubleValueIndexBlock(new double[] {3.0}, seriesMap));
    SGNetCDFVariable var = this.ncfile.findVariable("height");
    double[] values = data.arrangeValueArray(var, blockList);
    assertArrayEquals(new double[] {1.0, 3.0}, values, 0.0);
  }

  @Test
  void arrangeStringArrayConcatenatesBlocks() {
    SGSXYNetCDFMultipleData data = createBasic();
    Map<String, SGIntegerSeries> seriesMap = new HashMap<String, SGIntegerSeries>();
    seriesMap.put("x", new SGIntegerSeries(0, 0, 1));
    List<SGNamedStringBlock> blockList = new ArrayList<SGNamedStringBlock>();
    blockList.add(new SGNamedStringBlock(new String[] {"a"}, seriesMap));
    blockList.add(new SGNamedStringBlock(new String[] {"c"}, seriesMap));
    String[] values = data.arrangeStringArray(blockList);
    assertArrayEquals(new String[] {"a", "c"}, values);
  }

  // -----------------------------------------------------------------
  // Properties
  // -----------------------------------------------------------------

  @Test
  void callSuperPropertyHelpersDelegate() throws Exception {
    SGSXYNetCDFMultipleData data = createBasic();
    SGSXYNetCDFMultipleData.SXYNetCDFMultipleDataProperties p =
        new SGSXYNetCDFMultipleData.SXYNetCDFMultipleDataProperties();
    assertTrue(data.callSuperGetProperties(p));
    assertTrue(data.callSuperSetProperties(p));
    Document document =
        javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element el = document.createElement("properties");
    assertTrue(data.callSuperWriteProperty(el, exportMode()));
  }
}
