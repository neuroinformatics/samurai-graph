package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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
          file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_NETCDF_DATA);
    Map<String, SGIntegerSeriesSet> strideMap =
        SGDataStrideUtility.calcNetCDFDefaultStride(columns, infoMap);
    assertNotNull(strideMap);
    assertNotNull(strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE));
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
      String type =
          ii == 0 ? SGIDataColumnTypeConstants.X_VALUE : SGIDataColumnTypeConstants.Y_VALUE;
      columns[ii] = SGDataFileUtility.createDataColumnInfo(var, type);
    }
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(
        SGIDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA);
    Map<String, SGIntegerSeriesSet> strideMap =
        SGDataStrideUtility.calcMDArrayDefaultStride(columns, infoMap);
    assertNull(strideMap);
  }

  @Test
  void calcSDArrayDefaultStrideContainsIndexStride() {
    SGSDArrayDataColumnInfo[] columns = {
      new SGSDArrayDataColumnInfo("x", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, 10)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(100.0f, 80.0f));
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_DATA);
    Map<String, SGIntegerSeriesSet> strideMap =
        SGDataStrideUtility.calcSDArrayDefaultStride(columns, infoMap);
    assertNotNull(strideMap);
    SGIntegerSeriesSet stride = strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
    assertNotNull(stride);
    assertEquals(0, stride.getNumbers()[0]);
    assertEquals(9, stride.getNumbers()[stride.getNumbers().length - 1]);
  }

  @Test
  void vxyComponentTypesFollowPolarSelection() {
    assertEquals(
        SGIDataColumnTypeConstants.X_COMPONENT,
        SGDataStrideUtility.getVXYFirstComponentColumnType(false));
    assertEquals(
        SGIDataColumnTypeConstants.Y_COMPONENT,
        SGDataStrideUtility.getVXYSecondComponentColumnType(false));
    assertEquals(
        SGIDataColumnTypeConstants.MAGNITUDE,
        SGDataStrideUtility.getVXYFirstComponentColumnType(true));
    assertEquals(
        SGIDataColumnTypeConstants.ANGLE,
        SGDataStrideUtility.getVXYSecondComponentColumnType(true));
  }

  @Test
  void vxyComponentTypesReadPolarFlagFromInfoMap() {
    Map<String, Object> polar = new HashMap<String, Object>();
    polar.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    assertEquals(
        SGIDataColumnTypeConstants.MAGNITUDE,
        SGDataStrideUtility.getVXYFirstComponentColumnType(polar));
    assertEquals(
        SGIDataColumnTypeConstants.ANGLE,
        SGDataStrideUtility.getVXYSecondComponentColumnType(polar));

    Map<String, Object> orthogonal = new HashMap<String, Object>();
    orthogonal.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertEquals(
        SGIDataColumnTypeConstants.X_COMPONENT,
        SGDataStrideUtility.getVXYFirstComponentColumnType(orthogonal));
    assertEquals(
        SGIDataColumnTypeConstants.Y_COMPONENT,
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
}
