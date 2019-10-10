package jp.riken.brain.ni.samuraigraph.base;

import java.util.List;


/**
 * An interface for the data source.
 *
 */
public interface SGIDataSource extends Cloneable, SGIDisposable {

    /**
     * Returns the file path.
     * 
     * @return the file path
     */
    public String getPath();

    /**
     * Returns the list of attributes.
     * 
     * @return the list of attributes
     */
	public List<SGAttribute> getAttributes();

}
