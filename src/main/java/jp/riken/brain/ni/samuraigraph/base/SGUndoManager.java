package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A class to keep the property histories and to control undo/redo operation of
 * an undoable object.
 */
public class SGUndoManager implements SGIDisposable {

    // an undoable object
    private SGIUndoable mUndoable = null;

    // a list of the memento of mUndoable
    private List mMementoList = new ArrayList();

    // the index in the memento series of mUndoable
    private int mMementoCounter = 0;

    // A list of changed objects list, which contains mUndoable
    // and its undoable child objects if they exist.
    private List mChangedObjectListList = new ArrayList();

    // the index in the series of changed objects lists
    private int mChangedObjectListCounter = 0;

    // changed flag
    private boolean mChangedFlag = false;

    /**
     * Create this object with an undoable object.
     * 
     * @param obj
     *            an undoable object
     */
    public SGUndoManager(SGIUndoable obj) {
        super();
        this.mUndoable = obj;
    }

    /**
     * Initialize the history of the undoable object.
     * @return
     *         true if succeeded
     */
    public boolean initPropertiesHistory() {
        return this.addMemento(this.mUndoable.getMemento());
    }

    /**
     * Go backward the memento list and set the memento to the undoable object.
     * @return
     *         true if succeeded
     */
    public boolean setMementoBackward() {
        this.mMementoCounter--;
        return this.setCurrentMemento();
    }

    /**
     * Go forward the memento list and set the memento to the undoable object.
     * @return
     *         true if succeeded
     */
    public boolean setMementoForward() {
        this.mMementoCounter++;
        return this.setCurrentMemento();
    }

    // Set the memento to the undoable object with the current counter values.
    private boolean setCurrentMemento() {
        SGProperties p = (SGProperties) this.mMementoList
                .get(this.mMementoCounter);
        return this.mUndoable.setMemento(p);
    }

    /**
     * Undo the operation.
     * @return
     *         true if succeeded
     */
    public boolean undo() {
        if (this.isUndoable() == false) {
            return false;
        }
        ArrayList undoableList = (ArrayList) this.mChangedObjectListList
                .get(this.mChangedObjectListCounter - 1);
        for (int ii = 0; ii < undoableList.size(); ii++) {
            SGIUndoable undoable = (SGIUndoable) undoableList.get(ii);
            boolean flag;
            if (undoable.equals(this.mUndoable)) {
                flag = undoable.setMementoBackward();
            } else {
                flag = undoable.undo();
            }
            if (!flag) {
                throw new Error("undo erorr:" + undoable);
            }
        }

        // decrement the counter
        this.mChangedObjectListCounter--;

        return true;
    }

    /**
     * Redo the operation.
     * @return
     *         true if succeeded
     */
    public boolean redo() {
        if (this.isRedoable() == false) {
            return false;
        }
        ArrayList undoableList = (ArrayList) this.mChangedObjectListList
                .get(this.mChangedObjectListCounter);
        for (int ii = 0; ii < undoableList.size(); ii++) {
            SGIUndoable undoable = (SGIUndoable) undoableList.get(ii);
            boolean flag;
            if (undoable.equals(this.mUndoable)) {
                flag = undoable.setMementoForward();
            } else {
                flag = undoable.redo();
            }
            if (!flag) {
                throw new Error("redo erorr:" + undoable);
            }
        }

        // increment the counter
        this.mChangedObjectListCounter++;

        return true;
    }

    /**
     * Update the list of changed objects lists.
     */
    private boolean updateObjectHistory(final List objList) {
        List hList = new ArrayList(this.mChangedObjectListList.subList(0, this.mChangedObjectListCounter));
        hList.add(new ArrayList(objList));

        this.mChangedObjectListList = hList;
        this.mChangedObjectListCounter++;

        return true;
    }

    /**
     * Update the histories of the undoable object.
     * @return
     *         true if succeeded
     */
    public boolean updateHistory() {
        if (this.mUndoable.isChanged()) {
            // update the memento list of mUndoable
            if (this.updateMementoList() == false) {
                return false;
            }

            // add to the changed objects list
            ArrayList objList = new ArrayList();
            objList.add(this.mUndoable);
            if (this.updateObjectHistory(objList) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * Update the histories of the undoable object together with given undoable
     * objects.
     * @param objList
     *                a list of undoable objects
     * @return
     *                true if succeeded
     */
    public boolean updateHistory(final List objList) {
        ArrayList changedObjList = new ArrayList();
        if (this.mUndoable.isChanged()) {
            // update the memento list of mUndoable
            if (this.updateMementoList() == false) {
                return false;
            }

            // add to the changed objects list
            changedObjList.add(this.mUndoable);
        }

        for (int ii = 0; ii < objList.size(); ii++) {
            SGIUndoable obj = (SGIUndoable) objList.get(ii);
            if (obj.isChangedRoot()) {
                if (obj.updateHistory() == false) {
                    return false;
                }
                changedObjList.add(obj);
            }
        }

        if (changedObjList.size() != 0) {
            if (this.updateObjectHistory(changedObjList) == false) {
                return false;
            }
        }

        return true;
    }

    // Update the memento list.
    private boolean updateMementoList() {
        this.mMementoCounter++;
        SGProperties p = this.mUndoable.getMemento();
        if (p == null) {
            return false;
        }
        return this.addMemento(p);
    }

    // Update the memento list and add a property object to the tail of the list.
    private boolean addMemento(final SGProperties p) {
        for (int ii = this.mMementoList.size() - 1; ii >= this.mMementoCounter; ii--) {
            SGProperties pOld = (SGProperties) this.mMementoList.remove(ii);
            pOld.dispose();
            pOld = null;
        }
        this.mMementoList.add(p);
        return true;
    }

    /**
     * Return whether this object can undo.
     * @return
     *         true if it is possible to undo
     */
    public boolean isUndoable() {
        return (this.mChangedObjectListCounter != 0);
    }

    /**
     * Return whether this object can redo.
     * @return
     *         true if it is possible to redo
     */
    public boolean isRedoable() {
        return (this.mChangedObjectListCounter != this.mChangedObjectListList
                .size());
    }

    /**
     * Returns a list of all memento objects.
     * @return
     *         a list of all memento objects
     */
    public List getMementoList() {
        return new ArrayList(this.mMementoList);
    }

    /**
     * Returns lists of all changed objects.
     * @return
     *         lists of all changed objects
     */
    public List getChangedObjectListList() {
        return new ArrayList(this.mChangedObjectListList);
    }

    /**
     * Retuens the present index in the series of changed objects lists.
     * @return
     *         the present index in the series of changed objects lists
     */
    public int getChangedObjectListIndex() {
        return this.mChangedObjectListCounter;
    }

    /**
     * Returns the present index in the memento series of the undoable object.
     * @return
     *         the present index in the memento series of the undoable object
     */
    public int getMementoIndex() {
        return this.mMementoCounter;
    }

    /**
     * Dispose of this object.
     */
    public void dispose() {
        this.clear();
        this.mUndoable = null;
        this.mChangedObjectListList = null;
        this.mMementoList = null;
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
     * Clear all objects used in undo/redo operation.
     */
    public void initUndoBuffer() {
        this.clear();
        this.initPropertiesHistory();
    }

    // clear all objects
    private void clear() {
        List list = this.mMementoList;
        for (int ii = 0; ii < list.size(); ii++) {
            SGProperties p = (SGProperties) list.get(ii);
            p.dispose();
        }
        this.mMementoList.clear();
        this.mChangedObjectListList.clear();
        this.mChangedObjectListCounter = 0;
        this.mMementoCounter = 0;
        list = null;
    }

    /**
     * Sets the flag whether the undoable object is changed.
     * @param b
     *          a flag to be set
     */
    public void setChanged(final boolean b) {
        this.mChangedFlag = b;
    }

    /**
     * Returns whether the undoable object is changed.
     * @return
     *         true if the undoable object is changed
     */
    public boolean isChanged() {
        return this.mChangedFlag;
    }

     void dump() {
		 System.out.println(this.mChangedObjectListList);
		 System.out.println("size=" + this.mChangedObjectListList.size());
         System.out.println(this.mChangedObjectListCounter);
         System.out.println(this.mMementoList);
         System.out.println("size=" + this.mMementoList.size());
         System.out.println(this.mMementoCounter);
         System.out.println();
     }

    
    /**
     * Delete histories forward from the current position.
     * @return
     *         true if succeeded
     */
    public boolean deleteForwardHistory() {
	
		// remove elements in mChangedObjectListList at the index
		// more than mChangedObjectListCounter
		HashSet removedSet = new HashSet();
		for (int ii = this.mChangedObjectListList.size() - 1; ii >= this.mChangedObjectListCounter; ii--) {
		    List removedList = (List) this.mChangedObjectListList.remove(ii);
		    removedSet.addAll(removedList);
		    removedList.clear();
		}
		
		// remove elements in mMementoList at the index
		// more than (mMementoCounter + 1) and dispose them
		for (int ii = this.mMementoList.size() - 1; ii > this.mMementoCounter; ii--) {
		    SGProperties p = (SGProperties) this.mMementoList.remove(ii);
		    p.dispose();
            p = null;
		}
		
		return true;
    }
}
