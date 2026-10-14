/*
 * Copyright (C) 2026 Yoshihiro OKUMURA and the Samurai Graph developers.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGConstants.FONT_SIZE_MINIMAL_ORDER;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.MENUCMD_ARRANGE;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.MENUCMD_BRING_FORWARD;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.MENUCMD_BRING_TO_FRONT;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.MENUCMD_SEND_BACKWARD;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.MENUCMD_SEND_TO_BACK;

import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDateStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDateValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisValue;
import jp.riken.brain.ni.samuraigraph.base.SGCheckBoxMenuItem;
import jp.riken.brain.ni.samuraigraph.base.SGFigureElementAxisConstants;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyFileConstants;
import jp.riken.brain.ni.samuraigraph.base.SGSpinner;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Static helpers for the figure layer that do not belong to the base utility class. */
public final class SGFigureUtility {

  private SGFigureUtility() {}

  /**
   * Adds menu items to arrange objects.
   *
   * @param p the pop up menu
   * @param l the action listener
   */
  public static void addArrangeItems(JPopupMenu p, ActionListener l) {
    JMenu arrangeMenu = SGUtility.addMenu(p, l, MENUCMD_ARRANGE);
    SGUtility.addItem(arrangeMenu, l, MENUCMD_BRING_TO_FRONT);
    SGUtility.addItem(arrangeMenu, l, MENUCMD_BRING_FORWARD);
    SGUtility.addItem(arrangeMenu, l, MENUCMD_SEND_BACKWARD);
    SGUtility.addItem(arrangeMenu, l, MENUCMD_SEND_TO_BACK);
  }

  /**
   * Finds a right font name from the system with a given font name.
   *
   * @param name a font name
   * @return found font family name
   */
  public static String findFontFamilyName(final String name) {
    if (name == null) {
      return null;
    }
    String fName = null;
    String[] names = SGUtility.getAvailableFontFamilyNames();
    for (int ii = 0; ii < names.length; ii++) {
      if (SGUtilityText.isEqualString(names[ii], name)) {
        fName = names[ii];
        break;
      }
    }
    return fName;
  }

  /**
   * Parses a string into an axis value.
   *
   * @param str a value string
   * @param dateMode whether to parse as a date value
   * @return the parsed axis value, or null for an empty input
   * @throws ParseException if the input is invalid
   */
  public static SGAxisValue getAxisValue(final String str, final boolean dateMode)
      throws ParseException {
    if (str == null) {
      return null;
    }
    if (str.length() == 0) {
      return null;
    }

    if (dateMode) {
      // parses as a date value
      SGAxisDateValue dateValue = null;
      try {
        dateValue = new SGAxisDateValue(str);
      } catch (ParseException e) {
        throw e;
      }
      return dateValue;
    } else {
      // parses as a double value
      Double d = SGUtilityText.getDouble(str);
      if (d == null) {
        throw new ParseException("Invalid input: " + d, 0);
      }
      return new SGAxisDoubleValue(d);
    }
  }

  /**
   * Parses a string into an axis step value.
   *
   * @param str a step value string
   * @param dateMode whether to parse as a date value
   * @return the parsed axis step value, or null for an empty input
   * @throws ParseException if the input is invalid
   */
  public static SGAxisStepValue getAxisStepValue(final String str, final boolean dateMode)
      throws ParseException {
    if (str == null) {
      return null;
    }
    if (str.length() == 0) {
      return null;
    }

    if (dateMode) {
      // parses as a date value
      SGAxisDateStepValue p = null;
      try {
        p = new SGAxisDateStepValue(str);
      } catch (ParseException e) {
        throw e;
      }
      return p;
    } else {
      // parses as a double value
      Double d = SGUtilityText.getDouble(str);
      if (d == null) {
        throw new ParseException("Invalid input: " + d, 0);
      }
      return new SGAxisDoubleStepValue(d);
    }
  }

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

  /**
   * Adds a menu item with a check box to a pop-up menu.
   *
   * @param p the pop-up menu
   * @param l the action listener
   * @param text a text string
   * @param cmd a command to be added
   * @param enabled a flag whether the command is enabled
   * @return the added menu item
   */
  public static JCheckBoxMenuItem addCheckBoxItem(
      JPopupMenu p, ActionListener l, String text, String cmd, boolean enabled) {
    final SGCheckBoxMenuItem item = new SGCheckBoxMenuItem(text);
    item.setActionCommand(cmd);
    p.add(item);
    item.addActionListener(l);
    item.setEnabled(enabled);
    return item;
  }

  public static JCheckBoxMenuItem addCheckBoxItem(JPopupMenu menu, ActionListener l, String cmd) {
    return addCheckBoxItem(menu, l, cmd, cmd, true);
  }

  /**
   * Adds a menu command with the check box to the menu.
   *
   * @param text the text parameter
   * @param parent the parent menu item
   * @param l an action listener of the command
   * @param cmd a command to be added
   * @param enabled a flag whether the command is enabled
   * @return the added menu item
   */
  public static JCheckBoxMenuItem addCheckBoxItem(
      JMenuItem parent, ActionListener l, String text, String cmd, boolean enabled) {
    final SGCheckBoxMenuItem item = new SGCheckBoxMenuItem(text);
    parent.add(item);
    item.setActionCommand(cmd);
    item.addActionListener(l);
    item.setEnabled(enabled);
    return item;
  }

  public static JCheckBoxMenuItem addCheckBoxItem(JMenuItem parent, ActionListener l, String cmd) {
    return addCheckBoxItem(parent, l, cmd, cmd, true);
  }

  /**
   * @param obj
   * @return
   * @param sp the sp parameter
   */
  public static boolean setIntValue(final SGSpinner sp, final Object obj) {
    if (obj == null) {
      sp.setText("");
      return false;
    }

    String str = obj.toString().trim();
    Integer n = SGUtilityText.getInteger(str);
    if (n == null) {
      return false;
    }
    sp.setText(str);
    sp.setValue(Double.valueOf(n.doubleValue()));

    try {
      sp.commitEditByDefault();
    } catch (ParseException e) {
      // keep the spinner value unchanged
    }

    return true;
  }

  /** Moves an object to the head or tail of a list. */
  public static boolean moveObjectToRawList(
      final Object obj, final List<?> list, final boolean toTail) {
    final int index = list.indexOf(obj);
    if (index == -1) {
      return false;
    }
    @SuppressWarnings("unchecked")
    List<Object> rawList = (List<Object>) list;
    rawList.remove(obj);
    if (toTail) {
      rawList.add(obj);
    } else {
      rawList.add(0, obj);
    }
    return true;
  }

  /** Moves objects from one list to another. */
  public static boolean moveObjectRawList(
      final List<?> movedList, final List<?> list, final int num) {
    @SuppressWarnings("unchecked")
    final List<Object> rawList = (List<Object>) list;
    if (num > 0) {
      for (int ii = movedList.size() - 1; ii >= 0; ii--) {
        Object obj = movedList.get(ii);
        final int index = list.indexOf(obj);
        if (index == -1) {
          return false;
        }
        final int indexNew = index + num;
        if (indexNew > list.size() - 1) {
          continue;
        }
        rawList.remove(obj);
        rawList.add(indexNew, obj);
      }
    } else {
      for (int ii = 0; ii < movedList.size(); ii++) {
        Object obj = movedList.get(ii);
        final int index = list.indexOf(obj);
        if (index == -1) {
          return false;
        }
        final int indexNew = index + num;
        if (indexNew < 0) {
          continue;
        }
        rawList.remove(obj);
        rawList.add(indexNew, obj);
      }
    }
    return true;
  }

  /**
   * Finds and returns the version number in a property file for given element.
   *
   * @param el an element
   * @return a text string of the version number or null if the version number does not exist
   */
  public static String getVersionNumber(Element el) {
    Element property = null;
    Element cur = el;
    while (true) {
      Node node = cur.getParentNode();
      if (node == null) {
        property = cur;
        break;
      }
      if (!(node instanceof Element)) {
        property = cur;
        break;
      }
      cur = (Element) node;
    }
    if (property == null) {
      return null;
    }
    String version = property.getAttribute(SGPropertyFileConstants.KEY_VERSION_NUMBER);
    return version;
  }

  /**
   * Calculates and returns font size to export from given font size.
   *
   * @param size font size in units of FONT_SIZE_UNIT
   * @return font size to export
   */
  public static float getExportFontSize(final float size) {
    final int digit = FONT_SIZE_MINIMAL_ORDER - 1;
    return (float) SGUtilityNumber.roundOffNumber(size, digit);
  }
}
