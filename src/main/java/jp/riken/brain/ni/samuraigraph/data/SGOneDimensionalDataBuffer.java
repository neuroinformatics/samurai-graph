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

import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;

/** An abstract class of the data buffer for one-dimensional data. */
public abstract class SGOneDimensionalDataBuffer extends SGDataBuffer {

  // The length of array.
  protected int mLength = 0;

  /** The default constructor. */
  public SGOneDimensionalDataBuffer() {
    super();
  }

  /**
   * Returns the length of array.
   *
   * @return the length of array
   */
  public int getLength() {
    return this.mLength;
  }

  /**
   * Returns true if this data buffer is of grid type.
   *
   * @return true if this data buffer is of grid type
   */
  @Override
  public boolean isGridType() {
    // always returns false
    return false;
  }

  @Override
  public String getGridTypeKey() {
    // always returns null
    return null;
  }
}
