package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

public class SGFillPaint extends SGTransparentPaint {
    
    public static final Color DEFAULT_COLOR = Color.WHITE;
    
    protected Color mColor;
    
    public SGFillPaint() {
        this(DEFAULT_COLOR);
    }
    
    public SGFillPaint(Color color) {
        this.mColor = color;
    }
    
    @Override
    public Paint getPaint(final Rectangle2D rect) {
        return getTransparentedColor(this.mColor);
    }
    
    public Color getColor() {
        return this.mColor;
    }
    
    public boolean setColor(final Color color) {
        if (null==color) {
            return false;
        }
        this.mColor = color;
        return true;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        SGFillPaint paint = (SGFillPaint)super.clone();
        return paint;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SGFillPaint)) return false;
        SGFillPaint o = (SGFillPaint)obj;
        if (! super.equals(o)) return false;
        if (!o.mColor.equals(this.mColor)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 37*result+this.mColor.hashCode();
        return result;
    }

    @Override
	public SGPropertyMap getPropertyFileMap() {
    	SGPropertyMap map = super.getPropertyFileMap();
    	SGPropertyUtility.addProperty(map, KEY_FILL_COLOR, (Color) this.getPaint(null));
    	return map;
	}

}
