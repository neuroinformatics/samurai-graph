package jp.riken.brain.ni.samuraigraph.base;


/**
 * An object to manage shape objects.
 * 
 */
public interface SGIFigureElementShape extends SGIFigureElement {

    /**
     * 
     */
    public static final String TAG_NAME_SHAPE = "Shape";

    public static final int RECTANGLE = 0;

    public static final int ELLIPSE = 1;

    public static final int ARROW = 2;

    public static final int LINE = 3;

    /**
     * 
     * @param element
     */
    public void setAxisElement(final SGIFigureElementAxis element);

    /**
     * Inserts a shape object at a given point with default axes.
     * 
     * @param type
     *          the type of shape
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addShape(final int type, final float x, final float y);

    /**
     * Inserts a shape object at a given point with default axes.
     * 
     * @param id
     *          the ID to set
     * @param type
     *          the type of shape
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addShape(final int id, final int type, final double x, 
            final double y);
    
    /**
     * Inserts a shape object with given axis values for given axes.
     * 
     * @param id
     *          the ID to set
     * @param type
     *          the type of shape
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
    public boolean addShape(final int id, final int type, final double x,
            final double y, final String xAxisLocation,
            final String yAxisLocation);

}
