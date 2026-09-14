package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

/** Unit tests for the rectangle utility methods of {@link SGUtility}. */
class SGUtilityRectangleTest {

  private static final double DELTA = 1.0e-9;

  @Test
  void getRectStartReturnsOriginForEachDirection() {
    Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 5.0, 7.0);
    assertEquals(10.0, SGUtility.getRectStart(rect, true), DELTA);
    assertEquals(20.0, SGUtility.getRectStart(rect, false), DELTA);
  }

  @Test
  void getRectSizeReturnsExtentForEachDirection() {
    Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 5.0, 7.0);
    assertEquals(5.0, SGUtility.getRectSize(rect, true), DELTA);
    assertEquals(7.0, SGUtility.getRectSize(rect, false), DELTA);
  }

  @Test
  void getRectEndReturnsStartPlusSizeForEachDirection() {
    Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 5.0, 7.0);
    assertEquals(15.0, SGUtility.getRectEnd(rect, true), DELTA);
    assertEquals(27.0, SGUtility.getRectEnd(rect, false), DELTA);
  }

  @Test
  void setRectStartMovesOnlyTheSelectedDirection() {
    Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 5.0, 7.0);
    assertTrue(SGUtility.setRectStart(rect, 12.0, true));
    assertEquals(12.0, rect.getX(), DELTA);
    assertEquals(20.0, rect.getY(), DELTA);
    assertEquals(5.0, rect.getWidth(), DELTA);
    assertEquals(7.0, rect.getHeight(), DELTA);
    assertTrue(SGUtility.setRectStart(rect, 23.0, false));
    assertEquals(12.0, rect.getX(), DELTA);
    assertEquals(23.0, rect.getY(), DELTA);
  }

  @Test
  void setRectEndMovesOnlyTheSelectedDirection() {
    Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 5.0, 7.0);
    assertTrue(SGUtility.setRectEnd(rect, 12.0, true));
    assertEquals(7.0, rect.getX(), DELTA);
    assertEquals(12.0, rect.getX() + rect.getWidth(), DELTA);
    assertTrue(SGUtility.setRectEnd(rect, 30.0, false));
    assertEquals(23.0, rect.getY(), DELTA);
    assertEquals(30.0, rect.getY() + rect.getHeight(), DELTA);
  }

  @Test
  void setRectSizeChangesOnlyTheSelectedDirection() {
    Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 5.0, 7.0);
    assertTrue(SGUtility.setRectSize(rect, 8.0, true));
    assertEquals(8.0, rect.getWidth(), DELTA);
    assertEquals(7.0, rect.getHeight(), DELTA);
    assertTrue(SGUtility.setRectSize(rect, 9.0, false));
    assertEquals(8.0, rect.getWidth(), DELTA);
    assertEquals(9.0, rect.getHeight(), DELTA);
  }

  @Test
  void isRectContainsChecksValueWithinRange() {
    Rectangle2D rect = new Rectangle2D.Double(10.0, 20.0, 5.0, 7.0);
    assertTrue(SGUtility.isRectContains(rect, 12.0, true));
    assertFalse(SGUtility.isRectContains(rect, 5.0, true));
    assertTrue(SGUtility.isRectContains(rect, 27.0, false));
    assertFalse(SGUtility.isRectContains(rect, 30.0, false));
  }

  @Test
  void isRectContainsChecksInnerRectangle() {
    Rectangle2D outer = new Rectangle2D.Double(10.0, 20.0, 10.0, 10.0);
    Rectangle2D inner = new Rectangle2D.Double(12.0, 22.0, 3.0, 3.0);
    assertTrue(SGUtility.isRectContains(outer, inner, true));
    assertTrue(SGUtility.isRectContains(outer, inner, false));
    Rectangle2D outerToo = new Rectangle2D.Double(0.0, 20.0, 10.0, 10.0);
    assertFalse(SGUtility.isRectContains(outerToo, inner, true));
  }

  @Test
  void getOverlappingReturnsOverlapSize() {
    Rectangle2D r1 = new Rectangle2D.Double(0.0, 0.0, 10.0, 10.0);
    Rectangle2D r2 = new Rectangle2D.Double(5.0, 5.0, 10.0, 10.0);
    assertEquals(5.0, SGUtility.getOverlapping(r1, r2, true), DELTA);
    assertEquals(5.0, SGUtility.getOverlapping(r1, r2, false), DELTA);
    Rectangle2D r3 = new Rectangle2D.Double(20.0, 20.0, 2.0, 2.0);
    assertEquals(0.0, SGUtility.getOverlapping(r1, r3, true), DELTA);
  }

  @Test
  void createUnionReturnsUnionOfRects() {
    ArrayList<Rectangle2D> list = new ArrayList<>();
    list.add(new Rectangle2D.Double(0.0, 0.0, 10.0, 10.0));
    list.add(new Rectangle2D.Double(20.0, 30.0, 5.0, 5.0));
    Rectangle2D union = SGUtility.createUnion(list);
    assertEquals(0.0, union.getX(), DELTA);
    assertEquals(0.0, union.getY(), DELTA);
    assertEquals(25.0, union.getWidth(), DELTA);
    assertEquals(35.0, union.getHeight(), DELTA);
  }

  @Test
  void createUnionRejectsNullList() {
    assertThrows(IllegalArgumentException.class, () -> SGUtility.createUnion(null));
  }

  @Test
  void createUnionRejectsNullElement() {
    ArrayList<Rectangle2D> list = new ArrayList<>();
    list.add(null);
    assertThrows(IllegalArgumentException.class, () -> SGUtility.createUnion(list));
  }
}
