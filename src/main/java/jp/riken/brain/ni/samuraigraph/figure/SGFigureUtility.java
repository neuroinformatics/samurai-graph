/*
 * Copyright (C) 2026 Yoshihiro OKUMURA and the Samurai Graph developers.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

package jp.riken.brain.ni.samuraigraph.figure;

import jp.riken.brain.ni.samuraigraph.base.SGFigureElementAxisConstants;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/** Static helpers for the figure layer that do not belong to the base utility class. */
public final class SGFigureUtility {

  private SGFigureUtility() {}

  /**
   * Returns an integer value for an axis location from a string.
   *
   * @param location the axis location name
   * @return the axis location id, or -1 if the name is not recognized
   */
  public static int getAxisLocation(final String location) {
    int loc = -1;
    if (SGUtilityText.isEqualString(SGFigureElementAxisConstants.AXIS_BOTTOM, location)) {
      loc = SGFigureElementAxisConstants.AXIS_HORIZONTAL_1;
    } else if (SGUtilityText.isEqualString(SGFigureElementAxisConstants.AXIS_TOP, location)) {
      loc = SGFigureElementAxisConstants.AXIS_HORIZONTAL_2;
    } else if (SGUtilityText.isEqualString(SGFigureElementAxisConstants.AXIS_LEFT, location)) {
      loc = SGFigureElementAxisConstants.AXIS_VERTICAL_1;
    } else if (SGUtilityText.isEqualString(SGFigureElementAxisConstants.AXIS_RIGHT, location)) {
      loc = SGFigureElementAxisConstants.AXIS_VERTICAL_2;
    }
    return loc;
  }

  /**
   * Returns the location name of a given location ID.
   *
   * @param id a location ID
   * @return the location name
   */
  public static String getLocationName(final int id) {
    String name = null;
    switch (id) {
      case SGFigureElementAxisConstants.AXIS_HORIZONTAL_1:
        name = SGFigureElementAxisConstants.AXIS_BOTTOM;
        break;
      case SGFigureElementAxisConstants.AXIS_HORIZONTAL_2:
        name = SGFigureElementAxisConstants.AXIS_TOP;
        break;
      case SGFigureElementAxisConstants.AXIS_VERTICAL_1:
        name = SGFigureElementAxisConstants.AXIS_LEFT;
        break;
      case SGFigureElementAxisConstants.AXIS_VERTICAL_2:
        name = SGFigureElementAxisConstants.AXIS_RIGHT;
        break;
      case SGFigureElementAxisConstants.AXIS_NORMAL:
        name = SGFigureElementAxisConstants.AXIS_COLOR_BAR;
        break;
    }
    return name;
  }

  /**
   * Returns the location ID of a given location name.
   *
   * @param str a location name
   * @return the location ID
   */
  public static int getLocationInPlane(final String str) {
    int loc = -1;
    if (SGUtilityText.isEqualString(SGFigureElementAxisConstants.AXIS_BOTTOM, str)) {
      loc = SGFigureElementAxisConstants.AXIS_HORIZONTAL_1;
    } else if (SGUtilityText.isEqualString(SGFigureElementAxisConstants.AXIS_TOP, str)) {
      loc = SGFigureElementAxisConstants.AXIS_HORIZONTAL_2;
    } else if (SGUtilityText.isEqualString(SGFigureElementAxisConstants.AXIS_LEFT, str)) {
      loc = SGFigureElementAxisConstants.AXIS_VERTICAL_1;
    } else if (SGUtilityText.isEqualString(SGFigureElementAxisConstants.AXIS_RIGHT, str)) {
      loc = SGFigureElementAxisConstants.AXIS_VERTICAL_2;
    } else if (SGUtilityText.isEqualString(SGFigureElementAxisConstants.AXIS_COLOR_BAR, str)) {
      loc = SGFigureElementAxisConstants.AXIS_NORMAL;
    }
    return loc;
  }
}
