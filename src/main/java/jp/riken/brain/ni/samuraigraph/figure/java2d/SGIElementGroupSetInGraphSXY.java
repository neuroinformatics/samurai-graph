package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import jp.riken.brain.ni.samuraigraph.base.SGIData;

/**
 * An interface for the group set in scalar type xy graph.
 */
public interface SGIElementGroupSetInGraphSXY {
    
    /**
     * Returns the bounding box of tick labels for a given data.
     * @param data
     *             a data object
     */
    public Rectangle2D getTickLabelsBoundingBox(final SGIData data);

    /**
     * Paint tick labels.
     * @param g2d
     *            the graphics context
     */
    public boolean paintDataString(final Graphics2D g2d);

}