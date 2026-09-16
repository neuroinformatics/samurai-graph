package jp.riken.brain.ni.samuraigraph.application;

import java.io.File;
import java.util.List;

/**
 * The categorized files to open with the application.
 *
 * <p>At most one file is kept for the dedicated kinds (property, archive, NetCDF archive, script
 * and image), and lists hold the data files by type. When {@link #isAborted()} is true, the
 * analysis was aborted (an unreadable HDF5 file was reported) and the other fields are meaningless.
 */
final class SGOpenFileCategory {

  private final File propertyFile;

  private final File archiveFile;

  private final File netcdfArchiveFile;

  private final File scriptFile;

  private final File imageFile;

  private final List<File> netCDFDataFileList;

  private final List<File> hdf5DataFileList;

  private final List<File> matlabDataFileList;

  private final List<File> textDataFileList;

  private final boolean aborted;

  /**
   * Creates a category.
   *
   * @param propertyFile a property file or null
   * @param archiveFile an archive file or null
   * @param netcdfArchiveFile a NetCDF archive file or null
   * @param scriptFile a script file or null
   * @param imageFile an image file or null
   * @param netCDFDataFileList the list of NetCDF data files
   * @param hdf5DataFileList the list of HDF5 data files
   * @param matlabDataFileList the list of MATLAB data files
   * @param textDataFileList the list of text data files
   * @param aborted true if the analysis was aborted
   */
  SGOpenFileCategory(
      final File propertyFile,
      final File archiveFile,
      final File netcdfArchiveFile,
      final File scriptFile,
      final File imageFile,
      final List<File> netCDFDataFileList,
      final List<File> hdf5DataFileList,
      final List<File> matlabDataFileList,
      final List<File> textDataFileList,
      final boolean aborted) {
    super();
    this.propertyFile = propertyFile;
    this.archiveFile = archiveFile;
    this.netcdfArchiveFile = netcdfArchiveFile;
    this.scriptFile = scriptFile;
    this.imageFile = imageFile;
    this.netCDFDataFileList = netCDFDataFileList;
    this.hdf5DataFileList = hdf5DataFileList;
    this.matlabDataFileList = matlabDataFileList;
    this.textDataFileList = textDataFileList;
    this.aborted = aborted;
  }

  /** Returns the property file or null. */
  File getPropertyFile() {
    return this.propertyFile;
  }

  /** Returns the archive file or null. */
  File getArchiveFile() {
    return this.archiveFile;
  }

  /** Returns the NetCDF archive file or null. */
  File getNetcdfArchiveFile() {
    return this.netcdfArchiveFile;
  }

  /** Returns the script file or null. */
  File getScriptFile() {
    return this.scriptFile;
  }

  /** Returns the image file or null. */
  File getImageFile() {
    return this.imageFile;
  }

  /** Returns the list of NetCDF data files. */
  List<File> getNetCDFDataFileList() {
    return this.netCDFDataFileList;
  }

  /** Returns the list of HDF5 data files. */
  List<File> getHDF5DataFileList() {
    return this.hdf5DataFileList;
  }

  /** Returns the list of MATLAB data files. */
  List<File> getMATLABDataFileList() {
    return this.matlabDataFileList;
  }

  /** Returns the list of text data files. */
  List<File> getTextDataFileList() {
    return this.textDataFileList;
  }

  /** Returns true if the analysis was aborted. */
  boolean isAborted() {
    return this.aborted;
  }
}
