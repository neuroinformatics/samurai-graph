package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGIStringModifier;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;

class SGTextDataColumnTest {

  private static class UpperCaseModifier implements SGIStringModifier {
    @Override
    public String modify(String str) {
      return str.toUpperCase();
    }
  }

  @Test
  void constructorWithStringArray() {
    String[] values = {"a", "b", "c"};
    SGTextDataColumn col = new SGTextDataColumn("label", values);
    assertEquals(3, col.getLength());
    assertArrayEquals(values, col.getStringArray());
    assertNull(col.getModifier());
  }

  @Test
  void constructorWithStringArrayNullThrows() {
    assertThrows(
        IllegalArgumentException.class, () -> new SGTextDataColumn("label", (String[]) null));
  }

  @Test
  void constructorWithStringArrayNullElementBecomesEmptyString() {
    String[] values = {"a", null, "c"};
    SGTextDataColumn col = new SGTextDataColumn("label", values);
    assertArrayEquals(new String[] {"a", "", "c"}, col.getStringArray());
  }

  @Test
  void constructorWithModifier() {
    String[] values = {"hello", "world"};
    SGTextDataColumn col = new SGTextDataColumn("label", values, new UpperCaseModifier());
    assertArrayEquals(new String[] {"HELLO", "WORLD"}, col.getStringArray());
    assertNotNull(col.getModifier());
  }

  @Test
  void constructorWithModifierNullElementBecomesEmptyString() {
    String[] values = {"hello", null};
    SGTextDataColumn col = new SGTextDataColumn("label", values, new UpperCaseModifier());
    assertArrayEquals(new String[] {"HELLO", ""}, col.getStringArray());
  }

  @Test
  void getLength() {
    SGTextDataColumn col = new SGTextDataColumn("label", new String[] {"a", "b", "c"});
    assertEquals(3, col.getLength());
  }

  @Test
  void getValueType() {
    SGTextDataColumn col = new SGTextDataColumn("label", new String[] {"a"});
    assertEquals(SGIDataColumnTypeConstants.VALUE_TYPE_TEXT, col.getValueType());
  }

  @Test
  void getValue() {
    SGTextDataColumn col = new SGTextDataColumn("label", new String[] {"x", "y"});
    assertEquals("x", col.getValue(0));
    assertEquals("y", col.getValue(1));
  }

  @Test
  void getValueWithIndexOutOfBoundsThrows() {
    SGTextDataColumn col = new SGTextDataColumn("label", new String[] {"x"});
    assertThrows(IllegalArgumentException.class, () -> col.getValue(2));
  }

  @Test
  void getStringArrayReturnsDefensiveCopy() {
    SGTextDataColumn col = new SGTextDataColumn("label", new String[] {"a", "b"});
    String[] retrieved = col.getStringArray();
    retrieved[0] = "modified";
    assertArrayEquals(new String[] {"a", "b"}, col.getStringArray());
  }

  @Test
  void getStringArrayWithStride() {
    SGTextDataColumn col = new SGTextDataColumn("label", new String[] {"a", "b", "c", "d", "e"});
    SGIntegerSeriesSet stride = new SGIntegerSeriesSet(0, 4, 2);
    assertArrayEquals(new String[] {"a", "c", "e"}, col.getStringArray(stride));
  }

  @Test
  void dispose() {
    SGTextDataColumn col = new SGTextDataColumn("label", new String[] {"a"});
    col.dispose();
    assertTrue(col.isDisposed());
  }
}
