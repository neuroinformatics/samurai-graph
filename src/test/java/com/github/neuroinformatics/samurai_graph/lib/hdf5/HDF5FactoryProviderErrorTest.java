package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Error path tests for the HDF5 factory provider. */
class HDF5FactoryProviderErrorTest {

  @Test
  void missingFileThrowHDF5Exception() {
    assertThrows(
        HDF5Exception.class,
        () -> HDF5FactoryProvider.get().openForReading(new File("/nope/missing.h5")));
  }

  @Test
  void nonHDF5FileIsRejectedAtOpen() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.writeString(path, "plain text");
    try {
      assertThrows(
          HDF5Exception.class, () -> HDF5FactoryProvider.get().openForReading(path.toFile()));
    } finally {
      path.toFile().deleteOnExit();
    }
  }
}
