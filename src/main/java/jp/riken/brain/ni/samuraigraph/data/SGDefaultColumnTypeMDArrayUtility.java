package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeUtility.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import org.w3c.dom.NamedNodeMap;

/** Static helper for the MDArray default column types. */
public final class SGDefaultColumnTypeMDArrayUtility
    implements SGIDataColumnTypeConstants, SGIDataPropertyKeyConstants {

  private SGDefaultColumnTypeMDArrayUtility() {}

  private static List<MDArrayDimension> extractDimensions(
      TreeMap<Integer, List<MDArrayDimension>> varDimListMap, final int num) {
    List<MDArrayDimension> dimList = null;
    Iterator<Entry<Integer, List<MDArrayDimension>>> itr =
        varDimListMap.descendingMap().entrySet().iterator();
    while (itr.hasNext()) {
      Entry<Integer, List<MDArrayDimension>> entry = itr.next();
      List<MDArrayDimension> varDimList = entry.getValue();
      if (varDimList.size() >= num) {
        Map<Integer, MDArrayDimension> dimMap = new TreeMap<Integer, MDArrayDimension>();
        for (MDArrayDimension dim : varDimList) {
          MDArrayDimension cur = dimMap.get(dim.variableIndex);
          if (cur == null) {
            dimMap.put(dim.variableIndex, dim);
          }
        }
        if (dimMap.size() >= num) {
          dimList = new ArrayList<MDArrayDimension>(dimMap.values());
          break;
        }
      }
    }
    return dimList;
  }

  private static TreeMap<Integer, List<MDArrayDimension>> createDimensionListMap(
      SGDataColumnInfo[] columns) {
    TreeMap<Integer, List<MDArrayDimension>> varDimListMap =
        new TreeMap<Integer, List<MDArrayDimension>>();
    for (int ii = 0; ii < columns.length; ii++) {
      SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
      String valueType = mdInfo.getValueType();
      if (!SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER.equals(valueType)) {
        continue;
      }
      int[] dims = mdInfo.getDimensions();
      for (int jj = 0; jj < dims.length; jj++) {
        final int len = dims[jj];
        MDArrayDimension varDim = new MDArrayDimension(ii, jj);
        List<MDArrayDimension> varDimList = varDimListMap.get(len);
        if (varDimList == null) {
          varDimList = new ArrayList<MDArrayDimension>();
          varDimListMap.put(len, varDimList);
        }
        varDimList.add(varDim);
      }
    }
    return varDimListMap;
  }

  /**
   * Finds and returns the origin map for multidimensional data. If the origin map is not found,
   * returns an empty map.
   *
   * @param nodeMap a node map
   * @return the origin map
   */
  public static Map<String, int[]> getMDArrayDataOriginMap(NamedNodeMap nodeMap) {
    Map<String, int[]> map = new HashMap<String, int[]>();
    if (nodeMap != null) {
      String originMapStr = getString(nodeMap, SGIDataPropertyKeyConstants.KEY_ORIGIN_MAP);
      if (originMapStr != null) {
        char[] cArray = originMapStr.toCharArray();
        boolean skip = false;
        List<Integer> commaIndexList = new ArrayList<Integer>();
        for (int ii = 0; ii < cArray.length; ii++) {
          final char c = cArray[ii];
          if (c == '(') {
            skip = true;
          } else if (c == ')') {
            skip = false;
          } else if (c == ',') {
            if (!skip) {
              commaIndexList.add(ii);
            }
          }
        }
        List<String> originStrList = new ArrayList<String>();
        int beginIndex = 0;
        for (int ii = 0; ii <= commaIndexList.size(); ii++) {
          final int endIndex;
          if (ii < commaIndexList.size()) {
            endIndex = commaIndexList.get(ii);
          } else {
            endIndex = originMapStr.length();
          }
          String str = originMapStr.substring(beginIndex, endIndex);
          originStrList.add(str);
          beginIndex = endIndex + 1;
          if (beginIndex >= originMapStr.length()) {
            break;
          }
        }
        String[][] originStrArray = new String[originStrList.size()][];
        for (int ii = 0; ii < originStrArray.length; ii++) {
          String originStr = originStrList.get(ii);
          String[] keyValueStrArray = originStr.split("=");
          if (keyValueStrArray == null || keyValueStrArray.length != 2) {
            return null;
          }
          String key = keyValueStrArray[0];
          String value = keyValueStrArray[1];
          int[] numArray = SGUtilityText.getIntegerArray(value);
          if (numArray == null) {
            return null;
          }
          map.put(key, numArray);
        }
      }
    }
    return map;
  }

  private static int findMDArrayColumnInfo(
      final List<SGDataColumnInfo> columnInfoList, final String varName) {
    for (int ii = 0; ii < columnInfoList.size(); ii++) {
      SGMDArrayDataColumnInfo ncInfo = (SGMDArrayDataColumnInfo) columnInfoList.get(ii);
      String name = ncInfo.getName();
      if (varName.toUpperCase().startsWith(name.toUpperCase())) {
        return ii;
      }
    }
    return -1;
  }

  private static int setMDArrayColumnType(
      final List<SGDataColumnInfo> columnInfoList,
      final String[] columnTypes,
      final int[] dimensionIndex,
      final String varName,
      final String holderVarName,
      final String value) {
    final int index = findMDArrayColumnInfo(columnInfoList, varName);
    if (index != -1) {
      if (index < 0 || index >= columnTypes.length) {
        return -1;
      }
      final int cIndex = varName.lastIndexOf(':');
      if (cIndex == -1 || cIndex == varName.length() - 1) {
        return -1;
      }
      String sub = varName.substring(cIndex + 1);
      Integer num = SGUtilityText.getInteger(sub);
      if (num == null) {
        return -1;
      }
      if (num < 0 || num >= columnTypes.length) {
        return -1;
      }
      columnTypes[index] = SGDataColumnTitleUtility.appendColumnTitle(value, holderVarName);
      dimensionIndex[index] = num;
    }
    return index;
  }

  private static int setupMDArrayColumn2Dim(
      final List<SGDataColumnInfo> columnInfoList,
      final String[] columnTypes,
      final Map<String, Integer> dimensionMap,
      final String firstDimKey,
      final String secondDimKey,
      final String varName,
      final String value) {
    final int index = findMDArrayColumnInfo(columnInfoList, varName);
    if (index != -1) {
      if (index < 0 || index >= columnTypes.length) {
        return -1;
      }

      // the second dimension
      final int sIndex = varName.lastIndexOf(':');
      if (sIndex == -1 || sIndex == varName.length() - 1) {
        return -1;
      }
      final String sDimString = varName.substring(sIndex + 1);
      Integer sDim = SGUtilityText.getInteger(sDimString);
      if (sDim == null) {
        return -1;
      }

      // the first dimension
      final int fIndex = varName.lastIndexOf(':', sIndex - 1);
      if (fIndex == -1 || fIndex == varName.length() - 1) {
        return -1;
      }
      final String fDimString = varName.substring(fIndex + 1, sIndex);
      Integer fDim = SGUtilityText.getInteger(fDimString);
      if (fDim == null) {
        return -1;
      }

      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) columnInfoList.get(index);
      int[] dims = mdCol.getDimensions();
      if (sDim < 0 || sDim >= dims.length) {
        return -1;
      }
      if (fDim < 0 || fDim >= dims.length) {
        return -1;
      }
      columnTypes[index] = value;
      dimensionMap.put(firstDimKey, fDim);
      dimensionMap.put(secondDimKey, sDim);
    }
    return index;
  }

  private static int setupMDArrayColumn(
      final List<SGDataColumnInfo> columnInfoList,
      final String[] columnTypes,
      final int[] dimensionIndex,
      final String varName,
      final String value) {
    final int index = findMDArrayColumnInfo(columnInfoList, varName);
    if (index != -1) {
      if (index < 0 || index >= columnTypes.length) {
        return -1;
      }
      final int cIndex = varName.lastIndexOf(':');
      if (cIndex == -1 || cIndex == varName.length() - 1) {
        return -1;
      }
      final String dimString = varName.substring(cIndex + 1);
      Integer dim = SGUtilityText.getInteger(dimString);
      if (dim == null) {
        return -1;
      }
      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) columnInfoList.get(index);
      int[] dims = mdCol.getDimensions();
      if (dim < 0 || dim >= dims.length) {
        return -1;
      }
      columnTypes[index] = value;
      dimensionIndex[index] = dim;
    }
    return index;
  }

  static boolean getForSXYZMDArrayData(
      final List<SGDataColumnInfo> columnInfoList,
      final Map<String, Object> infoMap,
      final NamedNodeMap nodeMap,
      final String groupName,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    int[] dimensionIndex = new int[columns.length];
    for (int ii = 0; ii < dimensionIndex.length; ii++) {
      dimensionIndex[ii] = 0;
    }

    int index;

    // x, y and z values
    SGMDArrayDataColumnInfo xCol = null;
    int xIndex = -1;
    String[] xNames =
        getNames(nodeMap, new String[] {KEY_X_VALUE_NAME, KEY_X_VALUE_COLUMN_INDEX}, groupName);
    if (xNames != null) {
      if (xNames.length != 1) {
        return false;
      }
      index = setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, xNames[0], X_VALUE);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index)) {
        return false;
      }
      xIndex = dimensionIndex[index];
      xCol = (SGMDArrayDataColumnInfo) columns[index];
    }

    SGMDArrayDataColumnInfo yCol = null;
    int yIndex = -1;
    String[] yNames =
        getNames(nodeMap, new String[] {KEY_Y_VALUE_NAME, KEY_Y_VALUE_COLUMN_INDEX}, groupName);
    if (yNames != null) {
      if (yNames.length != 1) {
        return false;
      }
      index = setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, yNames[0], Y_VALUE);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index)) {
        return false;
      }
      yIndex = dimensionIndex[index];
      yCol = (SGMDArrayDataColumnInfo) columns[index];
    }

    String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
    if (dataType == null) {
      return false;
    }
    Boolean gridPlot = SGDataMiscUtility.isGridPlot(dataType, infoMap);
    if (gridPlot == null) {
      return false;
    }

    String[] zNames =
        getNames(nodeMap, new String[] {KEY_Z_VALUE_NAME, KEY_Z_VALUE_COLUMN_INDEX}, groupName);
    if (zNames == null) {
      return false;
    }
    if (zNames.length != 1) {
      return false;
    }
    Map<String, Integer> dimensionMap = new HashMap<String, Integer>();
    if (gridPlot) {
      index =
          setupMDArrayColumn2Dim(
              columnInfoList,
              columnTypes,
              dimensionMap,
              SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION,
              SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION,
              zNames[0],
              Z_VALUE);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index)) {
        return false;
      }
      SGMDArrayDataColumnInfo zCol = (SGMDArrayDataColumnInfo) columns[index];
      int[] zDim = zCol.getDimensions();
      if (xCol != null) {
        int[] xDim = xCol.getDimensions();
        Integer zxIndex = dimensionMap.get(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION);
        if (zxIndex != null) {
          if (zDim[zxIndex] != xDim[xIndex]) {
            return false;
          }
        }
      }
      if (yCol != null) {
        int[] yDim = yCol.getDimensions();
        Integer zyIndex = dimensionMap.get(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION);
        if (zyIndex != null) {
          if (zDim[zyIndex] != yDim[yIndex]) {
            return false;
          }
        }
      }
      zCol.putAllDimensionIndices(dimensionMap);
    } else {
      index = setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, zNames[0], Z_VALUE);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index)) {
        return false;
      }
    }

    for (int ii = 0; ii < columns.length; ii++) {
      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) columns[ii];
      mdCol.setColumnType(columnTypes[ii]);
      mdCol.setGenericDimensionIndex(dimensionIndex[ii]);
    }

    return true;
  }

  static boolean getForSXYZMDArrayDataIndex(
      final Map<String, Object> infoMap,
      final SGMDArrayVariable[] vars,
      final int size,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    TreeMap<Integer, List<MDArrayDimension>> varDimListMap = createDimensionListMap(columns);
    List<MDArrayDimension> dimList = extractDimensions(varDimListMap, 3);
    if (dimList == null) {
      return false;
    }
    MDArrayDimension xDim = dimList.get(0);
    MDArrayDimension yDim = dimList.get(1);
    MDArrayDimension zDim = dimList.get(2);
    columnTypes[xDim.variableIndex] = X_VALUE;
    columnTypes[yDim.variableIndex] = Y_VALUE;
    columnTypes[zDim.variableIndex] = Z_VALUE;

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
      SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
      mdInfo.setGenericDimensionIndex(0);
    }

    // sets dimensions
    setDimension(columns, xDim);
    setDimension(columns, yDim);
    setDimension(columns, zDim);

    return true;
  }

  static boolean getForSXYZMDArrayDataNormal(
      final Map<String, Object> infoMap,
      final SGMDArrayVariable[] vars,
      final int size,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    int zVarIndex = -1;
    int xDim = -1;
    int yDim = -1;
    for (int ii = 0; ii < columns.length; ii++) {
      SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
      int[] dims = mdInfo.getDimensions();
      if (dims.length >= 2) {
        zVarIndex = ii;
        xDim = 0;
        yDim = 1;
        break;
      }
    }
    if (zVarIndex == -1 || xDim == -1 || yDim == -1) {
      return false;
    }
    columnTypes[zVarIndex] = Z_VALUE;

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
      SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
      mdInfo.setGenericDimensionIndex(0);
    }

    // sets xDim and yDim
    SGMDArrayDataColumnInfo varInfo = (SGMDArrayDataColumnInfo) columns[zVarIndex];
    varInfo.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, xDim);
    varInfo.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, yDim);

    return true;
  }

  static boolean getForSXYZMDArrayData(
      final Map<String, Object> infoMap,
      final SGMDArrayVariable[] vars,
      final int size,
      final SGDataColumnInfo[] columns) {
    if (getForSXYZMDArrayDataNormal(infoMap, vars, size, columns)) {
      infoMap.put(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, true);
      return true;
    }
    if (getForSXYZMDArrayDataIndex(infoMap, vars, size, columns)) {
      infoMap.put(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, false);
      return true;
    }
    return false;
  }

  static boolean getForVXYMDArrayData(
      final List<SGDataColumnInfo> columnInfoList,
      final Map<String, Object> infoMap,
      final NamedNodeMap nodeMap,
      final String groupName,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    int[] dimensionIndex = new int[columns.length];
    for (int ii = 0; ii < dimensionIndex.length; ii++) {
      dimensionIndex[ii] = 0;
    }

    int index;

    // x-coordinate
    SGMDArrayDataColumnInfo xCol = null;
    int xIndex = -1;
    String[] xNames =
        getNames(
            nodeMap,
            new String[] {KEY_X_COORDINATE_VARIABLE_NAME, KEY_X_COORDINATE_COLUMN_INDEX},
            groupName);
    if (xNames != null) {
      if (xNames.length != 1) {
        return false;
      }
      index =
          setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, xNames[0], X_COORDINATE);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index)) {
        return false;
      }
      xIndex = dimensionIndex[index];
      xCol = (SGMDArrayDataColumnInfo) columns[index];
    }

    // y-coordinate
    SGMDArrayDataColumnInfo yCol = null;
    int yIndex = -1;
    String[] yNames =
        getNames(
            nodeMap,
            new String[] {KEY_Y_COORDINATE_VARIABLE_NAME, KEY_Y_COORDINATE_COLUMN_INDEX},
            groupName);
    if (yNames != null) {
      if (yNames.length != 1) {
        return false;
      }
      index =
          setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, yNames[0], Y_COORDINATE);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index)) {
        return false;
      }
      yIndex = dimensionIndex[index];
      yCol = (SGMDArrayDataColumnInfo) columns[index];
    }

    String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
    if (dataType == null) {
      return false;
    }
    Boolean gridPlot = SGDataMiscUtility.isGridPlot(dataType, infoMap);
    if (gridPlot == null) {
      return false;
    }

    // the first and the second component
    final String first = SGDataStrideUtility.getVXYFirstComponentColumnType(infoMap);
    final String second = SGDataStrideUtility.getVXYSecondComponentColumnType(infoMap);
    Map<String, Integer> dimensionMap = null;

    String[] fNames =
        getNames(
            nodeMap,
            new String[] {KEY_FIRST_COMPONENT_VARIABLE_NAME, KEY_FIRST_COMPONENT_COLUMN_INDEX},
            groupName);
    if (fNames == null) {
      return false;
    }
    if (fNames.length != 1) {
      return false;
    }
    if (gridPlot) {
      dimensionMap = new HashMap<String, Integer>();
      index =
          setupMDArrayColumn2Dim(
              columnInfoList,
              columnTypes,
              dimensionMap,
              SGIMDArrayConstants.KEY_VXY_X_DIMENSION,
              SGIMDArrayConstants.KEY_VXY_Y_DIMENSION,
              fNames[0],
              first);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index)) {
        return false;
      }
      SGMDArrayDataColumnInfo fCol = (SGMDArrayDataColumnInfo) columns[index];
      int[] fDim = fCol.getDimensions();
      if (xCol != null) {
        int[] xDim = xCol.getDimensions();
        Integer fxIndex = dimensionMap.get(SGIMDArrayConstants.KEY_VXY_X_DIMENSION);
        if (fxIndex != null) {
          if (fDim[fxIndex] != xDim[xIndex]) {
            return false;
          }
        }
      }
      if (yCol != null) {
        int[] yDim = yCol.getDimensions();
        Integer fyIndex = dimensionMap.get(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION);
        if (fyIndex != null) {
          if (fDim[fyIndex] != yDim[yIndex]) {
            return false;
          }
        }
      }
      fCol.putAllDimensionIndices(dimensionMap);
    } else {
      index = setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, fNames[0], first);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index)) {
        return false;
      }
    }

    String[] sNames =
        getNames(
            nodeMap,
            new String[] {KEY_SECOND_COMPONENT_VARIABLE_NAME, KEY_SECOND_COMPONENT_COLUMN_INDEX},
            groupName);
    if (sNames == null) {
      return false;
    }
    if (sNames.length != 1) {
      return false;
    }
    if (gridPlot) {
      dimensionMap = new HashMap<String, Integer>();
      index =
          setupMDArrayColumn2Dim(
              columnInfoList,
              columnTypes,
              dimensionMap,
              SGIMDArrayConstants.KEY_VXY_X_DIMENSION,
              SGIMDArrayConstants.KEY_VXY_Y_DIMENSION,
              sNames[0],
              second);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index)) {
        return false;
      }
      SGMDArrayDataColumnInfo sCol = (SGMDArrayDataColumnInfo) columns[index];
      int[] sDim = sCol.getDimensions();
      if (xCol != null) {
        int[] xDim = xCol.getDimensions();
        Integer sxIndex = dimensionMap.get(SGIMDArrayConstants.KEY_VXY_X_DIMENSION);
        if (sxIndex != null) {
          if (sDim[sxIndex] != xDim[xIndex]) {
            return false;
          }
        }
      }
      if (yCol != null) {
        int[] yDim = yCol.getDimensions();
        Integer syIndex = dimensionMap.get(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION);
        if (syIndex != null) {
          if (sDim[syIndex] != yDim[yIndex]) {
            return false;
          }
        }
      }
      sCol.putAllDimensionIndices(dimensionMap);
    } else {
      index = setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, sNames[0], second);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index)) {
        return false;
      }
    }

    for (int ii = 0; ii < columns.length; ii++) {
      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) columns[ii];
      mdCol.setColumnType(columnTypes[ii]);
      mdCol.setGenericDimensionIndex(dimensionIndex[ii]);
    }

    return true;
  }

  private static void setDimension(SGDataColumnInfo[] columns, MDArrayDimension dim) {
    SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[dim.variableIndex];
    mdInfo.setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, dim.dimensionIndex);
  }

  static boolean getForVXYMDArrayDataIndex(
      final Map<String, Object> infoMap,
      final SGMDArrayVariable[] vars,
      final int size,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    TreeMap<Integer, List<MDArrayDimension>> varDimListMap = createDimensionListMap(columns);
    List<MDArrayDimension> dimList = extractDimensions(varDimListMap, 4);
    if (dimList == null) {
      return false;
    }
    MDArrayDimension xDim = dimList.get(0);
    MDArrayDimension yDim = dimList.get(1);
    MDArrayDimension fDim = dimList.get(2);
    MDArrayDimension sDim = dimList.get(3);
    final String first = SGDataStrideUtility.getVXYFirstComponentColumnType(infoMap);
    final String second = SGDataStrideUtility.getVXYSecondComponentColumnType(infoMap);
    columnTypes[xDim.variableIndex] = X_COORDINATE;
    columnTypes[yDim.variableIndex] = Y_COORDINATE;
    columnTypes[fDim.variableIndex] = first;
    columnTypes[sDim.variableIndex] = second;

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
      SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
      mdInfo.setGenericDimensionIndex(0);
    }

    // sets dimensions
    setDimension(columns, xDim);
    setDimension(columns, yDim);
    setDimension(columns, fDim);
    setDimension(columns, sDim);

    return true;
  }

  static boolean getForVXYMDArrayDataNormal(
      final Map<String, Object> infoMap,
      final SGMDArrayVariable[] vars,
      final int size,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    List<Integer> candidates = new ArrayList<Integer>();
    for (int ii = 0; ii < columns.length; ii++) {
      SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
      int[] dims = mdInfo.getDimensions();
      if (dims.length >= 2) {
        candidates.add(ii);
      }
    }
    if (candidates.size() < 2) {
      return false;
    }

    int fVarIndex = -1;
    int fXDim = -1;
    int fYDim = -1;
    int sVarIndex = -1;
    int sXDim = -1;
    int sYDim = -1;
    for (int ii = 0; ii < candidates.size() - 1; ii++) {
      Integer index1 = candidates.get(ii);
      SGMDArrayDataColumnInfo c1 = (SGMDArrayDataColumnInfo) columns[index1];
      final int[] dims1 = c1.getDimensions();
      for (int jj = ii + 1; jj < candidates.size(); jj++) {
        Integer index2 = candidates.get(jj);
        SGMDArrayDataColumnInfo c2 = (SGMDArrayDataColumnInfo) columns[index2];
        final int[] dims2 = c2.getDimensions();
        List<IndexPair> pairs = new ArrayList<IndexPair>();
        for (int kk = 0; kk < dims1.length; kk++) {
          for (int ll = 0; ll < dims2.length; ll++) {
            if (dims1[kk] == dims2[ll]) {
              IndexPair pair = new IndexPair();
              pair.index1 = kk;
              pair.index2 = ll;
              pairs.add(pair);
            }
          }
        }
        if (pairs.size() > 1) {
          // finds two pairs without overlapping
          IndexPair pairX = null;
          IndexPair pairY = null;
          for (int kk = 0; kk < pairs.size() - 1; kk++) {
            IndexPair p1 = pairs.get(kk);
            for (int ll = kk + 1; ll < pairs.size(); ll++) {
              IndexPair p2 = pairs.get(ll);
              if (!p1.partiallyEquals(p2)) {
                pairX = p1;
                pairY = p2;
                break;
              }
            }
            if (pairX != null && pairY != null) {
              break;
            }
          }
          if (pairX == null || pairY == null) {
            continue;
          }
          fVarIndex = index1;
          sVarIndex = index2;
          fXDim = pairX.index1;
          sXDim = pairX.index2;
          fYDim = pairY.index1;
          sYDim = pairY.index2;
          break;
        }
      }
      if (fVarIndex != -1) {
        break;
      }
    }

    if (fVarIndex == -1 || fXDim == -1 || fYDim == -1) {
      return false;
    }
    if (sVarIndex == -1 || sXDim == -1 || sYDim == -1) {
      return false;
    }

    final String first = SGDataStrideUtility.getVXYFirstComponentColumnType(infoMap);
    final String second = SGDataStrideUtility.getVXYSecondComponentColumnType(infoMap);
    columnTypes[fVarIndex] = first;
    columnTypes[sVarIndex] = second;

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
      SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
      mdInfo.setGenericDimensionIndex(0);
    }

    // sets xDim and yDim
    SGMDArrayDataColumnInfo fVarInfo = (SGMDArrayDataColumnInfo) columns[fVarIndex];
    fVarInfo.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_X_DIMENSION, fXDim);
    fVarInfo.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION, fYDim);
    SGMDArrayDataColumnInfo sVarInfo = (SGMDArrayDataColumnInfo) columns[sVarIndex];
    sVarInfo.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_X_DIMENSION, sXDim);
    sVarInfo.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION, sYDim);

    return true;
  }

  static boolean getForVXYMDArrayData(
      final Map<String, Object> infoMap,
      final SGMDArrayVariable[] vars,
      final int size,
      final SGDataColumnInfo[] columns) {
    if (getForVXYMDArrayDataNormal(infoMap, vars, size, columns)) {
      infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, true);
      return true;
    }
    if (getForVXYMDArrayDataIndex(infoMap, vars, size, columns)) {
      infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, false);
      return true;
    }
    return false;
  }

  private static boolean setMDArrayErrorBar(
      final List<SGDataColumnInfo> columnInfoList,
      String[] columnTypes,
      int[] dimensionIndex,
      String leName,
      String ueName,
      String ehName) {
    if (leName.equals(ueName)) {
      final int index =
          setMDArrayColumnType(
              columnInfoList, columnTypes, dimensionIndex, leName, ehName, LOWER_UPPER_ERROR_VALUE);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index)) {
        return false;
      }
    } else {
      int index;
      index =
          setMDArrayColumnType(
              columnInfoList, columnTypes, dimensionIndex, leName, ehName, LOWER_ERROR_VALUE);
      if (index == -1) {
        return false;
      }
      index =
          setMDArrayColumnType(
              columnInfoList, columnTypes, dimensionIndex, ueName, ehName, UPPER_ERROR_VALUE);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index)) {
        return false;
      }
    }
    return true;
  }

  private static boolean setMDArrayTickLabel(
      final List<SGDataColumnInfo> columnInfoList,
      String[] columnTypes,
      int[] dimensionIndex,
      String tlName,
      String thName) {
    final int index =
        setMDArrayColumnType(
            columnInfoList, columnTypes, dimensionIndex, tlName, thName, TICK_LABEL);
    if (index == -1) {
      return false;
    }
    return true;
  }

  private static int getIndex(String name, List<SGDataColumnInfo> columnInfoList) {
    for (int ii = 0; ii < columnInfoList.size(); ii++) {
      SGDataColumnInfo info = columnInfoList.get(ii);
      if (name.equals(info.getName())) {
        return ii;
      }
    }
    return -1;
  }

  static boolean getForSXYMDArrayData(
      final List<SGDataColumnInfo> columnInfoList,
      final Map<String, Object> infoMap,
      final NamedNodeMap nodeMap,
      final String groupName,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    int[] dimensionIndex = new int[columns.length];
    for (int ii = 0; ii < dimensionIndex.length; ii++) {
      dimensionIndex[ii] = 0;
    }

    // pick up
    Map<?, ?> dimensionIndexMap =
        (infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP)
                instanceof Map<?, ?> m)
            ? m
            : null;
    SGMDArrayPickUpDimensionInfo pickUpInfo = null;
    if (dimensionIndexMap != null) {
      SGIntegerSeriesSet pickUpDimensionIndices =
          (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
      Map<String, Integer> typedMap = new java.util.HashMap<>();
      for (Map.Entry<?, ?> entry : dimensionIndexMap.entrySet()) {
        typedMap.put((String) entry.getKey(), (Integer) entry.getValue());
      }
      pickUpInfo = new SGMDArrayPickUpDimensionInfo(typedMap, pickUpDimensionIndices);
    }

    // x and y values
    String[] xNames =
        getNames(nodeMap, new String[] {KEY_X_VALUE_NAMES, KEY_X_VALUE_COLUMN_INDICES}, groupName);
    if (xNames == null) {
      xNames =
          getNames(nodeMap, new String[] {KEY_X_VALUE_NAME, KEY_X_VALUE_COLUMN_INDEX}, groupName);
    }
    if (xNames != null) {
      if (xNames.length == 0) {
        return false;
      }
    }
    String[] yNames =
        getNames(nodeMap, new String[] {KEY_Y_VALUE_NAMES, KEY_Y_VALUE_COLUMN_INDICES}, groupName);
    if (yNames == null) {
      yNames =
          getNames(nodeMap, new String[] {KEY_Y_VALUE_NAME, KEY_Y_VALUE_COLUMN_INDEX}, groupName);
    }
    if (yNames != null) {
      if (yNames.length == 0) {
        return false;
      }
    }
    if (xNames == null && yNames == null) {
      return false;
    }
    if (xNames != null && yNames != null) {
      if (xNames.length > 1 && yNames.length > 1) {
        return false;
      }
    }
    List<Integer> xIndices = new ArrayList<Integer>();
    if (xNames != null && !"".equals(xNames[0])) {
      for (int ii = 0; ii < xNames.length; ii++) {
        final int index =
            setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, xNames[ii], X_VALUE);
        if (index == -1) {
          return false;
        }
        if (!isNumberColumn(columnInfoList, index)) {
          return false;
        }
        xIndices.add(index);
      }
    }
    List<Integer> yIndices = new ArrayList<Integer>();
    if (yNames != null && !"".equals(yNames[0])) {
      for (int ii = 0; ii < yNames.length; ii++) {
        final int index =
            setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, yNames[ii], Y_VALUE);
        if (index == -1) {
          return false;
        }
        if (!isNumberColumn(columnInfoList, index)) {
          return false;
        }
        yIndices.add(index);
      }
    }

    // error values
    if (pickUpInfo != null) {
      String[] leNames =
          getNames(
              nodeMap,
              new String[] {KEY_LOWER_ERROR_VALUE_NAME, KEY_LOWER_ERROR_BAR_COLUMN_INDICES},
              groupName);
      String[] ueNames =
          getNames(
              nodeMap,
              new String[] {KEY_UPPER_ERROR_VALUE_NAME, KEY_UPPER_ERROR_BAR_COLUMN_INDICES},
              groupName);
      String[] ehNames =
          getNames(
              nodeMap,
              new String[] {KEY_ERROR_BAR_HOLDER_NAME, KEY_ERROR_BAR_HOLDER_COLUMN_INDICES},
              groupName);
      if (leNames != null && ueNames != null && ehNames != null) {
        if (leNames.length != 1 || ueNames.length != 1 || ehNames.length != 1) {
          return false;
        }
        String leName = leNames[0];
        String ueName = ueNames[0];
        String ehName = ehNames[0];
        final int pickUpHolderIndex = getIndex(ehName, columnInfoList);
        if (pickUpHolderIndex == -1) {
          return false;
        }
        SGMDArrayDataColumnInfo mdInfo =
            (SGMDArrayDataColumnInfo) columnInfoList.get(pickUpHolderIndex);
        if (setMDArrayErrorBar(
                columnInfoList, columnTypes, dimensionIndex, leName, ueName, mdInfo.getName())
            == false) {
          return false;
        }
      }

    } else {
      String[] leNames =
          getNames(
              nodeMap,
              new String[] {KEY_LOWER_ERROR_VALUE_NAMES, KEY_LOWER_ERROR_BAR_COLUMN_INDICES},
              groupName);
      String[] ueNames =
          getNames(
              nodeMap,
              new String[] {KEY_UPPER_ERROR_VALUE_NAMES, KEY_UPPER_ERROR_BAR_COLUMN_INDICES},
              groupName);
      String[] ehNames =
          getNames(
              nodeMap,
              new String[] {KEY_ERROR_BAR_HOLDER_NAMES, KEY_ERROR_BAR_HOLDER_COLUMN_INDICES},
              groupName);
      if (leNames != null && ueNames != null && ehNames != null) {
        if (ehNames.length != leNames.length || ehNames.length != ueNames.length) {
          return false;
        }
        for (int ii = 0; ii < ehNames.length; ii++) {
          final int index = findMDArrayColumnInfo(columnInfoList, ehNames[ii]);
          if (index == -1) {
            return false;
          }
          if (!isNumberColumn(columnInfoList, index)) {
            return false;
          }
          if (!xIndices.contains(index) && !yIndices.contains(index)) {
            return false;
          }
        }
        for (int ii = 0; ii < ehNames.length; ii++) {
          if (setMDArrayErrorBar(
                  columnInfoList,
                  columnTypes,
                  dimensionIndex,
                  leNames[ii],
                  ueNames[ii],
                  ehNames[ii])
              == false) {
            return false;
          }
        }
      }
    }

    // tick labels
    if (pickUpInfo != null) {
      String[] tlNames =
          getNames(
              nodeMap,
              new String[] {KEY_TICK_LABEL_NAME, KEY_TICK_LABEL_COLUMN_INDICES},
              groupName);
      String[] thNames =
          getNames(
              nodeMap,
              new String[] {KEY_TICK_LABEL_HOLDER_NAME, KEY_TICK_LABEL_HOLDER_COLUMN_INDICES},
              groupName);
      if (tlNames != null) {
        if (tlNames.length != 1 || thNames.length != 1) {
          return false;
        }
        String tlName = tlNames[0];
        String thName = thNames[0];
        final int pickUpHolderIndex = getIndex(thName, columnInfoList);
        if (pickUpHolderIndex == -1) {
          return false;
        }
        SGMDArrayDataColumnInfo mdInfo =
            (SGMDArrayDataColumnInfo) columnInfoList.get(pickUpHolderIndex);
        if (setMDArrayTickLabel(
                columnInfoList, columnTypes, dimensionIndex, tlName, mdInfo.getName())
            == false) {
          return false;
        }
      }

    } else {
      String[] tlNames =
          getNames(
              nodeMap,
              new String[] {KEY_TICK_LABEL_NAMES, KEY_TICK_LABEL_COLUMN_INDICES},
              groupName);
      String[] thNames =
          getNames(
              nodeMap,
              new String[] {KEY_TICK_LABEL_HOLDER_NAMES, KEY_TICK_LABEL_HOLDER_COLUMN_INDICES},
              groupName);
      if (tlNames != null && thNames != null) {
        if (tlNames.length != thNames.length) {
          return false;
        }
        for (int ii = 0; ii < thNames.length; ii++) {
          final int index = findMDArrayColumnInfo(columnInfoList, thNames[ii]);
          if (index == -1) {
            return false;
          }
          if (!xIndices.contains(index) && !yIndices.contains(index)) {
            return false;
          }
        }
        for (int ii = 0; ii < thNames.length; ii++) {
          if (setMDArrayTickLabel(
                  columnInfoList, columnTypes, dimensionIndex, tlNames[ii], thNames[ii])
              == false) {
            return false;
          }
        }
      }
    }

    for (int ii = 0; ii < columns.length; ii++) {
      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) columns[ii];
      mdCol.setColumnType(columnTypes[ii]);
      mdCol.setGenericDimensionIndex(dimensionIndex[ii]);
    }

    return true;
  }

  static boolean getForSXYMDArrayData(
      final Map<String, Object> infoMap,
      final SGMDArrayVariable[] vars,
      final int size,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    // dimension indices
    int[] indices = new int[columns.length];
    for (int ii = 0; ii < indices.length; ii++) {
      indices[ii] = 0;
    }

    Boolean multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
    if (multiple == null) {
      return false;
    }

    // create a map of dimensions
    TreeMap<Integer, List<MDArrayDimension>> dimListMap = createDimensionListMap(columns);

    // assign the columns for y-values
    List<MDArrayDimension> yDimList = extractDimensions(dimListMap, 1);
    final int maxSize = 5;
    if (yDimList.size() > maxSize) {
      yDimList = new ArrayList<MDArrayDimension>(yDimList.subList(0, maxSize));
    }
    if (yDimList == null || yDimList.size() == 0) {
      return false;
    }

    // for multiple variables
    if (multiple.booleanValue()) {
      // multiple y-indices
      for (MDArrayDimension yDim : yDimList) {
        columnTypes[yDim.variableIndex] = Y_VALUE;
        indices[yDim.variableIndex] = yDim.dimensionIndex;
      }
    } else {
      MDArrayDimension yDim = yDimList.get(0);
      columnTypes[yDim.variableIndex] = Y_VALUE;
      indices[yDim.variableIndex] = yDim.dimensionIndex;
    }

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
      SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
      mdInfo.setGenericDimensionIndex(indices[ii]);
    }

    return true;
  }

  private static class MDArrayDimension implements Comparable<Object> {
    int variableIndex = 0;
    int dimensionIndex = 0;

    MDArrayDimension(final int variableIndex, final int dimensionIndex) {
      super();
      this.variableIndex = variableIndex;
      this.dimensionIndex = dimensionIndex;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.variableIndex);
      sb.append('-');
      sb.append(this.dimensionIndex);
      return sb.toString();
    }

    @Override
    public int compareTo(Object o) {
      if (!(o instanceof MDArrayDimension)) {
        throw new IllegalArgumentException("Invalid input: " + o);
      }
      MDArrayDimension oDim = (MDArrayDimension) o;
      if (this.variableIndex < oDim.variableIndex) {
        return -1;
      } else if (this.variableIndex == oDim.variableIndex) {
        if (this.dimensionIndex < oDim.dimensionIndex) {
          return -1;
        } else if (this.dimensionIndex == oDim.dimensionIndex) {
          return 0;
        } else {
          return 1;
        }
      } else {
        return 1;
      }
    }
  }

  static class IndexPair {
    int index1;
    int index2;

    public boolean partiallyEquals(IndexPair pair) {
      return (this.index1 == pair.index1) || (this.index2 == pair.index2);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append('(');
      sb.append(index1);
      sb.append(',');
      sb.append(index2);
      sb.append(')');
      return sb.toString();
    }
  }
}
