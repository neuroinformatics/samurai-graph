package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.List;

/**
 * The class for an attribute.
 *
 */
public class SGAttribute {

	/**
	 * The name.
	 */
	private String mName = null;

	/**
	 * The list of values.
	 */
	private List<Object> mValueList = new ArrayList<Object>();

	/**
	 * Builds an attribute.
	 * 
	 * @param name
	 *           the name
	 * @param values
	 *           the values
	 */
	public SGAttribute(final String name, final Object[] values) {
		super();
		this.mName = name;
		if (values != null) {
			for (int ii = 0; ii < values.length; ii++) {
				this.mValueList.add(values[ii]);
			}
		}
	}

	/**
	 * Builds an attribute.
	 * 
	 * @param name
	 *           the name
	 * @param value
	 *           the value
	 */
	public SGAttribute(final String name, final Object value) {
		this(name, (value != null) ? new Object[] { value } : null);
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.mName;
	}

	/**
	 * Returns the number of values.
	 * 
	 * @return the number of values
	 */
	public int getSize() {
		return this.mValueList.size();
	}

	/**
	 * Returns the list of values.
	 * 
	 * @return the list of values
	 */
	public List<Object> getValues() {
		return new ArrayList<Object>(this.mValueList);
	}

	/**
	 * Returns the value at given array index.
	 * 
	 * @param index
	 *           the array index
	 * @return value at given array index
	 */
	public Object getValue(final int index) {
		if (this.mValueList.size() == 0) {
			return null;
		}
		if (index < 0 || index >= this.mValueList.size()) {
			throw new IllegalArgumentException("Index out of bounds: " + index);
		}
		return this.mValueList.get(index);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SGAttribute)) {
			return false;
		}
		SGAttribute attr = (SGAttribute) obj;
		if (!this.mName.equals(attr.mName)) {
			return false;
		}
		if (!SGUtility.equals(this.mValueList, attr.mValueList)) {
			return false;
		}
		return true;
	}

}
