package jp.riken.brain.ni.samuraigraph.base;

/**
 * An interface for the objects with visibility.
 */
public interface SGIVisible {
    /**
     * Returns whether this object is visible.
     * 
     * @return whether this object is visible
     * @uml.property name="visible"
     */
    public boolean isVisible();

    /**
     * Set visible or invisible.
     * 
     * @param b -
     *            true sets visible and false sets invisible
     * @uml.property name="visible"
     */
    public void setVisible(final boolean b);

}
