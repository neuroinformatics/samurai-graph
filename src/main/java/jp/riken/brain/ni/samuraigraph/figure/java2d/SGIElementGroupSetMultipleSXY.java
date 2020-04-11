package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGStyle;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeMultipleData;
import jp.riken.brain.ni.samuraigraph.figure.SGILineStylePropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;

import org.w3c.dom.Element;

public interface SGIElementGroupSetMultipleSXY extends
        SGIElementGroupSetForData, SGIElementGroupSetXY, SGILineStylePropertyDialogObserver {

    /**
     * Adds a group set.
     *
     * @param gs
     *           the group set to be add
     */
    public void addChildGroupSet(SGIElementGroupSetForData gs);

    /**
     * Removes a group set.
     *
     * @param gs
     *           the group set to be removed
     */
    public void removeChildGroupSet(SGIElementGroupSetForData gs);

    public SGIElementGroupSetForData[] getChildGroupSetArray();
    
    public int getChildNumber();

    public SGIElementGroupSetForData getFirst();

    /**
     * Creates drawing elements of error bars.
     *
     * @param dataXY
     *             XY type data
     * @return the group of error bars
     */
    public SGElementGroupErrorBar createErrorBars(SGISXYTypeMultipleData dataXY);

    /**
     * Creates drawing elements of tick labels.
     *
     * @param dataXY
     *             XY type data
     * @return the group of tick labels
     */
    public SGElementGroupTickLabel createTickLabels(
            SGISXYTypeMultipleData dataXY);

    /**
     * Update the text strings of tick labels.
     */
    public void updateTickLabelStrings();

    /**
     * Sets the direction of error bars.
     *
     * @param vertical
     *           true to set vertical
     * @return true if succeeded
     */
    public boolean setErrorBarDirection(final boolean vertical);

    /**
     * Sets the alignment of tick label.
     *
     * @param horizontal
     *           true to align horizontally
     * @return true if succeeded
     */
    public boolean setTickLabelAlignment(final boolean horizontal);

    public boolean setShiftX(final double shift);

    public boolean setShiftY(final double shift);

    /**
     * Initializes the line style of child objects.
     *
     * @return true if succeeded
     */
    public boolean initChildLineStyle();

    public boolean setErrorBarOnLinePosition(final boolean b);

    public double getBarWidthValue();
    
    public boolean setBarWidthValue(final double value);

    public boolean initLineStyle(final Element el);
    
    public boolean readColorMap(final Element el);
    
    /**
     * Sets the line style to the child data object.
     *
     * @param style
     *          the line style to set
     * @param index
     *          array index of child data object
     * @return true if succeeded
     */
    public boolean setLineStyle(final SGLineStyle style, final int index);

    /**
     * Returns the line style at given index.
     * 
     * @param index
     *           index of child object
     * @return the line style
     */
    public SGLineStyle getLineStyle(final int index);
    
    /**
     * Sets the properties of a child data object.
     * 
     * @param map
     *           a map of properties
     * @param childId
     *           ID of a child data object
     * @return the result of setting properties
     */
    public SGPropertyResults setProperties(SGPropertyMap map, final int childId);

    /**
     * Sets the properties of color map.
     * 
     * @param colorMapName
     *           the name of color map
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    public SGPropertyResults setColorMapProperties(final String colorMapName, SGPropertyMap map);

    /**
     * Sets the style of drawing elements.
     *
     * @param styleList
     *          list of the style
     * @return true if succeeded
     */
    public boolean setStyle(List<SGStyle> styleList);

    /**
     * Updates the child objects.
     * 
     * @return true if succeeded
     */
    public boolean updateChild();

    /**
     * Returns true if data is shifted.
     * 
     * @return true if data is shifted
     */
    public boolean isDataShifted();
    
}
