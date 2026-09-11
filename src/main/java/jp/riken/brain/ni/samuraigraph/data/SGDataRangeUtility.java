package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDataBufferUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnInfoUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTitleUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataMiscUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataStrideUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataTextUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataViewerUtility.*;

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;

/** Static helpers for computing value ranges of data. */
public final class SGDataRangeUtility
    implements SGIDataColumnTypeConstants,
        SGIDataPropertyKeyConstants,
        SGINetCDFConstants,
        SGIMDArrayConstants {

  private SGDataRangeUtility() {}

  /**
   * Returns the maximum values in a given array excepting NaN and infinity.
   *
   * @param array an array of values
   * @return the maximum value
   */
  public static double getMaxValue(double[] array) {
    boolean valid = false;
    double max = -Double.MAX_VALUE;
    for (int ii = 0; ii < array.length; ii++) {
      if (Double.isNaN(array[ii]) || Double.isInfinite(array[ii])) {
        continue;
      }
      if (array[ii] > max) {
        max = array[ii];
      }
      valid = true;
    }
    if (!valid) {
      return Double.NaN;
    }
    return max;
  }

  /**
   * Returns the minimum values in a given array excepting NaN and infinity.
   *
   * @param array an array of values
   * @return the minimum value
   */
  public static double getMinValue(double[] array) {
    boolean valid = false;
    double min = Double.MAX_VALUE;
    for (int ii = 0; ii < array.length; ii++) {
      if (Double.isNaN(array[ii]) || Double.isInfinite(array[ii])) {
        continue;
      }
      if (array[ii] < min) {
        min = array[ii];
      }
      valid = true;
    }
    if (!valid) {
      return Double.NaN;
    }
    return min;
  }

  /**
   * Returns the bounds from given array.
   *
   * @param array an array of values to search the minimum and maximum value
   * @return the bounds of given array
   */
  public static SGValueRange getBounds(final double[] array) {
    return new SGValueRange(getMinValue(array), getMaxValue(array));
  }

  /**
   * Returns the bounds of x-values of given data.
   *
   * @param data a data
   * @return the bounds of x-values
   */
  public static SGValueRange getBoundsX(SGISXYTypeSingleData data) {
    final double[] xValues = data.getXValueArray(false);
    if (xValues == null) {
      return null;
    }
    if (data.isErrorBarAvailable() && !data.isErrorBarVertical()) {
      return getBounds(data, xValues);
    } else {
      return getBounds(xValues);
    }
  }

  /**
   * Returns the bounds of y-values of given data.
   *
   * @param data a data
   * @return the bounds of y-values
   */
  public static SGValueRange getBoundsY(SGISXYTypeSingleData data) {
    final double[] yValues = data.getYValueArray(false);
    if (yValues == null) {
      return null;
    }
    if (data.isErrorBarAvailable() && data.isErrorBarVertical()) {
      return getBounds(data, yValues);
    } else {
      return getBounds(yValues);
    }
  }

  public static Double getMinValue(List<SGValueRange> rangeList) {
    Double min = null;
    for (int ii = 0; ii < rangeList.size(); ii++) {
      SGValueRange range = rangeList.get(ii);
      if (range.isMinValid()) {
        final double value = range.getMinValue();
        if (min == null) {
          min = Double.valueOf(value);
        } else {
          if (value < min.doubleValue()) {
            min = Double.valueOf(value);
          }
        }
      }
    }
    return min;
  }

  public static Double getMaxValue(List<SGValueRange> rangeList) {
    Double max = null;
    for (int ii = 0; ii < rangeList.size(); ii++) {
      SGValueRange range = rangeList.get(ii);
      if (range.isMaxValid()) {
        final double value = range.getMaxValue();
        if (max == null) {
          max = Double.valueOf(value);
        } else {
          if (value > max.doubleValue()) {
            max = Double.valueOf(value);
          }
        }
      }
    }
    return max;
  }

  /**
   * Returns the bounds of x-values of given data.
   *
   * @param data a data
   * @return the bounds of x-values
   */
  public static SGValueRange getBoundsX(SGISXYTypeMultipleData data) {
    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < sxyArray.length; ii++) {
      SGValueRange range = getBoundsX(sxyArray[ii]);
      rangeList.add(range);
    }
    // disposes of data objects
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);
    return getBounds(rangeList);
  }

  /**
   * Returns the bounds of y-values of given data.
   *
   * @param data a data
   * @return the bounds of y-values
   */
  public static SGValueRange getBoundsY(SGISXYTypeMultipleData data) {
    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < sxyArray.length; ii++) {
      SGValueRange range = getBoundsY(sxyArray[ii]);
      rangeList.add(range);
    }
    // disposes of data objects
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);
    return getBounds(rangeList);
  }

  /**
   * Returns the bounds of x-values of given data.
   *
   * @param data a data
   * @return the bounds of x-values
   */
  public static SGValueRange getBoundsX(SGIVXYTypeData data) {
    final double[] values = data.getXValueArray(false);
    if (values == null) {
      return null;
    }
    return getBounds(values);
    // final SGTuple2d[] coordinates = data.getXYValueArray();
    // if (coordinates == null) {
    // return null;
    // }
    // final int len = coordinates.length;
    // double[] values = new double[len];
    // for (int ii = 0; ii < len; ii++) {
    // values[ii] = coordinates[ii].x;
    // }
    // return getBounds(values);
  }

  /**
   * Returns the bounds of y-values of given data.
   *
   * @param data a data
   * @return the bounds of y-values
   */
  public static SGValueRange getBoundsY(SGIVXYTypeData data) {
    // final SGTuple2d[] coordinates = data.getXYValueArray();
    // if (coordinates == null) {
    // return null;
    // }
    // final int len = coordinates.length;
    // double[] values = new double[len];
    // for (int ii = 0; ii < len; ii++) {
    // values[ii] = coordinates[ii].y;
    // }
    // return getBounds(values);
    final double[] values = data.getYValueArray(false);
    if (values == null) {
      return null;
    }
    return getBounds(values);
  }

  /**
   * Returns the bounds of x-values of given data.
   *
   * @param data a data
   * @return the bounds of x-values
   */
  public static SGValueRange getBoundsX(SGISXYZTypeData data) {
    final double[] values = data.getXValueArray(false);
    if (values == null) {
      return null;
    }
    return getBounds(values);
  }

  /**
   * Returns the bounds of y-values of given data.
   *
   * @param data a data
   * @return the bounds of y-values
   */
  public static SGValueRange getBoundsY(SGISXYZTypeData data) {
    final double[] values = data.getYValueArray(false);
    if (values == null) {
      return null;
    }
    return getBounds(values);
  }

  /**
   * Returns the bounds of z-values of given data.
   *
   * @param data a data
   * @return the bounds of z-values
   */
  public static SGValueRange getBoundsZ(SGISXYZTypeData data) {
    final double[] values = data.getZValueArray(false);
    if (values == null) {
      return null;
    }
    return getBounds(values);
  }

  public static SGValueRange getAllAnimationFrameBoundsX(SGISXYTypeSingleData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return getBoundsX(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    return getAllAnimationFrameBoundsXSub(data, indices);
  }

  public static SGValueRange getAllAnimationFrameBoundsY(SGISXYTypeSingleData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return getBoundsY(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    return getAllAnimationFrameBoundsYSub(data, indices);
  }

  public static SGValueRange getAllAnimationFrameBoundsX(SGISXYTypeMultipleData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return getBoundsX(data);
    }
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    for (SGISXYTypeSingleData sxy : sxyArray) {
      SGValueRange range = getAllAnimationFrameBoundsXSub(sxy, indices);
      rangeList.add(range);
    }
    return getBounds(rangeList);
  }

  public static SGValueRange getAllAnimationFrameBoundsY(SGISXYTypeMultipleData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return getBoundsY(data);
    }
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    for (SGISXYTypeSingleData sxy : sxyArray) {
      SGValueRange range = getAllAnimationFrameBoundsYSub(sxy, indices);
      rangeList.add(range);
    }
    return getBounds(rangeList);
  }

  public static SGValueRange getAllAnimationFrameBoundsX(SGISXYZTypeData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return getBoundsX(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = getBoundsX(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }

  public static SGValueRange getAllAnimationFrameBoundsY(SGISXYZTypeData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return getBoundsY(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = getBoundsY(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }

  public static SGValueRange getAllAnimationFrameBoundsZ(SGISXYZTypeData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return getBoundsZ(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = getBoundsZ(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }

  public static SGValueRange getAllAnimationFrameBoundsX(SGIVXYTypeData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return getBoundsX(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = getBoundsX(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }

  public static SGValueRange getAllAnimationFrameBoundsY(SGIVXYTypeData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return getBoundsY(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = getBoundsY(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }

  private static SGValueRange getBounds(SGISXYTypeSingleData data, double[] values) {
    final double[] lArray = data.getLowerErrorValueArray(false);
    final double[] uArray = data.getUpperErrorValueArray(false);
    final double[] lyValues = new double[values.length];
    final double[] uyValues = new double[values.length];
    for (int ii = 0; ii < values.length; ii++) {
      lyValues[ii] = values[ii] - Math.abs(lArray[ii]);
      uyValues[ii] = values[ii] + Math.abs(uArray[ii]);
    }
    SGValueRange vBounds = getBounds(values);
    SGValueRange lBounds = getBounds(lyValues);
    SGValueRange uBounds = getBounds(uyValues);
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    rangeList.add(vBounds);
    rangeList.add(lBounds);
    rangeList.add(uBounds);
    Double min = getMinValue(rangeList);
    Double max = getMaxValue(rangeList);
    final double minValue = (min != null) ? min.doubleValue() : Double.NaN;
    final double maxValue = (max != null) ? max.doubleValue() : Double.NaN;
    SGValueRange bounds = new SGValueRange(minValue, maxValue);
    return bounds;
  }

  private static SGValueRange getBounds(List<SGValueRange> rangeList) {
    Double min = getMinValue(rangeList);
    Double max = getMaxValue(rangeList);
    final double minValue = (min != null) ? min.doubleValue() : Double.NaN;
    final double maxValue = (max != null) ? max.doubleValue() : Double.NaN;
    return new SGValueRange(minValue, maxValue);
  }

  static SGValueRange getAllAnimationFrameBoundsXSub(
      SGISXYTypeSingleData data, final int[] indices) {
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = getBoundsX(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }

  static SGValueRange getAllAnimationFrameBoundsYSub(
      SGISXYTypeSingleData data, final int[] indices) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return getBoundsY(data);
    }
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = getBoundsY(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }
}
