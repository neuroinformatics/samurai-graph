package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests of the extension file filter. */
class SGExtensionFileFilterTest {

  @Test
  void addExtensionNormalizesTheEntry() {
    final SGExtensionFileFilter filter = new SGExtensionFileFilter();
    filter.addExtension("nc");
    filter.addExtension(".Nc");
    final List<String> list = filter.getExtensionList();
    assertEquals(2, list.size());
    assertEquals(".nc", list.get(0));
    assertEquals(".nc", list.get(1));
  }

  @Test
  void getExtensionListReturnsACopy() {
    final SGExtensionFileFilter filter = new SGExtensionFileFilter();
    filter.addExtension("nc");
    filter.getExtensionList().clear();
    assertEquals(1, filter.getExtensionList().size());
  }

  @Test
  void explanationRoundTrips() {
    final SGExtensionFileFilter filter = new SGExtensionFileFilter();
    assertEquals("", filter.getExplanation());
    filter.setExplanation("netCDF files");
    assertEquals("netCDF files", filter.getExplanation());
  }

  @Test
  void descriptionListsTheExtensions() {
    final SGExtensionFileFilter filter = new SGExtensionFileFilter();
    filter.setExplanation("Data");
    filter.addExtension("nc");
    filter.addExtension("h5");
    assertEquals("Data (*.nc, *.h5)", filter.getDescription());
  }

  @Test
  void descriptionFallsBackToAllFiles() {
    final SGExtensionFileFilter filter = new SGExtensionFileFilter();
    filter.setExplanation("All");
    assertEquals("All (*.*)", filter.getDescription());
  }

  @Test
  void acceptMatchesTheExtensionsCaseInsensitively() {
    final SGExtensionFileFilter filter = new SGExtensionFileFilter();
    filter.addExtension("nc");
    assertTrue(filter.accept(new File("DATA.NC")));
    assertFalse(filter.accept(new File("data.h5")));
    // existing directories are always accepted
    assertTrue(filter.accept(new File(System.getProperty("java.io.tmpdir"))));
  }

  @Test
  void acceptChecksEveryExtension() {
    final SGExtensionFileFilter filter = new SGExtensionFileFilter();
    filter.addExtension("nc");
    filter.addExtension("h5");
    assertTrue(filter.accept(new File("data.h5")));
    assertNotSame(filter.getExtensionList(), filter.getExtensionList());
  }
}
