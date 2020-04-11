package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;

/**
 * An interface for the node in the tree structure.
 */
public interface SGINode {

    /**
     * Returns a list of child nodes.
     * 
     * @return a list of chid nodes
     */
    public ArrayList getChildNodes();

    /**
     * Returns a name of the class.
     * 
     * @return
     */
    public String getClassDescription();

    /**
     * Returns a name of an instance.
     * 
     * @return
     */
    public String getInstanceDescription();

}
