package jp.riken.brain.ni.samuraigraph.base;


/**
 * An object to manage timing lines.
 * 
 */
public interface SGIFigureElementTimingLine extends SGIFigureElement {

    public static final String TAG_NAME_TIMING_LINES = "TimingLines";

    /**
     * 
     * @param element
     */
    public void setAxisElement(final SGIFigureElementAxis element);

    /**
     * Inserts a timing line at a given point with default axis.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addTimingLine(final int x, final int y);

    /**
     * Inserts a timing line with given axis value to given axis.
     * 
     * @param id
     *           the ID to set
     * @param axisLocation
     *           location of the axis
     * @param value
     *           the axis value for given axis
     * @return true if succeeded
     */
    public boolean addTimingLine(final int id, final int axisLocation,
    		final double value);

    /**
     * 
     * @param x
     * @param y
     */
    public void guideToAdd(final int x, final int y);

}
