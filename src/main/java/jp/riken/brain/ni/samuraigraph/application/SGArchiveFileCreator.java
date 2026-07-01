package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow.BackgroundImage;
import jp.riken.brain.ni.samuraigraph.base.SGExtensionFileFilter;
import jp.riken.brain.ni.samuraigraph.base.SGFileChooser;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;

/** Create an archive file. */
public class SGArchiveFileCreator extends SGFileHandler implements SGIApplicationConstants {

  /** Constant value of End of File */
  protected static final int EOF = -1;

  /** Constructs an object. */
  public SGArchiveFileCreator() {
    super();
    this.initFilePath(
        SGIArchiveFileConstants.DEFAULT_ARCHIVE_FILE_NAME,
        SGIArchiveFileConstants.ARCHIVE_FILE_EXTENSION);
  }

  /** Create a dataset archive file. */
  public int create(final SGDrawingWindow wnd, final String rootDir, final String pathName) {
    File zFile = new File(pathName);
    return this.create(wnd, rootDir, zFile);
  }

  //
  public int create(final SGDrawingWindow wnd, final String rootDir, final File zFile) {
    final File root = new File(rootDir);
    if (!root.isDirectory()) {
      return -1;
    }

    // get file list
    List<File> fList = getFileList(root);
    if (fList.size() == 0) {
      return -2;
    }

    final String rootName = root.getAbsolutePath() + File.separator;

    ByteArrayOutputStream baOs = null;
    ZipOutputStream zos = null;

    FileOutputStream fos = null;
    BufferedOutputStream bos = null;

    try {
      // check file size
      baOs = new ByteArrayOutputStream();
      zos = new ZipOutputStream(baOs);

      List<ZipEntry> entries = new ArrayList<ZipEntry>(); // array list of ZipEntry
      for (int ii = 0; ii < fList.size(); ii++) {
        File file = fList.get(ii);
        String fname = file.getAbsolutePath();
        String entryname = fname.substring(rootName.length());
        ZipEntry entry = new ZipEntry(entryname);
        entries.add(entry);
        writeFileEntry(zos, file, entry);
      }

      BackgroundImage bgImg = wnd.getBackgroundImage();
      byte[] imageByteArray = null;
      ZipEntry imageZipEntry = null;
      if (bgImg != null) {
        imageByteArray = bgImg.getByteArray();
        String ext = bgImg.getExtension();
        StringBuffer sb = new StringBuffer();
        sb.append(SGIArchiveFileConstants.ARCHIVE_IMAGE_NAME);
        sb.append('.');
        sb.append(ext);
        String entryname = sb.toString();
        imageZipEntry = new ZipEntry(entryname);
        writeFileEntry(zos, imageByteArray, imageZipEntry);
      }

      zos.close();
      baOs.close();

      // create zip file image
      baOs = new ByteArrayOutputStream();
      zos = new ZipOutputStream(baOs);

      for (int ii = 0; ii < fList.size(); ii++) {
        File file = fList.get(ii);
        ZipEntry entry = entries.get(ii);
        writeFileEntry(zos, file, entry);
      }

      if (bgImg != null) {
        writeFileEntry(zos, imageByteArray, imageZipEntry);
      }

      zos.finish();

      // get compressed data and write zip file
      byte[] bufResult = baOs.toByteArray();
      fos = new FileOutputStream(zFile);
      bos = new BufferedOutputStream(fos);
      bos.write(bufResult, 0, bufResult.length);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
      return -1;
    } catch (ZipException ex) {
      ex.printStackTrace();
      return -1;
    } catch (IOException ex) {
      ex.printStackTrace();
      return -1;
    } finally {
      try {
        if (zos != null) zos.close();
      } catch (Exception e) {
      }
      try {
        if (baOs != null) baOs.close();
      } catch (Exception e) {
      }
      try {
        if (bos != null) bos.close();
      } catch (Exception e) {
      }
      try {
        if (fos != null) fos.close();
      } catch (Exception e) {
      }
    }
    return SGIConstants.OK_OPTION;
  }

  // get file bytes
  private static byte[] _getFileBytes(final File file) {
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    try {
      int len = (int) file.length();
      fis = new FileInputStream(file);
      bis = new BufferedInputStream(fis, len);
      byte buf[] = new byte[len];
      bis.read(buf, 0, len);
      return buf;
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      try {
        if (bis != null) {
          bis.close();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      try {
        if (fis != null) {
          fis.close();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return null;
  }

  public static class ArchiveFile {
    public File file = null;
    public String desc = null;
  }

  /** Change file extension in chooser text filename field by selecting file filter. */
  public static class FileFilterPropertyChangeListener implements PropertyChangeListener {

    private final SGFileChooser chooser;

    private File currentFile = null;

    public FileFilterPropertyChangeListener(SGFileChooser chooser, File currentFile) {
      this.chooser = chooser;
      this.currentFile = currentFile;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      String s = evt.getPropertyName();
      FileChooserUI ui = chooser.getUI();
      BasicFileChooserUI bui = null;
      if (ui instanceof BasicFileChooserUI) {
        bui = (BasicFileChooserUI) ui;
      }
      if (bui == null) {
        return;
      }
      if (s.equals(JFileChooser.FILE_FILTER_CHANGED_PROPERTY)) {
        File file = this.chooser.getSelectedFile();
        if (null != file) {
          this.currentFile = file;
          return;
        } else {
          this.currentFile = new File(this.currentFile.getParentFile(), bui.getFileName());
        }
      }
    }
  }

  private JFileChooser createFileChooser() {
    // set the file extension filter
    SGExtensionFileFilter ffOlder = new SGExtensionFileFilter();
    ffOlder.setExplanation(SGIArchiveFileConstants.ARCHIVE_FILE_DESCRIPTION_SGA107);
    ffOlder.addExtension(SGIArchiveFileConstants.ARCHIVE_FILE_EXTENSION);
    SGExtensionFileFilter ff = new SGExtensionFileFilter();
    ff.setExplanation(SGIArchiveFileConstants.ARCHIVE_FILE_DESCRIPTION);
    ff.addExtension(SGIArchiveFileConstants.ARCHIVE_FILE_EXTENSION);
    SGExtensionFileFilter[] ffArray = {ffOlder, ff};
    JFileChooser chooser =
        SGApplicationUtility.createFileChooser(
            this.mCurrentDirectory, this.mCurrentFileName, ffArray, "");

    // add property change listener for selection of file filter.
    String path = SGApplicationUtility.getPathName(this.mCurrentDirectory, this.mCurrentFileName);
    FileFilterPropertyChangeListener ffChangeListener =
        new FileFilterPropertyChangeListener((SGFileChooser) chooser, new File(path));
    chooser.addPropertyChangeListener(ffChangeListener);

    return chooser;
  }

  /**
   * @param wnd
   * @return
   */
  public ArchiveFile getArchiveFileFromFileChooser(final Component parent) throws IOException {

    // creates a file chooser
    JFileChooser chooser = this.createFileChooser();

    // show save dialog
    final int ret = chooser.showSaveDialog(parent);

    ArchiveFile aFile = new ArchiveFile();
    if (ret == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      this.setSelectedFile(file);

      FileFilter filter = chooser.getFileFilter();
      if (filter instanceof SGExtensionFileFilter) {
        SGExtensionFileFilter fSelected = (SGExtensionFileFilter) filter;
        final String desc = fSelected.getExplanation();
        aFile.desc = desc;
      } else {
        aFile.desc = SGIArchiveFileConstants.ARCHIVE_FILE_DESCRIPTION;
      }
      aFile.file = file;
    }

    return aFile;
  }

  /**
   * Get the target file list
   *
   * @param dirname : root directory
   */
  private static List<File> _getDirFileList(final String dirname) {
    List<File> list = new ArrayList<File>();
    File dir = new File(dirname);
    if (!dir.isDirectory()) {
      return list;
    }
    File[] files = dir.listFiles();
    for (int ii = 0; ii < files.length; ii++) {
      if (files[ii].isFile()) {
        list.add(files[ii]);
      } else if (files[ii].isDirectory()) {
        String _dirname = SGApplicationUtility.getPathName(dirname, files[ii].getName());
        List<File> sublist = _getDirFileList(_dirname);
        list.addAll(sublist);
      }
    }
    return list;
  }

  /**
   * Get the correct target file list
   *
   * @param dir : root directory
   */
  private static List<File> getFileList(final File dir) {
    String absDirname = dir.getAbsolutePath();
    List<File> list = new ArrayList<File>();
    // get file list
    List<File> fList = _getDirFileList(absDirname);
    // check file list
    for (int ii = 0; ii < fList.size(); ii++) {
      File file = fList.get(ii);
      String absFname = file.getAbsolutePath();
      if (!file.isFile()) {
        continue; // this is not file
      }
      if (!absFname.startsWith(absDirname)) {
        continue; // this file not exist in root dir
      }
      file = new File(absFname);
      list.add(file);
    }
    return list;
  }

  /**
   * Write the file and zip entry to zip stream.
   *
   * @param zos : archive file output stream
   * @param file : target file
   * @param entry : Zip Entry
   * @throws ZipException
   * @throws IOException
   */
  private static void writeFileEntry(
      final ZipOutputStream zos, final File file, final ZipEntry entry)
      throws ZipException, IOException {
    byte[] buf = _getFileBytes(file);
    zos.putNextEntry(entry); // start writing target file
    zos.write(buf, 0, buf.length);
    zos.closeEntry(); // end of writing target file
  }

  private static void writeFileEntry(final ZipOutputStream zos, byte[] b, final ZipEntry entry)
      throws ZipException, IOException {
    zos.putNextEntry(entry); // start writing target file
    zos.write(b, 0, b.length);
    zos.closeEntry(); // end of writing target file
  }
}
