package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

/** Unit tests for {@link SGDataViewerUtility}. */
class SGDataViewerUtilityTest {

  private SGSXYNetCDFData createSXYDataFromExample16() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE);
    return new SGSXYNetCDFData(
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
  }

  @Test
  void getXValueArrayReadsCoordinateSlice() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    double[] xValues = SGDataViewerUtility.getXValueArray(data, false);
    assertNotNull(xValues);
    assertEquals(8, xValues.length);
    assertEquals(1.0, xValues[0], 0.0);
  }

  @Test
  void getYValueArrayReadsTwoDimensionalVariable() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    double[] yValues = SGDataViewerUtility.getYValueArray(data, false);
    assertNotNull(yValues);
    assertEquals(8, yValues.length);
    assertEquals(1.5, yValues[0], 0.0);
    assertEquals(3.5, yValues[1], 0.0);
  }

  @Test
  void getDataViewerValueReadsCoordinateValues() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    SGISXYTypeMultipleData multiple = data.toMultiple();
    assertEquals(
        1.0,
        SGDataViewerUtility.getDataViewerValue(multiple, SGIDataColumnTypeConstants.X_VALUE, 0, 0),
        0.0);
    assertEquals(
        1.5,
        SGDataViewerUtility.getDataViewerValue(multiple, SGIDataColumnTypeConstants.Y_VALUE, 0, 0),
        0.0);
  }

  @Test
  void getCoordinateVariableValueReturnsInRangeValue() {
    double[] array = {1.0, 2.5, 4.0};
    assertEquals(1.0, SGDataViewerUtility.getCoordinateVariableValue(array, 0), 0.0);
    assertEquals(2.5, SGDataViewerUtility.getCoordinateVariableValue(array, 1), 0.0);
    assertEquals(4.0, SGDataViewerUtility.getCoordinateVariableValue(array, 2), 0.0);
  }

  @Test
  void matchesChecksRowAndColumnTypeForSingleDimension() {
    SGDataValueHistory.SDArray.D1 value =
        new SGDataValueHistory.SDArray.D1(3.5, SGIDataColumnTypeConstants.Y_VALUE, 2);
    assertTrue(SGDataViewerUtility.matches(2, SGIDataColumnTypeConstants.Y_VALUE, value, 0.0));
    assertFalse(SGDataViewerUtility.matches(3, SGIDataColumnTypeConstants.Y_VALUE, value, 0.0));
    assertFalse(
        SGDataViewerUtility.matches(2, SGIDataColumnTypeConstants.LOWER_ERROR_VALUE, value, 0.0));
  }

  @Test
  void matchesChecksRowColumnAndTypeForTwoDimensions() {
    SGDataValueHistory.SDArray.D1 value =
        new SGDataValueHistory.SDArray.D1(3.5, SGIDataColumnTypeConstants.Y_VALUE, 2);
    assertTrue(
        SGDataViewerUtility.matches(
            value.getRowIndex(),
            value.getColumnIndex(),
            SGIDataColumnTypeConstants.Y_VALUE,
            value,
            0.0));
    assertFalse(SGDataViewerUtility.matches(2, 1, SGIDataColumnTypeConstants.Y_VALUE, value, 0.0));
    assertFalse(SGDataViewerUtility.matches(2, 0, SGIDataColumnTypeConstants.X_VALUE, value, 0.0));
  }

  @Test
  void setDataViewerValueReturnsHistory() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    SGISXYTypeMultipleData multiple = data.toMultiple();
    SGIntegerSeriesSet stride = new SGIntegerSeriesSet();
    stride.add(new SGIntegerSeries(0, 7, 1));
    SGDataValueHistory history =
        SGDataViewerUtility.setDataViewerValue(
            multiple, SGIDataColumnTypeConstants.Y_VALUE, 0, 0, "9.5", stride);
    assertNotNull(history);
    assertEquals(9.5, history.getValue(), 0.0);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, history.getColumnType());
  }

  @Test
  void updateCacheRestoresSingleDataCaches() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    SGISXYTypeMultipleData multiple = data.toMultiple();
    SGDataViewerUtility.updateCache(multiple, new SGISXYTypeSingleData[] {data});
    double[] xValues = SGDataViewerUtility.getXValueArray(data, false);
    assertNotNull(xValues);
    assertEquals(8, xValues.length);
  }
}
