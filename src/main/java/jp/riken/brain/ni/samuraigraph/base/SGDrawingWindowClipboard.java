package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Clipboard operations for copied figures, objects and data. */
final class SGDrawingWindowClipboard implements SGIRootObjectConstants {

  private final SGDrawingWindow mWnd;

  /** The list of copied figures in this window. */
  private transient List<SGFigure> mCopiedFiguresList = new ArrayList<SGFigure>();

  /** The list of copied objects in this window such as labels or symbols. */
  private transient List<SGICopyable> mCopiedObjectsList = new ArrayList<SGICopyable>();

  /** The list of copied data objects in this window. */
  private transient List<SGData> mCopiedDataObjectsList = new ArrayList<SGData>();

  /** The list of names of copied data objects. */
  private transient List<String> mCopiedDataNameList = new ArrayList<String>();

  /** The list of properties of copied data objects. */
  private transient List<Map<Class<? extends SGIFigureElement>, SGProperties>>
      mCopiedDataPropertiesMapList =
          new ArrayList<Map<Class<? extends SGIFigureElement>, SGProperties>>();

  final transient List<SGISelectable> mPasteTargetList = new ArrayList<SGISelectable>();

  SGDrawingWindowClipboard(SGDrawingWindow wnd) {
    this.mWnd = wnd;
  }

  void disposeCopiedData() {
    for (int ii = 0; ii < this.mCopiedDataObjectsList.size(); ii++) {
      SGData data = this.mCopiedDataObjectsList.get(ii);
      data.dispose();
    }
    for (int ii = 0; ii < this.mCopiedDataPropertiesMapList.size(); ii++) {
      Map<Class<? extends SGIFigureElement>, SGProperties> map =
          this.mCopiedDataPropertiesMapList.get(ii);
      map.clear();
    }
    for (int ii = 0; ii < this.mCopiedObjectsList.size(); ii++) {
      Object obj = this.mCopiedObjectsList.get(ii);
      if (obj instanceof SGIDisposable) {
        SGIDisposable disp = (SGIDisposable) obj;
        disp.dispose();
      }
    }
    this.clearCopiedObjectsList();
  }

  /**
   * Returns a list of copied figures.
   *
   * @return a list of copied figures
   */
  public List<SGFigure> getCopiedFiguresList() {
    return new ArrayList<SGFigure>(this.mCopiedFiguresList);
  }

  /**
   * Paste the objects to the target figures.
   *
   * @param list a list of the objects.
   * @param dataList a list of a data objects.
   * @param nameList a list of a data name.
   * @param propertiesMapList a list of property map
   */
  public void pasteToFigures(
      List<SGICopyable> list,
      List<SGData> dataList,
      List<String> nameList,
      List<Map<Class<? extends SGIFigureElement>, SGProperties>> propertiesMapList) {

    List<SGFigure> fList = this.mWnd.getFocusedFigureList();
    if (fList.size() == 0) {
      return;
    }

    // paste to the target object
    for (int ii = 0; ii < fList.size(); ii++) {
      SGFigure figure = fList.get(ii);
      figure.paste(list);
      for (int jj = 0; jj < nameList.size(); jj++) {
        SGData data = dataList.get(jj);
        String name = nameList.get(jj);
        Map<Class<? extends SGIFigureElement>, SGProperties> map = propertiesMapList.get(jj);
        SGData dataNew = (SGData) data.copy();
        if (figure.addData(dataNew, name, map) == false) {
          throw new Error("Failed to add data.");
        }
      }
    }

    // repaint after pasted
    this.mWnd.repaintContentPane();

    // notify the change to the root
    this.mWnd.notifyToRoot();
  }

  /** Copy the focused objects. */
  public void doCopy() {
    this.copyFocusedObjects();
  }

  // Copy the focused objects.
  void copyFocusedObjects() {
    // get copied objects from all figures
    this.copyAllObjectsInVisibleFigures();

    // notify the copy command
    this.mWnd.notifyToListener(MENUBARCMD_COPY);

    // update the menu items
    this.mWnd.updateFocusedObjectItem();
  }

  /** Cut the focused objects. */
  public void doCut() {
    this.cutFocusedObjects();
  }

  // Cut focused objects.
  void cutFocusedObjects() {
    // get copied objects from all figures
    this.cutAllObjectsInVisibleFigures();

    // notify the cut command
    this.mWnd.notifyToListener(MENUBARCMD_CUT);

    // notify the change to the root
    this.mWnd.notifyToRoot();

    // update the menu items
    this.mWnd.updateFocusedObjectItem();

    // repaint
    this.mWnd.repaintContentPane();
  }

  /**
   * @param id
   * @param isCopy
   * @return
   */
  public boolean cutOrCopyFigure(final int id, final boolean isCopy) {
    // get the figure
    SGFigure f = this.mWnd.getFigure(id);
    if (f == null) {
      return false;
    }
    if (f.isVisible() == false) {
      return false;
    }

    // add to the attribute
    this.mCopiedFiguresList.add(f);

    // hide when cut the figure
    if (!isCopy) {
      this.mWnd.hideFigure(f);
    }

    // notify the command
    if (isCopy) {
      this.mWnd.notifyToListener(MENUBARCMD_COPY);
    } else {
      this.mWnd.notifyToListener(MENUBARCMD_CUT);
    }

    // notify the change to the root
    this.mWnd.notifyToRoot();

    // update the menu items
    this.mWnd.updateFocusedObjectItem();

    // repaint
    this.mWnd.repaintContentPane();

    return true;
  }

  /** Paste the copied objects. */
  public void doPaste() {
    this.pasteCopiedObjects();
  }

  // Paste the copied objects.
  void pasteCopiedObjects() {
    this.mWnd.notifyToListener(MENUBARCMD_PASTE);

    // notify the change to the root
    this.mWnd.notifyToRoot();
  }

  /** Duplicate the focused objects. */
  public void doDuplicate() {
    this.duplicateFocusedObjects();
  }

  // Duplicate the focused objects.
  void duplicateFocusedObjects() {
    ArrayList<SGFigure> list = this.mWnd.getVisibleFigureList();

    // duplicate child object of all figures
    for (int ii = 0; ii < list.size(); ii++) {
      SGFigure figure = list.get(ii);
      if (figure.duplicateFocusedObjects() == false) {
        return;
      }
    }

    // repaint after duplication
    this.mWnd.repaintContentPane();

    // notify the duplication command
    this.mWnd.notifyToListener(MENUBARCMD_DUPLICATE);

    // set unfocused the focused figures
    List<SGFigure> fList = this.mWnd.getFocusedFigureList();
    for (int ii = 0; ii < fList.size(); ii++) {
      SGFigure figure = fList.get(ii);
      this.mWnd.setFocusedFigure(figure, false);
    }

    // set focused the duplicated figures
    List<SGFigure> listNew = this.mWnd.getVisibleFigureList();
    for (int ii = 0; ii < listNew.size(); ii++) {
      SGFigure figure = listNew.get(ii);
      if (list.contains(figure) == false) {
        this.mWnd.setFocusedFigure(figure, true);
      }
    }

    // notify the change to the root
    this.mWnd.notifyToRoot();
  }

  /** Delete the focused objects. */
  public void doDelete() {
    this.deleteFocusedObjects();
  }

  // Delete the focused objects.
  void deleteFocusedObjects() {
    // hide all focused objects
    this.mWnd.hideSelectedObjects();

    // notify the change to the root
    this.mWnd.notifyToRoot();

    // update the menu items
    this.mWnd.updateDataItem();

    // repaint
    this.mWnd.repaintContentPane();
  }

  /** Cuts all objects in all visible figures. */
  void cutAllObjectsInVisibleFigures() {
    this.cutOrCopyAllObjectsInVisibleFigures(false);
  }

  /** Copies all objects in all visible figures. */
  void copyAllObjectsInVisibleFigures() {
    this.cutOrCopyAllObjectsInVisibleFigures(true);
  }

  /**
   * Cuts or copies all objects in all visible figures.
   *
   * @param isCopy true: copy, false: cut
   */
  void cutOrCopyAllObjectsInVisibleFigures(final boolean isCopy) {

    // get all visible figures
    List<SGFigure> fList = this.mWnd.getVisibleFigureList();

    // get objects from all visible figures
    List<SGICopyable> copiedObjList = new ArrayList<SGICopyable>();
    List<SGData> dataList = new ArrayList<SGData>();
    List<String> dataNameList = new ArrayList<String>();
    List<Map<Class<? extends SGIFigureElement>, SGProperties>> propertiesMapList =
        new ArrayList<Map<Class<? extends SGIFigureElement>, SGProperties>>();
    if (isCopy) {
      for (int ii = 0; ii < fList.size(); ii++) {
        SGFigure figure = fList.get(ii);

        // copied objects such as labels and symbols
        copiedObjList.addAll(figure.createCopiedObjects());

        // create copied data
        figure.createCopiedDataObjects(dataList, dataNameList, propertiesMapList);
      }
    } else {
      for (int ii = 0; ii < fList.size(); ii++) {
        SGFigure figure = fList.get(ii);

        // copied objects such as labels and symbols
        copiedObjList.addAll(figure.cutFocusedObjects());

        // create copied data
        figure.cutFocusedDataObjects(dataList, dataNameList, propertiesMapList);
      }
    }

    // clear all lists below
    this.clearCopiedObjectsList();

    // set to the attribute
    this.mCopiedObjectsList.addAll(copiedObjList);
    this.mCopiedDataObjectsList.addAll(dataList);
    this.mCopiedDataNameList.addAll(dataNameList);
    this.mCopiedDataPropertiesMapList.addAll(propertiesMapList);

    List<SGFigure> focusedFigureList = this.mWnd.getFocusedFigureList();
    for (int ii = 0; ii < focusedFigureList.size(); ii++) {
      this.mCopiedFiguresList.add(focusedFigureList.get(ii));
    }
  }

  /**
   * Returns the list of copied objects in this window.
   *
   * @return a list of copied objects
   */
  public List<SGICopyable> getCopiedObjectsList() {
    List<SGICopyable> list = new ArrayList<SGICopyable>();
    SGUtility.copyObjects(this.mCopiedObjectsList, list);
    return list;
  }

  /**
   * Returns the list of copied data objects in this window.
   *
   * @return a list of copied data objects
   */
  public List<SGData> getCopiedObjectsDataList() {
    List<SGData> list = new ArrayList<SGData>();
    SGUtility.copyObjects((List<? extends SGICopyable>) this.mCopiedDataObjectsList, list);
    return list;
  }

  /**
   * Returns the list of names of copied data objects in this window.
   *
   * @return a list of names of copied data objects
   */
  public List<String> getCopiedDataNameList() {
    List<String> list = new ArrayList<String>(this.mCopiedDataNameList);
    return list;
  }

  /**
   * Returns the list of properties of copied data objects in this window.
   *
   * @return a list of properties of copied data objects
   */
  public List<Map<Class<? extends SGIFigureElement>, SGProperties>>
      getCopiedDataPropertiesMapList() {
    List<Map<Class<? extends SGIFigureElement>, SGProperties>> list =
        new ArrayList<Map<Class<? extends SGIFigureElement>, SGProperties>>();
    for (int ii = 0; ii < this.mCopiedDataPropertiesMapList.size(); ii++) {
      Map<Class<? extends SGIFigureElement>, SGProperties> map =
          this.mCopiedDataPropertiesMapList.get(ii);
      list.add(new HashMap<Class<? extends SGIFigureElement>, SGProperties>(map));
    }
    return list;
  }

  /** Clear the list of copied objects. */
  public void clearCopiedObjectsList() {

    // disposes all copied objects
    for (SGICopyable cp : this.mCopiedObjectsList) {
      if (cp instanceof SGIDisposable) {
        SGIDisposable d = (SGIDisposable) cp;
        d.dispose();
      }
    }
    this.mCopiedObjectsList.clear();

    // disposes all copied data objects
    for (SGData d : this.mCopiedDataObjectsList) {
      d.dispose();
    }
    this.mCopiedDataObjectsList.clear();

    // clear other lists
    this.mCopiedDataNameList.clear();
    this.mCopiedDataPropertiesMapList.clear();
    this.mCopiedFiguresList.clear();
  }

  /** */
  void notifyPasteToFocusedFigures() {
    this.mPasteTargetList.clear();
    this.mPasteTargetList.addAll(this.mWnd.getFocusedObjectsList());
    this.mWnd.notifyToListener(MENUBARCMD_PASTE);
  }
}
