package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

  @Test
  void getDataViewerColumnNumberUsesMultipleYValues() {
    SGISXYTypeMultipleData data = mock(SGISXYTypeMultipleData.class);
    when(data.hasMultipleYValues()).thenReturn(true);
    when(data.getChildNumber()).thenReturn(3);
    assertEquals(
        3, SGDataViewerUtility.getDataViewerColumnNumber(data, SGIDataColumnTypeConstants.Y_VALUE));
    assertEquals(
        1, SGDataViewerUtility.getDataViewerColumnNumber(data, SGIDataColumnTypeConstants.X_VALUE));
  }

  @Test
  void getDataViewerColumnNumberWithoutMultipleYValues() {
    SGISXYTypeMultipleData data = mock(SGISXYTypeMultipleData.class);
    when(data.hasMultipleYValues()).thenReturn(false);
    when(data.getChildNumber()).thenReturn(3);
    assertEquals(
        1, SGDataViewerUtility.getDataViewerColumnNumber(data, SGIDataColumnTypeConstants.Y_VALUE));
    assertEquals(
        3, SGDataViewerUtility.getDataViewerColumnNumber(data, SGIDataColumnTypeConstants.X_VALUE));
  }

  @Test
  void getDataValueXYZReadsValuesWithoutStride() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.isIndexAvailable()).thenReturn(false);
    when(data.isStrideAvailable()).thenReturn(false);
    when(data.getXValueAt(2)).thenReturn(1.0);
    when(data.getYValueAt(1)).thenReturn(2.0);
    when(data.getZValueAt(1, 2)).thenReturn(3.0);
    SGDataValue value = SGDataViewerUtility.getDataValue(data, 2, 1);
    assertNotNull(value);
    SGDataValue.SXYZDataValue sxyz = (SGDataValue.SXYZDataValue) value;
    assertEquals(1.0, sxyz.xValue, 0.0);
    assertEquals(2.0, sxyz.yValue, 0.0);
    assertEquals(3.0, sxyz.zValue.number, 0.0);
  }

  @Test
  void getDataValueXYZReturnsNullWhenIndexAvailable() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.isIndexAvailable()).thenReturn(true);
    assertNull(SGDataViewerUtility.getDataValue(data, 0, 0));
  }

  @Test
  void getDataValueXYSUsesStrideIndices() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.isStrideAvailable()).thenReturn(true);
    when(data.isIndexAvailable()).thenReturn(false);
    when(data.getStride()).thenReturn(new SGIntegerSeriesSet(0, 9, 3));
    when(data.getXValueAt(1)).thenReturn(10.0);
    when(data.getYValueAt(1)).thenReturn(20.0);
    when(data.getXValueAt(2)).thenReturn(30.0);
    when(data.getYValueAt(2)).thenReturn(40.0);
    SGDataValue value = SGDataViewerUtility.getDataValue(data, 3, 6);
    assertNotNull(value);
    SGDataValue.SXYDoubleDataValue sxy = (SGDataValue.SXYDoubleDataValue) value;
    assertEquals(10.0, sxy.xValue0, 0.0);
    assertEquals(20.0, sxy.yValue0, 0.0);
    assertEquals(30.0, sxy.xValue1, 0.0);
    assertEquals(40.0, sxy.yValue1, 0.0);
  }

  @Test
  void getDataValueVXYReadsComponentsWithoutStride() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.isIndexAvailable()).thenReturn(false);
    when(data.isStrideAvailable()).thenReturn(false);
    when(data.getXValueAt(1)).thenReturn(1.0);
    when(data.getYValueAt(3)).thenReturn(2.0);
    when(data.getFirstComponentValueAt(3, 1)).thenReturn(3.0);
    when(data.getSecondComponentValueAt(3, 1)).thenReturn(4.0);
    SGDataValue value = SGDataViewerUtility.getDataValue(data, 1, 3);
    assertNotNull(value);
    SGDataValue.VXYDataValue vxy = (SGDataValue.VXYDataValue) value;
    assertEquals(1.0, vxy.xValue, 0.0);
    assertEquals(2.0, vxy.yValue, 0.0);
    assertEquals(3.0, vxy.fValue.number, 0.0);
    assertEquals(4.0, vxy.sValue.number, 0.0);
  }
}
