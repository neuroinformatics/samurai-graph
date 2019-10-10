package jp.riken.brain.ni.samuraigraph.figure.java2d;

/**
 * An observer of the property panel for single axis.
 */
public interface SGIAxisPanelObserver {

    /**
     * @return
     * @uml.property name="axisVisible"
     */
    public boolean isAxisVisible();

    /**
     * @return
     * @uml.property name="titleVisible"
     */
    public boolean isTitleVisible();

    /**
     * 
     * @return
     */
    public String getTitleString();

    /**
     * @return
     * @uml.property name="minValue"
     */
    public double getMinValue();

    /**
     * @return
     * @uml.property name="maxValue"
     */
    public double getMaxValue();

    /**
     * @return
     * @uml.property name="InvertCoordinates"
     */
    public boolean isInvertCoordinates();

    /**
     * @return
     * @uml.property name="scaleType"
     */
    public int getScaleType();

    /**
     * @return
     * @uml.property name="calculateAutomatically"
     */
    public boolean isCalculateAutomatically();

    /**
     * @return
     * @uml.property name="stepValue"
     */
    public double getStepValue();

    /**
     * @return
     * @uml.property name="baselineValue"
     */
    public double getBaselineValue();

    /**
     * @return
     * @uml.property name="tickMarksVisible"
     */
    public boolean isTickMarksVisible();

    /**
     * @return
     * @uml.property name="tickMarksInside"
     */
    public boolean isTickMarksInside();

    /**
     * @return
     * @uml.property name="numbersVisible"
     */
    public boolean isNumbersVisible();

    /**
     * @return
     * @uml.property name="numbersInteger"
     */
    public boolean isNumbersInteger();

    /**
     * @return
     * @uml.property name="exponentFlag"
     */
    public boolean getExponentFlag();

    /**
     * @return
     * @uml.property name="exponentValue"
     */
    public int getExponentValue();

    /**
     * @param b
     * @return
     * @uml.property name="axisVisible"
     */
    public boolean setAxisVisible(final boolean b);

    /**
     * @param b
     * @return
     * @uml.property name="titleVisible"
     */
    public boolean setTitleVisible(final boolean b);

    /**
     * 
     * @param str
     * @return
     */
    public boolean setTitle(final String str);

    /**
     * @param b
     * @return
     * @uml.property name="InvertCoordinates"
     */
    public boolean setInvertCoordinates(final boolean b);

    /**
     * Sets the range and the scale type.
     * 
     * @param minValue
     *           the minimum value
     * @param maxValue
     *           the maximum value
     * @param scaleType
     *           the scale type
     * @return true if succeeded
     */
    public boolean setScale(final Number minValue, final Number maxValue, final Integer scaleType);
    
    /**
     * @param b
     * @return
     * @uml.property name="calculateAutomatically"
     */
    public boolean setCalculateAutomatically(final boolean b);

    /**
     * @param value
     * @return
     * @uml.property name="stepValue"
     */
    public boolean setStepValue(final double value);

    /**
     * @param value
     * @return
     * @uml.property name="baselineValue"
     */
    public boolean setBaselineValue(final double value);

    /**
     * @param b
     * @return
     * @uml.property name="tickMarksVisible"
     */
    public boolean setTickMarksVisible(final boolean b);

    /**
     * @param b
     * @return
     * @uml.property name="tickMarksInside"
     */
    public boolean setTickMarksInside(final boolean b);

    /**
     * @param b
     * @return
     * @uml.property name="numbersVisible"
     */
    public boolean setNumbersVisible(final boolean b);

    /**
     * @param b
     * @return
     * @uml.property name="numbersInteger"
     */
    public boolean setNumbersInteger(final boolean b);

    /**
     * @param b
     * @return
     * @uml.property name="exponentFlag"
     */
    public boolean setExponentFlag(final boolean b);

    /**
     * @param value
     * @return
     * @uml.property name="exponentValue"
     */
    public boolean setExponentValue(final int value);

    // /**
    // *
    // * @param minValue
    // * @param maxValue
    // * @param scaleType
    // * @param baseValue
    // * @param stepValue
    // * @return
    // */
    // public boolean hasValidAxisValues(
    // final Number minValue, final Number maxValue, final Integer scaleType,
    // final Number baseValue, final Number stepValue );

    /**
     * 
     * @param minValue
     * @param maxValue
     * @param scaleType
     * @return
     */
    public boolean hasValidAxisRange(final Number minValue,
            final Number maxValue, final Integer scaleType);

    /**
     * 
     * @param baseValue
     * @param stepValue
     * @param scaleType
     * @return
     */
    public boolean hasValidAxisValues(final Number baseValue,
            final Number stepValue, final Integer scaleType);

}
