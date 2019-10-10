package jp.riken.brain.ni.samuraigraph.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;

/**
 * The class of variable that has byte data.
 * 
 */
public class SGByteDataVariable extends SGNetCDFVariable {
	
	/**
	 * Builds an object with a given variable.
	 *
	 * @param var
	 *           the variable
	 * @param ncfile
	 *           the netCDF file
	 */
	public SGByteDataVariable(final Variable var, final SGNetCDFFile ncfile) {
		super(var, ncfile);
	}

	/**
	 * Returns the value type.
	 * 
	 * @return the value type
	 */
	public String getValueType() {
		return SGIDataColumnTypeConstants.VALUE_TYPE_BYTE_DATA;
	}

	/**
	 * Returns the list of dimension.
	 * 
	 * @return the list of dimensions
	 */
	public List<Dimension> getDimensions() {
		// always returns an empty list
		return new ArrayList<Dimension>();
	}

	/**
	 * Returns the dimension of given index.
	 * 
	 * @param index
	 *           the index of dimension
     * @return the dimension of given index
	 */
	public Dimension getDimension(final int index) {
		// always returns null
		return null;
	}

	public Array read(int[] a, int[] b) throws InvalidRangeException, IOException {
		// always returns null
		return null;
	}
	
}
