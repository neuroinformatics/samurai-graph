package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import org.junit.jupiter.api.Test;

/** Unit tests for the static helpers and properties of error bar elements. */
class SGDrawingElementErrorBarTest {

  @Test
  void errorBarStylesAreValidated() {
    assertTrue(
        SGDrawingElementErrorBar.isValidErrorBarStyle(SGErrorBarConstants.ERROR_BAR_BOTHSIDES));
    assertTrue(SGDrawingElementErrorBar.isValidErrorBarStyle(SGErrorBarConstants.ERROR_BAR_UPSIDE));
    assertTrue(
        SGDrawingElementErrorBar.isValidErrorBarStyle(SGErrorBarConstants.ERROR_BAR_DOWNSIDE));
    assertTrue(!SGDrawingElementErrorBar.isValidErrorBarStyle(0));
    assertTrue(!SGDrawingElementErrorBar.isValidErrorBarStyle(999));
  }

  @Test
  void errorBarStyleNameRoundTrip() {
    assertEquals(
        SGErrorBarConstants.ERROR_BAR_BOTHSIDES,
        SGDrawingElementErrorBar.getErrorBarStyleFromName(
                SGErrorBarConstants.ERROR_BAR_STYLE_BOTHSIDES)
            .intValue());
    assertEquals(
        SGErrorBarConstants.ERROR_BAR_UPSIDE,
        SGDrawingElementErrorBar.getErrorBarStyleFromName(
                SGErrorBarConstants.ERROR_BAR_STYLE_UPSIDE)
            .intValue());
    assertEquals(
        SGErrorBarConstants.ERROR_BAR_DOWNSIDE,
        SGDrawingElementErrorBar.getErrorBarStyleFromName(
                SGErrorBarConstants.ERROR_BAR_STYLE_DOWNSIDE)
            .intValue());
    assertEquals(
        SGErrorBarConstants.ERROR_BAR_STYLE_BOTHSIDES,
        SGDrawingElementErrorBar.getErrorBarStyleName(SGErrorBarConstants.ERROR_BAR_BOTHSIDES));
    assertEquals(
        SGErrorBarConstants.ERROR_BAR_STYLE_DOWNSIDE,
        SGDrawingElementErrorBar.getErrorBarStyleName(SGErrorBarConstants.ERROR_BAR_DOWNSIDE));
    assertNull(SGDrawingElementErrorBar.getErrorBarStyleFromName(null));
    assertNull(SGDrawingElementErrorBar.getErrorBarStyleFromName("Invalid"));
    assertNull(SGDrawingElementErrorBar.getErrorBarStyleName(999));
  }

  @Test
  void headTypesAreValidated() {
    assertTrue(SGDrawingElementErrorBar.isValidHeadType(SGSymbolConstants.SYMBOL_TYPE_CIRCLE));
    assertTrue(
        SGDrawingElementErrorBar.isValidHeadType(SGArrowConstants.SYMBOL_TYPE_TRANSVERSELINE));
    assertTrue(SGDrawingElementErrorBar.isValidHeadType(SGArrowConstants.SYMBOL_TYPE_VOID));
    assertTrue(!SGDrawingElementErrorBar.isValidHeadType(SGSymbolConstants.SYMBOL_TYPE_SQUARE));
    assertEquals(
        SGSymbolConstants.SYMBOL_TYPE_CIRCLE,
        SGDrawingElementErrorBar.getHeadTypeFromName(SGSymbolConstants.SYMBOL_NAME_CIRCLE)
            .intValue());
    assertEquals(
        SGSymbolConstants.SYMBOL_NAME_CIRCLE,
        SGDrawingElementErrorBar.getHeadTypeName(SGSymbolConstants.SYMBOL_TYPE_CIRCLE));
    assertNull(SGDrawingElementErrorBar.getHeadTypeFromName(null));
    assertNull(SGDrawingElementErrorBar.getHeadTypeName(SGSymbolConstants.SYMBOL_TYPE_SQUARE));
  }

  @Test
  void errorBarPropertiesHoldAttributes() {
    SGDrawingElementErrorBar.ErrorBarProperties p =
        new SGDrawingElementErrorBar.ErrorBarProperties();
    p.setLineWidth(1.5f);
    p.setHeadSize(0.3f);
    p.setHeadType(SGSymbolConstants.SYMBOL_TYPE_CIRCLE);
    p.setErrorBarStyle(SGErrorBarConstants.ERROR_BAR_UPSIDE);
    assertTrue(p.setColor(Color.RED));
    p.setVertical(false);

    assertEquals(Float.valueOf(1.5f), p.getLineWidth());
    assertEquals(Float.valueOf(0.3f), p.getHeadSize());
    assertEquals(SGSymbolConstants.SYMBOL_TYPE_CIRCLE, p.getHeadType().intValue());
    assertEquals(SGErrorBarConstants.ERROR_BAR_UPSIDE, p.getErrorBarStyle().intValue());
    assertEquals(Color.RED, p.getColor());
    assertEquals(Boolean.FALSE, p.isVertical());
  }

  @Test
  void errorBarPropertiesEquality() {
    SGDrawingElementErrorBar.ErrorBarProperties p =
        new SGDrawingElementErrorBar.ErrorBarProperties();
    SGDrawingElementErrorBar.ErrorBarProperties same =
        new SGDrawingElementErrorBar.ErrorBarProperties();
    assertTrue(p.equals(same));
    assertTrue(p.equals(p));
    SGDrawingElementErrorBar.ErrorBarProperties other =
        new SGDrawingElementErrorBar.ErrorBarProperties();
    other.setErrorBarStyle(SGErrorBarConstants.ERROR_BAR_DOWNSIDE);
    assertTrue(!p.equals(other));
    assertTrue(!p.equals("other"));
  }
}
