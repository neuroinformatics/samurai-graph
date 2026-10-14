/*
 * Copyright (C) 2026 Yoshihiro OKUMURA and the Samurai Graph developers.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

package com.github.neuroinformatics.samurai_graph.lib.hdf5;

/** Wrapper for HDF5 data type information (compatibility layer over io.jhdf). */
public class HDF5DataTypeInformation {

  private final HDF5DataClass mDataClass;
  private final int mNumberOfElements;
  private final boolean mIsLongInteger;

  /**
   * Constructs data type information.
   *
   * @param dataClass the data class
   * @param numberOfElements the number of elements (for arrays)
   */
  public HDF5DataTypeInformation(final HDF5DataClass dataClass, final int numberOfElements) {
    this(dataClass, numberOfElements, false);
  }

  /**
   * Constructs data type information.
   *
   * @param dataClass the data class
   * @param numberOfElements the number of elements (for arrays)
   * @param isLongInteger true if the integer data is of the 64-bit (long) type
   */
  public HDF5DataTypeInformation(
      final HDF5DataClass dataClass, final int numberOfElements, final boolean isLongInteger) {
    this.mDataClass = dataClass;
    this.mNumberOfElements = numberOfElements;
    this.mIsLongInteger = isLongInteger;
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

  /**
   * Returns whether the integer data is of the 64-bit (long) type. Only meaningful when the data
   * class is {@link HDF5DataClass#INTEGER}.
   *
   * @return true if the integer data is of the 64-bit (long) type
   */
  public boolean isLongInteger() {
    return this.mIsLongInteger;
  }
}
