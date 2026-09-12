package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGSXYMDArrayMultipleData} built from a temporary HDF5 file. */
class SGSXYMDArrayMultipleDataTest {

  private static Path createTempHdf5File() throws IOException {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    return path;
  }

  private static SGHDF5File createHDF5File(Path path) throws IOException {
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(path.toFile())) {
      writer.writeDoubleArray("x", new double[] {1.0, 2.0, 3.0});
      writer.writeDoubleArray("y1", new double[] {10.0, 20.0, 30.0});
      writer.writeDoubleArray("y2", new double[] {40.0, 50.0, 60.0});
      writer.writeDoubleArray("le", new double[] {1.0, 1.0, 1.0});
      writer.writeDoubleArray("ue", new double[] {2.0, 2.0, 2.0});
    }
    IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(path.toFile());
    return new SGHDF5File(reader);
  }

  private static SGMDArrayDataColumnInfo[] infos(SGHDF5File file, String... names) {
    SGMDArrayDataColumnInfo[] infos = new SGMDArrayDataColumnInfo[names.length];
    for (int ii = 0; ii < names.length; ii++) {
      SGMDArrayDataColumnInfo col =
          (SGMDArrayDataColumnInfo)
              SGDataFileUtility.createDataColumnInfo(file.findVariable(names[ii]), "");
      col.setGenericDimensionIndex(0);
      infos[ii] = col;
    }
    return infos;
  }

  private static SGSXYMDArrayMultipleData createData(SGHDF5File file, Integer[][] xy) {
    return new SGSXYMDArrayMultipleData(
        file,
        new SGDataSourceObserver(),
        infos(file, "x"),
        infos(file, "y1", "y2"),
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        true);
  }

  @Test
  void buildsMultipleDataFromHDF5File() throws IOException {
    SGHDF5File file = createHDF5File(createTempHdf5File());
    SGSXYMDArrayMultipleData data = createData(file, null);
    assertEquals(SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA, data.getDataType());
    assertTrue(data.hasMultipleYValues());
    assertEquals(2, data.getChildNumber());
    assertEquals(3, data.getAllPointsNumber());
    assertFalse(data.isErrorBarAvailable());
  }

  @Test
  void valueArraysMatchHDF5File() throws IOException {
    SGHDF5File file = createHDF5File(createTempHdf5File());
    SGSXYMDArrayMultipleData data = createData(file, null);
    double[][] xValues = data.getXValueArray(false);
    double[][] yValues = data.getYValueArray(false);
    assertEquals(2, xValues.length);
    assertEquals(2, yValues.length);
    assertEquals(3, xValues[0].length);
    assertEquals(3, yValues[0].length);
    assertEquals(1.0, xValues[0][0], 0.0);
    assertEquals(2.0, xValues[0][1], 0.0);
    assertEquals(3.0, xValues[0][2], 0.0);
    assertEquals(10.0, yValues[0][0], 0.0);
    assertEquals(20.0, yValues[0][1], 0.0);
    assertEquals(30.0, yValues[0][2], 0.0);
    assertEquals(40.0, yValues[1][0], 0.0);
    assertEquals(60.0, yValues[1][2], 0.0);
  }

  @Test
  void shiftAndExponentRoundTrip() throws IOException {
    SGHDF5File file = createHDF5File(createTempHdf5File());
    SGSXYMDArrayMultipleData data = createData(file, null);
    SGTuple2d shift = data.getShift();
    assertEquals(0.0, shift.x, 0.0);
    assertEquals(0.0, shift.y, 0.0);
    data.setShift(new SGTuple2d(1.5, -0.5));
    SGTuple2d got = data.getShift();
    assertEquals(1.5, got.x, 0.0);
    assertEquals(-0.5, got.y, 0.0);
    assertThrows(IllegalArgumentException.class, () -> data.setShift(null));
    data.setExponent(4);
    assertEquals(4, data.getExponent());
  }

  @Test
  void constructorRejectsEmptyAndBothMultiple() throws IOException {
    SGHDF5File file = createHDF5File(createTempHdf5File());
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGSXYMDArrayMultipleData(
                file,
                new SGDataSourceObserver(),
                new SGMDArrayDataColumnInfo[] {},
                new SGMDArrayDataColumnInfo[] {},
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                true));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGSXYMDArrayMultipleData(
                file,
                new SGDataSourceObserver(),
                infos(file, "x", "y1"),
                infos(file, "y1", "y2"),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                true));
  }

  @Test
  void errorBarAvailableWithHolder() throws IOException {
    SGHDF5File file = createHDF5File(createTempHdf5File());
    SGSXYMDArrayMultipleData data =
        new SGSXYMDArrayMultipleData(
            file,
            new SGDataSourceObserver(),
            infos(file, "x"),
            infos(file, "y1", "y2"),
            infos(file, "le"),
            infos(file, "ue"),
            infos(file, "y1"),
            null,
            null,
            null,
            null,
            true);
    assertTrue(data.isErrorBarAvailable());
    Boolean[] same = data.hasSameErrorVariable();
    assertNotNull(same);
    assertEquals(2, same.length);
    assertFalse(same[0]);
  }

  private static final SGSXYDataBufferPolicy VALUE_POLICY =
      new SGSXYDataBufferPolicy(false, false, false, false, false);

  @Test
  void strideLimitsValueArrays() throws IOException {
    SGHDF5File file = createHDF5File(createTempHdf5File());
    SGSXYMDArrayMultipleData data =
        new SGSXYMDArrayMultipleData(
            file,
            new SGDataSourceObserver(),
            infos(file, "x"),
            infos(file, "y1", "y2"),
            null,
            null,
            null,
            null,
            null,
            new SGIntegerSeriesSet(0, 2, 2),
            null,
            true);
    double[][] x = data.getXValueArray(false);
    assertEquals(2, x.length);
    assertEquals(2, x[0].length);
    assertEquals(1.0, x[0][0], 0.0);
    assertEquals(3.0, x[0][1], 0.0);
  }

  @Test
  void sxyDataAndMultipleArraysHaveChildren() throws IOException {
    SGHDF5File file = createHDF5File(createTempHdf5File());
    SGSXYMDArrayMultipleData data = createData(file, null);
    assertEquals(2, data.getSXYDataArray().length);
    assertEquals(2, data.getSXYTypeMultipleDataArray().length);
  }

  @Test
  void errorBarValueArrays() throws IOException {
    SGHDF5File file = createHDF5File(createTempHdf5File());
    SGSXYMDArrayMultipleData data =
        new SGSXYMDArrayMultipleData(
            file,
            new SGDataSourceObserver(),
            infos(file, "x"),
            infos(file, "y1", "y2"),
            infos(file, "le"),
            infos(file, "ue"),
            infos(file, "y1"),
            null,
            null,
            null,
            null,
            true);
    double[][] le = data.getLowerErrorValueArray(VALUE_POLICY);
    double[][] ue = data.getUpperErrorValueArray(VALUE_POLICY);
    assertEquals(2, le.length);
    assertEquals(2, ue.length);
    assertEquals(3, le[0].length);
    assertEquals(1.0, le[0][0], 0.0);
    assertEquals(2.0, ue[0][0], 0.0);
  }

  @Test
  void setStrideRefetchesValues() throws IOException {
    SGHDF5File file = createHDF5File(createTempHdf5File());
    SGSXYMDArrayMultipleData data = createData(file, null);
    data.setStride(new SGIntegerSeriesSet(0, 2, 2));
    double[][] x = data.getXValueArray(false);
    assertEquals(2, x.length);
    assertEquals(2, x[0].length);
    assertEquals(1.0, x[0][0], 0.0);
    assertEquals(3.0, x[0][1], 0.0);
  }
}
