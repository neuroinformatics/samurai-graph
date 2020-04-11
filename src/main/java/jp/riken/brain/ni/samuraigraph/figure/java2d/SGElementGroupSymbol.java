package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTransparentPaint;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementSymbol;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISymbolConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGPaintUtility;

import org.w3c.dom.Element;

/**
 * A group of symbols.
 */
public abstract class SGElementGroupSymbol extends SGElementGroup implements
        SGISymbolConstants, SGIElementGroupConstants, SGIFigureDrawingElementConstants {

    /**
     * The size of each symbol.
     */
    protected float mSymbolSize;

    /**
     * The type of symbols.
     */
    protected int mSymbolType;

    /**
     * The line color of symbols.
     */
    protected Color mLineColor;

    /**
     * The inner paint of symbols.
     */
    protected SGFillPaint mInnerPaint = new SGFillPaint();

    /**
     * The line width of symbols.
     */
    protected float mLineWidth;

    /**
     * The line visibility of symbols.
     */
    protected boolean mLineVisible;
    
    /**
     * The angle.
     */
    protected float mAngle;
    
    /**
     * The shape.
     */
    private Shape mShape = null;
    
    /**
     * The default constructor.
     * 
     */
    public SGElementGroupSymbol() {
        super();
    }

    /**
     * Dispose this object.
     * 
     */
    public void dispose() {
        super.dispose();
        this.mLineColor = null;
        this.mShape = null;
    }

    /**
     * 
     */
    public float getSize() {
        return this.mSymbolSize;
    }

    /**
     * 
     * @param unit
     * @return
     */
    public float getSize(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getSize(), unit);
    }

    /**
     * 
     */
    public int getType() {
        return this.mSymbolType;
    }

    /**
     * 
     */
    public float getLineWidth() {
        return this.mLineWidth;
    }

    /**
     * 
     * @param unit
     * @return
     */
    public float getLineWidth(final String unit) {
        return (float) SGUtilityText
                .convertFromPoint(this.getLineWidth(), unit);
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
     * Returns the line visible.
     * 
     * @return the line visible
     */
    public boolean isLineVisible() {
        return this.mLineVisible;
    }
    
    /**
     * Returns the inner color.
     * 
     * @return the inner color
     */
    public Color getInnerColor() {
        return this.mInnerPaint.getColor();
    }

    
    /**
     * Returns the inner paint.
     * 
     * @return the inner paint
     */
    public SGIPaint getInnerPaint() {
        return this.mInnerPaint;
    }
    
    /**
     * Sets the symbol size.
     * 
     * @param size
     *           the symbol size to set
     * @return true if succeeded
     */
    public boolean setSize(final float size) {
        if (size < 0.0f) {
            throw new IllegalArgumentException("size < 0.0f");
        }
        this.mSymbolSize = size;
        this.updateShape();
        return true;
    }

    /**
     * Sets the size in a given unit.
     * 
     * @param size
     *           the symbol size to set
     * @param unit
     *           the unit for given symbol size
     * @return true if succeeded
     */
    public abstract boolean setSize(final float size, final String unit);

    /**
     * Sets the symbol type.
     * 
     * @param type
     *           the type to set
     * @return true if succeeded
     */
    public boolean setType(final int type) {
        if (SGDrawingElementSymbol.isValidSymbolType(type) == false) {
            return false;
        }
        this.mSymbolType = type;
        this.updateShape();
        return true;
    }

    /**
     * Sets the line color.
     * 
     * @param color
     *           the color to set
     * @return true if succeeded
     */
    public boolean setLineColor(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        this.mLineColor = color;
        return true;
    }
    
    /**
     * Sets the line visibility.
     * 
     * @param visible
     *           the visibility to set
     * @return true if succeeded
     */
    public boolean setLineVisible(final boolean visible) {
        this.mLineVisible = visible;
        return true;
    }
    
    /**
     * Sets the inner color.
     * 
     * @param color
     *           the color to set
     * @return true if succeeded
     */
    public boolean setInnerColor(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        if (this.mInnerPaint==null) {
            this.mInnerPaint = new SGFillPaint();
        }
        return this.mInnerPaint.setColor(color);
    }
    
    /**
     * Sets the inner transparency.
     * 
     * @param transparency
     *           the inner transparency to set
     * @return true if succeeded
     */
    public boolean setInnerTransparency(final int transparency) {
        if (this.mInnerPaint==null) {
            this.mInnerPaint = new SGFillPaint();
        }
        return this.mInnerPaint.setTransparency(transparency);
    }
    
    public int getInnerTransparency() {
    	return this.mInnerPaint.getTransparencyPercent();
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
        try {
            this.mInnerPaint = (SGFillPaint)paint.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
        return true;
    }

    /**
     * Sets the line width.
     * 
     * @param lw
     *          the line width to set
     * @return true if succeeded
     */
    public boolean setLineWidth(final float lw) {
        if (lw < 0.0f) {
            throw new IllegalArgumentException("lw < 0.0f");
        }
        this.mLineWidth = lw;
        return true;
    }

    /**
     * Sets the line width in a given unit.
     * 
     * @param lw
     *           the line width to set
     * @param unit
     *           the unit for given line width
     * @return true if succeeded
     */
    public abstract boolean setLineWidth(final float lw, final String unit);

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
    
    /**
     * Returns the location at a given index.
     * 
     * @param index
     *           the index
     * @return the location
     */
    public abstract SGTuple2f getLocation(final int index);

    /**
     * Sets the magnification.
     * 
     * @param mag
     *           the magnification
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
    	if (super.setMagnification(mag) == false) {
    		return false;
    	}
        this.updateShape();
        return true;
    }

    /**
     * 
     */
    public boolean paintElement(final Graphics2D g2d, final Rectangle2D clipRect) {
        SGDrawingElement[] array = this.mDrawingElementArray;
        if (array != null) {
            for (int ii = 0; ii < array.length; ii++) {
                SGDrawingElementSymbol2D el = (SGDrawingElementSymbol2D) array[ii];
                if (el.isVisible() == false) {
                    continue;
                }

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
        return TAG_NAME_SYMBOL;
    }

    /**
     * 
     */
    public boolean writeProperty(final Element el) {
	
        final int digitSymbolSize = SYMBOL_SIZE_MINIMAL_ORDER - 1;
        final int digitLineWidth = SGIConstants.LINE_WIDTH_MINIMAL_ORDER - 1;
	
        final float symbolSize = (float) SGUtilityNumber.roundOffNumber(
        	this.mSymbolSize * SGIConstants.CM_POINT_RATIO, digitSymbolSize);
        final float lineWidth = (float) SGUtilityNumber.roundOffNumber(
        	this.getLineWidth(), digitLineWidth);
        
        SGFillPaint paint = (SGFillPaint)this.getInnerPaint();
        List<Color> cList = new ArrayList<Color>();
        cList.add(paint.getColor());

        el.setAttribute(KEY_SYMBOL_SIZE,
        	Float.toString(symbolSize) + SGIConstants.cm);
        el.setAttribute(KEY_SYMBOL_TYPE,
        	SGDrawingElementSymbol.getSymbolTypeName(this.mSymbolType));
//        el.setAttribute(KEY_SYMBOL_INNER_COLOR_LIST,
//        	SGUtilityText.getColorListString(this.mColorList));
        el.setAttribute(KEY_SYMBOL_LINE_WIDTH,
        	Float.toString(lineWidth) + SGIConstants.pt);
        el.setAttribute(KEY_SYMBOL_LINE_COLOR,
        	SGUtilityText.getColorString(this.mLineColor));
        el.setAttribute(KEY_SYMBOL_LINE_VISIBLE,
                Boolean.toString(this.mLineVisible));
        el.setAttribute(KEY_SYMBOL_INNER_COLOR_LIST,
                SGUtilityText.getColorListString(cList));
        el.setAttribute(KEY_SYMBOL_INNER_TRANSPARENT,
                Integer.toString(paint.getTransparencyPercent())+
                SGPaintUtility.TRANSPARENCY_UNIT);
        
        return true;
    }

    /**
     * 
     * @param el
     * @return
     */
    public boolean readProperty(final Element el) {
        String str = null;
        Number num = null;
        Color cl = null;
        Boolean b = null;

        // symbol size
        str = el.getAttribute(KEY_SYMBOL_SIZE);
        if (str.length() != 0) {
            StringBuffer uSize = new StringBuffer();
            num = SGUtilityText.getNumber(str, uSize);
            if (num == null) {
                return false;
            }
            final float size = num.floatValue();
            if (this.setSize(size, uSize.toString()) == false) {
                return false;
            }
        }

        // symbol type
        str = el.getAttribute(KEY_SYMBOL_TYPE);
        if (str.length() != 0) {
            num = SGDrawingElementSymbol.getSymbolTypeFromName(str);
            if (num == null) {
                return false;
            }
            final int type = num.intValue();
            if (this.setType(type) == false) {
                return false;
            }
        }

        // inner color
        str = el.getAttribute(KEY_SYMBOL_INNER_COLOR_LIST);
        if (str.length() != 0) {
            List<Color> list = SGUtilityText.getColorList(str);
            if (list == null) {
                return false;
            }
            if (list.size() < 1) {
                return false;
            }
            final Color innerColor = (Color) list.get(0);
            if (this.setInnerColor(innerColor) == false) {
                return false;
            }
        }
        
        // inner transparency
        str = el.getAttribute(KEY_SYMBOL_INNER_TRANSPARENT);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b != null) {
                if (b.booleanValue() == false) {
                    if (this.setInnerTransparency(SGTransparentPaint.ALL_OPAQUE_VALUE) == false) {
                        return false;
                    }
                } else {
                    if (this.setInnerTransparency(SGTransparentPaint.ALL_TRANSPARENT_VALUE) == false) {
                        return false;
                    }
                }
            } else {
                num = SGUtilityText.getInteger(str, SGPaintUtility.TRANSPARENCY_UNIT);
                if (num == null) {
                    return false;
                }
                if (this.setInnerTransparency(num.intValue()) == false) {
                    return false;
                }
            }
        }

        // line width
        str = el.getAttribute(KEY_SYMBOL_LINE_WIDTH);
        if (str.length() != 0) {
            StringBuffer uLineWidth = new StringBuffer();
            num = SGUtilityText.getNumber(str, uLineWidth);
            if (num == null) {
                return false;
            }
            final float lineWidth = num.floatValue();
            if (this.setLineWidth(lineWidth, uLineWidth.toString()) == false) {
                return false;
            }
        }

        // line color
        str = el.getAttribute(KEY_SYMBOL_LINE_COLOR);
        if (str.length() != 0) {
            cl = SGUtilityText.parseColor(str);
            if (cl == null) {
                return false;
            }
            final Color lineColor = cl;
            if (this.setLineColor(lineColor) == false) {
                return false;
            }
        }
        
        // line visible
        str = el.getAttribute(KEY_SYMBOL_LINE_VISIBLE);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return false;
            }
            if (this.setLineVisible(b.booleanValue()) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * 
     * @return
     */
    protected SGDrawingElement createDrawingElementInstance(final int index) {
        return new SymbolInGroup(this, index);
    }
    
    /**
     * A symbol in a group.
     *
     */
    protected class SymbolInGroup extends SGDrawingElementSymbol2D {
        
        /**
         * The group of symbols.
         */
        protected SGElementGroupSymbol mGroup = null;

        /**
         * The array index.
         */
        protected int mIndex = -1;

        /**
         * Builds this object in a given group.
         * 
         * @param group
         *           a group of symbols
         * @param index
         *           the array index
         */
        public SymbolInGroup(final SGElementGroupSymbol group, final int index) {
            super();
            this.mGroup = group;
            this.mIndex = index;
        }
        
        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mGroup = null;
        }

        /**
         * Sets the size.
         * 
         * @param size
         *          the size to set
         * @return true if succeeded
         */
        public boolean setSize(final float size) {
            // do nothing
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
            // do nothing
            return true;
        }

        /**
         * Sets the angle.
         * 
         * @param angle
         *           the angle to set
         * @return true if succeeded
         */
        public boolean setAngle(final float angle) {
            // do nothing
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
            // do nothing
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
            // do nothing
            return true;
        }

        @Override
        public boolean setLineVisible(boolean visible) {
            // do nothing
            return true;
        }

        /**
         * Returns the size.
         * 
         * @return the size
         */
        public float getSize() {
            return this.mGroup.getSize();
        }

        /**
         * Returns the symbol type.
         * 
         * @return the symbol type
         */
        public int getType() {
            return this.mGroup.getType();
        }

        /**
         * Returns the line color.
         * 
         * @return the line color
         */
        public Color getLineColor() {
            return this.mGroup.getLineColor();
        }

        /**
         * Returns the line width.
         * 
         * @return the line width
         */
        public float getLineWidth() {
            return this.mGroup.getLineWidth();
        }

        @Override
        public boolean isLineVisible() {
            return this.mGroup.isLineVisible();
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
        public boolean setInnerColor(Color color) {
            // do nothing
            return true;
        }

        @Override
        public float getAngle() {
            return this.mGroup.getAngle();
        }

        @Override
        public SGTuple2f getLocation() {
            return this.mGroup.getLocation(this.mIndex);
        }

        @Override
        public boolean setLocation(float x, float y) {
            // do nothing
            return true;
        }

        @Override
        public boolean setLocation(SGTuple2f point) {
            // do nothing
            return true;
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
        
        /**
         * Returns the shape.
         * 
         * @return a shape object
         */
        protected Shape getShape() {
        	return SGElementGroupSymbol.this.mShape;
        }
        
        /**
         * Updates the shape.
         *
         */
        protected void updateShape() {
        	// do nothing
        }
        
        public int getIndex() {
        	return this.mIndex;
        }
    }

    /**
     * 
     * @return
     */
    protected boolean initDrawingElement(final SGTuple2f[] array) {
        final int num = array.length;
        this.initDrawingElement(num);

        SGDrawingElement[] sArray = this.mDrawingElementArray;
        for (int ii = 0; ii < num; ii++) {
            ((SGDrawingElementSymbol) sArray[ii]).setLocation(array[ii]);
        }
        return true;
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

        for (int ii = 0; ii < pointArray.length; ii++) {
            SGDrawingElementSymbol2D symbol = (SGDrawingElementSymbol2D) this.mDrawingElementArray[ii];
            final boolean eff = !(pointArray[ii].isInfinite() || pointArray[ii]
                    .isNaN());
            symbol.setVisible(eff);
//            if (eff) {
//                symbol.setLocation(pointArray[ii]);
//            }
        }

        return true;
    }

//    /**
//     * 
//     * @return
//     */
//    public boolean setPropertiesOfDrawingElements() {
//        for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
//            SGDrawingElementSymbol2D symbol = (SGDrawingElementSymbol2D) this.mDrawingElementArray[ii];
//            symbol.setMagnification(this.mMagnification);
////            symbol.setColorList(this.mColorList);
////            symbol.setInnerColor(this.getInnerColor());
////            symbol.setSize(this.mSymbolSize);
////            symbol.setType(this.mSymbolType);
////            symbol.setLineWidth(this.mLineWidth);
////            symbol.setLineColor(this.mLineColor);
//        }
//        return true;
//    }

    /**
     * 
     */
    public SGProperties getProperties() {
        SymbolProperties p = new SymbolProperties();
        this.getProperties(p);
        return p;
    }

    /**
     * 
     */
    public boolean getProperties(SGProperties p) {
        if (p == null) {
            return false;
        }
        if ((p instanceof SymbolProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }

        SymbolProperties sp = (SymbolProperties) p;
        sp.setSize(this.getSize());
        sp.setSymbolType(this.getType());
        sp.setLineWidth(this.getLineWidth());
        sp.setLineColor(this.getLineColor());
        sp.setLineVisible(this.isLineVisible());
        sp.setInnerPaint(this.getInnerPaint());
        return true;
    }

    /**
     * 
     */
    public boolean setProperties(SGProperties p) {
        if ((p instanceof SymbolProperties) == false)
            return false;

        if (super.setProperties(p) == false) {
            return false;
        }
        
        SymbolProperties sp = (SymbolProperties) p;

        final Float size = sp.getSize();
        if (size == null) {
            return false;
        }
        this.setSize(size.floatValue());

        final Integer type = sp.getSymbolType();
        if (type == null) {
            return false;
        }
        this.setType(type.intValue());

        final Float lineWidth = sp.getLineWidth();
        if (lineWidth == null) {
            return false;
        }
        this.setLineWidth(lineWidth.floatValue());

        final Color lineColor = sp.getLineColor();
        if (lineColor == null) {
            return false;
        }
        this.setLineColor(lineColor);
        
        final Boolean lineVisible = sp.isLineVisible();
        if (lineVisible == null) {
            return false;
        }
        this.setLineVisible(lineVisible.booleanValue());

        final SGIPaint innerPaint = sp.getInnerPaint();
        if (innerPaint == null) {
            return false;
        }
        this.setInnerPaint(innerPaint);
        
        return true;
    }

    /**
     * @author okumura
     */
    public static class SymbolProperties extends ElementGroupProperties {
        private SGDrawingElementSymbol.SymbolProperties mSymbolProperties = new SGDrawingElementSymbol.SymbolProperties();
        
        /**
         * 
         * 
         */
        public SymbolProperties() {
            super();
        }

        /**
         * Copy this object.
         * @return a copied object
         */
        public Object copy() {
            Object obj = super.copy();
            SymbolProperties p = (SymbolProperties) obj;
            p.mSymbolProperties = (SGDrawingElementSymbol.SymbolProperties) this.mSymbolProperties.copy();
            return p;
        }
        
        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mSymbolProperties.dispose();
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof SymbolProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            SymbolProperties p = (SymbolProperties) obj;
            if (this.mSymbolProperties.equals(p.mSymbolProperties) == false) {
                return false;
            }
            return true;
        }

        public Float getSize() {
            return this.mSymbolProperties.getSize();
        }

        public Integer getSymbolType() {
            return this.mSymbolProperties.getSymbolType();
        }

        public Float getLineWidth() {
            return this.mSymbolProperties.getLineWidth();
        }

        public Color getLineColor() {
            return this.mSymbolProperties.getLineColor();
        }
        
        public Boolean isLineVisible() {
            return this.mSymbolProperties.isLineVisible();
        }
        
        public SGIPaint getInnerPaint() {
            return this.mSymbolProperties.getInnerPaint();
        }

        public boolean setSize(final float size) {
            return this.mSymbolProperties.setSize(size);
        }

        public boolean setSymbolType(final int type) {
            return this.mSymbolProperties.setSymbolType(type);
        }

        public boolean setLineWidth(final float width) {
            return this.mSymbolProperties.setLineWidth(width);
        }

        public boolean setLineColor(final Color cl) {
            return this.mSymbolProperties.setLineColor(cl);
        }
        
        public boolean setLineVisible(final boolean visible) {
            return this.mSymbolProperties.setLineVisible(visible);
        }

        public boolean setInnerPaint(final SGIPaint paint) {
            return this.mSymbolProperties.setInnerPaint(paint);
        }
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
    	this.mShape = SGDrawingElementSymbol2D.createShape(this.getType(),
    			this.getMagnification() * this.getSize());
    }

}
