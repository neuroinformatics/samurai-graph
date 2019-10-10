package jp.riken.brain.ni.samuraigraph.base;

/**
 * An interface for the copiable objects.
 * 
 */
public interface SGICopiable {

    /**
     * Copy the object. (Deep Copy)
     * 
     * @return
     *       A copied object.
     */
    public Object copy();

}
