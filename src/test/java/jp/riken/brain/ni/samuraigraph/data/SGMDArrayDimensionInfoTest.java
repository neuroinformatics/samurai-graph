package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class SGMDArrayDimensionInfoTest {

  @Test
  void constructorWithNameAndIndex() {
    SGMDArrayDimensionInfo info = new SGMDArrayDimensionInfo("x", 0);
    assertEquals("x", info.getName());
    assertEquals(0, info.getIndex());
  }

  @Test
  void constructorWithNullName() {
    SGMDArrayDimensionInfo info = new SGMDArrayDimensionInfo(null, 1);
    assertEquals(null, info.getName());
    assertEquals(1, info.getIndex());
  }

  @Test
  void equalsSameValues() {
    SGMDArrayDimensionInfo a = new SGMDArrayDimensionInfo("x", 0);
    SGMDArrayDimensionInfo b = new SGMDArrayDimensionInfo("x", 0);
    assertEquals(a, b);
  }

  @Test
  void equalsDifferentName() {
    SGMDArrayDimensionInfo a = new SGMDArrayDimensionInfo("x", 0);
    SGMDArrayDimensionInfo b = new SGMDArrayDimensionInfo("y", 0);
    assertNotEquals(a, b);
  }

  @Test
  void equalsDifferentIndex() {
    SGMDArrayDimensionInfo a = new SGMDArrayDimensionInfo("x", 0);
    SGMDArrayDimensionInfo b = new SGMDArrayDimensionInfo("x", 1);
    assertNotEquals(a, b);
  }

  @Test
  void equalsNull() {
    SGMDArrayDimensionInfo a = new SGMDArrayDimensionInfo("x", 0);
    assertFalse(a.equals(null));
  }

  @Test
  void equalsDifferentType() {
    SGMDArrayDimensionInfo a = new SGMDArrayDimensionInfo("x", 0);
    assertFalse(a.equals("x"));
  }

  @Test
  void toStringContainsValues() {
    SGMDArrayDimensionInfo info = new SGMDArrayDimensionInfo("t", 2);
    String str = info.toString();
    assertEquals("[name=t, index=2]", str);
  }

  @Test
  void hashCodeConsistentWithEquals() {
    SGMDArrayDimensionInfo a = new SGMDArrayDimensionInfo("x", 0);
    SGMDArrayDimensionInfo b = new SGMDArrayDimensionInfo("x", 0);
    assertEquals(a.hashCode(), b.hashCode());
  }
}
