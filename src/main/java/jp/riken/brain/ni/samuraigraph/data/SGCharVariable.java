package jp.riken.brain.ni.samuraigraph.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGIStringModifier;

import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;

/**
 * The base class of a NetCDF variable of char values.
 *
 */
public abstract class SGCharVariable extends SGNetCDFVariable {

	public SGCharVariable(final Variable var, final SGNetCDFFile ncfile) {
		super(var, ncfile);
	}

	/**
	 * Returns the list of dimension.
	 * 
	 * @return the list of dimensions
	 */
    @Override
	public List<Dimension> getDimensions() {
		List<Dimension> dimList = new ArrayList<Dimension>(this.mVariable.getDimensions());
		// remove the dimension of length
		dimList.remove(this.getLengthDimension());
		return dimList;
	}

	/**
	 * Returns the dimension of given index.
	 * 
	 * @param index
	 *           the index of dimension
     * @return the dimension of given index
	 */
    @Override
	public Dimension getDimension(final int index) {
		if (index > 0) {
			return null;
		}
		return this.mVariable.getDimension(0);
	}

	/**
	 * Returns the dimension that has the length of the maximum length of
	 * all text strings.
	 * 
	 * @return the second dimension
	 */
	public Dimension getLengthDimension() {
		return this.mVariable.getDimension(1);
	}
	
    @Override
	public Array read(int[] a, int[] b) throws InvalidRangeException, IOException {
		// always returns null
		return null;
	}

	/**
	 * Returns the array of text strings.
	 * 
	 * @return the array of text strings
	 */
	public abstract String[] getStringArray();

	/**
	 * Returns the modifier.
	 * 
	 * @return the modifier
	 */
	public abstract SGIStringModifier getModifier();

}
