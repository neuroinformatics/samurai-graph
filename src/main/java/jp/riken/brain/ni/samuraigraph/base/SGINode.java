package jp.riken.brain.ni.samuraigraph.base;

import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGRootObjectConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGTextDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;

import java.util.ArrayList;

/** An interface for the node in the tree structure. */
public interface SGINode {

  /**
   * Returns a list of child nodes.
   *
   * @return a list of chid nodes
   */
  public ArrayList<SGINode> getChildNodes();

  /**
   * Returns a name of the class.
   *
   * @return
   */
  public String getClassDescription();

  /**
   * Returns a name of an instance.
   *
   * @return
   */
  public String getInstanceDescription();
}
