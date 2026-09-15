package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDIntArray;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Round-trip tests for the HDF5 compatibility layer backed by io.jhdf. */
class HDF5RoundTripTest {

  private File createTempHdf5File() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    return path.toFile();
  }

  @Test
  void attributesRoundTripAcrossTypes() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.float32().setAttr("/", "weight", 2.25f);
      writer.bool().setAttr("/", "enabled", true);
      writer.string().setAttr("/", "name", "dataset");
      writer.string().setArrayAttr("/", "tags", new String[] {"a", "b"});
      writer.int32().setAttr("/", "count", 7);
      writer.float32().setArrayAttr("/", "weights", new float[] {1.0f, 2.5f});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertEquals(2.25f, reader.float32().getAttr("/", "weight"), 0.0f);
      assertTrue(reader.bool().getAttr("/", "enabled"));
      assertEquals("dataset", reader.string().getAttr("/", "name"));
      String[] tags = reader.string().getArrayAttr("/", "tags");
      assertEquals("a", tags[0]);
      assertEquals("b", tags[1]);
      assertEquals(7, reader.int32().getAttr("/", "count"));
      float[] weights = reader.float32().getArrayAttr("/", "weights");
      assertEquals(2, weights.length);
    }
  }

  @Test
  void intArrayAttrRoundTrip() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.int32().setArrayAttr("/", "ids", new int[] {3, 5, 7});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      int[] ids = reader.int32().getArrayAttr("/", "ids");
      assertEquals(3, ids.length);
      assertEquals(5, ids[1]);
      assertEquals(7, ids[2]);
    }
  }

  @Test
  void matrixRoundTripThroughPaths() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.object().createGroup("/sub");
      writer.writeIntMatrix("matrix", new int[][] {{1, 2}, {3, 4}});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDIntArray matrix = reader.int32().readMDArray("matrix");
      assertEquals(4, matrix.size());
      HDF5DataSetInformation info = reader.getDataSetInformation("matrix");
      assertEquals(2, info.getRank());
      assertEquals(2, info.getDimensions()[0]);
      assertEquals(2, info.getDimensions()[1]);
    }
  }

  @Test
  void enumerationAttrRoundTrip() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.enumeration().setAttr("/", "kind", new HDF5EnumerationValue(3L));
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      HDF5EnumerationValue value = reader.enumeration().getAttr("/", "kind");
      assertEquals(3L, value.getValue());
    }
  }
}
