package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the property dialog utility of a drawing window.
 *
 * <p>These tests need a display because the window is a Swing frame.
 */
class SGDrawingWindowPropertyDialogUtilityTest {

  private SGDrawingWindow wnd;

  @AfterEach
  void disposeWindow() {
    if (this.wnd != null) {
      this.wnd.dispose();
      this.wnd = null;
    }
  }

  private SGDrawingWindow createWindow() {
    SGDrawingWindow wnd = new SGDrawingWindow();
    wnd.init();
    this.wnd = wnd;
    return wnd;
  }

  @Test
  void emptyWindowReturnsNoObservers() {
    SGDrawingWindow wnd = this.createWindow();
    jp.riken.brain.ni.samuraigraph.figure.SGFigureElementAxis element =
        new jp.riken.brain.ni.samuraigraph.figure.SGFigureElementAxis();
    List<SGIPropertyDialogObserver> obsList =
        SGDrawingWindowPropertyDialogUtility.getSelectedPropertyDialogObserverList(
            wnd, element, new ArrayList<>());
    assertNotNull(obsList);
    assertTrue(obsList.isEmpty());
    List<SGIPropertyDialogObserver> visibleList =
        SGDrawingWindowPropertyDialogUtility.getVisiblePropertyDialogObserverList(
            wnd, element, new ArrayList<>());
    assertTrue(visibleList.isEmpty());
    List<SGIPropertyDialogObserver> allList =
        SGDrawingWindowPropertyDialogUtility.getAllPropertyDialogObserverList(
            wnd, element, new ArrayList<>());
    assertTrue(allList.isEmpty());
    assertEquals(
        0, SGDrawingWindowPropertyDialogUtility.getPropertyDialogList(wnd, obsList).size());
  }
}
