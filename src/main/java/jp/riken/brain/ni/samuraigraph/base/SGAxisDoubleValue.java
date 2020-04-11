package jp.riken.brain.ni.samuraigraph.base;

import java.math.BigDecimal;

public class SGAxisDoubleValue extends SGAxisValue {

	private final double mValue;
	
    /**
     * Creates an instance for zero value.
     * 
     */
	public SGAxisDoubleValue() {
		super();
		this.mValue = 0.0;
	}
	
	public SGAxisDoubleValue(final double value) {
		super();
		this.mValue = value;
	}

	@Override
	public double getValue() {
		return this.mValue;
	}
	
	@Override
	public SGAxisValue plus(SGAxisStepValue step) {
		if (step instanceof SGAxisDoubleStepValue) {
			SGAxisDoubleStepValue d = (SGAxisDoubleStepValue) step;
			return new SGAxisDoubleValue(this.mValue + d.getValue());
		} else if (step instanceof SGAxisDateStepValue) {
			SGAxisDateStepValue d = (SGAxisDateStepValue) step;
			SGAxisDateValue dateValue = new SGAxisDateValue(this.mValue);
			return dateValue.plus(d);
		} else {
			throw new IllegalArgumentException("Invalid parameter: " + step);
		}
	}

	@Override
	public SGAxisValue minus(SGAxisStepValue step) {
		if (step instanceof SGAxisDoubleStepValue) {
			SGAxisDoubleStepValue d = (SGAxisDoubleStepValue) step;
			return new SGAxisDoubleValue(this.mValue - d.getValue());
		} else if (step instanceof SGAxisDateStepValue) {
			SGAxisDateStepValue d = (SGAxisDateStepValue) step;
			SGAxisDateValue dateValue = new SGAxisDateValue(this.mValue);
			return dateValue.minus(d);
		} else {
			throw new IllegalArgumentException("Invalid parameter: " + step);
		}
	}

	@Override
	public String toString() {
		return Double.toString(this.mValue);
	}
	
	@Override
	public SGAxisValue adjustValue(SGAxisValue min, SGAxisValue max, final int digit) {
        final double dValue = SGUtilityNumber.getNumberInRangeOrder(this.mValue, 
        		min.getValue(), max.getValue(), digit, BigDecimal.ROUND_HALF_UP);
        return new SGAxisDoubleValue(dValue);
	}
}
