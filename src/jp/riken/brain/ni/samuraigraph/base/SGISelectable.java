package jp.riken.brain.ni.samuraigraph.base;

/**
 * An interface for the objects which can be selected.
 * 
 */
public interface SGISelectable {

    /**
     * Sets selected or deselected the object.
     * 
     * @param b
     *            true selects and false deselects the object
     */
    public void setSelected(boolean b);

    /**
     * Returns whether this object is selected
     * 
     * @return whether this object is selected
     */
    public boolean isSelected();

}
