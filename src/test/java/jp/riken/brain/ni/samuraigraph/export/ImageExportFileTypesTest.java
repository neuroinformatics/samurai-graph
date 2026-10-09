package jp.riken.brain.ni.samuraigraph.export;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.freehep.graphicsbase.util.export.ExportFileType;
import org.freehep.graphicsbase.util.export.ExportFileTypeRegistry;
import org.junit.jupiter.api.Test;

/** Characterization tests for the bundled PNG and JPEG {@link ExportFileType} implementations. */
class ImageExportFileTypesTest {

  @Test
  void pngFileTypeReportsItsDescriptionExtensionsAndMimeType() {
    PNGExportFileType png = new PNGExportFileType();
    assertEquals("PNG Image", png.getDescription());
    assertArrayEquals(new String[] {"png"}, png.getExtensions());
    assertArrayEquals(new String[] {"image/png"}, png.getMIMETypes());
  }

  @Test
  void jpegFileTypeReportsItsDescriptionExtensionsAndMimeType() {
    JPEGExportFileType jpeg = new JPEGExportFileType();
    assertEquals("JPEG Image", jpeg.getDescription());
    assertArrayEquals(new String[] {"jpg", "jpeg"}, jpeg.getExtensions());
    assertArrayEquals(new String[] {"image/jpeg"}, jpeg.getMIMETypes());
  }

  @Test
  void pngAndJpegAreDiscoveredThroughTheirServiceFile() {
    // The bundled META-INF/services entry lists both types, so the default registry resolves
    // them by extension without any explicit registration.
    List<ExportFileType> png = ExportFileTypeRegistry.getDefaultInstance(null).get("png");
    assertEquals(1, png.size(), "the png extension should resolve to exactly the PNG type");
    List<ExportFileType> jpeg = ExportFileTypeRegistry.getDefaultInstance(null).get("jpeg");
    assertEquals(1, jpeg.size(), "the jpeg extension should resolve to exactly the JPEG type");
    assertNotNull(jpeg.get(0).getMIMETypes());
  }
}
