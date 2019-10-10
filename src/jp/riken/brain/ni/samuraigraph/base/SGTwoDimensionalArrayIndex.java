package jp.riken.brain.ni.samuraigraph.base;

public class SGTwoDimensionalArrayIndex implements SGIIndex {
	
	private int mRow = 0;
	
	private int mColumn = 0;

	public SGTwoDimensionalArrayIndex() {
		super();
	}

	public SGTwoDimensionalArrayIndex(final int col, final int row) {
		this();
		this.set(col, row);
	}
	
	public int getRow() {
		return this.mRow;
	}
	
	public int getColumn() {
		return this.mColumn;
	}
	
	public void set(final int col, final int row) {
		this.mColumn = col;
		this.mRow = row;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SGTwoDimensionalArrayIndex)) {
			return false;
		}
		SGTwoDimensionalArrayIndex idx = (SGTwoDimensionalArrayIndex) obj;
		if (idx.mRow != this.mRow) {
			return false;
		}
		if (idx.mColumn != this.mColumn) {
			return false;
		}
		return true;
	}
	
    /**
     * Clones this object.
     * 
     * @return the copy of this object
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
     * Returns a text string that represents this object.
     * 
     * @return a text string that represents this object
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append('(');
    	sb.append(this.mColumn);
    	sb.append(',');
    	sb.append(this.mRow);
    	sb.append(')');
        return sb.toString();
    }

}
