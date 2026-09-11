package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDataBufferUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnInfoUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTitleUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataMiscUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataRangeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataStrideUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataTextUtility.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory.ISingleDimension;
import jp.riken.brain.ni.samuraigraph.base.SGIData;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.SXYDoubleDataValue;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.SXYSingleDataValue;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.SXYZDataValue;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.VXYDataValue;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.Value;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData.DoubleValueSetResult;

/** Static helper for the Viewer responsibility. */
public final class SGDataViewerUtility
    implements SGIDataColumnTypeConstants,
        SGIDataPropertyKeyConstants,
        SGINetCDFConstants,
        SGIMDArrayConstants {

  private SGDataViewerUtility() {}

  public static void setEditedValue(
      SGIVXYTypeData data,
      double[] xValues,
      double[] yValues,
      double[][] fGridValues,
      double[][] sGridValues,
      final boolean all,
      SGDataValueHistory dataValue) {
    final double value = dataValue.getValue();
    String columnType = dataValue.getColumnType();
    final boolean polar = data.isPolar();
    final String first = SGDataStrideUtility.getVXYFirstComponentColumnType(polar);
    final String second = SGDataStrideUtility.getVXYSecondComponentColumnType(polar);
    if (X_COORDINATE.equals(columnType)) {
      ISingleDimension dataValueD1 = (ISingleDimension) dataValue;
      final int index = dataValueD1.getIndex();
      final int arrayIndex = getArrayIndex(data, all, data.getXStride(), index);
      xValues[arrayIndex] = value;
    } else if (Y_COORDINATE.equals(columnType)) {
      ISingleDimension dataValueD1 = (ISingleDimension) dataValue;
      final int index = dataValueD1.getIndex();
      final int arrayIndex = getArrayIndex(data, all, data.getYStride(), index);
      yValues[arrayIndex] = value;
    } else if (first.equals(columnType)) {
      final int col = dataValue.getColumnIndex();
      final int row = dataValue.getRowIndex();
      final int colIndex = getArrayIndex(data, all, data.getXStride(), col);
      final int rowIndex = getArrayIndex(data, all, data.getYStride(), row);
      fGridValues[rowIndex][colIndex] = value;
    } else if (second.equals(columnType)) {
      final int col = dataValue.getColumnIndex();
      final int row = dataValue.getRowIndex();
      final int colIndex = getArrayIndex(data, all, data.getXStride(), col);
      final int rowIndex = getArrayIndex(data, all, data.getYStride(), row);
      sGridValues[rowIndex][colIndex] = value;
    }
  }

  public static void setEditedValue(
      SGIVXYTypeData data,
      double[] xValues,
      double[] yValues,
      double[] fValues,
      double[] sValues,
      final boolean all,
      SGDataValueHistory dataValue) {
    final double value = dataValue.getValue();
    String columnType = dataValue.getColumnType();
    ISingleDimension dataValueD1 = (ISingleDimension) dataValue;
    final int index = dataValueD1.getIndex();
    final boolean polar = data.isPolar();
    final String first = SGDataStrideUtility.getVXYFirstComponentColumnType(polar);
    final String second = SGDataStrideUtility.getVXYSecondComponentColumnType(polar);
    double[] values;
    if (X_COORDINATE.equals(columnType)) {
      values = xValues;
    } else if (Y_COORDINATE.equals(columnType)) {
      values = yValues;
    } else if (first.equals(columnType)) {
      values = fValues;
    } else if (second.equals(columnType)) {
      values = sValues;
    } else {
      throw new Error("Invalid column type: " + columnType);
    }
    final int arrayIndex = getArrayIndex(data, all, data.getIndexStride(), index);
    values[arrayIndex] = value;
  }

  public static void setEditedValue(
      SGISXYZTypeData data,
      double[] xValues,
      double[] yValues,
      double[][] zGridValues,
      final boolean all,
      SGDataValueHistory dataValue) {
    final double value = dataValue.getValue();
    String columnType = dataValue.getColumnType();
    if (X_VALUE.equals(columnType)) {
      ISingleDimension dataValueD1 = (ISingleDimension) dataValue;
      final int index = dataValueD1.getIndex();
      final int arrayIndex = getArrayIndex(data, all, data.getXStride(), index);
      xValues[arrayIndex] = value;
    } else if (Y_VALUE.equals(columnType)) {
      ISingleDimension dataValueD1 = (ISingleDimension) dataValue;
      final int index = dataValueD1.getIndex();
      final int arrayIndex = getArrayIndex(data, all, data.getYStride(), index);
      yValues[arrayIndex] = value;
    } else if (Z_VALUE.equals(columnType)) {
      final int col = dataValue.getColumnIndex();
      final int row = dataValue.getRowIndex();
      final int colIndex = getArrayIndex(data, all, data.getXStride(), col);
      final int rowIndex = getArrayIndex(data, all, data.getYStride(), row);
      zGridValues[rowIndex][colIndex] = value;
    }
  }

  public static void setEditedValue(
      SGISXYZTypeData data,
      double[] xValues,
      double[] yValues,
      double[] zValues,
      final boolean all,
      SGDataValueHistory dataValue) {
    final double value = dataValue.getValue();
    String columnType = dataValue.getColumnType();
    ISingleDimension dataValueD1 = (ISingleDimension) dataValue;
    final int index = dataValueD1.getIndex();
    double[] values;
    if (X_VALUE.equals(columnType)) {
      values = xValues;
    } else if (Y_VALUE.equals(columnType)) {
      values = yValues;
    } else if (Z_VALUE.equals(columnType)) {
      values = zValues;
    } else {
      throw new Error("Invalid column type: " + columnType);
    }
    final int arrayIndex = getArrayIndex(data, all, data.getIndexStride(), index);
    values[arrayIndex] = value;
  }

  private static int getArrayIndex(
      SGIData data, final boolean all, SGIntegerSeriesSet stride, final int index) {
    final int arrayIndex;
    if (!all && data.isStrideAvailable()) {
      int[] indices = stride.getNumbers();
      arrayIndex = Arrays.binarySearch(indices, index);
    } else {
      arrayIndex = index;
    }
    return arrayIndex;
  }

  public static SGDataBufferPolicy getArchiveDataSetBufferPolicy(SGData data) {
    SGDataBufferPolicy policy;
    if (data instanceof SGISXYTypeMultipleData) {
      policy = new SGSXYDataBufferPolicy(true, false, true, false, false);
    } else {
      policy = new SGDataBufferPolicy(true, false, true);
    }
    return policy;
  }

  public static double getCoordinateVariableValue(final double[] array, final int index) {
    final double value = array[index];
    return SGUtilityNumber.getNumberInRangeOrder(
        value, array, SGINetCDFConstants.DIMENSION_EFFECTIVE_DIGIT);
  }

  public static List<SGDataValueHistory> getEditedDataValueList(
      SGISXYTypeMultipleData data,
      String columnType,
      final int row,
      final int col,
      Object value,
      SGIntegerSeriesSet stride) {

    List<SGDataValueHistory> list = new ArrayList<SGDataValueHistory>();
    final int colNum = data.getDataViewerColumnNumber(columnType, true); // true is meaningless
    if (colNum == 1) {
      for (int ii = 0; ii < data.getChildNumber(); ii++) {
        SGDataValueHistory editedValue =
            SGDataViewerUtility.setDataViewerValue(data, columnType, row, ii, value, stride);
        if (editedValue != null) {
          list.add(editedValue);
        }
      }
    } else {
      SGDataValueHistory editedValue =
          SGDataViewerUtility.setDataViewerValue(data, columnType, row, col, value, stride);
      if (editedValue != null) {
        list.add(editedValue);
      }
    }
    return list;
  }

  public static int getDataViewerColumnNumber(
      final SGISXYTypeMultipleData data, final String columnType) {
    final int num;
    if (Y_VALUE.equals(columnType)) {
      if (data.hasMultipleYValues()) {
        num = data.getChildNumber();
      } else {
        num = 1;
      }
    } else {
      if (!data.hasMultipleYValues()) {
        num = data.getChildNumber();
      } else {
        num = 1;
      }
    }
    return num;
  }

  public static double[][] getYValueArray(
      SGISXYTypeMultipleData data, boolean all, boolean useCache) {

    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    double[][] ret = new double[sxyArray.length][];
    for (int ii = 0; ii < ret.length; ii++) {
      ret[ii] = sxyArray[ii].getYValueArray(all, useCache);
    }

    // disposes of data objects
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);

    return ret;
  }

  public static double[][] getYValueArray(
      SGISXYTypeMultipleData data, boolean all, boolean useCache, boolean removeInvalidValues) {

    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    double[][] ret = new double[sxyArray.length][];
    for (int ii = 0; ii < ret.length; ii++) {
      ret[ii] = sxyArray[ii].getYValueArray(all, useCache, removeInvalidValues);
    }

    // disposes of data objects
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);

    return ret;
  }

  public static double[][] getYValueArray(SGISXYTypeMultipleData data, boolean all) {

    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    double[][] ret = new double[sxyArray.length][];
    for (int ii = 0; ii < ret.length; ii++) {
      ret[ii] = sxyArray[ii].getYValueArray(all);
    }

    // disposes of data objects
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);

    return ret;
  }

  public static double[][] getXValueArray(
      SGISXYTypeMultipleData data, boolean all, boolean useCache) {

    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    double[][] ret = new double[sxyArray.length][];
    for (int ii = 0; ii < ret.length; ii++) {
      ret[ii] = sxyArray[ii].getXValueArray(all, useCache);
    }

    // disposes of data objects
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);

    return ret;
  }

  public static double[][] getXValueArray(
      SGISXYTypeMultipleData data, boolean all, boolean useCache, boolean removeInvalidValues) {

    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    double[][] ret = new double[sxyArray.length][];
    for (int ii = 0; ii < ret.length; ii++) {
      ret[ii] = sxyArray[ii].getXValueArray(all, useCache, removeInvalidValues);
    }

    // disposes of data objects
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);

    return ret;
  }

  public static double[][] getXValueArray(SGISXYTypeMultipleData data, boolean all) {

    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    double[][] ret = new double[sxyArray.length][];
    for (int ii = 0; ii < ret.length; ii++) {
      ret[ii] = sxyArray[ii].getXValueArray(all);
    }

    // disposes of data objects
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);

    return ret;
  }

  public static void syncDataValueHistory(
      List<SGDataValueHistory> historyList, SGISXYTypeSingleData[] dataArray) {
    for (int ii = 0; ii < historyList.size(); ii++) {
      SGDataValueHistory dataValue = historyList.get(ii);
      SGDataValueHistory.IMultiple multipleDataValue =
          (SGDataValueHistory.IMultiple) historyList.get(ii);
      final int childIndex = multipleDataValue.getChildIndex();
      dataArray[childIndex].addMultipleDimensionEditedDataValue(dataValue);
    }
  }

  public static boolean matches(
      final int row, final int col, String columnType, SGDataValueHistory value, final double d) {
    if (value.getRowIndex() != row) {
      return false;
    }
    if (value.getColumnIndex() != col) {
      return false;
    }
    if (!value.getColumnType().equals(columnType)) {
      return false;
    }
    return true;
  }

  public static boolean matches(
      final int row, String columnType, SGDataValueHistory value, final double d) {
    if (value.getRowIndex() != row) {
      return false;
    }
    if (!value.getColumnType().equals(columnType)) {
      return false;
    }
    return true;
  }

  public static SGDataValue getDataValue(SGIVXYTypeData data, final int xIndex, final int yIndex) {
    if (data.isIndexAvailable()) {
      return null;
    }
    final int xArrayIndex, yArrayIndex;
    if (data.isStrideAvailable()) {
      SGIntegerSeriesSet xStride = data.getXStride();
      int[] xIndices = xStride.getNumbers();
      SGIntegerSeriesSet yStride = data.getYStride();
      int[] yIndices = yStride.getNumbers();
      xArrayIndex = Arrays.binarySearch(xIndices, xIndex);
      yArrayIndex = Arrays.binarySearch(yIndices, yIndex);
    } else {
      xArrayIndex = xIndex;
      yArrayIndex = yIndex;
    }
    VXYDataValue ret = new VXYDataValue();
    ret.xValue = data.getXValueAt(xArrayIndex);
    ret.yValue = data.getYValueAt(yArrayIndex);
    ret.fValue = new Value(data.getFirstComponentValueAt(yIndex, xIndex));
    ret.sValue = new Value(data.getSecondComponentValueAt(yIndex, xIndex));
    return ret;
  }

  public static SGDataValue getDataValue(SGIVXYTypeData data, final int index) {
    if (!data.isIndexAvailable()) {
      return null;
    }
    VXYDataValue ret = new VXYDataValue();
    final int arrayIndex;
    if (data.isStrideAvailable()) {
      SGIntegerSeriesSet stride = data.getIndexStride();
      int[] indices = stride.getNumbers();
      arrayIndex = Arrays.binarySearch(indices, index);
    } else {
      arrayIndex = index;
    }
    ret.xValue = data.getXValueAt(arrayIndex);
    ret.yValue = data.getYValueAt(arrayIndex);
    ret.fValue = new Value(data.getFirstComponentValueAt(arrayIndex));
    ret.sValue = new Value(data.getSecondComponentValueAt(arrayIndex));
    return ret;
  }

  public static SGDataValue getDataValue(SGISXYZTypeData data, final int xIndex, final int yIndex) {
    if (data.isIndexAvailable()) {
      return null;
    }
    final int xArrayIndex, yArrayIndex;
    if (data.isStrideAvailable()) {
      SGIntegerSeriesSet xStride = data.getXStride();
      int[] xIndices = xStride.getNumbers();
      SGIntegerSeriesSet yStride = data.getYStride();
      int[] yIndices = yStride.getNumbers();
      xArrayIndex = Arrays.binarySearch(xIndices, xIndex);
      yArrayIndex = Arrays.binarySearch(yIndices, yIndex);
    } else {
      xArrayIndex = xIndex;
      yArrayIndex = yIndex;
    }

    Double xValue = data.getXValueAt(xArrayIndex);
    Double yValue = data.getYValueAt(yArrayIndex);
    Value zValue = new Value(data.getZValueAt(yIndex, xIndex));

    SXYZDataValue ret = new SXYZDataValue();
    ret.xValue = xValue;
    ret.yValue = yValue;
    ret.zValue = zValue;
    return ret;
  }

  public static SGDataValue getDataValue(SGISXYZTypeData data, final int index) {
    if (!data.isIndexAvailable()) {
      return null;
    }
    final int arrayIndex;
    if (data.isStrideAvailable()) {
      SGIntegerSeriesSet stride = data.getIndexStride();
      int[] indices = stride.getNumbers();
      arrayIndex = Arrays.binarySearch(indices, index);
    } else {
      arrayIndex = index;
    }
    SXYZDataValue ret = new SXYZDataValue();
    ret.xValue = data.getXValueAt(arrayIndex);
    ret.yValue = data.getYValueAt(arrayIndex);
    ret.zValue = new Value(data.getZValueAt(arrayIndex));
    return ret;
  }

  public static SGDataValue getDataValue(
      SGISXYTypeSingleData data, final int index0, final int index1) {
    SXYDoubleDataValue ret = new SXYDoubleDataValue();
    final int arrayIndex0, arrayIndex1;
    if (data.isStrideAvailable()) {
      SGIntegerSeriesSet stride =
          data.isIndexAvailable() ? data.getIndexStride() : data.getStride();
      int[] indices = stride.getNumbers();
      arrayIndex0 = Arrays.binarySearch(indices, index0);
      arrayIndex1 = Arrays.binarySearch(indices, index1);
    } else {
      arrayIndex0 = index0;
      arrayIndex1 = index1;
    }
    ret.xValue0 = data.getXValueAt(arrayIndex0);
    ret.yValue0 = data.getYValueAt(arrayIndex0);
    ret.xValue1 = data.getXValueAt(arrayIndex1);
    ret.yValue1 = data.getYValueAt(arrayIndex1);
    return ret;
  }

  public static SGDataValue getDataValue(SGISXYTypeSingleData data, final int index) {
    SXYSingleDataValue ret = new SXYSingleDataValue();
    final int arrayIndex;
    if (data.isStrideAvailable()) {
      SGIntegerSeriesSet stride =
          data.isIndexAvailable() ? data.getIndexStride() : data.getStride();
      int[] indices = stride.getNumbers();
      arrayIndex = Arrays.binarySearch(indices, index);
    } else {
      arrayIndex = index;
    }
    ret.xValue = data.getXValueAt(arrayIndex);
    ret.yValue = data.getYValueAt(arrayIndex);
    return ret;
  }

  public static boolean isValidTooltipTextString(final String str) {
    return (str != null) && !"".equals(str);
  }

  public static List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlockList(
      SGIVXYTypeData data,
      final boolean all,
      final boolean useCache,
      final boolean removeInvalidValues) {
    if (data.isIndexAvailable()) {
      throw new Error("Not supported.");
    }
    List<SGXYSimpleDoubleValueIndexBlock> ret = null;
    if (useCache) {
      ret = SGVXYDataCache.getSecondComponentValueBlockList((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getSecondComponentValueBlockListSub(all, useCache, removeInvalidValues);
    }
    return ret;
  }

  public static List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlockList(
      SGIVXYTypeData data,
      final boolean all,
      final boolean useCache,
      final boolean removeInvalidValues) {
    if (data.isIndexAvailable()) {
      throw new Error("Not supported.");
    }
    List<SGXYSimpleDoubleValueIndexBlock> ret = null;
    if (useCache) {
      ret = SGVXYDataCache.getFirstComponentValueBlockList((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getFirstComponentValueBlockListSub(all, useCache, removeInvalidValues);
    }
    return ret;
  }

  public static double[] getSecondComponentValueArray(SGIVXYTypeData data, final boolean all) {
    double[] ret = null;
    final boolean useCache = data.useSecondComponentValueCache(all);
    if (useCache) {
      ret = SGVXYDataCache.getSecondComponentValues((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getSecondComponentValueArray(all, useCache);
    }
    return ret;
  }

  public static double[] getFirstComponentValueArray(SGIVXYTypeData data, final boolean all) {
    double[] ret = null;
    final boolean useCache = data.useFirstComponentValueCache(all);
    if (useCache) {
      ret = SGVXYDataCache.getFirstComponentValues((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getFirstComponentValueArray(all, useCache);
    }
    return ret;
  }

  public static List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockList(
      SGISXYZTypeData data,
      final boolean all,
      final boolean useCache,
      final boolean removeInvalidValues) {
    if (data.isIndexAvailable()) {
      throw new Error("Not supported.");
    }
    List<SGXYSimpleDoubleValueIndexBlock> ret = null;
    if (useCache) {
      ret = SGSXYZDataCache.getZValueBlockList((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getZValueBlockListSub(all, useCache, removeInvalidValues);
    }
    return ret;
  }

  public static double[] getZValueArray(SGISXYZTypeData data, final boolean all) {
    double[] ret = null;
    final boolean useCache = data.useZValueCache(all);
    if (useCache) {
      ret = SGSXYZDataCache.getZValues((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getZValueArray(all, useCache);
    }
    return ret;
  }

  public static double[] updateYValueArray(
      SGITwoDimensionalData data, final boolean all, final double[] values) {
    double[] ret = SGUtility.copyDoubleArray(values);
    if (all && data.isStrideAvailable() && !data.isIndexAvailable()) {
      final SGIntegerSeriesSet stride = data.getYStride();
      final boolean useCache = data.useXYCache(all, stride);
      if (!useCache) {
        // update values with cache
        if (!stride.isComplete()) {
          final double[] cachedValues = data.getYValueArray(false);
          int[] indices = stride.getNumbers();
          for (int ii = 0; ii < indices.length; ii++) {
            ret[indices[ii]] = cachedValues[ii];
          }
        }
      }
    }
    return ret;
  }

  public static double[] updateXValueArray(
      SGITwoDimensionalData data, final boolean all, final double[] values) {
    double[] ret = SGUtility.copyDoubleArray(values);
    if (all && data.isStrideAvailable() && !data.isIndexAvailable()) {
      final SGIntegerSeriesSet stride = data.getXStride();
      final boolean useCache = data.useXYCache(all, stride);
      if (!useCache) {
        // update values with cache
        if (!stride.isComplete()) {
          final double[] cachedValues = data.getXValueArray(false);
          int[] indices = stride.getNumbers();
          for (int ii = 0; ii < indices.length; ii++) {
            ret[indices[ii]] = cachedValues[ii];
          }
        }
      }
    }
    return ret;
  }

  public static double[] getYValueArray(SGIVXYTypeData data, final boolean all) {
    double[] ret = null;
    final boolean useCache = data.useYValueCache(all);
    if (useCache) {
      ret = SGVXYDataCache.getYValues((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getYValueArray(all, useCache);
    }
    return ret;
  }

  public static double[] getXValueArray(SGIVXYTypeData data, final boolean all) {
    double[] ret = null;
    final boolean useCache = data.useXValueCache(all);
    if (useCache) {
      ret = SGVXYDataCache.getXValues((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getXValueArray(all, useCache);
    }
    return ret;
  }

  public static double[] getYValueArray(SGISXYZTypeData data, final boolean all) {
    double[] ret = null;
    final boolean useCache = data.useYValueCache(all);
    if (useCache) {
      ret = SGSXYZDataCache.getYValues((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getYValueArray(all, useCache);
    }
    return ret;
  }

  public static double[] getXValueArray(SGISXYZTypeData data, final boolean all) {
    double[] ret = null;
    final boolean useCache = data.useXValueCache(all);
    if (useCache) {
      ret = SGSXYZDataCache.getXValues((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getXValueArray(all, useCache);
    }
    return ret;
  }

  public static String[] getStringArray(SGISXYTypeSingleData data, final boolean all) {
    if (!data.isTickLabelAvailable()) {
      return null;
    }
    String[] ret = null;
    final boolean useCache = data.useTickLabelCache(all);
    if (useCache) {
      ret = SGSXYDataCache.getTickLabels((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getStringArray(all, useCache);
    }
    if (ret == null) {
      return null;
    }
    // if (data.isStrideAvailable() && !all) {
    // int[] indices = data.getTickLabelStride().getNumbers();
    // String[] strArray = new String[indices.length];
    // for (int ii = 0; ii < strArray.length; ii++) {
    // strArray[ii] = ret[indices[ii]];
    // }
    // ret = strArray;
    // }
    return ret;
  }

  public static double[] getYValueArray(SGISXYTypeSingleData data, final boolean all) {
    double[] ret = null;
    final boolean useCache = data.useValueCache(all);
    if (useCache) {
      ret = SGSXYDataCache.getYValues((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getYValueArray(all, useCache);
    }
    return ret;
  }

  public static double[] getXValueArray(SGISXYTypeSingleData data, final boolean all) {
    double[] ret = null;
    final boolean useCache = data.useValueCache(all);
    if (useCache) {
      ret = SGSXYDataCache.getXValues((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getXValueArray(all, useCache);
    }
    return ret;
  }

  public static void updateCache(
      SGISXYTypeMultipleData multipleData, SGISXYTypeSingleData[] sxyDataArray) {
    final int cNum = multipleData.getChildNumber();
    if (sxyDataArray.length != cNum) {
      throw new IllegalArgumentException(
          "sxyDataArray.length != cNum : " + sxyDataArray.length + ", " + cNum);
    }
    SGSXYMultipleDataCache cache = new SGSXYMultipleDataCache();
    cache.mCacheArray = new SGSXYDataCache[cNum];
    for (int ii = 0; ii < cNum; ii++) {
      SGDataCache c = ((SGArrayData) sxyDataArray[ii]).getCache();
      cache.mCacheArray[ii] = (SGSXYDataCache) c;
    }
    ((SGArrayData) multipleData).setCache(cache);
  }

  public static SGDataValueHistory setDataViewerValue(
      SGIVXYTypeData data, String columnType, final int row, final int col, Object value) {

    if (value == null) {
      return null;
    }
    String text = value.toString();
    Double d = SGUtilityText.getDouble(text);
    if (d == null) {
      return null;
    }
    SGArrayData aData = (SGArrayData) data;
    if (aData.getCache() == null) {
      aData.restoreCache();
    }
    SGVXYDataCache cache = SGVXYDataCache.getCache(aData);
    if (cache == null) {
      return null;
    }
    final boolean polar = data.isPolar();
    final String first = SGDataStrideUtility.getVXYFirstComponentColumnType(polar);
    final String second = SGDataStrideUtility.getVXYSecondComponentColumnType(polar);

    SGDataValueHistory ret = null;
    if (data.isIndexAvailable()) {
      final int index;
      if (data.isStrideAvailable()) {
        int[] indices = data.getIndexStride().getNumbers();
        index = Arrays.binarySearch(indices, row);
      } else {
        index = row;
      }
      boolean diff = false;
      double prev = 0.0;
      if (X_COORDINATE.equals(columnType)) {
        if (index < cache.mXValues.length) {
          if (cache.mXValues[index] != d) {
            prev = cache.mXValues[index];
            cache.mXValues[index] = d;
            diff = true;
          }
        }
      } else if (Y_COORDINATE.equals(columnType)) {
        if (index < cache.mYValues.length) {
          if (cache.mYValues[index] != d) {
            prev = cache.mYValues[index];
            cache.mYValues[index] = d;
            diff = true;
          }
        }
      } else if (first.equals(columnType)) {
        if (index < cache.mFirstComponentValues.length) {
          if (cache.mFirstComponentValues[index] != d) {
            prev = cache.mFirstComponentValues[index];
            cache.mFirstComponentValues[index] = d;
            diff = true;
          }
        }
      } else if (second.equals(columnType)) {
        if (index < cache.mSecondComponentValues.length) {
          if (cache.mSecondComponentValues[index] != d) {
            prev = cache.mSecondComponentValues[index];
            cache.mSecondComponentValues[index] = d;
            diff = true;
          }
        }
      } else {
        return null;
      }
      if (diff) {
        if (data instanceof SGVXYNetCDFData) {
          SGVXYNetCDFData ncData = (SGVXYNetCDFData) data;
          SGNetCDFVariable xVar = ncData.getXVariable();
          SGNetCDFVariable yVar = ncData.getYVariable();
          SGNetCDFVariable fVar = ncData.getFirstComponentVariable();
          SGNetCDFVariable sVar = ncData.getSecondComponentVariable();
          SGNetCDFVariable var = null;
          if (X_COORDINATE.equals(columnType)) {
            var = xVar;
          } else if (Y_COORDINATE.equals(columnType)) {
            var = yVar;
          } else if (first.equals(columnType)) {
            var = fVar;
          } else if (second.equals(columnType)) {
            var = sVar;
          }
          ret = createIndexDataValueHistory(ncData, d, columnType, col, row, var, prev);
        } else if (data instanceof SGVXYMDArrayData) {
          SGVXYMDArrayData mdData = (SGVXYMDArrayData) data;
          SGMDArrayVariable xVar = mdData.getXVariable();
          SGMDArrayVariable yVar = mdData.getYVariable();
          SGMDArrayVariable fVar = mdData.getFirstComponentVariable();
          SGMDArrayVariable sVar = mdData.getSecondComponentVariable();
          SGMDArrayVariable var = null;
          if (X_COORDINATE.equals(columnType)) {
            var = xVar;
          } else if (Y_COORDINATE.equals(columnType)) {
            var = yVar;
          } else if (first.equals(columnType)) {
            var = fVar;
          } else if (second.equals(columnType)) {
            var = sVar;
          }
          ret = createOneDimensionalDataValueHistory(mdData, d, columnType, col, row, var, prev);
        } else {
          ret = new SGDataValueHistory.SDArray.D1(d, columnType, row, prev);
        }
      }

    } else {
      if (X_COORDINATE.equals(columnType)) {
        final int index;
        if (data.isStrideAvailable()) {
          int[] indices = data.getXStride().getNumbers();
          index = Arrays.binarySearch(indices, row);
        } else {
          index = row;
        }
        if (index < cache.mXValues.length) {
          if (cache.mXValues[index] != d) {
            final double prev = cache.mXValues[index];
            cache.mXValues[index] = d;
            if (data instanceof SGVXYNetCDFData) {
              SGVXYNetCDFData ncData = (SGVXYNetCDFData) data;
              SGNetCDFVariable var = ncData.getXVariable();
              ret =
                  createOneDimensionalDataValueHistory(ncData, d, columnType, col, row, var, prev);
            } else if (data instanceof SGVXYMDArrayData) {
              SGVXYMDArrayData mdData = (SGVXYMDArrayData) data;
              SGMDArrayVariable var = mdData.getXVariable();
              ret =
                  createOneDimensionalDataValueHistory(mdData, d, columnType, col, row, var, prev);
            }
          }
        }

      } else if (Y_COORDINATE.equals(columnType)) {
        final int index;
        if (data.isStrideAvailable()) {
          int[] indices = data.getYStride().getNumbers();
          index = Arrays.binarySearch(indices, row);
        } else {
          index = row;
        }
        if (index < cache.mYValues.length) {
          if (cache.mYValues[index] != d) {
            final double prev = cache.mYValues[index];
            cache.mYValues[index] = d;
            if (data instanceof SGVXYNetCDFData) {
              SGVXYNetCDFData ncData = (SGVXYNetCDFData) data;
              SGNetCDFVariable var = ncData.getYVariable();
              ret =
                  createOneDimensionalDataValueHistory(ncData, d, columnType, col, row, var, prev);
            } else if (data instanceof SGVXYMDArrayData) {
              SGVXYMDArrayData mdData = (SGVXYMDArrayData) data;
              SGMDArrayVariable var = mdData.getYVariable();
              ret =
                  createOneDimensionalDataValueHistory(mdData, d, columnType, col, row, var, prev);
            }
          }
        }

      } else if (first.equals(columnType)) {
        for (SGXYSimpleDoubleValueIndexBlock block : cache.mFirstComponentValueBlockList) {
          final Double prev = block.getValue(col, row);
          if (block.setValue(d, col, row)) {
            if (data instanceof SGVXYNetCDFData) {
              SGVXYNetCDFData ncData = (SGVXYNetCDFData) data;
              SGNetCDFVariable var = ncData.getFirstComponentVariable();
              ret =
                  createTwoDimensionalDataValueHistory(ncData, d, columnType, col, row, var, prev);
            } else if (data instanceof SGVXYMDArrayData) {
              SGVXYMDArrayData mdData = (SGVXYMDArrayData) data;
              SGMDArrayVariable var = mdData.getFirstComponentVariable();
              ret =
                  createTwoDimensionalDataValueHistory(mdData, d, columnType, col, row, var, prev);
            }
            break;
          }
        }
      } else if (second.equals(columnType)) {
        for (SGXYSimpleDoubleValueIndexBlock block : cache.mSecondComponentValueBlockList) {
          final Double prev = block.getValue(col, row);
          if (block.setValue(d, col, row)) {
            if (data instanceof SGVXYNetCDFData) {
              SGVXYNetCDFData ncData = (SGVXYNetCDFData) data;
              SGNetCDFVariable var = ncData.getSecondComponentVariable();
              ret =
                  createTwoDimensionalDataValueHistory(ncData, d, columnType, col, row, var, prev);
            } else if (data instanceof SGVXYMDArrayData) {
              SGVXYMDArrayData mdData = (SGVXYMDArrayData) data;
              SGMDArrayVariable var = mdData.getSecondComponentVariable();
              ret =
                  createTwoDimensionalDataValueHistory(mdData, d, columnType, col, row, var, prev);
            }
            break;
          }
        }
      }
    }

    return ret;
  }

  public static SGDataValueHistory setDataViewerValue(
      SGISXYZTypeData data, String columnType, final int row, final int col, Object value) {

    if (value == null) {
      return null;
    }
    String text = value.toString();
    Double d = SGUtilityText.getDouble(text);
    if (d == null) {
      return null;
    }
    SGArrayData aData = (SGArrayData) data;
    if (aData.getCache() == null) {
      aData.restoreCache();
    }
    SGSXYZDataCache cache = SGSXYZDataCache.getCache(aData);
    if (cache == null) {
      return null;
    }

    SGDataValueHistory ret = null;
    if (data.isIndexAvailable()) {
      final int index;
      if (data.isStrideAvailable()) {
        int[] indices = data.getIndexStride().getNumbers();
        index = Arrays.binarySearch(indices, row);
      } else {
        index = row;
      }
      boolean diff = false;
      double prev = 0.0;
      if (X_VALUE.equals(columnType)) {
        if (index < cache.mXValues.length) {
          if (cache.mXValues[index] != d) {
            prev = cache.mXValues[index];
            cache.mXValues[index] = d;
            diff = true;
          }
        }
      } else if (Y_VALUE.equals(columnType)) {
        if (index < cache.mYValues.length) {
          if (cache.mYValues[index] != d) {
            prev = cache.mYValues[index];
            cache.mYValues[index] = d;
            diff = true;
          }
        }
      } else if (Z_VALUE.equals(columnType)) {
        if (index < cache.mZValues.length) {
          if (cache.mZValues[index] != d) {
            prev = cache.mZValues[index];
            cache.mZValues[index] = d;
            diff = true;
          }
        }
      } else {
        return null;
      }
      if (diff) {
        if (data instanceof SGSXYZNetCDFData) {
          SGSXYZNetCDFData ncData = (SGSXYZNetCDFData) data;
          SGNetCDFVariable xVar = ncData.getXVariable();
          SGNetCDFVariable yVar = ncData.getYVariable();
          SGNetCDFVariable zVar = ncData.getZVariable();
          SGNetCDFVariable var = null;
          if (X_VALUE.equals(columnType)) {
            var = xVar;
          } else if (Y_VALUE.equals(columnType)) {
            var = yVar;
          } else if (Z_VALUE.equals(columnType)) {
            var = zVar;
          }
          ret = createIndexDataValueHistory(ncData, d, columnType, col, row, var, prev);
        } else if (data instanceof SGSXYZMDArrayData) {
          SGSXYZMDArrayData mdData = (SGSXYZMDArrayData) data;
          SGMDArrayVariable xVar = mdData.getXVariable();
          SGMDArrayVariable yVar = mdData.getYVariable();
          SGMDArrayVariable zVar = mdData.getZVariable();
          SGMDArrayVariable var = null;
          if (X_VALUE.equals(columnType)) {
            var = xVar;
          } else if (Y_VALUE.equals(columnType)) {
            var = yVar;
          } else if (Z_VALUE.equals(columnType)) {
            var = zVar;
          }
          ret = createOneDimensionalDataValueHistory(mdData, d, columnType, col, row, var, prev);
        } else {
          ret = new SGDataValueHistory.SDArray.D1(d, columnType, row, prev);
        }
      }

    } else {

      if (X_VALUE.equals(columnType)) {
        final int index;
        if (data.isStrideAvailable()) {
          int[] indices = data.getXStride().getNumbers();
          index = Arrays.binarySearch(indices, row);
        } else {
          index = row;
        }
        if (index < cache.mXValues.length) {
          if (cache.mXValues[index] != d) {
            final double prev = cache.mXValues[index];
            cache.mXValues[index] = d;
            if (data instanceof SGSXYZNetCDFData) {
              SGSXYZNetCDFData ncData = (SGSXYZNetCDFData) data;
              SGNetCDFVariable var = ncData.getXVariable();
              ret =
                  createOneDimensionalDataValueHistory(ncData, d, columnType, col, row, var, prev);
            } else if (data instanceof SGSXYZMDArrayData) {
              SGSXYZMDArrayData mdData = (SGSXYZMDArrayData) data;
              SGMDArrayVariable var = mdData.getXVariable();
              ret =
                  createOneDimensionalDataValueHistory(mdData, d, columnType, col, row, var, prev);
            }
          }
        }

      } else if (Y_VALUE.equals(columnType)) {
        final int index;
        if (data.isStrideAvailable()) {
          int[] indices = data.getYStride().getNumbers();
          index = Arrays.binarySearch(indices, row);
        } else {
          index = row;
        }
        if (index < cache.mYValues.length) {
          if (cache.mYValues[index] != d) {
            final double prev = cache.mYValues[index];
            cache.mYValues[index] = d;
            if (data instanceof SGSXYZNetCDFData) {
              SGSXYZNetCDFData ncData = (SGSXYZNetCDFData) data;
              SGNetCDFVariable var = ncData.getYVariable();
              ret =
                  createOneDimensionalDataValueHistory(ncData, d, columnType, col, row, var, prev);
            } else if (data instanceof SGSXYZMDArrayData) {
              SGSXYZMDArrayData mdData = (SGSXYZMDArrayData) data;
              SGMDArrayVariable var = mdData.getYVariable();
              ret =
                  createOneDimensionalDataValueHistory(mdData, d, columnType, col, row, var, prev);
            }
          }
        }

      } else if (Z_VALUE.equals(columnType)) {
        for (SGXYSimpleDoubleValueIndexBlock block : cache.mZValueBlockList) {
          final Double prev = block.getValue(col, row);
          if (block.setValue(d, col, row)) {
            if (data instanceof SGSXYZNetCDFData) {
              SGSXYZNetCDFData ncData = (SGSXYZNetCDFData) data;
              SGNetCDFVariable var = ncData.getZVariable();
              ret =
                  createTwoDimensionalDataValueHistory(ncData, d, columnType, col, row, var, prev);
            } else if (data instanceof SGSXYZMDArrayData) {
              SGSXYZMDArrayData mdData = (SGSXYZMDArrayData) data;
              SGMDArrayVariable var = mdData.getZVariable();
              ret =
                  createTwoDimensionalDataValueHistory(mdData, d, columnType, col, row, var, prev);
            }
            break;
          }
        }
      }
    }

    return ret;
  }

  private static SGDataValueHistory createOneDimensionalDataValueHistory(
      SGMDArrayData mdData,
      Double d,
      String columnType,
      final int col,
      final int row,
      SGMDArrayVariable var,
      final double prev) {
    String varName = var.getName();
    SGDataValueHistory.MDArray.D1 curValue =
        new SGDataValueHistory.MDArray.D1(d, columnType, row, varName, prev);
    final int dimension = var.getGenericDimensionIndex();
    curValue.setDimension(dimension);
    return curValue;
  }

  private static SGDataValueHistory createIndexDataValueHistory(
      SGNetCDFData ncData,
      Double d,
      String columnType,
      final int col,
      final int row,
      SGNetCDFVariable var,
      final double prev) {
    String varName = var.getName();
    SGDataValueHistory.NetCDF.D1 curValue =
        new SGDataValueHistory.NetCDF.D1(d, columnType, row, varName, prev);
    curValue.setIndexDimName(varName);
    SGNetCDFVariable timeVar = ncData.getTimeVariable();
    if (timeVar != null) {
      String timeDimName = timeVar.getName();
      final int timeOrigin = ncData.getOrigin(timeDimName);
      curValue.setAnimationInfo(timeDimName, timeOrigin);
    }
    return curValue;
  }

  private static SGDataValueHistory createOneDimensionalDataValueHistory(
      SGNetCDFData ncData,
      Double d,
      String columnType,
      final int col,
      final int row,
      SGNetCDFVariable var,
      final double prev) {
    String varName = var.getName();
    SGDataValueHistory.NetCDF.D1 curValue =
        new SGDataValueHistory.NetCDF.D1(d, columnType, row, varName, prev);
    return curValue;
  }

  private static SGDataValueHistory createTwoDimensionalDataValueHistory(
      SGMDArrayData mdData,
      Double d,
      String columnType,
      final int col,
      final int row,
      SGMDArrayVariable var,
      final double prev) {
    String varName = var.getName();
    SGDataValueHistory.MDArray.D2 curValue =
        new SGDataValueHistory.MDArray.D2(d, columnType, col, row, varName, prev);
    final int xDim = var.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION);
    final int yDim = var.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION);
    curValue.setXYDimension(xDim, yDim);
    return curValue;
  }

  private static SGDataValueHistory createTwoDimensionalDataValueHistory(
      SGTwoDimensionalNetCDFData ncData,
      Double d,
      String columnType,
      final int col,
      final int row,
      SGNetCDFVariable var,
      final double prev) {
    String varName = var.getName();
    SGDataValueHistory.NetCDF.D2 curValue =
        new SGDataValueHistory.NetCDF.D2(d, columnType, col, row, varName, prev);
    SGNetCDFVariable xVar = ncData.getXVariable();
    String xDimName = xVar.getName();
    SGNetCDFVariable yVar = ncData.getYVariable();
    String yDimName = yVar.getName();
    curValue.setXYDimName(xDimName, yDimName);
    SGNetCDFVariable timeVar = ncData.getTimeVariable();
    if (timeVar != null) {
      String timeDimName = timeVar.getName();
      final int timeOrigin = ncData.getOrigin(timeDimName);
      curValue.setAnimationInfo(timeDimName, timeOrigin);
    }
    return curValue;
  }

  public static SGDataValueHistory setDataViewerValue(
      SGISXYTypeMultipleData data,
      String columnType,
      final int row,
      final int col,
      Object value,
      SGIntegerSeriesSet stride) {

    if (value == null) {
      return null;
    }
    String text = value.toString();
    Double d = SGUtilityText.getDouble(text);
    if (d == null) {
      return null;
    }
    SGArrayData aData = (SGArrayData) data;
    if (aData.getCache() == null) {
      aData.restoreCache();
    }

    // subtract the shift value
    if (X_VALUE.equals(columnType)) {
      d -= data.getShift().x;
    } else if (Y_VALUE.equals(columnType)) {
      d -= data.getShift().y;
    }

    final int index;
    if (data.isStrideAvailable()) {
      int[] indices = stride.getNumbers();
      index = Arrays.binarySearch(indices, row);
    } else {
      index = row;
    }

    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    SGISXYTypeSingleData child = sxyArray[col];
    DoubleValueSetResult setResult = child.setDataViewerDoubleValue(columnType, index, d);
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);

    SGDataValueHistory ret = null;
    if (setResult.status) {
      if (data instanceof SGSXYNetCDFMultipleData) {
        SGSXYNetCDFMultipleData ncData = (SGSXYNetCDFMultipleData) data;
        SGNetCDFVariable cVar;
        if (ncData.isIndexAvailable()) {
          cVar = ncData.getIndexVariable();
        } else {
          SGNetCDFVariable xVar = ncData.getXVariable();
          if (xVar.isCoordinateVariable()) {
            cVar = xVar;
          } else {
            SGNetCDFVariable yVar = ncData.getYVariable();
            cVar = yVar;
          }
        }
        String dimName = cVar.getName();

        final SGNetCDFVariable var;
        if (X_VALUE.equals(columnType)) {
          SGNetCDFVariable[] vars = ncData.getXVariables();
          if (ncData.hasMultipleYValues()) {
            var = vars[0];
          } else {
            if (ncData.isDimensionPicked()) {
              var = vars[0];
            } else {
              var = vars[col];
            }
          }
        } else if (Y_VALUE.equals(columnType)) {
          SGNetCDFVariable[] vars = ncData.getYVariables();
          if (ncData.hasMultipleYValues()) {
            if (ncData.isDimensionPicked()) {
              var = vars[0];
            } else {
              var = vars[col];
            }
          } else {
            var = vars[0];
          }
        } else {
          throw new Error("Invalid column type: " + columnType);
        }
        String varName = var.getName();

        SGDataValueHistory.NetCDF.MD1 cur =
            new SGDataValueHistory.NetCDF.MD1(d, columnType, col, row, varName, setResult.prev);
        cur.setDimInfo(dimName);
        SGNetCDFPickUpDimensionInfo pickUpInfo =
            (SGNetCDFPickUpDimensionInfo) ncData.getPickUpDimensionInfo();
        if (pickUpInfo != null) {
          String pickUpDimName = pickUpInfo.getDimensionName();
          final int[] pickUpIndices = pickUpInfo.getIndices().getNumbers();
          final int pickUpOrigin = pickUpIndices[col];
          cur.setPickUpInfo(pickUpDimName, pickUpOrigin);
        }
        SGNetCDFVariable timeVar = ncData.getTimeVariable();
        if (timeVar != null) {
          String timeDimName = timeVar.getName();
          final int timeOrigin = ncData.getOrigin(timeDimName);
          cur.setAnimationInfo(timeDimName, timeOrigin);
        }
        ret = cur;

      } else if (data instanceof SGSXYMDArrayMultipleData) {
        SGSXYMDArrayMultipleData mdData = (SGSXYMDArrayMultipleData) data;
        SGMDArrayVariable xVar = mdData.getXVariable();
        SGMDArrayVariable yVar = mdData.getYVariable();
        SGMDArrayVariable var = null;
        if (X_VALUE.equals(columnType)) {
          var = xVar;
        } else if (Y_VALUE.equals(columnType)) {
          var = yVar;
        } else {
          throw new Error("Invalid column type: " + columnType);
        }
        String varName = var.getName();
        final int dimension = var.getGenericDimensionIndex();

        SGDataValueHistory.MDArray.MD1 cur =
            new SGDataValueHistory.MDArray.MD1(d, columnType, col, row, varName, setResult.prev);
        cur.setDimension(dimension);
        Integer pickUpDimension =
            var.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
        if (pickUpDimension != null && pickUpDimension != -1) {
          SGMDArrayPickUpDimensionInfo pickUpInfo =
              (SGMDArrayPickUpDimensionInfo) mdData.getPickUpDimensionInfo();
          final int[] pickUpIndices = pickUpInfo.getIndices().getNumbers();
          final int pickUpOrigin = pickUpIndices[col];
          cur.setPickUpInfo(pickUpDimension, pickUpOrigin);
        }
        Integer timeDimension = var.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
        if (timeDimension != null && timeDimension != -1) {
          final int timeOrigin = var.getOrigin(timeDimension);
          cur.setAnimationInfo(timeDimension, timeOrigin);
        }
        ret = cur;

      } else {
        ret = new SGDataValueHistory.SDArray.MD1(d, columnType, col, row, setResult.prev);
      }
    }
    return ret;
  }

  public static Double getDataViewerValue(
      final SGIVXYTypeData vxyData, final String columnType, final int row, final int col) {
    final int rIndex, cIndex;
    if (vxyData.isStrideAvailable()) {
      SGIntegerSeriesSet rowStride = vxyData.getDataViewerRowStride(columnType);
      if (rowStride != null) {
        rIndex = rowStride.search(row);
        if (rIndex < 0) {
          return null;
        }
      } else {
        rIndex = row;
      }
      SGIntegerSeriesSet colStride = vxyData.getDataViewerColStride(columnType);
      if (colStride != null) {
        cIndex = colStride.search(col);
        if (cIndex < 0) {
          return null;
        }
      } else {
        cIndex = col;
      }
    } else {
      rIndex = row;
      cIndex = col;
    }
    if (SGIDataColumnTypeConstants.X_COORDINATE.equals(columnType)) {
      return vxyData.getXValueAt(rIndex);
    } else if (SGIDataColumnTypeConstants.Y_COORDINATE.equals(columnType)) {
      return vxyData.getYValueAt(rIndex);
    } else {
      final boolean polar = vxyData.isPolar();
      final String first = SGDataStrideUtility.getVXYFirstComponentColumnType(polar);
      final String second = SGDataStrideUtility.getVXYSecondComponentColumnType(polar);
      if (first.equals(columnType)) {
        if (vxyData.isIndexAvailable()) {
          return vxyData.getFirstComponentValueAt(rIndex);
        } else {
          return vxyData.getFirstComponentValueAt(row, col);
        }
      } else if (second.equals(columnType)) {
        if (vxyData.isIndexAvailable()) {
          return vxyData.getSecondComponentValueAt(rIndex);
        } else {
          return vxyData.getSecondComponentValueAt(row, col);
        }
      } else {
        return null;
      }
    }
  }

  public static String getPreferredDataViewColumnType(final SGIVXYTypeData vxyData) {
    final boolean polar = vxyData.isPolar();
    final String first = SGDataStrideUtility.getVXYFirstComponentColumnType(polar);
    return first;
  }

  public static String getPreferredDataViewColumnType(final SGISXYZTypeData sxyData) {
    return SGIDataColumnTypeConstants.Z_VALUE;
  }

  public static String getPreferredDataViewColumnType(final SGISXYTypeMultipleData sxyData) {
    String ret = null;
    if (sxyData.hasMultipleYValues()) {
      ret = SGIDataColumnTypeConstants.Y_VALUE;
    } else {
      ret = SGIDataColumnTypeConstants.X_VALUE;
    }
    return ret;
  }

  public static Double getDataViewerValue(
      final SGISXYZTypeData sxyzData, final String columnType, final int row, final int col) {
    final int rIndex, cIndex;
    if (sxyzData.isStrideAvailable()) {
      SGIntegerSeriesSet rowStride = sxyzData.getDataViewerRowStride(columnType);
      if (rowStride != null) {
        rIndex = rowStride.search(row);
        if (rIndex < 0) {
          return null;
        }
      } else {
        rIndex = row;
      }
      SGIntegerSeriesSet colStride = sxyzData.getDataViewerColStride(columnType);
      if (colStride != null) {
        cIndex = colStride.search(col);
        if (cIndex < 0) {
          return null;
        }
      } else {
        cIndex = col;
      }
    } else {
      rIndex = row;
      cIndex = col;
    }
    if (SGIDataColumnTypeConstants.X_VALUE.equals(columnType)) {
      return sxyzData.getXValueAt(rIndex);
    } else if (SGIDataColumnTypeConstants.Y_VALUE.equals(columnType)) {
      return sxyzData.getYValueAt(rIndex);
    } else if (SGIDataColumnTypeConstants.Z_VALUE.equals(columnType)) {
      if (sxyzData.isIndexAvailable()) {
        return sxyzData.getZValueAt(rIndex);
      } else {
        return sxyzData.getZValueAt(row, col);
      }
    } else {
      return null;
    }
  }

  public static Double getDataViewerValue(
      final SGISXYTypeMultipleData sxyData, final String columnType, final int row, final int col) {
    final int rIndex;
    if (sxyData.isStrideAvailable()) {
      SGIntegerSeriesSet indices = sxyData.getDataViewerRowStride(columnType);
      rIndex = indices.search(row);
      if (rIndex < 0) {
        return null;
      }
    } else {
      rIndex = row;
    }
    if (SGIDataColumnTypeConstants.X_VALUE.equals(columnType)) {
      // add the shift value
      return sxyData.getXValueAt(col, rIndex) + sxyData.getShift().x;
    } else if (SGIDataColumnTypeConstants.Y_VALUE.equals(columnType)) {
      // add the shift value
      return sxyData.getYValueAt(col, rIndex) + sxyData.getShift().y;
    } else {
      return null;
    }
  }

  /**
   * Returns true if all stride of given data are available and each string representation is
   * different from "0:end".
   *
   * @param data a data
   * @return true if all stride are effective
   */
  public static boolean hasEffectiveStride(SGISXYTypeData data) {
    if (!data.isStrideAvailable()) {
      return false;
    }
    SGIntegerSeriesSet stride = data.getStride();
    SGIntegerSeriesSet tickLabelStride = data.getTickLabelStride();
    SGIntegerSeriesSet indexStride = null;
    if (data instanceof SGNetCDFData) {
      SGNetCDFData ncData = (SGNetCDFData) data;
      indexStride = ncData.getIndexStride();
    }
    final boolean tlFlag = !tickLabelStride.isComplete();
    if (stride != null) {
      return !stride.isComplete() || tlFlag;
    } else if (indexStride != null) {
      return !indexStride.isComplete() || tlFlag;
    } else {
      throw new Error("This shouldn't happen.");
    }
  }
}
