package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;

/**
 * Drawing element of bar. This element is the extension of the rectangle: it has axis values for
 * the baseline and width.
 */
public abstract class SGDrawingElementBar extends SGDrawingElementRectangle
    implements SGIBarConstants, SGIDrawingElementJava2D {

  /** The default constructor. */
  public SGDrawingElementBar() {
    super();
  }

  /** Returns the baseline value. */
  public abstract double getBaselineValue();

  /**
   * Sets the baseline value.
   *
   * @param value axis value to set to the baseline value
   */
  public abstract boolean setBaselineValue(final double value);

  /** Returns the width value. */
  public abstract double getWidthValue();

  /**
   * Sets the width value.
   *
   * @param value axis value to set to the width value
   */
  public abstract boolean setWidthValue(final double value);

  /** Returns whether this bar is vertical. */
  public abstract boolean isVertical();

  /**
   * Sets whether this bar is vertical.
   *
   * @param b true to set vertical
   */
  public abstract boolean setVertical(boolean b);

  public abstract double getInterval();

  public abstract boolean setInterval(final double value);

  /** */
  public SGProperties getProperties() {
    BarProperties p = new BarProperties();
    if (this.getProperties(p) == false) return null;
    return p;
  }

  /** */
  public boolean getProperties(SGProperties p) {
    if (p == null) return false;
    if ((p instanceof BarProperties) == false) return false;
    if (super.getProperties(p) == false) return false;

    BarProperties bp = (BarProperties) p;
    bp.setBaselineValue(this.getBaselineValue());
    bp.setWidthValue(this.getWidthValue());
    bp.setInterval(this.getInterval());

    return true;
  }

  /** */
  public static class BarProperties extends RectangleProperties {
    protected double mBaselineValue;

    protected double mWidthValue;

    protected boolean mVerticalFlag;

    protected double mInterval;

    /** */
    public BarProperties() {
      super();
    }

    /** */
    public boolean equals(final Object obj) {
      if ((obj instanceof BarProperties) == false) {
        return false;
      }
      if (super.equals(obj) == false) {
        return false;
      }
      BarProperties p = (BarProperties) obj;
      if (this.mBaselineValue != p.mBaselineValue) {
        return false;
      }
      if (this.mWidthValue != p.mWidthValue) {
        return false;
      }
      if (this.mVerticalFlag != p.mVerticalFlag) {
        return false;
      }
      if (this.mInterval != p.mInterval) {
        return false;
      }
      return true;
    }

    public Double getBaselineValue() {
      return Double.valueOf(this.mBaselineValue);
    }

    public boolean setBaselineValue(final double value) {
      this.mBaselineValue = value;
      return true;
    }

    public Double getWidthValue() {
      return Double.valueOf(this.mWidthValue);
    }

    public boolean setWidthValue(final double value) {
      this.mWidthValue = value;
      return true;
    }

    public Boolean isVertical() {
      return Boolean.valueOf(this.mVerticalFlag);
    }

    public boolean setVertical(final boolean b) {
      this.mVerticalFlag = b;
      return true;
    }

    public Double getInterval() {
      return Double.valueOf(this.mInterval);
    }

    public boolean setInterval(final double value) {
      this.mInterval = value;
      return true;
    }
  }

  /** */

  /** */
  public SGDrawingElementBar(final float x, final float y, final float w, final float h) {
    super();
    this.setBounds(x, y, w, h);
  }

  /** Returns the bounding box of this bar. */
  public Rectangle2D getElementBounds() {
    final float x = this.getX();
    final float y = this.getY();
    final float w = this.getWidth();
    final float h = this.getHeight();
    final float ww = Math.abs(w);
    final float hh = Math.abs(h);
    Rectangle2D rect = new Rectangle2D.Float(x, y, ww, hh);
    return rect;
  }

  /** */
  public boolean contains(final int x, final int y) {
    return this.getElementBounds().contains(x, y);
  }

  /** */
  public Shape getShape() {
    return this.getElementBounds();
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

    final Rectangle2D dRect = this.getElementBounds();

    // paint
    g2d.setPaint(this.getInnerPaint().getPaint(dRect));
    g2d.fill(dRect);

    // draw edge lines
    if (this.isEdgeLineVisible()) {
      Stroke stroke = this.getStroke().getBasicStroke();
      g2d.setPaint(this.getEdgeLineColor());
      g2d.setStroke(stroke);
      g2d.draw(dRect);
    }
  }

  /**
   * Paint this object with given clipping rectangle.
   *
   * @param g2d graphics context
   * @param clipRect clipping rectangle
   */
  public void paint(final Graphics2D g2d, final Rectangle2D clipRect) {

    if (clipRect == null) {
      this.paint(g2d);
    } else {
      if (this.isVisible() == false) {
        return;
      }

      final Rectangle2D dRect = this.getElementBounds();

      Area clipArea = new Area(clipRect);
      Area inner = new Area(dRect);
      inner.intersect(clipArea);

      // paint
      g2d.setPaint(this.getInnerPaint().getPaint(inner.getBounds2D()));
      g2d.fill(inner);

      // draw edge lines
      if (this.isEdgeLineVisible()) {
        Stroke stroke =
            new BasicStroke(
                this.getMagnification() * this.getEdgeLineWidth(),
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER);

        Shape edge = stroke.createStrokedShape(dRect);
        Area edgeArea = new Area(edge);
        edgeArea.intersect(clipArea);

        g2d.setStroke(stroke);
        g2d.setPaint(this.getEdgeLineColor());
        g2d.fill(edgeArea);
      }
    }
  }
}
