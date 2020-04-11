package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

/**
 * The buffer for multiple scalar-XY type data.
 *
 */
public class SGSXYMultipleDataBuffer extends SGOneDimensionalDataBuffer {

	// The array of X values.
	private double[][] mXValues = null;

	// The array of Y values.
	private double[][] mYValues = null;

	// The flag whether Y values are multiple.
	// If this flag is equal to null, both of x and y values are multiple.
	private Boolean mYValuesMultipleFlag = true;
	
	// The flag whether Y values are the holder of error bars and tick labels.
	private Boolean mYHolderFlag = null;
	
	// The array of lower error values.
	private double[][] mLowerErrorValues = null;
	
	// The flag whether values are of the date type.
	// True for X values, false for Y values or null:
	private Boolean mDateFlag = null;

	// The array of date objects.
	private SGDate[] mDateArray = null;

	// The array of upper error values.
	private double[][] mUpperErrorValues = null;
	
	// The array of flags whether child SXY data buffer has the same error values 
	// for lower and upper.
	private Boolean[] mSameErrorVariableFlags = null;

	// The array of tick labels.
	private String[][] mTickLabels = null;

	SGSXYMultipleDataBuffer() {
		super();
	}

	public SGSXYMultipleDataBuffer(double[] singleValues, double[][] multipleValues,
			final boolean yValuesMultipleFlag) {
		this();
		if (singleValues == null || multipleValues == null) {
			throw new IllegalArgumentException("singleValues == null || multipleValues == null");
		}
		for (int ii = 0; ii < multipleValues.length; ii++) {
			if (multipleValues[ii] == null) {
				throw new IllegalArgumentException("multipleValues[" + ii + "] == null");
			}
			if (singleValues.length != multipleValues[ii].length) {
				throw new IllegalArgumentException("singleValues.length != multipleValues[" 
						+ ii + "].length");
			}
		}
		this.mLength = singleValues.length;
		double[][] sValues = new double[multipleValues.length][];
		double[] sValueCopy = SGUtility.copyDoubleArray(singleValues);
		for (int ii = 0; ii < multipleValues.length; ii++) {
			sValues[ii] = sValueCopy;
		}
		double[][] mValues = SGUtility.copyDoubleArray(multipleValues);
		this.mXValues = yValuesMultipleFlag ? sValues : mValues;
		this.mYValues = yValuesMultipleFlag ? mValues : sValues;
		this.mYValuesMultipleFlag = yValuesMultipleFlag;
		this.mDateFlag = null;
		this.mDateArray = null;
		this.mYHolderFlag = yValuesMultipleFlag;
	}

	public SGSXYMultipleDataBuffer(double[][] xValues, double[][] yValues) {
		this();
		if (xValues == null || yValues == null) {
			throw new IllegalArgumentException("xValues == null || yValues == null");
		}
		if (xValues.length == 0) {
			throw new IllegalArgumentException("xValues.length == 0");
		}
		if (yValues.length == 0) {
			throw new IllegalArgumentException("yValues.length == 0");
		}
		int length = -1;
		for (int ii = 0; ii < xValues.length; ii++) {
			if (xValues[ii] == null) {
				throw new IllegalArgumentException("xValues[" + ii + "] == null");
			}
			if (length == -1) {
				length = xValues[ii].length;
			} else {
				if (length != xValues[ii].length) {
					throw new IllegalArgumentException("length != xValues[" 
							+ ii + "].length: " + length + ", " + xValues[ii].length);
				}
			}
		}
		for (int ii = 0; ii < yValues.length; ii++) {
			if (yValues[ii] == null) {
				throw new IllegalArgumentException("yValues[" + ii + "] == null");
			}
			if (length != yValues[ii].length) {
				throw new IllegalArgumentException("length != yValues[" 
						+ ii + "].length: " + length + ", " + yValues[ii].length);
			}
		}
		this.mLength = length;
		this.mXValues = this.copyValues(xValues);
		this.mYValues = this.copyValues(yValues);
		this.mYValuesMultipleFlag = null;
		this.mYHolderFlag = null;
		this.mDateFlag = null;
		this.mDateArray = null;
	}

	public SGSXYMultipleDataBuffer(double[] singleValues, double[][] multipleValues,
			final boolean yValuesMultipleFlag, 
			final Boolean dateFlag, SGDate[] dateArray,
			double[][] leValues, double[][] ueValues, 
			Boolean[] sameErrorVariableFlags, String[][] tickLabels) {
		this(singleValues, multipleValues, yValuesMultipleFlag);
		
		if (!(leValues != null && ueValues != null) && !(leValues == null && ueValues == null)) {
			throw new IllegalArgumentException(
					"!(leValues != null && ueValues != null) && !(leValues == null && ueValues == null)");
		}
		if (leValues != null && ueValues != null) {
			if (leValues.length != multipleValues.length) {
				throw new IllegalArgumentException("leValues.length != multipleValues.length");
			}
			if (leValues.length != ueValues.length) {
				throw new IllegalArgumentException("leValues.length != ueValues.length");
			}
			if (sameErrorVariableFlags == null) {
				throw new IllegalArgumentException("sameErrorVariableFlags == null");
			}
			if (leValues.length != sameErrorVariableFlags.length) {
				throw new IllegalArgumentException("leValues.length != sameErrorVariableFlags.length");
			}
			for (int ii = 0; ii < leValues.length; ii++) {
				if (leValues[ii] != null) {
					if (leValues[ii].length != this.mLength) {
						throw new IllegalArgumentException("leValues[" + ii + "].length != this.mLength");
					}
				}
			}
			this.mLowerErrorValues = SGUtility.copyDoubleArray(leValues);
			for (int ii = 0; ii < ueValues.length; ii++) {
				if (ueValues[ii] != null) {
					if (ueValues[ii].length != this.mLength) {
						throw new IllegalArgumentException("ueValues[" + ii + "].length != this.mLength");
					}
				}
			}
			this.mUpperErrorValues = SGUtility.copyDoubleArray(ueValues);
			for (int ii = 0; ii < multipleValues.length; ii++) {
				if (!(leValues[ii] != null && ueValues[ii] != null) 
						&& !(leValues[ii] == null && ueValues[ii] == null)) {
					throw new IllegalArgumentException("!(leValues[" + ii + "] != null && ueValues[" + ii + "] != null)" + 
							"&& !(leValues[" + ii + "] == null && ueValues[" + ii + "] == null)");
				}
			}
			this.mSameErrorVariableFlags = sameErrorVariableFlags.clone();
		}
		if (tickLabels != null) {
			if (tickLabels.length != multipleValues.length) {
				throw new IllegalArgumentException("tickLabels.length != multipleValues.length");
			}
			this.mTickLabels = SGUtility.copyStringArray(tickLabels);
		}
		
		this.mDateFlag = dateFlag;
		this.mDateArray = (dateArray != null) ? dateArray.clone() : null;
	}

	public SGSXYMultipleDataBuffer(double[][] xValues, double[][] yValues,
			final Boolean dateFlag, SGDate[] dateArray, final Boolean yHolderFlag,
			double[][] leValues, double[][] ueValues, 
			Boolean[] sameErrorVariableFlags, String[][] tickLabels) {
		this(xValues, yValues);
		
		if (!(leValues != null && ueValues != null) && !(leValues == null && ueValues == null)) {
			throw new IllegalArgumentException(
					"!(leValues != null && ueValues != null) && !(leValues == null && ueValues == null)");
		}
		if (((leValues != null && ueValues != null) || (tickLabels != null)) 
				&& yHolderFlag == null) {
			throw new IllegalArgumentException("yHolderFlag == null");
		}
		double[][] holderValues = null;
		if (yHolderFlag != null) {
			holderValues = yHolderFlag ? yValues : xValues;
		}
		if (leValues != null && ueValues != null) {
			if (leValues.length != holderValues.length) {
				throw new IllegalArgumentException("leValues.length != multipleValues.length");
			}
			if (leValues.length != ueValues.length) {
				throw new IllegalArgumentException("leValues.length != ueValues.length");
			}
			if (sameErrorVariableFlags == null) {
				throw new IllegalArgumentException("sameErrorVariableFlags == null");
			}
			if (leValues.length != sameErrorVariableFlags.length) {
				throw new IllegalArgumentException("leValues.length != sameErrorVariableFlags.length");
			}
			for (int ii = 0; ii < leValues.length; ii++) {
				if (leValues[ii] != null) {
					if (leValues[ii].length != this.mLength) {
						throw new IllegalArgumentException("leValues[" + ii + "].length != this.mLength");
					}
				}
			}
			for (int ii = 0; ii < ueValues.length; ii++) {
				if (ueValues[ii] != null) {
					if (ueValues[ii].length != this.mLength) {
						throw new IllegalArgumentException("ueValues[" + ii + "].length != this.mLength");
					}
				}
			}
			for (int ii = 0; ii < holderValues.length; ii++) {
				if (!(leValues[ii] != null && ueValues[ii] != null) 
						&& !(leValues[ii] == null && ueValues[ii] == null)) {
					throw new IllegalArgumentException("!(leValues[" + ii + "] != null && ueValues[" + ii + "] != null)" + 
							"&& !(leValues[" + ii + "] == null && ueValues[" + ii + "] == null)");
				}
			}
			this.mLowerErrorValues = this.copyValues(leValues);
			this.mUpperErrorValues = this.copyValues(ueValues);
			this.mSameErrorVariableFlags = sameErrorVariableFlags.clone();
		}
		if (tickLabels != null) {
			if (tickLabels.length != holderValues.length) {
				throw new IllegalArgumentException("tickLabels.length != multipleValues.length");
			}
			this.mTickLabels = this.copyValues(tickLabels);
		}
		
		this.mYHolderFlag = yHolderFlag;
		this.mDateFlag = dateFlag;
		this.mDateArray = (dateArray != null) ? dateArray.clone() : null;
	}

//	public double[] getSingleValues() {
//		double[] array = this.getSingleArray();
//		return (array != null) ? SGUtility.copyDoubleArray(array) : null;
//	}
//	
//	private double[] getSingleArray() {
//		if (this.hasOneSidedMultipleValues()) {
//			double[][] values = this.mYValuesMultipleFlag.booleanValue() ? this.mXValues : this.mYValues;
//			return values[0];
//		} else {
//			return null;
//		}
//	}
//	
//	public double[][] getMultipleValues() {
//		double[][] array = this.getMultipleArray();
//		return (array != null) ? SGUtility.copyDoubleArray(array) : null;
//	}
//
//	private double[][] getMultipleArray() {
//		if (this.hasOneSidedMultipleValues()) {
//			double[][] values = this.mYValuesMultipleFlag.booleanValue() ? this.mYValues : this.mXValues;
//			return values;
//		} else {
//			return null;
//		}
//	}

	private double[][] copyValues(double[][] values) {
		return SGUtility.copyDoubleArray(values);
	}

	private String[][] copyValues(String[][] values) {
		return SGUtility.copyStringArray(values);
	}

	public double[][] getXValues() {
		return SGUtility.copyDoubleArray(this.mXValues);
	}
	
	public double[][] getYValues() {
		return SGUtility.copyDoubleArray(this.mYValues);
	}

	public double[][] getLowerErrorValues() {
		if (this.mLowerErrorValues == null) {
			return null;
		}
		return this.copyValues(this.mLowerErrorValues);
	}

	public double[][] getUpperErrorValues() {
		if (this.mUpperErrorValues == null) {
			return null;
		}
		return this.copyValues(this.mUpperErrorValues);
	}

	public String[][] getTickLabels() {
		if (this.mTickLabels == null) {
			return null;
		}
		return this.copyValues(this.mTickLabels);
	}

	public Boolean getYValuesMultipleFlag() {
		return this.mYValuesMultipleFlag;
	}

    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
	@Override
    public Object clone() {
		SGSXYMultipleDataBuffer obj = (SGSXYMultipleDataBuffer) super.clone();
		obj.mXValues = this.copyValues(this.mXValues);
		obj.mYValues = this.copyValues(this.mYValues);
		obj.mLowerErrorValues = this.getLowerErrorValues();
		obj.mUpperErrorValues = this.getUpperErrorValues();
		obj.mTickLabels = this.getTickLabels();
		if (this.mDateArray != null) {
			obj.mDateArray = this.mDateArray.clone();
		}
		if (this.mSameErrorVariableFlags != null) {
			obj.mSameErrorVariableFlags = this.mSameErrorVariableFlags.clone();
		}
		return obj;
	}

	public SGSXYDataBuffer[] getSXYDataBufferArray() {
		final int multiplicity = this.getMultiplicity();
		SGSXYDataBuffer[] buffers = new SGSXYDataBuffer[multiplicity];
		for (int ii = 0; ii < buffers.length; ii++) {
			Boolean sameErrorVariableFlag = (this.mSameErrorVariableFlags != null) 
					? this.mSameErrorVariableFlags[ii] : null;
			double[] xValues = this.mXValues[ii];
			double[] yValues = this.mYValues[ii];
			double[] lowerErrorValues = null;
			if (this.mLowerErrorValues != null) {
				lowerErrorValues = this.mLowerErrorValues[ii];
			}
			double[] upperErrorValues = null;
			if (this.mUpperErrorValues != null) {
				upperErrorValues = this.mUpperErrorValues[ii];
			}
			String[] tickLabels = null;
			if (this.mTickLabels != null) {
				tickLabels = this.mTickLabels[ii];
			}
			SGDate[] dateValues = null;
			if (this.mDateArray != null) {
				dateValues = this.mDateArray.clone();
			}
			buffers[ii] = new SGSXYDataBuffer(xValues, yValues, this.mDateFlag, 
					dateValues, this.mYHolderFlag, lowerErrorValues, upperErrorValues, 
					sameErrorVariableFlag, tickLabels);
		}
		return buffers;
	}

	/**
	 * Returns the data type.
	 * 
	 * @return the data type
	 */
	@Override
	public String getDataType() {
		return SGDataTypeConstants.SXY_MULTIPLE_VIRTUAL_MDARRAY_DATA;
	}

	/**
	 * Returns the multiplicity.
	 * 
	 * @return the multiplicity
	 */
	public int getMultiplicity() {
		return this.mYValues.length;
	}
	
	/**
	 * Picks up the multiple values.
	 * 
	 * @param indices
	 *           indices to pick up
	 * @return picked up result
	 */
	public SGSXYMultipleDataBuffer pickUp(final int[] indices) {
    	if (indices == null) {
    		throw new IllegalArgumentException("indices == null");
    	}
    	if (indices.length == 0) {
    		throw new IllegalArgumentException("indices.length == 0");
    	}
		final int num = this.getMultiplicity();
		for (int ii = 0; ii < indices.length; ii++) {
			if (indices[ii] < 0 || indices[ii] >= num) {
	    		throw new IllegalArgumentException("Index out of bounds: indices[" + ii
	    				+ "] == " + indices[ii]);
			}
		}
		if (!SGUtility.checkOverlapping(indices)) {
    		throw new IllegalArgumentException("Indices overlapping");
		}
		double[][] xValues = new double[indices.length][];
		double[][] yValues = new double[indices.length][];
		for (int ii = 0; ii < indices.length; ii++) {
			xValues[ii] = this.mXValues[indices[ii]];
			yValues[ii] = this.mYValues[indices[ii]];
		}
		double[][] lowerErrorValues = (this.mLowerErrorValues != null) ? new double[indices.length][] : null;
		double[][] upperErrorValues = (this.mUpperErrorValues != null) ? new double[indices.length][] : null;
		Boolean[] sameErrorVariableFlags = (this.mSameErrorVariableFlags != null) ? new Boolean[indices.length] : null;
		String[][] tickLabels = (this.mTickLabels != null) ? new String[indices.length][] : null;
		for (int ii = 0; ii < indices.length; ii++) {
			if (this.mLowerErrorValues != null) {
				lowerErrorValues[ii] = this.mLowerErrorValues[indices[ii]];
			}
			if (this.mUpperErrorValues != null) {
				upperErrorValues[ii] = this.mUpperErrorValues[indices[ii]];
			}
			if (this.mSameErrorVariableFlags != null) {
				sameErrorVariableFlags[ii] = this.mSameErrorVariableFlags[indices[ii]];
			}
			if (this.mTickLabels != null) {
				tickLabels[ii] = this.mTickLabels[indices[ii]];
			}
		}
		SGDate[] dateArray = (this.mDateArray != null) ? this.mDateArray.clone() : null;
		final SGSXYMultipleDataBuffer ret = new SGSXYMultipleDataBuffer(xValues, yValues, 
				this.mDateFlag, dateArray, this.mYHolderFlag, 
				lowerErrorValues, upperErrorValues, sameErrorVariableFlags, tickLabels);
		return ret;
	}

    public SGDate[] getDateArray() {
    	return (this.mDateArray != null) ? this.mDateArray.clone() : null;
    }

    public Boolean getDateFlag() {
    	return this.mDateFlag;
    }
    
    public boolean isYValuesHolder() {
    	return this.mYHolderFlag;
    }
    
	public Boolean[] hasSameErrorVariable() {
		return (this.mSameErrorVariableFlags != null) ? this.mSameErrorVariableFlags.clone() : null;
	}
	
//	public boolean hasOneSidedMultipleValues() {
//		return (this.mYValuesMultipleFlag != null);
//	}
}
