package jp.riken.brain.ni.samuraigraph.figure.java2d;

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

class ElementGroupSymbol extends SGElementGroupSymbolForData implements ILegendElement {
  private final SGFigureElementLegend legend;

  /** A group set that this element group belongs. */
  protected SGFigureElementLegend.ElementGroupSetInLegend mGroupSet = null;

  /** The default constructor. */
  protected ElementGroupSymbol(final SGFigureElementLegend legend, SGISXYTypeData data) {
    super(data);
    this.legend = legend;
  }

  /**
   * Sets the element group set.
   *
   * @param gs the element group set
   */
  public boolean setElementGroupSet(SGFigureElementLegend.ElementGroupSetInLegend gs) {
    this.mGroupSet = gs;
    return true;
  }

  /**
   * Returns the preferred width.
   *
   * @return the preferred width
   */
  public float getPreferredWidth() {
    return this.getDataElementSize();
  }

  /**
   * Returns the preferred height.
   *
   * @return the preferred height
   */
  public float getPreferredHeight() {
    return 1.20f * this.getDataElementSize();
  }

  /** */
  private float getDataElementSize() {
    SGDrawingElement[] array = this.mDrawingElementArray;
    if (array != null) {
      if (array.length == 0) {
        return 0.0f;
      }

      SGDrawingElementSymbol2D symbol = (SGDrawingElementSymbol2D) array[0];
      Rectangle2D rect = symbol.getElementBounds().getBounds2D();
      return (float) rect.getHeight();
    }
    return 0.0f;
  }

  private Rectangle2D mBoundsRect = new Rectangle2D.Float();

  /**
   * @param rect
   */
  public void setDataElementBounds(final Rectangle2D rect) {
    this.mBoundsRect = rect;
  }

  /**
   * Returns the number of points of this element group.
   *
   * @return the number of points
   */
  public int getNumberOfPoints() {
    return 1;
  }

  private SGTuple2f mLocation = new SGTuple2f();

  /** */
  public boolean createDrawingElementInLegend() {
    Rectangle2D lRect = legend.getRectOfGroupSet(this.mGroupSet);
    Rectangle2D dRect = this.mBoundsRect;
    // final float x = (float)lRect.getX() +
    // 0.50f*this.getDataElementWidth();
    final float x = (float) lRect.getX() + 0.50f * (float) dRect.getWidth();
    final float y = (float) lRect.getY() + 0.50f * (float) lRect.getHeight();
    SGTuple2f position = new SGTuple2f(x, y);

    this.mLocation = position;
    if (this.setLocation(new SGTuple2f[] {position}) == false) {
      return false;
    }

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
      if (this.mGroupSet instanceof SGIElementGroupSetXY) {
        SGIElementGroupSetXY gsSXY = (SGIElementGroupSetXY) this.mGroupSet;
        if (!gsSXY.getBarGroup().isVisible()) {
          SGDrawingElementSymbol2D symbol = (SGDrawingElementSymbol2D) this.mDrawingElementArray[0];
          Rectangle2D rect = symbol.getElementBounds();
          SGUtilityForFigureElementJava2D.drawAnchorsOnRectangle(rect, g2d);
        }
      }
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

  /**
   * Returns the location at a given index.
   *
   * @param index the index
   * @return the location
   */
  public SGTuple2f getLocation(final int index) {
    return this.mLocation;
  }
}
