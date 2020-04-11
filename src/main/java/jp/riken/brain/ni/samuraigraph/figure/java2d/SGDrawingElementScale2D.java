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
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementScale;

public abstract class SGDrawingElementScale2D extends
		SGDrawingElementScale implements SGIDrawingElementJava2D {

	public SGDrawingElementScale2D() {
		super();
	}

	public SGDrawingElementScale2D(float x, float y, float w, float h) {
		super(x, y, w, h);
	}

	@Override
	public boolean setMagnification(float mag) {
        this.getHorizontalStringElement().setMagnification(mag);
        this.getVerticalStringElement().setMagnification(mag);
        return this.updateDrawingElementsLocation();
	}

    protected boolean updateDrawingElementsLocation() {
        final SGDrawingElementLine2D hline = (SGDrawingElementLine2D) this
                .getHorizontalLine();
        final SGDrawingElementLine2D vline = (SGDrawingElementLine2D) this
                .getVerticalLine();
        
        Point2D.Float jointPos = (Point2D.Float) this.getJoint();
        Point2D.Float hPos = (Point2D.Float) this.getHorizontalEnd();
        Point2D.Float vPos = (Point2D.Float) this.getVerticalEnd();
		final SGTuple2f joint = new SGTuple2f(jointPos.x, jointPos.y);
		final SGTuple2f hEnd = new SGTuple2f(hPos.x, hPos.y);
		final SGTuple2f vEnd = new SGTuple2f(vPos.x, vPos.y);

        hline.setTermPoints(joint, hEnd);
        vline.setTermPoints(joint, vEnd);

        this.updateLabelLocation();

        return true;
    }
    
    protected void updateLabelLocation() {
        final float mag = this.getMagnification();

        final SGDrawingElementString2DExtended hStr = (SGDrawingElementString2DExtended) this
                .getHorizontalStringElement();
        final SGDrawingElementString2DExtended vStr = (SGDrawingElementString2DExtended) this
                .getVerticalStringElement();

        final float width = mag * this.getWidth();
        final float height = mag * this.getHeight();
        final float space = mag * this.getSpace();

        // horizontal string
        final Rectangle2D hRect = hStr.getElementBounds();
        final float hRectWidth = (float) hRect.getWidth();
        final float hRectHeight = (float) hRect.getHeight();
        final float hDefaultX = this.getX() + width / 2.0f - hRectWidth / 2.0f;
        final float hDefaultY;
        if (this.isHorizontalTextDownside()) {
            hDefaultY = this.getY() + space;
        } else {
            hDefaultY = this.getY() - space - hRectHeight;
        }
		hStr.setLocation(hDefaultX, hDefaultY);
        final float hStrX = hDefaultX + (hDefaultX - (float) hStr.getElementBounds().getX());
        final float hStrY = hDefaultY + (hDefaultY - (float) hStr.getElementBounds().getY());
        hStr.setLocation(hStrX, hStrY);

        // vertical string
        final Rectangle2D vRect = vStr.getElementBounds();
        final float vRectWidth = (float) vRect.getWidth();
        final float vRectHeight = (float) vRect.getHeight();
        final float vDefaultY = this.getY() + height / 2.0f - (vRectHeight / 2.0f);
        final float vDefaultX;
        if (this.isVerticalTextLeftside()) {
            vDefaultX = this.getX() - space - vRectWidth;
        } else {
        	vDefaultX = this.getX() + space;
        }
        vStr.setLocation(vDefaultX, vDefaultY);
        final float vStrX = vDefaultX + (vDefaultX - (float) vStr.getElementBounds().getX());
        final float vStrY = vDefaultY + (vDefaultY - (float) vStr.getElementBounds().getY());
        vStr.setLocation(vStrX, vStrY);
    }

    public Point2D getJoint() {
        Point2D pos = new Point2D.Float(this.getX(), this.getY());
        return pos;
    }

    public float getEndPointX() {
        return this.getX() + this.getMagnification() * this.getWidth();
    }
    
    public Point2D getHorizontalEnd() {
        final Point2D pos = new Point2D.Float(this.getEndPointX(), this.getY());
        return pos;
    }

    public float getEndPointY() {
        return this.getY() + this.getMagnification() * this.getHeight();
    }

    public Point2D getVerticalEnd() {
        final Point2D pos = new Point2D.Float(this.getX(), this.getEndPointY());
        return pos;
    }

    public Point2D getHorizontalMiddle() {
        final Point2D pos = new Point2D.Float(this.getX()
                + this.getMagnification() * this.getWidth() / 2.0f, this.getY());
        return pos;
    }

    public Point2D getVerticalMiddle() {
		final Point2D pos = new Point2D.Float(this.getX(), this.getY()
				+ this.getMagnification() * this.getHeight() / 2.0f);
		return pos;
    }

    protected Rectangle2D getLineBounds() {
        final SGDrawingElementLine2D hline = (SGDrawingElementLine2D) this
                .getHorizontalLine();
        final SGDrawingElementLine2D vline = (SGDrawingElementLine2D) this
                .getVerticalLine();
        ArrayList<Rectangle2D> rectList = new ArrayList<Rectangle2D>();
        rectList.add(hline.getLineShape().getBounds2D());
        rectList.add(vline.getLineShape().getBounds2D());
        Rectangle2D rect = SGUtility.createUnion(rectList);
        return rect;
    }

    public Rectangle2D getHorizontalStringBounds() {
        return ((SGDrawingElementString2DExtended) this.getHorizontalStringElement())
                .getElementBounds();
    }

    public Rectangle2D getVerticalStringBounds() {
        return ((SGDrawingElementString2DExtended) this.getVerticalStringElement())
                .getElementBounds();
    }

    public Rectangle2D getElementBounds() {
        ArrayList<Rectangle2D> rectList = new ArrayList<Rectangle2D>();
        rectList.add(this.getLineBounds());
        rectList.add(this.getHorizontalStringBounds());
        rectList.add(this.getVerticalStringBounds());
        Rectangle2D rect = SGUtility.createUnion(rectList);
        return rect;
    }

    /**
     * Paint this object.
     * 
     * @param g2d
     *            graphics context
     */
    @Override
    public void paint(final Graphics2D g2d) {
        g2d.setPaint(this.getColor());

        // lines
        final float lineWidth = this.getMagnification() * this.getLineWidth();
        g2d.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER));
        final Shape hLineShape = ((SGDrawingElementLine2D) this
                .getHorizontalLine()).getLineShape();
        final Shape vLineShape = ((SGDrawingElementLine2D) this
                .getVerticalLine()).getLineShape();
        
        // string
        SGDrawingElementString2DExtended hStr = (SGDrawingElementString2DExtended) this
                .getHorizontalStringElement();
        SGDrawingElementString2DExtended vStr = (SGDrawingElementString2DExtended) this
                .getVerticalStringElement();
        
        if (this.isHorizontalVisible() && this.isVerticalVisible()) {
            GeneralPath gp = new GeneralPath();
            gp.append(hLineShape, true);
            gp.append(vLineShape, true);
            g2d.draw(gp);
            hStr.paint(g2d);
            vStr.paint(g2d);
        } else {
        	if (this.isHorizontalVisible()) {
        		g2d.draw(hLineShape);
                hStr.paint(g2d);
        	}
        	if (this.isVerticalVisible()) {
        		g2d.draw(vLineShape);
                vStr.paint(g2d);
        	}
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
    @Override
    public void paint(final Graphics2D g2d, final Rectangle2D clipRect) {
        this.paint(g2d);
    }

}
