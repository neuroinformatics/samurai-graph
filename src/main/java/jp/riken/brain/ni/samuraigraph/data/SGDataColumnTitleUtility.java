package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDataBufferUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnInfoUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataMiscUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataRangeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataStrideUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataTextUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataViewerUtility.*;

import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/** Static helpers for the column title grammar of data columns. */
public final class SGDataColumnTitleUtility
    implements SGIDataColumnTypeConstants,
        SGIDataPropertyKeyConstants,
        SGINetCDFConstants,
        SGIMDArrayConstants {

  /** A part of the appended column number. */
  static final String MID_COLUMN_NO = " for No.";

  /** A part of the appended column title. */
  static final String MID_COLUMN = " for ";

  private SGDataColumnTitleUtility() {}

  private static String removeHeaderSub(final String colType, final boolean isTitle) {
    String mid;
    if (isTitle) {
      mid = MID_COLUMN;
    } else {
      mid = MID_COLUMN_NO;
    }
    final int index = colType.indexOf(mid);
    if (index == -1) {
      return null;
    }
    return colType.substring(index + mid.length());
  }

  private static String getTitleString(final String str) {
    int idx = str.toUpperCase().indexOf(MID_COLUMN.toUpperCase().trim());
    if (idx == -1) {
      return null;
    }
    idx = idx + MID_COLUMN.trim().length();
    final String titleStr = str.substring(idx).trim();
    return titleStr;
  }

  /**
   * Appends the column number to a text string of the column type.
   *
   * @param colType the column type
   * @param index array index of the column
   */
  public static final String appendColumnNo(final String colType, final int index) {
    StringBuilder sb = new StringBuilder();
    sb.append(colType);
    sb.append(MID_COLUMN_NO);
    sb.append(index + 1);
    return sb.toString();
  }

  /**
   * Appends the variable name to a text string of the column type on the netCDF data.
   *
   * @param colType
   * @param variableName
   * @return
   */
  public static final String appendColumnTitle(final String colType, final String variableName) {
    StringBuilder sb = new StringBuilder();
    sb.append(colType);
    sb.append(MID_COLUMN);
    sb.append(variableName);
    return sb.toString();
  }

  public static String removeHeaderTitle(final String colType) {
    return removeHeaderSub(colType, true);
  }

  public static String removeHeaderNo(final String colType) {
    return removeHeaderSub(colType, false);
  }

  /**
   * Appends the column title to a text string of the column type on the netCDF data.
   *
   * <p>When using the netCDF data, column title exists and is not doubled.
   *
   * @param colType
   * @param index
   * @param colInfo
   * @return
   */
  public static final String appendColumnTitle(
      final String colType, final int index, final SGDataColumnInfo[] colInfo) {
    StringBuilder sb = new StringBuilder();
    sb.append(colType);
    sb.append(MID_COLUMN);
    sb.append(colInfo[index].getTitle());
    return sb.toString();
  }

  /**
   * Extracts the column number from a given text string.
   *
   * @param str a text string
   * @return the column number if it exists and otherwise null
   */
  public static final Integer getAppendedColumnIndex(final String str) {
    final String forSeparator = "for";
    final String noSeparator = "No";
    int idx = str.toUpperCase().indexOf(forSeparator.toUpperCase());
    if (idx == -1) {
      return null;
    }
    idx = idx + forSeparator.length();
    String substr = str.substring(idx).trim().toUpperCase();

    if (!substr.startsWith(noSeparator.toUpperCase())) {
      return null;
    }
    substr = SGUtilityText.getCharString(substr);
    final String numStr = substr.substring(noSeparator.length());
    Integer index = SGUtilityText.getInteger(numStr);
    if (index == null) {
      return null;
    }
    return Integer.valueOf(index.intValue() - 1);
  }

  public static final Integer getAppendedColumnIndex(
      final String str, final SGDataColumnInfo[] colInfo) {
    Integer value = getColumnIndexOfAppendedColumnTitle(str, colInfo);
    if (null == value) {
      value = getAppendedColumnIndex(str);
    }
    return value;
  }

  public static final Integer getAppendedColumnIndex(
      final String str, final String[] columnTitles) {
    int idx = str.toUpperCase().indexOf(MID_COLUMN.trim().toUpperCase());
    if (idx == -1) {
      return null;
    }
    idx = idx + MID_COLUMN.trim().length();
    final String titleStr = str.substring(idx).trim();
    for (int i = 0; i < columnTitles.length; i++) {
      if (columnTitles[i] != null) {
        if (titleStr.equals(columnTitles[i].trim())) {
          return Integer.valueOf(i);
        }
      }
    }
    return getAppendedColumnIndex(str);
  }

  /**
   * Extracts the column number from a given text string.
   *
   * @param str
   * @param colInfo
   * @return the column number if it exists and otherwise null
   */
  public static final Integer getColumnIndexOfAppendedColumnTitle(
      final String str, final SGDataColumnInfo[] colInfo) {
    int idx = str.toUpperCase().indexOf(MID_COLUMN.trim().toUpperCase());
    if (idx == -1) {
      return null;
    }
    idx = idx + MID_COLUMN.trim().length();
    final String titleStr = str.substring(idx).trim();
    for (int i = 0; i < colInfo.length; i++) {
      if (colInfo[i] instanceof SGNetCDFDataColumnInfo
          || colInfo[i] instanceof SGMDArrayDataColumnInfo) {
        if (titleStr.equals(colInfo[i].getName())) {
          return Integer.valueOf(i);
        }
      } else {
        if (titleStr.equals(colInfo[i].getTitle())) {
          return Integer.valueOf(i);
        }
      }
    }
    return null;
  }

  public static final Integer getColumnIndexOfAppendedColumnTitle(
      final String str, SGNetCDFData data) {
    final String titleStr = getTitleString(str);
    if (titleStr == null) {
      return null;
    }
    SGNetCDFFile file = data.getNetcdfFile();
    int index = file.getVariableIndex(titleStr);
    if (index == -1) {
      return null;
    }
    return Integer.valueOf(index);
  }

  public static final Integer getColumnIndexOfAppendedColumnTitle(
      final String str, SGMDArrayData data) {
    final String titleStr = getTitleString(str);
    if (titleStr == null) {
      return null;
    }
    int index = data.getVariableIndex(titleStr);
    if (index == -1) {
      return null;
    }
    return Integer.valueOf(index);
  }

  public static final String appendColumnNoOrTitle(
      final String colType, final int index, final boolean isRepeated, final String title) {
    if (isRepeated) {
      return appendColumnNo(colType, index);
    } else {
      return appendColumnTitle(colType, title);
    }
  }

  public static final String appendColumnType(
      final String colType,
      final int index,
      final boolean isNetCDFOrMDArrayData,
      final SGDataColumnInfo[] colInfo,
      final boolean isRepeated) {
    if (isNetCDFOrMDArrayData) {
      return appendColumnTitle(colType, index, colInfo);
    } else {
      return appendColumnNoOrTitle(colType, index, isRepeated, colInfo[index].getTitle());
    }
  }

  public static final Integer getColumnIndexOfAppendedColumnType(
      final String str, final String dataType, final SGDataColumnInfo[] colInfo) {
    if (SGDataDataTypeUtility.isNetCDFData(dataType)) {
      return getColumnIndexOfAppendedColumnTitle(str, colInfo);
    } else {
      return getAppendedColumnIndex(str, colInfo);
    }
  }
}
