package jp.riken.brain.ni.samuraigraph.base;

public abstract class SGAxisValue implements Comparable<SGAxisValue> {

	public SGAxisValue() {
		super();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SGAxisValue) {
			SGAxisValue aValue = (SGAxisValue) o;
			return (this.getValue() == aValue.getValue());
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(SGAxisValue o) {
		if (!(o instanceof SGAxisValue)) {
			throw new IllegalArgumentException("!(o instanceof SGAxisValue)");
		}
		SGAxisValue aValue = (SGAxisValue) o;
		return Double.valueOf(this.getValue()).compareTo(Double.valueOf(aValue.getValue()));
	}

	public abstract double getValue();
	
	public abstract SGAxisValue plus(SGAxisStepValue step);

	public abstract SGAxisValue minus(SGAxisStepValue step);

	public abstract SGAxisValue adjustValue(SGAxisValue min, SGAxisValue max, 
			final int digit);

}
