/*
 * Copyright (C) 2026 Yoshihiro OKUMURA and the Samurai Graph developers.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

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
