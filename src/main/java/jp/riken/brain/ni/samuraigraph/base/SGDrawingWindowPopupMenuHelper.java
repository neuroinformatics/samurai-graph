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

import java.awt.Component;
import java.awt.Point;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/** Collaborator that builds and shows the pop-up menu for the selected figures. */
class SGDrawingWindowPopupMenuHelper {

  private final SGDrawingWindow owner;

  SGDrawingWindowPopupMenuHelper(final SGDrawingWindow owner) {
    this.owner = owner;
  }

  boolean showPopupMenuForSelectedFigures(JComponent component, final int x, final int y) {
    List<SGFigure> figList = this.owner.getFocusedFigureList();

    // get properties fo figures
    final int num = figList.size();
    boolean[] legendVisibleArray = new boolean[num];
    boolean[] colorBarVisibleArray = new boolean[num];
    boolean[] axisScaleVisibleArray = new boolean[num];
    boolean[] gridVisibleArray = new boolean[num];
    boolean[] dataAnchoredArray = new boolean[num];
    boolean[] horizontalAxis1VisibleArray = new boolean[num];
    boolean[] horizontalAxis2VisibleArray = new boolean[num];
    boolean[] verticalAxis1VisibleArray = new boolean[num];
    boolean[] verticalAxis2VisibleArray = new boolean[num];
    boolean[] legendAvailableArray = new boolean[num];
    boolean[] colorBarAvailableArray = new boolean[num];
    boolean[] alignmentBarsAvailableArray = new boolean[num];
    boolean[] animationAvailableArray = new boolean[num];
    for (int ii = 0; ii < num; ii++) {
      SGFigure fig = figList.get(ii);
      legendVisibleArray[ii] = fig.isLegendVisible();
      colorBarVisibleArray[ii] = fig.isColorBarVisible();
      axisScaleVisibleArray[ii] = fig.isAxisScaleVisible();
      gridVisibleArray[ii] = fig.isGridVisible();
      dataAnchoredArray[ii] = fig.isDataAnchored();
      SGIFigureElementAxis aElement = fig.getAxisElement();
      horizontalAxis1VisibleArray[ii] =
          aElement.isAxisVisible(SGFigureElementAxisConstants.AXIS_HORIZONTAL_1);
      horizontalAxis2VisibleArray[ii] =
          aElement.isAxisVisible(SGFigureElementAxisConstants.AXIS_HORIZONTAL_2);
      verticalAxis1VisibleArray[ii] =
          aElement.isAxisVisible(SGFigureElementAxisConstants.AXIS_VERTICAL_1);
      verticalAxis2VisibleArray[ii] =
          aElement.isAxisVisible(SGFigureElementAxisConstants.AXIS_VERTICAL_2);
      legendAvailableArray[ii] = fig.isLegendAvailable();
      colorBarAvailableArray[ii] = fig.isColorBarAvailable();
      alignmentBarsAvailableArray[ii] = fig.isAlignmentBarsAvailable();
      animationAvailableArray[ii] = fig.hasAnimationAvailableData();
    }

    Boolean legendVisible = SGUtility.checkEquality(legendVisibleArray);
    Boolean colorBarVisible = SGUtility.checkEquality(colorBarVisibleArray);
    Boolean axisScaleVisible = SGUtility.checkEquality(axisScaleVisibleArray);
    Boolean gridVisible = SGUtility.checkEquality(gridVisibleArray);
    Boolean dataAnchored = SGUtility.checkEquality(dataAnchoredArray);
    Boolean horizontalAxis1Visible = SGUtility.checkEquality(horizontalAxis1VisibleArray);
    Boolean horizontalAxis2Visible = SGUtility.checkEquality(horizontalAxis2VisibleArray);
    Boolean verticalAxis1Visible = SGUtility.checkEquality(verticalAxis1VisibleArray);
    Boolean verticalAxis2Visible = SGUtility.checkEquality(verticalAxis2VisibleArray);
    Boolean legendAvailable = SGUtility.checkEquality(legendAvailableArray);
    Boolean colorBarAvailable = SGUtility.checkEquality(colorBarAvailableArray);
    Boolean alignmentBarsAvailable = SGUtility.checkEquality(alignmentBarsAvailableArray);
    Boolean animationAvailable = SGUtility.checkEquality(animationAvailableArray);

    SGFigurePopupMenu p = new SGFigurePopupMenu(figList);
    p.addPopupMenuListener(
        new PopupMenuListener() {
          // Adds a PopupMenuListener to refresh the attributes when popup menu becomes
          // invisible.
          @Override
          public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

          @Override
          public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            Point pos = owner.getMousePosition();
            if (pos != null) {
              owner.mMousePressLocation = pos;
              owner.mTempMouseLocation.setLocation(pos);
            }
          }

          @Override
          public void popupMenuCanceled(PopupMenuEvent e) {}
        });

    // set enabled or disabled the menu items
    Component[] components = p.getComponents();
    for (int ii = 0; ii < components.length; ii++) {
      if (components[ii] instanceof JMenuItem) {
        final JMenuItem item = (JMenuItem) components[ii];
        final String command = item.getActionCommand();
        if (SGFigure.MENUCMD_LEGEND_VISIBLE.equals(command)) {
          final boolean b = (legendAvailable != null) ? legendAvailable.booleanValue() : false;
          item.setEnabled(b);
        } else if (SGFigure.MENUCMD_COLOR_BAR_VISIBLE.equals(command)) {
          final boolean b = (colorBarAvailable != null) ? colorBarAvailable.booleanValue() : false;
          item.setEnabled(b);
        } else if (MENUCMD_ALIGN_BARS.equals(command)) {
          final boolean b =
              (alignmentBarsAvailable != null) ? alignmentBarsAvailable.booleanValue() : false;
          item.setEnabled(b);
        } else if (MENUCMD_FIT_AXES_TO_DATA.equals(command)) {
          JMenu menu = (JMenu) item;
          Component[] cArray = menu.getMenuComponents();
          for (int jj = 0; jj < cArray.length; jj++) {
            if (cArray[jj] instanceof JMenuItem) {
              JMenuItem cItem = (JMenuItem) cArray[jj];
              String text = cItem.getText();
              if (MENUCMD_FIT_COLOR_BAR_TO_DATA.equals(text)) {
                final boolean b = (colorBarAvailable != null) ? colorBarAvailable : false;
                cItem.setEnabled(b);
              } else if (MENUCMD_FIT_ALL_AXES_TO_DATA_FOR_ALL_ANIMATION_FRAMES.equals(text)) {
                final boolean b = (animationAvailable != null) ? animationAvailable : false;
                cItem.setEnabled(b);
              }
            }
          }
        }
      }
    }

    // set selected/deselected the menu items
    for (int ii = 0; ii < components.length; ii++) {
      if (components[ii] instanceof SGCheckBoxMenuItem) {
        SGCheckBoxMenuItem item = (SGCheckBoxMenuItem) components[ii];
        String com = item.getActionCommand();
        if (com.equals(SGFigure.MENUCMD_RUBBER_BANDING)) {
          item.setSelected(SGFigure.mRubberBandFlag);
        } else if (com.equals(SGFigure.MENUCMD_SHOW_BOUNDING_BOX)) {
          item.setSelected(SGFigure.mBoundingBoxVisibleFlag);
        } else if (com.equals(MENUBARCMD_SNAP_TO_GRID)) {
          item.setSelected(SGFigure.isSnappingToGrid());
        } else if (com.equals(SGFigure.MENUCMD_LEGEND_VISIBLE)) {
          item.setSelected(legendVisible);
        } else if (com.equals(SGFigure.MENUCMD_COLOR_BAR_VISIBLE)) {
          item.setSelected(colorBarVisible);
        } else if (com.equals(SGFigure.MENUCMD_SCALE_VISIBLE)) {
          item.setSelected(axisScaleVisible);
        } else if (com.equals(SGFigure.MENUCMD_GRID_VISIBLE)) {
          item.setSelected(gridVisible);
        } else if (com.equals(SGFigure.MENUCMD_DATA_ANCHORED)) {
          item.setSelected(dataAnchored);
        }
      } else if (components[ii] instanceof JMenu) {
        JMenu menu = (JMenu) components[ii];
        if (MENUCMD_AXES_VISIBLE.equals(menu.getActionCommand())) {
          SGCheckBoxMenuItem item;
          item = (SGCheckBoxMenuItem) SGUtility.findMenuItem(menu, MENUCMD_VISIBLE_BOTTOM_AXIS);
          item.setSelected(horizontalAxis1Visible);
          item = (SGCheckBoxMenuItem) SGUtility.findMenuItem(menu, MENUCMD_VISIBLE_LEFT_AXIS);
          item.setSelected(verticalAxis1Visible);
          item = (SGCheckBoxMenuItem) SGUtility.findMenuItem(menu, MENUCMD_VISIBLE_TOP_AXIS);
          item.setSelected(horizontalAxis2Visible);
          item = (SGCheckBoxMenuItem) SGUtility.findMenuItem(menu, MENUCMD_VISIBLE_RIGHT_AXIS);
          item.setSelected(verticalAxis2Visible);
        }
      }
    }

    // shows the menu
    p.show(component, x, y);

    return true;
  }
}
