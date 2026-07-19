package com.github.neuroinformatics.samurai_graph.lib.hdf5;

public interface IHDF5Int64DatatypeInterface {
  com.github.neuroinformatics.samurai_graph.lib.mdarray.MDLongArray readMDArray(
      final String dataSetName);
}
