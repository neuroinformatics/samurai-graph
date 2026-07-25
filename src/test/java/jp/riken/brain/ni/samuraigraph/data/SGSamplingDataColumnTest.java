package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SGSamplingDataColumnTest {

  @Test
  void constructorWithSamplingRate() {
    SGSamplingDataColumn col = new SGSamplingDataColumn(1000.0, 5);
    assertEquals(5, col.getLength());
    assertEquals(1000.0, col.getSamplingRate(), 0.0);
  }

  @Test
  void constructorWithZeroRateThrows() {
    assertThrows(IllegalArgumentException.class, () -> new SGSamplingDataColumn(0.0, 5));
  }

  @Test
  void constructorWithNegativeRateThrows() {
    assertThrows(IllegalArgumentException.class, () -> new SGSamplingDataColumn(-1.0, 5));
  }

  @Test
  void constructorWithNegativeLengthThrows() {
    assertThrows(IllegalArgumentException.class, () -> new SGSamplingDataColumn(1000.0, -1));
  }

  @Test
  void constructorWithZeroLength() {
    SGSamplingDataColumn col = new SGSamplingDataColumn(1000.0, 0);
    assertEquals(0, col.getLength());
  }

  @Test
  void getValueType() {
    SGSamplingDataColumn col = new SGSamplingDataColumn(1000.0, 5);
    assertEquals(SGIDataColumnTypeConstants.VALUE_TYPE_SAMPLING_RATE, col.getValueType());
  }

  @Test
  void getValue() {
    SGSamplingDataColumn col = new SGSamplingDataColumn(1000.0, 3);
    assertEquals(0.0 / 1000.0, (Double) col.getValue(0), 1e-10);
    assertEquals(1.0 / 1000.0, (Double) col.getValue(1), 1e-10);
    assertEquals(2.0 / 1000.0, (Double) col.getValue(2), 1e-10);
  }

  @Test
  void getNumberArray() {
    SGSamplingDataColumn col = new SGSamplingDataColumn(500.0, 4);
    double[] expected = {0.0 / 500.0, 1.0 / 500.0, 2.0 / 500.0, 3.0 / 500.0};
    assertArrayEquals(expected, col.getNumberArray(), 1e-10);
  }

  @Test
  void getTitle() {
    SGSamplingDataColumn col = new SGSamplingDataColumn(1000.0, 5);
    assertEquals("Sampling Rate 1000.0 Hz", col.getTitle());
  }

  @Test
  void getSamplingRate() {
    SGSamplingDataColumn col = new SGSamplingDataColumn(44100.0, 10);
    assertEquals(44100.0, col.getSamplingRate(), 0.0);
  }

  @Test
  void dispose() {
    SGSamplingDataColumn col = new SGSamplingDataColumn(1000.0, 5);
    col.dispose();
    assertTrue(col.isDisposed());
  }
}
