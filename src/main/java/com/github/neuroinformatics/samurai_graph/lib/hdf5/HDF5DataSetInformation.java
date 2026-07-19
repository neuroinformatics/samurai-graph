package com.github.neuroinformatics.samurai_graph.lib.hdf5;

/** Wrapper for HDF5 data set information (compatibility layer over io.jhdf). */
public class HDF5DataSetInformation {

  private final HDF5DataTypeInformation mTypeInformation;
  private final int mRank;
  private final long[] mDimensions;

  /**
   * Constructs data set information.
   *
   * @param typeInformation the type information
   * @param rank the number of dimensions
   * @param dimensions the dimensions
   */
  public HDF5DataSetInformation(
      final HDF5DataTypeInformation typeInformation, final int rank, final long[] dimensions) {
    this.mTypeInformation = typeInformation;
    this.mRank = rank;
    this.mDimensions = dimensions.clone();
  }

  /**
   * Returns the data type information.
   *
   * @return the type information
   */
  public HDF5DataTypeInformation getTypeInformation() {
    return this.mTypeInformation;
  }

  /**
   * Returns the rank (number of dimensions).
   *
   * @return the rank
   */
  public int getRank() {
    return this.mRank;
  }

  /**
   * Returns the dimensions.
   *
   * @return the dimensions
   */
  public long[] getDimensions() {
    return this.mDimensions.clone();
  }
}
