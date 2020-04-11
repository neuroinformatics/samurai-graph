package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGAxisStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisValue;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/**
 * An observer of the dialog for XY-type figures.
 */
public interface SGIXYFigureDialogObserver extends SGIFigureDialogObserver,
        SGITwoAxesHolder {

    /**
     * @param b
     * @return
     * @uml.property name="gridVisible"
     */
    public boolean setGridVisible(final boolean b);

    /**
     * @param b
     * @return
     * @uml.property name="autoCalculateRange"
     */
    public boolean setAutoCalculateRange(final boolean b);

    /**
     * @param value
     * @return
     * @uml.property name="gridStepValueX"
     */
    public boolean setGridStepValueX(final SGAxisStepValue value);

    /**
     * @param value
     * @return
     * @uml.property name="gridStepValueY"
     */
    public boolean setGridStepValueY(final SGAxisStepValue value);

    /**
     * @param value
     * @return
     * @uml.property name="gridBaselineValueX"
     */
    public boolean setGridBaselineValueX(final SGAxisValue value);

    /**
     * @param value
     * @return
     * @uml.property name="gridBaselineValueY"
     */
    public boolean setGridBaselineValueY(final SGAxisValue value);

    public boolean setGridLineWidth(final float width, final String unit);

    /**
     * @param type
     * @return
     * @uml.property name="gridLineType"
     */
    public boolean setGridLineType(final int type);

    /**
     * @param cl
     * @return
     * @uml.property name="gridLineColor"
     */
    public boolean setGridLineColor(final Color cl);

    /**
     * @return
     * @uml.property name="gridVisible"
     */
    public boolean isGridVisible();

    /**
     * @return
     * @uml.property name="autoCalculateRange"
     */
    public boolean isAutoCalculateRange();

    /**
     * @return
     * @uml.property name="gridStepValueX"
     */
    public SGAxisStepValue getGridStepValueX();

    /**
     * @return
     * @uml.property name="gridStepValueY"
     */
    public SGAxisStepValue getGridStepValueY();

    /**
     * @return
     * @uml.property name="gridBaselineValueX"
     */
    public SGAxisValue getGridBaselineValueX();

    /**
     * @return
     * @uml.property name="gridBaselineValueY"
     */
    public SGAxisValue getGridBaselineValueY();

    public float getGridLineWidth(final String unit);

    /**
     * @return
     * @uml.property name="gridLineType"
     */
    public int getGridLineType();

    /**
     * @return
     * @uml.property name="gridLineColor"
     */
    public Color getGridLineColor();

    public boolean hasValidStepXValue(final SGAxisStepValue step);

    public boolean hasValidStepYValue(final SGAxisStepValue step);

    /*
    public boolean getGridDateMode(final boolean horizontal);
    */
}
