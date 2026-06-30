package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

public final class SGIScaleConstants {

  //
  // Code of locations
  //

  public static final int BODY = 0;

  public static final int HORIZONTAL_MIDDLE = 1;

  public static final int VERTICAL_MIDDLE = 2;

  public static final int HORIZONTAL_END = 3;

  public static final int VERTICAL_END = 4;

  public static final int JOINT = 5;

  public static final int ON_HORIZONTAL_STRING = 6;

  public static final int ON_VERTICAL_STRING = 7;

  //
  // Mode for the change of axis scale
  //

  public enum AXIS_LENGTH_MODE {
    LENGTH_FIXED,
    VALUE_FIXED,
    ADAPTIVE
  };

  public static final String AXIS_LENGTH_MODE_FIXED = "Length Fixed";

  public static final String AXIS_LENGTH_MODE_VALUE_FIXED = "Value Fixed";

  public static final String AXIS_LENGTH_MODE_ADAPTIVE = "Adaptive";

  //
  // Space between the line and the symbol
  //

  public static final String SCALE_SPACE_UNIT = SGILineAndStringConstants.SPACE_UNIT;

  public static final double SCALE_SPACE_MIN = SGILineAndStringConstants.SPACE_MIN;

  public static final double SCALE_SPACE_MAX = SGILineAndStringConstants.SPACE_MAX;

  public static final double SCALE_SPACE_STEP = SGILineAndStringConstants.SPACE_STEP;

  public static final int SCALE_SPACE_FRAC_DIFIT_MIN =
      SGILineAndStringConstants.SPACE_FRAC_DIFIT_MIN;

  public static final int SCALE_SPACE_FRAC_DIFIT_MAX =
      SGILineAndStringConstants.SPACE_FRAC_DIFIT_MAX;

  public static final int SCALE_SPACE_MINIMAL_ORDER = SGILineAndStringConstants.SPACE_MINIMAL_ORDER;

  //
  // Keys
  //

  public static final String TAG_NAME_SCALE = "Scale";

  public static final String KEY_SCALE_X_VALUE = "X";

  public static final String KEY_SCALE_Y_VALUE = "Y";

  public static final String KEY_SCALE_X_AXIS_VISIBLE = "XAxisVisible";

  public static final String KEY_SCALE_X_AXIS_LENGTH_VALUE = "XAxisLength";

  public static final String KEY_SCALE_X_AXIS_TITLE_DOWNSIDE = "XAxisTitleDownSide";

  public static final String KEY_SCALE_X_AXIS_TEXT = "XAxisText";

  public static final String KEY_SCALE_X_AXIS_UNIT = "XAxisUnit";

  public static final String KEY_SCALE_Y_AXIS_LENGTH_VALUE = "YAxisLength";

  public static final String KEY_SCALE_Y_AXIS_VISIBLE = "YAxisVisible";

  public static final String KEY_SCALE_Y_AXIS_TITLE_LEFTSIDE = "YAxisTitleLeftSide";

  public static final String KEY_SCALE_AXIS_LENGTH_MODE = "AxisLengthMode";

  public static final String KEY_SCALE_Y_AXIS_TEXT = "YAxisText";

  public static final String KEY_SCALE_Y_AXIS_UNIT = "YAxisUnit";

  public static final String KEY_SCALE_LINE_COLOR = "LineColor";

  public static final String KEY_SCALE_FONT_COLOR = "FontColor";

  public static final String KEY_SCALE_TEXT_ANGLE = "Angle";

  //
  // Default Values
  //

  // Width
  public static final float DEFAULT_SCALE_SYMBOL_WIDTH = 2.0f;

  // Height
  public static final float DEFAULT_SCALE_SYMBOL_HEIGHT = 2.0f;

  // Line Width
  public static final float DEFAULT_SCALE_SYMBOL_LINE_WIDTH =
      SGILineAndStringConstants.DEFAULT_LINE_WIDTH;

  // Space
  public static final float DEFAULT_SCALE_SYMBOL_SPACE = SGILineAndStringConstants.DEFAULT_SPACE;

  // Horizontal Axis
  public static final String DEFAULT_SCALE_HORIZONTAL_AXIS =
      SGILineAndStringConstants.DEFAULT_HORIZONTAL_AXIS;

  // Vertical Axis
  public static final String DEFAULT_SCALE_VERTICAL_AXIS =
      SGILineAndStringConstants.DEFAULT_VERTICAL_AXIS;

  // Font
  public static final String DEFAULT_SCALE_SYMBOL_FONT_NAME =
      SGILineAndStringConstants.DEFAULT_FONT_NAME;

  public static final int DEFAULT_SCALE_SYMBOL_FONT_STYLE =
      SGILineAndStringConstants.DEFAULT_FONT_STYLE;

  public static final float DEFAULT_SCALE_SYMBOL_FONT_SIZE = 16.0f;

  // Color
  public static final Color DEFAULT_SCALE_SYMBOL_COLOR = SGILineAndStringConstants.DEFAULT_COLOR;

  // axis mode
  public static final SGIScaleConstants.AXIS_LENGTH_MODE DEFAULT_AXIS_CHANGE_MODE =
      SGIScaleConstants.AXIS_LENGTH_MODE.ADAPTIVE;

  public static final String COM_SCALE = "Scale";

  public static final String COM_SCALE_X_AXIS = "AxisX";

  public static final String COM_SCALE_Y_AXIS = "AxisY";

  public static final String COM_SCALE_VISIBLE = "Visible";

  public static final String COM_SCALE_LOCATION_X = "X";

  public static final String COM_SCALE_LOCATION_Y = "Y";

  public static final String COM_SCALE_AXIS_LENGTH_MODE = "AxisLengthMode";

  public static final String COM_SCALE_X_AXIS_VISIBLE = "XAxisVisible";

  public static final String COM_SCALE_X_AXIS_LENGTH = "XAxisLength";

  public static final String COM_SCALE_X_AXIS_TITLE_DOWNSIDE = "XAxisTitleDownside";

  public static final String COM_SCALE_X_AXIS_TITLE_TEXT = "XAxisTitleText";

  public static final String COM_SCALE_X_AXIS_TITLE_UNIT = "XAxisTitleUnit";

  public static final String COM_SCALE_Y_AXIS_VISIBLE = "YAxisVisible";

  public static final String COM_SCALE_Y_AXIS_LENGTH = "YAxisLength";

  public static final String COM_SCALE_Y_AXIS_TITLE_LEFT_SIDE = "YAxisTitleLeftside";

  public static final String COM_SCALE_Y_AXIS_TITLE_TEXT = "YAxisTitleText";

  public static final String COM_SCALE_Y_AXIS_TITLE_UNIT = "YAxisTitleUnit";

  public static final String COM_SCALE_LINE_COLOR = "LineColor";

  public static final String COM_SCALE_FONT_COLOR = "FontColor";

  public static final String COM_SCALE_FONT_ANGLE = "Angle";
}
