package jp.riken.brain.ni.samuraigraph.figure.java2d;

import static jp.riken.brain.ni.samuraigraph.base.SGIConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.geom.Rectangle2D;
import java.util.*;

interface ILegendElement {

  /**
   * Returns the preferred width.
   *
   * @return the preferred width
   */
  public float getPreferredWidth();

  /**
   * Returns the preferred height.
   *
   * @return the preferred height
   */
  public float getPreferredHeight();

  /**
   * @param rect
   */
  public void setDataElementBounds(final Rectangle2D rect);

  /**
   * @return
   */
  public boolean createDrawingElementInLegend();

  /**
   * Returns the number of points of this element group.
   *
   * @return the number of points
   */
  public int getNumberOfPoints();

  /**
   * Sets the element group set.
   *
   * @param gs the element group set.
   * @return true if succeeded
   */
  public boolean setElementGroupSet(ElementGroupSetInLegend gs);
}
