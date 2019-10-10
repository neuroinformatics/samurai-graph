package jp.riken.brain.ni.samuraigraph.base;

/**
 * The base class of a block of text strings.
 *
 */
public abstract class SGStringBlock {
	
	/**
	 * The array of text strings.
	 */
	protected String[] mValues = null;
	
	/**
	 * Builds an block object.
	 * 
	 * @param values
	 *           the array of double values
	 */
	public SGStringBlock(final String[] values) {
		super();
		if (values == null) {
			throw new IllegalArgumentException("values == null");
		}
		this.mValues = values.clone();
	}

	
	/**
	 * Returns the length of array.
	 * 
	 * @return the length of array
	 */
	public int getLength() {
		return this.mValues.length;
	}
	
	/**
	 * Returns the array of double values.
	 * 
	 * @return the array of double values
	 */
	public String[] getValues() {
		return this.mValues.clone();
	}
	
	/**
	 * Returns the value at given index.
	 * 
	 * @param index
	 *           the array index
	 * @return the value at given index
	 */
	public String getValue(final int index) {
		if (index < 0 || index >= this.mValues.length) {
			throw new IllegalArgumentException("index " + index + " is out of the bounds [0, " 
					+ (this.mValues.length - 1) + "].");
		}
		return this.mValues[index];
	}

	/**
	 * Returns a text string for this object.
	 * 
	 * @return a text string for this object
	 */
	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		sb.append(this.paramString());
		sb.append(']');
		return sb.toString();
	}
	
	/**
	 * Returns a text string for this object.
	 * 
	 * @return a text string for this object
	 */
	public String paramString() {
		StringBuffer sb = new StringBuffer();
		sb.append("value length=");
		sb.append(this.mValues.length);
		return sb.toString();
	}

}
