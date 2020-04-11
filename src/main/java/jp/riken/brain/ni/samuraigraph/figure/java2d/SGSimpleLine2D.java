package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;

public abstract class SGSimpleLine2D extends SGDrawingElementLine2D {

    /**
     * The start point.
     */
    protected SGTuple2f mStartPoint = new SGTuple2f();

    /**
     * The end point.
     */
    protected SGTuple2f mEndPoint = new SGTuple2f();

    /**
     * The magnification.
     */
    protected float mMagnification = 1.0f;

    /**
     * The color.
     */
    protected Color mColor = null;
    
    /**
     * The stroke.
     */
    protected SGStroke mStroke = new SGStroke();
    
    /**
     * Construct a new line object.
     */
    public SGSimpleLine2D() {
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
    public SGSimpleLine2D(final SGTuple2f start, final SGTuple2f end) {
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
    public SGSimpleLine2D(final float x1, final float y1,
            final float x2, final float y2) {
        super(x1, y1, x2, y2);
    }

    /**
     * Dispose this line object.
     */
    public void dispose() {
        super.dispose();
        this.mColor = null;
        this.mStroke = null;
        this.mStartPoint = null;
        this.mEndPoint = null;
    }
    
    /**
     * Returns the magnification.
     * 
     * @return the magnification
     */
    public float getMagnification() {
        return this.mMagnification;
    }

    /**
     * Sets the magnification.
     * 
     * @param mag
     *          the magnification
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        if (mag <= 0.0) {
            throw new IllegalArgumentException("mag <= 0.0");
        }
        this.mMagnification = mag;
        this.mStroke.setMagnification(mag);
        return true;
    }

    /**
     * Returns a stroke.
     * 
     * @return a stroke
     */
    protected SGStroke getStroke() {
        return this.mStroke;
    }

    /**
     * Set the line width.
     * 
     * @param width
     *            line width to set
     * @param unit
     *            unit for given line width
     * @return
     */
    public boolean setLineWidth(final float width, final String unit) {
        final double lw = SGUtilityText.convertToPoint(width, unit);
        if (this.setLineWidth((float) lw) == false) {
            return false;
        }
        return true;
    }

    /**
     * Sets the line width.
     * 
     * @param width
     *            line width to set
     * @return true if succeeded
     */
    public boolean setLineWidth(final float width) {
        this.getStroke().setLineWidth(width);
        return true;
    }

    /**
     * Sets the line type.
     * 
     * @param type
     *           line type
     * @return true if succeeded
     */
    public boolean setLineType(final int type) {
        this.getStroke().setLineType(type);
        return true;
    }

    /**
     * Returns the color.
     * 
     * @return the line color
     */
    public Color getColor() {
        return this.mColor;
    }
    
    /**
     * Sets the color.
     * 
     * @param cl
     *          the color to set
     * @return true if succeeded
     */
    public boolean setColor(final Color cl) {
        this.mColor = cl;
        return true;
    }

    /**
     * Sets the properties.
     * 
     * @param p
     *         properties bo set
     * @return true if succeeded
     */
    public boolean setProperties(SGProperties p) {
        if ((p instanceof LineProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        LineProperties lp = (LineProperties) p;
        Float width = lp.getLineWidth();
        if (width == null) {
            return false;
        }
        Integer type = lp.getLineType();
        if (type == null) {
            return false;
        }
        this.setLineWidth(width.floatValue());
        this.setLineType(type.intValue());
        return true;
    }

    /**
     * Set coordinates of the end points of this line.
     * 
     * @param start
     *            coordinate of the start point
     * @param end
     *            coordinate of the end point
     */
    public boolean setTermPoints(final SGTuple2f start, final SGTuple2f end) {
        this.mStartPoint = start;
        this.mEndPoint = end;
        return true;
    }

    /**
     * Returns the coordinates of the start point of this line.
     * 
     * @return the coordinates of the start point of this line
     */
    public SGTuple2f getStart() {
        return this.mStartPoint;
    }

    /**
     * Returns the coordinates of the end point of this line.
     * 
     * @return the coordinates of the end point of this line
     */
    public SGTuple2f getEnd() {
        return this.mEndPoint;
    }

}
