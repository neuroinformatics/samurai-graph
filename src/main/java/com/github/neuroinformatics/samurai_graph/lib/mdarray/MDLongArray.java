package com.github.neuroinformatics.samurai_graph.lib.mdarray;

import java.util.Arrays;

/** A long multidimensional array compatibility layer. */
public class MDLongArray {

  private final long[] mFlatArray;
  private final int[] mDimensions;

  public MDLongArray(final long[] flatArray) {
    this(flatArray, new int[] {flatArray.length});
  }

  public MDLongArray(final long[] flatArray, final int[] dimensions) {
    this.mFlatArray = flatArray;
    this.mDimensions = dimensions.clone();
  }

  /** Creates an empty MDLongArray with the given dimensions (initialized to 0). */
  public static MDLongArray empty(final int[] dimensions) {
    int size = 1;
    for (int d : dimensions) size *= d;
    return new MDLongArray(new long[size], dimensions);
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

  public long get(final int[] indices) {
    return this.mFlatArray[toFlatIndex(indices)];
  }

  public void set(final int[] indices, final long value) {
    this.mFlatArray[toFlatIndex(indices)] = value;
  }

  public void set(final long value, final int index) {
    this.mFlatArray[index] = value;
  }

  public long[] getFlatArrayRef() {
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
    return "MDLongArray{dimensions=" + Arrays.toString(this.mDimensions) + "}";
  }
}
