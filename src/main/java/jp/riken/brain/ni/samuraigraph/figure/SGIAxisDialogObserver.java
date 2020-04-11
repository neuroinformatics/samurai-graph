package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGAxisStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisValue;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;

/**
 * An observer of the properties dialog for axes.
 */
public interface SGIAxisDialogObserver extends SGIPropertyDialogObserver {

    public boolean isVisible();

    public boolean setVisible(final boolean b);


    //
    // Title
    //
    
    public boolean isTitleVisible();

    public String getTitleString();

    public String getTitleFontName();

    public float getTitleFontSize(final String unit);

    public int getTitleFontStyle();

    public Color getTitleFontColor();

    public boolean setTitleVisible(final boolean b);

    public boolean setTitleText(final String str);

    /**
     * Returns the space between axis line and numbers in given unit.
     * 
     * @param unit
     *           the length of unit
     * @return the space between axis line and numbers in given unit
     */
    public float getSpaceAxisLineAndNumbers(final String unit);
    
    /**
     * Sets the space between axis line and numbers in given unit.
     * 
     * @param space
     *           the space to set
     * @param unit
     *           the length of unit
     * @return true if succeeded
     */
    public boolean setSpaceAxisLineAndNumbers(final float space,
            final String unit);

    /**
     * Returns the space between numbers and title in given unit.
     * 
     * @param unit
     *           the length of unit
     * @return the space between numbers and title in given unit
     */
    public float getSpaceTitleAndNumbers(final String unit);
    
    /**
     * Sets the space between numbers and title in given unit.
     * 
     * @param space
     *           the space to set
     * @param unit
     *           the length of unit
     * @return true if succeeded
     */
    public boolean setSpaceTitleAndNumbers(final float space, 
    		final String unit);

    /**
     * Returns the space between numbers and title in given unit.
     * 
     * @param unit
     *           the length of unit
     * @return the space between numbers and title in given unit
     */
    public float getTitleShiftFromCenter(final String unit);
    
    /**
     * Sets the shift of title from the center line in given unit.
     * 
     * @param shift
     *           shift from the center line
     * @param unit
     *           the length of unit
     * @return true if succeeded
     */
    public boolean setTitleShiftFromCenter(final float shift, 
    		final String unit);

    public boolean setTitleFontName(final String name);
    
    public boolean setTitleFontStyle(final int style);

    public boolean setTitleFontSize(final float size, final String unit);

    public boolean setTitleFontColor(final Color cl);

    
    //
    // Axis Line
    //
    
    public boolean isAxisLineVisible();
    
    public float getAxisLineWidth(final String unit);

    public Color getAxisLineColor();
    
    public boolean setAxisLineVisible(final boolean b);

    public boolean setAxisLineWidth(final float width, final String unit);
    
    public boolean setAxisLineColor(Color cl);

    
    //
    // Scale
    //
    
    public SGAxisValue getMinValue();

    public SGAxisValue getMaxValue();

    public boolean isInvertCoordinates();

    public int getScaleType();

    public boolean isScaleAuto();

    public SGAxisValue getScaleBase();
    
    public SGAxisStepValue getScaleStep();
    
    //
    // Scale
    //

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
    public boolean setScale(final SGAxisValue minValue, final SGAxisValue maxValue, final Integer scaleType);
    
    public boolean setInvertedCoordinates(final boolean b);
    
    public boolean setCalculateAutomatically(final boolean b);

    public boolean setBaselineValue(final SGAxisValue value);

    public boolean setStepValue(final SGAxisStepValue value);

    /**
     * Returns the flag for date mode.
     * 
     * @return the flag for date mode
     */
    public boolean getDateMode();

    /**
     * Sets the flag for date mode.
     * 
     * @param b
     *          the flag to set
     * @param true if succeeded
     */
    public boolean setDateMode(final boolean b);

    //
    // Number
    //
    
    public boolean isNumbersVisible();

    public boolean isNumbersInteger();
    
    public float getNumberAngle();

    public boolean isExponentVisible();

    public int getExponentValue();
    
    public String getNumberFontName();
    
    public int getNumberFontStyle();
    
    public float getNumberFontSize(final String unit);
    
    public Color getNumberFontColor();

    public String getNumberDateFormat();

    public boolean setNumbersVisible(final boolean b);

    public boolean setNumbersInteger(final boolean b);

    public boolean setExponentVisible(final boolean b);

    public boolean setExponent(final int value);

    public boolean setNumbersAngle(final float angle);
    
    public boolean setNumberFontName(final String name);
    
    public boolean setNumberFontStyle(final int style);
    
    public boolean setNumberFontSize(final float size, final String unit);
    
    public boolean setNumberFontColor(final Color cl);

    public boolean setNumberDateFormat(final String format);

    //
    // Tick Mark
    //
    
    public boolean isTickMarkVisible();

    public boolean setTickMarkVisible(final boolean b);

    public float getTickMarkWidth(final String unit);

    public boolean setTickMarkWidth(final float width, final String unit);

    /**
     * Returns the length of major tick marks in a given unit of length.
     * 
     * @param unit
     *           a unit of length
     * @return the length of major tick mark
     */
    public float getMajorTickMarkLength(final String unit);

    /**
     * Sets the lengths of major tick marks in a given unit of length.
     * 
     * @param len
     *           the length of major tick marks
     * @param unit
     *           the unit of length
     * @return true if succeeded
     */
    public boolean setMajorTickMarkLength(final float len, final String unit);

    /**
     * Returns whether tick marks are drawn to both sides.
     * 
     * @return the direction of tick marks
     */
    public boolean isTickMarkBothsides();

    /**
     * Sets whether tick marks are drawn to both sides.
     * 
     * @param b
     *           true to draw to both sides
     * @return true if succeeded
     */
    public boolean setTickMarkBothsides(final boolean b);
    
    /**
     * Returns the number of minor tick mark.
     * 
     * @return the number of minor tick mark
     */
    public int getMinorTickMarkNumber();
    
    /**
     * Sets the number of minor tick marks.
     * 
     * @param num
     *           the number of minor tick marks
     * @return true if succeeded
     */
    public boolean setMinorTickMarkNumber(final int num);
    
    /**
     * Returns the length of minor tick mark in a given unit of length.
     * 
     * @param unit
     *           a unit of length
     * @return the length of minor tick mark
     */
    public float getMinorTickMarkLength(final String unit);

    /**
     * Sets the length of minor tick marks in a given unit of length.
     * 
     * @param len
     *           the length of minor tick marks
     * @param unit
     *           the unit of length
     * @return true if succeeded
     */
    public boolean setMinorTickMarkLength(final float len, final String unit);
    
    /**
     * Returns color of the tick marks.
     * 
     * @return color of the tick marks
     */
    public Color getTickMarkColor();

    /**
     * Sets the color of the tick marks.
     * 
     * @param cl
     *          the color
     * @return true if succeeded
     */
    public boolean setTickMarkColor(final Color cl);
    

    //
    // Shift
    //
    
    /**
     * Returns the shift in a given unit of length.
     * 
     * @param unit
     *            the unit of length
     * @return the shift in a given unit of length
     */
    public float getShift(final String unit);

    /**
     * Sets the shift of axis line.
     * 
     * @param shift
     *            the shift of axis line to set
     * @param unit
     *            the unit of length
     * @return true if succeeded
     */
    public boolean setShift(final float shift, final String unit);


    //
    // Exponent
    //
    
    /**
     * Returns the x-coordinate of the exponent symbol in given unit.
     * 
     * @param unit
     *           the unit of length
     * @return Returns the x-coordinate of the exponent symbol in given unit
     */
    public float getExponentLocationX(final String unit);

    /**
     * Returns the y-coordinate of the exponent symbol in given unit.
     * 
     * @param unit
     *           the unit of length
     * @return Returns the y-coordinate of the exponent symbol in given unit
     */
    public float getExponentLocationY(final String unit);

    /**
     * Sets the x-coordinate of the exponent symbol in given unit.
     * 
     * @param x
     *           the x-coordinate
     * @param unit
     *           the unit of length
     * @return true if succeeded
     */
    public boolean setExponentLocationX(final float x, final String unit);

    /**
     * Sets the y-coordinate of the exponent symbol in given unit.
     * 
     * @param y
     *           the y-coordinate
     * @param unit
     *           the unit of length
     * @return true if succeeded
     */
    public boolean setExponentLocationY(final float y, final String unit);
    
    /**
     * 
     * @param minValue
     * @param maxValue
     * @param scaleType
     * @return
     */
    public boolean hasValidAxisRange(final SGAxisValue minValue,
            final SGAxisValue maxValue, final Integer scaleType);

    /**
     * 
     * @param baseValue
     * @param stepValue
     * @param scaleType
     * @return
     */
    public boolean hasValidAxisValues(final SGAxisValue baseValue,
            final SGAxisStepValue stepValue, final Integer scaleType);

    /**
     * Returns the name of location.
     * 
     * @return the name of location
     */
    public String getLocationName();
    
}
