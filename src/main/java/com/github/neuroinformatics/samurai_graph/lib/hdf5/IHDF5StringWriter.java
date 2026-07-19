package com.github.neuroinformatics.samurai_graph.lib.hdf5;

public interface IHDF5StringWriter {
  void writeMDArray(
      final String dataSetName,
      final com.github.neuroinformatics.samurai_graph.lib.mdarray.MDArray<String> array);

  void setAttr(final String path, final String attributeName, final String value);

  void setArrayAttr(final String path, final String attributeName, final String[] value);
}
