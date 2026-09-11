package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collections;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import org.junit.jupiter.api.Test;

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
}
