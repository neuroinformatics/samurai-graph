package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;

public abstract class SGPickUpDimensionInfo implements Cloneable {
	
	protected SGIntegerSeriesSet mIndices = null;
	
	public SGPickUpDimensionInfo() {
		super();
	}

	public SGPickUpDimensionInfo(final SGIntegerSeriesSet indices) {
		this();
		this.mIndices = new SGIntegerSeriesSet(indices);
	}
	
	public SGIntegerSeriesSet getIndices() {
		return (SGIntegerSeriesSet) this.mIndices.clone();
	}
	
	public void setIndices(SGIntegerSeriesSet indices) {
		if (indices != null) {
			this.mIndices = (SGIntegerSeriesSet) indices.clone();
		} else {
			this.mIndices = null;
		}
	}

    /**
     * Clones this data object.
     * 
     * @return copy of this data object
     */
    public Object clone() {
    	SGPickUpDimensionInfo info = null;
        try {
        	info = (SGPickUpDimensionInfo) super.clone();
        	info.mIndices = (SGIntegerSeriesSet) this.mIndices.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
        return info;
    }
    
	@Override
    public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
    	if (!(obj instanceof SGPickUpDimensionInfo)) {
    		return false;
    	}
    	SGPickUpDimensionInfo info = (SGPickUpDimensionInfo) obj;
    	if (!info.mIndices.equals(this.mIndices)) {
    		return false;
    	}
    	return true;
    }
}
