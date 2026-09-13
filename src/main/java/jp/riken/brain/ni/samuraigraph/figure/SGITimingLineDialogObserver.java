package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGIAnchored;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGISingleAxisHolder;

/** An observer of the property dialog for timing lines. */
public interface SGITimingLineDialogObserver
    extends SGIPropertyDialogObserver, SGISingleAxisHolder, SGIAnchored {

  /**
   * @return
   */
  public double getValue();

  public float getLineWidth(final String unit);

  /**
   * @return
   */
  public int getLineType();

  /**
   * @return
   */
  public Color getColor();

  /**
   * @param value
   */
  public boolean setValue(final double value);

  public boolean setLineWidth(final float width, final String unit);

  /**
   * @param type
   * @return
   */
  public boolean setLineType(final int type);

  /**
   * @param cl
   * @return
   */
  public boolean setColor(final Color cl);

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidValue(final int config, final Number value);
}
