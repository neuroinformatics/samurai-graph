package jp.riken.brain.ni.samuraigraph.base;

/**
 * An interface for disposable objects.
 */
public interface SGIDisposable {

    /**
     * Dispose all attributes.
     * 
     */
    public void dispose();

    /**
     * Returns whether this object is already disposed of.
     * 
     * @return true if this object is already disposed of
     */
    public boolean isDisposed();

}
