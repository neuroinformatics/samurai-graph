package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDisposable;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;

/**
 * The base class of data column.
 *
 */
public abstract class SGDataColumn implements Cloneable, SGIDisposable, SGIConstants {

    /**
     * The title of this column.
     */
    protected String mTitle = null;
    
    /**
     * The type of column such as x-value, tick label and so on.
     */
    protected String mColumnType = null;
    
    /**
     * The default constructor.
     */
    public SGDataColumn() {
        super();
    }
    
    /**
     * Builds this object.
     * @param title
     *              a text string for the title
     */
    public SGDataColumn(String title) {
        super();
        this.setTitle(title);
    }
    
    /**
     * Clones this data column.
     * 
     * @return
     *         shallow copy of this data column
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
    
    /**
     * Returns the length of data column.
     * @return
     *         the length of data column
     */
    public abstract int getLength();
    
    /**
     * Returns the value type of this column.
     * @return
     *         the value type of this column
     */
    public abstract String getValueType();
    
    /**
     * Returns the title of this column.
     * @return
     *         the title of this column
     */
    public String getTitle() {
        if (this.mTitle != null) {
            return this.mTitle;
        } else {
            return null;
        }
    }
    
    /**
     * Sets the title.
     * @param title
     *              a text string for the title
     */
    public void setTitle(final String title) {
        if (title != null) {
            this.mTitle = title;
        } else {
            this.mTitle = null;
        }
    }
    
    /**
     * Returns the type of this column.
     * @return
     *         the type of this column
     */
    public String getColumnType() {
        return mColumnType;
    }

    /**
     * Sets the type of this column.
     * @param columnType
     *                   a value to set to the type of this column
     */
    public void setColumnType(String columnType) {
        this.mColumnType = columnType;
    }
    
    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
        this.mTitle = null;
    }

    // The flag whether this object is already disposed of.
    private boolean mDisposed = false;

    /**
     * Returns whether this object is already disposed of.
     * 
     * @return true if this object is already disposed of
     */
    public boolean isDisposed() {
    	return this.mDisposed;
    }

    /**
     * Returns a value in this data column at given row index.
     * 
     * @param rowIndex
     *            the row index
     * @return a value at given row index
     */
    public abstract Object getValue(final int rowIndex);
    
    /**
     * Returns an array of text strings.
     * 
     * @return an array of text strings
     */
    public abstract String[] getStringArray();

    /**
     * Returns an array of text strings with given stride.
     * 
     * @param stride
     *           the stride of an array
     * @return an array of text strings
     */
    public abstract String[] getStringArray(SGIntegerSeriesSet stride);

    /**
     * Calculates available array indices for given stride within given length of an array.
     * 
     * @param stride
     *           the stride of an array
     * @param len
     *           the length of an array
     * @return available array indices
     */
    protected int[] calcStrideIndices(final SGIntegerSeriesSet stride, final int len) {
    	if (stride == null) {
    		return SGIntegerSeriesSet.createInstance(len).getNumbers();
    	}
    	final int[] indices = stride.getNumbers();
    	List<Integer> indexList = new ArrayList<Integer>();
    	for (int ii = 0; ii < indices.length; ii++) {
    		if (indices[ii] >= len) {
    			break;
    		}
			indexList.add(indices[ii]);
    	}
    	final int[] ret = new int[indexList.size()];
    	for (int ii = 0; ii < ret.length; ii++) {
    		ret[ii] = indexList.get(ii);
    	}
    	return ret;
    }

}
