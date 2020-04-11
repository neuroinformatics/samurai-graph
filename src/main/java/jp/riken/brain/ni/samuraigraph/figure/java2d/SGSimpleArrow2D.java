package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Shape;

import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementArrow;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementSymbol;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;



/**
 * The base class for a simple arrow with Java2D.
 * @author kuromaru
 *
 */
public abstract class SGSimpleArrow2D extends SGDrawingElementArrow2D {

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
     * The start head type.
     */
    protected int mStartHeadType;

    /**
     * The end head type.
     */
    protected int mEndHeadType;

    /**
     * The line type.
     */
    protected int mLineType;
    
    /**
     * The head size.
     */
    protected float mHeadSize;
    
    /**
     * Open angle of the arrow.
     */
    protected float mHeadOpenAngle;

    /**
     * Close angle of the arrow.
     */
    protected float mHeadCloseAngle;

    /**
     * The stroke.
     */
    protected SGStroke mStroke = new SGStroke();

    /**
     * The shape of the start head.
     */
    protected Shape mStartHeadShape = null;
    
    /**
     * The shape of the end head.
     */
    protected Shape mEndHeadShape = null;

    /**
     * The default constructor.
     *
     */
    public SGSimpleArrow2D() {
        super();
        this.mLine = this.createBodyInstance();
        this.mStartHead = this.createHeadInstance(this, true);
        this.mEndHead = this.createHeadInstance(this, false);
    }
    
    /**
     * Dispose this line object.
     */
    public void dispose() {
        super.dispose();
        this.mStroke = null;
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
     *           the magnification
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        if (mag <= 0.0) {
            throw new IllegalArgumentException("mag <= 0.0");
        }
        this.mMagnification = mag;
        this.mStroke.setMagnification(mag);
        this.updateHeadAngle();
        this.updateHeadShape();
        return true;
    }

    /**
     * Returns the line width.
     * 
     * @return the line width
     */
    public float getLineWidth() {
        return this.mStroke.getLineWidth();
    }

    /**
     * Returns the line type.
     * 
     * @return the line type
     */
    public int getLineType() {
        return this.mLineType;
    }

    /**
     * Returns the head size.
     * 
     * @return the head size
     */
    public float getHeadSize() {
        return this.mHeadSize;
    }

    /**
     * Returns the start head type.
     * 
     * @return the start head type
     */
    public int getStartHeadType() {
        return this.mStartHeadType;
    }

    /**
     * Returns the end head type.
     * 
     * @return the end head type
     */
    public int getEndHeadType() {
        return this.mEndHeadType;
    }

    /**
     * Sets the open and close angle of the arrow head.
     * 
     * @param openAngle
     *           a value to set to the open angle
     * @param closeAngle
     *           a value to set to the close angle
     * @return true if succeeded
     */
    public boolean setHeadAngle(final Float openAngle, final Float closeAngle) {
        final Float oNew = (openAngle != null) ? SGUtility.calcPropertyValue(openAngle, 
                null, null, ARROW_HEAD_OPEN_ANGLE_MIN, ARROW_HEAD_OPEN_ANGLE_MAX, 
                ARROW_HEAD_ANGLE_MINIMAL_ORDER) : Float.valueOf(this.mHeadOpenAngle);
        if (oNew == null) {
            return false;
        }
        final Float cNew = (closeAngle != null) ? SGUtility.calcPropertyValue(closeAngle, 
                null, null, ARROW_HEAD_CLOSE_ANGLE_MIN, ARROW_HEAD_CLOSE_ANGLE_MAX, 
                ARROW_HEAD_ANGLE_MINIMAL_ORDER) : Float.valueOf(this.mHeadCloseAngle);
        if (cNew == null) {
            return false;
        }
        if (cNew <= oNew) {
            return false;
        }
        this.mHeadOpenAngle = oNew.floatValue();
        this.mHeadCloseAngle = cNew.floatValue();
        this.updateHeadAngle();
        this.updateHeadShape();
        return true;
    }

    /**
     * Returns the open angle of the arrow head.
     * 
     * @return the open angle of the arrow head
     */
    public float getHeadOpenAngle() {
        return this.mHeadOpenAngle;
    }

    /**
     * Returns the close angle of the arrow head.
     * 
     * @return the close angle of the arrow head
     */
    public float getHeadCloseAngle() {
        return this.mHeadCloseAngle;
    }
    
    /**
     * Sets the line width.
     * 
     * @param width
     *           line width to set
     * @return true if succeeded
     */
    public boolean setLineWidth(final float width) {
        this.mStroke.setLineWidth(width);
        this.updateHeadAngle();
        return true;
    }

    /**
     * Sets the line type.
     * 
     * @param type
     *           the line type to set
     * @return true if succeeded
     */
    public boolean setLineType(final int type) {
    	if (SGDrawingElementLine.isValidLineType(type) == false) {
    		return false;
    	}
        this.mLineType = type;
        this.mStroke.setLineType(type);
        return true;
    }

    /**
     * Sets the start head type.
     * 
     * @param type
     *           the start head type
     * @return true if succeeded
     */
    public boolean setStartHeadType(final int type) {
        if (SGDrawingElementArrow.isValidArrowHeadType(type) == false) {
        	return false;
        }
        this.mStartHeadType = type;
        this.updateHeadAngle();
        this.updateHeadShape();
        return true;
    }
    
    /**
     * Sets the end head type.
     * 
     * @param type
     *           the end head type
     * @return true if succeeded
     */
    public boolean setEndHeadType(final int type) {
        if (SGDrawingElementArrow.isValidArrowHeadType(type) == false) {
        	return false;
        }
        this.mEndHeadType = type;
        this.updateHeadShape();
        return true;
    }
    
    /**
     * Sets the head size.
     * 
     * @param size
     *          the head size to set
     * @return true if succeeded
     */
    public boolean setHeadSize(final float size) {
        this.mHeadSize = size;
        this.updateHeadAngle();
        this.updateHeadShape();
        return true;
    }

    /**
     * Returns the color.
     * 
     * @return the color
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
    	if (cl == null) {
    		throw new IllegalArgumentException("cl == null");
    	}
        this.mColor = cl;
        return true;
    }

    public SGStroke getStroke() {
        return this.mStroke;
    }

    @Override
    protected SGDrawingElementLine createBodyInstance() {
        return new ArrowBody(this);
    }

    /**
     * Creates and returns an instance of the head.
     * 
     * @param arrow
     *            an arrow that this arrow head belongs to
     * @param start
     *            true for the arrow head at start
     * @return an instance of the head
     */
    protected SGDrawingElementSymbol createHeadInstance(
            final SGDrawingElementArrow arrow, final boolean start) {
        return new ArrowHead(arrow, start);
    }

    @Override
    public SGTuple2f getEnd() {
        return new SGTuple2f(this.mEndPoint);
    }

    @Override
    public SGTuple2f getStart() {
        return new SGTuple2f(this.mStartPoint);
    }

    @Override
    public boolean setLocation(SGTuple2f start, SGTuple2f end) {
        this.mStartPoint.setValues(start);
        this.mEndPoint.setValues(end);
        this.updateHeadAngle();
        return true;
    }

    @Override
    public boolean setEndX(final float x) {
        this.mEndPoint.setX(x);
        this.updateHeadAngle();
        return true;
    }

    @Override
    public boolean setEndY(final float y) {
        this.mEndPoint.setY(y);
        this.updateHeadAngle();
        return true;
    }

    @Override
    public boolean setStartX(final float x) {
        this.mStartPoint.setX(x);
        this.updateHeadAngle();
        return true;
    }

    @Override
    public boolean setStartY(final float y) {
        this.mStartPoint.setY(y);
        this.updateHeadAngle();
        return true;
    }
    
    /**
     * Returns the shape of the start head.
     * 
     * @return a shape object
     */
    public Shape getStartHeadShape() {
    	return this.mStartHeadShape;
    }
    
    /**
     * Returns the shape of the end head.
     * 
     * @return a shape object
     */
    public Shape getEndHeadShape() {
    	return this.mEndHeadShape;
    }

    /**
     * Updates the head shape.
     *
     */
    protected void updateHeadShape() {
    	this.mStartHeadShape = ((SGDrawingElementSymbol2D) this.getStartHead()).createShape();
    	this.mEndHeadShape = ((SGDrawingElementSymbol2D) this.getEndHead()).createShape();
    }

}
