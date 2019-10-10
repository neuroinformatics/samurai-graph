package jp.riken.brain.ni.samuraigraph.base;


/**
 * An object to manage significant difference symbols.
 * 
 */
public interface SGIFigureElementSignificantDifference extends SGIFigureElement {

    /**
     * 
     */
    public static final String TAG_NAME_SIGNIFICANT_DIFFERENCE = "SignificantDifferenceSymbols";

    /**
     * 
     * @param element
     */
    public void setAxisElement(final SGIFigureElementAxis element);

    /**
     * Inserts a significant difference symbol at a given point with default axes.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addSignificantDifferenceSymbol(final float x, final float y);

    /**
     * Inserts a significant difference symbol with given axis values for default axes.
     * 
     * @param id
     *          the ID to set
     * @param x
     *          axis value for default x-axis
     * @param y
     *          axis value for default y-axis
     * @return true if succeeded
     */
    public boolean addSignificantDifferenceSymbol(final int id,
            final double x, final double y);

    /**
     * Inserts a significant difference symbol with given axis values for default axes.
     * 
     * @param id
     *          the ID to set
     * @param leftX
     *          axis value of the left end for default x-axis
     * @param leftY
     *          axis value of the left end for default y-axis
     * @param rightX
     *          axis value of the right end for default x-axis
     * @param rightY
     *          axis value of the right end for default y-axis
     * @param horizontalY
     *          axis value of the horizontal bar for default y-axis
     * @return true if succeeded
     */
    public boolean addSignificantDifferenceSymbol(final int id,
            final double leftX, final double leftY, final double rightX,
            final double rightY, final double horizontalY);

    /**
     * Inserts a significant difference symbol with given axis values for given axes.
     * 
     * @param id
     *          the ID to set
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
    public boolean addSignificantDifferenceSymbol(final int id, final double x, 
            final double y, final String xAxisLocation, final String yAxisLocation);

}
