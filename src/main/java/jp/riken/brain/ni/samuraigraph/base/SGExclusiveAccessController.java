package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;

/**
 * A class to controll exclusive access of selectable objects.
 */
public class SGExclusiveAccessController {
    //
    private ArrayList mSelectableList = new ArrayList();

    /**
     * 
     */
    public SGExclusiveAccessController() {
        super();
    }

    /**
     * 
     * @param obj
     */
    public void add(final SGISelectable obj) {
        this.mSelectableList.add(obj);
    }

    /**
     * 
     * @param obj
     */
    public void remove(final SGISelectable obj) {
        this.mSelectableList.remove(obj);
    }

    /**
     * 
     * @param obj
     * @return
     */
    public boolean contains(final SGISelectable obj) {
        return this.mSelectableList.contains(obj);
    }

    /**
     * 
     * @param obj
     */
    public void select(final SGISelectable obj) {
        ArrayList list = this.mSelectableList;
        if (!list.contains(obj)) {
            throw new IllegalArgumentException("!list.contains(obj)");
        }
        for (int ii = 0; ii < list.size(); ii++) {
            SGISelectable s = (SGISelectable) list.get(ii);
            s.setSelected(s.equals(obj));
        }
    }

    /**
     * 
     * @param obj
     */
    public void deselect(final SGISelectable obj) {
        ArrayList list = this.mSelectableList;
        if (!list.contains(obj)) {
            throw new IllegalArgumentException("!list.contains(obj)");
        }
        for (int ii = 0; ii < list.size(); ii++) {
            SGISelectable s = (SGISelectable) list.get(ii);
            if (s.equals(obj)) {
                s.setSelected(false);
                SGISelectable ss = (SGISelectable) list.get((ii + 1)
                        % list.size());
                ss.setSelected(true);
                return;
            }
        }
    }

    /**
     * 
     * @param obj
     * @param sub
     */
    public void deselect(final SGISelectable obj, final SGISelectable sub) {
        ArrayList list = this.mSelectableList;
        if (!list.contains(obj) || !list.contains(sub)) {
            throw new IllegalArgumentException(
                    "!list.contains(obj) || !list.contains(sub)");
        }
        for (int ii = 0; ii < list.size(); ii++) {
            SGISelectable s = (SGISelectable) list.get(ii);
            if (s.equals(obj)) {
                s.setSelected(false);
                sub.setSelected(true);
                return;
            }
        }
    }

}
