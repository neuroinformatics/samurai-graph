package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/**
 * An observer of the property dialog for labels.
 */
public interface SGILabelDialogObserver extends SGIPropertyDialogObserver,
        SGITwoAxesHolder {

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
     * @return
     * @uml.property name="string"
     */
    public String getString();

    /**
     * @return
     * @uml.property name="stringColor"
     */
    public Color getStringColor();

    /**
     * @return
     * @uml.property name="fontName"
     */
    public String getFontName();

    /**
     * 
     */
    public float getFontSize(final String unit);

    /**
     * @uml.property name="fontStyle"
     */
    public int getFontStyle();

    /**
     * @return
     * @uml.property name="angle"
     */
    public float getAngle();

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
     * @param str
     * @return
     * @uml.property name="string"
     */
    public boolean setString(final String str);

    /**
     * @param color
     * @uml.property name="stringColor"
     */
    public boolean setStringColor(final Color color);

    /**
     * @param name
     * @return
     * @uml.property name="fontName"
     */
    public boolean setFontName(final String name);

    /**
     * @param style
     * @return
     * @uml.property name="fontStyle"
     */
    public boolean setFontStyle(final int style);

    public boolean setFontSize(final float size, final String unit);

    /**
     * @param angle
     * @return
     * @uml.property name="angle"
     */
    public boolean setAngle(final float angle);

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
