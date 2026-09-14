/*
 * Copyright (C) 2026 Yoshihiro OKUMURA and the Samurai Graph developers.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

package com.github.neuroinformatics.samurai_graph.lib.hdf5;

/** Represents an HDF5 enumeration value (compatibility layer over io.jhdf). */
public class HDF5EnumerationValue {
  private final long mValue;

  public HDF5EnumerationValue(final long value) {
    this.mValue = value;
  }

  public long getValue() {
    return this.mValue;
  }
}
