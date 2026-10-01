package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/** Unit tests for {@link SGDefaultColumnTypeMDArrayUtility}. */
class SGDefaultColumnTypeMDArrayUtilityTest {

  private static SGMDArrayVariable[] vars(final String... names) {
    SGMDArrayVariable[] vars = new SGMDArrayVariable[names.length];
    for (int ii = 0; ii < names.length; ii++) {
      vars[ii] = mock(SGMDArrayVariable.class);
      when(vars[ii].getName()).thenReturn(names[ii]);
    }
    return vars;
  }

  private static SGMDArrayDataColumnInfo[] cols(final String... names) {
    SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[names.length];
    for (int ii = 0; ii < names.length; ii++) {
      cols[ii] = SGTestMDArrayColumns.column(names[ii], new int[] {4}, "");
    }
    return cols;
  }

  private static SGMDArrayDataColumnInfo[] cols(final int[][] dims, final String... names) {
    SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[names.length];
    for (int ii = 0; ii < names.length; ii++) {
      cols[ii] =
          new SGMDArrayDataColumnInfo(
              SGTestMDArrayColumns.variable(names[ii], dims[ii], new int[dims[ii].length]),
              names[ii],
              SGDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    }
    return cols;
  }

  private static List<SGDataColumnInfo> list(final SGMDArrayDataColumnInfo... cols) {
    List<SGDataColumnInfo> list = new ArrayList<SGDataColumnInfo>();
    for (SGMDArrayDataColumnInfo col : cols) {
      list.add(col);
    }
    return list;
  }

  private static NamedNodeMap nodeMap(final String... keyValues) {
    NamedNodeMap nodeMap = mock(NamedNodeMap.class);
    for (int ii = 0; ii + 1 < keyValues.length; ii += 2) {
      Node node = mock(Node.class);
      when(node.getNodeValue()).thenReturn(keyValues[ii + 1]);
      when(nodeMap.getNamedItem(keyValues[ii])).thenReturn(node);
    }
    return nodeMap;
  }

  private static Map<String, Object> infoMap(final Object... keyValues) {
    Map<String, Object> map = new HashMap<String, Object>();
    for (int ii = 0; ii + 1 < keyValues.length; ii += 2) {
      map.put(keyValues[ii].toString(), keyValues[ii + 1]);
    }
    return map;
  }

  // -- the origin map -------------------------------------------------------------

  @Test
  void theOriginMapIsParsedFromAParenthesizedString() {
    NamedNodeMap nodeMap = nodeMap(SGDataPropertyKeyConstants.KEY_ORIGIN_MAP, "x=(0,0),y=(1,2)");
    Map<String, int[]> map = SGDefaultColumnTypeMDArrayUtility.getMDArrayDataOriginMap(nodeMap);
    assertEquals(2, map.size());
    assertArrayEquals(new int[] {0, 0}, map.get("x"));
    assertArrayEquals(new int[] {1, 2}, map.get("y"));
  }

  @Test
  void theOriginMapIsEmptyForNullOrMissingNodeMaps() {
    assertTrue(SGDefaultColumnTypeMDArrayUtility.getMDArrayDataOriginMap(null).isEmpty());
    assertTrue(SGDefaultColumnTypeMDArrayUtility.getMDArrayDataOriginMap(nodeMap()).isEmpty());
  }

  @Test
  void theOriginMapIsMissingForUnparseableEntries() {
    NamedNodeMap nodeMap = nodeMap(SGDataPropertyKeyConstants.KEY_ORIGIN_MAP, "x=0");
    assertNull(SGDefaultColumnTypeMDArrayUtility.getMDArrayDataOriginMap(nodeMap));
    NamedNodeMap nodeMap2 = nodeMap(SGDataPropertyKeyConstants.KEY_ORIGIN_MAP, "x=(a,b)");
    assertNull(SGDefaultColumnTypeMDArrayUtility.getMDArrayDataOriginMap(nodeMap2));
  }

  // -- the index pair ---------------------------------------------------------------

  @Test
  void theIndexPairReportsPartialEqualityAndFormatsItself() {
    SGDefaultColumnTypeMDArrayUtility.IndexPair pair =
        new SGDefaultColumnTypeMDArrayUtility.IndexPair();
    pair.index1 = 1;
    pair.index2 = 2;
    SGDefaultColumnTypeMDArrayUtility.IndexPair sameFirst =
        new SGDefaultColumnTypeMDArrayUtility.IndexPair();
    sameFirst.index1 = 1;
    sameFirst.index2 = 9;
    SGDefaultColumnTypeMDArrayUtility.IndexPair other =
        new SGDefaultColumnTypeMDArrayUtility.IndexPair();
    other.index1 = 3;
    other.index2 = 4;
    assertTrue(pair.partiallyEquals(sameFirst));
    assertFalse(pair.partiallyEquals(other));
    assertEquals("(1,2)", pair.toString());
  }

  // -- the SXY data type ------------------------------------------------------------

  @Test
  void theSXYMultipleDataAssignsTheMatchingDimensionToAllMatchingVariables() {
    SGMDArrayDataColumnInfo[] cols = cols(new int[][] {{4}, {4}}, "x", "y");
    Map<String, Object> infoMap =
        infoMap(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.TRUE);
    assertTrue(
        SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(infoMap, vars("x", "y"), 2, cols));
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, cols[0].getColumnType());
    assertEquals(0, cols[0].getGenericDimensionIndex().intValue());
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, cols[1].getColumnType());
    assertEquals(0, cols[1].getGenericDimensionIndex().intValue());
  }

  @Test
  void theSXYSingleDataAssignsTheLongestMatchingDimensionToTheFirstMatchingVariable() {
    SGMDArrayDataColumnInfo[] cols = cols(new int[][] {{5}, {4}}, "x", "y");
    Map<String, Object> infoMap =
        infoMap(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(infoMap, vars("x", "y"), 2, cols));
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, cols[0].getColumnType());
    assertEquals("", cols[1].getColumnType());
  }

  @Test
  void theSXYMultipleDataCapsTheNumberOfAssignedVariables() {
    SGMDArrayDataColumnInfo[] cols =
        cols(new int[][] {{4}, {4}, {4}, {4}, {4}, {4}}, "a", "b", "c", "d", "e", "f");
    Map<String, Object> infoMap =
        infoMap(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.TRUE);
    assertTrue(
        SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(
            infoMap, vars("a", "b", "c", "d", "e", "f"), 6, cols));
    for (int ii = 0; ii < 5; ii++) {
      assertEquals(SGDataColumnTypeConstants.Y_VALUE, cols[ii].getColumnType());
    }
    assertEquals("", cols[5].getColumnType());
  }

  @Test
  void theSXYDataFailsWithoutAMultipleFlag() {
    SGMDArrayDataColumnInfo[] cols1 = cols("x");
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(infoMap(), vars("x"), 1, cols1));
  }

  @Test
  void theSXYDataThrowsForVariablesWithoutDimensions() {
    // the dimension map is empty so extractDimensions returns null, which the
    // SXY entry point does not check before dereferencing it
    SGMDArrayDataColumnInfo[] cols2 = cols(new int[][] {{}, {}}, "x", "y");
    Map<String, Object> infoMap2 =
        infoMap(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.TRUE);
    assertThrows(
        NullPointerException.class,
        () ->
            SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(
                infoMap2, vars("x", "y"), 2, cols2));
  }

  @Test
  void theSXYPropertyDataAssignsXYAndErrorTypes() {
    SGMDArrayDataColumnInfo[] cols = cols(new int[][] {{5}, {5}, {5}, {5}}, "x", "y", "le", "ue");
    NamedNodeMap nodeMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_VALUE_NAMES, "x:0",
            SGDataPropertyKeyConstants.KEY_Y_VALUE_NAMES, "y:0",
            SGDataPropertyKeyConstants.KEY_LOWER_ERROR_VALUE_NAMES, "le:0",
            SGDataPropertyKeyConstants.KEY_UPPER_ERROR_VALUE_NAMES, "ue:0",
            SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_NAMES, "y:0");
    Map<String, Object> infoMap =
        infoMap(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(
            list(cols), infoMap, nodeMap, null, cols));
    assertEquals(SGDataColumnTypeConstants.X_VALUE, cols[0].getColumnType());
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, cols[1].getColumnType());
    assertEquals(
        SGDataColumnTypeConstants.LOWER_ERROR_VALUE + SGDataColumnTitleUtility.MID_COLUMN + "y:0",
        cols[2].getColumnType());
    assertEquals(
        SGDataColumnTypeConstants.UPPER_ERROR_VALUE + SGDataColumnTitleUtility.MID_COLUMN + "y:0",
        cols[3].getColumnType());
  }

  @Test
  void theSXYPropertyDataAssignsALowerUpperErrorType() {
    SGMDArrayDataColumnInfo[] cols = cols(new int[][] {{5}, {5}, {5}}, "x", "y", "e");
    NamedNodeMap nodeMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_VALUE_NAMES, "x:0",
            SGDataPropertyKeyConstants.KEY_Y_VALUE_NAMES, "y:0",
            SGDataPropertyKeyConstants.KEY_LOWER_ERROR_VALUE_NAMES, "e:0",
            SGDataPropertyKeyConstants.KEY_UPPER_ERROR_VALUE_NAMES, "e:0",
            SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_NAMES, "y:0");
    Map<String, Object> infoMap =
        infoMap(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(
            list(cols), infoMap, nodeMap, null, cols));
    assertEquals(SGDataColumnTypeConstants.X_VALUE, cols[0].getColumnType());
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, cols[1].getColumnType());
    assertEquals(
        SGDataColumnTypeConstants.LOWER_UPPER_ERROR_VALUE
            + SGDataColumnTitleUtility.MID_COLUMN
            + "y:0",
        cols[2].getColumnType());
  }

  @Test
  void theSXYPropertyDataUsesThePickUpDimensionMapWhenPresent() {
    SGMDArrayDataColumnInfo[] cols = cols(new int[][] {{4}, {4}, {4}}, "x", "y", "e");
    Map<String, Integer> pickUpMap = new HashMap<String, Integer>();
    pickUpMap.put("y", 0);
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(new int[] {1});
    Map<String, Object> infoMap =
        infoMap(
            SGDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP,
            pickUpMap,
            SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES,
            indices);
    NamedNodeMap nodeMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x:0",
            SGDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "y:0",
            SGDataPropertyKeyConstants.KEY_LOWER_ERROR_VALUE_NAME, "e:0",
            SGDataPropertyKeyConstants.KEY_UPPER_ERROR_VALUE_NAME, "e:0",
            SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_NAME, "y");
    assertTrue(
        SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(
            list(cols), infoMap, nodeMap, null, cols));
    assertEquals(
        SGDataColumnTypeConstants.LOWER_UPPER_ERROR_VALUE
            + SGDataColumnTitleUtility.MID_COLUMN
            + "y",
        cols[2].getColumnType());
  }

  @Test
  void theSXYPropertyDataFailsOnInvalidAssignments() {
    SGMDArrayDataColumnInfo[] noNames = cols("x", "y");
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(
            list(noNames), infoMap(), nodeMap(), null, noNames));

    SGMDArrayDataColumnInfo[] multiXNames = cols("x", "y");
    NamedNodeMap multiXNamesMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_VALUE_NAMES, "{x:0,y:0}",
            SGDataPropertyKeyConstants.KEY_Y_VALUE_NAMES, "{y:0,x:0}");
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(
            list(multiXNames), infoMap(), multiXNamesMap, null, multiXNames));

    SGMDArrayDataColumnInfo[] multiXY = cols("a", "b", "c", "d");
    NamedNodeMap multiXYMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_VALUE_NAMES, "{a:0,b:0}",
            SGDataPropertyKeyConstants.KEY_Y_VALUE_NAMES, "{c:0,d:0}");
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(
            list(multiXY), infoMap(), multiXYMap, null, multiXY));

    SGMDArrayDataColumnInfo[] badDim = cols("x", "y");
    NamedNodeMap badDimMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x:9",
            SGDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "y:0");
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(
            list(badDim), infoMap(), badDimMap, null, badDim));

    SGMDArrayDataColumnInfo[] mismatchedErrors = cols("x", "y", "le", "ue", "e2");
    NamedNodeMap mismatchedErrorMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_VALUE_NAMES, "x:0",
            SGDataPropertyKeyConstants.KEY_Y_VALUE_NAMES, "y:0",
            SGDataPropertyKeyConstants.KEY_LOWER_ERROR_VALUE_NAMES, "{le:0,ue:0}",
            SGDataPropertyKeyConstants.KEY_UPPER_ERROR_VALUE_NAMES, "le:0",
            SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_NAMES, "y:0");
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(
            list(mismatchedErrors), infoMap(), mismatchedErrorMap, null, mismatchedErrors));

    SGMDArrayDataColumnInfo[] missingErrorHolder = cols("x", "y", "le", "ue");
    NamedNodeMap missingErrorHolderMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x:0",
            SGDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "y:0",
            SGDataPropertyKeyConstants.KEY_LOWER_ERROR_VALUE_NAMES, "le:0",
            SGDataPropertyKeyConstants.KEY_UPPER_ERROR_VALUE_NAMES, "ue:0",
            SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_NAMES, "e2:0");
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData(
            list(missingErrorHolder), infoMap(), missingErrorHolderMap, null, missingErrorHolder));
  }

  // -- the SXYZ data type -----------------------------------------------------------

  @Test
  void theSXYZGridPlotDataAssignsXYZTypesWithMatchingDimensions() {
    SGMDArrayDataColumnInfo[] cols = cols(new int[][] {{4}, {4}, {4, 4}}, "x", "y", "z");
    NamedNodeMap nodeMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x:0",
            SGDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "y:0",
            SGDataPropertyKeyConstants.KEY_Z_VALUE_NAME, "z:0:1");
    Map<String, Object> infoMap =
        infoMap(
            SGDataInformationKeyConstants.KEY_DATA_TYPE,
            SGDataTypeConstants.SXYZ_DATA,
            SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG,
            Boolean.TRUE);
    assertTrue(
        SGDefaultColumnTypeMDArrayUtility.getForSXYZMDArrayData(
            list(cols), infoMap, nodeMap, null, cols));
    assertEquals(SGDataColumnTypeConstants.X_VALUE, cols[0].getColumnType());
    assertEquals(0, cols[0].getGenericDimensionIndex().intValue());
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, cols[1].getColumnType());
    assertEquals(SGDataColumnTypeConstants.Z_VALUE, cols[2].getColumnType());
    assertEquals(0, cols[2].getDimensionIndex(SGMDArrayConstants.KEY_SXYZ_X_DIMENSION).intValue());
    assertEquals(1, cols[2].getDimensionIndex(SGMDArrayConstants.KEY_SXYZ_Y_DIMENSION).intValue());
  }

  @Test
  void theSXYZIndexPlotDataAssignsTheZTypeOnly() {
    SGMDArrayDataColumnInfo[] cols = cols(new int[][] {{4}, {4}, {4, 4}}, "x", "y", "z");
    NamedNodeMap nodeMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x:0",
            SGDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "y:0",
            SGDataPropertyKeyConstants.KEY_Z_VALUE_NAME, "z:0");
    Map<String, Object> infoMap =
        infoMap(
            SGDataInformationKeyConstants.KEY_DATA_TYPE,
            SGDataTypeConstants.SXYZ_DATA,
            SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG,
            Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeMDArrayUtility.getForSXYZMDArrayData(
            list(cols), infoMap, nodeMap, null, cols));
    assertEquals(SGDataColumnTypeConstants.X_VALUE, cols[0].getColumnType());
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, cols[1].getColumnType());
    assertEquals(SGDataColumnTypeConstants.Z_VALUE, cols[2].getColumnType());
    assertNull(cols[2].getDimensionIndex(SGMDArrayConstants.KEY_SXYZ_X_DIMENSION));
  }

  @Test
  void theSXYZIndexPlotDataInfersXyzFromThreeMatchingVariables() {
    SGMDArrayDataColumnInfo[] cols = cols(new int[][] {{4}, {4}, {4}}, "x", "y", "z");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    assertTrue(
        SGDefaultColumnTypeMDArrayUtility.getForSXYZMDArrayData(
            infoMap, vars("x", "y", "z"), 3, cols));
    assertEquals(SGDataColumnTypeConstants.X_VALUE, cols[0].getColumnType());
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, cols[1].getColumnType());
    assertEquals(SGDataColumnTypeConstants.Z_VALUE, cols[2].getColumnType());
    assertEquals(Boolean.FALSE, infoMap.get(SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG));
  }

  @Test
  void theSXYZGridPlotDataInfersZFromTheFirstTwoDimensionalVariable() {
    SGMDArrayDataColumnInfo[] cols = cols(new int[][] {{4}, {4, 4}}, "x", "z");
    Map<String, Object> infoMap = new HashMap<String, Object>();
    assertTrue(
        SGDefaultColumnTypeMDArrayUtility.getForSXYZMDArrayData(infoMap, vars("x", "z"), 2, cols));
    assertEquals("", cols[0].getColumnType());
    assertEquals(SGDataColumnTypeConstants.Z_VALUE, cols[1].getColumnType());
    assertEquals(0, cols[1].getDimensionIndex(SGMDArrayConstants.KEY_SXYZ_X_DIMENSION).intValue());
    assertEquals(1, cols[1].getDimensionIndex(SGMDArrayConstants.KEY_SXYZ_Y_DIMENSION).intValue());
    assertEquals(Boolean.TRUE, infoMap.get(SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG));
  }

  @Test
  void theSXYZDataFailsOnInvalidAssignments() {
    SGMDArrayDataColumnInfo[] noType = cols(new int[][] {{4}, {4, 4}}, "x", "z");
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForSXYZMDArrayData(
            list(noType), infoMap(), nodeMap(), null, noType));

    SGMDArrayDataColumnInfo[] strideOnly = cols(new int[][] {{4}, {4, 4}}, "x", "z");
    NamedNodeMap strideNodeMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x:0",
            SGDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "y:0");
    Map<String, Object> strideInfoMap =
        infoMap(
            SGDataInformationKeyConstants.KEY_DATA_TYPE,
            SGDataTypeConstants.SXYZ_DATA,
            SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG,
            Boolean.FALSE);
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForSXYZMDArrayData(
            list(strideOnly), strideInfoMap, strideNodeMap, null, strideOnly));

    SGMDArrayDataColumnInfo[] mismatched = cols(new int[][] {{4}, {5}, {4, 4}}, "x", "y", "z");
    NamedNodeMap mismatchedNodeMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_VALUE_NAME, "x:0",
            SGDataPropertyKeyConstants.KEY_Y_VALUE_NAME, "y:0",
            SGDataPropertyKeyConstants.KEY_Z_VALUE_NAME, "z:0:1");
    Map<String, Object> gridInfoMap =
        infoMap(
            SGDataInformationKeyConstants.KEY_DATA_TYPE,
            SGDataTypeConstants.SXYZ_DATA,
            SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG,
            Boolean.TRUE);
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForSXYZMDArrayData(
            list(mismatched), gridInfoMap, mismatchedNodeMap, null, mismatched));

    SGMDArrayDataColumnInfo[] noThreeMatching = cols(new int[][] {{4}, {5}, {6}}, "x", "y", "z");
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForSXYZMDArrayData(
            infoMap(), vars("x", "y", "z"), 3, noThreeMatching));
  }

  // -- the VXY data type ------------------------------------------------------------

  @Test
  void theVXYGridPlotDataAssignsCoordinatesAndComponents() {
    SGMDArrayDataColumnInfo[] cols =
        cols(new int[][] {{4}, {4}, {4, 4}, {4, 4}}, "x", "y", "u", "v");
    NamedNodeMap nodeMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_COORDINATE_VARIABLE_NAME, "x:0",
            SGDataPropertyKeyConstants.KEY_Y_COORDINATE_VARIABLE_NAME, "y:0",
            SGDataPropertyKeyConstants.KEY_FIRST_COMPONENT_VARIABLE_NAME, "u:0:1",
            SGDataPropertyKeyConstants.KEY_SECOND_COMPONENT_VARIABLE_NAME, "v:0:1");
    Map<String, Object> infoMap =
        infoMap(
            SGDataInformationKeyConstants.KEY_DATA_TYPE,
            SGDataTypeConstants.VXY_DATA,
            SGDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG,
            Boolean.TRUE,
            SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED,
            Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeMDArrayUtility.getForVXYMDArrayData(
            list(cols), infoMap, nodeMap, null, cols));
    assertEquals(SGDataColumnTypeConstants.X_COORDINATE, cols[0].getColumnType());
    assertEquals(SGDataColumnTypeConstants.Y_COORDINATE, cols[1].getColumnType());
    assertEquals(SGDataColumnTypeConstants.X_COMPONENT, cols[2].getColumnType());
    assertEquals(SGDataColumnTypeConstants.Y_COMPONENT, cols[3].getColumnType());
    assertEquals(0, cols[2].getDimensionIndex(SGMDArrayConstants.KEY_VXY_X_DIMENSION).intValue());
    assertEquals(1, cols[2].getDimensionIndex(SGMDArrayConstants.KEY_VXY_Y_DIMENSION).intValue());
  }

  @Test
  void theVXYIndexPlotDataInfersComponentsFromSharedDimensions() {
    SGMDArrayDataColumnInfo[] cols = cols(new int[][] {{4, 4}, {4, 4}}, "u", "v");
    Map<String, Object> infoMap =
        infoMap(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    assertTrue(
        SGDefaultColumnTypeMDArrayUtility.getForVXYMDArrayData(infoMap, vars("u", "v"), 2, cols));
    assertEquals(SGDataColumnTypeConstants.X_COMPONENT, cols[0].getColumnType());
    assertEquals(SGDataColumnTypeConstants.Y_COMPONENT, cols[1].getColumnType());
    assertEquals(0, cols[0].getDimensionIndex(SGMDArrayConstants.KEY_VXY_X_DIMENSION).intValue());
    assertEquals(1, cols[0].getDimensionIndex(SGMDArrayConstants.KEY_VXY_Y_DIMENSION).intValue());
    assertEquals(0, cols[1].getDimensionIndex(SGMDArrayConstants.KEY_VXY_X_DIMENSION).intValue());
    assertEquals(1, cols[1].getDimensionIndex(SGMDArrayConstants.KEY_VXY_Y_DIMENSION).intValue());
    assertEquals(Boolean.TRUE, infoMap.get(SGDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG));
  }

  @Test
  void theVXYIndexPlotDataSupportsOverlappingSharedDimensions() {
    SGMDArrayDataColumnInfo[] cols = cols(new int[][] {{4, 4, 8}, {4, 8, 8}}, "u", "v");
    Map<String, Object> infoMap =
        infoMap(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    assertTrue(
        SGDefaultColumnTypeMDArrayUtility.getForVXYMDArrayData(infoMap, vars("u", "v"), 2, cols));
    assertEquals(SGDataColumnTypeConstants.MAGNITUDE, cols[0].getColumnType());
    assertEquals(SGDataColumnTypeConstants.ANGLE, cols[1].getColumnType());
    // the greedy pairing takes the first disjoint pair for x and the next one for y
    assertEquals(0, cols[0].getDimensionIndex(SGMDArrayConstants.KEY_VXY_X_DIMENSION).intValue());
    assertEquals(2, cols[0].getDimensionIndex(SGMDArrayConstants.KEY_VXY_Y_DIMENSION).intValue());
    assertEquals(0, cols[1].getDimensionIndex(SGMDArrayConstants.KEY_VXY_X_DIMENSION).intValue());
    assertEquals(1, cols[1].getDimensionIndex(SGMDArrayConstants.KEY_VXY_Y_DIMENSION).intValue());
  }

  @Test
  void theVXYDataFailsOnInvalidAssignments() {
    SGMDArrayDataColumnInfo[] noType = cols(new int[][] {{4}, {4}, {4, 4}}, "x", "y", "u");
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForVXYMDArrayData(
            list(noType), infoMap(), nodeMap(), null, noType));

    SGMDArrayDataColumnInfo[] noSecond = cols(new int[][] {{4}, {4}, {4, 4}}, "x", "y", "u");
    NamedNodeMap noSecondNodeMap =
        nodeMap(
            SGDataPropertyKeyConstants.KEY_X_COORDINATE_VARIABLE_NAME, "x:0",
            SGDataPropertyKeyConstants.KEY_Y_COORDINATE_VARIABLE_NAME, "y:0",
            SGDataPropertyKeyConstants.KEY_FIRST_COMPONENT_VARIABLE_NAME, "u:0:1");
    Map<String, Object> gridInfoMap =
        infoMap(
            SGDataInformationKeyConstants.KEY_DATA_TYPE,
            SGDataTypeConstants.VXY_DATA,
            SGDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG,
            Boolean.TRUE,
            SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED,
            Boolean.FALSE);
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForVXYMDArrayData(
            list(noSecond), gridInfoMap, noSecondNodeMap, null, noSecond));

    SGMDArrayDataColumnInfo[] noShared = cols(new int[][] {{4, 4}, {5, 5}}, "u", "v");
    assertFalse(
        SGDefaultColumnTypeMDArrayUtility.getForVXYMDArrayData(
            infoMap(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE),
            vars("u", "v"),
            2,
            noShared));
  }
}
