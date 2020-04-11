package jp.riken.brain.ni.samuraigraph.base;


/**
 * An object to manage text string objects.
 * 
 */
public interface SGIFigureElementString extends SGIFigureElement {

    /**
     * Insert a label at a given point with default axes. The text string must be input later.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addString(int x, int y);

    /**
     * Insert a label at a given point with given axis values with default axes.
     * 
     * @param id
     *          the ID to set
     * @param str
     *          a text string to insert
     * @param x
     *          axis value for default x-axis
     * @param y
     *          axis value for default y-axis
     * @return true if succeeded
     */
    public boolean addString(final int id, final String str, final double x, 
            final double y);

    /**
     * Insert a label with given axis values with given axes.
     * 
     * @param id
     *          the ID to set
     * @param str
     *          a text string to insert
     * @param x
     *          axis value for given x-axis
     * @param y
     *          axis value for given y-axis
     * @param xAxisLocation
     *          location of the x-axis
     * @param yAxisLocation
     *          location of the y-axis
     * @return true if succeeded
     */
    public boolean addString(final int id, final String str, final double x, 
            final double y, final String xAxisLocation, final String yAxisLocation);

    /**
     * Insert a label at a given point with default axes.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return id of string label if succeeds. or -1.
     */
    public int addNewString(final String str, final int x, final int y);
    
    /**
     * Returns a label text in the given ID element.
     * 
     * @param id
     * @return label text. null if string element which has a given id not found.
     */
    public String getString(final int id);
    
    /**
     * Set newText to the given ID label.
     * 
     * @param id
     * @param text
     * @return true if succeeds.
     */
    public boolean setString(final int id, final String newText);
    
    /**
     * 
     * @param id
     * @param visible
     */
    public void setVisible(final int id, final boolean visible);
    
    /**
     * 
     * @param element
     */
    public void setAxisElement(final SGIFigureElementAxis element);

    public static final String TAG_NAME_STRING_ELEMENT = "Labels";

}
