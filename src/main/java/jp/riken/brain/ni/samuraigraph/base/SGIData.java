package jp.riken.brain.ni.samuraigraph.base;

import static jp.riken.brain.ni.samuraigraph.application.SGDataPluginConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGRootObjectConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGTextDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGArrowConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGColorMapConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGElementGroupConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGFigureDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSymbolConstants.*;

import java.util.List;

/** An interface for all data classes. */
public interface SGIData extends SGIDisposable {

  /** Returns the data type. */
  public String getDataType();

  /** Returns the data source. */
  public SGIDataSource getDataSource();

  /**
   * Sets the data source.
   *
   * @param src a data source
   */
  public void setDataSource(SGIDataSource src);

  /** Returns the number of data points taking into account the stride. */
  public int getPointsNumber();

  /** Returns the number of data points without taking into account the stride. */
  public int getAllPointsNumber();

  /**
   * Creates and returns a data buffer.
   *
   * @param policy the policy to get data buffer
   */
  public SGDataBuffer getDataBuffer(SGDataBufferPolicy policy);

  /** Returns whether the stride of data arrays is available. */
  public boolean isStrideAvailable();

  /**
   * Returns true if this data has at lease one "effective" stride that has the string
   * representation different from "0:end".
   */
  public boolean hasEffectiveStride();

  /** Returns whether the animation is supported in this data. */
  public boolean isAnimationSupported();

  /** Returns true if this data is available for the animation. */
  public boolean isAnimationAvailable();

  /** Returns the stride of time. */
  public SGIntegerSeriesSet getTimeStride();

  /** Returns the current index of time value. */
  public int getCurrentTimeValueIndex();

  /**
   * Sets the current index of time value.
   *
   * @param index a value to set to the index of time values
   */
  public void setCurrentTimeValueIndex(final int index);

  /**
   * Returns whether the index is available.
   *
   * @return true if the index is available
   */
  public boolean isIndexAvailable();

  public SGIntegerSeriesSet getIndexStride();

  /**
   * Returns the array of column types.
   *
   * @return the array of column types
   */
  public String[] getDataViewerColumnTypes();

  /**
   * Returns preferred column type for data viewer.
   *
   * @return preferred column type for data viewer
   */
  public String getPreferredDataViewColumnType();

  public Double getDataViewerValue(final String columnType, final int row, final int col);

  public void setDataViewerValue(
      final String columnType, final int row, final int col, final Object value);

  public int getDataViewerColumnNumber(final String columnType, final boolean all);

  public int getDataViewerRowNumber(final String columnType, final boolean all);

  public SGIntegerSeriesSet getDataViewerColStride(String columnType);

  public SGIntegerSeriesSet getDataViewerRowStride(String columnType);

  public void setDataValue(final SGDataValueHistory value);

  public void restoreCache();

  public SGTwoDimensionalArrayIndex getDataViewerCell(
      SGTwoDimensionalArrayIndex cell, String columnType, final boolean bStride);

  public List<SGDataValueHistory> getEditedValueList();
}
