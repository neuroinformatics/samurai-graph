package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Point;
import java.io.File;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles the files to open with the application.
 *
 * <p>The handler categorizes the files and routes them to the GUI actions, owned by {@link
 * SGOpenFileActions}, so that the routing is testable headlessly.
 */
final class SGFileOpenHandler {

  private static final Logger logger = LogManager.getLogger(SGFileOpenHandler.class);

  private final SGOpenFileActions actions;

  private final SGFileOpenCategorizer categorizer;

  /**
   * Creates a handler.
   *
   * @param actions the GUI actions
   * @param categorizer the categorizer of the files
   */
  SGFileOpenHandler(final SGOpenFileActions actions, final SGFileOpenCategorizer categorizer) {
    super();
    this.actions = actions;
    this.categorizer = categorizer;
  }

  /**
   * Opens the given files.
   *
   * @param fileList the list of files
   * @param wnd the window
   * @param pos the location the files are dropped or null
   * @return true if the operation succeeded
   */
  boolean execute(final List<File> fileList, final SGDrawingWindow wnd, final Point pos) {
    wnd.setWaitCursor(true);
    try {
      final SGOpenFileCategory category = this.categorizer.analyze(fileList, wnd);
      if (category.isAborted()) {
        return false;
      }
      if (category.getPropertyFile() != null) {
        if (wnd.needsConfirmationBeforeDiscard() && !this.actions.confirmDiscard(wnd)) {
          return true;
        }
        this.actions.closeModelessDialogs(wnd);
        if (!this.actions.handlePropertyFile(wnd, category.getPropertyFile())) {
          return false;
        }
      } else if (category.getArchiveFile() != null) {
        if (wnd.needsConfirmationBeforeDiscard() && !this.actions.confirmDiscard(wnd)) {
          return true;
        }
        this.actions.closeModelessDialogs(wnd);
        this.actions.handleDataSetArchive(wnd, category.getArchiveFile());
      } else if (category.getNetcdfArchiveFile() != null) {
        if (wnd.needsConfirmationBeforeDiscard() && !this.actions.confirmDiscard(wnd)) {
          return true;
        }
        this.actions.closeModelessDialogs(wnd);
        this.actions.handleNetCDFDataSetArchive(wnd, category.getNetcdfArchiveFile());
      } else if (category.getScriptFile() != null) {
        this.actions.handleScriptFile(wnd, category.getScriptFile());
      } else if (category.getImageFile() != null) {
        if (!this.actions.handleImageFile(wnd, category.getImageFile())) {
          return false;
        }
      } else if (!category.getNetCDFDataFileList().isEmpty()) {
        if (!this.actions.handleNetCDFData(wnd, pos, category.getNetCDFDataFileList())) {
          return false;
        }
      } else if (!category.getHDF5DataFileList().isEmpty()) {
        if (!this.actions.handleHDF5Data(wnd, pos, category.getHDF5DataFileList())) {
          return false;
        }
      } else if (!category.getMATLABDataFileList().isEmpty()) {
        if (!this.actions.handleMATLABData(wnd, pos, category.getMATLABDataFileList())) {
          return false;
        }
      } else {
        if (category.getTextDataFileList().isEmpty()) {
          return false;
        }
        if (!this.actions.handleTextData(wnd, pos, category.getTextDataFileList())) {
          return false;
        }
      }
      return true;
    } catch (Exception ex) {
      logger.warn("Error in main function operation", ex);
      return false;
    } finally {
      wnd.setWaitCursor(false);
    }
  }
}
