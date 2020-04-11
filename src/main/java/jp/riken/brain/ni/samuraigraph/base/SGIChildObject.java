package jp.riken.brain.ni.samuraigraph.base;

/**
 * The child object of a figure element.
 *
 */
public interface SGIChildObject extends SGIVisible {
    
    /**
     * Returns the ID.
     * 
     * @return the ID
     */
    public int getID();

    /**
     * Sets the ID.
     * 
     * @param id
     *           the number to set to ID
     * @return true if succeeded
     */
    public boolean setID(final int id);
    
    /**
     * Sets the magnification.
     * 
     * @param mag
     *           the magnification to set
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag);
    
    /**
     * Sets the properties directly.
     * 
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    public SGPropertyResults setProperties(SGPropertyMap map);

    /**
     * Returns a text string of the commands.
     * 
     * @return a text string of the commands
     */
	public String getCommandString(SGExportParameter params);

	/**
	 * Creates and returns a text string of commands.
	 * 
	 * @return a text string of commands
	 */
    public String createCommandString(SGExportParameter params);

    /**
     * Creates and returns the map of properties.
     * 
     * @return the map of properties
     */
    public SGPropertyMap getCommandPropertyMap(SGExportParameter params);

}