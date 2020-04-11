package jp.riken.brain.ni.samuraigraph.base;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * The base class of all data classes.
 */
public abstract class SGData implements Cloneable, SGIConstants, SGICopiable, SGIData, SGIDisposable {

	/**
	 * The data source.
	 */
	private SGIDataSource mDataSource = null;
	
    /**
     * An observer for array data.
     */
    private SGDataSourceObserver mObserver = null;
    
    /**
     * The default constructor.
     */
    public SGData() {
        super();
    }

    /**
     * Builds a data object.
     * 
     * @param src
     *           the data source
     * @param obs
     *           a data observer
     */
    public SGData(final SGIDataSource src, final SGDataSourceObserver obs) {
        this();
        if (src == null) {
            throw new IllegalArgumentException("src == null");
        }
        if (obs == null) {
            throw new IllegalArgumentException("obs == null");
        }
        this.mDataSource = src;
        this.mObserver = obs;
        obs.addData(this);
    }

    /**
     * Returns the data source.
     * 
     * @return the data source
     */
    public SGIDataSource getDataSource() {
    	return this.mDataSource;
    }

    /**
     * Sets the data source.
     * 
     * @param src
     *           a data source
     */
    public void setDataSource(SGIDataSource src) {
    	this.mDataSource = src;
    }

    /**
     * Returns the data source observer.
     * 
     * @return the data source observer
     */
    public SGDataSourceObserver getDataSourceObserver() {
    	return this.mObserver;
    }
    
    /**
     * Clones this data object.
     * 
     * @return
     *         shallow copy of this data object
     */
    public Object clone() {
        try {
            SGData data = (SGData) super.clone();
            this.mObserver.addData(data);
            return data;
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
    
    /**
     * Calling the clone method.
     * 
     * @return
     *         copy of this data object
     */
    public final Object copy() {
        return this.clone();
    }
    
    /**
     * Sets a given data.
     * 
     * @param data
     *           a data
     * @return true if succeeded
     */
    public boolean setData(final SGData data) {
    	this.mDataSource = data.getDataSource();
    	this.mObserver = data.getDataSourceObserver();
        this.mObserver.addData(this);
        return true;
    }

    /**
     * Disposes of this data object.
     * Do not call any method of this object after called this method.
     */
    public void dispose() {
    	this.mDisposed = true;
    	
        // call a method of data observer
        this.mObserver.dataDisposed(this);
        
        this.mDataSource = null;
        this.mObserver = null;
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
     * Write properties of this data to a given Element.
     * @param el
     *          an Element
     * @param type
     *            type of the method to save properties
     * @return
     *          true if succeeded
     */
    public abstract boolean writeProperty(Element el, SGExportParameter type);
    
//    /**
//     * Read properties from a given Element and set to this data.
//     * @param el
//     *          an Element
//     * @return
//     *          true if succeeded
//     */
//    public abstract boolean readProperty(Element el);
    
    /**
     * Returns a map which has data information.
     * This method returns an empty map by default.
     * Subclasses overrode this method if necessary.
     * @return
     *        a map which has data information
     */
    public Map<String, Object> getInfoMap() {
        return new HashMap<String, Object>();
    }
    
    /**
     * Returns properties of this data.
     * @return
     *         properties of this data
     */
    public abstract SGProperties getProperties();

    /**
     * Get properties of this data.
     * @param p
     *          properties to set values
     * @return
     *          true if succeeded
     */
    public abstract boolean getProperties(final SGProperties p);
    
    /**
     * Set properties to this data.
     * @param p
     *          properties that have values to set to this data
     * @return
     *          true if succeeded
     */
    public abstract boolean setProperties(final SGProperties p);

    /**
     * The base class of data properties.
     * 
     */
    public static abstract class DataProperties extends SGProperties {

        /**
         * The default constructor.
         */
        public DataProperties() {
            super();
        }
        
        /**
         * Returns whether this object is equal to given object.
         * 
         * @param obj
         *            an object to be compared
         * @return
         *            true if two objects are equal
         */
        @Override
        public boolean equals(final Object obj) {
            if ((obj instanceof DataProperties) == false) {
                return false;
            }
            DataProperties p = (DataProperties) obj;
            if (!this.hasEqualColumnTypes(p)) {
            	return false;
            }
            if (!this.hasEqualSize(p)) {
            	return false;
            }
        	return true;
        }
        
        /**
         * Returns whether this data property has the equal column types with given data property.
         * 
         * @param dp
         *          a data property
         * @return true if this data property has the equal column types with given data property
         */
        public abstract boolean hasEqualColumnTypes(DataProperties dp);
        
        public abstract boolean hasEqualSize(DataProperties dp);
    }

    /**
     * Returns a text string of the data type to save into a NetCDF data set file.
     * 
     * @return a text string of the data type to save into a NetCDF data set file
     */
    public abstract String getNetCDFDataSetDataType();
    
    /**
     * Returns a text string of file extension for the file in a data set file.
     * 
     * @return a text string of file extension
     */
    public abstract String getDataSetFileExtension();
    
    /**
     * Returns the file path of data source.
     * 
     * @return the file path of data source
     */
    public String getPath() {
    	return this.getDataSource().getPath();
    }
    
}
