package com.github.neuroinformatics.samurai_graph.lib.hdf5;

/** Represents an HDF5 enumeration value (compatibility layer over io.jhdf). */
public class HDF5EnumerationValue {
  // Placeholder for compatibility; the actual value is stored as a long
  private final long mValue;

  public HDF5EnumerationValue(final long value) {
    this.mValue = value;
  }

  public long getValue() {
    return this.mValue;
  }
}
