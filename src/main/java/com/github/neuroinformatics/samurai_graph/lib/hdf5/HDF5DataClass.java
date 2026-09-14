/*
 * Copyright (C) 2026 Yoshihiro OKUMURA and the Samurai Graph developers.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;

/** Enum of HDF5 data classes (compatibility layer over io.jhdf). */
public enum HDF5DataClass {
  FLOAT,
  INTEGER,
  STRING,
  BITFIELD,
  COMPOUND,
  TIME,
  OPATYPE,
  REFERENCE,
  ENUM,
  FLOAT16,
  VALUETYPE,
  BOOLEAN
}
