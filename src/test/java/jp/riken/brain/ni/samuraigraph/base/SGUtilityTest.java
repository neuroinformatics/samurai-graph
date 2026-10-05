package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Unit tests of the pure utility methods of {@link SGUtility}. The Swing-dependent methods are not
 * covered here.
 */
class SGUtilityTest {

  // -- number naming ------------------------------------------------------

  @Test
  void getNumberNameFormatsZeroAndPlainValues() {
    assertEquals("0", SGUtility.getNumberName(0.0));
    // minus zero is reported without the sign
    assertEquals("0", SGUtility.getNumberName(-0.0));
    assertEquals("5", SGUtility.getNumberName(5.0));
    assertEquals("5.5", SGUtility.getNumberName(5.5));
    assertEquals("123", SGUtility.getNumberName(123.45));
    assertEquals("500", SGUtility.getNumberName(500.0));
    assertEquals("9999", SGUtility.getNumberName(9999.0));
  }

  @Test
  void getNumberNameFormatsHundredsAndSmallValues() {
    assertEquals("100", SGUtility.getNumberName(100.0));
    assertEquals("1000", SGUtility.getNumberName(1000.0));
    assertEquals("10000", SGUtility.getNumberName(10000.0));
    assertEquals("0.5", SGUtility.getNumberName(0.5));
    assertEquals("0.1234", SGUtility.getNumberName(0.1234));
    assertEquals("0.0012", SGUtility.getNumberName(0.001234));
  }

  @Test
  void getNumberNameFormatsLargeValuesInExponentialNotation() {
    assertEquals("1.23e4", SGUtility.getNumberName(12345.0));
    assertEquals("1.5e5", SGUtility.getNumberName(150000.0));
    assertEquals("1.01e4", SGUtility.getNumberName(10100.0));
  }

  @Test
  void getNumberNameFormatsTinyValuesInExponentialNotation() {
    assertEquals("5e-5", SGUtility.getNumberName(0.00005));
    assertEquals("1.23e-7", SGUtility.getNumberName(1.23e-7));
  }

  @Test
  void getNumberNameKeepsMinusSign() {
    assertEquals("-5", SGUtility.getNumberName(-5.0));
    assertEquals("-1.23e4", SGUtility.getNumberName(-12345.0));
    assertEquals("-5e-5", SGUtility.getNumberName(-0.00005));
    assertEquals("-0.5", SGUtility.getNumberName(-0.5));
  }

  // -- version helpers ----------------------------------------------------

  @Test
  void splitVersionNumberParsesNumericParts() {
    assertArrayEquals(new int[] {1, 2, 3}, SGUtility.splitVersionNumber("1.2.3"));
    assertNull(SGUtility.splitVersionNumber("1.2"));
    assertNull(SGUtility.splitVersionNumber("1.x.3"));
  }

  @Test
  void compareVersionNumberComparesPartByPart() {
    assertTrue(SGUtility.compareVersionNumber(1, 2, 3, 1, 2, 4));
    assertTrue(SGUtility.compareVersionNumber(1, 2, 3, 1, 3, 0));
    assertTrue(SGUtility.compareVersionNumber(1, 2, 3, 2, 0, 0));
    assertFalse(SGUtility.compareVersionNumber(1, 2, 3, 1, 2, 2));
    assertFalse(SGUtility.compareVersionNumber(2, 0, 0, 1, 9, 9));
    assertFalse(SGUtility.compareVersionNumber(1, 2, 3, 1, 2, 3));
  }

  @Test
  void isVersionNumberEqualOrSmallerThanComparesVersions() {
    assertTrue(SGUtility.isVersionNumberEqualOrSmallerThan("1.0.1", "1.0.2"));
    assertTrue(SGUtility.isVersionNumberEqualOrSmallerThan("1.0.2", "1.0.2"));
    assertFalse(SGUtility.isVersionNumberEqualOrSmallerThan("1.0.2", "1.0.1"));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.isVersionNumberEqualOrSmallerThan(null, "1.0.1"));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.isVersionNumberEqualOrSmallerThan("1.0", "1.0.1"));
  }

  @Test
  void isVersionNumberEqualOrSmallerThanPermittingEmptyStringTreatsEmptyAsDefault() {
    assertTrue(SGUtility.isVersionNumberEqualOrSmallerThanPermittingEmptyString("", ""));
    // an empty version is interpreted as "1.0.7"
    assertTrue(SGUtility.isVersionNumberEqualOrSmallerThanPermittingEmptyString("", "1.0.8"));
    assertFalse(SGUtility.isVersionNumberEqualOrSmallerThanPermittingEmptyString("1.0.8", ""));
    assertTrue(SGUtility.isVersionNumberEqualOrSmallerThanPermittingEmptyString("1.0.1", "2.0.0"));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.isVersionNumberEqualOrSmallerThanPermittingEmptyString(null, "1.0.1"));
  }

  // -- value and array helpers ---------------------------------------------

  @Test
  void findNearestValueFindsNearestIndex() {
    final int[] array = {1, 3, 5, 7};
    assertEquals(-1, SGUtility.findNearestValue(4, new int[] {}));
    assertEquals(42, SGUtility.findNearestValue(100, new int[] {42}));
    assertEquals(5, SGUtility.findNearestValue(5, array));
    assertEquals(1, SGUtility.findNearestValue(0, array));
    assertEquals(7, SGUtility.findNearestValue(8, array));
    assertEquals(7, SGUtility.findNearestValue(6, array));
    // ties are resolved toward the next value
    assertEquals(3, SGUtility.findNearestValue(2, array));
    assertEquals(5, SGUtility.findNearestValue(4, array));
  }

  @Test
  void transposeExchangesRowsAndColumns() {
    final double[][] input = {{1, 2, 3}, {4, 5, 6}};
    assertArrayEquals(new double[][] {{1, 4}, {2, 5}, {3, 6}}, SGUtility.transpose(input));
  }

  @Test
  void transposeRejectsInvalidArrays() {
    assertThrows(IllegalArgumentException.class, () -> SGUtility.transpose(null));
    assertThrows(IllegalArgumentException.class, () -> SGUtility.transpose(new double[][] {}));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.transpose(new double[][] {new double[] {1}, null}));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.transpose(new double[][] {new double[] {1, 2}, new double[] {1}}));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.transpose(new double[][] {new double[] {1}, new double[] {}}));
  }

  @Test
  void createStringBuildsTrimmedUtf8String() throws Exception {
    final byte[] bytes = "  hello wörld  ".getBytes(StandardCharsets.UTF_8);
    assertEquals("hello wörld", SGUtility.createString(bytes));
  }

  @Test
  void createIndicesWithinRangeKeepsNumbersWhenAllWithinLength() {
    final SGIntegerSeriesSet stride = new SGIntegerSeriesSet(new int[] {0, 2, 4});
    final SGIntegerSeriesSet result = SGUtility.createIndicesWithinRange(stride, 8);
    assertArrayEquals(new int[] {0, 2, 4}, result.getNumbers());
  }

  @Test
  void createIndicesWithinRangeFallsBackToFullRange() {
    final SGIntegerSeriesSet stride = new SGIntegerSeriesSet(new int[] {0, 2, 9});
    final SGIntegerSeriesSet result = SGUtility.createIndicesWithinRange(stride, 8);
    assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5, 6, 7}, result.getNumbers());
  }

  // -- contains helpers -----------------------------------------------------

  @Test
  void containsChecksSingleElements() {
    assertTrue(SGUtility.contains(new Object[] {"a", "b"}, "a"));
    assertFalse(SGUtility.contains(new Object[] {"a", "b"}, "c"));
    assertThrows(
        IllegalArgumentException.class, () -> SGUtility.contains(new Object[] {"a"}, null));
    assertThrows(IllegalArgumentException.class, () -> SGUtility.contains((Object[]) null, "a"));

    assertTrue(SGUtility.contains(new int[] {1, 2}, 1));
    assertFalse(SGUtility.contains(new int[] {1, 2}, 3));
    assertThrows(IllegalArgumentException.class, () -> SGUtility.contains((int[]) null, 1));

    assertTrue(SGUtility.contains(new char[] {'a', 'b'}, 'a'));
    assertFalse(SGUtility.contains(new char[] {'a', 'b'}, 'c'));
    assertThrows(IllegalArgumentException.class, () -> SGUtility.contains((char[]) null, 'a'));
  }

  @Test
  void containsChecksArrayPairs() {
    assertTrue(SGUtility.contains(new Object[] {"a", "b"}, new Object[] {"c", "b"}));
    assertFalse(SGUtility.contains(new Object[] {"a", "b"}, new Object[] {"c", "d"}));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.contains(new Object[] {"a"}, (Object[]) null));

    assertTrue(SGUtility.contains(new char[] {'a', 'b'}, new char[] {'c', 'b'}));
    assertFalse(SGUtility.contains(new char[] {'a', 'b'}, new char[] {'c', 'd'}));
    assertThrows(IllegalArgumentException.class, () -> SGUtility.contains(new char[] {'a'}, null));
  }

  @Test
  void containsChecksListPairs() {
    assertTrue(SGUtility.contains(Arrays.asList("a", "b"), Arrays.asList("c", "b")));
    assertFalse(SGUtility.contains(Arrays.asList("a", "b"), Arrays.asList("c", "d")));
    assertThrows(IllegalArgumentException.class, () -> SGUtility.contains(List.of("a"), null));
  }

  @Test
  void containsAllChecksArrayAndList() {
    assertTrue(SGUtility.containsAll(new Object[] {"a", "b"}, new Object[] {"a", "b"}));
    assertTrue(SGUtility.containsAll(new Object[] {"a", "b"}, new Object[] {"a"}));
    assertFalse(SGUtility.containsAll(new Object[] {"a", "b"}, new Object[] {"a", "c"}));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.containsAll(new Object[] {"a"}, (Object[]) null));

    assertTrue(SGUtility.containsAll(Arrays.asList("a", "b"), Arrays.asList("a", "b")));
    assertFalse(SGUtility.containsAll(Arrays.asList("a", "b"), Arrays.asList("a", "c")));
    assertThrows(IllegalArgumentException.class, () -> SGUtility.containsAll(List.of("a"), null));
  }

  @Test
  void containsNullIgnoredSkipsNullElements() {
    assertTrue(SGUtility.containsNullIgnored(new Object[] {null, "a"}, "a"));
    assertFalse(SGUtility.containsNullIgnored(new Object[] {null, "a"}, "b"));
    // a null target is never found, even for a null-only array
    assertFalse(SGUtility.containsNullIgnored(new Object[] {null}, (Object) null));
    assertThrows(
        IllegalArgumentException.class, () -> SGUtility.containsNullIgnored((Object[]) null, "a"));

    assertTrue(SGUtility.containsNullIgnored(new Object[] {null, "a"}, new Object[] {null, "a"}));
    assertFalse(SGUtility.containsNullIgnored(new Object[] {null, "a"}, new Object[] {"b"}));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.containsNullIgnored(new Object[] {"a"}, (Object[]) null));
  }

  @Test
  void removeStripsAllMatchingElements() {
    assertArrayEquals(new int[] {1, 3}, SGUtility.remove(new int[] {1, 2, 3, 2}, 2));
    assertArrayEquals(new int[] {}, SGUtility.remove(new int[] {2, 2}, 2));
    assertArrayEquals(new int[] {1, 2}, SGUtility.remove(new int[] {1, 2}, 9));
    assertThrows(IllegalArgumentException.class, () -> SGUtility.remove((int[]) null, 1));
  }

  // -- equality helpers ------------------------------------------------------

  @Test
  void equalsHandlesNullsAndArrays() {
    assertTrue(SGUtility.equals((Object) null, (Object) null));
    assertFalse(SGUtility.equals((Object) null, (Object) "a"));
    assertFalse(SGUtility.equals((Object) "a", (Object) null));
    assertTrue(SGUtility.equals((Object) "a", (Object) "a"));

    assertTrue(SGUtility.equals(new int[] {1, 2}, new int[] {1, 2}));
    final int[] shared = {1, 2};
    assertTrue(SGUtility.equals(shared, shared));
    assertFalse(SGUtility.equals(new int[] {1, 2}, (int[]) null));
    assertFalse(SGUtility.equals(new int[] {1, 2}, new int[] {1, 3}));
    assertFalse(SGUtility.equals((int[]) null, new int[] {}));

    assertTrue(SGUtility.equals(new Object[] {null, "a"}, new Object[] {null, "a"}));
    assertTrue(SGUtility.equals((Object[]) null, (Object[]) null));
    assertFalse(SGUtility.equals(new Object[] {"a"}, (Object[]) null));
    assertFalse(SGUtility.equals(new Object[] {null}, new Object[] {"a"}));
    assertFalse(SGUtility.equals(new Object[] {"a"}, new Object[] {"a", "b"}));
  }

  @Test
  void checkEqualityReturnsCommonValueOrNull() {
    assertNull(SGUtility.checkEquality(new double[] {}));
    assertEquals(1.5, SGUtility.checkEquality(new double[] {1.5}));
    assertEquals(1.5, SGUtility.checkEquality(new double[] {1.5, 1.5}));
    assertNull(SGUtility.checkEquality(new double[] {1.5, 2.5}));

    assertNull(SGUtility.checkEquality(new float[] {}));
    assertEquals(1.5f, SGUtility.checkEquality(new float[] {1.5f, 1.5f}));
    assertNull(SGUtility.checkEquality(new float[] {1.5f, 2.5f}));

    assertNull(SGUtility.checkEquality(new int[] {}));
    assertEquals(3, SGUtility.checkEquality(new int[] {3, 3}));
    assertNull(SGUtility.checkEquality(new int[] {3, 4}));

    assertNull(SGUtility.checkEquality(new boolean[] {}));
    assertEquals(true, SGUtility.checkEquality(new boolean[] {true, true}));
    assertNull(SGUtility.checkEquality(new boolean[] {true, false}));

    assertNull(SGUtility.checkEquality(new String[] {}));
    assertEquals("a", SGUtility.checkEquality(new String[] {"a", "a"}));
    assertNull(SGUtility.checkEquality(new String[] {"a", "b"}));

    assertNull(SGUtility.checkEquality(new Object[] {}));
    assertEquals("a", SGUtility.checkEquality(new Object[] {"a", "a"}));
    assertNull(SGUtility.checkEquality(new Object[] {"a", "b"}));
  }

  @Test
  void checkEqualityForColorsComparesAllElements() {
    final Color red = new Color(255, 0, 0);
    assertNull(SGUtility.checkEquality(new Color[] {}));
    assertEquals(red, SGUtility.checkEquality(new Color[] {red}));
    assertEquals(red, SGUtility.checkEquality(new Color[] {red, new Color(255, 0, 0)}));
    assertNull(SGUtility.checkEquality(new Color[] {red, Color.BLUE}));

    assertNull(SGUtility.checkEquality(new Color[][] {}));
    assertSame(red, SGUtility.checkEquality(new Color[][] {{red}})[0]);
    assertNull(SGUtility.checkEquality(new Color[][] {{red}, {Color.BLUE}}));
  }

  // -- character classification ----------------------------------------------

  @Test
  void isAlphabeticAndIsDigitClassifyCharacters() {
    assertTrue(SGUtility.isAlphabetic('a'));
    assertTrue(SGUtility.isAlphabetic('Z'));
    assertFalse(SGUtility.isAlphabetic('0'));
    assertFalse(SGUtility.isAlphabetic('_'));

    assertTrue(SGUtility.isDigit('0'));
    assertTrue(SGUtility.isDigit('9'));
    assertFalse(SGUtility.isDigit('a'));
    assertFalse(SGUtility.isDigit('.'));
  }

  // -- file name helpers -------------------------------------------------------

  @Test
  void getFileNameExtractsNameFromPath() {
    assertEquals("data.mat", SGUtility.getFileName("data.mat"));
    assertEquals("data.mat", SGUtility.getFileName("/dir/sub/data.mat"));
    // a trailing separator keeps the whole path
    assertEquals("/dir/", SGUtility.getFileName("/dir/"));
    assertEquals("name", SGUtility.getFileName("name"));
  }

  @Test
  void getSimpleFileNameDropsExtension() {
    assertEquals("data", SGUtility.getSimpleFileName("/dir/data.mat"));
    assertEquals("data", SGUtility.getSimpleFileName("data"));
    // only the last extension is dropped
    assertEquals("tar", SGUtility.getSimpleFileName("tar.gz"));
  }

  @Test
  void addEscapeCharEscapesUnderscoreAndCaret() {
    assertEquals("a\\_b\\^c", SGUtility.addEscapeChar("a_b^c"));
    assertEquals("plain", SGUtility.addEscapeChar("plain"));
  }

  @Test
  void removeEscapeCharStripsBackslashes() {
    assertEquals("a_b^c", SGUtility.removeEscapeChar("a\\_b\\^c"));
  }

  @Test
  void createDataNameBaseCombinesHelpers() {
    assertEquals("a\\_b\\^c", SGUtility.createDataNameBase("/dir/a_b^c.mat"));
    assertNull(SGUtility.createDataNameBase("   "));
    assertNull(SGUtility.createDataNameBase(null));
  }

  // -- axis values --------------------------------------------------------------

  private static double utcDaysOf(final ZonedDateTime time) {
    return time.toEpochSecond() / 86400.0;
  }

  @Test
  void getAxisNumberParsesNumbersAndDates() {
    assertNull(SGUtility.getAxisNumber(null));
    assertNull(SGUtility.getAxisNumber(""));
    assertEquals(12.5, SGUtility.getAxisNumber("12.5"));
    assertEquals(Double.NaN, SGUtility.getAxisNumber("NaN"));

    final double expected = utcDaysOf(ZonedDateTime.of(2026, 9, 30, 0, 0, 0, 0, ZoneOffset.UTC));
    // a date string that cannot be read as a plain number falls back to date parsing
    assertEquals(expected, SGUtility.getAxisNumber("26-09-30"));
    assertNull(SGUtility.getAxisNumber("not a number"));
  }

  @Test
  void getAxisValueParsesNumericValues() throws Exception {
    assertNull(SGUtility.getAxisValue(null, false));
    assertNull(SGUtility.getAxisValue("", false));
    final SGAxisDoubleValue value = (SGAxisDoubleValue) SGUtility.getAxisValue("12.5", false);
    assertEquals(12.5, value.getValue());
    assertThrows(java.text.ParseException.class, () -> SGUtility.getAxisValue("abc", false));
  }

  @Test
  void getAxisValueParsesDateValues() throws Exception {
    final String str = "2026-09-30T12:00:00Z";
    final SGAxisDateValue value = (SGAxisDateValue) SGUtility.getAxisValue(str, true);
    final double expected = utcDaysOf(ZonedDateTime.parse(str));
    assertEquals(expected, value.getValue());
    assertThrows(java.text.ParseException.class, () -> SGUtility.getAxisValue("abc", true));
  }

  @Test
  void getAxisStepValueParsesNumericAndDateSteps() throws Exception {
    assertNull(SGUtility.getAxisStepValue(null, false));
    assertNull(SGUtility.getAxisStepValue("", false));
    final SGAxisDoubleStepValue step =
        (SGAxisDoubleStepValue) SGUtility.getAxisStepValue("2.5", false);
    assertEquals(2.5, step.getValue());
    assertThrows(java.text.ParseException.class, () -> SGUtility.getAxisStepValue("abc", false));

    final SGAxisDateStepValue dateStep =
        (SGAxisDateStepValue) SGUtility.getAxisStepValue("P1D", true);
    assertEquals(1, dateStep.getPeriod().getDays());
    assertThrows(java.text.ParseException.class, () -> SGUtility.getAxisStepValue("abc", true));
  }

  // -- export values ---------------------------------------------------------------

  @Test
  void getExportValueRoundsOffToOrder() {
    assertEquals(8700.0f, SGUtility.getExportValue(8715.61f, 2));
    assertEquals(8800.0f, SGUtility.getExportValue(8765.61f, 2));
    // rounding at the tens place drops values below ten
    assertEquals(0.0f, SGUtility.getExportValue(12.3f, 2));
    assertEquals(0.0f, SGUtility.getExportValue(12.5f, 2));
  }

  // -- axis location ---------------------------------------------------------------

  @Test
  void getAxisLocationMapsNamesToIds() {
    assertEquals(
        SGFigureElementAxisConstants.AXIS_HORIZONTAL_1,
        SGUtility.getAxisLocation(SGFigureElementAxisConstants.AXIS_BOTTOM));
    assertEquals(
        SGFigureElementAxisConstants.AXIS_HORIZONTAL_2,
        SGUtility.getAxisLocation(SGFigureElementAxisConstants.AXIS_TOP));
    assertEquals(
        SGFigureElementAxisConstants.AXIS_VERTICAL_1,
        SGUtility.getAxisLocation(SGFigureElementAxisConstants.AXIS_LEFT));
    assertEquals(
        SGFigureElementAxisConstants.AXIS_VERTICAL_2,
        SGUtility.getAxisLocation(SGFigureElementAxisConstants.AXIS_RIGHT));
    assertEquals(-1, SGUtility.getAxisLocation("None"));
  }

  @Test
  void getLocationInPlaneMapsNamesToIds() {
    assertEquals(
        SGFigureElementAxisConstants.AXIS_HORIZONTAL_1,
        SGUtility.getLocationInPlane(SGFigureElementAxisConstants.AXIS_BOTTOM));
    assertEquals(
        SGFigureElementAxisConstants.AXIS_HORIZONTAL_2,
        SGUtility.getLocationInPlane(SGFigureElementAxisConstants.AXIS_TOP));
    assertEquals(
        SGFigureElementAxisConstants.AXIS_VERTICAL_1,
        SGUtility.getLocationInPlane(SGFigureElementAxisConstants.AXIS_LEFT));
    assertEquals(
        SGFigureElementAxisConstants.AXIS_VERTICAL_2,
        SGUtility.getLocationInPlane(SGFigureElementAxisConstants.AXIS_RIGHT));
    assertEquals(
        SGFigureElementAxisConstants.AXIS_NORMAL,
        SGUtility.getLocationInPlane(SGFigureElementAxisConstants.AXIS_COLOR_BAR));
    assertEquals(-1, SGUtility.getLocationInPlane("None"));
  }

  @Test
  void getLocationNameMapsIdsToNames() {
    assertEquals(
        SGFigureElementAxisConstants.AXIS_BOTTOM,
        SGUtility.getLocationName(SGFigureElementAxisConstants.AXIS_HORIZONTAL_1));
    assertEquals(
        SGFigureElementAxisConstants.AXIS_TOP,
        SGUtility.getLocationName(SGFigureElementAxisConstants.AXIS_HORIZONTAL_2));
    assertEquals(
        SGFigureElementAxisConstants.AXIS_LEFT,
        SGUtility.getLocationName(SGFigureElementAxisConstants.AXIS_VERTICAL_1));
    assertEquals(
        SGFigureElementAxisConstants.AXIS_RIGHT,
        SGUtility.getLocationName(SGFigureElementAxisConstants.AXIS_VERTICAL_2));
    assertEquals(
        SGFigureElementAxisConstants.AXIS_COLOR_BAR,
        SGUtility.getLocationName(SGFigureElementAxisConstants.AXIS_NORMAL));
    assertNull(SGUtility.getLocationName(99));
  }

  // -- id assignment ------------------------------------------------------------------

  @Test
  void assignIdNumberReturnsOneForEmptyList() {
    assertEquals(1, SGUtility.assignIdNumber(List.of()));
  }

  @Test
  void assignIdNumberAppendsAfterHighestId() {
    assertEquals(4, SGUtility.assignIdNumber(List.of(1, 3, 2)));
  }

  @Test
  void assignIdNumberFillsGapWhenHighestIdIsExhausted() {
    // the sorted list ends with Integer.MAX_VALUE, so the first missing id is returned
    assertEquals(1, SGUtility.assignIdNumber(List.of(2, 3, Integer.MAX_VALUE)));
    assertEquals(3, SGUtility.assignIdNumber(List.of(1, 2, Integer.MAX_VALUE)));
  }

  // -- element helpers -------------------------------------------------------------------

  private static Element elementWithAttribute(final String attribute, final String value) {
    final Document doc = SGUtilityText.getDocumentFromString("<root/>");
    final Element el = doc.getDocumentElement();
    el.setAttribute(attribute, value);
    return el;
  }

  @Test
  void readIndicesParsesIndexProperty() {
    assertArrayEquals(
        new Integer[] {1, 3, 5},
        // the tokens must be plain numbers, so no spaces are allowed
        SGUtility.readIndices(elementWithAttribute("i", "{1,3,5}"), "i"));
    assertArrayEquals(new Integer[] {}, SGUtility.readIndices(elementWithAttribute("i", ""), "i"));
    assertArrayEquals(
        new Integer[] {7}, SGUtility.readIndices(elementWithAttribute("i", "7"), "i"));
    assertNull(SGUtility.readIndices(elementWithAttribute("i", "{a}"), "i"));
    assertNull(SGUtility.readIndices(elementWithAttribute("i", "{1, x}"), "i"));
    assertNull(SGUtility.readIndices(elementWithAttribute("i", "a{b"), "i"));
  }

  // -- list moving helpers ------------------------------------------------------

  @Test
  void moveObjectToRawListMovesObjectToTail() {
    final List<Object> list = new ArrayList<>(List.of("a", "b", "c"));
    assertTrue(SGUtility.moveObjectToRawList("b", list, true));
    assertEquals(List.of("a", "c", "b"), list);
  }

  @Test
  void moveObjectToRawListMovesObjectToHead() {
    final List<Object> list = new ArrayList<>(List.of("a", "b", "c"));
    assertTrue(SGUtility.moveObjectToRawList("b", list, false));
    assertEquals(List.of("b", "a", "c"), list);
  }

  @Test
  void moveObjectToRawListFailsWhenObjectMissing() {
    final List<Object> list = new ArrayList<>(List.of("a", "b"));
    assertFalse(SGUtility.moveObjectToRawList("z", list, true));
    assertEquals(List.of("a", "b"), list);
  }

  @Test
  void moveObjectRawListShiftsObjectsForward() {
    final List<Object> list = new ArrayList<>(List.of("a", "b", "c", "d"));
    final List<Object> moved = new ArrayList<>(List.of("b"));
    assertTrue(SGUtility.moveObjectRawList(moved, list, 1));
    assertEquals(List.of("a", "c", "b", "d"), list);
  }

  @Test
  void moveObjectRawListShiftsObjectsBackward() {
    final List<Object> list = new ArrayList<>(List.of("a", "b", "c", "d"));
    final List<Object> moved = new ArrayList<>(List.of("c"));
    assertTrue(SGUtility.moveObjectRawList(moved, list, -1));
    assertEquals(List.of("a", "c", "b", "d"), list);
  }

  @Test
  void moveObjectRawListFailsWhenObjectMissing() {
    final List<Object> list = new ArrayList<>(List.of("a", "b"));
    final List<Object> moved = new ArrayList<>(List.of("z"));
    assertFalse(SGUtility.moveObjectRawList(moved, list, 1));
    assertEquals(List.of("a", "b"), list);
  }

  // -- visibility helpers ---------------------------------------------------------

  /** A minimal {@link SGIVisible} used to exercise the visibility helper. */
  private static final class Visible implements SGIVisible {
    private boolean visible;

    Visible(final boolean visible) {
      this.visible = visible;
    }

    public boolean isVisible() {
      return visible;
    }

    public void setVisible(final boolean b) {
      this.visible = b;
    }
  }

  @Test
  void setVisibleListOrdersVisibleFirstAndUpdatesFlags() {
    final Visible v1 = new Visible(true);
    final Visible v2 = new Visible(true);
    final Visible v3 = new Visible(true);
    final List<Visible> list = new ArrayList<>(List.of(v1, v2, v3));
    final List<Visible> visible = new ArrayList<>(List.of(v2));
    assertTrue(SGUtility.setVisibleList(list, visible));
    // the list is rebuilt with the visible elements first, in their given order
    assertEquals(List.of(v2, v1, v3), list);
    assertFalse(v1.isVisible());
    assertTrue(v2.isVisible());
    assertFalse(v3.isVisible());
  }

  // -- message dialog visibility flag ---------------------------------------------

  @Test
  void messageDialogVisibilityFlagReflectsSetAndClear() {
    SGUtility.clearMessageDialogVisible();
    assertFalse(SGUtility.wasMessageDialogVisible());
    SGUtility.setMessageDialogWasVisible();
    assertTrue(SGUtility.wasMessageDialogVisible());
    SGUtility.clearMessageDialogVisible();
    assertFalse(SGUtility.wasMessageDialogVisible());
  }
}
