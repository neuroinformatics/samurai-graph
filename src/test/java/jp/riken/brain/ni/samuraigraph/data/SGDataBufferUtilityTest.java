package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

/** Unit tests for {@link SGDataBufferUtility}. */
class SGDataBufferUtilityTest {

  private SGSXYNetCDFData createSXYDataFromExample16() throws IOException {
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
    return new SGSXYNetCDFData(
        file,
        new SGDataSourceObserver(),
        xInfo,
        yInfo,
        leInfo,
        ueInfo,
        yInfo,
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
  void getDateValueArrayExtractsDateValues() {
    SGDate[] dates = {new SGDate(0.5), new SGDate(1.25), new SGDate(2.0)};
    double[] values = SGDataBufferUtility.getDateValueArray(dates);
    assertArrayEquals(new double[] {0.5, 1.25, 2.0}, values, 0.0);
  }

  @Test
  void getDataBufferBuildsBufferWithValues() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    SGSXYDataBuffer buffer =
        (SGSXYDataBuffer)
            SGDataBufferUtility.getDataBuffer(
                data, new SGSXYDataBufferPolicy(true, false, false, false, true));
    assertNotNull(buffer);
    assertNotNull(buffer.getXValues());
    assertNotNull(buffer.getYValues());
    assertEquals(8, buffer.getXValues().length);
    assertEquals(8, buffer.getYValues().length);
    assertEquals(15.0, buffer.getXValues()[7], 0.0);
  }

  @Test
  void getLowerErrorValueArrayReadsErrorVariable() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    double[] lower = SGDataBufferUtility.getLowerErrorValueArray(data, false);
    double[] upper = SGDataBufferUtility.getUpperErrorValueArray(data, false);
    assertNotNull(lower);
    assertNotNull(upper);
    assertEquals(8, lower.length);
    assertEquals(8, upper.length);
  }

  @Test
  void getTwoDimensionalValuesFillsGridFromBlocks() {
    SGXYSimpleDoubleValueIndexBlock blockA =
        new SGXYSimpleDoubleValueIndexBlock(
            new double[] {1.0, 2.0, 3.0, 4.0},
            new SGIntegerSeries(0, 1, 1),
            new SGIntegerSeries(0, 2, 2));
    SGXYSimpleDoubleValueIndexBlock blockB =
        new SGXYSimpleDoubleValueIndexBlock(
            new double[] {5.0, 6.0}, new SGIntegerSeries(0, 2, 2), new SGIntegerSeries(2, 2, 1));
    double[][] values =
        SGDataBufferUtility.getTwoDimensionalValues(
            Arrays.asList(blockA, blockB), Arrays.asList(0, 1, 2), Arrays.asList(0, 2));
    assertNotNull(values);
    assertEquals(2, values.length);
    assertEquals(3, values[0].length);
    assertArrayEquals(new double[] {1.0, 2.0, Double.NaN}, values[0], 0.0);
    assertArrayEquals(new double[] {5.0, 4.0, 6.0}, values[1], 0.0);
  }

  @Test
  void getTwoDimensionalValuesReturnsNullForMissingIndex() {
    SGXYSimpleDoubleValueIndexBlock block =
        new SGXYSimpleDoubleValueIndexBlock(
            new double[] {1.0, 2.0}, new SGIntegerSeries(0, 9, 9), new SGIntegerSeries(0, 0, 1));
    double[][] values =
        SGDataBufferUtility.getTwoDimensionalValues(
            Arrays.asList(block), Arrays.asList(0), Arrays.asList(0));
    assertNull(values);
  }

  @Test
  void getIndexListCollectsSeriesIndices() {
    SGXYSimpleDoubleValueIndexBlock blockA =
        new SGXYSimpleDoubleValueIndexBlock(
            new double[] {1.0, 2.0, 3.0, 4.0},
            new SGIntegerSeries(0, 1, 1),
            new SGIntegerSeries(0, 2, 2));
    SGXYSimpleDoubleValueIndexBlock blockB =
        new SGXYSimpleDoubleValueIndexBlock(
            new double[] {5.0, 6.0}, new SGIntegerSeries(0, 2, 2), new SGIntegerSeries(2, 2, 1));
    List<Integer> xIndexList = new ArrayList<Integer>();
    List<Integer> yIndexList = new ArrayList<Integer>();
    SGDataBufferUtility.getIndexList(Arrays.asList(blockA, blockB), xIndexList, yIndexList, 3, 3);
    assertEquals(Arrays.asList(0, 1, 2), xIndexList);
    assertEquals(Arrays.asList(0, 2), yIndexList);
  }

  @Test
  void getValueTableBuildsVXYTableFromBuffer() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.getDataBuffer(
            org.mockito.ArgumentMatchers.any(
                jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy.class)))
        .thenReturn(
            new SGVXYDataBuffer(
                new double[] {1.0, 2.0},
                new double[] {3.0, 4.0},
                new double[] {5.0, 6.0},
                new double[] {7.0, 8.0},
                false));
    Object[][] table =
        SGDataBufferUtility.getValueTable(data, null, new SGDataBufferPolicy(false, false, false));
    assertNotNull(table);
    assertEquals(2, table.length);
    assertEquals(1.0, (Double) table[0][0], 0.0);
    assertEquals(3.0, (Double) table[0][1], 0.0);
    assertEquals(5.0, (Double) table[0][2], 0.0);
    assertEquals(7.0, (Double) table[0][3], 0.0);
  }

  @Test
  void getValueTableBuildsSXYZTableFromBuffer() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getDataBuffer(
            org.mockito.ArgumentMatchers.any(
                jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy.class)))
        .thenReturn(
            new SGSXYZDataBuffer(
                new double[] {1.0, 2.0}, new double[] {3.0, 4.0}, new double[] {5.0, 6.0}));
    Object[][] table =
        SGDataBufferUtility.getValueTable(data, null, new SGDataBufferPolicy(false, false, false));
    assertNotNull(table);
    assertEquals(2, table.length);
    assertEquals(1.0, (Double) table[0][0], 0.0);
    assertEquals(3.0, (Double) table[0][1], 0.0);
    assertEquals(5.0, (Double) table[0][2], 0.0);
  }

  @Test
  void getValueTableReturnsNullWithoutBuffer() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.getDataBuffer(
            org.mockito.ArgumentMatchers.any(
                jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy.class)))
        .thenReturn(null);
    assertNull(
        SGDataBufferUtility.getValueTable(data, null, new SGDataBufferPolicy(false, false, false)));
  }

  @Test
  void getValueTableReturnsNullForOtherBufferType() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.getDataBuffer(
            org.mockito.ArgumentMatchers.any(
                jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy.class)))
        .thenReturn(mock(jp.riken.brain.ni.samuraigraph.base.SGDataBuffer.class));
    assertNull(
        SGDataBufferUtility.getValueTable(data, null, new SGDataBufferPolicy(false, false, false)));
  }

  @Test
  void getValueTableSXYZReturnsNullWithoutBuffer() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getDataBuffer(
            org.mockito.ArgumentMatchers.any(
                jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy.class)))
        .thenReturn(null);
    assertNull(
        SGDataBufferUtility.getValueTable(data, null, new SGDataBufferPolicy(false, false, false)));
  }

  @Test
  void getValueTableSXYZReturnsNullForOtherBufferType() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getDataBuffer(
            org.mockito.ArgumentMatchers.any(
                jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy.class)))
        .thenReturn(mock(jp.riken.brain.ni.samuraigraph.base.SGDataBuffer.class));
    assertNull(
        SGDataBufferUtility.getValueTable(data, null, new SGDataBufferPolicy(false, false, false)));
  }

  @Test
  void getLowerErrorValueArrayUsesDataValues() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.isErrorBarAvailable()).thenReturn(true);
    when(data.useValueCache(false)).thenReturn(false);
    when(data.getLowerErrorValueArray(false, false)).thenReturn(new double[] {1.0, 2.0});
    assertArrayEquals(
        new double[] {1.0, 2.0}, SGDataBufferUtility.getLowerErrorValueArray(data, false), 0.0);
  }

  @Test
  void getUpperErrorValueArrayReturnsNullWithoutErrorBar() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.isErrorBarAvailable()).thenReturn(false);
    assertNull(SGDataBufferUtility.getUpperErrorValueArray(data, false));
  }

  @Test
  void getUpperErrorValueArrayUsesDataValues() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.isErrorBarAvailable()).thenReturn(true);
    when(data.useValueCache(false)).thenReturn(false);
    when(data.getUpperErrorValueArray(false, false)).thenReturn(new double[] {3.0, 4.0});
    assertArrayEquals(
        new double[] {3.0, 4.0}, SGDataBufferUtility.getUpperErrorValueArray(data, false), 0.0);
  }
}
