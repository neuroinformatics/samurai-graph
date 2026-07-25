package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;

class SGMDArrayPickUpDimensionInfoTest {

  @Test
  void defaultConstructor() {
    SGMDArrayPickUpDimensionInfo info = new SGMDArrayPickUpDimensionInfo();
    assertEquals(null, info.getIndices());
    assertTrue(info.getDimensionMap().isEmpty());
  }

  @Test
  void constructorWithMapAndIndices() {
    Map<String, Integer> map = new HashMap<>();
    map.put("x", 0);
    map.put("y", 1);
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    SGMDArrayPickUpDimensionInfo info = new SGMDArrayPickUpDimensionInfo(map, indices);
    assertEquals(indices, info.getIndices());
    assertEquals(Integer.valueOf(0), info.getDimension("x"));
    assertEquals(Integer.valueOf(1), info.getDimension("y"));
  }

  @Test
  void getDimensionReturnsNullForUnknown() {
    Map<String, Integer> map = new HashMap<>();
    map.put("x", 0);
    SGMDArrayPickUpDimensionInfo info =
        new SGMDArrayPickUpDimensionInfo(map, new SGIntegerSeriesSet(0, 2, 1));
    assertEquals(null, info.getDimension("unknown"));
  }

  @Test
  void getDimensionMapReturnsDefensiveCopy() {
    Map<String, Integer> map = new HashMap<>();
    map.put("x", 0);
    SGMDArrayPickUpDimensionInfo info =
        new SGMDArrayPickUpDimensionInfo(map, new SGIntegerSeriesSet(0, 2, 1));
    Map<String, Integer> retrieved = info.getDimensionMap();
    retrieved.put("y", 1);
    assertFalse(info.getDimensionMap().containsKey("y"));
  }

  @Test
  void constructorCopiesMap() {
    Map<String, Integer> map = new HashMap<>();
    map.put("x", 0);
    SGMDArrayPickUpDimensionInfo info =
        new SGMDArrayPickUpDimensionInfo(map, new SGIntegerSeriesSet(0, 2, 1));
    map.put("y", 1);
    assertFalse(info.getDimensionMap().containsKey("y"));
  }

  @Test
  void getVariableNames() {
    Map<String, Integer> map = new HashMap<>();
    map.put("x", 0);
    map.put("y", 1);
    SGMDArrayPickUpDimensionInfo info =
        new SGMDArrayPickUpDimensionInfo(map, new SGIntegerSeriesSet(0, 2, 1));
    assertTrue(info.getVariableNames().contains("x"));
    assertTrue(info.getVariableNames().contains("y"));
    assertEquals(2, info.getVariableNames().size());
  }

  @Test
  void equalsSameValues() {
    Map<String, Integer> map = new HashMap<>();
    map.put("x", 0);
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    SGMDArrayPickUpDimensionInfo a = new SGMDArrayPickUpDimensionInfo(map, indices);
    SGMDArrayPickUpDimensionInfo b = new SGMDArrayPickUpDimensionInfo(map, indices);
    assertEquals(a, b);
  }

  @Test
  void equalsDifferentMap() {
    Map<String, Integer> map1 = new HashMap<>();
    map1.put("x", 0);
    Map<String, Integer> map2 = new HashMap<>();
    map2.put("y", 1);
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    SGMDArrayPickUpDimensionInfo a = new SGMDArrayPickUpDimensionInfo(map1, indices);
    SGMDArrayPickUpDimensionInfo b = new SGMDArrayPickUpDimensionInfo(map2, indices);
    assertNotEquals(a, b);
  }

  @Test
  void equalsDifferentIndices() {
    Map<String, Integer> map = new HashMap<>();
    map.put("x", 0);
    SGMDArrayPickUpDimensionInfo a =
        new SGMDArrayPickUpDimensionInfo(map, new SGIntegerSeriesSet(0, 2, 1));
    SGMDArrayPickUpDimensionInfo b =
        new SGMDArrayPickUpDimensionInfo(map, new SGIntegerSeriesSet(0, 3, 1));
    assertNotEquals(a, b);
  }

  @Test
  void hashCodeConsistentWithEquals() {
    Map<String, Integer> map = new HashMap<>();
    map.put("x", 0);
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    SGMDArrayPickUpDimensionInfo a = new SGMDArrayPickUpDimensionInfo(map, indices);
    SGMDArrayPickUpDimensionInfo b = new SGMDArrayPickUpDimensionInfo(map, indices);
    assertEquals(a.hashCode(), b.hashCode());
  }
}
