package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGIConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants.*;

import java.awt.*;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.RoundingMode;
import java.util.*;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import org.w3c.dom.Element;

class ElementGroupArrow extends SGElementGroupArrowForData implements ILegendElement {
  private final SGFigureElementLegend legend;

  /** A group set that this element group belongs. */
  protected ElementGroupSetInLegend mGroupSet = null;

  /** The default constructor. */
  protected ElementGroupArrow(final SGFigureElementLegend legend) {
    super();
    this.legend = legend;

    // set properties to the magnitude label
    this.mMagnitudeString.setFontName(DEFAULT_LEGEND_FONT_NAME);
    this.mMagnitudeString.setFontStyle(DEFAULT_LEGEND_FONT_STYLE);
    this.mMagnitudeString.setFontSize(DEFAULT_LEGEND_FONT_SIZE, FONT_SIZE_UNIT);
    this.mMagnitudeString.setColor(DEFAULT_LEGEND_FONT_COLOR);
  }

  /**
   * @return
   */
  protected SGDrawingElement createDrawingElementInstance(final int index) {
    return new ArrowInGroup(this, index);
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
    return this.getMagnification() * legend.getSymbolSpan();
  }

  /** */
  private Rectangle2D getDataElementBounds() {
    SGDrawingElement[] array = this.mDrawingElementArray;
    if (array != null) {
      if (array.length == 0) {
        return new Rectangle2D.Float();
      }

      SGDrawingElementArrow2D el = (SGDrawingElementArrow2D) array[0];
      Rectangle2D rect = el.getElementBounds();
      return rect;
    }
    return new Rectangle2D.Float();
  }

  /**
   * Returns the preferred height.
   *
   * @return the preferred height
   */
  public float getPreferredHeight() {
    final float elementSize = 1.20f * (float) this.getDataElementBounds().getHeight();
    final float strHeight = (float) this.mMagnitudeString.getElementBounds().getHeight();
    final float size = elementSize + 2.0f * strHeight;
    return size;
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

  private SGTuple2f mStartPoint = new SGTuple2f();

  private SGTuple2f mEndPoint = new SGTuple2f();

  /** */
  public boolean createDrawingElementInLegend() {
    Rectangle2D lRect = legend.getRectOfGroupSet(this.mGroupSet);
    Rectangle2D dRect = this.mBoundsRect;
    final float headSize = this.getMagnification() * this.getHeadSize();
    final int startHeadType = this.getStartHeadType();
    final int endHeadType = this.getEndHeadType();

    final float x = (float) lRect.getX();
    final float y = (float) lRect.getY();
    final float w = (float) dRect.getWidth();
    final float h = (float) lRect.getHeight();

    SGTuple2f start = new SGTuple2f();
    start.x = x;
    if (this.doShiftX(startHeadType)) {
      start.x += headSize;
    }
    start.y = y + 0.50f * h;

    SGTuple2f end = new SGTuple2f();
    end.x = x + w;
    if (this.doShiftX(endHeadType)) {
      end.x -= headSize;
    }
    end.y = start.y;

    this.mStartPoint = start;
    this.mEndPoint = end;
    if (this.setLocation(new SGTuple2f[] {start}, new SGTuple2f[] {end}) == false) {
      return false;
    }

    //
    this.updateMagnitudeString();

    return true;
  }

  private boolean doShiftX(final int type) {
    final boolean b =
        (type != SGIArrowConstants.SYMBOL_TYPE_ARROW_HEAD)
            && (type != SGIArrowConstants.SYMBOL_TYPE_ARROW)
            && (type != SGIArrowConstants.SYMBOL_TYPE_TRANSVERSELINE)
            && (type != SGIArrowConstants.SYMBOL_TYPE_VOID);
    return b;
  }

  private boolean updateMagnitudeString() {
    ElementGroupSetInLegendVXY groupSet = (ElementGroupSetInLegendVXY) this.mGroupSet;
    final float perCm = groupSet.getMagnitudePerCM();
    if (Float.isNaN(perCm)) {
      this.mMagnitudeString.setString("NaN");
    } else {
      final float span = legend.getSymbolSpan(cm);
      final float value = perCm * span;
      final float valueReduced =
          (float)
              SGUtilityNumber.getNumberInNumberOrder(
                  value, value, 3, RoundingMode.HALF_UP.ordinal());
      this.mMagnitudeString.setString(Float.toString(valueReduced));
    }

    Rectangle2D lRect = legend.getRectOfGroupSet(this.mGroupSet);
    Rectangle2D dRect = this.mBoundsRect;
    Rectangle2D sRect = this.mMagnitudeString.getElementBounds();
    Rectangle2D elRect = this.getDataElementBounds();

    SGTuple2f location = new SGTuple2f();
    location.x = (float) (lRect.getX() + 0.50f * (dRect.getWidth() - sRect.getWidth()));
    location.y = (float) (elRect.getY() + elRect.getHeight()) + 2.0f;

    this.mMagnitudeString.setLocation(location);

    return true;
  }

  // string to display the magnitude of an arrow
  private SGDrawingElementString2DExtended mMagnitudeString =
      new SGDrawingElementString2DExtended();

  /**
   * Sets the magnification.
   *
   * @param mag the magnification to set
   * @return true if succeeded
   */
  public boolean setMagnification(final float mag) {
    if (super.setMagnification(mag) == false) {
      return false;
    }
    if (this.mMagnitudeString.setMagnification(mag) == false) {
      return false;
    }
    return true;
  }

  /** */
  public boolean paintElement(final Graphics2D g2d, final Rectangle2D clipRect) {
    super.paintElement(g2d, clipRect);

    // paint the string to display the magnitude
    this.mMagnitudeString.paint(g2d, clipRect);

    // draw anchors if the group set is selected
    if (this.mGroupSet.isViewable()
        && this.mGroupSet.isSelected()
        && legend.isSymbolsVisibleAroundFocusedObjects()) {
      SGDrawingElementArrow arrow = (SGDrawingElementArrow) this.mDrawingElementArray[0];
      SGTuple2f start = arrow.getStart();
      SGTuple2f end = arrow.getEnd();
      SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(
          new Point2D.Float(start.x, start.y), g2d);
      SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(
          new Point2D.Float(end.x, end.y), g2d);
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
  public SGTuple2f getEndLocation(int index) {
    return this.mEndPoint;
  }

  @Override
  public SGTuple2f getStartLocation(int index) {
    return this.mStartPoint;
  }

  public boolean initDrawingElement(float[] xArray, float[] yArray) {
    return false;
  }

  public boolean setLocation(float[] xCoordinateArray, float[] yCoordinateArray) {
    return false;
  }
}
