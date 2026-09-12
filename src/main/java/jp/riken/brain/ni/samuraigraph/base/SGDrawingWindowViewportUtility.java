package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import javax.swing.JMenuBar;
import javax.swing.SwingConstants;

/** Geometry utilities of the drawing window. */
final class SGDrawingWindowViewportUtility {

  private SGDrawingWindowViewportUtility() {}

  public static int getTopWidth(final SGDrawingWindow wnd) {
    final Insets insets = wnd.getInsets();
    final int iTop = insets.top;

    // width of menu bar
    final JMenuBar menuBar = wnd.getJMenuBar();
    final int menuBarWidth = menuBar.getHeight();

    // width of tool bar
    int tHeight = 0;
    if (wnd.getToolBar().getOrientation() == SwingConstants.HORIZONTAL) {
      tHeight = wnd.getToolBarHeight();
    }

    final int width = iTop + menuBarWidth + tHeight;

    return width;
  }

  public static int getBottomWidth(final SGDrawingWindow wnd) {
    final Insets insets = wnd.getInsets();
    final int iBottom = insets.bottom;

    return iBottom;
  }

  public static int getLeftWidth(final SGDrawingWindow wnd) {
    final Insets insets = wnd.getInsets();
    final int iLeft = insets.left;

    int tWidth = 0;
    if (wnd.getToolBar().getOrientation() == SwingConstants.VERTICAL) {
      tWidth = wnd.getToolBarWidth();
    }

    return iLeft + tWidth;
  }

  public static int getRightWidth(final SGDrawingWindow wnd) {
    final Insets insets = wnd.getInsets();
    final int iRight = insets.right;
    return iRight;
  }

  public static SGTuple2f getViewportSize(final SGDrawingWindow wnd) {
    final SGTuple2f size = wnd.getPaneSize();
    final int rw = wnd.getClientPanel().getRulerWidth();
    size.x -= rw;
    size.y -= rw;
    return size;
  }

  public static SGTuple2f getPaneOrigin(final SGDrawingWindow wnd) {
    Rectangle2D rect = wnd.getPaneBounds();
    final SGTuple2f origin = new SGTuple2f((float) rect.getX(), (float) rect.getY());
    return origin;
  }

  public static SGTuple2f getPaneSize(final SGDrawingWindow wnd) {
    Rectangle2D rect = wnd.getPaneBounds();
    final SGTuple2f size = new SGTuple2f((float) rect.getWidth(), (float) rect.getHeight());
    return size;
  }

  public static Rectangle2D getPaneBounds(final SGDrawingWindow wnd) {

    // get size of boarder area
    final Insets insets = wnd.getInsets();
    final int iTop = insets.top;
    final int iBottom = insets.bottom;
    final int iLeft = insets.left;
    final int iRight = insets.right;

    // width of menu bar
    final JMenuBar menuBar = wnd.getJMenuBar();
    final int menuBarWidth = menuBar.getHeight();

    // width of tool bar
    final int toolBarWidth = wnd.getToolBarHeight();

    // StatusBar
    final int sbH = wnd.getStatusBar().getHeight();

    // set size
    final float sizeX = wnd.getWidth() - (iLeft + iRight);
    final float sizeY = wnd.getHeight() - (iTop + iBottom + menuBarWidth + toolBarWidth + sbH);

    final float x = iLeft;
    final float y = iTop + menuBarWidth + toolBarWidth;

    Rectangle2D rect = new Rectangle2D.Float(x, y, sizeX, sizeY);

    return rect;
  }

  public static boolean setPaperOrigin(final SGDrawingWindow wnd, final float x, final float y) {
    final Rectangle2D cRect = wnd.getClientRect();
    final float mag = wnd.getMagnification();
    final float xx = (x - (float) cRect.getX()) / mag;
    final float yy = (y - (float) cRect.getY()) / mag;
    wnd.getPaperOriginTuple().setValues(xx, yy);
    return true;
  }

  public static SGTuple2f getPaperSize(final SGDrawingWindow wnd) {
    return new SGTuple2f(
        wnd.getClientPanel().getPaperWidth(), wnd.getClientPanel().getPaperHeight());
  }

  public static float getPaperX(final SGDrawingWindow wnd) {
    final Rectangle2D cRect = wnd.getClientRect();
    return (float) cRect.getX() + wnd.getMagnification() * wnd.getPaperOriginTuple().x;
  }

  public static float getPaperY(final SGDrawingWindow wnd) {
    final Rectangle2D cRect = wnd.getClientRect();
    return (float) cRect.getY() + wnd.getMagnification() * wnd.getPaperOriginTuple().y;
  }

  public static Rectangle2D getPaperRect(final SGDrawingWindow wnd) {
    final float mag = wnd.getMagnification();
    Rectangle2D rect =
        new Rectangle2D.Float(
            wnd.getPaperX(),
            wnd.getPaperY(),
            mag * wnd.getClientPanel().getPaperWidth(),
            mag * wnd.getClientPanel().getPaperHeight());
    return rect;
  }

  public static Rectangle2D getBoundingBox(final SGDrawingWindow wnd) {
    Rectangle2D rect = new Rectangle2D.Float();

    final float margin = wnd.getMagnification() * SGDrawingWindow.PAPER_MARGIN;
    Rectangle2D pRect = wnd.getPaperRect();
    rect.setRect(pRect.getX(), pRect.getY(), pRect.getWidth() + margin, pRect.getHeight() + margin);

    return rect;
  }

  public static boolean setViewportSize(
      final SGDrawingWindow wnd, final float width, final float height) {

    wnd.getTemporaryViewportSize().setValues(width, height);

    // get size of boarder area
    final Insets insets = wnd.getInsets();
    final int iTop = insets.top;
    final int iBottom = insets.bottom;
    final int iLeft = insets.left;
    final int iRight = insets.right;

    // width of menu bar
    final JMenuBar menuBar = wnd.getJMenuBar();
    final int menuBarWidth = menuBar.getHeight();

    // width of tool bar
    final int toolBarWidth = wnd.getToolBarHeight();

    // set size
    final int rw = wnd.getClientPanel().getRulerWidth();
    final float sizeX = width + iLeft + iRight + rw;
    final float sizeY =
        height + iTop + iBottom + menuBarWidth + toolBarWidth + rw + wnd.getStatusBar().getHeight();
    wnd.setSize((int) sizeX, (int) sizeY);

    return true;
  }

  public static Rectangle2D getViewportBounds(final SGDrawingWindow wnd) {
    final SGTuple2f dim = wnd.getViewportSize();
    final float w = dim.x;
    final float h = dim.y;
    Rectangle2D rect = new Rectangle2D.Float(0.0f, 0.0f, w, h);
    return rect;
  }

  public static Rectangle2D getViewportBoundsInLayeredPane(final SGDrawingWindow wnd) {
    final SGTuple2f dim = wnd.getViewportSize();
    final float w = dim.x;
    final float h = dim.y;
    final int rw = wnd.getClientPanel().getRulerWidth();
    Rectangle2D rect = new Rectangle2D.Float(rw, rw, w, h);
    return rect;
  }

  public static Rectangle2D getViewportBoundsInComponent(final SGDrawingWindow wnd) {
    final int top = wnd.getTopWidth();
    final int left = wnd.getLeftWidth();
    final int rw = wnd.getClientPanel().getRulerWidth();
    final SGTuple2f dim = wnd.getViewportSize();
    final float w = dim.x + rw;
    final float h = dim.y + rw;
    Rectangle2D rect = new Rectangle2D.Float(left, top, w, h);
    return rect;
  }
}
