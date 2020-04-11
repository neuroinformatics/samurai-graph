package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SGDataSourceObserver {

    /**
	 * A map that has keys of data source and values of a list of data.
	 */
	private Map<SGIDataSource, Set<SGIData>> mDataMap = new HashMap<SGIDataSource, Set<SGIData>>();

    /**
	 * Builds an observer.
	 * 
	 */
    public SGDataSourceObserver() {
        super();
    }
    
    /**
     * Adds a new data to this observer.
     * 
     * @param data
     *           a data object
     */
    public void addData(SGIData data) {
        if (data == null) {
            throw new IllegalArgumentException("data == null");
        }
        SGIDataSource source = data.getDataSource();
        this.register(data, source);
    }

    /**
     * Disposes of a data source that is related to a given data object.
     * 
     * @param data
     *           a data object
     */
    public void dataDisposed(SGIData data) {
        if (data == null) {
            throw new IllegalArgumentException("data == null");
        }
        SGIDataSource source = data.getDataSource();
        this.unregister(data, source);
    }
    
    /**
     * Registers a data and its data source.
     * 
     * @param data
     *           a data object
     * @param source
     *           the data source object
     */
    public void register(SGIData data, SGIDataSource source) {
    	if (source == null) {
    		throw new IllegalArgumentException("source == null");
    	}
        Set<SGIData> dataSet = this.mDataMap.get(source);
        if (dataSet == null) {
            // if data list does not exist, create a new list
            Set<SGIData> set = new HashSet<SGIData>();
            set.add(data);
            this.mDataMap.put(source, set);
            dataSet = set;
        } else {
        	dataSet.add(data);
        }
//    	System.out.println("- " + this.mDataMap.size() + " " + this.getDataNum());
    }

    /**
     * Unregisters a data and its data source.
     * 
     * @param data
     *           a data object
     * @param source
     *           the data source object
     */
    public void unregister(SGIData data, SGIDataSource source) {
    	if (source == null) {
    		throw new IllegalArgumentException("source == null");
    	}
        Set<SGIData> dataSet = this.mDataMap.get(source);
        if (dataSet != null) {
            dataSet.remove(data);
            
            // if the list becomes empty, disposes of the data source
            if (dataSet.size() == 0) {
                this.mDataMap.remove(source);
            	source.dispose();
//            	System.out.println("- " + this.mDataMap.size() + " " + this.getDataNum());
            }
        }
    }
    
    int getDataNum() {
    	Iterator<Set<SGIData>> itr = this.mDataMap.values().iterator();
    	int cnt = 0;
    	while (itr.hasNext()) {
    		Set<SGIData> ds = itr.next();
    		cnt += ds.size();
    	}
    	return cnt;
    }

    /**
     * Returns the list of data sources.
     * 
     * @return the list of data sources
     */
    public List<SGIDataSource> getDataSourceList() {
    	return new ArrayList<SGIDataSource>(this.mDataMap.keySet());
    }
}
