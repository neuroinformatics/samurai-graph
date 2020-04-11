package jp.riken.brain.ni.samuraigraph.base;

/**
 * A class for the value range.
 * 
 */
public class SGValueRange {

	/**
	 * The range.
	 */
	private SGTuple2d mRange = new SGTuple2d();

	/**
	 * The default constructor.
	 *
	 */
	public SGValueRange() {
		super();
	}

	/**
	 * Builds an object with given minimum and maximum values.
	 * 
	 * @param min
	 *            the minimum value
	 * @param max
	 *            the maximum value
	 */
	public SGValueRange(final double min, final double max) {
		this();
		this.mRange.setValues(min, max);
	}

	/**
	 * Builds an object with given range object.
	 * 
	 * @param range
	 *            the range to set
	 */
	public SGValueRange(SGValueRange range) {
		this(range.getMinValue(), range.getMaxValue());
	}

	/**
	 * Returns the minimum value.
	 * 
	 * @return the minimum value
	 */
	public double getMinValue() {
		return this.mRange.x;
	}
	
	/**
	 * Returns the maximum value.
	 * 
	 * @return the maximum value
	 */
	public double getMaxValue() {
		return this.mRange.y;
	}
	
	/**
	 * Returns the range.
	 * 
	 * @return the range
	 */
	public SGTuple2d getRange() {
		return (SGTuple2d) this.mRange.clone();
	}
	
	/**
	 * Returns whether the minimum value is valid.
	 * 
	 * @return true if the minimum value is valid
	 */
	public boolean isMinValid() {
		final double value = this.mRange.x;
		return !(Double.isNaN(value) || Double.isInfinite(value));
	}
	
	/**
	 * Returns whether the maximum value is valid.
	 * 
	 * @return true if the maximum value is valid
	 */
	public boolean isMaxValid() {
		final double value = this.mRange.y;
		return !(Double.isNaN(value) || Double.isInfinite(value));
	}
	
	/**
	 * Sets the minimum and the maximum values.
	 * 
	 * @param min
	 *           the minimum value
	 * @param max
	 *           the maximum value
	 */
	public void setRange(final double min, final double max) {
		if (min > max) {
			throw new IllegalArgumentException("min: "+ min + " > max: " + max);
		}
		this.mRange.setValues(min, max);
	}

	/**
	 * Sets the range.
	 * 
	 * @param range
	 *           the range object to copy
	 */
	public void setRange(SGValueRange range) {
		this.mRange.setValues(range.mRange);
	}

	/**
	 * Returns the string representation of this object.
	 * 
	 * @return the string representation of this object
	 */
	public String toString() {
		return this.mRange.toString();
	}
}
