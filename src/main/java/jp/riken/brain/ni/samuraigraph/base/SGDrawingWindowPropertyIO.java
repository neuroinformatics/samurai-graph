package jp.riken.brain.ni.samuraigraph.base;

import static jp.riken.brain.ni.samuraigraph.base.SGIConstants.LINE_WIDTH_UNIT;
import static jp.riken.brain.ni.samuraigraph.base.SGIRootObjectConstants.*;

import java.awt.Color;
import org.w3c.dom.Element;

/** Reads and writes the window attributes of a property document. */
final class SGDrawingWindowPropertyIO {

  private SGDrawingWindowPropertyIO() {}

  private enum Kind {
    LENGTH,
    BOOLEAN,
    COLOR,
    SCALE
  }

  /** Descriptor of a single window property. */
  private static final class Attribute {
    final Kind kind;
    final String key;
    final String comKey;
    final String unit;

    Attribute(final Kind kind, final String key, final String comKey, final String unit) {
      this.kind = kind;
      this.key = key;
      this.comKey = comKey;
      this.unit = unit;
    }
  }

  private static final Attribute[] ATTRIBUTES = {
    new Attribute(Kind.LENGTH, KEY_PAPER_WIDTH, COM_PAPER_WIDTH, PAPER_SIZE_UNIT),
    new Attribute(Kind.LENGTH, KEY_PAPER_HEIGHT, COM_PAPER_HEIGHT, PAPER_SIZE_UNIT),
    new Attribute(Kind.COLOR, KEY_BACKGROUND_COLOR, COM_WINDOW_BACKGROUND_COLOR, null),
    new Attribute(Kind.BOOLEAN, KEY_GRID_VISIBLE, COM_WINDOW_GRID_VISIBLE, null),
    new Attribute(Kind.LENGTH, KEY_GRID_INTERVAL, COM_WINDOW_GRID_INTERVAL, GRID_INTERVAL_UNIT),
    new Attribute(Kind.LENGTH, KEY_GRID_LINE_WIDTH, COM_WINDOW_GRID_LINE_WIDTH, LINE_WIDTH_UNIT),
    new Attribute(Kind.COLOR, KEY_GRID_COLOR, COM_WINDOW_GRID_COLOR, null),
    new Attribute(Kind.LENGTH, KEY_IMAGE_LOCATION_X, COM_IMAGE_LOCATION_X, IMAGE_LOCATION_UNIT),
    new Attribute(Kind.LENGTH, KEY_IMAGE_LOCATION_Y, COM_IMAGE_LOCATION_Y, IMAGE_LOCATION_UNIT),
    new Attribute(Kind.SCALE, KEY_IMAGE_SCALE, COM_IMAGE_SCALING_FACTOR, null),
  };

  /**
   * Applies a window attribute from a textual value.
   *
   * @param wnd the drawing window
   * @param key the file attribute key, or the command key
   * @param value the textual value
   * @return {@link Boolean#TRUE} if the value was applied; {@link Boolean#FALSE} if the value is
   *     invalid; {@code null} if the key is not a window attribute
   */
  static Boolean applyStringValue(final SGDrawingWindow wnd, final String key, final String value) {
    for (int ii = 0; ii < ATTRIBUTES.length; ii++) {
      final Attribute attr = ATTRIBUTES[ii];
      if (attr.key.equalsIgnoreCase(key) || attr.comKey.equalsIgnoreCase(key)) {
        return Boolean.valueOf(applyAttribute(wnd, attr, value));
      }
    }
    return null;
  }

  /**
   * Returns the canonical command key for a given window attribute key.
   *
   * @param key the file attribute key, or the command key
   * @return the canonical command key, or {@code null} if the key is not a window attribute
   */
  static String getComKey(final String key) {
    for (int ii = 0; ii < ATTRIBUTES.length; ii++) {
      final Attribute attr = ATTRIBUTES[ii];
      if (attr.key.equalsIgnoreCase(key) || attr.comKey.equalsIgnoreCase(key)) {
        return attr.comKey;
      }
    }
    return null;
  }

  /**
   * @param wnd the drawing window
   * @param el the root element of a property document
   * @param progress the progress control
   * @param min the progress value at the first attribute
   * @param max the progress value at the last attribute
   * @return true if succeeded
   */
  static boolean readAttributes(
      final SGDrawingWindow wnd,
      final Element el,
      final SGIProgressControl progress,
      final float min,
      final float max) {
    final float ratio = (max - min) / (ATTRIBUTES.length - 1);
    for (int ii = 0; ii < ATTRIBUTES.length; ii++) {
      final Attribute attr = ATTRIBUTES[ii];
      progress.setProgressValue(ii == ATTRIBUTES.length - 1 ? max : min + ratio * ii);
      final String str = el.getAttribute(attr.key);
      if (str.length() == 0) {
        continue;
      }
      if (applyAttribute(wnd, attr, str) == false) {
        return false;
      }
    }
    return true;
  }

  private static boolean applyAttribute(
      final SGDrawingWindow wnd, final Attribute attr, final String value) {
    switch (attr.kind) {
      case LENGTH:
        {
          final StringBuilder unit = new StringBuilder();
          final Number num = SGUtilityText.getNumber(value, unit);
          if (num == null) {
            return false;
          }
          switch (attr.key) {
            case KEY_PAPER_WIDTH:
              return wnd.setPaperWidth(num.floatValue(), unit.toString());
            case KEY_PAPER_HEIGHT:
              return wnd.setPaperHeight(num.floatValue(), unit.toString());
            case KEY_GRID_INTERVAL:
              return wnd.setGridLineInterval(num.floatValue(), unit.toString());
            case KEY_GRID_LINE_WIDTH:
              return wnd.setGridLineWidth(num.floatValue(), unit.toString());
            case KEY_IMAGE_LOCATION_X:
              return wnd.setImageLocationX(num.floatValue(), unit.toString());
            case KEY_IMAGE_LOCATION_Y:
              return wnd.setImageLocationY(num.floatValue(), unit.toString());
            default:
              return false;
          }
        }
      case BOOLEAN:
        {
          final Boolean b = SGUtilityText.getBoolean(value);
          if (b == null) {
            return false;
          }
          return wnd.setGridLineVisible(b.booleanValue());
        }
      case COLOR:
        {
          Color cl = SGUtilityText.getColor(value);
          if (cl == null) {
            cl = SGUtilityText.parseColor(value);
          }
          if (cl == null) {
            return false;
          }
          if (KEY_BACKGROUND_COLOR.equals(attr.key)) {
            return wnd.setPaperColor(cl);
          }
          return wnd.setGridLineColor(cl);
        }
      case SCALE:
        {
          final Number num = SGUtilityText.getFloat(value);
          if (num == null) {
            return false;
          }
          if (SGUtility.isValidPropertyValue(num.floatValue()) == false) {
            return false;
          }
          return wnd.setImageScalingFactor(num.floatValue());
        }
      default:
        return false;
    }
  }

  /** Collects the window attribute values in a property file format. */
  private static void addProperties(
      final SGDrawingWindow wnd,
      final SGPropertyMap map,
      final String widthKey,
      final String heightKey,
      final String bgColorKey,
      final String gridVisibleKey,
      final String gridIntervalKey,
      final String gridLineWidthKey,
      final String gridColorKey,
      final String imageXKey,
      final String imageYKey,
      final String imageScaleKey) {

    // paper
    SGPropertyUtility.addProperty(
        map,
        widthKey,
        wnd.getExportLengthValue(wnd.getPaperWidth(PAPER_SIZE_UNIT)),
        PAPER_SIZE_UNIT);
    SGPropertyUtility.addProperty(
        map,
        heightKey,
        wnd.getExportLengthValue(wnd.getPaperHeight(PAPER_SIZE_UNIT)),
        PAPER_SIZE_UNIT);
    SGPropertyUtility.addProperty(map, bgColorKey, wnd.getPaperColor());

    // grid
    SGPropertyUtility.addProperty(map, gridVisibleKey, wnd.isGridLineVisible());
    SGPropertyUtility.addProperty(
        map,
        gridIntervalKey,
        wnd.getExportLengthValue(wnd.getGridLineInterval(GRID_INTERVAL_UNIT)),
        GRID_INTERVAL_UNIT);
    SGPropertyUtility.addProperty(
        map,
        gridLineWidthKey,
        SGUtility.getExportLineWidth(wnd.getGridLineWidth(LINE_WIDTH_UNIT)),
        LINE_WIDTH_UNIT);
    SGPropertyUtility.addProperty(map, gridColorKey, wnd.getGridLineColor());

    // image
    SGPropertyUtility.addProperty(
        map,
        imageXKey,
        wnd.getExportLengthValue(wnd.getImageLocationX(IMAGE_LOCATION_UNIT)),
        IMAGE_LOCATION_UNIT);
    SGPropertyUtility.addProperty(
        map,
        imageYKey,
        wnd.getExportLengthValue(wnd.getImageLocationY(IMAGE_LOCATION_UNIT)),
        IMAGE_LOCATION_UNIT);
    SGPropertyUtility.addProperty(
        map,
        imageScaleKey,
        SGUtility.getExportValue(
            wnd.getImageScalingFactor(), SGIRootObjectConstants.IMAGE_SCALING_ORDER));
  }

  /** Creates the map of properties with the file attribute keys. */
  static SGPropertyMap getPropertyFileMap(final SGDrawingWindow wnd) {
    final SGPropertyMap map = new SGPropertyMap();
    addProperties(
        wnd,
        map,
        KEY_PAPER_WIDTH,
        KEY_PAPER_HEIGHT,
        KEY_BACKGROUND_COLOR,
        KEY_GRID_VISIBLE,
        KEY_GRID_INTERVAL,
        KEY_GRID_LINE_WIDTH,
        KEY_GRID_COLOR,
        KEY_IMAGE_LOCATION_X,
        KEY_IMAGE_LOCATION_Y,
        KEY_IMAGE_SCALE);
    return map;
  }

  /** Creates the map of properties with the command keys. */
  static SGPropertyMap getCommandPropertyMap(
      final SGDrawingWindow wnd, final SGExportParameter params) {
    final SGPropertyMap map = new SGPropertyMap();
    addProperties(
        wnd,
        map,
        COM_PAPER_WIDTH,
        COM_PAPER_HEIGHT,
        COM_WINDOW_BACKGROUND_COLOR,
        COM_WINDOW_GRID_VISIBLE,
        COM_WINDOW_GRID_INTERVAL,
        COM_WINDOW_GRID_LINE_WIDTH,
        COM_WINDOW_GRID_COLOR,
        COM_IMAGE_LOCATION_X,
        COM_IMAGE_LOCATION_Y,
        COM_IMAGE_SCALING_FACTOR);
    final String path = wnd.getImageFilePath();
    if (path != null) {
      SGPropertyUtility.addQuotedStringProperty(map, COM_IMAGE_FILE_PATH, path);
    }
    return map;
  }

  /** Collects the window attributes into a window properties object. */
  static SGDrawingWindow.WindowProperties collectWindowProperties(final SGDrawingWindow wnd) {
    final SGDrawingWindow.WindowProperties p = new SGDrawingWindow.WindowProperties();
    final SGClientPanel panel = wnd.getClientPanel();

    p.setPaperWidth(panel.getPaperWidth());
    p.setPaperHeight(panel.getPaperHeight());
    p.setBackGroundColor(panel.getPaperColor());
    p.setGridColor(panel.getGridLineColor());
    p.setGridVisible(panel.isGridLineVisible());
    p.setGridInterval(panel.getGridLineInterval());
    p.setGridLineWidth(panel.getGridLineWidth());
    p.setVisibleFigureList(wnd.getVisibleFigureList());
    p.setImageLocationX(wnd.getImageLocationX());
    p.setImageLocationY(wnd.getImageLocationY());
    p.setImageScalingFactor(wnd.getImageScalingFactor());
    p.setImage(wnd.getImage());

    return p;
  }

  /**
   * Applies the window attributes from a window properties object.
   *
   * @param wnd the drawing window
   * @param p the window properties
   * @return true if succeeded
   */
  static boolean applyWindowProperties(
      final SGDrawingWindow wnd, final SGDrawingWindow.WindowProperties p) {

    final Float w = p.getPaperWidth();
    final Float h = p.getPaperHeight();
    if (w == null || h == null) {
      return false;
    }
    final SGClientPanel panel = wnd.getClientPanel();
    panel.setPaperSize(w.floatValue(), h.floatValue());

    final Color bgColor = p.getBackgroundColor();
    if (bgColor == null) {
      return false;
    }
    panel.setPaperColor(bgColor);

    final Color gridColor = p.getGridColor();
    if (gridColor == null) {
      return false;
    }
    panel.setGridLineColor(gridColor);

    final Boolean gridVisible = p.getGridVisible();
    if (gridVisible == null) {
      return false;
    }
    panel.setGridLineVisible(gridVisible.booleanValue());

    final Float gridInterval = p.getGridInterval();
    if (gridInterval == null) {
      return false;
    }
    panel.setGridLineInterval(gridInterval.floatValue());

    final Float gridLineWidth = p.getGridLineWidth();
    if (gridLineWidth == null) {
      return false;
    }
    panel.setGridLineWidth(gridLineWidth.floatValue());

    final Float imageLocationX = p.getImageLocationX();
    if (imageLocationX == null) {
      return false;
    }
    panel.setImageLocationX(imageLocationX.floatValue());

    final Float imageLocationY = p.getImageLocationY();
    if (imageLocationY == null) {
      return false;
    }
    panel.setImageLocationY(imageLocationY.floatValue());

    final Float imageScalingFactor = p.getImageScalingFactor();
    if (imageScalingFactor == null) {
      return false;
    }
    panel.setImageScalingFactor(imageScalingFactor.floatValue());

    return true;
  }
}
