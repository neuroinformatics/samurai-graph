package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class holds information of data.
 * 
 *
 */
public class SGDataClipBoard {

    /**
     * The list of copied data objects.
     */
    private List<DataCopy> mDataList = new ArrayList<DataCopy>();
    
    /**
     * Builds this class object.
     *
     */
    public SGDataClipBoard() {
        super();
    }

    /**
     * Adds a new data.
     * 
     * @param data
     *          data object
     * @param name
     *          the name of data
     * @return a copied data object
     */
    public DataCopy add(SGData data, String name) {
        return this.add(data, name, new HashMap<Class, SGProperties>());
    }

    /**
     * Adds a new data.
     * 
     * @param data
     *          data object
     * @param name
     *          the name of data
     * @param propertiesMap
     *           a map of properties for each figure element class
     * @return a copied data object
     */
    public DataCopy add(SGData data, String name, Map<Class, SGProperties> propertiesMap) {
        SGData dCopy = (SGData) data.copy();
        Map<Class, SGProperties> map = new HashMap<Class, SGProperties>(propertiesMap);
        DataCopy d = new DataCopy(dCopy, name, map);
        this.mDataList.add(d);
        return d;
    }
    
    /**
     * Clears all copied data.
     *
     */
    public void clear() {
        this.mDataList.clear();
    }
    
    /**
     * Returns the list of copied data.
     * 
     * @return the list of copied data
     */
    public List<DataCopy> getDataList() {
        return new ArrayList<DataCopy>(this.mDataList);
    }

    /**
     * Returns the copied data.
     * 
     * @param data
     *           a data object
     * @return copied data
     */
    public DataCopy getData(SGData data) {
        for (DataCopy d : this.mDataList) {
            if (d.getData().equals(data)) {
                return d;
            }
        }
        return null;
    }

    /**
     * A class for a copied data object.
     */
    public static class DataCopy {
        /**
         * The data.
         */
        private SGData data = null;

        /**
         * The name of data.
         */
        private String name = null;

        /**
         * Properties of data.
         */
        private Map<Class, SGProperties> propertiesMap = new HashMap<Class, SGProperties>();

        /**
         * Builds this class object.
         * 
         * @param data
         *           a data object
         * @param name
         *           the name of data
         * @param propertiesMap
         *           a map of properties for each figure element class
         */
        private DataCopy(SGData data, String name, Map<Class, SGProperties> propertiesMap) {
            super();
            this.data = data;
            this.name = name;
            this.propertiesMap = propertiesMap;
        }

        /**
         * Returns the data object.
         * 
         * @return the data object
         */
        public SGData getData() {
            return data;
        }

        /**
         * Returns the name of data.
         * 
         * @return the name of data
         */
        public String getName() {
            return name;
        }

//        /**
//         * Sets the data properties for each class object.
//         * 
//         * @param cl
//         *          class object
//         * @param p
//         *          properties of the data for a given figure element
//         */
//        public void setProperties(Class cl, SGProperties p) {
//            this.propertiesMap.put(cl, p);
//        }

        /**
         * Returns the data properties for a given class object.
         * 
         * @param cl
         *          class object
         * @return
         *          properties for a given class object
         */
        public SGProperties getProperties(Class cl) {
            SGProperties p = this.propertiesMap.get(cl);
            if (p == null) {
                return null;
            } else {
                return (SGProperties) p.copy();
            }
        }
        
        /**
         * Returns the map of data properties.
         * 
         * @return a map of data properties
         */
        public Map<Class, SGProperties> getPropertiesMap() {
            return new HashMap<Class, SGProperties>(this.propertiesMap);
        }
    }
    
}
