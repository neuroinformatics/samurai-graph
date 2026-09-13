package jp.riken.brain.ni.samuraigraph.base;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Iterator;
import javax.print.attribute.standard.MediaSize;
import javax.swing.*;

class SGDrawingWindowActionHandler {

  private final SGDrawingWindow owner;

  public SGDrawingWindowActionHandler(final SGDrawingWindow owner) {
    this.owner = owner;
  }

  public void actionPerformed(final ActionEvent e) {
    final String command = e.getActionCommand();
    final Object source = e.getSource();

    if (command.equals(owner.MENUBARCMD_SAVE_PROPERTY)) {
      owner.mPropertyFileCreationModeOfFigures = SGIRootObjectConstants.ALL_FIGURES;
      owner.notifyToListener(owner.MENUBARCMD_SAVE_PROPERTY);
    } else if (command.equals(owner.MENUBARCMD_SAVE_DATASET)) {
      owner.mPropertyFileCreationModeOfFigures = SGIRootObjectConstants.ALL_FIGURES;
      owner.notifyToListener(owner.MENUBARCMD_SAVE_DATASET);
    } else if (command.equals(owner.MENUBARCMD_DELETE)) {
      owner.deleteFocusedObjects();
    } else if (command.equals(owner.MENUBARCMD_CUT)) {
      owner.cutFocusedObjects();
    } else if (command.equals(owner.MENUBARCMD_COPY)) {
      owner.copyFocusedObjects();
    } else if (command.equals(owner.MENUBARCMD_PASTE)) {
      owner.pasteCopiedObjects();
    } else if (command.equals(owner.MENUBARCMD_DUPLICATE)) {
      owner.duplicateFocusedObjects();
    } else if (command.equals(owner.MENUBARCMD_DELETE_BACKGROUND_IMAGE)) {
      owner.deleteImage();
    } else if (command.equals(owner.MENUBARCMD_BRING_TO_FRONT)) {
      owner.bringFocusedObjectsToFront();
    } else if (command.equals(owner.MENUBARCMD_BRING_FORWARD)) {
      owner.bringFocusedObjectsForward();
    } else if (command.equals(owner.MENUBARCMD_SEND_BACKWARD)) {
      owner.sendFocusedObjectsBackward();
    } else if (command.equals(owner.MENUBARCMD_SEND_TO_BACK)) {
      owner.sendFocusedObjectsToBack();
    } else if (command.equals(owner.MENUBARCMD_CLEAR_UNDO_BUFFER)) {
      owner.clearUndoBuffer();
    } else if (command.equals(owner.MENUBARCMD_PAPER_A4_PORTRAIT)) {
      owner.setPaperSizeDirectly(MediaSize.ISO.A4, true);
    } else if (command.equals(owner.MENUBARCMD_PAPER_B5_PORTRAIT)) {
      owner.setPaperSizeDirectly(MediaSize.ISO.B5, true);
    } else if (command.equals(owner.MENUBARCMD_PAPER_USLETTER_PORTRAIT)) {
      owner.setPaperSizeDirectly(MediaSize.NA.LETTER, true);
    } else if (command.equals(owner.MENUBARCMD_PAPER_A4_LANDSCAPE)) {
      owner.setPaperSizeDirectly(MediaSize.ISO.A4, false);
    } else if (command.equals(owner.MENUBARCMD_PAPER_B5_LANDSCAPE)) {
      owner.setPaperSizeDirectly(MediaSize.ISO.B5, false);
    } else if (command.equals(owner.MENUBARCMD_PAPER_USLETTER_LANDSCAPE)) {
      owner.setPaperSizeDirectly(MediaSize.NA.LETTER, false);
    } else if (command.equals(owner.MENUBARCMD_BOUNDING_BOX)) {
      owner.setBoundingBox();
    } else if (command.equals(owner.MENUBARCMD_PAPER_USER_CUSTOMIZE)) {
      owner.showPropertyDialog();
    } else if (command.equals(owner.MENUBARCMD_MODE)) {
      final int mode =
          (owner.getMode() == owner.MODE_EXPORT_AS_IMAGE)
              ? owner.MODE_DISPLAY
              : owner.MODE_EXPORT_AS_IMAGE;
      owner.setMode(mode);
      owner.updateModeMenuItems();
    } else if (command.equals(owner.MENUBARCMD_AUTO_ARRANGEMENT)) {
      owner.alignFigures();
    } else if (command.equals(owner.MENUBARCMD_GRID_VISIBLE)) {
      owner.mClientPanel.setGridLineVisible(!owner.mClientPanel.isGridLineVisible());
      owner.updateGridItems();

      owner.setChanged(true);
      owner.notifyToRoot();
      owner.repaintContentPane();
    } else if (command.equals(owner.MENUBARCMD_PLUS_GRID)) {
      final double value = owner.mClientPanel.getGridLineInterval() * SGIConstants.CM_POINT_RATIO;
      final double min = SGIRootObjectConstants.GRID_INTERVAL_MIN_VALUE;
      final double max = SGIRootObjectConstants.GRID_INTERVAL_MAX_VALUE;
      final double step = SGIRootObjectConstants.GRID_INTERVAL_STEP_SIZE;
      double valueNew = SGUtilityNumber.stepValue(true, value, min, max, step, 0.001f);
      final int indexNew = (int) Math.rint(valueNew / step);
      final int indexMax = (int) Math.rint(max / step);
      if (indexNew != indexMax + 1) {
        if (valueNew > max) {
          valueNew = max;
        }
        owner.mClientPanel.setGridLineInterval((float) valueNew / SGIConstants.CM_POINT_RATIO);
        owner.updateGridItems();
        owner.repaintContentPane();

        owner.setChanged(true);
        owner.notifyToRoot();
      }
    } else if (command.equals(owner.MENUBARCMD_MINUS_GRID)) {
      final double value = owner.mClientPanel.getGridLineInterval() * SGIConstants.CM_POINT_RATIO;
      final double min = SGIRootObjectConstants.GRID_INTERVAL_MIN_VALUE;
      final double max = SGIRootObjectConstants.GRID_INTERVAL_MAX_VALUE;
      final double step = SGIRootObjectConstants.GRID_INTERVAL_STEP_SIZE;
      double valueNew = SGUtilityNumber.stepValue(false, value, min, max, step, 0.001f);
      final int indexNew = (int) Math.rint(valueNew / step);
      final int indexMin = (int) Math.rint(min / step);
      if (indexNew != indexMin - 1) {
        if (valueNew < min) {
          valueNew = min;
        }
        owner.mClientPanel.setGridLineInterval((float) valueNew / SGIConstants.CM_POINT_RATIO);
        owner.updateGridItems();
        owner.repaintContentPane();

        owner.setChanged(true);
        owner.notifyToRoot();
      }
    } else if (command.equals(owner.MENUBARCMD_SNAP_TO_GRID)) {
      SGFigure.setSnappingToGrid(!SGFigure.isSnappingToGrid());
      owner.updateSnapToGridItems();
    } else if (command.equals(owner.MENUBARCMD_ZOOM_IN)) {
      final int mag = (int) (owner.getMagnificationPercent());
      final int[] array = SGIRootObjectConstants.MAGNIFICATION_ARRAY;
      for (int ii = array.length - 1; ii >= 0; ii--) {
        if (array[ii] > mag) {
          owner.setZoomValue(Integer.valueOf(array[ii]));
          break;
        }
      }
    } else if (command.equals(owner.MENUBARCMD_ZOOM_OUT)) {
      final int mag = (int) (owner.getMagnificationPercent());
      final int[] array = SGIRootObjectConstants.MAGNIFICATION_ARRAY;
      for (int ii = 0; ii < array.length; ii++) {
        if (array[ii] < mag) {
          owner.setZoomValue(Integer.valueOf(array[ii]));
          break;
        }
      }
    } else if (command.equals(owner.MENUBARCMD_DEFAULT_ZOOM)) {
      owner.setDefaultZoom();
    } else if (command.equals(owner.MENUBARCMD_ZOOM_WAY_OUT)) {
      owner.zoomWayOut();
    } else if (command.equals(owner.MENUBARCMD_AUTO_ZOOM)) {
      owner.setAutoZoom(!owner.isAutoZoom());
    } else if (command.equals(owner.MENUBARCMD_LOCK)) {
      owner.setLocked(!owner.isLocked());
    } else if (Arrays.asList(SGIRootObjectConstants.INSERT_MENUBARCMD_ARRAY).contains(command)) {
      // menu to insert a symbol

      boolean selected;

      // synchronize the tool bar and the menu bar
      if (source.equals(owner.mMenuBar)) {
        selected = owner.mMenuBar.isInsertToggleItemSelected(command);
      } else if (source.equals(owner.mToolBar)) {
        selected = owner.mToolBar.isInsertToggleButtonSelected(command);
      } else {
        return;
      }

      owner.setInsertToggleItemsUnselected();
      owner.setInsertFlag(command, selected);
      owner.updateInsertItems();

      // change the mouse cursor
      if (selected) {
        final Cursor cur = new Cursor(Cursor.CROSSHAIR_CURSOR);
        owner.setCursor(cur);
      } else {
        owner.setCursor(null);
      }
    } else if (Arrays.asList(owner.TOOLBAR_MENUCMD_ARRAY).contains(command)) {
      // menu for the tool bar
      if (source.equals(owner.mMenuBar)) {
        owner.updateToolBarVisibleItems();
      } else if (source.equals(owner.mToolBar)) {
        owner.updateToolBarVisibleMenuItems();
      }
      owner.notifyPropertyChange(SGIRootObjectConstants.PROPERTY_NAME_TOOL_BAR);
    } else {
      owner.notifyToListener(command);
    }
  }

  public SGPropertyResults setProperties(SGPropertyMap map) {
    SGPropertyResults result = new SGPropertyResults();

    // prepare
    if (owner.prepare() == false) {
      return null;
    }

    Iterator<String> itr = map.getKeyIterator();
    while (itr.hasNext()) {
      String key = itr.next();
      String value = map.getValueString(key);

      if (SGIRootObjectConstants.COM_PAPER_SIZE.equalsIgnoreCase(key)) {
        String[] strArray = SGUtilityText.getStringsInBracket(value);
        if (strArray == null) {
          result.putResult(
              SGIRootObjectConstants.COM_PAPER_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (strArray.length != 2) {
          result.putResult(
              SGIRootObjectConstants.COM_PAPER_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        String str1 = strArray[0];
        String str2 = strArray[1];
        MediaSize size = SGUtilityText.getMediaSize(str1);
        if (size == null) {
          result.putResult(
              SGIRootObjectConstants.COM_PAPER_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        Boolean portrait = SGUtilityText.isPortrait(str2);
        if (portrait == null) {
          result.putResult(
              SGIRootObjectConstants.COM_PAPER_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (owner.mClientPanel.setPaperSize(size, portrait) == false) {
          result.putResult(
              SGIRootObjectConstants.COM_PAPER_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(SGIRootObjectConstants.COM_PAPER_SIZE, SGPropertyResults.SUCCEEDED);
        continue;
      }

      String comKey = SGDrawingWindowPropertyIO.getComKey(key);
      if (comKey == null) {
        continue;
      }
      Boolean r = SGDrawingWindowPropertyIO.applyStringValue(owner, key, value);
      if (r.booleanValue()) {
        result.putResult(comKey, SGPropertyResults.SUCCEEDED);
      } else {
        result.putResult(comKey, SGPropertyResults.INVALID_INPUT_VALUE);
      }
    }

    // commit the changes
    if (owner.commit() == false) {
      return null;
    }
    owner.notifyToRoot();
    owner.repaintContentPane();

    return result;
  }
}
