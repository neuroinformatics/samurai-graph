package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;

import org.w3c.dom.Element;


public abstract class SGTransparentPaint implements SGIPaint {
    
    public static final float MIN_TRANSPARENCY = 0.0f;
    
    public static final float MAX_TRANSPARENCY = 1.0f;
    
    public static final float OPAQUE_THRESHOLD_VALUE = 0.98f;
    
    public static final int ALL_TRANSPARENT_VALUE = 100;
    
    public static final int ALL_OPAQUE_VALUE = 0;
    
    /**
     * the alpha value
     */
    protected float mAlpha;
    
    /**
     * magnification
     */
    protected float mMagnification = 1.0f;
    
    public SGTransparentPaint() {
        super();
        this.mAlpha = MAX_TRANSPARENCY;
    }

    @Override
    public float getAlpha() {
        return this.mAlpha;
    }

    @Override
    public boolean setAlpha(final float alpha) {
        if (alpha<MIN_TRANSPARENCY || alpha>MAX_TRANSPARENCY) {
            return false;
        }
        this.mAlpha = alpha;
        return true;
    }
    
    public int getTransparencyPercent() {
        return Math.round((1.0f-this.mAlpha)*100.0f);
    }
    
    /**
     * Set the alpha channel using percentage.
     * <p>
     * Opaque if percentAlpha=0, transparent all if percentAlpha=100.
     * @param percentAlpha
     * @return true if 0<=percentAlpha<=100.
     */
    public boolean setTransparency(final int percentAlpha) {
        return setAlpha((100-percentAlpha)*0.01f);
    }
    
    public float getMagnification() {
        return this.mMagnification;
    }

    @Override
    public boolean setMagnification(float mag) {
        if (mag <= 0.0f) {
            return false;
        }
        this.mMagnification = mag;
        return true;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        SGTransparentPaint paint = (SGTransparentPaint)super.clone();
        return paint;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SGTransparentPaint)) return false;
        SGTransparentPaint o = (SGTransparentPaint)obj;
        if (o.mAlpha!=this.mAlpha) return false;
        if (o.mMagnification!=this.mMagnification) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37*result+Float.floatToIntBits(this.mAlpha);
        result = 37*result+Float.floatToIntBits(this.mMagnification);
        return result;
    }
    
    public Color getTransparentedColor(final Color color) {
        if (this.mAlpha>OPAQUE_THRESHOLD_VALUE) {
            return color;
        }
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(255*this.mAlpha));
    }
    
    public static Color getOpaqueColor(final Color color) {
        return new Color(color.getRGB());
    }

    @Override
    public SGPropertyMap getPropertyFileMap() {
    	SGPropertyMap map = new SGPropertyMap();
    	SGPropertyUtility.addProperty(map, KEY_TRANSPARENT, 
    			this.getTransparencyPercent(), TRANSPARENCY_UNIT);
    	return map;
    }
    
    public boolean writeProperty(final Element el) {
	  	SGPropertyMap map = this.getPropertyFileMap();
	  	map.setToElement(el);
	  	return true;
    }

}
