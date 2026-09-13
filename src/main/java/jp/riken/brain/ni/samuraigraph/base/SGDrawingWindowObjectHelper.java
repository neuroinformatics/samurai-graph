package jp.riken.brain.ni.samuraigraph.base;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class SGDrawingWindowObjectHelper {

  private final SGDrawingWindow owner;

  public SGDrawingWindowObjectHelper(final SGDrawingWindow owner) {
    this.owner = owner;
  }

  boolean moveFocusedObjects(final boolean toFront) {

    ArrayList<SGFigure> fList = owner.getVisibleFigureList();
    boolean changed = false;
    for (int ii = 0; ii < fList.size(); ii++) {
      SGFigure figure = fList.get(ii);
      if (figure.moveFocusedObjects(toFront) == false) {
        return false;
      }
      SGIFigureElement[] array = figure.getIFigureElementArray();
      for (int jj = 0; jj < array.length; jj++) {
        if (array[jj].isChanged()) {
          changed = true;
        }
      }
    }

    List<SGISelectable> list = owner.getFocusedObjectsList();
    List<SGFigure> objList = owner.mFigureList;
    List<SGFigure> objListOld = new ArrayList<SGFigure>(objList);

    // move focused objects
    if (toFront) {
      for (int ii = 0; ii < list.size(); ii++) {
        SGISelectable obj = list.get(ii);
        if (obj instanceof SGFigure) {
          if (SGUtility.moveObjectTo((SGFigure) obj, objList, objList.size() - 1) == false) {
            return false;
          }
        }
      }
    } else {
      for (int ii = list.size() - 1; ii >= 0; ii--) {
        SGISelectable obj = list.get(ii);
        if (obj instanceof SGFigure) {
          if (SGUtility.moveObjectTo((SGFigure) obj, objList, 0) == false) {
            return false;
          }
        }
      }
    }

    if (objList.equals(objListOld) == false) {
      owner.setChanged(true);
      changed = true;
    }

    if (changed) {
      owner.notifyToRoot();
      owner.updateDataItem();
    }

    // repaint
    owner.repaintContentPane();

    return true;
  }

  boolean moveFocusedObjects(final int num) {

    ArrayList<SGFigure> fList = owner.getVisibleFigureList();
    boolean changed = false;
    for (int ii = 0; ii < fList.size(); ii++) {
      SGFigure figure = fList.get(ii);
      if (figure.moveFocusedObjects(num) == false) {
        return false;
      }
      SGIFigureElement[] array = figure.getIFigureElementArray();
      for (int jj = 0; jj < array.length; jj++) {
        if (array[jj].isChanged()) {
          changed = true;
        }
      }
    }

    List<SGISelectable> list = owner.getFocusedObjectsList();
    List<SGFigure> objList = owner.mFigureList;

    // record the list before edited
    List<SGFigure> objListOld = new ArrayList<SGFigure>(objList);
    List<SGFigure> movedFigures = new ArrayList<>();
    for (SGISelectable s : list) {
      if (s instanceof SGFigure) {
        movedFigures.add((SGFigure) s);
      }
    }
    if (SGUtility.moveObject(movedFigures, objList, num) == false) {
      return false;
    }

    if (objList.equals(objListOld) == false) {
      owner.setChanged(true);
      changed = true;
    }

    if (changed) {
      owner.notifyToRoot();
      owner.updateDataItem();
    }

    // repaint
    owner.repaintContentPane();

    return true;
  }

  protected void updateFocusedObjectItem() {
    boolean eff = false;
    ArrayList<SGFigure> list = owner.getVisibleFigureList();
    for (int ii = 0; ii < list.size(); ii++) {
      SGFigure figure = list.get(ii);
      if (figure.isSelected()) {
        eff = true;
        break;
      }

      SGIFigureElement[] array = figure.getIFigureElementArray();
      for (int jj = 0; jj < array.length; jj++) {
        if (array[jj].getFocusedObjectsList().size() != 0) {
          eff = true;
          break;
        }
      }

      if (eff) {
        break;
      }
    }

    // set to the menu bar
    SGMenuBar mBar = owner.mMenuBar;
    mBar.setMenuItemEnabled(owner.MENUBAR_EDIT, owner.MENUBARCMD_CUT, eff);
    mBar.setMenuItemEnabled(owner.MENUBAR_EDIT, owner.MENUBARCMD_COPY, eff);
    mBar.setMenuItemEnabled(owner.MENUBAR_EDIT, owner.MENUBARCMD_DELETE, eff);
    mBar.setMenuItemEnabled(owner.MENUBAR_EDIT, owner.MENUBARCMD_DUPLICATE, eff);
    mBar.setMenuItemEnabled(owner.MENUBAR_ARRANGE, owner.MENUBARCMD_BRING_TO_FRONT, eff);
    mBar.setMenuItemEnabled(owner.MENUBAR_ARRANGE, owner.MENUBARCMD_BRING_FORWARD, eff);
    mBar.setMenuItemEnabled(owner.MENUBAR_ARRANGE, owner.MENUBARCMD_SEND_BACKWARD, eff);
    mBar.setMenuItemEnabled(owner.MENUBAR_ARRANGE, owner.MENUBARCMD_SEND_TO_BACK, eff);

    // set to the tool bar
    SGToolBar tBar = owner.mToolBar;
    tBar.setButtonEnabled(owner.MENUBARCMD_CUT, eff);
    tBar.setButtonEnabled(owner.MENUBARCMD_COPY, eff);
  }

  public boolean moveFigureToEnd(final int id, final boolean toFront) {
    SGFigure f = owner.getFigure(id);
    if (f == null) {
      return false;
    }
    if (f.isVisible() == false) {
      return false;
    }

    List<SGFigure> objList = owner.mFigureList;
    List<SGFigure> objListOld = new ArrayList<SGFigure>(objList);

    // move focused objects
    if (toFront) {
      if (SGUtility.moveObjectToTail(f, objList) == false) {
        return false;
      }
    } else {
      if (SGUtility.moveObjectToHead(f, objList) == false) {
        return false;
      }
    }

    final boolean ch = !owner.mFigureList.equals(objListOld);
    if (ch) {
      owner.setChanged(true);
      owner.notifyToRoot();
      owner.updateDataItem();
      owner.repaintContentPane();
    }

    return true;
  }

  public boolean moveFigure(final int id, final boolean toFront) {
    SGFigure f = owner.getFigure(id);
    if (f == null) {
      return false;
    }
    if (f.isVisible() == false) {
      return false;
    }

    List<SGFigure> objList = owner.mFigureList;
    List<SGFigure> objListOld = new ArrayList<SGFigure>(objList);

    // move focused objects
    if (toFront) {
      if (SGUtility.moveObjectToNext(f, objList) == false) {
        return false;
      }
    } else {
      if (SGUtility.moveObjectToPrevious(f, objList) == false) {
        return false;
      }
    }

    final boolean ch = !owner.mFigureList.equals(objListOld);
    if (ch) {
      owner.setChanged(true);
      owner.notifyToRoot();
      owner.updateDataItem();
      owner.repaintContentPane();
    }

    return true;
  }

  public boolean alignFigures() {
    // record the location
    ArrayList<SGFigure> list = owner.getVisibleFigureList();
    for (int ii = 0; ii < list.size(); ii++) {
      SGFigure figure = list.get(ii);
      figure.recordFigureRect();
    }
    owner.recordPaperRect();

    // aligns figures
    if (SGDrawingWindowAlignmentUtility.alignFiguresByGraphAreaNew(owner) == false) {
      return false;
    }

    //
    for (int ii = 0; ii < list.size(); ii++) {
      SGFigure figure = list.get(ii);
      if (figure.isFigureMoved()) {
        figure.setChanged(true);
      }
    }

    if (owner.isPaperBoundsChanged()) {
      owner.setChanged(true);
    }

    // notify to the root
    owner.notifyToRoot();

    return true;
  }

  protected boolean insertSymbol(final SGFigure figure, final int x, final int y) {

    boolean flag = false;

    // a label
    if (owner.getLabelInsertionFlag()) {
      flag = figure.addString(x, y);
    }

    // a timing line
    if (owner.getTimingLineInsertionFlag()) {
      flag = figure.addTimingLine(x, y);
    }

    // an axis break symbol
    if (owner.getAxisBreakSymbolInsertionFlag()) {
      flag = figure.addAxisBreakSymbol(x, y);
    }

    // a symbol of significant difference
    if (owner.getSignificantDifferenceSymbolInsertionFlag()) {
      flag = figure.addSignificantDifferenceSymbol(x, y);
    }

    // rectangle
    if (owner.getRectangleInsertionFlag()) {
      flag = figure.addShape(SGIFigureElementShape.RECTANGLE, x, y);
    }

    // ellipse
    if (owner.getEllipseInsertionFlag()) {
      flag = figure.addShape(SGIFigureElementShape.ELLIPSE, x, y);
    }

    // arrow
    if (owner.getArrowInsertionFlag()) {
      flag = figure.addShape(SGIFigureElementShape.ARROW, x, y);
    }

    // line
    if (owner.getLineInsertionFlag()) {
      flag = figure.addShape(SGIFigureElementShape.LINE, x, y);
    }

    return flag;
  }
}
