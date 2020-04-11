package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * A class which controls available components.
 * 
 */
public class SGComponentGroup implements SGIDisposable {
	
    // A flag whether this group is enabled.
    private boolean mEnableFlag = true;

    // The list of SGComponentGroupElement objects.
    private List<SGComponentGroupElement> mElementList = new ArrayList<SGComponentGroupElement>();

    /**
     * The default constructor.
     * 
     */
    public SGComponentGroup() {
        super();
    }

    /**
     * Add a component group object.
     * 
     * @param group
     *           a SGComponentGroupElement object to be set
     */
    public void addElement(SGComponentGroupElement group) {
        this.mElementList.add(group);
        group.addComponentGroup(this);
    }

    /**
     * Adds component group objects.
     * 
     * @param array
     *           an array of component group objects
     */
    public void addElement(SGComponentGroupElement[] array) {
        for (int ii = 0; ii < array.length; ii++) {
            this.addElement(array[ii]);
        }
    }

    /**
     * Adds component group objects.
     * 
     * @param list
     *           a list of component group objects
     */
    public void addElement(List<SGComponentGroupElement> list) {
        for (int ii = 0; ii < list.size(); ii++) {
            this.addElement(list.get(ii));
        }
    }

    /**
     * Sets to be enabled or disabled the components.
     * 
     * @param b
     *          true to be enabled
     */
    public void setEnabled(final boolean b) {
        this.mEnableFlag = b;
        for (int ii = 0; ii < this.mElementList.size(); ii++) {
            SGComponentGroupElement el = this.mElementList.get(ii);
            el.setEnabled(b);
        }
    }

    /**
     * Returns whether the components are enabled.
     * 
     * @return true if enabled
     */
    public boolean isEnabled() {
        return this.mEnableFlag;
    }

    /**
     * Disposes of this object.
     * 
     */
    public void dispose() {
    	this.mDisposed = true;    	
        for (int ii = 0; ii < this.mElementList.size(); ii++) {
            SGComponentGroupElement el = this.mElementList.get(ii);
            el.dispose();
        }
        this.mElementList.clear();
        this.mElementList = null;
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

    public void clear() {
    	this.mElementList.clear();
    }
    
    public SGComponentGroupElement findElement(Component comp) {
    	SGComponentGroupElement ret = null;
    	for (SGComponentGroupElement el : this.mElementList) {
    		if (el.getComponent().equals(comp)) {
    			ret = el;
    			break;
    		}
    	}
    	return ret;
    }
}
