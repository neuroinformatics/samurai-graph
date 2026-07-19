package com.github.neuroinformatics.samurai_graph.lib.hdf5;

public interface IHDF5Float64DatatypeInterface {
  com.github.neuroinformatics.samurai_graph.lib.mdarray.MDDoubleArray readMDArray(
      final String dataSetName);
}
