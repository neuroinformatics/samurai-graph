package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeUtility.*;

import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import org.w3c.dom.NamedNodeMap;

/** Static helper for the SDArray default column types. */
public final class SGDefaultColumnTypeSDArrayUtility
    implements SGIDataColumnTypeConstants, SGIDataPropertyKeyConstants {

  private SGDefaultColumnTypeSDArrayUtility() {}

  private static boolean isRepeatedTitle(
      final List<SGDataColumnInfo> columnInfoList, final int colIndex) {
    String[] titles = new String[columnInfoList.size()];
    for (int ii = 0; ii < titles.length; ii++) {
      SGDataColumnInfo colInfo = columnInfoList.get(ii);
      titles[ii] = colInfo.getTitle();
    }
    if (null == titles[colIndex]) {
      return true;
    }
    for (int i = 0; i < titles.length; i++) {
      if (colIndex != i && titles[colIndex].equals(titles[i])) {
        return true;
      }
    }
    return false;
  }

  private static boolean isEmptyTitle(
      final List<SGDataColumnInfo> columnInfoList, final int colIndex) {
    SGDataColumnInfo colInfo = columnInfoList.get(colIndex);
    String title = colInfo.getTitle();
    if (title == null || "".equals(title)) {
      return true;
    }
    return false;
  }

  static boolean getForSXYZSDArrayData(
      final List<SGDataColumnInfo> columnInfoList,
      final Map<String, Object> infoMap,
      final NamedNodeMap nodeMap,
      final String groupName,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    // x, y and z values
    Integer[] xIndices = getIndices(nodeMap, KEY_X_VALUE_COLUMN_INDEX);
    if (xIndices == null) {
      return false;
    }
    if (xIndices.length != 1) {
      return false;
    }
    if (!checkIndices(xIndices, columnTypes)) {
      return false;
    }
    Integer[] yIndices = getIndices(nodeMap, KEY_Y_VALUE_COLUMN_INDEX);
    if (yIndices == null) {
      return false;
    }
    if (yIndices.length != 1) {
      return false;
    }
    if (!checkIndices(yIndices, columnTypes)) {
      return false;
    }
    Integer[] zIndices = getIndices(nodeMap, KEY_Z_VALUE_COLUMN_INDEX);
    if (zIndices == null) {
      return false;
    }
    if (zIndices.length != 1) {
      return false;
    }
    if (!checkIndices(zIndices, columnTypes)) {
      return false;
    }

    final int xIndex = xIndices[0].intValue();
    if (!VALUE_TYPE_NUMBER.equals(columns[xIndex].getValueType())) {
      return false;
    }
    columnTypes[xIndex] = X_VALUE;

    final int yIndex = yIndices[0].intValue();
    if (!VALUE_TYPE_NUMBER.equals(columns[yIndex].getValueType())) {
      return false;
    }
    columnTypes[yIndex] = Y_VALUE;

    final int zIndex = zIndices[0].intValue();
    if (!VALUE_TYPE_NUMBER.equals(columns[zIndex].getValueType())) {
      return false;
    }
    columnTypes[zIndex] = Z_VALUE;

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  static boolean getForSXYZSDArrayData(
      final List<SGDataColumnInfo> columnInfoList,
      final int len,
      final Map<String, Object> infoMap,
      List<Integer> numberIndexList,
      List<Integer> textIndexList,
      List<Integer> dateIndexList,
      List<Integer> samplingIndexList,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    if (numberIndexList.size() >= 3) {
      // x, y and z values
      Integer num1 = numberIndexList.get(0);
      Integer num2 = numberIndexList.get(1);
      Integer num3 = numberIndexList.get(2);
      columnTypes[num1.intValue()] = X_VALUE;
      columnTypes[num2.intValue()] = Y_VALUE;
      columnTypes[num3.intValue()] = Z_VALUE;
    } else {
      return false;
    }

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  static boolean getForVXYSDArrayData(
      final List<SGDataColumnInfo> columnInfoList,
      final Map<String, Object> infoMap,
      final NamedNodeMap nodeMap,
      final String groupName,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    Integer[] xIndices = getIndices(nodeMap, KEY_X_COORDINATE_COLUMN_INDEX);
    if (xIndices == null) {
      return false;
    }
    if (xIndices.length != 1) {
      return false;
    }
    if (!checkIndices(xIndices, columnTypes)) {
      return false;
    }
    Integer[] yIndices = getIndices(nodeMap, KEY_Y_COORDINATE_COLUMN_INDEX);
    if (yIndices == null) {
      return false;
    }
    if (yIndices.length != 1) {
      return false;
    }
    if (!checkIndices(yIndices, columnTypes)) {
      return false;
    }
    Integer[] fIndices = getIndices(nodeMap, KEY_FIRST_COMPONENT_COLUMN_INDEX);
    if (fIndices == null) {
      return false;
    }
    if (fIndices.length != 1) {
      return false;
    }
    if (!checkIndices(fIndices, columnTypes)) {
      return false;
    }
    Integer[] sIndices = getIndices(nodeMap, KEY_SECOND_COMPONENT_COLUMN_INDEX);
    if (sIndices == null) {
      return false;
    }
    if (sIndices.length != 1) {
      return false;
    }
    if (!checkIndices(sIndices, columnTypes)) {
      return false;
    }

    final int xIndex = xIndices[0].intValue();
    if (!VALUE_TYPE_NUMBER.equals(columns[xIndex].getValueType())) {
      return false;
    }
    columnTypes[xIndex] = X_COORDINATE;

    final int yIndex = yIndices[0].intValue();
    if (!VALUE_TYPE_NUMBER.equals(columns[yIndex].getValueType())) {
      return false;
    }
    columnTypes[yIndex] = Y_COORDINATE;

    final int fIndex = fIndices[0].intValue();
    if (!VALUE_TYPE_NUMBER.equals(columns[fIndex].getValueType())) {
      return false;
    }
    columnTypes[fIndex] = SGDataStrideUtility.getVXYFirstComponentColumnType(infoMap);

    final int sIndex = sIndices[0].intValue();
    if (!VALUE_TYPE_NUMBER.equals(columns[sIndex].getValueType())) {
      return false;
    }
    columnTypes[sIndex] = SGDataStrideUtility.getVXYSecondComponentColumnType(infoMap);

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  static boolean getForVXYSDArrayData(
      final List<SGDataColumnInfo> columnInfoList,
      final int len,
      final Map<String, Object> infoMap,
      List<Integer> numberIndexList,
      List<Integer> textIndexList,
      List<Integer> dateIndexList,
      List<Integer> samplingIndexList,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    if (numberIndexList.size() >= 4) {
      Integer num1 = numberIndexList.get(0);
      Integer num2 = numberIndexList.get(1);
      Integer num3 = numberIndexList.get(2);
      Integer num4 = numberIndexList.get(3);
      columnTypes[num1.intValue()] = X_COORDINATE;
      columnTypes[num2.intValue()] = Y_COORDINATE;
      columnTypes[num3.intValue()] = SGDataStrideUtility.getVXYFirstComponentColumnType(infoMap);
      columnTypes[num4.intValue()] = SGDataStrideUtility.getVXYSecondComponentColumnType(infoMap);
    } else {
      return false;
    }

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  static boolean getForSXYSDArrayDataTickLabel(
      final List<SGDataColumnInfo> columnInfoList,
      final Map<String, Object> infoMap,
      final NamedNodeMap nodeMap,
      final String groupName,
      final String[] columnTypes,
      final SGDataColumnInfo[] columns) {
    Integer[] tlIndices = getIndices(nodeMap, KEY_TICK_LABEL_COLUMN_INDICES);
    Integer[] thIndices = getIndices(nodeMap, KEY_TICK_LABEL_HOLDER_COLUMN_INDICES);
    if (tlIndices != null && thIndices != null) {
      if (!checkIndices(tlIndices, columnTypes)) {
        return false;
      }
      if (!checkIndices(thIndices, columnTypes)) {
        return false;
      }
      if (tlIndices.length != thIndices.length) {
        return false;
      }
      for (int ii = 0; ii < thIndices.length; ii++) {
        final int thIndex = thIndices[ii].intValue();
        SGDataColumnInfo thColInfo = columnInfoList.get(thIndex);
        final boolean b =
            isEmptyTitle(columnInfoList, thIndex) || isRepeatedTitle(columnInfoList, thIndex);
        final String title = thColInfo.getTitle();
        columnTypes[tlIndices[ii]] =
            SGDataColumnTitleUtility.appendColumnNoOrTitle(TICK_LABEL, thIndex, b, title);
      }
    }
    return true;
  }

  static boolean getForSXYSDArrayDataErrorBar(
      final List<SGDataColumnInfo> columnInfoList,
      final Map<String, Object> infoMap,
      final NamedNodeMap nodeMap,
      final String groupName,
      final String[] columnTypes,
      final SGDataColumnInfo[] columns) {

    Integer[] leIndices = getIndices(nodeMap, KEY_LOWER_ERROR_BAR_COLUMN_INDICES);
    Integer[] ueIndices = getIndices(nodeMap, KEY_UPPER_ERROR_BAR_COLUMN_INDICES);
    Integer[] ehIndices = getIndices(nodeMap, KEY_ERROR_BAR_HOLDER_COLUMN_INDICES);
    if (leIndices != null && ueIndices != null && ehIndices != null) {
      if (!checkIndices(leIndices, columnTypes)) {
        return false;
      }
      if (!checkIndices(ueIndices, columnTypes)) {
        return false;
      }
      if (!checkIndices(ehIndices, columnTypes)) {
        return false;
      }
      if (ehIndices.length != leIndices.length || ehIndices.length != ueIndices.length) {
        return false;
      }
      for (int ii = 0; ii < ehIndices.length; ii++) {
        final int ehIndex = ehIndices[ii].intValue();
        SGDataColumnInfo ehColInfo = columnInfoList.get(ehIndex);
        final boolean b =
            isEmptyTitle(columnInfoList, ehIndex) || isRepeatedTitle(columnInfoList, ehIndex);
        final String title = ehColInfo.getTitle();
        final int leIndex = leIndices[ii].intValue();
        final int ueIndex = ueIndices[ii].intValue();
        if (!VALUE_TYPE_NUMBER.equals(columns[leIndex].getValueType())) {
          continue;
        }
        if (!VALUE_TYPE_NUMBER.equals(columns[ueIndex].getValueType())) {
          continue;
        }
        if (leIndex == ueIndex) {
          columnTypes[leIndex] =
              SGDataColumnTitleUtility.appendColumnNoOrTitle(
                  LOWER_UPPER_ERROR_VALUE, ehIndex, b, title);
        } else {
          columnTypes[leIndex] =
              SGDataColumnTitleUtility.appendColumnNoOrTitle(LOWER_ERROR_VALUE, ehIndex, b, title);
          columnTypes[ueIndex] =
              SGDataColumnTitleUtility.appendColumnNoOrTitle(UPPER_ERROR_VALUE, ehIndex, b, title);
        }
      }
    }
    return true;
  }

  static boolean getForSXYSDArrayData(
      final List<SGDataColumnInfo> columnInfoList,
      final Map<String, Object> infoMap,
      final NamedNodeMap nodeMap,
      final String groupName,
      final SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    // x and y values
    Integer[] xIndices = getIndices(nodeMap, KEY_X_VALUE_COLUMN_INDICES);
    if (xIndices == null) {
      return false;
    }
    if (xIndices.length == 0) {
      return false;
    }
    if (!checkIndices(xIndices, columnTypes)) {
      return false;
    }
    Integer[] yIndices = getIndices(nodeMap, KEY_Y_VALUE_COLUMN_INDICES);
    if (yIndices == null) {
      return false;
    }
    if (yIndices.length == 0) {
      return false;
    }
    if (!checkIndices(yIndices, columnTypes)) {
      return false;
    }
    if (xIndices.length > 1 && yIndices.length > 1) {
      return false;
    }
    int xCnt = 0;
    for (int ii = 0; ii < xIndices.length; ii++) {
      final int index = xIndices[ii].intValue();
      final String valueType = columns[index].getValueType();
      if (isSXYSDArrayNumberValueType(valueType)) {
        columnTypes[index] = X_VALUE;
        xCnt++;
      }
    }
    if (xCnt == 0) {
      return false;
    }
    int yCnt = 0;
    for (int ii = 0; ii < yIndices.length; ii++) {
      final int index = yIndices[ii].intValue();
      final String valueType = columns[index].getValueType();
      if (isSXYSDArrayNumberValueType(valueType)) {
        columnTypes[index] = Y_VALUE;
        yCnt++;
      }
    }
    if (yCnt == 0) {
      return false;
    }
    if (xCnt > 1 && yCnt > 1) {
      return false;
    }

    // error values
    getForSXYSDArrayDataErrorBar(columnInfoList, infoMap, nodeMap, groupName, columnTypes, columns);

    // tick labels
    getForSXYSDArrayDataTickLabel(
        columnInfoList, infoMap, nodeMap, groupName, columnTypes, columns);

    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii].setColumnType(columnTypes[ii]);
    }

    return true;
  }

  private static boolean isSXYSDArrayNumberValueType(String valueType) {
    return (VALUE_TYPE_NUMBER.equals(valueType)
        || VALUE_TYPE_DATE.equals(valueType)
        || VALUE_TYPE_SAMPLING_RATE.equals(valueType));
  }

  static boolean getForSXYSDArrayData(
      final List<SGDataColumnInfo> columnInfoList,
      final int len,
      final Map<String, Object> infoMap,
      List<Integer> numberIndexList,
      List<Integer> textIndexList,
      List<Integer> dateIndexList,
      List<Integer> samplingIndexList,
      SGDataColumnInfo[] columns) {

    String[] columnTypes = new String[columns.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = "";
    }

    if (samplingIndexList.size() > 1) {
      return false;
    }

    Double samplingRate = (Double) infoMap.get(SGIDataInformationKeyConstants.KEY_SAMPLING_RATE);
    Boolean multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
    if (multiple == null) {
      return false;
    }

    final boolean dateColumnUsed =
        (dateIndexList.size() > 0 && dateIndexList.contains(Integer.valueOf(0)));
    //		boolean dateColumnFound = false;
    //		// when a date column is found first,
    //		// the date column is used for x values
    //		for (int ii = 0; ii < len; ii++) {
    //			SGDataColumnInfo cInfo = (SGDataColumnInfo) columnInfoList.get(ii);
    //			String valueType = cInfo.getValueType();
    //			if (VALUE_TYPE_DATE.equals(valueType)) {
    //				dateColumnFound = true;
    //				break;
    //			}
    //		}

    if (multiple.booleanValue()) {
      if (samplingRate != null) {
        if (numberIndexList.size() >= 1) {
          for (int ii = 0; ii < numberIndexList.size(); ii++) {
            Integer num = numberIndexList.get(ii);
            columnTypes[num.intValue()] = Y_VALUE;
          }
        } else {
          return false;
        }
        if (samplingIndexList.size() == 1) {
          Integer num = samplingIndexList.get(0);
          columnTypes[num.intValue()] = X_VALUE;
        }
      } else if (dateColumnUsed) {
        if (numberIndexList.size() >= 1) {
          for (int ii = 0; ii < numberIndexList.size(); ii++) {
            Integer num = numberIndexList.get(ii);
            columnTypes[num.intValue()] = Y_VALUE;
          }
          Integer date0 = dateIndexList.get(0);
          columnTypes[date0.intValue()] = X_VALUE;
        } else {
          return false;
        }
      } else {
        if (numberIndexList.size() >= 2) {
          Integer num1 = numberIndexList.get(0);
          columnTypes[num1.intValue()] = X_VALUE;
          for (int ii = 1; ii < numberIndexList.size(); ii++) {
            Integer num = numberIndexList.get(ii);
            columnTypes[num.intValue()] = Y_VALUE;
          }
        } else {
          return false;
        }
      }

    } else {

      final int minNumberSize;
      if (samplingRate != null) {
        minNumberSize = 1;
      } else {
        if (dateColumnUsed) {
          minNumberSize = 1;
        } else {
          minNumberSize = 2;
        }
      }
      final int yNumberArrayIndex = minNumberSize - 1;

      // get the column index of y index
      int yNumberColumnIndex = -1;
      int cnt = 0;
      for (int ii = 0; ii < len; ii++) {
        SGDataColumnInfo cInfo = columnInfoList.get(ii);
        String valueType = cInfo.getValueType();
        if (VALUE_TYPE_NUMBER.equals(valueType)) {
          if (cnt == yNumberArrayIndex) {
            yNumberColumnIndex = ii;
            break;
          }
          cnt++;
        }
      }
      if (yNumberColumnIndex == -1) {
        return false;
      }

      if (numberIndexList.size() >= minNumberSize) {
        boolean dateAssigned = false;
        if (samplingRate != null) {
          if (samplingIndexList.size() == 1) {
            Integer num = samplingIndexList.get(0);
            columnTypes[num.intValue()] = X_VALUE;
          }
        } else {
          // x values
          if (dateIndexList.size() != 0 && dateColumnUsed) {
            Integer num1 = dateIndexList.get(0);
            columnTypes[num1.intValue()] = X_VALUE;
            dateAssigned = true;
          } else {
            Integer num1 = numberIndexList.get(0);
            columnTypes[num1.intValue()] = X_VALUE;
          }
        }

        // y values
        Integer num2 = numberIndexList.get(yNumberArrayIndex);
        columnTypes[num2.intValue()] = Y_VALUE;

        boolean[] isRepeatedTitle =
            SGDataMiscUtility.isEmptyOrRepeatedColumnTitle(
                columnInfoList.toArray(new SGDataColumnInfo[len]));

        // error bars
        if (numberIndexList.size() >= minNumberSize + 2) {
          Integer num3 = numberIndexList.get(yNumberArrayIndex + 1);
          Integer num4 = numberIndexList.get(yNumberArrayIndex + 2);
          columnTypes[num3.intValue()] =
              SGDataColumnTitleUtility.appendColumnNoOrTitle(
                  LOWER_ERROR_VALUE,
                  yNumberColumnIndex,
                  isRepeatedTitle[yNumberColumnIndex],
                  columnInfoList.get(yNumberColumnIndex).getTitle());
          columnTypes[num4.intValue()] =
              SGDataColumnTitleUtility.appendColumnNoOrTitle(
                  UPPER_ERROR_VALUE,
                  yNumberColumnIndex,
                  isRepeatedTitle[yNumberColumnIndex],
                  columnInfoList.get(yNumberColumnIndex).getTitle());
        }

        // tick labels
        if (textIndexList.size() > 0) {
          Integer tick = textIndexList.get(0);
          columnTypes[tick.intValue()] =
              SGDataColumnTitleUtility.appendColumnNoOrTitle(
                  TICK_LABEL,
                  yNumberColumnIndex,
                  isRepeatedTitle[yNumberColumnIndex],
                  columnInfoList.get(yNumberColumnIndex).getTitle());
        } else if (dateIndexList.size() > 0 && !dateAssigned) {
          Integer date = dateIndexList.get(0);
          columnTypes[date.intValue()] =
              SGDataColumnTitleUtility.appendColumnNoOrTitle(
                  TICK_LABEL,
                  yNumberColumnIndex,
                  isRepeatedTitle[yNumberColumnIndex],
                  columnInfoList.get(yNumberColumnIndex).getTitle());
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
}
