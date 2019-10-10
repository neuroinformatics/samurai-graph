package jp.riken.brain.ni.samuraigraph.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGBufferedFileWriter;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGITextDataConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

import org.w3c.dom.Element;

import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;
import ch.systemsx.cisd.hdf5.HDF5FactoryProvider;
import ch.systemsx.cisd.hdf5.IHDF5Writer;

/**
 * An abstract data class which has arrays of numbers and strings.
 *
 */
public abstract class SGSDArrayData extends SGArrayData implements SGITextDataConstants,
        SGIConstants, SGIDataColumnTypeConstants {

	/**
	 * The stride of an array.
	 */
	protected SGIntegerSeriesSet mStride = null;
	
    /**
     * The default constructor.
     */
    public SGSDArrayData() {
        super();
    }

	/**
	 * Build a data object.
	 * 
	 * @param dataFile
	 *            the text data file
	 * @param obs
	 *            a data observer
	 * @param strideAvailable
	 *            flag whether to set available the stride
	 */
	public SGSDArrayData(final SGSDArrayFile dataFile,
			final SGDataSourceObserver obs, final boolean strideAvailable) {
		super(dataFile, obs, strideAvailable);
	}

    /**
     * Clones this data object.
     * 
     * @return
     *         shallow copy of this data object
     */
    public Object clone() {
    	SGSDArrayData data = (SGSDArrayData) super.clone();
    	data.mStride = this.getStride();
    	return data;
    }
    
    /**
     * Returns the number of columns.
     * 
     * @return
     *        the number of columns or -1 if data columns do not exist
     */
    public int getColNum() {
    	return this.getDataFile().getColNum();
    }
    
    /**
     * Returns the number of rows of each column.
     * 
     * @return
     *        the number of rows of each column or -1 if data columns do not exist
     */
    public int getRowNum() {
    	return this.getDataFile().getRowNum();
    }
    
    /**
     * Disposes of this data object.
     * Do not call any method of this object after called this method.
     * 
     */
    public void dispose() {
        super.dispose();
        this.mStride = null;
    }
    
    /**
     * Returns an array of data columns to export.
     * 
     * @return an array of data columns to export
     */
    public abstract SGDataColumn[] getExportedColumns();
    
    public SGDataColumn[] getExportedColumnsClone() {
        SGDataColumn[] cols = this.getExportedColumns();
        SGDataColumn[] columns = new SGDataColumn[cols.length];
        for (int i = 0; i < cols.length; i++) {
            columns[i] = (SGDataColumn) cols[i].clone();
        }
        return columns;
    }
    
    /**
     * Returns an array of data columns that are used in a group set.
     * 
     * @return an array of data columns that are used in a group set
     */
    public abstract SGDataColumn[] getUsedDataColumnsClone();
    
    /**
     * Returns the number of data columns of a given class.
     * 
     * @param cl
     *           a class object
     * @return the number of data columns of a given class
     */
    public int getColNum(Class<?> cl) {
    	return this.getDataFile().getColNum(cl);
    }
    
    /**
     * Returns the number of data points taking into account the stride.
     * 
     * @return the number of data points taking into account the stride
     */
    @Override
	public int getPointsNumber() {
		if (this.isStrideAvailable()) {
			return this.mStride.getLength();
		} else {
			return this.getAllPointsNumber();
		}
	}

    /**
     * Returns the number of data points without taking into account the stride.
     * 
     * @return the number of data points without taking into account the stride
     */
    @Override
    public int getAllPointsNumber() {
    	SGSDArrayFile sdFile = this.getDataFile();
    	SGDataColumn[] columns = sdFile.getDataColumns();
    	if (columns.length == 0) {
    		throw new Error("There are no data columns.");
    	}
    	return columns[0].getLength();
    }

    /**
     * Returns an array of data column indices of given class.
     * 
     * @param cl
     *           a class object
     * @return an array of column indices
     */
    public int[] getIndexArray(Class<?> cl) {
    	return this.getDataFile().getIndexArray(cl);
    }
    
    /**
     * Sets a given data.
     * 
     * @param data
     *           a data
     * @return true if succeeded
     */
    public boolean setData(final SGData data) {
    	if (!super.setData(data)) {
    		return false;
    	}
        if (!(data instanceof SGSDArrayData)) {
            throw new IllegalArgumentException("!(data instanceof SGArrayData)");
        }
        SGSDArrayData sdData = (SGSDArrayData) data;
        this.mStride = sdData.getStride();
        return true;
    }
    
    /**
     * Returns an array of titles of the columns.
     * 
     * @return an array of titles of the columns
     */
    public String[] getTitles() {
    	return this.getDataFile().getTitles();
    }
    
    /**
     * Returns the title of a column at given index.
     * 
     * @param colIndex
     *                the column index
     * @return the title
     */
    public String getTitle(final int colIndex) {
    	return this.getDataFile().getTitle(colIndex);
    }

    /**
     * Returns an array of value types of the columns.
     * 
     * @return an array of value types of the columns
     */
    public String[] getValueTypes() {
    	return this.getDataFile().getValueTypes();
    }

    /**
     * Get properties of this data.
     * @param p
     *          properties to set values
     * @return
     *          true if succeeded
     */
    public boolean getProperties(final SGProperties p) {
        if ((p instanceof SDArrayDataProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
        	return false;
        }
        SDArrayDataProperties ap = (SDArrayDataProperties) p;
        ap.mStride = this.getStride();
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
        if ((p instanceof SDArrayDataProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
        	return false;
        }
        SDArrayDataProperties ap = (SDArrayDataProperties) p;
        this.setStride(ap.mStride);
        return true;
    }
    
    /**
     * A class for array data properties.
     */
    public static abstract class SDArrayDataProperties extends ArrayDataProperties {

    	SGIntegerSeriesSet mStride = null;
    	
        /**
         * The default constructor.
         */
        public SDArrayDataProperties() {
            super();
        }
        
        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mStride = null;
        }
        
        @Override
        public boolean equals(Object obj) {
            if ((obj instanceof SDArrayDataProperties) == false) {
                return false;
            }
        	if (super.equals(obj) == false) {
        		return false;
        	}
        	return true;
        }

    	@Override
        public boolean hasEqualSize(DataProperties dp) {
            if ((dp instanceof SDArrayDataProperties) == false) {
                return false;
            }
            SDArrayDataProperties p = (SDArrayDataProperties) dp;
            if (SGUtility.equals(this.mStride, p.mStride) == false) {
            	return false;
            }
        	return true;
        }

        /**
         * Copy this object.
         * @return a copied object
         */
        public Object copy() {
            Object obj = super.copy();
            SDArrayDataProperties p = (SDArrayDataProperties) obj;
        	p.mStride = (this.mStride != null) ? (SGIntegerSeriesSet) this.mStride.clone() : null;
            return p;
        }
    }
    
    /**
     * Read and returns the column index.
     * 
     * @param el
     *            an Element object
     * @param key
     *            the key of index property
     * @return the index if it exists
     */
    protected Integer readIndex(Element el, String key) {
        Integer num = null;
        String str = el.getAttribute(key);
        if (str.length() != 0) {
            num = SGUtilityText.getInteger(str);
        }
        return num;
    }

    /**
     * Read and returns the column indices.
     * 
     * @param el
     *            an Element object
     * @param key
     *            the key of index property
     * @return the indices it exists
     */
    protected Integer[] readIndices(Element el, String key) {
    	return SGUtility.readIndices(el, key);
    }

    protected abstract Object[][] getDataFileExportValues(final SGExportParameter mode,
    		SGDataBufferPolicy policy);

    protected abstract Object[][] getArchiveDataSetExportValues(final SGExportParameter mode);

    /**
     * Checks whether a given column index is within array bounds.
     * 
     * @param index
     *              the column index
     * @return true if the given column index is within array bounds
     */
    protected boolean checkColumnIndexRange(final Integer index) {
    	return this.getDataFile().checkColumnIndexRange(index);
    }
    
    /**
     * Sets the data column.
     * 
     * @param col
     *            the column to be set
     * @param colIndex
     *            the column index
     */
    protected void setDataColumn(final SGDataColumn col, final int colIndex) {
    	this.getDataFile().setDataColumn(col, colIndex);
    }
    
    /**
     * Writes properties of this data object to the Element.
     * 
     * @param el
     *            the Element object
     * @param type
     *            type of the method to save properties
     * @return true if succeeded
     */
    @Override
    public boolean writeProperty(Element el, final SGExportParameter params) {
    	if (super.writeProperty(el, params) == false) {
    		return false;
    	}
    	
    	final String keyStride;

    	OPERATION type = params.getType();
        switch (type) {
        case DUPLICATE_OBJECT:
        case COPY_OBJECT:
        case CUT_OBJECT:
        case SAVE_TO_PROPERTY_FILE:
            // write indices of attributes
            if (this.writeAttributeColumnIndices(el) == false) {
                return false;
            }
            keyStride = SGIDataPropertyKeyConstants.KEY_ARRAY_SECTION;
            break;
        case SAVE_TO_ARCHIVE_DATA_SET:
        case SAVE_TO_ARCHIVE_DATA_SET_107:
            // write sequential column indices
            if (this.writeSequentialColumnIndices(el) == false) {
                return false;
            }
            keyStride = SGIDataPropertyKeyConstants.KEY_ARRAY_SECTION;
            break;
        case SAVE_TO_DATA_SET_NETCDF:
            if (this.writeSequentialColumnName(el) == false) {
                return false;
            }
            keyStride = SGIDataPropertyKeyConstants.KEY_INDEX_ARRAY_SECTION;
            break;
        default:
        	return false;
        }

        // stride
        el.setAttribute(keyStride, this.mStride.toString());

        return true;
    }

    /**
     * Writes data column indices in the attributes.
     * 
     * @param el
     *            the Element object
     * @return true if succeeded
     */
    protected abstract boolean writeAttributeColumnIndices(Element el);

    /**
     * Writes data column indices as sequential numbers.
     * 
     * @param el
     *            the Element object
     * @return true if succeeded
     */
    protected abstract boolean writeSequentialColumnIndices(Element el);
    
    /**
     * Writes data column name for netCDF file as sequential numbers.
     * 
     * @param el
     *            the Element object
     * @return true if succeeded
     */
    protected abstract boolean writeSequentialColumnName(Element el);
    
    /**
     * Return data column name for netCDF file as sequential numbers.
     * 
     * @param index
     * @return sequential data column name for netCDF file
     */
    protected String getSequentialColumnName(final int index) {
        return "column"+index;
    }

	protected SGDataColumn getColumn(final Integer index) {
		if (index != null) {
			return this.getDataFile().mDataColumns[index.intValue()];
		} else {
			return null;
		}
	}

	protected SGDataColumn[] getColumns(final Integer[] indices) {
		if (indices != null) {
			SGDataColumn[] columns = new SGDataColumn[indices.length];
			for (int ii = 0; ii < columns.length; ii++) {
				columns[ii] = this.getDataFile().mDataColumns[indices[ii]];
			}
			return columns;
		} else {
			return null;
		}
	}

    /**
     * Returns a map which has data information.
     * Overrode to set the data type.
     * 
     * @return a map which has data information
     */
    public Map<String, Object> getInfoMap() {
        Map<String, Object> map = super.getInfoMap();
        map.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, this.getDataType());
        return map;
    }

    /**
     * Returns the data file.
     * 
     * @return the data file
     */
    public SGSDArrayFile getDataFile() {
    	return (SGSDArrayFile) this.getDataSource();
    }

    /**
     * Sets the data source.
     * 
     * @param src
     *           a data source
     */
    @Override
    public void setDataSource(SGIDataSource src) {
    	if (!(src instanceof SGSDArrayFile)) {
    		throw new IllegalArgumentException("Invalid data source.");
    	}
    	super.setDataSource(src);
    }

    /**
     * Returns the text data file.
     * 
     * @return the text data file
     */
    public SGSDArrayFile getTextDataFile() {
        return (SGSDArrayFile) this.getDataSource();
    }

    /**
     * Returns the stride.
     * 
     * @return the stride
     */
    public SGIntegerSeriesSet getStride() {
    	if (this.mStride == null) {
    		return null;
    	} else {
        	return (SGIntegerSeriesSet) this.mStride.clone();
    	}
    }

    /**
     * Sets the stride.
     * 
     * @param stride
     *           the stride
     */
    public void setStride(final SGIntegerSeriesSet stride) {
    	if (stride != null) {
        	this.mStride = (SGIntegerSeriesSet) stride.clone();
    	} else {
    		this.mStride = null;
    	}
    }

    /**
     * Returns an array of data column information.
     * 
     * @return an array of data column information
     */
    @Override
    public SGDataColumnInfo[] getColumnInfo() {
        final int colNum = this.getColNum();
        String[] titles = this.getTitles();
        String[] valueTypes = this.getValueTypes();
        String[] colTypes = this.getCurrentColumnType();
        SGDataColumnInfo[] colArray = new SGDataColumnInfo[colNum];
        for (int ii = 0; ii < colNum; ii++) {
            colArray[ii] = new SGSDArrayDataColumnInfo(titles[ii], valueTypes[ii]);
            colArray[ii].setColumnType(colTypes[ii]);
        }
        return colArray;
    }

    protected double[] getNumberArray(final int index, final boolean all) {
    	SGDataColumn[] columns = this.getDataFile().getDataColumns();
    	SGINumberDataColumn col = (SGINumberDataColumn) columns[index];
    	if (all) {
            return col.getNumberArray();
    	} else {
            return col.getNumberArray(this.mStride);
    	}
    }

    /**
     * Exports the title to a given writer.
     *
     * @param w
     *          a writer
     * @throws IOException
     */
    private void exportTitle(final Writer w) throws IOException {
        SGDataColumn[] colArray = this.getExportedColumns();
        boolean titleFlag = false;
        for (int ii = 0; ii < colArray.length; ii++) {
            String title = colArray[ii].getTitle();
            if (title != null) {
                titleFlag = true;
                break;
            }
        }
        if (titleFlag) {
            for (int ii = 0; ii < colArray.length; ii++) {
                String title = colArray[ii].getTitle();
                String str = SGUtilityText.getCSVString(title);
                if (ii != 0) {
                	w.write(',');
                }
            	w.write('\"');
            	if (str != null) {
                    w.write(str);
            	}
            	w.write('\"');
            }
            w.write('\n');
        }
    }

    protected static final String INDEX_DIMENSION_NAME = SGIDataColumnTypeConstants.INDEX;

    protected static final String ATTRIBUTE_KEY_LONG_NAME = "long_name";

    // Creates the dimension of indices.
    protected Dimension addIndexDimension(NetcdfFileWriteable ncfile, int len) {
		Dimension serialNumberDim = new Dimension(INDEX_DIMENSION_NAME, len);
		ncfile.addDimension(null, serialNumberDim);
		return serialNumberDim;
    }
    
	// Creates the variable of indices.
    protected Variable addIndexVarialbe(NetcdfFileWriteable ncfile, Dimension dim) {
		Variable var = new Variable(ncfile, null, null,
				INDEX_DIMENSION_NAME, DataType.INT, dim.getName());
		ncfile.addVariable(null, var);
		return var;
    }
    
    protected void writeIndexVarialbe(NetcdfFileWriteable ncfile,
			Variable var) throws IOException, InvalidRangeException {
		final int len = var.getDimension(0).getLength();
		Array indexArray = Array.factory(DataType.INT, new int[] { len });
		for (int ii = 0; ii < len; ii++) {
			indexArray.setInt(ii, ii);
		}
		ncfile.write(var.getShortName(), indexArray);
	}
    
    /**
     * Exports the data into a file of the same format.
     * 
     * @param file
     *           the file to save
     * @return true if succeeded
     */
    @Override
    public boolean saveToSameFormatFile(final File file, final SGExportParameter mode,
    		SGDataBufferPolicy policy) {
        return this.saveToTextFile(file, mode, policy);
    }

    private boolean writeData(Writer w, final SGExportParameter mode, Object[][] array) throws IOException {
        SGDataColumn[] colArray = this.getExportedColumns();
        StringBuffer sb = new StringBuffer();
        for (int ii = 0; ii < array.length; ii++) {
            final int colNum = array[ii].length;
            if (OPERATION.SAVE_TO_ARCHIVE_DATA_SET_107.equals(mode.getType())) {
                // check whether NaN values exist
                boolean isNaN = false;
                for (int jj = 0; jj < colNum; jj++) {
                    if (colArray[jj] instanceof SGNumberDataColumn) {
                        if (array[ii][jj] instanceof Double) {
                            Double d = (Double) array[ii][jj];
                            if (d.isNaN()) {
                                isNaN = true;
                                break;
                            }
                        }
                    }
                }

                // when NaN values exist
                if (isNaN) {
                    sb.append(DATA_COMMENT_PREFIX);
                    sb.append(DATA_COMMENT_HEADER_PREFIX);
                    sb.append(HEADER_NOT_A_COMMENT_LINE);
                    sb.append(DATA_COMMENT_HEADER_SUFFIX);
                }
            }

            // export values in a row
            for (int jj = 0; jj < colNum; jj++) {
                sb.append(array[ii][jj]);
                if (jj != colNum - 1) {
                    sb.append(',');
                }
            }
            sb.append('\n');
        }
        w.write(sb.toString());
        return true;
    }

    /**
     * Returns a text string of file extension for the file in a data set file.
     * 
     * @return a text string of file extension
     */
    @Override
    public String getDataSetFileExtension() {
    	return SGIDataFileConstants.CSV_FILE_EXTENSION;
    }

    /**
     * Exports the data into a text file.
     * 
     * @param file
     *           the file to save
     * @param mode
     *           the mode to save data
     * @return true if succeeded
     */
    @Override
    public boolean saveToTextFile(final File file, final SGExportParameter mode, SGDataBufferPolicy policy) {
        Object[][] array = this.getDataFileExportValues(mode, policy);
        if (array == null) {
        	return false;
        }
    	return this.doExport(file, mode, array);
    }

    /**
     * Saves the data to a file for archive data set file.
     *
     * @param file
     *             the file to save to
     * @param mode
     *             the mode of saving data
     * @return true if succeeded
     */
    @Override
    public boolean saveToArchiveDataSetFile(final File file, final SGExportParameter mode) {
        Object[][] array = this.getArchiveDataSetExportValues(mode);
        if (array == null) {
        	return false;
        }
    	return this.doExport(file, mode, array);
    }

    private boolean doExport(final File file, final SGExportParameter mode, Object[][] array) {
        SGBufferedFileWriter writer = null;
        try {
            writer = new SGBufferedFileWriter(file.getPath(), SGIConstants.CHAR_SET_NAME_UTF8);
			BufferedWriter bw = writer.getBufferedWriter();
        	this.exportTitle(bw);
            if (!this.writeData(bw, mode, array)) {
            	return false;
            }
        } catch (IOException ex) {
            return false;
        } finally {
        	if (writer != null) {
        		writer.close();
        	}
        }
    	return true;
    }

    /**
     * Saves the data into a NetCDF file.
     * 
     * @param file
     *           the file to save
     * @return true if succeeded
     */
	@Override
    public boolean saveToNetCDFFile(final File file, final SGExportParameter mode, SGDataBufferPolicy policy) {
		NetcdfFileWriteable ncWrite = null;
		try {
			ncWrite = NetcdfFileWriteable.createNew(file.getAbsolutePath());
			if (!this.exportToNetCDFFile(ncWrite, mode, policy)) {
				return false;
			}
		} catch (IOException e) {
			return false;
		} catch (InvalidRangeException e) {
			return false;
		} finally {
			if (ncWrite != null) {
				try {
					ncWrite.close();
				} catch (IOException e) {
				}
			}
		}
		return true;
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
    public abstract boolean exportToNetCDFFile(NetcdfFileWriteable ncWrite, final SGExportParameter mode, 
			SGDataBufferPolicy policy) throws IOException, InvalidRangeException;

    /**
     * Saves the data into a HDF5 file.
     * 
     * @param path
     *           the path of HDF5 file
     * @return true if succeeded
     */
    @Override
    public boolean saveToHDF5File(final File file, final SGExportParameter mode, SGDataBufferPolicy policy) {
        SGDataColumn[] colArray = this.getExportedColumns();
        for (SGDataColumn col : colArray) {
        	if (col instanceof SGDateDataColumn) {
        		// this method is not applicable to the data with date columns
        		return false;
        	}
        }
		if (file.exists()) {
			// deletes existing file to avoid updating the file
			file.delete();
		}
    	IHDF5Writer writer = HDF5FactoryProvider.get().open(file);
    	try {
    		if (!this.exportToHDF5File(writer, colArray)) {
    			return false;
    		}
    	} finally {
    		// closes the writer
    		writer.close();
    	}
    	
    	return true;
    }
    
    protected boolean exportToHDF5File(IHDF5Writer writer, SGDataColumn[] colArray) {
        for (int ii = 0; ii < colArray.length; ii++) {
        	SGDataColumn col = colArray[ii];
        	String title = col.getTitle();
        	String name = title;
        	if (title == null || "".equals(title)) {
        		name = "columns_" + (ii + 1);
        	}
        	if (col instanceof SGNumberDataColumn) {
        		SGNumberDataColumn nCol = (SGNumberDataColumn) col;
        		double[] dArray = nCol.getArray();
        		writer.writeDoubleArray(name, dArray);
        	} else if (col instanceof SGTextDataColumn) {
        		SGTextDataColumn tCol = (SGTextDataColumn) col;
        		String[] strArray = tCol.getStringArray();
        		writer.writeStringArray(name, strArray);
        	}
        }
    	return true;
    }

    /**
     * Exports the data into a MATLAB file.
     * 
     * @param path
     *           the path of MATLAB file
     * @return true if succeeded
     */
    @Override
    public boolean saveToMATLABFile(final File file, final SGExportParameter mode, SGDataBufferPolicy policy) {
    	// TODO
    	return false;
    }

    public abstract Object[][] getValueTable(final SGExportParameter mode, SGDataBufferPolicy policy);

    /**
     * Returns a text string for the command of the origin.
     * 
     * @return a text string for the command of the origin
     */
    @Override
    public String getOriginCommandString() {
    	// always returns null
    	return null;
    }
    
    /**
     * Returns whether animation is available.
     * 
     * @return true if animation is available
     */
    @Override
    public boolean isAnimationAvailable() {
    	// always returns false
    	return false;
    }

    /**
     * Returns the length of animation.
     * 
     * @return the length of animation
     */
    @Override
    public int getAnimationLength() {
    	// always returns -1
    	return -1;
    }

    /**
     * Returns whether the animation is supported in this data.
     * 
     * @return true if the animation is supported in this data
     */
    @Override
    public boolean isAnimationSupported() {
    	return false;
    }

    /**
     * Returns the stride of time.
     *
     * @return the stride of time
     */
	@Override
	public SGIntegerSeriesSet getTimeStride() {
		// always returns null
		return null;
	}

    /**
     * Returns the current index of time value.
     *
     * @return the current index of time value
     */
    @Override
    public int getCurrentTimeValueIndex() {
    	// always returns -1
    	return -1;
    }
    
    /**
     * Sets the current index of time value.
     *
     * @param index
     *           a value to set to the index of time values
     */
    @Override
    public void setCurrentTimeValueIndex(final int index) {
    	// do nothing
    }

    /**
     * Returns whether the index is available.
     *
     * @return true if the index is available
     */
    @Override
	public boolean isIndexAvailable() {
		// always returns true
		return true;
	}

    @Override
    public SGIntegerSeriesSet getIndexStride() {
    	return this.getStride();
    }
    
    /**
     * Returns a text string for the tooltip.
     * 
     * @return a text string for the tooltip
     */
    @Override
    public String getToolTipTextNotSpatiallyVaried() {
    	// always returns null
    	return null;
    }

	public boolean useCache(final boolean all) {
		if (!all) {
			return true;
		}
		if (!this.isStrideAvailable()) {
			return true;
		}
		return this.mStride.isComplete();
	}

	protected String getIndexToolTipSpatiallyVaried(final int index) {
		return Integer.toString(index);
	}
	
	protected String getIndexToolTipSpatiallyVaried(final int index0, final int index1) {
		StringBuffer sb = new StringBuffer();
		sb.append(index0);
		sb.append("-");
		sb.append(index1);
		return sb.toString();
	}

}
