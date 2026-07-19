package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import io.jhdf.HdfFile;
import io.jhdf.WritableHdfFile;
import java.io.File;

/** Factory provider for HDF5 readers and writers (compatibility layer over io.jhdf). */
public class HDF5FactoryProvider {

  private HDF5FactoryProvider() {}

  private static final HDF5FactoryProvider INSTANCE = new HDF5FactoryProvider();

  /** Returns the singleton factory provider. */
  public static HDF5FactoryProvider get() {
    return INSTANCE;
  }

  /**
   * Opens an HDF5 file for reading.
   *
   * @param file the HDF5 file
   * @return an HDF5 reader
   */
  public IHDF5Reader openForReading(final File file) {
    try {
      HdfFile hdfFile = new HdfFile(file);
      return new Hdf5ReaderAdapter(hdfFile);
    } catch (Exception e) {
      throw new HDF5Exception("Failed to open HDF5 file: " + file, e);
    }
  }

  /**
   * Opens an HDF5 file for writing (creates a new file).
   *
   * @param file the HDF5 file to create
   * @return an HDF5 writer
   */
  public IHDF5Writer open(final File file) {
    try {
      WritableHdfFile writableFile = HdfFile.write(file.toPath());
      return new Hdf5WriterAdapter(writableFile);
    } catch (Exception e) {
      throw new HDF5Exception("Failed to create HDF5 file: " + file, e);
    }
  }
}
