package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import java.awt.Font;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

import org.w3c.dom.Element;

/**
 * Drawing element of the text string.
 * 
 */
public abstract class SGDrawingElementString extends SGDrawingElement implements
        SGIStringConstants, SGIDrawingElementConstants {

    /**
     * The magnification.
     */
    protected float mMagnification = 1.0f;

    /**
     * The color.
     */
    protected Color mColor = null;
    
    /**
     * Text for this string element.
     */
    private String mString = null;

    /**
     * Font used to draw string actually. This object is updated with the
     * primary information source: font size, font style, font name and the
     * magnification.
     */
    private Font mFont = null;

    /**
     * Font name.
     */
    private String mFontName = DEFAULT_STRING_FONT_NAME;

    /**
     * Font style.
     */
    private int mFontStyle = DEFAULT_STRING_FONT_STYLE;

    /**
     * Font size in the default zoom.
     */
    private float mFontSize = DEFAULT_STRING_FONT_SIZE;

    /**
     * Angled of this string.
     */
    private float mAngle = DEFAULT_STRING_ANGLE;

    /**
     * The location of this symbol.
     */
    private SGTuple2f mLocation = new SGTuple2f();

    /**
     * Default constructor.
     */
    public SGDrawingElementString() {
        super();
        this.mString = "";
        this.mColor = DEFAULT_STRING_FONT_COLOR;
        this.updateFont();
    }

    /**
     * Construct a string element with given text.
     */
    public SGDrawingElementString(final String str) {
        super();
        this.mString = str;
        this.mColor = DEFAULT_STRING_FONT_COLOR;
        this.updateFont();
    }

    /**
     * Construct a string element with given string element.
     */
    public SGDrawingElementString(final SGDrawingElementString element) {
        super();
        this.mString = element.mString;
        this.mLocation.setValues(element.mLocation);
        this.mFontName = element.getFontName();
        this.mFontStyle = element.getFontStyle();
        this.mFontSize = element.getFontSize();
        this.mAngle = element.mAngle;
        this.updateFont();
    }

    /**
     * Construct a string element with given text and font information.
     */
    public SGDrawingElementString(final String str, final String fontName,
            final int fontStyle, final float fontSize, final Color color,
            final float mag, final float angle) {
        super();
        this.mString = str;
        this.mFontName = fontName;
        this.mFontStyle = fontStyle;
        this.mFontSize = fontSize;
        this.mAngle = angle;
        this.mColor = color;
        this.mMagnification = mag;
        this.updateFont();
    }

    /**
     * Disposes this object.
     * 
     */
    public void dispose() {
        super.dispose();
        this.mString = null;
        this.mFont = null;
        this.mFontName = null;
    }

    /**
     * Returns the text of this string element.
     */
    public final String toString() {
        return this.getString();
    }

    /**
     * Sets the text string.
     * 
     * @param str
     *            a text to set
     * @return true if succeeded
     */
    public boolean setString(final String str) {
        this.mString = str;
        return true;
    }

    public float getMagnification() {
        return this.mMagnification;
    }
    
    /**
     * Set the magnification.
     * 
     * @param mag
     *            the magnification to set
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        if (mag <= 0.0) {
            throw new IllegalArgumentException("mag <= 0.0");
        }
        this.mMagnification = mag;

        // update the attribute
        this.updateFont();

        return true;
    }

    /**
     * Sets the font name.
     * 
     * @param name
     *            the font name to set
     * @return true if succeeded
     */
    public final boolean setFontName(final String name) {
        return this.setFont(name, this.mFontStyle, this.mFontSize);
    }

    /**
     * Sets the font style.
     * 
     * @param style
     *            the font style to set
     * @return true if succeeded
     */
    public final boolean setFontStyle(final int style) {
        if (SGUtilityText.isValidFontStyle(style) == false) {
            return false;
        }
        return this.setFont(this.mFontName, style, this.mFontSize);
    }

    /**
     * Sets the font size.
     * 
     * @param size
     *            the font size to set
     * @return true if succeeded
     */
    public final boolean setFontSize(final float size) {
        return this.setFont(this.mFontName, this.mFontStyle, size);
    }

    /**
     * Set the font size in a given unit.
     * 
     * @param size
     *            the font size to set
     * @param unit
     *            a unit of length
     * @return true if succeeded
     */
    public final boolean setFontSize(final float size, final String unit) {
        final Float sNew = SGUtility.getFontSize(size, unit);
        if (sNew == null) {
            return false;
        }
        return this.setFontSize(sNew.floatValue());
    }

    /**
     * Sets the font.
     * 
     * @param name
     *            font name
     * @param style
     *            font style
     * @param size
     *            font size
     * @return true if succeeded
     */
    public boolean setFont(final String name, final int style, final float size) {
        this.mFontName = name;
        this.mFontStyle = style;
        this.mFontSize = size;

        // update the attribute
        this.updateFont();

        return true;
    }

    /**
     * Update the font object in attributes.
     */
    private boolean updateFont() {
        this.mFont = new Font(this.mFontName, this.mFontStyle, (int) (this
                .getMagnification() * this.mFontSize));
        return true;
    }

    /**
     * Sets the location of this symbol.
     * 
     * @param x
     *          the x coordinate to set
     * @param y
     *          the y coordinate to set
     * @return true if succeeded
     */
    public boolean setLocation(final float x, final float y) {
        this.mLocation.setValues(x, y);
        return true;
    }

    /**
     * Sets the location of this symbol.
     * 
     * @param pos
     *           the location to set
     * @return true if succeeded
     */
    public boolean setLocation(final SGTuple2f pos) {
        this.mLocation = pos;
        return true;
    }

    /**
     * Sets the angle of this string.
     * 
     * @param angle
     *            the angle to be set in units of degree
     * @return true if succeeded
     */
    public boolean setAngle(final float angle) {
        final Float aNew = SGUtility.calcPropertyValue(angle, null, null, 
                STRING_ANGLE_MIN, STRING_ANGLE_MAX, STRING_ANGLE_MINIMAL_ORDER);
        if (aNew == null) {
        	return false;
        }
        this.mAngle = aNew.floatValue();
        return true;
    }

    /**
     * Returns a string object.
     * 
     * @return string object
     */
    public final String getString() {
        return this.mString;
    }

    /**
     * Returns the location of this symbol.
     * 
     * @return the location of this symbol
     */
    public SGTuple2f getLocation() {
        return this.mLocation;
    }

    /**
     * Returns the x coordinate of the location of this symbol.
     * 
     * @return the x coordinate of the location of this symbol 
     */
    public float getX() {
        return this.mLocation.x;
    }

    /**
     * Returns the y coordinate of the location of this symbol.
     * 
     * @return the y coordinate of the location of this symbol 
     */
    public float getY() {
        return this.mLocation.y;
    }

    /**
     * Returns the font.
     * 
     * @return font
     */
    public final Font getFont() {
        return this.mFont;
    }

    /**
     * Returns the font name.
     * 
     * @return font name
     */
    public final String getFontName() {
        return this.mFontName;
    }

    /**
     * Returns the font size in the default zoom.
     * 
     * @return font size in the default zoom
     */
    public final float getFontSize() {
        return this.mFontSize;
    }

    public float getFontSize(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getFontSize(),
                unit);
    }

    /**
     * Returns the font style.
     * 
     * @return font style
     */
    public final int getFontStyle() {
        return this.mFontStyle;
    }

    /**
     * Returns the angle of this string.
     * 
     * @return angle of this string
     */
    public final float getAngle() {
        return this.mAngle;
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
     * @param color
     *           the color to set
     * @return true if succeeded
     */
    public boolean setColor(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        this.mColor = color;
        return true;
    }

    /**
     * 
     */
    public SGProperties getProperties() {
        StringProperties p = new StringProperties();
        this.getProperties(p);
        return p;
    }

    /**
     * 
     */
    public boolean getProperties(SGProperties p) {
        if (p == null)
            return false;
        if ((p instanceof StringProperties) == false)
            return false;

        super.getProperties(p);

        StringProperties sp = (StringProperties) p;
        sp.setFontName(this.mFontName);
        sp.setFontSize(this.mFontSize);
        sp.setFontStyle(this.mFontStyle);
        sp.setAngle(this.mAngle);

        return true;
    }

    /**
     * 
     */
    public boolean setProperties(SGProperties p) {
        if ((p instanceof StringProperties) == false)
            return false;

        if (super.setProperties(p) == false)
            return false;

        StringProperties ep = (StringProperties) p;

        this.setFont(ep.getFontName(), ep.getFontStyle().intValue(), ep
                .getFontSize().floatValue());
        this.setAngle(ep.getAngle().floatValue());
        this.setColor(ep.getColor());

        return true;

    }

    public boolean writeProperty(final Element el, SGExportParameter params) {
    	SGPropertyMap map = this.getPropertyFileMap(params);
    	map.setToElement(el);
        return true;
    }

    /**
     * Creates and returns the map of properties.
     * 
     * @return the map of properties
     */
    public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	SGPropertyUtility.addQuotedStringProperty(map, COM_LABEL_TEXT, this.getString());
    	this.addProperties(map, COM_LABEL_FONT_NAME, COM_LABEL_FONT_SIZE, 
    			COM_LABEL_FONT_STYLE, COM_LABEL_FONT_COLOR, COM_LABEL_ANGLE);
    	return map;
    }
    
	public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	SGPropertyUtility.addProperty(map, KEY_TEXT, this.getString());
    	this.addProperties(map, KEY_FONT_NAME, KEY_FONT_SIZE, KEY_FONT_STYLE, KEY_STRING_COLORS, 
    			KEY_ANGLE);
    	return map;
	}

    private void addProperties(SGPropertyMap map, String fontNameKey, String fontSizeKey,
    		String fontStyleKey, String colorKey, String angleKey) {
        SGPropertyUtility.addProperty(map, fontNameKey, this.getFontName());
        SGPropertyUtility.addProperty(map, fontSizeKey,
        		SGUtility.getExportFontSize(this.getFontSize(FONT_SIZE_UNIT)), 
        		FONT_SIZE_UNIT);
        SGPropertyUtility.addProperty(map, fontStyleKey, 
        		SGUtilityText.getFontStyleName(this.getFontStyle()));
        SGPropertyUtility.addProperty(map, colorKey, this.getColor());
        SGPropertyUtility.addProperty(map, angleKey, 
        		SGUtility.getExportValue(this.getAngle(), STRING_ANGLE_MINIMAL_ORDER));
    }

    /**
     * 
     */
    public boolean readProperty(final Element el) {
        String str = null;
        Number num = null;

        // set the text
        str = el.getAttribute(KEY_TEXT);
        if (str.length() != 0) {
            final String text = str;
            if (this.setString(text) == false) {
                return false;
            }
        }

        // set font name
        str = el.getAttribute(KEY_FONT_NAME);
        if (str.length() != 0) {
            final String fontName = str;
            if (this.setFontName(fontName) == false) {
                return false;
            }
        }

        // set font size
        str = el.getAttribute(KEY_FONT_SIZE);
        if (str.length() != 0) {
            StringBuffer uFontSize = new StringBuffer();
            num = SGUtilityText.getNumber(str, uFontSize);
            if (num == null) {
                return false;
            }
            final float fontSize = num.floatValue();
            if (this.setFontSize(fontSize, uFontSize.toString()) == false) {
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

        // set color
        str = el.getAttribute(KEY_STRING_COLORS);
        if (str.length() != 0) {
            final Color color = SGUtilityText.parseColorIncludingList(str);
            if (color == null) {
            	return false;
            }
            if (this.setColor(color) == false) {
                return false;
            }
        }

        // set angle
        str = el.getAttribute(KEY_ANGLE);
        if (str.length() != 0) {
            num = SGUtilityText.getFloat(str, SGIConstants.degree);
            if (num == null) {
                return false;
            }
            final float angle = num.floatValue();
            if (this.setAngle(angle) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * 
     */
    public static class StringProperties extends DrawingElementProperties {

        private float mFontSize = 0.0f;

        private int mFontStyle = -1;

        private String mFontName = null;

        private float mAngle = 0.0f;
        
        private Color mColor = null;

        /**
         * 
         * 
         */
        public StringProperties() {
            super();
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
            if (this.mFontSize != p.mFontSize) {
                return false;
            }
            if (this.mFontStyle != p.mFontStyle) {
                return false;
            }
            if (this.mFontName.equals(p.mFontName) == false) {
                return false;
            }
            if (this.mAngle != p.mAngle) {
                return false;
            }
            if (SGUtility.equals(this.mColor, p.mColor) == false) {
                return false;
            }
            return true;
        }

        public String getFontName() {
            return this.mFontName;
        }

        public Float getFontSize() {
            return Float.valueOf(this.mFontSize);
        }

        public Integer getFontStyle() {
            return Integer.valueOf(this.mFontStyle);
        }

        public Float getAngle() {
            return Float.valueOf(this.mAngle);
        }
        
        public Color getColor() {
            return this.mColor;
        }

        public boolean setFontName(final String name) {
            this.mFontName = name;
            return true;
        }

        public boolean setFontSize(final float size) {
            this.mFontSize = size;
            return true;
        }

        public boolean setFontStyle(final int style) {
            this.mFontStyle = style;
            return true;
        }

        public boolean setAngle(final float angle) {
            this.mAngle = angle;
            return true;
        }

        public boolean setColor(final Color cl) {
            this.mColor = cl;
            return true;
        }
    }

}
