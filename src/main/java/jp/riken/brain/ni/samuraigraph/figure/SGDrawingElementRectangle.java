package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGSelectablePaint;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

import org.w3c.dom.Element;

/**
 * Drawing element of the rectangle.
 * 
 */
public abstract class SGDrawingElementRectangle extends SGDrawingElement
        implements SGIRectangleConstants {

    /**
     * Construct a rectangle object.
     */
    public SGDrawingElementRectangle() {
        super();
    }

    /**
     * Returns a stroke.
     * 
     * @return a stroke
     */
    protected abstract SGStroke getStroke();

    /**
     * Returns X coordinate of this rectangle.
     * 
     * @return X coordinate of this rectangle
     */
    public abstract float getX();

    /**
     * Returns Y coordinate of this rectangle.
     * 
     * @return Y coordinate of this rectangle
     */
    public abstract float getY();

    /**
     * Returns the location of this recgtangle.
     * 
     * @return the location of this recgtangle
     */
    public SGTuple2f getLocation() {
        return new SGTuple2f(this.getX(), this.getY());
    }

    /**
     * Returns the width of this recgtangle.
     * 
     * @return the width of this recgtangle
     */
    public abstract float getWidth();

    /**
     * Returns the height of this recgtangle.
     * 
     * @return the height of this recgtangle
     */
    public abstract float getHeight();
    

    /**
     * Returns the edge line width.
     * 
     * @return the edge line width
     */
    public abstract float getEdgeLineWidth();
    
    public float getEdgeLineWidth(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getEdgeLineWidth(),
                unit);
    }

    /**
     * Returns the edge line color.
     * 
     * @return the edge line color
     */
    public abstract Color getEdgeLineColor();
    
    /**
     * Returns the edge line visibility.
     * 
     * @return the edge line visibility
     */
    public abstract boolean isEdgeLineVisible();

    /**
     * Returns the inner paint.
     * 
     * @return the inner paint
     */
    public abstract SGIPaint getInnerPaint();

    /**
     * Returns the edge line type.
     * 
     * @return the edge line type
     */
    public abstract int getEdgeLineType();
    
    /**
     * Returns the alpha value of this rectangle's body.
     * 
     * @return alpha value of this rectangle
     */
    public abstract float getTransparency();
    
    /**
     * Sets the edge line width.
     * 
     * @param width
     *           line width to set
     * @return true if succeeded
     */
    public abstract boolean setEdgeLineWidth(final float width);

    /**
     * Sets the line width in a given unit.
     * 
     * @param lw
     *           the line width to set
     * @param unit
     *           the unit for given line width
     * @return true if succeeded
     */
    public boolean setEdgeLineWidth(final float lw, final String unit) {
        final Float lwNew = SGUtility.getLineWidth(lw, unit);
        if (lwNew == null) {
            return false;
        }
        if (this.setEdgeLineWidth(lwNew) == false) {
            return false;
        }
        return true;
    }

    /**
     * Sets the edge line type.
     * 
     * @param type
     *           the line type
     * @return true if succeeded
     */
    public abstract boolean setEdgeLineType(final int type);
    
    /**
     * Sets the edge line color.
     * 
     * @param color
     *           the edge line color
     * @return true if succeeded
     */
    public abstract boolean setEdgeLineColor(final Color color);

    /**
     * Sets the edge line visible
     * .
     * @param visible
     *           the edge line visible
     * @return true if succeeded
     */
    public abstract boolean setEdgeLineVisible(final boolean visible);
    
    /**
     * Sets the inner paint.
     * 
     * @param paint
     *           the inner paint to set
     * @return true if succeeded
     */
    public abstract boolean setInnerPaint(final SGIPaint paint);

    /**
     * Sets the transparency of this rectangle.
     * 
     * @param alpha
     *          alpha value to set transparent
     */
    public abstract boolean setTransparent(final float alpha);

    /**
     * Sets the x coordinate.
     * 
     * @param x
     *          the x coordinate
     * @return true if succeeded
     */
    public abstract boolean setX(final float x);

    /**
     * Sets the y coordinate.
     * 
     * @param y
     *          the y coordinate
     * @return true if succeeded
     */
    public abstract boolean setY(final float y);

    /**
     * 
     * @param x
     * @param y
     * @return
     */
    public boolean setLocation(final float x, final float y) {
        this.setX(x);
        this.setY(y);
        return true;
    }

    /**
     * 
     * @param point
     * @return
     */
    public boolean setLocation(final SGTuple2f point) {
        this.setLocation(point.x, point.y);
        return true;
    }

    /**
     * Sets the width of this rectangle.
     * 
     * @param w
     *          the width to set
     */
    public abstract boolean setWidth(final float w);

    /**
     * 
     */
    public boolean setWidth(final float w, final String unit) {
        return this.setWidth((float) SGUtilityText.convertToPoint(w, unit));
    }

    /**
     * Sets the height of this rectangle.
     * 
     * @param h
     *          the height to set
     */
    public abstract boolean setHeight(final float h);
    

    /**
     * 
     */
    public boolean setHeight(final float h, final String unit) {
        return this.setHeight((float) SGUtilityText.convertToPoint(h, unit));
    }

    /**
     * 
     * @param w
     * @param h
     * @return
     */
    public boolean setSize(final float w, final float h) {
        this.setWidth(w);
        this.setHeight(h);
        return true;
    }

    /**
     * 
     */
    public boolean setBounds(final float x, final float y, final float w,
            final float h) {

        this.setLocation(x, y);
        this.setSize(w, h);
        return true;
    }

    /**
     * 
     */
    public SGProperties getProperties() {
        RectangleProperties p = new RectangleProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    /**
     * 
     */
    public boolean getProperties(SGProperties p) {
        if ((p instanceof RectangleProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        RectangleProperties bp = (RectangleProperties) p;
        bp.setHeight(this.getHeight());
        bp.setWidth(this.getWidth());
        bp.setEdgeLineWidth(this.getEdgeLineWidth());
        bp.setEdgeLineType(this.getEdgeLineType());
        bp.setEdgeLineColor(this.getEdgeLineColor());
        bp.setEdgeLineVisible(this.isEdgeLineVisible());
        bp.setInnerPaint(this.getInnerPaint());
        return true;
    }

    /**
     * 
     */
    public boolean setProperties(SGProperties p) {
        if ((p instanceof RectangleProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        RectangleProperties bp = (RectangleProperties) p;

        final Float width = bp.getWidth();
        if (width == null) {
            return false;
        }
        this.setWidth(width.floatValue());

        final Float height = bp.getHeight();
        if (height == null) {
            return false;
        }
        this.setHeight(height.floatValue());

        final Float edgeLineWidth = bp.getEdgeLineWidth();
        if (edgeLineWidth == null) {
            return false;
        }
        this.setEdgeLineWidth(edgeLineWidth.floatValue());

        final int edgeLineType = bp.getEdgeLineType();
        if (edgeLineType == -1) {
            return false;
        }
        this.setEdgeLineType(edgeLineType);

        final Color edgeLineColor = bp.getEdgeLineColor();
        if (edgeLineColor == null) {
            return false;
        }
        this.setEdgeLineColor(edgeLineColor);
        
        final Boolean edgeLineVisible = bp.isEdgeLineVisible();
        if (edgeLineVisible == null) {
            return false;
        }
        this.setEdgeLineVisible(edgeLineVisible.booleanValue());

        this.setTransparent(bp.getTransparency());
        
        final SGIPaint innerPaint = bp.getInnerPaint();
        if (innerPaint == null) {
            return false;
        }
        this.setInnerPaint(innerPaint);

        return true;
    }

    public boolean writeProperty(final Element el, SGExportParameter params) {
    	SGPropertyMap map = this.getPropertyFileMap(params);
    	map.setToElement(el);
        return true;
    }

	public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	this.addProperties(map, KEY_EDGE_LINE_WIDTH, KEY_EDGE_LINE_TYPE, KEY_EDGE_LINE_COLOR, 
    			KEY_EDGE_LINE_VISIBLE);
    	
    	// properties of the inner paint
    	SGIPaint paint = this.getInnerPaint();
    	map.putAll(paint.getPropertyFileMap());
		return map;
	}

	protected void addProperties(SGPropertyMap map, String edgeLineWidthKey, 
			String edgeLineTypeKey, String edgeLineColorKey, String edgeLineVisibleKey) {
        SGPropertyUtility.addProperty(map, edgeLineWidthKey, 
        		SGUtility.getExportLineWidth(this.getEdgeLineWidth(LINE_WIDTH_UNIT)), 
        		LINE_WIDTH_UNIT);
        SGPropertyUtility.addProperty(map, edgeLineTypeKey,
        		SGDrawingElementLine.getLineTypeName(this.getEdgeLineType()));
        SGPropertyUtility.addProperty(map, edgeLineColorKey, this.getEdgeLineColor());
        SGPropertyUtility.addProperty(map, edgeLineVisibleKey, this.isEdgeLineVisible());
	}

    /**
     * 
     */
    public boolean readProperty(final Element el) {
        // final float ratio = SGIConstants.CM_POINT_RATIO;

        String str = null;
        Number num = null;
        Color cl = null;
        Boolean b = null;

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

        // edge line type
        str = el.getAttribute(KEY_EDGE_LINE_TYPE);
        if (str.length() != 0) {
            num = SGDrawingElementLine.getLineTypeFromName(str);
            if (num == null) {
                return false;
            }
            final int edgeLineType = num.intValue();
            if (this.setEdgeLineType(edgeLineType) == false) {
                return false;
            }
        }

        // inner paint
        str = el.getAttribute(KEY_INNER_COLOR);
        if (str.length() != 0) {
//            List<Color> list = SGUtilityText.getColorList(str);
//            if (list == null) {
//                return false;
//            }
//            if (list.size() < 1) {
//                return false;
//            }
//            cl = (Color) list.get(0);
        	cl = SGUtilityText.parseColorIncludingList(str);
        	if (cl == null) {
        		return false;
        	}
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
            SGIPaint innerPaint = SGPaintUtility.readProperty(el);
            if (null != innerPaint) {
                this.setInnerPaint(innerPaint);
            } else {
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
            if (this.setEdgeLineColor(cl) == false) {
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

        return true;
    }

    /**
     * 
     */
    public static class RectangleProperties extends DrawingElementProperties {
        
        private float mWidth = 0.0f;

        private float mHeight = 0.0f;

        private float mEdgeLineWidth = 0.0f;

        private int mEdgeLineType = -1;

        private Color mEdgeLineColor = null;
        
        private boolean mEdgeLineVisible = true;
        
        private SGIPaint mInnerPaint = new SGFillPaint();

        /**
         * 
         */
        public RectangleProperties() {
            super();
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof RectangleProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            RectangleProperties p = (RectangleProperties) obj;
            if (this.mWidth != p.mWidth) {
                return false;
            }
            if (this.mHeight != p.mHeight) {
                return false;
            }
            if (this.mEdgeLineWidth != p.mEdgeLineWidth) {
                return false;
            }
            if (this.mEdgeLineType != p.mEdgeLineType) {
                return false;
            }
            if (this.mEdgeLineVisible != p.mEdgeLineVisible) {
                return false;
            }
            if (SGUtility.equals(this.mEdgeLineColor, p.mEdgeLineColor) == false) {
                return false;
            }
            if (!this.mInnerPaint.equals(p.mInnerPaint)) {
                return false;
            }

            return true;
        }

        public Float getWidth() {
            return Float.valueOf(this.mWidth);
        }

        public Float getHeight() {
            return Float.valueOf(this.mHeight);
        }

        public Float getEdgeLineWidth() {
            return Float.valueOf(this.mEdgeLineWidth);
        }

        public int getEdgeLineType() {
            return this.mEdgeLineType;
        }

        public Color getEdgeLineColor() {
            return this.mEdgeLineColor;
        }
        
        public Boolean isEdgeLineVisible() {
            return Boolean.valueOf(this.mEdgeLineVisible);
        }
        
        public SGIPaint getInnerPaint() {
            try {
                return (SGIPaint)this.mInnerPaint.clone();
            } catch (CloneNotSupportedException e) {
                throw new InternalError(e.getMessage());
            }
        }

        public float getTransparency() {
            return this.mInnerPaint.getAlpha();
        }

        public boolean setWidth(final float value) {
            this.mWidth = value;
            return true;
        }

        public boolean setHeight(final float value) {
            this.mHeight = value;
            return true;
        }

        public boolean setEdgeLineWidth(final float width) {
            this.mEdgeLineWidth = width;
            return true;
        }

        public boolean setEdgeLineType(final int type) {
            this.mEdgeLineType = type;
            return true;
        }

        public boolean setEdgeLineColor(final Color cl) {
            this.mEdgeLineColor = cl;
            return true;
        }
        
        public boolean setEdgeLineVisible(final boolean visible) {
            this.mEdgeLineVisible = visible;
            return true;
        }
        
        public boolean setInnerPaint(final SGIPaint paint) {
            this.mInnerPaint = paint;
            return true;
        }

        public boolean setTransparent(final float alpha) {
            return this.mInnerPaint.setAlpha(alpha);
        }

    }

}
