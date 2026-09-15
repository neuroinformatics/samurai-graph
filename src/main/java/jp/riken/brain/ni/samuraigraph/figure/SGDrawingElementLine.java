package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.figure.SGLineConstants.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/** Drawing element of the line. */
public abstract class SGDrawingElementLine extends SGDrawingElement
    implements SGIDrawingElementJava2D {

  /** Returns a stroke. */
  protected abstract SGStroke getStroke();

  /** Construct a new line object. */
  public SGDrawingElementLine() {
    super();
  }

  /**
   * Construct a new line object with given start and end points.
   *
   * @param start coordinate of the start point
   * @param end coordinate of the end point
   */
  public SGDrawingElementLine(final SGTuple2f start, final SGTuple2f end) {
    super();
    this.setTermPoints(start, end);
  }

  /**
   * Construct a new line object with given start and end points.
   *
   * @param x1 x coordinate of the start point
   * @param y1 y coordinate of the start point
   * @param x2 x coordinate of the end point
   * @param y2 y coordinate of the end point
   */
  public SGDrawingElementLine(final float x1, final float y1, final float x2, final float y2) {
    super();
    this.setTermPoints(x1, y1, x2, y2);
  }

  /**
   * Set coordinates of the end points of this line.
   *
   * @param x1 x coordinate of the start point
   * @param y1 y coordinate of the start point
   * @param x2 x coordinate of the end point
   * @param y2 y coordinate of the end point
   */
  public boolean setTermPoints(final float x1, final float y1, final float x2, final float y2) {
    return this.setTermPoints(new SGTuple2f(x1, y1), new SGTuple2f(x2, y2));
  }

  /**
   * Set coordinates of the end points of this line.
   *
   * @param start coordinate of the start point
   * @param end coordinate of the end point
   */
  public abstract boolean setTermPoints(final SGTuple2f start, final SGTuple2f end);

  /**
   * Sets the line width.
   *
   * @param width line width to set
   */
  public abstract boolean setLineWidth(final float width);

  /**
   * Sets the line type.
   *
   * @param type line type
   */
  public abstract boolean setLineType(final int type);

  /**
   * Set the type of cap of this line.
   *
   * @param cap cap type of this line
   */
  public boolean setCap(final int cap) {
    this.getStroke().setEndCap(cap);
    return true;
  }

  /** Returns the coordinates of the start point of this line. */
  public abstract SGTuple2f getStart();

  /** Returns the coordinates of the end point of this line. */
  public abstract SGTuple2f getEnd();

  /** Returns type of this line. */
  public int getLineType() {
    return this.getStroke().getLineType();
  }

  /** Returns width of this line. */
  public float getLineWidth() {
    return this.getStroke().getLineWidth();
  }

  /** Returns the color. */
  public abstract Color getColor();

  /**
   * Sets the color.
   *
   * @param cl the color to set
   */
  public abstract boolean setColor(final Color cl);

  /** Returns the cap type of this line. */
  public int getCap() {
    return this.getStroke().getEndCap();
  }

  /** */
  public float getMagnitude() {
    SGTuple2f start = this.getStart();
    SGTuple2f end = this.getEnd();
    final float xDiff = end.x - start.x;
    final float yDiff = end.y - start.y;
    final float mag = (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    return mag;
  }

  /**
   * Returns whether a given line type is valid.
   *
   * @param type a line type
   */
  public static boolean isValidLineType(final int type) {
    final int[] array = {
      LINE_TYPE_SOLID, LINE_TYPE_BROKEN, LINE_TYPE_DOTTED, LINE_TYPE_DASHED, LINE_TYPE_DOUBLE_DASHED
    };
    for (int ii = 0; ii < array.length; ii++) {
      if (type == array[ii]) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the line type constant from a given name.
   *
   * @param name the name of line type
   */
  public static Integer getLineTypeFromName(final String name) {
    if (name == null) {
      return null;
    }
    int type;
    if (SGUtilityText.isEqualString(LINE_NAME_SOLID, name)) {
      type = LINE_TYPE_SOLID;
    } else if (SGUtilityText.isEqualString(LINE_NAME_BROKEN, name)) {
      type = LINE_TYPE_BROKEN;
    } else if (SGUtilityText.isEqualString(LINE_NAME_DOTTED, name)) {
      type = LINE_TYPE_DOTTED;
    } else if (SGUtilityText.isEqualString(LINE_NAME_DASHED, name)) {
      type = LINE_TYPE_DASHED;
    } else if (SGUtilityText.isEqualString(LINE_NAME_DOUBLE_DASHED, name)) {
      type = LINE_TYPE_DOUBLE_DASHED;
    } else {
      return null;
    }
    return Integer.valueOf(type);
  }

  /**
   * Returns the name of a given line type.
   *
   * @param type the line type
   */
  public static String getLineTypeName(final int type) {

    String name = null;
    switch (type) {
      case LINE_TYPE_SOLID:
        name = LINE_NAME_SOLID;
        break;
      case LINE_TYPE_BROKEN:
        name = LINE_NAME_BROKEN;
        break;
      case LINE_TYPE_DOTTED:
        name = LINE_NAME_DOTTED;
        break;
      case LINE_TYPE_DASHED:
        name = LINE_NAME_DASHED;
        break;
      case LINE_TYPE_DOUBLE_DASHED:
        name = LINE_NAME_DOUBLE_DASHED;
        break;
      default:
    }

    return name;
  }

  /** */
  public boolean setProperties(SGProperties p) {

    if ((p instanceof LineProperties) == false) {
      return false;
    }
    if (super.setProperties(p) == false) {
      return false;
    }

    LineProperties lp = (LineProperties) p;
    Float width = lp.getLineWidth();
    if (width == null) {
      return false;
    }
    Integer type = lp.getLineType();
    if (type == null) {
      return false;
    }
    Color cl = lp.getColor();
    if (cl == null) {
      return false;
    }

    this.setLineWidth(width.floatValue());
    this.setLineType(type.intValue());
    this.setColor(cl);

    return true;
  }

  /** */
  public SGProperties getProperties() {
    LineProperties p = new LineProperties();
    if (this.getProperties(p) == false) {
      return null;
    }
    return p;
  }

  /** */
  public boolean getProperties(SGProperties p) {
    if (p == null) {
      return false;
    }
    if ((p instanceof LineProperties) == false) {
      return false;
    }
    if (super.getProperties(p) == false) {
      return false;
    }

    LineProperties lp = (LineProperties) p;
    lp.setLineWidth(this.getLineWidth());
    lp.setLineType(this.getLineType());
    lp.setColor(this.getColor());

    return true;
  }

  /** */
  public static class LineProperties extends DrawingElementProperties {

    private float mLineWidth = 0.0f;

    private int mLineType = -1;

    private Color mColor = null;

    /** */
    public LineProperties() {
      super();
    }

    /** */
    public boolean equals(final Object obj) {
      if ((obj instanceof LineProperties) == false) {
        return false;
      }
      if (super.equals(obj) == false) {
        return false;
      }
      LineProperties p = (LineProperties) obj;
      if (p.mLineWidth != this.mLineWidth) {
        return false;
      }
      if (p.mLineType != this.mLineType) {
        return false;
      }
      if (SGUtility.equals(p.mColor, this.mColor) == false) {
        return false;
      }
      return true;
    }

    public Float getLineWidth() {
      return Float.valueOf(this.mLineWidth);
    }

    public Integer getLineType() {
      return Integer.valueOf(this.mLineType);
    }

    public Color getColor() {
      return this.mColor;
    }

    public boolean setLineWidth(final float w) {
      this.mLineWidth = w;
      return true;
    }

    public boolean setLineType(final int num) {
      this.mLineType = num;
      return true;
    }

    public boolean setColor(final Color color) {
      this.mColor = color;
      return true;
    }
  }

  /** Construct a new line object. */

  /** Construct a new line object with given start and end points. */

  /** Construct a new line object with given start and end points. */

  /** The minimum line width used in "contains" method. */
  public static final float MINIMUM_LINE_WIDTH = 2.0f;

  /**
   * Returns whether this line object "contains" a given point.
   *
   * @param x x coordinate of the point
   * @param y y coordinate of the point
   */
  public boolean contains(final int x, final int y) {
    SGTuple2f start = this.getStart();
    SGTuple2f end = this.getEnd();
    return this.contains(x, y, start, end);
  }

  protected boolean contains(final int x, final int y, SGTuple2f start, SGTuple2f end) {
    final float lineWidth = this.getLineWidth() * this.getMagnification();
    return contains(x, y, start, end, lineWidth);
  }

  public static boolean contains(
      final int x, final int y, SGTuple2f start, SGTuple2f end, final float lineWidth) {
    float lw = lineWidth;
    if (lw < MINIMUM_LINE_WIDTH) {
      lw = MINIMUM_LINE_WIDTH;
    }
    final double lenSq = Line2D.ptSegDistSq(start.x, start.y, end.x, end.y, x, y);
    return (lenSq < lw * lw);
  }

  /**
   * Paint this object.
   *
   * @param g2d graphics context
   */
  public void paint(final Graphics2D g2d) {
    if (this.isVisible() == false) {
      return;
    }

    // set color
    g2d.setPaint(this.getColor());

    // set stroke
    Stroke stroke = this.getBasicStroke();
    g2d.setStroke(stroke);

    // draw the line shape
    g2d.draw(this.getLineShape());
  }

  /**
   * Paint this object with given clipping rectangle.
   *
   * @param g2d graphics context
   * @param clipRect clipping rectangle
   */
  public void paint(Graphics2D g2d, Rectangle2D clipRect) {
    if (clipRect == null) {
      this.paint(g2d);
    } else {
      if (this.isVisible() == false) {
        return;
      }
      Area clipArea = new Area(clipRect);
      Shape shape = this.getLineShape();
      if (shape == null) {
        return;
      }
      Shape edge = this.getBasicStroke().createStrokedShape(shape);
      Area sh = new Area(edge);
      sh.intersect(clipArea);
      g2d.setPaint(this.getColor());
      g2d.fill(sh);
    }
  }

  /**
   * Returns the bound of this line object.
   *
   * @return bounds of this line object
   */
  public Rectangle2D getElementBounds() {
    Shape sh = this.getBasicStroke().createStrokedShape(this.getLineShape());
    return sh.getBounds2D();
  }

  // get basic stroke
  private Stroke getBasicStroke() {
    return this.getStroke().getBasicStroke();
  }

  /** */
  public Shape getLineShape() {
    Line2D line =
        new Line2D.Float(this.getStart().x, this.getStart().y, this.getEnd().x, this.getEnd().y);
    return line;
  }

  /**
   * @param line
   * @return
   */
  public static Line2D getLine(final SGDrawingElementLine line) {
    SGTuple2f start = line.getStart();
    SGTuple2f end = line.getEnd();
    Line2D sh = new Line2D.Float(start.x, start.y, end.x, end.y);
    return sh;
  }

  /**
   * Returns the gradient of this segment.
   *
   * @return the gradient in units of radian
   */
  public float getGradient() {
    SGTuple2f start = this.getStart();
    SGTuple2f end = this.getEnd();
    return SGUtilityForFigureElementJava2D.getGradient(start.x, start.y, end.x, end.y);
  }
}
