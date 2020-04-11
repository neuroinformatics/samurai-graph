package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import ncsa.hdf.hdf5lib.exceptions.HDF5DatatypeInterfaceException;
import ch.systemsx.cisd.base.mdarray.MDArray;
import ch.systemsx.cisd.base.mdarray.MDDoubleArray;
import ch.systemsx.cisd.base.mdarray.MDLongArray;
import ch.systemsx.cisd.hdf5.HDF5DataClass;
import ch.systemsx.cisd.hdf5.HDF5DataSetInformation;
import ch.systemsx.cisd.hdf5.HDF5DataTypeInformation;
import ch.systemsx.cisd.hdf5.IHDF5Reader;

/**
 * The wrapper class of HDF5 data set.
 *
 */
public class SGHDF5Variable extends SGMDArrayVariable {

	private HDF5DataSetInformation mDataSetInfo = null;
	
	private List<SGAttribute> mAttrList = null;
	
	/**
	 * Builds an variable.
	 *
	 * @param file
	 *           the file of HDF5 data
	 * @param fqName
	 *           the fully qualified name of a data set
	 */
	public SGHDF5Variable(final SGHDF5File file, final String fqName) {
		super();
		if (file == null) {
			throw new IllegalArgumentException("file == null");
		}
		if (fqName == null) {
			throw new IllegalArgumentException("fqName == null");
		}
		this.mFile = file;
		this.mName = fqName;

		// initializes the attributes
		IHDF5Reader reader = this.getReader();
		this.mDataSetInfo = reader.getDataSetInformation(this.mName);
		List<String> attrNameList = reader.getAttributeNames(this.mName);
		this.mAttrList = SGDataUtility.findHDF5Attributes(reader, this.mName, attrNameList);
		
		// initializes the origins
		int[] dims = this.getDimensions();
		this.mOrigins = new int[dims.length];
		for (int ii = 0; ii < this.mOrigins.length; ii++) {
			this.mOrigins[ii] = 0;
		}
	}

	private SGHDF5File getHDF5File() {
		return (SGHDF5File) this.getFile();
	}
	
	/**
	 * Returns the reader.
	 * 
	 * @return the reader
	 */
	public IHDF5Reader getReader() {
		return this.getHDF5File().getReader();
	}

	/**
	 * Returns the data set information.
	 * 
	 * @return the data set information
	 */
	public HDF5DataSetInformation getDataSetInformation() {
		return this.mDataSetInfo;
	}

	/**
	 * Returns the data type information.
	 * 
	 * @return the data type information
	 */
	public HDF5DataTypeInformation getDataTypeInformation() {
		return this.getDataSetInformation().getTypeInformation();
	}
	
	/**
	 * Returns the data class.
	 * 
	 * @return the data class
	 */
	public HDF5DataClass getDataClass() {
		return this.getDataTypeInformation().getDataClass();
	}
	
	/**
	 * Returns the rank.
	 * 
	 * @return the rank
	 */
	public int getRank() {
		return this.getDataSetInformation().getRank();
	}
	
	/**
	 * Returns the dimensions.
	 * 
	 * @return the dimensions
	 */
	@Override
	public int[] getDimensions() {
		long[] dims = this.getDataSetInformation().getDimensions();
		int[] ret = new int[dims.length];
		for (int ii = 0; ii < ret.length; ii++) {
			ret[ii] = (int) dims[ii];
		}
		return ret;
	}
	
	/**
	 * Returns the multidimensional array of double values.
	 * 
	 * @return the multidimensional array of double values
	 */
	public MDDoubleArray readDoubleMDArray() {
		return this.getReader().readDoubleMDArray(this.mName);
	}

	/**
	 * Returns the multidimensional array of string objects.
	 * 
	 * @return the multidimensional array of string objects
	 */
	public MDArray<String> readStringMDArray() {
		return this.getReader().readStringMDArray(this.mName);
	}

	private String getString(MDArray<String> mdArray, final int dimensionIndex, 
			final int arrayIndex, final int[] origins) {
		if (dimensionIndex < 0 || dimensionIndex >= origins.length) {
			throw new IllegalArgumentException("Index out of bounds: " + dimensionIndex);
		}
		origins[dimensionIndex] = arrayIndex;
		final String value = mdArray.get(origins);
		return SGDataUtility.decodeString(value);
	}

    /**
     * Returns the list of attributes.
     * 
     * @return the list of attributes
     */
	@Override
	public List<SGAttribute> getAttributes() {
		return new ArrayList<SGAttribute>(this.mAttrList);
	}
	
	/**
	 * Returns true if the data file and the variable name is equal.
	 * 
	 * @param obj
	 *          an object to be compared
	 * @return true if the data file and the variable name is equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof SGHDF5Variable)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns true if this variable has only numeric data.
	 * 
	 * @return true if this variable has only numeric data
	 */
	@Override
	public boolean isNumberVariable() {
		return SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER.equals(this.getValueType());
	}

	/**
	 * Returns the value type.
	 * 
	 * @return the value type
	 */
	@Override
	public String getValueType() {
		HDF5DataClass dClass = this.getDataClass();
		if (HDF5DataClass.FLOAT.equals(dClass) || HDF5DataClass.INTEGER.equals(dClass)) {
			return SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER;
		} else if (HDF5DataClass.STRING.equals(dClass)) {
			return SGIDataColumnTypeConstants.VALUE_TYPE_TEXT;
		} else {
			throw new Error("Unsupported data class: " + dClass);
		}
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
		int[] dims = this.getDimensions();
		if (dimensionIndex < 0 || dimensionIndex >= dims.length) {
			throw new IllegalArgumentException("Index out of bounds: " + dimensionIndex);
		}
		MDDoubleArray mdArray = this.readDoubleMDArray();
		return this.getDoubleValue(mdArray, dimensionIndex, arrayIndex,
				origins.clone());
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
	public double[] getDoubleArray(final int dimensionIndex, final SGIntegerSeriesSet stride,
			final int[] origins) {
		int[] dims = this.getDimensions();
		if (dimensionIndex < 0 || dimensionIndex >= dims.length) {
			throw new IllegalArgumentException("Index out of bounds: " + dimensionIndex);
		}
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
		double[] array = null;
		try {
			// get multidimensional array
			MDDoubleArray mdArray = this.readDoubleMDArray();
			array = new double[indices.length];
			for (int ii = 0; ii < array.length; ii++) {
				array[ii] = this.getDoubleValue(mdArray, dimensionIndex, indices[ii],
						origins.clone());
			}
		} catch (HDF5DatatypeInterfaceException e) {
			try {
				if (HDF5DataClass.INTEGER.equals(this.getDataClass())) {
					// read data as long array
					MDLongArray mdArray = this.getReader().readLongMDArray(this.mName);
					array = new double[indices.length];
					for (int ii = 0; ii < array.length; ii++) {
						array[ii] = (double) this.getLongValue(mdArray, dimensionIndex, 
								indices[ii], origins.clone());
					}
				}
			} catch (HDF5DatatypeInterfaceException e2) {
				// failed to read data
				throw e2;
			}
		}
		return array;
	}

	private double getDoubleValue(MDDoubleArray mdArray, final int dimensionIndex, 
			final int arrayIndex, final int[] origins) {
		if (dimensionIndex < 0 || dimensionIndex >= origins.length) {
			throw new IllegalArgumentException("Index out of bounds: " + dimensionIndex);
		}
		origins[dimensionIndex] = arrayIndex;
		final double value = mdArray.get(origins);
		return value;
	}

	private long getLongValue(MDLongArray mdArray, final int dimensionIndex, 
			final int arrayIndex, final int[] origins) {
		if (dimensionIndex < 0 || dimensionIndex >= origins.length) {
			throw new IllegalArgumentException("Index out of bounds: " + dimensionIndex);
		}
		origins[dimensionIndex] = arrayIndex;
		final long value = mdArray.get(origins);
		return value;
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
		MDDoubleArray mdArray = this.readDoubleMDArray();
		final int[] indices = origins.clone();
		final int xSize = dims[xIndex];
		final int ySize = dims[yIndex];
		double[] array = new double[xSize * ySize];
		for (int yy = 0; yy < ySize; yy++) {
			indices[yIndex] = yy;
			for (int xx = 0; xx < xSize; xx++) {
				indices[xIndex] = xx;
				array[yy * xSize + xx] = mdArray.get(indices);
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

		MDDoubleArray mdArray = this.readDoubleMDArray();
		final int[] indices = origins.clone();
		int[] xIndices = xStride.getNumbers();
		int[] yIndices = yStride.getNumbers();
		final int xSize = xIndices.length;
		final int ySize = yIndices.length;
		double[] array = new double[xSize * ySize];
		for (int yy = 0; yy < ySize; yy++) {
			indices[yIndex] = yIndices[yy];
			for (int xx = 0; xx < xSize; xx++) {
				indices[xIndex] = xIndices[xx];
				array[yy * xSize + xx] = mdArray.get(indices);
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
		return this.getStringArray(dimensionIndex, null);
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
		return this.getStringArray(dimensionIndex, stride, this.mOrigins);
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
		int[] dims = this.getDimensions();
		if (dimensionIndex < 0 || dimensionIndex >= dims.length) {
			throw new IllegalArgumentException("Index out of bounds: " + dimensionIndex);
		}
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
		HDF5DataClass dClass = this.getDataClass();
		String[] array = null;
		if (HDF5DataClass.STRING.equals(dClass)) {
			try {
				// get multidimensional array
				MDArray<String> mdArray = this.readStringMDArray();
				array = new String[indices.length];
				for (int ii = 0; ii < array.length; ii++) {
					array[ii] = this.getString(mdArray, dimensionIndex, indices[ii], 
							origins.clone());
				}
			} catch (HDF5DatatypeInterfaceException e) {
				throw e;
			}
		}
		return array;
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
		int[] dims = this.getDimensions();
		if (dimensionIndex < 0 || dimensionIndex >= dims.length) {
			throw new IllegalArgumentException("Index out of bounds: " + dimensionIndex);
		}
		MDArray<String> mdArray = this.readStringMDArray();
		return this.getString(mdArray, dimensionIndex, arrayIndex,
				origins.clone());
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
		if (origins == null || origins.length == 0) {
			throw new IllegalArgumentException("Invalid input values.");
		}
		return this.getString(0, origins[0], origins);
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
		return this.getString(dimensionIndex, arrayIndex, this.mOrigins);
	}

	protected static class HDF5DataType extends MDArrayDataType {
		protected HDF5DataType(HDF5DataClass dataClass) {
			super();
			VALUE_TYPE valueType;
        	if (HDF5DataClass.INTEGER.equals(dataClass)
        			|| dataClass == null) {
        		valueType = VALUE_TYPE.INTEGER;
        	} else if (HDF5DataClass.STRING.equals(dataClass)) {
        		valueType = VALUE_TYPE.STRING;
        	} else {
        		valueType = VALUE_TYPE.FLOAT;
        	}
        	this.valueType = valueType;
		}
	}

	@Override
	protected MDArrayDataType getDataType() {
		return new HDF5DataType(this.getDataClass());
	}

	@Override
	protected MDArrayDataType getExportFloatingNumberDataType() {
		return new HDF5DataType(HDF5DataClass.FLOAT);
	}

	@Override
	public double getDoubleValue(final int index) {
		MDDoubleArray mdArray = this.readDoubleMDArray();
		return mdArray.get(index);
	}

}
