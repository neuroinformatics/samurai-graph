package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGIAnchored;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/** An observer of the property dialog for axis break symbols. */
public interface SGIAxisBreakDialogObserver
    extends SGIPropertyDialogObserver, SGITwoAxesHolder, SGIAnchored {

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
  public float getLength(final String unit);

  /**
   * @return
   */
  public float getInterval(final String unit);

  /**
   * @return
   */
  public float getDistortion();

  /**
   * @return
   */
  public float getAngle();

  /**
   * @return
   */
  public float getLineWidth(final String unit);

  /**
   * @return
   */
  public boolean isForHorizontalAxis();

  /**
   * @return
   */
  public Color getLineColor();

  /**
   * @return
   */
  public Color getInnerColor();

  /**
   * @param value
   */
  public boolean setXValue(final double value);

  /**
   * @param value
   */
  public boolean setYValue(final double value);

  /**
   * @param value
   * @param unit the unit parameter
   */
  public boolean setLength(final float value, final String unit);

  /**
   * @param value
   * @param unit the unit parameter
   */
  public boolean setInterval(final float value, final String unit);

  /**
   * @param value
   * @param unit the unit parameter
   */
  public boolean setLineWidth(final float value, final String unit);

  /**
   * @param angle
   */
  public boolean setAngle(final float angle);

  /**
   * @param value
   */
  public boolean setDistortion(final float value);

  /**
   * @param color
   */
  public boolean setLineColor(final Color color);

  /**
   * @param color
   */
  public boolean setInnerColor(final Color color);

  /**
   * @param flag
   */
  public boolean setForHorizontalAxisFlag(final boolean flag);

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
