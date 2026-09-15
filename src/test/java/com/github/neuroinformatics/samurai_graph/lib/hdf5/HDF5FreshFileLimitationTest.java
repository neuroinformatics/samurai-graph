package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDIntArray;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * Regression tests for the known limitation of the HDF5 compatibility layer over io.jhdf.
 *
 * <p>The upstream library cannot enumerate the children of the root of a freshly written file (the
 * object header is not flushed for the bottom level groups); the dataset level access succeeds.
 * These tests document the baseline for the future library updates.
 */
class HDF5FreshFileLimitationTest {

  private File createTempHdf5File() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    return path.toFile();
  }

  @Test
  void dataSetLevelAccessWorksForFreshlyWrittenFiles() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("values", new int[] {1, 2, 3});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDIntArray values = reader.int32().readMDArray("values");
      assertEquals(3, values.size());
    }
  }

  @Test
  void groupEnumerationOfFreshlyWrittenFilesIsRejected() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.object().createGroup("/nested");
      writer.writeIntArray("/text", new int[] {1});
      writer.int32().setAttr("/", "id", 7);
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      // documented limitation of the upstream library (0.13.0):
      // enumerating the children of a freshly written file fails
      assertThrows(RuntimeException.class, () -> reader.object().getAllGroupMembers("/"));
    }
  }
}
