package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/**
 * Drawing element of an error bar.
 */
public abstract class SGDrawingElementErrorBar extends SGDrawingElement
        implements SGIArrowConstants, SGIErrorBarConstants {

    /**
     * An arrow object for lower error.
     */
    protected SGDrawingElementArrow mLowerArrowElement;

    /**
     * An arrow object for upper error.
     */
    protected SGDrawingElementArrow mUpperArrowElement;
    
    /**
     * Builds an object.
     */
    public SGDrawingElementErrorBar() {
        super();
    }

    /**
     * Create an instance of an arrow.
     * 
     * @param index
     *           the array index
     */
    protected abstract SGDrawingElementArrow createArrowInstance(final int index);

    /**
     * Returns an arrow object for lower error.
     */
    protected final SGDrawingElementArrow getLowerArrow() {
        return this.mLowerArrowElement;
    }

    /**
     * Returns an arrow object for upper error.
     */
    protected final SGDrawingElementArrow getUpperArrow() {
        return this.mUpperArrowElement;
    }
    
    /**
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();
        this.mLowerArrowElement.dispose();
        this.mLowerArrowElement = null;
        this.mUpperArrowElement.dispose();
        this.mUpperArrowElement = null;
    }

    /**
     * Returns whether a given error bar style is valid.
     * 
     * @param style
     *            an error bar style
     * @return true if the given error bar style is valid
     */
    public static boolean isValidErrorBarStyle(final int style) {
        final int[] array = { ERROR_BAR_BOTHSIDES, ERROR_BAR_UPSIDE,
                ERROR_BAR_DOWNSIDE };
        for (int ii = 0; ii < array.length; ii++) {
            if (style == array[ii]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the error bar style constant from a given name.
     * 
     * @param name
     *            the name of error bar style
     * @return the error bar style constant if it exists or null otherwise
     */
    public static Integer getErrorBarStyleFromName(final String name) {
        if (name == null) {
            return null;
        }
        int style;
        if (SGUtilityText.isEqualString(ERROR_BAR_STYLE_BOTHSIDES, name)) {
            style = ERROR_BAR_BOTHSIDES;
        } else if (SGUtilityText.isEqualString(ERROR_BAR_STYLE_UPSIDE, name)) {
            style = ERROR_BAR_UPSIDE;
        } else if (SGUtilityText.isEqualString(ERROR_BAR_STYLE_DOWNSIDE, name)) {
            style = ERROR_BAR_DOWNSIDE;
        } else {
            return null;
        }
        return Integer.valueOf(style);
    }

    /**
     * Returns the name of a given error bar style.
     * 
     * @param style
     *          the error bar style
     * @return the name of a given error bar style
     */
    public static String getErrorBarStyleName(final int style) {
        String name = null;
        switch (style) {
        case ERROR_BAR_BOTHSIDES:
            name = ERROR_BAR_STYLE_BOTHSIDES;
            break;
        case ERROR_BAR_UPSIDE:
            name = ERROR_BAR_STYLE_UPSIDE;
            break;
        case ERROR_BAR_DOWNSIDE:
            name = ERROR_BAR_STYLE_DOWNSIDE;
            break;
        default:
        }
        return name;
    }

    /**
     * Returns whether a given head type is valid.
     * 
     * @param type
     *            a head type
     * @return true if the given head type is valid
     */
    public static boolean isValidHeadType(final int type) {
        final int[] array = { SYMBOL_TYPE_CIRCLE, SYMBOL_TYPE_TRANSVERSELINE,
                SYMBOL_TYPE_VOID };
        for (int ii = 0; ii < array.length; ii++) {
            if (type == array[ii]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the head type constant from a given name.
     * 
     * @param name
     *           the name of head type
     * @return the head type constant if it exists or null otherwise
     */
    public static Integer getHeadTypeFromName(final String name) {
        if (name == null) {
            return null;
        }
        int type;
        if (SGUtilityText.isEqualString(SYMBOL_NAME_CIRCLE, name)) {
            type = SYMBOL_TYPE_CIRCLE;
        } else if (SGUtilityText.isEqualString(SYMBOL_NAME_TRANSVERSE_LINE, name)) {
            type = SYMBOL_TYPE_TRANSVERSELINE;
        } else if (SGUtilityText.isEqualString(SYMBOL_NAME_VOID, name)) {
            type = SYMBOL_TYPE_VOID;
        } else {
            return null;
        }
        return Integer.valueOf(type);
    }

    /**
     * Returns the name of a given head type.
     * 
     * @param type
     *          the head type
     * @return the name of a given head type
     */
    public static String getHeadTypeName(final int type) {
        String name = null;
        switch (type) {
        case SYMBOL_TYPE_CIRCLE:
            name = SYMBOL_NAME_CIRCLE;
            break;
        case SYMBOL_TYPE_TRANSVERSELINE:
            name = SYMBOL_NAME_TRANSVERSE_LINE;
            break;
        case SYMBOL_TYPE_VOID:
            name = SYMBOL_NAME_VOID;
            break;
        default:
        }
        return name;
    }

    /**
     * Sets the location of points.
     * 
     * @param center
     *           the location of center
     * @param lower
     *           the location of lower end point
     * @param upper
     *           the location of upper end point
     * @return true if succeeded
     */
    public boolean setLocation(final SGTuple2f center, final SGTuple2f 
            lower, final SGTuple2f upper) {
        this.mLowerArrowElement.setLocation(center, lower);
        this.mUpperArrowElement.setLocation(center, upper);
        return true;
    }

    /**
     * Returns the color.
     * 
     * @return the color
     */
    public abstract Color getColor();

    /**
     * Sets the color.
     * 
     * @param cl
     *          the color to set
     * @return true if succeeded
     */
    public abstract boolean setColor(Color cl);

    /**
     * Returns the line width.
     * 
     * @return the line width.
     */
    public abstract float getLineWidth();
    
    /**
     * Sets the line width.
     * 
     * @param width
     *            line width to set
     * @return true if succeeded
     */
    public abstract boolean setLineWidth(final float width);

    /**
     * Returns the head size.
     * 
     * @return the head size.
     */
    public abstract float getHeadSize();

    /**
     * Sets the head size.
     * 
     * @param size
     *           the head size to set
     * @return true if succeeded
     */
    public abstract boolean setHeadSize(final float size);

    /**
     * Returns the error bar style.
     * 
     * @return the error bar style.
     */
    public abstract int getErrorBarStyle();

    /**
     * Sets the style of the error bar.
     * 
     * @param style
     *           the style
     * @return true if succeeded
     */
    public abstract boolean setErrorBarStyle(final int style);
    
    /**
     * Returns the head type.
     * 
     * @return the head type.
     */
    public abstract int getHeadType();
    
    /**
     * Sets the head type.
     * 
     * @param type
     *           the head type to set
     * @return true if succeeded
     */
    public abstract boolean setHeadType(int type);
    
    /**
     * 
     */
    public SGProperties getProperties() {
        ErrorBarProperties p = new ErrorBarProperties();
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
        if ((p instanceof ErrorBarProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        ErrorBarProperties ep = (ErrorBarProperties) p;
        ep.setLineWidth(this.getLineWidth());
        ep.setHeadSize(this.getHeadSize());
        ep.setHeadType(this.getHeadType());
        ep.setErrorBarStyle(this.getErrorBarStyle());
        ep.setColor(this.getColor());
        return true;
    }

    /**
     * Sets the visibility of the lower error bar.
     * 
     * @param b
     *         true to set visible
     */
    public void setLowerVisible(final boolean b) {
        this.mLowerArrowElement.setVisible(b);
    }
    
    /**
     * Sets the visibility of the upper error bar.
     * 
     * @param b
     *         true to set visible
     */
    public void setUpperVisible(final boolean b) {
        this.mUpperArrowElement.setVisible(b);
    }

    /**
     * Returns whether the lower error bar is visible.
     * 
     * @return true if the lower error bar is visible
     */
    public boolean isLowerVisible() {
        return this.mLowerArrowElement.isVisible();
    }
    
    /**
     * Returns whether the upper error bar is visible.
     * 
     * @return true if the upper error bar is visible
     */
    public boolean isUpperVisible() {
        return this.mUpperArrowElement.isVisible();
    }
    
    /**
     * @author okumura
     */
    public static class ErrorBarProperties extends DrawingElementProperties {

        private SGDrawingElementArrow.ArrowProperties mArrowProperties = new SGDrawingElementArrow.ArrowProperties();

        private int mErrorBarStyle = ERROR_BAR_BOTHSIDES;

        private int mHeadType;
        
    	private boolean mVerticalFlag;
    	
        public ErrorBarProperties() {
            super();
            this.mArrowProperties.setLineType(SGILineConstants.LINE_TYPE_SOLID);
        }

        public boolean equals(final Object obj) {
            if ((obj instanceof ErrorBarProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            ErrorBarProperties p = (ErrorBarProperties) obj;
            if (this.mArrowProperties.equals(p.mArrowProperties) == false) {
                return false;
            }
            if (this.mErrorBarStyle != p.mErrorBarStyle) {
                return false;
            }
            if (this.mHeadType != p.mHeadType) {
                return false;
            }
            if (SGUtility.equals(this.getColor(), p.getColor()) == false) {
                return false;
            }
            if (this.mVerticalFlag != p.mVerticalFlag) {
            	return false;
            }
            return true;
        }

        public Float getLineWidth() {
            return this.mArrowProperties.getLineWidth();
        }

        public Float getHeadSize() {
            return this.mArrowProperties.getHeadSize();
        }

        public Integer getHeadType() {
            return Integer.valueOf(this.mHeadType);
        }

        public Integer getErrorBarStyle() {
            return Integer.valueOf(this.mErrorBarStyle);
        }
        
        public Color getColor() {
            return this.mArrowProperties.getColor();
        }
        
        public Boolean isVertical() {
        	return Boolean.valueOf(this.mVerticalFlag);
        }

        public void setLineWidth(final float width) {
            this.mArrowProperties.setLineWidth(width);
        }

        public void setHeadType(final int type) {
            this.mHeadType = type;
        }

        public void setHeadSize(final float size) {
            this.mArrowProperties.setHeadSize(size);
        }

        public void setErrorBarStyle(final int style) {
            this.mErrorBarStyle = style;
        }
        
        public boolean setColor(final Color cl) {
            return this.mArrowProperties.setColor(cl);
        }

        public void setVertical(final boolean b) {
        	this.mVerticalFlag = b;
        }
    }

}
