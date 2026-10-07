package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.LookAndFeel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import org.junit.jupiter.api.Assumptions;
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

  // -- font and path accessors --------------------------------------------------

  @Test
  void findFontFamilyNameMatchesAvailableFontIgnoreCase() {
    // "Dialog" is a logical font that is always available
    assertEquals("Dialog", SGUtility.findFontFamilyName("Dialog"));
    assertEquals("Dialog", SGUtility.findFontFamilyName("dialog"));
    assertNull(SGUtility.findFontFamilyName("No Such Font Family"));
    assertNull(SGUtility.findFontFamilyName(null));
  }

  @Test
  void getCanonicalPathResolvesRelativeAndAbsolutePaths() {
    assertEquals(
        new File("PROBLEMS.md").getAbsolutePath(), SGUtility.getCanonicalPath("PROBLEMS.md"));
    assertEquals(new File("/").getAbsolutePath(), SGUtility.getCanonicalPath("/"));
  }

  // -- list movement wrappers ---------------------------------------------------

  @Test
  void moveObjectToTailMovesElementToLastPosition() {
    List<String> list = new ArrayList<>(List.of("a", "b", "c"));
    assertTrue(SGUtility.moveObjectToTail("a", list));
    assertEquals(List.of("b", "c", "a"), list);
    // an element already at the tail stays put
    assertTrue(SGUtility.moveObjectToTail("a", list));
    assertEquals(List.of("b", "c", "a"), list);
  }

  @Test
  void moveObjectToHeadMovesElementToFirstPosition() {
    List<String> list = new ArrayList<>(List.of("a", "b", "c"));
    assertTrue(SGUtility.moveObjectToHead("c", list));
    assertEquals(List.of("c", "a", "b"), list);
  }

  @Test
  void moveObjectToNextAndPreviousSwapWithAdjacentElement() {
    List<String> list = new ArrayList<>(List.of("a", "b", "c", "d"));
    assertTrue(SGUtility.moveObjectToNext("a", list));
    assertEquals(List.of("b", "a", "c", "d"), list);
    assertTrue(SGUtility.moveObjectToPrevious("a", list));
    assertEquals(List.of("a", "b", "c", "d"), list);
    // moving the head element previous stays at the head
    assertTrue(SGUtility.moveObjectToPrevious("a", list));
    assertEquals(List.of("a", "b", "c", "d"), list);
    // moving the tail element next stays at the tail
    assertTrue(SGUtility.moveObjectToNext("d", list));
    assertEquals(List.of("a", "b", "c", "d"), list);
  }

  @Test
  void moveObjectToPlacesElementAtGivenIndex() {
    List<String> list = new ArrayList<>(List.of("a", "b", "c", "d"));
    assertTrue(SGUtility.moveObjectTo("d", list, 1));
    assertEquals(List.of("a", "d", "b", "c"), list);
    // an absent element is reported as not moved
    assertFalse(SGUtility.moveObjectTo("q", list, 1));
    assertEquals(List.of("a", "d", "b", "c"), list);
    assertThrows(IllegalArgumentException.class, () -> SGUtility.moveObjectTo("a", null, 0));
    assertThrows(IllegalArgumentException.class, () -> SGUtility.moveObjectTo("a", list, 99));
  }

  @Test
  void moveObjectShiftsSeveralElementsByGivenLevel() {
    List<String> list = new ArrayList<>(List.of("x", "y", "z", "w"));
    assertTrue(SGUtility.moveObject(new ArrayList<>(List.of("x", "y")), list, 1));
    assertEquals(List.of("z", "x", "y", "w"), list);
    List<String> backward = new ArrayList<>(List.of("x", "y", "z", "w"));
    assertTrue(SGUtility.moveObject(new ArrayList<>(List.of("z", "w")), backward, -2));
    assertEquals(List.of("z", "w", "x", "y"), backward);
    // an element that is not in the list fails the move
    assertFalse(SGUtility.moveObject(new ArrayList<>(List.of("q")), backward, 1));
  }

  // -- copyable list --------------------------------------------------------------

  @Test
  void copyObjectsCopiesElementsIntoTargetList() {
    SGICopyable copyable = () -> "copied";
    List<SGICopyable> source = new ArrayList<>(List.of(copyable));
    source.add(null);
    List<Object> target = new ArrayList<>();
    assertTrue(SGUtility.copyObjects(source, target));
    assertEquals(List.of("copied"), target);
    assertThrows(IllegalArgumentException.class, () -> SGUtility.copyObjects(null, target));
    assertThrows(IllegalArgumentException.class, () -> SGUtility.copyObjects(source, null));
  }

  // -- object counting -------------------------------------------------------------

  @Test
  void getCntMapCountsOccurrencesOfEachObject() {
    Map<Object, Integer> cntMap = SGUtility.getCntMap(List.of("a", "b", "a", "c", "b", "a"));
    assertEquals(3, cntMap.get("a"));
    assertEquals(2, cntMap.get("b"));
    assertEquals(1, cntMap.get("c"));
    assertEquals(
        SGUtility.getCntMap(new Object[] {"a", "b", "a"}),
        SGUtility.getCntMap(List.of("a", "b", "a")));
    assertEquals(0, SGUtility.getCntMap(new Object[0]).size());
  }

  @Test
  void findMostFrequentObjectsReturnsMaximumCountObjects() {
    assertEquals(List.of("a"), SGUtility.findMostFrequentObjects(List.of("a", "b", "a")));
    // a tie returns all objects that share the maximum count
    List<Object> tied = SGUtility.findMostFrequentObjects(List.of("a", "b", "a", "b"));
    assertEquals(2, tied.size());
    assertTrue(tied.contains("a"));
    assertTrue(tied.contains("b"));
    List<Object> fromArray = SGUtility.findMostFrequentObjects(new Object[] {"a", "b"});
    assertEquals(2, fromArray.size());
    assertTrue(fromArray.containsAll(List.of("a", "b")));
    assertEquals(0, SGUtility.findMostFrequentObjects(List.of()).size());
  }

  // -- form widget value setters --------------------------------------------------

  private static SGSpinner createSpinner() {
    return new SGSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.5), null, 0, 2);
  }

  @Test
  void setDoubleValueOnTextFieldAcceptsValidNumbersOnly() {
    SGTextField tf = new SGTextField();
    assertTrue(SGUtility.setDoubleValue(tf, " 3.5 "));
    assertEquals("3.5", tf.getText());
    assertFalse(SGUtility.setDoubleValue(tf, "abc"));
    assertEquals("3.5", tf.getText());
    assertTrue(SGUtility.setDoubleValue(tf, null));
    assertNull(tf.getText());
  }

  @Test
  void setIntValueOnTextFieldAcceptsIntegersOnly() {
    SGTextField tf = new SGTextField();
    assertTrue(SGUtility.setIntValue(tf, "7"));
    assertEquals("7", tf.getText());
    assertFalse(SGUtility.setIntValue(tf, "x"));
    assertEquals("7", tf.getText());
    assertTrue(SGUtility.setIntValue(tf, null));
    assertNull(tf.getText());
  }

  @Test
  void setValueOnSpinnerCommitsValidNumbersOnly() {
    SGSpinner sp = createSpinner();
    assertTrue(SGUtility.setValue(sp, "3.5"));
    assertEquals(3.5, ((Number) sp.getValue()).doubleValue(), 0.0);
    assertEquals("3.5", sp.getText());
    assertFalse(SGUtility.setValue(sp, "abc"));
    assertEquals(3.5, ((Number) sp.getValue()).doubleValue(), 0.0);
    assertFalse(SGUtility.setValue(sp, null));
    assertEquals("", sp.getText());
  }

  @Test
  void setIntValueOnSpinnerCommitsIntegersOnly() {
    SGSpinner sp = createSpinner();
    assertTrue(SGUtility.setIntValue(sp, "7"));
    assertEquals(7.0, ((Number) sp.getValue()).doubleValue(), 0.0);
    assertFalse(SGUtility.setIntValue(sp, "x"));
    assertEquals(7.0, ((Number) sp.getValue()).doubleValue(), 0.0);
    assertFalse(SGUtility.setIntValue(sp, null));
    assertEquals("", sp.getText());
  }

  // -- property and export helpers -------------------------------------------------

  @Test
  void isValidPropertyValueRejectsNaNAndInfinite() {
    assertTrue(SGUtility.isValidPropertyValue(1.5));
    assertFalse(SGUtility.isValidPropertyValue(Double.NaN));
    assertFalse(SGUtility.isValidPropertyValue(Double.POSITIVE_INFINITY));
    assertFalse(SGUtility.isValidPropertyValue(Double.NEGATIVE_INFINITY));
  }

  @Test
  void getExportFontSizeRoundsToTwoFractionDigits() {
    assertEquals(10.5f, SGUtility.getExportFontSize(10.5f), 0.0f);
    assertEquals(12.35f, SGUtility.getExportFontSize(12.345f), 0.0f);
    assertEquals(-12.34f, SGUtility.getExportFontSize(-12.34f), 0.0f);
    assertEquals(0.0f, SGUtility.getExportFontSize(0.001f), 0.0f);
  }

  @Test
  void createPluginItemStringReturnsPluginName() {
    SGIPlugin plugin =
        new SGIPlugin() {
          @Override
          public String getName() {
            return "Plugin A";
          }

          @Override
          public String getDeveloper() {
            return "";
          }

          @Override
          public String getVersion() {
            return "";
          }

          @Override
          public String getDesc() {
            return "";
          }

          @Override
          public String getPath() {
            return "";
          }

          @Override
          public String getCommand() {
            return "";
          }
        };
    assertEquals("Plugin A", SGUtility.createPluginItemString(plugin));
  }

  @Test
  void getVersionNumberWalksUpToElementWithVersionAttribute() {
    Document doc = SGUtilityText.getDocumentFromString("<PropertySet><Figure/></PropertySet>");
    Element figure = (Element) doc.getDocumentElement().getFirstChild();
    assertEquals("", SGUtility.getVersionNumber(figure));
    doc.getDocumentElement().setAttribute("Version", "1.2.3");
    assertEquals("1.2.3", SGUtility.getVersionNumber(figure));
    assertEquals("1.2.3", SGUtility.getVersionNumber(doc.getDocumentElement()));
  }

  // -- mouse drag result -------------------------------------------------------------

  private static MouseEvent createDragEvent(final int modifiersEx, final int x, final int y) {
    return new MouseEvent(
        new java.awt.Canvas(), MouseEvent.MOUSE_DRAGGED, 0L, modifiersEx, x, y, 0, false, 0);
  }

  @Test
  void getMouseDragResultReportsPlainMovement() {
    SGUtility.MouseDragResult result =
        SGUtility.getMouseDragResult(
            new SGUtility.MouseDragInput(createDragEvent(0, 10, 7), new Point(4, 2), null, 0));
    assertEquals(6, result.dx);
    assertEquals(5, result.dy);
    assertEquals(10, result.ex);
    assertEquals(7, result.ey);
    assertNull(result.draggedDirection);
  }

  @Test
  void getMouseDragResultShiftDragLocksDominantAxis() {
    // horizontal drag dominates: the y coordinate is fixed
    SGUtility.MouseDragResult horizontal =
        SGUtility.getMouseDragResult(
            new SGUtility.MouseDragInput(
                createDragEvent(InputEvent.SHIFT_DOWN_MASK, 10, 7), new Point(4, 2), null, 0));
    assertEquals(6, horizontal.dx);
    assertEquals(0, horizontal.dy);
    assertEquals(10, horizontal.ex);
    assertEquals(7, horizontal.ey);
    assertEquals(new SGTuple2f(6.0f, 5.0f), horizontal.draggedDirection);
    // vertical drag dominates: the x coordinate is fixed
    SGUtility.MouseDragResult vertical =
        SGUtility.getMouseDragResult(
            new SGUtility.MouseDragInput(
                createDragEvent(InputEvent.SHIFT_DOWN_MASK, 1, 12), new Point(4, 2), null, 0));
    assertEquals(0, vertical.dx);
    assertEquals(10, vertical.dy);
    assertEquals(1, vertical.ex);
    assertEquals(12, vertical.ey);
    assertEquals(1, vertical.fixedCoordinate);
  }

  @Test
  void getMouseDragResultShiftDragKeepsPresetDirection() {
    SGUtility.MouseDragResult result =
        SGUtility.getMouseDragResult(
            new SGUtility.MouseDragInput(
                createDragEvent(InputEvent.SHIFT_DOWN_MASK, 3, 1),
                new Point(0, 0),
                new SGTuple2f(5.0f, 1.0f),
                99));
    // the preset direction keeps the horizontal axis dominant
    assertEquals(3, result.dx);
    assertEquals(0, result.dy);
    assertEquals(3, result.ex);
    assertEquals(99, result.ey);
    assertEquals(99, result.fixedCoordinate);
  }

  // -- characterization of remaining defensive branches ------------------------

  @Test
  void utilityClassIsInstantiable() {
    assertNotNull(new SGUtility());
  }

  @Test
  void identifyOSComparesPrefixOfOsName() {
    final String osName = SGConstants.OS_NAME.toLowerCase();
    assertTrue(SGUtility.identifyOS(osName.substring(0, 1)));
    assertFalse(SGUtility.identifyOS("~"));
    assertThrows(IllegalArgumentException.class, () -> SGUtility.identifyOS(null));
  }

  @Test
  void getLookAndFeelIDReturnsIdOfInstalledLookAndFeel() {
    final LookAndFeel laf = UIManager.getLookAndFeel();
    if (laf == null) {
      assertNull(SGUtility.getLookAndFeelID());
    } else {
      assertEquals(laf.getID(), SGUtility.getLookAndFeelID());
    }
  }

  @Test
  void getLookAndFeelIDReturnsNullWhenNoLookAndFeelIsInstalled() throws Exception {
    final LookAndFeel original = UIManager.getLookAndFeel();
    try {
      UIManager.setLookAndFeel((LookAndFeel) null);
      assertNull(SGUtility.getLookAndFeelID());
    } finally {
      if (original != null) {
        UIManager.setLookAndFeel(original);
      }
    }
  }

  @Test
  void getCanonicalPathReturnsNullWhenPathCannotBeResolved() {
    // a NUL byte in the path makes getCanonicalPath throw an IOException
    assertNull(SGUtility.getCanonicalPath("a\0b"));
  }

  @Test
  void copyArrayHelpersAcceptNullSource() {
    assertNull(SGUtility.copyStringArray((String[][]) null));
    assertNull(SGUtility.copyIntegerArray((int[]) null));
    assertNull(SGUtility.copyIntegerArray((Integer[]) null));
    assertNull(SGUtility.copyDoubleArray((double[]) null));
    assertNull(SGUtility.copyDoubleArray((double[][]) null));
  }

  @Test
  void copyStringArrayKeepsNullRows() {
    final String[][] src = {{"a", "b"}, null, {"c"}};
    final String[][] dest = SGUtility.copyStringArray(src);
    assertArrayEquals(src, dest);
    assertNull(dest[1]);
    assertNotSame(src[0], dest[0]);
    assertNotSame(src[2], dest[2]);
    // the copy is deep: mutating the copy does not affect the source
    dest[0][0] = "x";
    assertEquals("a", src[0][0]);
  }

  @Test
  void getFileNameFallsBackToForwardSlashOnWindowsPaths() {
    if (File.separatorChar == '/') {
      Assumptions.abort("the forward-slash fallback only exists for non-unix separators");
    }
    assertEquals("data.txt", SGUtility.getFileName("dir\\data.txt"));
    assertEquals("data.txt", SGUtility.getFileName("C:\\dir\\data.txt"));
  }

  @Test
  void equalsRejectsNullPairAgainstNonNullArray() {
    assertFalse(SGUtility.equals((Object[]) null, new Object[] {"a"}));
    assertFalse(SGUtility.equals(new int[] {1, 2}, new int[] {1}));
  }

  @Test
  void setValueOnSpinnerLogsAndSurvivesCommitFailure() throws ParseException {
    final SGSpinner sp = mock(SGSpinner.class);
    doThrow(new ParseException("forced", 0)).when(sp).commitEditByDefault();
    assertTrue(SGUtility.setValue(sp, "1.5"));
    verify(sp).setText("1.5");
    verify(sp).setValue(1.5);
    verify(sp).commitEditByDefault();
  }

  @Test
  void setIntValueOnSpinnerLogsAndSurvivesCommitFailure() throws ParseException {
    final SGSpinner sp = mock(SGSpinner.class);
    doThrow(new ParseException("forced", 0)).when(sp).commitEditByDefault();
    assertTrue(SGUtility.setIntValue(sp, "7"));
    verify(sp).setText("7");
    verify(sp).setValue(7.0);
    verify(sp).commitEditByDefault();
  }

  @Test
  void moveObjectToRejectsOutOfRangeIndex() {
    final List<String> list = new ArrayList<>(List.of("a", "b"));
    assertThrows(IllegalArgumentException.class, () -> SGUtility.moveObjectTo("a", list, -1));
    assertThrows(IllegalArgumentException.class, () -> SGUtility.moveObjectTo("a", list, 2));
  }

  @Test
  void moveObjectKeepsElementWhenTargetIndexIsOutOfRange() {
    final List<String> list = new ArrayList<>(List.of("a", "b", "c"));
    // the moved element is already at the tail, so shifting forward is a no-op
    assertTrue(SGUtility.moveObject(List.of("c"), list, 1));
    assertEquals(List.of("a", "b", "c"), list);
    // the moved element is already at the head, so shifting backward is a no-op
    assertTrue(SGUtility.moveObject(List.of("a"), list, -1));
    assertEquals(List.of("a", "b", "c"), list);
    // a missing element fails the move in both directions
    assertFalse(SGUtility.moveObject(List.of("q"), list, -1));
    assertEquals(List.of("a", "b", "c"), list);
  }

  @Test
  void moveObjectRawListKeepsElementWhenTargetIndexIsOutOfRange() {
    final List<Object> list = new ArrayList<>(List.of("a", "b", "c"));
    assertTrue(SGUtility.moveObjectRawList(List.of("c"), list, 1));
    assertEquals(List.of("a", "b", "c"), list);
    assertTrue(SGUtility.moveObjectRawList(List.of("a"), list, -1));
    assertEquals(List.of("a", "b", "c"), list);
    // a missing element fails the backward move
    assertFalse(SGUtility.moveObjectRawList(List.of("q"), list, -1));
    assertEquals(List.of("a", "b", "c"), list);
  }

  @Test
  void setVisibleListSkipsNullElementWhenSettingVisibility() {
    final Visible v1 = new Visible(true);
    final List<Visible> list = new ArrayList<>(List.of(v1));
    list.add(null);
    final List<Visible> visible = new ArrayList<>(List.of(v1));
    assertTrue(SGUtility.setVisibleList(list, visible));
    final List<Visible> expected = new ArrayList<>(List.of(v1));
    expected.add(null);
    assertEquals(expected, list);
    assertTrue(v1.isVisible());
  }

  @Test
  void containsRejectsNullArrayAsFirstArgument() {
    assertThrows(
        IllegalArgumentException.class, () -> SGUtility.contains((char[]) null, new char[] {'a'}));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.contains((Object[]) null, new Object[] {"a"}));
    assertThrows(
        IllegalArgumentException.class, () -> SGUtility.contains((List<?>) null, List.of("a")));
    assertThrows(
        IllegalArgumentException.class, () -> SGUtility.containsAll((List<?>) null, List.of("a")));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.containsAll((Object[]) null, new Object[] {"a"}));
  }

  @Test
  void containsNullIgnoredRejectsNullArrayPair() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.containsNullIgnored(new Object[] {"a"}, (Object[]) null));
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.containsNullIgnored((Object[]) null, new Object[] {"a"}));
  }

  @Test
  void compareVersionNumberRejectsNewerMinorWhenMajorIsEqual() {
    assertFalse(SGUtility.compareVersionNumber(1, 5, 0, 1, 3, 9));
  }

  @Test
  void isVersionNumberEqualOrSmallerThanRejectsNullTargetVersion() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.isVersionNumberEqualOrSmallerThan("1.0.1", null));
  }

  @Test
  void isVersionNumberEqualOrSmallerThanRejectsMalformedTargetVersion() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.isVersionNumberEqualOrSmallerThan("1.0.1", "1.x.3"));
  }

  @Test
  void isVersionNumberPermittingEmptyStringRejectsNullVersion() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.isVersionNumberEqualOrSmallerThanPermittingEmptyString("1.0.1", null));
  }

  @Test
  void assignIdNumberFindsGapBeforeExhaustedMaximum() {
    // ids 1..3 are all taken and the list ends with Integer.MAX_VALUE,
    // so the first missing id after the dense prefix is returned
    assertEquals(4, SGUtility.assignIdNumber(List.of(3, 1, Integer.MAX_VALUE, 2)));
  }

  @Test
  void checkEqualityReturnsSingleElementForFloatAndString() {
    assertEquals(2.5f, SGUtility.checkEquality(new float[] {2.5f}));
    assertEquals("solo", SGUtility.checkEquality(new String[] {"solo"}));
  }

  @Test
  void checkEqualityForColorRowsReturnsFirstRowWhenAllMatch() {
    final Color red = new Color(255, 0, 0);
    final Color blue = new Color(0, 0, 255);
    final Color[][] rows = {{red, blue}, {new Color(255, 0, 0), new Color(0, 0, 255)}};
    assertSame(rows[0], SGUtility.checkEquality(rows));
  }

  @Test
  void getVersionNumberReadsAttributeFromRootElement() {
    final Document doc = SGUtilityText.getDocumentFromString("<PropertySet/>");
    doc.getDocumentElement().setAttribute("Version", "9.9.9");
    assertEquals("9.9.9", SGUtility.getVersionNumber(doc.getDocumentElement()));
  }

  @Test
  void getVersionNumberReadsAttributeFromDetachedElement() {
    final Document doc = SGUtilityText.getDocumentFromString("<PropertySet/>");
    final Element orphan = doc.createElement("Orphan");
    orphan.setAttribute("Version", "5.5.5");
    assertEquals("5.5.5", SGUtility.getVersionNumber(orphan));
  }

  @Test
  void findNearestValuePrefersNextValueWhenEquidistant() {
    final int[] array = {1, 2, 5};
    // 4 is one step away from both 2 and 5; the next value wins
    assertEquals(5, SGUtility.findNearestValue(4, array));
  }

  @Test
  void findNearestValuePrefersPreviousValueWhenCloser() {
    // 2 is closer to 1 than to 4, so the previous value wins
    assertEquals(1, SGUtility.findNearestValue(2, new int[] {1, 4, 9}));
  }

  @Test
  void createImageLoadsClasspathResourceWithMediaTracking() {
    // the resource must be looked up with a class from the application class loader
    final Image image = SGUtility.createImage(SGUtility.class, new Panel(), "About.png");
    assertNotNull(image);
  }

  @Test
  void createImageWithoutComponentSkipsMediaTracking() {
    // a null component skips the media tracker setup
    final Image image = SGUtility.createImage(SGUtility.class, null, "About.png");
    assertNotNull(image);
  }

  @Test
  void isAlphabeticRejectsNonAsciiLetters() {
    assertFalse(SGUtility.isAlphabetic('\u00e4'));
  }

  @Test
  void transposeRejectsEmptyFirstRow() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SGUtility.transpose(new double[][] {new double[] {}}));
  }
}
