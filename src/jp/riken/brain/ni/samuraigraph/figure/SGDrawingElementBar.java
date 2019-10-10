package jp.riken.brain.ni.samuraigraph.figure;

import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;

/**
 * Drawing element of bar. This element is the extension of the rectangle: it
 * has axis values for the baseline and width.
 */
public abstract class SGDrawingElementBar extends SGDrawingElementRectangle
        implements SGIBarConstants, SGIDrawingElementConstants {

    /**
     * The default constructor.
     */
    public SGDrawingElementBar() {
        super();
    }

    /**
     * Returns the baseline value.
     * 
     * @return the baseline value
     */
    public abstract double getBaselineValue();

    /**
     * Sets the baseline value.
     * 
     * @param value
     *           axis value to set to the baseline value
     * @return true if succeeded
     */
    public abstract boolean setBaselineValue(final double value);

    /**
     * Returns the width value.
     * 
     * @return the width value
     */
    public abstract double getWidthValue();
    
    /**
     * Sets the width value.
     * 
     * @param value
     *          axis value to set to the width value
     * @return true if succeeded
     */
    public abstract boolean setWidthValue(final double value);
    
    /**
     * Returns whether this bar is vertlcal.
     * 
     * @return true if the bar is vertlcal
     */
    public abstract boolean isVertical();

    /**
     * Sets whether this bar is vertlcal.
     * 
     * @param b
     *          true to set vertical
     * @return true if succeeded
     */
    public abstract boolean setVertical(boolean b);
    
    public abstract double getInterval();
    
    public abstract boolean setInterval(final double value);

    /**
     * 
     */
    public SGProperties getProperties() {
        BarProperties p = new BarProperties();
        if (this.getProperties(p) == false)
            return null;
        return p;
    }

    /**
     * 
     */
    public boolean getProperties(SGProperties p) {
        if (p == null)
            return false;
        if ((p instanceof BarProperties) == false)
            return false;
        if (super.getProperties(p) == false)
            return false;

        BarProperties bp = (BarProperties) p;
        bp.setBaselineValue(this.getBaselineValue());
        bp.setWidthValue(this.getWidthValue());
        bp.setInterval(this.getInterval());

        return true;
    }

    /**
     * 
     */
    public static class BarProperties extends RectangleProperties {
        protected double mBaselineValue;

        protected double mWidthValue;
        
        protected boolean mVerticalFlag;
        
        protected double mInterval;

        /**
         * 
         */
        public BarProperties() {
            super();
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof BarProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            BarProperties p = (BarProperties) obj;
            if (this.mBaselineValue != p.mBaselineValue) {
                return false;
            }
            if (this.mWidthValue != p.mWidthValue) {
                return false;
            }
            if (this.mVerticalFlag != p.mVerticalFlag) {
            	return false;
            }
            if (this.mInterval != p.mInterval) {
                return false;
            }
            return true;
        }

        public Double getBaselineValue() {
            return Double.valueOf(this.mBaselineValue);
        }

        public boolean setBaselineValue(final double value) {
            this.mBaselineValue = value;
            return true;
        }

        public Double getWidthValue() {
            return Double.valueOf(this.mWidthValue);
        }

        public boolean setWidthValue(final double value) {
            this.mWidthValue = value;
            return true;
        }

        public Boolean isVertical() {
            return Boolean.valueOf(this.mVerticalFlag);
        }
        
        public boolean setVertical(final boolean b) {
            this.mVerticalFlag = b;
            return true;
        }
        
        public Double getInterval() {
            return Double.valueOf(this.mInterval);
        }
        
        public boolean setInterval(final double value) {
            this.mInterval = value;
            return true;
        }
    }

}
