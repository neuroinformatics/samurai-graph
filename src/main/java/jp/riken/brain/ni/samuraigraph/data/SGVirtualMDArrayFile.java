package jp.riken.brain.ni.samuraigraph.data;

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

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGAttribute;

/** The class of a virtual file with multidimensional number arrays. */
public class SGVirtualMDArrayFile extends SGMDArrayFile {

  /**
   * Builds a virtual file with an array of variables.
   *
   * @param vars an array of variables
   */
  public SGVirtualMDArrayFile(SGVirtualMDArrayVariable[] vars) {
    super();
    if (vars == null) {
      throw new IllegalArgumentException("vars == null");
    }
    this.mVariables = new SGMDArrayVariable[vars.length];
    for (int ii = 0; ii < vars.length; ii++) {
      this.mVariables[ii] = (SGMDArrayVariable) vars[ii].clone();
    }
  }

  @Override
  public String getPath() {
    // always returns null
    return null;
  }

  @Override
  public List<SGAttribute> getAttributes() {
    // always returns an empty list
    return new ArrayList<SGAttribute>();
  }
}
