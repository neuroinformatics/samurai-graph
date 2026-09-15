package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.FILE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Characterization tests of {@link SGOpenFileClassifier}. */
class SGOpenFileClassifierTest {

  private static Path createTempFile(final String name, final String content) throws Exception {
    Path path = Files.createTempFile(name, null);
    path.toFile().deleteOnExit();
    Files.writeString(path, content == null ? "" : content);
    return path;
  }

  @Test
  void classifiesByDedicatedExtensions() throws Exception {
    String base = "/home/samurai-graph";
    assertEquals(FILE_TYPE.PROPERTY, SGOpenFileClassifier.classifyOpenFile(base + ".sgp"));
    assertEquals(FILE_TYPE.DATASET, SGOpenFileClassifier.classifyOpenFile(base + ".sga"));
    assertEquals(FILE_TYPE.SCRIPT, SGOpenFileClassifier.classifyOpenFile(base + ".sgs"));
    assertEquals(FILE_TYPE.IMAGE, SGOpenFileClassifier.classifyOpenFile(base + ".png"));
  }

  @Test
  void classifiesDataFilesByContent() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".txt");
    path.toFile().deleteOnExit();
    Files.writeString(path, "x y\n1 2\n");
    assertEquals(FILE_TYPE.TXT_DATA, SGOpenFileClassifier.classifyOpenFile(path.toString()));
    Files.deleteIfExists(path);
  }

  @Test
  void fallsBackToTextDataForUnknownBinary() throws Exception {
    // truncated HDF5-like content on a non-Windows environment falls back to
    // the text path: the broken-HDF5 diagnosis is Windows-specific
    Path path = Files.createTempFile("samurai-graph-test", "h5");
    path.toFile().deleteOnExit();
    Files.write(path, new byte[] {(byte) 0x89, 'H'});
    assertEquals(FILE_TYPE.TXT_DATA, SGOpenFileClassifier.classifyOpenFile(path.toString()));
    Files.delete(path);
  }
}
