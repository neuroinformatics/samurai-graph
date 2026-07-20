package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGApplicationUtility} pure utility methods. */
class SGApplicationUtilityTest {

  @Test
  void getPathNameConcatenatesParentAndChild() {
    String result = SGApplicationUtility.getPathName("/parent", "child");
    // Uses File.separator from SGIConstants
    assertTrue(result.startsWith("/parent"));
    assertTrue(result.endsWith("child"));
  }

  @Test
  void getPathNameHandlesEmptyStrings() {
    String result = SGApplicationUtility.getPathName("", "file.txt");
    assertEquals("/file.txt", result);
  }

  @Test
  void appendExtensionAddsDotAndExtension() {
    assertEquals("file.txt", SGApplicationUtility.appendExtension("file", "txt"));
    assertEquals("data.csv", SGApplicationUtility.appendExtension("data", "csv"));
  }

  @Test
  void appendExtensionWithExistingExtension() {
    assertEquals("file.txt.bak", SGApplicationUtility.appendExtension("file.txt", "bak"));
  }

  @Test
  void removeExtensionRemovesLastExtension() {
    assertEquals("file", SGApplicationUtility.removeExtension("file.txt"));
    assertEquals("file.tar", SGApplicationUtility.removeExtension("file.tar.gz"));
  }

  @Test
  void removeExtensionReturnsOriginalWhenNoDot() {
    assertEquals("nodot", SGApplicationUtility.removeExtension("nodot"));
  }

  @Test
  void removeExtensionWithMultipleDots() {
    assertEquals("archive.tar", SGApplicationUtility.removeExtension("archive.tar.gz"));
  }

  @Test
  void hasExtensionMatchesSingleExtension() {
    assertTrue(SGApplicationUtility.hasExtension("file.txt", "txt"));
    assertTrue(SGApplicationUtility.hasExtension("file.TXT", "txt"));
    assertFalse(SGApplicationUtility.hasExtension("file.txt", "csv"));
  }

  @Test
  void hasExtensionMatchesAnyInArray() {
    assertTrue(SGApplicationUtility.hasExtension("file.jpg", new String[] {"png", "jpg"}));
    assertFalse(SGApplicationUtility.hasExtension("file.txt", new String[] {"png", "jpg"}));
  }

  @Test
  void hasExtensionEmptyArrayReturnsFalse() {
    assertFalse(SGApplicationUtility.hasExtension("file.txt", new String[] {}));
  }

  @Test
  void findExtensionReturnsMatchForKnownExtensions() {
    // DRAWABLE_IMAGE_EXTENSIONS = {"jpg", "jpeg", "gif", "png"}
    assertEquals("jpg", SGApplicationUtility.findExtension("jpg"));
    assertEquals("jpg", SGApplicationUtility.findExtension("JPG"));
    assertEquals("jpeg", SGApplicationUtility.findExtension("JPEG"));
    assertEquals("gif", SGApplicationUtility.findExtension("Gif"));
    assertEquals("png", SGApplicationUtility.findExtension("png"));
  }

  @Test
  void findExtensionReturnsNullForUnknown() {
    assertNull(SGApplicationUtility.findExtension("xyz123"));
    assertNull(SGApplicationUtility.findExtension("bmp"));
  }
}
