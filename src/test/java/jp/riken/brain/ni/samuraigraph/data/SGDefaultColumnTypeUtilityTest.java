package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeUtility.DefaultColumnTypeResult;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ucar.nc2.NetcdfFiles;

/** Unit tests for {@link SGDefaultColumnTypeUtility}. */
class SGDefaultColumnTypeUtilityTest {

  private static List<SGDataColumnInfo> createColumns(
      final String[] titles, final String valueType) {
    List<SGDataColumnInfo> list = new ArrayList<SGDataColumnInfo>();
    for (String title : titles) {
      list.add(new SGSDArrayDataColumnInfo(title, valueType));
    }
    return list;
  }

  private static Map<String, Object> createInfoMap(final boolean multiple) {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.valueOf(multiple));
    return infoMap;
  }

  @Test
  void testGetDefaultColumnTypesForSXYSDArray() {
    List<SGDataColumnInfo> columns =
        createColumns(new String[] {"x", "y"}, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    Map<String, Object> infoMap = createInfoMap(false);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    assertArrayEquals(
        new String[] {SGIDataColumnTypeConstants.X_VALUE, SGIDataColumnTypeConstants.Y_VALUE},
        result.getDefaultColumnTypes());
  }

  @Test
  void testGetDefaultColumnTypesForSXYSDArrayWithErrorBar() {
    List<SGDataColumnInfo> columns =
        createColumns(
            new String[] {"x", "y", "lower", "upper"},
            SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    Map<String, Object> infoMap = createInfoMap(false);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    String[] types = result.getDefaultColumnTypes();
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, types[0]);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, types[1]);
    assertTrue(types[2].startsWith(SGIDataColumnTypeConstants.LOWER_ERROR_VALUE));
    assertTrue(types[3].startsWith(SGIDataColumnTypeConstants.UPPER_ERROR_VALUE));
  }

  @Test
  void testGetDefaultColumnTypesForMultipleSXYSDArray() {
    List<SGDataColumnInfo> columns =
        createColumns(new String[] {"t", "y1", "y2"}, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    Map<String, Object> infoMap = createInfoMap(true);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_MULTIPLE_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    String[] types = result.getDefaultColumnTypes();
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, types[0]);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, types[1]);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, types[2]);
  }

  @Test
  void testGetDefaultColumnTypesFailsForInsufficientColumns() {
    List<SGDataColumnInfo> columns =
        createColumns(new String[] {"a"}, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    Map<String, Object> infoMap = createInfoMap(false);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_DATA, columns, infoMap);
    assertFalse(result.isSucceeded());
  }

  @Test
  void testGetDefaultColumnTypesRejectsNullInput() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SGDefaultColumnTypeUtility.getDefaultColumnTypes(null, null, null));
  }

  @Test
  void testGetDefaultColumnTypesForSXYNetCDF() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    for (SGNetCDFVariable var : file.getVariables()) {
      columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_NETCDF_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    String[] types = result.getDefaultColumnTypes();
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, types[findVariableIndex(file, "x")]);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, types[findVariableIndex(file, "height")]);
  }

  @Test
  void testGetDefaultColumnTypesForVXYSDArray() {
    List<SGDataColumnInfo> columns =
        createColumns(
            new String[] {"x", "y", "u", "v"}, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.VXY_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    assertArrayEquals(
        new String[] {
          SGIDataColumnTypeConstants.X_COORDINATE,
          SGIDataColumnTypeConstants.Y_COORDINATE,
          SGIDataColumnTypeConstants.X_COMPONENT,
          SGIDataColumnTypeConstants.Y_COMPONENT
        },
        result.getDefaultColumnTypes());
  }

  @Test
  void testGetDefaultColumnTypesForSXYZSDArray() {
    List<SGDataColumnInfo> columns =
        createColumns(new String[] {"x", "y", "z"}, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXYZ_DATA, columns, createInfoMap(false));
    assertTrue(result.isSucceeded());
    assertArrayEquals(
        new String[] {
          SGIDataColumnTypeConstants.X_VALUE,
          SGIDataColumnTypeConstants.Y_VALUE,
          SGIDataColumnTypeConstants.Z_VALUE
        },
        result.getDefaultColumnTypes());
  }

  @Test
  void testGetDefaultColumnTypesForSXYZNetCDF() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example15.nc"));
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    for (SGNetCDFVariable var : file.getVariables()) {
      columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXYZ_NETCDF_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    String[] types = result.getDefaultColumnTypes();
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, types[findVariableIndex(file, "x")]);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, types[findVariableIndex(file, "y")]);
    assertEquals(SGIDataColumnTypeConstants.Z_VALUE, types[findVariableIndex(file, "height")]);
    assertEquals(
        SGIDataColumnTypeConstants.ANIMATION_FRAME, types[findVariableIndex(file, "time")]);
  }

  @Test
  void testGetDefaultColumnTypesForSXYZMATLAB() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble data = new MLDouble("z", new double[][] {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(data));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    for (SGMDArrayVariable var : matFile.getVariables()) {
      columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, matFile);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXYZ_MATLAB_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    assertEquals(SGIDataColumnTypeConstants.Z_VALUE, result.getDefaultColumnTypes()[0]);
  }

  @Test
  void testGetDefaultColumnTypesForVXYMATLAB() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble u = new MLDouble("u", new double[][] {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}});
    MLDouble v = new MLDouble("v", new double[][] {{7.0, 8.0}, {9.0, 10.0}, {11.0, 12.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(u, v));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    for (SGMDArrayVariable var : matFile.getVariables()) {
      columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, matFile);
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.VXY_MATLAB_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    assertEquals(SGIDataColumnTypeConstants.X_COMPONENT, result.getDefaultColumnTypes()[0]);
    assertEquals(SGIDataColumnTypeConstants.Y_COMPONENT, result.getDefaultColumnTypes()[1]);
  }

  @Test
  void testGetDefaultColumnTypesForSXYNetCDFWithSubsetOfVariables() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    columns.add(SGDataFileUtility.createDataColumnInfo(file.findVariable("height"), ""));
    columns.add(SGDataFileUtility.createDataColumnInfo(file.findVariable("x"), ""));
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_NETCDF_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, result.getDefaultColumnTypes()[0]);
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, result.getDefaultColumnTypes()[1]);
  }

  @Test
  void testGetDefaultColumnTypesForSXYNetCDFMissingYVariable() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    columns.add(SGDataFileUtility.createDataColumnInfo(file.findVariable("x"), ""));
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_NETCDF_DATA, columns, infoMap);
    assertFalse(result.isSucceeded());
  }

  @Test
  void testGetDefaultColumnTypesForSXYNetCDFWithIndexVariable() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    ucar.nc2.write.NetcdfFormatWriter.Builder writer =
        ucar.nc2.write.NetcdfFormatWriter.createNewNetcdf3(path.toString());
    writer.addDimension("Index", 5);
    writer.addVariable("Index", ucar.ma2.DataType.INT, "Index");
    writer.addVariable("x", ucar.ma2.DataType.FLOAT, "Index");
    writer.addVariable("height", ucar.ma2.DataType.FLOAT, "Index");
    try (ucar.nc2.write.NetcdfFormatWriter ignored = writer.build()) {}
    SGNetCDFFile file = new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path.toString()));
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    for (SGNetCDFVariable var : file.getVariables()) {
      columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_NETCDF_DATA, columns, infoMap);
    assertTrue(result.isSucceeded(), result.toString());
    String[] types = result.getDefaultColumnTypes();
    assertEquals(SGIDataColumnTypeConstants.INDEX, types[findVariableIndex(file, "Index")]);
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, types[findVariableIndex(file, "x")]);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, types[findVariableIndex(file, "height")]);
  }

  @Test
  void testGetDefaultColumnTypesForMultipleSXYHDF5() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    try (com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer writer =
        com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider.get()
            .open(path.toFile())) {
      writer.writeDoubleMatrix("a", new double[][] {{1.0}, {2.0}, {3.0}});
      writer.writeDoubleMatrix("b", new double[][] {{4.0}, {5.0}, {6.0}});
    }
    try (com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader reader =
        com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider.get()
            .openForReading(path.toFile())) {
      SGHDF5File file = new SGHDF5File(reader);
      List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
      for (SGMDArrayVariable var : file.getVariables()) {
        columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
      }
      Map<String, Object> infoMap = new HashMap<String, Object>();
      infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
      infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.TRUE);
      DefaultColumnTypeResult result =
          SGDefaultColumnTypeUtility.getDefaultColumnTypes(
              SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA, columns, infoMap);
      assertTrue(result.isSucceeded(), result.toString());
      String[] types = result.getDefaultColumnTypes();
      for (String type : types) {
        assertEquals(SGIDataColumnTypeConstants.Y_VALUE, type);
      }
    }
  }

  private static SGNetCDFFile createNetCDFFile(final Path path) throws Exception {
    Files.delete(path);
    path.toFile().deleteOnExit();
    ucar.nc2.write.NetcdfFormatWriter.Builder writer =
        ucar.nc2.write.NetcdfFormatWriter.createNewNetcdf3(path.toString());
    ucar.nc2.Dimension xDim = writer.addDimension("x", 5);
    ucar.nc2.Dimension yDim = writer.addDimension("y", 4);
    writer.addVariable("x", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(xDim));
    writer.addVariable("y", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(yDim));
    String[] varNames = {"v1", "v2", "v3", "v4"};
    for (String var : varNames) {
      writer.addVariable(var, ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(yDim, xDim));
    }
    try (ucar.nc2.write.NetcdfFormatWriter ignored = writer.build()) {}
    return new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path.toString()));
  }

  private static SGNetCDFFile createNetCDFFileWithIndex(final Path path) throws Exception {
    Files.delete(path);
    path.toFile().deleteOnExit();
    ucar.nc2.write.NetcdfFormatWriter.Builder writer =
        ucar.nc2.write.NetcdfFormatWriter.createNewNetcdf3(path.toString());
    writer.addDimension("Index", 5);
    writer.addVariable("Index", ucar.ma2.DataType.INT, "Index");
    String[] vars = {"a", "b", "c", "d"};
    for (String var : vars) {
      writer.addVariable(var, ucar.ma2.DataType.FLOAT, "Index");
    }
    try (ucar.nc2.write.NetcdfFormatWriter ignored = writer.build()) {}
    return new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path.toString()));
  }

  @Test
  void testGetDefaultColumnTypesForVXYNetCDF() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    SGNetCDFFile file = createNetCDFFile(path);
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    for (SGNetCDFVariable var : file.getVariables()) {
      if (var.getName().equals("Index")) {
        continue;
      }
      columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.VXY_NETCDF_DATA, columns, infoMap);
    assertTrue(result.isSucceeded(), result.toString());
    String[] types = result.getDefaultColumnTypes();
    assertEquals(SGIDataColumnTypeConstants.X_COORDINATE, types[findVariableIndex(file, "x")]);
    assertEquals(SGIDataColumnTypeConstants.Y_COORDINATE, types[findVariableIndex(file, "y")]);
    assertEquals(SGIDataColumnTypeConstants.X_COMPONENT, types[findVariableIndex(file, "v1")]);
    assertEquals(SGIDataColumnTypeConstants.Y_COMPONENT, types[findVariableIndex(file, "v2")]);
  }

  @Test
  void testGetDefaultColumnTypesForVXYNetCDFWithIndexVariable() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    SGNetCDFFile file = createNetCDFFileWithIndex(path);
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    for (SGNetCDFVariable var : file.getVariables()) {
      columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.VXY_NETCDF_DATA, columns, infoMap);
    assertTrue(result.isSucceeded(), result.toString());
    String[] types = result.getDefaultColumnTypes();
    assertEquals(SGIDataColumnTypeConstants.INDEX, types[findVariableIndex(file, "Index")]);
    assertEquals(SGIDataColumnTypeConstants.X_COORDINATE, types[findVariableIndex(file, "a")]);
    assertEquals(SGIDataColumnTypeConstants.Y_COORDINATE, types[findVariableIndex(file, "b")]);
    assertEquals(SGIDataColumnTypeConstants.X_COMPONENT, types[findVariableIndex(file, "c")]);
    assertEquals(SGIDataColumnTypeConstants.Y_COMPONENT, types[findVariableIndex(file, "d")]);
  }

  @Test
  void testGetDefaultColumnTypesForSXYZNetCDFWithIndexVariable() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    SGNetCDFFile file = createNetCDFFileWithIndex(path);
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    for (SGNetCDFVariable var : file.getVariables()) {
      columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXYZ_NETCDF_DATA, columns, infoMap);
    assertTrue(result.isSucceeded(), result.toString());
    String[] types = result.getDefaultColumnTypes();
    assertEquals(SGIDataColumnTypeConstants.INDEX, types[findVariableIndex(file, "Index")]);
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, types[findVariableIndex(file, "a")]);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, types[findVariableIndex(file, "b")]);
    assertEquals(SGIDataColumnTypeConstants.Z_VALUE, types[findVariableIndex(file, "c")]);
  }

  @Test
  void testGetDefaultColumnTypesForVXYHDF5() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    try (com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer writer =
        com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider.get()
            .open(path.toFile())) {
      writer.writeDoubleMatrix("f", new double[][] {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}});
      writer.writeDoubleMatrix("s", new double[][] {{7.0, 8.0}, {9.0, 10.0}, {11.0, 12.0}});
    }
    try (com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader reader =
        com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider.get()
            .openForReading(path.toFile())) {
      SGHDF5File file = new SGHDF5File(reader);
      List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
      for (SGMDArrayVariable var : file.getVariables()) {
        columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
      }
      Map<String, Object> infoMap = new HashMap<String, Object>();
      infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
      infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
      DefaultColumnTypeResult result =
          SGDefaultColumnTypeUtility.getDefaultColumnTypes(
              SGDataTypeConstants.VXY_HDF5_DATA, columns, infoMap);
      assertTrue(result.isSucceeded(), result.toString());
      assertEquals(SGIDataColumnTypeConstants.X_COMPONENT, result.getDefaultColumnTypes()[0]);
      assertEquals(SGIDataColumnTypeConstants.Y_COMPONENT, result.getDefaultColumnTypes()[1]);
    }
  }

  @Test
  void testGetDefaultColumnTypesForSXYZHDF5() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    try (com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer writer =
        com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider.get()
            .open(path.toFile())) {
      writer.writeDoubleMatrix("z", new double[][] {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}});
    }
    try (com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader reader =
        com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider.get()
            .openForReading(path.toFile())) {
      SGHDF5File file = new SGHDF5File(reader);
      List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
      for (SGMDArrayVariable var : file.getVariables()) {
        columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
      }
      Map<String, Object> infoMap = new HashMap<String, Object>();
      infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
      DefaultColumnTypeResult result =
          SGDefaultColumnTypeUtility.getDefaultColumnTypes(
              SGDataTypeConstants.SXYZ_HDF5_DATA, columns, infoMap);
      assertTrue(result.isSucceeded(), result.toString());
      assertEquals(SGIDataColumnTypeConstants.Z_VALUE, result.getDefaultColumnTypes()[0]);
    }
  }

  @Test
  void testGetDefaultColumnTypesForVXYSDArrayPolar() {
    List<SGDataColumnInfo> columns =
        createColumns(
            new String[] {"x", "y", "r", "a"}, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.VXY_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    assertArrayEquals(
        new String[] {
          SGIDataColumnTypeConstants.X_COORDINATE,
          SGIDataColumnTypeConstants.Y_COORDINATE,
          SGIDataColumnTypeConstants.MAGNITUDE,
          SGIDataColumnTypeConstants.ANGLE
        },
        result.getDefaultColumnTypes());
  }

  @Test
  void testGetDefaultColumnTypesForSXYSDArrayWithSamplingRate() {
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    columns.add(
        new SGSDArrayDataColumnInfo("t", SGIDataColumnTypeConstants.VALUE_TYPE_SAMPLING_RATE));
    columns.add(new SGSDArrayDataColumnInfo("y", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SAMPLING_RATE, Double.valueOf(0.1));
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_DATA, columns, infoMap);
    assertTrue(result.isSucceeded(), result.toString());
    assertArrayEquals(
        new String[] {SGIDataColumnTypeConstants.X_VALUE, SGIDataColumnTypeConstants.Y_VALUE},
        result.getDefaultColumnTypes());
  }

  @Test
  void testGetDefaultColumnTypesForVXYNetCDFPolar() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    SGNetCDFFile file = createNetCDFFile(path);
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    for (SGNetCDFVariable var : file.getVariables()) {
      columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.VXY_NETCDF_DATA, columns, infoMap);
    assertTrue(result.isSucceeded(), result.toString());
    String[] types = result.getDefaultColumnTypes();
    assertEquals(SGIDataColumnTypeConstants.MAGNITUDE, types[findVariableIndex(file, "v1")]);
    assertEquals(SGIDataColumnTypeConstants.ANGLE, types[findVariableIndex(file, "v2")]);
  }

  @Test
  void testGetDefaultColumnTypesForSXYNetCDFMultipleVariable() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    ucar.nc2.write.NetcdfFormatWriter.Builder writer =
        ucar.nc2.write.NetcdfFormatWriter.createNewNetcdf3(path.toString());
    ucar.nc2.Dimension xDim = writer.addDimension("x", 5);
    writer.addVariable("x", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(xDim));
    writer.addVariable("y1", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(xDim));
    writer.addVariable("y2", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(xDim));
    try (ucar.nc2.write.NetcdfFormatWriter ignored = writer.build()) {}
    SGNetCDFFile file = new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path.toString()));
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    for (SGNetCDFVariable var : file.getVariables()) {
      columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.TRUE);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA, columns, infoMap);
    assertTrue(result.isSucceeded(), result.toString());
    String[] types = result.getDefaultColumnTypes();
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, types[findVariableIndex(file, "x")]);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, types[findVariableIndex(file, "y1")]);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, types[findVariableIndex(file, "y2")]);
  }

  @Test
  void testGetDefaultColumnTypesRejectsInvalidDataType() {
    assertThrows(
        Error.class,
        () ->
            SGDefaultColumnTypeUtility.getDefaultColumnTypes(
                "UNKNOWN_DATA_TYPE",
                createColumns(new String[] {"a"}, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER),
                createInfoMap(false)));
  }

  @Test
  void testGetDefaultColumnTypesForSXYHDF5WithoutUnitDimension() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    try (com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer writer =
        com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider.get()
            .open(path.toFile())) {
      writer.writeDoubleMatrix("z", new double[][] {{1.0, 2.0}, {3.0, 4.0}});
    }
    try (com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader reader =
        com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider.get()
            .openForReading(path.toFile())) {
      SGHDF5File file = new SGHDF5File(reader);
      List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
      for (SGMDArrayVariable var : file.getVariables()) {
        columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
      }
      Map<String, Object> infoMap = new HashMap<String, Object>();
      infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
      infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
      DefaultColumnTypeResult result =
          SGDefaultColumnTypeUtility.getDefaultColumnTypes(
              SGDataTypeConstants.SXY_HDF5_DATA, columns, infoMap);
      assertTrue(result.isSucceeded(), result.toString());
      assertEquals(SGIDataColumnTypeConstants.Y_VALUE, result.getDefaultColumnTypes()[0]);
    }
  }

  @Test
  void testGetDefaultColumnTypesForSXYHDF5SingleWithUnitDimension() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    try (com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer writer =
        com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider.get()
            .open(path.toFile())) {
      writer.writeDoubleMatrix("a", new double[][] {{1.0}, {2.0}, {3.0}});
    }
    try (com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader reader =
        com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider.get()
            .openForReading(path.toFile())) {
      SGHDF5File file = new SGHDF5File(reader);
      List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
      for (SGMDArrayVariable var : file.getVariables()) {
        columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
      }
      Map<String, Object> infoMap = new HashMap<String, Object>();
      infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);
      infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
      DefaultColumnTypeResult result =
          SGDefaultColumnTypeUtility.getDefaultColumnTypes(
              SGDataTypeConstants.SXY_HDF5_DATA, columns, infoMap);
      assertTrue(result.isSucceeded(), result.toString());
      assertEquals(SGIDataColumnTypeConstants.Y_VALUE, result.getDefaultColumnTypes()[0]);
    }
  }

  private static int findVariableIndex(final SGNetCDFFile file, final String name) {
    int index = -1;
    for (int ii = 0; ii < file.getVariables().size(); ii++) {
      if (file.getVariables().get(ii).getName().equals(name)) {
        index = ii;
        break;
      }
    }
    assertTrue(index != -1);
    return index;
  }

  @Test
  void testGetDefaultColumnTypesForMultipleSXYMATLAB() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    List<SGDataColumnInfo> columns = new ArrayList<SGDataColumnInfo>();
    for (SGMDArrayVariable var : matFile.getVariables()) {
      columns.add(SGDataFileUtility.createDataColumnInfo(var, ""));
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, matFile);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.TRUE);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    String[] types = result.getDefaultColumnTypes();
    for (String type : types) {
      assertEquals(SGIDataColumnTypeConstants.Y_VALUE, type);
    }
  }

  @Test
  void getNetCDFOriginMapParsesAttribute() {
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_ORIGIN_MAP, "machine=3,date=5");
    Map<String, Integer> map =
        SGDefaultColumnTypeNetCDFUtility.getNetCDFOriginMap(root.getAttributes());
    assertEquals(3, map.get("machine").intValue());
    assertEquals(5, map.get("date").intValue());
  }

  @Test
  void getNetCDFOriginMapReturnsEmptyOnBadValue() {
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_ORIGIN_MAP, "machine=x");
    Map<String, Integer> map =
        SGDefaultColumnTypeNetCDFUtility.getNetCDFOriginMap(root.getAttributes());
    assertEquals(0, map.size());
  }

  @Test
  void getNetCDFOriginMapReturnsEmptyWithoutAttribute() {
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Map<String, Integer> map =
        SGDefaultColumnTypeNetCDFUtility.getNetCDFOriginMap(
            doc.getDocumentElement().getAttributes());
    assertEquals(0, map.size());
  }

  private static SGNetCDFDataColumnInfo[] createNetCDFColumns(
      final SGNetCDFFile file, final String[] titles) throws IOException {
    SGNetCDFDataColumnInfo[] cols = new SGNetCDFDataColumnInfo[titles.length];
    for (int ii = 0; ii < titles.length; ii++) {
      cols[ii] =
          SGDataFileUtility.createDataColumnInfo(
              file.findVariable(titles[ii]), SGIDataColumnTypeConstants.X_VALUE);
    }
    return cols;
  }

  @Test
  void getForSXYNetCDFDataSetsXYTypes() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE);
    List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>();
    columnInfoList.add(xInfo);
    columnInfoList.add(yInfo);
    SGDataColumnInfo[] columns = {xInfo, yInfo};
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "height");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeNetCDFUtility.getForSXYNetCDFData(
            columnInfoList, infoMap, root.getAttributes(), null, columns));
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, columns[0].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, columns[1].getColumnType());
  }

  @Test
  void getForSXYNetCDFDataFailsWithoutXName() {
    List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>();
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "height");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    assertFalse(
        SGDefaultColumnTypeNetCDFUtility.getForSXYNetCDFData(
            columnInfoList, infoMap, root.getAttributes(), null, new SGDataColumnInfo[0]));
  }

  @Test
  void getForSXYNetCDFDataFailsForUnknownColumn() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    SGNetCDFDataColumnInfo heightInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE);
    List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>();
    columnInfoList.add(heightInfo);
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_X_VALUE_NAME, "missing");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "height");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    assertFalse(
        SGDefaultColumnTypeNetCDFUtility.getForSXYNetCDFData(
            columnInfoList, infoMap, root.getAttributes(), null, new SGDataColumnInfo[1]));
  }

  @Test
  void getForSXYNetCDFDataSetsErrorBarTypes() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE);
    SGNetCDFDataColumnInfo leInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("le"), SGIDataColumnTypeConstants.LOWER_ERROR_VALUE, "height");
    SGNetCDFDataColumnInfo ueInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("ue"), SGIDataColumnTypeConstants.UPPER_ERROR_VALUE, "height");
    List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>();
    columnInfoList.add(xInfo);
    columnInfoList.add(yInfo);
    columnInfoList.add(leInfo);
    columnInfoList.add(ueInfo);
    SGDataColumnInfo[] columns = {xInfo, yInfo, leInfo, ueInfo};
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "height");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_LOWER_ERROR_VALUE_NAME, "le");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_UPPER_ERROR_VALUE_NAME, "ue");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_NAME, "height");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeNetCDFUtility.getForSXYNetCDFData(
            columnInfoList, infoMap, root.getAttributes(), null, columns));
    assertTrue(columns[2].getColumnType().startsWith(SGIDataColumnTypeConstants.LOWER_ERROR_VALUE));
    assertTrue(columns[3].getColumnType().startsWith(SGIDataColumnTypeConstants.UPPER_ERROR_VALUE));
  }

  @Test
  void getForSXYNetCDFDataSetsTickLabelType() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE);
    SGNetCDFDataColumnInfo leInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("le"), SGIDataColumnTypeConstants.LOWER_ERROR_VALUE, "height");
    List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>();
    columnInfoList.add(xInfo);
    columnInfoList.add(yInfo);
    columnInfoList.add(leInfo);
    SGDataColumnInfo[] columns = {xInfo, yInfo, leInfo};
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "height");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_TICK_LABEL_NAME, "le");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_TICK_LABEL_HOLDER_NAME, "height");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeNetCDFUtility.getForSXYNetCDFData(
            columnInfoList, infoMap, root.getAttributes(), null, columns));
    assertTrue(columns[2].getColumnType().startsWith(SGIDataColumnTypeConstants.TICK_LABEL));
    assertTrue(columns[2].getColumnType().contains("height"));
  }

  @Test
  void getForSXYNetCDFDataRejectsBothMultipleXY() {
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_X_VALUE_NAMES, "x1,x2");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_VALUE_NAMES, "y1,y2");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    assertFalse(
        SGDefaultColumnTypeNetCDFUtility.getForSXYNetCDFData(
            new ArrayList<SGDataColumnInfo>(),
            infoMap,
            root.getAttributes(),
            null,
            new SGDataColumnInfo[0]));
  }

  private static List<SGNetCDFVariable> example16VariableList(final SGNetCDFFile file) {
    List<SGNetCDFVariable> varList = new ArrayList<SGNetCDFVariable>();
    varList.add(file.findVariable("x"));
    varList.add(file.findVariable("height"));
    varList.add(file.findVariable("le"));
    return varList;
  }

  private static SGNetCDFDataColumnInfo[] example16Columns(final SGNetCDFFile file) {
    return new SGNetCDFDataColumnInfo[] {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("le"), SGIDataColumnTypeConstants.LOWER_ERROR_VALUE, "height")
    };
  }

  @Test
  void getForSXYNetCDFDataIndexAssignsIndexAndXY() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    List<SGNetCDFVariable> varList = example16VariableList(file);
    SGNetCDFDataColumnInfo[] columns = example16Columns(file);
    assertTrue(
        SGDefaultColumnTypeNetCDFUtility.getForSXYNetCDFDataIndex(
            new HashMap<String, Object>(),
            varList,
            varList.size(),
            columns,
            new ArrayList<Integer>()));
    assertEquals(SGIDataColumnTypeConstants.INDEX, columns[0].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, columns[1].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, columns[2].getColumnType());
  }

  @Test
  void getForSXYNetCDFDataNormalAssignsXAndYWithMultipleVariable() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    List<SGNetCDFVariable> varList = example16VariableList(file);
    SGNetCDFDataColumnInfo[] columns = example16Columns(file);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
    assertTrue(
        SGDefaultColumnTypeNetCDFUtility.getForSXYNetCDFData(
            infoMap, varList, varList.size(), columns));
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, columns[0].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, columns[1].getColumnType());
  }

  private static SGNetCDFFile createVXYNetCDFFile() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    ucar.nc2.write.NetcdfFormatWriter.Builder writer =
        ucar.nc2.write.NetcdfFormatWriter.createNewNetcdf3(path.toString());
    ucar.nc2.Dimension xDim = writer.addDimension("x", 5);
    ucar.nc2.Dimension yDim = writer.addDimension("y", 4);
    writer.addVariable("x", ucar.ma2.DataType.FLOAT, Arrays.asList(xDim));
    writer.addVariable("y", ucar.ma2.DataType.FLOAT, Arrays.asList(yDim));
    writer.addVariable("v1", ucar.ma2.DataType.FLOAT, Arrays.asList(yDim, xDim));
    writer.addVariable("v2", ucar.ma2.DataType.FLOAT, Arrays.asList(yDim, xDim));
    try (ucar.nc2.write.NetcdfFormatWriter ignored = writer.build()) {}
    return new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path.toString()));
  }

  @Test
  void getForVXYNetCDFDataAssignsCoordinatesAndComponents() throws Exception {
    SGNetCDFFile file = createVXYNetCDFFile();
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGIDataColumnTypeConstants.X_COORDINATE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("y"), SGIDataColumnTypeConstants.Y_COORDINATE);
    SGNetCDFDataColumnInfo fInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("v1"), SGIDataColumnTypeConstants.X_COMPONENT);
    SGNetCDFDataColumnInfo sInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("v2"), SGIDataColumnTypeConstants.Y_COMPONENT);
    List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>();
    columnInfoList.add(xInfo);
    columnInfoList.add(yInfo);
    columnInfoList.add(fInfo);
    columnInfoList.add(sInfo);
    SGDataColumnInfo[] columns = {xInfo, yInfo, fInfo, sInfo};
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_X_COORDINATE_VARIABLE_NAME, "x");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_COORDINATE_VARIABLE_NAME, "y");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_FIRST_COMPONENT_VARIABLE_NAME, "v1");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_SECOND_COMPONENT_VARIABLE_NAME, "v2");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeNetCDFUtility.getForVXYNetCDFData(
            columnInfoList, infoMap, root.getAttributes(), null, columns));
    assertEquals(SGIDataColumnTypeConstants.X_COORDINATE, columns[0].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.Y_COORDINATE, columns[1].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.X_COMPONENT, columns[2].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.Y_COMPONENT, columns[3].getColumnType());
  }

  @Test
  void getForVXYNetCDFDataRejectsMissingFirstComponent() throws Exception {
    SGNetCDFFile file = createVXYNetCDFFile();
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGIDataColumnTypeConstants.X_COORDINATE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("y"), SGIDataColumnTypeConstants.Y_COORDINATE);
    List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>();
    columnInfoList.add(xInfo);
    columnInfoList.add(yInfo);
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_X_COORDINATE_VARIABLE_NAME, "x");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_COORDINATE_VARIABLE_NAME, "y");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertFalse(
        SGDefaultColumnTypeNetCDFUtility.getForVXYNetCDFData(
            columnInfoList, infoMap, root.getAttributes(), null, new SGDataColumnInfo[2]));
  }

  private static SGNetCDFFile createSXYZNetCDFFile() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    ucar.nc2.write.NetcdfFormatWriter.Builder writer =
        ucar.nc2.write.NetcdfFormatWriter.createNewNetcdf3(path.toString());
    ucar.nc2.Dimension xDim = writer.addDimension("x", 5);
    ucar.nc2.Dimension yDim = writer.addDimension("y", 4);
    ucar.nc2.Dimension zDim = writer.addDimension("z", 3);
    writer.addVariable("x", ucar.ma2.DataType.FLOAT, Arrays.asList(xDim));
    writer.addVariable("y", ucar.ma2.DataType.FLOAT, Arrays.asList(yDim));
    writer.addVariable("z", ucar.ma2.DataType.FLOAT, Arrays.asList(zDim));
    writer.addVariable("v", ucar.ma2.DataType.FLOAT, Arrays.asList(zDim, yDim, xDim));
    try (ucar.nc2.write.NetcdfFormatWriter ignored = writer.build()) {}
    return new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path.toString()));
  }

  @Test
  void getForSXYZNetCDFDataAssignsXYZTypes() throws Exception {
    SGNetCDFFile file = createSXYZNetCDFFile();
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("y"), SGIDataColumnTypeConstants.Y_VALUE);
    SGNetCDFDataColumnInfo zInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("v"), SGIDataColumnTypeConstants.Z_VALUE);
    List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>();
    columnInfoList.add(xInfo);
    columnInfoList.add(yInfo);
    columnInfoList.add(zInfo);
    SGDataColumnInfo[] columns = {xInfo, yInfo, zInfo};
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "y");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Z_VALUE_NAME, "v");
    assertTrue(
        SGDefaultColumnTypeNetCDFUtility.getForSXYZNetCDFData(
            columnInfoList, new HashMap<String, Object>(), root.getAttributes(), null, columns));
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, columns[0].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, columns[1].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.Z_VALUE, columns[2].getColumnType());
  }

  @Test
  void getForSXYZNetCDFDataRejectsMissingZ() throws Exception {
    SGNetCDFFile file = createSXYZNetCDFFile();
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("y"), SGIDataColumnTypeConstants.Y_VALUE);
    List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>();
    columnInfoList.add(xInfo);
    columnInfoList.add(yInfo);
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "y");
    assertFalse(
        SGDefaultColumnTypeNetCDFUtility.getForSXYZNetCDFData(
            columnInfoList,
            new HashMap<String, Object>(),
            root.getAttributes(),
            null,
            new SGDataColumnInfo[2]));
  }

  @Test
  void getForVXYNetCDFDataNormalAssignsCoordinatesAndComponents() throws Exception {
    SGNetCDFFile file = createVXYNetCDFFile();
    List<SGNetCDFVariable> varList = new ArrayList<SGNetCDFVariable>();
    varList.add(file.findVariable("x"));
    varList.add(file.findVariable("y"));
    varList.add(file.findVariable("v1"));
    varList.add(file.findVariable("v2"));
    SGNetCDFDataColumnInfo[] columns = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.X_COORDINATE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGIDataColumnTypeConstants.Y_COORDINATE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v1"), SGIDataColumnTypeConstants.X_COMPONENT),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v2"), SGIDataColumnTypeConstants.Y_COMPONENT)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeNetCDFUtility.getForVXYNetCDFDataNormal(
            infoMap, varList, varList.size(), columns));
    assertEquals(SGIDataColumnTypeConstants.X_COORDINATE, columns[0].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.Y_COORDINATE, columns[1].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.X_COMPONENT, columns[2].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.Y_COMPONENT, columns[3].getColumnType());
  }

  @Test
  void getForSXYZNetCDFDataNormalAssignsXYZValues() throws Exception {
    SGNetCDFFile file = createSXYZNetCDFFile();
    List<SGNetCDFVariable> varList = new ArrayList<SGNetCDFVariable>();
    varList.add(file.findVariable("x"));
    varList.add(file.findVariable("y"));
    varList.add(file.findVariable("z"));
    varList.add(file.findVariable("v"));
    SGNetCDFDataColumnInfo[] columns = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGIDataColumnTypeConstants.Y_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("z"), SGIDataColumnTypeConstants.Z_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v"), SGIDataColumnTypeConstants.Z_VALUE)
    };
    assertTrue(
        SGDefaultColumnTypeNetCDFUtility.getForSXYZNetCDFData(
            new HashMap<String, Object>(), varList, varList.size(), columns));
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, columns[0].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, columns[1].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.Z_VALUE, columns[3].getColumnType());
  }

  @Test
  void getForSXYNetCDFDataSetsTimeType() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE);
    List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>();
    columnInfoList.add(xInfo);
    columnInfoList.add(yInfo);
    SGDataColumnInfo[] columns = {xInfo, yInfo};
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "height");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_TIME_VARIABLE_NAME, "x");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeNetCDFUtility.getForSXYNetCDFData(
            columnInfoList, infoMap, root.getAttributes(), null, columns));
    assertEquals(SGIDataColumnTypeConstants.ANIMATION_FRAME, columns[0].getColumnType());
  }

  @Test
  void getForSXYNetCDFDataSetsPickUpType() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE);
    List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>();
    columnInfoList.add(xInfo);
    columnInfoList.add(yInfo);
    SGDataColumnInfo[] columns = {xInfo, yInfo};
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "height");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_PICKUP_DIMENSION_NAME, "x");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeNetCDFUtility.getForSXYNetCDFData(
            columnInfoList, infoMap, root.getAttributes(), null, columns));
    assertEquals(SGIDataColumnTypeConstants.PICKUP, columns[0].getColumnType());
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, columns[1].getColumnType());
  }

  @Test
  void getForVXYNetCDFDataSetsTimeType() throws Exception {
    SGNetCDFFile file = createVXYNetCDFFile();
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGIDataColumnTypeConstants.X_COORDINATE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("y"), SGIDataColumnTypeConstants.Y_COORDINATE);
    SGNetCDFDataColumnInfo fInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("v1"), SGIDataColumnTypeConstants.X_COMPONENT);
    SGNetCDFDataColumnInfo sInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("v2"), SGIDataColumnTypeConstants.Y_COMPONENT);
    List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>();
    columnInfoList.add(xInfo);
    columnInfoList.add(yInfo);
    columnInfoList.add(fInfo);
    columnInfoList.add(sInfo);
    SGDataColumnInfo[] columns = {xInfo, yInfo, fInfo, sInfo};
    Document doc = SGUtilityText.getDocumentFromString("<root/>");
    Element root = doc.getDocumentElement();
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_X_COORDINATE_VARIABLE_NAME, "x");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_Y_COORDINATE_VARIABLE_NAME, "y");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_FIRST_COMPONENT_VARIABLE_NAME, "v1");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_SECOND_COMPONENT_VARIABLE_NAME, "v2");
    root.setAttribute(SGIDataPropertyKeyConstants.KEY_TIME_VARIABLE_NAME, "x");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeNetCDFUtility.getForVXYNetCDFData(
            columnInfoList, infoMap, root.getAttributes(), null, columns));
    assertEquals(SGIDataColumnTypeConstants.ANIMATION_FRAME, columns[0].getColumnType());
  }
}
