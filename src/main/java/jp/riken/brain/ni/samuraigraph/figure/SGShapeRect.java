package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGShapeConstants.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.*;
import jp.riken.brain.ni.samuraigraph.base.SGFigureElementAxisConstants;
import org.w3c.dom.Element;

class SGShapeRect extends SGSimpleRectangle2D
    implements SGFigureElementShape.IElement, SGIRectangularShapeDialogObserver {

  private final SGFigureElementShape owner;

  SGShapeRect(final SGFigureElementShape owner) {
    this.owner = owner;
  }

  public static final String NAME = "Rectangle";

  public static final String KEY_LEFT_X_VALUE = "LeftXValue";

  public static final String KEY_RIGHT_X_VALUE = "RightXValue";

  public static final String KEY_BOTTOM_Y_VALUE = "BottomYValue";

  public static final String KEY_TOP_Y_VALUE = "TopYValue";

  public static final String KEY_ANCHORED = "Anchored";

  // Shape object
  private SGFigureElementShape.ShapeObject mShape = null;

  /** */
  private double mXValue1;

  /** */
  private double mYValue1;

  /** */
  private double mXValue2;

  /** */
  private double mYValue2;

  /** If this is true, rect object does not move or reshape with axis scaling. */
  private boolean mIsAnchored;

  //

  /** */
  private boolean init() {
    this.setWidth(DEFAULT_SHAPE_RECTANGLE_WIDTH, cm);
    this.setHeight(DEFAULT_SHAPE_RECTANGLE_HEIGHT, cm);

    SGSelectablePaint paint = new SGSelectablePaint();
    paint.setFillColor(DEFAULT_SHAPE_RECTANGLE_INNER_COLOR);
    paint.setSelectedPaintStyle(SGSelectablePaint.STYLE_INDEX_FILL);
    this.setInnerPaint(paint);

    this.setEdgeLineWidth(DEFAULT_SHAPE_RECTANGLE_EDGE_LINE_WIDTH, LINE_WIDTH_UNIT);
    this.setEdgeLineType(DEFAULT_SHAPE_RECTANGLE_EDGE_LINE_TYPE);
    this.setEdgeLineColor(DEFAULT_SHAPE_RECTANGLE_EDGE_LINE_COLOR);
    this.setEdgeLineVisible(DEFAULT_SHAPE_RECTANGLE_EDGE_LINE_VISIBLE);

    this.setAnchored(DEFAULT_SHAPE_RECTANGLE_ANCHORED);

    return true;
  }

  public void dispose() {
    super.dispose();
    this.mShape = null;
    if (this.mTemporaryProperties != null) {
      this.mTemporaryProperties.dispose();
      this.mTemporaryProperties = null;
    }
  }

  /** */
  protected SGFigureElementShape.ShapeObject getShapeObject() {
    return this.mShape;
  }

  /** */
  public void setShapeObject(SGFigureElementShape.ShapeObject sh) {
    this.mShape = sh;
  }

  protected SGFigureElementShape getShapeElement() {
    return this.mShape.getShapeElement();
  }

  protected int getID() {
    return this.mShape.getID();
  }

  protected SGAxis getXAxis() {
    return this.mShape.getXAxis();
  }

  protected SGAxis getYAxis() {
    return this.mShape.getYAxis();
  }

  public float getLineWidth(final String unit) {
    return this.getEdgeLineWidth(unit);
  }

  /** */
  protected boolean isHorizontallyReversed() {
    return (this.getWidth() < 0.0f);
  }

  /** */
  protected boolean isVerticallyReversed() {
    return (this.getHeight() < 0.0f);
  }

  /** */
  public float getX1() {
    return this.getShapeElement().getXFromGraphRectValue(super.getX());
  }

  /** */
  public float getX() {
    return this.getX1();
  }

  /** */
  public float getX2() {
    return this.getX1() + this.getMagnification() * this.getWidth();
  }

  /** */
  public float getY1() {
    return this.getShapeElement().getYFromGraphRectValue(super.getY());
  }

  /** */
  public float getY() {
    return this.getY1();
  }

  /** */
  public float getY2() {
    return this.getY1() + this.getMagnification() * this.getHeight();
  }

  /** */
  public float getLeftX() {
    return !this.isHorizontallyReversed() ? this.getX1() : this.getX2();
  }

  /** */
  public float getRightX() {
    return !this.isHorizontallyReversed() ? this.getX2() : this.getX1();
  }

  /** */
  public float getCenterX() {
    return this.getX1() + 0.50f * this.getMagnification() * this.getWidth();
  }

  /** */
  public float getTopY() {
    return !this.isVerticallyReversed() ? this.getY1() : this.getY2();
  }

  /** */
  public float getBottomY() {
    return !this.isVerticallyReversed() ? this.getY2() : this.getY1();
  }

  /** */
  public float getCenterY() {
    return this.getY1() + 0.50f * this.getMagnification() * this.getHeight();
  }

  /**
   * @param x
   */
  public void setLeftX(final float x) {
    if (!this.isHorizontallyReversed()) {
      this.setX1(x);
    } else {
      this.setX2(x);
    }
  }

  /**
   * @param x
   */
  public void setRightX(final float x) {
    if (!this.isHorizontallyReversed()) {
      this.setX2(x);
    } else {
      this.setX1(x);
    }
  }

  /**
   * @param y
   */
  public void setTopY(final float y) {
    if (!this.isVerticallyReversed()) {
      this.setY1(y);
    } else {
      this.setY2(y);
    }
  }

  /**
   * @param y
   */
  public void setBottomY(final float y) {
    if (!this.isVerticallyReversed()) {
      this.setY2(y);
    } else {
      this.setY1(y);
    }
  }

  /**
   * @param x
   */
  public boolean setX1(final float x) {
    return this.setX(x);
  }

  /**
   * @param x
   */
  public boolean setX2(final float x) {
    return this.setX(x - this.getMagnification() * this.getWidth());
  }

  /**
   * @param y
   */
  public boolean setY1(final float y) {
    return this.setY(y);
  }

  /**
   * @param y
   */
  public boolean setY2(final float y) {
    return this.setY(y - this.getMagnification() * this.getHeight());
  }

  /** */
  public boolean setX(final float x) {
    return super.setX(this.getShapeElement().getGraphRectValueX(x));
  }

  /** */
  public boolean setY(final float y) {
    return super.setY(this.getShapeElement().getGraphRectValueY(y));
  }

  /** */
  public boolean setShapeWithAxesValues() {
    SGFigureElementShape.ShapeObject sh = this.mShape;
    SGAxis xAxis = this.getXAxis();
    SGAxis yAxis = this.getYAxis();

    SGFigureElementShape sElement = this.getShapeElement();

    final float x1 = sElement.calcLocation(this.mXValue1, xAxis, true);
    final float y1 = sElement.calcLocation(this.mYValue1, yAxis, false);
    final float x2 = sElement.calcLocation(this.mXValue2, xAxis, true);
    final float y2 = sElement.calcLocation(this.mYValue2, yAxis, false);

    if (Float.isNaN(x1) || Float.isNaN(y1) || Float.isNaN(x2) || Float.isNaN(y2)) {
      sh.setValid(false);
      return false;
    }
    sh.setValid(true);

    final float mag = this.getMagnification();
    final float w = (x2 - x1) / mag;
    final float h = (y2 - y1) / mag;
    this.setLocation(x1, y1);
    this.setWidth(w);
    this.setHeight(h);

    return true;
  }

  /** */
  public boolean setAxisValuesWithShape() {
    SGAxis xAxis = this.getXAxis();
    SGAxis yAxis = this.getYAxis();

    SGFigureElementShape sElement = this.getShapeElement();

    final double xValue1 = sElement.calcValue(this.getX1(), xAxis, true);
    final double yValue1 = sElement.calcValue(this.getY1(), yAxis, false);
    final double xValue2 = sElement.calcValue(this.getX2(), xAxis, true);
    final double yValue2 = sElement.calcValue(this.getY2(), yAxis, false);

    this.mXValue1 = SGUtilityNumber.getNumberInRangeOrder(xValue1, xAxis);
    this.mYValue1 = SGUtilityNumber.getNumberInRangeOrder(yValue1, yAxis);
    this.mXValue2 = SGUtilityNumber.getNumberInRangeOrder(xValue2, xAxis);
    this.mYValue2 = SGUtilityNumber.getNumberInRangeOrder(yValue2, yAxis);

    return true;
  }

  public int getMouseLocation(final int x, final int y) {
    final int left = (int) this.getLeftX();
    final int right = (int) this.getRightX();
    final int top = (int) this.getTopY();
    final int bottom = (int) this.getBottomY();
    final int centerX = (int) this.getCenterX();
    final int centerY = (int) this.getCenterY();

    int location = -1;
    if (SGFigureElementShape.isInside(left, top, x, y)) {
      location = NORTH_WEST;
    } else if (SGFigureElementShape.isInside(left, centerY, x, y)) {
      location = WEST;
    } else if (SGFigureElementShape.isInside(left, bottom, x, y)) {
      location = SOUTH_WEST;
    } else if (SGFigureElementShape.isInside(right, top, x, y)) {
      location = NORTH_EAST;
    } else if (SGFigureElementShape.isInside(right, centerY, x, y)) {
      location = EAST;
    } else if (SGFigureElementShape.isInside(right, bottom, x, y)) {
      location = SOUTH_EAST;
    } else if (SGFigureElementShape.isInside(centerX, top, x, y)) {
      location = NORTH;
    } else if (SGFigureElementShape.isInside(centerX, bottom, x, y)) {
      location = SOUTH;
    } else if (this.getElementBounds().contains(x, y)) {
      location = OTHER;
    }

    return location;
  }

  /** */
  public List<Point2D> getAnchorPointList() {
    ArrayList<Point2D> list = new ArrayList<Point2D>();

    final float mag = this.getMagnification();
    final float x = this.getX();
    final float y = this.getY();
    final float w = mag * this.getWidth();
    final float h = mag * this.getHeight();
    final float cx = x + 0.50f * w;
    final float cy = y + 0.50f * h;

    Point2D pNW = new Point2D.Float(x, y);
    Point2D pNE = new Point2D.Float(x + w, y);
    Point2D pSW = new Point2D.Float(x, y + h);
    Point2D pSE = new Point2D.Float(x + w, y + h);
    Point2D pW = new Point2D.Float(x, cy);
    Point2D pE = new Point2D.Float(x + w, cy);
    Point2D pN = new Point2D.Float(cx, y);
    Point2D pS = new Point2D.Float(cx, y + h);

    list.add(pNW);
    list.add(pNE);
    list.add(pSW);
    list.add(pSE);
    list.add(pW);
    list.add(pE);
    list.add(pN);
    list.add(pS);

    return list;
  }

  /** */
  public boolean drag(MouseEvent e, Point pos, final int ml) {
    Rectangle2D rect = this.getElementBounds();

    // update the rectangle
    SGUtility.resizeRectangle(rect, pos, e, ml);

    final float mag = this.getMagnification();
    final float x = (float) rect.getX();
    final float y = (float) rect.getY();
    final float w = (float) rect.getWidth() / mag;
    final float h = (float) rect.getHeight() / mag;

    this.setWidth(w);
    this.setHeight(h);
    this.setLeftX(x);
    this.setTopY(y);

    return true;
  }

  /** */
  public Object copy() {
    SGShapeRect el = new SGShapeRect(owner);
    el.setShapeObject(this.mShape);
    el.setMagnification(this.getMagnification());
    el.setProperties(this.getProperties());
    el.setShapeWithAxesValues();

    return el;
  }

  /** */
  public void translate(final float dx, final float dy) {
    this.translateSub(dx, dy);
  }

  public void translateSub(final float dx, final float dy) {
    this.setLocation(this.getX() + dx, this.getY() + dy);
    this.setAxisValuesWithShape();
  }

  /** Overrode for the anchors. */
  public boolean contains(final int x, final int y) {
    if (super.contains(x, y) == true) {
      return true;
    }

    if (this.mShape.isSelected()) {
      final int left = (int) this.getLeftX();
      final int right = (int) this.getRightX();
      final int top = (int) this.getTopY();
      final int bottom = (int) this.getBottomY();
      final int centerX = (int) this.getCenterX();
      final int centerY = (int) this.getCenterY();

      final boolean b =
          SGFigureElementShape.isInside(left, top, x, y)
              || (SGFigureElementShape.isInside(left, centerY, x, y))
              || (SGFigureElementShape.isInside(left, bottom, x, y))
              || (SGFigureElementShape.isInside(right, top, x, y))
              || (SGFigureElementShape.isInside(right, centerY, x, y))
              || (SGFigureElementShape.isInside(right, bottom, x, y))
              || (SGFigureElementShape.isInside(centerX, top, x, y))
              || (SGFigureElementShape.isInside(centerX, bottom, x, y));
      if (b) {
        return true;
      }
    }

    return false;
  }

  /** */
  public String getName() {
    return NAME;
  }

  /** */
  public String getClassDescription() {
    return "";
  }

  /**
   * Returns the description of an instance.
   *
   * @return the description of an instance
   */
  public String getInstanceDescription() {
    SGIFigureElementAxis aElement = owner.mAxisElement;
    String xAxis = aElement.getLocationName(this.getXAxis());
    String yAxis = aElement.getLocationName(this.getYAxis());

    StringBuilder sb = new StringBuilder();
    sb.append(this.getID());
    sb.append(": ");
    sb.append(this.getName());
    sb.append(", AxisX=");
    sb.append(xAxis.toString());
    sb.append(", AxisY=");
    sb.append(yAxis.toString());
    sb.append(", X=");
    sb.append(this.mXValue1);
    sb.append(", Y=");
    sb.append(this.mYValue1);
    return sb.toString();
  }

  /** */
  public ArrayList<SGINode> getChildNodes() {
    return new ArrayList<SGINode>();
  }

  /** */
  public SGProperties getProperties() {
    SGProperties p = new SGFigureElementShape.RectangularShapeProperties();
    if (this.getProperties(p) == false) {
      return null;
    }
    return p;
  }

  /** */
  public boolean getProperties(SGProperties p) {
    if ((p instanceof SGFigureElementShape.RectangularShapeProperties) == false) {
      return false;
    }
    if (super.getProperties(p) == false) {
      return false;
    }
    SGFigureElementShape.RectangularShapeProperties rp =
        (SGFigureElementShape.RectangularShapeProperties) p;

    SGFigureElementShape sElement = this.getShapeElement();
    SGIFigureElementAxis aElement = sElement.getAxisElement();

    SGAxis xAxis = this.getXAxis();
    SGAxis yAxis = this.getYAxis();
    rp.setXAxisLocation(aElement.getLocationInPlane(xAxis));
    rp.setYAxisLocation(aElement.getLocationInPlane(yAxis));

    rp.setXValue1(this.mXValue1);
    rp.setYValue1(this.mYValue1);
    rp.setXValue2(this.mXValue2);
    rp.setYValue2(this.mYValue2);
    rp.setAnchored(this.mIsAnchored);

    return true;
  }

  /** */
  public boolean setProperties(SGProperties p) {
    if ((p instanceof SGFigureElementShape.RectangularShapeProperties) == false) return false;

    if (super.setProperties(p) == false) return false;

    SGFigureElementShape.RectangularShapeProperties rp =
        (SGFigureElementShape.RectangularShapeProperties) p;

    final Double x1 = rp.getXValue1();
    if (x1 == null) return false;
    this.mXValue1 = x1.doubleValue();

    final Double y1 = rp.getYValue1();
    if (y1 == null) return false;
    this.mYValue1 = y1.doubleValue();

    final Double x2 = rp.getXValue2();
    if (x2 == null) return false;
    this.mXValue2 = x2.doubleValue();

    final Double y2 = rp.getYValue2();
    if (y2 == null) return false;
    this.mYValue2 = y2.doubleValue();

    final Boolean anchored = rp.getAnchored();
    if (anchored == null) return false;
    this.mIsAnchored = anchored.booleanValue();

    return true;
  }

  /** */
  public SGProperties getMemento() {
    return this.getProperties();
  }

  /** */
  public boolean setMemento(SGProperties p) {
    return this.setProperties(p);
  }

  //
  // dialog
  //

  /** */
  public double getLeftXValue() {
    return !this.isHorizontallyReversed() ? this.mXValue1 : this.mXValue2;
  }

  /** */
  public double getTopYValue() {
    return !this.isVerticallyReversed() ? this.mYValue1 : this.mYValue2;
  }

  /** */
  public double getRightXValue() {
    return !this.isHorizontallyReversed() ? this.mXValue2 : this.mXValue1;
  }

  /** */
  public double getBottomYValue() {
    return !this.isVerticallyReversed() ? this.mYValue2 : this.mYValue1;
  }

  @Override
  public boolean isAnchored() {
    return this.mIsAnchored;
  }

  /** */
  public float getLineWidth() {
    return this.getEdgeLineWidth();
  }

  /** */
  public int getLineType() {
    return this.getEdgeLineType();
  }

  /** */
  public Color getLineColor() {
    return this.getEdgeLineColor();
  }

  @Override
  public boolean isLineVisible() {
    return this.isEdgeLineVisible();
  }

  /**
   * Sets the axis value of the left edge.
   *
   * @param value the axis value to set
   * @return true if succeeded
   */
  public boolean setLeftXValue(final double value) {
    if (this.getXAxis().isValidValue(value) == false) {
      return false;
    }
    if (!this.isHorizontallyReversed()) {
      this.mXValue1 = value;
    } else {
      this.mXValue2 = value;
    }
    return true;
  }

  /**
   * Sets the axis value of the right edge.
   *
   * @param value the axis value to set
   * @return true if succeeded
   */
  public boolean setRightXValue(final double value) {
    if (this.getXAxis().isValidValue(value) == false) {
      return false;
    }
    if (!this.isHorizontallyReversed()) {
      this.mXValue2 = value;
    } else {
      this.mXValue1 = value;
    }
    return true;
  }

  /**
   * Sets the axis value of the upper edge.
   *
   * @param value the axis value to set
   * @return true if succeeded
   */
  public boolean setTopYValue(final double value) {
    if (this.getYAxis().isValidValue(value) == false) {
      return false;
    }
    if (!this.isVerticallyReversed()) {
      this.mYValue1 = value;
    } else {
      this.mYValue2 = value;
    }
    return true;
  }

  /**
   * Sets the axis value of the lower edge.
   *
   * @param value the axis value to set
   * @return true if succeeded
   */
  public boolean setBottomYValue(final double value) {
    if (this.getYAxis().isValidValue(value) == false) {
      return false;
    }
    if (!this.isVerticallyReversed()) {
      this.mYValue2 = value;
    } else {
      this.mYValue1 = value;
    }
    return true;
  }

  @Override
  public boolean setAnchored(boolean anchored) {
    this.mIsAnchored = anchored;
    return true;
  }

  /**
   * Sets the line width in a given unit.
   *
   * @param lw the line width to set
   * @param unit the unit for given line width
   * @return true if succeeded
   */
  public boolean setLineWidth(final float lw, final String unit) {
    return this.setEdgeLineWidth(lw, unit);
  }

  /**
   * Sets the edge line type.
   *
   * @param type the line type
   * @return true if succeeded
   */
  public boolean setLineType(final int type) {
    return this.setEdgeLineType(type);
  }

  /**
   * Sets the line color.
   *
   * @param cl the color to set
   * @return true if succeeded
   */
  public boolean setLineColor(final Color cl) {
    return this.setEdgeLineColor(cl);
  }

  @Override
  public boolean setLineVisible(final boolean visible) {
    return this.setEdgeLineVisible(visible);
  }

  /** */
  public int getXAxisLocation() {
    return this.mShape.getAxisConfiguration(this.getXAxis());
  }

  /** */
  public int getYAxisLocation() {
    return this.mShape.getAxisConfiguration(this.getYAxis());
  }

  /** */
  public boolean setXAxisLocation(final int config) {
    if (config != SGFigureElementAxisConstants.AXIS_HORIZONTAL_1
        && config != SGFigureElementAxisConstants.AXIS_HORIZONTAL_2) {
      return false;
    }
    this.mShape.setXAxis(config);
    return true;
  }

  /** */
  public boolean setYAxisLocation(final int config) {
    if (config != SGFigureElementAxisConstants.AXIS_VERTICAL_1
        && config != SGFigureElementAxisConstants.AXIS_VERTICAL_2) {
      return false;
    }
    this.mShape.setYAxis(config);
    return true;
  }

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidLeftXValue(final int config, final Number value) {
    SGIFigureElementAxis aElement = this.getShapeElement().getAxisElement();
    final SGAxis axis = (config == -1) ? this.getXAxis() : aElement.getAxisInPlane(config);
    final double v = (value != null) ? value.doubleValue() : this.getLeftXValue();
    return axis.isValidValue(v);
  }

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidTopYValue(final int config, final Number value) {
    SGIFigureElementAxis aElement = this.getShapeElement().getAxisElement();
    final SGAxis axis = (config == -1) ? this.mShape.getYAxis() : aElement.getAxisInPlane(config);
    final double v = (value != null) ? value.doubleValue() : this.getTopYValue();
    return axis.isValidValue(v);
  }

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidRightXValue(final int config, final Number value) {
    SGIFigureElementAxis aElement = this.getShapeElement().getAxisElement();
    final SGAxis axis = (config == -1) ? this.getXAxis() : aElement.getAxisInPlane(config);
    final double v = (value != null) ? value.doubleValue() : this.getRightXValue();
    return axis.isValidValue(v);
  }

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidBottomYValue(final int config, final Number value) {
    SGIFigureElementAxis aElement = this.getShapeElement().getAxisElement();
    final SGAxis axis = (config == -1) ? this.getYAxis() : aElement.getAxisInPlane(config);
    final double v = (value != null) ? value.doubleValue() : this.getBottomYValue();
    return axis.isValidValue(v);
  }

  /** */
  private SGProperties mTemporaryProperties = null;

  /** */
  public boolean prepare() {
    this.mTemporaryProperties = this.getProperties();
    return true;
  }

  /** */
  public boolean commit() {
    SGProperties pTemp = this.mTemporaryProperties;
    SGProperties pPresent = this.getProperties();
    if (pTemp.equals(pPresent) == false) {
      this.mShape.setChanged(true);
    }

    this.setShapeWithAxesValues();

    SGFigureElementShape sh = this.getShapeElement();
    sh.notifyChangeOnCommit();
    sh.repaint();

    return true;
  }

  /** */
  public boolean cancel() {
    if (this.setProperties(this.mTemporaryProperties) == false) {
      return false;
    }
    this.mTemporaryProperties = null;

    this.setShapeWithAxesValues();
    SGFigureElementShape sh = this.getShapeElement();
    sh.notifyChangeOnCancel();
    sh.repaint();
    return true;
  }

  /** */
  public boolean preview() {
    this.setShapeWithAxesValues();
    SGFigureElementShape sh = this.getShapeElement();
    sh.notifyChange();
    sh.repaint();
    return true;
  }

  /**
   * Returns the property dialog.
   *
   * @return a property dialog
   */
  public SGPropertyDialog getPropertyDialog() {
    return owner.getShapeDialog(this);
  }

  public void dump() {
    // debug method
  }

  @Override
  public SGPropertyMap getCommandPropertyMap() {
    SGPropertyMap map = new SGPropertyMap();

    // properties of shape
    this.addProperties(
        map,
        COM_RECTANGLE_EDGE_LINE_WIDTH,
        COM_RECTANGLE_EDGE_LINE_TYPE,
        COM_RECTANGLE_EDGE_LINE_COLOR,
        COM_RECTANGLE_EDGE_LINE_VISIBLE);

    // properties of location
    this.addProperties(
        map,
        COM_RECTANGLE_LEFT_X,
        COM_RECTANGLE_RIGHT_X,
        COM_RECTANGLE_TOP_Y,
        COM_RECTANGLE_BOTTOM_Y,
        COM_RECTANGLE_ANCHORED);

    // properties of inner paint
    String[] rectKeys = {
      COM_RECTANGLE_BACKGROUND_PAINT_STYLE,
      COM_RECTANGLE_BACKGROUND_FILL_COLOR,
      COM_RECTANGLE_BACKGROUND_PATTERN_COLOR,
      COM_RECTANGLE_BACKGROUND_PATTERN_TYPE,
      COM_RECTANGLE_BACKGROUND_GRADATION_COLOR1,
      COM_RECTANGLE_BACKGROUND_GRADATION_COLOR2,
      COM_RECTANGLE_BACKGROUND_GRADATION_DIRECTION,
      COM_RECTANGLE_BACKGROUND_GRADATION_ORDER,
      COM_RECTANGLE_BACKGROUND_TRANSPARENCY
    };
    SGSelectablePaint.COMMAND_KEYS[] comKeys = SGSelectablePaint.COMMAND_KEYS.values();
    Map<SGSelectablePaint.COMMAND_KEYS, String> keyMap =
        new HashMap<SGSelectablePaint.COMMAND_KEYS, String>();
    for (int ii = 0; ii < comKeys.length; ii++) {
      keyMap.put(comKeys[ii], rectKeys[ii]);
    }
    SGSelectablePaint paint = (SGSelectablePaint) this.getInnerPaint();
    paint.getProperties(map, keyMap);

    return map;
  }

  @Override
  public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    SGPropertyMap map = super.getPropertyFileMap(params);
    this.addProperties(
        map,
        KEY_LEFT_X_VALUE,
        KEY_RIGHT_X_VALUE,
        KEY_TOP_Y_VALUE,
        KEY_BOTTOM_Y_VALUE,
        KEY_ANCHORED);
    return map;
  }

  private void addProperties(
      SGPropertyMap map,
      String leftXKey,
      String rightXKey,
      String topYKey,
      String bottomYKey,
      String anchorKey) {
    SGPropertyUtility.addProperty(map, leftXKey, this.getLeftXValue());
    SGPropertyUtility.addProperty(map, rightXKey, this.getRightXValue());
    SGPropertyUtility.addProperty(map, topYKey, this.getTopYValue());
    SGPropertyUtility.addProperty(map, bottomYKey, this.getBottomYValue());
    SGPropertyUtility.addProperty(map, anchorKey, this.isAnchored());
  }

  /** */
  public boolean readProperty(final Element el) {
    if (super.readProperty(el) == false) {
      return false;
    }

    String str = null;
    Number num = null;
    Boolean b = null;

    final SGAxis xAxis = this.getXAxis();
    final SGAxis yAxis = this.getYAxis();

    // left x value
    str = el.getAttribute(KEY_LEFT_X_VALUE);
    if (str.length() != 0) {
      num = SGUtilityText.getDouble(str);
      if (num == null) {
        return false;
      }
      final double leftXValue = num.doubleValue();
      if (xAxis.isValidValue(leftXValue) == false) {
        return false;
      }
      this.setLeftXValue(leftXValue);
    }

    // right x value
    str = el.getAttribute(KEY_RIGHT_X_VALUE);
    if (str.length() != 0) {
      num = SGUtilityText.getDouble(str);
      if (num == null) {
        return false;
      }
      final double rightXValue = num.doubleValue();
      if (xAxis.isValidValue(rightXValue) == false) {
        return false;
      }
      this.setRightXValue(rightXValue);
    }

    // bottom y value
    str = el.getAttribute(KEY_BOTTOM_Y_VALUE);
    if (str.length() != 0) {
      num = SGUtilityText.getDouble(str);
      if (num == null) {
        return false;
      }
      final double bottomYValue = num.doubleValue();
      if (yAxis.isValidValue(bottomYValue) == false) {
        return false;
      }
      this.setBottomYValue(bottomYValue);
    }

    // top y value
    str = el.getAttribute(KEY_TOP_Y_VALUE);
    if (str.length() != 0) {
      num = SGUtilityText.getDouble(str);
      if (num == null) {
        return false;
      }
      final double topYValue = num.doubleValue();
      if (yAxis.isValidValue(topYValue) == false) {
        return false;
      }
      this.setTopYValue(topYValue);
    }

    // anchored
    str = el.getAttribute(KEY_ANCHORED);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      this.setAnchored(b.booleanValue());
      this.setShapeWithAxesValues();
    }

    return true;
  }

  /**
   * Sets the properties.
   *
   * @param map a map of properties
   * @param iResult the result of setting properties
   * @return updated result
   */
  public SGPropertyResults setProperties(SGPropertyMap map, SGPropertyResults iResult) {

    SGPropertyResults result = (SGPropertyResults) iResult.clone();

    Iterator<String> itr = map.getKeyIterator();
    while (itr.hasNext()) {
      String key = itr.next();
      String value = map.getValueString(key);

      if (COM_RECTANGLE_LEFT_X.equalsIgnoreCase(key)) {
        Double num = SGUtilityText.getDouble(value);
        if (num == null) {
          result.putResult(COM_RECTANGLE_LEFT_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
          result.putResult(COM_RECTANGLE_LEFT_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setLeftXValue(num.doubleValue()) == false) {
          result.putResult(COM_RECTANGLE_LEFT_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_LEFT_X, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_RIGHT_X.equalsIgnoreCase(key)) {
        Double num = SGUtilityText.getDouble(value);
        if (num == null) {
          result.putResult(COM_RECTANGLE_RIGHT_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
          result.putResult(COM_RECTANGLE_RIGHT_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setRightXValue(num.doubleValue()) == false) {
          result.putResult(COM_RECTANGLE_RIGHT_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_RIGHT_X, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_TOP_Y.equalsIgnoreCase(key)) {
        Double num = SGUtilityText.getDouble(value);
        if (num == null) {
          result.putResult(COM_RECTANGLE_TOP_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
          result.putResult(COM_RECTANGLE_TOP_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setTopYValue(num.doubleValue()) == false) {
          result.putResult(COM_RECTANGLE_TOP_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_TOP_Y, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_BOTTOM_Y.equalsIgnoreCase(key)) {
        Double num = SGUtilityText.getDouble(value);
        if (num == null) {
          result.putResult(COM_RECTANGLE_BOTTOM_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
          result.putResult(COM_RECTANGLE_BOTTOM_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setBottomYValue(num.doubleValue()) == false) {
          result.putResult(COM_RECTANGLE_BOTTOM_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_BOTTOM_Y, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_EDGE_LINE_WIDTH.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_RECTANGLE_EDGE_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setEdgeLineWidth(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_RECTANGLE_EDGE_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_EDGE_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_EDGE_LINE_TYPE.equalsIgnoreCase(key)) {
        final Integer type = SGDrawingElementLine.getLineTypeFromName(value);
        if (type == null) {
          result.putResult(COM_RECTANGLE_EDGE_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setEdgeLineType(type) == false) {
          result.putResult(COM_RECTANGLE_EDGE_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_EDGE_LINE_TYPE, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_EDGE_LINE_COLOR.equalsIgnoreCase(key)) {
        Color cl = SGUtilityText.parseColorString(value);
        if (cl == null) {
          result.putResult(COM_RECTANGLE_EDGE_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setLineColor(cl) == false) {
          result.putResult(COM_RECTANGLE_EDGE_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_EDGE_LINE_COLOR, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_EDGE_LINE_VISIBLE.equalsIgnoreCase(key)) {
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(COM_RECTANGLE_EDGE_LINE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setLineVisible(b.booleanValue()) == false) {
          result.putResult(COM_RECTANGLE_EDGE_LINE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_EDGE_LINE_VISIBLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_BACKGROUND_PAINT_STYLE.equalsIgnoreCase(key)) {
        final Integer index = SGSelectablePaint.getStyleIndex(value);
        if (index == null) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_PAINT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setInnerPaintStyle(index) == false) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_PAINT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_BACKGROUND_PAINT_STYLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_BACKGROUND_FILL_COLOR.equalsIgnoreCase(key)) {
        Color cl = SGUtilityText.parseColorString(value);
        if (cl == null) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_FILL_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setInnerFillColor(cl) == false) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_FILL_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_BACKGROUND_FILL_COLOR, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_BACKGROUND_PATTERN_COLOR.equalsIgnoreCase(key)) {
        Color cl = SGUtilityText.parseColorString(value);
        if (cl == null) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_PATTERN_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setInnerPatternColor(cl) == false) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_PATTERN_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_BACKGROUND_PATTERN_COLOR, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_BACKGROUND_PATTERN_TYPE.equalsIgnoreCase(key)) {
        final Integer type = SGPatternPaint.getTypeFromName(value);
        if (type == null) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_PATTERN_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setInnerPatternType(type) == false) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_PATTERN_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_BACKGROUND_PATTERN_TYPE, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_BACKGROUND_GRADATION_COLOR1.equalsIgnoreCase(key)) {
        Color cl = SGUtilityText.parseColorString(value);
        if (cl == null) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_GRADATION_COLOR1, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setInnerGradationColor1(cl) == false) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_GRADATION_COLOR1, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_COLOR1, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_BACKGROUND_GRADATION_COLOR2.equalsIgnoreCase(key)) {
        Color cl = SGUtilityText.parseColorString(value);
        if (cl == null) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_GRADATION_COLOR2, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setInnerGradationColor2(cl) == false) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_GRADATION_COLOR2, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_COLOR2, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_BACKGROUND_GRADATION_DIRECTION.equalsIgnoreCase(key)) {
        final Integer index = SGGradationPaint.getDirectionIndex(value);
        if (index == null) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_GRADATION_DIRECTION, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setInnerGradationDirection(index) == false) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_GRADATION_DIRECTION, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_DIRECTION, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_BACKGROUND_GRADATION_ORDER.equalsIgnoreCase(key)) {
        final Integer index = SGGradationPaint.getOrderIndex(value);
        if (index == null) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_GRADATION_ORDER, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setInnerGradationOrder(index) == false) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_GRADATION_ORDER, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_ORDER, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_BACKGROUND_TRANSPARENCY.equalsIgnoreCase(key)) {
        Integer num = SGUtilityText.getInteger(value, percent);
        if (num == null) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_TRANSPARENCY, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setTransparent(num.intValue()) == false) {
          result.putResult(
              COM_RECTANGLE_BACKGROUND_TRANSPARENCY, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_BACKGROUND_TRANSPARENCY, SGPropertyResults.SUCCEEDED);
      } else if (COM_RECTANGLE_ANCHORED.equalsIgnoreCase(key)) {
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(COM_RECTANGLE_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setAnchored(b.booleanValue()) == false) {
          result.putResult(COM_RECTANGLE_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_RECTANGLE_ANCHORED, SGPropertyResults.SUCCEEDED);
      }
    }

    return result;
  }

  @Override
  public String getShapeType() {
    return SHAPE_TYPE_RECTANGLE;
  }

  @Override
  public boolean getAxisDateMode(final int location) {
    return owner.mAxisElement.getAxisDateMode(location);
  }
}
