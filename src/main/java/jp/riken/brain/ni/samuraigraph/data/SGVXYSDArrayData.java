package jp.riken.brain.ni.samuraigraph.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;

import org.w3c.dom.Element;

import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;
import ch.systemsx.cisd.base.mdarray.MDDoubleArray;
import ch.systemsx.cisd.hdf5.IHDF5Writer;

/**
 * Two dimensional vector data.
 *
 */
public class SGVXYSDArrayData extends SGSDArrayData implements SGIVXYTypeData,
        SGIDataColumnTypeConstants, SGIDataPropertyKeyConstants {

    /**
     * The column index for x-coordinate values.
     */
    protected Integer mXCoordinateIndex = null;

    /**
     * The column index for y-coordinate values.
     */
    protected Integer mYCoordinateIndex = null;

    /**
     * The column index for the first component values.
     */
    protected Integer mFirstComponentIndex = null;

    /**
     * The column index for the second component values.
     */
    protected Integer mSecondComponentIndex = null;

    /**
     * A flag for polar mode.
     */
    private boolean mPolarFlag = false;

    /**
     * The default constructor.
     */
    public SGVXYSDArrayData() {
        super();
    }

	/**
	 * Build a data with given columns and indices.
	 *
	 * @param dataFile
	 *            the text data file
	 * @param obs
	 *            an observer for array data
	 * @param xCoordinateIndex
	 *            the index for x-coordinate values
	 * @param yCoordinateIndex
	 *            the index for y-coordinate values
	 * @param componentIndex1
	 *            the index for the first component values
	 * @param componentIndex2
	 *            the index for the second component values
	 * @param isPolar
	 *            true if two arrays are given in polar coordinate
	 * @param stride
	 *            stride of data arrays
	 * @param strideAvailable
	 *            flag whether to set available the stride
	 */
	public SGVXYSDArrayData(final SGSDArrayFile dataFile,
			final SGDataSourceObserver obs, final Integer xCoordinateIndex,
			final Integer yCoordinateIndex, final Integer componentIndex1,
			final Integer componentIndex2, final boolean isPolar,
			final SGIntegerSeriesSet stride, final boolean strideAvailable) {

		super(dataFile, obs, strideAvailable);

        // null check
        if (xCoordinateIndex == null || yCoordinateIndex == null || componentIndex1 == null || componentIndex2 == null) {
            throw new IllegalArgumentException("Indices must not be null.");
        }

        // x coordinate values
        if (this.checkColumnIndexRange(xCoordinateIndex) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + xCoordinateIndex);
        }

        // y coordinate values
        if (this.checkColumnIndexRange(yCoordinateIndex) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + yCoordinateIndex);
        }

        // the first component values
        if (this.checkColumnIndexRange(componentIndex1) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + componentIndex1);
        }

        // the second component values
        if (this.checkColumnIndexRange(componentIndex2) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + componentIndex2);
        }

        // set to attributes
        this.mXCoordinateIndex = xCoordinateIndex;
        this.mYCoordinateIndex = yCoordinateIndex;
        this.mFirstComponentIndex = componentIndex1;
        this.mSecondComponentIndex = componentIndex2;
        this.mPolarFlag = isPolar;

        // set up the stride
    	final int len = this.getAllPointsNumber();
        this.mStride = this.createStride(stride, len);
    }

    /**
     * Disposes of this data object.
     * Do not call any method of this object after called this method.
     */
    public void dispose() {
    	super.dispose();
    	this.mXCoordinateIndex = null;
    	this.mYCoordinateIndex = null;
    	this.mFirstComponentIndex = null;
    	this.mSecondComponentIndex = null;
    }

    /**
     * Set values with a given data.
     *
     * @param data
     *             data to be copied
     * @return
     *             true if succeeded
     */
    public boolean setData(final SGData data) {

        if (!(data instanceof SGVXYSDArrayData)) {
            return false;
        }

        if (super.setData(data) == false) {
            return false;
        }

        SGVXYSDArrayData dataVXY = (SGVXYSDArrayData) data;
        this.mXCoordinateIndex = dataVXY.mXCoordinateIndex;
        this.mYCoordinateIndex = dataVXY.mYCoordinateIndex;
        this.mFirstComponentIndex = dataVXY.mFirstComponentIndex;
        this.mSecondComponentIndex = dataVXY.mSecondComponentIndex;
        this.mPolarFlag = dataVXY.isPolar();

        return true;
    }

    /**
     * Returns the name of data type.
     *
     * @return the name of data type
     */
    public String getDataType() {
        return SGDataTypeConstants.VXY_DATA;
    }

    /**
     * Returns the bounds of x-values.
     *
     * @return the bounds of x-values
     */
    public SGValueRange getBoundsX() {
        return SGDataUtility.getBoundsX(this);
    }

    /**
     * Returns the bounds of y-values.
     *
     * @return the bounds of y-values
     */
    public SGValueRange getBoundsY() {
        return SGDataUtility.getBoundsY(this);
    }

    /**
     * Returns an array of coordinates.
     *
     * @return an array of coordinates
     */
    public SGTuple2d[] getXYValueArray(final boolean all) {
        double[] xArray = this.getXValueArray(all);
        double[] yArray = this.getYValueArray(all);
        SGTuple2d[] cArray = new SGTuple2d[xArray.length];
        for (int ii = 0; ii < xArray.length; ii++) {
            cArray[ii] = new SGTuple2d(xArray[ii], yArray[ii]);
        }
        return cArray;
    }

    /**
     * Returns an array of x component of vectors.
     *
     * @return an array of x component of vectors
     */
    public double[] getXComponentArray(final boolean all) {
        if (this.isPolar()) {
            final double[] magArray = this.getFirstComponentValueArray(all);
            final double[] angleArray = this.getSecondComponentValueArray(all);
            return SGVXYDataUtility.getXComponentArray(magArray, angleArray);
        } else {
        	return this.getFirstComponentValueArray(all);
        }
    }

    /**
     * Returns an array of y component of vectors.
     *
     * @return an array of y component of vectors
     */
    public double[] getYComponentArray(final boolean all) {
        if (this.isPolar()) {
            final double[] magArray = this.getFirstComponentValueArray(all);
            final double[] angleArray = this.getSecondComponentValueArray(all);
            return SGVXYDataUtility.getYComponentArray(magArray, angleArray);
        } else {
        	return this.getSecondComponentValueArray(all);
        }
    }

    /**
     * Returns an array of magnitude of vectors.
     *
     * @return an array of magnitude of vectors
     */
    public double[] getMagnitudeArray(final boolean all) {
        if (this.isPolar()) {
        	return this.getFirstComponentValueArray(all);
        } else {
            final double[] xComArray = this.getFirstComponentValueArray(all);
            final double[] yComArray = this.getSecondComponentValueArray(all);
            return SGVXYDataUtility.getMagnitudeArray(xComArray, yComArray);
        }
    }

    /**
     * Returns an array of angle of vectors.
     *
     * @return an array of angle of vectors
     */
    public double[] getAngleArray(final boolean all) {
        if (this.isPolar()) {
        	return this.getSecondComponentValueArray(all);
        } else {
            final double[] xComArray = this.getFirstComponentValueArray(all);
            final double[] yComArray = this.getSecondComponentValueArray(all);
            return SGVXYDataUtility.getAngleArray(xComArray, yComArray);
        }
    }

    /**
     * Returns an array of values for the first component.
     *
     * @return an array of values for the first component
     */
    @Override
    public double[] getFirstComponentValueArray(final boolean all) {
		return SGDataUtility.getFirstComponentValueArray(this, all);
	}
    
    @Override
	public boolean useFirstComponentValueCache(final boolean all) {
		return this.useCache(all);
	}

	@Override
    public double[] getFirstComponentValueArray(final boolean all, final boolean useCache) {
    	double[] ret = null;
    	final boolean b = (all || !this.isStrideAvailable());
    	ret = this.getNumberArray(this.mFirstComponentIndex, b);
    	if (useCache) {
            SGVXYDataCache.setFirstComponentValues(this, ret);
    	}
    	return ret;
    }

    /**
     * Returns an array of values for the second component.
     *
     * @return an array of values for the second component
     */
    @Override
    public double[] getSecondComponentValueArray(final boolean all) {
		return SGDataUtility.getSecondComponentValueArray(this, all);
    }
    
    @Override
	public boolean useSecondComponentValueCache(final boolean all) {
		return this.useCache(all);
	}

	@Override
    public double[] getSecondComponentValueArray(final boolean all, final boolean useCache) {
    	double[] ret = null;
    	final boolean b = (all || !this.isStrideAvailable());
    	ret = this.getNumberArray(this.mSecondComponentIndex, b);
    	if (useCache) {
            SGVXYDataCache.setSecondComponentValues(this, ret);
    	}
    	return ret;
    }

    /**
     * Returns whether the data is given in the polar coordinate.
     *
     * @return true if the data is given in the polar coordinate
     *
     */
    public boolean isPolar() {
        return this.mPolarFlag;
    }

    /**
     * Writes data column indices in the attributes.
     *
     * @param el
     *            the Element object
     * @return true if succeeded
     */
    protected boolean writeAttributeColumnIndices(Element el) {
        el.setAttribute(KEY_X_COORDINATE_COLUMN_INDEX, this.mXCoordinateIndex.toString());
        el.setAttribute(KEY_Y_COORDINATE_COLUMN_INDEX, this.mYCoordinateIndex.toString());
        el.setAttribute(KEY_FIRST_COMPONENT_COLUMN_INDEX, this.mFirstComponentIndex.toString());
        el.setAttribute(KEY_SECOND_COMPONENT_COLUMN_INDEX, this.mSecondComponentIndex.toString());
        return true;
    }

    /**
     * Writes data column indices as sequential numbers.
     *
     * @param el
     *            the Element object
     * @return true if succeeded
     */
    protected boolean writeSequentialColumnIndices(Element el) {
        el.setAttribute(KEY_X_COORDINATE_COLUMN_INDEX, Integer.toString(0));
        el.setAttribute(KEY_Y_COORDINATE_COLUMN_INDEX, Integer.toString(1));
        el.setAttribute(KEY_FIRST_COMPONENT_COLUMN_INDEX, Integer.toString(2));
        el.setAttribute(KEY_SECOND_COMPONENT_COLUMN_INDEX, Integer.toString(3));
        return true;
    }

    @Override
    protected boolean writeSequentialColumnName(Element el) {
        el.setAttribute(KEY_X_COORDINATE_COLUMN_INDEX, this.getSequentialColumnName(0));
        el.setAttribute(KEY_Y_COORDINATE_COLUMN_INDEX, this.getSequentialColumnName(1));
        el.setAttribute(KEY_FIRST_COMPONENT_COLUMN_INDEX, this.getSequentialColumnName(2));
        el.setAttribute(KEY_SECOND_COMPONENT_COLUMN_INDEX, this.getSequentialColumnName(3));

        // serial numbers
        el.setAttribute(KEY_INDEX_VARIABLE_NAME, INDEX);

        return true;
    }

//    /**
//     * Read properties from a given Element and set to this data.
//     * @param el
//     *          an Element
//     * @return
//     *          true if succeeded
//     */
//    public boolean readProperty(Element el) {
//        Integer index = null;
//        if ((index = this.readIndex(el, KEY_X_COORDINATE_COLUMN_INDEX)) != null) {
//            if (this.checkColumnIndexRange(index) == false) {
//                return false;
//            }
//            this.mXCoordinateIndex = index;
//        } else {
//            return false;
//        }
//        if ((index = this.readIndex(el, KEY_Y_COORDINATE_COLUMN_INDEX)) != null) {
//            if (this.checkColumnIndexRange(index) == false) {
//                return false;
//            }
//            this.mYCoordinateIndex = index;
//        } else {
//            return false;
//        }
//        if ((index = this.readIndex(el, KEY_FIRST_COMPONENT_COLUMN_INDEX)) != null) {
//            if (this.checkColumnIndexRange(index) == false) {
//                return false;
//            }
//            this.mComponentIndex1 = index;
//        } else {
//            return false;
//        }
//        if ((index = this.readIndex(el, KEY_SECOND_COMPONENT_COLUMN_INDEX)) != null) {
//            if (this.checkColumnIndexRange(index) == false) {
//                return false;
//            }
//            this.mComponentIndex2 = index;
//        } else {
//            return false;
//        }
//        return true;
//    }

    public Map<String, Object> getInfoMap() {
        Map<String, Object> map = super.getInfoMap();
        map.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED,
                Boolean.valueOf(this.isPolar()));
        return map;
    }

    /**
     * Returns the title of X values.
     * @return
     *         the title of X values
     */
    public String getTitleX() {
        if (this.mXCoordinateIndex == null) {
            return "";
        } else {
        	SGDataColumn[] columns = this.getDataFile().mDataColumns;
            return columns[this.mXCoordinateIndex.intValue()].getTitle();
        }
    }

    /**
     * Returns the title of Y values.
     * @return
     *         the title of Y values
     */
    public String getTitleY() {
        if (this.mYCoordinateIndex == null) {
            return "";
        } else {
        	SGDataColumn[] columns = this.getDataFile().mDataColumns;
            return columns[this.mYCoordinateIndex.intValue()].getTitle();
        }
    }

    public Integer getXCoordinateIndex() {
        return mXCoordinateIndex;
    }

    public Integer getYCoordinateIndex() {
        return mYCoordinateIndex;
    }

    public Integer getFirstComponentIndex() {
        return mFirstComponentIndex;
    }

    public Integer getSecondComponentIndex() {
        return mSecondComponentIndex;
    }

    public void setXCoordinateIndex(Integer index) {
        if (this.checkColumnIndexRange(index) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        mXCoordinateIndex = index;
    }

    public void setYCoordinateIndex(Integer index) {
        if (this.checkColumnIndexRange(index) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        mYCoordinateIndex = index;
    }

    public void setComponentIndex1(Integer index) {
        if (this.checkColumnIndexRange(index) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        mFirstComponentIndex = index;
    }

    public void setComponentIndex2(Integer index) {
        if (this.checkColumnIndexRange(index) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        mSecondComponentIndex = index;
    }

    /**
     * Returns properties of this data.
     * @return
     *         properties of this data
     */
    public SGProperties getProperties() {
        SGProperties p = new VXYSDArrayDataProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    /**
     * Get properties of this data.
     * @param p
     *          properties to set values
     * @return
     *          true if succeeded
     */
    public boolean getProperties(final SGProperties p) {
        if ((p instanceof VXYSDArrayDataProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        VXYSDArrayDataProperties ep = (VXYSDArrayDataProperties) p;
        ep.mXCoordinateIndex = this.getXCoordinateIndex();
        ep.mYCoordinateIndex = this.getYCoordinateIndex();
        ep.mComponentIndex1 = this.getFirstComponentIndex();
        ep.mComponentIndex2 = this.getSecondComponentIndex();
        return true;
    }

    /**
     * Set properties to this data.
     * @param p
     *          properties that have values to set to this data
     * @return
     *          true if succeeded
     */
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof VXYSDArrayDataProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        VXYSDArrayDataProperties ep = (VXYSDArrayDataProperties) p;
        this.mXCoordinateIndex = ep.mXCoordinateIndex;
        this.mYCoordinateIndex = ep.mYCoordinateIndex;
        this.mFirstComponentIndex = ep.mComponentIndex1;
        this.mSecondComponentIndex = ep.mComponentIndex2;
        return true;
    }

    /**
     * Sets the type of data columns.
     *
     * @param column
     *           an array of column types
     * @return true if succeeded
     */
    public boolean setColumnType(String[] columns) {
        if (columns == null) {
            throw new IllegalArgumentException("columns == null");
        }

        final boolean polar = this.isPolar();
    	final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
    	final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
        Integer xIndex = null;
        Integer yIndex = null;
        Integer fIndex = null;
        Integer sIndex = null;
        for (int ii = 0; ii < columns.length; ii++) {
            if (SGDataUtility.isEqualColumnType(X_COORDINATE, columns[ii])) {
                xIndex = Integer.valueOf(ii);
            } else if (SGDataUtility.isEqualColumnType(Y_COORDINATE, columns[ii])) {
                yIndex = Integer.valueOf(ii);
            } else if (SGDataUtility.isEqualColumnType(first, columns[ii])) {
                fIndex = Integer.valueOf(ii);
            } else if (SGDataUtility.isEqualColumnType(second, columns[ii])) {
                sIndex = Integer.valueOf(ii);
            } else if ("".equals(columns[ii])) {
                continue;
            } else {
            	return false;
            }
        }

        // set to the attributes
        this.mXCoordinateIndex = xIndex;
        this.mYCoordinateIndex = yIndex;
        this.mFirstComponentIndex = fIndex;
        this.mSecondComponentIndex = sIndex;

        return true;
    }

//    /**
//     * Returns whether all given data columns are valid.
//     *
//     * @param columns
//     *           data columns
//     * @return true if all given data columns are valid
//     */
//    public boolean isValidColumn(String[] columns) {
//
//        Integer xOld = this.mXCoordinateIndex;
//        Integer yOld = this.mYCoordinateIndex;
//        Integer fOld = this.mComponentIndex1;
//        Integer sOld = this.mComponentIndex2;
//
//        Integer xIndex = null;
//        Integer yIndex = null;
//        Integer fIndex = null;
//        Integer sIndex = null;
//
//        for (int ii = 0; ii < columns.length; ii++) {
//            if (SGUtilityText.isEqualString(X_COORDINATE, columns[ii])) {
//                xIndex = Integer.valueOf(ii);
//            } else if (SGUtilityText.isEqualString(Y_COORDINATE, columns[ii])) {
//                yIndex = Integer.valueOf(ii);
//            } else if (SGUtilityText.isEqualString(MAGNITUDE, columns[ii])
//            		|| SGUtilityText.isEqualString(X_COMPONENT, columns[ii])) {
//                fIndex = Integer.valueOf(ii);
//            } else if (SGUtilityText.isEqualString(ANGLE, columns[ii])
//            		|| SGUtilityText.isEqualString(Y_COMPONENT, columns[ii])) {
//                sIndex = Integer.valueOf(ii);
//            } else if ("".equals(columns[ii])) {
//                continue;
//            } else {
//            	return false;
//            }
//        }
//
//        // check the necessary columns
//        if (xIndex == null || yIndex == null || fIndex == null || sIndex == null) {
//        	return false;
//        }
//
//        // set to attributes
//        this.mXCoordinateIndex = xIndex;
//        this.mYCoordinateIndex = yIndex;
//        this.mComponentIndex1 = fIndex;
//        this.mComponentIndex2 = sIndex;
//
////        try {
////            // check magnitude
////            double[] magArray = this.getMagnitudeArray();
////            for (int ii = 0; ii < magArray.length; ii++) {
////            	if (magArray[ii] < 0.0) {
////            		return false;
////            	}
////            }
////
////        } finally {
////            // recover
////    		this.mXCoordinateIndex = xOld;
////    		this.mYCoordinateIndex = yOld;
////    		this.mComponentIndex1 = fOld;
////    		this.mComponentIndex2 = sOld;
////        }
//
//        // recover
//		this.mXCoordinateIndex = xOld;
//		this.mYCoordinateIndex = yOld;
//		this.mComponentIndex1 = fOld;
//		this.mComponentIndex2 = sOld;
//
//    	return true;
//    }

    /**
     * A class of properties of vector xy-type data.
     */
    public static class VXYSDArrayDataProperties extends SDArrayDataProperties {

        // indices of data columns
        Integer mXCoordinateIndex = null;

        Integer mYCoordinateIndex = null;

        Integer mComponentIndex1 = null;

        Integer mComponentIndex2 = null;

        /**
         * The default constructor.
         */
        public VXYSDArrayDataProperties() {
            super();
        }

    	@Override
    	public boolean equals(Object obj) {
            if ((obj instanceof VXYSDArrayDataProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
            	return false;
            }
            return true;
        }

    	@Override
        public boolean hasEqualSize(DataProperties dp) {
            if ((dp instanceof VXYSDArrayDataProperties) == false) {
                return false;
            }
            if (!super.hasEqualSize(dp)) {
            	return false;
            }
            return true;
    	}

        /**
         * Returns whether this data property has the equal column types with given data property.
         *
         * @param p
         *          a data property
         * @return true if this data property has the equal column types with given data property
         */
        @Override
        public boolean hasEqualColumnTypes(DataProperties dp) {
            if ((dp instanceof VXYSDArrayDataProperties) == false) {
                return false;
            }
            VXYSDArrayDataProperties p = (VXYSDArrayDataProperties) dp;
            if (!SGUtility.equals(this.mXCoordinateIndex, p.mXCoordinateIndex)) {
            	return false;
            }
            if (!SGUtility.equals(this.mYCoordinateIndex, p.mYCoordinateIndex)) {
            	return false;
            }
            if (!SGUtility.equals(this.mComponentIndex1, p.mComponentIndex1)) {
            	return false;
            }
            if (!SGUtility.equals(this.mComponentIndex2, p.mComponentIndex2)) {
            	return false;
            }
            return true;
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mXCoordinateIndex = null;
            this.mYCoordinateIndex = null;
            this.mComponentIndex1 = null;
            this.mComponentIndex2 = null;
        }
    }

    /**
     * Returns an array of current column types.
     * @return
     *        an array of current column types
     */
    public String[] getCurrentColumnType() {
    	final boolean polar = this.isPolar();
    	final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
    	final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
        final int colNum = this.getColNum();
        String[] array = new String[colNum];
        for (int ii = 0; ii < array.length; ii++) {
        	array[ii] = "";
        }
        if (this.mXCoordinateIndex != null) {
            array[this.mXCoordinateIndex.intValue()] = X_COORDINATE;
        }
        if (this.mYCoordinateIndex != null) {
            array[this.mYCoordinateIndex.intValue()] = Y_COORDINATE;
        }
        if (this.mFirstComponentIndex != null) {
            array[this.mFirstComponentIndex.intValue()] = first;
        }
        if (this.mSecondComponentIndex != null) {
            array[this.mSecondComponentIndex.intValue()] = second;
        }
        return array;
    }

    /**
     * Returns an array of data columns to save to a data set.
     *
     * @return an array of data columns to save to a data set
     */
    public SGDataColumn[] getExportedColumns() {
    	SGDataColumn[] columns = this.getDataFile().mDataColumns;
        SGDataColumn[] colArray = new SGDataColumn[4];
        colArray[0] = columns[this.mXCoordinateIndex.intValue()];
        colArray[1] = columns[this.mYCoordinateIndex.intValue()];
        colArray[2] = columns[this.mFirstComponentIndex.intValue()];
        colArray[3] = columns[this.mSecondComponentIndex.intValue()];

        final boolean polar = this.isPolar();
    	final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
    	final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
        colArray[0].setColumnType(X_COORDINATE);
        colArray[1].setColumnType(Y_COORDINATE);
        colArray[2].setColumnType(first);
        colArray[3].setColumnType(second);
        return colArray;
    }

    @Override
    public SGDataColumn[] getUsedDataColumnsClone() {
        return this.getExportedColumnsClone();
    }

    /**
     * Returns the list of blocks of values of the first component.
     *
     * @return the list of blocks of values of the first component
     */
	@Override
    public List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlockList() {
    	// always returns null
    	return null;
    }

    /**
     * Returns the list of blocks of values of the second component.
     *
     * @return the list of blocks of values of the second component
     */
	@Override
    public List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlockList() {
    	// always returns null
    	return null;
    }

    /**
     * Returns a text string of the data type to save into a NetCDF data set file.
     *
     * @return a text string of the data type to save into a NetCDF data set file
     */
    @Override
    public String getNetCDFDataSetDataType() {
    	return SGDataTypeConstants.VXY_NETCDF_DATA;
    }

    /**
     * Sets the stride of data arrays.
     *
     * @param map
     *           the map of the stride
     */
    @Override
    public void setStrideMap(Map<String, SGIntegerSeriesSet> map) {
    	this.mStride = map.get(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE);
    }

    /**
     * Returns a map of stride for data arrays.
     *
     * @return a map of stride for data arrays
     */
	@Override
    protected Map<String, SGIntegerSeriesSet> getStrideMap() {
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    	map.put(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE, this.getStride());
    	return map;
    }

	@Override
	public double[] getXValueArray(final boolean all) {
    	return SGDataUtility.getXValueArray(this, all);
	}
	
    @Override
	public boolean useXValueCache(final boolean all) {
		return this.useCache(all);
	}

	@Override
	public double[] getXValueArray(final boolean all, final boolean useCache) {
		double[] ret = null;
    	final boolean b = (all || !this.isStrideAvailable());
		ret = this.getNumberArray(this.mXCoordinateIndex, b);
    	if (useCache) {
            SGVXYDataCache.setXValues(this, ret);
    	}
    	return ret;
	}

	@Override
	public double[] getYValueArray(final boolean all) {
    	return SGDataUtility.getYValueArray(this, all);
	}
	
    @Override
	public boolean useYValueCache(final boolean all) {
		return this.useCache(all);
	}

	@Override
	public double[] getYValueArray(final boolean all, final boolean useCache) {
		double[] ret = null;
    	final boolean b = (all || !this.isStrideAvailable());
		ret = this.getNumberArray(this.mYCoordinateIndex, b);
    	if (useCache) {
            SGVXYDataCache.setYValues(this, ret);
    	}
    	return ret;
	}

	class Arrow {
		double x;
		double y;
		double f;
		double s;
		Arrow(double x, double y, double f, double s) {
			super();
			this.x = x;
			this.y = y;
			this.f = f;
			this.s = s;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Arrow)) {
				return false;
			}
			Arrow r = (Arrow) obj;
			if (this.x != r.x) {
				return false;
			}
			if (this.y != r.y) {
				return false;
			}
			if (this.f != r.f) {
				return false;
			}
			if (this.s != r.s) {
				return false;
			}
			return true;
		}
	}

    /**
     * Exports the data into a NetCDF file.
     * 
     * @param ncWrite
     *           the file to save
     * @param mode
     *           the mode to save
     * @param policy
     *           the policy for exporting data
     * @return true if succeeded
     */
	@Override
    public boolean exportToNetCDFFile(NetcdfFileWriteable ncWrite, final SGExportParameter mode, 
			SGDataBufferPolicy policy) throws IOException, InvalidRangeException {

		//
		// function creating NetCDF data file
		//

		SGVXYDataBuffer vxyBuffer = (SGVXYDataBuffer) this.getDataBuffer(policy);
		final int len = vxyBuffer.getLength();
		double[] xValues = vxyBuffer.getXValues();
		double[] yValues = vxyBuffer.getYValues();
		double[] fValues = vxyBuffer.getFirstComponentValues();
		double[] sValues = vxyBuffer.getSecondComponentValues();
		
		final boolean polar = this.isPolar();
        String xName = "X";
        String yName = "Y";
        String fName = polar ? "Magnitude" : "X_Component";
        String sName = polar ? "Angle" : "Y_Component";

		// grid plot
        /*
		Map<Double, List<Arrow>> xMap = new TreeMap<Double, List<Arrow>>();
		Map<Double, List<Arrow>> yMap = new TreeMap<Double, List<Arrow>>();
		for (int ii = 0; ii < len; ii++) {
			final double x = xValues[ii];
			final double y = yValues[ii];
			Arrow value = new Arrow(xValues[ii], yValues[ii], fValues[ii], sValues[ii]);
			
			List<Arrow> xList = xMap.get(x);
			if (xList == null) {
				xList = new ArrayList<Arrow>();
				xMap.put(x, xList);
			}
			xList.add(value);
			
			List<Arrow> yList = yMap.get(y);
			if (yList == null) {
				yList = new ArrayList<Arrow>();
				yMap.put(y, yList);
			}
			yList.add(value);
			
			xMap.put(x, xList);
			yMap.put(y, yList);
		}
		
		List<Double> xValueList = new ArrayList<Double>(xMap.keySet());
		List<Double> yValueList = new ArrayList<Double>(yMap.keySet());
		
		Dimension xDim = new Dimension(xName, xValueList.size());
		ncWrite.addDimension(null, xDim);
		Dimension yDim = new Dimension(yName, yValueList.size());
		ncWrite.addDimension(null, yDim);
		
		Variable xVar = new Variable(ncWrite, null, null, xName, DataType.DOUBLE, xName);
		xVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
				SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		ncWrite.addVariable(null, xVar);

		Variable yVar = new Variable(ncWrite, null, null, yName, DataType.DOUBLE, yName);
		yVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
				SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		ncWrite.addVariable(null, yVar);
		
		String dimString = xName + " " + yName;

		Variable fVar = new Variable(ncWrite, null, null, fName, DataType.DOUBLE, dimString);
		fVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
				SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		ncWrite.addVariable(null, fVar);
		
		Variable sVar = new Variable(ncWrite, null, null, sName, DataType.DOUBLE, dimString);
		sVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
				SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		ncWrite.addVariable(null, sVar);
		
		ncWrite.create();
		
		final int xLen = xDim.getLength();
		Array xArray = Array.factory(DataType.DOUBLE, new int[] { xLen });
		for (int ii = 0; ii < xLen; ii++) {
			xArray.setDouble(ii, xValueList.get(ii));
		}
		ncWrite.write(xName, xArray);
		
		final int yLen = yDim.getLength();
		Array yArray = Array.factory(DataType.DOUBLE, new int[] { yLen });
		for (int ii = 0; ii < yLen; ii++) {
			yArray.setDouble(ii, yValueList.get(ii));
		}
		ncWrite.write(yName, yArray);
		
		Array fArray = Array.factory(DataType.DOUBLE, new int[] { xLen, yLen });
		Index fIndex = fArray.getIndex();
		Array sArray = Array.factory(DataType.DOUBLE, new int[] { xLen, yLen });
		Index sIndex = sArray.getIndex();
		for (int yy = 0; yy < yLen; yy++) {
			List<Arrow> yList = yMap.get(yValueList.get(yy));
			for (int xx = 0; xx < xLen; xx++) {
				List<Arrow> xList = xMap.get(xValueList.get(xx));
				fIndex.set(xx, yy);
				sIndex.set(xx, yy);
				Arrow common = null;
				for (Arrow yValue : yList) {
					for (Arrow xValue : xList) {
						if (xValue.equals(yValue)) {
							common = xValue;
							break;
						}
					}
				}
				fArray.setDouble(fIndex, common.f);
				sArray.setDouble(sIndex, common.s);
			}
		}
		ncWrite.write(fName, fArray);
		ncWrite.write(sName, sArray);
		*/
		
		// scatter plot
		String indexName = "Index";
		Dimension indexDim = new Dimension(indexName, len);
		ncWrite.addDimension(null, indexDim);

		Variable indexVar = new Variable(ncWrite, null, null, indexName, DataType.INT, indexName);
		indexVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
				SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		ncWrite.addVariable(null, indexVar);

		Variable xVar = new Variable(ncWrite, null, null, xName, DataType.DOUBLE, indexName);
		xVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
				SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		ncWrite.addVariable(null, xVar);

		Variable yVar = new Variable(ncWrite, null, null, yName, DataType.DOUBLE, indexName);
		yVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
				SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		ncWrite.addVariable(null, yVar);
		
		Variable fVar = new Variable(ncWrite, null, null, fName, DataType.DOUBLE, indexName);
		fVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
				SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		ncWrite.addVariable(null, fVar);

		Variable sVar = new Variable(ncWrite, null, null, sName, DataType.DOUBLE, indexName);
		sVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
				SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		ncWrite.addVariable(null, sVar);

		ncWrite.create();

		Array xArray = Array.factory(DataType.DOUBLE, new int[] { len });
		for (int ii = 0; ii < len; ii++) {
			xArray.setDouble(ii, xValues[ii]);
		}
		ncWrite.write(xName, xArray);
		
		Array yArray = Array.factory(DataType.DOUBLE, new int[] { len });
		for (int ii = 0; ii < len; ii++) {
			yArray.setDouble(ii, yValues[ii]);
		}
		ncWrite.write(yName, yArray);

		Array fArray = Array.factory(DataType.DOUBLE, new int[] { len });
		for (int ii = 0; ii < len; ii++) {
			fArray.setDouble(ii, fValues[ii]);
		}
		ncWrite.write(fName, fArray);

		Array sArray = Array.factory(DataType.DOUBLE, new int[] { len });
		for (int ii = 0; ii < len; ii++) {
			sArray.setDouble(ii, sValues[ii]);
		}
		ncWrite.write(sName, sArray);
		
		return true;
	}
	
	@Override
    public boolean saveToDataSetNetCDFFile(final File file) {
        final int dataNum = this.getAllPointsNumber();

        SGDataColumn[] colArray = this.getExportedColumnsClone();
        if (colArray.length < 4) {
            return false;
        }
        SGNumberDataColumn colx = (SGNumberDataColumn) colArray[0];
        SGNumberDataColumn coly = (SGNumberDataColumn) colArray[1];
        SGNumberDataColumn colc1 = (SGNumberDataColumn) colArray[2];
        SGNumberDataColumn colc2 = (SGNumberDataColumn) colArray[3];
        
        double[] xvalues = colx.getArray();
        double[] yvalues = coly.getArray();
        double[] c1values = colc1.getArray();
        double[] c2values = colc2.getArray();

        NetcdfFileWriteable ncfile = null;
        try {
            ncfile = NetcdfFileWriteable.createNew(file.getAbsolutePath(), true);

            // Creates the dimension and variable of indices.
            Dimension indexDim = this.addIndexDimension(ncfile, dataNum);
            String indexDimName = indexDim.getName();
            Variable indexVar = this.addIndexVarialbe(ncfile, indexDim);
            indexVar.addAttribute(SGDataUtility.getValueTypeAttribute(SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
            
            // Add data columns as variables.
            Variable varx = new Variable(ncfile, null, null, "column0", DataType.DOUBLE, indexDimName);
            Variable vary = new Variable(ncfile, null, null, "column1", DataType.DOUBLE, indexDimName);
            Variable varc1 = new Variable(ncfile, null, null, "column2", DataType.DOUBLE, indexDimName);
            Variable varc2 = new Variable(ncfile, null, null, "column3", DataType.DOUBLE, indexDimName);
            String title = colx.getTitle();
            if (null!=title && "".equals(title.trim())==false) {
                Attribute attr = new Attribute(ATTRIBUTE_KEY_LONG_NAME, title);
                varx.addAttribute(attr);
            }
            title = coly.getTitle();
            if (null!=title && "".equals(title.trim())==false) {
                Attribute attr = new Attribute(ATTRIBUTE_KEY_LONG_NAME, title);
                vary.addAttribute(attr);
            }
            title = colc1.getTitle();
            if (null!=title && "".equals(title.trim())==false) {
                Attribute attr = new Attribute(ATTRIBUTE_KEY_LONG_NAME, title);
                varc1.addAttribute(attr);
            }
            title = colc2.getTitle();
            if (null!=title && "".equals(title.trim())==false) {
                Attribute attr = new Attribute(ATTRIBUTE_KEY_LONG_NAME, title);
                varc2.addAttribute(attr);
            }

            ncfile.addVariable(null, varx);
            ncfile.addVariable(null, vary);
            ncfile.addVariable(null, varc1);
            ncfile.addVariable(null, varc2);

            String[] varNames = new String[colArray.length];
            varNames[0] = varx.getShortName();
            varNames[1] = vary.getShortName();
            varNames[2] = varc1.getShortName();
            varNames[3] = varc2.getShortName();
            
            varx.addAttribute(SGDataUtility.getValueTypeAttribute(colx.getValueType()));
            vary.addAttribute(SGDataUtility.getValueTypeAttribute(coly.getValueType()));
            varc1.addAttribute(SGDataUtility.getValueTypeAttribute(colc1.getValueType()));
            varc2.addAttribute(SGDataUtility.getValueTypeAttribute(colc2.getValueType()));

            ncfile.create();

            // add values

            Array xarray = Array.factory(DataType.DOUBLE, new int[] { dataNum });
            for (int i = 0; i < xvalues.length; i++) {
                xarray.setDouble(i, xvalues[i]);
            }
            ncfile.write(varNames[0], xarray);

            Array yarray = Array.factory(DataType.DOUBLE, new int[] { dataNum });
            for (int i = 0; i < yvalues.length; i++) {
                yarray.setDouble(i, yvalues[i]);
            }
            ncfile.write(varNames[1], yarray);

            Array c1array = Array.factory(DataType.DOUBLE, new int[] { dataNum });
            for (int i = 0; i < c1values.length; i++) {
                c1array.setDouble(i, c1values[i]);
            }
            ncfile.write(varNames[2], c1array);

            Array c2array = Array.factory(DataType.DOUBLE, new int[] { dataNum });
            for (int i = 0; i < c2values.length; i++) {
                c2array.setDouble(i, c2values[i]);
            }
            ncfile.write(varNames[3], c2array);

            // write serial numbers
            this.writeIndexVarialbe(ncfile, indexVar);
            
        } catch (IOException e) {
            return false;
        } catch (InvalidRangeException e) {
        	return false;
        } finally {
            if (ncfile != null) {
                try {
					ncfile.close();
				} catch (IOException e) {
				}
            }
        }
        
        return true;
	}

    /**
     * Creates and returns a data buffer.
     * 
     * @param param
     *           parameters for data buffer
     * @return the data buffer
     */
	@Override
	public SGDataBuffer getDataBuffer(SGDataBufferPolicy param) {
		final boolean all = param.isAllValuesGotten();
		final boolean edit = param.isEditedValuesReflected();
		
		double[] xValues = this.getXValueArray(all, false);
		double[] yValues = this.getYValueArray(all, false);
		double[] fValues = this.getFirstComponentValueArray(all, false);
		double[] sValues = this.getSecondComponentValueArray(all, false);
		
		// set edited values
		if (edit) {
			final boolean polar = this.isPolar();
			final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
			final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
			SGIntegerSeriesSet stride = this.getStride();
			int[] indices = stride.getNumbers();
			for (int ii = 0; ii < this.mEditedDataValueList.size(); ii++) {
				SGDataValueHistory dataValue = this.mEditedDataValueList.get(ii);
				String columnType = dataValue.getColumnType();
				final int index = dataValue.getRowIndex();
				final int arrayIndex = all ? index : Arrays.binarySearch(indices, index);
				final double value = dataValue.getValue();
				double[] values;
				if (X_COORDINATE.equals(columnType)) {
					values = xValues;
				} else if (Y_COORDINATE.equals(columnType)) {
					values = yValues;
				} else if (first.equals(columnType)) {
					values = fValues;
				} else if (second.equals(columnType)) {
					values = sValues;
				} else {
					throw new Error("Invalid column type: " + columnType);
				}
				values[arrayIndex] = value;
			}
		}

		return new SGVXYDataBuffer(xValues, yValues, fValues, sValues, this.isPolar());
	}

	@Override
	protected Object[][] getDataFileExportValues(SGExportParameter mode, SGDataBufferPolicy policy) {
		return this.getValueTable(mode, policy);
	}

	@Override
	protected Object[][] getArchiveDataSetExportValues(SGExportParameter mode) {
		return this.getValueTable(mode, SGDataUtility.getArchiveDataSetBufferPolicy(this));
	}

	@Override
    public Object[][] getValueTable(final SGExportParameter mode, SGDataBufferPolicy policy) {
		return SGDataUtility.getValueTable(this, mode, policy);
	}

	/**
	 * Returns true if all stride of this data are available and each string representation 
	 * is different from "0:end".
	 * 
	 * @return true if all stride are effective
	 */
	@Override
	public boolean hasEffectiveStride() {
		if (!this.isStrideAvailable()) {
			return false;
		}
    	SGIntegerSeriesSet stride = this.getStride();
    	return !stride.isComplete();
	}

	@Override
    protected boolean exportToHDF5File(IHDF5Writer writer, SGDataColumn[] colArray) {
		// scatter plot
//		return super.exportToHDF5File(writer, colArray);
		
		// grid plot
		SGVXYDataBuffer vxyBuffer = (SGVXYDataBuffer) this.getDataBuffer(
				new SGDataBufferPolicy(true, false, true));
		final int len = vxyBuffer.getLength();
		double[] xValues = vxyBuffer.getXValues();
		double[] yValues = vxyBuffer.getYValues();
		double[] fValues = vxyBuffer.getFirstComponentValues();
		double[] sValues = vxyBuffer.getSecondComponentValues();
		
		final boolean polar = this.isPolar();
        String xName = "X";
        String yName = "Y";
        String fName = polar ? "Magnitude" : "X_Component";
        String sName = polar ? "Angle" : "Y_Component";

		// grid plot
		Map<Double, List<Arrow>> xMap = new TreeMap<Double, List<Arrow>>();
		Map<Double, List<Arrow>> yMap = new TreeMap<Double, List<Arrow>>();
		for (int ii = 0; ii < len; ii++) {
			final double x = xValues[ii];
			final double y = yValues[ii];
			Arrow value = new Arrow(xValues[ii], yValues[ii], fValues[ii], sValues[ii]);
			
			List<Arrow> xList = xMap.get(x);
			if (xList == null) {
				xList = new ArrayList<Arrow>();
				xMap.put(x, xList);
			}
			xList.add(value);
			
			List<Arrow> yList = yMap.get(y);
			if (yList == null) {
				yList = new ArrayList<Arrow>();
				yMap.put(y, yList);
			}
			yList.add(value);
			
			xMap.put(x, xList);
			yMap.put(y, yList);
		}
		
		List<Double> xValueList = new ArrayList<Double>(xMap.keySet());
		List<Double> yValueList = new ArrayList<Double>(yMap.keySet());
		
		final int xLen = xValueList.size();
		final int yLen = yValueList.size();
		
		double[] xValueArray = new double[xLen];
		for (int ii = 0; ii < xLen; ii++) {
			xValueArray[ii] = xValueList.get(ii);
		}
		writer.writeDoubleArray(xName, xValueArray);
		
		double[] yValueArray = new double[yLen];
		for (int ii = 0; ii < yLen; ii++) {
			yValueArray[ii] = yValueList.get(ii);
		}
		writer.writeDoubleArray(yName, yValueArray);
		
		MDDoubleArray fArray = new MDDoubleArray(new int[] { xLen, yLen });
		MDDoubleArray sArray = new MDDoubleArray(new int[] { xLen, yLen });
		for (int yy = 0; yy < yLen; yy++) {
			List<Arrow> yList = yMap.get(yValueList.get(yy));
			for (int xx = 0; xx < xLen; xx++) {
				List<Arrow> xList = xMap.get(xValueList.get(xx));
				Arrow common = null;
				for (Arrow yValue : yList) {
					for (Arrow xValue : xList) {
						if (xValue.equals(yValue)) {
							common = xValue;
							break;
						}
					}
				}
				fArray.set(common.f, xx, yy);
				sArray.set(common.s, xx, yy);
			}
		}
		writer.writeDoubleMDArray(fName, fArray);
		writer.writeDoubleMDArray(sName, sArray);
		
    	return true;
    }

    /**
     * Returns a text string for the command of the column types.
     * 
     * @return a text string for the command of the column types
     */
	@Override
    public String getColumnTypeCommandString() {
		List<String> varList = new ArrayList<String>();
		List<String> columnTypeList = new ArrayList<String>();
		
    	final boolean polar = this.isPolar();

		String strX = Integer.toString(this.mXCoordinateIndex + 1);
		varList.add(strX);
		columnTypeList.add(X_COORDINATE);
		
		String strY = Integer.toString(this.mYCoordinateIndex + 1);
		varList.add(strY);
		columnTypeList.add(Y_COORDINATE);
		
		String strF = Integer.toString(this.mFirstComponentIndex + 1);
		varList.add(strF);
		columnTypeList.add(SGDataUtility.getVXYFirstComponentColumnType(polar));
		
		String strS = Integer.toString(this.mSecondComponentIndex + 1);
		varList.add(strS);
		columnTypeList.add(SGDataUtility.getVXYSecondComponentColumnType(polar));
		
		return SGDataUtility.getDataColumnTypeCommand(varList, columnTypeList);
	}

	@Override
	protected boolean setArraySectionPropertySub(SGPropertyMap map) {
        if (this.isStrideAvailable()) {
            SGIntegerSeriesSet arraySection = this.getStride();
            if (!arraySection.isComplete()) {
            	SGPropertyUtility.addQuotedStringProperty(map, COM_DATA_ARRAY_SECTION, arraySection.toString());
            }
        }
		return true;
	}

    /**
     * Returns the bounds of x-values for all animation frames.
     * 
     * @return the bounds of x-values
     */
	@Override
    public SGValueRange getAllAnimationFrameBoundsX() {
		return SGDataUtility.getAllAnimationFrameBoundsX(this);
	}

    /**
     * Returns the bounds of y-values for all animation frames.
     * 
     * @return the bounds of y-values
     */
	@Override
    public SGValueRange getAllAnimationFrameBoundsY() {
		return SGDataUtility.getAllAnimationFrameBoundsY(this);
	}

    /**
     * Returns the array of column types.
     * 
     * @return the array of column types
     */
	@Override
    public String[] getDataViewerColumnTypes() {
    	List<String> list = new ArrayList<String>();
		list.add(SGIDataColumnTypeConstants.X_COORDINATE);
		list.add(SGIDataColumnTypeConstants.Y_COORDINATE);
		final boolean polar = this.isPolar();
    	final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
    	final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
		list.add(first);
		list.add(second);
    	String[] ret = list.toArray(new String[list.size()]);
		return ret;
    }

    /**
     * Returns preferred column type for data viewer.
     * 
     * @return preferred column type for data viewer
     */
	@Override
    public String getPreferredDataViewColumnType() {
    	return SGDataUtility.getPreferredDataViewColumnType(this);
    }
	
	@Override
	public Double getDataViewerValue(String columnType, int row, int col) {
		return SGDataUtility.getDataViewerValue(this, columnType, row, col);
	}

	@Override
	public int getDataViewerColumnNumber(String columnType, final boolean all) {
		// always returns 1
		return 1;
	}

	@Override
	public int getDataViewerRowNumber(String columnType, final boolean all) {
		if (all) {
			return this.getAllPointsNumber();
		} else {
			return this.getPointsNumber();
		}
	}

	@Override
    public Double getXValueAt(final int index) {
		double[] values = this.getXValueArray(false);
		return values[index];
	}
    
	@Override
    public Double getYValueAt(final int index) {
		double[] values = this.getYValueArray(false);
		return values[index];
	}

	@Override
    public Double getFirstComponentValueAt(final int index) {
		double[] values = this.getFirstComponentValueArray(false);
		return values[index];
	}

	@Override
    public Double getFirstComponentValueAt(final int row, final int col) {
		throw new Error("Unsupported.");
	}

	@Override
    public Double getSecondComponentValueAt(final int index) {
		double[] values = this.getSecondComponentValueArray(false);
		return values[index];
	}

	@Override
    public Double getSecondComponentValueAt(final int row, final int col) {
		throw new Error("Unsupported.");
	}

	@Override
    public SGIntegerSeriesSet getDataViewerColStride(String columnType) {
		return new SGIntegerSeriesSet(0);
    }

	@Override
    public SGIntegerSeriesSet getDataViewerRowStride(String columnType) {
		return this.mStride;
	}

	@Override
	public void setDataViewerValue(String columnType, int row, int col,
			Object value) {
		SGDataValueHistory editedValue = SGDataUtility.setDataViewerValue(this,
				columnType, row, col, value);
		if (editedValue != null) {
			this.mEditedDataValueList.add(editedValue);
		}
	}

	@Override
	public SGIntegerSeriesSet getXStride() {
		return this.mStride;
	}

	@Override
	public SGIntegerSeriesSet getYStride() {
		return this.mStride;
	}

	@Override
    public String getToolTipSpatiallyVaried(final int index) {
		return this.getIndexToolTipSpatiallyVaried(index);
	}

    public String getToolTipSpatiallyVaried(final int index0, final int index1) {
		// always returns null
		return null;
    }

    /**
     * Returns a text string of the unit for X values.
     * 
     * @return a text string of the unit for X values
     */
	@Override
    public String getUnitsStringX() {
		// always returns null
		return null;
	}

    /**
     * Returns a text string of the unit for Y values.
     * 
     * @return a text string of the unit for Y values
     */
	@Override
    public String getUnitsStringY() {
		// always returns null
		return null;
	}

	@Override
	public void setDataValue(SGDataValueHistory value) {
		SGDataUtility.setDataViewerValue(this, value.getColumnType(), 
				value.getRowIndex(), value.getColumnIndex(), 
				value.getValue());
	}

	@Override
	public void restoreCache() {
		final boolean all = false;
		final boolean useCache = true;
    	this.getXValueArray(all, useCache);
    	this.getYValueArray(all, useCache);
    	this.getFirstComponentValueArray(all, useCache);
    	this.getSecondComponentValueArray(all, useCache);
	}

	@Override
	public List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlockListSub(
			final boolean all, final boolean useCache, final boolean removeInvalidValues) {
		// always returns null
		return null;
	}

	@Override
	public List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlockListSub(
			final boolean all, final boolean useCache, final boolean removeInvalidValues) {
		// always returns null
		return null;
	}

	@Override
	public SGDataValue getDataValue(final int index) {
		return SGDataUtility.getDataValue(this, index);
	}

	@Override
	public SGDataValue getDataValue(final int xIndex, final int yIndex) {
		throw new Error("Unsupported.");
	}

	@Override
	public double[] getXValueArray(boolean all, boolean useCache,
			boolean removeInvalidValues) {
		return this.getXValueArray(all, useCache);
	}

	@Override
	public double[] getYValueArray(boolean all, boolean useCache,
			boolean removeInvalidValues) {
		return this.getYValueArray(all, useCache);
	}

	@Override
	public double[] getFirstComponentValueArray(boolean all, boolean useCache,
			boolean removeInvalidValues) {
		return this.getFirstComponentValueArray(all, useCache);
	}

	@Override
	public double[] getSecondComponentValueArray(boolean all, boolean useCache,
			boolean removeInvalidValues) {
		return this.getSecondComponentValueArray(all, useCache);
	}

	@Override
	protected boolean matches(final int col, final int row, String columnType,
			SGDataValueHistory value, final double d) {
		return SGDataUtility.matches(col, row, columnType, value, d);
	}

	@Override
	public List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlockList(
			final boolean all, final boolean useCache, 
			final boolean removeInvalidValues) {
		// always returns null
		return null;
	}

	@Override
	public List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlockList(
			final boolean all, final boolean useCache, 
			final boolean removeInvalidValues) {
		// always returns null
		return null;
	}

}
