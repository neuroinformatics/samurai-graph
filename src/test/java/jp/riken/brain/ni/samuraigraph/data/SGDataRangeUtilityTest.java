package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

/** Unit tests for {@link SGDataRangeUtility}. */
class SGDataRangeUtilityTest {

  @Test
  void getMinValueIgnoresNonFiniteValues() {
    assertEquals(2.0, SGDataRangeUtility.getMinValue(new double[] {Double.NaN, 5.0, 2.0}), 0.0);
    assertEquals(
        Double.NaN,
        SGDataRangeUtility.getMinValue(new double[] {Double.NaN, Double.POSITIVE_INFINITY}),
        0.0);
  }

  @Test
  void getMaxValueIgnoresNonFiniteValues() {
    assertEquals(5.0, SGDataRangeUtility.getMaxValue(new double[] {5.0, Double.NaN, 2.0}), 0.0);
    assertEquals(Double.NaN, SGDataRangeUtility.getMaxValue(new double[] {}), 0.0);
  }

  @Test
  void getBoundsReturnsMinMaxRange() {
    SGValueRange range = SGDataRangeUtility.getBounds(new double[] {3.0, -1.0, 2.0});
    assertEquals(-1.0, range.getMinValue(), 0.0);
    assertEquals(3.0, range.getMaxValue(), 0.0);
  }

  @Test
  void getMinValueOfRangeListReturnsSmallestValidMin() {
    assertEquals(
        1.0,
        SGDataRangeUtility.getMinValue(
            Arrays.asList(new SGValueRange(1.0, 2.0), new SGValueRange(5.0, 8.0))),
        0.0);
  }

  @Test
  void getMinValueOfRangeListIgnoringInvalidRanges() {
    assertEquals(
        3.0,
        SGDataRangeUtility.getMinValue(
            Arrays.asList(new SGValueRange(Double.NaN, 2.0), new SGValueRange(3.0, 4.0))),
        0.0);
  }

  @Test
  void getMinValueOfEmptyRangeListReturnsNull() {
    assertNull(SGDataRangeUtility.getMinValue(Collections.emptyList()));
  }

  @Test
  void getMaxValueOfRangeListReturnsLargestValidMax() {
    assertEquals(
        8.0,
        SGDataRangeUtility.getMaxValue(
            Arrays.asList(new SGValueRange(1.0, 2.0), new SGValueRange(5.0, 8.0))),
        0.0);
  }

  @Test
  void getBoundsOfCoordinateValuesFromNetCDFData() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
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
    SGValueRange xRange = SGDataRangeUtility.getBoundsX(data);
    assertTrue(xRange.isMinValid());
    assertTrue(xRange.isMaxValid());
    assertEquals(1.0, xRange.getMinValue(), 0.0);
    assertEquals(15.0, xRange.getMaxValue(), 0.0);
    SGValueRange yRange = SGDataRangeUtility.getBoundsY(data);
    assertTrue(yRange.isMinValid());
    assertTrue(yRange.isMaxValid());
    assertEquals(1.5, yRange.getMinValue(), 0.0);
    assertEquals(15.5, yRange.getMaxValue(), 0.0);
  }

  @Test
  void getBoundsOfMultipleData() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
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
    SGValueRange xRange = SGDataRangeUtility.getBoundsX(data.toMultiple());
    assertEquals(1.0, xRange.getMinValue(), 0.0);
    assertEquals(15.0, xRange.getMaxValue(), 0.0);
    SGValueRange yRange = SGDataRangeUtility.getBoundsY(data.toMultiple());
    assertEquals(1.5, yRange.getMinValue(), 0.0);
    assertEquals(15.5, yRange.getMaxValue(), 0.0);
  }

  @Test
  void animationFrameBoundsFallBackToDataBounds() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
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
    SGValueRange expected = SGDataRangeUtility.getBoundsX(data);
    SGValueRange actual = SGDataRangeUtility.getAllAnimationFrameBoundsX(data);
    assertEquals(expected.getMinValue(), actual.getMinValue(), 0.0);
    assertEquals(expected.getMaxValue(), actual.getMaxValue(), 0.0);
  }

  @Test
  void getMinValueOfAllInvalidRangeListReturnsNull() {
    assertNull(
        SGDataRangeUtility.getMinValue(
            Arrays.asList(
                new SGValueRange(Double.NaN, Double.NaN),
                new SGValueRange(Double.NaN, Double.NaN))));
  }

  @Test
  void getMaxValueOfAllInvalidRangeListReturnsNull() {
    assertNull(
        SGDataRangeUtility.getMaxValue(
            Arrays.asList(
                new SGValueRange(Double.NaN, Double.NaN),
                new SGValueRange(Double.NaN, Double.NaN))));
  }

  @Test
  void getBoundsOfInvalidArrayMarksRangeInvalid() {
    SGValueRange range =
        SGDataRangeUtility.getBounds(new double[] {Double.NaN, Double.POSITIVE_INFINITY});
    assertTrue(Double.isNaN(range.getMinValue()));
    assertTrue(Double.isNaN(range.getMaxValue()));
    assertTrue(range.isMinValid() == false);
    assertTrue(range.isMaxValid() == false);
  }

  @Test
  void getBoundsXOfSingleDataIncludesErrorBars() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0, 5.0});
    when(data.isErrorBarAvailable()).thenReturn(true);
    when(data.isErrorBarVertical()).thenReturn(false);
    when(data.getLowerErrorValueArray(false)).thenReturn(new double[] {1.0, 1.0});
    when(data.getUpperErrorValueArray(false)).thenReturn(new double[] {1.0, 1.0});
    SGValueRange range = SGDataRangeUtility.getBoundsX(data);
    assertEquals(0.0, range.getMinValue(), 0.0);
    assertEquals(6.0, range.getMaxValue(), 0.0);
  }

  @Test
  void getBoundsYOfSingleDataIncludesVerticalErrorBars() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getYValueArray(false)).thenReturn(new double[] {2.0, 8.0});
    when(data.isErrorBarAvailable()).thenReturn(true);
    when(data.isErrorBarVertical()).thenReturn(true);
    when(data.getLowerErrorValueArray(false)).thenReturn(new double[] {1.0, 1.0});
    when(data.getUpperErrorValueArray(false)).thenReturn(new double[] {2.0, 2.0});
    SGValueRange range = SGDataRangeUtility.getBoundsY(data);
    assertEquals(1.0, range.getMinValue(), 0.0);
    assertEquals(10.0, range.getMaxValue(), 0.0);
  }

  @Test
  void getBoundsOfSingleDataReturnsNullWithoutValues() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getXValueArray(false)).thenReturn(null);
    assertNull(SGDataRangeUtility.getBoundsX(data));
    when(data.getYValueArray(false)).thenReturn(null);
    assertNull(SGDataRangeUtility.getBoundsY(data));
  }

  @Test
  void getBoundsOfMultipleDataAggregatesChildren() {
    SGISXYTypeSingleData child = mock(SGISXYTypeSingleData.class);
    when(child.getXValueArray(false)).thenReturn(new double[] {1.0, 3.0});
    when(child.getYValueArray(false)).thenReturn(new double[] {2.0, 4.0});
    when(child.isErrorBarAvailable()).thenReturn(false);
    SGISXYTypeMultipleData data = mock(SGISXYTypeMultipleData.class);
    when(data.getSXYDataArray()).thenReturn(new SGISXYTypeSingleData[] {child});
    SGValueRange xRange = SGDataRangeUtility.getBoundsX(data);
    assertEquals(1.0, xRange.getMinValue(), 0.0);
    assertEquals(3.0, xRange.getMaxValue(), 0.0);
    SGValueRange yRange = SGDataRangeUtility.getBoundsY(data);
    assertEquals(2.0, yRange.getMinValue(), 0.0);
    assertEquals(4.0, yRange.getMaxValue(), 0.0);
  }

  @Test
  void getBoundsOfVectorDataUsesComponentValues() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.getXValueArray(false)).thenReturn(new double[] {5.0, 1.0});
    when(data.getYValueArray(false)).thenReturn(new double[] {3.0, 7.0});
    SGValueRange xRange = SGDataRangeUtility.getBoundsX(data);
    assertEquals(1.0, xRange.getMinValue(), 0.0);
    assertEquals(5.0, xRange.getMaxValue(), 0.0);
    SGValueRange yRange = SGDataRangeUtility.getBoundsY(data);
    assertEquals(3.0, yRange.getMinValue(), 0.0);
    assertEquals(7.0, yRange.getMaxValue(), 0.0);
    when(data.getXValueArray(false)).thenReturn(null);
    assertNull(SGDataRangeUtility.getBoundsX(data));
  }

  @Test
  void getBoundsOfXYZDataUsesComponentValues() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getXValueArray(false)).thenReturn(new double[] {5.0});
    when(data.getYValueArray(false)).thenReturn(new double[] {3.0});
    when(data.getZValueArray(false)).thenReturn(new double[] {9.0});
    assertEquals(5.0, SGDataRangeUtility.getBoundsX(data).getMaxValue(), 0.0);
    assertEquals(3.0, SGDataRangeUtility.getBoundsY(data).getMaxValue(), 0.0);
    assertEquals(9.0, SGDataRangeUtility.getBoundsZ(data).getMaxValue(), 0.0);
    when(data.getZValueArray(false)).thenReturn(null);
    assertNull(SGDataRangeUtility.getBoundsZ(data));
  }

  @Test
  void animationFrameBoundsOfSingleDataIteratesTimeStride() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.isAnimationSupported()).thenReturn(true);
    when(data.isAnimationAvailable()).thenReturn(true);
    when(data.getTimeStride())
        .thenReturn(new jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet(0, 2, 2));
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0, 2.0});
    when(data.isErrorBarAvailable()).thenReturn(false);
    SGValueRange range = SGDataRangeUtility.getAllAnimationFrameBoundsX(data);
    assertEquals(1.0, range.getMinValue(), 0.0);
    assertEquals(2.0, range.getMaxValue(), 0.0);
  }

  @Test
  void animationFrameBoundsWithoutAnimationDelegatesToNormalBounds() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.isAnimationSupported()).thenReturn(false);
    when(data.getXValueArray(false)).thenReturn(new double[] {4.0, 8.0});
    when(data.isErrorBarAvailable()).thenReturn(false);
    assertEquals(4.0, SGDataRangeUtility.getAllAnimationFrameBoundsX(data).getMinValue(), 0.0);
    assertEquals(8.0, SGDataRangeUtility.getAllAnimationFrameBoundsX(data).getMaxValue(), 0.0);
  }

  @Test
  void animationFrameBoundsOfXYZDataIteratesTimeStride() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.isAnimationSupported()).thenReturn(true);
    when(data.isAnimationAvailable()).thenReturn(true);
    when(data.getTimeStride())
        .thenReturn(new jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet(0, 1, 1));
    when(data.getCurrentTimeValueIndex()).thenReturn(0);
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0});
    when(data.getYValueArray(false)).thenReturn(new double[] {2.0});
    when(data.getZValueArray(false)).thenReturn(new double[] {3.0});
    assertEquals(1.0, SGDataRangeUtility.getAllAnimationFrameBoundsX(data).getMaxValue(), 0.0);
    assertEquals(2.0, SGDataRangeUtility.getAllAnimationFrameBoundsY(data).getMaxValue(), 0.0);
    assertEquals(3.0, SGDataRangeUtility.getAllAnimationFrameBoundsZ(data).getMaxValue(), 0.0);
  }

  @Test
  void animationFrameBoundsOfVectorDataIteratesTimeStride() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.isAnimationSupported()).thenReturn(true);
    when(data.isAnimationAvailable()).thenReturn(true);
    when(data.getTimeStride())
        .thenReturn(new jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet(0, 1, 1));
    when(data.getCurrentTimeValueIndex()).thenReturn(0);
    when(data.getXValueArray(false)).thenReturn(new double[] {6.0});
    when(data.getYValueArray(false)).thenReturn(new double[] {7.0});
    assertEquals(6.0, SGDataRangeUtility.getAllAnimationFrameBoundsX(data).getMaxValue(), 0.0);
    assertEquals(7.0, SGDataRangeUtility.getAllAnimationFrameBoundsY(data).getMaxValue(), 0.0);
  }
}
