package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper class of Component.
 * 
 */
public class SGComponentGroupElement implements SGIDisposable {

    // A component.
    private Component mComponent;

    // The list of component group.
    private List<SGComponentGroup> mComponentGroupList = new ArrayList<SGComponentGroup>();

    /**
     * Builds this object with a given component.
     * 
     * @param com
     *           a component
     */
    public SGComponentGroupElement(Component com) {
        this.mComponent = com;
    }

    /**
     * Adds a component group.
     * 
     * @param group
     *           a component group
     */
    public void addComponentGroup(SGComponentGroup group) {
        this.mComponentGroupList.add(group);
    }

    /**
     * Set the component enabled. Only when all component groups that this instance
     * has are enabled, the component is set to be enabled.
     * 
     * @param b
     *           true to be enabled
     */
    public void setEnabled(final boolean b) {
        boolean flag = b;
        for (SGComponentGroup group : this.mComponentGroupList) {
            flag &= group.isEnabled();
        }
        this.mComponent.setEnabled(flag);
    }

    /**
     * Returns a Component object.
     * 
     * @return a Component for this object
     */
    public Component getComponent() {
        return this.mComponent;
    }

    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
        this.mComponent = null;
        if (this.mComponentGroupList != null) {
            this.mComponentGroupList.clear();
            this.mComponentGroupList = null;
        }
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

}
