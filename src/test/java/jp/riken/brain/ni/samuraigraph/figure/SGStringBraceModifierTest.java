package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGStringBraceModifier}. */
class SGStringBraceModifierTest {

  private final SGStringBraceModifier modifier = new SGStringBraceModifier();

  @Test
  void plainStringIsUnchanged() {
    assertEquals("hello", this.modifier.modify("hello"));
  }

  @Test
  void subscriptAndSuperscriptAreKept() {
    assertEquals("a_{sub}b", this.modifier.modify("a_{sub}b"));
    assertEquals("A^{2}+b", this.modifier.modify("A^{2}+b"));
  }

  @Test
  void bareBracesAreEscaped() {
    assertEquals("\\{brace\\}", this.modifier.modify("{brace}"));
    assertEquals("x_{a}\\{b\\}", this.modifier.modify("x_{a}{b}"));
  }

  @Test
  void escapedBracesAreKept() {
    assertEquals("a\\{x\\}", this.modifier.modify("a\\{x\\}"));
  }

  @Test
  void leadingUnderscriptWithoutBaseFallsBackToRawString() {
    // a base string is required before a sub/superscript marker
    assertEquals("_{only\\}", this.modifier.modify("_{only}"));
  }

  @Test
  void emptyStringIsUnchanged() {
    assertEquals("", this.modifier.modify(""));
  }
}
