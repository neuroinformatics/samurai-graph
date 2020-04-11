package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/**
 * An observer of the property dialog for legend.
 */
interface SGILegendDialogObserver extends SGIPropertyDialogObserver,
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
     * 
     * @return
     */
    public Color getFontColor();

    /**
     * 
     * @return
     */
    public boolean isFrameVisible();

    /**
     * 
     * @return
     */
    public float getFrameLineWidth(final String unit);

    /**
     * @return
     * @uml.property name="frameLineColor"
     */
    public Color getFrameColor();

    /**
     * @return
     * @uml.property name="legendVisible"
     */
    public boolean isVisible();

    /**
     * @return
     * @uml.property name="backgroundTransparent"
     */
    public int getBackgroundTransparency();

    /**
     * @return
     * @uml.property name="backgroundColor"
     */
    public Color getBackgroundColor();

    /**
     * @return
     */
    public float getSymbolSpan(final String unit);

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
     * @param b
     * @uml.property name="legendVisible"
     */
    public boolean setVisible(final boolean b);

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

    /**
     * 
     * @param size
     * @return
     */
    public boolean setFontSize(final float size, final String unit);

    /**
     * 
     * @param cl
     */
    public boolean setFontColor(final Color cl);

    /**
     * 
     * @param b
     */
    public boolean setFrameVisible(final boolean b);

    /**
     * 
     * @param width
     */
    public boolean setFrameLineWidth(final float width, final String unit);

    /**
     * @param cl
     * @uml.property name="frameLineColor"
     */
    public boolean setFrameLineColor(final Color cl);

    /**
     * @param b
     * @uml.property name="backgroundTransparent"
     */
    public boolean setBackgroundTransparent(final int percentAlpha);

    /**
     * @param cl
     * @uml.property name="backgroundColor"
     */
    public boolean setBackgroundColor(Color cl);

    /**
     * @param f
     */
    public boolean setSymbolSpan(final float w, final String unit);

    /**
     * 
     * @param config
     * @param value
     * @return
     */
    public boolean hasValidXAxisValue(final int config, final Number value);

    /**
     * 
     * @param config
     * @param value
     * @return
     */
    public boolean hasValidYAxisValue(final int config, final Number value);

}
