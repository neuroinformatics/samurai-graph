package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Undo and history management of the drawing window. */
final class SGDrawingWindowUndoUtility {

  private SGDrawingWindowUndoUtility() {}

  public static boolean initPropertiesHistory(final SGDrawingWindow wnd) {

    return wnd.getUndoManager().initPropertiesHistory();
  }

  public static boolean undo(final SGDrawingWindow wnd) {

    if (wnd.getUndoManager().undo() == false) {
      return false;
    }

    // update
    wnd.updateOnUndo();
    updateStatusBarSavedFlag(wnd);

    wnd.repaintContentPane();

    return true;
  }

  public static boolean redo(final SGDrawingWindow wnd) {

    if (wnd.getUndoManager().redo() == false) {
      return false;
    }

    // update
    wnd.updateOnUndo();
    updateStatusBarSavedFlag(wnd);

    wnd.repaintContentPane();

    return true;
  }

  public static boolean existsOnUndo(
      final SGDrawingWindow wnd, final boolean bUndo, final SGFigure figure, final SGData data) {

    // try undo / redo
    if (bUndo) {
      if (isUndoable(wnd) == false) {
        throw new Error("This must not happen.");
      }
      wnd.getUndoManager().undo();
    } else {
      if (isRedoable(wnd) == false) {
        throw new Error("This must not happen.");
      }
      wnd.getUndoManager().redo();
    }

    try {
      List<SGFigure> figureList = wnd.getVisibleFigureList();
      if (!figureList.contains(figure)) {
        return false;
      }
      boolean dataFound = false;
      for (SGFigure f : figureList) {
        List<SGData> dataList = f.getVisibleDataList();
        if (dataList.contains(data)) {
          dataFound = true;
          break;
        }
      }
      return dataFound;

    } finally {

      // recover the state
      if (bUndo) {
        wnd.getUndoManager().redo();
      } else {
        wnd.getUndoManager().undo();
      }
    }
  }

  public static boolean isChanged(final SGDrawingWindow wnd) {

    return wnd.getUndoManager().isChanged();
  }

  public static void setChanged(final SGDrawingWindow wnd, final boolean b) {

    wnd.getUndoManager().setChanged(b);
  }

  public static void clearChanged(final SGDrawingWindow wnd) {

    setChanged(wnd, false);
    List<SGFigure> fList = wnd.getVisibleFigureList();
    for (int ii = 0; ii < fList.size(); ii++) {
      SGFigure f = fList.get(ii);
      f.clearChanged();
    }
  }

  public static boolean deleteForwardHistory(final SGDrawingWindow wnd) {

    // delete forward history of figures
    for (int ii = 0; ii < wnd.getFigureListInternal().size(); ii++) {
      SGFigure f = wnd.getFigureListInternal().get(ii);
      if (f.deleteForwardHistory() == false) {
        return false;
      }
    }

    // delete forward history of this window
    if (wnd.getUndoManager().deleteForwardHistory() == false) {
      return false;
    }

    // delete useless child objects
    if (deleteUselessFigures(wnd) == false) {
      return false;
    }

    return true;
  }

  public static Set<SGFigure> getAvailableChildSet(final SGDrawingWindow wnd) {

    Set<SGFigure> set = new HashSet<SGFigure>();
    List<SGProperties> mList = wnd.getUndoManager().getMementoList();
    for (int ii = 0; ii < mList.size(); ii++) {
      SGProperties p = mList.get(ii);
      if (p instanceof SGDrawingWindow.WindowProperties) {
        SGDrawingWindow.WindowProperties wp = (SGDrawingWindow.WindowProperties) p;
        set.addAll(wp.getVisibleFigureList());
      }
    }

    return set;
  }

  public static void initUndoBuffer(final SGDrawingWindow wnd) {

    // figures
    for (int ii = 0; ii < wnd.getFigureListInternal().size(); ii++) {
      SGFigure f = wnd.getFigureListInternal().get(ii);
      f.initUndoBuffer();
    }

    // // dispose invisible figures
    // for (int ii = wnd.getFigureListInternal().size() - 1; ii >= 0; ii--) {
    // SGFigure f = (SGFigure) wnd.getFigureListInternal().get(ii);
    // if (!f.isVisible()) {
    // wnd.getFigureListInternal().remove(f);
    // f.dispose();
    // }
    // }

    // initialize undo buffer
    wnd.getUndoManager().initUndoBuffer();

    // delete useless figures
    if (deleteUselessFigures(wnd) == false) {
      throw new Error("Failed to initialize undo buffer.");
    }
  }

  public static boolean isUndoable(final SGDrawingWindow wnd) {

    return wnd.getUndoManager().isUndoable();
  }

  public static boolean isRedoable(final SGDrawingWindow wnd) {

    return wnd.getUndoManager().isRedoable();
  }

  public static boolean updateHistory(final SGDrawingWindow wnd) {

    // update the updated index
    // this method must be called before SGUndoManager::updateHistory is
    // called
    updateSavedListIndex(wnd);

    // update the history
    if (wnd.getUndoManager().updateHistory(wnd.getVisibleFigureList()) == false) {
      return false;
    }

    // update items
    wnd.updateUndoItems();

    // update the status bar
    updateStatusBarSavedFlag(wnd);

    return true;
  }

  public static void updateSavedListIndex(final SGDrawingWindow wnd) {

    boolean changed = false;
    if (isChanged(wnd)) {
      changed = true;
    } else {
      ArrayList<SGFigure> list = wnd.getVisibleFigureList();
      for (int ii = 0; ii < list.size(); ii++) {
        SGFigure f = list.get(ii);
        if (f.isChanged()) {
          changed = true;
          break;
        }
      }
    }

    //
    if (changed) {
      final int index = wnd.getUndoManager().getChangedObjectListIndex();
      if (index < wnd.getSavedListIndex()) {
        initSavedHistory(wnd);
      }
    }
  }

  public static void updateStatusBarSavedFlag(final SGDrawingWindow wnd) {

    boolean b = false;
    if (wnd.getVisibleFigureList().size() != 0) {
      final int index = wnd.getUndoManager().getChangedObjectListIndex();
      b = (index != wnd.getSavedListIndex());
    }
    wnd.getStatusBar().setSaved(b);
  }

  public static void initSavedHistory(final SGDrawingWindow wnd) {

    wnd.setSavedListIndex(-1);
    updateStatusBarSavedFlag(wnd);
  }

  public static boolean isSaved(final SGDrawingWindow wnd) {

    return (wnd.getSavedListIndex() == wnd.getUndoManager().getChangedObjectListIndex());
  }

  public static void setSaved(final SGDrawingWindow wnd, final boolean b) {

    if (b) wnd.setSavedListIndex(wnd.getUndoManager().getChangedObjectListIndex());
    updateStatusBarSavedFlag(wnd);
  }

  public static boolean deleteUselessFigures(final SGDrawingWindow wnd) {

    Set<SGFigure> set = getAvailableChildSet(wnd);
    boolean gc = false;
    List<SGFigure> cList = new ArrayList<SGFigure>(wnd.getFigureListInternal());
    for (int ii = cList.size() - 1; ii >= 0; ii--) {
      Object obj = cList.get(ii);
      if (set.contains(obj) == false) {
        SGFigure f = (SGFigure) obj;
        deleteFigure(wnd, f);
        gc = true;
      }
      obj = null;
    }

    if (gc) {
      cList.clear();
      set.clear();
    }
    return true;
  }

  public static void deleteFigure(final SGDrawingWindow wnd, SGFigure f) {

    wnd.getFigureListInternal().remove(f);
    f.dispose();
  }
}
