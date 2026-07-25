package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;

class SGNetCDFPickUpDimensionInfoTest {

  @Test
  void defaultConstructor() {
    SGNetCDFPickUpDimensionInfo info = new SGNetCDFPickUpDimensionInfo();
    assertEquals(null, info.getDimensionName());
    assertEquals(null, info.getIndices());
  }

  @Test
  void constructorWithNameAndIndices() {
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    SGNetCDFPickUpDimensionInfo info = new SGNetCDFPickUpDimensionInfo("lat", indices);
    assertEquals("lat", info.getDimensionName());
    assertEquals(indices, info.getIndices());
  }

  @Test
  void constructorWithNullName() {
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    SGNetCDFPickUpDimensionInfo info = new SGNetCDFPickUpDimensionInfo(null, indices);
    assertEquals(null, info.getDimensionName());
  }

  @Test
  void equalsSameValues() {
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    SGNetCDFPickUpDimensionInfo a = new SGNetCDFPickUpDimensionInfo("lat", indices);
    SGNetCDFPickUpDimensionInfo b = new SGNetCDFPickUpDimensionInfo("lat", indices);
    assertEquals(a, b);
  }

  @Test
  void equalsDifferentName() {
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    SGNetCDFPickUpDimensionInfo a = new SGNetCDFPickUpDimensionInfo("lat", indices);
    SGNetCDFPickUpDimensionInfo b = new SGNetCDFPickUpDimensionInfo("lon", indices);
    assertNotEquals(a, b);
  }

  @Test
  void equalsDifferentIndices() {
    SGNetCDFPickUpDimensionInfo a =
        new SGNetCDFPickUpDimensionInfo("lat", new SGIntegerSeriesSet(0, 2, 1));
    SGNetCDFPickUpDimensionInfo b =
        new SGNetCDFPickUpDimensionInfo("lat", new SGIntegerSeriesSet(0, 3, 1));
    assertNotEquals(a, b);
  }

  @Test
  void equalsNull() {
    SGNetCDFPickUpDimensionInfo info =
        new SGNetCDFPickUpDimensionInfo("lat", new SGIntegerSeriesSet(0, 2, 1));
    assertNotEquals(null, info);
  }

  @Test
  void hashCodeConsistentWithEquals() {
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    SGNetCDFPickUpDimensionInfo a = new SGNetCDFPickUpDimensionInfo("lat", indices);
    SGNetCDFPickUpDimensionInfo b = new SGNetCDFPickUpDimensionInfo("lat", indices);
    assertEquals(a.hashCode(), b.hashCode());
  }
}
