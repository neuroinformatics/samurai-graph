package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/** An observer of the property dialog for labels. */
public interface SGILabelDialogObserver extends SGIPropertyDialogObserver, SGITwoAxesHolder {

  /**
   * @return
   */
  public double getXValue();

  /**
   * @return
   */
  public double getYValue();

  /**
   * @return
   */
  public String getString();

  /**
   * @return
   */
  public Color getStringColor();

  /**
   * @return
   */
  public String getFontName();

  /** */
  public float getFontSize(final String unit);

  /** */
  public int getFontStyle();

  /**
   * @return
   */
  public float getAngle();

  /**
   * @param value
   */
  public boolean setXValue(final double value);

  /**
   * @param value
   */
  public boolean setYValue(final double value);

  /**
   * @param str
   * @return
   */
  public boolean setString(final String str);

  /**
   * @param color
   */
  public boolean setStringColor(final Color color);

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
   * @param angle
   * @return
   */
  public boolean setAngle(final float angle);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidXValue(final int config, final Number value);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidYValue(final int config, final Number value);
}
