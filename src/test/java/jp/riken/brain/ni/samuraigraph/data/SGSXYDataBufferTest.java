package jp.riken.brain.ni.samuraigraph.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class SGSXYDataBufferTest {

  double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
  double[] yValues = {2.0, 4.0, 6.0, 8.0, 10.0};

  @Test
  void constructor_throwsWhenXValuesIsNull() {
    assertThatThrownBy(() -> new SGSXYDataBuffer(null, yValues))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void constructor_throwsWhenYValuesIsNull() {
    assertThatThrownBy(() -> new SGSXYDataBuffer(xValues, null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void constructor_throwsWhenLengthsMismatch() {
    double[] shortY = {1.0, 2.0};
    assertThatThrownBy(() -> new SGSXYDataBuffer(xValues, shortY))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void constructor_initializesCorrectly() {
    SGSXYDataBuffer buffer = new SGSXYDataBuffer(xValues, yValues);

    assertThat(buffer.getLength()).isEqualTo(5);
    assertThat(buffer.getXValues()).containsExactly(1.0, 2.0, 3.0, 4.0, 5.0);
    assertThat(buffer.getYValues()).containsExactly(2.0, 4.0, 6.0, 8.0, 10.0);
  }

  @Test
  void getXValues_returnsCopy() {
    SGSXYDataBuffer buffer = new SGSXYDataBuffer(xValues, yValues);
    double[] returned = buffer.getXValues();
    returned[0] = 999.0;
    assertThat(buffer.getXValues()[0]).isEqualTo(1.0);
  }

  @Test
  void getYValues_returnsCopy() {
    SGSXYDataBuffer buffer = new SGSXYDataBuffer(xValues, yValues);
    double[] returned = buffer.getYValues();
    returned[0] = 999.0;
    assertThat(buffer.getYValues()[0]).isEqualTo(2.0);
  }

  @Test
  void getDataType_returnsSXYVirtualMDArray() {
    SGSXYDataBuffer buffer = new SGSXYDataBuffer(xValues, yValues);
    assertThat(buffer.getDataType()).isEqualTo(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA);
  }

  @Test
  void isGridType_returnsFalse() {
    SGSXYDataBuffer buffer = new SGSXYDataBuffer(xValues, yValues);
    assertThat(buffer.isGridType()).isFalse();
  }

  @Test
  void getGridTypeKey_returnsNull() {
    SGSXYDataBuffer buffer = new SGSXYDataBuffer(xValues, yValues);
    assertThat(buffer.getGridTypeKey()).isNull();
  }

  @Test
  void constructor_withErrorBars_setsLowerAndUpperErrorValues() {
    double[] lowerErrors = {0.1, 0.2, 0.3, 0.4, 0.5};
    double[] upperErrors = {0.2, 0.4, 0.6, 0.8, 1.0};

    SGSXYDataBuffer buffer =
        new SGSXYDataBuffer(
            xValues, yValues, false, null, true, lowerErrors, upperErrors, false, null);

    assertThat(buffer.getLowerErrorValues()).containsExactly(0.1, 0.2, 0.3, 0.4, 0.5);
    assertThat(buffer.getUpperErrorValues()).containsExactly(0.2, 0.4, 0.6, 0.8, 1.0);
  }

  @Test
  void constructor_withoutErrorBars_returnsNull() {
    SGSXYDataBuffer buffer = new SGSXYDataBuffer(xValues, yValues);
    assertThat(buffer.getLowerErrorValues()).isNull();
    assertThat(buffer.getUpperErrorValues()).isNull();
  }

  @Test
  void constructor_withTickLabels_setsTickLabels() {
    String[] tickLabels = {"a", "b", "c", "d", "e"};

    SGSXYDataBuffer buffer =
        new SGSXYDataBuffer(xValues, yValues, false, null, false, null, null, null, tickLabels);

    assertThat(buffer.getTickLabels()).containsExactly("a", "b", "c", "d", "e");
  }

  @Test
  void constructor_withoutTickLabels_returnsNull() {
    SGSXYDataBuffer buffer = new SGSXYDataBuffer(xValues, yValues);
    assertThat(buffer.getTickLabels()).isNull();
  }

  @Test
  void getXDateFlag_returnsFalseByDefault() {
    SGSXYDataBuffer buffer = new SGSXYDataBuffer(xValues, yValues);
    assertThat(buffer.getXDateFlag()).isFalse();
  }

  @Test
  void getDateArray_returnsNullWhenNotSet() {
    SGSXYDataBuffer buffer = new SGSXYDataBuffer(xValues, yValues);
    assertThat(buffer.getDateArray()).isNull();
  }

  @Test
  void getMultipleDataBuffer_returnsNonNull() {
    SGSXYDataBuffer buffer = new SGSXYDataBuffer(xValues, yValues);
    assertThat(buffer.getMultipleDataBuffer()).isNotNull();
  }

  @Test
  void singleElement_arrayWorks() {
    SGSXYDataBuffer buffer = new SGSXYDataBuffer(new double[] {1.0}, new double[] {2.0});
    assertThat(buffer.getLength()).isEqualTo(1);
    assertThat(buffer.getXValues()).containsExactly(1.0);
    assertThat(buffer.getYValues()).containsExactly(2.0);
  }

  @Test
  void hasSameErrorVariableFlag_returnsNullWhenNotSet() {
    SGSXYDataBuffer buffer = new SGSXYDataBuffer(xValues, yValues);
    assertThat(buffer.hasSameErrorVariableFlag()).isNull();
  }

  @Test
  void constructor_withSameErrorValuesFlag_setsFlag() {
    double[] lowerErrors = {0.1, 0.1, 0.1, 0.1, 0.1};
    double[] upperErrors = {0.2, 0.2, 0.2, 0.2, 0.2};

    SGSXYDataBuffer buffer =
        new SGSXYDataBuffer(
            xValues, yValues, false, null, true, lowerErrors, upperErrors, true, null);

    assertThat(buffer.hasSameErrorVariableFlag()).isTrue();
  }
}
