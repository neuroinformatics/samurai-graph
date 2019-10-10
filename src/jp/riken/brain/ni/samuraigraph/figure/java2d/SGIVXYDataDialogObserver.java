package jp.riken.brain.ni.samuraigraph.figure.java2d;

import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;

/**
 * An observer of the property dialog for two-dimensional vector-type data.
 */
public interface SGIVXYDataDialogObserver extends
        SGIDataPropertyDialogObserver, SGITwoAxesHolder, SGIArrowPanelObserver {

    /**
     * @return
     * @uml.property name="magnitudePerCM"
     */
    public float getMagnitudePerCM();

    /**
     * @param f
     * @return
     * @uml.property name="magnitudePerCM"
     */
    public boolean setMagnitudePerCM(final float f);

    /**
     * @return
     * @uml.property name="directionInvariant"
     */
    public boolean isDirectionInvariant();

    /**
     * @param b
     * @return
     * @uml.property name="directionInvariant"
     */
    public boolean setDirectionInvariant(final boolean b);

    /**
     * Sets the stride for the x-direction.
     * 
     * @param stride
     *           stride of arrays
     * @return true if succeeded
     */
    public boolean setStrideX(SGIntegerSeriesSet stride);

    /**
     * Sets the stride for the y-direction.
     * 
     * @param stride
     *           stride of arrays
     * @return true if succeeded
     */
    public boolean setStrideY(SGIntegerSeriesSet stride);

}
