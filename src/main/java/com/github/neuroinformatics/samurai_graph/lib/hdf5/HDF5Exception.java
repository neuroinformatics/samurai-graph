/*
 * Copyright (C) 2026 Yoshihiro OKUMURA and the Samurai Graph developers.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

package com.github.neuroinformatics.samurai_graph.lib.hdf5;

/** Exception thrown for HDF5 operations (compatibility layer over io.jhdf). */
public class HDF5Exception extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public HDF5Exception(final String message) {
    super(message);
  }

  public HDF5Exception(final String message, final Throwable cause) {
    super(message, cause);
  }

  public HDF5Exception(final Throwable cause) {
    super(cause);
  }
}
