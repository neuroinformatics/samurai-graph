package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnInfoUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTitleUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataMiscUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataRangeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataStrideUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataTextUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataViewerUtility.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Static helper for the Buffer responsibility. */
public final class SGDataBufferUtility
    implements SGIDataColumnTypeConstants,
        SGIDataPropertyKeyConstants,
        SGINetCDFConstants,
        SGIMDArrayConstants {

  private static final Logger logger = LogManager.getLogger(SGDataBufferUtility.class);

  enum STATUS {
    VALUE_EXIST,
    TICK_LABEL_EXIST,
    SKIPPED
  }

  private SGDataBufferUtility() {}

  public static double[] getUpperErrorValueArray(SGISXYTypeSingleData data, final boolean all) {
    if (!data.isErrorBarAvailable()) {
      return null;
    }
    double[] ret = null;
    final boolean useCache = data.useValueCache(all);
    if (useCache) {
      ret = SGSXYDataCache.getUpperErrorValues((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getUpperErrorValueArray(all, useCache);
    }
    return ret;
  }

  public static double[] getLowerErrorValueArray(SGISXYTypeSingleData data, final boolean all) {
    if (!data.isErrorBarAvailable()) {
      return null;
    }
    double[] ret = null;
    final boolean useCache = data.useValueCache(all);
    if (useCache) {
      ret = SGSXYDataCache.getLowerErrorValues((SGArrayData) data);
    }
    if (ret == null) {
      ret = data.getLowerErrorValueArray(all, useCache);
    }
    return ret;
  }

  public static double[][] getTwoDimensionalValues(
      List<SGXYSimpleDoubleValueIndexBlock> blocks,
      List<Integer> xIndexList,
      List<Integer> yIndexList) {
    final int xLen = xIndexList.size();
    final int yLen = yIndexList.size();
    double[][] values = new double[yLen][xLen];
    for (int ii = 0; ii < values.length; ii++) {
      Arrays.fill(values[ii], Double.NaN);
    }
    for (SGXYSimpleDoubleValueIndexBlock block : blocks) {
      SGIntegerSeries xSeries = block.getXSeries();
      SGIntegerSeries ySeries = block.getYSeries();
      final int[] xIndices = xSeries.getNumbers();
      final int[] yIndices = ySeries.getNumbers();
      final double[] blockValueArray = block.getValues();
      for (int yy = 0; yy < yIndices.length; yy++) {
        final int yIndex = yIndices[yy];
        final int yIdx = yIndexList.indexOf(yIndex);
        for (int xx = 0; xx < xIndices.length; xx++) {
          final int index = xIndices.length * yy + xx;
          final double value = blockValueArray[index];
          final int xIndex = xIndices[xx];
          final int xIdx = xIndexList.indexOf(xIndex);
          if (xIdx == -1 || yIdx == -1) {
            return null;
          }
          values[yIdx][xIdx] = value;
        }
      }
    }
    return values;
  }

  public static void getIndexList(
      List<SGXYSimpleDoubleValueIndexBlock> blocks,
      List<Integer> xIndexList,
      List<Integer> yIndexList,
      final int xAllLen,
      final int yAllLen) {
    Set<SGIntegerSeries> xSeriesSet = new HashSet<SGIntegerSeries>();
    Set<SGIntegerSeries> ySeriesSet = new HashSet<SGIntegerSeries>();
    for (SGXYSimpleDoubleValueIndexBlock block : blocks) {
      SGIntegerSeries xSeries = block.getXSeries();
      SGIntegerSeries ySeries = block.getYSeries();
      xSeriesSet.add(xSeries);
      ySeriesSet.add(ySeries);
    }
    List<SGIntegerSeries> xSeriesList = new ArrayList<SGIntegerSeries>(xSeriesSet);
    List<SGIntegerSeries> ySeriesList = new ArrayList<SGIntegerSeries>(ySeriesSet);

    Set<Integer> xIndexSet = new TreeSet<Integer>();
    for (SGIntegerSeries series : xSeriesList) {
      int[] numArray = series.getNumbers();
      for (int ii = 0; ii < numArray.length; ii++) {
        xIndexSet.add(numArray[ii]);
      }
    }
    Set<Integer> yIndexSet = new TreeSet<Integer>();
    for (SGIntegerSeries series : ySeriesList) {
      int[] numArray = series.getNumbers();
      for (int ii = 0; ii < numArray.length; ii++) {
        yIndexSet.add(numArray[ii]);
      }
    }

    Integer[] xIndexArray = xIndexSet.toArray(new Integer[xIndexSet.size()]);
    final int xIndexMin = xIndexArray[0];
    final int xIndexMax = xIndexArray[xIndexArray.length - 1];
    for (int ii = xIndexMin; ii <= xIndexMax; ii++) {
      boolean b = true;
      for (SGIntegerSeries series : xSeriesList) {
        if (series.isWithinRange(ii)) {
          b = false;
          break;
        }
      }
      if (b) {
        xIndexSet.add(ii);
      }
    }
    Integer[] yIndexArray = yIndexSet.toArray(new Integer[yIndexSet.size()]);
    final int yIndexMin = yIndexArray[0];
    final int yIndexMax = yIndexArray[yIndexArray.length - 1];
    for (int ii = yIndexMin; ii <= yIndexMax; ii++) {
      boolean b = true;
      for (SGIntegerSeries series : ySeriesList) {
        if (series.isWithinRange(ii)) {
          b = false;
          break;
        }
      }
      if (b) {
        yIndexSet.add(ii);
      }
    }

    xIndexList.addAll(xIndexSet);
    yIndexList.addAll(yIndexSet);
  }

  public static Object[][] getValueTable(
      SGIVXYTypeData data, final SGExportParameter mode, SGDataBufferPolicy policy) {
    SGDataBuffer buf = data.getDataBuffer(policy);
    if (buf == null) {
      return null;
    }
    if (buf instanceof SGVXYDataBuffer) {
      SGVXYDataBuffer buffer = (SGVXYDataBuffer) buf;
      final int len = buffer.getLength();
      double[] xValues = buffer.getXValues();
      double[] yValues = buffer.getYValues();
      double[] fValues = buffer.getFirstComponentValues();
      double[] sValues = buffer.getSecondComponentValues();
      Object[][] array = new Object[len][4];
      for (int ii = 0; ii < len; ii++) {
        array[ii][0] = xValues[ii];
        array[ii][1] = yValues[ii];
        array[ii][2] = fValues[ii];
        array[ii][3] = sValues[ii];
      }
      return array;
    } else {
      return null;
    }
  }

  public static Object[][] getValueTable(
      SGISXYZTypeData data, final SGExportParameter mode, SGDataBufferPolicy policy) {
    SGDataBuffer buf = data.getDataBuffer(policy);
    if (buf == null) {
      return null;
    }
    if (buf instanceof SGSXYZDataBuffer) {
      SGSXYZDataBuffer buffer = (SGSXYZDataBuffer) buf;
      final int len = buffer.getLength();
      double[] xValues = buffer.getXValues();
      double[] yValues = buffer.getYValues();
      double[] zValues = buffer.getZValues();
      Object[][] array = new Object[len][3];
      for (int ii = 0; ii < len; ii++) {
        array[ii][0] = xValues[ii];
        array[ii][1] = yValues[ii];
        array[ii][2] = zValues[ii];
      }
      return array;
    } else {
      return null;
    }
  }

  public static SGDate[] getDateArray(
      final SGISXYTypeMultipleData data, final SGSXYDataBufferPolicy policy) {
    SGDate[] allValues = data.getDateArray(true);
    if (allValues == null) {
      return null;
    }
    SGDate[] ret = null;
    if (policy.isTakingAllStride()) {
      if (data.isStrideAvailable()) {
        SGIntegerSeriesSet mainStride = data.getMainStride();
        SGIntegerSeriesSet tickLabelStride = data.getTickLabelStride();
        if (mainStride.isComplete() && tickLabelStride.isComplete()) {
          ret = allValues;
        } else {
          int[] mainIndices = mainStride.getNumbers();
          int[] tickLabelIndices = tickLabelStride.getNumbers();
          final int allNum = data.getAllPointsNumber();
          STATUS[] statusArray = new STATUS[allNum];
          Arrays.fill(statusArray, STATUS.SKIPPED);
          for (int ii = 0; ii < mainIndices.length; ii++) {
            statusArray[mainIndices[ii]] = STATUS.VALUE_EXIST; // status to set an empty string
          }
          for (int ii = 0; ii < tickLabelIndices.length; ii++) {
            statusArray[tickLabelIndices[ii]] =
                STATUS.TICK_LABEL_EXIST; // status to set given string
          }
          int cnt = 0;
          for (int jj = 0; jj < allNum; jj++) {
            final STATUS status = statusArray[jj];
            if (!STATUS.SKIPPED.equals(status)) {
              cnt++;
            }
          }
          SGDate[] retArray = new SGDate[cnt];
          cnt = 0;
          for (int jj = 0; jj < allNum; jj++) {
            final STATUS status = statusArray[jj];
            if (!STATUS.SKIPPED.equals(status)) {
              retArray[cnt] = allValues[jj];
              cnt++;
            }
          }
          ret = retArray;
        }
      } else {
        ret = allValues;
      }
    } else {
      ret = allValues;
    }
    return ret;
  }

  public static String[][] getTickLabelArray(
      final SGISXYTypeMultipleData data, final SGSXYDataBufferPolicy policy) {
    if (!data.isTickLabelAvailable()) {
      return null;
    }
    String[][] ret = null;
    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    String[][] allValues = new String[sxyArray.length][];
    for (int ii = 0; ii < sxyArray.length; ii++) {
      if (sxyArray[ii].isTickLabelAvailable()) {
        allValues[ii] = sxyArray[ii].getStringArray(true);
      }
    }
    if (policy.isTakingAllStride()) {
      if (data.isStrideAvailable()) {
        SGIntegerSeriesSet mainStride = data.getMainStride();
        SGIntegerSeriesSet tickLabelStride = data.getTickLabelStride();
        if (mainStride.isComplete() && tickLabelStride.isComplete()) {
          ret = allValues;
        } else {
          int[] mainIndices = mainStride.getNumbers();
          int[] tickLabelIndices = tickLabelStride.getNumbers();
          final int allNum = data.getAllPointsNumber();
          STATUS[] statusArray = new STATUS[allNum];
          Arrays.fill(statusArray, STATUS.SKIPPED);
          for (int ii = 0; ii < mainIndices.length; ii++) {
            statusArray[mainIndices[ii]] = STATUS.VALUE_EXIST; // status to set an empty string
          }
          for (int ii = 0; ii < tickLabelIndices.length; ii++) {
            statusArray[tickLabelIndices[ii]] =
                STATUS.TICK_LABEL_EXIST; // status to set given string
          }
          ret = new String[allValues.length][];
          for (int ii = 0; ii < allValues.length; ii++) {
            if (!sxyArray[ii].isTickLabelAvailable()) {
              continue;
            }
            String[] allValueArray = allValues[ii];
            int cnt = 0;
            for (int jj = 0; jj < allNum; jj++) {
              final STATUS status = statusArray[jj];
              if (!STATUS.SKIPPED.equals(status)) {
                cnt++;
              }
            }
            String[] retArray = new String[cnt];
            cnt = 0;
            for (int jj = 0; jj < allNum; jj++) {
              final STATUS status = statusArray[jj];
              if (STATUS.TICK_LABEL_EXIST.equals(status)) {
                retArray[cnt] = allValueArray[jj];
                cnt++;
              } else if (STATUS.VALUE_EXIST.equals(status)) {
                retArray[cnt] = "";
                cnt++;
              }
            }
            ret[ii] = retArray;
          }
        }
      } else {
        ret = allValues;
      }
    } else {
      ret = allValues;
    }

    // disposes of data objects
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);

    return ret;
  }

  private static double[][] getErrorValues(
      final SGISXYTypeMultipleData data,
      final SGSXYDataBufferPolicy policy,
      final double[][] allValues) {
    double[][] ret = null;
    if (policy.isTakingAllStride()) {
      if (data.isStrideAvailable()) {
        SGIntegerSeriesSet mainStride = data.getMainStride();
        SGIntegerSeriesSet tickLabelStride = data.getTickLabelStride();
        if (mainStride.isComplete() && tickLabelStride.isComplete()) {
          ret = allValues;
        } else {
          int[] mainIndices = mainStride.getNumbers();
          int[] tickLabelIndices = tickLabelStride.getNumbers();
          final int allNum = data.getAllPointsNumber();
          STATUS[] statusArray = new STATUS[allNum];
          Arrays.fill(statusArray, STATUS.SKIPPED);
          for (int ii = 0; ii < tickLabelIndices.length; ii++) {
            statusArray[tickLabelIndices[ii]] = STATUS.TICK_LABEL_EXIST;
          }
          for (int ii = 0; ii < mainIndices.length; ii++) {
            statusArray[mainIndices[ii]] = STATUS.VALUE_EXIST;
          }
          ret = new double[allValues.length][];
          for (int ii = 0; ii < allValues.length; ii++) {
            double[] allValueArray = allValues[ii];
            if (allValueArray == null) {
              continue;
            }
            int cnt = 0;
            for (int jj = 0; jj < allNum; jj++) {
              final STATUS status = statusArray[jj];
              if (!STATUS.SKIPPED.equals(status)) {
                cnt++;
              }
            }
            double[] retArray = new double[cnt];
            cnt = 0;
            for (int jj = 0; jj < allNum; jj++) {
              final STATUS status = statusArray[jj];
              if (!STATUS.SKIPPED.equals(status)) {
                retArray[cnt] = allValueArray[jj];
                cnt++;
              }
            }
            ret[ii] = retArray;
          }
        }
      } else {
        ret = allValues;
      }
    } else {
      ret = allValues;
    }
    return ret;
  }

  public static double[][] getUpperErrorValueArray(
      final SGISXYTypeMultipleData data, final SGSXYDataBufferPolicy policy) {
    if (!data.isErrorBarAvailable()) {
      return null;
    }
    final boolean all = policy.isAllValuesGotten();
    final boolean remove = policy.isInvalidValuesRemoved();
    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    double[][] allValues = new double[sxyArray.length][];
    for (int ii = 0; ii < sxyArray.length; ii++) {
      if (sxyArray[ii].isErrorBarAvailable()) {
        allValues[ii] = sxyArray[ii].getUpperErrorValueArray(all, false, remove);
      }
    }

    // disposes of data objects
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);
    return getErrorValues(data, policy, allValues);
  }

  public static double[][] getLowerErrorValueArray(
      final SGISXYTypeMultipleData data, final SGSXYDataBufferPolicy policy) {
    if (!data.isErrorBarAvailable()) {
      return null;
    }
    final boolean all = policy.isAllValuesGotten();
    final boolean remove = policy.isInvalidValuesRemoved();
    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    double[][] allValues = new double[sxyArray.length][];
    for (int ii = 0; ii < sxyArray.length; ii++) {
      if (sxyArray[ii].isErrorBarAvailable()) {
        allValues[ii] = sxyArray[ii].getLowerErrorValueArray(all, false, remove);
      }
    }

    // disposes of data objects
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);
    return getErrorValues(data, policy, allValues);
  }

  private static double[] shiftValues(final double[] values, final double shift) {
    double[] ret = new double[values.length];
    for (int ii = 0; ii < ret.length; ii++) {
      ret[ii] = values[ii] + shift;
    }
    return ret;
  }

  private static double[][] getUnshiftedXYValues(
      final SGISXYTypeMultipleData data,
      final SGSXYDataBufferPolicy policy,
      final double[][] allValues,
      final boolean yFlag) {
    double[][] ret = null;
    if (policy.isTakingAllStride()) {
      if (data.isStrideAvailable()) {
        SGIntegerSeriesSet mainStride = data.getMainStride();
        SGIntegerSeriesSet tickLabelStride = data.getTickLabelStride();
        if (mainStride.isComplete() && tickLabelStride.isComplete()) {
          ret = allValues;
        } else {
          final boolean nanFlag;
          if (data.isTickLabelAvailable()) {
            final boolean horizontal = data.isTickLabelHorizontal();
            if (horizontal) {
              nanFlag = yFlag;
            } else {
              nanFlag = !yFlag;
            }
          } else {
            nanFlag = false;
          }
          int[] mainIndices = mainStride.getNumbers();
          int[] tickLabelIndices = tickLabelStride.getNumbers();
          final int allNum = data.getAllPointsNumber();
          STATUS[] statusArray = new STATUS[allNum];
          Arrays.fill(statusArray, STATUS.SKIPPED);
          for (int ii = 0; ii < tickLabelIndices.length; ii++) {
            statusArray[tickLabelIndices[ii]] = STATUS.TICK_LABEL_EXIST; // status to set NaN
          }
          for (int ii = 0; ii < mainIndices.length; ii++) {
            statusArray[mainIndices[ii]] = STATUS.VALUE_EXIST; // status to set given value
          }
          ret = new double[allValues.length][];
          for (int ii = 0; ii < allValues.length; ii++) {
            double[] allValueArray = allValues[ii];
            int cnt = 0;
            for (int jj = 0; jj < allNum; jj++) {
              final STATUS status = statusArray[jj];
              if (!STATUS.SKIPPED.equals(status)) {
                cnt++;
              }
            }
            double[] retArray = new double[cnt];
            cnt = 0;
            for (int jj = 0; jj < allNum; jj++) {
              final STATUS status = statusArray[jj];
              if (STATUS.VALUE_EXIST.equals(status)) {
                retArray[cnt] = allValueArray[jj];
                cnt++;
              } else if (STATUS.TICK_LABEL_EXIST.equals(status)) {
                retArray[cnt] = nanFlag ? Double.NaN : allValueArray[jj];
                cnt++;
              }
            }
            ret[ii] = retArray;
          }
        }
      } else {
        ret = allValues;
      }
    } else {
      ret = allValues;
    }
    return ret;
  }

  public static double[][] getUnshiftedYValues(
      final SGISXYTypeMultipleData data, final SGSXYDataBufferPolicy policy) {
    final boolean remove =
        policy.isInvalidValuesRemoved() || policy.isShiftValuesContained(); // set null if shifted
    double[][] allValues = data.getYValueArray(true, false, remove);
    if (policy.isAllValuesGotten()) {
      return allValues;
    }
    return getUnshiftedXYValues(data, policy, allValues, true);
  }

  public static double[][] getYValues(
      final SGISXYTypeMultipleData data, final SGSXYDataBufferPolicy policy) {
    double[][] values = data.getUnshiftedYValueArray(policy);
    final double[][] ret;
    if (policy.isShiftValuesContained()) {
      SGTuple2d shift = data.getShift();
      ret = new double[values.length][];
      for (int ii = 0; ii < values.length; ii++) {
        ret[ii] = shiftValues(values[ii], shift.y);
      }
    } else {
      ret = values;
    }
    return ret;
  }

  public static double[][] getUnshiftedXValues(
      final SGISXYTypeMultipleData data, final SGSXYDataBufferPolicy policy) {
    final boolean remove =
        policy.isInvalidValuesRemoved() || policy.isShiftValuesContained(); // set null if shifted
    double[][] allValues = data.getXValueArray(true, false, remove);
    if (policy.isAllValuesGotten()) {
      return allValues;
    }
    return getUnshiftedXYValues(data, policy, allValues, false);
  }

  public static double[][] getXValues(
      final SGISXYTypeMultipleData data, final SGSXYDataBufferPolicy policy) {
    double[][] values = data.getUnshiftedXValueArray(policy);
    final double[][] ret;
    if (policy.isShiftValuesContained()) {
      SGTuple2d shift = data.getShift();
      ret = new double[values.length][];
      for (int ii = 0; ii < values.length; ii++) {
        ret[ii] = shiftValues(values[ii], shift.x);
      }
    } else {
      ret = values;
    }
    return ret;
  }

  /**
   * Creates and returns a data buffer with given array of child indices.
   *
   * @param data a multiple scalar-XY data
   * @param policy parameters for data buffer
   * @param indices array of child indices
   * @return the data buffer
   */
  public static SGDataBuffer getDataBuffer(
      SGISXYTypeMultipleData data, SGSXYDataBufferPolicy policy, int[] indices) {
    if (data == null) {
      throw new IllegalArgumentException("data == null");
    }
    if (policy == null) {
      throw new IllegalArgumentException("policy == null");
    }
    if (indices == null) {
      throw new IllegalArgumentException("indices == null");
    }
    final int num = data.getChildNumber();
    for (int ii = 0; ii < indices.length; ii++) {
      if (indices[ii] < 0 || indices[ii] >= num) {
        throw new IllegalArgumentException(
            "Index out of bounds: indices[" + ii + "] == " + indices[ii]);
      }
    }
    if (!SGUtility.checkOverlapping(indices)) {
      throw new IllegalArgumentException("Indices overlapping");
    }

    SGSXYDataBufferPolicy sxyPolicy = (SGSXYDataBufferPolicy) policy;
    final boolean edit = sxyPolicy.isEditedValuesReflected();
    final boolean shift = sxyPolicy.isShiftValuesContained();
    SGTuple2d shiftValue = shift ? data.getShift() : new SGTuple2d();

    double[][] xArray = data.getXValueArray(sxyPolicy);
    double[][] yArray = data.getYValueArray(sxyPolicy);
    final Boolean yHolderFlag = data.isYValuesHolder();
    final Boolean dateFlag = data.getDateFlag();
    SGDate[] dateArray = data.getDateArray(sxyPolicy);
    double[][] lowerErrorValues = data.getLowerErrorValueArray(sxyPolicy);
    double[][] upperErrorValues = data.getUpperErrorValueArray(sxyPolicy);
    String[][] tickLabels = data.getTickLabelArray(sxyPolicy);
    Boolean[] sameErrorVariableFlags = data.hasSameErrorVariable();

    // set edited values
    if (edit) {
      final boolean all = policy.isAllValuesGotten();
      SGIntegerSeriesSet stride =
          data.isIndexAvailable() ? data.getIndexStride() : data.getStride();
      int[] indexArray = stride.getNumbers();
      List<SGDataValueHistory> editedValueList = data.getEditedValueList();
      for (int ii = 0; ii < editedValueList.size(); ii++) {
        SGDataValueHistory dataValue = editedValueList.get(ii);
        SGDataValueHistory.IMultiple multiple = (SGDataValueHistory.IMultiple) dataValue;
        String columnType = dataValue.getColumnType();
        final int childIndex = multiple.getChildIndex();
        final int index = multiple.getIndex();
        final int arrayIndex = all ? index : Arrays.binarySearch(indexArray, index);
        final double value = dataValue.getValue();
        if (X_VALUE.equals(columnType)) {
          xArray[childIndex][arrayIndex] = value + shiftValue.x;
        } else if (Y_VALUE.equals(columnType)) {
          yArray[childIndex][arrayIndex] = value + shiftValue.y;
        }
      }
    }

    final SGDataBuffer buffer;
    if (data.hasOneSidedMultipleValues()) {
      double[] singleValues = null;
      final double[][] mValues;
      if (data.hasMultipleYValues()) {
        singleValues = xArray[0];
        mValues = yArray;
      } else {
        singleValues = yArray[0];
        mValues = xArray;
      }
      double[][] multipleValues = new double[indices.length][];
      for (int ii = 0; ii < indices.length; ii++) {
        multipleValues[ii] = mValues[indices[ii]];
      }
      buffer =
          new SGSXYMultipleDataBuffer(
              singleValues,
              multipleValues,
              data.hasMultipleYValues(),
              dateFlag,
              dateArray,
              lowerErrorValues,
              upperErrorValues,
              sameErrorVariableFlags,
              tickLabels);
    } else {
      double[][] xValues = new double[indices.length][];
      double[][] yValues = new double[indices.length][];
      for (int ii = 0; ii < indices.length; ii++) {
        xValues[ii] = xArray[indices[ii]];
      }
      for (int ii = 0; ii < indices.length; ii++) {
        yValues[ii] = yArray[indices[ii]];
      }
      buffer =
          new SGSXYMultipleDataBuffer(
              xValues,
              yValues,
              dateFlag,
              dateArray,
              yHolderFlag,
              lowerErrorValues,
              upperErrorValues,
              sameErrorVariableFlags,
              tickLabels);
    }
    return buffer;
  }

  /**
   * Creates and returns a data buffer.
   *
   * @param data a multiple scalar-XY data
   * @param policy parameters for data buffer
   * @return the data buffer
   */
  public static SGDataBuffer getDataBuffer(
      SGISXYTypeMultipleData data, SGSXYDataBufferPolicy policy) {
    if (data == null) {
      throw new IllegalArgumentException("data == null");
    }
    if (policy == null) {
      throw new IllegalArgumentException("param == null");
    }

    final int childNum = data.getChildNumber();
    int[] indices = new int[childNum];
    for (int ii = 0; ii < childNum; ii++) {
      indices[ii] = ii;
    }
    return getDataBuffer(data, policy, indices);
  }

  /**
   * Creates and returns a data buffer.
   *
   * @param data a multiple scalar-XY data
   * @param policy parameters for data buffer
   * @return the data buffer
   */
  public static SGDataBuffer getDataBuffer(
      SGISXYTypeSingleData data, SGSXYDataBufferPolicy policy) {
    if (data == null) {
      throw new IllegalArgumentException("data == null");
    }
    SGSXYMultipleDataBuffer multipleBuffer =
        (SGSXYMultipleDataBuffer) getDataBuffer(data.toMultiple(), policy);
    return multipleBuffer.getSXYDataBufferArray()[0];
  }

  /**
   * Returns the value array for given date objects.
   *
   * @param dArray an array of date objects
   * @return the values for given date objects
   */
  public static double[] getDateValueArray(SGDate[] dArray) {
    final double[] values = new double[dArray.length];
    for (int ii = 0; ii < values.length; ii++) {
      values[ii] = dArray[ii].getDateValue();
    }
    return values;
  }
}
