package jp.riken.brain.ni.samuraigraph.base;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
import javax.swing.*;
import org.w3c.dom.Document;

class SGDrawingWindowExportHelper {

  private final SGDrawingWindow owner;

  public SGDrawingWindowExportHelper(final SGDrawingWindow owner) {
    this.owner = owner;
  }

  boolean beforeExport(
      final SGDrawingWindow.ExportPanel ePanel,
      final SGDrawingWindow.InfoForExport info,
      final boolean silent) {
    float mag = owner.mMagnification;
    SGTuple2f value = owner.mClientPanel.getScrollRatio();
    float hValue = value.x;
    float vValue = value.y;
    List<SGFigure> list = owner.getVisibleFigureList();

    if (!silent) {
      // check whether the figures run off the edge of the paper
      boolean isInside = true;
      Rectangle pRect = owner.getPaperRect().getBounds();
      for (int ii = 0; ii < list.size(); ii++) {
        SGFigure figure = list.get(ii);
        Rectangle rect = figure.getBoundingBox().getBounds();
        if (pRect.contains(rect) == false) {
          isInside = false;
          break;
        }
      }
      if (!isInside) {
        SGUtility.showMessageDialog(
            owner,
            "Some figures run off the edge of paper.",
            "Warning",
            JOptionPane.WARNING_MESSAGE);
      }
    }

    // preprocessing for image export
    for (int ii = 0; ii < list.size(); ii++) {
      SGFigure figure = list.get(ii);
      figure.beforeExport();
    }

    // record the location of figures
    SGTuple2f[] locationArray = new SGTuple2f[list.size()];
    for (int ii = 0; ii < list.size(); ii++) {
      SGFigure figure = list.get(ii);
      locationArray[ii] = new SGTuple2f(figure.mGraphRectX, figure.mGraphRectY);
    }

    // zoom
    owner.zoom(1.0f);

    // set the location of figures
    Rectangle2D cRect = owner.getClientRect();
    for (int ii = 0; ii < list.size(); ii++) {
      SGFigure figure = list.get(ii);
      figure.setGraphRectLocation(
          figure.getGraphRectX() - (float) cRect.getX(),
          figure.getGraphRectY() - (float) cRect.getY());
    }

    //
    // set to the export panel
    //

    // set the location and the size of preview dialog
    final float width = owner.mClientPanel.getPaperWidth();
    final float height = owner.mClientPanel.getPaperHeight();

    // set the layered pane
    ePanel.setOpaque(false);
    ePanel.setLocation(0, 0);
    ePanel.setSize((int) width, (int) height);

    // add figures to the export panel
    Rectangle2D vBounds = new Rectangle2D.Float(0.0f, 0.0f, width, height);
    for (int ii = 0; ii < list.size(); ii++) {
      SGFigure figure = list.get(ii);
      ePanel.add(figure);
      figure.setViewBounds(vBounds);
    }

    // set an image
    Image image = owner.mClientPanel.getImage();
    if (image != null) {
      SGTuple2f location = owner.mClientPanel.getImageLocation();
      SGTuple2f size = owner.mClientPanel.getImageSize();
      final float f = owner.mClientPanel.getImageScalingFactor();
      ePanel.setImage(image, location.x, location.y, size.x, size.y, f);
    }

    // set invisible
    for (int ii = 0; ii < list.size(); ii++) {
      SGFigure figure = list.get(ii);
      figure.setVisible(false);
    }

    for (int ii = 0; ii < list.size(); ii++) {
      SGFigure figure = list.get(ii);
      figure.setMode(owner.MODE_EXPORT_AS_IMAGE);
    }

    // set information
    info.mag = mag;
    info.hValue = hValue;
    info.vValue = vValue;
    info.locationArray = locationArray;
    info.visibleFigureList = list;

    return true;
  }

  boolean afterExport(
      final SGDrawingWindow.ExportPanel ePanel,
      final SGDrawingWindow.InfoForExport info,
      final boolean silent) {
    float mag = info.mag;
    float hValue = info.hValue;
    float vValue = info.vValue;
    SGTuple2f[] locationArray = info.locationArray;
    List<SGFigure> fList = info.visibleFigureList;

    // zoom
    owner.zoom(mag);

    // set scroll value
    owner.mClientPanel.setScrollRatio(vValue, hValue);

    // set the location
    // SGTuple2f vpSize = owner.getViewportSize();
    for (int ii = 0; ii < fList.size(); ii++) {
      SGFigure figure = fList.get(ii);
      figure.mGraphRectX = locationArray[ii].x;
      figure.mGraphRectY = locationArray[ii].y;
      figure.updateGraphRect();
      figure.setViewBounds();
    }

    // postprocessing for image export
    for (int ii = 0; ii < fList.size(); ii++) {
      SGFigure figure = fList.get(ii);
      figure.afterExport();
    }

    owner.repaintContentPane();

    return true;
  }

  boolean toImage(final int mode, final boolean silent) {
    final int width = (int) owner.mClientPanel.getPaperWidth();
    final int height = (int) owner.mClientPanel.getPaperHeight();

    SGDrawingWindow.InfoForExport info = new SGDrawingWindow.InfoForExport();
    SGDrawingWindow.ExportPanel target = new SGDrawingWindow.ExportPanel();
    target.setOpaque(true);
    target.setBackground(owner.mClientPanel.getPaperColor());
    target.setPreferredSize(new Dimension(width, height));

    SGIImageExportManager man = owner.mImageExportManager;
    man.preprocessExport(owner);

    owner.beforeExport(target, info, silent);

    boolean ret;
    switch (mode) {
      case SGIRootObjectConstants.EXPORT:
        {
          // export as image
          ret = man.export(target, owner, width, height, silent);
          break;
        }

      case SGIRootObjectConstants.PRINT:
        {
          // print as image
          ret = man.print(target, owner, width, height, silent);
          break;
        }

      default:
        {
          ret = false;
        }
    }

    owner.afterExport(target, info, silent);

    return ret;
  }

  public boolean createDOMTree(Document document, final SGExportParameter params) {
    boolean flag;
    switch (owner.mPropertyFileCreationModeOfFigures) {
      case SGIRootObjectConstants.ALL_FIGURES:
        {
          flag = owner.createDOMTreeForAllFigures(document, params);
          break;
        }

      case SGIRootObjectConstants.FOCUSED_FIGURES_FOR_COPY:
        {
          flag = owner.createDOMTreeForFocusedFiguresForDuplication(document, params);
          break;
        }

      case SGIRootObjectConstants.FOCUSED_FIGURES_IN_BOUNDING_BOX:
        {
          flag = owner.createDOMTreeForFocusedFiguresInBoundingBox(document, params);
          break;
        }

      case SGIRootObjectConstants.FOCUSED_FIGURES_FOR_DUPLICATION:
        {
          flag = owner.createDOMTreeForFocusedFiguresForDuplication(document, params);
          break;
        }

      default:
        {
          throw new Error();
        }
    }

    return flag;
  }

  public String getCommandString(SGExportParameter params) {
    SGIConstants.OPERATION type = params.getType();

    StringBuilder sb = new StringBuilder();

    // creates the command for owner window
    String wndCommands =
        SGCommandUtility.createCommandString(
            owner.COM_WINDOW, null, owner.getCommandPropertyMap(params));
    sb.append(wndCommands);

    // creates the command of figures
    List<SGFigure> figureList = owner.getVisibleFigureList();
    for (SGFigure f : figureList) {
      if (SGIConstants.OPERATION.SAVE_INTO_FILE_ATTRIBUTE.equals(type)) {
        List<SGData> dataList = f.getVisibleDataList();
        SGDataExportParameter exportParams = (SGDataExportParameter) params;
        boolean found = false;
        for (SGData data : dataList) {
          if (exportParams.canExport(data)) {
            found = true;
            break;
          }
        }
        if (!found) {
          continue;
        }
      }
      String fCommands = f.getCommandString(params);
      sb.append(fCommands);
    }
    return sb.toString();
  }
}
