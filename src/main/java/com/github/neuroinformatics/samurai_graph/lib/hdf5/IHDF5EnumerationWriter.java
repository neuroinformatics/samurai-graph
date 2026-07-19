package com.github.neuroinformatics.samurai_graph.lib.hdf5;

public interface IHDF5EnumerationWriter {
  void setAttr(final String path, final String attributeName, final HDF5EnumerationValue value);
}
