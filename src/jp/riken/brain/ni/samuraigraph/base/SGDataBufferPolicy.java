package jp.riken.brain.ni.samuraigraph.base;

/**
 * Policy of getting data buffer.
 *
 */
public class SGDataBufferPolicy {
	
	private boolean mAllPointsGotten;
	
	private boolean mEditedValuesReflected;
	
	private boolean mInvalidValuesRemoved;
	
	public SGDataBufferPolicy(final boolean all, final boolean removeInvalidValues,
			final boolean editedValuesReflected) {
		super();
		this.mAllPointsGotten = all;
		this.mInvalidValuesRemoved = removeInvalidValues;
		this.mEditedValuesReflected = editedValuesReflected;
	}
	
	public boolean isAllValuesGotten() {
		return this.mAllPointsGotten;
	}
	
	public boolean isEditedValuesReflected() {
		return this.mEditedValuesReflected;
	}
	
	public boolean isInvalidValuesRemoved() {
		return this.mInvalidValuesRemoved;
	}
}
