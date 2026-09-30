package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests of the command string builder. */
class SGCommandUtilityTest {

  @Test
  void theCommandIncludesTheIdAndTheProperties() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("color", "(255,0,0)");
    final String command = SGCommandUtility.createCommandString("setColor", "id1", map);
    assertEquals("setColor(id1, color=(255,0,0))\n", command);
  }

  @Test
  void theCommandOmitsANullId() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("x", "1.0");
    final String command = SGCommandUtility.createCommandString("undo", null, map);
    assertEquals("undo(x=1.0)\n", command);
  }

  @Test
  void theCommandJoinsThePropertyEntries() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("x", "1.0");
    map.putValue("y", "2.0");
    final String command = SGCommandUtility.createCommandString("move", null, map);
    assertEquals("move(x=1.0, y=2.0)\n", command);
  }
}
