package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
  void writeAndReadBackDoubleArray() throws Exception {
    File file = createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeDoubleArray("values", new double[] {1.5, 2.5, 3.5});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDDoubleArray values = reader.float64().readMDArray("values");
      assertEquals(3, values.size());
      assertEquals(1.5, values.get(0), 0.0);
      assertEquals(3.5, values.get(2), 0.0);
      HDF5DataSetInformation info = reader.getDataSetInformation("values");
      assertEquals(HDF5DataClass.FLOAT, info.getTypeInformation().getDataClass());
      assertEquals(1, info.getRank());
      assertEquals(3, info.getDimensions()[0]);
    }
  }

  @Test
  void writeAndReadBackIntArray() throws Exception {
    File file = createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("counts", new int[] {10, 20});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDIntArray counts = reader.int32().readMDArray("counts");
      assertEquals(2, counts.size());
      assertEquals(10, counts.get(new int[] {0}));
      assertEquals(20, counts.get(new int[] {1}));
    }
  }

  @Test
  void writeAndReadBackStringArray() throws Exception {
    File file = createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeStringArray("names", new String[] {"x", "y"});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDArray<String> names = reader.string().readMDArray("names");
      assertEquals(2, names.size());
      assertEquals("x", names.get(new int[] {0}));
    }
  }
}
