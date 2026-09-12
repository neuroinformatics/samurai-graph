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
import jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeUtility.DefaultColumnTypeResult;
import org.junit.jupiter.api.Test;
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
}
