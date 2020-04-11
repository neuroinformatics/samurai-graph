package jp.riken.brain.ni.samuraigraph.base;

public abstract class SGAxisStepValue {

	public SGAxisStepValue() {
		super();
	}
	
	/**
	 * Returns true if this object has zero step value.
	 * 
	 * @return true if this object has zero step value
	 */
	public abstract boolean isZero();
	
	/**
	 * Returns a negated step value.
	 * 
	 * @return a negated step value
	 */
	public abstract SGAxisStepValue negated();
}
