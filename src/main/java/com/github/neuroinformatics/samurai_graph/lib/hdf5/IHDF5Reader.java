package com.github.neuroinformatics.samurai_graph.lib.hdf5;

/** Interface for HDF5 reader operations (compatibility layer over io.jhdf). */
public interface IHDF5Reader extends AutoCloseable {

  /** Returns the float64 datatype reader. */
  IHDF5Float64DatatypeInterface float64();

  /** Returns the int32 datatype reader. */
  IHDF5Int32DatatypeInterface int32();

  /** Returns the int64 datatype reader. */
  IHDF5Int64DatatypeInterface int64();

  /** Returns the float32 datatype reader. */
  IHDF5Float32DatatypeInterface float32();

  /** Returns the boolean datatype reader. */
  IHDF5BoolDatatypeInterface bool();

  /** Returns the enumeration datatype reader. */
  IHDF5EnumerationDatatypeInterface enumeration();

  /** Returns the string datatype reader. */
  IHDF5StringDatatypeInterface string();

  /** Returns the object-level reader. */
  IHDF5ObjectReader object();

  /** Returns the file-level reader. */
  IHDF5FileReader file();

  /**
   * Returns data set information.
   *
   * @param dataSetName the name of the data set
   * @return the data set information
   */
  HDF5DataSetInformation getDataSetInformation(final String dataSetName);

  @Override
  void close();
}
