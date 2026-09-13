package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGCSVTokenizer;
import jp.riken.brain.ni.samuraigraph.base.SGConstants;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

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
            new String[] {SGDataColumnTypeConstants.X_VALUE, "Y"});
    assertEquals(SGDataColumnTypeConstants.X_VALUE, canonical[0]);
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, canonical[1]);
  }

  @Test
  void isArchiveDataSetOperationChecksMode() {
    assertTrue(
        SGDataMiscUtility.isArchiveDataSetOperation(
            SGConstants.OPERATION.SAVE_TO_ARCHIVE_DATA_SET));
    assertFalse(
        SGDataMiscUtility.isArchiveDataSetOperation(SGConstants.OPERATION.DUPLICATE_OBJECT));
  }

  @Test
  void getDataColumnTypeCommandBuildsParenthesizedMapping() {
    List<String> vars = Arrays.asList("x", "y");
    List<String> types =
        Arrays.asList(SGDataColumnTypeConstants.X_VALUE, SGDataColumnTypeConstants.Y_VALUE);
    assertEquals("(x:X,y:Y)", SGDataMiscUtility.getDataColumnTypeCommand(vars, types));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGDataMiscUtility.getDataColumnTypeCommand(Arrays.asList("x"), types));
  }

  @Test
  void checkDataColumnsAcceptsBasicSXYSelection() {
    SGDataColumnInfo[] columns = {
      new SGSDArrayDataColumnInfo("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, 4),
      new SGSDArrayDataColumnInfo("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, 4)
    };
    columns[0].setColumnType(SGDataColumnTypeConstants.X_VALUE);
    columns[1].setColumnType(SGDataColumnTypeConstants.Y_VALUE);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    assertTrue(SGDataMiscUtility.checkDataColumns(SGDataTypeConstants.SXY_DATA, columns, infoMap));
  }

  @Test
  void getColumnTypeCandidatesForSDArraySXYZ() {
    String[] candidates =
        SGDataMiscUtility.getColumnTypeCandidates(
            SGDataTypeConstants.SXYZ_DATA,
            new HashMap<String, Object>(),
            SGDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    assertNotNull(candidates);
    assertEquals(4, candidates.length);
    assertEquals(SGDataColumnTypeConstants.X_VALUE, candidates[1]);
    assertEquals(SGDataColumnTypeConstants.Z_VALUE, candidates[3]);
  }

  @Test
  void isComplementButtonVisibleForMultipleSXY() {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_MULTIPLE_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.TRUE);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
    assertTrue(SGDataMiscUtility.isComplementButtonVisible(infoMap));
  }

  @Test
  void isComplementButtonVisibleForSingleSXY() {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
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
    grid.put(SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, Boolean.TRUE);
    assertEquals(Boolean.TRUE, SGDataMiscUtility.isGridPlot(SGDataTypeConstants.SXYZ_DATA, grid));
    Map<String, Object> stride = new HashMap<String, Object>();
    stride.put(SGDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE, new SGIntegerSeriesSet());
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
    cols.add(new SGSDArrayDataColumnInfo("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, 4));
    cols.add(new SGSDArrayDataColumnInfo("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, 4));
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_DATA);
    String[] cur = {SGDataColumnTypeConstants.X_VALUE, ""};
    String[] complemented = SGDataMiscUtility.getComplementedColumnType(infoMap, cur, cols);
    assertArrayEquals(
        new String[] {SGDataColumnTypeConstants.X_VALUE, SGDataColumnTypeConstants.Y_VALUE},
        complemented);
    assertNull(
        SGDataMiscUtility.getComplementedColumnType(
            infoMap, new String[] {SGDataColumnTypeConstants.X_VALUE}, cols));
  }

  @Test
  void isComplementedButtonEnabledForSingleSDArraySelection() {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_DATA);
    assertTrue(
        SGDataMiscUtility.isComplementedButtonEnabled(
            infoMap,
            new String[] {SGDataColumnTypeConstants.X_VALUE, ""},
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
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE),
      column("label", SGDataColumnTypeConstants.VALUE_TYPE_TEXT, "")
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
      column("bad", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, "UnknownType")
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

  @Test
  void disposeSXYDataArrayDisposesEachElement() {
    SGISXYTypeSingleData[] array = {
      mock(SGISXYTypeSingleData.class), mock(SGISXYTypeSingleData.class)
    };
    SGDataMiscUtility.disposeSXYDataArray(array);
    verify(array[0]).dispose();
    verify(array[1]).dispose();
  }

  @Test
  void getColumnIndexListOfNumberMarksNumericTokens() {
    assertEquals(
        java.util.Arrays.asList(1, 0, 1, 1, 0),
        SGDataMiscUtility.getColumnIndexListOfNumber(
            java.util.Arrays.asList(
                new SGCSVTokenizer.Token("1.5", false),
                new SGCSVTokenizer.Token("abc", false),
                new SGCSVTokenizer.Token("10", false),
                new SGCSVTokenizer.Token("1e3", false),
                new SGCSVTokenizer.Token("1.5", true))));
  }

  @Test
  void updateDataColumnsReturnsCopyForNonSXYTypes() {
    SGSDArrayDataColumnInfo[] cols = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE)
    };
    String[] types = {SGDataColumnTypeConstants.X_VALUE};
    String[] ret = SGDataMiscUtility.updateDataColumns(SGDataTypeConstants.SXYZ_DATA, cols, types);
    assertArrayEquals(types, ret);
  }

  @Test
  void updateDataColumnsKeepsValidXYTypes() {
    SGSDArrayDataColumnInfo[] cols = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE),
      column("l", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, "")
    };
    String[] types = {SGDataColumnTypeConstants.X_VALUE, SGDataColumnTypeConstants.Y_VALUE, ""};
    String[] ret = SGDataMiscUtility.updateDataColumns(SGDataTypeConstants.SXY_DATA, cols, types);
    assertArrayEquals(types, ret);
  }

  @Test
  void getDataTypeCandidateListAddsTypesForNumberColumns() {
    java.util.List<SGCSVTokenizer.Token> tokens =
        java.util.Arrays.asList(
            new SGCSVTokenizer.Token("1.5", false), new SGCSVTokenizer.Token("abc", false));
    java.util.List<Integer> indexList = java.util.Arrays.asList(1, 0);
    java.util.List<String> cList = new java.util.ArrayList<String>();
    assertTrue(SGDataMiscUtility.getDataTypeCandidateList(tokens, indexList, cList));
    assertTrue(cList.contains(SGDataTypeConstants.SXY_DATA));
    assertFalse(cList.contains(SGDataTypeConstants.SXYZ_DATA));
  }

  @Test
  void getDataTypeCandidateListAddsDateTypeForDateTokens() {
    java.util.List<SGCSVTokenizer.Token> tokens =
        java.util.Arrays.asList(new SGCSVTokenizer.Token("2020-01-01", false));
    java.util.List<Integer> indexList = java.util.Arrays.asList(1);
    java.util.List<String> cList = new java.util.ArrayList<String>();
    SGDataMiscUtility.getDataTypeCandidateList(tokens, indexList, cList);
    assertTrue(cList.contains(SGDataTypeConstants.SXY_DATA));
    assertTrue(cList.contains(SGDataTypeConstants.SXY_DATE_DATA));
  }

  @Test
  void isEmptyOrRepeatedColumnTitleMarksEmptyAndDuplicates() {
    SGSDArrayDataColumnInfo[] cols = {
      column("", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, ""),
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, ""),
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, ""),
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, "")
    };
    boolean[] result = SGDataMiscUtility.isEmptyOrRepeatedColumnTitle(cols);
    assertTrue(result[0]);
    assertTrue(result[1]);
    assertTrue(result[2]);
    assertFalse(result[3]);
  }

  @Test
  void isPolarReadsSelectionFlag() {
    java.util.Map<String, Object> infoMap = new java.util.HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    assertTrue(SGDataMiscUtility.isPolar(infoMap));
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertFalse(SGDataMiscUtility.isPolar(infoMap));
  }

  @Test
  void isPolarThrowsWithoutSelection() {
    assertThrows(
        Error.class, () -> SGDataMiscUtility.isPolar(new java.util.HashMap<String, Object>()));
  }

  @Test
  void getSXYColumnTypeClassifiesErrorAndTickCols() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGDataColumnTypeConstants.Y_VALUE);
    SGNetCDFDataColumnInfo leInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("le"), SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "height");
    SGNetCDFDataColumnInfo ueInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("ue"), SGDataColumnTypeConstants.UPPER_ERROR_VALUE, "height");
    SGNetCDFDataColumnInfo tInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("le"), SGDataColumnTypeConstants.TICK_LABEL, "height");
    SGDataColumnInfo[] cols = {xInfo, yInfo, leInfo, ueInfo, tInfo};
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
    assertEquals(2, lIndexMap.get(1).intValue());
    assertEquals(3, uIndexMap.get(1).intValue());
    assertEquals(4, tIndexMap.get(1).intValue());
  }

  @Test
  void updateDataColumnsClearsUnassignableErrorTypes() {
    SGSDArrayDataColumnInfo[] cols = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE),
      column("label", SGDataColumnTypeConstants.VALUE_TYPE_TEXT, "")
    };
    String[] types = {
      SGDataColumnTypeConstants.X_VALUE,
      SGDataColumnTypeConstants.Y_VALUE,
      SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for y"
    };
    String[] ret = SGDataMiscUtility.updateDataColumns(SGDataTypeConstants.SXY_DATA, cols, types);
    assertEquals(
        SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for y", ret[2], Arrays.toString(ret));
  }

  @Test
  void getColumnNameAndAppendedNumberListRejectsBadSuffix() {
    SGSDArrayDataColumnInfo[] cols = {
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE)
    };
    cols[0].setColumnType(SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for no");
    List<String> nameList = new java.util.ArrayList<String>();
    List<Integer> indexList = new java.util.ArrayList<Integer>();
    assertFalse(
        SGDataMiscUtility.getColumnNameAndAppendedNumberList(
            cols, SGDataColumnTypeConstants.LOWER_ERROR_VALUE, nameList, indexList));
  }

  @Test
  void getColumnNameAndAppendedNumberListAcceptsHolderName() {
    SGSDArrayDataColumnInfo[] cols = {
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE)
    };
    SGSDArrayDataColumnInfo le = column("le", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, "");
    le.setColumnType(SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for y");
    List<String> nameList = new java.util.ArrayList<String>();
    List<Integer> indexList = new java.util.ArrayList<Integer>();
    assertTrue(
        SGDataMiscUtility.getColumnNameAndAppendedNumberList(
            new SGSDArrayDataColumnInfo[] {cols[0], le},
            SGDataColumnTypeConstants.LOWER_ERROR_VALUE,
            nameList,
            indexList));
    assertEquals("le", nameList.get(0));
    assertEquals(0, indexList.get(0).intValue());
  }

  @Test
  void getColumnListStartsWithFiltersByPrefix() {
    SGSDArrayDataColumnInfo[] cols = {
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE),
      column(
          "le",
          SGDataColumnTypeConstants.VALUE_TYPE_NUMBER,
          SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for y")
    };
    List<SGDataColumnInfo> list =
        SGDataMiscUtility.getColumnListStartsWith(
            cols, SGDataColumnTypeConstants.LOWER_ERROR_VALUE);
    assertEquals(1, list.size());
    assertEquals("le", list.get(0).getName());
  }

  @Test
  void getColumnListMatchesExactType() {
    SGSDArrayDataColumnInfo[] cols = {
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE),
      column(
          "le",
          SGDataColumnTypeConstants.VALUE_TYPE_NUMBER,
          SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for y")
    };
    List<SGDataColumnInfo> list =
        SGDataMiscUtility.getColumnList(cols, SGDataColumnTypeConstants.Y_VALUE);
    assertEquals(1, list.size());
    assertEquals("y", list.get(0).getName());
  }

  @Test
  void getColumnNameListCollectsMatchingNames() {
    SGSDArrayDataColumnInfo[] cols = {
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE),
      column(
          "le",
          SGDataColumnTypeConstants.VALUE_TYPE_NUMBER,
          SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for y")
    };
    List<String> names =
        SGDataMiscUtility.getColumnNameList(cols, SGDataColumnTypeConstants.Y_VALUE);
    assertEquals(java.util.Collections.singletonList("y"), names);
  }

  @Test
  void checkDataColumnsAcceptsMultipleSXY() {
    SGSDArrayDataColumnInfo[] cols = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("y1", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE),
      column("y2", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE)
    };
    assertTrue(
        SGDataMiscUtility.checkDataColumns(
            SGDataTypeConstants.SXY_MULTIPLE_DATA, cols, new HashMap<String, Object>()));
  }

  @Test
  void checkDataColumnsRejectsMissingXY() {
    SGSDArrayDataColumnInfo[] cols = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("l", SGDataColumnTypeConstants.VALUE_TYPE_TEXT, "")
    };
    assertFalse(
        SGDataMiscUtility.checkDataColumns(
            SGDataTypeConstants.SXY_MULTIPLE_DATA, cols, new HashMap<String, Object>()));
  }

  @Test
  void checkDataColumnsRejectsSamplingRateMultipleValues() {
    SGSDArrayDataColumnInfo[] cols = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column(
          "y1",
          SGDataColumnTypeConstants.VALUE_TYPE_SAMPLING_RATE,
          SGDataColumnTypeConstants.Y_VALUE),
      column(
          "y2",
          SGDataColumnTypeConstants.VALUE_TYPE_SAMPLING_RATE,
          SGDataColumnTypeConstants.Y_VALUE)
    };
    assertFalse(
        SGDataMiscUtility.checkDataColumns(
            SGDataTypeConstants.SXY_MULTIPLE_DATA, cols, new HashMap<String, Object>()));
  }

  @Test
  void checkDataColumnsAcceptsSXYZAndRejectsDuplicates() {
    SGSDArrayDataColumnInfo[] cols = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE),
      column("z", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Z_VALUE)
    };
    assertTrue(
        SGDataMiscUtility.checkDataColumns(SGDataTypeConstants.SXYZ_DATA, cols, new HashMap<>()));
    SGSDArrayDataColumnInfo[] dups = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("x2", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("z", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Z_VALUE)
    };
    assertFalse(
        SGDataMiscUtility.checkDataColumns(SGDataTypeConstants.SXYZ_DATA, dups, new HashMap<>()));
  }

  @Test
  void checkDataColumnsAcceptsVXYModes() {
    SGSDArrayDataColumnInfo[] ortho = {
      column(
          "x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_COORDINATE),
      column(
          "y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_COORDINATE),
      column(
          "vx", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_COMPONENT),
      column(
          "vy", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_COMPONENT)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertTrue(SGDataMiscUtility.checkDataColumns(SGDataTypeConstants.VXY_DATA, ortho, infoMap));
    SGSDArrayDataColumnInfo[] polar = {
      column(
          "x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_COORDINATE),
      column(
          "y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_COORDINATE),
      column(
          "mag", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.MAGNITUDE),
      column("ang", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.ANGLE)
    };
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    assertTrue(SGDataMiscUtility.checkDataColumns(SGDataTypeConstants.VXY_DATA, polar, infoMap));
    assertFalse(SGDataMiscUtility.checkDataColumns(SGDataTypeConstants.VXY_DATA, ortho, infoMap));
  }

  @Test
  void checkDataColumnsChecksErrorBarConsistency() {
    SGSDArrayDataColumnInfo[] cols = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE),
      column(
          "le",
          SGDataColumnTypeConstants.VALUE_TYPE_NUMBER,
          SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for y"),
      column(
          "ue",
          SGDataColumnTypeConstants.VALUE_TYPE_NUMBER,
          SGDataColumnTypeConstants.UPPER_ERROR_VALUE + " for y")
    };
    assertTrue(
        SGDataMiscUtility.checkDataColumns(SGDataTypeConstants.SXY_DATA, cols, new HashMap<>()));
    SGSDArrayDataColumnInfo[] onlyLower = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE),
      column(
          "le",
          SGDataColumnTypeConstants.VALUE_TYPE_NUMBER,
          SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for y")
    };
    assertFalse(
        SGDataMiscUtility.checkDataColumns(
            SGDataTypeConstants.SXY_DATA, onlyLower, new HashMap<>()));
    SGSDArrayDataColumnInfo[] dupLower = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE),
      column(
          "le1",
          SGDataColumnTypeConstants.VALUE_TYPE_NUMBER,
          SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for y"),
      column(
          "le2",
          SGDataColumnTypeConstants.VALUE_TYPE_NUMBER,
          SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for y"),
      column(
          "ue",
          SGDataColumnTypeConstants.VALUE_TYPE_NUMBER,
          SGDataColumnTypeConstants.UPPER_ERROR_VALUE + " for y")
    };
    assertFalse(
        SGDataMiscUtility.checkDataColumns(
            SGDataTypeConstants.SXY_DATA, dupLower, new HashMap<>()));
  }

  @Test
  void checkDataColumnsAcceptsMDArrayMultiple() {
    SGMDArrayDataColumnInfo x =
        SGTestMDArrayColumns.column("x", new int[] {4}, SGDataColumnTypeConstants.X_VALUE);
    x.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    SGMDArrayDataColumnInfo y1 =
        SGTestMDArrayColumns.column("y1", new int[] {4}, SGDataColumnTypeConstants.Y_VALUE);
    y1.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    SGMDArrayDataColumnInfo y2 =
        SGTestMDArrayColumns.column("y2", new int[] {4}, SGDataColumnTypeConstants.Y_VALUE);
    y2.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    assertTrue(
        SGDataMiscUtility.checkDataColumns(
            SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA,
            new SGDataColumnInfo[] {x, y1, y2},
            new HashMap<String, Object>()));
  }

  @Test
  void getColumnTypeCandidatesForSXYSDArray() {
    SGSDArrayDataColumnInfo[] cols = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE)
    };
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_COLUMN_INFO, cols);
    infoMap.put(SGDataInformationKeyConstants.KEY_CURRENT_ROW_INDEX, 0);
    String[] items =
        SGDataMiscUtility.getColumnTypeCandidates(
            SGDataTypeConstants.SXY_DATA, infoMap, SGDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    assertNotNull(items);
    assertTrue(java.util.Arrays.asList(items).contains(SGDataColumnTypeConstants.X_VALUE));
    assertTrue(
        java.util.Arrays.asList(items).contains(SGDataColumnTypeConstants.TICK_LABEL + " for y"));
  }

  private static SGNetCDFFile createNetCDFFile(final String path) throws Exception {
    Files.deleteIfExists(Path.of(path));
    ucar.nc2.write.NetcdfFormatWriter.Builder writer =
        ucar.nc2.write.NetcdfFormatWriter.createNewNetcdf3(path);
    ucar.nc2.Dimension xDim = writer.addDimension("x", 5);
    ucar.nc2.Dimension yDim = writer.addDimension("y", 4);
    writer.addVariable("x", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(xDim));
    writer.addVariable("y", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(yDim));
    writer.addVariable("v1", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(yDim, xDim));
    try (ucar.nc2.write.NetcdfFormatWriter ignored = writer.build()) {}
    return new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path));
  }

  @Test
  void getColumnTypeCandidatesForNetCDFTypes() throws Exception {
    String path = Files.createTempFile("samurai-graph-test", ".nc").toString();
    Files.deleteIfExists(Path.of(path));
    SGNetCDFFile file = createNetCDFFile(path);
    SGNetCDFDataColumnInfo xCoord =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGDataColumnTypeConstants.X_COORDINATE);
    SGNetCDFDataColumnInfo v1 =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("v1"), SGDataColumnTypeConstants.X_COMPONENT);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    infoMap.put(SGDataInformationKeyConstants.KEY_COLUMN_INFO, new SGDataColumnInfo[] {xCoord, v1});
    infoMap.put(SGDataInformationKeyConstants.KEY_CURRENT_ROW_INDEX, 0);
    String[] items =
        SGDataMiscUtility.getColumnTypeCandidates(
            SGDataTypeConstants.SXY_NETCDF_DATA,
            infoMap,
            SGDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    assertNotNull(items);
    assertTrue(java.util.Arrays.asList(items).contains(SGDataColumnTypeConstants.PICKUP));

    infoMap.put(SGDataInformationKeyConstants.KEY_CURRENT_ROW_INDEX, 1);
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    String[] polar =
        SGDataMiscUtility.getColumnTypeCandidates(
            SGDataTypeConstants.VXY_NETCDF_DATA,
            infoMap,
            SGDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    assertNotNull(polar);
    assertTrue(java.util.Arrays.asList(polar).contains(SGDataColumnTypeConstants.MAGNITUDE));

    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    String[] orthogonal =
        SGDataMiscUtility.getColumnTypeCandidates(
            SGDataTypeConstants.VXY_NETCDF_DATA,
            infoMap,
            SGDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    assertNotNull(orthogonal);

    String[] sxyz =
        SGDataMiscUtility.getColumnTypeCandidates(
            SGDataTypeConstants.SXYZ_NETCDF_DATA,
            infoMap,
            SGDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    assertNotNull(sxyz);
    assertTrue(java.util.Arrays.asList(sxyz).contains(SGDataColumnTypeConstants.Z_VALUE));
  }

  @Test
  void getColumnTypeCandidatesForMDArrayTypes() {
    SGMDArrayDataColumnInfo x =
        SGTestMDArrayColumns.column("x", new int[] {4}, SGDataColumnTypeConstants.X_VALUE);
    x.setDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    infoMap.put(SGDataInformationKeyConstants.KEY_COLUMN_INFO, new SGDataColumnInfo[] {x});
    infoMap.put(SGDataInformationKeyConstants.KEY_CURRENT_ROW_INDEX, 0);
    String[] sxy =
        SGDataMiscUtility.getColumnTypeCandidates(
            SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
            infoMap,
            SGDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    assertNotNull(sxy);
    String[] sxyz =
        SGDataMiscUtility.getColumnTypeCandidates(
            SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA,
            new HashMap<String, Object>(),
            SGDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    assertEquals(4, sxyz.length);
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    String[] vxy =
        SGDataMiscUtility.getColumnTypeCandidates(
            SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA,
            infoMap,
            SGDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    assertNotNull(vxy);
  }

  @Test
  void getColumnTypeCandidatesRejectsInvalidType() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            SGDataMiscUtility.getColumnTypeCandidates(
                "INVALID_TYPE",
                new HashMap<String, Object>(),
                SGDataColumnTypeConstants.VALUE_TYPE_NUMBER));
  }

  @Test
  void getSXYDimensionDataColumnTypeSortsColumns() {
    SGSDArrayDataColumnInfo[] cols = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("y", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.Y_VALUE),
      column(
          "le",
          SGDataColumnTypeConstants.VALUE_TYPE_NUMBER,
          SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for y"),
      column(
          "ue",
          SGDataColumnTypeConstants.VALUE_TYPE_NUMBER,
          SGDataColumnTypeConstants.UPPER_ERROR_VALUE + " for y"),
      column(
          "lu",
          SGDataColumnTypeConstants.VALUE_TYPE_NUMBER,
          SGDataColumnTypeConstants.LOWER_UPPER_ERROR_VALUE + " for y"),
      column(
          "tl",
          SGDataColumnTypeConstants.VALUE_TYPE_TEXT,
          SGDataColumnTypeConstants.TICK_LABEL + " for y"),
      column("pk", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.PICKUP),
      column(
          "tm",
          SGDataColumnTypeConstants.VALUE_TYPE_NUMBER,
          SGDataColumnTypeConstants.ANIMATION_FRAME),
      column("ix", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.INDEX),
      column("blank", SGDataColumnTypeConstants.VALUE_TYPE_TEXT, "")
    };
    List<SGDataColumnInfo> xInfoList = new java.util.ArrayList<SGDataColumnInfo>();
    List<SGDataColumnInfo> yInfoList = new java.util.ArrayList<SGDataColumnInfo>();
    List<SGDataColumnInfo> leInfoList = new java.util.ArrayList<SGDataColumnInfo>();
    List<SGDataColumnInfo> ueInfoList = new java.util.ArrayList<SGDataColumnInfo>();
    List<SGDataColumnInfo> tlInfoList = new java.util.ArrayList<SGDataColumnInfo>();
    List<SGDataColumnInfo> pickupInfoList = new java.util.ArrayList<SGDataColumnInfo>();
    List<SGDataColumnInfo> timeInfoList = new java.util.ArrayList<SGDataColumnInfo>();
    List<SGDataColumnInfo> indexInfoList = new java.util.ArrayList<SGDataColumnInfo>();
    assertTrue(
        SGDataMiscUtility.getSXYDimensionDataColumnType(
            cols,
            xInfoList,
            yInfoList,
            leInfoList,
            ueInfoList,
            tlInfoList,
            pickupInfoList,
            timeInfoList,
            indexInfoList));
    assertEquals(1, xInfoList.size());
    assertEquals(1, yInfoList.size());
    assertEquals(2, leInfoList.size());
    assertEquals(2, ueInfoList.size());
    assertEquals(1, tlInfoList.size());
    assertEquals(1, pickupInfoList.size());
    assertEquals(1, timeInfoList.size());
    assertEquals(1, indexInfoList.size());
    assertFalse(
        SGDataMiscUtility.getSXYDimensionDataColumnType(
            new SGDataColumnInfo[] {
              column("bad", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, "UnknownType")
            },
            new java.util.ArrayList<SGDataColumnInfo>(),
            new java.util.ArrayList<SGDataColumnInfo>(),
            new java.util.ArrayList<SGDataColumnInfo>(),
            new java.util.ArrayList<SGDataColumnInfo>(),
            new java.util.ArrayList<SGDataColumnInfo>(),
            new java.util.ArrayList<SGDataColumnInfo>(),
            new java.util.ArrayList<SGDataColumnInfo>(),
            new java.util.ArrayList<SGDataColumnInfo>()));
  }

  @Test
  void addGridTypeSetsFlagForMDArray() {
    SGMDArrayDataColumnInfo z =
        SGTestMDArrayColumns.column("z", new int[] {4, 5}, SGDataColumnTypeConstants.Z_VALUE);
    z.setDimensionIndex(SGMDArrayConstants.KEY_SXYZ_X_DIMENSION, 1);
    z.setDimensionIndex(SGMDArrayConstants.KEY_SXYZ_Y_DIMENSION, 0);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    assertTrue(
        SGDataMiscUtility.addGridType(
            new SGDataColumnInfo[] {z}, SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA, infoMap));
    assertEquals(Boolean.TRUE, infoMap.get(SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG));

    SGMDArrayDataColumnInfo mag =
        SGTestMDArrayColumns.column("mag", new int[] {4, 5}, SGDataColumnTypeConstants.MAGNITUDE);
    mag.setDimensionIndex(SGMDArrayConstants.KEY_VXY_X_DIMENSION, 1);
    mag.setDimensionIndex(SGMDArrayConstants.KEY_VXY_Y_DIMENSION, 0);
    Map<String, Object> vxyMap = new HashMap<String, Object>();
    vxyMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    assertTrue(
        SGDataMiscUtility.addGridType(
            new SGDataColumnInfo[] {mag}, SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA, vxyMap));
    assertEquals(Boolean.TRUE, vxyMap.get(SGDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG));
    assertFalse(
        SGDataMiscUtility.addGridType(
            new SGDataColumnInfo[] {mag},
            SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA,
            new HashMap<>()));
  }

  @Test
  void isGridPlotForVXYData() {
    Map<String, Object> grid = new HashMap<String, Object>();
    grid.put(SGDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, Boolean.TRUE);
    assertEquals(Boolean.TRUE, SGDataMiscUtility.isGridPlot(SGDataTypeConstants.VXY_DATA, grid));
    Map<String, Object> stride = new HashMap<String, Object>();
    stride.put(SGDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE, new SGIntegerSeriesSet());
    assertEquals(Boolean.FALSE, SGDataMiscUtility.isGridPlot(SGDataTypeConstants.VXY_DATA, stride));
    assertEquals(
        Boolean.TRUE, SGDataMiscUtility.isGridPlot(SGDataTypeConstants.VXY_DATA, new HashMap<>()));
  }

  @Test
  void updateDataColumnsClearsErrorBarsForInvalidHolder() {
    SGSDArrayDataColumnInfo[] cols = {
      column("x", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGDataColumnTypeConstants.X_VALUE),
      column("l", SGDataColumnTypeConstants.VALUE_TYPE_TEXT, "")
    };
    String[] types = {
      SGDataColumnTypeConstants.X_VALUE,
      SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for missing"
    };
    String[] ret = SGDataMiscUtility.updateDataColumns(SGDataTypeConstants.SXY_DATA, cols, types);
    assertNull(ret);
  }
}
