package com.github.neuroinformatics.samurai_graph.lib.mdarray;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link MDDoubleArray}. */
class MDDoubleArrayTest {

  @Test
  void constructorFromFlatArrayCreates1DArray() {
    double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};
    MDDoubleArray arr = new MDDoubleArray(data);

    assertEquals(1, arr.getRank());
    assertArrayEquals(new int[] {5}, arr.getDimensions());
    assertEquals(5, arr.size());
  }

  @Test
  void constructorFromFlatArrayAndDimensions() {
    double[] data = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    MDDoubleArray arr = new MDDoubleArray(data, new int[] {2, 3});

    assertEquals(2, arr.getRank());
    assertArrayEquals(new int[] {2, 3}, arr.getDimensions());
    assertEquals(6, arr.size());
  }

  @Test
  void constructorFromDimensionsCreatesZeroFilledArray() {
    MDDoubleArray arr = new MDDoubleArray(new int[] {3, 4});

    assertEquals(2, arr.getRank());
    assertEquals(12, arr.size());
    assertEquals(0.0, arr.get(new int[] {0, 0}), 0.0);
    assertEquals(0.0, arr.get(new int[] {2, 3}), 0.0);
  }

  @Test
  void constructorFromSingleValue() {
    MDDoubleArray arr = new MDDoubleArray(42.0);

    assertEquals(1, arr.getRank());
    assertArrayEquals(new int[] {1}, arr.getDimensions());
    assertEquals(1, arr.size());
    assertEquals(42.0, arr.get(new int[] {0}), 0.0);
  }

  @Test
  void constructorRejectsNullFlatArray() {
    assertThrows(NullPointerException.class, () -> new MDDoubleArray((double[]) null));
  }

  @Test
  void constructorRejectsNullDimensions() {
    assertThrows(
        IllegalArgumentException.class, () -> new MDDoubleArray(new double[] {1.0}, (int[]) null));
  }

  @Test
  void constructorRejectsEmptyDimensions() {
    assertThrows(
        IllegalArgumentException.class, () -> new MDDoubleArray(new double[] {1.0}, new int[] {}));
  }

  @Test
  void getSetWithMultiDimensionalIndices() {
    MDDoubleArray arr = new MDDoubleArray(new int[] {2, 3});

    arr.set(new int[] {0, 0}, 10.0);
    arr.set(new int[] {0, 1}, 20.0);
    arr.set(new int[] {0, 2}, 30.0);
    arr.set(new int[] {1, 0}, 40.0);
    arr.set(new int[] {1, 1}, 50.0);
    arr.set(new int[] {1, 2}, 60.0);

    assertEquals(10.0, arr.get(new int[] {0, 0}), 0.0);
    assertEquals(20.0, arr.get(new int[] {0, 1}), 0.0);
    assertEquals(30.0, arr.get(new int[] {0, 2}), 0.0);
    assertEquals(40.0, arr.get(new int[] {1, 0}), 0.0);
    assertEquals(50.0, arr.get(new int[] {1, 1}), 0.0);
    assertEquals(60.0, arr.get(new int[] {1, 2}), 0.0);
  }

  @Test
  void getSetWithFlatIndex() {
    double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};
    MDDoubleArray arr = new MDDoubleArray(data);

    arr.set(99.0, 2);
    assertEquals(99.0, arr.get(2), 0.0);
  }

  @Test
  void getSetWith2DConvenienceMethod() {
    MDDoubleArray arr = new MDDoubleArray(new int[] {2, 3});
    arr.set(77.7, 1, 2);
    assertEquals(77.7, arr.get(new int[] {1, 2}), 0.0);
  }

  @Test
  void getSetWithValueFirstOverload() {
    MDDoubleArray arr = new MDDoubleArray(new int[] {2, 3});
    arr.set(55.5, new int[] {1, 0});
    assertEquals(55.5, arr.get(new int[] {1, 0}), 0.0);
  }

  @Test
  void setToObjectWithNumber() {
    MDDoubleArray arr = new MDDoubleArray(new double[3]);
    arr.setToObject(Integer.valueOf(42), 0);
    arr.setToObject(Float.valueOf(3.14f), 1);
    arr.setToObject(Long.valueOf(100L), 2);

    assertEquals(42.0, arr.get(0), 0.0);
    assertEquals(3.14, arr.get(1), 0.001);
    assertEquals(100.0, arr.get(2), 0.0);
  }

  @Test
  void setToObjectWithString() {
    MDDoubleArray arr = new MDDoubleArray(new double[1]);
    arr.setToObject("123.45", 0);
    assertEquals(123.45, arr.get(0), 0.001);
  }

  @Test
  void getSetRejectsWrongIndexLength() {
    MDDoubleArray arr = new MDDoubleArray(new int[] {2, 3});
    assertThrows(IllegalArgumentException.class, () -> arr.get(new int[] {0}));
    assertThrows(IllegalArgumentException.class, () -> arr.set(new int[] {0}, 1.0));
  }

  @Test
  void getSetRejectsOutOfBoundsIndex() {
    MDDoubleArray arr = new MDDoubleArray(new int[] {2, 3});
    assertThrows(IndexOutOfBoundsException.class, () -> arr.get(new int[] {2, 0}));
    assertThrows(IndexOutOfBoundsException.class, () -> arr.get(new int[] {0, 3}));
    assertThrows(IndexOutOfBoundsException.class, () -> arr.get(new int[] {-1, 0}));
  }

  @Test
  void getDimensionsReturnsClone() {
    MDDoubleArray arr = new MDDoubleArray(new int[] {2, 3});
    int[] dims = arr.getDimensions();
    dims[0] = 99;
    assertArrayEquals(new int[] {2, 3}, arr.getDimensions());
  }

  @Test
  void getFlatArrayRefReturnsMutableReference() {
    double[] data = {1.0, 2.0, 3.0};
    MDDoubleArray arr = new MDDoubleArray(data);
    double[] flat = arr.getFlatArrayRef();
    flat[0] = 999.0;
    assertEquals(999.0, arr.get(0), 0.0);
  }

  @Test
  void threeDimensionalArray() {
    MDDoubleArray arr = new MDDoubleArray(new int[] {2, 3, 4});

    assertEquals(3, arr.getRank());
    assertEquals(24, arr.size());

    arr.set(new int[] {1, 2, 3}, 42.0);
    assertEquals(42.0, arr.get(new int[] {1, 2, 3}), 0.0);
  }

  @Test
  void toStringContainsDimensions() {
    MDDoubleArray arr = new MDDoubleArray(new int[] {2, 3});
    String str = arr.toString();
    assertTrue(str.contains("MDDoubleArray"));
    assertTrue(str.contains("[2, 3]"));
  }
}
