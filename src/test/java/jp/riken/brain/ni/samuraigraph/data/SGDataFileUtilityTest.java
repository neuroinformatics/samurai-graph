package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        SGDataFileUtility.getValueTypeAttribute(SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    assertEquals(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, attr.getShortName());
    assertEquals(SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, attr.getStringValue());
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
    assertEquals(SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, columns[0].getValueType());
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
            file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE);
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
        new SGSDArrayDataColumnInfo(title, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, 4);
    info.setColumnType(columnType);
    return info;
  }

  @Test
  void checkNetCDFDataColumnsRejectsMultipleAnimationFrames() {
    SGDataColumnInfo[] cols = {
      column("t1", SGIDataColumnTypeConstants.ANIMATION_FRAME),
      column("t2", SGIDataColumnTypeConstants.ANIMATION_FRAME)
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
      column("t1", SGIDataColumnTypeConstants.ANIMATION_FRAME),
      column("x", SGIDataColumnTypeConstants.X_VALUE)
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
    SGDataColumnInfo[] cols = {column("x", SGIDataColumnTypeConstants.X_VALUE)};
    SGNetCDFFile file = mock(SGNetCDFFile.class);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    assertFalse(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkNetCDFDataColumnsRejectsMultipleXWithoutFlag() {
    SGDataColumnInfo[] cols = {
      column("x1", SGIDataColumnTypeConstants.X_VALUE),
      column("x2", SGIDataColumnTypeConstants.X_VALUE),
      column("y", SGIDataColumnTypeConstants.Y_VALUE)
    };
    SGNetCDFFile file = mock(SGNetCDFFile.class);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertFalse(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkNetCDFDataColumnsRejectsCoordinateYVariable() throws IOException {
    SGNetCDFFile file = openExample16();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.Y_VALUE)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertFalse(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkNetCDFDataColumnsRejectsUnpairedErrorValues() throws IOException {
    SGNetCDFFile file = openExample16();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("le"), SGIDataColumnTypeConstants.LOWER_ERROR_VALUE, "height")
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertFalse(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkNetCDFDataColumnsAcceptsSXYWithPickUp() throws Exception {
    SGNetCDFFile file = createVXYNetCDFFile();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v1"), SGIDataColumnTypeConstants.Y_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGIDataColumnTypeConstants.PICKUP)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
    assertTrue(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }

  @Test
  void checkNetCDFDataColumnsAcceptsSXYZSelection() throws Exception {
    SGNetCDFFile file = createSXYZNetCDFFile();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGIDataColumnTypeConstants.Y_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v"), SGIDataColumnTypeConstants.Z_VALUE)
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
    try (ucar.nc2.write.NetcdfFormatWriter ignored = writer.build()) {}
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
    try (ucar.nc2.write.NetcdfFormatWriter ignored = writer.build()) {}
    return new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path.toString()));
  }

  @Test
  void checkNetCDFDataColumnsAcceptsSXYWithErrorBars() throws Exception {
    SGNetCDFFile file = createSXY2DNetCDFFile();
    SGNetCDFDataColumnInfo leInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("le"), SGIDataColumnTypeConstants.LOWER_ERROR_VALUE, "v");
    leInfo.setColumnType(SGIDataColumnTypeConstants.LOWER_ERROR_VALUE + " for v (0)");
    SGNetCDFDataColumnInfo ueInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("ue"), SGIDataColumnTypeConstants.UPPER_ERROR_VALUE, "v");
    ueInfo.setColumnType(SGIDataColumnTypeConstants.UPPER_ERROR_VALUE + " for v (0)");
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v"), SGIDataColumnTypeConstants.Y_VALUE),
      leInfo,
      ueInfo,
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGIDataColumnTypeConstants.PICKUP)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
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
    try (ucar.nc2.write.NetcdfFormatWriter ignored = writer.build()) {}
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
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGIDataColumnTypeConstants.X_VALUE);
    cols[1] = SGDataFileUtility.createDataColumnInfo(vars[1], SGIDataColumnTypeConstants.Y_VALUE);
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
        SGDataFileUtility.createDataColumnInfo(vars[0], SGIDataColumnTypeConstants.X_COMPONENT);
    cols[1] =
        SGDataFileUtility.createDataColumnInfo(vars[1], SGIDataColumnTypeConstants.Y_COMPONENT);
    cols[0].setGenericDimensionIndex(0);
    cols[1].setGenericDimensionIndex(0);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
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
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGIDataColumnTypeConstants.Z_VALUE);
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
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGIDataColumnTypeConstants.X_VALUE);
    cols[1] = SGDataFileUtility.createDataColumnInfo(vars[1], SGIDataColumnTypeConstants.Y_VALUE);
    cols[0].setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 0);
    cols[0].setGenericDimensionIndex(0);
    cols[1].setGenericDimensionIndex(0);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    Map<String, Integer> dimensionIndexMap = new HashMap<String, Integer>();
    dimensionIndexMap.put("a", 0);
    infoMap.put(
        SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP,
        dimensionIndexMap);
    infoMap.put(
        SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, new SGIntegerSeriesSet(0, 2, 1));
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
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGIDataColumnTypeConstants.X_VALUE);
    cols[1] = SGDataFileUtility.createDataColumnInfo(vars[1], SGIDataColumnTypeConstants.Y_VALUE);
    cols[2] =
        SGDataFileUtility.createDataColumnInfo(
            vars[2], SGIDataColumnTypeConstants.LOWER_ERROR_VALUE, "b");
    cols[3] =
        SGDataFileUtility.createDataColumnInfo(
            vars[3], SGIDataColumnTypeConstants.UPPER_ERROR_VALUE, "b");
    cols[2].setColumnType(SGIDataColumnTypeConstants.LOWER_ERROR_VALUE + " for b");
    cols[3].setColumnType(SGIDataColumnTypeConstants.UPPER_ERROR_VALUE + " for b");
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
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGIDataColumnTypeConstants.X_VALUE);
    cols[1] = SGDataFileUtility.createDataColumnInfo(vars[1], SGIDataColumnTypeConstants.Y_VALUE);
    cols[2] =
        SGDataFileUtility.createDataColumnInfo(vars[2], SGIDataColumnTypeConstants.TICK_LABEL, "b");
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
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGIDataColumnTypeConstants.X_VALUE);
    cols[1] = SGDataFileUtility.createDataColumnInfo(vars[1], SGIDataColumnTypeConstants.Y_VALUE);
    cols[2] =
        SGDataFileUtility.createDataColumnInfo(
            vars[2], SGIDataColumnTypeConstants.LOWER_UPPER_ERROR_VALUE, "b");
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
        SGDataFileUtility.createDataColumnInfo(vars[0], SGIDataColumnTypeConstants.X_COMPONENT);
    cols[1] =
        SGDataFileUtility.createDataColumnInfo(vars[1], SGIDataColumnTypeConstants.Y_COMPONENT);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
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
          file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
    assertTrue(
        SGDataFileUtility.checkNetCDFDataColumns(
            cols, SGDataTypeConstants.SXY_NETCDF_DATA, file, infoMap));
  }
}
