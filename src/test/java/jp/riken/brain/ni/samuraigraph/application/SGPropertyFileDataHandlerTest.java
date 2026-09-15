package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.VALUE_TYPE_NUMBER;
import static jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants.KEY_SXY_MULTIPLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.data.SGDataTypeConstants;
import org.junit.jupiter.api.Test;

/**
 * Characterization tests of the SDArray data handler in {@link SGPropertyFileDataHandler}: parsing
 * a plain text file into the default column information set.
 */
class SGPropertyFileDataHandlerTest {

  private static final String VERSION_NUMBER = "2.2.0";

  private static Path createTempTextFile(final String... lines) throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".txt");
    path.toFile().deleteOnExit();
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      for (String line : lines) {
        writer.write(line);
        writer.write("\n");
      }
    }
    return path;
  }

  private static Map<String, Object> createInfoMap() {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(KEY_SXY_MULTIPLE, Boolean.FALSE);
    return infoMap;
  }

  private static SGPropertyFileDataHandler createHandler() {
    return new SGPropertyFileDataHandler(new SGDataCreator(), null);
  }

  @Test
  void buildsDefaultColumnInfoFromTitleLineAndRows() throws Exception {
    Path path = createTempTextFile("time value", "1 10", "2 20", "3 30");
    SGDataColumnInfoSet colInfoSet =
        createHandler()
            .getSDArrayDefaultDataColumnInfo(
                path.toString(),
                SGDataTypeConstants.SXY_DATA,
                createInfoMap(),
                false,
                VERSION_NUMBER);

    assertNotNull(colInfoSet);
    SGDataColumnInfo[] cols = colInfoSet.getDataColumnInfoArray();
    assertEquals(2, cols.length);
    assertEquals("time", cols[0].getTitle());
    assertEquals("value", cols[1].getTitle());
    assertEquals(VALUE_TYPE_NUMBER, cols[0].getValueType());
  }

  @Test
  void insufficientColumnsProduceNoDefaultColumnInfoSet() throws Exception {
    // SXY data requires at least the X and Y columns
    Path path = createTempTextFile("text", "abc", "def");
    SGDataColumnInfoSet colInfoSet =
        createHandler()
            .getSDArrayDefaultDataColumnInfo(
                path.toString(),
                SGDataTypeConstants.SXY_DATA,
                createInfoMap(),
                false,
                VERSION_NUMBER);
    assertNull(colInfoSet);
  }

  @Test
  void rejectsUnreadableFile() throws Exception {
    SGDataColumnInfoSet colInfoSet =
        createHandler()
            .getSDArrayDefaultDataColumnInfo(
                "non-existent-file.txt",
                SGDataTypeConstants.SXY_DATA,
                createInfoMap(),
                false,
                VERSION_NUMBER);
    assertNull(colInfoSet);
  }
}
