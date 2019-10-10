package jp.riken.brain.ni.samuraigraph.base;


public class SGAxisDoubleStepValue extends SGAxisStepValue {

	private double mValue;

	public SGAxisDoubleStepValue(final double value) {
		super();
		this.mValue = value;
	}

	public double getValue() {
		return this.mValue;
	}
	
	@Override
	public String toString() {
		return Double.toString(this.mValue);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SGAxisDoubleStepValue)) {
			return false;
		}
		SGAxisDoubleStepValue step = (SGAxisDoubleStepValue) obj;
		return (this.mValue == step.mValue);
	}

	/**
	 * Returns true if this object has zero step value.
	 * 
	 * @return true if this object has zero step value
	 */
	@Override
	public boolean isZero() {
		return (this.mValue == 0.0);
	}
	
	/**
	 * Returns a negated step value.
	 * 
	 * @return a negated step value
	 */
	@Override
	public SGAxisStepValue negated() {
		return new SGAxisDoubleStepValue(- this.mValue);
	}
}
