package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGTuple2d}. */
class SGTuple2dTest {

  @Test
  void defaultConstructorClearsToZero() {
    SGTuple2d t = new SGTuple2d();
    assertEquals(0.0, t.x, 0.0);
    assertEquals(0.0, t.y, 0.0);
  }

  @Test
  void constructorWithValues() {
    SGTuple2d t = new SGTuple2d(3.5, 7.2);
    assertEquals(3.5, t.x, 0.0);
    assertEquals(7.2, t.y, 0.0);
  }

  @Test
  void constructorFromTuple() {
    SGTuple2d original = new SGTuple2d(1.0, 2.0);
    SGTuple2d copy = new SGTuple2d(original);

    assertEquals(1.0, copy.x, 0.0);
    assertEquals(2.0, copy.y, 0.0);
  }

  @Test
  void setXAndSetY() {
    SGTuple2d t = new SGTuple2d();
    t.setX(5.0);
    t.setY(10.0);
    assertEquals(5.0, t.x, 0.0);
    assertEquals(10.0, t.y, 0.0);
  }

  @Test
  void setValuesWithCoordinates() {
    SGTuple2d t = new SGTuple2d();
    t.setValues(1.5, 2.5);
    assertEquals(1.5, t.x, 0.0);
    assertEquals(2.5, t.y, 0.0);
  }

  @Test
  void setValuesFromTuple() {
    SGTuple2d src = new SGTuple2d(3.0, 4.0);
    SGTuple2d dst = new SGTuple2d();
    dst.setValues(src);
    assertEquals(3.0, dst.x, 0.0);
    assertEquals(4.0, dst.y, 0.0);
  }

  @Test
  void setValuesFromNullTupleThrows() {
    SGTuple2d t = new SGTuple2d();
    assertThrows(IllegalArgumentException.class, () -> t.setValues((SGTuple2d) null));
  }

  @Test
  void clear() {
    SGTuple2d t = new SGTuple2d(1.0, 2.0);
    t.clear();
    assertEquals(0.0, t.x, 0.0);
    assertEquals(0.0, t.y, 0.0);
  }

  @Test
  void toStringContainsValues() {
    SGTuple2d t = new SGTuple2d(1.0, 2.0);
    String str = t.toString();
    assertTrue(str.contains("1.0"));
    assertTrue(str.contains("2.0"));
    assertTrue(str.startsWith("("));
    assertTrue(str.endsWith(")"));
  }

  @Test
  void equalsSameValues() {
    SGTuple2d a = new SGTuple2d(1.0, 2.0);
    SGTuple2d b = new SGTuple2d(1.0, 2.0);
    assertEquals(b, a);
  }

  @Test
  void equalsDifferentValues() {
    SGTuple2d a = new SGTuple2d(1.0, 2.0);
    SGTuple2d b = new SGTuple2d(3.0, 4.0);
    assertFalse(b.equals(a));
  }

  @Test
  void equalsNullReturnsFalse() {
    SGTuple2d a = new SGTuple2d(1.0, 2.0);
    assertFalse(a.equals(null));
  }

  @Test
  void equalsDifferentTypeReturnsFalse() {
    SGTuple2d a = new SGTuple2d(1.0, 2.0);
    assertFalse(a.equals("1.0,2.0"));
  }

  @Test
  void hashCodeConsistentWithEquals() {
    SGTuple2d a = new SGTuple2d(1.0, 2.0);
    SGTuple2d b = new SGTuple2d(1.0, 2.0);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  void isInfiniteWithInfiniteX() {
    SGTuple2d t = new SGTuple2d(Double.POSITIVE_INFINITY, 1.0);
    assertTrue(t.isInfinite());
  }

  @Test
  void isInfiniteWithInfiniteY() {
    SGTuple2d t = new SGTuple2d(1.0, Double.NEGATIVE_INFINITY);
    assertTrue(t.isInfinite());
  }

  @Test
  void isInfiniteWithFiniteValues() {
    SGTuple2d t = new SGTuple2d(1.0, 2.0);
    assertFalse(t.isInfinite());
  }

  @Test
  void isNaNWithNaNX() {
    SGTuple2d t = new SGTuple2d(Double.NaN, 1.0);
    assertTrue(t.isNaN());
  }

  @Test
  void isNaNWithNormalValues() {
    SGTuple2d t = new SGTuple2d(1.0, 2.0);
    assertFalse(t.isNaN());
  }

  @Test
  void isZeroWhenBothZero() {
    SGTuple2d t = new SGTuple2d(0.0, 0.0);
    assertTrue(t.isZero());
  }

  @Test
  void isZeroWhenOneNonZero() {
    SGTuple2d t = new SGTuple2d(1.0, 0.0);
    assertFalse(t.isZero());
  }

  @Test
  void cloneProducesIndependentCopy() {
    SGTuple2d original = new SGTuple2d(1.0, 2.0);
    SGTuple2d copy = (SGTuple2d) original.clone();

    assertNotSame(original, copy);
    assertEquals(1.0, copy.x, 0.0);
    assertEquals(2.0, copy.y, 0.0);

    original.x = 99.0;
    assertEquals(1.0, copy.x, 0.0);
  }
}
