package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDataViewerUtility}. */
class SGDataViewerUtilityTest {

  @Test
  void getCoordinateVariableValueReturnsInRangeValue() {
    double[] array = {1.0, 2.5, 4.0};
    assertEquals(1.0, SGDataViewerUtility.getCoordinateVariableValue(array, 0), 0.0);
    assertEquals(2.5, SGDataViewerUtility.getCoordinateVariableValue(array, 1), 0.0);
    assertEquals(4.0, SGDataViewerUtility.getCoordinateVariableValue(array, 2), 0.0);
  }

  @Test
  void matchesChecksRowAndColumnTypeForSingleDimension() {
    SGDataValueHistory.SDArray.D1 value =
        new SGDataValueHistory.SDArray.D1(3.5, SGIDataColumnTypeConstants.Y_VALUE, 2);
    assertTrue(SGDataViewerUtility.matches(2, SGIDataColumnTypeConstants.Y_VALUE, value, 0.0));
    assertFalse(SGDataViewerUtility.matches(3, SGIDataColumnTypeConstants.Y_VALUE, value, 0.0));
    assertFalse(
        SGDataViewerUtility.matches(2, SGIDataColumnTypeConstants.LOWER_ERROR_VALUE, value, 0.0));
  }

  @Test
  void matchesChecksRowColumnAndTypeForTwoDimensions() {
    SGDataValueHistory.SDArray.D1 value =
        new SGDataValueHistory.SDArray.D1(3.5, SGIDataColumnTypeConstants.Y_VALUE, 2);
    assertTrue(
        SGDataViewerUtility.matches(
            value.getRowIndex(),
            value.getColumnIndex(),
            SGIDataColumnTypeConstants.Y_VALUE,
            value,
            0.0));
    assertFalse(SGDataViewerUtility.matches(2, 1, SGIDataColumnTypeConstants.Y_VALUE, value, 0.0));
    assertFalse(SGDataViewerUtility.matches(2, 0, SGIDataColumnTypeConstants.X_VALUE, value, 0.0));
  }
}
