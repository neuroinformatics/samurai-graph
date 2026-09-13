package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;

/** Client panel interface. */
public interface SGIClientPanel {

  public float getPaperWidth(final String unit);

  public float getPaperHeight(final String unit);

  public float getGridLineInterval(final String unit);

  public float getGridLineWidth(final String unit);

  /**
   * @return
   */
  public boolean isGridLineVisible();

  /**
   * @return
   */
  public Color getPaperColor();

  /**
   * @return
   */
  public Color getGridLineColor();

  public float getImageLocationX(final String unit);

  public float getImageLocationY(final String unit);

  public float getImageWidth(final String unit);

  public float getImageHeight(final String unit);

  /**
   * @return
   */
  public float getImageScalingFactor();

  public boolean setPaperWidth(final float value, final String unit);

  public boolean setPaperHeight(final float value, final String unit);

  public boolean setGridLineInterval(final float value, final String unit);

  public boolean setGridLineWidth(final float value, final String unit);

  /**
   * @param b
   * @return
   */
  public boolean setGridLineVisible(final boolean b);

  /**
   * @param cl
   * @return
   */
  public boolean setPaperColor(final Color cl);

  /**
   * @param cl
   * @return
   */
  public boolean setGridLineColor(final Color cl);

  public boolean setImageLocationX(final float value, final String unit);

  public boolean setImageLocationY(final float value, final String unit);

  /**
   * @param value
   * @return
   */
  public boolean setImageScalingFactor(final float value);
}
