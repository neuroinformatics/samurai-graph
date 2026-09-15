package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.COM_DATA_ANIMATION_FRAME_DIMENSION;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.COM_DATA_PICKUP_DIMENSION;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.COM_DATA_PICKUP_END;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.COM_DATA_PICKUP_INDICES;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.COM_DATA_PICKUP_START;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGInteger;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.data.SGHDF5File;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMDArrayMultipleData;
import org.junit.jupiter.api.Test;

/**
 * Characterization tests for {@link SGColumnTypeUpdater} driving the guard branches of the MDArray
 * back end against real {@link SGSXYMDArrayMultipleData}.
 */
class SGColumnTypeUpdaterTest {

  private static Path createTempHdf5File() throws IOException {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    return path;
  }

  private static SGHDF5File createHdf5File(Path path) throws IOException {
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(path.toFile())) {
      writer.writeDoubleArray("x", new double[] {1.0, 2.0, 3.0});
      writer.writeDoubleArray("y1", new double[] {10.0, 20.0, 30.0});
      writer.writeDoubleArray("y2", new double[] {40.0, 50.0, 60.0});
    }
    IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(path.toFile());
    return new SGHDF5File(reader);
  }

  private static SGMDArrayDataColumnInfo[] infos(SGHDF5File file, String... names) {
    SGMDArrayDataColumnInfo[] infos = new SGMDArrayDataColumnInfo[names.length];
    for (int ii = 0; ii < names.length; ii++) {
      SGMDArrayVariable var = file.findVariable(names[ii]);
      SGMDArrayDataColumnInfo col =
          new SGMDArrayDataColumnInfo(var, var.getName(), var.getValueType());
      col.setGenericDimensionIndex(0);
      infos[ii] = col;
    }
    return infos;
  }

  private static SGSXYMDArrayMultipleData createData(SGHDF5File file) {
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

  private static SGColumnTypeUpdater createUpdater() throws IOException {
    SGHDF5File file = createHdf5File(createTempHdf5File());
    SGSXYMDArrayMultipleData data = createData(file);
    SGElementGroupSetInGraphSXYMultiple owner =
        new SGElementGroupSetInGraphSXYMultiple(data, new SGFigureElementGraph());
    return new SGColumnTypeUpdater(owner);
  }

  @Test
  void unquotedPickUpIndicesAreRejected() throws IOException {
    SGColumnTypeUpdater updater = createUpdater();
    SGPropertyMap map = new SGPropertyMap();
    SGPropertyResults results = new SGPropertyResults();
    map.putValue(COM_DATA_PICKUP_INDICES, "1:2:1");

    assertFalse(
        updater.setPickUpIndices(
            map, results, COM_DATA_PICKUP_INDICES, "1:2:1", new HashMap<String, SGInteger>()));
    assertEquals(SGPropertyResults.INVALID_INPUT_VALUE, results.getResult(COM_DATA_PICKUP_INDICES));
  }

  @Test
  void unrelatedKeyIsIgnoredBySetPickUpIndices() throws IOException {
    SGColumnTypeUpdater updater = createUpdater();
    SGPropertyMap map = new SGPropertyMap();
    SGPropertyResults results = new SGPropertyResults();

    assertTrue(
        updater.setPickUpIndices(
            map, results, "UnknownKey", "value", new HashMap<String, SGInteger>()));
    assertNull(results.getResult("UnknownKey"));
  }

  @Test
  void quotedPickUpIndicesAreRejectedWithoutPickedDimension() throws IOException {
    SGColumnTypeUpdater updater = createUpdater();
    SGPropertyMap map = new SGPropertyMap();
    SGPropertyResults results = new SGPropertyResults();
    map.putValue(COM_DATA_PICKUP_INDICES, "\"1:2:1\"");

    assertFalse(
        updater.setPickUpIndices(
            map, results, COM_DATA_PICKUP_INDICES, "1:2:1", new HashMap<String, SGInteger>()));
    assertEquals(SGPropertyResults.INVALID_INPUT_VALUE, results.getResult(COM_DATA_PICKUP_INDICES));
  }

  @Test
  void pickUpBoundariesAreRejectedForMdArrayData() throws IOException {
    SGColumnTypeUpdater updater = createUpdater();
    for (String key : new String[] {COM_DATA_PICKUP_START, COM_DATA_PICKUP_END}) {
      SGPropertyResults results = new SGPropertyResults();
      assertFalse(
          updater.setPickUpIndices(
              new SGPropertyMap(), results, key, "0", new HashMap<String, SGInteger>()));
      assertEquals(SGPropertyResults.INVALID_INPUT_VALUE, results.getResult(key));
    }
  }

  @Test
  void invalidPickUpAndTimeDimensionAreReported() throws IOException {
    SGColumnTypeUpdater updater = createUpdater();
    SGPropertyResults results = new SGPropertyResults();

    updater.setPickUpAndTimeDimension(results, "not-a-dimension", "not-a-dimension");
    assertEquals(
        SGPropertyResults.INVALID_INPUT_VALUE, results.getResult(COM_DATA_PICKUP_DIMENSION));
    assertEquals(
        SGPropertyResults.INVALID_INPUT_VALUE,
        results.getResult(COM_DATA_ANIMATION_FRAME_DIMENSION));
  }

  @Test
  void setPickUpResultsDoesNothingForMdArrayData() throws IOException {
    SGColumnTypeUpdater updater = createUpdater();
    SGPropertyMap map = new SGPropertyMap();
    SGPropertyResults results = new SGPropertyResults();

    updater.setPickUpResults(map, results, new SGInteger(0), new SGInteger(2), new SGInteger(1));
    assertNull(results.getResult(COM_DATA_PICKUP_START));
  }
}
