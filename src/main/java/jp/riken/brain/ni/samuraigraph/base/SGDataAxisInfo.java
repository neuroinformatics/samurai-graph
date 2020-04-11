package jp.riken.brain.ni.samuraigraph.base;


/**
 * Information of an axis related to the data.
 *
 */
public class SGDataAxisInfo {
    
    /**
     * The data.
     */
    protected SGData mData = null;

    /**
     * The axis.
     */
    protected SGAxis mAxis = null;

    /**
     * The range of axis value.
     */
    protected SGValueRange mRange = null;

    /**
     * The axis location.
     */
    protected int mLocation = -1;

    /**
     * The title.
     */
    protected String mTitle = null;
    
    /**
     * Builds this object.
     * 
     * @param data
     *           a data
     * @param axis
     *           an axis
     * @param range
     *           the range of axis values
     * @param location
     *           the axis location
     */
    public SGDataAxisInfo(final SGData data, final SGAxis axis, 
    		final SGValueRange range,
            final String title, final int location) {
        super();
        this.mData = data;
        this.mAxis = axis;
        this.mRange = new SGValueRange(range);
        this.mTitle = title;
        this.mLocation = location;
    }

    /**
     * Returns the data.
     * 
     * @return the data
     */
    public SGData getData() {
        return this.mData;
    }

    /**
     * Returns the axis.
     * 
     * @return the axis
     */
    public SGAxis getAxis() {
        return this.mAxis;
    }
    
    /**
     * Returns the range of axis value.
     * 
     * @return the range of axis value
     */
    public SGValueRange getRange() {
        return this.mRange;
    }

    /**
     * Returns the location of the axis.
     * 
     * @return the location of the axis
     */
    public int getLocation() {
        return this.mLocation;
    }
    
    /**
     * Returns the title for the axis.
     * 
     * @return the title for the axis
     */
    public String getTitle() {
        return this.mTitle;
    }
    
}
