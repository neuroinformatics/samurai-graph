package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.event.ActionEvent;
import org.junit.jupiter.api.Test;

/** Unit tests of the check box menu item with the indeterminate state. */
class SGCheckBoxMenuItemTest {

  @Test
  void theConstructorKeepsTheTextAndSelectsNothing() {
    final SGCheckBoxMenuItem item = new SGCheckBoxMenuItem("item");
    assertEquals("item", item.getText());
    assertFalse(item.isSelected());
    assertEquals(Boolean.FALSE, item.getSelected());
  }

  @Test
  void setSelectedTogglesTheState() {
    final SGCheckBoxMenuItem item = new SGCheckBoxMenuItem("item");
    item.setSelected(Boolean.TRUE);
    assertTrue(item.isSelected());
    assertEquals(Boolean.TRUE, item.getSelected());
    assertEquals(Color.BLACK, item.getForeground());

    item.setSelected(Boolean.FALSE);
    assertFalse(item.isSelected());
    assertEquals(Boolean.FALSE, item.getSelected());
    assertEquals(Color.BLACK, item.getForeground());
  }

  @Test
  void aDisabledItemGetsTheGrayColor() {
    final SGCheckBoxMenuItem item = new SGCheckBoxMenuItem("item");
    item.setEnabled(false);
    item.setSelected(Boolean.FALSE);
    assertEquals(Color.GRAY, item.getForeground());
  }

  @Test
  void setSelectedWithNullEntersTheIndeterminateState() {
    final SGCheckBoxMenuItem item = new SGCheckBoxMenuItem("item");
    item.setSelected(Boolean.TRUE);
    item.setSelected(null);
    assertFalse(item.isSelected());
    assertNull(item.getSelected());
    assertEquals(Color.DARK_GRAY, item.getForeground());
  }

  @Test
  void anActionFromItselfClearsTheIndeterminateState() {
    final SGCheckBoxMenuItem item = new SGCheckBoxMenuItem("item");
    item.setSelected(null);
    item.actionPerformed(new ActionEvent(item, ActionEvent.ACTION_PERFORMED, ""));
    assertFalse(item.isSelected());
    assertEquals(Boolean.FALSE, item.getSelected());
    assertEquals(Color.BLACK, item.getForeground());
  }

  @Test
  void anActionFromAnotherSourceKeepsTheIndeterminateState() {
    final SGCheckBoxMenuItem item = new SGCheckBoxMenuItem("item");
    item.setSelected(null);
    item.actionPerformed(new ActionEvent(new Object(), ActionEvent.ACTION_PERFORMED, ""));
    assertNull(item.getSelected());
    assertEquals(Color.DARK_GRAY, item.getForeground());
  }
}
