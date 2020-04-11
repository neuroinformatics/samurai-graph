package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGIAnimation;

public interface SGIDataAnimation extends SGIAnimation {

	/**
	 * Prepares for the animation.
	 * 
	 */
	public void prepareForChanges();
	
    /**
     * Saves all changes of this data source.
     *
     */
    public void saveChanges();

    /**
     * Cancels all changes of this data source.
     *
     */
    public void cancelChanges();

    /**
     * Returns the name of data source.
     *
     * @return the name of this animation data source
     */
    public String getDataSourceName();

    /**
     * Returns the unit string of data source.
     *
     * @return the unit string of data source
     */
    public String getDataSourceUnitString();

    /**
     * Returns the current value of data object.
     *
     * @return the current value of data object
     */
    public Number getCurrentValue();

    /**
     * Returns the data.
     *
     * @return the data
     */
    public SGData getData();

}
