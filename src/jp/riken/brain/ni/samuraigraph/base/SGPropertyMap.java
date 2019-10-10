package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.w3c.dom.Element;

/**
 * A map of properties.
 * 
 */
public class SGPropertyMap implements Cloneable {
	
	/**
	 * The map of properties.
	 * 
	 */
	private Map<String, String> mMap = new HashMap<String, String>();
	
	/**
	 * A list of keys to hold keys in order of addition.
	 * 
	 */
	private List<String> mKeyList = new ArrayList<String>();
	
	/**
	 * The map of original keys.
	 * 
	 */
	private Map<String, String> mOriginalKeyMap = new HashMap<String, String>();

	/**
	 * The default constructor.
	 *
	 */
	public SGPropertyMap() {
		super();
	}

	/**
	 * Puts a value for a given key.
	 * 
	 * @param key
	 *          the key
	 * @param value
	 *          the value
	 * @return previously stored value
	 */
	public String putValue(final String key, final String value) {
		String uKey = key.toUpperCase();
		if (this.mKeyList.contains(uKey)) {
			this.mKeyList.remove(uKey);
		}
		this.mKeyList.add(uKey);
		this.mOriginalKeyMap.put(uKey, key);
		return this.mMap.put(uKey, value);
	}

	/**
	 * Removes a value for a given key.
	 * 
	 * @param key
	 *           the key
	 * @return removed value if it exists, otherwise null
	 */
	public String removeValue(final String key) {
		String uKey = key.toUpperCase();
		this.mKeyList.remove(uKey);
		this.mOriginalKeyMap.remove(uKey);
		return this.mMap.remove(uKey);
	}
	
    /**
     * Returns the value.
     * 
	 * @param key
	 *          the key
     * @return the value
     */
    public String getValue(final String key) {
		String uKey = key.toUpperCase();
        return this.mMap.get(uKey);
    }
    
    /**
     * Returns a text string of the value related to the given key. 
     * The returned value is trimmed, and if double quotations exist on the both side of
     * it, they are removed.
     * If the value for the given key is equal to null, returns an empty string.
     * 
	 * @param key
	 *          the key
     * @return a text string of the value
     */
    public String getValueString(final String key) {
		String uKey = key.toUpperCase();
    	String ret = null;
    	String value = this.getValue(uKey);
        if (value != null) {
            String vStr = value.toString();
            vStr = vStr.trim();
            if (SGUtilityText.isDoubleQuoted(vStr)) {
            	vStr = vStr.substring(1, vStr.length() - 1);
            }
            ret = vStr;
        } else {
            ret = "";
        }
        return ret;
    }

    /**
     * Returns whether the value for a given key is double quoted.
     * 
     * @param key
     *           a key
     * @return true if the value for a given key is double quoted
     */
    public boolean isDoubleQuoted(final String key) {
    	String value = this.getValue(key);
    	if (value != null) {
    		return SGUtilityText.isDoubleQuoted(value);
    	} else {
    		return false;
    	}
    }
 
    /**
     * Clones this object.
     * 
     * @return a copy of this object
     */
    @Override
    public final Object clone() {
        try {
        	SGPropertyMap copy = (SGPropertyMap) super.clone();
        	copy.mKeyList = new ArrayList<String>(this.mKeyList);
            copy.mMap = new HashMap<String, String>(this.mMap);
            copy.mOriginalKeyMap = new HashMap<String, String>(this.mOriginalKeyMap);
            return copy;
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
    
    /**
     * Returns the list of keys.
     * All text strings are composed of capital characters.
     * 
     * @return the list of keys
     */
    public List<String> getKeys() {
    	return new ArrayList<String>(this.mKeyList);
    }

    /**
     * Returns the iterator of keys.
     * 
     * @return the iterator of keys
     */
    public Iterator<String> getKeyIterator() {
    	return this.mKeyList.iterator();
    }

    /**
     * Creates and returns a class object of java.util.Properties.
     * 
     * @return the properties
     */
    public Properties toProperties() {
    	Properties p = new Properties();
    	Iterator<String> itr = this.getKeyIterator();
    	while (itr.hasNext()) {
    		String key = itr.next();
    		String value = this.getValue(key);
    		p.put(key, value);
    	}
    	return p;
    }
    
    /**
     * Returns a text string for this map.
     * 
     * @return a text string for this map
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	Iterator<String> itr = this.getKeyIterator();
    	while (true) {
    		String key = itr.next();
    		String name = this.mOriginalKeyMap.get(key);
    		String value = this.getValue(key);
    		sb.append(name);
    		sb.append('=');
    		sb.append(value);
    		if (itr.hasNext()) {
        		sb.append(", ");
    		} else {
    			break;
    		}
    	}
    	return sb.toString();
    }
    
    /**
     * Sets all properties to given Element.
     * 
     * @param el
     *           an Element object
     */
    public void setToElement(Element el) {
    	Iterator<Entry<String, String>> itr = this.mMap.entrySet().iterator();
    	while (itr.hasNext()) {
    		Entry<String, String> entry = itr.next();
    		String key = entry.getKey();
    		String name = this.mOriginalKeyMap.get(key);
    		String value = this.getValue(name);
    		el.setAttribute(name, value);
    	}
    }
    
    /**
     * Returns the original key of given key.
     * 
     * @param key
     *           the key
     * @return the original key
     */
    public String getOriginalKey(final String key) {
    	return this.mOriginalKeyMap.get(key.toUpperCase());
    }
    
    /**
     * Puts all entries to this map.
     * 
     * @param map
     *           a map to put
     */
    public void putAll(SGPropertyMap map) {
    	Iterator<String> keyItr = map.getKeyIterator();
    	while (keyItr.hasNext()) {
    		String key = keyItr.next();
    		String oriKey = map.getOriginalKey(key);
    		this.putValue(oriKey, map.getValue(oriKey));
    	}
    }
}
