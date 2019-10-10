package jp.riken.brain.ni.samuraigraph.figure.java2d;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGITickLabelConstants;

import org.w3c.dom.Element;

/**
 * 
 */
public abstract class SGElementGroupTickLabel extends SGElementGroupString
        implements SGITickLabelConstants {

	/**
	 * A flag whether tick labels align parallel to the horizontal axis.
	 */
	protected boolean mAlignHorizontalFlag;

    /**
     * The default constructor.
     * 
     */
    public SGElementGroupTickLabel() {
        super();
    }

    /**
     * 
     */
    public boolean writeProperty(final Element el) {
        if (super.writeProperty(el) == false) {
            return false;
        }
        el.setAttribute(KEY_ANGLE, Float.toString(this.mAngle));
        el.setAttribute(KEY_TICK_LABEL_ALIGN_HORIAONTALLY, 
        		Boolean.toString(this.mAlignHorizontalFlag));
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
        Boolean b = null;
        // ArrayList list = null;

        if (super.readProperty(el) == false) {
            return false;
        }

        // angle
        str = el.getAttribute(KEY_ANGLE);
        if (str.length() != 0) {
            num = SGUtilityText.getFloat(str, SGIConstants.degree);
            if (num == null) {
                if (str.trim().equalsIgnoreCase(HORIZONTAL)) {
                    if (this.setAngle(ANGLE_HORIZONTAL) == false) {
                        return false;
                    }
                } else if (str.trim().equalsIgnoreCase(INCLINED)) {
                    if (this.setAngle(ANGLE_INCLINED) == false) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else if (this.setAngle(num.floatValue()) == false) {
                return false;
            }
        }

        // vertical
        str = el.getAttribute(KEY_TICK_LABEL_ALIGN_HORIAONTALLY);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return false;
            } else if (this.setHorizontalAlignment(b.booleanValue()) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * Update the location of tick labels.
     * @return true if succeeded
     */
    public abstract boolean updateLocation();

    /**
	 * Returns whether the tick labels align horizontally.
	 * 
	 * @return true if tick labels align horizontally, false if they do not so
	 */
    public boolean hasHorizontalAlignment() {
		return this.mAlignHorizontalFlag;
    }

    /**
     * Sets the alignment of tick labels.
     * 
     * @param b
     *          true to make the tick labels align horizontally
     * @return true if succeeded
     */
    public boolean setHorizontalAlignment(final boolean b) {
    	this.mAlignHorizontalFlag = b;
    	return true;
    }

    public SGProperties getProperties() {
    	TickLabelProperties p = new TickLabelProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    public boolean getProperties(SGProperties p) {
        if ((p instanceof TickLabelProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        TickLabelProperties tp = (TickLabelProperties) p;
        tp.mAlignmentHorizontal = this.mAlignHorizontalFlag;
        return true;
    }

    public boolean setProperties(SGProperties p) {
        if ((p instanceof TickLabelProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        TickLabelProperties tp = (TickLabelProperties) p;
        this.mAlignHorizontalFlag = tp.mAlignmentHorizontal;
        return true;
    }

    public static class TickLabelProperties extends StringProperties {
    	boolean mAlignmentHorizontal;
        public TickLabelProperties() {
            super();
        }
        public boolean equals(final Object obj) {
            if ((obj instanceof TickLabelProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            TickLabelProperties p = (TickLabelProperties) obj;
            if (this.mAlignmentHorizontal != p.mAlignmentHorizontal) {
                return false;
            }
            return true;
        }
    }
    
}
