package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/** Shows property dialogs for the objects of a drawing window. */
final class SGDrawingWindowPropertyDialogUtility {

  private SGDrawingWindowPropertyDialogUtility() {}

  public static boolean showPropertyDialogForSelectedFigures(final SGDrawingWindow wnd) {

    List<SGFigure> figList = wnd.getFocusedFigureList();
    List<SGPropertyDialog> dList = new ArrayList<SGPropertyDialog>();
    for (int ii = 0; ii < figList.size(); ii++) {
      SGFigure fig = figList.get(ii);
      SGPropertyDialog dg = fig.getPropertyDialog();
      if (dg != null) {
        dList.add(dg);
      }
    }

    // clear focused objects in figures
    List<SGFigure> listAll = wnd.getFigureList();
    for (int ii = 0; ii < listAll.size(); ii++) {
      SGFigure fig = listAll.get(ii);
      fig.clearFocusedObjects();
    }

    // add listeners to the property dialog
    SGPropertyDialog dg = dList.get(0);
    List<SGIPropertyDialogObserver> lList = new ArrayList<SGIPropertyDialogObserver>();
    lList.addAll(figList);
    showPropertyDialog(wnd, dg, lList);

    return true;
  }

  public static boolean showPropertyDialogForSelectedObjects(
      final SGDrawingWindow wnd, final SGFigure figure, final SGIFigureElement element) {

    ArrayList<SGFigure> figList = wnd.getVisibleFigureList();

    // clears focused objects
    clearFocusedObjects(wnd, figure, element, figList);

    // gets observers
    List<SGIPropertyDialogObserver> obsList =
        getSelectedPropertyDialogObserverList(wnd, element, figList);
    if (obsList.size() == 0) {
      return false;
    }

    // gets property dialogs
    List<SGPropertyDialog> dList = getPropertyDialogList(wnd, obsList);
    if (dList == null || dList.size() == 0) {
      return false;
    }

    // show the property dialog
    SGPropertyDialog dg = dList.get(0);
    showPropertyDialog(wnd, dg, obsList);

    return true;
  }

  public static boolean showPropertyDialogForAllVisibleObjects(
      final SGDrawingWindow wnd, final SGFigure figure, final SGIFigureElement element) {

    ArrayList<SGFigure> figList = wnd.getVisibleFigureList();

    // clears focused objects
    clearFocusedObjects(wnd, figure, element, figList);

    // gets observers
    List<SGIPropertyDialogObserver> obsList =
        getVisiblePropertyDialogObserverList(wnd, element, figList);
    if (obsList.size() == 0) {
      return false;
    }

    // gets property dialogs
    List<SGPropertyDialog> dList = getPropertyDialogList(wnd, obsList);
    if (dList == null || dList.size() == 0) {
      return false;
    }

    // show the property dialog
    SGPropertyDialog dg = dList.get(0);
    showPropertyDialog(wnd, dg, obsList);

    return true;
  }

  public static boolean showPropertyDialogForAllObjects(
      final SGDrawingWindow wnd, final SGFigure figure, final SGIFigureElement element) {

    ArrayList<SGFigure> figList = wnd.getVisibleFigureList();

    // clears focused objects
    clearFocusedObjects(wnd, figure, element, figList);

    // gets observers
    List<SGIPropertyDialogObserver> obsList =
        getAllPropertyDialogObserverList(wnd, element, figList);
    if (obsList.size() == 0) {
      return false;
    }

    // gets property dialogs
    List<SGPropertyDialog> dList = getPropertyDialogList(wnd, obsList);
    if (dList == null || dList.size() == 0) {
      return false;
    }

    // show the property dialog
    SGPropertyDialog dg = dList.get(0);
    showPropertyDialog(wnd, dg, obsList);

    return true;
  }

  public static void clearFocusedObjects(
      final SGDrawingWindow wnd,
      final SGFigure figure,
      final SGIFigureElement element,
      ArrayList<SGFigure> figList) {

    // sets the class object of property observer
    Class<?> cl = element.getPropertyDialogObserverClass();
    for (int ii = 0; ii < figList.size(); ii++) {
      SGFigure fig = figList.get(ii);
      fig.setPropertyDialogObserverClass(cl);
    }

    // clears focused objects
    for (int ii = 0; ii < figList.size(); ii++) {
      SGFigure fig = figList.get(ii);
      fig.clearFocusedObjects(element);
      if (fig.equals(figure) == false) {
        fig.setSelected(false);
      }
    }
    wnd.repaintContentPane();
  }

  public static List<SGIPropertyDialogObserver> getPropertyDialogObserverList(
      final SGDrawingWindow wnd,
      SGIFigureElement element,
      ArrayList<SGFigure> figList,
      PROPERTY_SETTING_CONDITION condition) {

    List<SGIPropertyDialogObserver> obsList = new ArrayList<>();
    Class<?> cl = element.getClass();
    Class<?> obsClass = element.getPropertyDialogObserverClass();
    for (int ii = 0; ii < figList.size(); ii++) {
      SGFigure fig = figList.get(ii);
      SGIFigureElement el = fig.getIFigureElement(cl);
      if (el == null) {
        continue;
      }
      List<SGIPropertyDialogObserver> list = null;
      if (PROPERTY_SETTING_CONDITION.SELECTED.equals(condition)) {
        list = el.getSelectedPropertyDialogObserverList(obsClass);
      } else if (PROPERTY_SETTING_CONDITION.VISIBLE.equals(condition)) {
        list = el.getVisiblePropertyDialogObserverList(obsClass);
      } else if (PROPERTY_SETTING_CONDITION.ALL.equals(condition)) {
        list = el.getAllPropertyDialogObserverList(obsClass);
      }
      if (list == null || list.size() == 0) {
        continue;
      }
      obsList.addAll(list);
    }
    return obsList;
  }

  public static List<SGIPropertyDialogObserver> getSelectedPropertyDialogObserverList(
      final SGDrawingWindow wnd, SGIFigureElement element, ArrayList<SGFigure> figList) {

    return getPropertyDialogObserverList(
        wnd, element, figList, PROPERTY_SETTING_CONDITION.SELECTED);
  }

  public static List<SGIPropertyDialogObserver> getVisiblePropertyDialogObserverList(
      final SGDrawingWindow wnd, SGIFigureElement element, ArrayList<SGFigure> figList) {

    return getPropertyDialogObserverList(wnd, element, figList, PROPERTY_SETTING_CONDITION.VISIBLE);
  }

  public static List<SGIPropertyDialogObserver> getAllPropertyDialogObserverList(
      final SGDrawingWindow wnd, SGIFigureElement element, ArrayList<SGFigure> figList) {

    return getPropertyDialogObserverList(wnd, element, figList, PROPERTY_SETTING_CONDITION.ALL);
  }

  public static List<SGPropertyDialog> getPropertyDialogList(
      final SGDrawingWindow wnd, List<SGIPropertyDialogObserver> obsList) {

    List<SGPropertyDialog> dList = new ArrayList<SGPropertyDialog>();
    for (int ii = 0; ii < obsList.size(); ii++) {
      SGIPropertyDialogObserver obs = obsList.get(ii);
      SGPropertyDialog dg = obs.getPropertyDialog();
      dList.add(dg);
    }
    for (int ii = 0; ii < dList.size() - 1; ii++) {
      Object obj1 = dList.get(ii);
      for (int jj = ii + 1; jj < dList.size(); jj++) {
        Object obj2 = dList.get(jj);
        if (obj1.getClass().equals(obj2.getClass()) == false) {
          SGUtility.showMessageDialog(
              wnd,
              "Object type is different.",
              "Failed to show the property dialog.",
              JOptionPane.WARNING_MESSAGE);
          return null;
        }
      }
    }
    return dList;
  }

  public static void showPropertyDialog(
      final SGDrawingWindow wnd, SGPropertyDialog dg, SGIPropertyDialogObserver l) {

    ArrayList<SGIPropertyDialogObserver> list = new ArrayList<>();
    list.add(l);
    showPropertyDialog(wnd, dg, list);
  }

  public static void showPropertyDialog(
      final SGDrawingWindow wnd, SGPropertyDialog dg, List<SGIPropertyDialogObserver> lList) {

    // add dialog observers
    for (int ii = 0; ii < lList.size(); ii++) {
      SGIPropertyDialogObserver l = lList.get(ii);
      dg.addPropertyDialogObserver(l);
      l.prepare();
    }

    // set properties to dialog
    dg.setDialogProperty();
    dg.setLocation(wnd.getLocation());

    // show property dialog
    dg.setVisible(true);

    // remove all dialog observers
    dg.removeAllPropertyDialogObserver();

    // when the OK button is pressed, update the history tree
    final int closeOption = dg.getCloseOption();
    if (closeOption == SGDialog.OK_OPTION) {
      wnd.notifyToRoot();
    }
  }

  enum PROPERTY_SETTING_CONDITION {
    SELECTED,
    VISIBLE,
    ALL,
  };
}
