package com.github.neuroinformatics.samurai_graph.lib.mdarray;

import java.util.Arrays;

/** An int multidimensional array compatibility layer. */
public class MDIntArray {

  private final int[] mFlatArray;
  private final int[] mDimensions;

  public MDIntArray(final int[] flatArray) {
    this(flatArray, new int[] {flatArray.length});
  }

  public MDIntArray(final int[] flatArray, final int[] dimensions) {
    this.mFlatArray = flatArray;
    this.mDimensions = dimensions.clone();
  }

  /** Creates an empty MDIntArray with the given dimensions (initialized to 0). */
  public static MDIntArray empty(final int[] dimensions) {
    int size = 1;
    for (int d : dimensions) size *= d;
    return new MDIntArray(new int[size], dimensions);
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

  public int get(final int[] indices) {
    return this.mFlatArray[toFlatIndex(indices)];
  }

  public void set(final int[] indices, final int value) {
    this.mFlatArray[toFlatIndex(indices)] = value;
  }

  public void set(final int value, final int index) {
    this.mFlatArray[index] = value;
  }

  public int[] getFlatArrayRef() {
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
    return "MDIntArray{dimensions=" + Arrays.toString(this.mDimensions) + "}";
  }
}
