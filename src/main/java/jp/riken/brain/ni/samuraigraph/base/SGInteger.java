package jp.riken.brain.ni.samuraigraph.base;

import java.util.HashMap;
import java.util.Map;

/**
 * The class for an integer number.
 *
 */
public class SGInteger implements Cloneable {

	/**
	 * An integer number.
	 */
	private Integer mNumber = null;

	/**
	 * The alias map.
	 */
	private Map<String, Integer> mAliasMap = new HashMap<String, Integer>();
	
	/**
	 * The string representation of this number.
	 */
	private String mText = null;
	
	/**
	 * Builds an object with an integer number.
	 * 
	 * @param num
	 *          an integer number
	 */
	public SGInteger(final int num) {
		super();
		this.mNumber = num;
	}

	/**
	 * Builds an object with the string representation.
	 * 
	 * @param text
	 *          the string representation of this number
	 */
	public SGInteger(final String text) {
		super();
		this.mText = text;
	}

	/**
	 * Builds an object with an integer number and the string representation of it.
	 * 
	 * @param num
	 *          an integer number
	 * @param text
	 *          the string representation of this number
	 */
	public SGInteger(final int num, final String text) {
		this(num);
		this.mText = text;
	}

	/**
	 * Builds an object with an integer number object.
	 * 
	 * @param num
	 *          an integer number object
	 */
	public SGInteger(final SGInteger num) {
		super();
		if (num == null) {
			throw new IllegalArgumentException("num == null");
		}
		this.mNumber = num.mNumber;
		this.mText = num.mText;
	}
	
	/**
	 * Returns the number.
	 * 
	 * @return the number
	 */
	public Integer getNumber() {
		if (this.mNumber != null) {
			return this.mNumber;
		} else {
			return this.mAliasMap.get(this.mText);
		}
	}

	/**
	 * Sets the number.
	 * 
	 * @param num
	 *           the number to set
	 */
	public void setNumber(Integer num) {
		this.mNumber = num;
	}

	/**
	 * Returns the alias.
	 * 
	 * @return the alias
	 */
	public String getText() {
		return this.mText;
	}

	/**
	 * Sets the alias.
	 * 
	 * @param text
	 *           a text string to set
	 */
	public void setText(String text) {
		this.mText = text;
	}
	
	/**
	 * Adds an alias for given integer value.
	 * 
	 * @param value
	 *           an integer value
	 * @param alias
	 *           the alias
	 */
	public void addAlias(final int value, final String alias) {
		this.mAliasMap.put(alias, value);
	}
	
	public Map<String, Integer> getAliasMap() {
		return new HashMap<String, Integer>(this.mAliasMap);
	}
	
    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
	@Override
    public Object clone() {
        try {
        	SGInteger obj = (SGInteger) super.clone();
        	obj.mAliasMap = new HashMap<String, Integer>(this.mAliasMap);
        	return obj;
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

	@Override
	public String toString() {
		if (this.mText != null) {
			return this.mText;
		} else {
			Integer num = this.getNumber();
			if (num != null) {
				return num.toString();
			} else {
				return "null";
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (!(obj instanceof SGInteger)) {
			return false;
		}
		SGInteger num = (SGInteger) obj;
		if (!SGUtility.equals(this.mText, num.mText)) {
			return false;
		}
		if (!SGUtility.equals(this.mNumber, num.mNumber)) {
			return false;
		}
		return true;
	}

	/**
	 * Parses a given text string and returns an integer series object.
	 * 
	 * @param str
	 *          a text string
	 * @param aliasMap
	 *          the alias map
	 * @return an integer series object or null if failed to parse
	 */
	public static SGInteger parse(final String str, final Map<String, Integer> aliasMap) {
		if (str == null || aliasMap == null) {
			throw new IllegalArgumentException("str == null || aliasMap == null");
		}
		String key = str.toLowerCase();
		Integer value = aliasMap.get(key);
		if (value != null) {
			// given value is contained in given alias map
			return new SGInteger(value, key);
		} else {
			if (aliasMap.keySet().contains(key)) {
				// alias map has the key but does not have the value
				return new SGInteger(key);
			} else {
				value = SGUtilityText.getInteger(str);
				if (value != null) {
					// given value is an integer number
					return new SGInteger(value);
				} else {
					// invalid value
					return null;
				}
			}
		}
	}
	
}
