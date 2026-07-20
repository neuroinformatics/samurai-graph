package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDArray;
import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDDoubleArray;
import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDIntArray;
import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDLongArray;
import io.jhdf.HdfFile;
import io.jhdf.api.Attribute;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import io.jhdf.api.NodeType;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Adapter that wraps an io.jhdf.HdfFile to provide IHDF5Reader interface. */
class Hdf5ReaderAdapter implements IHDF5Reader {

  private static final Logger logger = LoggerFactory.getLogger(Hdf5ReaderAdapter.class);

  private final HdfFile mHdfFile;

  Hdf5ReaderAdapter(final HdfFile hdfFile) {
    this.mHdfFile = hdfFile;
  }

  @Override
  public IHDF5Float64DatatypeInterface float64() {
    return new Float64Reader();
  }

  @Override
  public IHDF5Int32DatatypeInterface int32() {
    return new Int32Reader();
  }

  @Override
  public IHDF5Int64DatatypeInterface int64() {
    return new Int64Reader();
  }

  @Override
  public IHDF5Float32DatatypeInterface float32() {
    return new Float32Reader();
  }

  @Override
  public IHDF5BoolDatatypeInterface bool() {
    return new BoolReader();
  }

  @Override
  public IHDF5EnumerationDatatypeInterface enumeration() {
    return new EnumerationReader();
  }

  @Override
  public IHDF5StringDatatypeInterface string() {
    return new StringReader();
  }

  @Override
  public IHDF5ObjectReader object() {
    return new ObjectReader();
  }

  @Override
  public IHDF5FileReader file() {
    return new FileReader();
  }

  @Override
  public HDF5DataSetInformation getDataSetInformation(final String dataSetName) {
    try {
      Dataset dataset = getDataset(dataSetName);
      long[] dims = dataset.getDimensionsAsLong();
      HDF5DataClass dataClass = mapDataClass(dataset.getDataType().getDataClass());
      return new HDF5DataSetInformation(
          new HDF5DataTypeInformation(dataClass, 1), dataset.getDimensions().length, dims);
    } catch (Exception e) {
      logger.error("Failed to get dataset information for: {}", dataSetName, e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
    try {
      this.mHdfFile.close();
    } catch (Exception e) {
      logger.warn("Error closing HDF5 file", e);
    }
  }

  private Node getNodeByPath(final String path) {
    String normalizedPath = path;
    if (normalizedPath.startsWith("/")) {
      normalizedPath = normalizedPath.substring(1);
    }
    if (normalizedPath.isEmpty()) {
      return this.mHdfFile;
    }
    return this.mHdfFile.getByPath(normalizedPath);
  }

  private Dataset getDataset(final String path) {
    String normalizedPath = path;
    if (normalizedPath.startsWith("/")) {
      normalizedPath = normalizedPath.substring(1);
    }
    if (normalizedPath.isEmpty()) {
      throw new HDF5Exception("Cannot get dataset at root path");
    }
    Node node = getNodeByPath(path);
    if (node instanceof Dataset) {
      return (Dataset) node;
    }
    // Try getDatasetByPath as fallback
    if (normalizedPath.startsWith("/")) {
      normalizedPath = normalizedPath.substring(1);
    }
    return this.mHdfFile.getDatasetByPath(normalizedPath);
  }

  private Group getGroup(final String path) {
    Node node = getNodeByPath(path);
    if (node instanceof Group) {
      return (Group) node;
    }
    throw new HDF5Exception("Path is not a group: " + path);
  }

  private static HDF5DataClass mapDataClass(final int dataClass) {
    // HDF5 data class constants from HDF5 spec
    switch (dataClass) {
      case 1:
        return HDF5DataClass.FLOAT; // H5T_FLOAT
      case 2:
        return HDF5DataClass.INTEGER; // H5T_INTEGER
      case 3:
        return HDF5DataClass.STRING; // H5T_STRING
      case 4:
        return HDF5DataClass.BITFIELD; // H5T_BITFIELD
      case 5:
        return HDF5DataClass.OPATYPE; // H5T_OPAQUE
      case 6:
        return HDF5DataClass.COMPOUND; // H5T_COMPOUND
      case 7:
        return HDF5DataClass.REFERENCE; // H5T_REFERENCE
      case 8:
        return HDF5DataClass.ENUM; // H5T_ENUM
      case 9:
        return HDF5DataClass.VALUETYPE; // H5T_VLEN (not in our enum, use INTEGER)
      case 10:
        return HDF5DataClass.TIME; // H5T_TIME
      default:
        return HDF5DataClass.INTEGER;
    }
  }

  // --- Nested reader implementations ---

  private class Float64Reader implements IHDF5Float64DatatypeInterface {
    @Override
    public MDDoubleArray readMDArray(final String dataSetName) {
      try {
        Dataset dataset = Hdf5ReaderAdapter.this.getDataset(dataSetName);
        Object data = dataset.getData();
        return convertToMDDoubleArray(data);
      } catch (Exception e) {
        logger.error("Failed to read float64 data: {}", dataSetName, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class Int32Reader implements IHDF5Int32DatatypeInterface {
    @Override
    public MDIntArray readMDArray(final String dataSetName) {
      try {
        Dataset dataset = Hdf5ReaderAdapter.this.getDataset(dataSetName);
        Object data = dataset.getData();
        return convertToMDIntArray(data);
      } catch (Exception e) {
        logger.error("Failed to read int32 data: {}", dataSetName, e);
        throw new RuntimeException(e);
      }
    }

    @Override
    public int getAttr(final String path, final String attributeName) {
      try {
        Node node = Hdf5ReaderAdapter.this.getNodeByPath(path);
        Attribute attr = node.getAttribute(attributeName);
        Object value = attr.getData();
        return toInt(value);
      } catch (Exception e) {
        logger.error("Failed to read int attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }

    @Override
    public int[] getArrayAttr(final String path, final String attributeName) {
      try {
        Node node = Hdf5ReaderAdapter.this.getNodeByPath(path);
        Attribute attr = node.getAttribute(attributeName);
        Object value = attr.getData();
        return toIntArray(value);
      } catch (Exception e) {
        logger.error("Failed to read int array attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class Int64Reader implements IHDF5Int64DatatypeInterface {
    @Override
    public MDLongArray readMDArray(final String dataSetName) {
      try {
        Dataset dataset = Hdf5ReaderAdapter.this.getDataset(dataSetName);
        Object data = dataset.getData();
        return convertToMDLongArray(data);
      } catch (Exception e) {
        logger.error("Failed to read int64 data: {}", dataSetName, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class Float32Reader implements IHDF5Float32DatatypeInterface {
    @Override
    public float getAttr(final String path, final String attributeName) {
      try {
        Node node = Hdf5ReaderAdapter.this.getNodeByPath(path);
        Attribute attr = node.getAttribute(attributeName);
        Object value = attr.getData();
        return toFloat(value);
      } catch (Exception e) {
        logger.error("Failed to read float32 attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }

    @Override
    public float[] getArrayAttr(final String path, final String attributeName) {
      try {
        Node node = Hdf5ReaderAdapter.this.getNodeByPath(path);
        Attribute attr = node.getAttribute(attributeName);
        Object value = attr.getData();
        return toFloatArray(value);
      } catch (Exception e) {
        logger.error("Failed to read float32 array attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class BoolReader implements IHDF5BoolDatatypeInterface {
    @Override
    public boolean getAttr(final String path, final String attributeName) {
      try {
        Node node = Hdf5ReaderAdapter.this.getNodeByPath(path);
        Attribute attr = node.getAttribute(attributeName);
        Object value = attr.getData();
        return toBoolean(value);
      } catch (Exception e) {
        logger.error("Failed to read bool attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class EnumerationReader implements IHDF5EnumerationDatatypeInterface {
    @Override
    public HDF5EnumerationValue getAttr(final String path, final String attributeName) {
      try {
        Node node = Hdf5ReaderAdapter.this.getNodeByPath(path);
        Attribute attr = node.getAttribute(attributeName);
        Object value = attr.getData();
        return new HDF5EnumerationValue(toLong(value));
      } catch (Exception e) {
        logger.error("Failed to read enum attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class StringReader implements IHDF5StringDatatypeInterface {
    @Override
    public MDArray<String> readMDArray(final String dataSetName) {
      try {
        Dataset dataset = Hdf5ReaderAdapter.this.getDataset(dataSetName);
        Object data = dataset.getData();
        return convertToMDStringArray(data);
      } catch (Exception e) {
        logger.error("Failed to read string data: {}", dataSetName, e);
        throw new RuntimeException(e);
      }
    }

    @Override
    public String getAttr(final String path, final String attributeName) {
      try {
        Node node = Hdf5ReaderAdapter.this.getNodeByPath(path);
        Attribute attr = node.getAttribute(attributeName);
        Object value = attr.getData();
        return toStringValue(value);
      } catch (Exception e) {
        logger.error("Failed to read string attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }

    @Override
    public String[] getArrayAttr(final String path, final String attributeName) {
      try {
        Node node = Hdf5ReaderAdapter.this.getNodeByPath(path);
        Attribute attr = node.getAttribute(attributeName);
        Object value = attr.getData();
        return toStringArray(value);
      } catch (Exception e) {
        logger.error("Failed to read string array attr: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class ObjectReader implements IHDF5ObjectReader {
    @Override
    public List<String> getAllGroupMembers(final String groupName) {
      try {
        Group group = Hdf5ReaderAdapter.this.getGroup(groupName);
        return new ArrayList<>(group.getChildren().keySet());
      } catch (Exception e) {
        logger.error("Failed to list group members: {}", groupName, e);
        throw new RuntimeException(e);
      }
    }

    @Override
    public boolean isDataSet(final String path) {
      try {
        Node node = Hdf5ReaderAdapter.this.getNodeByPath(path);
        return node.getType() == NodeType.DATASET;
      } catch (Exception e) {
        return false;
      }
    }

    @Override
    public boolean isGroup(final String path) {
      try {
        Node node = Hdf5ReaderAdapter.this.getNodeByPath(path);
        return node.isGroup();
      } catch (Exception e) {
        return false;
      }
    }

    @Override
    public List<String> getGroupMemberPaths(final String path) {
      try {
        Group group = Hdf5ReaderAdapter.this.getGroup(path);
        List<String> members = new ArrayList<>();
        String prefix = path;
        if (!prefix.endsWith("/")) prefix = prefix + "/";
        for (String name : group.getChildren().keySet()) {
          members.add(prefix + name);
        }
        return members;
      } catch (Exception e) {
        logger.error("Failed to get group member paths: {}", path, e);
        throw new RuntimeException(e);
      }
    }

    @Override
    public List<String> getAttributeNames(final String path) {
      try {
        Node node = Hdf5ReaderAdapter.this.getNodeByPath(path);
        return new ArrayList<>(node.getAttributes().keySet());
      } catch (Exception e) {
        logger.error("Failed to get attribute names: {}", path, e);
        throw new RuntimeException(e);
      }
    }

    @Override
    public List<String> getAllAttributeNames(final String path) {
      return getAttributeNames(path);
    }

    @Override
    public boolean hasAttribute(final String path, final String attributeName) {
      try {
        Node node = Hdf5ReaderAdapter.this.getNodeByPath(path);
        return node.getAttributes().containsKey(attributeName);
      } catch (Exception e) {
        return false;
      }
    }

    @Override
    public HDF5DataTypeInformation getAttributeInformation(
        final String path, final String attributeName) {
      try {
        Node node = Hdf5ReaderAdapter.this.getNodeByPath(path);
        Attribute attr = node.getAttribute(attributeName);
        int numElements = (int) attr.getSize();
        return new HDF5DataTypeInformation(
            mapDataClass(attr.getDataType().getDataClass()), (int) numElements);
      } catch (Exception e) {
        logger.error("Failed to get attribute info: {}@{}", attributeName, path, e);
        throw new RuntimeException(e);
      }
    }
  }

  private class FileReader implements IHDF5FileReader {
    @Override
    public File getFile() {
      return Hdf5ReaderAdapter.this.mHdfFile.getFile();
    }
  }

  // --- Helper conversion methods ---

  private static MDDoubleArray convertToMDDoubleArray(final Object data) {
    if (data instanceof double[]) {
      return new MDDoubleArray((double[]) data);
    } else if (data instanceof double[][]) {
      return new MDDoubleArray(flattenDouble2D((double[][]) data));
    } else if (data instanceof double[][][]) {
      return new MDDoubleArray(flattenDouble3D((double[][][]) data));
    } else if (data instanceof Number) {
      return new MDDoubleArray(new double[] {((Number) data).doubleValue()});
    } else if (data instanceof float[]) {
      float[] arr = (float[]) data;
      double[] flat = new double[arr.length];
      for (int i = 0; i < arr.length; i++) flat[i] = arr[i];
      return new MDDoubleArray(flat);
    } else if (data instanceof int[]) {
      int[] arr = (int[]) data;
      double[] flat = new double[arr.length];
      for (int i = 0; i < arr.length; i++) flat[i] = arr[i];
      return new MDDoubleArray(flat);
    } else if (data instanceof long[]) {
      long[] arr = (long[]) data;
      double[] flat = new double[arr.length];
      for (int i = 0; i < arr.length; i++) flat[i] = arr[i];
      return new MDDoubleArray(flat);
    } else {
      throw new RuntimeException("Cannot convert " + data.getClass() + " to MDDoubleArray");
    }
  }

  private static MDIntArray convertToMDIntArray(final Object data) {
    if (data instanceof int[]) {
      return new MDIntArray((int[]) data);
    } else if (data instanceof int[][]) {
      return new MDIntArray(flattenInt2D((int[][]) data));
    } else {
      throw new RuntimeException("Cannot convert " + data.getClass() + " to MDIntArray");
    }
  }

  private static MDLongArray convertToMDLongArray(final Object data) {
    if (data instanceof long[]) {
      return new MDLongArray((long[]) data);
    } else if (data instanceof long[][]) {
      return new MDLongArray(flattenLong2D((long[][]) data));
    } else {
      throw new RuntimeException("Cannot convert " + data.getClass() + " to MDLongArray");
    }
  }

  @SuppressWarnings("unchecked")
  private static MDArray<String> convertToMDStringArray(final Object data) {
    if (data instanceof String[]) {
      return new MDArray<>((String[]) data);
    } else if (data instanceof byte[][]) {
      byte[][] arr = (byte[][]) data;
      String[] flat = new String[arr.length];
      for (int i = 0; i < arr.length; i++) {
        flat[i] = new String(arr[i], StandardCharsets.UTF_8).trim();
      }
      return new MDArray<>(flat);
    } else if (data instanceof String) {
      return new MDArray<>(new String[] {(String) data});
    } else {
      throw new RuntimeException("Cannot convert " + data.getClass() + " to MDArray<String>");
    }
  }

  private static int toInt(final Object value) {
    if (value instanceof Number) {
      return ((Number) value).intValue();
    } else if (value instanceof String) {
      return Integer.parseInt((String) value);
    } else {
      return Integer.parseInt(String.valueOf(value));
    }
  }

  private static float toFloat(final Object value) {
    if (value instanceof Number) {
      return ((Number) value).floatValue();
    } else if (value instanceof String) {
      return Float.parseFloat((String) value);
    } else {
      return Float.parseFloat(String.valueOf(value));
    }
  }

  @SuppressWarnings("unchecked")
  private static float[] toFloatArray(final Object value) {
    if (value instanceof float[]) {
      return (float[]) value;
    } else if (value instanceof Number[]) {
      Number[] arr = (Number[]) value;
      float[] result = new float[arr.length];
      for (int i = 0; i < arr.length; i++) {
        result[i] = arr[i].floatValue();
      }
      return result;
    } else if (value instanceof double[]) {
      double[] arr = (double[]) value;
      float[] result = new float[arr.length];
      for (int i = 0; i < arr.length; i++) {
        result[i] = (float) arr[i];
      }
      return result;
    } else {
      return new float[] {toFloat(value)};
    }
  }

  private static boolean toBoolean(final Object value) {
    if (value instanceof Boolean) {
      return (boolean) value;
    } else if (value instanceof Number) {
      return ((Number) value).intValue() != 0;
    } else if (value instanceof String) {
      return Boolean.parseBoolean((String) value);
    } else {
      return Boolean.parseBoolean(String.valueOf(value));
    }
  }

  private static long toLong(final Object value) {
    if (value instanceof Number) {
      return ((Number) value).longValue();
    } else if (value instanceof String) {
      return Long.parseLong((String) value);
    } else {
      return Long.parseLong(String.valueOf(value));
    }
  }

  @SuppressWarnings("unchecked")
  private static int[] toIntArray(final Object value) {
    if (value instanceof int[]) {
      return (int[]) value;
    } else if (value instanceof Number[]) {
      Number[] arr = (Number[]) value;
      int[] result = new int[arr.length];
      for (int i = 0; i < arr.length; i++) {
        result[i] = arr[i].intValue();
      }
      return result;
    } else if (value instanceof long[]) {
      long[] arr = (long[]) value;
      int[] result = new int[arr.length];
      for (int i = 0; i < arr.length; i++) {
        result[i] = (int) arr[i];
      }
      return result;
    } else {
      return new int[] {toInt(value)};
    }
  }

  @SuppressWarnings("unchecked")
  private static String toStringValue(final Object value) {
    if (value instanceof String) {
      return (String) value;
    } else if (value instanceof byte[]) {
      return new String((byte[]) value, StandardCharsets.UTF_8).trim();
    } else {
      return String.valueOf(value);
    }
  }

  @SuppressWarnings("unchecked")
  private static String[] toStringArray(final Object value) {
    if (value instanceof String[]) {
      return (String[]) value;
    } else if (value instanceof String) {
      return new String[] {(String) value};
    } else if (value instanceof byte[][]) {
      byte[][] arr = (byte[][]) value;
      String[] result = new String[arr.length];
      for (int i = 0; i < arr.length; i++) {
        result[i] = new String(arr[i], StandardCharsets.UTF_8).trim();
      }
      return result;
    } else {
      return new String[] {String.valueOf(value)};
    }
  }

  private static double[] flattenDouble2D(final double[][] arr) {
    int total = 0;
    for (double[] row : arr) total += row.length;
    double[] flat = new double[total];
    int idx = 0;
    for (double[] row : arr) {
      System.arraycopy(row, 0, flat, idx, row.length);
      idx += row.length;
    }
    return flat;
  }

  private static double[] flattenDouble3D(final double[][][] arr) {
    int total = 0;
    for (double[][] plane : arr) {
      for (double[] row : plane) total += row.length;
    }
    double[] flat = new double[total];
    int idx = 0;
    for (double[][] plane : arr) {
      for (double[] row : plane) {
        System.arraycopy(row, 0, flat, idx, row.length);
        idx += row.length;
      }
    }
    return flat;
  }

  private static int[] flattenInt2D(final int[][] arr) {
    int total = 0;
    for (int[] row : arr) total += row.length;
    int[] flat = new int[total];
    int idx = 0;
    for (int[] row : arr) {
      System.arraycopy(row, 0, flat, idx, row.length);
      idx += row.length;
    }
    return flat;
  }

  private static long[] flattenLong2D(final long[][] arr) {
    int total = 0;
    for (long[] row : arr) total += row.length;
    long[] flat = new long[total];
    int idx = 0;
    for (long[] row : arr) {
      System.arraycopy(row, 0, flat, idx, row.length);
      idx += row.length;
    }
    return flat;
  }
}
