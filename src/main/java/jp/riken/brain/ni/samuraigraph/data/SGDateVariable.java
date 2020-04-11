package jp.riken.brain.ni.samuraigraph.data;

import java.text.ParseException;

import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGIStringModifier;

import ucar.nc2.Variable;

public class SGDateVariable extends SGCharVariable {
    
	/**
	 * A array of date objects
	 */
    private SGDate[] mDate = null;

    /**
     * Builds an object with a given variable.
     *
     * @param var
     *           the variable
	 * @param ncfile
	 *           the netCDF file
	 * @param stringArray
	 *           an array of text strings
     */
    public SGDateVariable(final Variable var, final SGNetCDFFile ncfile, 
    		final String[] stringArray) {
        super(var, ncfile);
        
		if (stringArray == null) {
			throw new IllegalArgumentException("stringArray == null");
		}
		
        try {
        	this.mDate = new SGDate[stringArray.length];
            for (int ii = 0; ii < stringArray.length; ii++) {
            	this.mDate[ii] = new SGDate(stringArray[ii].trim());
            }
        } catch (ParseException e) {
        	this.mDate = null;
        }
    }

	/**
	 * Returns the modifier.
	 * 
	 * @return the modifier
	 */
	@Override
	public SGIStringModifier getModifier() {
		// always returns null
		return null;
	}

	/**
	 * Returns the array of date objects.
	 * 
	 * @return the array of date objects
	 */
    public SGDate[] getDate() {
        if (this.mDate == null) {
            return null;
        }
    	return (SGDate[]) this.mDate.clone();
    }
    
	/**
	 * Returns the array of numbers.
	 * 
	 * @return the array of numbers
	 */
    @Override
    public double[] getNumberArray() {
        if (this.mDate == null) {
            return null;
        }
        return SGDataUtility.getDateValueArray(this.mDate);
    }
    
	/**
	 * Returns the array of text strings.
	 * 
	 * @return the array of text strings
	 */
	@Override
    public String[] getStringArray() {
        if (this.mDate==null) {
            return null;
        }
        String[] values = new String[this.mDate.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = this.mDate[i].toString();
        }
        return values;
    }

	/**
	 * Returns the value type.
	 * 
	 * @return the value type
	 */
    @Override
	public String getValueType() {
		return SGIDataColumnTypeConstants.VALUE_TYPE_DATE;
	}

}
