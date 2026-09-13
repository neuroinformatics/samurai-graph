package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationTextConstants.*;
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
import static jp.riken.brain.ni.samuraigraph.figure.SGArrowConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGFigureDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSXYDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGShapeConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGStringConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSymbolConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGTimingLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGVXYDataConstants.*;

import com.sun.jna.Pointer;

/** Wrapper class of data plug-in. */
public class SGDataCalcLibrary {

  private SGIDataCalcLibrary mLibrary;

  public SGDataCalcLibrary(SGIDataCalcLibrary lib) {
    super();
    if (lib == null) {
      throw new IllegalArgumentException("lib == null");
    }
    this.mLibrary = lib;
  }

  /** Returns a text string of identifier. */
  public String getId() {
    return Integer.toString(this.mLibrary.hashCode());
  }

  /**
   * Processes given data and returns the result.
   *
   * @param input pointer for input data
   */
  public Pointer calc(Pointer input) {
    return this.mLibrary.calc(input);
  }

  /**
   * Frees the memory of given data buffer.
   *
   * @param buffer data buffer
   */
  public void freeData(Pointer buffer) {
    this.mLibrary.freeData(buffer);
  }
}
