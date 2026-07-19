package com.github.neuroinformatics.samurai_graph.lib.hdf5;

public interface IHDF5Float64Writer {
  void writeMDArray(
      final String dataSetName,
      final com.github.neuroinformatics.samurai_graph.lib.mdarray.MDDoubleArray array);
}
