package jp.riken.brain.ni.samuraigraph.figure;

import jp.riken.brain.ni.samuraigraph.base.SGIAnchored;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;

/** An observer of the property dialog for arrows. */
public interface SGIArrowDialogObserver
    extends SGIPropertyDialogObserver, SGITwoAxesHolder, SGIArrowPanelObserver, SGIAnchored {

  /**
   * @return
   */
  public double getStartXValue();

  /**
   * @return
   */
  public double getStartYValue();

  /**
   * @return
   */
  public double getEndXValue();

  /**
   * @return
   */
  public double getEndYValue();

  /**
   * @param value
   */
  public boolean setStartXValue(final double value);

  /**
   * @param value
   */
  public boolean setStartYValue(final double value);

  /**
   * @param value
   */
  public boolean setEndXValue(final double value);

  /**
   * @param value
   */
  public boolean setEndYValue(final double value);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidStartXValue(final int config, final Number value);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidStartYValue(final int config, final Number value);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidEndXValue(final int config, final Number value);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidEndYValue(final int config, final Number value);
}
