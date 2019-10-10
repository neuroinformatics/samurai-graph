package jp.riken.brain.ni.samuraigraph.figure.java2d;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;

public interface SGISXYAxisShiftEnabled {
    
    /**
     * Return minimum value of shift size
     *  with which objects shift along specified axis.
     * @param axis
     * @return minimum value
     */
    public Double getShiftMinValue(final SGAxis axis);
    
    /**
     * Return maximum value of shift size
     *  with which objects shift along specified axis.
     * @param axis
     * @return maximum value
     */
    public Double getShiftMaxValue(final SGAxis axis);
    
    /**
     * Return baseline value for given axis
     * if bar is visible.
     * <p>
     * If bar is vertical and given axis is X-axis, return null
     * because baseline value is not x axis value.
     * 
     * @param axis
     * @return bar baseline value. or null.
     */
    public Double getBarBaselineForAxis(final SGAxis axis);

}
