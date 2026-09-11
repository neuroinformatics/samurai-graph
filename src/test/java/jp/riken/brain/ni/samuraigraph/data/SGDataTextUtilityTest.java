package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDataTextUtility}. */
class SGDataTextUtilityTest {

  @Test
  void createTitleStringPerDataType() {
    assertEquals(
        "Data (Scalar-XY Graph)",
        SGDataTextUtility.createTitleString("Data", SGDataTypeConstants.SXY_DATA));
    assertEquals(
        "Data (Pseudocolor Map)",
        SGDataTextUtility.createTitleString("Data", SGDataTypeConstants.SXYZ_DATA));
    assertEquals(
        "Data (Vector-XY Graph)",
        SGDataTextUtility.createTitleString("Data", SGDataTypeConstants.VXY_DATA));
    assertEquals("Data ()", SGDataTextUtility.createTitleString("Data", "UNKNOWN"));
  }

  @Test
  void getTextValueQuotesInput() {
    assertEquals("\"abc\"", SGDataTextUtility.getTextValue("abc"));
  }

  @Test
  void getDimensionStringJoinsNamesWithSpaces() {
    assertEquals("x y z", SGDataTextUtility.getDimensionString(new String[] {"x", "y", "z"}));
    assertEquals("", SGDataTextUtility.getDimensionString(new String[] {}));
  }

  @Test
  void getNetCDFValidNameSanitizes() {
    assertNull(SGDataTextUtility.getNetCDFValidName(null));
    assertNull(SGDataTextUtility.getNetCDFValidName(""));
    assertEquals("sg_123", SGDataTextUtility.getNetCDFValidName("123"));
    assertEquals("a_b", SGDataTextUtility.getNetCDFValidName("a-b"));
    assertEquals("sg__x_y", SGDataTextUtility.getNetCDFValidName("-x-y"));
    assertEquals("a_b1", SGDataTextUtility.getNetCDFValidName("a_b1"));
  }

  @Test
  void bindVariableNamesInBracketWithStringArray() {
    assertEquals(
        "{a,b,c}", SGDataTextUtility.bindVariableNamesInBracket(new String[] {"a", "b", "c"}));
  }

  @Test
  void bindVariableNamesInBracketWithStringList() {
    assertEquals("{x}", SGDataTextUtility.bindVariableNamesInBracket(Arrays.asList("x")));
  }

  @Test
  void encodeAndDecodeStringRoundTrip() {
    String str = "hello";
    assertEquals(str, SGDataTextUtility.decodeString(SGDataTextUtility.encodeString(str)));
  }

  @Test
  void appendGroupNameConcatenatesGroupAndName() {
    assertEquals("group/name", SGDataTextUtility.appendGroupName("name", "group"));
  }
}
