package jp.riken.brain.ni.samuraigraph.base;

/**
 * The base class of data buffer.
 *
 */
public abstract class SGDataBuffer implements Cloneable {
	
	/**
	 * The default constructor.
	 */
	public SGDataBuffer() {
		super();
	}
	
    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
	@Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
	
	/**
	 * Returns the data type.
	 * 
	 * @return the data type
	 */
	public abstract String getDataType();
	
	/**
	 * Returns true if this data buffer is of grid type.
	 * 
	 * @return true if this data buffer is of grid type
	 */
	public abstract boolean isGridType();
	
	/**
	 * Returns a text string for the information key of grid type.
	 * 
	 * @return a text string for the information key of grid type
	 */
	public abstract String getGridTypeKey();
	
}
