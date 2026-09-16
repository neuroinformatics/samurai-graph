package jp.riken.brain.ni.samuraigraph.application;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;

/** The actions performed by the application for the embedded contents. */
interface SGEmbeddedActions {

  /**
   * Executes the embedded commands.
   *
   * @return true if the commands were executed
   */
  boolean executeEmbeddedCommands(String commands, String path, SGDrawingWindow wnd);

  /**
   * Applies the embedded properties.
   *
   * @return true if the properties were applied
   */
  boolean applyEmbeddedProperties(String commands, String path, SGDrawingWindow wnd);
}
