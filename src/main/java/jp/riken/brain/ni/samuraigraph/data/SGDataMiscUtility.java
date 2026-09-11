package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDataBufferUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnInfoUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTitleUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataRangeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataStrideUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataTextUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataViewerUtility.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import jp.riken.brain.ni.samuraigraph.base.SGCSVTokenizer.Token;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants.OPERATION;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/** Static helper for the Misc responsibility. */
public final class SGDataMiscUtility
    implements SGIDataColumnTypeConstants,
        SGIDataPropertyKeyConstants,
        SGINetCDFConstants,
        SGIMDArrayConstants {
  // Column type candidate lists and validation messages.
  static final String[] ARRAY_EMPTY = {""};

  static final String[] ARRAY_NUMBER_SDARRAY_SXYZ = {"", X_VALUE, Y_VALUE, Z_VALUE};

  static final String[] ARRAY_NUMBER_NETCDF_SXYZ_ALL = {"", X_VALUE, Y_VALUE};

  static final String[] ARRAY_NUMBER_MDARRAY_SXYZ = {"", X_VALUE, Y_VALUE, Z_VALUE};

  static final String[] ARRAY_NUMBER_SDARRAY_VXY_POLAR = {
    "", X_COORDINATE, Y_COORDINATE, MAGNITUDE, ANGLE
  };

  static final String[] ARRAY_NUMBER_SDARRAY_VXY_ORTHOGONAL = {
    "", X_COORDINATE, Y_COORDINATE, X_COMPONENT, Y_COMPONENT
  };

  static final String[] ARRAY_NUMBER_NETCDF_VXY_POLAR_ALL = {"", X_COORDINATE, Y_COORDINATE};

  static final String[] ARRAY_NUMBER_NETCDF_VXY_ORTHOGONAL_ALL = {"", X_COORDINATE, Y_COORDINATE};

  static final String[] ARRAY_NUMBER_MDARRAY_VXY_POLAR = {
    "", X_COORDINATE, Y_COORDINATE, MAGNITUDE, ANGLE
  };

  static final String[] ARRAY_NUMBER_MDARRAY_VXY_ORTHOGONAL = {
    "", X_COORDINATE, Y_COORDINATE, X_COMPONENT, Y_COMPONENT
  };

  public static final String MSG_PROPER_COLUMN_TYPE = "Select proper column types.";

  public static final String MSG_PROPER_STRIDE = "Input proper values for the stride.";

  public static final String MSG_PROPER_STRIDE_LINE_AND_BAR =
      "Input proper values for the stride of Line and Bar.";

  public static final String MSG_PROPER_STRIDE_TICK_LABEL =
      "Input proper values for the stride of Tick Label.";

  public static final String MSG_PROPER_STRIDE_INDEX =
      "Input proper values for the stride of Index.";

  public static final String MSG_PROPER_STRIDE_X = "Input proper values for the stride of X.";

  public static final String MSG_PROPER_STRIDE_Y = "Input proper values for the stride of Y.";

  public static final String MSG_PROPER_PICK_UP_INDICES =
      "Input proper values for Pick Up indices.";

  public static final String MSG_UNIQUE_DIMENSIONS = "Select unique dimensions.";

  public static final String MSG_DIMENSIONS_SAME_LENGTH = "Select dimensions with the same length.";

  public static final String MSG_DIMENSIONS_SAME_LENGTH_ANIMATION_FRAME =
      "Select dimensions with the same length for Animation Frame.";

  public static final String MSG_DIMENSIONS_SAME_LENGTH_PICK_UP =
      "Select dimensions with the same length for Pick Up.";

  public static final String MSG_DIMENSION_AND_COLUMN_TYPE_PICK_UP =
      "Select proper dimension and column type for Pick Up.";

  private SGDataMiscUtility() {}

  private static String[] updateNetCDFItems(
      List<String> itemList,
      Map<String, Object> infoMap,
      String dataType,
      String valueType,
      String[] cOptions,
      String[] options) {

    // current row index
    Integer rowIndex = (Integer) infoMap.get(SGIDataInformationKeyConstants.KEY_CURRENT_ROW_INDEX);

    // all columns
    SGDataColumnInfo[] colInfo =
        (SGDataColumnInfo[]) infoMap.get(SGIDataInformationKeyConstants.KEY_COLUMN_INFO);

    // add time, pickup and serial number columns
    final boolean isNetCDFData = isNetCDFData(dataType);
    if (isNetCDFData && VALUE_TYPE_NUMBER.equals(valueType)) {
      SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) colInfo[rowIndex];
      if (ncInfo.isCoordinateVariable()) {
        for (String op : cOptions) {
          itemList.add(op);
        }
      } else {
        for (String op : options) {
          itemList.add(op);
        }
      }
    }

    String[] ret = itemList.toArray(new String[itemList.size()]);
    return ret;
  }

  private static String[] updateNetCDFItems(
      String[] items,
      Map<String, Object> infoMap,
      String dataType,
      String valueType,
      String[] cOptions,
      String[] options) {
    List<String> itemList = new ArrayList<String>();
    for (String item : items) {
      itemList.add(item);
    }
    return updateNetCDFItems(itemList, infoMap, dataType, valueType, cOptions, options);
  }

  public static String getDataColumnTypeCommand(List<String> varList, List<String> columnTypeList) {

    if (varList.size() != columnTypeList.size()) {
      throw new IllegalArgumentException("varList.size() != columnTypeList.size()");
    }
    StringBuilder sb = new StringBuilder();
    sb.append('(');
    for (int ii = 0; ii < varList.size(); ii++) {
      if (ii > 0) {
        sb.append(',');
      }
      String varName = varList.get(ii);
      String columnType = columnTypeList.get(ii);
      sb.append(varName);
      sb.append(':');
      sb.append(columnType);
    }
    sb.append(')');
    return sb.toString();
  }

  public static boolean hasValidHDF5CharacterForWin(final String str) {
    char[] cArray = str.toCharArray();
    for (int ii = 0; ii < cArray.length; ii++) {
      final char c = cArray[ii];
      if (!isAcceptableCharHDF5Wind(c)) {
        return false;
      }
    }
    return true;
  }

  public static boolean isAcceptableCharHDF5Wind(final char c) {
    return (c >= '\u0020' && c <= '\u007e');
  }

  public static void disposeSXYDataArray(SGISXYTypeSingleData[] sxyArray) {
    for (int ii = 0; ii < sxyArray.length; ii++) {
      sxyArray[ii].dispose();
    }
  }

  public static boolean isArchiveDataSetOperation(OPERATION mode) {
    return (OPERATION.SAVE_TO_ARCHIVE_DATA_SET.equals(mode)
        || OPERATION.SAVE_TO_ARCHIVE_DATA_SET_107.equals(mode));
  }

  /**
   * Creates and returns an array of text strings in canonical format for the column type from an
   * array of input text strings.
   *
   * @param columnTypes an array of input text strings
   * @return an array of text strings in canonical format for the column type
   */
  public static String[] getCanonicalColumnTypes(String[] columnTypes) {
    String[] allColumnTypes = {
      X_VALUE,
      Y_VALUE,
      Z_VALUE,
      LOWER_ERROR_VALUE,
      UPPER_ERROR_VALUE,
      LOWER_UPPER_ERROR_VALUE,
      TICK_LABEL,
      X_COORDINATE,
      Y_COORDINATE,
      X_COMPONENT,
      Y_COMPONENT,
      MAGNITUDE,
      ANGLE,
      ANIMATION_FRAME,
      TIME,
      PICKUP,
      INDEX,
      SERIAL_NUMBERS,
      X_INDEX,
      Y_INDEX
    };
    String[] retColumnTypes = new String[columnTypes.length];
    for (int ii = 0; ii < retColumnTypes.length; ii++) {
      String retStr = columnTypes[ii];
      for (int jj = 0; jj < allColumnTypes.length; jj++) {
        String ref = allColumnTypes[jj];
        if (SGUtilityText.isEqualString(retStr, ref)) {
          retStr = ref;
          break;
        }
      }
      retColumnTypes[ii] = retStr;
    }
    return retColumnTypes;
  }

  /**
   * Adds the grid type of data to the information map.
   *
   * @param colInfoArray an array of data column information
   * @param dataType data type
   * @param infoMap the information map
   * @return true if succeeded
   */
  public static boolean addGridType(
      SGDataColumnInfo[] colInfoArray, String dataType, Map<String, Object> infoMap) {
    if (!SGDataDataTypeUtility.isMDArrayData(dataType)) {
      return false;
    }
    if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
      List<SGDataColumnInfo> zList =
          SGDataColumnInfoUtility.findColumnsWithColumnType(
              colInfoArray, SGIDataColumnTypeConstants.Z_VALUE);
      if (zList.size() != 1) {
        return false;
      }
      SGMDArrayDataColumnInfo zInfo = (SGMDArrayDataColumnInfo) zList.get(0);
      Integer xDim = zInfo.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION);
      Integer yDim = zInfo.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION);
      Boolean grid =
          SGDataDataTypeUtility.isValidDimensionIndex(xDim)
              && SGDataDataTypeUtility.isValidDimensionIndex(yDim);
      infoMap.put(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, grid);
    } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
      Boolean polar = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED);
      if (polar == null) {
        return false;
      }
      String colName =
          polar ? SGIDataColumnTypeConstants.MAGNITUDE : SGIDataColumnTypeConstants.X_COMPONENT;
      List<SGDataColumnInfo> fList =
          SGDataColumnInfoUtility.findColumnsWithColumnType(colInfoArray, colName);
      if (fList.size() != 1) {
        return false;
      }
      SGMDArrayDataColumnInfo fInfo = (SGMDArrayDataColumnInfo) fList.get(0);
      Integer xDim = fInfo.getDimensionIndex(SGIMDArrayConstants.KEY_VXY_X_DIMENSION);
      Integer yDim = fInfo.getDimensionIndex(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION);
      Boolean grid =
          SGDataDataTypeUtility.isValidDimensionIndex(xDim)
              && SGDataDataTypeUtility.isValidDimensionIndex(yDim);
      infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, grid);
    } else {
      return false;
    }
    return true;
  }

  /**
   * Returns the grid plot flag.
   *
   * @param dataType the type of data
   * @param infoMap information map
   * @return the grid plot flag
   */
  public static Boolean isGridPlot(String dataType, Map<String, Object> infoMap) {
    Boolean ret = null;
    if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
      Boolean gridPlot =
          (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG);
      final boolean b;
      if (gridPlot != null) {
        b = gridPlot.booleanValue();
      } else {
        SGIntegerSeriesSet indexStride =
            (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE);
        b = (indexStride == null);
      }
      ret = b;
    } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
      Boolean gridPlot =
          (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG);
      final boolean b;
      if (gridPlot != null) {
        b = gridPlot.booleanValue();
      } else {
        SGIntegerSeriesSet indexStride =
            (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE);
        b = (indexStride == null);
      }
      ret = b;
    }
    return ret;
  }

  /**
   * Dump map for debug.
   *
   * @param infoMap
   */
  public static void dumpInfoMap(final Map<String, Object> infoMap) {
    // debug method
  }

  private static String[] getComplementedColumnType(
      final Map<String, Object> infoMap,
      final String[] curColType,
      final List<SGDataColumnInfo> colInfoList,
      final Integer singleIndex,
      final String singleColumnType,
      final String multiColumnType) {

    String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
    String[] compColType = new String[curColType.length];
    compColType[singleIndex.intValue()] = singleColumnType;
    if (isSDArrayData(dataType)) {
      for (int ii = 0; ii < colInfoList.size(); ii++) {
        if (ii == singleIndex.intValue()) {
          continue;
        }
        SGDataColumnInfo colInfo = colInfoList.get(ii);
        String valueType = colInfo.getValueType();
        if (VALUE_TYPE_NUMBER.equals(valueType)) {
          compColType[ii] = multiColumnType;
        } else {
          compColType[ii] = curColType[ii];
        }
      }
    } else if (isNetCDFData(dataType)) {
      SGNetCDFDataColumnInfo singleColInfo = (SGNetCDFDataColumnInfo) colInfoList.get(singleIndex);
      if (singleColInfo.isCoordinateVariable()) {
        SGDimensionInfo cDim = singleColInfo.getDimension(0);
        for (int ii = 0; ii < colInfoList.size(); ii++) {
          if (ii == singleIndex.intValue()) {
            continue;
          }
          SGNetCDFDataColumnInfo nColInfo = (SGNetCDFDataColumnInfo) colInfoList.get(ii);
          if (nColInfo.isCoordinateVariable()) {
            compColType[ii] = curColType[ii];
          }
          List<SGDimensionInfo> dimList = nColInfo.getDimensions();
          if (dimList.contains(cDim)) {
            compColType[ii] = multiColumnType;
          } else {
            compColType[ii] = "";
          }
        }
      } else {
        compColType = curColType.clone();
      }
    }

    return compColType;
  }

  /**
   * Complements the column types.
   *
   * <p>If data type is not scalar-XY, return current column type. If data type is scalar-XY, and if
   * number of "X" column type is 1 and "Y" is 0, set "Y" column type to columns which value types
   * are Number (array data) or which dimensions contain the dimension of "X" (netCDF data). Same
   * for number of "X" is 0 and "Y" is 1. Otherwise return current column type.
   *
   * @param infoMap the information map
   * @param curColType the current column types
   * @param colInfoList the list of column information
   * @return complemented column types
   */
  public static String[] getComplementedColumnType(
      final Map<String, Object> infoMap,
      final String[] curColType,
      final List<SGDataColumnInfo> colInfoList) {
    if (curColType.length != colInfoList.size()) {
      return null;
    }
    String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
    String[] compColType = null;
    if (isSXYTypeData(dataType)) {
      List<Integer> xIndexList = new ArrayList<Integer>();
      List<Integer> yIndexList = new ArrayList<Integer>();
      for (int ii = 0; ii < curColType.length; ii++) {
        if (X_VALUE.equals(curColType[ii])) {
          xIndexList.add(Integer.valueOf(ii));
        } else if (Y_VALUE.equals(curColType[ii])) {
          yIndexList.add(Integer.valueOf(ii));
        }
      }
      if (xIndexList.size() == 1 && yIndexList.size() == 0) {
        compColType =
            getComplementedColumnType(
                infoMap, curColType, colInfoList, xIndexList.get(0), X_VALUE, Y_VALUE);
      } else if (xIndexList.size() == 0 && yIndexList.size() == 1) {
        compColType =
            getComplementedColumnType(
                infoMap, curColType, colInfoList, yIndexList.get(0), Y_VALUE, X_VALUE);
      } else {
        compColType = curColType.clone();
      }
    } else {
      compColType = curColType.clone();
    }
    return compColType;
  }

  /** If data is netCDF, single data selected must be a coordinate variable. */
  private static boolean isComplementedButtonEnabled(
      final Map<String, Object> infoMap,
      final String[] curColType,
      final List<SGDataColumnInfo> colInfoList,
      final Integer singleIndex) {
    SGNetCDFDataColumnInfo singleColInfo = (SGNetCDFDataColumnInfo) colInfoList.get(singleIndex);
    if (!singleColInfo.isCoordinateVariable()) {
      return false;
    }
    return true;
  }

  /**
   * Returns whether the complement button is to be enabled.
   *
   * <p>If data type is scalar-XY, and if number of "X" column type is 1 or number of "Y" is 1, the
   * complement button is enabled.
   *
   * @param infoMap the information map
   * @param curColType current column type
   * @param colInfoList list of column information
   * @return true to set visible
   */
  public static boolean isComplementedButtonEnabled(
      final Map<String, Object> infoMap,
      final String[] curColType,
      final List<SGDataColumnInfo> colInfoList) {
    String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
    if (isSXYTypeData(dataType)) {
      List<Integer> xIndexList = new ArrayList<Integer>();
      List<Integer> yIndexList = new ArrayList<Integer>();
      for (int ii = 0; ii < curColType.length; ii++) {
        if (X_VALUE.equals(curColType[ii])) {
          xIndexList.add(Integer.valueOf(ii));
        } else if (Y_VALUE.equals(curColType[ii])) {
          yIndexList.add(Integer.valueOf(ii));
        }
      }
      if (isSDArrayData(dataType)) {
        if (xIndexList.size() == 1 || yIndexList.size() == 1) {
          return true;
        } else {
          return false;
        }
      } else if (isNetCDFData(dataType)) {
        if (xIndexList.size() == 1) {
          return isComplementedButtonEnabled(infoMap, curColType, colInfoList, xIndexList.get(0));
        } else if (yIndexList.size() == 1) {
          return isComplementedButtonEnabled(infoMap, curColType, colInfoList, yIndexList.get(0));
        } else {
          return false;
        }
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  /**
   * Returns whether the complement button is to be visible.
   *
   * <p>If data type is Scalar-XY, and multiple graphs drawing and multiple variables are selected,
   * complement button is visible.
   *
   * @param infoMap the information map
   * @return true to set visible
   */
  public static boolean isComplementButtonVisible(final Map<String, Object> infoMap) {
    String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
    if (!isSXYTypeData(dataType)) {
      return false;
    }
    Boolean multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
    if (multiple == null) {
      return false;
    }
    final boolean compVisible;
    if (multiple.booleanValue()) {
      Boolean multipleVariable =
          (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE);
      if (multipleVariable == null) {
        return false;
      }
      compVisible = multipleVariable.booleanValue();
    } else {
      compVisible = false;
    }
    return compVisible;
  }

  /**
   * Get index list of column type on text data.
   *
   * <p>If column type in <b>colInfo</b> matches "X", add the index of <b>colInfo</b> into
   * <b>xIndexList</b>. Same, check matching of "Y", "Lower Error", "Upper Error", "Lower / Upper
   * Error" and "Tick Label".
   *
   * @param colInfo
   * @param xIndexList
   * @param yIndexList
   * @param lIndexMap
   * @param uIndexMap
   * @param tIndexMap
   * @return false if column type name is a wrong name. true if succeeds.
   */
  public static boolean getSXYColumnType(
      SGDataColumnInfo[] colInfo,
      List<Integer> xIndexList,
      List<Integer> yIndexList,
      Map<Integer, Integer> lIndexMap,
      Map<Integer, Integer> uIndexMap,
      Map<Integer, Integer> tIndexMap) {
    for (int ii = 0; ii < colInfo.length; ii++) {
      String cType = colInfo[ii].getColumnType();
      if (SGDataDataTypeUtility.isEqualColumnType(X_VALUE, cType)) {
        xIndexList.add(Integer.valueOf(ii));
      } else if (SGDataDataTypeUtility.isEqualColumnType(Y_VALUE, cType)) {
        yIndexList.add(Integer.valueOf(ii));
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(cType, LOWER_ERROR_VALUE)) {
        Integer num = SGDataColumnTitleUtility.getAppendedColumnIndex(cType, colInfo);
        lIndexMap.put(num, Integer.valueOf(ii));
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(cType, UPPER_ERROR_VALUE)) {
        Integer num = SGDataColumnTitleUtility.getAppendedColumnIndex(cType, colInfo);
        uIndexMap.put(num, Integer.valueOf(ii));
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(cType, LOWER_UPPER_ERROR_VALUE)) {
        Integer num = SGDataColumnTitleUtility.getAppendedColumnIndex(cType, colInfo);
        lIndexMap.put(num, Integer.valueOf(ii));
        uIndexMap.put(num, Integer.valueOf(ii));
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(cType, TICK_LABEL)) {
        Integer num = SGDataColumnTitleUtility.getAppendedColumnIndex(cType, colInfo);
        tIndexMap.put(num, Integer.valueOf(ii));
      } else if ("".equals(cType)) {
        continue;
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * Creates a text string for the title with given sampling rate.
   *
   * @return the title for this sampling data
   */
  public static String createSamplingRateTitle(final double samplingRate) {
    StringBuilder sb = new StringBuilder();
    sb.append("Sampling Rate ");
    sb.append(samplingRate);
    sb.append(" Hz");
    return sb.toString();
  }

  /**
   * Returns the list of column indices those are assignable optional columns such as error bars
   * tick labels.
   */
  private static List<Integer> getOptionalColumnsAssignableIndexList(
      String dataType,
      SGDataColumnInfo[] colInfo,
      List<Integer> xIndexList,
      List<Integer> yIndexList) {
    if (!isSXYTypeData(dataType)) {
      return null;
    }
    Set<Integer> indexSet = new TreeSet<Integer>();
    for (Integer index : xIndexList) {
      String valueType = colInfo[index].getValueType();
      if (VALUE_TYPE_NUMBER.equals(valueType)) {
        indexSet.add(index);
      }
    }
    for (Integer index : yIndexList) {
      String valueType = colInfo[index].getValueType();
      if (VALUE_TYPE_NUMBER.equals(valueType)) {
        indexSet.add(index);
      }
    }
    List<Integer> indexList = new ArrayList<Integer>();
    if (isSDArrayData(dataType)) {
      indexList.addAll(indexSet);
    } else if (isNetCDFData(dataType)) {
      for (int ii = 0; ii < colInfo.length; ii++) {
        SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) colInfo[ii];
        Integer index = Integer.valueOf(ii);
        if (indexSet.contains(index)) {
          if (!ncInfo.isCoordinateVariable()) {
            indexList.add(index);
          }
        }
      }
    } else if (isMDArrayData(dataType)) {
      for (int ii = 0; ii < colInfo.length; ii++) {
        Integer index = Integer.valueOf(ii);
        if (indexSet.contains(index)) {
          indexList.add(index);
        }
      }
    } else {
      return null;
    }
    return indexList;
  }

  /**
   * Updates the column types.
   *
   * @param dataType data type
   * @param colInfo column information
   * @param columnTypes an array of columns types
   * @return new column types
   */
  public static String[] updateDataColumns(
      final String dataType, final SGDataColumnInfo[] colInfo, final String[] columnTypes) {

    String[] ret = (String[]) columnTypes.clone();
    if (isSXYTypeData(dataType)) {

      // find the column for X or Y
      List<Integer> xIndexList = new ArrayList<Integer>();
      List<Integer> yIndexList = new ArrayList<Integer>();
      for (int ii = 0; ii < columnTypes.length; ii++) {
        final Integer index = Integer.valueOf(ii);
        if (X_VALUE.equals(columnTypes[ii])) {
          xIndexList.add(index);
        } else if (Y_VALUE.equals(columnTypes[ii])) {
          yIndexList.add(index);
        }
      }

      // get the list of column index those are assignable for the error bar
      List<Integer> indexList =
          getOptionalColumnsAssignableIndexList(dataType, colInfo, xIndexList, yIndexList);
      if (indexList == null) {
        return null;
      }

      // clear the column type for error bars for non-existing X or Y
      for (int ii = 0; ii < columnTypes.length; ii++) {
        String colType = columnTypes[ii];
        if (colType.startsWith(LOWER_ERROR_VALUE)
            || colType.startsWith(UPPER_ERROR_VALUE)
            || colType.startsWith(LOWER_UPPER_ERROR_VALUE)
            || colType.startsWith(TICK_LABEL)) {
          // get the column number
          Integer num = getColumnIndexOfAppendedColumnType(colType, dataType, colInfo);
          if (num == null) {
            return null;
          }
          if (!indexList.contains(num)) {
            // clear the column type
            ret[ii] = "";
          }
        }
      }
    }

    return ret;
  }

  static boolean getColumnNameAndAppendedNumberList(
      final SGDataColumnInfo[] cols,
      final String colType,
      List<String> nameList,
      List<Integer> indexList) {
    for (int ii = 0; ii < cols.length; ii++) {
      String type = cols[ii].getColumnType();
      if (type.toUpperCase().startsWith(colType.toUpperCase())) {
        String name = cols[ii].getName();
        Integer index = getColumnIndexOfAppendedColumnTitle(type, cols);
        if (index == null) {
          return false;
        }
        nameList.add(name);
        indexList.add(index);
      }
    }
    return true;
  }

  static List<SGDataColumnInfo> getColumnListStartsWith(
      final SGDataColumnInfo[] cols, final String prefix) {
    List<SGDataColumnInfo> list = new ArrayList<SGDataColumnInfo>();
    for (int ii = 0; ii < cols.length; ii++) {
      String type = cols[ii].getColumnType();
      if (type.toUpperCase().startsWith(prefix.toUpperCase())) {
        list.add(cols[ii]);
      }
    }
    return list;
  }

  static List<SGDataColumnInfo> getColumnList(final SGDataColumnInfo[] cols, final String colType) {
    List<SGDataColumnInfo> list = new ArrayList<SGDataColumnInfo>();
    for (int ii = 0; ii < cols.length; ii++) {
      String type = cols[ii].getColumnType();
      if (type.equalsIgnoreCase(colType)) {
        list.add(cols[ii]);
      }
    }
    return list;
  }

  static List<String> getColumnNameList(final SGDataColumnInfo[] cols, final String colType) {
    List<String> list = new ArrayList<String>();
    for (int ii = 0; ii < cols.length; ii++) {
      String type = cols[ii].getColumnType();
      if (type.equalsIgnoreCase(colType)) {
        list.add(cols[ii].getName());
      }
    }
    return list;
  }

  public static List<Integer> getColumnIndexListOfNumber(final List<Token> tokenList) {
    List<Integer> list = new ArrayList<Integer>();
    for (int ii = 0; ii < tokenList.size(); ii++) {
      Token token = (Token) tokenList.get(ii);
      final String str = token.getString();
      if (token.isDoubleQuoted()) {
        list.add(Integer.valueOf(0));
      } else {
        Double d = SGUtilityText.getDouble(str);
        if (d != null) {
          list.add(Integer.valueOf(1));
        } else {
          list.add(Integer.valueOf(0));
        }
      }
    }
    return list;
  }

  /**
   * Determine the data-type from the first line.
   *
   * @param tokenList the list of tokens of the first line
   * @param indexList the list of indices whether each token is of the number type or the text type
   * @param cList the list of candidates of data types
   * @return true if succeeded
   */
  public static boolean getDataTypeCandidateList(
      final List<Token> tokenList, final List<Integer> indexList, final List<String> cList) {

    // count the number of number type columns
    int cntNumber = 0;
    for (int ii = 0; ii < indexList.size(); ii++) {
      Integer obj = (Integer) indexList.get(ii);
      int num = obj.intValue();
      if (num == 1) {
        cntNumber++;
      }
    }

    String[] dataTypes = {
      SGDataTypeConstants.SXY_DATA,
      SGDataTypeConstants.SXY_MULTIPLE_DATA,
      SGDataTypeConstants.SXY_SAMPLING_DATA,
      SGDataTypeConstants.VXY_DATA,
      SGDataTypeConstants.SXYZ_DATA
    };
    for (int ii = 0; ii < dataTypes.length; ii++) {
      final int num = getMinimumNumberColumns(dataTypes[ii]);
      if (cntNumber >= num) {
        cList.add(dataTypes[ii]);
      }
    }
    if (cntNumber >= getMinimumNumberColumns(SGDataTypeConstants.SXY_DATE_DATA)) {
      for (int ii = 0; ii < tokenList.size(); ii++) {
        Token token = (Token) tokenList.get(ii);
        if (SGUtilityText.getDate(token.getString()) != null) {
          cList.add(SGDataTypeConstants.SXY_DATE_DATA);
          break;
        }
      }
    }

    return true;
  }

  static boolean[] isEmptyOrRepeatedColumnTitle(SGDataColumnInfo[] columnInfo) {
    int len = columnInfo.length;
    boolean[] result = new boolean[len];
    Arrays.fill(result, false);

    for (int i = 0; i < len; i++) {
      SGDataColumnInfo cInfo = columnInfo[i];
      String title = cInfo.getTitle();
      if (title == null || title.trim().equals("")) {
        result[i] = true;
      } else {
        for (int j = i + 1; j < len; j++) {
          String nextTitle = columnInfo[j].getTitle();
          if (nextTitle != null && title.equalsIgnoreCase(nextTitle)) {
            result[i] = true;
            result[j] = true;
          }
        }
      }
    }

    return result;
  }

  /**
   * Returns whether the polar mode is selected.
   *
   * @param infoMap information map for data
   * @return true if polar mode is selected
   * @throws Error throw if infoMap not have KEY_POLAR_SELECTED key
   */
  public static boolean isPolar(final Map<String, Object> infoMap) {
    Object value = infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED);
    if (value == null) {
      throw new Error("Mode for vector data is not selected.");
    }
    Boolean b = (Boolean) value;
    return b.booleanValue();
  }

  private static boolean isDuplicated(List<?> items) {
    for (int ii = 0; ii < items.size() - 1; ii++) {
      for (int jj = ii + 1; jj < items.size(); jj++) {
        if (SGUtility.equals(items.get(ii), items.get(jj))) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Count the number of equal objects in a given array.
   *
   * @param items an array
   * @param value a value
   * @return the number of items in a given array
   */
  private static int count(Object[] items, Object value) {
    if (value == null) {
      throw new IllegalArgumentException("value == null");
    }
    int cnt = 0;
    for (int ii = 0; ii < items.length; ii++) {
      if (items[ii] == null) {
        continue;
      }
      if (SGDataDataTypeUtility.isEqualColumnType(items[ii].toString(), value.toString())) {
        cnt++;
      }
    }
    return cnt;
  }

  /**
   * Checks whether given data columns are valid for given data type. This method checks only the
   * number of selected columns.
   *
   * @param dataType data type
   * @param columnInfoArray an array of columns information
   * @param infoMap information map for data
   */
  public static boolean checkDataColumns(
      final String dataType,
      final SGDataColumnInfo[] columnInfoArray,
      final Map<String, Object> infoMap) {

    final int len = columnInfoArray.length;
    String[] columnTypes = new String[len];
    for (int ii = 0; ii < len; ii++) {
      columnTypes[ii] = columnInfoArray[ii].getColumnType();
    }

    // other than multiple and sampling data, check the duplication
    if (!isSXYTypeData(dataType)) {
      for (int ii = 0; ii < len - 1; ii++) {
        if (columnTypes[ii] == null || "".equals(columnTypes[ii])) {
          continue;
        }
        for (int jj = ii + 1; jj < len; jj++) {
          if (columnTypes[ii].equals(columnTypes[jj])) {
            return false;
          }
        }
      }
    }

    if (isSXYTypeData(dataType)) {

      // one array for x values and multiple arrays for y values
      // or one array for y values and multiple arrays for x values
      // are permitted
      final int xCnt = count(columnTypes, X_VALUE);
      final int yCnt = count(columnTypes, Y_VALUE);
      if (xCnt == 0 && yCnt == 0) {
        return false;
      }

      final boolean byMultiple;
      final boolean bxMultiple;
      final boolean bothSingle;
      if (isMDArrayData(dataType)) {
        byMultiple = ((xCnt == 0 || xCnt == 1) && yCnt > 1);
        bxMultiple = (xCnt > 1 && (yCnt == 0 || yCnt == 1));
        bothSingle = ((xCnt == 0 || xCnt == 1) && (yCnt == 0 || yCnt == 1));
      } else {
        byMultiple = (xCnt == 1 && yCnt > 1);
        bxMultiple = (xCnt > 1 && yCnt == 1);
        bothSingle = (xCnt == 1 && yCnt == 1);
        if (!byMultiple && !bxMultiple && !bothSingle) {
          return false;
        }
      }

      String multipleColumnType = null;
      String singleColumnType = null;
      if (byMultiple) {
        multipleColumnType = Y_VALUE;
        singleColumnType = X_VALUE;
      } else if (bxMultiple) {
        multipleColumnType = X_VALUE;
        singleColumnType = Y_VALUE;
      }

      if (!bothSingle) {
        Set<String> multipleValueTypeSet = new HashSet<String>();
        if (multipleColumnType != null) {
          for (int ii = 0; ii < len; ii++) {
            if (multipleColumnType.equals(columnTypes[ii])) {
              multipleValueTypeSet.add(columnInfoArray[ii].getValueType());
            }
          }
        }

        // checks whether sampling rate is used for multiple values
        if (multipleValueTypeSet.contains(VALUE_TYPE_SAMPLING_RATE)) {
          return false;
        }

        // checks whether the data column exists in multiple values
        if (multipleValueTypeSet.contains(VALUE_TYPE_DATE)) {
          return false;
        }
      }

      // boolean checkAppendedIndex = false;
      // if (isArrayData(dataType)){
      // checkAppendedIndex = true;
      // } else if (isNetCDFData(dataType) || isMDData(dataType)) {
      // Boolean variable = (Boolean) infoMap.get(KEY_MULTIPLE_VARIABLE);
      // if (variable == null) {
      // return false;
      // }
      // checkAppendedIndex = variable.booleanValue();
      // } else {
      // return false;
      // }

      boolean checkAppendedIndex = true;

      if (checkAppendedIndex) {
        // checks column indices appended to the error bars and tick labels

        List<Integer> lList = new ArrayList<Integer>();
        List<Integer> uList = new ArrayList<Integer>();
        List<Integer> luList = new ArrayList<Integer>();
        for (int ii = 0; ii < columnTypes.length; ii++) {
          String colType = columnTypes[ii];
          if (colType.startsWith(LOWER_ERROR_VALUE)) {
            Integer num = getColumnIndexOfAppendedColumnType(colType, dataType, columnInfoArray);
            if (num == null) {
              return false;
            }
            lList.add(num);
          } else if (colType.startsWith(UPPER_ERROR_VALUE)) {
            Integer num = getColumnIndexOfAppendedColumnType(colType, dataType, columnInfoArray);
            if (num == null) {
              return false;
            }
            uList.add(num);
          } else if (colType.startsWith(LOWER_UPPER_ERROR_VALUE)) {
            Integer num = getColumnIndexOfAppendedColumnType(colType, dataType, columnInfoArray);
            if (num == null) {
              return false;
            }
            luList.add(num);
          }
        }

        List<Integer> tList = new ArrayList<Integer>();
        for (int ii = 0; ii < columnTypes.length; ii++) {
          String colType = columnTypes[ii];
          if (colType.startsWith(TICK_LABEL)) {
            Integer num = getColumnIndexOfAppendedColumnType(colType, dataType, columnInfoArray);
            if (num == null) {
              return false;
            }
            tList.add(num);
          }
        }

        if (!bothSingle) {
          // checks whether error bars and tick labels are assigned to
          // not multiple values
          Integer singleIndex = null;
          for (int ii = 0; ii < columnTypes.length; ii++) {
            if (columnTypes[ii].equals(singleColumnType)) {
              singleIndex = Integer.valueOf(ii);
              break;
            }
          }
          if (!isMDArrayData(dataType)) {
            // check for array data and NetCDF data
            if (singleIndex == null) {
              return false;
            }
          }
          if (lList.contains(singleIndex)) {
            return false;
          }
          if (uList.contains(singleIndex)) {
            return false;
          }
          if (luList.contains(singleIndex)) {
            return false;
          }
          if (tList.contains(singleIndex)) {
            return false;
          }
        }

        // checks whether error bars and tick labels for x values
        // and those for y values exist at the same time
        Set<Integer> allSet = new TreeSet<Integer>();
        allSet.addAll(lList);
        allSet.addAll(uList);
        allSet.addAll(luList);
        allSet.addAll(tList);
        List<Integer> xList = new ArrayList<Integer>();
        List<Integer> yList = new ArrayList<Integer>();
        Iterator<Integer> itr = allSet.iterator();
        while (itr.hasNext()) {
          Integer index = itr.next();
          String colType = columnTypes[index.intValue()];
          if (X_VALUE.equals(colType)) {
            xList.add(index);
          } else if (Y_VALUE.equals(colType)) {
            yList.add(index);
          }
        }
        if (xList.size() != 0 && yList.size() != 0) {
          return false;
        }

        // check duplication
        if (isDuplicated(lList)) {
          return false;
        }
        if (isDuplicated(uList)) {
          return false;
        }
        if (isDuplicated(luList)) {
          return false;
        }
        if (isDuplicated(tList)) {
          return false;
        }

        // check consistency
        if (!lList.equals(uList)) {
          return false;
        }
        for (int ii = 0; ii < luList.size(); ii++) {
          Object obj = luList.get(ii);
          if (lList.contains(obj)) {
            return false;
          }
          if (uList.contains(obj)) {
            return false;
          }
        }
      }

    } else if (isSXYZTypeData(dataType)) {
      // duplication was already checked

      // only one x values must be selected
      final int xCnt = count(columnTypes, X_VALUE);
      if (isMDArrayData(dataType)) {
        if (xCnt > 1) {
          return false;
        }
      } else {
        if (xCnt != 1) {
          return false;
        }
      }

      // only one y values must be selected
      final int yCnt = count(columnTypes, Y_VALUE);
      if (isMDArrayData(dataType)) {
        if (yCnt > 1) {
          return false;
        }
      } else {
        if (yCnt != 1) {
          return false;
        }
      }

      // only one z values must be selected
      final int zCnt = count(columnTypes, Z_VALUE);
      if (zCnt != 1) {
        return false;
      }

      // x and y index
      if (isNetCDFData(dataType)) {
        final int indexCnt = count(columnTypes, INDEX);
        final int xIndexCnt = count(columnTypes, X_INDEX);
        final int yIndexCnt = count(columnTypes, Y_INDEX);
        if (indexCnt == 1) {
          if (xIndexCnt == 1 || yIndexCnt == 1) {
            return false;
          }
        }
      }

    } else if (isVXYTypeData(dataType)) {
      // duplication was already checked

      final int xCnt = count(columnTypes, X_COORDINATE);
      final int yCnt = count(columnTypes, Y_COORDINATE);
      if (xCnt > 1) {
        return false;
      }
      if (yCnt > 1) {
        return false;
      }

      // both of magnitude and angle must be selected in polar mode,
      // and both of x component and y component must be selected in the
      // other mode
      final String first = SGDataStrideUtility.getVXYFirstComponentColumnType(infoMap);
      final String second = SGDataStrideUtility.getVXYSecondComponentColumnType(infoMap);
      final int bCnt1 = count(columnTypes, first);
      final int bCnt2 = count(columnTypes, second);
      if (bCnt1 != 1) {
        return false;
      }
      if (bCnt2 != 1) {
        return false;
      }
    }

    if (isNetCDFData(dataType)) {
      // animation
      final int tCnt = count(columnTypes, ANIMATION_FRAME);
      if (tCnt > 1) {
        return false;
      }
    }

    return true;
  }

  /**
   * Create items for the combo box.
   *
   * <p>Return the enabled candidates for column type.
   *
   * @param dataType the type of data
   * @param infoMap the map of data information
   * @param valueType the type of value
   * @return an array of items
   */
  public static String[] getColumnTypeCandidates(
      final String dataType, final Map<String, Object> infoMap, final String valueType) {

    String[] items = null;
    if (isSXYTypeData(dataType)) {
      // SXY
      if (VALUE_TYPE_BYTE_DATA.equals(valueType)) {
        items = ARRAY_EMPTY;
      } else {
        items = getSXYColumnTypeCandidates(dataType, infoMap, valueType);
      }
    } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
      // SXYZ
      if (VALUE_TYPE_NUMBER.equals(valueType)) {
        if (SGDataDataTypeUtility.isSDArrayData(dataType)) {
          items = ARRAY_NUMBER_SDARRAY_SXYZ;
        } else if (SGDataDataTypeUtility.isNetCDFData(dataType)) {
          items =
              updateNetCDFItems(
                  ARRAY_NUMBER_NETCDF_SXYZ_ALL,
                  infoMap,
                  dataType,
                  valueType,
                  new String[] {ANIMATION_FRAME, INDEX},
                  new String[] {Z_VALUE});
        } else if (SGDataDataTypeUtility.isMDArrayData(dataType)) {
          items = ARRAY_NUMBER_MDARRAY_SXYZ;
        } else {
          throw new IllegalArgumentException("Invalid data type: " + dataType);
        }
      } else if (VALUE_TYPE_TEXT.equals(valueType)) {
        items = ARRAY_EMPTY;
      } else if (VALUE_TYPE_DATE.equals(valueType)) {
        items = ARRAY_EMPTY;
      } else if (VALUE_TYPE_BYTE_DATA.equals(valueType)) {
        items = ARRAY_EMPTY;
      } else {
        throw new IllegalArgumentException("Invalid value type: " + valueType);
      }
    } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
      // VXY
      if (VALUE_TYPE_NUMBER.equals(valueType)) {
        if (isPolar(infoMap)) {
          if (SGDataDataTypeUtility.isSDArrayData(dataType)) {
            items = ARRAY_NUMBER_SDARRAY_VXY_POLAR;
          } else if (SGDataDataTypeUtility.isNetCDFData(dataType)) {
            items =
                updateNetCDFItems(
                    ARRAY_NUMBER_NETCDF_VXY_POLAR_ALL,
                    infoMap,
                    dataType,
                    valueType,
                    new String[] {ANIMATION_FRAME, INDEX},
                    new String[] {MAGNITUDE, ANGLE});
          } else if (SGDataDataTypeUtility.isMDArrayData(dataType)) {
            items = ARRAY_NUMBER_MDARRAY_VXY_POLAR;
          } else {
            throw new IllegalArgumentException("Invalid data type: " + dataType);
          }
        } else {
          if (SGDataDataTypeUtility.isSDArrayData(dataType)) {
            items = ARRAY_NUMBER_SDARRAY_VXY_ORTHOGONAL;
          } else if (SGDataDataTypeUtility.isNetCDFData(dataType)) {
            items =
                updateNetCDFItems(
                    ARRAY_NUMBER_NETCDF_VXY_ORTHOGONAL_ALL,
                    infoMap,
                    dataType,
                    valueType,
                    new String[] {ANIMATION_FRAME, INDEX},
                    new String[] {X_COMPONENT, Y_COMPONENT});
          } else if (SGDataDataTypeUtility.isMDArrayData(dataType)) {
            items = ARRAY_NUMBER_MDARRAY_VXY_ORTHOGONAL;
          } else {
            throw new IllegalArgumentException("Invalid data type: " + dataType);
          }
        }
      } else if (VALUE_TYPE_TEXT.equals(valueType)) {
        items = ARRAY_EMPTY;
      } else if (VALUE_TYPE_DATE.equals(valueType)) {
        items = ARRAY_EMPTY;
      } else if (VALUE_TYPE_BYTE_DATA.equals(valueType)) {
        items = ARRAY_EMPTY;
      } else {
        throw new IllegalArgumentException("Invalid value type: " + valueType);
      }
    } else {
      throw new IllegalArgumentException("Invalid data type: " + dataType);
    }

    return (String[]) items.clone();
  }

  /**
   * Create items for the combo box.
   *
   * <p>Return the enabled candidates for column type of Scalar-XY data type.
   *
   * @param dataType the type of data
   * @param infoMap the map of data information
   * @param valueType the type of value
   * @return an array of items
   */
  private static String[] getSXYColumnTypeCandidates(
      final String dataType, final Map<String, Object> infoMap, final String valueType) {

    final boolean isNetCDFData = isNetCDFData(dataType);
    final boolean isMDData = isMDArrayData(dataType);
    final boolean isNetCDFOrMDData = (isNetCDFData || isMDData);

    Boolean multiple = null;
    if (isNetCDFOrMDData) {
      multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
      if (multiple == null) {
        return null;
      }
    }

    List<String> itemList = new ArrayList<String>();
    itemList.add("");
    if (!VALUE_TYPE_TEXT.equals(valueType)) {
      // other than text column
      itemList.add(X_VALUE);
      itemList.add(Y_VALUE);
    }

    // current row index
    Integer rowIndex = (Integer) infoMap.get(SGIDataInformationKeyConstants.KEY_CURRENT_ROW_INDEX);

    // all columns
    SGDataColumnInfo[] colInfo =
        (SGDataColumnInfo[]) infoMap.get(SGIDataInformationKeyConstants.KEY_COLUMN_INFO);
    boolean[] isRepeatedTitle = isEmptyOrRepeatedColumnTitle(colInfo);

    // get column indices of x and y values
    List<Integer> xIndexList = new ArrayList<Integer>();
    List<Integer> yIndexList = new ArrayList<Integer>();
    for (int ii = 0; ii < colInfo.length; ii++) {
      String colType = colInfo[ii].getColumnType();
      final Integer index = Integer.valueOf(ii);
      if (X_VALUE.equals(colType)) {
        xIndexList.add(index);
      } else if (Y_VALUE.equals(colType)) {
        yIndexList.add(index);
      }
    }

    // counts the number of number type rows
    int numberRowNum = 0;
    for (int ii = 0; ii < colInfo.length; ii++) {
      String vType = colInfo[ii].getValueType();
      if (VALUE_TYPE_NUMBER.equals(vType)) {
        numberRowNum++;
      } else if (VALUE_TYPE_DATE.equals(vType)) {
        if (X_VALUE.equals(colInfo[ii].getColumnType())
            || Y_VALUE.equals(colInfo[ii].getColumnType())) {
          numberRowNum++;
        }
      }
    }

    // add time, pickup and serial number columns
    // if (isNetCDFData && VALUE_TYPE_NUMBER.equals(valueType)) {
    // SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) colInfo[rowIndex];
    // if (ncInfo.isCoordinateVariable()) {
    // itemList.add(ANIMATION_FRAME);
    // itemList.add(PICKUP);
    // itemList.add(INDEX);
    // }
    // }
    updateNetCDFItems(
        itemList,
        infoMap,
        dataType,
        valueType,
        new String[] {ANIMATION_FRAME, PICKUP, INDEX},
        new String[] {});

    // a flag whether to show options for error bars and tick labels
    boolean errorBarTickLabelFlag = true;
    if (isNetCDFData) {
      // skips NetCDF coordinate variables
      SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) colInfo[rowIndex];
      if (ncInfo.isCoordinateVariable()) {
        errorBarTickLabelFlag = false;
      }
    }
    if (errorBarTickLabelFlag) {
      // get the list of column index those are assignable
      // for the error bar or tick labels
      List<Integer> indexList =
          getOptionalColumnsAssignableIndexList(dataType, colInfo, xIndexList, yIndexList);
      if (indexList != null) {
        // remove the current row if it exists
        indexList.remove(rowIndex);

        // remove picked up column for multidimensional data
        if (SGDataDataTypeUtility.isMDArrayData(dataType)) {
          List<Integer> pickUpIndexList = new ArrayList<Integer>();
          for (int ii = 0; ii < colInfo.length; ii++) {
            SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfo[ii];
            Integer pickUpIndex = mdInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
            if (pickUpIndex != null) {
              pickUpIndexList.add(ii);
            }
          }
          if (pickUpIndexList.size() > 0) {
            for (int ii = indexList.size() - 1; ii >= 0; ii--) {
              Integer index = indexList.get(ii);
              if (!pickUpIndexList.contains(index)) {
                indexList.remove(index);
              }
            }
          }
        }

        if (VALUE_TYPE_NUMBER.equals(valueType)) {
          // only for the number type column

          // add items of error bars to the list
          for (int ii = 0; ii < indexList.size(); ii++) {
            Integer index = indexList.get(ii);
            String vType = colInfo[index.intValue()].getValueType();
            if (VALUE_TYPE_SAMPLING_RATE.equals(vType)) {
              continue;
            }
            final int num = index.intValue();
            final int min1 = isMDData ? 3 : 4;
            final int min2 = min1 - 1;
            if (numberRowNum >= min1) {
              itemList.add(
                  appendColumnType(
                      LOWER_ERROR_VALUE, num, isNetCDFOrMDData, colInfo, isRepeatedTitle[num]));
              itemList.add(
                  appendColumnType(
                      UPPER_ERROR_VALUE, num, isNetCDFOrMDData, colInfo, isRepeatedTitle[num]));
              itemList.add(
                  appendColumnType(
                      LOWER_UPPER_ERROR_VALUE,
                      num,
                      isNetCDFOrMDData,
                      colInfo,
                      isRepeatedTitle[num]));
            } else if (numberRowNum >= min2) {
              itemList.add(
                  appendColumnType(
                      LOWER_UPPER_ERROR_VALUE,
                      num,
                      isNetCDFOrMDData,
                      colInfo,
                      isRepeatedTitle[num]));
            }
          }
        }

        // add items of tick labels to the list
        if (!VALUE_TYPE_SAMPLING_RATE.equals(valueType)) {
          // add items of tick labels to the list
          for (int ii = 0; ii < indexList.size(); ii++) {
            Integer index = indexList.get(ii);
            final int num = index.intValue();
            itemList.add(
                appendColumnType(TICK_LABEL, num, isNetCDFOrMDData, colInfo, isRepeatedTitle[num]));
          }
        }
      }
    }

    return itemList.toArray(new String[itemList.size()]);
  }

  /**
   * Get name list of column type on netCDF data.
   *
   * <p>If column type in <b>colInfo</b> matches "X", add the variable name of <b>colInfo</b> into
   * <b>xNameList</b>. Same, check matching of "Y", "Lower Error", "Upper Error", "Lower / Upper
   * Error", "Tick Label", "Dimension", "Time" and "Index".
   *
   * @param colInfo
   * @param xInfoList contains variable names which have "X" column type.
   * @param yInfoList
   * @param leInfoList
   * @param ueInfoList
   * @param tlInfoList
   * @param pickupInfoList
   * @param timeInfoList
   * @param indexInfoList
   * @return
   */
  public static boolean getSXYDimensionDataColumnType(
      SGDataColumnInfo[] colInfo,
      List<SGDataColumnInfo> xInfoList,
      List<SGDataColumnInfo> yInfoList,
      List<SGDataColumnInfo> leInfoList,
      List<SGDataColumnInfo> ueInfoList,
      List<SGDataColumnInfo> tlInfoList,
      List<SGDataColumnInfo> pickupInfoList,
      List<SGDataColumnInfo> timeInfoList,
      List<SGDataColumnInfo> indexInfoList) {
    for (int ii = 0; ii < colInfo.length; ii++) {
      SGDataColumnInfo col = colInfo[ii];
      String cType = col.getColumnType();
      if (SGDataDataTypeUtility.isEqualColumnType(X_VALUE, cType)) {
        xInfoList.add(col);
      } else if (SGDataDataTypeUtility.isEqualColumnType(Y_VALUE, cType)) {
        yInfoList.add(col);
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(cType, LOWER_ERROR_VALUE)) {
        leInfoList.add(col);
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(cType, UPPER_ERROR_VALUE)) {
        ueInfoList.add(col);
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(cType, LOWER_UPPER_ERROR_VALUE)) {
        leInfoList.add(col);
        ueInfoList.add(col);
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(cType, TICK_LABEL)) {
        tlInfoList.add(col);
      } else if (SGDataDataTypeUtility.isEqualColumnType(PICKUP, cType)) {
        pickupInfoList.add(col);
      } else if (SGDataDataTypeUtility.isEqualColumnType(ANIMATION_FRAME, cType)) {
        timeInfoList.add(col);
      } else if (SGDataDataTypeUtility.isEqualColumnType(INDEX, cType)) {
        indexInfoList.add(col);
      } else if ("".equals(cType)) {
        continue;
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * Get name list of column type on netCDF data.
   *
   * <p>If column type in <b>colInfo</b> matches "X", add the variable name of <b>colInfo</b> into
   * <b>xNameList</b>. Same, check matching of "Y", "Lower Error", "Upper Error", "Lower / Upper
   * Error", "Tick Label", "Time" and "Serial Number".
   *
   * @param colInfo
   * @param xInfoList contains variable names which have "X" column type.
   * @param yInfoList
   * @param leInfoMap
   * @param ueInfoMap
   * @param tlInfoMap
   * @param timeInfoList
   * @param indexInfoList
   * @param pickUpInfoList
   * @return
   */
  public static boolean getSXYColumnType(
      SGDataColumnInfo[] colInfo,
      List<SGDataColumnInfo> xInfoList,
      List<SGDataColumnInfo> yInfoList,
      Map<SGDataColumnInfo, SGDataColumnInfo> leInfoMap,
      Map<SGDataColumnInfo, SGDataColumnInfo> ueInfoMap,
      Map<SGDataColumnInfo, SGDataColumnInfo> tlInfoMap,
      List<SGDataColumnInfo> timeInfoList,
      List<SGDataColumnInfo> indexInfoList,
      List<SGDataColumnInfo> pickUpInfoList) {
    for (int ii = 0; ii < colInfo.length; ii++) {
      SGDataColumnInfo col = colInfo[ii];
      String cType = col.getColumnType();
      if (SGDataDataTypeUtility.isEqualColumnType(X_VALUE, cType)) {
        xInfoList.add(col);
      } else if (SGDataDataTypeUtility.isEqualColumnType(Y_VALUE, cType)) {
        yInfoList.add(col);
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(cType, LOWER_ERROR_VALUE)) {
        Integer num = SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle(cType, colInfo);
        SGDataColumnInfo colAppend = colInfo[num.intValue()];
        leInfoMap.put(colAppend, col);
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(cType, UPPER_ERROR_VALUE)) {
        Integer num = SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle(cType, colInfo);
        SGDataColumnInfo colAppend = colInfo[num.intValue()];
        ueInfoMap.put(colAppend, col);
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(cType, LOWER_UPPER_ERROR_VALUE)) {
        Integer num = SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle(cType, colInfo);
        SGDataColumnInfo colAppend = colInfo[num.intValue()];
        leInfoMap.put(colAppend, col);
        ueInfoMap.put(colAppend, col);
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(cType, TICK_LABEL)) {
        Integer num = SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle(cType, colInfo);
        SGDataColumnInfo colAppend = colInfo[num.intValue()];
        tlInfoMap.put(colAppend, col);
      } else if (SGDataDataTypeUtility.isEqualColumnType(ANIMATION_FRAME, cType)) {
        timeInfoList.add(col);
      } else if (SGDataDataTypeUtility.isEqualColumnType(INDEX, cType)) {
        indexInfoList.add(col);
      } else if (SGDataDataTypeUtility.isEqualColumnType(PICKUP, cType)) {
        pickUpInfoList.add(col);
      } else if ("".equals(cType)) {
        continue;
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * Updates the information map with given data type and column information.
   *
   * @param dataType the data type
   * @param colInfo the column information
   * @param infoMap the information map
   * @return updated information map
   */
  public static Map<String, Object> updateInfoMap(
      String dataType, SGDataColumnInfo[] colInfo, Map<String, Object> infoMap) {
    Map<String, Object> infoMapUpd = new HashMap<String, Object>(infoMap);
    // if (isSXYTypeData(dataType)) {
    // // column information
    // infoMapUpd.put(SGIDataInformationKeyConstants.KEY_COLUMN_INFO,
    // colInfo.clone());
    // }
    // column information
    infoMapUpd.put(SGIDataInformationKeyConstants.KEY_COLUMN_INFO, colInfo.clone());
    return infoMapUpd;
  }
}
