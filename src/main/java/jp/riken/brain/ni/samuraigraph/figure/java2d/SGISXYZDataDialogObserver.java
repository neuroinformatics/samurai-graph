package jp.riken.brain.ni.samuraigraph.figure.java2d;

import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;

public interface SGISXYZDataDialogObserver extends
        SGIDataPropertyDialogObserver, SGITwoAxesHolder {

    public double getRectangleWidthValue();
    
    public boolean setRectangleWidthValue(final double value);
    
    public double getRectangleHeightValue();
    
    public boolean setRectangleHeightValue(final double value);
    
    public boolean hasValidRectangleWidthValue(final int config,
            final Number value);
    
    public boolean hasValidRectangleHeightValue(final int config,
            final Number value);

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
