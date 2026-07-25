package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SGVXYDataBufferTest {

  @Test
  void constructorWithAllArrays() {
    SGVXYDataBuffer buf =
        new SGVXYDataBuffer(
            new double[] {1.0, 2.0},
            new double[] {3.0, 4.0},
            new double[] {5.0, 6.0},
            new double[] {7.0, 8.0},
            false);
    assertEquals(2, buf.getLength());
    assertArrayEquals(new double[] {1.0, 2.0}, buf.getXValues(), 0.0);
    assertArrayEquals(new double[] {3.0, 4.0}, buf.getYValues(), 0.0);
    assertArrayEquals(new double[] {5.0, 6.0}, buf.getFirstComponentValues(), 0.0);
    assertArrayEquals(new double[] {7.0, 8.0}, buf.getSecondComponentValues(), 0.0);
    assertFalse(buf.isPolar());
  }

  @Test
  void constructorWithPolarFlag() {
    SGVXYDataBuffer buf =
        new SGVXYDataBuffer(
            new double[] {1.0, 2.0},
            new double[] {3.0, 4.0},
            new double[] {5.0, 6.0},
            new double[] {7.0, 8.0},
            true);
    assertTrue(buf.isPolar());
  }

  @Test
  void constructorWithNullArrayThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGVXYDataBuffer(
                null, new double[] {1.0}, new double[] {1.0}, new double[] {1.0}, false));
  }

  @Test
  void constructorWithMismatchedLengthThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGVXYDataBuffer(
                new double[] {1.0, 2.0},
                new double[] {3.0},
                new double[] {4.0},
                new double[] {5.0},
                false));
  }

  @Test
  void getXValuesReturnsDefensiveCopy() {
    SGVXYDataBuffer buf =
        new SGVXYDataBuffer(
            new double[] {1.0}, new double[] {2.0}, new double[] {3.0}, new double[] {4.0}, false);
    double[] retrieved = buf.getXValues();
    retrieved[0] = 99.0;
    assertArrayEquals(new double[] {1.0}, buf.getXValues(), 0.0);
  }

  @Test
  void cloneProducesIndependentCopy() {
    SGVXYDataBuffer original =
        new SGVXYDataBuffer(
            new double[] {1.0, 2.0},
            new double[] {3.0, 4.0},
            new double[] {5.0, 6.0},
            new double[] {7.0, 8.0},
            true);
    SGVXYDataBuffer copy = (SGVXYDataBuffer) original.clone();
    assertNotSame(original, copy);
    assertArrayEquals(original.getXValues(), copy.getXValues(), 0.0);
    copy.getXValues();
  }

  @Test
  void getDataType() {
    SGVXYDataBuffer buf =
        new SGVXYDataBuffer(
            new double[] {1.0}, new double[] {2.0}, new double[] {3.0}, new double[] {4.0}, false);
    assertEquals(SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA, buf.getDataType());
  }

  @Test
  void getGridTypeKey() {
    SGVXYDataBuffer buf =
        new SGVXYDataBuffer(
            new double[] {1.0}, new double[] {2.0}, new double[] {3.0}, new double[] {4.0}, false);
    assertEquals(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, buf.getGridTypeKey());
  }
}
