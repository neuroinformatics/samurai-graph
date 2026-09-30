package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Unit tests of the property map building of the selectable paint. */
class SGSelectablePaintGetPropertiesTest {

  private static Map<SGSelectablePaint.COMMAND_KEYS, String> fullKeyMap() {
    final Map<SGSelectablePaint.COMMAND_KEYS, String> keyMap =
        new EnumMap<SGSelectablePaint.COMMAND_KEYS, String>(SGSelectablePaint.COMMAND_KEYS.class);
    keyMap.put(SGSelectablePaint.COMMAND_KEYS.PAINT_STYLE, "style");
    keyMap.put(SGSelectablePaint.COMMAND_KEYS.FILL_COLOR, "fill");
    keyMap.put(SGSelectablePaint.COMMAND_KEYS.PATTERN_COLOR, "pcolor");
    keyMap.put(SGSelectablePaint.COMMAND_KEYS.PATTERN_TYPE, "ptype");
    keyMap.put(SGSelectablePaint.COMMAND_KEYS.GRADATION_COLOR1, "gcolor1");
    keyMap.put(SGSelectablePaint.COMMAND_KEYS.GRADATION_COLOR2, "gcolor2");
    keyMap.put(SGSelectablePaint.COMMAND_KEYS.GRADATION_DIRECTION, "direction");
    keyMap.put(SGSelectablePaint.COMMAND_KEYS.GRADATION_ORDER, "order");
    keyMap.put(SGSelectablePaint.COMMAND_KEYS.TRANSPARENCY, "transparency");
    return keyMap;
  }

  @Test
  void getPropertiesRejectsANullKeyMap() {
    final SGSelectablePaint paint = new SGSelectablePaint();
    assertFalse(paint.getProperties(new SGPropertyMap(), null));
  }

  @Test
  void getPropertiesFillsEveryEntry() {
    final SGSelectablePaint paint = new SGSelectablePaint();
    paint.setFillColor(Color.RED);
    paint.setPatternColor(Color.BLUE);

    final SGPropertyMap map = new SGPropertyMap();
    assertTrue(paint.getProperties(map, fullKeyMap()));

    assertEquals(9, map.getKeys().size());
    assertEquals("Fill", map.getValue("style"));
    assertEquals("(255,0,0)", map.getValue("fill"));
    assertEquals("(0,0,255)", map.getValue("pcolor"));
  }

  @Test
  void getPropertiesReflectsThePatternAndGradationSettings() {
    final SGSelectablePaint paint = new SGSelectablePaint();
    paint.setPatternIndex(1);
    final SGGradationPaint gradation = new SGGradationPaint();
    gradation.setColors(new Color[] {Color.GREEN, Color.MAGENTA});
    paint.setGradationPaint(gradation);

    final SGPropertyMap map = new SGPropertyMap();
    assertTrue(paint.getProperties(map, fullKeyMap()));

    assertEquals(
        SGPatternPaint.getTypeName(paint.getPatternPaint().getTypeIndex()), map.getValue("ptype"));
    assertEquals("(0,255,0)", map.getValue("gcolor1"));
    assertEquals("(255,0,255)", map.getValue("gcolor2"));
  }
}
