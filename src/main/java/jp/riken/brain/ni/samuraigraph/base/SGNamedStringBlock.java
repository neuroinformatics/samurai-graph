package jp.riken.brain.ni.samuraigraph.base;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationTextConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGAnimationConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDateConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGPaintConstant.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataPropertyKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGArrowConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGFigureDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSXYDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGShapeConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGStringConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSymbolConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGTimingLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGVXYDataConstants.*;

import java.util.HashMap;
import java.util.Map;

/** The class of a block of text strings with names. */
public class SGNamedStringBlock extends SGStringBlock {
  /** The map of index series. */
  private Map<String, SGIntegerSeries> mSeriesMap = null;

  /**
   * Builds an block object.
   *
   * @param values the values parameter
   * @param seriesMap the map of dimension indices
   */
  public SGNamedStringBlock(final String[] values, Map<String, SGIntegerSeries> seriesMap) {
    super(values);
    if (seriesMap == null) {
      throw new IllegalArgumentException("seriesMap == null");
    }
    if (seriesMap.size() == 0) {
      throw new IllegalArgumentException("seriesMap.size() == 0");
    }
    int len = 1;
    for (SGIntegerSeries s : seriesMap.values()) {
      len *= s.getLength();
    }
    if (values.length != len) {
      throw new IllegalArgumentException(
          "Invalid array length: " + values.length + " must be equal to " + len);
    }
    this.mSeriesMap = new HashMap<String, SGIntegerSeries>(seriesMap);
  }

  /**
   * Returns the series of given dimension name.
   *
   * @param name the name of dimension
   * @return the series of given dimension name
   */
  public SGIntegerSeries getSeries(final String name) {
    return this.mSeriesMap.get(name);
  }

  /**
   * Returns a text string for this object.
   *
   * @return a text string for this object
   */
  public String paramString() {
    StringBuilder sb = new StringBuilder();
    sb.append(", map=");
    sb.append(this.mSeriesMap.toString());
    return sb.toString();
  }
}
