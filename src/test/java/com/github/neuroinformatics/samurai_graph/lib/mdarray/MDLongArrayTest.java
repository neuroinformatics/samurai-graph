package com.github.neuroinformatics.samurai_graph.lib.mdarray;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link MDLongArray}. */
class MDLongArrayTest {

  @Test
  void constructorFromFlatArrayCreates1DArray() {
    long[] data = {1L, 2L, 3L, 4L, 5L};
    MDLongArray arr = new MDLongArray(data);

    assertEquals(1, arr.getRank());
    assertArrayEquals(new int[] {5}, arr.getDimensions());
    assertEquals(5, arr.size());
  }

  @Test
  void constructorFromFlatArrayAndDimensions() {
    long[] data = {1L, 2L, 3L, 4L, 5L, 6L};
    MDLongArray arr = new MDLongArray(data, new int[] {2, 3});

    assertEquals(2, arr.getRank());
    assertArrayEquals(new int[] {2, 3}, arr.getDimensions());
    assertEquals(6, arr.size());
  }

  @Test
  void emptyFactoryCreatesZeroFilledArray() {
    MDLongArray arr = MDLongArray.empty(new int[] {3, 4});

    assertEquals(2, arr.getRank());
    assertEquals(12, arr.size());
    assertEquals(0L, arr.get(new int[] {0, 0}));
    assertEquals(0L, arr.get(new int[] {2, 3}));
  }

  @Test
  void getSetWithMultiDimensionalIndices() {
    MDLongArray arr = MDLongArray.empty(new int[] {2, 3});

    arr.set(new int[] {0, 0}, 100L);
    arr.set(new int[] {0, 1}, 200L);
    arr.set(new int[] {0, 2}, 300L);
    arr.set(new int[] {1, 0}, 400L);
    arr.set(new int[] {1, 1}, 500L);
    arr.set(new int[] {1, 2}, 600L);

    assertEquals(100L, arr.get(new int[] {0, 0}));
    assertEquals(200L, arr.get(new int[] {0, 1}));
    assertEquals(300L, arr.get(new int[] {0, 2}));
    assertEquals(400L, arr.get(new int[] {1, 0}));
    assertEquals(500L, arr.get(new int[] {1, 1}));
    assertEquals(600L, arr.get(new int[] {1, 2}));
  }

  @Test
  void getSetWithFlatIndex() {
    long[] data = {1L, 2L, 3L, 4L, 5L};
    MDLongArray arr = new MDLongArray(data);

    arr.set(999L, 2);
    assertEquals(999L, arr.getFlatArrayRef()[2]);
  }

  @Test
  void getDimensionsReturnsClone() {
    MDLongArray arr = new MDLongArray(new long[6], new int[] {2, 3});
    int[] dims = arr.getDimensions();
    dims[0] = 99;
    assertArrayEquals(new int[] {2, 3}, arr.getDimensions());
  }

  @Test
  void threeDimensionalArray() {
    MDLongArray arr = MDLongArray.empty(new int[] {2, 3, 4});

    assertEquals(3, arr.getRank());
    assertEquals(24, arr.size());

    arr.set(new int[] {1, 2, 3}, 42L);
    assertEquals(42L, arr.get(new int[] {1, 2, 3}));
  }

  @Test
  void toStringContainsDimensions() {
    MDLongArray arr = new MDLongArray(new long[6], new int[] {2, 3});
    String str = arr.toString();
    assertEquals("MDLongArray{dimensions=[2, 3]}", str);
  }
}
