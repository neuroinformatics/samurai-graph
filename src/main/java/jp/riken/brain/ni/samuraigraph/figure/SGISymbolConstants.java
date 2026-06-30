package jp.riken.brain.ni.samuraigraph.figure;

/** Constants for the symbols. */
public final class SGISymbolConstants {

  /*
   * Constants for the symbol types.
   */
  public static final int SYMBOL_TYPE_CIRCLE = 1;

  public static final int SYMBOL_TYPE_SQUARE = 2;

  public static final int SYMBOL_TYPE_DIAMOND = 3;

  public static final int SYMBOL_TYPE_TRIANGLE = 4;

  public static final int SYMBOL_TYPE_INVERTED_TRIANGLE = 5;

  public static final int SYMBOL_TYPE_CROSS = 6;

  public static final int SYMBOL_TYPE_PLUS = 7;

  public static final int[] SYMBOL_TYPE_ARRAY = {
    SGISymbolConstants.SYMBOL_TYPE_CIRCLE,
    SGISymbolConstants.SYMBOL_TYPE_SQUARE,
    SGISymbolConstants.SYMBOL_TYPE_DIAMOND,
    SGISymbolConstants.SYMBOL_TYPE_TRIANGLE,
    SGISymbolConstants.SYMBOL_TYPE_INVERTED_TRIANGLE,
    SGISymbolConstants.SYMBOL_TYPE_CROSS,
    SGISymbolConstants.SYMBOL_TYPE_PLUS
  };

  /*
   * Constants for the symbol names.
   */
  public static final String SYMBOL_NAME_CIRCLE = "Circle";

  public static final String SYMBOL_NAME_SQUARE = "Square";

  public static final String SYMBOL_NAME_DIAMOND = "Diamond";

  public static final String SYMBOL_NAME_TRIANGLE = "Triangle";

  public static final String SYMBOL_NAME_INVERTED_TRIANGLE = "Inverted Triangle";

  // added from version 0.6.1
  // declared to maintain downward compatibility with the old releases
  public static final String SYMBOL_NAME_INVERTED_TRIANGLE_OLD = "I-Triangle";

  public static final String SYMBOL_NAME_CROSS = "Cross";

  public static final String SYMBOL_NAME_PLUS = "Plus";

  public static final String[] SYMBOL_NAME_ARRAY = {
    SGISymbolConstants.SYMBOL_NAME_CIRCLE,
    SGISymbolConstants.SYMBOL_NAME_SQUARE,
    SGISymbolConstants.SYMBOL_NAME_DIAMOND,
    SGISymbolConstants.SYMBOL_NAME_TRIANGLE,
    SGISymbolConstants.SYMBOL_NAME_INVERTED_TRIANGLE,
    SGISymbolConstants.SYMBOL_NAME_CROSS,
    SGISymbolConstants.SYMBOL_NAME_PLUS
  };

  /*
   * Constants for the property file.
   */
  public static final String TAG_NAME_SYMBOL = "Symbol";

  public static final String KEY_SYMBOL_SIZE = "Size";

  public static final String KEY_SYMBOL_TYPE = "Type";

  public static final String KEY_SYMBOL_LINE_WIDTH = "LineWidth";

  public static final String KEY_SYMBOL_LINE_COLOR = "LineColor";

  public static final String KEY_SYMBOL_LINE_VISIBLE = "LineVisible";

  public static final String KEY_SYMBOL_INNER_COLOR_LIST = "InnerColor";

  public static final String KEY_SYMBOL_INNER_TRANSPARENT = "InnerTransparent";
}
