package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the pure helpers of {@link SGDataDataTypeUtility} and other SGData*Utility
 * classes.
 */
class SGDataUtilityTest {

  @Test
  void appendColumnNoAppendsOneBasedIndex() {
    assertEquals("SXY for No.1", SGDataColumnTitleUtility.appendColumnNo("SXY", 0));
    assertEquals("SXY for No.3", SGDataColumnTitleUtility.appendColumnNo("SXY", 2));
  }

  @Test
  void appendColumnTitleAppendsVariableName() {
    assertEquals("SXY for x", SGDataColumnTitleUtility.appendColumnTitle("SXY", "x"));
  }

  @Test
  void appendColumnNoOrTitleChoosesByFlag() {
    assertEquals(
        "SXY for No.2", SGDataColumnTitleUtility.appendColumnNoOrTitle("SXY", 1, true, "ignored"));
    assertEquals(
        "SXY for title", SGDataColumnTitleUtility.appendColumnNoOrTitle("SXY", 0, false, "title"));
  }

  @Test
  void removeHeaderTitleExtractsTitle() {
    assertEquals("x", SGDataColumnTitleUtility.removeHeaderTitle("SXY for x"));
    assertNull(SGDataColumnTitleUtility.removeHeaderTitle("SXY"));
  }

  @Test
  void removeHeaderNoExtractsNumber() {
    assertEquals("3", SGDataColumnTitleUtility.removeHeaderNo("SXY for No.3"));
    assertNull(SGDataColumnTitleUtility.removeHeaderNo("SXY for x"));
  }

  @Test
  void getAppendedColumnIndexReturnsZeroBasedIndex() {
    assertEquals(
        Integer.valueOf(2), SGDataColumnTitleUtility.getAppendedColumnIndex("SXY for No.3"));
    assertNull(SGDataColumnTitleUtility.getAppendedColumnIndex("SXY for x"));
  }

  @Test
  void isNetCDFDataRecognizesNetCDFTypes() {
    assertTrue(SGDataDataTypeUtility.isNetCDFData(SGDataTypeConstants.SXY_NETCDF_DATA));
    assertFalse(SGDataDataTypeUtility.isNetCDFData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isHDF5DataRecognizesHDF5Types() {
    assertTrue(SGDataDataTypeUtility.isHDF5Data(SGDataTypeConstants.SXY_HDF5_DATA));
    assertFalse(SGDataDataTypeUtility.isHDF5Data(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isSXYTypeDataRecognizesSingleAndMultipleTypes() {
    assertTrue(SGDataDataTypeUtility.isSXYTypeData(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataDataTypeUtility.isSXYTypeData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
    assertFalse(SGDataDataTypeUtility.isSXYTypeData(SGDataTypeConstants.VXY_DATA));
  }

  @Test
  void isVXYTypeDataRecognizesVectorTypes() {
    assertTrue(SGDataDataTypeUtility.isVXYTypeData(SGDataTypeConstants.VXY_DATA));
    assertFalse(SGDataDataTypeUtility.isVXYTypeData(SGDataTypeConstants.SXYZ_DATA));
  }

  @Test
  void isSXYZTypeDataRecognizesScalarTypes() {
    assertTrue(SGDataDataTypeUtility.isSXYZTypeData(SGDataTypeConstants.SXYZ_DATA));
    assertFalse(SGDataDataTypeUtility.isSXYZTypeData(SGDataTypeConstants.VXY_DATA));
  }

  @Test
  void isMultipleDataRecognizesMultipleTypes() {
    assertTrue(SGDataDataTypeUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
    assertFalse(SGDataDataTypeUtility.isMultipleData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void predicatesRejectNullInput() {
    assertThrows(
        IllegalArgumentException.class, () -> SGDataDataTypeUtility.isNetCDFData((String) null));
    assertThrows(
        IllegalArgumentException.class, () -> SGDataDataTypeUtility.isHDF5Data((String) null));
    assertThrows(
        IllegalArgumentException.class, () -> SGDataDataTypeUtility.isSXYTypeData((String) null));
  }

  @Test
  void getMinValueIgnoresNonFiniteValues() {
    assertEquals(2.0, SGDataRangeUtility.getMinValue(new double[] {Double.NaN, 5.0, 2.0}), 0.0);
    assertEquals(
        Double.NaN,
        SGDataRangeUtility.getMinValue(new double[] {Double.NaN, Double.POSITIVE_INFINITY}),
        0.0);
  }

  @Test
  void getMaxValueIgnoresNonFiniteValues() {
    assertEquals(5.0, SGDataRangeUtility.getMaxValue(new double[] {5.0, Double.NaN, 2.0}), 0.0);
    assertEquals(Double.NaN, SGDataRangeUtility.getMaxValue(new double[] {}), 0.0);
  }

  @Test
  void getBoundsReturnsMinMaxRange() {
    SGValueRange range = SGDataRangeUtility.getBounds(new double[] {3.0, -1.0, 2.0});
    assertEquals(-1.0, range.getMinValue(), 0.0);
    assertEquals(3.0, range.getMaxValue(), 0.0);
  }

  @Test
  void appendColumnTypeAppendsTitleForNetCDFOrMDArrayData() {
    SGDataColumnInfo[] colInfo = {new TestColumnInfo("x", "NUMBER")};
    assertEquals(
        "SXY for x", SGDataColumnTitleUtility.appendColumnType("SXY", 0, true, colInfo, false));
  }

  @Test
  void appendColumnTypeAppendsNumberOrTitleForOtherData() {
    SGDataColumnInfo[] colInfo = {new TestColumnInfo("x", "NUMBER")};
    assertEquals(
        "SXY for No.1", SGDataColumnTitleUtility.appendColumnType("SXY", 0, false, colInfo, true));
    assertEquals(
        "SXY for x", SGDataColumnTitleUtility.appendColumnType("SXY", 0, false, colInfo, false));
  }

  @Test
  void getColumnIndexOfAppendedColumnTitleFindsByTitle() {
    SGDataColumnInfo[] colInfo = {
      new TestColumnInfo("x", "NUMBER"), new TestColumnInfo("y", "NUMBER")
    };
    assertEquals(
        Integer.valueOf(1),
        SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle("SXY for y", colInfo));
    assertNull(SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle("SXY for z", colInfo));
    assertNull(SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle("SXY", colInfo));
  }

  @Test
  void getAppendedColumnIndexWithTitlesFindsByTitle() {
    String[] titles = {"x", "y"};
    assertEquals(
        Integer.valueOf(1), SGDataColumnTitleUtility.getAppendedColumnIndex("SXY for y", titles));
    assertNull(SGDataColumnTitleUtility.getAppendedColumnIndex("SXY for z", titles));
  }

  @Test
  void getColumnIndexOfAppendedColumnTypeUsesTitleForNetCDF() {
    SGDataColumnInfo[] colInfo = {new TestColumnInfo("x", "NUMBER")};
    assertEquals(
        Integer.valueOf(0),
        SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnType(
            "SXY for x", SGDataTypeConstants.SXY_NETCDF_DATA, colInfo));
    assertEquals(
        Integer.valueOf(0),
        SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnType(
            "SXY for No.1", SGDataTypeConstants.SXY_DATA, colInfo));
  }

  @Test
  void getMinimumNumberColumnsReturnsRequiredColumnCount() {
    assertEquals(1, SGDataDataTypeUtility.getMinimumNumberColumns(SGDataTypeConstants.SXY_DATA));
    assertEquals(4, SGDataDataTypeUtility.getMinimumNumberColumns(SGDataTypeConstants.VXY_DATA));
    assertEquals(3, SGDataDataTypeUtility.getMinimumNumberColumns(SGDataTypeConstants.SXYZ_DATA));
    assertEquals(-1, SGDataDataTypeUtility.getMinimumNumberColumns("UNKNOWN"));
  }

  @Test
  void isSXYTypeSingleDataRecognizesSingleTypes() {
    assertTrue(SGDataDataTypeUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataDataTypeUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_NETCDF_DATA));
    assertFalse(SGDataDataTypeUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
  }

  @Test
  void isSXYTypeMultipleDataRecognizesMultipleTypes() {
    assertTrue(SGDataDataTypeUtility.isSXYTypeMultipleData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
    assertTrue(
        SGDataDataTypeUtility.isSXYTypeMultipleData(SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA));
  }

  @Test
  void isSDArrayDataRecognizesSingleDimensionalTypes() {
    assertTrue(SGDataDataTypeUtility.isSDArrayData(SGDataTypeConstants.SXY_DATA));
    assertFalse(SGDataDataTypeUtility.isSDArrayData(SGDataTypeConstants.SXY_NETCDF_DATA));
  }

  @Test
  void isMDArrayDataRecognizesHDF5MatlabAndVirtualTypes() {
    assertTrue(SGDataDataTypeUtility.isMDArrayData(SGDataTypeConstants.SXY_HDF5_DATA));
    assertTrue(SGDataDataTypeUtility.isMDArrayData(SGDataTypeConstants.SXY_MATLAB_DATA));
    assertTrue(SGDataDataTypeUtility.isMDArrayData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA));
    assertFalse(SGDataDataTypeUtility.isMDArrayData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isMATLABDataRecognizesMatlabTypes() {
    assertTrue(SGDataDataTypeUtility.isMATLABData(SGDataTypeConstants.SXY_MATLAB_DATA));
    assertFalse(SGDataDataTypeUtility.isMATLABData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isVirtualMDArrayDataRecognizesVirtualTypes() {
    assertTrue(
        SGDataDataTypeUtility.isVirtualMDArrayData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA));
    assertFalse(SGDataDataTypeUtility.isVirtualMDArrayData(SGDataTypeConstants.SXY_HDF5_DATA));
  }

  @Test
  void isHDF5FileDataRecognizesHDF5AndVirtualTypes() {
    assertTrue(SGDataDataTypeUtility.isHDF5FileData(SGDataTypeConstants.SXY_HDF5_DATA));
    assertTrue(SGDataDataTypeUtility.isHDF5FileData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA));
    assertFalse(SGDataDataTypeUtility.isHDF5FileData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isNetCDFDimensionDataRecognizesDimensionTypes() {
    assertTrue(
        SGDataDataTypeUtility.isNetCDFDimensionData(
            SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA));
    assertFalse(SGDataDataTypeUtility.isNetCDFDimensionData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isValidDataAcceptsKnownDataTypes() {
    assertTrue(SGDataDataTypeUtility.isValidData(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataDataTypeUtility.isValidData(SGDataTypeConstants.SXY_NETCDF_DATA));
    assertFalse(SGDataDataTypeUtility.isValidData("UNKNOWN"));
  }

  @Test
  void updateInfoMapStoresColumnInfoCopy() {
    SGDataColumnInfo[] colInfo = {new TestColumnInfo("x", "NUMBER")};
    Map<String, Object> map = new HashMap<String, Object>();
    Map<String, Object> updated =
        SGDataMiscUtility.updateInfoMap(SGDataTypeConstants.SXY_DATA, colInfo, map);
    Object stored = updated.get(SGIDataInformationKeyConstants.KEY_COLUMN_INFO);
    assertTrue(stored instanceof SGDataColumnInfo[]);
    assertArrayEquals(colInfo, (SGDataColumnInfo[]) stored);
  }

  @Test
  void hasTickLabelsRecognizesTickLabelTypes() {
    assertTrue(SGDataDataTypeUtility.hasTickLabels(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataDataTypeUtility.hasTickLabels(SGDataTypeConstants.SXY_DATE_DATA));
    assertFalse(SGDataDataTypeUtility.hasTickLabels(SGDataTypeConstants.SXY_MULTIPLE_DATA));
  }

  @Test
  void getMinValueOfRangeListReturnsSmallestValidMin() {
    List<SGValueRange> list = Arrays.asList(new SGValueRange(1.0, 2.0), new SGValueRange(5.0, 8.0));
    assertEquals(1.0, SGDataRangeUtility.getMinValue(list), 0.0);
  }

  @Test
  void getMinValueOfRangeListIgnoresInvalidRanges() {
    List<SGValueRange> list =
        Arrays.asList(new SGValueRange(Double.NaN, 2.0), new SGValueRange(3.0, 4.0));
    assertEquals(3.0, SGDataRangeUtility.getMinValue(list), 0.0);
  }

  @Test
  void getMinValueOfEmptyRangeListReturnsNull() {
    assertNull(SGDataRangeUtility.getMinValue(Collections.emptyList()));
  }

  @Test
  void getMaxValueOfRangeListReturnsLargestValidMax() {
    List<SGValueRange> list = Arrays.asList(new SGValueRange(1.0, 2.0), new SGValueRange(5.0, 8.0));
    assertEquals(8.0, SGDataRangeUtility.getMaxValue(list), 0.0);
  }

  @Test
  void findColumnWithNameMatchesCaseInsensitively() {
    SGDataColumnInfo[] cols = {
      new TestColumnInfo("x", "NUMBER"), new TestColumnInfo("y", "NUMBER")
    };
    assertSame(cols[1], SGDataColumnInfoUtility.findColumnWithName(cols, "Y"));
    assertNull(SGDataColumnInfoUtility.findColumnWithName(cols, "z"));
  }

  @Test
  void findColumnsWithColumnTypeMatchesIgnoreCase() {
    SGDataColumnInfo[] cols = {
      new TestColumnInfo("x", "NUMBER"), new TestColumnInfo("y", "NUMBER")
    };
    cols[0].setColumnType("X_VALUE");
    cols[1].setColumnType("Y_VALUE");
    List<SGDataColumnInfo> result =
        SGDataColumnInfoUtility.findColumnsWithColumnType(cols, "x_value");
    assertEquals(1, result.size());
    assertSame(cols[0], result.get(0));
  }

  @Test
  void findColumnsWithColumnTypeStartsWithMatchesPrefix() {
    SGDataColumnInfo[] cols = {
      new TestColumnInfo("x", "NUMBER"), new TestColumnInfo("y", "NUMBER")
    };
    cols[0].setColumnType("LOWER_ERROR_VALUE");
    cols[1].setColumnType("Y_VALUE");
    List<SGDataColumnInfo> result =
        SGDataColumnInfoUtility.findColumnsWithColumnTypeStartsWith(cols, "lower");
    assertEquals(1, result.size());
    assertSame(cols[0], result.get(0));
  }

  @Test
  void findColumnsWithValueTypeMatchesIgnoreCase() {
    SGDataColumnInfo[] cols = {new TestColumnInfo("x", "NUMBER"), new TestColumnInfo("y", "TEXT")};
    List<SGDataColumnInfo> result =
        SGDataColumnInfoUtility.findColumnsWithValueType(cols, "number");
    assertEquals(1, result.size());
    assertSame(cols[0], result.get(0));
  }

  @Test
  void hasEqualColumnTypeComparesColumnTypes() {
    SGDataColumnInfo[] a = {new TestColumnInfo("x", "NUMBER")};
    a[0].setColumnType("X_VALUE");
    SGDataColumnInfo[] b = {new TestColumnInfo("x", "NUMBER")};
    b[0].setColumnType("X_VALUE");
    SGDataColumnInfo[] c = {new TestColumnInfo("x", "NUMBER")};
    c[0].setColumnType("Y_VALUE");
    assertTrue(SGDataColumnInfoUtility.hasEqualColumnType(a, b));
    assertFalse(SGDataColumnInfoUtility.hasEqualColumnType(a, c));
  }

  @Test
  void hasEqualColumnTypeRejectsNullInput() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SGDataColumnInfoUtility.hasEqualColumnType(null, null));
  }

  @Test
  void hasEqualColumnTypeRejectsDifferentLengths() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            SGDataColumnInfoUtility.hasEqualColumnType(
                new SGDataColumnInfo[1], new SGDataColumnInfo[2]));
  }

  @Test
  void hasEqualInputComparesClassAndColumnType() {
    SGDataColumnInfo[] a = {new TestColumnInfo("x", "NUMBER")};
    a[0].setColumnType("X_VALUE");
    SGDataColumnInfo[] b = {new TestColumnInfo("x", "NUMBER")};
    b[0].setColumnType("X_VALUE");
    SGDataColumnInfo[] c = {new TestColumnInfo("x", "NUMBER")};
    c[0].setColumnType("Y_VALUE");
    assertTrue(SGDataColumnInfoUtility.hasEqualInput(a, b));
    assertFalse(SGDataColumnInfoUtility.hasEqualInput(a, c));
  }

  @Test
  void hasEqualInputRejectsNullInput() {
    assertThrows(
        IllegalArgumentException.class, () -> SGDataColumnInfoUtility.hasEqualInput(null, null));
  }

  @Test
  void getCanonicalColumnTypesMapsAliasesToCanonicalNames() {
    String[] result = SGDataMiscUtility.getCanonicalColumnTypes(new String[] {"x", "unknown"});
    assertEquals("X", result[0]);
    assertEquals("unknown", result[1]);
  }

  @Test
  void bindVariableNamesInBracketWithStringArray() {
    assertEquals(
        "{a,b,c}", SGDataTextUtility.bindVariableNamesInBracket(new String[] {"a", "b", "c"}));
  }

  @Test
  void bindVariableNamesInBracketWithStringList() {
    assertEquals("{x}", SGDataTextUtility.bindVariableNamesInBracket(Arrays.asList("x")));
  }

  @Test
  void createTitleStringPerDataType() {
    assertEquals(
        "Data (Scalar-XY Graph)",
        SGDataTextUtility.createTitleString("Data", SGDataTypeConstants.SXY_DATA));
    assertEquals(
        "Data (Pseudocolor Map)",
        SGDataTextUtility.createTitleString("Data", SGDataTypeConstants.SXYZ_DATA));
    assertEquals(
        "Data (Vector-XY Graph)",
        SGDataTextUtility.createTitleString("Data", SGDataTypeConstants.VXY_DATA));
    assertEquals("Data ()", SGDataTextUtility.createTitleString("Data", "UNKNOWN"));
  }

  @Test
  void encodeAndDecodeStringRoundTrip() {
    String str = "hello";
    assertEquals(str, SGDataTextUtility.decodeString(SGDataTextUtility.encodeString(str)));
  }

  @Test
  void getTextValueQuotesInput() {
    assertEquals("\"abc\"", SGDataTextUtility.getTextValue("abc"));
  }

  @Test
  void getNetCDFValidNameSanitizes() {
    assertNull(SGDataTextUtility.getNetCDFValidName(null));
    assertNull(SGDataTextUtility.getNetCDFValidName(""));
    assertEquals("sg_123", SGDataTextUtility.getNetCDFValidName("123"));
    assertEquals("a_b", SGDataTextUtility.getNetCDFValidName("a-b"));
    assertEquals("sg__x_y", SGDataTextUtility.getNetCDFValidName("-x-y"));
    assertEquals("a_b1", SGDataTextUtility.getNetCDFValidName("a_b1"));
  }

  @Test
  void isAcceptableCharHDF5WindChecksPrintableAsciiRange() {
    assertTrue(SGDataMiscUtility.isAcceptableCharHDF5Wind('A'));
    assertTrue(SGDataMiscUtility.isAcceptableCharHDF5Wind('~'));
    assertFalse(SGDataMiscUtility.isAcceptableCharHDF5Wind('\t'));
  }

  @Test
  void hasValidHDF5CharacterForWinChecksAllCharacters() {
    assertTrue(SGDataMiscUtility.hasValidHDF5CharacterForWin("ABC"));
    assertFalse(SGDataMiscUtility.hasValidHDF5CharacterForWin("A\nB"));
  }

  @Test
  void isEqualColumnTypeIsCaseInsensitiveAndIgnoresSeparators() {
    assertTrue(SGDataDataTypeUtility.isEqualColumnType("x_value", "X VALUE"));
    assertFalse(SGDataDataTypeUtility.isEqualColumnType("x_value", "y_value"));
  }

  @Test
  void columnTypeStartsWithMatchesPrefix() {
    assertTrue(SGDataDataTypeUtility.columnTypeStartsWith("LOWER_ERROR_VALUE", "lower"));
    assertFalse(SGDataDataTypeUtility.columnTypeStartsWith("Y_VALUE", "lower"));
  }

  @Test
  void getDimensionStringJoinsNamesWithSpaces() {
    assertEquals("x y z", SGDataTextUtility.getDimensionString(new String[] {"x", "y", "z"}));
  }

  @Test
  void isSameNetCDFGroupComparesNonLeafComponents() {
    assertTrue(SGDataTextUtility.isSameNetCDFGroup("/g1/a", "/g1/b"));
    assertFalse(SGDataTextUtility.isSameNetCDFGroup("/g1/a", "/g2/b"));
    assertTrue(SGDataTextUtility.isSameNetCDFGroup("a", "b"));
  }

  @Test
  void appendGroupNameConcatenatesGroupAndName() {
    assertEquals("group/name", SGDataTextUtility.appendGroupName("name", "group"));
  }

  @Test
  void isPolarReadsPolarFlag() {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    assertTrue(SGDataMiscUtility.isPolar(infoMap));
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertFalse(SGDataMiscUtility.isPolar(infoMap));
  }

  @Test
  void isPolarThrowsWhenFlagIsMissing() {
    assertThrows(Error.class, () -> SGDataMiscUtility.isPolar(new HashMap<String, Object>()));
  }

  @Test
  void isEmptyOrRepeatedColumnTitleMarksEmptyAndDuplicates() {
    SGDataColumnInfo[] cols = {
      new TestColumnInfo("x", "NUMBER"),
      new TestColumnInfo("x", "NUMBER"),
      new TestColumnInfo("", "NUMBER")
    };
    boolean[] flags = SGDataMiscUtility.isEmptyOrRepeatedColumnTitle(cols);
    assertArrayEquals(new boolean[] {true, true, true}, flags);
  }

  @Test
  void isEmptyOrRepeatedColumnTitleMarksUniqueTitlesFalse() {
    SGDataColumnInfo[] cols = {
      new TestColumnInfo("x", "NUMBER"), new TestColumnInfo("y", "NUMBER")
    };
    assertArrayEquals(
        new boolean[] {false, false}, SGDataMiscUtility.isEmptyOrRepeatedColumnTitle(cols));
  }

  @Test
  void isValidPickUpValueRejectsNullAndMinusOne() {
    assertFalse(SGDataColumnInfoUtility.isValidPickUpValue(null));
    assertFalse(SGDataColumnInfoUtility.isValidPickUpValue(-1));
    assertTrue(SGDataColumnInfoUtility.isValidPickUpValue(2));
  }

  @Test
  void isValidTimeValueRejectsNullAndMinusOne() {
    assertFalse(SGDataColumnInfoUtility.isValidTimeValue(null));
    assertFalse(SGDataColumnInfoUtility.isValidTimeValue(-1));
    assertTrue(SGDataColumnInfoUtility.isValidTimeValue(2));
  }

  @Test
  void isValidDimensionIndexRejectsNullAndMinusOne() {
    assertFalse(SGDataDataTypeUtility.isValidDimensionIndex(null));
    assertFalse(SGDataDataTypeUtility.isValidDimensionIndex(-1));
    assertTrue(SGDataDataTypeUtility.isValidDimensionIndex(0));
  }

  @Test
  void createDefaultStepSeriesWithSmallLengthUsesStepOne() {
    assertArrayEquals(
        new int[] {0, 1, 2, 3}, SGDataStrideUtility.createDefaultStepSeries(4).getNumbers());
  }

  @Test
  void createDefaultStepSeriesWithLargeLengthUsesStepOfLengthOverFour() {
    assertArrayEquals(
        new int[] {0, 2, 4, 6}, SGDataStrideUtility.createDefaultStepSeries(8).getNumbers());
  }

  /** Minimal concrete subclass for testing column-info based helpers. */
  private static class TestColumnInfo extends SGDataColumnInfo {

    TestColumnInfo(String title, String valueType) {
      super(title, valueType);
    }
  }
}
