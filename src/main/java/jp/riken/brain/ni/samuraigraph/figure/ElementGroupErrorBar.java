package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGIConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import org.w3c.dom.Element;

class ElementGroupErrorBar extends SGElementGroupErrorBarForData implements ILegendElement {
  private final SGFigureElementLegend legend;

  /** A group set that this element group belongs. */
  protected ElementGroupSetInLegend mGroupSet = null;

  /** The constructor. */
  protected ElementGroupErrorBar(final SGFigureElementLegend legend, SGISXYTypeData data) {
    super(data);
    this.legend = legend;
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
    return this.getMagnification() * this.getHeadSize();
  }

  private static final float DEFAULT_ERROR_BAR_HEIGHT = 10.0f;

  /**
   * Returns the preferred height.
   *
   * @return the preferred height
   */
  public float getPreferredHeight() {

    ElementGroupSetInLegendSXY legend = (ElementGroupSetInLegendSXY) this.mGroupSet;

    SGData data = legend.getData();
    if (data instanceof SGISXYTypeData) {
      SGISXYTypeData dataSXY = (SGISXYTypeData) data;
      if (dataSXY.isErrorBarAvailable() == false) {
        return 0.0f;
      }
    }

    SGElementGroupSymbol sg = legend.getSymbolGroup();
    SGElementGroupBar bg = legend.getBarGroup();

    float size = 0.0f;
    if (sg.isVisible() || bg.isVisible()) {
      final float symbolSize = sg.isVisible() ? ((ILegendElement) sg).getPreferredHeight() : 0.0f;
      final float barWidth = bg.isVisible() ? ((ILegendElement) bg).getPreferredHeight() : 0.0f;
      size = (symbolSize > barWidth) ? symbolSize : barWidth;
    } else {
      size = DEFAULT_ERROR_BAR_HEIGHT;
    }

    final float mag = this.getMagnification();
    final float barHeight = 1.20f * mag * size;
    final float headSize = mag * this.getHeadSize();
    return barHeight + 2.0f * headSize;
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

  private SGTuple2f mCenter = new SGTuple2f();

  private SGTuple2f mLower = new SGTuple2f();

  private SGTuple2f mUpper = new SGTuple2f();

  /** Create drawing elements. */
  public boolean createDrawingElementInLegend() {
    SGTuple2f center = new SGTuple2f();
    SGTuple2f lower = new SGTuple2f();
    SGTuple2f upper = new SGTuple2f();

    Rectangle2D lRect = legend.getRectOfGroupSet(this.mGroupSet);
    Rectangle2D dRect = this.mBoundsRect;

    final float mag = this.getMagnification();

    final float x = (float) lRect.getX() + 0.50f * (float) dRect.getWidth();
    center.x = x;
    lower.x = x;
    upper.x = x;

    final float headSize = mag * this.getHeadSize();
    final float y = (float) lRect.getY() + (float) lRect.getHeight() / 2;
    final float d = ((float) dRect.getHeight() - headSize) / 2;

    center.y = y;
    lower.y = y + d;
    upper.y = y - d;

    this.mCenter = center;
    this.mLower = lower;
    this.mUpper = upper;
    if (this.setLocation(new SGTuple2f[] {center}, new SGTuple2f[] {lower}, new SGTuple2f[] {upper})
        == false) {
      return false;
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
   * Returns whether this group set contains the given point.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @return true if this element group contains the given point
   */
  public boolean contains(final int x, final int y) {
    // if the data object do not have error bars, return false
    SGISXYTypeData dataSXY = (SGISXYTypeData) legend.getData(this.mGroupSet);
    if (dataSXY.isErrorBarAvailable() == false) {
      return false;
    }
    return super.contains(x, y);
  }

  /**
   * Update the location of error bars.
   *
   * @return true if succeeded
   */
  public boolean updateLocation() {
    // do nothing
    return true;
  }

  @Override
  public SGTuple2f getLowerEndLocation(int index) {
    return this.mLower;
  }

  @Override
  public SGTuple2f getStartLocation(int index) {
    return this.mCenter;
  }

  @Override
  public SGTuple2f getUpperEndLocation(int index) {
    return this.mUpper;
  }
}
