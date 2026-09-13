package jp.riken.brain.ni.samuraigraph.base;

import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementAxisConstants.*;

/** An interface which provides the definition of the objects which holds two axes. */
public interface SGITwoAxesHolder extends SGIAxisHolder {

  /**
   * Returns the location of the x axis in a plane.
   *
   * @return one of the following values defined in
   *     jp.riken.brain.ni.samuraigraph.base.SGIAxisElement : AXIS_HORIZONTAL_1, AXIS_HORIZONTAL_2,
   *     AXIS_VERTICAL_1 or AXIS_VERTICAL_2.
   */
  public int getXAxisLocation();

  /**
   * Returns the location of the y axis in a plane.
   *
   * @return one of the following values defined in
   *     jp.riken.brain.ni.samuraigraph.base.SGIAxisElement : AXIS_HORIZONTAL_1, AXIS_HORIZONTAL_2,
   *     AXIS_VERTICAL_1 or AXIS_VERTICAL_2.
   */
  public int getYAxisLocation();

  /**
   * Sets the location of the x axis in a plane.
   *
   * @param location - one of the following values defined in
   *     jp.riken.brain.ni.samuraigraph.base.SGIAxisElement : AXIS_HORIZONTAL_1, AXIS_HORIZONTAL_2,
   *     AXIS_VERTICAL_1 or AXIS_VERTICAL_2.
   * @return
   */
  public boolean setXAxisLocation(final int location);

  /**
   * Sets the location of the y axis in a plane.
   *
   * @param location - one of the following values defined in
   *     jp.riken.brain.ni.samuraigraph.base.SGIAxisElement : AXIS_HORIZONTAL_1, AXIS_HORIZONTAL_2,
   *     AXIS_VERTICAL_1 or AXIS_VERTICAL_2.
   * @return
   */
  public boolean setYAxisLocation(final int location);
}
