package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import org.junit.jupiter.api.Test;

/** Unit tests of the number format state shared by the scalar XY data classes. */
class SGXYNumberFormatTest {

  @Test
  void decimalPlacesDefaultAndRoundTrip() {
    final SGXYNumberFormat format = new SGXYNumberFormat();
    assertEquals(0, format.getDecimalPlaces());
    format.setDecimalPlaces(3);
    assertEquals(3, format.getDecimalPlaces());
  }

  @Test
  void decimalPlacesRejectsNegative() {
    final SGXYNumberFormat format = new SGXYNumberFormat();
    assertThrows(IllegalArgumentException.class, () -> format.setDecimalPlaces(-1));
  }

  @Test
  void exponentRoundTrip() {
    final SGXYNumberFormat format = new SGXYNumberFormat();
    assertEquals(0, format.getExponent());
    format.setExponent(-2);
    assertEquals(-2, format.getExponent());
  }

  @Test
  void shiftDefaultIsZero() {
    final SGXYNumberFormat format = new SGXYNumberFormat();
    final SGTuple2d shift = format.getShift();
    assertEquals(0.0, shift.x);
    assertEquals(0.0, shift.y);
  }

  @Test
  void shiftGetterReturnsACopy() {
    final SGXYNumberFormat format = new SGXYNumberFormat();
    format.setShift(new SGTuple2d(1.0, 2.0));
    final SGTuple2d first = format.getShift();
    first.setX(9.0);
    final SGTuple2d second = format.getShift();
    assertNotSame(first, second);
    assertEquals(1.0, second.x);
    assertEquals(2.0, second.y);
  }

  @Test
  void setShiftRejectsNull() {
    final SGXYNumberFormat format = new SGXYNumberFormat();
    assertThrows(IllegalArgumentException.class, () -> format.setShift(null));
  }

  @Test
  void setShiftCopiesTheInput() {
    final SGXYNumberFormat format = new SGXYNumberFormat();
    final SGTuple2d input = new SGTuple2d(1.0, 2.0);
    format.setShift(input);
    input.setX(9.0);
    final SGTuple2d shift = format.getShift();
    assertEquals(1.0, shift.x);
    assertEquals(2.0, shift.y);
  }

  @Test
  void dateFormatDefaultEmptyAndRoundTrip() {
    final SGXYNumberFormat format = new SGXYNumberFormat();
    assertEquals("", format.getDateFormat());
    format.setDateFormat("yyyy/MM/dd");
    assertEquals("yyyy/MM/dd", format.getDateFormat());
  }
}
