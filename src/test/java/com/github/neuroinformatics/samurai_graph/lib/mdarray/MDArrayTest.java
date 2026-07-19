package com.github.neuroinformatics.samurai_graph.lib.mdarray;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link MDArray}. */
class MDArrayTest {

  @Test
  void constructorFromFlatArrayInfers1D() {
    Object[] data = new Object[] {"a", "b", "c"};
    MDArray<Object> arr = new MDArray<>(data);

    assertEquals(1, arr.getRank());
    assertArrayEquals(new int[] {3}, arr.getDimensions());
    assertEquals(3, arr.size());
  }

  @Test
  void constructorFromFlatArrayAndDimensions() {
    Object[] data = new Object[] {"a", "b", "c", "d", "e", "f"};
    MDArray<Object> arr = new MDArray<>(data, new int[] {2, 3});

    assertEquals(2, arr.getRank());
    assertArrayEquals(new int[] {2, 3}, arr.getDimensions());
    assertEquals(6, arr.size());
  }

  @Test
  void constructorFromFlatArrayWithNullDimensionsInfers1D() {
    Object[] data = new Object[] {"x", "y"};
    MDArray<Object> arr = new MDArray<>(data, null);

    assertEquals(1, arr.getRank());
    assertArrayEquals(new int[] {2}, arr.getDimensions());
  }

  @Test
  void constructorFromFlatArrayWithEmptyDimensionsInfers1D() {
    Object[] data = new Object[] {"x", "y"};
    MDArray<Object> arr = new MDArray<>(data, new int[] {});

    assertEquals(1, arr.getRank());
    assertArrayEquals(new int[] {2}, arr.getDimensions());
  }

  @Test
  void constructorFromDimensionsCreatesNullFilledArray() {
    MDArray<String> arr = new MDArray<>(new int[] {2, 3});

    assertEquals(2, arr.getRank());
    assertEquals(6, arr.size());
    assertNull(arr.get(new int[] {0, 0}));
  }

  @Test
  void getSetWithMultiDimensionalIndices() {
    MDArray<String> arr = new MDArray<>(new int[] {2, 3});

    arr.set(new int[] {0, 0}, "a");
    arr.set(new int[] {0, 1}, "b");
    arr.set(new int[] {0, 2}, "c");
    arr.set(new int[] {1, 0}, "d");
    arr.set(new int[] {1, 1}, "e");
    arr.set(new int[] {1, 2}, "f");

    assertEquals("a", arr.get(new int[] {0, 0}));
    assertEquals("b", arr.get(new int[] {0, 1}));
    assertEquals("c", arr.get(new int[] {0, 2}));
    assertEquals("d", arr.get(new int[] {1, 0}));
    assertEquals("e", arr.get(new int[] {1, 1}));
    assertEquals("f", arr.get(new int[] {1, 2}));
  }

  @Test
  void getSetWithFlatIndex() {
    Object[] data = new Object[] {"x", "y", "z"};
    MDArray<Object> arr = new MDArray<>(data);

    arr.set("replaced", 1);
    assertEquals("replaced", arr.get(new int[] {1}));
  }

  @Test
  void getDimensionsReturnsClone() {
    MDArray<Integer> arr = new MDArray<>(new int[] {2, 3});
    int[] dims = arr.getDimensions();
    dims[0] = 99;
    assertArrayEquals(new int[] {2, 3}, arr.getDimensions());
  }

  @Test
  void getFlatArrayRefReturnsMutableReference() {
    Object[] data = new Object[] {"a", "b"};
    MDArray<Object> arr = new MDArray<>(data);
    Object[] flat = arr.getFlatArrayRef();
    flat[0] = "replaced";
    assertEquals("replaced", arr.get(new int[] {0}));
  }

  @Test
  void threeDimensionalArray() {
    MDArray<Integer> arr = new MDArray<>(new int[] {2, 3, 4});

    assertEquals(3, arr.getRank());
    assertEquals(24, arr.size());

    arr.set(new int[] {1, 2, 3}, 42);
    assertEquals(42, arr.get(new int[] {1, 2, 3}));
  }

  @Test
  void toStringContainsDimensions() {
    MDArray<String> arr = new MDArray<>(new int[] {2, 3});
    String str = arr.toString();
    assertEquals("MDArray{dimensions=[2, 3]}", str);
  }
}
