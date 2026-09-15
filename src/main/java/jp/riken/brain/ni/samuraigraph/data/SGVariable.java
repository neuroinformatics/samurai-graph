package jp.riken.brain.ni.samuraigraph.data;

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

import jp.riken.brain.ni.samuraigraph.base.SGIDisposable;

/** The base class of variables. */
public abstract class SGVariable implements SGIDisposable {

  /** A flag whether this object is disposed of. */
  private boolean mDisposed = false;

  /** The default constructor. */
  public SGVariable() {
    super();
  }

  /** Returns the name of this variable. */
  public abstract String getName();

  /** Disposes of this object. */
  @Override
  public void dispose() {
    this.mDisposed = true;
  }

  /**
   * Returns whether this object is already disposed of.
   *
   * @return true if this object is already disposed of
   */
  @Override
  public boolean isDisposed() {
    return this.mDisposed;
  }
}
