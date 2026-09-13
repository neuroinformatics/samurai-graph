package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.IOException;
import jp.riken.brain.ni.samuraigraph.base.SGData;
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
            file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGDataColumnTypeConstants.Y_VALUE);
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
        SGDataViewerUtility.getDataViewerValue(multiple, SGDataColumnTypeConstants.X_VALUE, 0, 0),
        0.0);
    assertEquals(
        1.5,
        SGDataViewerUtility.getDataViewerValue(multiple, SGDataColumnTypeConstants.Y_VALUE, 0, 0),
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
        new SGDataValueHistory.SDArray.D1(3.5, SGDataColumnTypeConstants.Y_VALUE, 2);
    assertTrue(SGDataViewerUtility.matches(2, SGDataColumnTypeConstants.Y_VALUE, value, 0.0));
    assertFalse(SGDataViewerUtility.matches(3, SGDataColumnTypeConstants.Y_VALUE, value, 0.0));
    assertFalse(
        SGDataViewerUtility.matches(2, SGDataColumnTypeConstants.LOWER_ERROR_VALUE, value, 0.0));
  }

  @Test
  void matchesChecksRowColumnAndTypeForTwoDimensions() {
    SGDataValueHistory.SDArray.D1 value =
        new SGDataValueHistory.SDArray.D1(3.5, SGDataColumnTypeConstants.Y_VALUE, 2);
    assertTrue(
        SGDataViewerUtility.matches(
            value.getRowIndex(),
            value.getColumnIndex(),
            SGDataColumnTypeConstants.Y_VALUE,
            value,
            0.0));
    assertFalse(SGDataViewerUtility.matches(2, 1, SGDataColumnTypeConstants.Y_VALUE, value, 0.0));
    assertFalse(SGDataViewerUtility.matches(2, 0, SGDataColumnTypeConstants.X_VALUE, value, 0.0));
  }

  @Test
  void setDataViewerValueReturnsHistory() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    SGISXYTypeMultipleData multiple = data.toMultiple();
    SGIntegerSeriesSet stride = new SGIntegerSeriesSet();
    stride.add(new SGIntegerSeries(0, 7, 1));
    SGDataValueHistory history =
        SGDataViewerUtility.setDataViewerValue(
            multiple, SGDataColumnTypeConstants.Y_VALUE, 0, 0, "9.5", stride);
    assertNotNull(history);
    assertEquals(9.5, history.getValue(), 0.0);
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, history.getColumnType());
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
        3, SGDataViewerUtility.getDataViewerColumnNumber(data, SGDataColumnTypeConstants.Y_VALUE));
    assertEquals(
        1, SGDataViewerUtility.getDataViewerColumnNumber(data, SGDataColumnTypeConstants.X_VALUE));
  }

  @Test
  void getDataViewerColumnNumberWithoutMultipleYValues() {
    SGISXYTypeMultipleData data = mock(SGISXYTypeMultipleData.class);
    when(data.hasMultipleYValues()).thenReturn(false);
    when(data.getChildNumber()).thenReturn(3);
    assertEquals(
        1, SGDataViewerUtility.getDataViewerColumnNumber(data, SGDataColumnTypeConstants.Y_VALUE));
    assertEquals(
        3, SGDataViewerUtility.getDataViewerColumnNumber(data, SGDataColumnTypeConstants.X_VALUE));
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
  void setEditedValueVXYGridWritesCoordinateAndComponent() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.isPolar()).thenReturn(false);
    when(data.isStrideAvailable()).thenReturn(false);
    when(data.getXStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 1));
    when(data.getYStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 1));
    double[] xValues = new double[5];
    double[] yValues = new double[5];
    double[][] fGrid = new double[7][5];
    double[][] sGrid = new double[7][5];
    SGDataValueHistory.SDArray.D1 d1 =
        new SGDataValueHistory.SDArray.D1(3.5, SGDataColumnTypeConstants.X_COORDINATE, 2);
    SGDataViewerUtility.setEditedValue(data, xValues, yValues, fGrid, sGrid, false, d1);
    assertEquals(3.5, xValues[2], 0.0);
    SGDataValueHistory.SDArray.D1 d2 =
        new SGDataValueHistory.SDArray.D1(4.5, SGDataColumnTypeConstants.X_COMPONENT, 6);
    SGDataViewerUtility.setEditedValue(data, xValues, yValues, fGrid, sGrid, false, d2);
    assertEquals(4.5, fGrid[6][0], 0.0);
  }

  @Test
  void setEditedValueVXYSingleWritesComponentAndRejectsOther() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.isPolar()).thenReturn(false);
    when(data.isStrideAvailable()).thenReturn(false);
    when(data.getIndexStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 1));
    double[] xValues = new double[5];
    double[] yValues = new double[5];
    double[] fValues = new double[5];
    double[] sValues = new double[5];
    SGDataValueHistory.SDArray.D1 d1 =
        new SGDataValueHistory.SDArray.D1(1.5, SGDataColumnTypeConstants.Y_COMPONENT, 2);
    SGDataViewerUtility.setEditedValue(data, xValues, yValues, fValues, sValues, false, d1);
    assertEquals(1.5, sValues[2], 0.0);
    SGDataValueHistory.SDArray.D1 d2 =
        new SGDataValueHistory.SDArray.D1(2.5, SGDataColumnTypeConstants.X_VALUE, 1);
    assertThrows(
        Error.class,
        () ->
            SGDataViewerUtility.setEditedValue(
                data, xValues, yValues, fValues, sValues, false, d2));
  }

  @Test
  void setEditedValueXYZGridWritesZValue() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.isStrideAvailable()).thenReturn(false);
    when(data.getXStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 1));
    when(data.getYStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 1));
    double[] xValues = new double[5];
    double[] yValues = new double[5];
    double[][] zGrid = new double[7][5];
    SGDataValueHistory.SDArray.D1 d1 =
        new SGDataValueHistory.SDArray.D1(9.5, SGDataColumnTypeConstants.Z_VALUE, 6);
    SGDataViewerUtility.setEditedValue(data, xValues, yValues, zGrid, false, d1);
    assertEquals(9.5, zGrid[6][0], 0.0);
  }

  @Test
  void setEditedValueXYZSingleWritesZValue() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.isStrideAvailable()).thenReturn(false);
    when(data.getIndexStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 1));
    double[] xValues = new double[5];
    double[] yValues = new double[5];
    double[] zValues = new double[5];
    SGDataValueHistory.SDArray.D1 d1 =
        new SGDataValueHistory.SDArray.D1(7.5, SGDataColumnTypeConstants.Z_VALUE, 3);
    SGDataViewerUtility.setEditedValue(data, xValues, yValues, zValues, false, d1);
    assertEquals(7.5, zValues[3], 0.0);
  }

  @Test
  void getDataValueXYZUsesStrideIndices() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.isIndexAvailable()).thenReturn(false);
    when(data.isStrideAvailable()).thenReturn(true);
    when(data.getXStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 2));
    when(data.getYStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 2));
    when(data.getXValueAt(1)).thenReturn(1.0);
    when(data.getYValueAt(2)).thenReturn(2.0);
    when(data.getZValueAt(4, 2)).thenReturn(3.0);
    SGDataValue value = SGDataViewerUtility.getDataValue(data, 2, 4);
    SGDataValue.SXYZDataValue sxyz = (SGDataValue.SXYZDataValue) value;
    assertEquals(1.0, sxyz.xValue, 0.0);
    assertEquals(2.0, sxyz.yValue, 0.0);
    assertEquals(3.0, sxyz.zValue.number, 0.0);
  }

  @Test
  void getDataValueVXYReturnsNullWithoutIndex() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.isIndexAvailable()).thenReturn(false);
    assertNull(SGDataViewerUtility.getDataValue(data, 0));
  }

  @Test
  void getDataValueXYSUsesIndexStride() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.isStrideAvailable()).thenReturn(true);
    when(data.isIndexAvailable()).thenReturn(true);
    when(data.getIndexStride()).thenReturn(new SGIntegerSeriesSet(0, 9, 3));
    when(data.getXValueAt(1)).thenReturn(10.0);
    when(data.getYValueAt(1)).thenReturn(20.0);
    when(data.getXValueAt(2)).thenReturn(30.0);
    when(data.getYValueAt(2)).thenReturn(40.0);
    SGDataValue value = SGDataViewerUtility.getDataValue(data, 3, 6);
    SGDataValue.SXYDoubleDataValue sxy = (SGDataValue.SXYDoubleDataValue) value;
    assertEquals(10.0, sxy.xValue0, 0.0);
    assertEquals(30.0, sxy.xValue1, 0.0);
  }

  @Test
  void getArchiveDataSetBufferPolicyDistinguishesMultipleData() {
    SGData multiple =
        mock(SGData.class, withSettings().extraInterfaces(SGISXYTypeMultipleData.class));
    assertTrue(
        SGDataViewerUtility.getArchiveDataSetBufferPolicy(multiple)
            instanceof SGSXYDataBufferPolicy);
    assertTrue(
        SGDataViewerUtility.getArchiveDataSetBufferPolicy(mock(SGData.class))
            instanceof jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy);
  }

  private static SGISXYTypeMultipleData createMultipleWithSingles(
      final double[][] y0, final double[][] y1, final double[][] x0, final double[][] x1) {
    SGISXYTypeSingleData single0 = mock(SGISXYTypeSingleData.class);
    when(single0.getYValueArray(false, true)).thenReturn(y0[0]);
    when(single0.getYValueArray(false, true, true)).thenReturn(y0[1]);
    when(single0.getYValueArray(false)).thenReturn(y0[2]);
    when(single0.getXValueArray(false, true)).thenReturn(x0[0]);
    when(single0.getXValueArray(false, true, true)).thenReturn(x0[1]);
    when(single0.getXValueArray(false)).thenReturn(x0[2]);
    SGISXYTypeSingleData single1 = mock(SGISXYTypeSingleData.class);
    when(single1.getYValueArray(false, true)).thenReturn(y1[0]);
    when(single1.getYValueArray(false, true, true)).thenReturn(y1[1]);
    when(single1.getYValueArray(false)).thenReturn(y1[2]);
    when(single1.getXValueArray(false, true)).thenReturn(x1[0]);
    when(single1.getXValueArray(false, true, true)).thenReturn(x1[1]);
    when(single1.getXValueArray(false)).thenReturn(x1[2]);
    SGISXYTypeMultipleData multiple = mock(SGISXYTypeMultipleData.class);
    when(multiple.getSXYDataArray()).thenReturn(new SGISXYTypeSingleData[] {single0, single1});
    return multiple;
  }

  @Test
  void getYValueArrayCombinesChildBuffers() {
    SGISXYTypeMultipleData data =
        createMultipleWithSingles(
            new double[][] {{1.0}, {2.0}, {3.0}},
            new double[][] {{4.0}, {5.0}, {6.0}},
            new double[][] {{7.0}, {8.0}, {9.0}},
            new double[][] {{10.0}, {11.0}, {12.0}});
    double[][] ret = SGDataViewerUtility.getYValueArray(data, false, true);
    assertArrayEquals(new double[] {1.0}, ret[0], 0.0);
    assertArrayEquals(new double[] {4.0}, ret[1], 0.0);
    double[][] ret2 = SGDataViewerUtility.getYValueArray(data, false, true, true);
    assertArrayEquals(new double[] {2.0}, ret2[0], 0.0);
    double[][] ret3 = SGDataViewerUtility.getYValueArray(data, false);
    assertArrayEquals(new double[] {3.0}, ret3[0], 0.0);
  }

  @Test
  void getXValueArrayCombinesChildBuffers() {
    SGISXYTypeMultipleData data =
        createMultipleWithSingles(
            new double[][] {{1.0}, {2.0}, {3.0}},
            new double[][] {{4.0}, {5.0}, {6.0}},
            new double[][] {{7.0}, {8.0}, {9.0}},
            new double[][] {{10.0}, {11.0}, {12.0}});
    double[][] ret = SGDataViewerUtility.getXValueArray(data, false, true);
    assertArrayEquals(new double[] {7.0}, ret[0], 0.0);
    assertArrayEquals(new double[] {10.0}, ret[1], 0.0);
    double[][] ret2 = SGDataViewerUtility.getXValueArray(data, false, true, true);
    assertArrayEquals(new double[] {8.0}, ret2[0], 0.0);
    double[][] ret3 = SGDataViewerUtility.getXValueArray(data, false);
    assertArrayEquals(new double[] {9.0}, ret3[0], 0.0);
  }

  @Test
  void syncDataValueHistoryFeedsChildData() {
    SGDataValueHistory history =
        mock(
            SGDataValueHistory.class,
            withSettings().extraInterfaces(SGDataValueHistory.IMultiple.class));
    when(((SGDataValueHistory.IMultiple) history).getChildIndex()).thenReturn(0);
    java.util.List<SGDataValueHistory> historyList = new java.util.ArrayList<SGDataValueHistory>();
    historyList.add(history);
    SGISXYTypeSingleData child = mock(SGISXYTypeSingleData.class);
    SGDataViewerUtility.syncDataValueHistory(historyList, new SGISXYTypeSingleData[] {child});
  }

  @Test
  void setEditedValueVXYGridWritesPolarComponents() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.isPolar()).thenReturn(true);
    when(data.isStrideAvailable()).thenReturn(false);
    when(data.getXStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 1));
    when(data.getYStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 1));
    double[] xValues = new double[5];
    double[] yValues = new double[5];
    double[][] fGrid = new double[7][5];
    double[][] sGrid = new double[7][5];
    SGDataValueHistory.SDArray.D1 magnitude =
        new SGDataValueHistory.SDArray.D1(8.5, SGDataColumnTypeConstants.MAGNITUDE, 6);
    SGDataViewerUtility.setEditedValue(data, xValues, yValues, fGrid, sGrid, false, magnitude);
    assertEquals(8.5, fGrid[6][0], 0.0);
    SGDataValueHistory.SDArray.D1 angle =
        new SGDataValueHistory.SDArray.D1(9.5, SGDataColumnTypeConstants.ANGLE, 6);
    SGDataViewerUtility.setEditedValue(data, xValues, yValues, fGrid, sGrid, false, angle);
    assertEquals(9.5, sGrid[6][0], 0.0);
  }

  @Test
  void arrayAccessorsOfVectorDataUseDataValues() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.useXValueCache(false)).thenReturn(false);
    when(data.useYValueCache(false)).thenReturn(false);
    when(data.useFirstComponentValueCache(false)).thenReturn(false);
    when(data.useSecondComponentValueCache(false)).thenReturn(false);
    when(data.getXValueArray(false, false)).thenReturn(new double[] {1.0, 2.0});
    when(data.getYValueArray(false, false)).thenReturn(new double[] {3.0, 4.0});
    when(data.getFirstComponentValueArray(false, false)).thenReturn(new double[] {5.0, 6.0});
    when(data.getSecondComponentValueArray(false, false)).thenReturn(new double[] {7.0, 8.0});
    assertArrayEquals(
        new double[] {1.0, 2.0}, SGDataViewerUtility.getXValueArray(data, false), 0.0);
    assertArrayEquals(
        new double[] {3.0, 4.0}, SGDataViewerUtility.getYValueArray(data, false), 0.0);
    assertArrayEquals(
        new double[] {5.0, 6.0}, SGDataViewerUtility.getFirstComponentValueArray(data, false), 0.0);
    assertArrayEquals(
        new double[] {7.0, 8.0},
        SGDataViewerUtility.getSecondComponentValueArray(data, false),
        0.0);
  }

  @Test
  void blockListAccessorsOfVectorDataUseDataValues() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.isIndexAvailable()).thenReturn(false);
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(
            new double[] {1.0, 2.0}, new SGIntegerSeries(0, 1, 1), new SGIntegerSeries(0, 0, 1));
    when(data.getFirstComponentValueBlockListSub(false, false, false))
        .thenReturn(java.util.Arrays.asList(block));
    when(data.getSecondComponentValueBlockListSub(false, false, false))
        .thenReturn(java.util.Arrays.asList(block));
    assertEquals(
        1, SGDataViewerUtility.getFirstComponentValueBlockList(data, false, false, false).size());
    assertEquals(
        1, SGDataViewerUtility.getSecondComponentValueBlockList(data, false, false, false).size());
  }

  @Test
  void arrayAccessorsOfXYZDataUseDataValues() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.useXValueCache(false)).thenReturn(false);
    when(data.useYValueCache(false)).thenReturn(false);
    when(data.useZValueCache(false)).thenReturn(false);
    when(data.getXValueArray(false, false)).thenReturn(new double[] {1.0});
    when(data.getYValueArray(false, false)).thenReturn(new double[] {2.0});
    when(data.getZValueArray(false, false)).thenReturn(new double[] {3.0});
    assertArrayEquals(new double[] {1.0}, SGDataViewerUtility.getXValueArray(data, false), 0.0);
    assertArrayEquals(new double[] {2.0}, SGDataViewerUtility.getYValueArray(data, false), 0.0);
    assertArrayEquals(new double[] {3.0}, SGDataViewerUtility.getZValueArray(data, false), 0.0);
    when(data.isIndexAvailable()).thenReturn(false);
    when(data.getZValueBlockListSub(false, false, false))
        .thenReturn(java.util.Arrays.asList(createBlock()));
    assertEquals(1, SGDataViewerUtility.getZValueBlockList(data, false, false, false).size());
  }

  private static SGXYSimpleDoubleValueIndexBlock createBlock() {
    return new SGXYSimpleDoubleValueIndexBlock(
        new double[] {1.0, 2.0}, new SGIntegerSeries(0, 1, 1), new SGIntegerSeries(0, 0, 1));
  }

  @Test
  void updateValueArraysFillsStrideGaps() {
    SGITwoDimensionalData data = mock(SGITwoDimensionalData.class);
    when(data.isStrideAvailable()).thenReturn(true);
    when(data.isIndexAvailable()).thenReturn(false);
    when(data.getXStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 2));
    when(data.getYStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 2));
    when(data.useXYCache(true, new SGIntegerSeriesSet(0, 4, 2))).thenReturn(false);
    when(data.getXValueArray(false)).thenReturn(new double[] {10.0, 20.0, 30.0});
    when(data.getYValueArray(false)).thenReturn(new double[] {40.0, 50.0, 60.0});
    double[] xValues = {0.0, 0.0, 0.0, 0.0, 0.0};
    double[] yValues = {0.0, 0.0, 0.0, 0.0, 0.0};
    double[] xRet = SGDataViewerUtility.updateXValueArray(data, true, xValues);
    double[] yRet = SGDataViewerUtility.updateYValueArray(data, true, yValues);
    assertEquals(10.0, xRet[0], 0.0);
    assertEquals(20.0, xRet[2], 0.0);
    assertEquals(30.0, xRet[4], 0.0);
    assertEquals(40.0, yRet[0], 0.0);
    assertEquals(50.0, yRet[2], 0.0);
    assertEquals(60.0, yRet[4], 0.0);
  }

  @Test
  void stringArrayAndSingleArrayAccessorsUseDataValues() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.isTickLabelAvailable()).thenReturn(true);
    when(data.useTickLabelCache(false)).thenReturn(false);
    when(data.getStringArray(false, false)).thenReturn(new String[] {"a", "b"});
    assertArrayEquals(new String[] {"a", "b"}, SGDataViewerUtility.getStringArray(data, false));
    when(data.useValueCache(false)).thenReturn(false);
    when(data.getXValueArray(false, false)).thenReturn(new double[] {1.0});
    when(data.getYValueArray(false, false)).thenReturn(new double[] {2.0});
    assertArrayEquals(new double[] {1.0}, SGDataViewerUtility.getXValueArray(data, false), 0.0);
    assertArrayEquals(new double[] {2.0}, SGDataViewerUtility.getYValueArray(data, false), 0.0);
  }

  @Test
  void setDataViewerValueMultipleCreatesHistory() {
    SGArrayData base =
        mock(SGArrayData.class, withSettings().extraInterfaces(SGISXYTypeMultipleData.class));
    SGISXYTypeMultipleData data = (SGISXYTypeMultipleData) base;
    when(base.getCache()).thenReturn(null);
    when(data.getShift()).thenReturn(new jp.riken.brain.ni.samuraigraph.base.SGTuple2d(1.0, 2.0));
    when(data.isStrideAvailable()).thenReturn(false);
    SGISXYTypeSingleData child = mock(SGISXYTypeSingleData.class);
    SGISXYTypeSingleData.DoubleValueSetResult result =
        new SGISXYTypeSingleData.DoubleValueSetResult();
    result.status = true;
    result.prev = 0.5;
    when(child.setDataViewerDoubleValue(SGDataColumnTypeConstants.X_VALUE, 0, 4.0))
        .thenReturn(result);
    when(data.getSXYDataArray()).thenReturn(new SGISXYTypeSingleData[] {child});
    SGDataValueHistory history =
        SGDataViewerUtility.setDataViewerValue(
            data, SGDataColumnTypeConstants.X_VALUE, 0, 0, "5.0", new SGIntegerSeriesSet());
    assertNotNull(history);
    assertEquals(SGDataColumnTypeConstants.X_VALUE, history.getColumnType());
    assertNull(
        SGDataViewerUtility.setDataViewerValue(
            data, SGDataColumnTypeConstants.X_VALUE, 0, 0, "abc", new SGIntegerSeriesSet()));
    assertNull(
        SGDataViewerUtility.setDataViewerValue(
            data, SGDataColumnTypeConstants.X_VALUE, 0, 0, null, new SGIntegerSeriesSet()));
  }

  @Test
  void setDataViewerValueVectorWithIndexUpdatesCache() {
    SGArrayData base =
        mock(SGArrayData.class, withSettings().extraInterfaces(SGIVXYTypeData.class));
    SGIVXYTypeData data = (SGIVXYTypeData) base;
    SGVXYDataCache cache = new SGVXYDataCache();
    cache.mXValues = new double[] {1.0, 2.0};
    cache.mYValues = new double[] {3.0, 4.0};
    cache.mFirstComponentValues = new double[] {5.0, 6.0};
    cache.mSecondComponentValues = new double[] {7.0, 8.0};
    when(base.getCache()).thenReturn(cache);
    when(data.isIndexAvailable()).thenReturn(true);
    when(data.isStrideAvailable()).thenReturn(false);
    when(data.isPolar()).thenReturn(false);
    SGDataValueHistory history =
        SGDataViewerUtility.setDataViewerValue(
            data, SGDataColumnTypeConstants.X_COORDINATE, 0, 0, "9.0");
    assertNotNull(history);
    assertEquals(9.0, cache.mXValues[0], 0.0);
    SGDataViewerUtility.setDataViewerValue(
        data, SGDataColumnTypeConstants.Y_COORDINATE, 1, 0, "4.5");
    assertEquals(4.5, cache.mYValues[1], 0.0);
    SGDataViewerUtility.setDataViewerValue(
        data, SGDataColumnTypeConstants.X_COMPONENT, 0, 0, "6.5");
    assertEquals(6.5, cache.mFirstComponentValues[0], 0.0);
    SGDataViewerUtility.setDataViewerValue(
        data, SGDataColumnTypeConstants.Y_COMPONENT, 1, 0, "8.5");
    assertEquals(8.5, cache.mSecondComponentValues[1], 0.0);
  }

  @Test
  void setDataViewerValueVectorWithoutIndexUpdatesComponentBlocks() {
    SGArrayData base =
        mock(SGArrayData.class, withSettings().extraInterfaces(SGIVXYTypeData.class));
    SGIVXYTypeData data = (SGIVXYTypeData) base;
    SGVXYDataCache cache = new SGVXYDataCache();
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(
            new double[] {
              1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
              1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0
            },
            new SGIntegerSeries(0, 4, 1),
            new SGIntegerSeries(0, 4, 1));
    java.util.List<SGXYSimpleDoubleValueIndexBlock> blockList = new java.util.ArrayList<>();
    blockList.add(block);
    cache.mFirstComponentValueBlockList = blockList;
    when(base.getCache()).thenReturn(cache);
    when(data.isIndexAvailable()).thenReturn(false);
    when(data.isStrideAvailable()).thenReturn(false);
    when(data.isPolar()).thenReturn(false);
    SGDataViewerUtility.setDataViewerValue(
        data, SGDataColumnTypeConstants.X_COMPONENT, 2, 2, "9.0");
    assertEquals(9.0, block.getValue(2, 2), 0.0);
  }

  @Test
  void getDataViewerValueVectorReadsCells() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.isStrideAvailable()).thenReturn(false);
    when(data.isPolar()).thenReturn(false);
    when(data.getXValueAt(2)).thenReturn(1.0);
    when(data.getYValueAt(3)).thenReturn(2.0);
    when(data.isIndexAvailable()).thenReturn(true);
    when(data.getFirstComponentValueAt(4)).thenReturn(3.0);
    when(data.getSecondComponentValueAt(5)).thenReturn(4.0);
    assertEquals(
        1.0,
        SGDataViewerUtility.getDataViewerValue(data, SGDataColumnTypeConstants.X_COORDINATE, 2, 9),
        0.0);
    assertEquals(
        2.0,
        SGDataViewerUtility.getDataViewerValue(data, SGDataColumnTypeConstants.Y_COORDINATE, 3, 9),
        0.0);
    assertEquals(
        3.0,
        SGDataViewerUtility.getDataViewerValue(data, SGDataColumnTypeConstants.X_COMPONENT, 4, 9),
        0.0);
    assertEquals(
        4.0,
        SGDataViewerUtility.getDataViewerValue(data, SGDataColumnTypeConstants.Y_COMPONENT, 5, 9),
        0.0);
    assertNull(
        SGDataViewerUtility.getDataViewerValue(data, SGDataColumnTypeConstants.Z_VALUE, 0, 0));
  }

  @Test
  void getDataViewerValueXYZAndMultipleReadsCells() {
    SGISXYZTypeData xyz = mock(SGISXYZTypeData.class);
    when(xyz.isStrideAvailable()).thenReturn(false);
    when(xyz.getXValueAt(1)).thenReturn(10.0);
    when(xyz.getYValueAt(2)).thenReturn(20.0);
    when(xyz.isIndexAvailable()).thenReturn(true);
    when(xyz.getZValueAt(3)).thenReturn(30.0);
    assertEquals(
        10.0,
        SGDataViewerUtility.getDataViewerValue(xyz, SGDataColumnTypeConstants.X_VALUE, 1, 9),
        0.0);
    assertEquals(
        20.0,
        SGDataViewerUtility.getDataViewerValue(xyz, SGDataColumnTypeConstants.Y_VALUE, 2, 9),
        0.0);
    assertEquals(
        30.0,
        SGDataViewerUtility.getDataViewerValue(xyz, SGDataColumnTypeConstants.Z_VALUE, 3, 9),
        0.0);

    SGISXYTypeMultipleData multiple = mock(SGISXYTypeMultipleData.class);
    when(multiple.isStrideAvailable()).thenReturn(false);
    when(multiple.getXValueAt(1, 2)).thenReturn(40.0);
    when(multiple.getYValueAt(1, 2)).thenReturn(50.0);
    when(multiple.getShift())
        .thenReturn(new jp.riken.brain.ni.samuraigraph.base.SGTuple2d(1.0, 1.0));
    assertEquals(
        41.0,
        SGDataViewerUtility.getDataViewerValue(multiple, SGDataColumnTypeConstants.X_VALUE, 2, 1),
        0.0);
    assertEquals(
        51.0,
        SGDataViewerUtility.getDataViewerValue(multiple, SGDataColumnTypeConstants.Y_VALUE, 2, 1),
        0.0);
    assertNull(
        SGDataViewerUtility.getDataViewerValue(multiple, SGDataColumnTypeConstants.Z_VALUE, 2, 1));
  }

  @Test
  void preferredDataViewColumnTypesFollowDataShape() {
    SGIVXYTypeData polar = mock(SGIVXYTypeData.class);
    when(polar.isPolar()).thenReturn(true);
    assertEquals(
        SGDataColumnTypeConstants.MAGNITUDE,
        SGDataViewerUtility.getPreferredDataViewColumnType(polar));
    SGIVXYTypeData orthogonal = mock(SGIVXYTypeData.class);
    when(orthogonal.isPolar()).thenReturn(false);
    assertEquals(
        SGDataColumnTypeConstants.X_COMPONENT,
        SGDataViewerUtility.getPreferredDataViewColumnType(orthogonal));
    assertEquals(
        SGDataColumnTypeConstants.Z_VALUE,
        SGDataViewerUtility.getPreferredDataViewColumnType(mock(SGISXYZTypeData.class)));
    SGISXYTypeMultipleData multiple = mock(SGISXYTypeMultipleData.class);
    when(multiple.hasMultipleYValues()).thenReturn(true);
    assertEquals(
        SGDataColumnTypeConstants.Y_VALUE,
        SGDataViewerUtility.getPreferredDataViewColumnType(multiple));
    when(multiple.hasMultipleYValues()).thenReturn(false);
    assertEquals(
        SGDataColumnTypeConstants.X_VALUE,
        SGDataViewerUtility.getPreferredDataViewColumnType(multiple));
  }

  @Test
  void matchesCheckRowAndColumnType() {
    SGDataValueHistory.SDArray.D1 history =
        new SGDataValueHistory.SDArray.D1(1.0, SGDataColumnTypeConstants.X_VALUE, 0);
    assertTrue(SGDataViewerUtility.matches(0, 0, SGDataColumnTypeConstants.X_VALUE, history, 1.0));
    assertFalse(SGDataViewerUtility.matches(0, 1, SGDataColumnTypeConstants.X_VALUE, history, 1.0));
  }

  @Test
  void tooltipAndStrideHelpers() {
    assertTrue(SGDataViewerUtility.isValidTooltipTextString("a"));
    assertFalse(SGDataViewerUtility.isValidTooltipTextString(""));
    assertFalse(SGDataViewerUtility.isValidTooltipTextString(null));
    SGISXYTypeData data = mock(SGISXYTypeData.class);
    when(data.isStrideAvailable()).thenReturn(false);
    assertFalse(SGDataViewerUtility.hasEffectiveStride(data));
    when(data.isStrideAvailable()).thenReturn(true);
    when(data.getStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 2));
    when(data.getTickLabelStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 2));
    assertTrue(SGDataViewerUtility.hasEffectiveStride(data));
  }

  @Test
  void getDataValueVectorAndSXYZIndexPaths() {
    SGIVXYTypeData vector = mock(SGIVXYTypeData.class);
    when(vector.isIndexAvailable()).thenReturn(false);
    when(vector.isStrideAvailable()).thenReturn(false);
    when(vector.getXValueAt(0)).thenReturn(1.0);
    when(vector.getYValueAt(0)).thenReturn(2.0);
    when(vector.getFirstComponentValueAt(0, 0)).thenReturn(3.0);
    when(vector.getSecondComponentValueAt(0, 0)).thenReturn(4.0);
    SGDataValue value = SGDataViewerUtility.getDataValue(vector, 0, 0);
    assertAboveZero(value);
    when(vector.isIndexAvailable()).thenReturn(true);
    when(vector.getFirstComponentValueAt(1)).thenReturn(5.0);
    when(vector.getSecondComponentValueAt(1)).thenReturn(6.0);
    assertNotNull(SGDataViewerUtility.getDataValue(vector, 1));

    SGISXYZTypeData xyz = mock(SGISXYZTypeData.class);
    when(xyz.isIndexAvailable()).thenReturn(false);
    when(xyz.isStrideAvailable()).thenReturn(false);
    when(xyz.getXValueAt(0)).thenReturn(1.0);
    when(xyz.getYValueAt(0)).thenReturn(2.0);
    when(xyz.getZValueAt(0, 0)).thenReturn(3.0);
    assertNotNull(SGDataViewerUtility.getDataValue(xyz, 0, 0));
  }

  private static void assertAboveZero(final SGDataValue value) {
    assertNotNull(value);
    assertEquals(1.0, ((SGDataValue.VXYDataValue) value).xValue, 0.0);
  }

  @Test
  void getEditedDataValueListIteratesColumns() {
    SGArrayData base =
        mock(SGArrayData.class, withSettings().extraInterfaces(SGISXYTypeMultipleData.class));
    SGISXYTypeMultipleData data = (SGISXYTypeMultipleData) base;
    when(base.getCache()).thenReturn(null);
    when(data.getDataViewerColumnNumber(SGDataColumnTypeConstants.X_VALUE, true)).thenReturn(1);
    when(data.getChildNumber()).thenReturn(2);
    when(data.getShift()).thenReturn(new jp.riken.brain.ni.samuraigraph.base.SGTuple2d(0.0, 0.0));
    when(data.isStrideAvailable()).thenReturn(false);
    SGISXYTypeSingleData child = mock(SGISXYTypeSingleData.class);
    SGISXYTypeSingleData.DoubleValueSetResult result =
        new SGISXYTypeSingleData.DoubleValueSetResult();
    result.status = false;
    when(child.setDataViewerDoubleValue(SGDataColumnTypeConstants.X_VALUE, 0, 3.0))
        .thenReturn(result);
    when(data.getSXYDataArray()).thenReturn(new SGISXYTypeSingleData[] {child, child});
    assertTrue(
        SGDataViewerUtility.getEditedDataValueList(
                data, SGDataColumnTypeConstants.X_VALUE, 0, 0, "3.0", new SGIntegerSeriesSet())
            .isEmpty());
  }

  @Test
  void setDataViewerValueXYZWithIndexUpdatesCache() {
    SGArrayData base =
        mock(SGArrayData.class, withSettings().extraInterfaces(SGISXYZTypeData.class));
    SGISXYZTypeData data = (SGISXYZTypeData) base;
    SGSXYZDataCache cache = new SGSXYZDataCache();
    cache.mXValues = new double[] {1.0, 2.0};
    cache.mYValues = new double[] {3.0, 4.0};
    cache.mZValues = new double[] {5.0, 6.0};
    when(base.getCache()).thenReturn(cache);
    when(data.isIndexAvailable()).thenReturn(true);
    when(data.isStrideAvailable()).thenReturn(false);
    SGDataValueHistory xHistory =
        SGDataViewerUtility.setDataViewerValue(
            data, SGDataColumnTypeConstants.X_VALUE, 0, 0, "9.0");
    assertNotNull(xHistory);
    assertEquals(9.0, cache.mXValues[0], 0.0);
    SGDataViewerUtility.setDataViewerValue(data, SGDataColumnTypeConstants.Y_VALUE, 1, 0, "4.5");
    assertEquals(4.5, cache.mYValues[1], 0.0);
    SGDataViewerUtility.setDataViewerValue(data, SGDataColumnTypeConstants.Z_VALUE, 0, 0, "6.5");
    assertEquals(6.5, cache.mZValues[0], 0.0);
  }

  @Test
  void setDataViewerValueXYZGridUpdatesZBlock() {
    SGArrayData base =
        mock(SGArrayData.class, withSettings().extraInterfaces(SGISXYZTypeData.class));
    SGISXYZTypeData data = (SGISXYZTypeData) base;
    SGSXYZDataCache cache = new SGSXYZDataCache();
    SGXYSimpleDoubleValueIndexBlock block = createBlock();
    cache.mZValueBlockList = new java.util.ArrayList<>();
    cache.mZValueBlockList.add(block);
    when(base.getCache()).thenReturn(cache);
    when(data.isIndexAvailable()).thenReturn(false);
    when(data.isStrideAvailable()).thenReturn(false);
    SGDataViewerUtility.setDataViewerValue(data, SGDataColumnTypeConstants.Z_VALUE, 0, 0, "9.0");
    assertEquals(9.0, block.getValue(0, 0), 0.0);
  }

  @Test
  void getDataViewerValueVXYUsesStrideSearch() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.isStrideAvailable()).thenReturn(true);
    when(data.getDataViewerRowStride(SGDataColumnTypeConstants.X_COORDINATE))
        .thenReturn(new SGIntegerSeriesSet(0, 8, 4));
    when(data.getDataViewerColStride(SGDataColumnTypeConstants.X_COORDINATE))
        .thenReturn(new SGIntegerSeriesSet(0, 8, 4));
    when(data.getXValueAt(1)).thenReturn(7.0);
    assertEquals(
        7.0,
        SGDataViewerUtility.getDataViewerValue(data, SGDataColumnTypeConstants.X_COORDINATE, 4, 4),
        0.0);
    assertNull(
        SGDataViewerUtility.getDataViewerValue(data, SGDataColumnTypeConstants.X_COORDINATE, 5, 0));
  }

  @Test
  void cacheBackedAccessorsUseCachedValues() {
    SGArrayData base =
        mock(SGArrayData.class, withSettings().extraInterfaces(SGIVXYTypeData.class));
    SGIVXYTypeData data = (SGIVXYTypeData) base;
    SGVXYDataCache cache = new SGVXYDataCache();
    cache.mFirstComponentValues = new double[] {1.0};
    cache.mSecondComponentValues = new double[] {2.0};
    when(base.getCache()).thenReturn(cache);
    when(data.isIndexAvailable()).thenReturn(false);
    when(data.useFirstComponentValueCache(false)).thenReturn(true);
    when(data.useSecondComponentValueCache(false)).thenReturn(true);
    assertArrayEquals(
        new double[] {1.0}, SGDataViewerUtility.getFirstComponentValueArray(data, false), 0.0);
    assertArrayEquals(
        new double[] {2.0}, SGDataViewerUtility.getSecondComponentValueArray(data, false), 0.0);
    SGXYSimpleDoubleValueIndexBlock block = createBlock();
    cache.mFirstComponentValueBlockList = new java.util.ArrayList<>();
    cache.mFirstComponentValueBlockList.add(block);
    cache.mSecondComponentValueBlockList = new java.util.ArrayList<>();
    cache.mSecondComponentValueBlockList.add(block);
    assertEquals(
        1, SGDataViewerUtility.getFirstComponentValueBlockList(data, false, true, false).size());
    assertEquals(
        1, SGDataViewerUtility.getSecondComponentValueBlockList(data, false, true, false).size());
  }

  @Test
  void setEditedValueWithAllUsesStrideIndices() {
    SGIVXYTypeData vxy = mock(SGIVXYTypeData.class);
    when(vxy.isPolar()).thenReturn(false);
    when(vxy.isStrideAvailable()).thenReturn(true);
    when(vxy.getXStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 2));
    when(vxy.getYStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 2));
    when(vxy.getIndexStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 2));
    double[] xValues = new double[5];
    double[] yValues = new double[5];
    double[][] fGrid = new double[5][5];
    double[][] sGrid = new double[5][5];
    SGDataValueHistory.SDArray.D1 xValue =
        new SGDataValueHistory.SDArray.D1(1.5, SGDataColumnTypeConstants.X_COORDINATE, 2);
    SGDataViewerUtility.setEditedValue(vxy, xValues, yValues, fGrid, sGrid, true, xValue);
    assertEquals(1.5, xValues[2], 0.0);
    double[] fValues = new double[5];
    double[] sValues = new double[5];
    SGDataValueHistory.SDArray.D1 first =
        new SGDataValueHistory.SDArray.D1(2.5, SGDataColumnTypeConstants.X_COMPONENT, 3);
    SGDataViewerUtility.setEditedValue(vxy, xValues, yValues, fValues, sValues, true, first);
    assertEquals(2.5, fValues[3], 0.0);
    assertThrows(
        Error.class,
        () ->
            SGDataViewerUtility.setEditedValue(
                vxy,
                xValues,
                yValues,
                fValues,
                sValues,
                true,
                new SGDataValueHistory.SDArray.D1(3.5, SGDataColumnTypeConstants.Z_VALUE, 0)));

    SGISXYZTypeData xyz = mock(SGISXYZTypeData.class);
    when(xyz.isStrideAvailable()).thenReturn(true);
    when(xyz.getXStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 2));
    when(xyz.getYStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 2));
    when(xyz.getIndexStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 2));
    double[][] zGrid = new double[5][5];
    SGDataValueHistory.SDArray.D1 zValue =
        new SGDataValueHistory.SDArray.D1(4.5, SGDataColumnTypeConstants.Z_VALUE, 4);
    SGDataViewerUtility.setEditedValue(xyz, new double[5], new double[5], zGrid, true, zValue);
    assertEquals(4.5, zGrid[4][0], 0.0);
    assertThrows(
        Error.class,
        () ->
            SGDataViewerUtility.setEditedValue(
                xyz,
                new double[5],
                new double[5],
                new double[5],
                true,
                new SGDataValueHistory.SDArray.D1(1.0, SGDataColumnTypeConstants.X_COORDINATE, 0)));
  }

  @Test
  void hasEffectiveStrideHandlesMissingStride() {
    SGISXYTypeData data = mock(SGISXYTypeData.class);
    when(data.isStrideAvailable()).thenReturn(true);
    when(data.getStride()).thenReturn(null);
    when(data.getTickLabelStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 1));
    assertThrows(Error.class, () -> SGDataViewerUtility.hasEffectiveStride(data));
    when(data.getStride()).thenReturn(new SGIntegerSeriesSet(0, 4, 2));
    assertTrue(SGDataViewerUtility.hasEffectiveStride(data));
  }

  @Test
  void cacheBackedXYZAccessorsUseCachedValues() {
    SGArrayData base =
        mock(SGArrayData.class, withSettings().extraInterfaces(SGISXYZTypeData.class));
    SGISXYZTypeData data = (SGISXYZTypeData) base;
    SGSXYZDataCache cache = new SGSXYZDataCache();
    cache.mZValues = new double[] {1.0};
    cache.mXValues = new double[] {2.0};
    cache.mYValues = new double[] {3.0};
    SGXYSimpleDoubleValueIndexBlock block = createBlock();
    cache.mZValueBlockList = new java.util.ArrayList<>();
    cache.mZValueBlockList.add(block);
    when(base.getCache()).thenReturn(cache);
    when(data.isIndexAvailable()).thenReturn(false);
    when(data.useZValueCache(false)).thenReturn(true);
    when(data.useXValueCache(false)).thenReturn(true);
    when(data.useYValueCache(false)).thenReturn(true);
    assertArrayEquals(new double[] {1.0}, SGDataViewerUtility.getZValueArray(data, false), 0.0);
    assertEquals(1, SGDataViewerUtility.getZValueBlockList(data, false, true, false).size());
    assertArrayEquals(new double[] {2.0}, SGDataViewerUtility.getXValueArray(data, false), 0.0);
    assertArrayEquals(new double[] {3.0}, SGDataViewerUtility.getYValueArray(data, false), 0.0);
  }

  @Test
  void stringArrayReturnsNullWithoutTickLabels() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.isTickLabelAvailable()).thenReturn(false);
    assertNull(SGDataViewerUtility.getStringArray(data, false));
  }

  @Test
  void getEditedDataValueListUsesTargetColumn() {
    SGArrayData base =
        mock(SGArrayData.class, withSettings().extraInterfaces(SGISXYTypeMultipleData.class));
    SGISXYTypeMultipleData data = (SGISXYTypeMultipleData) base;
    when(base.getCache()).thenReturn(null);
    when(data.getDataViewerColumnNumber(SGDataColumnTypeConstants.X_VALUE, true)).thenReturn(2);
    when(data.getShift()).thenReturn(new jp.riken.brain.ni.samuraigraph.base.SGTuple2d(0.0, 0.0));
    when(data.isStrideAvailable()).thenReturn(false);
    SGISXYTypeSingleData child = mock(SGISXYTypeSingleData.class);
    SGISXYTypeSingleData.DoubleValueSetResult result =
        new SGISXYTypeSingleData.DoubleValueSetResult();
    result.status = true;
    result.prev = 0.25;
    when(child.setDataViewerDoubleValue(SGDataColumnTypeConstants.X_VALUE, 0, 3.0))
        .thenReturn(result);
    when(data.getSXYDataArray()).thenReturn(new SGISXYTypeSingleData[] {child});
    assertEquals(
        1,
        SGDataViewerUtility.getEditedDataValueList(
                data, SGDataColumnTypeConstants.X_VALUE, 0, 0, "3.0", new SGIntegerSeriesSet())
            .size());
  }
}
