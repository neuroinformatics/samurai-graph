package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;

/**
 * A class of line for Java2D.
 * 
 */
public abstract class SGDrawingElementLine2D extends SGDrawingElementLine implements
        SGIDrawingElementJava2D {
    /**
     * Construct a new line object.
     */
    public SGDrawingElementLine2D() {
        super();
    }

    /**
     * Construct a new line object with given start and end points.
     * 
     * @param start
     *            coordinate of the start point
     * @param end
     *            coordinate of the end point
     */
    public SGDrawingElementLine2D(final SGTuple2f start, final SGTuple2f end) {
        super(start, end);
    }

    /**
     * Construct a new line object with given start and end points.
     * 
     * @param x1
     *            x coordinate of the start point
     * @param y1
     *            y coordinate of the start point
     * @param x2
     *            x coordinate of the end point
     * @param y2
     *            y coordinate of the end point
     */
    public SGDrawingElementLine2D(final float x1, final float y1,
            final float x2, final float y2) {
        super(x1, y1, x2, y2);
    }

    /**
     * The minimum line width used in "contains" method.
     */
    public static final float MINIMUM_LINE_WIDTH = 2.0f;

    /**
     * Returns whether this line object "contains" a given point.
     * 
     * @param x
     *            x coordinate of the point
     * @param y
     *            y coordinate of the point
     * @return true when this line object "contains" given point
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

    public static boolean contains(final int x, final int y, SGTuple2f start, SGTuple2f end,
    		final float lineWidth) {
    	float lw = lineWidth;
        if (lw < MINIMUM_LINE_WIDTH) {
            lw = MINIMUM_LINE_WIDTH;
        }
        final double lensq = Line2D.ptSegDistSq(start.x, start.y, end.x, end.y,
                x, y);
        return (lensq < lw * lw);
    }

    /**
     * Paint this object.
     * 
     * @param g2d
     *            graphics context
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

    /**
     * 
     */
    public Shape getLineShape() {
        Line2D line = new Line2D.Float(this.getStart().x, this.getStart().y,
                this.getEnd().x, this.getEnd().y);
        return line;
    }

    /**
     * 
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
