package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.ATTR_NAME_SAMURAI_GRAPH_COMMAND;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.ATTR_NAME_SAMURAI_GRAPH_PROPERTIES;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;

/** Reads the embedded samurai-graph contents of a NetCDF file. */
final class NetCDFEmbeddedContentReader implements SGEmbeddedContentReader {

  private final NetcdfFile ncFile;

  NetCDFEmbeddedContentReader(final NetcdfFile ncFile) {
    super();
    this.ncFile = ncFile;
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
    final Attribute attr = this.ncFile.findGlobalAttribute(attributeName);
    if (attr != null) {
      return attr.getStringValue();
    }
    return null;
  }

  @Override
  public String getPath() {
    return this.ncFile.getLocation();
  }
}
