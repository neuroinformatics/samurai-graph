package jp.riken.brain.ni.samuraigraph.application;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;

/**
 * Loads and applies the embedded samurai-graph commands and properties of a data file.
 *
 * <p>The two methods are intentionally kept independent so that the caller keeps the close timing
 * of the underlying reader.
 */
final class SGEmbeddedCommandLoader {

  /**
   * Executes the embedded commands if present.
   *
   * @param reader the reader of the embedded contents
   * @param wnd the window
   * @param actions the actions to execute the contents
   * @return true if the commands were executed
   */
  boolean executeEmbeddedCommands(
      final SGEmbeddedContentReader reader,
      final SGDrawingWindow wnd,
      final SGEmbeddedActions actions) {
    final String commands = reader.getCommand();
    if (commands != null) {
      return actions.executeEmbeddedCommands(commands, reader.getPath(), wnd);
    }
    return false;
  }

  /**
   * Applies the embedded properties if present.
   *
   * @param reader the reader of the embedded contents
   * @param wnd the window
   * @param actions the actions to apply the contents
   * @return true if the properties were applied
   */
  boolean applyEmbeddedProperties(
      final SGEmbeddedContentReader reader,
      final SGDrawingWindow wnd,
      final SGEmbeddedActions actions) {
    final String properties = reader.getProperties();
    if (properties != null) {
      return actions.applyEmbeddedProperties(properties, reader.getPath(), wnd);
    }
    return false;
  }
}
