package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link HDF5EnumerationValue}. */
class HDF5EnumerationValueTest {

  @Test
  void returnsValue() {
    HDF5EnumerationValue value = new HDF5EnumerationValue(42L);
    assertEquals(42L, value.getValue());
  }

  @Test
  void returnsZeroValue() {
    HDF5EnumerationValue value = new HDF5EnumerationValue(0L);
    assertEquals(0L, value.getValue());
  }

  @Test
  void returnsNegativeValue() {
    HDF5EnumerationValue value = new HDF5EnumerationValue(-100L);
    assertEquals(-100L, value.getValue());
  }

  @Test
  void returnsLongMaxValue() {
    HDF5EnumerationValue value = new HDF5EnumerationValue(Long.MAX_VALUE);
    assertEquals(Long.MAX_VALUE, value.getValue());
  }
}
