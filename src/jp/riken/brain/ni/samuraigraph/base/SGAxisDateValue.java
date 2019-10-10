package jp.riken.brain.ni.samuraigraph.base;

import java.text.ParseException;

import org.joda.time.DateTime;


public class SGAxisDateValue extends SGAxisValue {

	private final SGDate mDate;

	/**
	 * Creates an instance for a given date.
	 * 
	 * @param date
	 *          a date
	 */
	public SGAxisDateValue(SGDate date) {
		super();
		this.mDate = date;
	}

    /**
     * Creates an instance at the epoch time in UTC time zone.
     * 
     */
	public SGAxisDateValue() {
		this(new SGDate());
	}

	public SGAxisDateValue(String str) throws ParseException {
		this(new SGDate(str));
	}
	
	public SGAxisDateValue(DateTime dateTime) {
		this(new SGDate(dateTime));
	}
	
	public SGAxisDateValue(final double dateValue) {
		this(new SGDate(dateValue));
	}

	public SGAxisDateValue(final long millis) {
		this(new SGDate(millis));
	}

	public SGDate getDate() {
		return this.mDate;
	}

	@Override
	public double getValue() {
		return this.mDate.getDateValue();
	}
	
	@Override
	public SGAxisValue plus(SGAxisStepValue step) {
		if (step instanceof SGAxisDateStepValue) {
			SGAxisDateStepValue p = (SGAxisDateStepValue) step;
			DateTime dateTime = this.mDate.getUTCDateTime().plus(p.getPeriod());
			return new SGAxisDateValue(dateTime);
		} else if (step instanceof SGAxisDoubleStepValue) {
			SGAxisDoubleStepValue d = (SGAxisDoubleStepValue) step;
			return new SGAxisDateValue(this.getValue() + d.getValue());
		} else {
			throw new IllegalArgumentException("Invalid parameter: " + step);
		}
	}
	
	@Override
	public SGAxisValue minus(SGAxisStepValue step) {
		if (step instanceof SGAxisDateStepValue) {
			SGAxisDateStepValue p = (SGAxisDateStepValue) step;
			DateTime dateTime = this.mDate.getUTCDateTime().minus(p.getPeriod());
			return new SGAxisDateValue(dateTime);
		} else if (step instanceof SGAxisDoubleStepValue) {
			SGAxisDoubleStepValue d = (SGAxisDoubleStepValue) step;
			return new SGAxisDateValue(this.getValue() - d.getValue());
		} else {
			throw new IllegalArgumentException("Invalid parameter: " + step);
		}
	}
	
	@Override
	public String toString() {
		return this.mDate.toString();
	}

	@Override
	public SGAxisValue adjustValue(SGAxisValue min, SGAxisValue max, final int digit) {
		// just returns this object
		return this;
	}
}
