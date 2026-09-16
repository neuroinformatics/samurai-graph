package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.ATTR_NAME_SAMURAI_GRAPH_COMMAND;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.ATTR_NAME_SAMURAI_GRAPH_PROPERTIES;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;

/** Reads the embedded samurai-graph contents of an HDF5 file. */
final class HDF5EmbeddedContentReader implements SGEmbeddedContentReader {

  private final IHDF5Reader reader;

  HDF5EmbeddedContentReader(final IHDF5Reader reader) {
    super();
    this.reader = reader;
  }

  @Override
  public String getCommand() {
    return this.getStringValue(ATTR_NAME_SAMURAI_GRAPH_COMMAND);
  }

  @Override
  public String getProperties() {
    return this.getStringValue(ATTR_NAME_SAMURAI_GRAPH_PROPERTIES);
  }

  private String getStringValue(final String attributeName) {
    if (!this.reader.object().hasAttribute("/", attributeName)) {
      return null;
    }
    return this.reader.string().getAttr("/", attributeName);
  }

  @Override
  public String getPath() {
    return this.reader.file().getFile().getPath();
  }
}
