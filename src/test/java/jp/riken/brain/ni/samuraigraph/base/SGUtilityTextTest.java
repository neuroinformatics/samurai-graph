package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.print.attribute.standard.MediaSize;
import jp.riken.brain.ni.samuraigraph.base.SGCSVTokenizer.Token;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

/** Unit tests of the text utility methods. */
class SGUtilityTextTest {

  // -- superscript / subscript ------------------------------------------

  @Test
  void subscriptAndSuperscriptStringsAreWrappedInBraces() {
    assertEquals("x^{2}", SGUtilityText.getSuperscriptString("x", "2"));
    assertEquals("x_{2}", SGUtilityText.getSubscriptString("x", "2"));
  }

  @Test
  void subscriptAndSuperscriptInfoSplitsSimpleTokens() {
    final List<String> baseList = new ArrayList<String>();
    final List<String> superList = new ArrayList<String>();
    final List<String> subList = new ArrayList<String>();
    assertTrue(
        SGUtilityText.getSubscriptAndSuperscriptInfo("x_{a}^{b}", baseList, superList, subList));
    assertEquals(Arrays.asList("x"), baseList);
    assertEquals(Arrays.asList("b"), superList);
    assertEquals(Arrays.asList("a"), subList);
  }

  @Test
  void subscriptAndSuperscriptInfoSplitsPlainIndexes() {
    final List<String> baseList = new ArrayList<String>();
    final List<String> superList = new ArrayList<String>();
    final List<String> subList = new ArrayList<String>();
    assertTrue(SGUtilityText.getSubscriptAndSuperscriptInfo("x_1^2", baseList, superList, subList));
    assertEquals(Arrays.asList("x"), baseList);
    assertEquals(Arrays.asList("2"), superList);
    assertEquals(Arrays.asList("1"), subList);
  }

  @Test
  void subscriptAndSuperscriptInfoAllowsMultipleTokens() {
    final List<String> baseList = new ArrayList<String>();
    final List<String> superList = new ArrayList<String>();
    final List<String> subList = new ArrayList<String>();
    assertTrue(
        SGUtilityText.getSubscriptAndSuperscriptInfo("x_1y_2", baseList, superList, subList));
    assertEquals(Arrays.asList("x", "y"), baseList);
    assertEquals(Arrays.asList(null, null), superList);
    assertEquals(Arrays.asList("1", "2"), subList);
  }

  @Test
  void subscriptAndSuperscriptInfoHandlesEscapes() {
    // an escaped brace inside the index brace is kept as is
    final List<String> baseList = new ArrayList<String>();
    final List<String> superList = new ArrayList<String>();
    final List<String> subList = new ArrayList<String>();
    assertTrue(
        SGUtilityText.getSubscriptAndSuperscriptInfo("x_{a\\{b}", baseList, superList, subList));
    assertEquals(Arrays.asList("x"), baseList);
    assertEquals(Arrays.asList("a\\{b"), subList);

    // an escaped pattern name is replaced by its character
    final List<String> base2 = new ArrayList<String>();
    final List<String> super2 = new ArrayList<String>();
    final List<String> sub2 = new ArrayList<String>();
    assertTrue(SGUtilityText.getSubscriptAndSuperscriptInfo("x_{\\alpha}", base2, super2, sub2));
    assertEquals(Arrays.asList("x"), base2);
    assertEquals(Arrays.asList("\u03B1"), sub2);
  }

  @Test
  void subscriptAndSuperscriptInfoRejectsUnbalancedStrings() {
    final List<String> baseList = new ArrayList<String>();
    final List<String> superList = new ArrayList<String>();
    final List<String> subList = new ArrayList<String>();
    assertFalse(SGUtilityText.getSubscriptAndSuperscriptInfo(null, baseList, superList, subList));
    assertFalse(SGUtilityText.getSubscriptAndSuperscriptInfo("", baseList, superList, subList));
    assertFalse(SGUtilityText.getSubscriptAndSuperscriptInfo("x_", baseList, superList, subList));
    assertFalse(SGUtilityText.getSubscriptAndSuperscriptInfo("x^", baseList, superList, subList));
    assertFalse(SGUtilityText.getSubscriptAndSuperscriptInfo("x_{a", baseList, superList, subList));
    assertFalse(SGUtilityText.getSubscriptAndSuperscriptInfo("x{a", baseList, superList, subList));
    assertFalse(SGUtilityText.getSubscriptAndSuperscriptInfo("^x", baseList, superList, subList));
    assertFalse(
        SGUtilityText.getSubscriptAndSuperscriptInfo("x^2^3", baseList, superList, subList));
    assertFalse(SGUtilityText.getSubscriptAndSuperscriptInfo("x", null, superList, subList));
  }

  @Test
  void compileStripsBracesAndResolvesEscapes() {
    assertEquals("xy", SGUtilityText.compile("x{y}"));
    assertEquals("x{y", SGUtilityText.compile("x\\{y"));
    assertEquals("x\\y", SGUtilityText.compile("x\\\\y"));
    assertEquals("x\u03B1 y", SGUtilityText.compile("x\\alpha y"));
    assertEquals("", SGUtilityText.compile(""));
  }

  // -- csv / tokens -------------------------------------------------------

  @Test
  void csvStringKeepsPlainValuesAndWrapsSpecialValues() {
    assertNull(SGUtilityText.getCSVString(null));
    assertEquals("abc", SGUtilityText.getCSVString("abc"));
    assertEquals("a,b\"a,b\"", SGUtilityText.getCSVString("a,b"));
    assertEquals(" a\" a\"", SGUtilityText.getCSVString(" a"));
    assertEquals("a\"\"b\"a\"\"b\"", SGUtilityText.getCSVString("a\"b"));
  }

  @Test
  void tokenListParsesBracedCommaLists() {
    assertEquals(Arrays.asList("a", " b ", "c"), SGUtilityText.getTokenList("{a, b ,c}"));
    assertNull(SGUtilityText.getTokenList("abc"));
  }

  @Test
  void tokenListInBracketExtractsParenthesizedGroups() {
    assertEquals(
        Arrays.asList("(1,2)", "(3,4)"), SGUtilityText.getTokenListInBracket("(1,2), (3,4)"));
    assertEquals(0, SGUtilityText.getTokenListInBracket("nothing").size());
  }

  @Test
  void colorListParsesBracedColorGroups() {
    final List<Color> list = SGUtilityText.getColorList("{(255,0,0),(0,255,0)}");
    assertNotNull(list);
    assertEquals(2, list.size());
    assertEquals(new Color(255, 0, 0), list.get(0));
    assertEquals(new Color(0, 255, 0), list.get(1));
    assertNull(SGUtilityText.getColorList("abc"));
  }

  @Test
  void tokenizeFillsTheTokenList() {
    final List<Token> tokenList = new ArrayList<Token>();
    assertTrue(SGUtilityText.tokenize("a,b", tokenList, true));
    assertEquals(2, tokenList.size());
    assertEquals("a", tokenList.get(0).getString());
    assertEquals("b", tokenList.get(1).getString());

    // an empty string is a successful no-op
    final List<Token> empty = new ArrayList<Token>();
    assertTrue(SGUtilityText.tokenize("", empty, true));
    assertEquals(0, empty.size());

    // an odd number of quotes is an error
    final List<Token> odd = new ArrayList<Token>();
    assertFalse(SGUtilityText.tokenize("a\"b", odd, false));
    assertEquals(0, odd.size());

    assertThrows(IllegalArgumentException.class, () -> SGUtilityText.tokenize(null, odd, true));
  }

  @Test
  void firstTokenListSkipsBlankLinesAndComments() {
    final BufferedReader br = new BufferedReader(new StringReader("   \n#comment\na, b\n"));
    final List<Token> tokenList = SGUtilityText.getFirstTokenList(br);
    assertEquals(2, tokenList.size());
    assertEquals("a", tokenList.get(0).getString());
    assertEquals("b", tokenList.get(1).getString());

    final BufferedReader commentsOnly = new BufferedReader(new StringReader("#comment\n"));
    assertEquals(0, SGUtilityText.getFirstTokenList(commentsOnly).size());
  }

  @Test
  void readLineSkipsBlankLinesAndTheByteOrderMark() {
    final BufferedReader br = new BufferedReader(new StringReader("\uFEFFabc\n   \ndef\n"));
    assertEquals("abc", SGUtilityText.readLine(br));
    assertEquals("def", SGUtilityText.readLine(br));
    assertNull(SGUtilityText.readLine(br));
  }

  @Test
  void createStringJoinsTheCharacterList() {
    final ArrayList<Character> charList = new ArrayList<>(Arrays.asList('a', 'b', 'c'));
    assertEquals("abc", SGUtilityText.createString(charList));
    assertEquals("", SGUtilityText.createString(new ArrayList<Character>()));
  }

  // -- arrays -------------------------------------------------------------

  @Test
  void numberArraysParseParenthesizedGroups() {
    assertArrayEquals(new int[] {1, 2, 3}, SGUtilityText.getIntegerArray("(1, 2, 3)"));
    assertArrayEquals(new float[] {1.5f, 2.5f}, SGUtilityText.getFloatArray("(1.5, 2.5)"));
    assertArrayEquals(new double[] {1.5, 2.25}, SGUtilityText.getDoubleArray("(1.5, 2.25)"));
    assertNull(SGUtilityText.getIntegerArray("abc"));
    assertNull(SGUtilityText.getIntegerArray("(1, x)"));
    assertNull(SGUtilityText.getFloatArray(null));
    assertNull(SGUtilityText.getDoubleArray(null));
  }

  @Test
  void parseIndicesParsesNumbersAndBracedLists() {
    assertArrayEquals(new Integer[] {}, SGUtilityText.parseIndices(""));
    assertArrayEquals(new Integer[] {3}, SGUtilityText.parseIndices("3"));
    assertArrayEquals(new Integer[] {1, 2, 3}, SGUtilityText.parseIndices("{1,2,3}"));
    assertNull(SGUtilityText.parseIndices("abc"));
    assertNull(SGUtilityText.parseIndices("{1,x}"));
  }

  @Test
  void parseStringsParsesPlainAndBracedLists() {
    assertArrayEquals(new String[] {"a"}, SGUtilityText.parseStrings("a"));
    assertArrayEquals(new String[] {"a", "b"}, SGUtilityText.parseStrings("{a, b}"));
    assertNull(SGUtilityText.parseStrings(""));
    // a single brace without its closing part is treated as a plain token
    assertArrayEquals(new String[] {"a{b"}, SGUtilityText.parseStrings("a{b"));
  }

  // -- colors -------------------------------------------------------------

  @Test
  void parseColorValidatesTheComponents() {
    assertEquals(new Color(255, 0, 0), SGUtilityText.parseColor("(255,0,0)"));
    assertEquals(new Color(255, 0, 0, 128), SGUtilityText.parseColor("(255,0,0,128)"));
    assertNull(SGUtilityText.parseColor("(300,0,0)"));
    assertNull(SGUtilityText.parseColor("(255,0)"));
    assertNull(SGUtilityText.parseColor("abc"));
  }

  @Test
  void getColorByNameMatchesTheDefaultColors() {
    assertSame(Color.RED, SGUtilityText.getColor("RED"));
    assertNull(SGUtilityText.getColor("notacolor"));
    assertThrows(IllegalArgumentException.class, () -> SGUtilityText.getColor((String) null));
  }

  @Test
  void getColorByComponentsValidatesTheRange() {
    assertEquals(new Color(255, 0, 0), SGUtilityText.getColor("255", "0", "0"));
    assertNull(SGUtilityText.getColor("300", "0", "0"));
    assertTrue(SGUtilityText.isValidColor(255, 0, 0));
    assertFalse(SGUtilityText.isValidColor(256, 0, 0));
  }

  @Test
  void colorStringFormattingIsSymmetricWithParsing() {
    assertEquals("(255,0,0)", SGUtilityText.getColorString(new Color(255, 0, 0)));
    assertNull(SGUtilityText.getColorString(null));
    assertEquals("255, 0, 0", SGUtilityText.getSimpleColorString(new Color(255, 0, 0)));
    assertNull(SGUtilityText.getSimpleColorString(null));
    assertEquals(
        "{(255,0,0),(0,0,255)}",
        SGUtilityText.getColorListString(Arrays.asList(Color.RED, Color.BLUE)));
  }

  @Test
  void writeColorListPropertyLineWritesTheKeyAndColors() throws Exception {
    final StringWriter writer = new StringWriter();
    assertTrue(
        SGUtilityText.writeColorListPropertyLine(writer, "key", Arrays.asList(new Color(1, 2, 3))));
    assertEquals("key={(1,2,3)}\n", writer.toString());
  }

  @Test
  void colorParsersTryNamesThenRgbStrings() {
    assertSame(Color.RED, SGUtilityText.parseColorString("RED"));
    assertEquals(new Color(1, 2, 3), SGUtilityText.parseColorString("(1,2,3)"));
    assertNull(SGUtilityText.parseColorString("zzz"));
    assertSame(Color.RED, SGUtilityText.parseColorText("RED"));
    assertNull(SGUtilityText.parseColorText("zzz"));
    assertEquals(new Color(1, 2, 3), SGUtilityText.parseColorIncludingList("{(1,2,3),(4,5,6)}"));
    assertEquals(new Color(1, 2, 3), SGUtilityText.parseColorIncludingList("(1,2,3)"));
    assertNull(SGUtilityText.parseColorIncludingList("zzz"));
  }

  // -- simple value parsers ----------------------------------------------

  @Test
  void booleanParsersAcceptOnlyBooleanWordsAndZeroOne() {
    assertEquals(Boolean.TRUE, SGUtilityText.getBoolean("true"));
    assertEquals(Boolean.FALSE, SGUtilityText.getBoolean("FALSE"));
    assertNull(SGUtilityText.getBoolean("xyz"));
    assertNull(SGUtilityText.getBoolean(null));
    assertEquals("true", SGUtilityText.getBooleanString("0"));
    assertEquals("false", SGUtilityText.getBooleanString("1"));
    assertEquals("true", SGUtilityText.getBooleanString("true"));
    assertEquals("false", SGUtilityText.getBooleanString("FALSE"));
    assertNull(SGUtilityText.getBooleanString("x"));
  }

  @Test
  void numericParsersReturnNullOnMalformedInput() {
    assertEquals(Integer.valueOf(42), SGUtilityText.getInteger("42"));
    assertNull(SGUtilityText.getInteger("abc"));
    assertEquals(Float.valueOf(1.5f), SGUtilityText.getFloat("1.5"));
    assertNull(SGUtilityText.getFloat("abc"));
    assertEquals(Double.valueOf(1.5), SGUtilityText.getDouble("1.5"));
    assertEquals(Double.NaN, SGUtilityText.getDouble("NaN"));
    assertNull(SGUtilityText.getDouble("abc"));
    assertNull(SGUtilityText.getDouble(null));
  }

  @Test
  void fontStyleParsersRoundTripTheFourStyles() {
    assertEquals("Plain", SGUtilityText.getFontStyleName(Font.PLAIN));
    assertEquals("Bold", SGUtilityText.getFontStyleName(Font.BOLD));
    assertEquals("Italic", SGUtilityText.getFontStyleName(Font.ITALIC));
    assertEquals("Bold Italic", SGUtilityText.getFontStyleName(Font.BOLD | Font.ITALIC));
    assertNull(SGUtilityText.getFontStyleName(5));
    assertEquals(Integer.valueOf(Font.PLAIN), SGUtilityText.getFontStyle("Plain"));
    assertEquals(Integer.valueOf(Font.BOLD), SGUtilityText.getFontStyle("bold"));
    assertEquals(Integer.valueOf(Font.ITALIC), SGUtilityText.getFontStyle("Italic"));
    assertEquals(
        Integer.valueOf(Font.BOLD | Font.ITALIC), SGUtilityText.getFontStyle("Bold Italic"));
    assertNull(SGUtilityText.getFontStyle("zzz"));
    assertNull(SGUtilityText.getFontStyle(null));
    assertTrue(SGUtilityText.isValidFontStyle(Font.PLAIN));
    assertFalse(SGUtilityText.isValidFontStyle(5));
  }

  @Test
  void scaleTypeParsersMapTheTwoScales() {
    assertEquals("Linear", SGUtilityText.getScaleTypeName(SGAxis.LINEAR_SCALE));
    assertEquals("Log", SGUtilityText.getScaleTypeName(SGAxis.LOG_SCALE));
    assertNull(SGUtilityText.getScaleTypeName(5));
    assertEquals(SGAxis.LINEAR_SCALE, SGUtilityText.getScaleType("Linear"));
    assertEquals(SGAxis.LOG_SCALE, SGUtilityText.getScaleType("log"));
    assertEquals(-1, SGUtilityText.getScaleType("zzz"));
    assertEquals(-1, SGUtilityText.getScaleType(null));
  }

  @Test
  void axisDirectionParsersMapTheThreeDirections() {
    assertEquals(
        SGFigureElementAxisConstants.AXIS_DIRECTION_HORIZONTAL,
        SGUtilityText.getAxisDirection("Horizontal"));
    assertEquals(
        SGFigureElementAxisConstants.AXIS_DIRECTION_VERTICAL,
        SGUtilityText.getAxisDirection("vertical"));
    assertEquals(
        SGFigureElementAxisConstants.AXIS_DIRECTION_NORMAL,
        SGUtilityText.getAxisDirection("ColorBar"));
    assertEquals(-1, SGUtilityText.getAxisDirection("zzz"));
    assertEquals(-1, SGUtilityText.getAxisDirection(null));
  }

  // -- string helpers -----------------------------------------------------

  @Test
  void lastSubstringReturnsThePartAfterTheLastSeparator() {
    assertEquals("c", SGUtilityText.lastSubstring("a/b/c", '/'));
    assertNull(SGUtilityText.lastSubstring("abc", '/'));
    assertNull(SGUtilityText.lastSubstring("abc/", '/'));
  }

  @Test
  void createMapParsesParenthesizedKeyValues() {
    final Map<String, String> map = SGUtilityText.createMap("(a:1, b:2)");
    assertNotNull(map);
    assertEquals("1", map.get("a"));
    assertEquals("2", map.get("b"));
    assertNull(SGUtilityText.createMap("abc"));
    assertNull(SGUtilityText.createMap("(a:1:2)"));
  }

  @Test
  void createIntegerKeyMapRequiresIntegerKeys() {
    final Map<Integer, String> map = SGUtilityText.createIntegerKeyMap("(1:a, 2:b)");
    assertNotNull(map);
    assertEquals("a", map.get(1));
    assertEquals("b", map.get(2));
    assertNull(SGUtilityText.createIntegerKeyMap("(a:1)"));
  }

  @Test
  void tokenizeCommandSplitsOutsideStringsAndBrackets() {
    assertEquals(Arrays.asList("a", "b"), SGUtilityText.tokenizeCommand("a, b"));
    assertEquals(Arrays.asList("a", "(b, c)"), SGUtilityText.tokenizeCommand("a, (b, c)"));
    assertEquals(Arrays.asList("\"a, b\"", "c"), SGUtilityText.tokenizeCommand("\"a, b\", c"));
    assertEquals(Arrays.asList("\"a\\\", b\""), SGUtilityText.tokenizeCommand("\"a\\\", b\""));
    assertEquals(Arrays.asList(""), SGUtilityText.tokenizeCommand(""));
    assertNull(SGUtilityText.tokenizeCommand("a, b\""));
    assertNull(SGUtilityText.tokenizeCommand("(a, b"));
  }

  @Test
  void getSerialNameContinuesTheHighestIndex() {
    assertEquals("x(3)", SGUtilityText.getSerialName(Arrays.asList("x(1)", "x(2)"), "x"));
    assertEquals("x(1)", SGUtilityText.getSerialName(Arrays.asList("x"), "x"));
    assertEquals("x", SGUtilityText.getSerialName(Arrays.asList("y(5)"), "x"));
    assertEquals("x", SGUtilityText.getSerialName(new ArrayList<String>(), "x"));
  }

  @Test
  void stringHelpersMatchOnlyLettersAndDigits() {
    assertEquals(2, SGUtilityText.count("ababc", 'a'));
    assertTrue(SGUtilityText.isDoubleQuoted("\"abc\""));
    assertFalse(SGUtilityText.isDoubleQuoted("\"abc"));
    assertFalse(SGUtilityText.isDoubleQuoted("a"));
    assertTrue(SGUtilityText.isEqualString("aBc", "abc"));
    assertFalse(SGUtilityText.isEqualString("abc", "abd"));
    assertThrows(IllegalArgumentException.class, () -> SGUtilityText.isEqualString(null, "abc"));
    assertTrue(SGUtilityText.startsWith("ABCdef", "abc"));
    assertFalse(SGUtilityText.startsWith("defABC", "abc"));
    assertThrows(IllegalArgumentException.class, () -> SGUtilityText.startsWith(null, "abc"));
    assertEquals("ABC1", SGUtilityText.getCharString("a B-c_1"));
    assertThrows(IllegalArgumentException.class, () -> SGUtilityText.getCharString(null));
    assertTrue(SGUtilityText.isValidString("abc"));
    assertFalse(SGUtilityText.isValidString("   "));
    assertFalse(SGUtilityText.isValidString("\u3000"));
    assertFalse(SGUtilityText.isValidString(""));
    assertFalse(SGUtilityText.isValidString(null));
  }

  @Test
  void readStringMapsParsesKeyEqualsValuePairs() {
    final String[][] maps = SGUtilityText.readStringMaps("y=0, x=2");
    assertEquals(2, maps.length);
    assertEquals("y", maps[0][0]);
    assertEquals("0", maps[0][1]);
    assertEquals("x", maps[1][0]);
    assertEquals("2", maps[1][1]);
  }

  // -- lengths ------------------------------------------------------------

  @Test
  void convertUsesTheUnitRatios() {
    assertEquals(10.0, SGUtilityText.convert(1.0, "cm", "mm"), 1e-9);
    assertEquals(2.54, SGUtilityText.convert(1.0, "inch", "cm"), 1e-6);
    assertEquals(1.0, SGUtilityText.convert(72.0, "pt", "inch"), 1e-6);
    assertEquals(1.0, SGUtilityText.convert(1.0, "cm", "cm"), 1e-9);
    assertEquals(72.0, SGUtilityText.convertToPoint(1.0, "inch"), 1e-6);
    assertEquals(1.0, SGUtilityText.convertFromPoint(72.0, "inch"), 1e-6);
    assertThrows(IllegalArgumentException.class, () -> SGUtilityText.convert(1.0, "cm", "km"));
    assertThrows(IllegalArgumentException.class, () -> SGUtilityText.convert(1.0, null, "cm"));
  }

  @Test
  void isLengthUnitKnowsTheFourUnits() {
    assertTrue(SGUtilityText.isLengthUnit("cm"));
    assertFalse(SGUtilityText.isLengthUnit("km"));
    assertThrows(IllegalArgumentException.class, () -> SGUtilityText.isLengthUnit(null));
    assertArrayEquals(
        new String[] {"cm", "mm", "pt", "inch"}, SGUtilityText.getUnitsArrayOfLength());
  }

  @Test
  void lengthParsersConvertToTheRequestedUnit() {
    assertEquals(2.54, SGUtilityText.getLength("1inch", "cm").doubleValue(), 1e-6);
    assertEquals(1.0 / 2.54, SGUtilityText.getLength("1cm", "inch").doubleValue(), 1e-6);
    assertEquals(72.0, SGUtilityText.getLengthInPoint("1inch").doubleValue(), 1e-6);
    assertNull(SGUtilityText.getLength("abc", "cm"));
    assertNull(SGUtilityText.getLength(null, "cm"));
    assertNull(SGUtilityText.getLength("", "cm"));
    assertEquals(Integer.valueOf(1), SGUtilityText.getInteger("1cm", "cm"));
    assertEquals(Float.valueOf(1.5f), SGUtilityText.getFloat("1.5cm", "cm"));
    assertEquals(Double.valueOf(1.5), SGUtilityText.getDouble("1.5inch", "inch"));
  }

  @Test
  void convertStringRewritesTheValueInTheNewUnit() {
    // the conversion uses float ratios, so the result is not exact
    assertEquals("2.5399999618530273", SGUtilityText.convertString("1inch", "cm"));
    assertNull(SGUtilityText.convertString("abc", "cm"));
    assertNull(SGUtilityText.convertString(null, "cm"));

    final StringBuilder unit = new StringBuilder();
    assertEquals(Double.valueOf(1.0), SGUtilityText.getNumber("1cm", unit));
    assertEquals("cm", unit.toString());
    assertNull(SGUtilityText.getNumber("abc", unit));
  }

  @Test
  void removeUnitAndRemoveSuffixStripTrailingUnits() {
    assertEquals("1", SGUtilityText.removeSuffix("1cm", "cm"));
    assertEquals("1abc", SGUtilityText.removeSuffix("1abc", "cm"));
    assertEquals("1cm", SGUtilityText.removeSuffix("1cm", null));
    assertNull(SGUtilityText.removeSuffix(null, "cm"));
    assertNull(SGUtilityText.removeSuffix("", "cm"));
    assertEquals(Double.valueOf(1.0), SGUtilityText.removeUnit("1 cm", "cm"));
    assertNull(SGUtilityText.removeUnit("1x cm", "cm"));
  }

  // -- media sizes --------------------------------------------------------

  @Test
  void mediaSizeParsersKnowTheStandardSizes() {
    assertSame(MediaSize.ISO.A4, SGUtilityText.getMediaSize("A4"));
    assertSame(MediaSize.ISO.A4, SGUtilityText.getMediaSize("a4"));
    assertSame(MediaSize.ISO.B5, SGUtilityText.getMediaSize("B5"));
    assertSame(MediaSize.NA.LETTER, SGUtilityText.getMediaSize("US_Letter"));
    assertSame(MediaSize.NA.LETTER, SGUtilityText.getMediaSize("Letter"));
    assertNull(SGUtilityText.getMediaSize("zzz"));
    assertNull(SGUtilityText.getMediaSize(null));
    assertEquals(Boolean.TRUE, SGUtilityText.isPortrait("Portrait"));
    assertEquals(Boolean.FALSE, SGUtilityText.isPortrait("Landscape"));
    assertNull(SGUtilityText.isPortrait("zzz"));
    assertNull(SGUtilityText.isPortrait(null));
  }

  @Test
  void dimensionParsesExactlyTwoNonNegativeValues() {
    assertEquals(new Dimension(100, 200), SGUtilityText.getDimension("(100, 200)"));
    assertNull(SGUtilityText.getDimension("(100)"));
    assertNull(SGUtilityText.getDimension("(-1, 2)"));
    assertNull(SGUtilityText.getDimension("abc"));
  }

  // -- xml / period / date ------------------------------------------------

  @Test
  void getDocumentFromStringParsesWellFormedXml() {
    final Document doc = SGUtilityText.getDocumentFromString("<root><a/></root>");
    assertNotNull(doc);
    assertEquals("root", doc.getDocumentElement().getTagName());
    assertNull(SGUtilityText.getDocumentFromString("not xml"));
  }

  @Test
  void getPeriodParsesIso8601Periods() {
    final SGPeriod period = SGUtilityText.getPeriod("P1Y2M3DT4H5M6.789S");
    assertNotNull(period);
    assertEquals(1, period.getYears());
    assertEquals(2, period.getMonths());
    assertEquals(3, period.getDays());
    assertEquals(4, period.getHours());
    assertEquals(5, period.getMinutes());
    assertEquals(6, period.getSeconds());
    assertEquals(789, period.getMillis());
    assertNull(SGUtilityText.getPeriod("bad"));
  }

  @Test
  void getDateParsesDefaultFormats() {
    assertNotNull(SGUtilityText.getDate("26/1/1"));
    assertNull(SGUtilityText.getDate("not a date"));
  }
}
