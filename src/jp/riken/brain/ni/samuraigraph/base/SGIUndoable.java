package jp.riken.brain.ni.samuraigraph.base;

/**
 * The SGIUndoable interface provides definitions for undoable objects.
 * Collaborating with the SGUndoManager object, undoable objects can record its
 * change tracking and recover the properties.
 */
public interface SGIUndoable {

    /**
     * Go backward the histories list.
     */
    public boolean setMementoBackward();

    /**
     * Go forward the histories list.
     */
    public boolean setMementoForward();

    /**
     * Undo the operation.
     */
    public boolean undo();

    /**
     * Redo the operation.
     */
    public boolean redo();

    /**
     * Initialize the history of this object.
     */
    public boolean initPropertiesHistory();

    /**
     * 
     */
    public boolean updateHistory();

    /**
     * 
     * 
     */
    public void notifyToRoot();

    /**
     * Returns whether this undoable objects has changed. If it was changed, its
     * properties would be recorded.
     * 
     * @return
     * @uml.property name="changed"
     */
    public boolean isChanged();

    /**
     * Returns whether this object or its child objects are changed. if this
     * object itself was changed, this method returns true certainly.
     * 
     * @return
     */
    public boolean isChangedRoot();

    /**
     * Set changed or unchanged this object.
     * 
     * @param b -
     *            true sets changed and false sets unchanged.
     * @uml.property name="changed"
     */
    public void setChanged(boolean b);

    /**
     * Returns a mement object.
     * 
     * @return
     */
    public SGProperties getMemento();

    /**
     * Set the mement object.
     * 
     * @param p -
     *            properties to be set
     * @return
     * @uml.property name="memento"
     */
    public boolean setMemento(SGProperties p);

    /**
     * Returns whether this object can be undo its opration.
     * 
     * @return
     */
    public boolean isUndoable();

    /**
     * Returns whether this object can be redo its opration.
     * 
     * @return
     */
    public boolean isRedoable();

    /**
     * Clear the undo buffer.
     * 
     */
    public void initUndoBuffer();

    /**
     * Delete all forward histories.
     *
     * @return
     *         true if succeeded
     */
    public boolean deleteForwardHistory();
    
    /**
     * Clear changed flag of this undoable object and all child objects.
     *
     */
    public void clearChanged();
}
