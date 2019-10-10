package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGIAnchored;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/**
 * An observer of the property dialog for significant difference symbols.
 */
public interface SGISignificantDifferenceDialogObserver extends
        SGIPropertyDialogObserver, SGITwoAxesHolder, SGIAnchored {

    /**
     * @return
     * @uml.property name="text"
     */
    public String getText();

    /**
     * @return
     * @uml.property name="horizontalYValue"
     */
    public double getHorizontalYValue();

    /**
     * @return
     * @uml.property name="leftXValue"
     */
    public double getLeftXValue();

    /**
     * @return
     * @uml.property name="leftYValue"
     */
    public double getLeftYValue();

    /**
     * @return
     * @uml.property name="rightXValue"
     */
    public double getRightXValue();

    /**
     * @return
     * @uml.property name="rightYValue"
     */
    public double getRightYValue();

    /**
     * @return
     * @uml.property name="lineVisible"
     */
    public boolean isLineVisible();

    /**
     * 
     * @return
     */
    public float getSpace(final String unit);

    /**
     * 
     * @return
     */
    public float getLineWidth(final String unit);

    /**
     * @return
     * @uml.property name="fontName"
     */
    public String getFontName();

    /**
     * 
     * @return
     */
    public float getFontSize(final String unit);

    /**
     * @return
     * @uml.property name="fontStyle"
     */
    public int getFontStyle();

    /**
     * @return
     * @uml.property name="color"
     */
    public Color getColor();
    
    /**
     * @param str
     * @return
     * @uml.property name="text"
     */
    public boolean setText(final String str);

    /**
     * @param value
     * @uml.property name="horizontalYValue"
     */
    public boolean setHorizontalYValue(final double value);

    /**
     * @param value
     * @uml.property name="leftXValue"
     */
    public boolean setLeftXValue(final double value);

    /**
     * @param value
     * @uml.property name="leftYValue"
     */
    public boolean setLeftYValue(final double value);

    /**
     * @param value
     * @uml.property name="rightXValue"
     */
    public boolean setRightXValue(final double value);

    /**
     * @param value
     * @uml.property name="rightYValue"
     */
    public boolean setRightYValue(final double value);

    /**
     * @param b
     * @uml.property name="lineVisible"
     */
    public boolean setLineVisible(final boolean b);

    /**
     * 
     */
    public boolean setSpace(final float space, final String unit);

    /**
     * 
     */
    public boolean setLineWidth(final float lineWidth, final String unit);

    /**
     * @param name
     * @param style
     * @param size
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
     * @param cl
     * @uml.property name="color"
     */
    public boolean setColor(final Color cl);

    /**
     * 
     * @param config
     * @param value
     * @return
     */
    public boolean hasValidHorizontalYValue(final int config, final Number value);

    /**
     * 
     * @param config
     * @param value
     * @return
     */
    public boolean hasValidLeftXValue(final int config, final Number value);

    /**
     * 
     * @param config
     * @param value
     * @return
     */
    public boolean hasValidLeftYValue(final int config, final Number value);

    /**
     * 
     * @param config
     * @param value
     * @return
     */
    public boolean hasValidRightXValue(final int config, final Number value);

    /**
     * 
     * @param config
     * @param value
     * @return
     */
    public boolean hasValidRightYValue(final int config, final Number value);

}
