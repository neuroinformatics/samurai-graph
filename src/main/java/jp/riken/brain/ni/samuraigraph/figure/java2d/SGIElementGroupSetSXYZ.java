package jp.riken.brain.ni.samuraigraph.figure.java2d;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;

public interface SGIElementGroupSetSXYZ extends SGIElementGroupSetForData {
    
    /**
     * Returns a color map which is the first element of an array.
     * 
     * @return the first element of an array of line groups, or null when this
     *         group set does not have any line groups
     */
    public SGElementGroupPseudocolorMap getColorMap();
    
    /**
     * Sets the Z-axis.
     * 
     * @param axis
     *            an axis to set to the Z-axis
     * @return true if succeeded
     */
    public boolean setZAxis(final SGAxis axis);
    
    /**
     * Updates the size of color map.
     * 
     * @return true if succeeded
     */
    public boolean updateColorMapSize();
    
    public static final String ELEMENT_GROUP_COLOR_MAP = "ColorMap";
}
