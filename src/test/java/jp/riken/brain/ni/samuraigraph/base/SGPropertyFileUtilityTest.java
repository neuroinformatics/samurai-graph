package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Unit tests of the property file attribute setter. */
class SGPropertyFileUtilityTest {

  private Element element;

  @BeforeEach
  void createDocument() throws Exception {
    final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    final Document document = builder.newDocument();
    this.element = document.createElement("test");
  }

  @Test
  void theStringAttributeIsSet() {
    SGPropertyFileUtility.setAttribute(this.element, "key", "value");
    assertEquals("value", this.element.getAttribute("key"));
  }

  @Test
  void theColorAttributeIsSet() {
    SGPropertyFileUtility.setAttribute(this.element, "color", new Color(255, 128, 0));
    assertEquals("(255,128,0)", this.element.getAttribute("color"));
  }

  @Test
  void theBooleanAttributeIsSet() {
    SGPropertyFileUtility.setAttribute(this.element, "flag", true);
    assertEquals("true", this.element.getAttribute("flag"));
  }

  @Test
  void theNumberAttributeIsSet() {
    SGPropertyFileUtility.setAttribute(this.element, "width", 10);
    assertEquals("10", this.element.getAttribute("width"));
  }

  @Test
  void theNumberAttributeAppendsTheUnit() {
    SGPropertyFileUtility.setAttribute(this.element, "width", 10.5, "px");
    assertEquals("10.5px", this.element.getAttribute("width"));
  }
}
