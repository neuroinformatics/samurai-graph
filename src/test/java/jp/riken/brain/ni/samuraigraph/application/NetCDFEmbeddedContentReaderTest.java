package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.ATTR_NAME_SAMURAI_GRAPH_COMMAND;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.ATTR_NAME_SAMURAI_GRAPH_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.write.NetcdfFormatWriter;

/** Unit tests of the NetCDF embedded content reader with a real NetCDF file. */
class NetCDFEmbeddedContentReaderTest {

  @TempDir Path tempDir;

  private Path writeFile(final String command, final String properties) throws IOException {
    final Path path = this.tempDir.resolve("embedded.nc");
    final NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3(path.toString());
    if (command != null) {
      builder.addAttribute(new Attribute(ATTR_NAME_SAMURAI_GRAPH_COMMAND, command));
    }
    if (properties != null) {
      builder.addAttribute(new Attribute(ATTR_NAME_SAMURAI_GRAPH_PROPERTIES, properties));
    }
    try (NetcdfFormatWriter writer = builder.build()) {
      // file written with the global attributes
    }
    return path;
  }

  @Test
  void readsEmbeddedCommandAndProperties() throws IOException {
    final Path path = this.writeFile("print x;", "<properties/>");
    try (NetcdfFile nc = NetcdfFiles.open(path.toString())) {
      final NetCDFEmbeddedContentReader reader = new NetCDFEmbeddedContentReader(nc);
      assertEquals("print x;", reader.getCommand());
      assertEquals("<properties/>", reader.getProperties());
      assertNotNull(reader.getPath());
      assertTrue(reader.getPath().endsWith("embedded.nc"));
    }
  }

  @Test
  void returnsNullForAbsentContents() throws IOException {
    final Path path = this.writeFile(null, null);
    try (NetcdfFile nc = NetcdfFiles.open(path.toString())) {
      final NetCDFEmbeddedContentReader reader = new NetCDFEmbeddedContentReader(nc);
      assertNull(reader.getCommand());
      assertNull(reader.getProperties());
    }
  }

  @Test
  void returnsNullForTheOtherAttribute() throws IOException {
    final Path path = this.writeFile("print x;", null);
    try (NetcdfFile nc = NetcdfFiles.open(path.toString())) {
      final NetCDFEmbeddedContentReader reader = new NetCDFEmbeddedContentReader(nc);
      assertEquals("print x;", reader.getCommand());
      assertNull(reader.getProperties());
    }
  }
}
