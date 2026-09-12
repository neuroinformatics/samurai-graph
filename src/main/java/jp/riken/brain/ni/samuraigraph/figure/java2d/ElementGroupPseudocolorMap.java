package jp.riken.brain.ni.samuraigraph.figure.java2d;

import static jp.riken.brain.ni.samuraigraph.base.SGIConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants.*;

import java.awt.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;

class ElementGroupPseudocolorMap extends SGElementGroupPseudocolorMapForData
    implements ILegendElement {
  private final SGFigureElementLegend legend;

  protected ElementGroupPseudocolorMap(final SGFigureElementLegend legend) {
    this.legend = legend;
  }

  /**
   * Creates and returns an instance of drawing element.
   *
   * @return an instance of drawing element
   */
  protected SGDrawingElement createDrawingElementInstance(final int index) {
    return new PseudocolorMapRectangleInLegend(this, index);
  }

  public boolean createDrawingElementInLegend() {
    // set size
    final float w = this.getPreferredWidth();
    final float h = this.getPreferredHeight();
    PseudocolorMapRectangle rect = (PseudocolorMapRectangle) this.mDrawingElementArray[0];
    rect.setWidth(w);
    rect.setHeight(h);

    // set the location
    Rectangle2D lRect = legend.getRectOfGroupSet(this.mGroupSet);
    Rectangle2D dRect = this.mBoundsRect;
    final float x = (float) lRect.getX() + 0.50f * (float) dRect.getWidth();
    final float y = (float) lRect.getY() + 0.50f * (float) lRect.getHeight();
    SGTuple2f position = new SGTuple2f(x, y);
    if (this.setLocation(new SGTuple2f[] {position}) == false) {
      return false;
    }

    return true;
  }

  public int getNumberOfPoints() {
    return 1;
  }

  public float getPreferredHeight() {
    final ElementGroupSetInLegendSXYZ gs = (ElementGroupSetInLegendSXYZ) this.mGroupSet;
    Rectangle2D strRect = gs.mDrawingString.getElementBounds();
    return (float) strRect.getHeight();
  }

  public float getPreferredWidth() {
    return this.getMagnification() * legend.getSymbolSpan();
  }

  private Rectangle2D mBoundsRect = new Rectangle2D.Float();

  public void setDataElementBounds(Rectangle2D rect) {
    this.mBoundsRect = rect;
  }

  /** A group set that this element group belongs. */
  protected ElementGroupSetInLegend mGroupSet = null;

  public boolean setElementGroupSet(ElementGroupSetInLegend gs) {
    this.mGroupSet = gs;
    return true;
  }

  /** Paint a color bar. */
  public boolean paintElement(final Graphics2D g2d, final Rectangle2D clipRect) {
    // if (super.paintElement(g2d, clipRect) == false) {
    // return false;
    // }
    PseudocolorMapRectangle rect = (PseudocolorMapRectangle) this.mDrawingElementArray[0];
    Rectangle bounds = rect.getElementBounds().getBounds();

    final double x = bounds.getX();
    final double y = bounds.getY();
    final double w = 1.0;
    final double h = bounds.getHeight();
    final int nStart = (int) bounds.getMinX();
    final int nEnd = (int) bounds.getMaxX();

    // get the value range of the color bar
    final double zMin = this.mColorBarModel.getMinValue();
    final double zMax = this.mColorBarModel.getMaxValue();
    final double zRange = zMax - zMin;

    // fill the rectangle
    final int nDiff = nEnd - nStart;
    if (nDiff != 0) {
      for (int ii = 0; ii <= nDiff; ii++) {
        final Rectangle2D thinRect = new Rectangle2D.Double();
        thinRect.setRect(x + ii, y, w, h);
        final double ratio = (double) ii / nDiff;
        final double zValue = zMin + ratio * zRange;
        final Color cl = this.mColorBarModel.evaluate(zValue, SGAxis.LINEAR_SCALE);
        g2d.setColor(cl);
        g2d.fill(thinRect);
      }
    }

    // draw bounds
    g2d.setStroke(new BasicStroke(1.0f));
    g2d.setColor(Color.BLACK);
    g2d.draw(bounds);

    // draw anchors if the group set is selected
    if (this.mGroupSet.isViewable()
        && this.mGroupSet.isSelected()
        && legend.isSymbolsVisibleAroundFocusedObjects()) {
      SGUtilityForFigureElementJava2D.drawAnchorsOnRectangle(bounds, g2d);
    }
    return true;
  }

  /**
   * Sets the location of each rectangle.
   *
   * @param pointArray an array of location
   */
  public boolean setLocation(SGTuple2f[] pointArray) {
    if (this.mDrawingElementArray == null) {
      return true;
    }

    if (pointArray.length != this.mDrawingElementArray.length) {
      // throw new IllegalArgumentException();
      this.initDrawingElement(pointArray);
    }

    this.mLocation.setValues(pointArray[0]);

    return true;
  }

  private SGTuple2f mLocation = new SGTuple2f();

  @Override
  public float getX(int index) {
    return this.mLocation.x;
  }

  @Override
  public float getY(int index) {
    return this.mLocation.y;
  }
}
