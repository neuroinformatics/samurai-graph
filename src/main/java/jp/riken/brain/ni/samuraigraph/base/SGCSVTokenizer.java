package jp.riken.brain.ni.samuraigraph.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

/**
 * The CSV tokenizer class allows an application to break a Comma Separated Value format string or
 * line into tokens. Tokens may be quoted (double-quoted) or unquoted. In CSV mode
 * (comma-separated), tokens are separated by commas. In non-CSV mode, tokens are separated by
 * whitespace.
 *
 * <p>A token is returned as a {@link Token} object that provides the string value and whether it
 * was originally enclosed in double quotes.
 *
 * <p>Comment lines (starting with {@code #} when {@code isDataFile} is true) are detected but yield
 * no tokens via {@link #hasMoreTokens()}.
 *
 * <p>The following is one example of the use of the tokenizer. The code:
 *
 * <blockquote>
 *
 * <pre>
 * SGCSVTokenizer tokenizer = new SGCSVTokenizer(&quot;this,is,a,test&quot;, false);
 * while (tokenizer.hasMoreTokens()) {
 *     System.out.println(tokenizer.nextToken().getString());
 * }
 * </pre>
 *
 * </blockquote>
 *
 * <p>prints the following output:
 *
 * <blockquote>
 *
 * <pre>
 *        this
 *        is
 *        a
 *        test
 * </pre>
 *
 * </blockquote>
 *
 * @see SGITextDataConstants
 */
public class SGCSVTokenizer implements Iterator<SGCSVTokenizer.Token> {

  private final boolean isCommentLine;
  private final Iterator<CSVRecord> recordIterator;
  private final List<RawToken> rawTokens;
  private int rawTokenIndex;

  /** Stores raw token information (value and whether it was double-quoted). */
  private static class RawToken {

    private final String value;
    private final boolean doubleQuoted;

    RawToken(String value, boolean doubleQuoted) {
      this.value = value;
      this.doubleQuoted = doubleQuoted;
    }

    String getValue() {
      return value;
    }

    boolean isDoubleQuoted() {
      return doubleQuoted;
    }
  }

  /**
   * Constructs a CSV tokenizer for the specified string.
   *
   * @param aString a string to be parsed.
   * @param isDataFile a data file reading flag. If true and the string starts with {@code #}, the
   *     line is treated as a comment and {@link #hasMoreTokens()} will return false.
   */
  public SGCSVTokenizer(final String aString, final boolean isDataFile) {
    if (aString == null) {
      throw new IllegalArgumentException("aString must not be null");
    }

    String trimmed = aString.trim();

    // Check for comment line
    this.isCommentLine = isDataFile && trimmed.startsWith(SGITextDataConstants.DATA_COMMENT_PREFIX);

    if (this.isCommentLine) {
      this.recordIterator = null;
      this.rawTokens = new ArrayList<>(0);
      this.rawTokenIndex = 0;
      return;
    }

    // Detect CSV mode: check if there are commas outside of quotes
    boolean isCsvMode = isCommaSeparated(trimmed);

    // Parse using Apache Commons CSV
    this.rawTokens = parseTokens(trimmed, isCsvMode);
    this.rawTokenIndex = 0;
    this.recordIterator = null;
  }

  /**
   * Constructs a CSV tokenizer for the specified reader. Reads the first line from the reader.
   *
   * @param reader a reader to read from
   * @param isDataFile a data file reading flag. If true and the line starts with {@code #}, the
   *     line is treated as a comment.
   * @throws IOException if an I/O error occurs
   */
  public SGCSVTokenizer(final Reader reader, final boolean isDataFile) throws IOException {
    try (BufferedReader br = new BufferedReader(reader)) {
      String line = br.readLine();
      if (line == null) {
        this.isCommentLine = false;
        this.rawTokens = new ArrayList<>(0);
        this.rawTokenIndex = 0;
        this.recordIterator = null;
        return;
      }

      String trimmed = line.trim();
      this.isCommentLine = isDataFile && trimmed.startsWith(SGITextDataConstants.DATA_COMMENT_PREFIX);

      if (this.isCommentLine) {
        this.recordIterator = null;
        this.rawTokens = new ArrayList<>(0);
        this.rawTokenIndex = 0;
        return;
      }

      boolean isCsvMode = isCommaSeparated(trimmed);
      this.rawTokens = parseTokens(trimmed, isCsvMode);
      this.rawTokenIndex = 0;
      this.recordIterator = null;
    }
  }

  /**
   * Checks whether the string uses comma as a separator (outside of quoted fields).
   *
   * @param record the string to check
   * @return true if the string contains commas outside of double-quoted fields
   */
  private static boolean isCommaSeparated(String record) {
    boolean inQuote = false;
    for (int i = 0; i < record.length(); i++) {
      char c = record.charAt(i);
      if (inQuote) {
        if (c == '"') {
          // Check for escaped quote ("")
          if (i + 1 < record.length() && record.charAt(i + 1) == '"') {
            i++; // Skip the next quote
          } else {
            inQuote = false;
          }
        }
      } else {
        if (c == '"') {
          inQuote = true;
        } else if (c == ',') {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Parses the string into raw tokens using Apache Commons CSV.
   *
   * @param record the string to parse
   * @param isCsvMode true if comma-separated mode, false if whitespace-delimited
   * @return a list of raw tokens
   */
  private static List<RawToken> parseTokens(String record, boolean isCsvMode) {
    List<RawToken> tokens = new ArrayList<>();

    if (isCsvMode) {
      // Use RFC 4180 compliant CSV parsing
      CSVFormat format =
          CSVFormat.Builder.create(CSVFormat.DEFAULT)
              .setDelimiter(',')
              .setQuote('"')
              .setEscape('\\')
              .setQuoteMode(QuoteMode.MINIMAL)
              .setIgnoreEmptyLines(false)
              .setTrim(false)
              .get();

      try (CSVParser parser = CSVParser.parse(record, format)) {
        for (CSVRecord recordItem : parser) {
          for (int i = 0; i < recordItem.size(); i++) {
            String value = recordItem.get(i);
            boolean quoted = isFieldQuoted(record, i, isCsvMode);
            // Trim whitespace from unquoted tokens in CSV mode for compatibility
            if (!quoted) {
              value = value.trim();
            }
            tokens.add(new RawToken(value, quoted));
          }
        }
      } catch (IOException e) {
        // Should not happen with StringReader, but handle gracefully
        throw new RuntimeException("Failed to parse CSV string", e);
      }
    } else {
      // Non-CSV mode: whitespace-delimited
      // We need to handle quoted fields within whitespace-delimited context
      tokens = parseWhitespaceDelimited(record);
    }

    return tokens;
  }

  /** Parses a whitespace-delimited string, respecting quoted fields. */
  private static List<RawToken> parseWhitespaceDelimited(String record) {
    List<RawToken> tokens = new ArrayList<>();
    int i = 0;
    int len = record.length();

    while (i < len) {
      // Skip whitespace
      while (i < len && Character.isWhitespace(record.charAt(i))) {
        i++;
      }
      if (i >= len) {
        break;
      }

      if (record.charAt(i) == '"') {
        // Quoted field
        StringBuilder sb = new StringBuilder();
        i++; // skip opening quote
        boolean doubleQuoted = true;
        while (i < len) {
          char c = record.charAt(i);
          if (c == '"') {
            if (i + 1 < len && record.charAt(i + 1) == '"') {
              // Escaped quote
              sb.append('"');
              i += 2;
            } else {
              // End of quoted field
              i++; // skip closing quote
              break;
            }
          } else {
            sb.append(c);
            i++;
          }
        }
        tokens.add(new RawToken(sb.toString().trim(), doubleQuoted));
      } else {
        // Unquoted field: read until whitespace
        int start = i;
        while (i < len && !Character.isWhitespace(record.charAt(i))) {
          i++;
        }
        String value = record.substring(start, i).trim();
        tokens.add(new RawToken(value, false));
      }
    }

    return tokens;
  }

  /**
   * Determines whether a specific field at the given index was quoted in the original string. This
   * is a heuristic based on scanning the original record.
   */
  private static boolean isFieldQuoted(String record, int fieldIndex, boolean isCsvMode) {
    if (!isCsvMode) {
      // For non-CSV mode, check if the token starts with a quote
      int pos = 0;
      int currentField = 0;

      while (pos < record.length() && currentField < fieldIndex) {
        // Skip whitespace
        while (pos < record.length() && Character.isWhitespace(record.charAt(pos))) {
          pos++;
        }
        if (pos >= record.length()) break;

        if (record.charAt(pos) == '"') {
          // Skip quoted field
          pos++;
          while (pos < record.length()) {
            if (record.charAt(pos) == '"') {
              if (pos + 1 < record.length() && record.charAt(pos + 1) == '"') {
                pos += 2;
              } else {
                pos++;
                break;
              }
            } else {
              pos++;
            }
          }
        } else {
          // Skip unquoted field
          while (pos < record.length() && !Character.isWhitespace(record.charAt(pos))) {
            pos++;
          }
        }
        currentField++;
      }

      if (pos < record.length()) {
        return record.charAt(pos) == '"';
      }
      return false;
    }

    // For CSV mode, scan through fields to find the one at fieldIndex
    int pos = 0;
    int currentField = 0;

    while (pos < record.length() && currentField < fieldIndex) {
      if (record.charAt(pos) == '"') {
        // Skip quoted field
        pos++;
        while (pos < record.length()) {
          if (record.charAt(pos) == '"') {
            if (pos + 1 < record.length() && record.charAt(pos + 1) == '"') {
              pos += 2;
            } else {
              pos++;
              break;
            }
          } else {
            pos++;
          }
        }
        // Skip comma
        if (pos < record.length() && record.charAt(pos) == ',') {
          pos++;
        }
      } else {
        // Skip unquoted field
        while (pos < record.length() && record.charAt(pos) != ',') {
          pos++;
        }
        // Skip comma
        if (pos < record.length() && record.charAt(pos) == ',') {
          pos++;
        }
      }
      currentField++;
    }

    if (pos < record.length()) {
      return record.charAt(pos) == '"';
    }
    return false;
  }

  /**
   * Tests if there are more tokens available from this tokenizer.
   *
   * @return {@code true} if and only if there is at least one token remaining; {@code false}
   *     otherwise.
   */
  public boolean hasMoreTokens() {
    if (this.isCommentLine) {
      return false;
    }
    return this.rawTokenIndex < this.rawTokens.size();
  }

  /**
   * Returns the next token from this string tokenizer.
   *
   * @return the next token.
   * @throws NoSuchElementException if there are no more tokens.
   */
  public Token nextToken() throws NoSuchElementException {
    if (!hasMoreTokens()) {
      throw new NoSuchElementException();
    }
    RawToken raw = this.rawTokens.get(this.rawTokenIndex++);
    return new Token(raw.getValue(), raw.isDoubleQuoted());
  }

  /**
   * Tests if there are more elements. Delegates to {@link #hasMoreTokens()}.
   *
   * @return {@code true} if there are more elements.
   */
  @Override
  public boolean hasNext() {
    return hasMoreTokens();
  }

  /**
   * Returns the next element. Delegates to {@link #nextToken()}.
   *
   * @return the next token.
   * @throws NoSuchElementException if there are no more tokens.
   */
  @Override
  public Token next() {
    return nextToken();
  }

  /**
   * Returns the same value as the {@code hasMoreTokens} method. It exists so that this class can
   * implement the legacy {@code Enumeration} interface.
   *
   * @return {@code true} if there are more tokens.
   */
  public boolean hasMoreElements() {
    return hasMoreTokens();
  }

  /**
   * Returns the same value as the {@code nextToken} method. It exists so that this class can
   * implement the legacy {@code Enumeration} interface.
   *
   * @return the next token.
   */
  public Token nextElement() {
    return nextToken();
  }

  /** A token containing a parsed value and metadata about quoting. */
  public static class Token {

    private final String string;
    private final boolean doubleQuoted;

    /**
     * Creates a token.
     *
     * @param string the token value
     * @param doubleQuoted whether the token was enclosed in double quotes in the original input
     */
    public Token(String string, boolean doubleQuoted) {
      this.string = string;
      this.doubleQuoted = doubleQuoted;
    }

    /**
     * Returns the string value of this token.
     *
     * @return the token value
     */
    public String getString() {
      return this.string;
    }

    /**
     * Returns whether this token was enclosed in double quotes.
     *
     * @return true if the token was double-quoted in the original input
     */
    public boolean isDoubleQuoted() {
      return this.doubleQuoted;
    }

    @Override
    public String toString() {
      return getString();
    }
  }
}
