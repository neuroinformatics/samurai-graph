package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/**
 * Drawing element of the symbol.
 */
public abstract class SGDrawingElementSymbol extends SGDrawingElement implements
        SGISymbolConstants, SGIDrawingElementConstants {


    /**
     * 
     */
    public SGDrawingElementSymbol() {
        super();
    }
    
    /**
     * Sets the x-coordinate.
     * 
     * @param x
     *          the x-coordinate to set
     * @return true if succeeded
     */
    public abstract boolean setX(final float x);

    /**
     * Sets the y-coordinate.
     * 
     * @param y
     *          the y-coordinate to set
     * @return true if succeeded
     */
    public abstract boolean setY(final float y);

    /**
     * Sets the location.
     * 
     * @param x
     *          the x-coordinate to set
     * @param y
     *          the y-coordinate to set
     * @return true if succeeded
     */
    public abstract boolean setLocation(final float x, final float y);

    /**
     * Sets the location.
     * 
     * @param point
     *          the location to set
     * @return true if succeeded
     */
    public abstract boolean setLocation(final SGTuple2f point);

    /**
     * Sets the size.
     * 
     * @param size
     *          the size to set
     * @return true if succeeded
     */
    public abstract boolean setSize(final float size);

    /**
     * Sets the symbol type.
     * 
     * @param type
     *          the symbol type to set
     * @return true if succeeded
     */
    public abstract boolean setType(final int type);

    /**
     * Sets the angle.
     * 
     * @param angle
     *           the angle to set
     * @return true if succeeded
     */
    public abstract boolean setAngle(final float angle);

    /**
     * Sets the line color.
     * 
     * @param color
     *           the line colot to set
     * @return true if succeeded
     */
    public abstract boolean setLineColor(final Color color);

    /**
     * Sets the inner color.
     * 
     * @param color
     *           the inner color to set
     * @return true if succeeded
     */
    public abstract boolean setInnerColor(final Color color);

    /**
     * Sets the line width.
     * 
     * @param lineWidth
     *           the line width to set
     * @return true if succeeded
     */
    public abstract boolean setLineWidth(final float lineWidth);
    
    /**
     * Sets the line visible.
     * 
     * @param visible
     *           the line visible to set
     * @return true if succeeded
     */
    public abstract boolean setLineVisible(final boolean visible);
    
    /**
     * Returns the location.
     * 
     * @return the location
     */
    public abstract SGTuple2f getLocation();

    /**
     * Returns the x-coordinate.
     * 
     * @return the x-coordinate
     */
    public float getX() {
        return this.getLocation().x;
    }

    /**
     * Returns the y-coordinate.
     * 
     * @return the y-coordinate
     */
    public float getY() {
        return this.getLocation().y;
    }

    /**
     * Returns the angle of this symbol.
     * 
     * @return the angle in units of radian
     */
    public abstract float getAngle();

    /**
     * Returns the size.
     * 
     * @return the size
     */
    public abstract float getSize();

    /**
     * Returns the symbol type.
     * 
     * @return the symbol type
     */
    public abstract int getType();

    /**
     * Returns the inner paint.
     * 
     * @return the inner paint
     */
    public abstract SGIPaint getInnerPaint();

    /**
     * Returns the line color.
     * 
     * @return the line color
     */
    public abstract Color getLineColor();

    /**
     * Returns the line width.
     * 
     * @return the line width
     */
    public abstract float getLineWidth();

    /**
     * Returns the line visible.
     * 
     * @return the line visible
     */
    public abstract boolean isLineVisible();
    
    /**
     * Returns whether a given symbol type is valid.
     * 
     * @param type
     *            a symbol type
     * @return true if the given symbol type is valid
     */
    public static boolean isValidSymbolType(final int type) {
        final int[] array = { SYMBOL_TYPE_CIRCLE,
                SYMBOL_TYPE_SQUARE, SYMBOL_TYPE_DIAMOND, SYMBOL_TYPE_TRIANGLE,
                SYMBOL_TYPE_INVERTED_TRIANGLE, SYMBOL_TYPE_CROSS,
                SYMBOL_TYPE_PLUS };
        for (int ii = 0; ii < array.length; ii++) {
            if (type == array[ii]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the symbol type constant from a given name.
     * 
     * @param name
     *           the name of symbol type
     * @return the symbol type constant if it exists or null otherwise
     */
    public static Integer getSymbolTypeFromName(final String name) {
        if (name == null) {
            return null;
        }
        int type;
        if (SGUtilityText.isEqualString(SYMBOL_NAME_CIRCLE, name)) {
            type = SYMBOL_TYPE_CIRCLE;
        } else if (SGUtilityText.isEqualString(SYMBOL_NAME_SQUARE, name)) {
            type = SYMBOL_TYPE_SQUARE;
        } else if (SGUtilityText.isEqualString(SYMBOL_NAME_DIAMOND, name)) {
            type = SYMBOL_TYPE_DIAMOND;
        } else if (SGUtilityText.isEqualString(SYMBOL_NAME_TRIANGLE, name)) {
            type = SYMBOL_TYPE_TRIANGLE;
        } else if (SGUtilityText.isEqualString(SYMBOL_NAME_INVERTED_TRIANGLE, name)
                || SGUtilityText.isEqualString(SYMBOL_NAME_INVERTED_TRIANGLE_OLD, name)) {
            // to maintain downward compatibility with the old releases
            type = SYMBOL_TYPE_INVERTED_TRIANGLE;
        } else if (SGUtilityText.isEqualString(SYMBOL_NAME_CROSS, name)) {
            type = SYMBOL_TYPE_CROSS;
        } else if (SGUtilityText.isEqualString(SYMBOL_NAME_PLUS, name)) {
            type = SYMBOL_TYPE_PLUS;
        } else {
            return null;
        }
        return Integer.valueOf(type);
    }

    /**
     * Returns the name of a given symbol type.
     * 
     * @param type
     *          the symbol type
     * @return the name of a given symbol type
     */
    public static String getSymbolTypeName(final int type) {
        String name = null;
        switch (type) {
        case SGISymbolConstants.SYMBOL_TYPE_CIRCLE:
            name = SGISymbolConstants.SYMBOL_NAME_CIRCLE;
            break;
        case SGISymbolConstants.SYMBOL_TYPE_SQUARE:
            name = SGISymbolConstants.SYMBOL_NAME_SQUARE;
            break;
        case SGISymbolConstants.SYMBOL_TYPE_DIAMOND:
            name = SGISymbolConstants.SYMBOL_NAME_DIAMOND;
            break;
        case SGISymbolConstants.SYMBOL_TYPE_TRIANGLE:
            name = SGISymbolConstants.SYMBOL_NAME_TRIANGLE;
            break;
        case SGISymbolConstants.SYMBOL_TYPE_INVERTED_TRIANGLE:
            name = SGISymbolConstants.SYMBOL_NAME_INVERTED_TRIANGLE;
            break;
        case SGISymbolConstants.SYMBOL_TYPE_CROSS:
            name = SGISymbolConstants.SYMBOL_NAME_CROSS;
            break;
        case SGISymbolConstants.SYMBOL_TYPE_PLUS:
            name = SGISymbolConstants.SYMBOL_NAME_PLUS;
            break;
        default:
        }
        return name;
    }

    /**
     * Returns whether a given symbol type is of the line type.
     * 
     * @param type
     *            a symbol type
     * @return true if the given symbol type is of the line type
     */
    public static boolean isLineTypeSymbol(final int type) {
        return ((type == SYMBOL_TYPE_CROSS) || (type == SYMBOL_TYPE_PLUS));
    }

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
        if (p == null)
            return false;
        if ((p instanceof SymbolProperties) == false)
            return false;

        super.getProperties(p);

        SymbolProperties sp = (SymbolProperties) p;
        sp.setSize(this.getSize());
        sp.setSymbolType(this.getType());
        sp.setLineWidth(this.getLineWidth());
        sp.setLineColor(this.getLineColor());

        return true;
    }

    /**
     * 
     */
    public static class SymbolProperties extends DrawingElementProperties {

        private int mType = -1;

        private float mSize = 0.0f;

        private float mLineWidth = 0.0f;

        private Color mLineColor = null;
        
        private boolean mLineVisible = true;

        private SGFillPaint mInnerPaint = null;
        
        /**
         * 
         * 
         */
        public SymbolProperties() {
            super();
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
            if (p.mType != this.mType) {
                return false;
            }
            if (p.mSize != this.mSize) {
                return false;
            }
            if (p.mLineWidth != this.mLineWidth) {
                return false;
            }
            if (SGUtility.equals(p.mLineColor, this.mLineColor) == false) {
                return false;
            }
            if (p.mLineVisible != this.mLineVisible) {
                return false;
            }
            if (SGUtility.equals(p.mInnerPaint, this.mInnerPaint) == false) {
                return false;
            }
            return true;
        }

        public Float getSize() {
            return Float.valueOf(this.mSize);
        }

        public Integer getSymbolType() {
            return Integer.valueOf(this.mType);
        }

        public Float getLineWidth() {
            return Float.valueOf(this.mLineWidth);
        }

        public Color getLineColor() {
            return this.mLineColor;
        }
        
        public Boolean isLineVisible() {
            return Boolean.valueOf(this.mLineVisible);
        }

        public SGIPaint getInnerPaint() {
            return this.mInnerPaint;
        }

        public boolean setSize(final float size) {
            this.mSize = size;
            return true;
        }

        public boolean setSymbolType(final int type) {
            this.mType = type;
            return true;
        }

        public boolean setLineWidth(final float width) {
            this.mLineWidth = width;
            return true;
        }

        public boolean setLineColor(final Color cl) {
            this.mLineColor = cl;
            return true;
        }
        
        public boolean setLineVisible(final boolean visible) {
            this.mLineVisible = visible;
            return true;
        }

        public boolean setInnerPaint(final SGIPaint paint) {
            try {
                this.mInnerPaint = (SGFillPaint) paint.clone();
            } catch (CloneNotSupportedException e) {
                throw new InternalError(e.getMessage());
            }
            return true;
        }
    }

}
