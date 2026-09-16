package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.FILE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests of the file categorization for the open file handler. */
class SGFileOpenCategorizerTest {

  private final List<String> hdf5Handled = new ArrayList<String>();

  private boolean netCDFArchive = false;

  private static File file(final String name) {
    return new File("/open-file-test/" + name);
  }

  private static FILE_TYPE classify(final String path) {
    if (path.endsWith(".sgp")) {
      return FILE_TYPE.PROPERTY;
    }
    if (path.endsWith(".sga")) {
      return FILE_TYPE.DATASET;
    }
    if (path.endsWith(".sgs")) {
      return FILE_TYPE.SCRIPT;
    }
    if (path.endsWith(".png")) {
      return FILE_TYPE.IMAGE;
    }
    if (path.endsWith(".nc")) {
      return FILE_TYPE.NETCDF_DATA;
    }
    if (path.endsWith(".h5")) {
      return FILE_TYPE.HDF5_DATA;
    }
    if (path.endsWith(".mat")) {
      return FILE_TYPE.MATLAB_DATA;
    }
    if (path.endsWith(".bad")) {
      return FILE_TYPE.POSSIBLY_HDF5_DATA;
    }
    return FILE_TYPE.TXT_DATA;
  }

  private SGFileOpenCategorizer categorizer() {
    return new SGFileOpenCategorizer(
        path -> classify(path),
        (wnd, path) -> this.netCDFArchive,
        (wnd, path) -> this.hdf5Handled.add(path));
  }

  @Test
  void classifiesPropertyFile() {
    final File f = file("graph.sgp");
    final SGOpenFileCategory category = categorizer().analyze(Arrays.asList(f), null);
    assertSame(f, category.getPropertyFile());
    assertNull(category.getArchiveFile());
    assertNull(category.getScriptFile());
    assertNull(category.getImageFile());
    assertNull(category.getNetcdfArchiveFile());
    assertTrue(category.getNetCDFDataFileList().isEmpty());
    assertTrue(category.getHDF5DataFileList().isEmpty());
    assertTrue(category.getMATLABDataFileList().isEmpty());
    assertTrue(category.getTextDataFileList().isEmpty());
    assertFalse(category.isAborted());
  }

  @Test
  void classifiesArchiveFile() {
    final File f = file("data.sga");
    final SGOpenFileCategory category = categorizer().analyze(Arrays.asList(f), null);
    assertSame(f, category.getArchiveFile());
    assertNull(category.getPropertyFile());
  }

  @Test
  void classifiesScriptFile() {
    final File f = file("script.sgs");
    final SGOpenFileCategory category = categorizer().analyze(Arrays.asList(f), null);
    assertSame(f, category.getScriptFile());
  }

  @Test
  void classifiesImageFile() {
    final File f = file("figure.png");
    final SGOpenFileCategory category = categorizer().analyze(Arrays.asList(f), null);
    assertSame(f, category.getImageFile());
  }

  @Test
  void classifiesNetCDFDataFile() {
    this.netCDFArchive = false;
    final File f = file("values.nc");
    final SGOpenFileCategory category = categorizer().analyze(Arrays.asList(f), null);
    assertEquals(Collections.singletonList(f), category.getNetCDFDataFileList());
    assertNull(category.getNetcdfArchiveFile());
  }

  @Test
  void classifiesNetCDFArchiveFile() {
    this.netCDFArchive = true;
    final File f = file("values.nc");
    final SGOpenFileCategory category = categorizer().analyze(Arrays.asList(f), null);
    assertSame(f, category.getNetcdfArchiveFile());
    assertTrue(category.getNetCDFDataFileList().isEmpty());
  }

  @Test
  void classifiesHDF5DataFile() {
    final File f = file("values.h5");
    final SGOpenFileCategory category = categorizer().analyze(Arrays.asList(f), null);
    assertEquals(Collections.singletonList(f), category.getHDF5DataFileList());
  }

  @Test
  void classifiesMATLABDataFile() {
    final File f = file("values.mat");
    final SGOpenFileCategory category = categorizer().analyze(Arrays.asList(f), null);
    assertEquals(Collections.singletonList(f), category.getMATLABDataFileList());
  }

  @Test
  void classifiesTextDataFile() {
    final File f = file("values.txt");
    final SGOpenFileCategory category = categorizer().analyze(Arrays.asList(f), null);
    assertEquals(Collections.singletonList(f), category.getTextDataFileList());
  }

  @Test
  void reportsUnreadableHDF5AsAborted() {
    final File f = file("broken.bad");
    final SGOpenFileCategory category = categorizer().analyze(Arrays.asList(f), null);
    assertTrue(category.isAborted());
    assertEquals(Collections.singletonList(f.getAbsolutePath()), this.hdf5Handled);
  }

  @Test
  void categorizesMixedFiles() {
    final File property = file("graph.sgp");
    final File netcdf = file("values.nc");
    final File hdf5 = file("signals.h5");
    final File text = file("plain.txt");
    final SGOpenFileCategory category =
        categorizer().analyze(Arrays.asList(property, netcdf, hdf5, text), null);
    assertSame(property, category.getPropertyFile());
    assertEquals(Collections.singletonList(netcdf), category.getNetCDFDataFileList());
    assertEquals(Collections.singletonList(hdf5), category.getHDF5DataFileList());
    assertEquals(Collections.singletonList(text), category.getTextDataFileList());
    assertNull(category.getArchiveFile());
    assertNull(category.getScriptFile());
    assertNull(category.getImageFile());
  }

  @Test
  void lastCategoryFileWins() {
    final File first = file("first.sgp");
    final File second = file("second.sgp");
    final SGOpenFileCategory category = categorizer().analyze(Arrays.asList(first, second), null);
    assertSame(second, category.getPropertyFile());
  }

  @Test
  void emptyFileListIsNotAborted() {
    final SGOpenFileCategory category = categorizer().analyze(Collections.emptyList(), null);
    assertFalse(category.isAborted());
    assertNull(category.getPropertyFile());
    assertNull(category.getArchiveFile());
    assertNull(category.getScriptFile());
    assertNull(category.getImageFile());
    assertNull(category.getNetcdfArchiveFile());
    assertTrue(category.getNetCDFDataFileList().isEmpty());
    assertTrue(category.getHDF5DataFileList().isEmpty());
    assertTrue(category.getMATLABDataFileList().isEmpty());
    assertTrue(category.getTextDataFileList().isEmpty());
  }
}
