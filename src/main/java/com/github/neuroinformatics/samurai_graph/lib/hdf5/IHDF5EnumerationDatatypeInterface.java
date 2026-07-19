package com.github.neuroinformatics.samurai_graph.lib.hdf5;

public interface IHDF5EnumerationDatatypeInterface {
  HDF5EnumerationValue getAttr(final String path, final String attributeName);
}
