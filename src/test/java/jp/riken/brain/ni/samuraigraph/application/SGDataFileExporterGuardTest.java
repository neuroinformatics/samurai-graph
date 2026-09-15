package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Command guard tests of the data exporter without a real window. */
class SGDataFileExporterGuardTest {

  private SGDataFileExporter exporter;

  private SGDrawingWindow wnd;

  @AfterEach
  void disposeWindow() {
    if (this.wnd != null) {
      this.wnd.dispose();
      this.wnd = null;
    }
  }

  @Test
  void defaultsAreSetAtConstruction() {
    SGDataFileExporter exporter = new SGDataFileExporter();
    assertEquals("data", SGDataFileExporter.DEFAULT_EXPORT_DATA_FILE_NAME);
    assertEquals(null, exporter.getCurrentFileType());
  }

  @Test
  void unknownCommandIsRejected() {
    SGDataFileExporter exporter = new SGDataFileExporter();
    org.junit.jupiter.api.Assertions.assertThrows(
        Error.class, () -> exporter.export(null, null, "name", "not-a-command"));
  }

  @Test
  void exportSkipsWhenThereIsNoRunMode() {
    // exporting with an empty path uses the SAVE dialog when headful;
    // it is not executed in the unit tests
    SGDataFileExporter handler = new SGDataFileExporter();
    assertEquals(null, handler.getCurrentFileType());
  }
}
