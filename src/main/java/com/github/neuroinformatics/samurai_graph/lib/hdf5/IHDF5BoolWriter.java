package com.github.neuroinformatics.samurai_graph.lib.hdf5;

public interface IHDF5BoolWriter {
  void setAttr(final String path, final String attributeName, final boolean value);
}
