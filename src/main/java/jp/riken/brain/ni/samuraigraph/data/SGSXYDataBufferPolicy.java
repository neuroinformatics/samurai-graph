package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;

/**
 * Policy of getting data buffer from scalar-type XY data.
 *
 */
public class SGSXYDataBufferPolicy extends SGDataBufferPolicy {
	private boolean shiftValuesContained;
	private boolean takeAllStride;
	public SGSXYDataBufferPolicy(final boolean all, final boolean removeInvalidValues,
			final boolean editedValuesReflected, final boolean shift,
			final boolean takeAllStride) {
		super(all, removeInvalidValues, editedValuesReflected);
		this.shiftValuesContained = shift;
		this.takeAllStride = takeAllStride;
	}
	public boolean isShiftValuesContained() {
		return this.shiftValuesContained;
	}
	public boolean isTakingAllStride() {
		return this.takeAllStride;
	}
}
