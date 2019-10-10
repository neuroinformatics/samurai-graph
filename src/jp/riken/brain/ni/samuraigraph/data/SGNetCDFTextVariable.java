package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGIStringModifier;

import ucar.nc2.Dimension;
import ucar.nc2.Variable;

/**
 * A NetCDF variable class for multidimensional array of tick labels.
 *
 */
public class SGNetCDFTextVariable extends SGNetCDFVariable {
	
	/**
	 * The modifier.
	 */
	private SGIStringModifier mModifier = null;

	/**
	 * Builds an object.
	 *
	 * @param var
	 *           the variable
	 * @param ncfile
	 *           the netCDF file
     * @param mod
     *           the modifier
	 */
	public SGNetCDFTextVariable(final Variable var, final SGNetCDFFile ncfile) {
		this(var, ncfile, null);
	}

	/**
	 * Builds an object with a modifier.
	 *
	 * @param var
	 *           the variable
	 * @param ncfile
	 *           the netCDF file
     * @param mod
     *           the modifier
	 */
	public SGNetCDFTextVariable(final Variable var, final SGNetCDFFile ncfile,
			final SGIStringModifier mod) {
		super(var, ncfile);
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
	 * Returns the value type.
	 * 
	 * @return the value type
	 */
	public String getValueType() {
		return SGIDataColumnTypeConstants.VALUE_TYPE_TEXT;
	}

	/**
	 * Returns the dimension of given index.
	 * 
	 * @param index
	 *           the index of dimension
     * @return the dimension of given index
	 */
	public Dimension getDimension(final int index) {
		if (index > 0) {
			return null;
		}
		List<Dimension> dims = this.mVariable.getDimensions();
		return dims.get(index);
	}

	/**
	 * Returns the dimension that has the length of the maximum length of
	 * all text strings.
	 * 
	 * @return the second dimension
	 */
	public Dimension getLengthDimension() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.mVariable.getShortName());
		sb.append("_strlen");
		return this.mNetCDFFile.findDimension(sb.toString());
	}

	/**
	 * Returns the list of dimension.
	 * 
	 * @return the list of dimensions
	 */
	public List<Dimension> getDimensions() {
		List<Dimension> dimList = new ArrayList<Dimension>(this.mVariable.getDimensions());
		// remove the dimension of length
		dimList.remove(this.getLengthDimension());
		return dimList;
	}

}
