package com.github.neuroinformatics.samurai_graph.lib.mdarray;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link MDIntArray}. */
class MDIntArrayTest {

  @Test
  void constructorFromFlatArrayCreates1DArray() {
    int[] data = {1, 2, 3, 4, 5};
    MDIntArray arr = new MDIntArray(data);

    assertEquals(1, arr.getRank());
    assertArrayEquals(new int[] {5}, arr.getDimensions());
    assertEquals(5, arr.size());
  }

  @Test
  void constructorFromFlatArrayAndDimensions() {
    int[] data = {1, 2, 3, 4, 5, 6};
    MDIntArray arr = new MDIntArray(data, new int[] {2, 3});

    assertEquals(2, arr.getRank());
    assertArrayEquals(new int[] {2, 3}, arr.getDimensions());
    assertEquals(6, arr.size());
  }

  @Test
  void emptyFactoryCreatesZeroFilledArray() {
    MDIntArray arr = MDIntArray.empty(new int[] {3, 4});

    assertEquals(2, arr.getRank());
    assertEquals(12, arr.size());
    assertEquals(0, arr.get(new int[] {0, 0}));
    assertEquals(0, arr.get(new int[] {2, 3}));
  }

  @Test
  void getSetWithMultiDimensionalIndices() {
    MDIntArray arr = MDIntArray.empty(new int[] {2, 3});

    arr.set(new int[] {0, 0}, 10);
    arr.set(new int[] {0, 1}, 20);
    arr.set(new int[] {0, 2}, 30);
    arr.set(new int[] {1, 0}, 40);
    arr.set(new int[] {1, 1}, 50);
    arr.set(new int[] {1, 2}, 60);

    assertEquals(10, arr.get(new int[] {0, 0}));
    assertEquals(20, arr.get(new int[] {0, 1}));
    assertEquals(30, arr.get(new int[] {0, 2}));
    assertEquals(40, arr.get(new int[] {1, 0}));
    assertEquals(50, arr.get(new int[] {1, 1}));
    assertEquals(60, arr.get(new int[] {1, 2}));
  }

  @Test
  void getSetWithFlatIndex() {
    int[] data = {1, 2, 3, 4, 5};
    MDIntArray arr = new MDIntArray(data);

    arr.set(99, 2);
    assertEquals(99, arr.getFlatArrayRef()[2]);
  }

  @Test
  void getDimensionsReturnsClone() {
    MDIntArray arr = new MDIntArray(new int[6], new int[] {2, 3});
    int[] dims = arr.getDimensions();
    dims[0] = 99;
    assertArrayEquals(new int[] {2, 3}, arr.getDimensions());
  }

  @Test
  void getFlatArrayRefReturnsMutableReference() {
    int[] data = {1, 2, 3};
    MDIntArray arr = new MDIntArray(data);
    int[] flat = arr.getFlatArrayRef();
    flat[0] = 999;
    assertEquals(999, arr.getFlatArrayRef()[0]);
  }

  @Test
  void threeDimensionalArray() {
    MDIntArray arr = MDIntArray.empty(new int[] {2, 3, 4});

    assertEquals(3, arr.getRank());
    assertEquals(24, arr.size());

    arr.set(new int[] {1, 2, 3}, 42);
    assertEquals(42, arr.get(new int[] {1, 2, 3}));
  }
}
