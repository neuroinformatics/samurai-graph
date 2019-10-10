package jp.riken.brain.ni.samuraigraph.base;


/**
 * The set of data columns.
 * 
 */
public class SGDataColumnInfoSet implements Cloneable {
	
	/**
	 * The array of data column information.
	 */
	private SGDataColumnInfo[] mDataColumnInfoArray = null;

	/**
	 * Builds a data column set.
	 * 
	 * @param infoArray
	 *                   the array of data column information
	 */
	public SGDataColumnInfoSet(final SGDataColumnInfo[] infoArray) {
		super();
		if (infoArray == null) {
			throw new IllegalArgumentException("infoArray == null");
		}
		this.mDataColumnInfoArray = (SGDataColumnInfo[]) infoArray.clone();
	}

	/**
	 * Returns the array of data column information.
	 * 
	 * @return the array of data column information
	 */
	public SGDataColumnInfo[] getDataColumnInfoArray() {
		if (this.mDataColumnInfoArray == null) {
			return new SGDataColumnInfo[] {};
		} else {
			return (SGDataColumnInfo[]) this.mDataColumnInfoArray.clone();
		}
	}

    /**
     * Clones this data column.
     * 
     * @return copy of this data column
     */
    public Object clone() {
    	SGDataColumnInfoSet set = null;
        try {
        	set = (SGDataColumnInfoSet) super.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
        return set;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("[");
        sb.append(this.paramString());
        sb.append("]");
    	return sb.toString();
    }
    
    protected String paramString() {
        if (this.mDataColumnInfoArray == null) {
            return "[SGDataColumnInfoSet]";
        }
        StringBuffer sb = new StringBuffer();
        sb.append('\n');
        for (int ii = 0; ii < this.mDataColumnInfoArray.length; ii++) {
        	if (ii > 0) {
        		sb.append(", \n");
        	}
            sb.append(this.mDataColumnInfoArray[ii].toString());
        }
        sb.append('\n');
        return sb.toString();
    }
}
