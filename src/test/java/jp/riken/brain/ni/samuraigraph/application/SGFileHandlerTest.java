package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import org.junit.jupiter.api.Test;

/** Unit tests for file path handling in {@link SGFileHandler}. */
class SGFileHandlerTest {

  private static final String HOME_PATH = System.getProperty("user.home");

  @Test
  void currentFileIsNullByDefault() {
    SGFileHandler handler = new SGFileHandler();
    assertNull(handler.getCurrentFile());
  }

  @Test
  void fileReferenceIsSetFromDirectoryAndName() {
    SGFileHandler handler = new SGFileHandler();
    File file = handler.setCurrentFile("/tmp", "data.txt");
    assertEquals("/tmp/data.txt", file.getPath());
    assertEquals("data.txt", handler.getCurrentFile().getName());
  }

  @Test
  void nullFileNameBecomesEmptyString() {
    SGFileHandler handler = new SGFileHandler();
    File file = handler.setCurrentFile("/tmp", null);
    assertEquals("/tmp", file.getPath());
  }

  @Test
  void fileNameCanBeChangedAfterwards() {
    SGFileHandler handler = new SGFileHandler();
    handler.setCurrentDirectory("/tmp");
    assertEquals("/tmp", handler.getCurrentFile().getPath());
    handler.setCurrentFileName("figure2.sgf");
    File file = handler.getCurrentFile();
    assertEquals("figure2", SGApplicationUtility.removeExtension(file.getName()));
  }

  @Test
  void defaultFilePathAppendsExtension() {
    SGArchiveFileExtractor extractor = new SGArchiveFileExtractor();
    File file = extractor.getCurrentFile();
    assertEquals(HOME_PATH, file.getParent());
    assertEquals("dataset.sga", file.getName());
  }
}
