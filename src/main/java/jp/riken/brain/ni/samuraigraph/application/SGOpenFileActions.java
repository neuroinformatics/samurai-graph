package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Point;
import java.io.File;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;

/**
 * The actions performed by the application for each kind of open file.
 *
 * <p>The open file handler only performs the routing and delegates the GUI operations to this
 * interface so that the routing can be tested headlessly with a fake implementation.
 */
interface SGOpenFileActions {

  /**
   * Confirms whether to discard the current content before loading a new file.
   *
   * @return false if the user cancelled
   */
  boolean confirmDiscard(SGDrawingWindow wnd);

  /** Closes all modeless dialogs. */
  void closeModelessDialogs(SGDrawingWindow wnd);

  /**
   * Applies the given property file.
   *
   * @return false if the property file cannot be applied
   */
  boolean handlePropertyFile(SGDrawingWindow wnd, File propertyFile);

  /** Loads the given data set archive file. */
  boolean handleDataSetArchive(SGDrawingWindow wnd, File archiveFile);

  /** Loads the given NetCDF data set archive file. */
  boolean handleNetCDFDataSetArchive(SGDrawingWindow wnd, File archiveFile);

  /** Runs the given script file. */
  void handleScriptFile(SGDrawingWindow wnd, File scriptFile);

  /**
   * Applies the given image file.
   *
   * @return false if the image file cannot be applied
   */
  boolean handleImageFile(SGDrawingWindow wnd, File imageFile);

  /** Adds the given NetCDF data files. */
  boolean handleNetCDFData(SGDrawingWindow wnd, Point pos, List<File> fileList);

  /** Adds the given HDF5 data files. */
  boolean handleHDF5Data(SGDrawingWindow wnd, Point pos, List<File> fileList);

  /** Adds the given MATLAB data files. */
  boolean handleMATLABData(SGDrawingWindow wnd, Point pos, List<File> fileList);

  /** Adds the given text data files. */
  boolean handleTextData(SGDrawingWindow wnd, Point pos, List<File> fileList);
}
