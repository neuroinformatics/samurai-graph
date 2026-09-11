package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeUtility.DefaultColumnTypeResult;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDefaultColumnTypeUtility}. */
class SGDefaultColumnTypeUtilityTest {

  private static List<SGDataColumnInfo> createColumns(
      final String[] titles, final String valueType) {
    List<SGDataColumnInfo> list = new ArrayList<SGDataColumnInfo>();
    for (String title : titles) {
      list.add(new SGSDArrayDataColumnInfo(title, valueType));
    }
    return list;
  }

  private static Map<String, Object> createInfoMap(final boolean multiple) {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.valueOf(multiple));
    return infoMap;
  }

  @Test
  void testGetDefaultColumnTypesForSXYSDArray() {
    List<SGDataColumnInfo> columns =
        createColumns(new String[] {"x", "y"}, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    Map<String, Object> infoMap = createInfoMap(false);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    assertArrayEquals(
        new String[] {SGIDataColumnTypeConstants.X_VALUE, SGIDataColumnTypeConstants.Y_VALUE},
        result.getDefaultColumnTypes());
  }

  @Test
  void testGetDefaultColumnTypesForSXYSDArrayWithErrorBar() {
    List<SGDataColumnInfo> columns =
        createColumns(
            new String[] {"x", "y", "lower", "upper"},
            SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    Map<String, Object> infoMap = createInfoMap(false);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    String[] types = result.getDefaultColumnTypes();
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, types[0]);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, types[1]);
    assertTrue(types[2].startsWith(SGIDataColumnTypeConstants.LOWER_ERROR_VALUE));
    assertTrue(types[3].startsWith(SGIDataColumnTypeConstants.UPPER_ERROR_VALUE));
  }

  @Test
  void testGetDefaultColumnTypesForMultipleSXYSDArray() {
    List<SGDataColumnInfo> columns =
        createColumns(new String[] {"t", "y1", "y2"}, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    Map<String, Object> infoMap = createInfoMap(true);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_MULTIPLE_DATA, columns, infoMap);
    assertTrue(result.isSucceeded());
    String[] types = result.getDefaultColumnTypes();
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, types[0]);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, types[1]);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, types[2]);
  }

  @Test
  void testGetDefaultColumnTypesFailsForInsufficientColumns() {
    List<SGDataColumnInfo> columns =
        createColumns(new String[] {"a"}, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    Map<String, Object> infoMap = createInfoMap(false);
    DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_DATA, columns, infoMap);
    assertFalse(result.isSucceeded());
  }

  @Test
  void testGetDefaultColumnTypesRejectsNullInput() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SGDefaultColumnTypeUtility.getDefaultColumnTypes(null, null, null));
  }
}
