package jp.riken.brain.ni.samuraigraph.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.application.SGMainFunctions.DataList;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.data.SGDataViewerDialog;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupSetInGraph;

/** Static helper for dialog lifecycle operations. */
final class SGDataDialogUtility {

  private SGDataDialogUtility() {}

  static DataList getVisibleDataList(List<SGFigure> figureList) {
    DataList ret = new DataList();
    List<SGData> dataList = new ArrayList<SGData>();
    Map<SGData, SGFigure> figureMap = new HashMap<SGData, SGFigure>();
    for (SGFigure f : figureList) {
      List<SGData> dList = f.getVisibleDataList();
      dataList.addAll(dList);
      for (SGData data : dList) {
        figureMap.put(data, f);
      }
    }
    ret.dataList = dataList;
    ret.figureMap = figureMap;
    return ret;
  }

  static SGDataAnimationDialog[] closeDataAnimationDialogsSub(
      final SGDataAnimationDialog[] dataAnimationDialogArray,
      List<SGData> removedDataList,
      Map<SGData, SGFigure> figureMap,
      SGDrawingWindow wnd,
      final boolean bUndo) {
    if (dataAnimationDialogArray.length == 0) {
      return dataAnimationDialogArray;
    }
    List<SGDataAnimationDialog> dgList = new ArrayList<SGDataAnimationDialog>();
    for (SGDataAnimationDialog dg : dataAnimationDialogArray) {
      SGData[] animationDataArray = dg.getDataArray();
      boolean bClose = false;
      SGData rData = null;
      for (SGData animationData : animationDataArray) {
        if (removedDataList.contains(animationData)) {
          rData = animationData;
          break;
        }
      }
      if (rData != null) {
        SGFigure figure = figureMap.get(rData);
        SGElementGroupSetInGraph gs =
            (SGElementGroupSetInGraph) figure.getGraphElement().getChild(rData);
        if (!wnd.existsOnUndo(bUndo, figure, gs.getData())) {
          bClose = true;
        }
      }
      if (bClose) {
        dg.close();
      } else {
        dgList.add(dg);
      }
    }
    return dgList.toArray(new SGDataAnimationDialog[dgList.size()]);
  }

  static SGDataAnimationDialog[] closeDataAnimationDialogsSub(
      final SGDataAnimationDialog[] dataAnimationDialogArray, List<SGData> removedDataList) {
    if (dataAnimationDialogArray.length == 0) {
      return dataAnimationDialogArray;
    }
    List<SGDataAnimationDialog> dgList = new ArrayList<SGDataAnimationDialog>();
    for (SGDataAnimationDialog dg : dataAnimationDialogArray) {
      SGData[] animationDataArray = dg.getDataArray();
      boolean found = false;
      for (SGData animationData : animationDataArray) {
        if (removedDataList.contains(animationData)) {
          found = true;
          break;
        }
      }
      if (found) {
        dg.close();
      } else {
        dgList.add(dg);
      }
    }
    return dgList.toArray(new SGDataAnimationDialog[dgList.size()]);
  }

  static SGDataViewerDialog[] closeDataViewerDialogsSub(
      final SGDataViewerDialog[] dataViewerDialogArray,
      List<SGData> dataList,
      Map<SGData, SGFigure> figureMap,
      SGDrawingWindow wnd,
      final boolean bUndo) {
    if (dataViewerDialogArray.length == 0) {
      return dataViewerDialogArray;
    }
    List<SGDataViewerDialog> dgList = new ArrayList<SGDataViewerDialog>();
    for (SGDataViewerDialog dg : dataViewerDialogArray) {
      SGData data = dg.getData();
      boolean bClose = false;
      if (dataList.contains(data)) {
        SGFigure figure = figureMap.get(data);
        SGElementGroupSetInGraph gs =
            (SGElementGroupSetInGraph) figure.getGraphElement().getChild(data);
        if (!wnd.existsOnUndo(bUndo, figure, gs.getData())) {
          bClose = true;
        }
        if (bClose) {
          gs.removeFocusedValueList(dg);
          dg.setVisible(false);
        }
      }
      if (!bClose) {
        dgList.add(dg);
      }
    }
    return dgList.toArray(new SGDataViewerDialog[dgList.size()]);
  }

  static SGDataViewerDialog[] closeDataViewerDialogsSub(
      final SGDataViewerDialog[] dataViewerDialogArray,
      List<SGData> dataList,
      Map<SGData, SGFigure> figureMap,
      SGDrawingWindow wnd) {
    if (dataViewerDialogArray.length == 0) {
      return dataViewerDialogArray;
    }
    List<SGDataViewerDialog> dgList = new ArrayList<SGDataViewerDialog>();
    for (SGDataViewerDialog dg : dataViewerDialogArray) {
      SGData data = dg.getData();
      if (dataList.contains(data)) {
        dg.setVisible(false);
        SGFigure figure = figureMap.get(data);
        SGElementGroupSetInGraph gs =
            (SGElementGroupSetInGraph) figure.getGraphElement().getChild(data);
        gs.removeFocusedValueList(dg);
      } else {
        dgList.add(dg);
      }
    }
    return dgList.toArray(new SGDataViewerDialog[dgList.size()]);
  }
}
