/*
 * Copyright (C) 2026 Yoshihiro OKUMURA and the Samurai Graph developers.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

package com.github.neuroinformatics.samurai_graph.lib.hdf5;

public interface IHDF5StringWriter {
  void writeMDArray(
      final String dataSetName,
      final com.github.neuroinformatics.samurai_graph.lib.mdarray.MDArray<String> array);

  void setAttr(final String path, final String attributeName, final String value);

  void setArrayAttr(final String path, final String attributeName, final String[] value);
}
