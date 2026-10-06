package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDArray;
import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDDoubleArray;
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
      // The reader must preserve the 2-D shape, not flatten it to a 1-D vector.
      assertEquals(2, matrix.getRank());
      assertArrayEquals(new int[] {2, 2}, matrix.getDimensions());
      assertEquals(1, matrix.get(new int[] {0, 0}));
      assertEquals(2, matrix.get(new int[] {0, 1}));
      assertEquals(3, matrix.get(new int[] {1, 0}));
      assertEquals(4, matrix.get(new int[] {1, 1}));
      HDF5DataSetInformation info = reader.getDataSetInformation("matrix");
      assertEquals(2, info.getRank());
      assertEquals(2, info.getDimensions()[0]);
      assertEquals(2, info.getDimensions()[1]);
    }
  }

  @Test
  void doubleArrayRoundTripPreservesValues() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeDoubleArray("values", new double[] {1.5, 2.5, 3.5});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDDoubleArray values = reader.float64().readMDArray("values");
      assertEquals(3, values.size());
      assertEquals(1, values.getRank());
      assertArrayEquals(new int[] {3}, values.getDimensions());
      assertEquals(1.5, values.get(new int[] {0}), 0.0);
      assertEquals(2.5, values.get(new int[] {1}), 0.0);
      assertEquals(3.5, values.get(new int[] {2}), 0.0);
      HDF5DataSetInformation info = reader.getDataSetInformation("values");
      assertEquals(1, info.getRank());
      assertEquals(3, info.getDimensions()[0]);
      assertEquals(HDF5DataClass.FLOAT, info.getTypeInformation().getDataClass());
    }
  }

  @Test
  void intArrayRoundTripPreservesValues() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("ids", new int[] {10, 20, 30});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDIntArray ids = reader.int32().readMDArray("ids");
      assertEquals(3, ids.size());
      assertEquals(1, ids.getRank());
      assertArrayEquals(new int[] {3}, ids.getDimensions());
      assertEquals(10, ids.get(new int[] {0}));
      assertEquals(20, ids.get(new int[] {1}));
      assertEquals(30, ids.get(new int[] {2}));
      HDF5DataSetInformation info = reader.getDataSetInformation("ids");
      assertEquals(1, info.getRank());
      assertEquals(HDF5DataClass.INTEGER, info.getTypeInformation().getDataClass());
    }
  }

  @Test
  void doubleMatrixRoundTripPreservesShape() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeDoubleMatrix("matrix", new double[][] {{1.0, 2.0}, {3.0, 4.0}});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDDoubleArray matrix = reader.float64().readMDArray("matrix");
      // The reader must preserve the 2-D shape, not flatten it to a 1-D vector.
      assertEquals(2, matrix.getRank());
      assertArrayEquals(new int[] {2, 2}, matrix.getDimensions());
      assertEquals(1.0, matrix.get(new int[] {0, 0}), 0.0);
      assertEquals(2.0, matrix.get(new int[] {0, 1}), 0.0);
      assertEquals(3.0, matrix.get(new int[] {1, 0}), 0.0);
      assertEquals(4.0, matrix.get(new int[] {1, 1}), 0.0);
      HDF5DataSetInformation info = reader.getDataSetInformation("matrix");
      assertEquals(2, info.getRank());
      assertEquals(2, info.getDimensions()[0]);
      assertEquals(2, info.getDimensions()[1]);
    }
  }

  @Test
  void stringArrayRoundTripPreservesValues() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeStringArray("labels", new String[] {"a", "bb", "ccc"});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDArray<String> labels = reader.string().readMDArray("labels");
      assertEquals(3, labels.size());
      assertEquals(1, labels.getRank());
      assertArrayEquals(new int[] {3}, labels.getDimensions());
      assertEquals("a", labels.get(new int[] {0}));
      assertEquals("bb", labels.get(new int[] {1}));
      assertEquals("ccc", labels.get(new int[] {2}));
      HDF5DataSetInformation info = reader.getDataSetInformation("labels");
      assertEquals(1, info.getRank());
      assertEquals(HDF5DataClass.STRING, info.getTypeInformation().getDataClass());
    }
  }

  @Test
  void typedMdArrayRoundTripsAsOneDimensionalData() throws Exception {
    File file = this.createTempHdf5File();
    MDDoubleArray doubles = new MDDoubleArray(new double[] {1.0, 2.0, 3.0});
    MDIntArray ints = new MDIntArray(new int[] {4, 5, 6});
    MDArray<String> strings = new MDArray<>(new String[] {"x", "yy", "zzz"});
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.float64().writeMDArray("doubles", doubles);
      writer.int32().writeMDArray("ints", ints);
      writer.string().writeMDArray("strings", strings);
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDDoubleArray readDoubles = reader.float64().readMDArray("doubles");
      assertEquals(1, readDoubles.getRank());
      assertArrayEquals(new double[] {1.0, 2.0, 3.0}, readDoubles.getFlatArrayRef());
      MDIntArray readInts = reader.int32().readMDArray("ints");
      assertEquals(1, readInts.getRank());
      assertArrayEquals(new int[] {4, 5, 6}, readInts.getFlatArrayRef());
      MDArray<String> readStrings = reader.string().readMDArray("strings");
      assertEquals(1, readStrings.getRank());
      assertArrayEquals(new String[] {"x", "yy", "zzz"}, readStrings.getFlatArrayRef());
    }
  }

  @Test
  void typedMdArrayWritePreservesTwoDimensionalShape() throws Exception {
    File file = this.createTempHdf5File();
    // The typed writeMDArray keeps the dataset shape, so a 2-D array comes back as a
    // 2-D dataset with the same dimensions and value order.
    MDDoubleArray doubles = new MDDoubleArray(new double[] {1.0, 2.0, 3.0, 4.0}, new int[] {2, 2});
    MDIntArray ints = new MDIntArray(new int[] {5, 6, 7, 8}, new int[] {2, 2});
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.float64().writeMDArray("doubles", doubles);
      writer.int32().writeMDArray("ints", ints);
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDDoubleArray readDoubles = reader.float64().readMDArray("doubles");
      assertEquals(2, readDoubles.getRank());
      assertArrayEquals(new int[] {2, 2}, readDoubles.getDimensions());
      assertArrayEquals(new double[] {1.0, 2.0, 3.0, 4.0}, readDoubles.getFlatArrayRef());
      MDIntArray readInts = reader.int32().readMDArray("ints");
      assertEquals(2, readInts.getRank());
      assertArrayEquals(new int[] {2, 2}, readInts.getDimensions());
      assertArrayEquals(new int[] {5, 6, 7, 8}, readInts.getFlatArrayRef());
    }
  }

  @Test
  void typedDoubleMdArrayWritePreservesThreeDimensionalShape() throws Exception {
    File file = this.createTempHdf5File();
    MDDoubleArray cube =
        new MDDoubleArray(
            new double[] {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0}, new int[] {2, 2, 2});
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.float64().writeMDArray("cube", cube);
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDDoubleArray readCube = reader.float64().readMDArray("cube");
      assertEquals(3, readCube.getRank());
      assertArrayEquals(new int[] {2, 2, 2}, readCube.getDimensions());
      assertArrayEquals(
          new double[] {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0}, readCube.getFlatArrayRef());
    }
  }

  @Test
  void archiveRoundTripKeepsTwoDimensionalShapeThroughTypedWrite() throws Exception {
    // Mirrors the archive-save path: a 2-D dataset is read back and written again
    // through the typed writeMDArray, so it must not be downgraded to 1-D.
    File file = this.createTempHdf5File();
    double[][] source = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeDoubleMatrix("data", source);
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDDoubleArray readBack = reader.float64().readMDArray("data");
      File archive = this.createTempHdf5File();
      try (IHDF5Writer writer = HDF5FactoryProvider.get().open(archive)) {
        writer.float64().writeMDArray("data", readBack);
      }
      try (IHDF5Reader reread = HDF5FactoryProvider.get().openForReading(archive)) {
        MDDoubleArray archived = reread.float64().readMDArray("data");
        assertEquals(2, archived.getRank());
        assertArrayEquals(new int[] {2, 3}, archived.getDimensions());
        assertArrayEquals(new double[] {1.0, 2.0, 3.0, 4.0, 5.0, 6.0}, archived.getFlatArrayRef());
      }
      archive.deleteOnExit();
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
