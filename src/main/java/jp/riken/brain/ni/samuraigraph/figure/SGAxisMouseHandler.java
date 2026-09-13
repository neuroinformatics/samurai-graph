package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import jp.riken.brain.ni.samuraigraph.base.*;

class SGAxisMouseHandler {

  private final SGAxisElement owner;

  public SGAxisMouseHandler(final SGAxisElement owner) {
    this.owner = owner;
  }

  public boolean onMouseClicked(final MouseEvent e) {
    final int x = e.getX();
    final int y = e.getY();
    final int mod = e.getModifiersEx();
    final int cnt = e.getClickCount();
    final boolean ctrl = (mod & InputEvent.CTRL_DOWN_MASK) != 0;
    final boolean shift = (mod & InputEvent.SHIFT_DOWN_MASK) != 0;

    // check axis line even when axis is invisible
    for (int ii = 0; ii < owner.mAxisLines.length; ii++) {
      if (owner.mAxisLines[ii].contains(x, y)) {
        return owner.clicked(e);
      }
    }

    // return if axis is invisible
    if (!owner.isVisible()) {
      return false;
    }

    // title
    if (owner.isTitleVisible()) {
      if (owner.mTitle.contains(x, y)) {
        if (owner.mAxisElement.isEdited()) {
          owner.mAxisElement.closeTextField();
        }
        if (SwingUtilities.isLeftMouseButton(e) && cnt == 1) {
          if (owner.mTitle.isSelected() && !ctrl && !shift) {
            owner.mAxisElement.mEditingStringElement = owner.mTitle;
            Point point = owner.mAxisElement.getPressedPoint();
            final int tx = point.x - (int) owner.mTitle.getX();
            final int ty = point.y - (int) owner.mTitle.getY();
            owner.mAxisElement.showEditField(owner.mAxisElement.mTextField, owner.mTitle, tx, ty);
          } else {
            owner.mAxisElement.updateFocusedObjectsList(owner.mTitle, e);

            // avoids simultaneous selection
            if (owner.mTitle.isSelected()) {
              owner.setSelectedFlag(false);
            }
          }
          return true;
        }
        return owner.clicked(e);
      }
    }

    // numbers
    if (owner.isNumbersVisible()) {
      for (int ii = 0; ii < owner.mNumberList.size(); ii++) {
        SGAxisElement.ElementStringNumber el = owner.mNumberList.get(ii);
        if (el.contains(x, y)) {
          return owner.clicked(e);
        }
      }
    }

    // exponent
    if (owner.isExponentAvailable()) {
      if (owner.mExponentSymbol.contains(x, y)) {
        if (SwingUtilities.isLeftMouseButton(e) && cnt == 1) {
          owner.mAxisElement.updateFocusedObjectsList(owner.mExponentSymbol, e);

          // avoids simultaneous selection
          if (owner.mExponentSymbol.isSelected()) {
            owner.setSelectedFlag(false);
          }
          return true;
        }
        return owner.clicked(e);
      }
    }

    // tick marks
    if (owner.isTickMarkVisible()) {
      for (int ii = 0; ii < owner.mTickMarksList.size(); ii++) {
        SGAxisElement.ElementLineTickMark el = owner.mTickMarksList.get(ii);
        if (el.contains(x, y)) {
          return owner.clicked(e);
        }
      }
    }

    return false;
  }

  public boolean onMouseDragged(final MouseEvent e) {
    if (!owner.mVisible) {
      return false;
    }
    if (owner.mDraggingElement == null || owner.mTempRange == null) {
      return false;
    }

    SGAxisElement.AxisValueRange range = null;
    if (owner.mDraggingElement instanceof SGAxisElement.ElementStringNumber) {
      // ElementStringOfScale
      range = owner.dragScaleNumber(e);
    } else if (owner.mDraggingElement instanceof SGAxisElement.ElementLineTickMark) {
      // ElementLineOfScale
      range = owner.dragElementLineOfScale(e);
    }

    if (range != null) {
      // set the range to the axis
      final SGAxisValue minValue = range.min;
      final SGAxisValue maxValue = range.max;
      final int scaleType = owner.getScaleType();
      owner.mAxis.setScale(minValue, maxValue, scaleType);

      // create drawing elements
      if (owner.createDrawingElements() == false) {
        return false;
      }

      // notify to listeners
      if (SGFigureElementAxis.mNotifyChangeOnDraggingFlag) {
        owner.mAxisElement.notifyChange();
      }

      // notify to listeners
      owner.notifyAxisScaleChange();
    }

    return true;
  }
}
