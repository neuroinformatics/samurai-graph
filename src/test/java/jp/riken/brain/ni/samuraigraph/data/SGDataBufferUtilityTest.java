package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import jp.riken.brain.ni.samuraigraph.base.SGDate;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDataBufferUtility}. */
class SGDataBufferUtilityTest {

  @Test
  void getDateValueArrayExtractsDateValues() {
    SGDate[] dates = {new SGDate(0.5), new SGDate(1.25), new SGDate(2.0)};
    double[] values = SGDataBufferUtility.getDateValueArray(dates);
    assertArrayEquals(new double[] {0.5, 1.25, 2.0}, values, 0.0);
  }
}
