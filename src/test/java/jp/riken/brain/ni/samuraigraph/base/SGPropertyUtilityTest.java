package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import org.junit.jupiter.api.Test;

/** Unit tests of the property map helper. */
class SGPropertyUtilityTest {

  @Test
  void addPropertyBooleanFormatsTheFlag() {
    final SGPropertyMap map = new SGPropertyMap();
    SGPropertyUtility.addProperty(map, "flag", true);
    assertEquals("true", map.getValue("flag"));
  }

  @Test
  void addPropertyNumberFormatsTheNumber() {
    final SGPropertyMap map = new SGPropertyMap();
    SGPropertyUtility.addProperty(map, "width", 12.5f);
    assertEquals("12.5", map.getValue("width"));
  }

  @Test
  void addPropertyNumberWithUnitConcatenatesTheUnit() {
    final SGPropertyMap map = new SGPropertyMap();
    SGPropertyUtility.addProperty(map, "space", 3.0f, "mm");
    assertEquals("3.0mm", map.getValue("space"));
  }

  @Test
  void addPropertyStringStoresTheRawValue() {
    final SGPropertyMap map = new SGPropertyMap();
    SGPropertyUtility.addProperty(map, "text", "hello");
    assertEquals("hello", map.getValue("text"));
  }

  @Test
  void addQuotedStringPropertyWrapsTheValueInQuotes() {
    final SGPropertyMap map = new SGPropertyMap();
    SGPropertyUtility.addQuotedStringProperty(map, "quoted", "value");
    assertEquals("\"value\"", map.getValue("quoted"));
  }

  @Test
  void addQuotedStringPropertyWrapsANullValue() {
    final SGPropertyMap map = new SGPropertyMap();
    SGPropertyUtility.addQuotedStringProperty(map, "quoted", null);
    assertEquals("\"\"", map.getValue("quoted"));
  }

  @Test
  void addPropertyColorStoresTheColorString() {
    final SGPropertyMap map = new SGPropertyMap();
    final Color color = new Color(255, 0, 0);
    SGPropertyUtility.addProperty(map, "color", color);
    assertEquals(SGUtilityText.getColorString(color), map.getValue("color"));
  }

  @Test
  void putValueIsSilentlyIgnoredForANullMap() {
    assertDoesNotThrow(() -> SGPropertyUtility.addProperty(null, "k", "v"));
  }
}
