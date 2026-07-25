package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SGVXYDataUtilityTest {

  @Test
  void getXComponentArrayWithNormalValues() {
    double[] mag = {2.0, 1.0, 0.0};
    double[] angle = {0.0, Math.PI, Math.PI / 2.0};
    double[] expected = {2.0, -1.0, 0.0};
    assertArrayEquals(expected, SGVXYDataUtility.getXComponentArray(mag, angle), 1e-10);
  }

  @Test
  void getXComponentArrayWithNaNReturnsNaN() {
    double[] mag = {Double.NaN, 1.0};
    double[] angle = {0.0, Double.NaN};
    double[] result = SGVXYDataUtility.getXComponentArray(mag, angle);
    assertEquals(Double.NaN, result[0], 0.0);
    assertEquals(Double.NaN, result[1], 0.0);
  }

  @Test
  void getXComponentArrayWithNegativeMagnitudeReturnsNaN() {
    double[] mag = {-1.0, -0.5};
    double[] angle = {0.0, Math.PI};
    double[] result = SGVXYDataUtility.getXComponentArray(mag, angle);
    assertEquals(Double.NaN, result[0], 0.0);
    assertEquals(Double.NaN, result[1], 0.0);
  }

  @Test
  void getYComponentArrayWithNormalValues() {
    double[] mag = {2.0, 1.0, 0.0};
    double[] angle = {0.0, Math.PI, Math.PI / 2.0};
    double[] expected = {0.0, 0.0, 0.0};
    assertArrayEquals(expected, SGVXYDataUtility.getYComponentArray(mag, angle), 1e-10);
  }

  @Test
  void getYComponentArrayWithNaNReturnsNaN() {
    double[] mag = {Double.NaN, 1.0};
    double[] angle = {0.0, Double.NaN};
    double[] result = SGVXYDataUtility.getYComponentArray(mag, angle);
    assertEquals(Double.NaN, result[0], 0.0);
    assertEquals(Double.NaN, result[1], 0.0);
  }

  @Test
  void getYComponentArrayWithNegativeMagnitudeReturnsNaN() {
    double[] mag = {-1.0};
    double[] angle = {0.0};
    double[] result = SGVXYDataUtility.getYComponentArray(mag, angle);
    assertEquals(Double.NaN, result[0], 0.0);
  }

  @Test
  void getMagnitudeArray() {
    double[] x = {3.0, 1.0, 0.0};
    double[] y = {4.0, 0.0, 0.0};
    assertArrayEquals(
        new double[] {5.0, 1.0, 0.0}, SGVXYDataUtility.getMagnitudeArray(x, y), 1e-10);
  }

  @Test
  void getAngleArray() {
    double[] x = {1.0, 0.0, -1.0};
    double[] y = {1.0, 1.0, 0.0};
    double[] expected = {Math.atan(1.0 / 1.0), Math.PI / 2.0, Math.atan(0.0 / -1.0)};
    assertArrayEquals(expected, SGVXYDataUtility.getAngleArray(x, y), 1e-10);
  }
}
