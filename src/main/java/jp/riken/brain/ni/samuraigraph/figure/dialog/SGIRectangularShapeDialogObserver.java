package jp.riken.brain.ni.samuraigraph.figure.dialog;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGIAnchored;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/** An observer of the property dialog for rectangular shapes. */
public interface SGIRectangularShapeDialogObserver
    extends SGIPropertyDialogObserver, SGITwoAxesHolder, SGIAnchored {

  /**
   * @return
   */
  public double getLeftXValue();

  /**
   * @return
   */
  public double getRightXValue();

  /**
   * @return
   */
  public double getTopYValue();

  /**
   * @return
   */
  public double getBottomYValue();

  /**
   * @return
   */
  public float getLineWidth(final String unit);

  /**
   * @return
   */
  public int getLineType();

  /**
   * @return
   */
  public Color getLineColor();

  /**
   * @return
   */
  public boolean isLineVisible();

  /**
   * @return
   */
  public SGIPaint getInnerPaint();

  /**
   * @return
   */
  public float getTransparency();

  /**
   * @param value
   */
  public boolean setLeftXValue(final double value);

  /**
   * @param value
   */
  public boolean setRightXValue(final double value);

  /**
   * @param value
   */
  public boolean setTopYValue(final double value);

  /**
   * @param value
   */
  public boolean setBottomYValue(final double value);

  /** */
  public boolean setLineWidth(final float lw, final String unit);

  /**
   * @param type
   * @return
   */
  public boolean setLineType(final int type);

  /**
   * @param cl
   */
  public boolean setLineColor(final Color cl);

  /**
   * @param visible
   * @return
   */
  public boolean setLineVisible(final boolean visible);

  /**
   * @param paint
   */
  public boolean setInnerPaint(final SGIPaint paint);

  /**
   * @param alpha
   * @return
   */
  public boolean setTransparent(final float alpha);

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
  public boolean hasValidTopYValue(final int config, final Number value);

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
  public boolean hasValidBottomYValue(final int config, final Number value);
}
