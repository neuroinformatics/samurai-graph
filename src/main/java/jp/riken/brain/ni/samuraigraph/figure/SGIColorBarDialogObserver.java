package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/**
 * An interface for the observer of color bar dialog.
 * 
 */
public interface SGIColorBarDialogObserver extends SGIAxisDialogObserver, SGITwoAxesHolder {

    //
    // Layout
    //
    
    public double getXValue();

    public double getYValue();

    public float getBarWidth(final String unit);

    public float getBarLength(final String unit);

    public float getSpaceToScale(final String unit);

    public float getSpaceToTitle(final String unit);

    public String getDirection();
    
//    public boolean isReversedOrder();

    public boolean setXValue(final double xValue);
    
    public boolean setYValue(final double yValue);

    public boolean setBarWidth(final float w, final String unit);
    
    public boolean setBarLength(final float len, final String unit);
    
    public boolean setSpaceToScale(final float space, final String unit);
    
    public boolean setSpaceToTitle(final float space, final String unit);
    
    public boolean setDirection(final String dir);

//    public boolean setReversedOrder(final boolean b);

    public boolean setReversedOrder(final String name, final boolean b);

    //
    // Style
    //
    
    public String getColorBarStyle();
    
    public boolean setColorBarStyle(final String style);
    
    public SGColorMap getColorMap(final String name);
    
//    /**
//     * Sets the name and the properties of the color map.
//     * 
//     * @param name
//     *           the name of color map
//     * @param p
//     *           properties of color map
//     * @return true if succeeded
//     */
//    public boolean setColorMap(String name, SGProperties p);
    
    public boolean setColors(String name, Color[] colors);

    /**
     * Returns whether the observer has valid x axis value.
     * 
     * @param location
     *           location of the x-axis
     * @param value
     *           axis value
     * @return true for valid axis value
     */
    public boolean hasValidXAxisValue(final int location, final Number value);

    /**
     * Returns whether the observer has valid y axis value.
     * 
     * @param location
     *           location of the y-axis
     * @param value
     *           axis value
     * @return true for valid axis value
     */
    public boolean hasValidYAxisValue(final int location, final Number value);
    
}
