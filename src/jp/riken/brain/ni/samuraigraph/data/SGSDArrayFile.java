package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGAttribute;

/**
 * A class of the text type data file.
 *
 */
public class SGSDArrayFile extends SGDataSource {

    /**
     * The array of data columns.
     */
    protected SGDataColumn[] mDataColumns = null;
    
    /**
     * The path of data file.
     */
    private String mFilePath = null;
    
    /**
     * Builds an object for a text data file.
     * 
     * @param filePath
     *           the file path
     * @param columns
     *           the data columns
     */
    public SGSDArrayFile(String filePath, SGDataColumn[] columns) {
        super();
        
        if (filePath == null) {
            throw new IllegalArgumentException("filePath == null");
        }
        if (columns == null) {
            throw new IllegalArgumentException("columns == null");
        }
        for (int ii = 0; ii < columns.length; ii++) {
            if (columns[ii] == null) {
                throw new IllegalArgumentException("columnsArray[" + ii + "] == null");
            }
        }
        
        // length check
        if (columns.length == 0) {
            throw new IllegalArgumentException("columnsArray.length == 0");
        }
        
        // length check for all columns
        if (columns.length > 1) {
            final int len0 = columns[0].getLength();
            for (int ii = 1; ii < columns.length; ii++) {
                final int len = columns[ii].getLength();
                if (len != len0) {
                    throw new IllegalArgumentException("Data length is illegal.");
                }
            }
        }

        // set to attributes
        this.mDataColumns = (SGDataColumn[]) columns.clone();
        this.mFilePath = filePath;
    }
    
    /**
     * Returns the file path.
     * 
     * @return the file path
     */
    public String getPath() {
        return this.mFilePath;
    }

    /**
     * Returns the array of data columns.
     * 
     * @return the array of data columns
     */
    public SGDataColumn[] getDataColumns() {
        return (SGDataColumn[]) this.mDataColumns.clone();
    }
	
	/**
	 * Sets the data columns.
	 * 
	 * @param columns
	 *            the data columns to set
	 */
	public void setDataColumns(final SGDataColumn[] columns) {
		if (columns == null) {
			throw new IllegalArgumentException("columns == null");
		}
        this.mDataColumns = new SGDataColumn[columns.length];
        for (int ii = 0; ii < columns.length; ii++) {
        	this.mDataColumns[ii] = (SGDataColumn) columns[ii].clone();
        }
	}
	
    /**
     * Clones this data file.
     * 
     * @return copy of this data file
     */
    public Object clone() {
    	SGSDArrayFile dataFile = null;
        try {
            dataFile = (SGSDArrayFile) super.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
        if (this.mDataColumns != null) {
            SGDataColumn[] colArray = new SGDataColumn[this.mDataColumns.length];
            for (int ii = 0; ii < colArray.length; ii++) {
                colArray[ii] = (SGDataColumn) this.mDataColumns[ii].clone();
            }
            dataFile.mDataColumns = colArray;
        }
        return dataFile;
    }

    /**
     * Disposes of this object.
     *
     */
    public void dispose() {
    	super.dispose();
    	if (this.mDataColumns != null) {
    		for (int ii = 0; ii < this.mDataColumns.length; ii++) {
    			this.mDataColumns[ii].dispose();
    		}
    	}
    	this.mDataColumns = null;
    }
    
    /**
     * Returns the number of columns.
     * 
     * @return the number of columns or -1 if data columns do not exist
     */
    public int getColNum() {
        if (this.mDataColumns == null) {
            return -1;
        } else {
            return this.mDataColumns.length;
        }
    }

    /**
     * Returns the length of data.
     * 
     * @return the length of data
     */
    public int getLength() {
    	return this.mDataColumns[0].getLength();
    }

    /**
     * Returns the number of data columns of a given class.
     * 
     * @param cl
     *           a class object
     * @return the number of data columns of a given class
     */
    public int getColNum(Class<?> cl) {
    	SGDataColumn[] columns = this.mDataColumns;
        int cnt = 0;
        for (int ii = 0; ii < columns.length; ii++) {
            if (columns[ii].getClass().isAssignableFrom(cl)) {
                cnt++;
            }
        }
        return cnt;
    }

    /**
	 * Returns the number of rows of each column.
	 * 
	 * @return the number of rows of each column or -1 if data columns do not
	 *         exist
	 */
    public int getRowNum() {
        if (this.mDataColumns == null || this.mDataColumns.length == 0) {
            return -1;
        } else {
            return this.mDataColumns[0].getLength();
        }
    }

    /**
     * Returns an array of data column indices of given class.
     * @param cl
     *           a class object
     * @return an array of column indices
     */
    public int[] getIndexArray(Class<?> cl) {
    	SGDataColumn[] columns = this.mDataColumns;
        List<Integer> indexList = new ArrayList<Integer>();
        for (int ii = 0; ii < columns.length; ii++) {
            if (columns[ii].getClass().isAssignableFrom(cl)) {
                indexList.add(Integer.valueOf(ii));
            }
        }
        int[] array = new int[indexList.size()];
        for (int ii = 0; ii < indexList.size(); ii++) {
            Integer value = (Integer) indexList.get(ii);
            array[ii] = value.intValue();
        }
        return array;
    }

    /**
     * Returns an array of titles of the columns.
     * 
     * @return an array of titles of the columns
     */
    public String[] getTitles() {
    	SGDataColumn[] columns = this.mDataColumns;
        String[] titles = new String[columns.length];
        for (int ii = 0; ii < columns.length; ii++) {
            titles[ii] = columns[ii].getTitle();
        }
        return titles;
    }

    /**
     * Returns the title of a column at given index.
     * 
     * @param colIndex
     *                the column index
     * @return the title
     */
    public String getTitle(final int colIndex) {
    	SGDataColumn[] columns = this.mDataColumns;
        if (colIndex < 0 || colIndex >= this.getColNum()) {
            throw new IllegalArgumentException("Index out of bounds: " + colIndex);
        }
        return columns[colIndex].getTitle();
    }
    
    public boolean isEmptyTitle(final int colIndex) {
        if (null==this.getTitle(colIndex) || this.getTitle(colIndex).trim().equals("")) {
            return true;
        }
        return false;
    }
    
    public boolean isRepeatedTitle(final int colIndex) {
        String[] titles = this.getTitles();
        if (null==titles[colIndex]) {
            return true;
        }
        for (int i = 0; i < titles.length; i++) {
            if (colIndex!=i && titles[colIndex].equals(titles[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an array of value types of the columns.
     * 
     * @return an array of value types of the columns
     */
    public String[] getValueTypes() {
    	SGDataColumn[] columns = this.mDataColumns;
        String[] types = new String[columns.length];
        for (int ii = 0; ii < columns.length; ii++) {
        	types[ii] = columns[ii].getValueType();
        }
        return types;
    }

    /**
     * Checks whether a given column index is within array bounds.
     * 
     * @param index
     *              the column index
     * @return true if the given column index is within array bounds
     */
    protected boolean checkColumnIndexRange(final Integer index) {
    	SGDataColumn[] columns = this.mDataColumns;
        final int num = index.intValue();
        return (0 <= num && num < columns.length);
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
    	SGDataColumn[] columns = this.mDataColumns;
        if (colIndex < 0 || colIndex >= columns.length) {
            throw new IllegalArgumentException("Index out of bounds: "
                    + colIndex);
        }
        columns[colIndex] = col;
    }
    
    /**
     * Returns the list of attributes.
     * 
     * @return the list of attributes
     */
	public List<SGAttribute> getAttributes() {
		// returns an empty list
		List<SGAttribute> aList = new ArrayList<SGAttribute>();
		return aList;
	}

}
