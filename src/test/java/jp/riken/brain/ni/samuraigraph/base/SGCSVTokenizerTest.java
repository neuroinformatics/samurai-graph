package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGCSVTokenizer}. */
class SGCSVTokenizerTest {

  @Test
  void csvModeParsesCommaSeparatedTokens() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer("a,b,c,d", false);

    assertTrue(tokenizer.hasMoreTokens());
    assertEquals("a", tokenizer.nextToken().getString());
    assertEquals("b", tokenizer.nextToken().getString());
    assertEquals("c", tokenizer.nextToken().getString());
    assertEquals("d", tokenizer.nextToken().getString());
    assertFalse(tokenizer.hasMoreTokens());
  }

  @Test
  void whitespaceModeParsesSpaceSeparatedTokens() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer("a b c d", false);

    assertTrue(tokenizer.hasMoreTokens());
    assertEquals("a", tokenizer.nextToken().getString());
    assertEquals("b", tokenizer.nextToken().getString());
    assertEquals("c", tokenizer.nextToken().getString());
    assertEquals("d", tokenizer.nextToken().getString());
    assertFalse(tokenizer.hasMoreTokens());
  }

  @Test
  void csvModeDetectsCommaSeparatedInput() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer("1,2,3", false);
    assertEquals(3, countTokens(tokenizer));
  }

  @Test
  void whitespaceModeDetectsNoCommas() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer("1 2 3", false);
    assertEquals(3, countTokens(tokenizer));
  }

  @Test
  void quotedFieldInCsvMode() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer("\"hello, world\",foo", false);

    assertTrue(tokenizer.hasMoreTokens());
    SGCSVTokenizer.Token token1 = tokenizer.nextToken();
    assertEquals("hello, world", token1.getString());
    assertTrue(token1.isDoubleQuoted());

    SGCSVTokenizer.Token token2 = tokenizer.nextToken();
    assertEquals("foo", token2.getString());
    assertFalse(tokenizer.hasMoreTokens());
  }

  @Test
  void quotedFieldPreservesDoubleQuote() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer("\"say \"\"hi\"\"\"", false);

    assertTrue(tokenizer.hasMoreTokens());
    SGCSVTokenizer.Token token = tokenizer.nextToken();
    assertEquals("say \"hi\"", token.getString());
    assertTrue(token.isDoubleQuoted());
  }

  @Test
  void commentLineInDataFileMode() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer("# this is a comment", true);

    assertFalse(tokenizer.hasMoreTokens());
  }

  @Test
  void nonCommentLineInDataFileMode() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer("a,b", true);

    assertTrue(tokenizer.hasMoreTokens());
    assertEquals("a", tokenizer.nextToken().getString());
    assertEquals("b", tokenizer.nextToken().getString());
  }

  @Test
  void commentLineInNonDataFileMode() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer("# not a comment", false);

    assertTrue(tokenizer.hasMoreTokens());
  }

  @Test
  void hasNextDelegatesToHasMoreTokens() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer("a,b", false);

    assertTrue(tokenizer.hasNext());
    tokenizer.next();
    assertTrue(tokenizer.hasNext());
    tokenizer.next();
    assertFalse(tokenizer.hasNext());
  }

  @Test
  void nextThrowsWhenNoTokens() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer("", false);

    assertThrows(NoSuchElementException.class, tokenizer::nextToken);
  }

  @Test
  void tokenToString() {
    SGCSVTokenizer.Token token = new SGCSVTokenizer.Token("hello", false);
    assertEquals("hello", token.toString());
  }

  @Test
  void tokenIsDoubleQuoted() {
    SGCSVTokenizer.Token quoted = new SGCSVTokenizer.Token("a", true);
    SGCSVTokenizer.Token unquoted = new SGCSVTokenizer.Token("a", false);

    assertTrue(quoted.isDoubleQuoted());
    assertFalse(unquoted.isDoubleQuoted());
  }

  @Test
  void constructorRejectsNullString() {
    assertThrows(IllegalArgumentException.class, () -> new SGCSVTokenizer((String) null, false));
  }

  @Test
  void tokenizerFromReader() throws Exception {
    try (StringReader reader = new StringReader("x,y,z")) {
      SGCSVTokenizer tokenizer = new SGCSVTokenizer(reader, false);

      assertEquals("x", tokenizer.nextToken().getString());
      assertEquals("y", tokenizer.nextToken().getString());
      assertEquals("z", tokenizer.nextToken().getString());
      assertFalse(tokenizer.hasMoreTokens());
    }
  }

  @Test
  void tokenizerFromEmptyReader() throws Exception {
    try (StringReader reader = new StringReader("")) {
      SGCSVTokenizer tokenizer = new SGCSVTokenizer(reader, false);
      assertFalse(tokenizer.hasMoreTokens());
    }
  }

  @Test
  void hasMoreElementsAndNextElement() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer("a,b", false);

    assertTrue(tokenizer.hasMoreElements());
    assertNotNull(tokenizer.nextElement());
    assertTrue(tokenizer.hasMoreElements());
    assertNotNull(tokenizer.nextElement());
    assertFalse(tokenizer.hasMoreElements());
  }

  @Test
  void whitespaceModeWithQuotedField() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer("foo \"bar baz\" qux", false);

    assertEquals("foo", tokenizer.nextToken().getString());
    SGCSVTokenizer.Token quotedToken = tokenizer.nextToken();
    assertEquals("bar baz", quotedToken.getString());
    assertTrue(quotedToken.isDoubleQuoted());
    assertEquals("qux", tokenizer.nextToken().getString());
    assertFalse(tokenizer.hasMoreTokens());
  }

  @Test
  void csvModeTrimsUnquotedTokens() {
    SGCSVTokenizer tokenizer = new SGCSVTokenizer(" a , b , c ", false);

    assertEquals("a", tokenizer.nextToken().getString());
    assertEquals("b", tokenizer.nextToken().getString());
    assertEquals("c", tokenizer.nextToken().getString());
  }

  private int countTokens(SGCSVTokenizer tokenizer) {
    int count = 0;
    while (tokenizer.hasMoreTokens()) {
      tokenizer.nextToken();
      count++;
    }
    return count;
  }
}
