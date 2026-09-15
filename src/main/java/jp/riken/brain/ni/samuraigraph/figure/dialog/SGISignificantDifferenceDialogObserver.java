package jp.riken.brain.ni.samuraigraph.figure.dialog;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGIAnchored;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/** An observer of the property dialog for significant difference symbols. */
public interface SGISignificantDifferenceDialogObserver
    extends SGIPropertyDialogObserver, SGITwoAxesHolder, SGIAnchored {

  /**
   * @return
   */
  public String getText();

  /**
   * @return
   */
  public double getHorizontalYValue();

  /**
   * @return
   */
  public double getLeftXValue();

  /**
   * @return
   */
  public double getLeftYValue();

  /**
   * @return
   */
  public double getRightXValue();

  /**
   * @return
   */
  public double getRightYValue();

  /**
   * @return
   */
  public boolean isLineVisible();

  /**
   * @return
   */
  public float getSpace(final String unit);

  /**
   * @return
   */
  public float getLineWidth(final String unit);

  /**
   * @return
   */
  public String getFontName();

  /**
   * @return
   */
  public float getFontSize(final String unit);

  /**
   * @return
   */
  public int getFontStyle();

  /**
   * @return
   */
  public Color getColor();

  /**
   * @param str
   * @return
   */
  public boolean setText(final String str);

  /**
   * @param value
   */
  public boolean setHorizontalYValue(final double value);

  /**
   * @param value
   */
  public boolean setLeftXValue(final double value);

  /**
   * @param value
   */
  public boolean setLeftYValue(final double value);

  /**
   * @param value
   */
  public boolean setRightXValue(final double value);

  /**
   * @param value
   */
  public boolean setRightYValue(final double value);

  /**
   * @param b
   */
  public boolean setLineVisible(final boolean b);

  /** */
  public boolean setSpace(final float space, final String unit);

  /** */
  public boolean setLineWidth(final float lineWidth, final String unit);

  /**
   * @param name
   * @return
   */
  public boolean setFontName(final String name);

  /**
   * @param style
   * @return
   */
  public boolean setFontStyle(final int style);

  public boolean setFontSize(final float size, final String unit);

  /**
   * @param cl
   */
  public boolean setColor(final Color cl);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidHorizontalYValue(final int config, final Number value);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidLeftXValue(final int config, final Number value);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidLeftYValue(final int config, final Number value);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidRightXValue(final int config, final Number value);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidRightYValue(final int config, final Number value);
}
