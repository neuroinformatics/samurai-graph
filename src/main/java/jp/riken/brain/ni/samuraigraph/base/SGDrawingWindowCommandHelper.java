/*
 * Copyright (C) 2026 Yoshihiro OKUMURA and the Samurai Graph developers.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

package jp.riken.brain.ni.samuraigraph.base;

import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGRootObjectConstants.*;

import java.util.ArrayList;

/** Collaborator that fires the focused-data commands of the drawing window. */
class SGDrawingWindowCommandHelper {

  private final SGDrawingWindow owner;

  SGDrawingWindowCommandHelper(final SGDrawingWindow owner) {
    this.owner = owner;
  }

  /** Splits the focused data objects. */
  void doSplitMultipleData() {
    this.owner.notifyToListener(MENUBARCMD_SPLIT);
    this.owner.notifyToRoot();
    this.owner.updateFocusedObjectItem();
    this.owner.repaintContentPane();
  }

  /** Transforms the focused data objects to other type data. */
  void doTransformData() {
    this.owner.notifyToListener(MENUCMD_TRANSFORM_DATA);
    this.owner.notifyToRoot();
    this.owner.updateFocusedObjectItem();
    this.owner.repaintContentPane();
  }

  /** Merge the focused data objects to one object. */
  void doMergeMultipleData() {
    this.owner.notifyToListener(MENUBARCMD_MERGE);
    this.owner.notifyToRoot();
    this.owner.updateFocusedObjectItem();
    this.owner.repaintContentPane();
  }

  /** Assigns line colors. */
  void doAssignLineColors() {
    this.owner.notifyToListener(MENUBARCMD_ASSIGN_LINE_COLORS);
    this.owner.notifyToRoot();
    this.owner.updateFocusedObjectItem();
    this.owner.repaintContentPane();
  }

  void doHideSelectedAxes() {
    ArrayList<SGFigure> fList = this.owner.getVisibleFigureList();
    for (SGFigure f : fList) {
      f.getAxisElement().hideSelectedAxes();
    }
    this.owner.repaint();
    this.owner.notifyToRoot();
  }
}
