package jp.riken.brain.ni.samuraigraph.figure.dialog;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGIDialogObserver;

/** An observer of the property panel for arrows. */
public interface SGIArrowPanelObserver extends SGIDialogObserver {

  public float getLineWidth(final String unit);

  /**
   * @return
   */
  public int getLineType();

  /**
   * @return
   */
  public Color getColor();

  public float getHeadSize(final String unit);

  /**
   * @return
   */
  public float getHeadOpenAngle();

  /**
   * @return
   */
  public float getHeadCloseAngle();

  /**
   * @return
   */
  public int getStartHeadType();

  /**
   * @return
   */
  public int getEndHeadType();

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

  public boolean setHeadSize(final float size, final String unit);

  /**
   * * @return
   *
   * @param openAngle the openAngle parameter
   * @param closeAngle the closeAngle parameter
   */
  public boolean setHeadAngle(final Float openAngle, final Float closeAngle);

  /**
   * @param type
   * @return
   */
  public boolean setStartHeadType(final int type);

  /**
   * @param type
   * @return
   */
  public boolean setEndHeadType(final int type);

  /**
   * @param open
   * @param close
   * @return
   */
  public boolean hasValidAngle(final Number open, final Number close);
}
