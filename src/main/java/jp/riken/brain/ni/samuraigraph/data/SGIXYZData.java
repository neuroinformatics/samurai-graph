package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGRootObjectConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGTextDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;

import jp.riken.brain.ni.samuraigraph.base.SGValueRange;

/** An interface for the data with X, Y and Z axes. */
public interface SGIXYZData extends SGIXYData {

  /**
   * Returns the bounds of z-values.
   *
   * @return the bounds of z-values
   */
  public SGValueRange getBoundsZ();

  /**
   * Returns the bounds of z-values for all animation frames.
   *
   * @return the bounds of z-values
   */
  public SGValueRange getAllAnimationFrameBoundsZ();

  /**
   * Returns the title of z-values.
   *
   * @return the title of z-values
   */
  public String getTitleZ();
}
