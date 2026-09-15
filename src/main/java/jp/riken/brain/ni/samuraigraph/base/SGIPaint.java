package jp.riken.brain.ni.samuraigraph.base;

import static jp.riken.brain.ni.samuraigraph.base.SGAnimationConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDateConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGPaintConstant.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataPropertyKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;

import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import org.w3c.dom.Element;

/** An interface of inner painting. */
public interface SGIPaint extends Cloneable {

  public float getAlpha();

  /**
   * Set the transparency.
   *
   * @param alpha
   * @return true if it succeeds.
   */
  public boolean setAlpha(final float alpha);

  /**
   * Set the magnification.
   *
   * @param mag the magnification.
   * @return true if it succeeds.
   */
  public boolean setMagnification(float mag);

  public Paint getPaint(final Rectangle2D rect);

  public Object clone() throws CloneNotSupportedException;

  public SGPropertyMap getPropertyFileMap();

  public boolean writeProperty(final Element el);
}
