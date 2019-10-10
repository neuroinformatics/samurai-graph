package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;

/**
 * Column information for a single dimensional array.
 *
 */
public class SGSDArrayDataColumnInfo extends SGDataColumnInfo {

	/**
	 * Length of the array.
	 */
	private int mLength = -1;
	
	/**
	 * Builds an object.
	 * 
	 * @param title
	 *          the title
	 * @param valueType
	 *          value type
	 */
	public SGSDArrayDataColumnInfo(String title, String valueType) {
		super(title, valueType);
	}

	/**
	 * Builds an object.
	 * 
	 * @param title
	 *          the title
	 * @param valueType
	 *          value type
	 * @param length
	 *          length of the array
	 */
	public SGSDArrayDataColumnInfo(String title, String valueType, final int length) {
		this(title, valueType);
		this.mLength = length;
	}

	/**
	 * Returns the length.
	 * 
	 * @return the length of the array
	 */
	public int getLength() {
		return this.mLength;
	}
	
    @Override
    public boolean equals(Object obj) {
    	if (!(obj instanceof SGSDArrayDataColumnInfo)) {
    		return false;
    	}
    	if (!super.equals(obj)) {
    		return false;
    	}
    	SGSDArrayDataColumnInfo col = (SGSDArrayDataColumnInfo) obj;
    	if (this.mLength != col.mLength) {
    		return false;
    	}
    	return true;
    }

}
