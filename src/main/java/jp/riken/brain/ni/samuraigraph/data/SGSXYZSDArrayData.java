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
 * Scalar type XYZ data. This object has of x, y and z values.
 *
 */
public class SGSXYZSDArrayData extends SGSDArrayData implements SGISXYZTypeData,
        SGIDataColumnTypeConstants, SGIDataPropertyKeyConstants {

    /**
     * The column index for x-values.
     */
    protected Integer mXIndex = null;

    /**
     * The column index for y-values.
     */
    protected Integer mYIndex = null;

    /**
     * The column index for z-values.
     */
    protected Integer mZIndex = null;

    /**
     * Builds a data object.
     *
     */
    public SGSXYZSDArrayData() {
        super();
    }

	/**
	 * Builds a data object with given columns and indices.
	 *
	 * @param dataFile
	 *            the text data file
	 * @param obs
	 *            an observer for array data
	 * @param xIndex
	 *            the column index for x-values
	 * @param yIndex
	 *            the column index for y-values
	 * @param zIndex
	 *            the column index for z-values
	 * @param stride
	 *            stride of data arrays
	 * @param strideAvailable
	 *            flag whether to set available the stride
	 */
    public SGSXYZSDArrayData(final SGSDArrayFile dataFile,
    		final SGDataSourceObserver obs,
            final Integer xIndex, final Integer yIndex, final Integer zIndex,
			final SGIntegerSeriesSet stride, final boolean strideAvailable) {

        super(dataFile, obs, strideAvailable);

        // null check
        if (xIndex == null || yIndex == null || zIndex == null) {
            throw new IllegalArgumentException("Indices for values must not be null.");
        }

        // x-values
        if (this.checkColumnIndexRange(xIndex) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + xIndex);
        }

        // y-values
        if (this.checkColumnIndexRange(yIndex) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + yIndex);
        }

        // z-values
        if (this.checkColumnIndexRange(zIndex) == false) {
            throw new IllegalArgumentException("Index out of bounds: " + zIndex);
        }

        // set indices
        this.mXIndex = xIndex;
        this.mYIndex = yIndex;
        this.mZIndex = zIndex;

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
    	this.mXIndex = null;
    	this.mYIndex = null;
    	this.mZIndex = null;
    }

    /**
     * Sets the data.
     *
     * @param data
     *             data set to this object
     * @return
     *             true if succeeded
     */
    public boolean setData(final SGData data) {

        if (!(data instanceof SGSXYZSDArrayData)) {
            throw new IllegalArgumentException("!(data instanceof SGSXYZData)");
        }

        if (super.setData(data) == false) {
            return false;
        }

        SGSXYZSDArrayData dataSXYZ = (SGSXYZSDArrayData) data;
        this.mXIndex = dataSXYZ.mXIndex;
        this.mYIndex = dataSXYZ.mYIndex;
        this.mZIndex = dataSXYZ.mZIndex;

        return true;
    }

    /**
     * Returns a copy of x-value array.
     *
     * @return
     *         x-value array
     */
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
    	ret = this.getNumberArray(this.mXIndex, b);
    	if (useCache) {
            SGSXYZDataCache.setXValues(this, ret);
    	}
    	return ret;
    }

    /**
     * Returns a copy of y-value array.
     *
     * @return
     *         y-value array
     */
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
    	ret = this.getNumberArray(this.mYIndex, b);
    	if (useCache) {
            SGSXYZDataCache.setYValues(this, ret);
    	}
    	return ret;
    }

    /**
     * Returns a copy of z-value array.
     *
     * @return
     *         z-value array
     */
    @Override
    public double[] getZValueArray(final boolean all) {
        return SGDataUtility.getZValueArray(this, all);
    }
    
	@Override
	public boolean useZValueCache(final boolean all) {
		return this.useCache(all);
	}
    
    @Override
    public double[] getZValueArray(final boolean all, final boolean useCache) {
    	double[] ret = null;
    	final boolean b = (all || !this.isStrideAvailable());
    	ret = this.getNumberArray(this.mZIndex, b);
    	if (useCache) {
            SGSXYZDataCache.setZValues(this, ret);
    	}
    	return ret;
    }

    /**
     * Returns an array of xy-values.
     *
     * @return an array of xy-values
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
     * Returns an array of x-values.
     * @return
     *         an array of x-values
     */
    protected double[] getXArray() {
		return this.getNumberArray(this.mXIndex, false);
    }

    /**
     * Returns an array of y-values.
     * @return
     *         an array of y-values
     */
    protected double[] getYArray() {
		return this.getNumberArray(this.mYIndex, false);
    }

    /**
     * Returns an array of z-values.
     * @return
     *         an array of z-values
     */
    protected double[] getZArray() {
		return this.getNumberArray(this.mZIndex, false);
    }

    /**
     * Returns the type of data.
     *
     * @return
     *         the type of data
     */
    public String getDataType() {
        return SGDataTypeConstants.SXYZ_DATA;
    }

    /**
     * Returns an array of current column types.
     * @return
     *        an array of current column types
     */
    public String[] getCurrentColumnType() {
        final int colNum = this.getColNum();
        String[] array = new String[colNum];
        for (int ii = 0; ii < array.length; ii++) {
        	array[ii] = "";
        }
        if (this.mXIndex != null) {
            array[this.mXIndex.intValue()] = X_VALUE;
        }
        if (this.mYIndex != null) {
            array[this.mYIndex.intValue()] = Y_VALUE;
        }
        if (this.mZIndex != null) {
            array[this.mZIndex.intValue()] = Z_VALUE;
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
        SGDataColumn[] colArray = new SGDataColumn[3];
        colArray[0] = columns[this.mXIndex.intValue()];
        colArray[1] = columns[this.mYIndex.intValue()];
        colArray[2] = columns[this.mZIndex.intValue()];

        colArray[0].setColumnType(X_VALUE);
        colArray[1].setColumnType(Y_VALUE);
        colArray[2].setColumnType(Z_VALUE);
        return colArray;
    }

    @Override
    public SGDataColumn[] getUsedDataColumnsClone() {
        return this.getExportedColumnsClone();
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
        Integer x = null;
        Integer y = null;
        Integer z = null;
        for (int ii = 0; ii < columns.length; ii++) {
            if (SGDataUtility.isEqualColumnType(X_VALUE, columns[ii])) {
                x = Integer.valueOf(ii);
            } else if (SGDataUtility.isEqualColumnType(Y_VALUE, columns[ii])) {
                y = Integer.valueOf(ii);
            } else if (SGDataUtility.isEqualColumnType(Z_VALUE, columns[ii])) {
                z = Integer.valueOf(ii);
            } else if ("".equals(columns[ii])) {
                continue;
            } else {
            	return false;
            }
        }

        // check the necessary columns
        if (x == null || y == null || z == null) {
        	return false;
        }

        // set to the attributes
        this.mXIndex = x;
        this.mYIndex = y;
        this.mZIndex = z;

        return true;
    }

    /**
     * Returns properties of this data.
     * @return
     *         properties of this data
     */
    public SGProperties getProperties() {
        SGProperties p = new SXYZSDArrayDataProperties();
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
        if ((p instanceof SXYZSDArrayDataProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        SXYZSDArrayDataProperties ep = (SXYZSDArrayDataProperties) p;
        ep.mXIndex = this.mXIndex;
        ep.mYIndex = this.mYIndex;
        ep.mZIndex = this.mZIndex;
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
        if ((p instanceof SXYZSDArrayDataProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        SXYZSDArrayDataProperties ep = (SXYZSDArrayDataProperties) p;
        this.mXIndex = ep.mXIndex;
        this.mYIndex = ep.mYIndex;
        this.mZIndex = ep.mZIndex;
        return true;
    }

    /**
     * Writes data column indices in the attributes.
     *
     * @param el
     *            the Element object
     * @return true if succeeded
     */
    protected boolean writeAttributeColumnIndices(Element el) {
        el.setAttribute(KEY_X_VALUE_COLUMN_INDEX, this.mXIndex.toString());
        el.setAttribute(KEY_Y_VALUE_COLUMN_INDEX, this.mYIndex.toString());
        el.setAttribute(KEY_Z_VALUE_COLUMN_INDEX, this.mZIndex.toString());
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
        el.setAttribute(KEY_X_VALUE_COLUMN_INDEX, Integer.toString(0));
        el.setAttribute(KEY_Y_VALUE_COLUMN_INDEX, Integer.toString(1));
        el.setAttribute(KEY_Z_VALUE_COLUMN_INDEX, Integer.toString(2));
        return true;
    }

    @Override
    protected boolean writeSequentialColumnName(Element el) {
        el.setAttribute(KEY_X_VALUE_COLUMN_INDEX, this.getSequentialColumnName(0));
        el.setAttribute(KEY_Y_VALUE_COLUMN_INDEX, this.getSequentialColumnName(1));
        el.setAttribute(KEY_Z_VALUE_COLUMN_INDEX, this.getSequentialColumnName(2));

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
//        Integer xIndex = null;
//        Integer yIndex = null;
//        Integer zIndex = null;
//        if ((index = this.readIndex(el, KEY_X_VALUE_COLUMN_INDEX)) != null) {
//            if (this.checkColumnIndexRange(index) == false) {
//                return false;
//            }
//            xIndex = index;
//        } else {
//            return false;
//        }
//        if ((index = this.readIndex(el, KEY_Y_VALUE_COLUMN_INDEX)) != null) {
//            if (this.checkColumnIndexRange(index) == false) {
//                return false;
//            }
//            yIndex = index;
//        } else {
//            return false;
//        }
//        if ((index = this.readIndex(el, KEY_Z_VALUE_COLUMN_INDEX)) != null) {
//            if (this.checkColumnIndexRange(index) == false) {
//                return false;
//            }
//            zIndex = index;
//        } else {
//            return false;
//        }
//
//        // set to attributes
//        this.mXIndex = xIndex;
//        this.mYIndex = yIndex;
//        this.mZIndex = zIndex;
//
//        return true;
//    }

    /**
     * A class for scalar xyz-type data properties.
     *
     */
    public static class SXYZSDArrayDataProperties extends SDArrayDataProperties {

        // indices of data columns
        Integer mXIndex = null;

        Integer mYIndex = null;

        Integer mZIndex = null;

        /**
         * The default constructor.
         */
        public SXYZSDArrayDataProperties() {
            super();
        }

    	@Override
    	public boolean equals(Object obj) {
            if ((obj instanceof SXYZSDArrayDataProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
            	return false;
            }
            return true;
        }

    	@Override
        public boolean hasEqualSize(DataProperties dp) {
            if ((dp instanceof SXYZSDArrayDataProperties) == false) {
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
            if ((dp instanceof SXYZSDArrayDataProperties) == false) {
                return false;
            }
            SXYZSDArrayDataProperties p = (SXYZSDArrayDataProperties) dp;
            if (!SGUtility.equals(this.mXIndex, p.mXIndex)) {
            	return false;
            }
            if (!SGUtility.equals(this.mYIndex, p.mYIndex)) {
            	return false;
            }
            if (!SGUtility.equals(this.mZIndex, p.mZIndex)) {
            	return false;
            }
            return true;
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mXIndex = null;
            this.mYIndex = null;
            this.mZIndex = null;
        }
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
     * Returns the bounds of z-values.
     *
     * @return the bounds of z-values
     */
    public SGValueRange getBoundsZ() {
        return SGDataUtility.getBoundsZ(this);
    }

    /**
     * Returns the title of X values.
     *
     * @return
     *         the title of X values
     */
    public String getTitleX() {
        if (this.mXIndex == null) {
            return "";
        } else {
        	SGDataColumn[] columns = this.getDataFile().mDataColumns;
            return columns[this.mXIndex.intValue()].getTitle();
        }
    }

    /**
     * Returns the title of Y values.
     *
     * @return
     *         the title of Y values
     */
    public String getTitleY() {
        if (this.mYIndex == null) {
            return "";
        } else {
        	SGDataColumn[] columns = this.getDataFile().mDataColumns;
            return columns[this.mYIndex.intValue()].getTitle();
        }
    }

    /**
     * Returns the title of Z values.
     *
     * @return
     *         the title of Z values
     */
    public String getTitleZ() {
        if (this.mZIndex == null) {
            return "";
        } else {
        	SGDataColumn[] columns = this.getDataFile().mDataColumns;
            return columns[this.mZIndex.intValue()].getTitle();
        }
    }

	public SGDataColumn getXColumn() {
		return this.getColumn(this.mXIndex);
	}

	public SGDataColumn getYColumn() {
		return this.getColumn(this.mYIndex);
	}

	public SGDataColumn getZColumn() {
		return this.getColumn(this.mZIndex);
	}

    /**
     * Returns the list of blocks of z-values.
     *
     * @return the list of blocks of z-values
     */
	@Override
    public List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockList() {
		// always returns null;
		return null;
	}

    /**
     * Returns a text string of the data type to save into a NetCDF data set file.
     *
     * @return a text string of the data type to save into a NetCDF data set file
     */
    @Override
    public String getNetCDFDataSetDataType() {
    	return SGDataTypeConstants.SXYZ_NETCDF_DATA;
    }

    /**
     * Sets the stride of data arrays.
     *
     * @param map
     *           the map of the stride
     */
    @Override
    public void setStrideMap(Map<String, SGIntegerSeriesSet> map) {
    	this.mStride = map.get(SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE);
    }

    /**
     * Returns a map of stride for data arrays.
     *
     * @return a map of stride for data arrays
     */
	@Override
    protected Map<String, SGIntegerSeriesSet> getStrideMap() {
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    	map.put(SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE, this.getStride());
    	return map;
    }
	
	class Rect {
		double x;
		double y;
		double z;
		Rect(double x, double y, double z) {
			super();
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public boolean equals(Object obj) {
			if (!(obj instanceof Rect)) {
				return false;
			}
			Rect r = (Rect) obj;
			if (this.x != r.x) {
				return false;
			}
			if (this.y != r.y) {
				return false;
			}
			if (this.z != r.z) {
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

        String xName = "X";
        String yName = "Y";
        String zName = "Z";
        
		SGSXYZDataBuffer sxyzBuffer = (SGSXYZDataBuffer) this.getDataBuffer(policy);
		final int len = sxyzBuffer.getLength();
		double[] xValues = sxyzBuffer.getXValues();
		double[] yValues = sxyzBuffer.getYValues();
		double[] zValues = sxyzBuffer.getZValues();

		// grid plot
		/*
		Map<Double, List<Rect>> xMap = new TreeMap<Double, List<Rect>>();
		Map<Double, List<Rect>> yMap = new TreeMap<Double, List<Rect>>();
		for (int ii = 0; ii < len; ii++) {
			final double x = xValues[ii];
			final double y = yValues[ii];
			final double z = zValues[ii];
			Rect r = new Rect(x, y, z);
			
			List<Rect> xList = xMap.get(x);
			if (xList == null) {
				xList = new ArrayList<Rect>();
				xMap.put(x, xList);
			}
			xList.add(r);
			
			List<Rect> yList = yMap.get(y);
			if (yList == null) {
				yList = new ArrayList<Rect>();
				yMap.put(y, yList);
			}
			yList.add(r);
			
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

		Variable zVar = new Variable(ncWrite, null, null, zName, DataType.DOUBLE, dimString);
		zVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
				SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		ncWrite.addVariable(null, zVar);
		
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
		
		Array zArray = Array.factory(DataType.DOUBLE, new int[] { xLen, yLen });
		Index zIndex = zArray.getIndex();
		for (int yy = 0; yy < yLen; yy++) {
			List<Rect> yList = yMap.get(yValueList.get(yy));
			for (int xx = 0; xx < xLen; xx++) {
				List<Rect> xList = xMap.get(xValueList.get(xx));
				zIndex.set(xx, yy);
				Rect common = null;
				for (Rect yValue : yList) {
					for (Rect xValue : xList) {
						if (xValue.equals(yValue)) {
							common = xValue;
							break;
						}
					}
				}
				zArray.setDouble(zIndex, common.z);
			}
		}
		ncWrite.write(zName, zArray);
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
		
		Variable zVar = new Variable(ncWrite, null, null, zName, DataType.DOUBLE, indexName);
		zVar.addAttribute(new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, 
				SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER));
		ncWrite.addVariable(null, zVar);
		
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

		Array zArray = Array.factory(DataType.DOUBLE, new int[] { len });
		for (int ii = 0; ii < len; ii++) {
			zArray.setDouble(ii, zValues[ii]);
		}
		ncWrite.write(zName, zArray);
		
		return true;
	}
	
	@Override
    public boolean saveToDataSetNetCDFFile(final File file) {
        final int dataNum = this.getAllPointsNumber();

        SGDataColumn[] colArray = this.getExportedColumnsClone();
        if (colArray.length<3) {
            return false;
        }
        SGNumberDataColumn colx = (SGNumberDataColumn) colArray[0];
        SGNumberDataColumn coly = (SGNumberDataColumn) colArray[1];
        SGNumberDataColumn colz = (SGNumberDataColumn) colArray[2];
        
        double[] xvalues = colx.getArray();
        double[] yvalues = coly.getArray();
        double[] zvalues = colz.getArray();

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
            Variable varz = new Variable(ncfile, null, null, "column2", DataType.DOUBLE, indexDimName);
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
            title = colz.getTitle();
            if (null!=title && "".equals(title.trim())==false) {
                Attribute attr = new Attribute(ATTRIBUTE_KEY_LONG_NAME, title);
                varz.addAttribute(attr);
            }

            ncfile.addVariable(null, varx);
            ncfile.addVariable(null, vary);
            ncfile.addVariable(null, varz);
            
            String[] varNames = new String[colArray.length];
            varNames[0] = varx.getShortName();
            varNames[1] = vary.getShortName();
            varNames[2] = varz.getShortName();

            varx.addAttribute(SGDataUtility.getValueTypeAttribute(colx.getValueType()));
            vary.addAttribute(SGDataUtility.getValueTypeAttribute(coly.getValueType()));
            varz.addAttribute(SGDataUtility.getValueTypeAttribute(colz.getValueType()));

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

            Array zarray = Array.factory(DataType.DOUBLE, new int[] { dataNum });
            for (int i = 0; i < zvalues.length; i++) {
                zarray.setDouble(i, zvalues[i]);
            }
            ncfile.write(varNames[2], zarray);

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
		double[] zValues = this.getZValueArray(all, false);
		
		// set edited values
		if (edit) {
			SGIntegerSeriesSet stride = this.getStride();
			int[] indices = stride.getNumbers();
			for (int ii = 0; ii < this.mEditedDataValueList.size(); ii++) {
				SGDataValueHistory dataValue = this.mEditedDataValueList.get(ii);
				String columnType = dataValue.getColumnType();
				final int index = dataValue.getRowIndex();
				final int arrayIndex = all ? index : Arrays.binarySearch(indices, index);
				final double value = dataValue.getValue();
				double[] values;
				if (X_VALUE.equals(columnType)) {
					values = xValues;
				} else if (Y_VALUE.equals(columnType)) {
					values = yValues;
				} else if (Z_VALUE.equals(columnType)) {
					values = zValues;
				} else {
					throw new Error("Invalid column type: " + columnType);
				}
				values[arrayIndex] = value;
			}
		}

		return new SGSXYZDataBuffer(xValues, yValues, zValues);
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
		SGSXYZDataBuffer sxyzBuffer = (SGSXYZDataBuffer) this.getDataBuffer(
				new SGDataBufferPolicy(true, false, true));
		final int len = sxyzBuffer.getLength();
		double[] xValues = sxyzBuffer.getXValues();
		double[] yValues = sxyzBuffer.getYValues();
		double[] zValues = sxyzBuffer.getZValues();
		
        String xName = "X";
        String yName = "Y";
        String zName = "Z";

		// grid plot
		Map<Double, List<Rect>> xMap = new TreeMap<Double, List<Rect>>();
		Map<Double, List<Rect>> yMap = new TreeMap<Double, List<Rect>>();
		for (int ii = 0; ii < len; ii++) {
			final double x = xValues[ii];
			final double y = yValues[ii];
			Rect value = new Rect(xValues[ii], yValues[ii], zValues[ii]);
			
			List<Rect> xList = xMap.get(x);
			if (xList == null) {
				xList = new ArrayList<Rect>();
				xMap.put(x, xList);
			}
			xList.add(value);
			
			List<Rect> yList = yMap.get(y);
			if (yList == null) {
				yList = new ArrayList<Rect>();
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
		
		MDDoubleArray zArray = new MDDoubleArray(new int[] { xLen, yLen });
		for (int yy = 0; yy < yLen; yy++) {
			List<Rect> yList = yMap.get(yValueList.get(yy));
			for (int xx = 0; xx < xLen; xx++) {
				List<Rect> xList = xMap.get(xValueList.get(xx));
				Rect common = null;
				for (Rect yValue : yList) {
					for (Rect xValue : xList) {
						if (xValue.equals(yValue)) {
							common = xValue;
							break;
						}
					}
				}
				zArray.set(common.z, xx, yy);
			}
		}
		writer.writeDoubleMDArray(zName, zArray);
		
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
		
		String strX = Integer.toString(this.mXIndex + 1);
		varList.add(strX);
		columnTypeList.add(X_VALUE);
		
		String strY = Integer.toString(this.mYIndex + 1);
		varList.add(strY);
		columnTypeList.add(Y_VALUE);
		
		String strZ = Integer.toString(this.mZIndex + 1);
		varList.add(strZ);
		columnTypeList.add(Z_VALUE);
		
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
     * Returns the bounds of z-values for all animation frames.
     * 
     * @return the bounds of z-values
     */
	@Override
    public SGValueRange getAllAnimationFrameBoundsZ() {
		return SGDataUtility.getAllAnimationFrameBoundsZ(this);
	}

    /**
     * Returns the array of column types.
     * 
     * @return the array of column types
     */
	@Override
    public String[] getDataViewerColumnTypes() {
    	List<String> list = new ArrayList<String>();
		list.add(SGIDataColumnTypeConstants.X_VALUE);
		list.add(SGIDataColumnTypeConstants.Y_VALUE);
		list.add(SGIDataColumnTypeConstants.Z_VALUE);
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
    public Double getZValueAt(final int index) {
		double[] values = this.getZValueArray(false);
		return values[index];
	}

	@Override
    public Double getZValueAt(final int row, final int col) {
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
		// always returns null
		return null;
	}

	@Override
	public SGIntegerSeriesSet getYStride() {
		// always returns null
		return null;
	}

	@Override
	public String getToolTipSpatiallyVaried(int index) {
		return this.getIndexToolTipSpatiallyVaried(index);
	}

	@Override
	public String getToolTipSpatiallyVaried(int index0, int index1) {
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
    	this.getZValueArray(all, useCache);
	}

	@Override
	public List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockListSub(
			final boolean all, final boolean useCache, 
			final boolean removeInvalidValues) {
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
	public double[] getZValueArray(boolean all, boolean useCache,
			boolean removeInvalidValues) {
		return this.getZValueArray(all, useCache);
	}

	@Override
	protected boolean matches(final int col, final int row, String columnType,
			SGDataValueHistory value, final double d) {
		return SGDataUtility.matches(col, row, columnType, value, d);
	}

	@Override
	public List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockList(
			final boolean all, final boolean useCache, 
			final boolean removeInvalidValues) {
		// always returns null
		return null;
	}

}
