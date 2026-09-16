package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.FILE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests of the file open handler routing with fake actions. */
class SGFileOpenHandlerTest {

  private static File file(final String name) {
    return new File("/open-handler-test/" + name);
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

  private static final class RecordingActions implements SGOpenFileActions {
    private final List<String> calls = new ArrayList<String>();
    private boolean discard = true;
    private boolean property = true;
    private boolean image = true;
    private boolean text = true;
    private boolean throwOnText = false;
    private File lastFile = null;

    List<String> getCalls() {
      return this.calls;
    }

    File getLastFile() {
      return this.lastFile;
    }

    @Override
    public boolean confirmDiscard(final SGDrawingWindow wnd) {
      this.calls.add("confirmDiscard");
      return this.discard;
    }

    @Override
    public void closeModelessDialogs(final SGDrawingWindow wnd) {
      this.calls.add("closeModelessDialogs");
    }

    @Override
    public boolean handlePropertyFile(final SGDrawingWindow wnd, final File propertyFile) {
      this.calls.add("handlePropertyFile");
      this.lastFile = propertyFile;
      return this.property;
    }

    @Override
    public boolean handleDataSetArchive(final SGDrawingWindow wnd, final File archiveFile) {
      this.calls.add("handleDataSetArchive");
      this.lastFile = archiveFile;
      return true;
    }

    @Override
    public boolean handleNetCDFDataSetArchive(final SGDrawingWindow wnd, final File archiveFile) {
      this.calls.add("handleNetCDFDataSetArchive");
      this.lastFile = archiveFile;
      return true;
    }

    @Override
    public void handleScriptFile(final SGDrawingWindow wnd, final File scriptFile) {
      this.calls.add("handleScriptFile");
      this.lastFile = scriptFile;
    }

    @Override
    public boolean handleImageFile(final SGDrawingWindow wnd, final File imageFile) {
      this.calls.add("handleImageFile");
      this.lastFile = imageFile;
      return this.image;
    }

    @Override
    public boolean handleNetCDFData(
        final SGDrawingWindow wnd, final Point pos, final List<File> fs) {
      this.calls.add("handleNetCDFData");
      this.lastFile = fs.get(0);
      return true;
    }

    @Override
    public boolean handleHDF5Data(final SGDrawingWindow wnd, final Point pos, final List<File> fs) {
      this.calls.add("handleHDF5Data");
      this.lastFile = fs.get(0);
      return true;
    }

    @Override
    public boolean handleMATLABData(
        final SGDrawingWindow wnd, final Point pos, final List<File> fs) {
      this.calls.add("handleMATLABData");
      this.lastFile = fs.get(0);
      return true;
    }

    @Override
    public boolean handleTextData(final SGDrawingWindow wnd, final Point pos, final List<File> fs) {
      this.calls.add("handleTextData");
      this.lastFile = fs.get(0);
      if (this.throwOnText) {
        throw new IllegalStateException("thrown by fake action");
      }
      return this.text;
    }
  }

  private SGDrawingWindow wnd;

  private RecordingActions actions;

  private boolean netCDFArchive = false;

  private SGFileOpenCategorizer categorizer() {
    return new SGFileOpenCategorizer(
        path -> classify(path), (w, path) -> this.netCDFArchive, (w, path) -> {});
  }

  private SGFileOpenHandler handler() {
    return new SGFileOpenHandler(this.actions, this.categorizer());
  }

  @BeforeEach
  void setUp() {
    this.wnd = mock(SGDrawingWindow.class);
    this.actions = new RecordingActions();
  }

  @Test
  void routesPropertyFileWithoutDiscardConfirmation() {
    final File f = file("graph.sgp");
    assertTrue(handler().execute(Collections.singletonList(f), this.wnd, null));
    assertEquals(
        java.util.Arrays.asList("closeModelessDialogs", "handlePropertyFile"),
        this.actions.getCalls());
    assertEquals(f, this.actions.getLastFile());
    verify(this.wnd).setWaitCursor(true);
    verify(this.wnd).setWaitCursor(false);
  }

  @Test
  void cancelsPropertyFileOnDiscardConfirmation() {
    when(this.wnd.needsConfirmationBeforeDiscard()).thenReturn(true);
    this.actions.discard = false;
    final File f = file("graph.sgp");
    assertTrue(handler().execute(Collections.singletonList(f), this.wnd, null));
    assertEquals(Collections.singletonList("confirmDiscard"), this.actions.getCalls());
  }

  @Test
  void confirmsDiscardThenRoutesPropertyFile() {
    when(this.wnd.needsConfirmationBeforeDiscard()).thenReturn(true);
    final File f = file("graph.sgp");
    assertTrue(handler().execute(Collections.singletonList(f), this.wnd, null));
    assertEquals(
        java.util.Arrays.asList("confirmDiscard", "closeModelessDialogs", "handlePropertyFile"),
        this.actions.getCalls());
  }

  @Test
  void failsOnPropertyFileRejected() {
    this.actions.property = false;
    final File f = file("graph.sgp");
    assertFalse(handler().execute(Collections.singletonList(f), this.wnd, null));
    assertEquals(
        java.util.Arrays.asList("closeModelessDialogs", "handlePropertyFile"),
        this.actions.getCalls());
  }

  @Test
  void routesArchiveFile() {
    final File f = file("data.sga");
    assertTrue(handler().execute(Collections.singletonList(f), this.wnd, null));
    assertEquals(
        java.util.Arrays.asList("closeModelessDialogs", "handleDataSetArchive"),
        this.actions.getCalls());
    assertEquals(f, this.actions.getLastFile());
  }

  @Test
  void routesNetCDFArchiveFile() {
    this.netCDFArchive = true;
    final File f = file("data.nc");
    assertTrue(handler().execute(Collections.singletonList(f), this.wnd, null));
    assertEquals(
        java.util.Arrays.asList("closeModelessDialogs", "handleNetCDFDataSetArchive"),
        this.actions.getCalls());
    assertEquals(f, this.actions.getLastFile());
  }

  @Test
  void routesNetCDFDataFile() {
    final File f = file("data.nc");
    assertTrue(handler().execute(Collections.singletonList(f), this.wnd, null));
    assertEquals(Collections.singletonList("handleNetCDFData"), this.actions.getCalls());
    assertEquals(f, this.actions.getLastFile());
  }

  @Test
  void routesScriptFile() {
    final File f = file("script.sgs");
    assertTrue(handler().execute(Collections.singletonList(f), this.wnd, null));
    assertEquals(Collections.singletonList("handleScriptFile"), this.actions.getCalls());
    assertEquals(f, this.actions.getLastFile());
  }

  @Test
  void routesImageFile() {
    final File f = file("figure.png");
    assertTrue(handler().execute(Collections.singletonList(f), this.wnd, null));
    assertEquals(Collections.singletonList("handleImageFile"), this.actions.getCalls());
    assertEquals(f, this.actions.getLastFile());
  }

  @Test
  void failsOnImageFileRejected() {
    this.actions.image = false;
    final File f = file("figure.png");
    assertFalse(handler().execute(Collections.singletonList(f), this.wnd, null));
    assertEquals(Collections.singletonList("handleImageFile"), this.actions.getCalls());
  }

  @Test
  void routesTextDataFile() {
    final File f = file("plain.txt");
    assertTrue(handler().execute(Collections.singletonList(f), this.wnd, null));
    assertEquals(Collections.singletonList("handleTextData"), this.actions.getCalls());
    assertEquals(f, this.actions.getLastFile());
  }

  @Test
  void failsOnEmptyFileList() {
    assertFalse(handler().execute(Collections.emptyList(), this.wnd, null));
    assertTrue(this.actions.getCalls().isEmpty());
  }

  @Test
  void failsOnUnreadableHDF5BeforeAnyAction() {
    final File f = file("broken.bad");
    assertFalse(handler().execute(Collections.singletonList(f), this.wnd, null));
    assertTrue(this.actions.getCalls().isEmpty());
  }

  @Test
  void netCDFFilesTakePrecedenceOverHDF5Files() {
    final File netcdf = file("values.nc");
    final File hdf5 = file("signals.h5");
    assertTrue(handler().execute(java.util.Arrays.asList(netcdf, hdf5), this.wnd, null));
    assertEquals(Collections.singletonList("handleNetCDFData"), this.actions.getCalls());
  }

  @Test
  void returnsFalseAndResetsCursorOnActionException() {
    this.actions.throwOnText = true;
    final File f = file("plain.txt");
    assertFalse(handler().execute(Collections.singletonList(f), this.wnd, null));
    verify(this.wnd).setWaitCursor(true);
    verify(this.wnd).setWaitCursor(false);
  }
}
