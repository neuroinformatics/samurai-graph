package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDataStrideUtility}. */
class SGDataStrideUtilityTest {

  @Test
  void vxyComponentTypesFollowPolarSelection() {
    assertEquals(
        SGIDataColumnTypeConstants.X_COMPONENT,
        SGDataStrideUtility.getVXYFirstComponentColumnType(false));
    assertEquals(
        SGIDataColumnTypeConstants.Y_COMPONENT,
        SGDataStrideUtility.getVXYSecondComponentColumnType(false));
    assertEquals(
        SGIDataColumnTypeConstants.MAGNITUDE,
        SGDataStrideUtility.getVXYFirstComponentColumnType(true));
    assertEquals(
        SGIDataColumnTypeConstants.ANGLE,
        SGDataStrideUtility.getVXYSecondComponentColumnType(true));
  }

  @Test
  void vxyComponentTypesReadPolarFlagFromInfoMap() {
    Map<String, Object> polar = new HashMap<String, Object>();
    polar.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    assertEquals(
        SGIDataColumnTypeConstants.MAGNITUDE,
        SGDataStrideUtility.getVXYFirstComponentColumnType(polar));
    assertEquals(
        SGIDataColumnTypeConstants.ANGLE,
        SGDataStrideUtility.getVXYSecondComponentColumnType(polar));

    Map<String, Object> orthogonal = new HashMap<String, Object>();
    orthogonal.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertEquals(
        SGIDataColumnTypeConstants.X_COMPONENT,
        SGDataStrideUtility.getVXYFirstComponentColumnType(orthogonal));
    assertEquals(
        SGIDataColumnTypeConstants.Y_COMPONENT,
        SGDataStrideUtility.getVXYSecondComponentColumnType(orthogonal));
  }

  @Test
  void vxyComponentTypesReturnNullWithoutPolarFlag() {
    assertNull(SGDataStrideUtility.getVXYFirstComponentColumnType(new HashMap<String, Object>()));
    assertNull(SGDataStrideUtility.getVXYSecondComponentColumnType(new HashMap<String, Object>()));
  }

  @Test
  void createDefaultStepSeriesUsesUnitStepForSmallLength() {
    SGIntegerSeries series = SGDataStrideUtility.createDefaultStepSeries(3);
    assertNotNull(series);
    assertEquals(0, series.getMin());
    assertEquals(2, series.getMax());
    assertEquals(3, series.getLength());
  }

  @Test
  void createDefaultStepSeriesIncreasesStepForLargeLength() {
    SGIntegerSeries series = SGDataStrideUtility.createDefaultStepSeries(10);
    assertNotNull(series);
    assertEquals(0, series.getMin());
    assertEquals(8, series.getMax());
    assertEquals(5, series.getLength());
    assertEquals(2, series.getStep().getNumber());
  }
}
