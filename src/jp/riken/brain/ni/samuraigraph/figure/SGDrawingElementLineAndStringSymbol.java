package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

import org.w3c.dom.Element;

public abstract class SGDrawingElementLineAndStringSymbol extends SGDrawingElement
		implements SGILineAndStringConstants {

    /**
     * The location.
     */
    protected SGTuple2f mLocation = new SGTuple2f();

    /**
     * The color.
     */
    protected Color mColor;

    /**
     * The line width.
     */
    protected float mLineWidth;

    /**
     * Space between the line and the symbol.
     */
    protected float mSpace;
    
    /**
     * The font name.
     */
    protected String mFontName;

    /**
     * The font style.
     */
    protected int mFontStyle;
    
    /**
     * The font size.
     */
    protected float mFontSize;

    /**
     * The default constructor.
     */
    public SGDrawingElementLineAndStringSymbol() {
    	super();
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
        this.mColor = color;
        return true;
    }

    public float getLineWidth() {
        return this.mLineWidth;
    }

    public float getLineWidth(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getLineWidth(),
                unit);
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
     *           the line width
     * @param unit
     *           a unit of length
     * @return true if succeeded
     */
    public boolean setLineWidth(final float lw, final String unit) {
		final Float lwNew = SGUtility.getLineWidth(lw, unit);
		if (lwNew == null) {
			return false;
		}
		if (this.setLineWidth(lwNew.floatValue()) == false) {
			return false;
		}
		return true;
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
    
    public boolean setX(final float x) {
    	this.mLocation.setX(x);
    	return true;
    }

    public boolean setY(final float y) {
    	this.mLocation.setY(y);
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

    public float getSpace() {
        return this.mSpace;
    }

    /**
     * Sets the space.
     * 
     * @param space
     *           the space to set
     * @return true if succeeded
     */
    public boolean setSpace(final float space) {
        this.mSpace = space;
        return true;
    }

    /**
     * Sets the space in a given unit.
     * 
     * @param space
     *           the space
     * @param unit
     *           a unit of length
     * @return true if succeeded
     */
    public boolean setSpace(final float space, final String unit) {
		final Float sNew = SGUtility.calcPropertyValue(space, unit,
				SPACE_UNIT, SPACE_MIN,
				SPACE_MAX, SPACE_MINIMAL_ORDER);
		if (sNew == null) {
			return false;
		}
		if (this.setSpace(sNew) == false) {
			return false;
		}
		return true;
    }

    public float getSpace(final String unit) {
        return (float) SGUtilityText
                .convertFromPoint(this.getSpace(), unit);
    }

    public String getFontName() {
    	return this.mFontName;
    }

    /**
     * Sets the font name.
     * 
     * @param name
     *           the font name
     * @return true if succeeded
     */
    public boolean setFontName(final String name) {
    	this.mFontName = name;
    	return true;
    }

    public int getFontStyle() {
        return this.mFontStyle;
    }

    /**
     * Sets the font style.
     * 
     * @param style
     *           the font style
     * @return true if succeeded
     */
    public boolean setFontStyle(final int style) {
    	if (SGUtilityText.isValidFontStyle(style) == false) {
    		return false;
    	}
    	this.mFontStyle = style;
    	return true;
    }

    public float getFontSize() {
        return this.mFontSize;
    }

    public float getFontSize(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getFontSize(),
                unit);
    }

    /**
     * Sets the font size.
     * 
     * @param size
     *           the font size
     * @return true if succeeded
     */
    public boolean setFontSize(final float size) {
    	this.mFontSize = size;
    	return true;
    }

    /**
     * Sets the font size in a given unit.
     * 
     * @param size
     *           the font size to set
     * @param unit
     *           a unit of length
     * @return true if succeeded
     */
    public boolean setFontSize(final float size, final String unit) {
		final Float sNew = SGUtility.getFontSize(size, unit);
		if (sNew == null) {
			return false;
		}
		if (this.setFontSize(sNew.floatValue()) == false) {
			return false;
		}
		return true;
    }

    public boolean setFont(final String name, final int style, final float size) {
        if (size < 0.0f) {
            throw new IllegalArgumentException("size < 0.0f");
        }
        this.mFontName = name;
        this.mFontStyle = style;
        this.mFontSize = size;
        return true;
    }
    
    public static class LineAndStringProperties extends
			SGDrawingElement.DrawingElementProperties {
		
		float mSpace;

		float mLineWidth;

		String mFontName;

		float mFontSize;

		int mFontStyle;

		Color mColor;

		public LineAndStringProperties() {
			super();
		}

		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof LineAndStringProperties)) {
				return false;
			}
			if (super.equals(obj) == false) {
				return false;
			}

			LineAndStringProperties p = (LineAndStringProperties) obj;

			if (p.mSpace != this.mSpace) {
				return false;
			}
			if (p.mLineWidth != this.mLineWidth) {
				return false;
			}
			if (SGUtility.equals(p.mFontName, this.mFontName) == false) {
				return false;
			}
			if (p.mFontSize != this.mFontSize) {
				return false;
			}
			if (p.mFontStyle != this.mFontStyle) {
				return false;
			}
			if (SGUtility.equals(p.mColor, this.mColor) == false) {
				return false;
			}
			return true;
		}
	}

    @Override
    public SGProperties getProperties() {
    	LineAndStringProperties p = new LineAndStringProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    @Override
    public boolean getProperties(SGProperties p) {
        if ((p instanceof LineAndStringProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        LineAndStringProperties sp = (LineAndStringProperties) p;
        sp.mSpace = this.mSpace;
        sp.mLineWidth = this.mLineWidth;
        sp.mFontName = this.mFontName;
        sp.mFontSize = this.mFontSize;
        sp.mFontStyle = this.mFontStyle;
        sp.mColor = this.mColor;
        return true;
    }

    @Override
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof LineAndStringProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        LineAndStringProperties sp = (LineAndStringProperties) p;
        this.setSpace(sp.mSpace);
        this.setFont(sp.mFontName, sp.mFontStyle, sp.mFontSize);
        this.setLineWidth(sp.mLineWidth);
        this.setColor(sp.mColor);
        return true;
    }

    public boolean writeProperty(final Element el, SGExportParameter params) {
    	SGPropertyMap map = this.getPropertyFileMap(params);
    	map.setToElement(el);
        return true;
    }

    public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	this.addProperties(map, KEY_FONT_NAME, KEY_FONT_SIZE, KEY_FONT_STYLE, 
    			KEY_LINE_WIDTH, KEY_SPACE);
    	return map;
    }

    public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	this.addProperties(map, COM_FONT_NAME, COM_FONT_SIZE, COM_FONT_STYLE, 
    			COM_LINE_WIDTH, COM_SPACE);
    	return map;
    }
    
    private void addProperties(SGPropertyMap map, String fontNameKey, String fontSizeKey, 
    		String fontStyleKey, String lineWidthKey, 
    		String spaceKey) {
        SGPropertyUtility.addProperty(map, fontNameKey, this.getFontName());
        SGPropertyUtility.addProperty(map, fontSizeKey,
        		SGUtility.getExportFontSize(this.getFontSize(FONT_SIZE_UNIT)), 
        		FONT_SIZE_UNIT);
        SGPropertyUtility.addProperty(map, fontStyleKey, 
        		SGUtilityText.getFontStyleName(this.getFontStyle()));
        SGPropertyUtility.addProperty(map, lineWidthKey, 
        		SGUtility.getExportLineWidth(this.getLineWidth(LINE_WIDTH_UNIT)), 
        		LINE_WIDTH_UNIT);
        SGPropertyUtility.addProperty(map, spaceKey, 
        		SGUtility.getExportValue(this.getSpace(SPACE_UNIT), 
        				SPACE_MINIMAL_ORDER), SPACE_UNIT);
    }

    public boolean readProperty(final Element el) {
        String str = null;
        Number num = null;

        // font name
        str = el.getAttribute(KEY_FONT_NAME);
        if (str.length() != 0) {
            final String fontName = str;
            if (this.setFontName(fontName) == false) {
                return false;
            }
        }

        // font size
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

        // font style
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

        // line width
        str = el.getAttribute(KEY_LINE_WIDTH);
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

        // space
        str = el.getAttribute(KEY_SPACE);
        if (str.length() != 0) {
            StringBuffer uSpace = new StringBuffer();
            num = SGUtilityText.getNumber(str, uSpace);
            if (num == null) {
                return false;
            }
            final float space = num.floatValue();
            if (this.setSpace(space, uSpace.toString()) == false) {
                return false;
            }
        }

        return true;
    }

}
