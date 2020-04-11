package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The result of setting properties.
 *
 */
public class SGPropertyResults implements Cloneable {
    
    /**
     * The status of succeeded to set the property.
     * 
     */
    public static final int SUCCEEDED = 0;
    
    /**
     * The status of failed to set the property with an invalid input value.
     * 
     */
    public static final int INVALID_INPUT_VALUE = 1;

    /**
     * The status that a given key is not found.
     * 
     */
    public static final int NOT_FOUND = 2;

    /**
     * The status that a given key is skipped.
     * 
     */
    public static final int SKIPPED = 3;

    /**
     * The map of status.
     * 
     */
    private Map<String, Integer> mMap = new HashMap<String, Integer>();
    
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
    public SGPropertyResults() {
        super();
    }

    /**
     * Puts a result for a given key.
     * 
     * @param key
     *           the property key
     * @param status
     *           the status of setting a property
     * @return true if succeeded
     */
    public boolean putResult(final String key, final int status) {
		String uKey = key.toUpperCase();
		if (this.mKeyList.contains(uKey)) {
			this.mKeyList.remove(uKey);
		}
		this.mKeyList.add(uKey);
		this.mOriginalKeyMap.put(uKey, key);
        this.mMap.put(uKey, Integer.valueOf(status));
        return true;
    }
    
	/**
	 * Removes a result for a given key.
	 * 
	 * @param key
	 *           the key
	 * @return removed result if it exists, otherwise null
	 */
	public Integer removeResult(final String key) {
		String uKey = key.toUpperCase();
		this.mKeyList.remove(uKey);
		this.mOriginalKeyMap.remove(uKey);
		return this.mMap.remove(uKey);
	}

    /**
     * Returns the result of the given key.
     * 
     * @param key
     *           the property key
     * @return the result
     */
    public Integer getResult(final String key) {
		String uKey = key.toUpperCase();
        return this.mMap.get(uKey);
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
     * Dumps all results.
     *
     */
    public void showResults() {
        Iterator<String> itr = this.mKeyList.iterator();
        while (itr.hasNext()) {
        	String key = itr.next();
        	String uKey = key.toUpperCase();
        	String name = this.mOriginalKeyMap.get(uKey);
            final int value = this.mMap.get(uKey);
            StringBuffer sb = new StringBuffer();
            sb.append(name);
            sb.append("...");
            String status = "";
            if (SUCCEEDED == value) {
            	status = "OK";
            } else if (INVALID_INPUT_VALUE == value) {
                status = "NG";
            } else if (NOT_FOUND == value) {
                status = "NOT FOUND";
            } else if (SKIPPED == value) {
                status = "SKIPPED";
            }
            sb.append(status);
            System.out.println(sb.toString());
        }
    }
    
    /**
     * Clones this object.
     * 
     * @return a copy of this object
     */
    public final Object clone() {
        try {
            SGPropertyResults copy = (SGPropertyResults) super.clone();
            copy.mKeyList = new ArrayList<String>(this.mKeyList);
            copy.mMap = new HashMap<String, Integer>(this.mMap);
            return copy;
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     * Returns the iterator of keys.
     * 
     * @return the iterator of keys
     */
    public Iterator<String> getKeyIterator() {
    	return this.mKeyList.iterator();
    }

}
