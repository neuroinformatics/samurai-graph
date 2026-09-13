package jp.riken.brain.ni.samuraigraph.base;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationTextConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGDataPluginConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGAnimationConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDateConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGPaintConstant.*;
import static jp.riken.brain.ni.samuraigraph.base.SGRootObjectConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGTextDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataPropertyKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGArrowConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGColorMapConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGElementGroupConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGFigureDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSXYDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGShapeConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGStringConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSymbolConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGTimingLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGVXYDataConstants.*;

import java.awt.Point;
import java.util.List;

/** An interface for a figure element that has data objects. */
public interface SGIFigureElementForData extends SGIFigureElement {

  /**
   * Set the name of a data object.
   *
   * @param data the data object to get the name
   */
  public String getDataName(SGData data);

  /**
   * Set the name of a data object.
   *
   * @param name the name parameter
   * @param data the data object to set the name
   */
  public boolean setDataName(String name, SGData data);

  /** Returns whether the data object is visible. */
  public boolean isDataVisible(SGData data);

  /**
   * Returns whether a given data is visible in legend.
   *
   * @param data a data
   */
  public boolean isDataVisibleInLegend(final SGData data);

  /**
   * Returns whether the data object is selected.
   *
   * @param data a data object
   */
  public boolean isDataSelected(final SGData data);

  /**
   * Checks whether the objects related to a given data are changed.
   *
   * @param data a data
   */
  public boolean checkDataChanged(final SGData data);

  public boolean isDataChanged(final SGData data);

  /** Returns a list of focused data objects. */
  public List<SGData> getFocusedDataList();

  /** Returns a list of cut data objects. */
  public List<SGData> cutFocusedData();

  /** Returns a list of labels for data objects. */
  public List<DataLabel> getDataLabelList();

  /** A class of the label for data objects. */
  public static class DataLabel {

    private String mText = null;

    private Point mLocation = new Point();

    /** The default constructor. */
    public DataLabel(final String text, final int x, final int y) {
      super();
      this.mText = text;
      this.mLocation.x = x;
      this.mLocation.y = y;
    }

    public String getText() {
      return this.mText;
    }

    public int getX() {
      return this.mLocation.x;
    }

    public int getY() {
      return this.mLocation.y;
    }
  }

  /**
   * Returns the data of a given ID.
   *
   * @param id the ID of data
   */
  public SGData getData(final int id);

  /**
   * Returns the list of axis information of a given data.
   *
   * @param forAnimationFrames the forAnimationFrames parameter
   * @param data a data object
   */
  public List<SGDataAxisInfo> getAxisInfoList(SGData data, final boolean forAnimationFrames);

  /** Returns the list of visible data objects. */
  public List<SGData> getVisibleDataList();

  /** */
  public List<Boolean> getVisibleFlagList(SGData data);

  /**
   * Returns the child object for given data.
   *
   * @param data data object
   */
  public SGIChildObject getChild(SGData data);

  /**
   * Returns the style of drawing elements of given data.
   *
   * @param data a data
   */
  public List<SGStyle> getStyle(SGData data);

  /**
   * Sets the data selection.
   *
   * @param data the data to set the state of selection
   * @param b true to be selected
   */
  public void setDataFocused(SGData data, boolean b);
}
