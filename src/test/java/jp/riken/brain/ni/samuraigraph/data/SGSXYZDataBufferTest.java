package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SGSXYZDataBufferTest {

  @Test
  void constructorWithAllArrays() {
    SGSXYZDataBuffer buf =
        new SGSXYZDataBuffer(
            new double[] {1.0, 2.0}, new double[] {3.0, 4.0}, new double[] {5.0, 6.0});
    assertEquals(2, buf.getLength());
    assertArrayEquals(new double[] {1.0, 2.0}, buf.getXValues(), 0.0);
    assertArrayEquals(new double[] {3.0, 4.0}, buf.getYValues(), 0.0);
    assertArrayEquals(new double[] {5.0, 6.0}, buf.getZValues(), 0.0);
  }

  @Test
  void constructorWithNullArrayThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGSXYZDataBuffer(null, new double[] {1.0}, new double[] {1.0}));
  }

  @Test
  void constructorWithMismatchedLengthThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGSXYZDataBuffer(new double[] {1.0, 2.0}, new double[] {3.0}, new double[] {4.0}));
  }

  @Test
  void getXValuesReturnsDefensiveCopy() {
    SGSXYZDataBuffer buf =
        new SGSXYZDataBuffer(new double[] {1.0}, new double[] {2.0}, new double[] {3.0});
    double[] retrieved = buf.getXValues();
    retrieved[0] = 99.0;
    assertArrayEquals(new double[] {1.0}, buf.getXValues(), 0.0);
  }

  @Test
  void cloneProducesIndependentCopy() {
    SGSXYZDataBuffer original =
        new SGSXYZDataBuffer(
            new double[] {1.0, 2.0}, new double[] {3.0, 4.0}, new double[] {5.0, 6.0});
    SGSXYZDataBuffer copy = (SGSXYZDataBuffer) original.clone();
    assertNotSame(original, copy);
    assertArrayEquals(original.getXValues(), copy.getXValues(), 0.0);
  }

  @Test
  void getDataType() {
    SGSXYZDataBuffer buf =
        new SGSXYZDataBuffer(new double[] {1.0}, new double[] {2.0}, new double[] {3.0});
    assertEquals(SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA, buf.getDataType());
  }

  @Test
  void getGridTypeKey() {
    SGSXYZDataBuffer buf =
        new SGSXYZDataBuffer(new double[] {1.0}, new double[] {2.0}, new double[] {3.0});
    assertEquals(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, buf.getGridTypeKey());
  }
}
