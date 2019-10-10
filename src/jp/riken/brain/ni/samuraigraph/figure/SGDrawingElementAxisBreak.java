package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import java.util.List;

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
 * Drawing element of axis break symbol.
 */
public abstract class SGDrawingElementAxisBreak extends SGDrawingElement
        implements SGIAxisBreakConstants, SGIDrawingElementConstants {

    /**
     * The location of this symbol.
     */
    private SGTuple2f mLocation = new SGTuple2f();

    /**
     * 
     */
    private float mLength;

    /**
     * 
     */
    private float mInterval;

    /**
     * 
     */
    private float mLineWidth;

    /**
     * 
     */
    private float mAngle;

    /**
     * Value in the range [-1,1]
     */
    private float mDistortion;

    /**
     * The line color.
     */
    private Color mLineColor;

    /**
     * The inner color.
     */
    private Color mInnerColor;
    
    /**
     * 
     */
    private boolean mForHorizontalAxisFlag;

    /**
     * 
     * 
     */
    public SGDrawingElementAxisBreak() {
        super();
    }

    /**
     * 
     */
    protected SGDrawingElementAxisBreak(final float length,
            final String lengthUnit, final float interval,
            final String intervalUnit, final float dist, final float angle,
            final boolean horizontal) {
        this.setLength(length, lengthUnit);
        this.setInterval(interval, intervalUnit);
        this.setDistortion(dist);
        this.setAngle(angle);
        this.setForHorizontalAxisFlag(horizontal);
    }

    /**
     * 
     */
    public void dispose() {
        super.dispose();
        this.mLineColor = null;
        this.mLocation = null;
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
     * 
     * @return
     */
    public final float getLength() {
        return this.mLength;
    }

    /**
     * 
     * @return
     */
    public final float getInterval() {
        return this.mInterval;
    }

    /**
     * 
     * @return
     */
    public final float getLineWidth() {
        return this.mLineWidth;
    }

    /**
     * 
     * @return
     */
    public final float getAngle() {
        return this.mAngle;
    }

    /**
     * 
     * @return
     */
    public final float getDistortion() {
        return this.mDistortion;
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
     * Returns the inner color.
     * 
     * @return the inner color
     */
    public Color getInnerColor() {
        return this.mInnerColor;
    }

    /**
     * 
     * @return
     */
    public boolean isForHorizontalAxis() {
        return this.mForHorizontalAxisFlag;
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
     * Sets the length.
     * 
     * @param len
     *           the length to set
     * @return true if succeeded
     */
    public boolean setLength(final float len) {
        if (len < 0.0f) {
            throw new IllegalArgumentException("len < 0.0f");
        }
        this.mLength = len;
        return true;
    }

    /**
     * Sets the interval.
     * 
     * @param interval
     *           the interval to set
     * @return true if succeeded
     */
    public boolean setInterval(final float interval) {
        if (interval < 0.0f) {
            throw new IllegalArgumentException("interval < 0.0f");
        }
        this.mInterval = interval;
        return true;
    }

    /**
     * Sets the line width.
     * 
     * @param lw
     *           the line width to set
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
     * Sets the angle.
     * 
     * @param angle
     *           the angle to set
     * @return true if succeeded
     */
    public boolean setAngle(final float angle) {
		final Float aNew = SGUtility.calcPropertyValue(angle, null,
				null, AXIS_BREAK_ANGLE_MIN, AXIS_BREAK_ANGLE_MAX, 
				AXIS_BREAK_DISTORTION_MINIMAL_ORDER);
		if (aNew == null) {
			return false;
		}
        this.mAngle = angle;
        return true;
    }
    
    /**
     * Sets the distortion.
     * 
     * @param distortion
     *           distortion to set
     * @return true if succeeded
     */
    public boolean setDistortion(final float distortion) {
		final Float dNew = SGUtility.calcPropertyValue(distortion, null,
				null, AXIS_BREAK_DISTORTION_MIN,
				AXIS_BREAK_DISTORTION_MAX, AXIS_BREAK_DISTORTION_MINIMAL_ORDER);
		if (dNew == null) {
			return false;
		}
        this.mDistortion = distortion;
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
            throw new IllegalArgumentException("color==null");
        }
        this.mLineColor = color;
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
            throw new IllegalArgumentException("color==null");
        }
        this.mInnerColor = color;
        return true;
    }
    
    /**
     * 
     * @param flag
     */
    public boolean setForHorizontalAxisFlag(final boolean flag) {
        this.mForHorizontalAxisFlag = flag;
        return true;
    }

    public float getLineWidth(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getLineWidth(),
                unit);
    }

    public float getLength(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getLength(),
                unit);
    }

    public float getInterval(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getInterval(),
                unit);
    }

    /**
     * Sets the line width in a given unit.
     * 
     * @param lw
     *           the line width to set
     * @param unit
     *           a unit of length
     * @return true if succeeded
     */
    public boolean setLineWidth(final float lw, final String unit) {
        final Float lwNew = SGUtility.getLineWidth(lw, unit);
        if (lwNew == null) {
            return false;
        }
        if (this.setLineWidth(lwNew) == false) {
            return false;
        }
        return true;
    }

    /**
     * Sets the length in a given unit.
     * 
     * @param len
     *           the length to set
     * @param unit
     *           a unit of length
     * @return true if succeeded
     */
    public boolean setLength(final float len, final String unit) {
		final Float sNew = SGUtility.calcPropertyValue(len, unit,
				AXIS_BREAK_LENGTH_UNIT, AXIS_BREAK_LENGTH_MIN,
				AXIS_BREAK_LENGTH_MAX, AXIS_BREAK_LENGTH_MINIMAL_ORDER);
		if (sNew == null) {
			return false;
		}
		if (this.setLength(sNew) == false) {
			return false;
		}
		return true;
	}

    /**
     * Sets the interval in a given unit.
     * 
     * @param interval
     *           the interval to set
     * @param unit
     *           a unit of length
     * @return true if succeeded
     */
    public boolean setInterval(final float interval, final String unit) {
		final Float iNew = SGUtility.calcPropertyValue(interval, unit,
				AXIS_BREAK_INTERVAL_UNIT, AXIS_BREAK_INTERVAL_MIN,
				AXIS_BREAK_INTERVAL_MAX, AXIS_BREAK_INTERVAL_MINIMAL_ORDER);
		if (iNew == null) {
			return false;
		}
		if (this.setInterval(iNew) == false) {
			return false;
		}
		return true;
    }


    /**
     * 
     */
    public boolean setProperties(SGProperties p) {

        if ((p instanceof AxisBreakSymbolProperties) == false)
            return false;

        if (super.setProperties(p) == false)
            return false;

        AxisBreakSymbolProperties ap = (AxisBreakSymbolProperties) p;

        final Float len = ap.getLength();
        if (len == null) {
            return false;
        }
        this.mLength = len.floatValue();

        final Float interval = ap.getInterval();
        if (interval == null) {
            return false;
        }
        this.mInterval = interval.floatValue();

        final Float dist = ap.getDistortion();
        if (dist == null) {
            return false;
        }
        this.mDistortion = dist.floatValue();

        final Float angle = ap.getAngle();
        if (angle == null) {
            return false;
        }
        this.mAngle = angle.floatValue();

        final Boolean horizontal = ap.isHorizontal();
        if (horizontal == null) {
            return false;
        }
        this.mForHorizontalAxisFlag = horizontal.booleanValue();

        final Float lw = ap.getLineWidth();
        if (lw == null) {
            return false;
        }
        this.mLineWidth = lw.floatValue();

        final Color lc = ap.getLineColor();
        if (lc == null) {
            return false;
        }
        this.mLineColor = lc;

        final Color ic = ap.getInnerColor();
        if (ic == null) {
            return false;
        }
        this.mInnerColor = ic;

        return true;

    }

    /**
     * 
     * @return
     */
    public SGProperties getProperties() {
        final AxisBreakSymbolProperties p = new AxisBreakSymbolProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    /**
     * 
     * @param p
     * @return
     */
    public boolean getProperties(final SGProperties p) {
        if ((p instanceof AxisBreakSymbolProperties) == false) {
            return false;
        }

        if (super.getProperties(p) == false) {
            return false;
        }

        final AxisBreakSymbolProperties dp = (AxisBreakSymbolProperties) p;
        dp.setLength(this.mLength);
        dp.setInterval(this.mInterval);
        dp.setDistortion(this.mDistortion);
        dp.setAngle(this.mAngle);
        dp.setHorizontal(this.mForHorizontalAxisFlag);
        dp.setLineWidth(this.mLineWidth);
        dp.setLineColor(this.mLineColor);
        dp.setInnerColor(this.mInnerColor);

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
    	this.addProperties(map, COM_BREAK_LENGTH, COM_BREAK_INTERVAL, COM_BREAK_DISTORTION, 
    			COM_BREAK_ANGLE, COM_BREAK_LINE_WIDTH, COM_BREAK_HORIZONTAL, 
    			COM_BREAK_LINE_COLOR, COM_BREAK_INNER_COLOR);
    	return map;
	}
	
	public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	this.addProperties(map, KEY_LENGTH, KEY_INTERVAL, KEY_DISTORTION, KEY_ANGLE, 
    			KEY_LINE_WIDTH, KEY_HORIZONTAL, KEY_LINE_COLOR, KEY_INNER_COLOR);
    	return map;
	}

    private void addProperties(SGPropertyMap map, String lengthKey, String intervalKey,
    		String distortionKey, String angleKey, String lineWidthKey, String horizontalKey,
    		String lineColorKey, String innerColorKey) {
        SGPropertyUtility.addProperty(map, lengthKey, 
        		SGUtility.getExportValue(this.getLength(AXIS_BREAK_LENGTH_UNIT), 
        				AXIS_BREAK_LENGTH_MINIMAL_ORDER), AXIS_BREAK_LENGTH_UNIT);
        SGPropertyUtility.addProperty(map, intervalKey,
        		SGUtility.getExportValue(this.getInterval(AXIS_BREAK_INTERVAL_UNIT), 
        				AXIS_BREAK_INTERVAL_MINIMAL_ORDER), AXIS_BREAK_INTERVAL_UNIT);
        SGPropertyUtility.addProperty(map, distortionKey, this.getDistortion());
        SGPropertyUtility.addProperty(map, angleKey, 
        		SGUtility.getExportValue(this.getAngle(), AXIS_BREAK_ANGLE_MINIMAL_ORDER));
        SGPropertyUtility.addProperty(map, lineWidthKey, 
        		SGUtility.getExportLineWidth(this.getLineWidth(LINE_WIDTH_UNIT)), 
        		LINE_WIDTH_UNIT);
        SGPropertyUtility.addProperty(map, horizontalKey, 
        		this.isForHorizontalAxis());
        SGPropertyUtility.addProperty(map, lineColorKey, this.getLineColor());
        SGPropertyUtility.addProperty(map, innerColorKey, this.getInnerColor());
    }

    /**
     * 
     */
    public boolean readProperty(final Element el) {
        String str = null;
        Number num = null;
        Color cl = null;
        Boolean b = null;
        List list = null;

        // width
        str = el.getAttribute(SGIAxisBreakConstants.KEY_LENGTH);
        if (str.length() != 0) {
            StringBuffer uLength = new StringBuffer();
            num = SGUtilityText.getNumber(str, uLength);
            if (num == null) {
                return false;
            }
            final float length = num.floatValue();
            if (this.setLength(length, uLength.toString()) == false) {
                return false;
            }
        }

        // interval
        str = el.getAttribute(SGIAxisBreakConstants.KEY_INTERVAL);
        if (str.length() != 0) {
            StringBuffer uInterval = new StringBuffer();
            num = SGUtilityText.getNumber(str, uInterval);
            if (num == null) {
                return false;
            }
            final float interval = num.floatValue();
            if (this.setInterval(interval, uInterval.toString()) == false) {
                return false;
            }
        }

        // distortion
        str = el.getAttribute(SGIAxisBreakConstants.KEY_DISTORTION);
        if (str.length() != 0) {
            num = SGUtilityText.getFloat(str);
            if (num == null) {
                return false;
            }
            final float distortion = num.floatValue();
            if (this.setDistortion(distortion) == false) {
                return false;
            }
        }

        // angle
        str = el.getAttribute(SGIAxisBreakConstants.KEY_ANGLE);
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

        // line width
        str = el.getAttribute(SGIAxisBreakConstants.KEY_LINE_WIDTH);
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

        // horizontal
        str = el.getAttribute(SGIAxisBreakConstants.KEY_HORIZONTAL);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return false;
            }
            final boolean horizontal = b.booleanValue();
            if (this.setForHorizontalAxisFlag(horizontal) == false) {
                return false;
            }
        }

        // line color
        str = el.getAttribute(SGIAxisBreakConstants.KEY_LINE_COLOR);
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

        // inner color
        str = el.getAttribute(SGIAxisBreakConstants.KEY_INNER_COLOR);
        if (str.length() != 0) {
        	cl = SGUtilityText.parseColorIncludingList(str);
        	if (cl == null) {
        		return false;
        	}
            this.mInnerColor = cl;
        }

        return true;
    }

    /**
     * Property of Axis Break Symbol.
     */
    public static class AxisBreakSymbolProperties extends
            SGDrawingElement.DrawingElementProperties {

        private float mX = 0.0f;

        private float mY = 0.0f;

        private float mLength = 0.0f;

        private float mInterval = 0.0f;

        private float mDistortion = 0.0f;

        private float mAngle = 0.0f;

        private float mLineWidth = 0.0f;

        private Color mLineColor = null;
        
        private Color mInnerColor = null;

        private boolean mHorizontal = true;

        /**
         * 
         */
        public AxisBreakSymbolProperties() {
            super();
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof AxisBreakSymbolProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            
            AxisBreakSymbolProperties p = (AxisBreakSymbolProperties) obj;
            if (this.mX != p.mX) {
                return false;
            }
            if (this.mY != p.mY) {
                return false;
            }
            if (this.mLength != p.mLength) {
                return false;
            }
            if (this.mInterval != p.mInterval) {
                return false;
            }
            if (this.mDistortion != p.mDistortion) {
                return false;
            }
            if (this.mAngle != p.mAngle) {
                return false;
            }
            if (this.mLineWidth != p.mLineWidth) {
                return false;
            }
//            if (this.mLineColor.equals(p.mLineColor) == false)
//                return false;
            if (SGUtility.equals(this.mLineColor, p.mLineColor) == false) {
                return false;
            }
            if (SGUtility.equals(this.mInnerColor, p.mInnerColor) == false) {
                return false;
            }
            if (this.mHorizontal != p.mHorizontal) {
                return false;
            }

            return true;
        }

        public Float getX() {
            return Float.valueOf(this.mX);
        }

        public Float getY() {
            return Float.valueOf(this.mY);
        }

        public Float getLength() {
            return Float.valueOf(this.mLength);
        }

        public Float getInterval() {
            return Float.valueOf(this.mInterval);
        }

        public Float getDistortion() {
            return Float.valueOf(this.mDistortion);
        }

        public Float getAngle() {
            return Float.valueOf(this.mAngle);
        }

        public Boolean isHorizontal() {
            return Boolean.valueOf(this.mHorizontal);
        }

        public Float getLineWidth() {
            return Float.valueOf(this.mLineWidth);
        }

        public Color getLineColor() {
            return this.mLineColor;
        }
        
        public Color getInnerColor() {
            return this.mInnerColor;
        }

        public boolean setX(final float x) {
            this.mX = x;
            return true;
        }

        public boolean setY(final float y) {
            this.mY = y;
            return true;
        }

        public boolean setLength(final float value) {
            this.mLength = value;
            return true;
        }

        public boolean setInterval(final float value) {
            this.mInterval = value;
            return true;
        }

        public boolean setDistortion(final float value) {
            this.mDistortion = value;
            return true;
        }

        public boolean setAngle(final float value) {
            this.mAngle = value;
            return true;
        }

        public boolean setHorizontal(final boolean b) {
            this.mHorizontal = b;
            return true;
        }

        public boolean setLineWidth(final float value) {
            this.mLineWidth = value;
            return true;
        }

        public boolean setLineColor(final Color cl) {
            this.mLineColor = cl;
            return true;
        }

        public boolean setInnerColor(final Color cl) {
            this.mInnerColor = cl;
            return true;
        }
    }

}
