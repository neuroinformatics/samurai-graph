package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/** An observer of the property dialog for legend. */
interface SGILegendDialogObserver extends SGIPropertyDialogObserver, SGITwoAxesHolder {

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
  public String getFontName();

  /** */
  public float getFontSize(final String unit);

  /** */
  public int getFontStyle();

  /**
   * @return
   */
  public Color getFontColor();

  /**
   * @return
   */
  public boolean isFrameVisible();

  /**
   * @return
   */
  public float getFrameLineWidth(final String unit);

  /**
   * @return
   */
  public Color getFrameColor();

  /**
   * @return
   */
  public boolean isVisible();

  /**
   * @return
   */
  public int getBackgroundTransparency();

  /**
   * @return
   */
  public Color getBackgroundColor();

  /**
   * @return
   */
  public float getSymbolSpan(final String unit);

  /**
   * @param value
   */
  public boolean setXValue(final double value);

  /**
   * @param value
   */
  public boolean setYValue(final double value);

  /**
   * @param b
   */
  public boolean setVisible(final boolean b);

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

  /**
   * @param size
   * @return
   * @param unit the unit parameter
   */
  public boolean setFontSize(final float size, final String unit);

  /**
   * @param cl
   */
  public boolean setFontColor(final Color cl);

  /**
   * @param b
   */
  public boolean setFrameVisible(final boolean b);

  /**
   * @param width
   * @param unit the unit parameter
   */
  public boolean setFrameLineWidth(final float width, final String unit);

  /**
   * @param cl
   */
  public boolean setFrameLineColor(final Color cl);

  /**
   * @param percentAlpha
   */
  public boolean setBackgroundTransparent(final int percentAlpha);

  /**
   * @param cl
   */
  public boolean setBackgroundColor(Color cl);

  /**
   * @param w the w parameter
   * @param unit the unit parameter
   */
  public boolean setSymbolSpan(final float w, final String unit);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidXAxisValue(final int config, final Number value);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidYAxisValue(final int config, final Number value);
}
