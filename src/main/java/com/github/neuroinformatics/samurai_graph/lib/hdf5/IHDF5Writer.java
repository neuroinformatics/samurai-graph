package com.github.neuroinformatics.samurai_graph.lib.hdf5;

/** Interface for HDF5 writer operations (compatibility layer over io.jhdf). */
public interface IHDF5Writer extends AutoCloseable {

  /** Returns the float64 datatype writer. */
  IHDF5Float64Writer float64();

  /** Returns the int32 datatype writer. */
  IHDF5Int32Writer int32();

  /** Returns the string datatype writer. */
  IHDF5StringWriter string();

  /** Returns the object-level writer. */
  IHDF5ObjectWriter object();

  /** Writes a 1D int array. */
  void writeIntArray(final String name, final int[] values);

  /** Writes a 1D double array. */
  void writeDoubleArray(final String name, final double[] values);

  /** Writes a 2D int matrix. */
  void writeIntMatrix(final String name, final int[][] values);

  /** Writes a 2D double matrix. */
  void writeDoubleMatrix(final String name, final double[][] values);

  /** Writes a string array. */
  void writeStringArray(final String name, final String[] values);

  /** Returns the float32 datatype writer. */
  IHDF5Float32Writer float32();

  /** Returns the boolean datatype writer. */
  IHDF5BoolWriter bool();

  /** Returns the enumeration datatype writer. */
  IHDF5EnumerationWriter enumeration();

  @Override
  void close();
}
