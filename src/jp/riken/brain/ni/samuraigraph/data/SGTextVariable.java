package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIStringModifier;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import ucar.nc2.Variable;

/**
 * A variable class for tick labels. A variable has only one dimension and 
 * text strings for tick labels.
 *
 */
public class SGTextVariable extends SGCharVariable {
	
	/**
	 * A array of text strings.
	 */
	private String[] mStringArray = null;
	
	/**
	 * The modifier.
	 */
	private SGIStringModifier mModifier = null;

	/**
	 * Builds an object with text strings.
	 *
	 * @param var
	 *           the variable
	 * @param ncfile
	 *           the netCDF file
	 * @param stringArray
	 *           an array of text strings
	 */
	public SGTextVariable(final Variable var, final SGNetCDFFile ncfile, 
			final String[] stringArray) {
		this(var, ncfile, stringArray, null);
	}

	/**
	 * Builds an object with text strings and a modifier.
	 *
	 * @param var
	 *           the variable
	 * @param ncfile
	 *           the netCDF file
	 * @param stringArray
	 *           an array of text strings
     * @param mod
     *           the modifier
	 */
	public SGTextVariable(final Variable var, final SGNetCDFFile ncfile, 
			final String[] stringArray, final SGIStringModifier mod) {
		super(var, ncfile);

		if (stringArray == null) {
			throw new IllegalArgumentException("stringArray == null");
		}

		// set to the attribute
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
        this.mStringArray = stringArrayNew;
        this.mModifier = mod;
	}

	/**
	 * Returns the modifier.
	 * 
	 * @return the modifier
	 */
	@Override
	public SGIStringModifier getModifier() {
		return this.mModifier;
	}
	
	/**
	 * Returns the array of text strings.
	 * 
	 * @return the array of text strings
	 */
	@Override
	public String[] getStringArray() {
		return SGUtility.copyStringArray(this.mStringArray);
	}

	/**
	 * Returns the value type.
	 * 
	 * @return the value type
	 */
    @Override
	public String getValueType() {
		return SGIDataColumnTypeConstants.VALUE_TYPE_TEXT;
	}

}
