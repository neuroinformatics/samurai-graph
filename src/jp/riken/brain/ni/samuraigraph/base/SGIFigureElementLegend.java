package jp.riken.brain.ni.samuraigraph.base;

import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * An interface for the legend in figures.
 */
public interface SGIFigureElementLegend extends SGIFigureElement, SGIFigureElementForData {

    /**
     * 
     */
    public boolean setGraphElement(SGIFigureElementGraph element);

    /**
     * 
     * @param element
     * @return
     */
    public boolean setAxisElement(SGIFigureElementAxis element);

    /**
     * 
     */
    public SGAxis getXAxis(final SGData data);

    /**
     * 
     */
    public SGAxis getYAxis(final SGData data);

    /**
     * 
     */
    public Rectangle2D getLegendRect();

    public boolean setVisible(boolean flag);

    public boolean isVisible();
    
    /**
     * Returns the index of data object in legend.
     * @param data
     *             a data object
     * @return
     *             the index of data object in legend or -1 if not found
     */
    public int getIndex(SGData data);
    
    /**
     * Sort the order of legend objects.
     * @param dataArray
     *                   an array of data
     * @param indexArray
     *                   an array of index
     * @return
     *                   true if succeeded
     */
    public boolean sortLegend(SGData[] dataArray, int[] indexArray);
    
    /**
     * Returns whether this legend is selected.
     * @return
     *         true if legend is selected
     */
    public boolean isSelected();
    
    /**
     * Select or deselect this legend.
     * @param b
     *          true to select and false to deselect
     */
    public void setSelected(boolean b);
    
    /**
     * Clear all focused data.
     */
    public void clearAllFocusedData();
    

    public static final String TAG_NAME_LEGEND = "Legend";

    public static final String KEY_LEGEND_X = "X";

    public static final String KEY_LEGEND_Y = "Y";

    public static final String KEY_LEGEND_VISIBLE = "LegendVisible";

    public static final String KEY_FRAME_VISIBLE = "FrameVisible";

    public static final String KEY_FRAME_LINE_WIDTH = "FrameLineWidth";

    public static final String KEY_FRAME_LINE_COLOR = "FrameLineColor";

    public static final String KEY_BACKGROUND_COLOR = "BackgroundColor";

    public static final String KEY_BACKGROUND_TRANSPARENT = "BackgroundTransparent";

    public static final String KEY_SYMBOL_SPAN = "SymbolSpan";

    /**
     * Moves the data of given ID to the top or the bottom in legend.
     * 
     * @param id
     *           the ID of an object
     * @param toTop
     *           true to move to the top
     * @return true if succeeded
     */
    public boolean moveLegendToEnd(final int id, final boolean toTop);

    /**
     * Moves the data of given ID to upper or lower in legend.
     * 
     * @param id
     *           the ID of an object
     * @param toUpper
     *           true to move to upper
     * @return true if succeeded
     */
    public boolean moveLegend(final int id, final boolean toUpper);

	/**
	 * Returns a list of viewable data.
	 * 
	 * @return a list of viewable data
	 */
	public List<SGData> getViewableDataList();
	
    /**
     * Returns a list of focused data objects in sorted order.
     * 
     * @return a list of focused data objects
     */
    public List<SGData> getFocusedDataListInSortedOrder();

}
