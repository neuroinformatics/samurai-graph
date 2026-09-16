package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.FILE_TYPE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

/**
 * Categorizes the files to open with the application into the dedicated kinds.
 *
 * <p>The classifier and the dialogs are injected so that the routing logic can be tested without a
 * running display.
 */
final class SGFileOpenCategorizer {

  /** Classifies a single file path. */
  @FunctionalInterface
  interface OpenFileClassifier {
    FILE_TYPE classify(String path);
  }

  /** Confirms whether a NetCDF file should be loaded as a data set archive. */
  @FunctionalInterface
  interface NetCDFDataSetConfirmer {
    boolean isNetCDFArchive(SGDrawingWindow wnd, String path);
  }

  /** Handles a file reported as an unreadable HDF5 file. */
  @FunctionalInterface
  interface Hdf5UnreadableHandler {
    void handle(SGDrawingWindow wnd, String path);
  }

  private final OpenFileClassifier classifier;

  private final NetCDFDataSetConfirmer netcdfConfirmer;

  private final Hdf5UnreadableHandler hdf5UnreadableHandler;

  /**
   * Creates a categorizer.
   *
   * @param classifier the classifier of each file
   * @param confirmer the confirmation for a NetCDF data set archive
   * @param handler the handler for an unreadable HDF5 file
   */
  SGFileOpenCategorizer(
      final OpenFileClassifier classifier,
      final NetCDFDataSetConfirmer confirmer,
      final Hdf5UnreadableHandler handler) {
    super();
    this.classifier = classifier;
    this.netcdfConfirmer = confirmer;
    this.hdf5UnreadableHandler = handler;
  }

  /** Creates the categorizer with the production classifier and dialogs. */
  static SGFileOpenCategorizer create() {
    return new SGFileOpenCategorizer(
        SGOpenFileClassifier::classifyOpenFile,
        (wnd, path) -> {
          if (!SGNetCDFDataSetManager.isNetCDFDatasetFile(path)) {
            return false;
          }
          final String message =
              "This NetCDF file has samurai-graph properties. Do you apply them to the graph?";
          final int confirmResult = SGUtility.showYesNoConfirmationDialog(wnd, message);
          return confirmResult == JOptionPane.OK_OPTION;
        },
        (wnd, path) -> SGApplicationUtility.showHDF5ReadErrorMessageDialog(wnd, path));
  }

  /**
   * Analyzes the files and returns the categorized result.
   *
   * @param fileList the list of files
   * @param wnd the window or null
   * @return the categorized result
   */
  SGOpenFileCategory analyze(final List<File> fileList, final SGDrawingWindow wnd) {
    final List<File> textDataFileList = new ArrayList<File>();
    final List<File> netCDFDataFileList = new ArrayList<File>();
    final List<File> hdf5DataFileList = new ArrayList<File>();
    final List<File> matlabDataFileList = new ArrayList<File>();
    File propertyFile = null;
    File archiveFile = null;
    File netcdfArchiveFile = null;
    File scriptFile = null;
    File imageFile = null;
    for (File file : fileList) {
      final String path = file.getAbsolutePath();
      final FILE_TYPE type = this.classifier.classify(path);
      if (FILE_TYPE.PROPERTY.equals(type)) {
        propertyFile = file;
      } else if (FILE_TYPE.DATASET.equals(type)) {
        archiveFile = file;
      } else if (FILE_TYPE.SCRIPT.equals(type)) {
        scriptFile = file;
      } else if (FILE_TYPE.IMAGE.equals(type)) {
        imageFile = file;
      } else if (FILE_TYPE.POSSIBLY_HDF5_DATA.equals(type)) {
        this.hdf5UnreadableHandler.handle(wnd, path);
        return this.aborted();
      } else if (FILE_TYPE.MATLAB_DATA.equals(type)) {
        matlabDataFileList.add(file);
      } else if (FILE_TYPE.HDF5_DATA.equals(type)) {
        hdf5DataFileList.add(file);
      } else if (FILE_TYPE.NETCDF_DATA.equals(type)) {
        if (this.netcdfConfirmer.isNetCDFArchive(wnd, path)) {
          netcdfArchiveFile = file;
        } else {
          netCDFDataFileList.add(file);
        }
      } else {
        textDataFileList.add(file);
      }
    }
    return new SGOpenFileCategory(
        propertyFile,
        archiveFile,
        netcdfArchiveFile,
        scriptFile,
        imageFile,
        netCDFDataFileList,
        hdf5DataFileList,
        matlabDataFileList,
        textDataFileList,
        false);
  }

  private SGOpenFileCategory aborted() {
    return new SGOpenFileCategory(
        null,
        null,
        null,
        null,
        null,
        new ArrayList<File>(),
        new ArrayList<File>(),
        new ArrayList<File>(),
        new ArrayList<File>(),
        true);
  }
}
