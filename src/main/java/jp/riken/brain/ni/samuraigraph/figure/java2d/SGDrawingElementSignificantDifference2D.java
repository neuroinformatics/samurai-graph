/*
 * Created on 2004/06/15
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementString;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementSignificantDifference;

/**
 * @author kuromaru To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class SGDrawingElementSignificantDifference2D extends
        SGDrawingElementSignificantDifference implements
        SGIDrawingElementJava2D {
    /**
     * 
     */
    public SGDrawingElementSignificantDifference2D() {
        super();
    }

    /**
     * 
     */
    public SGDrawingElementSignificantDifference2D(final float x,
            final float y, final float w, final float hl, final float hr) {
        super(x, y, w, hl, hr);
    }

    /**
     * 
     */
    protected SGDrawingElementString createString() {
        return new SGDrawingElementString2DExtended("*");
    }

    protected SGDrawingElementLine createLine() {
        return new SigDiffLine();
    }
    
    /**
     * A line in a significant different symbol.
     *
     */
    private static class SigDiffLine extends SGSimpleLine2D {
        public SigDiffLine() {
            super();
        }
    }

    /**
     * 
     */
    protected boolean createDrawingElement() {
        // System.out.println("<< createDrawingElement >>");

        final float mag = this.getMagnification();
        final float width = mag * this.getWidth();
        final float h1 = mag * this.getVerticalHeight1();
        final float h2 = mag * this.getVerticalHeight2();
        final float space = mag * this.getSpace();

        final SGDrawingElementString2DExtended str = (SGDrawingElementString2DExtended) this
                .getStringElement();
        final SGDrawingElementLine2D hline = (SGDrawingElementLine2D) this
                .getHorizontalLine();
        final SGDrawingElementLine2D pline1 = (SGDrawingElementLine2D) this
                .getVerticalLine1();
        final SGDrawingElementLine2D pline2 = (SGDrawingElementLine2D) this
                .getVerticalLine2();

        final SGTuple2f leftTop = new SGTuple2f(this.getX(), this.getY());

        final SGTuple2f rightTop = new SGTuple2f(this.getX() + width, this
                .getY());

        final SGTuple2f leftBottom = new SGTuple2f(this.getX(), this.getY()
                + h1);

        final SGTuple2f rightBottom = new SGTuple2f(this.getX() + width, this
                .getY()
                + h2);

        // System.out.println(this.getX()+" "+this.getY());

        hline.setTermPoints(leftTop, rightTop);
        pline1.setTermPoints(leftTop, leftBottom);
        pline2.setTermPoints(rightTop, rightBottom);

        final Rectangle2D rect = str.getElementBounds();
        final float strLeading = str.getLeading() * 2.0f;
        final float rectWidth = (float) rect.getWidth();
        final float rectHeight = (float) rect.getHeight();

        final float strX = this.getX() + width / 2.0f - (rectWidth / 2.0f);
        float strY;
        if (!this.isFlippingVertical()) {
            strY = this.getY() - space - rectHeight - strLeading;
        } else {
            strY = this.getY() + space + strLeading;
        }

        str.setLocation(strX, strY);

        return true;

    }

    /**
     * 
     */
    public Point2D getJoint1() {
        Point2D pos = new Point2D.Float(this.getX1(), this.getY());
        return pos;
    }

    /**
     * 
     */
    public Point2D getJoint2() {
        Point2D pos = new Point2D.Float(this.getX2(), this.getY());
        return pos;
    }

    /**
     * 
     */
    public Point2D getLeftJoint() {
        final Point2D pos;
        if (!this.isFlippingHorizontal()) {
            pos = this.getJoint1();
        } else {
            pos = this.getJoint2();
        }
        return pos;
    }

    /**
     * 
     */
    public Point2D getLeftTerm() {
        final Point2D pos = this.getLeftJoint();
        pos.setLocation(pos.getX(), pos.getY() + this.getMagnification()
                * this.getLeftHeight());
        return pos;
    }

    /**
     * 
     */
    public Point2D getRightJoint() {
        final Point2D pos;
        if (!this.isFlippingHorizontal()) {
            pos = this.getJoint2();
        } else {
            pos = this.getJoint1();
        }
        return pos;
    }

    /**
     * 
     */
    public Point2D getRightTerm() {
        final Point2D pos = this.getRightJoint();
        pos.setLocation(pos.getX(), pos.getY() + this.getMagnification()
                * this.getRightHeight());
        return pos;
    }

    /**
     * 
     * @return
     */
    public Point2D getHorizontalMiddle() {
        final Point2D pos = new Point2D.Float(this.getX1()
                + this.getMagnification() * this.getWidth() / 2.0f, this.getY());
        return pos;
    }

    /**
     * 
     * @return
     */
    public Point2D getLeftMiddle() {
        final Point2D pos = new Point2D.Float();
        final Point2D joint = this.getLeftJoint();
        final Point2D term = this.getLeftTerm();
        pos.setLocation((joint.getX() + term.getX()) / 2.0,
                (joint.getY() + term.getY()) / 2.0);
        return pos;
    }

    /**
     * 
     * @return
     */
    public Point2D getRightMiddle() {
        final Point2D pos = new Point2D.Float();
        final Point2D joint = this.getRightJoint();
        final Point2D term = this.getRightTerm();
        pos.setLocation((joint.getX() + term.getX()) / 2.0,
                (joint.getY() + term.getY()) / 2.0);
        return pos;
    }

    /**
     * 
     */
    public Shape getConnectedShape() {
        final SGDrawingElementLine2D hline = (SGDrawingElementLine2D) this
                .getHorizontalLine();
        final SGDrawingElementLine2D pline1 = (SGDrawingElementLine2D) this
                .getVerticalLine1();
        final SGDrawingElementLine2D pline2 = (SGDrawingElementLine2D) this
                .getVerticalLine2();
        GeneralPath gp = new GeneralPath();
        gp.append(pline1.getLineShape(), true);
        gp.append(hline.getLineShape(), true);
        gp.append(pline2.getLineShape(), true);
        return gp;
    }

    /**
     * Sets the magnification.
     * 
     * @param mag
     *           the magnification to set
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        this.getStringElement().setMagnification(mag);
        return this.createDrawingElement();
    }

    /**
     * 
     * @return
     */
    public Rectangle2D getElementBounds() {
        ArrayList rectList = new ArrayList();
        rectList.add(this.getLineBounds());
        rectList.add(this.getStringBounds());
        Rectangle2D rect = SGUtility.createUnion(rectList);

        return rect;
    }

    /**
     * 
     */
    public Rectangle2D getLineBounds() {
        final SGDrawingElementLine2D hline = (SGDrawingElementLine2D) this
                .getHorizontalLine();
        final SGDrawingElementLine2D pline1 = (SGDrawingElementLine2D) this
                .getVerticalLine1();
        final SGDrawingElementLine2D pline2 = (SGDrawingElementLine2D) this
                .getVerticalLine2();
        ArrayList rectList = new ArrayList();
        rectList.add(hline.getLineShape().getBounds2D());
        rectList.add(pline1.getLineShape().getBounds2D());
        rectList.add(pline2.getLineShape().getBounds2D());
        Rectangle2D rect = SGUtility.createUnion(rectList);

        return rect;
    }

    /**
     * 
     */
    public Rectangle2D getStringBounds() {
        return ((SGDrawingElementString2DExtended) this.getStringElement())
                .getElementBounds();
    }

    /**
     * Paint this object.
     * 
     * @param g2d
     *            graphics context
     */
    public void paint(final Graphics2D g2d) {
        g2d.setPaint(this.getColor());

        if (this.isLineVisible()) {
            final float lineWidth = this.getMagnification()
                    * this.getLineWidth();
            g2d.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER));

            Shape sh = this.getConnectedShape();
            g2d.draw(sh);
        }

        // string
        SGDrawingElementString2DExtended sElement = (SGDrawingElementString2DExtended) this
                .getStringElement();
        sElement.paint(g2d);

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

}
