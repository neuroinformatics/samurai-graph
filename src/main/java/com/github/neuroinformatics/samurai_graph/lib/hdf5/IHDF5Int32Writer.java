package com.github.neuroinformatics.samurai_graph.lib.hdf5;

public interface IHDF5Int32Writer {
  void writeMDArray(
      final String dataSetName,
      final com.github.neuroinformatics.samurai_graph.lib.mdarray.MDIntArray array);

  void setAttr(final String path, final String attributeName, final int value);

  void setArrayAttr(final String path, final String attributeName, final int[] value);
}
