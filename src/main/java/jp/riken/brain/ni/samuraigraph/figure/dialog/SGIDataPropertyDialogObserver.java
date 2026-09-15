package jp.riken.brain.ni.samuraigraph.figure.dialog;

import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;

/** An observer of the property dialog for data objects. */
public interface SGIDataPropertyDialogObserver extends SGIPropertyDialogObserver {
  /** */
  public String getName();

  /**
   * @param str
   */
  public boolean setName(final String str);

  public boolean getLegendVisibleFlag();

  public boolean setVisibleInLegend(final boolean b);

  /** Returns an array of information of data columns. */
  public SGDataColumnInfo[] getDataColumnInfoArray();

  /** Returns a map which has data information. */
  public Map<String, Object> getInfoMap();

  /**
   * Sets the information of data columns.
   *
   * @param columns information of data columns
   * @param message operation message
   */
  public boolean setColumnInfo(SGDataColumnInfo[] columns, String message);

  /** Returns the type of data. */
  public String getDataType();

  /** Returns the properties of data. */
  public SGProperties getDataProperties();

  /** Returns the data. */
  public SGData getData();

  /** Update drawing elements with related data object. */
  public boolean updateWithData();

  /**
   * Sets whether the stride of data arrays is available.
   *
   * @param b true if the stride of data arrays is available
   */
  public void setStrideAvailable(final boolean b);

  /**
   * Sets the stride of the indices.
   *
   * @param stride stride of arrays
   * @return true if succeeded
   */
  public boolean setIndexStride(SGIntegerSeriesSet stride);

  /**
   * Sets the stride for single dimensional data.
   *
   * @param stride stride of arrays
   * @return true if succeeded
   */
  public boolean setSDArrayStride(SGIntegerSeriesSet stride);
}
