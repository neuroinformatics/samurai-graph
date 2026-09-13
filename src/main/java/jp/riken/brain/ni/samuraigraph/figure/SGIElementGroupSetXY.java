package jp.riken.brain.ni.samuraigraph.figure;

import java.util.List;

public interface SGIElementGroupSetXY {

  /**
   * Returns a line group which is the first element of an array.
   *
   * <p>any line groups
   */
  public SGElementGroupLine getLineGroup();

  /**
   * Returns a symbol group which is the first element of an array.
   *
   * <p>have any symbol groups
   */
  public SGElementGroupSymbol getSymbolGroup();

  /**
   * Returns a bar group which is the first element of an array.
   *
   * <p>any bar groups
   */
  public SGElementGroupBar getBarGroup();

  /**
   * Returns an error bar group which is the first element of an array.
   *
   * <p>have any error bar groups
   */
  public SGElementGroupErrorBar getErrorBarGroup();

  /**
   * Returns a tick label group which is the first element of an array.
   *
   * <p>not have any tick label groups
   */
  public SGElementGroupTickLabel getTickLabelGroup();

  /** Returns a list of line groups. */
  public List<SGElementGroupLine> getLineGroups();

  /** Returns a list of symbol groups. */
  public List<SGElementGroupSymbol> getSymbolGroups();

  /** Returns a list of bar groups. */
  public List<SGElementGroupBar> getBarGroups();

  /** Returns a list of error bar groups. */
  public List<SGElementGroupErrorBar> getErrorBarGroups();

  /** Returns a list of tick label groups. */
  public List<SGElementGroupTickLabel> getTickLabelGroups();

  public boolean isLineVisible();

  public boolean isSymbolVisible();

  public boolean isBarVisible();

  public boolean setLineVisible(final boolean b);

  public boolean setSymbolVisible(final boolean b);

  public boolean setBarVisible(final boolean b);

  public boolean setErrorBarOnLinePosition(final boolean b);

  public double getBarWidthValue();

  public boolean setBarWidthValue(final double value);

  /** Returns whether line color is automatically assigned. */
  public boolean isLineColorAutoAssigned();

  /**
   * Sets whether line color is automatically assigned
   *
   * @param b a flag whether line color is automatically assigned
   */
  public void setLineColorAutoAssigned(final boolean b);

  /**
   * Returns the name of the color map for lines.
   *
   * @return the name of the color map for lines
   */
  public String getLineColorMapName();

  public boolean isErrorBarAvailable();

  public boolean isTickLabelAvailable();

  public static final String ELEMENT_GROUP_LINE = "Line";

  public static final String ELEMENT_GROUP_SYMBOL = "Symbol";

  public static final String ELEMENT_GROUP_BAR = "Bar";

  public static final String ELEMENT_GROUP_ERROR_BAR = "ErrorBar";

  public static final String ELEMENT_GROUP_TICK_LABEL = "TickLabel";
}
