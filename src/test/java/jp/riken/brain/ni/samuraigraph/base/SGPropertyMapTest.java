package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Unit tests of the property map. */
class SGPropertyMapTest {

  @Test
  void getIsCaseInsensitiveAndKeysAreUppercased() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("Alpha", "1");
    assertEquals("1", map.getValue("alpha"));
    assertEquals("1", map.getValue("ALPHA"));
    assertEquals(List.of("ALPHA"), map.getKeys());
  }

  @Test
  void putValueKeepsInsertionOrderAndMovesOnRePut() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("a", "1");
    map.putValue("b", "2");
    map.putValue("a", "3");
    assertEquals(List.of("B", "A"), map.getKeys());
  }

  @Test
  void putValueReturnsThePreviousValue() {
    final SGPropertyMap map = new SGPropertyMap();
    assertNull(map.putValue("key", "1"));
    assertEquals("1", map.putValue("key", "2"));
  }

  @Test
  void getValueStringTrimsAndUnwrapsDoubleQuotes() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("k", "  \"v\"  ");
    assertEquals("v", map.getValueString("k"));
  }

  @Test
  void getValueStringKeepsThePlainValue() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("k", "plain");
    assertEquals("plain", map.getValueString("k"));
  }

  @Test
  void getValueStringIsEmptyForMissingValue() {
    final SGPropertyMap map = new SGPropertyMap();
    assertEquals("", map.getValueString("missing"));
  }

  @Test
  void isDoubleQuotedReflectsTheStoredValue() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("q", "\"quoted\"");
    map.putValue("p", "plain");
    map.putValue("two", "\"\"");
    map.putValue("one", "\"");
    assertTrue(map.isDoubleQuoted("q"));
    assertTrue(map.isDoubleQuoted("two"));
    assertFalse(map.isDoubleQuoted("p"));
    assertFalse(map.isDoubleQuoted("one"));
    assertFalse(map.isDoubleQuoted("missing"));
  }

  @Test
  void removeValueRemovesTheEntry() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("k", "1");
    assertEquals("1", map.removeValue("K"));
    assertNull(map.getValue("k"));
    assertTrue(map.getKeys().isEmpty());
  }

  @Test
  void cloneIsDeepCopy() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("k", "1");
    final SGPropertyMap copy = (SGPropertyMap) map.clone();
    copy.putValue("k", "2");
    copy.putValue("new", "x");
    assertEquals("1", map.getValue("k"));
    assertTrue(map.getKeys().contains("K"));
    assertFalse(map.getKeys().contains("NEW"));
  }

  @Test
  void toPropertiesContainsUppercasedKeysAndValues() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("a", "1");
    map.putValue("b", "2");
    final Properties props = map.toProperties();
    assertEquals("1", props.getProperty("A"));
    assertEquals("2", props.getProperty("B"));
  }

  @Test
  void toStringUsesOriginalNamesInInsertionOrder() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("Alpha", "1");
    map.putValue("Beta", "2");
    assertEquals("Alpha=1, Beta=2", map.toString());
  }

  @Test
  void getOriginalKeyReturnsTheInsertedCasing() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("foO", "1");
    assertEquals("foO", map.getOriginalKey("FOO"));
    assertNull(map.getOriginalKey("missing"));
  }

  @Test
  void putAllCopiesTheEntriesWithTheirOriginalKeys() {
    final SGPropertyMap source = new SGPropertyMap();
    source.putValue("Foo", "x");
    source.putValue("Bar", "y");
    final SGPropertyMap target = new SGPropertyMap();
    target.putAll(source);
    assertEquals(List.of("FOO", "BAR"), target.getKeys());
    assertEquals("x", target.getValue("FOO"));
    assertEquals("y", target.getValue("BAR"));
    assertEquals("Foo", target.getOriginalKey("FOO"));
  }

  @Test
  void setToElementSetsTheAttributesWithOriginalNames() throws Exception {
    final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    final Element el = doc.createElement("root");
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("SomeKey", "value");
    map.setToElement(el);
    assertEquals("value", el.getAttribute("SomeKey"));
  }

  @Test
  void getKeyIteratorReflectsRemovalsAndAdditionOrder() {
    final SGPropertyMap map = new SGPropertyMap();
    map.putValue("a", "1");
    map.putValue("b", "2");
    map.removeValue("a");
    map.putValue("c", "3");
    assertEquals(List.of("B", "C"), map.getKeys());
  }
}
