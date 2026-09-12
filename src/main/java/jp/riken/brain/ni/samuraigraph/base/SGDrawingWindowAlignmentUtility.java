package jp.riken.brain.ni.samuraigraph.base;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/** Alignment algorithms for the figures in a window. */
final class SGDrawingWindowAlignmentUtility {

  private SGDrawingWindowAlignmentUtility() {}

  /**
   * @return
   */
  static SGFigure[][] getOrderedFigureArray(final SGDrawingWindow wnd) {

    // get the visible figure list
    ArrayList<SGFigure> list = wnd.getVisibleFigureList();

    // get the size of array
    final int n = list.size();
    if (n == 0) {
      return new SGFigure[0][0];
    }
    int size = 0;
    for (int ii = 1; ii <= 16; ii++) {
      final int sqSmall = (ii - 1) * (ii - 1);
      final int sqLarge = ii * ii;
      if ((sqSmall < n) && (n <= sqLarge)) {
        size = ii;
        break;
      }
    }
    int sx = size;
    int div = n / sx;
    int sy = n % sx == 0 ? div : div + 1;

    // create a figure array
    final SGFigure[][] figureArray = new SGFigure[sy][sx];

    //
    // in the order of figure-ID
    //

    boolean flag = true;
    for (int ny = 0; ny < sy; ny++) {
      for (int nx = 0; nx < sx; nx++) {
        final int index = ny * sx + nx;
        if (index >= list.size()) {
          flag = false;
          break;
        }
        figureArray[ny][nx] = list.get(index);
      }
      if (!flag) {
        break;
      }
    }

    return figureArray;
  }

  /** Returns a two dimensional array of figure list. */
  static ArrayList<ArrayList<ArrayList<SGFigure>>> getFigureListArray(final SGDrawingWindow wnd) {
    // get the visible figure list
    ArrayList<SGFigure> figureList = wnd.getVisibleFigureList();
    if (figureList.size() == 0) {
      return null;
    }

    // width of division
    float minWidth = Float.MAX_VALUE;
    float minHeight = Float.MAX_VALUE;
    for (int ii = 0; ii < figureList.size(); ii++) {
      SGFigure figure = figureList.get(ii);
      Rectangle2D rect = figure.getGraphRect();
      if (rect.getWidth() < minWidth) {
        minWidth = (float) rect.getWidth();
      }
      if (rect.getHeight() < minHeight) {
        minHeight = (float) rect.getHeight();
      }
    }
    final float dx = minWidth;
    final float dy = minHeight;

    Rectangle2D bbRect = wnd.getBoundingBoxOfFigures(figureList);

    final int numX = (int) ((float) bbRect.getWidth() / dx) + 1;
    final int numY = (int) ((float) bbRect.getHeight() / dy) + 1;

    // get a two-dimensional ArrayList of figures
    ArrayList<ArrayList<ArrayList<SGFigure>>> fListArray =
        new ArrayList<ArrayList<ArrayList<SGFigure>>>(numX);
    for (int ii = 0; ii < numX; ii++) {
      ArrayList<ArrayList<SGFigure>> row = new ArrayList<ArrayList<SGFigure>>(numY);
      for (int jj = 0; jj < numY; jj++) {
        row.add(new ArrayList<SGFigure>());
      }
      fListArray.add(row);
    }
    for (int ii = 0; ii < figureList.size(); ii++) {
      SGFigure figure = figureList.get(ii);
      Rectangle2D gRect = figure.getGraphRect();
      int nx = (int) ((gRect.getCenterX() - bbRect.getX()) / dx);
      int ny = (int) ((gRect.getCenterY() - bbRect.getY()) / dy);
      fListArray.get(nx).get(ny).add(figure);
    }

    ArrayList<Integer> numListX = new ArrayList<Integer>();
    for (int nx = 0; nx < numX; nx++) {
      boolean flag = false;
      for (int ny = 0; ny < numY; ny++) {
        if (fListArray.get(nx).get(ny).size() != 0) {
          flag = true;
          break;
        }
      }
      if (flag) {
        numListX.add(Integer.valueOf(nx));
      }
    }

    ArrayList<Integer> numListY = new ArrayList<Integer>();
    for (int ny = 0; ny < numY; ny++) {
      boolean flag = false;
      for (int nx = 0; nx < numX; nx++) {
        if (fListArray.get(nx).get(ny).size() != 0) {
          flag = true;
          break;
        }
      }
      if (flag) {
        numListY.add(Integer.valueOf(ny));
      }
    }

    final int sx = numListX.size();
    final int sy = numListY.size();

    ArrayList<ArrayList<ArrayList<SGFigure>>> figureListArray =
        new ArrayList<ArrayList<ArrayList<SGFigure>>>(sx);
    for (int ii = 0; ii < sx; ii++) {
      final int nx = numListX.get(ii);
      ArrayList<ArrayList<SGFigure>> row = new ArrayList<ArrayList<SGFigure>>(sy);
      for (int jj = 0; jj < sy; jj++) {
        final int ny = numListY.get(jj);
        row.add(fListArray.get(nx).get(ny));
      }
      figureListArray.add(row);
    }

    return figureListArray;
  }

  /**
   * @return
   */
  static boolean alignFiguresLeftAndBottom(
      final SGDrawingWindow wnd, ArrayList<ArrayList<ArrayList<SGFigure>>> figureListArray) {
    final int sx = figureListArray.size();
    final int sy = figureListArray.get(0).size();

    //
    final float[][] topArray = new float[sx][sy];
    final float[][] bottomArray = new float[sx][sy];
    final float[][] leftArray = new float[sx][sy];
    final float[][] rightArray = new float[sx][sy];
    for (int ii = 0; ii < sx; ii++) {
      for (int jj = 0; jj < sy; jj++) {
        ArrayList<SGFigure> list = figureListArray.get(ii).get(jj);
        float maxTop = 0.0f;
        float maxBottom = 0.0f;
        float maxLeft = 0.0f;
        float maxRight = 0.0f;
        for (int kk = 0; kk < list.size(); kk++) {
          SGFigure figure = list.get(kk);
          Rectangle2D rect = figure.getGraphRect();
          final float width = (float) rect.getWidth();
          final float height = (float) rect.getHeight();
          SGTuple2f tb = new SGTuple2f();
          SGTuple2f lr = new SGTuple2f();
          figure.calcMargin(tb, lr);
          final float top = tb.x;
          final float bottom = tb.y;
          final float left = lr.x;
          final float right = lr.y;
          if (top + height > maxTop) {
            maxTop = top + height;
          }
          if (bottom > maxBottom) {
            maxBottom = bottom;
          }
          if (left > maxLeft) {
            maxLeft = left;
          }
          if (right + width > maxRight) {
            maxRight = right + width;
          }
        }

        topArray[ii][jj] = maxTop;
        bottomArray[ii][jj] = maxBottom;
        leftArray[ii][jj] = maxLeft;
        rightArray[ii][jj] = maxRight;
      }
    }

    // get arrays of the width and the height
    final float[] widthArray = new float[sx];
    for (int nx = 0; nx < sx; nx++) {
      float wMax = 0.0f;
      for (int ny = 0; ny < sy; ny++) {
        float width = leftArray[nx][ny] + rightArray[nx][ny];
        if (width > wMax) {
          wMax = width;
        }
      }
      widthArray[nx] = wMax;
    }

    final float[] heightArray = new float[sy];
    for (int ny = 0; ny < sy; ny++) {
      float hMax = 0.0f;
      for (int nx = 0; nx < sx; nx++) {
        float height = topArray[nx][ny] + bottomArray[nx][ny];
        if (height > hMax) {
          hMax = height;
        }
      }
      heightArray[ny] = hMax;
    }

    // get arrays of the width and the height
    final float[] maxLeftArray = new float[sx];
    for (int nx = 0; nx < sx; nx++) {
      float wMax = 0.0f;
      for (int ny = 0; ny < sy; ny++) {
        float width = leftArray[nx][ny];
        if (width > wMax) {
          wMax = width;
        }
      }
      maxLeftArray[nx] = wMax;
    }

    final float[] maxRightArray = new float[sx];
    for (int nx = 0; nx < sx; nx++) {
      float wMax = 0.0f;
      for (int ny = 0; ny < sy; ny++) {
        float width = rightArray[nx][ny];
        if (width > wMax) {
          wMax = width;
        }
      }
      maxRightArray[nx] = wMax;
    }

    final float[] maxBottomArray = new float[sy];
    for (int ny = 0; ny < sy; ny++) {
      float hMax = 0.0f;
      for (int nx = 0; nx < sx; nx++) {
        float height = bottomArray[nx][ny];
        if (height > hMax) {
          hMax = height;
        }
      }
      maxBottomArray[ny] = hMax;
    }

    // create arrays of the coordinate of the left-bottom corner
    final float diff = wnd.getMagnification() * wnd.getGridLineInterval();
    Rectangle2D pRect = wnd.getPaperRect();
    final float px = (float) pRect.getX();
    final float py = (float) pRect.getY();
    float x = px;
    float y = py;
    final float[] originXArray = new float[sx];
    for (int nx = 0; nx < sx; nx++) {
      final float value = x + maxLeftArray[nx];
      final int index = (int) ((value - px) / diff) + 1;
      originXArray[nx] = px + index * diff;
      x = originXArray[nx] + maxRightArray[nx];
    }
    final float[] originYArray = new float[sy];
    for (int ny = 0; ny < sy; ny++) {
      final float value = y + heightArray[ny] - maxBottomArray[ny];
      final int index = (int) ((value - py) / diff) + 1;
      originYArray[ny] = py + index * diff;
      y = originYArray[ny] + maxBottomArray[ny];
    }

    // set the location of figures
    boolean flag = true;
    for (int ny = 0; ny < sy; ny++) {
      for (int nx = 0; nx < sx; nx++) {
        ArrayList<SGFigure> list = figureListArray.get(nx).get(ny);
        for (int ii = 0; ii < list.size(); ii++) {
          SGFigure figure = list.get(ii);
          if (figure == null) {
            flag = false;
            break;
          }

          if (figure.setGraphRectLocationByLeftBottom(originXArray[nx], originYArray[ny])
              == false) {
            return false;
          }
        }
        if (!flag) {
          break;
        }
      }
      if (!flag) {
        break;
      }
    }

    return true;
  }

  /**
   * @return
   */
  static boolean alignFiguresByGraphArea(final SGDrawingWindow wnd) {
    return alignFiguresByGraphAreaNew(wnd);
  }

  static boolean alignFiguresByGraphAreaNew(final SGDrawingWindow wnd) {

    // get the visible figure list
    ArrayList<SGFigure> figureList = wnd.getVisibleFigureList();
    if (figureList.size() == 0) {
      return true;
    }

    // get a two-dimensional array of the list of figures
    ArrayList<ArrayList<ArrayList<SGFigure>>> figureListArray = getFigureListArray(wnd);
    if (figureListArray == null) {
      return false;
    }
    if (figureListArray.size() == 0) {
      return false;
    }

    // align figures
    if (alignFiguresLeftAndBottom(wnd, figureListArray) == false) {
      return false;
    }

    // set bounding box
    if (wnd.setFigureBoundingBox(0) == false) {
      return false;
    }

    return true;
  }

  /**
   * Order the figures.
   *
   * @return
   */
  static boolean alignFiguresByBoundingBox(final SGDrawingWindow wnd) {
    boolean flag;

    final SGFigure[][] figureArray = getOrderedFigureArray(wnd);
    if (figureArray == null) {
      return false;
    }
    if (figureArray.length == 0) {
      return true;
    }
    final int sy = figureArray.length;
    final int sx = figureArray[0].length;

    // create an array of the bounding box of the figures
    Rectangle2D[][] rectArray = new Rectangle2D[sy][sx];
    flag = true;
    for (int ny = 0; ny < sy; ny++) {
      for (int nx = 0; nx < sx; nx++) {
        if (figureArray[ny][nx] == null) {
          flag = false;
          break;
        }
        rectArray[ny][nx] = figureArray[ny][nx].getBoundingBox();
      }
      if (!flag) {
        break;
      }
    }

    // get arrays of the width and the height
    final float[] widthArray = new float[sx];
    for (int nx = 0; nx < sx; nx++) {
      float wMax = 0.0f;
      for (int ny = 0; ny < sy; ny++) {
        Rectangle2D rect = rectArray[ny][nx];
        if (rect == null) {
          break;
        }
        float width = (float) rectArray[ny][nx].getWidth();
        if (width > wMax) {
          wMax = width;
        }
      }
      widthArray[nx] = wMax;
    }

    final float[] heightArray = new float[sy];
    for (int ny = 0; ny < sy; ny++) {
      float hMax = 0.0f;
      for (int nx = 0; nx < sx; nx++) {
        Rectangle2D rect = rectArray[ny][nx];
        if (rect == null) {
          break;
        }
        float height = (float) rectArray[ny][nx].getHeight();
        if (height > hMax) {
          hMax = height;
        }
      }
      heightArray[ny] = hMax;
    }

    // create arrays of the coordinate of the centers
    Rectangle2D cRect = wnd.getClientRect();

    final float[] centerXArray = new float[sx];
    float cx = (float) cRect.getX();
    for (int nx = 0; nx < sx; nx++) {
      centerXArray[nx] = cx + widthArray[nx] / 2.0f;
      cx += widthArray[nx];
    }

    final float[] centerYArray = new float[sy];
    float cy = (float) cRect.getY();
    for (int ny = 0; ny < sy; ny++) {
      centerYArray[ny] = cy + heightArray[ny] / 2.0f;
      cy += heightArray[ny];
    }

    // set the location of figures
    flag = true;
    for (int ny = 0; ny < sy; ny++) {
      for (int nx = 0; nx < sx; nx++) {
        // final int index = ny*sx + nx;
        SGFigure figure = figureArray[ny][nx];
        if (figure == null) {
          flag = false;
          break;
        }
        if (figure.setCenter(centerXArray[nx], centerYArray[ny]) == false) {
          return false;
        }
      }
      if (!flag) {
        break;
      }
    }

    // enlarge the size of paper
    int mode = -1;
    float wTotal = 0.0f;
    float hTotal = 0.0f;
    for (int ii = 0; ii < widthArray.length; ii++) {
      wTotal += widthArray[ii];
    }
    for (int ii = 0; ii < heightArray.length; ii++) {
      hTotal += heightArray[ii];
    }
    Rectangle2D pRect = wnd.getPaperRect();
    final boolean bw = (pRect.getWidth() < wTotal);
    final boolean bh = (pRect.getHeight() < hTotal);

    if (bw && bh) {
      mode = 0;
    } else if (bw) {
      mode = 1;
    } else if (bh) {
      mode = 2;
    }

    if (mode != -1) {
      if (wnd.setFigureBoundingBox(mode) == false) {
        return false;
      }
    }

    return true;
  }
}
