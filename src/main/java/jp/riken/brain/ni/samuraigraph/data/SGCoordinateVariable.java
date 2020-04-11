package jp.riken.brain.ni.samuraigraph.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;

/**
 * The wrapper class for the netCDF coordinate variable that does not exist
 * in the netCDF data.
 *
 */
public class SGCoordinateVariable extends SGNetCDFVariable {

	/**
	 * The dimension.
	 * 
	 */
	private Dimension mDimension = null;
	
	/**
	 * Builds an object with a given dimension.
	 *
	 * @param dim
	 *           the dimension
	 * @param ncfile
	 *           the netCDF file
	 */
	public SGCoordinateVariable(final Dimension dim, final SGNetCDFFile ncfile) {
		super(ncfile);
		if (dim == null) {
			throw new IllegalArgumentException("dim == null");
		}
		this.mDimension = dim;
	}

	/**
	 * Returns the dimension.
	 * 
	 * @return the dimension
	 */
	public Dimension getDimension() {
		return this.mDimension;
	}
	
	/**
	 * Overrode to return the name of dimension.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.mDimension.getName();
	}

	/**
	 * Returns the constant "undefined".
	 * 
	 * @return the name of this variable
	 */
	public String getNameInPriorityOrder() {
		// returns a constant text string
		return "undefined";
	}

	public int findDimensionIndex(final String name) {
		if (this.mDimension.getName().equals(name)) {
			return 0;
		} else {
			return -1;
		}
	}

	public List<Dimension> getDimensions() {
		List<Dimension> list = new ArrayList<Dimension>();
		list.add(this.mDimension);
		return list;
	}

	public Dimension getDimension(final int index) {
		if (index == 0) {
			return this.mDimension;
		} else {
			return null;
		}
	}
	
	public boolean isCoordinateVariable() {
		// always returns true
		return true;
	}

	public Attribute findAttribute(final String name) {
		return null;
	}

	public String getUnitsString() {
		// always returns null
		return null;
	}

	public boolean isUnlimited() {
		return this.mDimension.isUnlimited();
	}

	public int getLength() {
		return this.mDimension.getLength();
	}
	
	public List<Attribute> getAttributes() {
		return new ArrayList<Attribute>();
	}

	public Array read(int[] a, int[] b) throws InvalidRangeException, IOException {
		// always returns null
		return null;
	}

	public String toString() {
		if (this.mVariable == null) {
			if (this.mDimension == null) {
				return "null";
			} else {
				return this.mDimension.toString();
			}
		} else {
			return this.mVariable.toString();
		}
	}

	/**
	 * Overrode to return the dimension name.
	 * 
	 * @return the name to identify this variable
	 */
	public String getIdentifiableNameInPriorityOrder() {
		return this.mDimension.getName();
	}

	/**
	 * Returns the array of numbers.
	 * 
	 * @return the array of numbers
	 */
	@Override
	public double[] getNumberArray() {
    	final int len = this.getLength();
    	if (len == 0) {
    		// if the length of a coordinate variable is zero, returns null
    		return null;
    	}
    	double[] values = new double[len];
    	for (int ii = 0; ii < len; ii++) {
    		values[ii] = ii;
    	}
    	return values;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SGCoordinateVariable)) {
			return false;
		}
		SGCoordinateVariable var = (SGCoordinateVariable) obj;
		return var.mDimension.equals(this.mDimension);
	}

}
