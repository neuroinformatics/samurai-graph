package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.FILE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Unit tests for the data file type identification in {@link SGApplicationUtility}. */
class SGApplicationUtilityNetCDFTest {

  @TempDir Path tempDir;

  @Test
  void textFileWithNetCDFExtensionIsIdentifiedAsText() throws Exception {
    Path file = this.tempDir.resolve("text.nc");
    Files.writeString(file, "not a netcdf file, but text\n");
    assertEquals(FILE_TYPE.TXT_DATA, SGApplicationUtility.identifyDataFileType(file.toString()));
  }

  @Test
  void missingNetCDFFileIsIdentifiedAsText() {
    Path missing = this.tempDir.resolve("missing.nc");
    assertEquals(FILE_TYPE.TXT_DATA, SGApplicationUtility.identifyDataFileType(missing.toString()));
  }
}
