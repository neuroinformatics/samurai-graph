package jp.riken.brain.ni.samuraigraph.figure.java2d;

import static jp.riken.brain.ni.samuraigraph.base.SGIConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants.*;

import java.awt.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;
import org.w3c.dom.Element;

class ElementGroupLine extends SGElementGroupLineForData implements ILegendElement {
  private final SGFigureElementLegend legend;

  private SGTuple2f mStart = new SGTuple2f();

  private SGTuple2f mEnd = new SGTuple2f();

  /** A group set that this element group belongs. */
  protected ElementGroupSetInLegend mGroupSet = null;

  /** The default constructor. */
  protected ElementGroupLine(final SGFigureElementLegend legend, SGISXYTypeData data) {
    super(data);
    this.legend = legend;

    // initialize the index array
    this.mEndPointsIndexArray = new int[1][2];
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

  /** */
  public float getPreferredWidth() {
    return this.getMagnification() * legend.getSymbolSpan();
  }

  /**
   * Returns the preferred height.
   *
   * @return the preferred height
   */
  public float getPreferredHeight() {
    return this.getMagnification() * this.getLineWidth();
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
    return 2;
  }

  /** Create drawing elements. */
  public boolean createDrawingElementInLegend() {
    // final float width = this.getDataElementWidth();

    Rectangle2D lRect = legend.getRectOfGroupSet(this.mGroupSet);
    Rectangle2D dRect = this.mBoundsRect;

    SGTuple2f start = new SGTuple2f();
    start.x = (float) lRect.getX();
    start.y = (float) lRect.getY() + 0.50f * (float) lRect.getHeight();
    SGTuple2f end = new SGTuple2f();
    end.x = start.x + (float) dRect.getWidth();
    end.y = start.y;

    if (this.setLocation(new SGTuple2f[] {start, end}) == false) {
      return false;
    }
    this.mStart = start;
    this.mEnd = end;

    return true;
  }

  /** Overrode for gradient paint and anchors. */
  @Override
  public boolean paintElement(final Graphics2D g2d, final Rectangle2D clipRect) {
    if (g2d == null) {
      return false;
    }

    // setup the stroke
    List<Object> lineTypeList = new ArrayList<Object>();
    List<Object> lineWidthList = new ArrayList<Object>();
    for (SGLineStyle style : this.mLineStyleList) {
      lineTypeList.add(style.getLineType());
      lineWidthList.add(style.getLineWidth());
    }

    List<Object> mfLineTypeList = SGUtility.findMostFrequentObjects(lineTypeList);
    if (mfLineTypeList.size() == 0) {
      return false;
    }
    int minLineType = Integer.MAX_VALUE;
    for (Object obj : mfLineTypeList) {
      Integer lt = (Integer) obj;
      if (lt < minLineType) {
        minLineType = lt;
      }
    }
    Integer lineType = minLineType;

    List<Object> mfLineWidthList = SGUtility.findMostFrequentObjects(lineWidthList);
    if (mfLineWidthList.size() == 0) {
      return false;
    }
    float maxLineWidth = 0.0f;
    for (Object obj : mfLineWidthList) {
      Float lw = (Float) obj;
      if (lw > maxLineWidth) {
        maxLineWidth = lw;
      }
    }
    Float lineWidth = maxLineWidth;

    SGStroke tempStroke = (SGStroke) this.mStroke.clone();
    tempStroke.setLineType(lineType);
    tempStroke.setLineWidth(lineWidth);
    Stroke stroke = tempStroke.getBasicStroke();
    g2d.setStroke(stroke);

    // draw lines
    final float y = this.mStart.y;
    if (this.mLineStyleList.size() == 1) {
      SGLineStyle style = this.mLineStyleList.get(0);
      Color cl = style.getColor();
      g2d.setPaint(cl);
      Line2D line = new Line2D.Float(this.mStart.x, y, this.mEnd.x, y);
      g2d.draw(line);
    } else if (this.mLineStyleList.size() > 1) {
      // setup the location of lines
      final float xDiff = (this.mEnd.x - this.mStart.x) / (this.mLineStyleList.size() - 1);
      final float[] xArray = new float[this.mLineStyleList.size()];
      for (int ii = 0; ii < this.mLineStyleList.size(); ii++) {
        xArray[ii] = this.mStart.x + ii * xDiff;
      }

      final int lineNum = this.mLineStyleList.size() - 1;

      // creates a single GeneralPath object
      GeneralPath gPath = new GeneralPath();
      for (int ii = 0; ii < lineNum; ii++) {
        Line2D line = new Line2D.Float(xArray[ii], y, xArray[ii + 1], y);
        gPath.append(line, true);
      }

      // creates GradientPaint objects
      Paint[] paintArray = new Paint[lineNum];
      for (int ii = 0; ii < lineNum; ii++) {
        SGLineStyle style1 = this.mLineStyleList.get(ii);
        SGLineStyle style2 = this.mLineStyleList.get(ii + 1);
        final float start = xArray[ii];
        final float end = xArray[ii + 1];
        Color cl1 = style1.getColor();
        Color cl2 = style2.getColor();
        GradientPaint gp = new GradientPaint(start, y, cl1, end, y, cl2);
        paintArray[ii] = gp;
      }

      // draw shapes
      Rectangle2D rect = legend.getRectOfGroupSet(this.mGroupSet);
      if (rect != null) {
        final float rectY = (float) rect.getY();
        final float rectH = (float) rect.getHeight();
        Shape clip = g2d.getClip();
        for (int ii = 0; ii < lineNum; ii++) {
          // clips the path
          final float rectW = xArray[ii + 1] - xArray[ii];
          Rectangle2D cRect = new Rectangle2D.Float(xArray[ii], rectY, rectW, rectH);
          g2d.setClip(cRect);
          g2d.setPaint(paintArray[ii]);
          g2d.draw(gPath);
        }
        g2d.setClip(clip);
      }
    }

    // draw anchors if the group set is selected
    if (this.mGroupSet.isViewable()
        && this.mGroupSet.isSelected()
        && legend.isSymbolsVisibleAroundFocusedObjects()) {
      if (this.mGroupSet instanceof SGIElementGroupSetXY) {
        SGIElementGroupSetXY gsSXY = (SGIElementGroupSetXY) this.mGroupSet;
        if (!gsSXY.getSymbolGroup().isVisible() && !gsSXY.getBarGroup().isVisible()) {
          SGDrawingElementLine2D line = (SGDrawingElementLine2D) this.mDrawingElementArray[0];
          SGTuple2f start = line.getStart();
          SGTuple2f end = line.getEnd();
          SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(
              new Point2D.Float(start.x, start.y), g2d);
          SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(
              new Point2D.Float(end.x, end.y), g2d);
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

  @Override
  public SGTuple2f getEnd(int index) {
    return this.mEnd;
  }

  @Override
  public SGTuple2f getStart(int index) {
    return this.mStart;
  }

  @Override
  public void setStyle(SGLineStyle s) {
    super.setStyle(s);
    SGLineStyle style = (s != null) ? (SGLineStyle) s.clone() : null;
    this.mLineStyleList.clear();
    this.mLineStyleList.add(style);
  }

  List<SGLineStyle> mLineStyleList = new ArrayList<SGLineStyle>();

  void clearLineStyleList() {
    this.mLineStyleList.clear();
  }

  void setLineStyleList(List<SGLineStyle> lineStyleList) {
    this.mLineStyleList = new ArrayList<SGLineStyle>(lineStyleList);
  }

  void addLineStyle(SGLineStyle lineStyle) {
    this.mLineStyleList.add(lineStyle);
  }

  void setLineStyle(SGLineStyle lineStyle, final int index) {
    this.mLineStyleList.set(index, lineStyle);
  }
}
