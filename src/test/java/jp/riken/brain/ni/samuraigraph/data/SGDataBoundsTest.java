package jp.riken.brain.ni.samuraigraph.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import org.junit.jupiter.api.Test;

class SGDataBoundsTest {

  // -- getMaxValue(double[]) --

  @Test
  void getMaxValue_positiveArray() {
    double[] arr = {1.0, 3.0, 2.0, 5.0, 4.0};
    assertThat(SGDataUtility.getMaxValue(arr)).isEqualTo(5.0);
  }

  @Test
  void getMaxValue_negativeArray() {
    double[] arr = {-5.0, -3.0, -1.0, -4.0};
    assertThat(SGDataUtility.getMaxValue(arr)).isEqualTo(-1.0);
  }

  @Test
  void getMaxValue_mixedArray() {
    double[] arr = {-3.0, 0.0, 5.0, -1.0, 2.0};
    assertThat(SGDataUtility.getMaxValue(arr)).isEqualTo(5.0);
  }

  @Test
  void getMaxValue_singleElement() {
    double[] arr = {42.0};
    assertThat(SGDataUtility.getMaxValue(arr)).isEqualTo(42.0);
  }

  @Test
  void getMaxValue_emptyArray() {
    double[] arr = {};
    assertThat(SGDataUtility.getMaxValue(arr)).isNaN();
  }

  @Test
  void getMaxValue_withNaNSkipped() {
    double[] arr = {1.0, Double.NaN, 3.0};
    assertThat(SGDataUtility.getMaxValue(arr)).isEqualTo(3.0);
  }

  @Test
  void getMaxValue_withPositiveInfinitySkipped() {
    double[] arr = {1.0, Double.POSITIVE_INFINITY, 3.0};
    assertThat(SGDataUtility.getMaxValue(arr)).isEqualTo(3.0);
  }

  @Test
  void getMaxValue_withNegativeInfinitySkipped() {
    double[] arr = {1.0, Double.NEGATIVE_INFINITY, 3.0};
    assertThat(SGDataUtility.getMaxValue(arr)).isEqualTo(3.0);
  }

  @Test
  void getMaxValue_allNaN() {
    double[] arr = {Double.NaN, Double.NaN};
    assertThat(SGDataUtility.getMaxValue(arr)).isNaN();
  }

  @Test
  void getMaxValue_allInfinity() {
    double[] arr = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    assertThat(SGDataUtility.getMaxValue(arr)).isNaN();
  }

  @Test
  void getMaxValue_allInvalid() {
    double[] arr = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    assertThat(SGDataUtility.getMaxValue(arr)).isNaN();
  }

  // -- getMinValue(double[]) --

  @Test
  void getMinValue_positiveArray() {
    double[] arr = {1.0, 3.0, 2.0, 5.0, 4.0};
    assertThat(SGDataUtility.getMinValue(arr)).isEqualTo(1.0);
  }

  @Test
  void getMinValue_negativeArray() {
    double[] arr = {-5.0, -3.0, -1.0, -4.0};
    assertThat(SGDataUtility.getMinValue(arr)).isEqualTo(-5.0);
  }

  @Test
  void getMinValue_mixedArray() {
    double[] arr = {-3.0, 0.0, 5.0, -1.0, 2.0};
    assertThat(SGDataUtility.getMinValue(arr)).isEqualTo(-3.0);
  }

  @Test
  void getMinValue_singleElement() {
    double[] arr = {42.0};
    assertThat(SGDataUtility.getMinValue(arr)).isEqualTo(42.0);
  }

  @Test
  void getMinValue_emptyArray() {
    double[] arr = {};
    assertThat(SGDataUtility.getMinValue(arr)).isNaN();
  }

  @Test
  void getMinValue_withNaNSkipped() {
    double[] arr = {1.0, Double.NaN, 3.0};
    assertThat(SGDataUtility.getMinValue(arr)).isEqualTo(1.0);
  }

  @Test
  void getMinValue_withPositiveInfinitySkipped() {
    double[] arr = {1.0, Double.POSITIVE_INFINITY, 3.0};
    assertThat(SGDataUtility.getMinValue(arr)).isEqualTo(1.0);
  }

  @Test
  void getMinValue_withNegativeInfinitySkipped() {
    double[] arr = {1.0, Double.NEGATIVE_INFINITY, 3.0};
    assertThat(SGDataUtility.getMinValue(arr)).isEqualTo(1.0);
  }

  @Test
  void getMinValue_allNaN() {
    double[] arr = {Double.NaN, Double.NaN};
    assertThat(SGDataUtility.getMinValue(arr)).isNaN();
  }

  @Test
  void getMinValue_allInfinity() {
    double[] arr = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    assertThat(SGDataUtility.getMinValue(arr)).isNaN();
  }

  @Test
  void getMinValue_allInvalid() {
    double[] arr = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    assertThat(SGDataUtility.getMinValue(arr)).isNaN();
  }

  // -- getBounds(double[]) --

  @Test
  void getBounds_normalArray() {
    double[] arr = {1.0, 3.0, 2.0};
    SGValueRange bounds = SGDataUtility.getBounds(arr);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  @Test
  void getBounds_withNaN() {
    double[] arr = {1.0, Double.NaN, 3.0};
    SGValueRange bounds = SGDataUtility.getBounds(arr);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  @Test
  void getBounds_allInvalid() {
    double[] arr = {Double.NaN, Double.POSITIVE_INFINITY};
    SGValueRange bounds = SGDataUtility.getBounds(arr);
    assertThat(bounds.getMinValue()).isNaN();
    assertThat(bounds.getMaxValue()).isNaN();
  }

  @Test
  void getBounds_emptyArray() {
    double[] arr = {};
    SGValueRange bounds = SGDataUtility.getBounds(arr);
    assertThat(bounds.getMinValue()).isNaN();
    assertThat(bounds.getMaxValue()).isNaN();
  }

  // -- getMinValue(List<SGValueRange>) --

  @Test
  void getMinValueRanges_validRanges() {
    List<SGValueRange> ranges =
        Arrays.asList(new SGValueRange(1.0, 5.0), new SGValueRange(3.0, 7.0));
    assertThat(SGDataUtility.getMinValue(ranges)).isEqualTo(1.0);
  }

  @Test
  void getMinValueRanges_singleRange() {
    List<SGValueRange> ranges = Collections.singletonList(new SGValueRange(10.0, 20.0));
    assertThat(SGDataUtility.getMinValue(ranges)).isEqualTo(10.0);
  }

  @Test
  void getMinValueRanges_withInvalidMinSkipped() {
    List<SGValueRange> ranges =
        Arrays.asList(new SGValueRange(Double.NaN, 5.0), new SGValueRange(3.0, 7.0));
    assertThat(SGDataUtility.getMinValue(ranges)).isEqualTo(3.0);
  }

  @Test
  void getMinValueRanges_allInvalidMin() {
    List<SGValueRange> ranges =
        Arrays.asList(
            new SGValueRange(Double.NaN, 5.0), new SGValueRange(Double.POSITIVE_INFINITY, 7.0));
    assertThat(SGDataUtility.getMinValue(ranges)).isNull();
  }

  @Test
  void getMinValueRanges_emptyList() {
    List<SGValueRange> ranges = Collections.emptyList();
    assertThat(SGDataUtility.getMinValue(ranges)).isNull();
  }

  // -- getMaxValue(List<SGValueRange>) --

  @Test
  void getMaxValueRanges_validRanges() {
    List<SGValueRange> ranges =
        Arrays.asList(new SGValueRange(1.0, 5.0), new SGValueRange(3.0, 7.0));
    assertThat(SGDataUtility.getMaxValue(ranges)).isEqualTo(7.0);
  }

  @Test
  void getMaxValueRanges_singleRange() {
    List<SGValueRange> ranges = Collections.singletonList(new SGValueRange(10.0, 20.0));
    assertThat(SGDataUtility.getMaxValue(ranges)).isEqualTo(20.0);
  }

  @Test
  void getMaxValueRanges_withInvalidMaxSkipped() {
    List<SGValueRange> ranges =
        Arrays.asList(new SGValueRange(1.0, Double.NaN), new SGValueRange(3.0, 7.0));
    assertThat(SGDataUtility.getMaxValue(ranges)).isEqualTo(7.0);
  }

  @Test
  void getMaxValueRanges_allInvalidMax() {
    List<SGValueRange> ranges =
        Arrays.asList(
            new SGValueRange(1.0, Double.NaN), new SGValueRange(3.0, Double.NEGATIVE_INFINITY));
    assertThat(SGDataUtility.getMaxValue(ranges)).isNull();
  }

  @Test
  void getMaxValueRanges_emptyList() {
    List<SGValueRange> ranges = Collections.emptyList();
    assertThat(SGDataUtility.getMaxValue(ranges)).isNull();
  }

  // -- getBoundsX(SGISXYTypeSingleData) --

  @Test
  void getBoundsX_normalData() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0, 2.0, 3.0});
    when(data.isErrorBarAvailable()).thenReturn(false);

    SGValueRange bounds = SGDataUtility.getBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  @Test
  void getBoundsX_nullXValues() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getXValueArray(false)).thenReturn(null);

    assertThat(SGDataUtility.getBoundsX(data)).isNull();
  }

  @Test
  void getBoundsX_withoutErrorBars() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getXValueArray(false)).thenReturn(new double[] {-2.0, 0.0, 5.0});
    when(data.isErrorBarAvailable()).thenReturn(false);

    SGValueRange bounds = SGDataUtility.getBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(-2.0);
    assertThat(bounds.getMaxValue()).isEqualTo(5.0);
  }

  @Test
  void getBoundsX_withVerticalErrorBarsIgnored() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0, 2.0, 3.0});
    when(data.isErrorBarAvailable()).thenReturn(true);
    when(data.isErrorBarVertical()).thenReturn(true);

    SGValueRange bounds = SGDataUtility.getBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  @Test
  void getBoundsX_withHorizontalErrorBars() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getXValueArray(false)).thenReturn(new double[] {2.0, 4.0});
    when(data.isErrorBarAvailable()).thenReturn(true);
    when(data.isErrorBarVertical()).thenReturn(false);
    when(data.getLowerErrorValueArray(false)).thenReturn(new double[] {1.0, 0.5});
    when(data.getUpperErrorValueArray(false)).thenReturn(new double[] {0.5, 1.0});

    SGValueRange bounds = SGDataUtility.getBoundsX(data);
    // lyValues: 2.0-1.0=1.0, 4.0-0.5=3.5  -> min 1.0
    // uyValues: 2.0+0.5=2.5, 4.0+1.0=5.0  -> max 5.0
    // xValues: 2.0, 4.0
    // Combined: min=1.0, max=5.0
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(5.0);
  }

  @Test
  void getBoundsX_withNaNInValues() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0, Double.NaN, 3.0});
    when(data.isErrorBarAvailable()).thenReturn(false);

    SGValueRange bounds = SGDataUtility.getBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  // -- getBoundsY(SGISXYTypeSingleData) --

  @Test
  void getBoundsY_normalData() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getYValueArray(false)).thenReturn(new double[] {10.0, 20.0, 30.0});
    when(data.isErrorBarAvailable()).thenReturn(false);

    SGValueRange bounds = SGDataUtility.getBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(10.0);
    assertThat(bounds.getMaxValue()).isEqualTo(30.0);
  }

  @Test
  void getBoundsY_nullYValues() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getYValueArray(false)).thenReturn(null);

    assertThat(SGDataUtility.getBoundsY(data)).isNull();
  }

  @Test
  void getBoundsY_withoutErrorBars() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getYValueArray(false)).thenReturn(new double[] {-5.0, 0.0, 10.0});
    when(data.isErrorBarAvailable()).thenReturn(false);

    SGValueRange bounds = SGDataUtility.getBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(-5.0);
    assertThat(bounds.getMaxValue()).isEqualTo(10.0);
  }

  @Test
  void getBoundsY_withHorizontalErrorBarsIgnored() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getYValueArray(false)).thenReturn(new double[] {1.0, 2.0, 3.0});
    when(data.isErrorBarAvailable()).thenReturn(true);
    when(data.isErrorBarVertical()).thenReturn(false);

    SGValueRange bounds = SGDataUtility.getBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  @Test
  void getBoundsY_withVerticalErrorBars() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getYValueArray(false)).thenReturn(new double[] {2.0, 4.0});
    when(data.isErrorBarAvailable()).thenReturn(true);
    when(data.isErrorBarVertical()).thenReturn(true);
    when(data.getLowerErrorValueArray(false)).thenReturn(new double[] {1.0, 0.5});
    when(data.getUpperErrorValueArray(false)).thenReturn(new double[] {0.5, 1.0});

    SGValueRange bounds = SGDataUtility.getBoundsY(data);
    // lyValues: 2.0-1.0=1.0, 4.0-0.5=3.5  -> min 1.0
    // uyValues: 2.0+0.5=2.5, 4.0+1.0=5.0  -> max 5.0
    // yValues: 2.0, 4.0
    // Combined: min=1.0, max=5.0
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(5.0);
  }

  @Test
  void getBoundsY_withNaNInValues() {
    SGISXYTypeSingleData data = mock(SGISXYTypeSingleData.class);
    when(data.getYValueArray(false)).thenReturn(new double[] {1.0, Double.NaN, 3.0});
    when(data.isErrorBarAvailable()).thenReturn(false);

    SGValueRange bounds = SGDataUtility.getBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  // -- getBoundsX(SGISXYTypeMultipleData) --

  @Test
  void getBoundsXMultiple_normalData() {
    SGISXYTypeSingleData d1 = mock(SGISXYTypeSingleData.class);
    when(d1.getXValueArray(false)).thenReturn(new double[] {1.0, 2.0, 3.0});
    when(d1.isErrorBarAvailable()).thenReturn(false);

    SGISXYTypeSingleData d2 = mock(SGISXYTypeSingleData.class);
    when(d2.getXValueArray(false)).thenReturn(new double[] {4.0, 5.0, 6.0});
    when(d2.isErrorBarAvailable()).thenReturn(false);

    SGISXYTypeMultipleData data = mock(SGISXYTypeMultipleData.class);
    when(data.getSXYDataArray()).thenReturn(new SGISXYTypeSingleData[] {d1, d2});

    SGValueRange bounds = SGDataUtility.getBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(6.0);
  }

  @Test
  void getBoundsXMultiple_singleSeries() {
    SGISXYTypeSingleData d1 = mock(SGISXYTypeSingleData.class);
    when(d1.getXValueArray(false)).thenReturn(new double[] {10.0, 20.0});
    when(d1.isErrorBarAvailable()).thenReturn(false);

    SGISXYTypeMultipleData data = mock(SGISXYTypeMultipleData.class);
    when(data.getSXYDataArray()).thenReturn(new SGISXYTypeSingleData[] {d1});

    SGValueRange bounds = SGDataUtility.getBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(10.0);
    assertThat(bounds.getMaxValue()).isEqualTo(20.0);
  }

  // -- getBoundsY(SGISXYTypeMultipleData) --

  @Test
  void getBoundsYMultiple_normalData() {
    SGISXYTypeSingleData d1 = mock(SGISXYTypeSingleData.class);
    when(d1.getYValueArray(false)).thenReturn(new double[] {10.0, 20.0, 30.0});
    when(d1.isErrorBarAvailable()).thenReturn(false);

    SGISXYTypeSingleData d2 = mock(SGISXYTypeSingleData.class);
    when(d2.getYValueArray(false)).thenReturn(new double[] {40.0, 50.0, 60.0});
    when(d2.isErrorBarAvailable()).thenReturn(false);

    SGISXYTypeMultipleData data = mock(SGISXYTypeMultipleData.class);
    when(data.getSXYDataArray()).thenReturn(new SGISXYTypeSingleData[] {d1, d2});

    SGValueRange bounds = SGDataUtility.getBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(10.0);
    assertThat(bounds.getMaxValue()).isEqualTo(60.0);
  }

  @Test
  void getBoundsYMultiple_singleSeries() {
    SGISXYTypeSingleData d1 = mock(SGISXYTypeSingleData.class);
    when(d1.getYValueArray(false)).thenReturn(new double[] {5.0, 15.0});
    when(d1.isErrorBarAvailable()).thenReturn(false);

    SGISXYTypeMultipleData data = mock(SGISXYTypeMultipleData.class);
    when(data.getSXYDataArray()).thenReturn(new SGISXYTypeSingleData[] {d1});

    SGValueRange bounds = SGDataUtility.getBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(5.0);
    assertThat(bounds.getMaxValue()).isEqualTo(15.0);
  }

  // -- getBoundsX(SGIVXYTypeData) --

  @Test
  void getBoundsXVXY_normalData() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0, 2.0, 3.0});

    SGValueRange bounds = SGDataUtility.getBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  @Test
  void getBoundsXVXY_nullValues() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.getXValueArray(false)).thenReturn(null);

    assertThat(SGDataUtility.getBoundsX(data)).isNull();
  }

  @Test
  void getBoundsXVXY_withNaN() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0, Double.NaN, 3.0});

    SGValueRange bounds = SGDataUtility.getBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  // -- getBoundsY(SGIVXYTypeData) --

  @Test
  void getBoundsYVXY_normalData() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.getYValueArray(false)).thenReturn(new double[] {10.0, 20.0, 30.0});

    SGValueRange bounds = SGDataUtility.getBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(10.0);
    assertThat(bounds.getMaxValue()).isEqualTo(30.0);
  }

  @Test
  void getBoundsYVXY_nullValues() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.getYValueArray(false)).thenReturn(null);

    assertThat(SGDataUtility.getBoundsY(data)).isNull();
  }

  @Test
  void getBoundsYVXY_withNaN() {
    SGIVXYTypeData data = mock(SGIVXYTypeData.class);
    when(data.getYValueArray(false)).thenReturn(new double[] {1.0, Double.NaN, 3.0});

    SGValueRange bounds = SGDataUtility.getBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  // -- getBoundsX(SGISXYZTypeData) --

  @Test
  void getBoundsXXYZ_normalData() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0, 2.0, 3.0});

    SGValueRange bounds = SGDataUtility.getBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  @Test
  void getBoundsXXYZ_nullValues() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getXValueArray(false)).thenReturn(null);

    assertThat(SGDataUtility.getBoundsX(data)).isNull();
  }

  @Test
  void getBoundsXXYZ_withNaN() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getXValueArray(false)).thenReturn(new double[] {1.0, Double.NaN, 3.0});

    SGValueRange bounds = SGDataUtility.getBoundsX(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  // -- getBoundsY(SGISXYZTypeData) --

  @Test
  void getBoundsYXYZ_normalData() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getYValueArray(false)).thenReturn(new double[] {10.0, 20.0, 30.0});

    SGValueRange bounds = SGDataUtility.getBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(10.0);
    assertThat(bounds.getMaxValue()).isEqualTo(30.0);
  }

  @Test
  void getBoundsYXYZ_nullValues() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getYValueArray(false)).thenReturn(null);

    assertThat(SGDataUtility.getBoundsY(data)).isNull();
  }

  @Test
  void getBoundsYXYZ_withNaN() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getYValueArray(false)).thenReturn(new double[] {1.0, Double.NaN, 3.0});

    SGValueRange bounds = SGDataUtility.getBoundsY(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  // -- getBoundsZ(SGISXYZTypeData) --

  @Test
  void getBoundsZXYZ_normalData() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getZValueArray(false)).thenReturn(new double[] {100.0, 200.0, 300.0});

    SGValueRange bounds = SGDataUtility.getBoundsZ(data);
    assertThat(bounds.getMinValue()).isEqualTo(100.0);
    assertThat(bounds.getMaxValue()).isEqualTo(300.0);
  }

  @Test
  void getBoundsZXYZ_nullValues() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getZValueArray(false)).thenReturn(null);

    assertThat(SGDataUtility.getBoundsZ(data)).isNull();
  }

  @Test
  void getBoundsZXYZ_withNaN() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getZValueArray(false)).thenReturn(new double[] {1.0, Double.NaN, 3.0});

    SGValueRange bounds = SGDataUtility.getBoundsZ(data);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(3.0);
  }

  @Test
  void getBoundsZXYZ_negativeValues() {
    SGISXYZTypeData data = mock(SGISXYZTypeData.class);
    when(data.getZValueArray(false)).thenReturn(new double[] {-50.0, -10.0, -100.0});

    SGValueRange bounds = SGDataUtility.getBoundsZ(data);
    assertThat(bounds.getMinValue()).isEqualTo(-100.0);
    assertThat(bounds.getMaxValue()).isEqualTo(-10.0);
  }
}
