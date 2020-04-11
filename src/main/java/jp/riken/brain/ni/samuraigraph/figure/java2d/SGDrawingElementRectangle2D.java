package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementRectangle;

/**
 * 
 */
public abstract class SGDrawingElementRectangle2D extends SGDrawingElementRectangle
        implements SGIDrawingElementJava2D {

    /**
     * 
     */
    public SGDrawingElementRectangle2D() {
        super();
    }

    /**
     * 
     */
    public Rectangle2D getElementBounds() {
        final float mag = this.getMagnification();
        final float x = this.getX();
        final float y = this.getY();
        final float w = mag * this.getWidth();
        final float h = mag * this.getHeight();

        final float ww = Math.abs(w);
        final float hh = Math.abs(h);
        final float xx = w > 0.0f ? x : x - ww;
        final float yy = h > 0.0f ? y : y - hh;

        Rectangle2D rect = new Rectangle2D.Float(xx, yy, ww, hh);

        return rect;
    }

    /**
     * 
     */
    public boolean contains(final int x, final int y) {
        return this.getRectShape().contains(x, y);
    }

    /**
     * 
     * @return
     */
    protected Shape getRectShape() {
        return this.getElementBounds();
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

        final Shape sh = this.getRectShape();
        if (this.getTransparency()!=0.0f) {
            g2d.setPaint(this.getInnerPaint().getPaint(sh.getBounds2D()));
            g2d.fill(sh);
        }

//        final float width = this.getMagnification() * this.getEdgeLineWidth();
//        Stroke stroke = new BasicStroke(width, BasicStroke.CAP_BUTT,
//                BasicStroke.JOIN_MITER);
        
        if (this.isEdgeLineVisible()) {
            Stroke stroke = this.getStroke().getBasicStroke();
            g2d.setPaint(this.getEdgeLineColor());
            g2d.setStroke(stroke);
            g2d.draw(sh);
        }
    }

    /**
     * Paint this object with given clipping rectangle.
     * 
     * @param g2d
     *            graphics context
     * @param clipRect
     *            clipping rectangle
     */
    public void paint(final Graphics2D g2d, final Rectangle2D clipRect) {
        this.paint(g2d);
    }

    protected boolean contains(final int x, final int y, SGTuple2f location) {
    	final float width = this.getWidth();
    	final float height = this.getHeight();
    	final float halfWidth = width / 2;
    	final float halfHeight = height / 2;
    	Rectangle2D rect = new Rectangle2D.Float(location.x - halfWidth, 
    			location.y - halfHeight, width, height);
    	return rect.contains(x, y);
    }

}
