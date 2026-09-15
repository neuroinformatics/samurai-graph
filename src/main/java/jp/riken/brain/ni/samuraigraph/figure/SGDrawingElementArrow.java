package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGArrowConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGLineConstants.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import org.w3c.dom.Element;

/** Drawing element of arrow. */
public abstract class SGDrawingElementArrow extends SGDrawingElement {

  /** A line object. */
  protected SGDrawingElementLine mLine;

  /** A symbol object for the start head. */
  protected SGDrawingElementSymbol mStartHead;

  /** A symbol object for the end head. */
  protected SGDrawingElementSymbol mEndHead;

  /** Default constructor. */
  public SGDrawingElementArrow() {
    super();
  }

  /** Creates and returns an instance of the body. */
  protected abstract SGDrawingElementLine createBodyInstance();

  /**
   * Creates and returns an instance of the head.
   *
   * @param arrow an arrow that this head belongs to
   * @param start true for the arrow head at start
   */
  protected abstract SGDrawingElementSymbol createHeadInstance(
      SGDrawingElementArrow arrow, final boolean start);

  /** Returns the line of this arrow. */
  protected SGDrawingElementLine getLine() {
    return this.mLine;
  }

  /** Returns the symbol of the start. */
  protected SGDrawingElementSymbol getStartHead() {
    return this.mStartHead;
  }

  /** Returns the symbol of the end. */
  protected SGDrawingElementSymbol getEndHead() {
    return this.mEndHead;
  }

  /** Dispose this object. */
  public void dispose() {
    super.dispose();
    this.mLine.dispose();
    this.mStartHead.dispose();
    this.mEndHead.dispose();
    this.mLine = null;
    this.mStartHead = null;
    this.mEndHead = null;
  }

  @Override
  public boolean contains(final int x, final int y) {
    final boolean lineFlag = this.getLine().contains(x, y);
    if (lineFlag) {
      return true;
    }
    SGDrawingElementSymbol startHead = this.getStartHead();
    if (this.isVisibleHead(startHead)) {
      if (startHead.contains(x, y)) {
        return true;
      }
    }
    SGDrawingElementSymbol endHead = this.getEndHead();
    if (this.isVisibleHead(endHead)) {
      if (endHead.contains(x, y)) {
        return true;
      }
    }
    return false;
  }

  protected boolean isVisibleHead(SGDrawingElementSymbol head) {
    if (!head.isVisible()) {
      return false;
    }
    final int type = head.getType();
    if (type == -1) {
      return false;
    }
    return true;
  }

  /** Returns the color. */
  public abstract Color getColor();

  /** Returns the line stroke. */
  public abstract SGStroke getStroke();

  /** Returns the angle of this arrow. */
  /**
   * Sets the color.
   *
   * @param cl the color to set
   */
  public abstract boolean setColor(final Color cl);

  /**
   * Sets the coordinate of the start point.
   *
   * @param start the coordinate set to the start point
   */
  public boolean setStart(SGTuple2f start) {
    return this.setLocation(start, this.getEnd());
  }

  /**
   * Sets the x-coordinate of the start point.
   *
   * @param x the x-coordinate set to the start point
   */
  public abstract boolean setStartX(final float x);

  /**
   * Sets the y-coordinate of the start point.
   *
   * @param y the y-coordinate set to the start point
   */
  public abstract boolean setStartY(final float y);

  /**
   * Sets the coordinate of the end point.
   *
   * @param end the coordinate set to the end point
   */
  public boolean setEnd(SGTuple2f end) {
    return this.setLocation(this.getStart(), end);
  }

  /**
   * Sets the x-coordinate of the end point.
   *
   * @param x the x-coordinate set to the end point
   */
  public abstract boolean setEndX(final float x);

  /**
   * Sets the y-coordinate of the end point.
   *
   * @param y the y-coordinate set to the end point
   */
  public abstract boolean setEndY(final float y);

  /**
   * Sets the coordinates of the end points.
   *
   * @param start the coordinate set to the start point
   * @param end the coordinate set to the end point
   */
  public abstract boolean setLocation(final SGTuple2f start, final SGTuple2f end);

  /**
   * Sets the coordinates of the end points.
   *
   * @param x1 the x-coordinate set to the start point
   * @param y1 the y-coordinate set to the start point
   * @param x2 the x-coordinate set to the end point
   * @param y2 the y-coordinate set to the end point
   */
  public boolean setTermPoints(final float x1, final float y1, final float x2, final float y2) {
    return this.setLocation(new SGTuple2f(x1, y1), new SGTuple2f(x2, y2));
  }

  /** Returns the x-coordinate of the start point. */
  public float getStartX() {
    return this.getStart().x;
  }

  /** Returns the y-coordinate of the start point. */
  public float getStartY() {
    return this.getStart().y;
  }

  /** Returns the x-coordinate of the end point. */
  public float getEndX() {
    return this.getEnd().x;
  }

  /** Returns the y-coordinate of the end point. */
  public float getEndY() {
    return this.getEnd().y;
  }

  /** Returns the start point. */
  public abstract SGTuple2f getStart();

  /** Returns the end point. */
  public abstract SGTuple2f getEnd();

  /**
   * Sets the line width
   *
   * @param width line width to set
   */
  public abstract boolean setLineWidth(final float width);

  /**
   * Set the line width in a given unit.
   *
   * @param lw the line width to set
   * @param unit the unit for the given line width
   */
  public boolean setLineWidth(final float lw, final String unit) {
    final Float lwNew = SGUtility.getLineWidth(lw, unit);
    if (lwNew == null) {
      return false;
    }
    if (this.setLineWidth(lwNew) == false) {
      return false;
    }
    return true;
  }

  /**
   * Sets the line type.
   *
   * @param type the line type to set
   */
  public abstract boolean setLineType(final int type);

  /**
   * Sets the head size.
   *
   * @param size the head size to set
   */
  public abstract boolean setHeadSize(final float size);

  /**
   * Sets the size in a given unit.
   *
   * @param size the symbol size to set
   * @param unit the unit for given symbol size
   */
  public boolean setHeadSize(final float size, final String unit) {
    final Float sNew =
        SGUtility.calcPropertyValue(
            size,
            unit,
            ARROW_HEAD_SIZE_UNIT,
            ARROW_HEAD_SIZE_MIN,
            ARROW_HEAD_SIZE_MAX,
            ARROW_HEAD_SIZE_MINIMAL_ORDER);
    if (sNew == null) {
      return false;
    }
    if (this.setHeadSize(sNew) == false) {
      return false;
    }
    return true;
  }

  /**
   * Sets the start head type.
   *
   * @param type the start head type
   */
  public abstract boolean setStartHeadType(final int type);

  /**
   * Sets the end head type.
   *
   * @param type the end head type
   */
  public abstract boolean setEndHeadType(final int type);

  /** Returns the line width. */
  public abstract float getLineWidth();

  /** Returns the line type. */
  public abstract int getLineType();

  /** Returns the head size. */
  public abstract float getHeadSize();

  /**
   * Sets the open and close angle of the arrow head.
   *
   * @param openAngle a value to set to the open angle
   * @param closeAngle a value to set to the close angle
   */
  public abstract boolean setHeadAngle(final Float openAngle, final Float closeAngle);

  /** Returns the open angle of the arrow head. */
  public abstract float getHeadOpenAngle();

  /** Returns the close angle of the arrow head. */
  public abstract float getHeadCloseAngle();

  /** Returns the start head type. */
  public abstract int getStartHeadType();

  /** Returns the end head type. */
  public abstract int getEndHeadType();

  public float getLineWidth(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getLineWidth(), unit);
  }

  public float getHeadSize(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getHeadSize(), unit);
  }

  /** */
  public SGProperties getProperties() {
    ArrowProperties p = new ArrowProperties();
    if (this.getProperties(p) == false) return null;
    return p;
  }

  /** */
  public boolean getProperties(SGProperties p) {
    if ((p instanceof ArrowProperties) == false) {
      return false;
    }
    if (super.getProperties(p) == false) {
      return false;
    }
    ArrowProperties ap = (ArrowProperties) p;
    ap.setLineWidth(this.getLineWidth());
    ap.setLineType(this.getLineType());
    ap.setHeadSize(this.getHeadSize());
    ap.setHeadOpenAngle(this.getHeadOpenAngle());
    ap.setHeadCloseAngle(this.getHeadCloseAngle());
    ap.setStartHeadType(this.getStartHeadType());
    ap.setEndHeadType(this.getEndHeadType());
    ap.setColor(this.getColor());
    return true;
  }

  /**
   * Sets the properties of arrow object.
   *
   * @param p properties of an arrow
   */
  public boolean setProperties(final SGProperties p) {
    if ((p instanceof ArrowProperties) == false) {
      return false;
    }
    if (super.setProperties(p) == false) {
      return false;
    }
    ArrowProperties ap = (ArrowProperties) p;
    final Float lineWidth = ap.getLineWidth();
    if (lineWidth == null) {
      return false;
    }
    this.setLineWidth(lineWidth.floatValue());

    final Integer lineType = ap.getLineType();
    if (lineType == null) {
      return false;
    }
    this.setLineType(lineType.intValue());

    final Float headSize = ap.getHeadSize();
    if (headSize == null) {
      return false;
    }
    this.setHeadSize(headSize.floatValue());

    final Float openAngle = ap.getHeadOpenAngle();
    if (openAngle == null) {
      return false;
    }
    final Float closeAngle = ap.getHeadCloseAngle();
    if (closeAngle == null) {
      return false;
    }
    this.setHeadAngle(openAngle.floatValue(), closeAngle.floatValue());

    final Integer startHeadType = ap.getStartHeadType();
    if (startHeadType == null) {
      return false;
    }
    this.setStartHeadType(startHeadType.intValue());

    final Integer endHeadType = ap.getEndHeadType();
    if (endHeadType == null) {
      return false;
    }
    this.setEndHeadType(endHeadType.intValue());

    final Color cl = ap.getColor();
    if (cl == null) {
      return false;
    }
    this.setColor(cl);

    return true;
  }

  /**
   * Returns whether a given head type is valid.
   *
   * @param type a head type
   */
  public static boolean isValidArrowHeadType(final int type) {
    if (SGDrawingElementSymbol.isValidSymbolType(type)) {
      return true;
    }
    final int[] array = {
      SYMBOL_TYPE_VOID, SYMBOL_TYPE_TRANSVERSELINE, SYMBOL_TYPE_ARROW_HEAD, SYMBOL_TYPE_ARROW
    };
    for (int ii = 0; ii < array.length; ii++) {
      if (type == array[ii]) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns whether a given symbol type is of the line type.
   *
   * @param type a symbol type
   */
  public static boolean isLineTypeSymbol(final int type) {
    if (SGDrawingElementSymbol.isLineTypeSymbol(type)) {
      return true;
    }
    return (type == SYMBOL_TYPE_TRANSVERSELINE || type == SYMBOL_TYPE_ARROW);
  }

  /**
   * Returns the arrow head type constant from a given name.
   *
   * @param name the name of an arrow head type
   */
  public static Integer getArrowHeadTypeFromName(final String name) {
    Integer type = SGDrawingElementSymbol.getSymbolTypeFromName(name);
    if (type != null) {
      return type;
    } else {
      if (SGUtilityText.isEqualString(SYMBOL_NAME_VOID, name)) {
        type = SYMBOL_TYPE_VOID;
      } else if (SGUtilityText.isEqualString(SYMBOL_NAME_TRANSVERSE_LINE, name)) {
        type = SYMBOL_TYPE_TRANSVERSELINE;
      } else if (SGUtilityText.isEqualString(SYMBOL_NAME_ARROW_HEAD, name)) {
        type = SYMBOL_TYPE_ARROW_HEAD;
      } else if (SGUtilityText.isEqualString(SYMBOL_NAME_ARROW, name)) {
        type = SYMBOL_TYPE_ARROW;
      }
    }
    return type;
  }

  /**
   * Returns the name of a given arrow head type.
   *
   * @param type the arrow head type
   */
  public static String getArrowHeadTypeName(final int type) {
    String name = SGDrawingElementSymbol.getSymbolTypeName(type);
    if (name == null) {
      switch (type) {
        case SYMBOL_TYPE_VOID:
          name = SYMBOL_NAME_VOID;
          break;
        case SYMBOL_TYPE_TRANSVERSELINE:
          name = SYMBOL_NAME_TRANSVERSE_LINE;
          break;
        case SYMBOL_TYPE_ARROW_HEAD:
          name = SYMBOL_NAME_ARROW_HEAD;
          break;
        case SYMBOL_TYPE_ARROW:
          name = SYMBOL_NAME_ARROW;
          break;
        default:
      }
    }
    return name;
  }

  public boolean writeProperty(final Element el, SGExportParameter params) {
    SGPropertyMap map = this.getPropertyFileMap(params);
    map.setToElement(el);
    return true;
  }

  public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    SGPropertyMap map = new SGPropertyMap();
    this.addProperties(
        map,
        SGLineConstants.KEY_LINE_WIDTH,
        KEY_LINE_TYPE,
        KEY_HEAD_SIZE,
        KEY_COLOR,
        KEY_START_HEAD_TYPE,
        KEY_END_HEAD_TYPE);
    return map;
  }

  protected void addProperties(
      SGPropertyMap map,
      String lineWidthKey,
      String lineTypeKey,
      String headSizeKey,
      String colorKey,
      String startTypeKey,
      String endTypeKey) {
    SGPropertyUtility.addProperty(
        map,
        lineWidthKey,
        SGUtility.getExportLineWidth(this.getLineWidth(LINE_WIDTH_UNIT)),
        LINE_WIDTH_UNIT);
    SGPropertyUtility.addProperty(
        map, lineTypeKey, SGDrawingElementLine.getLineTypeName(this.getLineType()));
    SGPropertyUtility.addProperty(
        map,
        headSizeKey,
        SGUtility.getExportValue(
            this.getHeadSize(ARROW_HEAD_SIZE_UNIT), ARROW_HEAD_SIZE_MINIMAL_ORDER),
        ARROW_HEAD_SIZE_UNIT);
    SGPropertyUtility.addProperty(map, colorKey, this.getColor());
    SGPropertyUtility.addProperty(
        map, startTypeKey, SGDrawingElementArrow.getArrowHeadTypeName(this.getStartHeadType()));
    SGPropertyUtility.addProperty(
        map, endTypeKey, SGDrawingElementArrow.getArrowHeadTypeName(this.getEndHeadType()));
  }

  /**
   * Reads the properties from an element.
   *
   * @param el an element
   */
  public boolean readProperty(final Element el) {

    String str = null;
    Number num = null;

    // line width
    str = el.getAttribute(SGLineConstants.KEY_LINE_WIDTH);
    if (str.length() != 0) {
      StringBuilder uLineWidth = new StringBuilder();
      num = SGUtilityText.getNumber(str, uLineWidth);
      if (num == null) {
        return false;
      }
      final float lineWidth = num.floatValue();
      if (this.setLineWidth(lineWidth, uLineWidth.toString()) == false) {
        return false;
      }
    }

    // line type
    str = el.getAttribute(KEY_LINE_TYPE);
    if (str.length() != 0) {
      num = SGDrawingElementLine.getLineTypeFromName(str);
      if (num == null) {
        return false;
      }
      final int lineType = num.intValue();
      if (this.setLineType(lineType) == false) {
        return false;
      }
    }

    // head size
    str = el.getAttribute(KEY_HEAD_SIZE);
    if (str.length() != 0) {
      StringBuilder uHeadSize = new StringBuilder();
      num = SGUtilityText.getNumber(str, uHeadSize);
      if (num == null) {
        return false;
      }
      final float headSize = num.floatValue();
      if (this.setHeadSize(headSize, uHeadSize.toString()) == false) {
        return false;
      }
    }

    // start head type
    str = el.getAttribute(KEY_START_HEAD_TYPE);
    if (str.length() != 0) {
      num = SGDrawingElementArrow.getArrowHeadTypeFromName(str);
      if (num == null) {
        return false;
      }
      final int startHeadType = num.intValue();
      if (this.setStartHeadType(startHeadType) == false) {
        return false;
      }
    }

    // end head type
    str = el.getAttribute(KEY_END_HEAD_TYPE);
    if (str.length() != 0) {
      num = SGDrawingElementArrow.getArrowHeadTypeFromName(str);
      if (num == null) {
        return false;
      }
      final int endHeadType = num.intValue();
      if (this.setEndHeadType(endHeadType) == false) {
        return false;
      }
    }

    // color
    str = el.getAttribute(KEY_COLOR);
    if (str.length() != 0) {
      Color cl = SGUtilityText.parseColorIncludingList(str);
      if (cl == null) {
        return false;
      }
      if (this.setColor(cl) == false) {
        return false;
      }
    }

    // open angle
    str = el.getAttribute(KEY_HEAD_OPEN_ANGLE);
    if (str.length() != 0) {
      num = SGUtilityText.getFloat(str, degree);
      if (num == null) {
        return false;
      }
      final float openAngle = num.floatValue();

      // close angle
      str = el.getAttribute(KEY_HEAD_CLOSE_ANGLE);
      if (str.length() != 0) {
        num = SGUtilityText.getFloat(str, degree);
        if (num == null) {
          return false;
        }
        final float closeAngle = num.floatValue();
        if (this.setHeadAngle(openAngle, closeAngle) == false) {
          return false;
        }
      }
    }

    return true;
  }

  public static class ArrowProperties extends DrawingElementProperties {

    private SGDrawingElementLine.LineProperties mLineProperties =
        new SGDrawingElementLine.LineProperties();

    private SGDrawingElementSymbol.SymbolProperties mSymbolProperties =
        new SGDrawingElementSymbol.SymbolProperties();

    private int mEndHeadType;

    private double mHeadOpenAngle;

    private double mHeadCloseAngle;

    private Color mColor = null;

    /** */
    public boolean equals(Object obj) {
      if ((obj instanceof ArrowProperties) == false) return false;
      if (super.equals(obj) == false) return false;

      ArrowProperties p = (ArrowProperties) obj;

      if (this.mLineProperties.equals(p.mLineProperties) == false) {
        return false;
      }
      if (this.mSymbolProperties.equals(p.mSymbolProperties) == false) {
        return false;
      }
      if (this.mEndHeadType != p.mEndHeadType) {
        return false;
      }
      if (this.mHeadOpenAngle != p.mHeadOpenAngle) {
        return false;
      }
      if (this.mHeadCloseAngle != p.mHeadCloseAngle) {
        return false;
      }
      if (SGUtility.equals(this.mColor, p.mColor) == false) {
        return false;
      }

      return true;
    }

    public Float getLineWidth() {
      return this.mLineProperties.getLineWidth();
    }

    public Integer getLineType() {
      return this.mLineProperties.getLineType();
    }

    public Float getHeadSize() {
      return this.mSymbolProperties.getSize();
    }

    public Integer getStartHeadType() {
      return this.mSymbolProperties.getSymbolType();
    }

    public Integer getEndHeadType() {
      return Integer.valueOf(this.mEndHeadType);
    }

    public Float getHeadOpenAngle() {
      return Float.valueOf((float) this.mHeadOpenAngle);
    }

    public Float getHeadCloseAngle() {
      return Float.valueOf((float) this.mHeadCloseAngle);
    }

    public Color getColor() {
      return this.mColor;
    }

    public boolean setLineWidth(final float width) {
      if (this.mLineProperties.setLineWidth(width) == false) {
        return false;
      }
      if (this.mSymbolProperties.setLineWidth(width) == false) {
        return false;
      }
      return true;
    }

    public boolean setLineType(final int type) {
      return this.mLineProperties.setLineType(type);
    }

    public boolean setStartHeadType(final int num) {
      return this.mSymbolProperties.setSymbolType(num);
    }

    public boolean setEndHeadType(final int num) {
      this.mEndHeadType = num;
      return true;
    }

    public boolean setHeadSize(final float size) {
      return this.mSymbolProperties.setSize(size);
    }

    public boolean setColor(final Color cl) {
      this.mColor = cl;
      return true;
    }

    public boolean setHeadOpenAngle(final float value) {
      this.mHeadOpenAngle = value;
      return true;
    }

    public boolean setHeadCloseAngle(final float value) {
      this.mHeadCloseAngle = value;
      return true;
    }
  }

  /** The default constructor. */

  /** */
  public Rectangle2D getElementBounds() {
    SGDrawingElementLine line = (SGDrawingElementLine) this.getLine();
    SGDrawingElementSymbol start = (SGDrawingElementSymbol) this.getStartHead();
    SGDrawingElementSymbol end = (SGDrawingElementSymbol) this.getEndHead();
    ArrayList<Rectangle2D> list = new ArrayList<Rectangle2D>();
    list.add(line.getElementBounds());
    list.add(start.getElementBounds());
    list.add(end.getElementBounds());
    Rectangle2D rect = SGUtility.createUnion(list);
    return rect;
  }

  /**
   * Paint this object.
   *
   * @param g2d graphics context
   */
  public void paint(Graphics2D g2d) {
    if (this.isVisible() == false) {
      return;
    }
    if (this.getMagnitude() == 0.0f) {
      this.paintZeroLengthArrow(g2d, this.getStart());
      return;
    }
    final SGDrawingElementLine line = (SGDrawingElementLine) this.getLine();
    final SGDrawingElementSymbol start = (SGDrawingElementSymbol) this.getStartHead();
    final SGDrawingElementSymbol end = (SGDrawingElementSymbol) this.getEndHead();
    line.paint(g2d);
    start.paint(g2d);
    end.paint(g2d);
  }

  /**
   * Paint this object with given clipping rectangle.
   *
   * @param g2d graphics context
   * @param clipRect clipping rectangle
   */
  public void paint(Graphics2D g2d, Rectangle2D clipRect) {
    if (this.isVisible() == false) {
      return;
    }
    if (this.getMagnitude() == 0.0f) {
      this.paintZeroLengthArrow(g2d, this.getStart());
      return;
    }
    final SGDrawingElementLine line = (SGDrawingElementLine) this.getLine();
    final SGDrawingElementSymbol start = (SGDrawingElementSymbol) this.getStartHead();
    final SGDrawingElementSymbol end = (SGDrawingElementSymbol) this.getEndHead();
    line.paint(g2d, clipRect);
    start.paint(g2d, clipRect);
    end.paint(g2d, clipRect);
  }

  private void paintZeroLengthArrow(Graphics2D g2d, SGTuple2f point) {
    final float lw = this.getMagnification() * this.getLineWidth();
    final float lwHalf = lw / 2;
    final float x = point.x - lwHalf;
    final float y = point.y - lwHalf;
    Shape circle = new Ellipse2D.Float(x, y, lw, lw);
    g2d.setColor(this.getColor());
    g2d.draw(circle);
  }

  /** Returns the magnitude of this arrow. */
  public float getMagnitude() {
    final SGTuple2f start = this.getStart();
    final SGTuple2f end = this.getEnd();
    final float x = start.x - end.x;
    final float y = start.y - end.y;
    return (float) Math.sqrt(x * x + y * y);
  }

  /** Returns the gradient of this arrow. */
  public float getGradient() {
    final SGTuple2f start = this.getStart();
    final SGTuple2f end = this.getEnd();
    return SGUtilityForFigureElementJava2D.getGradient(start.x, start.y, end.x, end.y);
  }

  protected void updateHeadAngle() {
    final SGDrawingElementLine line = (SGDrawingElementLine) this.getLine();
    final SGDrawingElementSymbol start = (SGDrawingElementSymbol) this.getStartHead();
    final SGDrawingElementSymbol end = (SGDrawingElementSymbol) this.getEndHead();
    final float pi = (float) Math.PI;
    final float angle = line.getGradient();
    start.setAngle(angle - 0.50f * pi);
    end.setAngle(angle + 0.50f * pi);
  }

  /** Returns the shape of the start head. */
  protected abstract Shape getStartHeadShape();

  /** Returns the shape of the start head. */
  protected abstract Shape getEndHeadShape();

  /** Updates the head shape. */
  protected abstract void updateHeadShape();

  /** An inner class for arrow body. */
  protected static class ArrowBody extends SGDrawingElementLine {

    /** The arrow. */
    protected SGDrawingElementArrow mArrow = null;

    /** The default constructor. */
    public ArrowBody(final SGDrawingElementArrow arrow) {
      super();
      this.mArrow = arrow;
    }

    /** Returns a line object. */
    public Shape getLineShape() {
      final float yStart, yEnd;
      final int aType = SYMBOL_TYPE_ARROW_HEAD;
      final boolean startArrow = (this.mArrow.getStartHeadType() == aType);
      final boolean endArrow = (this.mArrow.getEndHeadType() == aType);
      final float len = this.getMagnitude();
      if (startArrow || endArrow) {
        final float open = this.mArrow.getHeadOpenAngle() * RADIAN_DEGREE_RATIO;
        final float close = this.mArrow.getHeadCloseAngle() * RADIAN_DEGREE_RATIO;
        final float tanOpen = (float) Math.tan(open);
        final float tanClose = (float) Math.tan(close);
        final float diff =
            (open <= close)
                ? this.getMagnification() * this.mArrow.getHeadSize() * (1.0f - tanOpen / tanClose)
                : 0.0f;
        yStart = startArrow ? 0.50f * diff : 0.0f;
        yEnd = endArrow ? (len - 0.50f * diff) : len;
      } else {
        yStart = 0.0f;
        yEnd = len;
      }
      Line2D line = new Line2D.Float(0.0f, yStart, 0.0f, yEnd);
      Shape shape = this.getAffineTransform().createTransformedShape(line);
      return shape;
    }

    /** Returns an affine transform. */
    private AffineTransform getAffineTransform() {
      AffineTransform af = new AffineTransform();

      // translate
      SGTuple2f start = this.getStart();
      af.translate(start.x, start.y);

      // rotate
      final double angle = this.getGradient() - 0.50 * Math.PI;
      af.rotate(angle);

      return af;
    }

    @Override
    public Color getColor() {
      return this.mArrow.getColor();
    }

    @Override
    protected SGStroke getStroke() {
      return this.mArrow.getStroke();
    }

    @Override
    public boolean setColor(Color cl) {
      // do nothing
      return true;
    }

    @Override
    public boolean setLineType(int type) {
      // do nothing
      return true;
    }

    @Override
    public boolean setLineWidth(float width) {
      // do nothing
      return true;
    }

    @Override
    public float getMagnification() {
      return this.mArrow.getMagnification();
    }

    @Override
    public boolean setMagnification(float mag) {
      // do nothing
      return true;
    }

    @Override
    public SGTuple2f getEnd() {
      return this.mArrow.getEnd();
    }

    @Override
    public SGTuple2f getStart() {
      return this.mArrow.getStart();
    }

    @Override
    public boolean setTermPoints(SGTuple2f start, SGTuple2f end) {
      // do nothing
      return true;
    }
  }

  /** An inner class for arrow head. */
  protected static class ArrowHead extends SGDrawingElementSymbol {

    /** The arrow. */
    protected SGDrawingElementArrow mArrow = null;

    /** A flag whether this arrow head is on the head of the arrow. */
    protected boolean mStartFlag = true;

    /**
     * Builds this object.
     *
     * @param arrow an arrow that this arrow head belongs to
     * @param start true for the head of the arrow
     */
    public ArrowHead(final SGDrawingElementArrow arrow, final boolean start) {
      super();
      this.mArrow = arrow;
      this.mStartFlag = start;
    }

    /** Disposes this object. */
    public void dispose() {
      super.dispose();
      this.mArrow = null;
    }

    /** Overrode not to draw line in the symbol with finite area. */
    protected void paintLine(Graphics2D g2d, Shape sh) {
      if (SGDrawingElementArrow.isLineTypeSymbol(this.getType())) {
        super.paintLine(g2d, sh);
      }
    }

    /** Overrode not to draw line in the symbol with finite area. */
    protected void paintLine(Graphics2D g2d, Area clipArea, Shape sh) {
      if (SGDrawingElementArrow.isLineTypeSymbol(this.getType())) {
        super.paintLine(g2d, clipArea, sh);
      }
    }

    protected float getHeadOpenAngle() {
      return this.mArrow.getHeadOpenAngle();
    }

    protected float getHeadCloseAngle() {
      return this.mArrow.getHeadCloseAngle();
    }

    /** Creates a shape. */
    protected Shape createShape() {
      final int type = this.getType();
      final float headSize = this.getMagnification() * this.getSize();
      final float open = this.getHeadOpenAngle();
      final float close = this.getHeadCloseAngle();
      return SGDrawingElementArrow.createHeadShape(type, headSize, open, close);
    }

    /**
     * Paint inside the arrow head.
     *
     * @param g2d The graphics context.
     * @param shape Shape object of this arrow head.
     */
    protected void paintInner(Graphics2D g2d, Shape shape) {
      if (!SGDrawingElementArrow.isLineTypeSymbol(this.getType())) {
        g2d.setPaint(this.getInnerColor());
        g2d.fill(shape);
      }
    }

    @Override
    public float getAngle() {
      final float angle = this.mArrow.getGradient();
      final float shift = (float) Math.PI / 2;
      if (this.mStartFlag) {
        return angle - shift;
      } else {
        return angle + shift;
      }
    }

    public Color getInnerColor() {
      return this.mArrow.getColor();
    }

    /**
     * @return null
     */
    @Override
    public SGIPaint getInnerPaint() {
      return null;
    }

    @Override
    public Color getLineColor() {
      return this.mArrow.getColor();
    }

    @Override
    public float getLineWidth() {
      return this.mArrow.getLineWidth();
    }

    @Override
    public boolean isLineVisible() {
      // always true
      return true;
    }

    @Override
    public float getSize() {
      return this.mArrow.getHeadSize();
    }

    @Override
    public int getType() {
      if (this.mStartFlag) {
        return this.mArrow.getStartHeadType();
      } else {
        return this.mArrow.getEndHeadType();
      }
    }

    @Override
    public boolean setAngle(float angle) {
      // do nothing
      return true;
    }

    @Override
    public boolean setInnerColor(Color color) {
      // do nothing
      return true;
    }

    @Override
    public boolean setLineColor(Color color) {
      // do nothing
      return true;
    }

    @Override
    public boolean setLineWidth(float lineWidth) {
      // do nothing
      return true;
    }

    @Override
    public boolean setLineVisible(boolean visible) {
      // do nothing
      return true;
    }

    @Override
    public boolean setSize(float size) {
      // do nothing
      return true;
    }

    @Override
    public boolean setType(int type) {
      // do nothing
      return true;
    }

    @Override
    public float getMagnification() {
      return this.mArrow.getMagnification();
    }

    @Override
    public boolean setMagnification(float mag) {
      // do nothing
      return true;
    }

    @Override
    public SGTuple2f getLocation() {
      if (this.mStartFlag) {
        return this.mArrow.getStart();
      } else {
        return this.mArrow.getEnd();
      }
    }

    public float getX() {
      return this.getLocation().x;
    }

    public float getY() {
      return this.getLocation().y;
    }

    @Override
    public boolean setLocation(float x, float y) {
      // do nothing
      return true;
    }

    @Override
    public boolean setLocation(SGTuple2f point) {
      // do nothing
      return true;
    }

    @Override
    public boolean setX(float x) {
      // do nothing
      return true;
    }

    @Override
    public boolean setY(float y) {
      // do nothing
      return true;
    }

    @Override
    protected Shape getShape() {
      if (this.mStartFlag) {
        return ((SGDrawingElementArrow) this.mArrow).getStartHeadShape();
      } else {
        return ((SGDrawingElementArrow) this.mArrow).getEndHeadShape();
      }
    }

    @Override
    protected void updateShape() {
      // do nothing
    }
  }

  /**
   * Creates a shape.
   *
   * @param type the symbol type
   * @param headSize the head size
   * @param open the open angle in units of degree
   * @param close the close angle in units of degree
   * @return a shape object
   */
  protected static Shape createHeadShape(
      final int type, final float headSize, final float open, final float close) {
    Shape sh = null;

    // arrow head
    if (type == SYMBOL_TYPE_ARROW_HEAD) {
      if (close <= open) {
        return null;
      }
      final float tanOpen = (float) Math.tan(open * RADIAN_DEGREE_RATIO);
      final float tanClose = (float) Math.tan(close * RADIAN_DEGREE_RATIO);
      final float openSize = headSize * tanOpen;
      Point2D[] pointArray = new Point2D[4];
      pointArray[0] = new Point2D.Float(0, 0);
      pointArray[1] = new Point2D.Float(openSize, headSize);
      pointArray[2] = new Point2D.Float(0, headSize - openSize / tanClose);
      pointArray[3] = new Point2D.Float(-openSize, headSize);
      Shape[] pathArray = new Line2D[pointArray.length];
      for (int ii = 0; ii < pathArray.length; ii++) {
        pathArray[ii] = new Line2D.Float(pointArray[ii], pointArray[(ii + 1) % pointArray.length]);
      }
      GeneralPath gp = new GeneralPath();
      for (int ii = 0; ii < pathArray.length; ii++) {
        gp.append(pathArray[ii], true);
      }
      sh = gp;
    } else if (type == SYMBOL_TYPE_ARROW) {
      Path2D gp = new Path2D.Float();
      final float tanOpen = (float) Math.tan(open * RADIAN_DEGREE_RATIO);
      final float openSize = headSize * tanOpen;
      gp.moveTo(openSize, headSize);
      gp.lineTo(0, 0);
      gp.lineTo(-openSize, headSize);
      sh = gp;
    } else if (type == SYMBOL_TYPE_TRANSVERSELINE) {
      final float half = 0.50f * headSize;
      GeneralPath gp = new GeneralPath();
      gp.moveTo(-half, 0);
      gp.lineTo(0, 0);
      gp.lineTo(half, 0);
      sh = gp;
    } else if (type == SYMBOL_TYPE_VOID) {
      // do nothing
    } else {
      sh = SGDrawingElementSymbol.createShape(type, headSize);
    }
    return sh;
  }

  protected boolean contains(
      final int x, final int y, SGTuple2f startLocation, SGTuple2f endLocation) {
    SGDrawingElementLine line = (SGDrawingElementLine) this.getLine();
    if (line.contains(x, y, startLocation, endLocation)) {
      return true;
    }
    SGDrawingElementSymbol startHead = (SGDrawingElementSymbol) this.getStartHead();
    if (this.isVisibleHead(startHead)) {
      if (startHead.contains(x, y, startLocation)) {
        return true;
      }
    }
    SGDrawingElementSymbol endHead = (SGDrawingElementSymbol) this.getEndHead();
    if (this.isVisibleHead(endHead)) {
      if (endHead.contains(x, y, endLocation)) {
        return true;
      }
    }
    return false;
  }
}
