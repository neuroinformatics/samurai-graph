package jp.riken.brain.ni.samuraigraph.figure.dialog;

import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;

/** An observer of the property dialog for two-dimensional vector-type data. */
public interface SGIVXYDataDialogObserver
    extends SGIDataPropertyDialogObserver, SGITwoAxesHolder, SGIArrowPanelObserver {

  /**
   * @return
   */
  public float getMagnitudePerCM();

  /**
   * @param f
   * @return
   */
  public boolean setMagnitudePerCM(final float f);

  /**
   * @return
   */
  public boolean isDirectionInvariant();

  /**
   * @param b
   * @return
   */
  public boolean setDirectionInvariant(final boolean b);

  /**
   * Sets the stride for the x-direction.
   *
   * @param stride stride of arrays
   * @return true if succeeded
   */
  public boolean setStrideX(SGIntegerSeriesSet stride);

  /**
   * Sets the stride for the y-direction.
   *
   * @param stride stride of arrays
   * @return true if succeeded
   */
  public boolean setStrideY(SGIntegerSeriesSet stride);
}
