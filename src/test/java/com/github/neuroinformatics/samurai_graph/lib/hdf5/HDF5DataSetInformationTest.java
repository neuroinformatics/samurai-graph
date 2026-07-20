package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link HDF5DataSetInformation}. */
class HDF5DataSetInformationTest {

  @Test
  void returnsTypeInformation() {
    HDF5DataTypeInformation typeInfo = new HDF5DataTypeInformation(HDF5DataClass.FLOAT, 1);
    HDF5DataSetInformation info = new HDF5DataSetInformation(typeInfo, 1, new long[] {10});
    assertSame(typeInfo, info.getTypeInformation());
  }

  @Test
  void returnsRank() {
    HDF5DataSetInformation info = new HDF5DataSetInformation(null, 3, new long[] {10, 20, 30});
    assertEquals(3, info.getRank());
  }

  @Test
  void returnsClonedDimensions() {
    long[] dims = new long[] {100, 200};
    HDF5DataSetInformation info = new HDF5DataSetInformation(null, 2, dims);
    long[] returned = info.getDimensions();
    assertArrayEquals(dims, returned);
    assertNotNull(returned);
    // Verify it is a clone (different reference)
    returned[0] = 999;
    assertArrayEquals(new long[] {100, 200}, info.getDimensions());
  }

  @Test
  void constructorClonesInputDimensions() {
    long[] dims = new long[] {5, 10};
    HDF5DataSetInformation info = new HDF5DataSetInformation(null, 2, dims);
    dims[0] = 0;
    assertArrayEquals(new long[] {5, 10}, info.getDimensions());
  }

  @Test
  void scalarDataset() {
    HDF5DataTypeInformation typeInfo = new HDF5DataTypeInformation(HDF5DataClass.INTEGER, 1);
    HDF5DataSetInformation info = new HDF5DataSetInformation(typeInfo, 0, new long[0]);
    assertEquals(0, info.getRank());
    assertArrayEquals(new long[0], info.getDimensions());
  }
}
