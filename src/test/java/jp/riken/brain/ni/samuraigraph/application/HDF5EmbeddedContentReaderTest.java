package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.ATTR_NAME_SAMURAI_GRAPH_COMMAND;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.ATTR_NAME_SAMURAI_GRAPH_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5FileReader;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5ObjectReader;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5StringDatatypeInterface;
import java.io.File;
import org.junit.jupiter.api.Test;

/** Unit tests of the HDF5 embedded content reader with a mocked reader. */
class HDF5EmbeddedContentReaderTest {

  private static HDF5EmbeddedContentReader reader(
      final boolean commandPresent, final boolean propertiesPresent) {
    final IHDF5Reader reader = mock(IHDF5Reader.class);
    final IHDF5ObjectReader object = mock(IHDF5ObjectReader.class);
    final IHDF5StringDatatypeInterface string = mock(IHDF5StringDatatypeInterface.class);
    final IHDF5FileReader fileReader = mock(IHDF5FileReader.class);
    when(reader.object()).thenReturn(object);
    when(reader.string()).thenReturn(string);
    when(reader.file()).thenReturn(fileReader);
    when(fileReader.getFile()).thenReturn(new File("/embedded-test/data.h5"));
    when(object.hasAttribute("/", ATTR_NAME_SAMURAI_GRAPH_COMMAND)).thenReturn(commandPresent);
    when(object.hasAttribute("/", ATTR_NAME_SAMURAI_GRAPH_PROPERTIES))
        .thenReturn(propertiesPresent);
    when(string.getAttr("/", ATTR_NAME_SAMURAI_GRAPH_COMMAND)).thenReturn("print x;");
    when(string.getAttr("/", ATTR_NAME_SAMURAI_GRAPH_PROPERTIES)).thenReturn("<properties/>");
    return new HDF5EmbeddedContentReader(reader);
  }

  @Test
  void readsEmbeddedCommandAndProperties() {
    final HDF5EmbeddedContentReader reader = reader(true, true);
    assertEquals("print x;", reader.getCommand());
    assertEquals("<properties/>", reader.getProperties());
    assertEquals("/embedded-test/data.h5", reader.getPath());
  }

  @Test
  void returnsNullForAbsentContents() {
    final HDF5EmbeddedContentReader reader = reader(false, false);
    assertNull(reader.getCommand());
    assertNull(reader.getProperties());
    assertEquals("/embedded-test/data.h5", reader.getPath());
  }

  @Test
  void returnsNullForTheOtherAttribute() {
    final HDF5EmbeddedContentReader reader = reader(true, false);
    assertEquals("print x;", reader.getCommand());
    assertNull(reader.getProperties());
  }
}
