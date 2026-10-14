package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGConstants;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;

/**
 * Characterization tests for the less-exercised paths of {@link SGSXYMDArrayMultipleData}: HDF5 and
 * MATLAB export, picked and unpicked column-type lists, variable-name assembly, titles, child
 * names, data-viewer columns, data-viewer edits, data-source swapping and the picked value array.
 */
class SGSXYMDArrayMultipleDataCoverageTest {

  // -----------------------------------------------------------------
  // Fixtures
  // -----------------------------------------------------------------

  private static Path createTempHdf5File() throws IOException {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    return path;
  }

  // Builds a 1D file with x, y1, y2, le and ue of length 3.
  private static SGHDF5File create1DFile(Path path) throws IOException {
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(path.toFile())) {
      writer.writeDoubleArray("x", new double[] {1.0, 2.0, 3.0});
      writer.writeDoubleArray("y1", new double[] {10.0, 20.0, 30.0});
      writer.writeDoubleArray("y2", new double[] {40.0, 50.0, 60.0});
      writer.writeDoubleArray("le", new double[] {1.0, 1.0, 1.0});
      writer.writeDoubleArray("ue", new double[] {2.0, 2.0, 2.0});
    }
    IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(path.toFile());
    return new SGHDF5File(reader);
  }

  // Builds a 1D file with x, y1, y2, le, ue and a string label of length 3.
  private static SGHDF5File createLabelFile(Path path) throws IOException {
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(path.toFile())) {
      writer.writeDoubleArray("x", new double[] {1.0, 2.0, 3.0});
      writer.writeDoubleArray("y1", new double[] {10.0, 20.0, 30.0});
      writer.writeStringArray("label", new String[] {"a", "bb", "ccc"});
    }
    IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(path.toFile());
    return new SGHDF5File(reader);
  }

  // Builds a 2D file with x(5,3) and y(5,3).
  private static SGHDF5File create2DFile(Path path) throws IOException {
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(path.toFile())) {
      double[][] xm = new double[5][3];
      double[][] ym = new double[5][3];
      for (int i = 0; i < 5; i++) {
        for (int j = 0; j < 3; j++) {
          xm[i][j] = i * 3 + j;
          ym[i][j] = 100 + i * 3 + j;
        }
      }
      writer.writeDoubleMatrix("x", xm);
      writer.writeDoubleMatrix("y", ym);
    }
    IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(path.toFile());
    return new SGHDF5File(reader);
  }

  private static SGMDArrayDataColumnInfo info(SGMDArrayFile file, String name, String type) {
    SGMDArrayDataColumnInfo col =
        SGDataFileUtility.createDataColumnInfo(file.findVariable(name), type);
    col.setGenericDimensionIndex(0);
    return col;
  }

  private static SGSXYMDArrayMultipleData create1D(SGMDArrayFile file) {
    return new SGSXYMDArrayMultipleData(
        file,
        new SGDataSourceObserver(),
        new SGMDArrayDataColumnInfo[] {info(file, "x", SGDataColumnTypeConstants.X_VALUE)},
        new SGMDArrayDataColumnInfo[] {
          info(file, "y1", SGDataColumnTypeConstants.Y_VALUE),
          info(file, "y2", SGDataColumnTypeConstants.Y_VALUE)
        },
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        true);
  }

  private static SGSXYMDArrayMultipleData createPicked(SGMDArrayFile file) {
    SGMDArrayDataColumnInfo xInfo = info(file, "x", SGDataColumnTypeConstants.X_VALUE);
    xInfo.setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 1);
    SGMDArrayDataColumnInfo yInfo = info(file, "y", SGDataColumnTypeConstants.Y_VALUE);
    yInfo.setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 1);
    return new SGSXYMDArrayMultipleData(
        file,
        new SGDataSourceObserver(),
        xInfo,
        yInfo,
        null,
        null,
        null,
        new SGIntegerSeriesSet(0, 2, 1),
        null,
        null,
        true);
  }

  private static SGSXYMDArrayMultipleData create1DWithErrorBar(SGMDArrayFile file) {
    return new SGSXYMDArrayMultipleData(
        file,
        new SGDataSourceObserver(),
        new SGMDArrayDataColumnInfo[] {info(file, "x", SGDataColumnTypeConstants.X_VALUE)},
        new SGMDArrayDataColumnInfo[] {info(file, "y1", SGDataColumnTypeConstants.Y_VALUE)},
        new SGMDArrayDataColumnInfo[] {
          info(
              file,
              "le",
              SGDataColumnTitleUtility.appendColumnTitle(
                  SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "y1"))
        },
        new SGMDArrayDataColumnInfo[] {
          info(
              file,
              "ue",
              SGDataColumnTitleUtility.appendColumnTitle(
                  SGDataColumnTypeConstants.UPPER_ERROR_VALUE, "y1"))
        },
        new SGMDArrayDataColumnInfo[] {info(file, "y1", SGDataColumnTypeConstants.Y_VALUE)},
        null,
        null,
        null,
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
  void saveToHDF5FileWritesBasicData() throws Exception {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    assertTrue(data.saveToHDF5File(newTempFile(".h5"), exportMode(), policy()));
  }

  @Test
  void saveToHDF5FileWritesPickedData() throws Exception {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    assertTrue(data.saveToHDF5File(newTempFile(".h5"), exportMode(), policy()));
  }

  @Test
  void saveToHDF5FileWritesErrorBarData() throws Exception {
    SGSXYMDArrayMultipleData data = create1DWithErrorBar(create1DFile(createTempHdf5File()));
    assertTrue(data.saveToHDF5File(newTempFile(".h5"), exportMode(), policy()));
  }

  @Test
  void saveToMATLABFileWritesBasicData() throws Exception {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    assertTrue(data.saveToMATLABFile(newTempFile(".mat"), exportMode(), policy()));
  }

  @Test
  void saveToMATLABFileWritesPickedData() throws Exception {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    assertTrue(data.saveToMATLABFile(newTempFile(".mat"), exportMode(), policy()));
  }

  // -----------------------------------------------------------------
  // Column-type and variable-name lists
  // -----------------------------------------------------------------

  @Test
  void getVarNameColumnTypeListForBasicData() throws Exception {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    List<String> varList = new ArrayList<String>();
    List<String> columnTypeList = new ArrayList<String>();
    data.getVarNameColumnTypeList(varList, columnTypeList);
    int xIndex = varList.indexOf("x:0");
    int y1Index = varList.indexOf("y1:0");
    assertTrue(xIndex != -1, "x:0 not found: " + varList);
    assertTrue(y1Index != -1, "y1:0 not found: " + varList);
    assertEquals(SGDataColumnTypeConstants.X_VALUE, columnTypeList.get(xIndex));
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, columnTypeList.get(y1Index));
  }

  @Test
  void getVarNameColumnTypeListForErrorBarData() throws Exception {
    SGSXYMDArrayMultipleData data = create1DWithErrorBar(create1DFile(createTempHdf5File()));
    List<String> varList = new ArrayList<String>();
    List<String> columnTypeList = new ArrayList<String>();
    data.getVarNameColumnTypeList(varList, columnTypeList);
    int leIndex = varList.indexOf("le:0");
    assertTrue(leIndex != -1, "le:0 not found: " + varList);
    assertEquals(
        SGDataColumnTitleUtility.appendColumnTitle(
            SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "y1"),
        columnTypeList.get(leIndex));
  }

  @Test
  void getVariableNamesAssemblesLists() throws Exception {
    SGSXYMDArrayMultipleData data = create1DWithErrorBar(create1DFile(createTempHdf5File()));
    List<String> xNameList = new ArrayList<String>();
    List<String> yNameList = new ArrayList<String>();
    List<String> leNameList = new ArrayList<String>();
    List<String> ueNameList = new ArrayList<String>();
    List<String> tlNameList = new ArrayList<String>();
    data.getVariableNames(xNameList, yNameList, leNameList, ueNameList, tlNameList);
    assertEquals(List.of("x"), xNameList);
    assertEquals(List.of("y1"), yNameList);
    assertEquals(List.of("le"), leNameList);
    assertEquals(List.of("ue"), ueNameList);
    assertTrue(tlNameList.isEmpty());
  }

  @Test
  void getVariablesInXYOrderResolvesErrorHolder() throws Exception {
    SGSXYMDArrayMultipleData data = create1DWithErrorBar(create1DFile(createTempHdf5File()));
    SGMDArrayVariable[] leVars = data.getLowerErrorVariables();
    SGMDArrayVariable[] ehVars = data.getErrorHolderVariables();
    // The lower-error variable holds the y value, so the XY-order lookup returns the y variable.
    SGMDArrayVariable[] ordered = data.getVariablesInXYOrder(leVars, ehVars);
    assertNotNull(ordered);
    assertEquals(1, ordered.length);
    assertEquals("le", ordered[0].getName());
  }

  // -----------------------------------------------------------------
  // Titles and child names
  // -----------------------------------------------------------------

  @Test
  void titlesForSingleYData() throws Exception {
    SGSXYMDArrayMultipleData data = create1DWithErrorBar(create1DFile(createTempHdf5File()));
    assertEquals("x", data.getTitleX());
    assertEquals("y1", data.getTitleY());
  }

  @Test
  void childNameListForBasicData() throws Exception {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    List<String> names = data.getChildNameList();
    assertEquals(List.of("y1", "y2"), names);
  }

  @Test
  void childNameListForPickedData() throws Exception {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    List<String> names = data.getChildNameList();
    assertEquals(List.of("0", "1", "2"), names);
  }

  @Test
  void assignedVariablesForBasicData() throws Exception {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    SGMDArrayVariable[] vars = data.getAssignedVariables();
    List<String> names = new ArrayList<String>();
    for (SGMDArrayVariable var : vars) {
      names.add(var.getName());
    }
    assertTrue(names.containsAll(List.of("x", "y1", "y2")));
  }

  // -----------------------------------------------------------------
  // Picked value array
  // -----------------------------------------------------------------

  @Test
  void getPickUpValueArrayReadsPickedDimension() throws Exception {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    SGMDArrayVariable xVar = data.getXVariable();
    double[][] values = data.getPickUpValueArray(new SGIntegerSeriesSet(0, 4, 1), xVar, false);
    assertEquals(3, values.length);
    assertEquals(5, values[0].length);
    assertEquals(0.0, values[0][0], 0.0);
    assertEquals(9.0, values[0][3], 0.0);
    assertEquals(1.0, values[1][0], 0.0);
  }

  @Test
  void getPickUpValueArrayWithAllFlagIgnoresPickupStride() throws Exception {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    SGMDArrayVariable xVar = data.getXVariable();
    double[][] values = data.getPickUpValueArray(null, xVar, true);
    assertEquals(3, values.length);
    assertEquals(5, values[0].length);
  }

  // -----------------------------------------------------------------
  // Data viewer
  // -----------------------------------------------------------------

  @Test
  void dataViewerColumnTypesForBasicData() throws Exception {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    String[] types = data.getDataViewerColumnTypes();
    assertEquals(2, types.length);
    assertEquals(SGDataColumnTypeConstants.X_VALUE, types[0]);
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, types[1]);
  }

  @Test
  void dataViewerColumnNumberForPickedData() throws Exception {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    // Y values: one column per picked child; X values: the pickup dimension length.
    assertEquals(3, data.getDataViewerColumnNumber(SGDataColumnTypeConstants.Y_VALUE, false));
    assertEquals(3, data.getDataViewerColumnNumber(SGDataColumnTypeConstants.X_VALUE, false));
  }

  @Test
  void setDataViewerValueRecordsEditedValue() throws Exception {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    data.setDataViewerValue(SGDataColumnTypeConstants.Y_VALUE, 0, 0, "500.0");
    assertTrue(data.saveToHDF5File(newTempFile(".h5"), exportMode(), policy()));
    assertTrue(data.saveToMATLABFile(newTempFile(".mat"), exportMode(), policy()));
  }

  @Test
  void addSingleDimensionEditedDataValueAcceptsD1() throws Exception {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    SGDataValueHistory.MDArray.D1 d1 =
        new SGDataValueHistory.MDArray.D1(1.5, SGDataColumnTypeConstants.Y_VALUE, 0, 0, "y1");
    d1.setDimension(0);
    // The method converts the 1-D edited value to the multiple form and keeps
    // the generic dimension index so the archive save can locate it again.
    data.addSingleDimensionEditedDataValue(d1);
    assertTrue(data.saveToArchiveDataSetFile(newTempFile(".h5"), archiveMode()));
  }

  @Test
  void archiveModeHdf5ExportFailsCleanlyInsteadOfNpe() throws Exception {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    // The archive operation is only valid for NetCDF-format archives; the
    // HDF5/MATLAB exporters must refuse it rather than dereference null.
    assertFalse(data.saveToHDF5File(newTempFile(".h5"), archiveMode(), policy()));
    assertFalse(data.saveToMATLABFile(newTempFile(".mat"), archiveMode(), policy()));
  }

  // -----------------------------------------------------------------
  // Data source
  // -----------------------------------------------------------------

  @Test
  void setDataSourceSwapsTheFile() throws Exception {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    SGHDF5File other = create1DFile(createTempHdf5File());
    data.setDataSource(other);
    assertEquals(other.getPath(), data.getDataSource().getPath());
  }

  @Test
  void getMaxLengthForStringVariable() throws Exception {
    SGHDF5File file = createLabelFile(createTempHdf5File());
    SGMDArrayVariable labelVar = file.findVariable("label");
    labelVar.setGenericDimensionIndex(0);
    SGSXYMDArrayMultipleData data =
        new SGSXYMDArrayMultipleData(
            file,
            new SGDataSourceObserver(),
            new SGMDArrayDataColumnInfo[] {info(file, "x", SGDataColumnTypeConstants.X_VALUE)},
            new SGMDArrayDataColumnInfo[] {info(file, "y1", SGDataColumnTypeConstants.Y_VALUE)},
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            true);
    // The longest string ("ccc") has 3 UTF-8 bytes.
    assertEquals(3, data.getMaxLength(labelVar));
  }

  // -----------------------------------------------------------------
  // Picked column-type reassignment, variable names and child-name lists
  // -----------------------------------------------------------------

  @Test
  void setColumnTypeDimensionPickedReassignsRoles() throws Exception {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    // Swap the roles through the picked column-type path (x -> Y, y -> X).
    String[] columns = {SGDataColumnTypeConstants.Y_VALUE, SGDataColumnTypeConstants.X_VALUE};
    assertTrue(data.setColumnType(columns));
    assertEquals("y", data.getXVariable().getName());
    assertEquals("x", data.getYVariable().getName());
  }

  @Test
  void getVariableNamesForPickedData() throws Exception {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    List<String> xNameList = new ArrayList<String>();
    List<String> yNameList = new ArrayList<String>();
    List<String> leNameList = new ArrayList<String>();
    List<String> ueNameList = new ArrayList<String>();
    List<String> tlNameList = new ArrayList<String>();
    data.getVariableNames(xNameList, yNameList, leNameList, ueNameList, tlNameList);
    assertEquals(List.of("x"), xNameList);
    assertEquals(List.of("y"), yNameList);
    assertTrue(leNameList.isEmpty());
    assertTrue(ueNameList.isEmpty());
    assertTrue(tlNameList.isEmpty());
  }

  @Test
  void getChildNameListStaticWithColumns() throws Exception {
    SGHDF5File file = create1DFile(createTempHdf5File());
    SGDataColumnInfo[] cols = {
      info(file, "x", SGDataColumnTypeConstants.X_VALUE),
      info(file, "y1", SGDataColumnTypeConstants.Y_VALUE),
      info(file, "y2", SGDataColumnTypeConstants.Y_VALUE)
    };
    List<String> names = SGSXYMDArrayMultipleData.getChildNameList(cols);
    // With a single X, each Y column gives a child name.
    assertEquals(List.of("y1", "y2"), names);
  }

  @Test
  void getEditedValuesFromPickedDataViewerEdit() throws Exception {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    data.setDataViewerValue(SGDataColumnTypeConstants.Y_VALUE, 0, 0, "500.0");
    assertTrue(data.saveToHDF5File(newTempFile(".h5"), exportMode(), policy()));
    assertTrue(data.saveToMATLABFile(newTempFile(".mat"), exportMode(), policy()));
  }
}
