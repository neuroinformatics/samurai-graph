package jp.riken.brain.ni.samuraigraph.figure.java2d;

import jp.riken.brain.ni.samuraigraph.base.SGIAnchored;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/**
 * An observer of the property dialog for arrows.
 */
public interface SGIArrowDialogObserver extends SGIPropertyDialogObserver,
        SGITwoAxesHolder, SGIArrowPanelObserver, SGIAnchored {

    /**
     * @return
     * @uml.property name="startXValue"
     */
    public double getStartXValue();

    /**
     * @return
     * @uml.property name="startYValue"
     */
    public double getStartYValue();

    /**
     * @return
     * @uml.property name="endXValue"
     */
    public double getEndXValue();

    /**
     * @return
     * @uml.property name="endYValue"
     */
    public double getEndYValue();

    /**
     * @param value
     * @uml.property name="startXValue"
     */
    public boolean setStartXValue(final double value);

    /**
     * @param value
     * @uml.property name="startYValue"
     */
    public boolean setStartYValue(final double value);

    /**
     * @param value
     * @uml.property name="endXValue"
     */
    public boolean setEndXValue(final double value);

    /**
     * @param value
     * @uml.property name="endYValue"
     */
    public boolean setEndYValue(final double value);

    /**
     * 
     * @param config
     * @param value
     * @return
     */
    public boolean hasValidStartXValue(final int config, final Number value);

    /**
     * 
     * @param config
     * @param value
     * @return
     */
    public boolean hasValidStartYValue(final int config, final Number value);

    /**
     * 
     * @param config
     * @param value
     * @return
     */
    public boolean hasValidEndXValue(final int config, final Number value);

    /**
     * 
     * @param config
     * @param value
     * @return
     */
    public boolean hasValidEndYValue(final int config, final Number value);

}
