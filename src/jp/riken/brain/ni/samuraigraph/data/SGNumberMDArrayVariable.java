package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;


/**
 * The class of variable of multidimensional number array.
 *
 */
public abstract class SGNumberMDArrayVariable extends SGMDArrayVariable {
	// To get the value of a MLArray in a MATLAB file, the method com.jmatio.types.MLNumericArray.get(int index)
	// is used. For a d-dimensional MLNumericArray object, the argument of this method 'index' for a set of 
	// dimension indices {n[0], n[1], n[2], ..., n[d - 1]} is given by
	//   index = n[0] + s[0] * (n[1] + s[1] * (n[2] + s[2] * (n[3] + s[3] * (...))))
	//         = n[0] + s[0] * n[1] + (s[0] * s[1]) * n[2] + (s[0] * s[1] * s[2]) * n[3]
	//             + ... + (s[0] * s[1] * ... * s[d - 2]) * n[d - 1],
	// where {s[0], s[1], s[2], ..., s[d - 1]] are the size of each dimension.
	// Factors for each term of dimension index are calculated and stored in the attribute of this class, mFactors.
	// In the subclass of this class, SGVirtualMDArrayVariable, array values are stored in the same ordering in an
	// attribute, mValues.
	
	/**
	 * The array of factors for each term of dimension indices.
	 */
	protected int[] mFactors = null;
	
	/**
	 * The array of numbers.
	 */
	protected INumberArray mNumberArray = null;

	/**
	 * The default constructor.
	 */
	public SGNumberMDArrayVariable() {
		super();
	}

	protected void initWithDimensions() {
		// initializes the origins
		int[] dims = this.getDimensions();
		this.mOrigins = new int[dims.length];
		for (int ii = 0; ii < this.mOrigins.length; ii++) {
			this.mOrigins[ii] = 0;
		}
		
		// calculates the factors
		final int[] factors = new int[dims.length];
		int temp = 1;
		for (int ii = 0; ii < factors.length; ii++) {
			factors[ii] = temp;
			temp *= dims[ii];
		}
		this.mFactors = factors;
	}

	protected int calcOffset(final int[] dims, final int dimensionIndex, final int[] origins) {
		return this.calcOffset(dims, new int[] { dimensionIndex }, origins);
	}
	
	protected int calcOffset(final int[] dims, final int[] dimensionIndices, final int[] origins) {
		int offset = 0;
		for (int ii = 0; ii < dims.length; ii++) {
			if (SGUtility.contains(dimensionIndices, ii)) {
				continue;
			}
			offset += this.mFactors[ii] * origins[ii];
		}
		return offset;
	}
	
    /**
     * Clones this data object.
     * 
     * @return copy of this data object
     */
	@Override
    public Object clone() {
		SGNumberMDArrayVariable var = (SGNumberMDArrayVariable) super.clone();
    	var.mFactors = this.mFactors.clone();
    	return var;
    }

	/**
	 * Returns true if this variable has only numeric data.
	 * 
	 * @return true if this variable has only numeric data
	 */
	@Override
	public boolean isNumberVariable() {
		// always returns true
		return true;
	}
	
	/**
	 * Returns the value type.
	 * 
	 * @return the value type
	 */
	@Override
	public String getValueType() {
		return SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER;
	}

	/**
	 * Returns a double value for given dimension index and array index at given origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param arrayIndex
	 *           array index
	 * @param origins
	 *           the origins
	 * @return a double value
	 */
	@Override
	public double getDoubleValue(final int dimensionIndex, final int arrayIndex,
			final int[] origins) {
		final int[] dims = this.mNumberArray.getDimensions();
		if (dimensionIndex < 0 || dimensionIndex >= dims.length) {
			throw new IllegalArgumentException("Index out of bounds: " + dimensionIndex);
		}
		
		// get double value
		final int factor = this.mFactors[dimensionIndex];
		final int offset = this.calcOffset(dims, dimensionIndex, origins);
		final int index = factor * arrayIndex + offset;
		return this.mNumberArray.get(index);
	}
	
	@Override
	public double getDoubleValue(final int index) {
		return this.mNumberArray.get(index);
	}

	/**
	 * Returns the array of double values for given dimension index with given stride and origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param stride
	 *           stride of the array
	 * @param origins
	 *           the origins
	 * @return the array of double values
	 */
	@Override
	public double[] getDoubleArray(final int dimensionIndex, final SGIntegerSeriesSet stride,
			final int[] origins) {
		final int[] dims = this.mNumberArray.getDimensions();
		if (dimensionIndex < 0 || dimensionIndex >= dims.length) {
			throw new IllegalArgumentException("Index out of bounds: " + dimensionIndex);
		}
		
		// get double array
		final int factor = this.mFactors[dimensionIndex];
		final int offset = this.calcOffset(dims, dimensionIndex, origins);
		final int[] indices;
		if (stride != null) {
			indices = stride.getNumbers();
		} else {
			final int len = dims[dimensionIndex];
			indices = new int[len];
			for (int ii = 0; ii < indices.length; ii++) {
				indices[ii] = ii;
			}
		}
		double[] array = new double[indices.length];
		for (int ii = 0; ii < array.length; ii++) {
			final int index = factor * indices[ii] + offset;
			array[ii] = this.mNumberArray.get(index);
		}
		return array;
	}
	
	/**
	 * Returns the array of double values for given dimension indices of x and y values
	 * with given origins.
	 * 
	 * @param xIndex
	 *           dimension index for x-values
	 * @param yIndex
	 *           dimension index for y-values
	 * @param origins
	 *           the origins
	 * @return the array of double values
	 */
	@Override
	public double[] getDoubleArray(final int xIndex, final int yIndex, final int origins[]) {
		int[] dims = this.getDimensions();
		if (xIndex < 0 || xIndex >= dims.length) {
			throw new IllegalArgumentException("Index out of bounds: " + xIndex);
		}
		if (yIndex < 0 || yIndex >= dims.length) {
			throw new IllegalArgumentException("Index out of bounds: " + yIndex);
		}
		if (xIndex == yIndex) {
			throw new IllegalArgumentException("Two indices are equal: " + xIndex);
		}

		// get multidimensional array
		final int[] indices = origins.clone();
		final int xFactor = this.mFactors[xIndex];
		final int yFactor = this.mFactors[yIndex];
		final int offset = this.calcOffset(dims, new int[] { xIndex, yIndex }, origins);
		final int xSize = dims[xIndex];
		final int ySize = dims[yIndex];
		double[] array = new double[xSize * ySize];
		for (int yy = 0; yy < ySize; yy++) {
			indices[xIndex] = yy;
			final int offsetNew = yFactor * yy + offset;
			for (int xx = 0; xx < xSize; xx++) {
				indices[xIndex] = xx;
				final int index = xFactor * xx + offsetNew;
				array[yy * xSize + xx] = this.mNumberArray.get(index);
			}
		}
		return array;
	}

	/**
	 * Returns the array of double values for given dimension indices with given stride
	 * and given origins.
	 * 
	 * @param xIndex
	 *           dimension index for x-values
	 * @param yIndex
	 *           dimension index for y-values
	 * @param xSeries
	 *           array indices for x-values
	 * @param ySeries
	 *           array indices for y-values
	 * @param origins
	 *           the origins
	 * @return the array of double values
	 */
	@Override
	public double[] getDoubleArray(final int xIndex, final int yIndex,
			final SGIntegerSeries xSeries, SGIntegerSeries ySeries, final int[] origins) {
		return this.getDoubleArray(xIndex, yIndex, new SGIntegerSeriesSet(xSeries), 
				new SGIntegerSeriesSet(ySeries), origins);
	}

	@Override
	public double[] getDoubleArray(final int xIndex, final int yIndex,
			final SGIntegerSeriesSet xStride, SGIntegerSeriesSet yStride, int[] origins) {
		int[] dims = this.getDimensions();
		if (xIndex < 0 || xIndex >= dims.length) {
			throw new IllegalArgumentException("Index out of bounds: " + xIndex);
		}
		if (yIndex < 0 || yIndex >= dims.length) {
			throw new IllegalArgumentException("Index out of bounds: " + yIndex);
		}
		if (xIndex == yIndex) {
			throw new IllegalArgumentException("Two indices are equal: " + xIndex);
		}

		// get multidimensional array
//		final int[] indices = origins.clone();
		final int xFactor = this.mFactors[xIndex];
		final int yFactor = this.mFactors[yIndex];
		final int offset = this.calcOffset(dims, new int[] { xIndex, yIndex },
				origins);
		int[] xIndices = xStride.getNumbers();
		int[] yIndices = yStride.getNumbers();
		final int xSize = xIndices.length;
		final int ySize = yIndices.length;
		double[] array = new double[xSize * ySize];
		for (int yy = 0; yy < ySize; yy++) {
//			indices[xIndex] = yIndices[yy];
			final int offsetNew = yFactor * yIndices[yy] + offset;
			for (int xx = 0; xx < xSize; xx++) {
//				indices[xIndex] = xIndices[xx];
				final int index = xFactor * xIndices[xx] + offsetNew;
				array[yy * xSize + xx] = this.mNumberArray.get(index);
			}
		}
		return array;
	}

	/**
	 * Returns an array of text strings with the current origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @return an array of text strings
	 */
	@Override
	public String[] getStringArray(final int dimensionIndex) {
		// always returns null
		return null;
	}

	/**
	 * Returns an array of text strings with given stride and the current origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param stride
	 *           stride of the array
	 * @return an array of text strings
	 */
	@Override
	public String[] getStringArray(final int dimensionIndex, final SGIntegerSeriesSet stride) {
		// always returns null
		return null;
	}

	/**
	 * Returns an array of text strings with given stride and given origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param stride
	 *           stride of the array
	 * @param origins
	 *           the origins
	 * @return an array of text strings
	 */
	@Override
	public String[] getStringArray(final int dimensionIndex, SGIntegerSeriesSet stride, 
			int[] origins) {
		// always returns null
		return null;
	}

	/**
	 * Returns a text string for given dimension index and array index at given origins.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param arrayIndex
	 *           array index
	 * @param origins
	 *           the origins
	 * @return a text string
	 */
	@Override
	public String getString(final int dimensionIndex, final int arrayIndex,
			final int[] origins) {
		// always returns null
		return null;
	}
	
	/**
	 * Returns a text string with given origins.
	 * 
	 * @param origins
	 *          the origins
	 * @return a double value
	 */
	@Override
	public String getString(final int[] origins) {
		// always returns null
		return null;
	}

	/**
	 * Returns a text string for given dimension index and array index.
	 * 
	 * @param dimensionIndex
	 *           dimension index
	 * @param arrayIndex
	 *           array index
	 * @return a double value
	 */
	@Override
	public String getString(final int dimensionIndex, final int arrayIndex) {
		// always returns null
		return null;
	}

	/**
	 * The interface for an array of numbers.
	 *
	 */
	protected static interface INumberArray {
		
		/**
		 * Returns the number at given array index.
		 * 
		 * @param index
		 *            the array index
		 * @return the number
		 */
		public Double get(final int index);

		/**
		 * Returns the array of dimensions.
		 * 
		 * @return the array of dimensions
		 */
		public int[] getDimensions();
	}
}
