package com.github.neuroinformatics.samurai_graph.lib.hdf5;

/** Wrapper for HDF5 data type information (compatibility layer over io.jhdf). */
public class HDF5DataTypeInformation {

  private final HDF5DataClass mDataClass;
  private final int mNumberOfElements;

  /**
   * Constructs data type information.
   *
   * @param dataClass the data class
   * @param numberOfElements the number of elements (for arrays)
   */
  public HDF5DataTypeInformation(final HDF5DataClass dataClass, final int numberOfElements) {
    this.mDataClass = dataClass;
    this.mNumberOfElements = numberOfElements;
  }

  /**
   * Returns the data class.
   *
   * @return the data class
   */
  public HDF5DataClass getDataClass() {
    return this.mDataClass;
  }

  /**
   * Returns the number of elements (1 for scalar types).
   *
   * @return the number of elements
   */
  public int getNumberOfElements() {
    return this.mNumberOfElements;
  }
}
