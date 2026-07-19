package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGStringPatterns}. */
class SGStringPatternsTest {

  @Test
  void getGreekAlpha() {
    assertEquals('\u03B1', SGStringPatterns.getChar("alpha"));
    assertEquals('\u03B1', SGStringPatterns.getChar("a"));
  }

  @Test
  void getGreekBeta() {
    assertEquals('\u03B2', SGStringPatterns.getChar("beta"));
    assertEquals('\u03B2', SGStringPatterns.getChar("b"));
  }

  @Test
  void getGreekGamma() {
    assertEquals('\u0393', SGStringPatterns.getChar("Gamma"));
    assertEquals('\u03B3', SGStringPatterns.getChar("gamma"));
  }

  @Test
  void getGreekPi() {
    assertEquals('\u03A0', SGStringPatterns.getChar("Pi"));
    assertEquals('\u03C0', SGStringPatterns.getChar("pi"));
  }

  @Test
  void getGreekOmega() {
    assertEquals('\u03A9', SGStringPatterns.getChar("Omega"));
    assertEquals('\u03C9', SGStringPatterns.getChar("omega"));
  }

  @Test
  void getMathematicalSymbols() {
    assertEquals('\u221E', SGStringPatterns.getChar("infty"));
    assertEquals('\u2264', SGStringPatterns.getChar("leq"));
    assertEquals('\u2265', SGStringPatterns.getChar("geq"));
    assertEquals('\u00B1', SGStringPatterns.getChar("pm"));
    assertEquals('\u00D7', SGStringPatterns.getChar("times"));
    assertEquals('\u00F7', SGStringPatterns.getChar("div"));
  }

  @Test
  void getArrowSymbols() {
    assertEquals('\u2192', SGStringPatterns.getChar("rightarrow"));
    assertEquals('\u2190', SGStringPatterns.getChar("leftarrow"));
    assertEquals('\u2191', SGStringPatterns.getChar("uparrow"));
    assertEquals('\u2193', SGStringPatterns.getChar("downarrow"));
    assertEquals('\u21D2', SGStringPatterns.getChar("Rightarrow"));
  }

  @Test
  void getSubsetSymbols() {
    assertEquals('\u2282', SGStringPatterns.getChar("subset"));
    assertEquals('\u2286', SGStringPatterns.getChar("subseteq"));
    assertEquals('\u2283', SGStringPatterns.getChar("supset"));
    assertEquals('\u2287', SGStringPatterns.getChar("supseteq"));
  }

  @Test
  void getLogicalSymbols() {
    assertEquals('\u2200', SGStringPatterns.getChar("forall"));
    assertEquals('\u2203', SGStringPatterns.getChar("exists"));
  }

  @Test
  void getDegreeSymbol() {
    assertEquals('\u00B0', SGStringPatterns.getChar("degree"));
  }

  @Test
  void getPartialAndIntegral() {
    assertEquals('\u2202', SGStringPatterns.getChar("partial"));
    assertEquals('\u222B', SGStringPatterns.getChar("int"));
  }

  @Test
  void getNonExistentPatternReturnsNull() {
    assertNull(SGStringPatterns.getChar("nonexistent"));
  }

  @Test
  void getCeilingAndFloor() {
    assertEquals('\u230A', SGStringPatterns.getChar("lfloor"));
    assertEquals('\u230B', SGStringPatterns.getChar("rfloor"));
    assertEquals('\u2308', SGStringPatterns.getChar("lceil"));
    assertEquals('\u2309', SGStringPatterns.getChar("rceil"));
  }

  @Test
  void getEquivalenceSymbols() {
    assertEquals('\u2261', SGStringPatterns.getChar("equiv"));
    assertEquals('\u2248', SGStringPatterns.getChar("approx"));
    assertEquals('\u2245', SGStringPatterns.getChar("cong"));
    assertEquals('\u2260', SGStringPatterns.getChar("neq"));
  }
}
