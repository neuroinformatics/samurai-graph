package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDataMiscUtility}. */
class SGDataMiscUtilityTest {

  private static SGSDArrayDataColumnInfo column(
      final String title, final String valueType, final String columnType) {
    SGSDArrayDataColumnInfo info = new SGSDArrayDataColumnInfo(title, valueType, 4);
    info.setColumnType(columnType);
    return info;
  }

  @Test
  void createSamplingRateTitleFormatsValue() {
    assertEquals("Sampling Rate 0.5 Hz", SGDataMiscUtility.createSamplingRateTitle(0.5));
    assertEquals("Sampling Rate 1.0 Hz", SGDataMiscUtility.createSamplingRateTitle(1.0));
  }

  @Test
  void hasValidHDF5CharacterForWinRejectsInvalidChars() {
    assertTrue(SGDataMiscUtility.hasValidHDF5CharacterForWin("abc123"));
    assertFalse(SGDataMiscUtility.hasValidHDF5CharacterForWin("a\nb"));
    assertTrue(SGDataMiscUtility.isAcceptableCharHDF5Wind('a'));
    assertTrue(SGDataMiscUtility.isAcceptableCharHDF5Wind(':'));
    assertFalse(SGDataMiscUtility.isAcceptableCharHDF5Wind('\n'));
  }

  @Test
  void getCanonicalColumnTypesNormalizesEntries() {
    String[] canonical =
        SGDataMiscUtility.getCanonicalColumnTypes(
            new String[] {SGIDataColumnTypeConstants.X_VALUE, "Y"});
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, canonical[0]);
    assertEquals(SGIDataColumnTypeConstants.Y_VALUE, canonical[1]);
  }

  @Test
  void isArchiveDataSetOperationChecksMode() {
    assertTrue(
        SGDataMiscUtility.isArchiveDataSetOperation(
            SGIConstants.OPERATION.SAVE_TO_ARCHIVE_DATA_SET));
    assertFalse(
        SGDataMiscUtility.isArchiveDataSetOperation(SGIConstants.OPERATION.DUPLICATE_OBJECT));
  }

  @Test
  void getDataColumnTypeCommandBuildsParenthesizedMapping() {
    List<String> vars = Arrays.asList("x", "y");
    List<String> types =
        Arrays.asList(SGIDataColumnTypeConstants.X_VALUE, SGIDataColumnTypeConstants.Y_VALUE);
    assertEquals("(x:X,y:Y)", SGDataMiscUtility.getDataColumnTypeCommand(vars, types));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGDataMiscUtility.getDataColumnTypeCommand(Arrays.asList("x"), types));
  }

  @Test
  void checkDataColumnsAcceptsBasicSXYSelection() {
    SGDataColumnInfo[] columns = {
      new SGSDArrayDataColumnInfo("x", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, 4),
      new SGSDArrayDataColumnInfo("y", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, 4)
    };
    columns[0].setColumnType(SGIDataColumnTypeConstants.X_VALUE);
    columns[1].setColumnType(SGIDataColumnTypeConstants.Y_VALUE);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    assertTrue(SGDataMiscUtility.checkDataColumns(SGDataTypeConstants.SXY_DATA, columns, infoMap));
  }

  @Test
  void getColumnTypeCandidatesForSDArraySXYZ() {
    String[] candidates =
        SGDataMiscUtility.getColumnTypeCandidates(
            SGDataTypeConstants.SXYZ_DATA,
            new HashMap<String, Object>(),
            SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    assertNotNull(candidates);
    assertEquals(4, candidates.length);
    assertEquals(SGIDataColumnTypeConstants.X_VALUE, candidates[1]);
    assertEquals(SGIDataColumnTypeConstants.Z_VALUE, candidates[3]);
  }

  @Test
  void isComplementButtonVisibleForMultipleSXY() {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(
        SGIDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_MULTIPLE_DATA);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.TRUE);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
    assertTrue(SGDataMiscUtility.isComplementButtonVisible(infoMap));
  }

  @Test
  void isComplementButtonVisibleForSingleSXY() {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_DATA);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    assertFalse(SGDataMiscUtility.isComplementButtonVisible(infoMap));
  }

  @Test
  void isComplementButtonVisibleRejectsMissingType() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SGDataMiscUtility.isComplementButtonVisible(new HashMap<String, Object>()));
  }

  @Test
  void isGridPlotForSXYZData() {
    Map<String, Object> grid = new HashMap<String, Object>();
    grid.put(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, Boolean.TRUE);
    assertEquals(Boolean.TRUE, SGDataMiscUtility.isGridPlot(SGDataTypeConstants.SXYZ_DATA, grid));
    Map<String, Object> stride = new HashMap<String, Object>();
    stride.put(SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE, new SGIntegerSeriesSet());
    assertEquals(
        Boolean.FALSE, SGDataMiscUtility.isGridPlot(SGDataTypeConstants.SXYZ_DATA, stride));
    assertNull(
        SGDataMiscUtility.isGridPlot(SGDataTypeConstants.SXY_DATA, new HashMap<String, Object>()));
  }

  @Test
  void addGridTypeReturnsFalseForNonMDArrayData() {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    assertFalse(
        SGDataMiscUtility.addGridType(
            new SGDataColumnInfo[0], SGDataTypeConstants.SXY_DATA, infoMap));
  }

  @Test
  void getComplementedColumnTypeCompletesSingleSelection() {
    List<SGDataColumnInfo> cols = new java.util.ArrayList<SGDataColumnInfo>();
    cols.add(new SGSDArrayDataColumnInfo("x", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, 4));
    cols.add(new SGSDArrayDataColumnInfo("y", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, 4));
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_DATA);
    String[] cur = {SGIDataColumnTypeConstants.X_VALUE, ""};
    String[] complemented = SGDataMiscUtility.getComplementedColumnType(infoMap, cur, cols);
    assertArrayEquals(
        new String[] {SGIDataColumnTypeConstants.X_VALUE, SGIDataColumnTypeConstants.Y_VALUE},
        complemented);
    assertNull(
        SGDataMiscUtility.getComplementedColumnType(
            infoMap, new String[] {SGIDataColumnTypeConstants.X_VALUE}, cols));
  }

  @Test
  void isComplementedButtonEnabledForSingleSDArraySelection() {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_DATA);
    assertTrue(
        SGDataMiscUtility.isComplementedButtonEnabled(
            infoMap,
            new String[] {SGIDataColumnTypeConstants.X_VALUE, ""},
            new java.util.ArrayList<SGDataColumnInfo>()));
    assertFalse(
        SGDataMiscUtility.isComplementedButtonEnabled(
            infoMap, new String[] {"", ""}, new java.util.ArrayList<SGDataColumnInfo>()));
  }

  @Test
  void isAcceptableCharHDF5WindChecksAsciiRange() {
    assertTrue(SGDataMiscUtility.isAcceptableCharHDF5Wind('a'));
    assertTrue(SGDataMiscUtility.isAcceptableCharHDF5Wind(' '));
    assertTrue(SGDataMiscUtility.isAcceptableCharHDF5Wind('~'));
    assertFalse(SGDataMiscUtility.isAcceptableCharHDF5Wind('\u001f'));
    assertFalse(SGDataMiscUtility.isAcceptableCharHDF5Wind('\u007f'));
    assertFalse(SGDataMiscUtility.isAcceptableCharHDF5Wind('\u00e9'));
  }

  @Test
  void getSXYColumnTypeCollectsXAndYIndexes() {
    SGSDArrayDataColumnInfo[] cols = {
      column("x", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGIDataColumnTypeConstants.X_VALUE),
      column("y", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGIDataColumnTypeConstants.Y_VALUE),
      column("label", SGIDataColumnTypeConstants.VALUE_TYPE_TEXT, "")
    };
    java.util.List<Integer> xIndexList = new java.util.ArrayList<Integer>();
    java.util.List<Integer> yIndexList = new java.util.ArrayList<Integer>();
    java.util.Map<Integer, Integer> lIndexMap = new java.util.HashMap<Integer, Integer>();
    java.util.Map<Integer, Integer> uIndexMap = new java.util.HashMap<Integer, Integer>();
    java.util.Map<Integer, Integer> tIndexMap = new java.util.HashMap<Integer, Integer>();
    assertTrue(
        SGDataMiscUtility.getSXYColumnType(
            cols, xIndexList, yIndexList, lIndexMap, uIndexMap, tIndexMap));
    assertEquals(0, xIndexList.get(0).intValue());
    assertEquals(1, yIndexList.get(0).intValue());
    assertEquals(0, lIndexMap.size());
    assertEquals(0, uIndexMap.size());
    assertEquals(0, tIndexMap.size());
  }

  @Test
  void getSXYColumnTypeRejectsUnknownColumnType() {
    SGSDArrayDataColumnInfo[] cols = {
      column("bad", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, "UnknownType")
    };
    java.util.List<Integer> xIndexList = new java.util.ArrayList<Integer>();
    java.util.List<Integer> yIndexList = new java.util.ArrayList<Integer>();
    java.util.Map<Integer, Integer> lIndexMap = new java.util.HashMap<Integer, Integer>();
    java.util.Map<Integer, Integer> uIndexMap = new java.util.HashMap<Integer, Integer>();
    java.util.Map<Integer, Integer> tIndexMap = new java.util.HashMap<Integer, Integer>();
    assertFalse(
        SGDataMiscUtility.getSXYColumnType(
            cols, xIndexList, yIndexList, lIndexMap, uIndexMap, tIndexMap));
  }
}
