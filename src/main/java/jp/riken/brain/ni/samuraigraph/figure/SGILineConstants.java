package jp.riken.brain.ni.samuraigraph.figure;

/** Constants for lines. */
public final class SGILineConstants {

  /*
   * Constants for the line types.
   */
  public static final int LINE_TYPE_SOLID = 1;

  public static final int LINE_TYPE_BROKEN = 2;

  public static final int LINE_TYPE_DOTTED = 3;

  public static final int LINE_TYPE_DASHED = 4;

  public static final int LINE_TYPE_DOUBLE_DASHED = 5;

  public static final int[] LINE_TYPE_ARRAY = {
    SGILineConstants.LINE_TYPE_SOLID,
    SGILineConstants.LINE_TYPE_BROKEN,
    SGILineConstants.LINE_TYPE_DOTTED,
    SGILineConstants.LINE_TYPE_DASHED,
    SGILineConstants.LINE_TYPE_DOUBLE_DASHED
  };

  /*
   * Constants for the line names.
   */
  public static final String LINE_NAME_SOLID = "Solid";

  public static final String LINE_NAME_BROKEN = "Broken";

  public static final String LINE_NAME_DOTTED = "Dotted";

  public static final String LINE_NAME_DASHED = "Dashed";

  public static final String LINE_NAME_DOUBLE_DASHED = "Double Dashed";

  public static final String[] LINE_NAME_ARRAY = {
    SGILineConstants.LINE_NAME_SOLID,
    SGILineConstants.LINE_NAME_BROKEN,
    SGILineConstants.LINE_NAME_DOTTED,
    SGILineConstants.LINE_NAME_DASHED,
    SGILineConstants.LINE_NAME_DOUBLE_DASHED
  };

  /*
   * Constants for the property file.
   */
  public static final String TAG_NAME_LINE = "Line";

  public static final String KEY_LINE_WIDTH = "Width";

  public static final String KEY_LINE_TYPE = "Type";

  public static final String KEY_LINE_CONNECT_ALL_EFFECTIVE_POINTS = "ConnectAllEffectivePoints";

  public static final String TAG_NAME_STYLES = "Styles";

  public static final String TAG_NAME_STYLE = "Style";
}
