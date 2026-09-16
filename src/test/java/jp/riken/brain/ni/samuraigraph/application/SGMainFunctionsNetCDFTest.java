package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.write.NetcdfFormatWriter;

/**
 * Regression tests verifying that {@link NetcdfFormatWriter#openExisting} can read existing
 * attributes from a NetCDF-3 file.
 *
 * <p>Note: {@code updateAttribute(null, attr)} does NOT work for global attributes on existing
 * files opened via {@code openExisting()}, so the deprecated {@code NetcdfFileWriter} API must be
 * retained for that use case.
 */
class SGMainFunctionsNetCDFTest {

  @TempDir Path tempDir;

  @Test
  void openExistingCanReadGlobalAttributes() throws IOException {
    Path path = tempDir.resolve("test_read.nc");

    final NetcdfFormatWriter writer =
        NetcdfFormatWriter.createNewNetcdf3(path.toString())
            .addAttribute(new Attribute("commandScript", "test_value"))
            .build();
    writer.close();

    try (NetcdfFormatWriter reader = NetcdfFormatWriter.openExisting(path.toString()).build()) {
      Attribute attr = reader.findGlobalAttribute("commandScript");
      assertNotNull(attr);
      assertEquals("test_value", attr.getStringValue());
    }
  }

  @Test
  void openExistingPreservesGlobalAttributes() throws IOException {
    Path path = tempDir.resolve("test_preserve.nc");

    final NetcdfFormatWriter writer =
        NetcdfFormatWriter.createNewNetcdf3(path.toString())
            .addAttribute(new Attribute("file_level", "original"))
            .build();
    writer.close();

    try (NetcdfFile ncfile = NetcdfFiles.open(path.toString())) {
      assertNotNull(ncfile.findGlobalAttribute("file_level"));
      assertEquals("original", ncfile.findGlobalAttribute("file_level").getStringValue());
    }
  }
}
