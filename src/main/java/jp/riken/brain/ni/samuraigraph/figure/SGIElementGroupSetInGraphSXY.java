package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.application.SGDataPluginConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGRootObjectConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGTextDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGArrowConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGColorMapConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGElementGroupConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGFigureDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSymbolConstants.*;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import jp.riken.brain.ni.samuraigraph.base.SGIData;

/** An interface for the group set in scalar type xy graph. */
public interface SGIElementGroupSetInGraphSXY {

  /**
   * Returns the bounding box of tick labels for a given data.
   *
   * @param data a data object
   */
  public Rectangle2D getTickLabelsBoundingBox(final SGIData data);

  /**
   * Paint tick labels.
   *
   * @param g2d the graphics context
   */
  public boolean paintDataString(final Graphics2D g2d);
}
