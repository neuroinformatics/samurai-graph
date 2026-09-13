package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

/** Unit tests for {@link SGDataStrideUtility}. */
class SGDataStrideUtilityTest {

  @Test
  void calcNetCDFDefaultStrideContainsXYStride() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    SGDataColumnInfo[] columns = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("height"), SGDataColumnTypeConstants.Y_VALUE)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_NETCDF_DATA);
    Map<String, SGIntegerSeriesSet> strideMap =
        SGDataStrideUtility.calcNetCDFDefaultStride(columns, infoMap);
    assertNotNull(strideMap);
    assertNotNull(strideMap.get(SGDataInformationKeyConstants.KEY_SXY_STRIDE));
  }

  @Test
  void calcMDArrayDefaultStrideContainsXYStride() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    new MatFileWriter(path.toFile(), java.util.Arrays.asList(a, b));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGDataColumnInfo[] columns = new SGDataColumnInfo[matFile.getVariables().length];
    for (int ii = 0; ii < columns.length; ii++) {
      SGMDArrayVariable var = matFile.getVariables()[ii];
      String type = ii == 0 ? SGDataColumnTypeConstants.X_VALUE : SGDataColumnTypeConstants.Y_VALUE;
      columns[ii] = SGDataFileUtility.createDataColumnInfo(var, type);
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(
        SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA);
    Map<String, SGIntegerSeriesSet> strideMap =
        SGDataStrideUtility.calcMDArrayDefaultStride(columns, infoMap);
    assertNull(strideMap);
  }

  @Test
  void calcSDArrayDefaultStrideContainsIndexStride() {
    SGSDArrayDataColumnInfo[] columns = {
      new SGSDArrayDataColumnInfo("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, 10)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_DATA);
    Map<String, SGIntegerSeriesSet> strideMap =
        SGDataStrideUtility.calcSDArrayDefaultStride(columns, infoMap);
    assertNotNull(strideMap);
    SGIntegerSeriesSet stride = strideMap.get(SGDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
    assertNotNull(stride);
    assertEquals(0, stride.getNumbers()[0]);
    assertEquals(9, stride.getNumbers()[stride.getNumbers().length - 1]);
  }

  @Test
  void vxyComponentTypesFollowPolarSelection() {
    assertEquals(
        SGDataColumnTypeConstants.X_COMPONENT,
        SGDataStrideUtility.getVXYFirstComponentColumnType(false));
    assertEquals(
        SGDataColumnTypeConstants.Y_COMPONENT,
        SGDataStrideUtility.getVXYSecondComponentColumnType(false));
    assertEquals(
        SGDataColumnTypeConstants.MAGNITUDE,
        SGDataStrideUtility.getVXYFirstComponentColumnType(true));
    assertEquals(
        SGDataColumnTypeConstants.ANGLE, SGDataStrideUtility.getVXYSecondComponentColumnType(true));
  }

  @Test
  void vxyComponentTypesReadPolarFlagFromInfoMap() {
    Map<String, Object> polar = new HashMap<String, Object>();
    polar.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    assertEquals(
        SGDataColumnTypeConstants.MAGNITUDE,
        SGDataStrideUtility.getVXYFirstComponentColumnType(polar));
    assertEquals(
        SGDataColumnTypeConstants.ANGLE,
        SGDataStrideUtility.getVXYSecondComponentColumnType(polar));

    Map<String, Object> orthogonal = new HashMap<String, Object>();
    orthogonal.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertEquals(
        SGDataColumnTypeConstants.X_COMPONENT,
        SGDataStrideUtility.getVXYFirstComponentColumnType(orthogonal));
    assertEquals(
        SGDataColumnTypeConstants.Y_COMPONENT,
        SGDataStrideUtility.getVXYSecondComponentColumnType(orthogonal));
  }

  @Test
  void vxyComponentTypesReturnNullWithoutPolarFlag() {
    assertNull(SGDataStrideUtility.getVXYFirstComponentColumnType(new HashMap<String, Object>()));
    assertNull(SGDataStrideUtility.getVXYSecondComponentColumnType(new HashMap<String, Object>()));
  }

  @Test
  void createDefaultStepSeriesUsesUnitStepForSmallLength() {
    SGIntegerSeries series = SGDataStrideUtility.createDefaultStepSeries(3);
    assertNotNull(series);
    assertEquals(0, series.getMin());
    assertEquals(2, series.getMax());
    assertEquals(3, series.getLength());
  }

  @Test
  void createDefaultStepSeriesIncreasesStepForLargeLength() {
    SGIntegerSeries series = SGDataStrideUtility.createDefaultStepSeries(10);
    assertNotNull(series);
    assertEquals(0, series.getMin());
    assertEquals(8, series.getMax());
    assertEquals(5, series.getLength());
    assertEquals(2, series.getStep().getNumber());
  }

  @Test
  void dimensionSeriesMergesIndicesOfMultipleDimensionData() {
    SGData d1 =
        mock(SGData.class, withSettings().extraInterfaces(SGISXYMultipleDimensionData.class));
    when(((SGISXYMultipleDimensionData) d1).getIndices())
        .thenReturn(new SGIntegerSeriesSet(0, 8, 2));
    SGData d2 =
        mock(SGData.class, withSettings().extraInterfaces(SGISXYMultipleDimensionData.class));
    when(((SGISXYMultipleDimensionData) d2).getIndices())
        .thenReturn(new SGIntegerSeriesSet(1, 5, 2));
    List<SGData> dataList = new ArrayList<SGData>();
    dataList.add(d1);
    dataList.add(d2);
    SGIntegerSeriesSet set = SGDataStrideUtility.getDimensionSeries(dataList, 10);
    assertEquals(0, set.getNumbers()[0]);
    assertEquals(8, set.getNumbers()[set.getNumbers().length - 1]);
    assertEquals(8, set.getNumbers().length);
  }

  @Test
  void dimensionSeriesSkipsOtherDataTypes() {
    SGData d1 =
        mock(SGData.class, withSettings().extraInterfaces(SGISXYMultipleDimensionData.class));
    when(((SGISXYMultipleDimensionData) d1).getIndices())
        .thenReturn(new SGIntegerSeriesSet(0, 3, 3));
    List<SGData> dataList = new ArrayList<SGData>();
    dataList.add(d1);
    dataList.add(mock(SGData.class));
    SGIntegerSeriesSet set = SGDataStrideUtility.getDimensionSeries(dataList, 4);
    assertEquals(2, set.getNumbers().length);
    assertEquals(0, set.getNumbers()[0]);
    assertEquals(3, set.getNumbers()[1]);
  }

  @Test
  void dimensionSeriesMarksLastIndexAsArrayEndValues() {
    SGData d1 =
        mock(SGData.class, withSettings().extraInterfaces(SGISXYMultipleDimensionData.class));
    when(((SGISXYMultipleDimensionData) d1).getIndices())
        .thenReturn(new SGIntegerSeriesSet(0, 4, 4));
    List<SGData> dataList = new ArrayList<SGData>();
    dataList.add(d1);
    SGIntegerSeriesSet set = SGDataStrideUtility.getDimensionSeries(dataList, 5);
    assertEquals(0, set.getNumbers()[0]);
    assertEquals(4, set.getNumbers()[set.getNumbers().length - 1]);
  }

  @Test
  void getInitialMagnitudePerCMReturnsDefaultForZeroOrNaN() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.getMagnitudeArray(false)).thenReturn(new double[] {0.0, -1.0});
    assertEquals(1.0f, SGDataStrideUtility.getInitialMagnitudePerCM(data), 0.0f);
    when(data.getMagnitudeArray(false)).thenReturn(new double[] {Double.NaN});
    assertEquals(1.0f, SGDataStrideUtility.getInitialMagnitudePerCM(data), 0.0f);
  }

  @Test
  void getInitialMagnitudePerCMReturnsPositiveValue() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.getMagnitudeArray(false)).thenReturn(new double[] {1.0, 2.0, 3.0});
    final float mag = SGDataStrideUtility.getInitialMagnitudePerCM(data);
    assertTrue(mag > 0.0f);
    assertFalse(Float.isNaN(mag));
  }

  @Test
  void roundMagnitudePerCMReturnsInputForNaNOrInvalid() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.getMagnitudeArray(false)).thenReturn(new double[] {1.0, 2.0});
    assertTrue(Float.isNaN(SGDataStrideUtility.roundMagnitudePerCM(Float.NaN, data)));
    assertEquals(5.0f, SGDataStrideUtility.roundMagnitudePerCM(5.0f, data), 0.0f);
    when(data.getMagnitudeArray(false)).thenReturn(new double[] {Double.NaN});
    assertEquals(3.0f, SGDataStrideUtility.roundMagnitudePerCM(3.0f, data), 0.0f);
  }

  @Test
  void calcMDArrayDefaultStrideSXYUsesGenericDimension() {
    SGMDArrayDataColumnInfo xCol =
        SGTestMDArrayColumns.column("x", new int[] {8}, SGDataColumnTypeConstants.X_VALUE);
    xCol.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    SGMDArrayDataColumnInfo yCol =
        SGTestMDArrayColumns.column("y", new int[] {8}, SGDataColumnTypeConstants.Y_VALUE);
    yCol.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(
        SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA);
    Map<String, SGMDArrayDimensionInfo> dimNameMap = new HashMap<String, SGMDArrayDimensionInfo>();
    Map<String, SGIntegerSeriesSet> map =
        SGDataStrideUtility.calcMDArrayDefaultStride(
            new SGDataColumnInfo[] {xCol, yCol}, infoMap, dimNameMap);
    assertNotNull(map);
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_SXY_STRIDE));
    assertEquals("y", dimNameMap.get(SGDataInformationKeyConstants.KEY_SXY_STRIDE).getName());
  }

  @Test
  void calcMDArrayDefaultStrideSXYZGridUsesXAndYStrides() {
    SGMDArrayDataColumnInfo xCol =
        SGTestMDArrayColumns.column("x", new int[] {8}, SGDataColumnTypeConstants.X_VALUE);
    xCol.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    SGMDArrayDataColumnInfo yCol =
        SGTestMDArrayColumns.column("y", new int[] {8}, SGDataColumnTypeConstants.Y_VALUE);
    yCol.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(
        SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, Boolean.TRUE);
    Map<String, SGIntegerSeriesSet> map =
        SGDataStrideUtility.calcMDArrayDefaultStride(new SGDataColumnInfo[] {xCol, yCol}, infoMap);
    assertNotNull(map);
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_SXYZ_STRIDE_X));
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y));
  }

  @Test
  void calcMDArrayDefaultStrideSXYZNonGridUsesZDimension() {
    SGMDArrayDataColumnInfo zCol =
        SGTestMDArrayColumns.column("z", new int[] {8}, SGDataColumnTypeConstants.Z_VALUE);
    zCol.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(
        SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, Boolean.FALSE);
    Map<String, SGIntegerSeriesSet> map =
        SGDataStrideUtility.calcMDArrayDefaultStride(new SGDataColumnInfo[] {zCol}, infoMap);
    assertNotNull(map);
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE));
  }

  @Test
  void calcMDArrayDefaultStrideSXYZNonGridWithoutColumnsReturnsNull() {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(
        SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, Boolean.FALSE);
    assertNull(
        SGDataStrideUtility.calcMDArrayDefaultStride(
            new SGDataColumnInfo[] {SGTestMDArrayColumns.column("a", new int[] {8}, "")}, infoMap));
  }

  @Test
  void calcMDArrayDefaultStrideVXYNonGridUsesFirstComponent() {
    SGMDArrayDataColumnInfo fCol =
        SGTestMDArrayColumns.column("v1", new int[] {8}, SGDataColumnTypeConstants.MAGNITUDE);
    fCol.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(
        SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, Boolean.FALSE);
    Map<String, SGIntegerSeriesSet> map =
        SGDataStrideUtility.calcMDArrayDefaultStride(new SGDataColumnInfo[] {fCol}, infoMap);
    assertNotNull(map);
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE));
  }

  @Test
  void calcMDArrayDefaultStrideVXYGridUsesCoordinateColumns() {
    SGMDArrayDataColumnInfo xCol =
        SGTestMDArrayColumns.column("x", new int[] {5}, SGDataColumnTypeConstants.X_COORDINATE);
    xCol.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    SGMDArrayDataColumnInfo yCol =
        SGTestMDArrayColumns.column("y", new int[] {4}, SGDataColumnTypeConstants.Y_COORDINATE);
    yCol.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(
        SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, Boolean.TRUE);
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    Map<String, SGMDArrayDimensionInfo> dimNameMap = new HashMap<String, SGMDArrayDimensionInfo>();
    Map<String, SGIntegerSeriesSet> map =
        SGDataStrideUtility.calcMDArrayDefaultStride(
            new SGDataColumnInfo[] {xCol, yCol}, infoMap, dimNameMap);
    assertNotNull(map);
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_VXY_STRIDE_X));
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_VXY_STRIDE_Y));
  }

  @Test
  void calcMDArrayDefaultStrideVXYGridWithoutCoordinatesUsesComponentDimension() {
    SGMDArrayDataColumnInfo fCol =
        SGTestMDArrayColumns.column("v1", new int[] {4, 5}, SGDataColumnTypeConstants.X_COMPONENT);
    fCol.setDimensionIndex(SGMDArrayConstants.KEY_VXY_X_DIMENSION, 1);
    fCol.setDimensionIndex(SGMDArrayConstants.KEY_VXY_Y_DIMENSION, 0);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(
        SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, Boolean.TRUE);
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    Map<String, SGIntegerSeriesSet> map =
        SGDataStrideUtility.calcMDArrayDefaultStride(new SGDataColumnInfo[] {fCol}, infoMap);
    assertNotNull(map);
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_VXY_STRIDE_X));
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_VXY_STRIDE_Y));
  }

  private static SGNetCDFFile createNetCDFFile(final String path) throws Exception {
    Files.deleteIfExists(Path.of(path));
    ucar.nc2.write.NetcdfFormatWriter.Builder writer =
        ucar.nc2.write.NetcdfFormatWriter.createNewNetcdf3(path);
    ucar.nc2.Dimension xDim = writer.addDimension("x", 5);
    ucar.nc2.Dimension yDim = writer.addDimension("y", 4);
    writer.addVariable("x", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(xDim));
    writer.addVariable("y", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(yDim));
    writer.addVariable("v1", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(yDim, xDim));
    try (ucar.nc2.write.NetcdfFormatWriter ignored = writer.build()) {}
    return new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path));
  }

  @Test
  void calcNetCDFDefaultStrideSXYWithIndexUsesIndexDimension() throws Exception {
    String path = Files.createTempFile("samurai-graph-test", ".nc").toString();
    Files.deleteIfExists(Path.of(path));
    SGNetCDFFile file = createNetCDFFile(path);
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.INDEX),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGDataColumnTypeConstants.Y_VALUE)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_NETCDF_DATA);
    Map<String, String> dimNameMap = new HashMap<String, String>();
    Map<String, SGIntegerSeriesSet> map =
        SGDataStrideUtility.calcNetCDFDefaultStride(cols, infoMap, dimNameMap);
    assertNotNull(map);
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE));
    assertEquals("x", dimNameMap.get(SGDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE));
  }

  @Test
  void calcNetCDFDefaultStrideSXYZUsesBothDimensions() throws Exception {
    String path = Files.createTempFile("samurai-graph-test", ".nc").toString();
    Files.deleteIfExists(Path.of(path));
    SGNetCDFFile file = createNetCDFFile(path);
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGDataColumnTypeConstants.Y_VALUE)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXYZ_NETCDF_DATA);
    Map<String, SGIntegerSeriesSet> map =
        SGDataStrideUtility.calcNetCDFDefaultStride(cols, infoMap);
    assertNotNull(map);
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_SXYZ_STRIDE_X));
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y));
  }

  @Test
  void calcNetCDFDefaultStrideVXYWithCoordinateUsesVectorStride() throws Exception {
    String path = Files.createTempFile("samurai-graph-test", ".nc").toString();
    Files.deleteIfExists(Path.of(path));
    SGNetCDFFile file = createNetCDFFile(path);
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGDataColumnTypeConstants.X_COORDINATE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGDataColumnTypeConstants.Y_COORDINATE)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.VXY_NETCDF_DATA);
    Map<String, SGIntegerSeriesSet> map =
        SGDataStrideUtility.calcNetCDFDefaultStride(cols, infoMap);
    assertNotNull(map);
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_VXY_STRIDE_X));
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_VXY_STRIDE_Y));
  }

  @Test
  void calcSDArrayDefaultStrideUsesMaxFigureSize() {
    SGSDArrayDataColumnInfo[] columns = {
      new SGSDArrayDataColumnInfo("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, 10)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(
        SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA);
    Map<String, SGIntegerSeriesSet> map =
        SGDataStrideUtility.calcSDArrayDefaultStride(columns, infoMap);
    assertNotNull(map);
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE));
  }
}
