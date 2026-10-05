package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Panel;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
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

  // -- mouse location ---------------------------------------------------------

  /** Builds a rectangle of 100x100 at the origin for location tests. */
  private static Rectangle2D unitRect() {
    return new Rectangle2D.Double(0, 0, 100, 100);
  }

  @Test
  void getMouseLocationMapsCorners() {
    Rectangle2D rect = unitRect();
    assertEquals(SGConstants.NORTH_WEST, SGUtility.getMouseLocation(rect, 0, 0, 5));
    assertEquals(SGConstants.NORTH_EAST, SGUtility.getMouseLocation(rect, 100, 0, 5));
    assertEquals(SGConstants.SOUTH_WEST, SGUtility.getMouseLocation(rect, 0, 100, 5));
    assertEquals(SGConstants.SOUTH_EAST, SGUtility.getMouseLocation(rect, 100, 100, 5));
  }

  @Test
  void getMouseLocationMapsEdgeMidpoints() {
    Rectangle2D rect = unitRect();
    assertEquals(SGConstants.NORTH, SGUtility.getMouseLocation(rect, 50, 0, 5));
    assertEquals(SGConstants.SOUTH, SGUtility.getMouseLocation(rect, 50, 100, 5));
    assertEquals(SGConstants.WEST, SGUtility.getMouseLocation(rect, 0, 50, 5));
    assertEquals(SGConstants.EAST, SGUtility.getMouseLocation(rect, 100, 50, 5));
  }

  @Test
  void getMouseLocationReturnsOtherOutsideTolerance() {
    Rectangle2D rect = unitRect();
    assertEquals(SGConstants.OTHER, SGUtility.getMouseLocation(rect, 50, 50, 5));
    assertEquals(SGConstants.OTHER, SGUtility.getMouseLocation(rect, 30, 30, 5));
  }

  // -- resizeRectangle --------------------------------------------------------

  private static MouseEvent event(final int x, final int y) {
    return new MouseEvent(new Panel(), 0, 0L, 0, x, y, 1, false);
  }

  private static MouseEvent eventWithShift(final int x, final int y) {
    return new MouseEvent(new Panel(), 0, 0L, InputEvent.SHIFT_DOWN_MASK, x, y, 1, false);
  }

  @Test
  void resizeRectangleExpandsFromEastEdge() {
    Rectangle2D rect = new Rectangle2D.Double(10, 10, 50, 50);
    Point pos = new Point(60, 10); // the east edge
    SGUtility.resizeRectangle(rect, pos, event(70, 10), SGConstants.EAST);
    assertEquals(10.0, rect.getX(), DELTA);
    assertEquals(10.0, rect.getY(), DELTA);
    assertEquals(60.0, rect.getWidth(), DELTA);
    assertEquals(50.0, rect.getHeight(), DELTA);
    assertEquals(70, pos.x);
    assertEquals(10, pos.y);
  }

  @Test
  void resizeRectangleMovesNorthEdgeDownward() {
    Rectangle2D rect = new Rectangle2D.Double(10, 10, 50, 50);
    Point pos = new Point(35, 10); // the north edge
    SGUtility.resizeRectangle(rect, pos, event(35, 20), SGConstants.NORTH);
    assertEquals(10.0, rect.getX(), DELTA);
    assertEquals(20.0, rect.getY(), DELTA);
    assertEquals(50.0, rect.getWidth(), DELTA);
    assertEquals(40.0, rect.getHeight(), DELTA);
    assertEquals(35, pos.x);
    assertEquals(20, pos.y);
  }

  @Test
  void resizeRectangleExpandsFromSouthEdge() {
    Rectangle2D rect = new Rectangle2D.Double(10, 10, 50, 50);
    Point pos = new Point(35, 60); // the south edge
    SGUtility.resizeRectangle(rect, pos, event(35, 70), SGConstants.SOUTH);
    assertEquals(10.0, rect.getX(), DELTA);
    assertEquals(10.0, rect.getY(), DELTA);
    assertEquals(50.0, rect.getWidth(), DELTA);
    assertEquals(60.0, rect.getHeight(), DELTA);
  }

  @Test
  void resizeRectangleMovesWestEdgeLeftward() {
    Rectangle2D rect = new Rectangle2D.Double(10, 10, 50, 50);
    Point pos = new Point(10, 35); // the west edge
    SGUtility.resizeRectangle(rect, pos, event(-5, 35), SGConstants.WEST);
    assertEquals(-5.0, rect.getX(), DELTA);
    assertEquals(10.0, rect.getY(), DELTA);
    assertEquals(65.0, rect.getWidth(), DELTA);
    assertEquals(50.0, rect.getHeight(), DELTA);
  }

  @Test
  void resizeRectangleExpandsFromSouthEastCorner() {
    Rectangle2D rect = new Rectangle2D.Double(10, 10, 50, 50);
    Point pos = new Point(60, 60); // the south-east corner
    SGUtility.resizeRectangle(rect, pos, event(80, 70), SGConstants.SOUTH_EAST);
    assertEquals(10.0, rect.getX(), DELTA);
    assertEquals(10.0, rect.getY(), DELTA);
    assertEquals(70.0, rect.getWidth(), DELTA);
    assertEquals(60.0, rect.getHeight(), DELTA);
  }

  @Test
  void resizeRectangleMovesNorthWestCorner() {
    Rectangle2D rect = new Rectangle2D.Double(10, 10, 50, 50);
    Point pos = new Point(10, 10); // the north-west corner
    SGUtility.resizeRectangle(rect, pos, event(0, 0), SGConstants.NORTH_WEST);
    assertEquals(0.0, rect.getX(), DELTA);
    assertEquals(0.0, rect.getY(), DELTA);
    assertEquals(60.0, rect.getWidth(), DELTA);
    assertEquals(60.0, rect.getHeight(), DELTA);
  }

  @Test
  void resizeRectangleKeepsAspectWhenShiftIsHeld() {
    Rectangle2D rect = new Rectangle2D.Double(10, 10, 50, 50);
    Point pos = new Point(60, 60); // the south-east corner
    // dragging 20 px to the right keeps the 1:1 aspect ratio, so height grows too
    SGUtility.resizeRectangle(rect, pos, eventWithShift(80, 60), SGConstants.SOUTH_EAST);
    assertEquals(70.0, rect.getWidth(), DELTA);
    assertEquals(70.0, rect.getHeight(), DELTA);
  }
}
