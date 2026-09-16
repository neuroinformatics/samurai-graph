package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer;
import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

/** Unit tests for {@link SGDataFileUtility}. */
class SGDataFileUtilityTest {

  private SGNetCDFFile openExample16() throws IOException {
    return new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
  }

  private File createTempHdf5File() throws IOException {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    return path.toFile();
  }

  @Test
  void getValueTypeAttributeCarriesValueType() {
    ucar.nc2.Attribute attr =
        SGDataFileUtility.getValueTypeAttribute(SGDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    assertEquals(SGNetCDFConstants.ATTRIBUTE_VALUE_TYPE, attr.getShortName());
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, attr.getStringValue());
  }

  @Test
  void textAndDateVariableChecksRejectNumericVariables() throws IOException {
    SGNetCDFFile file = openExample16();
    assertFalse(SGDataFileUtility.isSGTextVariable(file.findVariable("x").getVariable()));
    assertFalse(SGDataFileUtility.isSGDateVariable(file.findVariable("x").getVariable()));
  }

  @Test
  void getNetCDFDataColumnInfoBuildsColumnsForAllVariables() throws IOException {
    SGNetCDFFile file = openExample16();
    List<SGNetCDFVariable> vars = file.getVariables();
    SGNetCDFDataColumnInfo[] columns =
        SGDataFileUtility.getNetCDFDataColumnInfo(vars, new HashMap<String, Object>());
    assertNotNull(columns);
    assertEquals(vars.size(), columns.length);
    assertEquals("x", columns[0].getName());
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, columns[0].getValueType());
  }

  @Test
  void getMDArrayDataColumnInfoBuildsColumnsForAllVariables() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayDataColumnInfo[] columns =
        SGDataFileUtility.getMDArrayDataColumnInfo(
            matFile, matFile.getVariables(), new HashMap<String, Object>());
    assertNotNull(columns);
    assertEquals(2, columns.length);
    assertEquals("a", columns[0].getName());
  }

  @Test
  void findHDF5AttributesReadsAttribute() throws Exception {
    File file = createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeDoubleArray("values", new double[] {1.5, 2.5});
      writer.string().setAttr("/", "value_type", "Text");
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      List<SGAttribute> attrs =
          SGDataFileUtility.findHDF5Attributes(reader, "/", Arrays.asList("value_type"));
      assertEquals(1, attrs.size());
      assertEquals("value_type", attrs.get(0).getName());
      assertEquals("Text", attrs.get(0).getValue(0));
    }
  }

  @Test
  void writeHDF5AttributeCopiesAttributeToAnotherFile() throws Exception {
    File src = createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(src)) {
      writer.writeDoubleArray("values", new double[] {1.5, 2.5});
      writer.string().setAttr("/", "value_type", "Text");
    }
    File dst = createTempHdf5File();
    try (IHDF5Writer dstWriter = HDF5FactoryProvider.get().open(dst)) {
      dstWriter.writeDoubleArray("values", new double[] {3.0, 4.0});
      try (IHDF5Reader srcReader = HDF5FactoryProvider.get().openForReading(src)) {
        assertTrue(
            SGDataFileUtility.writeHDF5Attribute(
                dstWriter, srcReader, "/", Arrays.asList("value_type")));
      }
    }
    try (IHDF5Reader dstReader = HDF5FactoryProvider.get().openForReading(dst)) {
      List<SGAttribute> attrs =
          SGDataFileUtility.findHDF5Attributes(dstReader, "/", Arrays.asList("value_type"));
      assertEquals(1, attrs.size());
      assertEquals("Text", attrs.get(0).getValue(0));
    }
  }

  @Test
  void canOpenNetCDFReturnsTrueForLoadedData() throws IOException {
    SGNetCDFFile file = openExample16();
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGDataColumnTypeConstants.Y_VALUE);
    SGSXYNetCDFData data =
        new SGSXYNetCDFData(
            file,
            new SGDataSourceObserver(),
            xInfo,
            yInfo,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            true);
    assertTrue(SGDataFileUtility.canOpenNetCDF(data));
  }

  private static SGSDArrayDataColumnInfo column(final String title, final String columnType) {
    SGSDArrayDataColumnInfo info =
        new SGSDArrayDataColumnInfo(title, SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, 4);
    info.setColumnType(columnType);
    return info;
  }

  @Test
  void checkNetCDFDataColumnsRejectsMultipleAnimationFrames() {
    SGDataColumnInfo[] cols = {
      column("t1", SGDataColumnTypeConstants.ANIMATION_FRAME),
      column("t2", SGDataColumnTypeConstants.ANIMATION_FRAME)
    };
    SGNetCDFFile file = mock(SGNetCDFFile.class);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    assertFalse(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkNetCDFDataColumnsRejectsUnknownAnimationVariable() {
    SGDataColumnInfo[] cols = {
      column("t1", SGDataColumnTypeConstants.ANIMATION_FRAME),
      column("x", SGDataColumnTypeConstants.X_VALUE)
    };
    SGNetCDFFile file = mock(SGNetCDFFile.class);
    when(file.findVariable("t1")).thenReturn(null);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    assertFalse(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkNetCDFDataColumnsRejectsMissingMultipleVariableFlag() {
    SGDataColumnInfo[] cols = {column("x", SGDataColumnTypeConstants.X_VALUE)};
    SGNetCDFFile file = mock(SGNetCDFFile.class);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    assertFalse(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkNetCDFDataColumnsRejectsMultipleXWithoutFlag() {
    SGDataColumnInfo[] cols = {
      column("x1", SGDataColumnTypeConstants.X_VALUE),
      column("x2", SGDataColumnTypeConstants.X_VALUE),
      column("y", SGDataColumnTypeConstants.Y_VALUE)
    };
    SGNetCDFFile file = mock(SGNetCDFFile.class);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertFalse(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkNetCDFDataColumnsRejectsCoordinateYVariable() throws IOException {
    SGNetCDFFile file = openExample16();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.Y_VALUE)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertFalse(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkNetCDFDataColumnsRejectsUnpairedErrorValues() throws IOException {
    SGNetCDFFile file = openExample16();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("height"), SGDataColumnTypeConstants.Y_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("le"), SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "height")
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertFalse(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkNetCDFDataColumnsAcceptsSXYWithPickUp() throws Exception {
    SGNetCDFFile file = createVXYNetCDFFile();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v1"), SGDataColumnTypeConstants.Y_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGDataColumnTypeConstants.PICKUP)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertTrue(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkNetCDFDataColumnsAcceptsSXYZSelection() throws Exception {
    SGNetCDFFile file = createSXYZNetCDFFile();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGDataColumnTypeConstants.Y_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v"), SGDataColumnTypeConstants.Z_VALUE)
    };
    assertTrue(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXYZ_NETCDF_DATA, file, new HashMap<String, Object>()));
  }

  @Test
  void checkNetCDFDataColumnsAcceptsVXYSelection() throws Exception {
    SGNetCDFFile file = createVXYNetCDFFile();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_COORDINATE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGDataColumnTypeConstants.Y_COORDINATE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v1"), SGDataColumnTypeConstants.X_COMPONENT),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v2"), SGDataColumnTypeConstants.Y_COMPONENT)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertTrue(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.VXY_NETCDF_DATA, file, infoMap));
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
    final ucar.nc2.write.NetcdfFormatWriter ignored = writer.build();
    ignored.close();
    return new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path.toString()));
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
    final ucar.nc2.write.NetcdfFormatWriter ignored = writer.build();
    ignored.close();
    return new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path.toString()));
  }

  @Test
  void checkNetCDFDataColumnsAcceptsSXYWithErrorBars() throws Exception {
    SGNetCDFFile file = createSXY2DNetCDFFile();
    SGNetCDFDataColumnInfo leInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("le"), SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "v");
    leInfo.setColumnType(SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for v (0)");
    SGNetCDFDataColumnInfo ueInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("ue"), SGDataColumnTypeConstants.UPPER_ERROR_VALUE, "v");
    ueInfo.setColumnType(SGDataColumnTypeConstants.UPPER_ERROR_VALUE + " for v (0)");
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v"), SGDataColumnTypeConstants.Y_VALUE),
      leInfo,
      ueInfo,
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGDataColumnTypeConstants.PICKUP)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertTrue(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  private static SGNetCDFFile createSXY2DNetCDFFile() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    ucar.nc2.write.NetcdfFormatWriter.Builder writer =
        ucar.nc2.write.NetcdfFormatWriter.createNewNetcdf3(path.toString());
    ucar.nc2.Dimension xDim = writer.addDimension("x", 5);
    ucar.nc2.Dimension yDim = writer.addDimension("y", 4);
    writer.addVariable("x", ucar.ma2.DataType.FLOAT, Arrays.asList(xDim));
    writer.addVariable("y", ucar.ma2.DataType.FLOAT, Arrays.asList(yDim));
    writer.addVariable("v", ucar.ma2.DataType.FLOAT, Arrays.asList(yDim, xDim));
    writer.addVariable("le", ucar.ma2.DataType.FLOAT, Arrays.asList(yDim, xDim));
    writer.addVariable("ue", ucar.ma2.DataType.FLOAT, Arrays.asList(yDim, xDim));
    final ucar.nc2.write.NetcdfFormatWriter ignored = writer.build();
    ignored.close();
    return new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path.toString()));
  }

  @Test
  void checkMDArrayDataColumnsAcceptsSXYSelections() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayVariable[] vars = matFile.getVariables();
    SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[2];
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGDataColumnTypeConstants.X_VALUE);
    cols[1] = SGDataFileUtility.createDataColumnInfo(vars[1], SGDataColumnTypeConstants.Y_VALUE);
    cols[0].setGenericDimensionIndex(0);
    cols[1].setGenericDimensionIndex(0);
    StringBuilder errmsg = new StringBuilder();
    boolean ok =
        SGDataFileUtility.checkMDArrayDataColumns(
            cols,
            SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA,
            matFile,
            new HashMap<String, Object>(),
            errmsg);
    assertTrue(ok, errmsg.toString());
  }

  @Test
  void checkMDArrayDataColumnsRejectsMissingXY() {
    assertFalse(
        SGDataFileUtility.checkMDArrayDataColumns(
            new SGDataColumnInfo[0],
            SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA,
            null,
            new HashMap<String, Object>(),
            new StringBuilder()));
  }

  @Test
  void checkMDArrayDataColumnsAcceptsVXYSelections() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayVariable[] vars = matFile.getVariables();
    SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[2];
    cols[0] =
        SGDataFileUtility.createDataColumnInfo(vars[0], SGDataColumnTypeConstants.X_COMPONENT);
    cols[1] =
        SGDataFileUtility.createDataColumnInfo(vars[1], SGDataColumnTypeConstants.Y_COMPONENT);
    cols[0].setGenericDimensionIndex(0);
    cols[1].setGenericDimensionIndex(0);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    StringBuffer dummy = new StringBuffer();
    StringBuilder errmsg = new StringBuilder();
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            cols, SGDataTypeConstants.VXY_MATLAB_DATA, matFile, infoMap, errmsg),
        errmsg.toString());
  }

  @Test
  void checkMDArrayDataColumnsAcceptsSXYZSelections() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(path.toFile())) {
      writer.writeDoubleArray("v", new double[] {1.0, 2.0, 3.0});
    }
    IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(path.toFile());
    SGHDF5File hdf5File = new SGHDF5File(reader);
    SGMDArrayVariable[] vars = hdf5File.getVariables();
    SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[1];
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGDataColumnTypeConstants.Z_VALUE);
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            cols,
            SGDataTypeConstants.SXYZ_HDF5_DATA,
            hdf5File,
            new HashMap<String, Object>(),
            new StringBuilder()));
  }

  @Test
  void checkMDArrayDataColumnsAcceptsSXYWithPickUp() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayVariable[] vars = matFile.getVariables();
    SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[2];
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGDataColumnTypeConstants.X_VALUE);
    cols[1] = SGDataFileUtility.createDataColumnInfo(vars[1], SGDataColumnTypeConstants.Y_VALUE);
    cols[0].setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 0);
    cols[0].setGenericDimensionIndex(0);
    cols[1].setGenericDimensionIndex(0);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    Map<String, Integer> dimensionIndexMap = new HashMap<String, Integer>();
    dimensionIndexMap.put("a", 0);
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP,
        dimensionIndexMap);
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, new SGIntegerSeriesSet(0, 2, 1));
    StringBuilder errmsg = new StringBuilder();
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            cols, SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA, matFile, infoMap, errmsg),
        errmsg.toString());
  }

  @Test
  void checkMDArrayDataColumnsAcceptsSXYWithErrorBars() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    MLDouble le = new MLDouble("le", new double[][] {{0.1}, {0.2}, {0.3}});
    MLDouble ue = new MLDouble("ue", new double[][] {{0.4}, {0.5}, {0.6}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b, le, ue));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayVariable[] vars = matFile.getVariables();
    SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[4];
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGDataColumnTypeConstants.X_VALUE);
    cols[1] = SGDataFileUtility.createDataColumnInfo(vars[1], SGDataColumnTypeConstants.Y_VALUE);
    cols[2] =
        SGDataFileUtility.createDataColumnInfo(
            vars[2], SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "b");
    cols[3] =
        SGDataFileUtility.createDataColumnInfo(
            vars[3], SGDataColumnTypeConstants.UPPER_ERROR_VALUE, "b");
    cols[2].setColumnType(SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for b");
    cols[3].setColumnType(SGDataColumnTypeConstants.UPPER_ERROR_VALUE + " for b");
    for (SGMDArrayDataColumnInfo col : cols) {
      col.setGenericDimensionIndex(0);
    }
    StringBuilder errmsg = new StringBuilder();
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            cols,
            SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA,
            matFile,
            new HashMap<String, Object>(),
            errmsg),
        errmsg.toString());
  }

  @Test
  void checkMDArrayDataColumnsAcceptsSXYWithTickLabels() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    MLDouble t = new MLDouble("t", new double[][] {{1.0}, {2.0}, {3.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b, t));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayVariable[] vars = matFile.getVariables();
    SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[3];
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGDataColumnTypeConstants.X_VALUE);
    cols[1] = SGDataFileUtility.createDataColumnInfo(vars[1], SGDataColumnTypeConstants.Y_VALUE);
    cols[2] =
        SGDataFileUtility.createDataColumnInfo(vars[2], SGDataColumnTypeConstants.TICK_LABEL, "b");
    for (SGMDArrayDataColumnInfo col : cols) {
      col.setGenericDimensionIndex(0);
    }
    StringBuilder errmsg = new StringBuilder();
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            cols,
            SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA,
            matFile,
            new HashMap<String, Object>(),
            errmsg),
        errmsg.toString());
  }

  @Test
  void checkMDArrayDataColumnsAcceptsSXYWithLowerUpper() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    MLDouble lu = new MLDouble("lu", new double[][] {{0.1}, {0.2}, {0.3}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b, lu));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayVariable[] vars = matFile.getVariables();
    SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[3];
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGDataColumnTypeConstants.X_VALUE);
    cols[1] = SGDataFileUtility.createDataColumnInfo(vars[1], SGDataColumnTypeConstants.Y_VALUE);
    cols[2] =
        SGDataFileUtility.createDataColumnInfo(
            vars[2], SGDataColumnTypeConstants.LOWER_UPPER_ERROR_VALUE, "b");
    for (SGMDArrayDataColumnInfo col : cols) {
      col.setGenericDimensionIndex(0);
    }
    StringBuilder errmsg = new StringBuilder();
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            cols,
            SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA,
            matFile,
            new HashMap<String, Object>(),
            errmsg),
        errmsg.toString());
  }

  @Test
  void checkMDArrayDataColumnsAcceptsVXYGridSelection() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayVariable[] vars = matFile.getVariables();
    SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[2];
    cols[0] =
        SGDataFileUtility.createDataColumnInfo(vars[0], SGDataColumnTypeConstants.X_COMPONENT);
    cols[1] =
        SGDataFileUtility.createDataColumnInfo(vars[1], SGDataColumnTypeConstants.Y_COMPONENT);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    StringBuilder errmsg = new StringBuilder();
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            cols, SGDataTypeConstants.VXY_MATLAB_DATA, matFile, infoMap, errmsg),
        errmsg.toString());
  }

  @Test
  void checkNetCDFDataColumnsAcceptsSXYMultipleVariable() throws IOException {
    SGNetCDFFile file = openExample16();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("height"), SGDataColumnTypeConstants.Y_VALUE)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
    assertTrue(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void updatePickupParametersForMDArrayAndNetCDF() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          matFile.getVariables()[0], SGDataColumnTypeConstants.X_VALUE)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    assertTrue(SGDataFileUtility.updatePickupParameters(infoMap, cols));
    assertEquals(
        Integer.valueOf(0),
        ((SGIntegerSeriesSet) infoMap.get(SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES))
            .getNumbers()[0]);

    cols[0].setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, -1);
    Map<String, Object> invalidMap = new HashMap<String, Object>();
    assertTrue(SGDataFileUtility.updatePickupParameters(invalidMap, cols));

    cols[0].setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 0);
    Map<String, Object> infoMap2 = new HashMap<String, Object>();
    assertTrue(SGDataFileUtility.updatePickupParameters(infoMap2, cols));
    assertEquals(
        3,
        ((SGIntegerSeriesSet) infoMap2.get(SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES))
            .getNumbers()
            .length);

    SGNetCDFFile file = createSXY2DNetCDFFile();
    SGNetCDFDataColumnInfo[] nCols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.PICKUP)
    };
    Map<String, Object> infoMap3 = new HashMap<String, Object>();
    assertTrue(SGDataFileUtility.updatePickupParameters(infoMap3, nCols));
    assertEquals(
        5,
        ((SGIntegerSeriesSet) infoMap3.get(SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES))
            .getNumbers()
            .length);
    SGNetCDFDataColumnInfo[] noPickup = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v"), SGDataColumnTypeConstants.Y_VALUE)
    };
    Map<String, Object> infoMap4 = new HashMap<String, Object>();
    assertTrue(SGDataFileUtility.updatePickupParameters(infoMap4, noPickup));
    assertEquals(
        0,
        ((SGIntegerSeriesSet) infoMap4.get(SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES))
            .getNumbers()[0]);
  }

  @Test
  void textAndDateVariableChecksAcceptAttributeVariables() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    ucar.nc2.write.NetcdfFormatWriter.Builder writer =
        ucar.nc2.write.NetcdfFormatWriter.createNewNetcdf4(
            ucar.nc2.write.NetcdfFileFormat.NETCDF4, path.toString(), null);
    ucar.nc2.Dimension xDim = writer.addDimension("x", 3);
    ucar.nc2.Variable.Builder<?> textVar =
        writer.addVariable("label", ucar.ma2.DataType.CHAR, Arrays.asList(xDim));
    textVar.addAttribute(
        new ucar.nc2.Attribute(
            SGNetCDFConstants.ATTRIBUTE_VALUE_TYPE, SGDataColumnTypeConstants.VALUE_TYPE_TEXT));
    ucar.nc2.Variable.Builder<?> dateVar =
        writer.addVariable("date", ucar.ma2.DataType.CHAR, Arrays.asList(xDim));
    dateVar.addAttribute(
        new ucar.nc2.Attribute(
            SGNetCDFConstants.ATTRIBUTE_VALUE_TYPE, SGDataColumnTypeConstants.VALUE_TYPE_DATE));
    final ucar.nc2.write.NetcdfFormatWriter ignored = writer.build();
    ignored.close();
    ucar.nc2.NetcdfFile ncFile = ucar.nc2.NetcdfFiles.open(path.toString());
    assertTrue(SGDataFileUtility.isSGTextVariable(ncFile.findVariable("label")));
    assertTrue(SGDataFileUtility.isSGDateVariable(ncFile.findVariable("date")));
    assertFalse(SGDataFileUtility.isSGTextVariable(ncFile.findVariable("date")));
  }

  @Test
  void getMDArrayDataColumnInfoAppliesTimeAndPickupMaps() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    Map<String, Integer> timeDimMap = new HashMap<String, Integer>();
    timeDimMap.put("a", 1);
    Map<String, Integer> pickupDimMap = new HashMap<String, Integer>();
    pickupDimMap.put("a", 0);
    infoMap.put(SGDataInformationKeyConstants.KEY_TIME_DIMENSION_INDEX_MAP, timeDimMap);
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP, pickupDimMap);
    SGMDArrayDataColumnInfo[] columns =
        SGDataFileUtility.getMDArrayDataColumnInfo(matFile, matFile.getVariables(), infoMap);
    assertEquals(1, columns[0].getTimeDimensionIndex().intValue());
    assertEquals(
        0, columns[0].getDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION).intValue());
  }

  @Test
  void getMDArrayDataColumnInfoRejectsInvalidMaps() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    Map<String, Integer> timeDimMap = new HashMap<String, Integer>();
    timeDimMap.put("a", 5);
    infoMap.put(SGDataInformationKeyConstants.KEY_TIME_DIMENSION_INDEX_MAP, timeDimMap);
    assertNull(
        SGDataFileUtility.getMDArrayDataColumnInfo(matFile, matFile.getVariables(), infoMap));
    Map<String, Integer> pickupDimMap = new HashMap<String, Integer>();
    pickupDimMap.put("a", 5);
    infoMap.clear();
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP, pickupDimMap);
    assertNull(
        SGDataFileUtility.getMDArrayDataColumnInfo(matFile, matFile.getVariables(), infoMap));
    SGMDArrayVariable var2 = matFile.getVariables()[0];
    var2.setGenericDimensionIndex(1);
    infoMap.clear();
    Map<String, Integer> timeMap2 = new HashMap<String, Integer>();
    timeMap2.put("a", 1);
    infoMap.put(SGDataInformationKeyConstants.KEY_TIME_DIMENSION_INDEX_MAP, timeMap2);
    assertNull(
        SGDataFileUtility.getMDArrayDataColumnInfo(
            matFile, new SGMDArrayVariable[] {var2}, infoMap));
    Map<String, Integer> pickupMap2 = new HashMap<String, Integer>();
    pickupMap2.put("a", 1);
    infoMap.clear();
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP, pickupMap2);
    assertNull(
        SGDataFileUtility.getMDArrayDataColumnInfo(
            matFile, new SGMDArrayVariable[] {var2}, infoMap));
  }

  @Test
  void writeHDF5AttributeCopiesIntFloatAndArrayAttributes() throws Exception {
    File src = createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(src)) {
      writer.writeDoubleArray("values", new double[] {1.5, 2.5});
      writer.int32().setAttr("/", "count", 3);
      writer.float32().setAttr("/", "ratio", 1.5f);
      writer.string().setArrayAttr("/", "tags", new String[] {"a", "b"});
    }
    File dst = createTempHdf5File();
    try (IHDF5Writer dstWriter = HDF5FactoryProvider.get().open(dst)) {
      dstWriter.writeDoubleArray("values", new double[] {3.0, 4.0});
      try (IHDF5Reader srcReader = HDF5FactoryProvider.get().openForReading(src)) {
        assertTrue(
            SGDataFileUtility.writeHDF5Attribute(
                dstWriter, srcReader, "/", Arrays.asList("count", "ratio", "tags")));
      }
    }
    try (IHDF5Reader dstReader = HDF5FactoryProvider.get().openForReading(dst)) {
      List<SGAttribute> attrs =
          SGDataFileUtility.findHDF5Attributes(
              dstReader, "/", Arrays.asList("count", "ratio", "tags"));
      assertEquals(3, attrs.size());
      assertEquals(3, ((Number) attrs.get(0).getValue(0)).intValue());
      assertEquals(1.5f, ((Number) attrs.get(1).getValue(0)).floatValue(), 0.0f);
      assertEquals("[a, b]", java.util.Arrays.toString((String[]) attrs.get(2).getValue(0)));
    }
  }

  @Test
  void createDataColumnInfoArrayBuildsAllVariants() throws Exception {
    SGNetCDFFile file = openExample16();
    SGNetCDFVariable[] netCDFVars = {file.findVariable("x"), file.findVariable("height")};
    SGNetCDFDataColumnInfo[] byTypes =
        SGDataFileUtility.createDataColumnInfoArray(
            netCDFVars,
            new String[] {SGDataColumnTypeConstants.X_VALUE, SGDataColumnTypeConstants.Y_VALUE});
    assertEquals(2, byTypes.length);
    assertEquals(SGDataColumnTypeConstants.X_VALUE, byTypes[0].getColumnType());
    SGNetCDFDataColumnInfo[] byType =
        SGDataFileUtility.createDataColumnInfoArray(netCDFVars, SGDataColumnTypeConstants.Y_VALUE);
    assertEquals(2, byType.length);
    SGNetCDFDataColumnInfo[] single =
        SGDataFileUtility.createDataColumnInfoArray(
            file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE);
    assertEquals(1, single.length);
    assertNull(SGDataFileUtility.createDataColumnInfoArray((SGNetCDFVariable[]) null, "x"));
    assertNull(
        SGDataFileUtility.createDataColumnInfo(
            (SGNetCDFVariable) null, SGDataColumnTypeConstants.X_VALUE));
    assertNull(
        SGDataFileUtility.createDataColumnInfo(
            (SGNetCDFVariable) null, SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "y"));

    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayVariable[] mdVars = matFile.getVariables();
    SGMDArrayDataColumnInfo[] mdByTypes =
        SGDataFileUtility.createDataColumnInfoArray(
            mdVars,
            new String[] {SGDataColumnTypeConstants.X_VALUE, SGDataColumnTypeConstants.Y_VALUE});
    assertEquals(2, mdByTypes.length);
    SGMDArrayDataColumnInfo[] mdByType =
        SGDataFileUtility.createDataColumnInfoArray(mdVars, SGDataColumnTypeConstants.Y_VALUE);
    assertEquals(2, mdByType.length);
    SGMDArrayDataColumnInfo[] mdSingle =
        SGDataFileUtility.createDataColumnInfoArray(mdVars[0], SGDataColumnTypeConstants.X_VALUE);
    assertEquals(1, mdSingle.length);
    assertNull(SGDataFileUtility.createDataColumnInfoArray((SGMDArrayVariable[]) null, "x"));
    assertNull(SGDataFileUtility.createDataColumnInfo((SGMDArrayVariable) null, "x"));
    assertNull(SGDataFileUtility.createDataColumnInfo((SGMDArrayVariable) null, "x", "y"));
    assertEquals(
        0, SGDataFileUtility.createDataColumnInfoArray((SGMDArrayVariable) null, "x").length);
  }

  @Test
  void checkMDArrayDataColumnsRejectsInvalidSelections() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0, 2.0}, {3.0, 4.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          matFile.getVariables()[0], SGDataColumnTypeConstants.X_VALUE)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    StringBuilder errmsg = new StringBuilder();
    assertFalse(
        SGDataFileUtility.checkMDArrayDataColumns(
            cols, SGDataTypeConstants.SXY_MATLAB_DATA, matFile, infoMap, errmsg));
  }

  @Test
  void checkMDArrayDataColumnsAcceptsPickUpSelections() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0, 2.0}, {3.0, 4.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayDataColumnInfo xCol =
        SGDataFileUtility.createDataColumnInfo(
            matFile.getVariables()[0], SGDataColumnTypeConstants.X_VALUE);
    xCol.setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 0);
    xCol.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 1);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    Map<String, Integer> dimMap = new HashMap<String, Integer>();
    dimMap.put("a", 0);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP, dimMap);
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, new SGIntegerSeriesSet(0, 1, 2));
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    StringBuilder errmsg = new StringBuilder();
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            new SGDataColumnInfo[] {xCol},
            SGDataTypeConstants.SXY_MATLAB_DATA,
            matFile,
            infoMap,
            errmsg),
        errmsg.toString());
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, new SGIntegerSeriesSet(0, 5, 5));
    assertFalse(
        SGDataFileUtility.checkMDArrayDataColumns(
            new SGDataColumnInfo[] {xCol},
            SGDataTypeConstants.SXY_MATLAB_DATA,
            matFile,
            infoMap,
            errmsg));
  }

  @Test
  void updateColumnTypesOfMultipleVariableDataFromNames() throws Exception {
    SGNetCDFFile file = createSXY2DNetCDFFile();
    List<SGNetCDFVariable> vars = file.getVariables();
    SGNetCDFDataColumnInfo[] columns = new SGNetCDFDataColumnInfo[vars.size()];
    for (int ii = 0; ii < vars.size(); ii++) {
      columns[ii] = SGDataFileUtility.createDataColumnInfo(vars.get(ii), "");
    }
    SGDataFileUtility.updateColumnTypeOfSXYMultipleVariableNetCDFDataFromVariableNames(
        file,
        columns,
        new String[] {"x"},
        new String[] {"v"},
        new String[] {"le"},
        new String[] {"ue"},
        new String[] {"v"},
        null,
        null);
    assertEquals(SGDataColumnTypeConstants.X_VALUE, columns[0].getColumnType());
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, columns[2].getColumnType());
    assertEquals(
        SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for v", columns[3].getColumnType());
    assertEquals(
        SGDataColumnTypeConstants.UPPER_ERROR_VALUE + " for v", columns[4].getColumnType());
    SGDataFileUtility.updateColumnTypeOfSXYMultipleVariableNetCDFDataFromVariableNames(
        file,
        columns,
        new String[0],
        new String[0],
        null,
        null,
        null,
        new String[] {"le"},
        new String[] {"v"});
    assertEquals(SGDataColumnTypeConstants.TICK_LABEL + " for v", columns[3].getColumnType());
    SGDataFileUtility.updateColumnTypeOfSXYMultipleVariableNetCDFDataFromVariableNames(
        file,
        columns,
        new String[0],
        new String[0],
        new String[] {"le"},
        new String[] {"le"},
        new String[] {"v"},
        null,
        null);
  }

  @Test
  void checkNetCDFDataColumnsAcceptsVXYPolar() throws Exception {
    SGNetCDFFile file = createVXYNetCDFFile();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_COORDINATE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGDataColumnTypeConstants.Y_COORDINATE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v1"), SGDataColumnTypeConstants.MAGNITUDE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v2"), SGDataColumnTypeConstants.ANGLE)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    assertTrue(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.VXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkMDArrayDataColumnsAcceptsErrorBars() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    MLDouble le = new MLDouble("le", new double[][] {{0.5}, {0.5}, {0.5}});
    MLDouble ue = new MLDouble("ue", new double[][] {{0.5}, {0.5}, {0.5}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b, le, ue));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayVariable[] vars = matFile.getVariables();
    SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[4];
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGDataColumnTypeConstants.X_VALUE);
    cols[1] = SGDataFileUtility.createDataColumnInfo(vars[1], SGDataColumnTypeConstants.Y_VALUE);
    cols[2] =
        SGDataFileUtility.createDataColumnInfo(
            vars[2], SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "b");
    cols[3] =
        SGDataFileUtility.createDataColumnInfo(
            vars[3], SGDataColumnTypeConstants.UPPER_ERROR_VALUE, "b");
    for (SGMDArrayDataColumnInfo col : cols) {
      col.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    StringBuilder errmsg = new StringBuilder();
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            cols, SGDataTypeConstants.SXY_MATLAB_DATA, matFile, infoMap, errmsg),
        errmsg.toString());
    SGMDArrayDataColumnInfo[] onlyLower = new SGMDArrayDataColumnInfo[3];
    onlyLower[0] = cols[0];
    onlyLower[1] = cols[1];
    onlyLower[2] = cols[2];
    assertFalse(
        SGDataFileUtility.checkMDArrayDataColumns(
            onlyLower, SGDataTypeConstants.SXY_MATLAB_DATA, matFile, infoMap, errmsg));
  }

  @Test
  void findHolderInfoAndCanOpenHelpers() throws Exception {
    SGNetCDFFile file = openExample16();
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGDataColumnTypeConstants.Y_VALUE);
    SGNetCDFDataColumnInfo leInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("le"), SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "height");
    assertEquals(
        "height",
        SGDataFileUtility.findHolderInfo(new SGDataColumnInfo[] {yInfo}, leInfo).getName());
    SGNetCDFData data = mock(SGNetCDFData.class);
    when(data.getNetcdfFile()).thenThrow(new RuntimeException("broken"));
    assertFalse(SGDataFileUtility.canOpenNetCDF(data));
  }

  @Test
  void getNetCDFDataColumnInfoAppliesOriginMapAndGroupName() throws Exception {
    SGNetCDFFile file = openExample16();
    org.w3c.dom.NamedNodeMap nodeMap = mock(org.w3c.dom.NamedNodeMap.class);
    org.w3c.dom.Node node = mock(org.w3c.dom.Node.class);
    when(nodeMap.getNamedItem(SGDataPropertyKeyConstants.KEY_ORIGIN_MAP)).thenReturn(node);
    when(node.getNodeValue()).thenReturn("x:0");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph.KEY_NODE_MAP, nodeMap);
    infoMap.put(jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph.KEY_GROUP_NAME, "g");
    SGNetCDFDataColumnInfo[] columns =
        SGDataFileUtility.getNetCDFDataColumnInfo(file.getVariables(), infoMap);
    assertEquals(file.getVariables().size(), columns.length);
    assertEquals("x", columns[0].getName());
    when(node.getNodeValue()).thenReturn("x:99");
    SGNetCDFDataColumnInfo[] bounded =
        SGDataFileUtility.getNetCDFDataColumnInfo(file.getVariables(), infoMap);
    assertEquals(0, bounded[0].getOrigin());
  }

  @Test
  void writeHDF5AttributeCopiesBooleanAttribute() throws Exception {
    File src = createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(src)) {
      writer.writeDoubleArray("values", new double[] {1.5});
      writer.bool().setAttr("/", "flag", true);
    }
    File dst = createTempHdf5File();
    try (IHDF5Writer dstWriter = HDF5FactoryProvider.get().open(dst)) {
      dstWriter.writeDoubleArray("values", new double[] {2.5});
      try (IHDF5Reader srcReader = HDF5FactoryProvider.get().openForReading(src)) {
        assertTrue(
            SGDataFileUtility.writeHDF5Attribute(dstWriter, srcReader, "/", Arrays.asList("flag")));
      }
    }
  }

  @Test
  void createErrorBarInfoUsesLowerUpperForSameVariables() throws Exception {
    SGNetCDFFile file = openExample16();
    SGNetCDFVariable le = file.findVariable("le");
    SGNetCDFVariable ue = file.findVariable("le");
    SGNetCDFDataColumnInfo info =
        SGDataFileUtility.createErrorBarInfo(
            le, SGDataColumnTypeConstants.LOWER_ERROR_VALUE, le, ue, file.findVariable("height"));
    assertEquals(
        SGDataColumnTypeConstants.LOWER_UPPER_ERROR_VALUE + " for height", info.getColumnType());
  }

  @Test
  void checkNetCDFDataColumnsAcceptsVXYWithIndex() throws Exception {
    SGNetCDFFile file = createVXYNetCDFFile();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v1"), SGDataColumnTypeConstants.X_COORDINATE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v2"), SGDataColumnTypeConstants.Y_COORDINATE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v1"), SGDataColumnTypeConstants.X_COMPONENT),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v2"), SGDataColumnTypeConstants.Y_COMPONENT),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.INDEX)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertTrue(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.VXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkNetCDFDataColumnsAcceptsSXYZWithIndex() throws Exception {
    SGNetCDFFile file = createSXYZNetCDFFile();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v"), SGDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v"), SGDataColumnTypeConstants.Y_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v"), SGDataColumnTypeConstants.Z_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.INDEX)
    };
    assertTrue(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXYZ_NETCDF_DATA, file, new HashMap<>()));
  }

  @Test
  void checkNetCDFDataColumnsRejectsBadSXYZSelections() throws Exception {
    SGNetCDFFile file = createSXYZNetCDFFile();
    SGDataColumnInfo[] twoX = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v"), SGDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v"), SGDataColumnTypeConstants.Y_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v"), SGDataColumnTypeConstants.Z_VALUE)
    };
    assertFalse(
        SGDataFileUtility.checkNetCDFDataColumns(
            twoX, SGDataTypeConstants.SXYZ_NETCDF_DATA, file, new HashMap<>()));
    SGDataColumnInfo[] coordZ = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGDataColumnTypeConstants.Y_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("z"), SGDataColumnTypeConstants.Z_VALUE)
    };
    assertFalse(
        SGDataFileUtility.checkNetCDFDataColumns(
            coordZ, SGDataTypeConstants.SXYZ_NETCDF_DATA, file, new HashMap<>()));
    SGDataColumnInfo[] noZ = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGDataColumnTypeConstants.Y_VALUE)
    };
    assertFalse(
        SGDataFileUtility.checkNetCDFDataColumns(
            noZ, SGDataTypeConstants.SXYZ_NETCDF_DATA, file, new HashMap<>()));
  }

  @Test
  void checkMDArrayDataColumnsAcceptsSXYZAndVXYSelections() throws Exception {
    SGMDArrayFile mdFile = mock(SGMDArrayFile.class);
    StringBuilder errmsg = new StringBuilder();

    SGMDArrayDataColumnInfo zGrid =
        SGTestMDArrayColumns.column("z", new int[] {3, 4}, SGDataColumnTypeConstants.Z_VALUE);
    zGrid.setDimensionIndex(SGMDArrayConstants.KEY_SXYZ_X_DIMENSION, 1);
    zGrid.setDimensionIndex(SGMDArrayConstants.KEY_SXYZ_Y_DIMENSION, 0);
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            new SGDataColumnInfo[] {zGrid},
            SGDataTypeConstants.SXYZ_MATLAB_DATA,
            mdFile,
            new HashMap<String, Object>(),
            errmsg),
        errmsg.toString());

    SGMDArrayDataColumnInfo xGrid =
        SGTestMDArrayColumns.column("x", new int[] {4}, SGDataColumnTypeConstants.X_VALUE);
    xGrid.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            new SGDataColumnInfo[] {zGrid, xGrid},
            SGDataTypeConstants.SXYZ_MATLAB_DATA,
            mdFile,
            new HashMap<String, Object>(),
            errmsg),
        errmsg.toString());
    SGMDArrayDataColumnInfo xBad =
        SGTestMDArrayColumns.column("x", new int[] {5}, SGDataColumnTypeConstants.X_VALUE);
    xBad.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    assertFalse(
        SGDataFileUtility.checkMDArrayDataColumns(
            new SGDataColumnInfo[] {zGrid, xBad},
            SGDataTypeConstants.SXYZ_MATLAB_DATA,
            mdFile,
            new HashMap<String, Object>(),
            errmsg));

    SGMDArrayDataColumnInfo zScatter =
        SGTestMDArrayColumns.column("z", new int[] {5}, SGDataColumnTypeConstants.Z_VALUE);
    zScatter.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            new SGDataColumnInfo[] {zScatter},
            SGDataTypeConstants.SXYZ_MATLAB_DATA,
            mdFile,
            new HashMap<String, Object>(),
            errmsg),
        errmsg.toString());

    SGMDArrayDataColumnInfo fGrid =
        SGTestMDArrayColumns.column("v1", new int[] {4, 5}, SGDataColumnTypeConstants.X_COMPONENT);
    fGrid.setDimensionIndex(SGMDArrayConstants.KEY_VXY_X_DIMENSION, 1);
    fGrid.setDimensionIndex(SGMDArrayConstants.KEY_VXY_Y_DIMENSION, 0);
    SGMDArrayDataColumnInfo sGrid =
        SGTestMDArrayColumns.column("v2", new int[] {4, 5}, SGDataColumnTypeConstants.Y_COMPONENT);
    sGrid.setDimensionIndex(SGMDArrayConstants.KEY_VXY_X_DIMENSION, 1);
    sGrid.setDimensionIndex(SGMDArrayConstants.KEY_VXY_Y_DIMENSION, 0);
    Map<String, Object> vxyMap = new HashMap<String, Object>();
    vxyMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            new SGDataColumnInfo[] {fGrid, sGrid},
            SGDataTypeConstants.VXY_MATLAB_DATA,
            mdFile,
            vxyMap,
            errmsg),
        errmsg.toString());
    SGMDArrayDataColumnInfo fScatter =
        SGTestMDArrayColumns.column("v1", new int[] {4}, SGDataColumnTypeConstants.X_COMPONENT);
    fScatter.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    SGMDArrayDataColumnInfo sScatter =
        SGTestMDArrayColumns.column("v2", new int[] {4}, SGDataColumnTypeConstants.Y_COMPONENT);
    sScatter.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    assertTrue(
        SGDataFileUtility.checkMDArrayDataColumns(
            new SGDataColumnInfo[] {fScatter, sScatter},
            SGDataTypeConstants.VXY_MATLAB_DATA,
            mdFile,
            vxyMap,
            errmsg),
        errmsg.toString());
  }
}
