package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Point;
import java.util.List;

/**
 * An interface for a figure element that has data objects.
 *
 */
public interface SGIFigureElementForData extends SGIFigureElement{

    /**
     * Set the name of a data object.
     * 
     * @param data
     *            the data object to get the name
     * @return the name of data object
     */
    public String getDataName(SGData data);

    /**
     * Set the name of a data object.
     * 
     * @param data
     *            the data object to set the name
     * @return true if succeeded
     */
    public boolean setDataName(String name, SGData data);

    /**
     * Returns whether the data object is visible.
     * 
     * @return whether the data object is visible
     */
    public boolean isDataVisible(SGData data);

    /**
     * Returns whether a given data is visible in legend.
     * 
     * @param data
     *           a data
     * @return true if a given data is visible in legend
     */
    public boolean isDataVisibleInLegend(final SGData data);

    /**
     * Returns whether the data object is selected.
     * 
     * @param data
     *             a data object
     * @return
     *             true if selected
     */
    public boolean isDataSelected(final SGData data);
    
    /**
     * Checks whether the objects related to a given data are changed.
     * 
     * @param data
     *          a data
     * @return true if something changed related to a given data
     */
    public boolean checkDataChanged(final SGData data);

    public boolean isDataChanged(final SGData data);

    /**
     * Returns a list of focused data objects.
     * @return
     *         a list of focused data objects
     */
    public List<SGData> getFocusedDataList();
    
    /**
     * Returns a list of cut data objects.
     * 
     * @return
     *         a list of cut data objects
     */
    public List<SGData> cutFocusedData();
    
    /**
     * Returns a list of labels for data obejcts.
     * 
     * @return a list of labels for data obejcts 
     */
    public List<DataLabel> getDataLabelList();

    /**
     * A class of the label for data objects.
     *
     */
    public static class DataLabel {
        
        private String mText = null;
        
        private Point mLocation = new Point();
        
        /**
         * The default constaructor.
         *
         */
        public DataLabel(final String text, final int x, final int y) {
            super();
            this.mText = text;
            this.mLocation.x = x;
            this.mLocation.y = y;
        }
        
        public String getText() {
            return this.mText;
        }
        
        public int getX() {
            return this.mLocation.x;
        }
        
        public int getY() {
            return this.mLocation.y;
        }
    }
    
    /**
     * Returns the data of a given ID.
     * 
     * @param id
     *           the ID of data
     * @return the data object if it exists
     */
    public SGData getData(final int id);

    /**
     * Returns the list of axis information of a given data.
     * 
     * @param data
     *           a data object
     * @return the list of axis information of a given data
     */
    public List<SGDataAxisInfo> getAxisInfoList(SGData data,
    		final boolean forAnimationFrames);

    /**
     * Returns the list of visible data objects.
     * 
     * @return a list of visible data objects
     */
    public List<SGData> getVisibleDataList();

    /**
     * 
     */
    public List<Boolean> getVisibleFlagList(SGData data);

    /**
     * Returns the child object for given data.
     * 
     * @param data
     *           data object
     * @return the child object
     */
    public SGIChildObject getChild(SGData data);

    /**
     * Returns the style of drawing elements of given data.
     *
     * @param data
     *          a data
     * @return list of the style
     */
    public List<SGStyle> getStyle(SGData data);

    /**
     * Sets the data selection.
     *
     * @param data
     *           the data to set the state of selection
     * @param b
     *           true to be selected
     */
    public void setDataFocused(SGData data, boolean b);

}
