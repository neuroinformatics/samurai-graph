package jp.riken.brain.ni.samuraigraph.base;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGPreferencesConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGUpgradeConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;

import java.util.ArrayList;

/** A class to control exclusive access of selectable objects. */
public class SGExclusiveAccessController {
  //
  private ArrayList<SGISelectable> mSelectableList = new ArrayList<SGISelectable>();

  /** */
  public SGExclusiveAccessController() {
    super();
  }

  /**
   * @param obj
   */
  public void add(final SGISelectable obj) {
    this.mSelectableList.add(obj);
  }

  /**
   * @param obj
   */
  public void remove(final SGISelectable obj) {
    this.mSelectableList.remove(obj);
  }

  /**
   * @param obj
   */
  public boolean contains(final SGISelectable obj) {
    return this.mSelectableList.contains(obj);
  }

  /**
   * @param obj
   */
  public void select(final SGISelectable obj) {
    ArrayList<SGISelectable> list = this.mSelectableList;
    if (!list.contains(obj)) {
      throw new IllegalArgumentException("!list.contains(obj)");
    }
    for (int ii = 0; ii < list.size(); ii++) {
      SGISelectable s = list.get(ii);
      s.setSelected(s.equals(obj));
    }
  }

  /**
   * @param obj
   */
  public void deselect(final SGISelectable obj) {
    ArrayList<SGISelectable> list = this.mSelectableList;
    if (!list.contains(obj)) {
      throw new IllegalArgumentException("!list.contains(obj)");
    }
    for (int ii = 0; ii < list.size(); ii++) {
      SGISelectable s = list.get(ii);
      if (s.equals(obj)) {
        s.setSelected(false);
        SGISelectable ss = list.get((ii + 1) % list.size());
        ss.setSelected(true);
        return;
      }
    }
  }

  /**
   * @param obj
   * @param sub
   */
  public void deselect(final SGISelectable obj, final SGISelectable sub) {
    ArrayList<SGISelectable> list = this.mSelectableList;
    if (!list.contains(obj) || !list.contains(sub)) {
      throw new IllegalArgumentException("!list.contains(obj) || !list.contains(sub)");
    }
    for (int ii = 0; ii < list.size(); ii++) {
      SGISelectable s = list.get(ii);
      if (s.equals(obj)) {
        s.setSelected(false);
        sub.setSelected(true);
        return;
      }
    }
  }
}
