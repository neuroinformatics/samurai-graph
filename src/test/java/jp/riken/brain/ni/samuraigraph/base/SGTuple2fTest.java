package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGTuple2f}. */
class SGTuple2fTest {

  @Test
  void defaultConstructorClearsToZero() {
    SGTuple2f t = new SGTuple2f();
    assertEquals(0.0f, t.x, 0.0f);
    assertEquals(0.0f, t.y, 0.0f);
  }

  @Test
  void constructorWithValues() {
    SGTuple2f t = new SGTuple2f(3.5f, 7.2f);
    assertEquals(3.5f, t.x, 0.0f);
    assertEquals(7.2f, t.y, 0.0f);
  }

  @Test
  void constructorFromTuple() {
    SGTuple2f original = new SGTuple2f(1.0f, 2.0f);
    SGTuple2f copy = new SGTuple2f(original);
    assertEquals(1.0f, copy.x, 0.0f);
    assertEquals(2.0f, copy.y, 0.0f);
  }

  @Test
  void setXAndSetY() {
    SGTuple2f t = new SGTuple2f();
    t.setX(5.0f);
    t.setY(10.0f);
    assertEquals(5.0f, t.x, 0.0f);
    assertEquals(10.0f, t.y, 0.0f);
  }

  @Test
  void setValuesWithCoordinates() {
    SGTuple2f t = new SGTuple2f();
    t.setValues(1.5f, 2.5f);
    assertEquals(1.5f, t.x, 0.0f);
    assertEquals(2.5f, t.y, 0.0f);
  }

  @Test
  void setValuesFromTuple() {
    SGTuple2f src = new SGTuple2f(3.0f, 4.0f);
    SGTuple2f dst = new SGTuple2f();
    dst.setValues(src);
    assertEquals(3.0f, dst.x, 0.0f);
    assertEquals(4.0f, dst.y, 0.0f);
  }

  @Test
  void setValuesFromNullTupleThrows() {
    SGTuple2f t = new SGTuple2f();
    assertThrows(IllegalArgumentException.class, () -> t.setValues((SGTuple2f) null));
  }

  @Test
  void clear() {
    SGTuple2f t = new SGTuple2f(1.0f, 2.0f);
    t.clear();
    assertEquals(0.0f, t.x, 0.0f);
    assertEquals(0.0f, t.y, 0.0f);
  }

  @Test
  void equalsSameValues() {
    SGTuple2f a = new SGTuple2f(1.0f, 2.0f);
    SGTuple2f b = new SGTuple2f(1.0f, 2.0f);
    assertEquals(b, a);
  }

  @Test
  void equalsDifferentValues() {
    SGTuple2f a = new SGTuple2f(1.0f, 2.0f);
    SGTuple2f b = new SGTuple2f(3.0f, 4.0f);
    assertFalse(b.equals(a));
  }

  @Test
  void hashCodeConsistentWithEquals() {
    SGTuple2f a = new SGTuple2f(1.0f, 2.0f);
    SGTuple2f b = new SGTuple2f(1.0f, 2.0f);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  void isInfiniteWithInfiniteValue() {
    SGTuple2f t = new SGTuple2f(Float.POSITIVE_INFINITY, 1.0f);
    assertTrue(t.isInfinite());
  }

  @Test
  void isNaNWithNaNValue() {
    SGTuple2f t = new SGTuple2f(Float.NaN, 1.0f);
    assertTrue(t.isNaN());
  }

  @Test
  void isZeroWhenBothZero() {
    SGTuple2f t = new SGTuple2f(0.0f, 0.0f);
    assertTrue(t.isZero());
  }

  @Test
  void cloneProducesIndependentCopy() {
    SGTuple2f original = new SGTuple2f(1.0f, 2.0f);
    SGTuple2f copy = (SGTuple2f) original.clone();

    assertNotSame(original, copy);
    assertEquals(1.0f, copy.x, 0.0f);
    original.x = 99.0f;
    assertEquals(1.0f, copy.x, 0.0f);
  }
}
