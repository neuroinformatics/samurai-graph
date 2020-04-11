package jp.riken.brain.ni.samuraigraph.base;

import java.util.HashMap;
import java.util.Map;

/**
 * Properties for users.
 * 
 * @author kuromaru
 *
 */
public class SGUserProperties {

	private Map<String, String> mProperties = new HashMap<String, String>();

	private static SGUserProperties mInstance = new SGUserProperties();

	private SGUserProperties() {
	}

	/**
	 * Returns the instance of this class.
	 * 
	 * @return the instance of this class
	 */
	public static SGUserProperties getInstance() {
		return mInstance;
	}

	/**
	 * Returns the property.
	 * 
	 * @param key
	 *           the key of a property
	 * @return the property
	 */
	public String getProperty(String key) {
		return this.mProperties.get(key);
	}

	/**
	 * Sets a property.
	 * 
	 * @param key
	 *           the key of a property
	 * @param value
	 *           the value of a property
	 */
	public void setProperty(String key, String value) {
		this.mProperties.put(key, value);
	}
	
	public String toString() {
		return this.mProperties.toString();
	}
}
