/*
 * Copyright (C) 2026 Yoshihiro OKUMURA and the Samurai Graph developers.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

package jp.riken.brain.ni.samuraigraph.base;

import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;

import java.util.ArrayList;

/** Static helper for rendering the SGDrawingWindow node tree. */
final class SGDrawingWindowTreeUtility {

  private SGDrawingWindowTreeUtility() {}

  /**
   * Creates a text representation of a node tree.
   *
   * @param node the root node
   * @param sb a string builder to append to
   * @return true if succeeded
   */
  static boolean createTree(final SGINode node, final StringBuilder sb) {
    createTree(node, sb, 0);
    return true;
  }

  private static void createTree(final SGINode node, final StringBuilder sb, final int depth) {
    final ArrayList<SGINode> childList = node.getChildNodes();
    final String cText = node.getClassDescription();
    final String iText = node.getInstanceDescription();
    final boolean pFlag = (node instanceof SGIPropertyDialogObserver);

    // has child objects
    if (childList.size() != 0) {
      if (cText != null && iText != null) {
        append(sb, iText, depth);

        final int d = depth + 1;

        // add child
        for (int ii = 0; ii < childList.size(); ii++) {
          SGINode child = childList.get(ii);
          createTree(child, sb, d);
        }
      }
    } else {
      if (iText != null) {
        // property dialog observer
        if (pFlag) {
          append(sb, iText, depth);
        }
      }
    }
  }

  private static void append(final StringBuilder sb, final String str, final int depth) {
    final String s = "  ";
    for (int ii = 0; ii < depth; ii++) {
      sb.append(s);
    }
    sb.append(str);
    sb.append(LINE_SEPARATOR);
  }
}
