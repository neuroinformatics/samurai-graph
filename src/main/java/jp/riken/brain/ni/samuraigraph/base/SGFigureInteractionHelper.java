package jp.riken.brain.ni.samuraigraph.base;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class SGFigureInteractionHelper {

  private final SGFigure owner;

  public SGFigureInteractionHelper(final SGFigure owner) {
    this.owner = owner;
  }

  public boolean readProperty(final Element el) {
    String str = null;
    Number num = null;
    Boolean b = null;
    Color cl = null;

    // x
    str = el.getAttribute(SGFigure.KEY_FIGURE_X_IN_CLIENT);
    if (str.length() != 0) {
      StringBuilder ux = new StringBuilder();
      num = SGUtilityText.getNumber(str, ux);
      if (num == null) {
        return false;
      }
      final float x = num.floatValue();
      if (owner.setFigureX(x, ux.toString()) == false) {
        return false;
      }
    }

    // y
    str = el.getAttribute(SGFigure.KEY_FIGURE_Y_IN_CLIENT);
    if (str.length() != 0) {
      StringBuilder uy = new StringBuilder();
      num = SGUtilityText.getNumber(str, uy);
      if (num == null) {
        return false;
      }
      final float y = num.floatValue();
      if (owner.setFigureY(y, uy.toString()) == false) {
        return false;
      }
    }

    // width
    str = el.getAttribute(SGFigure.KEY_FIGURE_WIDTH);
    if (str.length() != 0) {
      StringBuilder uWidth = new StringBuilder();
      num = SGUtilityText.getNumber(str, uWidth);
      if (num == null) {
        return false;
      }
      final float width = num.floatValue();
      if (owner.setFigureWidth(width, uWidth.toString()) == false) {
        return false;
      }
    }

    // height
    str = el.getAttribute(SGFigure.KEY_FIGURE_HEIGHT);
    if (str.length() != 0) {
      StringBuilder uHeight = new StringBuilder();
      num = SGUtilityText.getNumber(str, uHeight);
      if (num == null) {
        return false;
      }
      final float height = num.floatValue();
      if (owner.setFigureHeight(height, uHeight.toString()) == false) {
        return false;
      }
    }

    // background color
    str = el.getAttribute(SGFigure.KEY_FIGURE_BACKGROUND_COLOR);
    if (str.length() != 0) {
      cl = SGUtilityText.parseColor(str);
      if (cl == null) {
        return false;
      }
      if (owner.setBackgroundColor(cl) == false) {
        return false;
      }
    }

    // transparent
    str = el.getAttribute(SGFigure.KEY_FIGURE_BACKGROUND_TRANSPARENT);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      final boolean transparent = b.booleanValue();
      if (owner.setTransparent(transparent) == false) {
        return false;
      }
    }

    // data anchor
    str = el.getAttribute(SGFigure.KEY_FIGURE_DATA_ANCHOR);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      final boolean anchor = b.booleanValue();
      owner.setDataAnchored(anchor);
    }

    return true;
  }

  void snap(final float interval, final int mouseLocation) {
    final float px = owner.mWnd.getPaperX();
    final float py = owner.mWnd.getPaperY();

    Rectangle2D dRect = owner.getDraggingRect();
    final float minX = (float) dRect.getMinX();
    final float maxX = (float) dRect.getMaxX();
    final float minY = (float) dRect.getMinY();
    final float maxY = (float) dRect.getMaxY();

    final float ox = minX;
    final float oy = maxY;

    final float ox2 = ox - px;
    final float oy2 = oy - py;

    final int nx = (int) (ox2 / interval);
    final int ny = (int) (oy2 / interval);

    final float rx = interval * nx;
    final float ry = interval * ny;

    int nxNew = nx;
    int nyNew = ny;
    if (ox2 - rx > interval / 2.0f) {
      nxNew++;
    }
    if (oy2 - ry > interval / 2.0f) {
      nyNew++;
    }

    // new origin
    final float oxNew = px + nxNew * interval;
    final float oyNew = py + nyNew * interval;

    // new bounds
    float xNew;
    float yNew;
    float wNew;
    float hNew;

    // x
    if (mouseLocation == SGIConstants.WEST
        || mouseLocation == SGIConstants.SOUTH_WEST
        || mouseLocation == SGIConstants.NORTH_WEST) {
      xNew = oxNew;
      wNew = maxX - xNew;
    } else if (mouseLocation == SGIConstants.EAST
        || mouseLocation == SGIConstants.SOUTH_EAST
        || mouseLocation == SGIConstants.NORTH_EAST) {
      xNew = minX;

      final int nMax = (int) ((maxX - px) / interval);
      final float rMax = interval * nMax;

      int nNew = nMax;
      if ((maxX - px) - rMax > interval / 2.0f) {
        nNew++;
      }

      final float maxNew = px + nNew * interval;
      wNew = maxNew - minX;
    } else if (mouseLocation == SGIConstants.OTHER) {
      xNew = oxNew;
      wNew = (float) dRect.getWidth();
    } else {
      xNew = minX;
      wNew = (float) dRect.getWidth();
    }

    // y
    if (mouseLocation == SGIConstants.SOUTH
        || mouseLocation == SGIConstants.SOUTH_WEST
        || mouseLocation == SGIConstants.SOUTH_EAST) {
      yNew = minY;
      hNew = oyNew - minY;
    } else if (mouseLocation == SGIConstants.NORTH
        || mouseLocation == SGIConstants.NORTH_EAST
        || mouseLocation == SGIConstants.NORTH_WEST) {
      final int nMin = (int) ((minY - py) / interval);
      final float rMin = interval * nMin;

      int nNew = nMin;
      if ((minY - py) - rMin > interval / 2.0f) {
        nNew++;
      }

      final float minNew = py + nNew * interval;
      hNew = maxY - minNew;

      yNew = minNew;
    } else if (mouseLocation == SGIConstants.OTHER) {
      hNew = (float) dRect.getHeight();
      yNew = oyNew - hNew;
    } else {
      yNew = minY;
      hNew = (float) dRect.getHeight();
    }

    // set new values to the rubber band rectangle
    owner.setRubberBandRect(xNew, yNew, wNew, hNew);
  }

  protected boolean onMouseDragged(final MouseEvent e) {

    // notify to the pressed SGIFigureElement object
    if (owner.mPressedElement != null) {
      List<SGISelectable> list = owner.mPressedElement.getFocusedObjectsList();

      if (owner.mPressedElement.onMouseDragged(e) == false) {
        return false;
      }

      if (list.size() != 0) {
        owner.mWnd.moveFocusedObjects(e);
      } else {
        owner.mWnd.clearAllFocusedObjectsInFigures();
      }

      owner.setCursorToWindow(owner.mPressedElement);
      return true;
    }

    // if this figure is not selected, return false
    if (!owner.isSelected()) {
      return false;
    }

    // mouse location
    final int ml = owner.mMouseLocation;

    // other points
    if (ml == SGIConstants.OTHER) {
      Rectangle2D rect = owner.getExtraRegionBounds();
      if (owner.mWnd.mMousePressLocation != null
          && !rect.contains(owner.mWnd.mMousePressLocation)) {
        return false;
      }

      // parallel displacement
      owner.mWnd.moveFocusedObjects(e);
      return true;
    }
    // record the temporary bounds
    owner.recordFigureRect();

    // create a temporary object
    Point posNew = new Point(owner.mPressedPoint);
    Rectangle2D rectNew = owner.getDraggingRect();

    // update the rectangle
    SGUtility.resizeRectangle(rectNew, posNew, e, ml);

    // when the size of rectangle becomes too small, return true
    if (rectNew.getWidth() < SGFigure.MIN_WIDTH || rectNew.getHeight() < SGFigure.MAX_WIDTH) {
      return true;
    }

    // set to an attribute
    owner.mPressedPoint.setLocation(posNew);

    // update the graph rectangle
    owner.setDraggingRect(rectNew);
    owner.snapToLines(ml);

    // if we do not draw the rubber band, change the rectangle of figure now
    if (SGFigure.mRubberBandFlag == false) {
      owner.setGraphRectOnDragging();
    }

    return false;
  }

  Cursor changeCursor() {

    Cursor cur = null;
    switch (owner.mMouseLocation) {
      case SGIConstants.WEST:
        {
          cur = new Cursor(Cursor.W_RESIZE_CURSOR);
          break;
        }
      case SGIConstants.EAST:
        {
          cur = new Cursor(Cursor.E_RESIZE_CURSOR);
          break;
        }
      case SGIConstants.NORTH:
        {
          cur = new Cursor(Cursor.N_RESIZE_CURSOR);
          break;
        }
      case SGIConstants.SOUTH:
        {
          cur = new Cursor(Cursor.S_RESIZE_CURSOR);
          break;
        }
      case SGIConstants.NORTH_WEST:
        {
          cur = new Cursor(Cursor.NW_RESIZE_CURSOR);
          break;
        }
      case SGIConstants.SOUTH_EAST:
        {
          cur = new Cursor(Cursor.SE_RESIZE_CURSOR);
          break;
        }
      case SGIConstants.NORTH_EAST:
        {
          cur = new Cursor(Cursor.NE_RESIZE_CURSOR);
          break;
        }
      case SGIConstants.SOUTH_WEST:
        {
          cur = new Cursor(Cursor.SW_RESIZE_CURSOR);
          break;
        }
      default:
        {
          cur = Cursor.getDefaultCursor();
        }
    }

    // set the cursor to the window
    // if set to the figure, the cursor does not change
    owner.setMouseCursor(cur);

    return cur;
  }

  protected boolean onKeyPressed(final KeyEvent e) {
    boolean effective = false;

    effective = owner.onFigureElementsKeyPressed(e);
    if (!owner.isSelected()) {
      return effective;
    }

    final int keycode = e.getKeyCode();
    int dx = 0;
    int dy = 0;
    switch (keycode) {
      case KeyEvent.VK_UP:
        dy = -1;
        break;
      case KeyEvent.VK_DOWN:
        dy = 1;
        break;
      case KeyEvent.VK_LEFT:
        dx = -1;
        break;
      case KeyEvent.VK_RIGHT:
        dx = 1;
        break;
    }
    if (dx != 0 || dy != 0) {
      Rectangle2D rect = owner.getGraphRect();
      float interval = owner.mMagnification;
      if (SGFigure.isSnappingToGrid()) interval *= owner.mWnd.getGridLineInterval();
      else
        interval *=
            (float) (SGIRootObjectConstants.GRID_INTERVAL_STEP_SIZE / SGIConstants.CM_POINT_RATIO);
      // horizontal
      if (dx != 0)
        rect.setRect(rect.getX() + interval * dx, rect.getY(), rect.getWidth(), rect.getHeight());
      // vertical
      if (dy != 0)
        rect.setRect(rect.getX(), rect.getY() + interval * dy, rect.getWidth(), rect.getHeight());

      // moves figure
      owner.setDraggingRect(rect);
      owner.snapToLines(SGIConstants.OTHER);
      owner.setGraphRectOnDragging();
      if (owner.isFigureMoved()) {
        owner.setChanged(true);
      }

      effective = true;
    }

    return effective;
  }

  public boolean calcMargin(final SGTuple2f topAndBottom, final SGTuple2f leftAndRight) {

    final SGIFigureElement[] array = owner.getIFigureElementArray();

    final SGTuple2f[] tbArray = new SGTuple2f[array.length];
    final SGTuple2f[] lrArray = new SGTuple2f[array.length];

    for (int ii = 0; ii < array.length; ii++) {
      tbArray[ii] = new SGTuple2f();
      lrArray[ii] = new SGTuple2f();
      final boolean flag = array[ii].getMarginAroundGraphRect(tbArray[ii], lrArray[ii]);
      if (!flag) {
        return false;
      }
    }

    float topMax = 0.0f;
    float bottomMax = 0.0f;
    float leftMax = 0.0f;
    float rightMax = 0.0f;
    for (int ii = 0; ii < array.length; ii++) {

      final float top = tbArray[ii].x;
      final float bottom = tbArray[ii].y;
      final float left = lrArray[ii].x;
      final float right = lrArray[ii].y;

      if (top > topMax) {
        topMax = top;
      }
      if (bottom > bottomMax) {
        bottomMax = bottom;
      }
      if (left > leftMax) {
        leftMax = left;
      }
      if (right > rightMax) {
        rightMax = right;
      }
    }

    final float mag = owner.mMagnification;
    topAndBottom.x = topMax + mag * SGFigure.MARGIN_TOP;
    topAndBottom.y = bottomMax + mag * SGFigure.MARGIN_BOTTOM;
    leftAndRight.x = leftMax + mag * SGFigure.MARGIN_LEFT;
    leftAndRight.y = rightMax + mag * SGFigure.MARGIN_RIGHT;

    return true;
  }

  public boolean drawbackFigure() {
    final Rectangle2D cRect = owner.mWnd.getClientRect();
    final Rectangle2D bbRect = owner.getBoundingBox();
    final Rectangle2D pRect = owner.mWnd.getPaperRect();

    final Rectangle2D rect = new Rectangle2D.Float();
    rect.setRect(bbRect);

    final int margin = SGFigure.DRAW_BACK_MARGIN;

    if (bbRect.getX() < cRect.getX()) {
      rect.setRect(margin, rect.getY() + margin, rect.getWidth(), rect.getHeight());
    }

    if (bbRect.getY() < cRect.getY()) {
      rect.setRect(rect.getX() + margin, margin, rect.getWidth(), rect.getHeight());
    }

    if (bbRect.getX() + bbRect.getWidth() > pRect.getX() + pRect.getWidth()) {
      rect.setRect(
          pRect.getX() + pRect.getWidth() - bbRect.getWidth() - margin,
          rect.getY() + margin,
          rect.getWidth(),
          rect.getHeight());
    }

    if (bbRect.getY() + bbRect.getHeight() > pRect.getY() + pRect.getHeight()) {
      rect.setRect(
          rect.getX() + margin,
          pRect.getY() + pRect.getHeight() - bbRect.getHeight() - margin,
          rect.getWidth(),
          rect.getHeight());
    }

    if (owner.setBoundingBox(rect) == false) {
      return false;
    }

    // snap to the lines
    owner.snapToLines(SGIConstants.OTHER);
    owner.setGraphRectOnDragging();

    return true;
  }

  boolean createElementLower(
      final Document document, final Element parent, final SGExportParameter params) {

    SGIFigureElement[] array = owner.getIFigureElementArray();
    for (int ii = 0; ii < array.length; ii++) {
      if (array[ii] instanceof SGIFigureElementGraph) {
        continue;
      }
      Element[] elements = array[ii].createElement(document, params);
      if (elements == null) {
        return false;
      }
      for (int jj = 0; jj < elements.length; jj++) {
        parent.appendChild(elements[jj]);
      }
    }

    // create Element objects for data
    List<Element> elList = new ArrayList<Element>();
    List<SGData> dataList = new ArrayList<SGData>();
    if (owner.getGraphElement().createElementOfData(document, elList, dataList, params) == false) {
      return false;
    }

    // add an attribute for the index in legend
    SGIFigureElementLegend lElement = owner.getLegendElement();
    for (int ii = 0; ii < elList.size(); ii++) {
      Element el = elList.get(ii);
      SGData data = dataList.get(ii);
      final int index = lElement.getIndex(data);
      if (index < 0) {
        return false;
      }
      el.setAttribute(SGIFigureElement.KEY_INDEX_IN_LEGEND, Integer.toString(index));
    }

    // append new Element objects to the parent
    for (int ii = 0; ii < elList.size(); ii++) {
      Element el = elList.get(ii);
      parent.appendChild(el);
    }

    return true;
  }

  protected boolean onMouseClicked(MouseEvent e) {

    // count
    final int count = e.getClickCount();

    // ask to SGIFigureElement objects
    SGIFigureElement el = owner.onFigureElementClicked(e);
    if (el != null) {
      //
      owner.mWnd.setFocusedFigure(owner, false);

      // after treatment
      owner.afterClicked(e);

      return true;
    }

    if (owner.getExtraRegionBounds().contains(e.getPoint())) {
      // when a point in the extra region of the figure is clicked

      // update the list of focused figures
      owner.updateFocusedFigureList(e);

      // after treatment
      owner.afterClicked(e);

      // show the pop-up menu
      if (SwingUtilities.isRightMouseButton(e) && count == 1) {
        owner.mWnd.showPopupMenuForSelectedFigures(owner.getComponent(), e.getX(), e.getY());
      }

      // show property dialog
      if (SwingUtilities.isLeftMouseButton(e) && count == 2) {
        owner.mWnd.showPropertyDialogForSelectedFigures();
      }

      return true;
    }

    return false;
  }

  protected boolean updateFocusedFigureList(final MouseEvent e) {
    final SGDrawingWindow wnd = owner.getWindow();
    final List<SGFigure> fList = wnd.getFocusedFigureList();

    // Neither CTRL key nor SHIFT key is pressed.
    final int mod = e.getModifiersEx();
    if (((mod & InputEvent.CTRL_DOWN_MASK) == 0) && ((mod & InputEvent.SHIFT_DOWN_MASK) == 0)) {
      // If the list already contains this object.
      if (fList.contains(owner)) {
        // There is nothing to do.
      } else {
        wnd.clearAllFocusedObjectsInFigures();
        wnd.setFocusedFigure(owner, true);
      }

    }
    // otherwise
    else {
      // If the list already contains this object.
      if (fList.contains(owner)) {
        final int loc = owner.mMouseLocation;

        // except the four corners
        if (loc != SGIConstants.NORTH_WEST
            && loc != SGIConstants.NORTH_EAST
            && loc != SGIConstants.SOUTH_EAST
            && loc != SGIConstants.SOUTH_WEST) {
          // if no figure element is pressed, remove this figure
          // from the list of the selected figure
          if (owner.mPressedElement == null) {
            wnd.setFocusedFigure(owner, !owner.isSelected());
          }
        }
      } else {
        wnd.setFocusedFigure(owner, !owner.isSelected());
      }
    }

    return true;
  }

  protected boolean onMouseMoved(final MouseEvent e) {
    final int x = e.getX();
    final int y = e.getY();

    if (owner.mWnd.isInsertFlagSelected() == false) {
      // when the toggle button is not selected

      SGIFigureElement[] array = owner.getIFigureElementArray();
      for (int ii = array.length - 1; ii >= 0; ii--) {
        array[ii].onMouseMoved(e);
      }

      // change the mouse cursor
      Cursor cur = owner.setMouseCursor(x, y);

      if (Cursor.getDefaultCursor().equals(cur) == false) {
        return true;
      }

    } else if (owner.mWnd.getTimingLineInsertionFlag()) {
      // timing line is to be inserted
      SGIFigureElementTimingLine el =
          (SGIFigureElementTimingLine) owner.getIFigureElement(SGIFigureElementTimingLine.class);
      if (el != null) {
        el.guideToAdd(x, y);
      }
    }

    if (owner.mMouseInExtraRegionFlag) {
      return true;
    } else {
      return false;
    }
  }

  protected boolean onMouseReleased(final MouseEvent e) {
    final int x = e.getX();
    final int y = e.getY();

    // set the mouse cursor
    if (owner.mWnd.isInsertFlagSelected() == false) {
      owner.setMouseCursor(x, y);
    }

    // set the rectangle of the graph area
    if (SGFigure.mRubberBandFlag && owner.mPressedElement == null) {
      owner.setGraphRectOnDragging();
    }

    if (owner.isFigureMoved()) {
      owner.setChanged(true);
    }

    // set invisible the rubber band
    if (SwingUtilities.isLeftMouseButton(e)) {
      SGFigure.mRubberBandVisibleFlag = false;
    }

    // call the "released" method of pressed SGIFigureElement object
    if (owner.mPressedElement != null) {
      owner.mPressedElement.onMouseReleased(e);
    }
    owner.mPressedElement = null;
    owner.mWnd.mDraggedDirection = null;

    return true;
  }
}
