package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;

/**
 * An observer of the figure dialog.
 */
public interface SGIFigureDialogObserver extends SGIPropertyDialogObserver {

    public float getFigureX(final String unit);

    public float getFigureY(final String unit);

    public float getFigureWidth(final String unit);

    public float getFigureHeight(final String unit);

    public boolean isFrameLineVisible();
    
    public float getFrameLineWidth(final String unit);
    
    public Color getFrameLineColor();

    /**
     * @return
     * @uml.property name="transparent"
     */
    public boolean isTransparent();

    /**
     * @return
     * @uml.property name="backgroundColor"
     */
    public Color getBackgroundColor();

    /**
     * @return
     * @uml.property name="legendVisible"
     */
    public boolean isLegendVisible();
    
    /**
     * Returns whether the color bar is visible.
     * 
     * @return true if visible
     */
    public boolean isColorBarVisible();

    public boolean isAxisScaleVisible();

    /**
     * Returns whether the legend is available.
     * If there are any visible data objects, this method returns true.
     * 
     * @return true if the legend is available
     */
    public boolean isLegendAvailable();
    
    /**
     * Returns whether the color bar is available.
     * If there are any visible data objects with the color bar, this method returns true.
     * 
     * @return true if the color bar is available
     */
    public boolean isColorBarAvailable();
    
    public boolean setFigureX(final float x, final String unit);

    public boolean setFigureY(final float y, final String unit);

    public boolean setFigureWidth(final float w, final String unit);

    public boolean setFigureHeight(final float h, final String unit);
    
    public boolean setFrameVisible(final boolean b);

    public boolean setFrameLineWidth(final float lw, final String unit);
    
    public boolean setFrameLineColor(final Color cl);

    public boolean setTransparent(final boolean b);

    public boolean setBackgroundColor(final Color cl);

    public boolean setLegendVisible(final boolean b);

    /**
     * Sets whether the color bar is visible.
     * 
     * @param true to set visible
     * 
     * @return true if succeeded
     */
    public boolean setColorBarVisible(final boolean b);

    public boolean setAxisScaleVisible(final boolean b);

    /**
     * Returns whether data objects in this figure are anchored.
     * 
     * @return true if data objects in this figure are anchored
     */
    public boolean isDataAnchored();

    /**
     * Sets the flag whether data objects in this figure are anchored
     * 
     * @param b
     *           true to set data objects in this figure anchored
     * @return true if succeeded
     */
    public boolean setDataAnchored(final boolean b);

}
