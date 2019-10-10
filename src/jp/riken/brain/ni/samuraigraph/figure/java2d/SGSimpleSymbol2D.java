package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Shape;

import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;

public class SGSimpleSymbol2D extends SGDrawingElementSymbol2D {

    /**
     * The location.
     */
    protected SGTuple2f mLocation = new SGTuple2f();
    
    /**
     * The magnification.
     */
    protected float mMagnification = 1.0f;

    /**
     * The size.
     */
    private float mSize;

    /**
     * The symbol type.
     */
    private int mType;

    /**
     * The line color.
     */
    private Color mLineColor;

    /**
     * The inner paint.
     */
    private SGFillPaint mInnerPaint = new SGFillPaint();

    /**
     * The line width.
     */
    private float mLineWidth;

    /**
     * The line visibility.
     */
    private boolean mLineVisible;
    
    /**
     * The angle.
     */
    private float mAngle = 0.0f;

    /**
     * The shape.
     */
    private Shape mShape = null;

    /**
     * The default constructor.
     *
     */
    public SGSimpleSymbol2D() {
        super();
    }
    
    /**
     * Dispose this object.
     * 
     */
    public void dispose() {
        super.dispose();
        this.mLocation = null;
        this.mInnerPaint = null;
        this.mLineColor = null;
        this.mShape = null;
    }

    /**
     * Sets the size.
     * 
     * @param size
     *          the size to set
     * @return true if succeeded
     */
    public boolean setSize(final float size) {
        this.mSize = size;
        this.updateShape();
        return true;
    }

    /**
     * Sets the symbol type.
     * 
     * @param type
     *          the symbol type to set
     * @return true if succeeded
     */
    public boolean setType(final int type) {
        this.mType = type;
        this.updateShape();
        return true;
    }

    /**
     * Sets the line color.
     * 
     * @param color
     *           the line colot to set
     * @return true if succeeded
     */
    public boolean setLineColor(final Color color) {
        this.mLineColor = color;
        return true;
    }

    /**
     * Sets the line width.
     * 
     * @param lineWidth
     *           the line width to set
     * @return true if succeeded
     */
    public boolean setLineWidth(final float lineWidth) {
        this.mLineWidth = lineWidth;
        return true;
    }

    @Override
    public boolean setLineVisible(boolean visible) {
        this.mLineVisible = visible;
        return true;
    }

    /**
     * Returns the size.
     * 
     * @return the size
     */
    public float getSize() {
        return this.mSize;
    }

    /**
     * Returns the symbol type.
     * 
     * @return the symbol type
     */
    public int getType() {
        return this.mType;
    }

    /**
     * Returns the line color.
     * 
     * @return the line color
     */
    public Color getLineColor() {
        return this.mLineColor;
    }

    /**
     * Returns the line width.
     * 
     * @return the line width
     */
    public float getLineWidth() {
        return this.mLineWidth;
    }

    @Override
    public boolean isLineVisible() {
        return this.mLineVisible;
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
     *           the magnification to set
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        if (mag <= 0.0) {
            throw new IllegalArgumentException("mag <= 0.0");
        }
        this.mMagnification = mag;
        this.updateShape();
        return true;
    }

    /**
     * Returns the inner color.
     * 
     * @return the inner color
     */
    @Override
    public SGIPaint getInnerPaint() {
        return this.mInnerPaint;
    }

    /**
     * Sets the inner color.
     * 
     * @param color
     *           the inner color to set
     * @return true if succeeded
     */
    public boolean setInnerColor(Color color) {
        return this.mInnerPaint.setColor(color);
    }

  /**
    * Returns the angle.
    * 
    * @return the angle
    */
   public float getAngle() {
       return this.mAngle;
   }

   /**
    * Sets the angle.
    * 
    * @param angle
    *           the angle to set
    * @return true if succeeded
    */
   public boolean setAngle(final float angle) {
       this.mAngle = angle;
       return true;
   }

    @Override
    public SGTuple2f getLocation() {
        return new SGTuple2f(this.mLocation);
    }
    
    @Override
    public boolean setLocation(float x, float y) {
        this.mLocation.setValues(x, y);
        return true;
    }
    
    @Override
    public boolean setLocation(SGTuple2f point) {
        this.mLocation.setValues(point);
        return true;
    }
    
    @Override
    public boolean setX(float x) {
        this.mLocation.x = x;
        return true;
    }
    
    @Override
    public boolean setY(float y) {
        this.mLocation.y = y;
        return true;
    }

    /**
     * Returns the shape.
     * 
     * @return a shape object
     */
    protected Shape getShape() {
    	return this.mShape;
    }

    /**
     * Updates the shape.
     *
     */
    protected void updateShape() {
    	this.mShape = this.createShape();
    }

}
