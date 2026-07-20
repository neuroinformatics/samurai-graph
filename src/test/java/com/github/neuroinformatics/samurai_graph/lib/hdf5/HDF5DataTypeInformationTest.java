package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link HDF5DataTypeInformation}. */
class HDF5DataTypeInformationTest {

  @Test
  void returnsDataClass() {
    HDF5DataTypeInformation info = new HDF5DataTypeInformation(HDF5DataClass.FLOAT, 1);
    assertEquals(HDF5DataClass.FLOAT, info.getDataClass());
  }

  @Test
  void returnsNumberOfElements() {
    HDF5DataTypeInformation info = new HDF5DataTypeInformation(HDF5DataClass.INTEGER, 4);
    assertEquals(4, info.getNumberOfElements());
  }

  @Test
  void scalarTypeWithOneElement() {
    HDF5DataTypeInformation info = new HDF5DataTypeInformation(HDF5DataClass.STRING, 1);
    assertEquals(HDF5DataClass.STRING, info.getDataClass());
    assertEquals(1, info.getNumberOfElements());
  }
}
