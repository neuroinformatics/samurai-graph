package com.github.neuroinformatics.samurai_graph.lib.hdf5;

public interface IHDF5StringDatatypeInterface {
  com.github.neuroinformatics.samurai_graph.lib.mdarray.MDArray<String> readMDArray(
      final String dataSetName);

  String getAttr(final String path, final String attributeName);

  String[] getArrayAttr(final String path, final String attributeName);
}
