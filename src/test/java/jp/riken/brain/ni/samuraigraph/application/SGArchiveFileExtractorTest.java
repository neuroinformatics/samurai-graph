package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Unit tests for {@link SGArchiveFileExtractor}. */
class SGArchiveFileExtractorTest {

  @TempDir Path tempDir;

  private File createTestZip() throws Exception {
    File zipFile = new File(this.tempDir.toFile(), "archive.zip");
    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
      zos.putNextEntry(new ZipEntry("sub/"));
      zos.closeEntry();
      ZipEntry textEntry = new ZipEntry("sub/hello.txt");
      zos.putNextEntry(textEntry);
      zos.write("hello".getBytes("US-ASCII"));
      zos.closeEntry();
    }
    return zipFile;
  }

  @Test
  void archiveFileIsInitializedAtConstruction() {
    SGArchiveFileExtractor extractor = new SGArchiveFileExtractor();
    File currentFile = extractor.getCurrentFile();
    assertEquals("dataset.sga", currentFile.getName());
  }

  @Test
  void zipFileIsExtractedIntoDestinationDirectory() throws Exception {
    SGArchiveFileExtractor extractor = new SGArchiveFileExtractor();
    File zipFile = this.createTestZip();
    File destDir = this.tempDir.resolve("out").toFile();
    assertTrue(destDir.mkdirs());

    final int res = extractor.extract(null, destDir.getPath(), zipFile.getPath());
    assertEquals(0, res);

    File textFile = new File(destDir, "sub/hello.txt");
    assertTrue(textFile.isFile());
    assertEquals("hello", Files.readString(textFile.toPath()));
    assertTrue(new File(destDir, "sub").isDirectory());

    ArrayList<File> list = extractor.getExtractedFileList();
    assertEquals(2, list.size());
    assertTrue(list.contains(textFile));
  }

  @Test
  void extractedFilesAreDeleted() throws Exception {
    SGArchiveFileExtractor extractor = new SGArchiveFileExtractor();
    File zipFile = this.createTestZip();
    Path destPath = this.tempDir.resolve("out");
    assertTrue(destPath.toFile().mkdirs());
    assertEquals(0, extractor.extract(null, destPath.toFile().getPath(), zipFile.getPath()));

    File textFile = new File(destPath.toFile(), "sub/hello.txt");
    assertTrue(extractor.deleteExtractedFiles());
    assertTrue(!textFile.exists());
    assertTrue(extractor.getExtractedFileList().isEmpty());
  }

  @Test
  void nonExistentDestinationDirectoryIsRejected() throws Exception {
    SGArchiveFileExtractor extractor = new SGArchiveFileExtractor();
    File zipFile = this.createTestZip();
    final int res =
        extractor.extract(
            null, this.tempDir.resolve("missing").toFile().getPath(), zipFile.getPath());
    assertEquals(-2, res);
  }

  @Test
  void nonExistentArchiveFileIsRejected() throws Exception {
    SGArchiveFileExtractor extractor = new SGArchiveFileExtractor();
    File destDir = this.tempDir.resolve("out").toFile();
    assertTrue(destDir.mkdirs());
    final int res = extractor.extract(null, destDir.getPath(), "nonexistent.sgarchive");
    assertEquals(-2, res);
  }
}
