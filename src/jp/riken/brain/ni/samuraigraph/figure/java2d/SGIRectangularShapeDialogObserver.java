package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGIAnchored;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/**
 * An observer of the property dialog for rectangular shapes.
 */
public interface SGIRectangularShapeDialogObserver extends
        SGIPropertyDialogObserver, SGITwoAxesHolder, SGIAnchored {

    /**
     * @return
     * @uml.property name="leftXValue"
     */
    public double getLeftXValue();

    /**
     * @return
     * @uml.property name="rightXValue"
     */
    public double getRightXValue();

    /**
     * @return
     * @uml.property name="topYValue"
     */
    public double getTopYValue();

    /**
     * @return
     * @uml.property name="bottomYValue"
     */
    public double getBottomYValue();

    /**
     * 
     * @return
     */
    public float getLineWidth(final String unit);

    /**
     * @return
     * @uml.property name="lineType"
     */
    public int getLineType();

    /**
     * @return
     * @uml.property name="lineColor"
     */
    public Color getLineColor();

    /**
     * @return
     * @uml.property name="lineVisible"
     */
    public boolean isLineVisible();

    /**
     * @return
     * @uml.property name="innerPaint"
     */
    public SGIPaint getInnerPaint();

    /**
     * @return
     * @uml.property name="transparent"
     */
    public float getTransparency();
    
    /**
     * @param value
     * @uml.property name="leftXValue"
     */
    public boolean setLeftXValue(final double value);

    /**
     * @param value
     * @uml.property name="rightXValue"
     */
    public boolean setRightXValue(final double value);

    /**
     * @param value
     * @uml.property name="topYValue"
     */
    public boolean setTopYValue(final double value);

    /**
     * @param value
     * @uml.property name="bottomYValue"
     */
    public boolean setBottomYValue(final double value);

    /**
     * 
     */
    public boolean setLineWidth(final float lw, final String unit);

    /**
     * @param type
     * @return
     * @uml.property name="lineType"
     */
    public boolean setLineType(final int type);

    /**
     * @param cl
     * @uml.property name="lineColor"
     */
    public boolean setLineColor(final Color cl);
    
    /**
     * @param visible
     * @return
     * @uml.property name="lineVisible"
     */
    public boolean setLineVisible(final boolean visible);

    /**
     * @param paint
     * @uml.property name="innerPaint"
     */
    public boolean setInnerPaint(final SGIPaint paint);

    /**
     * @param alpha
     * @return
     * @uml.property name="transparent"
     */
    public boolean setTransparent(final float alpha);

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
    public boolean hasValidTopYValue(final int config, final Number value);

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
    public boolean hasValidBottomYValue(final int config, final Number value);

}
