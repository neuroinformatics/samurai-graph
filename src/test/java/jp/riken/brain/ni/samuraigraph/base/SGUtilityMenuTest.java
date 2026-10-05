package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the menu-building helpers of {@link SGUtility}. The helpers only build menu
 * structures (no top-level window is shown), so the tests are safe in a headless environment.
 */
class SGUtilityMenuTest {

  private static final ActionListener NO_OP = event -> {};

  // -- addItem ----------------------------------------------------------------

  @Test
  void addItemAppendsConfiguredItemToPopup() {
    JPopupMenu menu = new JPopupMenu();
    JMenuItem item = SGUtility.addItem(menu, NO_OP, "Label", "cmd", true);
    assertSame(item, menu.getComponent(0));
    assertEquals("Label", item.getText());
    assertEquals("cmd", item.getActionCommand());
    assertTrue(item.isEnabled());
    assertTrue(item.getListeners(ActionListener.class).length > 0);
  }

  @Test
  void addItemUsesCommandAsTextAndEnablesByDefault() {
    JPopupMenu menu = new JPopupMenu();
    JMenuItem item = SGUtility.addItem(menu, NO_OP, "cmd");
    assertEquals("cmd", item.getText());
    assertEquals("cmd", item.getActionCommand());
    assertTrue(item.isEnabled());
  }

  @Test
  void addItemHonorsEnabledFlag() {
    JPopupMenu menu = new JPopupMenu();
    JMenuItem item = SGUtility.addItem(menu, NO_OP, "Label", "cmd", false);
    assertFalse(item.isEnabled());
  }

  @Test
  void addItemAppendsItemToParentMenuItem() {
    JMenuItem parent = new JMenuItem("Parent");
    JMenuItem item = SGUtility.addItem(parent, NO_OP, "Label", "cmd", true);
    assertEquals("cmd", item.getActionCommand());
    assertTrue(parent.isAncestorOf(item));
  }

  // -- addCheckBoxItem --------------------------------------------------------

  @Test
  void addCheckBoxItemAppendsConfiguredCheckBoxToPopup() {
    JPopupMenu menu = new JPopupMenu();
    JCheckBoxMenuItem item = SGUtility.addCheckBoxItem(menu, NO_OP, "CB", "cbcmd", true);
    assertTrue(item instanceof SGCheckBoxMenuItem);
    assertEquals("CB", item.getText());
    assertEquals("cbcmd", item.getActionCommand());
    assertTrue(item.isEnabled());
  }

  @Test
  void addCheckBoxItemUsesCommandAsTextAndEnablesByDefault() {
    JPopupMenu menu = new JPopupMenu();
    JCheckBoxMenuItem item = SGUtility.addCheckBoxItem(menu, NO_OP, "cbcmd");
    assertEquals("cbcmd", item.getText());
    assertEquals("cbcmd", item.getActionCommand());
    assertTrue(item.isEnabled());
  }

  @Test
  void addCheckBoxItemAppendsItemToParentMenuItem() {
    JMenuItem parent = new JMenuItem("Parent");
    JCheckBoxMenuItem item = SGUtility.addCheckBoxItem(parent, NO_OP, "CB", "cbcmd", true);
    assertEquals("cbcmd", item.getActionCommand());
    assertTrue(parent.isAncestorOf(item));
  }

  // -- addMenu ----------------------------------------------------------------

  @Test
  void addMenuAppendsConfiguredMenuToPopup() {
    JPopupMenu popup = new JPopupMenu();
    JMenu menu = SGUtility.addMenu(popup, NO_OP, "Sub", true);
    assertEquals("Sub", menu.getText());
    assertTrue(menu.isEnabled());
  }

  @Test
  void addMenuEnablesByDefault() {
    JPopupMenu popup = new JPopupMenu();
    JMenu menu = SGUtility.addMenu(popup, NO_OP, "Sub");
    assertTrue(menu.isEnabled());
  }

  // -- findMenuItem / findMenu ------------------------------------------------

  @Test
  void findMenuItemReturnsDirectChild() {
    JMenu menu = new JMenu("Root");
    JMenuItem alpha = new JMenuItem("Alpha");
    JMenuItem beta = new JMenuItem("Beta");
    menu.add(alpha);
    menu.add(beta);
    assertSame(alpha, SGUtility.findMenuItem(menu, "Alpha"));
    assertSame(beta, SGUtility.findMenuItem(menu, "Beta"));
    assertNull(SGUtility.findMenuItem(menu, "Gamma"));
  }

  @Test
  void findMenuItemSearchesNestedSubMenus() {
    JMenu root = new JMenu("Root");
    JMenu sub = new JMenu("Sub");
    JMenuItem deep = new JMenuItem("Deep");
    sub.add(deep);
    root.add(sub);
    assertSame(deep, SGUtility.findMenuItem(root, "Deep"));
    assertNull(SGUtility.findMenuItem(root, "Missing"));
  }

  @Test
  void findMenuItemReturnsNullForEmptyMenuAndNonMatchingSubMenus() {
    JMenu empty = new JMenu("Empty");
    assertNull(SGUtility.findMenuItem(empty, "Anything"));

    JMenu root = new JMenu("Root");
    JMenu sub = new JMenu("Sub");
    sub.add(new JMenuItem("NotIt"));
    root.add(sub);
    assertNull(SGUtility.findMenuItem(root, "Target"));
  }

  @Test
  void findMenuReturnsTopLevelMenuByName() {
    JPopupMenu popup = new JPopupMenu();
    JMenu wanted = new JMenu("Wanted");
    JMenu other = new JMenu("Other");
    popup.add(new JMenuItem("Item"));
    popup.add(wanted);
    popup.add(other);
    assertSame(wanted, SGUtility.findMenu(popup, "Wanted"));
    assertNull(SGUtility.findMenu(popup, "Absent"));
  }
}
