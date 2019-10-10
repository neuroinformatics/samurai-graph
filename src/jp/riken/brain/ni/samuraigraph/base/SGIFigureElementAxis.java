package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.util.List;

/**
 * An object to manage axes.
 */
public interface SGIFigureElementAxis extends SGIFigureElement, SGIFigureElementAxisConstants {

    /**
     * Returns the list of all axes.
     * 
     * @return the list of all axes
     */
    public List<SGAxis> getAxisList();

    /**
     * Returns the list of horizontal axes.
     * 
     * @return the list of horizontal axes
     */
    public List<SGAxis> getHorizontalAxisList();

    /**
     * Returns the list of vertical axes.
     * 
     * @return the list of vertical axes
     */
    public List<SGAxis> getVerticalAxisList();

    /**
     * Returns the list of normal axes.
     * 
     * @return the list of normal axes
     */
    public List<SGAxis> getNormalAxisList();

    /**
     * Returns whether a given axis is horizontal.
     * 
     * @param axis
     *            an axis
     * @return true if a given axis is horizontal
     */
    public boolean isHorizontal(final SGAxis axis);

    /**
     * Returns whether a given axis is vertical.
     * 
     * @param axis
     *            an axis
     * @return true if a given axis is vertical
     */
    public boolean isVertical(final SGAxis axis);

    /**
     * Returns whether a given axis is normal.
     * 
     * @param axis
     *           an axis
     * @return true if a given axis is normal
     */
    public boolean isNormal(final SGAxis axis);

    /**
     * Returns string representation of the axis location.
     * 
     * @param locationInPlane -
     *            One of the following parameters: AXIS_HORIZONTAL_1,
     *            AXIS_HORIZONTAL_2, AXIS_VERTICAL_1 and
     *            AXIS_VERTICAL_2
     * @return String representation of the axis location
     */
    public String getLocationName(final int locationInPlane);

    /**
     * Returns string representation of the axis location.
     * 
     * @param axis
     *            an axis
     * @return String representation of the axis location
     */
    public String getLocationName(final SGAxis axis);

    /**
     * Returns code of the location in a plane of a given axis.
     * 
     * @param axis
     *            an axis
     * @return the axis location
     */
    public int getLocationInPlane(final SGAxis axis);

    /**
     * Returns an axis at a given location.
     * 
     * @param locationInPlane
     *            string representation of the location of an axis in a plane
     * @return an axis object
     */
    public SGAxis getAxisInPlane(final int locationInPlane);

    /**
     * Returns an axis at a given location.
     * 
     * @param locationInPlane
     *            the location of an axis in a plane
     * @return an axis object
     */
    public SGAxis getAxis(final String str);

    /**
     * Returns the z-axis.
     * 
     * @return the z-axis
     */
    public SGAxis getZAxis();
    
	public boolean isFrameLineVisible();

	public float getFrameLineWidth();

	public float getFrameLineWidth(String unit);

	public Color getFrameLineColor();

	public boolean setFrameVisible(boolean b);

	public boolean setFrameLineWidth(float lw);
	
	public boolean setFrameLineWidth(float lw, String unit);

	public boolean setFrameLineColor(Color cl);

    /**
     * Returns the space between axis line and numbers in the default unit at given location.
     * 
     * @param
     *     location of axis
     * @return the space between axis line and numbers in the default unit at given location
     */
    public float getSpaceAxisLineAndNumber(final int location);

    /**
     * Returns the space between numbers and title in the default unit at given location.
     * 
     * @param
     *     location of axis
     * @return the space between numbers and title in the default unit at given location
     */
    public float getSpaceNumberAndTitle(final int location);
    
    /**
     * Returns whether the color bar is available.
     * 
     * @return true if the color bar is available
     */
    public boolean isColorBarAvailable();
    
    /**
     * Returns the color bar model.
     * 
     * @return the color bar model
     */
    public SGColorMap getColorMap();

    /**
     * Returns whether the color bar is visible.
     * 
     * @return true if visible
     */
    public boolean isColorBarVisible();

    /**
     * Sets whether the color bar is visible.
     * 
     * @param true to set visible
     * 
     * @return true if succeeded
     */
    public boolean setColorBarVisible(final boolean b);

    /**
     * Fits the range of axes range to the focused data.
     * 
     * @param element
     *           a figure element for data
     * @return true if succeeded
     */
    public boolean fitAxisRangeToFocusedData(SGIFigureElementForData element,
    		final boolean forAnimationFrames);
    
    public boolean fitAxisRangeToFocusedData(SGIFigureElementForData element, 
    		int axisDirection, final boolean forAnimationFrames);

    /**
     * Fits the range of axes range to the given data.
     * 
     * @param element
     *           a figure element for data
     * @param dataList
     *           a list of data
     * @return true if succeeded
     */
    public boolean fitAxisRangeToData(SGIFigureElementForData element, 
    		List<SGData> dataList, final boolean forAnimationFrames);

    public boolean fitAxisRangeToData(SGIFigureElementForData element, 
    		List<SGData> dataList, int axisDirection,
    		final boolean forAnimationFrames);
    
    /**
     * 
     * @param config
     * @param x
     * @param y
     * @return
     */
    public double getValue(final int config, final int x, final int y);

    /**
     * Returns whether the axis at given location is visible.
     * 
     * @param location
     *           location of an axis
     * @return true if the axis at given location is visible
     */
    public boolean isAxisVisible(final int location);

    /**
     * Sets the visibility of the axis at given location is visible.
     * 
     * @param location
     *           location of an axis
     * @param b
     *           true to set visible
     */
    public void setAxisVisible(final int location, final boolean b);

    public void setChanged(final int location, final boolean b);

    public void hideSelectedAxes();

	public boolean getAxisDateMode(final int location);

    public SGPropertyResults setScaleProperties(SGPropertyMap map);

    public boolean isAxisScaleVisible();

    public void setAxisScaleVisible(final boolean b);

    public void setAxisScaleVisibleForCommit(final boolean b);
    
    public void setColorBarVisibleForCommit(final boolean b);

}
