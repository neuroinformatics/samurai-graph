package jp.riken.brain.ni.samuraigraph.base;


/**
 * An object to manage axis break symbols.
 * 
 */
public interface SGIFigureElementAxisBreak extends SGIFigureElement {

    public static final String TAG_NAME_AXIS_BREAK = "AxisBreakSymbols";

    /**
     * 
     * @param element
     */
    public void setAxisElement(final SGIFigureElementAxis element);

    /**
     * Inserts an axis break symbol at a given point with default axes.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addAxisBreakSymbol(final float x, final float y);

    /**
     * Inserts a axis break symbol with given axis value for default axes.
     * 
     * @param id
     *          the ID to set
     * @param x
     *          axis value for default x-axis
     * @param y
     *          axis value for default y-axis
     * @return true if succeeded
     */
    public boolean addAxisBreakSymbol(final int id, final double x, final double y);
    
    /**
     * Inserts a axis break symbol with given axis value for given axes.
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
    public boolean addAxisBreakSymbol(final int id, final double x, final double y,
            final String xAxisLocation, final String yAxisLocation);

}
