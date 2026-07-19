package com.github.neuroinformatics.samurai_graph.lib.hdf5;

import java.util.List;

public interface IHDF5ObjectReader {
  List<String> getAllGroupMembers(final String groupName);

  boolean isDataSet(final String path);

  boolean isGroup(final String path);

  List<String> getGroupMemberPaths(final String path);

  List<String> getAttributeNames(final String path);

  List<String> getAllAttributeNames(final String path);

  boolean hasAttribute(final String path, final String attributeName);

  HDF5DataTypeInformation getAttributeInformation(final String path, final String attributeName);
}
