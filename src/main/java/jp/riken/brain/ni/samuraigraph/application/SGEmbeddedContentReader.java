package jp.riken.brain.ni.samuraigraph.application;

/**
 * Reads the embedded samurai-graph contents of a data file.
 *
 * <p>The contents are read on demand so that the read timing of the caller is preserved.
 */
interface SGEmbeddedContentReader {

  /** Returns the embedded command string or null if not present. */
  String getCommand();

  /** Returns the embedded properties string or null if not present. */
  String getProperties();

  /** Returns the path of the file. */
  String getPath();
}
