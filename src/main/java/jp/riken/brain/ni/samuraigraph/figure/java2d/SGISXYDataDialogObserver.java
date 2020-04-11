package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.data.SGPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.figure.SGILineStyleDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;

/**
 * An observer of the property dialog for two-dimensional scalar type data.
 */
public interface SGISXYDataDialogObserver extends
        SGIDataPropertyDialogObserver, SGITwoAxesHolder, SGILineStyleDialogObserver {

    /**
     * Returns the type of clicked objects.
     *
     * @return
     */
    public SGISXYDataConstants.ELEMENT_TYPE getSelectedGroupType();

    // Line
    /**
     * @return
     * @uml.property name="lineVisible"
     */
    public boolean isLineVisible();


    /**
     * Returns whether the lines connect all effective points.
     *
     * @return true if connecting all effective points
     */
    public boolean isLineConnectingAll();

    /**
     * @return
     */
    public double getShiftX();

    /**
     * @return
     */
    public double getShiftY();

    /**
     * @param b
     * @return
     * @uml.property name="lineVisible"
     */
    public boolean setLineVisible(final boolean b);

    /**
     * Sets whether the lines connect all effective points.
     *
     * @return true if succeeded
     */
    public boolean setLineConnectingAll(final boolean b);

    /**
     * Sets the shift size of line to x direction.
     * @param shift
     * @return true if succeeded
     */
    public boolean setShiftX(final double shift);

    /**
     * Sets the shift size of line to y direction.
     * @param shift
     * @return true if succeeded
     */
    public boolean setShiftY(final double shift);

    // Symbol
    /**
     * @return
     * @uml.property name="symbolVisible"
     */
    public boolean isSymbolVisible();

    /**
     * @return
     * @uml.property name="symbolType"
     */
    public int getSymbolType();

    public float getSymbolSize(final String unit);

    public float getSymbolLineWidth(final String unit);

    /**
     * @return
     * @uml.property name="symbolInnerPaint"
     */
    public SGIPaint getSymbolInnerPaint();

    /**
     * @return
     * @uml.property name="symbolLineColor"
     */
    public Color getSymbolLineColor();

    /**
     * @return
     * @uml.property name="symbolLineVisible"
     */
    public boolean isSymbolLineVisible();

    /**
     * @param b
     * @return
     * @uml.property name="symbolVisible"
     */
    public boolean setSymbolVisible(final boolean b);

    /**
     * @param type
     * @return
     * @uml.property name="symbolType"
     */
    public boolean setSymbolType(final int type);

    public boolean setSymbolSize(final float size, final String unit);

    public boolean setSymbolLineWidth(final float width, final String unit);

    /**
     * @param paint
     * @return
     * @uml.property name="symbolInnerPaint"
     */
    public boolean setSymbolInnerPaint(final SGIPaint paint);

    /**
     * @param cl
     * @return
     * @uml.property name="symbolLineColor"
     */
    public boolean setSymbolLineColor(final Color cl);

    /**
     * @param b
     * @return
     * @uml.property name="symbolLineVisible"
     */
    public boolean setSymbolLineVisible(final boolean b);

    // Bar
    /**
     * @return
     * @uml.property name="barVisible"
     */
    public boolean isBarVisible();

    /**
     * @return
     * @uml.property name="barBaselineValue"
     */
    public double getBarBaselineValue();

    /**
     * @return
     * @uml.property name="barWidthValue"
     */
    public double getBarWidthValue();

    public float getBarEdgeLineWidth(final String unit);

    /**
     * @return
     * @uml.property name="barInnerPaint"
     */
    public SGIPaint getBarInnerPaint();

    /**
     * @return
     * @uml.property name="barEdgeLineColor"
     */
    public Color getBarEdgeLineColor();

    /**
     * @return
     * @uml.property name="barEdgeLineVisible"
     */
    public boolean isBarEdgeLineVisible();

    /**
     * @return
     * @uml.property name="barVertical"
     */
    public boolean isBarVertical();

    /**
     * @return
     * @uml.property name="barShiftX"
     */
    public double getBarOffsetX();

    /**
     * @return
     * @uml.property name="barShiftY"
     */
    public double getBarOffsetY();

    /**
     * @return
     * @uml.property name="barInterval"
     */
    public double getBarInterval();

    /**
     * @param b
     * @return
     * @uml.property name="barVisible"
     */
    public boolean setBarVisible(final boolean b);

    /**
     * @param value
     * @return
     * @uml.property name="barBaselineValue"
     */
    public boolean setBarBaselineValue(final double value);

    /**
     * @param width
     * @return
     * @uml.property name="barWidthValue"
     */
    public boolean setBarWidthValue(final double width);

    public boolean setBarEdgeLineWidth(final float width, final String unit);

    /**
     * @param paint
     * @return
     * @uml.property name="barInnerPaint"
     */
    public boolean setBarInnerPaint(final SGIPaint paint);

    /**
     * @param cl
     * @return
     * @uml.property name="barEdgeLineColor"
     */
    public boolean setBarEdgeLineColor(final Color cl);

    /**
     * @param b
     * @return
     * @uml.property name="barEdgeLineVisible"
     */
    public boolean setBarEdgeLineVisible(final boolean b);

    public boolean setBarVertical(final boolean b);

    public boolean hasValidBaselineValue(final int config, final Number value);

    /**
     * @return
     * @uml.property name="barShiftX"
     */
    public boolean setBarOffsetX(final double shift);

    /**
     * @return
     * @uml.property name="barShiftY"
     */
    public boolean setBarOffsetY(final double shift);

    /**
     * @return
     * @uml.property name="barInterval"
     */
    public boolean setBarInterval(final double interval);

    // Error Bar
    public boolean isErrorBarAvailable();

    /**
     * @return
     * @uml.property name="errorBarVisible"
     */
    public boolean isErrorBarVisible();

    /**
     * @return
     * @uml.property name="errorBarHeadType"
     */
    public int getErrorBarHeadType();

    public float getErrorBarHeadSize(final String unit);

    /**
     * @return
     * @uml.property name="errorBarColor"
     */
    public Color getErrorBarColor();

    public float getErrorBarLineWidth(final String unit);

    /**
     * @return
     * @uml.property name="errorBarStyle"
     */
    public int getErrorBarStyle();

    public boolean isErrorBarVertical();

    public boolean isErrorBarOnLinePosition();

    /**
     * @param b
     * @return
     * @uml.property name="errorBarVisible"
     */
    public boolean setErrorBarVisible(final boolean b);

    /**
     * @param type
     * @return
     * @uml.property name="errorBarHeadType"
     */
    public boolean setErrorBarHeadType(final int type);

    public boolean setErrorBarHeadSize(final float size, final String unit);

    /**
     * @param cl
     * @return
     * @uml.property name="errorBarColor"
     */
    public boolean setErrorBarColor(final Color cl);

    public boolean setErrorBarLineWidth(final float width, final String unit);

    /**
     * @param style
     * @return
     * @uml.property name="errorBarStyle"
     */
    public boolean setErrorBarStyle(final int style);

    public boolean setErrorBarVertical(final boolean b);

    public boolean setErrorBarOnLinePosition(final boolean b);

    // Tick Label
    public boolean isTickLabelAvailable();

    /**
     * @return
     * @uml.property name="tickLabelVisible"
     */
    public boolean isTickLabelVisible();

    /**
     * @return
     * @uml.property name="tickLabelFontName"
     */
    public String getTickLabelFontName();

    /**
     * @return
     * @uml.property name="tickLabelFontStyle"
     */
    public int getTickLabelFontStyle();

    public float getTickLabelFontSize(final String unit);

    /**
     * @return
     * @uml.property name="tickLabelColor"
     */
    public Color getTickLabelColor();

    /**
     * @return true if tick labels align horizontally. Return false if vertically.
     */
    public boolean hasTickLabelHorizontalAlignment();

    /**
     * @return
     * @uml.property name="tickLabelAngle"
     */
    public float getTickLabelAngle();

    /**
     * @param b
     * @return
     * @uml.property name="tickLabelVisible"
     */
    public boolean setTickLabelVisible(final boolean b);

    /**
     * @param name
     * @return
     * @uml.property name="tickLabelFontName"
     */
    public boolean setTickLabelFontName(final String name);

    /**
     * @param style
     * @return
     * @uml.property name="tickLabelFontStyle"
     */
    public boolean setTickLabelFontStyle(final int style);

    public boolean setTickLabelFontSize(final float size, final String unit);
    
    public boolean setTickLabelDateFormat(final String format);

    /**
     * @param cl
     * @return
     * @uml.property name="tickLabelColor"
     */
    public boolean setTickLabelColor(final Color cl);

    /**
     * @param b
     * @return
     */
    public boolean setTickLabelHorizontalAlignment(final boolean b);

    /**
     * @param angle
     * @return
     * @uml.property name="tickLabelAngle"
     */
    public boolean setTickLabelAngle(final float angle);

    /**
     * Returns the decimal places of tick labels.
     *
     * @return the decimal places of tick labels
     */
    public int getTickLabelDecimalPlaces();

    /**
     * Returns the exponent of tick labels.
     *
     * @return the exponent of tick labels
     */
    public int getTickLabelExponent();
    
    public String getTickLabelDateFormat();

    /**
     * Sets the value of the decimal places of tick labels.
     *
     * @param dp
     *          a value to set to the decimal places of tick labels
     * @return true if succeeded
     */
    public boolean setTickLabelDecimalPlaces(final int dp);

    /**
     * Sets the value of the exponent of tick labels.
     *
     * @param exp
     *          a value to set to the exponent of tick labels
     * @return true if succeeded
     */
    public boolean setTickLabelExponent(final int exp);

    /**
     * Sets the information of picked up dimension.
     *
     * @param info
     *           the information of picked up dimension
     * @return true if succeeded
     */
    public boolean setPickUpDimensionInfo(SGPickUpDimensionInfo info);

    /**
     * Sets the stride.
     *
     * @param stride
     *           stride of arrays
     * @return true if succeeded
     */
    public boolean setStride(SGIntegerSeriesSet stride);

    /**
     * Sets the stride of the tick labels.
     *
     * @param stride
     *           stride of arrays
     * @return true if succeeded
     */
    public boolean setTickLabelStride(SGIntegerSeriesSet stride);
    
    public boolean hasDateTickLabels();

}
