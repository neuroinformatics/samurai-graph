package jp.riken.brain.ni.samuraigraph.figure.java2d;

import static jp.riken.brain.ni.samuraigraph.base.SGIConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import org.w3c.dom.Element;

class ElementGroupTickLabels extends SGElementGroupTickLabelForData implements ILegendElement {
  private final SGFigureElementLegend legend;

  /** A group set that this element group belongs. */
  protected SGFigureElementLegend.ElementGroupSetInLegend mGroupSet = null;

  /**
   * Sets the element group set.
   *
   * @param gs the element group set
   */
  public boolean setElementGroupSet(SGFigureElementLegend.ElementGroupSetInLegend gs) {
    this.mGroupSet = gs;
    return true;
  }

  /** The default constructor. */
  ElementGroupTickLabels(final SGFigureElementLegend legend, SGISXYTypeData data) {
    super(data);
    this.legend = legend;
  }

  /** */
  protected SGDrawingElement createDrawingElementInstance(final int index) {
    return new SGDrawingElementString2DExtended();
  }

  /** Create drawing elements. */
  public boolean createDrawingElementInLegend() {
    // Do nothing
    return true;
  }

  /** */
  public float getPreferredWidth() {
    return 0.0f;
  }

  /** */
  public float getPreferredHeight() {
    return 0.0f;
  }

  // private Rectangle2D mBoundsRect = new Rectangle2D.Float();

  /**
   * @param rect
   */
  public void setDataElementBounds(final Rectangle2D rect) {
    // this.mBoundsRect = rect;
  }

  /**
   * Returns the number of points of this element group.
   *
   * @return the number of points
   */
  public int getNumberOfPoints() {
    return 0;
  }

  /**
   * @param el
   * @return
   */
  public boolean readProperty(final Element el) {
    if (super.readProperty(el) == false) {
      return false;
    }
    return legend.readProperty(this, el);
  }

  /**
   * Returns whether this group set contains the given point.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @return true if this element group contains the given point
   */
  public boolean contains(final int x, final int y) {
    // always returns false
    return false;
  }

  /**
   * Update the location of tick labels.
   *
   * @return true if succeeded
   */
  public boolean updateLocation() {
    return true;
  }
}
