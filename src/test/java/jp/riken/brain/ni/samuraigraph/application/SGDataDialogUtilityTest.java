package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import jp.riken.brain.ni.samuraigraph.application.SGMainFunctions.DataList;
import jp.riken.brain.ni.samuraigraph.data.SGDataViewerDialog;
import org.junit.jupiter.api.Test;

/** Unit tests for the dialog lifecycle utility helpers. */
class SGDataDialogUtilityTest {

  @Test
  void emptyFigureListYieldsAnEmptyDataList() {
    DataList ret = SGDataDialogUtility.getVisibleDataList(new ArrayList<>());
    assertTrue(ret.dataList.isEmpty());
    assertTrue(ret.figureMap.isEmpty());
  }

  @Test
  void emptyAnimationDialogArrayReturnsSameInstance() {
    SGDataAnimationDialog[] empty = new SGDataAnimationDialog[0];
    assertSame(empty, SGDataDialogUtility.closeDataAnimationDialogsSub(empty, new ArrayList<>()));
  }

  @Test
  void emptyViewerDialogArrayReturnsSameInstance() {
    SGDataViewerDialog[] empty = new SGDataViewerDialog[0];
    assertSame(
        empty,
        SGDataDialogUtility.closeDataViewerDialogsSub(empty, new ArrayList<>(), null, null, true));
  }
}
