package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDArray;
import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDDoubleArray;
import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDIntArray;
import io.jhdf.WritableHdfFile;
import io.jhdf.api.WritableGroup;
import io.jhdf.api.WritableNode;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Adapter that wraps an io.jhdf.WritableHdfFile to provide IHDF5Writer interface. */
class Hdf5WriterAdapter implements IHDF5Writer {

  private static final Logger LOG = LoggerFactory.getLogger(Hdf5WriterAdapter.class);

  private final WritableHdfFile mHdfFile;

  Hdf5WriterAdapter(final WritableHdfFile hdfFile) {
    this.mHdfFile = hdfFile;
  }

  @Override
  public IHDF5Float64Writer float64() {
    return new Float64Writer();
  }

  @Override
  public IHDF5Int32Writer int32() {
    return new Int32Writer();
  }

  @Override
  public IHDF5StringWriter string() {
    return new StringWriter();
  }

  @Override
  public IHDF5Float32Writer float32() {
    return new Float32Writer();
  }

  @Override
  public IHDF5BoolWriter bool() {
    return new BoolWriter();
  }

  @Override
  public IHDF5EnumerationWriter enumeration() {
    return new EnumerationWriter();
  }

  @Override
  public IHDF5ObjectWriter object() {
    return new ObjectWriter();
  }

  @Override
  public void close() {
    try {
      this.mHdfFile.close();
    } catch (Exception e) {
      LOG.warn("Error closing HDF5 writer", e);
    }
  }

  @Override
  public void writeIntArray(final String name, final int[] values) {
    try {
      this.mHdfFile.putDataset(name, values);
    } catch (Exception e) {
      LOG.error("Failed to write int array: {}", name, e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void writeDoubleArray(final String name, final double[] values) {
    try {
      this.mHdfFile.putDataset(name, values);
    } catch (Exception e) {
      LOG.error("Failed to write double array: {}", name, e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void writeIntMatrix(final String name, final int[][] values) {
    try {
      this.mHdfFile.putDataset(name, values);
    } catch (Exception e) {
      LOG.error("Failed to write int matrix: {}", name, e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void writeDoubleMatrix(final String name, final double[][] values) {
    try {
      this.mHdfFile.putDataset(name, values);
    } catch (Exception e) {
      LOG.error("Failed to write double matrix: {}", name, e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void writeStringArray(final String name, final String[] values) {
    try {
      this.mHdfFile.putDataset(name, values);
    } catch (Exception e) {
      LOG.error("Failed to write string array: {}", name, e);
      throw new RuntimeException(e);
    }
  }

  private WritableGroup getOrCreateGroup(final String path) {
    String normalized = path;
    if (normalized.startsWith("/")) {
      normalized = normalized.substring(1);
    }
    if (normalized.isEmpty()) {
      return this.mHdfFile;
    }
    String[] parts = normalized.split("/");
    WritableGroup current = this.mHdfFile;
    for (String part : parts) {
      if (part.isEmpty()) continue;
      WritableNode child = (WritableNode) current.getChild(part);
      if (child == null) {
        current = current.putGroup(part);
      } else if (child instanceof WritableGroup) {
        current = (WritableGroup) child;
      } else {
        throw new HDF5Exception("Path conflict: " + part + " is not a group");
      }
    }
    return current;
  }

  // --- Nested writer implementations ---

  private class Float64Writer implements IHDF5Float64Writer {
    @Override
    public void writeMDArray(final String dataSetName, final MDDoubleArray array) {
      try {
        String[] parts = splitPath(dataSetName);
        WritableGroup parent = Hdf5WriterAdapter.this.getOrCreateGroup(parts[0]);
        parent.putDataset(parts[1], array.getFlatArrayRef());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private class Int32Writer implements IHDF5Int32Writer {
    @Override
    public void writeMDArray(final String dataSetName, final MDIntArray array) {
      try {
        String[] parts = splitPath(dataSetName);
        WritableGroup parent = Hdf5WriterAdapter.this.getOrCreateGroup(parts[0]);
        parent.putDataset(parts[1], array.getFlatArrayRef());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void setAttr(final String path, final String attributeName, final int value) {
      try {
        WritableGroup group = Hdf5WriterAdapter.this.getOrCreateGroup(path);
        group.putAttribute(attributeName, value);
      } catch (Exception e) {
        LOG.error("Failed to set int attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }

    @Override
    public void setArrayAttr(final String path, final String attributeName, final int[] value) {
      try {
        WritableGroup group = Hdf5WriterAdapter.this.getOrCreateGroup(path);
        group.putAttribute(attributeName, value);
      } catch (Exception e) {
        LOG.error("Failed to set int array attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class StringWriter implements IHDF5StringWriter {
    @Override
    public void writeMDArray(final String dataSetName, final MDArray<String> array) {
      try {
        String[] parts = splitPath(dataSetName);
        WritableGroup parent = Hdf5WriterAdapter.this.getOrCreateGroup(parts[0]);
        parent.putDataset(parts[1], array.getFlatArrayRef());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void setAttr(final String path, final String attributeName, final String value) {
      try {
        WritableGroup group = Hdf5WriterAdapter.this.getOrCreateGroup(path);
        group.putAttribute(attributeName, value);
      } catch (Exception e) {
        LOG.error("Failed to set string attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }

    @Override
    public void setArrayAttr(final String path, final String attributeName, final String[] value) {
      try {
        WritableGroup group = Hdf5WriterAdapter.this.getOrCreateGroup(path);
        group.putAttribute(attributeName, value);
      } catch (Exception e) {
        LOG.error("Failed to set string array attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class ObjectWriter implements IHDF5ObjectWriter {
    @Override
    public void createGroup(final String path) {
      try {
        Hdf5WriterAdapter.this.getOrCreateGroup(path);
      } catch (Exception e) {
        LOG.error("Failed to create group: {}", path, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class Float32Writer implements IHDF5Float32Writer {
    @Override
    public void setAttr(final String path, final String attributeName, final float value) {
      try {
        WritableGroup group = Hdf5WriterAdapter.this.getOrCreateGroup(path);
        group.putAttribute(attributeName, value);
      } catch (Exception e) {
        LOG.error("Failed to set float32 attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }

    @Override
    public void setArrayAttr(final String path, final String attributeName, final float[] value) {
      try {
        WritableGroup group = Hdf5WriterAdapter.this.getOrCreateGroup(path);
        group.putAttribute(attributeName, value);
      } catch (Exception e) {
        LOG.error("Failed to set float32 array attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class BoolWriter implements IHDF5BoolWriter {
    @Override
    public void setAttr(final String path, final String attributeName, final boolean value) {
      try {
        WritableGroup group = Hdf5WriterAdapter.this.getOrCreateGroup(path);
        group.putAttribute(attributeName, value);
      } catch (Exception e) {
        LOG.error("Failed to set bool attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class EnumerationWriter implements IHDF5EnumerationWriter {
    @Override
    public void setAttr(
        final String path, final String attributeName, final HDF5EnumerationValue value) {
      try {
        WritableGroup group = Hdf5WriterAdapter.this.getOrCreateGroup(path);
        group.putAttribute(attributeName, (long) value.getValue());
      } catch (Exception e) {
        LOG.error("Failed to set enum attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class FileReader implements IHDF5FileReader {
    @Override
    public File getFile() {
      return Hdf5WriterAdapter.this.mHdfFile.getFile();
    }
  }

  /** Splits a path like "/group/dataset" into ["group", "dataset"]. */
  private static String[] splitPath(final String fullPath) {
    String normalized = fullPath;
    if (normalized.startsWith("/")) {
      normalized = normalized.substring(1);
    }
    int lastSlash = normalized.lastIndexOf('/');
    if (lastSlash < 0) {
      return new String[] {"/", normalized};
    }
    String parent = normalized.substring(0, lastSlash);
    String name = normalized.substring(lastSlash + 1);
    if (parent.isEmpty()) parent = "/";
    return new String[] {"/" + parent, name};
  }
}
