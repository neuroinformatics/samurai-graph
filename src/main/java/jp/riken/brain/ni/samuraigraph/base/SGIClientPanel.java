package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;

/**
 * @author okumura
 */
public interface SGIClientPanel {

    public float getPaperWidth(final String unit);

    public float getPaperHeight(final String unit);

    public float getGridLineInterval(final String unit);

    public float getGridLineWidth(final String unit);

    /**
     * @return
     * @uml.property name="gridLineVisible"
     */
    public boolean isGridLineVisible();

    /**
     * @return
     * @uml.property name="paperColor"
     */
    public Color getPaperColor();

    /**
     * @return
     * @uml.property name="gridLineColor"
     */
    public Color getGridLineColor();

    public float getImageLocationX(final String unit);

    public float getImageLocationY(final String unit);

    public float getImageWidth(final String unit);

    public float getImageHeight(final String unit);

    /**
     * @return
     * @uml.property name="imageScalingFactor"
     */
    public float getImageScalingFactor();

    public boolean setPaperWidth(final float value, final String unit);

    public boolean setPaperHeight(final float value, final String unit);

    public boolean setGridLineInterval(final float value, final String unit);

    public boolean setGridLineWidth(final float value, final String unit);

    /**
     * @param b
     * @return
     * @uml.property name="gridLineVisible"
     */
    public boolean setGridLineVisible(final boolean b);

    /**
     * @param cl
     * @return
     * @uml.property name="paperColor"
     */
    public boolean setPaperColor(final Color cl);

    /**
     * @param cl
     * @return
     * @uml.property name="gridLineColor"
     */
    public boolean setGridLineColor(final Color cl);

    public boolean setImageLocationX(final float value, final String unit);

    public boolean setImageLocationY(final float value, final String unit);

//    public boolean setImageWidth(final float value, final String unit);
//
//    public boolean setImageHeight(final float value, final String unit);

    /**
     * @param value
     * @return
     * @uml.property name="imageScalingFactor"
     */
    public boolean setImageScalingFactor(final float value);

}
