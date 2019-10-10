package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGGradationPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGPatternPaint;
import jp.riken.brain.ni.samuraigraph.base.SGSelectablePaint;
import jp.riken.brain.ni.samuraigraph.base.SGTransparentPaint;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;

public class SGSimpleRectangle2D extends SGDrawingElementRectangle2D {

    /**
     * X coordinate of this rectangle.
     */
    protected float mX;

    /**
     * Y coordinate of this rectangle.
     */
    protected float mY;

    /**
     * The magnification.
     */
    protected float mMagnification = 1.0f;

    /**
     * Width of the rectangle. The negative value is permitted.
     */
    protected float mWidth;

    /**
     * Height of the rectangle. The negative value is permitted.
     */
    protected float mHeight;

    /**
     * The type of edge line.
     */
    protected int mEdgeLineType;

    /**
     * Color of edge line.
     */
    protected Color mEdgeLineColor;

    /**
     * Visibility of edge line.
     */
    protected boolean mEdgeLineVisible;
    
    /**
     * The inner paint.
     */
    protected SGIPaint mInnerPaint = new SGSelectablePaint();

    /**
     * The line stroke.
     */
    protected SGStroke mStroke = new SGStroke();

    public SGSimpleRectangle2D() {
        super();
        
        // sets the join of the line stroke
        this.mStroke.setJoin(BasicStroke.JOIN_MITER);
    }

    /**
     * Dispose this rectgangle.
     */
    public void dispose() {
        super.dispose();
        this.mEdgeLineColor = null;
        this.mStroke = null;
        this.mInnerPaint = null;
    }

    /**
     * Returns the width of this recgtangle.
     * 
     * @return the width of this recgtangle
     */
    public float getWidth() {
        return this.mWidth;
    }

    /**
     * Returns the height of this recgtangle.
     * 
     * @return the height of this recgtangle
     */
    public float getHeight() {
        return this.mHeight;
    }

    /**
     * Returns the edge line width.
     * 
     * @return the edge line width
     */
    public float getEdgeLineWidth() {
      return this.mStroke.getLineWidth();
    }

    /**
     * Returns the edge line color.
     * 
     * @return the edge line color
     */
    public Color getEdgeLineColor() {
        return this.mEdgeLineColor;
    }
    
    @Override
    public boolean isEdgeLineVisible() {
        return this.mEdgeLineVisible;
    }

    @Override
    public SGIPaint getInnerPaint() {
        try {
            return (SGIPaint)this.mInnerPaint.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());      
        }
    }

    /**
     * Returns the edge line type.
     * 
     * @return the edge line type
     */
    public int getEdgeLineType() {
        return this.mEdgeLineType;
    }

    @Override
    public float getTransparency() {
        return this.mInnerPaint.getAlpha();
    }

    /**
     * Sets the width of this rectangle.
     * 
     * @param w
     *          the width to set
     */
    public boolean setWidth(final float w) {
        this.mWidth = w;
        return true;
    }

    /**
     * Sets the height of this rectangle.
     * 
     * @param h
     *          the height to set
     */
    public boolean setHeight(final float h) {
        this.mHeight = h;
        return true;
    }

    /**
     * Sets the edge line width.
     * 
     * @param lw
     *           line width to set
     * @return true if succeeded
     */
    public boolean setEdgeLineWidth(final float lw) {
        this.mStroke.setLineWidth(lw);
        return true;
    }

    /**
     * Sets the edge line type.
     * 
     * @param type
     *           the line type
     * @return true if succeeded
     */
    public boolean setEdgeLineType(final int type) {
    	if (SGDrawingElementLine.isValidLineType(type) == false) {
    		return false;
    	}
        this.mEdgeLineType = type;
        this.mStroke.setLineType(type);
        return true;
    }

    /**
     * Sets the edge line color.
     * 
     * @param color
     *           the color to set
     * @return true if succeeded
     */
    public boolean setEdgeLineColor(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        this.mEdgeLineColor = color;
        return true;
    }
    
    /**
     * Sets the visibility of the edge line.
     * 
     * @param visible
     *           the visibility to set
     * @return true if succeeded
     */
    public boolean setEdgeLineVisible(final boolean visible) {
        this.mEdgeLineVisible = visible;
        return true;
    }

    @Override
    public boolean setInnerPaint(final SGIPaint paint) {
        if (null==paint) {
            throw new IllegalArgumentException("paint == null");
        }
        this.mInnerPaint = paint;
        return true;
    }
    
    /**
     * Sets the paint style index of inner paint.
     * 
     * @param style
     *           the style index to set
     * @return true if succeeded
     */
    public boolean setInnerPaintStyle(final int style) {
        if (this.mInnerPaint instanceof SGSelectablePaint) {
            return ((SGSelectablePaint)this.mInnerPaint).setSelectedPaintStyle(style);
        }
        return false;
    }
    
    /**
     * Sets the inner filled color.
     * 
     * @param color
     *           the color to set
     * @return true if succeeded
     */
    public boolean setInnerFillColor(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        if (this.mInnerPaint instanceof SGFillPaint) {
            return ((SGFillPaint)this.mInnerPaint).setColor(color);
        } else if (this.mInnerPaint instanceof SGSelectablePaint) {
            return ((SGSelectablePaint)this.mInnerPaint).setFillColor(color);
        }
        return false;
    }
    
    /**
     * Sets the color of inner pattern.
     * 
     * @param color
     *           the color to set
     * @return true if succeeded
     */
    public boolean setInnerPatternColor(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        if (this.mInnerPaint instanceof SGPatternPaint) {
            return ((SGPatternPaint)this.mInnerPaint).setColor(color);
        } else if (this.mInnerPaint instanceof SGSelectablePaint) {
            return ((SGSelectablePaint)this.mInnerPaint).setPatternColor(color);
        }
        return false;
    }
    
    /**
     * Sets the pattern index of inner paint.
     * 
     * @param type
     *           the type index to set
     * @return true if succeeded
     */
    public boolean setInnerPatternType(final int type) {
        if (this.mInnerPaint instanceof SGPatternPaint) {
            return ((SGPatternPaint)this.mInnerPaint).setTypeIndex(type);
        } else if (this.mInnerPaint instanceof SGSelectablePaint) {
            return ((SGSelectablePaint)this.mInnerPaint).setPatternIndex(type);
        }
        return false;
    }
    
    /**
     * Sets the first color of inner gradation.
     * 
     * @param color
     *           the color to set
     * @return true if succeeded
     */
    public boolean setInnerGradationColor1(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        if (this.mInnerPaint instanceof SGGradationPaint) {
            return ((SGGradationPaint)this.mInnerPaint).setColor1(color);
        } else if (this.mInnerPaint instanceof SGSelectablePaint) {
            return ((SGSelectablePaint)this.mInnerPaint).setGradationColor1(color);
        }
        return false;
    }
    
    /**
     * Sets the second color of inner gradation.
     * 
     * @param color
     *           the color to set
     * @return true if succeeded
     */
    public boolean setInnerGradationColor2(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        if (this.mInnerPaint instanceof SGGradationPaint) {
            return ((SGGradationPaint)this.mInnerPaint).setColor2(color);
        } else if (this.mInnerPaint instanceof SGSelectablePaint) {
            return ((SGSelectablePaint)this.mInnerPaint).setGradationColor2(color);
        }
        return false;
    }
    
    /**
     * Sets the direction index of inner gradation paint.
     * 
     * @param direction
     *           the direction index to set
     * @return true if succeeded
     */
    public boolean setInnerGradationDirection(final int direction) {
        if (this.mInnerPaint instanceof SGGradationPaint) {
            return ((SGGradationPaint)this.mInnerPaint).setDirection(direction);
        } else if (this.mInnerPaint instanceof SGSelectablePaint) {
            return ((SGSelectablePaint)this.mInnerPaint).setGradationDirection(direction);
        }
        return false;
    }
    
    /**
     * Sets the order index of inner gradation paint.
     * 
     * @param order
     *           the order index to set
     * @return true if succeeded
     */
    public boolean setInnerGradationOrder(final int order) {
        if (this.mInnerPaint instanceof SGGradationPaint) {
            return ((SGGradationPaint)this.mInnerPaint).setOrder(order);
        } else if (this.mInnerPaint instanceof SGSelectablePaint) {
            return ((SGSelectablePaint)this.mInnerPaint).setGradationOrder(order);
        }
        return false;
    }
    
    @Override
    public boolean setTransparent(final float alpha) {
        return this.mInnerPaint.setAlpha(alpha);
    }
    
    public boolean setTransparent(final int percentAlpha) {
        return ((SGTransparentPaint)this.mInnerPaint).setTransparency(percentAlpha);
    }

    /**
     * Sets the magnification.
     * 
     * @param mag
     *          the magnification
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
//        if (super.setMagnification(ratio) == false) {
//            return false;
//        }
        if (mag <= 0.0) {
            throw new IllegalArgumentException("mag <= 0.0");
        }
        this.mMagnification = mag;
        this.mStroke.setMagnification(mag);
        this.mInnerPaint.setMagnification(mag);
        return true;
    }
    
//    /**
//     * Zoom this rectangle.
//     * 
//     * @param ratio
//     *          the magnification
//     * @return true if succeeded
//     */
//    public boolean zoom(final float ratio) {
//        if (super.zoom(ratio) == false) {
//            return false;
//        }
//        this.mStroke.setMagnification(ratio);
//        return true;
//    }

    /**
     * Returns a stroke.
     * 
     * @return a stroke
     */
    protected SGStroke getStroke() {
        return this.mStroke;
    }

    /**
     * Returns the magnification.
     * 
     * @return the magnification
     */
    public float getMagnification() {
        return this.mMagnification;
    }

    @Override
    public float getX() {
        return this.mX;
    }

    @Override
    public float getY() {
        return this.mY;
    }

    @Override
    public boolean setX(float x) {
        this.mX = x;
        return true;
    }

    @Override
    public boolean setY(float y) {
        this.mY = y;
        return true;
    }

}
