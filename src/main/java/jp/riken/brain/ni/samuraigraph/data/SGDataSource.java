package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;

/**
 * The base class for data source object.
 *
 */
public abstract class SGDataSource implements SGIDataSource {

    /**
     * The flag whether this object is already disposed of.
     */
    private boolean mDisposed = false;

    /**
     * The default constructor.
     */
    public SGDataSource() {
    	super();
    }

    /**
     * Disposes of this object.
     *
     */
	@Override
	public void dispose() {
		this.mDisposed = true;
	}

    /**
     * Returns whether this object is already disposed of.
     * 
     * @return true if this object is already disposed of
     */
	@Override
    public boolean isDisposed() {
    	return this.mDisposed;
    }

}
