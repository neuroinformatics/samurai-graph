package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGArrayIndex}. */
class SGArrayIndexTest {

  @Test
  void defaultConstructorSetsIndexToZero() {
    SGArrayIndex idx = new SGArrayIndex();
    assertEquals(0, idx.getIndex());
  }

  @Test
  void constructorWithInitialValue() {
    SGArrayIndex idx = new SGArrayIndex(42);
    assertEquals(42, idx.getIndex());
  }

  @Test
  void setAndGet() {
    SGArrayIndex idx = new SGArrayIndex();
    idx.set(10);
    assertEquals(10, idx.getIndex());

    idx.set(-5);
    assertEquals(-5, idx.getIndex());

    idx.set(0);
    assertEquals(0, idx.getIndex());
  }

  @Test
  void equalsSameIndex() {
    SGArrayIndex a = new SGArrayIndex(5);
    SGArrayIndex b = new SGArrayIndex(5);
    assertEquals(b, a);
  }

  @Test
  void equalsDifferentIndex() {
    SGArrayIndex a = new SGArrayIndex(5);
    SGArrayIndex b = new SGArrayIndex(10);
    assertFalse(b.equals(a));
  }

  @Test
  void equalsWithNull() {
    SGArrayIndex a = new SGArrayIndex(5);
    assertFalse(a.equals(null));
  }

  @Test
  void equalsWithDifferentType() {
    SGArrayIndex a = new SGArrayIndex(5);
    assertFalse(a.equals("5"));
    assertFalse(a.equals(Integer.valueOf(5)));
  }

  @Test
  void toStringReturnsIndexAsString() {
    SGArrayIndex idx = new SGArrayIndex(42);
    assertEquals("42", idx.toString());

    idx.set(-1);
    assertEquals("-1", idx.toString());
  }

  @Test
  void cloneProducesEqualObject() {
    SGArrayIndex original = new SGArrayIndex(42);
    SGArrayIndex clone = (SGArrayIndex) original.clone();

    assertNotSame(original, clone);
    assertEquals(42, clone.getIndex());
  }

  @Test
  void cloneIsIndependent() {
    SGArrayIndex original = new SGArrayIndex(42);
    SGArrayIndex clone = (SGArrayIndex) original.clone();

    original.set(100);
    assertEquals(100, original.getIndex());
    assertEquals(42, clone.getIndex());
  }
}
