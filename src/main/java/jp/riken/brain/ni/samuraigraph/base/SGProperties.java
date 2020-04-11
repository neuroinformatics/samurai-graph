package jp.riken.brain.ni.samuraigraph.base;

/**
 * The base class of properties.
 * 
 */
public abstract class SGProperties implements SGIDisposable, Cloneable, SGICopiable {
    /**
     * The default constructor.
     * 
     */
    public SGProperties() {
        super();
    }

    /**
     * Clones this data object.
     * 
     * @return
     *         shallow copy of this data object
     */
    public final Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
    
    /**
     * Returns a copy of this object.
     * 
     * @return a copy of this object
     */
    public Object copy() {
        return this.clone();
    }
    
    // The flag whether this object is already disposed of.
    private boolean mDisposed = false;

    /**
     * Returns whether this object is already disposed of.
     * 
     * @return true if this object is already disposed of
     */
    public boolean isDisposed() {
    	return this.mDisposed;
    }
    
    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
    }

}
