package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGArrowConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGShapeConstants.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.*;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGIArrowDialogObserver;
import org.w3c.dom.Element;

class SGShapeArrow extends SGSimpleArrow2D
    implements SGFigureElementShape.IElement, SGIArrowDialogObserver {

  private final SGFigureElementShape owner;

  public static final String NAME = "Arrow";

  public static final String KEY_START_X_VALUE = "StartXValue";

  public static final String KEY_START_Y_VALUE = "StartYValue";

  public static final String KEY_END_X_VALUE = "EndXValue";

  public static final String KEY_END_Y_VALUE = "EndYValue";

  public static final String KEY_ANCHORED = "Anchored";

  /** */
  private float mStartX;

  private float mStartY;

  private float mEndX;

  private float mEndY;

  /** */
  private double mStartXValue;

  private double mStartYValue;

  private double mEndXValue;

  private double mEndYValue;

  private SGFigureElementShape.ShapeObject mShape = null;

  /** If this is true, rect object does not move or reshape with axis scaling. */
  private boolean mIsAnchored;

  SGShapeArrow(final SGFigureElementShape owner) {
    super();
    this.owner = owner;

    // set default properties
    this.setLineWidth(DEFAULT_SHAPE_ARROW_LINE_WIDTH, LINE_WIDTH_UNIT);
    this.setLineType(DEFAULT_SHAPE_ARROW_LINE_TYPE);
    this.setColor(DEFAULT_SHAPE_ARROW_COLOR);
    this.setHeadSize(DEFAULT_SHAPE_ARROW_HEAD_SIZE, ARROW_HEAD_SIZE_UNIT);
    this.setHeadAngle(DEFAULT_SHAPE_ARROW_HEAD_OPEN_ANGLE, DEFAULT_SHAPE_ARROW_HEAD_CLOSE_ANGLE);
    this.setAnchored(DEFAULT_SHAPE_ARROW_ANCHORED);
  }

  public void dispose() {
    super.dispose();
    this.mShape = null;
    if (this.mTemporaryProperties != null) {
      this.mTemporaryProperties.dispose();
      this.mTemporaryProperties = null;
    }
  }

  protected SGFigureElementShape getShapeElement() {
    return this.mShape.getShapeElement();
  }

  /** */
  public void setShapeObject(SGFigureElementShape.ShapeObject sh) {
    this.mShape = sh;
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

  private static final int START = NORTH_EAST;

  private static final int END = SOUTH_EAST;

  private static final int BODY = OTHER;

  public int getMouseLocation(final int x, final int y) {

    final int startX = (int) this.getStartX();
    final int startY = (int) this.getStartY();
    final int endX = (int) this.getEndX();
    final int endY = (int) this.getEndY();

    int location = -1;
    {
      if (SGFigureElementShape.isInside(startX, startY, x, y)) {
        location = START;
      } else if (SGFigureElementShape.isInside(endX, endY, x, y)) {
        location = END;
      } else {
        location = BODY;
      }
    }

    return location;
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
  public float getStartX() {
    return this.getShapeElement().getXFromGraphRectValue(this.mStartX);
  }

  /** */
  public float getStartY() {
    return this.getShapeElement().getYFromGraphRectValue(this.mStartY);
  }

  /** */
  public float getEndX() {
    return this.getShapeElement().getXFromGraphRectValue(this.mEndX);
  }

  /** */
  public float getEndY() {
    return this.getShapeElement().getYFromGraphRectValue(this.mEndY);
  }

  /**
   * Sets the location of the x-axis.
   *
   * @param location the location of the x-axis
   */
  public boolean setXAxisLocation(final int location) {
    return this.mShape.setXAxis(location);
  }

  /**
   * Sets the location of the x-axis.
   *
   * @param location the location of the x-axis
   */
  public boolean setYAxisLocation(final int location) {
    return this.mShape.setYAxis(location);
  }

  /** */
  public boolean setStartX(final float x) {
    super.setStartX(x);
    this.mStartX = this.getShapeElement().getGraphRectValueX(x);
    return true;
  }

  /** */
  public boolean setStartY(final float y) {
    super.setStartY(y);
    this.mStartY = this.getShapeElement().getGraphRectValueY(y);
    return true;
  }

  /** */
  public boolean setEndX(final float x) {
    super.setEndX(x);
    this.mEndX = this.getShapeElement().getGraphRectValueX(x);
    return true;
  }

  /** */
  public boolean setEndY(final float y) {
    super.setEndY(y);
    this.mEndY = this.getShapeElement().getGraphRectValueY(y);
    return true;
  }

  /** */
  public double getStartXValue() {
    return this.mStartXValue;
  }

  /** */
  public double getStartYValue() {
    return this.mStartYValue;
  }

  /** */
  public double getEndXValue() {
    return this.mEndXValue;
  }

  /** */
  public double getEndYValue() {
    return this.mEndYValue;
  }

  /**
   * Sets the axis value of the start x.
   *
   * @param value the axis value to set
   */
  public boolean setStartXValue(final double value) {
    if (this.getXAxis().isValidValue(value) == false) {
      return false;
    }
    this.mStartXValue = value;
    return true;
  }

  /**
   * Sets the axis value of the start y.
   *
   * @param value the axis value to set
   */
  public boolean setStartYValue(final double value) {
    if (this.getYAxis().isValidValue(value) == false) {
      return false;
    }
    this.mStartYValue = value;
    return true;
  }

  /**
   * Sets the axis value of the end x.
   *
   * @param value the axis value to set
   */
  public boolean setEndXValue(final double value) {
    if (this.getXAxis().isValidValue(value) == false) {
      return false;
    }
    this.mEndXValue = value;
    return true;
  }

  /**
   * Sets the axis value of the end y.
   *
   * @param value the axis value to set
   */
  public boolean setEndYValue(final double value) {
    if (this.getYAxis().isValidValue(value) == false) {
      return false;
    }
    this.mEndYValue = value;
    return true;
  }

  @Override
  public boolean isAnchored() {
    return this.mIsAnchored;
  }

  @Override
  public boolean setAnchored(boolean anchored) {
    this.mIsAnchored = anchored;
    return true;
  }

  /**
   * @param config
   * @param value
   */
  public boolean hasValidStartXValue(final int config, final Number value) {
    SGIFigureElementAxis aElement = this.getShapeElement().getAxisElement();
    final SGAxis axis = (config == -1) ? this.getXAxis() : aElement.getAxisInPlane(config);
    final double v = (value != null) ? value.doubleValue() : this.getStartXValue();
    return axis.isValidValue(v);
  }

  /**
   * @param config
   * @param value
   */
  public boolean hasValidStartYValue(final int config, final Number value) {
    SGIFigureElementAxis aElement = this.getShapeElement().getAxisElement();
    final SGAxis axis = (config == -1) ? this.getYAxis() : aElement.getAxisInPlane(config);
    final double v = (value != null) ? value.doubleValue() : this.getStartYValue();
    return axis.isValidValue(v);
  }

  /**
   * @param config
   * @param value
   */
  public boolean hasValidEndXValue(final int config, final Number value) {
    SGIFigureElementAxis aElement = this.getShapeElement().getAxisElement();
    final SGAxis axis = (config == -1) ? this.getXAxis() : aElement.getAxisInPlane(config);
    final double v = (value != null) ? value.doubleValue() : this.getEndXValue();
    return axis.isValidValue(v);
  }

  /**
   * @param config
   * @param value
   */
  public boolean hasValidEndYValue(final int config, final Number value) {
    SGIFigureElementAxis aElement = this.getShapeElement().getAxisElement();
    final SGAxis axis = (config == -1) ? this.mShape.getYAxis() : aElement.getAxisInPlane(config);
    final double v = (value != null) ? value.doubleValue() : this.getEndYValue();
    return axis.isValidValue(v);
  }

  /**
   * @param open
   * @param close
   */
  public boolean hasValidAngle(final Number open, final Number close) {
    final float openAngle = (open != null) ? open.floatValue() : this.getHeadOpenAngle();
    final float closeAngle = (close != null) ? close.floatValue() : this.getHeadCloseAngle();
    return (openAngle < closeAngle);
  }

  /** */
  public boolean setAxisValuesWithShape() {
    SGAxis xAxis = this.getXAxis();
    SGAxis yAxis = this.getYAxis();

    SGFigureElementShape sElement = this.getShapeElement();

    final double xValue1 = sElement.calcValue(this.getStartX(), xAxis, true);
    final double yValue1 = sElement.calcValue(this.getStartY(), yAxis, false);
    final double xValue2 = sElement.calcValue(this.getEndX(), xAxis, true);
    final double yValue2 = sElement.calcValue(this.getEndY(), yAxis, false);

    this.mStartXValue = SGUtilityNumber.getNumberInRangeOrder(xValue1, xAxis);
    this.mStartYValue = SGUtilityNumber.getNumberInRangeOrder(yValue1, yAxis);
    this.mEndXValue = SGUtilityNumber.getNumberInRangeOrder(xValue2, xAxis);
    this.mEndYValue = SGUtilityNumber.getNumberInRangeOrder(yValue2, yAxis);

    this.setShapeWithAxesValues();

    return true;
  }

  /** */
  public boolean setShapeWithAxesValues() {
    SGFigureElementShape.ShapeObject sh = this.mShape;
    SGAxis xAxis = this.getXAxis();
    SGAxis yAxis = this.getYAxis();

    SGFigureElementShape sElement = this.getShapeElement();

    final float x1 = sElement.calcLocation(this.mStartXValue, xAxis, true);
    final float y1 = sElement.calcLocation(this.mStartYValue, yAxis, false);
    final float x2 = sElement.calcLocation(this.mEndXValue, xAxis, true);
    final float y2 = sElement.calcLocation(this.mEndYValue, yAxis, false);

    if (Float.isNaN(x1) || Float.isNaN(y1) || Float.isNaN(x2) || Float.isNaN(y2)) {
      sh.setValid(false);
      return false;
    }
    sh.setValid(true);

    this.setStartX(x1);
    this.setStartY(y1);
    this.setEndX(x2);
    this.setEndY(y2);

    return true;
  }

  /** */
  public List<Point2D> getAnchorPointList() {
    ArrayList<Point2D> list = new ArrayList<Point2D>();

    Point2D ps = new Point2D.Float(this.getStartX(), this.getStartY());
    Point2D pe = new Point2D.Float(this.getEndX(), this.getEndY());

    list.add(ps);
    list.add(pe);

    return list;
  }

  /** */
  public Object copy() {
    SGShapeArrow el = new SGShapeArrow(owner);
    el.setShapeObject(this.mShape);
    el.setMagnification(this.getMagnification());
    el.setProperties(this.getProperties());
    el.setShapeWithAxesValues();
    return el;
  }

  /** */
  public boolean drag(MouseEvent e, Point pos, final int ml) {
    SGUtility.MouseDragResult result = owner.getMouseDragResult(e);
    final int diffX = result.dx;
    final int diffY = result.dy;
    pos.setLocation(pos.getX() + diffX, pos.getY() + diffY);

    if (ml == START) {
      this.setStartX(this.getStartX() + diffX);
      this.setStartY(this.getStartY() + diffY);
    } else if (ml == END) {
      this.setEndX(this.getEndX() + diffX);
      this.setEndY(this.getEndY() + diffY);
    }

    return true;
  }

  /** */
  public void translate(final float dx, final float dy) {
    this.translateSub(dx, dy);
  }

  public void translateSub(final float dx, final float dy) {
    this.setStartX(this.getStartX() + dx);
    this.setStartY(this.getStartY() + dy);
    this.setEndX(this.getEndX() + dx);
    this.setEndY(this.getEndY() + dy);

    this.setAxisValuesWithShape();
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
    sb.append(", START=(");
    sb.append(this.mStartXValue);
    sb.append(", ");
    sb.append(this.mStartYValue);
    sb.append("), END=(");
    sb.append(this.mEndXValue);
    sb.append(", ");
    sb.append(this.mEndYValue);
    sb.append(")");
    return sb.toString();
  }

  /** */
  public ArrayList<SGINode> getChildNodes() {
    return new ArrayList<SGINode>();
  }

  /** */
  public SGProperties getMemento() {
    return this.getProperties();
  }

  /** */
  public boolean setMemento(SGProperties p) {
    return this.setProperties(p);
  }

  /** */
  public SGProperties getProperties() {
    SGProperties p = new SGFigureElementShape.ArrowShapeProperties();
    if (this.getProperties(p) == false) {
      return null;
    }
    return p;
  }

  /** */
  public boolean getProperties(SGProperties p) {
    if ((p instanceof SGFigureElementShape.ArrowShapeProperties) == false) {
      return false;
    }
    if (super.getProperties(p) == false) {
      return false;
    }
    SGFigureElementShape.ArrowShapeProperties rp = (SGFigureElementShape.ArrowShapeProperties) p;
    SGFigureElementShape sElement = this.getShapeElement();
    SGIFigureElementAxis aElement = sElement.getAxisElement();
    SGAxis xAxis = this.getXAxis();
    SGAxis yAxis = this.getYAxis();
    rp.setXAxisLocation(aElement.getLocationInPlane(xAxis));
    rp.setYAxisLocation(aElement.getLocationInPlane(yAxis));
    rp.setStartXValue(this.mStartXValue);
    rp.setStartYValue(this.mStartYValue);
    rp.setEndXValue(this.mEndXValue);
    rp.setEndYValue(this.mEndYValue);
    rp.setAnchored(this.mIsAnchored);
    return true;
  }

  /** */
  public boolean setProperties(SGProperties p) {
    if ((p instanceof SGFigureElementShape.ArrowShapeProperties) == false) {
      return false;
    }
    if (super.setProperties(p) == false) {
      return false;
    }
    SGFigureElementShape.ArrowShapeProperties rp = (SGFigureElementShape.ArrowShapeProperties) p;

    final Double x1 = rp.getStartXValue();
    if (x1 == null) return false;
    this.mStartXValue = x1.doubleValue();

    final Double y1 = rp.getStartYValue();
    if (y1 == null) return false;
    this.mStartYValue = y1.doubleValue();

    final Double x2 = rp.getEndXValue();
    if (x2 == null) return false;
    this.mEndXValue = x2.doubleValue();

    final Double y2 = rp.getEndYValue();
    if (y2 == null) return false;
    this.mEndYValue = y2.doubleValue();

    final Boolean anchored = rp.getAnchored();
    this.mIsAnchored = anchored.booleanValue();

    return true;
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

  public boolean readProperty(final Element el) {
    if (super.readProperty(el) == false) {
      return false;
    }

    String str = null;
    Number num = null;

    // start x value
    str = el.getAttribute(KEY_START_X_VALUE);
    if (str.length() != 0) {
      num = SGUtilityText.getDouble(str);
      if (num == null) {
        return false;
      }
      final double startXValue = num.doubleValue();
      if (this.setStartXValue(startXValue) == false) {
        return false;
      }
    }

    // start y value
    str = el.getAttribute(KEY_START_Y_VALUE);
    if (str.length() != 0) {
      num = SGUtilityText.getDouble(str);
      if (num == null) {
        return false;
      }
      final double startYValue = num.doubleValue();
      if (this.setStartYValue(startYValue) == false) {
        return false;
      }
    }

    // end x value
    str = el.getAttribute(KEY_END_X_VALUE);
    if (str.length() != 0) {
      num = SGUtilityText.getDouble(str);
      if (num == null) {
        return false;
      }
      final double endXValue = num.doubleValue();
      if (this.setEndXValue(endXValue) == false) {
        return false;
      }
    }

    // end y value
    str = el.getAttribute(KEY_END_Y_VALUE);
    if (str.length() != 0) {
      num = SGUtilityText.getDouble(str);
      if (num == null) {
        return false;
      }
      final double endYValue = num.doubleValue();
      if (this.setEndYValue(endYValue) == false) {
        return false;
      }
    }

    // anchored
    str = el.getAttribute(KEY_ANCHORED);
    if (str.length() != 0) {
      final Boolean b = SGUtilityText.getBoolean(str);
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

      if (COM_ARROW_START_X.equalsIgnoreCase(key)) {
        Double num = SGUtilityText.getDouble(value);
        if (num == null) {
          result.putResult(COM_ARROW_START_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
          result.putResult(COM_ARROW_START_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setStartXValue(num.doubleValue()) == false) {
          result.putResult(COM_ARROW_START_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_ARROW_START_X, SGPropertyResults.SUCCEEDED);
      } else if (COM_ARROW_START_Y.equalsIgnoreCase(key)) {
        Double num = SGUtilityText.getDouble(value);
        if (num == null) {
          result.putResult(COM_ARROW_START_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
          result.putResult(COM_ARROW_START_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setStartYValue(num.doubleValue()) == false) {
          result.putResult(COM_ARROW_START_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_ARROW_START_Y, SGPropertyResults.SUCCEEDED);
      } else if (COM_ARROW_END_X.equalsIgnoreCase(key)) {
        Double num = SGUtilityText.getDouble(value);
        if (num == null) {
          result.putResult(COM_ARROW_END_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
          result.putResult(COM_ARROW_END_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setEndXValue(num.doubleValue()) == false) {
          result.putResult(COM_ARROW_END_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_ARROW_END_X, SGPropertyResults.SUCCEEDED);
      } else if (COM_ARROW_END_Y.equalsIgnoreCase(key)) {
        Double num = SGUtilityText.getDouble(value);
        if (num == null) {
          result.putResult(COM_ARROW_END_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
          result.putResult(COM_ARROW_END_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setEndYValue(num.doubleValue()) == false) {
          result.putResult(COM_ARROW_END_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_ARROW_END_Y, SGPropertyResults.SUCCEEDED);
      } else if (COM_ARROW_LINE_WIDTH.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_ARROW_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setLineWidth(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_ARROW_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_ARROW_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
      } else if (COM_ARROW_LINE_TYPE.equalsIgnoreCase(key)) {
        final Integer type = SGDrawingElementLine.getLineTypeFromName(value);
        if (type == null) {
          result.putResult(COM_ARROW_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setLineType(type) == false) {
          result.putResult(COM_ARROW_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_ARROW_LINE_TYPE, SGPropertyResults.SUCCEEDED);
      } else if (COM_ARROW_HEAD_SIZE.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_ARROW_HEAD_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setHeadSize(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_ARROW_HEAD_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_ARROW_HEAD_SIZE, SGPropertyResults.SUCCEEDED);
      } else if (COM_ARROW_COLOR.equalsIgnoreCase(key)) {
        Color cl = SGUtilityText.getColor(value);
        if (cl != null) {
          if (this.setColor(cl) == false) {
            result.putResult(COM_ARROW_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        } else {
          cl = SGUtilityText.parseColor(value);
          if (cl == null) {
            result.putResult(COM_ARROW_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (this.setColor(cl) == false) {
            result.putResult(COM_ARROW_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        }
        result.putResult(COM_ARROW_COLOR, SGPropertyResults.SUCCEEDED);
      } else if (COM_ARROW_START_TYPE.equalsIgnoreCase(key)) {
        final Integer type = SGDrawingElementArrow.getArrowHeadTypeFromName(value);
        if (type == null) {
          result.putResult(COM_ARROW_START_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setStartHeadType(type) == false) {
          result.putResult(COM_ARROW_START_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_ARROW_START_TYPE, SGPropertyResults.SUCCEEDED);
      } else if (COM_ARROW_END_TYPE.equalsIgnoreCase(key)) {
        final Integer type = SGDrawingElementArrow.getArrowHeadTypeFromName(value);
        if (type == null) {
          result.putResult(COM_ARROW_END_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setEndHeadType(type) == false) {
          result.putResult(COM_ARROW_END_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_ARROW_END_TYPE, SGPropertyResults.SUCCEEDED);
      } else if (COM_ARROW_HEAD_OPEN_ANGLE.equalsIgnoreCase(key)) {
        Float openAngle = SGUtilityText.getFloat(value);
        if (openAngle == null) {
          result.putResult(COM_ARROW_HEAD_OPEN_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setHeadAngle(openAngle, null) == false) {
          result.putResult(COM_ARROW_HEAD_OPEN_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_ARROW_HEAD_OPEN_ANGLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_ARROW_HEAD_CLOSE_ANGLE.equalsIgnoreCase(key)) {
        Float closeAngle = SGUtilityText.getFloat(value);
        if (closeAngle == null) {
          result.putResult(COM_ARROW_HEAD_CLOSE_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setHeadAngle(null, closeAngle) == false) {
          result.putResult(COM_ARROW_HEAD_CLOSE_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_ARROW_HEAD_CLOSE_ANGLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_ARROW_HEAD_ANGLE.equalsIgnoreCase(key)) {
        float[] angles = SGUtilityText.getFloatArray(value);
        if (angles == null) {
          result.putResult(COM_ARROW_HEAD_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (angles.length != 2) {
          result.putResult(COM_ARROW_HEAD_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setHeadAngle(angles[0], angles[1]) == false) {
          result.putResult(COM_ARROW_HEAD_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_ARROW_HEAD_ANGLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_ARROW_ANCHORED.equalsIgnoreCase(key)) {
        Boolean anchored = SGUtilityText.getBoolean(value);
        if (anchored == null) {
          result.putResult(COM_ARROW_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setAnchored(anchored) == false) {
          result.putResult(COM_ARROW_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_ARROW_ANCHORED, SGPropertyResults.SUCCEEDED);
      }
    }

    return result;
  }

  private float getExportHeadOpenAngle() {
    return SGUtility.getExportValue(this.getHeadOpenAngle(), ARROW_HEAD_ANGLE_MINIMAL_ORDER);
  }

  private float getExportHeadCloseAngle() {
    return SGUtility.getExportValue(this.getHeadCloseAngle(), ARROW_HEAD_ANGLE_MINIMAL_ORDER);
  }

  /**
   * Creates and returns the map of properties.
   *
   * @return the map of properties
   */
  @Override
  public SGPropertyMap getCommandPropertyMap() {
    SGPropertyMap map = new SGPropertyMap();

    // open angles
    StringBuilder sbAngle = new StringBuilder();
    sbAngle.append('(');
    sbAngle.append(this.getExportHeadOpenAngle());
    sbAngle.append(',');
    sbAngle.append(this.getExportHeadCloseAngle());
    sbAngle.append(')');
    SGPropertyUtility.addProperty(map, COM_ARROW_HEAD_ANGLE, sbAngle.toString());

    // properties for arrow shape
    this.addProperties(
        map,
        COM_ARROW_LINE_WIDTH,
        COM_ARROW_LINE_TYPE,
        COM_ARROW_HEAD_SIZE,
        COM_ARROW_COLOR,
        COM_ARROW_START_TYPE,
        COM_ARROW_END_TYPE);

    // properties related to the location
    this.addProperties(
        map,
        COM_ARROW_START_X,
        COM_ARROW_START_Y,
        COM_ARROW_END_X,
        COM_ARROW_END_Y,
        COM_ARROW_ANCHORED);

    return map;
  }

  @Override
  public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    SGPropertyMap map = super.getPropertyFileMap(params);

    // open angles
    SGPropertyUtility.addProperty(map, KEY_HEAD_OPEN_ANGLE, this.getExportHeadOpenAngle());
    SGPropertyUtility.addProperty(map, KEY_HEAD_CLOSE_ANGLE, this.getExportHeadCloseAngle());

    // properties related to the location
    this.addProperties(
        map, KEY_START_X_VALUE, KEY_START_Y_VALUE, KEY_END_X_VALUE, KEY_END_Y_VALUE, KEY_ANCHORED);
    return map;
  }

  private void addProperties(
      SGPropertyMap map,
      String startXKey,
      String startYKey,
      String endXKey,
      String endYKey,
      String anchorKey) {
    SGPropertyUtility.addProperty(map, startXKey, this.getStartXValue());
    SGPropertyUtility.addProperty(map, startYKey, this.getStartYValue());
    SGPropertyUtility.addProperty(map, endXKey, this.getEndXValue());
    SGPropertyUtility.addProperty(map, endYKey, this.getEndYValue());
    SGPropertyUtility.addProperty(map, anchorKey, this.isAnchored());
  }

  @Override
  public String getShapeType() {
    return SHAPE_TYPE_ARROW;
  }

  @Override
  public boolean getAxisDateMode(final int location) {
    return owner.mAxisElement.getAxisDateMode(location);
  }
}
