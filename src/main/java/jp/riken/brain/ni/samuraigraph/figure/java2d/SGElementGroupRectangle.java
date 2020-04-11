package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGGradationPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGPatternPaint;
import jp.riken.brain.ni.samuraigraph.base.SGSelectablePaint;
import jp.riken.brain.ni.samuraigraph.base.SGTransparentPaint;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementRectangle;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIRectangleConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGPaintUtility;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;

import org.w3c.dom.Element;

/**
 * 
 */
public abstract class SGElementGroupRectangle extends SGElementGroup
        implements SGIRectangleConstants, SGIFigureDrawingElementConstants {

    /**
     * The line stroke for edge lines.
     */
    protected SGStroke mStroke = new SGStroke();

    /**
     * The width of each rectangle.
     */
    protected float mRectangleWidth;

    /**
     * The height of each rectangle.
     */
    protected float mRectangleHeight;

    /**
     * The edge line color.
     */
    protected Color mEdgeLineColor;
    
    /**
     * The visibility of the edge line.
     */
    private boolean mEdgeLineVisible;

    /**
     * The inner paint of symbols.
     */
    protected SGIPaint mInnerPaint = new SGFillPaint();

    /**
     * The default construcor.
     */
    public SGElementGroupRectangle() {
        super();
        
        // sets the join of the line stroke
        this.mStroke.setJoin(BasicStroke.JOIN_MITER);
    }

    /**
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();
        this.mEdgeLineColor = null;
        this.mStroke = null;
        this.mInnerPaint = null;
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
     * Retruns the x-coordinate of the rectangle at a given index.
     * 
     * @param index
     *           the index
     * @return the x-coordinate
     */
    public abstract float getX(final int index);

    /**
     * Retruns the y-coordinate of the rectangle at a given index.
     * 
     * @param index
     *           the index
     * @return the y-coordinate
     */
    public abstract float getY(final int index);

    /**
     * Returns the width of a rectangle.
     * 
     * @return the width of a rectangle
     */
    public float getRectangleWidth() {
        return this.mRectangleWidth;
    }

    /**
     * 
     * @param unit
     * @return
     */
    public float getRectangleWidth(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getRectangleWidth(),
                unit);
    }

    /**
     * Returns the height of a rectangle.
     * 
     * @return the height of a rectangle
     */
    public float getRectangleHeight() {
        return this.mRectangleHeight;
    }

    /**
     * 
     * @param unit
     * @return
     */
    public float getRectangleHeight(final String unit) {
        return (float) SGUtilityText.convertFromPoint(
                this.getRectangleHeight(), unit);
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
     * 
     * @param unit
     * @return
     */
    public float getEdgeLineWidth(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getEdgeLineWidth(),
                unit);
    }

    /**
     * Returns the edge line color.
     * 
     * @return the edge line color
     */
    public Color getEdgeLineColor() {
        return this.mEdgeLineColor;
    }
    
    /**
     * Returns the edge line visibility.
     * @return the edge line visibility
     */
    public boolean isEdgeLineVisible() {
        return this.mEdgeLineVisible;
    }
    
    /**
     * Returns the inner paint.
     * 
     * @return the inner paint
     */
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
        return this.mStroke.getLineType();
    }

    /**
     * Returns the transparent of this rectangle.
     * 
     * @return alpha value
     */
    public float getTransparency() {
        return this.mInnerPaint.getAlpha();
    }

    /**
     * 
     * @param value
     * @return
     */
    public boolean setRectangleWidth(final float value) {
        this.mRectangleWidth = value;
        return true;
    }

    /**
     * 
     * @param width
     * @param unit
     * @return
     */
    public boolean setRectangleWidth(final float width, final String unit) {
        final double w = SGUtilityText.convertToPoint(width, unit);
        if (this.setRectangleWidth((float) w) == false) {
            return false;
        }

        return true;
    }

    /**
     * 
     * @param value
     * @return
     */
    public boolean setRectangleHeight(final float value) {
        this.mRectangleHeight = value;
        return true;
    }

    /**
     * 
     * @param height
     * @param unit
     * @return
     */
    public boolean setRectangleHeight(final float height, final String unit) {
        final double h = SGUtilityText.convertToPoint(height, unit);
        if (this.setRectangleHeight((float) h) == false) {
            return false;
        }

        return true;
    }

    /**
     * Sets the edge line width.
     * 
     * @param lw
     *          the edge line width to set
     * @return true if succeeded
     */
    public boolean setEdgeLineWidth(final float lw) {
        if (lw < 0.0f) {
            throw new IllegalArgumentException("lw < 0.0f");
        }
        this.mStroke.setLineWidth(lw);
        return true;
    }

    /**
     * Sets the edge line width in a given unit.
     * 
     * @param lw
     *           the edge line width to set
     * @param unit
     *           the unit for given line width
     * @return true if succeeded
     */
    public abstract boolean setEdgeLineWidth(final float lw, final String unit);

    /**
     * Sets the line color.
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
     * 
     */
    public boolean setEdgeLineColor(final String value) {
        final Color cl = SGUtilityText.getColor(value);
        if (cl == null) {
            return false;
        }
        return this.setEdgeLineColor(cl);
    }
    
    /**
     * Sets the edge line visibility.
     * @param visible
     *           the visibility to set
     * @return true if succeeded
     */
    public boolean setEdgeLineVisible(final boolean visible) {
        this.mEdgeLineVisible = visible;
        return true;
    }

    /**
     * Sets the inner paint.
     * 
     * @param paint
     *           the paint to set
     * @return true if succeeded
     */
    public boolean setInnerPaint(final SGIPaint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("paint == null");
        }
        this.mInnerPaint = paint;
        this.mInnerPaint.setMagnification(this.mMagnification);
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

    /**
     * 
     * @param r
     * @param g
     * @param b
     * @return
     */
    public boolean setEdgeLineColor(final String r, final String g,
            final String b) {
        final Color cl = SGUtilityText.getColor(r, g, b);
        if (cl == null) {
            return false;
        }
        return this.setEdgeLineColor(cl);
    }

//    /**
//     * 
//     */
//    public SGDrawingElement getDrawingElement() {
//        SGDrawingElementRectangle rect = (SGDrawingElementRectangle) this
//                .createDrawingElementInstance();
//        rect.setWidth(this.getRectangleWidth());
//        rect.setHeight(this.getRectangleHeight());
//        rect.setVisible(this.isVisible());
//        rect.setEdgeLineWidth(this.getEdgeLineWidth());
//        rect.setEdgeLineColor(this.getEdgeLineColor());
//        rect.setColorList(this.getColorList());
//
//        return rect;
//    }

    /**
     * Sets the alpha value of this rectangle's body.
     * 
     * @param alpha
     *          alpha value to set transparent
     */
    public boolean setTransparent(final float alpha) {
        return this.mInnerPaint.setAlpha(alpha);
    }
    
    public boolean setTransparentPercent(final int percentAlpha) {
        return ((SGTransparentPaint)this.mInnerPaint).setTransparency(percentAlpha);
    }

    /**
     * 
     * @return
     */
    protected SGDrawingElement createDrawingElementInstance(final int index) {
        return new RectInGroup(this, index);
    }
    
    /**
     * 
     * @return
     */
    protected boolean initDrawingElement(final SGTuple2f[] array) {
        final int num = array.length;
        if (this.initDrawingElement(num) == false) {
            return false;
        }

        SGDrawingElement[] bArray = this.mDrawingElementArray;
        for (int ii = 0; ii < bArray.length; ii++) {
            ((SGDrawingElementRectangle) bArray[ii]).setLocation(array[ii].x,
                    array[ii].y);
        }

        return true;
    }

//    /**
//     * 
//     */
//    public boolean setProperty(final SGDrawingElement element) {
//        if (!(element instanceof SGDrawingElementRectangle)) {
//            return false;
//        }
//
//        if (super.setProperty(element) == false) {
//            return false;
//        }
//
//        SGDrawingElementRectangle rect = (SGDrawingElementRectangle) element;
//        this.setRectangleWidth(rect.getWidth());
//        this.setRectangleHeight(rect.getHeight());
//        this.setEdgeLineWidth(rect.getEdgeLineWidth());
//        this.setEdgeLineColor(rect.getEdgeLineColor());
//
//        return true;
//    }

//    /**
//     * Zoom this line group.
//     */
//    public boolean zoom(final float mag) {
//        if (super.zoom(mag) == false) {
//            return false;
//        }
//        this.mStroke.setMagnification(mag);
//        return true;
//    }

    // Set magnification.
    public boolean setMagnification(final float mag) {
        if (super.setMagnification(mag) == false) {
            return false;
        }
        this.mStroke.setMagnification(mag);
        this.mInnerPaint.setMagnification(mag);
        return true;
    }

    /**
     * 
     */
    @Override
    public boolean writeProperty(final Element el) {
	
        final int digitRectSize = RECT_SIZE_MINIMAL_ORDER - 1;
        final int digitLineWidth = SGIConstants.LINE_WIDTH_MINIMAL_ORDER - 1;
	
        final float rectWidth = (float) SGUtilityNumber.roundOffNumber(
        	this.mRectangleWidth * SGIConstants.CM_POINT_RATIO, digitRectSize);
        final float rectHeight = (float) SGUtilityNumber.roundOffNumber(
        	this.mRectangleHeight * SGIConstants.CM_POINT_RATIO, digitRectSize);
        final float edgeLineWidth = (float) SGUtilityNumber.roundOffNumber(
        	this.getEdgeLineWidth(), digitLineWidth);

        el.setAttribute(KEY_RECTANGLE_WIDTH, Float.toString(rectWidth) + SGIConstants.cm);
        el.setAttribute(KEY_RECTANGLE_HEIGHT, Float.toString(rectHeight) + SGIConstants.cm);
        el.setAttribute(KEY_EDGE_LINE_WIDTH, Float.toString(edgeLineWidth) + SGIConstants.pt);
//        el.setAttribute(KEY_COLOR_LIST, SGUtilityText.getColorListString(this.mColorList));
        el.setAttribute(KEY_EDGE_LINE_COLOR, SGUtilityText.getColorString(this.mEdgeLineColor));
        el.setAttribute(KEY_EDGE_LINE_VISIBLE, Boolean.toString(this.mEdgeLineVisible));
//        if (! SGPaintUtility.writeProperty(el, this.mInnerPaint)) {
//            return false;
//        }
    	if (!this.mInnerPaint.writeProperty(el)) {
    		return false;
    	}

        return true;
    }

    /**
     * 
     * @param el
     * @return
     */
    @Override
    public boolean readProperty(final Element el) {
        // final float ratio = SGIConstants.CM_POINT_RATIO;

        String str = null;
        Number num = null;
        Color cl = null;
        Boolean b = null;
        // List list = null;

        // width of the bar
        str = el.getAttribute(KEY_RECTANGLE_WIDTH);
        if (str.length() != 0) {
            // The attribute of width-value is added from version 0.9.1
            // This code is for the old versions.

            str = el.getAttribute(KEY_RECTANGLE_WIDTH);
            if (str.length() == 0) {
                return false;
            }
            StringBuffer uWidth = new StringBuffer();
            num = SGUtilityText.getNumber(str, uWidth);
            if (num == null) {
                return false;
            }

            if (this.setRectangleWidth(num.floatValue(), uWidth.toString()) == false) {
                return false;
            }
        }

        SGIPaint paint = SGPaintUtility.readProperty(el);
        if (null != paint) {
            this.setInnerPaint(paint);
        } else {
            return false;
        }

        // edge line width
        str = el.getAttribute(KEY_EDGE_LINE_WIDTH);
        if (str.length() != 0) {
            StringBuffer uEdgeLineWidth = new StringBuffer();
            num = SGUtilityText.getNumber(str, uEdgeLineWidth);
            if (num == null) {
                return false;
            }
            final float edgeLineWidth = num.floatValue();
            if (this.setEdgeLineWidth(edgeLineWidth, uEdgeLineWidth.toString()) == false) {
                return false;
            }
        }

        // edge line color
        str = el.getAttribute(KEY_EDGE_LINE_COLOR);
        if (str.length() != 0) {
            cl = SGUtilityText.parseColor(str);
            if (cl == null) {
                return false;
            }
            final Color edgeLineColor = cl;
            if (this.setEdgeLineColor(edgeLineColor) == false) {
                return false;
            }
        }
        
        // edge line visibility
        str = el.getAttribute(KEY_EDGE_LINE_VISIBLE);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return false;
            }
            if (this.setEdgeLineVisible(b.booleanValue()) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * A rectangle in a group.
     * @author kuromaru
     *
     */
    protected static class RectInGroup extends SGDrawingElementRectangle2D {
        
        // a group of rectangles
        protected SGElementGroupRectangle mGroup = null;
        
        protected int mIndex = -1;

        /**
         * Builds a rectangle in a group of rectangles.
         * 
         * @param group
         *           a group of rectangles
         */
        public RectInGroup(SGElementGroupRectangle group, final int index) {
            super();
            this.mGroup = group;
            this.mIndex = index;
        }

        /**
         * Dispose this object.
         */
        @Override
        public void dispose() {
            super.dispose();
            this.mGroup = null;
        }

        @Override
        public Color getEdgeLineColor() {
            return this.mGroup.getEdgeLineColor();
        }

        @Override
        public int getEdgeLineType() {
            return this.mGroup.getEdgeLineType();
        }

        @Override
        public float getEdgeLineWidth() {
            return this.mGroup.getEdgeLineWidth();
        }

        @Override
        public boolean isEdgeLineVisible() {
            return this.mGroup.isEdgeLineVisible();
        }

        @Override
        public float getHeight() {
            return this.mGroup.getRectangleHeight();
        }

        @Override
        public float getWidth() {
            return this.mGroup.getRectangleWidth();
        }

        @Override
        public float getTransparency() {
            return this.mGroup.getTransparency();
        }

        @Override
        public boolean setEdgeLineColor(Color color) {
            // do nothing
            return true;
        }

        @Override
        public boolean setEdgeLineType(int type) {
            // do nothing
            return true;
        }

        @Override
        public boolean setEdgeLineWidth(float width) {
            // do nothing
            return true;
        }

        @Override
        public boolean setEdgeLineVisible(boolean visible) {
            // do nothing
            return false;
        }

        @Override
        public boolean setHeight(float h) {
            // do nothing
            return true;
        }

        @Override
        public boolean setTransparent(float alpha) {
            // do nothing
            return true;
        }

        @Override
        public boolean setWidth(float w) {
            // do nothing
            return true;
        }

        @Override
        protected SGStroke getStroke() {
            return this.mGroup.getStroke();
        }

        @Override
        public float getMagnification() {
            return this.mGroup.getMagnification();
        }

        @Override
        public boolean setMagnification(float mag) {
            // do nothing
            return true;
        }

        @Override
        public SGIPaint getInnerPaint() {
            return this.mGroup.getInnerPaint();
        }

        @Override
        public boolean setInnerPaint(SGIPaint paint) {
            // do nothing
            return true;
        }

        @Override
        public float getX() {
            return this.mGroup.getX(this.mIndex);
        }

        @Override
        public float getY() {
            return this.mGroup.getY(this.mIndex);
        }

        @Override
        public boolean setX(float x) {
            // do nothing
            return true;
        }

        @Override
        public boolean setY(float y) {
            // do nothing
            return true;
        }
        
        public int getIndex() {
        	return this.mIndex;
        }
    }

}
