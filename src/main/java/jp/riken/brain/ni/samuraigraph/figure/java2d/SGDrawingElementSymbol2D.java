package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementSymbol;

/**
 * 
 */
public abstract class SGDrawingElementSymbol2D extends SGDrawingElementSymbol implements
        SGIDrawingElementJava2D {

    /**
     * The default constructor.
     */
    public SGDrawingElementSymbol2D() {
        super();
    }

    /**
     * Minimum size of symbol.
     */
    public static final float MIN_SIZE = 6.0f;

    /**
     * Returns whether this symbol containts a given point.
     * 
     * @param x
     *          the x coordinate
     * @param y
     *          the y coordinate
     * @return true if a given point is contained
     */
    public boolean contains(final int x, final int y) {
        Shape sh = this.getSymbolShape();
        if (sh == null) {
            return false;
        }

        Rectangle2D rect = sh.getBounds2D();
        final float min = MIN_SIZE;

        // if the symbol is too small, enlarge the rectangle
        if (rect.getWidth() < min || rect.getHeight() < min) {
            final float centerX = (float) rect.getCenterX();
            final float centerY = (float) rect.getCenterY();
            rect = new Rectangle2D.Float(centerX - min / 2.0f, centerY - min
                    / 2.0f, min, min);
        }

        return rect.contains(x, y);
    }

    protected boolean contains(final int x, final int y, SGTuple2f location) {
    	final float size = this.getSize() * this.getMagnification();
    	return contains(x, y, location, size);
    }

    public static boolean contains(final int x, final int y, SGTuple2f location, final float size) {
    	final float halfSize = size / 2;
    	Rectangle2D rect = new Rectangle2D.Float(location.x - halfSize, location.y - halfSize, size, size);
    	return rect.contains(x, y);
    }

    /**
     * Returns the bounds of this symbol.
     * 
     * @return the bounds of this symbol
     */
    public Rectangle2D getElementBounds() {
        Shape sh = this.getSymbolShape();
        if (sh == null) {
            return new Rectangle2D.Float(this.getX(), this.getY(), 0.0f, 0.0f);
        }
        return sh.getBounds2D();
    }

    /**
     * Returns the shape.
     * 
     * @return a shape
     */
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
     * @param type
     *           the symbol type
     * @param size
     *           the size
     * @return a shape object
     */
    protected static Shape createShape(final int type, final float size) {
    	
        final float half = 0.50f * size;
        final float negHalf = - half;

        Shape sh = null;
        switch (type) {
        case SYMBOL_TYPE_CIRCLE: {
            sh = new Ellipse2D.Float(negHalf, negHalf, size, size);
            break;
        }
        case SYMBOL_TYPE_SQUARE: {
            sh = new Rectangle2D.Float(negHalf, negHalf, size, size);
            break;
        }
        case SYMBOL_TYPE_DIAMOND: {
            final float diff = size / SQRT_2;
            final float nDiff = - diff;
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
        case SYMBOL_TYPE_TRIANGLE: {
            final float div = size / SQRT_3;
            final float p1x = 0;
            final float p1y = - div;
            final float p2x = half;
            final float p2y = 0.50f * div;
            final float p3x = - half;
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
        case SYMBOL_TYPE_INVERTED_TRIANGLE: {
            final float div = -size / SQRT_3;
            final float p1x = 0;
            final float p1y = - div;
            final float p2x = half;
            final float p2y = 0.50f * div;
            final float p3x = - half;
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
        case SYMBOL_TYPE_CROSS: {
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
        case SYMBOL_TYPE_PLUS: {
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

    /**
     * Creates a shape object. Created object is located at the origin.
     * 
     * @return a shape
     */
    protected Shape createShape() {
        final int type = this.getType();
        final float size = this.getMagnification() * this.getSize();
        return createShape(type, size);
    }

    /**
     * Returns the affine transform to rotate and translate the symbol.
     * 
     * @return the affine transform
     */
    protected AffineTransform getAffineTransform() {
        AffineTransform af = new AffineTransform();
        af.translate(this.getX(), this.getY());
        af.rotate(this.getAngle());
        return af;
    }

    /**
     * Paint this object.
     * 
     * @param g2d
     *            graphics context
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
     * @param g2d
     *            The graphics context.
     * @param shape
     *            Shape object of this arrow head.
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
     * 
     * @param g2d
     * @param shape
     */
    protected void paintLine(Graphics2D g2d, Shape shape) {
        Stroke stroke = new BasicStroke(this.getMagnification()
                * this.getLineWidth(), BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER);
        g2d.setStroke(stroke);
        g2d.setPaint(this.getLineColor());
        g2d.draw(shape);
    }

    /**
     * Paint this object with given clipping rectangle.
     * 
     * @param g2d
     *            graphics context
     * @param clipRect
     *            clipping rectangle
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
     * 
     * @param g2d
     * @param clipArea
     * @param shape
     */
    protected void paintInner(Graphics2D g2d, Area clipArea, Shape shape) {
        Area inner = new Area(shape);
        inner.intersect(clipArea);
        if (this.getInnerPaint()!=null) {
            g2d.setPaint(this.getInnerPaint().getPaint(null));
        }
        g2d.fill(inner);
    }

    /**
     * 
     * @param g2d
     * @param clipArea
     * @param shape
     */
    protected void paintLine(Graphics2D g2d, Area clipArea, Shape shape) {
        Stroke stroke = new BasicStroke(this.getMagnification()
                * this.getLineWidth(), BasicStroke.CAP_BUTT,
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
    
    /**
     * Updates the shape.
     *
     */
    protected abstract void updateShape();
    
}
