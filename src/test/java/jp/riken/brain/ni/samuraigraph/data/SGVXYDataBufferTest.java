package jp.riken.brain.ni.samuraigraph.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class SGVXYDataBufferTest {

  double[] xValues = {0.0, 1.0, 2.0, 3.0};
  double[] yValues = {0.0, 1.0, 2.0, 3.0};
  double[] fValues = {1.0, 2.0, 3.0, 4.0};
  double[] sValues = {0.5, 1.0, 1.5, 2.0};

  @Test
  void constructor_throwsWhenXValuesIsNull() {
    assertThatThrownBy(() -> new SGVXYDataBuffer(null, yValues, fValues, sValues, false))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void constructor_throwsWhenYValuesIsNull() {
    assertThatThrownBy(() -> new SGVXYDataBuffer(xValues, null, fValues, sValues, false))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void constructor_throwsWhenFValuesIsNull() {
    assertThatThrownBy(() -> new SGVXYDataBuffer(xValues, yValues, null, sValues, false))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void constructor_throwsWhenSValuesIsNull() {
    assertThatThrownBy(() -> new SGVXYDataBuffer(xValues, yValues, fValues, null, false))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void constructor_throwsWhenXAndYLengthsMismatch() {
    double[] shortY = {0.0, 1.0};
    assertThatThrownBy(() -> new SGVXYDataBuffer(xValues, shortY, fValues, sValues, false))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void constructor_throwsWhenXAndFLengthsMismatch() {
    double[] shortF = {1.0, 2.0};
    assertThatThrownBy(() -> new SGVXYDataBuffer(xValues, yValues, shortF, sValues, false))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void constructor_throwsWhenXAndSLengthsMismatch() {
    double[] shortS = {0.5, 1.0};
    assertThatThrownBy(() -> new SGVXYDataBuffer(xValues, yValues, fValues, shortS, false))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void constructor_cartesian_initializesCorrectly() {
    SGVXYDataBuffer buffer = new SGVXYDataBuffer(xValues, yValues, fValues, sValues, false);

    assertThat(buffer.getLength()).isEqualTo(4);
    assertThat(buffer.getXValues()).containsExactly(0.0, 1.0, 2.0, 3.0);
    assertThat(buffer.getYValues()).containsExactly(0.0, 1.0, 2.0, 3.0);
    assertThat(buffer.getFirstComponentValues()).containsExactly(1.0, 2.0, 3.0, 4.0);
    assertThat(buffer.getSecondComponentValues()).containsExactly(0.5, 1.0, 1.5, 2.0);
  }

  @Test
  void constructor_cartesian_isNotPolar() {
    SGVXYDataBuffer buffer = new SGVXYDataBuffer(xValues, yValues, fValues, sValues, false);
    assertThat(buffer.isPolar()).isFalse();
  }

  @Test
  void constructor_polar_isPolar() {
    SGVXYDataBuffer buffer = new SGVXYDataBuffer(xValues, yValues, fValues, sValues, true);
    assertThat(buffer.isPolar()).isTrue();
  }

  @Test
  void getXValues_returnsCopy() {
    SGVXYDataBuffer buffer = new SGVXYDataBuffer(xValues, yValues, fValues, sValues, false);
    double[] returned = buffer.getXValues();
    returned[0] = 999.0;
    assertThat(buffer.getXValues()[0]).isEqualTo(0.0);
  }

  @Test
  void getYValues_returnsCopy() {
    SGVXYDataBuffer buffer = new SGVXYDataBuffer(xValues, yValues, fValues, sValues, false);
    double[] returned = buffer.getYValues();
    returned[0] = 999.0;
    assertThat(buffer.getYValues()[0]).isEqualTo(0.0);
  }

  @Test
  void getFirstComponentValues_returnsCopy() {
    SGVXYDataBuffer buffer = new SGVXYDataBuffer(xValues, yValues, fValues, sValues, false);
    double[] returned = buffer.getFirstComponentValues();
    returned[0] = 999.0;
    assertThat(buffer.getFirstComponentValues()[0]).isEqualTo(1.0);
  }

  @Test
  void getSecondComponentValues_returnsCopy() {
    SGVXYDataBuffer buffer = new SGVXYDataBuffer(xValues, yValues, fValues, sValues, false);
    double[] returned = buffer.getSecondComponentValues();
    returned[0] = 999.0;
    assertThat(buffer.getSecondComponentValues()[0]).isEqualTo(0.5);
  }

  @Test
  void getDataType_returnsVXYVirtualMDArray() {
    SGVXYDataBuffer buffer = new SGVXYDataBuffer(xValues, yValues, fValues, sValues, false);
    assertThat(buffer.getDataType()).isEqualTo(SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA);
  }

  @Test
  void getGridTypeKey_returnsVXYGridPlotFlag() {
    SGVXYDataBuffer buffer = new SGVXYDataBuffer(xValues, yValues, fValues, sValues, false);
    assertThat(buffer.getGridTypeKey())
        .isEqualTo(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG);
  }

  @Test
  void isGridType_returnsFalse() {
    SGVXYDataBuffer buffer = new SGVXYDataBuffer(xValues, yValues, fValues, sValues, false);
    assertThat(buffer.isGridType()).isFalse();
  }

  @Test
  void clone_returnsIndependentCopy() {
    SGVXYDataBuffer original = new SGVXYDataBuffer(xValues, yValues, fValues, sValues, true);
    SGVXYDataBuffer cloned = (SGVXYDataBuffer) original.clone();

    assertThat(cloned.getLength()).isEqualTo(4);
    assertThat(cloned.getXValues()).containsExactly(0.0, 1.0, 2.0, 3.0);
    assertThat(cloned.getYValues()).containsExactly(0.0, 1.0, 2.0, 3.0);
    assertThat(cloned.getFirstComponentValues()).containsExactly(1.0, 2.0, 3.0, 4.0);
    assertThat(cloned.getSecondComponentValues()).containsExactly(0.5, 1.0, 1.5, 2.0);
    assertThat(cloned.isPolar()).isTrue();
    assertThat(cloned.getDataType()).isEqualTo(SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA);

    cloned.getXValues()[0] = 999.0;
    assertThat(original.getXValues()[0]).isEqualTo(0.0);
  }

  @Test
  void singleElement_arrayWorks() {
    SGVXYDataBuffer buffer =
        new SGVXYDataBuffer(
            new double[] {1.0}, new double[] {2.0}, new double[] {3.0}, new double[] {4.0}, false);
    assertThat(buffer.getLength()).isEqualTo(1);
  }
}
