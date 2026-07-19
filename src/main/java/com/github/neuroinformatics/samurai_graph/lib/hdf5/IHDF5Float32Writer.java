package com.github.neuroinformatics.samurai_graph.lib.hdf5;

public interface IHDF5Float32Writer {
  void setAttr(final String path, final String attributeName, final float value);

  void setArrayAttr(final String path, final String attributeName, final float[] value);
}
