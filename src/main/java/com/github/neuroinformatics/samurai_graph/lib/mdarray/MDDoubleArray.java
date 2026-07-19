package com.github.neuroinformatics.samurai_graph.lib.mdarray;

import java.util.Arrays;

/** A double multidimensional array compatibility layer. */
public class MDDoubleArray {

  private double[] mFlatArray;
  private final int[] mDimensions;

  /** Constructs an MDDoubleArray from a double[] flat array (inferred as 1D). */
  public MDDoubleArray(final double[] flatArray) {
    this(flatArray, new int[] {flatArray.length});
  }

  /** Constructs an MDDoubleArray from a double[] flat array and dimensions. */
  public MDDoubleArray(final double[] flatArray, final int[] dimensions) {
    if (flatArray == null) {
      throw new IllegalArgumentException("flatArray is null");
    }
    if (dimensions == null || dimensions.length == 0) {
      throw new IllegalArgumentException("dimensions is null or empty");
    }
    this.mFlatArray = flatArray;
    this.mDimensions = dimensions.clone();
  }

  /** Constructs an empty MDDoubleArray with the given dimensions (initialized to 0.0). */
  public MDDoubleArray(final int[] dimensions) {
    if (dimensions == null || dimensions.length == 0) {
      throw new IllegalArgumentException("dimensions is null or empty");
    }
    this.mDimensions = dimensions.clone();
    int size = 1;
    for (int d : dimensions) {
      size *= d;
    }
    this.mFlatArray = new double[size];
  }

  /** Constructs an MDDoubleArray from a single-element double value. */
  public MDDoubleArray(final double value) {
    this.mDimensions = new int[] {1};
    this.mFlatArray = new double[] {value};
  }

  /** Returns the dimensions of the array. */
  public int[] getDimensions() {
    return this.mDimensions.clone();
  }

  /** Returns the rank (number of dimensions). */
  public int getRank() {
    return this.mDimensions.length;
  }

  /** Returns the total number of elements. */
  public long size() {
    return this.mFlatArray.length;
  }

  /** Returns the element at the given indices. */
  public double get(final int[] indices) {
    return this.mFlatArray[toFlatIndex(indices)];
  }

  /** Returns the element at the given flat index. */
  public double get(final int flatIndex) {
    return this.mFlatArray[flatIndex];
  }

  /** Sets the element at the given multi-dimensional indices. */
  public void set(final int[] indices, final double value) {
    this.mFlatArray[toFlatIndex(indices)] = value;
  }

  /** Sets the element at the given multi-dimensional indices (value first). */
  public void set(final double value, final int[] indices) {
    this.mFlatArray[toFlatIndex(indices)] = value;
  }

  /** Sets the element at the given flat index. */
  public void set(final double value, final int flatIndex) {
    this.mFlatArray[flatIndex] = value;
  }

  /** Sets the element at the given 2D indices. */
  public void set(final double value, final int i, final int j) {
    set(value, new int[] {i, j});
  }

  /** Sets the object value at the given flat index (type conversion handled). */
  public void setToObject(final Object value, final int index) {
    if (value instanceof Number) {
      this.mFlatArray[index] = ((Number) value).doubleValue();
    } else {
      this.mFlatArray[index] = Double.parseDouble(String.valueOf(value));
    }
  }

  /** Returns the underlying flat array reference. */
  public double[] getFlatArrayRef() {
    return this.mFlatArray;
  }

  private int toFlatIndex(final int[] indices) {
    if (indices == null || indices.length != this.mDimensions.length) {
      throw new IllegalArgumentException(
          "Invalid indices length: "
              + (indices == null ? "null" : indices.length)
              + " != "
              + this.mDimensions.length);
    }
    int index = 0;
    int stride = 1;
    for (int i = this.mDimensions.length - 1; i >= 0; i--) {
      int dimIndex = indices[i];
      if (dimIndex < 0 || dimIndex >= this.mDimensions[i]) {
        throw new IndexOutOfBoundsException(
            "Dimension " + i + " index " + dimIndex + " out of bounds");
      }
      index += dimIndex * stride;
      stride *= this.mDimensions[i];
    }
    return index;
  }

  @Override
  public String toString() {
    return "MDDoubleArray{dimensions=" + Arrays.toString(this.mDimensions) + "}";
  }
}
