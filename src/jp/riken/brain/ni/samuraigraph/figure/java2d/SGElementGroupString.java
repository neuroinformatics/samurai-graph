package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGDateUtility;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementString;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIStringConstants;

import org.w3c.dom.Element;

public abstract class SGElementGroupString extends SGElementGroup implements
        SGIStringConstants, SGIElementGroupConstants, SGIFigureDrawingElementConstants {

    /**
     * The font name.
     */
    protected String mFontName;

    /**
     * The font size.
     */
    protected float mFontSize;

    /**
     * The font style.
     */
    protected int mFontStyle;

    /**
     * The font color.
     */
    protected Color mColor = null;
    
    /**
     * The angle of text strings.
     */
    protected float mAngle;
    
    /**
     * The decimal places for the tick labels of numbers.
     */
    protected int mDecimalPlaces;

    /**
     * The exponent for the tick labels of numbers.
     */
    protected int mExponent;

    /**
     * The format for date string.
     * Initializes with an empty string.
     */
    protected String mDateFormat = "";
    
    /**
     * Builds a group of text strings.
     */
    public SGElementGroupString() {
        super();
    }

    /**
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();
        this.mFontName = null;
    }

    /**
     * Sets the text strings to the strings.
     * @param array
     *              an array of text strings to set to the strings
     * @return
     *              true if succeeded
     */
    public boolean setStrings(final String[] array) {
        if (array == null) {
            throw new IllegalArgumentException("array == null");
        }
        if (array.length != this.mDrawingElementArray.length) {
            throw new IllegalArgumentException("Invalid array length: " + array.length + " != " + this.mDrawingElementArray.length);
        }
        for (int ii = 0; ii < array.length; ii++) {
            SGDrawingElementString el = (SGDrawingElementString) this.mDrawingElementArray[ii];
            el.setString(array[ii]);
        }
        return true;
    }
    
    /**
     * 
     * @param name
     * @param style
     * @param size
     * @return
     */
    public boolean setFont(final String name, final int style, final float size) {
    	if (!SGUtility.equals(this.mFontName, name)
    			|| this.mFontStyle != style
    			|| this.mFontSize != size) {
            this.mFontName = name;
            this.mFontStyle = style;
            this.mFontSize = size;
            if (this.mDrawingElementArray != null) {
                for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
                    SGDrawingElementString el = (SGDrawingElementString) this.mDrawingElementArray[ii];
                    el.setFont(name, style, size);
                }
            }
    	}
        return true;
    }

    /**
     * Sets the font name.
     * 
     * @param name
     *          the font name to set
     * @return true if succeeded
     */
    public boolean setFontName(final String name) {
    	if (!SGUtility.equals(this.mFontName, name)) {
            this.mFontName = name;
            if (this.mDrawingElementArray != null) {
                for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
                    SGDrawingElementString el = (SGDrawingElementString) this.mDrawingElementArray[ii];
                    el.setFontName(name);
                }
            }
    	}
        return true;
    }

    /**
     * Sets the font size.
     * 
     * @param size
     *           the font size to set
     * @return true if succeeded
     */
    public boolean setFontSize(final float size) {
        if (size < 0.0f) {
            throw new IllegalArgumentException("size < 0.0f");
        }
    	if (this.mFontSize != size) {
            this.mFontSize = size;
            if (this.mDrawingElementArray != null) {
                for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
                    SGDrawingElementString el = (SGDrawingElementString) this.mDrawingElementArray[ii];
                    el.setFontSize(size);
                }
            }
    	}
        return true;
    }

    /**
     * Sets the font size in a given unit.
     * 
     * @param size
     *           the font size to set
     * @param unit
     *           the unit for given font size
     * @return true if succeeded
     */
    public abstract boolean setFontSize(final float size, final String unit);

    /**
     * Sets the font style.
     * 
     * @param style
     *           the font style to set
     * @return true if succeeded
     */
    public boolean setFontStyle(final int style) {
        if (SGUtilityText.isValidFontStyle(style) == false) {
            return false;
        }
        if (this.mFontStyle != style) {
            this.mFontStyle = style;
            if (this.mDrawingElementArray != null) {
                for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
                    SGDrawingElementString el = (SGDrawingElementString) this.mDrawingElementArray[ii];
                    el.setFontStyle(style);
                }
            }
        }
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
        final Float aNew = SGUtility.calcPropertyValue(angle, null, null, 
                STRING_ANGLE_MIN, STRING_ANGLE_MAX, STRING_ANGLE_MINIMAL_ORDER);
        if (aNew == null) {
            return false;
        }
    	if (this.mAngle != angle) {
            this.mAngle = angle;
            if (this.mDrawingElementArray != null) {
                for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
                    SGDrawingElementString el = (SGDrawingElementString) this.mDrawingElementArray[ii];
                    el.setAngle(angle);
                }
            }
    	}
        return true;
    }

    /**
     * Sets the color.
     * 
     * @param color
     *           the color to set
     * @return true if succeeded
     */
    public boolean setColor(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        if (!SGUtility.equals(this.mColor, color)) {
            this.mColor = color;
            if (this.mDrawingElementArray != null) {
                for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
                    SGDrawingElementString el = (SGDrawingElementString) this.mDrawingElementArray[ii];
                    el.setColor(color);
                }
            }
        }
        return true;
    }

    /**
     * 
     */
    public String getFontName() {
        return this.mFontName;
    }

    /**
     * 
     */
    public float getFontSize() {
        return this.mFontSize;
    }

    /**
     * 
     * @param unit
     * @return
     */
    public float getFontSize(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getFontSize(), unit);
    }

    /**
     * 
     */
    public int getFontStyle() {
        return this.mFontStyle;
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
     * 
     */
    public float getAngle() {
        return this.mAngle;
    }
    
    /**
     * Sets the decimal places.
     * 
     * @param value
     *           the decimal places to set
     * @return true if succeeded
     */
    public boolean setDecimalPlaces(final int value) {
        final int vNew;
        if (value < TICK_LABEL_DECIMAL_PLACES_MIN) {
            return false;
        } else if (value > TICK_LABEL_DECIMAL_PLACES_MAX) {
            return false;
        } else {
            vNew = value;
        }
        this.mDecimalPlaces = vNew;
        return true;
    }
    
    /**
     * Sets the exponent.
     * 
     * @param value
     *           the exponent to set
     * @return true if succeeded
     */
    public boolean setExponent(final int value) {
        final int vNew;
        if (value < TICK_LABEL_EXPONENT_MIN) {
            return false;
        } else if (value > TICK_LABEL_EXPONENT_MAX) {
            return false;
        } else {
            vNew = value;
        }
        this.mExponent = vNew;
        return true;
    }
    
    public int getDecimalPlaces() {
        return this.mDecimalPlaces;
    }
    
    public int getExponent() {
        return this.mExponent;
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
            ((SGDrawingElementString) sArray[ii]).setLocation(array[ii]);
        }
        return true;
    }

    /**
     * 
     */
    public boolean paintElement(final Graphics2D g2d, Rectangle2D clipRect) {
        if (this.mDrawingElementArray != null) {
            for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
                SGDrawingElementString2DExtended el = (SGDrawingElementString2DExtended) this.mDrawingElementArray[ii];
                el.paint(g2d);
            }
        }
        return true;
    }

    /**
     * 
     */
    public boolean setLocation(final SGTuple2f[] pointArray) {
        if (pointArray == null) {
            throw new IllegalArgumentException("pointArray==null");
        }
        if (pointArray.length != this.mDrawingElementArray.length) {
        	this.initDrawingElement(pointArray);
        }
        for (int ii = 0; ii < pointArray.length; ii++) {
            SGDrawingElementString2DExtended el = (SGDrawingElementString2DExtended) this.mDrawingElementArray[ii];
            final boolean eff = !(pointArray[ii].isInfinite() || pointArray[ii]
                    .isNaN());
            el.setVisible(eff);
            if (eff) {
                el.setLocation(pointArray[ii]);
            }
        }
        return true;
    }

    /**
     * 
     * @return
     */
    public String getTagName() {
        return TAG_NAME_LABEL;
    }

    /**
     * 
     */
    public boolean writeProperty(final Element el) {
        final int digitFontSize = FONT_SIZE_MINIMAL_ORDER - 1;
        final int digitLabelAngle = LABEL_ANGLE_MINIMAL_ORDER - 1;
        final float fontSize = (float) SGUtilityNumber.roundOffNumber(
        	this.mFontSize, digitFontSize);
        final float angle = (float) SGUtilityNumber.roundOffNumber(
        	-this.mAngle, digitLabelAngle);        
        List<Color> cList = new ArrayList<Color>();
        cList.add(this.getColor());
        
        el.setAttribute(KEY_FONT_SIZE, Float.toString(fontSize) + SGIConstants.pt);
        el.setAttribute(KEY_FONT_NAME, this.mFontName);
        el.setAttribute(KEY_FONT_STYLE, SGUtilityText.getFontStyleName(this.mFontStyle));
        el.setAttribute(KEY_ANGLE, Float.toString(angle));
        el.setAttribute(KEY_DECIMAL_PLACES, Integer.toString(this.mDecimalPlaces));
        el.setAttribute(KEY_EXPONENT, Integer.toString(this.mExponent));
        el.setAttribute(KEY_COLOR, SGUtilityText.getColorListString(cList));
        el.setAttribute(KEY_DATE_FORMAT, this.mDateFormat);
        
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
        // Color cl = null;
        // Boolean b = null;
        List list = null;

        // set font size
        str = el.getAttribute(KEY_FONT_SIZE);
        if (str.length() != 0) {
            StringBuffer uFontSize = new StringBuffer();
            num = SGUtilityText.getNumber(str, uFontSize);
            if (num == null) {
                return false;
            }
            if (this.setFontSize(num.floatValue(), uFontSize.toString()) == false) {
                return false;
            }
        }

        // set font name
        str = el.getAttribute(KEY_FONT_NAME);
        if (str.length() != 0) {
            if (this.setFontName(str) == false) {
                return false;
            }
        }

        // set font style
        str = el.getAttribute(KEY_FONT_STYLE);
        if (str.length() != 0) {
            final Integer fontStyle = SGUtilityText.getFontStyle(str);
            if (fontStyle == null) {
                return false;
            }
            if (this.setFontStyle(fontStyle.intValue()) == false) {
                return false;
            }
        }

        // color
        str = el.getAttribute(KEY_COLOR);
        if (str.length() != 0) {
            list = SGUtilityText.getColorList(str);
            if (list == null) {
                return false;
            }
            if (list.size() < 1) {
                return false;
            }
            final Color color = (Color) list.get(0);
            if (this.setColor(color) == false) {
                return false;
            }
        }
        
        // decimal places
        str = el.getAttribute(KEY_DECIMAL_PLACES);
        if (str.length() != 0) {
            num = SGUtilityText.getInteger(str);
            if (num == null) {
                return false;
            }
            if (num.intValue() < 0) {
                return false;
            }
            this.setDecimalPlaces(num.intValue());
        }
        
        // exponent
        str = el.getAttribute(KEY_EXPONENT);
        if (str.length() != 0) {
            num = SGUtilityText.getInteger(str);
            if (num == null) {
                return false;
            }
            final int exp = num.intValue();
            this.setExponent(exp);
        }
        
        // date format
        str = el.getAttribute(KEY_DATE_FORMAT);
        if (str.length() != 0) {
        	if (SGDateUtility.isValidDateFormat(str) == false) {
        		return false;
        	}
        	this.setDateFormat(str);
        }

        return true;
    }

    /**
     * 
     */
    public SGProperties getProperties() {
        StringProperties p = new StringProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    /**
     * 
     */
    public boolean getProperties(SGProperties p) {
        if ((p instanceof StringProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        StringProperties sp = (StringProperties) p;
        sp.setFontName(this.getFontName());
        sp.setFontSize(this.getFontSize());
        sp.setFontStyle(this.getFontStyle());
        sp.setAngle(this.getAngle());
        sp.setColor(this.getColor());
        sp.decimalPlaces = this.getDecimalPlaces();
        sp.exponent = this.getExponent();
        sp.dateFormat = this.getDateFormat();
        return true;
    }

    /**
     * 
     */
    public boolean setProperties(SGProperties p) {
        if ((p instanceof StringProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        StringProperties sp = (StringProperties) p;

        final Float fontSize = sp.getFontSize();
        if (fontSize == null) {
            return false;
        }

        final Integer fontStyle = sp.getFontStyle();
        if (fontStyle == null) {
            return false;
        }

        this.setFont(sp.getFontName(), fontStyle.intValue(), fontSize
                .floatValue());

        final Float angle = sp.getAngle();
        if (angle == null) {
            return false;
        }
        this.setAngle(angle.floatValue());
        
        this.setDecimalPlaces(sp.decimalPlaces);
        
        this.setExponent(sp.exponent);
        
        this.setDateFormat(sp.dateFormat);
        
        final Color cl = sp.getColor();
        if (cl == null) {
            return false;
        }
        this.setColor(cl);

        return true;
    }

    public static class StringProperties extends ElementGroupProperties {

        protected SGDrawingElementString.StringProperties stringProperties = new SGDrawingElementString.StringProperties();

        protected int decimalPlaces;
        
        protected int exponent;
        
        protected String dateFormat;
        
        public StringProperties() {
            super();
        }

        /**
         * Copy this object.
         * @return a copied object
         */
        public Object copy() {
            Object obj = super.copy();
            StringProperties p = (StringProperties) obj;
            p.stringProperties = (SGDrawingElementString.StringProperties) this.stringProperties.copy();
//            this.mDecimalPlaces = p.mDecimalPlaces;
//            this.mExponent = p.mExponent;
            return p;
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.stringProperties.dispose();
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof StringProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            StringProperties p = (StringProperties) obj;
            if (this.stringProperties.equals(p.stringProperties) == false) {
                return false;
            }
            if (this.decimalPlaces != p.decimalPlaces) {
                return false;
            }
            if (this.exponent != p.exponent) {
                return false;
            }
            if (!SGUtility.equals(this.dateFormat, p.dateFormat)) {
            	return false;
            }
            return true;
        }

        public Float getFontSize() {
            return this.stringProperties.getFontSize();
        }

        public Integer getFontStyle() {
            return this.stringProperties.getFontStyle();
        }

        public String getFontName() {
            return this.stringProperties.getFontName();
        }

        public Float getAngle() {
            return this.stringProperties.getAngle();
        }
        
        public Color getColor() {
            return this.stringProperties.getColor();
        }

        public boolean setFontSize(final float size) {
            return this.stringProperties.setFontSize(size);
        }

        public boolean setFontStyle(final int style) {
            return this.stringProperties.setFontStyle(style);
        }

        public boolean setFontName(final String name) {
            return this.stringProperties.setFontName(name);
        }

        public boolean setAngle(final float angle) {
            return this.stringProperties.setAngle(angle);
        }
        
        public boolean setColor(final Color cl) {
            return this.stringProperties.setColor(cl);
        }
    }

    /**
     * Sets the magnification.
     * 
     * @param mag
     *           the magnification to set
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        if (super.setMagnification(mag) == false) {
            return false;
        }
        if (this.mDrawingElementArray != null) {
            for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
                this.mDrawingElementArray[ii].setMagnification(mag);
            }
        }
        return true;
    }
    
    public boolean setDateFormat(final String format) {
//    	boolean found = false;
//    	for (String f : DATE_DISPLAY_FORMAT_ARRAY) {
//    		if (f.equals(format)) {
//    			found = true;
//    			break;
//    		}
//    	}
//    	if (!found) {
//    		return false;
//    	}
        this.mDateFormat = format;
        return true;
    }
    
    public String getDateFormat() {
    	return this.mDateFormat;
    }
}
