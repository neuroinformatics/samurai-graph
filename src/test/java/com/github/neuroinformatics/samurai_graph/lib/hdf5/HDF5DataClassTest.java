package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link HDF5DataClass} enum. */
class HDF5DataClassTest {

  @Test
  void containsExpectedValues() {
    HDF5DataClass[] values = HDF5DataClass.values();
    assertEquals(12, values.length);
  }

  @Test
  void valueOfReturnsCorrectInstance() {
    assertNotNull(HDF5DataClass.valueOf("FLOAT"));
    assertNotNull(HDF5DataClass.valueOf("INTEGER"));
    assertNotNull(HDF5DataClass.valueOf("STRING"));
  }

  @Test
  void valueByOrdinal() {
    assertEquals(HDF5DataClass.FLOAT, HDF5DataClass.values()[0]);
    assertEquals(HDF5DataClass.INTEGER, HDF5DataClass.values()[1]);
    assertEquals(HDF5DataClass.BOOLEAN, HDF5DataClass.values()[11]);
  }
}
