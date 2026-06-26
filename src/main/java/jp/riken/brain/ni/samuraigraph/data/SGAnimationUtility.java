package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;

/** Utility methods for animation frame bounds calculation. */
public final class SGAnimationUtility {

  private SGAnimationUtility() {
    // utility class
  }

  public static SGValueRange getAllAnimationFrameBoundsX(SGISXYTypeSingleData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return SGDataUtility.getBoundsX(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    return getAllAnimationFrameBoundsXSub(data, indices);
  }

  static SGValueRange getAllAnimationFrameBoundsXSub(
      SGISXYTypeSingleData data, final int[] indices) {
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = SGDataUtility.getBoundsX(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }

  public static SGValueRange getAllAnimationFrameBoundsY(SGISXYTypeSingleData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return SGDataUtility.getBoundsY(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    return getAllAnimationFrameBoundsYSub(data, indices);
  }

  static SGValueRange getAllAnimationFrameBoundsYSub(
      SGISXYTypeSingleData data, final int[] indices) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return SGDataUtility.getBoundsY(data);
    }
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = SGDataUtility.getBoundsY(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }

  public static SGValueRange getAllAnimationFrameBoundsX(SGISXYTypeMultipleData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return SGDataUtility.getBoundsX(data);
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
      return SGDataUtility.getBoundsY(data);
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
      return SGDataUtility.getBoundsX(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = SGDataUtility.getBoundsX(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }

  public static SGValueRange getAllAnimationFrameBoundsY(SGISXYZTypeData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return SGDataUtility.getBoundsY(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = SGDataUtility.getBoundsY(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }

  public static SGValueRange getAllAnimationFrameBoundsZ(SGISXYZTypeData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return SGDataUtility.getBoundsZ(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = SGDataUtility.getBoundsZ(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }

  public static SGValueRange getAllAnimationFrameBoundsX(SGIVXYTypeData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return SGDataUtility.getBoundsX(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = SGDataUtility.getBoundsX(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }

  public static SGValueRange getAllAnimationFrameBoundsY(SGIVXYTypeData data) {
    if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
      return SGDataUtility.getBoundsY(data);
    }
    SGIntegerSeriesSet arraySection = data.getTimeStride();
    int[] indices = arraySection.getNumbers();
    final int curIndex = data.getCurrentTimeValueIndex();
    List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    for (int ii = 0; ii < indices.length; ii++) {
      data.setCurrentTimeValueIndex(indices[ii]);
      SGValueRange range = SGDataUtility.getBoundsY(data);
      rangeList.add(range);
    }
    data.setCurrentTimeValueIndex(curIndex);
    return getBounds(rangeList);
  }

  private static SGValueRange getBounds(List<SGValueRange> rangeList) {
    Double min = null;
    Double max = null;
    for (SGValueRange range : rangeList) {
      if (range == null) continue;
      if (min == null || range.getMinValue() < min) {
        min = range.getMinValue();
      }
      if (max == null || range.getMaxValue() > max) {
        max = range.getMaxValue();
      }
    }
    if (min == null || max == null) {
      return null;
    }
    return new SGValueRange(min, max);
  }
}
