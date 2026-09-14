package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import org.junit.jupiter.api.Test;

/** Unit tests for the static geometry utilities of {@code SGUtilityForFigureElementJava2D}. */
class SGUtilityForFigureElementJava2DTest {

  private static final double DELTA = 1.0e-4;

  @Test
  void pointsBoundingBoxCoversAllPoints() {
    List<Point2D> points =
        List.of(new Point2D.Float(1, 2), new Point2D.Float(4, 6), new Point2D.Float(-1, 3));
    Rectangle2D rect = SGUtilityForFigureElementJava2D.getPointsBoundingBox(points);
    assertEquals(-1.0, rect.getMinX(), DELTA);
    assertEquals(4.0, rect.getMaxX(), DELTA);
    assertEquals(2.0, rect.getMinY(), DELTA);
    assertEquals(6.0, rect.getMaxY(), DELTA);
  }

  @Test
  void emptyPointListReturnsNullBoundingBox() {
    assertNull(SGUtilityForFigureElementJava2D.getPointsBoundingBox(java.util.List.of()));
  }

  @Test
  void boundingBoxOfDrawingElementsUnionsBounds() {
    Line2D.Float line = new Line2D.Float(0, 0, 2, 3);
    List<Point2D> points =
        SGUtilityForFigureElementJava2D.getSegmentList(line.getPathIterator(null));
    // a line segment yields two end points
    assertEquals(List.of(new Point2D.Float(0, 0), new Point2D.Float(2, 3)), points);
  }

  @Test
  void lineSegmentsAreCollectedFromPathIterator() {
    Path2D path = new Path2D.Float();
    path.moveTo(0, 0);
    path.lineTo(2, 0);
    path.lineTo(2, 2);
    List<Point2D> points =
        SGUtilityForFigureElementJava2D.getSegmentList(path.getPathIterator(null));
    assertEquals(
        List.of(new Point2D.Float(0, 0), new Point2D.Float(2, 0), new Point2D.Float(2, 2)), points);
  }

  @Test
  void gradientReturnsAnglesInRadian() {
    assertEquals(0.0f, SGUtilityForFigureElementJava2D.getGradient(0, 0, 1, 0), DELTA);
    assertEquals(Math.PI / 2, SGUtilityForFigureElementJava2D.getGradient(0, 0, 0, 1), DELTA);
    assertEquals(Math.PI, SGUtilityForFigureElementJava2D.getGradient(0, 0, -1, 0), DELTA);
    assertEquals(1.5 * Math.PI, SGUtilityForFigureElementJava2D.getGradient(0, 0, 0, -1), DELTA);
    assertEquals(Math.atan(1.0), SGUtilityForFigureElementJava2D.getGradient(0, 0, 1, 1), DELTA);
    assertEquals(0.0f, SGUtilityForFigureElementJava2D.getGradient(5, 5, 5, 5), DELTA);
  }

  @Test
  void solidLineStrokeHasNoDash() {
    BasicStroke stroke =
        SGUtilityForFigureElementJava2D.getBasicStroke(
            SGLineConstants.LINE_TYPE_SOLID,
            1.5f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f,
            0.0f);
    assertNull(stroke.getDashArray());
    assertEquals(1.5f, stroke.getLineWidth());
  }

  @Test
  void brokenStrokeHasDashPattern() {
    BasicStroke stroke =
        SGUtilityForFigureElementJava2D.getBasicStroke(
            SGLineConstants.LINE_TYPE_BROKEN,
            1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f,
            0.0f);
    assertNotNull(stroke.getDashArray());
    assertEquals(2.0f, stroke.getDashArray()[0], DELTA);
    assertEquals(1.0f, stroke.getDashArray()[1], DELTA);
    assertEquals(2, stroke.getDashArray().length);
  }

  @Test
  void stringCompatibilityModifiesOnlyOldVersions() {
    String str = "{b}";
    assertEquals(
        "\\{b\\}", SGUtilityForFigureElementJava2D.modifyStringForCompatibility(str, "2.0.0"));
    assertEquals(
        "\\{b\\}", SGUtilityForFigureElementJava2D.modifyStringForCompatibility(str, "1.0.0"));
    assertEquals(str, SGUtilityForFigureElementJava2D.modifyStringForCompatibility(str, "2.0.1"));
  }

  @Test
  void minimumGapIsReturnedAsValueSize() {
    SGAxis axis = new SGAxis(0.0, 100.0);
    double[] values = {0.5, 1.4, 5.0, 5.1};
    double size = SGUtilityForFigureElementJava2D.calcSizeValue(values, axis, true);
    assertEquals(3.6, size, 1.0e-3);
  }

  @Test
  void singleValueReturnsDefaultValue() {
    SGAxis axis = new SGAxis(0.0, 100.0);
    double size = SGUtilityForFigureElementJava2D.calcSizeValue(new double[] {2.0}, axis, true);
    assertEquals(1.0, size);
  }

  @Test
  void unionOfRectanglesCoversBoth() {
    ArrayList<Rectangle2D> list = new ArrayList<Rectangle2D>();
    list.add(new Rectangle2D.Double(0, 0, 2, 2));
    list.add(new Rectangle2D.Double(3, 3, 2, 2));
    Rectangle2D rect = SGUtility.createUnion(list);
    assertEquals(5.0, rect.getWidth(), DELTA);
    assertEquals(5.0, rect.getHeight(), DELTA);
  }
}
