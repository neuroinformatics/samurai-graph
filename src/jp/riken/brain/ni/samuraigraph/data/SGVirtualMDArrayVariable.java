package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.data.SGHDF5Variable.HDF5DataType;
import jp.riken.brain.ni.samuraigraph.data.SGNumberMDArrayVariable.INumberArray;
import ch.systemsx.cisd.hdf5.HDF5DataClass;

/**
 * The base class of virtual multidimensional array.
 *
 */
public abstract class SGVirtualMDArrayVariable extends SGNumberMDArrayVariable 
		implements INumberArray {
	
	// Array of dimensions.
	protected int[] mDimensions;
	
	// Array of values.
	protected double[] mValues;

	/**
	 * The default constructor.
	 * 
	 * @param name
	 *           the name
	 */
	public SGVirtualMDArrayVariable(final String name) {
		super();
		this.mName = name;
		
		// sets myself to an attribute
		this.mNumberArray = this;
	}

	/**
	 * Overrode to check the class type.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof SGVirtualMDArrayVariable)) {
			return false;
		}
		return true;
	}

    /**
     * Clones this data object.
     * 
     * @return copy of this data object
     */
	@Override
    public Object clone() {
		SGVirtualMDArrayVariable var = (SGVirtualMDArrayVariable) super.clone();
    	var.mDimensions = this.mDimensions.clone();
    	var.mValues = SGUtility.copyDoubleArray(this.mValues);
    	return var;
    }

	/**
	 * Returns the dimensions.
	 * 
	 * @return the dimensions
	 */
	@Override
	public int[] getDimensions() {
		return this.mDimensions.clone();
	}
	
    /**
     * Returns the list of attributes.
     * 
     * @return the list of attributes
     */
	@Override
	public List<SGAttribute> getAttributes() {
		// always returns an empty list
		return new ArrayList<SGAttribute>();
	}

	/**
	 * Returns the number at given array index.
	 * 
	 * @param index
	 *            the array index
	 * @return the number
	 */
	@Override
	public Double get(final int index) {
		return this.mValues[index];
	}
	
	@Override
	public MDArrayDataType getDataType() {
		return new VirtualMDArrayDataType();
	}

	/**
	 * The class of one dimensional number array.
	 *
	 */
	public static class D1 extends SGVirtualMDArrayVariable {
		
		/**
		 * Builds an object with given one dimensional array.
		 * 
		 * @param values
		 *           array of values
		 * @param name
		 *           the name
		 */
		public D1(final double[] values, final String name) {
			super(name);
			if (values == null) {
				throw new IllegalArgumentException("values == null");
			}
			this.mValues = SGUtility.copyDoubleArray(values);
			
			this.mDimensions = new int[] { values.length };
			this.initWithDimensions();
		}
	}
	
	/**
	 * The class of two dimensional number array.
	 *
	 */
	public static class D2 extends SGVirtualMDArrayVariable {
		
		/**
		 * Builds an object with given two dimensional array.
		 * 
		 * @param values
		 *           array of values
		 * @param name
		 *           the name
		 */
		public D2(final double[][] values, final String name) {
			super(name);
			if (values == null) {
				throw new IllegalArgumentException("values == null");
			}
			if (values.length == 0) {
				throw new IllegalArgumentException("values.length == 0");
			}
			int len = -1;
			for (int ii = 0; ii < values.length; ii++) {
				if (values[ii] == null) {
					throw new IllegalArgumentException("values[" + ii + "] == null");
				}
				if (ii == 0) {
					len = values[ii].length;
				} else {
					if (values[ii].length != len) {
						throw new IllegalArgumentException("Invalid array length.");
					}
				}
			}
			this.mValues = new double[values.length * len];
			int cnt = 0;
			for (int ii = 0; ii < len; ii++) {
				for (int jj = 0; jj < values.length; jj++) {
					this.mValues[cnt] = values[jj][ii];
					cnt++;
				}
			}
			
			this.mDimensions = new int[] { values.length, len };
			this.initWithDimensions();
		}
	}
	
	protected static class VirtualMDArrayDataType extends MDArrayDataType {
		protected VirtualMDArrayDataType() {
			super();
		}
	}

	@Override
	protected MDArrayDataType getExportFloatingNumberDataType() {
		return new HDF5DataType(HDF5DataClass.FLOAT);
	}

}
