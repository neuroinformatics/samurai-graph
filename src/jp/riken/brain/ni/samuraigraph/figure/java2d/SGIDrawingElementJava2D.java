package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Drawing element with Java2D methods.
 */
public interface SGIDrawingElementJava2D {

    /**
     * Returns the bounding box of this object.
     * 
     * @return
     *       A bounding box of this object.
     */
    public Rectangle2D getElementBounds();

    /**
     * Paint this object.
     * 
     * @param g2d
     *            graphics context
     */
    public void paint(final Graphics2D g2d);

    /**
     * Paint this object with given clipping rectangle.
     * 
     * @param g2d
     *            graphics context
     * @param clipRect
     *            clipping rectangle
     */
    public void paint(Graphics2D g2d, Rectangle2D clipRect);
    
}
