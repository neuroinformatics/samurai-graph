package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGTwoDimensionalArrayIndex}. */
class SGTwoDimensionalArrayIndexTest {

  @Test
  void defaultConstructor() {
    SGTwoDimensionalArrayIndex idx = new SGTwoDimensionalArrayIndex();
    assertEquals(0, idx.getRow());
    assertEquals(0, idx.getColumn());
  }

  @Test
  void constructorWithValues() {
    SGTwoDimensionalArrayIndex idx = new SGTwoDimensionalArrayIndex(5, 3);
    assertEquals(3, idx.getRow());
    assertEquals(5, idx.getColumn());
  }

  @Test
  void set() {
    SGTwoDimensionalArrayIndex idx = new SGTwoDimensionalArrayIndex();
    idx.set(10, 7);
    assertEquals(7, idx.getRow());
    assertEquals(10, idx.getColumn());
  }

  @Test
  void equalsSameValues() {
    SGTwoDimensionalArrayIndex a = new SGTwoDimensionalArrayIndex(3, 5);
    SGTwoDimensionalArrayIndex b = new SGTwoDimensionalArrayIndex(3, 5);
    assertEquals(b, a);
  }

  @Test
  void equalsDifferentRow() {
    SGTwoDimensionalArrayIndex a = new SGTwoDimensionalArrayIndex(3, 5);
    SGTwoDimensionalArrayIndex b = new SGTwoDimensionalArrayIndex(3, 7);
    assertFalse(b.equals(a));
  }

  @Test
  void equalsDifferentColumn() {
    SGTwoDimensionalArrayIndex a = new SGTwoDimensionalArrayIndex(3, 5);
    SGTwoDimensionalArrayIndex b = new SGTwoDimensionalArrayIndex(2, 5);
    assertFalse(b.equals(a));
  }

  @Test
  void equalsWithDifferentType() {
    SGTwoDimensionalArrayIndex a = new SGTwoDimensionalArrayIndex(3, 5);
    assertFalse(a.equals("3,5"));
    assertFalse(a.equals(null));
  }

  @Test
  void hashCodeConsistentWithEquals() {
    SGTwoDimensionalArrayIndex a = new SGTwoDimensionalArrayIndex(3, 5);
    SGTwoDimensionalArrayIndex b = new SGTwoDimensionalArrayIndex(3, 5);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  void toStringFormat() {
    SGTwoDimensionalArrayIndex idx = new SGTwoDimensionalArrayIndex(5, 3);
    assertEquals("(5,3)", idx.toString());
  }

  @Test
  void cloneProducesIndependentCopy() {
    SGTwoDimensionalArrayIndex original = new SGTwoDimensionalArrayIndex(5, 3);
    SGTwoDimensionalArrayIndex clone = (SGTwoDimensionalArrayIndex) original.clone();

    assertNotSame(original, clone);
    assertEquals(3, clone.getRow());
    assertEquals(5, clone.getColumn());
  }
}
