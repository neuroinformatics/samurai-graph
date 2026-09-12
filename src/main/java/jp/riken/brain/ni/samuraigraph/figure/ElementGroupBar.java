package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGIConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants.*;

import java.awt.*;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import org.w3c.dom.Element;

class ElementGroupBar extends SGElementGroupBarForData implements ILegendElement {
  private final SGFigureElementLegend legend;

  /** A group set that this element group belongs. */
  protected ElementGroupSetInLegend mGroupSet = null;

  /** The default constructor. */
  protected ElementGroupBar(final SGFigureElementLegend legend, SGISXYTypeData data) {
    super(data);
    this.legend = legend;
  }

  /**
   * Creates and returns an instance of drawing element.
   *
   * @return an instance of drawing element
   */
  protected SGDrawingElement createDrawingElementInstance(final int index) {
    return new BarInLegend(this, index);
  }

  /**
   * Sets the element group set.
   *
   * @param gs the element group set
   */
  public boolean setElementGroupSet(ElementGroupSetInLegend gs) {
    this.mGroupSet = gs;
    return true;
  }

  /**
   * Returns the preferred width.
   *
   * @return the preferred width
   */
  public float getPreferredWidth() {
    final ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY) this.mGroupSet;
    Rectangle2D strRect = gs.mDrawingString.getElementBounds();
    return (float) strRect.getHeight();
  }

  /**
   * Returns the preferred height.
   *
   * @return the preferred height
   */
  public float getPreferredHeight() {
    return this.getPreferredWidth();
  }

  private Rectangle2D mBoundsRect = new Rectangle2D.Float();

  /**
   * @param rect
   */
  public void setDataElementBounds(final Rectangle2D rect) {
    this.mBoundsRect = rect;
  }

  /**
   * Returns the number of points in this element group.
   *
   * @return the number of points
   */
  public int getNumberOfPoints() {
    return 1;
  }

  /** */
  public boolean createDrawingElementInLegend() {
    Rectangle2D lRect = legend.getRectOfGroupSet(this.mGroupSet);
    Rectangle2D dRect = this.mBoundsRect;

    // set size
    final float h = this.getPreferredWidth();
    this.setRectangleHeight(h);
    final float w = (float) dRect.getWidth();
    this.setRectangleWidth(w);

    // set location
    final float x = (float) lRect.getX();
    final float y = (float) (lRect.getY() + lRect.getHeight() / 2 - this.getPreferredHeight() / 2);
    SGTuple2f start = new SGTuple2f(x, y);
    this.setLocation(new SGTuple2f[] {start});

    return true;
  }

  /**
   * @return
   */
  public boolean initDrawingElement(final int num) {
    super.initDrawingElement(num);
    return true;
  }

  /** The location. */
  private SGTuple2f mLocation = new SGTuple2f();

  /** */
  public boolean setLocation(final SGTuple2f[] pointArray) {

    if (this.mDrawingElementArray == null) {
      return true;
    }

    if (pointArray.length != this.mDrawingElementArray.length) {
      // throw new IllegalArgumentException();
      this.initDrawingElement(pointArray);
    }

    this.mLocation = pointArray[0];

    return true;
  }

  /** Paint line objects. */
  public boolean paintElement(final Graphics2D g2d, final Rectangle2D clipRect) {
    if (super.paintElement(g2d, clipRect) == false) {
      return false;
    }

    // draw anchors if the group set is selected
    if (this.mGroupSet.isViewable()
        && this.mGroupSet.isSelected()
        && legend.isSymbolsVisibleAroundFocusedObjects()) {
      SGDrawingElementBar2D bar = (SGDrawingElementBar2D) this.mDrawingElementArray[0];
      Rectangle2D rect = bar.getElementBounds();
      SGUtilityForFigureElementJava2D.drawAnchorsOnRectangle(rect, g2d);
    }
    return true;
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

  @Override
  public float getX(int index) {
    return this.mLocation.x;
  }

  @Override
  public float getY(int index) {
    return this.mLocation.y;
  }

  // @Override
  // protected float getShiftXInGraph() {
  // return 0;
  // }
  //
  // @Override
  // protected float getShiftYInGraph() {
  // return 0;
  // }

}
