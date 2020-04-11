package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class SGSelectablePaint extends SGTransparentPaint {
    
    public static final String NAME_UNKNOWN = "UNKNOWN";
    
    public static final String STYLE_NAME_FILL = "Fill";
    
    public static final String STYLE_NAME_PATTERN = "Pattern";
    
    public static final String STYLE_NAME_GRADATION = "Gradation";
    
    public static final int STYLE_INDEX_FILL = 0;
    
    public static final int STYLE_INDEX_PATTERN = 1;
    
    public static final int STYLE_INDEX_GRADATION = 2;
    
    public static final int[] STYLE_INDEXES = {
        STYLE_INDEX_FILL,
        STYLE_INDEX_PATTERN,
        STYLE_INDEX_GRADATION
    };
    
    public static final String[] STYLE_NAMES = {
        STYLE_NAME_FILL,
        STYLE_NAME_PATTERN,
        STYLE_NAME_GRADATION
    };
    
    protected SGFillPaint mFillPaint;
    
    protected SGPatternPaint mPatternPaint;
    
    protected SGGradationPaint mGradationPaint;
    
    protected int mPaintStyleIndex;
    
    public SGSelectablePaint() {
        this.mFillPaint = new SGFillPaint();
        this.mPatternPaint = new SGPatternPaint();
        this.mGradationPaint = new SGGradationPaint();
        init();
    }
    
    protected void init() {
        this.mPaintStyleIndex = STYLE_INDEX_FILL;
    }

    @Override
    public Paint getPaint(final Rectangle2D rect) {
        Paint paint = null;
        switch (this.mPaintStyleIndex) {
        case STYLE_INDEX_FILL :
            paint = this.mFillPaint.getPaint(rect);
            break;
        case STYLE_INDEX_PATTERN :
            paint = this.mPatternPaint.getPaint(rect);
            break;
        case STYLE_INDEX_GRADATION :
            paint = this.mGradationPaint.getPaint(rect);
            break;
        default :
            break;
        }
        if (null == paint) {
            return new SGNullPaint().getPaint(rect);
        } else {
            return paint;
        }
    }
    @Override
    public boolean setAlpha(final float alpha) {
        if (super.setAlpha(alpha)) {
            this.mFillPaint.setAlpha(alpha);
            this.mPatternPaint.setAlpha(alpha);
            this.mGradationPaint.setAlpha(alpha);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean setMagnification(float mag) {
        if (super.setMagnification(mag)) {
            this.mFillPaint.setMagnification(mag);
            this.mPatternPaint.setMagnification(mag);
            this.mGradationPaint.setMagnification(mag);
            return true;
        }
        return false;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        SGSelectablePaint paint = (SGSelectablePaint)super.clone();
        paint.mFillPaint = (SGFillPaint)this.mFillPaint.clone();
        paint.mPatternPaint = (SGPatternPaint)this.mPatternPaint.clone();
        paint.mGradationPaint = (SGGradationPaint)this.mGradationPaint.clone();
        return paint;
    }
    
    public Color getFillColor() {
        return (Color)this.mFillPaint.getPaint(null);
    }
    
    public Color getPatternColor() {
        return this.mPatternPaint.getColor();
    }
    
    public Integer getPatternIndex() {
        return Integer.valueOf(this.mPatternPaint.getTypeIndex());
    }
    
    public SGPatternPaint getPatternPaint() {
        try {
            return (SGPatternPaint)this.mPatternPaint.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
    }
    
    public SGGradationPaint getGradationPaint() {
        try {
            return (SGGradationPaint)this.mGradationPaint.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
    }
    
    public Integer getSelectedStyle() {
        return Integer.valueOf(this.mPaintStyleIndex);
    }
    
    public boolean setFillColor(final Color color) {
        return this.mFillPaint.setColor(color);
    }
    
    public boolean setPatternPaint(final SGPatternPaint paint) {
        if (null == paint) {
            return false;
        }
        try {
            this.mPatternPaint = (SGPatternPaint)paint.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
        return true;
    }
    
    public boolean setPatternColor(final Color color) {
        return this.mPatternPaint.setColor(color);
    }
    
    public boolean setPatternIndex(final int type) {
        return this.mPatternPaint.setTypeIndex(type);
    }
    
    public boolean setGradationPaint(final SGGradationPaint gpaint) {
        if (null == gpaint) {
            return false;
        }
        this.mGradationPaint.setColors(gpaint.getColors());
        this.mGradationPaint.setDirection(gpaint.getDirectionIndex());
        this.mGradationPaint.setOrder(gpaint.getOrderIndex());
        return true;
    }
    
    public boolean setGradationColor1(final Color color) {
        if (null == color) {
            return false;
        }
        return this.mGradationPaint.setColor1(color);
    }
    
    public boolean setGradationColor2(final Color color) {
        if (null == color) {
            return false;
        }
        return this.mGradationPaint.setColor2(color);
    }
    
    public boolean setGradationDirection(final int direction) {
        return this.mGradationPaint.setDirection(direction);
    }
    
    public boolean setGradationOrder(final int order) {
        return this.mGradationPaint.setOrder(order);
    }
    
    public boolean setSelectedPaintStyle(final int style) {
        if (!isValidStyle(style)) {
            return false;
        }
        this.mPaintStyleIndex = style;
        return true;
    }
    
    /**
     * Returns whether a given paint style is valid.
     * 
     * @param style
     *           a paint style
     * @return true if the given paint style is valid
     */
    public static boolean isValidStyle(final int style) {
        final int[] array = STYLE_INDEXES;
        for (int i = 0; i < array.length; i++) {
            if (style == array[i]) {
                return true;
            }
        }
        return false;
    }
    
    public static String getStyleName(final int style) {
        if (! isValidStyle(style)) {
            return NAME_UNKNOWN;
        }
        switch (style) {
        case STYLE_INDEX_FILL :
            return STYLE_NAME_FILL;
        case STYLE_INDEX_PATTERN :
            return STYLE_NAME_PATTERN;
        case STYLE_INDEX_GRADATION :
            return STYLE_NAME_GRADATION;
        default :
            return NAME_UNKNOWN;
        }
    }
    
    public static Integer getStyleIndex(final String styleName) {
        for (int i = 0; i < STYLE_NAMES.length; i++) {
            if (SGUtilityText.isEqualString(styleName, STYLE_NAMES[i])) {
                return Integer.valueOf(STYLE_INDEXES[i]);
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SGSelectablePaint)) return false;
        SGSelectablePaint o = (SGSelectablePaint)obj;
        if (! super.equals(o)) return false;
        if (o.mPaintStyleIndex!=this.mPaintStyleIndex) return false;
        if (!o.mFillPaint.equals(this.mFillPaint)) return false;
        if (!o.mPatternPaint.equals(this.mPatternPaint)) return false;
        if (!o.mGradationPaint.equals(this.mGradationPaint)) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 37*result+this.mFillPaint.hashCode();
        result = 37*result+this.mPatternPaint.hashCode();
        result = 37*result+this.mGradationPaint.hashCode();
        result = 37*result+this.mPaintStyleIndex;
        return result;
    }
    
    public static enum COMMAND_KEYS {
    	PAINT_STYLE,
    	FILL_COLOR,
    	PATTERN_COLOR,
    	PATTERN_TYPE,
    	GRADATION_COLOR1,
    	GRADATION_COLOR2,
    	GRADATION_DIRECTION,
    	GRADATION_ORDER,
    	TRANSPARENCY,
    };

    public boolean getProperties(SGPropertyMap map, Map<COMMAND_KEYS, String> keyMap) {
    	if (keyMap == null) {
    		return false;
    	}
    	
    	SGPropertyUtility.addProperty(map, keyMap.get(COMMAND_KEYS.PAINT_STYLE),
    			SGSelectablePaint.getStyleName(this.getSelectedStyle()));
    	
		// fill paint
		SGPropertyUtility.addProperty(map, keyMap.get(COMMAND_KEYS.FILL_COLOR),
				this.getFillColor());

		// pattern color
		SGPropertyUtility.addProperty(map, keyMap.get(COMMAND_KEYS.PATTERN_COLOR),
				this.getPatternColor());
		
		// pattern type
		final int patternIndex = this.getPatternIndex();
		String patternName = SGPatternPaint.getTypeName(patternIndex);
    	SGPropertyUtility.addProperty(map, keyMap.get(COMMAND_KEYS.PATTERN_TYPE),
    			patternName);
    	
		// colors
		Color[] colors = this.getGradationPaint().getColors();
		SGPropertyUtility.addProperty(map, keyMap.get(COMMAND_KEYS.GRADATION_COLOR1),
				colors[0]);
		SGPropertyUtility.addProperty(map, keyMap.get(COMMAND_KEYS.GRADATION_COLOR2),
				colors[1]);
		
		// direction
		final int directionIndex = this.getGradationPaint().getDirectionIndex();
		String directionName = SGGradationPaint.getDirectionName(directionIndex);
    	SGPropertyUtility.addProperty(map, keyMap.get(COMMAND_KEYS.GRADATION_DIRECTION),
    			directionName);
    	
    	// order
    	final int orderIndex = this.getGradationPaint().getOrderIndex();
    	String orderName = SGGradationPaint.getOrderName(orderIndex);
    	SGPropertyUtility.addProperty(map, keyMap.get(COMMAND_KEYS.GRADATION_ORDER),
    			orderName);

    	SGTransparentPaint transPaint = (SGTransparentPaint) this;
    	SGPropertyUtility.addProperty(map, keyMap.get(COMMAND_KEYS.TRANSPARENCY),
    			transPaint.getTransparencyPercent());

    	return true;
    }
    
    @Override
	public SGPropertyMap getPropertyFileMap() {
    	SGPropertyMap map = super.getPropertyFileMap();
    	SGPropertyUtility.addProperty(map, KEY_PAINT_STYLE,
    			SGSelectablePaint.getStyleName(this.getSelectedStyle()));
    	SGPropertyUtility.addProperty(map, KEY_FILL_COLOR,
    			SGUtilityText.getColorString(this.getFillColor()));
    	SGPropertyUtility.addProperty(map, KEY_PATTERN_COLOR,
    			SGUtilityText.getColorString(this.getPatternColor()));
    	SGPropertyUtility.addProperty(map, KEY_PATTERN_TYPE,
    			SGPatternPaint.getTypeName(this.getPatternIndex()));
    	
    	SGGradationPaint gradPaint = this.getGradationPaint();
    	map.putAll(gradPaint.getPropertyFileMap());
    	return map;
	}

}
