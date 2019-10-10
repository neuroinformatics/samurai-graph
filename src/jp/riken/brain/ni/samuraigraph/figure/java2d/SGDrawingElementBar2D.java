package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementBar;

/**
 * 
 */
public abstract class SGDrawingElementBar2D extends SGDrawingElementBar implements
        SGIDrawingElementJava2D {

    /**
     * 
     */
    public SGDrawingElementBar2D() {
        super();
    }

    /**
     * 
     */
    public SGDrawingElementBar2D(final float x, final float y, final float w,
            final float h) {
        super();
        this.setBounds(x, y, w, h);
    }

    /**
     * Returns the bounding box of this bar.
     * 
     * @return a bounding box of this bar
     */
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

    /**
     * 
     */
    public boolean contains(final int x, final int y) {
        return this.getElementBounds().contains(x, y);
    }

    /**
     * 
     */
    public Shape getShape() {
        return this.getElementBounds();
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

//        final float width = this.getMagnification() * this.getEdgeLineWidth();
//        Stroke stroke = new BasicStroke(width, BasicStroke.CAP_BUTT,
//                BasicStroke.JOIN_MITER);
        
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
     * @param g2d
     *            graphics context
     * @param clipRect
     *            clipping rectangle
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
                Stroke stroke = new BasicStroke(this.getMagnification()
                        * this.getEdgeLineWidth(), BasicStroke.CAP_BUTT,
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

    // /**
    // *
    // */
    // public Object copy()
    // {
    // SGDrawingElementBar2D el = new SGDrawingElementBar2D();
    // this.setPropertiesForCopy(el);
    // return el;
    // }
    //
    //
    // protected boolean setPropertiesForCopy( SGDrawingElementBar2D el )
    // {
    // el.setProperties( this.getProperties() );
    // el.setLocation( this.getX(), this.getY() );
    // el.setMagnification( this.mMagnification );
    // return true;
    // }

}
