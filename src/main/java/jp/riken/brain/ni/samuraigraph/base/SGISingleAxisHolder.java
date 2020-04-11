package jp.riken.brain.ni.samuraigraph.base;

/**
 * An interface which provides the definition of the objects which holds single
 * axis.
 */
public interface SGISingleAxisHolder extends SGIAxisHolder {

    /**
     * Returns the location of the axis in a plane.
     * 
     * @return one of the following values defined in
     *         jp.riken.brain.ni.samuraigraph.base.SGIAxisElement :
     *         AXIS_HORIZONTAL_1, AXIS_HORIZONTAL_2, AXIS_VERTICAL_1 or
     *         AXIS_VERTICAL_2.
     * @uml.property name="axisLocation"
     */
    public int getAxisLocation();

    /**
     * Sets the location of the axis in a plane.
     * 
     * @param location -
     *            one of the following values defined in
     *            jp.riken.brain.ni.samuraigraph.base.SGIAxisElement :
     *            AXIS_HORIZONTAL_1, AXIS_HORIZONTAL_2, AXIS_VERTICAL_1 or
     *            AXIS_VERTICAL_2.
     * @return
     * @uml.property name="axisLocation"
     */
    public boolean setAxisLocation(final int location);
}
