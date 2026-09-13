package jp.riken.brain.ni.samuraigraph.figure;

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
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;

/** An interface for the observers of line style dialog. */
public interface SGILineStyleDialogObserver {

  /** Returns the map of line style. */
  public Map<Integer, SGLineStyle> getLineStyleMap();

  /**
   * Sets the line style to the child data object.
   *
   * @param style the line style to set
   * @param index array index of child data object
   */
  public boolean setLineStyle(final SGLineStyle style, final int index);

  /**
   * Sets the line styles to the child data object.
   *
   * @param styleList list of line styles
   */
  public boolean setLineStyle(final List<SGLineStyle> styleList);

  /** Returns whether line color is automatically assigned. */
  public boolean isLineColorAutoAssigned();

  /**
   * Sets whether line color is automatically assigned
   *
   * @param b a flag whether line color is automatically assigned
   */
  public void setLineColorAutoAssigned(final boolean b);

  /**
   * Returns the number of child data objects.
   *
   * @return the number of child data objects
   */
  public int getChildNumber();

  /**
   * Returns the color map manager for lines.
   *
   * @return the color map manager for lines
   */
  public SGColorMapManager getLineColorMapManager();

  /**
   * Returns the name of the color map for lines.
   *
   * @return the name of the color map for lines
   */
  public String getLineColorMapName();

  /**
   * Returns a map of the properties of line color maps.
   *
   * @return a map of the properties of line color maps
   */
  public Map<String, SGProperties> getLineColorMapProperties();

  /**
   * Sets the color map for lines.
   *
   * @param name name of the map to set
   * @return true if succeeded
   */
  public boolean setLineColorMapName(String name);

  /**
   * Sets the properties of line color map.
   *
   * @param colorMapProperties the map of properties of color maps
   * @return true if succeeded
   */
  public boolean setLineColorMapProperties(Map<String, SGProperties> colorMapProperties);

  /**
   * Returns the color map for lines.
   *
   * @return the color map for lines
   */
  public SGColorMap getLineColorMap();

  /**
   * Returns the list of child objects.
   *
   * @return the list of child objects
   */
  public List<String> getChildNameList();
}
