package com.github.neuroinformatics.samurai_graph.lib.hdf5;

/** Enum of HDF5 data classes (compatibility layer over io.jhdf). */
public enum HDF5DataClass {
  FLOAT,
  INTEGER,
  STRING,
  BITFIELD,
  COMPOUND,
  TIME,
  OPATYPE,
  REFERENCE,
  ENUM,
  FLOAT16,
  VALUETYPE,
  BOOLEAN
}
