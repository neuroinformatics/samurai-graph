package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGSelectablePaint;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementBar;
import jp.riken.brain.ni.samuraigraph.figure.SGIBarConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGPaintUtility;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;

import org.w3c.dom.Element;

/**
 * 
 */
public abstract class SGElementGroupBar extends SGElementGroupRectangle
        implements SGIBarConstants, SGIElementGroupConstants {

    /**
     * The axis value of baseline.
     */
    protected double mBaselineValue;

    /**
     * The axis value of width of bar.
     */
    protected double mWidthValue;

    /**
     * The width of bars.
     */
    protected float mWidth;

    /**
     * A flag whether this bar is vertical.
     */
    protected boolean mVerticalFlag;
    
    /**
     * The bar shift size to x direction.
     */
    protected double mOffsetX;
    
    /**
     * The bar shift size to y direction.
     */
    protected double mOffsetY;
    
    /**
     * The bar interval size of adjacent series.
     */
    protected double mInterval;
    
    /**
     * The default constructor.
     */
    public SGElementGroupBar() {
        super();
    }
    
    /**
     * Returns the x-coordinate of the bar at given index.
     * 
     * @param index
     *           the array index
     * @return the x-coordinate of the bar
     */
    public abstract float getX(final int index);

    /**
     * Returns the y-coordinate of the bar at given index.
     * 
     * @param index
     *           the array index
     * @return the y-coordinate of the bar
     */
    public abstract float getY(final int index);

    /**
     * Returns the baseline value.
     * 
     * @return the baseline value
     */
    public double getBaselineValue() {
        return this.mBaselineValue;
    }

    /**
     * Sets the baseline value.
     * 
     * @param value
     *           axis value to set to the baseline value
     * @return true if succeeded
     */
    public boolean setBaselineValue(final double value) {
        this.mBaselineValue = value;
        return true;
    }

    /**
     * Returns the width value.
     * 
     * @return the width value
     */
    public double getWidthValue() {
        return this.mWidthValue;
    }

    /**
     * Sets the width value.
     * 
     * @param value
     *          axis value to set to the width value
     * @return true if succeeded
     */
    public boolean setWidthValue(final double value) {
    	if (value < 0.0) {
    		return false;
    	}
        this.mWidthValue = value;
        return true;
    }

    /**
     * Returns whether this bar is vertical.
     * 
     * @return true if vertical
     */
    public boolean isVertical() {
        return this.mVerticalFlag;
    }
    
    public double getOffsetX() {
        return this.mOffsetX;
    }
    
    public double getOffsetY() {
        return this.mOffsetY;
    }
    
    /**
     * Sets the shift size of bars to x direction.
     * 
     * @return true
     */
    public boolean setOffsetX(final double offset) {
        this.mOffsetX = offset;
        return true;
    }
    
    /**
     * Sets the shift size of bars to y direction.
     * 
     * @return true
     */
    public boolean setOffsetY(final double offset) {
        this.mOffsetY = offset;
        return true;
    }
    
    public double getInterval() {
        return this.mInterval;
    }
    
    /**
     * Sets the interval size of adjacent bars when the graph is multiple.
     * 
     * @return true
     */
    public boolean setInterval(final double interval) {
        this.mInterval = interval;
        return true;
    }

    /**
     * 
     */
    public boolean paintElement(final Graphics2D g2d, final Rectangle2D clipRect) {
        SGDrawingElement[] array = this.mDrawingElementArray;
        if (array != null) {
            for (int ii = 0; ii < array.length; ii++) {
                SGDrawingElementBar2D el = (SGDrawingElementBar2D) array[ii];
                el.paint(g2d, clipRect);
            }
        }
        return true;
    }

    /**
     * 
     * @return
     */
    public String getTagName() {
        return TAG_NAME_BAR;
    }

    /**
     * 
     */
    public boolean writeProperty(final Element el) {
	
        final int digitLineWidth = SGIConstants.LINE_WIDTH_MINIMAL_ORDER - 1;
        final float edgeLineWidth = (float) SGUtilityNumber.roundOffNumber(
        	this.getEdgeLineWidth(), digitLineWidth);

        el.setAttribute(KEY_RECTANGLE_WIDTH_VALUE, Double.toString(this.mWidthValue));
        el.setAttribute(KEY_EDGE_LINE_WIDTH, Float.toString(edgeLineWidth) + SGIConstants.pt);
        el.setAttribute(KEY_BASELINE_VALUE, Double.toString(this.mBaselineValue));
//        el.setAttribute(KEY_COLOR_LIST, SGUtilityText.getColorListString(this.mColorList));
        el.setAttribute(KEY_EDGE_LINE_COLOR, SGUtilityText.getColorString(this.mEdgeLineColor));
        el.setAttribute(KEY_EDGE_LINE_VISIBLE, Boolean.toString(this.isEdgeLineVisible()));
//        if (! SGPaintUtility.writeProperty(el, this.mInnerPaint)) {
//            return false;
//        }
    	if (!this.mInnerPaint.writeProperty(el)) {
    		return false;
    	}
        el.setAttribute(KEY_VERTICAL, Boolean.toString(this.mVerticalFlag));
        el.setAttribute(KEY_INTERVAL, Double.toString(this.mInterval));
        el.setAttribute(KEY_OFFSETX, Double.toString(this.mOffsetX));
        el.setAttribute(KEY_OFFSETY, Double.toString(this.mOffsetY));

        return true;
    }

    /**
     * 
     * @param el
     * @return
     */
    public boolean readProperty(final Element el) {
        // final float ratio = SGIConstants.CM_POINT_RATIO;

        String str = null;
        Number num = null;
        Color cl = null;
        Boolean b = null;

        // width of the bar
        str = el.getAttribute(KEY_RECTANGLE_WIDTH_VALUE);
        if (str.length() != 0) {
            num = SGUtilityText.getDouble(str);
            if (num == null) {
                return false;
            }

            if (this.setWidthValue(num.doubleValue()) == false) {
                return false;
            }
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

        // baseline value
        str = el.getAttribute(KEY_BASELINE_VALUE);
        if (str.length() != 0) {
            num = SGUtilityText.getDouble(str);
            if (num == null) {
                return false;
            }
            final double baselineValue = num.doubleValue();
            if (this.setBaselineValue(baselineValue) == false) {
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
        
        // edge line visible
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
        
        // inner color
        str = el.getAttribute(KEY_COLOR);
        if (str.length() != 0) {
            // backward compatibility
            List<Color> list = SGUtilityText.getColorList(str);
            if (list == null) {
                return false;
            }
            if (list.size() < 1) {
                return false;
            }
            cl = (Color) list.get(0);
            SGSelectablePaint paint = new SGSelectablePaint();
            paint.setSelectedPaintStyle(SGSelectablePaint.STYLE_INDEX_FILL);
            paint.setFillColor(cl);
            if (this.setInnerPaint(paint) == false) {
                return false;
            }
            
            str = el.getAttribute(KEY_BACKGROUND_TRANSPARENT);
            if (str.length() != 0) {
                b = SGUtilityText.getBoolean(str);
                if (b != null) {
                    if (b.booleanValue() == false) {
                        if (this.setTransparent(1.0f) == false) {
                            return false;
                        }
                    } else {
                        if (this.setTransparent(0.0f) == false) {
                            return false;
                        }
                    }
                }
            }
        } else {
            // inner paint
            SGIPaint paint = SGPaintUtility.readProperty(el);
            if (null != paint) {
                this.setInnerPaint(paint);
            } else {
                return false;
            }
        }
        
        // bar vertical
        str = el.getAttribute(KEY_VERTICAL);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return false;
            }
            final Boolean vertical = b;
            if (this.setVertical(vertical) == false) {
                return false;
            }
        }
        
        // bar interval
        str = el.getAttribute(KEY_INTERVAL);
        if (str.length() != 0) {
            num = SGUtilityText.getDouble(str);
            if (num == null) {
                return false;
            }
            final double interval = num.doubleValue();
            if (this.setInterval(interval) == false) {
                return false;
            }
        }
        
        // bar offset x
        str = el.getAttribute(KEY_OFFSETX);
        if (str.length() != 0) {
            num = SGUtilityText.getDouble(str);
            if (num == null) {
                return false;
            }
            final double offsetX = num.doubleValue();
            if (this.setOffsetX(offsetX) == false) {
                return false;
            }
        }
        
        // bar offset y
        str = el.getAttribute(KEY_OFFSETY);
        if (str.length() != 0) {
            num = SGUtilityText.getDouble(str);
            if (num == null) {
                return false;
            }
            final double offsetY = num.doubleValue();
            if (this.setOffsetY(offsetY) == false) {
                return false;
            }
        }

        return true;
    }

    
    protected static abstract class BarInGroup extends SGDrawingElementBar2D {
        
        // a group of bars
        protected SGElementGroupBar mGroup = null;

        // width of this bar
        protected float mWidth;

        // height of this bar
        protected float mHeight;

        /**
         * The array index.
         */
        protected int mIndex = -1;

        /**
         * Builds a rectangle in a group of rectangles.
         * 
         * @param group
         *           a group of rectangles
         */
        public BarInGroup(SGElementGroupBar group, final int index) {
            super();
            this.mGroup = group;
            this.mIndex = index;
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
        protected SGStroke getStroke() {
            return this.mGroup.getStroke();
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
            return true;
        }

        @Override
        public boolean setTransparent(float alpha) {
            // do nothing
            return true;
        }

        @Override
        public double getBaselineValue() {
            return this.mGroup.getBaselineValue();
        }

        @Override
        public double getWidthValue() {
            return this.mGroup.getWidthValue();
        }

        @Override
        public boolean isVertical() {
            return this.mGroup.isVertical();
        }
        
        @Override
        public double getInterval() {
            return this.mGroup.getInterval();
        }

        @Override
        public boolean setBaselineValue(double value) {
            // do nothing
            return true;
        }

        @Override
        public boolean setVertical(boolean b) {
            // do nothing
            return true;
        }

        @Override
        public boolean setWidthValue(double value) {
            // do nothing
            return true;
        }
        
        @Override
        public boolean setInterval(double value) {
            // do nothing
            return true;
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

    /**
     * 
     */
    public boolean setLocation(final SGTuple2f[] pointArray) {

        if (this.mDrawingElementArray == null) {
            return true;
        }
        

        if (pointArray.length != this.mDrawingElementArray.length) {
//            throw new IllegalArgumentException();
        	this.initDrawingElement(pointArray);
        }

        SGDrawingElement[] array = this.mDrawingElementArray;
        for (int ii = 0; ii < array.length; ii++) {
            SGDrawingElementBar2D bar = (SGDrawingElementBar2D) array[ii];
            final boolean eff = !(pointArray[ii].isInfinite() || pointArray[ii]
                    .isNaN());
            bar.setVisible(eff);
//            if (eff) {
//                bar.setLocation(pointArray[ii]);
//            }
        }

        return true;
    }

    /**
     * Sets whether the bars are vertical.
     * 
     * @param b
     *          true to set vertical
     * @return true if succeeded
     */
    public boolean setVertical(final boolean b) {
    	this.mVerticalFlag = b;
        return true;
    }

    /**
     * 
     */
    public SGProperties getProperties() {
        BarProperties p = new BarProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    /**
     * 
     */
    public boolean getProperties(SGProperties p) {
        if (p == null) {
            return false;
        }
        if ((p instanceof BarProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }

        BarProperties bp = (BarProperties) p;
        bp.setEdgeLineWidth(this.getEdgeLineWidth());
        bp.setInnerPaint(this.getInnerPaint());
        bp.setEdgeLineColor(this.getEdgeLineColor());
        bp.setBaselineValue(this.getBaselineValue());
        bp.setWidthValue(this.getWidthValue());
        bp.setVerticalFlag(this.isVertical());
        bp.setEdgeLineVisible(this.isEdgeLineVisible());
        bp.setInterval(this.getInterval());
        bp.setShiftX(this.getOffsetX());
        bp.setShiftY(this.getOffsetY());

        return true;
    }

    /**
     * 
     */
    public boolean setProperties(SGProperties p) {
        if ((p instanceof BarProperties) == false)
            return false;

        if (super.setProperties(p) == false)
            return false;

        BarProperties bp = (BarProperties) p;

        // for the versions older than 0.9.1
        Float width = bp.getBarWidth();
        if (width == null) {
            return false;
        }
        this.setRectangleWidth(width.floatValue());

        Float edgeLineWidth = bp.getEdgeLineWidth();
        if (edgeLineWidth == null) {
            return false;
        }
        this.setEdgeLineWidth(edgeLineWidth.floatValue());

        Color edgeLineColor = bp.getEdgeLineColor();
        if (edgeLineColor == null) {
            return false;
        }
        this.setEdgeLineColor(edgeLineColor);
        
        Boolean edgeLineVisible = bp.isEdgeLineVisible();
        if (edgeLineVisible == null) {
            return false;
        }
        this.setEdgeLineVisible(edgeLineVisible.booleanValue());
        
        SGIPaint innerPaint = bp.getInnerPaint();
        if (innerPaint == null) {
            return false;
        }
        this.setInnerPaint(innerPaint);

        Double baselineValue = bp.getBaselineValue();
        if (baselineValue == null) {
            return false;
        }
        this.setBaselineValue(baselineValue.doubleValue());

        Double widthValue = bp.getWidthValue();
        if (widthValue == null) {
            return false;
        }
        this.setWidthValue(widthValue.doubleValue());

        Boolean vertical = bp.isVertical();
        if (vertical == null) {
            return false;
        }
        this.setVertical(vertical);
        
        Double intervalValue = bp.getInterval();
        if (intervalValue == null) {
            return false;
        }
        this.setInterval(intervalValue);
        
        this.setOffsetX(bp.mShiftX);
        this.setOffsetY(bp.mShiftY);
        
        return true;
    }

    /**
     * @author okumura
     */
    public static class BarProperties extends ElementGroupProperties {
        protected SGDrawingElementBar.BarProperties mBarProperties = new SGDrawingElementBar.BarProperties();
        
        private double mShiftX;
        
        private double mShiftY;
        
        /**
         * 
         */
        public BarProperties() {
            super();
        }

        /**
         * Copy this object.
         * @return a copied object
         */
        public Object copy() {
            Object obj = super.copy();
            BarProperties p = (BarProperties) obj;
            p.mBarProperties = (SGDrawingElementBar.BarProperties) this.mBarProperties.copy();
            p.mShiftX = this.mShiftX;
            p.mShiftY = this.mShiftY;
            return p;
        }
        
        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mBarProperties.dispose();
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof BarProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            BarProperties p = (BarProperties) obj;
            if (this.mBarProperties.equals(p.mBarProperties) == false) {
                return false;
            }
            
            if (this.mShiftX != p.mShiftX) {
                return false;
            }
            if (this.mShiftY != p.mShiftY) {
                return false;
            }
            return true;
        }

        public Float getBarWidth() {
            return this.mBarProperties.getWidth();
        }

        public Float getEdgeLineWidth() {
            return this.mBarProperties.getEdgeLineWidth();
        }

        public Color getEdgeLineColor() {
            return this.mBarProperties.getEdgeLineColor();
        }
        
        public Boolean isEdgeLineVisible() {
            return this.mBarProperties.isEdgeLineVisible();
        }
        
        public SGIPaint getInnerPaint() {
            return this.mBarProperties.getInnerPaint();
        }

        public Double getBaselineValue() {
            return this.mBarProperties.getBaselineValue();
        }

        public Double getWidthValue() {
            return this.mBarProperties.getWidthValue();
        }
        
        public Boolean isVertical() {
            return this.mBarProperties.isVertical();
        }
        
        public Double getInterval() {
            return this.mBarProperties.getInterval();
        }
        
        public Double getShiftX() {
            return Double.valueOf(this.mShiftX);
        }
        
        public Double getShiftY() {
            return Double.valueOf(this.mShiftY);
        }

        public boolean setBarWidth(final float width) {
            return this.mBarProperties.setWidth(width);
        }

        public boolean setBaselineValue(final double value) {
            return this.mBarProperties.setBaselineValue(value);
        }

        public boolean setWidthValue(final double value) {
            return this.mBarProperties.setWidthValue(value);
        }

        public boolean setEdgeLineWidth(final float width) {
            return this.mBarProperties.setEdgeLineWidth(width);
        }

        public boolean setEdgeLineColor(final Color cl) {
            return this.mBarProperties.setEdgeLineColor(cl);
        }
        
        public boolean setEdgeLineVisible(final boolean b) {
            return this.mBarProperties.setEdgeLineVisible(b);
        }

        public boolean setInnerPaint(final SGIPaint paint) {
            return this.mBarProperties.setInnerPaint(paint);
        }
        
        public boolean setVerticalFlag(final boolean b) {
            return this.mBarProperties.setVertical(b);
        }
        
        public boolean setInterval(final double value) {
            return this.mBarProperties.setInterval(value);
        }
        
        public boolean setShiftX(final double value) {
            this.mShiftX = value;
            return true;
        }
        
        public boolean setShiftY(final double value) {
            this.mShiftY = value;
            return true;
        }
    }

}
