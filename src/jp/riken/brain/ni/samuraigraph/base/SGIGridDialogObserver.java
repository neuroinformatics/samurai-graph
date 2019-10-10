package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;

/**
 * An observer of the grid dialog.
 */
public interface SGIGridDialogObserver extends SGIPropertyDialogObserver,
        SGITwoAxesHolder {

    /**
     * @param b
     * @return
     * @uml.property name="gridVisible"
     */
    public boolean setGridVisible(final boolean b);

    public boolean setAutoRangeFlag(final boolean b);

    /**
     * @param value
     * @return
     * @uml.property name="stepValueX"
     */
    public boolean setStepValueX(final SGAxisStepValue value);

    /**
     * @param value
     * @return
     * @uml.property name="stepValueY"
     */
    public boolean setStepValueY(final SGAxisStepValue value);

    /**
     * @param value
     * @return
     * @uml.property name="baselineValueX"
     */
    public boolean setBaselineValueX(final SGAxisValue value);

    /**
     * @param value
     * @return
     * @uml.property name="baselineValueY"
     */
    public boolean setBaselineValueY(final SGAxisValue value);

    public boolean setLineWidth(final float width, final String unit);

    /**
     * @param type
     * @return
     * @uml.property name="lineType"
     */
    public boolean setLineType(final int type);

    /**
     * @param cl
     * @return
     * @uml.property name="color"
     */
    public boolean setColor(final Color cl);

    /**
     * @return
     * @uml.property name="gridVisible"
     */
    public boolean isGridVisible();

    public boolean isAutoRange();

    /**
     * @return
     * @uml.property name="stepValueX"
     */
    public SGAxisStepValue getStepValueX();

    /**
     * @return
     * @uml.property name="stepValueY"
     */
    public SGAxisStepValue getStepValueY();

    /**
     * @return
     * @uml.property name="baselineValueX"
     */
    public SGAxisValue getBaselineValueX();

    /**
     * @return
     * @uml.property name="baselineValueY"
     */
    public SGAxisValue getBaselineValueY();

    public float getLineWidth(final String unit);

    /**
     * @return
     * @uml.property name="lineType"
     */
    public int getLineType();

    /**
     * @return
     * @uml.property name="color"
     */
    public Color getColor();

    public boolean hasValidStepXValue(final SGAxisStepValue step);

    public boolean hasValidStepYValue(final SGAxisStepValue step);

}
