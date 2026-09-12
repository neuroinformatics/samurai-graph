package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import org.w3c.dom.Element;

/** Reads and writes the window attributes of a property document. */
final class SGDrawingWindowPropertyIO {

  private SGDrawingWindowPropertyIO() {}

  /**
   * @param el
   * @return
   */
  static boolean readAttributes(
      final SGDrawingWindow wnd,
      final Element el,
      final SGIProgressControl progress,
      final float min,
      final float max) {
    String str = null;
    Number num = null;
    Boolean b = null;
    Color cl = null;

    final float ratio = (max - min) / 9;

    // width
    progress.setProgressValue(min);
    str = el.getAttribute(SGIRootObjectConstants.KEY_PAPER_WIDTH);
    if (str.length() != 0) {
      StringBuilder uWidth = new StringBuilder();
      num = SGUtilityText.getNumber(str, uWidth);
      if (num == null) {
        return false;
      }
      final float width = num.floatValue();
      if (wnd.setPaperWidth(width, uWidth.toString()) == false) {
        return false;
      }
    }

    // height
    progress.setProgressValue(min + ratio * 1);
    str = el.getAttribute(SGIRootObjectConstants.KEY_PAPER_HEIGHT);
    if (str.length() != 0) {
      StringBuilder uHeight = new StringBuilder();
      num = SGUtilityText.getNumber(str, uHeight);
      if (num == null) {
        return false;
      }
      final float height = num.floatValue();
      if (wnd.setPaperHeight(height, uHeight.toString()) == false) {
        return false;
      }
    }

    // grid visible
    progress.setProgressValue(min + ratio * 2);
    str = el.getAttribute(SGIRootObjectConstants.KEY_GRID_VISIBLE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      final boolean gridVisible = b.booleanValue();
      if (wnd.setGridLineVisible(gridVisible) == false) {
        return false;
      }
    }

    // grid interval
    progress.setProgressValue(min + ratio * 3);
    str = el.getAttribute(SGIRootObjectConstants.KEY_GRID_INTERVAL);
    if (str.length() != 0) {
      StringBuilder uInterval = new StringBuilder();
      num = SGUtilityText.getNumber(str, uInterval);
      if (num == null) {
        return false;
      }
      final float interval = num.floatValue();
      if (wnd.setGridLineInterval(interval, uInterval.toString()) == false) {
        return false;
      }
    }

    // grid line width
    progress.setProgressValue(min + ratio * 4);
    str = el.getAttribute(SGIRootObjectConstants.KEY_GRID_LINE_WIDTH);
    if (str.length() != 0) {
      StringBuilder uGridLineWidth = new StringBuilder();
      num = SGUtilityText.getNumber(str, uGridLineWidth);
      if (num == null) {
        return false;
      }
      final float gridLineWidth = num.floatValue();
      if (wnd.setGridLineWidth(gridLineWidth, uGridLineWidth.toString()) == false) {
        return false;
      }
    }

    // background color
    progress.setProgressValue(min + ratio * 5);
    str = el.getAttribute(SGIRootObjectConstants.KEY_BACKGROUND_COLOR);
    if (str.length() != 0) {
      cl = SGUtilityText.parseColor(str);
      if (cl == null) {
        return false;
      }
      final Color bgColor = cl;
      if (wnd.setPaperColor(bgColor) == false) {
        return false;
      }
    }

    // grid line color
    progress.setProgressValue(min + ratio * 6);
    str = el.getAttribute(SGIRootObjectConstants.KEY_GRID_COLOR);
    if (str.length() != 0) {
      cl = SGUtilityText.parseColor(str);
      if (cl == null) {
        return false;
      }
      final Color gridColor = cl;
      if (wnd.setGridLineColor(gridColor) == false) {
        return false;
      }
    }

    // image location X
    progress.setProgressValue(min + ratio * 7);
    str = el.getAttribute(SGIRootObjectConstants.KEY_IMAGE_LOCATION_X);
    if (str.length() != 0) {
      StringBuilder uX = new StringBuilder();
      num = SGUtilityText.getNumber(str, uX);
      if (num == null) {
        return false;
      }
      final float x = num.floatValue();
      if (wnd.setImageLocationX(x, uX.toString()) == false) {
        return false;
      }
    }

    // image location Y
    progress.setProgressValue(min + ratio * 8);
    str = el.getAttribute(SGIRootObjectConstants.KEY_IMAGE_LOCATION_Y);
    if (str.length() != 0) {
      StringBuilder uY = new StringBuilder();
      num = SGUtilityText.getNumber(str, uY);
      if (num == null) {
        return false;
      }
      final float y = num.floatValue();
      if (wnd.setImageLocationY(y, uY.toString()) == false) {
        return false;
      }
    }

    // image scaling factor
    progress.setProgressValue(max);
    str = el.getAttribute(SGIRootObjectConstants.KEY_IMAGE_SCALE);
    if (str.length() != 0) {
      num = SGUtilityText.getDouble(str);
      if (num == null) {
        return false;
      }
      final float f = num.floatValue();
      if (wnd.setImageScalingFactor(f) == false) {
        return false;
      }
    }

    return true;
  }
}
