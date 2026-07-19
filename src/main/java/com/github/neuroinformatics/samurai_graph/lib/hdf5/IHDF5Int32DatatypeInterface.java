package com.github.neuroinformatics.samurai_graph.lib.hdf5;

public interface IHDF5Int32DatatypeInterface {
  com.github.neuroinformatics.samurai_graph.lib.mdarray.MDIntArray readMDArray(
      final String dataSetName);

  int getAttr(final String path, final String attributeName);

  int[] getArrayAttr(final String path, final String attributeName);
}
