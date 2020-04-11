package jp.riken.brain.ni.samuraigraph.base;

/**
 * The base class of index block.
 *
 */
public abstract class SGIndexBlock implements Cloneable {

	/**
	 * The default constructor.
	 */
	public SGIndexBlock() {
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

}
