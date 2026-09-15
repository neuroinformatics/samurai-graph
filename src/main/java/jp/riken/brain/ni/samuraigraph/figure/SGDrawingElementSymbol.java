package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.figure.SGSymbolConstants.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/** Drawing element of the symbol. */
public abstract class SGDrawingElementSymbol extends SGDrawingElement
    implements SGIDrawingElementJava2D {

  /** */
  public SGDrawingElementSymbol() {
    super();
  }

  /**
   * Sets the x-coordinate.
   *
   * @param x the x-coordinate to set
   */
  public abstract boolean setX(final float x);

  /**
   * Sets the y-coordinate.
   *
   * @param y the y-coordinate to set
   */
  public abstract boolean setY(final float y);

  /**
   * Sets the location.
   *
   * @param x the x-coordinate to set
   * @param y the y-coordinate to set
   */
  public abstract boolean setLocation(final float x, final float y);

  /**
   * Sets the location.
   *
   * @param point the location to set
   */
  public abstract boolean setLocation(final SGTuple2f point);

  /**
   * Sets the size.
   *
   * @param size the size to set
   */
  public abstract boolean setSize(final float size);

  /**
   * Sets the symbol type.
   *
   * @param type the symbol type to set
   */
  public abstract boolean setType(final int type);

  /**
   * Sets the angle.
   *
   * @param angle the angle to set
   */
  public abstract boolean setAngle(final float angle);

  /**
   * Sets the line color.
   *
   * @param color the line color to set
   */
  public abstract boolean setLineColor(final Color color);

  /**
   * Sets the inner color.
   *
   * @param color the inner color to set
   */
  public abstract boolean setInnerColor(final Color color);

  /**
   * Sets the line width.
   *
   * @param lineWidth the line width to set
   */
  public abstract boolean setLineWidth(final float lineWidth);

  /**
   * Sets the line visible.
   *
   * @param visible the line visible to set
   */
  public abstract boolean setLineVisible(final boolean visible);

  /** Returns the location. */
  public abstract SGTuple2f getLocation();

  /** Returns the x-coordinate. */
  public float getX() {
    return this.getLocation().x;
  }

  /** Returns the y-coordinate. */
  public float getY() {
    return this.getLocation().y;
  }

  /** Returns the angle of this symbol. */
  public abstract float getAngle();

  /** Returns the size. */
  public abstract float getSize();

  /** Returns the symbol type. */
  public abstract int getType();

  /** Returns the inner paint. */
  public abstract SGIPaint getInnerPaint();

  /** Returns the line color. */
  public abstract Color getLineColor();

  /** Returns the line width. */
  public abstract float getLineWidth();

  /** Returns the line visible. */
  public abstract boolean isLineVisible();

  /**
   * Returns whether a given symbol type is valid.
   *
   * @param type a symbol type
   */
  public static boolean isValidSymbolType(final int type) {
    final int[] array = {
      SYMBOL_TYPE_CIRCLE,
      SYMBOL_TYPE_SQUARE,
      SYMBOL_TYPE_DIAMOND,
      SYMBOL_TYPE_TRIANGLE,
      SYMBOL_TYPE_INVERTED_TRIANGLE,
      SYMBOL_TYPE_CROSS,
      SYMBOL_TYPE_PLUS
    };
    for (int ii = 0; ii < array.length; ii++) {
      if (type == array[ii]) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the symbol type constant from a given name.
   *
   * @param name the name of symbol type
   */
  public static Integer getSymbolTypeFromName(final String name) {
    if (name == null) {
      return null;
    }
    int type;
    if (SGUtilityText.isEqualString(SYMBOL_NAME_CIRCLE, name)) {
      type = SYMBOL_TYPE_CIRCLE;
    } else if (SGUtilityText.isEqualString(SYMBOL_NAME_SQUARE, name)) {
      type = SYMBOL_TYPE_SQUARE;
    } else if (SGUtilityText.isEqualString(SYMBOL_NAME_DIAMOND, name)) {
      type = SYMBOL_TYPE_DIAMOND;
    } else if (SGUtilityText.isEqualString(SYMBOL_NAME_TRIANGLE, name)) {
      type = SYMBOL_TYPE_TRIANGLE;
    } else if (SGUtilityText.isEqualString(SYMBOL_NAME_INVERTED_TRIANGLE, name)
        || SGUtilityText.isEqualString(SYMBOL_NAME_INVERTED_TRIANGLE_OLD, name)) {
      // to maintain downward compatibility with the old releases
      type = SYMBOL_TYPE_INVERTED_TRIANGLE;
    } else if (SGUtilityText.isEqualString(SYMBOL_NAME_CROSS, name)) {
      type = SYMBOL_TYPE_CROSS;
    } else if (SGUtilityText.isEqualString(SYMBOL_NAME_PLUS, name)) {
      type = SYMBOL_TYPE_PLUS;
    } else {
      return null;
    }
    return Integer.valueOf(type);
  }

  /**
   * Returns the name of a given symbol type.
   *
   * @param type the symbol type
   */
  public static String getSymbolTypeName(final int type) {
    String name = null;
    switch (type) {
      case SYMBOL_TYPE_CIRCLE:
        name = SYMBOL_NAME_CIRCLE;
        break;
      case SYMBOL_TYPE_SQUARE:
        name = SYMBOL_NAME_SQUARE;
        break;
      case SYMBOL_TYPE_DIAMOND:
        name = SYMBOL_NAME_DIAMOND;
        break;
      case SYMBOL_TYPE_TRIANGLE:
        name = SYMBOL_NAME_TRIANGLE;
        break;
      case SYMBOL_TYPE_INVERTED_TRIANGLE:
        name = SYMBOL_NAME_INVERTED_TRIANGLE;
        break;
      case SYMBOL_TYPE_CROSS:
        name = SYMBOL_NAME_CROSS;
        break;
      case SYMBOL_TYPE_PLUS:
        name = SYMBOL_NAME_PLUS;
        break;
      default:
    }
    return name;
  }

  /**
   * Returns whether a given symbol type is of the line type.
   *
   * @param type a symbol type
   */
  public static boolean isLineTypeSymbol(final int type) {
    return ((type == SYMBOL_TYPE_CROSS) || (type == SYMBOL_TYPE_PLUS));
  }

  /** */
  public SGProperties getProperties() {
    SymbolProperties p = new SymbolProperties();
    this.getProperties(p);
    return p;
  }

  /** */
  public boolean getProperties(SGProperties p) {
    if (p == null) return false;
    if ((p instanceof SymbolProperties) == false) return false;

    super.getProperties(p);

    SymbolProperties sp = (SymbolProperties) p;
    sp.setSize(this.getSize());
    sp.setSymbolType(this.getType());
    sp.setLineWidth(this.getLineWidth());
    sp.setLineColor(this.getLineColor());

    return true;
  }

  /** */
  public static class SymbolProperties extends DrawingElementProperties {

    private int mType = -1;

    private float mSize = 0.0f;

    private float mLineWidth = 0.0f;

    private Color mLineColor = null;

    private boolean mLineVisible = true;

    private SGFillPaint mInnerPaint = null;

    /** */
    public SymbolProperties() {
      super();
    }

    /** */
    public boolean equals(final Object obj) {
      if ((obj instanceof SymbolProperties) == false) {
        return false;
      }
      if (super.equals(obj) == false) {
        return false;
      }
      SymbolProperties p = (SymbolProperties) obj;
      if (p.mType != this.mType) {
        return false;
      }
      if (p.mSize != this.mSize) {
        return false;
      }
      if (p.mLineWidth != this.mLineWidth) {
        return false;
      }
      if (SGUtility.equals(p.mLineColor, this.mLineColor) == false) {
        return false;
      }
      if (p.mLineVisible != this.mLineVisible) {
        return false;
      }
      if (SGUtility.equals(p.mInnerPaint, this.mInnerPaint) == false) {
        return false;
      }
      return true;
    }

    public Float getSize() {
      return Float.valueOf(this.mSize);
    }

    public Integer getSymbolType() {
      return Integer.valueOf(this.mType);
    }

    public Float getLineWidth() {
      return Float.valueOf(this.mLineWidth);
    }

    public Color getLineColor() {
      return this.mLineColor;
    }

    public Boolean isLineVisible() {
      return Boolean.valueOf(this.mLineVisible);
    }

    public SGIPaint getInnerPaint() {
      return this.mInnerPaint;
    }

    public boolean setSize(final float size) {
      this.mSize = size;
      return true;
    }

    public boolean setSymbolType(final int type) {
      this.mType = type;
      return true;
    }

    public boolean setLineWidth(final float width) {
      this.mLineWidth = width;
      return true;
    }

    public boolean setLineColor(final Color cl) {
      this.mLineColor = cl;
      return true;
    }

    public boolean setLineVisible(final boolean visible) {
      this.mLineVisible = visible;
      return true;
    }

    public boolean setInnerPaint(final SGIPaint paint) {
      try {
        this.mInnerPaint = (SGFillPaint) paint.clone();
      } catch (CloneNotSupportedException e) {
        throw new InternalError(e.getMessage());
      }
      return true;
    }
  }

  /** The default constructor. */

  /** Minimum size of symbol. */
  public static final float MIN_SIZE = 6.0f;

  /**
   * Returns whether this symbol contains a given point.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public boolean contains(final int x, final int y) {
    Shape sh = this.getSymbolShape();
    if (sh == null) {
      return false;
    }

    Rectangle2D rect = sh.getBounds2D();
    final float min = MIN_SIZE;

    if (rect.getWidth() < min || rect.getHeight() < min) {
      final float centerX = (float) rect.getCenterX();
      final float centerY = (float) rect.getCenterY();
      rect = new Rectangle2D.Float(centerX - min / 2.0f, centerY - min / 2.0f, min, min);
    }

    return rect.contains(x, y);
  }

  protected boolean contains(final int x, final int y, SGTuple2f location) {
    final float size = this.getSize() * this.getMagnification();
    return contains(x, y, location, size);
  }

  public static boolean contains(final int x, final int y, SGTuple2f location, final float size) {
    final float halfSize = size / 2;
    Rectangle2D rect =
        new Rectangle2D.Float(location.x - halfSize, location.y - halfSize, size, size);
    return rect.contains(x, y);
  }

  /** Returns the bounds of this symbol. */
  public Rectangle2D getElementBounds() {
    Shape sh = this.getSymbolShape();
    if (sh == null) {
      return new Rectangle2D.Float(this.getX(), this.getY(), 0.0f, 0.0f);
    }
    return sh.getBounds2D();
  }

  /** Returns the shape. */
  public Shape getSymbolShape() {
    Shape sh = this.getShape();
    if (sh != null) {
      AffineTransform af = this.getAffineTransform();
      sh = af.createTransformedShape(sh);
    }
    return sh;
  }

  private static final float SQRT_2 = (float) Math.sqrt(2.0f);

  private static final float SQRT_3 = (float) Math.sqrt(3.0f);

  /**
   * Creates a shape.
   *
   * @param type the symbol type
   * @param size the size
   */
  protected static Shape createShape(final int type, final float size) {

    final float half = 0.50f * size;
    final float negHalf = -half;

    Shape sh = null;
    switch (type) {
      case SYMBOL_TYPE_CIRCLE:
        {
          sh = new Ellipse2D.Float(negHalf, negHalf, size, size);
          break;
        }
      case SYMBOL_TYPE_SQUARE:
        {
          sh = new Rectangle2D.Float(negHalf, negHalf, size, size);
          break;
        }
      case SYMBOL_TYPE_DIAMOND:
        {
          final float diff = size / SQRT_2;
          final float nDiff = -diff;
          final float m = nDiff;
          final float p = diff;

          Line2D line12 = new Line2D.Float(0, p, p, 0);
          Line2D line23 = new Line2D.Float(p, 0, 0, m);
          Line2D line34 = new Line2D.Float(0, m, m, 0);
          Line2D line41 = new Line2D.Float(m, 0, 0, p);

          GeneralPath gp = new GeneralPath();
          gp.append(line12, true);
          gp.append(line23, true);
          gp.append(line34, true);
          gp.append(line41, true);
          gp.append(line12, true);
          sh = gp;

          break;
        }
      case SYMBOL_TYPE_TRIANGLE:
        {
          final float div = size / SQRT_3;
          final float p1x = 0;
          final float p1y = -div;
          final float p2x = half;
          final float p2y = 0.50f * div;
          final float p3x = -half;
          final float p3y = p2y;

          Line2D line12 = new Line2D.Float(p1x, p1y, p2x, p2y);
          Line2D line23 = new Line2D.Float(p2x, p2y, p3x, p3y);
          Line2D line31 = new Line2D.Float(p3x, p3y, p1x, p1y);

          GeneralPath gp = new GeneralPath();
          gp.append(line12, true);
          gp.append(line23, true);
          gp.append(line31, true);
          gp.append(line12, true);
          sh = gp;

          break;
        }
      case SYMBOL_TYPE_INVERTED_TRIANGLE:
        {
          final float div = -size / SQRT_3;
          final float p1x = 0;
          final float p1y = -div;
          final float p2x = half;
          final float p2y = 0.50f * div;
          final float p3x = -half;
          final float p3y = p2y;

          Line2D line12 = new Line2D.Float(p1x, p1y, p2x, p2y);
          Line2D line23 = new Line2D.Float(p2x, p2y, p3x, p3y);
          Line2D line31 = new Line2D.Float(p3x, p3y, p1x, p1y);

          GeneralPath gp = new GeneralPath();
          gp.append(line12, true);
          gp.append(line23, true);
          gp.append(line31, true);
          gp.append(line12, true);
          sh = gp;

          break;
        }
      case SYMBOL_TYPE_CROSS:
        {
          final float xm = negHalf;
          final float xp = half;
          final float ym = negHalf;
          final float yp = half;

          Line2D line1 = new Line2D.Float(xm, ym, xp, yp);
          Line2D line2 = new Line2D.Float(xp, ym, xm, yp);

          GeneralPath gp = new GeneralPath();
          gp.append(line1, false);
          gp.append(line2, false);
          sh = gp;

          break;
        }
      case SYMBOL_TYPE_PLUS:
        {
          Line2D line1 = new Line2D.Float(negHalf, 0, half, 0);
          Line2D line2 = new Line2D.Float(0, negHalf, 0, half);

          GeneralPath gp = new GeneralPath();
          gp.append(line1, false);
          gp.append(line2, false);
          sh = gp;

          break;
        }
    }

    return sh;
  }

  /** Creates a shape object. Created object is located at the origin. */
  protected Shape createShape() {
    final int type = this.getType();
    final float size = this.getMagnification() * this.getSize();
    return createShape(type, size);
  }

  /** Returns the affine transform to rotate and translate the symbol. */
  protected AffineTransform getAffineTransform() {
    AffineTransform af = new AffineTransform();
    af.translate(this.getX(), this.getY());
    af.rotate(this.getAngle());
    return af;
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

    // get the shape without affine transformation
    Shape shape = this.getShape();
    if (shape == null) {
      return;
    }

    // create an affine transform
    // and set to the graphic context
    AffineTransform cur = g2d.getTransform();
    AffineTransform af = new AffineTransform(cur);
    af.concatenate(this.getAffineTransform());
    g2d.setTransform(af);

    // paint the inner of symbol
    this.paintInner(g2d, shape);

    // paint the line
    if (this.isLineVisible()) {
      this.paintLine(g2d, shape);
    }

    // restore the graphic context
    g2d.setTransform(cur);
  }

  /**
   * Paint inside the symbol.
   *
   * @param g2d The graphics context.
   * @param shape Shape object of this arrow head.
   */
  protected void paintInner(Graphics2D g2d, Shape shape) {
    if (!SGDrawingElementSymbol.isLineTypeSymbol(this.getType())) {
      // paint inside the symbol only with finite area
      // because painting inside the symbols without finite area
      // such as cross lines induce irrelevant effect in some environment
      if (this.getInnerPaint() != null) {
        g2d.setPaint(this.getInnerPaint().getPaint(null));
      }
      g2d.fill(shape);
    }
  }

  /**
   * @param g2d
   * @param shape
   */
  protected void paintLine(Graphics2D g2d, Shape shape) {
    Stroke stroke =
        new BasicStroke(
            this.getMagnification() * this.getLineWidth(),
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER);
    g2d.setStroke(stroke);
    g2d.setPaint(this.getLineColor());
    g2d.draw(shape);
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

      Shape shape = this.getSymbolShape();
      if (shape == null) {
        return;
      }

      // Area to be clipped
      Area clipArea = new Area(clipRect);

      // paint the inner of symbol
      this.paintInner(g2d, clipArea, shape);

      // paint the line
      if (this.isLineVisible()) {
        this.paintLine(g2d, clipArea, shape);
      }
    }
  }

  /**
   * @param g2d
   * @param clipArea
   * @param shape
   */
  protected void paintInner(Graphics2D g2d, Area clipArea, Shape shape) {
    Area inner = new Area(shape);
    inner.intersect(clipArea);
    if (this.getInnerPaint() != null) {
      g2d.setPaint(this.getInnerPaint().getPaint(null));
    }
    g2d.fill(inner);
  }

  /**
   * @param g2d
   * @param clipArea
   * @param shape
   */
  protected void paintLine(Graphics2D g2d, Area clipArea, Shape shape) {
    Stroke stroke =
        new BasicStroke(
            this.getMagnification() * this.getLineWidth(),
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER);

    Shape edge = stroke.createStrokedShape(shape);
    Area sh = new Area(edge);
    sh.intersect(clipArea);

    g2d.setPaint(this.getLineColor());
    g2d.fill(sh);
  }

  /**
   * Returns the shape.
   *
   * @return a shape object
   */
  protected abstract Shape getShape();

  /** Updates the shape. */
  protected abstract void updateShape();
}
