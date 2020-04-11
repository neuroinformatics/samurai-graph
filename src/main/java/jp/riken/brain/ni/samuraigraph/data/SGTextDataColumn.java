package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIStringModifier;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

/**
 * The class of data column of the text type.
 *
 */
public class SGTextDataColumn extends SGDataColumn {

    /**
     * An array of text strings.
     */
    protected String[] mArray = null;
    
	/**
	 * The modifier.
	 */
	private SGIStringModifier mModifier = null;

    /**
     * Builds this object with text strings.
     * 
     * @param title
     *              a text string for the title
     * @param array
     *              an array of text strings
     */
    public SGTextDataColumn(final String title, final String[] array) {
        this(title, array, null);
    }

    /**
     * Builds this object with text strings and a modifier.
     * 
     * @param title
     *              a text string for the title
     * @param stringArray
     *              an array of text strings
     * @param mod
     *              the modifier
     */
    public SGTextDataColumn(final String title, final String[] stringArray,
    		final SGIStringModifier mod) {
        
        super(title);

        // check
        if (stringArray == null) {
            throw new IllegalArgumentException("stringArray == null");
        }
        
        // set to the attributes
        String[] stringArrayNew = null;
        if (mod != null) {
        	stringArrayNew = new String[stringArray.length];
        	for (int ii = 0; ii < stringArray.length; ii++) {
        		if (stringArray[ii] == null) {
        			stringArrayNew[ii] = "";
        		} else {
            		stringArrayNew[ii] = mod.modify(stringArray[ii]);
        		}
        	}
        } else {
    		stringArrayNew = SGUtility.copyStringArray(stringArray);
    		for (int ii = 0; ii < stringArrayNew.length; ii++) {
    			if (stringArrayNew[ii] == null) {
    				stringArrayNew[ii] = "";
    			}
    		}
        }
        this.mArray = stringArrayNew;
        this.mModifier = mod;
    }

	/**
	 * Returns the modifier.
	 * 
	 * @return the modifier
	 */
	public SGIStringModifier getModifier() {
		return this.mModifier;
	}
	
    
    /**
     * Returns the length of data column.
     * @return
     *         the length of data column
     */
    public int getLength() {
        return this.mArray.length;
    }

    /**
     * Returns the value type of this column.
     * @return
     *         the value type of this column
     */
    public String getValueType() {
        return SGIDataColumnTypeConstants.VALUE_TYPE_TEXT;
    }
    
    /**
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();
        this.mArray = null;
    }
    
    String[] getTextArray() {
        return this.mArray;
    }
    
    /**
     * Returns a value in this data column at given row index.
     * 
     * @param rowIndex
     *            the row index
     * @return a value at given row index
     */
    public Object getValue(final int rowIndex) {
        if (rowIndex > this.mArray.length) {
            throw new IllegalArgumentException("Index out of bounds: "
                    + rowIndex + " > " + this.mArray.length);
        }
        return this.mArray[rowIndex];
    }

    /**
     * Returns an array of text strings.
     * 
     * @return an array of text strings
     */
    public String[] getStringArray() {
        return SGUtility.copyStringArray(this.mArray);
    }

    /**
     * Returns an array of text strings with given stride.
     * 
     * @param stride
     *           the stride of an array
     * @return an array of text strings
     */
    public String[] getStringArray(final SGIntegerSeriesSet stride) {
    	final int[] indices = this.calcStrideIndices(stride, this.mArray.length);
    	String[] ret = new String[indices.length];
    	for (int ii = 0; ii < ret.length; ii++) {
    		ret[ii] = this.mArray[indices[ii]];
    	}
    	return ret;
    }
}
