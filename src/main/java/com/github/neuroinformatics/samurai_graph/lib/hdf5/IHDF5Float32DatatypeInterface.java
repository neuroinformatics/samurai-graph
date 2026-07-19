package com.github.neuroinformatics.samurai_graph.lib.hdf5;

public interface IHDF5Float32DatatypeInterface {
  float getAttr(final String path, final String attributeName);

  float[] getArrayAttr(final String path, final String attributeName);
}
