package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGIAnchored;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/**
 * An observer of the property dialog for axis break symbols.
 */
public interface SGIAxisBreakDialogObserver extends SGIPropertyDialogObserver,
        SGITwoAxesHolder, SGIAnchored {

    /**
     * @return
     * @uml.property name="xValue"
     */
    public double getXValue();

    /**
     * @return
     * @uml.property name="yValue"
     */
    public double getYValue();

    /**
     * 
     * @return
     */
    public float getLength(final String unit);

    /**
     * 
     * @return
     */
    public float getInterval(final String unit);

    /**
     * @return
     * @uml.property name="distortion"
     */
    public float getDistortion();

    /**
     * @return
     * @uml.property name="angle"
     */
    public float getAngle();

    /**
     * 
     * @return
     */
    public float getLineWidth(final String unit);

    /**
     * 
     * @return
     */
    public boolean isForHorizontalAxis();

    /**
     * @return
     * @uml.property name="lineColor"
     */
    public Color getLineColor();

    /**
     * @return
     * @uml.property name="innerColor"
     */
    public Color getInnerColor();

    /**
     * @param value
     * @uml.property name="xValue"
     */
    public boolean setXValue(final double value);

    /**
     * @param value
     * @uml.property name="yValue"
     */
    public boolean setYValue(final double value);

    /**
     * 
     * @param value
     */
    public boolean setLength(final float value, final String unit);

    /**
     * 
     * @param value
     */
    public boolean setInterval(final float value, final String unit);

    /**
     * 
     * @param value
     */
    public boolean setLineWidth(final float value, final String unit);

    /**
     * @param angle
     * @uml.property name="angle"
     */
    public boolean setAngle(final float angle);

    /**
     * @param value
     * @uml.property name="distortion"
     */
    public boolean setDistortion(final float value);

    /**
     * @param color
     * @uml.property name="lineColor"
     */
    public boolean setLineColor(final Color color);

    /**
     * @param color
     * @uml.property name="innerColor"
     */
    public boolean setInnerColor(final Color color);

    /**
     * 
     * @param flag
     */
    public boolean setForHorizontalAxisFlag(final boolean flag);
    
    /**
     * 
     * @param config
     * @param value
     * @return
     */
    public boolean hasValidXValue(final int config, final Number value);

    /**
     * 
     * @param config
     * @param value
     * @return
     */
    public boolean hasValidYValue(final int config, final Number value);

}
