package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationCommandConstants.SCRIPT_FILE_EXTENSION;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.FILE_TYPE;
import static jp.riken.brain.ni.samuraigraph.application.SGArchiveFileConstants.ARCHIVE_FILE_EXTENSION;
import static jp.riken.brain.ni.samuraigraph.application.SGImageConstants.DRAWABLE_IMAGE_EXTENSIONS;
import static jp.riken.brain.ni.samuraigraph.base.SGPropertyFileConstants.PROPERTY_FILE_EXTENSION;

/**
 * The classifier for a file to open with the application, deciding its category from the file name
 * only.
 */
final class SGOpenFileClassifier {

  /** Not instantiable. */
  private SGOpenFileClassifier() {}

  /**
   * Classifies a file from its path. The files that cannot be identified as the dedicated kinds are
   * reported as text data.
   *
   * @param path an absolute path of the file
   * @return the file type
   */
  static FILE_TYPE classifyOpenFile(final String path) {
    if (SGApplicationUtility.hasExtension(path, PROPERTY_FILE_EXTENSION)) {
      return FILE_TYPE.PROPERTY;
    }
    if (SGApplicationUtility.hasExtension(path, ARCHIVE_FILE_EXTENSION)) {
      return FILE_TYPE.DATASET;
    }
    if (SGApplicationUtility.hasExtension(path, SCRIPT_FILE_EXTENSION)) {
      return FILE_TYPE.SCRIPT;
    }
    if (SGApplicationUtility.hasExtension(path, DRAWABLE_IMAGE_EXTENSIONS)) {
      return FILE_TYPE.IMAGE;
    }
    return SGApplicationUtility.identifyDataFileType(path);
  }
}
