package jp.riken.brain.ni.samuraigraph.base;

public interface SGIAnchored {
    
    /**
     * 
     * @return true if this object is anchored.
     * @uml.property name="anchored"
     */
    public boolean isAnchored();

    /**
     * 
     * @param anchored
     * @return true if succeeds.
     * @uml.property name="anchored"
     */
    public boolean setAnchored(final boolean anchored);

}
