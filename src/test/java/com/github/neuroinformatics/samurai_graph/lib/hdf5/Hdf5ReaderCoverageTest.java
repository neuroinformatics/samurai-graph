package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDArray;
import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDDoubleArray;
import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDIntArray;
import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDLongArray;
import io.jhdf.HdfFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for the HDF5 reader int64 path, the object-level group/attribute inspection methods, and
 * the attribute value coercion branches.
 *
 * <p>The convenience writers only support int32, so the int64 (long) datasets are created through
 * the raw {@link HdfFile#write} writer, which accepts a {@code long[]}/{@code long[][]} value.
 */
class Hdf5ReaderCoverageTest {

  private File createTempHdf5File() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    return path.toFile();
  }

  // --- int64 read path ---

  @Test
  void int64ReadsOneDimensionalLongDataset() throws Exception {
    File file = this.createTempHdf5File();
    try (io.jhdf.WritableHdfFile w = HdfFile.write(file.toPath())) {
      w.putDataset("longs", new long[] {1L, 2L, Long.MAX_VALUE});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDLongArray longs = reader.int64().readMDArray("longs");
      assertEquals(1, longs.getRank());
      assertArrayEquals(new int[] {3}, longs.getDimensions());
      assertArrayEquals(new long[] {1L, 2L, Long.MAX_VALUE}, longs.getFlatArrayRef());
    }
  }

  @Test
  void int64ReadsTwoDimensionalLongDatasetPreservingShape() throws Exception {
    File file = this.createTempHdf5File();
    try (io.jhdf.WritableHdfFile w = HdfFile.write(file.toPath())) {
      w.putDataset("matrix", new long[][] {{1L, 2L, 3L}, {4L, 5L, 6L}});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDLongArray matrix = reader.int64().readMDArray("matrix");
      // The reader must preserve the 2-D shape (and its flatten helper) instead of a flat vector.
      assertEquals(2, matrix.getRank());
      assertArrayEquals(new int[] {2, 3}, matrix.getDimensions());
      assertArrayEquals(new long[] {1L, 2L, 3L, 4L, 5L, 6L}, matrix.getFlatArrayRef());
      assertEquals(4L, matrix.get(new int[] {1, 0}));
    }
  }

  @Test
  void int32ReadsTwoDimensionalIntDatasetPreservingShape() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntMatrix("matrix", new int[][] {{1, 2, 3}, {4, 5, 6}});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDIntArray matrix = reader.int32().readMDArray("matrix");
      // The reader must preserve the 2-D shape (and its flatten helper) instead of a flat vector.
      assertEquals(2, matrix.getRank());
      assertArrayEquals(new int[] {2, 3}, matrix.getDimensions());
      assertArrayEquals(new int[] {1, 2, 3, 4, 5, 6}, matrix.getFlatArrayRef());
      assertEquals(4, matrix.get(new int[] {1, 0}));
    }
  }

  @Test
  void int64ReadRejectsNonLongDatasets() throws Exception {
    File file = this.createTempHdf5File();
    try (io.jhdf.WritableHdfFile w = HdfFile.write(file.toPath())) {
      w.putDataset("bools", new boolean[] {true, false});
      w.putDataset("doubles", new double[][] {{1.0}});
      w.putDataset("strings", new String[] {"a"});
      w.putDataset("ints", new int[] {1});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertThrows(RuntimeException.class, () -> reader.int64().readMDArray("bools"));
      assertThrows(RuntimeException.class, () -> reader.int64().readMDArray("doubles"));
      assertThrows(RuntimeException.class, () -> reader.int64().readMDArray("strings"));
      assertThrows(RuntimeException.class, () -> reader.int64().readMDArray("ints"));
    }
  }

  // --- float64 numeric coercion branches ---

  @Test
  void float64ReadsScalarNumericDataset() throws Exception {
    File file = this.createTempHdf5File();
    try (io.jhdf.WritableHdfFile w = HdfFile.write(file.toPath())) {
      w.putDataset("scalar", 3.5d);
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDDoubleArray scalar = reader.float64().readMDArray("scalar");
      assertEquals(1, scalar.getRank());
      assertEquals(3.5d, scalar.get(new int[] {0}), 0.0d);
    }
  }

  @Test
  void float64ReadsIntegerFloatAndLongDatasets() throws Exception {
    File file = this.createTempHdf5File();
    try (io.jhdf.WritableHdfFile w = HdfFile.write(file.toPath())) {
      w.putDataset("ints", new int[] {1, 2, 3});
      w.putDataset("floats", new float[] {1.5f, 2.5f});
      w.putDataset("longs", new long[] {1L, 2L});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertArrayEquals(
          new double[] {1, 2, 3}, reader.float64().readMDArray("ints").getFlatArrayRef());
      assertArrayEquals(
          new double[] {1.5d, 2.5d}, reader.float64().readMDArray("floats").getFlatArrayRef());
      assertArrayEquals(
          new double[] {1, 2}, reader.float64().readMDArray("longs").getFlatArrayRef());
    }
  }

  @Test
  void float64ReadRejectsNonNumericDatasets() throws Exception {
    File file = this.createTempHdf5File();
    try (io.jhdf.WritableHdfFile w = HdfFile.write(file.toPath())) {
      w.putDataset("bools", new boolean[] {true});
      w.putDataset("strings", new String[] {"a"});
      w.putDataset("intMatrix", new int[][] {{1}});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertThrows(RuntimeException.class, () -> reader.float64().readMDArray("bools"));
      assertThrows(RuntimeException.class, () -> reader.float64().readMDArray("strings"));
      assertThrows(RuntimeException.class, () -> reader.float64().readMDArray("intMatrix"));
    }
  }

  @Test
  void readMDArrayRejectsIncompatibleDatasetsPerType() throws Exception {
    File file = this.createTempHdf5File();
    try (io.jhdf.WritableHdfFile w = HdfFile.write(file.toPath())) {
      w.putDataset("doubles", new double[] {1.0});
      w.putDataset("ints", new int[] {1});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      // A double dataset cannot be coerced into an int MDArray.
      assertThrows(RuntimeException.class, () -> reader.int32().readMDArray("doubles"));
      // A numeric dataset cannot be coerced into a string MDArray.
      assertThrows(RuntimeException.class, () -> reader.string().readMDArray("ints"));
    }
  }

  // --- attribute value coercion branches ---

  @Test
  void int32AndFloat32AttributesCoerceNumericAndStringValues() throws Exception {
    File file = this.createTempHdf5File();
    try (io.jhdf.WritableHdfFile w = HdfFile.write(file.toPath())) {
      w.putDataset("vals", new double[] {1.0});
      w.putAttribute("intAttr", 5);
      w.putAttribute("longAttr", 42L);
      w.putAttribute("stringInt", "7");
      w.putAttribute("floatAttr", 1.25f);
      w.putAttribute("stringFloat", "7.5");
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertEquals(5, reader.int32().getAttr("/", "intAttr"));
      assertEquals(42, reader.int32().getAttr("/", "longAttr"));
      assertEquals(7, reader.int32().getAttr("/", "stringInt"));
      assertEquals(1.25f, reader.float32().getAttr("/", "floatAttr"), 0.0f);
      assertEquals(7.5f, reader.float32().getAttr("/", "stringFloat"), 0.0f);
    }
  }

  @Test
  void attributeArrayCoercionHandlesLongAndDoubleAndScalarSources() throws Exception {
    File file = this.createTempHdf5File();
    try (io.jhdf.WritableHdfFile w = HdfFile.write(file.toPath())) {
      w.putDataset("vals", new double[] {1.0});
      w.putAttribute("longArr", new long[] {1L, 2L});
      w.putAttribute("doubleArr", new double[] {0.5d, 1.5d});
      w.putAttribute("floatArr", new float[] {1.5f, 2.5f});
      w.putAttribute("scalar", 9);
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertArrayEquals(new int[] {1, 2}, reader.int32().getArrayAttr("/", "longArr"));
      assertArrayEquals(
          new float[] {0.5f, 1.5f}, reader.float32().getArrayAttr("/", "doubleArr"), 0.0f);
      // A float[] source is returned as-is.
      assertArrayEquals(
          new float[] {1.5f, 2.5f}, reader.float32().getArrayAttr("/", "floatArr"), 0.0f);
      // A scalar source falls back to a single-element array.
      assertArrayEquals(new int[] {9}, reader.int32().getArrayAttr("/", "scalar"));
      assertArrayEquals(new float[] {9.0f}, reader.float32().getArrayAttr("/", "scalar"), 0.0f);
      // A double[] cannot be coerced to an int[].
      assertThrows(RuntimeException.class, () -> reader.int32().getArrayAttr("/", "doubleArr"));
    }
  }

  @Test
  void booleanAndEnumerationAttributesCoerceFromNumberAndString() throws Exception {
    File file = this.createTempHdf5File();
    try (io.jhdf.WritableHdfFile w = HdfFile.write(file.toPath())) {
      w.putDataset("vals", new double[] {1.0});
      w.putAttribute("intAttr", 5);
      w.putAttribute("boolAttr", true);
      w.putAttribute("stringBool", "true");
      w.putAttribute("stringEnum", "42");
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertEquals(true, reader.bool().getAttr("/", "intAttr"));
      assertEquals(true, reader.bool().getAttr("/", "boolAttr"));
      assertEquals(true, reader.bool().getAttr("/", "stringBool"));
      assertEquals(42L, reader.enumeration().getAttr("/", "stringEnum").getValue());
    }
  }

  @Test
  void stringAttributesCoerceFromStringArrayAndOtherScalarSources() throws Exception {
    File file = this.createTempHdf5File();
    try (io.jhdf.WritableHdfFile w = HdfFile.write(file.toPath())) {
      w.putDataset("vals", new double[] {1.0});
      w.putAttribute("stringAttr", "hello");
      w.putAttribute("stringArr", new String[] {"a", "b"});
      w.putAttribute("intAttr", 5);
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertEquals("hello", reader.string().getAttr("/", "stringAttr"));
      assertArrayEquals(new String[] {"a", "b"}, reader.string().getArrayAttr("/", "stringArr"));
      // A non-string scalar is rendered with String.valueOf.
      assertEquals("5", reader.string().getAttr("/", "intAttr"));
    }
  }

  @Test
  void scalarStringDatasetReadsAsOneElement() throws Exception {
    File file = this.createTempHdf5File();
    try (io.jhdf.WritableHdfFile w = HdfFile.write(file.toPath())) {
      w.putDataset("single", "one");
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      MDArray<String> single = reader.string().readMDArray("single");
      assertEquals(1, single.size());
      assertEquals("one", single.get(new int[] {0}));
    }
  }

  // --- object-level group/attribute inspection ---

  @Test
  void objectReaderInspectsRootGroupMembersAndType() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("values", new int[] {1, 2, 3});
      writer.writeStringArray("labels", new String[] {"a", "b"});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      // A file whose root holds only flat datasets can have its members enumerated.
      List<String> members = reader.object().getAllGroupMembers("/");
      assertTrue(members.contains("values"));
      assertTrue(members.contains("labels"));
      List<String> paths = reader.object().getGroupMemberPaths("/");
      assertTrue(paths.contains("/values"));
      assertTrue(paths.contains("/labels"));
      assertTrue(reader.object().isDataSet("values"));
      assertTrue(reader.object().isDataSet("/labels"));
      assertTrue(reader.object().isDataSet("labels"));
      assertTrue(reader.object().isGroup("/"));
      assertFalse(reader.object().isGroup("values"));
    }
  }

  @Test
  void objectReaderReportsMissingPathsAsFalse() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("values", new int[] {1});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertFalse(reader.object().isDataSet("missing"));
      assertFalse(reader.object().isGroup("missing"));
      assertFalse(reader.object().hasAttribute("/", "missing"));
      assertFalse(reader.object().hasAttribute("/missing", "attr"));
    }
  }

  @Test
  void objectReaderListsAndChecksAttributes() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("values", new int[] {1});
      writer.int32().setAttr("/", "count", 7);
      writer.int32().setArrayAttr("/", "ids", new int[] {1, 2, 3});
      writer.bool().setAttr("/", "enabled", true);
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      List<String> names = reader.object().getAttributeNames("/");
      assertTrue(names.contains("count"));
      assertTrue(names.contains("ids"));
      assertTrue(reader.object().getAllAttributeNames("/").contains("count"));
      assertTrue(reader.object().hasAttribute("/", "count"));
      assertFalse(reader.object().hasAttribute("/", "missing"));

      HDF5DataTypeInformation countInfo = reader.object().getAttributeInformation("/", "count");
      assertEquals(HDF5DataClass.INTEGER, countInfo.getDataClass());
      assertEquals(1, countInfo.getNumberOfElements());
      HDF5DataTypeInformation idsInfo = reader.object().getAttributeInformation("/", "ids");
      assertEquals(HDF5DataClass.INTEGER, idsInfo.getDataClass());
      assertEquals(3, idsInfo.getNumberOfElements());
      assertEquals(
          HDF5DataClass.BITFIELD,
          reader.object().getAttributeInformation("/", "enabled").getDataClass());
    }
  }

  @Test
  void objectReaderInspectsDatasetNodeAttributes() throws Exception {
    File file = this.createTempHdf5File();
    try (io.jhdf.WritableHdfFile w = HdfFile.write(file.toPath())) {
      io.jhdf.api.WritableDataset vals = w.putDataset("vals", new double[] {1.0, 2.0});
      vals.putAttribute("src", "probe");
      vals.putAttribute("count", 2);
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertTrue(reader.object().getAttributeNames("vals").contains("src"));
      assertTrue(reader.object().hasAttribute("vals", "src"));
      assertFalse(reader.object().hasAttribute("vals", "missing"));
      HDF5DataTypeInformation srcInfo = reader.object().getAttributeInformation("vals", "src");
      assertEquals(HDF5DataClass.STRING, srcInfo.getDataClass());
      assertEquals(1, srcInfo.getNumberOfElements());
    }
  }

  @Test
  void objectReaderReportsGroupNodes() throws Exception {
    File file = this.createTempHdf5File();
    // A file holding only a group keeps the group resolvable; mixing root datasets
    // after createGroup breaks node resolution in the upstream alpha writer.
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.object().createGroup("/nested");
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertTrue(reader.object().isGroup("/nested"));
      assertFalse(reader.object().isDataSet("/nested"));
      assertTrue(reader.object().getAllGroupMembers("/").contains("nested"));
    }
  }

  // --- file-level reader ---

  @Test
  void fileReaderReturnsTheOpenedFile() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("values", new int[] {1});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertEquals(file.getAbsolutePath(), reader.file().getFile().getAbsolutePath());
    }
  }

  // --- error paths ---

  @Test
  void readMDArrayThrowsOnMissingDataset() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("values", new int[] {1});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertThrows(RuntimeException.class, () -> reader.float64().readMDArray("missing"));
      assertThrows(RuntimeException.class, () -> reader.int32().readMDArray("missing"));
      assertThrows(RuntimeException.class, () -> reader.int64().readMDArray("missing"));
      assertThrows(RuntimeException.class, () -> reader.string().readMDArray("missing"));
      assertThrows(RuntimeException.class, () -> reader.getDataSetInformation("missing"));
    }
  }

  @Test
  void readMDArrayThrowsOnEmptyPath() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("values", new int[] {1});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertThrows(RuntimeException.class, () -> reader.float64().readMDArray(""));
      assertThrows(RuntimeException.class, () -> reader.getDataSetInformation(""));
    }
  }

  @Test
  void getAttrThrowsOnMissingAttribute() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("values", new int[] {1});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertThrows(RuntimeException.class, () -> reader.int32().getAttr("/", "missing"));
      assertThrows(RuntimeException.class, () -> reader.int32().getArrayAttr("/", "missing"));
      assertThrows(RuntimeException.class, () -> reader.float32().getAttr("/", "missing"));
      assertThrows(RuntimeException.class, () -> reader.float32().getArrayAttr("/", "missing"));
      assertThrows(RuntimeException.class, () -> reader.string().getAttr("/", "missing"));
      assertThrows(RuntimeException.class, () -> reader.string().getArrayAttr("/", "missing"));
      assertThrows(RuntimeException.class, () -> reader.bool().getAttr("/", "missing"));
      assertThrows(RuntimeException.class, () -> reader.enumeration().getAttr("/", "missing"));
    }
  }

  @Test
  void getAttrThrowsOnMissingNode() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("values", new int[] {1});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertThrows(RuntimeException.class, () -> reader.int32().getAttr("/missing", "attr"));
    }
  }

  @Test
  void objectReaderThrowsOnInvalidGroupAndMissingAttributeInfo() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("values", new int[] {1});
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertThrows(RuntimeException.class, () -> reader.object().getAllGroupMembers("values"));
      assertThrows(RuntimeException.class, () -> reader.object().getGroupMemberPaths("values"));
      assertThrows(RuntimeException.class, () -> reader.object().getAttributeNames("missing"));
      assertThrows(
          RuntimeException.class, () -> reader.object().getAttributeInformation("/", "missing"));
    }
  }

  // --- factory provider open error paths ---

  @Test
  void openForWritingThrowsHdf5ExceptionWhenParentDirectoryIsMissing() {
    assertThrows(
        HDF5Exception.class,
        () -> HDF5FactoryProvider.get().open(new File("/no/such/dir/at/all/out.h5")));
  }

  @Test
  void openForWritingThrowsHdf5ExceptionOnReadOnlyDirectory() throws Exception {
    Path dir = Files.createTempDirectory("samurai-graph-readonly");
    dir.toFile().deleteOnExit();
    Path file = dir.resolve("readonly.h5");
    try {
      Files.setPosixFilePermissions(
          dir, java.nio.file.attribute.PosixFilePermissions.fromString("r-x------"));
      assertThrows(HDF5Exception.class, () -> HDF5FactoryProvider.get().open(file.toFile()));
    } finally {
      Files.setPosixFilePermissions(
          dir, java.nio.file.attribute.PosixFilePermissions.fromString("rwx------"));
      Files.deleteIfExists(file);
      Files.deleteIfExists(dir);
    }
  }

  // --- writer path handling ---

  @Test
  void createGroupReusesExistingGroupAndSkipsEmptyPathSegments() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.object().createGroup("a");
      writer.object().createGroup("a");
      writer.object().createGroup("a//b");
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertTrue(reader.object().isGroup("a"));
      assertTrue(reader.object().isGroup("a/b"));
    }
  }

  @Test
  void writerThrowsWhenGroupPathConflictsWithADataset() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("conflict", new int[] {1});
      assertThrows(RuntimeException.class, () -> writer.object().createGroup("conflict/nested"));
      assertThrows(RuntimeException.class, () -> writer.int32().setAttr("conflict/x", "attr", 1));
      assertThrows(
          RuntimeException.class,
          () -> writer.int32().writeMDArray("conflict/x", new MDIntArray(new int[] {1})));
    }
  }

  @Test
  void typedWritersThrowWhenPathConflictsWithADataset() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("conflict", new int[] {1});
      assertThrows(
          RuntimeException.class,
          () -> writer.float64().writeMDArray("conflict/x", new MDDoubleArray(new double[] {1})));
      assertThrows(
          RuntimeException.class,
          () -> writer.int32().setArrayAttr("conflict/x", "attr", new int[] {1}));
      assertThrows(
          RuntimeException.class,
          () -> writer.string().writeMDArray("conflict/x", new MDArray<>(new String[] {"a"})));
      assertThrows(
          RuntimeException.class, () -> writer.string().setAttr("conflict/x", "attr", "s"));
      assertThrows(
          RuntimeException.class,
          () -> writer.string().setArrayAttr("conflict/x", "attr", new String[] {"s"}));
      assertThrows(
          RuntimeException.class, () -> writer.float32().setAttr("conflict/x", "attr", 1f));
      assertThrows(
          RuntimeException.class,
          () -> writer.float32().setArrayAttr("conflict/x", "attr", new float[] {1f}));
      assertThrows(RuntimeException.class, () -> writer.bool().setAttr("conflict/x", "attr", true));
      assertThrows(
          RuntimeException.class,
          () -> writer.enumeration().setAttr("conflict/x", "attr", new HDF5EnumerationValue(1L)));
    }
  }

  @Test
  void writeMDArrayWithLeadingSlashTargetsTheParentGroup() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.float64().writeMDArray("/grp/vals", new MDDoubleArray(new double[] {1.0, 2.0}));
      writer.int32().writeMDArray("/grp/intvals", new MDIntArray(new int[] {1, 2}));
      writer.string().writeMDArray("/grp/strvals", new MDArray<>(new String[] {"a", "b"}));
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertEquals(2, reader.float64().readMDArray("/grp/vals").size());
      assertEquals(2, reader.int32().readMDArray("/grp/intvals").size());
      assertEquals(2, reader.string().readMDArray("/grp/strvals").size());
      // A group path without a trailing slash is normalized before listing members.
      assertTrue(reader.object().getGroupMemberPaths("grp").contains("grp/vals"));
    }
  }

  @Test
  void setAttrCreatesMissingIntermediateGroups() throws Exception {
    File file = this.createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeIntArray("values", new int[] {1});
      writer.int32().setAttr("a/b", "attr", 1);
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      assertTrue(reader.object().isGroup("a/b"));
    }
  }
}
