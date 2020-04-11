package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIDisposable;

/**
 * The base class of variables.
 *
 */
public abstract class SGVariable implements SGIDisposable {

	/**
	 * A flag whether this object is disposed of.
	 */
	private boolean mDisposed = false;
	
	/**
	 * The default constructor.
	 */
	public SGVariable() {
		super();
	}
	
    /**
     * Returns the name of this variable.
     * 
     * @return the name of this variable
     */
	public abstract String getName();

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
