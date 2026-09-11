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

/** Unit tests for the pure helpers of {@link SGDataUtility}. */
class SGDataUtilityTest {

  @Test
  void appendColumnNoAppendsOneBasedIndex() {
    assertEquals("SXY for No.1", SGDataUtility.appendColumnNo("SXY", 0));
    assertEquals("SXY for No.3", SGDataUtility.appendColumnNo("SXY", 2));
  }

  @Test
  void appendColumnTitleAppendsVariableName() {
    assertEquals("SXY for x", SGDataUtility.appendColumnTitle("SXY", "x"));
  }

  @Test
  void appendColumnNoOrTitleChoosesByFlag() {
    assertEquals("SXY for No.2", SGDataUtility.appendColumnNoOrTitle("SXY", 1, true, "ignored"));
    assertEquals("SXY for title", SGDataUtility.appendColumnNoOrTitle("SXY", 0, false, "title"));
  }

  @Test
  void removeHeaderTitleExtractsTitle() {
    assertEquals("x", SGDataUtility.removeHeaderTitle("SXY for x"));
    assertNull(SGDataUtility.removeHeaderTitle("SXY"));
  }

  @Test
  void removeHeaderNoExtractsNumber() {
    assertEquals("3", SGDataUtility.removeHeaderNo("SXY for No.3"));
    assertNull(SGDataUtility.removeHeaderNo("SXY for x"));
  }

  @Test
  void getAppendedColumnIndexReturnsZeroBasedIndex() {
    assertEquals(Integer.valueOf(2), SGDataUtility.getAppendedColumnIndex("SXY for No.3"));
    assertNull(SGDataUtility.getAppendedColumnIndex("SXY for x"));
  }

  @Test
  void isNetCDFDataRecognizesNetCDFTypes() {
    assertTrue(SGDataUtility.isNetCDFData(SGDataTypeConstants.SXY_NETCDF_DATA));
    assertFalse(SGDataUtility.isNetCDFData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isHDF5DataRecognizesHDF5Types() {
    assertTrue(SGDataUtility.isHDF5Data(SGDataTypeConstants.SXY_HDF5_DATA));
    assertFalse(SGDataUtility.isHDF5Data(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isSXYTypeDataRecognizesSingleAndMultipleTypes() {
    assertTrue(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
    assertFalse(SGDataUtility.isSXYTypeData(SGDataTypeConstants.VXY_DATA));
  }

  @Test
  void isVXYTypeDataRecognizesVectorTypes() {
    assertTrue(SGDataUtility.isVXYTypeData(SGDataTypeConstants.VXY_DATA));
    assertFalse(SGDataUtility.isVXYTypeData(SGDataTypeConstants.SXYZ_DATA));
  }

  @Test
  void isSXYZTypeDataRecognizesScalarTypes() {
    assertTrue(SGDataUtility.isSXYZTypeData(SGDataTypeConstants.SXYZ_DATA));
    assertFalse(SGDataUtility.isSXYZTypeData(SGDataTypeConstants.VXY_DATA));
  }

  @Test
  void isMultipleDataRecognizesMultipleTypes() {
    assertTrue(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
    assertFalse(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void predicatesRejectNullInput() {
    assertThrows(IllegalArgumentException.class, () -> SGDataUtility.isNetCDFData((String) null));
    assertThrows(IllegalArgumentException.class, () -> SGDataUtility.isHDF5Data((String) null));
    assertThrows(IllegalArgumentException.class, () -> SGDataUtility.isSXYTypeData((String) null));
  }

  @Test
  void getMinValueIgnoresNonFiniteValues() {
    assertEquals(2.0, SGDataUtility.getMinValue(new double[] {Double.NaN, 5.0, 2.0}), 0.0);
    assertEquals(
        Double.NaN,
        SGDataUtility.getMinValue(new double[] {Double.NaN, Double.POSITIVE_INFINITY}),
        0.0);
  }

  @Test
  void getMaxValueIgnoresNonFiniteValues() {
    assertEquals(5.0, SGDataUtility.getMaxValue(new double[] {5.0, Double.NaN, 2.0}), 0.0);
    assertEquals(Double.NaN, SGDataUtility.getMaxValue(new double[] {}), 0.0);
  }

  @Test
  void getBoundsReturnsMinMaxRange() {
    SGValueRange range = SGDataUtility.getBounds(new double[] {3.0, -1.0, 2.0});
    assertEquals(-1.0, range.getMinValue(), 0.0);
    assertEquals(3.0, range.getMaxValue(), 0.0);
  }

  @Test
  void appendColumnTypeAppendsTitleForNetCDFOrMDArrayData() {
    SGDataColumnInfo[] colInfo = {new TestColumnInfo("x", "NUMBER")};
    assertEquals("SXY for x", SGDataUtility.appendColumnType("SXY", 0, true, colInfo, false));
  }

  @Test
  void appendColumnTypeAppendsNumberOrTitleForOtherData() {
    SGDataColumnInfo[] colInfo = {new TestColumnInfo("x", "NUMBER")};
    assertEquals("SXY for No.1", SGDataUtility.appendColumnType("SXY", 0, false, colInfo, true));
    assertEquals("SXY for x", SGDataUtility.appendColumnType("SXY", 0, false, colInfo, false));
  }

  @Test
  void getColumnIndexOfAppendedColumnTitleFindsByTitle() {
    SGDataColumnInfo[] colInfo = {
      new TestColumnInfo("x", "NUMBER"), new TestColumnInfo("y", "NUMBER")
    };
    assertEquals(
        Integer.valueOf(1),
        SGDataUtility.getColumnIndexOfAppendedColumnTitle("SXY for y", colInfo));
    assertNull(SGDataUtility.getColumnIndexOfAppendedColumnTitle("SXY for z", colInfo));
    assertNull(SGDataUtility.getColumnIndexOfAppendedColumnTitle("SXY", colInfo));
  }

  @Test
  void getAppendedColumnIndexWithTitlesFindsByTitle() {
    String[] titles = {"x", "y"};
    assertEquals(Integer.valueOf(1), SGDataUtility.getAppendedColumnIndex("SXY for y", titles));
    assertNull(SGDataUtility.getAppendedColumnIndex("SXY for z", titles));
  }

  @Test
  void getColumnIndexOfAppendedColumnTypeUsesTitleForNetCDF() {
    SGDataColumnInfo[] colInfo = {new TestColumnInfo("x", "NUMBER")};
    assertEquals(
        Integer.valueOf(0),
        SGDataUtility.getColumnIndexOfAppendedColumnType(
            "SXY for x", SGDataTypeConstants.SXY_NETCDF_DATA, colInfo));
    assertEquals(
        Integer.valueOf(0),
        SGDataUtility.getColumnIndexOfAppendedColumnType(
            "SXY for No.1", SGDataTypeConstants.SXY_DATA, colInfo));
  }

  @Test
  void getMinimumNumberColumnsReturnsRequiredColumnCount() {
    assertEquals(1, SGDataUtility.getMinimumNumberColumns(SGDataTypeConstants.SXY_DATA));
    assertEquals(4, SGDataUtility.getMinimumNumberColumns(SGDataTypeConstants.VXY_DATA));
    assertEquals(3, SGDataUtility.getMinimumNumberColumns(SGDataTypeConstants.SXYZ_DATA));
    assertEquals(-1, SGDataUtility.getMinimumNumberColumns("UNKNOWN"));
  }

  @Test
  void isSXYTypeSingleDataRecognizesSingleTypes() {
    assertTrue(SGDataUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_NETCDF_DATA));
    assertFalse(SGDataUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
  }

  @Test
  void isSXYTypeMultipleDataRecognizesMultipleTypes() {
    assertTrue(SGDataUtility.isSXYTypeMultipleData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
    assertTrue(SGDataUtility.isSXYTypeMultipleData(SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA));
  }

  @Test
  void isSDArrayDataRecognizesSingleDimensionalTypes() {
    assertTrue(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXY_DATA));
    assertFalse(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXY_NETCDF_DATA));
  }

  @Test
  void isMDArrayDataRecognizesHDF5MatlabAndVirtualTypes() {
    assertTrue(SGDataUtility.isMDArrayData(SGDataTypeConstants.SXY_HDF5_DATA));
    assertTrue(SGDataUtility.isMDArrayData(SGDataTypeConstants.SXY_MATLAB_DATA));
    assertTrue(SGDataUtility.isMDArrayData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA));
    assertFalse(SGDataUtility.isMDArrayData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isMATLABDataRecognizesMatlabTypes() {
    assertTrue(SGDataUtility.isMATLABData(SGDataTypeConstants.SXY_MATLAB_DATA));
    assertFalse(SGDataUtility.isMATLABData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isVirtualMDArrayDataRecognizesVirtualTypes() {
    assertTrue(SGDataUtility.isVirtualMDArrayData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA));
    assertFalse(SGDataUtility.isVirtualMDArrayData(SGDataTypeConstants.SXY_HDF5_DATA));
  }

  @Test
  void isHDF5FileDataRecognizesHDF5AndVirtualTypes() {
    assertTrue(SGDataUtility.isHDF5FileData(SGDataTypeConstants.SXY_HDF5_DATA));
    assertTrue(SGDataUtility.isHDF5FileData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA));
    assertFalse(SGDataUtility.isHDF5FileData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isNetCDFDimensionDataRecognizesDimensionTypes() {
    assertTrue(
        SGDataUtility.isNetCDFDimensionData(
            SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA));
    assertFalse(SGDataUtility.isNetCDFDimensionData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isValidDataAcceptsKnownDataTypes() {
    assertTrue(SGDataUtility.isValidData(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataUtility.isValidData(SGDataTypeConstants.SXY_NETCDF_DATA));
    assertFalse(SGDataUtility.isValidData("UNKNOWN"));
  }

  @Test
  void updateInfoMapStoresColumnInfoCopy() {
    SGDataColumnInfo[] colInfo = {new TestColumnInfo("x", "NUMBER")};
    Map<String, Object> map = new HashMap<String, Object>();
    Map<String, Object> updated =
        SGDataUtility.updateInfoMap(SGDataTypeConstants.SXY_DATA, colInfo, map);
    Object stored = updated.get(SGIDataInformationKeyConstants.KEY_COLUMN_INFO);
    assertTrue(stored instanceof SGDataColumnInfo[]);
    assertArrayEquals(colInfo, (SGDataColumnInfo[]) stored);
  }

  @Test
  void hasTickLabelsRecognizesTickLabelTypes() {
    assertTrue(SGDataUtility.hasTickLabels(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataUtility.hasTickLabels(SGDataTypeConstants.SXY_DATE_DATA));
    assertFalse(SGDataUtility.hasTickLabels(SGDataTypeConstants.SXY_MULTIPLE_DATA));
  }

  @Test
  void getMinValueOfRangeListReturnsSmallestValidMin() {
    List<SGValueRange> list = Arrays.asList(new SGValueRange(1.0, 2.0), new SGValueRange(5.0, 8.0));
    assertEquals(1.0, SGDataUtility.getMinValue(list), 0.0);
  }

  @Test
  void getMinValueOfRangeListIgnoresInvalidRanges() {
    List<SGValueRange> list =
        Arrays.asList(new SGValueRange(Double.NaN, 2.0), new SGValueRange(3.0, 4.0));
    assertEquals(3.0, SGDataUtility.getMinValue(list), 0.0);
  }

  @Test
  void getMinValueOfEmptyRangeListReturnsNull() {
    assertNull(SGDataUtility.getMinValue(Collections.emptyList()));
  }

  @Test
  void getMaxValueOfRangeListReturnsLargestValidMax() {
    List<SGValueRange> list = Arrays.asList(new SGValueRange(1.0, 2.0), new SGValueRange(5.0, 8.0));
    assertEquals(8.0, SGDataUtility.getMaxValue(list), 0.0);
  }

  @Test
  void findColumnWithNameMatchesCaseInsensitively() {
    SGDataColumnInfo[] cols = {
      new TestColumnInfo("x", "NUMBER"), new TestColumnInfo("y", "NUMBER")
    };
    assertSame(cols[1], SGDataUtility.findColumnWithName(cols, "Y"));
    assertNull(SGDataUtility.findColumnWithName(cols, "z"));
  }

  @Test
  void findColumnsWithColumnTypeMatchesIgnoreCase() {
    SGDataColumnInfo[] cols = {
      new TestColumnInfo("x", "NUMBER"), new TestColumnInfo("y", "NUMBER")
    };
    cols[0].setColumnType("X_VALUE");
    cols[1].setColumnType("Y_VALUE");
    List<SGDataColumnInfo> result = SGDataUtility.findColumnsWithColumnType(cols, "x_value");
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
        SGDataUtility.findColumnsWithColumnTypeStartsWith(cols, "lower");
    assertEquals(1, result.size());
    assertSame(cols[0], result.get(0));
  }

  @Test
  void findColumnsWithValueTypeMatchesIgnoreCase() {
    SGDataColumnInfo[] cols = {new TestColumnInfo("x", "NUMBER"), new TestColumnInfo("y", "TEXT")};
    List<SGDataColumnInfo> result = SGDataUtility.findColumnsWithValueType(cols, "number");
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
    assertTrue(SGDataUtility.hasEqualColumnType(a, b));
    assertFalse(SGDataUtility.hasEqualColumnType(a, c));
  }

  @Test
  void hasEqualColumnTypeRejectsNullInput() {
    assertThrows(
        IllegalArgumentException.class, () -> SGDataUtility.hasEqualColumnType(null, null));
  }

  @Test
  void hasEqualColumnTypeRejectsDifferentLengths() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SGDataUtility.hasEqualColumnType(new SGDataColumnInfo[1], new SGDataColumnInfo[2]));
  }

  @Test
  void hasEqualInputComparesClassAndColumnType() {
    SGDataColumnInfo[] a = {new TestColumnInfo("x", "NUMBER")};
    a[0].setColumnType("X_VALUE");
    SGDataColumnInfo[] b = {new TestColumnInfo("x", "NUMBER")};
    b[0].setColumnType("X_VALUE");
    SGDataColumnInfo[] c = {new TestColumnInfo("x", "NUMBER")};
    c[0].setColumnType("Y_VALUE");
    assertTrue(SGDataUtility.hasEqualInput(a, b));
    assertFalse(SGDataUtility.hasEqualInput(a, c));
  }

  @Test
  void hasEqualInputRejectsNullInput() {
    assertThrows(IllegalArgumentException.class, () -> SGDataUtility.hasEqualInput(null, null));
  }

  @Test
  void getCanonicalColumnTypesMapsAliasesToCanonicalNames() {
    String[] result = SGDataUtility.getCanonicalColumnTypes(new String[] {"x", "unknown"});
    assertEquals("X", result[0]);
    assertEquals("unknown", result[1]);
  }

  @Test
  void bindVariableNamesInBracketWithStringArray() {
    assertEquals("{a,b,c}", SGDataUtility.bindVariableNamesInBracket(new String[] {"a", "b", "c"}));
  }

  @Test
  void bindVariableNamesInBracketWithStringList() {
    assertEquals("{x}", SGDataUtility.bindVariableNamesInBracket(Arrays.asList("x")));
  }

  @Test
  void createTitleStringPerDataType() {
    assertEquals(
        "Data (Scalar-XY Graph)",
        SGDataUtility.createTitleString("Data", SGDataTypeConstants.SXY_DATA));
    assertEquals(
        "Data (Pseudocolor Map)",
        SGDataUtility.createTitleString("Data", SGDataTypeConstants.SXYZ_DATA));
    assertEquals(
        "Data (Vector-XY Graph)",
        SGDataUtility.createTitleString("Data", SGDataTypeConstants.VXY_DATA));
    assertEquals("Data ()", SGDataUtility.createTitleString("Data", "UNKNOWN"));
  }

  @Test
  void encodeAndDecodeStringRoundTrip() {
    String str = "hello";
    assertEquals(str, SGDataUtility.decodeString(SGDataUtility.encodeString(str)));
  }

  @Test
  void getTextValueQuotesInput() {
    assertEquals("\"abc\"", SGDataUtility.getTextValue("abc"));
  }

  @Test
  void getNetCDFValidNameSanitizes() {
    assertNull(SGDataUtility.getNetCDFValidName(null));
    assertNull(SGDataUtility.getNetCDFValidName(""));
    assertEquals("sg_123", SGDataUtility.getNetCDFValidName("123"));
    assertEquals("a_b", SGDataUtility.getNetCDFValidName("a-b"));
    assertEquals("sg__x_y", SGDataUtility.getNetCDFValidName("-x-y"));
    assertEquals("a_b1", SGDataUtility.getNetCDFValidName("a_b1"));
  }

  @Test
  void isAcceptableCharHDF5WindChecksPrintableAsciiRange() {
    assertTrue(SGDataUtility.isAcceptableCharHDF5Wind('A'));
    assertTrue(SGDataUtility.isAcceptableCharHDF5Wind('~'));
    assertFalse(SGDataUtility.isAcceptableCharHDF5Wind('\t'));
  }

  @Test
  void hasValidHDF5CharacterForWinChecksAllCharacters() {
    assertTrue(SGDataUtility.hasValidHDF5CharacterForWin("ABC"));
    assertFalse(SGDataUtility.hasValidHDF5CharacterForWin("A\nB"));
  }

  @Test
  void isEqualColumnTypeIsCaseInsensitiveAndIgnoresSeparators() {
    assertTrue(SGDataUtility.isEqualColumnType("x_value", "X VALUE"));
    assertFalse(SGDataUtility.isEqualColumnType("x_value", "y_value"));
  }

  @Test
  void columnTypeStartsWithMatchesPrefix() {
    assertTrue(SGDataUtility.columnTypeStartsWith("LOWER_ERROR_VALUE", "lower"));
    assertFalse(SGDataUtility.columnTypeStartsWith("Y_VALUE", "lower"));
  }

  @Test
  void getDimensionStringJoinsNamesWithSpaces() {
    assertEquals("x y z", SGDataUtility.getDimensionString(new String[] {"x", "y", "z"}));
  }

  @Test
  void isSameNetCDFGroupComparesNonLeafComponents() {
    assertTrue(SGDataUtility.isSameNetCDFGroup("/g1/a", "/g1/b"));
    assertFalse(SGDataUtility.isSameNetCDFGroup("/g1/a", "/g2/b"));
    assertTrue(SGDataUtility.isSameNetCDFGroup("a", "b"));
  }

  @Test
  void appendGroupNameConcatenatesGroupAndName() {
    assertEquals("group/name", SGDataUtility.appendGroupName("name", "group"));
  }

  @Test
  void isPolarReadsPolarFlag() {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    assertTrue(SGDataUtility.isPolar(infoMap));
    infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertFalse(SGDataUtility.isPolar(infoMap));
  }

  @Test
  void isPolarThrowsWhenFlagIsMissing() {
    assertThrows(Error.class, () -> SGDataUtility.isPolar(new HashMap<String, Object>()));
  }

  @Test
  void isEmptyOrRepeatedColumnTitleMarksEmptyAndDuplicates() {
    SGDataColumnInfo[] cols = {
      new TestColumnInfo("x", "NUMBER"),
      new TestColumnInfo("x", "NUMBER"),
      new TestColumnInfo("", "NUMBER")
    };
    boolean[] flags = SGDataUtility.isEmptyOrRepeatedColumnTitle(cols);
    assertArrayEquals(new boolean[] {true, true, true}, flags);
  }

  @Test
  void isEmptyOrRepeatedColumnTitleMarksUniqueTitlesFalse() {
    SGDataColumnInfo[] cols = {
      new TestColumnInfo("x", "NUMBER"), new TestColumnInfo("y", "NUMBER")
    };
    assertArrayEquals(
        new boolean[] {false, false}, SGDataUtility.isEmptyOrRepeatedColumnTitle(cols));
  }

  @Test
  void isValidPickUpValueRejectsNullAndMinusOne() {
    assertFalse(SGDataUtility.isValidPickUpValue(null));
    assertFalse(SGDataUtility.isValidPickUpValue(-1));
    assertTrue(SGDataUtility.isValidPickUpValue(2));
  }

  @Test
  void isValidTimeValueRejectsNullAndMinusOne() {
    assertFalse(SGDataUtility.isValidTimeValue(null));
    assertFalse(SGDataUtility.isValidTimeValue(-1));
    assertTrue(SGDataUtility.isValidTimeValue(2));
  }

  @Test
  void isValidDimensionIndexRejectsNullAndMinusOne() {
    assertFalse(SGDataUtility.isValidDimensionIndex(null));
    assertFalse(SGDataUtility.isValidDimensionIndex(-1));
    assertTrue(SGDataUtility.isValidDimensionIndex(0));
  }

  @Test
  void createDefaultStepSeriesWithSmallLengthUsesStepOne() {
    assertArrayEquals(
        new int[] {0, 1, 2, 3}, SGDataUtility.createDefaultStepSeries(4).getNumbers());
  }

  @Test
  void createDefaultStepSeriesWithLargeLengthUsesStepOfLengthOverFour() {
    assertArrayEquals(
        new int[] {0, 2, 4, 6}, SGDataUtility.createDefaultStepSeries(8).getNumbers());
  }

  /** Minimal concrete subclass for testing column-info based helpers. */
  private static class TestColumnInfo extends SGDataColumnInfo {

    TestColumnInfo(String title, String valueType) {
      super(title, valueType);
    }
  }
}
