package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeUtility.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import org.w3c.dom.NamedNodeMap;
import ucar.nc2.Dimension;

/** Static helper for the NetCDF default column types. */
public final class SGDefaultColumnTypeNetCDFUtility
    implements SGIDataColumnTypeConstants, SGIDataPropertyKeyConstants {

  private SGDefaultColumnTypeNetCDFUtility() {}

  private static List<Integer> getIndexVariableColumnIndices(SGDataColumnInfo[] columns) {
    final String idx = INDEX.toUpperCase();
    final String sn = SERIAL_NUMBERS.toUpperCase();
    List<Integer> indices = new ArrayList<Integer>();
    for (int ii = 0; ii < columns.length; ii++) {
      String name = columns[ii].getName();
      String nUpper = name.toUpperCase();
      if (nUpper.endsWith(idx) || nUpper.endsWith(sn)) {
        indices.add(ii);
      }
    }
    return indices;
  }

  /**
   * Finds and returns the origin map for netCDF data. If the origin map is not found, returns an
   * empty map.
   *
   * @param nodeMap a node map
   * @return the origin map
   */
  public static Map<String, Integer> getNetCDFOriginMap(NamedNodeMap nodeMap) {
    Map<String, Integer> map = new HashMap<String, Integer>();
    if (nodeMap != null) {
      String originMapStr = getString(nodeMap, SGIDataPropertyKeyConstants.KEY_ORIGIN_MAP);
      if (originMapStr != null) {
        String[][] originMapArray = SGUtilityText.readStringMaps(originMapStr);
        for (int ii = 0; ii < originMapArray.length; ii++) {
          String key = originMapArray[ii][0];
          String value = originMapArray[ii][1];
          Integer num = SGUtilityText.getInteger(value);
          if (num == null) {
            return new HashMap<String, Integer>();
          }
          map.put(key, num);
        }
      }
    }
    return map;
  }

  private static int getNetCDFCoordinateVariableIndexWithoutUnlimited(
      final List<SGNetCDFVariable> varList, final int startIndex) {
    final int size = varList.size();
    int index = -1;
    for (int ii = startIndex; ii < size; ii++) {
      SGNetCDFVariable var = varList.get(ii);
      if (var.isCoordinateVariable() && !var.isUnlimited()) {
        // excepting unlimited variables first
        index = ii;
        break;
      }
    }
    return index;
  }

  private static boolean isEqualNetCDFName(String name1, String name2) {
    char[] cArray1 = name1.toCharArray();
    char[] cArray2 = name2.toCharArray();
    List<Character> cList1 = new ArrayList<Character>();
    List<Character> cList2 = new ArrayList<Character>();
    for (char c : cArray1) {
      if (c == '/') {
        c = '_';
      }
      cList1.add(c);
    }
    for (char c : cArray2) {
      if (c == '/') {
        c = '_';
      }
      cList2.add(c);
    }
    return cList1.equals(cList2);
  }

  /**
   * Returns the index of the column matching the given netCDF variable in the selection.
   *
   * @param columns the selected columns
   * @param var the netCDF variable
   * @return the column index, or -1 if the variable is not selected
   */
  private static int findColumnIndex(final SGDataColumnInfo[] columns, final SGNetCDFVariable var) {
    for (int ii = 0; ii < columns.length; ii++) {
      SGNetCDFDataColumnInfo info = (SGNetCDFDataColumnInfo) columns[ii];
      if (isEqualNetCDFName(info.getName(), var.getName())) {
        return ii;
      }
    }
    return -1;
  }

  private static int findNetCDFColumnInfo(
      final List<SGDataColumnInfo> columnInfoList, final String varName) {
    for (int ii = 0; ii < columnInfoList.size(); ii++) {
      SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) columnInfoList.get(ii);
      //			if (ncInfo.getName().equals(varName)) {
      //				return ii;
      //			}
      if (isEqualNetCDFName(ncInfo.getName(), varName)) {
        return ii;
      }
    }
    return -1;
  }

  private static int setupNetCDFColumn(
      final List<SGDataColumnInfo> columnInfoList,
      final String[] columnTypes,
      final String varName,
      final String holderVarName,
      final String value) {
    final int index = findNetCDFColumnInfo(columnInfoList, varName);
    if (index != -1) {
      if (index < 0 || index >= columnTypes.length) {
        return -1;
      }
      columnTypes[index] = SGDataColumnTitleUtility.appendColumnTitle(value, holderVarName);
    }
    return index;
  }

  private static int setNetCDFColumnType(
      final List<SGDataColumnInfo> columnInfoList,
      final String[] columnTypes,
      final String varName,
      final String value) {
    int index = findNetCDFColumnInfo(columnInfoList, varName);
    if (index != -1) {
      if (index < 0 || index >= columnTypes.length) {
        return -1;
      }
      columnTypes[index] = value;
    }
    return index;
  }

  private static int getNetCDFCoordinateVariableIndex(
      final List<SGNetCDFVariable> varList, final int startIndex) {
    final int size = varList.size();
    int index = -1;

    for (int ii = startIndex; ii < size; ii++) {
      SGNetCDFVariable var = varList.get(ii);
      if (var.isCoordinateVariable()) {
        index = ii;
        break;
      }
    }

    return index;
  }

  private static int[] getNetCDFCoordinateVariableXYIndex(
      final List<SGNetCDFVariable> varList, final int startIndex) {
    final int size = varList.size();
    int xIndex = -1;
    int yIndex = -1;

    // a coordinate variable found first is assigned to the x variable
    // and found next is assigned to the y variable
    xIndex = getNetCDFCoordinateVariableIndexWithoutUnlimited(varList, startIndex);
    if (xIndex == -1 || xIndex == size - 1) {
      xIndex = getNetCDFCoordinateVariableIndex(varList, startIndex);
    }
    if (xIndex == -1 || xIndex == size - 1) {
      return null;
    }

    yIndex = getNetCDFCoordinateVariableIndexWithoutUnlimited(varList, xIndex + 1);
    if (yIndex == -1) {
      yIndex = getNetCDFCoordinateVariableIndex(varList, xIndex + 1);
    }
    if (yIndex == -1) {
      return null;
    }

    return new int[] {xIndex, yIndex};
  }

  static boolean getForSXYZNetCDFData(
      final List<SGDataColumnInfo> columnInfoList,
      final Map<String, Object> infoMap,
      final NamedNodeMap nodeMap,
      final String groupName,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    int idx;

    // index
    String[] indexNames = getNames(nodeMap, KEY_INDEX_VARIABLE_NAME, groupName);
    if (indexNames == null) {
      indexNames = getNames(nodeMap, KEY_SERIAL_NUMBER_VARIABLE_NAME, groupName);
    }
    int indexIdx = -1;
    if (indexNames != null) {
      if (indexNames.length != 1) {
        return false;
      }
      if ((indexIdx = setNetCDFColumnType(columnInfoList, columnTypes, indexNames[0], INDEX))
          == -1) {
        return false;
      }
      if (!isCoordinateVariableColumn(columnInfoList, indexIdx)) {
        return false;
      }
    }

    // x, y and z values
    String[] xNames =
        getNames(nodeMap, new String[] {KEY_X_VALUE_NAME, KEY_X_VALUE_COLUMN_INDEX}, groupName);
    if (xNames == null) {
      return false;
    }
    if (xNames.length != 1) {
      return false;
    }
    idx = setNetCDFColumnType(columnInfoList, columnTypes, xNames[0], X_VALUE);
    if (idx == -1) {
      return false;
    }
    if (!isNumberColumn(columnInfoList, idx)) {
      return false;
    }
    if (indexIdx != -1) {
      if (isCoordinateVariableColumn(columnInfoList, idx)) {
        return false;
      }
    } else {
      String[] xIndexNames = getNames(nodeMap, new String[] {KEY_X_INDEX_VARIABLE_NAME}, groupName);
      if (xIndexNames != null) {
        if (xIndexNames.length != 1) {
          return false;
        }
        idx = setNetCDFColumnType(columnInfoList, columnTypes, xIndexNames[0], X_INDEX);
        if (idx == -1) {
          return false;
        }
        if (!isNumberColumn(columnInfoList, idx)) {
          return false;
        }
        if (!isCoordinateVariableColumn(columnInfoList, idx)) {
          return false;
        }
      } else {
        if (!isCoordinateVariableColumn(columnInfoList, idx)) {
          return false;
        }
      }
    }

    String[] yNames =
        getNames(nodeMap, new String[] {KEY_Y_VALUE_NAME, KEY_Y_VALUE_COLUMN_INDEX}, groupName);
    if (yNames == null) {
      return false;
    }
    if (yNames.length != 1) {
      return false;
    }
    idx = setNetCDFColumnType(columnInfoList, columnTypes, yNames[0], Y_VALUE);
    if (idx == -1) {
      return false;
    }
    if (!isNumberColumn(columnInfoList, idx)) {
      return false;
    }
    if (indexIdx != -1) {
      if (isCoordinateVariableColumn(columnInfoList, idx)) {
        return false;
      }
    } else {
      String[] yIndexNames = getNames(nodeMap, new String[] {KEY_Y_INDEX_VARIABLE_NAME}, groupName);
      if (yIndexNames != null) {
        if (yIndexNames.length != 1) {
          return false;
        }
        idx = setNetCDFColumnType(columnInfoList, columnTypes, yIndexNames[0], Y_INDEX);
        if (idx == -1) {
          return false;
        }
        if (!isNumberColumn(columnInfoList, idx)) {
          return false;
        }
        if (!isCoordinateVariableColumn(columnInfoList, idx)) {
          return false;
        }
      } else {
        if (!isCoordinateVariableColumn(columnInfoList, idx)) {
          return false;
        }
      }
    }

    String[] zNames =
        getNames(nodeMap, new String[] {KEY_Z_VALUE_NAME, KEY_Z_VALUE_COLUMN_INDEX}, groupName);
    if (zNames == null) {
      return false;
    }
    if (zNames.length != 1) {
      return false;
    }
    idx = setNetCDFColumnType(columnInfoList, columnTypes, zNames[0], Z_VALUE);
    if (idx == -1) {
      return false;
    }
    if (!isNumberColumn(columnInfoList, idx)) {
      return false;
    }
    if (isCoordinateVariableColumn(columnInfoList, idx)) {
      return false;
    }

    // time
    String[] timeNames = getNames(nodeMap, KEY_TIME_VARIABLE_NAME, groupName);
    if (timeNames != null) {
      if (timeNames.length != 1) {
        return false;
      }
      idx = setNetCDFColumnType(columnInfoList, columnTypes, timeNames[0], ANIMATION_FRAME);
      if (idx == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, idx)) {
        return false;
      }
      if (!isCoordinateVariableColumn(columnInfoList, idx)) {
        return false;
      }
    }

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  static boolean getForSXYZNetCDFDataIndex(
      final Map<String, Object> infoMap,
      final List<SGNetCDFVariable> varList,
      final int size,
      final SGDataColumnInfo[] columns,
      List<Integer> serialNumberIndices) {

    List<SGNetCDFVariable> varListSorted = sortVariableList(varList, serialNumberIndices);

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    int iIndex = -1;
    int xIndex = -1;
    int yIndex = -1;
    int zIndex = -1;
    for (int ii = 0; ii < varListSorted.size(); ii++) {
      SGNetCDFVariable var = varListSorted.get(ii);
      if (var.isCoordinateVariable()) {
        Dimension cDim = var.getDimension(0);
        for (int jj = 0; jj < varListSorted.size(); jj++) {
          if (jj == ii) {
            continue;
          }
          SGNetCDFVariable v = varListSorted.get(jj);
          if (!VALUE_TYPE_NUMBER.equals(v.getValueType())) {
            continue;
          }
          List<Dimension> dimList = v.getDimensions();
          if (!dimList.contains(cDim)) {
            continue;
          }
          if (xIndex == -1) {
            xIndex = jj;
          } else if (yIndex == -1) {
            yIndex = jj;
          } else if (zIndex == -1) {
            zIndex = jj;
          }
        }
        if (xIndex != -1 && yIndex != -1 && zIndex != -1) {
          // checks overlapping
          Set<Integer> indexSet = new HashSet<Integer>();
          indexSet.add(xIndex);
          indexSet.add(yIndex);
          indexSet.add(zIndex);
          if (indexSet.size() == 3) {
            iIndex = ii;
            break;
          }
        }

        // clears indices
        xIndex = -1;
        yIndex = -1;
        zIndex = -1;
      }
    }
    if (iIndex == -1 || xIndex == -1 || yIndex == -1 || zIndex == -1) {
      return false;
    }

    int iCol = findColumnIndex(columns, varListSorted.get(iIndex));
    int xCol = findColumnIndex(columns, varListSorted.get(xIndex));
    int yCol = findColumnIndex(columns, varListSorted.get(yIndex));
    int zCol = findColumnIndex(columns, varListSorted.get(zIndex));
    if (iCol == -1 || xCol == -1 || yCol == -1 || zCol == -1) {
      return false;
    }

    columnTypes[iCol] = INDEX;
    columnTypes[xCol] = X_VALUE;
    columnTypes[yCol] = Y_VALUE;
    columnTypes[zCol] = Z_VALUE;

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  static boolean getForSXYZNetCDFDataNormal(
      final Map<String, Object> infoMap,
      final List<SGNetCDFVariable> varList,
      final int size,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    int xIndex = -1;
    int yIndex = -1;
    int tIndex = -1;

    // a coordinate variable found first is assigned to the x variable
    // and found next is assigned to the y variable
    SGNetCDFVariable xVar = null;
    SGNetCDFVariable yVar = null;
    int startIndex = 0;
    int zIndex = -1;
    List<Dimension> dimListZ = null;
    for (int ii = 0; ii < varList.size() - 2; ii++) {
      int[] xyIndex = getNetCDFCoordinateVariableXYIndex(varList, startIndex);
      if (xyIndex == null) {
        startIndex += 1;
        continue;
      } else {
        xIndex = xyIndex[0];
        yIndex = xyIndex[1];
        xVar = varList.get(xIndex);
        yVar = varList.get(yIndex);
      }

      Dimension xDim = xVar.getDimension(0);
      Dimension yDim = yVar.getDimension(0);
      for (int jj = 0; jj < size; jj++) {
        SGNetCDFVariable var = varList.get(jj);
        if (!var.isCoordinateVariable() && VALUE_TYPE_NUMBER.equals(var.getValueType())) {
          List<Dimension> dimList = var.getDimensions();
          if (dimList.contains(xDim) && dimList.contains(yDim)) {
            zIndex = jj;
            break;
          }
        }
      }
      if (zIndex != -1) {
        break;
      } else {
        startIndex += 1;
      }
    }
    if (zIndex == -1) {
      return false;
    } else {
      dimListZ = varList.get(zIndex).getDimensions();
    }

    // unlimited coordinate variable is assigned to time variable
    // if it exists
    for (int ii = 0; ii < size; ii++) {
      if (ii == xIndex || ii == yIndex || ii == zIndex) {
        continue;
      }
      SGNetCDFVariable var = varList.get(ii);
      if (var.isUnlimited() && var.isCoordinateVariable()) {
        Dimension dim = var.getDimension(0);
        if (dimListZ.contains(dim)) {
          tIndex = ii;
        }
        break;
      }
    }

    int xCol = findColumnIndex(columns, xVar);
    int yCol = findColumnIndex(columns, yVar);
    int zCol = findColumnIndex(columns, varList.get(zIndex));
    if (xCol == -1 || yCol == -1 || zCol == -1) {
      return false;
    }
    columnTypes[xCol] = X_VALUE;
    columnTypes[yCol] = Y_VALUE;
    columnTypes[zCol] = Z_VALUE;
    if (tIndex != -1) {
      int tCol = findColumnIndex(columns, varList.get(tIndex));
      if (tCol != -1) {
        columnTypes[tCol] = ANIMATION_FRAME;
      }
    }

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  static boolean getForSXYZNetCDFData(
      final Map<String, Object> infoMap,
      final List<SGNetCDFVariable> varList,
      final int size,
      final SGDataColumnInfo[] columns) {
    List<Integer> indices = getIndexVariableColumnIndices(columns);
    if (indices.size() > 0) {
      if (getForSXYZNetCDFDataIndex(infoMap, varList, size, columns, indices)) {
        return true;
      }
      if (getForSXYZNetCDFDataNormal(infoMap, varList, size, columns)) {
        return true;
      }
    } else {
      if (getForSXYZNetCDFDataNormal(infoMap, varList, size, columns)) {
        return true;
      }
      if (getForSXYZNetCDFDataIndex(infoMap, varList, size, columns, indices)) {
        return true;
      }
    }
    return false;
  }

  static boolean getForVXYNetCDFData(
      final List<SGDataColumnInfo> columnInfoList,
      final Map<String, Object> infoMap,
      final NamedNodeMap nodeMap,
      final String groupName,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    int idx;

    // serial numbers
    String[] indexNames = getNames(nodeMap, KEY_INDEX_VARIABLE_NAME, groupName);
    if (indexNames == null) {
      indexNames = getNames(nodeMap, KEY_SERIAL_NUMBER_VARIABLE_NAME, groupName);
    }
    int indexIdx = -1;
    if (indexNames != null) {
      if (indexNames.length != 1) {
        return false;
      }
      if ((indexIdx = setNetCDFColumnType(columnInfoList, columnTypes, indexNames[0], INDEX))
          == -1) {
        return false;
      }
      if (!isCoordinateVariableColumn(columnInfoList, indexIdx)) {
        return false;
      }
    }

    // x-coordinate
    String[] xNames =
        getNames(
            nodeMap,
            new String[] {KEY_X_COORDINATE_VARIABLE_NAME, KEY_X_COORDINATE_COLUMN_INDEX},
            groupName);
    if (xNames == null) {
      return false;
    }
    if (xNames.length != 1) {
      return false;
    }
    idx = setNetCDFColumnType(columnInfoList, columnTypes, xNames[0], X_COORDINATE);
    if (idx == -1) {
      return false;
    }
    if (!isNumberColumn(columnInfoList, idx)) {
      return false;
    }
    if (indexIdx != -1) {
      if (isCoordinateVariableColumn(columnInfoList, idx)) {
        return false;
      }
    } else {
      String[] xIndexNames = getNames(nodeMap, new String[] {KEY_X_INDEX_VARIABLE_NAME}, groupName);
      if (xIndexNames != null) {
        if (xIndexNames.length != 1) {
          return false;
        }
        idx = setNetCDFColumnType(columnInfoList, columnTypes, xIndexNames[0], X_INDEX);
        if (idx == -1) {
          return false;
        }
        if (!isNumberColumn(columnInfoList, idx)) {
          return false;
        }
        if (!isCoordinateVariableColumn(columnInfoList, idx)) {
          return false;
        }
      } else {
        if (!isCoordinateVariableColumn(columnInfoList, idx)) {
          return false;
        }
      }
    }

    // y-coordinate
    String[] yNames =
        getNames(
            nodeMap,
            new String[] {KEY_Y_COORDINATE_VARIABLE_NAME, KEY_Y_COORDINATE_COLUMN_INDEX},
            groupName);
    if (yNames == null) {
      return false;
    }
    if (yNames.length != 1) {
      return false;
    }
    idx = setNetCDFColumnType(columnInfoList, columnTypes, yNames[0], Y_COORDINATE);
    if (idx == -1) {
      return false;
    }
    if (!isNumberColumn(columnInfoList, idx)) {
      return false;
    }
    if (indexIdx != -1) {
      if (isCoordinateVariableColumn(columnInfoList, idx)) {
        return false;
      }
    } else {
      String[] yIndexNames = getNames(nodeMap, new String[] {KEY_Y_INDEX_VARIABLE_NAME}, groupName);
      if (yIndexNames != null) {
        if (yIndexNames.length != 1) {
          return false;
        }
        idx = setNetCDFColumnType(columnInfoList, columnTypes, yIndexNames[0], Y_INDEX);
        if (idx == -1) {
          return false;
        }
        if (!isNumberColumn(columnInfoList, idx)) {
          return false;
        }
        if (!isCoordinateVariableColumn(columnInfoList, idx)) {
          return false;
        }
      } else {
        if (!isCoordinateVariableColumn(columnInfoList, idx)) {
          return false;
        }
      }
    }

    // the first and the second component
    final String first = SGDataStrideUtility.getVXYFirstComponentColumnType(infoMap);
    final String second = SGDataStrideUtility.getVXYSecondComponentColumnType(infoMap);

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
    idx = setNetCDFColumnType(columnInfoList, columnTypes, fNames[0], first);
    if (idx == -1) {
      return false;
    }
    if (!isNumberColumn(columnInfoList, idx)) {
      return false;
    }
    if (isCoordinateVariableColumn(columnInfoList, idx)) {
      return false;
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
    idx = setNetCDFColumnType(columnInfoList, columnTypes, sNames[0], second);
    if (idx == -1) {
      return false;
    }
    if (!isNumberColumn(columnInfoList, idx)) {
      return false;
    }
    if (isCoordinateVariableColumn(columnInfoList, idx)) {
      return false;
    }

    // time
    String[] timeNames = getNames(nodeMap, KEY_TIME_VARIABLE_NAME, groupName);
    if (timeNames != null) {
      if (timeNames.length != 1) {
        return false;
      }
      idx = setNetCDFColumnType(columnInfoList, columnTypes, timeNames[0], ANIMATION_FRAME);
      if (idx == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, idx)) {
        return false;
      }
      if (!isCoordinateVariableColumn(columnInfoList, idx)) {
        return false;
      }
    }

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  static boolean getForVXYNetCDFDataIndex(
      final Map<String, Object> infoMap,
      final List<SGNetCDFVariable> varList,
      final int size,
      final SGDataColumnInfo[] columns,
      List<Integer> serialNumberIndices) {

    List<SGNetCDFVariable> varListSorted = sortVariableList(varList, serialNumberIndices);

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    int iIndex = -1;
    int xIndex = -1;
    int yIndex = -1;
    int fIndex = -1;
    int sIndex = -1;
    for (int ii = 0; ii < varListSorted.size(); ii++) {
      SGNetCDFVariable var = varListSorted.get(ii);
      if (var.isCoordinateVariable()) {
        Dimension cDim = var.getDimension(0);
        for (int jj = 0; jj < varListSorted.size(); jj++) {
          if (jj == ii) {
            continue;
          }
          SGNetCDFVariable v = varListSorted.get(jj);
          if (!VALUE_TYPE_NUMBER.equals(v.getValueType())) {
            continue;
          }
          List<Dimension> dimList = v.getDimensions();
          if (!dimList.contains(cDim)) {
            continue;
          }
          if (xIndex == -1) {
            xIndex = jj;
          } else if (yIndex == -1) {
            yIndex = jj;
          } else if (fIndex == -1) {
            fIndex = jj;
          } else if (sIndex == -1) {
            sIndex = jj;
          }
        }
        if (xIndex != -1 && yIndex != -1 && fIndex != -1 && sIndex != -1) {
          // checks overlapping
          Set<Integer> indexSet = new HashSet<Integer>();
          indexSet.add(xIndex);
          indexSet.add(yIndex);
          indexSet.add(fIndex);
          indexSet.add(sIndex);
          if (indexSet.size() == 4) {
            iIndex = ii;
            break;
          }
        }

        // clears indices
        xIndex = -1;
        yIndex = -1;
        fIndex = -1;
        sIndex = -1;
      }
    }
    if (iIndex == -1 || xIndex == -1 || yIndex == -1 || fIndex == -1 || sIndex == -1) {
      return false;
    }

    int iCol = findColumnIndex(columns, varListSorted.get(iIndex));
    int xCol = findColumnIndex(columns, varListSorted.get(xIndex));
    int yCol = findColumnIndex(columns, varListSorted.get(yIndex));
    int fCol = findColumnIndex(columns, varListSorted.get(fIndex));
    int sCol = findColumnIndex(columns, varListSorted.get(sIndex));
    if (iCol == -1 || xCol == -1 || yCol == -1 || fCol == -1 || sCol == -1) {
      return false;
    }

    columnTypes[iCol] = INDEX;
    columnTypes[xCol] = X_COORDINATE;
    columnTypes[yCol] = Y_COORDINATE;
    columnTypes[fCol] = SGDataStrideUtility.getVXYFirstComponentColumnType(infoMap);
    columnTypes[sCol] = SGDataStrideUtility.getVXYSecondComponentColumnType(infoMap);

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  static boolean getForVXYNetCDFDataNormal(
      final Map<String, Object> infoMap,
      final List<SGNetCDFVariable> varList,
      final int size,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    int xIndex = -1;
    int yIndex = -1;
    int tIndex = -1;

    // a coordinate variable found first is assigned to the x variable
    // and found next is assigned to the y variable
    SGNetCDFVariable xVar = null;
    SGNetCDFVariable yVar = null;

    int startIndex = 0;
    int comIndex1 = -1;
    int comIndex2 = -1;
    List<Dimension> dimListCom1 = null;
    List<Dimension> dimListCom2 = null;

    for (int ii = 0; ii < varList.size() - 3; ii++) {
      int[] xyIndex = getNetCDFCoordinateVariableXYIndex(varList, startIndex);
      if (xyIndex == null) {
        startIndex += 1;
        continue;
      } else {
        xIndex = xyIndex[0];
        yIndex = xyIndex[1];
        xVar = varList.get(xIndex);
        yVar = varList.get(yIndex);
      }

      Dimension xDim = xVar.getDimension(0);
      Dimension yDim = yVar.getDimension(0);

      // variables for two components must have dimensions
      // those of x and y variables
      for (int jj = 0; jj < size; jj++) {
        SGNetCDFVariable var = varList.get(jj);
        if (!var.isCoordinateVariable() && VALUE_TYPE_NUMBER.equals(var.getValueType())) {
          List<Dimension> dimList = var.getDimensions();
          if (dimList.contains(xDim) && dimList.contains(yDim)) {
            comIndex1 = jj;
            dimListCom1 = dimList;
            break;
          }
        }
      }
      if (comIndex1 == -1 || comIndex1 == size - 1) {
        startIndex += 1;
        continue;
      }

      for (int jj = comIndex1 + 1; jj < size; jj++) {
        SGNetCDFVariable var = varList.get(jj);
        if (!var.isCoordinateVariable() && VALUE_TYPE_NUMBER.equals(var.getValueType())) {
          List<Dimension> dimList = var.getDimensions();
          if (dimList.contains(xDim) && dimList.contains(yDim)) {
            comIndex2 = jj;
            dimListCom2 = dimList;
            break;
          }
        }
      }
      if (comIndex2 != -1) {
        break;
      } else {
        startIndex += 1;
      }
    }

    if (comIndex1 == -1 || comIndex2 == -1) {
      return false;
    } else {
      dimListCom1 = varList.get(comIndex1).getDimensions();
      dimListCom2 = varList.get(comIndex2).getDimensions();
    }

    // unlimited coordinate variable is assigned to time variable
    // if it exists
    for (int ii = 0; ii < size; ii++) {
      if (ii == xIndex || ii == yIndex || ii == comIndex1 || ii == comIndex2) {
        continue;
      }
      SGNetCDFVariable var = varList.get(ii);
      if (var.isUnlimited() && var.isCoordinateVariable()) {
        Dimension dim = var.getDimension(0);
        if (dimListCom1.contains(dim) && dimListCom2.contains(dim)) {
          tIndex = ii;
        }
        break;
      }
    }

    int xCol = findColumnIndex(columns, xVar);
    int yCol = findColumnIndex(columns, yVar);
    int com1Col = findColumnIndex(columns, varList.get(comIndex1));
    int com2Col = findColumnIndex(columns, varList.get(comIndex2));
    if (xCol == -1 || yCol == -1 || com1Col == -1 || com2Col == -1) {
      return false;
    }
    columnTypes[xCol] = X_COORDINATE;
    columnTypes[yCol] = Y_COORDINATE;
    columnTypes[com1Col] = SGDataStrideUtility.getVXYFirstComponentColumnType(infoMap);
    columnTypes[com2Col] = SGDataStrideUtility.getVXYSecondComponentColumnType(infoMap);
    if (tIndex != -1) {
      int tCol = findColumnIndex(columns, varList.get(tIndex));
      if (tCol != -1) {
        columnTypes[tCol] = ANIMATION_FRAME;
      }
    }

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  static boolean getForVXYNetCDFData(
      final Map<String, Object> infoMap,
      final List<SGNetCDFVariable> varList,
      final int size,
      final SGDataColumnInfo[] columns) {
    List<Integer> indices = getIndexVariableColumnIndices(columns);
    if (indices.size() > 0) {
      if (getForVXYNetCDFDataIndex(infoMap, varList, size, columns, indices)) {
        return true;
      }
      if (getForVXYNetCDFDataNormal(infoMap, varList, size, columns)) {
        return true;
      }
    } else {
      if (getForVXYNetCDFDataNormal(infoMap, varList, size, columns)) {
        return true;
      }
      if (getForVXYNetCDFDataIndex(infoMap, varList, size, columns, indices)) {
        return true;
      }
    }
    return false;
  }

  static boolean getForSXYNetCDFData(
      final List<SGDataColumnInfo> columnInfoList,
      final Map<String, Object> infoMap,
      final NamedNodeMap nodeMap,
      final String groupName,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    // serial numbers
    String[] indexNames = getNames(nodeMap, KEY_INDEX_VARIABLE_NAME, groupName);
    if (indexNames == null) {
      indexNames = getNames(nodeMap, KEY_SERIAL_NUMBER_VARIABLE_NAME, groupName);
    }
    int idxIndex = -1;
    if (indexNames != null) {
      if (indexNames.length != 1) {
        return false;
      }
      if ((idxIndex = setNetCDFColumnType(columnInfoList, columnTypes, indexNames[0], INDEX))
          == -1) {
        return false;
      }
      if (!isCoordinateVariableColumn(columnInfoList, idxIndex)) {
        return false;
      }
    }

    // x and y values
    String[] xNames =
        getNames(nodeMap, new String[] {KEY_X_VALUE_NAME, KEY_X_VALUE_COLUMN_INDEX}, groupName);
    if (xNames == null) {
      xNames =
          getNames(
              nodeMap, new String[] {KEY_X_VALUE_NAMES, KEY_X_VALUE_COLUMN_INDICES}, groupName);
      if (xNames == null) {
        return false;
      }
    }
    if (xNames.length == 0) {
      return false;
    }
    String[] yNames =
        getNames(nodeMap, new String[] {KEY_Y_VALUE_NAME, KEY_Y_VALUE_COLUMN_INDEX}, groupName);
    if (yNames == null) {
      yNames =
          getNames(
              nodeMap, new String[] {KEY_Y_VALUE_NAMES, KEY_Y_VALUE_COLUMN_INDICES}, groupName);
      if (yNames == null) {
        return false;
      }
    }
    if (yNames.length == 0) {
      return false;
    }
    if (xNames.length > 1 && yNames.length > 1) {
      return false;
    }
    List<Integer> xIndices = new ArrayList<Integer>();
    for (int ii = 0; ii < xNames.length; ii++) {
      final int index = setNetCDFColumnType(columnInfoList, columnTypes, xNames[ii], X_VALUE);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index) && !isDateColumn(columnInfoList, index)) {
        return false;
      }
      xIndices.add(index);
    }
    List<Integer> yIndices = new ArrayList<Integer>();
    for (int ii = 0; ii < yNames.length; ii++) {
      final int index = setNetCDFColumnType(columnInfoList, columnTypes, yNames[ii], Y_VALUE);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index) && !isDateColumn(columnInfoList, index)) {
        return false;
      }
      yIndices.add(index);
    }

    if (idxIndex != -1) {
      for (Integer xIndex : xIndices) {
        if (isCoordinateVariableColumn(columnInfoList, xIndex)) {
          return false;
        }
      }
      for (Integer yIndex : yIndices) {
        if (isCoordinateVariableColumn(columnInfoList, yIndex)) {
          return false;
        }
      }
    } else {
      Boolean xCoordinateVariable = null;
      for (Integer xIndex : xIndices) {
        final boolean b = isCoordinateVariableColumn(columnInfoList, xIndex);
        if (xCoordinateVariable == null) {
          xCoordinateVariable = b;
        } else {
          if (!xCoordinateVariable.equals(b)) {
            return false;
          }
        }
      }
      if (xCoordinateVariable == null) {
        return false;
      }
      for (Integer yIndex : yIndices) {
        final boolean b = isCoordinateVariableColumn(columnInfoList, yIndex);
        if (xCoordinateVariable.equals(b)) {
          return false;
        }
      }
    }

    // error values
    String[] leNames =
        getNames(
            nodeMap,
            new String[] {
              KEY_LOWER_ERROR_VALUE_NAME,
              KEY_LOWER_ERROR_VALUE_NAMES,
              KEY_LOWER_ERROR_BAR_COLUMN_INDICES
            },
            groupName);
    String[] ueNames =
        getNames(
            nodeMap,
            new String[] {
              KEY_UPPER_ERROR_VALUE_NAME,
              KEY_UPPER_ERROR_VALUE_NAMES,
              KEY_UPPER_ERROR_BAR_COLUMN_INDICES
            },
            groupName);
    String[] ehNames =
        getNames(
            nodeMap,
            new String[] {
              KEY_ERROR_BAR_HOLDER_NAME,
              KEY_ERROR_BAR_HOLDER_NAMES,
              KEY_ERROR_BAR_HOLDER_COLUMN_INDICES
            },
            groupName);
    if (leNames != null && ueNames != null && ehNames != null) {
      if (ehNames.length != leNames.length || ehNames.length != ueNames.length) {
        return false;
      }
      for (int ii = 0; ii < ehNames.length; ii++) {
        final int index = findNetCDFColumnInfo(columnInfoList, ehNames[ii]);
        if (index == -1) {
          return false;
        }
        if (!isNumberColumn(columnInfoList, index)) {
          return false;
        }
        if (isCoordinateVariableColumn(columnInfoList, index)) {
          return false;
        }
        if (!xIndices.contains(index) && !yIndices.contains(index)) {
          return false;
        }
      }
      for (int ii = 0; ii < ehNames.length; ii++) {
        if (leNames[ii].equals(ueNames[ii])) {
          final int index =
              setupNetCDFColumn(
                  columnInfoList, columnTypes, leNames[ii], ehNames[ii], LOWER_UPPER_ERROR_VALUE);
          if (index == -1) {
            return false;
          }
          if (!isNumberColumn(columnInfoList, index)) {
            return false;
          }
          if (isCoordinateVariableColumn(columnInfoList, index)) {
            return false;
          }
        } else {
          int index;
          index =
              setupNetCDFColumn(
                  columnInfoList, columnTypes, leNames[ii], ehNames[ii], LOWER_ERROR_VALUE);
          if (index == -1) {
            return false;
          }
          index =
              setupNetCDFColumn(
                  columnInfoList, columnTypes, ueNames[ii], ehNames[ii], UPPER_ERROR_VALUE);
          if (index == -1) {
            return false;
          }
          if (!isNumberColumn(columnInfoList, index)) {
            return false;
          }
          if (isCoordinateVariableColumn(columnInfoList, index)) {
            return false;
          }
        }
      }
    }

    // tick labels
    String[] tlNames =
        getNames(
            nodeMap,
            new String[] {KEY_TICK_LABEL_NAME, KEY_TICK_LABEL_NAMES, KEY_TICK_LABEL_COLUMN_INDICES},
            groupName);
    String[] thNames =
        getNames(
            nodeMap,
            new String[] {
              KEY_TICK_LABEL_HOLDER_NAME,
              KEY_TICK_LABEL_HOLDER_NAMES,
              KEY_TICK_LABEL_HOLDER_COLUMN_INDICES
            },
            groupName);
    if (tlNames != null && thNames != null) {
      if (tlNames.length != thNames.length) {
        return false;
      }
      for (int ii = 0; ii < thNames.length; ii++) {
        final int index = findNetCDFColumnInfo(columnInfoList, thNames[ii]);
        if (index == -1) {
          return false;
        }
        if (isCoordinateVariableColumn(columnInfoList, index)) {
          return false;
        }
        if (!xIndices.contains(index) && !yIndices.contains(index)) {
          return false;
        }
      }
      for (int ii = 0; ii < thNames.length; ii++) {
        final int index =
            setupNetCDFColumn(columnInfoList, columnTypes, tlNames[ii], thNames[ii], TICK_LABEL);
        if (index == -1) {
          return false;
        }
        if (isCoordinateVariableColumn(columnInfoList, index)) {
          return false;
        }
      }
    }

    // time
    String[] timeNames = getNames(nodeMap, KEY_TIME_VARIABLE_NAME, groupName);
    if (timeNames != null) {
      if (timeNames.length != 1) {
        return false;
      }
      final int index =
          setNetCDFColumnType(columnInfoList, columnTypes, timeNames[0], ANIMATION_FRAME);
      if (index == -1) {
        return false;
      }
      if (!isNumberColumn(columnInfoList, index)) {
        return false;
      }
      if (!isCoordinateVariableColumn(columnInfoList, index)) {
        return false;
      }
    }

    Boolean variable =
        (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE);
    if (variable != null) {
      if (!variable.booleanValue()) {
        // pick up
        String[] pickupNames = getNames(nodeMap, KEY_PICKUP_DIMENSION_NAME, groupName);
        if (pickupNames != null) {
          if (pickupNames.length != 1) {
            return false;
          }
          final int index =
              setNetCDFColumnType(columnInfoList, columnTypes, pickupNames[0], PICKUP);
          if (index == -1) {
            return false;
          }
          if (!isNumberColumn(columnInfoList, index)) {
            return false;
          }
          if (!isCoordinateVariableColumn(columnInfoList, index)) {
            return false;
          }
        }
      }
    }

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  static boolean getForSXYNetCDFDataIndex(
      final Map<String, Object> infoMap,
      final List<SGNetCDFVariable> varList,
      final int size,
      final SGDataColumnInfo[] columns,
      final List<Integer> serialNumberIndices) {

    List<SGNetCDFVariable> varListSorted = sortVariableList(varList, serialNumberIndices);

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    int iIndex = -1;
    int xIndex = -1;
    int yIndex = -1;
    for (int ii = 0; ii < varListSorted.size(); ii++) {
      SGNetCDFVariable var = varListSorted.get(ii);
      if (var.isCoordinateVariable()) {
        Dimension cDim = var.getDimension(0);
        for (int jj = 0; jj < varListSorted.size(); jj++) {
          if (jj == ii) {
            continue;
          }
          SGNetCDFVariable v = varListSorted.get(jj);
          if (!VALUE_TYPE_NUMBER.equals(v.getValueType())) {
            continue;
          }
          List<Dimension> dimList = v.getDimensions();
          if (!dimList.contains(cDim)) {
            continue;
          }
          if (xIndex == -1) {
            xIndex = jj;
          } else if (yIndex == -1) {
            yIndex = jj;
          }
        }
        if (xIndex != -1 && yIndex != -1) {
          if (xIndex != yIndex) {
            iIndex = ii;
            break;
          }
        }

        // clears indices
        xIndex = -1;
        yIndex = -1;
      }
    }
    if (iIndex == -1 || xIndex == -1 || yIndex == -1) {
      return false;
    }

    int iCol = findColumnIndex(columns, varListSorted.get(iIndex));
    int xCol = findColumnIndex(columns, varListSorted.get(xIndex));
    int yCol = findColumnIndex(columns, varListSorted.get(yIndex));
    if (iCol == -1 || xCol == -1 || yCol == -1) {
      return false;
    }

    columnTypes[iCol] = INDEX;
    columnTypes[xCol] = X_VALUE;
    columnTypes[yCol] = Y_VALUE;

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  private static List<SGNetCDFVariable> sortVariableList(
      final List<SGNetCDFVariable> varList, final List<Integer> serialNumberIndices) {
    List<SGNetCDFVariable> varListSorted = new ArrayList<SGNetCDFVariable>();
    for (int ii = 0; ii < serialNumberIndices.size(); ii++) {
      Integer index = serialNumberIndices.get(ii);
      varListSorted.add(varList.get(index));
    }
    for (int ii = 0; ii < varList.size(); ii++) {
      if (!serialNumberIndices.contains(ii)) {
        varListSorted.add(varList.get(ii));
      }
    }
    return varListSorted;
  }

  static boolean getForSXYNetCDFDataNormal(
      final Map<String, Object> infoMap,
      final List<SGNetCDFVariable> varList,
      final int size,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    // a coordinate variable found first is assigned to x variable
    int xIndex = -1;
    SGNetCDFVariable xVar = null;
    List<Integer> tempVarIndexList = new ArrayList<Integer>();

    // get unlimited variable
    for (int ii = 0; ii < size; ii++) {
      SGNetCDFVariable var = varList.get(ii);
      if (var.isUnlimited() && var.isCoordinateVariable()) {
        Dimension dim = var.getDimension(0);
        final int len = dim.getLength();
        if (len <= 1) {
          tempVarIndexList.add(ii);
          continue;
        }
        xIndex = ii;
        xVar = var;
        break;
      }
    }

    // if unlimited variable is not found, search the coordinate variable
    if (xVar == null) {
      for (int ii = 0; ii < size; ii++) {
        SGNetCDFVariable var = varList.get(ii);
        if (var.isCoordinateVariable()) {
          Dimension dim = var.getDimension(0);
          final int len = dim.getLength();
          if (len <= 1) {
            tempVarIndexList.add(ii);
            continue;
          }
          xIndex = ii;
          xVar = var;
          break;
        }
      }
    }

    // searches from temporary list
    if (xIndex == -1) {
      if (tempVarIndexList.size() > 0) {
        xIndex = tempVarIndexList.get(0);
        xVar = varList.get(xIndex);
      }
    }

    if (xIndex == -1 || xVar == null) {
      return false;
    }

    Boolean multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
    if (multiple == null) {
      return false;
    }

    Boolean variable =
        (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE);
    if (variable == null) {
      return false;
    }

    // y variable must not be a coordinate variable and it has a dimension
    // that is the same as that of the x variable
    Integer[] yIndices = null;
    Dimension xDim = xVar.getDimension(0);
    List<Integer> yList = new ArrayList<Integer>();
    for (int ii = 0; ii < size; ii++) {
      SGNetCDFVariable var = varList.get(ii);
      if (!var.isCoordinateVariable() && VALUE_TYPE_NUMBER.equals(var.getValueType())) {
        List<Dimension> dimList = var.getDimensions();
        if (dimList.contains(xDim)) {
          yList.add(ii);
        }
      }
    }
    if (yList.size() == 0) {
      return false;
    } else {
      yIndices = new Integer[yList.size()];
      for (int ii = 0; ii < yList.size(); ii++) {
        yIndices[ii] = yList.get(ii);
      }
    }

    int xCol = findColumnIndex(columns, xVar);
    if (xCol == -1) {
      return false;
    }

    // for multiple variables
    if (variable.booleanValue()) {
      columnTypes[xCol] = X_VALUE;

      if (multiple.booleanValue()) {
        // multiple y-indices
        for (int yIndex : yIndices) {
          int yCol = findColumnIndex(columns, varList.get(yIndex));
          if (yCol != -1) {
            columnTypes[yCol] = Y_VALUE;
          }
        }
      } else {
        // finds the y-index
        for (int ii = 0; ii < yIndices.length; ii++) {
          final int yIndex = yIndices[ii];
          SGNetCDFVariable var = varList.get(yIndex);
          if (VALUE_TYPE_NUMBER.equals(var.getValueType())) {
            int yCol = findColumnIndex(columns, var);
            if (yCol != -1) {
              columnTypes[yCol] = Y_VALUE;
            }
            break;
          }
        }
      }
    } else {
      // for multiple dimension indices
      final int yIndex = yIndices[0];
      int yCol = findColumnIndex(columns, varList.get(yIndex));
      if (yCol == -1) {
        return false;
      }
      columnTypes[xCol] = X_VALUE;
      columnTypes[yCol] = Y_VALUE;

      int dIndex = -1;
      final SGNetCDFVariable yVar = varList.get(yIndex);
      List<Dimension> yDimList = yVar.getDimensions();
      for (int ii = 0; ii < varList.size(); ii++) {
        SGNetCDFVariable var = varList.get(ii);
        if (var.isCoordinateVariable()) {
          Dimension dim = var.getDimension(0);
          if (!xDim.equals(dim) && yDimList.contains(dim)) {
            dIndex = ii;
            break;
          }
        }
      }
      if (dIndex != -1) {
        int dCol = findColumnIndex(columns, varList.get(dIndex));
        if (dCol != -1) {
          columnTypes[dCol] = PICKUP;
        }
      } else {
        return false;
      }
    }

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  static boolean getForSXYNetCDFData(
      final Map<String, Object> infoMap,
      final List<SGNetCDFVariable> varList,
      final int size,
      final SGDataColumnInfo[] columns) {
    List<Integer> indices = getIndexVariableColumnIndices(columns);
    if (indices.size() > 0) {
      if (getForSXYNetCDFDataIndex(infoMap, varList, size, columns, indices)) {
        return true;
      }
      if (getForSXYNetCDFDataNormal(infoMap, varList, size, columns)) {
        return true;
      }
    } else {
      if (getForSXYNetCDFDataNormal(infoMap, varList, size, columns)) {
        return true;
      }
      if (getForSXYNetCDFDataIndex(infoMap, varList, size, columns, indices)) {
        return true;
      }
    }
    return false;
  }
}
