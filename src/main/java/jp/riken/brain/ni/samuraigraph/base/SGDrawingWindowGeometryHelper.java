package jp.riken.brain.ni.samuraigraph.base;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import javax.swing.*;

class SGDrawingWindowGeometryHelper {

  private final SGDrawingWindow owner;

  public SGDrawingWindowGeometryHelper(final SGDrawingWindow owner) {
    this.owner = owner;
  }

  boolean updateClientRectOld() {

    //
    // if the client rectangle does not contain the bounding box,
    // fit the client rectangle to the bounding box.
    //
    // if the viewport rectangle contains the bounding box,
    // fit the the client rect to the viewport rectangle.
    //

    // horizontal

    owner.fitRect(owner.mClientRect, owner.getBoundingBox(), true);

    if (SGUtility.isRectContains(owner.getViewportBounds(), owner.getBoundingBox(), true)) {
      owner.fitRect(owner.mClientRect, owner.getViewportBounds(), true);
    }

    if (SGUtility.isRectContains(owner.getClientRect(), owner.getViewportBounds(), true) == false) {
      Rectangle2D cRect = owner.getClientRect();
      Rectangle2D vpRect = owner.getViewportBounds();

      final boolean b1 = SGUtility.isRectContains(cRect, vpRect.getX(), true);
      final boolean b2 = SGUtility.isRectContains(cRect, vpRect.getX() + vpRect.getWidth(), true);

      double diff = 0.0;
      if (!b1 && b2) {
        diff = vpRect.getX() - cRect.getX();
      } else if (b1 && !b2) {
        diff = (vpRect.getX() + vpRect.getWidth()) - (cRect.getX() + cRect.getWidth());
      } else if (!b1 && !b2) {
        if (cRect.getX() < vpRect.getX()) {
          diff = (vpRect.getX() + vpRect.getWidth()) - (cRect.getX() + cRect.getWidth());
        } else {
          diff = vpRect.getX() - cRect.getX();
        }
      }

      owner.setClientRect(
          (float) (cRect.getX() + diff),
          (float) cRect.getY(),
          (float) cRect.getWidth(),
          (float) cRect.getHeight());
    }

    // vertical

    owner.fitRect(owner.mClientRect, owner.getBoundingBox(), false);

    if (SGUtility.isRectContains(owner.getViewportBounds(), owner.getBoundingBox(), false)) {
      owner.fitRect(owner.mClientRect, owner.getViewportBounds(), false);
    }

    if (SGUtility.isRectContains(owner.getClientRect(), owner.getViewportBounds(), false)
        == false) {
      Rectangle2D cRect = owner.getClientRect();
      Rectangle2D vpRect = owner.getViewportBounds();

      final boolean b1 = SGUtility.isRectContains(cRect, vpRect.getY(), false);
      final boolean b2 = SGUtility.isRectContains(cRect, vpRect.getY() + vpRect.getHeight(), false);

      double diff = 0.0;
      if (!b1 && b2) {
        diff = vpRect.getY() - cRect.getY();
      } else if (b1 && !b2) {
        diff = (vpRect.getY() + vpRect.getHeight()) - (cRect.getY() + cRect.getHeight());
      } else if (!b1 && !b2) {
        if (cRect.getY() < vpRect.getY()) {
          diff = (vpRect.getY() + vpRect.getHeight()) - (cRect.getY() + cRect.getHeight());
        } else {
          diff = vpRect.getY() - cRect.getY();
        }
      }

      owner.setClientRect(
          (float) cRect.getX(),
          (float) (cRect.getY() + diff),
          (float) cRect.getWidth(),
          (float) cRect.getHeight());
    }

    final Rectangle2D bbRect = owner.getBoundingBox();
    final Rectangle2D vpRect = owner.getViewportBounds();
    final Rectangle2D cRect = owner.getClientRect();

    owner.mClientPanel.setScrollBarValue(cRect, vpRect);

    //
    owner.mClientPanel.setEnableScrollBars(vpRect, bbRect);

    if (SGUtility.isRectContains(vpRect, bbRect, true)) {
      owner.fitRect(owner.mClientRect, vpRect, true);
    }
    if (SGUtility.isRectContains(vpRect, bbRect, false)) {
      owner.fitRect(owner.mClientRect, vpRect, false);
    }

    //
    owner.mClientPanel.setScrollBarValue(cRect, vpRect);

    return true;
  }

  boolean onResized() {
    if (owner.getClientRect() == null) {
      return false;
    }

    // set the size of the components
    owner.setComponentBounds();

    // get and record the size of viewport
    final SGTuple2f size = owner.getViewportSize();

    // ratio of the viewport size
    final float ratioX = size.x / owner.mTemporaryViewportSize.x;
    final float ratioY = size.y / owner.mTemporaryViewportSize.y;

    //
    owner.updateClientRect();

    // resize the figures
    ArrayList<SGFigure> list = owner.getVisibleFigureList();
    for (int ii = 0; ii < list.size(); ii++) {
      SGFigure figure = list.get(ii);
      figure.recordFigureRect();
      figure.setViewBounds();
    }

    // when the figures are locked
    if (owner.isLocked()) {
      // resize the paper
      Rectangle2D pRect = owner.getPaperRect();
      final float pWidth = ratioX * (float) pRect.getWidth() / owner.mMagnification;
      final float pHeight = ratioY * (float) pRect.getHeight() / owner.mMagnification;
      owner.mClientPanel.setPaperSizeRoundingOff(pWidth, pHeight);

      // resize the figures
      for (int ii = 0; ii < list.size(); ii++) {
        SGFigure figure = list.get(ii);
        figure.recordFigureRect();
        figure.resize(ratioX, ratioY);
        figure.setChanged(true);
      }

      //
      owner.updateClientRect();

      if (owner.mTemporaryViewportSize.equals(size) == false) {
        owner.setChanged(true);
        owner.notifyToRoot();
      }
    }

    //
    owner.mTemporaryViewportSize.setValues(size);

    //
    owner.doAutoZoom();

    return true;
  }

  public boolean setBoundingBox() {
    // Rectangle pRect = owner.getPaperRect().getBounds();
    Rectangle2D cRect = owner.getClientRect();

    ArrayList<SGFigure> list = owner.getVisibleFigureList();
    if (list.size() != 0) {
      // update the temporary rectangles
      for (int ii = 0; ii < list.size(); ii++) {
        SGFigure figure = list.get(ii);
        figure.recordFigureRect();
      }
      owner.recordPaperRect();

      // align figures
      Rectangle2D bbRect = owner.getBoundingBoxOfFigures(list);
      for (int ii = 0; ii < list.size(); ii++) {
        SGFigure figure = list.get(ii);
        float x =
            owner.BOUNDING_BOX_MARGIN
                + (float) (cRect.getX() + figure.getGraphRectX() - bbRect.getX());
        float y =
            owner.BOUNDING_BOX_MARGIN
                + (float) (cRect.getY() + figure.getGraphRectY() - bbRect.getY());
        figure.setGraphRectLocationRoundingOut(x, y);
        if (figure.isFigureMoved()) {
          figure.setChanged(true);
        }
      }

      //
      owner.setFigureBoundingBox(0);

      //
      if (owner.isPaperBoundsChanged()) {
        owner.setChanged(true);
      }

      // notify to the root
      owner.notifyToRoot();

    } else {
      SGUtility.showMessageDialog(
          owner,
          "There is no figure.",
          "Failed to get the Bounding box.",
          JOptionPane.WARNING_MESSAGE);
    }

    return true;
  }

  public boolean setFigureBoundingBox(final int mode) {
    if (mode != 0 && mode != 1 && mode != 2) {
      return false;
    }

    ArrayList<Rectangle2D> rectList = new ArrayList<Rectangle2D>();
    ArrayList<SGFigure> fList = owner.getVisibleFigureList();
    for (int ii = 0; ii < fList.size(); ii++) {
      SGFigure figure = fList.get(ii);
      rectList.add(figure.getBoundingBox());
    }
    Rectangle2D bbRect = SGUtility.createUnion(rectList);
    if (bbRect == null) {
      return false;
    }

    Rectangle2D cRect = owner.getClientRect();
    Rectangle2D pRect = owner.getPaperRect();
    float width = (float) pRect.getWidth();
    float height = (float) pRect.getHeight();
    final float mag = owner.mMagnification;

    // width
    if (mode == 0 || mode == 1) {
      width =
          owner.BOUNDING_BOX_MARGIN
              + (float) (-cRect.getX() + bbRect.getX() + bbRect.getWidth()) / mag;
    }

    // height
    if (mode == 0 || mode == 2) {
      height =
          owner.BOUNDING_BOX_MARGIN
              + (float) (-cRect.getY() + bbRect.getY() + bbRect.getHeight()) / mag;
    }

    // set to the paper
    owner.mClientPanel.setPaperSizeRoundingOut(width, height);

    owner.updateClientRect();

    return true;
  }

  Point2D getLocationInPane(final int x, final int y) {

    int xx = x;
    int yy = y;

    // get size of boarder area
    final Insets insets = owner.getInsets();
    final int mTop = insets.top;
    // final int mBottom = insets.bottom;
    final int mLeft = insets.left;
    // final int mRight = insets.right;

    xx -= mLeft;
    yy -= mTop;

    // menu bar
    final JMenuBar menuBar = owner.getJMenuBar();
    final double menuHeight = menuBar.getHeight();
    yy -= (int) menuHeight;

    // tool bar
    yy -= owner.getToolBarHeight();

    // ruler
    final double rulerWidth = owner.mClientPanel.getRulerWidth();
    xx -= (int) rulerWidth;
    yy -= (int) rulerWidth;

    return new Point2D.Float(xx, yy);
  }
}
