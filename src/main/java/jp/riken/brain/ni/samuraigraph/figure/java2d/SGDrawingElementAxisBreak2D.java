/*
 * Created on 2004/09/29
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementAxisBreak;

/**
 * @author kuromaru
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class SGDrawingElementAxisBreak2D extends SGDrawingElementAxisBreak
        implements SGIDrawingElementJava2D {

    /**
     * 
     */
    private Shape mCurve1 = null;

    /**
     * 
     */
    private Shape mCurve2 = null;

    /**
     * 
     */
    public SGDrawingElementAxisBreak2D() {
        super();
    }

    /**
     * 
     */
    public SGDrawingElementAxisBreak2D(final float length,
            final String lengthUnit, final float interval,
            final String intervalUnit, final float dist, final float angle,
            final boolean horizontal) {
        super(length, lengthUnit, interval, intervalUnit, dist, angle,
                horizontal);
    }

    /**
     * 
     */
    public void dispose() {
        super.dispose();
        this.mCurve1 = null;
        this.mCurve2 = null;
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
    public Rectangle2D getElementBounds() {
        if (this.mCurve1 == null || this.mCurve2 == null) {
            return new Rectangle2D.Float();
        }
        Rectangle2D rect = this.getArea().getBounds2D();
        return rect;
    }

    /**
     * 
     * @return
     */
    public Area getArea() {
        Shape[] array = this.getShapeArray();
        GeneralPath gp = new GeneralPath();
        for (int ii = 0; ii < array.length; ii++) {
            gp.append(array[ii], true);
        }
        Area area = new Area(gp);
        return area;
    }

    /**
     * 
     * @return
     */
    private CubicCurve2D getBaseCurve() {
        final float mag = this.getMagnification();
        final float length = this.getLength() * mag;
        final float dist = this.getDistortion() * length;

        CubicCurve2D cv = new CubicCurve2D.Float(0.0f, 0.0f, -dist,
                0.25f * length, dist, 0.75f * length, 0.0f, length);

        return cv;
    }

    /**
     * 
     * @return
     */
    protected boolean create() {
        this.createCurve1();
        this.createCurve2();
        return true;
    }

    /**
     * 
     * @return
     */
    private boolean createCurve1() {
        final float x = this.getX();
        final float y = this.getY();
        final float mag = this.getMagnification();
        final float length = this.getLength() * mag;
        final float interval = this.getInterval() * mag;
        final float angle = this.getAngle() * SGIConstants.RADIAN_DEGREE_RATIO;

        final float sn = (float) Math.sin(angle);
        final float cs = (float) Math.cos(angle);

        AffineTransform af = new AffineTransform();
        float startX1;
        float startY1;
        float rot = angle;
        if (this.isForHorizontalAxis()) {
            startX1 = x - interval / 2.0f - (length / 2.0f) * sn;
            startY1 = y - (length / 2.0f) * cs;
        } else {
            startX1 = x - (length / 2.0f) * cs;
            startY1 = y - interval / 2.0f + (length / 2.0f) * sn;
            rot += Math.PI / 2.0;
        }
        af.translate(startX1, startY1);
        af.rotate(-rot);

        this.mCurve1 = af.createTransformedShape(this.getBaseCurve());

        return true;
    }

    /**
     * 
     * @return
     */
    private boolean createCurve2() {
        SGTuple2f pos = this.getLocation();

        AffineTransform af = new AffineTransform();
        af.rotate(Math.PI, pos.x, pos.y);
        this.mCurve2 = af.createTransformedShape(this.mCurve1);

        return true;
    }

    /**
     * 
     * @return
     */
    protected Shape[] getShapeArray() {
        if (this.mCurve1 == null || this.mCurve2 == null) {
            return new Shape[0];
        }

        Shape[] array = new Shape[4];

        Shape curve1 = this.mCurve1;
        Shape curve2 = this.mCurve2;

        PathIterator itr1 = curve1.getPathIterator(null);
        ArrayList list1 = SGUtilityForFigureElementJava2D.getSegmentList(itr1);
        Point2D pStart1 = (Point2D) list1.get(0);
        Point2D pEnd1 = (Point2D) list1.get(list1.size() - 1);

        PathIterator itr2 = curve2.getPathIterator(null);
        ArrayList list2 = SGUtilityForFigureElementJava2D.getSegmentList(itr2);
        Point2D pStart2 = (Point2D) list2.get(0);
        Point2D pEnd2 = (Point2D) list2.get(list2.size() - 1);

        array[0] = curve1;

        array[1] = new Line2D.Float((float) pEnd1.getX(), (float) pEnd1.getY(),
                (float) pStart2.getX(), (float) pStart2.getY());

        array[2] = curve2;

        array[3] = new Line2D.Float((float) pEnd2.getX(), (float) pEnd2.getY(),
                (float) pStart1.getX(), (float) pStart1.getY());

        return array;

    }

    /**
     * 
     * @return
     */
    private GeneralPath createConnectedPath() {
        GeneralPath gp = new GeneralPath();
        Shape[] array = this.getShapeArray();
        for (int ii = 0; ii < array.length; ii++) {
            gp.append(array[ii], true);
        }
        return gp;
    }

    /**
     * Sets the location of this symbol.
     * 
     * @param x
     *          the x coordinate to set
     * @param y
     *          the y coordinate to set
     * @return true if succeeded
     */
    public boolean setLocation(final float x, final float y) {
        super.setLocation(x, y);
        return this.create();
    }

    /**
     * Sets the magnification.
     * 
     * @param mag
     *          the magnification to set
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        return this.create();
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

    /**
     * Paint this object.
     * 
     * @param g2d
     *            graphics context
     */
    public void paint(final Graphics2D g2d) {
        if (this.mCurve1 == null || this.mCurve2 == null) {
            return;
        }

        g2d.setPaint(this.getInnerColor());
        g2d.fill(this.createConnectedPath());

        g2d.setPaint(this.getLineColor());
        g2d.setStroke(new BasicStroke(this.getLineWidth()
                * this.getMagnification()));

        g2d.draw(this.mCurve1);
        g2d.draw(this.mCurve2);
    }

}
