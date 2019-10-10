package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGStyle;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

public class SGLineStyle extends SGStyle implements Cloneable {
	private Color mColor = Color.BLACK;
	private int mLineType = SGILineConstants.LINE_TYPE_SOLID;
	private float mLineWidth = 1.0f;

	public SGLineStyle(final int type, final Color cl, final float lineWidth) {
		super();
		this.mLineType = type;
		this.mColor = cl;
		this.mLineWidth = lineWidth;
	}
	
    /**
     * Clones this object.
     * 
     * @return copy of this data object
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

	public int getLineType() {
		return this.mLineType;
	}

	public Color getColor() {
		return this.mColor;
	}
	
	public float getLineWidth() {
		return this.mLineWidth;
	}
	
    public float getLineWidth(final String unit) {
        return (float) SGUtilityText
                .convertFromPoint(this.getLineWidth(), unit);
    }

	public void setLineType(final int type) {
		this.mLineType = type;
	}
	
	public void setColor(Color cl) {
		this.mColor = cl;
	}
	
	public void setLineWidth(final float lw) {
		this.mLineWidth = lw;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SGLineStyle)) {
			return false;
		}
		SGLineStyle s = (SGLineStyle) obj;
		if (!SGUtility.equals(this.mColor, s.mColor)) {
			return false;
		}
		if (this.mLineType != s.mLineType) {
			return false;
		}
		if (this.mLineWidth != s.mLineWidth) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("color=");
		sb.append(this.mColor);
		sb.append(", type=");
		sb.append(SGDrawingElementLine.getLineTypeName(this.mLineType));
		sb.append(", lineWidth=");
		sb.append(this.mLineWidth);
		sb.append("]");
		return sb.toString();
	}
}
