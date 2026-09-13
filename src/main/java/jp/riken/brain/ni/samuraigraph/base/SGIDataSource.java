package jp.riken.brain.ni.samuraigraph.base;

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

import java.util.List;

/** An interface for the data source. */
public interface SGIDataSource extends Cloneable, SGIDisposable {

  /**
   * Returns the file path.
   *
   * @return the file path
   */
  public String getPath();

  /**
   * Returns the list of attributes.
   *
   * @return the list of attributes
   */
  public List<SGAttribute> getAttributes();
}
