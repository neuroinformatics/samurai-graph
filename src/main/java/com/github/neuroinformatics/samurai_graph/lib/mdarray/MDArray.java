package com.github.neuroinformatics.samurai_graph.lib.mdarray;

import java.util.Arrays;

/** A generic n-dimensional array compatibility layer. */
public class MDArray<T> {

  private final T[] mFlatArray;
  private final int[] mDimensions;

  @SuppressWarnings("unchecked")
  public MDArray(final T[] flatArray, final int[] dimensions) {
    this.mFlatArray = flatArray;
    if (dimensions == null || dimensions.length == 0) {
      this.mDimensions = new int[] {flatArray.length};
    } else {
      this.mDimensions = dimensions.clone();
    }
  }

  @SuppressWarnings("unchecked")
  public MDArray(final T[] flatArray) {
    this.mFlatArray = flatArray;
    this.mDimensions = new int[] {flatArray.length};
  }

  public MDArray(final int[] dimensions) {
    this.mDimensions = dimensions.clone();
    int size = 1;
    for (int d : dimensions) size *= d;
    @SuppressWarnings("unchecked")
    T[] arr = (T[]) new Object[size];
    this.mFlatArray = arr;
  }

  public int[] getDimensions() {
    return this.mDimensions.clone();
  }

  public int getRank() {
    return this.mDimensions.length;
  }

  public long size() {
    return this.mFlatArray.length;
  }

  public T get(final int[] indices) {
    return this.mFlatArray[toFlatIndex(indices)];
  }

  public void set(final int[] indices, final T value) {
    this.mFlatArray[toFlatIndex(indices)] = value;
  }

  public void set(final T value, final int index) {
    this.mFlatArray[index] = value;
  }

  public T[] getFlatArrayRef() {
    return this.mFlatArray;
  }

  private int toFlatIndex(final int[] indices) {
    int index = 0;
    int stride = 1;
    for (int i = this.mDimensions.length - 1; i >= 0; i--) {
      index += indices[i] * stride;
      stride *= this.mDimensions[i];
    }
    return index;
  }

  @Override
  public String toString() {
    return "MDArray{dimensions=" + Arrays.toString(this.mDimensions) + "}";
  }
}
