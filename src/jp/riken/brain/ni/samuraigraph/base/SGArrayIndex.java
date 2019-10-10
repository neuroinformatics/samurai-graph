package jp.riken.brain.ni.samuraigraph.base;

public class SGArrayIndex implements SGIIndex {

	private int mIndex = 0;
	
	public SGArrayIndex() {
		super();
	}

	public SGArrayIndex(final int idx) {
		this();
		this.set(idx);
	}
	
	public int getIndex() {
		return this.mIndex;
	}
	
	public void set(final int idx) {
		this.mIndex = idx;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SGArrayIndex)) {
			return false;
		}
		SGArrayIndex idx = (SGArrayIndex) obj;
		if (idx.mIndex != this.mIndex) {
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
        return Integer.toString(this.mIndex);
    }

}
